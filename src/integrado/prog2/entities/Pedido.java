package integrado.prog2.entities;

import integrado.prog2.enums.Estado;
import integrado.prog2.enums.FormaPago;
import integrado.prog2.interfaces.Calculable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Pedido extends Base implements Calculable {
    private LocalDate fecha;
    private Estado estado;
    private Double total;
    private FormaPago formaPago;
    private Usuario usuario;
    private List<DetallePedido> detalles;

    public Pedido() {
        super();
        this.fecha = LocalDate.now();
        this.estado = Estado.PENDIENTE;
        this.total = 0.0;
        this.detalles = new ArrayList<>();
    }

    public Pedido(Usuario usuario, FormaPago formaPago) {
        this();
        this.usuario = usuario;
        this.formaPago = formaPago;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public FormaPago getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(FormaPago formaPago) {
        this.formaPago = formaPago;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles = detalles;
    }

    
    public void addDetallePedido(int cantidad, Double subtotal, Producto producto) {
        DetallePedido detalle = new DetallePedido(cantidad, subtotal, producto);
        detalle.setId((long) (detalles.size() + 1));
        detalles.add(detalle);
    }

    
    public DetallePedido findeDetallePedidoByProducto(Producto producto) {
        for (DetallePedido detalle : detalles) {
            if (detalle.getProducto() != null && detalle.getProducto().equals(producto)) {
                return detalle;
            }
        }
        return null;
    }

    
    public void deleteDetallePedidoByProducto(Producto producto) {
        DetallePedido toRemove = findeDetallePedidoByProducto(producto);
        if (toRemove != null) {
            detalles.remove(toRemove);
        }
    }

    @Override
    public void calcularTotal() {
        double sum = 0.0;
        for (DetallePedido detalle : detalles) {
            sum += (detalle.getSubtotal() != null) ? detalle.getSubtotal() : 0.0;
        }
        this.total = sum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pedido)) return false;
        Pedido pedido = (Pedido) o;
        return Objects.equals(getId(), pedido.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        String usrNombre = (usuario != null) ? (usuario.getNombre() + " " + usuario.getApellido()) : "Anónimo";
        return String.format("Pedido [ID: %d] - Fecha: %s | Cliente: %s | Estado: %s | Pago: %s | Total: $%.2f | Detalles: %d",
                getId(), fecha, usrNombre, estado, formaPago, total, detalles.size());
    }
}
