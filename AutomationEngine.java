package com.noticall.app;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;

import java.util.ArrayList;
import java.util.Calendar;

public final class AutomationEngine {
    private AutomationEngine() {}

    public static boolean hasAllPermissions(Context c) {
        return c.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                && c.checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED
                && c.checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean withinTimeWindow(Context c) {
        if (!Prefs.timeWindowEnabled(c)) return true;
        int h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int start = Prefs.startHour(c), end = Prefs.endHour(c);
        if (start == end) return true;
        if (start < end) return h >= start && h < end;
        return h >= start || h < end;
    }

    public static String renderMessage(Context c, String number) {
        return Prefs.message(c)
                .replace("{attivita}", Prefs.business(c))
                .replace("{link}", Prefs.link(c))
                .replace("{numero}", number == null ? "" : number);
    }

    public static void scheduleMissedCallCheck(Context c) {
        Intent i = new Intent(c, ProcessMissedCallReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(c, 2001, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                android.os.SystemClock.elapsedRealtime() + 2500L, pi);
    }

    public static void scheduleSms(Context c, String number, long callDate) {
        Intent i = new Intent(c, SendSmsReceiver.class);
        i.putExtra("number", number);
        i.putExtra("call_date", callDate);
        int req = (number + callDate).hashCode();
        PendingIntent pi = PendingIntent.getBroadcast(c, req, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        long when = android.os.SystemClock.elapsedRealtime() + Prefs.delaySeconds(c) * 1000L;
        am.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, when, pi);
    }

    public static void sendSmsNow(Context c, String number, String message, boolean test) throws Exception {
        if (number == null || number.trim().isEmpty()) throw new IllegalArgumentException("Numero mancante");
        if (c.checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
            throw new SecurityException("Permesso SMS non concesso");

        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> parts = sms.divideMessage(message);
        ArrayList<PendingIntent> sent = new ArrayList<>();
        for (int idx = 0; idx < parts.size(); idx++) {
            Intent result = new Intent(c, SendResultReceiver.class);
            result.putExtra("number", number);
            result.putExtra("test", test);
            result.putExtra("final_part", idx == parts.size() - 1);
            PendingIntent spi = PendingIntent.getBroadcast(c,
                    (number + System.nanoTime() + idx).hashCode(), result,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            sent.add(spi);
        }
        if (parts.size() <= 1) {
            sms.sendTextMessage(number, null, message, sent.get(0), null);
        } else {
            sms.sendMultipartTextMessage(number, null, parts, sent, null);
        }
    }
}
