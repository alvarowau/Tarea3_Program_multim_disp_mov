package org.alvarowau.tarea3.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.core.content.ContextCompat;

import org.alvarowau.tarea3.R;

import java.io.IOException;
import java.io.InputStream;

public class Contacto implements Parcelable {
    private int idContacto;
    private String nombre;
    private String telefono;
    private String tipoAviso;
    private String mensaje;
    private String imagenURI;
    private String fechaNac;

    public Contacto() {}

    public Contacto(int idContacto, String nombre, String telefono, String tipoAviso, String fechaNac, String mensaje, String imagenURI) {
        this.idContacto = idContacto;
        this.nombre = nombre;
        this.telefono = telefono;
        this.tipoAviso = tipoAviso;
        this.mensaje = mensaje;
        this.fechaNac = fechaNac;
        this.imagenURI = imagenURI;
    }

    public Contacto(int idContacto, String nombre, String telefono, String tipoAviso, String imagenUri) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.tipoAviso = tipoAviso;
        this.imagenURI = imagenUri;
        this.idContacto = idContacto;
    }

    public Contacto(int idContacto, String nombre, String telefono, String fechaNac) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.idContacto = idContacto;
        this.fechaNac = fechaNac;
    }

    public static Drawable generarDrawable(Context context, String imagenURI) {
        Drawable imagenDrawable = null;
        if (imagenURI != null) {
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(Uri.parse(imagenURI));
                imagenDrawable = Drawable.createFromStream(inputStream, imagenURI);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            imagenDrawable = ContextCompat.getDrawable(context, R.drawable.avatarcontacto);
        }
        return imagenDrawable;
    }

    // Getters y setters
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

    // Métodos Parcelable
    protected Contacto(Parcel in) {
        idContacto = in.readInt();
        nombre = in.readString();
        telefono = in.readString();
        tipoAviso = in.readString();
        mensaje = in.readString();
        imagenURI = in.readString();
        fechaNac = in.readString();
    }

    public static final Creator<Contacto> CREATOR = new Creator<Contacto>() {
        @Override
        public Contacto createFromParcel(Parcel in) {
            return new Contacto(in);
        }

        @Override
        public Contacto[] newArray(int size) {
            return new Contacto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idContacto);
        dest.writeString(nombre);
        dest.writeString(telefono);
        dest.writeString(tipoAviso);
        dest.writeString(mensaje);
        dest.writeString(imagenURI);
        dest.writeString(fechaNac);
    }

    @Override
    public String toString() {
        return "Contacto{" +
                "idContacto=" + idContacto +
                ", nombre='" + nombre + '\'' +
                ", telefono='" + telefono + '\'' +
                ", tipoAviso='" + tipoAviso + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", imagenURI='" + imagenURI + '\'' +
                ", fechaNac='" + fechaNac + '\'' +
                '}';
    }
}
