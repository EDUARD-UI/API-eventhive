// Función para restaurar la sección activa después de recargar
function restoreActiveSection() {
    const savedSection = sessionStorage.getItem('activeSection');
    if (savedSection) {
        // Remover clase active de todas las secciones
        document.querySelectorAll('.content-section').forEach(section => {
            section.classList.remove('active');
        });
        
        // Remover clase active de todos los links
        document.querySelectorAll('.sidebar-link').forEach(link => {
            link.classList.remove('active');
        });
        
        // Activar la sección guardada
        const section = document.getElementById(savedSection);
        if (section) {
            section.classList.add('active');
            
            // Activar el link correspondiente
            const link = document.querySelector(`.sidebar-link[href="#${savedSection}"]`);
            if (link) {
                link.classList.add('active');
            }
        }
        
        // Limpiar el sessionStorage
        sessionStorage.removeItem('activeSection');
    }
}

// Restaurar sección al cargar la página
document.addEventListener('DOMContentLoaded', function() {
    restoreActiveSection();
    
    // Navegación entre secciones
    const sidebarLinks = document.querySelectorAll('.sidebar-link');
    const sections = document.querySelectorAll('.content-section');

    sidebarLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            
            // Remover clase activa de todos los links
            sidebarLinks.forEach(l => l.classList.remove('active'));
            
            // Agregar clase activa al link clickeado
            this.classList.add('active');
            
            // Ocultar todas las secciones
            sections.forEach(section => section.classList.remove('active'));
            
            // Mostrar la sección correspondiente
            const targetSection = this.getAttribute('href').substring(1);
            const section = document.getElementById(targetSection);
            if (section) {
                section.classList.add('active');
            }
        });
    });

    // Gestión de modales
    const modalTriggers = document.querySelectorAll('.modal-trigger');
    const closeButtons = document.querySelectorAll('.close-button');
    const modals = document.querySelectorAll('.modal');

    // Abrir modal
    modalTriggers.forEach(trigger => {
        trigger.addEventListener('click', function(e) {
            e.preventDefault();
            const modalId = this.getAttribute('data-modal');
            const modal = document.getElementById(modalId);
            
            if (modal) {
                modal.classList.add('show');
                
                // Limpiar formulario si es acción "new"
                const action = this.getAttribute('data-action');
                if (action === 'new-evento' || !action) {
                    const form = modal.querySelector('form');
                    if (form) {
                        form.reset();
                        // Limpiar campo ID oculto
                        const idField = form.querySelector('[id$="-id"]');
                        if (idField) {
                            idField.value = '';
                        }
                    }
                    
                    // Cambiar título del modal
                    const modalTitle = modal.querySelector('.modal-title, h2');
                    if (modalTitle && modalId.includes('evento')) {
                        modalTitle.textContent = 'Crear Evento';
                    }
                }
            }
        });
    });

    // Cerrar modal
    closeButtons.forEach(button => {
        button.addEventListener('click', function() {
            const modal = this.closest('.modal');
            if (modal) {
                modal.classList.remove('show');
            }
        });
    });

    // Cerrar modal al hacer clic fuera
    modals.forEach(modal => {
        modal.addEventListener('click', function(e) {
            if (e.target === this) {
                this.classList.remove('show');
            }
        });
    });

    // Cerrar modal con tecla ESC
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            modals.forEach(modal => {
                modal.classList.remove('show');
            });
        }
    });
});