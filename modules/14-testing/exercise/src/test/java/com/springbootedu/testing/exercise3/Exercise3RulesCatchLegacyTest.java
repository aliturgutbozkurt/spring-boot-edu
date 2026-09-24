package com.springbootedu.testing.exercise3;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Proves that the rules of exercise 3 are not empty: applied to the whole exercise, including the legacy package,
 * every rule finds a violation.
 */
class Exercise3RulesCatchLegacyTest {

    private final JavaClasses everything = new ClassFileImporter().importPackages("com.springbootedu.testing.exercise3");

    @Test
    void everyRuleFindsTheLegacyViolations() {
        List<ArchRule> rules = List.of(
                ShopArchitectureTest.repositoriesAreUsedOnlyByServices,
                ShopArchitectureTest.controllersDoNotUseRepositories,
                ShopArchitectureTest.dataClassesLiveInTheRepositoryPackage);

        assertThat(rules).allSatisfy(rule ->
                assertThat(rule.evaluate(everything).hasViolation()).as(rule.getDescription()).isTrue());
    }
}
