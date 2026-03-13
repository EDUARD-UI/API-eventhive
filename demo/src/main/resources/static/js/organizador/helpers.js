/* ════════════════════════════════════════
   EventHive — helpers.js
   Funciones utilitarias reutilizables.
════════════════════════════════════════ */

/**
 * Formatea una fecha al formato "DD de MMM de YYYY"
 */
function fmtDate(dateStr) {
  if (!dateStr) return '—';
  try {
    const d = new Date(dateStr + 'T00:00:00');
    return d.toLocaleDateString('es-CO', { day: 'numeric', month: 'long', year: 'numeric' });
  } catch {
    return '—';
  }
}

/**
 * Formatea una cantidad como moneda COP
 */
function fmtCurrency(val) {
  if (!val) return '$0';
  return new Intl.NumberFormat('es-CO', {
    style: 'currency',
    currency: 'COP',
    maximumFractionDigits: 0,
  }).format(val);
}

/**
 * Obtiene las iniciales del nombre y apellido
 */
function getInitials(nombre = '', apellido = '') {
  const n = (nombre || '').charAt(0).toUpperCase();
  const a = (apellido || '').charAt(0).toUpperCase();
  return (n + a) || 'OG';
}

/**
 * HTML vacío para listas sin datos
 */
function emptyHTML(icon, title, subtitle = '') {
  return `
    <div style="text-align:center;padding:40px 20px;color:var(--muted)">
      <div style="font-size:2.5rem;margin-bottom:12px">${icon}</div>
      <div style="font-weight:600;margin-bottom:4px">${title}</div>
      ${subtitle ? `<div style="font-size:.85rem">${subtitle}</div>` : ''}
    </div>
  `;
}

/**
 * Calcula el porcentaje de ocupación
 */
function _ocuPct(localidad) {
  if (!localidad?.capacidad || localidad.capacidad === 0) return 0;
  const ocupados = (localidad.capacidad || 0) - (localidad.disponibles || 0);
  return Math.round((ocupados / localidad.capacidad) * 100);
}

/**
 * Retorna el color según el porcentaje de ocupación
 */
function _ocuColor(pct) {
  if (pct >= 80) return 'var(--red)';
  if (pct >= 50) return 'var(--amber)';
  return 'var(--green)';
}

/**
 * Router global para navegación entre páginas
 */
const Router = {
  currentPage: 'dashboard',

  async go(page) {
    this.currentPage = page;
    navigate(page);
  },
};
