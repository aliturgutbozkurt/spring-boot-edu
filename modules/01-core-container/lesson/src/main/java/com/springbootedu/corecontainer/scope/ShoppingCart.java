package com.springbootedu.corecontainer.scope;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Lesson 3.3 — a prototype-scoped bean: every lookup creates a new, independent cart.
 */
// tag::prototype[]
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ShoppingCart {

    private final List<String> isbns = new ArrayList<>();   // state → must not be shared
    // end::prototype[]

    public void add(String isbn) {
        isbns.add(isbn);
    }

    public List<String> items() {
        return List.copyOf(isbns);
    }
}
