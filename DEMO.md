# Demo FIOS

## 0. Antes de empezar

Desde PowerShell, en la carpeta raiz:

```powershell
# Abre PowerShell en la carpeta raiz del proyecto
if (!(Test-Path .env)) { Copy-Item .env.example .env }
docker compose up --build -d
docker compose ps
```

Resultado esperado:

- `database` en estado `healthy`.
- `server` en ejecucion.
- `client` en ejecucion.
- Frontend abierto en `http://localhost:1234`.

Si la demo ya fue usada y quieres volver al estado inicial:

```powershell
docker compose down -v
docker compose up --build -d
```

## 1. Usuarios que vas a usar

La mayoria de contrasenas son `Fios2026!`. La cuenta de demo completa usa `12345678a`.

| Usuario | Correo | Para que se usa |
| --- | --- | --- |
| Admin | `admin@fios.com` | Panel de administracion, aprobacion de espacios, eventos, reservas, usuarios y reseñas |
| Lucas | `lucas@fios.com` | Demo completa con perfil, instrumento, busquedas, reservas propias, mensajes, espacio gestionado `Boa`, banda `The Rapants`, reclutamiento, evento, entrada, favoritos y resenas |
| Irene | `irene.valverde@fios.com` | Musica: perfil, favoritos, reserva, mensajes, bandas, busquedas de miembros, entradas y reseñas |
| Nico | `nico.falcon@fios.com` | Propietario de `Sala Porto Alto`: espacios gestionados, reservas recibidas, horarios, equipamiento y mensajes |
| Lucia | `lucia.castro@fios.com` | Usuario alternativo si quieres enseñar entradas o reseñas ya cargadas |

Consejo: cierra sesion entre usuarios o usa una ventana de incognito para el visitante.

## 2. Flujo de datos que tienes que explicar

```text
Visitante
  busca espacios, eventos y bandas publicas
  ve mapas, filtros, reseñas y disponibilidad

Irene
  inicia sesion
  guarda Sala Porto Alto como favorito
  crea una reserva PENDING para 04/09/2026 de 17:00 a 19:00
  envia un mensaje asociado a esa reserva

Nico
  ve la reserva recibida porque gestiona Sala Porto Alto
  lee el mensaje, responde y acepta la reserva

Irene
  ve la reserva ACCEPTED
  ve la respuesta de Nico
  comprueba que la franja aceptada queda ocupada

Admin
  ve el sistema completo
  aprueba/modera espacios
  revisa reservas, usuarios, reseñas, eventos y busquedas de miembros
```

Entidades que se crean o cambian en directo:

- `favorite_space`: Irene guarda un espacio.
- `reservation_session`: Irene crea una reserva y Nico cambia `PENDING` a `ACCEPTED`.
- `message`: Irene y Nico escriben en la conversacion de la reserva.
- `event_purchase`: Irene compra una entrada.
- Opcional: `musical_space` nuevo en `PENDING`, aprobado despues por Admin.
- Opcional: `band_recruitment` nuevo para una banda de Irene.

## 3. Demo principal paso a paso

### A. Visitante sin iniciar sesion

1. Abre `http://localhost:1234`.
2. Cambia el idioma en la barra superior: español, ingles o gallego.
3. Entra en `Espacios`.
4. Filtra por ciudad `A Coruña` y busca `Sala Porto Alto`.
5. Abre el detalle de `Sala Porto Alto`.
6. Enseña imagen, tipo de espacio, aforo, precio, insonorizacion, equipamiento, reseñas y mapa.
7. En disponibilidad, selecciona `04/09/2026`.

Que demuestras: rutas publicas, internacionalizacion, listado de espacios, filtros, detalle, imagenes, equipamiento, reseñas, mapa Leaflet y disponibilidad calculada.

### B. Busqueda inteligente

1. Entra en `Busqueda inteligente` o abre `/search`.
2. Escribe:

```text
Busco local de ensayo en A Coruna para 3 personas el 04/09/2026 de 17 a 19 hasta 60 euros
```

3. Enseña los chips detectados: ciudad, fecha, horario, personas y presupuesto.
4. Cambia a vista de mapa.
5. Abre `Sala Porto Alto` desde el resultado.

Que demuestras: busqueda por lenguaje natural, Gemini cuando esta activo, fallback local, resultados cruzados, filtros detectados y mapa. No dependas de Gemini para la defensa; la app funciona sin esa clave.

### C. Irene crea datos reales

1. Inicia sesion con `irene.valverde@fios.com`.
2. Abre `Mi perfil`.
3. Enseña datos personales, imagen de usuario e instrumentos `Guitarra electrica` y `Voz`.
4. Abre `Instrumentos` si quieres enseñar el catalogo publico.
5. Vuelve a `Sala Porto Alto`.
6. Pulsa `Añadir a favoritos`.
7. Abre `Favoritos` y comprueba que aparece `Sala Porto Alto`.
8. Vuelve al detalle de `Sala Porto Alto`.
9. Reserva con estos datos:

| Campo | Valor |
| --- | --- |
| Fecha | `04/09/2026` |
| Hora inicio | `17:00` |
| Duracion | `2 horas` |
| Personas | `3` |
| Observaciones | `Ensayo de demo con bateria hibrida y dos sintes. Necesitamos monitores.` |

10. Abre `Reservas`.
11. Comprueba que la reserva aparece como `Pendiente`.
12. Abre `Mensajes`.
13. Envia:

```text
Hola, iremos con bateria hibrida y dos sintes. Podeis confirmar monitores?
```

Que demuestras: login JWT, perfil, instrumentos, favoritos, creacion de reserva, precio calculado, persistencia y mensajeria ligada a una reserva.

### D. Nico gestiona el espacio y acepta

1. Cierra sesion de Irene.
2. Inicia sesion con `nico.falcon@fios.com`.
3. Abre `Mis espacios`.
4. Enseña `Sala Porto Alto`.
5. Abre editar espacio sin guardar cambios destructivos.
6. Enseña datos del espacio: direccion, coordenadas `43.3623, -8.4071`, capacidad, tipo, imagen y descripcion.
7. Enseña horarios del espacio:
   - Lunes a viernes de `17:00` a `23:00`.
   - Sabado de `11:00` a `15:00`.
8. Enseña equipamiento, cantidades, estado y observaciones.
9. Si se ve en la pantalla, enseña excepciones de disponibilidad. Si no, dilo: Sala Porto Alto tiene un bloqueo el `04/09/2026` de `20:00` a `23:00`.
10. Abre `Reservas` y cambia a reservas recibidas/gestionadas.
11. Busca la reserva de Irene en `Sala Porto Alto`.
12. Abre la conversacion o `Mensajes`.
13. Responde:

```text
Si, dejo monitores preparados.
```

14. Vuelve a la reserva y pulsa `Aceptar`.

Que demuestras: permisos de propietario, gestion de espacios, coordenadas, horarios, excepciones, equipamiento, reservas recibidas, mensajes entre participantes y cambio de estado `PENDING` -> `ACCEPTED`.

### E. Irene comprueba el cambio

1. Cierra sesion de Nico.
2. Entra otra vez con Irene.
3. Abre `Reservas`.
4. Comprueba que la reserva esta `Aceptada`.
5. Abre `Mensajes`.
6. Comprueba la respuesta de Nico.
7. Vuelve al detalle de `Sala Porto Alto`, fecha `04/09/2026`.
8. Comprueba que la franja `17:00-19:00` ya no queda libre o aparece como ocupada.
9. Si quieres enseñar validacion de solapamiento, intenta reservar otra vez esa misma franja y muestra el mensaje de no disponibilidad.

Que demuestras: sincronizacion entre usuarios, persistencia en PostgreSQL, mensajes no leidos y prevencion de solapamientos.

### F. Bandas y busqueda de musicos

1. Con Irene, abre `Bandas`.
2. Entra en `Ria Electrica`.
3. Enseña miembros, roles y que Irene es lider.
4. Abre `Mis bandas` para enseñar acciones de gestion.
5. Abre `Bandas buscan miembros`.
6. Enseña oportunidades abiertas y cerradas.
7. Crea una busqueda nueva solo si quieres demostrar escritura:

| Campo | Valor |
| --- | --- |
| Banda | `Ria Electrica` |
| Instrumento | `Bajo electrico` |
| Titulo | `Bajista para demo FIOS` |
| Rol buscado | `Bajista` |
| Nivel | `Intermedio` |
| Ciudad | `Vigo` |
| Vacantes | `1` |
| Descripcion | `Prueba creada durante la demo para demostrar el flujo de busqueda de musicos.` |

8. Comprueba que aparece como abierta.
9. Si quieres enseñar estados, cierrala despues.

Que demuestras: bandas publicas, detalle de banda, miembros, lideres, permisos, creacion y cierre de oportunidades.

### G. Eventos y entradas

Primero enseña que un usuario puede crear eventos:

1. Entra como `irene.valverde@fios.com` o como otro usuario que lidere una banda activa.
2. Abre `Eventos`.
3. Pulsa `Proponer evento`.
4. Rellena un evento de prueba:

| Campo | Valor |
| --- | --- |
| Banda | `Ria Electrica` |
| Titulo | `Showcase usuario FIOS defensa` |
| Tipo | `Showcase` |
| Fecha | `19/12/2026` |
| Hora inicio | `18:00` |
| Hora fin | `20:00` |
| Sala | `Mercado Cultural FIOS` |
| Ciudad | `Vigo` |
| Provincia | `Pontevedra` |
| Pais | `Espana` |
| Genero | `Synth Pop` |
| Precio | `0` |
| Descripcion | `Evento creado por un usuario durante la demo para comprobar la publicacion desde FIOS.` |

5. Guarda el evento y comprueba el aviso de que queda pendiente de aprobacion.
6. Cierra sesion y entra como `admin@fios.com`.
7. Abre `Administracion` > `Eventos`.
8. Filtra por estado `Pendiente` o busca `Showcase usuario FIOS defensa`.
9. Pulsa `Publicar`.
10. Vuelve a entrar como Irene, abre `Eventos` y comprueba que el evento ya aparece publicado.

Despues enseña el catalogo y entradas:

1. Abre `Eventos`.
2. Filtra o busca `Latido Verde Sesion FIOS`.
3. Abre el detalle.
4. Enseña que es un evento propio de FIOS del `10/10/2026`.
5. Enseña fecha, hora, ubicacion, mapa, precio, capacidad, banda y espacio asociado.
6. Si quieres enseñar mas eventos propios de FIOS a partir de octubre, busca:
   - `Noite FIOS: Bruma Norte`
   - `Clinic FIOS de grabacion casera`
   - `Jam Aberta FIOS Santiago`
   - `Ria Electrica Live FIOS`
   - `Festival FIOS Invierno`
7. Pulsa comprar entrada con Irene.
8. Abre `Mis entradas` o `/tickets` y comprueba que aparece la entrada.
9. Vuelve a `Eventos` y busca un evento externo, por ejemplo `Placebo` o `Hombres G`.
10. Enseña que los eventos externos ya estan cargados y enlazan con Ticketmaster.

Que demuestras: creacion de eventos por usuarios lideres de banda, aprobacion administrativa antes de publicarse, eventos propios de FIOS desde octubre de 2026, eventos externos, detalle, mapa, compra de entradas, persistencia de compras e integracion Ticketmaster sin depender de la API en directo.

### H. Reseñas

1. Con Irene, abre `Reseñas`.
2. Enseña pestañas `Pendientes`, `Mis reseñas` y `Recibidas`.
3. Si aparece una reseña pendiente de una reserva completada, abre el formulario.
4. Enseña que hay valoraciones 1-5 y comentario.
5. No publiques una reseña nueva si no quieres cambiar medias; basta con enseñar el formulario y las reseñas ya cargadas.

Que demuestras: reseñas de espacios, reseñas de usuarios, validaciones y relacion con reservas completadas.

### I. Administrador

1. Cierra sesion.
2. Entra con `admin@fios.com`.
3. Abre `Administracion` o `/admin`.
4. Enseña el resumen con metricas.
5. Recorre estas secciones:

| Seccion | Que enseñar |
| --- | --- |
| Usuarios | Roles, usuarios activos y acciones de activar/desactivar o cambiar rol |
| Espacios | Estados `PENDING`, `APPROVED`, `REJECTED`; botones `Aprobar` y `Rechazar` |
| Reservas | Estados, fecha, usuario, espacio, precio y cancelacion administrativa |
| Reseñas | Moderacion y borrado |
| Eventos | Eventos internos, externos, borradores, creacion/edicion y archivo |
| Busquedas miembros | Ofertas abiertas/cerradas y moderacion |

6. No borres reseñas ni usuarios en directo salvo que el profesor lo pida.

Que demuestras: rol `ADMIN`, rutas protegidas, panel administrativo y moderacion global.

## 4. Flujo opcional muy visual: espacio pendiente y aprobacion

Hazlo si quieres demostrar de forma clara que un usuario normal no publica directamente.

### Crear espacio como Nico

1. Entra como `nico.falcon@fios.com`.
2. Abre `Mis espacios`.
3. Pulsa crear espacio.
4. Usa estos datos:

| Campo | Valor |
| --- | --- |
| Nombre | `Espacio Demo Profesor` |
| Descripcion | `Espacio creado durante la demo para comprobar aprobacion administrativa.` |
| Tipo | `Sala de ensayo` |
| Capacidad | `4` |
| Metros cuadrados | `25` |
| Insonorizado | `Si` |
| Pais | `España` |
| Provincia | `A Coruña` |
| Ciudad | `A Coruña` |
| Calle | `Rua Demo` |
| Portal | `1` |
| Codigo postal | `15001` |
| Coordenadas | `43.3623`, `-8.4071` |

5. Guarda.
6. Comprueba que nace pendiente o que no aparece en el listado publico.

### Comprobar como visitante

1. Cierra sesion.
2. Entra en `Espacios`.
3. Busca `Espacio Demo Profesor`.
4. Debe no aparecer mientras este pendiente.

### Aprobar como Admin

1. Entra como `admin@fios.com`.
2. Abre `Administracion` > `Espacios`.
3. Busca `Espacio Demo Profesor`.
4. Pulsa `Aprobar`.
5. Cierra sesion o abre vista publica.
6. Busca de nuevo `Espacio Demo Profesor`.
7. Ahora debe aparecer.

Que demuestras: alta de espacio por propietario, estado `PENDING`, filtro publico, aprobacion administrativa y publicacion.

## 5. Pruebas rapidas de seguridad y validacion

Haz 2 o 3, no todas.

| Prueba | Paso | Resultado esperado |
| --- | --- | --- |
| Login incorrecto | Usa `admin@fios.com` con contraseña `mal` | Mensaje de credenciales incorrectas |
| Ruta protegida | Sin sesion abre `/profile` | Redireccion a login o acceso bloqueado |
| Admin bloqueado | Entra como Irene y abre `/admin` | Acceso restringido |
| Registro invalido | En `Registrarse`, pon contraseña `12345678` | Validacion de contraseña |
| Reserva fuera de horario | En Sala Porto Alto intenta reservar fuera de `17:00-23:00` | No permite reservar |
| Mensaje vacio | Intenta enviar mensaje vacio | Boton bloqueado o validacion |
| Reseña incompleta | Abre formulario de reseña sin puntuaciones | Validacion 1-5 |

## 6. Cierre que tienes que decir

Resume el recorrido asi:

```text
Hemos visto el ciclo completo:
un visitante descubre espacios, eventos y bandas;
un musico inicia sesion, guarda favorito, reserva y escribe;
el propietario recibe la solicitud, responde y acepta;
la disponibilidad se actualiza para evitar solapamientos;
el usuario puede comprar entradas, gestionar bandas y revisar reseñas;
y el administrador supervisa espacios, reservas, usuarios, eventos, reseñas y busquedas.
```

## 7. Si algo falla

| Problema | Que hacer |
| --- | --- |
| No carga la web | `docker compose ps` y `docker compose logs --tail=100 client` |
| Backend no responde | `docker compose logs --tail=100 server` |
| Base de datos no esta healthy | `docker compose logs --tail=100 database` |
| Login falla | Comprueba `Fios2026!` con mayuscula inicial |
| La reserva de `04/09/2026 17:00-19:00` ya esta ocupada | Usa `19:00-20:00` o restaura con `docker compose down -v` |
| Ticketmaster falla | Enseña eventos externos ya cargados; no dependas de importar en directo |
| Gemini no esta activo | Explica que hay parser local y sigue con la busqueda inteligente |

## 8. Checklist final

- [ ] Docker Desktop abierto.
- [ ] `docker compose ps` correcto.
- [ ] `http://localhost:1234` carga.
- [ ] Usuarios probados: Admin, Irene y Nico.
- [ ] `DEMO.md` abierto.
- [ ] Flujo principal ensayado una vez.
- [ ] No dependes de claves externas.
