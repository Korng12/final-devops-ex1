package com.example.id_card_system.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.id_card_system.entity.Profile;
import com.example.id_card_system.entity.ProfileType;

public interface ProfileRepository
        extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUuid(String uuid);

    Optional<Profile> findByRegistrationNumber(String registrationNumber);

    boolean existsByRegistrationNumber(String registrationNumber);

    boolean existsByEmail(String email);

    long countByRegistrationNumberStartingWith(String prefix);

    List<Profile> findByType(ProfileType type);

    List<Profile> findByFullNameContainingIgnoreCaseOrRegistrationNumberContainingIgnoreCaseOrDepartmentContainingIgnoreCase(
            String fullName, String registrationNumber, String department);
}
