package org.alvarowau.tarea3.db;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.ContactsContract;

import org.alvarowau.tarea3.model.Contacto;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ManagerDataBase extends SQLiteOpenHelper {

    private static final String DB_NAME = "Birthday";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "cumples";

    private final char TIPO_NOTI = 'M';
    private final String MENSAJE = "MUCHAS FELICIDADES";

    public ManagerDataBase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void crearTabla(){
        String queryCrearTabla = ("CREATE TABLE IF NOT EXISTS cumples(" +
                "ID integer," +
                "TipoNotif char(1)," +
                "Mensaje VARCHAR(160)," +
                "Telefono VARCHAR(15)," +
                "FechaNacimiento VARCHAR(15)," +
                "Nombre VARCHAR(128)" +
                ");");
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(queryCrearTabla);
    }

    public boolean existeTabla(String nombreTabla) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[]{"table", nombreTabla});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count > 0;
    }

    public void guardarTodosContactos(ArrayList<Contacto> listaContactos) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (Contacto contacto : listaContactos) {
            ContentValues values = new ContentValues();
            values.put("ID", contacto.getIdContacto());
            values.put("Nombre", contacto.getNombre());
            values.put("Telefono", contacto.getTelefono());
            values.put("FechaNacimiento", contacto.getFechaNac());
            values.put("TipoNotif", Character.toString(TIPO_NOTI));
            values.put("Mensaje", MENSAJE);

            db.insert("cumples", null, values);
        }

        db.close();
    }

    public ArrayList<Contacto> obtenerContactosListView(Context context) {
        ArrayList<Contacto> listaContactosListView = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM cumples";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int idContactoID = cursor.getColumnIndex("ID");
                int idContacto = cursor.getInt(idContactoID);
                int nombreID = cursor.getColumnIndex("Nombre");
                String nombre = cursor.getString(nombreID);
                int telefonoID = cursor.getColumnIndex("Telefono");
                String telefono = cursor.getString(telefonoID);
                int fechaNacID = cursor.getColumnIndex("FechaNacimiento");
                String fechaNac = cursor.getString(fechaNacID);
                int tipoNotifID = cursor.getColumnIndex("TipoNotif");
                String tipoNotif = cursor.getString(tipoNotifID);
                int mensajeID = cursor.getColumnIndex("Mensaje");
                String mensaje = cursor.getString(mensajeID);

                String[] projection = {
                        ContactsContract.CommonDataKinds.Phone.PHOTO_URI
                };

                Uri uriContacto = ContentUris.withAppendedId(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, idContacto);
                Cursor cursor2 = context.getContentResolver().query(uriContacto, projection, null, null, null);
                String imagenURI=null;

                if (cursor2 != null && cursor2.moveToFirst()) {
                    int imagenUriID = cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI);
                    imagenURI = cursor2.getString(imagenUriID);
                }



                Contacto contacto = new Contacto(idContacto, nombre, telefono, tipoNotif, fechaNac, mensaje, imagenURI);
                listaContactosListView.add(contacto);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listaContactosListView;
    }



    public ArrayList<Contacto> obtenerQuienCumple() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Contacto> quienCumple = new ArrayList<>();
        final Calendar c = Calendar.getInstance();
        int mes = c.get(Calendar.MONTH) + 1;
        int dia = c.get(Calendar.DAY_OF_MONTH);

        String fechaActual = String.format(Locale.getDefault(), "%02d-%02d", mes, dia);
        Cursor cursor = db.rawQuery("SELECT * FROM " + "cumples" +
                " WHERE strftime('%m-%d', " + "FechaNacimiento" + ") = ?", new String[]{fechaActual});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idContactoID = cursor.getColumnIndex("ID");
                int idContacto = cursor.getInt(idContactoID);
                int nombreID = cursor.getColumnIndex("Nombre");
                String nombre = cursor.getString(nombreID);
                int telefonoID = cursor.getColumnIndex("Telefono");
                String telefono = cursor.getString(telefonoID);
                int fechaNacID = cursor.getColumnIndex("FechaNacimiento");
                String fechaNac = cursor.getString(fechaNacID);
                int tipoNotifID = cursor.getColumnIndex("TipoNotif");
                String tipoNotif = cursor.getString(tipoNotifID);
                int mensajeID = cursor.getColumnIndex("Mensaje");
                String mensaje = cursor.getString(mensajeID);

                Contacto contacto = new Contacto(idContacto, nombre, telefono, tipoNotif, fechaNac, mensaje, "");
                quienCumple.add(contacto);
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();

        return quienCumple;
    }



    public Contacto obtenerContactoPorId(int idContacto) {
        SQLiteDatabase db = this.getReadableDatabase();
        Contacto contacto = null;

        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE ID = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(idContacto)});

        if (cursor != null && cursor.moveToFirst()) {
            int idContactoID = cursor.getColumnIndex("ID");
            int id = cursor.getInt(idContactoID);
            int nombreID = cursor.getColumnIndex("Nombre");
            String nombre = cursor.getString(nombreID);
            int telefonoID = cursor.getColumnIndex("Telefono");
            String telefono = cursor.getString(telefonoID);
            int fechaNacID = cursor.getColumnIndex("FechaNacimiento");
            String fechaNac = cursor.getString(fechaNacID);
            int tipoNotifID = cursor.getColumnIndex("TipoNotif");
            String tipoNotif = cursor.getString(tipoNotifID);
            int mensajeID = cursor.getColumnIndex("Mensaje");
            String mensaje = cursor.getString(mensajeID);

            contacto = new Contacto(id, nombre, telefono, tipoNotif, fechaNac, mensaje, null);

            cursor.close();
        }

        db.close();
        return contacto;
    }

    public boolean actualizarContacto(Contacto contacto) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("Nombre", contacto.getNombre());
        values.put("Telefono", contacto.getTelefono());
        values.put("FechaNacimiento", contacto.getFechaNac());
        values.put("TipoNotif", contacto.getTipoAviso());
        values.put("Mensaje", contacto.getMensaje());
        int rowsUpdated = db.update(TABLE_NAME, values, "ID = ?", new String[]{String.valueOf(contacto.getIdContacto())});

        db.close();

        return rowsUpdated > 0;
    }




}
