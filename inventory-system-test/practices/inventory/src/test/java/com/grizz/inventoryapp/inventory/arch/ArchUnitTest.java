package com.grizz.inventoryapp.inventory.arch;

import com.grizz.inventoryapp.test.exception.NotImplementedTestException;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

public class ArchUnitTest {

    private final JavaClasses targetClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS) // 테스트 클래스 제외
            .importPackages("com.grizz.inventoryapp.inventory");

    private final Architectures.LayeredArchitecture baseRule = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Controller").definedBy("..controller..")
            .layer("Service").definedBy("..service..")
            .layer("Repository").definedBy("..repository..");


    @DisplayName("Controller 레이어는 Service 레이어에만 의존한다.")
    @Test
    void test1() {
        final ArchRule rule = baseRule
                .whereLayer("Controller").mayNotBeAccessedByAnyLayer() // Controller 레이어는 다른 레이어에 의존하지 않는다.
                .whereLayer("Controller").mayOnlyAccessLayers("Service"); // Controller 레이어는 Service 레이어에만 의존한다.

        rule.check(targetClasses);
    }

    @DisplayName("Service 레이어는 그 어떤 레이어에 의존하지 않는다")
    @Test
    void test2() {
        final ArchRule rule = baseRule
                .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller", "Repository")
                .whereLayer("Service").mayNotAccessAnyLayer();

        rule.check(targetClasses);
    }

    @DisplayName("Repository 레이어는 Service 레이어에만 의존한다.")
    @Test
    void test3() {
        final ArchRule rule = baseRule
                .whereLayer("Repository").mayNotBeAccessedByAnyLayer()
                .whereLayer("Service").mayOnlyAccessLayers("Service");

        rule.check(targetClasses);
    }

}
