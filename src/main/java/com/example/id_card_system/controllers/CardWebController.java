package com.example.id_card_system.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.id_card_system.dto.ProfileForm;
import com.example.id_card_system.entity.BarcodeType;
import com.example.id_card_system.entity.Profile;
import com.example.id_card_system.entity.ProfileType;
import com.example.id_card_system.services.CardRenderingService;
import com.example.id_card_system.services.ProfileService;
import com.example.id_card_system.services.TemplateService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CardWebController {

    private final ProfileService profileService;
    private final TemplateService templateService;
    private final CardRenderingService cardRenderingService;

    @GetMapping({"/", "/cards"})
    public String generator(Model model,
            @RequestParam(required = false) ProfileType type,
            @RequestParam(required = false) String search) {
        model.addAttribute("profileForm", new ProfileForm());
        model.addAttribute("profiles", profileService.findAll(type, search));
        model.addAttribute("templates", templateService.findAll(null));
        model.addAttribute("profileTypes", ProfileType.values());
        model.addAttribute("barcodeTypes", BarcodeType.values());
        model.addAttribute("selectedType", type);
        model.addAttribute("search", search);
        return "cards/index";
    }

    @PostMapping("/cards")
    public String create(@ModelAttribute ProfileForm profileForm,
            @RequestParam(required = false) MultipartFile photo,
            RedirectAttributes redirectAttributes) {
        try {
            Profile profile = profileService.create(profileForm.toRequest());
            if (photo != null && !photo.isEmpty()) {
                profileService.attachPhoto(profile.getId(), photo);
            }
            redirectAttributes.addFlashAttribute("success", "ID card generated for " + profile.getFullName());
            return "redirect:/cards/" + profile.getId();
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/cards";
        }
    }

    @GetMapping("/cards/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Profile profile = profileService.findById(id);
        model.addAttribute("profile", profile);
        model.addAttribute("previewHtml", cardRenderingService.previewHtml(profile));
        return "cards/detail";
    }

    @PostMapping("/cards/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        profileService.delete(id);
        redirectAttributes.addFlashAttribute("success", "ID card deleted");
        return "redirect:/cards";
    }
}
