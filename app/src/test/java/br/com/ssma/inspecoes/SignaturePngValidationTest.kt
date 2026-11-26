package br.com.ssma.inspecoes

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class SignaturePngValidationTest {
    private val signature = byteArrayOf(
        0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
    )

    @Test fun acceptsPngHeaderAndMinimumLength() {
        val file = File.createTempFile("signature-valid", ".tmp")
        try {
            file.writeBytes(signature + ByteArray(25))
            assertTrue(hasValidPngFile(file))
        } finally {
            file.delete()
        }
    }

    @Test fun rejectsIncorrectHeader() {
        val file = File.createTempFile("signature-invalid", ".tmp")
        try {
            file.writeBytes(ByteArray(33))
            assertFalse(hasValidPngFile(file))
        } finally {
            file.delete()
        }
    }

    @Test fun rejectsTruncatedFile() {
        val file = File.createTempFile("signature-short", ".tmp")
        try {
            file.writeBytes(signature)
            assertFalse(hasValidPngFile(file))
        } finally {
            file.delete()
        }
    }
}
