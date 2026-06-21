package integrado.prog2.service;

import integrado.prog2.config.DataStore;
import integrado.prog2.entities.Categoria;
import integrado.prog2.entities.Producto;
import integrado.prog2.exception.EntidadNoEncontradaException;
import integrado.prog2.exception.NegocioException;

import java.util.ArrayList;
import java.util.List;

public class CategoriaService {
    private final DataStore dataStore = DataStore.getInstance();

    public List<Categoria> listarCategorias() {
        List<Categoria> resultado = new ArrayList<>();
        for (Categoria c : dataStore.getCategorias()) {
            if (!c.isEliminado()) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    public Categoria buscarPorId(Long id) throws EntidadNoEncontradaException {
        if (id == null) {
            throw new EntidadNoEncontradaException("El ID de la categoría no puede ser nulo.");
        }
        for (Categoria c : dataStore.getCategorias()) {
            if (c.getId().equals(id) && !c.isEliminado()) {
                return c;
            }
        }
        throw new EntidadNoEncontradaException("No se encontró la categoría con ID: " + id);
    }

    public Categoria crearCategoria(String nombre, String descripcion) throws NegocioException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new NegocioException("El nombre de la categoría no puede estar vacío.");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new NegocioException("La descripción de la categoría no puede estar vacía.");
        }

        for (Categoria c : dataStore.getCategorias()) {
            if (!c.isEliminado() && c.getNombre().equalsIgnoreCase(nombre.trim())) {
                throw new NegocioException("Ya existe una categoría activa con el nombre: " + nombre);
            }
        }

        Categoria nueva = new Categoria(nombre.trim(), descripcion.trim());
        nueva.setId(dataStore.nextCategoriaId());
        dataStore.getCategorias().add(nueva);
        return nueva;
    }

    public Categoria editarCategoria(Long id, String nombre, String descripcion) throws EntidadNoEncontradaException, NegocioException {
        Categoria c = buscarPorId(id);

        if (nombre != null && !nombre.trim().isEmpty()) {
            if (!c.getNombre().equalsIgnoreCase(nombre.trim())) {
                for (Categoria cat : dataStore.getCategorias()) {
                    if (!cat.isEliminado() && cat.getNombre().equalsIgnoreCase(nombre.trim())) {
                        throw new NegocioException("Ya existe una categoría activa con el nombre: " + nombre);
                    }
                }
            }
            c.setNombre(nombre.trim());
        }

        if (descripcion != null && !descripcion.trim().isEmpty()) {
            c.setDescripcion(descripcion.trim());
        }

        return c;
    }

    public void eliminarCategoria(Long id) throws EntidadNoEncontradaException, NegocioException {
        Categoria c = buscarPorId(id);

        for (Producto p : dataStore.getProductos()) {
            if (!p.isEliminado() && p.getCategoria() != null && p.getCategoria().equals(c)) {
                throw new NegocioException("No se puede eliminar la categoría '" + c.getNombre() + 
                        "' porque tiene productos activos asociados (ej: " + p.getNombre() + ").");
            }
        }

        c.setEliminado(true);
    }
}
