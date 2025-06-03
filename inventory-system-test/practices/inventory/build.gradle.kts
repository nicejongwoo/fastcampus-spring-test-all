plugins {
    id("customs.java-conventions")
    id("customs.test-conventions")
    id("customs.spring-conventions")
}

dependencies {
    implementation(Spring.boot.actuator)
    implementation(Spring.boot.web.toString()) {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-undertow")
    implementation(Spring.boot.data.jpa)
    implementation(Spring.boot.data.redis)
    implementation(Spring.cloud.stream.stream)
    implementation(Spring.cloud.stream.binderKafka)

    implementation("mysql:mysql-connector-java:_")

    // test
    testImplementation("com.h2database:h2")

    // testcontainers
    testImplementation(platform("org.testcontainers:testcontainers-bom:_"))
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mysql")
    testImplementation("org.testcontainers:kafka")

    // archUnit
    testImplementation("com.tngtech.archunit:archunit-junit5:_")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.1") // spring boot 3.4.X 호환
    }
}
