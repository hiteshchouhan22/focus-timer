#!/usr/bin/env bash
# Runs INSIDE the emulator step (working directory = repo root).
# Captures three real screenshots of Focus Timer for the Play Store listing.
# Buttons are located by resource-id: their on-screen text is upper-cased by
# the Android button style, so text matching is unreliable.
set -x

tap_id() {
  adb shell uiautomator dump /sdcard/ui.xml > /dev/null
  adb pull /sdcard/ui.xml ui.xml > /dev/null
  bounds=$(grep -o "resource-id=\"[^\"]*:id/$1\"[^>]*" ui.xml | grep -o 'bounds="\[[0-9]*,[0-9]*\]\[[0-9]*,[0-9]*\]"' | head -1)
  coords=$(echo "$bounds" | sed -E 's/.*\[([0-9]+),([0-9]+)\]\[([0-9]+),([0-9]+)\].*/\1 \2 \3 \4/')
  set -- $coords
  if [ -n "${1:-}" ]; then
    adb shell input tap $(( ($1 + $3) / 2 )) $(( ($2 + $4) / 2 ))
  else
    echo "Could not find view with id: $1"
  fi
}

adb install app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.shadowboxstudios.focustimer/.MainActivity
sleep 4
adb exec-out screencap -p > shot-1-focus.png
tap_id shortBreakButton
sleep 1
adb exec-out screencap -p > shot-2-break.png
tap_id startPauseButton
sleep 3
adb exec-out screencap -p > shot-3-running.png
ls -la shot-*.png
