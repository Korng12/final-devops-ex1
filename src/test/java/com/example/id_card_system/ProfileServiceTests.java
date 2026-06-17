package com.example.id_card_system;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.id_card_system.dto.ProfileRequest;
import com.example.id_card_system.entity.Profile;
import com.example.id_card_system.entity.ProfileType;
import com.example.id_card_system.services.ProfileService;

@SpringBootTest
class ProfileServiceTests {

    @Autowired
    private ProfileService profileService;

    @Test
    void createsProfileWithUuidAndRegistrationNumber() {
        Profile profile = profileService.create(new ProfileRequest(
                ProfileType.STUDENT,
                "Ada Lovelace",
                "Engineering",
                "Computer Science",
                "ada@example.com",
                "555-0100",
                "O+",
                null,
                null,
                null,
                null,
                null));

        assertThat(profile.getId()).isNotNull();
        assertThat(profile.getUuid()).isNotBlank();
        assertThat(profile.getRegistrationNumber()).contains("-ENG-");
        assertThat(profile.getBarcodeType()).isNotNull();
        assertThat(profile.getTemplate()).isNotNull();
    }

    @Test
    void listsProfilesBySearchTerm() {
        Profile profile = profileService.create(new ProfileRequest(
                ProfileType.EMPLOYEE,
                "Grace Hopper",
                "Research",
                "Engineer",
                "grace@example.com",
                null,
                null,
                null,
                null,
                null,
                null,
                null));

        assertThat(profile.getRegistrationNumber()).contains("-EMP-RES-");
        assertThat(profileService.findAll(null, "Hopper"))
                .extracting(Profile::getFullName)
                .contains("Grace Hopper");
    }
}
