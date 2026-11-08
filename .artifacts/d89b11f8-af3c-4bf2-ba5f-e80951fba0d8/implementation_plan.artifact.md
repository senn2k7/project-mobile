# Implementation Plan - Institutional Teacher "Presensi Manual" (Manual Attendance) Activity & Navigation

Implement the "Presensi Manual" (Manual Attendance) screen for institutional teachers, wire up the "Presensi Manual" button in `DetailKelasActivity` to open `PresensiManualActivity`, with class/meeting selectors, a search bar, an empty student list container (to be populated from the database later), attendance legend indicators (H, A, S, I), and the "Akhiri Presensi" action button.

## User Review Required

> [!IMPORTANT]
> - A new `PresensiManualActivity` will be created programmatically in Kotlin matching the provided UI mockup.
> - Clicking "Presensi Manual" in `DetailKelasActivity` will launch `PresensiManualActivity`.
> - The UI includes: Header, Class selector dropdown, Meeting selector dropdown ("Pertemuan ke-"), Search bar ("Cari nama pelajar..."), an empty student list container (ready for future database binding), Attendance status legend (Hadir, Alasan, Sakit, Izin), and the "Akhiri Presensi" button.

## Proposed Changes

### Presensi Manual Feature for Institutional Teachers

#### [NEW] [PresensiManualActivity.kt](file:///C:/Users/Huseinnn/AndroidStudioProjects/projectMobile/app/src/main/java/com/example/projectmobile/PresensiManualActivity.kt)
- Create `PresensiManualActivity` programmatically matching the UI mockup:
  - Header: Back button (`‹`), title "Presensi Manual", options menu (`⋮`).
  - Class selector dropdown and meeting selector dropdown ("Pertemuan ke-").
  - Search bar ("Cari nama pelajar...").
  - Empty student list container (as requested, left empty for database population).
  - Legend row (H: Hadir, A: Alasan, S: Sakit, I: Izin).
  - Bottom action button ("Akhiri Presensi").

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/Huseinnn/AndroidStudioProjects/projectMobile/app/src/main/AndroidManifest.xml)
- Register `PresensiManualActivity`.

#### [MODIFY] [DetailKelasActivity.kt](file:///C:/Users/Huseinnn/AndroidStudioProjects/projectMobile/app/src/main/java/com/example/projectmobile/DetailKelasActivity.kt)
- Wire up the "Presensi Manual" button click listener to launch `PresensiManualActivity`.

## Verification Plan

### Automated Tests
- Run Gradle build: `gradle_build("app:assembleDebug")`.

### Manual Verification
- Deploy and test tapping "Presensi Manual" in institutional class detail, verifying that `PresensiManualActivity` opens with all form elements, empty student list, and action button.
