package br.com.ssma.inspecoes

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
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
import androidx.exifinterface.media.ExifInterface
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

private const val APP_VERSION = "2.2.18"
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

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity:ComponentActivity(){
 private lateinit var db:DB
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);db=DB(this)
 window.statusBarColor = android.graphics.Color.rgb(37,45,135); window.navigationBarColor = android.graphics.Color.rgb(244,248,252); if (android.os.Build.VERSION.SDK_INT >= 29) window.isNavigationBarContrastEnforced = false; setContent { MaterialTheme(colorScheme=lightColorScheme(primary=Color(0xFF252D87),secondary=Color(0xFF20A9D2),background=Color(0xFFF4F8FC),surface=Color.White)){
 var screen by remember{mutableStateOf("home")};var editing by remember{mutableStateOf<Inspection?>(null)};var refresh by remember{mutableIntStateOf(0)};var dateDialog by remember{mutableStateOf(false)};var deadlineDialogIndex by remember{mutableIntStateOf(-1)}
 fun toast(s:String)=Toast.makeText(this@MainActivity,s,Toast.LENGTH_SHORT).show()
 fun shareFiles(files:List<File>,zip:Boolean=false){try{val validFiles=files.filter{it.exists()&&it.isFile};if(validFiles.isEmpty()){toast("Nenhum arquivo disponível para compartilhar");return};val uris=validFiles.map{FileProvider.getUriForFile(this@MainActivity,"br.com.ssma.inspecoes.fileprovider",it)};val mime=if(zip)"application/zip" else "application/pdf";val intent=if(uris.size==1)Intent(Intent.ACTION_SEND).apply{type=mime;putExtra(Intent.EXTRA_STREAM,uris[0])}else Intent(Intent.ACTION_SEND_MULTIPLE).apply{type=mime;putParcelableArrayListExtra(Intent.EXTRA_STREAM,ArrayList(uris))};val clip=android.content.ClipData.newUri(contentResolver,"Compartilhar exportação",uris.first());uris.drop(1).forEach{clip.addItem(android.content.ClipData.Item(it))};intent.clipData=clip;intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);startActivity(Intent.createChooser(intent,"Compartilhar exportação"))}catch(e:Exception){toast("Falha ao compartilhar: ${e.message}")}}
 fun pdf(i:Inspection):File {
  val logo=assets.open(LOGO_ASSET).use{BitmapFactory.decodeStream(it)}
  val doc=PdfDocument()
  val p=android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply{textSize=11f}
  val h=android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply{textSize=15f;isFakeBoldText=true}
  var pageNo=1
  var page=doc.startPage(PdfDocument.PageInfo.Builder(595,842,pageNo).create())
  var canvas=page.canvas
  var y=120f
  fun header(){
   val paint=android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
   canvas.drawBitmap(logo,null,android.graphics.RectF(40f,24f,112f,96f),paint)
   canvas.drawText("F-SSMA-0073 • REGISTRO DE INSPEÇÃO DE SSMA",128f,58f,h)
   canvas.drawText("SALUM CONSTRUÇÕES • Rev.00 • 31/08/2022",128f,80f,p)
   canvas.drawLine(40f,105f,555f,105f,paint)
  }
  fun finishCurrent(){
   val footer=android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply{textSize=9f;color=android.graphics.Color.GRAY}
   canvas.drawText("F-SSMA-0073 Rev.00 • Página $pageNo",40f,815f,footer)
   doc.finishPage(page)
  }
  fun newPage(){
   finishCurrent()
   pageNo++
   page=doc.startPage(PdfDocument.PageInfo.Builder(595,842,pageNo).create())
   canvas=page.canvas
   y=120f
   header()
  }
  fun ensureSpace(height:Float){if(y+height>790f)newPage()}
  fun line(text:String,paint:android.graphics.Paint=p){
   var remaining=text
   if(remaining.isEmpty()){ensureSpace(18f);y+=18f;return}
   while(remaining.isNotEmpty()){
    ensureSpace(18f)
    val count=paint.breakText(remaining,true,515f,null).coerceAtLeast(1)
    val chunk=remaining.substring(0,count)
    canvas.drawText(chunk,40f,y,paint)
    remaining=remaining.substring(count).trimStart()
    y+=18f
   }
  }
  fun image(raw:String,maxWidth:Float,maxHeight:Float){
   try{
    val original=loadBitmap(this@MainActivity,Uri.parse(raw),maxWidth.toInt().coerceAtLeast(1),maxHeight.toInt().coerceAtLeast(1))
    if(original!=null){
     ensureSpace(maxHeight+24f)
     val scale=minOf(maxWidth/original.width,maxHeight/original.height,1f)
     val w=(original.width*scale).toInt().coerceAtLeast(1)
     val hh=(original.height*scale).toInt().coerceAtLeast(1)
     val scaled=Bitmap.createScaledBitmap(original,w,hh,true)
     canvas.drawBitmap(scaled,40f,y,null)
     y+=hh+12f
     if(scaled!==original)scaled.recycle()
     original.recycle()
    }
   }catch(_:Exception){}
  }
  header()
  line("IDENTIFICAÇÃO DA INSPEÇÃO",h)
  line("Responsável: ${i.responsible}")
  line("Nº: ${i.number} • Data: ${i.date} • Local: ${i.location}")
  y+=8f
  line("PARTICIPANTES",h)
  i.participants.forEachIndexed{n,participant->
   if(participant.signature.isNotBlank())ensureSpace(190f)
   line("${n+1}. ${participant.name}")
   if(participant.signature.isNotBlank()){line("Assinatura do participante:");image(participant.signature,220f,80f)}
  }
  y+=8f
  line("CLASSIFICAÇÃO DE SEVERIDADE",h)
  listOf("LEVE","MODERADA","GRAVE","CRÍTICO").forEach{line("$it — ${severityDescription(it)}")}
  y+=8f
  line("CONDIÇÕES DE RISCO E AÇÕES",h)
  val tableWidths=floatArrayOf(30f,145f,65f,125f,75f,75f)
  val tableLabels=listOf(listOf("Nº"),listOf("CONDIÇÃO DE RISCO/","NÃO CONFORME"),listOf("SEVERIDADE"),listOf("AÇÃO PARA","ADEQUAÇÃO/","CORREÇÃO"),listOf("PRAZO DE","EXECUÇÃO"),listOf("RESPONSÁVEL"))
  val tableHeaderPaint=android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply{textSize=9f;isFakeBoldText=true;color=android.graphics.Color.rgb(37,45,135)}
  val tableCellPaint=android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply{textSize=9.5f;color=android.graphics.Color.BLACK}
  val gridPaint=android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply{color=android.graphics.Color.rgb(130,140,150);strokeWidth=0.6f;style=android.graphics.Paint.Style.STROKE}
  val headerFill=android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply{color=android.graphics.Color.rgb(226,237,248);style=android.graphics.Paint.Style.FILL}
  val cellFill=android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply{color=android.graphics.Color.WHITE;style=android.graphics.Paint.Style.FILL}
  val tableHeaderHeight=38f
  fun drawTableHeader(){
   ensureSpace(tableHeaderHeight)
   val top=y
   var x=40f
   tableWidths.forEachIndexed{idx,width->
    canvas.drawRect(x,top,x+width,top+tableHeaderHeight,headerFill)
    canvas.drawRect(x,top,x+width,top+tableHeaderHeight,gridPaint)
    tableLabels[idx].forEachIndexed{li,label->canvas.drawText(label,x+4f,top+12f+li*10f,tableHeaderPaint)}
    x+=width
   }
   y=top+tableHeaderHeight
  }
  fun wrapCell(raw:String,width:Float):List<String>{
   var remaining=raw.ifBlank{"—"}
   val result=mutableListOf<String>()
   while(remaining.isNotEmpty()){
    val count=tableCellPaint.breakText(remaining,true,(width-8f).coerceAtLeast(1f),null).coerceAtLeast(1)
    result.add(remaining.substring(0,count))
    remaining=remaining.substring(count).trimStart()
   }
   return result.ifEmpty{listOf("—")}
  }
  drawTableHeader()
  i.findings.forEachIndexed{n,f->
   val values=listOf((n+1).toString(),f.desc,f.severity,f.action,f.deadline,f.owner)
   val wrapped=values.mapIndexed{idx,value->wrapCell(value,tableWidths[idx])}
   val maxLines=wrapped.maxOf{it.size}
   var lineIndex=0
   while(lineIndex<maxLines){
    var available=((790f-y-8f)/12f).toInt()
    if(available<=0){newPage();drawTableHeader();available=((790f-y-8f)/12f).toInt()}
    val segmentLines=minOf(available,maxLines-lineIndex).coerceAtLeast(1)
    val rowHeight=segmentLines*12f+8f
    val top=y
    var x=40f
    tableWidths.forEachIndexed{idx,width->
     canvas.drawRect(x,top,x+width,top+rowHeight,cellFill)
     canvas.drawRect(x,top,x+width,top+rowHeight,gridPaint)
     for(li in 0 until segmentLines){
      val text=wrapped[idx].getOrNull(lineIndex+li)?:if(idx==0&&lineIndex>0)"" else ""
      if(text.isNotEmpty())canvas.drawText(text,x+4f,top+12f+li*12f,tableCellPaint)
     }
     x+=width
    }
    y=top+rowHeight
    lineIndex+=segmentLines
    if(lineIndex<maxLines){newPage();drawTableHeader()}
   }
   if(f.photo.isNotBlank()){ensureSpace(300f);line("Evidência fotográfica (recurso digital adicional):");image(f.photo,360f,250f)}
   y+=8f
  }
  line("VALIDAÇÃO DO RESPONSÁVEL PELA INSPEÇÃO",h)
  line("Matrícula: ${i.registration}")
  if(i.inspectorSignature.isNotBlank()){ensureSpace(150f);line("Assinatura / Matrícula do Responsável pela Inspeção:");image(i.inspectorSignature,260f,100f)}
  finishCurrent()
  val f=File(filesDir,"Inspecao_${safeFilePart(i.number)}_${safeFilePart(i.date)}_${safeFilePart(i.location)}.pdf")
  FileOutputStream(f).use{doc.writeTo(it)}
  doc.close()
  logo.recycle()
  return f
 }
 var photoIndex by remember{mutableIntStateOf(-1)}
 var responsible by remember{mutableStateOf("")};var number by remember{mutableStateOf("")};var date by remember{mutableStateOf("")};var location by remember{mutableStateOf("")};var findings by remember{mutableStateOf(listOf(Finding()))};var participants by remember{mutableStateOf(listOf(Participant()))};var inspectorSignature by remember{mutableStateOf("")};var registration by remember{mutableStateOf("")};var signatureTarget by remember{mutableStateOf<String?>(null)}
 val draftPrefs=remember{getSharedPreferences("ssma_draft",MODE_PRIVATE)}
 var draftExists by remember{mutableStateOf(draftPrefs.getString("inspectionDraft",null)!=null)}
 var showNewFormConfirm by remember{mutableStateOf(false)};var pendingHistoryEdit by remember{mutableStateOf<Inspection?>(null)}
 val gallery=rememberLauncherForActivityResult(ActivityResultContracts.GetContent()){uri->if(uri!=null&&photoIndex>=0&&photoIndex<findings.size){val extension=normalizedPhotoExtension(contentResolver.getType(uri)?.let{android.webkit.MimeTypeMap.getSingleton().getExtensionFromMimeType(it)});val stored=File(filesDir,"photo_gallery_${System.currentTimeMillis()}.$extension");try{val input=contentResolver.openInputStream(uri)?:throw java.io.IOException("Não foi possível abrir a imagem selecionada");input.use{stream->FileOutputStream(stored).use{output->stream.copyTo(output)}};if(stored.length()<=0L)throw java.io.IOException("A imagem importada está vazia");findings=findings.toMutableList().also{list->list[photoIndex]=list[photoIndex].copy(photo=Uri.fromFile(stored).toString())}}catch(e:Exception){stored.delete();toast("Falha ao salvar foto: ${e.message}")}}}
 var pendingCameraFile by remember{mutableStateOf<File?>(null)}
 val camera=rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()){success->val f=pendingCameraFile;if(success&&f!=null&&photoIndex in findings.indices){findings=findings.toMutableList().also{list->list[photoIndex]=list[photoIndex].copy(photo=Uri.fromFile(f).toString())}}else f?.delete();pendingCameraFile=null}
 
 var filterStart by remember{mutableStateOf("")};var filterEnd by remember{mutableStateOf("")};var filterText by remember{mutableStateOf("")};var selected by remember{mutableStateOf(setOf<Int>())};var filterStartDialog by remember{mutableStateOf(false)};var filterEndDialog by remember{mutableStateOf(false)}
 fun startForm(ins:Inspection?){editing=ins;responsible=ins?.responsible?: "";number=ins?.number?:db.nextNumber();date=ins?.date?: "";location=ins?.location?: "";findings=ins?.findings?:listOf(Finding());participants=ins?.participants?:listOf(Participant());inspectorSignature=ins?.inspectorSignature?: "";registration=ins?.registration?: "";screen="form"}
 fun resumeDraft(){
  val raw=draftPrefs.getString("inspectionDraft",null)?:return
  try{
   val obj=JSONObject(raw)
   val draftId=obj.optInt("id",0)
   editing=if(draftId>0)db.list().firstOrNull{it.id==draftId}else null
   responsible=obj.optString("responsible")
   number=obj.optString("number")
   date=obj.optString("date")
   location=obj.optString("location")
   findings=db.decode(obj.optString("findings","[]"))
   participants=db.decodeParticipants(obj.optString("participants","[]"))
   inspectorSignature=obj.optString("inspectorSignature")
   registration=obj.optString("registration")
   signatureTarget=null
   screen="form"
  }catch(e:Exception){toast("Não foi possível recuperar o rascunho: ${e.message}")}
 }
 LaunchedEffect(screen,responsible,number,date,location,findings,participants,inspectorSignature,registration,editing?.id){
  if(screen=="form"){
   val draft=JSONObject().put("id",editing?.id?:0).put("responsible",responsible).put("number",number).put("date",date).put("location",location).put("findings",db.encode(findings)).put("participants",db.encodeParticipants(participants)).put("inspectorSignature",inspectorSignature).put("registration",registration)
   draftPrefs.edit().putString("inspectionDraft",draft.toString()).apply()
   draftExists=true
  }
 }
 BackHandler(enabled=screen!="home"){screen="home"}
 if(dateDialog){val state=rememberDatePickerState(initialSelectedDateMillis=dateTextToPickerMillis(date));DatePickerDialog(onDismissRequest={dateDialog=false},confirmButton={TextButton(onClick={state.selectedDateMillis?.let{ms->val formatted=datePickerMillisToText(ms);Log.i("SSMA_DATE_TRACE","campo=DATA rawMillis=$ms utcFormatted=$formatted localDate=${java.util.Date(ms)} timezone=${java.util.TimeZone.getDefault().id}");date=formatted};dateDialog=false}){Text("Confirmar")}},dismissButton={TextButton(onClick={dateDialog=false}){Text("Cancelar")}}){DatePicker(state)}}
 if(deadlineDialogIndex>=0){val idx=deadlineDialogIndex;val current=findings.getOrNull(idx)?.deadline?: "";val initialMillis=try{dateTextToPickerMillis(current)}catch(_:Exception){null};val state=rememberDatePickerState(initialSelectedDateMillis=initialMillis);DatePickerDialog(onDismissRequest={deadlineDialogIndex=-1},confirmButton={TextButton(onClick={state.selectedDateMillis?.let{ms->val formatted=datePickerMillisToText(ms);Log.i("SSMA_DATE_TRACE","campo=PRAZO indice=$idx rawMillis=$ms utcFormatted=$formatted localDate=${java.util.Date(ms)} timezone=${java.util.TimeZone.getDefault().id}");findings=findings.toMutableList().also{list->list[idx]=list[idx].copy(deadline=formatted)} };deadlineDialogIndex=-1}){Text("Confirmar")}},dismissButton={TextButton(onClick={deadlineDialogIndex=-1}){Text("Cancelar")}}){DatePicker(state)}}
 if(filterStartDialog){val state=rememberDatePickerState(initialSelectedDateMillis=dateTextToPickerMillis(filterStart));DatePickerDialog(onDismissRequest={filterStartDialog=false},confirmButton={TextButton(onClick={state.selectedDateMillis?.let{filterStart=datePickerMillisToText(it)};filterStartDialog=false}){Text("Confirmar")}},dismissButton={TextButton(onClick={filterStartDialog=false}){Text("Cancelar")}}){DatePicker(state)}}
 if(filterEndDialog){val state=rememberDatePickerState(initialSelectedDateMillis=dateTextToPickerMillis(filterEnd));DatePickerDialog(onDismissRequest={filterEndDialog=false},confirmButton={TextButton(onClick={state.selectedDateMillis?.let{filterEnd=datePickerMillisToText(it)};filterEndDialog=false}){Text("Confirmar")}},dismissButton={TextButton(onClick={filterEndDialog=false}){Text("Cancelar")}}){DatePicker(state)}}
 Column(Modifier.fillMaxSize().imePadding().navigationBarsPadding().padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
 when(screen){
 "home"->Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(18.dp)){
 Card(colors=CardDefaults.cardColors(containerColor=Color(0xFFEAF6FC)),modifier=Modifier.fillMaxWidth(),shape=RoundedCornerShape(20.dp)){Column(Modifier.fillMaxWidth().padding(20.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(12.dp)){val logoBitmap=remember { assets.open(LOGO_ASSET).use { BitmapFactory.decodeStream(it) } };Image(bitmap=logoBitmap.asImageBitmap(),contentDescription="Logo Salum Construções",modifier=Modifier.fillMaxWidth().height(132.dp),contentScale=ContentScale.Fit);Text("REGISTRO DE INSPEÇÃO DE SSMA",style=MaterialTheme.typography.titleLarge,color=Color(0xFF252D87),textAlign=androidx.compose.ui.text.style.TextAlign.Center,modifier=Modifier.fillMaxWidth());Text("F-SSMA-0073 • Rev.00",style=MaterialTheme.typography.bodyMedium);Text("Versão do aplicativo: $APP_VERSION",style=MaterialTheme.typography.labelMedium)}}
 Button(onClick={if(draftExists)showNewFormConfirm=true else startForm(null)},modifier=Modifier.fillMaxWidth().height(60.dp)){Text("NOVO REGISTRO")}
 if(draftExists)OutlinedButton(onClick={resumeDraft()},modifier=Modifier.fillMaxWidth().height(60.dp)){Text("RETOMAR RASCUNHO")}
 OutlinedButton(onClick={screen="history"},modifier=Modifier.fillMaxWidth().height(60.dp)){Text("CONSULTAR INSPEÇÕES")}
 if(showNewFormConfirm){AlertDialog(onDismissRequest={showNewFormConfirm=false},title={Text("Novo registro")},text={Text("Existe um rascunho salvo. Iniciar uma nova inspeção descartará o rascunho atual.")},confirmButton={TextButton(onClick={draftPrefs.edit().remove("inspectionDraft").apply();draftExists=false;showNewFormConfirm=false;startForm(null)}){Text("Descartar e iniciar")}},dismissButton={TextButton(onClick={showNewFormConfirm=false}){Text("Cancelar")}})}
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
 findings.forEachIndexed{idx,f->Card{Column(Modifier.padding(12.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){Row(verticalAlignment= androidx.compose.ui.Alignment.CenterVertically){Text("Desvio #${idx+1}",style=MaterialTheme.typography.titleMedium);Spacer(Modifier.weight(1f));if(findings.size>1)TextButton(onClick={findings=findings.filterIndexed{j,_->j!=idx};toast("Desvio excluído")}){Text("Excluir")}};OutlinedTextField(f.desc,{value->findings=findings.toMutableList().also{list->list[idx]=f.copy(desc=value)}},label={Text("Descrição (obrigatória)")},modifier=Modifier.fillMaxWidth(),minLines=2);var expanded by remember{mutableStateOf(false)};Box{OutlinedButton(onClick={expanded=true}){Text("Severidade: ${f.severity}")};DropdownMenu(expanded,{expanded=false}){listOf("LEVE","MODERADA","GRAVE","CRÍTICO").forEach{s->DropdownMenuItem(text={Text(s)},onClick={findings=findings.toMutableList().also{it[idx]=f.copy(severity=s)};expanded=false})}}};Text(severityDescription(f.severity),style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant);OutlinedTextField(f.action,{value->findings=findings.toMutableList().also{list->list[idx]=f.copy(action=value)}},label={Text("Ação para adequação/correção")},modifier=Modifier.fillMaxWidth());OutlinedTextField(f.deadline,{value->findings=findings.toMutableList().also{list->list[idx]=f.copy(deadline=value)}},label={Text("Prazo de execução")},placeholder={if(f.deadline.isBlank())Text("Clique no calendário para inserir a data")},modifier=Modifier.fillMaxWidth(),readOnly=true,trailingIcon={IconButton(onClick={deadlineDialogIndex=idx}){Text("📅")}});OutlinedTextField(f.owner,{value->findings=findings.toMutableList().also{list->list[idx]=f.copy(owner=value)}},label={Text("Responsável")},modifier=Modifier.fillMaxWidth());Row{Button(onClick={photoIndex=idx;gallery.launch("image/*")}){Text("Galeria")};Button(onClick={photoIndex=idx;val file=File(filesDir,"photo_camera_${System.currentTimeMillis()}.jpg");val uri=FileProvider.getUriForFile(this@MainActivity,"br.com.ssma.inspecoes.fileprovider",file);pendingCameraFile=file;camera.launch(uri)}){Text("Câmera")};TextButton(onClick={findings=findings.toMutableList().also{it[idx]=f.copy(photo="")}}){Text("Remover foto")}};if(f.photo.isNotBlank()){val bm=remember(f.photo){loadBitmap(this@MainActivity, Uri.parse(f.photo))};bm?.let{Image(it.asImageBitmap(),null,Modifier.fillMaxWidth().height(160.dp))}}}}}
 Button(onClick={findings=findings+Finding()},modifier=Modifier.fillMaxWidth()){Text("Adicionar desvio")}

 if(signatureTarget!=null){SignaturePadDialog(onDismiss={signatureTarget=null},onSave={path->val target=signatureTarget;if(target=="inspector")inspectorSignature=path else if(target?.startsWith("participant:")==true){val idx=target.substringAfter(":").toIntOrNull();if(idx!=null&&idx in participants.indices)participants=participants.toMutableList().also{it[idx]=it[idx].copy(signature=path)} };signatureTarget=null})}
 Button(onClick={val validation=validateInspectionFields(responsible,date,location,findings);if(validation!=null){toast(validation)}else{val record=Inspection(editing?.id?:0,responsible,number,date,location,findings,participants=participants,inspectorSignature=inspectorSignature,registration=registration);val file=pdf(record);val saved=record.copy(pdf=file.absolutePath);if(editing==null)db.save(saved) else db.update(saved);toast("Registro salvo");draftPrefs.edit().remove("inspectionDraft").apply();draftExists=false;refresh++;shareFiles(listOf(file));screen="home"}},modifier=Modifier.fillMaxWidth()){Text("Salvar e exportar PDF")}
 }
 "history"->Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(10.dp)){
 Row{TextButton(onClick={screen="home"}){Text("← Início")};Text("Inspeções realizadas",style=MaterialTheme.typography.titleLarge)}
 OutlinedTextField(filterText,{filterText=it},label={Text("Buscar número, local ou responsável")},modifier=Modifier.fillMaxWidth());OutlinedTextField(filterStart,{filterStart=it},label={Text("Data inicial (dd/mm/aaaa)")},modifier=Modifier.fillMaxWidth(),readOnly=true,trailingIcon={IconButton(onClick={filterStartDialog=true}){Text("📅")}});OutlinedTextField(filterEnd,{filterEnd=it},label={Text("Data final (dd/mm/aaaa)")},modifier=Modifier.fillMaxWidth(),readOnly=true,trailingIcon={IconButton(onClick={filterEndDialog=true}){Text("📅")}});Row{TextButton(onClick={filterStart=""}){Text("Limpar início")};TextButton(onClick={filterEnd=""}){Text("Limpar fim")}}

 val startKey=if(filterStart.isBlank())0L else dateKey(filterStart);val endKey=if(filterEnd.isBlank())Long.MAX_VALUE else dateKey(filterEnd)
 val invalidStart=filterStart.isNotBlank()&&startKey==0L;val invalidEnd=filterEnd.isNotBlank()&&endKey==0L;val invalidOrder=!invalidStart&&!invalidEnd&&filterStart.isNotBlank()&&filterEnd.isNotBlank()&&startKey>endKey
 if(invalidStart||invalidEnd||invalidOrder)Text(if(invalidOrder)"A data inicial não pode ser posterior à data final." else "Selecione datas válidas no calendário.",color=MaterialTheme.colorScheme.error)
 val query=filterText.trim().lowercase();val records=if(invalidStart||invalidEnd||invalidOrder)emptyList() else db.list().filter{val k=dateKey(it.date);(filterStart.isBlank()||k>=startKey)&&(filterEnd.isBlank()||k<=endKey)&&(query.isBlank()||it.number.lowercase().contains(query)||it.location.lowercase().contains(query)||it.responsible.lowercase().contains(query))}.sortedByDescending{dateKey(it.date)}
 Row{Checkbox(checked=records.isNotEmpty()&&records.all{it.id in selected},onCheckedChange={selected=if(it)selected+records.map{r->r.id}else selected-records.map{r->r.id}});Text("Selecionar todas")}
 if(selected.isNotEmpty())Button(onClick={
  val chosen=db.list().filter{it.id in selected}
  val z=File(cacheDir,"Inspecoes_${System.currentTimeMillis()}.zip")
  var evidenceExpected=0
  var evidenceIncluded=0
  var evidenceMissing=0
  fun stageUri(raw:String):File?{
   var staged:File?=null
   try{
    val temp=File.createTempFile("ssma_evidence_",".tmp",cacheDir)
    staged=temp
    val uri=Uri.parse(raw)
    val input=if(uri.scheme=="file")uri.path?.let{File(it).takeIf{file->file.exists()&&file.isFile}?.inputStream()} else contentResolver.openInputStream(uri)
    if(input==null){temp.delete();return null}
    input.use{source->temp.outputStream().use{output->source.copyTo(output,8192)}}
    if(temp.length()<=0L){temp.delete();return null}
    return temp
   }catch(_:Exception){staged?.delete();return null}
  }
  fun sha256(file:File):String{
   val digest=MessageDigest.getInstance("SHA-256")
   file.inputStream().buffered().use{input->
    val buffer=ByteArray(8192)
    while(true){val count=input.read(buffer);if(count<0)break;digest.update(buffer,0,count)}
   }
   return digest.digest().joinToString(""){byte->"%02x".format(byte)}
  }
  try{
   ZipOutputStream(FileOutputStream(z)).use{zip->
    chosen.forEach{r->
     val base=safeFilePart("${r.number.ifBlank{r.id.toString()}}_ID_${r.id}")
     val pdfFile=File(r.pdf).takeIf{it.exists()}?:pdf(r)
     zip.putNextEntry(ZipEntry("$base/Inspecao.pdf"));pdfFile.inputStream().use{it.copyTo(zip)};zip.closeEntry()
     val evidence=collectInspectionEvidence(r)
     evidenceExpected+=evidence.size
     val outcomes=evidence.map{item->
      val staged=stageUri(item.sourceUri)
      if(staged==null)ExportEvidenceStatus(item,false)
      else try{
       val bytes=staged.length()
       val checksum=sha256(staged)
       zip.putNextEntry(ZipEntry("$base/${item.archiveFileName}"))
       staged.inputStream().use{it.copyTo(zip,8192)}
       zip.closeEntry()
       ExportEvidenceStatus(item,true,bytes,checksum)
      }finally{staged.delete()}
     }
     evidenceIncluded+=outcomes.count{it.included}
     evidenceMissing+=outcomes.count{!it.included}
     zip.putNextEntry(ZipEntry("$base/Manifest_Evidencias.txt"))
     zip.write(buildEvidenceManifest(r.number,r.date,r.location,outcomes).toByteArray(Charsets.UTF_8))
     zip.closeEntry()
    }
   }
   shareFiles(listOf(z),true)
   toast(if(evidenceMissing>0) "ZIP exportado: $evidenceIncluded/$evidenceExpected evidências incluídas. Consulte o Manifesto." else if(evidenceExpected==0) "ZIP exportado. Nenhuma evidência adicional registrada." else "ZIP exportado: todas $evidenceIncluded evidências incluídas.")
   selected=setOf()
  }catch(e:Exception){
   z.delete()
   toast("Falha ao gerar ZIP: ${e.message}")
  }
 },modifier=Modifier.fillMaxWidth()){Text("Exportar selecionadas em ZIP (PDF + evidências)")}
 if(pendingHistoryEdit!=null){val target=pendingHistoryEdit!!;AlertDialog(onDismissRequest={pendingHistoryEdit=null},title={Text("Rascunho em andamento")},text={Text("Há um rascunho não concluído. Para editar esta inspeção, será necessário descartá-lo. Você pode retomar o rascunho em vez de descartá-lo.")},confirmButton={TextButton(onClick={draftPrefs.edit().remove("inspectionDraft").apply();draftExists=false;pendingHistoryEdit=null;startForm(target)}){Text("Descartar e editar")}},dismissButton={TextButton(onClick={pendingHistoryEdit=null;resumeDraft()}){Text("Retomar rascunho")}})}
 records.forEach{r->Card(Modifier.fillMaxWidth()){Column(Modifier.padding(12.dp),verticalArrangement=Arrangement.spacedBy(6.dp)){Row{Checkbox(checked=r.id in selected,onCheckedChange={selected=if(it)selected+r.id else selected-r.id});Column{Text("Inspeção ${r.number.ifBlank{"#${r.id}"}}",style=MaterialTheme.typography.titleMedium);Text("${r.date} • ${r.location}");Text("${r.responsible}")}};Row{Button(onClick={if(draftExists)pendingHistoryEdit=r else startForm(r)}){Text("Editar")};Button(onClick={val f=File(r.pdf);shareFiles(listOf(if(f.exists())f else pdf(r)))}){Text("PDF")}}}}}
 }
 }
 }
 }
 }
 }
 }
 
private fun severityDescription(value:String):String=when(value){
 "LEVE"->"Condição de risco/desvio que não gera transtornos à saúde e ocasiona acidentes que demandam somente primeiros socorros ou impacto ambiental não significativo."
 "MODERADA"->"Condição de risco/desvio que gera transtornos à saúde e ocasiona acidentes sem afastamento (com restrição médica) ou impacto ambiental que afeta ecossistemas comuns."
 "GRAVE"->"Condição de risco/desvio que gera transtornos à saúde e ocasiona acidentes com afastamento ou até uma fatalidade; impacto ambiental que afeta ecossistemas com espécies raras e afeta a comunidade."
 "CRÍTICO"->"Evento não planejado com liberação de energia que poderia resultar em mais de uma fatalidade ou lesão incapacitante permanente, dada ligeira mudança no tempo, posição ou atuação dos controles."
 else->"Descrição não disponível."
}
private fun safeFilePart(raw:String):String = raw.trim().replace(Regex("[^A-Za-z0-9_-]"),"_").trim('_').ifBlank{"sem_dado"}
private fun loadBitmap(context:android.content.Context, uri:Uri, reqWidth:Int=1024, reqHeight:Int=1024):Bitmap? {
  fun openStream():java.io.InputStream? {
    if(uri.scheme=="file"){ val path=uri.path ?: return null; return File(path).inputStream() }
    return context.contentResolver.openInputStream(uri)
  }
  return try {
   val bounds=BitmapFactory.Options().apply{inJustDecodeBounds=true}
   openStream()?.use{BitmapFactory.decodeStream(it,null,bounds)}
   if(bounds.outWidth<=0||bounds.outHeight<=0)return null
   var sample=1
   while(bounds.outWidth/sample>reqWidth.coerceAtLeast(1)||bounds.outHeight/sample>reqHeight.coerceAtLeast(1))sample*=2
   val options=BitmapFactory.Options().apply{inSampleSize=sample}
   val decoded=openStream()?.use{BitmapFactory.decodeStream(it,null,options)}?:return null
   val orientation=try{openStream()?.use{ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL)}?:ExifInterface.ORIENTATION_NORMAL}catch(_:Exception){ExifInterface.ORIENTATION_NORMAL}
   val matrix=Matrix()
   when(orientation){
    ExifInterface.ORIENTATION_FLIP_HORIZONTAL->matrix.setScale(-1f,1f)
    ExifInterface.ORIENTATION_ROTATE_180->matrix.setRotate(180f)
    ExifInterface.ORIENTATION_FLIP_VERTICAL->matrix.setScale(1f,-1f)
    ExifInterface.ORIENTATION_TRANSPOSE->{matrix.setRotate(90f);matrix.postScale(-1f,1f)}
    ExifInterface.ORIENTATION_ROTATE_90->matrix.setRotate(90f)
    ExifInterface.ORIENTATION_TRANSVERSE->{matrix.setRotate(-90f);matrix.postScale(-1f,1f)}
    ExifInterface.ORIENTATION_ROTATE_270->matrix.setRotate(-90f)
   }
   if(matrix.isIdentity)decoded else Bitmap.createBitmap(decoded,0,0,decoded.width,decoded.height,matrix,true).also{if(it!==decoded)decoded.recycle()}
  }catch(_:Exception){null}
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
