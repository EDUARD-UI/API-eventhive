// Crear/Actualizar Localidad
document.querySelector('#modal-localidad form').addEventListener('submit', async function(event) {
    event.preventDefault();
    
    const localidadId = document.getElementById('localidad-id').value;
    const isEdit = localidadId && localidadId !== '';
    
    const formData = new FormData();
    if (isEdit) {
        formData.append('id', localidadId);
    }
    formData.append('nombre', document.getElementById('localidad-nombre').value.trim());
    formData.append('precio', document.getElementById('localidad-precio').value.trim());
    formData.append('capacidad', document.getElementById('localidad-capacidad').value.trim());
    formData.append('disponibles', document.getElementById('localidad-disponibles').value.trim());
    formData.append('eventoId', document.getElementById('localidad-evento').value);

    try {
        const url = isEdit ? '/localidades/actualizar' : '/localidades/crear';
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
                document.getElementById('modal-localidad').classList.remove('show');
                // Mantener la sección activa
                const currentSection = document.querySelector('.content-section.active')?.id || 'seccion-localidades';
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

// Editar Localidad
document.addEventListener('click', function(e) {
    const editButton = e.target.closest('.btn-secondary[data-action="edit"]');
    if (editButton && editButton.closest('#seccion-localidades')) {
        const row = editButton.closest('tr');
        const cells = row.querySelectorAll('td');
        
        // Llenar el modal con los datos actuales
        document.getElementById('localidad-id').value = cells[0].textContent.trim();
        document.getElementById('localidad-nombre').value = cells[1].textContent.trim();
        
        // Limpiar el precio de caracteres extra
        const precioText = cells[2].textContent.trim();
        const precio = precioText.replace('$', '').replace('N/A', '');
        document.getElementById('localidad-precio').value = precio;
        
        document.getElementById('localidad-capacidad').value = cells[3].textContent.trim();
        document.getElementById('localidad-disponibles').value = cells[4].textContent.trim();
        
        // Buscar y seleccionar el evento por nombre
        const eventoNombre = cells[5].textContent.trim();
        const eventoSelect = document.getElementById('localidad-evento');
        for (let option of eventoSelect.options) {
            if (option.text === eventoNombre || eventoNombre === 'Sin evento') {
                option.selected = true;
                break;
            }
        }
    }
});

// Eliminar Localidad
document.addEventListener('click', function(e) {
    const deleteButton = e.target.closest('.btn-delete');
    if (deleteButton && deleteButton.closest('#seccion-localidades')) {
        e.preventDefault();
        e.stopPropagation();
        
        const row = deleteButton.closest('tr');
        const localidadId = row.querySelector('td').textContent.trim();
        const localidadNombre = row.querySelectorAll('td')[1].textContent.trim();
        
        Swal.fire({
            title: '¿Estás seguro?',
            text: `¿Deseas eliminar la localidad ${localidadNombre}?`,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#3085d6',
            confirmButtonText: 'Sí, eliminar',
            cancelButtonText: 'Cancelar'
        }).then(async (result) => {
            if (result.isConfirmed) {
                try {
                    const response = await fetch(`/localidades/eliminar/${localidadId}`, {
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
                            // Mantener la sección activa
                            sessionStorage.setItem('activeSection', 'seccion-localidades');
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