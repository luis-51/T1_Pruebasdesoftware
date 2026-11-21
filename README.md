# matricula-api

API REST de matrícula para el curso Pruebas de Software (CIBERTEC). Se usa como sistema bajo prueba en los ejercicios de BDD con Gherkin, Cucumber, Serenity y Rest Assured.

Maneja carreras, docentes, cursos con prerrequisitos, alumnos, periodos académicos, matrículas, pagos, notas e historial. Todo protegido con usuarios y roles.

Stack: Java 21, Spring Boot 4.1, Spring Security (resource server con JWT), Spring Data JPA, MySQL, springdoc-openapi.

## Cómo ejecutarla

1. Copia `.env.example` como `.env` y completa los datos de tu MySQL:

   ```properties
   DB_URL=jdbc:mysql://localhost:3306/matricula_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Lima
   DB_USER=root
   DB_PASSWORD=tu_clave
   ```

2. Arranca la aplicación desde IntelliJ con Java 21 o con `./mvnw spring-boot:run` (en Windows, `mvnw.cmd spring-boot:run`). El `.env` se lee desde la carpeta del proyecto.

La base `matricula_db` se crea sola. En cada arranque se borran y recrean las tablas y se cargan los datos de `src/main/resources/data.sql`, así que siempre se parte del mismo estado. Si una prueba deja datos que estorban, reinicia la API.

Con la API arriba:

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI en JSON: http://localhost:8080/v3/api-docs
- Estado: http://localhost:8080/actuator/health

En Swagger, ejecuta primero `POST /api/auth/login`, copia el `accessToken` y pégalo en el botón Authorize.

## Seguridad

El login devuelve dos tokens:

```json
{
  "accessToken": "eyJraWQiOi...",
  "refreshToken": "q8V3m...",
  "tipo": "Bearer",
  "expiraEn": 900,
  "username": "i202610001",
  "rol": "ALUMNO"
}
```

El `accessToken` va en cada petición con el header `Authorization: Bearer <token>`. Así funciona:

| Tema | Cómo está hecho |
|---|---|
| Access token | JWT firmado con RSA (RS256), dura 15 minutos. La llave privada se genera al arrancar y nunca sale del servidor, por eso al reiniciar la API los tokens anteriores dejan de valer. |
| Sesión en base de datos | Cada token pertenece a una sesión guardada en la tabla `sesion`. En cada petición se revisa que la sesión siga activa, así que un logout o un cambio de clave invalida el token al instante. |
| Refresh token | Es un valor aleatorio de 256 bits. En la base solo se guarda su hash SHA-256. Dura 8 horas y se puede usar una sola vez: `POST /api/auth/refresh` entrega un par nuevo y anula el anterior. Si alguien reutiliza un refresh token ya usado, se cierran todas las sesiones de ese usuario. |
| Contraseñas | Se guardan con BCrypt. Las nuevas deben tener de 8 a 64 caracteres, con mayúscula, minúscula y número. |
| Bloqueo | Al quinto intento fallido la cuenta queda bloqueada 15 minutos (HTTP 423). Un ADMIN la puede desbloquear antes. |
| Cuentas nuevas | Al registrar un alumno o docente se crea su usuario con una clave temporal aleatoria, que se muestra una sola vez en la respuesta del POST. |

Los tiempos se pueden cambiar en `application.properties` (`seguridad.access-segundos`, `seguridad.refresh-horas`, `seguridad.max-intentos`, `seguridad.bloqueo-minutos`).

Roles:

| Rol | Puede |
|---|---|
| ADMIN | Todo: mantenimiento de carreras, docentes, cursos, alumnos y periodos; matricular a cualquier alumno; ver todos los pagos; desbloquear cuentas. |
| ALUMNO | Ver sus datos, constancia e historial; cotizar, matricularse, pagar y anular solo lo suyo; ver solo sus pagos. |
| DOCENTE | Ver sus cursos y los alumnos de esos cursos; registrar notas solo en sus cursos. |

Sin login se puede consultar el catálogo: cursos, carreras y periodos.

## Endpoints

Autenticación y usuarios

| Método | Ruta | Acceso | Respuestas |
|---|---|---|---|
| POST | `/api/auth/login` | público | 200, 400, 401, 423 |
| POST | `/api/auth/refresh` | público | 200, 400, 401, 423 |
| POST | `/api/auth/logout` | con sesión | 204, 401 |
| GET | `/api/auth/perfil` | con sesión | 200, 401 |
| PUT | `/api/auth/password` | con sesión | 204, 400, 401, 422 |
| GET | `/api/usuarios` | ADMIN | 200, 401, 403 |
| PATCH | `/api/usuarios/{username}/desbloquear` | ADMIN | 200, 401, 403, 404 |

Carreras y periodos

| Método | Ruta | Acceso | Respuestas |
|---|---|---|---|
| GET | `/api/carreras` | público | 200 |
| GET | `/api/carreras/{codigo}` | público | 200, 404 |
| POST | `/api/carreras` | ADMIN | 201, 400, 409 |
| PUT | `/api/carreras/{codigo}` | ADMIN | 200, 400, 404 |
| DELETE | `/api/carreras/{codigo}` | ADMIN | 204, 404, 409 |
| GET | `/api/periodos` | público | 200 |
| GET | `/api/periodos/actual` | público | 200, 404 |
| GET | `/api/periodos/{codigo}` | público | 200, 404 |
| POST | `/api/periodos` | ADMIN | 201, 400, 409 |
| PATCH | `/api/periodos/{codigo}/abrir` | ADMIN | 200, 404, 409 |
| PATCH | `/api/periodos/{codigo}/cerrar` | ADMIN | 200, 404, 409 |

Docentes

| Método | Ruta | Acceso | Respuestas |
|---|---|---|---|
| GET | `/api/docentes?nombre=` | ADMIN | 200 |
| GET | `/api/docentes/{codigo}` | ADMIN o el mismo docente | 200, 403, 404 |
| GET | `/api/docentes/{codigo}/cursos` | ADMIN o el mismo docente | 200, 403, 404 |
| POST | `/api/docentes` | ADMIN | 201, 400, 409 |
| PUT | `/api/docentes/{codigo}` | ADMIN | 200, 400, 404, 409 |
| DELETE | `/api/docentes/{codigo}` | ADMIN | 204, 404, 409 |

Cursos

| Método | Ruta | Acceso | Respuestas |
|---|---|---|---|
| GET | `/api/cursos?ciclo=&modalidad=&dia=&carrera=&nombre=` | público | 200, 400 |
| GET | `/api/cursos/{codigo}` | público | 200, 404 |
| GET | `/api/cursos/{codigo}/alumnos` | ADMIN o docente del curso | 200, 403, 404 |
| POST | `/api/cursos` | ADMIN | 201, 400, 404, 409, 422 |
| PUT | `/api/cursos/{codigo}` | ADMIN | 200, 400, 404, 422 |
| DELETE | `/api/cursos/{codigo}` | ADMIN | 204, 404, 409 |

Alumnos

| Método | Ruta | Acceso | Respuestas |
|---|---|---|---|
| GET | `/api/alumnos?nombre=&deuda=&carrera=` | ADMIN | 200 |
| GET | `/api/alumnos/{codigo}` | ADMIN o el mismo alumno | 200, 403, 404 |
| GET | `/api/alumnos/{codigo}/constancia` | ADMIN o el mismo alumno | 200, 403, 404, 422 |
| GET | `/api/alumnos/{codigo}/historial` | ADMIN o el mismo alumno | 200, 403, 404 |
| POST | `/api/alumnos` | ADMIN | 201, 400, 404, 409 |
| PUT | `/api/alumnos/{codigo}` | ADMIN | 200, 400, 404, 409 |
| DELETE | `/api/alumnos/{codigo}` | ADMIN | 204, 404, 409 |

Matrículas y pagos

| Método | Ruta | Acceso | Respuestas |
|---|---|---|---|
| POST | `/api/matriculas/cotizacion` | ADMIN o el alumno | 200, 400, 403, 404, 422 |
| POST | `/api/matriculas` | ADMIN o el alumno | 201, 400, 403, 404, 409, 422 |
| GET | `/api/matriculas?alumno=&curso=&estado=&periodo=` | ADMIN, ALUMNO | 200, 400 |
| GET | `/api/matriculas/{id}` | ADMIN, el alumno o el docente del curso | 200, 403, 404 |
| PATCH | `/api/matriculas/{id}/pagar` | ADMIN o el alumno | 200, 400, 403, 404, 409 |
| PATCH | `/api/matriculas/{id}/anular` | ADMIN o el alumno | 200, 403, 404, 409 |
| PATCH | `/api/matriculas/{id}/nota` | ADMIN o docente del curso | 200, 400, 403, 404, 409 |
| GET | `/api/pagos?alumno=&metodo=` | ADMIN, ALUMNO | 200, 400 |
| GET | `/api/pagos/{id}` | ADMIN o el alumno | 200, 403, 404 |

Todos los filtros son opcionales. Un alumno que lista matrículas o pagos solo recibe los suyos, aunque mande otro código en el filtro. Valores válidos: `modalidad` PRESENCIAL, VIRTUAL, HIBRIDO; `dia` LUNES a SABADO; `estado` REGISTRADA, PAGADA, ANULADA; `metodo` EFECTIVO, TARJETA, YAPE, TRANSFERENCIA.

Sin token, los endpoints protegidos responden 401. Con un rol que no corresponde, o con datos de otro usuario, responden 403.

## Flujo de una matrícula

1. Login: `POST /api/auth/login` con `{"username": "i202610001", "password": "Cibertec2026"}`.
2. Cotizar: `POST /api/matriculas/cotizacion` con `{"codigoAlumno": "i202610001", "cursos": ["PS5511", "TS5514"]}`. Calcula créditos, subtotal, descuento y total sin guardar nada.
3. Matricular: `POST /api/matriculas` con `{"codigoAlumno": "i202610001", "codigoCurso": "PS5511"}`. Queda REGISTRADA en el periodo abierto y ocupa una vacante.
4. Pagar: `PATCH /api/matriculas/{id}/pagar` con `{"metodo": "YAPE"}`. El body es opcional; sin body el método es EFECTIVO. Pasa a PAGADA y se genera un pago con número de operación `OP-000014`.
5. Anular: `PATCH /api/matriculas/{id}/anular`. Solo si no está pagada. Libera la vacante.
6. Nota: el docente del curso registra la nota con `PATCH /api/matriculas/{id}/nota` y `{"nota": 16}`. Solo para matrículas pagadas. Se aprueba con 13.
7. Constancia: `GET /api/alumnos/{codigo}/constancia`. Cursos del periodo abierto, créditos, monto total, pagado y pendiente.
8. Historial: `GET /api/alumnos/{codigo}/historial`. Cursos con nota de todos los periodos, créditos aprobados y promedio ponderado por créditos.

Al cerrar un periodo (`PATCH /api/periodos/{codigo}/cerrar`), las matrículas que siguen sin pagar pasan a ANULADA. Solo puede haber un periodo abierto a la vez.

## Reglas

El monto de cada curso es créditos × S/ 120. Con promedio 17 o más se descuenta 20 %; con 15 o más, 10 %. Es la misma lógica de `CalculadoraMatricula` que se trabajó con TDD. El tope es 24 créditos por periodo.

Al matricular, la API revisa en este orden:

| Caso | HTTP | codigo |
|---|---|---|
| Un alumno intenta matricular a otro | 403 | ACCESO_DENEGADO |
| El alumno no existe | 404 | ALUMNO_NO_ENCONTRADO |
| El curso no existe | 404 | CURSO_NO_ENCONTRADO |
| No hay periodo abierto | 422 | SIN_PERIODO_ABIERTO |
| El curso es de otra carrera | 422 | CURSO_DE_OTRA_CARRERA |
| El alumno tiene deuda | 422 | ALUMNO_CON_DEUDA |
| Ya está matriculado en ese curso | 409 | MATRICULA_DUPLICADA |
| Ya aprobó ese curso | 422 | CURSO_APROBADO |
| Le falta aprobar un prerrequisito | 422 | PRERREQUISITO_PENDIENTE |
| El curso no tiene vacantes | 409 | SIN_VACANTES |
| El horario se cruza con otro curso suyo | 409 | CRUCE_HORARIO |
| Pasaría de 24 créditos | 422 | LIMITE_CREDITOS |

La matrícula bloquea la fila del curso mientras revisa las vacantes. Si varias personas piden la última vacante al mismo tiempo, solo una la obtiene.

Otras reglas:

| Caso | HTTP | codigo |
|---|---|---|
| Usuario o contraseña incorrectos | 401 | CREDENCIALES_INVALIDAS |
| Cuenta bloqueada por intentos | 423 | CUENTA_BLOQUEADA |
| Petición sin token | 401 | NO_AUTENTICADO |
| Token alterado, vencido o de una sesión cerrada | 401 | TOKEN_INVALIDO |
| Refresh token desconocido o vencido | 401 | REFRESH_INVALIDO |
| Refresh token ya usado | 401 | REFRESH_REUTILIZADO |
| Rol sin permiso o datos de otro usuario | 403 | ACCESO_DENEGADO |
| Contraseña actual incorrecta | 422 | PASSWORD_INCORRECTA |
| Nueva contraseña igual a la actual | 422 | PASSWORD_REPETIDA |
| Pagar una matrícula ya pagada | 409 | MATRICULA_YA_PAGADA |
| Pagar una matrícula anulada | 409 | MATRICULA_ANULADA |
| Anular una matrícula pagada | 409 | MATRICULA_PAGADA |
| Anular una matrícula ya anulada | 409 | MATRICULA_YA_ANULADA |
| Poner nota a una matrícula no pagada | 409 | MATRICULA_NO_PAGADA |
| Matrícula, pago, carrera, docente, periodo o usuario inexistente | 404 | MATRICULA_NO_ENCONTRADA, PAGO_NO_ENCONTRADO, CARRERA_NO_ENCONTRADA, DOCENTE_NO_ENCONTRADO, PERIODO_NO_ENCONTRADO, USUARIO_NO_ENCONTRADO |
| Consultar el periodo actual sin periodo abierto | 404 | SIN_PERIODO_ABIERTO |
| Código repetido | 409 | ALUMNO_DUPLICADO, CURSO_DUPLICADO, DOCENTE_DUPLICADO, CARRERA_DUPLICADA, PERIODO_DUPLICADO |
| Email usado por otro alumno o docente | 409 | EMAIL_DUPLICADO |
| Hora de inicio igual o posterior a la de fin | 400 | HORARIO_INVALIDO |
| Fecha de inicio igual o posterior a la de fin | 400 | FECHAS_INVALIDAS |
| Requisito inexistente o el mismo curso | 422 | REQUISITO_INVALIDO |
| Bajar las vacantes por debajo de los matriculados | 422 | VACANTES_INSUFICIENTES |
| Eliminar algo que está en uso | 409 | ALUMNO_CON_MATRICULAS, CURSO_CON_MATRICULAS, CURSO_ES_REQUISITO, DOCENTE_CON_CURSOS, CARRERA_EN_USO |
| Abrir un periodo con otro abierto | 409 | PERIODO_ABIERTO_EXISTENTE |
| Abrir uno abierto o cerrar uno cerrado | 409 | PERIODO_YA_ABIERTO, PERIODO_YA_CERRADO |
| Campos obligatorios vacíos o con formato incorrecto | 400 | DATOS_INVALIDOS |

## Formato de los errores

Los errores siguen el formato ProblemDetail (RFC 9457) y agregan el campo `codigo`:

```json
{
  "type": "about:blank",
  "title": "Conflict",
  "status": 409,
  "detail": "El curso IA5513 no tiene vacantes",
  "instance": "/api/matriculas",
  "codigo": "SIN_VACANTES"
}
```

En los errores de validación también viene `errores`, con el mensaje de cada campo. Por ejemplo, al registrar un alumno con código `X1`:

```json
{
  "status": 400,
  "detail": "Datos de entrada inválidos",
  "codigo": "DATOS_INVALIDOS",
  "errores": { "codigo": "debe tener el formato i + 9 dígitos" }
}
```

Los mensajes de las validaciones estándar (`@NotBlank`, `@Email`, `@Min`) cambian de idioma según el header `Accept-Language`: con `es` dicen "no debe estar vacío" y con `en`, "must not be blank".

Las respuestas 401 traen además el header `WWW-Authenticate: Bearer`.

## Datos de prueba

Usuarios

| Usuario | Contraseña | Rol |
|---|---|---|
| admin | Admin2026 | ADMIN |
| D0001 a D0005 | Cibertec2026 | DOCENTE |
| código de cada alumno, por ejemplo i202610001 | Cibertec2026 | ALUMNO |

Carreras: CI Computación e Informática y AE Administración de Empresas.

Periodos: 2026-1 (16/03 al 11/07, cerrado) y 2026-2 (17/08 al 12/12, abierto).

Docentes

| Código | Nombre | Cursos |
|---|---|---|
| D0001 | Marco Salazar | PS5511, QA5517, PI5620 |
| D0002 | Lucía Paredes | DM5512, TS5514, LP5401 |
| D0003 | Raúl Gómez | IA5513, AS5402 |
| D0004 | Elena Castro | SA5516, BD5410 |
| D0005 | Pablo Rojas | ninguno |

Cursos

| Código | Nombre | Carrera | Ciclo | Créditos | Vacantes | Modalidad | Horario | Requisito |
|---|---|---|---|---|---|---|---|---|
| PS5511 | Pruebas de Software | CI | 6 | 3 | 30 | PRESENCIAL | LUNES 08:00-11:00 | AS5402 |
| DM5512 | Desarrollo de Aplicaciones Móviles | CI | 6 | 4 | 25 | HIBRIDO | MARTES 08:00-12:00 | LP5401 |
| IA5513 | Inteligencia Artificial Aplicada | CI | 6 | 3 | 1 | VIRTUAL | MIERCOLES 08:00-11:00 | |
| TS5514 | Taller de Software | CI | 6 | 4 | 30 | HIBRIDO | JUEVES 08:00-12:00 | LP5401 |
| QA5517 | Gestión de Calidad de Software | CI | 6 | 3 | 30 | VIRTUAL | LUNES 09:00-12:00 | AS5402 |
| PI5620 | Proyecto Integrador | CI | 6 | 6 | 20 | PRESENCIAL | VIERNES 08:00-14:00 | AS5402 |
| SA5516 | Seguridad de Aplicaciones | CI | 5 | 4 | 30 | VIRTUAL | LUNES 14:00-18:00 | |
| BD5410 | Base de Datos Avanzado | CI | 5 | 4 | 30 | PRESENCIAL | MARTES 14:00-18:00 | |
| LP5401 | Lenguaje de Programación II | CI | 5 | 4 | 30 | PRESENCIAL | SABADO 08:00-12:00 | |
| AS5402 | Análisis y Diseño de Sistemas | CI | 5 | 3 | 30 | VIRTUAL | SABADO 14:00-17:00 | |
| AD5101 | Contabilidad General | AE | 1 | 3 | 35 | PRESENCIAL | MIERCOLES 14:00-17:00 | |

Alumnos

| Código | Nombre | Carrera | Promedio | Deuda | Notas 2026-1 (LP5401, AS5402) | Matrículas 2026-2 al arrancar |
|---|---|---|---|---|---|---|
| i202610001 | Ana Torres | CI | 18.0 | no | 18, 17 | DM5512 (anulada) |
| i202610002 | Luis Ramos | CI | 16.0 | no | 16, 15 | PS5511 |
| i202610003 | Carla Quispe | CI | 12.0 | no | 13, 14 | IA5513, que ocupa la única vacante |
| i202610004 | Jorge Huamán | CI | 14.0 | sí | 11, 13 | ninguna |
| i202610005 | Rosa Medina | CI | 15.5 | no | 15, 16 | PI5620 (pagada), PS5511, DM5512, SA5516, BD5410: 21 créditos |
| i202610006 | Pedro Salas | CI | 13.0 | no | 14, 9 | ninguna |
| i202620001 | Sofía Vargas | AE | 16.0 | no | sin historial | ninguna |

Las matrículas del periodo 2026-2 tienen los ids 1 a 8 en este orden: Carla IA5513, Rosa PI5620, Rosa PS5511, Rosa DM5512, Rosa SA5516, Rosa BD5410, Luis PS5511 y Ana DM5512. Las del historial 2026-1 tienen los ids 9 a 20 y todas están pagadas. El pago 1 es el de Rosa en PI5620, con TARJETA.

## Casos listos para probar

Partiendo de la API recién iniciada. Salvo que se indique otro, con el token de admin.

| Caso | Request | Resultado |
|---|---|---|
| Matrícula válida, 20 % | Ana en PS5511 | 201, monto 288.0 |
| Matrícula válida, 10 % | Luis en TS5514 | 201, monto 432.0 |
| Matrícula válida, sin descuento | Carla en PS5511 | 201, monto 360.0 |
| Volver a matricular un curso anulado | Ana en DM5512 | 201, monto 384.0 |
| Otra carrera | Sofía en AD5101 | 201, monto 324.0 |
| Duplicada | Carla en IA5513 | 409 MATRICULA_DUPLICADA |
| Sin vacantes | Luis en IA5513 | 409 SIN_VACANTES |
| Con deuda | Jorge en PS5511 | 422 ALUMNO_CON_DEUDA |
| Cruce de horario | Luis en QA5517 | 409 CRUCE_HORARIO |
| Límite de créditos | Rosa en TS5514 | 422 LIMITE_CREDITOS |
| Prerrequisito pendiente | Pedro en PS5511 | 422 PRERREQUISITO_PENDIENTE |
| Curso ya aprobado | Pedro en LP5401 | 422 CURSO_APROBADO |
| Curso de otra carrera | Sofía en PS5511 | 422 CURSO_DE_OTRA_CARRERA |
| Alumno matricula a otro | token de Ana, Luis en BD5410 | 403 ACCESO_DENEGADO |
| Cotización | Ana con PS5511 y TS5514 | 200, 7 créditos, subtotal 840.0, total 672.0 |
| Pagar | `PATCH /api/matriculas/7/pagar` con `{"metodo": "YAPE"}` | 200, estado PAGADA |
| Nota del docente | después de pagar la 7, `PATCH /api/matriculas/7/nota` con token de D0001 y `{"nota": 17}` | 200, condicion APROBADO |
| Nota de otro docente | lo mismo con token de D0002 | 403 ACCESO_DENEGADO |
| Nota sin pagar | `PATCH /api/matriculas/3/nota` con token de D0001 | 409 MATRICULA_NO_PAGADA |
| Anular una pagada | `PATCH /api/matriculas/2/anular` | 409 MATRICULA_PAGADA |
| Anular una ya anulada | `PATCH /api/matriculas/8/anular` | 409 MATRICULA_YA_ANULADA |
| Anular una inexistente | `PATCH /api/matriculas/99999/anular` | 404 MATRICULA_NO_ENCONTRADA |
| Constancia | Rosa | 21 créditos, total 2268.0, pagado 648.0, pendiente 1620.0 |
| Historial | Pedro | créditos aprobados 4, promedio ponderado 11.86 |
| Sin token | `GET /api/alumnos` | 401 NO_AUTENTICADO |
| Rol sin permiso | `GET /api/alumnos` con token de Ana | 403 ACCESO_DENEGADO |
| Bloqueo | 5 logins de Jorge con clave errada | el quinto da 423 CUENTA_BLOQUEADA |
| Logout | `POST /api/auth/logout` y repetir una petición con el mismo token | 401 TOKEN_INVALIDO |

## Token en las pruebas con Rest Assured

```java
String token = given()
        .contentType(ContentType.JSON)
        .body(Map.of("username", "admin", "password", "Admin2026"))
        .post("/api/auth/login")
        .then().statusCode(200)
        .extract().path("accessToken");

given().auth().oauth2(token)
        .get("/api/alumnos")
        .then().statusCode(200);
```

En Screenplay, la task de login guarda el token con `actor.remember("token", ...)` y las demás lo envían con `.with(req -> req.auth().oauth2(token))`.
