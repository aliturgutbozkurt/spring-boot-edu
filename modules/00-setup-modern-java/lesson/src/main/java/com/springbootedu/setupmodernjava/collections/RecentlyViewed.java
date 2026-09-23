package com.springbootedu.setupmodernjava.collections;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.SequencedSet;

/**
 * Lesson 3.7 — sequenced collections: first/last access and a reversed view on ordered collections.
 */
// tag::sequenced[]
public class RecentlyViewed {

    private final SequencedSet<String> isbns = new LinkedHashSet<>();
    private final int limit;

    public RecentlyViewed(int limit) {
        this.limit = limit;
    }

    public void view(String isbn) {
        isbns.addFirst(isbn);               // already present? LinkedHashSet moves it to the front
        while (isbns.size() > limit) {
            isbns.removeLast();             // drop the oldest
        }
    }

    public List<String> newestFirst() {
        return List.copyOf(isbns);
    }

    public List<String> oldestFirst() {
        return List.copyOf(isbns.reversed());   // a view, no copy of the set
    }

    public Optional<String> latest() {
        return isbns.isEmpty() ? Optional.empty() : Optional.of(isbns.getFirst());
    }
}
// end::sequenced[]
