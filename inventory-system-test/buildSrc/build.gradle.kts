plugins {
    `kotlin-dsl`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
        vendor.set(JvmVendorSpec.ADOPTIUM) // sdk install java 21.0.7-tem
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-gradle-plugin:_")
    implementation("io.spring.gradle:dependency-management-plugin:_")
    implementation("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:_")
}
