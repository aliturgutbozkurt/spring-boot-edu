package com.springbootedu.dockerdeployment;

import com.springbootedu.dockerdeployment.shop.ShopProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Prints what the lesson is about when the application becomes ready (section 3).
 * Start it with: ./mvnw -pl modules/20-docker-deployment/lesson spring-boot:run   (Docker must be running)
 */
@Component
@ConditionalOnBooleanProperty(name = "bookstore.tour.enabled", matchIfMissing = true)
class LessonTour {

    private final ShopProperties shop;

    LessonTour(ShopProperties shop) {
        this.shop = shop;
    }

    @EventListener
    void readinessChanged(AvailabilityChangeEvent<ReadinessState> event) {
        System.out.println("=== readiness: " + event.getState() + " (shop \"" + shop.name() + "\") ===");
        if (event.getState() == ReadinessState.ACCEPTING_TRAFFIC) {
            System.out.println("  probes: http://localhost:8080/actuator/health/liveness and /readiness");
            System.out.println("  in a container: see README (docker build, docker compose up)");
        }
    }
}
