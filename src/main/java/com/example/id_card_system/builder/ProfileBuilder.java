package com.example.id_card_system.builder;

import java.time.LocalDate;

import com.example.id_card_system.entity.BarcodeType;
import com.example.id_card_system.entity.Profile;
import com.example.id_card_system.entity.ProfileType;

public class ProfileBuilder {

    private final Profile profile;

    public ProfileBuilder() {
        profile = new Profile();
    }

    public ProfileBuilder fullName(String value) {
        profile.setFullName(value);
        return this;
    }

    public ProfileBuilder type(ProfileType value) {
        profile.setType(value);
        return this;
    }

    public ProfileBuilder department(String value) {
        profile.setDepartment(value);
        return this;
    }

    public ProfileBuilder title(String value) {
        profile.setTitle(value);
        return this;
    }

    public ProfileBuilder email(String value) {
        profile.setEmail(value);
        return this;
    }

    public ProfileBuilder defaultDates() {
        LocalDate now = LocalDate.now();
        profile.setIssueDate(now);
        profile.setExpiryDate(now.plusYears(1));
        return this;
    }

    public ProfileBuilder defaultCardSettings() {
        profile.setType(ProfileType.USER);
        profile.setDepartment("GENERAL");
        profile.setBarcodeType(BarcodeType.CODE_128);
        return this;
    }

    public Profile build() {
        return profile;
    }
}
