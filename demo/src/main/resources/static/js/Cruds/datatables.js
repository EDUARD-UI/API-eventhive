// datatables.js - Versión corregida sin CORS
function initDataTables() {
    $('table.display').not('.dataTable').each(function() {
        $(this).DataTable({
            language: {
                "decimal": "",
                "emptyTable": "No hay datos disponibles en la tabla",
                "info": "Mostrando _START_ a _END_ de _TOTAL_ registros",
                "infoEmpty": "Mostrando 0 a 0 de 0 registros",
                "infoFiltered": "(filtrado de _MAX_ registros totales)",
                "infoPostFix": "",
                "thousands": ",",
                "lengthMenu": "Mostrar _MENU_ registros",
                "loadingRecords": "Cargando...",
                "processing": "Procesando...",
                "search": "Buscar:",
                "zeroRecords": "No se encontraron registros coincidentes",
                "paginate": {
                    "first": "Primero",
                    "last": "Último",
                    "next": "Siguiente",
                    "previous": "Anterior"
                },
                "aria": {
                    "sortAscending": ": activar para ordenar ascendente",
                    "sortDescending": ": activar para ordenar descendente"
                }
            },
            pageLength: 10,
            lengthMenu: [5, 10, 25, 50],
            responsive: true,
            dom: '<"top"lf>rt<"bottom"ip><"clear">'
        });
    });
}

// Inicializar al cargar
$(document).ready(function() {
    setTimeout(initDataTables, 100);
});

// Reinicializar al cambiar de sección
$(document).on('click', '.sidebar-link', function() {
    setTimeout(initDataTables, 500);
});

// Función para destruir y recrear DataTables si es necesario
function reloadDataTables() {
    $('table.display').each(function() {
        if ($.fn.DataTable.isDataTable(this)) {
            $(this).DataTable().destroy();
            $(this).empty();
        }
    });
    initDataTables();
}