package com.springbootedu.testing.exercise3;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Exercise 3 — the architecture rules of the shop, checked on every build.
 */
@AnalyzeClasses(packages = "com.springbootedu.testing.exercise3.shop")
class ShopArchitectureTest {

    @ArchTest
    static final ArchRule repositoriesAreUsedOnlyByServices = classes()
            .that().resideInAPackage("..repository..")
            .should().onlyBeAccessed().byAnyPackage("..service..", "..repository..");

    @ArchTest
    static final ArchRule controllersDoNotUseRepositories = noClasses()
            .that().haveSimpleNameEndingWith("Controller")
            .should().dependOnClassesThat().resideInAPackage("..repository..");

    @ArchTest
    static final ArchRule dataClassesLiveInTheRepositoryPackage = classes()
            .that().haveSimpleNameEndingWith("Repository").or().haveSimpleNameEndingWith("Store")
            .should().resideInAPackage("..repository..");
}
