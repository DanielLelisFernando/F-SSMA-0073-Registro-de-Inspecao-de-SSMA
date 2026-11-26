package br.com.ssma.inspecoes

data class ExportEvidenceItem(
    val displayName: String,
    val archiveFileName: String,
    val sourceUri: String
)

data class ExportEvidenceStatus(
    val item: ExportEvidenceItem,
    val included: Boolean
)

fun collectInspectionEvidence(inspection: Inspection): List<ExportEvidenceItem> {
    val items = mutableListOf<ExportEvidenceItem>()
    inspection.findings.forEachIndexed { index, finding ->
        if (finding.photo.isNotBlank()) {
            items += ExportEvidenceItem(
                displayName = "Foto do desvio #${index + 1}",
                archiveFileName = "Foto_Desvio_${index + 1}.jpg",
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
                appendLine("$label ${status.item.displayName} | ${status.item.archiveFileName}")
            }
        }
        appendLine()
        appendLine("Evidências ausentes não foram copiadas ao ZIP.")
        appendLine("Verifique a inspeção no aplicativo e reexporte se necessário.")
    }
}
