plugins {
    val kotlinVersion = "1.6.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.10.0"
}

group = "com.jasonzyt.mirai.githubbot"
version = "0.1.0"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    // Only required if using EAP releases
    maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation("com.google.code.gson:gson:2.9.0")
    compileOnly("xyz.cssxsh.mirai:mirai-selenium-plugin:2.0.8")
    implementation("io.ktor:ktor-server-core:2.0.0-beta-1")
    implementation("io.ktor:ktor-server-netty:2.0.0-beta-1")
    implementation("io.ktor:ktor-server-status-pages:2.0.0-beta-1")
    implementation("io.ktor:ktor-server-default-headers:2.0.0-beta-1")
}
