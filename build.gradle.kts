plugins {
    kotlin("jvm") version "1.5.10"
    java
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://papermc.io/repo/repository/maven-public/")
    maven(url = "https://repo.dmulloy2.net/repository/public/" )
}

dependencies {
    implementation(kotlin("stdlib"))
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")
    compileOnly("org.github.paperspigot:paperspigot-api:1.8.8-R0.1-SNAPSHOT")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}