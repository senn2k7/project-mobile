# Walkthrough - Institutional Teacher "Buat Kuis" (Create Quiz) Activity & Navigation

Implemented the "Buat Kuis" (Create Quiz) activity for institutional teachers with flexible duration selection and wired up navigation from `DetailKelasActivity`.

## Changes Made

### Create Quiz Feature for Institutional Teachers

#### [NEW] [BuatKuisActivity.kt](file:///C:/Users/Huseinnn/AndroidStudioProjects/projectMobile/app/src/main/java/com/example/projectmobile/BuatKuisActivity.kt)
- Created `BuatKuisActivity` programmatically matching the UI mockup:
  - Header with back button (`‹`), title "Buat Kuis", and options menu (`⋮`).
  - Form fields:
    - **Pilih Kelas** (Select Class dropdown)
    - **Pertemuan ke-** (Meeting dropdown)
    - **Judul Kuis** (Quiz title input)
    - **Instruksi** (Multi-line instructions input with live `0/500` character counter)
    - **Durasi Pengerjaan** (Flexible duration selector with options ranging from minutes to hours: 5 menit, 10 menit, 15 menit, 20 menit, 30 menit, 45 menit, 1 jam, 1.5 jam, 2 jam)
    - **Nilai Minimal** (Minimum score input, default 70)
    - **Jumlah Soal** (Number of questions input, default 10)
  - **Tambah Soal** dashed upload card (`"Upload file soal (opsional)"`, `"Format: PDF, DOCX, ZIP"`).
  - Action buttons (`"Upload Kuis"`, `"Kembali Draf"`) and bottom navigation bar.

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/Huseinnn/AndroidStudioProjects/projectMobile/app/src/main/AndroidManifest.xml)
- Registered `BuatKuisActivity`.

#### [MODIFY] [DetailKelasActivity.kt](file:///C:/Users/Huseinnn/AndroidStudioProjects/projectMobile/app/src/main/java/com/example/projectmobile/DetailKelasActivity.kt)
- Wired up the "Buat Kuis" tab click listener to launch `BuatKuisActivity`.

## Verification Results

### Automated Tests
- Gradle build completed successfully: `gradle_build("app:assembleDebug")` returned status `Build finished successfully`.
