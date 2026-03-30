document.addEventListener("DOMContentLoaded", () => {
  checkSession();
  const id = new URLSearchParams(window.location.search).get("id");
  if (id) cargarEvento(id);
  else mostrarNotFound();
});

//verificar la session
async function checkSession() {
  try {
    const res = await fetch("/api/pagos", { credentials: "include" });
    const json = await res.json();
    if (json.data) {
      renderNavAuth(json.data.nombre, json.data.rolNombre || "");
    } else {
      renderNavGuest();
    }
  } catch {
    renderNavGuest();
  }
}

function renderNavAuth(nombre, rol) {
  const el = document.getElementById("navAuth");
  if (!el) return;
  el.innerHTML = `
    <span class="text-dark/50 hidden sm:block text-sm font-medium">${esc(nombre)}</span>
    ${
      rol === "organizador"
        ? `<a href="/pages/organizador/index.html" class="bg-brand text-white text-xs font-bold px-4 py-2 rounded-xl hover:bg-blue-700 transition">Dashboard</a>`
        : `<a href="/pages/perfil.html" class="text-dark/60 hover:text-brand transition text-xs font-medium">Mi Perfil</a>`
    }
    <button id="btnLogout" class="text-dark/40 hover:text-brand text-xs transition font-medium">Salir</button>`;
  document.getElementById("btnLogout").addEventListener("click", async () => {
    await fetch("/api/auth/logout", { method: "POST", credentials: "include" });
    location.reload();
  });
}

function renderNavGuest() {
  const el = document.getElementById("navAuth");
  if (!el) return;
  el.innerHTML = `
    <a href="/pages/login.html" class="text-dark/60 hover:text-brand transition text-sm font-medium">Iniciar sesión</a>
    <a href="/pages/signin.html" class="bg-brand text-white text-xs font-bold px-4 py-2 rounded-xl hover:bg-blue-700 transition shadow-sm">Registrarse</a>`;
}

// funcion para cargar el evento seleccionado
async function cargarEvento(id) {
  try {
    const res = await fetch(`/api/eventos/${id}`, { credentials: "include" });
    if (!res.ok) {
      mostrarNotFound();
      return;
    }
    const json = await res.json();
    const e = json.data;
    if (!e) {
      mostrarNotFound();
      return;
    }
    renderEvento(e);
  } catch {
    mostrarNotFound();
  }
}

function renderEvento(e) {
  document.title = `${e.titulo} — EventHive`;

  // hero background
  if (e.foto) {
    document.getElementById("heroBanner").style.backgroundImage =
      `linear-gradient(to bottom,rgba(0,0,0,.5) 0%,rgba(0,0,0,.78) 100%), url('/uploads/eventos/${e.foto}')`;
  }

  document.getElementById("breadNombre").textContent = e.titulo;
  document.getElementById("heroTitulo").textContent = e.titulo;

  if (e.categoriaNombre) {
    const catEl = document.getElementById("heroCat");
    catEl.classList.remove("hidden");
    catEl.querySelector("span").textContent = e.categoriaNombre;
  }

  const meta = [];
  if (e.fecha)
    meta.push(
      `<span><i class="far fa-calendar text-accent mr-2"></i>${formatFecha(e.fecha)}</span>`,
    );
  if (e.hora)
    meta.push(
      `<span><i class="far fa-clock text-accent mr-2"></i>${formatHora(e.hora)}</span>`,
    );
  if (e.lugar)
    meta.push(
      `<span><i class="fas fa-map-marker-alt text-accent mr-2"></i>${esc(e.lugar)}</span>`,
    );
  if (e.estadoNombre)
    meta.push(
      `<span class="bg-white/15 px-3 py-1 rounded-full text-white/80 text-xs font-bold">${esc(e.estadoNombre)}</span>`,
    );
  document.getElementById("heroMeta").innerHTML = meta.join("");

  // imagen
  if (e.foto) {
    const imgEl = document.getElementById("imgEl");
    imgEl.src = `/uploads/eventos/${e.foto}`;
    imgEl.alt = e.titulo;
    document.getElementById("eventoImg").classList.remove("hidden");
  }

  // descripción
  document.getElementById("eventoDesc").textContent =
    e.descripcion || "Sin descripción disponible.";

  // localidades
  renderLocalidades(e.localidades || [], e.titulo);

  // mostrar contenido
  document.getElementById("skeleton").classList.add("hidden");
  const contenido = document.getElementById("contenido");
  contenido.classList.remove("hidden");
  contenido.classList.add("grid");
}

// ── LOCALIDADES ───────────────────────────────────────────
function renderLocalidades(localidades, eventoNombre) {
  const container = document.getElementById("localidadesContainer");
  const noLocal = document.getElementById("noLocalidades");

  if (!localidades.length) {
    noLocal.classList.remove("hidden");
    return;
  }

  container.innerHTML = localidades
    .map((l) => {
      const agotado = l.disponibles === 0;
      const precio = Number(l.precio || 0).toLocaleString("es-CO");
      const pct = Math.max(
        0,
        Math.min(100, (l.disponibles / l.capacidad) * 100),
      );
      return `
      <div class="localidad-card bg-white rounded-3xl border ${agotado ? "border-gray-100 opacity-60" : "border-gray-200"} p-6">
        <div class="flex items-start justify-between mb-3">
          <div>
            <h3 class="font-extrabold text-dark text-base">${esc(l.nombre)}</h3>
            <p class="text-xs text-dark/40 mt-0.5">${l.disponibles} de ${l.capacidad} disponibles</p>
          </div>
          <span class="text-brand font-extrabold text-lg">$${precio} COP</span>
        </div>
        <div class="w-full bg-gray-100 rounded-full h-1.5 mb-4">
          <div class="bg-brand h-1.5 rounded-full" style="width:${pct}%"></div>
        </div>
        <div class="flex items-center gap-3">
          <div class="flex items-center border border-gray-200 rounded-xl overflow-hidden">
            <button onclick="cambiarCantidad(this,-1)"
              class="px-3 py-2 text-dark/50 hover:bg-gray-50 transition text-sm font-bold">−</button>
            <input type="number" value="1" min="1" max="${Math.min(l.disponibles, 10)}"
              id="cant-${l.id}"
              class="w-10 text-center text-sm font-bold text-dark outline-none bg-white" readonly/>
            <button onclick="cambiarCantidad(this,1)"
              class="px-3 py-2 text-dark/50 hover:bg-gray-50 transition text-sm font-bold">+</button>
          </div>
          <button
            onclick="irAPago(${l.id},'${esc(l.nombre)}',${l.precio},'${esc(eventoNombre)}')"
            ${agotado ? "disabled" : ""}
            class="flex-1 ${
              agotado
                ? "bg-gray-100 text-gray-400 cursor-not-allowed"
                : "bg-brand hover:bg-blue-700 text-white shadow-sm shadow-brand/20"
            } font-bold py-2.5 rounded-xl transition text-sm uppercase tracking-wide">
            ${agotado ? "Agotado" : "Seleccionar"}
          </button>
        </div>
      </div>`;
    })
    .join("");
}

function cambiarCantidad(btn, delta) {
  const input = btn.parentElement.querySelector("input");
  const val = parseInt(input.value) + delta;
  const max = parseInt(input.max);
  if (val >= 1 && val <= max) input.value = val;
}

async function irAPago(localidadId, localidadNombre, precio, eventoNombre) {
  try {
    // Verificar si existe sesión
    const res = await fetch("/api/pagos", { credentials: "include" });
    const json = await res.json();

    // Si no hay sesión
    if (!json.data) {
      const result = await Swal.fire({
        icon: "warning",
        title: "Debes iniciar sesión",
        text: "Necesitas iniciar sesión para comprar boletos.",
        confirmButtonText: "Ir a Login",
        confirmButtonColor: "#007bff",
        showCancelButton: true,
        cancelButtonText: "Cancelar",
        cancelButtonColor: "#6c757d",
      });

      if (result.isConfirmed) {
        // Guardar la página actual para volver después del login
        const redirect = encodeURIComponent(window.location.href);
        window.location.href = `/pages/login.html?redirect=${redirect}`;
      }

      return;
    }
  } catch (error) {
    // Si falla la verificación asumimos que no hay sesión
    window.location.href = "/pages/login.html";
    return;
  }

  // Obtener cantidad seleccionada
  const cantidad = document.getElementById(`cant-${localidadId}`).value;

  //parámetros para la página de pago
  const params = new URLSearchParams({
    evento: eventoNombre,
    localidad: localidadNombre,
    localidadId,
    precio,
    cantidad,
  });
  window.location.href = `/pages/pago.html?${params}`;
}

function mostrarNotFound() {
  document.getElementById("skeleton").classList.add("hidden");
  document.getElementById("notFound").classList.remove("hidden");
}

// funciones de apoyo
function formatFecha(v) {
  if (!v) return "Fecha por confirmar";
  if (Array.isArray(v)) {
    const [y, m, d] = v;
    return new Date(y, m - 1, d).toLocaleDateString("es-ES", {
      day: "numeric",
      month: "long",
      year: "numeric",
    });
  }
  const d = new Date(v);
  return isNaN(d)
    ? String(v)
    : d.toLocaleDateString("es-ES", {
        day: "numeric",
        month: "long",
        year: "numeric",
      });
}
function formatHora(v) {
  if (!v) return "";
  if (Array.isArray(v)) {
    const [h, m] = v;
    return `${String(h).padStart(2, "0")}:${String(m).padStart(2, "0")}`;
  }
  return String(v).slice(0, 5);
}
function esc(s) {
  if (!s) return "";
  return String(s)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;");
}
