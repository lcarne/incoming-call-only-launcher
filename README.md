# CallOnly - Minimalist & Secure Android Launcher for Seniors

**CallOnly** is an ultra-simplified Open Source Android launcher designed specifically for elderly or vulnerable individuals. It transforms a modern Android smartphone into a secure, basic phone that prevents accidental misuse and confusion.

![License](https://img.shields.io/badge/license-MIT-blue.svg) ![Android](https://img.shields.io/badge/platform-Android-green.svg)

## 🎯 Goal

The goal of CallOnly is to **lock** the user into a trusted interface where they can only perform two actions:
1.  **View the Date and Time** (High visibility).
2.  **Receive calls** from a trusted list of favorites.

Everything else (settings, notifications, outgoing calls, other apps) is hidden or blocked to prevent the user from getting lost or falling victim to scams.

## ✨ Features

*   **Minimalist Interface**:
    *   Large digital clock and full date.
    *   No visible buttons or distractions on the home screen.
    *   High-contrast theme (White on Black) for maximum readability.
*   **Secure Call Screening**:
    *   **Incoming**: Only calls from "Favorite" contacts (configured in the app) are allowed to ring. Unknown numbers are silently rejected.
    *   **Outgoing**: Outgoing calls are not provided by the interface.
*   **True Kiosk Mode (Device Owner)**:
    *   **Strict Lockdown**: Prevents the use of system gestures (Home, Back, Recent Apps) to exit.
    *   **Status Bar Blocked**: Prevents swiping down to access Quick Settings or Notifications.
*   **Secure Administration**:
    *   Settings are protected by a specific gesture (hold Date for 60s) and a PIN code (`1234`).
    *   Authorized users can manage the Favorites list.

## 🛠️ Technical Stack

*   **Language**: Kotlin
*   **UI**: Jetpack Compose (Material3)
*   **Architecture**: MVVM + Hilt
*   **Data**: Room Database
*   **Security**: `DevicePolicyManager` (Device Owner mode) & `CallScreeningService`

## 🚀 Installation & Setup

### 1. Build & Install
1.  Clone this repository.
2.  Build the APK using Android Studio.
3.  Install it on the target device.

### 2. Set as Default Launcher
1.  Press the **Home** button on the device.
2.  Select **CallOnly** and choose **"Always"**.

### 3. 🛡️ Enable True Kiosk Mode (CRITICAL)
To prevent the user from exiting the app using system gestures (like swiping up), you **MUST** set the app as the **Device Owner**. This grants the app permission to fully lock the screen and status bar.

**Prerequisites**:
*   **Remove all Google Accounts** from the device first (`Settings > Accounts`). You can re-add them after this step if absolutely necessary, but it's recommended to keep the device "clean".
*   Enable **USB Debugging** in Developer Options.

**Run this command from your computer via ADB:**
```bash
adb shell dpm set-device-owner com.callonly.launcher/.receivers.CallOnlyAdminReceiver
```

*If successful, the app will instantly lock the status bar and navigation.*

### 4. Admin Access & Configuration
To access the Settings menu (to add contacts or unlock the device):
1.  **Press and HOLD** the **Date** text (e.g., "Monday 5 January") for **10 seconds**.
2.  Enter the PIN code: **1234**.
3.  You are now in the Admin Interface.
    *   **Manage Contacts**: Add trusted numbers here.
    *   **Unlock / Exit**: Press the **"Déverrouiller"** (Unlock) button to temporarily disable Kiosk mode and access system settings.

## 🚨 Emergency Unlock (How to Remove Kiosk Mode)
If you are locked out or need to uninstall the app and the in-app "Unlock" button is not accessible, you can forcibly remove the Device Owner status via ADB. This will immediately kill the Kiosk mode.

**Run this command via ADB:**
```bash
adb shell dpm remove-active-admin com.callonly.launcher/.receivers.CallOnlyAdminReceiver
```

## 📄 License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
