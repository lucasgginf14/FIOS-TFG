# Configuracion

[Volver al README principal](../README.md)

El proyecto FIOS usa variables de entorno para separar la configuracion del codigo. En esta entrega se incluye `.env.example` como plantilla segura. Cada persona que ejecute el proyecto debe crear su propio `.env` local a partir de esa plantilla.

El archivo `.env.example` si forma parte de la entrega. El archivo `.env` no debe incluirse en el ZIP ni compartirse, porque puede contener contrasenas o claves privadas.

## Base de datos

| Variable | Uso | Obligatoria | Valor de ejemplo | Consecuencias de modificarla |
| --- | --- | --- | --- | --- |
| `DB_HOST` | Nombre del servicio de PostgreSQL al que se conecta el backend. | Si | `database` | En Docker Compose debe coincidir con el servicio `database`. Si se cambia sin ajustar la infraestructura, el backend no conectara. |
| `DB_PORT` | Puerto interno de PostgreSQL. | Si | `5432` | Debe coincidir con el puerto interno del contenedor de PostgreSQL. |
| `DB_NAME` | Nombre de la base de datos. | Si | `fios` | Si se cambia, PostgreSQL creara o usara otra base de datos. El volcado incluido esta preparado para `fios`. |
| `DB_USERNAME` | Usuario de PostgreSQL. | Si | `postgres` | Debe coincidir con el usuario configurado en el contenedor de la base de datos. |
| `DB_PASSWORD` | Contrasena local de PostgreSQL. | Si | `fios-local-password` | Si se cambia despues de crear el volumen, PostgreSQL conserva la contrasena anterior. Para empezar con otra contrasena habria que recrear el volumen. |

## Backend y perfil de Spring

| Variable | Uso | Obligatoria | Valor de ejemplo | Consecuencias de modificarla |
| --- | --- | --- | --- | --- |
| `SPRING_PROFILES_ACTIVE` | Indica el perfil de Spring Boot que debe usar el backend. | Si | `prod` | En esta entrega debe mantenerse en `prod`, que usa PostgreSQL y la configuracion preparada para Docker. |
| `HIBERNATE_DDL_AUTO` | Controla como Hibernate trata el esquema de la base de datos. | Si | `validate` | `validate` comprueba que el esquema restaurado coincide con las entidades Java. No debe cambiarse a modos que creen o modifiquen tablas en esta entrega. |
| `IMAGES_PATH` | Ruta interna donde el backend guarda imagenes subidas. | Si | `/app/uploads/images` | Debe coincidir con el volumen `server_uploads` configurado en `compose.yml`. |

## Seguridad JWT

JWT es el mecanismo usado por el backend para firmar los tokens de sesion.

| Variable | Uso | Obligatoria | Valor de ejemplo | Consecuencias de modificarla |
| --- | --- | --- | --- | --- |
| `JWT_SECRET_KEY` | Clave usada para firmar y validar tokens JWT. | Si | `una-clave-local-para-entrega-suficientemente-larga` | Si se cambia, los tokens emitidos antes dejan de ser validos. No uses una clave real en documentacion compartida. |

## Frontend y CORS

CORS es la politica que permite al navegador llamar al backend desde el origen del frontend.

| Variable | Uso | Obligatoria | Valor de ejemplo | Consecuencias de modificarla |
| --- | --- | --- | --- | --- |
| `CLIENT_HOST` | Origen permitido por el backend para peticiones del frontend. | Si | `http://localhost:1234` | Debe coincidir con la URL publica del frontend. Si no coincide, el navegador puede bloquear peticiones a la API. |
| `VITE_BACKEND_URL` | URL base de la API al construir el frontend. | Si | `/api` | En Docker debe ser `/api` para que Nginx redirija las peticiones al backend mediante el proxy. |

## Carga inicial de datos

| Variable | Uso | Obligatoria | Valor de ejemplo | Consecuencias de modificarla |
| --- | --- | --- | --- | --- |
| `DATABASE_LOADER_ENABLED` | Activa o desactiva el cargador Java de datos iniciales. | Si | `false` | En esta entrega debe permanecer desactivada. Los datos se restauran desde `database/fios_database.sql`, no desde el cargador Java. Ademas, `compose.yml` la fija explicitamente a `false` para evitar duplicados. |

## Ticketmaster

| Variable | Uso | Obligatoria | Valor de ejemplo | Consecuencias de modificarla |
| --- | --- | --- | --- | --- |
| `TICKETMASTER_ENABLED` | Indica si se activa la integracion con Ticketmaster. | No | `false` | `compose.yml` lee este valor desde `.env`. Para consultar la API en directo, ponlo a `true` en tu `.env` privado junto con una clave valida. Si se queda a `false`, se conservan los eventos externos ya cargados. |
| `TICKETMASTER_API_KEY` | Clave de acceso a Ticketmaster. | No | `<clave-ticketmaster>` | No incluyas claves reales en archivos compartidos. Sin clave valida, usa los eventos externos precargados para la demo. |

## Gemini

| Variable | Uso | Obligatoria | Valor de ejemplo | Consecuencias de modificarla |
| --- | --- | --- | --- | --- |
| `GEMINI_ENABLED` | Indica si se activa el analisis de busqueda con Gemini. | No | `false` | `compose.yml` lee este valor desde `.env`. Si esta a `true` y hay clave, la busqueda natural usa Gemini y conserva fallback local. |
| `GEMINI_API_KEY` | Clave de acceso a Gemini. | No | vacio | No incluyas claves reales en archivos compartidos. Sin clave valida, la busqueda natural funciona con el parser local. |

## Otras integraciones

El backend incluye geocodificacion mediante Nominatim, configurada en `server/src/main/resources/application.yml`. Esta integracion no requiere clave en la configuracion incluida.

## Restauracion de la base de datos

`compose.yml` monta el archivo:

```text
database/fios_database.sql
```

en esta ruta interna del contenedor de PostgreSQL:

```text
/docker-entrypoint-initdb.d/01-fios_database.sql
```

La imagen oficial de PostgreSQL ejecuta los archivos de esa carpeta solo cuando crea el directorio de datos por primera vez. Por eso:

- `docker compose down` mantiene la base de datos.
- `docker compose up -d` reutiliza el volumen existente.
- `docker compose down -v` elimina el volumen de PostgreSQL.
- Despues de `docker compose down -v`, `docker compose up --build -d` restaura los datos iniciales desde el volcado SQL.

Usa `docker compose down -v` solo cuando quieras borrar los cambios locales y restaurar los datos iniciales.
