package com.noticall.app;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class EventStore {
    private static final String KEY = "events_json";
    private static final int MAX = 80;
    private EventStore() {}

    public static void add(Context c, String kind, String title, String detail) {
        try {
            SharedPreferences p = Prefs.p(c);
            JSONArray old = new JSONArray(p.getString(KEY, "[]"));
            JSONArray fresh = new JSONArray();
            JSONObject e = new JSONObject();
            e.put("time", System.currentTimeMillis());
            e.put("kind", kind);
            e.put("title", title);
            e.put("detail", detail == null ? "" : detail);
            fresh.put(e);
            for (int i = 0; i < old.length() && fresh.length() < MAX; i++) fresh.put(old.get(i));
            p.edit().putString(KEY, fresh.toString()).apply();
        } catch (Exception ignored) {}
    }

    public static List<Event> list(Context c) {
        ArrayList<Event> out = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(Prefs.p(c).getString(KEY, "[]"));
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                out.add(new Event(o.optLong("time"), o.optString("kind"), o.optString("title"), o.optString("detail")));
            }
        } catch (Exception ignored) {}
        return out;
    }

    public static void clear(Context c) { Prefs.p(c).edit().remove(KEY).apply(); }

    public static int countToday(Context c, String kind) {
        long now = System.currentTimeMillis();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.ITALY);
        String today = df.format(new Date(now));
        int n = 0;
        for (Event e : list(c)) {
            if (kind.equals(e.kind) && today.equals(df.format(new Date(e.time)))) n++;
        }
        return n;
    }

    public static final class Event {
        public final long time;
        public final String kind;
        public final String title;
        public final String detail;
        public Event(long time, String kind, String title, String detail) {
            this.time = time; this.kind = kind; this.title = title; this.detail = detail;
        }
    }
}
