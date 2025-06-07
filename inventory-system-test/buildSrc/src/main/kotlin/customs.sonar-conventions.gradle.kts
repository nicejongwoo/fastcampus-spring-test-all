plugins {
    id("org.sonarqube")
}

sonar {
    properties {
        property("sonar.organization", "nicejongwoo")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.sources", "src/main/java")
        property("sonar.tests", "src/test/java")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "build/reports/jacoco/test/jacocoTestReport.xml",
        )
        property(
            "sonar.junit.reportPaths",
            "build/test-results/test"
        )
    }
}