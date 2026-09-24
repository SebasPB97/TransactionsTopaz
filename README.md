# TransactionsTopaz
## Requisitos a implementar
1. Reglas de negocio: una transacción solo puede reversarse si su estado actual es APROBADA; si el estado
es distinto, el endpoint debe responder 409 Conflict con un mensaje claro. Además, solo puede
reversarse dentro de las 24 horas siguientes a fechaCreacion; si ya pasó ese plazo, debe responder 422
Unprocessable Entity. Si la reversión es válida, cambiar el estado a REVERSADA y persistir.
2. Concurrencia: dos solicitudes simultáneas de reversión sobre la misma transacción no deben tener éxito
ambas de forma silenciosa. Usa el campo @Version ya presente en la entidad (bloqueo optimista) y
maneja OptimisticLockException devolviendo 409 Conflict con un mensaje indicando que la transacción
fue modificada concurrentemente.
3. Manejo de errores: usa un mecanismo limpio (por ejemplo, excepciones de negocio propias junto con
@ExceptionHandler/@ControllerAdvice) en lugar de condicionales dispersos devolviendo códigos HTTP
directamente desde el controlador.
4. Pruebas: escribe al menos una prueba unitaria de TransaccionService que verifique la regla de las 24
horas, usando Mockito para el repositorio.
## Entregables al finalizar los 40 minutos:
• Código fuente completo de las clases modificadas o creadas (TransaccionService, excepciones nuevas,
manejador de excepciones y cualquier clase auxiliar).
• Al menos un test unitario ejecutable que valide la regla de las 24 horas.
• (Opcional, si el tiempo alcanza) un test adicional para el caso de conflicto de estado.
