package ru.kuzmin.bankaccount.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Тест границ гексагональной архитектуры.
 * Главное правило: домен не зависит ни от фреймворков, ни от application/infrastructure.
 */
class HexagonalArchitectureTest {

    private static final JavaClasses classes =
            new ClassFileImporter().importPackages("ru.kuzmin.bankaccount");

    @Test
    void domain_should_not_depend_on_frameworks_or_other_layers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "org.apache.kafka..",
                        "com.fasterxml.jackson..",
                        "..infrastructure..",
                        "..application..");

        rule.check(classes);
    }

    @Test
    void application_should_not_depend_on_infrastructure() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInAPackage("..infrastructure..");

        rule.check(classes);
    }
}
