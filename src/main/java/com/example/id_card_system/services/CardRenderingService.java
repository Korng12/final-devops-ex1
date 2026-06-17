package com.example.id_card_system.services;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.id_card_system.entity.BarcodeType;
import com.example.id_card_system.entity.Profile;
import com.example.id_card_system.entity.Template;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.TextAlignment;

@Service
public class CardRenderingService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    private final String verificationBaseUrl;

    public CardRenderingService(@Value("${idcard.verification-base-url:http://localhost:9090/api/profiles/verify}") String verificationBaseUrl) {
        this.verificationBaseUrl = verificationBaseUrl;
    }

    public String previewHtml(Profile profile) {
        Template template = templateOrDefault(profile);
        String qrData = verificationUrl(profile);
        return """
                <!doctype html>
                <html>
                <head>
                  <meta charset="utf-8">
                  <style>
                    body { font-family: Arial, sans-serif; background: #f3f4f6; padding: 24px; }
                    .card { width: 360px; min-height: 540px; border-radius: 14px; overflow: hidden; background: white; box-shadow: 0 12px 30px rgba(0,0,0,.14); color: %s; }
                    .top { background: %s; color: white; padding: 22px; text-align: center; }
                    .org { font-size: 18px; font-weight: 700; }
                    .tag { font-size: 12px; opacity: .9; margin-top: 4px; }
                    .body { padding: 22px; }
                    .photo { width: 112px; height: 112px; border-radius: 10px; background: %s; margin: 0 auto 16px; display: grid; place-items: center; color: %s; font-weight: 700; }
                    h1 { font-size: 24px; margin: 0; text-align: center; }
                    .title { text-align: center; margin: 6px 0 18px; color: #4b5563; }
                    .row { display: flex; justify-content: space-between; gap: 18px; border-top: 1px solid #e5e7eb; padding: 10px 0; font-size: 14px; }
                    .label { color: #6b7280; }
                    .value { font-weight: 700; text-align: right; }
                    .codes { display: flex; justify-content: space-between; align-items: end; margin-top: 16px; }
                    .qr { width: 90px; height: 90px; background: %s; color: %s; display: grid; place-items: center; font-size: 11px; text-align: center; padding: 6px; box-sizing: border-box; }
                    .bar { font-family: monospace; font-size: 13px; letter-spacing: 1px; }
                  </style>
                </head>
                <body>
                  <article class="card">
                    <section class="top">
                      <div class="org">%s</div>
                      <div class="tag">%s</div>
                    </section>
                    <section class="body">
                      <div class="photo">%s</div>
                      <h1>%s</h1>
                      <div class="title">%s</div>
                      %s
                      <div class="codes">
                        <div class="qr">QR<br>%s</div>
                        <div class="bar">||||| %s |||||</div>
                      </div>
                    </section>
                  </article>
                </body>
                </html>
                """.formatted(
                escape(template.getTextColor()),
                escape(template.getPrimaryColor()),
                escape(template.getSecondaryColor()),
                escape(template.getPrimaryColor()),
                escape(template.getSecondaryColor()),
                escape(template.getPrimaryColor()),
                escape(defaultText(template.getOrganizationName(), "ID Card System")),
                escape(defaultText(template.getTagline(), "Official Identity Card")),
                initials(profile.getFullName()),
                escape(profile.getFullName()),
                escape(defaultText(profile.getTitle(), profile.getType().name())),
                detailRows(profile),
                escape(qrData),
                escape(barcodeValue(profile)));
    }

    public byte[] pdf(Profile profile) {
        return pdf(List.of(profile));
    }

    public byte[] pdf(List<Profile> profiles) {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PdfDocument pdf = new PdfDocument(new PdfWriter(output));
            Document document = new Document(pdf, PageSize.A6);
            for (int i = 0; i < profiles.size(); i++) {
                if (i > 0) {
                    document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                }
                addProfilePage(document, profiles.get(i));
            }
            document.close();
            return output.toByteArray();
        } catch (Exception ex) {
            throw new IllegalStateException("Could not render PDF", ex);
        }
    }

    public byte[] qrCode(Profile profile) {
        return png(verificationUrl(profile), BarcodeFormat.QR_CODE, 240, 240);
    }

    public byte[] barcode(Profile profile) {
        BarcodeFormat format = profile.getBarcodeType() == BarcodeType.EAN_13 ? BarcodeFormat.EAN_13 : BarcodeFormat.CODE_128;
        return png(barcodeValue(profile), format, 360, 110);
    }

    public String verificationUrl(Profile profile) {
        return verificationBaseUrl + "/" + profile.getUuid();
    }

    private void addProfilePage(Document document, Profile profile) {
        Template template = templateOrDefault(profile);
        DeviceRgb primary = color(template.getPrimaryColor());
        document.add(new Paragraph(defaultText(template.getOrganizationName(), "ID Card System"))
                .setFontSize(18)
                .setBold()
                .setFontColor(primary)
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph(defaultText(template.getTagline(), "Official Identity Card"))
                .setFontSize(9)
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph(profile.getFullName()).setFontSize(20).setBold().setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph(defaultText(profile.getTitle(), profile.getType().name())).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Registration: " + profile.getRegistrationNumber()));
        document.add(new Paragraph("Department: " + defaultText(profile.getDepartment(), "N/A")));
        document.add(new Paragraph("Email: " + defaultText(profile.getEmail(), "N/A")));
        document.add(new Paragraph("Phone: " + defaultText(profile.getPhone(), "N/A")));
        document.add(new Paragraph("Valid: " + format(profile.getIssueDate()) + " to " + format(profile.getExpiryDate())));
        document.add(new Image(ImageDataFactory.create(qrCode(profile))).setWidth(90).setHeight(90));
        document.add(new Image(ImageDataFactory.create(barcode(profile))).setWidth(210).setHeight(64));
    }

    private byte[] png(String value, BarcodeFormat format, int width, int height) {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            BitMatrix matrix = new MultiFormatWriter().encode(value, format, width, height);
            MatrixToImageWriter.writeToStream(matrix, "PNG", output);
            return output.toByteArray();
        } catch (Exception ex) {
            throw new IllegalStateException("Could not render code image", ex);
        }
    }

    private String barcodeValue(Profile profile) {
        if (profile.getBarcodeType() == BarcodeType.EAN_13) {
            long value = Math.abs(profile.getRegistrationNumber().getBytes(StandardCharsets.UTF_8).hashCode());
            return String.format("590%09d", value % 1_000_000_000L);
        }
        return profile.getRegistrationNumber();
    }

    private Template templateOrDefault(Profile profile) {
        if (profile.getTemplate() != null) {
            return profile.getTemplate();
        }
        return Template.builder()
                .organizationName("ID Card System")
                .primaryColor("#0f766e")
                .secondaryColor("#ccfbf1")
                .textColor("#111827")
                .tagline("Official Identity Card")
                .build();
    }

    private String detailRows(Profile profile) {
        return row("Type", profile.getType().name())
                + row("Reg No", profile.getRegistrationNumber())
                + row("Department", defaultText(profile.getDepartment(), "N/A"))
                + row("Email", defaultText(profile.getEmail(), "N/A"))
                + row("Phone", defaultText(profile.getPhone(), "N/A"))
                + row("Valid Until", format(profile.getExpiryDate()));
    }

    private String row(String label, String value) {
        return "<div class=\"row\"><span class=\"label\">" + escape(label) + "</span><span class=\"value\">"
                + escape(value) + "</span></div>";
    }

    private DeviceRgb color(String hex) {
        String clean = defaultText(hex, "#0f766e").replace("#", "");
        return new DeviceRgb(
                Integer.parseInt(clean.substring(0, 2), 16),
                Integer.parseInt(clean.substring(2, 4), 16),
                Integer.parseInt(clean.substring(4, 6), 16));
    }

    private String initials(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return "ID";
        }
        String[] parts = fullName.trim().split("\\s+");
        String first = parts[0].substring(0, 1);
        String last = parts.length > 1 ? parts[parts.length - 1].substring(0, 1) : "";
        return (first + last).toUpperCase();
    }

    private String format(java.time.LocalDate date) {
        return date == null ? "N/A" : DATE_FORMAT.format(date);
    }

    private String defaultText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String escape(String value) {
        return defaultText(value, "").replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
