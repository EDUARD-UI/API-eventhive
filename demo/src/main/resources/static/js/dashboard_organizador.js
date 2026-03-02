document.addEventListener('DOMContentLoaded', function() {
    // Restaurar la sección activa desde localStorage
    const seccionActiva = localStorage.getItem('seccionActiva') || 'seccion-panel';
    mostrarSeccion(seccionActiva);
    
    // Inicializar gráficos si estamos en reportes
    if (seccionActiva === 'seccion-reportes') {
        setTimeout(inicializarGraficosReportes, 100);
    }

    // Navegación del sidebar
    const sidebarLinks = document.querySelectorAll('.sidebar-link');
    const sections = document.querySelectorAll('.content-section');

    sidebarLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            
            const targetId = this.getAttribute('href').substring(1);
            
            // Guardar la sección activa en localStorage
            localStorage.setItem('seccionActiva', targetId);
            
            // Mostrar la sección
            mostrarSeccion(targetId);
            
            // Inicializar gráficos si es la sección de reportes
            if (targetId === 'seccion-reportes') {
                setTimeout(inicializarGraficosReportes, 100);
            }
        });
    });

    // Modales
    const modals = document.querySelectorAll('.modal');
    const modalTriggers = document.querySelectorAll('.modal-trigger');
    const closeButtons = document.querySelectorAll('.close-button');

    modalTriggers.forEach(btn => {
        btn.addEventListener('click', () => {
            const modalId = btn.dataset.modal;
            document.getElementById(modalId).classList.add('show');
        });
    });

    closeButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            btn.closest('.modal').classList.remove('show');
        });
    });

    window.addEventListener('click', e => {
        modals.forEach(m => {
            if (e.target === m) m.classList.remove('show');
        });
    });

    // Botones de acción en tablas
    document.addEventListener('click', e => {
        const btn = e.target.closest('button');
        if (!btn) return;

        const row = btn.closest('tr');

        if (btn.classList.contains('btn-delete')) {
            if (confirm("¿Desea eliminar este elemento?")) row.remove();
        }

        if (btn.classList.contains('btn-deactivate')) {
            const inactive = row.classList.contains('deactivated');

            if (confirm(inactive ? "¿Reactivar elemento?" : "¿Desactivar elemento?")) {
                row.classList.toggle('deactivated');
                btn.classList.toggle('active');
            }
        }
    });

    // Botones de acceso rápido
    const btnCrearEvento = document.getElementById('btnCrearEvento');
    if (btnCrearEvento) {
        btnCrearEvento.addEventListener('click', function() {
            localStorage.setItem('seccionActiva', 'seccion-eventos');
            mostrarSeccion('seccion-eventos');
            
            // Abrir modal de evento
            setTimeout(() => {
                const modalEvento = document.getElementById('modal-evento');
                if (modalEvento) {
                    // Limpiar formulario
                    const form = modalEvento.querySelector('form');
                    if (form) form.reset();
                    modalEvento.classList.add('show');
                }
            }, 100);
        });
    }

    const btnVerEventos = document.getElementById('btnVerEventos');
    if (btnVerEventos) {
        btnVerEventos.addEventListener('click', function() {
            localStorage.setItem('seccionActiva', 'seccion-eventos');
            mostrarSeccion('seccion-eventos');
        });
    }

    const btnReportes = document.getElementById('btnReportes');
    if (btnReportes) {
        btnReportes.addEventListener('click', function() {
            localStorage.setItem('seccionActiva', 'seccion-reportes');
            mostrarSeccion('seccion-reportes');
            setTimeout(inicializarGraficosReportes, 100);
        });
    }
});

// Función para mostrar sección
function mostrarSeccion(seccionId) {
    // Ocultar todas las secciones
    document.querySelectorAll('.content-section').forEach(section => {
        section.classList.remove('active');
    });
    
    // Mostrar la sección seleccionada
    const seccion = document.getElementById(seccionId);
    if (seccion) {
        seccion.classList.add('active');
    }
    
    // Actualizar enlaces activos del sidebar
    document.querySelectorAll('.sidebar-link').forEach(link => {
        link.classList.remove('active');
        if (link.getAttribute('href') === '#' + seccionId) {
            link.classList.add('active');
        }
    });
}

// Función para inicializar gráficos de reportes
function inicializarGraficosReportes() {
    const chartReporteVentas = document.getElementById("chartReporteVentas");
    if (chartReporteVentas) {
        new Chart(chartReporteVentas, {
            type: "bar",
            data: {
                labels: ["Concierto", "Feria", "Seminario", "Expo"],
                datasets: [{
                    label: "Ventas",
                    data: [320, 540, 210, 410],
                    backgroundColor: "#2196F3"
                }]
            }
        });
    }

    const chartReporteAsistencia = document.getElementById("chartReporteAsistencia");
    if (chartReporteAsistencia) {
        new Chart(chartReporteAsistencia, {
            type: "line",
            data: {
                labels: ["Ene", "Feb", "Mar", "Abr", "May"],
                datasets: [{
                    label: "Asistencia",
                    data: [150, 200, 180, 260, 300],
                    borderColor: "#4CAF50",
                    borderWidth: 2,
                    tension: 0.3
                }]
            }
        });
    }
}