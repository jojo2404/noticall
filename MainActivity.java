package com.noticall.app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final int REQ_PERMS = 901;

    private static final int BG = Color.rgb(6, 17, 31);
    private static final int CARD = Color.rgb(11, 28, 47);
    private static final int CARD_2 = Color.rgb(14, 37, 61);
    private static final int BORDER = Color.rgb(25, 63, 91);
    private static final int TEXT = Color.rgb(243, 250, 255);
    private static final int MUTED = Color.rgb(143, 166, 188);
    private static final int CYAN = Color.rgb(32, 217, 245);
    private static final int TEAL = Color.rgb(26, 235, 183);
    private static final int RED = Color.rgb(255, 91, 111);
    private static final int GREEN = Color.rgb(46, 224, 149);

    private FrameLayout content;
    private LinearLayout navHome, navActivity, navSettings;
    private int currentTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(Color.rgb(5, 15, 27));
        buildShell();
        showHome();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (content != null) renderCurrent();
    }

    private void buildShell() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);
        root.setPadding(0, 0, 0, 0);

        content = new FrameLayout(this);
        root.addView(content, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setGravity(Gravity.CENTER);
        nav.setPadding(dp(10), dp(7), dp(10), dp(10));
        nav.setBackground(bg(Color.rgb(5, 18, 31), BORDER, dp(0), false));

        navHome = navItem("⌂", "Home", 0);
        navActivity = navItem("▥", "Attività", 1);
        navSettings = navItem("⚙", "Impostazioni", 2);
        nav.addView(navHome, new LinearLayout.LayoutParams(0, dp(58), 1f));
        nav.addView(navActivity, new LinearLayout.LayoutParams(0, dp(58), 1f));
        nav.addView(navSettings, new LinearLayout.LayoutParams(0, dp(58), 1f));
        root.addView(nav, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setContentView(root);
    }

    private LinearLayout navItem(String icon, String label, int index) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);
        TextView a = text(icon, 21, MUTED, true);
        TextView b = text(label, 11, MUTED, true);
        box.addView(a);
        box.addView(b);
        box.setTag(new TextView[]{a, b});
        box.setOnClickListener(v -> {
            currentTab = index;
            renderCurrent();
        });
        return box;
    }

    private void updateNav() {
        LinearLayout[] items = {navHome, navActivity, navSettings};
        for (int i = 0; i < items.length; i++) {
            TextView[] t = (TextView[]) items[i].getTag();
            boolean active = i == currentTab;
            t[0].setTextColor(active ? CYAN : MUTED);
            t[1].setTextColor(active ? CYAN : MUTED);
            items[i].setBackground(active ? bg(Color.rgb(7, 33, 51), Color.TRANSPARENT, dp(14), false) : null);
        }
    }

    private void renderCurrent() {
        if (currentTab == 0) showHome();
        else if (currentTab == 1) showActivity();
        else showSettings();
    }

    private ScrollView screen() {
        ScrollView sc = new ScrollView(this);
        sc.setFillViewport(true);
        sc.setBackgroundColor(BG);
        return sc;
    }

    private LinearLayout column() {
        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setPadding(dp(18), dp(18), dp(18), dp(24));
        return col;
    }

    private void showHome() {
        currentTab = 0;
        updateNav();
        content.removeAllViews();
        ScrollView sc = screen();
        LinearLayout col = column();
        sc.addView(col);
        content.addView(sc);

        col.addView(header());
        col.addView(space(12));

        boolean granted = AutomationEngine.hasAllPermissions(this);
        boolean enabled = Prefs.enabled(this);
        LinearLayout hero = card(CARD, enabled ? CYAN : BORDER, 20);
        LinearLayout top = new LinearLayout(this);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);
        TextView dot = text("●", 24, enabled ? GREEN : MUTED, true);
        top.addView(dot);
        LinearLayout statusText = new LinearLayout(this);
        statusText.setOrientation(LinearLayout.VERTICAL);
        statusText.setPadding(dp(8), 0, 0, 0);
        statusText.addView(text(enabled ? "Automazione attiva" : "Automazione disattivata", 19, TEXT, true));
        statusText.addView(text(enabled ? "Noticall risponde alle chiamate perse." : "Attivala quando sei pronto.", 13, MUTED, false));
        top.addView(statusText, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        Switch sw = new Switch(this);
        sw.setChecked(enabled);
        sw.setShowText(false);
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            sw.setThumbTintList(new ColorStateList(new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}}, new int[]{Color.WHITE, Color.LTGRAY}));
            sw.setTrackTintList(new ColorStateList(new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}}, new int[]{TEAL, Color.rgb(55,74,92)}));
        }
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && !AutomationEngine.hasAllPermissions(this)) {
                buttonView.setChecked(false);
                requestNoticallPermissions();
                return;
            }
            Prefs.setEnabled(this, isChecked);
            EventStore.add(this, "system", isChecked ? "Automazione attivata" : "Automazione disattivata", "");
            renderCurrent();
        });
        top.addView(sw);
        hero.addView(top);
        hero.addView(space(14));

        if (!granted) {
            LinearLayout warning = miniCard(Color.rgb(36, 30, 18), Color.rgb(115, 84, 26));
            warning.addView(text("Permessi da completare", 14, Color.rgb(255, 218, 135), true));
            warning.addView(text("Telefono, registro chiamate e SMS sono necessari per la prova reale.", 12, Color.rgb(221, 196, 143), false));
            Button p = button("Configura permessi", false);
            p.setOnClickListener(v -> requestNoticallPermissions());
            warning.addView(space(8));
            warning.addView(p);
            hero.addView(warning);
        } else {
            TextView ready = text("✓ Pronto per le chiamate reali", 13, GREEN, true);
            hero.addView(ready);
        }
        col.addView(hero);

        col.addView(space(12));
        LinearLayout metrics = new LinearLayout(this);
        metrics.setOrientation(LinearLayout.HORIZONTAL);
        metrics.addView(metric("Perse oggi", EventStore.countToday(this, "missed") + ""), new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        metrics.addView(spaceH(8));
        metrics.addView(metric("SMS inviati", EventStore.countToday(this, "sent") + ""), new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        metrics.addView(spaceH(8));
        metrics.addView(metric("Saltati", EventStore.countToday(this, "skip") + ""), new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        col.addView(metrics);

        col.addView(space(12));
        LinearLayout preview = card(CARD_2, BORDER, 18);
        TextView label = text("RISPOSTA AUTOMATICA", 11, CYAN, true);
        label.setLetterSpacing(.12f);
        preview.addView(label);
        preview.addView(space(8));
        preview.addView(text(AutomationEngine.renderMessage(this, "+39 333 123 4567"), 15, TEXT, false));
        preview.addView(space(10));
        TextView info = text("Invio dopo " + Prefs.delaySeconds(this) + "s  •  Anti-spam " + (Prefs.antiSpamHours(this) == 0 ? "OFF" : Prefs.antiSpamHours(this) + "h"), 12, MUTED, false);
        preview.addView(info);
        col.addView(preview);

        col.addView(space(12));
        Button test = button("Invia un SMS di prova", true);
        test.setOnClickListener(v -> openTestDialog());
        col.addView(test);

        col.addView(space(20));
        LinearLayout recentTitle = new LinearLayout(this);
        recentTitle.setOrientation(LinearLayout.HORIZONTAL);
        recentTitle.setGravity(Gravity.CENTER_VERTICAL);
        recentTitle.addView(text("Attività recente", 18, TEXT, true), new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        TextView all = text("Vedi tutto  ›", 13, CYAN, true);
        all.setOnClickListener(v -> { currentTab = 1; showActivity(); });
        recentTitle.addView(all);
        col.addView(recentTitle);
        col.addView(space(8));

        List<EventStore.Event> events = EventStore.list(this);
        if (events.isEmpty()) {
            LinearLayout empty = miniCard(CARD, BORDER);
            empty.addView(text("Nessuna attività ancora", 14, TEXT, true));
            empty.addView(text("Quando Noticall rileverà una chiamata, la vedrai qui.", 12, MUTED, false));
            col.addView(empty);
        } else {
            LinearLayout list = card(CARD, BORDER, 16);
            int max = Math.min(4, events.size());
            for (int i = 0; i < max; i++) {
                list.addView(eventRow(events.get(i)));
                if (i < max - 1) list.addView(divider());
            }
            col.addView(list);
        }
    }

    private View header() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        ImageView logo = new ImageView(this);
        logo.setImageResource(com.noticall.app.R.drawable.noticall_icon);
        logo.setScaleType(ImageView.ScaleType.CENTER_CROP);
        GradientDrawable lbg = bg(Color.rgb(7, 25, 42), Color.rgb(18, 111, 137), dp(14), false);
        logo.setBackground(lbg);
        row.addView(logo, new LinearLayout.LayoutParams(dp(58), dp(58)));
        LinearLayout words = new LinearLayout(this);
        words.setOrientation(LinearLayout.VERTICAL);
        words.setPadding(dp(12), 0, 0, 0);
        TextView name = text("Noticall", 28, TEXT, true);
        words.addView(name);
        words.addView(text("Meno chiamate perse. Più opportunità.", 12, MUTED, false));
        row.addView(words, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        return row;
    }

    private void showActivity() {
        currentTab = 1;
        updateNav();
        content.removeAllViews();
        ScrollView sc = screen();
        LinearLayout col = column();
        sc.addView(col);
        content.addView(sc);

        col.addView(text("Attività", 28, TEXT, true));
        col.addView(text("Tutto ciò che Noticall ha rilevato sul telefono.", 13, MUTED, false));
        col.addView(space(14));

        List<EventStore.Event> events = EventStore.list(this);
        if (events.isEmpty()) {
            LinearLayout empty = card(CARD, BORDER, 18);
            empty.addView(text("Ancora nessun evento", 17, TEXT, true));
            empty.addView(space(4));
            empty.addView(text("Attiva Noticall e fai una chiamata di prova da un altro telefono.", 13, MUTED, false));
            col.addView(empty);
        } else {
            LinearLayout list = card(CARD, BORDER, 16);
            for (int i = 0; i < events.size(); i++) {
                list.addView(eventRow(events.get(i)));
                if (i < events.size() - 1) list.addView(divider());
            }
            col.addView(list);
            col.addView(space(12));
            Button clear = button("Svuota storico", false);
            clear.setOnClickListener(v -> new AlertDialog.Builder(this)
                    .setTitle("Svuotare lo storico?")
                    .setMessage("Le impostazioni di Noticall resteranno invariate.")
                    .setNegativeButton("Annulla", null)
                    .setPositiveButton("Svuota", (d, w) -> { EventStore.clear(this); showActivity(); })
                    .show());
            col.addView(clear);
        }
    }

    private View eventRow(EventStore.Event e) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, dp(10), 0, dp(10));
        TextView icon = text(eventIcon(e.kind), 20, eventColor(e.kind), true);
        icon.setGravity(Gravity.CENTER);
        row.addView(icon, new LinearLayout.LayoutParams(dp(36), dp(36)));
        LinearLayout t = new LinearLayout(this);
        t.setOrientation(LinearLayout.VERTICAL);
        t.setPadding(dp(8), 0, 0, 0);
        t.addView(text(e.title, 14, TEXT, true));
        if (e.detail != null && !e.detail.isEmpty()) t.addView(text(e.detail, 12, MUTED, false));
        row.addView(t, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.ITALY);
        row.addView(text(df.format(new Date(e.time)), 12, MUTED, false));
        return row;
    }

    private String eventIcon(String kind) {
        if ("sent".equals(kind)) return "✓";
        if ("missed".equals(kind)) return "↘";
        if ("error".equals(kind)) return "!";
        if ("skip".equals(kind)) return "—";
        if ("queued".equals(kind)) return "◷";
        return "•";
    }
    private int eventColor(String kind) {
        if ("sent".equals(kind)) return GREEN;
        if ("missed".equals(kind)) return RED;
        if ("error".equals(kind)) return Color.rgb(255, 176, 74);
        if ("queued".equals(kind)) return CYAN;
        return MUTED;
    }

    private void showSettings() {
        currentTab = 2;
        updateNav();
        content.removeAllViews();
        ScrollView sc = screen();
        LinearLayout col = column();
        sc.addView(col);
        content.addView(sc);

        col.addView(text("Impostazioni", 28, TEXT, true));
        col.addView(text("Poche opzioni, quelle che servono davvero.", 13, MUTED, false));
        col.addView(space(14));

        LinearLayout perm = card(CARD, AutomationEngine.hasAllPermissions(this) ? Color.rgb(25, 88, 68) : Color.rgb(115, 84, 26), 18);
        perm.addView(text(AutomationEngine.hasAllPermissions(this) ? "✓ Permessi pronti" : "Permessi Android", 16, AutomationEngine.hasAllPermissions(this) ? GREEN : Color.rgb(255, 218, 135), true));
        perm.addView(space(3));
        perm.addView(text(permissionSummary(), 12, MUTED, false));
        if (!AutomationEngine.hasAllPermissions(this)) {
            Button grant = button("Concedi permessi", false);
            grant.setOnClickListener(v -> requestNoticallPermissions());
            perm.addView(space(9));
            perm.addView(grant);
        }
        col.addView(perm);
        col.addView(space(12));

        LinearLayout form = card(CARD, BORDER, 18);
        form.addView(sectionLabel("MESSAGGIO"));
        EditText business = input(Prefs.business(this), "Nome attività", false);
        EditText link = input(Prefs.link(this), "Link prenotazione", false);
        EditText message = input(Prefs.message(this), "Messaggio SMS", true);
        form.addView(fieldLabel("Nome attività")); form.addView(business);
        form.addView(fieldLabel("Link prenotazione")); form.addView(link);
        form.addView(fieldLabel("Messaggio SMS")); form.addView(message);
        form.addView(text("Variabili: {attivita}  {link}  {numero}", 11, CYAN, false));
        col.addView(form);
        col.addView(space(12));

        LinearLayout rules = card(CARD, BORDER, 18);
        rules.addView(sectionLabel("REGOLE"));

        rules.addView(fieldLabel("Ritardo prima dell'SMS"));
        Spinner delay = spinner(new String[]{"5 secondi", "10 secondi", "30 secondi", "60 secondi"});
        int[] delays = {5,10,30,60};
        selectInt(delay, delays, Prefs.delaySeconds(this));
        rules.addView(delay);

        rules.addView(fieldLabel("Anti-spam sullo stesso numero"));
        Spinner anti = spinner(new String[]{"Disattivato", "1 ora", "6 ore", "12 ore", "24 ore"});
        int[] antiVals = {0,1,6,12,24};
        selectInt(anti, antiVals, Prefs.antiSpamHours(this));
        rules.addView(anti);

        Switch rejected = switchRow("Includi chiamate rifiutate", "Oltre alle chiamate lasciate squillare", Prefs.includeRejected(this));
        rules.addView(space(9)); rules.addView(rejected);

        Switch timeWindow = switchRow("Usa fascia oraria", "L'automazione funziona solo nelle ore indicate", Prefs.timeWindowEnabled(this));
        rules.addView(space(8)); rules.addView(timeWindow);

        LinearLayout hours = new LinearLayout(this);
        hours.setOrientation(LinearLayout.HORIZONTAL);
        EditText start = input(String.valueOf(Prefs.startHour(this)), "Da", false);
        start.setInputType(InputType.TYPE_CLASS_NUMBER);
        EditText end = input(String.valueOf(Prefs.endHour(this)), "A", false);
        end.setInputType(InputType.TYPE_CLASS_NUMBER);
        hours.addView(start, new LinearLayout.LayoutParams(0, dp(50), 1f));
        hours.addView(spaceH(8));
        hours.addView(end, new LinearLayout.LayoutParams(0, dp(50), 1f));
        rules.addView(space(8));
        rules.addView(hours);
        rules.addView(text("Ore in formato 0–23. Esempio: 8 → 21.", 11, MUTED, false));
        col.addView(rules);
        col.addView(space(12));

        LinearLayout sim = card(CARD, BORDER, 18);
        sim.addView(sectionLabel("SIM"));
        sim.addView(text("Usa la SIM predefinita Android per gli SMS.", 14, TEXT, true));
        sim.addView(text("La scelta manuale dual-SIM arriverà dopo il test della logica principale.", 12, MUTED, false));
        col.addView(sim);
        col.addView(space(14));

        Button save = button("Salva impostazioni", true);
        save.setOnClickListener(v -> {
            try {
                int sh = clampHour(Integer.parseInt(start.getText().toString().trim()));
                int eh = clampHour(Integer.parseInt(end.getText().toString().trim()));
                Prefs.saveSettings(this,
                        business.getText().toString(), link.getText().toString(), message.getText().toString(),
                        delays[delay.getSelectedItemPosition()], antiVals[anti.getSelectedItemPosition()],
                        rejected.isChecked(), timeWindow.isChecked(), sh, eh);
                EventStore.add(this, "system", "Impostazioni aggiornate", "");
                Toast.makeText(this, "Salvato", Toast.LENGTH_SHORT).show();
                showSettings();
            } catch (Exception e) {
                Toast.makeText(this, "Controlla gli orari inseriti", Toast.LENGTH_LONG).show();
            }
        });
        col.addView(save);
        col.addView(space(10));
        col.addView(text("Noticall salva tutto solo su questo telefono. Nessun login e nessun cloud nella versione test.", 11, MUTED, false));
    }

    private int clampHour(int h) { return Math.max(0, Math.min(23, h)); }

    private String permissionSummary() {
        StringBuilder s = new StringBuilder();
        s.append(has(Manifest.permission.READ_PHONE_STATE) ? "Telefono ✓" : "Telefono ✕");
        s.append("   ");
        s.append(has(Manifest.permission.READ_CALL_LOG) ? "Registro ✓" : "Registro ✕");
        s.append("   ");
        s.append(has(Manifest.permission.SEND_SMS) ? "SMS ✓" : "SMS ✕");
        return s.toString();
    }

    private boolean has(String permission) { return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED; }

    private void requestNoticallPermissions() {
        requestPermissions(new String[]{
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.SEND_SMS
        }, REQ_PERMS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_PERMS) {
            if (AutomationEngine.hasAllPermissions(this)) {
                Toast.makeText(this, "Permessi configurati", Toast.LENGTH_SHORT).show();
                EventStore.add(this, "system", "Permessi configurati", "");
            } else {
                Toast.makeText(this, "Mancano alcuni permessi. Per la prova completa potrebbe servire l'installazione ADB.", Toast.LENGTH_LONG).show();
            }
            renderCurrent();
        }
    }

    private void openTestDialog() {
        if (!has(Manifest.permission.SEND_SMS)) {
            requestNoticallPermissions();
            return;
        }
        final EditText number = input("", "+39...", false);
        number.setInputType(InputType.TYPE_CLASS_PHONE);
        int pad = dp(20);
        FrameLayout box = new FrameLayout(this);
        box.setPadding(pad, dp(4), pad, 0);
        box.addView(number);
        new AlertDialog.Builder(this)
                .setTitle("SMS di prova")
                .setMessage("Inserisci un numero diverso da quello del telefono di test.")
                .setView(box)
                .setNegativeButton("Annulla", null)
                .setPositiveButton("Invia", (d, w) -> {
                    String n = number.getText().toString().trim();
                    try {
                        AutomationEngine.sendSmsNow(this, n, AutomationEngine.renderMessage(this, n), true);
                        Toast.makeText(this, "Invio richiesto", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        EventStore.add(this, "error", "Test SMS non avviato", e.getMessage());
                        Toast.makeText(this, "Errore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }).show();
    }

    private LinearLayout metric(String label, String value) {
        LinearLayout m = miniCard(CARD, BORDER);
        m.setGravity(Gravity.CENTER);
        m.addView(text(value, 24, CYAN, true));
        TextView l = text(label, 10, MUTED, true); l.setGravity(Gravity.CENTER); m.addView(l);
        return m;
    }

    private LinearLayout card(int fill, int stroke, int radius) {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(dp(16), dp(16), dp(16), dp(16));
        l.setBackground(bg(fill, stroke, dp(radius), true));
        return l;
    }
    private LinearLayout miniCard(int fill, int stroke) {
        LinearLayout l = card(fill, stroke, 14);
        l.setPadding(dp(13), dp(12), dp(13), dp(12));
        return l;
    }

    private GradientDrawable bg(int fill, int stroke, int radius, boolean gradient) {
        GradientDrawable g;
        if (gradient) {
            g = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                    new int[]{fill, Color.rgb(Math.min(255, Color.red(fill)+3), Math.min(255, Color.green(fill)+7), Math.min(255, Color.blue(fill)+12))});
        } else g = new GradientDrawable();
        g.setColor(fill);
        g.setCornerRadius(radius);
        if (stroke != Color.TRANSPARENT) g.setStroke(dp(1), stroke);
        return g;
    }

    private TextView text(String value, int sp, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(sp);
        t.setTextColor(color);
        t.setLineSpacing(0, 1.08f);
        if (bold) t.setTypeface(Typeface.create("sans", Typeface.BOLD));
        return t;
    }

    private TextView sectionLabel(String s) {
        TextView t = text(s, 11, CYAN, true);
        t.setLetterSpacing(.12f);
        t.setPadding(0, 0, 0, dp(6));
        return t;
    }
    private TextView fieldLabel(String s) {
        TextView t = text(s, 12, MUTED, true);
        t.setPadding(0, dp(10), 0, dp(6));
        return t;
    }

    private EditText input(String value, String hint, boolean multiline) {
        EditText e = new EditText(this);
        e.setText(value);
        e.setHint(hint);
        e.setHintTextColor(Color.rgb(92, 119, 143));
        e.setTextColor(TEXT);
        e.setTextSize(14);
        e.setPadding(dp(13), dp(10), dp(13), dp(10));
        e.setBackground(bg(Color.rgb(6, 23, 39), BORDER, dp(12), false));
        if (multiline) {
            e.setMinLines(4); e.setGravity(Gravity.TOP); e.setSingleLine(false);
        } else e.setSingleLine(true);
        return e;
    }

    private Spinner spinner(String[] values) {
        Spinner sp = new Spinner(this);
        ArrayAdapter<String> a = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, values) {
            @Override public View getView(int position, View convertView, ViewGroup parent) {
                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setTextColor(TEXT); v.setTextSize(14); v.setPadding(dp(13), 0, dp(13), 0); return v;
            }
        };
        sp.setAdapter(a);
        sp.setBackground(bg(Color.rgb(6, 23, 39), BORDER, dp(12), false));
        sp.setPadding(dp(4), 0, dp(4), 0);
        sp.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(52)));
        return sp;
    }

    private void selectInt(Spinner sp, int[] values, int current) {
        for (int i = 0; i < values.length; i++) if (values[i] == current) { sp.setSelection(i); return; }
    }

    private Switch switchRow(String title, String subtitle, boolean checked) {
        Switch s = new Switch(this);
        s.setText(title + "\n" + subtitle);
        s.setTextColor(TEXT);
        s.setTextSize(13);
        s.setChecked(checked);
        s.setPadding(0, dp(4), 0, dp(4));
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            s.setTrackTintList(new ColorStateList(new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}}, new int[]{TEAL, Color.rgb(55,74,92)}));
        }
        return s;
    }

    private Button button(String title, boolean primary) {
        Button b = new Button(this);
        b.setText(title);
        b.setTextSize(14);
        b.setAllCaps(false);
        b.setTypeface(Typeface.create("sans", Typeface.BOLD));
        b.setTextColor(primary ? Color.rgb(0, 19, 29) : TEXT);
        int fill = primary ? CYAN : Color.rgb(12, 37, 58);
        int stroke = primary ? Color.rgb(75, 240, 255) : BORDER;
        b.setBackground(bg(fill, stroke, dp(14), false));
        b.setMinHeight(dp(52));
        return b;
    }

    private View divider() {
        View v = new View(this);
        v.setBackgroundColor(Color.rgb(22, 47, 68));
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(1)));
        return v;
    }

    private Space space(int h) { Space s = new Space(this); s.setLayoutParams(new LinearLayout.LayoutParams(1, dp(h))); return s; }
    private Space spaceH(int w) { Space s = new Space(this); s.setLayoutParams(new LinearLayout.LayoutParams(dp(w), 1)); return s; }
    private int dp(int v) { return Math.round(v * getResources().getDisplayMetrics().density); }
}
