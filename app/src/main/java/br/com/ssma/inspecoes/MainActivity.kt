package br.com.ssma.inspecoes

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import java.io.File
import java.io.FileOutputStream

data class Finding(
    val desc: String = "",
    val severity: String = "LEVE",
    val action: String = "",
    val deadline: String = "",
    val owner: String = "",
    val photo: Uri? = null
)

class DB(context: Activity) : SQLiteOpenHelper(context, "ssma.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE inspections(" +
                "id INTEGER PRIMARY KEY, responsible TEXT, number TEXT, date TEXT, " +
                "location TEXT, findings TEXT, pdf TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit

    fun save(
        responsible: String,
        number: String,
        date: String,
        location: String,
        findings: String,
        pdf: String
    ) {
        val values = ContentValues().apply {
            put("responsible", responsible)
            put("number", number)
            put("date", date)
            put("location", location)
            put("findings", findings)
            put("pdf", pdf)
        }
        writableDatabase.insert("inspections", null, values)
    }
}

class MainActivity : ComponentActivity() {
    private lateinit var db: DB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = DB(this)

        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF252D87),
                    secondary = Color(0xFF20A9D2),
                    background = Color(0xFFF4F8FC),
                    surface = Color.White,
                    surfaceVariant = Color(0xFFEAF6FC)
                )
            ) {
                var responsible by remember { mutableStateOf("") }
                var number by remember { mutableStateOf("") }
                var date by remember { mutableStateOf("") }
                var location by remember { mutableStateOf("") }
                var findings by remember { mutableStateOf(listOf(Finding())) }
                var photoTarget by remember { mutableIntStateOf(0) }

                val galleryLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.GetContent()
                ) { uri ->
                    if (uri != null && photoTarget in findings.indices) {
                        findings = findings.toMutableList().also { list ->
                            list[photoTarget] = list[photoTarget].copy(photo = uri)
                        }
                    }
                }

                val cameraLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.TakePicturePreview()
                ) { bitmap ->
                    if (bitmap != null && photoTarget in findings.indices) {
                        val photoFile = File(cacheDir, "desvio_${System.currentTimeMillis()}.jpg")
                        FileOutputStream(photoFile).use { output ->
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, output)
                        }
                        findings = findings.toMutableList().also { list ->
                            list[photoTarget] = list[photoTarget].copy(
                                photo = Uri.fromFile(photoFile)
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Registro de Inspeção de SSMA",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text("F-SSMA-0073 • Rev.00")

                    OutlinedTextField(
                        value = responsible,
                        onValueChange = { responsible = it },
                        label = { Text("Responsável inspeção") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = number,
                        onValueChange = { number = it },
                        label = { Text("Nº Inspeção") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Data (dd/mm/aaaa)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Local") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Condições / desvios", style = MaterialTheme.typography.titleMedium)

                    findings.forEachIndexed { index, finding ->
                        FindingCard(
                            index = index,
                            finding = finding,
                            onFindingChange = { updated ->
                                findings = findings.toMutableList().also { it[index] = updated }
                            },
                            onGallery = {
                                photoTarget = index
                                galleryLauncher.launch("image/*")
                            },
                            onCamera = {
                                photoTarget = index
                                cameraLauncher.launch(null)
                            },
                            onRemovePhoto = {
                                findings = findings.toMutableList().also {
                                    it[index] = it[index].copy(photo = null)
                                }
                            },
                            loadPhoto = ::loadPhoto
                        )
                    }

                    Button(
                        onClick = { findings = findings + Finding() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Adicionar desvio")
                    }

                    Button(
                        onClick = {
                            if (findings.any { it.desc.isBlank() }) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Preencha a descrição de todos os desvios.",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                val pdfFile = makePdf(
                                    responsible,
                                    number,
                                    date,
                                    location,
                                    findings
                                )
                                db.save(
                                    responsible,
                                    number,
                                    date,
                                    location,
                                    findings.joinToString("\n") { it.desc },
                                    pdfFile.absolutePath
                                )
                                Toast.makeText(
                                    this@MainActivity,
                                    "Inspeção salva e PDF criado.",
                                    Toast.LENGTH_LONG
                                ).show()
                                sharePdf(pdfFile)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Salvar e gerar PDF")
                    }
                }
            }
        }
    }

    private fun loadPhoto(uri: Uri): Bitmap? {
        return try {
            if (uri.scheme == "file") {
                uri.path?.let(BitmapFactory::decodeFile)
            } else {
                contentResolver.openInputStream(uri)?.use(BitmapFactory::decodeStream)
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun makePdf(
        responsible: String,
        number: String,
        date: String,
        location: String,
        findings: List<Finding>
    ): File {
        val document = PdfDocument()
        var pageNumber = 1
        var page = document.startPage(
            PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
        )
        var canvas = page.canvas
        var y = 48

        val headingPaint = android.graphics.Paint().apply {
            textSize = 16f
            isFakeBoldText = true
        }
        val textPaint = android.graphics.Paint().apply { textSize = 12f }

        fun writeLine(text: String) {
            canvas.drawText(text.take(90), 40f, y.toFloat(), textPaint)
            y += 20
        }

        fun nextPage() {
            document.finishPage(page)
            pageNumber += 1
            page = document.startPage(
                PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
            )
            canvas = page.canvas
            y = 48
        }

        canvas.drawText(
            "F-SSMA-0073 • REGISTRO DE INSPEÇÃO DE SSMA • Rev.00",
            40f,
            y.toFloat(),
            headingPaint
        )
        y += 30
        writeLine("Responsável: $responsible")
        writeLine("Nº: $number   Data: $date   Local: $location")
        y += 12

        findings.forEachIndexed { index, finding ->
            if (y > 700) nextPage()
            canvas.drawText("DESVIO ${index + 1}", 40f, y.toFloat(), headingPaint)
            y += 24

            listOf(
                "Descrição: ${finding.desc}",
                "Severidade: ${finding.severity}",
                "Ação: ${finding.action}",
                "Prazo: ${finding.deadline}",
                "Responsável: ${finding.owner}"
            ).forEach { writeLine(it) }

            finding.photo?.let { photoUri ->
                val bitmap = loadPhoto(photoUri)
                if (bitmap != null) {
                    if (y + 230 > 790) nextPage()
                    val ratio = minOf(300f / bitmap.width, 220f / bitmap.height)
                    val scaled = Bitmap.createScaledBitmap(
                        bitmap,
                        (bitmap.width * ratio).toInt().coerceAtLeast(1),
                        (bitmap.height * ratio).toInt().coerceAtLeast(1),
                        true
                    )
                    canvas.drawBitmap(scaled, 40f, y.toFloat(), null)
                    y += scaled.height + 12
                    if (scaled !== bitmap) scaled.recycle()
                    bitmap.recycle()
                }
            }
            y += 18
        }

        document.finishPage(page)
        val outputFile = File(filesDir, "Inspecao_${System.currentTimeMillis()}.pdf")
        FileOutputStream(outputFile).use(document::writeTo)
        document.close()
        return outputFile
    }

    private fun sharePdf(file: File) {
        val uri = Uri.fromFile(file)
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(sendIntent, "Compartilhar PDF"))
    }
}

@androidx.compose.runtime.Composable
private fun FindingCard(
    index: Int,
    finding: Finding,
    onFindingChange: (Finding) -> Unit,
    onGallery: () -> Unit,
    onCamera: () -> Unit,
    onRemovePhoto: () -> Unit,
    loadPhoto: (Uri) -> Bitmap?
) {
    var severityMenuExpanded by remember(index) { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Desvio #${index + 1}", style = MaterialTheme.typography.titleSmall)

            OutlinedTextField(
                value = finding.desc,
                onValueChange = { onFindingChange(finding.copy(desc = it)) },
                label = { Text("Descrição do desvio (obrigatória)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Box {
                OutlinedButton(onClick = { severityMenuExpanded = true }) {
                    Text("Severidade: ${finding.severity}")
                }
                DropdownMenu(
                    expanded = severityMenuExpanded,
                    onDismissRequest = { severityMenuExpanded = false }
                ) {
                    listOf("LEVE", "MODERADA", "GRAVE", "CRÍTICO").forEach { severity ->
                        DropdownMenuItem(
                            text = { Text(severity) },
                            onClick = {
                                onFindingChange(finding.copy(severity = severity))
                                severityMenuExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = finding.action,
                onValueChange = { onFindingChange(finding.copy(action = it)) },
                label = { Text("Ação para adequação/correção") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
            OutlinedTextField(
                value = finding.deadline,
                onValueChange = { onFindingChange(finding.copy(deadline = it)) },
                label = { Text("Prazo de execução") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = finding.owner,
                onValueChange = { onFindingChange(finding.copy(owner = it)) },
                label = { Text("Responsável") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onGallery) { Text("Galeria") }
                Button(onClick = onCamera) { Text("Câmera") }
            }

            finding.photo?.let { uri ->
                val bitmap = remember(uri) { loadPhoto(uri) }
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Foto do desvio ${index + 1}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                } else {
                    Text("Não foi possível carregar a foto selecionada.")
                }
            }

            if (finding.photo != null) {
                TextButton(onClick = onRemovePhoto) {
                    Text("Remover foto")
                }
            }
        }
    }
}
