package com.example.id_card_system.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.id_card_system.entity.Template;
import com.example.id_card_system.repository.TemplateRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner defaultTemplate(TemplateRepository templateRepository) {
        return args -> {
            if (!templateRepository.existsByCode("DEFAULT")) {
                templateRepository.save(Template.builder()
                        .code("DEFAULT")
                        .name("Default Campus Card")
                        .organizationName("ID Card System")
                        .layout("VERTICAL")
                        .primaryColor("#0f766e")
                        .secondaryColor("#ccfbf1")
                        .textColor("#111827")
                        .tagline("Official Identity Card")
                        .build());
            }
        };
    }
}
