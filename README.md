# Tarea 3 - Programación de Múltiples Dispositivos Móviles

Este proyecto corresponde a la Tarea 3 de la materia **Programación de Múltiples Dispositivos Móviles**. Su objetivo es demostrar habilidades avanzadas en el desarrollo de aplicaciones móviles utilizando Android Studio y técnicas de optimización de código.

## Descripción

La aplicación desarrollada en este proyecto permite gestionar notificaciones de cumpleaños de los contactos almacenados en el teléfono. Los usuarios pueden elegir entre dos opciones principales para recibir recordatorios:

1. **Notificaciones**: Aviso directo en la barra de notificaciones del dispositivo.
2. **Envío de SMS**: Mensaje de texto automático al contacto que cumple años.

### Características principales

- **Gestor de notificaciones**: Permite configurar cómo y cuándo se recibirán los recordatorios.
- **Interfaz amigable**: Diseñada para facilitar la selección de preferencias.
- **Uso de contactos del teléfono**: Integra la agenda existente para automatizar los recordatorios de cumpleaños.

## Requisitos

- **Entorno de desarrollo**: Android Studio.
- **Lenguaje**: Java/Kotlin.
- **Versión de Android**: API 28 o superior.
- **Permisos requeridos**:
    - Acceso a los contactos del dispositivo.
    - Enviar y recibir mensajes SMS.
    - Mostrar notificaciones.

## Instalación

1. Clona este repositorio:
   ```bash
   git clone https://github.com/alvarowau/Tarea3_Program_multim_disp_mov.git
   ```

2. Abre el proyecto en Android Studio.

3. Configura los permisos necesarios en el archivo `AndroidManifest.xml`.

4. Ejecuta la aplicación en un dispositivo o emulador Android.

## Estructura del proyecto

- **`MainActivity`**: Clase principal que actúa como punto de entrada de la aplicación.
- **Clases auxiliares**: Se incluyen diversas clases para gestionar la lógica de notificaciones, integración con contactos y envío de mensajes SMS.

## Problemas conocidos

- Al intentar configurar recordatorios en una segunda instancia, la aplicación puede cerrarse inesperadamente. Se está trabajando en una solución.
- La barra de búsqueda aún no tiene implementación y no realiza ninguna acción cuando se utiliza.

## Contribuciones

Las contribuciones son bienvenidas. Si deseas colaborar, sigue estos pasos:

1. Haz un fork del repositorio.
2. Crea una rama para tu función o arreglo:
   ```bash
   git checkout -b mi-funcionalidad
   ```
3. Realiza tus cambios y realiza commits explicativos.
4. Envía un pull request detallando tus aportes.

## Licencia

Este proyecto está bajo la licencia MIT. Consulta el archivo [`LICENSE`](LICENSE) para más detalles.

---

_Desarrollado por AlvaroWau como parte de los proyectos de Programación de Múltiples Dispositivos Móviles._
