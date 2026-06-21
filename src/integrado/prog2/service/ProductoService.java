package integrado.prog2.service;

import integrado.prog2.config.DataStore;
import integrado.prog2.entities.Categoria;
import integrado.prog2.entities.Producto;
import integrado.prog2.exception.EntidadNoEncontradaException;
import integrado.prog2.exception.NegocioException;
import integrado.prog2.exception.StockInvalidoException;

import java.util.ArrayList;
import java.util.List;

public class ProductoService {
    private final DataStore dataStore = DataStore.getInstance();
    private final CategoriaService categoriaService = new CategoriaService();

    public List<Producto> listarProductos() {
        List<Producto> resultado = new ArrayList<>();
        for (Producto p : dataStore.getProductos()) {
            if (!p.isEliminado()) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public List<Producto> listarProductosPorCategoria(Long categoriaId) throws EntidadNoEncontradaException {
        Categoria cat = categoriaService.buscarPorId(categoriaId);
        List<Producto> resultado = new ArrayList<>();
        for (Producto p : dataStore.getProductos()) {
            if (!p.isEliminado() && p.getCategoria() != null && p.getCategoria().equals(cat)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public Producto buscarPorId(Long id) throws EntidadNoEncontradaException {
        if (id == null) {
            throw new EntidadNoEncontradaException("El ID del producto no puede ser nulo.");
        }
        for (Producto p : dataStore.getProductos()) {
            if (p.getId().equals(id) && !p.isEliminado()) {
                return p;
            }
        }
        throw new EntidadNoEncontradaException("No se encontró el producto con ID: " + id);
    }

    public Producto crearProducto(String nombre, Double precio, String descripcion, int stock, String imagen, Boolean disponible, Long categoriaId) 
            throws NegocioException, StockInvalidoException, EntidadNoEncontradaException {
        
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new NegocioException("El nombre del producto no puede estar vacío.");
        }
        if (precio == null || precio < 0.0) {
            throw new NegocioException("El precio del producto no puede ser menor a 0.");
        }
        if (stock < 0) {
            throw new StockInvalidoException("El stock inicial del producto no puede ser menor a 0.");
        }
        
        Categoria categoria = categoriaService.buscarPorId(categoriaId);

        Producto nuevo = new Producto(
                nombre.trim(), 
                precio, 
                descripcion != null ? descripcion.trim() : "", 
                stock, 
                imagen != null ? imagen.trim() : "", 
                disponible != null ? disponible : true, 
                categoria
        );
        nuevo.setId(dataStore.nextProductoId());
        dataStore.getProductos().add(nuevo);
        return nuevo;
    }

    public Producto editarProducto(Long id, String nombre, Double precio, String descripcion, Integer stock, String imagen, Boolean disponible, Long categoriaId) 
            throws EntidadNoEncontradaException, NegocioException, StockInvalidoException {
        
        Producto p = buscarPorId(id);

        if (nombre != null && !nombre.trim().isEmpty()) {
            p.setNombre(nombre.trim());
        }
        
        if (precio != null) {
            if (precio < 0.0) {
                throw new NegocioException("El precio no puede ser menor a 0.");
            }
            p.setPrecio(precio);
        }

        if (stock != null) {
            if (stock < 0) {
                throw new StockInvalidoException("El stock no puede ser menor a 0.");
            }
            p.setStock(stock);
        }

        if (descripcion != null) {
            p.setDescripcion(descripcion.trim());
        }

        if (imagen != null) {
            p.setImagen(imagen.trim());
        }

        if (disponible != null) {
            p.setDisponible(disponible);
        }

        if (categoriaId != null) {
            Categoria categoria = categoriaService.buscarPorId(categoriaId);
            p.setCategoria(categoria);
        }

        return p;
    }

    public void eliminarProducto(Long id) throws EntidadNoEncontradaException {
        Producto p = buscarPorId(id);
        p.setEliminado(true);
    }
}
