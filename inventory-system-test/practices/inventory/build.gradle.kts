plugins {
    id("customs.java-conventions")
    id("customs.test-conventions")
    id("customs.spring-conventions")
}

dependencies {
    implementation(Spring.boot.actuator)
    implementation(Spring.boot.web)
    implementation(Spring.boot.data.jpa)
    implementation(Spring.boot.data.redis)

    implementation("mysql:mysql-connector-java:_")

    // test
    testImplementation("com.h2database:h2")

    // testcontainers
    testImplementation(platform("org.testcontainers:testcontainers-bom:_"))
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mysql")

    // archUnit
    testImplementation("com.tngtech.archunit:archunit-junit5:_")
}
