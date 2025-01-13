package org.alvarowau.tarea3.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.alvarowau.tarea3.R;
import org.alvarowau.tarea3.model.Contacto;

import java.util.ArrayList;

public class ContactosAdapter extends BaseAdapter {
    private ArrayList<Contacto> contactos;
    private Context context;

    public ContactosAdapter(Context context, ArrayList<Contacto> contactos){
        this.contactos = contactos;
        this.context = context;
    }


    @Override
    public int getCount() {
        return this.contactos.size();
    }

    @Override
    public Object getItem(int position) {
        return getContactos().get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;

        if (item == null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            item = inflater.inflate(R.layout.contactos, null);
        }
        TextView nombre = item.findViewById(R.id.nombrecontacto);
        nombre.setText(contactos.get(position).getNombre());

        TextView telefono = item.findViewById(R.id.telefonocontacto);
        telefono.setText(contactos.get(position).getTelefono());

        TextView fecha = item.findViewById(R.id.fechacontacto);
        fecha.setText(contactos.get(position).getFechaNac());

        TextView aviso = item.findViewById(R.id.avisocontacto);
        String tipoAviso = contactos.get(position).getTipoAviso();
        if (tipoAviso.equals("S")){
            aviso.setText("Aviso: Enviar SMS");
        }else {
            aviso.setText("Aviso: Solo notificación");
        }


        ImageView imagen = item.findViewById(R.id.imagencontacto);
        String imagenURI = contactos.get(position).getImagenURI();

        imagen.setImageDrawable(Contacto.generarDrawable(this.context, imagenURI));

        return item;
    }

    public ArrayList<Contacto> getContactos() {
        return contactos;
    }
    public Context getContext() {
        return context;
    }

}
