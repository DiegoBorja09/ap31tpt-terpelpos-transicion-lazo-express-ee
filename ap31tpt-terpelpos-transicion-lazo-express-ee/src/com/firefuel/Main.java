package com.firefuel;

import com.WT2.appTerpel.application.UseCase.CreateImageFolder;
import com.WT2.appTerpel.application.UseCase.Payment.Listing.RecoverDataMedioPago;
import com.WT2.appTerpel.application.UseCase.RecoverMedioPagoImage;
import com.WT2.Containers.Dependency.SingletonMedioPago;
import com.WT2.appTerpel.domain.entities.MedioPagoImageBean;
import com.application.useCases.facturacionelectronica.FindAllTiposIdentificacionDianUseCase;
import com.application.useCases.movimientos.ValidatePaymentMethodShiftUseCase;
import com.application.useCases.equipos.GetMacEquipoUseCase;
import com.application.useCases.persons.GetPrincipalTurnoUseCase;
import com.bean.CredencialBean;
import com.bean.EquiposAutorizacionBean;
import com.bean.PersonaBean;
import com.bean.TipoNegocio;
import com.controllers.ControllerSync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.EntradaCombustibleDao.EntradaCombustibleDao;
import com.dao.EquipoDao;
import com.dao.FacturacionElectronicaDao;
import com.dao.MovimientosDao;
import com.dao.PersonasDao;
import com.dao.Postgrest;
import com.dao.RumboDao;
import com.dao.SetupDao;
import com.dao.SurtidorDao;
import com.dao.ListenToNotifyMessage;
import com.dao.ventaCursoDAO.VentaCursoDAO;
import com.firefuel.componentes.notificaciones.error.NotificacionHistorialerror;
import com.firefuel.controlImpresion.ControlImpresion;
import com.firefuel.facturacion.electronica.AsignacionclienteRemision;
import com.firefuel.facturacion.electronica.FacturacionElectronica;
import com.firefuel.facturacion.electronica.ReenviodeFE;
import com.firefuel.recuperadorMarketCan.SincronizadorMarketCanPOS;
import com.firefuel.recuperadorMarketCan.dao.SincronizadorMarketCanDAO;
import com.firefuel.facturacion.electronica.ValidarConfiguracionFE;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.infrastructure.database.ExampleConnection;
import com.server.api.ServerComandoWS;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.security.Security;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.application.useCases.ventas.VentaEnCursoUseCase;

public class Main {

    public GetPrincipalTurnoUseCase getPrincipalTurnoUseCase;
    public static String APPLICATION = "LAZO EXPRESS PRS";
    public static String VERSION_APP = "TERPEL";
    public static ExampleConnection exampleConnection = new ExampleConnection();
    public final static int COMPILADO = 4;
    public final static String APLICACION_CODE = "21." + COMPILADO + ".2";
    public static String VERSION_CODE = "" + APLICACION_CODE;
    public static String VERSION_ACTUAL = ""
            + "CAMBIOS EN INTERFAZ EN BOTONES DE IMPRESIÓN (BLOQUEOS Y REFRESCO AL IMPRIMIR)"
            + "ASIGNACIÓN DE PUERTO Y URL API DE NUEVO SERVICIO DE IMPRESIÓN";

    public static boolean FACTURACION_NEGATIVO = true;
    public static boolean CENTRALIZADOR = true;
    public static boolean SYNC = false;
    public static boolean PROGRAMA_REGISTRADO = true;
    public static boolean DEBUG_REPORTE_EQUIPO = false;
    public static boolean LECTURAS_DISPONIBLE = false;
    public static int puertoTotalizadores = 8019;

    public static InfoViewController info = null;

    public static CredencialBean credencial;
    public static TreeMap<String, String> parametros;
    public static TreeMap<String, String> parametrosCore;
    public static Gson gson = new GsonBuilder().setDateFormat(NovusConstante.FORMAT_FULL_DATE_ISO).setPrettyPrinting()
            .create();
    public static PersonaBean persona;

    public static SimpleDateFormat SDFSQL = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);

    public static Gson GSON = new GsonBuilder().setDateFormat(NovusConstante.FORMAT_FULL_DATE_ISO).setPrettyPrinting()
            .create();

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    static int ADMIN = -1;

    public static Postgrest dbCore;
    public static Postgrest dbRegistry;
    public static Connection conexionCore;
    public static Connection conexionRegistry;

    public static Postgrest dbCoreAsync;
    public static Postgrest dbRegistryAsync;
    public static Connection conexionCoreAsync;
    public static Connection conexionRegistryAsync;

    public static SetupDao sdao = new SetupDao();
    public static SurtidorDao surtidorDao = new SurtidorDao();
    public static EquipoDao edao = new EquipoDao();
    public static RecoverDataMedioPago recoverData = new RecoverDataMedioPago(sdao);
    public static RecoverMedioPagoImage recoverLista = new RecoverMedioPagoImage();
    public static ArrayList<MedioPagoImageBean> listMedios;

    public static RumboDao rumboDao = new RumboDao();
    public static EntradaCombustibleDao entradaCombustibleDao = new EntradaCombustibleDao();
    public static boolean SIN_SURTIDOR = false;
    public static String TIPO_NEGOCIO = "COMB";

    public static VentaCursoDAO ventaCursoDAO = new VentaCursoDAO();
    public static FacturacionElectronicaDao fdao = new FacturacionElectronicaDao();
    public static TreeMap<Integer, TipoNegocio> INFO_TIPO_NEGOCIOS = new TreeMap<>();

    public static SincronizadorMarketCanDAO sincronizarMarketCanDAO = new SincronizadorMarketCanDAO();
    public static MovimientosDao mdao = new MovimientosDao();
    public static ValidatePaymentMethodShiftUseCase validatePaymentMethodShiftUseCase;

    public static GetMacEquipoUseCase getMacEquipoUseCase;


    public static void main(String[] args) {
        if (args.length > 0) {
            NovusConstante.POSGRES_PORT = args[0];
        }
        Security.setProperty("crypto.policy", "unlimited");
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        NovusUtils.printLn("***************************************");
        NovusUtils.printLn("*** INICIANDO APP , VERSION " + VERSION_CODE);
        NovusUtils.printLn("*** AUTOR ORGANIZACIÓN TERPEL");
        NovusUtils.printLn("*** VERSION ACTUAL : " + VERSION_ACTUAL.replace(",", "\n"));
        NovusUtils.printLn("***************************************");
        // exampleConnection.main(new String[0]);
        // APLICACION_CODE = "21." + COMPILADO +".2";
        // VERSION_CODE = APLICACION_CODE;
        Main main = new Main();
        main.loadFonts();
        NovusUtils.printLn("*********");
        NovusUtils.printLn("Creando carpeta de imagenes medio de pago");
        new CreateImageFolder().execute();
        dbCore = new Postgrest("lazoexpresscore");
        dbRegistry = new Postgrest("lazoexpressregistry");

        try {
            conexionCore = dbCore.conectar();
            conexionRegistry = dbRegistry.conectar();
        } catch (Exception | DAOException e) {
            NovusUtils.printLn("***** SIN CONEXION A LA BD ****");
            System.exit(0);
        }

        dbCoreAsync = new Postgrest("lazoexpresscore");
        dbRegistryAsync = new Postgrest("lazoexpressregistry");
        Utils.setTimeout("", () -> {
            try {
                conexionCoreAsync = dbCoreAsync.conectar();
                conexionRegistryAsync = dbRegistryAsync.conectar();
            } catch (DAOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }, 0);
        EquipoDao dao = new EquipoDao();
        dao.restructurarBD();
        dao.establecerParametrosInicialesCore();
        // dao.actualizarDatafonoRutaImagen();
        cargarParametros();

        // INICIALIZAR CACHE LIVIANO AL ARRANCAR LA APLICACIÓN
        System.out.println(" Inicializando sistema de cache...");
        try {
            com.infrastructure.cache.KioscoCacheServiceLiviano cache = 
                com.infrastructure.cache.KioscoCacheServiceLiviano.getInstance();
            System.out.println(" Sistema de cache inicializado correctamente");
        } catch (Exception e) {
            System.err.println(" Error inicializando cache (continuando sin cache): " + e.getMessage());
        }

        // INICIALIZAR CACHE WACHER_PARAMETROS AL ARRANCAR LA APLICACIÓN
        System.out.println(" Inicializando cache wacher_parametros...");
        try {
            com.infrastructure.cache.WacherParametrosCacheSimple wacherCache = 
                com.infrastructure.cache.WacherParametrosCacheSimple.getInstance();
            System.out.println(" Cache wacher_parametros inicializado correctamente");
            
            // Mostrar contenido del cache de parámetros
            wacherCache.mostrarContenidoCacheParametros();
        } catch (Exception e) {
            System.err.println(" Error inicializando cache wacher_parametros (continuando sin cache): " + e.getMessage());
            e.printStackTrace();
        }

        MovimientosDao mdao = new MovimientosDao();
        new VentaEnCursoUseCase("I", NovusUtils.getTipoNegocioComplementario()).execute();
        new VentaEnCursoUseCase("I", NovusConstante.PARAMETER_CAN).execute();
     


        try {
            cargarMediosPago();
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            Main.credencial = new CredencialBean();
            dao.getInformacionSurtidorIsla();
            Main.credencial.setId(1);
            Main.credencial.setEquipos_id(dao.findEquipoId());
            Main.credencial.setEmpresas_id(dao.findEmpresaEquipoId());
            getMacEquipoUseCase = new GetMacEquipoUseCase(Main.credencial.getEquipos_id());
            Main.credencial.setMac(getMacEquipoUseCase.execute());
            Main.credencial.setEmpresa(dao.findEmpresa(Main.credencial));
        } catch (DAOException ex) {
            Logger.getLogger(InfoViewController.class.getName()).log(Level.SEVERE, null, ex);
            NovusUtils.printLn(Main.class.getName() + ":" + ex.getMessage());
        }

        ServerComandoWS webserver = new ServerComandoWS();
        webserver.start();
        reenvioDeFeProgramado();
        cargarInformacionError();
        cargarMediosIdentificacion();
        cargarParametroFe();
        cargaParametrosAppterepl();
        sincronizarVentasMarketCanPOS();
        consultarVentasPedientesImpresion();
        try {
            EquiposAutorizacionBean eqAutorizacion;
            eqAutorizacion = dao.getEquipo();
            if (eqAutorizacion == null || Main.credencial.getEquipos_id() == 0
                    || Main.credencial.getEmpresa() == null) {

                RegistroEquipoView autorizacionView = new RegistroEquipoView();
                autorizacionView.setVisible(true);

            } else {
                // PersonasDao pdao = new PersonasDao();
                // PersonaBean perso = pdao.getPrincipalTurno();
                GetPrincipalTurnoUseCase getPrincipalTurnoUseCase = new GetPrincipalTurnoUseCase();
                PersonaBean perso = getPrincipalTurnoUseCase.execute();
                // NovusUtils.printLn("getNombre en GetPrincipalTurnoUseCase: " +
                // perso.getNombre());

                if (perso != null) {
                    Main.persona = perso;
                } else {
                    // Fallback: Si no se encuentra un turno principal,
                    // intentar obtener el primer promotor de la lista de jornadas activas
                    try {
                        com.application.useCases.movimientos.ObtenerPromotoresJornadaUseCase obtenerPromotoresUseCase = new com.application.useCases.movimientos.ObtenerPromotoresJornadaUseCase();
                        java.util.List<com.bean.PersonaBean> promotores = obtenerPromotoresUseCase.execute();
                        if (promotores != null && !promotores.isEmpty()) {
                            Main.persona = promotores.get(0); // Tomar el primer promotor como principal
                        }
                    } catch (Exception ex) {
                        java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.WARNING,
                                "No se pudieron obtener promotores de jornadas activas", ex);
                    }
                }

                Main.info = new InfoViewController();
                Main.info.setVisible(true);

                InfoViewController.validarConexionInternet();

                ControllerSync sync = new ControllerSync();
                sync.setDaemon(true);
                sync.setPriority(Thread.MAX_PRIORITY);
                sync.start();
            }
        } catch (Exception | DAOException e) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
        }

        try {
            Main.pgNotify();
        } catch (Exception e) {
        }
    }

    public static void cargarMediosIdentificacion() {
        FindAllTiposIdentificacionDianUseCase findAllTiposIdentificacionDianUseCase = new FindAllTiposIdentificacionDianUseCase();
        findAllTiposIdentificacionDianUseCase.execute();

    }

    public static void cargarMediosPago() throws SQLException {
        // recoverData.loadMedioPago();
        // listMedios = recoverLista.execute();
        SingletonMedioPago.ConetextDependecy.getRecoverMedio().loadMedioPago();
        listMedios = SingletonMedioPago.ConetextDependecy.getMedioPago().execute();
    }

    public static void cargarParametroFe() {
        FacturacionElectronicaDao facturacionElectronicaDao = new FacturacionElectronicaDao();
        NovusConstante.IS_DEFAULT_FE = facturacionElectronicaDao.isDefaultFe();
    }

    public static void cargaParametrosAppterepl() {
        NovusConstante.TIEMPO_MENSAJE_APPTERPEL = SingletonMedioPago.ConetextDependecy
                .getRecuperarTiempoMensajeAppterpel().execute(null);
        NovusUtils.printLn("El tiempo para mostar mensajes appterpel es de " + NovusConstante.TIEMPO_MENSAJE_APPTERPEL);

    }

    public static void reenvioDeFeProgramado() {

        Timer temporizador = new Timer();
        int tiempo = getParametroIntCoreCache("TIEMPO_REENVIO_FE");
        NovusUtils.printLn("TIEMPO_REENVIO_FE: " + tiempo);
        if (tiempo <= 60000) {
            tiempo = 60000;
        }
        temporizador.schedule(new ValidarConfiguracionFE(), tiempo, tiempo);

    }

    public static void sincronizarVentasMarketCanPOS() {
        // NovusUtils.printLn("************************ " + "Iniciando Sincronizador
        // Ventas MARKET - CAN POS " + "*************************");
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new SincronizadorMarketCanPOS(),
                0, 60,
                TimeUnit.SECONDS);
    }

    public static void consultarVentasPedientesImpresion() {
        ControlImpresion controlImpresion = new ControlImpresion();
        controlImpresion.iniciarProceso();
    }

    public static Connection obtenerConexion(String db) {
        Connection cn = null;
        try {
            if (db.equals("lazoexpressregistry")) {
                cn = dbRegistry.conectar();
            } else if (db.equals("lazoexpresscore")) {
                cn = dbCore.conectar();
            }
        } catch (Exception | DAOException e) {
            NovusUtils.printLn("ERROR EN [obtenerConexion]" + e.getMessage());
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
        }
        return cn;
    }

    public static Connection obtenerConexionAsync(String db) {
        Connection cn = null;
        try {
            if (db.equals("lazoexpressregistry")) {
                cn = dbRegistryAsync.conectar();
            } else if (db.equals("lazoexpresscore")) {
                cn = dbCoreAsync.conectar();
            }
        } catch (Exception | DAOException e) {
            NovusUtils.printLn("ERROR EN [obtenerConexion]" + e.getMessage());
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
        }
        return cn;
    }

    public static void cargarParametros() {
        parametros = EquipoDao.getParametros("lazoexpressregistry");
        parametrosCore = EquipoDao.getParametros("lazoexpresscore");
        if (!parametros.containsKey(NovusConstante.PREFERENCE_IP_IMPRESORA)) {
            String value = "127.0.0.1";
            parametros.put(NovusConstante.PREFERENCE_IP_IMPRESORA, value);
            EquipoDao.setParametro(NovusConstante.PREFERENCE_IP_IMPRESORA, value);
        }
        if (!parametros.containsKey(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE)) {
            parametros.put(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE,
                    NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE_IP);
            EquipoDao.setParametro(NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE,
                    NovusConstante.PREFERENCE_IP_IMPRESORA_SOURCE_IP);
        }
        String value = getParametroCore(NovusConstante.PREFERENCE_HOST_SERVER, false);
        if (value != null && !value.isEmpty()) {
            NovusConstante.HOST_END_POINT = value;
        }
        SIN_SURTIDOR = SingletonMedioPago.ConetextDependecy.getValidateModeWithoutPump().execute(null);
        TIPO_NEGOCIO = SingletonMedioPago.ConetextDependecy.getValidateBusinessType().execute(null);
        INFO_TIPO_NEGOCIOS = fdao.informacionTiposNegocios();
    }

    public static boolean getParametroCoreBoolean(String parametro, boolean recargar) {

        if (recargar) {
            cargarParametros();
        }
        if (parametrosCore.containsKey(parametro)) {
            boolean status = parametrosCore.get(parametro).equals("S");
            NovusUtils.printLn("[getParametroCoreBoolean]" + parametro + (status ? " TRUE" : " FALSE"));
            return status;
        }
        NovusUtils.printLn("[getParametroCoreBoolean]" + parametro + " FALSE");
        return false;
    }

    public static String getParametroCore(String parametro, boolean recargar) {
        // NovusUtils.printLn("[getParametroCore]" + parametro);
        if (recargar) {
            cargarParametros();
        }
        if (parametrosCore.containsKey(parametro)) {
            return parametrosCore.get(parametro);
        }
        return "";
    }
    
    /**
     * Versión con cache del método getParametroCore
     * Usa el cache de wacher_parametros para consultas más rápidas
     */
    public static String getParametroCoreCache(String parametro) {
        try {
            com.infrastructure.cache.WacherParametrosCacheSimple cache = 
                com.infrastructure.cache.WacherParametrosCacheSimple.getInstance();
            String valor = cache.getParameter(parametro);
            return valor != null ? valor : "";
        } catch (Exception e) {
            // Fallback al método tradicional si hay error en cache
            NovusUtils.printLn("Error en cache, usando método tradicional: " + e.getMessage());
            return getParametroCore(parametro, false);
        }
    }

    public static int getParametroIntCore(String parametro, boolean recargar) {
        NovusUtils.printLn("[getParametroIntCore]" + parametro);
        NovusUtils.printLn("[parametrosCore]" + parametrosCore);

        if (recargar) {
            cargarParametros();
        }
        if (parametrosCore.containsKey(parametro)) {
            try {
                return Integer.parseInt(parametrosCore.get(parametro));
            } catch (NumberFormatException e) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return -1;
    }
    
    /**
     * Versión con cache del método getParametroIntCore
     * Usa el cache de wacher_parametros para consultas más rápidas
     */
    public static int getParametroIntCoreCache(String parametro) {
        try {
            com.infrastructure.cache.WacherParametrosCacheSimple cache = 
                com.infrastructure.cache.WacherParametrosCacheSimple.getInstance();
            String valor = cache.getParameter(parametro);
            if (valor != null && !valor.isEmpty()) {
                try {
                    return Integer.parseInt(valor);
                } catch (NumberFormatException e) {
                    NovusUtils.printLn("Error parseando parámetro " + parametro + " = " + valor);
                    return -1;
                }
            }
            return -1;
        } catch (Exception e) {
            // Fallback al método tradicional si hay error en cache
            NovusUtils.printLn("Error en cache, usando método tradicional: " + e.getMessage());
            return getParametroIntCore(parametro, false);
        }
    }

    public static boolean getParametroBoolean(String parametro, boolean recargar) {
        NovusUtils.printLn("[getParametroBoolean]" + parametro);
        if (recargar) {
            cargarParametros();
        }
        if (parametros.containsKey(parametro)) {
            return parametros.get(parametro).equals("S");
        }
        return false;
    }

    public static String getParametro(String parametro, boolean recargar) {
        NovusUtils.printLn("[getParametro]" + parametro);
        if (recargar) {
            cargarParametros();
        }
        if (parametros.containsKey(parametro)) {
            return parametros.get(parametro);
        }
        return "";
    }

    public static int getParametroInt(String parametro, boolean recargar) {
        NovusUtils.printLn("[getParametroInt]" + parametro);
        if (recargar) {
            cargarParametros();
        }
        if (parametros.containsKey(parametro)) {
            try {
                return Integer.parseInt(parametros.get(parametro));
            } catch (NumberFormatException e) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return -1;
    }

    public void loadFonts() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] array = new String[] {
                "/fonts/digital-mono.ttf",
                "/fonts/conthrax-sb.ttf",
                "/fonts/TerpelSans-Bold.otf",
                "/fonts/TerpelSans-ExtraBold.otf",
                "/fonts/bebas.ttf"
        };
        for (String url : array) {
            Font font = null;
            try {
                InputStream is3 = this.getClass().getResourceAsStream(url);
                font = Font.createFont(Font.TRUETYPE_FONT, is3);
                ge.registerFont(font);
                NovusUtils.printLn("FUENTE: [" + font.getFontName(Locale.US) + "] DISPONIBLE");
            } catch (FontFormatException | IOException e) {
                if (font == null) {
                    NovusUtils.printLn("PROBLEMAS A OBTENER LA FUENTE: " + url + " " + e.getMessage());
                } else {
                    NovusUtils.printLn("PROBLEMAS A OBTENER LA FUENTE: " + font.getName() + " " + e.getMessage());
                }
            }
        }
    }

    public static void cargarInformacionError() {

        NotificacionHistorialerror.consultarHistoricoReciente
                .scheduleWithFixedDelay(() -> NotificacionHistorialerror.notificar(), 10, 60, TimeUnit.SECONDS);
    }

    public static Date getLastFecha() {
        MovimientosDao mdao = new MovimientosDao();
        Date fechaBD = mdao.getLastDateMovimiento();
        Calendar calendar = Calendar.getInstance();
        Date fecha = calendar.getTime();

        Date maxima;
        if (fechaBD != null && fechaBD.after(fecha)) {
            calendar.setTime(fechaBD);
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 1);
            maxima = calendar.getTime();
        } else {
            maxima = fecha;
        }
        return maxima;
    }

    private static void pgNotify() {
        ListenToNotifyMessage th = new ListenToNotifyMessage();
        th.start();
    }
}
