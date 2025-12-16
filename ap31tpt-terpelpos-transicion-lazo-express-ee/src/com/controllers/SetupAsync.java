package com.controllers;

import com.WT2.Containers.Dependency.SingletonMedioPago;
import com.bean.BodegaBean;
import com.bean.CategoriaBean;
import com.bean.ConsecutivoBean;
import com.bean.ContactoBean;
import com.bean.EmpresaBean;
import com.bean.IdentificadoresBean;
import com.bean.MediosPagosBean;
import com.bean.ModulosBean;
import com.bean.PerfilesBean;
import com.bean.PersonaBean;
import com.bean.ProductoBean;
import com.dao.DAOException;
import com.dao.EquipoDao;
import com.dao.MovimientosDao;
import com.dao.SetupDao;
import com.infrastructure.cache.ProductoUpdateInterceptorLiviano;
import com.firefuel.InfoViewController;
import com.firefuel.Main;
import static com.firefuel.Main.credencial;
import com.firefuel.SincronizarView;
import com.firefuel.Utils;
import static com.firefuel.Utils.syncLog;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;

public class SetupAsync extends Thread {

    SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_DATE_SQL);
    MovimientosDao mdao = new MovimientosDao();
    
    // Interceptor para invalidación automática de cache
    private ProductoUpdateInterceptorLiviano productoUpdateInterceptor;
    
    // Inicializar interceptor liviano de cache
    private void initializeCacheInterceptor() {
        try {
            this.productoUpdateInterceptor = new ProductoUpdateInterceptorLiviano();
        } catch (Exception e) {
            System.err.println(" No se pudo inicializar ProductoUpdateInterceptor: " + e.getMessage());
            this.productoUpdateInterceptor = null;
        }
    }

    int identificadorRequest = -1;
    long productoId = 0;
    long bodega = 0;
    JsonArray productos = new JsonArray();
    int PERFIL_PROMOTOR_ID = 8;
    String PERFIL_PROMOTOR_DESCRIPCION = "PROMOTOR";
    boolean DEBUG = true;
    Runnable OnComplete = null;
    boolean manual = false;
    int idNotificacion = 0;

    InfoViewController parent;
    EquipoDao edao = new EquipoDao();

    public static AtomicBoolean EN_EJECUCIO_AJUSTE = new AtomicBoolean(false);

    public SetupAsync(Integer identificadorRequest) {
        if (identificadorRequest != null) {
            this.identificadorRequest = identificadorRequest;
            manual = true;
        }
        initializeCacheInterceptor();
    }

    public SetupAsync(InfoViewController parent, Integer identificadorRequest) {
        if (identificadorRequest != null) {
            this.identificadorRequest = identificadorRequest;
            this.parent = parent;
            manual = true;
        }
    }

    public SetupAsync(Integer identificadorRequest, Integer productoId) {
        if (identificadorRequest != null && productoId != null) {
            this.identificadorRequest = identificadorRequest;
            this.productoId = productoId;
        }
    }

    public SetupAsync(Integer identificadorRequest, Integer productoId, boolean debug, Runnable run, int id) {
        if (identificadorRequest != null && productoId != null) {
            this.identificadorRequest = identificadorRequest;
            this.productoId = productoId;
            this.idNotificacion = id;
        }
        DEBUG = debug;
        OnComplete = run;
    }

    public SetupAsync(Integer identificadorRequest, JsonArray producto, Integer bodega, boolean debug, Runnable run, int id) {
        if (identificadorRequest != null && bodega != null && !producto.isJsonNull()) {
            this.identificadorRequest = identificadorRequest;
            this.bodega = bodega;
            this.productos = producto;
            this.idNotificacion = id;
        }
        DEBUG = debug;
        OnComplete = run;
    }

    @Override
    public void run() {
        try {
            if (identificadorRequest != -1 && !manual && !Arrays.asList(NovusConstante.NOTIFICACIONES_PERMITIDAS_AUTOMATICAS).contains(String.valueOf(this.identificadorRequest))) {
                NovusUtils.printLn(">>>>NOTIFICACION NO PERMITIDA<<< " + String.valueOf(this.identificadorRequest));
                return;
            } else {
                EN_EJECUCIO_AJUSTE.set(true);
                System.gc();
//            if (SincronizarView.jbutton_cerrar != null) {
//                SincronizarView.activarVista(false);
//            }

                switch (this.identificadorRequest) {
                    case NovusConstante.DESCARGAR_DATOS_BASICOS_ID:
                        if(NovusUtils.getCircuitBreakerAllStatus()){
                        NovusUtils.printLn(">>>>Datos Basicos<<<<");
                        loader(SincronizarView.jloader_empresa, SincronizarView.jbutton_reintentar_empresa, "SINCRONIZANDO DATOS BASICOS...");
//                        descargaDatos();
                        sincronizarHorarios();
                        sincronizarCentralizador("stations");
    //                        actualizarFechaNotificacion();
                            break;
                        }
                    case NovusConstante.DESCARGAR_CATEGORIAS_ID:
                        if(NovusUtils.getCircuitBreakerAllStatus()){
                        NovusUtils.printLn(">>>>Actualizando Categorias<<<<");
                        loader(SincronizarView.jloader_categorias, SincronizarView.jbutton_reintentar_categorias, "SINCRONIZANDO CATEGORIAS ...");
                        /*descargaCategorias();*/
                        sincronizarCentralizador("categories");
//                        actualizarFechaNotificacion();
                        break;
                        }
                    case NovusConstante.DESCARGAR_BODEGAS_ID:
                        if(NovusUtils.getCircuitBreakerAllStatus()){
                        NovusUtils.printLn(">>>>Actualizando Bodega<<<<");
                        //INCIAR LOADER
                        JsonArray info = infoBodegasInicial(); //
                        loader(SincronizarView.jloader_bodega, SincronizarView.jbutton_reintentar_bodegas, "SINCRONIZANDO BODEGAS Y CONSECUTIVOS...");

//                    descargaBodegas(true);
                        sincronizarCentralizador("inventory");
                        sincronizarCentralizador("consecutive");
//                    actualizarFechaNotificacion();

                        sincronizarCentralizador("products");
                        sincronizarCentralizador("dispenser");

                        //  NUEVO: Invalidar cache después de sincronización de productos y bodegas
                        if (productoUpdateInterceptor != null) {
                            System.out.println(" Invalidando cache KIOSCO después de sincronización completa (products + inventory)");
                            productoUpdateInterceptor.onProductosMasivosActualizados();
                        }

                        validarInfoBodegas(info);

                        //FIN LOADER
                        break;
                        }
                    case NovusConstante.DESCARGAR_PRODUCTOS_CANASTILLA_ID:
                        if(NovusUtils.getCircuitBreakerAllStatus()){
                        NovusUtils.printLn(">>>>Actualizando Productos<<<<");
                        loader(SincronizarView.jloader_productos, SincronizarView.jbutton_reintentar_productos, "SINCRONIZANDO PRODUCTOS STORE...");
                        sincronizarCentralizador("categories");
//                      sincronizarCentralizador("productsck");
                        sincronizarCentralizador("products");
                        
                        //  NUEVO: Invalidar cache después de sincronización de productos
                        if (productoUpdateInterceptor != null) {
                            System.out.println(" Invalidando cache KIOSCO después de sincronización de productos");
                            productoUpdateInterceptor.onProductosMasivosActualizados();
                        } else {
                            System.out.println(" ProductoUpdateInterceptor no disponible para invalidación de cache");
                        }
                        
                        break;
                        }
                    case NovusConstante.DESCARGAR_SURTIDORES_ID:
                        if(NovusUtils.getCircuitBreakerAllStatus()){
                        NovusUtils.printLn(">>>>Actualizando Surtidor<<<<");

                        JsonArray infoB = infoBodegasInicial();
                        loader(SincronizarView.jloader_surtidores, SincronizarView.jbutton_reintentar_surtidores, "SINCRONIZANDO DATOS SURTIDOR...");

                        sincronizarCentralizador("inventory");
                        sincronizarCentralizador("consecutive");
                        sincronizarCentralizador("products");
                        sincronizarCentralizador("dispenser");

                        validarInfoBodegas(infoB);

                        break;
                        }
                    case NovusConstante.DESCARGAR_KARDEX_ID:
                        if(NovusUtils.getCircuitBreakerAllStatus()){
                        NovusUtils.printLn(">>>>Actualizando Kardex<<<<");
                        descargaInventarioBodega();
                        break;
                        }
                    case NovusConstante.DESCARGAR_PERSONAS_ID:
                        if(NovusUtils.getCircuitBreakerAllStatus()){
                        NovusUtils.printLn(">>>>Personal<<<<");
                        descargaPersonal();
                        sincronizarCentralizador("persons");
                        break;
                        }
                    case NovusConstante.DESCARGAR_MEDIOS_ID:
                        if(NovusUtils.getCircuitBreakerAllStatus()){
                        NovusUtils.printLn(">>>>Medios Pago<<<<");
                        descargaMediosPagos();
                        sincronizarCentralizador("payments");
                        break;
                        }
                    case NovusConstante.DESCARGAR_AJUSTE_INICIAL:
                        if(NovusUtils.getCircuitBreakerAllStatus()){
                        ejecucionAjusteInicial();
                        break;
                        }
                    case NovusConstante.DESCARGAR_CONSECUTIVOS:
                        if(NovusUtils.getCircuitBreakerAllStatus()){
                        sincronizarCentralizador("inventory");
                        sincronizarCentralizador("consecutive");
                        break;
                        }
                    case NovusConstante.SINCRONIZAR_AFORO:
                        if(NovusUtils.getCircuitBreakerAllStatus()){
                        sincronizarAforo();
                        break;
                        }
                    case NovusConstante.SINCRONIZAR_DATAFONOS:
                        if(NovusUtils.getCircuitBreakerAllStatus()){
                        descargaDatafonos();
                        break;
                        }
                    default:
                        if(NovusUtils.getCircuitBreakerAllStatus()){
                        obtenerDatosCompletos();
                        sincronizarCentralizador(null);
                        }
                        break;
                }
            }
//            if (SincronizarView.jbutton_cerrar != null) {
//                SincronizarView.activarVista(true);
//            }
            if (OnComplete != null) {
                OnComplete.run();
            }
            NovusUtils.printLn("---->  *********************************** SINCRONIZACION FINALIZADA ***************************************************");
            EN_EJECUCIO_AJUSTE.set(false);
            long initialTime = System.currentTimeMillis();
            long finalTime;
            do {
                finalTime = System.currentTimeMillis();
            } while ((finalTime - initialTime) <= 2000);
        } catch (Exception ex) {
            Logger.getLogger(SetupAsync.class.getName()).log(Level.SEVERE, null, ex);
            Thread.currentThread().interrupt();
        }
    }

    public void loader(JLabel labelCheck, JLabel labelSync, String mensaje) {
        if (labelCheck != null) {
            labelSync.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btSincro.gif")));
            syncLog(mensaje);
//            Thread.sleep(2000);
            labelCheck.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/chulito_off.png")));
        }
    }

    public void descargaDatafonos() {
        JsonObject json = new JsonObject();
        String url = "http://localhost:8012/v1.0/mqtt/sync-up/dataphones";
        ClientWSAsync async = new ClientWSAsync("CONSULTA INFORMACION DATAFONOS", url, NovusConstante.GET, json, DEBUG, false, NovusUtils.headers());
        JsonObject resps = null;
        if (SincronizarView.jloaderDatafonos != null) {
            SincronizarView.jloaderDatafonos.setIcon(
                    new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/chulito_off.png")));
            SincronizarView.jbuttonDatafono.setIcon(
                    new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btSincro.gif")));
            syncLog("SINCRONIZANDO DATAFONOS");
            syncLog("SINCRONIZANDO PROVEEDORES");
        }
        try {
            resps = async.esperaRespuesta();
            if (resps != null) {
                SincronizarView.jloaderDatafonos.setIcon(
                        new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/chulito_on.png")));
                syncLog("DATAFONOS Y PROVEEDORES SINCRONIZADOS");
            } else {
                SincronizarView.jloaderDatafonos.setIcon(
                        new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/error.png")));
                syncLog("ERROR AL SINCRONIZAR DATAFONOS");
                syncLog("ERROR AL SINCRONIZAR PROVEEDORES");
            }
            SincronizarView.jbuttonDatafono.setIcon(
                    new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btReintentar.png"))); // NOI18N
        } catch (Exception e) {
            Logger.getLogger(SetupAsync.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public JsonArray infoBodegasInicial() {
        JsonArray info = mdao.getInfoBodegas();
        return info;
    }

    public void sincronizarAforo() {
        NovusUtils.printLn("Sincronizando Aforo");
        try {
            if (parent == null) {
                Main.info = new InfoViewController();
                Main.info.cargarLecturaAforo();
            } else {
                parent.cargarLecturaAforo();
            }
        } catch (Exception ex) {
            Logger.getLogger(SetupAsync.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void validarInfoBodegas(JsonArray info) {
        JsonArray infoBodega = mdao.getInfoBodegas();

        String oldInfo = info.toString();
        NovusUtils.printLn("Old Info: " + oldInfo);
        NovusUtils.printLn("::::::::::::::::::");

        String newInfo = infoBodega.toString();
        NovusUtils.printLn("New Info: " + newInfo);
        NovusUtils.printLn("::::::::::::::::::");

        if (!oldInfo.equals(newInfo)) {
            NovusUtils.printLn("Sincronizar Aforo True");
            sincronizarAforo();
        } else {
            NovusUtils.printLn("Sincronizar Aforo False");
        }
    }

    public void ejecucionAjusteInicial() {
        NovusUtils.printLn("*****************Ajuste Inicial ******************");
        descargaBodegas(false);
        for (JsonElement producto : productos) {
            productoId = producto.getAsJsonPrimitive().getAsLong();
            descargarProductos();
        }
        descargaInventarioBodega();
    }

    public void sincronizarCentralizador(String param) {
        if(!NovusUtils.getCircuitBreakerAllStatus()){
            NovusUtils.printLn("{INFO}=> Sinronizacion cancelada por el circuit breaker (sincronizacion centralizador)");
            return;
        }
        if (param != null) {
            switch (param) {
                case "stations":
                    crearNotificacion(NovusConstante.DESCARGAR_DATOS_BASICOS_ID);
                    break;
                case "categories":
                    crearNotificacion(NovusConstante.DESCARGAR_CATEGORIAS_ID);
                    break;
                case "consecutive":
                    crearNotificacion(NovusConstante.DESCARGAR_CONSECUTIVOS);
                    break;
                case "products":
                case "productsck":
                    crearNotificacion(NovusConstante.DESCARGAR_PRODUCTOS_CANASTILLA_ID);
                    break;
                case "dispenser":
                case "inventory":
                    crearNotificacion(NovusConstante.DESCARGAR_BODEGAS_ID);
                    break;
                default:
                    System.out.println("DEFAULF: " + param);
                    sincronizar(param);
                    break;
            }
        } else {
            System.out.println("DEFAULF: " + param);
            sincronizar(param);
        }

    }

    private void crearNotificacion(int tipo) {

        try {
            mdao.crearNotificacion(tipo);
        } catch (Exception e) {
            Logger.getLogger(SetupAsync.class.getName()).log(Level.SEVERE, null, e);
            switch (tipo) {
                case NovusConstante.DESCARGAR_SURTIDORES_ID:
                case NovusConstante.DESCARGAR_BODEGAS_ID:
                    SincronizarView.jbutton_reintentar_surtidores.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btReintentar.gif")));
                    SincronizarView.jloader_surtidores.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/error.png")));
                    SincronizarView.jbutton_reintentar_bodegas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btReintentar.gif")));
                    SincronizarView.jloader_bodega.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/error.png")));
                    break;
                case NovusConstante.DESCARGAR_PRODUCTOS_CANASTILLA_ID:
                    if (SincronizarView.jbutton_reintentar_productos != null) {
                        SincronizarView.jbutton_reintentar_productos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btReintentar.png")));
                        SincronizarView.jloader_productos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/error.png")));
                    }
                    break;
                case NovusConstante.DESCARGAR_DATOS_BASICOS_ID:
                    if (SincronizarView.jbutton_reintentar_empresa != null) {
                        SincronizarView.jbutton_reintentar_empresa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btReintentar.png")));
                        SincronizarView.jloader_empresa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/error.png")));
                    }
                    break;
                case NovusConstante.DESCARGAR_CATEGORIAS_ID:
                    if (SincronizarView.jbutton_reintentar_categorias != null) {
                        SincronizarView.jbutton_reintentar_categorias.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btReintentar.png")));
                        SincronizarView.jloader_categorias.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/error.png")));
                    }
                    break;
            }
        }
    }

    public void sincronizar(String param) {
        ClientWSAsync async = new ClientWSAsync("DATOS BASICOS DE LA EMPRESA", NovusConstante.SECURE_CENTRAL_POINT_SINCRONIZACION_CENTRALIZADOR + (param != null ? ("/" + param) : ""), NovusConstante.GET, null, DEBUG, false, null);
        JsonObject resp;
        if (SincronizarView.jloader_empresa != null) {
            syncLog("SINCRONIZANDO CENTRALIZADOR...");
        }
        try {
            resp = async.esperaRespuesta();
            if (resp != null) {
                syncLog(" CENTRALIZADOR SINCRONIZADO");
            }
        } catch (Exception ex) {
            syncLog("ERROR AL SINCRONIZAR CENTRALIZADOR");
        }
    }

    void obtenerDatosCompletos() {
        descargaDatos();
        descargaCategorias();
        descargaBodegas(true);
        descargarProductos();
        descargaDatosSurtidores();
        descargaInventarioBodega();
        descargaPersonal();
        descargaMediosPagos();
    }

    public static byte[] toByteArray(InputStream in) throws IOException {

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        return os.toByteArray();
    }

    public void descargaDatos() {
        if(!NovusUtils.getCircuitBreakerAllStatus()){
            NovusUtils.printLn("{INFO}=> Sinronizacion cancelada por el circuit breaker (descargaDatos)");
            return;
        }
        int identificador = 2;

        boolean success = true;
        if (SincronizarView.jloader_empresa != null) {
            SincronizarView.jloader_empresa.setIcon(
                    new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/chulito_off.png")));
        }
        SetupDao pdao = new SetupDao();
        JsonObject json = new JsonObject();
        json.addProperty("identificadorEmpresa", Main.credencial.getEmpresas_id());
        SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_FULL_DATE_ISO);
        String url = NovusConstante.getServer(NovusConstante.SECURE_CENTRAL_POINT_INFO_EMPRESA);
        TreeMap<String, String> header = new TreeMap<>();
        header.put("content-Type", "application/json");
        header.put("authorization", "123344");
        header.put("fecha", "2020-09-09T11:46:35-05:00");
        header.put("aplicacion", NovusConstante.APLICATION);
        header.put("identificadorDispositivo", "localhost");
        header.put("uuid", NovusConstante.UUID);
        header.put("password", "12345");
        header.put("versioncode", "" + Main.VERSION_CODE);
        header.put("versionapp", Main.VERSION_APP);
        ClientWSAsync async = new ClientWSAsync("DATOS BASICOS DE LA EMPRESA", url, NovusConstante.POST, json, DEBUG, false, header);
        try {
            JsonObject resp = null;
            if (SincronizarView.jloader_empresa != null) {
                SincronizarView.jbutton_reintentar_empresa.setIcon(
                        new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btSincro.gif")));
                syncLog("SINCRONIZANDO DATOS EMPRESA");
            }
            try {
                resp = async.esperaRespuesta();
            } catch (Exception ex) {
                success = false;
                syncLog("{ERROR WS}=>NO SE DESCARGO INFO EMPRESAS");
            }
            if (resp != null) {
                JsonArray array = resp.getAsJsonArray("data") != null && !resp.getAsJsonArray("data").isJsonNull()
                        ? resp.getAsJsonArray("data")
                        : new JsonArray();
                int size = array.size();
                int insert = 0;
                for (int i = 0; i < size; i++) {
                    if(!NovusUtils.getCircuitBreakerAllStatus()){
                        NovusUtils.printLn("{INFO}=> Sinronizacion cancelada por el circuit breaker (descargaDatos)");
                        return;
                    }
                    syncLog("{SUCCESS}=>INFO EMPRESA DESCARGADA");
                    try {
                        JsonObject jemp = resp.get("data").getAsJsonArray().get(0).getAsJsonObject();
                        EmpresaBean empresa = new EmpresaBean();
                        empresa.setId(jemp.get("id").getAsLong());
                        empresa.setRazonSocial(jemp.get("razon_social").getAsString());
                        empresa.setNit(jemp.get("nit").getAsString());
                        empresa.setCiudadId(jemp.get("ciudades_id").getAsLong());
                        empresa.setNombreReginal(jemp.get("razon_social_regional").getAsString());
                        if (jemp.get("c_descripcion") != null && !jemp.get("c_descripcion").isJsonNull()) {
                            empresa.setCiudadDescripcion(jemp.get("c_descripcion").getAsString());
                        } else {
                            empresa.setCiudadDescripcion("");
                        }
                        empresa.setEstado(jemp.get("estado").getAsString());
                        empresa.setEmpresasId(jemp.get("empresas_id").getAsLong());
                        empresa.setDominioId(jemp.get("dominio_id").getAsLong());
                        empresa.setNegocioId(jemp.get("negocio_id").getAsLong());
                        empresa.setCodigo(jemp.get("codigo").getAsString());
                        empresa.setEmpresaTipoId(jemp.get("empresas_tipos_id").getAsLong());
                        empresa.setAlias(jemp.get("alias").getAsString());
                        if (jemp.get("localizacion") != null && jemp.get("localizacion").isJsonNull()) {
                            empresa.setLocalizacion(jemp.get("localizacion").getAsString());
                        } else {
                            empresa.setLocalizacion("");
                        }
                        if (jemp.get("zona_horaria") != null && jemp.get("zona_horaria").isJsonNull()) {
                            empresa.setCiudadZonaHoraria(jemp.get("zona_horaria").getAsString());
                        }
                        if (jemp.get("indicadores") != null && jemp.get("indicadores").isJsonNull()) {
                            empresa.setCiudadIndicador(jemp.get("indicadores").getAsInt());
                        }
                        if (jemp.get("id_tipo_empresa") != null) {
                            empresa.setIdTipoEmpresa(Long.valueOf(jemp.get("id_tipo_empresa").getAsString()));
                        }
                        empresa.setProvinciaId(jemp.get("pr_id").getAsLong());
                        empresa.setProvinciaDescripcion(jemp.get("pr_descripcion").getAsString());

                        empresa.setPaisId(jemp.get("pa_id").getAsLong());
                        empresa.setPaisDescripcion(jemp.get("pa_descripcion").getAsString());

                        if (jemp.get("moneda") != null && jemp.get("moneda").isJsonNull()) {
                            empresa.setPaisMoneda(jemp.get("moneda").getAsString());
                        }

                        if (jemp.get("indicador") != null && jemp.get("indicador").isJsonNull()) {
                            empresa.setPaisIndicador(jemp.get("indicador").getAsInt());
                        }

                        if (jemp.get("nomenclatura") != null && jemp.get("nomenclatura").isJsonNull()) {
                            empresa.setPaisNomenclatura(jemp.get("nomenclatura").getAsString());
                        }

                        if (jemp.get("url_foto") != null && jemp.get("url_foto").isJsonNull()) {
                            empresa.setUrlFotos(jemp.get("url_foto").getAsString());
                        }
                        String telefono = "", direccion = "";
                        if (jemp.get("contactos") != null && !jemp.get("contactos").isJsonNull()) {
                            JsonArray catg = jemp.getAsJsonArray("contactos").getAsJsonArray();
                            empresa.setContacto(new ArrayList<>());
                            for (JsonElement elemt : catg) {
                                if(!NovusUtils.getCircuitBreakerAllStatus()){
                                    NovusUtils.printLn("{INFO}=> Sinronizacion cancelada por el circuit breaker (descargaDatos)");
                                    return;
                                }
                                ContactoBean contacto = new ContactoBean();
                                contacto.setTipo(elemt.getAsJsonObject().get("tipo").getAsInt());
                                contacto.setEtiqueta(elemt.getAsJsonObject().get("descripcion").getAsString());
                                contacto.setContacto(elemt.getAsJsonObject().get("contacto").getAsString());
                                contacto.setPrincipal(
                                        elemt.getAsJsonObject().get("principal").getAsString().contains("S"));
                                if (contacto.getTipo() == 1) {
                                    telefono = contacto.getContacto();
                                }
                                if (contacto.getTipo() == 3) {
                                    direccion = contacto.getContacto();
                                }
                                empresa.getContacto().add(contacto);
                            }
                        }
                        empresa.setTelefonoPrincipal(telefono);
                        empresa.setDireccionPrincipal(direccion);
                        if (jemp.get("descriptores") != null && !jemp.get("descriptores").isJsonNull()) {
                            empresa.setDescriptorId(empresa.getEmpresasId());
                            empresa.setDescriptorHeader(
                                    jemp.get("descriptores").getAsJsonObject().get("header").getAsString());
                            empresa.setDescriptorFooter(
                                    jemp.get("descriptores").getAsJsonObject().get("footer").getAsString());
                        }
                        empresa.setPerfilesEmpresa(new ArrayList<>());
                        if (jemp.get("perfiles") != null && jemp.get("perfiles").isJsonArray()) {
                            for (JsonElement perfilElement : jemp.get("perfiles").getAsJsonArray()) {
                                if(!NovusUtils.getCircuitBreakerAllStatus()){
                                    NovusUtils.printLn("{INFO}=> Sinronizacion cancelada por el circuit breaker (descargaDatos)");
                                    return;
                                }
                                PerfilesBean perfil = new PerfilesBean();
                                JsonObject jsonPerfil = perfilElement.getAsJsonObject();
                                perfil.setId(jsonPerfil.get("id").getAsLong());
                                perfil.setDescripcion(jsonPerfil.get("descripcion").getAsString());
                                perfil.setEstado(jsonPerfil.get("estado").getAsString());
                                empresa.getPerfilesEmpresa().add(perfil);
                            }
                        }
                        Main.credencial.setEmpresa(empresa);
                        if (Main.info != null) {
                            InfoViewController.jLabel7.setText(Main.credencial.getEmpresa().getAlias());
                        }

                        success = success && pdao.createEmpresasCore(empresa);
                        success = success && pdao.createEmpresasRegistry(empresa);

                        if (success) {
                            syncLog("{SUCCESS}=> INFO EMPRESA PROCESADA");
                        } else {
                            syncLog("{ERROR DAO}=> NO SE PROCESO INFO EMPRESA");
                        }
                        pdao.actualizarNombreReginal(empresa);
                    } catch (Exception ex) {
                        success = false;
                        syncLog("{ERROR DAO}=>NO SE PROCESO INFO EMPRESA");
                    } catch (DAOException ex) {
                        success = false;
                        syncLog("{ERROR DAO}=>NO SE PROCESO INFO EMPRESA");
                    }
                    insert++;
                }
            } else {

                success = false;
                syncLog("{ERROR WS}=>NO SE DESCARGO INFO EMPRESAS");
            }
        } catch (Exception a) {
            success = false;
            Logger.getLogger(SetupAsync.class.getName()).log(Level.SEVERE, null, a);
            NovusUtils.printLn(a.getMessage());
            syncLog("{ERROR DAO}=>NO SE PROCESO LOGO IMPRESION EMPRESA");
        }
        if (SincronizarView.jloader_empresa != null) {
            if (success) {
                SincronizarView.jloader_empresa.setIcon(
                        new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/chulito_on.png")));
            } else {
                SincronizarView.jloader_empresa.setIcon(
                        new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/error.png")));
            }
            SincronizarView.jbutton_reintentar_empresa.setIcon(
                    new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btReintentar.png")));
        }
    }

    public void descargarProductos() {
        if(!NovusUtils.getCircuitBreakerAllStatus()){
            NovusUtils.printLn("{INFO}=> Sinronizacion cancelada por el circuit breaker (descargarProductos)");
            return;
        }
        System.out.println("DESCARGANDO PRODUCTOS -------------------------------------------------");
        int identificador = 3;
        System.gc();

        boolean success = true;
        JsonObject json = new JsonObject();
        if (SincronizarView.jloader_productos != null) {
            SincronizarView.jloader_productos.setIcon(
                    new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/chulito_off.png"))); // NOI18N
        }
        String url = NovusConstante.getServer(NovusConstante.SECURE_END_POINT_PRODUCTOS_POS_ACUERDOS_NEW + "/" + credencial.getEmpresas_id() + "/" + this.productoId);
        SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_FULL_DATE_ISO);
        TreeMap<String, String> header = new TreeMap<>();
        header.put("content-Type", "application/json");
        header.put("authorization", "123344");
        header.put("fecha", "2020-09-09T11:46:35-05:00");
        header.put("aplicacion", NovusConstante.APLICATION);
        header.put("identificadorDispositivo", "localhost");
        header.put("uuid", NovusConstante.UUID);
        header.put("password", "1111");
        header.put("versioncode", "" + Main.VERSION_CODE);
        header.put("versionapp", Main.VERSION_APP);
        ClientWSAsync async = new ClientWSAsync("CONSULTA DE ESTADO", url, NovusConstante.GET, json, DEBUG, false, header);
        JsonObject resps = null;
        syncLog("SINCRONIZANDO PRODUCTOS ...");
        try {
            resps = async.esperaRespuesta();
        } catch (Exception e) {
            success = false;
            syncLog("{ERROR WS}=>NO SE PUDIERON DESCARGAR PRODUCTOS");
        }
        SetupDao pdao = new SetupDao();
        ProductoBean p = null;
        JsonObject jprd = null;
        NovusUtils.printLn("SINCRONIZANDO PRODUCTO(S) #" + productoId);
        if (resps != null) {
            System.gc();
            pdao.deleteIdentificadores(productoId);
            pdao.eliminarCategorias(productoId);
            
            //  CAMBIO: Solo inhabilitar productos específicos que vienen del servidor
            // En lugar de inhabilitar TODOS los productos masivamente
            pdao.inhabilitarProductos(productoId);
            com.infrastructure.cache.KioscoCacheServiceLiviano cache = com.infrastructure.cache.KioscoCacheServiceLiviano.getInstance();
            cache.sincronizarProductoDesdeHO(productoId);
            System.out.println(" SINCRONIZACIÓN SELECTIVA: Procesando solo productos específicos del servidor");

            JsonArray array = resps.get("data") != null && resps.get("data").isJsonArray() ? resps.getAsJsonArray("data") : new JsonArray();
            JsonArray tiposArray = resps.get("tipos") != null && resps.get("tipos").isJsonArray() ? resps.getAsJsonArray("tipos") : new JsonArray();

            int size = array.size();
            int insert = 0;
            if (size > 0) {
                syncLog("{SUCCESS}=> INFO PRODUCTOS DESCARGADA");
                try {
                    success = success && pdao.borraTodosImpuestos(productoId);
                    if (success) {
                        syncLog("{SUCCESS}=> IMPUESTOS LIMPIADOS");
                    } else {
                        syncLog("{ERROR DAO}=> ERROR AL LIMPIAR IMPUESTOS");
                    }
                } catch (DAOException a) {
                    Logger.getLogger(SetupAsync.class.getName()).log(Level.SEVERE, null, a);

                    syncLog("{ERROR DAO}=> ERROR AL LIMPIAR IMPUESTOS");

                    NovusUtils.printLn(a.getMessage());
                }

                for (int i = 0; i < size; i++) {
                    if(!NovusUtils.getCircuitBreakerAllStatus()){
                        NovusUtils.printLn("{INFO}=> Sinronizacion cancelada por el circuit breaker (descargarProductos)");
                        return;
                    }
                    try {
                        System.gc();
                        jprd = array.get(i).getAsJsonObject();
                        if (jprd != null && !jprd.isJsonNull()) {
                            try {
                                if (jprd.get("estado").getAsString().equals("I")) {
                                    pdao.desactivarProducto(jprd.get("id").getAsLong());
                                    success = true;
                                    syncLog("{SUCCESS}=>  PRODUCTO [" + (jprd.get("descripcion").getAsString()) + "]" + (i + 1) + "/" + (size) + " INACTIVADO SELECTIVAMENTE");
                                    System.out.println(" INACTIVADO SELECTIVO: Solo producto ID " + jprd.get("id").getAsLong() + " según estado del servidor");
                                    insert++;
                                } else {
                                    p = ProductoBean.fromJson(jprd, credencial.getEmpresas_id());

                                    if (p.isCompuesto()) {
                                        String urlIngredientes = NovusConstante.getServer(NovusConstante.SECURE_END_POINT_PRODUCTOS_INGREDIENTES + "/" + p.getId());
                                        ClientWSAsync async2 = new ClientWSAsync("CONSULTA DE INGREDIENTES", urlIngredientes, NovusConstante.GET, json, DEBUG, false, header);
                                        JsonObject rIngredientes = async2.esperaRespuesta();
                                        jprd.add("ingredientes", rIngredientes.get("mensaje").getAsJsonArray());
                                        p.setCantidadIngredientes(rIngredientes.get("mensaje").getAsJsonArray().size());
                                    }

                                    if (p.getEstado().equals("I")) {
                                        pdao.desactivarProducto(p.getId());
                                        System.out.println(" INACTIVADO SELECTIVO: Producto ID " + p.getId() + " marcado como inactivo por el servidor");
                                        success = true;
                                    } else {
                                        success = success && pdao.createBloqueado(i, p, credencial, tiposArray);
                                        success = success && pdao.createBloqueadoCore(i, p, credencial, tiposArray);

                                        if (success) {
                                            try {
                                                pdao.liberarProductos(p.getId());
                                                pdao.liberarProductosCore(p.getId());
                                                if (success) {
                                                    syncLog("{SUCCESS}=> PRODUCTO LIBERADO CORRECTAMENTE");
                                                } else {
                                                    syncLog("{ERROR DAO}=>PRODUCTO NO HAN SIDO DESBLOQUEADO");
                                                }
                                            } catch (Exception ex) {
                                                Logger.getLogger(SetupAsync.class.getName()).log(Level.SEVERE, null, ex);

                                                success = false;
                                                syncLog("{ERROR DAO}=>PRODUCTO NO HAN SIDO DESBLOQUEADO");
                                            } catch (DAOException ex) {
                                                Logger.getLogger(SetupAsync.class.getName()).log(Level.SEVERE, null, ex);
                                                success = false;
                                                syncLog("{ERROR DAO}=>PRODUCTO NO HAN SIDO DESBLOQUEADO");
                                            }
                                        }
                                    }

                                    if (success) {
                                        syncLog("{SUCCESS}=>  PRODUCTO [" + (p.getDescripcion()) + "]" + (i + 1) + "/" + (size) + " PROCESADO");
                                        insert++;
                                    } else {
                                        syncLog("{ERROR DAO}=> PRODUCTO [" + (p.getDescripcion()) + "]" + (i + 1) + "/" + (size) + " NO PROCESADO");
                                        success = false;
                                    }
                                    JsonArray arrayIngre;
                                    if (jprd.get("ingredientes") != null && jprd.get("ingredientes").isJsonArray()) {
                                        arrayIngre = jprd.getAsJsonArray("ingredientes");
                                        p.setIngredientes(new ArrayList<>());
                                        for (JsonElement elemento : arrayIngre) {
                                            if(!NovusUtils.getCircuitBreakerAllStatus()){
                                                NovusUtils.printLn("{INFO}=> Sinronizacion cancelada por el circuit breaker (descargarProductos)");
                                                return;
                                            }
                                            JsonObject jsons = elemento.getAsJsonObject();
                                            ProductoBean ingrediente = new ProductoBean();
                                            ingrediente.setId(jsons.get("id").getAsLong());
                                            ingrediente.setProducto_compuesto_id(jsons.get("ingredientes_id").getAsLong());
                                            ingrediente.setProducto_compuesto_cantidad(jsons.get("cantidad").getAsFloat());
                                            p.getIngredientes().add(ingrediente);
                                        }
                                        try {
                                            success = success && pdao.limpiarIngredientes(p.getId());
                                            if (success) {
                                                syncLog("{SUCCESS}=> INGREDIENTES LIMPIADOS");
                                            } else {
                                                syncLog("{ERROR DAO}=> INGREDIENTES NO FUERON LIMPIADOS");
                                            }
                                        } catch (DAOException ex) {
                                            syncLog("{ERROR DAO}=> INGREDIENTES NO FUERON LIMPIADOS");
                                            success = false;
                                        }
                                    }

                                    if (p.getIngredientes() != null) {
                                        for (ProductoBean ingrediente : p.getIngredientes()) {
                                            if(!NovusUtils.getCircuitBreakerAllStatus()){
                                                NovusUtils.printLn("{INFO}=> Sinronizacion cancelada por el circuit breaker (descargarProductos)");
                                                return;
                                            }
                                            try {
                                                pdao.integrarProducto(p, ingrediente);
                                                pdao.integrarProductoCore(p, ingrediente);
                                            } catch (DAOException ex) {
                                                Logger.getLogger(SetupAsync.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
                                    }
                                }
                            } catch (Exception ex) {
                                Logger.getLogger(SetupAsync.class.getName()).log(Level.SEVERE, null, ex);
                                syncLog("{ERROR DAO}=> PRODUCTO [" + (jprd.get("id").getAsString()) + " - " + (jprd.get("descripcion").getAsString()) + "]" + (i + 1) + "/" + (size) + " SIN CODIGO DE PLU");
                            }
                        }

                    } catch (Exception ex) {
                        Logger.getLogger(SetupAsync.class.getName()).log(Level.SEVERE, null, ex);
                        if (p != null) {
                            Logger.getLogger(SetupAsync.class.getName()).log(Level.SEVERE, null, ex);
                            syncLog("{ERROR DAO}=> PRODUCTO [" + (p.getDescripcion()) + "]" + (i + 1) + "/" + (size) + " NO PROCESADO");
                            success = false;
                        }

                    } catch (DAOException ex) {
                        Logger.getLogger(SetupAsync.class.getName()).log(Level.SEVERE, null, ex);

                        if (jprd != null) {
                            syncLog("{ERROR DAO}=> PRODUCTO [" + (p.getDescripcion()) + "]" + (i + 1) + "/" + (size)
                                    + " NO PROCESADO");
                            success = false;
                        }
                    }
                }
                array = null;
                if (insert == size) {
                    syncLog("{SUCCESS}=> PRODUCTOS PROCESADOS CORRECTAMENTE");
                } else if (insert == 0) {
                    success = false;
                    syncLog("{ERROR DAO}=>  PRODUCTOS NO FUERON PROCESADOS");
                } else {
                    success = false;
                    syncLog("{ERROR DAO}=> ALGUNOS PRODUCTOS NO FUERON PROCESADOS");
                }
            } else {
                success = false;
                syncLog("{INFO}=> NO HAY INFO DE PRODUCTOS");
            }
        } else {
            success = false;
            syncLog("{ERROR WS}=> NO SE PUDIERON DESCARGAR PRODUCTOS");
        }
        if (SincronizarView.jloader_productos != null) {
            if (success) {
                SincronizarView.jloader_productos.setIcon(
                        new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/chulito_on.png")));
            } else {
                SincronizarView.jloader_productos.setIcon(
                        new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/error.png")));
            }
            SincronizarView.jbutton_reintentar_productos.setIcon(
                    new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btReintentar.png")));
        }
    }

    public void descargaBodegas(boolean ACTUALIZA_CONSECUTIVO) {
        if(!NovusUtils.getCircuitBreakerAllStatus()){
            NovusUtils.printLn("{INFO}=> Sinronizacion cancelada por el circuit breaker (descargaBodegas)");
            return;
        }
        int identificador = 8;

        SetupDao sdao = new SetupDao();
        boolean success = true;
        JsonArray bodegas = null;
        String logs = "";
        if (SincronizarView.jloader_bodega != null) {
            SincronizarView.jloader_bodega.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/chulito_off.png")));
        }

        JsonObject json = new JsonObject();
        String url = NovusConstante.getServer(NovusConstante.SECURE_END_POINT_BODEGA + "/" + credencial.getEmpresas_id() + "/" + credencial.getEquipos_id());
        TreeMap<String, String> header = new TreeMap<>();
        header.put("content-Type", "application/json");
        header.put("authorization", "123344");
        header.put("fecha", "2020-09-09T11:46:35-05:00");
        header.put("aplicacion", NovusConstante.APLICATION);
        header.put("identificadorDispositivo", "localhost");
        header.put("uuid", NovusConstante.UUID);
        header.put("password", "1111");
        header.put("versioncode", "" + Main.VERSION_CODE);
        header.put("versionapp", Main.VERSION_APP);

        ClientWSAsync async = new ClientWSAsync("DESCARGA DE BODEGA", url, NovusConstante.GET, json, DEBUG, false, header);
        JsonObject response = null;
        if (SincronizarView.jloader_bodega != null) {
            SincronizarView.jbutton_reintentar_bodegas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btSincro.gif")));
            syncLog("SINCRONIZANDO BODEGAS Y CONSECUTIVOS...");
        }
        try {
            response = async.esperaRespuesta();
        } catch (Exception e) {
            success = false;
            syncLog("{ERROR WS}=>NO SE DESCARGO INFO BODEGAS");
        }

        if (response == null) {
            success = false;
            syncLog("{ERROR WS}=>NO SE DESCARGO INFO BODEGAS");
        } else {
            bodegas = response.get("data") != null && !response.get("data").isJsonNull() ? (response.get("data").getAsJsonObject().get("bodegas") != null && !response.get("data").getAsJsonObject().get("bodegas").isJsonNull() ? response.get("data").getAsJsonObject().get("bodegas").getAsJsonArray() : new JsonArray()) : new JsonArray();
            if (bodegas.size() > 0) {
                syncLog("{SUCCESS}=>INFO BODEGAS DESCARGADA");
                sdao.inhabilitarBodega();
                BodegaBean bconsecutivo = new BodegaBean();
                if (ACTUALIZA_CONSECUTIVO) {
                    if (response.get("data").getAsJsonObject().get("consecutivos") != null
                            && !response.get("data").getAsJsonObject().get("consecutivos").isJsonNull()) {
                        JsonArray consecutivoArray = response.get("data").getAsJsonObject().get("consecutivos")
                                .getAsJsonArray();
                        bconsecutivo.setConsecutivos(new ArrayList<>());
                        for (JsonElement elemt : consecutivoArray) {
                            if(!NovusUtils.getCircuitBreakerAllStatus()){
                                NovusUtils.printLn("{INFO}=> Sinronizacion cancelada por el circuit breaker (descargaBodegas)");
                                return;
                            }
                            ConsecutivoBean cons = new ConsecutivoBean();
                            cons.setId(elemt.getAsJsonObject().get("id").getAsLong());
                            cons.setTipo_documento(elemt.getAsJsonObject().get("tipo_documento").getAsInt());
                            cons.setPrefijo(elemt.getAsJsonObject().get("prefijo").getAsString());
                            if (!elemt.getAsJsonObject().get("fecha_inicio").isJsonNull()) {
                                cons.setFecha_inicio(Utils
                                        .stringToDateFull(elemt.getAsJsonObject().get("fecha_inicio").getAsString()));
                            }
                            if (!elemt.getAsJsonObject().get("fecha_fin").isJsonNull()) {
                                cons.setFecha_fin(
                                        Utils.stringToDateFull(elemt.getAsJsonObject().get("fecha_fin").getAsString()));
                            }
                            cons.setConsecutivo_inicial(elemt.getAsJsonObject().get("consecutivo_inicial").getAsLong());
                            if (!elemt.getAsJsonObject().get("consecutivo_final").isJsonNull()) {
                                cons.setConsecutivo_final(elemt.getAsJsonObject().get("consecutivo_final").getAsLong());
                            }
                            if (!elemt.getAsJsonObject().get("consecutivo_actual").isJsonNull()) {
                                cons.setConsecutivo_actual(
                                        elemt.getAsJsonObject().get("consecutivo_actual").getAsLong());
                            }

                            cons.setEstado(elemt.getAsJsonObject().get("estado").getAsString());

                            if (!elemt.getAsJsonObject().get("resolucion").isJsonNull()) {
                                cons.setResolucion(elemt.getAsJsonObject().get("resolucion").getAsString());
                            }

                            if (!elemt.getAsJsonObject().get("observaciones").isJsonNull()) {
                                cons.setObservaciones(elemt.getAsJsonObject().get("observaciones").getAsString());
                            }

                            if (elemt.getAsJsonObject().get("equipos_id") != null
                                    && !elemt.getAsJsonObject().get("equipos_id").isJsonNull()) {
                                cons.setEquipo_id(elemt.getAsJsonObject().get("equipos_id").getAsLong());
                            } else {
                                cons.setEquipo_id(Main.credencial.getEquipos_id());
                            }
                            if (!elemt.getAsJsonObject().get("cs_atributos").getAsJsonObject().isJsonNull()) {
                                cons.setAtributos(elemt.getAsJsonObject().get("cs_atributos").getAsJsonObject());
                            }
                            bconsecutivo.getConsecutivos().add(cons);
                        }
                    } else {
                        success = false;
                        syncLog("{ERROR WS}=>NO SE DESCARGO INFO CONSECUTIVOS");
                    }
                }

                SetupDao pdao = new SetupDao();
                int cant = bodegas.size();
                int insert = 0;
                int i = 0;
                for (; i < cant; i++) {
                    if(!NovusUtils.getCircuitBreakerAllStatus()){
                        NovusUtils.printLn("{INFO}=> Sinronizacion cancelada por el circuit breaker (descargaBodegas)");
                        return;
                    }
                    JsonObject object = response.get("data").getAsJsonObject().get("bodegas").getAsJsonArray().get(i)
                            .getAsJsonObject();
                    BodegaBean bodega = new BodegaBean();
                    String finalidadBodega = "X";
                    if (!object.isJsonNull() && object.get("finalidad") != null && !object.get("finalidad").isJsonNull()) {
                        finalidadBodega = object.get("finalidad").getAsString();
                    }
                    bodega.setId(object.get("id").getAsLong());
                    bodega.setEmpresaId(Main.credencial.getEmpresas_id());
                    bodega.setDescripcion(object.get("descripcion").getAsString());
                    bodega.setCodigo(object.get("codigo").getAsString());
                    bodega.setEstado(object.get("estado").getAsString());
                    bodega.setDimension("");
                    bodega.setUbicacion("");
                    bodega.setFinalidad(finalidadBodega);

                    try {
                        success = success && pdao.createBodega(bodega, credencial);
                        if (success) {
                            syncLog("{SUCCESS}=> BODEGA [" + (bodega.getDescripcion()) + "] " + (i + 1) + "/" + cant
                                    + " PROCESADA");
                            insert++;
                        } else {
                            syncLog("{ERROR DAO}=> BODEGA [" + (bodega.getDescripcion()) + "] " + (i + 1) + "/" + cant + " NO PROCESADA");
                        }
                    } catch (Exception e) {
                        success = false;
                        syncLog("{ERROR DAO}=> BODEGA [" + (bodega.getDescripcion()) + "] " + (i + 1) + "/" + cant + " NO PROCESADA");
                    } catch (DAOException ex) {
                        success = false;
                        syncLog("{ERROR DAO}=> BODEGA [" + (bodega.getDescripcion()) + "] " + (i + 1) + "/" + cant + " NO PROCESADA");
                    }
                }
                if (insert == cant) {
                    syncLog("{SUCCESS}=>  BODEGAS PROCESADAS CORRECTAMENTE");
                } else {
                    success = false;
                    syncLog("{INFO}=>  ALGUNAS BODEGAS NO FUERON PROCESADAS");
                }
                if (ACTUALIZA_CONSECUTIVO) {
                    if (bconsecutivo.getConsecutivos() != null && !bconsecutivo.getConsecutivos().isEmpty()) {
                        try {
                            success = success && pdao.createConsecutivos(bconsecutivo.getConsecutivos(), credencial);

                            if (success) {
                                syncLog("{SUCCESS}=>  CONSECUTIVOS PROCESADOS CORRECTAMENTE");
                            } else {
                                syncLog("{ERROR DAO}=> CONSECUTIVOS NO PROCESADOS");
                            }
                        } catch (DAOException r) {
                            success = false;
                            syncLog("{ERROR DAO}=> CONSECUTIVOS NO PROCESADOS");
                        }

                    } else {
                        success = false;
                        syncLog("{ERROR WS}=>NO SE DESCARGO INFO CONSECUTIVOS");
                    }
                }
            } else {
                success = false;
                syncLog("{INFO}=> NO HAY INFO DE BODEGAS");

            }
        }
        if (SincronizarView.jloader_bodega != null) {
            if (success) {
                SincronizarView.jloader_bodega.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/chulito_on.png")));
            } else {
                SincronizarView.jloader_bodega.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/error.png")));
            }
            SincronizarView.jbutton_reintentar_bodegas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btReintentar.png")));
        }
        response = null;
        bodegas = null;
    }

    public void descargaPersonal() {
        if(!NovusUtils.getCircuitBreakerAllStatus()){
            NovusUtils.printLn("{INFO}=> Sinronizacion cancelada por el circuit breaker (descargaPersonal)");
            return;
        }
        SetupDao sdao = new SetupDao();
        int identificador = 1;

        JsonObject json = new JsonObject();
        boolean success = true;

        if (SincronizarView.jloader_personal != null) {
            SincronizarView.jloader_personal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/chulito_off.png")));
        }
        String url = NovusConstante.getServer(NovusConstante.SECURE_END_POINT_PERSONAL + "/" + credencial.getEmpresas_id());
        ClientWSAsync async = new ClientWSAsync("DESCARGA DE EMPLEADOS", url, NovusConstante.GET, json, true);
        JsonObject response = null;
        if (SincronizarView.jloader_personal != null) {
            SincronizarView.jbutton_reintentar_personas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btSincro.gif")));
            syncLog("SINCRONIZANDO PERSONAL...");
        }
        try {
            response = async.esperaRespuesta();
            NovusUtils.printLn(response.toString());
            NovusUtils.printLn("");
        } catch (Exception e) {
            success = false;
            if (SincronizarView.jloader_personal != null) {
                syncLog("{ERROR WS}=>NO SE DESCARGO INFO PERSONAL");
            }
        }
        String personaFalla = "";
        if (response != null) {
            try {
                JsonArray array = response.get("data") != null && !response.get("data").isJsonNull()
                        && response.get("data").isJsonArray() ? response.getAsJsonArray("data") : new JsonArray();
                int size = array.size();
                SetupDao pdao = new SetupDao();
                int insert = 0;
                try {
                    pdao.limpiaEmpleadosReg();
                    pdao.limpiaEmpleadosCore();
                    syncLog("LIMPIANDO PERSONAL...");

                } catch (Exception e) {
                    NovusUtils.printLn(e.getMessage());
                    success = false;
                    syncLog("ERROR LIMPIANDO PERSONAL...");
                }
                if (size > 0) {
                    success = success && pdao.limpiaEmpleados();
                    for (int i = 0; i < size; i++) {
                        try {
                            if(!NovusUtils.getCircuitBreakerAllStatus()){
                                NovusUtils.printLn("{INFO}=> Sinronizacion cancelada por el circuit breaker (descargaPersonal)");
                                return;
                            }
                            JsonObject jper = array.get(i).getAsJsonObject();
                            PersonaBean personal = new PersonaBean();
                            personaFalla = jper.toString();
                            personal.setId(jper.get("id").getAsLong());
                            NovusUtils.printLn("Id: Persona : " + jper.get("id").getAsLong());
                            NovusUtils.printLn("Id: Nombre : " + jper.get("nombres").getAsString());
                            if (jper.get("tipo_identificacion") != null
                                    && !jper.get("tipo_identificacion").isJsonNull()) {
                                personal.setTipoIdentificacionId(
                                        jper.get("tipo_identificacion").getAsJsonObject().get("id").getAsLong());

                                if (!jper.get("tipo_identificacion").getAsJsonObject().get("descripcion").isJsonNull() && jper.get("tipo_identificacion").getAsJsonObject().get("descripcion").getAsJsonObject().get("label") != null && !jper.get("tipo_identificacion").getAsJsonObject().get("descripcion").getAsJsonObject().get("label").isJsonNull()) {
                                    personal.setTipoIdentificacionDesc(jper.get("tipo_identificacion").getAsJsonObject().get("descripcion").isJsonObject()
                                            ? jper.get("tipo_identificacion").getAsJsonObject().get("descripcion").getAsJsonObject().get("label").getAsString()
                                            : jper.get("tipo_identificacion").getAsJsonObject().get("descripcion").getAsString());
                                }
                                //ADMINISTRADOR
                                if (!jper.get("tipo_identificacion").getAsJsonObject().get("descripcion").isJsonNull() && jper.get("tipo_identificacion").getAsJsonObject().get("descripcion").getAsJsonObject().get("alias") != null && !jper.get("tipo_identificacion").getAsJsonObject().get("descripcion").getAsJsonObject().get("alias").isJsonNull()) {
                                    personal.setTipoIdentificacionDesc(jper.get("tipo_identificacion").getAsJsonObject().get("descripcion").isJsonObject()
                                            ? jper.get("tipo_identificacion").getAsJsonObject().get("descripcion").getAsJsonObject().get("alias").getAsString()
                                            : jper.get("tipo_identificacion").getAsJsonObject().get("descripcion").getAsString());
                                }

                            } else {
                                personal.setTipoIdentificacionId(1);
                                personal.setTipoIdentificacionDesc("");
                            }
                            if (jper.get("perfiles") != null && jper.get("perfiles").isJsonArray()) {
                                for (JsonElement element : jper.getAsJsonArray("perfiles")) {
                                    if(!NovusUtils.getCircuitBreakerAllStatus()){
                                        NovusUtils.printLn("{INFO}=> Sinronizacion cancelada por el circuit breaker (descargaPersonal)");
                                        return;
                                    }
                                    JsonObject jsonPerfil = element.getAsJsonObject();
                                    if (jsonPerfil.get("descripcion") != null && !jsonPerfil.get("descripcion").isJsonNull() && jsonPerfil.get("descripcion").getAsString().contains(PERFIL_PROMOTOR_DESCRIPCION)) {
                                        personal.setPerfil(PERFIL_PROMOTOR_ID);
                                    }
                                    if (jsonPerfil.get("id") != null && !jsonPerfil.get("id").isJsonNull()) {

                                        personal.setPerfil(jsonPerfil.get("id").getAsInt());
                                        int tipo = jsonPerfil.get("tipo") != null && !jsonPerfil.get("tipo").isJsonNull() ? jsonPerfil.get("tipo").getAsInt() : 7;
                                        int idPerfil = jsonPerfil.get("id").getAsInt();
                                        String descripcion = jsonPerfil.get("descripcion") != null && !jsonPerfil.get("descripcion").isJsonNull() ? jsonPerfil.get("descripcion").getAsString() : "";
                                        String estado = jsonPerfil.get("estado") != null && !jsonPerfil.get("estado").isJsonNull() ? jsonPerfil.get("estado").getAsString() : "A";

                                        NovusUtils.printLn(">>>Id Perfil: " + idPerfil + " Tipo: " + tipo + " Descripcion: " + descripcion + " Estado :" + estado + "<<<");
                                        sdao.actualizarPerfil(idPerfil, tipo, descripcion, estado);
                                    }
                                    break;
                                }
                            } else {
                                personal.setPerfil(PERFIL_PROMOTOR_ID);
                            }
                            personal.setIdentificacion(jper.get("identificacion").getAsString());
                            personal.setNombre(jper.get("nombres").getAsString());
                            personal.setApellidos(jper.get("apellidos").getAsString());
                            personal.setEstado(jper.get("estado").getAsString());
                            personal.setSangre(jper.get("sangre").getAsString());
                            personal.setGenero("");
                            if (jper.get("es_cliente") != null && !jper.get("es_cliente").isJsonNull()) {
                                personal.setIsCliente(jper.get("es_cliente").getAsString().equals("S"));
                            } else {
                                personal.setIsCliente(false);
                            }
                            if (jper.get("pin") != null && !jper.get("pin").isJsonNull()) {
                                personal.setPin(jper.get("pin").getAsInt());
                                personal.setClave(jper.get("clave").getAsString());
                            }
                            if (jper.get("empresa") != null & !jper.get("empresa").isJsonNull()) {
                                JsonObject jsonEmpresa = jper.get("empresa").getAsJsonObject();
                                personal.setEmpresaId(jsonEmpresa.get("id").getAsLong());
                                personal.setEmpresaRazonSocial(jsonEmpresa.get("razon_social").getAsString());
                            } else {
                                personal.setEmpresaId(Main.credencial.getEmpresas_id());
                                personal.setEmpresaRazonSocial("");
                            }

                            personal.setCiudadId(jper.get("ciudad").getAsJsonObject().get("id").getAsLong());
                            personal.setCiudadDesc(
                                    jper.get("ciudad").getAsJsonObject().get("descripcion").getAsString());
                            if (jper.get("modulos") != null && !jper.get("modulos").isJsonNull()) {
                                JsonArray arrat = jper.get("modulos").getAsJsonArray();
                                personal.setModulos(new ArrayList<>());
                                for (JsonElement obj : arrat) {
                                    if(!NovusUtils.getCircuitBreakerAllStatus()){
                                        NovusUtils.printLn("{INFO}=> Sinronizacion cancelada por el circuit breaker (descargaPersonal)");
                                        return;
                                    }
                                    ModulosBean mod = new ModulosBean();
                                    mod.setId(obj.getAsJsonObject().get("id").getAsInt());
                                    mod.setDescripcion(obj.getAsJsonObject().get("descripcion").getAsString());
                                    mod.setModuloId(obj.getAsJsonObject().get("sub_modulos_id").getAsLong());
                                    mod.setAcciones(obj.getAsJsonObject().get("acciones").getAsString());
                                    mod.setEstado(obj.getAsJsonObject().get("estado").getAsString());
                                    JsonObject atributos = new JsonObject();
                                    atributos.addProperty("posicion",
                                            obj.getAsJsonObject().get("posicion").getAsString());
                                    atributos.addProperty("color", obj.getAsJsonObject().get("color").getAsString());
                                    atributos.addProperty("url",
                                            obj.getAsJsonObject().get("link") != null
                                            && !obj.getAsJsonObject().get("link").isJsonNull()
                                            ? obj.getAsJsonObject().get("link").getAsString()
                                            : obj.getAsJsonObject().get("url").getAsString());

                                    mod.setAtributos(atributos);
                                    personal.getModulos().add(mod);
                                }
                            }
                            if (jper.get("identificadores") != null && !jper.get("identificadores").isJsonNull()) {
                                JsonArray arrat = jper.get("identificadores").getAsJsonArray();
                                personal.setIdentificadores(new ArrayList<>());
                                for (JsonElement obj : arrat) {
                                    if(!NovusUtils.getCircuitBreakerAllStatus()){
                                        NovusUtils.printLn("{INFO}=> Sinronizacion cancelada por el circuit breaker (descargaPersonal)");
                                        return;
                                    }
                                    IdentificadoresBean ident = new IdentificadoresBean();
                                    ident.setId(obj.getAsJsonObject().get("id").getAsInt());
                                    ident.setIdentificador(obj.getAsJsonObject().get("identificador").getAsString());
                                    ident.setEstado("A");
                                    personal.getIdentificadores().add(ident);
                                }
                            }

                            success = success && pdao.procesarEmpleadoRegistry(personal, credencial);
                            success = success && pdao.procesarEmpleadoCore(personal, credencial);
                            if (success) {
                                syncLog("{SUCCESS} => PERSONA [" + personal.getNombre() + "] " + (i + 1) + "/" + size
                                        + " PROCESADA");
                                insert++;
                            } else {
                                syncLog("{ERROR DAO} => PERSONA [" + personal.getNombre() + "] " + (i + 1) + "/" + size
                                        + " NO PROCESADA");
                            }
                        } catch (Exception ex) {
                            NovusUtils.printLn("personaFalla: " + personaFalla);
                            Logger.getLogger(SetupAsync.class.getName()).log(Level.SEVERE, null, ex);
                            syncLog("{ERROR DAO} =>  PERSONA  " + (i + 1) + "/" + size + " NO PROCESADA");
                            NovusUtils.printLn(ex.getMessage());
                        }

                    }
                    pdao.procesarClientesFijosRegistry(credencial);
                    pdao.procesarClientesFijosCore(credencial);
                    if (insert == size) {
                        syncLog("{SUCCESS} => PERSONAS PROCESADAS CORRECTAMENTE");
                    } else if (insert > 0) {
                        success = false;
                        syncLog("{ERROR DAO} => ALGUNAS PERSONAS NO FUERON PROCESADAS");
                    } else {
                        success = false;
                        syncLog("{ERROR DAO} => PERSONAS NO PROCESADAS");
                    }
                } else {
                    if (SincronizarView.jloader_personal != null) {
                        success = false;
                        syncLog("{INFO}=> NO HAY PERSONAS ASIGNADAS");
                    }
                }
            } catch (Exception a) {
                success = false;
                syncLog("{ERROR DAO}=> ERROR PROCESAR PERSONAL");
                Logger.getLogger(SetupAsync.class.getName()).log(Level.SEVERE, null, a);

            } catch (DAOException ex) {
                NovusUtils.printLn(ex.getMessage());
                syncLog("{ERROR DAO}=> ERROR PROCESAR PERSONAL");
                success = false;
                Logger.getLogger(SetupAsync.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            if (SincronizarView.jloader_personal != null) {
                success = false;
                syncLog("{ERROR WS}=>NO SE DESCARGO INFO PERSONAL");
            }
        }
        if (SincronizarView.jloader_personal != null) {
            if (success) {
                SincronizarView.jloader_personal.setIcon(
                        new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/chulito_on.png")));
            } else {
                SincronizarView.jloader_personal.setIcon(
                        new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/error.png")));
            }
            SincronizarView.jbutton_reintentar_personas.setIcon(
                    new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btReintentar.png")));
        }
    }

    public void descargaCategorias() {
        if(!NovusUtils.getCircuitBreakerAllStatus()){
            NovusUtils.printLn("{INFO}=> Sinronizacion cancelada por el circuit breaker (descargaCategorias)");
            return;
        }
        int identificador = 5;

        boolean success = true;
        String logs = "";
        if (SincronizarView.jloader_categorias != null) {
            SincronizarView.jloader_categorias.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/chulito_off.png"))); // NOI18N
        }
        JsonObject json = new JsonObject();
        TreeMap<String, String> header = new TreeMap<>();
        header.put("content-Type", "application/json");
        header.put("authorization", "123344");
        header.put("fecha", "2020-09-09T11:46:35-05:00");
        header.put("aplicacion", NovusConstante.APLICATION);
        header.put("identificadorDispositivo", "localhost");
        header.put("uuid", NovusConstante.UUID);
        header.put("password", "1111");
        header.put("versioncode", "" + Main.VERSION_CODE);
        header.put("versionapp", Main.VERSION_APP);
        String url = NovusConstante.getServer(NovusConstante.SECURE_END_POINT_CATEGORIAS + "/" + credencial.getEmpresa().getNegocioId());
        boolean isArray = false;
        ClientWSAsync async = new ClientWSAsync(
                "CONSULTA DE CATEGORIAS",
                url, NovusConstante.GET, json, DEBUG, isArray,
                header);

        JsonObject response = null;
        if (SincronizarView.jloader_categorias != null) {
            SincronizarView.jbutton_reintentar_categorias
                    .setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btSincro.gif"))); // NOI18N
            syncLog("SINCRONIZANDO CATEGORIAS...");
        }
        try {
            response = async.esperaRespuesta();
        } catch (Exception e) {
            success = false;
            syncLog("{ERROR WS}=>NO SE PUEDO DESCARGAR CATEGORIAS");
        }
        if (response != null) {
            NovusUtils.printLn(Main.ANSI_BLUE + credencial.getEmpresa().getNegocioId());
            NovusUtils.printLn(Main.ANSI_BLUE + credencial.getEmpresa().getNegocioId());
            try {
                JsonArray array = response.get("data") != null && !response.get("data").isJsonNull()
                        && response.get("data").isJsonArray() ? response.get("data").getAsJsonArray() : new JsonArray();
                int size = array.size();
                SetupDao pdao = new SetupDao();
                //pdao.borrarCategorias();
                int insert = 0;
                if (size > 0) {
                    syncLog("{SUCCESS}=>INFO CATEGORIA DESCARGADA");
                }
                for (int i = 0; i < size; i++) {
                    boolean categoriaInsertada = true;
                    try {
                        if(!NovusUtils.getCircuitBreakerAllStatus()){
                            NovusUtils.printLn("{INFO}=> Sinronizacion cancelada por el circuit breaker (descargaCategorias)");
                            return;
                        }
                        JsonObject jper = array.get(i).getAsJsonObject();
                        CategoriaBean cat = new CategoriaBean();
                        cat.setId(jper.get("id").getAsLong());
                        cat.setGrupo(jper.get("grupo").getAsString());
                        cat.setEstado(jper.get("estado").getAsString());
                        cat.setGruposTiposId(jper.get("grupos_tipos_id").getAsLong());
                        cat.setEmpresas_id(jper.get("empresas_id").getAsLong());

                        JsonObject atributos = new JsonObject();
                        atributos.addProperty("visible", jper.get("visible").getAsBoolean());
                        cat.setAtributos(atributos);

                        if (jper.get("url_foto") != null && !jper.get("url_foto").isJsonNull()) {
                            cat.setUrl_foto(jper.get("url_foto").getAsString());
                        } else {
                            cat.setUrl_foto("N");
                        }

                        if (jper.get("grupos_id") != null && !jper.get("grupos_id").isJsonNull()) {
                            cat.setGrupos_id(jper.get("grupos_id").getAsLong());
                        } else {
                            cat.setGrupos_id(0);
                        }
                        categoriaInsertada = categoriaInsertada && pdao.procesarCategorias(i, cat, credencial);
                        if (categoriaInsertada) {
                            syncLog("{SUCCESS}=> CATEGORIA " + (i + 1) + "/" + size + " PROCESADA");
                            insert++;
                        } else {
                            syncLog("{ERROR DAO} => CATEGORIA " + (i + 1) + "/" + size + "NO PROCESADA");
                        }
                    } catch (Exception e) {
                        syncLog("{ERROR DAO} => CATEGORIA " + (i + 1) + "/" + size + "NO PROCESADA");
                    }
                }
                if (insert == size) {
                    syncLog("{SUCCESS}=>CATEGORIAS PROCESADAS CORRECTAMENTE");
                } else if (insert > 0) {
                    success = false;
                    syncLog("{ERROR DAO}=>ALGUNAS CATEGORIAS NO FUERON PROCESADAS");
                } else {
                    success = false;
                    syncLog("{ERROR DAO}=> CATEGORIAS NO PROCESADAS");
                }
            } catch (Exception e) {
                success = false;
                Logger.getLogger(SetupAsync.class.getName()).log(Level.SEVERE, null, e);
                syncLog("{ERROR DAO}=>CATEGORIAS NO PROCESADAS");
            } catch (DAOException e) {
                success = false;
                Logger.getLogger(SetupAsync.class.getName()).log(Level.SEVERE, null, e);
                syncLog("{ERROR DAO}=>CATEGORIAS NO PROCESADAS");
            }
        } else {
            success = false;
            syncLog("{ERROR WS}=>NO SE PUEDO DESCARGAR CATEGORIAS");
        }
        if (SincronizarView.jloader_categorias != null) {
            if (success) {
                SincronizarView.jloader_categorias.setIcon(
                        new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/chulito_on.png")));
            } else {
                SincronizarView.jloader_categorias.setIcon(
                        new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/error.png")));
            }
            SincronizarView.jbutton_reintentar_categorias.setIcon(
                    new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btReintentar.png"))); // NOI18N
        }
    }

    //AQUI RECUPERAN LOS MEDIOS DE PAGOS
    public void descargaMediosPagos() {
        if(!NovusUtils.getCircuitBreakerAllStatus()){
            NovusUtils.printLn("{INFO}=> Sinronizacion cancelada por el circuit breaker (descargaMediosPagos)");
            return;
        }
        if (SincronizarView.jloader_medios != null) {
            SincronizarView.jloader_medios.setIcon(
                    new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/chulito_off.png")));
        }
        boolean success = true;
        String logs = "";
        JsonObject json = new JsonObject();
        String url = NovusConstante.getServer(NovusConstante.SECURE_END_POINT_MEDIOS_PAGOS + "/" + credencial.getEmpresas_id());
        ClientWSAsync async = new ClientWSAsync("DESCARGA DE MEDIOS DE PAGOS", url, NovusConstante.GET, json, DEBUG);
        JsonObject response = null;
        if (SincronizarView.jloader_medios != null) {
            SincronizarView.jbutton_reintentar_medios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btSincro.gif")));
            syncLog("SINCRONIZANDO MEDIOS DE PAGOS...");
        }
        try {
            response = async.esperaRespuesta();
        } catch (Exception e) {
            success = false;
            syncLog("{ERROR WS}=> NO SE DESCARGARON MEDIOS PAGO");
        }
        if (response != null) {
            try {
                JsonArray array = response.get("data") != null && !response.get("data").isJsonNull()
                        ? response.getAsJsonArray("data")
                        : new JsonArray();
                int size = array.size();
                SetupDao pdao = new SetupDao();
                int insert = 0;
                for (int i = 0; i < size; i++) {
                    try {
                        if(!NovusUtils.getCircuitBreakerAllStatus()){
                            NovusUtils.printLn("{INFO}=> Sinronizacion cancelada por el circuit breaker (descargaMediosPagos)");
                            return;
                        }
                        JsonObject jper = array.get(i).getAsJsonObject();
                        MediosPagosBean med = new MediosPagosBean();
                        med.setId(jper.get("id").getAsLong());
                        med.setDescripcion(jper.get("descripcion").getAsString().toUpperCase());
                        med.setEstado(jper.get("estado").getAsString());
                        med.setCredito(jper.get("credito").getAsString().equals("S"));
                        med.setCambio(jper.get("cambio").getAsString().equals("S"));
                        if (jper.get("minimo_valor") != null && !jper.get("minimo_valor").isJsonNull()) {
                            med.setMinimo_valor(jper.get("minimo_valor").getAsFloat());
                        } else {
                            med.setMinimo_valor(0);
                        }
                        if (jper.get("maximo_valor") != null && !jper.get("maximo_valor").isJsonNull()) {
                            med.setMaximo_valor(jper.get("maximo_valor").getAsFloat());
                        } else {
                            med.setMaximo_valor(0);
                        }
                        if (jper.get("solicita_comprobante") != null
                                && !jper.get("solicita_comprobante").isJsonNull()) {
                            med.setComprobante(jper.get("solicita_comprobante").getAsString().equals("S"));
                        } else {
                            med.setComprobante(false);
                        }
                        if (jper.get("atributos") != null
                                && !jper.get("atributos").isJsonNull()) {
                            med.setAtributos(jper.get("atributos").getAsJsonObject());
                        } else {
                            med.setAtributos(null);
                        }
                        if (jper.has("codigo_adquiriente") && !jper.get("codigo_adquiriente").isJsonNull() && jper.get("codigo_adquiriente").getAsString() != null) {
                            med.setCodigoAdquiriente(jper.get("codigo_adquiriente").getAsString().trim());
                        } else {
                            med.setCodigoAdquiriente("");
                        }
                        success = success && pdao.procesarMediosPago(med, credencial);
                        if (success) {
                            syncLog("{SUCCESS}=>  MEDIO PAGO [" + med.getDescripcion() + "] " + (i + 1) + "/" + size + " PROCESADO");
                            insert++;
                        } else {
                            syncLog("{ERROR DAO}=>  MEDIO PAGO [" + med.getDescripcion() + "] " + (i + 1) + "/" + size + "NO SE PROCESO");
                        }

                    } catch (Exception ex) {
                        syncLog("{ERROR DAO}=>  MEDIO PAGO " + (i + 1) + "/" + size + "NO SE PROCESADO");
                        success = false;

                    } catch (DAOException ex) {
                        syncLog("{ERROR DAO}=>  MEDIO PAGO " + (i + 1) + "/" + size + "NO SE PROCESADO");
                        success = false;
                    }
                }
                if (insert == size) {
                    syncLog("{SUCCESS}=>MEDIOS PAGOS PROCESADAS CORRECTAMENTE");
                } else if (insert > 0) {
                    syncLog("{ERROR DAO}=>ALGUNOS MEDIOS DE PAGOS NO FUERON PROCESADOS");
                } else {
                    syncLog("{ERROR DAO}=> MEDIOS DE PAGO NO PROCESADOS");
                }
                SingletonMedioPago.ConetextDependecy.getUpdateImagenReferenciaMedioPago().execute();
                SingletonMedioPago.ConetextDependecy.getRecoverMedio().loadMedioPago();
            } catch (Exception a) {
                NovusUtils.printLn(a.getMessage());
                success = false;
                syncLog("{ERROR DAO}=> MEDIOS DE PAGO NO PROCESADOS");
            }

        } else {
            success = false;
            syncLog("{ERROR WS}=> NO SE DESCARGARON MEDIOS PAGO");
        }
        if (SincronizarView.jloader_categorias != null) {
            if (success) {
                SincronizarView.jloader_medios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/chulito_on.png")));
            } else {
                SincronizarView.jloader_medios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/error.png")));
            }
            SincronizarView.jbutton_reintentar_medios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btReintentar.png")));
        }
    }

    boolean success = true;

    String MENSAGE_ERROR = "{ERROR WS}=>NO SE PUEDO DESCARGAR KARDEX";

    public JsonObject requestInventarioBodega(TreeMap<String, String> Headers) {

        JsonObject result = null;

        String url = NovusConstante.getServer(NovusConstante.SECURE_END_POINT_INVENTARIO + "/" + credencial.getEmpresas_id());

        ClientWSAsync async = new ClientWSAsync("DESCARGA DE INVENTARIOS DE PRODUCTOS", url, NovusConstante.GET, null, DEBUG, false, Headers);
        try {
            result = async.esperaRespuesta();
        } catch (Exception e) {
            success = false;
            syncLog(MENSAGE_ERROR);
            SincronizarView.jbutton_reintentar_medios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btReintentar.png")));
        }
        return result;
    }

    public JsonObject requestInventarioBodegaProductos(TreeMap<String, String> Headers, long bodega, JsonArray productos) {

        JsonObject result = null;
        String url = NovusConstante.getServer(NovusConstante.SECURE_END_POINT_KARDEX + "/" + credencial.getEmpresas_id() + "/bodega/" + bodega);
        JsonObject Json = new JsonObject();
        Json.add("productos", productos);
        ClientWSAsync async = new ClientWSAsync("DESCARGA DE INVENTARIOS DE PRODUCTOS", url, NovusConstante.POST, Json, DEBUG, false, Headers);
        try {
            result = async.esperaRespuesta();
        } catch (Exception e) {
            success = false;
            syncLog(MENSAGE_ERROR);
        }
        return result;
    }

    public void descargaInventarioBodega() {
        if(!NovusUtils.getCircuitBreakerAllStatus()){
            NovusUtils.printLn("{INFO}=> Sinronizacion cancelada por el circuit breaker (descargaInventarioBodega)");
            return;
        }
        if (SincronizarView.jloader_kardex != null) {
            SincronizarView.jloader_kardex.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/chulito_off.png")));
        }

        TreeMap<String, String> header = new TreeMap<>();
        header.put("content-Type", "application/json");
        header.put("authorization", "123344");
        header.put("fecha", "2020-09-09T11:46:35-05:00");
        header.put("aplicacion", NovusConstante.APLICATION);
        header.put("identificadorDispositivo", "localhost");
        header.put("uuid", NovusConstante.UUID);
        header.put("password", "1111");
        header.put("versioncode", "" + Main.VERSION_CODE);
        header.put("versionapp", Main.VERSION_APP);

        int i = 0;
        int cant = 0;
        JsonArray producto = null;
        ArrayList<ProductoBean> bodegas = new ArrayList<>();
        JsonObject response = null;
        if (SincronizarView.jloader_kardex != null) {
            SincronizarView.jbutton_reintentar_kardex.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btSincro.gif")));
            syncLog("SINCRONIZANDO KARDEX...");
        }
        response = bodega == 0 ? requestInventarioBodega(header) : requestInventarioBodegaProductos(header, bodega, productos);
        if (response != null) {
            try {
                productos = response.get("data") != null && response.get("data").isJsonObject() ? response.getAsJsonObject("data").get("inventarios").getAsJsonArray() : new JsonArray();
                cant = productos.size();

                if (cant > 0) {
                    syncLog("{SUCCESS}=> KARDEX DESCARGADO");
                    for (JsonElement product : productos) {
                        if(!NovusUtils.getCircuitBreakerAllStatus()){
                            NovusUtils.printLn("{INFO}=> Sinronizacion cancelada por el circuit breaker (descargaInventarioBodega)");
                            return;
                        }
                        ProductoBean pp = new ProductoBean();
                        if (product.getAsJsonObject().get("ct_bodegas_id") != null && !product.getAsJsonObject().get("ct_bodegas_id").isJsonNull()) {
                            pp.setBodega_producto_id(product.getAsJsonObject().get("ct_bodegas_id").getAsLong());
                        }
                        if (product.getAsJsonObject().get("ct_productos_id") != null && !product.getAsJsonObject().get("ct_productos_id").isJsonNull()) {
                            pp.setId(product.getAsJsonObject().get("ct_productos_id").getAsLong());
                        }
                        if (product.getAsJsonObject().get("saldo") != null && !product.getAsJsonObject().get("saldo").isJsonNull()) {
                            pp.setSaldo(product.getAsJsonObject().get("saldo").getAsFloat());
                        }
                        if (product.getAsJsonObject().get("cantidad_minima") != null && !product.getAsJsonObject().get("cantidad_minima").isJsonNull()) {
                            pp.setCantidadMinima(product.getAsJsonObject().get("cantidad_minima").getAsInt());
                        }
                        if (product.getAsJsonObject().get("cantidad_maxima") != null && !product.getAsJsonObject().get("cantidad_maxima").isJsonNull()) {
                            pp.setCantidadMaxima(product.getAsJsonObject().get("cantidad_maxima").getAsInt());
                        }
                        if (product.getAsJsonObject().get("unidades_id") != null && !product.getAsJsonObject().get("unidades_id").isJsonNull()) {
                            pp.setUnidades_medida_id(product.getAsJsonObject().get("unidades_id").getAsLong());
                        }
                        if (product.getAsJsonObject().get("tiempo_reorden") != null && !product.getAsJsonObject().get("tiempo_reorden").isJsonNull()) {
                            pp.setTiempoReorden(product.getAsJsonObject().get("tiempo_reorden").getAsInt());
                        } else {
                            pp.setTiempoReorden(0);
                        }
                        if (product.getAsJsonObject().get("costos") != null
                                && !product.getAsJsonObject().get("costos").isJsonNull()) {
                            pp.setCosto(product.getAsJsonObject().get("costos").getAsFloat());
                        } else {
                            pp.setCosto(0);
                        }

                        if (product.getAsJsonObject().get("ct_bodegas_id") != null
                                && !product.getAsJsonObject().get("ct_bodegas_id").isJsonNull()
                                && product.getAsJsonObject().get("ct_productos_id") != null
                                && !product.getAsJsonObject().get("ct_productos_id").isJsonNull()) {
                            bodegas.add(pp);
                        }

                    }
                    SetupDao pdao = new SetupDao();
                    int insertado = 0;
                    if (bodega == 0) {
                        pdao.limpiarInventarios();
                    }
                    for (ProductoBean listaProductosbodegas : bodegas) {
                        try {
                            if(!NovusUtils.getCircuitBreakerAllStatus()){
                                NovusUtils.printLn("{INFO}=> Sinronizacion cancelada por el circuit breaker (descargaInventarioBodega)");
                                return;
                            }
                            if (pdao.existeBodega(listaProductosbodegas.getBodega_producto_id())) {
                                boolean proceso = pdao.procesarInventario(i, credencial.getEmpresas_id(), listaProductosbodegas);
                                success = proceso;
                                if (success) {
                                    syncLog("{SUCCESS}=> KARDEX " + (i + 1) + "/" + cant + " PROCESADO");
                                    insertado++;
                                } else {
                                    syncLog("{ERROR DAO}=> KARDEX " + (i + 1) + "/" + cant + " PRODUCTO [" + listaProductosbodegas.getId() + "] NO ENCONTRADO");
                                }
                            }

                        } catch (DAOException e) {
                            syncLog("{ERROR DAO}=> KARDEX " + (i + 1) + "/" + cant + " NO PROCESADO");
                            success = false;
                            Logger.getLogger(SetupAsync.class.getName()).log(Level.SEVERE, null, e);
                        }
                        i++;
                    }
                    if (insertado == cant) {
                        syncLog("{SUCCESS}=>KARDEX PROCESADO CORRECTAMENTE");
                    } else if (insertado > 0) {
                        syncLog("{ERROR DAO}=>ALGUNOS INVENTARIOS NO FUERON PROCESADOS");
                    } else {
                        syncLog("{ERROR DAO}=> INVENTARIOS NO PROCESADOS");

                    }
                    pdao.LimpiarInventariosInactivos();
                    pdao.eliminarProductosDuplicados();
                } else {
                    syncLog("{INFO}=> PRODUCTOS NO ASIGNADOS A BODEGA");
                    success = false;
                }
            } catch (Exception ex) {
                NovusUtils.printLn(ex.getMessage());
                syncLog("{ERROR DAO}=> INVENTARIOS NO PROCESADOS");
                Logger.getLogger(SetupAsync.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            success = false;
            syncLog("{ERROR WS}=>NO SE PUEDO DESCARGAR KARDEX");
        }

        if (SincronizarView.jloader_empresa != null) {
            if (success) {
                SincronizarView.jloader_kardex.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/chulito_on.png")));
            } else {
                SincronizarView.jloader_kardex.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/error.png")));
            }
            SincronizarView.jbutton_reintentar_kardex.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btReintentar.png")));
        }
    }

    public void descargaDatosSurtidores() {
        if(!NovusUtils.getCircuitBreakerAllStatus()){
            NovusUtils.printLn("{INFO}=> Sinronizacion cancelada por el circuit breaker (descargaDatosSurtidores)");
            return;
        }
        boolean success = true;
        if (SincronizarView.jloader_surtidores != null) {
            SincronizarView.jloader_surtidores.setIcon(
                    new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/chulito_off.png")));
        }
        String url = NovusConstante.getServer(NovusConstante.SECURE_END_POINT_SURTIDORES + "/" + credencial.getEquipos_id());
        ClientWSAsync async = new ClientWSAsync("DESCARGA DE DATOS DE SURTIDORES", url, NovusConstante.GET, DEBUG);
        JsonObject response = null;
        if (SincronizarView.jloader_surtidores != null) {
            SincronizarView.jbutton_reintentar_surtidores
                    .setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btSincro.gif")));
            syncLog("SINCRONIZANDO DATOS SURTIDOR...");
        }
        success = success && descargarProductosCombustible();
        try {
            response = async.esperaRespuesta();
        } catch (Exception e) {
            success = false;
            syncLog("{ERROR WS}=> NO SE PUDO DESCARGAR INFO SURTIDORES");
        }
        if (response != null) {
            JsonObject data = response.get("data") != null && response.get("data").isJsonObject() ? response.getAsJsonObject("data") : null;
            if (data != null) {
                try {
                    JsonArray surtidoresArray = data.get("surtidores").getAsJsonArray();
                    JsonArray tanquesArray = data.get("tanques").getAsJsonArray();
                    JsonArray unidadesArray = data.get("unidades").getAsJsonArray();
                    JsonArray protocolosArray = data.get("protocolos").getAsJsonArray();
                    JsonArray tiposArray = data.get("tipos").getAsJsonArray();
                    SetupDao dao = new SetupDao();
                    success = success && dao.guardarEstadosSurtidor();
                    for (JsonElement unidad : unidadesArray) {
                        success = success && dao.createUnidades(unidad.getAsJsonObject());
                        if (success) {
                            syncLog("{SUCCESS}=> UNIDAD [" + unidad.getAsJsonObject().get("descripcion").getAsString() + "] PROCESADA");
                        } else {
                            syncLog("{ERROR DAO}=> NO SE PUDIERON PROCESAR UNIDAD [" + unidad.getAsJsonObject().get("descripcion").getAsString() + "]");
                        }
                    }

                    dao.eliminarAforo();
                    StringBuilder str = new StringBuilder();
                    for (JsonElement tanque : tanquesArray) {
                        if(!NovusUtils.getCircuitBreakerAllStatus()){
                            NovusUtils.printLn("{INFO}=> Sinronizacion cancelada por el circuit breaker (descargaDatosSurtidores)");
                            return;
                        }
                        JsonObject jsonTanque = tanque.getAsJsonObject();
                        str.append(jsonTanque.get("id").getAsLong());
                        str.append(",");
                        success = success && dao.createTanque(jsonTanque);
                        if (success) {
                            syncLog("{SUCCESS}=> TANQUE [" + jsonTanque.get("bodega").getAsString() + "] PROCESADO");
                        } else {
                            syncLog("{ERROR DAO}=> TANQUE [" + jsonTanque.get("bodega").getAsString() + "] NO PUDO SER PROCESADO");
                        }
                        JsonArray aforoArray = jsonTanque.get("aforo").getAsJsonArray();
                        success = success && dao.registrarAforo(aforoArray);
                        if (success) {
                            syncLog("{SUCCESS}=> TANQUE [" + jsonTanque.get("bodega").getAsString() + "]: AFORO  PROCESADO");
                        } else {
                            syncLog("{ERROR DAO}=> TANQUE [" + jsonTanque.get("bodega").getAsString() + "]: AFORO   NO PUDO SER PROCESADO");
                        }
                    }
                    dao.limpiarTanquesExternos(str.toString().substring(0, str.toString().length() - 1));
                    for (JsonElement protocolo : protocolosArray) {
                        if(!NovusUtils.getCircuitBreakerAllStatus()){
                            NovusUtils.printLn("{INFO}=> Sinronizacion cancelada por el circuit breaker (descargaDatosSurtidores)");
                            return;
                        }
                        JsonObject jsonProtocolos = protocolo.getAsJsonObject();
                        success = success && dao.createProtocolo(jsonProtocolos);
                        if (success) {
                            syncLog("{SUCCESS}=> PROTOCOLO [" + jsonProtocolos.get("nombre").getAsString() + "] SURTIDOR PROCESADOS");
                        } else {
                            syncLog("{ERROR DAO}=>PROTOCOLO [" + jsonProtocolos.get("nombre").getAsString() + "] SURTIDOR NO PUDO SER PROCESADO");
                        }
                    }

                    for (JsonElement tipo : tiposArray) {
                        if(!NovusUtils.getCircuitBreakerAllStatus()){
                            NovusUtils.printLn("{INFO}=> Sinronizacion cancelada por el circuit breaker (descargaDatosSurtidores)");
                            return;
                        }
                        JsonObject jsonTipos = tipo.getAsJsonObject();
                        success = success && dao.createTiposSurtidor(jsonTipos);
                        if (success) {
                            syncLog("{SUCCESS}=> TIPOS SURTIDORES PROCESADOS");
                        } else {
                            syncLog("{ERROR DAO}=> NO SE PUDO PROCESAR TIPO SURTIDORES");
                        }
                    }
                    int index = 0;
                    for (JsonElement surtidor : surtidoresArray) {
                        if(!NovusUtils.getCircuitBreakerAllStatus()){
                            NovusUtils.printLn("{INFO}=> Sinronizacion cancelada por el circuit breaker (descargaDatosSurtidores)");
                            return;
                        }
                        JsonObject jsonSurtidor = surtidor.getAsJsonObject();
                        success = success && dao.createSurtidor(jsonSurtidor, index);
                        JsonArray surtidoresDetallesArray = jsonSurtidor.get("surtidores_detalles").getAsJsonArray();
                        for (JsonElement detalle : surtidoresDetallesArray) {
                            success = success && dao.createDetallesSurtidor(detalle.getAsJsonObject());
                        }
                        if (success) {
                            syncLog("{SUCCESS}=> INFO SURTIDOR PROCESADA");
                        } else {
                            syncLog("{ERROR DAO}=> NO SE PROCESO INFO SURTIDOR");
                        }
                        index++;
                    }
                } catch (SQLException e) {
                    success = false;
                    syncLog("[ERROR DAO>]=> OCURRIERON ERRORES AL PROCESAR INFO SURTIDORES");
                    Logger.getLogger(SetupAsync.class.getName()).log(Level.SEVERE, null, e);
                } catch (Exception e) {
                    success = false;
                    Logger.getLogger(SetupAsync.class.getName()).log(Level.SEVERE, null, e);

                    syncLog("{ERROR DAO}=> OCURRIERON ERRORES AL PROCESAR INFO SURTIDORES");
                } catch (DAOException ex) {
                    Logger.getLogger(SetupAsync.class.getName()).log(Level.SEVERE, null, ex);

                    success = false;
                    syncLog("ERROR DAO}=> OCURRIERON ERRORES AL PROCESAR INFO SURTIDORES");
                }
            } else {
                success = false;
                syncLog("{INFO }=> NO HAY INFO SURTIDORES");
            }
        } else {
            success = false;
            syncLog("{ERROR WS}=> NO SE PUDO DESCARGAR INFO SURTIDORES");

        }
        if (SincronizarView.jloader_empresa != null) {

            if (success) {
                SincronizarView.jloader_surtidores.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/chulito_on.png")));
            } else {
                SincronizarView.jloader_surtidores.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/error.png")));
            }
            SincronizarView.jbutton_reintentar_surtidores.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btReintentar.png")));
        }
    }

    private boolean descargarProductosCombustible() {
        if(!NovusUtils.getCircuitBreakerAllStatus()){
            NovusUtils.printLn("{INFO}=> Sinronizacion cancelada por el circuit breaker (descargarProductosCombustible)");
            return false;
        }
        boolean success = true;
        String logs = "";
        TreeMap<String, String> header = new TreeMap<>();
        header.put("content-Type", "application/json");
        header.put("authorization", "123344");
        header.put("fecha", "2020-09-09T11:46:35-05:00");
        header.put("aplicacion", NovusConstante.APLICATION);
        header.put("identificadorDispositivo", "localhost");
        header.put("uuid", NovusConstante.UUID);
        header.put("password", "1111");
        header.put("versioncode", "" + Main.VERSION_CODE);
        header.put("versionapp", Main.VERSION_APP);
        String url = NovusConstante.getServer(NovusConstante.SECURE_END_POINT_PRODUCTOS_POS_COMBUSTIBLE + "/" + credencial.getEquipos_id());
        ClientWSAsync async = new ClientWSAsync("DESCARGA DE DATOS DE SURTIDORES", url, NovusConstante.GET, null, DEBUG, false, header);
        JsonObject response = null;
        try {
            response = async.esperaRespuesta();
        } catch (Exception e) {
            success = false;
            syncLog("{ERROR WS}=> PRODUCTOS COMBUSTIBLES NO PUDIERON SER DESCARGADOS");
        }
        if (response != null) {

            JsonObject data = response.get("data") != null && response.get("data").isJsonObject()
                    ? response.get("data").getAsJsonObject()
                    : null;
            if (data != null) {
                try {
                    JsonArray tiposProductosArray = data.get("productos_tipos") != null
                            && data.get("productos_tipos").isJsonArray() ? data.get("productos_tipos").getAsJsonArray()
                            : new JsonArray();
                    JsonArray familiasProductosArray = data.get("productos_familias") != null
                            && data.get("productos_familias").isJsonArray()
                            ? data.get("productos_familias").getAsJsonArray()
                            : new JsonArray();
                    JsonArray unidadesProductosArray = data.get("productos_unidades") != null
                            && data.get("productos_unidades").isJsonArray()
                            ? data.get("productos_unidades").getAsJsonArray()
                            : new JsonArray();
                    JsonArray productosArray = data.get("productos") != null && data.get("productos").isJsonArray()
                            ? data.get("productos").getAsJsonArray()
                            : new JsonArray();
                    SetupDao dao = new SetupDao();
                    success = success && dao.crearProductosCombustible(familiasProductosArray, tiposProductosArray, unidadesProductosArray, productosArray);
                    if (success) {
                        syncLog("{SUCCESS}=>PRODUCTOS COMBUSTIBLES PROCESADOS");
                    } else {
                        syncLog("{ERROR DAO}=>PRODUCTOS COMBUSTIBLES NO PUDIERON SER PROCESADOS");
                    }
                } catch (Exception e) {
                    success = false;
                    syncLog("{ERROR DAO}=>PRODUCTOS COMBUSTIBLES NO PUDIERON SER PROCESADOS");
                } catch (DAOException ex) {
                    success = false;
                    syncLog("{ERROR DAO}=>PRODUCTOS COMBUSTIBLES NO PUDIERON SER PROCESADOS");
                }
            } else {
                success = false;
                syncLog("{INFO}=>PRODUCTOS COMBUSTIBLES NO PUDIERON SER DESCARGADOS");
            }

        } else {
            success = false;
            syncLog("{ERROR WS}=>  PRODUCTOS COMBUSTIBLES NO PUDIERON SER DESCARGADOS");
        }
        return success;
    }

    private void sincronizarHorarios() {
        if(!NovusUtils.getCircuitBreakerAllStatus()){
            NovusUtils.printLn("{INFO}=> Sinronizacion cancelada por el circuit breaker (sincronizarHorarios)");
            return;
        }
        try {
            SetupDao dao = new SetupDao();
            dao.actulizarHorarios();
        } catch (DAOException ex) {
            Logger.getLogger(SetupAsync.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
