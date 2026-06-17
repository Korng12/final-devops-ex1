package com.example.id_card_system.services;

import java.time.Year;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.example.id_card_system.entity.ProfileType;
import com.example.id_card_system.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IdGeneratorService {

    private final ProfileRepository profileRepository;

    public String generate(ProfileType type, String department) {
        String dept = normalizeDepartment(department);
        String profilePrefix = type == ProfileType.EMPLOYEE ? "EMP-" + dept : dept;
        String prefix = Year.now().getValue() + "-" + profilePrefix + "-";
        long next = profileRepository.countByRegistrationNumberStartingWith(prefix) + 1;
        String candidate = prefix + String.format("%03d", next);

        while (profileRepository.existsByRegistrationNumber(candidate)) {
            next++;
            candidate = prefix + String.format("%03d", next);
        }
        return candidate;
    }

    private String normalizeDepartment(String department) {
        if (department == null || department.isBlank()) {
            return "GEN";
        }
        String clean = department.trim().toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]", "");
        if (clean.isBlank()) {
            return "GEN";
        }
        return clean.substring(0, Math.min(3, clean.length()));
    }
}
