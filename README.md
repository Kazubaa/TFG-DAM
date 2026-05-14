# Descripción del proyecto
Se trata de una aplicación que integra un concesionario de motos junto a un taller para mayor accesibilidad. Dispone de una aplicación móvil que mejora la gestión de reservas y citas, tanto de vehículos como de taller. Permite a los usuarios solicitar el mantenimiento de sus motos o consultar el catálogo de motos disponibles para su compra. Para los empleados, permite subir y actualizar motos nuevas y de segunda mano, gestionar reservas y controlar las citas del taller para el mantenimiento de los vehículos de los clientes.

# Información sobre despliegue

1. Introducción
Este documento describe paso a paso cómo desplegar la aplicación. El sistema se compone de un backend REST hecho con Spring Boot, una base de datos MySQL y una aplicación móvil en Kotlin.
El despliegue está diseñado para ser sencillo usando Docker. El backend se expone mediante ngrok para poder acceder desde el exterior sin necesidad de configurar puertos en el router.

2. Requisitos previos
Software obligatorio
SoftwareVersión mínimaUsoDocker Desktop4.xContenedores del backend y base de datosDocker Composev2Orquestación de contenedoresGit2.xClonar el repositoriongrok3.39+Exposición pública del backendAndroid StudioIguana o superiorCompilación de la app móvil
Recursos de hardware recomendados

Procesador con 4 núcleos.
8 GB de memoria RAM (4 GB libres para Docker).
10 GB de espacio libre en disco.
Conexión a internet estable para descargar imágenes y mantener el túnel activo.

Cuentas necesarias
Crea una cuenta gratuita en ngrok.com para obtener el authtoken y reservar un dominio fijo.

3. Estructura del proyecto
   Backend y FrontEnd
   
<img width="381" height="619" alt="Captura de pantalla 2026-05-10 184838" src="https://github.com/user-attachments/assets/1c405711-b785-4239-b46f-e0179a760d43" />


<img width="221" height="619" alt="Captura de pantalla 2026-05-10 192408" src="https://github.com/user-attachments/assets/9c6bd60b-3196-462a-b65d-2c8a713abe72" />



5. Despliegue del backend
a) Arranque con Docker
Abre una terminal en la carpeta del proyecto y ejecuta:
bashcd C:\Users\xdlol\Desktop\proyecto   # ajusta la ruta a tu máquina
docker compose up -d
Docker descargará las imágenes eclipse-temurin:21-jre y mysql:8.1 la primera vez y arrancará los dos servicios. Para ver que todo está corriendo:
bashdocker ps
Para ver los logs en tiempo real:
bashdocker compose logs -f backend
b) Arranque con ngrok
Instalación y configuración:

Descarga ngrok desde https://ngrok.com/download e instálalo.
Registra el authtoken en ngrok:

bashngrok config add-authtoken <tu-token>
El token se guarda en C:\Users\<usuario>\AppData\Local\ngrok\ngrok.yml. Si no existe ese archivo, ngrok dará error de lectura.
Archivo de configuración backend.yml:
Crea el siguiente archivo de configuración:
yamlversion: "2"
authtoken: <token-proporcionado-por-ngrok>
tunnels:
  backend:
    proto: http
    addr: 8080
    domain: vendor-pasture-scalding.ngrok-free.dev

Sustituye el domain por el dominio fijo que tengas reservado en tu cuenta de ngrok.

Arrancar el túnel:
bashngrok start backend
La consola de ngrok mostrará el estado de la sesión y la URL pública activa. Anota la URL Forwarding que aparece (p. ej. https://vendor-pasture-scalding.ngrok-free.dev).

5. Despliegue de la aplicación móvil
a) Configuración de BASE_URL
Abre el archivo de constantes de la app:
motos/app/src/main/java/com/example/motos/utils/Constants.kt
Sustituye la URL por la que te proporciona ngrok:
<img width="610" height="64" alt="Captura de pantalla 2026-05-10 195746" src="https://github.com/user-attachments/assets/5a0f3a7b-38b1-451b-afeb-c18b9c5678af" />


Importante: la URL debe terminar en /. Si no, Retrofit lanzará una excepción al arrancar.


6. Generación del APK

En Android Studio ve a Build → Generate Signed App Bundle / APK…
Selecciona APK y pulsa Next.
Crea o selecciona un keystore (archivo .jks).
Elige el variant release y pulsa Finish.
El APK se genera en app/release/app-release.apk.


7. Instalación del APK

Transfiere el APK al dispositivo Android (por cable, email o cualquier método).
Abre el archivo en el dispositivo y, si se solicita, permite la instalación desde fuentes desconocidas.
Tras instalar, abre la app y comprueba que se conecta al backend.


8. Mantenimiento y resolución de problemas
8.1 Problemas frecuentes
La app no se conecta al backend

Verifica que ngrok está en ejecución y que la URL en Constants.kt coincide exactamente con la que muestra ngrok.
Confirma que el dispositivo tiene conexión a internet (datos móviles o Wi-Fi).
Revisa el log de Logcat en Android Studio buscando errores de OkHttp.

No se reproducen los vídeos

Comprueba que el archivo de vídeo está en uploads/videos/motonueva/ del servidor.
Verifica que la entidad MotoNueva tiene el campo videoFile correctamente guardado en la base de datos.
Asegúrate de que WebConfig en el backend mapea /uploads/** a /app/uploads/.

No se ven las imágenes en el listado

Comprueba que las imágenes están subidas con el tipo correcto (GALERIA o R360).
Verifica que el campo url de ImagenMotoNueva contiene la ruta correcta (motonueva/<archivo>.jpg).
Revisa que Glide construye bien la URL: BASE_URL + uploads/imagenes/ + url.

8.2 Copia de seguridad de datos
Los elementos importantes a respaldar son:

Volumen de MySQL: datos de la base de datos (db_data).
Carpeta uploads/: imágenes y vídeos subidos.

Para hacer una copia de seguridad del volumen de MySQL:
bashdocker run --rm -v concesionario_db_data:/data -v $(pwd):/backup alpine \
  tar czf /backup/backup_db.tar.gz /data
8.3 Comandos útiles
ComandoDescripcióndocker compose up -dArrancar todos los servicios en segundo planodocker compose downParar y eliminar los contenedoresdocker compose build --no-cacheReconstruir la imagen desde cerodocker compose logs -f backendVer logs del backend en tiempo realdocker compose logs -f dbVer logs de MySQLdocker psVer contenedores en ejecución


# Información sobre cómo usarlo

Introducción
Este manual está pensado para el uso del usuario junto a la aplicación. Combina todo lo necesario para que el usuario haga el uso correcto de ella.

Primeros pasos
1. Inicio de sesión
Al abrir la aplicación nos lleva a la pantalla de inicio de sesión, donde debemos introducir nuestros datos de usuario y contraseña y pulsar ENTRAR.
<img width="400" height="600" alt="image" src="https://github.com/user-attachments/assets/8e732ad3-5494-4199-ab3b-dd49b66f151f" />


3. Registro de usuario
En caso de no tener usuario creado, pulsaremos sobre Registrarse e introduciremos nuestras credenciales:

<img width="400" height="600" alt="image" src="https://github.com/user-attachments/assets/cf0a0b12-0048-4200-af49-c0ca09fe51f1" />


Usuario: nombre de usuario que quieres usar.
Contraseña: mínimo 4 caracteres.
Correo electrónico: dirección real donde recibirás el email de confirmación.

Después de pulsar REGISTRARSE recibirás un email en la dirección indicada. Debes pulsar el enlace de confirmación antes de poder iniciar sesión. Si el correo introducido ya está en uso, la app te avisará para que utilices otro.
3. Acceso como invitado
Si no quieres crear una cuenta, pulsa Continuar como invitado. Tendrás acceso de solo lectura al catálogo de motos nuevas y de segunda mano, sin posibilidad de reservar ni pedir citas.

En caso de que te hayas olvidado la contraseña hay un boton para resetearla:

<img width="400" height="600" alt="image" src="https://github.com/user-attachments/assets/c4f91a12-1c9b-4aed-92a5-544053b67656" />

Estructura de la aplicación
1. Barra de navegación
Dentro de la aplicación hay 5 secciones:

-Motos nuevasCatálogo organizado por marcas y categorías.

-Segunda manoCatálogo de motos de segunda mano.

-InicioOfertas en motos y promociones de taller.

-TallerCitas y presupuestos para tu moto.

-AjustesPerfil, reservas, citas y opciones según el rol.

3. Menú de ajustes
Al pulsar el icono de ajustes aparece un menú contextual con las opciones disponibles según tu rol:

Perfil — editar datos personales.
Mis reservas — reservas de motos de segunda mano.
Mis citas — citas en el taller.
Cerrar sesión.


Perfil
En el perfil puedes modificar tus datos personales (nombre, email y teléfono) y gestionar tus vehículos.

Pulsa GUARDAR para guardar los cambios.
En la sección MIS VEHÍCULOS, pulsa el botón + para añadir una nueva moto introduciendo matrícula, marca, modelo y kilómetros.
Pulsa el icono de papelera para eliminar un vehículo.
Pulsa sobre un vehículo para ver su historial de reparaciones.


Nota: debes tener al menos un vehículo añadido para poder pedir cita en el taller.


Mis citas
Accede desde Ajustes → Mis citas o desde la pestaña Taller. Verás el listado de citas con su estado (PENDIENTE, ACEPTADA, COMPLETADA, etc.).
Para pedir una nueva cita, pulsa el botón +:

Selecciona tu moto del desplegable.
Elige el tipo: Revisión o Mantenimiento.
Añade una descripción del problema o servicio (opcional).
Pulsa Continuar, selecciona la fecha (de lunes a viernes) y elige la hora disponible.

Cuando el mecánico genere un presupuesto, podrás verlo pulsando sobre la cita y aceptarlo o rechazarlo. Una vez terminada la reparación, recibirás una notificación en el taller indicando que la moto está lista para recoger.

Mis reservas
Accede desde Ajustes → Mis reservas. Verás la lista de reservas de motos de segunda mano que has realizado, con el estado de cada una (PENDIENTE, ACEPTADA, CANCELADA, RECHAZADA).
Pulsa sobre una reserva para ver los detalles de la moto reservada.

Taller
La pestaña Taller muestra el historial completo de citas y mantenimientos realizados a tus motos.
Al pulsar sobre una cita con presupuesto puedes:

Ver el desglose de conceptos (piezas, mano de obra, tareas).
Ver el subtotal, IVA (21%) y total.
Pulsar APROBAR para aceptar el presupuesto (la moto pasa a estado TALLER).
Pulsar RECHAZAR para rechazar el presupuesto.

Cuando la reparación esté lista, aparecerá la notificación "Tienes X moto(s) lista(s) para recoger". Al confirmar la recogida la cita pasa a estado COMPLETADA.

Motos de segunda mano
La sección Segunda mano muestra el catálogo de motos disponibles con foto, marca, modelo, precio, cilindrada, kilómetros y CV.

Pulsa el icono de filtro (≡) para buscar con autocompletado por marca, modelo, CV, km y cilindrada mínima.
Pulsa APLICAR para filtrar o LIMPIAR para quitar los filtros.
Pulsa sobre una moto para ver sus detalles y solicitar una reserva pulsando el botón de reserva.


Motos nuevas
La sección Motos muestra una cuadrícula de marcas (Honda, Yamaha, KTM, Kawasaki, Suzuki…). Al pulsar una marca, elige la categoría (Supersport, Naked, Touring, Scooter, 125 cc). Dentro verás la lista de modelos disponibles.
Al pulsar un modelo accedes a su ficha completa con:

Galería de imágenes — desliza el dedo hacia la izquierda o derecha para pasar fotos.
Vista 360° — pulsa el botón 360° y arrastra el dedo horizontalmente para rotar la moto. Pulsa GALERÍA para volver a las fotos.
Vídeo — si el modelo dispone de vídeo, aparece el reproductor debajo de la galería con opción de pantalla completa.
Especificaciones — cilindrada, potencia, peso y categoría.
Descripción del modelo.


Inicio
La pantalla de inicio muestra las promociones activas del concesionario:

Ofertas en motos — imágenes de modelos destacados.
Promoción de taller — descuentos y servicios especiales.

# Autores
KazMotors: Antonio Jesus Castillo Henares

