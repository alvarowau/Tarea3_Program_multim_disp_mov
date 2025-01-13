package org.alvarowau.tarea3;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.MaterialToolbar;

import org.alvarowau.tarea3.databinding.ActivityMainBinding;
import org.alvarowau.tarea3.db.ManagerDataBase;
import org.alvarowau.tarea3.iu.ContactoActivity;
import org.alvarowau.tarea3.model.Contacto;
import org.alvarowau.tarea3.util.ContactAdapter;
import org.alvarowau.tarea3.util.ContactClickListener;
import org.alvarowau.tarea3.util.ContactManager;
import org.alvarowau.tarea3.util.GestorAlarmas;
import org.alvarowau.tarea3.util.GestorNotificaciones;
import org.alvarowau.tarea3.util.GestorPermisos;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements ContactClickListener {

    public static final String ID_CONTACTO = "ID_nombre";

    private ContactManager gestorContactos;
    private ActivityMainBinding binding;
    private GestorNotificaciones gestorNotificaciones;
    private GestorPermisos gestorPermisos;
    private ContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupUI();
        initializeGestores();
        gestorPermisos.solicitarPermisos();
    }

    private void setupUI() {
        MaterialToolbar toolbar = binding.topAppBar;
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.setting) {
                mostrarConfiguracion();
                return true;
            }
            return false;
        });
    }

    private void initializeGestores() {
        ManagerDataBase gestorBD = new ManagerDataBase(this);
        gestorContactos = new ContactManager(this, gestorBD);
        gestorNotificaciones = new GestorNotificaciones(this);
        gestorPermisos = new GestorPermisos(this);

        adapter = new ContactAdapter(this);
        binding.rvSuperhero.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSuperhero.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == R.id.setting) {
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
        try {
            ArrayList<Contacto> listaContactos = gestorContactos.getContactListView();
            for (Contacto contacto : listaContactos) {
                contacto.setImagenURI(gestorContactos.getContactImage(contacto.getIdContacto()));
            }
            adapter.setContactoList(listaContactos);
        } catch (Exception e) {
            Log.e("MainActivity", "Error al mostrar contactos", e);
            Toast.makeText(this, "Error al cargar los contactos", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarConfiguracion() {
        Calendar calendario = Calendar.getInstance();
        int hora = calendario.get(Calendar.HOUR_OF_DAY);
        int minuto = calendario.get(Calendar.MINUTE);

        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            gestorNotificaciones.configurarAlarmaDiaria(hourOfDay, minute);


            gestionarAvisosAhora();

            Toast.makeText(this, "Hora seleccionada: " + hourOfDay + ":" + minute, Toast.LENGTH_SHORT).show();
        }, hora, minuto, true).show();
    }

    private void gestionarAvisosAhora() {
        ManagerDataBase gestorBD = new ManagerDataBase(this);
        ArrayList<Contacto> quienCumple = gestorBD.obtenerQuienCumple();

        GestorAlarmas gestorAlarmas = new GestorAlarmas();
        gestorAlarmas.gestionarAvisos(this, quienCumple);
    }

    private void navigateContactDetails(int id) {
        Intent intent = new Intent(this, ContactoActivity.class);
        intent.putExtra(ID_CONTACTO, id);
        startActivity(intent);
    }

    @Override
    public void onContactClicked(Contacto contacto) {
        navigateContactDetails(contacto.getIdContacto());
    }
}
