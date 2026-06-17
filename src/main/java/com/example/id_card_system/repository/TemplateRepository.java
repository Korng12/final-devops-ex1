package com.example.id_card_system.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.id_card_system.entity.Template;

public interface TemplateRepository
        extends JpaRepository<Template, Long> {

    Optional<Template> findByCode(String code);

    boolean existsByCode(String code);

    List<Template> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(String name, String code);
}
