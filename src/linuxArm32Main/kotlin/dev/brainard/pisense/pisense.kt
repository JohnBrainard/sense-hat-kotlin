package dev.brainard.pisense

import kotlinx.cinterop.*
import platform.linux.*
import platform.posix.*

class PiSenseDeviceException(message:String) : Exception(message)

@ExperimentalUnsignedTypes
fun locateDevicePath():String? =    memScoped {
    val entries = alloc<glob_t>()

    when(glob("/sys/class/graphics/fb*", 0, staticCFunction(::globErrFn), entries.ptr)) {
        GLOB_NOSPACE -> throw OutOfMemoryError()
        GLOB_ABORTED -> throw PiSenseDeviceException("error locating framebuffer device: GLOB_ABORTED")
        GLOB_NOMATCH -> throw PiSenseDeviceException("error locating framebuffer device: Please ensure Pi Sense Library is installed")

        else -> {
            for (i in 0..entries.gl_pathc.toInt()) {
                val path = entries.gl_pathv?.get(i)?.toKString()
                if (path != null && isPiSenseDevice(path))
                    return "/dev/${path.substringAfterLast("/")}"
            }
        }
    }

    return null
}

@ExperimentalUnsignedTypes
private fun isPiSenseDevice(path:String):Boolean = memScoped {
    val result = alloc<stat>()
    if(stat("$path/name", result.ptr) != 0)
        return false

    if (!result.isRegularFile())
        return false

    val fd = fopen("$path/name", "r")
    if (fd == NULL)
        return false

    val buf = allocArray<ByteVar>(1024)
    fgets(buf,  1024, fd)

    if (buf.toKString().trim() == "RPi-Sense FB")
        return true

    return false
}

private fun globErrFn(byteVar:CPointer<ByteVar>?, intVar:Int):Int = 0

@ExperimentalUnsignedTypes
private fun stat.isRegularFile():Boolean = st_mode.and(S_IFMT.toUInt()) == S_IFREG.toUInt()