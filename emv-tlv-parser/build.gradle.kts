
import org.gradle.api.publish.maven.MavenPublication

plugins {
    alias(libs.plugins.kotlinJvm)
    id("maven-publish")
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    testImplementation(libs.junit)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            groupId = "com.github.omar20alaa"
            artifactId = "emv-tlv-parser"
            version = "1.0.0"

            pom {
                name.set("EMV TLV Parser")
                description.set(
                    "A lightweight Kotlin library for parsing EMV TLV data."
                )
            }
        }
    }
}