package com.springbootedu.testing.exercise3.legacy;

import java.util.List;

/**
 * Given: an old data class outside the repository package — your rules must catch it.
 */
public class LegacyStore {

    public List<String> titles() {
        return List.of("Java Puzzlers");
    }
}
