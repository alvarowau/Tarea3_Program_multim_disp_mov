package org.alvarowau.tarea3.iu;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import org.alvarowau.tarea3.MainActivity;
import org.alvarowau.tarea3.R;
import org.alvarowau.tarea3.databinding.ActivityContactoBinding;
import org.alvarowau.tarea3.db.GestorBBDD;
import org.alvarowau.tarea3.model.Contacto;
import org.alvarowau.tarea3.util.GestorContactos;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ContactoActivity extends AppCompatActivity implements View.OnClickListener {



    private ActivityContactoBinding binding;
    private int contactoId;

//    private Calendar calendar;
//    private SimpleDateFormat dateFormat;
    private Contacto contacto;
    private GestorContactos gestorContactos;
    private GestorBBDD gestorBD;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        gestorBD = new GestorBBDD(this);
        gestorContactos = new GestorContactos(this, gestorBD);


        contacto = new Contacto();
        Intent intent = getIntent();
        if (intent != null) {
            contactoId = intent.getIntExtra(MainActivity.ID_CONTACTO, -1);  // Si no se pasa el ID, se obtiene -1 como valor predeterminado
        }

        // Ahora puedes usar el contactoId para obtener el contacto desde la base de datos o el gestor de contactos
        if (contactoId != -1) {
            Log.d("pruebas", "el id es " + contactoId);
            contacto = gestorContactos.devolverContactoID(contactoId);
            Log.d("pruebas", "el contacto es: " +contacto.toString());
            if (contacto != null) {
                rellenarDatos(contacto);
            }
        }
    }



    private void rellenarDatos(Contacto contacto) {

//        if (contacto != null) {
//            ivFoto.setImageDrawable(Contacto.generarDrawable(this, contacto.getImagenURI()));
//            etNombre.setText(contacto.getNombre());
//            cbNotificacion.setChecked(contacto.getTipoAviso().equals("S"));
//            ArrayList<String> telefonos = gestorContactos.obtenerTelefonos(contacto.getNombre());
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, telefonos);
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            spTelefonos.setAdapter(adapter);
//            etFechaNac.setText(contacto.getFechaNac());
//            etMensaje.setText(contacto.getMensaje());
//            btnEditar.setOnClickListener(this);
//            btnGuardar.setOnClickListener(this);
//        }
    }

    @Override
    public void onClick(View v) {

    }

//    private void showDatePickerDialog() {
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
//            calendar.set(Calendar.YEAR, year1);
//            calendar.set(Calendar.MONTH, month1);
//            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//            etFechaNac.setText(dateFormat.format(calendar.getTime()));
//        }, year, month, day);
//        datePickerDialog.show();
//    }
}
