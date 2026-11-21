# Informe – Evaluación Continua T1 – Pruebas de Software (2424)

**Alumno:** Luis Angel Sanchez Cueva · **Código:** i202506013 · **D = 3**

Fila de datos que me corresponde (P1): alumno **i202610002** (Luis Ramos, promedio 16.0) · curso **PI5620** · método de pago **sin cuerpo en la petición** · acción que debe fallar: **pagar otra vez la misma matrícula**.

Cómo ejecutar: reiniciar la API (BD limpia) y luego `./mvnw verify` → reporte en `target/site/serenity/index.html`.

---

## Pregunta 1 – Matrícula (matricula.feature / MatriculaStepDefinitions.java)

### Tabla de cálculo (Escenario 1: cotizar PI5620 para Luis)

| Dato | Cálculo | Valor |
|---|---|---|
| Créditos de PI5620 | dato del curso | 6 |
| Subtotal | 6 créditos × S/ 120 | **720,0** |
| Descuento | promedio 16.0 → tramo ≥ 15 y < 17 | **10 %** |
| Total | 720 − 10 % (72) | **648,0** |

Otras validaciones que pasa antes de matricular: carrera CI = CI, sin deuda, sin duplicado, aprueba prerrequisito AS5402 (nota 15), hay vacantes (20 − 1 ocupada), sin cruce (PI5620 viernes 08–14 vs PS5511 lunes) y créditos 3 + 6 = 9 ≤ 24.

### Escenario 2: matricularse y pagar
- `POST /api/matriculas` → 201, estado `REGISTRADA`, monto = total cotizado = 648,0, cumple `schemas/matricula.json`. El id se toma de la respuesta y se guarda en la memoria del actor (no hay ids fijos).
- `PATCH /api/matriculas/{id}/pagar` **sin cuerpo** → 200, estado `PAGADA`, `fechaPago` con valor.
- `GET /api/pagos` → existe un solo pago con el mismo `idMatricula`, método **EFECTIVO** y `numeroOperacion` con patrón `^OP-[0-9]{6}$`.

**¿Qué método registra la API cuando no hay cuerpo?** `MatriculaController.pagar` declara `@RequestBody(required = false)`; en `MatriculaService.pagar`, si `req == null` se crea el pago con `MetodoPago.EFECTIVO` (y `PagoRequest.metodoOEfectivo()` también devuelve EFECTIVO si el campo es nulo).

### Escenario 3: regla de negocio que debe fallar
Pagar otra vez la misma matrícula → **HTTP 409**, `codigo` = **`MATRICULA_YA_PAGADA`**, cumple `schemas/error.json`.

**Regla que lo produce y por qué no lo detiene antes otra validación.** En `MatriculaService.pagar` las validaciones van en este orden: (1) la matrícula existe (si no, 404 `MATRICULA_NO_ENCONTRADA`); (2) `permitirAlumno`: el alumno es el dueño (Luis paga lo suyo, así que pasa; si no sería 403); (3) estado `ANULADA` → 409 `MATRICULA_ANULADA`; (4) estado `PAGADA` → 409 `MATRICULA_YA_PAGADA`. La matrícula existe, es de Luis y su estado es `PAGADA` (no `ANULADA`), por eso las tres primeras pasan y la que dispara es la cuarta. La petición sin cuerpo tampoco falla antes porque el cuerpo es opcional.

### Capturas
_(pegar aquí: consola con nombre/código/fecha del Hook + resultado de matricula.feature)_

---

## Pregunta 2 – Seguridad con Screenplay (seguridad.feature)

Actor: Luis (**i202610002**, fila D = 3). Los tokens se guardan con `actor.remember(...)` / `actor.recall(...)` (ninguna variable `static`).

| Escenario | Resultado esperado |
|---|---|
| 1. Rotación del refresh token | 1.er refresh → 200 con tokens nuevos; reutilizar el refresh original → **401 `REFRESH_REUTILIZADO`**; luego perfil con el accessToken nuevo → **401 `TOKEN_INVALIDO`** |
| 2. Cierre de sesión | logout → **204**; perfil con el mismo accessToken → **401 `TOKEN_INVALIDO`** |
| 3. Bloqueo (i202610004) | intentos 1–4 mal → **401 `CREDENCIALES_INVALIDAS`**; intento 5 mal → **423 `CUENTA_BLOQUEADA`**; clave correcta → **423 `CUENTA_BLOQUEADA`** |
| 4. Alumno lista alumnos | **403 `ACCESO_DENEGADO`** |

**Por qué el accessToken nuevo también es rechazado (Esc. 1).** El refresh token es de un solo uso: al usarlo se revoca su sesión. Si alguien lo reutiliza, `AuthService.refresh` lo interpreta como posible robo y ejecuta `sesiones.revocarTodas(usuario)`, cerrando todas las sesiones del usuario, incluida la que se creó con el primer refresh. El JWT lleva el claim `sid`; en cada petición `TokenConfig` valida que esa sesión siga activa (`existsByIdAndRevocadaFalseAndExpiraAfter`), y como fue revocada el token se rechaza aunque no haya vencido (15 min).

**Por qué con la clave correcta tampoco puede ingresar (Esc. 3).** En `AuthService.login`, tras 5 fallos `Usuario.registrarFallo` fija `bloqueadoHasta = ahora + 15 min`. En los siguientes logins la comprobación `u.bloqueado()` se hace **antes** de verificar la contraseña, así que responde 423 aunque la clave sea correcta, hasta que pase el tiempo (o un ADMIN la desbloquee / se reinicie la API).

### Capturas
_(pegar aquí)_

---

## Pregunta 3 – Calificaciones e historial (calificaciones.feature)

Docente D0001, alumna Rosa Medina (i202610005), curso PI5620 (6 créditos). Notas previas de Rosa en el historial (2026-1): LP5401 = 15 (4 créditos), AS5402 = 16 (3 créditos). Nota aprobatoria = 13.

Nota 1 = 10 + D = **13**; nota 2 = **12,5**.

| Fila | Nota PI5620 | Condición | Créditos aprobados | Promedio ponderado |
|---|---|---|---|---|
| 1 | 13 | **APROBADO** (13 ≥ 13) | 4 + 3 + 6 = **13** | (15·4 + 16·3 + 13·6) / 13 = 186 / 13 = 14,3077 → **14,31** |
| 2 | 12,5 | **DESAPROBADO** (12,5 < 13) | 4 + 3 = **7** | (15·4 + 16·3 + 12,5·6) / 13 = 183 / 13 = 14,0769 → **14,08** |

Escenarios adicionales (códigos descubiertos en el código de la API):
- Docente que no dicta el curso (D0002 sobre PI5620) → **403 `ACCESO_DENEGADO`** (`calificar` exige ser ADMIN o el docente del curso).
- Matrícula no pagada (PS5511 de Rosa, D0001) → **409 `MATRICULA_NO_PAGADA`** (se valida el permiso primero, y D0001 sí dicta PS5511; luego falla porque el estado no es PAGADA).

El id de la matrícula se obtiene con `GET /api/matriculas?curso=...` usando el token de la alumna (no está escrito en el código).

### Prueba con 12.5 (punto en lugar de coma)
Ejecutar **una vez** la fila de 12,5 escrita como `12.5` en la tabla `Ejemplos`, capturar y completar aquí lo observado.

Causa esperada: el feature está en español (`# language: es`) y Cucumber interpreta `{double}` con la configuración regional **es**, donde el separador decimal es la **coma** y el punto es el separador de miles. Por eso `12,5` se lee como 12.5, pero `12.5` no se lee como 12.5 (se toma como “doce mil quinientos”, es decir 125, o el paso no coincide con la definición). Si llega a la API como 125, `NotaRequest` (`@DecimalMax("20.0")`) la rechaza con 400 `DATOS_INVALIDOS`. **Confirmar con la captura real y ajustar este párrafo a lo que se vea.**

### Capturas
_(pegar aquí: consola + reporte `target/site/serenity/index.html`)_
