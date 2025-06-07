plugins {
    id("org.sonarqube")
}

sonar {
    properties {
        property("sonar.organization", "nicejongwoo")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}