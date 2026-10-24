#!/bin/bash
set -euo pipefail
serial="${1:-emulator-5554}"
[[ "$serial" == emulator-* ]] || { echo 'Use an emulator, never a personal device.' >&2; exit 1; }
adb -s "$serial" wait-for-device
# ADB becomes available before Android's settings service on a cold boot.
for attempt in {1..180}; do
    [[ "$(adb -s "$serial" shell getprop sys.boot_completed | tr -d '\r')" == 1 ]] && break
    [[ "$attempt" != 180 ]] || { echo 'Emulator did not finish booting.' >&2; exit 1; }
    sleep 1
done
adb -s "$serial" shell settings put system font_scale 1.0
adb -s "$serial" shell wm size 1280x2856
adb -s "$serial" shell wm density 480
adb -s "$serial" shell settings put global window_animation_scale 0
adb -s "$serial" shell settings put global transition_animation_scale 0
adb -s "$serial" shell settings put global animator_duration_scale 1
adb -s "$serial" shell cmd uimode night yes
adb -s "$serial" shell settings put system accelerometer_rotation 0
adb -s "$serial" shell settings put system user_rotation 0
adb -s "$serial" shell input keyevent KEYCODE_WAKEUP
adb -s "$serial" shell wm dismiss-keyguard
