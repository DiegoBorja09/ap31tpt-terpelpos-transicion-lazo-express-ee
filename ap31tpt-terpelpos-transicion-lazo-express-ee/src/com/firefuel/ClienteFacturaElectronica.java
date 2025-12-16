package com.firefuel;
import com.WT2.FacturacionElectronica.domain.entities.InformacionVentaClienteFE;
import com.WT2.FacturacionElectronica.domain.entities.MediosPagoVenta;
import com.WT2.appTerpel.domain.valueObject.MediosPagosDescription;
import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.entity.TransaccionProceso;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.WT2.loyalty.domain.entities.beans.FoundClient;
import com.WT2.loyalty.domain.entities.beans.IdentificationClient;
import com.WT2.loyalty.domain.entities.response.RespuestaRedencionBono;
import com.WT2.loyalty.presentation.handler.RedencionBonoHandler;
import com.application.useCases.movimientos.BuscarTransaccionDatafonoCaseUse;
import com.application.useCases.persons.RegistrarClienteUseCase;
import com.application.useCases.sutidores.CrearPosVentaFEUseCase;
import com.bean.AsignacionClienteBean;
import com.bean.ConsecutivoBean;
import com.bean.CrearPosVentaFEParams;
import com.bean.MediosPagosBean;
import com.bean.MovimientosBean;
import com.bean.Notificador;
import com.bean.PersonaBean;
import com.bean.ReciboExtended;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.FacturacionElectronicaDao;
import com.dao.MovimientosDao;
import com.dao.SurtidorDao;
import com.facade.FidelizacionFacade;
import com.firefuel.datafonos.ActualizarMediosPagos;
import com.firefuel.datafonos.EnviarPagosDatafonos;
import com.firefuel.facturacion.electronica.ConsultaClienteEnviarFE;
import com.firefuel.facturacion.electronica.ConvertidorFE;
import com.firefuel.facturacion.electronica.FacturaElectronicaVentaEnVivo;
import com.firefuel.facturacion.electronica.FacturacionElectronica;
import com.firefuel.fidelizacionYfacturaElectronica.RenderizarProcesosFidelizacionyFE;
import com.firefuel.fidelizacionYfacturaElectronica.VentasCurso;
import com.firefuel.utils.ImageCache;
import com.firefuel.utils.ShowMessageSingleton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.neo.app.bean.Manguera;
import com.neo.app.bean.Recibo;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.Timer;

import com.services.TimeOutsManager;
import teclado.view.common.TecladoExtendido;
import com.application.useCases.wacherparametros.FindByParameterUseCase;
import com.application.commons.CtWacherParametrosEnum;
import com.application.useCases.movimientocliente.ObtenerIdTransmisionDesdeMovimientoUseCase;

public class ClienteFacturaElectronica extends javax.swing.JDialog {
    ObtenerIdTransmisionDesdeMovimientoUseCase obtenerIdTransmisionDesdeMovimientoUseCase;
    CrearPosVentaFEUseCase crearPosVentaFEUseCase;
    FindByParameterUseCase findByParameterUseCase = new FindByParameterUseCase(CtWacherParametrosEnum.CODIGO.getColumnName(), "REMISION");
    public boolean isProcesoRechazada = false;
    public TransaccionProceso transaccionProceso = null;
    PersonaBean cliente = null;
    InfoViewController principal;
    VentaCursoPlaca ventaVivo;
    JFrame PrincipalStore;
    JsonObject clientJson = null;
    Manguera manguera;
    long identificadorTipoDocumento = 0;
    int identifierTypeId = 0;
    JToggleButton lastToggleSelected = null;
    String numeroDocumento = "";
    boolean entryIsCardReader = false;
    private String identifierCodeTemp;
    boolean IsVentaEnVivo = false;
    boolean IsPanelVenta = false;
    ReciboExtended recibo;
    MovimientosBean movimientoFac = new MovimientosBean();
    public static final int DOCUMENTO_CEDULA = 13;
    public static final int DOCUMENTO_CLIENTES_VARIOS = 31;
    public static final int DOCUMENTO_TARJ_EXTRANJERIA = 21;
    public static final int DOCUMENTO_CEDULA_EXTRANJERIA = 22;
    public static final int DOCUMENTO_NIT = 31;
    public static final int DOCUMENTO_PASAPORTE = 41;
    public static final int DOCUMENTO_IDENTIFICACION_EXTRANJERO = 42;
    public static final int DOCUMENTO_NIT_DE_OTRO_PAIS = 50;
    public static final int DOCUMENTO_NUIP = 91;
    public static final int DOCUMENTO_REGISTRO_CIVIL = 11;
    public static final int DOCUMENTO_TARJETA_IDENTIFICACION = 12;
    public JsonObject RESPUESTA_FACTURA;
    public static boolean MANUAL = false;
    public JsonObject RESPUESTA_FACTURA_VENTA_EN_VIVO;
    public TreeMap<Integer, String> tiposDocumentos = new TreeMap<>();
    public JsonObject respuesta;
    public JsonObject responseClient;
    public JsonObject sendFac;
    public static int CONSECUTIVO_ACTUAL = 0;
    public static int ID_CONSECUTIVO = 0;
    public JFrame parent;
    public JsonObject clienteFE;
    public static boolean CANASTILLA;
    public boolean placa = false;
    public JsonObject objPlaca;
    public JsonObject objManguera;
    boolean convertir;
    JsonObject notaP;
    public long numeroVenta;
    boolean ventaFeVivosinFacturar;
    boolean consumopropio;
    private Runnable runnable;
    private Runnable cerrar;
    public Runnable cerrarTodo;
    public Runnable devolverDatafonos;
    private Runnable confirmar;
    public static Runnable regresar;
    private Runnable confirmarCliente;
    private Runnable noConfirmarCliente;
    public static Notificador notificadorView = null;
    boolean habilitar = true;
    Runnable volver;
    Runnable noApcetar;
    VentasFE ventas;
    Timer timer = null;
    String identificacion;
    boolean consulta;
    PersonaBean persona = null;
    String reNumerico = "[0-9]*";
    String reAlfa = "[0-9a-zA-Z]*";
    String caracteresAceptados = reNumerico;
    boolean nuevo;
    MovimientosBean movimientosBean;
    long idmovimiento;
    boolean ventanaVenta = false;
    public Runnable regresarHIstotial;
    VentasHistorialView ventasHistorial;
    Runnable cerrarConsumo = null;

    Runnable anterior = null;
    boolean asignarDatos = false;
    boolean consultarCliente = false;
    boolean consultarDatos = false;
    String consumidorFinal = "222222222222";
    boolean datafono = Boolean.FALSE;
    public boolean isAppTerpelPendiente = false;

    private boolean pagoMixto = false;
    String plaquetaDatafono;
    String adquiriente;
    String codigoTerminal;
    int idAquiriente;
    String tipoVenta = "";
    private boolean hayDatafono;
    boolean datosCliente;
    ArrayList<MediosPagosBean> mediosPagoVenta = new ArrayList<>();
    ArrayList<MediosPagosBean> pagosDatafono = new ArrayList<>();
    JsonObject infoActualizarCliente = new JsonObject();
    private Runnable runnableDatafono = null;
    private IdentificationClient datosClienteFidelizaion;
    private String codigoIndentificacionEstacion;
    private FoundClient foundClient;
    String caracteresPermitidos = reNumerico;
    int cantidadCaracteres = 10;
    Color activo = new Color(228, 30, 19);
    Color inactivo = new Color(229, 230, 232);

    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static ScheduledFuture<?> tareaProgramada;
    FidelizacionyFacturacionElectronica fidelizacionyFacturacoinElectronica;

    RespuestaRedencionBono respuestaRedencionBono;
    private boolean ventaMarket = false;

    ConsultaClienteEnviarFE consultaClienteEnviarFE;
    RenderizarProcesosFidelizacionyFE procesosFidelizacionyFE;
    private static Icon loaderDatafono, errorDatafono, pagoDatafono,
    btn_atras, 
    btDangerNormal,
    btDangerSmallb, 
    fnd_cambio_ventamanual,
    fnd_cambio_ventamanual_small,
    btn_blanco_2,
    fndRumbo,
    separadorVertical, logoDevitech, enviandoPago, fndMsjDatafonos;
    ConvertidorFE convertidor;
    RedencionBonoHandler redencionBonoHandler;
    AsignacionClienteBean asignacionClienteBean;
    FacturaElectronicaVentaEnVivo facturaElectronicaVentaEnVivo;
    FacturacionElectronicaDao electronicaDao;
    SurtidorDao surtidorDao;
    TimeOutsManager timeOutsManager;
    MediosPagosBean mediosPagosBean;
    FacturacionElectronica facturacionElectronica;
    EnviarPagosDatafonos enviarPagosDatafonos;
    ActualizarMediosPagos actualizarMediosPagos;
    ExecutorService executorService;
    MovimientosDao mdao;
    RegistrarClienteUseCase useCase = new RegistrarClienteUseCase();
    MovimientosBean movimiento;


    public  void loadIconsImages(){
        loaderDatafono = ImageCache.getImage("/com/firefuel/resources/enviando pago.gif");
        errorDatafono = ImageCache.getImage("/com/firefuel/resources/error enviar pago.png");
        pagoDatafono = ImageCache.getImage("/com/firefuel/resources/transaccionRecibida.gif");
        btn_atras = ImageCache.getImage("/com/firefuel/resources/btn_atras.png");
        btDangerNormal = ImageCache.getImage("/com/firefuel/resources/botones/bt-danger-normal.png");
        btDangerSmallb = ImageCache.getImage("/com/firefuel/resources/botones/bt-danger-smallb.png");
        fnd_cambio_ventamanual = ImageCache.getImage("/com/firefuel/resources/fnd_cambio_ventamanual.png");
        fnd_cambio_ventamanual_small = ImageCache.getImage("/com/firefuel/resources/fnd_cambio_ventamanual_small.png");
        fndRumbo = ImageCache.getImage("/com/firefuel/resources/fndRumbo.png");
        separadorVertical = ImageCache.getImage("/com/firefuel/resources/separadorVertical.png");
        logoDevitech = ImageCache.getImage("/com/firefuel/resources/logoDevitech_3.png");
        enviandoPago = ImageCache.getImage("/com/firefuel/resources/enviando pago.gif");
        fndMsjDatafonos = ImageCache.getImage("/com/firefuel/resources/fndMsjDatafonos.png");
        btn_blanco_2 = ImageCache.getImage("/com/firefuel/resources/botones/boton-blanco-2.png");
    }

    /**
     * Creates new form ClienteFacturaElectronica
     *
     * @param parent
     * @param ventavivo
     * @param modal
     */
    public ClienteFacturaElectronica(InfoViewController parent, VentaCursoPlaca ventavivo, boolean modal) {
        super(parent, modal);
        this.ventaVivo = ventavivo;
        principal = parent;
        this.persona = Main.persona;
        initComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.init();
    }

    public ClienteFacturaElectronica(InfoViewController parent, VentaCursoPlaca ventavivo, boolean modal, boolean placa, JsonObject objPlaca, JsonObject manguera) {
        super(parent, modal);
        this.ventaVivo = ventavivo;
        principal = parent;
        this.placa = placa;
        this.persona = Main.persona;
        this.objPlaca = objPlaca;
        this.objManguera = manguera;
        initComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.init();
    }

    public ClienteFacturaElectronica(JFrame parent, InfoViewController info, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        this.persona = Main.persona;
        this.principal = info;
        initComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.init();
    }

    public ClienteFacturaElectronica(JFrame parent,
            InfoViewController info,
            boolean modal, boolean consumopropio,
            MovimientosBean movimientosBean, boolean ventanVenta,
            boolean ventaMarket) {
        super(parent, modal);
        this.parent = parent;
        this.principal = info;
        this.persona = Main.persona;
        this.consumopropio = consumopropio;
        this.movimientosBean = movimientosBean;
        this.ventanaVenta = ventanVenta;
        this.ventaMarket = ventaMarket;
        initComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.init();
    }

    public ClienteFacturaElectronica(JFrame parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        this.persona = Main.persona;
        initComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.init();
    }

    public ClienteFacturaElectronica(JFrame parent, InfoViewController info, boolean modal, PersonaBean personas) {
        super(parent, modal);
        this.parent = parent;
        this.principal = info;
        this.persona = personas;
        initComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.init();
    }

    public ClienteFacturaElectronica(JFrame parent, boolean modal, Runnable runnable) {
        super(parent, modal);
        this.parent = parent;
        this.persona = Main.persona;
        this.runnable = runnable;
        initComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.init();
    }

    public ClienteFacturaElectronica(JFrame parent, boolean modal, Runnable runnable, Runnable cerrar) {
        super(parent, modal);
        this.parent = parent;
        this.runnable = runnable;
        this.volver = cerrar;
        this.persona = Main.persona;
        initComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.init();
    }

    public ClienteFacturaElectronica(InfoViewController parent, boolean modal, long numeroVenta, boolean ventaFeVivosinFacturar, VentasFE venta) {
        super(parent, modal);
        this.parent = parent;
        this.numeroVenta = numeroVenta;
        
        // 🔍 DEBUG: Verificar inicialización de numeroVenta
        System.out.println("🔍 DEBUG Constructor ClienteFacturaElectronica (VentasFE):");
        System.out.println("    - numeroVenta parámetro: " + numeroVenta);
        System.out.println("    - this.numeroVenta asignado: " + this.numeroVenta);
        
        this.ventaFeVivosinFacturar = ventaFeVivosinFacturar;
        this.ventas = venta;
        this.persona = Main.persona;
        initComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.init();
    }

    public ClienteFacturaElectronica(InfoViewController parent, boolean modal, long numeroVenta, boolean ventaFeVivosinFacturar, VentasHistorialView ventaHistorial, Runnable cerrarConsumo, Boolean consultarDatos) {
        super(parent, modal);
        this.parent = parent;
        this.numeroVenta = numeroVenta;
        
        // 🔍 DEBUGs: Verificar inicialización de numeroVenta
        System.out.println("🔍 DEBUG Constructor ClienteFacturaElectronica (VentasHistorialView):");
        System.out.println("    - numeroVenta parámetro: " + numeroVenta);
        System.out.println("    - this.numeroVenta asignado: " + this.numeroVenta);
        
        this.ventaFeVivosinFacturar = ventaFeVivosinFacturar;
        this.ventasHistorial = ventaHistorial;
        this.persona = Main.persona;
        this.cerrarConsumo = cerrarConsumo;
        this.consultarDatos = consultarDatos;
        initComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.init();

        asignarDatos();
    }

    public ClienteFacturaElectronica(InfoViewController principal, boolean modal, long numeroVenta, boolean ventaFeVivosinFacturar, Runnable anterior, Runnable cerrartodo, boolean asignarDatos, boolean consultarCliente) {
        super(principal, modal);
        this.principal = principal;
        this.numeroVenta = numeroVenta;
        this.ventaFeVivosinFacturar = ventaFeVivosinFacturar;
        this.anterior = anterior;
        this.asignarDatos = asignarDatos;
        this.consultarCliente = consultarCliente;
        this.cerrarTodo = cerrartodo;
        initComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.init();
        asignarCliente();
    }

    public ClienteFacturaElectronica(InfoViewController principal, boolean modal, long numeroVenta, boolean ventaFeVivosinFacturar,
            Runnable anterior, Runnable cerrartodo, boolean asignarDatos, boolean consultarCliente,
            ArrayList<MediosPagosBean> mediosPagoVenta, MovimientosBean movimientosBean) {
        super(principal, modal);
        this.principal = principal;
        this.numeroVenta = numeroVenta;
        this.ventaFeVivosinFacturar = ventaFeVivosinFacturar;
        this.anterior = anterior;
        this.asignarDatos = asignarDatos;
        this.consultarCliente = consultarCliente;
        this.cerrarTodo = cerrartodo;
        this.mediosPagoVenta = mediosPagoVenta;
        this.movimiento = movimientosBean;
        initComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.init();
        asignarCliente();
    }

    public ClienteFacturaElectronica(){
    }

    public ClienteFacturaElectronica(InfoViewController principal, boolean modal,
            long numeroVenta, boolean ventaFeVivosinFacturar, Runnable anterior,
            Runnable cerrartodo, boolean asignarDatos, boolean datosCLiente, boolean consultarCliente,
            MovimientosBean movimiento, ArrayList<MediosPagosBean> mediosPagoVenta) {
        super(principal, modal);
        this.principal = principal;
        this.numeroVenta = numeroVenta;
        
        // 🔍 DEBUG: Verificar inicialización de numeroVenta
        System.out.println("🔍 DEBUG Constructor ClienteFacturaElectronica (MovimientosBean - APPTERPEL):");
        System.out.println("    - numeroVenta parámetro: " + numeroVenta);
        System.out.println("    - this.numeroVenta asignado: " + this.numeroVenta);
        System.out.println("    - movimiento.getId(): " + (movimiento != null ? movimiento.getId() : "null"));
        
        this.ventaFeVivosinFacturar = ventaFeVivosinFacturar;
        this.anterior = anterior;
        this.asignarDatos = asignarDatos;
        this.consultarCliente = consultarCliente;
        this.cerrarTodo = cerrartodo; // ESTE ES EL CONSTRUCTOR QUE SE USA CON APPTERPEL
        this.movimiento = movimiento;
        this.datosCliente = datosCLiente;
        this.mediosPagoVenta = mediosPagoVenta;
        initComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.init();
        asignarCliente();
    }

    public ClienteFacturaElectronica(InfoViewController parent, boolean modal, boolean IsVentaEnVivo) {
        principal = parent;
        initComponents();
        this.IsVentaEnVivo = IsVentaEnVivo;
        this.persona = Main.persona;
        this.init();
    }

    public ClienteFacturaElectronica(InfoViewController parent, boolean modal, boolean IsVentaEnVivo, boolean hablitarBoton) {
        principal = parent;
        initComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.IsVentaEnVivo = IsVentaEnVivo;
        this.habilitar = hablitarBoton;
        this.persona = Main.persona;
        this.init();
    }

    public ClienteFacturaElectronica(InfoViewController parent, boolean modal, boolean IsVentaEnVivo, boolean hablitarBoton, boolean consulta) {
        principal = parent;
        initComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.IsVentaEnVivo = IsVentaEnVivo;
        this.habilitar = hablitarBoton;
        this.persona = Main.persona;
        this.consulta = consulta;
        this.init();
    }

    public void setDatosClientesFidelizacion(IdentificationClient fidelizacion) {
        this.datosClienteFidelizaion = fidelizacion;
    }

    public void setIdentificacionPuntoDeVenta(String identificaionPuntoVenta) {
        this.codigoIndentificacionEstacion = identificaionPuntoVenta;
    }

    void init() {
        this.obtenerIdTransmisionDesdeMovimientoUseCase = new ObtenerIdTransmisionDesdeMovimientoUseCase(0L);
        jLabel17.setVisible(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        jTextField1.requestFocus();
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);
        jLabel7.setVisible(this.habilitar);
        jLabel9.setVisible(habilitar);
        jTextField1.requestFocus();
        identificadorTipoDocumento = DOCUMENTO_CEDULA;
        NovusUtils.llenarComboBox(JComboTipoDocumentos);
        if (consultarCliente || consultarDatos) {
            identificadorTipoDocumento = DOCUMENTO_NIT;
        } else {
            identificadorTipoDocumento = DOCUMENTO_CEDULA;
        }
        notificadorView = this::recibir;

        try {
            mdao.getConsecutivo(ConsecutivoBean.TIPO_ELECTRONICA);
            //mdao.getTipoClienteREgistrado();
            Long empresasId = Main.credencial.getEmpresas_id();
            useCase.execute(empresasId);
            jLabel12.setText("FACTURA ELECTRONICA");
        } catch (DAOException ex) {
            Logger.getLogger(ClienteFacturaElectronica.class.getName()).log(Level.SEVERE, null, ex);
        }
        JComboTipoDocumentos.setBackground(new java.awt.Color(255, 255, 255));
    }

    public void sethayDatafonos(boolean isDatafono) {
        this.datafono = hayDatafono;
    }

    public void asignarDatos() {
        jLabel12.setText("ASIGNAR CLIENTE");
        jLabel7.setText("GUARDAR");
        jTextField1.setText(consumidorFinal);
        identificadorTipoDocumento = DOCUMENTO_NIT;
    }

    public void asignarCliente() {
        consultaAsignarCliente();
        jLabel12.setText("ASIGNAR CLIENTE");
        jLabel7.setText("SIGUIENTE");
    }

    void recibir(JsonObject data) {
        if (data.get("loader").getAsBoolean()) {
            loader(data);
        } else {
            mensajes(data);
        }
    }

    void selectIdentifierType(int identifierTypeIdSelected, JToggleButton toggle) {
        if (this.lastToggleSelected == null || toggle != this.lastToggleSelected) {
            if (toggle != null) {
                this.toggleActivationToggleButtonIdentifiersType(toggle, true);
            }
            if (this.lastToggleSelected != null) {
                this.toggleActivationToggleButtonIdentifiersType(this.lastToggleSelected, false);
            }
            this.lastToggleSelected = toggle;
            this.setIdentifierTypeId(identifierTypeIdSelected);
            this.jTextField1.requestFocus();
        } else {
            this.toggleActivationToggleButtonIdentifiersType(this.lastToggleSelected, false);
            this.lastToggleSelected = null;
            this.setIdentifierTypeId(0);
        }
    }

    boolean isIdentifierEntried() {
        String identifierTxt = this.jTextField1.getText().trim();
        if (this.entryIsCardReader) {
            return this.identifierCodeTemp != null && !this.identifierCodeTemp.trim().equals("");
        } else {
            return !identifierTxt.equals("");
        }
    }

    boolean isIdentifierTypeSelected() {
        return this.identifierTypeId > 0;
    }

    void cambiarDato() {
        if (this.movimientoFac != null) {
            jLabel7.setText("FACTURAR");
        }
    }

    void setMovimiento(Recibo mov) {
        this.movimientoFac.setId(mov.getNumero());
    }

    public void setIdentifierTypeId(int identifierTypeId) {
        this.identifierTypeId = identifierTypeId;
    }

    void toggleActivationToggleButtonIdentifiersType(JToggleButton toggle, boolean active) {
        if (active) {

            toggle.setBackground(new Color(153, 0, 0));
            toggle.setForeground(Color.WHITE);
        } else {
            toggle.setBackground(Color.WHITE);
            toggle.setForeground(new Color(153, 0, 0));
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        //Reutilización de este metodo en todos los constructores
        //Se agregan instancias aca para evitar duplicidad
        crearPosVentaFEUseCase = new CrearPosVentaFEUseCase();
        loadIconsImages();
        this.mdao = new MovimientosDao();
        
        // 🔧 FIX: Solo inicializar si no existe para no sobrescribir el objeto ya asignado
        if (this.movimiento == null) {
            this.movimiento = new MovimientosBean();
        }
        
        this.procesosFidelizacionyFE = new RenderizarProcesosFidelizacionyFE();
        this.foundClient = new FoundClient();
        // this.movimiento = new MovimientosBean(); // 🔧 FIX: Removido para evitar sobrescribir el objeto ya asignado
        this.consultaClienteEnviarFE = new ConsultaClienteEnviarFE();
        this.convertidor = new ConvertidorFE();
        this.redencionBonoHandler = new RedencionBonoHandler();
        this.asignacionClienteBean = new AsignacionClienteBean();
        this.facturaElectronicaVentaEnVivo = new FacturaElectronicaVentaEnVivo();
        this.electronicaDao = new FacturacionElectronicaDao();
        this.surtidorDao = new SurtidorDao();
        this.timeOutsManager = new TimeOutsManager();
        this.mediosPagosBean = new MediosPagosBean();
        this.facturacionElectronica = new FacturacionElectronica();
        this.enviarPagosDatafonos = new EnviarPagosDatafonos();
        this.actualizarMediosPagos = new ActualizarMediosPagos();

        
        tipoDocumento = new javax.swing.ButtonGroup();
        pnlPrincipal = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblConsultar = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jPanel1 = new TecladoExtendido();
        jTextField1 = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jMensajesIdentificacion = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        JComboTipoDocumentos = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        notificaniones = new javax.swing.JPanel();
        jMensaje = new javax.swing.JLabel();
        botones_confirmacion = new javax.swing.JPanel();
        boton_cerrar = new javax.swing.JPanel();
        jCerrar = new javax.swing.JLabel();
        confirmacion = new javax.swing.JPanel();
        btn_aceptar = new javax.swing.JLabel();
        btn_denegar1 = new javax.swing.JLabel();
        jIcono = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        msjDatafonos = new javax.swing.JPanel();
        jCerrar1 = new javax.swing.JLabel();
        jTitulo = new javax.swing.JLabel();
        jInfoDatafono = new javax.swing.JLabel();
        jTransacciones = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jIcono1 = new javax.swing.JLabel();
        fndFondo = new javax.swing.JLabel();

        setUndecorated(true);
        getContentPane().setLayout(null);

        pnlPrincipal.setLayout(new java.awt.CardLayout());

        jPanel5.setLayout(null);

        jLabel14.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(186, 12, 47));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("IDENTIFICACIÓN:");
        jPanel5.add(jLabel14);
        jLabel14.setBounds(80, 100, 540, 20);

        jLabel8.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("CLIENTE");
        jPanel5.add(jLabel8);
        jLabel8.setBounds(80, 260, 530, 50);
       

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setIcon(btn_atras); // NOI18N
        jLabel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel13MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel13MouseReleased(evt);
            }
        });
        jPanel5.add(jLabel13);
        jLabel13.setBounds(10, 10, 70, 71);

        jLabel12.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("FACTURACION ELECTRONICA");
        jPanel5.add(jLabel12);
        jLabel12.setBounds(110, 0, 1080, 80);

        lblConsultar.setFont(new java.awt.Font("Terpel Sans", 0, 24)); // NOI18N
        lblConsultar.setForeground(new java.awt.Color(255, 255, 255));
        lblConsultar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblConsultar.setIcon(btDangerNormal); // NOI18N
        lblConsultar.setText("CONSULTAR");
        lblConsultar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblConsultar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblConsultarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblConsultarMouseReleased(evt);
            }
        });
        jPanel5.add(lblConsultar);
        lblConsultar.setBounds(670, 250, 300, 70);

        jLabel17.setFont(new java.awt.Font("Terpel Sans", 0, 24)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setIcon(btDangerSmallb); // NOI18N
        jLabel17.setText("SIGUIENTE");
        jLabel17.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel17MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel17MouseReleased(evt);
            }
        });
        jPanel5.add(jLabel17);
        jLabel17.setBounds(1040, 250, 180, 70);
        jPanel5.add(jPanel1);
        jPanel1.setBounds(110, 340, 1060, 360);

        jTextField1.setFont(new java.awt.Font("Roboto", 1, 36)); // NOI18N
        jTextField1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField1.setBorder(null);
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField1KeyTyped(evt);
            }
        });
        jPanel5.add(jTextField1);
        jTextField1.setBounds(80, 140, 520, 50);

        jLabel15.setIcon(fnd_cambio_ventamanual); // NOI18N
        jPanel5.add(jLabel15);
        jLabel15.setBounds(70, 130, 550, 70);

        jLabel16.setIcon(fnd_cambio_ventamanual); // NOI18N
        jPanel5.add(jLabel16);
        jLabel16.setBounds(70, 250, 550, 70);

        jLabel1.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(186, 12, 47));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("NOMBRE DEL CLIENTE");
        jPanel5.add(jLabel1);
        jLabel1.setBounds(100, 230, 490, 24);

        jLabel6.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(186, 12, 47));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setText("TIPO DE IDENTIFICACIÓN:");
        jPanel5.add(jLabel6);
        jLabel6.setBounds(690, 100, 450, 20);

        jLabel9.setFont(new java.awt.Font("Terpel Sans", 0, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(153, 0, 0));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setIcon(btn_blanco_2); // NOI18N
        jLabel9.setText("CANCELAR");
        jLabel9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel9MouseReleased(evt);
            }
        });
        jPanel5.add(jLabel9);
        jLabel9.setBounds(360, 720, 280, 70);

        jLabel7.setFont(new java.awt.Font("Terpel Sans", 0, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(153, 0, 0));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setIcon(btn_blanco_2); // NOI18N
        jLabel7.setText("SIGUIENTE");
        jLabel7.setEnabled(false);
        jLabel7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel7MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel7MouseReleased(evt);
            }
        });
        jPanel5.add(jLabel7);
        jLabel7.setBounds(700, 720, 280, 70);

        jMensajesIdentificacion.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jMensajesIdentificacion.setForeground(new java.awt.Color(204, 0, 0));
        jMensajesIdentificacion.setToolTipText("");
        jPanel5.add(jMensajesIdentificacion);
        jMensajesIdentificacion.setBounds(90, 200, 520, 30);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(null);

        JComboTipoDocumentos.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        JComboTipoDocumentos.setForeground(new java.awt.Color(51, 51, 51));
        JComboTipoDocumentos.setBorder(new com.firefuel.components.panelesPersonalizados.BordesRedondos());
        JComboTipoDocumentos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JComboTipoDocumentosActionPerformed(evt);
            }
        });
        jPanel2.add(JComboTipoDocumentos);
        JComboTipoDocumentos.setBounds(10, 12, 530, 60);

        jPanel5.add(jPanel2);
        jPanel2.setBounds(680, 120, 570, 80);

        jLabel2.setIcon(fndRumbo); // NOI18N
        jPanel5.add(jLabel2);
        jLabel2.setBounds(0, 0, 1280, 800);

        jLabel4.setFont(new java.awt.Font("Juicebox", 0, 24)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Identificacion");
        jPanel5.add(jLabel4);
        jLabel4.setBounds(60, 220, 450, 30);

        pnlPrincipal.add(jPanel5, "pnl_principal");

        notificaniones.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jMensaje.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jMensaje.setForeground(new java.awt.Color(186, 12, 47));
        jMensaje.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jMensaje.setText("OK");
        notificaniones.add(jMensaje, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 190, 890, 300));

        botones_confirmacion.setBackground(new java.awt.Color(255, 255, 255));
        botones_confirmacion.setLayout(new java.awt.CardLayout());

        boton_cerrar.setBackground(new java.awt.Color(255, 255, 255));
        boton_cerrar.setLayout(null);

        jCerrar.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jCerrar.setForeground(new java.awt.Color(255, 255, 255));
        jCerrar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jCerrar.setIcon(btDangerNormal); // NOI18N
        jCerrar.setText("CERRAR");
        jCerrar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jCerrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jCerrarMouseClicked(evt);
            }
        });
        boton_cerrar.add(jCerrar);
        jCerrar.setBounds(310, 40, 264, 54);

        botones_confirmacion.add(boton_cerrar, "boton_cerrar");

        confirmacion.setBackground(new java.awt.Color(255, 255, 255));
        confirmacion.setLayout(null);

        btn_aceptar.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btn_aceptar.setForeground(new java.awt.Color(255, 255, 255));
        btn_aceptar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btn_aceptar.setIcon(btDangerNormal); // NOI18N
        btn_aceptar.setText("SI");
        btn_aceptar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn_aceptar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_aceptarMouseClicked(evt);
            }
        });
        confirmacion.add(btn_aceptar);
        btn_aceptar.setBounds(460, 40, 264, 54);

        btn_denegar1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btn_denegar1.setForeground(new java.awt.Color(255, 255, 255));
        btn_denegar1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btn_denegar1.setIcon(btDangerNormal); // NOI18N
        btn_denegar1.setText("NO");
        btn_denegar1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn_denegar1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_denegar1MouseClicked(evt);
            }
        });
        confirmacion.add(btn_denegar1);
        btn_denegar1.setBounds(100, 40, 264, 54);

        botones_confirmacion.add(confirmacion, "confirmacion");

        notificaniones.add(botones_confirmacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 490, 840, 140));

        jIcono.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        notificaniones.add(jIcono, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 190, 320, 300));

        jLabel11.setIcon(fndRumbo); // NOI18N
        notificaniones.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1280, 800));

        pnlPrincipal.add(notificaniones, "notificacion");

        msjDatafonos.setLayout(null);

        jCerrar1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jCerrar1.setForeground(new java.awt.Color(153, 0, 0));
        jCerrar1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jCerrar1.setIcon(btn_blanco_2); // NOI18N
        jCerrar1.setText("CERRAR");
        jCerrar1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jCerrar1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jCerrar1MouseClicked(evt);
            }
        });
        msjDatafonos.add(jCerrar1);
        jCerrar1.setBounds(990, 18, 249, 53);

        jTitulo.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jTitulo.setForeground(new java.awt.Color(186, 12, 47));
        jTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jTitulo.setText("TITULO MENSAJE");
        jTitulo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        msjDatafonos.add(jTitulo);
        jTitulo.setBounds(150, 448, 980, 80);

        jInfoDatafono.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jInfoDatafono.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jInfoDatafono.setText("INFORMACION DATAFONO");
        jInfoDatafono.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        msjDatafonos.add(jInfoDatafono);
        jInfoDatafono.setBounds(280, 530, 740, 28);

        jTransacciones.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jTransacciones.setForeground(new java.awt.Color(186, 12, 47));
        jTransacciones.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jTransacciones.setText("TRANSACCION");
        jTransacciones.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        msjDatafonos.add(jTransacciones);
        jTransacciones.setBounds(280, 570, 740, 28);

        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setIcon(separadorVertical); // NOI18N
        msjDatafonos.add(jLabel32);
        jLabel32.setBounds(80, 10, 10, 68);

        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setIcon(separadorVertical); // NOI18N
        msjDatafonos.add(jLabel33);
        jLabel33.setBounds(120, 710, 10, 80);

        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setIcon(separadorVertical); // NOI18N
        msjDatafonos.add(jLabel34);
        jLabel34.setBounds(1130, 710, 10, 80);

        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setIcon(logoDevitech); // NOI18N
        msjDatafonos.add(jLabel35);
        jLabel35.setBounds(10, 710, 100, 80);

        jIcono1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jIcono1.setIcon(enviandoPago); // NOI18N
        jIcono1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        msjDatafonos.add(jIcono1);
        jIcono1.setBounds(520, 190, 248, 248);

        fndFondo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fndFondo.setIcon(fndMsjDatafonos); // NOI18N
        fndFondo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fndFondo.setMaximumSize(new java.awt.Dimension(1280, 800));
        fndFondo.setMinimumSize(new java.awt.Dimension(1280, 800));
        fndFondo.setPreferredSize(new java.awt.Dimension(1280, 800));
        fndFondo.setRequestFocusEnabled(false);
        msjDatafonos.add(fndFondo);
        fndFondo.setBounds(0, 0, 1280, 800);

        pnlPrincipal.add(msjDatafonos, "mensajesDatafono");

        getContentPane().add(pnlPrincipal);
        pnlPrincipal.setBounds(0, 0, 1280, 800);
        pnlPrincipal.getAccessibleContext().setAccessibleName("");

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jCerrarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCerrarMouseClicked
        if (cerrar != null) {
            NovusConstante.ventanaFE = Boolean.FALSE;
            cerrar.run();
            terminarTareaProgramada();
        }
    }//GEN-LAST:event_jCerrarMouseClicked

    private void btn_aceptarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_aceptarMouseClicked
        NovusUtils.beep();
        if (confirmar != null) {
            confirmar.run();
            confirmar = null;
        } else if (confirmarCliente != null) {
            confirmarCliente.run();
            confirmarCliente = null;
        }
    }//GEN-LAST:event_btn_aceptarMouseClicked

    private void btn_denegar1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_denegar1MouseClicked
        if (noApcetar != null) {
            noApcetar.run();
        } else if (noConfirmarCliente != null) {
            NovusUtils.printLn("No se confirma Consumidor Final");
            noConfirmarCliente.run();
            noConfirmarCliente = null;
        } else {
            principal.cerrarSubmenu();
            cerrar();
        }
    }//GEN-LAST:event_btn_denegar1MouseClicked

    private void jCerrar1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCerrar1MouseClicked
        if (asignarDatos) {
            updateInfoCliente(this.numeroVenta);
        }
        if (cerrarTodo != null) {
            cerrarTodo.run();
        }
        if (devolverDatafonos != null) {
            devolverDatafonos.run();
        }
    }//GEN-LAST:event_jCerrar1MouseClicked

    private void JComboTipoDocumentosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JComboTipoDocumentosActionPerformed
        if (JComboTipoDocumentos.getSelectedItem() != null) {
            if (JComboTipoDocumentos.getSelectedItem().toString().equals("CONSUMIDOR FINAL")) {
                jTextField1.setText(consumidorFinal);
            } else {
                jTextField1.setText("");
            }
            jTextField1.requestFocus();
            TecladoExtendido teclado = (TecladoExtendido) jPanel1;
            identificadorTipoDocumento = NovusUtils.tipoDeIndentificacion(JComboTipoDocumentos.getSelectedItem().toString().toUpperCase());
            caracteresPermitidos = NovusUtils.obtenerRestriccionCaracteres(JComboTipoDocumentos.getSelectedItem().toString().toUpperCase());
            cantidadCaracteres = NovusUtils.obtenerLimiteCaracteres(JComboTipoDocumentos.getSelectedItem().toString().toUpperCase());
            teclado.habilitarAlfanumeric(NovusUtils.habilitarTecladoAlfanumerico(caracteresPermitidos));
            teclado.habilitarPunto(NovusUtils.habilitarPunto(caracteresPermitidos));
            teclado.habilitarDosPuntos(NovusUtils.habilitarDosPunto(caracteresPermitidos));
        }
    }//GEN-LAST:event_JComboTipoDocumentosActionPerformed

    private void jTextField1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyTyped
        NovusUtils.limitarCarateres(evt, jTextField1, cantidadCaracteres, jMensajesIdentificacion, caracteresPermitidos);
    }//GEN-LAST:event_jTextField1KeyTyped

    private void jLabel7MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel7MousePressed

    private void jLabel17MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel17MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel17MousePressed

    private void jLabel17MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel17MouseReleased
        funcionSiguiente();
    }//GEN-LAST:event_jLabel17MouseReleased

    private void lblConsultarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblConsultarMouseReleased
        NovusUtils.beep();
        validar(jTextField1.getText(), caracteresAceptados);
    }//GEN-LAST:event_lblConsultarMouseReleased

    private void lblConsultarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblConsultarMousePressed

    }//GEN-LAST:event_lblConsultarMousePressed

    public void cerrarVistaAsignarCliente() {
        if (asignarDatos) {
            updateInfoCliente(this.numeroVenta);
        }
        if (cerrarTodo != null) {
            cerrarTodo.run();
        }
        if (devolverDatafonos != null) {
            devolverDatafonos.run();
        }
    }

    private void validar(String cadena, String caracteres) {
        cadena = String.valueOf(cadena);
        if (!nuevo) {
            if (identificadorTipoDocumento == DOCUMENTO_CEDULA_EXTRANJERIA || identificadorTipoDocumento == DOCUMENTO_TARJ_EXTRANJERIA || identificadorTipoDocumento == DOCUMENTO_PASAPORTE || identificadorTipoDocumento == DOCUMENTO_IDENTIFICACION_EXTRANJERO) {
                consultarCliente();
            } else {
                if (cadena.matches(caracteres)) {
                    consultarCliente();
                } else {
                    showMessage("Datos Invalidos",
                            "/com/firefuel/resources/btBad.png", true, () -> {
                                mostrarPanel("pnl_principal");
                            }, true, LetterCase.FIRST_UPPER_CASE);
                }
            }
        } else {
            nuevo = false;
            lblConsultar.setText("CONSULTAR");
            jTextField1.setText("");
            jTextField1.requestFocus();
            jLabel8.setText("CLIENTE");
            activar(true);
        }
    }

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextField1ActionPerformed
        if (!ventaMarket) {
            validar(jTextField1.getText(), caracteresAceptados);
        }
    }

    FacturacionManualView factManual;

    private void jLabel7MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel7MouseReleased
        if (jLabel7.isEnabled()) {
            funcionSiguiente();
        }
    }// GEN-LAST:event_jLabel7MouseReleased

    private void funcionSiguiente() {
        NovusUtils.beep();
        if (asignarDatos || this.datosCliente) {
            if (jTextField1.getText().equals(consumidorFinal)) {
                confirmarCliente();
            } else {
                asignacionDatosCliente();
            }
        } else {
            if (parent instanceof FacturacionManualView) {
                factManual = (FacturacionManualView) parent;
                factManual.abrirMediosPago();
                factManual.isElectronica = true;
                NovusUtils.printLn("id del promotro en la vista de consulta cliente" + persona.getId());
                factManual.clienteFE = clienteFE;
                factManual.personas(persona);
                factManual.setVisible(true);
                cerrar();
                return;
            }
            if (this.ventaFeVivosinFacturar) {
                if (consultarDatos) {
                    if (jTextField1.getText().equals(consumidorFinal)) {
                        confirmarConsumoPropio();
                    } else {
                        mostrarPanel("notificacion");
                        confirmar();
                    }
                } else {
                    mostrarPanel("notificacion");
                    confirmar();
                }
            } else {
                enviarFacturacionYFidelizacion();
            }
        }
    }

    private void enviarFacturacionYFidelizacion() {
        VentasCurso ventasCurso = new VentasCurso();
        ReciboExtended recibo = new ReciboExtended();
        recibo.setSurtidor(1 + "");
        recibo.setManguera(1 + "");
        recibo.setCara(1 + "");
        Runnable regresar = () -> ventasCurso.cargarPanelExterno(pnlPrincipal, jPanel5);

        Runnable enviar = () -> {
            NovusUtils.showPanel(pnlPrincipal, "notificacion");
            pnlPrincipal.revalidate();
            pnlPrincipal.repaint();
            clienteFE = fidelizacionyFacturacoinElectronica.getDatosClienteFE();

            for (Component comp : pnlPrincipal.getComponents()) {
                System.out.println("Panel: " + comp.getClass().getSimpleName() + " | Name: " + comp.getName());
            }
            notificaniones.setName("notificacion"); // Asegura que el JPanel tiene el nombre correcto
            pnlPrincipal.add(notificaniones, "notificacion");
            NovusUtils.printLn("::::::::::::::::::");
            NovusUtils.printLn("FidelizaCliente " + fidelizacionyFacturacoinElectronica.getDatosClienteFidelizacion());
            NovusUtils.printLn("::::::::::::::::::");
            foundClient = validarInfoFidelizacion(fidelizacionyFacturacoinElectronica.getDatosClienteFidelizacion());
            datosClienteFidelizaion = foundClient.getDatosCliente() != null ? foundClient.getDatosCliente().getCustomer() : null;
            enviarFacturaElectronica(this.placa, this.objPlaca != null ? objPlaca : new JsonObject(), this.objManguera != null ? objManguera : new JsonObject());
        };

        fidelizacionyFacturacoinElectronica
                = new FidelizacionyFacturacionElectronica(recibo, jTextField1.getText(),
                        JComboTipoDocumentos.getSelectedItem().toString(), regresar, false, enviar, true, this.RESPUESTA_FACTURA);

        pnlPrincipal.add("pnlFidelizacion", fidelizacionyFacturacoinElectronica);
        NovusUtils.showPanel(pnlPrincipal, "pnlFidelizacion");
    }

    private FoundClient validarInfoFidelizacion(JsonObject infoFidelizacion) {
        if (infoFidelizacion != null) {
            foundClient = Main.gson.fromJson(infoFidelizacion, FoundClient.class);
        }
        return foundClient;
    }

    private void jLabel9MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel9MouseReleased
        NovusUtils.beep();
        cerrar();
    }// GEN-LAST:event_jLabel9MouseReleased

    private void jToggleButton6ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jToggleButton6ActionPerformed
        NovusUtils.beep();
        jTextField1.requestFocus();
        caracteresAceptados = reNumerico;
        seleccionEsteDocumento(DOCUMENTO_NIT, evt);
    }// GEN-LAST:event_jToggleButton6ActionPerformed

    private void jToggleButton5ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jToggleButton5ActionPerformed
        NovusUtils.beep();
        jTextField1.requestFocus();
        caracteresAceptados = reNumerico;
        seleccionEsteDocumento(DOCUMENTO_CEDULA, evt);

    }// GEN-LAST:event_jToggleButton5ActionPerformed

    private void jToggleButton4ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jToggleButton4ActionPerformed
        NovusUtils.beep();
        jTextField1.requestFocus();
        caracteresAceptados = reNumerico;
        seleccionEsteDocumento(DOCUMENTO_TARJ_EXTRANJERIA, evt);
    }// GEN-LAST:event_jToggleButton4ActionPerformed

    private void jToggleButton3ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jToggleButton3ActionPerformed
        NovusUtils.beep();
        jTextField1.requestFocus();
        caracteresAceptados = reAlfa;
        seleccionEsteDocumento(DOCUMENTO_PASAPORTE, evt);
    }// GEN-LAST:event_jToggleButton3ActionPerformed

    private void jToggleButton2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jToggleButton2ActionPerformed
        NovusUtils.beep();
        jTextField1.requestFocus();
        caracteresAceptados = reNumerico;
        seleccionEsteDocumento(DOCUMENTO_CEDULA_EXTRANJERIA, evt);
    }// GEN-LAST:event_jToggleButton2ActionPerformed

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jToggleButton1ActionPerformed
        NovusUtils.beep();
        jTextField1.requestFocus();
        caracteresAceptados = reNumerico;
        seleccionEsteDocumento(DOCUMENTO_NIT_DE_OTRO_PAIS, evt);
    }// GEN-LAST:event_jToggleButton1ActionPerformed

    private void jLabel13MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel13MousePressed

    }// GEN-LAST:event_jLabel13MousePressed

    private void jLabel13MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel13MouseReleased
        NovusUtils.beep();
        cerrar();
    }// GEN-LAST:event_jLabel13MouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> JComboTipoDocumentos;
    private javax.swing.JPanel boton_cerrar;
    private javax.swing.JPanel botones_confirmacion;
    private javax.swing.JLabel btn_aceptar;
    private javax.swing.JLabel btn_denegar1;
    private javax.swing.JPanel confirmacion;
    private javax.swing.JLabel fndFondo;
    private javax.swing.JLabel jCerrar;
    public static javax.swing.JLabel jCerrar1;
    private javax.swing.JLabel jIcono;
    private javax.swing.JLabel jIcono1;
    private javax.swing.JLabel jInfoDatafono;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    public javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    public javax.swing.JLabel jLabel7;
    public static javax.swing.JLabel jLabel8;
    public javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jMensaje;
    private javax.swing.JLabel jMensajesIdentificacion;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    public static javax.swing.JTextField jTextField1;
    private javax.swing.JLabel jTitulo;
    private javax.swing.JLabel jTransacciones;
    public javax.swing.JLabel lblConsultar;
    private javax.swing.JPanel msjDatafonos;
    private javax.swing.JPanel notificaniones;
    private javax.swing.JPanel pnlPrincipal;
    private javax.swing.ButtonGroup tipoDocumento;
    // End of variables declaration//GEN-END:variables

    void activar(boolean activar) {
        lblConsultar.setEnabled(activar);
        jLabel9.setEnabled(activar);
        jTextField1.setEnabled(activar);
        jLabel7.setEnabled(activar);
        JComboTipoDocumentos.setEnabled(activar);
    }

    private void consultarCliente() {
        activar(false);
        if (identificadorTipoDocumento != 0) {
            numeroDocumento = jTextField1.getText();
            
            JsonObject data = new JsonObject();
            data.addProperty("icono", "/com/firefuel/resources/loader_fac.gif");
            data.addProperty("mensajeError", "ESPERE UN MOMENTO");
            loader(data);

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    clienteFE = consultaClienteEnviarFE.consultarCliente(numeroDocumento, identificadorTipoDocumento, notificadorView);
                    if (clienteFE.has("codigoError")) {
                        activar(true);
                        nuevo = false;
                        lblConsultar.setText("CONSULTAR");
                        jLabel7.setEnabled(false);
                        jLabel9.setEnabled(true);
                        consultarCliente = false;
                        if (asignarDatos) {
                            mdao.actualizarEstadoMovimientosClientes(3, numeroVenta);
                            mdao.actualizarEstadoTransmision(3, numeroVenta);
                        }
                    } else {
                        nuevo = true;
                        jLabel7.setEnabled(true);
                        jLabel9.setEnabled(true);
                        lblConsultar.setText("NUEVO");
                        lblConsultar.setEnabled(true);
                        consultarCliente = false;
                        validacionMensajeIncompletos(clienteFE);
                    }
                    if (consulta && nuevo) {
                        nuevo = true;
                        activar(true);
                    }

                }
            };

            CompletableFuture<Void> completable = CompletableFuture.runAsync(runnable);

        }
    }

  

    private void mostrarLoader(String mensaje) {
        JsonObject data = new JsonObject();
        data.addProperty("icono", "/com/firefuel/resources/loader_fac.gif");
        data.addProperty("mensajeError", mensaje);
        loader(data);
    }

    private void manejarResultadoConsulta(JsonObject cliente) {
        if (cliente == null || cliente.has("codigoError")) {
            activar(true);
            nuevo = false;
            consultarCliente = false;
            jLabel7.setEnabled(false);
            jLabel9.setEnabled(true);
            lblConsultar.setText("CONSULTAR");

            if (asignarDatos) {
                mdao.actualizarEstadoMovimientosClientes(3, numeroVenta);
                mdao.actualizarEstadoTransmision(3, numeroVenta);
            }
        } else {
            nuevo = true;
            consultarCliente = false;
            jLabel7.setEnabled(true);
            jLabel9.setEnabled(true);
            lblConsultar.setText("NUEVO");
            lblConsultar.setEnabled(true);
            validacionMensajeIncompletos(cliente);
        }

        if (consulta && nuevo) {
            activar(true);
        }
    }


    private void validacionMensajeIncompletos(JsonObject cliente) {
        if (cliente.has("errorFaltaCampos") && cliente.get("errorFaltaCampos").getAsBoolean()) {
            jLabel7.setEnabled(false);
        } else {
            mostrarPanel("pnl_principal");

        }
    }

    private void consultaAsignarCliente() {
        if (!this.asignacionClienteBean.getDatosCliente().isEmpty() && this.asignacionClienteBean.getDatosCliente().containsKey("DatosCliente")) {
            numeroDocumento = this.asignacionClienteBean.getDatosCliente().get("DatosCliente").getAsJsonObject().get("documentoCliente").getAsString();
            jTextField1.setText(numeroDocumento);
            identificadorTipoDocumento = this.asignacionClienteBean.getDatosCliente().get("DatosCliente").getAsJsonObject().get("identificacion_cliente").getAsLong();
        } else {
            JComboTipoDocumentos.setSelectedItem("CONSUMIDOR FINAL");
            jTextField1.setText(consumidorFinal);
            numeroDocumento = consumidorFinal;
            identificadorTipoDocumento = DOCUMENTO_CLIENTES_VARIOS;
        }
    }

    private void enviarFacturaElectronica(boolean placa, JsonObject objPlaca, JsonObject objManguera) {
        NovusUtils.printLn("enviarFacturaElectronica(boolean placa, JsonObject objPlaca, JsonObject objManguera) _");
      
        if (this.convertir) {
            convertidor.ConvertidorPOSaFE(notificadorView, movimientoFac, clienteFE, this.notaP);
        } else {
            consultaClienteEnviarFE.movimientosBean = movimientosBean;
            consultaClienteEnviarFE.setDatosClientesFidelizacion(datosClienteFidelizaion);
            consultaClienteEnviarFE.setIdentificadorPuntoDeventa(codigoIndentificacionEstacion);
            consultaClienteEnviarFE.setFoundClient(foundClient);
            try {

                if (validarEnvioRedencionBono(RESPUESTA_FACTURA)) {

                    JsonObject mensajeLoader = new JsonObject();
                    mensajeLoader.addProperty("mensajeError", "REDIMIENDO BONOS, POR FAVOR ESPERE....");
                    mensajeLoader.addProperty("icono", "/com/firefuel/resources/loader_fac.gif");
                    loader(mensajeLoader);

                    CompletableFuture.runAsync(() -> {
                        InformacionVentaClienteFE infoVentaCliente = Main.gson.fromJson(RESPUESTA_FACTURA, InformacionVentaClienteFE.class);

                        respuestaRedencionBono = redencionBonoHandler.execute(
                                FidelizacionFacade.buildRequestRedencionBonoCanastilla(infoVentaCliente));

                        procesarRespuestaBono(respuestaRedencionBono, consultaClienteEnviarFE);

                    });
                } else {
                    consultaClienteEnviarFE.enviarFacturaElectronica((JFrame) principal, notificadorView,
                            clienteFE, RESPUESTA_FACTURA, CANASTILLA, placa,
                            objPlaca, objManguera, this.MANUAL);
                }
            } catch (ParseException ex) {
                Logger.getLogger(ClienteFacturaElectronica.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void procesarRespuestaBono(RespuestaRedencionBono respuestaRedencionBono, ConsultaClienteEnviarFE enviarFE) {
        NovusUtils.printLn("Respuesta Redencion Bono: " + Main.gson.toJsonTree(respuestaRedencionBono));
        switch (respuestaRedencionBono.getStatus()) {
            case NovusConstante.STATUS_200:
                if (respuestaRedencionBono.getMensajeError() == null) {
                    try {
                        enviarFE.enviarFacturaElectronica((JFrame) principal, notificadorView,
                                clienteFE, RESPUESTA_FACTURA, CANASTILLA, this.placa,
                                new JsonObject(), new JsonObject(), this.MANUAL);
                    } catch (ParseException ex) {
                        Logger.getLogger(ClienteFacturaElectronica.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    mensajeErrorBonos("ERROR AL REDIMIR LOS BONOS");
                }
                break;
            case NovusConstante.STATUS_400:
            case NovusConstante.STATUS_404:
            case NovusConstante.STATUS_500:
                mensajeErrorBonos("ERROR AL REDIMIR LOS BONOS");
                break;
            default:
                mensajeErrorBonos("ERROR AL REDIMIR LOS BONOS");
                break;
        }

    }

    private void mensajeErrorBonos(String mensaje) {
        showMessage(mensaje,
                "/com/firefuel/resources/btBad.png",
                true, this::mostrarMenuPrincipal,
                true, LetterCase.FIRST_UPPER_CASE);
    }

    private boolean validarEnvioRedencionBono(JsonObject infoVenta) {
        boolean validar = false;
        InformacionVentaClienteFE infoVentaCliente = Main.gson.fromJson(infoVenta, InformacionVentaClienteFE.class);
        if (infoVentaCliente != null && infoVentaCliente.getDatos_FE().getPagos() != null) {
            List<MediosPagoVenta> pagos = infoVentaCliente.getDatos_FE().getPagos();
            for (MediosPagoVenta pago : pagos) {
                if (pago.getIdentificacionMediosPagos() == NovusConstante.ID_MEDIO_BONO_TERPEL) {
                    validar = true;
                    break;
                }
            }
        }
        return validar;
    }

    private void enviarVentaSinFacturarAppTerpel(long numeroVenta, String msg) {

        jIcono.setText("");
        jMensaje.setText("");
        NovusUtils.printLn("[enviarVentaSinFacturar]" + numeroVenta);
        showMessage("ENVIANDO FACTURA ELECTRONICA...",
                "/com/firefuel/resources/btOk.png",
                true, cerrarTodo,
                false, LetterCase.FIRST_UPPER_CASE);


        JsonObject datosCliente = new JsonObject();
        datosCliente.addProperty("documentoCliente", jTextField1.getText());
        datosCliente.addProperty("identificacion_cliente", identificadorTipoDocumento);
        
        // 🔍 DEBUG: Verificar valores antes de llamar datosSinfacturar
        System.out.println("🔍 DEBUG enviarVentaSinFacturarAppTerpel():");
        System.out.println("    - numeroVenta parámetro: " + numeroVenta);
        System.out.println("    - this.numeroVenta: " + this.numeroVenta);
        System.out.println("    - this.movimiento es null: " + (this.movimiento == null));
        System.out.println("    - this.movimiento.getId(): " + (this.movimiento != null ? this.movimiento.getId() : "null"));
        
        facturaElectronicaVentaEnVivo.datosSinfacturar(clienteFE, datosCliente, numeroVenta, this.principal, notificadorView, true, this.movimiento.getId(), msg, true);
        mdao.actualizarEstadoTransmision(3, numeroVenta);
        mdao.actualizarEstadoMovimientosClientes(3, numeroVenta);
    }

    private void enviarVentaSinFacturar(long numeroVenta) {
        String datafonoMsg = "Se enviara factura electronica una vez se confirme el pago con datafono";
        jIcono.setText("");
        jMensaje.setText("");
        NovusUtils.printLn("[enviarVentaSinFacturar]" + numeroVenta);
        showMessage("ENVIANDO FACTURA ELECTRONICA...",
                "/com/firefuel/resources/btOk.png",
                true, cerrarTodo,
                false, LetterCase.FIRST_UPPER_CASE);


        JsonObject datosCliente = new JsonObject();
        datosCliente.addProperty("documentoCliente", jTextField1.getText());
        datosCliente.addProperty("identificacion_cliente", identificadorTipoDocumento);
        
        // 🔍 DEBUG: Verificar valores antes de llamar datosSinfacturar
        System.out.println("🔍 DEBUG enviarVentaSinFacturar():");
        System.out.println("    - numeroVenta parámetro: " + numeroVenta);
        System.out.println("    - this.numeroVenta: " + this.numeroVenta);
        System.out.println("    - this.movimiento es null: " + (this.movimiento == null));
        System.out.println("    - this.movimiento.getId(): " + (this.movimiento != null ? this.movimiento.getId() : "null"));
        
        if (new BuscarTransaccionDatafonoCaseUse(this.movimiento.getId()).execute()) {
            this.facturaElectronicaVentaEnVivo.datosSinfacturar(clienteFE, datosCliente, numeroVenta, this.principal, notificadorView, true, this.movimiento.getId(), datafonoMsg, false);
        } else {
            this.facturaElectronicaVentaEnVivo.datosSinfacturar(clienteFE, datosCliente, numeroVenta, this.principal, notificadorView, datafono, this.movimiento.getId(), datafonoMsg, isAppTerpelPendiente);
        }

        if (clienteFE.get("imprimir").getAsBoolean() && !this.datafono && !this.isAppTerpelPendiente) {
            NovusUtils.printLn("estoy ejecutando esta parte de imprimir en enviarVentaSinFacturar");
            imprimirVentaFe(numeroVenta);
        }
    }

    // 🚀 OPTIMIZACIÓN: ExecutorService dedicado para impresión asíncrona
    private static final ExecutorService PRINT_EXECUTOR = Executors.newFixedThreadPool(2, r -> {
        Thread t = new Thread(r, "ImpresionFE-" + System.currentTimeMillis());
        t.setDaemon(true);
        return t;
    });

    /**
     * 🔄 MIGRADO A SERVICIO PYTHON
     * 🚀 OPTIMIZACIÓN: Método de impresión asíncrono con CompletableFuture
     * @version 2.0 - Migrado a Python
     */
    private void imprimirVentaFe(long id) {
        long startTime = System.currentTimeMillis();
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  🐍 SERVICIO PYTHON - CLIENTE FACTURA ELECTRÓNICA       ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("🚀 DEBUG [imprimirVentaFe]: INICIO - ID: " + id);
        System.out.println("  ⏰ Timestamp inicio: " + new Date());
        
        // 🚀 OPTIMIZACIÓN: Ejecutar de forma asíncrona para no bloquear el hilo principal
        CompletableFuture.supplyAsync(() -> {
            System.out.println("  🔄 DEBUG [imprimirVentaFe]: Iniciando procesamiento asíncrono...");
            
            long idMovimiento = 0L;
            if (findByParameterUseCase.execute()) {
                System.out.println("  📋 DEBUG [imprimirVentaFe]: Usando remisión activa");
                idMovimiento = this.mdao.buscarMOvimientoIdRemision(numeroVenta);
                System.out.println("  🔍 ID Movimiento (remisión): " + idMovimiento);
            } else {
                System.out.println("  📋 DEBUG [imprimirVentaFe]: Usando factura electrónica");
                // 🚀 FALLBACK: Si id es 0, usar el idMovimiento de this.movimiento
                if (id == 0 && this.movimiento != null) {
                    System.out.println("  🔄 DEBUG [imprimirVentaFe]: Aplicando fallback - usando this.movimiento.getId(): " + this.movimiento.getId());
                    obtenerIdTransmisionDesdeMovimientoUseCase.setIdMovimiento(this.movimiento.getId());
                    long idTransmisionObtenido = obtenerIdTransmisionDesdeMovimientoUseCase.execute();
                    if (idTransmisionObtenido > 0) {
                        System.out.println("  ✅ DEBUG [imprimirVentaFe]: Fallback exitoso - nuevo id: " + idTransmisionObtenido);
                        return idTransmisionObtenido; // Usar el id obtenido para buscar movimiento
                    }
                }
                idMovimiento = this.mdao.buscarMOvimientoId(id);
                System.out.println("  🔍 ID Movimiento (FE): " + idMovimiento);
            }
            
            return idMovimiento;
            
        }, PRINT_EXECUTOR).thenAcceptAsync(idMovimiento -> {
            System.out.println("  🖨️ DEBUG [imprimirVentaFe]: Preparando impresión para ID movimiento: " + idMovimiento);
            
            // 🚀 MIGRACIÓN A SERVICIO PYTHON - INICIO
            String funcion = "IMPRIMIR VENTAS - CLIENTE FE";
            JsonObject json = new JsonObject();
            json.addProperty("identificadorMovimiento", idMovimiento);
            json.addProperty("movement_id", idMovimiento);
            json.addProperty("flow_type", "CONSULTAR_VENTAS");  // ✅ Usar flow_type estándar
            
            boolean esRemision = findByParameterUseCase.execute();
            String tipoDocumento = esRemision ? "REMISION" : "FACTURA-ELECTRONICA";  // ✅ Corregir formato
            json.addProperty("report_type", tipoDocumento);
            
            // ✅ Body vacío: Python consultará desde BD (venta ya existe en registry)
            JsonObject bodyJson = new JsonObject();
            json.add("body", bodyJson);
            
            // URL del servicio Python
            String url = NovusConstante.SECURE_CENTRAL_POINT_PRINT_TICKET_SALES;
            
            TreeMap<String, String> header = new TreeMap<>();
            header.put("Content-type", "application/json; charset=UTF-8");
            header.put("Accept", "application/json");
            
            System.out.println("  🌐 DEBUG [imprimirVentaFe]: URL Servicio Python: " + url);
            System.out.println("  📤 DEBUG [imprimirVentaFe]: Tipo Documento: " + tipoDocumento);
            System.out.println("  📤 DEBUG [imprimirVentaFe]: Payload: " + json.toString());
            
            if (!esRemision) {
                System.out.println("  📝 DEBUG [imprimirVentaFe]: Actualizando estado de impresión...");
                electronicaDao.actualizarEstadoImpresion(idMovimiento);
                System.out.println("  ✅ DEBUG [imprimirVentaFe]: Estado actualizado");
            }
            
            System.out.println("  🚀 DEBUG [imprimirVentaFe]: Iniciando cliente WS asíncrono a Python...");
            ClientWSAsync client = new ClientWSAsync(funcion, url, NovusConstante.POST, json, true, false, header, 10000);
            client.start();
            
            JsonObject response = client.getResponse();
            if (response != null) {
                System.out.println("  ✅ Respuesta del servicio Python: " + response.toString());
                if (response.has("success") && response.get("success").getAsBoolean()) {
                    System.out.println("  ✅ IMPRESIÓN EXITOSA EN SERVICIO PYTHON - CLIENTE FE");
                } else {
                    String mensaje = response.has("message") ? response.get("message").getAsString() : "Sin mensaje";
                    System.out.println("  ⚠️  Python respondió con error: " + mensaje);
                }
            } else {
                System.out.println("  ⚠️  Sin respuesta del servicio Python (puede estar apagado)");
            }
            
            System.out.println("  ⚡ DEBUG [imprimirVentaFe]: Cliente WS Python completado");
            // 🚀 MIGRACIÓN A SERVICIO PYTHON - FIN
            
        }, PRINT_EXECUTOR).whenComplete((result, throwable) -> {
            long duration = System.currentTimeMillis() - startTime;
            
            if (throwable != null) {
                System.out.println("  ❌ DEBUG [imprimirVentaFe]: ERROR después de " + duration + "ms");
                System.out.println("  🔥 Mensaje error: " + throwable.getMessage());
                throwable.printStackTrace();
            } else {
                System.out.println("  ✅ DEBUG [imprimirVentaFe]: COMPLETADO exitosamente en " + duration + "ms");
            }
            
            System.out.println("🏁 DEBUG [imprimirVentaFe]: FIN - Timestamp: " + new Date());
            System.out.println("═══════════════════════════════════════════════════════════");
        });
        
        System.out.println("  🎯 DEBUG [imprimirVentaFe]: Tarea asíncrona lanzada, continuando ejecución principal...");
     }


    // private void imprimirVentaFe(long id) {

    //     long idMovimiento = 0L;
    //     if (this.mdao.remisionActiva()) {
    //         idMovimiento = this.mdao.buscarMOvimientoIdRemision(numeroVenta);
    //     } else {
    //         idMovimiento = this.mdao.buscarMOvimientoId(id);
    //     }

    //     String funcion = "IMPRIMIR VENTAS";
    //     JsonObject json = new JsonObject();
    //     json.addProperty("identificadorMovimiento", idMovimiento);
    //     JsonObject bodyJson = new JsonObject();
    //     json.add("body", bodyJson);
    //     String paramImpresion = this.mdao.remisionActiva() ? "/remision" : "/factura-electronica";
    //     String url = NovusConstante.SECURE_CENTRAL_POINT_IMPRESION_VENTA.concat(paramImpresion);
    //     String method = NovusConstante.POST;
    //     boolean isArray = false;
    //     boolean isDebug = true;
    //     TreeMap<String, String> header = new TreeMap<>();
    //     header.put("Content-type", "application/json");
    //     if (!this.mdao.remisionActiva()) {
    //         electronicaDao.actualizarEstadoImpresion(idMovimiento);
    //     } else {
    //         ClientWSAsync client = new ClientWSAsync(funcion, url, method, json, isDebug, isArray);
    //         client.start();
    //     }

    // }

    private void cerrar() {
        NovusConstante.ventanaFE = Boolean.FALSE;
        NovusUtils.printLn("Iniciando cerrado de la venta de -> " + ClienteFacturaElectronica.class.getName());
        if (volver != null) {
            volver.run();
            NovusUtils.printLn("ejecutando  el volver del metodo cerrar -> " + ClienteFacturaElectronica.class.getName());
        }
        if (MANUAL) {
            NovusUtils.printLn("entrando en el modo manual de contingencia -> " + ClienteFacturaElectronica.class.getName());
            if (ventanaVenta) {
                NovusUtils.printLn("volver a la ventana de contingencia  -> " + ClienteFacturaElectronica.class.getName());
                principal.mostrarSubPanel(new VentaManualMenuPanel(principal, ventanaVenta, MANUAL));
            } else {
                NovusUtils.printLn("volver al inicio ->" + ClienteFacturaElectronica.class.getName());
                principal.mostrarSubPanel(new VentaManualMenuPanel(principal, true));
            }
        } else {
            NovusUtils.printLn("manual es falso en el metodo cerrar  -> " + ClienteFacturaElectronica.class.getName());
            if (anterior != null) {
                NovusUtils.printLn("paso a la venta antiro -> " + ClienteFacturaElectronica.class.getName());
                anterior();
            } else {
                if (parent != null) {
                    NovusUtils.printLn("volver al inicio del parent -> " + ClienteFacturaElectronica.class.getName());
                    parent.setVisible(true);
                } else {
                    NovusUtils.printLn("volver al inicio del principal -> " + ClienteFacturaElectronica.class.getName());
                    principal.setVisible(true);
                }
            }
        }
        if (noApcetar != null) {
            NovusUtils.printLn("ejecutando el Runnable noAceptar -> " + ClienteFacturaElectronica.class.getName());
            noApcetar.run();
        }
        if (NovusConstante.KIOSCO_CAN_CONTINGENCIA) {
            NovusConstante.VENTAS_CONTINGENCIA = false;
        }
        NovusUtils.printLn("saliendo  del metodo cerrar -> " + ClienteFacturaElectronica.class.getName());
        this.setVisible(false);
        dispose();
        this.setVisible(false);
        WindowEvent windowClosing = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(windowClosing);
    }

    private void anterior() {
        anterior.run();
        JsonObject datosCliente = new JsonObject();
        datosCliente.addProperty("documentoCliente", jTextField1.getText());
        datosCliente.addProperty("identificacion_cliente", identificadorTipoDocumento);
        this.asignacionClienteBean.agregarInformacionCliente("DatosCliente", datosCliente);
    }

    public PersonaBean getCliente() {
        return cliente;
    }

    private void seleccionEsteDocumento(int DOCUMENTO_NIT, java.awt.event.ActionEvent evt) {
        identificadorTipoDocumento = DOCUMENTO_NIT;
        if (((JToggleButton) evt.getSource()).isSelected()) {
            ((JToggleButton) evt.getSource()).setForeground(Color.WHITE);
            ((JToggleButton) evt.getSource()).setBackground(new Color(153, 0, 0));
        } else {
            ((JToggleButton) evt.getSource()).setForeground(new Color(153, 0, 0));
            ((JToggleButton) evt.getSource()).setBackground(Color.WHITE);
        }
    }

    public void crearTransaccion() {
        NovusUtils.printLn("crearTransaccion");
        if (!this.IsVentaEnVivo) {
            if (jLabel7.isEnabled() && this.sendFac != null) {
                showMessage("ESPERE UN MOMENTO...",
                        "/com/firefuel/resources/loader_fac.gif",
                        false, null,
                        false, LetterCase.FIRST_UPPER_CASE);
                String funcion = "FAC POS A FACT ELECTRONICA.";
                String url = NovusConstante.getServer(NovusConstante.SOURCE_END_POINT_FACTURA_ELECTRONICA);
                String method = "POST";
                JsonObject json = this.sendFac;
                Thread response = new Thread() {
                    @Override
                    public void run() {
                        ClientWSAsync client = new ClientWSAsync(funcion, url, method, json, true);
                        JsonObject response = client.esperaRespuesta();
                        clienteRespuesta(response);
                    }
                };
                response.start();
            }
        } else {
            JsonObject obj = this.responseClient;
            crearPosVentaFEUseCase.execute(new CrearPosVentaFEParams(manguera.getCara(), manguera.getId(), obj));
            showMessage("DATOS REGISTRADOS CON EXITO, SE ACTUALIZA LOS DATOS DE FACTURA EN MANGUERA " + manguera.getCara(),
                    "/com/firefuel/resources/btOk.png",
                    true, this::volver,
                    true, LetterCase.FIRST_UPPER_CASE);
        }
    }

    public void volver() {
        ventaVivo.dispose();
        this.setVisible(false);
        principal.setVisible(true);
    }

    public void clienteRespuesta(JsonObject response) {
        if (response != null) {
            showMessage("FACTURA ELECTRONICA ENVIADA",
                    "/com/firefuel/resources/btOk.png",
                    true, this::mostrarMenuPrincipal,
                    true, LetterCase.FIRST_UPPER_CASE);
            if (this.PrincipalStore != null) {
                this.PrincipalStore.dispose();
            } else {
                this.principal.dispose();
            }
        } else {
            showMessage("NO SE PUDO PROCESAR LA FACTURA",
                    "/com/firefuel/resources/btBad.png",
                    true, this::mostrarMenuPrincipal,
                    true, LetterCase.FIRST_UPPER_CASE);
        }
    }

    public void clienteRespuesta() {
        enviarVentaSinFacturar(numeroVenta);
    }

    void restaurarVista() {
        showMessage("NO SE HA OBTENIDO RESPUESTA, REINTENTE",
                "/com/firefuel/resources/btBad.png",
                true, this::mostrarMenuPrincipal,
                true, LetterCase.FIRST_UPPER_CASE);
        jLabel8.setText("");
        jLabel7.setEnabled(false);
        jLabel9.setEnabled(true);
        jTextField1.setEnabled(true);
        lblConsultar.setEnabled(true);
        JComboTipoDocumentos.setEnabled(true);
        identificadorTipoDocumento = 0;
    }

    private void showMessage(String msj, String ruta,
            boolean habilitar, Runnable runnable,
            boolean autoclose, String letterCase) {
        ParametrosMensajes parametrosMensajes = new ParametrosMensajesBuilder().setMsj(msj)
                .setRuta(ruta).setHabilitar(habilitar)
                .setRunnable(runnable).setAutoclose(autoclose)
                .setLetterCase(letterCase).build();
        mostrarSubPanel(ShowMessageSingleton.showMassegesInstance().execute(parametrosMensajes));
    }

    private void showDialog(JDialog dialog) {
        JPanel panel = (JPanel) dialog.getContentPane();
        mostrarSubPanel(panel);
    }

    private void mostrarSubPanel(JPanel panel) {
        CardLayout layout = (CardLayout) pnlPrincipal.getLayout();
        pnlPrincipal.add("pnl_ext", panel);
        layout.show(pnlPrincipal, "pnl_ext");
    }

    private void mostrarMenuPrincipal() {
        CardLayout layout = (CardLayout) pnlPrincipal.getLayout();
        layout.show(pnlPrincipal, "pnl_principal");
        close();
    }

    public void mostrarPanel(String panel) {
        NovusUtils.showPanel(pnlPrincipal, panel);
    }

    private void mensajes(JsonObject data) {
        mostrarPanel("notificacion");

        if (data == null) {
            System.err.println("❌ JsonObject 'data' es null en mensajes(JsonObject)");
            jMensaje.setText("<html><b>Error inesperado</b>: no se recibió información.</html>");
            jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/default-warning.png")));
            return;
        }

        // Logs iniciales
        System.out.println("[mensajes] data recibido: " + data.toString());
        System.out.println("[mensajes] mensajeError = " + (data.has("mensajeError") ? data.get("mensajeError") : "NO DEFINIDO"));
        System.out.println("[mensajes] icono = " + (data.has("icono") ? data.get("icono") : "NO DEFINIDO"));
        System.out.println("[mensajes] error = " + (data.has("error") ? data.get("error") : "NO DEFINIDO"));
        System.out.println("[mensajes] datosImpresion = " + (data.has("datosImpresion") ? data.get("datosImpresion") : "NO DEFINIDO"));

        // Mensaje
        if (data.has("mensajeError") && !data.get("mensajeError").isJsonNull()) {
            jMensaje.setText("<html>" + data.get("mensajeError").getAsString() + "</html>");
        } else {
            jMensaje.setText("<html><b>Error inesperado</b>: mensaje no disponible.</html>");
            System.err.println("⚠️ 'mensajeError' no presente o es null.");
        }

        // Icono
        if (data.has("icono") && !data.get("icono").isJsonNull()) {
            try {
                jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource(data.get("icono").getAsString())));
            } catch (Exception e) {
                System.err.println("❌ Error cargando ícono: " + e.getMessage());
                jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/default-warning.png")));
            }
        } else {
            jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/default-warning.png")));
            System.err.println("⚠️ 'icono' no presente o es null.");
        }

        // Botón de cerrar
        CardLayout card = (CardLayout) botones_confirmacion.getLayout();
        card.show(botones_confirmacion, "boton_cerrar");

        if (!data.get("error").getAsBoolean() && data.has("datosImpresion") && !data.get("datosImpresion").isJsonNull()) {
            NovusUtils.printLn("🖨️ Mostrando mensaje con impresión habilitada.");
            cerrar = () -> {
                confirmar(data);
                NovusConstante.ventanaFE = Boolean.FALSE;
                cerrar = null;
            };
            tareaProgramada = scheduler.schedule(cerrar, 6, TimeUnit.SECONDS);
            scheduler.shutdown();
        } else {
            NovusUtils.printLn("🟡 Entrando al bloque else de mensajes(...)");

            cerrar = () -> {
                System.out.println("[mensajes] data.get('principal') = " + (data.has("principal") ? data.get("principal") : "NO DEFINIDO"));
                System.out.println("[mensajes] principal (objeto) = " + (principal != null ? "OK" : "NULL"));

                if (data.has("principal") && data.get("principal").getAsBoolean()) {
                    if (principal != null) {
                        principal.cerrarSubmenu();
                    } else {
                        System.err.println("⚠️ 'principal' es null en bloque 'principal'.");
                    }
                    cerrarTimer();
                    cerrar();
                    return;
                }

                if (volver != null) {
                    volver.run();
                    cerrarTimer();
                } else {
                    System.err.println("⚠️ 'volver' es null.");
                }

                if (ventas != null) {
                    ventas.setVisible(true);
                    cerrarTimer();
                    this.dispose();
                    this.setVisible(false);
                } else {
                    System.err.println("⚠️ 'ventas' es null.");
                }

                if (ventasHistorial != null) {
                    ventasHistorial.setVisible(true);
                    if (cerrarConsumo != null) {
                        cerrarConsumo.run();
                    } else {
                        System.err.println("⚠️ 'cerrarConsumo' es null.");
                    }
                    cerrarTimer();
                    this.dispose();
                    this.setVisible(false);
                } else {
                    System.err.println("⚠️ 'ventasHistorial' es null.");
                }

                if (data.has("inicio") && data.get("inicio").getAsBoolean() && !asignarDatos) {
                    if (principal != null) {
                        principal.cerrarSubmenu();
                    } else {
                        System.err.println("⚠️ 'principal' es null en bloque 'inicio'.");
                    }
                    cerrar = null;
                    cerrarTimer();
                    cerrar();
                    return;
                } else {
                    mostrarPanel("pnl_principal");
                    cerrar = null;
                    cerrarTimer();
                }

                if (regresarHIstotial != null) {
                    NovusConstante.ventanaFE = Boolean.FALSE;
                    regresarHIstotial.run();
                    cerrar = null;
                    cerrarTimer();
                    this.dispose();
                    this.setVisible(false);
                } else {
                    System.err.println("⚠️ 'regresarHIstotial' es null.");
                }

                finalizarProcesoAsignacion(data);
            };

            if (timeOutsManager != null) {
                timeOutsManager.setTimeoutUtilManager(5000, () -> {
                    if (cerrar != null) cerrar.run();
                    cerrarTimer();
                });
            } else {
                System.err.println("⚠️ 'timeOutsManager' es null.");
            }

            if (!asignarDatos && timer != null) {
                timer.start();
            }
        }

        jCerrar.setVisible(true);
    }


    private void finalizarProcesoAsignacion(JsonObject data) {
        if (asignarDatos) {
            if (data.get("error").getAsBoolean()) {
                mostrarPanel("pnl_principal");
                cerrar = null;
                cerrarTimer();
                dispose();
                this.setVisible(false);
            } else {
                cerrarTodo.run();
            }
        }
    }

    public void mostrarInicio() {
        mostrarPanel("pnl_principal");
    }

    public void limpiarComponentes() {
        jIcono.setText("");
        jMensaje.setText("");
        confirmacion.setVisible(false);
    }

    public void enviarPagoAdquiriente(long idTransmision) {
        Gson gson = new Gson();
        NovusUtils.printLn("***************************id de transmision***********************************************");
        NovusUtils.printLn("este es numero de la transmision -> " + idTransmision);
        NovusUtils.printLn("***************************id de transmision***********************************************");
        this.numeroVenta = idTransmision;
        infoActualizarCliente = gson.toJsonTree(this.asignacionClienteBean.getDatosCliente()).getAsJsonObject();
        JsonArray medios = infoActualizarCliente.get("MediosPago").getAsJsonObject().get("mediosDePagos").getAsJsonArray();

        for (JsonElement medio : medios) {
            JsonObject medioIt = medio.getAsJsonObject();
            mediosPagosBean.setId(medioIt.get("ct_medios_pagos_id").getAsLong());
            mediosPagosBean.setDescripcion(medioIt.get("descripcion").getAsString());
            mediosPagosBean.setRecibido(medioIt.get("valor_recibido").getAsLong());
            mediosPagosBean.setCambio(medioIt.get("valor_cambio").getAsLong());
            mediosPagosBean.setVoucher(medioIt.get("voucher").getAsString());
            mediosPagoVenta.add(mediosPagosBean);
        }
        mostrarPanel("mensajesDatafono");
        enviarPagoDatafono();
        cerrarTimer();
    }

    private void loader(JsonObject data) {
        mostrarPanel("notificacion");
        String mensaje = data.get("mensajeError").getAsString();
        mensaje = mensaje.substring(0, 1).toUpperCase()
                + mensaje.substring(1).toLowerCase();
        jMensaje.setText("".concat(mensaje));
        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource(data.get("icono").getAsString())));
        jCerrar.setVisible(false);
    }

    private void confirmarCliente() {
        mostrarPanel("notificacion");
        jMensaje.setText("<html><center>¿ Se facturará  a  cliente  consumidor  final  NIT 222222222222, está seguro ?</center></html>");
        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/alert.gif")));
        CardLayout card = (CardLayout) botones_confirmacion.getLayout();
        card.show(botones_confirmacion, "confirmacion");
        confirmarCliente = () -> {
            asignacionDatosCliente();
            confirmarCliente = null;
        };
        noConfirmarCliente = () -> {
            mostrarPanel("pnl_principal");
            noConfirmarCliente = null;
        };
    }

    private void confirmarConsumoPropio() {
        mostrarPanel("notificacion");
        jMensaje.setText("<html><center>¿ Se facturará  a  cliente  consumidor  final  NIT 222222222222, está seguro ?</center></html>");
        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/alert.gif")));
        CardLayout card = (CardLayout) botones_confirmacion.getLayout();
        card.show(botones_confirmacion, "confirmacion");
        confirmarCliente = () -> {
            confirmar();
            confirmarCliente = null;
        };
        noConfirmarCliente = () -> {
            mostrarPanel("pnl_principal");
            noConfirmarCliente = null;
        };
    }

    private void asignacionDatosCliente() {
        if (!this.asignacionClienteBean.getDatosCliente().isEmpty() && this.asignacionClienteBean.getDatosCliente().containsKey("VentaCliente")) {
            this.asignacionClienteBean.getDatosCliente().get("VentaCliente").getAsJsonObject().get("Cliente").getAsJsonObject().add("cliente", clienteFE);
            System.out.println("[asignacionDatosCliente] " + clienteFE.toString());
        }
        confirmar();
    }

    //TODO: NO SE USA AUN LA ULTIMA FNC -> UPDATE BD
    public void updateInfoCliente(long idTransmision) {
        Gson gson = new Gson();
        NovusUtils.printLn("recibiendo el id de la transmision -> " + idTransmision);
        this.numeroVenta = idTransmision;
        infoActualizarCliente = gson.toJsonTree(this.asignacionClienteBean.getDatosCliente()).getAsJsonObject();

        long idMovimiento = 0l;

        if (findByParameterUseCase.execute()) {
            NovusUtils.printLn("REMISION ACTIVA");
            idMovimiento = Main.mdao.buscarMOvimientoIdRemision(idTransmision);
        } else {
            NovusUtils.printLn("FE ACTIVA");
            idMovimiento = Main.mdao.buscarMOvimientoId(idTransmision);
        }

        NovusUtils.printLn("****************************");
        NovusUtils.printLn("ID TRANSMISION: " + idTransmision);
        NovusUtils.printLn("ID MOVIMIENTO:  " + idMovimiento);
        NovusUtils.printLn("****************************");

        infoActualizarCliente.get("VentaCliente").getAsJsonObject().addProperty("identificadorMovimiento", idMovimiento);

        boolean respuestaAsignacion = mdao.asignarDatosCliente(infoActualizarCliente.toString());
        if (infoActualizarCliente.has("MediosPago")) {
            JsonArray mediosPago = infoActualizarCliente.get("MediosPago").getAsJsonObject().get("mediosDePagos").getAsJsonArray();

            if (respuestaAsignacion) {
                actualizarInformacionCliente(mediosPago, idTransmision);
                this.asignacionClienteBean.getDatosCliente().clear();
            } else {
                int sincronizado = isVentaDatafono(mediosPago) ? 4 : 3;
                mdao.actualizarEstadoTransmision(sincronizado, idTransmision);
                mdao.actualizarEstadoMovimientosClientes(sincronizado, idTransmision);
                showMessage("ERROR AL ASIGNAR DATOS CLIENTE",
                        "/com/firefuel/resources/btBad.png",
                        true, cerrarTodo,
                        true, LetterCase.FIRST_UPPER_CASE);
            }
            // ACTUALIZAR INFORMACION DE CLIENTE Y ENVIAR FE
            enviarVentaSinFacturar(idTransmision);
            cerrar();
            if (cerrarTodo != null) {
                cerrarTodo.run();
                this.dispose();
                this.setVisible(false);
            }
        }
        
    }



    // public void updateInfoCliente(long idTransmision) {
    //     Gson gson = new Gson();
    //     NovusUtils.printLn("recibiendo el id de la transmision -> " + idTransmision);
    //     this.numeroVenta = idTransmision;
    //     infoActualizarCliente = gson.toJsonTree(this.asignacionClienteBean.getDatosCliente()).getAsJsonObject();

    //     long idMovimiento = 0l;

    //     if (Main.mdao.remisionActiva()) {
    //         NovusUtils.printLn("REMISION ACTIVA");
    //         idMovimiento = Main.mdao.buscarMOvimientoIdRemision(idTransmision);
    //     } else {
    //         NovusUtils.printLn("FE ACTIVA");
    //         idMovimiento = Main.mdao.buscarMOvimientoId(idTransmision);
    //     }

    //     NovusUtils.printLn("****************************");
    //     NovusUtils.printLn("ID TRANSMISION: " + idTransmision);
    //     NovusUtils.printLn("ID MOVIMIENTO:  " + idMovimiento);
    //     NovusUtils.printLn("****************************");

    //     infoActualizarCliente.get("VentaCliente").getAsJsonObject().addProperty("identificadorMovimiento", idMovimiento);

    //     boolean respuestaAsignacion = mdao.asignarDatosCliente(infoActualizarCliente.toString());
    //     if (infoActualizarCliente.has("MediosPago")) {
    //         JsonArray mediosPago = infoActualizarCliente.get("MediosPago").getAsJsonObject().get("mediosDePagos").getAsJsonArray();

    //         if (respuestaAsignacion) {
    //             actualizarInformacionCliente(mediosPago, idTransmision);
    //             this.asignacionClienteBean.getDatosCliente().clear();
    //         } else {
    //             int sincronizado = isVentaDatafono(mediosPago) ? 4 : 3;
    //             mdao.actualizarEstadoTransmision(sincronizado, idTransmision);
    //             mdao.actualizarEstadoMovimientosClientes(sincronizado, idTransmision);
    //             showMessage("ERROR AL ASIGNAR DATOS CLIENTE",
    //                     "/com/firefuel/resources/btBad.png",
    //                     true, cerrarTodo,
    //                     true, LetterCase.FIRST_UPPER_CASE);
    //         }
    //         // ACTUALIZAR INFORMACION DE CLIENTE Y ENVIAR FE
    //         enviarVentaSinFacturar(idTransmision);
    //         cerrar();
    //         if (cerrarTodo != null) {
    //             cerrarTodo.run();
    //             this.dispose();
    //             this.setVisible(false);
    //         }
    //     }
    // }

    private boolean isVentaDatafono(JsonArray arrayMedios) {
        return verificarMedioPago(arrayMedios, "CON DATAFONO");
    }

    private boolean verificarMedioPago(JsonArray arrayMedios, String medioDescripcion) {

        for (JsonElement arrayMedio : arrayMedios) {
            JsonObject dataMedios = arrayMedio.getAsJsonObject();
            if (dataMedios.get("descripcion").getAsString().equalsIgnoreCase(medioDescripcion)) {
                return true;
            }
        }
        return false;

    }

    private void actualizarInformacionCliente(JsonArray arrayMedios, long idTransmision) {
        NovusUtils.printLn("actualizarInformacionCliente(JsonArray arrayMedios, long idTransmision)");
        this.datafono = isVentaDatafono(arrayMedios);
        int sincronizado = this.datafono ? 4 : 2;

        if (!this.datafono) {
            // APPTERPEL VALIDA  QUE NO SE PONGA EN ESTADO 2
            this.isAppTerpelPendiente = verificarMedioPago(arrayMedios, MediosPagosDescription.APPTERPEL);
            sincronizado = this.isAppTerpelPendiente ? 4 : 2;
        }
        NovusUtils.printLn("este es el numero de transmision recibido en el proceso actualizarInformacionCliente -> " + idTransmision);

        mdao.actualizarEstadoTransmision(sincronizado, idTransmision);
        mdao.actualizarEstadoMovimientosClientes(sincronizado, idTransmision);

        long idMovimiento = 0l;
        if (findByParameterUseCase.execute()) {
            idMovimiento = mdao.buscarMOvimientoIdRemision(idTransmision);
        } else {
            idMovimiento = mdao.buscarMOvimientoId(idTransmision);
        }

        mdao.actualizarClienteMovimiento(idMovimiento, idTransmision, sincronizado);
        if (!asignarDatos) {
            confirmar();
        }
    }

    // private void actualizarInformacionCliente(JsonArray arrayMedios, long idTransmision) {
    //     NovusUtils.printLn("actualizarInformacionCliente(JsonArray arrayMedios, long idTransmision)");
    //     this.datafono = isVentaDatafono(arrayMedios);
    //     int sincronizado = this.datafono ? 4 : 2;

    //     if (!this.datafono) {
    //         // APPTERPEL VALIDA  QUE NO SE PONGA EN ESTADO 2
    //         this.isAppTerpelPendiente = verificarMedioPago(arrayMedios, MediosPagosDescription.APPTERPEL);
    //         sincronizado = this.isAppTerpelPendiente ? 4 : 2;
    //     }
    //     NovusUtils.printLn("este es el numero de transmision recibido en el proceso actualizarInformacionCliente -> " + idTransmision);

    //     mdao.actualizarEstadoTransmision(sincronizado, idTransmision);
    //     mdao.actualizarEstadoMovimientosClientes(sincronizado, idTransmision);

    //     long idMovimiento = 0l;
    //     if (Main.mdao.remisionActiva()) {
    //         idMovimiento = mdao.buscarMOvimientoIdRemision(idTransmision);
    //     } else {
    //         idMovimiento = mdao.buscarMOvimientoId(idTransmision);
    //     }

    //     mdao.actualizarClienteMovimiento(idMovimiento, idTransmision, sincronizado);
    //     if (!asignarDatos) {
    //         confirmar();
    //     }
    // }

    private void confirmar(JsonObject data) {
        NovusUtils.printLn("Mostrando Panel Notificacion");
        mostrarPanel("notificaion");
        jMensaje.setText("¿Desea imprimir el comprobante de venta?");
        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/alert.gif")));
        CardLayout card = (CardLayout) botones_confirmacion.getLayout();
        card.show(botones_confirmacion, "confirmacion");
        if (data.get("venta_en_vivo").getAsBoolean()) {
            confirmar = () -> {
                NovusUtils.printLn("ventana de venta envivo para la impresion -> " + ClienteFacturaElectronica.class.getName());
                this.facturaElectronicaVentaEnVivo.imprimirVenta(data.get("datosImpresion").getAsJsonObject());
                principal.cerrarSubmenu();
                cerrar();
                confirmar = null;
            };
        } else {
            confirmar = () -> {
                if (facturacionElectronica.remisionActiva()) {
                    NovusUtils.printLn("ventana de venta kco y can  para la impresion en remision -> " + ClienteFacturaElectronica.class.getName());
                    this.consultaClienteEnviarFE.imprimirComprobante(data.get("datosImpresion").getAsJsonObject());
                    principal.cerrarSubmenu();
                    cerrar();
                    confirmar = null;
                } else {
                    JsonObject mensajeLoader = new JsonObject();
                    mensajeLoader.addProperty("mensajeError", "ESPERE UN MOMENTO");
                    mensajeLoader.addProperty("icono", "/com/firefuel/resources/loader_fac.gif");
                    card.show(botones_confirmacion, "boton_cerrar");
                    loader(mensajeLoader);
                    Runnable imprimir = () -> {
                        NovusUtils.printLn("ventana de venta kco y can para la impresion -> " + ClienteFacturaElectronica.class.getName());
                        JsonObject respuestas = this.consultaClienteEnviarFE.imprimirComprobante(data.get("datosImpresion").getAsJsonObject());
                        principal.cerrarSubmenu();
                        mostrarPanel("notificaion");
                        jCerrar.setVisible(true);
                        jMensaje.setText("<html>" + respuestas.get("message").getAsString().toUpperCase() + "</html>");
                        String icono = respuestas.get("status").getAsInt() == 200 ? "/com/firefuel/resources/btOk.png" : "/com/firefuel/resources/btBad.png";
                        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource(icono)));
                        card.show(botones_confirmacion, "boton_cerrar");
                        cerrar = () -> cerrar();
                        mostrarPanelMensaje(respuestas.get("message").getAsString(),
                                icono, cerrar, LetterCase.FIRST_UPPER_CASE);
                    };
                    CompletableFuture.runAsync(imprimir);
                    confirmar = null;
                }
            };
        }

    }

    private void confirmar() {
        mostrarPanel("notificacion");
        jMensaje.setText("¿Desea imprimir el comprobante de venta?");
        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/alert.gif")));
        CardLayout card = (CardLayout) botones_confirmacion.getLayout();
        card.show(botones_confirmacion, "confirmacion");
        confirmar = () -> handleConfirmar(true);
        noApcetar = () -> handleConfirmar(false);
    }

    private void handleConfirmar(boolean imprimir) {
        clienteFE.addProperty("imprimir", imprimir);
        System.out.println("SE VA MEDIO DE PAGOS PORQUE APPTERPEL ES: " + isProcesoRechazada);
        System.out.println("SE VA MEDIO DE PAGOS PORQUE Datafono  ES: " + !asignarDatos);

        if (!asignarDatos) {
            enviarVentaSinFacturar(this.numeroVenta);
        } else if (!isProcesoRechazada) {
            if (this.transaccionProceso.getIdintegracion() == 1) {
                String APMessage = "Se enviara factura electronica una vez se confirme el pago con APPTERPEL".toUpperCase();
                enviarVentaSinFacturarAppTerpel(this.numeroVenta, APMessage);
            } else {
                enviarVentaSinFacturar(this.numeroVenta);
            }
        } else if (cerrarTodo != null) {
            // PASA A LA VENTANA DE MEDIOS DE PAGOS // 5UVT FE
            cerrarTodo.run();
            this.dispose();
            this.setVisible(false);
        }
        if (imprimir) {
            confirmar = null;
        } else {
            noApcetar = null;
        }
    }

    private void close() {
        cerrar();
    }

    private void cerrarTimer() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    public boolean isPagoDatafono() {
        boolean isPagoDatafono = false;
        for (int i = 0; i < mediosPagoVenta.size(); i++) {
            isPagoDatafono = mediosPagoVenta.get(i).getDescripcion().equalsIgnoreCase("CON DATAFONO");
        }
        return isPagoDatafono;
    }

    private void enviarPagoDatafono() {
        mostrarPanel("mensajesDatafono");
        JsonArray datafonos = infoActualizarCliente.get("Datafonos").getAsJsonObject().get("Datafonos").getAsJsonArray();
        for (JsonElement jsonDatafono : datafonos) {
            JsonObject infoDatafono = jsonDatafono.getAsJsonObject();
            plaquetaDatafono = infoDatafono.get("plaqueta").getAsString();
            adquiriente = infoDatafono.get("proveedor").getAsString();
            codigoTerminal = infoDatafono.get("codigo_terminal").getAsString();
            idAquiriente = infoDatafono.get("id_adquiriente").getAsInt();
        }
        ArrayList<MediosPagosBean> mediosDepagosSinDatafonos = new ArrayList<>();
        for (int i = 0; i < mediosPagoVenta.size(); i++) {
            if (mediosPagoVenta.get(i).getDescripcion().equalsIgnoreCase("CON DATAFONO")) {
                pagosDatafono.add(mediosPagoVenta.get(i));
            } else {
                mediosDepagosSinDatafonos.add(mediosPagoVenta.get(i));
            }
        }
        tipoVenta();
        procesarPago(this.movimiento, mediosDepagosSinDatafonos);
    }

    // private void procesarPago(MovimientosBean movimiento, ArrayList<MediosPagosBean> mediosPagosSinDatafono) {
    //     showPanel("mensajesDatafono", "ENVIANDO EL PAGO AL DATAFONO", "", "", loaderDatafono, false, null);
    //     pagoMixto = pagosDatafono.size() > 1 ? Boolean.TRUE : Boolean.FALSE;
    //     Thread response = new Thread() {
    //         @Override
    //         public void run() {
    //             JsonObject respuestaEnvio = new JsonObject();
    //             int contador = 0;

    //             for (MediosPagosBean medios : pagosDatafono) {
    //                 float totalPagoDatafono = medios.getRecibido();
    //                 if (contador == 0) {
    //                     respuestaEnvio = enviarPagosDatafonos.enviarVentaDatafonos(totalPagoDatafono, movimiento, adquiriente, codigoTerminal, idAquiriente, pagoMixto);
    //                 } else {
    //                     NovusUtils.pause(10);
    //                     respuestaEnvio = enviarPagosDatafonos.enviarVentaDatafonos(totalPagoDatafono, movimiento, adquiriente, codigoTerminal, idAquiriente, pagoMixto);
    //                 }
    //                 contador++;
    //             }

    //             Runnable volver = () -> {
    //                 if (cerrarTodo != null) {
    //                     cerrarTodo.run();
    //                 }

    //                 if (devolverDatafonos != null) {
    //                     devolverDatafonos.run();
    //                 }
    //             };

    //             Runnable continuar = () -> {
    //                 mostrarPanel("notificacion");
    //                 updateInfoCliente(numeroVenta);
    //                 if (cerrarTodo != null) {
    //                     devolverDatafonos = null;
    //                     cerrarTodo.run();
    //                 }
    //             };

    //             Runnable cerrar = () -> {
    //                 cerrarVistaAsignarCliente();
    //             };

    //             if (respuestaEnvio.get("estado").getAsInt() == 200) {
    //                 showPanel("mensajesDatafono",
    //                         respuestaEnvio.get("mensaje").getAsString(),
    //                         respuestaEnvio.get("informacionDatafono").getAsString(),
    //                         respuestaEnvio.get("transacciones").getAsString(),
    //                         pagoDatafono,
    //                         respuestaEnvio.get("cerrar").getAsBoolean(),
    //                         continuar);
                    
    //                 if (!mediosPagosSinDatafono.isEmpty()) {
    //                     actualizarMediosPagos.actulizarMediosDePagoSinDatafonos(movimiento, mediosPagosSinDatafono);
    //                 }
    //             } else {
    //                 showPanel("mensajesDatafono",
    //                         respuestaEnvio.get("mensaje").getAsString(),
    //                         respuestaEnvio.get("informacionDatafono").getAsString(),
    //                         respuestaEnvio.get("transacciones").getAsString(),
    //                         errorDatafono,
    //                         respuestaEnvio.get("cerrar").getAsBoolean(),
    //                         cerrar);
    //                 mdao.actualizarEstadoTransmision(2, numeroVenta);
    //                 mdao.actualizarEstadoMovimientosClientes(2, numeroVenta);
    //             }
    //         }
    //     };
    //     response.start();
    // }


    private void procesarPago(MovimientosBean movimiento, ArrayList<MediosPagosBean> mediosPagosSinDatafono) {
        showPanel("mensajesDatafono", "ENVIANDO EL PAGO AL DATAFONO", "", "", loaderDatafono, false, null);
        pagoMixto = pagosDatafono.size() > 1;
        executorService.submit(() -> {
            JsonObject respuesta = procesarPagosDatafono(movimiento);

            if (respuesta != null && respuesta.has("estado") && respuesta.get("estado").getAsInt() == 200) {
                handlePagoExitoso(respuesta, movimiento, mediosPagosSinDatafono);
            } else {
                handlePagoFallido(respuesta);
            }
        });
    }

    private JsonObject procesarPagosDatafono(MovimientosBean movimiento) {
        JsonObject respuesta = new JsonObject();
        for (int i = 0; i < pagosDatafono.size(); i++) {
            MediosPagosBean medios = pagosDatafono.get(i);
            if (i > 0) NovusUtils.pause(10);
            respuesta = enviarPagosDatafonos.enviarVentaDatafonos(
                medios.getRecibido(), movimiento, adquiriente, codigoTerminal, idAquiriente, pagoMixto
            );
        }
        return respuesta;
    }

    private void handlePagoExitoso(JsonObject respuesta, MovimientosBean movimiento, ArrayList<MediosPagosBean> mediosPagosSinDatafono) {
        Runnable continuar = () -> {
            mostrarPanel("notificacion");
            updateInfoCliente(numeroVenta);
            if (cerrarTodo != null) {
                devolverDatafonos = null;
                cerrarTodo.run();
            }
        };

        showPanel("mensajesDatafono",
                getAsString(respuesta, "mensaje"),
                getAsString(respuesta, "informacionDatafono"),
                getAsString(respuesta, "transacciones"),
                pagoDatafono,
                getAsBoolean(respuesta, "cerrar"),
                continuar
        );

        if (!mediosPagosSinDatafono.isEmpty()) {
            actualizarMediosPagos.actulizarMediosDePagoSinDatafonos(movimiento, mediosPagosSinDatafono);
        }
    }

    private void handlePagoFallido(JsonObject respuesta) {
        Runnable cerrar = this::cerrarVistaAsignarCliente;

        showPanel("mensajesDatafono",
                getAsString(respuesta, "mensaje"),
                getAsString(respuesta, "informacionDatafono"),
                getAsString(respuesta, "transacciones"),
                errorDatafono,
                getAsBoolean(respuesta, "cerrar"),
                cerrar
        );

        mdao.actualizarEstadoTransmision(2, numeroVenta);
        mdao.actualizarEstadoMovimientosClientes(2, numeroVenta);
    }

    private String getAsString(JsonObject json, String key) {
        return json != null && json.has(key) ? json.get(key).getAsString() : "";
    }

    private boolean getAsBoolean(JsonObject json, String key) {
        return json != null && json.has(key) && json.get(key).getAsBoolean();
    }

    

    public void tipoVenta() {
        if (pagosDatafono.size() > 1) {
            tipoVenta = NovusConstante.PAGO_MIXTO;
        }
    }

    private void showPanel(String panel, String msjPrincipal, String informacionDatafono, String transacciones, Icon icono, boolean cerrar, Runnable accion) {
        mostrarPanel("mensajesDatafono");
        jTitulo.setText(msjPrincipal);
        jInfoDatafono.setText(informacionDatafono);
        jTransacciones.setText(transacciones);
        jIcono1.setIcon(icono);
        jCerrar1.setVisible(cerrar);
        runnableDatafono = accion;
        if (accion != null) {
            setTimeout(5, accion);
        }
    }

    public void mostrarPanelMensaje(String mensaje, String icono,
            Runnable accion, String letterCase) {
        ParametrosMensajes parametrosMensajes = new ParametrosMensajesBuilder().setMsj(mensaje)
                .setRuta(icono).setHabilitar(habilitar).setRunnable(accion)
                .setLetterCase(letterCase).build();
        pnlPrincipal.add("card_mensajes", ShowMessageSingleton.showMassegesInstance().execute(parametrosMensajes));
        CardLayout panel = (CardLayout) pnlPrincipal.getLayout();
        panel.show(pnlPrincipal, "card_mensajes");
    }

    public void setTimeout(int delay, Runnable runnable) {
        new Thread(() -> {
            try {
                Thread.sleep((long) delay * 1000);
                if (runnable != null) {
                    runnable.run();
                }
            } catch (InterruptedException e) {
                NovusUtils.printLn("Interrupted.setTimeout" + e.getLocalizedMessage());
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public void terminarTareaProgramada() {
        if (tareaProgramada != null && !tareaProgramada.isDone()) {
            NovusUtils.printLn("Terminado Tarea");
            tareaProgramada.cancel(true);
        }
    }

    public IdentificationClient getDatosClienteFidelizaion() {
        return datosClienteFidelizaion;
    }

}
