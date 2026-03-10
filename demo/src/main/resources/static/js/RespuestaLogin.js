const BASE = "";

document.addEventListener("DOMContentLoaded", () => {
  // obtener posible redirect
  const params = new URLSearchParams(window.location.search);
  const redirect = params.get("redirect");

  // toggle contraseña
  document.getElementById("togglePwd").addEventListener("click", () => {
    const p = document.getElementById("password");
    const i = document.querySelector("#togglePwd i");

    p.type = p.type === "password" ? "text" : "password";
    i.className =
      p.type === "password" ? "far fa-eye text-sm" : "far fa-eye-slash text-sm";
  });

  document.getElementById("btnLogin").addEventListener("click", async () => {
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;

    if (!email || !password) {
      Swal.fire({
        icon: "warning",
        title: "Campos requeridos",
        text: "Ingresa tu correo y contraseña.",
        confirmButtonColor: "#007bff",
      });
      return;
    }

    try {
      const res = await fetch(`${BASE}/api/auth/login`, {
        method: "POST",
        credentials: "include",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
        },
        body: new URLSearchParams({
          correo: email,
          clave: password,
        }),
      });

      const json = await res.json();

      if (res.ok && json.data) {
        await Swal.fire({
          icon: "success",
          title: "¡Bienvenido!",
          text: json.message || "Sesión iniciada.",
          confirmButtonColor: "#007bff",
          timer: 1500,
          showConfirmButton: false,
        });

        // 👇 REDIRECCIÓN INTELIGENTE
        if (redirect) {
          window.location.href = redirect;
        } else {
          window.location.href =
            json.data.rol?.nombre === "organizador"
              ? "/organizador/dashboard"
              : "/index.html";
        }
      } else {
        Swal.fire({
          icon: "error",
          title: "Error",
          text: json.message || "Credenciales incorrectas.",
          confirmButtonColor: "#007bff",
        });
      }
    } catch {
      Swal.fire({
        icon: "error",
        title: "Error de conexión",
        text: "No se pudo conectar al servidor.",
        confirmButtonColor: "#007bff",
      });
    }
  });
});
