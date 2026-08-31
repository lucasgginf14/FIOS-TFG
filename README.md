# Proyecto FIOS

El proyecto FIOS es una aplicación web desarrollada como Trabajo Fin de Grado para conectar a músicos, bandas, espacios musicales, reservas y eventos. Esta carpeta contiene una versión final preparada para entrega: incluye el código del backend y del frontend, la configuración de Docker Compose y un volcado SQL con la base de datos inicial.

La idea es que cualquier persona pueda abrir la carpeta raíz del proyecto, arrancar los contenedores con Docker Desktop y probar la aplicación sin configurar una base de datos manualmente.

## Funcionalidades principales

- Registro e inicio de sesión con autenticación JWT.
- Consulta de espacios musicales, equipamiento, horarios y disponibilidad.
- Gestión de reservas, mensajes asociados y reseñas.
- Gestión de bandas, miembros y ofertas para incorporar músicos.
- Publicación y consulta de eventos musicales.
- Panel de administración para revisar usuarios, espacios, eventos, reservas y reseñas.
- Restauración automática de la base de datos inicial desde `database/fios_database.sql`.

## Tecnologías utilizadas

- Backend: Java 17, Spring Boot, Spring Security, JPA/Hibernate y Maven.
- Base de datos: PostgreSQL 16.
- Frontend: Vue 3, Vite, Bootstrap, Leaflet y npm.
- Servidor web del frontend: Nginx, con proxy hacia la API.
- Entorno de ejecución: Docker Compose.

## Requisitos previos

- Docker Desktop instalado y abierto.
- Puertos locales libres:
  - `1234` para el frontend.
  - `8080` para el backend.
- Java 17 y Node.js solo son necesarios si quieres ejecutar pruebas o compilar fuera de Docker.

## Estructura básica

```text
FIOS_ENTREGA/
├── README.md
├── compose.yml
├── .env.example
├── VERSION.txt
├── server/
├── client/
├── database/
│   └── fios_database.sql
└── docs/
    ├── EJECUCION.md
    ├── CONFIGURACION.md
    └── USUARIOS_PRUEBA.md
```

## Inicio rápido con Docker

Ejecuta los comandos desde la carpeta raíz del proyecto.

En Windows PowerShell:

```powershell
# Abre PowerShell en la carpeta raiz del proyecto
Copy-Item .env.example .env
docker compose up --build -d
docker compose ps
```

En Linux o macOS:

```bash
cd /ruta/a/FIOS_ENTREGA
cp .env.example .env
docker compose up --build -d
docker compose ps
```

En el primer arranque, PostgreSQL crea el volumen de la base de datos e importa automáticamente `database/fios_database.sql`. Esa importación solo se ejecuta cuando el volumen se crea por primera vez.

## URLs de acceso

- Frontend: `http://localhost:1234`
- Backend: `http://localhost:8080`
- API mediante el proxy del frontend: `http://localhost:1234/api`

## Comprobar el estado

```powershell
docker compose ps
```

El contenedor `database` debe aparecer como `healthy`. Los contenedores `server` y `client` deben estar en ejecución.

## Consultar registros

Para ver los registros de todos los contenedores:

```powershell
docker compose logs -f
```

Para revisar un servicio concreto:

```powershell
docker compose logs -f database
docker compose logs -f server
docker compose logs -f client
```

## Detener y volver a iniciar

Para detener el proyecto sin perder los datos:

```powershell
docker compose down
```

Este comando detiene y elimina los contenedores y la red de Docker Compose, pero mantiene los volúmenes. La base de datos permanece guardada.

Para volver a iniciar el proyecto:

```powershell
docker compose up -d
```

## Restaurar la base de datos inicial

Usa este procedimiento solo si quieres borrar la base de datos local y volver al estado inicial incluido en la entrega.

```powershell
docker compose down -v
docker compose up --build -d
```

Advertencia: `docker compose down -v` elimina el volumen de PostgreSQL. También elimina los cambios que se hayan realizado después del primer arranque.

## Pruebas del backend

En Windows PowerShell:

```powershell
Set-Location .\server
.\mvnw.cmd test
```

En Linux o macOS:

```bash
cd server
./mvnw test
```

## Lint y build del frontend

En Windows PowerShell:

```powershell
Set-Location .\client
npm ci
npm run lint
npm run build
```

En Linux o macOS:

```bash
cd client
npm ci
npm run lint
npm run build
```

## Cuentas de prueba

Las cuentas preparadas para probar el proyecto FIOS están documentadas en [Usuarios de prueba](docs/USUARIOS_PRUEBA.md). La mayoria usa `Fios2026!`; la cuenta completa de Lucas/The Rapants (`lucas@fios.com`) usa `12345678a`.

## Documentación adicional

- [Guía de ejecución](docs/EJECUCION.md): arranque, parada, reinicio, registros y resolución de problemas.
- [Configuración](docs/CONFIGURACION.md): variables de entorno y servicios externos.
- [Usuarios de prueba](docs/USUARIOS_PRUEBA.md): cuentas disponibles y qué se puede probar con cada una.

## Servicios externos opcionales

Ticketmaster y Gemini son opcionales y se configuran desde `.env`. Si estan activos y tienen clave valida, el backend los usa para importar eventos externos y para interpretar busquedas en lenguaje natural. Si no hay clave, FIOS sigue funcionando con eventos externos precargados y parser local.

No compartas claves reales ni incluyas el archivo `.env` en el ZIP de entrega. El archivo que debe compartirse es `.env.example`.
