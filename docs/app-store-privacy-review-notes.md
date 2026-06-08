# App Store Privacy Review Notes

These notes map the current Crew app code to App Store Connect privacy disclosures. They are not a replacement for legal review, but they should help keep the Privacy Policy, App Privacy labels, and review notes consistent.

## App Privacy Summary

Crew collects data from the app. The collected data is linked to the user's identity because it is tied to the user's account, user ID, event participation, tickets, and game participation.

Current code review did not find third-party advertising SDKs, third-party analytics SDKs, App Tracking Transparency usage, IDFA usage, location permission, contacts permission, microphone permission, HealthKit, or payment-card collection.

## Suggested App Store Connect Data Types

Use purpose: App Functionality unless noted otherwise. Use "Not used for tracking" for all listed types based on the current codebase.

| App Store data type | Crew examples | Linked to user | Tracking |
| --- | --- | --- | --- |
| Contact Info - Name | Full name | Yes | No |
| Contact Info - Email Address | Account email, verification, password reset | Yes | No |
| Identifiers - User ID | Backend user ID, participant ID | Yes | No |
| Identifiers - Device ID | Random app installation ID sent during registration | Yes | No |
| User Content - Photos or Videos | Optional profile picture upload | Yes | No |
| User Content - Gameplay Content | Multiplayer matching, invites, match results, task state, scoreboards | Yes | No |
| Purchases - Purchase History | Event ticket records, price, purchased/created timestamp, ticket status | Yes | No |
| Other Data - Other Data Types | Date of birth, gender, event IDs, check-in codes, QR tokens, attendance state | Yes | No |
| Diagnostics - Other Diagnostic Data | Only include if backend or operational systems retain app/server error logs or request diagnostics | Usually yes | No |

## Data Types Not Evident In Current Code

Do not declare these unless the backend, a release-only SDK, or App Store Connect configuration collects them outside this repository:

- Precise Location
- Coarse Location
- Contacts
- Audio Data
- Health
- Fitness
- Browsing History
- Search History
- Payment Info
- Credit Info
- Third-party advertising data
- Data used for tracking

## Review Checks Before Submission

- App Store Connect must include a publicly accessible Privacy Policy URL.
- The app must include an easily accessible Privacy Policy link inside the app, per App Review Guideline 5.1.1(i).
- Because the app supports account creation, confirm there is an in-app account deletion flow before review, per App Review Guideline 5.1.1(v).
- The iOS app uses `NSUserDefaults` for the installation ID. Confirm the submitted app bundle includes a valid `PrivacyInfo.xcprivacy` required-reason API declaration for user defaults.
- Confirm the contact email in `docs/privacy-policy.md` is the public support/privacy email you want users and Apple reviewers to use.
- Host `docs/privacy-policy.md` as a public web page before entering the Privacy Policy URL in App Store Connect.
