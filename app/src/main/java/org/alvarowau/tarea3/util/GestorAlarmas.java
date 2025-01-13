package org.alvarowau.tarea3.util;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import org.alvarowau.tarea3.R;
import org.alvarowau.tarea3.db.GestorBBDD;
import org.alvarowau.tarea3.model.Contacto;

import java.util.ArrayList;
import java.util.Calendar;

public class GestorAlarmas extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "¡La alarma se ha activado!", Toast.LENGTH_SHORT).show();
        ArrayList<Contacto> quienCumple;
        GestorBBDD gestorBD = new GestorBBDD(context);
        quienCumple = gestorBD.obtenerQuienCumple();
        gestionarAvisos(context, quienCumple);
    }


    public void gestionarAvisos(Context context, ArrayList<Contacto> quienCumple){
        String datosContactos ="";
        for (Contacto contacto : quienCumple){
            datosContactos += contacto.getNombre() + " ";

            if (contacto.getTipoAviso().equals("S")){
                datosContactos += "(SMS) ";
                enviarSMS(context, contacto);
            }
        }
        enviarAviso(context, datosContactos);
    }

    public void enviarSMS(Context context, Contacto contacto){



        String telefono = contacto.getTelefono();

        String message = contacto.getMensaje();

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(telefono, null, message, null, null);
            Toast.makeText(context, "SMS enviado.",Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(context,"SMS no enviado, por favor, inténtalo otra vez.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void enviarAviso(Context context, String datosContactos){

        String canalId = "CHANNEL_ID_101";
        CharSequence name = context.getString(R.string.canal_notificacion);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        Notification notify;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(canalId, name, importance);
            channel.setDescription("Canal predeterminado para notificaciones");

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, canalId)
                    .setSmallIcon(R.drawable.ic_cake)
                    .setContentTitle("Birthday Helper")
                    .setContentText("Despliega para ver todo el texto")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Hoy es el cumpleaños de: "+ datosContactos))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_cake));

            notify = builder.build();

            NotificationManager notificationManager =  context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        } else {
            notify = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_cake)
                    .setContentTitle("Birthday Helper")
                    .setContentText("Despliega para ver todo el texto")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Hoy es el cumpleaños de: "+ datosContactos))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_cake))
                    .build();
        }

        NotificationManager notificationManager =  context.getSystemService(NotificationManager.class);
        notificationManager.notify(777, notify);
    }

    public void configurarAlarmaDiaria(Context context, int hora, int minuto) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, GestorAlarmas.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hora);
        calendar.set(Calendar.MINUTE, minuto);
        calendar.set(Calendar.SECOND, 0);

        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }


}
