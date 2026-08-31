# Frontend del proyecto FIOS

[Volver al README principal](../README.md)

Este directorio contiene el frontend del proyecto FIOS. Está desarrollado con Vue 3, Vite, Bootstrap, Leaflet y npm.

Para ejecutar la versión completa de entrega, usa Docker Compose desde la carpeta raíz del proyecto. El frontend se sirve con Nginx en `http://localhost:1234` y redirige las peticiones de `/api` al backend.

## Instalación local

Si quieres trabajar solo con el frontend fuera de Docker, instala las dependencias:

```bash
npm ci
```

## Variable de API

El frontend usa `VITE_BACKEND_URL` para saber dónde está la API.

En Docker Compose, el valor correcto es:

```text
VITE_BACKEND_URL=/api
```

Con ese valor, Nginx actúa como proxy y envía las peticiones al backend.

## Servidor de desarrollo

```bash
npm run dev
```

El servidor de desarrollo de Vite usa el puerto `1234`, según la configuración del proyecto.

## Lint y build

```bash
npm run lint
npm run build
```

## Scripts disponibles

Los scripts principales definidos en `package.json` son:

- `npm run dev`: arranca Vite en modo desarrollo.
- `npm run lint`: revisa el código con ESLint.
- `npm run build`: genera la versión de producción.
- `npm test`: ejecuta las pruebas JavaScript configuradas.

## Documentación relacionada

- [README principal](../README.md)
- [Guía de ejecución](../docs/EJECUCION.md)
- [Configuración](../docs/CONFIGURACION.md)
