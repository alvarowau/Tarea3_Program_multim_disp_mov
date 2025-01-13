package org.alvarowau.tarea3.util;

import java.util.Calendar;
import java.util.Random;

public class GenerateDate {

    private GenerateDate(){}

    public static String generarFechaNacimiento() {
        // Crear una instancia de Calendar
        Calendar calendar = Calendar.getInstance();
        Random random = new Random();

        // Obtener la fecha actual
        int yearActual = calendar.get(Calendar.YEAR);
        int mesActual = calendar.get(Calendar.MONTH);  // Mes actual (0-11)
        int diaActual = calendar.get(Calendar.DAY_OF_MONTH);

        // Calcular la fecha de nacimiento, asegurando que la persona tenga más de 13 años
        int edadMinima = 13;
        int yearNacimiento = yearActual - edadMinima - (random.nextInt(10));  // Edad entre 13 y 23 años
        int mesNacimiento = random.nextInt(12);  // Mes aleatorio (0-11)
        int diaNacimiento = random.nextInt(28) + 1;  // Día aleatorio (1-28 para evitar desbordes de mes)

        // Establecer la fecha de nacimiento en el calendario
        calendar.set(yearNacimiento, mesNacimiento, diaNacimiento);

        // Devolver la fecha como un string en formato "yyyy-MM-dd"
        return String.format("%04d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
    }
}
