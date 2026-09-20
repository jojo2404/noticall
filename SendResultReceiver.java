package com.noticall.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SendResultReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String number = intent.getStringExtra("number");
        boolean test = intent.getBooleanExtra("test", false);
        boolean finalPart = intent.getBooleanExtra("final_part", true);
        if (getResultCode() == Activity.RESULT_OK) {
            if (finalPart) {
                if (!test) Prefs.setLastSmsAt(context, number, System.currentTimeMillis());
                EventStore.add(context, "sent", test ? "SMS di prova inviato" : "SMS automatico inviato", number);
            }
        } else {
            String reason;
            switch (getResultCode()) {
                case android.telephony.SmsManager.RESULT_ERROR_GENERIC_FAILURE: reason = "Errore generico"; break;
                case android.telephony.SmsManager.RESULT_ERROR_NO_SERVICE: reason = "Nessun servizio"; break;
                case android.telephony.SmsManager.RESULT_ERROR_NULL_PDU: reason = "PDU non disponibile"; break;
                case android.telephony.SmsManager.RESULT_ERROR_RADIO_OFF: reason = "Radio disattivata"; break;
                default: reason = "Codice " + getResultCode();
            }
            EventStore.add(context, "error", test ? "Test SMS fallito" : "SMS automatico fallito", number + " • " + reason);
        }
    }
}
