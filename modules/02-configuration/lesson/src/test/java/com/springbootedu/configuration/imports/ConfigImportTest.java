package com.springbootedu.configuration.imports;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Lesson 3.5 — spring.config.import pulls in extra files; "optional:" tolerates missing ones.
 */
@SpringBootTest
class ConfigImportTest {

    @Autowired
    CampaignProperties campaigns;

    @Test
    void campaignsComeFromTheImportedFile() {
        assertThat(campaigns.active())
                .extracting(CampaignProperties.Campaign::code)
                .containsExactly("OKULA-DONUS", "KITAP-FUARI");
    }

    @Test
    void campaignDetailsAreBound() {
        assertThat(campaigns.active().getFirst().percent()).isEqualTo(15);
    }
}
