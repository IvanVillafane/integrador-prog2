package integrado.prog2.service;

import integrado.prog2.config.DataStore;
import integrado.prog2.entities.DetallePedido;
import integrado.prog2.entities.Pedido;
import integrado.prog2.entities.Producto;
import integrado.prog2.entities.Usuario;
import integrado.prog2.enums.Estado;
import integrado.prog2.enums.FormaPago;
import integrado.prog2.exception.EntidadNoEncontradaException;
import integrado.prog2.exception.NegocioException;
import integrado.prog2.exception.StockInvalidoException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PedidoService {
    private final DataStore dataStore = DataStore.getInstance();
    private final UsuarioService usuarioService = new UsuarioService();
    private final ProductoService productoService = new ProductoService();

    public static class ItemPedido {
        private Long productoId;
        private int cantidad;

        public ItemPedido(Long productoId, int cantidad) {
            this.productoId = productoId;
            this.cantidad = cantidad;
        }

        public Long getProductoId() {
            return productoId;
        }

        public int getCantidad() {
            return cantidad;
        }
    }

    public List<Pedido> listarPedidos() {
        List<Pedido> resultado = new ArrayList<>();
        for (Pedido p : dataStore.getPedidos()) {
            if (!p.isEliminado()) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public List<Pedido> listarPedidosPorUsuario(Long usuarioId) throws EntidadNoEncontradaException {
        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        List<Pedido> resultado = new ArrayList<>();
        for (Pedido p : dataStore.getPedidos()) {
            if (!p.isEliminado() && p.getUsuario() != null && p.getUsuario().equals(usuario)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public Pedido buscarPorId(Long id) throws EntidadNoEncontradaException {
        if (id == null) {
            throw new EntidadNoEncontradaException("El ID del pedido no puede ser nulo.");
        }
        for (Pedido p : dataStore.getPedidos()) {
            if (p.getId().equals(id) && !p.isEliminado()) {
                return p;
            }
        }
        throw new EntidadNoEncontradaException("No se encontró el pedido con ID: " + id);
    }

    public Pedido crearPedido(Long usuarioId, FormaPago formaPago, List<ItemPedido> items) 
            throws EntidadNoEncontradaException, StockInvalidoException, NegocioException {
        
        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        if (usuario.isEliminado()) {
            throw new EntidadNoEncontradaException("El usuario está eliminado y no puede realizar pedidos.");
        }
        if (items == null || items.isEmpty()) {
            throw new NegocioException("Debe agregar al menos un detalle de producto para crear el pedido.");
        }

        Pedido nuevoPedido = new Pedido(usuario, formaPago);
        
        Map<Producto, Integer> stockModificaciones = new HashMap<>();

        try {
            for (ItemPedido item : items) {
                Producto producto = productoService.buscarPorId(item.getProductoId());
                int cantidad = item.getCantidad();

                if (cantidad <= 0) {
                    throw new NegocioException("La cantidad del producto '" + producto.getNombre() + "' debe ser mayor a 0.");
                }
                
                if (!producto.getDisponible()) {
                    throw new NegocioException("El producto '" + producto.getNombre() + "' no se encuentra disponible para la venta.");
                }

                if (producto.getStock() < cantidad) {
                    throw new StockInvalidoException("Stock insuficiente para el producto '" + producto.getNombre() + 
                            "'. Disponible: " + producto.getStock() + ", Solicitado: " + cantidad);
                }

                int stockAnterior = producto.getStock();
                producto.setStock(stockAnterior - cantidad);
                stockModificaciones.put(producto, stockModificaciones.getOrDefault(producto, 0) + cantidad);

                Double subtotal = cantidad * producto.getPrecio();
                nuevoPedido.addDetallePedido(cantidad, subtotal, producto);
            }

            nuevoPedido.calcularTotal();

            nuevoPedido.setId(dataStore.nextPedidoId());
            dataStore.getPedidos().add(nuevoPedido);

            return nuevoPedido;

        } catch (Exception e) {
            for (Map.Entry<Producto, Integer> entry : stockModificaciones.entrySet()) {
                Producto p = entry.getKey();
                int cantARestaurar = entry.getValue();
                p.setStock(p.getStock() + cantARestaurar);
            }
            throw e;
        }
    }

    public Pedido actualizarEstadoPago(Long id, Estado nuevoEstado, FormaPago nuevaFormaPago) 
            throws EntidadNoEncontradaException {
        
        Pedido p = buscarPorId(id);

        if (nuevoEstado != null) {
            if (nuevoEstado == Estado.CANCELADO && p.getEstado() != Estado.CANCELADO) {
                for (DetallePedido detalle : p.getDetalles()) {
                    Producto prod = detalle.getProducto();
                    if (prod != null) {
                        prod.setStock(prod.getStock() + detalle.getCantidad());
                    }
                }
            }
            p.setEstado(nuevoEstado);
        }

        if (nuevaFormaPago != null) {
            p.setFormaPago(nuevaFormaPago);
        }

        return p;
    }

    public void eliminarPedido(Long id) throws EntidadNoEncontradaException {
        Pedido p = buscarPorId(id);
        
       
        if (p.getEstado() != Estado.CANCELADO) {
            for (DetallePedido detalle : p.getDetalles()) {
                Producto prod = detalle.getProducto();
                if (prod != null) {
                    prod.setStock(prod.getStock() + detalle.getCantidad());
                }
            }
        }
        
        p.setEliminado(true);
        for (DetallePedido d : p.getDetalles()) {
            d.setEliminado(true);
        }
    }
}
