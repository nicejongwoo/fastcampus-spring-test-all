plugins {
    java
    idea
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")
    }

    maxHeapSize = "2g"
}

dependencies {
    // junit
    testImplementation(Testing.junit.jupiter)
    testRuntimeOnly(Testing.junit.jupiter.engine)

    // mockito
    testImplementation(Testing.mockito.core)
    testImplementation(Testing.mockito.junitJupiter)

    // 조건에 따른 테스트 의존성 추가
    project.afterEvaluate {
        if (project.pluginManager.hasPlugin("customs.spring-conventions")) {
            testImplementation(Spring.boot.test)
        }
    }
}
