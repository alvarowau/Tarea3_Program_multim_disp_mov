package org.alvarowau.tarea3;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.Manifest;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Calendar;

import org.alvarowau.tarea3.db.GestorBBDD;
import org.alvarowau.tarea3.iu.ContactoActivity;
import org.alvarowau.tarea3.model.Contacto;
import org.alvarowau.tarea3.util.ContactosAdapter;
import org.alvarowau.tarea3.util.GestorAlarmas;


public class MainActivity extends AppCompatActivity {



    GestorBBDD gestorBD;
    int horaAlarma;
    int minutoAlarma;

    boolean permisosContactos = false;
    boolean permisosSMS = false;
    boolean permisosNotificaciones = false;

    private static final int REQUEST_READ_CONTACTS = 1;
    private static final int REQUEST_SMS_PERMISSION = 2;
    private static final int REQUEST_NOTIFICATIONS_PERMISSION = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setLogo(R.mipmap.ic_launcher);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            actionBar.setTitle("Birthday Helper");
        }

        gestorBD = new GestorBBDD(this);

        // Inicia la solicitud de permisos
        pedirPermisos();

        if (permisosContactos) {
            mostrarContactos();
        }
    }

    public void onRestart() {
        super.onRestart();
        ListView listView = findViewById(R.id.listview_contactos);
        listView.invalidate();
        mostrarContactos();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.felicitaciones) {
            mostrarConfiguracion();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void pedirPermisos() {
        pedirPermisosContactos();
    }

    private void pedirPermisosContactos() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        } else {
            permisosContactos = true;
            pedirPermisosSMS();
        }
    }

    private void pedirPermisosSMS() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.SEND_SMS},
                    REQUEST_SMS_PERMISSION);
        } else {
            permisosSMS = true;
            pedirPermisosNotificaciones();
        }
    }

    private void pedirPermisosNotificaciones() {
        if (Build.VERSION.SDK_INT >= 33 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_NOTIFICATIONS_PERMISSION);
        } else {
            permisosNotificaciones = true;
            Toast.makeText(this, "Todos los permisos necesarios ya están concedidos.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permisosContactos = true;
            } else {
                permisosContactos = false;
                Toast.makeText(this, "Permiso no concedido, no se pueden mostrar los contactos", Toast.LENGTH_SHORT).show();
            }
            pedirPermisosSMS();
        }

        if (requestCode == REQUEST_SMS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permisosSMS = true;
            } else {
                permisosSMS = false;
                Toast.makeText(this, "Permiso de SMS denegado. No se puede enviar SMS.", Toast.LENGTH_SHORT).show();
            }
            pedirPermisosNotificaciones();
        }

        if (requestCode == REQUEST_NOTIFICATIONS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permisosNotificaciones = true;
            } else {
                permisosNotificaciones = false;
                Toast.makeText(this, "Permiso de Notificaciones denegado.", Toast.LENGTH_SHORT).show();
            }
        }

        if (permisosContactos && permisosSMS && permisosNotificaciones) {
            mostrarContactos();
        }
    }

    private ArrayList<Contacto> leerContactos() {
        ArrayList<Contacto> listaContactos = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        String[] projection = {
                ContactsContract.Contacts._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
        };
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, projection, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            int idContactoIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            int nombreID = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int telefonoID = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String nombreAnterior = null; // Nombre del contacto anterior
            Contacto contactoActual = null;

            do {
                int idContacto = cursor.getInt(idContactoIndex);
                String nombre = cursor.getString(nombreID);
                String telefono = cursor.getString(telefonoID);

                if (!nombre.equals(nombreAnterior)) {
                    if (contactoActual != null) {
                        listaContactos.add(contactoActual);
                    }
                    contactoActual = new Contacto(idContacto, nombre, telefono);
                    nombreAnterior = nombre;
                }
            } while (cursor.moveToNext());

            if (contactoActual != null) {
                listaContactos.add(contactoActual);
            }

            cursor.close();
        }
        return listaContactos;
    }

    public void mostrarContactos() {
        boolean existeTabla = gestorBD.existeTabla("cumples");
        if (!existeTabla) {
            gestorBD.crearTabla();
            ArrayList<Contacto> listaContactos = leerContactos();
            gestorBD.guardarTodosContactos(listaContactos);
        }

        ArrayList<Contacto> listaContactosListView = gestorBD.obtenerContactosListView(this);
        ListView contactos = findViewById(R.id.listview_contactos);
        ContactosAdapter contactoAdapter = new ContactosAdapter(this, listaContactosListView);
        contactos.setAdapter(contactoAdapter);

        contactos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contacto contactoSeleccionado = listaContactosListView.get(position);

                Intent intent = new Intent(MainActivity.this, ContactoActivity.class);
                intent.putExtra("contacto", (CharSequence) contactoSeleccionado);

                startActivity(intent);
            }
        });
    }

    private void mostrarConfiguracion(){

        Calendar calendario = Calendar.getInstance();
        int hora = calendario.get(Calendar.HOUR_OF_DAY);
        int minuto = calendario.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        horaAlarma = hourOfDay;
                        minutoAlarma = minute;

                        confAlarma(horaAlarma, minutoAlarma);



                        Toast.makeText(MainActivity.this, "Hora seleccionada: " + hourOfDay + ":" + minute, Toast.LENGTH_SHORT).show();
                    }
                }, hora, minuto, true);

        timePickerDialog.show();

    }

    private void confAlarma(int hora, int minuto){
        Calendar calendario = Calendar.getInstance();
        calendario.setTimeInMillis(System.currentTimeMillis());
        calendario.set(Calendar.HOUR_OF_DAY, hora);
        calendario.set(Calendar.MINUTE, minuto);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, GestorAlarmas.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE);

        alarmManager.cancel(pendingIntent);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendario.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle("Birthday Helper");
    }
}