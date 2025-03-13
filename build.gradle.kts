plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.6"
}

group = "com.github.officialdonut"
version = "0.0.1"

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://repo.papermc.io/repository/maven-public") }
    maven { url = uri("https://repo.skriptlang.org/releases") }
}

dependencies {
    compileOnly("com.github.officialdonut:SkProtobuf:0.0.1")
    compileOnly("com.github.SkriptLang:Skript:2.10.2")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")

    implementation("io.grpc:grpc-netty-shaded:1.71.0")
    implementation("io.grpc:grpc-stub:1.71.0")
    implementation("io.grpc:grpc-protobuf:1.71.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    mergeServiceFiles()
}
