package com.springbootedu.configuration.diagnostics;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport;
import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport.ConditionAndOutcomes;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.7 — reads the same conditions report that --debug prints at startup.
 * (Not named "AutoConfigurationReport": Boot already registers its report under that bean name.)
 */
// tag::report[]
@Component
public class ConditionsExplainer {

    private final ConfigurableListableBeanFactory beanFactory;

    public ConditionsExplainer(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public String explain(String autoConfigurationSimpleName) {
        Map<String, ConditionAndOutcomes> outcomes =
                ConditionEvaluationReport.get(beanFactory).getConditionAndOutcomesBySource();

        return outcomes.entrySet().stream()
                .filter(entry -> entry.getKey().endsWith("." + autoConfigurationSimpleName))
                .findFirst()
                .map(entry -> (entry.getValue().isFullMatch() ? "MATCHED: " : "SKIPPED: ")
                        + entry.getValue().stream()
                                .map(outcome -> outcome.getOutcome().getMessage())
                                .collect(Collectors.joining("; ")))
                .orElse("NOT A CANDIDATE: its module is not on the classpath");
    }
}
// end::report[]
