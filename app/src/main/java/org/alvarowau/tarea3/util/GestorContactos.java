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

    public ArrayList<Contacto> obtenerContactosListView() {
        boolean existeTabla = gestorBD.existeTabla("cumples");
        if (!existeTabla) {
            gestorBD.crearTabla();
            ArrayList<Contacto> listaContactos = leerContactos();
            gestorBD.guardarTodosContactos(listaContactos);
        }
        return gestorBD.obtenerContactosListView(context);
    }
}
