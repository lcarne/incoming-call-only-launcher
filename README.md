# Call Only Launcher – Minimalist & Secure Android Launcher

<p>
  <img src="images/app_icon.svg" alt="App icon" width="160"/>
</p>

Call Only Launcher is a minimalist open-source Android launcher for elderly or vulnerable users.
It allows incoming calls only from trusted contacts, outgoing calls are completely disabled.

All other features (notifications, settings, system UI) are hidden to ensure safety and simplicity.

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Android](https://img.shields.io/badge/platform-Android-green.svg)

---

## Purpose

Call Only Launcher locks the user into a simple interface that emphasizes two primary functions:

- View the date and time with a **large, high-contrast display**
- Receive **incoming calls only** from a curated list of trusted contacts (Favorites)

Other system features (notifications, outgoing dialer, settings, system UI) are hidden or restricted to prevent confusion and accidental misuse.

**Typical use cases include:**
- Elderly users who should not place accidental or emergency calls
- People with Alzheimer’s disease or cognitive impairment
- Patients in care facilities or hospitals
- Children or vulnerable individuals using a shared or dedicated device
- Situations where caregivers need full control over who can call the device

---

## Key Features

- **Senior-Centric UI**  
  Large digital clock, full date, and high-contrast theme. Redesigned call screens with extra-large text, vibrant action buttons, and a reassuring visual style.

- **Intelligent Audio Routing**  
  Incoming calls automatically start on **speakerphone** by default to assist users with hearing or dexterity challenges. Easily toggle between speaker and earpiece with large, clear buttons.

- **Safe Call Handling**  
  - Incoming calls ring only for contacts marked as Favorites.
  - unknown callers are silenced or rejected automatically.
  - **2-Tap Safety**: Hang-up and Refuse actions require two taps to prevent accidental termination of calls.

- **Kiosk / Device Owner support**  
  When set as Device Owner, the app can:
  - Disable the status bar
  - Block system navigation gestures
  - Prevent leaving the launcher

- **Protected Admin interface**  
  Hidden admin screen to manage contacts and device state.

- **Simple PIN access**  
  Admin access is protected by a PIN (default: `1234`) for quick caregiver access.

---

## Technical Stack

- **Language**: Kotlin  
- **UI**: Jetpack Compose (Material 3)  
- **Architecture**: MVVM + Hilt  
- **Storage**: Room Database  
- **Security**:
  - `DevicePolicyManager` (Device Owner / Kiosk mode)
  - `CallScreeningService` (incoming call filtering)

---

## Screenshots

> Clean, high-contrast UI designed for elderly and vulnerable users.

### Home & Incoming Call

<table>
  <tr>
    <td align="center">
      <img src="images/home.png" width="320" alt="Home screen"/><br/>
      <strong>Home screen</strong>
    </td>
    <td align="center">
      <img src="images/incoming_call.png" width="320" alt="Incoming call"/><br/>
      <strong>Incoming call</strong>
    </td>
  </tr>
</table>

---

### Admin & Contact Management

<table>
  <tr>
    <td align="center">
      <img src="images/admin.png" width="320" alt="Admin PIN"/><br/>
      <strong>Admin / PIN entry</strong>
    </td>
    <td align="center">
      <img src="images/contacts.png" width="320" alt="Contacts"/><br/>
      <strong>Favorite contacts</strong>
    </td>
  </tr>
</table>

---

### Settings

<table>
  <tr>
    <td align="center">
      <img src="images/settings_1.png" width="320" alt="Settings"/><br/>
      <strong>Settings</strong>
    </td>
    <td align="center">
      <img src="images/settings_2.png" width="320" alt="Settings"/><br/>
      <strong>Advanced options</strong>
    </td>
  </tr>
</table>

---

### Additional States

<table>
  <tr>
    <td align="center">
      <img src="images/home_ring_off.png" width="280" alt="Ring off"/><br/>
      <strong>Ringer disabled</strong>
    </td>
    <td align="center">
      <img src="images/home_night.png" width="280" alt="Night mode"/><br/>
      <strong>Night mode</strong>
    </td>
    <td align="center">
      <img src="images/ongoing_call.png" width="280" alt="Ongoing call"/><br/>
      <strong>Ongoing call</strong>
    </td>
  </tr>
</table>


---

## Installation & Setup

### Option 1 – Download the pre-built APK (recommended)

You can download a ready-to-install APK directly from GitHub Releases:

➡️ **https://github.com/lcarne/call-only-launcher/releases**

Each release includes:
- A signed APK
- Release notes

**Steps:**
1. Download the `.apk` file from the Releases page.
2. Copy it to the target Android device.
3. Allow installation from unknown sources if prompted.
4. Install the APK.

---

### Option 2 – Build from source

1. Clone this repository.
2. Open the project in Android Studio.
3. Build and install the APK on the target device.

---

## Set as Default Launcher

After installation:
1. Press the **Home** button.
2. Select **Call Only Launcher**.
3. Choose **Always** to make it the default launcher.

---

## Enable True Kiosk Mode (Device Owner)

For full lockdown (disable status bar, navigation, system gestures), set the app as **Device Owner**.

⚠️ **Warning**  
This action is irreversible without ADB access.

### Prerequisites
- Remove Google accounts from the device (recommended)
- Enable **USB debugging** in Developer Options

### ADB command
```bash
adb shell dpm set-device-owner com.callonly.launcher/.receivers.CallOnlyAdminReceiver
```

If successful, the launcher will be pinned and the status bar/navigation will be disabled according to device policy.

## Admin Access (How to open Admin screen)

The Admin interface is intentionally hidden to prevent accidental access by the end user.

- On the home screen, long-press the date/time area for 30 seconds to open the Admin entry point.
- Enter the default PIN: `1234` (this is used for demonstration; change it in a production deployment).

From the Admin interface you can:
- Add or remove Favorites (trusted contacts).
- Configure device state (Night mode, Clock color, Ringer volume).
- Temporarily unpin/unlock the device (an "Unlock" button is available in Admin Settings to exit Kiosk mode).

## Emergency Unlock / Remove Device Owner

If you cannot access the Admin unlock button, remove the Device Owner via ADB:

```bash
adb shell dpm remove-active-admin com.callonly.launcher/.receivers.CallOnlyAdminReceiver
```

## Notes & Implementation Details

- The app verifies the Admin PIN in code (`AdminViewModel` currently checks for `"1234"`).
- The Admin receiver is `com.callonly.launcher.receivers.CallOnlyAdminReceiver` and is declared in the manifest with `BIND_DEVICE_ADMIN` permission.
- Kiosk behavior (lock task packages, disabling the status bar) is controlled via `DevicePolicyManager` in `MainActivity`.

## Contributing

Contributions and corrections are welcome. Please open issues or pull requests for feature requests, fixes, or documentation updates.

## License

This project is licensed under the MIT License, see the [LICENSE](LICENSE) file.
