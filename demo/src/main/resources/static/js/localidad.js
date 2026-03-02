function incrementarCantidad(button) {
    const input = button.parentElement.querySelector('.cantidad-input');
    const max = parseInt(input.getAttribute('max'));
    const current = parseInt(input.value);
    
    if (current < max) {
        input.value = current + 1;
    }
}

function decrementarCantidad(button) {
    const input = button.parentElement.querySelector('.cantidad-input');
    const current = parseInt(input.value);
    
    if (current > 1) {
        input.value = current - 1;
    }
}

// Función principal para manejar la compra
document.addEventListener('DOMContentLoaded', function() {
    console.log('Script localidad.js cargado correctamente');
    
    const botonesComprar = document.querySelectorAll('.btn-comprar');
    console.log('Total de botones de compra encontrados:', botonesComprar.length);
    
    botonesComprar.forEach((button, index) => {
        console.log(`Botón ${index + 1}:`, {
            id: button.getAttribute('data-localidad-id'),
            nombre: button.getAttribute('data-localidad-nombre'),
            precio: button.getAttribute('data-precio'),
            disabled: button.disabled
        });
        
        button.addEventListener('click', function(e) {
            e.preventDefault();
            console.log('Click en botón comprar');
            
            // Si está deshabilitado, no hacer nada
            if (this.disabled) {
                console.log('Botón deshabilitado - AGOTADO');
                return;
            }
            
            // Obtener el card contenedor
            const card = this.closest('.localidad-card');
            if (!card) {
                console.error('No se encontró .localidad-card');
                alert('Error: No se pudo encontrar la información de la localidad');
                return;
            }
            
            // Obtener datos del botón
            const localidadId = this.getAttribute('data-localidad-id');
            const localidadNombre = this.getAttribute('data-localidad-nombre');
            const precio = this.getAttribute('data-precio');
            
            // Obtener cantidad
            const cantidadInput = card.querySelector('.cantidad-input');
            const cantidad = cantidadInput ? cantidadInput.value : null;
            
            // Obtener nombre del evento
            const eventoElement = document.querySelector('.evento-info h1');
            const eventoNombre = eventoElement ? eventoElement.textContent.trim() : null;
            
            console.log('Datos recopilados:', {
                localidadId,
                localidadNombre,
                precio,
                cantidad,
                eventoNombre
            });
            
            if (!localidadId || !localidadNombre || precio === null || precio === undefined || !cantidad || !eventoNombre) {
                console.error('Faltan datos:', {
                    localidadId: !!localidadId,
                    localidadNombre: !!localidadNombre,
                    precio: precio !== null && precio !== undefined,
                    cantidad: !!cantidad,
                    eventoNombre: !!eventoNombre
                });
                alert('Error: Faltan datos para procesar la compra. Revisa la consola para más detalles.');
                return;
            }
            
            // Construir URL
            const params = new URLSearchParams({
                evento: eventoNombre,
                localidad: localidadNombre,
                localidadId: localidadId,
                precio: precio,
                cantidad: cantidad
            });
            
            const url = `/pago?${params.toString()}`;
            console.log('URL construida:', url);
            console.log('Redirigiendo...');
            
            // Redirigir a la página de pago
            window.location.href = url;
        });
    });
    
    // Verificar estructura del HTML
    const eventoInfo = document.querySelector('.evento-info h1');
    if (!eventoInfo) {
        console.error('ADVERTENCIA: No se encontró .evento-info h1');
    } else {
        console.log('Título del evento encontrado:', eventoInfo.textContent);
    }
    
    const localidadCards = document.querySelectorAll('.localidad-card');
    console.log('Total de tarjetas de localidad:', localidadCards.length);
});