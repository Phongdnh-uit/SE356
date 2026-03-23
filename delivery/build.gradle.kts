val timefoldSolverVersion = "2.0.0-beta-2"

dependencies {
    implementation(project(":common"))

    implementation("org.springframework.boot:spring-boot-starter-kafka")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-kafka-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    implementation(platform("ai.timefold.solver:timefold-solver-bom:${timefoldSolverVersion}"))
    implementation("ai.timefold.solver:timefold-solver-core")
}