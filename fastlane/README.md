fastlane documentation
----

# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```sh
xcode-select --install
```

For _fastlane_ installation instructions, see [Installing _fastlane_](https://docs.fastlane.tools/#installing-fastlane)

# Available Actions

### bump

```sh
[bundle exec] fastlane bump
```

Build number'ı artırır. Marketing version için: fastlane bump version:1.1

### ios_upload

```sh
[bundle exec] fastlane ios_upload
```

Archive alır ve App Store Connect'e yükler (bump yapmaz)

### android_apk

```sh
[bundle exec] fastlane android_apk
```

İmzalı release APK üretir

### release

```sh
[bundle exec] fastlane release
```

Bump + iOS yükleme + Android APK

----

This README.md is auto-generated and will be re-generated every time [_fastlane_](https://fastlane.tools) is run.

More information about _fastlane_ can be found on [fastlane.tools](https://fastlane.tools).

The documentation of _fastlane_ can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
