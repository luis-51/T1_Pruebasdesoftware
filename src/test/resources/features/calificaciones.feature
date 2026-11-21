# language: es
Característica: Calificación de matrículas
  Como docente de CIBERTEC
  Quiero registrar la nota final de una matrícula pagada
  Para que el historial académico recalcule el promedio ponderado y los créditos aprobados

  Antecedentes:
    Dado que la alumna "i202610005" inicia sesión con la clave "Cibertec2026"

  Esquema del escenario: El docente registra la nota y el historial se actualiza
    Dado que el docente "D0001" inicia sesión con la clave "Cibertec2026"
    Y la alumna "i202610005" obtiene el id de su matrícula del curso "PI5620"
    Cuando el docente "D0001" registra la nota <nota> de la matrícula de la alumna "i202610005"
    Entonces el codigo de respuesta es 200
    Y la condición de la matrícula es "<condicion>"
    Cuando la alumna "i202610005" consulta su historial
    Entonces el codigo de respuesta es 200
    Y la respuesta cumple el esquema "historial.json"
    Y el curso "PI5620" figura en el historial con nota <nota> y condición "<condicion>"
    Y el historial indica <creditos> créditos aprobados
    Y el promedio ponderado del historial es <promedio>

    Ejemplos:
      | nota | condicion   | creditos | promedio |
      | 13   | APROBADO    | 13       | 14,31    |
      | 12,5 | DESAPROBADO | 7        | 14,08    |

  Escenario: Un docente que no dicta el curso intenta registrar la nota
    Dado que el docente "D0002" inicia sesión con la clave "Cibertec2026"
    Y la alumna "i202610005" obtiene el id de su matrícula del curso "PI5620"
    Cuando el docente "D0002" registra la nota 15 de la matrícula de la alumna "i202610005"
    Entonces el codigo de respuesta es 403
    Y el codigo de error es "ACCESO_DENEGADO"
    Y la respuesta cumple el esquema "error.json"

  Escenario: No se puede calificar una matrícula que no está pagada
    Dado que el docente "D0001" inicia sesión con la clave "Cibertec2026"
    Y la alumna "i202610005" obtiene el id de su matrícula del curso "PS5511"
    Cuando el docente "D0001" registra la nota 15 de la matrícula de la alumna "i202610005"
    Entonces el codigo de respuesta es 409
    Y el codigo de error es "MATRICULA_NO_PAGADA"
    Y la respuesta cumple el esquema "error.json"
