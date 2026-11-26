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

    @Test fun sortableKeysPreserveCalendarOrder() {
        val earlier = dateTextToSortableKey("02/01/2026")
        val later = dateTextToSortableKey("10/01/2026")
        assertTrue(earlier != null && later != null)
        assertTrue(earlier!! < later!!)
        assertEquals(20260102L, earlier)
    }
}
