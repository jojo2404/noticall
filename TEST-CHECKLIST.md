# Checklist test Noticall

## Test 1 — Avvio
- [ ] l'app si installa
- [ ] l'app si apre senza crash
- [ ] Home / Attività / Impostazioni funzionano

## Test 2 — SMS manuale
- [ ] permesso SMS concesso
- [ ] inserito numero di un secondo telefono
- [ ] SMS di prova ricevuto
- [ ] evento "SMS di prova inviato" nello storico

## Test 3 — Chiamata persa
- [ ] automazione attiva
- [ ] chiamata da un secondo telefono
- [ ] nessuna risposta sul telefono Noticall
- [ ] evento "Chiamata persa rilevata"
- [ ] evento "SMS programmato"
- [ ] SMS ricevuto dopo il ritardo
- [ ] evento "SMS automatico inviato"

## Test 4 — Anti-spam
- [ ] richiamare subito dallo stesso numero
- [ ] secondo SMS NON inviato
- [ ] evento "SMS evitato dall'anti-spam"

## Test 5 — Personalizzazione
- [ ] `{attivita}` sostituito correttamente
- [ ] `{link}` sostituito correttamente
- [ ] `{numero}` sostituito correttamente
