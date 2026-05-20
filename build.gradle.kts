plugins {
	java
    id("org.sonarqube") version "7.2.2.6593"
    id("jacoco")
    id("com.diffplug.spotless") version "8.2.1"
	id("org.springframework.boot") version "4.0.2" apply false
	id("io.spring.dependency-management") version "1.1.7" apply false
}

jacoco {
    toolVersion = "0.8.14"
}

spotless {
    java {
        target("**/src/**/*.java")
        googleJavaFormat()
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
}

sonar {
    properties {
        property("sonar.projectKey", "Phongdnh-uit_FlashMile")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.organization", "phongdnh")
        property("sonar.coverage.jacoco.xmlReportPaths", "**/build/reports/jacoco/test/jacocoTestReport.xml")
    }
}

allprojects {
    group = "com.uit.se356"
    version = "0.0.1-SNAPSHOT"
    description = "Dự án cho môn học SE356"
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "jacoco")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    sonar {
        properties {
            property("sonar.sources", "src/main/java")
            property("sonar.tests", "src/test/java")
            property("sonar.java.binaries", "build/classes/java/main")
        }
    }

    dependencies {
        val lokiVersion = "2.0.3"
        implementation("org.springframework.boot:spring-boot-starter-actuator")
        implementation ("com.github.loki4j:loki-logback-appender:$lokiVersion")
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
        annotationProcessor("org.projectlombok:lombok")
        testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        finalizedBy("jacocoTestReport")
    }

    tasks.withType<JacocoReport> {
        dependsOn(tasks.test)
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }
}
