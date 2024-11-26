Link: https://github.com/Jaime1999l/Jaime_Plataforma_Voluntariado_PDE.git

# Plataforma de Voluntariado

## Descripción General

La "Plataforma de Voluntariado" es una aplicación diseñada para conectar a voluntarios con oportunidades de voluntariado en diversas organizaciones. Ofrece una interfaz intuitiva que permite a los usuarios buscar, filtrar y registrarse en actividades de voluntariado según sus intereses y ubicación.

## Características Principales

- **Interfaz Modular**: Utiliza fragmentos para una experiencia de usuario flexible y personalizable.
- **Búsqueda y Filtrado**: Permite a los usuarios encontrar oportunidades de voluntariado basadas en diversos criterios.
- **Sugerencias Basadas en Ubicación**: Emplea sensores del dispositivo para recomendar actividades cercanas.
- **Gestión de Usuarios**: Incluye funcionalidades de registro, inicio de sesión y perfiles de usuario.

## Descripción de Clases

### `PantallaPrincipalActivity`
Controla la navegación principal de la aplicación y sirve como punto central para enlazar fragmentos clave como el menú y la lista de actividades.

### `ActividadVoluntariado`
Representa una actividad de voluntariado. Incluye información como el nombre de la actividad, su descripción, ubicación y fecha.

### `Usuario`
Modelo que almacena los datos del usuario, incluyendo identificador, nombre, correo electrónico y contraseña cifrada.

### `LoginActivity`
Gestiona el inicio de sesión de los usuarios, validando sus credenciales contra la base de datos y redirigiéndolos a la pantalla principal en caso de éxito.

### `RegisterActivity`
Permite a nuevos usuarios registrarse en la plataforma, validando la información ingresada antes de crear un nuevo registro en la base de datos.

### `ActivitiesFragment`
Fragmento que muestra una lista de actividades disponibles para los usuarios. Utiliza un RecyclerView para renderizar dinámicamente las actividades.

### `MenuFragment`
Fragmento que ofrece opciones de navegación, como acceder al perfil, buscar actividades y gestionar configuraciones.

### `SearchActivity`
Pantalla que permite a los usuarios buscar y filtrar actividades de voluntariado. Los criterios incluyen ubicación, fecha y tipo de actividad.

### `FirebaseHandler`
Clase encargada de gestionar las interacciones con Firebase, como la sincronización de datos entre la base de datos remota y la aplicación local.

### `AppDatabase`
Define la estructura y configuración de la base de datos local de la aplicación utilizando Room.

### `Adapters`
Clases adaptadoras, como `ActivitiesAdapter`, que gestionan la representación de listas en la interfaz de usuario, proporcionando una conexión entre los datos y los componentes visuales.

## Uso de Fragmentos para una Interfaz Modular

La aplicación emplea fragmentos para crear una interfaz de usuario modular y personalizable. Por ejemplo, `ActivitiesFragment` muestra la lista de actividades disponibles, mientras que `MenuFragment` proporciona opciones de navegación. Estos fragmentos se gestionan mediante `FragmentManager` y se pueden reemplazar dinámicamente según las interacciones del usuario.

## Sistema de Búsqueda y Filtrado de Oportunidades

Se implementa un sistema de búsqueda y filtrado que permite a los usuarios encontrar oportunidades de voluntariado basadas en criterios como ubicación, fecha y tipo de actividad. Esto se logra mediante consultas a la base de datos y la actualización dinámica de la interfaz para reflejar los resultados.

## Fragmento de Próximas Oportunidades en la Pantalla de Inicio

La aplicación incluye un fragmento que muestra las próximas oportunidades de voluntariado en la pantalla de inicio del dispositivo. Este fragmento se actualiza periódicamente para reflejar las actividades más recientes y relevantes para el usuario en función de su ubicación.

## Sugerencias Basadas en Ubicación

Utilizando los sensores del dispositivo, la aplicación proporciona sugerencias de voluntariado basadas en la ubicación actual del usuario. Esto se implementa mediante la obtención de coordenadas GPS y la comparación con las ubicaciones de las actividades disponibles, ofreciendo así recomendaciones personalizadas.
