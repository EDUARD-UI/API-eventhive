// Validaciones
function validarTexto(texto) {
    return /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/.test(texto);
}

function validarTelefono(telefono) {
    return /^\d{7,15}$/.test(telefono);
}

function validarEmail(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function validarClave(clave) {
    return clave.length >= 6;
}

document.getElementById('editProfileForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const nombre = document.getElementById('nombre').value.trim();
    const correo = document.getElementById('correo').value.trim();
    const telefono = document.getElementById('telefono').value.trim();
    const password = document.getElementById('password').value;

    // Validar nombre
    if (!nombre || nombre.length < 2 || nombre.length > 100) {
        Swal.fire({
            icon: 'error',
            title: 'Nombre inválido',
            text: 'El nombre debe tener entre 2 y 100 caracteres'
        });
        return;
    }

    if (!validarTexto(nombre)) {
        Swal.fire({
            icon: 'error',
            title: 'Nombre inválido',
            text: 'El nombre solo puede contener letras y espacios'
        });
        return;
    }

    // Validar email
    if (!validarEmail(correo)) {
        Swal.fire({
            icon: 'error',
            title: 'Correo inválido',
            text: 'Por favor ingresa un correo electrónico válido'
        });
        return;
    }

    // Validar teléfono
    if (!validarTelefono(telefono)) {
        Swal.fire({
            icon: 'error',
            title: 'Teléfono inválido',
            text: 'El teléfono debe contener solo números (7-15 dígitos)'
        });
        return;
    }

    // Validar contraseña si se proporcionó
    if (password && !validarClave(password)) {
        Swal.fire({
            icon: 'error',
            title: 'Contraseña inválida',
            text: 'La contraseña debe tener al menos 6 caracteres'
        });
        return;
    }

    // Confirmación antes de enviar
    const confirmResult = await Swal.fire({
        title: '¿Guardar cambios?',
        text: "¿Estás seguro de que quieres actualizar tu perfil?",
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sí, guardar',
        cancelButtonText: 'Cancelar'
    });

    if (!confirmResult.isConfirmed) {
        return;
    }

    const formData = new FormData();
    formData.append('nombre', nombre);
    formData.append('correo', correo);
    formData.append('telefono', telefono);
    formData.append('password', password);

    try {
        const response = await fetch('/editarPerfil', {
            method: 'POST',
            body: formData
        });

        const result = await response.json();

        if (result.success) {
            await Swal.fire({
                icon: 'success',
                title: '¡Éxito!',
                text: result.message,
                confirmButtonText: 'Aceptar'
            });
            window.location.href = '/perfil';
        } else {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: result.message,
                confirmButtonText: 'Aceptar'
            });
        }
    } catch (error) {
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: 'Error de conexión',
            confirmButtonText: 'Aceptar'
        });
    }
});

// Validación en tiempo real para nombre
document.getElementById('nombre').addEventListener('input', function(e) {
    this.value = this.value.replace(/[^a-zA-ZáéíóúÁÉÍÓÚñÑ\s]/g, '');
});

// Validación en tiempo real para teléfono
document.getElementById('telefono').addEventListener('input', function(e) {
    this.value = this.value.replace(/[^\d]/g, '');
    if (this.value.length > 15) {
        this.value = this.value.slice(0, 15);
    }
});