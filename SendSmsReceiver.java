package com.noticall.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SendSmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Prefs.enabled(context)) return;
        String number = intent.getStringExtra("number");
        if (number == null || number.trim().isEmpty()) return;
        try {
            if (!AutomationEngine.withinTimeWindow(context)) {
                EventStore.add(context, "skip", "SMS annullato: fuori fascia", number);
                return;
            }
            int hours = Prefs.antiSpamHours(context);
            if (hours > 0) {
                long last = Prefs.lastSmsAt(context, number);
                if (last > 0 && System.currentTimeMillis() - last < hours * 3600000L) {
                    EventStore.add(context, "skip", "SMS annullato dall'anti-spam", number);
                    return;
                }
            }
            String message = AutomationEngine.renderMessage(context, number);
            AutomationEngine.sendSmsNow(context, number, message, false);
        } catch (Exception e) {
            EventStore.add(context, "error", "Invio SMS non riuscito", number + " • " + e.getMessage());
        }
    }
}
