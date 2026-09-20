@echo off
setlocal
set APK=%~1
if "%APK%"=="" set APK=Noticall-test.apk

echo.
echo === NOTICALL TEST INSTALLER ===
echo APK: %APK%
echo.

where adb >nul 2>nul
if errorlevel 1 (
  echo ERRORE: adb non trovato. Installa Android Platform Tools e riapri questo file.
  pause
  exit /b 1
)

adb devices
echo.
echo Sul telefono accetta la richiesta "Consenti debug USB" se compare.
pause

adb install -r --allow-restricted-permissions "%APK%"
adb shell pm grant com.noticall.app android.permission.READ_PHONE_STATE
adb shell pm grant com.noticall.app android.permission.READ_CALL_LOG
adb shell pm grant com.noticall.app android.permission.SEND_SMS

echo.
echo Stato permessi:
adb shell dumpsys package com.noticall.app | findstr /I "READ_PHONE_STATE READ_CALL_LOG SEND_SMS"

echo.
echo Apertura Noticall...
adb shell monkey -p com.noticall.app 1

echo.
echo Fatto. Ora configura messaggio, link e attiva Noticall.
pause
