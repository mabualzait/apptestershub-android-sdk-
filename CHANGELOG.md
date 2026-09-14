# Changelog

All notable changes to the AppTestersHub Android SDK will be documented in this file.

## [1.0.0] - 2024-10-20

### Added
- Initial release of AppTestersHub Android SDK
- Automatic screenshot capture functionality
- Email verification dialog
- Background screenshot upload
- SDK status checking
- Encrypted local storage
- Offline queue support
- Date validation (one screenshot per day)
- Device information collection
- Custom configuration support
- Debug logging support

### Features
- **Automatic Screenshot Capture**: Captures screenshots when app comes to foreground
- **Email Verification**: First-time dialog to verify tester email
- **Background Upload**: Uploads screenshots in background thread
- **Date Validation**: Ensures one screenshot per day
- **SDK Status Checking**: Respects app owner's SDK toggle
- **Offline Queue**: Queues uploads when offline, uploads when online
- **Encrypted Storage**: Secure local storage for sensitive data

### Requirements
- Minimum SDK: 21 (Android 5.0)
- Target SDK: 34
- Size: < 500KB

### Installation
- JitPack: `implementation 'com.github.apptestershub:sdk:1.0.0'`
- AAR: Download and add to `libs` folder

