#!/usr/bin/env bash
set -e
APK="${1:-Noticall-test.apk}"
command -v adb >/dev/null || { echo "adb non trovato: installa Android Platform Tools"; exit 1; }

echo "=== NOTICALL TEST INSTALLER ==="
adb devices
read -r -p "Accetta il debug USB sul telefono, poi premi Invio..."
adb install -r --allow-restricted-permissions "$APK"
adb shell pm grant com.noticall.app android.permission.READ_PHONE_STATE || true
adb shell pm grant com.noticall.app android.permission.READ_CALL_LOG || true
adb shell pm grant com.noticall.app android.permission.SEND_SMS || true
adb shell dumpsys package com.noticall.app | grep -E "READ_PHONE_STATE|READ_CALL_LOG|SEND_SMS" || true
adb shell monkey -p com.noticall.app 1
