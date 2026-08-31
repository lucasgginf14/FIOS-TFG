# FIOS

FIOS es una aplicación web desarrollada como Trabajo Fin de Grado para centralizar diferentes necesidades relacionadas con la actividad musical, con especial atención a la gestión y reserva de espacios musicales.

La plataforma permite consultar espacios, gestionar su disponibilidad y realizar reservas, además de incorporar funcionalidades relacionadas con bandas, ofertas de reclutamiento, eventos musicales, mensajería, valoraciones y búsqueda en lenguaje natural.

El proyecto se distribuye preparado para su ejecución mediante Docker Compose, incluyendo el backend, el frontend y una base de datos inicial con información de prueba.

## Funcionalidades principales

### Espacios musicales
- Consulta y búsqueda de espacios musicales.
- Creación y edición de espacios.
- Gestión de equipamiento.
- Configuración de horarios habituales.
- Excepciones de disponibilidad para fechas concretas.
- Consulta automática de franjas disponibles.
- Espacios favoritos.

### Reservas
- Creación y seguimiento de reservas.
- Gestión de reservas recibidas por los propietarios de espacios.
- Aceptación, rechazo, cancelación y finalización de reservas.
- Cálculo del precio según horarios y disponibilidad configurada.
- Mensajería asociada a cada reserva.
- Valoraciones de espacios y usuarios tras completar una reserva.

### Bandas y reclutamiento
- Creación y gestión de bandas.
- Gestión de miembros y responsables.
- Publicación de ofertas de reclutamiento.
- Búsqueda de músicos según diferentes criterios.

### Eventos
- Consulta de eventos musicales.
- Creación y gestión de eventos internos.
- Reserva de entradas para eventos internos.
- Integración con Ticketmaster para incorporar eventos externos.
- Visualización geográfica de eventos y espacios.

### Búsqueda
- Búsqueda mediante filtros.
- Consultas escritas en lenguaje natural.
- Integración opcional con Google Gemini.
- Mecanismo local de interpretación cuando Gemini no está configurado.
- Visualización de resultados sobre mapas.

### Administración
- Gestión de usuarios y roles.
- Activación y desactivación de cuentas.
- Revisión y aprobación de espacios musicales.
- Supervisión de reservas, valoraciones y ofertas de reclutamiento.
- Gestión de eventos.
- Importación de eventos desde Ticketmaster.

## Tecnologías

### Backend
- Java 17
- Spring Boot
- Spring Web MVC
- Spring Security
- JWT
- JPA / Hibernate
- Maven

### Frontend
- Vue 3
- Vite
- Vue Router
- Axios
- Bootstrap 5
- Leaflet
- Vue I18n

### Persistencia e infraestructura
- PostgreSQL 16
- Docker
- Docker Compose
- Nginx

### Servicios externos
- Ticketmaster Discovery API
- Google Gemini
- Nominatim / OpenStreetMap

## Ejecución rápida

La forma recomendada de probar FIOS es mediante Docker Compose.

### Requisitos

- Docker Desktop instalado y en ejecución.
- Puerto `1234` disponible para el frontend.
- Puerto `8080` disponible para el backend.

No es necesario instalar Java, Node.js ni PostgreSQL para ejecutar la aplicación mediante Docker.

### 1. Preparar la configuración

Desde la carpeta raíz del proyecto:

#### Windows PowerShell

```powershell
Copy-Item .env.example .env
```

#### Linux o macOS

```bash
cp .env.example .env
```

El archivo `.env.example` contiene una configuración preparada para ejecutar la aplicación sin necesidad de introducir claves de servicios externos.

### 2. Iniciar FIOS

```bash
docker compose up --build -d
```

### 3. Comprobar los contenedores

```bash
docker compose ps
```

Los servicios `database`, `server` y `client` deben aparecer en ejecución. El servicio de PostgreSQL debe alcanzar el estado `healthy`.

### 4. Abrir la aplicación

- Aplicación web: `http://localhost:1234`
- Backend: `http://localhost:8080`
- API mediante el proxy del frontend: `http://localhost:1234/api`

En el primer arranque, PostgreSQL crea automáticamente el volumen de datos e importa el contenido inicial de:

```text
database/fios_database.sql
```

Esta importación solo se realiza cuando se crea por primera vez el volumen de PostgreSQL.

## Datos de demostración

El proyecto incluye datos iniciales preparados para poder recorrer las principales funcionalidades de la aplicación desde el primer arranque.

Las cuentas disponibles y los escenarios recomendados para probar FIOS se encuentran en:

[docs/USUARIOS_PRUEBA.md](docs/USUARIOS_PRUEBA.md)

Las credenciales incluidas son exclusivamente cuentas de demostración y no corresponden a servicios o cuentas reales.

## Documentación

La carpeta `docs/` contiene documentación adicional para ejecutar y probar el proyecto:

- [Guía de ejecución](docs/EJECUCION.md): inicio, parada, reinicio, logs y resolución de problemas.
- [Configuración](docs/CONFIGURACION.md): variables de entorno y configuración de servicios externos.
- [Usuarios de prueba](docs/USUARIOS_PRUEBA.md): cuentas preparadas y funcionalidades recomendadas para cada una.

## Estructura del proyecto

```text
FIOS_ENTREGA/
├── README.md
├── compose.yml
├── .env.example
├── VERSION.txt
│
├── server/
│   └── Backend desarrollado con Spring Boot
│
├── client/
│   └── Aplicación web desarrollada con Vue 3
│
├── database/
│   └── fios_database.sql
│
└── docs/
    ├── EJECUCION.md
    ├── CONFIGURACION.md
    └── USUARIOS_PRUEBA.md
```

## Detener la aplicación

Para detener los contenedores manteniendo los datos:

```bash
docker compose down
```

Para volver a iniciar posteriormente:

```bash
docker compose up -d
```

## Restaurar los datos iniciales

Si se quiere eliminar la base de datos local y recuperar exactamente el estado inicial incluido en el proyecto:

```bash
docker compose down -v
docker compose up --build -d
```

> `docker compose down -v` elimina el volumen de PostgreSQL y, por tanto, todos los cambios realizados desde el primer arranque.

## Logs

Para consultar los logs de todos los servicios:

```bash
docker compose logs -f
```

O de un servicio concreto:

```bash
docker compose logs -f database
docker compose logs -f server
docker compose logs -f client
```

## Pruebas

### Backend

#### Windows PowerShell

```powershell
Set-Location .\server
.\mvnw.cmd test
```

#### Linux o macOS

```bash
cd server
./mvnw test
```

### Frontend

Desde `client/`:

```bash
npm ci
npm test
npm run lint
npm run build
```

## Servicios externos

### Ticketmaster

Ticketmaster se utiliza para buscar e importar eventos externos desde el área de administración.

La integración es opcional. Sin una clave configurada, la aplicación continúa funcionando y puede utilizar los eventos externos incluidos en los datos iniciales.

### Google Gemini

Gemini puede utilizarse como apoyo para interpretar las consultas escritas en lenguaje natural.

Su configuración también es opcional. Cuando Gemini no está disponible, FIOS utiliza un mecanismo local de interpretación para mantener esta funcionalidad.

Las variables necesarias para estas integraciones están documentadas en:

[docs/CONFIGURACION.md](docs/CONFIGURACION.md)

## Seguridad de la configuración

El repositorio no debe contener claves reales, contraseñas de servicios externos ni otros secretos.

El archivo:

```text
.env
```

es local y no debe versionarse.

Como referencia debe utilizarse:

```text
.env.example
```

con valores seguros o de ejemplo.

## Autor

Proyecto desarrollado por Lucas García García como Trabajo Fin de Grado del Grado en Enxeñaría Informática
