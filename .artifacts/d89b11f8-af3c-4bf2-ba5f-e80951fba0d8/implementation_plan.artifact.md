# Implementation Plan - Institutional Teacher "Buat Kuis" (Create Quiz) Activity & Navigation

Implement the "Buat Kuis" (Create Quiz) screen for institutional teachers, wire up the "Buat Kuis" tab in `DetailKelasActivity` to open `BuatKuisActivity`, and include flexible duration selection (minutes to hours), class selection, meeting selection, title, instructions with character count, minimum score, number of questions, file upload option, and action buttons ("Upload Kuis", "Kembali Draf").

## User Review Required

> [!IMPORTANT]
> - A new `BuatKuisActivity` will be created programmatically in Kotlin matching the provided UI mockup.
> - Clicking the "Buat Kuis" tab in `DetailKelasActivity` will launch `BuatKuisActivity`.
> - The UI includes: Header, form inputs for Class selection, Meeting selection, Quiz Title, Instructions (with 0/500 counter), flexible Duration selector (minutes to hours options), Minimum Score, Number of Questions, Dashed file upload card ("Upload file soal (opsional)"), action buttons ("Upload Kuis", "Kembali Draf"), and bottom navigation.

## Proposed Changes

### Buat Kuis Feature for Institutional Teachers

#### [NEW] [BuatKuisActivity.kt](file:///C:/Users/Huseinnn/AndroidStudioProjects/projectMobile/app/src/main/java/com/example/projectmobile/BuatKuisActivity.kt)
- Create `BuatKuisActivity` programmatically matching the UI mockup:
  - Header: Back button (`‹`), title "Buat Kuis", options menu (`⋮`).
  - Form fields (Pilih Kelas, Pertemuan ke-, Judul Kuis, Instruksi with 0/500 counter, Durasi Pengerjaan with flexible minute/hour options, Nilai Minimal, Jumlah Soal).
  - Dashed upload card for optional question files (PDF, DOCX, ZIP).
  - Action buttons ("Upload Kuis", "Kembali Draf") and bottom navigation bar.

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/Huseinnn/AndroidStudioProjects/projectMobile/app/src/main/AndroidManifest.xml)
- Register `BuatKuisActivity`.

#### [MODIFY] [DetailKelasActivity.kt](file:///C:/Users/Huseinnn/AndroidStudioProjects/projectMobile/app/src/main/java/com/example/projectmobile/DetailKelasActivity.kt)
- Wire up the "Buat Kuis" tab click listener to launch `BuatKuisActivity`.

## Verification Plan

### Automated Tests
- Run Gradle build: `gradle_build("app:assembleDebug")`.

### Manual Verification
- Deploy and test tapping the "Buat Kuis" tab in institutional class detail, verifying that `BuatKuisActivity` opens and all form inputs and action buttons work correctly.
