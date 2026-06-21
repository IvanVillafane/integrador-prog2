package integrado.prog2.config;

import integrado.prog2.entities.*;
import integrado.prog2.enums.*;
import integrado.prog2.service.PedidoService;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonUtil {

    public static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public static String categoriasToJson(List<Categoria> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            Categoria c = list.get(i);
            sb.append(String.format("{\"id\":%d,\"nombre\":\"%s\",\"descripcion\":\"%s\"}",
                    c.getId(), escape(c.getNombre()), escape(c.getDescripcion())));
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public static String productosToJson(List<Producto> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            Producto p = list.get(i);
            String catName = p.getCategoria() != null ? p.getCategoria().getNombre() : "Ninguna";
            Long catId = p.getCategoria() != null ? p.getCategoria().getId() : 0L;
            sb.append(String.format(java.util.Locale.US, "{\"id\":%d,\"nombre\":\"%s\",\"precio\":%.2f,\"descripcion\":\"%s\",\"stock\":%d,\"imagen\":\"%s\",\"disponible\":%b,\"categoriaId\":%d,\"categoriaNombre\":\"%s\"}",
                    p.getId(), escape(p.getNombre()), p.getPrecio(), escape(p.getDescripcion()),
                    p.getStock(), escape(p.getImagen()), p.getDisponible(), catId, escape(catName)));
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public static String usuariosToJson(List<Usuario> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            Usuario u = list.get(i);
            sb.append(String.format("{\"id\":%d,\"nombre\":\"%s\",\"apellido\":\"%s\",\"mail\":\"%s\",\"celular\":\"%s\",\"rol\":\"%s\"}",
                    u.getId(), escape(u.getNombre()), escape(u.getApellido()), escape(u.getMail()),
                    escape(u.getCelular()), u.getRol().name()));
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public static String detallePedidoToJson(DetallePedido d) {
        String prodName = d.getProducto() != null ? d.getProducto().getNombre() : "Producto Desconocido";
        Long prodId = d.getProducto() != null ? d.getProducto().getId() : 0L;
        return String.format(java.util.Locale.US, "{\"id\":%d,\"cantidad\":%d,\"subtotal\":%.2f,\"productoId\":%d,\"productoNombre\":\"%s\"}",
                d.getId(), d.getCantidad(), d.getSubtotal(), prodId, escape(prodName));
    }

    public static String pedidoToJson(Pedido p) {
        String usrName = p.getUsuario() != null ? (p.getUsuario().getNombre() + " " + p.getUsuario().getApellido()) : "Anónimo";
        Long usrId = p.getUsuario() != null ? p.getUsuario().getId() : 0L;

        StringBuilder detSb = new StringBuilder("[");
        List<DetallePedido> detalles = p.getDetalles();
        for (int i = 0; i < detalles.size(); i++) {
            detSb.append(detallePedidoToJson(detalles.get(i)));
            if (i < detalles.size() - 1) detSb.append(",");
        }
        detSb.append("]");

        return String.format(java.util.Locale.US, "{\"id\":%d,\"fecha\":\"%s\",\"estado\":\"%s\",\"total\":%.2f,\"formaPago\":\"%s\",\"usuarioId\":%d,\"usuarioNombre\":\"%s\",\"detalles\":%s}",
                p.getId(), p.getFecha().toString(), p.getEstado().name(), p.getTotal(),
                p.getFormaPago().name(), usrId, escape(usrName), detSb.toString());
    }

    public static String pedidosToJson(List<Pedido> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append(pedidoToJson(list.get(i)));
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public static class ParsedPedidoRequest {
        public Long usuarioId;
        public FormaPago formaPago;
        public List<PedidoService.ItemPedido> items = new ArrayList<>();
    }

    public static ParsedPedidoRequest parsePedidoRequest(String json) throws Exception {
        ParsedPedidoRequest req = new ParsedPedidoRequest();

        Pattern pUser = Pattern.compile("\"usuarioId\"\\s*:\\s*(\\d+)");
        Matcher mUser = pUser.matcher(json);
        if (mUser.find()) {
            req.usuarioId = Long.parseLong(mUser.group(1));
        } else {
            throw new Exception("El campo 'usuarioId' es obligatorio y debe ser numérico.");
        }

        Pattern pPago = Pattern.compile("\"formaPago\"\\s*:\\s*\"([^\"]+)\"");
        Matcher mPago = pPago.matcher(json);
        if (mPago.find()) {
            String fpStr = mPago.group(1).toUpperCase();
            try {
                req.formaPago = FormaPago.valueOf(fpStr);
            } catch (IllegalArgumentException e) {
                throw new Exception("Forma de pago inválida: " + fpStr);
            }
        } else {
            req.formaPago = FormaPago.EFECTIVO;
        }

        Pattern pItem = Pattern.compile("\\{\\s*\"productoId\"\\s*:\\s*(\\d+)\\s*,\\s*\"cantidad\"\\s*:\\s*(\\d+)\\s*\\}");
        Matcher mItem = pItem.matcher(json);
        while (mItem.find()) {
            Long prodId = Long.parseLong(mItem.group(1));
            int cant = Integer.parseInt(mItem.group(2));
            req.items.add(new PedidoService.ItemPedido(prodId, cant));
        }

        if (req.items.isEmpty()) {
            throw new Exception("El pedido debe contener al menos un producto en la lista 'items'.");
        }

        return req;
    }

    public static class ParsedCategoriaRequest {
        public String nombre;
        public String descripcion;
    }

    public static ParsedCategoriaRequest parseCategoriaRequest(String json) throws Exception {
        ParsedCategoriaRequest req = new ParsedCategoriaRequest();
        
        Pattern pNombre = Pattern.compile("\"nombre\"\\s*:\\s*\"([^\"]+)\"");
        Matcher mNombre = pNombre.matcher(json);
        if (mNombre.find()) {
            req.nombre = mNombre.group(1);
        } else {
            throw new Exception("El campo 'nombre' es obligatorio.");
        }

        Pattern pDesc = Pattern.compile("\"descripcion\"\\s*:\\s*\"([^\"]+)\"");
        Matcher mDesc = pDesc.matcher(json);
        if (mDesc.find()) {
            req.descripcion = mDesc.group(1);
        } else {
            throw new Exception("El campo 'descripcion' es obligatorio.");
        }

        return req;
    }

    public static class ParsedProductoRequest {
        public String nombre;
        public Double precio;
        public String descripcion;
        public Integer stock;
        public String imagen;
        public Boolean disponible;
        public Long categoriaId;
    }

    public static ParsedProductoRequest parseProductoRequest(String json) throws Exception {
        ParsedProductoRequest req = new ParsedProductoRequest();

        Pattern pNombre = Pattern.compile("\"nombre\"\\s*:\\s*\"([^\"]+)\"");
        Matcher mNombre = pNombre.matcher(json);
        if (mNombre.find()) {
            req.nombre = mNombre.group(1);
        } else {
            throw new Exception("El campo 'nombre' es obligatorio.");
        }

        Pattern pPrecio = Pattern.compile("\"precio\"\\s*:\\s*(\\d+(?:\\.\\d+)?)");
        Matcher mPrecio = pPrecio.matcher(json);
        if (mPrecio.find()) {
            req.precio = Double.parseDouble(mPrecio.group(1));
        } else {
            throw new Exception("El precio es obligatorio y debe ser numérico.");
        }

        Pattern pDesc = Pattern.compile("\"descripcion\"\\s*:\\s*\"([^\"]*)\"");
        Matcher mDesc = pDesc.matcher(json);
        if (mDesc.find()) {
            req.descripcion = mDesc.group(1);
        } else {
            req.descripcion = "";
        }

        Pattern pStock = Pattern.compile("\"stock\"\\s*:\\s*(\\d+)");
        Matcher mStock = pStock.matcher(json);
        if (mStock.find()) {
            req.stock = Integer.parseInt(mStock.group(1));
        } else {
            req.stock = 0;
        }

        Pattern pImg = Pattern.compile("\"imagen\"\\s*:\\s*\"([^\"]*)\"");
        Matcher mImg = pImg.matcher(json);
        if (mImg.find()) {
            req.imagen = mImg.group(1);
        } else {
            req.imagen = "";
        }

        Pattern pDisp = Pattern.compile("\"disponible\"\\s*:\\s*(true|false)");
        Matcher mDisp = pDisp.matcher(json);
        if (mDisp.find()) {
            req.disponible = Boolean.parseBoolean(mDisp.group(1));
        } else {
            req.disponible = true;
        }

        Pattern pCat = Pattern.compile("\"categoriaId\"\\s*:\\s*(\\d+)");
        Matcher mCat = pCat.matcher(json);
        if (mCat.find()) {
            req.categoriaId = Long.parseLong(mCat.group(1));
        } else {
            throw new Exception("El campo 'categoriaId' es obligatorio.");
        }

        return req;
    }

    public static class ParsedStatusRequest {
        public Long pedidoId;
        public Estado estado;
    }

    public static ParsedStatusRequest parseStatusRequest(String json) throws Exception {
        ParsedStatusRequest req = new ParsedStatusRequest();

        Pattern pId = Pattern.compile("\"pedidoId\"\\s*:\\s*(\\d+)");
        Matcher mId = pId.matcher(json);
        if (mId.find()) {
            req.pedidoId = Long.parseLong(mId.group(1));
        } else {
            throw new Exception("El campo 'pedidoId' es obligatorio.");
        }

        Pattern pEstado = Pattern.compile("\"estado\"\\s*:\\s*\"([^\"]+)\"");
        Matcher mEstado = pEstado.matcher(json);
        if (mEstado.find()) {
            String estStr = mEstado.group(1).toUpperCase();
            try {
                req.estado = Estado.valueOf(estStr);
            } catch (IllegalArgumentException e) {
                throw new Exception("Estado inválido: " + estStr);
            }
        } else {
            throw new Exception("El campo 'estado' es obligatorio.");
        }

        return req;
    }
}
