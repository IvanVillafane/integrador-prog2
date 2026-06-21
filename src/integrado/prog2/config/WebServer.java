package integrado.prog2.config;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import integrado.prog2.service.*;
import integrado.prog2.entities.*;
import integrado.prog2.enums.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class WebServer {
    private static final CategoriaService categoriaService = new CategoriaService();
    private static final ProductoService productoService = new ProductoService();
    private static final UsuarioService usuarioService = new UsuarioService();
    private static final PedidoService pedidoService = new PedidoService();

    public static void start(int port) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

            server.createContext("/api/categorias", new ApiCategoriasHandler());
            server.createContext("/api/productos", new ApiProductosHandler());
            server.createContext("/api/usuarios", new ApiUsuariosHandler());
            server.createContext("/api/pedidos/status", new ApiPedidosStatusHandler());
            server.createContext("/api/pedidos", new ApiPedidosHandler());
            server.createContext("/", new StaticFilesHandler());

            server.setExecutor(null);
            server.start();
            System.out.println("\n==================================================");
            System.out.println("  FOOD STORE - API WEB Y INTERFAZ GRÁFICA ACTIVA");
            System.out.println("  👉 http://localhost:" + port + "/");
            System.out.println("==================================================\n");
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor web: " + e.getMessage());
        }
    }

    private static void sendJsonResponse(HttpExchange exchange, int statusCode, String jsonResponse) throws IOException {
        byte[] bytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void handleOptions(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.sendResponseHeaders(204, -1);
    }

    private static class ApiCategoriasHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                handleOptions(exchange);
                return;
            }
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                List<Categoria> list = categoriaService.listarCategorias();
                sendJsonResponse(exchange, 200, JsonUtil.categoriasToJson(list));
            } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                try {
                    InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                    BufferedReader br = new BufferedReader(isr);
                    StringBuilder bodySb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        bodySb.append(line);
                    }
                    String requestBody = bodySb.toString();

                    JsonUtil.ParsedCategoriaRequest req = JsonUtil.parseCategoriaRequest(requestBody);
                    Categoria c = categoriaService.crearCategoria(req.nombre, req.descripcion);
                    sendJsonResponse(exchange, 201, String.format("{\"id\":%d,\"nombre\":\"%s\",\"descripcion\":\"%s\"}",
                            c.getId(), JsonUtil.escape(c.getNombre()), JsonUtil.escape(c.getDescripcion())));
                } catch (Exception e) {
                    sendJsonResponse(exchange, 400, "{\"error\":\"" + JsonUtil.escape(e.getMessage()) + "\"}");
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    private static class ApiProductosHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                handleOptions(exchange);
                return;
            }
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                Long catId = null;
                if (query != null && query.contains("categoriaId=")) {
                    try {
                        String[] parts = query.split("categoriaId=");
                        if (parts.length > 1) {
                            catId = Long.parseLong(parts[1].split("&")[0]);
                        }
                    } catch (Exception ignored) {}
                }

                try {
                    List<Producto> list;
                    if (catId != null) {
                        list = productoService.listarProductosPorCategoria(catId);
                    } else {
                        list = productoService.listarProductos();
                    }
                    sendJsonResponse(exchange, 200, JsonUtil.productosToJson(list));
                } catch (Exception e) {
                    sendJsonResponse(exchange, 400, "{\"error\":\"" + JsonUtil.escape(e.getMessage()) + "\"}");
                }
            } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                try {
                    InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                    BufferedReader br = new BufferedReader(isr);
                    StringBuilder bodySb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        bodySb.append(line);
                    }
                    String requestBody = bodySb.toString();

                    JsonUtil.ParsedProductoRequest req = JsonUtil.parseProductoRequest(requestBody);
                    Producto p = productoService.crearProducto(req.nombre, req.precio, req.descripcion, req.stock, req.imagen, req.disponible, req.categoriaId);
                    sendJsonResponse(exchange, 201, String.format(java.util.Locale.US, "{\"id\":%d,\"nombre\":\"%s\",\"precio\":%.2f}",
                            p.getId(), JsonUtil.escape(p.getNombre()), p.getPrecio()));
                } catch (Exception e) {
                    sendJsonResponse(exchange, 400, "{\"error\":\"" + JsonUtil.escape(e.getMessage()) + "\"}");
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    private static class ApiUsuariosHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                handleOptions(exchange);
                return;
            }
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                List<Usuario> list = usuarioService.listarUsuarios();
                sendJsonResponse(exchange, 200, JsonUtil.usuariosToJson(list));
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    private static class ApiPedidosHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                handleOptions(exchange);
                return;
            }

            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                Long usuarioId = null;
                if (query != null && query.contains("usuarioId=")) {
                    try {
                        String[] parts = query.split("usuarioId=");
                        if (parts.length > 1) {
                            usuarioId = Long.parseLong(parts[1].split("&")[0]);
                        }
                    } catch (Exception ignored) {}
                }

                try {
                    List<Pedido> list;
                    if (usuarioId != null) {
                        list = pedidoService.listarPedidosPorUsuario(usuarioId);
                    } else {
                        list = pedidoService.listarPedidos();
                    }
                    sendJsonResponse(exchange, 200, JsonUtil.pedidosToJson(list));
                } catch (Exception e) {
                    sendJsonResponse(exchange, 400, "{\"error\":\"" + JsonUtil.escape(e.getMessage()) + "\"}");
                }
            } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                try {
                    InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                    BufferedReader br = new BufferedReader(isr);
                    StringBuilder bodySb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        bodySb.append(line);
                    }
                    String requestBody = bodySb.toString();

                    JsonUtil.ParsedPedidoRequest req = JsonUtil.parsePedidoRequest(requestBody);
                    Pedido p = pedidoService.crearPedido(req.usuarioId, req.formaPago, req.items);
                    sendJsonResponse(exchange, 201, JsonUtil.pedidoToJson(p));
                } catch (Exception e) {
                    sendJsonResponse(exchange, 400, "{\"error\":\"" + JsonUtil.escape(e.getMessage()) + "\"}");
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    private static class ApiPedidosStatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                handleOptions(exchange);
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                try {
                    InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                    BufferedReader br = new BufferedReader(isr);
                    StringBuilder bodySb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        bodySb.append(line);
                    }
                    String requestBody = bodySb.toString();

                    JsonUtil.ParsedStatusRequest req = JsonUtil.parseStatusRequest(requestBody);
                    Pedido p = pedidoService.actualizarEstadoPago(req.pedidoId, req.estado, null);
                    sendJsonResponse(exchange, 200, JsonUtil.pedidoToJson(p));
                } catch (Exception e) {
                    sendJsonResponse(exchange, 400, "{\"error\":\"" + JsonUtil.escape(e.getMessage()) + "\"}");
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    private static class StaticFilesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String pathStr = exchange.getRequestURI().getPath();
            if (pathStr.equals("/")) {
                pathStr = "/index.html";
            }

            Path filePath = Paths.get("frontend", pathStr.substring(1)).toAbsolutePath().normalize();
            Path allowedDir = Paths.get("frontend").toAbsolutePath().normalize();

            if (!filePath.startsWith(allowedDir) || !Files.exists(filePath) || Files.isDirectory(filePath)) {
                String responseText = "404 Not Found";
                exchange.sendResponseHeaders(404, responseText.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseText.getBytes(StandardCharsets.UTF_8));
                }
                return;
            }

            String contentType = "text/plain";
            String fileName = filePath.getFileName().toString().toLowerCase();
            if (fileName.endsWith(".html")) {
                contentType = "text/html; charset=utf-8";
            } else if (fileName.endsWith(".css")) {
                contentType = "text/css; charset=utf-8";
            } else if (fileName.endsWith(".js")) {
                contentType = "application/javascript; charset=utf-8";
            } else if (fileName.endsWith(".png")) {
                contentType = "image/png";
            } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (fileName.endsWith(".svg")) {
                contentType = "image/svg+xml";
            }

            byte[] fileBytes = Files.readAllBytes(filePath);
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, fileBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(fileBytes);
            }
        }
    }
}
