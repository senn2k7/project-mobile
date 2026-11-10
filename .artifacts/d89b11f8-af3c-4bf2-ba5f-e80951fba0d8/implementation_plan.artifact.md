# Implementation Plan - Student Institutional Discussion Activity & Navigation

Implement the student institutional discussion screen (`DiskusiPelajarInstitusiActivity`) matching the UI mockup with an empty chat container (ready for dynamic message bubbles from the database), and wire up the "Diskusi" tab in `DetailKelasPelajarInstitusiActivity` to open it, strictly for institutional students without modifying teacher UI.

## User Review Required

> [!IMPORTANT]
> - A new `DiskusiPelajarInstitusiActivity` will be created programmatically in Kotlin matching the student discussion UI mockup.
> - Clicking the "Diskusi" tab in `DetailKelasPelajarInstitusiActivity` will launch `DiskusiPelajarInstitusiActivity`.
> - The UI includes: Header ("Diskusi Pemrograman Mobile", "28 peserta"), an empty chat container area (ready for future database message population), and a bottom message input bar (emoji, attachment, "Tulis pesan...", send button).

## Proposed Changes

### Student Institutional Discussion Feature

#### [NEW] [DiskusiPelajarInstitusiActivity.kt](file:///C:/Users/Huseinnn/AndroidStudioProjects/projectMobile/app/src/main/java/com/example/projectmobile/DiskusiPelajarInstitusiActivity.kt)
- Create student discussion screen matching the UI mockup:
  - Header with back button (`‹`), title "Diskusi Pemrograman Mobile", subtitle "28 peserta", options menu (`⋮`).
  - Empty chat message container (ready for database message bubbles).
  - Bottom message input bar (emoji icon, attachment icon, "Tulis pesan..." input field, send button).

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/Huseinnn/AndroidStudioProjects/projectMobile/app/src/main/AndroidManifest.xml)
- Register `DiskusiPelajarInstitusiActivity`.

#### [MODIFY] [DetailKelasPelajarInstitusiActivity.kt](file:///C:/Users/Huseinnn/AndroidStudioProjects/projectMobile/app/src/main/java/com/example/projectmobile/DetailKelasPelajarInstitusiActivity.kt)
- Wire up the "Diskusi" tab click listener to launch `DiskusiPelajarInstitusiActivity`.

## Verification Plan

### Automated Tests
- Run Gradle build: `gradle_build("app:assembleDebug")`.

### Manual Verification
- Deploy and test tapping the "Diskusi" tab in `DetailKelasPelajarInstitusiActivity`, verifying that `DiskusiPelajarInstitusiActivity` opens with the correct title, empty chat area, and message input bar, without touching teacher UI.
