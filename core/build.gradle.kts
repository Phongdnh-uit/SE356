dependencies {
    val bucket4jVersion = "8.10.1"
    val caffeineVersion = "3.2.3"
    val rsqlVersion = "6.0.33"
    val minioVersion = "8.6.0"
    val zxingVersion = "3.5.3"
    val googleAuthVersion = "1.5.0"   
    var webAuthn4jVersion = "0.31.1.RELEASE"

    implementation(project(":common"))
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-flyway")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.flywaydb:flyway-database-postgresql")
    implementation ("io.micrometer:micrometer-registry-prometheus")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation ("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.1")
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server")
    implementation ("com.fasterxml.uuid:java-uuid-generator:5.1.0")
    implementation ("org.springframework.boot:spring-boot-starter-mail")
    implementation ("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation ("com.bucket4j:bucket4j-core:$bucket4jVersion")
    implementation ("com.github.ben-manes.caffeine:caffeine:$caffeineVersion")
    implementation("io.github.perplexhub:rsql-jpa-spring-boot-starter:$rsqlVersion")
    implementation ("io.minio:minio:$minioVersion")
    implementation ("com.google.zxing:core:$zxingVersion")
    implementation ("com.google.zxing:javase:$zxingVersion")
    implementation ("com.warrenstrange:googleauth:$googleAuthVersion")
    implementation("com.webauthn4j:webauthn4j-core:$webAuthn4jVersion")

	runtimeOnly("org.postgresql:postgresql")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    testImplementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server-test")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
	testImplementation("org.springframework.boot:spring-boot-starter-flyway-test")
	testImplementation("org.springframework.boot:spring-boot-starter-thymeleaf-test")
	testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    testImplementation ("com.redis:testcontainers-redis")
    testImplementation ("org.testcontainers:testcontainers-minio")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
