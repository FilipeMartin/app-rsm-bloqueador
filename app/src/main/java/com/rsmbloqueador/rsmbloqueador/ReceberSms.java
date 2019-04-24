package com.rsmbloqueador.rsmbloqueador;

import android.content.*;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * Created by Filipe on 14/08/2017.
 */

public class ReceberSms extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Receber a mensagem SMS
        Bundle bundle = intent.getExtras();
        SmsMessage[] mensagem = null;
        String numero = "";
        String localizacao= "";
        if(bundle != null){
            // Recuperar a mensagem SMS recebia
            Object[]pdus = (Object[])bundle.get("pdus");
            mensagem = new SmsMessage[pdus.length];
            for(int i=0; i<mensagem.length; i++){
                mensagem[i]= SmsMessage.createFromPdu((byte[])pdus[i]);
                numero = mensagem[i].getOriginatingAddress();
                localizacao = mensagem[i].getMessageBody().toString();

            }
            /* Exibir a nova mensagem SMS
            Toast.makeText(context, pacote, Toast.LENGTH_SHORT).show();
            Log.i("Programador_Filipe", pacote);
            */
            // send a broadcast intent to update the sms received in a textview
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("SMS_RECEIVED_ACTION");
            broadcastIntent.putExtra("numero", numero);
            broadcastIntent.putExtra("sms", localizacao);
            context.sendBroadcast(broadcastIntent);
        }
    }
}