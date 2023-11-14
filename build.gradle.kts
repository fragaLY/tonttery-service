import java.net.URI

plugins {
    java
    application
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.4"
    id("com.google.cloud.tools.jib") version "3.4.0"
}

group = "by.vk"
version = "1.0.0-RC1"

springBoot {
    buildInfo()
}

application {
    mainClass = "by.vk.tonttery.Application"
    applicationName = "tonttery-service"
}


java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    maven { url = URI.create("https://repo.spring.io/release") }
    mavenCentral()
}

extra["springCloudGcpVersion"] = "4.8.0"
extra["springCloudVersion"] = "2022.0.4"

dependencies {
    //region spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-quartz")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
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
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("io.micrometer:micrometer-registry-prometheus")

//    implementation("org.telegram:telegrambots-spring-boot-starter:6.8.0")
    //endregion
    //region test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
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

object JVMProps {
    const val XMX = "512m"
    const val XMS = "256m"
    const val XSS = "256k"
    const val MAX_METASPACE_SIZE = "128m"
    const val MAX_DIRECT_MEMORY_SIZE = "128m"
    const val HEAPDUMP_PATH = "/opt/tmp/heapdump.bin"
    const val MAX_RAM_PERCENTAGE = "80"
    const val INITIAL_RAM_PERCENTAGE = "50"
}

jib {
    setAllowInsecureRegistries(false)
    to {
        image = "fragaly/tonttery-service"
        tags = setOf("$version", "latest")
    }
    from {
        image = "gcr.io/distroless/java17"
    }
    container {
        jvmFlags = listOf(
                "-Xss${JVMProps.XSS}",
                "-Xmx${JVMProps.XMX}",
                "-Xms${JVMProps.XMS}",
                "-XX:MaxMetaspaceSize=${JVMProps.MAX_METASPACE_SIZE}",
                "-XX:MaxDirectMemorySize=${JVMProps.MAX_DIRECT_MEMORY_SIZE}",
                "-XX:MaxRAMPercentage=${JVMProps.MAX_RAM_PERCENTAGE}",
                "-XX:InitialRAMPercentage=${JVMProps.INITIAL_RAM_PERCENTAGE}",
                "-XX:+HeapDumpOnOutOfMemoryError",
                "-XX:HeapDumpPath=${JVMProps.HEAPDUMP_PATH}",
                "-XX:+UseContainerSupport",
                "-XX:+OptimizeStringConcat",
                "-XX:+UseStringDeduplication",
                "-XX:+ExitOnOutOfMemoryError",
                "-XX:+AlwaysActAsServerClassMachine")
        ports = listOf("8080")
        labels.set(mapOf("appname" to application.applicationName, "version" to version.toString(), "maintainer" to "Vadzim Kavalkou <vadzim.kavalkou@gmail.com>"))
        creationTime.set("USE_CURRENT_TIMESTAMP")
    }
}