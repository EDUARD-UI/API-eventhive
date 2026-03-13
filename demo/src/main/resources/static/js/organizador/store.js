/* ════════════════════════════════════════
   EventHive — store.js
   Estado global compartido entre módulos.
════════════════════════════════════════ */

const Store = {
  usuario:     null,
  eventos:     [],
  localidades: [],
  promociones: [],
  categorias:  [],
  estados:     [],

  /* filtros activos */
  filtros: {
    eventos:     { q: '', categoria: 'all' },
    localidades: { q: '' },
    promociones: { q: '' },
  },

  /* vista activa por sección (grid | table) */
  vistas: {
    eventos:     'grid',
    localidades: 'grid',
    promociones: 'grid',
  },

  /* helpers */
  setEventos(list)     { this.eventos     = list; },
  setLocalidades(list) { this.localidades = list; },
  setPromociones(list) { this.promociones = list; },
  setCategorias(list)  { this.categorias  = list; },
  setEstados(list)     { this.estados     = list; },
  setUsuario(u)        { this.usuario     = u;    },

  addPromocion(p)      { this.promociones.push(p); },
  removePromocion(id)  { this.promociones = this.promociones.filter(p => p.id != id); },
  removeEvento(id)     { this.eventos     = this.eventos.filter(e => e.id != id); },
  removeLocalidad(id)  { this.localidades = this.localidades.filter(l => l.id != id); },
};
