package com.springbootedu.corecontainer.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.8 — cross-cutting code (timing) kept out of the business classes.
 */
// tag::aspect[]
@Aspect
@Component
public class ExecutionTimeAspect {

    private static final Logger log = LoggerFactory.getLogger(ExecutionTimeAspect.class);

    private final MethodTimings timings;

    public ExecutionTimeAspect(MethodTimings timings) {
        this.timings = timings;
    }

    @Around("@annotation(com.springbootedu.corecontainer.aop.LogExecutionTime)")
    public Object measure(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().getDeclaringType().getSimpleName() + "." + joinPoint.getSignature().getName();
        long start = System.nanoTime();
        try {
            return joinPoint.proceed();       // call the real method
        } finally {
            long micros = (System.nanoTime() - start) / 1_000;
            timings.record(method);
            log.info("{} took {} µs", method, micros);
        }
    }
}
// end::aspect[]
