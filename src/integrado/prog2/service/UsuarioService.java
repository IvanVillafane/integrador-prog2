package integrado.prog2.service;

import integrado.prog2.config.DataStore;
import integrado.prog2.entities.Usuario;
import integrado.prog2.enums.Rol;
import integrado.prog2.exception.EntidadNoEncontradaException;
import integrado.prog2.exception.NegocioException;

import java.util.ArrayList;
import java.util.List;

public class UsuarioService {
    private final DataStore dataStore = DataStore.getInstance();

    public List<Usuario> listarUsuarios() {
        List<Usuario> resultado = new ArrayList<>();
        for (Usuario u : dataStore.getUsuarios()) {
            if (!u.isEliminado()) {
                resultado.add(u);
            }
        }
        return resultado;
    }

    public Usuario buscarPorId(Long id) throws EntidadNoEncontradaException {
        if (id == null) {
            throw new EntidadNoEncontradaException("El ID del usuario no puede ser nulo.");
        }
        for (Usuario u : dataStore.getUsuarios()) {
            if (u.getId().equals(id) && !u.isEliminado()) {
                return u;
            }
        }
        throw new EntidadNoEncontradaException("No se encontró el usuario con ID: " + id);
    }

    public Usuario crearUsuario(String nombre, String apellido, String mail, String celular, String contrasenia, Rol rol) 
            throws NegocioException {
        
        validarDatosBasicos(nombre, apellido, mail, contrasenia);
        validarMailUnico(mail, null);

        Usuario nuevo = new Usuario(
                nombre.trim(), 
                apellido.trim(), 
                mail.trim().toLowerCase(), 
                celular != null ? celular.trim() : "", 
                contrasenia, 
                rol != null ? rol : Rol.USUARIO
        );
        nuevo.setId(dataStore.nextUsuarioId());
        dataStore.getUsuarios().add(nuevo);
        return nuevo;
    }

    public Usuario editarUsuario(Long id, String nombre, String apellido, String mail, String celular, String contrasenia, Rol rol) 
            throws EntidadNoEncontradaException, NegocioException {
        
        Usuario u = buscarPorId(id);

        if (nombre != null && !nombre.trim().isEmpty()) {
            u.setNombre(nombre.trim());
        }
        if (apellido != null && !apellido.trim().isEmpty()) {
            u.setApellido(apellido.trim());
        }
        if (mail != null && !mail.trim().isEmpty()) {
            String cleanMail = mail.trim().toLowerCase();
            if (!cleanMail.contains("@") || !cleanMail.contains(".")) {
                throw new NegocioException("Formato de correo electrónico inválido.");
            }
            if (!u.getMail().equals(cleanMail)) {
                validarMailUnico(cleanMail, id);
            }
            u.setMail(cleanMail);
        }
        if (celular != null) {
            u.setCelular(celular.trim());
        }
        if (contrasenia != null && !contrasenia.isEmpty()) {
            u.setContrasenia(contrasenia);
        }
        if (rol != null) {
            u.setRol(rol);
        }

        return u;
    }

    public void eliminarUsuario(Long id) throws EntidadNoEncontradaException {
        Usuario u = buscarPorId(id);
        u.setEliminado(true);
    }

    private void validarDatosBasicos(String nombre, String apellido, String mail, String contrasenia) throws NegocioException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new NegocioException("El nombre del usuario no puede estar vacío.");
        }
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new NegocioException("El apellido del usuario no puede estar vacío.");
        }
        if (mail == null || mail.trim().isEmpty()) {
            throw new NegocioException("El correo electrónico del usuario no puede estar vacío.");
        }
        if (!mail.trim().contains("@") || !mail.trim().contains(".")) {
            throw new NegocioException("Formato de correo electrónico inválido (debe contener '@' y '.').");
        }
        if (contrasenia == null || contrasenia.isEmpty()) {
            throw new NegocioException("La contraseña no puede estar vacía.");
        }
    }

    private void validarMailUnico(String mail, Long idUsuarioExistente) throws NegocioException {
        String cleanMail = mail.trim().toLowerCase();
        for (Usuario u : dataStore.getUsuarios()) {
            if (!u.isEliminado() && u.getMail().equals(cleanMail)) {
                if (idUsuarioExistente == null || !u.getId().equals(idUsuarioExistente)) {
                    throw new NegocioException("Ya existe un usuario activo registrado con el correo: " + mail);
                }
            }
        }
    }
}
