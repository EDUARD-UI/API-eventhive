document.addEventListener('DOMContentLoaded', () => {
  initBusqueda();
});

function initBusqueda() {
  const input   = document.getElementById('searchInput');
  const results = document.getElementById('searchResults');
  if (!input || !results) return;

  let timer;

  input.addEventListener('input', () => {
    const q = input.value.trim();
    clearTimeout(timer);
    if (q.length < 2) {
      results.style.display = 'none';
      return;
    }
    results.style.display = 'block';
    results.innerHTML = '<p class="text-dark/30 text-sm text-center py-4">Buscando...</p>';
    timer = setTimeout(() => buscarPorTitulo(q), 200);
  });

  document.addEventListener('click', e => {
    if (!input.contains(e.target) && !results.contains(e.target)) {
      results.style.display = 'none';
    }
  });
}

async function buscarPorTitulo(q) {
  const results = document.getElementById('searchResults');
  try {
    const res  = await fetch(`${BASE}/api/eventos/buscar?titulo=${encodeURIComponent(q)}`,
                             { credentials: 'include' });
    if (!res.ok) {
      results.innerHTML = '<p class="text-dark/30 text-sm text-center py-4">Error al buscar</p>';
      return;
    }

    const json  = await res.json();
    const lista = Array.isArray(json.data) ? json.data : [];

    if (!lista.length) {
      results.innerHTML = '<p class="text-dark/30 text-sm text-center py-4">Sin resultados</p>';
      return;
    }

    results.innerHTML = lista.map(e => `
      <a href="/pages/infoEvento.html?id=${e.id}"
        class="flex items-center gap-3 px-4 py-3 hover:bg-gray-50 transition
               border-b border-gray-100 last:border-0">
        <div class="w-9 h-9 rounded-lg bg-brand/10 flex items-center justify-center shrink-0">
          <i class="fas fa-ticket-alt text-brand text-xs"></i>
        </div>
        <div>
          <p class="text-sm font-medium text-dark">${highlight(e.titulo, q)}</p>
          <p class="text-xs text-dark/35">${esc(e.nombreCategoria || '')}</p>
        </div>
      </a>`).join('');

  } catch {
    results.innerHTML = '<p class="text-dark/30 text-sm text-center py-4">Error al buscar</p>';
  }
}

// ── funciones de apoyo ───────────────────────────────────────────────
function highlight(text, q) {
  if (!text) return '';
  const safe = q.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
  return esc(text).replace(new RegExp(`(${safe})`, 'gi'),
    '<mark style="background:#ffc107;color:#212529;border-radius:2px;padding:0 2px">$1</mark>');
}

function esc(s) {
  if (!s) return '';
  return String(s)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}