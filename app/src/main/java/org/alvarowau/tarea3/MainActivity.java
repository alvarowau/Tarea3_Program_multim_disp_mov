package org.alvarowau.tarea3;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.alvarowau.tarea3.databinding.ActivityMainBinding;
import org.alvarowau.tarea3.db.GestorBBDD;
import org.alvarowau.tarea3.iu.ContactoActivity;
import org.alvarowau.tarea3.model.Contacto;
import org.alvarowau.tarea3.util.ContactAdapter;
import org.alvarowau.tarea3.util.ContactClickListener;
import org.alvarowau.tarea3.util.GestorContactos;
import org.alvarowau.tarea3.util.GestorNotificaciones;
import org.alvarowau.tarea3.util.GestorPermisos;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements ContactClickListener {

    public static final String ID_CONTACTO = "ID_nombre";
    private GestorContactos gestorContactos;
    private ActivityMainBinding binding;
    private GestorNotificaciones gestorNotificaciones;
    private GestorPermisos gestorPermisos;
    private ContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setupUI();
        initializeGestores();
        gestorPermisos.solicitarPermisos();
    }

    private void setupUI() {
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
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
        adapter = new ContactAdapter(this);
        binding.rvSuperhero.setHasFixedSize(true);
        binding.rvSuperhero.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        binding.rvSuperhero.setAdapter(adapter);
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
        for (Contacto con : listaContactosListView) {
            con.setImagenURI(gestorContactos
                    .obtenerImagenDeContacto(con
                            .getIdContacto()));
        }
        adapter.setContactoList(listaContactosListView);
    }


    private void navigateContactDetails(int id){
        Intent intent = new Intent(this, ContactoActivity.class);
        intent.putExtra(ID_CONTACTO,id);
        startActivity(intent);
    }



    @Override
    public void onContactClicked(Contacto contacto) {
        navigateContactDetails(contacto.getIdContacto());
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
