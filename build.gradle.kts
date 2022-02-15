plugins {
    val kotlinVersion = "1.4.30"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.6.6"
}

group = "com.jasonzyt.mirai.githubbot"
version = "0.1.0"

repositories {
    mavenLocal()
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/com.github.kevinsawicki/http-request
    implementation("com.github.kevinsawicki:http-request:6.0")
    // https://mvnrepository.com/artifact/org.rapidoid/rapidoid-http
    implementation("org.rapidoid:rapidoid-quick:5.5.5")
    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation("com.google.code.gson:gson:2.9.0")
}
