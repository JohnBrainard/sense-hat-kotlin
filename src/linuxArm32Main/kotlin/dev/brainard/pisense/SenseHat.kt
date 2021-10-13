package dev.brainard.pisense

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.toCValues
import platform.posix.FILE
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fwrite

data class Pixel(val red:Int, val green:Int, val blue:Int)

fun Pixel.pack():Short {
    val r = red.shr(3).and(0x1F)
    val g = green.shr(2).and(0X2F)
    val b = blue.shr(3).and(0x1F)

    return (r.shl(11) + g.shl(5) + b).toShort()
}

class SenseHat (private val fbDevice:String) {

    private var pixels:MutableList<Pixel> = (1..64).map { Pixel(0, 0, 0) }.toMutableList()

    @ExperimentalUnsignedTypes
    fun setPixels(newPixels:List<Pixel>) {
        if (pixels.size != 64) throw PiSenseDeviceException("pixels must have 64 elements")
        this.pixels = newPixels.toMutableList()
        display()
    }

    fun setPixel(index:Int, pixel:Pixel) {
        if (index < 0 || index > 63) throw PiSenseDeviceException("pixel range must be 0-63")
        pixels[index] = pixel
        display()
    }

    private fun display() {
        withDevice {fd ->
            val values = pixels.map(Pixel::pack)
                    .toShortArray()
                    .toCValues()
            fwrite(values, 16.toUInt(), values.size.toUInt(), fd)
        }
    }

    private fun withDevice(block:(CPointer<FILE>)->Unit) {
        val fd:CPointer<FILE>? = fopen(fbDevice, "wb")
        try {
            if (fd == null)
                throw PiSenseDeviceException("framebuffer device cannot be opened")

            block(fd)
        } finally {
            fclose(fd)
        }
    }
}