# Guía de ejecución

[Volver al README principal](../README.md)

Esta guía explica cómo arrancar, detener, reiniciar y revisar el proyecto FIOS desde la carpeta raíz del proyecto.

## Requisitos

- Docker Desktop instalado y abierto.
- Puertos `1234` y `8080` libres en el equipo.
- Archivo `.env` creado a partir de `.env.example`.

En Windows PowerShell:

```powershell
# Abre PowerShell en la carpeta raiz del proyecto
Copy-Item .env.example .env
```

En Linux o macOS:

```bash
cd /ruta/a/FIOS_ENTREGA
cp .env.example .env
```

## Primer arranque

Desde la carpeta raíz del proyecto:

```powershell
docker compose up --build -d
```

Este comando construye las imágenes del backend y del frontend, crea los contenedores y los deja ejecutándose en segundo plano.

Durante el primer arranque, el contenedor de PostgreSQL crea el volumen `database_data` e importa el archivo `database/fios_database.sql`. Ese archivo contiene la estructura y los datos iniciales de la base de datos.

## Arranques posteriores

Cuando el volumen de PostgreSQL ya existe, puedes iniciar el proyecto sin reconstruir:

```powershell
docker compose up -d
```

PostgreSQL reutiliza el volumen existente. No vuelve a importar el archivo SQL.

## Estado de los contenedores

```powershell
docker compose ps
```

El resultado esperado es:

- `database`: en ejecución y con estado `healthy`.
- `server`: en ejecución.
- `client`: en ejecución.

## Consulta de registros

Para ver todos los registros en tiempo real:

```powershell
docker compose logs -f
```

Para consultar un contenedor concreto:

```powershell
docker compose logs -f database
docker compose logs -f server
docker compose logs -f client
```

También puedes consultar solo las últimas líneas:

```powershell
docker compose logs --tail=100 server
```

## URLs de acceso

- Frontend: `http://localhost:1234`
- Backend: `http://localhost:8080`
- API mediante el proxy del frontend: `http://localhost:1234/api`

## Detener sin perder datos

Hay dos formas habituales de detener el proyecto sin borrar la base de datos:

```powershell
docker compose stop
```

Este comando detiene los contenedores, pero los conserva.

```powershell
docker compose down
```

Este comando detiene y elimina los contenedores y la red creada por Docker Compose, pero mantiene los volúmenes. Es el comando recomendado cuando quieres cerrar el proyecto y volver a iniciarlo más adelante.

Con cualquiera de los dos comandos, los datos de PostgreSQL se conservan.

## Reinicio

Si has usado `docker compose down`, vuelve a iniciar el proyecto con:

```powershell
docker compose up -d
```

Si has usado `docker compose stop`, puedes volver a arrancar los contenedores con:

```powershell
docker compose start
```

## Reconstrucción de imágenes

Si cambias código del backend o del frontend, reconstruye las imágenes:

```powershell
docker compose up --build -d
```

Para forzar una reconstrucción completa sin usar caché:

```powershell
docker compose build --no-cache
docker compose up -d
```

## Restaurar los datos iniciales

Usa este procedimiento solo cuando quieras borrar la base de datos local y volver exactamente al estado incluido en `database/fios_database.sql`.

```powershell
docker compose down -v
docker compose up --build -d
```

Advertencia: `docker compose down -v` elimina el volumen de PostgreSQL. Eso borra los usuarios, reservas, mensajes, reseñas y cualquier otro cambio realizado después del primer arranque.

La restauración se produce en el siguiente arranque porque PostgreSQL detecta un volumen vacío y ejecuta de nuevo el archivo SQL montado en `/docker-entrypoint-initdb.d/`.

## Diferencia entre `down` y `down -v`

`docker compose down`:

- Detiene los contenedores.
- Elimina los contenedores.
- Elimina la red de Docker Compose.
- Conserva el volumen de la base de datos.
- Mantiene los cambios realizados en la aplicación.

`docker compose down -v`:

- Detiene los contenedores.
- Elimina los contenedores.
- Elimina la red de Docker Compose.
- Elimina también los volúmenes.
- Borra la base de datos local y obliga a restaurar los datos iniciales en el siguiente arranque.

## Resolución de problemas

### Docker Desktop no está abierto

Si Docker no responde, abre Docker Desktop y espera a que el motor esté disponible. Después ejecuta de nuevo:

```powershell
docker compose ps
```

### Un puerto está ocupado

El proyecto FIOS usa los puertos `1234` y `8080`. Si Docker informa de que uno de ellos está ocupado, cierra el proceso que lo esté usando o cambia el mapeo de puertos en `compose.yml`.

### El backend todavía está arrancando

Durante unos segundos, el frontend puede responder antes de que el backend termine de iniciar. Si ves un error `502 Bad Gateway` al acceder a `/api`, espera unos instantes y revisa:

```powershell
docker compose logs -f server
```

### PostgreSQL no aparece como `healthy`

Revisa los registros del contenedor de la base de datos:

```powershell
docker compose logs -f database
```

En el primer arranque puede tardar unos segundos porque se importa el archivo SQL.

### La aplicación no carga

Comprueba que el contenedor `client` está en ejecución:

```powershell
docker compose ps
docker compose logs -f client
```

Después abre `http://localhost:1234`.

### El inicio de sesión falla

Comprueba que el backend responde y que la base de datos se ha restaurado:

```powershell
docker compose logs -f server
docker compose logs -f database
```

Las cuentas disponibles están en [Usuarios de prueba](USUARIOS_PRUEBA.md).

## Comprobaciones rápidas

Estas peticiones permiten confirmar que el frontend, el backend y el proxy están disponibles:

```powershell
Invoke-WebRequest http://localhost:1234
Invoke-WebRequest http://localhost:8080/api/musical-spaces
Invoke-WebRequest http://localhost:1234/api/musical-spaces
```

Inicio de sesión con una cuenta de prueba:

```powershell
$body = '{"email":"admin@fios.com","password":"Fios2026!"}'
Invoke-WebRequest `
  -Uri http://localhost:8080/api/account/login `
  -Method POST `
  -ContentType "application/json" `
  -Body $body
```

El equivalente en Bash es:

```bash
curl http://localhost:1234
curl http://localhost:8080/api/musical-spaces
curl http://localhost:1234/api/musical-spaces

curl -X POST http://localhost:8080/api/account/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@fios.com","password":"Fios2026!"}'
```
