plugins {
    java
    kotlin("jvm") version "1.3.72"
    jacoco
    maven
    `maven-publish`
    signing
}

group = "com.github.itasyurt"
version = "7.8.1-alpha002"

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


publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.itasyurt"
            artifactId = "app-search-kotlin"
            version = "7.8.1-alpha002"

            from(components["java"])
        }
    }
}

tasks {
    val sourcesJar by creating(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    val javadocJar by creating(Jar::class) {
        dependsOn.add(javadoc)
        archiveClassifier.set("javadoc")
        from(javadoc)
    }

    artifacts {
        archives(sourcesJar)
        archives(javadocJar)
        archives(jar)
    }

}
signing {
    sign(configurations.archives.get())
}


tasks.named<Upload>("uploadArchives") {
    repositories {
        withConvention(MavenRepositoryHandlerConvention::class) {
            mavenDeployer {
                beforeDeployment { signing.signPom(this) }

                val nexusUsername:String by project
                val nexusPassword:String by project

                withGroovyBuilder {
                    "repository"("url" to "https://oss.sonatype.org/service/local/staging/deploy/maven2") {
                        "authentication"("userName" to nexusUsername, "password" to nexusPassword)
                    }
                    "snapshotRepository"("url" to "https://oss.sonatype.org/content/repositories/snapshots") {
                        "authentication"("userName" to nexusUsername, "password" to nexusPassword)
                    }
                    "pom" {
                        "project" {
                            "licenses" {
                                "license" {
                                    setProperty("name", "The Apache Software License, Version 2.0")
                                    setProperty("url", "http://www.apache.org/licenses/LICENSE-2.0.txt")
                                }
                            }

                            setProperty("packaging", "jar")
                            setProperty("name", "App Search Kotlin")
                            setProperty("description", "Elastic App Search Kotlin Client")
                            setProperty("url","https://github.com/itasyurt/app-search-kotlin/")
                            "scm" {
                                setProperty("connection", "scm:git:https://github.com/itasyurt/app-search-kotlin.git")
                                setProperty("url","https://github.com/itasyurt/app-search-kotlin/")
                            }
                            "developers" {
                                "developer"{
                                    setProperty("name", "Ibrahim Tasyurt")
                                }
                            }


                        }
                    }

                }

            }
        }
    }
}