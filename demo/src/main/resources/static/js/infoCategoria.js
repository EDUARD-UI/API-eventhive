const BASE     = '';
const PER_PAGE = 12;


let allEventos  = [];
let currentPage = 1;

document.addEventListener('DOMContentLoaded', () => {
  const id = getCategoriaId();
  if (!id) { showEmpty(); return; }
  loadCategoria(id);
  loadEventos(id);
});

function getCategoriaId() {
  const m = location.pathname.match(/\/categorias\/(\d+)/);
  if (m) return m[1];
  return new URLSearchParams(location.search).get('id');
}

// banner de la categoria (foto con el nombre)
async function loadCategoria(id) {
  try {
    const res  = await fetch(`${BASE}/api/categorias/${id}`, { credentials: 'include' });
    const json = await res.json();
    const cat  = json.data; // ← .data, no json directo
    if (!cat) return;

    document.title = `${cat.nombre} — EventHive`;
    document.getElementById('catNombre').textContent = cat.nombre;
    document.getElementById('breadCat').textContent  = cat.nombre;

    if (cat.foto) {
      document.getElementById('catBanner').style.backgroundImage =
        `linear-gradient(to bottom,rgba(0,0,0,.52) 0%,rgba(0,0,0,.78) 100%),
         url('/uploads/categorias/${cat.foto}')`;
    }
  } catch { /* mantiene defaults */ }
}

// mostrar los eventos de la categoria
async function loadEventos(categoriaId) {
  try {
    const res  = await fetch(`${BASE}/api/eventos/categoria/${categoriaId}`, { credentials: 'include' });
    const json = await res.json();
    allEventos = json.data || [];
  } catch {
    allEventos = [];
  }

  if (!allEventos.length) { showEmpty(); return; }

  document.getElementById('catConteo').textContent =
    `${allEventos.length} evento${allEventos.length !== 1 ? 's' : ''}`;

  renderPage(1);
  buildPagination();
}

// renderizacion de paginas
function renderPage(page) {
  currentPage = page;
  const start = (page - 1) * PER_PAGE;
  const slice = allEventos.slice(start, start + PER_PAGE);

  document.getElementById('eventosGrid').innerHTML = slice.map(cardHTML).join('');

  const end = Math.min(page * PER_PAGE, allEventos.length);
  document.getElementById('pgInfo').textContent =
    `Mostrando ${start + 1}–${end} de ${allEventos.length} eventos`;

  window.scrollTo({ top: 0, behavior: 'smooth' });
}

function cardHTML(e) {
  const img   = e.foto ? `/uploads/eventos/${e.foto}` : null;
  const fecha = formatFecha(e.fecha);
  return `
    <a href="/pages/infoEvento.html?id=${e.id}"
      class="event-card block bg-white rounded-2xl overflow-hidden border border-gray-100 shadow-sm">
      <div class="relative h-48 overflow-hidden bg-gray-100">
        ${img
          ? `<img src="${img}" alt="${e.titulo}"
               onerror="this.parentElement.innerHTML='<div class=\\'w-full h-full flex items-center justify-center\\'><i class=\\'fas fa-calendar-alt text-3xl text-gray-300\\'></i></div>'"
               class="w-full h-full object-cover transition duration-500 hover:scale-105"/>`
          : `<div class="w-full h-full flex items-center justify-center">
               <i class="fas fa-calendar-alt text-3xl text-gray-300"></i>
             </div>`
        }
      </div>
      <div class="p-5">
        <h3 class="font-display font-bold text-dark text-sm mb-3 line-clamp-2 leading-snug">
          ${e.titulo}
        </h3>
        <p class="text-dark/40 text-xs flex items-center gap-1 mb-1">
          <i class="far fa-calendar text-brand mr-1"></i>${fecha}
        </p>
        ${e.lugar
          ? `<p class="text-dark/40 text-xs">
               <i class="fas fa-map-marker-alt text-brand mr-1"></i>${e.lugar}
             </p>`
          : ''}
        <div class="mt-4 pt-4 border-t border-gray-100 flex items-center justify-between">
          <span class="text-xs text-dark/30">Ver detalles</span>
          <span class="w-7 h-7 rounded-full bg-brand/10 flex items-center justify-center">
            <i class="fas fa-arrow-right text-brand text-[10px]"></i>
          </span>
        </div>
      </div>
    </a>`;
}

// paginacion para las cards
function buildPagination() {
  const total = Math.ceil(allEventos.length / PER_PAGE);
  if (total <= 1) return;

  document.getElementById('paginacion').classList.remove('hidden');

  const prev    = document.getElementById('pgPrev');
  const next    = document.getElementById('pgNext');
  const numbers = document.getElementById('pgNumbers');

  function refresh() {
    prev.disabled = currentPage === 1;
    next.disabled = currentPage === total;
    numbers.innerHTML = '';
    getPageRange(currentPage, total).forEach(p => {
      if (p === '…') {
        numbers.innerHTML += `<span class="px-1 text-dark/25 self-center text-sm">…</span>`;
        return;
      }
      const btn = document.createElement('button');
      btn.textContent = p;
      btn.className = [
        'pg-btn w-10 h-10 rounded-xl border text-sm font-medium',
        p === currentPage
          ? 'active'
          : 'border-gray-200 text-dark/50 hover:border-brand hover:text-brand'
      ].join(' ');
      btn.addEventListener('click', () => { renderPage(p); refresh(); });
      numbers.appendChild(btn);
    });
  }

  prev.addEventListener('click', () => { if (currentPage > 1)     { renderPage(currentPage - 1); refresh(); } });
  next.addEventListener('click', () => { if (currentPage < total) { renderPage(currentPage + 1); refresh(); } });

  refresh();
}

function getPageRange(cur, total) {
  if (total <= 7) return Array.from({ length: total }, (_, i) => i + 1);
  if (cur <= 4)   return [1, 2, 3, 4, 5, '…', total];
  if (cur >= total - 3) return [1, '…', total-4, total-3, total-2, total-1, total];
  return [1, '…', cur - 1, cur, cur + 1, '…', total];
}

//funciones de apoyo
function showEmpty() {
  document.getElementById('eventosGrid').innerHTML = '';
  document.getElementById('emptyState').classList.remove('hidden');
}

function formatFecha(str) {
  if (!str) return 'Fecha por confirmar';
  // Spring puede devolver fecha como array [year, month, day] o string 'YYYY-MM-DD'
  if (Array.isArray(str)) {
    const [y, m, d] = str;
    return new Date(y, m - 1, d).toLocaleDateString('es-ES',
      { day: 'numeric', month: 'short', year: 'numeric' });
  }
  return new Date(str).toLocaleDateString('es-ES',
    { day: 'numeric', month: 'short', year: 'numeric' });
}