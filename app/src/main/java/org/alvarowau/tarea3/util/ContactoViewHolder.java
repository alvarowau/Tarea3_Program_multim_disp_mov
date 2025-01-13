package org.alvarowau.tarea3.util;


import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.alvarowau.tarea3.R;
import org.alvarowau.tarea3.databinding.ItemContacsBinding;
import org.alvarowau.tarea3.model.Contacto;

public class ContactoViewHolder extends RecyclerView.ViewHolder {

    private ItemContacsBinding binding;

    public ContactoViewHolder(View itemView) {
        super(itemView);
        binding = ItemContacsBinding.bind(itemView);
    }

    // Método para vincular los datos del Contacto con las vistas
    public void bind(Contacto contacto, int contador) {
        if (contador % 2 == 0) {
            Log.d("ColorChange", "Color Par, el contrador es " + contador);
            binding.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(binding.cardView.getContext(), R.color.color_par));
        } else {
            Log.d("ColorChange", "Color Impar, el contrador es " + contador);
            binding.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(binding.cardView.getContext(), R.color.color_impar));
        }

        String tipoAviso = devolverTipoAviso(contacto.getTipoAviso());

        String recordatorio = "Recordatorio: " + tipoAviso;

        String uri = contacto.getImagenURI();
        if(uri == null || uri.isEmpty()){
            binding.imgContact.setImageResource(R.drawable.avatarcontacto);
        }else{
            Picasso.get().load(uri).into(binding.imgContact);
        }

        binding.tvContactName.setText(contacto.getNombre());
        binding.tvContactNumber.setText(contacto.getTelefono());
        binding.tvNotificationType.setText(recordatorio);

    }


    private String devolverTipoAviso(String letra) {
        String tipoAviso;
        switch (letra) {
            case "M":
                tipoAviso = "Enviar sms";
                break;
            case "N":
                tipoAviso = "Enviar Notificación";
                break;
            case "T":
                tipoAviso = "Enviar Mensaje y notificación";
                break;
            default:
                tipoAviso = "No se pudo recuperar";
                break;
        }
        return tipoAviso;
    }


}
