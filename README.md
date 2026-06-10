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

##Screenshots
-theme of the app: <img width="865" height="929" alt="image" src="https://github.com/user-attachments/assets/9e6268bc-7192-4dcb-836d-43ae5a839df6" /> handles the colours of the app
-manifest: <img width="841" height="696" alt="image" src="https://github.com/user-attachments/assets/531c2580-91dd-4d57-87ab-194b5184fcfe" /> project map
-mainactivity: <img width="1400" height="913" alt="image" src="https://github.com/user-attachments/assets/bb9301b0-90c2-478a-ad6e-781d69b89484" /> the heart of the app
-color.kt: <img width="516" height="495" alt="image" src="https://github.com/user-attachments/assets/83c1f5b1-e1f7-4b20-8bcf-e86a4714517d" /> look and feel of the app
