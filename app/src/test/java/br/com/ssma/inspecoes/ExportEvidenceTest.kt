package br.com.ssma.inspecoes

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExportEvidenceTest {
    @Test
    fun collectorIncludesOnlyNonBlankPhotosAndSignatures() {
        val inspection = Inspection(
            id = 1,
            responsible = "Responsável",
            number = "SCL-0001",
            date = "26/11/2026",
            location = "Obra",
            findings = listOf(Finding(photo = "file:///foto1.jpg"), Finding()),
            participants = listOf(Participant("Ana", "file:///ana.png"), Participant("Bruno")),
            inspectorSignature = "file:///responsavel.png"
        )

        val evidence = collectInspectionEvidence(inspection)

        assertEquals(3, evidence.size)
        assertEquals("Foto_Desvio_1.jpg", evidence[0].archiveFileName)
        assertEquals("Assinatura_Participante_1.png", evidence[1].archiveFileName)
        assertEquals("Assinatura_Responsavel.png", evidence[2].archiveFileName)
    }

    @Test
    fun manifestReportsIncludedAndMissingEvidence() {
        val first = ExportEvidenceItem("Foto do desvio #1", "Foto_Desvio_1.jpg", "file:///foto.jpg")
        val second = ExportEvidenceItem("Assinatura do participante #1", "Assinatura_Participante_1.png", "file:///assinatura.png")
        val manifest = buildEvidenceManifest(
            "SCL-0001",
            "26/11/2026",
            "Obra",
            listOf(ExportEvidenceStatus(first, true), ExportEvidenceStatus(second, false))
        )

        assertTrue(manifest.contains("Incluídas: 1"))
        assertTrue(manifest.contains("Ausentes: 1"))
        assertTrue(manifest.contains("[INCLUÍDA] Foto do desvio #1"))
        assertTrue(manifest.contains("[AUSENTE] Assinatura do participante #1"))
        assertTrue(manifest.contains("não copiada"))
    }

    @Test
    fun manifestIncludesSizeAndSha256ForCopiedEvidence() {
        val item = ExportEvidenceItem("Foto do desvio #1", "Foto_Desvio_1.jpg", "file:///foto.jpg")
        val manifest = buildEvidenceManifest(
            "SCL-0001", "26/11/2026", "Obra",
            listOf(ExportEvidenceStatus(item, true, 2048L, "a".repeat(64)))
        )
        assertTrue(manifest.contains("bytes: 2048"))
        assertTrue(manifest.contains("SHA-256: " + "a".repeat(64)))
    }

    @Test
    fun emptyEvidenceManifestClearlyStatesNoAdditionalEvidence() {
        val manifest = buildEvidenceManifest("SCL-0001", "26/11/2026", "Obra", emptyList())
        assertEquals(0, manifest.lines().count { it.contains("[INCLUÍDA]") || it.contains("[AUSENTE]") })
        assertTrue(manifest.contains("Nenhuma evidência adicional foi registrada."))
    }
}
