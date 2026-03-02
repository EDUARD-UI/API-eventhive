const inputBusqueda = document.getElementById('searchInput');
const divResultados = document.getElementById('resultados');

let timeoutId;

// Se ejecuta cada vez que el usuario escribe
inputBusqueda.addEventListener('input', function() {
    const texto = this.value.trim();
    
    clearTimeout(timeoutId);
    
    // Si no hay texto, ocultar resultados
    if (texto.length < 2) {
        divResultados.innerHTML = '';
        divResultados.style.display = 'none';
        return;
    }
    
    // Mostrar loading
    divResultados.innerHTML = '<div class="resultado-loading">Buscando...</div>';
    divResultados.style.display = 'block';
    
    // Debounce - esperar 150ms después de que el usuario deje de escribir
    timeoutId = setTimeout(() => {
        realizarBusqueda(texto);
    }, 150);
});

function realizarBusqueda(texto) {
    fetch(`/eventos/buscar?titulo=${encodeURIComponent(texto)}`)
        .then(response => {
            if (!response.ok) throw new Error('Error en la respuesta');
            return response.json();
        })
        .then(eventos => {
            mostrarResultados(eventos, texto);
        })
        .catch(error => {
            console.error('Error:', error);
            divResultados.innerHTML = '<div class="resultado-error">Error al buscar</div>';
        });
}

function mostrarResultados(eventos, textoBuscado) {
    // Limpiar resultados anteriores
    divResultados.innerHTML = '';
    
    if (eventos.length === 0) {
        divResultados.innerHTML = '<div class="resultado-vacio">No se encontraron eventos</div>';
    } else {
        // Mostrar cada evento encontrado
        eventos.forEach(evento => {
            const div = document.createElement('div');
            div.className = 'resultado-item';
            div.innerHTML = `
                <a href="/eventos/${evento.id}" class="resultado-link">
                    <strong>${resaltarTexto(evento.titulo, textoBuscado)}</strong>
                    <small>${evento.nombreCategoria}</small>
                </a>
            `;
            divResultados.appendChild(div);
        });
    }
    
    divResultados.style.display = 'block';
}

function resaltarTexto(texto, busqueda) {
    const regex = new RegExp(`(${busqueda})`, 'gi');
    return texto.replace(regex, '<mark>$1</mark>');
}

// Cerrar resultados al hacer click fuera
document.addEventListener('click', function(e) {
    if (!e.target.closest('.search-bar-hero')) {
        divResultados.style.display = 'none';
    }
});