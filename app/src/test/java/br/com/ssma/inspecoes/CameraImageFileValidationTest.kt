package br.com.ssma.inspecoes

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CameraImageFileValidationTest {
    private fun withTempFile(bytes: ByteArray, test: (File) -> Unit) {
        val file = File.createTempFile("camera-validation", ".img")
        try {
            file.writeBytes(bytes)
            test(file)
        } finally {
            file.delete()
        }
    }

    @Test
    fun acceptsRecognizedImageSignatures() {
        val signatures = listOf(
            byteArrayOf(0xff.toByte(), 0xd8.toByte(), 0xff.toByte(), 0xe0.toByte()),
            byteArrayOf(0x89.toByte(), 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a),
            "RIFF0000WEBP".toByteArray(Charsets.US_ASCII),
            "GIF89a".toByteArray(Charsets.US_ASCII),
            "BM00".toByteArray(Charsets.US_ASCII),
            "0000ftypheic".toByteArray(Charsets.US_ASCII)
        )
        signatures.forEach { signature ->
            withTempFile(signature) { assertTrue(isValidCameraImageFile(it)) }
        }
    }

    @Test
    fun rejectsEmptyAndUnknownFiles() {
        withTempFile(byteArrayOf()) { assertFalse(isValidCameraImageFile(it)) }
        withTempFile("not an image".toByteArray(Charsets.US_ASCII)) {
            assertFalse(isValidCameraImageFile(it))
        }
    }

    @Test
    fun rejectsMissingFileAndNull() {
        assertFalse(isValidCameraImageFile(null))
        val missing = File.createTempFile("camera-validation", ".img")
        missing.delete()
        assertFalse(isValidCameraImageFile(missing))
    }
}
