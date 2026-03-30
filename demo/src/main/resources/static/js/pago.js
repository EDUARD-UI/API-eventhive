// Obtener parámetros de la URL
const urlParams = new URLSearchParams(window.location.search);
const eventoNombre = urlParams.get("evento");
const localidadNombre = urlParams.get("localidad");
const localidadId = urlParams.get("localidadId");
const precioParam = urlParams.get("precio");
const precio = precioParam !== null ? parseFloat(precioParam) : null;
const cantidad = parseInt(urlParams.get("cantidad"));

//Verificar que todos los datos estén presentes
if (
  !eventoNombre ||
  !localidadId ||
  precio === null ||
  isNaN(precio) ||
  !cantidad
) {
  console.error("Datos incompletos:", {
    eventoNombre,
    localidadId,
    precio,
    cantidad,
  });
  Swal.fire({
    icon: "error",
    title: "Error",
    text: "Datos de compra incompletos",
    confirmButtonColor: "#ff4757",
  }).then(() => {
    window.history.back();
  });
} else {
  // Mostrar datos en el resumen
  document.getElementById("nombreEvento").textContent = eventoNombre;
  document.getElementById("nombreLocalidad").textContent = localidadNombre;

  // Si el evento es gratuito
  if (precio === 0) {
    document.getElementById("precioUnitario").textContent = "GRATIS";
    document.getElementById("total").textContent = "GRATIS";

    // Ocultar campos de tarjeta para eventos gratis y quitar required
    const paymentFormSection = document.querySelector(".payment-form-section");
    const cardFields = paymentFormSection.querySelectorAll(".input-group");
    cardFields.forEach((field) => {
      field.style.display = "none";
    });

    // Quitar atributo required de los campos ocultos - INCLUYENDO card-name
    const requiredFields = document.querySelectorAll(
      "#card-name, #card-number, #expiry-date, #cvv"
    );
    requiredFields.forEach((field) => {
      field.removeAttribute("required");
    });

    // Cambiar texto del botón
    const btnPagar = document.querySelector(".btn-pagar");
    btnPagar.textContent = "Confirmar Reserva Gratuita";
  } else {
    document.getElementById(
      "precioUnitario"
    ).textContent = `$${precio.toLocaleString()} COP`;
    document.getElementById("total").textContent = `$${(
      precio * cantidad
    ).toLocaleString()} COP`;
  }

  document.getElementById("cantidadTickets").textContent = cantidad;
  document.getElementById("localidadId").value = localidadId;
  document.getElementById("cantidad").value = cantidad;
}

// Formatear número de tarjeta
document.getElementById("card-number").addEventListener("input", function (e) {
  let value = e.target.value.replace(/\s/g, "");
  value = value.replace(/\D/g, "");
  value = value.substring(0, 16);
  let formattedValue = value.match(/.{1,4}/g)?.join(" ") || value;
  e.target.value = formattedValue;
});

// Formatear fecha de vencimiento
document.getElementById("expiry-date").addEventListener("input", function (e) {
  let value = e.target.value.replace(/\D/g, "");
  if (value.length >= 2) {
    value = value.substring(0, 2) + "/" + value.substring(2, 4);
  }
  e.target.value = value;
});

// Formatear CVV (solo números)
document.getElementById("cvv").addEventListener("input", function (e) {
  e.target.value = e.target.value.replace(/\D/g, "");
});

// Procesar pago
document
  .getElementById("paymentForm")
  .addEventListener("submit", async function (e) {
    e.preventDefault();

    // Si el evento es gratuito, no validar campos de tarjeta
    if (precio === 0) {
      procesarCompraGratuita();
      return;
    }

    const cardNumber = document
      .getElementById("card-number")
      .value.replace(/\s/g, "");
    const expiryDate = document.getElementById("expiry-date").value;
    const cvv = document.getElementById("cvv").value;

    // Validaciones básicas
    if (cardNumber.length !== 16) {
      Swal.fire({
        icon: "error",
        title: "Error",
        text: "Número de tarjeta inválido (debe tener 16 dígitos)",
        confirmButtonColor: "#ff4757",
      });
      return;
    }

    if (!/^\d{2}\/\d{2}$/.test(expiryDate)) {
      Swal.fire({
        icon: "error",
        title: "Error",
        text: "Fecha de vencimiento inválida (formato MM/AA)",
        confirmButtonColor: "#ff4757",
      });
      return;
    }

    if (cvv.length < 3 || cvv.length > 4) {
      Swal.fire({
        icon: "error",
        title: "Error",
        text: "CVV inválido (debe tener 3 o 4 dígitos)",
        confirmButtonColor: "#ff4757",
      });
      return;
    }

    procesarCompra("Tarjeta de Crédito");
  });

// Función para procesar compra gratuita
async function procesarCompraGratuita() {
    // Validar que tenemos todos los datos necesarios
    const localidadId = document.getElementById('localidadId').value;
    const cantidad = document.getElementById('cantidad').value;
    
    if (!localidadId || !cantidad) {
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: 'Datos incompletos para la reserva',
            confirmButtonColor: '#ff4757'
        });
        return;
    }
    
    // Validar que sea realmente gratuito
    if (precio !== 0) {
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: 'Este evento no es gratuito',
            confirmButtonColor: '#ff4757'
        });
        return;
    }
    
    await procesarCompra('Gratuito');
}

// Función para procesar la compra
async function procesarCompra(metodoPago) {
  // Mostrar loading
  Swal.fire({
    title: precio === 0 ? "Procesando reserva..." : "Procesando pago...",
    html: "Por favor espere mientras procesamos su transacción",
    allowOutsideClick: false,
    didOpen: () => {
      Swal.showLoading();
    },
  });

  try {
    const response = await fetch("/compras/procesar", {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: new URLSearchParams({
        localidadId: document.getElementById("localidadId").value,
        cantidad: document.getElementById("cantidad").value,
        metodoPago: metodoPago,
      }),
    });

    const result = await response.json();

    if (result.success) {
      Swal.fire({
        icon: "success",
        title: precio === 0 ? "¡Reserva Exitosa!" : "¡Pago Exitoso!",
        html: `
                    <p>Tu ${
                      precio === 0 ? "reserva" : "compra"
                    } se ha realizado correctamente</p>
                    <p><strong>Código de ${
                      precio === 0 ? "reserva" : "compra"
                    }:</strong> #${result.compraId}</p>
                    <p>Recibirás tus boletos con código QR</p>
                `,
        confirmButtonText: "Ver mis boletos",
        confirmButtonColor: "#10ac84",
        showCancelButton: true,
        cancelButtonText: "Ir al inicio",
        cancelButtonColor: "#6c5ce7",
      }).then((result) => {
        if (result.isConfirmed) {
          window.location.href = "/pages/historialCompras.html";
        } else {
          window.location.href = "/";
        }
      });
    } else {
      Swal.fire({
        icon: "error",
        title: precio === 0 ? "Error en la reserva" : "Error en el pago",
        text: result.mensaje,
        confirmButtonColor: "#ff4757",
      });
    }
  } catch (error) {
    console.error("Error:", error);
    Swal.fire({
      icon: "error",
      title: "Error",
      text:
        precio === 0
          ? "Hubo un problema al procesar la reserva. Por favor intente nuevamente."
          : "Hubo un problema al procesar el pago. Por favor intente nuevamente.",
      confirmButtonColor: "#ff4757",
    });
  }
}