plugins {
    id("customs.java-conventions")
    id("customs.test-conventions")
    id("customs.spring-conventions")
}

dependencies {
    implementation(Spring.boot.actuator)
    implementation(Spring.boot.web)
    implementation(Spring.boot.data.jpa)

    // test
    testImplementation("com.h2database:h2")
    testImplementation("mysql:mysql-connector-java:_")
}
