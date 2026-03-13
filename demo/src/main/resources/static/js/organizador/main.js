/* ════════════════════════════════════════
   EventHive — main.js
   Punto de entrada. Inicializa la app.
════════════════════════════════════════ */

document.addEventListener('DOMContentLoaded', async () => {

  /* 1 — Setup de UI base */
  initSidebar();
  initModalOverlays();

  /* 2 — Cargar datos base en paralelo */
  try {
    const [sesionRes, catsRes, estadosRes, promosRes] = await Promise.allSettled([
      SesionAPI.obtener(),
      CategoriasAPI.listar(),
      EstadosAPI.listar(),
      loadPromociones(),
    ]);

    if (sesionRes.status === 'fulfilled' && sesionRes.value?.data) {
      Store.setUsuario(sesionRes.value.data);
      updateSidebarUser(Store.usuario);
      updateHeroUser(Store.usuario);
      updateProfileCard(Store.usuario);
    }

    if (catsRes.status === 'fulfilled') {
      Store.setCategorias(catsRes.value?.data || []);
    }

    if (estadosRes.status === 'fulfilled') {
      Store.setEstados(estadosRes.value?.data || []);
    }

  } catch (err) {
    console.warn('Error cargando datos base:', err);
  }

  /* 3 — Cargar eventos + localidades */
  await loadEventosYLocalidades();

  /* 4 — Navegar al dashboard (carga el HTML e inicializa) */
  navigate('dashboard');
});

/* ─── Carga eventos → luego localidades por cada evento ─── */
async function loadEventosYLocalidades() {
  try {
    const res = await EventosAPI.listar();
    Store.setEventos(res.data || []);
  } catch (err) {
    console.warn('No se pudieron cargar los eventos:', err);
  }

  try {
    const results = await Promise.allSettled(
      Store.eventos.map(ev => LocalidadesAPI.listarPorEvento(ev.id))
    );

    Store.setLocalidades(
      results.flatMap((r, i) =>
        r.status === 'fulfilled'
          ? (r.value?.data || []).map(l => ({
              ...l,
              eventoTitulo: Store.eventos[i]?.titulo || '—',
            }))
          : []
      )
    );
  } catch (err) {
    console.warn('No se pudieron cargar las localidades:', err);
  }
}

/* ─── Carga promociones (local por ahora) ─── */
async function loadPromociones() {
  /* No hay endpoint de backend aún, devolver array vacío */
  return { data: [] };
}

