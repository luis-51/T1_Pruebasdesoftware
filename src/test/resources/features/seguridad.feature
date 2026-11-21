# language: es
Característica: Seguridad de la sesión
  Como equipo de calidad
  Quiero verificar el comportamiento de los tokens, el cierre de sesión y el bloqueo de cuentas
  Para garantizar que la API es segura antes de pasar a producción

  Escenario: Rotación del refresh token
    Dado que el alumno "i202610002" inicia sesión con la clave "Cibertec2026"
    Cuando renueva sus tokens con su refreshToken
    Entonces el codigo de respuesta es 200
    Y recibe un accessToken y un refreshToken nuevos
    Cuando intenta renovar otra vez con el refreshToken original
    Entonces el codigo de respuesta es 401
    Y el codigo de error es "REFRESH_REUTILIZADO"
    Y la respuesta cumple el esquema "error.json"
    Cuando consulta su perfil con su accessToken
    Entonces el codigo de respuesta es 401
    Y el codigo de error es "TOKEN_INVALIDO"
    Y la respuesta cumple el esquema "error.json"

  Escenario: Cierre de sesión
    Dado que el alumno "i202610002" inicia sesión con la clave "Cibertec2026"
    Cuando cierra sesión
    Entonces el codigo de respuesta es 204
    Cuando consulta su perfil con su accessToken
    Entonces el codigo de respuesta es 401
    Y el codigo de error es "TOKEN_INVALIDO"
    Y la respuesta cumple el esquema "error.json"

  Esquema del escenario: Bloqueo de cuenta por intentos fallidos
    Cuando el alumno "i202610004" intenta ingresar con la clave "<clave>" en el intento <intento>
    Entonces el codigo de respuesta es <status>
    Y el codigo de error es "<codigo>"
    Y la respuesta cumple el esquema "error.json"

    Ejemplos:
      | intento | clave        | status | codigo                |
      | 1       | ClaveMala1   | 401    | CREDENCIALES_INVALIDAS |
      | 2       | ClaveMala2   | 401    | CREDENCIALES_INVALIDAS |
      | 3       | ClaveMala3   | 401    | CREDENCIALES_INVALIDAS |
      | 4       | ClaveMala4   | 401    | CREDENCIALES_INVALIDAS |
      | 5       | ClaveMala5   | 423    | CUENTA_BLOQUEADA       |
      | 6       | Cibertec2026 | 423    | CUENTA_BLOQUEADA       |

  Escenario: Alumno sin permiso de administrador
    Dado que el alumno "i202610002" inicia sesión con la clave "Cibertec2026"
    Cuando intenta listar todos los alumnos
    Entonces el codigo de respuesta es 403
    Y el codigo de error es "ACCESO_DENEGADO"
    Y la respuesta cumple el esquema "error.json"
