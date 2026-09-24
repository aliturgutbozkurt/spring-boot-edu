package com.springbootedu.asyncschedulingbatch.exercise2;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * Given: reads the order file named by the job parameter "input.file".
 */
@Configuration(proxyBeanMethods = false)
class OrderReaderConfiguration {

    @Bean
    @StepScope
    FlatFileItemReader<OrderLine> orderLineReader(@Value("#{jobParameters['input.file']}") Resource input) {
        return new FlatFileItemReaderBuilder<OrderLine>()
                .name("orderLineReader")
                .resource(input)
                .linesToSkip(1)
                .delimited().names("orderId", "isbn", "quantity")
                .targetType(OrderLine.class)
                .build();
    }
}
