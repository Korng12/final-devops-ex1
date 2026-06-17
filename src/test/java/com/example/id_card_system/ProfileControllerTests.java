package com.example.id_card_system;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ProfileControllerTests {

    @Autowired
    private ObjectMapper objectMapper;

    @LocalServerPort
    private int port;

    @Test
    void createsProfileAndReturnsPreview() throws Exception {
        String body = """
                {
                  "type": "STUDENT",
                  "fullName": "Katherine Johnson",
                  "department": "Math",
                  "title": "Applied Mathematics",
                  "email": "katherine@example.com"
                }
                """;

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> createResponse = client.send(HttpRequest.newBuilder(uri("/api/profiles"))
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build(), HttpResponse.BodyHandlers.ofString());

        assertThat(createResponse.statusCode()).isEqualTo(200);
        Map<String, Object> profile = objectMapper.readValue(createResponse.body(), Map.class);
        assertThat(profile).containsEntry("fullName", "Katherine Johnson");
        assertThat((String) profile.get("registrationNumber")).contains("-MAT-");
        Integer id = (Integer) profile.get("id");

        HttpResponse<String> preview = client.send(HttpRequest.newBuilder(uri("/api/profiles/" + id + "/preview"))
                .GET()
                .build(), HttpResponse.BodyHandlers.ofString());
        assertThat(preview.statusCode()).isEqualTo(200);
        assertThat(preview.body()).contains("Katherine Johnson");

        HttpResponse<byte[]> pdf = client.send(HttpRequest.newBuilder(uri("/api/profiles/" + id + "/pdf"))
                .GET()
                .build(), HttpResponse.BodyHandlers.ofByteArray());
        assertThat(pdf.statusCode()).isEqualTo(200);
        assertThat(pdf.headers().firstValue("Content-Disposition")).hasValueSatisfying(value -> assertThat(value).contains(".pdf"));
    }

    private URI uri(String path) {
        return URI.create("http://localhost:" + port + path);
    }
}
