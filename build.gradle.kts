plugins {
    kotlin("multiplatform") version "1.3.72"
}

group = "dev.brainard"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

kotlin {
    /* Targets configuration omitted. 
    *  To find out how to configure the targets, please follow the link:
    *  https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#setting-up-targets */
    linuxArm32Hfp("linuxArm32") {
        binaries {
            executable {
                entryPoint = "pisense.main"
            }
        }
    }

    sourceSets {
        val linuxArm32Main by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-io-native:0.1.1")
            }
        }

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
    }
}
