/* ════════════════════════════════════════
   EventHive — api.js
   Centraliza todas las llamadas HTTP.
   Cada función retorna la promesa directa
   o lanza un Error con el mensaje del backend.
════════════════════════════════════════ */

const BASE = '';   // mismo origen; cambiar si el backend está en otro host

/**
 * Fetch base con credenciales de sesión.
 * @param {string} url
 * @param {RequestInit} opts
 * @returns {Promise<any>} data del JSON de respuesta
 */
async function apiFetch(url, opts = {}) {
  const res = await fetch(BASE + url, {
    credentials: 'include',
    ...opts,
  });

  const json = await res.json().catch(() => ({}));

  if (!res.ok) {
    throw new Error(json.mensaje || `Error ${res.status}`);
  }

  return json;
}

const toParams  = (obj) => new URLSearchParams(obj);

/* ─────────────────────────────────────────
   AUTH
───────────────────────────────────────── */
const AuthAPI = {
  login: (correo, clave) =>
    apiFetch('/api/auth/login', { method: 'POST', body: toParams({ correo, clave }) }),

  logout: () =>
    apiFetch('/api/auth/logout', { method: 'POST', body: toParams({}) }),

  registrarCliente: (data) =>
    apiFetch('/api/auth/registrar-cliente', { method: 'POST', body: toParams(data) }),

  registrarOrganizador: (data) =>
    apiFetch('/api/auth/registrar-organizador', { method: 'POST', body: toParams(data) }),
};

/* ─────────────────────────────────────────
   SESIÓN  (GET /api/pagos devuelve el usuario logueado)
───────────────────────────────────────── */
const SesionAPI = {
  obtener: () => apiFetch('/api/pagos'),
};

/* ─────────────────────────────────────────
   USUARIOS / PERFIL
───────────────────────────────────────── */
const UsuariosAPI = {
  listar:  ()   => apiFetch('/api/usuarios'),
  obtener: (id) => apiFetch(`/api/usuarios/${id}`),

  crear: (data) =>
    apiFetch('/api/usuarios', { method: 'POST', body: toParams(data) }),

  actualizar: (id, data) =>
    apiFetch(`/api/usuarios/${id}`, { method: 'PUT', body: toParams(data) }),

  /** Actualiza únicamente los datos del perfil del usuario en sesión */
  actualizarPerfil: (data) =>
    apiFetch('/api/usuarios/perfil', { method: 'PUT', body: toParams(data) }),

  eliminar: (id) =>
    apiFetch(`/api/usuarios/${id}`, { method: 'DELETE' }),
};

/* ─────────────────────────────────────────
   CATEGORÍAS
───────────────────────────────────────── */
const CategoriasAPI = {
  listar:       ()   => apiFetch('/api/categorias'),
  destacadas:   ()   => apiFetch('/api/categorias/destacadas'),
  conEventos:   ()   => apiFetch('/api/categorias/con-eventos'),
  obtener:      (id) => apiFetch(`/api/categorias/${id}`),

  crear: (formData) =>
    apiFetch('/api/categorias', { method: 'POST', body: formData }),

  actualizar: (id, formData) =>
    apiFetch(`/api/categorias/${id}`, { method: 'PUT', body: formData }),

  eliminar: (id) =>
    apiFetch(`/api/categorias/${id}`, { method: 'DELETE' }),
};

/* ─────────────────────────────────────────
   ESTADOS
───────────────────────────────────────── */
const EstadosAPI = {
  listar:   ()   => apiFetch('/api/estados'),
  obtener:  (id) => apiFetch(`/api/estados/${id}`),

  crear: (data) =>
    apiFetch('/api/estados', { method: 'POST', body: toParams(data) }),

  actualizar: (id, data) =>
    apiFetch(`/api/estados/${id}`, { method: 'PUT', body: toParams(data) }),

  eliminar: (id) =>
    apiFetch(`/api/estados/${id}`, { method: 'DELETE' }),
};

/* ─────────────────────────────────────────
   ROLES
───────────────────────────────────────── */
const RolesAPI = {
  listar:   ()   => apiFetch('/api/roles'),
  crear:    (d)  => apiFetch('/api/roles', { method: 'POST', body: toParams(d) }),
  actualizar:(id,d)=>apiFetch(`/api/roles/${id}`,{method:'PUT',body:toParams(d)}),
  eliminar: (id) => apiFetch(`/api/roles/${id}`, { method: 'DELETE' }),
};

/* ─────────────────────────────────────────
   EVENTOS
───────────────────────────────────────── */
const EventosAPI = {
  listar:      ()     => apiFetch('/api/eventos'),
  obtener:     (id)   => apiFetch(`/api/eventos/${id}`),
  buscar:      (titulo) => apiFetch(`/api/eventos/buscar?titulo=${encodeURIComponent(titulo)}`),
  destacados:  ()     => apiFetch('/api/eventos/destacados'),

  /** formData debe incluir: titulo, descripcion, lugar, fecha, hora, categoriaId, estadoId y opcionalmente foto (File) */
  crear: (formData) =>
    apiFetch('/api/eventos', { method: 'POST', body: formData }),

  actualizar: (id, formData) =>
    apiFetch(`/api/eventos/${id}`, { method: 'PUT', body: formData }),

  eliminar: (id) =>
    apiFetch(`/api/eventos/${id}`, { method: 'DELETE' }),
};

/* ─────────────────────────────────────────
   LOCALIDADES
───────────────────────────────────────── */
const LocalidadesAPI = {
  listar:         ()        => apiFetch('/api/localidades'),
  listarPorEvento:(eventoId)=> apiFetch(`/api/localidades/evento/${eventoId}`),

  crear: (data) =>
    apiFetch('/api/localidades', { method: 'POST', body: toParams(data) }),

  actualizar: (id, data) =>
    apiFetch(`/api/localidades/${id}`, { method: 'PUT', body: toParams(data) }),

  eliminar: (id) =>
    apiFetch(`/api/localidades/${id}`, { method: 'DELETE' }),
};

/* ─────────────────────────────────────────
   PROMOCIONES
   (los endpoints aún no existen en el backend,
    se declaran para cuando sean implementados)
───────────────────────────────────────── */
const PromocionesAPI = {
  listarPorEvento: (eventoId) =>
    apiFetch(`/api/eventos/${eventoId}/promociones`),

  crear: (eventoId, data) =>
    apiFetch(`/api/eventos/${eventoId}/promociones`, { method: 'POST', body: toParams(data) }),

  eliminar: (id) =>
    apiFetch(`/api/promociones/${id}`, { method: 'DELETE' }),
};

/* ─────────────────────────────────────────
   COMPRAS
───────────────────────────────────────── */
const ComprasAPI = {
  procesar: (localidadId, cantidad, metodoPago) =>
    apiFetch('/api/compras', { method: 'POST', body: toParams({ localidadId, cantidad, metodoPago }) }),

  historial:  ()        => apiFetch('/api/compras/historial'),
  obtener:    (id)      => apiFetch(`/api/compras/${id}`),
};

/* ─────────────────────────────────────────
   BOLETOS
───────────────────────────────────────── */
const BoletosAPI = {
  obtener: (compraId) => apiFetch(`/api/boletos/${compraId}`),
};

/* ─────────────────────────────────────────
   EVENTOS DESEADOS
───────────────────────────────────────── */
const EventosDeseadosAPI = {
  listar:   ()        => apiFetch('/api/eventos-deseados'),
  agregar:  (eventoId)=> apiFetch('/api/eventos-deseados', { method: 'POST', body: toParams({ eventoId }) }),
  eliminar: (id)      => apiFetch(`/api/eventos-deseados/${id}`, { method: 'DELETE' }),
};

/* ─────────────────────────────────────────
   VALORACIONES
───────────────────────────────────────── */
const ValoracionesAPI = {
  porUsuario:  ()        => apiFetch('/api/valoraciones/usuario'),
  porEvento:   (eventoId)=> apiFetch(`/api/valoraciones/evento/${eventoId}`),

  crear: (data) =>
    apiFetch('/api/valoraciones', { method: 'POST', body: toParams(data) }),

  actualizar: (id, data) =>
    apiFetch(`/api/valoraciones/${id}`, { method: 'PUT', body: toParams(data) }),

  eliminar: (id) =>
    apiFetch(`/api/valoraciones/${id}`, { method: 'DELETE' }),
};

/* ─────────────────────────────────────────
   REPORTES
───────────────────────────────────────── */
const ReportesAPI = {
  eventosPorCategoria: () => apiFetch('/api/reportes/eventos-por-categoria'),
};
