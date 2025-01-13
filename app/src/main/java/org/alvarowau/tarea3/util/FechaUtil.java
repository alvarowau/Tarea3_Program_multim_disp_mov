package org.alvarowau.tarea3.util;

import java.util.Calendar;

public class FechaUtil {

    // Método para convertir la fecha de "yyyy-MM-dd" a "dd/MM/yyyy"
    public static String convertirFechaParaIU(String fecha) {
        String[] partesFecha = fecha.split("-");

        if (partesFecha.length == 3) {
            int year = Integer.parseInt(partesFecha[0]);
            int month = Integer.parseInt(partesFecha[1]) - 1;
            int day = Integer.parseInt(partesFecha[2]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);

            // Formatear la fecha en el formato "dd/MM/yyyy"
            return String.format("%02d/%02d/%d", calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        }

        return "";
    }

    // Método para convertir la fecha de "dd/MM/yyyy" a "yyyy-MM-dd"
    public static String convertirFechaParaDDBB(String fecha) {
        String[] partesFecha = fecha.split("/");

        if (partesFecha.length == 3) {
            int day = Integer.parseInt(partesFecha[0]);
            int month = Integer.parseInt(partesFecha[1]) - 1;
            int year = Integer.parseInt(partesFecha[2]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);

            // Convertir la fecha en formato "yyyy-MM-dd"
            return String.format("%04d-%02d-%02d", calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        }

        return "";
    }
}