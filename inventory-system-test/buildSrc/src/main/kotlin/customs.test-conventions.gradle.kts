plugins {
    java
    idea
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform {
        if (EnvUtils.isAct()) {
            excludeTags("integration") // act 에서는 도커 컨테이너 테스트가 안되어서 제외
        }
    }

    testLogging {
        events("passed", "skipped", "failed")
    }

    maxHeapSize = "2g"
}

dependencies {

    // junit
    testImplementation(Testing.junit.jupiter)
    testRuntimeOnly(Testing.junit.jupiter.engine.withoutVersion()) // Spring.boot.test 하고 버전 충돌 방지

    // mockito
    testImplementation(Testing.mockito.core)
    testImplementation(Testing.mockito.junitJupiter)

    // 조건에 따른 테스트 의존성 추가
    project.afterEvaluate {
        if (project.pluginManager.hasPlugin("customs.spring-conventions")) {
            testImplementation(Spring.boot.test).apply {  }
        }
    }

}
