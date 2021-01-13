import io.kotless.plugin.gradle.dsl.kotless
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


group = "com.ak"
version = "0.0.1"

plugins {
    // 1.4 is not supported yet by kotless :(
    kotlin("jvm") version "1.3.72" apply true
    id("io.kotless") version "0.1.6" apply true
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.kotless", "ktor-lang", "0.1.6")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.2")
    implementation("com.amazonaws:amazon-kinesis-client:1.14.0")

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.withType<JavaExec> {
    jvmArgs = listOf(
            "-Xdebug",
            "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5006"
            )
}

kotless {
    config {
        bucket = "kotless.bucket"

        terraform {
            profile = "kotless"
            region = "eu-central-1"
        }
    }

    webapp {
        lambda {
            kotless {
                packages = setOf("com.ak.biesdorf")
            }
        }
    }

    extensions {
        terraform {
            files {
                add(file("src/main/tf/extensions.tf"))
            }
        }
    }
}