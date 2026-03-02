// Crear o Actualizar Evento
document.querySelector('#modal-evento form').addEventListener('submit', async function(event) {
    event.preventDefault();
    
    const eventoId = document.getElementById('evento-id').value;
    const isEdit = eventoId && eventoId !== '';
    
    const formData = new FormData();
    if (isEdit) {
        formData.append('id', eventoId);
    }
    
    formData.append('titulo', document.getElementById('evento-titulo').value.trim());
    formData.append('descripcion', document.getElementById('evento-descripcion').value.trim());
    formData.append('lugar', document.getElementById('evento-lugar').value.trim());
    formData.append('fecha', document.getElementById('evento-fecha').value);
    formData.append('hora', document.getElementById('evento-hora').value);
    formData.append('categoriaId', document.getElementById('evento-categoria').value);
    formData.append('estadoId', document.getElementById('evento-estado').value);
    
    // Agregar foto si existe
    const fotoInput = document.getElementById('evento-foto');
    if (fotoInput && fotoInput.files.length > 0) {
        formData.append('foto', fotoInput.files[0]);
    }

    try {
        const url = isEdit ? '/eventos/actualizar' : '/eventos/crear';
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
                document.getElementById('modal-evento').classList.remove('show');
                // Mantener la sección activa
                const currentSection = document.querySelector('.content-section.active')?.id || 'seccion-eventos';
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

// Editar Evento
document.addEventListener('click', function(e) {
    const editButton = e.target.closest('.btn-secondary[data-action="edit"]');
    if (editButton && editButton.closest('#seccion-eventos')) {
        const row = editButton.closest('tr');
        const cells = row.querySelectorAll('td');
        
        console.log('Celdas encontradas:', cells.length);
        
        // Llenar el modal con los datos actuales
        document.getElementById('evento-id').value = cells[0].textContent.trim();
        document.getElementById('evento-titulo').value = cells[1].textContent.trim();
        document.getElementById('evento-descripcion').value = cells[2].textContent.trim();
        
        // Saltamos cells[3] que es el Organizador (NO se carga al modal)
        
        document.getElementById('evento-lugar').value = cells[4].textContent.trim();
        
        // Formato de fecha: convertir de "dd/MM/yyyy" a "yyyy-MM-dd"
        const fechaTexto = cells[5].textContent.trim();
        if (fechaTexto) {
            const [dia, mes, anio] = fechaTexto.split('/');
            document.getElementById('evento-fecha').value = `${anio}-${mes.padStart(2, '0')}-${dia.padStart(2, '0')}`;
        }
        
        document.getElementById('evento-hora').value = cells[6].textContent.trim();
        
        // Seleccionar la categoría correspondiente
        const categoriaTexto = cells[7].textContent.trim();
        const categoriaSelect = document.getElementById('evento-categoria');
        for (let option of categoriaSelect.options) {
            if (option.text === categoriaTexto) {
                option.selected = true;
                break;
            }
        }
        
        // Seleccionar el estado correspondiente
        const estadoTexto = cells[8].textContent.trim();
        const estadoSelect = document.getElementById('evento-estado');
        for (let option of estadoSelect.options) {
            if (option.text === estadoTexto) {
                option.selected = true;
                break;
            }
        }
        
        // Cambiar título del modal
        document.querySelector('#modal-evento .modal-title, #modal-evento h2').textContent = 'Editar Evento';
        console.log('Modal llenado con datos del evento');
    }
});

// Eliminar Evento
document.addEventListener('click', function(e) {
    const deleteButton = e.target.closest('.btn-delete');
    if (deleteButton && deleteButton.closest('#seccion-eventos')) {
        e.preventDefault();
        e.stopPropagation();
        
        const row = deleteButton.closest('tr');
        const cells = row.querySelectorAll('td');
        const eventoId = cells[0].textContent.trim();
        const eventoTitulo = cells[1].textContent.trim();
        
        Swal.fire({
            title: '¿Estás seguro?',
            text: `¿Deseas eliminar el evento "${eventoTitulo}"?`,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#3085d6',
            confirmButtonText: 'Sí, eliminar',
            cancelButtonText: 'Cancelar'
        }).then(async (result) => {
            if (result.isConfirmed) {
                try {
                    const response = await fetch(`/eventos/eliminar/${eventoId}`, {
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
                            sessionStorage.setItem('activeSection', 'seccion-eventos');
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

// Limpiar modal al abrirlo para crear nuevo evento
document.addEventListener('click', function(e) {
    const newButton = e.target.closest('[data-action="new-evento"]');
    if (newButton) {
        document.getElementById('evento-id').value = '';
        document.querySelector('#modal-evento form').reset();
        document.querySelector('#modal-evento .modal-title, #modal-evento h2').textContent = 'Crear Nuevo Evento';
    }
});