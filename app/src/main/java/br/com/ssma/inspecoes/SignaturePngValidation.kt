package br.com.ssma.inspecoes

import java.io.File

private val pngSignature = byteArrayOf(
    0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
)

internal fun hasValidPngFile(file: File): Boolean {
    if (!file.isFile || file.length() < 33L) return false
    return file.inputStream().use { input ->
        val header = ByteArray(pngSignature.size)
        var offset = 0
        while (offset < header.size) {
            val count = input.read(header, offset, header.size - offset)
            if (count <= 0) return@use false
            offset += count
        }
        header.contentEquals(pngSignature)
    }
}
