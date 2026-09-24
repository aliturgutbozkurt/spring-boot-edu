package com.springbootedu.security.users;

import static org.assertj.core.api.Assertions.assertThat;

import com.springbootedu.security.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

/**
 * Lesson 3.2 — users live in PostgreSQL; passwords are stored as salted BCrypt hashes, never as plain text.
 */
@SpringBootTest(properties = "bookstore.tour.enabled=false")
@Import(TestcontainersConfiguration.class)
class PasswordStorageTest {

    @Autowired
    UserDetailsManager users;

    @Autowired
    PasswordEncoder passwords;

    @Autowired
    JdbcClient jdbc;

    @Test
    void theDemoUsersAreLoadedFromTheDatabase() {
        var ada = users.loadUserByUsername("ada");

        assertThat(ada.getAuthorities()).extracting(Object::toString).containsExactly("ROLE_CUSTOMER");
        assertThat(passwords.matches("ada-password", ada.getPassword())).isTrue();
    }

    @Test
    void aNewUserIsStoredWithAnAlgorithmPrefixAndASaltedHash() {
        users.createUser(User.withUsername("carol-" + System.nanoTime())
                .password(passwords.encode("carol-password")).roles("CUSTOMER").build());

        var stored = jdbc.sql("SELECT password FROM users WHERE username LIKE 'carol-%' ORDER BY username DESC LIMIT 1")
                .query(String.class).single();
        assertThat(stored).startsWith("{bcrypt}$2a$").doesNotContain("carol-password");
    }

    @Test
    void theSamePasswordGivesADifferentHashEveryTime() {
        assertThat(passwords.encode("same")).isNotEqualTo(passwords.encode("same"));   // a new random salt
    }
}
