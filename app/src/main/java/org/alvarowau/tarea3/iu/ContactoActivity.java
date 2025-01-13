package org.alvarowau.tarea3.iu;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.alvarowau.tarea3.MainActivity;
import org.alvarowau.tarea3.R;
import org.alvarowau.tarea3.databinding.ActivityContactoBinding;
import org.alvarowau.tarea3.db.ManagerDataBase;
import org.alvarowau.tarea3.model.Contacto;
import org.alvarowau.tarea3.util.FechaUtil;
import org.alvarowau.tarea3.util.ContactManager;

import java.util.Calendar;
import java.util.List;

public class ContactoActivity extends AppCompatActivity {

    private ActivityContactoBinding binding;
    private int contactoId;
    private Contacto contacto;
    private ContactManager contactManager;
    private ManagerDataBase gestorBD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        gestorBD = new ManagerDataBase(this);
        contactManager = new ContactManager(this, gestorBD);

        contacto = new Contacto();
        contactoId = getIntent().getIntExtra(MainActivity.ID_CONTACTO, -1);

        if (contactoId != -1) {
            initContacto();
        } else {
            mostrarErrorYRedirigir("Error: ID de contacto inválido.");
        }

        initListeners();
    }

    private void initContacto() {
        contacto = contactManager.getContactById(contactoId);
        if (contacto != null) {
            contacto.setImagenURI(contactManager.getContactImage(contactoId));
            rellenarDatos();
        } else {
            mostrarErrorYRedirigir("Error: No se encontró el contacto.");
        }
    }

    private void initListeners() {
        binding.etFechaNac.setOnClickListener(v -> showDatePicker());
        binding.btnGuardar.setOnClickListener(view -> botonGuardarPulsed());
        binding.btnEditar.setOnClickListener(view -> navigateToContacts());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> setDateInEditText(dayOfMonth, month, year),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void setDateInEditText(int day, int month, int year) {
        String formattedDate = String.format("%02d/%02d/%d", day, month + 1, year);
        binding.etFechaNac.setText(formattedDate);
    }

    private void rellenarDatos() {
        if (contacto.getImagenURI() == null || contacto.getImagenURI().isEmpty()) {
            binding.ivContact.setImageResource(R.drawable.avatarcontacto);
        } else {
            Picasso.get().load(contacto.getImagenURI()).into(binding.ivContact);
        }

        binding.etNombre.setText(contacto.getNombre());
        List<String> phones = contactManager.getPhones(contacto.getNombre());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, phones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spTelefonos.setAdapter(adapter);

        String fechaFormateada = FechaUtil.convertirFechaParaIU(contacto.getFechaNac());
        binding.etFechaNac.setText(fechaFormateada);
        binding.etMensaje.setText(contacto.getMensaje());
        llenarCheckBox(contacto.getTipoAviso());
    }

    private void llenarCheckBox(String tipoAviso) {
        if ("T".equals(tipoAviso)) {
            binding.cbNoti.setChecked(true);
            binding.cbSms.setChecked(true);
        } else if ("M".equals(tipoAviso)) {
            binding.cbSms.setChecked(true);
        } else if ("N".equals(tipoAviso)) {
            binding.cbNoti.setChecked(true);
        }
    }

    private void botonGuardarPulsed() {
        String mensaje = binding.etMensaje.getText().toString();
        boolean notificaciones = binding.cbNoti.isChecked();
        boolean sms = binding.cbSms.isChecked();

        String tipoAviso = saverNotificaciones(notificaciones, sms);

        contacto.setMensaje(mensaje);
        contacto.setTipoAviso(tipoAviso);

        boolean edicion = gestorBD.actualizarContacto(contacto);
        if (edicion) {
            Toast.makeText(this, "Contacto actualizado correctamente", Toast.LENGTH_SHORT).show();
            volverAlMain();
        } else {
            Toast.makeText(this, "Error al actualizar el contacto", Toast.LENGTH_SHORT).show();
            volverAlMain();
        }
    }

    private String saverNotificaciones(boolean notificaciones, boolean sms) {
        if (notificaciones && sms) return "T";
        if (sms) return "M";
        return "N";
    }

    private void mostrarErrorYRedirigir(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
        volverAlMain();
    }

    private void volverAlMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToContacts() {
        String nombre = contactManager.getNameByPhone(contacto.getTelefono());
        long idCont = contactManager.getIdByName(nombre);

        String lookupKey = contactManager.getLookUpId((int) idCont);
        Uri selectedContactUri = ContactsContract.Contacts.getLookupUri(contacto.getIdContacto(), lookupKey);
        Intent editIntent = new Intent(Intent.ACTION_EDIT);
        editIntent.setDataAndType(selectedContactUri, ContactsContract.Contacts.CONTENT_ITEM_TYPE);
        editIntent.putExtra("finishActivityOnSaveCompleted", true);
        startActivity(editIntent);
    }
}
