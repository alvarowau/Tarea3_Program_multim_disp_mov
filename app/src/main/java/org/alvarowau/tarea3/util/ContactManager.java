package org.alvarowau.tarea3.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import org.alvarowau.tarea3.db.ManagerDataBase;
import org.alvarowau.tarea3.model.Contacto;

import java.util.ArrayList;

public class ContactManager {

    private final Context context;
    private final ManagerDataBase gestorBD;

    public ContactManager(Context context, ManagerDataBase gestorBD) {
        this.context = context;
        this.gestorBD = gestorBD;
    }

    public ArrayList<Contacto> getContactList() {
        ArrayList<Contacto> contactList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = {
                ContactsContract.Contacts._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
        };
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, projection, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            int contactIdIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String previousName = null;
            Contacto currentContact = null;

            do {
                int contactId = cursor.getInt(contactIdIndex);
                String name = cursor.getString(nameIndex);
                String phone = cursor.getString(phoneIndex);
                String birthDate = GenerateDate.generarFechaNacimiento();

                Uri birthdayUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactId));
                Cursor cursorBirthday = contentResolver.query(
                        ContactsContract.Data.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Event.START_DATE},
                        ContactsContract.Data.CONTACT_ID + " = ? AND " +
                                ContactsContract.Data.MIMETYPE + " = ? AND " +
                                ContactsContract.CommonDataKinds.Event.TYPE + " = ?",
                        new String[]{String.valueOf(contactId),
                                ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE,
                                String.valueOf(ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY)},
                        null);

                if (cursorBirthday != null && cursorBirthday.moveToFirst()) {
                    int startDateIndex = cursorBirthday.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);
                    if (startDateIndex != -1) {
                        birthDate = cursorBirthday.getString(startDateIndex);
                    }
                    cursorBirthday.close();
                }

                if (!name.equals(previousName)) {
                    if (currentContact != null) {
                        contactList.add(currentContact);
                    }
                    currentContact = new Contacto(contactId, name, phone, birthDate);
                    previousName = name;
                }
            } while (cursor.moveToNext());

            if (currentContact != null) {
                contactList.add(currentContact);
            }

            cursor.close();
        }
        return contactList;
    }

    public String getNameByPhone(String phone) {
        String name = null;

        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

        String selection = ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?";
        String[] selectionArgs = {phone};

        Cursor cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            name = cursor.getString(nameIndex);
            cursor.close();
        }

        return name;
    }

    public ArrayList<Contacto> getContactListView() {
        boolean isTableExist = gestorBD.existeTabla("cumples");
        if (!isTableExist) {
            gestorBD.crearTabla();
            ArrayList<Contacto> contactList = getContactList();
            gestorBD.guardarTodosContactos(contactList);
        }
        return gestorBD.obtenerContactosListView(context);
    }

    public ArrayList<String> getPhones(String contactName) {
        ArrayList<String> phones = new ArrayList<>();

        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?";
        String[] selectionArgs = {contactName};

        Cursor cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int phoneId = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String phone = cursor.getString(phoneId);
                phones.add(phone);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return phones;
    }


    public Contacto getContactById(int id){
        return gestorBD.obtenerContactoPorId(id);
    }

    public long getIdByName(String name) {
        long id = -1;

        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = {ContactsContract.Contacts._ID};

        String selection = ContactsContract.Contacts.DISPLAY_NAME + " = ?";
        String[] selectionArgs = {name};

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

    public String getLookUpId(int contactId) {
        String[] projection = {ContactsContract.Contacts._ID, ContactsContract.Contacts.LOOKUP_KEY};
        String filter = ContactsContract.Contacts._ID + " = ?";
        String[] filterArgs = {String.valueOf(contactId)};
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, filter, filterArgs, null);
        String lookUpKey = null;
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int lookUpKeyIndex = cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY);
                lookUpKey = cursor.getString(lookUpKeyIndex);
            }
        }
        cursor.close();
        return lookUpKey;
    }

    public String getContactImage(int contactId) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactId));
        Cursor cursor = contentResolver.query(uri, new String[]{ContactsContract.Contacts.PHOTO_URI}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int photoUriIndex = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI);
            String photoUri = cursor.getString(photoUriIndex);
            cursor.close();
            return photoUri != null ? photoUri : null;
        }
        return null;


    }


    public boolean updateContact(Contacto contacto){
        return gestorBD.actualizarContacto(contacto);
    }
}
