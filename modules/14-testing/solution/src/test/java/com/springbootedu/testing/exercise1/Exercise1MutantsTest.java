package com.springbootedu.testing.exercise1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import com.springbootedu.testing.exercise1.mutants.BrokenMinimum;
import com.springbootedu.testing.exercise1.mutants.NoBirthdayBonus;
import com.springbootedu.testing.exercise1.mutants.NoCap;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.testkit.engine.EngineTestKit;

/**
 * Proves that the tests of exercise 1 are good enough: each deliberately broken calculator must make
 * LoyaltyCalculatorTest fail (a tiny "mutation test").
 */
class Exercise1MutantsTest {

    static Stream<Supplier<LoyaltyCalculator>> mutants() {
        return Stream.of(BrokenMinimum::new, NoBirthdayBonus::new, NoCap::new);
    }

    @ParameterizedTest
    @MethodSource("mutants")
    void theTestsCatchTheBug(Supplier<LoyaltyCalculator> mutant) {
        Subjects.calculator = mutant;
        try {
            long failed = EngineTestKit.engine("junit-jupiter")
                    .selectors(selectClass(LoyaltyCalculatorTest.class))
                    .execute()
                    .testEvents().failed().count();

            assertThat(failed).as("tests failing against %s", mutant.get().getClass().getSimpleName()).isPositive();
        } finally {
            Subjects.calculator = LoyaltyCalculator::new;
        }
    }
}
