package br.com.ssma.inspecoes

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DateUtilsTest {
    @Test fun parsesAndFormatsValidBrazilianDateInUtc() {
        val millis = dateTextToPickerMillis("25/11/2026")
        assertTrue(millis != null)
        assertEquals("25/11/2026", datePickerMillisToText(millis!!))
    }

    @Test fun rejectsInvalidDatesAndTrailingText() {
        assertNull(dateTextToPickerMillis("31/02/2026"))
        assertNull(dateTextToPickerMillis("25/11/2026abc"))
        assertNull(dateTextToPickerMillis("5/11/2026"))
        assertNull(dateTextToSortableKey("31/02/2026"))
    }

    @Test fun rejectsInvalidInspectionFields() {
        val validFinding = listOf(Finding(desc = "Desvio identificado"))
        assertEquals("Informe o responsável pela inspeção.", validateInspectionFields("", "25/11/2026", "Local", validFinding))
        assertEquals("Selecione uma data válida para a inspeção.", validateInspectionFields("Responsável", "31/02/2026", "Local", validFinding))
        assertEquals("Informe o local da inspeção.", validateInspectionFields("Responsável", "25/11/2026", "", validFinding))
        assertEquals("Preencha a descrição de todos os desvios.", validateInspectionFields("Responsável", "25/11/2026", "Local", listOf(Finding())))
        assertNull(validateInspectionFields("Responsável", "25/11/2026", "Local", validFinding))
    }

    @Test fun acceptsLeapDayAndRejectsNonLeapDay() {
        assertTrue(dateTextToPickerMillis("29/02/2024") != null)
        assertNull(dateTextToPickerMillis("29/02/2025"))
    }

    @Test fun sortableKeysPreserveCalendarOrder() {
        val earlier = dateTextToSortableKey("02/01/2026")
        val later = dateTextToSortableKey("10/01/2026")
        assertTrue(earlier != null && later != null)
        assertTrue(earlier!! < later!!)
        assertEquals(20260102L, earlier)
    }
}
