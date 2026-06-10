# Campsite Commander 🏕️

Campsite Commander is an Android application designed to help campers organize their trips by managing packing lists for gear and food supplies.

## Features
- **Item Management:** Add camping gear and food supplies to your list.
- **Categorization:** Easily distinguish between "Gear" and "Food" items.
- **Detailed Checklist:** Track your packing progress with an interactive checklist view.
- **Progress Tracking:** Real-time counter showing how many items have been packed.

## Technical Implementation
This project demonstrates fundamental Android development concepts using Jetpack Compose:
- **User Input:** Capturing data via `OutlinedTextField` and `RadioButton`.
- **Parallel Arrays:** Data is managed using synchronized `SnapshotStateList` objects for names, categories, and status.
- **Screen Navigation:** State-driven UI switching between adding items and viewing the checklist.
- **Loops:** Utilizing `itemsIndexed` and manual `for` loops to iterate through list data and calculate progress.

## How to Use
1. **Add Screen:** Enter the name of your item, select a category (Gear or Food), and tap "Add to List".
2. **Checklist Screen:** Switch to the checklist tab to see your items. Tap the checkbox as you pack each item.
