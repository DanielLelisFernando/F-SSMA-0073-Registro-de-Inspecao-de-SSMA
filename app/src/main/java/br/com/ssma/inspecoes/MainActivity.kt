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
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp\nimport androidx.compose.ui.graphics.Color\nimport androidx.compose.foundation.background\nimport androidx.compose.foundation.shape.RoundedCornerShape\nimport androidx.compose.ui.text.font.FontWeight
import java.io.File
import java.io.FileOutputStream

data class Finding(var desc:String="",var severity:String="LEVE",var action:String="",var deadline:String="",var owner:String="",var photo:Uri?=null)
class DB(ctx:Activity):SQLiteOpenHelper(ctx,"ssma.db",null,1){
 override fun onCreate(db:SQLiteDatabase){db.execSQL("CREATE TABLE inspections(id INTEGER PRIMARY KEY, responsible TEXT, number TEXT, date TEXT, location TEXT, findings TEXT, pdf TEXT)")}
 override fun onUpgrade(db:SQLiteDatabase,o:Int,n:Int){}
 fun save(responsible:String,number:String,date:String,location:String,findings:String,pdf:String){val v=ContentValues().apply{put("responsible",responsible);put("number",number);put("date",date);put("location",location);put("findings",findings);put("pdf",pdf)};writableDatabase.insert("inspections",null,v)}
}
class MainActivity:ComponentActivity(){
 private lateinit var db:DB
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);db=DB(this)
 setContent { MaterialTheme(colorScheme = lightColorScheme(primary = Color(0xFF252D87), secondary = Color(0xFF20A9D2), background = Color(0xFFF4F8FC), surface = Color.White, surfaceVariant = Color(0xFFEAF6FC))) {
 var resp by remember { mutableStateOf("") }; var num by remember { mutableStateOf("") }; var date by remember { mutableStateOf("") }; var local by remember { mutableStateOf("") }
 var items by remember { mutableStateOf(listOf(Finding())) }
 var photoTarget by remember { mutableIntStateOf(0) }
 val gallery=rememberLauncherForActivityResult(ActivityResultContracts.GetContent()){uri->if(uri!=null)items=items.toMutableList().also{it[photoTarget]=it[photoTarget].copy(photo=uri)}}
 val camera=rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()){bitmap->if(bitmap!=null){val f=File(cacheDir,"desvio_${System.currentTimeMillis()}.jpg");FileOutputStream(f).use{bitmap.compress(Bitmap.CompressFormat.JPEG,85,it)};items=items.toMutableList().also{it[photoTarget]=it[photoTarget].copy(photo=Uri.fromFile(f))}}}
 Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
 Text("Registro de Inspeção de SSMA",style=MaterialTheme.typography.headlineSmall)
 Text("F-SSMA-0073 • Rev.00",style=MaterialTheme.typography.bodyMedium)
 OutlinedTextField(resp,{resp=it},label={Text("Responsável inspeção")},modifier=Modifier.fillMaxWidth())
 OutlinedTextField(num,{num=it},label={Text("Nº Inspeção")},modifier=Modifier.fillMaxWidth())
 OutlinedTextField(date,{date=it},label={Text("Data (dd/mm/aaaa)")},modifier=Modifier.fillMaxWidth())
 OutlinedTextField(local,{local=it},label={Text("Local")},modifier=Modifier.fillMaxWidth())
 Text("Condições / desvios",style=MaterialTheme.typography.titleMedium)
 items.forEachIndexed { idx, f ->
 Card(Modifier.fillMaxWidth(),shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=Color.White)) {Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
 Text("Desvio #${idx+1}",style=MaterialTheme.typography.titleSmall)
 OutlinedTextField(f.desc,{v->items=items.toMutableList().also{it[idx]=it[idx].copy(desc=v)}},label={Text("Descrição do desvio (obrigatória)")},modifier=Modifier.fillMaxWidth(),minLines=3)
 var expanded by remember(idx){mutableStateOf(false)}
 Box { OutlinedButton(onClick={expanded=true}){Text("Severidade: ${f.severity}")};DropdownMenu(expanded,onDismissRequest={expanded=false}){listOf("LEVE","MODERADA","GRAVE","CRÍTICO").forEach{DropdownMenuItem(text={Text(it)},onClick={items=items.toMutableList().also{ls->ls[idx]=ls[idx].copy(severity=it)};expanded=false})}}}
 OutlinedTextField(f.action,{v->items=items.toMutableList().also{it[idx]=it[idx].copy(action=v)}},label={Text("Ação para adequação/correção")},modifier=Modifier.fillMaxWidth(),minLines=2)
 OutlinedTextField(f.deadline,{v->items=items.toMutableList().also{it[idx]=it[idx].copy(deadline=v)}},label={Text("Prazo de execução")},modifier=Modifier.fillMaxWidth())
 OutlinedTextField(f.owner,{v->items=items.toMutableList().also{it[idx]=it[idx].copy(owner=v)}},label={Text("Responsável")},modifier=Modifier.fillMaxWidth())
 Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Button(onClick={photoTarget=idx;gallery.launch("image/*")}){Text("Galeria")};Button(onClick={photoTarget=idx;camera.launch(null)}){Text("Câmera")}}
 f.photo?.let{u->try{val bmp=if(u.scheme=="file")BitmapFactory.decodeFile(u.path) else contentResolver.openInputStream(u)?.use{BitmapFactory.decodeStream(it)};bmp?.let{Image(it.asImageBitmap(),null,Modifier.height(180.dp))}}catch(_:Exception){}}
 if(f.photo!=null)TextButton(onClick={items=items.toMutableList().also{it[idx]=it[idx].copy(photo=null)}}){Text("Remover foto")}
 }}
 }
 Button(onClick={items=items+Finding()},modifier=Modifier.fillMaxWidth()){Text("Adicionar desvio")}
 Button(onClick={if(items.any{it.desc.isBlank()})Toast.makeText(this@MainActivity,"Preencha a descrição de todos os desvios.",Toast.LENGTH_LONG).show() else {val pdf=makePdf(resp,num,date,local,items);db.save(resp,num,date,local,items.joinToString("\\n"){it.desc},pdf.absolutePath);Toast.makeText(this@MainActivity,"Inspeção salva e PDF criado.",Toast.LENGTH_LONG).show();sharePdf(pdf)},modifier=Modifier.fillMaxWidth()){Text("Salvar e gerar PDF")}
 }
 }}
 }
 private fun makePdf(resp:String,num:String,date:String,local:String,items:List<Finding>):File{
 val doc=PdfDocument();var page=doc.startPage(PdfDocument.PageInfo.Builder(595,842,1).create());var canvas=page.canvas;var y=48
 val p=android.graphics.Paint().apply{textSize=16f;isFakeBoldText=true}
 val t=android.graphics.Paint().apply{textSize=12f}
 fun line(s:String){canvas.drawText(s,40f,y.toFloat(),t);y+=20}
 canvas.drawText("F-SSMA-0073 • REGISTRO DE INSPEÇÃO DE SSMA • Rev.00",40f,y.toFloat(),p);y+=30
 line("Responsável: $resp");line("Nº: $num   Data: $date   Local: $local");y+=12
 items.forEachIndexed{index,f->if(y>730){doc.finishPage(page);page=doc.startPage(PdfDocument.PageInfo.Builder(595,842,doc.pages.size+1).create());canvas=page.canvas;y=48}
 canvas.drawText("DESVIO ${index+1}",40f,y.toFloat(),p);y+=24
 listOf("Descrição: "+f.desc,"Severidade: "+f.severity,"Ação: "+f.action,"Prazo: "+f.deadline,"Responsável: "+f.owner).forEach{line(it)}
 f.photo?.let{u->try{val b=if(u.scheme=="file")BitmapFactory.decodeFile(u.path) else contentResolver.openInputStream(u)?.use{BitmapFactory.decodeStream(it)};if(b!=null){val scaled=Bitmap.createScaledBitmap(b,300,220,true);canvas.drawBitmap(scaled,40f,y.toFloat(),null);y+=230}}catch(_:Exception){}}
 y+=18
 }
 doc.finishPage(page);val out=File(filesDir,"Inspecao_${System.currentTimeMillis()}.pdf");FileOutputStream(out).use{doc.writeTo(it)};doc.close();return out
 }
 private fun sharePdf(file:File){val uri=Uri.fromFile(file);val i=Intent(Intent.ACTION_SEND).apply{type="application/pdf";putExtra(Intent.EXTRA_STREAM,uri);addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)};startActivity(Intent.createChooser(i,"Compartilhar PDF"))}
}
