package com.springbootedu.testing.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lesson 3.8 — architecture rules as tests: they fail the build when someone breaks the structure.
 */
// tag::archunit[]
@AnalyzeClasses(packages = "com.springbootedu.testing", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule controllersDoNotTalkToRepositories = noClasses()
            .that().areAnnotatedWith(RestController.class)
            .should().dependOnClassesThat().haveSimpleNameEndingWith("Repository")
            .because("controllers go through a service, which owns the transaction");

    @ArchTest
    static final ArchRule pricingIsPlainJava = noClasses()
            .that().resideInAPackage("..pricing..")
            .should().dependOnClassesThat().resideInAPackage("org.springframework..")
            .because("business rules must be testable without Spring");

    @ArchTest
    static final ArchRule noFieldInjection = fields()
            .should().notBeAnnotatedWith(Autowired.class)
            .because("constructor injection makes dependencies explicit and classes testable with new");
}
// end::archunit[]
