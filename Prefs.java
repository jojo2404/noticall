package com.noticall.app;

import android.content.Context;
import android.content.SharedPreferences;

public final class Prefs {
    private static final String FILE = "noticall_prefs";
    private Prefs() {}

    public static SharedPreferences p(Context c) {
        return c.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    public static boolean enabled(Context c) { return p(c).getBoolean("enabled", false); }
    public static void setEnabled(Context c, boolean v) { p(c).edit().putBoolean("enabled", v).apply(); }

    public static String business(Context c) { return p(c).getString("business", "La tua attività"); }
    public static String link(Context c) { return p(c).getString("link", "https://tuosito.it/prenota"); }
    public static String message(Context c) {
        return p(c).getString("message", "Ciao! Abbiamo visto la tua chiamata a {attivita}. Non siamo riusciti a rispondere. Puoi prenotare qui: {link}");
    }

    public static int delaySeconds(Context c) { return p(c).getInt("delay_seconds", 10); }
    public static int antiSpamHours(Context c) { return p(c).getInt("antispam_hours", 12); }
    public static boolean includeRejected(Context c) { return p(c).getBoolean("include_rejected", false); }
    public static boolean timeWindowEnabled(Context c) { return p(c).getBoolean("time_window", false); }
    public static int startHour(Context c) { return p(c).getInt("start_hour", 8); }
    public static int endHour(Context c) { return p(c).getInt("end_hour", 21); }

    public static void saveSettings(Context c, String business, String link, String message,
                                    int delaySeconds, int antiSpamHours, boolean includeRejected,
                                    boolean timeWindow, int startHour, int endHour) {
        p(c).edit()
                .putString("business", business.trim())
                .putString("link", link.trim())
                .putString("message", message.trim())
                .putInt("delay_seconds", delaySeconds)
                .putInt("antispam_hours", antiSpamHours)
                .putBoolean("include_rejected", includeRejected)
                .putBoolean("time_window", timeWindow)
                .putInt("start_hour", startHour)
                .putInt("end_hour", endHour)
                .apply();
    }

    public static boolean ringingSeen(Context c) { return p(c).getBoolean("ringing_seen", false); }
    public static void setRinging(Context c, boolean seen, long at) {
        p(c).edit().putBoolean("ringing_seen", seen).putLong("ringing_at", at).apply();
    }
    public static long ringingAt(Context c) { return p(c).getLong("ringing_at", 0L); }

    public static String lastProcessed(Context c) { return p(c).getString("last_processed", ""); }
    public static void setLastProcessed(Context c, String id) { p(c).edit().putString("last_processed", id).apply(); }

    private static String smsKey(String number) {
        return "last_sms_" + Integer.toHexString(number == null ? 0 : number.hashCode());
    }
    public static long lastSmsAt(Context c, String number) { return p(c).getLong(smsKey(number), 0L); }
    public static void setLastSmsAt(Context c, String number, long at) { p(c).edit().putLong(smsKey(number), at).apply(); }
}
