package com.example.id_card_system.dto;

import java.time.LocalDate;

import com.example.id_card_system.entity.BarcodeType;
import com.example.id_card_system.entity.ProfileType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileForm {

    private ProfileType type = ProfileType.STUDENT;
    private String fullName;
    private String department;
    private String title;
    private String email;
    private String phone;
    private String bloodGroup;
    private LocalDate dateOfBirth;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private Long templateId;
    private BarcodeType barcodeType = BarcodeType.CODE_128;

    public ProfileRequest toRequest() {
        return new ProfileRequest(
                type,
                fullName,
                department,
                title,
                email,
                phone,
                bloodGroup,
                dateOfBirth,
                issueDate,
                expiryDate,
                templateId,
                barcodeType);
    }
}
