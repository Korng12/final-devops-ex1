package com.example.id_card_system.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.id_card_system.dto.ProfileRequest;
import com.example.id_card_system.entity.BarcodeType;
import com.example.id_card_system.entity.Profile;
import com.example.id_card_system.entity.ProfileType;
import com.example.id_card_system.entity.Template;
import com.example.id_card_system.repository.ProfileRepository;
import com.example.id_card_system.repository.TemplateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final TemplateRepository templateRepository;
    private final IdGeneratorService idGeneratorService;
    private final PhotoStorageService photoStorageService;

    public List<Profile> findAll(ProfileType type, String search) {
        if (search != null && !search.isBlank()) {
            return profileRepository
                    .findByFullNameContainingIgnoreCaseOrRegistrationNumberContainingIgnoreCaseOrDepartmentContainingIgnoreCase(
                            search, search, search);
        }
        if (type != null) {
            return profileRepository.findByType(type);
        }
        return profileRepository.findAll();
    }

    public Profile findById(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found: " + id));
    }

    public Profile findByUuid(String uuid) {
        return profileRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found: " + uuid));
    }

    public Profile create(ProfileRequest request) {
        Profile profile = new Profile();
        profile.setUuid(UUID.randomUUID().toString());
        profile.setRegistrationNumber(idGeneratorService.generate(request.type(), request.department()));
        applyRequest(profile, request);
        return profileRepository.save(profile);
    }

    public Profile update(Long id, ProfileRequest request) {
        Profile profile = findById(id);
        applyRequest(profile, request);
        return profileRepository.save(profile);
    }

    public void delete(Long id) {
        profileRepository.delete(findById(id));
    }

    public Profile attachPhoto(Long id, MultipartFile photo) {
        Profile profile = findById(id);
        profile.setPhotoFileName(photoStorageService.store(photo));
        profile.setPhotoContentType(photo.getContentType());
        return profileRepository.save(profile);
    }

    public List<Profile> findAllByIds(List<Long> ids) {
        List<Profile> profiles = profileRepository.findAllById(ids);
        if (profiles.size() != ids.size()) {
            throw new IllegalArgumentException("One or more profiles were not found");
        }
        return profiles;
    }

    private void applyRequest(Profile profile, ProfileRequest request) {
        profile.setType(request.type());
        profile.setFullName(request.fullName());
        profile.setDepartment(request.department());
        profile.setTitle(request.title());
        profile.setEmail(request.email());
        profile.setPhone(request.phone());
        profile.setBloodGroup(request.bloodGroup());
        profile.setDateOfBirth(request.dateOfBirth());
        profile.setIssueDate(request.issueDate());
        profile.setExpiryDate(request.expiryDate());
        profile.setBarcodeType(request.barcodeType() == null ? BarcodeType.CODE_128 : request.barcodeType());
        profile.setTemplate(resolveTemplate(request.templateId()));
    }

    private Template resolveTemplate(Long templateId) {
        if (templateId == null) {
            return templateRepository.findByCode("DEFAULT").orElse(null);
        }
        return templateRepository.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("Template not found: " + templateId));
    }
}
