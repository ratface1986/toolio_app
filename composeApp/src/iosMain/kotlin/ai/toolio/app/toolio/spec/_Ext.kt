package ai.toolio.app.toolio.spec

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
fun NSData.toByteArray(): ByteArray {
    val size = this.length.toInt()
    if (size == 0) return ByteArray(0)

    val byteArray = ByteArray(size)
    val buffer = this.bytes

    buffer?.let {
        byteArray.usePinned { pinned ->
            memcpy(pinned.addressOf(0), buffer, size.convert())
        }
    }

    return byteArray
}