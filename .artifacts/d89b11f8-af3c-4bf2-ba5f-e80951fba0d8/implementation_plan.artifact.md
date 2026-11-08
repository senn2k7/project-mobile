# Implementation Plan - Bottom Navigation Bar Functionality Across Activities

Implement click handlers for all items in the bottom navigation bar (`createBottomNav`) across institutional teacher activities so that tapping icons (Beranda, Kelas, Tugas, AI, Profil) navigates to the respective screens or handles actions smoothly.

## User Review Required

> [!IMPORTANT]
> - Add functional click listeners to each bottom navigation item across activities (`KelasSayaActivity`, `DetailKelasActivity`, `TugasActivity`, `DetailTugasActivity`, `PengumpulanTugasActivity`, `BuatKuisActivity`, `NilaiHasilKuisActivity`, `DetailHasilKuisActivity`, `DiskusiActivity`, `PresensiManualActivity`).
> - Tapping **Beranda** (🏠) -> goes to `HomeActivity`.
> - Tapping **Kelas** (📚) -> goes to `KelasSayaActivity`.
> - Tapping **Tugas / Aktivitas** (📋) -> goes to `TugasActivity`.
> - Tapping **AI** (🤖) or **Profil** (👤) -> provides navigation or responsive feedback (Toast/intent).

## Proposed Changes

### Bottom Navigation Wiring

#### [MODIFY] [KelasSayaActivity.kt](file:///C:/Users/Huseinnn/AndroidStudioProjects/projectMobile/app/src/main/java/com/example/projectmobile/KelasSayaActivity.kt)
#### [MODIFY] [DetailKelasActivity.kt](file:///C:/Users/Huseinnn/AndroidStudioProjects/projectMobile/app/src/main/java/com/example/projectmobile/DetailKelasActivity.kt)
#### [MODIFY] [TugasActivity.kt](file:///C:/Users/Huseinnn/AndroidStudioProjects/projectMobile/app/src/main/java/com/example/projectmobile/TugasActivity.kt)
#### [MODIFY] [DetailTugasActivity.kt](file:///C:/Users/Huseinnn/AndroidStudioProjects/projectMobile/app/src/main/java/com/example/projectmobile/DetailTugasActivity.kt)
#### [MODIFY] [PengumpulanTugasActivity.kt](file:///C:/Users/Huseinnn/AndroidStudioProjects/projectMobile/app/src/main/java/com/example/projectmobile/PengumpulanTugasActivity.kt)
#### [MODIFY] [BuatKuisActivity.kt](file:///C:/Users/Huseinnn/AndroidStudioProjects/projectMobile/app/src/main/java/com/example/projectmobile/BuatKuisActivity.kt)
#### [MODIFY] [NilaiHasilKuisActivity.kt](file:///C:/Users/Huseinnn/AndroidStudioProjects/projectMobile/app/src/main/java/com/example/projectmobile/NilaiHasilKuisActivity.kt)
#### [MODIFY] [DetailHasilKuisActivity.kt](file:///C:/Users/Huseinnn/AndroidStudioProjects/projectMobile/app/src/main/java/com/example/projectmobile/DetailHasilKuisActivity.kt)
#### [MODIFY] [DiskusiActivity.kt](file:///C:/Users/Huseinnn/AndroidStudioProjects/projectMobile/app/src/main/java/com/example/projectmobile/DiskusiActivity.kt)
#### [MODIFY] [PresensiManualActivity.kt](file:///C:/Users/Huseinnn/AndroidStudioProjects/projectMobile/app/src/main/java/com/example/projectmobile/PresensiManualActivity.kt)

- Update `createBottomNav()` in each activity to accept click actions and handle navigation:
  - 🏠 Beranda -> `HomeActivity`
  - 📚 Kelas -> `KelasSayaActivity`
  - 📋 Tugas -> `TugasActivity`
  - 🤖 AI -> Toast/Feedback
  - 👤 Profil -> Toast/Feedback

## Verification Plan

### Automated Tests
- Run Gradle build: `gradle_build("app:assembleDebug")`.

### Manual Verification
- Test tapping each bottom navigation item across different screens to ensure correct navigation and responsiveness.
