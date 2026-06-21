package integrado.prog2.config;

import integrado.prog2.entities.*;
import integrado.prog2.enums.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class DataStore {
    private static DataStore instance;

    private final List<Categoria> categorias = new ArrayList<>();
    private final List<Producto> productos = new ArrayList<>();
    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<Pedido> pedidos = new ArrayList<>();

    private final AtomicLong categoriaIdGen = new AtomicLong(1);
    private final AtomicLong productoIdGen = new AtomicLong(1);
    private final AtomicLong usuarioIdGen = new AtomicLong(1);
    private final AtomicLong pedidoIdGen = new AtomicLong(1);

    private DataStore() {
        seedData();
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public long nextCategoriaId() {
        return categoriaIdGen.getAndIncrement();
    }

    public long nextProductoId() {
        return productoIdGen.getAndIncrement();
    }

    public long nextUsuarioId() {
        return usuarioIdGen.getAndIncrement();
    }

    public long nextPedidoId() {
        return pedidoIdGen.getAndIncrement();
    }

    private void seedData() {
        Categoria catMinutas = new Categoria("Minutas", "Platos rápidos como hamburguesas, milanesas, papas fritas.");
        catMinutas.setId(nextCategoriaId());
        categorias.add(catMinutas);

        Categoria catPizzas = new Categoria("Pizzas", "Pizzas artesanales de diversos sabores y tamaños.");
        catPizzas.setId(nextCategoriaId());
        categorias.add(catPizzas);

        Categoria catBebidas = new Categoria("Bebidas", "Gaseosas, jugos, aguas y cervezas.");
        catBebidas.setId(nextCategoriaId());
        categorias.add(catBebidas);

        Producto burgerDouble = new Producto("Hamburguesa Doble", 4500.00, "Doble carne, doble queso cheddar, lechuga y salsa especial.", 20, "burger_double.png", true, catMinutas);
        burgerDouble.setId(nextProductoId());
        productos.add(burgerDouble);

        Producto papasFritas = new Producto("Papas Fritas Medianas", 2000.00, "Papas fritas bastón crujientes con sal.", 50, "papas.png", true, catMinutas);
        papasFritas.setId(nextProductoId());
        productos.add(papasFritas);

        Producto pizzaMuzzarella = new Producto("Pizza Muzzarella", 6000.00, "Masa casera, salsa de tomate, muzzarella y aceitunas.", 15, "pizza_muzza.png", true, catPizzas);
        pizzaMuzzarella.setId(nextProductoId());
        productos.add(pizzaMuzzarella);

        Producto cocaCola = new Producto("Coca Cola 500ml", 1200.00, "Gaseosa sabor original en botella de plástico.", 100, "coca.png", true, catBebidas);
        cocaCola.setId(nextProductoId());
        productos.add(cocaCola);

        Usuario admin = new Usuario("Ivan", "Alejandro", "admin@foodstore.com", "123456789", "admin123", Rol.ADMIN);
        admin.setId(nextUsuarioId());
        usuarios.add(admin);

        Usuario user1 = new Usuario("Maria", "Gomez", "maria@mail.com", "987654321", "maria456", Rol.USUARIO);
        user1.setId(nextUsuarioId());
        usuarios.add(user1);

        Usuario user2 = new Usuario("Juan", "Perez", "juan@mail.com", "555123456", "juan789", Rol.USUARIO);
        user2.setId(nextUsuarioId());
        usuarios.add(user2);
    }
}
