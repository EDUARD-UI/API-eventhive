// Crear/Actualizar Promoción
document.querySelector('#modal-promocion form').addEventListener('submit', async function(event) {
    event.preventDefault();
    
    const promocionId = document.getElementById('promocion-id').value;
    const isEdit = promocionId && promocionId !== '';
    
    const formData = new FormData();
    if (isEdit) {
        formData.append('id', promocionId);
    }
    formData.append('eventoId', document.getElementById('promocion-evento').value);
    formData.append('descripcion', document.getElementById('promocion-descripcion').value.trim());
    formData.append('descuento', document.getElementById('promocion-descuento').value.trim());
    formData.append('fechaInicio', document.getElementById('promocion-inicio').value);
    formData.append('fechaFin', document.getElementById('promocion-fin').value);

    try {
        const url = isEdit ? '/promociones/actualizar' : '/promociones/crear';
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
                document.getElementById('modal-promocion').classList.remove('show');
                // Mantener la sección activa
                const currentSection = document.querySelector('.content-section.active')?.id || 'seccion-promociones';
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

// Editar Promoción
document.addEventListener('click', function(e) {
    const editButton = e.target.closest('.btn-secondary[data-action="edit"]');
    if (editButton && editButton.closest('#seccion-promociones')) {
        const row = editButton.closest('tr');
        const cells = row.querySelectorAll('td');
        
        // Llenar el modal con los datos actuales
        document.getElementById('promocion-id').value = cells[0].textContent.trim();
        
        // Buscar y seleccionar el evento por nombre
        const eventoNombre = cells[1].textContent.trim();
        const eventoSelect = document.getElementById('promocion-evento');
        for (let option of eventoSelect.options) {
            if (option.text === eventoNombre) {
                option.selected = true;
                break;
            }
        }
        
        document.getElementById('promocion-descripcion').value = cells[2].textContent.trim();
        
        // Limpiar el descuento del símbolo %
        const descuentoText = cells[3].textContent.trim();
        const descuento = descuentoText.replace('%', '');
        document.getElementById('promocion-descuento').value = descuento;
        
        document.getElementById('promocion-inicio').value = cells[4].textContent.trim();
        document.getElementById('promocion-fin').value = cells[5].textContent.trim();
    }
});

// Eliminar Promoción
document.addEventListener('click', function(e) {
    const deleteButton = e.target.closest('.btn-delete');
    if (deleteButton && deleteButton.closest('#seccion-promociones')) {
        e.preventDefault();
        e.stopPropagation();
        
        const row = deleteButton.closest('tr');
        const promocionId = row.querySelector('td').textContent.trim();
        const eventoNombre = row.querySelectorAll('td')[1].textContent.trim();
        
        Swal.fire({
            title: '¿Estás seguro?',
            text: `¿Deseas eliminar la promoción del evento ${eventoNombre}?`,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#3085d6',
            confirmButtonText: 'Sí, eliminar',
            cancelButtonText: 'Cancelar'
        }).then(async (result) => {
            if (result.isConfirmed) {
                try {
                    const response = await fetch(`/promociones/eliminar/${promocionId}`, {
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
                            sessionStorage.setItem('activeSection', 'seccion-promociones');
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