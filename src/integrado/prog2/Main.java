package integrado.prog2;

import integrado.prog2.entities.Categoria;
import integrado.prog2.entities.DetallePedido;
import integrado.prog2.entities.Pedido;
import integrado.prog2.entities.Producto;
import integrado.prog2.entities.Usuario;
import integrado.prog2.enums.Estado;
import integrado.prog2.enums.FormaPago;
import integrado.prog2.enums.Rol;
import integrado.prog2.exception.FoodStoreException;
import integrado.prog2.service.CategoriaService;
import integrado.prog2.service.PedidoService;
import integrado.prog2.service.ProductoService;
import integrado.prog2.service.UsuarioService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final CategoriaService categoriaService = new CategoriaService();
    private static final ProductoService productoService = new ProductoService();
    private static final UsuarioService usuarioService = new UsuarioService();
    private static final PedidoService pedidoService = new PedidoService();

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("    BIENVENIDO A FOOD STORE (SISTEMA DE PEDIDOS)   ");
        System.out.println("==================================================");
        System.out.println("Seleccione modo de inicio:");
        System.out.println("1. Modo Consola Tradicional");
        System.out.println("2. Modo Web (Servidor API REST + Interfaz Gráfica)");
        int modo = leerEntero("Seleccione opción (1 o 2): ");

        if (modo == 2) {
            System.out.println("Iniciando Servidor Web...");
            integrado.prog2.config.WebServer.start(8080);
            try {
                Thread.currentThread().join();
            } catch (InterruptedException e) {
                System.out.println("Servidor detenido.");
            }
        } else {
            ejecutarModoConsola();
        }
    }

    private static void ejecutarModoConsola() {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n=== SISTEMA DE PEDIDOS (FOOD STORE) ===");
            System.out.println("1. Categorías");
            System.out.println("2. Productos");
            System.out.println("3. Usuarios");
            System.out.println("4. Pedidos");
            System.out.println("0. Salir");
            int opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1:
                    menuCategorias();
                    break;
                case 2:
                    menuProductos();
                    break;
                case 3:
                    menuUsuarios();
                    break;
                case 4:
                    menuPedidos();
                    break;
                case 0:
                    salir = true;
                    System.out.println("\n¡Gracias por utilizar Food Store! Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opción inválida. Intente nuevamente.");
            }
        }
    }



    private static void menuCategorias() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- MENÚ CATEGORÍAS ---");
            System.out.println("1. Listar categorías");
            System.out.println("2. Crear categoría");
            System.out.println("3. Editar categoría");
            System.out.println("4. Eliminar categoría");
            System.out.println("0. Volver al menú principal");
            int opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1:
                    listarCategorias();
                    break;
                case 2:
                    crearCategoria();
                    break;
                case 3:
                    editarCategoria();
                    break;
                case 4:
                    eliminarCategoria();
                    break;
                case 0:
                    volver = true;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    private static void menuProductos() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- MENÚ PRODUCTOS ---");
            System.out.println("1. Listar todos los productos");
            System.out.println("2. Listar productos por categoría");
            System.out.println("3. Crear producto");
            System.out.println("4. Editar producto");
            System.out.println("5. Eliminar producto");
            System.out.println("0. Volver al menú principal");
            int opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1:
                    listarProductos();
                    break;
                case 2:
                    listarProductosPorCategoria();
                    break;
                case 3:
                    crearProducto();
                    break;
                case 4:
                    editarProducto();
                    break;
                case 5:
                    eliminarProducto();
                    break;
                case 0:
                    volver = true;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    private static void menuUsuarios() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- MENÚ USUARIOS ---");
            System.out.println("1. Listar usuarios");
            System.out.println("2. Crear usuario");
            System.out.println("3. Editar usuario");
            System.out.println("4. Eliminar usuario");
            System.out.println("0. Volver al menú principal");
            int opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1:
                    listarUsuarios();
                    break;
                case 2:
                    crearUsuario();
                    break;
                case 3:
                    editarUsuario();
                    break;
                case 4:
                    eliminarUsuario();
                    break;
                case 0:
                    volver = true;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    private static void menuPedidos() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- MENÚ PEDIDOS ---");
            System.out.println("1. Listar todos los pedidos");
            System.out.println("2. Listar pedidos por usuario");
            System.out.println("3. Crear pedido");
            System.out.println("4. Actualizar estado / forma de pago");
            System.out.println("5. Eliminar pedido");
            System.out.println("0. Volver al menú principal");
            int opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1:
                    listarPedidos();
                    break;
                case 2:
                    listarPedidosPorUsuario();
                    break;
                case 3:
                    crearPedido();
                    break;
                case 4:
                    actualizarPedido();
                    break;
                case 5:
                    eliminarPedido();
                    break;
                case 0:
                    volver = true;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }


    private static void listarCategorias() {
        System.out.println("\n--- Listado de Categorías ---");
        List<Categoria> lista = categoriaService.listarCategorias();
        if (lista.isEmpty()) {
            System.out.println("No hay categorías cargadas.");
        } else {
            for (Categoria c : lista) {
                System.out.println(c);
            }
        }
    }

    private static void crearCategoria() {
        System.out.println("\n--- Crear Categoría ---");
        String nombre = leerTextoObligatorio("Ingrese nombre: ");
        String descripcion = leerTextoObligatorio("Ingrese descripción: ");

        try {
            Categoria c = categoriaService.crearCategoria(nombre, descripcion);
            System.out.println("✓ Categoría creada exitosamente con ID: " + c.getId());
        } catch (FoodStoreException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private static void editarCategoria() {
        System.out.println("\n--- Editar Categoría ---");
        listarCategorias();
        long id = leerEntero("Ingrese el ID de la categoría a editar: ");

        System.out.println("(Deje en blanco y presione Enter para mantener el valor actual)");
        String nombre = leerTextoOpcional("Nuevo nombre: ");
        String descripcion = leerTextoOpcional("Nueva descripción: ");

        try {
            Categoria c = categoriaService.editarCategoria(id, nombre, descripcion);
            System.out.println("✓ Categoría actualizada: " + c);
        } catch (FoodStoreException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private static void eliminarCategoria() {
        System.out.println("\n--- Eliminar Categoría ---");
        listarCategorias();
        long id = leerEntero("Ingrese el ID de la categoría a eliminar: ");
        
        String confirmacion = leerTextoObligatorio("¿Está seguro de eliminar esta categoría? (S/N): ");
        if (confirmacion.equalsIgnoreCase("S")) {
            try {
                categoriaService.eliminarCategoria(id);
                System.out.println("✓ Categoría eliminada exitosamente (baja lógica).");
            } catch (FoodStoreException e) {
                System.out.println("✗ Error: " + e.getMessage());
            }
        } else {
            System.out.println("Operación cancelada.");
        }
    }


    private static void listarProductos() {
        System.out.println("\n--- Listado de Productos ---");
        List<Producto> lista = productoService.listarProductos();
        if (lista.isEmpty()) {
            System.out.println("No hay productos cargados.");
        } else {
            for (Producto p : lista) {
                System.out.println(p);
            }
        }
    }

    private static void listarProductosPorCategoria() {
        System.out.println("\n--- Listar Productos por Categoría ---");
        listarCategorias();
        long catId = leerEntero("Ingrese el ID de la categoría: ");

        try {
            List<Producto> lista = productoService.listarProductosPorCategoria(catId);
            if (lista.isEmpty()) {
                System.out.println("No hay productos cargados en esta categoría.");
            } else {
                for (Producto p : lista) {
                    System.out.println(p);
                }
            }
        } catch (FoodStoreException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private static void crearProducto() {
        System.out.println("\n--- Crear Producto ---");
        String nombre = leerTextoObligatorio("Ingrese nombre: ");
        double precio = leerDouble("Ingrese precio: ");
        String descripcion = leerTextoObligatorio("Ingrese descripción: ");
        int stock = leerEntero("Ingrese stock inicial: ");
        String imagen = leerTextoObligatorio("Ingrese nombre de archivo de imagen (ej. pizza.png): ");
        
        System.out.println("Disponibilidad:");
        System.out.println("1. Disponible");
        System.out.println("2. No disponible");
        int dispOpt = leerEntero("Seleccione opción: ");
        boolean disponible = (dispOpt == 1);

        listarCategorias();
        long catId = leerEntero("Ingrese ID de categoría para asociar: ");

        try {
            Producto p = productoService.crearProducto(nombre, precio, descripcion, stock, imagen, disponible, catId);
            System.out.println("✓ Producto creado exitosamente con ID: " + p.getId());
        } catch (FoodStoreException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private static void editarProducto() {
        System.out.println("\n--- Editar Producto ---");
        listarProductos();
        long id = leerEntero("Ingrese el ID del producto a editar: ");

        System.out.println("(Deje en blanco/presione Enter para mantener el valor actual)");
        String nombre = leerTextoOpcional("Nuevo nombre: ");
        Double precio = leerDoubleOpcional("Nuevo precio: ");
        String descripcion = leerTextoOpcional("Nueva descripción: ");
        Integer stock = leerEnteroOpcional("Nuevo stock: ");
        String imagen = leerTextoOpcional("Nueva imagen: ");
        
        Boolean disponible = null;
        System.out.println("Nueva Disponibilidad (1: Disponible, 2: No disponible, Enter para no cambiar):");
        String dispStr = leerTextoOpcional("Seleccione: ");
        if (!dispStr.isEmpty()) {
            disponible = dispStr.equals("1");
        }

        Long catId = null;
        System.out.println("¿Desea cambiar la categoría? (Ingrese ID de categoría, o Enter para no cambiar)");
        listarCategorias();
        String catStr = leerTextoOpcional("ID Categoría: ");
        if (!catStr.isEmpty()) {
            try {
                catId = Long.parseLong(catStr);
            } catch (NumberFormatException e) {
                System.out.println("ID inválido, se mantendrá la categoría original.");
            }
        }

        try {
            Producto p = productoService.editarProducto(id, nombre, precio, descripcion, stock, imagen, disponible, catId);
            System.out.println("✓ Producto actualizado: " + p);
        } catch (FoodStoreException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private static void eliminarProducto() {
        System.out.println("\n--- Eliminar Producto ---");
        listarProductos();
        long id = leerEntero("Ingrese el ID del producto a eliminar: ");

        String confirmacion = leerTextoObligatorio("¿Está seguro de eliminar este producto? (S/N): ");
        if (confirmacion.equalsIgnoreCase("S")) {
            try {
                productoService.eliminarProducto(id);
                System.out.println("✓ Producto eliminado exitosamente (baja lógica).");
            } catch (FoodStoreException e) {
                System.out.println("✗ Error: " + e.getMessage());
            }
        } else {
            System.out.println("Operación cancelada.");
        }
    }


    private static void listarUsuarios() {
        System.out.println("\n--- Listado de Usuarios ---");
        List<Usuario> lista = usuarioService.listarUsuarios();
        if (lista.isEmpty()) {
            System.out.println("No hay usuarios cargados.");
        } else {
            for (Usuario u : lista) {
                System.out.println(u);
            }
        }
    }

    private static void crearUsuario() {
        System.out.println("\n--- Crear Usuario ---");
        String nombre = leerTextoObligatorio("Nombre: ");
        String apellido = leerTextoObligatorio("Apellido: ");
        String mail = leerTextoObligatorio("Email: ");
        String celular = leerTextoObligatorio("Celular: ");
        String contrasenia = leerTextoObligatorio("Contraseña: ");
        
        System.out.println("Rol del usuario:");
        System.out.println("1. ADMIN");
        System.out.println("2. USUARIO");
        int rolOpt = leerEntero("Seleccione rol: ");
        Rol rol = (rolOpt == 1) ? Rol.ADMIN : Rol.USUARIO;

        try {
            Usuario u = usuarioService.crearUsuario(nombre, apellido, mail, celular, contrasenia, rol);
            System.out.println("✓ Usuario creado exitosamente con ID: " + u.getId());
        } catch (FoodStoreException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private static void editarUsuario() {
        System.out.println("\n--- Editar Usuario ---");
        listarUsuarios();
        long id = leerEntero("Ingrese el ID del usuario a editar: ");

        System.out.println("(Deje en blanco/presione Enter para mantener el valor actual)");
        String nombre = leerTextoOpcional("Nuevo nombre: ");
        String apellido = leerTextoOpcional("Nuevo apellido: ");
        String mail = leerTextoOpcional("Nuevo email: ");
        String celular = leerTextoOpcional("Nuevo celular: ");
        String contrasenia = leerTextoOpcional("Nueva contraseña: ");

        Rol rol = null;
        System.out.println("Nuevo Rol (1: ADMIN, 2: USUARIO, Enter para no cambiar):");
        String rolStr = leerTextoOpcional("Seleccione: ");
        if (!rolStr.isEmpty()) {
            rol = rolStr.equals("1") ? Rol.ADMIN : Rol.USUARIO;
        }

        try {
            Usuario u = usuarioService.editarUsuario(id, nombre, apellido, mail, celular, contrasenia, rol);
            System.out.println("✓ Usuario actualizado: " + u);
        } catch (FoodStoreException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private static void eliminarUsuario() {
        System.out.println("\n--- Eliminar Usuario ---");
        listarUsuarios();
        long id = leerEntero("Ingrese el ID del usuario a eliminar: ");

        String confirmacion = leerTextoObligatorio("¿Está seguro de eliminar este usuario? (S/N): ");
        if (confirmacion.equalsIgnoreCase("S")) {
            try {
                usuarioService.eliminarUsuario(id);
                System.out.println("✓ Usuario eliminado exitosamente (baja lógica).");
            } catch (FoodStoreException e) {
                System.out.println("✗ Error: " + e.getMessage());
            }
        } else {
            System.out.println("Operación cancelada.");
        }
    }


    private static void listarPedidos() {
        System.out.println("\n--- Listado de Pedidos ---");
        List<Pedido> lista = pedidoService.listarPedidos();
        if (lista.isEmpty()) {
            System.out.println("No hay pedidos cargados.");
        } else {
            for (Pedido p : lista) {
                System.out.println(p);
                for (DetallePedido d : p.getDetalles()) {
                    System.out.println(d);
                }
            }
        }
    }

    private static void listarPedidosPorUsuario() {
        System.out.println("\n--- Listar Pedidos por Usuario ---");
        listarUsuarios();
        long usrId = leerEntero("Ingrese ID de usuario: ");

        try {
            List<Pedido> lista = pedidoService.listarPedidosPorUsuario(usrId);
            if (lista.isEmpty()) {
                System.out.println("Este usuario no registra pedidos activos.");
            } else {
                for (Pedido p : lista) {
                    System.out.println(p);
                    for (DetallePedido d : p.getDetalles()) {
                        System.out.println(d);
                    }
                }
            }
        } catch (FoodStoreException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private static void crearPedido() {
        System.out.println("\n--- Crear Pedido (con Detalles) ---");
        listarUsuarios();
        long usuarioId = leerEntero("Seleccione ID del usuario comprador: ");

        System.out.println("Seleccione Forma de Pago:");
        System.out.println("1. TARJETA");
        System.out.println("2. TRANSFERENCIA");
        System.out.println("3. EFECTIVO");
        int pagoOpt = leerEntero("Opción: ");
        FormaPago formaPago = FormaPago.EFECTIVO;
        if (pagoOpt == 1) formaPago = FormaPago.TARJETA;
        else if (pagoOpt == 2) formaPago = FormaPago.TRANSFERENCIA;

        List<PedidoService.ItemPedido> items = new ArrayList<>();
        boolean cargandoDetalles = true;

        System.out.println("\nCarga de productos para el pedido:");
        while (cargandoDetalles) {
            System.out.println("\nProductos Disponibles:");
            List<Producto> productos = productoService.listarProductos();
            for (Producto prod : productos) {
                if (prod.getDisponible()) {
                    System.out.println("ID: " + prod.getId() + " - " + prod.getNombre() + 
                            " (Precio: $" + prod.getPrecio() + " | Stock: " + prod.getStock() + ")");
                }
            }

            long prodId = leerEntero("Ingrese ID del producto (o 0 para finalizar y procesar pedido): ");
            if (prodId == 0) {
                if (items.isEmpty()) {
                    System.out.println("Debe ingresar al menos 1 producto antes de finalizar.");
                    continue;
                }
                cargandoDetalles = false;
                break;
            }

            boolean existe = false;
            for (Producto pr : productos) {
                if (pr.getId().equals(prodId) && pr.getDisponible()) {
                    existe = true;
                    break;
                }
            }

            if (!existe) {
                System.out.println("Producto inválido o no disponible.");
                continue;
            }

            int cantidad = leerEntero("Ingrese cantidad: ");
            if (cantidad <= 0) {
                System.out.println("La cantidad debe ser mayor a 0.");
                continue;
            }

            items.add(new PedidoService.ItemPedido(prodId, cantidad));
            System.out.println("✓ Detalle pre-agregado al pedido.");
        }

        try {
            System.out.println("Procesando pedido...");
            Pedido p = pedidoService.crearPedido(usuarioId, formaPago, items);
            System.out.println("==================================================");
            System.out.println("✓ ¡PEDIDO CREADO CON ÉXITO!");
            System.out.println("Detalles del Pedido:");
            System.out.println(p);
            for (DetallePedido dp : p.getDetalles()) {
                System.out.println(dp);
            }
            System.out.println("==================================================");
        } catch (FoodStoreException e) {
            System.out.println("==================================================");
            System.out.println("✗ ERROR CRÍTICO AL CREAR PEDIDO");
            System.out.println("Detalle del error: " + e.getMessage());
            System.out.println("El pedido ha sido CANCELADO. Lotes y stock revertidos.");
            System.out.println("==================================================");
        }
    }

    private static void actualizarPedido() {
        System.out.println("\n--- Actualizar Estado / Forma de Pago de Pedido ---");
        listarPedidos();
        long id = leerEntero("Ingrese el ID del pedido a actualizar: ");

        Estado nuevoEstado = null;
        System.out.println("Seleccione Nuevo Estado:");
        System.out.println("1. PENDIENTE");
        System.out.println("2. CONFIRMADO");
        System.out.println("3. TERMINADO");
        System.out.println("4. CANCELADO");
        System.out.println("0. No modificar estado");
        int estOpt = leerEntero("Opción: ");
        switch (estOpt) {
            case 1: nuevoEstado = Estado.PENDIENTE; break;
            case 2: nuevoEstado = Estado.CONFIRMADO; break;
            case 3: nuevoEstado = Estado.TERMINADO; break;
            case 4: nuevoEstado = Estado.CANCELADO; break;
        }

        FormaPago nuevaFormaPago = null;
        System.out.println("Seleccione Nueva Forma de Pago:");
        System.out.println("1. TARJETA");
        System.out.println("2. TRANSFERENCIA");
        System.out.println("3. EFECTIVO");
        System.out.println("0. No modificar forma de pago");
        int pagoOpt = leerEntero("Opción: ");
        switch (pagoOpt) {
            case 1: nuevaFormaPago = FormaPago.TARJETA; break;
            case 2: nuevaFormaPago = FormaPago.TRANSFERENCIA; break;
            case 3: nuevaFormaPago = FormaPago.EFECTIVO; break;
        }

        try {
            Pedido p = pedidoService.actualizarEstadoPago(id, nuevoEstado, nuevaFormaPago);
            System.out.println("✓ Pedido actualizado con éxito.");
            System.out.println(p);
        } catch (FoodStoreException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private static void eliminarPedido() {
        System.out.println("\n--- Eliminar Pedido ---");
        listarPedidos();
        long id = leerEntero("Ingrese el ID del pedido a eliminar: ");

        String confirmacion = leerTextoObligatorio("¿Está seguro de eliminar este pedido? (S/N): ");
        if (confirmacion.equalsIgnoreCase("S")) {
            try {
                pedidoService.eliminarPedido(id);
                System.out.println("✓ Pedido eliminado exitosamente (baja lógica) y stock restituido si correspondía.");
            } catch (FoodStoreException e) {
                System.out.println("✗ Error: " + e.getMessage());
            }
        } else {
            System.out.println("Operación cancelada.");
        }
    }


    private static int leerEntero(String prompt) {
        while (true) {
            System.out.print(prompt);
            String entrada = scanner.nextLine().trim();
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número entero.");
            }
        }
    }

    private static Integer leerEnteroOpcional(String prompt) {
        while (true) {
            System.out.print(prompt);
            String entrada = scanner.nextLine().trim();
            if (entrada.isEmpty()) {
                return null;
            }
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número entero o presione Enter.");
            }
        }
    }

    private static double leerDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String entrada = scanner.nextLine().trim();
            try {
                return Double.parseDouble(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número decimal (use punto '.' para decimales).");
            }
        }
    }

    private static Double leerDoubleOpcional(String prompt) {
        while (true) {
            System.out.print(prompt);
            String entrada = scanner.nextLine().trim();
            if (entrada.isEmpty()) {
                return null;
            }
            try {
                return Double.parseDouble(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número decimal o presione Enter.");
            }
        }
    }

    private static String leerTextoObligatorio(String prompt) {
        while (true) {
            System.out.print(prompt);
            String entrada = scanner.nextLine().trim();
            if (!entrada.isEmpty()) {
                return entrada;
            }
            System.out.println("Este campo es obligatorio.");
        }
    }

    private static String leerTextoOpcional(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}
