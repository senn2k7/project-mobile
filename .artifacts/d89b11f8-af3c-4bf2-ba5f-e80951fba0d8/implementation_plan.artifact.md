# Implementation Plan - Student Institutional Tasks List Activity & Navigation

Implement the student institutional tasks list screen (`TugasPelajarInstitusiActivity`) matching the UI mockup with empty task container (ready for database population), filter tabs ("Semua", "Belum Dikerjakan", "Selesai"), and wire up the "Tugas" tab in `DetailKelasPelajarInstitusiActivity` to open it, strictly for institutional students without affecting teacher UI.

## User Review Required

> [!IMPORTANT]
> - A new `TugasPelajarInstitusiActivity` will be created programmatically in Kotlin matching the student tasks list UI mockup.
> - Clicking the "Tugas" tab in `DetailKelasPelajarInstitusiActivity` will launch `TugasPelajarInstitusiActivity`.
> - The UI includes: Header ("Tugas"), filter tabs ("Semua", "Belum Dikerjakan", "Selesai"), an empty task list container (as requested, left empty for database population), and student bottom navigation.

## Proposed Changes

### Student Institutional Tasks List Feature

#### [NEW] [TugasPelajarInstitusiActivity.kt](file:///C:/Users/Huseinnn/AndroidStudioProjects/projectMobile/app/src/main/java/com/example/projectmobile/TugasPelajarInstitusiActivity.kt)
- Create `TugasPelajarInstitusiActivity` programmatically matching the UI mockup:
  - Header with back button (`‹`), title "Tugas", options menu (`⋮`).
  - Filter tabs ("Semua", "Belum Dikerjakan", "Selesai").
  - Empty task list container (left empty for future database population).
  - Student bottom navigation bar.

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/Huseinnn/AndroidStudioProjects/projectMobile/app/src/main/AndroidManifest.xml)
- Register `TugasPelajarInstitusiActivity`.

#### [MODIFY] [DetailKelasPelajarInstitusiActivity.kt](file:///C:/Users/Huseinnn/AndroidStudioProjects/projectMobile/app/src/main/java/com/example/projectmobile/DetailKelasPelajarInstitusiActivity.kt)
- Wire up the "Tugas" tab click listener to launch `TugasPelajarInstitusiActivity`.

## Verification Plan

### Automated Tests
- Run Gradle build: `gradle_build("app:assembleDebug")`.

### Manual Verification
- Deploy and test tapping the "Tugas" tab in `DetailKelasPelajarInstitusiActivity`, verifying that `TugasPelajarInstitusiActivity` opens with filter tabs and an empty task list, without touching teacher UI.
