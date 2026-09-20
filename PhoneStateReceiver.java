package com.noticall.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class PhoneStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(intent.getAction())) return;
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
            Prefs.setRinging(context, true, System.currentTimeMillis());
        } else if (TelephonyManager.EXTRA_STATE_IDLE.equals(state) && Prefs.ringingSeen(context)) {
            Prefs.setRinging(context, false, Prefs.ringingAt(context));
            if (Prefs.enabled(context)) AutomationEngine.scheduleMissedCallCheck(context);
        }
    }
}
