# language: es
Característica: Matrícula del alumno
  Como alumno de CIBERTEC
  Quiero cotizar, matricularme y pagar un curso
  Para asegurar mi vacante en el periodo abierto

  Antecedentes:
    Dado que el alumno "i202610002" inicia sesión con la clave "Cibertec2026"

  Escenario: Cotizar el curso
    Cuando cotiza el curso "PI5620"
    Entonces el codigo de respuesta es 200
    Y los créditos cotizados son 6
    Y el subtotal cotizado es 720,0
    Y el descuento cotizado es 10
    Y el total cotizado es 648,0
    Y la respuesta cumple el esquema "cotizacion.json"

  Escenario: Matricularse y pagar el curso
    Cuando cotiza el curso "PI5620"
    Y se matricula en el curso "PI5620"
    Entonces el codigo de respuesta es 201
    Y el estado de la matrícula es "REGISTRADA"
    Y el monto de la matrícula es igual al total cotizado
    Y el monto de la matrícula es 648,0
    Y la respuesta cumple el esquema "matricula.json"
    Cuando paga la matrícula sin cuerpo en la petición
    Entonces el codigo de respuesta es 200
    Y el estado de la matrícula es "PAGADA"
    Y la fecha de pago tiene valor
    Cuando consulta sus pagos
    Entonces el codigo de respuesta es 200
    Y existe un solo pago de su matrícula
    Y el pago de su matrícula tiene el método "EFECTIVO"
    Y el pago de su matrícula tiene un numero de operacion con formato OP-000000

  Escenario: Regla de negocio que debe fallar - pagar otra vez la misma matrícula
    Dado que su matrícula del curso "PI5620" ya está pagada
    Cuando paga otra vez la misma matrícula sin cuerpo en la petición
    Entonces el codigo de respuesta es 409
    Y el codigo de error es "MATRICULA_YA_PAGADA"
    Y la respuesta cumple el esquema "error.json"
