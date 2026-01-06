# CallOnly - Minimalist & Secure Android Launcher

CallOnly is an ultra-simplified open-source Android launcher designed for elderly or vulnerable users. It converts an Android device into a tightly controlled phone interface focused on readability and safe incoming calls.

![License](https://img.shields.io/badge/license-MIT-blue.svg) ![Android](https://img.shields.io/badge/platform-Android-green.svg)

## Purpose

CallOnly locks the user into a simple interface that emphasizes two primary functions:
- View the date and time with large, high-contrast display.
- Receive incoming calls from a curated list of trusted contacts (Favorites).

Other system features (notifications, outgoing dialer, settings) are hidden or restricted to prevent confusion and accidental misuse.

## Key Features

- **Minimal UI**: Large digital clock, full date, and a high-contrast theme for visibility.
- **Secure call handling**: Incoming calls only ring for contacts marked as Favorites; unknown callers can be rejected/silenced.
- **Kiosk / Device Owner support**: When set as Device Owner the app can disable the status bar and block system gestures to prevent leaving the app.
- **Admin interface**: A protected Admin screen to manage favorites and device settings.
- **Simple PIN**: Admin access is protected by a PIN (default `1234`) for quick caregiver access.

## Technical Stack

- Language: Kotlin
- UI: Jetpack Compose (Material3)
- Architecture: MVVM + Hilt
- Storage: Room Database
- Security: DevicePolicyManager (Device Owner) and CallScreeningService

## Installation & Setup

1. Clone this repository.
2. Build the APK in Android Studio and install it on the target device.

### Set as Default Launcher

After installation, press the Home button and select CallOnly, choosing "Always" to make it the default launcher.

### Enable True Kiosk Mode (Device Owner)

For full lockdown (prevent status bar, system gestures, etc.) set the app as Device Owner. NOTE: this action is irreversible without ADB unless you remove the admin status.

Prerequisites:
- Remove Google accounts from the device (recommended).
- Enable USB debugging in Developer Options.

Run from your computer (ADB):

```bash
adb shell dpm set-device-owner com.callonly.launcher/.receivers.CallOnlyAdminReceiver
```

If successful, the launcher will be pinned and the status bar/navigation will be disabled according to device policy.

## Admin Access (How to open Admin screen)

The Admin interface is intentionally hidden to prevent accidental access by the end user.

- On the home screen, long-press the date/time area for 3 seconds to open the Admin entry point.
- Enter the default PIN: `1234` (this is used for demonstration; change it in a production deployment).

From the Admin interface you can:
- Add or remove Favorites (trusted contacts).
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

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file.
