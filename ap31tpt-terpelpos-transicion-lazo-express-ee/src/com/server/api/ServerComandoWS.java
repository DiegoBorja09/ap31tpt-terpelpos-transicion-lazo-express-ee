package com.server.api;

import com.WT2.appTerpel.presentation.PaymentHttpHandler;
import com.application.useCases.grupos.BuscarCategoriasVisiblesUseCase;
import com.application.useCases.persons.FindPersonaByIdUseCase;
import com.application.useCases.persons.RegistrarTagUseCase;
import com.application.useCases.productos.GetProductosPorCategoriaUseCase;
import com.application.useCases.productos.GetProductosPorDescripcionUseCase;
import com.application.useCases.sutidores.HabilitarBotonesFamiliaAutorizacionUseCase;
import com.bean.CatalogoBean;
import com.bean.MovimientosBean;
import com.bean.PersonaBean;
import com.bean.ProductoBean;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.controllers.SetupAsync;
import com.dao.DAOException;
import com.dao.EquipoDao;
import com.dao.MovimientosDao;
import com.dao.PersonasDao;
import com.dao.SetupDao;
import com.firefuel.EstadoNotificacionesViewController;
import com.firefuel.EstadoRfidViewController;
import com.firefuel.EstadoSurtidorViewController;
import com.firefuel.InfoViewController;
import com.firefuel.Main;
import com.firefuel.RegistroIButtonViewController;
import com.firefuel.StoreConfirmarViewController;
import com.firefuel.TurnosFinalizarViewController;
import static com.firefuel.Utils.setTimeout;
import com.firefuel.VentaCursoPlaca;
import com.firefuel.VentaPredefinirPlaca;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.neo.app.bean.ErrorResponse;
import com.neo.app.bean.Utils;
import com.neo.app.notificacion.Notificacion;
import com.neo.print.services.PrinterFacade;
import com.server.api.filter.AuthorizationFilter;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.infrastructure.cache.ProductoUpdateInterceptorLiviano;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerComandoWS extends Thread{

    public static boolean iniciado = false;
    AuthorizationFilter filters = new AuthorizationFilter();
    Gson gson = new Gson();
    SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_FULL_DATE_ISO);
    SimpleDateFormat sdfSQL = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
    SimpleDateFormat sdf2 = new SimpleDateFormat(NovusConstante.FORMAT_PROCESS);
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);
    public TreeMap<Integer, String> ESTADO_SURTIDOR;
    final static String procesoId = "proceso_id";
    final static String iniciadoId = "iniciado";
    final static String productoId = "productoId";
    final static String bodega = "bodega";
    final static String productos = "productos";
    final static String id = "id";
    SetupDao sdao = new SetupDao();
    EquipoDao equipoDao = new EquipoDao();
    private HabilitarBotonesFamiliaAutorizacionUseCase habilitarBotonesFamiliaAutorizacionUseCase;
    
    // Interceptor para invalidación de cache por comandos remotos
    private ProductoUpdateInterceptorLiviano productoUpdateInterceptor;

    //Retorno del cambio de AsyncExecutor a Thread
    @Override
    public void run() {
        NovusUtils.printLn("INICIANDO SERVER WS " + NovusConstante.PORT_SERVER_WS_API);
        try {
            init();
        } catch (Exception ex) {
            Logger.getLogger(ServerComandoWS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void init() {
        try {
            this.habilitarBotonesFamiliaAutorizacionUseCase = new HabilitarBotonesFamiliaAutorizacionUseCase();
            
            // Inicializar interceptor para invalidación de cache por comandos remotos
            try {
                this.productoUpdateInterceptor = new ProductoUpdateInterceptorLiviano();
                NovusUtils.printLn(" Interceptor cache inicializado en ServerComandoWS");
            } catch (Exception e) {
                NovusUtils.printLn(" No se pudo inicializar interceptor cache en ServerComandoWS: " + e.getMessage());
                this.productoUpdateInterceptor = null;
            }
            
            NovusUtils.printLn("INICIANDO CONFIGURACION DEL WEBSERVER");
            // INFORMATIVA
            ESTADO_SURTIDOR = new TreeMap<>();
            ESTADO_SURTIDOR.put(0, "CERRADO");
            ESTADO_SURTIDOR.put(1, "ESPERA");
            ESTADO_SURTIDOR.put(2, "DESCOLGADA");
            ESTADO_SURTIDOR.put(3, "DESPACHO");
            ESTADO_SURTIDOR.put(4, "FIN DE VENTA");
            ESTADO_SURTIDOR.put(5, "ERROR");
            ESTADO_SURTIDOR.put(6, "NO DETERMINADO");
            ESTADO_SURTIDOR.put(7, "DETENIDO");
            ESTADO_SURTIDOR.put(8, "SALTO DE LECTURA");

            HttpServer server = HttpServer.create(new InetSocketAddress(NovusConstante.PORT_SERVER_WS_API), 0);
            iniciado = true;

            server.createContext("/api/ping", new HttpHandlerPing());
            server.createContext("/api", new HttpHandlerApi());
            server.createContext("/passportx.evento/estadoSurtidorEvento", new HttpHandlerSurtidorEstado());
            server.createContext("/api/notificaciones", new HttpHandlerNotificaciones());
            server.createContext("/api/dispositivosnotificaciones", new HttpHandlerdispositivosNotificaciones());
            server.createContext("/api/identificadorPromotor", new HttpHandlerIdentificadorEmpleado());
            server.createContext("/api/registroIdentificacion", new HttpHandlerRegistroIdentificacion());
            server.createContext("/passportx.evento/transaccionVentaEvento", new HttpHandlerFinVenta());
            server.createContext("/passportx.evento/sincronizar", new HttpHandlerSincronizacion());
            server.createContext("/api/conexionSurtidor", new HttpHandlerSurtidorConectado());
            server.createContext("/api/informarVentasRetenidas", new HttpHandlerVentasRetenidas());
            server.createContext("/api/informarVentasFE", new HttpHandlerVentasFE());

            server.createContext("/api/productos", new HttpHandlerProductos());
            server.createContext("/api/categorias", new HttpHandlerCategorias());
            server.createContext("/api/productos/categoria", new HttpHandlerProductosCategoria());
            server.createContext("/api/ventaexpress", new HttpHandlerVentaExpress());
            server.createContext("/api/errorNotificaciones", new HttpHandlerNotifications());
            server.createContext("/api/payments/notification", new PaymentHttpHandler());
            server.createContext("/passportx.evento/invalidarCache", new HttpHandlerInvalidarCache());
            server.setExecutor(null);
            server.start();

        } catch (java.net.BindException ex) {
            NovusUtils.printLn("ERROR PUERTO OCUPADO SERVER WS " + NovusConstante.PORT_SERVER_WS_API);

            Logger.getLogger(ServerComandoWS.class.getName()).log(Level.SEVERE, null, ex);
            if (Main.info != null) {
                Main.info.setVisible(false);
                Main.info = null;
                NovusUtils.printLn("DESTRUYENDO INSTANCIA LOCAL DE INFO");
            }
            System.exit(0);
        } catch (Exception ex) {
            NovusUtils.printLn("ERROR INSTACIA SERVER WS " + NovusConstante.PORT_SERVER_WS_API);
            Logger.getLogger(ServerComandoWS.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }

    class HttpHandlerInvalidarCache implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            long startTime = System.currentTimeMillis();
            String clientIP = t.getRemoteAddress().getAddress().getHostAddress();
            String method = t.getRequestMethod();
            
            try {
                NovusUtils.printLn("[API INVALIDAR CACHE] ===== CONSUMO INICIADO =====");
                NovusUtils.printLn("Cliente IP: " + clientIP);
                NovusUtils.printLn("Método HTTP: " + method);
                NovusUtils.printLn("Timestamp: " + new java.util.Date());
                
                // Leer request body
                InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                int b;
                StringBuilder buf = new StringBuilder();
                while ((b = br.read()) != -1) {
                    buf.append((char) b);
                }

                NovusUtils.printLn(" Request body recibido: " + (buf.length() > 0 ? buf.toString() : "[VACÍO]"));
                
                // Invalidar cache usando el interceptor
                NovusUtils.printLn(" Interceptor disponible: " + (productoUpdateInterceptor != null ? "SÍ" : "NO"));
                
                if (productoUpdateInterceptor != null) {
                    try {
                        NovusUtils.printLn(" [PROCESANDO] Obteniendo instancia del cache...");
                        // Obtener instancia del cache
                        com.infrastructure.cache.KioscoCacheServiceLiviano cache = 
                            com.infrastructure.cache.KioscoCacheServiceLiviano.getInstance();
                        
                        // Procesar request JSON
                        NovusUtils.printLn(" [PROCESANDO] Analizando request JSON...");
                        if (buf.length() > 0) {
                            try {
                                JsonObject request = gson.fromJson(buf.toString(), JsonObject.class);
                                
                                // SINCRONIZACIÓN DESDE HO - COMPORTAMIENTO CONFIGURABLE
                                if (request.has("productos_ids")) {
                                    JsonArray productosIds = request.getAsJsonArray("productos_ids");
                                    
                                    //  OPCIÓN A: Sincronización selectiva (comportamiento anterior)
                                    boolean sincronizacionSelectiva = request.has("modo") && 
                                        "selectivo".equals(request.get("modo").getAsString());
                                    
                                    if (sincronizacionSelectiva) {
                                        NovusUtils.printLn("[SINCRONIZACIÓN SELECTIVA] Procesando productos específicos - Cantidad: " + productosIds.size());
                                        
                                        for (JsonElement elemento : productosIds) {
                                            Long productoId = elemento.getAsLong();
                                            NovusUtils.printLn("    Sincronizando producto ID: " + productoId);
                                            cache.sincronizarProductoDesdeHO(productoId);
                                        }
                                        
                                        NovusUtils.printLn(" [COMPLETADO] " + productosIds.size() + " productos sincronizados selectivamente");
                                        
                                    } else {
                                        // OPCIÓN B: Sincronización estilo PRODUCTOS STORE (IGUAL QUE LA INTERFAZ)
                                        NovusUtils.printLn(" [SINCRONIZACIÓN MASIVA] Comportamiento PRODUCTOS STORE - Invalidando cache completo y precargando");
                                        NovusUtils.printLn("    Productos recibidos del HO: " + productosIds.size());
                                        
                                        // PASO 1: Invalidar todo el cache (igual que hace la interfaz)
                                        NovusUtils.printLn("    PASO 1: Invalidando cache completo...");
                                        cache.limpiarCacheCompleto();
                                        
                                        // PASO 2: Pre-cargar productos populares (igual que hace la interfaz)
                                        NovusUtils.printLn("   PASO 2: Pre-cargando productos populares...");
                                        cache.obtenerProductosPopularesConCache(1000);
                                        
                                        // PASO 3: Pre-cargar categorías (igual que hace la interfaz)
                                        NovusUtils.printLn("    PASO 3: Pre-cargando categorías...");
                                        cache.obtenerCategoriasConCache();
                                        
                                        NovusUtils.printLn(" [COMPLETADO] Cache actualizado estilo PRODUCTOS STORE - Cache limpio y pre-cargado");
                                    }
                                    
                                // Opción 2: Limpiar cache completo
                                } else if (request.has("action") && "invalidate_all".equals(request.get("action").getAsString())) {
                                    NovusUtils.printLn(" [OPCIÓN 2] Limpiar cache completo iniciado...");
                                    cache.limpiarCacheCompleto();
                                    NovusUtils.printLn(" [COMPLETADO] Cache completo invalidado vía HTTP");
                                    
                                // Opción 3: Actualizar productos populares
                                } else if (request.has("action") && "refresh_populares".equals(request.get("action").getAsString())) {
                                    NovusUtils.printLn("[OPCIÓN 3] Refrescar productos populares iniciado...");
                                    cache.invalidarCachesTipo("productos_populares");
                                    NovusUtils.printLn(" [COMPLETADO] Cache de productos populares invalidado - se recargará automáticamente");
                                    
                                } else {
                                    NovusUtils.printLn(" [OPCIÓN DEFAULT] Request sin parámetros específicos - Limpiar todo...");
                                    cache.limpiarCacheCompleto();
                                    NovusUtils.printLn(" [COMPLETADO] Cache completo invalidado vía HTTP (sin parámetros)");
                                }
                                
                            } catch (Exception jsonError) {
                                // Si no es JSON válido, limpiar todo (compatibilidad)
                                NovusUtils.printLn(" [ERROR JSON] JSON inválido - Aplicando fallback...");
                                NovusUtils.printLn("   Error: " + jsonError.getMessage());
                                cache.limpiarCacheCompleto();
                                NovusUtils.printLn(" [COMPLETADO] Cache completo invalidado vía HTTP (JSON inválido)");
                            }
                        } else {
                            // Request vacío = limpiar todo
                            NovusUtils.printLn(" [REQUEST VACÍO] Sin contenido - Aplicando limpieza completa...");
                            cache.limpiarCacheCompleto();
                            NovusUtils.printLn(" [COMPLETADO] Cache completo invalidado vía HTTP (request vacío)");
                        }
                        
                        // Respuesta exitosa
                        long processingTime = System.currentTimeMillis() - startTime;
                        String response = "{\"status\":\"success\",\"message\":\"Cache invalidado exitosamente\",\"processing_time_ms\":" + processingTime + "}";
                        
                        NovusUtils.printLn(" [ENVIANDO RESPUESTA] Status: 200 OK");
                        t.getResponseHeaders().add("Content-Type", "application/json");
                        t.sendResponseHeaders(200, response.getBytes().length);
                        t.getResponseBody().write(response.getBytes());
                        t.getResponseBody().close();
                        
                        NovusUtils.printLn(" [API INVALIDAR CACHE] ===== CONSUMO TERMINADO EXITOSAMENTE =====");
                        NovusUtils.printLn(" Tiempo total de procesamiento: " + processingTime + " ms");
                        
                    } catch (Exception e) {
                        long processingTime = System.currentTimeMillis() - startTime;
                        NovusUtils.printLn("[ERROR INTERCEPTOR] " + e.getMessage());
                        e.printStackTrace();
                        
                        String errorResponse = "{\"status\":\"error\",\"message\":\"Error invalidando cache: " + e.getMessage().replace("\"", "'") + "\",\"processing_time_ms\":" + processingTime + "}";
                        t.getResponseHeaders().add("Content-Type", "application/json");
                        t.sendResponseHeaders(500, errorResponse.getBytes().length);
                        t.getResponseBody().write(errorResponse.getBytes());
                        t.getResponseBody().close();
                        
                        NovusUtils.printLn(" [API INVALIDAR CACHE] ===== CONSUMO TERMINADO CON ERROR =====");
                        NovusUtils.printLn(" Tiempo total: " + processingTime + " ms");
                    }
                } else {
                    long processingTime = System.currentTimeMillis() - startTime;
                    NovusUtils.printLn(" [SIN INTERCEPTOR] Servicio no disponible");
                    
                    String errorResponse = "{\"status\":\"error\",\"message\":\"Cache service no disponible\",\"processing_time_ms\":" + processingTime + "}";
                    t.getResponseHeaders().add("Content-Type", "application/json");
                    t.sendResponseHeaders(503, errorResponse.getBytes().length);
                    t.getResponseBody().write(errorResponse.getBytes());
                    t.getResponseBody().close();
                    
                    NovusUtils.printLn(" [API INVALIDAR CACHE] ===== CONSUMO TERMINADO - SERVICIO NO DISPONIBLE =====");
                    NovusUtils.printLn(" Tiempo total: " + processingTime + " ms");
                }
                
            } catch (Exception e) {
                long processingTime = System.currentTimeMillis() - startTime;
                NovusUtils.printLn(" [ERROR FATAL] " + e.getMessage());
                e.printStackTrace();
                
                String errorResponse = "{\"status\":\"error\",\"message\":\"Error procesando request: " + e.getMessage().replace("\"", "'") + "\",\"processing_time_ms\":" + processingTime + "}";
                t.getResponseHeaders().add("Content-Type", "application/json");
                t.sendResponseHeaders(500, errorResponse.getBytes().length);
                t.getResponseBody().write(errorResponse.getBytes());
                t.getResponseBody().close();
                
                NovusUtils.printLn(" [API INVALIDAR CACHE] ===== CONSUMO TERMINADO CON ERROR FATAL =====");
                NovusUtils.printLn(" Tiempo total: " + processingTime + " ms");
            }
        }
    }

    class HttpHandlerVentasRetenidas implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            String method = t.getRequestMethod();
            if (method.equalsIgnoreCase(NovusConstante.POST)) {
                InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                int b;
                StringBuilder buf = new StringBuilder();
                while ((b = br.read()) != -1) {
                    buf.append((char) b);
                }

                JsonObject request = gson.fromJson(buf.toString(), JsonObject.class);
                try {
                    int numeroVentas = request.get("numeroVentas") != null && !request.get("numeroVentas").isJsonNull()
                            ? request.get("numeroVentas").getAsInt()
                            : 0;
                    boolean sincronizado = request.get("sincronizado") != null
                            ? request.get("sincronizado").getAsBoolean()
                            : false;
                    InfoViewController.renderVentasRetenidas(numeroVentas, sincronizado);
                    JsonObject json = new JsonObject();
                    json.addProperty("version_codigo", Main.VERSION_CODE);
                    json.addProperty("version_nombre", Main.VERSION_APP);
                    json.addProperty("equipo_id", Main.credencial.getEquipos_id());
                    json.addProperty("empresa_id", Main.credencial.getEmpresas_id());
                    json.addProperty("fechaProceso", sdf.format(new Date()));

                    String response = json.toString();
                    Headers respHeaders = t.getResponseHeaders();
                    respHeaders.add("content-type", "application/json");
                    t.sendResponseHeaders(200, response.getBytes("utf-8").length);
                    try (OutputStream os = t.getResponseBody()) {
                        os.write(response.getBytes());
                        os.flush();
                    }
                } catch (Exception e) {
                    NovusUtils.printLn(e.getMessage());
                }
            } else {
                ErrorResponse error = new ErrorResponse();
                error.setTipo("negocio");
                error.setStatusCode(ErrorResponse.SC_METHOD_NOT_ALLOWED);
                error.setCodigo(ErrorResponse.ERROR_40500_ID);
                error.setMensaje(ErrorResponse.ERROR_40500_DESC);
                error.setFechaProceso(new Date().toString());
                Utils.responseError(t, error);
            }
        }
    }

    class HttpHandlerVentasFE implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            String method = t.getRequestMethod();
            if (method.equalsIgnoreCase(NovusConstante.POST)) {
                InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                int b;
                StringBuilder buf = new StringBuilder();
                while ((b = br.read()) != -1) {
                    buf.append((char) b);
                }
                JsonObject resquest = gson.fromJson(buf.toString(), JsonObject.class);
                int datafono = 0;
                int ventasRetenidasFE = 0;
                if (resquest.get("datafono") != null && !resquest.get("datafono").isJsonNull()) {
                    datafono = resquest.get("datafono").getAsInt();
                }
                if (resquest.get("remision") != null && !resquest.get("remision").isJsonNull()) {
                    ventasRetenidasFE += resquest.get("remision").getAsInt();
                }
                if (resquest.get("facturaElectronica") != null && !resquest.get("facturaElectronica").isJsonNull()) {
                    ventasRetenidasFE += resquest.get("facturaElectronica").getAsInt();
                }
                InfoViewController.ventasPorclientesFE(ventasRetenidasFE, datafono);
                String response = "";
                Headers respHeaders = t.getResponseHeaders();
                respHeaders.add("content-type", "application/json");
                t.sendResponseHeaders(200, response.getBytes("utf-8").length);
                try (OutputStream os = t.getResponseBody()) {
                    os.write(response.getBytes());
                    os.flush();
                }
            }
        }
    }

    class HttpHandlerRegistroIdentificacion implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            String method = t.getRequestMethod();
            if (method.equalsIgnoreCase(NovusConstante.POST)) {
                InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                int b;
                StringBuilder buf = new StringBuilder();
                while ((b = br.read()) != -1) {
                    buf.append((char) b);
                }
                NovusUtils.printLn(t.getRequestURI().toString());
                NovusUtils.printLn(buf.toString());
                JsonObject request = gson.fromJson(buf.toString(), JsonObject.class);
                try {
                    String identificacionPersona = request.get("identificacion").getAsString();
                    String identicacionDispositivo = request.get("tag").getAsString();

                   // PersonasDao pdao = new PersonasDao();
                   // pdao.registrarTag(identificacionPersona, identicacionDispositivo);

                    RegistrarTagUseCase registrarTagUseCase = new RegistrarTagUseCase();
                    registrarTagUseCase.execute(identificacionPersona, identicacionDispositivo);

                    JsonObject json = new JsonObject();
                    json.addProperty("version_codigo", Main.VERSION_CODE);
                    json.addProperty("version_nombre", Main.VERSION_APP);
                    json.addProperty("equipo_id", Main.credencial.getEquipos_id());
                    json.addProperty("empresa_id", Main.credencial.getEmpresas_id());
                    json.addProperty("fechaProceso", sdf.format(new Date()));

                    String response = json.toString();
                    Headers respHeaders = t.getResponseHeaders();
                    respHeaders.add("content-type", "application/json");
                    t.sendResponseHeaders(200, response.getBytes("utf-8").length);
                    try (OutputStream os = t.getResponseBody()) {
                        os.write(response.getBytes());
                        os.flush();
                    }
                } catch (Exception e) {
                    NovusUtils.printLn(e.getMessage());
                }
            } else {
                ErrorResponse error = new ErrorResponse();
                error.setTipo("negocio");
                error.setStatusCode(ErrorResponse.SC_METHOD_NOT_ALLOWED);
                error.setCodigo(ErrorResponse.ERROR_40500_ID);
                error.setMensaje(ErrorResponse.ERROR_40500_DESC);
                error.setFechaProceso(new Date().toString());
                Utils.responseError(t, error);
            }
        }
    }

    class HttpHandlerSurtidorConectado implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            String method = t.getRequestMethod();
            if (method.equalsIgnoreCase(NovusConstante.POST)) {
                InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                int b;
                StringBuilder buf = new StringBuilder();
                while ((b = br.read()) != -1) {
                    buf.append((char) b);
                }
                JsonObject request = gson.fromJson(buf.toString(), JsonObject.class);

                try {
                    boolean conectado = request.get("conectado") != null ? request.get("conectado").getAsBoolean()
                            : false;
                    boolean salto = false;
                    if (conectado == true) {
                        salto = sdao.getSaltoLectura();
                    }
                    if (Main.info != null) {
                        Main.info.renderSurtidorConexion(conectado, salto);
                        Main.info.saldoDeLEctura(salto);
                        Main.info.bloqueoManguera(sdao.bloqueo());
                    } else {
                        Logger.getLogger(ServerComandoWS.class.getName()).log(Level.WARNING, "Main.info es null");
                    }

                    JsonObject json = new JsonObject();
                    json.addProperty("version_codigo", Main.VERSION_CODE);
                    json.addProperty("version_nombre", Main.VERSION_APP);
                    json.addProperty("equipo_id", Main.credencial.getEquipos_id());
                    json.addProperty("empresa_id", Main.credencial.getEmpresas_id());
                    json.addProperty("fechaProceso", sdf.format(new Date()));

                    String response = json.toString();
                    Headers respHeaders = t.getResponseHeaders();
                    respHeaders.add("content-type", "application/json");
                    t.sendResponseHeaders(200, response.getBytes("utf-8").length);
                    try (OutputStream os = t.getResponseBody()) {
                        os.write(response.getBytes());
                        os.flush();
                    }
                } catch (Exception e) {
                    Logger.getLogger(ServerComandoWS.class.getName()).log(Level.SEVERE, null, e);
                    NovusUtils.printLn(e.getMessage());
                }
            } else {
                ErrorResponse error = new ErrorResponse();
                error.setTipo("negocio");
                error.setStatusCode(ErrorResponse.SC_METHOD_NOT_ALLOWED);
                error.setCodigo(ErrorResponse.ERROR_40500_ID);
                error.setMensaje(ErrorResponse.ERROR_40500_DESC);
                error.setFechaProceso(new Date().toString());
                Utils.responseError(t, error);
            }
        }
    }

    class HttpHandlerSincronizacion implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            String method = t.getRequestMethod();
            if (method.equalsIgnoreCase(NovusConstante.POST)) {
                InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                int b;
                StringBuilder buf = new StringBuilder();
                while ((b = br.read()) != -1) {
                    buf.append((char) b);
                }
                NovusUtils.printLn("<<<<<<" + t.getRequestURI().toString());
                NovusUtils.printLn("<<<<<<" + buf.toString());
                JsonObject request = gson.fromJson(buf.toString(), JsonObject.class);
                try {
                    int proceso_id = -1;
                    int producto_id = 0;
                    int bodega_id = 0;
                    int id = 0;
                    JsonArray productos = new JsonArray();
                    JsonObject json = new JsonObject();
                    if (request.get(ServerComandoWS.procesoId) != null
                            && !request.get(ServerComandoWS.procesoId).isJsonNull()) {
                        proceso_id = request.get(ServerComandoWS.procesoId).getAsInt();
                    }
                    if (request.get(ServerComandoWS.productoId) != null
                            && !request.get(ServerComandoWS.productoId).isJsonNull()) {
                        if (request.get(ServerComandoWS.productoId).isJsonObject()) {
                            productos = request.get(ServerComandoWS.productoId).getAsJsonObject().get("productos")
                                    .getAsJsonArray();
                            NovusUtils.printLn("Productos: " + productos);

                        } else {
                            producto_id = request.get(ServerComandoWS.productoId).getAsInt();
                            NovusUtils.printLn("Producto Id: " + producto_id);
                        }
                    }
                    if (request.get(ServerComandoWS.bodega) != null) {
                        bodega_id = request.get(ServerComandoWS.bodega).getAsInt();
                        NovusUtils.printLn("Bodega::::::" + bodega_id);
                    }
                    if (request.get(ServerComandoWS.productos) != null) {
                        productos = request.get(ServerComandoWS.productos).getAsJsonArray();
                        NovusUtils.printLn("Producto::::::" + request.get(ServerComandoWS.productos).getAsJsonArray());
                    }
                    if (request.get(ServerComandoWS.id) != null && !request.get(ServerComandoWS.id).isJsonNull()) {
                        id = request.get(ServerComandoWS.id).getAsInt();
                    }
                    json.addProperty(ServerComandoWS.iniciadoId, false);
                    InfoViewController.renderVentasRetenidas(-1, true);

                    Runnable run = () -> {
                        InfoViewController.renderVentasRetenidas(-2, false);
                    };

                    System.gc();
                    if (proceso_id == NovusConstante.DESCARGAR_PRODUCTOS_CANASTILLA_ID) {
                        if (!SetupAsync.EN_EJECUCIO_AJUSTE.get()) {
                            json.addProperty(ServerComandoWS.iniciadoId, true);
                            SetupAsync sync = new SetupAsync(proceso_id, producto_id, false, run, id);
                            sync.start();
                        } else {
                            NovusUtils.printLn("*******************************************");
                            NovusUtils.printLn("*******************************************");
                            NovusUtils.printLn("NO SE INICIA EL PROCESO POR QUE ESTÁ OCUPADO !");

                            NovusUtils.printLn("*******************************************");
                            NovusUtils.printLn("*******************************************");
                        }
                    } else {
                        NovusUtils.printLn("*******************************************");
                        NovusUtils.printLn("*******************************************");
                        NovusUtils.printLn("SE INICIA PROCESO ID # " + proceso_id);

                        NovusUtils.printLn("*******************************************");
                        NovusUtils.printLn("*******************************************");
                        if (!SetupAsync.EN_EJECUCIO_AJUSTE.get()) {

                            NovusUtils.printLn("SE INICIA EL PROCESO NO ESTÁ OCUPADO");

                            json.addProperty(ServerComandoWS.iniciadoId, true);
                            SetupAsync sync = new SetupAsync(proceso_id, productos, bodega_id, false, run, id);
                            sync.start();
                            
                            invalidarCacheSiEsProductosKiosco(proceso_id, productos);
                        } else {
                            NovusUtils.printLn("*******************************************");
                            NovusUtils.printLn("*******************************************");
                            NovusUtils.printLn("NO SE INICIA EL PROCESO POR QUE ESTÁ OCUPADO !!");

                            NovusUtils.printLn("*******************************************");
                            NovusUtils.printLn("*******************************************");
                        }
                    }

                    json.addProperty("version_codigo", Main.VERSION_CODE);
                    json.addProperty("version_nombre", Main.VERSION_APP);
                    json.addProperty("equipo_id", Main.credencial.getEquipos_id());
                    json.addProperty("empresa_id", Main.credencial.getEmpresas_id());
                    json.addProperty("fechaProceso", sdf.format(new Date()));

                    String response = json.toString();
                    Headers respHeaders = t.getResponseHeaders();
                    respHeaders.add("content-type", "application/json");
                    t.sendResponseHeaders(200, response.getBytes("utf-8").length);
                    try (OutputStream os = t.getResponseBody()) {
                        os.write(response.getBytes());
                        os.flush();
                    }
                } catch (Exception e) {
                    NovusUtils.printLn(e.getMessage());
                }
            } else {
                ErrorResponse error = new ErrorResponse();
                error.setTipo("negocio");
                error.setStatusCode(ErrorResponse.SC_METHOD_NOT_ALLOWED);
                error.setCodigo(ErrorResponse.ERROR_40500_ID);
                error.setMensaje(ErrorResponse.ERROR_40500_DESC);
                error.setFechaProceso(new Date().toString());
                Utils.responseError(t, error);
            }
        }
    }

    private class HttpHandlerApi implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            String method = t.getRequestMethod();
            if (method.equalsIgnoreCase(NovusConstante.POST)) {

                Headers outHeaders = t.getResponseHeaders();
                outHeaders.set("Context-Type", "application/json");

                InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);

                int b;
                StringBuilder buf = new StringBuilder();
                while ((b = br.read()) != -1) {
                    buf.append((char) b);
                }

                String peticiontext = procesaTrama(buf.toString());

                Headers respHeaders = t.getResponseHeaders();
                respHeaders.add("content-type", "application/json");
                t.sendResponseHeaders(200, peticiontext.getBytes().length);
                OutputStream os = t.getResponseBody();
                os.write(peticiontext.getBytes());
                os.flush();
                os.close();
            } else {
                ErrorResponse error = new ErrorResponse();
                error.setTipo("negocio");
                error.setStatusCode(ErrorResponse.SC_METHOD_NOT_ALLOWED);
                error.setCodigo(ErrorResponse.ERROR_40500_ID);
                error.setMensaje(ErrorResponse.ERROR_40500_DESC);
                error.setFechaProceso(new Date().toString());
                Utils.responseError(t, error);
            }
        }

        public String procesaTrama(String trama) {
            String respuesta = new String();
            Notificacion notification = gson.fromJson(trama, Notificacion.class);
            if (notification.getTipo() != 0) {
                switch (notification.getTipo()) {
                    case Notificacion.ERROR:
                        break;
                    case Notificacion.IMPRIMIR:
                        PrinterFacade fcade = new PrinterFacade();
                        fcade.printVentaCombustible(notification.getMensaje());
                        JsonObject json = new JsonObject();
                        json.addProperty("success", true);
                        json.addProperty("mensaje", "DOCUMENTO IMPRESO CORRECTAMENTE");
                        respuesta = json.toString();

                        break;
                    default:
                        NovusUtils.printLn("Notificacion no implementada");
                        break;
                }
            }
            return respuesta;
        }

    }

    private class HttpHandlerFinVenta implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            String method = t.getRequestMethod();
            if (method.equalsIgnoreCase(NovusConstante.POST)) {
                InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);

                int b;
                StringBuilder buf = new StringBuilder();
                while ((b = br.read()) != -1) {
                    buf.append((char) b);
                }

                NovusUtils.printLn(t.getRequestURI().toString());
                NovusUtils.printLn(buf.toString());
                JsonObject request = gson.fromJson(buf.toString(), JsonObject.class);
                try {
                    long idMovimiento = request.get("idTransaccionLazo").getAsLong();
                    int cara = request.get("numeroCara").getAsInt();
                    JsonObject jsonVenta = request.get("venta").getAsJsonObject();
                    boolean solicitarMediosPagos = jsonVenta.get("solicitarMediosPagos").getAsBoolean();
                    float totalVenta = jsonVenta.get("monto").getAsFloat();

                    MovimientosBean movimiento = new MovimientosBean();
                    movimiento.setId(idMovimiento);
                    movimiento.setVentaTotal(totalVenta);
                    JsonObject json = new JsonObject();
                    json.addProperty("version_codigo", Main.VERSION_CODE);
                    json.addProperty("version_nombre", Main.VERSION_APP);
                    json.addProperty("equipo_id", Main.credencial.getEquipos_id());
                    json.addProperty("empresa_id", Main.credencial.getEmpresas_id());
                    json.addProperty("fechaProceso", sdf.format(new Date()));

                    String response = json.toString();
                    Headers respHeaders = t.getResponseHeaders();
                    respHeaders.add("content-type", "application/json");
                    t.sendResponseHeaders(200, response.getBytes("utf-8").length);
                    try (OutputStream os = t.getResponseBody()) {
                        os.write(response.getBytes());
                        os.flush();
                    }
                    eliminarPlacaCliente(cara);
                    if (solicitarMediosPagos) {
                        if (Main.info != null) {
                            StoreConfirmarViewController confirmView = new StoreConfirmarViewController(Main.info, true,
                                    movimiento, false, true);
                            confirmView.setVisible(true);
                        }
                    }
                } catch (Exception e) {
                    NovusUtils.printLn(e.getMessage());
                }

            } else {
                ErrorResponse error = new ErrorResponse();
                error.setTipo("negocio");
                error.setStatusCode(ErrorResponse.SC_METHOD_NOT_ALLOWED);
                error.setCodigo(ErrorResponse.ERROR_40500_ID);
                error.setMensaje(ErrorResponse.ERROR_40500_DESC);
                error.setFechaProceso(new Date().toString());
                Utils.responseError(t, error);
            }

        }
    }

    private class HttpHandlerIdentificadorEmpleado implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            String method = t.getRequestMethod();
            if (method.equalsIgnoreCase(NovusConstante.POST)) {
                InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                int b;
                StringBuilder buf = new StringBuilder();
                while ((b = br.read()) != -1) {
                    buf.append((char) b);
                }
                NovusUtils.printLn(t.getRequestURI().toString());
                NovusUtils.printLn(buf.toString());
                JsonObject request = gson.fromJson(buf.toString(), JsonObject.class);
                String medio = request.get("medio").getAsString().trim().toLowerCase();
                long idPromotor = request.get("promotorId") == null || request.get("promotorId").isJsonNull() ? -1
                        : request.get("promotorId").getAsLong();
                String tag = request.get("promotorIdentificador").getAsString().trim();
                int cara = 0;
                if (request.has("cara") && request.get("cara") != null && !request.get("cara").isJsonNull()) {
                    cara = request.get("cara").getAsInt();
                }
                long miID = new Date().getTime();
                String sProceso = "PROCESO : " + miID;
                NovusUtils.printLn(sProceso + "-> 0");
                PersonasDao pdao = new PersonasDao();
                NovusUtils.printLn(sProceso + "-> 1");

                try {
                    PersonaBean promotor = null;
                    if (RegistroIButtonViewController.instance != null) {
                        RegistroIButtonViewController.jtag.setText(tag);
                    }
                    NovusUtils.printLn(sProceso + "-> 2");
                    if (InfoViewController.instanciaRegistroTag != null) {
                        InfoViewController.instanciaRegistroTag.jtag.setText(tag);
                    }
                    NovusUtils.printLn(sProceso + "-> 3");
                    if (idPromotor <= 0 && InfoViewController.NotificadorRumbo != null) {
                        JsonObject identifierRequest = new JsonObject();
                        identifierRequest.addProperty("medio", medio);
                        identifierRequest.addProperty("serial", tag);
                        identifierRequest.addProperty("cara", cara);
                        identifierRequest.addProperty("tipo", 1);
                        NovusUtils.printLn(sProceso + "-> 3.5");
                        setTimeout("NotificadorRumbo.instance.handleIdentifierRequest", () -> {
                            InfoViewController.NotificadorRumbo.send(identifierRequest);
                            NovusUtils.printLn(sProceso + "->3.4");
                        }, 0);
                        NovusUtils.printLn(sProceso + "-> 4");
                    }

                    if (idPromotor <= 0 && InfoViewController.NotificadorClientePropio != null) {
                        JsonObject identifierRequest = new JsonObject();
                        identifierRequest.addProperty("medio", medio);
                        identifierRequest.addProperty("serial", tag);
                        identifierRequest.addProperty("cara", cara);
                        NovusUtils.printLn(sProceso + "-> 5.5");
                        setTimeout("VentaPredefinirPlaca.recepcionNotificacionExterna", () -> {
                            InfoViewController.NotificadorClientePropio.send(identifierRequest);
                            NovusUtils.printLn(sProceso + "->5.4");
                        }, 0);
                        NovusUtils.printLn(sProceso + "-> 6");

                    } else if (idPromotor <= 0 && InfoViewController.NotificadorRecuperacion != null) {
                        JsonObject identifierRequest = new JsonObject();
                        identifierRequest.addProperty("medio", medio);
                        identifierRequest.addProperty("serial", tag);
                        NovusUtils.printLn(sProceso + "-> 4.5");
                        setTimeout("RecuperacionVentaView.instance.handleIdentifierRequest", () -> {
                            InfoViewController.NotificadorRecuperacion.send(identifierRequest);
                            NovusUtils.printLn(sProceso + "->4.4");
                        }, 0);
                        NovusUtils.printLn(sProceso + "-> 5");

                    } else if (idPromotor > -1) {
                        NovusUtils.printLn(sProceso + "->7");
                        //promotor = pdao.findPersonaById(idPromotor);
                        System.out.println(" idPromotor: " + idPromotor);
                        FindPersonaByIdUseCase findPersonaByIdUseCase = new FindPersonaByIdUseCase();
                        promotor = findPersonaByIdUseCase.execute(idPromotor);


                        NovusUtils.printLn(sProceso + "->8");

                        if (promotor == null) {
                            NovusUtils.printLn(sProceso + "-> promotor no encontrado, omitiendo acciones");
                        } else if (InfoViewController.instanciaInicioTurno != null) {
                            if (InfoViewController.instanciaInicioTurno.jsaldo.equals("")
                                    || InfoViewController.instanciaInicioTurno.jsaldo.getText().length() == 0) {
                                InfoViewController.instanciaInicioTurno.jsaldo.setText("0");
                            }
                            NovusUtils.printLn(sProceso + "->9");

                            InfoViewController.instanciaInicioTurno.jpassword.setText(promotor.getPin() + "");
                            InfoViewController.instanciaInicioTurno.juser.setText(promotor.getIdentificacion());
                            NovusUtils.printLn(sProceso + "->10");
                            setTimeout("TurnosIniciarViewController.instance.guardar", () -> {
                                InfoViewController.instanciaInicioTurno.guardar(false);
                                NovusUtils.printLn(sProceso + "->11");
                            }, 0);
                            NovusUtils.printLn(sProceso + "->11.5");

                        } else if (TurnosFinalizarViewController.instance != null) {
                            NovusUtils.printLn(sProceso + "->12");
                            TurnosFinalizarViewController.jpassword.setText(promotor.getPin() + "");
                            TurnosFinalizarViewController.juser.setText(promotor.getIdentificacion());
                            setTimeout("TurnosFinalizarViewController.instance.doClick", () -> {
                                TurnosFinalizarViewController.jButton1.doClick();
                                NovusUtils.printLn(sProceso + "->13.3");
                            }, 0);
                            NovusUtils.printLn(sProceso + "->13");

                        } else {
                            NovusUtils.printLn(sProceso + "->19");
                            renderPromotorRfid(promotor, medio.equals("ibutton"));
                        }
                    }
                    NovusUtils.printLn(sProceso + "->20");

                } catch (Exception e) {
                    Logger.getLogger(ServerComandoWS.class.getName()).log(Level.SEVERE, null, e);
                    NovusUtils.printLn("[Exception][identificadorPromotor]" + e.getMessage());
                }
                JsonObject json = new JsonObject();
                json.addProperty("version_codigo", Main.VERSION_CODE);
                json.addProperty("version_nombre", Main.VERSION_APP);
                json.addProperty("equipo_id", Main.credencial.getEquipos_id());
                json.addProperty("empresa_id", Main.credencial.getEmpresas_id());
                json.addProperty("fechaProceso", sdf.format(new Date()));

                String response = json.toString();
                Headers respHeaders = t.getResponseHeaders();
                respHeaders.add("content-type", "application/json");
                t.sendResponseHeaders(200, response.getBytes("utf-8").length);
                try (OutputStream os = t.getResponseBody()) {
                    os.write(response.getBytes());
                    os.flush();
                }
            } else {
                ErrorResponse error = new ErrorResponse();
                error.setTipo("negocio");
                error.setStatusCode(ErrorResponse.SC_METHOD_NOT_ALLOWED);
                error.setCodigo(ErrorResponse.ERROR_40500_ID);
                error.setMensaje(ErrorResponse.ERROR_40500_DESC);
                error.setFechaProceso(new Date().toString());
                Utils.responseError(t, error);
            }
        }
    }

    boolean estaEnTurno(PersonaBean promotor) {
        boolean estaEnTurno = false;
        if (InfoViewController.turnosPersonas.size() > 1) {
            for (PersonaBean persona : InfoViewController.turnosPersonas) {
                if (persona.getId() == promotor.getId()) {
                    return true;
                }
            }
        }
        return estaEnTurno;
    }

    void organizeRifdNotify() {
        if (!InfoViewController.notificacionesRfid.isEmpty()) {
            InfoViewController.jnotificacionesRfid.setVisible(true);
            InfoViewController.jnotificacionesRfid.removeAll();
        }
        int i = 0;
        for (Map.Entry<Long, EstadoRfidViewController> entry : InfoViewController.notificacionesRfid.entrySet()) {
            EstadoRfidViewController value = entry.getValue();
            value.setVisible(true);
            InfoViewController.jnotificacionesRfid.add(value);
            value.setBounds(0, 0, 510, 90);
            i++;
        }

        InfoViewController.jnotificacionesRfid.setVisible(!InfoViewController.notificacionesRfid.isEmpty());
        InfoViewController.jnotificacionesRfid.validate();
        InfoViewController.jnotificacionesRfid.repaint();
    }

    void renderPromotorRfid(PersonaBean promotor, boolean isIbutton) {
        InfoViewController.notificacionesRfid.clear();
        try {
            if (!InfoViewController.notificacionesRfid.containsKey(promotor.getId())) {
                EstadoRfidViewController panel = new EstadoRfidViewController(isIbutton);
                InfoViewController.notificacionesRfid.put(promotor.getId(), panel);
            }
            InfoViewController.notificacionesRfid.get(promotor.getId())
                    .setValores(promotor.getNombre().trim().toUpperCase(), isIbutton);
            this.organizeRifdNotify();
            setTimeout("ServerComandoWS.renderPromotorRfid", () -> limpiarPromotor(promotor.getId()), 10000);
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
            Logger.getLogger(ServerComandoWS.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void limpiarPromotor(long promotorId) {
        if (InfoViewController.notificacionesRfid != null) {
            InfoViewController.notificacionesRfid.get(promotorId).setVisible(false);
            InfoViewController.notificacionesRfid.remove(promotorId);
        }
        this.organizeRifdNotify();
    }

    private class HttpHandlerdispositivosNotificaciones implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            String method = t.getRequestMethod();
            if (method.equalsIgnoreCase(NovusConstante.POST)) {
                try {
                    InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
                    BufferedReader br = new BufferedReader(isr);
                    int b;
                    StringBuilder buf = new StringBuilder();
                    while ((b = br.read()) != -1) {
                        buf.append((char) b);
                    }
                    JsonObject request = gson.fromJson(buf.toString(), JsonObject.class);
                    NovusUtils.printLn("request" + request);
                    int cara = request.get("cara").getAsInt();
                    String clienteNombre = (request.get("clienteNombre").isJsonNull()) ? ""
                            : request.get("clienteNombre").getAsString();
                    String placa = request.get("placa").getAsString();

                    if (!InfoViewController.statusPumpNotificaciones.containsKey(cara)) {
                        EstadoNotificacionesViewController panel = new EstadoNotificacionesViewController();
                        InfoViewController.statusPumpNotificaciones.put(cara, panel);
                        InfoViewController.statusPumpNotificaciones.get(cara).setValores(placa, clienteNombre,
                                cara + "");
                    } else {
                        InfoViewController.statusPumpNotificaciones.get(cara).setValores(placa, clienteNombre,
                                cara + "");
                    }
                    organizarPlacasComponentes();
                    int delay;
                    if (Main.parametrosCore.containsKey(NovusConstante.PREFERENCE_TIEMPO_AUTORIZACION)) {
                        delay = (int) NovusUtils.secondsToMilliseconds(Integer
                                .parseInt(Main.parametrosCore.get(NovusConstante.PREFERENCE_TIEMPO_AUTORIZACION)));
                    } else {
                        delay = (int) NovusUtils.secondsToMilliseconds(30);
                    }

                    setTimeout("ServerComandoWS.HttpHandlerdispositivosNotificaciones.handle", () -> {
                        for (Map.Entry<Integer, EstadoNotificacionesViewController> entry : InfoViewController.statusPumpNotificaciones
                                .entrySet()) {
                            EstadoNotificacionesViewController panel = entry.getValue();
                            int key = entry.getKey();
                            if (!panel.getDespachoIniciado()) {
                                InfoViewController.statusPumpNotificaciones.remove(key);
                            }
                        }
                        organizarPlacasComponentes();
                    }, delay);

                    JsonObject json = new JsonObject();
                    json.addProperty("version_codigo", Main.VERSION_CODE);
                    json.addProperty("version_nombre", Main.VERSION_APP);
                    json.addProperty("equipo_id", Main.credencial.getEquipos_id());
                    json.addProperty("empresa_id", Main.credencial.getEmpresas_id());
                    json.addProperty("fechaProceso", sdf.format(new Date()));

                    String response = json.toString();

                    Headers respHeaders = t.getResponseHeaders();
                    respHeaders.add("content-type", "application/json");
                    t.sendResponseHeaders(200, response.getBytes("utf-8").length);
                    try (OutputStream os = t.getResponseBody()) {
                        os.write(response.getBytes());
                        os.flush();
                    }
                } catch (JsonSyntaxException | IOException | NumberFormatException e) {
                }
            } else {
                ErrorResponse error = new ErrorResponse();
                error.setTipo("negocio");
                error.setStatusCode(ErrorResponse.SC_METHOD_NOT_ALLOWED);
                error.setCodigo(ErrorResponse.ERROR_40500_ID);
                error.setMensaje(ErrorResponse.ERROR_40500_DESC);
                error.setFechaProceso(new Date().toString());
                Utils.responseError(t, error);
            }

        }
    }

    public static void setPlacasCliente(int cara, String clienteNombre, String placa) {
        if (!InfoViewController.statusPumpNotificaciones.containsKey(cara)) {
            EstadoNotificacionesViewController panel = new EstadoNotificacionesViewController();
            InfoViewController.statusPumpNotificaciones.put(cara, panel);
            InfoViewController.statusPumpNotificaciones
                    .get(cara)
                    .setValores(placa, clienteNombre, cara + "");
        } else {
            InfoViewController.statusPumpNotificaciones.get(cara).setValores(placa, clienteNombre, cara + "");
        }
        organizarPlacasComponentes();
    }

    public static void eliminarPlacaCliente(int cara) {
        if (InfoViewController.statusPumpNotificaciones != null) {
            InfoViewController.statusPumpNotificaciones.remove(cara);
            organizarPlacasComponentes();
        }
    }

    public static void organizarPlacasComponentes() {

        if (!InfoViewController.statusPumpNotificaciones.isEmpty()) {
            InfoViewController.jnotificaciones1.removeAll();
        }

        int i = 0;
        for (Map.Entry<Integer, EstadoNotificacionesViewController> entry : InfoViewController.statusPumpNotificaciones
                .entrySet()) {
            EstadoNotificacionesViewController value = entry.getValue();
            value.setVisible(true);
            InfoViewController.jnotificaciones1.add(value);
            value.setBounds(i * 460, 0, 464, 260);
            i++;
        }

        InfoViewController.jnotificaciones1.setVisible(InfoViewController.statusPumpNotificaciones.size() >= 1);
        InfoViewController.jnotificaciones1.validate();
        InfoViewController.jnotificaciones1.repaint();

    }

    private class HttpHandlerNotificaciones implements HttpHandler {
        
        @Override
        public void handle(HttpExchange t) throws IOException {
            String method = t.getRequestMethod();
            if (method.equalsIgnoreCase(NovusConstante.POST)) {
                
                // 🚀 LOG PARA DEBUGGING
                System.out.println("🚀 [HttpHandlerNotificaciones] URL recibida: " + t.getRequestURI().toString());
                System.out.println("🚀 [HttpHandlerNotificaciones] Método: " + method);
                
                InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                int b;
                StringBuilder buf = new StringBuilder();
                while ((b = br.read()) != -1) {
                    buf.append((char) b);
                }
                
                // 🚀 LOG DEL CONTENIDO
                System.out.println("🚀 [HttpHandlerNotificaciones] JSON recibido: " + buf.toString());
                
                JsonElement request = gson.fromJson(buf.toString(), JsonElement.class);
                NovusUtils.printLn(request.toString());
                JsonArray alertas;
                if (request != null && !request.isJsonNull()) {
                    if (request.isJsonArray()) {
                        alertas = request.getAsJsonArray();
                    } else {
                        alertas = new JsonArray();
                        alertas.add(request.getAsJsonObject());
                    }
                    JsonObject json = new JsonObject();
                    json.addProperty("version_codigo", Main.VERSION_CODE);
                    json.addProperty("version_nombre", Main.VERSION_APP);
                    json.addProperty("equipo_id", Main.credencial.getEquipos_id());
                    json.addProperty("empresa_id", Main.credencial.getEmpresas_id());
                    json.addProperty("fechaProceso", sdf.format(new Date()));
                    String response = json.toString();
                    Headers respHeaders = t.getResponseHeaders();
                    respHeaders.add("content-type", "application/json");
                    t.sendResponseHeaders(200, response.getBytes("utf-8").length);
                    try (OutputStream os = t.getResponseBody()) {
                        os.write(response.getBytes());
                        os.flush();
                    }
                    final String _mensaje = "mensaje";
                    final String _titulo = "titulo";
                    final String _codigo = "codigo";
                    final String _datafono = "plaqueta";
                    final String _venta = "IdVenta";
                    final String _codigoAutorizacion = "codigoAutorizacion";
                    final String _estado = "estado";
                    final String _placa = "placa";

                    for (JsonElement alertaElement : alertas) {
                        JsonObject alertaJson = alertaElement.getAsJsonObject();
                        String titulo = (alertaJson.get(_titulo) != null && !alertaJson.get(_titulo).isJsonNull()
                                ? alertaJson.get(_titulo).getAsString()
                                : "").toUpperCase();
                        String mensaje = (alertaJson.get(_mensaje) != null && !alertaJson.get(_mensaje).isJsonNull()
                                ? alertaJson.get(_mensaje).getAsString()
                                : "").toUpperCase();
                        String codigo = (alertaJson.get(_codigo) != null && !alertaJson.get(_codigo).isJsonNull()
                                ? alertaJson.get(_codigo).getAsString().trim()
                                : "").toUpperCase();
                        String datafono = (alertaJson.has(_datafono) && !alertaJson.get(_datafono).isJsonNull()
                                && alertaJson.get(_datafono) != null ? alertaJson.get(_datafono).getAsString() : "")
                                .toUpperCase();
                        String venta = (alertaJson.has(_venta) && !alertaJson.get(_venta).isJsonNull()
                                && alertaJson.get(_venta) != null ? alertaJson.get(_venta).getAsString() : "")
                                .toUpperCase();
                        String placa = (alertaJson.has(_placa) && !alertaJson.get(_placa).isJsonNull()
                                && alertaJson.get(_placa) != null ? alertaJson.get(_placa).getAsString() : "")
                                .toUpperCase();
                        boolean estado = (alertaJson.has(_estado) && !alertaJson.get(_estado).isJsonNull()
                                && alertaJson.get(_estado) != null ? alertaJson.get(_estado).getAsBoolean() : true);
                        String codigoAutorizacion = (alertaJson.has(_codigoAutorizacion)
                                && !alertaJson.get(_codigoAutorizacion).isJsonNull()
                                && alertaJson.get(_codigoAutorizacion) != null
                                        ? alertaJson.get(_codigoAutorizacion).getAsString()
                                        : "")
                                .toUpperCase();
                        if (Main.info != null) {
                            if (codigo.equals("145555")) {
                                Main.info.preguntarJornadas();
                            }
                            if (codigo.equals("1815053") && (InfoViewController.NotificadorVentaView != null)) {
                                datafono = datafono.isEmpty() ? "" : " PLAQUETA: ".concat(datafono);
                                alertaJson.addProperty("mensaje",
                                        mensaje.concat(datafono).concat("\n NRO: ".concat(codigoAutorizacion)));
                                alertaJson.addProperty("tipo", 2);
                                alertaJson.addProperty("icono", "/com/firefuel/resources/alert.gif");
                                alertaJson.addProperty("estado", estado);
                                alertaJson.addProperty("plaqueta", datafono);
                                alertaJson.addProperty("codigoAutorizacion", codigoAutorizacion);
                                InfoViewController.NotificadorVentaView.send(alertaJson);
                            } else if (codigo.equals("1815053") && (InfoViewController.NotificadorInfoView != null)) {
                                datafono = datafono.isEmpty() ? "" : " PLAQUETA: ".concat(datafono);
                                alertaJson.addProperty("mensaje",
                                        mensaje.concat(datafono).concat("\n NRO: ".concat(codigoAutorizacion)));
                                alertaJson.addProperty("tipo", 2);
                                alertaJson.addProperty("icono", "/com/firefuel/resources/alert.gif");
                                alertaJson.addProperty("estado", estado);
                                alertaJson.addProperty("plaqueta", datafono);
                                alertaJson.addProperty("codigoAutorizacion", codigoAutorizacion);
                                InfoViewController.NotificadorInfoView.send(alertaJson);
                            } else {
                                if (InfoViewController.NotificadorInfoView != null && codigo.equals("904052")) {
                                    alertaJson.addProperty("mensaje", titulo.replace("<BR/>", "<br>") + "\n" + mensaje);
                                    alertaJson.addProperty("tipo", 3);
                                    alertaJson.addProperty("estado", estado);
                                    alertaJson.addProperty("placa", placa);
                                    InfoViewController.NotificadorInfoView.send(alertaJson);
                                } else if (InfoViewController.NotificadorInfoView != null) {
                                    alertaJson.addProperty("mensaje", titulo.replace("<BR/>", "<br>") + "\n" + mensaje);
                                    alertaJson.addProperty("tipo", 1);
                                    alertaJson.addProperty("icono", "/com/firefuel/resources/alert.gif");
                                    InfoViewController.NotificadorInfoView.send(alertaJson);
                                }
                            }
                            if (codigo.equals("1815053") && (InfoViewController.NotificadorVentasHistorial != null)) {
                                datafono = datafono.isEmpty() ? "" : " PLAQUETA: ".concat(datafono);
                                alertaJson.addProperty("mensaje",
                                        mensaje.concat(datafono).concat("\n NRO: ".concat(codigoAutorizacion)));
                                alertaJson.addProperty("tipo", 2);
                                alertaJson.addProperty("icono", "/com/firefuel/resources/alert.gif");
                                alertaJson.addProperty("estado", estado);
                                alertaJson.addProperty("plaqueta", datafono);
                                alertaJson.addProperty("codigoAutorizacion", codigoAutorizacion);
                                InfoViewController.NotificadorVentasHistorial.send(alertaJson);
                            }
                            if (codigo.equals("1815054") && (InfoViewController.NotificadorVentasHistorial != null)) {
                                alertaJson.addProperty("mensaje", mensaje);
                                alertaJson.addProperty("tipo", 2);
                                alertaJson.addProperty("icono", "/com/firefuel/resources/alert.gif");
                                InfoViewController.NotificadorVentasHistorial.send(alertaJson);
                            }
                            if (InfoViewController.NotificadorInfoView != null && codigo.equals("904052")) {
                                alertaJson.addProperty("mensaje", mensaje);
                                alertaJson.addProperty("tipo", 1);
                                alertaJson.addProperty("icono", "/com/firefuel/resources/alert.gif");
                                InfoViewController.NotificadorInfoView.send(alertaJson);
                            }
                        }
                        String mensajes = "por favor verifique la manguera".toUpperCase();
                        if (InfoViewController.NotificadorRumbo != null && mensaje.contains(mensajes)) {
                            alertaJson.addProperty("tipo", 3);
                            InfoViewController.NotificadorRumbo.send(alertaJson);
                        }
                    }
                }
            } else {
                ErrorResponse error = new ErrorResponse();
                error.setTipo("negocio");
                error.setStatusCode(ErrorResponse.SC_METHOD_NOT_ALLOWED);
                error.setCodigo(ErrorResponse.ERROR_40500_ID);
                error.setMensaje(ErrorResponse.ERROR_40500_DESC);
                error.setFechaProceso(new Date().toString());
                Utils.responseError(t, error);
            }
        }
    }

    private class HttpHandlerSurtidorEstado implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            String method = t.getRequestMethod();
            if (method.equalsIgnoreCase(NovusConstante.POST)) {
                InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                int b;
                StringBuilder buf = new StringBuilder();
                while ((b = br.read()) != -1) {
                    buf.append((char) b);
                }
                JsonObject request = gson.fromJson(buf.toString(), JsonObject.class);
                long promotor = -1;
                try {
                    int cara = request.get("numeroCara").getAsInt();
                    int estado = request.get("codigoEstadoSurtidor").getAsInt();
                    // estado 2, 3, 4
                    switch (estado) {
                        case 1:
                        case 4:
                            if (InfoViewController.surtidores.containsKey(cara)) {
                                InfoViewController.surtidores.get(cara).setVisible(false);
                                InfoViewController.surtidores.remove(cara);
                                if (InfoViewController.statusPumpNotificaciones.containsKey(cara)) {
                                    InfoViewController.statusPumpNotificaciones.get(cara).setVisible(false);
                                    InfoViewController.statusPumpNotificaciones.remove(cara);
                                }
                            }
                            if (estado == 4) {
                                NovusUtils.finVentaAudio();
                                eliminarPlacaCliente(cara);
                            }
                            break;
                        case 2:
                        case 3:
                            Double importe = request.get("monto").getAsDouble();
                            double volumen = request.get("volumen").getAsDouble();
                            double precio = request.get("precioUnidad").getAsDouble();
                            String unidad = request.get("unidad").isJsonNull() ? ""
                                    : request.get("unidad").getAsString();
                            int manguera = request.get("numeroMangueraSurtidor") != null
                                    && !request.get("numeroMangueraSurtidor").isJsonNull()
                                            ? request.get("numeroMangueraSurtidor").getAsInt()
                                            : 0;
                            String familiaDescripcion = request.get("familia").getAsString();
                            if (!InfoViewController.surtidores.containsKey(cara)) {
                                EstadoSurtidorViewController panel = new EstadoSurtidorViewController(cara, manguera,
                                        estado == 3);
                                panel.setLabel(String.valueOf(cara));
                                InfoViewController.surtidores.put(cara, panel);
                            }
                            InfoViewController.surtidores.get(cara).setEnVenta(estado == 3);
                            InfoViewController.surtidores.get(cara).setValores(familiaDescripcion, familiaDescripcion,
                                    unidad,
                                    (importe < 1 ? "0" : "") + new DecimalFormat("##,###").format(importe),
                                    (volumen < 1 ? "0" : "") + new DecimalFormat("##,###.000").format(volumen),
                                    (precio < 1 ? "0" : "") + new DecimalFormat("##,###").format(precio));

                            if (estado == 2) {
                                NovusUtils.mangueraDescolgadaAudio();
                            }
                            if (estado == 3) {
                                if (InfoViewController.surtidores.containsKey(cara)) {
                                    String saleAuthorizationIdentifier = request
                                            .get("identificadorAutorizacionEDS") != null
                                            && !request.get("identificadorAutorizacionEDS").isJsonNull()
                                                    ? request.get("identificadorAutorizacionEDS").getAsString()
                                                    : null;
                                    if (saleAuthorizationIdentifier != null && !saleAuthorizationIdentifier.isEmpty()
                                            && saleAuthorizationIdentifier.length() > 0) {
                                        boolean comunidades = false;
                                        if (equipoDao.existeAutorizacionCaraRumbo(saleAuthorizationIdentifier, cara)) {
                                            InfoViewController.surtidores.get(cara).ventaAFidelizar = true;
                                            InfoViewController.surtidores.get(cara).btnMediosPago.setEnabled(false);
                                            InfoViewController.surtidores.get(cara).lblbtnMediosPago
                                                    .setForeground(Color.WHITE);
                                        } else if (equipoDao.existeAutorizacionCaraClientePropios(
                                                saleAuthorizationIdentifier,
                                                cara)) {
                                            InfoViewController.surtidores.get(cara).ventaAFidelizar = true;
                                            InfoViewController.surtidores.get(cara).btnMediosPago.setEnabled(false);
                                            InfoViewController.surtidores.get(cara).lblbtnMediosPago
                                                    .setForeground(Color.WHITE);
                                            NovusUtils.printLn("");

                                        } else if (equipoDao.existeAutorizacionCaraClientecomunidades(
                                                saleAuthorizationIdentifier,
                                                cara)) {
                                            comunidades = true;
                                            InfoViewController.surtidores.get(cara).ventaAFidelizar = false;
                                        } else if (equipoDao.existeAutorizacionCaraC(saleAuthorizationIdentifier,
                                                cara)) {
                                            if (habilitarBotonesFamiliaAutorizacionUseCase.execute(familiaDescripcion)
                                                    || equipoDao.tipoVenta(saleAuthorizationIdentifier, cara) == 2) {
                                                ocultarBotones(cara);
                                            }
                                        }
                                        JsonObject data = equipoDao.getDatosCliente(saleAuthorizationIdentifier, cara);
                                        if (data != null) {
                                            setPlacasCliente(cara, data.get("cliente").getAsString(),
                                                    data.get("placa").getAsString());
                                            if (comunidades) {
                                                InfoViewController.surtidores.get(cara).ventaComunidades = comunidades;
                                                InfoViewController.surtidores.get(cara).placa = data.get("placa")
                                                        .getAsString();
                                            }
                                        }
                                    }
                                    if (InfoViewController.NotificadorRumbo != null) {
                                        NovusUtils.printLn(request.toString());
                                        NovusUtils.printLn(Main.ANSI_BLUE + "IDENTIFICADOR DE VENTA RUMBO ES: "
                                                + saleAuthorizationIdentifier);
                                        if (saleAuthorizationIdentifier != null
                                                && !saleAuthorizationIdentifier.isEmpty()
                                                && saleAuthorizationIdentifier.length() > 0) {
                                            JsonObject info = new JsonObject();
                                            info.addProperty("manguera", manguera);
                                            info.addProperty("saleAuthorizationIdentifier",
                                                    saleAuthorizationIdentifier);
                                            info.addProperty("tipo", 2);
                                            InfoViewController.NotificadorRumbo.send(info);
                                        }
                                    }
                                    if (InfoViewController.statusPumpNotificaciones.containsKey(cara)) {
                                        InfoViewController.statusPumpNotificaciones.get(cara).setDespachoIniciado(true);
                                    }
                                    InfoViewController.surtidores.get(cara).setManguera(manguera);
                                    InfoViewController.surtidores.get(cara).setDespachoIniciado(true);
                                }
                            }
                            break;
                        default:
                            break;
                    }
                    if (request.get("codigoEstadoSurtidor").getAsInt() == 3
                            || request.get("codigoEstadoSurtidor").getAsInt() == 4) {
                        promotor = request.get("promotor_id") != null && !request.get("promotor_id").isJsonNull()
                                && request.get("promotor_id").getAsLong() > 0 ? request.get("promotor_id").getAsLong()
                                        : -1;
                    }
                    if (promotor > -1) {
                        if (InfoViewController.notificacionesRfid.containsKey(promotor)) {
                            limpiarPromotor(promotor);
                        }
                    }
                    if (!InfoViewController.surtidores.isEmpty()) {
                        InfoViewController.jnotificaciones.removeAll();
                    }

                    int i = 0;
                    for (Map.Entry<Integer, EstadoSurtidorViewController> entry : InfoViewController.surtidores
                            .entrySet()) {
                        EstadoSurtidorViewController value = entry.getValue();
                        value.setVisible(true);
                        InfoViewController.jnotificaciones.add(value);
                        value.setBounds(i * 410, 0, 405, 535);
                        i++;
                    }

                    InfoViewController.jnotificaciones.validate();
                    if (InfoViewController.jSubMenu != null && InfoViewController.jSubMenu.isVisible()) {
                        InfoViewController.jnotificaciones.setVisible(false);
                        InfoViewController.jSubMenu.revalidate();
                        InfoViewController.jSubMenu.repaint();
                    } else {
                        InfoViewController.jnotificaciones.setVisible(InfoViewController.surtidores.size() >= 1);
                        InfoViewController.jnotificaciones.repaint();
                    }
                } catch (Exception w) {
                    Logger.getLogger(ServerComandoWS.class.getName()).log(Level.SEVERE, null, w);
                    NovusUtils.printLn(w.getMessage());
                }

                JsonObject json = new JsonObject();
                json.addProperty("version_codigo", Main.VERSION_CODE);
                json.addProperty("version_nombre", Main.VERSION_APP);
                json.addProperty("equipo_id", Main.credencial.getEquipos_id());
                json.addProperty("empresa_id", Main.credencial.getEmpresas_id());
                json.addProperty("fechaProceso", sdf.format(new Date()));

                String response = json.toString();

                Headers respHeaders = t.getResponseHeaders();
                respHeaders.add("content-type", "application/json");
                respHeaders.add("Access-Control-Allow-Origin", "*");
                t.sendResponseHeaders(200, response.getBytes("utf-8").length);
                try (OutputStream os = t.getResponseBody()) {
                    os.write(response.getBytes());
                    os.flush();
                }

            } else {
                ErrorResponse error = new ErrorResponse();
                error.setTipo("negocio");
                error.setStatusCode(ErrorResponse.SC_METHOD_NOT_ALLOWED);
                error.setCodigo(ErrorResponse.ERROR_40500_ID);
                error.setMensaje(ErrorResponse.ERROR_40500_DESC);
                error.setFechaProceso(new Date().toString());
                Utils.responseError(t, error);
            }
        }

    }

    private void ocultarBotones(int cara) {
//        InfoViewController.surtidores.get(cara).btnGestionarVenta.setEnabled(false);
//        InfoViewController.surtidores.get(cara).lblbtnGestionarVenta.setForeground(Color.WHITE);
//
//        InfoViewController.surtidores.get(cara).btnMediosPago.setEnabled(false);
//        InfoViewController.surtidores.get(cara).lblbtnMediosPago.setForeground(Color.WHITE);
    }

    private class HttpHandlerPing implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            String method = t.getRequestMethod();
            if (method.equalsIgnoreCase(NovusConstante.GET)) {

                JsonObject json = new JsonObject();
                json.addProperty("version_codigo", Main.VERSION_CODE);
                json.addProperty("version_nombre", Main.VERSION_APP);
                json.addProperty("equipo_id", Main.credencial.getEquipos_id());
                json.addProperty("empresa_id", Main.credencial.getEmpresas_id());
                json.addProperty("fechaProceso", sdf.format(new Date()));

                String response = json.toString();

                Headers respHeaders = t.getResponseHeaders();
                respHeaders.add("content-type", "application/json");
                t.sendResponseHeaders(200, response.getBytes("utf-8").length);
                try (OutputStream os = t.getResponseBody()) {
                    os.write(response.getBytes());
                    os.flush();
                }

            } else {
                ErrorResponse error = new ErrorResponse();
                error.setTipo("negocio");
                error.setStatusCode(ErrorResponse.SC_METHOD_NOT_ALLOWED);
                error.setCodigo(ErrorResponse.ERROR_40500_ID);
                error.setMensaje(ErrorResponse.ERROR_40500_DESC);
                error.setFechaProceso(new Date().toString());
                Utils.responseError(t, error);
            }
        }
    }

    private class HttpHandlerProductos implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            String method = t.getRequestMethod();
            valideCors(t);
            if (method.equalsIgnoreCase(NovusConstante.POST)) {
                InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                int b;
                StringBuilder buf = new StringBuilder();
                while ((b = br.read()) != -1) {
                    buf.append((char) b);
                }
                JsonElement request = gson.fromJson(buf.toString(), JsonElement.class);
                NovusUtils.printLn(request.toString());
                if (request != null && !request.isJsonNull()) {

                    String keyword = "";
                    if (!request.getAsJsonObject().get("keyword").isJsonNull()) {
                        keyword = request.getAsJsonObject().get("keyword").getAsString();
                    }

                    JsonArray array = new JsonArray();
                    try {
                        //MovimientosDao dao = new MovimientosDao();
                        //ArrayList<ProductoBean> productos = dao.buscarListaBasicaProductor(keyword);
                        List<ProductoBean> productos = new GetProductosPorDescripcionUseCase(keyword).execute();

                        for (ProductoBean producto : productos) {
                            JsonObject json = new JsonObject();
                            json.addProperty("id", producto.getId());
                            json.addProperty("plu", producto.getPlu());
                            json.addProperty("precio", producto.getPrecio());
                            json.addProperty("producto", producto.getDescripcion());
                            array.add(json);
                        }

                    } catch (Exception ex) {
                        Logger.getLogger(ServerComandoWS.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    String response = array.toString();
                    Headers respHeaders = t.getResponseHeaders();
                    respHeaders.add("content-type", "application/json; charset=utf-8");
                    respHeaders.add("Access-Control-Allow-Origin", "*");
                    t.sendResponseHeaders(200, response.getBytes("utf-8").length);
                    try (OutputStream os = t.getResponseBody()) {
                        os.write(response.getBytes());
                        os.flush();
                    }
                }
            } else {
                ErrorResponse error = new ErrorResponse();
                error.setTipo("negocio");
                error.setStatusCode(ErrorResponse.SC_METHOD_NOT_ALLOWED);
                error.setCodigo(ErrorResponse.ERROR_40500_ID);
                error.setMensaje(ErrorResponse.ERROR_40500_DESC);
                error.setFechaProceso(new Date().toString());
                Utils.responseError(t, error);
            }
        }
    }

    private class HttpHandlerCategorias implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            String method = t.getRequestMethod();
            valideCors(t);
            if (method.equalsIgnoreCase(NovusConstante.GET)) {

                JsonArray array = new JsonArray();
                try {
                    //MovimientosDao dao = new MovimientosDao();
                    // ArrayList<CatalogoBean> categorias = dao.buscarListaBasicaCategorias();
                    ArrayList<CatalogoBean> categorias = new ArrayList<>(
                            new BuscarCategoriasVisiblesUseCase().execute());
                    for (CatalogoBean categoria : categorias) {
                        JsonObject json = new JsonObject();
                        json.addProperty("id", categoria.getId());
                        json.addProperty("descripcion", categoria.getDescripcion());
                        array.add(json);
                    }

                } catch (Exception ex) {
                    Logger.getLogger(ServerComandoWS.class.getName()).log(Level.SEVERE, null, ex);
                }

                String response = array.toString();

                Headers respHeaders = t.getResponseHeaders();
                respHeaders.add("content-type", "application/json; charset=utf-8");
                respHeaders.add("Access-Control-Allow-Origin", "*");
                t.sendResponseHeaders(200, response.getBytes("utf-8").length);
                try (OutputStream os = t.getResponseBody()) {
                    os.write(response.getBytes());
                    os.flush();
                } catch (Exception ex) {
                    Logger.getLogger(ServerComandoWS.class.getName()).log(Level.SEVERE, null, ex);
                    ErrorResponse error = new ErrorResponse();
                    error.setTipo("negocio");
                    error.setStatusCode(ErrorResponse.SC_INTERNAL_SERVER_ERROR);
                    error.setCodigo("INTERNAL_SERVER_ERROR");
                    error.setMensaje(ex.getMessage());
                    error.setFechaProceso(new Date().toString());
                    Utils.responseError(t, error);
                }

            } else {
                ErrorResponse error = new ErrorResponse();
                error.setTipo("negocio");
                error.setStatusCode(ErrorResponse.SC_METHOD_NOT_ALLOWED);
                error.setCodigo(ErrorResponse.ERROR_40500_ID);
                error.setMensaje(ErrorResponse.ERROR_40500_DESC);
                error.setFechaProceso(new Date().toString());
                Utils.responseError(t, error);
            }
        }
    }

    private class HttpHandlerProductosCategoria implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            String method = t.getRequestMethod();
            valideCors(t);
            if (method.equalsIgnoreCase(NovusConstante.POST)) {
                InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                int b;
                StringBuilder buf = new StringBuilder();
                while ((b = br.read()) != -1) {
                    buf.append((char) b);
                }
                JsonElement request = gson.fromJson(buf.toString(), JsonElement.class);
                NovusUtils.printLn(request.toString());
                if (request != null && !request.isJsonNull()) {

                    int grupo = 0;
                    if (!request.getAsJsonObject().get("categoriaId").isJsonNull()) {
                        grupo = request.getAsJsonObject().get("categoriaId").getAsInt();
                    }

                    JsonArray array = new JsonArray();
                    try {
                        MovimientosDao dao = new MovimientosDao();
                        //ArrayList<ProductoBean> productos = dao.buscarListaBasicaProductosPorCategoria(grupo);
                        List<ProductoBean> productos = new GetProductosPorCategoriaUseCase(grupo).execute();

                        for (ProductoBean producto : productos) {
                            JsonObject json = new JsonObject();
                            json.addProperty("id", producto.getId());
                            json.addProperty("plu", producto.getPlu());
                            json.addProperty("precio", producto.getPrecio());
                            json.addProperty("producto", producto.getDescripcion());
                            array.add(json);
                        }

                    } catch (Exception ex) {
                        Logger.getLogger(ServerComandoWS.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    String response = array.toString();
                    Headers respHeaders = t.getResponseHeaders();
                    respHeaders.add("content-type", "application/json; charset=utf-8");
                    respHeaders.add("Access-Control-Allow-Origin", "*");
                    t.sendResponseHeaders(200, response.getBytes("utf-8").length);
                    try (OutputStream os = t.getResponseBody()) {
                        os.write(response.getBytes());
                        os.flush();
                    }
                }
            } else {
                ErrorResponse error = new ErrorResponse();
                error.setTipo("negocio");
                error.setStatusCode(ErrorResponse.SC_METHOD_NOT_ALLOWED);
                error.setCodigo(ErrorResponse.ERROR_40500_ID);
                error.setMensaje(ErrorResponse.ERROR_40500_DESC);
                error.setFechaProceso(new Date().toString());
                Utils.responseError(t, error);
            }
        }
    }

    private class HttpHandlerVentaExpress implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            String method = t.getRequestMethod();
            valideCors(t);
            if (method.equalsIgnoreCase(NovusConstante.POST)) {
                InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                int b;
                StringBuilder buf = new StringBuilder();
                while ((b = br.read()) != -1) {
                    buf.append((char) b);
                }
                JsonElement request = gson.fromJson(buf.toString(), JsonElement.class);
                NovusUtils.printLn(request.toString());
                if (request != null && !request.isJsonNull()) {

                    ArrayList<ProductoBean> products = new ArrayList<>();
                    JsonArray array = request.getAsJsonObject().get("productos").getAsJsonArray();

                    for (JsonElement jProd : array) {
                        JsonObject json = jProd.getAsJsonObject();
                        ProductoBean producto = new ProductoBean();
                        producto.setPlu(json.get("plu").getAsString());
                        producto.setCantidad(json.get("cantidad").getAsInt());
                        products.add(producto);

                    }

                    PersonaBean promotor = new PersonaBean();
                    promotor.setId(request.getAsJsonObject().get("promotorId").getAsLong());
                    //Retorno del cambio de AsyncExecutor a Thread
                    new Thread(() -> {
                        try {
                            Thread.sleep(100);
                            KCOSExpressServices express = new KCOSExpressServices();
                            express.generarVenta(promotor, products);
                        } catch (InterruptedException e) {
                            System.err.println(e);
                        }
                    }).start();

                    String response = array.toString();
                    Headers respHeaders = t.getResponseHeaders();
                    respHeaders.add("content-type", "application/json; charset=utf-8");
                    respHeaders.add("Access-Control-Allow-Origin", "*");
                    t.sendResponseHeaders(200, response.getBytes("utf-8").length);
                    try (OutputStream os = t.getResponseBody()) {
                        os.write(response.getBytes());
                        os.flush();
                    }
                }
            } else {
                ErrorResponse error = new ErrorResponse();
                error.setTipo("negocio");
                error.setStatusCode(ErrorResponse.SC_METHOD_NOT_ALLOWED);
                error.setCodigo(ErrorResponse.ERROR_40500_ID);
                error.setMensaje(ErrorResponse.ERROR_40500_DESC);
                error.setFechaProceso(new Date().toString());
                Utils.responseError(t, error);
            }
        }
    }

    private class HttpHandlerNotifications implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            String method = t.getRequestMethod();
            valideCors(t);
            if (method.equalsIgnoreCase(NovusConstante.POST)) {
                InputStreamReader isr = new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);
                int b;
                StringBuilder buf = new StringBuilder();
                while ((b = br.read()) != -1) {
                    buf.append((char) b);
                }
                JsonArray request = gson.fromJson(buf.toString(), JsonArray.class);

                NovusUtils.printLn(request.toString());
                if (request != null && !request.isJsonNull()) {
                    String response = request.toString();
                    Headers respHeaders = t.getResponseHeaders();
                    respHeaders.add("content-type", "application/json; charset=utf-8");
                    respHeaders.add("Access-Control-Allow-Origin", "*");

                    if (InfoViewController.NotificadorNotificacionesError != null) {
                        JsonObject data = new JsonObject();
                        data.add("data", request);
                        InfoViewController.NotificadorNotificacionesError.send(data);
                    }
                    request.forEach((element) -> {
                        JsonObject objData = element.getAsJsonObject();
                        if (objData.get("eventComponent").getAsString().toUpperCase().equals("DATABASE")) {
                            NovusConstante.setEstadoDeLaBaseDeDatos(
                                    objData.get("eventStatus").getAsString().toUpperCase().equals("RESUELTA"));
                        }
                        validarData(objData);
                    });
                    Main.info.errorNotificaciones(0, request);
                    t.sendResponseHeaders(200, "".getBytes("utf-8").length);
                    try (OutputStream os = t.getResponseBody()) {
                        JsonObject rest = new JsonObject();
                        rest.addProperty("respuesta", "ok");
                        os.write(rest.toString().getBytes());
                        os.flush();
                    }
                } else {
                    ErrorResponse error = new ErrorResponse();
                    error.setTipo("negocio");
                    error.setStatusCode(ErrorResponse.SC_METHOD_NOT_ALLOWED);
                    error.setCodigo(ErrorResponse.ERROR_40500_ID);
                    error.setMensaje("Objeto vacío");
                    error.setFechaProceso(new Date().toString());
                    Utils.responseError(t, error);
                }
            } else {
                ErrorResponse error = new ErrorResponse();
                error.setTipo("negocio");
                error.setStatusCode(ErrorResponse.SC_METHOD_NOT_ALLOWED);
                error.setCodigo(ErrorResponse.ERROR_40500_ID);
                error.setMensaje(ErrorResponse.ERROR_40500_DESC);
                error.setFechaProceso(new Date().toString());
                Utils.responseError(t, error);
            }
        }

        private void validarData(JsonObject objData) {

            if (objData.get("eventComponent").getAsString().toUpperCase().equals("DATABASE")) {
                NovusConstante.setEstadoDeLaBaseDeDatos(
                        objData.get("eventStatus").getAsString().toUpperCase().equals("RESUELTA"));
            }

            if (objData.get("eventComponentProcess").getAsString().toUpperCase().equals("CORE__AUTODIAGNOSTICO")) {
                if (objData.get("eventStatus").getAsString().toUpperCase().equals("RESUELTA")) {
                    NovusConstante.setDataAutoDiagnosticoSurtidor(new JsonArray());
                } else {
                    JsonArray data = new JsonArray();
                    data.add(objData);
                    byte[] dataUTF8 = objData.get("eventMessage").getAsString().getBytes(StandardCharsets.UTF_8);
                    String dataS = new String(dataUTF8, StandardCharsets.UTF_8);
                    System.out.println(Main.ANSI_GREEN + " " + dataS + " hola jajajajajajaj" + " " + Main.ANSI_RESET);
                    NovusConstante.setDataAutoDiagnosticoSurtidor(data);
                }
            }

            if (objData.get("eventComponentProcess").getAsString().toUpperCase().equals("CORE__MAPEO_SURTIDOR")) {
                if (objData.get("eventStatus").getAsString().toUpperCase().equals("RESUELTA")) {
                    NovusConstante.setDataMapeoSurtidor(new JsonArray());
                } else {
                    JsonArray data = new JsonArray();
                    data.add(objData);
                    byte[] dataUTF8 = objData.get("eventMessage").getAsString().getBytes(StandardCharsets.UTF_8);
                    String dataS = new String(dataUTF8, StandardCharsets.UTF_8);
                    System.out.println(Main.ANSI_GREEN + " " + dataS + " hola jajajajajajaj" + " " + Main.ANSI_RESET);
                    NovusConstante.setDataAutoDiagnosticoSurtidor(data);
                    NovusConstante.setDataMapeoSurtidor(data);
                }
            }
        }
    }

    private void valideCors(HttpExchange t) {
        if (t.getRequestMethod().equalsIgnoreCase(NovusConstante.OPTIONS)) {
            try {
                t.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
                t.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                Headers respHeaders = t.getResponseHeaders();
                respHeaders.add("content-type", "application/json; charset=utf-8");
                respHeaders.add("Access-Control-Allow-Origin", "*");
                t.sendResponseHeaders(200, 0);
                try (OutputStream os = t.getResponseBody()) {
                    os.flush();
                }
            } catch (IOException ex) {
                Logger.getLogger(ServerComandoWS.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Actualizar cache selectivamente para productos sincronizados (OPTIMIZADO)
     * En lugar de invalidar todo, consulta y actualiza solo productos específicos
     */
    private void invalidarCacheSiEsProductosKiosco(int procesoId, JsonArray productos) {
        try {
            if (productoUpdateInterceptor == null) {
                return; // Sin interceptor, no hacer nada
            }
            
            boolean esProductosKiosco = false;
            String tipoSincronizacion = "";
            
            // Verificar si es sincronización de productos KIOSCO
            switch (procesoId) {
                case NovusConstante.DESCARGAR_PRODUCTOS_CANASTILLA_ID:
                    NovusUtils.printLn(" Comando sincronización: PRODUCTOS CANASTILLA (incluye KIOSCO)");
                    esProductosKiosco = true;
                    tipoSincronizacion = "PRODUCTOS";
                    break;
                    
                case NovusConstante.DESCARGAR_BODEGAS_ID:
                    NovusUtils.printLn(" Comando sincronización: BODEGAS (incluye inventarios KIOSCO)");
                    esProductosKiosco = true;
                    tipoSincronizacion = "INVENTARIOS";
                    break;
                    
                case NovusConstante.DESCARGAR_CATEGORIAS_ID:
                    NovusUtils.printLn(" Comando sincronización: CATEGORÍAS (incluye categorías KIOSCO)");
                    esProductosKiosco = true;
                    tipoSincronizacion = "CATEGORIAS";
                    break;
                    
                default:
                    NovusUtils.printLn(" Comando sincronización: PROCESO ID " + procesoId + " (no afecta KIOSCO)");
                    esProductosKiosco = false;
                    break;
            }
            
            // Si es sincronización que afecta KIOSCO, actualizar cache selectivamente
            if (esProductosKiosco) {
                NovusUtils.printLn(" COMANDO REMOTO: Actualizando cache KIOSCO selectivamente");
                NovusUtils.printLn("   - Proceso ID: " + procesoId);
                NovusUtils.printLn("   - Tipo: " + tipoSincronizacion);
                NovusUtils.printLn("   - Origen: Comando servidor sincronizador");
                
                if ("CATEGORIAS".equals(tipoSincronizacion)) {
                    // Para categorías, invalidar solo cache de categorías
                    NovusUtils.printLn("    Invalidando solo cache de CATEGORÍAS");
                    productoUpdateInterceptor.onCategoriaActualizada();
                    
                } else if (productos != null && productos.size() > 0) {
                    // Para productos, usar sincronización inteligente
                    actualizarProductosEspecificosEnCacheInteligente(productos, tipoSincronizacion);
                    
                } else {
                    // Si no hay productos específicos, invalidación masiva como fallback
                    NovusUtils.printLn("    Sin productos específicos - Invalidación masiva como fallback");
                    productoUpdateInterceptor.onProductosMasivosActualizados();
                }
                
                NovusUtils.printLn(" Cache KIOSCO actualizado por comando remoto");
            } else {
                NovusUtils.printLn("ℹ️ Comando no afecta KIOSCO - Cache mantenido");
            }
            
        } catch (Exception e) {
            // No debe fallar la sincronización por errores de cache
            NovusUtils.printLn(" Error actualizando cache por comando remoto (NO CRÍTICO): " + e.getMessage());
        }
    }
    
    /**
     * Actualizar productos específicos usando SINCRONIZACIÓN INTELIGENTE
     * - Si está en cache: actualizar
     * - Si NO está en cache: validar KIOSCO+bodega antes de agregar
     */
    private void actualizarProductosEspecificosEnCacheInteligente(JsonArray productos, String tipoSincronizacion) {
        try {
            NovusUtils.printLn("Iniciando SINCRONIZACIÓN INTELIGENTE de cache...");
            NovusUtils.printLn("   Productos a procesar: " + productos.size());
            NovusUtils.printLn("    Tipo: " + tipoSincronizacion);
            
            com.infrastructure.cache.KioscoCacheServiceLiviano cache = 
                com.infrastructure.cache.KioscoCacheServiceLiviano.getInstance();
            
            int productosActualizados = 0;
            int productosAgregados = 0;
            int productosIgnorados = 0;
            int productosNoEncontrados = 0;
            
            // Procesar cada producto del JSON
            for (JsonElement elemento : productos) {
                try {
                    JsonObject productoJson = elemento.getAsJsonObject();
                    
                    // Extraer ID del producto del JSON
                    Long productoId = null;
                    if (productoJson.has("producto_id")) {
                        productoId = productoJson.get("producto_id").getAsLong();
                    } else if (productoJson.has("productos_id")) {
                        productoId = productoJson.get("productos_id").getAsLong();
                    } else if (productoJson.has("id")) {
                        productoId = productoJson.get("id").getAsLong();
                    }
                    
                    if (productoId != null) {
                        NovusUtils.printLn("    Procesando producto ID: " + productoId);
                        
                        // Usar sincronización inteligente
                        cache.sincronizarProductoInteligente(productoId);
                        
                        // Los contadores se actualizan dentro del método de sincronización
                        productosActualizados++;
                        
                    } else {
                        NovusUtils.printLn("    Producto sin ID válido en JSON: " + productoJson.toString());
                        productosNoEncontrados++;
                    }
                    
                } catch (Exception e) {
                    NovusUtils.printLn("    Error procesando producto individual: " + e.getMessage());
                    productosNoEncontrados++;
                }
            }
            
            NovusUtils.printLn("RESULTADO SINCRONIZACIÓN INTELIGENTE:");
            NovusUtils.printLn("    Productos procesados: " + productosActualizados);
            NovusUtils.printLn("    Productos con error: " + productosNoEncontrados);
            NovusUtils.printLn("    Tipo sincronización: " + tipoSincronizacion);
            
        } catch (Exception e) {
            NovusUtils.printLn(" Error general en sincronización inteligente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Actualizar productos específicos en cache consultando BD (MÉTODO ORIGINAL)
     */
    private void actualizarProductosEspecificosEnCache(JsonArray productos, String tipoSincronizacion) {
        try {
            NovusUtils.printLn(" Iniciando actualización selectiva de cache...");
            NovusUtils.printLn("   Productos a procesar: " + productos.size());
            
            SetupDao setupDao = new SetupDao();
            int productosActualizados = 0;
            int productosNoEncontrados = 0;
            
            // Procesar cada producto del JSON
            for (JsonElement elemento : productos) {
                try {
                    JsonObject productoJson = elemento.getAsJsonObject();
                    
                    // Extraer ID del producto del JSON
                    Long productoId = null;
                    if (productoJson.has("producto_id")) {
                        productoId = productoJson.get("producto_id").getAsLong();
                    } else if (productoJson.has("productos_id")) {
                        productoId = productoJson.get("productos_id").getAsLong();
                    } else if (productoJson.has("id")) {
                        productoId = productoJson.get("id").getAsLong();
                    }
                    
                    if (productoId != null && productoId > 0) {
                        // Consultar producto actualizado desde BD
                        com.bean.MovimientosDetallesBean productoActualizado = 
                            setupDao.findProductoByIdParaCache(productoId);
                        
                        if (productoActualizado != null) {
                            // Actualizar en cache directamente
                            productoUpdateInterceptor.onProductoEspecificoActualizado(productoId, productoActualizado);
                            productosActualizados++;
                            
                            if (productosActualizados <= 3) {
                                NovusUtils.printLn("    Actualizado: " + productoActualizado.getDescripcion() + 
                                                   " (ID: " + productoId + ")");
                            }
                        } else {
                            productosNoEncontrados++;
                        }
                    }
                    
                } catch (Exception e) {
                    NovusUtils.printLn("    Error procesando producto individual: " + e.getMessage());
                }
            }
            
            // Logs de resumen
            NovusUtils.printLn("RESUMEN ACTUALIZACIÓN SELECTIVA:");
            NovusUtils.printLn("    Productos actualizados en cache: " + productosActualizados);
            NovusUtils.printLn("    Productos no encontrados en BD: " + productosNoEncontrados);
            NovusUtils.printLn("   Tipo sincronización: " + tipoSincronizacion);
            
            if (productosActualizados > 3) {
                NovusUtils.printLn("   ... y " + (productosActualizados - 3) + " productos más");
            }
            
        } catch (Exception e) {
            NovusUtils.printLn(" Error en actualización selectiva: " + e.getMessage());
            // Fallback a invalidación masiva en caso de error
            NovusUtils.printLn(" Fallback: Invalidación masiva");
            productoUpdateInterceptor.onProductosMasivosActualizados();
        }
    }
}
