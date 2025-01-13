package org.alvarowau.tarea3.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;

public class GestorNotificaciones {

    private final Context context;

    public GestorNotificaciones(Context context) {
        this.context = context;
    }

    /**
     * Configura una alarma diaria a la hora y minuto especificados.
     *
     * @param hora   Hora de la alarma.
     * @param minuto Minuto de la alarma.
     */
    public void configurarAlarmaDiaria(int hora, int minuto) {
        Calendar calendario = Calendar.getInstance();
        calendario.setTimeInMillis(System.currentTimeMillis());
        calendario.set(Calendar.HOUR_OF_DAY, hora);
        calendario.set(Calendar.MINUTE, minuto);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, GestorAlarmas.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendario.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }
}
