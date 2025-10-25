plugins {
	java
	id("org.springframework.boot") version "3.3.4"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.gmapex"
version = "0.0.1-SNAPSHOT"
description = "Order Service"
java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
	// --- Core ---
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	// --- Core ---
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	// --- MongoDB ---
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

	// --- Redis ---
	implementation("org.springframework.boot:spring-boot-starter-data-redis")

	// --- Kafka ---
	implementation("org.springframework.kafka:spring-kafka")

	// --- Lombok ---
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// --- Test ---
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}