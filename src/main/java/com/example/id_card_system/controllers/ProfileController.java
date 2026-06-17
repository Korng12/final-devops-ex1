package com.example.id_card_system.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.id_card_system.dto.ProfileRequest;
import com.example.id_card_system.entity.Profile;
import com.example.id_card_system.entity.ProfileType;
import com.example.id_card_system.services.CardRenderingService;
import com.example.id_card_system.services.ProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@Validated
public class ProfileController {

    private final ProfileService profileService;
    private final CardRenderingService cardRenderingService;

    @GetMapping
    public List<Profile> list(@RequestParam(required = false) ProfileType type,
            @RequestParam(required = false) String search) {
        return profileService.findAll(type, search);
    }

    @PostMapping
    public Profile create(@Valid @RequestBody ProfileRequest request) {
        return profileService.create(request);
    }

    @GetMapping("/{id}")
    public Profile get(@PathVariable Long id) {
        return profileService.findById(id);
    }

    @PutMapping("/{id}")
    public Profile update(@PathVariable Long id, @Valid @RequestBody ProfileRequest request) {
        return profileService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        profileService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Profile uploadPhoto(@PathVariable Long id, @RequestPart("photo") MultipartFile photo) {
        return profileService.attachPhoto(id, photo);
    }

    @GetMapping(path = "/{id}/preview", produces = MediaType.TEXT_HTML_VALUE)
    public String preview(@PathVariable Long id) {
        return cardRenderingService.previewHtml(profileService.findById(id));
    }

    @GetMapping(path = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> pdf(@PathVariable Long id) {
        Profile profile = profileService.findById(id);
        return pdfResponse(cardRenderingService.pdf(profile), profile.getRegistrationNumber() + ".pdf");
    }

    @PostMapping(path = "/batch-pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> batchPdf(@RequestBody List<Long> profileIds) {
        return pdfResponse(cardRenderingService.pdf(profileService.findAllByIds(profileIds)), "id-cards-batch.pdf");
    }

    @GetMapping(path = "/{id}/qr.png", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] qrCode(@PathVariable Long id) {
        return cardRenderingService.qrCode(profileService.findById(id));
    }

    @GetMapping(path = "/{id}/barcode.png", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] barcode(@PathVariable Long id) {
        return cardRenderingService.barcode(profileService.findById(id));
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<Resource> photo(@PathVariable Long id) throws Exception {
        Profile profile = profileService.findById(id);
        if (!profile.hasPhoto()) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new UrlResource(profileService.photoPath(profile).toUri());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(profile.getPhotoContentType()))
                .body(resource);
    }

    @GetMapping("/verify/{uuid}")
    public Map<String, Object> verify(@PathVariable String uuid) {
        Profile profile = profileService.findByUuid(uuid);
        return Map.of(
                "valid", true,
                "registrationNumber", profile.getRegistrationNumber(),
                "fullName", profile.getFullName(),
                "type", profile.getType());
    }

    private ResponseEntity<byte[]> pdfResponse(byte[] bytes, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(bytes);
    }
}
