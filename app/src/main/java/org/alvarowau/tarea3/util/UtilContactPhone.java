package org.alvarowau.tarea3.util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

import org.alvarowau.tarea3.model.ContactPhone;

import java.util.ArrayList;
import java.util.List;

public class UtilContactPhone {

    public static List<ContactPhone> getContactSimple(ContentResolver contentResolver) {
        List<ContactPhone> contactList = new ArrayList<>();

        // Consultamos los contactos
        Cursor cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String phoneNumber = "";
                String birthdate = "";  // Este es un valor vacío, a menos que se obtenga de alguna otra fuente

                // Verificar si se obtienen valores válidos de los índices de columnas
                if (name != null && contactId != null) {
                    // Consultamos los números de teléfono asociados al contacto
                    Cursor phones = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{contactId},
                            null
                    );

                    if (phones != null && phones.moveToFirst()) {
                        // Aseguramos que el índice de la columna es válido
                        int phoneIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        if (phoneIndex >= 0) {
                            phoneNumber = phones.getString(phoneIndex);
                        }
                        phones.close();
                    }

                    // Crear objeto ContactPhone y añadirlo a la lista
                    ContactPhone contactPhone = new ContactPhone(name, phoneNumber, birthdate);
                    contactList.add(contactPhone);
                }
            }
            cursor.close();
        }
        return contactList;
    }
}
