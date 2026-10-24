#!/bin/bash
set -euo pipefail
cd "$(dirname "$0")/../.."
# Simulator-only framework, separate executable and identifier; no production Xcode target.
framework="screenshots/shared/build/bin/iosSimulatorArm64/debugFramework"
app="screenshots/build/ios/CrewScreenshots.app"
rm -rf "$app"
mkdir -p "$app"
# Static framework linking does not package Compose resources. Use the plugin's
# sync task with the same destination variables that Xcode normally supplies.
BUILT_PRODUCTS_DIR="$PWD/screenshots/build/ios" \
UNLOCALIZED_RESOURCES_FOLDER_PATH="CrewScreenshots.app" \
PLATFORM_NAME="iphonesimulator" ARCHS="arm64" \
./gradlew -PcrewScreenshots=true :screenshots:shared:linkDebugFrameworkIosSimulatorArm64 \
    :screenshots:shared:syncComposeResourcesForIos
cp screenshots/ios/Info.plist "$app/Info.plist"
xcrun --sdk iphonesimulator swiftc -parse-as-library \
    -target arm64-apple-ios18.5-simulator \
    -sdk "$(xcrun --sdk iphonesimulator --show-sdk-path)" \
    -F "$framework" -framework CrewScreenshots \
    -framework UIKit -framework SwiftUI -framework Metal -framework CoreText \
    -framework CoreGraphics -framework QuartzCore -framework Foundation \
    -lsqlite3 -lc++ screenshots/ios/Main.swift -o "$app/CrewScreenshotsApp"
test -f "$app/compose-resources/composeResources/crew.screenshots.generated.resources/files/event.png"
codesign --force --sign - "$app"
echo "Built $app"
