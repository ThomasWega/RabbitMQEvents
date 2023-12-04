plugins {
    id("java")
    id("io.freefair.lombok") version "8.4"
    `maven-publish`
    `java-library`
}

group = "me.wega"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api("com.rabbitmq:amqp-client:5.20.0")
    api("org.json:json:20231013")
    implementation("org.jetbrains:annotations:24.0.0")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("RabbitMQEvents") {
            from(components["java"])
        }
    }
}