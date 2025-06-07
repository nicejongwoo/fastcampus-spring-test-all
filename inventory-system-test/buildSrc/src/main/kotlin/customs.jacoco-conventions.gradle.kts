plugins {
    java
    idea
    jacoco
}

jacoco {
    toolVersion = "0.8.12"
}

// 확인용: 태스크 목록 출력
// ./gradlew tasks --all | grep jacoco
tasks.jacocoTestReport {
    reports {
        html.required.set(EnvUtils.isLocal())
        xml.required.set(EnvUtils.isCI())
    }
}