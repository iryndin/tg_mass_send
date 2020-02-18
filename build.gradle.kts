import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val coroutineVersion = "1.3.3"
val logbackVersion = "1.2.3"

val jUnitVersion = "5.4.2"
val spekVersion = "2.0.5"
val kluentVersion = "1.51"
val easyRandomVersion = "4.0.0"
val mockKVersion = "1.9.3"

plugins {
    val kotlinVersion = "1.3.61"

    application
    id("org.springframework.boot") version "2.2.4.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"

    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion

    kotlin("jvm") version "1.3.50"

}

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}

group = "com.personik.msgservice"
version = "0.0.1"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    implementation("io.github.microutils:kotlin-logging:1.7.8")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.squareup.okhttp3:okhttp:4.3.1")
    implementation("org.postgresql:postgresql:42.2.6")
    implementation("org.flywaydb:flyway-core:6.2.3")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:$jUnitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jUnitVersion")
    testImplementation("org.amshove.kluent:kluent:$kluentVersion")
    testImplementation("io.mockk:mockk:$mockKVersion")
    testImplementation("org.jeasy:easy-random-core:$easyRandomVersion")
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
}


tasks.test {

    useJUnitPlatform {
        includeEngines("junit-jupiter","spek2")
    }
}

tasks.withType<KotlinCompile> {

    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"

    kotlinOptions {
        jvmTarget = "1.8"
        apiVersion = "1.3"
        languageVersion = "1.3"
        allWarningsAsErrors = true
    }
}

tasks.wrapper {
    gradleVersion = "6.1.1"
}
