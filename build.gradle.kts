import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
}

group = "com.n0n5ense"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jogamp.jogl:jogl-all-main:2.3.2")
    implementation("org.jogamp.gluegen:gluegen-rt-main:2.3.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.20")


    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}