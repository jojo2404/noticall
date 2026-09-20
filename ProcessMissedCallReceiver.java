package com.noticall.app;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;

public class ProcessMissedCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Prefs.enabled(context)) return;
        if (context.checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            EventStore.add(context, "error", "Permesso registro chiamate mancante", "Apri Noticall e completa i permessi");
            return;
        }
        long ringingAt = Prefs.ringingAt(context);
        Cursor cur = null;
        try {
            String[] projection = {
                    CallLog.Calls._ID,
                    CallLog.Calls.NUMBER,
                    CallLog.Calls.TYPE,
                    CallLog.Calls.DATE
            };
            cur = context.getContentResolver().query(
                    CallLog.Calls.CONTENT_URI,
                    projection,
                    null,
                    null,
                    CallLog.Calls.DATE + " DESC"
            );
            if (cur == null || !cur.moveToFirst()) return;
            long id = cur.getLong(0);
            String number = cur.getString(1);
            int type = cur.getInt(2);
            long date = cur.getLong(3);
            String callKey = id + ":" + date;
            if (callKey.equals(Prefs.lastProcessed(context))) return;

            long now = System.currentTimeMillis();
            if (date < ringingAt - 5000L || now - date > 120000L) return;

            boolean missed = type == CallLog.Calls.MISSED_TYPE;
            boolean rejected = type == CallLog.Calls.REJECTED_TYPE;
            if (!missed && !(rejected && Prefs.includeRejected(context))) return;

            Prefs.setLastProcessed(context, callKey);

            if (number == null || number.trim().isEmpty() || number.contains("-1")) {
                EventStore.add(context, "skip", "Numero non disponibile", "Nessun SMS inviato");
                return;
            }

            EventStore.add(context, "missed", missed ? "Chiamata persa rilevata" : "Chiamata rifiutata rilevata", number);

            if (!AutomationEngine.withinTimeWindow(context)) {
                EventStore.add(context, "skip", "Fuori fascia oraria", number);
                return;
            }

            int hours = Prefs.antiSpamHours(context);
            if (hours > 0) {
                long last = Prefs.lastSmsAt(context, number);
                if (last > 0 && now - last < hours * 3600000L) {
                    EventStore.add(context, "skip", "SMS evitato dall'anti-spam", number);
                    return;
                }
            }

            AutomationEngine.scheduleSms(context, number, date);
            EventStore.add(context, "queued", "SMS programmato", number + " • " + Prefs.delaySeconds(context) + "s");
        } catch (SecurityException e) {
            EventStore.add(context, "error", "Accesso chiamate bloccato", e.getMessage());
        } catch (Exception e) {
            EventStore.add(context, "error", "Errore rilevamento chiamata", e.getClass().getSimpleName());
        } finally {
            if (cur != null) cur.close();
        }
    }
}
