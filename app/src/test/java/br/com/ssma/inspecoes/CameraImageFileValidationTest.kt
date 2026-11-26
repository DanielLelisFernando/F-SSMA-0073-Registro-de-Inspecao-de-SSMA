package br.com.ssma.inspecoes

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CameraImageFileValidationTest {
    private fun withTempFile(bytes: ByteArray, test: (File) -> Unit) {
        val file = File.createTempFile("camera-validation", ".jpg")
        try {
            file.writeBytes(bytes)
            test(file)
        } finally {
            file.delete()
        }
    }

    @Test
    fun acceptsJpegSignature() {
        withTempFile(byteArrayOf(0xff.toByte(), 0xd8.toByte(), 0xff.toByte(), 0xe0.toByte())) {
            assertTrue(isValidCameraImageFile(it))
        }
    }

    @Test
    fun rejectsEmptyAndNonJpegFiles() {
        withTempFile(byteArrayOf()) { assertFalse(isValidCameraImageFile(it)) }
        withTempFile("PNG data".toByteArray(Charsets.US_ASCII)) {
            assertFalse(isValidCameraImageFile(it))
        }
        withTempFile("not an image".toByteArray(Charsets.US_ASCII)) {
            assertFalse(isValidCameraImageFile(it))
        }
    }

    @Test
    fun rejectsMissingFileAndNull() {
        assertFalse(isValidCameraImageFile(null))
        val missing = File.createTempFile("camera-validation", ".jpg")
        missing.delete()
        assertFalse(isValidCameraImageFile(missing))
    }
}
