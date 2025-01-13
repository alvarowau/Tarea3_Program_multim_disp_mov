package org.alvarowau.tarea3.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import org.alvarowau.tarea3.db.GestorBBDD;
import org.alvarowau.tarea3.model.Contacto;

import java.util.ArrayList;

public class GestorContactos {

    private final Context context;
    private final GestorBBDD gestorBD;

    public GestorContactos(Context context, GestorBBDD gestorBD) {
        this.context = context;
        this.gestorBD = gestorBD;
    }

    public ArrayList<Contacto> leerContactos() {
        ArrayList<Contacto> listaContactos = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
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
            String nombreAnterior = null;
            Contacto contactoActual = null;

            do {
                int idContacto = cursor.getInt(idContactoIndex);
                String nombre = cursor.getString(nombreID);
                String telefono = cursor.getString(telefonoID);
                String fechaNacimiento = GenerateDate.generarFechaNacimiento();

                // Consultar la fecha de nacimiento del contacto
                Uri birthdayUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(idContacto));
                Cursor cursorBirthday = contentResolver.query(
                        ContactsContract.Data.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Event.START_DATE},
                        ContactsContract.Data.CONTACT_ID + " = ? AND " +
                                ContactsContract.Data.MIMETYPE + " = ? AND " +
                                ContactsContract.CommonDataKinds.Event.TYPE + " = ?",
                        new String[]{String.valueOf(idContacto),
                                ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE,
                                String.valueOf(ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY)},
                        null);

                if (cursorBirthday != null && cursorBirthday.moveToFirst()) {
                    int startDateIndex = cursorBirthday.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);
                    if (startDateIndex != -1) {
                        fechaNacimiento = cursorBirthday.getString(startDateIndex);
                    }
                    cursorBirthday.close();
                }

                if (!nombre.equals(nombreAnterior)) {
                    if (contactoActual != null) {
                        listaContactos.add(contactoActual);
                    }
                    contactoActual = new Contacto(idContacto, nombre, telefono, fechaNacimiento);
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



    public ArrayList<Contacto> obtenerContactosListView() {
        boolean existeTabla = gestorBD.existeTabla("cumples");
        if (!existeTabla) {
            gestorBD.crearTabla();
            ArrayList<Contacto> listaContactos = leerContactos();
            gestorBD.guardarTodosContactos(listaContactos);
        }
        return gestorBD.obtenerContactosListView(context);
    }

    public ArrayList<String> obtenerTelefonos(String nombreContacto) {
        ArrayList<String> telefonos = new ArrayList<>();

        ContentResolver contentResolver = context.getContentResolver();
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


    public Contacto devolverContactoID(int id){
        return gestorBD.obtenerContactoPorId(id);
    }

    public String obtenerImagenDeContacto(int idContacto) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(idContacto));
        Cursor cursor = contentResolver.query(uri, new String[]{ContactsContract.Contacts.PHOTO_URI}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int photoUriIndex = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI);
            String photoUri = cursor.getString(photoUriIndex);
            cursor.close();
            return photoUri != null ? photoUri : null;  // Si no tiene foto, devuelve null
        }
        return null;  // Si no se encuentra la imagen, devuelve null
    }
}
