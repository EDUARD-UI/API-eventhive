# 📋 Documentación API REST - EventHive

**Versión:** 1.0  
**Base URL:** `http://localhost:8082/api`  
**Formato de Datos:** JSON  
**Puerto:** 8082

---

## 🔐 Seguridad y Autenticación

Todos los endpoints están protegidos mediante **Spring Security** con roles basados en autorización.

### Roles Disponibles
- `ADMINISTRADOR` - Acceso total a todas las funcionalidades
- `ORGANIZADOR` - Crear y gestionar eventos y promociones
- `CLIENTE` - Comprar entradas y realizar valoraciones

### Validaciones Generales
- El servidor valida todas las solicitudes entrantes
- Las contraseñas se almacenan encriptadas con BCrypt
- El correo electrónico es único por usuario
- Los IDs usan MongoDB ObjectId (`507f1f77bcf86cd799439011`)

---

## 📦 Estructura de Respuesta Global

Todas las respuestas siguen el siguiente formato:

```json
{
  "success": true,
  "mensaje": "Descripción de la operación",
  "data": {}
}
```

### Códigos HTTP
- `200 OK` - Solicitud exitosa
- `201 CREATED` - Recurso creado exitosamente
- `400 BAD REQUEST` - Solicitud inválida
- `401 UNAUTHORIZED` - No autenticado
- `403 FORBIDDEN` - No autorizado
- `404 NOT FOUND` - Recurso no encontrado
- `500 INTERNAL SERVER ERROR` - Error del servidor

---

## 👥 Endpoints de Usuarios

### 1. Listar Todos los Usuarios
```
GET /usuarios
```
**Autorización:** `ADMINISTRADOR`  
**Parámetros de Query:**
- `page` (integer, default: 0) - Número de página
- `size` (integer, default: 20) - Elementos por página
- `sort` (string, default: "id,desc") - Criterio de ordenamiento

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Usuarios obtenidos",
  "data": {
    "content": [
      {
        "id": "507f1f77bcf86cd799439011",
        "nombre": "Juan",
        "apellido": "García",
        "correo": "juan.garcia@email.com",
        "telefono": "+57 310 555 0123",
        "rolNombre": "CLIENTE"
      }
    ],
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 150,
    "totalPages": 8,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

---

### 2. Obtener Usuario por ID
```
GET /usuarios/{id}
```
**Autorización:** `ADMINISTRADOR`  
**Parámetros de Ruta:**
- `id` (string, requerido) - ID del usuario

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Usuario obtenido",
  "data": {
    "id": "507f1f77bcf86cd799439011",
    "nombre": "Juan",
    "apellido": "García",
    "correo": "juan.garcia@email.com",
    "telefono": "+57 310 555 0123",
    "rolNombre": "CLIENTE"
  }
}
```

---

### 3. Crear Usuario
```
POST /usuarios
```
**Autorización:** `ADMINISTRADOR`  
**Parámetros (form-data):**
- `nombre` (string, requerido, max: 50) - Nombre del usuario
- `apellido` (string, requerido, max: 50) - Apellido del usuario
- `correo` (string, requerido, unique) - Email válido
- `telefono` (string, requerido, max: 20) - Teléfono con formato internacional
- `clave` (string, requerido, min: 8) - Contraseña encriptada con BCrypt
- `rolId` (string, requerido) - ID del rol a asignar

**Respuesta (201 CREATED):**
```json
{
  "success": true,
  "mensaje": "Usuario creado exitosamente"
}
```

---

### 4. Actualizar Usuario
```
PUT /usuarios/{id}
```
**Autorización:** `ADMINISTRADOR`  
**Parámetros de Ruta:**
- `id` (string, requerido) - ID del usuario

**Parámetros (form-data):**
- `nombre` (string, requerido) - Nuevo nombre
- `apellido` (string, requerido) - Nuevo apellido
- `telefono` (string, requerido) - Nuevo teléfono

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Usuario actualizado exitosamente"
}
```

---

### 5. Asignar Rol a Usuario
```
PUT /usuarios/{id}/asignar-rol
```
**Autorización:** `ADMINISTRADOR`  
**Parámetros de Ruta:**
- `id` (string, requerido) - ID del usuario

**Parámetros (form-data):**
- `rolId` (string, requerido) - ID del nuevo rol

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Rol asignado exitosamente"
}
```

---

### 6. Eliminar Usuario
```
DELETE /usuarios/{id}
```
**Autorización:** `ADMINISTRADOR`  
**Parámetros de Ruta:**
- `id` (string, requerido) - ID del usuario a eliminar

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Usuario eliminado exitosamente"
}
```

---

### 7. Obtener Perfil del Usuario Autenticado
```
GET /usuarios/perfil
```
**Autorización:** `isAuthenticated()` (usuario debe estar logueado)  

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Perfil obtenido",
  "data": {
    "id": "507f1f77bcf86cd799439011",
    "nombre": "Juan",
    "apellido": "García",
    "correo": "juan.garcia@email.com",
    "telefono": "+57 310 555 0123",
    "rolNombre": "CLIENTE"
  }
}
```

---

### 8. Actualizar Perfil del Usuario Autenticado
```
PUT /usuarios/perfil
```
**Autorización:** `isAuthenticated()`  
**Body (JSON):**
```json
{
  "nombre": "Juan",
  "apellido": "García",
  "correo": "juan.garcia@email.com",
  "telefono": "+57 310 555 0125"
}
```

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Perfil actualizado",
  "data": {
    "id": "507f1f77bcf86cd799439011",
    "nombre": "Juan",
    "apellido": "García",
    "correo": "juan.garcia@email.com",
    "telefono": "+57 310 555 0125",
    "rolNombre": "CLIENTE"
  }
}
```

---

### 9. Obtener Eventos Deseados del Usuario
```
GET /usuarios/eventos-deseados
```
**Autorización:** `isAuthenticated()`  

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Eventos deseados",
  "data": [
    {
      "id": "507f1f77bcf86cd799439051",
      "eventoId": "507f1f77bcf86cd799439033",
      "eventoTitulo": "Concierto de Rock Cartagena",
      "eventoLugar": "Anfiteatro Castillo San Felipe",
      "eventoFoto": "uploads/eventos/concierto-rock-1234567890.jpg"
    }
  ]
}
```

---

## 🎭 Endpoints de Roles

### 1. Listar Todos los Roles
```
GET /roles
```
**Autorización:** Pública  
**Parámetros de Query:**
- `page` (integer, default: 0)
- `size` (integer, default: 20)

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Roles obtenidos",
  "data": {
    "content": [
      {
        "id": "507f1f77bcf86cd799439021",
        "nombre": "CLIENTE",
        "descripcion": "Usuario con permisos de cliente"
      },
      {
        "id": "507f1f77bcf86cd799439022",
        "nombre": "ORGANIZADOR",
        "descripcion": "Usuario con permisos para crear eventos"
      }
    ],
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 3,
    "totalPages": 1,
    "hasNext": false,
    "hasPrevious": false
  }
}
```

---

### 2. Crear Rol
```
POST /roles
```
**Autorización:** `ADMINISTRADOR`  
**Parámetros (form-data):**
- `nombre` (string, requerido, unique, max: 50) - Nombre del rol
- `descripcion` (string, opcional, max: 255) - Descripción del rol

**Respuesta (201 CREATED):**
```json
{
  "success": true,
  "mensaje": "Rol creado exitosamente"
}
```

---

### 3. Actualizar Rol
```
PUT /roles/{id}
```
**Autorización:** `ADMINISTRADOR`  
**Parámetros de Ruta:**
- `id` (string, requerido)

**Parámetros (form-data):**
- `nombre` (string, requerido)
- `descripcion` (string, opcional)

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Rol actualizado exitosamente"
}
```

---

### 4. Eliminar Rol
```
DELETE /roles/{id}
```
**Autorización:** `ADMINISTRADOR`  

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Rol eliminado exitosamente"
}
```

---

## 📂 Endpoints de Categorías

### 1. Listar Todas las Categorías con Paginación
```
GET /categorias
```
**Autorización:** Pública  
**Parámetros de Query:**
- `page` (integer, default: 0)
- `size` (integer, default: 20)

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Categorías obtenidas",
  "data": {
    "content": [
      {
        "id": "507f1f77bcf86cd799439061",
        "nombre": "Música en Vivo",
        "foto": "uploads/categorias/musica-vivo-1234567890.jpg",
        "eventos": []
      }
    ],
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 25,
    "totalPages": 2,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

---

### 2. Listar Nombres de Categorías
```
GET /categorias/nombres
```
**Autorización:** Pública  

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Categorías obtenidas",
  "data": [
    {
      "id": "507f1f77bcf86cd799439061",
      "nombre": "Música en Vivo"
    },
    {
      "id": "507f1f77bcf86cd799439062",
      "nombre": "Deportes"
    }
  ]
}
```

---

### 3. Obtener Categorías Destacadas (Top 4)
```
GET /categorias/destacadas
```
**Autorización:** Pública  

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Categorías destacadas",
  "data": [
    {
      "id": "507f1f77bcf86cd799439061",
      "nombre": "Música en Vivo",
      "foto": "uploads/categorias/musica-vivo-1234567890.jpg",
      "eventos": []
    }
  ]
}
```

---

### 4. Obtener Categorías con Eventos
```
GET /categorias/con-eventos
```
**Autorización:** Pública  

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Categorías con eventos",
  "data": [
    {
      "id": "507f1f77bcf86cd799439061",
      "nombre": "Música en Vivo",
      "eventos": [
        {
          "id": "507f1f77bcf86cd799439033",
          "titulo": "Concierto de Rock Cartagena"
        }
      ],
      "totalEventos": 2
    }
  ]
}
```

---

### 5. Obtener Categoría por ID
```
GET /categorias/{id}
```
**Autorización:** Pública  

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Categoría obtenida",
  "data": {
    "id": "507f1f77bcf86cd799439061",
    "nombre": "Música en Vivo",
    "foto": "uploads/categorias/musica-vivo-1234567890.jpg",
    "eventos": []
  }
}
```

---

### 6. Crear Categoría
```
POST /categorias
```
**Autorización:** `ADMINISTRADOR`  
**Content-Type:** `multipart/form-data`  
**Parámetros:**
- `nombre` (string, requerido, max: 100) - Nombre de la categoría
- `foto` (file, opcional, max: 5MB) - Imagen de la categoría (jpg, png)

**Respuesta (201 CREATED):**
```json
{
  "success": true,
  "mensaje": "Categoría creada"
}
```

---

### 7. Actualizar Categoría
```
PUT /categorias/{id}
```
**Autorización:** `ADMINISTRADOR`  
**Content-Type:** `multipart/form-data`  
**Parámetros:**
- `nombre` (string, requerido)
- `foto` (file, opcional)

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Categoría actualizada"
}
```

---

### 8. Eliminar Categoría
```
DELETE /categorias/{id}
```
**Autorización:** `ADMINISTRADOR`  

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Categoría eliminada"
}
```

---

## 🌍 Endpoints de Estados

### 1. Listar Todos los Estados
```
GET /estados
```
**Autorización:** Pública  
**Parámetros de Query:**
- `page` (integer, default: 0)
- `size` (integer, default: 20)

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Estados obtenidos",
  "data": {
    "content": [
      {
        "id": "507f1f77bcf86cd799439071",
        "nombre": "PRÓXIMO",
        "descripcion": "Evento que se realizará próximamente"
      }
    ],
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 5,
    "totalPages": 1,
    "hasNext": false,
    "hasPrevious": false
  }
}
```

---

### 2. Obtener Estado por ID
```
GET /estados/{id}
```
**Autorización:** Pública  

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Estado obtenido",
  "data": {
    "id": "507f1f77bcf86cd799439071",
    "nombre": "PRÓXIMO",
    "descripcion": "Evento que se realizará próximamente"
  }
}
```

---

### 3. Crear Estado
```
POST /estados
```
**Autorización:** `ADMINISTRADOR`  
**Parámetros (form-data):**
- `nombre` (string, requerido, unique, max: 50)
- `descripcion` (string, opcional, max: 255)

**Respuesta (201 CREATED):**
```json
{
  "success": true,
  "mensaje": "Estado creado exitosamente"
}
```

---

### 4. Actualizar Estado
```
PUT /estados/{id}
```
**Autorización:** `ADMINISTRADOR`  
**Parámetros (form-data):**
- `nombre` (string, requerido)
- `descripcion` (string, opcional)

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Estado actualizado exitosamente"
}
```

---

### 5. Eliminar Estado
```
DELETE /estados/{id}
```
**Autorización:** `ADMINISTRADOR`  

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Estado eliminado exitosamente"
}
```

---

## 🎪 Endpoints de Eventos

### 1. Listar Todos los Eventos
```
GET /eventos
```
**Autorización:** Pública  
**Parámetros de Query:**
- `page` (integer, default: 0)
- `size` (integer, default: 20)
- `sort` (string, default: "fecha,asc")

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Eventos obtenidos",
  "data": {
    "content": [
      {
        "id": "507f1f77bcf86cd799439033",
        "titulo": "Concierto de Rock Cartagena",
        "descripcion": "Gran concierto de rock con bandas internacionales",
        "foto": "uploads/eventos/concierto-rock-1234567890.jpg",
        "fecha": "2026-06-15",
        "hora": "20:00:00",
        "lugar": "Anfiteatro Castillo San Felipe",
        "estado": {
          "id": "507f1f77bcf86cd799439071",
          "nombre": "PRÓXIMO"
        },
        "categoria": {
          "id": "507f1f77bcf86cd799439061",
          "nombre": "Música en Vivo"
        },
        "organizador": {
          "id": "507f1f77bcf86cd799439012",
          "nombre": "María",
          "apellido": "Rodríguez"
        },
        "localidades": [
          {
            "id": "loc-001",
            "nombre": "Zona Platinum",
            "precio": 150000,
            "capacidad": 100,
            "disponibles": 45
          }
        ],
        "promociones": [],
        "valoraciones": []
      }
    ],
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 50,
    "totalPages": 3,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

---

### 2. Obtener Evento por ID
```
GET /eventos/{id}
```
**Autorización:** Pública  

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Evento obtenido",
  "data": {
    "id": "507f1f77bcf86cd799439033",
    "titulo": "Concierto de Rock Cartagena",
    "descripcion": "Gran concierto de rock con bandas internacionales",
    "foto": "uploads/eventos/concierto-rock-1234567890.jpg",
    "fecha": "2026-06-15",
    "hora": "20:00:00",
    "lugar": "Anfiteatro Castillo San Felipe",
    "estado": {
      "id": "507f1f77bcf86cd799439071",
      "nombre": "PRÓXIMO"
    },
    "categoria": {
      "id": "507f1f77bcf86cd799439061",
      "nombre": "Música en Vivo"
    },
    "organizador": {
      "id": "507f1f77bcf86cd799439012",
      "nombre": "María",
      "apellido": "Rodríguez"
    },
    "localidades": [
      {
        "id": "loc-001",
        "nombre": "Zona Platinum",
        "precio": 150000,
        "capacidad": 100,
        "disponibles": 45
      },
      {
        "id": "loc-002",
        "nombre": "Zona General",
        "precio": 80000,
        "capacidad": 500,
        "disponibles": 250
      }
    ],
    "promociones": [
      "507f1f77bcf86cd799439081"
    ],
    "valoraciones": []
  }
}
```

---

### 3. Obtener Localidades de un Evento
```
GET /eventos/{id}/localidades
```
**Autorización:** Pública  

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Localidades obtenidas",
  "data": [
    {
      "id": "loc-001",
      "nombre": "Zona Platinum",
      "precio": 150000,
      "capacidad": 100,
      "disponibles": 45
    },
    {
      "id": "loc-002",
      "nombre": "Zona General",
      "precio": 80000,
      "capacidad": 500,
      "disponibles": 250
    }
  ]
}
```

---

### 4. Crear Evento
```
POST /eventos
```
**Autorización:** `ORGANIZADOR` | `ADMINISTRADOR`  
**Content-Type:** `application/json`  
**Body:**
```json
{
  "titulo": "Concierto de Rock Cartagena",
  "descripcion": "Gran concierto de rock con bandas internacionales",
  "foto": "uploads/eventos/concierto-rock-1234567890.jpg",
  "fecha": "2026-06-15",
  "hora": "20:00:00",
  "lugar": "Anfiteatro Castillo San Felipe",
  "estadoId": "507f1f77bcf86cd799439071",
  "categoriaId": "507f1f77bcf86cd799439061"
}
```

**Respuesta (201 CREATED):**
```json
{
  "success": true,
  "mensaje": "Evento creado",
  "data": {
    "id": "507f1f77bcf86cd799439033",
    "titulo": "Concierto de Rock Cartagena"
  }
}
```

---

### 5. Actualizar Evento
```
PUT /eventos/{id}
```
**Autorización:** `ORGANIZADOR` | `ADMINISTRADOR`  
**Body:** (mismo formato que crear)

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Evento actualizado",
  "data": {
    "id": "507f1f77bcf86cd799439033",
    "titulo": "Concierto de Rock Cartagena"
  }
}
```

---

### 6. Eliminar Evento
```
DELETE /eventos/{id}
```
**Autorización:** `ORGANIZADOR` | `ADMINISTRADOR`  

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Evento eliminado"
}
```

---

### 7. Agregar Localidad a Evento
```
POST /eventos/{eventoId}/localidades
```
**Autorización:** `ORGANIZADOR` | `ADMINISTRADOR`  
**Body:**
```json
{
  "id": "loc-003",
  "nombre": "Zona VIP",
  "precio": 200000,
  "capacidad": 50,
  "disponibles": 45
}
```

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Localidad agregada",
  "data": {
    "id": "507f1f77bcf86cd799439033"
  }
}
```

---

### 8. Actualizar Localidad
```
PUT /eventos/{eventoId}/localidades/{localidadIndex}
```
**Autorización:** `ORGANIZADOR` | `ADMINISTRADOR`  
**Body:** (mismo formato que agregar)

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Localidad actualizada",
  "data": {
    "id": "507f1f77bcf86cd799439033"
  }
}
```

---

### 9. Eliminar Localidad
```
DELETE /eventos/{eventoId}/localidades/{localidadIndex}
```
**Autorización:** `ORGANIZADOR` | `ADMINISTRADOR`  

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Localidad eliminada",
  "data": {
    "id": "507f1f77bcf86cd799439033"
  }
}
```

---

### 10. Agregar Evento a Favoritos (Deseados)
```
POST /eventos/{eventoId}/deseados
```
**Autorización:** `isAuthenticated()`  

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Evento agregado a favoritos"
}
```

---

### 11. Eliminar Evento de Favoritos
```
DELETE /eventos/{eventoId}/deseados
```
**Autorización:** `isAuthenticated()`  

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Evento eliminado de favoritos"
}
```

---

## 🛒 Endpoints de Compras

### 1. Listar Mis Compras
```
GET /compras
```
**Autorización:** `isAuthenticated()`  
**Parámetros de Query:**
- `page` (integer, default: 0)
- `size` (integer, default: 20)
- `sort` (string, default: "fechaCompra,desc")

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Compras obtenidas",
  "data": {
    "content": [
      {
        "id": "507f1f77bcf86cd799439041",
        "fechaCompra": "2026-05-10T14:30:00",
        "total": 320000,
        "metodoPago": "TARJETA_CREDITO",
        "items": [
          {
            "eventoId": "507f1f77bcf86cd799439033",
            "localidadId": "loc-001",
            "cantidad": 2,
            "precioUnitario": 150000
          }
        ]
      }
    ],
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 15,
    "totalPages": 1,
    "hasNext": false,
    "hasPrevious": false
  }
}
```

---

### 2. Obtener Compra por ID
```
GET /compras/{id}
```
**Autorización:** `isAuthenticated()`  

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Compra obtenida",
  "data": {
    "id": "507f1f77bcf86cd799439041",
    "fechaCompra": "2026-05-10T14:30:00",
    "total": 320000,
    "metodoPago": "TARJETA_CREDITO",
    "items": [
      {
        "eventoId": "507f1f77bcf86cd799439033",
        "localidadId": "loc-001",
        "cantidad": 2,
        "precioUnitario": 150000
      },
      {
        "eventoId": "507f1f77bcf86cd799439033",
        "localidadId": "loc-002",
        "cantidad": 1,
        "precioUnitario": 80000
      }
    ]
  }
}
```

---

### 3. Realizar Compra
```
POST /compras
```
**Autorización:** `isAuthenticated()`  
**Content-Type:** `application/json`  
**Body:**
```json
[
  {
    "eventoId": "507f1f77bcf86cd799439033",
    "localidadId": "loc-001",
    "cantidad": 2,
    "precioUnitario": 150000
  },
  {
    "eventoId": "507f1f77bcf86cd799439033",
    "localidadId": "loc-002",
    "cantidad": 1,
    "precioUnitario": 80000
  }
]
```

**Respuesta (201 CREATED):**
```json
{
  "success": true,
  "mensaje": "Compra realizada",
  "data": {
    "id": "507f1f77bcf86cd799439043",
    "fechaCompra": "2026-05-15T10:45:00",
    "total": 320000,
    "metodoPago": "TARJETA_CREDITO",
    "items": [
      {
        "eventoId": "507f1f77bcf86cd799439033",
        "localidadId": "loc-001",
        "cantidad": 2,
        "precioUnitario": 150000
      }
    ]
  }
}
```

---

### 4. Cancelar Compra
```
DELETE /compras/{id}
```
**Autorización:** `isAuthenticated()`  

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Compra cancelada"
}
```

---

## 🎟️ Endpoints de Boletos

> **Nota:** Este endpoint devuelve los datos básicos de la compra (según `BoletosApiController`). Para detalle de tiquetes, usar `/compras/{id}`.

### 1. Obtener Boletos de Compra
```
GET /boletos/{compraId}
```
**Autorización:** `CLIENTE`  

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Compra obtenida",
  "data": {
    "id": "507f1f77bcf86cd799439041",
    "fechaCompra": "2026-05-10T14:30:00",
    "total": 320000,
    "metodoPago": "TARJETA_CREDITO",
    "tiqueteCompras": [
      {
        "id": "507f1f77bcf86cd799439091",
        "tiquete": {
          "id": "507f1f77bcf86cd799439091",
          "codigoQR": "QR-507f1f77bcf86cd799439091-20260615",
          "localidad": {
            "id": "loc-001",
            "nombre": "Zona Platinum",
            "precio": 150000,
            "evento": {
              "id": "507f1f77bcf86cd799439033",
              "titulo": "Concierto de Rock Cartagena",
              "fecha": "2026-06-15",
              "hora": "20:00:00",
              "lugar": "Anfiteatro Castillo San Felipe"
            }
          }
        }
      }
    ]
  }
}
```

---

## 💰 Endpoints de Pagos

### 1. Obtener Datos de Usuario para Pago
```
GET /pagos
```
**Autorización:** `isAuthenticated()`  

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Usuario autenticado",
  "data": {
    "id": "507f1f77bcf86cd799439011",
    "nombre": "Juan",
    "apellido": "García",
    "correo": "juan.garcia@email.com",
    "telefono": "+57 310 555 0123",
    "rolNombre": "CLIENTE"
  }
}
```

---

## 🎁 Endpoints de Promociones

### 1. Listar Todas las Promociones
```
GET /promociones
```
**Autorización:** `ADMINISTRADOR`  
**Parámetros de Query:**
- `page` (integer, default: 0)
- `size` (integer, default: 20)

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Promociones obtenidas",
  "data": {
    "content": [
      {
        "id": "507f1f77bcf86cd799439081",
        "descripcion": "20% de descuento en todas las localidades",
        "descuento": 0.20,
        "fechaInicio": "2026-05-20",
        "fechaFinal": "2026-05-31",
        "eventoId": "507f1f77bcf86cd799439033",
        "eventoTitulo": "Concierto de Rock Cartagena"
      }
    ],
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 10,
    "totalPages": 1,
    "hasNext": false,
    "hasPrevious": false
  }
}
```

---

### 2. Listar Promociones del Organizador
```
GET /promociones/organizador
```
**Autorización:** `ORGANIZADOR`  
**Parámetros de Query:**
- `page` (integer, default: 0)
- `size` (integer, default: 20)

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Promociones obtenidas",
  "data": {
    "content": [
      {
        "id": "507f1f77bcf86cd799439081",
        "descripcion": "20% de descuento",
        "descuento": 0.20,
        "fechaInicio": "2026-05-20",
        "fechaFinal": "2026-05-31",
        "eventoId": "507f1f77bcf86cd799439033",
        "eventoTitulo": "Concierto de Rock Cartagena"
      }
    ],
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 5,
    "totalPages": 1,
    "hasNext": false,
    "hasPrevious": false
  }
}
```

---

### 3. Crear Promoción
```
POST /promociones
```
**Autorización:** `ORGANIZADOR`  
**Parámetros (form-data):**
- `eventoId` (string, requerido) - ID del evento
- `descripcion` (string, requerido, max: 500) - Descripción de la promoción
- `descuento` (BigDecimal, requerido) - Porcentaje de descuento (0-1)
- `fechaInicio` (string, requerido) - Formato: "YYYY-MM-DD"
- `fechaFin` (string, requerido) - Formato: "YYYY-MM-DD"

**Respuesta (201 CREATED):**
```json
{
  "success": true,
  "mensaje": "Promoción creada"
}
```

---

### 4. Actualizar Promoción
```
PUT /promociones/{id}
```
**Autorización:** `ORGANIZADOR`  
**Parámetros de Ruta:**
- `id` (string, requerido) - ID de la promoción

**Parámetros (form-data):**
- `eventoId` (string, requerido) - ID del evento
- `descripcion` (string, requerido, max: 500) - Descripción
- `descuento` (BigDecimal, requerido) - Porcentaje (0-1)
- `fechaInicio` (string, requerido) - Formato: "YYYY-MM-DD"
- `fechaFin` (string, requerido) - Formato: "YYYY-MM-DD"

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Promoción actualizada"
}
```

---

### 5. Eliminar Promoción
```
DELETE /promociones/{id}
```
**Autorización:** `ORGANIZADOR`  

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Promoción eliminada"
}
```

---

## ⭐ Endpoints de Valoraciones

### 1. Obtener Valoraciones del Usuario
```
GET /valoraciones/usuario
```
**Autorización:** `CLIENTE`  
**Parámetros de Query:**
- `page` (integer, default: 0)
- `size` (integer, default: 20)

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Valoraciones obtenidas",
  "data": {
    "content": [
      {
        "id": "507f1f77bcf86cd799439051",
        "comentario": "Excelente evento, la organización fue impecable",
        "calificacion": 5,
        "eventoId": "507f1f77bcf86cd799439033",
        "eventoTitulo": "Concierto de Rock Cartagena"
      }
    ],
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 8,
    "totalPages": 1,
    "hasNext": false,
    "hasPrevious": false
  }
}
```

---

### 2. Obtener Valoraciones de un Evento
```
GET /valoraciones/evento/{eventoId}
```
**Autorización:** Pública  
**Parámetros de Query:**
- `page` (integer, default: 0)
- `size` (integer, default: 20)

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Valoraciones del evento",
  "data": {
    "content": [
      {
        "id": "507f1f77bcf86cd799439051",
        "comentario": "Excelente evento, la organización fue impecable",
        "calificacion": 5,
        "eventoId": "507f1f77bcf86cd799439033",
        "eventoTitulo": "Concierto de Rock Cartagena"
      },
      {
        "id": "507f1f77bcf86cd799439052",
        "comentario": "Muy bueno, solo que los precios están un poco altos",
        "calificacion": 4,
        "eventoId": "507f1f77bcf86cd799439033",
        "eventoTitulo": "Concierto de Rock Cartagena"
      }
    ],
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 25,
    "totalPages": 2,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

---

### 3. Crear Valoración
```
POST /valoraciones
```
**Autorización:** `CLIENTE`  
**Parámetros (form-data):**
- `eventoId` (string, requerido) - ID del evento
- `comentario` (string, requerido, max: 500) - Comentario de la valoración
- `calificacion` (long, requerido) - Calificación de 1 a 5

**Validaciones:**
- El usuario debe haber comprado entradas del evento
- No puede valorar el mismo evento dos veces
- La calificación debe estar entre 1 y 5

**Respuesta (201 CREATED):**
```json
{
  "success": true,
  "mensaje": "Valoración creada exitosamente"
}
```

---

### 4. Actualizar Valoración
```
PUT /valoraciones/{id}
```
**Autorización:** `CLIENTE`  
**Parámetros (form-data):**
- `comentario` (string, requerido)
- `calificacion` (long, requerido)

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Valoración actualizada exitosamente"
}
```

---

### 5. Eliminar Valoración
```
DELETE /valoraciones/{id}
```
**Autorización:** `CLIENTE`  

**Respuesta (200 OK):**
```json
{
  "success": true,
  "mensaje": "Valoración eliminada exitosamente"
}
```

---

## 🔍 Códigos de Error Comunes

| Código | Mensaje | Causa |
|--------|---------|-------|
| 400 | Solicitud inválida | Parámetros faltantes o incorrectos |
| 401 | No autorizado | Token expirado o no autenticado |
| 403 | Acceso denegado | Permisos insuficientes |
| 404 | No encontrado | Recurso no existe |
| 409 | Conflicto | Valor único duplicado (email, nombre) |
| 422 | Entidad no procesable | Datos válidos pero negocio rechaza |
| 500 | Error interno | Problema en el servidor |

---

## 📋 Notas Importantes

1. **Paginación:** Todos los endpoints que devuelven listados usan paginación. Por defecto devuelven 20 elementos por página.

2. **Fechas:** El formato de fechas es `YYYY-MM-DD` y horas en formato `HH:mm:ss`.

3. **IDs de MongoDB:** Se utilizan ObjectIds de MongoDB (24 caracteres hexadecimales).

4. **Autenticación:** Se maneja mediante Spring Security con sesiones HTTP. Incluye automáticamente el usuario autenticado en el contexto.

5. **Archivos:** Máximo 5MB por archivo. Se guardan en `uploads/categorias` y `uploads/eventos`.

6. **Validaciones de Negocio:**
   - Un usuario no puede comprar entradas que ya ha comprado
   - No puede haber localidades sin evento
   - Las promociones deben tener fecha de fin posterior a fecha de inicio

---

## 📞 Soporte

Para reportar bugs o problemas con la API, contactar al equipo de desarrollo.

**Última actualización:** Abril 2026  
**Estado:** Producción
