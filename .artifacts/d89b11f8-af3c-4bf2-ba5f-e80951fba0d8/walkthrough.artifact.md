# Walkthrough - Student Institutional Tasks List Activity & Navigation

Implemented the student institutional tasks list screen (`TugasPelajarInstitusiActivity`) with filter tabs ("Semua", "Belum Dikerjakan", "Selesai") and an empty task list container (ready for database population), and wired up navigation from the "Tugas" tab in `DetailKelasPelajarInstitusiActivity` strictly for institutional students without modifying teacher UI.

## Changes Made

### Student Institutional Tasks List Feature

#### [NEW] [TugasPelajarInstitusiActivity.kt](file:///C:/Users/Huseinnn/AndroidStudioProjects/projectMobile/app/src/main/java/com/example/projectmobile/TugasPelajarInstitusiActivity.kt)
- Created `TugasPelajarInstitusiActivity` programmatically matching the UI mockup:
  - Header with back button (`‹`), title "Tugas", options menu (`⋮`).
  - Filter tabs ("Semua", "Belum Dikerjakan", "Selesai") with active state switching.
  - Empty task list container (left empty as requested for future automatic population from the student database).
  - Student bottom navigation bar with functional navigation.

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/Huseinnn/AndroidStudioProjects/projectMobile/app/src/main/AndroidManifest.xml)
- Registered `TugasPelajarInstitusiActivity`.

#### [MODIFY] [DetailKelasPelajarInstitusiActivity.kt](file:///C:/Users/Huseinnn/AndroidStudioProjects/projectMobile/app/src/main/java/com/example/projectmobile/DetailKelasPelajarInstitusiActivity.kt)
- Wired up the "Tugas" tab click listener to launch `TugasPelajarInstitusiActivity`.

## Verification Results

### Automated Tests
- Gradle build completed successfully: `gradle_build("app:assembleDebug")` returned status `Build finished successfully`.
