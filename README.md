# Noticall — Android beta reale

Noticall è una app Android senza login e senza cloud che automatizza un solo flusso:

**chiamata persa → regole locali → SMS automatico dalla SIM del telefono**

## Cosa contiene questa build

- UI nativa Android semplice/tech in blu notte + cyan/teal
- Home con stato automazione e metriche
- Attività locale con eventi e errori
- Impostazioni: nome attività, link, testo SMS, ritardo, anti-spam, fascia oraria
- variabili `{attivita}`, `{link}`, `{numero}`
- chiamate rifiutate opzionali
- SMS di prova
- nessun account, nessun database cloud
- rilevamento chiamata basato su PHONE_STATE + verifica del CallLog
- invio SMS con SmsManager

## Come generare l'APK

1. Carica tutto il contenuto di questa cartella in un repository GitHub.
2. Apri **Actions → Build Noticall APK → Run workflow**.
3. A fine build, scarica l'artifact **Noticall-test-apk**.
4. Dentro trovi `Noticall-test.apk`, firmato automaticamente come build debug e installabile.

La workflow usa Android API 36, Java 17, AGP 8.13.2 e Gradle 8.13.

## Installazione per provare davvero chiamate + SMS

I permessi `READ_CALL_LOG` e `SEND_SMS` sono molto sensibili sulle versioni Android moderne. Per la beta privata la via più ripetibile è ADB:

1. Installa Android Platform Tools sul PC/Mac.
2. Sul telefono Android abilita **Opzioni sviluppatore → Debug USB**.
3. Collega il telefono via USB e accetta il computer.
4. Metti `Noticall-test.apk` nella stessa cartella dello script.
5. Windows: avvia `scripts/INSTALLA-NOTICALL-WINDOWS.bat` passando l'APK, oppure trascina l'APK sul file .bat.
6. Mac/Linux: `./scripts/install-noticall-mac-linux.sh /percorso/Noticall-test.apk`.

Lo script usa `adb install --allow-restricted-permissions` e prova a concedere i tre permessi necessari.

## Test corretto

- Apri Noticall.
- In Impostazioni configura nome, link e SMS.
- Prima usa **Invia un SMS di prova** verso un secondo numero.
- Attiva l'automazione.
- Da un secondo telefono chiama l'Android e lascia squillare fino a chiamata persa.
- Attendi il ritardo configurato.
- Controlla **Attività**: dovresti vedere `Chiamata persa rilevata → SMS programmato → SMS automatico inviato`.

## Nota importante

Questa beta serve a dimostrare la logica su hardware reale. La distribuzione pubblica tramite Google Play richiede poi di gestire correttamente la policy Google per i permessi SMS/Call Log e la relativa dichiarazione d'uso.
