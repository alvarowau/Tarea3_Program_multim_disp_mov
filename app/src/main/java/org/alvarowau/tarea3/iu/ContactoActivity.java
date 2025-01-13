package org.alvarowau.tarea3.iu;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.alvarowau.tarea3.R;
import org.alvarowau.tarea3.db.GestorBBDD;
import org.alvarowau.tarea3.model.Contacto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ContactoActivity extends AppCompatActivity implements View.OnClickListener {

    private Contacto contacto;
    EditText etNombre;
    CheckBox cbNotificacion;

    EditText etFechaNac;
    EditText etMensaje;
    Calendar calendar;
    SimpleDateFormat dateFormat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contacto);
        contacto = null;

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("contacto")) {
            contacto = (Contacto) intent.getSerializableExtra("contacto");

        }
        if (contacto != null) {
            rellenarDatos(contacto);
            calendar = Calendar.getInstance();
            dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            etFechaNac.setFocusable(false);

            etFechaNac.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePickerDialog();
                }
            });
        }

    }

    private void rellenarDatos(Contacto contacto) {
        ImageView ivFoto = findViewById(R.id.ivFoto);
        etNombre = findViewById(R.id.etNombre);
        cbNotificacion = findViewById(R.id.cbNotificacion);
        Spinner spTelefonos = findViewById(R.id.spTelefonos);
        etFechaNac = findViewById(R.id.etFechaNac);
        etMensaje = findViewById(R.id.etMensaje);
        Button btnEditar = findViewById(R.id.btnEditar);
        Button btnGuardar = findViewById(R.id.btnGuardar);

        if (contacto != null) {
            String imagenURI = contacto.getImagenURI();
            ivFoto.setImageDrawable(Contacto.generarDrawable(this, imagenURI));
            etNombre.setText(contacto.getNombre());
            if (contacto.getTipoAviso().equals("S")) {
                cbNotificacion.setChecked(true);
            } else {
                cbNotificacion.setChecked(false);
            }
            ArrayList<String> telefonos = new ArrayList<>();
            telefonos = obtenerTelefonos(contacto.getNombre());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, telefonos);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spTelefonos.setAdapter(adapter);
            etFechaNac.setText(contacto.getFechaNac());
            etMensaje.setText(contacto.getMensaje());
            btnEditar.setOnClickListener(this);
            btnGuardar.setOnClickListener(this);


        }
    }

    public ArrayList<String> obtenerTelefonos(String nombreContacto) {
        ArrayList<String> telefonos = new ArrayList<>();

        ContentResolver contentResolver = getContentResolver();
        String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?";
        String[] selectionArgs = {nombreContacto};

        Cursor cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int telefonoID = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String telefono = cursor.getString(telefonoID);
                telefonos.add(telefono);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return telefonos;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnEditar) {

            llamarIntentEditarContacto(contacto);

        } else if (v.getId() == R.id.btnGuardar) {
            contacto.setNombre(etNombre.getText().toString());
            if (cbNotificacion.isChecked()) {
                contacto.setTipoAviso("S");
            } else {
                contacto.setTipoAviso("N");
            }
            contacto.setFechaNac(etFechaNac.getText().toString());
            contacto.setMensaje(etMensaje.getText().toString());
            GestorBBDD gestorBD = new GestorBBDD(this);
            gestorBD.guardarUnContacto(contacto);
            finish();

        }

    }

    private void llamarIntentEditarContacto(Contacto contacto) {

        String nombreEnAgenda = obtenerNombrePorTelefono(contacto.getTelefono());
        long idEnAgenda = obtenerIdPorNombre(nombreEnAgenda);

        String mCurrentLookupKey = obtenerLookUpId((int) idEnAgenda);
        Uri selectedContactUri = ContactsContract.Contacts.getLookupUri(contacto.getIdContacto(), mCurrentLookupKey);
        Intent editIntent = new Intent(Intent.ACTION_EDIT);
        editIntent.setDataAndType(selectedContactUri, ContactsContract.Contacts.CONTENT_ITEM_TYPE);
        editIntent.putExtra("finishActivityOnSaveCompleted", true);
        startActivity(editIntent);
    }


    private String obtenerLookUpId(int idContact) {

        //https://developer.android.com/training/contacts-provider/modify-data

        String[] proyeccion = {ContactsContract.Contacts._ID,
                ContactsContract.Contacts.LOOKUP_KEY};
        String filtro = ContactsContract.Contacts._ID + " = ?";
        String[] args_filtro = {String.valueOf(idContact)};
        Cursor cur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, proyeccion, filtro, args_filtro, null);
        String lookUpKey = null;
        if (cur.getCount() > 0) {

            while (cur.moveToNext()) {
                int numLookUpKey = cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY);
                lookUpKey = cur.getString(numLookUpKey);
                int idindex = cur.getColumnIndex(ContactsContract.Contacts._ID);
                String id = cur.getString(idindex);
            }
        }
        cur.close();
        return lookUpKey;
    }


    private void showDatePickerDialog() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                etFechaNac.setText(dateFormat.format(calendar.getTime()));
            }
        }, year, month, day);

        datePickerDialog.show();
    }


    public String obtenerNombrePorTelefono(String telefono) {
        String nombre = null;

        ContentResolver contentResolver = getContentResolver();
        String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

        String selection = ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?";
        String[] selectionArgs = {telefono};

        Cursor cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int nombreIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            nombre = cursor.getString(nombreIndex);
            cursor.close();
        }

        return nombre;
    }

    public long obtenerIdPorNombre(String nombre) {
        long id = -1;

        ContentResolver contentResolver = getContentResolver();
        String[] projection = {ContactsContract.Contacts._ID};

        String selection = ContactsContract.Contacts.DISPLAY_NAME + " = ?";
        String[] selectionArgs = {nombre};

        Cursor cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            id = cursor.getLong(idIndex);
            cursor.close();
        }

        return id;
    }


}