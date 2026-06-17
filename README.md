# ID Card System

Spring Boot application for managing ID cards for students, employees, and general users.

## Features

- CRUD APIs for profile data used on ID cards
- MySQL persistence with Spring Data JPA
- Local JPEG/PNG photo upload with size and content-type validation
- Default ID card template model for HTML preview and PDF rendering
- Live HTML card preview endpoint
- Unique registration numbers in `YEAR-DEPT-###` and `YEAR-EMP-DEPT-###` formats
- PDF export for a single ID card or a batch of profile IDs
- QR code generation for verification URLs
- Code-128 and EAN-13 barcode image generation

## Tech Stack

- Java 25
- Spring Boot 4.1
- Spring Data JPA
- MySQL
- Thymeleaf dependency for template support
- iText for PDF generation
- ZXing for QR code and barcode generation
- H2 for tests

## Configuration

The app defaults to:

```properties
server.port=9090
spring.datasource.url=jdbc:mysql://localhost:3309/idcard_db
spring.datasource.username=kong
spring.datasource.password=123321
```

You can override these with environment variables:

```bash
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/idcard_db
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=secret
IDCARD_PHOTO_STORAGE_DIR=uploads/photos
IDCARD_VERIFICATION_BASE_URL=http://localhost:9090/api/profiles/verify
```

## Run

```bash
./gradlew bootRun
```

## Test

```bash
./gradlew test
```

## Main API Endpoints

- `GET /api/profiles`
- `POST /api/profiles`
- `GET /api/profiles/{id}`
- `PUT /api/profiles/{id}`
- `DELETE /api/profiles/{id}`
- `POST /api/profiles/{id}/photo`
- `GET /api/profiles/{id}/preview`
- `GET /api/profiles/{id}/pdf`
- `POST /api/profiles/batch-pdf`
- `GET /api/profiles/{id}/qr.png`
- `GET /api/profiles/{id}/barcode.png`
- `GET /api/profiles/verify/{uuid}`
- `GET /api/templates`
- `POST /api/templates`
- `GET /api/templates/{id}`
- `PUT /api/templates/{id}`
- `DELETE /api/templates/{id}`
