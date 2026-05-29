# Crew

A story-driven crew app built on a modern Kotlin Multiplatform setup.
This repo splits `auth`, `character creation`, and `home` flows into separate modules while sharing UI and business logic across Android and iOS.

## Screenshots

<table>
  <tr>
    <td align="center"><img src="./docs/screenshots/screenshot_login.jpeg" alt="Screenshot 01 - Login" width="250" /></td>
    <td align="center"><img src="./docs/screenshots/screenshot_register.jpeg" alt="Screenshot 02 - Register" width="250" /></td>
    <td align="center"><img src="./docs/screenshots/screenshot_email_verification.jpeg" alt="Screenshot 03 - E-Mail Verification" width="250" /></td>
  </tr>
  <tr>
    <td align="center"><img src="./docs/screenshots/screenshot_verification_success.jpeg" alt="Screenshot 04 - Verification Success" width="250" /></td>
    <td align="center"><img src="./docs/screenshots/screenshot_character_creation.jpeg" alt="Screenshot 05 - Character Creation" width="250" /></td>
    <td align="center"><img src="./docs/screenshots/screenshot_dashboard_empty_state.jpeg" alt="Screenshot 06 - Dashboard - Empty State" width="250" /></td>
  </tr>
  <tr>
    <td align="center"><img src="./docs/screenshots/screenshot_dashboard.jpeg" alt="Screenshot 07 - Dashboard" width="250" /></td>
    <td align="center"><img src="./docs/screenshots/screenshot_dispatch.jpeg" alt="Screenshot 08 - Dispatch" width="250" /></td>
    <td align="center"><img src="./docs/screenshots/screenshot_pub.jpeg" alt="Screenshot 09 - Pub" width="250" /></td>
  </tr>
</table>

## What This Project Is About

Looking at the structure, the app currently revolves around:

- sign up / login flows
- email verification and deep link handling
- avatar and trait-based character creation
- a dashboard / dispatch focused home experience
- a shared design system with feature-based modular architecture

## Architecture

The project leans on a clean, feature-first architecture where each domain area is split into `data`, `domain`, and `presentation` layers. That keeps responsibilities clear: `presentation` handles UI and state, `domain` holds the business rules and contracts, and `data` takes care of networking, persistence, and implementation details. In practice, this makes features easier to read in isolation and easier to change without dragging unrelated parts of the codebase with them.

On top of that, the repo uses a modular KMP setup with shared `core` modules for cross-cutting concerns such as design system components, common data utilities, and presentation helpers. The result is a structure that stays disciplined without feeling heavy: feature modules can move independently, shared code has an explicit home, and the app entry layer mostly focuses on wiring navigation, dependency injection, and platform-specific setup together.

## Tech Stack

- Kotlin Multiplatform
- Compose Multiplatform
- Compose Navigation
- Koin
- Ktor
- Room
- DataStore
- Ktlint
- Detekt

## Project Structure

```text
.
├── composeApp                # Main application entry point
├── core
│   ├── data                  # Shared data layer
│   ├── designsystem          # Theme and reusable UI components
│   ├── domain                # Domain models and contracts
│   └── presentation          # Shared presentation helpers
├── feature
│   ├── auth
│   │   ├── data
│   │   ├── domain
│   │   └── presentation
│   ├── character
│   │   ├── data
│   │   ├── domain
│   │   └── presentation
│   └── home
│       ├── data
│       ├── db
│       ├── domain
│       └── presentation
├── iosApp                    # iOS host app
└── build-logic               # Convention plugins
```

## Quick Start

What you need before running the project:

- JDK 17
- Android Studio
- Xcode

```bash
git clone <repo-url>
cd crew
```

## Running the App

### Android

To build a debug version:

```bash
./gradlew :composeApp:assembleDebug
```

If you prefer using the IDE, running the `composeApp` configuration is enough.

### iOS

Open the Xcode project:

```bash
open iosApp/iosApp.xcodeproj
```

Then run the app on a simulator or a physical device.

## Code Quality

This repo already includes a solid baseline for code quality:

```bash
./gradlew ktlintCheck
./gradlew detekt
./gradlew androidLint
./gradlew allTests
```

If you want a broader verification pass:

```bash
./gradlew check
```

## Release Note

Android release signing uses the following environment variables or Gradle properties:

- `CREW_STORE_FILE`
- `CREW_STORE_PASSWORD`
- `CREW_KEY_ALIAS`
- `CREW_KEY_PASSWORD`

If these values are missing, the project can still run normally in debug/development mode. Only release signing is skipped.

## Why This Setup Works

Because even when the product grows, the structure stays easy to reason about:

- `auth` lives in its own space
- `character creation` can evolve as a standalone feature
- `home` can expand with dashboard and dispatch style flows
- `core` keeps shared pieces from leaking everywhere

So adding new features does not immediately turn the repo into a maze.
