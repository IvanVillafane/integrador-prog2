# Trabajo Práctico Integrador - Programación II

## Integrantes

- Ivan Alejandro Villafañe
- Juan Franco Cavallieri
- Federico Elías

---

## Descripción

**Food Store** es una aplicación desarrollada en Java como trabajo práctico integrador para la materia **Programación II**.

El sistema permite gestionar un negocio de venta de alimentos mediante la administración de:

- Categorías
- Productos
- Usuarios
- Pedidos

La aplicación puede ejecutarse en dos modalidades:

1. **Modo Consola**: interacción mediante menús en terminal.
2. **Modo Web**: servidor HTTP integrado con API REST e interfaz gráfica desarrollada con HTML, CSS y JavaScript.

---

## Enlaces del Proyecto

Link al video explicativo: https://www.youtube.com/watch?v=PmcZY5AAyos

Documentación en PDF: TP Integrador - Programación II (Ubicado en raíz del repositorio)

---

## Tecnologías Utilizadas

### Backend
- Java
- Programación Orientada a Objetos (POO)
- API HTTP nativa de Java (`HttpServer`)
- Persistencia en archivos JSON

### Frontend
- HTML5
- CSS3
- JavaScript Vanilla

---

## Funcionalidades Principales

### Gestión de Categorías
- Alta de categorías
- Modificación de categorías
- Baja lógica de categorías
- Consulta de categorías

### Gestión de Productos
- Alta de productos
- Modificación de productos
- Baja lógica de productos
- Administración de stock
- Asociación con categorías

### Gestión de Usuarios
- Registro de usuarios
- Administración de roles
- Consulta de usuarios

### Gestión de Pedidos
- Creación de pedidos
- Selección de forma de pago
- Cálculo automático del total
- Actualización de estados
- Control de stock
- Consulta de pedidos

---

## Arquitectura del Proyecto

```text
src/
└── integrador/
    ├── config/
    ├── entities/
    ├── enums/
    ├── exception/
    ├── interfaces/
    ├── service/
    └── Main.java

frontend/
├── index.html
├── style.css
└── app.js
```

### Capas

#### Entities
Contiene las entidades principales del dominio:

- Categoria
- Producto
- Usuario
- Pedido
- DetallePedido

#### Services
Implementa la lógica de negocio y operaciones CRUD.

#### Config
Configuración general del sistema:

- DataStore
- JsonUtil
- WebServer

#### Exceptions
Manejo de excepciones personalizadas de negocio.

#### Enums
Enumeraciones utilizadas por el sistema:

- Estado
- FormaPago
- Rol

---

## Modelo de Negocio

### Producto

Cada producto posee:

- Nombre
- Precio
- Descripción
- Stock
- Imagen
- Disponibilidad
- Categoría

### Pedido

Cada pedido contiene:

- Fecha
- Estado
- Forma de pago
- Usuario asociado
- Detalles del pedido
- Total calculado automáticamente

### Estados del Pedido

- Pendiente
- En preparación
- Entregado
- Cancelado

(según la implementación del enum correspondiente)

---

## API REST

El servidor web expone endpoints para la gestión de recursos.

### Categorías

```http
GET  /api/categorias
POST /api/categorias
```

### Productos

```http
GET  /api/productos
POST /api/productos
```

### Usuarios

```http
GET  /api/usuarios
POST /api/usuarios
```

### Pedidos

```http
GET  /api/pedidos
POST /api/pedidos
```

### Estado de Pedidos

```http
GET /api/pedidos/status
```

---

## Ejecución

### Modo Consola

Ejecutar:

```bash
Main.java
```

Seleccionar:

```text
1. Modo Consola Tradicional
```

### Modo Web

Ejecutar:

```bash
Main.java
```

Seleccionar:

```text
2. Modo Web
```

Luego acceder desde el navegador:

```text
http://localhost:8080
```

---

## Conceptos Aplicados

Durante el desarrollo se utilizaron conceptos de:

- Programación Orientada a Objetos
- Encapsulamiento
- Herencia
- Polimorfismo
- Interfaces
- Enumeraciones
- Manejo de excepciones
- Arquitectura por capas
- APIs REST
- Persistencia de datos
- Separación entre frontend y backend

---

## Objetivo Académico

El objetivo del proyecto es integrar los conocimientos adquiridos durante la cursada de Programación II mediante el desarrollo de una aplicación completa que combine:

- Modelado de entidades
- Lógica de negocio
- Persistencia
- Servicios
- Interfaz de usuario
- Comunicación cliente-servidor
