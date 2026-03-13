/* ════════════════════════════════════════
   EventHive — globals.js
   Funciones globales y garantías de disponibilidad.
════════════════════════════════════════ */

/**
 * Asegura que las funciones críticas estén disponibles globalmente
 * Se ejecuta después de cargar todos los módulos base.
 */

/* Garantizar que reloadEventos esté disponible (definido en eventos.js) */
if (typeof reloadEventos === 'undefined') {
  window.reloadEventos = async function() {
    try {
      const r = await EventosAPI.listar();
      Store.setEventos(r.data || []);
      fillSelectEventos();
    } catch (err) {
      console.warn('Error recargando eventos:', err);
    }
  };
}

/* Garantizar que renderDashboard esté disponible (definido en dashboard.js) */
if (typeof renderDashboard === 'undefined') {
  window.renderDashboard = function() {
    const stEv = document.getElementById('st-ev');
    const stLoc = document.getElementById('st-loc');
    const stPr = document.getElementById('st-pr');
    if (stEv) stEv.textContent = Store.eventos.length;
    if (stLoc) stLoc.textContent = Store.localidades.length;
    if (stPr) stPr.textContent = Store.promociones.length;
  };
}

/* Garantizar que updateNavBadge esté disponible (definido en sidebar.js) */
if (typeof updateNavBadge === 'undefined') {
  window.updateNavBadge = function(count) {
    const nb = document.getElementById('nb-ev');
    if (nb) nb.textContent = count;
  };
}
