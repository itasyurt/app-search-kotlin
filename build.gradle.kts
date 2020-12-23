plugins {
    java
    kotlin("jvm") version "1.3.72"
    jacoco
    maven
    `maven-publish`
    signing
}

group = "com.github.itasyurt"
version = "7.8.1-alpha003"

repositories {
    mavenLocal()
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
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.4")
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

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId =  project.group.toString()
            artifactId = "app-search-kotlin"
            version = project.version.toString()

            from(components["java"])

            pom {
                name.set("App Search Kotlin")
                description.set("Elastic App Search Kotlin Client")
                url.set("https://github.com/itasyurt/app-search-kotlin")
                packaging = "jar"

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("itasyurt")
                        name.set("Ibrahim Tasyurt")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/itasyurt/app-search-kotlin.git")
                    developerConnection.set("scm:git:ssh://example.com/my-library.git")
                    url.set("https://github.com/itasyurt/app-search-kotli")
                }
            }
            repositories {
                maven {
                    val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2"
                    val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots"
                    url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
                    credentials {
                        val nexusUsername:String by project
                        val nexusPassword:String by project
                        username = nexusUsername
                        password = nexusPassword

                    }

                }
            }
        }


    }
}



signing {
    sign(publishing.publications["mavenJava"])
}
