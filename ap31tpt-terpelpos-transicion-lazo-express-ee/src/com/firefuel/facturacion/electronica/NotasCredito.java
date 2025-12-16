package com.firefuel.facturacion.electronica;

import com.application.useCases.persons.IsAdminUseCase;
import com.bean.Notificador;
import com.bean.PersonaBean;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.EquipoDao;
import com.dao.MovimientosDao;
import com.dao.PersonasDao;
import com.dao.anulacion.AnulacionDao;
import com.firefuel.AutorizacionView;
import com.firefuel.ConfirmarAnulacionView;
import com.firefuel.InfoViewController;
import com.firefuel.Main;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.infrastructure.cache.ProductoUpdateInterceptorLiviano;
import com.neo.app.bean.Recibo;
import java.util.TreeMap;
import javax.swing.Icon;
import javax.swing.JDialog;

public class NotasCredito {

    PersonaBean persona = Main.persona;
    IsAdminUseCase isAdminUseCase = new IsAdminUseCase();
    //PersonasDao pdao = new PersonasDao();
    EquipoDao edao = new EquipoDao();
    
    final Icon botonActivo = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"));
    final Icon botonBloqueado = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-link-normal.png"));
    ConfiguracionFE config = new ConfiguracionFE();
    
    // Interceptor para invalidar cache después de anulaciones
    private ProductoUpdateInterceptorLiviano productoUpdateInterceptor;

    //genera la data de producto de KCO Y CAN
    public JsonObject dataproductoNota(JsonArray dataNota, int numeroVenta, javax.swing.JLabel label) {
        JsonObject productoNota = new JsonObject();
        AnulacionDao anulacionDao = new AnulacionDao();
        for (JsonElement jsonElement : dataNota) {
            JsonObject dataP = jsonElement.getAsJsonObject();
            while (dataP.get("id").getAsLong() == numeroVenta) {
                if (dataP.get("atributos").getAsJsonObject().has("isElectronica")) {
                    if (dataP.get("atributos").getAsJsonObject().get("isElectronica").getAsBoolean() && !anulacionDao.anulacionTotal(numeroVenta)) {
                        label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png")));
                        label.setEnabled(true);
                        productoNota = dataP.getAsJsonObject();
                    } else {
                        label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-link-normal.png")));
                        label.setEnabled(false);
                    }
                } else {
                    productoNota = dataP.getAsJsonObject();
                    label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-link-normal.png")));
                    label.setEnabled(false);
                }
                break;
            }
        }
        return productoNota;

    }

    //envia la data de la nota credito a la vista
    public JsonObject enviar(JsonObject json, InfoViewController parent, JDialog dialog, boolean isCombustible, Runnable panelPrincipal) {
        JsonObject data = new JsonObject();
        Recibo recibo = new Recibo();
        int consecutivo;
        String prefijo;
        if (isCombustible) {
            recibo.setNumero(json.get("id").getAsLong());
            consecutivo = Integer.parseInt(json.get("consecutivo").getAsJsonObject().get("consecutivo_actual").getAsString());
            prefijo = json.get("consecutivo").getAsJsonObject().get("prefijo").getAsString();

        } else {
            recibo.setNumero(json.get("id").getAsLong());
            consecutivo = Integer.parseInt(json.get("consecutivo_venta").getAsString());
            prefijo = json.get("atributos").getAsJsonObject().get("consecutivo").getAsJsonObject().get("prefijo").getAsString();
        }
        ConfirmarAnulacionView anulacion = new ConfirmarAnulacionView(parent, true, recibo, consecutivo, prefijo, isCombustible, dialog, panelPrincipal);

        Long id = persona.getId();
        NovusUtils.printLn("Id: " + id + ">>>>>>>>>");
       // NovusUtils.printLn("isAdmin :" + pdao.isAdmin(id));
        NovusUtils.printLn("isAdmin :" + isAdminUseCase.execute(id));

        //if (pdao.isAdmin(id)) {
        if (isAdminUseCase.execute(id)) {
            anulacion.setVisible(true);
        } else {
            AutorizacionView auto = new AutorizacionView(parent, anulacion, true);
            auto.setVisible(true);
        }
        return data;
    }

    public JsonObject enviar(JsonObject json, InfoViewController parent, JDialog dialog, boolean isCombustible, Runnable panelPrincipal, long id) {
        JsonObject data = new JsonObject();
        Recibo recibo = new Recibo();
        int consecutivo;
        String prefijo;
        if (isCombustible) {
            recibo.setNumero(json.get("id").getAsLong());
            consecutivo = Integer.parseInt(json.get("consecutivo").getAsJsonObject().get("consecutivo_actual").getAsString());
            prefijo = json.get("consecutivo").getAsJsonObject().get("prefijo").getAsString();

        } else {
            recibo.setNumero(json.get("id").getAsLong());
            consecutivo = Integer.parseInt(json.get("consecutivo_venta").getAsString());
            prefijo = json.get("atributos").getAsJsonObject().get("consecutivo").getAsJsonObject().get("prefijo").getAsString();
        }
        ConfirmarAnulacionView anulacion = new ConfirmarAnulacionView(parent, true, recibo, consecutivo, prefijo, isCombustible, dialog, panelPrincipal);

        NovusUtils.printLn("Id: " + id);
        //NovusUtils.printLn("isAdmin :" + pdao.isAdmin(id));
        NovusUtils.printLn("isAdmin :" + isAdminUseCase.execute(id));
        //if (pdao.isAdmin(id)) {
        if (isAdminUseCase.execute(id)) {
            anulacion.setVisible(true);
        } else {
            AutorizacionView auto = new AutorizacionView(parent, anulacion, true);
            auto.setVisible(true);
        }

        return data;
    }

    //recibir la data que entrega los historiales de venta para hacer las notas credito
    /**
     *
     * @param url
     * @param observaciones
     * @param devolver
     * @param recibo
     * @param tipoAnulacion
     * @param ANULACION_VENTA
     * @param combustible
     * @param notificadorView
     * @param consecutivo
     * @param dataPoducto
     * @throws com.dao.DAOException
     */
    public void recibirNotaCreditoWS(String url, String observaciones, boolean devolver,
            long recibo, long tipoAnulacion, long ANULACION_VENTA, boolean combustible,
            Notificador notificadorView, int consecutivo, JsonObject dataPoducto) throws DAOException {

        long equipoId = edao.findEquipoId();
        
        // Inicializar interceptor para invalidar cache
        try {
            if (productoUpdateInterceptor == null) {
                productoUpdateInterceptor = new ProductoUpdateInterceptorLiviano();
            }
        } catch (Exception e) {
            NovusUtils.printLn("[NotasCredito] No se pudo inicializar interceptor de cache: " + e.getMessage());
        }

        TreeMap<String, String> header = new TreeMap<>();
        header.put("content-Type", "application/json");
        JsonObject json = new JsonObject();
        json.addProperty("identificadorMovimiento", recibo);
        json.addProperty("identificadorTipoDocumento", tipoAnulacion);
        if (!combustible) {
            json.addProperty("devolucionInventario", devolver);
        } else {
            json.addProperty("devolucionInventario", false);
        }
        json.addProperty("identificadorPromotor", Main.persona.getId());
        json.addProperty("observaciones", observaciones);
        json.addProperty("identificadorNegocio", Main.credencial.getEmpresa().getNegocioId());
        json.addProperty("consecutivo", consecutivo);
        json.addProperty("equipoId", equipoId);
        if (dataPoducto != null) {
            json.addProperty("tipoAnulacion", "parcial");
            json.add("productos", dataPoducto.get("productos").getAsJsonArray());
            json.addProperty("totalVenta", dataPoducto.get("totalVenta").getAsDouble());
            json.addProperty("totaImpuesto", dataPoducto.get("totaImpuesto").getAsDouble());
            json.addProperty("costoTotal", dataPoducto.get("costoTotal").getAsDouble());
            json.add("mediosDePago", dataPoducto.get("mediosDePago").getAsJsonObject());
        } else {
            json.addProperty("totalVenta", 0);
            json.addProperty("totaImpuesto", 0);
            json.addProperty("costoTotal", 0);
            json.addProperty("tipoAnulacion", "total");
            json.add("productos", new JsonArray());
            json.add("mediosDePago", new JsonObject());
        }

        if (combustible) {
            if (tipoAnulacion == ANULACION_VENTA) {
                JsonObject errorJson = new JsonObject();
                errorJson.addProperty("mensajeError", "NO SE PUDE HACER DEVOLUCIONES DE COMBUSTIBLES");
                errorJson.addProperty("icono", "/com/firefuel/resources/btBad.png");
                errorJson.addProperty("habilitar", true);
                errorJson.addProperty("autoclose", true);
                notificadorView.send(errorJson);
            } else {
                enviarNotaCreditoWS(header, json, notificadorView);
            }
        } else {
            if (url.isEmpty()) {
                enviarNotaCreditoWS(header, json, notificadorView);
            } else {
                enviarNotaCreditoParcialWS(header, json, notificadorView);
            }

        }
    }

    //envia la data recivida para enviarlas al servicio de notas credito
    private void enviarNotaCreditoWS(TreeMap<String, String> header, JsonObject json, Notificador notificadorView) {
        JsonObject errorJson = new JsonObject();
        errorJson.addProperty("mensajeError", "POR FAVOR, ESPERE UN MOMENTO.");
        errorJson.addProperty("icono", "/com/firefuel/resources/loader_fac.gif");
        errorJson.addProperty("habilitar", false);
        errorJson.addProperty("autoclose", false);
        notificadorView.send(errorJson);
        try {
            Thread resp = new Thread() {
                @Override
                public void run() {
                    JsonObject response = null;
                    ClientWSAsync client = new ClientWSAsync("ANULAR VENTA", NovusConstante.SECURE_CENTRAL_POINT_ANULACION_FE, NovusConstante.PUT, json, true, false, header);
                    response = client.esperaRespuesta();
                    JsonObject mensaje = respuestaAnular(response);
                    errorJson.addProperty("mensajeError", mensaje.get("mensaje").getAsString());
                    errorJson.addProperty("habilitar", true);
                    errorJson.addProperty("autoclose", true);
                    errorJson.addProperty("icono", mensaje.get("icono").getAsString());
                    notificadorView.send(errorJson);
                    
                    // ✅ SOLUCIÓN: Invalidar cache de productos después de anulación exitosa
                    if (response != null && !mensaje.get("error").getAsBoolean()) {
                        invalidarCacheProductosAnulados(json);
                        
                        // ✨ FORZAR RECARGA DE UI: Invalidar cache de productos populares para forzar refresh
                        if (productoUpdateInterceptor != null) {
                            System.out.println("Forzando recarga completa de productos en UI");
                            productoUpdateInterceptor.onProductosMasivosActualizados();
                        }
                        
                        // ✨ NOTIFICAR A KIOSKO para que refresque automáticamente
                        try {
                            System.out.println("Notificando a KCOViewController sobre anulación exitosa...");
                            com.firefuel.KCOViewController.notificarAnulacionExitosa();
                        } catch (Exception e) {
                            NovusUtils.printLn("[NotasCredito]  Error notificando callback: " + e.getMessage());
                        }
                    }
                }
            };
            resp.start();

        } catch (Exception e) {
            NovusUtils.printLn("error al hacer una  nota  " + e);
        }

    }

    private void enviarNotaCreditoParcialWS(TreeMap<String, String> header, JsonObject json, Notificador notificadorView) {
        JsonObject errorJson = new JsonObject();
        errorJson.addProperty("mensajeError", "POR FAVOR, ESPERE UN MOMENTO.");
        errorJson.addProperty("icono", "/com/firefuel/resources/loader_fac.gif");
        errorJson.addProperty("habilitar", false);
        errorJson.addProperty("autoclose", false);
        notificadorView.send(errorJson);
        try {
            Thread resp = new Thread() {
                @Override
                public void run() {

                    JsonObject response = null;
                    ClientWSAsync client = new ClientWSAsync("ANULAR VENTA", NovusConstante.SECURE_CENTRAL_POINT_ANULACION_FE_PARCIAL, NovusConstante.POST, json, true, false, header);
                    response = client.esperaRespuesta();
                    JsonObject mensaje = respuestaAnular(response);
                    errorJson.addProperty("mensajeError", mensaje.get("mensaje").getAsString());
                    errorJson.addProperty("habilitar", true);
                    errorJson.addProperty("autoclose", true);
                    errorJson.addProperty("icono", mensaje.get("icono").getAsString());
                    notificadorView.send(errorJson);
                    
                    // ✅ SOLUCIÓN: Invalidar cache de productos después de anulación parcial exitosa
                    if (response != null && !mensaje.get("error").getAsBoolean()) {
                        invalidarCacheProductosAnulados(json);
                        
                        // ✨ FORZAR RECARGA DE UI: Invalidar cache de productos populares para forzar refresh
                        if (productoUpdateInterceptor != null) {
                            System.out.println("Forzando recarga completa de productos en UI");
                            productoUpdateInterceptor.onProductosMasivosActualizados();
                        }
                        
                        // ✨ NOTIFICAR A KIOSKO para que refresque automáticamente
                        try {
                            System.out.println("Notificando a KCOViewController sobre anulación parcial exitosa...");
                            com.firefuel.KCOViewController.notificarAnulacionExitosa();
                        } catch (Exception e) {
                            NovusUtils.printLn("[NotasCredito] Error notificando callback: " + e.getMessage());
                        }
                    }
                }
            };
            resp.start();

        } catch (Exception e) {
            NovusUtils.printLn("error al hacer una  nota  " + e);
        }

    }

    //muestra los mensaje del proceso de la notas credito si es fallida o no 
    private JsonObject respuestaAnular(JsonObject response) {
        JsonObject respuesta = new JsonObject();
        String obj = config.mensajesFE(400, "nota-credito");
        String icono = "/com/firefuel/resources/btBad.png";
        respuesta.addProperty("error", true);
        respuesta.addProperty("icono", icono);
        if (response != null) {
            obj = config.mensajesFE(200, "nota-credito");
            icono = "/com/firefuel/resources/btOk.png";
            respuesta.addProperty("error", false);
        }
        respuesta.addProperty("icono", icono);
        respuesta.addProperty("mensaje", obj);
        respuesta.addProperty("loader", false);

        return respuesta;
    }
    
    /**
     * ✅ SOLUCIÓN COMPLETA: Devuelve inventario localmente y actualiza cache
     * 1. Actualiza BD local (BODEGAS_PRODUCTOS)
     * 2. Invalida cache para refrescar UI
     * 
     * @param json Datos de la anulación que contienen los productos
     */
    private void invalidarCacheProductosAnulados(JsonObject json) {
        try {
            NovusUtils.printLn("[NotasCredito] Iniciando devolución de inventario y actualización de cache");
            
            MovimientosDao movimientosDao = new MovimientosDao();
            boolean devolucionInventario = json.has("devolucionInventario") && json.get("devolucionInventario").getAsBoolean();
            
            // Obtener productos del JSON de anulación
            if (json.has("productos") && json.get("productos").isJsonArray()) {
                JsonArray productos = json.get("productos").getAsJsonArray();
                
                if (productos.size() == 0) {
                    // Anulación total - obtener productos del movimiento
                    NovusUtils.printLn("[NotasCredito] Anulación TOTAL");
                    
                    if (devolucionInventario && json.has("identificadorMovimiento")) {
                        long movimientoId = json.get("identificadorMovimiento").getAsLong();
                        devolverInventarioAnulacionTotal(movimientoId, movimientosDao);
                    }
                    
                    // Invalidar todo el cache
                    if (productoUpdateInterceptor != null) {
                        productoUpdateInterceptor.onProductosMasivosActualizados();
                    }
                } else {
                    // Anulación parcial - devolver solo productos específicos
                    NovusUtils.printLn("[NotasCredito] Anulación PARCIAL - Procesando " + productos.size() + " productos");
                    NovusUtils.printLn("[NotasCredito] devolucionInventario flag: " + devolucionInventario);
                    
                    for (JsonElement productoElement : productos) {
                        try {
                            JsonObject producto = productoElement.getAsJsonObject();
                            
                            // DEBUG: Imprimir JSON completo del producto
                            NovusUtils.printLn("[NotasCredito] DEBUG - Producto JSON: " + producto.toString());
                            
                            // Obtener datos del producto
                            long productoId = producto.has("productos_id") 
                                ? producto.get("productos_id").getAsLong() 
                                : (producto.has("id") ? producto.get("id").getAsLong() : 0);
                            
                            float cantidad = producto.has("cantidad") 
                                ? producto.get("cantidad").getAsFloat() 
                                : 0f;
                            
                            long bodegaId = producto.has("identificadorBodega") 
                                ? producto.get("identificadorBodega").getAsLong() 
                                : 0L;
                            
                            boolean esCompuesto = producto.has("compuesto") && producto.get("compuesto").getAsBoolean();
                            
                            NovusUtils.printLn("[NotasCredito] DEBUG - Producto ID: " + productoId + ", Cantidad: " + cantidad + ", BodegaId: " + bodegaId + ", Compuesto: " + esCompuesto);
                            
                            if (productoId > 0 && cantidad > 0 && devolucionInventario) {
                                NovusUtils.printLn("[NotasCredito]   → Devolviendo inventario producto ID: " + productoId + ", Cantidad: " + cantidad);
                                
                                // Devolver inventario en BD local
                                try {
                                    if (esCompuesto) {
                                        // Si es compuesto, devolver ingredientes
                                        movimientosDao.devolverInventarioIngredientes(productoId, cantidad, bodegaId);
                                    } else {
                                        // Si es simple, devolver producto directo
                                        movimientosDao.devolverInventarioProducto(productoId, cantidad, bodegaId);
                                    }
                                } catch (DAOException ex) {
                                    NovusUtils.printLn("[NotasCredito]  Error devolviendo inventario: " + ex.getMessage());
                                    ex.printStackTrace();
                                }
                            } else {
                                NovusUtils.printLn("[NotasCredito]  PRODUCTO OMITIDO - Razón: " + 
                                    (productoId <= 0 ? "ID inválido " : "") +
                                    (cantidad <= 0 ? "Cantidad inválida " : "") +
                                    (!devolucionInventario ? "Flag devolucionInventario=false" : ""));
                            }
                        } catch (Exception e) {
                            NovusUtils.printLn("[NotasCredito]  Error devolviendo inventario de producto individual: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
                
                NovusUtils.printLn("[NotasCredito]  Inventario devuelto y cache actualizado correctamente");
                
                // ✨ CRÍTICO: Invalidar cache de productos populares para forzar recarga en UI
                if (productoUpdateInterceptor != null) {
                    System.out.println(" Invalidando lista de productos populares para refresh de UI");
                    productoUpdateInterceptor.onProductosMasivosActualizados();
                }
                
            } else {
                // Si no hay productos en el JSON, es anulación total
                NovusUtils.printLn("[NotasCredito] Sin productos en JSON - Anulación total");
                
                if (devolucionInventario && json.has("identificadorMovimiento")) {
                    long movimientoId = json.get("identificadorMovimiento").getAsLong();
                    devolverInventarioAnulacionTotal(movimientoId, movimientosDao);
                }
                
                if (productoUpdateInterceptor != null) {
                    productoUpdateInterceptor.onProductosMasivosActualizados();
                }
            }
            
        } catch (Exception e) {
            NovusUtils.printLn("[NotasCredito]  Error en devolución de inventario: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * ✅ Devuelve inventario de una anulación total consultando todos los productos del movimiento
     */
    private void devolverInventarioAnulacionTotal(long movimientoId, MovimientosDao movimientosDao) {
        try {
            NovusUtils.printLn("[NotasCredito] Consultando productos del movimiento " + movimientoId + " para devolución total");
            
            AnulacionDao anulacionDao = new AnulacionDao();
            java.util.List<com.facade.anulacion.AnulacionProductos> productos = anulacionDao.obtenerProductos(movimientoId);
            
            for (com.facade.anulacion.AnulacionProductos producto : productos) {
                try {
                    long productoId = producto.getProductos_id();
                    float cantidad = producto.getCantidad();
                    long bodegaId = producto.getIdentificadorBodega();
                    
                    NovusUtils.printLn("[NotasCredito]   → Devolviendo inventario total - Producto ID: " + productoId + ", Cantidad: " + cantidad);
                    
                    // Verificar si es compuesto
                    if (producto.getIngredientes() != null && !producto.getIngredientes().isEmpty()) {
                        try {
                            movimientosDao.devolverInventarioIngredientes(productoId, cantidad, bodegaId);
                        } catch (DAOException ex) {
                            NovusUtils.printLn("[NotasCredito] Error devolviendo ingredientes: " + ex.getMessage());
                        }
                    } else {
                        try {
                            movimientosDao.devolverInventarioProducto(productoId, cantidad, bodegaId);
                        } catch (DAOException ex) {
                            NovusUtils.printLn("[NotasCredito] Error devolviendo producto: " + ex.getMessage());
                        }
                    }
                } catch (Exception e) {
                    NovusUtils.printLn("[NotasCredito] Error devolviendo producto en anulación total: " + e.getMessage());
                }
            }
            
            NovusUtils.printLn("[NotasCredito] Devolución total completada - " + productos.size() + " productos procesados");
            
        } catch (Exception e) {
            NovusUtils.printLn("[NotasCredito] Error en devolución total: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
