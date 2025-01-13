package org.alvarowau.tarea3.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.core.content.ContextCompat;

import org.alvarowau.tarea3.R;

import java.io.IOException;
import java.io.InputStream;

public class Contacto {
    private int idContacto;
    private String nombre;
    private String telefono;
    private String tipoAviso;
    private String mensaje;
    private String imagenURI

            ;
    private String fechaNac;

    public Contacto(){

    }
    public Contacto(int idContacto, String nombre, String telefono, String tipoAviso, String fechaNac, String mensaje, String imagenURI){
        this.idContacto = idContacto;
        this.nombre = nombre;
        this.telefono = telefono;
        this.tipoAviso = tipoAviso;
        this.mensaje = mensaje;
        this.fechaNac = fechaNac;
        this.imagenURI = imagenURI;
    }

    public Contacto(int idContacto, String nombre, String telefono, String tipoAviso, String imagenUri){
        this.nombre = nombre;
        this.telefono = telefono;
        this.tipoAviso = tipoAviso;
        this.imagenURI = imagenUri;
        this.idContacto = idContacto;
    }

    public Contacto (int idContacto, String nombre, String telefono){
        this.nombre = nombre;
        this.telefono = telefono;
        this.idContacto = idContacto;
    }

    public static Drawable generarDrawable (Context context, String imagenURI){
        Drawable imagenDrawable = null;
        if (imagenURI != null) {
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(Uri.parse(imagenURI));
                imagenDrawable = Drawable.createFromStream(inputStream, imagenURI);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            imagenDrawable = ContextCompat.getDrawable(context, R.drawable.avatarcontacto);
        }
        return imagenDrawable;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getTipoAviso() {
        return tipoAviso;
    }

    public void setTipoAviso(String tipoAviso) {
        this.tipoAviso = tipoAviso;
    }


    public String getImagenURI() {
        return imagenURI;
    }

    public void setImagenURI(String imagenURI) {
        this.imagenURI = imagenURI;
    }

    public String getFechaNac() {
        return fechaNac;
    }

    public void setFechaNac(String fechaNac) {
        this.fechaNac = fechaNac;
    }

    public int getIdContacto() {
        return idContacto;
    }

    public void setIdContacto(int idContacto) {
        this.idContacto = idContacto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}