# Backend del proyecto FIOS

[Volver al README principal](../README.md)

Este directorio contiene el backend del proyecto FIOS. Está desarrollado con Java 17, Spring Boot, Spring Security, JPA/Hibernate y Maven.

Para ejecutar la versión completa de entrega con PostgreSQL, backend y frontend, usa Docker Compose desde la carpeta raíz del proyecto. Las instrucciones principales están en el [README principal](../README.md) y en la [guía de ejecución](../docs/EJECUCION.md).

## Ejecución local del backend

La forma recomendada para probar la entrega es Docker Compose. Aun así, el backend puede ejecutarse de forma local si tienes Java 17 y una base de datos PostgreSQL configurada.

En Windows PowerShell, desde este directorio:

```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

Para usar el perfil `prod`, define antes las variables necesarias de base de datos, CORS y JWT.

## Variables relevantes

El backend utiliza, entre otras, estas variables:

```text
SPRING_PROFILES_ACTIVE
DB_HOST
DB_PORT
DB_NAME
DB_USERNAME
DB_PASSWORD
CLIENT_HOST
JWT_SECRET_KEY
HIBERNATE_DDL_AUTO
DATABASE_LOADER_ENABLED
IMAGES_PATH
TICKETMASTER_ENABLED
TICKETMASTER_API_KEY
GEMINI_ENABLED
GEMINI_API_KEY
```

La explicación completa está en [Configuración](../docs/CONFIGURACION.md).

## Pruebas

En Windows PowerShell:

```powershell
.\mvnw.cmd test
```

En Linux o macOS:

```bash
./mvnw test
```

## Notas

- En la entrega, los datos iniciales se restauran desde `database/fios_database.sql`.
- `DATABASE_LOADER_ENABLED` debe permanecer desactivado en la ejecución con Docker Compose.
- Las rutas públicas de eventos están en `/api/events`.
- Las rutas de administración de eventos están en `/api/admin/events`.
- Los scripts de `src/main/resources/db/manual` son migraciones manuales del proyecto, pero no son necesarias para arrancar la copia de entrega ya preparada.
