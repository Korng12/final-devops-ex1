package com.example.id_card_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record TemplateRequest(
        @NotBlank @Size(max = 60) String code,
        @NotBlank @Size(max = 80) String name,
        @Size(max = 120) String organizationName,
        @NotBlank @Size(max = 20) String layout,
        @Pattern(regexp = "^#[0-9a-fA-F]{6}$") String primaryColor,
        @Pattern(regexp = "^#[0-9a-fA-F]{6}$") String secondaryColor,
        @Pattern(regexp = "^#[0-9a-fA-F]{6}$") String textColor,
        @Size(max = 255) String tagline) {
}
