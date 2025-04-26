plugins {
    id("org.jetbrains.kotlin.jvm") version "2.1.20"
    kotlin("plugin.serialization") version "2.0.21"
    application
}

group = "com.foenichs"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("net.dv8tion", "JDA", "5.3.0")

    implementation("club.minnced", "jda-ktx","0.12.0")

    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-json", "1.8.0")

    implementation("io.ktor", "ktor-client-core-jvm", "2.3.12")
    implementation("io.ktor", "ktor-client-cio", "2.3.12")

    implementation("org.slf4j", "slf4j-api", "2.0.16")
    implementation("org.slf4j", "slf4j-simple", "2.0.16")

    implementation("org.mariadb.jdbc", "mariadb-java-client", "3.5.2")

    implementation("org.yaml:snakeyaml:2.4")
}

application {
    mainClass.set("com.foenichs.ghastling.MainKt")
}