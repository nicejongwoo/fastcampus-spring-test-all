plugins {
    id("customs.java-conventions")
    id("customs.test-conventions")
    id("customs.spring-conventions")
}

dependencies {
    implementation(Spring.boot.actuator)
    implementation(Spring.boot.web)
}
