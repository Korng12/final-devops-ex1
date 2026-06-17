package com.example.id_card_system;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.id_card_system.dto.ProfileRequest;
import com.example.id_card_system.entity.Profile;
import com.example.id_card_system.entity.ProfileType;
import com.example.id_card_system.services.CardRenderingService;
import com.example.id_card_system.services.ProfileService;

@SpringBootTest
@ActiveProfiles("test")
class CardRenderingServiceTests {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private CardRenderingService cardRenderingService;

    @Test
    void rendersPreviewPdfQrAndBarcode() {
        Profile profile = profileService.create(new ProfileRequest(
                ProfileType.USER,
                "Lin Chen",
                "Operations",
                "Visitor",
                "lin@example.com",
                null,
                null,
                null,
                null,
                null,
                null,
                null));

        assertThat(cardRenderingService.previewHtml(profile)).contains("Lin Chen");
        assertThat(cardRenderingService.pdf(profile)).startsWith("%PDF".getBytes());
        assertThat(cardRenderingService.qrCode(profile)).isNotEmpty();
        assertThat(cardRenderingService.barcode(profile)).isNotEmpty();
    }
}
