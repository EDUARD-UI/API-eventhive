async function initDashboard() {
  document.getElementById('h-date').textContent =
    new Date().toLocaleDateString('es-CO', {
      weekday: 'long', day: 'numeric', month: 'long', year: 'numeric'
    });

  if (ORG.usuario) {
    document.getElementById('h-greeting').textContent =
      `Bienvenido, ${ORG.usuario.nombre || 'Organizador'} 👋`;
  }

  try {
    const res  = await fetch('/api/eventos/organizador/estadisticas', { credentials: 'include' });
    const json = await res.json();
    if (!json.success) throw new Error(json.mensaje);

    const d = json.data;
    document.getElementById('st-ev').textContent     = d.totalEventos;
    document.getElementById('st-loc').textContent    = d.totalLocalidades;
    document.getElementById('st-promo').textContent  = d.totalPromociones;
    document.getElementById('nb-ev').textContent     = d.totalEventos;

    ORG.dashboard = d;
  } catch {
    document.getElementById('st-ev').textContent    = '—';
    document.getElementById('st-loc').textContent   = '—';
    document.getElementById('st-promo').textContent = '—';
  }
}