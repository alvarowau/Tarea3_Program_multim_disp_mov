package org.alvarowau.tarea3;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Calendar;

import org.alvarowau.tarea3.db.GestorBBDD;
import org.alvarowau.tarea3.iu.ContactoActivity;
import org.alvarowau.tarea3.model.Contacto;
import org.alvarowau.tarea3.util.ContactosAdapter;
import org.alvarowau.tarea3.util.GestorContactos;
import org.alvarowau.tarea3.util.GestorNotificaciones;
import org.alvarowau.tarea3.util.GestorPermisos;

public class MainActivity extends AppCompatActivity {

    private GestorContactos gestorContactos;
    private GestorNotificaciones gestorNotificaciones;
    private GestorPermisos gestorPermisos;

    public static final String NOMBRE_CONTACTO = "contacto_nombre";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();
        initializeGestores();
        gestorPermisos.solicitarPermisos();
    }

    private void setupUI() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupActionBar();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setLogo(R.mipmap.ic_launcher);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle("Birthday Helper");
        }
    }

    private void initializeGestores() {
        GestorBBDD gestorBD = new GestorBBDD(this);
        gestorContactos = new GestorContactos(this, gestorBD);
        gestorNotificaciones = new GestorNotificaciones(this);
        gestorPermisos = new GestorPermisos(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshContactosListView();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.manu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == R.id.felicitaciones) {
            mostrarConfiguracion();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle("Birthday Helper");
        if (gestorPermisos.todosLosPermisosConcedidos()) {
            mostrarContactos();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        gestorPermisos.manejarResultadoPermisos(requestCode, grantResults);
        if (gestorPermisos.todosLosPermisosConcedidos()) {
            mostrarContactos();
        }
    }

    private void mostrarContactos() {
        ArrayList<Contacto> listaContactosListView = gestorContactos.obtenerContactosListView();
        ListView contactos = findViewById(R.id.listview_contactos);
        ContactosAdapter contactoAdapter = new ContactosAdapter(this, listaContactosListView);
        contactos.setAdapter(contactoAdapter);

        contactos.setOnItemClickListener((parent, view, position, id) -> {
            Contacto contactoSeleccionado = listaContactosListView.get(position);

            // Creamos un Intent para abrir la pantalla de contacto
            Intent intent = new Intent(MainActivity.this, ContactoActivity.class);

            // Pasamos el ID del contacto (suponiendo que cada contacto tiene un ID único)
            intent.putExtra(NOMBRE_CONTACTO, contactoSeleccionado.getIdContacto());

            // Iniciamos la actividad ContactoActivity
            startActivity(intent);
        });
    }

    private void refreshContactosListView() {
        ListView listView = findViewById(R.id.listview_contactos);
        listView.invalidate();
        mostrarContactos();
    }

    private void mostrarConfiguracion() {
        Calendar calendario = Calendar.getInstance();
        int hora = calendario.get(Calendar.HOUR_OF_DAY);
        int minuto = calendario.get(Calendar.MINUTE);

        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            gestorNotificaciones.configurarAlarmaDiaria(hourOfDay, minute);
            Toast.makeText(this, "Hora seleccionada: " + hourOfDay + ":" + minute, Toast.LENGTH_SHORT).show();
        }, hora, minuto, true).show();
    }
}
