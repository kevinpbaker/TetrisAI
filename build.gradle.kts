import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
    application
}
group = "ca.kevbot"
version = "1.0"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test-junit"))
    implementation(fileTree(mapOf("dir" to "\\lib\\koma", "include" to listOf("*.jar"))))
    implementation(fileTree(mapOf("dir" to "\\lib\\processing", "include" to listOf("*.jar"))))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
    implementation("com.google.code.gson:gson:2.9.0")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "16"
}
