package pisense

import dev.brainard.pisense.Pixel
import dev.brainard.pisense.SenseHat
import dev.brainard.pisense.locateDevicePath
import platform.posix.*

val MILLISECOND:useconds_t = 1000u
val SECOND:useconds_t = MILLISECOND * 1000u

@ExperimentalUnsignedTypes
fun main() {
    val fbDevicePath = locateDevicePath()
    if (fbDevicePath == null) {
        fprintf(stderr, "unable to locate framebuffer device\n")
        exit(1)
        return
    }
    fprintf(stderr, "framebuffer device: $fbDevicePath\n")

    val senseHat = SenseHat(fbDevicePath)

    val pixelsA = (1..64).map { Pixel(0, 255, 255) }
    val pixelsB = (1..64).map { Pixel(255, 0, 255) }
//    while (true) {
//        senseHat.setPixels(pixelsA)
//        sleep(1)
//
//        senseHat.setPixels(pixelsB)
//        sleep(1)
//    }

    val delay = MILLISECOND * 3u
    while (true) {
        for(i in 0..63) {
            senseHat.setPixel(i, Pixel(0, 255, 255))
            usleep(delay)
        }

        for(i in 0..63) {
            senseHat.setPixel(i, Pixel(255, 0, 255))
            usleep(delay)
        }
    }
}
