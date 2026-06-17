package com.example.id_card_system.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.id_card_system.dto.TemplateRequest;
import com.example.id_card_system.entity.Template;
import com.example.id_card_system.repository.TemplateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateRepository templateRepository;

    public List<Template> findAll(String search) {
        if (search == null || search.isBlank()) {
            return templateRepository.findAll();
        }
        return templateRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(search, search);
    }

    public Template findById(Long id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Template not found: " + id));
    }

    public Template create(TemplateRequest request) {
        if (templateRepository.existsByCode(request.code())) {
            throw new IllegalArgumentException("Template code already exists");
        }
        return templateRepository.save(toTemplate(new Template(), request));
    }

    public Template update(Long id, TemplateRequest request) {
        Template template = findById(id);
        templateRepository.findByCode(request.code())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Template code already exists");
                });
        return templateRepository.save(toTemplate(template, request));
    }

    public void delete(Long id) {
        templateRepository.delete(findById(id));
    }

    private Template toTemplate(Template template, TemplateRequest request) {
        template.setCode(request.code());
        template.setName(request.name());
        template.setOrganizationName(request.organizationName());
        template.setLayout(request.layout());
        template.setPrimaryColor(defaultValue(request.primaryColor(), "#0f766e"));
        template.setSecondaryColor(defaultValue(request.secondaryColor(), "#ccfbf1"));
        template.setTextColor(defaultValue(request.textColor(), "#111827"));
        template.setTagline(request.tagline());
        return template;
    }

    private String defaultValue(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
