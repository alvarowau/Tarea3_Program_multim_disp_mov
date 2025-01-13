package org.alvarowau.tarea3.util;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;

import org.alvarowau.tarea3.MainActivity;
import org.alvarowau.tarea3.R;
import org.alvarowau.tarea3.model.Contacto;
import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactoViewHolder> {

    private List<Contacto> contactoList;

    private int contador = 0;

    private ContactClickListener clickListener;

    public ContactAdapter(ContactClickListener clickListener) {
        this.contactoList = new ArrayList<>();
        this.clickListener = clickListener;
    }

    // Método para actualizar la lista de contactos
    public void setContactoList(List<Contacto> contactoList) {
        this.contactoList = contactoList;
        notifyDataSetChanged();
    }

    // Método que crea un nuevo ViewHolder
    @Override
    public ContactoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContactoViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contacs,parent,false));
    }

    @Override
    public void onBindViewHolder(ContactoViewHolder holder, int position) {
        holder.bind(contactoList.get(position), contador++);
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onContactClicked(contactoList.get(position));
            }
        });
    }

    // Método que retorna el número de elementos en la lista
    @Override
    public int getItemCount() {
        return contactoList.size();
    }


}
