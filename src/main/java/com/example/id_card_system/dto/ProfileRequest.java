package com.example.id_card_system.dto;

import java.time.LocalDate;

import com.example.id_card_system.entity.BarcodeType;
import com.example.id_card_system.entity.ProfileType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProfileRequest(
        @NotNull ProfileType type,
        @NotBlank @Size(max = 120) String fullName,
        @Size(max = 80) String department,
        @Size(max = 120) String title,
        @Email @Size(max = 120) String email,
        @Size(max = 40) String phone,
        @Size(max = 60) String bloodGroup,
        LocalDate dateOfBirth,
        LocalDate issueDate,
        LocalDate expiryDate,
        Long templateId,
        BarcodeType barcodeType) {
}
