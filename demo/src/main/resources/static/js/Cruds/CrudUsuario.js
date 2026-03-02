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

// Crear Usuario
document.getElementById('form-usuario').addEventListener('submit', async function(event) {
    event.preventDefault();
    
    const usuarioId = document.getElementById('usuario-id').value;
    const isEdit = usuarioId && usuarioId !== '';
    
    const nombre = document.getElementById('usuario-nombre').value.trim();
    const apellido = document.getElementById('usuario-apellido').value.trim();
    const correo = document.getElementById('usuario-correo').value.trim();
    const telefono = document.getElementById('usuario-telefono').value.trim();
    const clave = document.getElementById('usuario-clave').value.trim();

    // Validar nombre
    if (!validarTexto(nombre)) {
        Swal.fire({
            icon: 'error',
            title: 'Nombre inválido',
            text: 'El nombre solo puede contener letras y espacios'
        });
        return;
    }

    if (nombre.length < 2 || nombre.length > 50) {
        Swal.fire({
            icon: 'error',
            title: 'Nombre inválido',
            text: 'El nombre debe tener entre 2 y 50 caracteres'
        });
        return;
    }

    // Validar apellido
    if (!validarTexto(apellido)) {
        Swal.fire({
            icon: 'error',
            title: 'Apellido inválido',
            text: 'El apellido solo puede contener letras y espacios'
        });
        return;
    }

    if (apellido.length < 2 || apellido.length > 50) {
        Swal.fire({
            icon: 'error',
            title: 'Apellido inválido',
            text: 'El apellido debe tener entre 2 y 50 caracteres'
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
    if (clave && !validarClave(clave)) {
        Swal.fire({
            icon: 'error',
            title: 'Contraseña inválida',
            text: 'La contraseña debe tener al menos 6 caracteres'
        });
        return;
    }

    const formData = new FormData();
    if (isEdit) {
        formData.append('id', usuarioId);
    }
    formData.append('nombre', nombre);
    formData.append('apellido', apellido);
    formData.append('correo', correo);
    formData.append('telefono', telefono);
    
    if (clave) {
        formData.append('clave', clave);
    }
    
    formData.append('rol', document.getElementById('usuario-rol').value);
    formData.append('estado', document.getElementById('usuario-estado').value);

    try {
        const url = isEdit ? '/usuarios/actualizar' : '/usuarios/crear';
        const response = await fetch(url, {
            method: 'POST',
            body: formData
        });

        const data = await response.json();

        if (data.success) {
            Swal.fire({
                icon: 'success',
                title: '¡Éxito!',
                text: data.mensaje,
                timer: 2000,
                showConfirmButton: false
            }).then(() => {
                document.getElementById('modal-usuario').classList.remove('show');
                // Mantener la sección activa antes de recargar
                const currentSection = document.querySelector('.content-section.active')?.id || 'seccion-usuarios';
                sessionStorage.setItem('activeSection', currentSection);
                location.reload();
            });
        } else {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: data.mensaje
            });
        }
    } catch (error) {
        Swal.fire({
            icon: 'error',
            title: 'Error de conexión',
            text: 'No se pudo conectar con el servidor'
        });
    }
});

// Editar Usuario
document.addEventListener('click', function(e) {
    const editButton = e.target.closest('.btn-secondary[data-action="edit"]');
    if (editButton && editButton.closest('#seccion-usuarios')) {
        e.preventDefault();
        e.stopPropagation();
        
        const row = editButton.closest('tr');
        const cells = row.querySelectorAll('td');
        document.getElementById('usuario-id').value = cells[0].textContent.trim();
        document.getElementById('usuario-nombre').value = cells[1].textContent.trim();
        document.getElementById('usuario-apellido').value = cells[2].textContent.trim();
        document.getElementById('usuario-correo').value = cells[3].textContent.trim();
        document.getElementById('usuario-telefono').value = cells[4].textContent.trim();
        
        // Buscar y seleccionar el rol por nombre
        const rolNombre = cells[5].textContent.trim();
        const rolSelect = document.getElementById('usuario-rol');
        for (let option of rolSelect.options) {
            if (option.text === rolNombre) {
                option.selected = true;
                break;
            }
        }
        
        // Buscar y seleccionar el estado por nombre
        const estadoNombre = cells[6].textContent.trim();
        const estadoSelect = document.getElementById('usuario-estado');
        for (let option of estadoSelect.options) {
            if (option.text === estadoNombre) {
                option.selected = true;
                break;
            }
        }
        
        // La contraseña se deja vacía por seguridad
        document.getElementById('usuario-clave').value = '';
        document.getElementById('usuario-clave').removeAttribute('required');
    }
});

// Eliminar Usuario
document.addEventListener('click', function(e) {
    const deleteButton = e.target.closest('.btn-delete');
    if (deleteButton && deleteButton.closest('#seccion-usuarios')) {
        e.preventDefault();
        e.stopPropagation();
        
        const row = deleteButton.closest('tr');
        const cells = row.querySelectorAll('td');
        const usuarioId = cells[0].textContent.trim();
        const usuarioNombre = cells[1].textContent.trim() + ' ' + cells[2].textContent.trim();
        
        Swal.fire({
            title: '¿Estás seguro?',
            text: `¿Deseas eliminar al usuario ${usuarioNombre}?`,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#3085d6',
            confirmButtonText: 'Sí, eliminar',
            cancelButtonText: 'Cancelar'
        }).then(async (result) => {
            if (result.isConfirmed) {
                try {
                    const response = await fetch(`/usuarios/eliminar/${usuarioId}`, {
                        method: 'DELETE'
                    });
                    
                    const data = await response.json();
                    
                    if (data.success) {
                        Swal.fire({
                            icon: 'success',
                            title: 'Eliminado',
                            text: data.mensaje,
                            timer: 2000,
                            showConfirmButton: false
                        }).then(() => {
                            // Mantener la sección activa antes de recargar
                            sessionStorage.setItem('activeSection', 'seccion-usuarios');
                            location.reload();
                        });
                    } else {
                        Swal.fire({
                            icon: 'error',
                            title: 'Error',
                            text: data.mensaje
                        });
                    }
                } catch (error) {
                    Swal.fire({
                        icon: 'error',
                        title: 'Error de conexión',
                        text: 'No se pudo conectar con el servidor'
                    });
                }
            }
        });
    }
});

// Validación en tiempo real para nombre
document.getElementById('usuario-nombre').addEventListener('input', function(e) {
    this.value = this.value.replace(/[^a-zA-ZáéíóúÁÉÍÓÚñÑ\s]/g, '');
});

// Validación en tiempo real para apellido
document.getElementById('usuario-apellido').addEventListener('input', function(e) {
    this.value = this.value.replace(/[^a-zA-ZáéíóúÁÉÍÓÚñÑ\s]/g, '');
});

// Validación en tiempo real para teléfono
document.getElementById('usuario-telefono').addEventListener('input', function(e) {
    this.value = this.value.replace(/[^\d]/g, '');
    if (this.value.length > 15) {
        this.value = this.value.slice(0, 15);
    }
});