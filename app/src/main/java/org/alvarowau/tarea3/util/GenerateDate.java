package org.alvarowau.tarea3.util;

import java.util.Calendar;
import java.util.Random;

public class GenerateDate {

    private GenerateDate(){}

    public static String generarFechaNacimiento() {
        Calendar calendar = Calendar.getInstance();
        Random random = new Random();

        // Obtener la fecha actual
        int yearActual = calendar.get(Calendar.YEAR);
        int mesActual = calendar.get(Calendar.MONTH);
        int diaActual = calendar.get(Calendar.DAY_OF_MONTH);

        int edadMinima = 13;
        int yearNacimiento = yearActual - edadMinima - (random.nextInt(10));
        int mesNacimiento = random.nextInt(12);
        int diaNacimiento = random.nextInt(28) + 1;

        calendar.set(yearNacimiento, mesNacimiento, diaNacimiento);

        return String.format("%04d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
    }
}
