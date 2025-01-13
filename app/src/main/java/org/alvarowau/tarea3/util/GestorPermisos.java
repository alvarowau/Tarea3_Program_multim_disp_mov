package org.alvarowau.tarea3.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class GestorPermisos {

    private final Activity activity;

    public static final int REQUEST_READ_CONTACTS = 1;
    public static final int REQUEST_SMS_PERMISSION = 2;
    public static final int REQUEST_NOTIFICATIONS_PERMISSION = 3;

    private boolean permisosContactos = false;
    private boolean permisosSMS = false;
    private boolean permisosNotificaciones = false;

    public GestorPermisos(Activity activity) {
        this.activity = activity;
    }

    public void solicitarPermisos() {
        solicitarPermisosContactos();
    }

    private void solicitarPermisosContactos() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        } else {
            permisosContactos = true;
            solicitarPermisosSMS();
        }
    }

    private void solicitarPermisosSMS() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.SEND_SMS},
                    REQUEST_SMS_PERMISSION);
        } else {
            permisosSMS = true;
            solicitarPermisosNotificaciones();
        }
    }

    private void solicitarPermisosNotificaciones() {
        if (Build.VERSION.SDK_INT >= 33 &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_NOTIFICATIONS_PERMISSION);
        } else {
            permisosNotificaciones = true;
            Toast.makeText(activity, "Todos los permisos necesarios ya están concedidos.", Toast.LENGTH_SHORT).show();
        }
    }

    public void manejarResultadoPermisos(int requestCode, int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            permisosContactos = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (!permisosContactos) {
                Toast.makeText(activity, "Permiso no concedido, no se pueden mostrar los contactos", Toast.LENGTH_SHORT).show();
            }
            solicitarPermisosSMS();
        } else if (requestCode == REQUEST_SMS_PERMISSION) {
            permisosSMS = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (!permisosSMS) {
                Toast.makeText(activity, "Permiso de SMS denegado. No se puede enviar SMS.", Toast.LENGTH_SHORT).show();
            }
            solicitarPermisosNotificaciones();
        } else if (requestCode == REQUEST_NOTIFICATIONS_PERMISSION) {
            permisosNotificaciones = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (!permisosNotificaciones) {
                Toast.makeText(activity, "Permiso de Notificaciones denegado.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean todosLosPermisosConcedidos() {
        return permisosContactos && permisosSMS && permisosNotificaciones;
    }
}
