package br.com.ssma.inspecoes

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
import androidx.activity.compose.BackHandler
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

private const val APP_VERSION = "2.2.1"
private const val LOGO_ASSET = "salum_logo.jpg"
data class Finding(val desc:String="",val severity:String="LEVE",val action:String="",val deadline:String="",val owner:String="",val photo:String="")
data class Participant(val name:String="",val signature:String="")
data class Inspection(val id:Int,val responsible:String,val number:String,val date:String,val location:String,val findings:List<Finding>,val pdf:String="",val participants:List<Participant> = emptyList(),val inspectorSignature:String="",val registration:String="")

class DB(ctx:android.content.Context):SQLiteOpenHelper(ctx,"ssma.db",null,3){
 override fun onCreate(db:SQLiteDatabase){db.execSQL("CREATE TABLE inspections(id INTEGER PRIMARY KEY AUTOINCREMENT,responsible TEXT,number TEXT,date TEXT,location TEXT,findings TEXT,pdf TEXT,participants TEXT DEFAULT '[]',responsible_signature TEXT DEFAULT '',registration TEXT DEFAULT '')")}
 override fun onUpgrade(db:SQLiteDatabase,old:Int,new:Int){if(old<2){try{db.execSQL("ALTER TABLE inspections ADD COLUMN findings TEXT")}catch(_:Exception){}};if(old<3){listOf("participants TEXT DEFAULT '[]'","responsible_signature TEXT DEFAULT ''","registration TEXT DEFAULT ''").forEach{col->try{db.execSQL("ALTER TABLE inspections ADD COLUMN $col")}catch(_:Exception){}}}}
 fun encode(fs:List<Finding>):String=JSONArray().apply{fs.forEach{put(JSONObject().put("desc",it.desc).put("severity",it.severity).put("action",it.action).put("deadline",it.deadline).put("owner",it.owner).put("photo",it.photo))}}.toString()
 fun decode(s:String):List<Finding>{try{val a=JSONArray(s);return (0 until a.length()).map{val o=a.getJSONObject(it);Finding(o.optString("desc"),o.optString("severity","LEVE"),o.optString("action"),o.optString("deadline"),o.optString("owner"),o.optString("photo"))}}catch(_:Exception){return if(s.isBlank()) emptyList() else listOf(Finding(desc=s))}}
 fun encodeParticipants(ps:List<Participant>):String=JSONArray().apply{ps.forEach{put(JSONObject().put("name",it.name).put("signature",it.signature))}}.toString()
 fun decodeParticipants(s:String):List<Participant>{try{val a=JSONArray(s);return (0 until a.length()).map{val o=a.getJSONObject(it);Participant(o.optString("name"),o.optString("signature"))}}catch(_:Exception){return emptyList()}}
 fun save(i:Inspection):Int{val v=ContentValues().apply{put("responsible",i.responsible);put("number",i.number);put("date",i.date);put("location",i.location);put("findings",encode(i.findings));put("pdf",i.pdf);put("participants",encodeParticipants(i.participants));put("responsible_signature",i.inspectorSignature);put("registration",i.registration)};return writableDatabase.insert("inspections",null,v).toInt()}
 fun nextNumber():String { val c=readableDatabase.rawQuery("SELECT MAX(id) FROM inspections",null);var n=0;c.use{if(it.moveToFirst())n=it.getInt(0)};return "SCL-"+String.format("%04d",n+1) }
 fun update(i:Inspection){val v=ContentValues().apply{put("responsible",i.responsible);put("number",i.number);put("date",i.date);put("location",i.location);put("findings",encode(i.findings));put("pdf",i.pdf);put("participants",encodeParticipants(i.participants));put("responsible_signature",i.inspectorSignature);put("registration",i.registration)};writableDatabase.update("inspections",v,"id=?",arrayOf(i.id.toString()))}
 fun list():List<Inspection>{val c=readableDatabase.rawQuery("SELECT id,responsible,number,date,location,findings,pdf,participants,responsible_signature,registration FROM inspections ORDER BY id DESC",null);val l=mutableListOf<Inspection>();while(c.moveToNext()){l+=Inspection(c.getInt(0),c.getString(1)?: "",c.getString(2)?: "",c.getString(3)?: "",c.getString(4)?: "",decode(c.getString(5)?: ""),c.getString(6)?: "",decodeParticipants(c.getString(7)?: "[]"),c.getString(8)?: "",c.getString(9)?: "")};c.close();return l}
}

class MainActivity:ComponentActivity(){
 private lateinit var db:DB
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);db=DB(this)
 window.statusBarColor = android.graphics.Color.rgb(37,45,135); window.navigationBarColor = android.graphics.Color.rgb(244,248,252); if (android.os.Build.VERSION.SDK_INT >= 29) window.isNavigationBarContrastEnforced = false; setContent { MaterialTheme(colorScheme=lightColorScheme(primary=Color(0xFF252D87),secondary=Color(0xFF20A9D2),background=Color(0xFFF4F8FC),surface=Color.White)){
 var screen by remember{mutableStateOf("home")};var editing by remember{mutableStateOf<Inspection?>(null)};var refresh by remember{mutableIntStateOf(0)};var dateDialog by remember{mutableStateOf(false)};var deadlineDialogIndex by remember{mutableIntStateOf(-1)}
 fun toast(s:String)=Toast.makeText(this@MainActivity,s,Toast.LENGTH_SHORT).show()
 fun shareFiles(files:List<File>,zip:Boolean=false){try{val uris=files.map{FileProvider.getUriForFile(this@MainActivity,"br.com.ssma.inspecoes.fileprovider",it)};val intent=if(uris.size==1&&!zip)Intent(Intent.ACTION_SEND).apply{type="application/pdf";putExtra(Intent.EXTRA_STREAM,uris[0])}else Intent(Intent.ACTION_SEND_MULTIPLE).apply{type=if(zip)"application/zip" else "application/pdf";putParcelableArrayListExtra(Intent.EXTRA_STREAM,ArrayList(uris))};intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);startActivity(Intent.createChooser(intent,"Compartilhar exportação"))}catch(e:Exception){toast("Falha ao compartilhar: ${e.message}")}}
 fun pdf(i:Inspection):File{val logo=assets.open(LOGO_ASSET).use{BitmapFactory.decodeStream(it)};val doc=PdfDocument();var page=doc.startPage(PdfDocument.PageInfo.Builder(595,842,1).create());var canvas=page.canvas;val p=android.graphics.Paint().apply{textSize=12f};val h=android.graphics.Paint().apply{textSize=16f;isFakeBoldText=true};var y=45
 fun header(){val paint=android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);canvas.drawBitmap(logo,null,android.graphics.RectF(40f,24f,112f,96f),paint);canvas.drawText("F-SSMA-0073 • REGISTRO DE INSPEÇÃO DE SSMA",128f,58f,h);canvas.drawText("SALUM CONSTRUÇÕES",128f,80f,p)}
 fun newPage(){doc.finishPage(page);page=doc.startPage(PdfDocument.PageInfo.Builder(595,842,doc.pages.size+1).create());canvas=page.canvas;y=120;header()}
 fun line(t:String){if(y>780)newPage();canvas.drawText(t.take(100),40f,y.toFloat(),p);y+=20}
 header();y=125;line("Responsável: ${i.responsible}");line("Nº: ${i.number} | Data: ${i.date} | Local: ${i.location}");y+=12
 i.findings.forEachIndexed{n,f->canvas.drawText("DESVIO ${n+1}",40f,y.toFloat(),h);y+=25;line("Descrição: ${f.desc}");line("Severidade: ${f.severity}");line("Ação: ${f.action}");line("Prazo: ${f.deadline}");line("Responsável: ${f.owner}");if(f.photo.isNotBlank()){try{val b=loadBitmap(this@MainActivity, Uri.parse(f.photo));if(b!=null){if(y+200>780){doc.finishPage(page);page=doc.startPage(PdfDocument.PageInfo.Builder(595,842,doc.pages.size+1).create());canvas=page.canvas;y=45};val scaled=Bitmap.createScaledBitmap(b, minOf(b.width,300),minOf(b.height,200),true);canvas.drawBitmap(scaled,40f,y.toFloat(),null);y+=scaled.height+10;b.recycle()}}catch(_:Exception){}};y+=15}
  line("PARTICIPANTES");i.participants.forEachIndexed{n,participant->line("${n+1}. ${participant.name}");if(participant.signature.isNotBlank()){val sig=loadBitmap(this@MainActivity,Uri.parse(participant.signature));if(sig!=null){if(y+100>780){doc.finishPage(page);page=doc.startPage(PdfDocument.PageInfo.Builder(595,842,doc.pages.size+1).create());canvas=page.canvas;y=45};val sc=minOf(180f/sig.width,70f/sig.height,1f);val scaled=Bitmap.createScaledBitmap(sig,(sig.width*sc).toInt().coerceAtLeast(1),(sig.height*sc).toInt().coerceAtLeast(1),true);canvas.drawBitmap(scaled,40f,y.toFloat(),null);y+=scaled.height+8;sig.recycle()}}};line("Matrícula do responsável: ${i.registration}");if(i.inspectorSignature.isNotBlank()){line("Assinatura do responsável pela inspeção:");val sig=loadBitmap(this@MainActivity,Uri.parse(i.inspectorSignature));if(sig!=null){if(y+100>780){doc.finishPage(page);page=doc.startPage(PdfDocument.PageInfo.Builder(595,842,doc.pages.size+1).create());canvas=page.canvas;y=45};val sc=minOf(220f/sig.width,80f/sig.height,1f);val scaled=Bitmap.createScaledBitmap(sig,(sig.width*sc).toInt().coerceAtLeast(1),(sig.height*sc).toInt().coerceAtLeast(1),true);canvas.drawBitmap(scaled,40f,y.toFloat(),null);y+=scaled.height+10;sig.recycle()}};doc.finishPage(page);val f=File(filesDir,"Inspeção_${safeFilePart(i.number)}_${safeFilePart(i.date)}_${safeFilePart(i.location)}.pdf");FileOutputStream(f).use{doc.writeTo(it)};doc.close();logo.recycle();return f}
 var photoIndex by remember{mutableIntStateOf(-1)}
 var responsible by remember{mutableStateOf("")};var number by remember{mutableStateOf("")};var date by remember{mutableStateOf("")};var location by remember{mutableStateOf("")};var findings by remember{mutableStateOf(listOf(Finding()))};var participants by remember{mutableStateOf(listOf(Participant()))};var inspectorSignature by remember{mutableStateOf("")};var registration by remember{mutableStateOf("")};var signatureTarget by remember{mutableStateOf<String?>(null)}
 val gallery=rememberLauncherForActivityResult(ActivityResultContracts.GetContent()){uri->if(uri!=null&&photoIndex>=0&&photoIndex<findings.size)findings=findings.toMutableList().also{list->list[photoIndex]=list[photoIndex].copy(photo=uri.toString())}}
 val camera=rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()){b->if(b!=null&&photoIndex in findings.indices){val f=File(filesDir,"photo_${System.currentTimeMillis()}.jpg");FileOutputStream(f).use{b.compress(Bitmap.CompressFormat.JPEG,85,it)};findings=findings.toMutableList().also{list->list[photoIndex]=list[photoIndex].copy(photo=Uri.fromFile(f).toString())}}}
 
 var filterStart by remember{mutableStateOf("")};var filterEnd by remember{mutableStateOf("")};var selected by remember{mutableStateOf(setOf<Int>())}
 fun startForm(ins:Inspection?){editing=ins;responsible=ins?.responsible?: "";number=ins?.number?:db.nextNumber();date=ins?.date?: "";location=ins?.location?: "";findings=ins?.findings?:listOf(Finding());participants=ins?.participants?:listOf(Participant());inspectorSignature=ins?.inspectorSignature?: "";registration=ins?.registration?: "";screen="form"}
 BackHandler(enabled=screen!="home"){screen="home"}
 if(dateDialog){val state=rememberDatePickerState();DatePickerDialog(onDismissRequest={dateDialog=false},confirmButton={TextButton(onClick={state.selectedDateMillis?.let{ms->date=java.text.SimpleDateFormat("dd/MM/yyyy",java.util.Locale("pt","BR")).format(java.util.Date(ms))};dateDialog=false}){Text("Confirmar")}},dismissButton={TextButton(onClick={dateDialog=false}){Text("Cancelar")}}){DatePicker(state)}}
 if(deadlineDialogIndex>=0){val idx=deadlineDialogIndex;val current=findings.getOrNull(idx)?.deadline?: "";val initialMillis=try{java.text.SimpleDateFormat("dd/MM/yyyy",java.util.Locale("pt","BR")).parse(current)?.time}catch(_:Exception){null};val state=rememberDatePickerState(initialSelectedDateMillis=initialMillis);DatePickerDialog(onDismissRequest={deadlineDialogIndex=-1},confirmButton={TextButton(onClick={state.selectedDateMillis?.let{ms->findings=findings.toMutableList().also{list->list[idx]=list[idx].copy(deadline=java.text.SimpleDateFormat("dd/MM/yyyy",java.util.Locale("pt","BR")).format(java.util.Date(ms)))} };deadlineDialogIndex=-1}){Text("Confirmar")}},dismissButton={TextButton(onClick={deadlineDialogIndex=-1}){Text("Cancelar")}}){DatePicker(state)}}
 Column(Modifier.fillMaxSize().imePadding().navigationBarsPadding().padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
 when(screen){
 "home"->Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(18.dp)){
 Card(colors=CardDefaults.cardColors(containerColor=Color(0xFFEAF6FC)),modifier=Modifier.fillMaxWidth(),shape=RoundedCornerShape(20.dp)){Column(Modifier.fillMaxWidth().padding(20.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(12.dp)){val logoBitmap=remember { assets.open(LOGO_ASSET).use { BitmapFactory.decodeStream(it) } };Image(bitmap=logoBitmap.asImageBitmap(),contentDescription="Logo Salum Construções",modifier=Modifier.fillMaxWidth().height(132.dp),contentScale=ContentScale.Fit);Text("REGISTRO DE INSPEÇÃO DE SSMA",style=MaterialTheme.typography.titleLarge,color=Color(0xFF252D87),textAlign=androidx.compose.ui.text.style.TextAlign.Center,modifier=Modifier.fillMaxWidth());Text("F-SSMA-0073 • Rev.00",style=MaterialTheme.typography.bodyMedium);Text("Versão do aplicativo: $APP_VERSION",style=MaterialTheme.typography.labelMedium)}}
 Button(onClick={startForm(null)},modifier=Modifier.fillMaxWidth().height(60.dp)){Text("NOVO REGISTRO")}
 OutlinedButton(onClick={screen="history"},modifier=Modifier.fillMaxWidth().height(60.dp)){Text("CONSULTAR INSPEÇÕES")}
 }
 "form"->Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom=24.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
 Row(verticalAlignment=Alignment.CenterVertically){Button(onClick={screen="home"},modifier=Modifier.height(52.dp)){Text("← INÍCIO",style=MaterialTheme.typography.titleMedium)};Spacer(Modifier.width(12.dp));Text(if(editing==null)"Novo registro" else "Editar registro",style=MaterialTheme.typography.titleLarge)}
 OutlinedTextField(responsible,{responsible=it},label={Text("Responsável inspeção")},modifier=Modifier.fillMaxWidth())
 OutlinedTextField(number,{number=it},label={Text("Nº Inspeção")},modifier=Modifier.fillMaxWidth(),readOnly=editing==null)
 OutlinedTextField(date,{date=it},label={Text("Data (dd/mm/aaaa)")},placeholder={if(date.isBlank())Text("Clique no calendário para inserir a data")},modifier=Modifier.fillMaxWidth(),readOnly=true,trailingIcon={IconButton(onClick={dateDialog=true}){Text("📅")}})
 OutlinedTextField(location,{location=it},label={Text("Local")},modifier=Modifier.fillMaxWidth())
 Text("Participantes",style=MaterialTheme.typography.titleMedium)
 participants.forEachIndexed{pi,participant->Card{Column(Modifier.padding(10.dp),verticalArrangement=Arrangement.spacedBy(6.dp)){OutlinedTextField(participant.name,{v->participants=participants.toMutableList().also{it[pi]=participant.copy(name=v)}},label={Text("Nome do participante")},modifier=Modifier.fillMaxWidth());Row{Button(onClick={signatureTarget="participant:$pi"}){Text(if(participant.signature.isBlank())"Assinar" else "Refazer assinatura")};TextButton(onClick={participants=participants.toMutableList().also{it.removeAt(pi)}}){Text("Remover")}};if(participant.signature.isNotBlank()){loadBitmap(this@MainActivity,Uri.parse(participant.signature))?.let{Image(it.asImageBitmap(),null,Modifier.fillMaxWidth().height(90.dp))}}}}}
 Button(onClick={participants=participants+Participant()},modifier=Modifier.fillMaxWidth()){Text("Adicionar participante")}
 OutlinedTextField(registration,{registration=it},label={Text("Matrícula do responsável pela inspeção")},modifier=Modifier.fillMaxWidth())
 Row{Button(onClick={signatureTarget="inspector"}){Text(if(inspectorSignature.isBlank())"Assinatura do responsável" else "Refazer assinatura")};TextButton(onClick={inspectorSignature=""}){Text("Limpar")}}
 if(inspectorSignature.isNotBlank()){loadBitmap(this@MainActivity,Uri.parse(inspectorSignature))?.let{Image(it.asImageBitmap(),null,Modifier.fillMaxWidth().height(100.dp))}}
 findings.forEachIndexed{idx,f->Card{Column(Modifier.padding(12.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){Row(verticalAlignment= androidx.compose.ui.Alignment.CenterVertically){Text("Desvio #${idx+1}",style=MaterialTheme.typography.titleMedium);Spacer(Modifier.weight(1f));if(findings.size>1)TextButton(onClick={findings=findings.filterIndexed{j,_->j!=idx};toast("Desvio excluído")}){Text("Excluir")}};OutlinedTextField(f.desc,{value->findings=findings.toMutableList().also{list->list[idx]=f.copy(desc=value)}},label={Text("Descrição (obrigatória)")},modifier=Modifier.fillMaxWidth(),minLines=2);var expanded by remember{mutableStateOf(false)};Box{OutlinedButton(onClick={expanded=true}){Text("Severidade: ${f.severity}")};DropdownMenu(expanded,{expanded=false}){listOf("LEVE","MODERADA","GRAVE","CRÍTICO").forEach{s->DropdownMenuItem(text={Text(s)},onClick={findings=findings.toMutableList().also{it[idx]=f.copy(severity=s)};expanded=false})}}};OutlinedTextField(f.action,{value->findings=findings.toMutableList().also{list->list[idx]=f.copy(action=value)}},label={Text("Ação para adequação/correção")},modifier=Modifier.fillMaxWidth());OutlinedTextField(f.deadline,{value->findings=findings.toMutableList().also{list->list[idx]=f.copy(deadline=value)}},label={Text("Prazo de execução")},placeholder={if(f.deadline.isBlank())Text("Clique no calendário para inserir a data")},modifier=Modifier.fillMaxWidth(),readOnly=true,trailingIcon={IconButton(onClick={deadlineDialogIndex=idx}){Text("📅")}});OutlinedTextField(f.owner,{value->findings=findings.toMutableList().also{list->list[idx]=f.copy(owner=value)}},label={Text("Responsável")},modifier=Modifier.fillMaxWidth());Row{Button(onClick={photoIndex=idx;gallery.launch("image/*")}){Text("Galeria")};Button(onClick={photoIndex=idx;camera.launch(null)}){Text("Câmera")};TextButton(onClick={findings=findings.toMutableList().also{it[idx]=f.copy(photo="")}}){Text("Remover foto")}};if(f.photo.isNotBlank()){val bm=remember(f.photo){loadBitmap(this@MainActivity, Uri.parse(f.photo))};bm?.let{Image(it.asImageBitmap(),null,Modifier.fillMaxWidth().height(160.dp))}}}}}
 Button(onClick={findings=findings+Finding()},modifier=Modifier.fillMaxWidth()){Text("Adicionar desvio")}
 if(signatureTarget!=null){SignaturePadDialog(onDismiss={signatureTarget=null},onSave={path->val target=signatureTarget;if(target=="inspector")inspectorSignature=path else if(target?.startsWith("participant:")==true){val idx=target.substringAfter(":").toIntOrNull();if(idx!=null&&idx in participants.indices)participants=participants.toMutableList().also{it[idx]=it[idx].copy(signature=path)} };signatureTarget=null})}
 Button(onClick={if(findings.any{it.desc.isBlank()}){toast("Preencha a descrição de todos os desvios")}else{val record=Inspection(editing?.id?:0,responsible,number,date,location,findings,participants=participants,inspectorSignature=inspectorSignature,registration=registration);val file=pdf(record);val saved=record.copy(pdf=file.absolutePath);if(editing==null)db.save(saved) else db.update(saved);toast("Registro salvo");refresh++;shareFiles(listOf(file));screen="home"}},modifier=Modifier.fillMaxWidth()){Text("Salvar e exportar PDF")}
 }
 "history"->Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(10.dp)){
 Row{TextButton(onClick={screen="home"}){Text("← Início")};Text("Inspeções realizadas",style=MaterialTheme.typography.titleLarge)}
 OutlinedTextField(filterStart,{filterStart=it},label={Text("Data inicial (dd/mm/aaaa)")},modifier=Modifier.fillMaxWidth());OutlinedTextField(filterEnd,{filterEnd=it},label={Text("Data final (dd/mm/aaaa)")},modifier=Modifier.fillMaxWidth())
 fun dateKey(raw:String):Long { return try { val parts=raw.trim().split("/"); if(parts.size!=3) 0L else (parts[2].toInt()*10000L+parts[1].toInt()*100L+parts[0].toInt()) } catch(_:Exception){0L} }
 val startKey=if(filterStart.isBlank())0L else dateKey(filterStart);val endKey=if(filterEnd.isBlank())Long.MAX_VALUE else dateKey(filterEnd)
 val records=db.list().filter{val k=dateKey(it.date);(filterStart.isBlank()||k>=startKey)&&(filterEnd.isBlank()||k<=endKey)}.sortedByDescending{dateKey(it.date)}
 Row{Checkbox(checked=records.isNotEmpty()&&records.all{it.id in selected},onCheckedChange={selected=if(it)selected+records.map{r->r.id}else selected-records.map{r->r.id}});Text("Selecionar todas")}
 if(selected.isNotEmpty())Button(onClick={val chosen=records.filter{it.id in selected};val z=File(cacheDir,"Inspecoes_${System.currentTimeMillis()}.zip");ZipOutputStream(FileOutputStream(z)).use{zip->chosen.forEach{r->val f=File(r.pdf);val pdfFile=if(f.exists())f else pdf(r);zip.putNextEntry(ZipEntry("Inspecao_${r.number.ifBlank{r.id.toString()}}.pdf"));pdfFile.inputStream().use{it.copyTo(zip)};zip.closeEntry()}};shareFiles(listOf(z),true);selected=setOf()},modifier=Modifier.fillMaxWidth()){Text("Exportar selecionadas em ZIP")}
 records.forEach{r->Card(Modifier.fillMaxWidth()){Column(Modifier.padding(12.dp),verticalArrangement=Arrangement.spacedBy(6.dp)){Row{Checkbox(checked=r.id in selected,onCheckedChange={selected=if(it)selected+r.id else selected-r.id});Column{Text("Inspeção ${r.number.ifBlank{"#${r.id}"}}",style=MaterialTheme.typography.titleMedium);Text("${r.date} • ${r.location}");Text("${r.responsible}")}};Row{Button(onClick={startForm(r)}){Text("Editar")};Button(onClick={val f=File(r.pdf);shareFiles(listOf(if(f.exists())f else pdf(r)))}){Text("PDF")}}}}}
 }
 }
 }
 }
 }
 }
 }
 private fun safeFilePart(raw:String):String = raw.trim().replace(Regex("[^A-Za-z0-9_-]"),"_").trim('_').ifBlank{"sem_dado"}
private fun loadBitmap(context:android.content.Context, uri:Uri):Bitmap? {
  return try {
   if(uri.scheme=="file") { BitmapFactory.decodeFile(uri.path) }
   else { val stream=context.contentResolver.openInputStream(uri); stream?.use { input -> BitmapFactory.decodeStream(input) } }
  } catch(_:Exception) { null }
 }

@Composable
private fun SignaturePadDialog(onDismiss:()->Unit,onSave:(String)->Unit){
 val context=androidx.compose.ui.platform.LocalContext.current
 var strokes by remember{mutableStateOf(listOf<List<Offset>>())}
 var active by remember{mutableStateOf(listOf<Offset>())}
 var canvasSize by remember{mutableStateOf(IntSize.Zero)}
 AlertDialog(
  onDismissRequest=onDismiss,
  title={Text("Capturar assinatura")},
  text={
   Column(verticalArrangement=Arrangement.spacedBy(8.dp)){
    Text("Assine dentro da área abaixo.")
    Canvas(
     Modifier.fillMaxWidth().height(180.dp).border(1.dp,Color.Gray,RoundedCornerShape(8.dp)).background(Color.White)
      .onSizeChanged{canvasSize=it}
      .pointerInput(Unit){
       detectDragGestures(
        onDragStart={active=listOf(it)},
        onDrag={change,_->change.consume();active=active+change.position},
        onDragEnd={if(active.isNotEmpty())strokes=strokes+listOf(active);active=emptyList()}
       )
      }
    ){
     (strokes+listOf(active)).forEach{pts->
      if(pts.size>1){
       val path=Path().apply{moveTo(pts.first().x,pts.first().y);pts.drop(1).forEach{lineTo(it.x,it.y)}}
       drawPath(path,Color.Black,style=Stroke(width=4.dp.toPx()))
      }
     }
    }
    TextButton(onClick={strokes=emptyList();active=emptyList()}){Text("Limpar assinatura")}
   }
  },
  confirmButton={
   Button(onClick={
    if(strokes.isNotEmpty()&&canvasSize.width>0&&canvasSize.height>0){
     val bmp=Bitmap.createBitmap(1000,400,Bitmap.Config.ARGB_8888)
     val c=android.graphics.Canvas(bmp);c.drawColor(android.graphics.Color.WHITE)
     val paint=android.graphics.Paint().apply{color=android.graphics.Color.BLACK;strokeWidth=8f;style=android.graphics.Paint.Style.STROKE;strokeCap=android.graphics.Paint.Cap.ROUND;strokeJoin=android.graphics.Paint.Join.ROUND;isAntiAlias=true}
     val sx=1000f/canvasSize.width;val sy=400f/canvasSize.height
     (strokes+listOf(active)).forEach{pts->
      if(pts.size>1){
       val path=android.graphics.Path().apply{moveTo(pts.first().x*sx,pts.first().y*sy);pts.drop(1).forEach{lineTo(it.x*sx,it.y*sy)}}
       c.drawPath(path,paint)
      }
     }
     val file=File(context.filesDir,"signature_${System.currentTimeMillis()}.png")
     FileOutputStream(file).use{bmp.compress(Bitmap.CompressFormat.PNG,100,it)}
     bmp.recycle()
     onSave(Uri.fromFile(file).toString())
    }else onDismiss()
   }){Text("Salvar")}
  },
  dismissButton={TextButton(onClick=onDismiss){Text("Cancelar")}}
 )
}
