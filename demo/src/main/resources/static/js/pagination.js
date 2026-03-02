// pagination.js - Sistema de paginación para eventos
class EventPagination {
    constructor(containerId, itemsPerPage = 9) {
        this.container = document.querySelector(containerId);
        this.itemsPerPage = itemsPerPage;
        this.currentPage = 1;
        this.items = [];
        this.totalPages = 0;
        
        this.init();
    }
    
    init() {
        // Obtener todos los eventos
        this.items = Array.from(this.container.querySelectorAll('.event-card'));
        this.totalPages = Math.ceil(this.items.length / this.itemsPerPage);
        
        // Crear contenedor de paginación
        this.createPaginationControls();
        
        // Mostrar primera página
        this.showPage(1);
    }
    
    createPaginationControls() {
        const paginationHTML = `
            <div class="pagination-container">
                <button class="pagination-btn prev-btn" data-action="prev">
                    « Anterior
                </button>
                <div class="pagination-numbers"></div>
                <button class="pagination-btn next-btn" data-action="next">
                    Siguiente »
                </button>
            </div>
            <div class="pagination-info"></div>
        `;
        
        // Insertar después del grid
        this.container.insertAdjacentHTML('afterend', paginationHTML);
        
        // Añadir event listeners
        document.querySelectorAll('.pagination-btn').forEach(btn => {
            btn.addEventListener('click', (e) => this.handleClick(e));
        });
        
        this.updatePaginationControls();
    }
    
    handleClick(e) {
        const action = e.target.closest('button')?.dataset.action;
        
        if (action === 'prev' && this.currentPage > 1) {
            this.showPage(this.currentPage - 1);
        } else if (action === 'next' && this.currentPage < this.totalPages) {
            this.showPage(this.currentPage + 1);
        } else if (action === 'page') {
            const page = parseInt(e.target.dataset.page);
            this.showPage(page);
        }
    }
    
    showPage(pageNum) {
        this.currentPage = pageNum;
        
        // Ocultar todos los items
        this.items.forEach(item => {
            item.style.display = 'none';
        });
        
        // Mostrar items de la página actual
        const start = (pageNum - 1) * this.itemsPerPage;
        const end = start + this.itemsPerPage;
        
        this.items.slice(start, end).forEach((item, index) => {
            item.style.display = 'block';
            item.style.opacity = '0';
            
            // Pequeña animación
            setTimeout(() => {
                item.style.opacity = '1';
            }, index * 50);
        });
        
        // Actualizar controles
        this.updatePaginationControls();
        
        // Scroll suave hacia arriba
        this.scrollToTop();
    }
    
    updatePaginationControls() {
        // Actualizar botones prev/next
        const prevBtn = document.querySelector('.prev-btn');
        const nextBtn = document.querySelector('.next-btn');
        
        prevBtn.disabled = this.currentPage === 1;
        nextBtn.disabled = this.currentPage === this.totalPages;
        
        // Actualizar números de página
        this.updatePageNumbers();
        
        // Actualizar info
        this.updateInfo();
    }
    
    updatePageNumbers() {
        const numbersContainer = document.querySelector('.pagination-numbers');
        let html = '';
        
        const maxVisible = 5; // Máximo de números visibles
        let startPage = Math.max(1, this.currentPage - Math.floor(maxVisible / 2));
        let endPage = Math.min(this.totalPages, startPage + maxVisible - 1);
        
        // Ajustar si estamos cerca del final
        if (endPage - startPage < maxVisible - 1) {
            startPage = Math.max(1, endPage - maxVisible + 1);
        }
        
        // Primera página
        if (startPage > 1) {
            html += `<button class="pagination-btn" data-action="page" data-page="1">1</button>`;
            if (startPage > 2) {
                html += `<span class="pagination-ellipsis">...</span>`;
            }
        }
        
        // Páginas del rango
        for (let i = startPage; i <= endPage; i++) {
            const activeClass = i === this.currentPage ? 'active' : '';
            html += `<button class="pagination-btn ${activeClass}" data-action="page" data-page="${i}">${i}</button>`;
        }
        
        // Última página
        if (endPage < this.totalPages) {
            if (endPage < this.totalPages - 1) {
                html += `<span class="pagination-ellipsis">...</span>`;
            }
            html += `<button class="pagination-btn" data-action="page" data-page="${this.totalPages}">${this.totalPages}</button>`;
        }
        
        numbersContainer.innerHTML = html;
        
        // Añadir event listeners a los nuevos botones
        numbersContainer.querySelectorAll('.pagination-btn').forEach(btn => {
            btn.addEventListener('click', (e) => this.handleClick(e));
        });
    }
    
    updateInfo() {
        const infoContainer = document.querySelector('.pagination-info');
        const start = (this.currentPage - 1) * this.itemsPerPage + 1;
        const end = Math.min(this.currentPage * this.itemsPerPage, this.items.length);
        
        infoContainer.textContent = `Mostrando ${start}-${end} de ${this.items.length} eventos`;
    }
    
    scrollToTop() {
        const header = document.querySelector('.cards-panel');
        if (header) {
            const offset = 100; // Offset para el navbar
            const top = header.getBoundingClientRect().top + window.pageYOffset - offset;
            
            window.scrollTo({
                top: top,
                behavior: 'smooth'
            });
        }
    }
}

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', () => {
    const eventsGrid = document.querySelector('.cards-grid');
    
    if (eventsGrid && eventsGrid.children.length > 0) {
        // Inicializar paginación con 10 items por página
        new EventPagination('.cards-grid', 10);
    }
});