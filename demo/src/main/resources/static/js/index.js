// Carga eventos destacados y categorías al iniciar la página
document.addEventListener('DOMContentLoaded', () => {
    cargarEventosDestacados();
    cargarCategorias();
});


function cargarEventosDestacados() {
    const apiUrl = '/api/eventos/destacados';

    fetch(apiUrl)
        .then(response => {
            if (!response.ok) throw new Error('Error al cargar eventos');
            return response.json();
        })
        .then(data => {
            const eventos = data.data || data; // Maneja ambos formatos
            renderizarEventos(eventos);
        })
        .catch(error => {
            console.error('Error cargando eventos:', error);
            document.getElementById('eventGrid').innerHTML = 
                '<p style="grid-column: 1/-1; text-align: center;">Error al cargar los eventos.</p>';
        });
}

function renderizarEventos(eventos) {
    const eventGrid = document.getElementById('eventGrid');
    eventGrid.innerHTML = '';

    if (!eventos || eventos.length === 0) {
        eventGrid.innerHTML = '<p style="grid-column: 1/-1; text-align: center;">No hay eventos disponibles.</p>';
        return;
    }

    eventos.forEach(evento => {
        const eventCard = document.createElement('div');
        eventCard.className = 'event-card';

        const imagenUrl = evento.foto 
            ? `/uploads/eventos/${evento.foto}` 
            : '/images/placeholder.jpg';

        const fechaFormato = formatearFecha(evento.fecha);

        eventCard.innerHTML = `
            <img src="${imagenUrl}" alt="${evento.titulo}" onerror="this.src='/images/placeholder.jpg'">
            <div class="card-content">
                <h3>${evento.titulo}</h3>
                <p class="event-meta">
                    <i class="far fa-calendar-alt"></i>
                    <span>${fechaFormato}</span>
                    <i class="fas fa-map-marker-alt"></i>
                    <span>${evento.lugar || 'Por determinar'}</span>
                </p>
                <p class="event-description">${evento.descripcion || ''}</p>
                <div class="card-footer">
                    <a href="/eventos/${evento.id}" class="btn-buy">
                        Comprar Tiquete <i class="fas fa-ticket-alt"></i>
                    </a>
                </div>
                <div class="event-tags">
                    <span>${evento.categoria ? evento.categoria.nombre : 'Sin categoría'}</span>
                </div>
            </div>
        `;

        eventGrid.appendChild(eventCard);
    });
}

function cargarCategorias() {
    const apiUrl = '/api/categorias/destacadas'; // Ajusta según tu API

    fetch(apiUrl)
        .then(response => {
            if (!response.ok) throw new Error('Error al cargar categorías');
            return response.json();
        })
        .then(data => {
            const categorias = data.data || data || [];
            renderizarCategorias(categorias);
            llenarSelectorCategorias(categorias);
        })
        .catch(error => {
            console.error('Error cargando categorías:', error);
        });
}

function renderizarCategorias(categorias) {
    const categoryGrid = document.getElementById('categoryGrid');
    categoryGrid.innerHTML = '';

    if (!categorias || categorias.length === 0) {
        categoryGrid.innerHTML = '<p style="grid-column: 1/-1; text-align: center;">No hay categorías disponibles.</p>';
        return;
    }

    categorias.forEach(categoria => {
        const imagenUrl = categoria.foto 
            ? `/uploads/categorias/${categoria.foto}` 
            : '/images/placeholder.jpg';

        const categoryCard = document.createElement('a');
        categoryCard.href = `/categorias/${categoria.id}`;
        categoryCard.className = 'category-card';
        categoryCard.style.backgroundImage = `url('${imagenUrl}')`;
        categoryCard.onerror = () => {
            categoryCard.style.backgroundImage = "url('/images/placeholder.jpg')";
        };

        categoryCard.innerHTML = `
            <div class="category-overlay">
                <h3>${categoria.nombre}</h3>
            </div>
        `;

        categoryGrid.appendChild(categoryCard);
    });
}

function llenarSelectorCategorias(categorias) {
    const categorySelect = document.getElementById('categorySelect');

    if (!categorias || categorias.length === 0) return;

    categorias.forEach(categoria => {
        const option = document.createElement('option');
        option.value = categoria.id;
        option.textContent = categoria.nombre;
        categorySelect.appendChild(option);
    });

    // Manejar cambio de categoría
    categorySelect.addEventListener('change', (e) => {
        if (e.target.value) {
            window.location.href = `/categorias/${e.target.value}`;
        }
    });
}

//FUNCIONES DE APOYO

function formatearFecha(fechaString) {
    if (!fechaString) return 'Fecha por determinar';

    const fecha = new Date(fechaString);
    const opciones = { year: 'numeric', month: 'short', day: 'numeric' };
    return fecha.toLocaleDateString('es-ES', opciones);
}
