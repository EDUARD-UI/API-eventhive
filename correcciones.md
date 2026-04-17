servicePromocion: Los métodos reciben fechas como String en lugar de LocalDate. Spring no convierte automáticamente en servicios.

Hay métodos como obtenerBoletosDTO(), obtenerHistorialDTO() en servicios que solo transforman. Mejor usar constructores o métodos estáticos en los DTOs.