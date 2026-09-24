package com.springbootedu.testing.exercise3;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Exercise 3 — the architecture rules of the shop. Replace each placeholder with a real rule.
 * Exercise3RulesCatchLegacyTest checks that every rule finds the violations of the legacy package.
 */
@AnalyzeClasses(packages = "com.springbootedu.testing.exercise3.shop")
class ShopArchitectureTest {

    // TODO 3a: classes in a "repository" package may only be used by the service (and repository) packages
    @ArchTest
    static final ArchRule repositoriesAreUsedOnlyByServices = placeholder();

    // TODO 3b: classes whose name ends with "Controller" must not depend on the repository package
    @ArchTest
    static final ArchRule controllersDoNotUseRepositories = placeholder();

    // TODO 3c: classes named "…Repository" or "…Store" must live in a "repository" package
    @ArchTest
    static final ArchRule dataClassesLiveInTheRepositoryPackage = placeholder();

    /**
     * A rule that checks nothing — delete it when your rules are done.
     */
    private static ArchRule placeholder() {
        return classes().that().haveSimpleName("Placeholder").should().bePublic().allowEmptyShould(true);
    }
}
