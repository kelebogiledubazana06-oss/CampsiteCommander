# Campsite Commander - Submission Documentation

## 1. Project Overview
**Campsite Commander** is a tactical camping inventory management application built with Jetpack Compose. It allows users to "enlist" equipment, track packing progress through a mission checklist, and view detailed "intelligence reports" on their gear. The app features a military-themed UI with dark mode support.

---

## 2. Pseudocode

### Main Application Logic (CampsiteApp)
```
INITIALIZE item lists (names, categories, quantities, comments, checked status)
SET currentScreen to SPLASH
IF screen is SPLASH:
    WAIT 2 seconds
    TRANSITION to HOME
ELSE:
    DISPLAY Scaffold with Bottom Navigation
    SWITCH based on currentScreen:
        CASE HOME: 
            CALCULATE packed progress
            SHOW Dashboard with "Enlist" and "Checklist" buttons
        CASE ADD_ITEM:
            CAPTURE user input for gear details
            VALIDATE inputs (not empty, valid quantity)
            IF valid: ADD to lists AND NAVIGATE to CHECKLIST
        CASE CHECKLIST:
            FILTER items based on Search Query or Category Chip
            DISPLAY items in a scrollable list
            IF checkbox clicked: TOGGLE checked status
            IF delete clicked: REMOVE item AND SHOW "Undo" snackbar
        CASE DETAIL:
            LOOP through all items
            DISPLAY informative cards with "Command Tips" based on category
```

---

## 3. Write-up (Technical Summary)

### Features & Implementation
- **State Management:** Utilized `mutableStateListOf` (SnapshotStateList) to ensure that changes to the inventory (adding, removing, or checking items) immediately trigger UI recompositions across all screens.
- **Navigation:** Implemented a manual navigation system using a `Screen` enum and `Crossfade` for smooth transitions between the Splash, Home, Add, Checklist, and Detail views.
- **UI/UX:** Adhered to Material 3 design principles using `Scaffold`, `NavigationBar`, and `Card` components. Custom military-themed styling was applied via the `CampsiteCommanderTheme`.
- **Validation:** The "AddItem" screen includes robust error handling to prevent empty entries or invalid quantities, ensuring data integrity.
- **User Feedback:** Integrated `Snackbar` with action labels (e.g., "Undo" for deletions, "View" for new additions) to provide a responsive user experience.

---

## 4. Referenced Comments (Code Snippets)

I have annotated the source code in `MainActivity.kt` with detailed comments. Key areas include:
- **Navigation Logic:** Lines 85-103 (Crossfade & Screen switching).
- **State Persistence:** Lines 47-61 (Initialization of SnapshotStateLists).
- **Input Validation:** Lines 312-335 (Handling errors in the Add Item form).
- **Undo Logic:** Lines 425-442 (Temporary data capture for snackbar-based restoration).

---

## 5. Screenshots
*Note: Screenshots are captured from the running emulator.*

- **Dashboard:** `screenshot_home.png`
- **Add Equipment:** `screenshot_add.png`
- **Checklist & Search:** `screenshot_checklist.png`
- **Intelligence Report:** `screenshot_detail.png`
- **Code Reference:** `screenshot_code_comments.png`
