package br.com.ssma.inspecoes

data class ExportEvidenceItem(
    val displayName: String,
    val archiveFileName: String,
    val sourceUri: String
)

fun normalizedPhotoExtension(candidate: String?): String {
    val extension = candidate?.trim()?.lowercase()
    return extension?.takeIf { it.matches(Regex("(?=.*[a-z])[a-z0-9]{1,8}")) } ?: "jpg"
}

fun photoEvidenceExtension(sourceUri: String): String {
    val path = try {
        java.net.URI(sourceUri).path ?: java.net.URI(sourceUri).rawPath.orEmpty()
    } catch (_: Exception) {
        sourceUri
    }
    val extension = java.io.File(path).extension.lowercase()
    return normalizedPhotoExtension(extension)
}

data class ExportEvidenceStatus(
    val item: ExportEvidenceItem,
    val included: Boolean,
    val bytesCopied: Long? = null,
    val sha256: String? = null
)

fun collectInspectionEvidence(inspection: Inspection): List<ExportEvidenceItem> {
    val items = mutableListOf<ExportEvidenceItem>()
    inspection.findings.forEachIndexed { index, finding ->
        if (finding.photo.isNotBlank()) {
            items += ExportEvidenceItem(
                displayName = "Foto do desvio #${index + 1}",
                archiveFileName = "Foto_Desvio_${index + 1}.${photoEvidenceExtension(finding.photo)}",
                sourceUri = finding.photo
            )
        }
    }
    inspection.participants.forEachIndexed { index, participant ->
        if (participant.signature.isNotBlank()) {
            items += ExportEvidenceItem(
                displayName = "Assinatura do participante #${index + 1}",
                archiveFileName = "Assinatura_Participante_${index + 1}.png",
                sourceUri = participant.signature
            )
        }
    }
    if (inspection.inspectorSignature.isNotBlank()) {
        items += ExportEvidenceItem(
            displayName = "Assinatura do responsável",
            archiveFileName = "Assinatura_Responsavel.png",
            sourceUri = inspection.inspectorSignature
        )
    }
    return items
}

fun buildEvidenceManifest(
    inspectionNumber: String,
    inspectionDate: String,
    location: String,
    statuses: List<ExportEvidenceStatus>
): String {
    val included = statuses.count { it.included }
    val missing = statuses.size - included
    return buildString {
        appendLine("SCL Inspeções - Manifesto de evidências")
        appendLine("Inspeção: ${inspectionNumber.ifBlank { "(sem número)" }}")
        appendLine("Data: ${inspectionDate.ifBlank { "(sem data)" }}")
        appendLine("Local: ${location.ifBlank { "(sem local)" }}")
        appendLine("Total esperado: ${statuses.size}")
        appendLine("Incluídas: $included")
        appendLine("Ausentes: $missing")
        appendLine()
        if (statuses.isEmpty()) {
            appendLine("Nenhuma evidência adicional foi registrada.")
        } else {
            statuses.forEach { status ->
                val label = if (status.included) "[INCLUÍDA]" else "[AUSENTE]"
                val integrity = if (status.included) {
                    " | bytes: ${status.bytesCopied ?: 0} | SHA-256: ${status.sha256 ?: "não calculado"}"
                } else {
                    " | não copiada"
                }
                appendLine("$label ${status.item.displayName} | ${status.item.archiveFileName}$integrity")
            }
        }
        appendLine()
        appendLine("Evidências ausentes não foram copiadas ao ZIP.")
        appendLine("Verifique a inspeção no aplicativo e reexporte se necessário.")
    }
}


fun isValidCameraImageFile(file: java.io.File?): Boolean {
    if (file == null || !file.exists() || !file.isFile || file.length() < 4L) return false
    val header = ByteArray(12)
    val read = try {
        file.inputStream().use { it.read(header) }
    } catch (_: Exception) {
        return false
    }
    if (read < 4) return false
    fun ascii(offset: Int, length: Int): String =
        String(header, offset, length.coerceAtMost(read - offset), Charsets.US_ASCII)
    val jpeg = (header[0].toInt() and 0xff) == 0xff &&
        (header[1].toInt() and 0xff) == 0xd8 && (header[2].toInt() and 0xff) == 0xff
    val png = read >= 8 && header.copyOfRange(0, 8).contentEquals(
        byteArrayOf(0x89.toByte(), 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a)
    )
    val webp = read >= 12 && ascii(0, 4) == "RIFF" && ascii(8, 4) == "WEBP"
    val gif = read >= 6 && (ascii(0, 6) == "GIF87a" || ascii(0, 6) == "GIF89a")
    val bmp = ascii(0, 2) == "BM"
    val heif = read >= 12 && ascii(4, 4) == "ftyp" &&
        ascii(8, 4) in setOf("heic", "heix", "hevc", "hevx", "mif1", "msf1")
    return jpeg || png || webp || gif || bmp || heif
}
