import java.net.URI

plugins {
    java
    checkstyle
    application
    id("org.springframework.boot") version "3.1.4"
    id("io.spring.dependency-management") version "1.1.3"
    id("com.google.cloud.tools.jib") version "3.4.0"
}

group = "by.vk"
version = "1.0.0-RC"

springBoot {
    buildInfo()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    maven { url = URI.create("https://repo.spring.io/release") }
    mavenCentral()
}

extra["springCloudGcpVersion"] = "4.7.2"
extra["springCloudVersion"] = "2022.0.4"

dependencies {
    //region spring
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework:spring-context-indexer")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-quartz")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
    //endregion
    //region lombok
    annotationProcessor("org.projectlombok:lombok")
    implementation("org.projectlombok:lombok")
    //endregion
    //region logback
    implementation("ch.qos.logback.contrib:logback-jackson:0.1.5")
    implementation("ch.qos.logback.contrib:logback-json-classic:0.1.5")
    //endregion
    //region database
    implementation("org.flywaydb:flyway-core")
    runtimeOnly("org.postgresql:postgresql")
    //end region
    //region 3rd party
    implementation("com.google.cloud:spring-cloud-gcp-starter")
//    implementation("org.telegram:telegrambots-spring-boot-starter:6.8.0")
    //endregion
    //region test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    //endregion
}

dependencyManagement {
    imports {
        mavenBom("com.google.cloud:spring-cloud-gcp-dependencies:${property("springCloudGcpVersion")}")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<Checkstyle>().configureEach {
    reports {
        xml.required.set(false)
        html.required.set(true)
        html.stylesheet = resources.text.fromFile(projectDir.toString().plus("/google_checks.xml"))
    }
}

jib {
    setAllowInsecureRegistries(true)
    to {
        image = System.getenv("GITHUB_REPOSITORY").plus("/")
                .plus(":")
                .plus(System.getenv("GITHUB_SHA"))
    }
    from {
        image = "gcr.io/distroless/java17:latest"
    }
    container {
        labels.set(mapOf("appname" to application.applicationName, "version" to version.toString(), "owner" to "vadzim.kavalkou@gmail.com"))
        ports = listOf("8080")
        creationTime.set("USE_CURRENT_TIMESTAMP")
    }
}

//todo vk: add EHACHE
//todo vk: add HATEOAS
//todo vk: add resilience4j
