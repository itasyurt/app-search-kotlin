plugins {
    java
    kotlin("jvm") version "1.3.72"
    jacoco
}

group = "org.itasyurt"
version = "0.1-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-allopen:1.3.72")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.+")
    implementation("org.apache.httpcomponents:httpclient:4.5.13")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation("io.mockk:mockk:1.10.2")
    testImplementation("org.mock-server:mockserver-netty:3.10.8") {
        exclude(group = "junit")
    }
    testImplementation("org.mock-server:mockserver-client-java:3.10.8")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.6.3")
    testImplementation("org.junit.vintage:junit-vintage-engine:5.6.3")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
}


val jacocoReport = tasks.withType<JacocoReport> {
    reports {
        csv.isEnabled = true
        xml.isEnabled = false
        html.destination = file("$buildDir/jacocoHtml")
    }

    afterEvaluate {
        classDirectories.setFrom(files(classDirectories.files.map {
            fileTree(it) {
            }
        }))
    }
}


val coverage = tasks.withType<JacocoCoverageVerification> {

    violationRules {
        rule {
            element = "BUNDLE"

            limit {
                minimum = 0.7.toBigDecimal()
            }

            excludes = listOf(
            )
        }
    }

    afterEvaluate {
        classDirectories.setFrom(files(classDirectories.files.map {
            fileTree(it) {
            }
        }))
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    tasks.withType<Test> {
        useJUnitPlatform()
        finalizedBy(jacocoReport, coverage)
    }

}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

