package com.firefuel;
import com.WT2.appTerpel.application.UseCase.Payment.Procesing.ValidateIfDeleteAppTerpelPayment;
import com.WT2.appTerpel.application.UseCase.Payment.Procesing.ValidateIsAppTerpelPaymentSuccess;
import com.WT2.appTerpel.application.UseCase.RecoverMedioPagoImage;
import com.WT2.Containers.Dependency.SingletonMedioPago;
import com.WT2.appTerpel.domain.entities.MedioPagoImageBean;
import com.WT2.appTerpel.domain.valueObject.MediosPagosDescription;
import com.WT2.appTerpel.domain.valueObject.TransaccionMessageView;
import com.WT2.appTerpel.presentation.SendPaymentAppTerpelToEndPointHandler;
import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.application.useCases.datafonos.AdvertenciaDeNotificacionDeCierreTurnoUseCase;
import com.application.useCases.datafonos.HasOngoingDatafonSalesUseCase;
import com.application.useCases.movimientos.BuscarTransaccionDatafonoCaseUse;
import com.application.useCases.movimientosdetalles.GetByProductoMovimientoDetalleUseCase;
import com.bean.AsignacionClienteBean;
import com.bean.BonoViveTerpel;
import com.bean.MediosPagosBean;
import com.bean.MovimientosBean;
import com.bean.MovimientosDetallesBean;
import com.bean.Notificador;
import com.bean.ProductoBean;
import com.bean.Recibo;
import com.bean.ReciboExtended;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.DatafonosDao;
import com.dao.EquipoDao;
import com.dao.MovimientosDao;
import com.dao.SetupDao;
import com.dao.SurtidorDao;
import com.facade.ConfigurationFacade;
import com.facade.FidelizacionFacade;
import com.facade.PedidoFacade;
import com.firefuel.datafonos.ActualizarMediosPagos;
import com.firefuel.datafonos.AnulacionMedioPago;
import com.firefuel.datafonos.EnviarPagosDatafonos;
import com.firefuel.utils.ImageCache;
import com.firefuel.utils.ShowMessageSingleton;
import com.google.gson.*;
import com.neo.print.services.PrinterFacade;
import java.awt.CardLayout;
import java.awt.Color;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.services.TimeOutsManager;
import teclado.view.common.TecladoNumerico;
import com.application.useCases.gopass.GetIsGoPassMovimientoUseCase;

public class MedioPagosConfirmarViewController extends javax.swing.JDialog {
    GetIsGoPassMovimientoUseCase getIsGoPassMovimientoUseCase;

    public AdvertenciaDeNotificacionDeCierreTurnoUseCase advertenciaDeNotificacionDeCierreTurnoUseCase;
    public static final String PANEL_MEDIOS_PAGOS = "pnl_cambio_medio";
    public static final String PANEL_VALIDACION_BONO = "pnl_validar_bono";
    public static final String PANEL_REDENCION_VIVE_TERPEL = "pnl_redencion_vive_Terpel";
    Long cambio = 0L;
    int selectedRow = -1;
    int idMovimiento = 0;
    Long recibido = 0L;
    JFrame parent;
    boolean facturacionPOS = false;
    boolean isNewVenta;
    boolean isFac = false;
    ReciboExtended reciboRec;
    JsonObject dataViveTerpel = null;
    boolean ErrorViveTerpel = false;
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY_WITHOUT_ZERO);
    DefaultTableCellRenderer textRight;
    DefaultTableCellRenderer textCenter;
    ArrayList<MediosPagosBean> mediosPagosDisponibles;
    public ArrayList<MediosPagosBean> mediosPagoVenta = new ArrayList<>();
    ArrayList<BonoViveTerpel> BonosViveTerpel = new ArrayList<>();
    public static long consumoPropio;
    public static boolean isConsumoPropio;
    private Runnable runnable;
    private Runnable recargar;
    public static DatafonosView datafonosView = null;
    private Runnable runnableDatafono = null;
    private Runnable runnableClientesDatafono = null;
    public static boolean anulacion = Boolean.FALSE;

    boolean asignarCliente = false;
    Runnable cerrarTodo = null;
    Runnable cerrarSinDatatafono;
    Runnable cerrar = null;
    InfoViewController principal;

    MovimientosDao mdao;
    EquipoDao edao;
    SurtidorDao sdao;
    MovimientosBean movimiento;

    JsonArray datafonos = new JsonArray();
    JsonObject info = new JsonObject();
    private boolean pagoMixto = false;
    String plaquetaDatafono;
    String adquiriente;
    String codigoTerminal;
    int idAquiriente;
    String tipoVenta = "";

    ArrayList<Integer> idTransacciones = new ArrayList<>();
    ArrayList<MediosPagosBean> pagosDatafono = new ArrayList<>();
    Runnable asignarDatosDatafonos = null;
    Timer timer = null;
    Runnable aceptar;
    Runnable noAceptar;
    TimeOutsManager timeOutsManager;
    PedidoFacade pedidoFacade;
    Recibo reciboBean;

    public static boolean bonosValidados = false;


    public static Icon botonBloqueado, desigButton, loaderDatafono, errorDatafono, pagoDatafono;
    ValidacionBonosViveTerpel validacionBonos;
    DatafonosDao datafonosDao;
    SendPaymentAppTerpelToEndPointHandler senderPayment;
    PrinterFacade printerFacade;
    MediosPagosBean medioPagoBean;
    BonoViveTerpel bonosViveTerpel;
    EnviarPagosDatafonos enviarPagoDatafono;




    public void loadIcons() {
        botonBloqueado = ImageCache.getImage("/com/firefuel/resources/botones/bt-link-small.png");
        desigButton = ImageCache.getImage("/com/firefuel/resources/botones/boton-blanco-2.png");
        loaderDatafono = ImageCache.getImage("/com/firefuel/resources/enviando pago.gif");
        errorDatafono = ImageCache.getImage("/com/firefuel/resources/error enviar pago.png");
        pagoDatafono = ImageCache.getImage("/com/firefuel/resources/transaccionRecibida.gif");
    }


    DateTimeFormatter formateador = DateTimeFormatter.ofPattern("uuuu-MM-dd - HH:mm");
    private List<MedioPagoImageBean> medioPagos;

    public MedioPagosConfirmarViewController(JFrame parent, boolean modal, MovimientosBean movimiento,
                                             boolean isNewVenta, boolean isFac, ReciboExtended reciboRec) {

        super(parent, modal);
        this.parent = parent;
        initComponents();
        this.isNewVenta = isNewVenta;
        this.reciboRec = reciboRec;
        this.isFac = isFac;
        this.movimiento = movimiento;
        this.init();
    }

    public MedioPagosConfirmarViewController(JFrame parent, boolean modal, MovimientosBean movimiento,
                                             boolean isNewVenta, boolean isFac, ReciboExtended reciboRec, Runnable runnable) {
        super(parent, modal);
        this.parent = parent;
        this.runnable = runnable;
        initComponents();
        this.isNewVenta = isNewVenta;
        this.reciboRec = reciboRec;
        this.isFac = isFac;
        this.movimiento = movimiento;
        this.init();
    }

    public MedioPagosConfirmarViewController(JFrame parent, boolean modal, MovimientosBean movimiento,
                                             boolean isNewVenta, boolean isFac, ReciboExtended reciboRec, Runnable runnable, Runnable recargar) {
        super(parent, modal);
        this.parent = parent;
        this.runnable = runnable;
        initComponents();
        this.isNewVenta = isNewVenta;
        this.reciboRec = reciboRec;
        this.isFac = isFac;
        this.movimiento = movimiento;
        this.recargar = recargar;
        this.init();
    }

    public MedioPagosConfirmarViewController(InfoViewController parent, boolean modal, MovimientosBean movimiento,
                                             boolean isNewVenta, boolean isFac, ReciboExtended reciboRec, Runnable cerrarTodo,
                                             Runnable cerrar, boolean asignarCliente, Runnable cerrarSindatafono) {
        super(parent, modal);
        this.principal = parent;
        this.cerrar = cerrar;
        initComponents();
        this.isNewVenta = isNewVenta;
        this.reciboRec = reciboRec;
        this.isFac = isFac;
        this.movimiento = movimiento;
        this.asignarCliente = asignarCliente;
        this.cerrarTodo = cerrarTodo;
        this.cerrarSinDatatafono = cerrarSindatafono;
        this.init();
    }

    void init() {
        getIsGoPassMovimientoUseCase = new GetIsGoPassMovimientoUseCase(movimiento.getId());
        loadIcons();
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);
        btnAnularMedios.setIcon(botonBloqueado);
        if (asignarCliente) {
            btnAnularMedios.setEnabled(false);
        } else {
            btnAnularMedios.setEnabled(true);
        }
        btnAnterior.setFont(new java.awt.Font("Bebas Neue", 0, 24));
        textRight = new DefaultTableCellRenderer();
        textRight.setHorizontalAlignment(SwingConstants.RIGHT);
        textCenter = new DefaultTableCellRenderer();
        textCenter.setHorizontalAlignment(SwingConstants.CENTER);

        jtableMediosPago.setSelectionBackground(new Color(255, 182, 0));
        jtableMediosPago.setSelectionForeground(new Color(0, 0, 0));
        jtableMediosPago.setFont(new java.awt.Font("Bebas Neue", 0, 24)); // NOI18N

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(186, 12, 47));
        headerRenderer.setForeground(new Color(255, 255, 255));
        jtableMediosPago.setFillsViewportHeight(true);
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < jtableMediosPago.getModel().getColumnCount(); i++) {
            jtableMediosPago.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
            jtableMediosPago.getColumnModel().getColumn(i).setCellRenderer(textCenter);
        }

        TableRowSorter<TableModel> rowSorter = new TableRowSorter<TableModel>(jtableMediosPago.getModel()) {
            @Override
            public boolean isSortable(int column) {
                super.isSortable(column);
                return false;
            }
        };
        jtableMediosPago.setRowSorter(rowSorter);
        jScrollPane1.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        jScrollPane1.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane1.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));

        this.movimiento.setVentaTotal((float) ((this.movimiento.getVentaTotal() * 100) / 100.0));
        jtotal_venta.setText("$ " + df.format(this.movimiento.getVentaTotal()));
        cambio = 0L;
        selectedRow = -1;
        this.movimiento.setRecibidoTotal(this.movimiento.getVentaTotal());
        NovusUtils.printLn("Recibido total:" + this.movimiento.getRecibidoTotal());
        if (this.isNewVenta) {
            if (movimiento.getMediosPagos() != null) {
                for (Map.Entry<Long, MediosPagosBean> entry : movimiento.getMediosPagos().entrySet()) {
                    MediosPagosBean value = entry.getValue();
                    mediosPagoVenta.add(value);
                }
            }
        } else {
            facturacionPOS = Main.getParametroCoreBoolean(NovusConstante.PREFERENCE_FACTURACION_POS, false);
        }
        this.loadMediosPagos();
        cargarMediosPagos();
        if (this.mediosPagoVenta.isEmpty()) {
            jmedioCantidad.setText(df.format(this.movimiento.getVentaTotal()));
        }
        jeliminar_medio.setIcon(botonBloqueado);
        if (!asignarCliente) {
            btnAnterior.setVisible(false);
            jLabel3.setText("CONFIRMACION DE VENTA");
            jguardarVenta.setText("GUARDAR VENTA");
        } else {
            jguardarVenta.setText("GUARDAR");
        }

        MedioPagosConfirmarViewController.bonosValidados = false;
        NovusUtils.designButtons(btnAnterior, desigButton);
        NovusUtils.designButtons(jguardarVenta, desigButton);

    }

    private void cargarMediosPagos() {
        if (movimiento.getMediosPagos() != null && !movimiento.getMediosPagos().isEmpty()) {
            NovusUtils.printLn("recorriendo los medios de pagos de la veneta ");
            mediosPagoVenta.clear();
            for (Map.Entry<Long, MediosPagosBean> entry : movimiento.getMediosPagos().entrySet()) {
                MediosPagosBean value = entry.getValue();
                mediosPagoVenta.add(value);
            }
        } else {
            NovusUtils.printLn("buscando los medios de pagos de la venta");
            mediosPagoVenta.clear();
            llenarMediosDePagos(mdao.obtenerMediosPagoVenta(this.movimiento.getId()));
        }
    }

    public void changeLabelContinuar() {
        if (!asignarCliente) {
            btnAnterior.setVisible(false);
            jLabel3.setText("CONFIRMACION DE VENTA");
            jguardarVenta.setText("GUARDAR VENTA");
        } else {
            jguardarVenta.setText("GUARDAR");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        this.mdao = new MovimientosDao();
        this.edao = new EquipoDao();
        this.sdao = new SurtidorDao();
        this.movimiento = new MovimientosBean();
        this.validacionBonos = new ValidacionBonosViveTerpel();
        this.timeOutsManager = new TimeOutsManager();
        this.pedidoFacade = new PedidoFacade();
        this.reciboBean = new Recibo();
        this.datafonosDao = new DatafonosDao();
        this.senderPayment = new SendPaymentAppTerpelToEndPointHandler();
        this.printerFacade = new PrinterFacade();
        this.medioPagoBean = new MediosPagosBean();
        this.bonosViveTerpel = new BonoViveTerpel();
        this.enviarPagoDatafono = new EnviarPagosDatafonos();
        this.advertenciaDeNotificacionDeCierreTurnoUseCase = new AdvertenciaDeNotificacionDeCierreTurnoUseCase();

        pnlPrincipal = new javax.swing.JPanel();
        pnl_principal = new javax.swing.JPanel();
        pn_contenido_medio = new javax.swing.JPanel();
        pnl_cambio_medio = new javax.swing.JPanel();
        jmedioVoucher = new javax.swing.JTextField();
        jeliminar_medio = new javax.swing.JLabel();
        jeliminar_medio1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jtotal_venta = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jmedioPagoCombo = new javax.swing.JComboBox<>();
        jmedioCantidad = new javax.swing.JTextField();
        panel_teclado_medios = new TecladoNumerico()
        ;
        jbutton_addMedio = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtableMediosPago = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        jrecibido_venta = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnAnularMedios = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jMensajes = new javax.swing.JLabel();
        btnAnterior = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jguardarVenta = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jalerta = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        fondoMedio = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jCerrar = new javax.swing.JLabel();
        jTitulo = new javax.swing.JLabel();
        jInfoDatafono = new javax.swing.JLabel();
        jTransacciones = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jIcono = new javax.swing.JLabel();
        fndFondo = new javax.swing.JLabel();
        pnlConfirmacion = new javax.swing.JPanel();
        btnContinuar = new javax.swing.JLabel();
        btnSalir = new javax.swing.JLabel();
        jSalirConfirmacion = new javax.swing.JLabel();
        jsubtitulo = new javax.swing.JLabel();
        jtitulo = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        fndFondo1 = new javax.swing.JLabel();
        mensajeAnulacionDatafono = new javax.swing.JPanel();
        jTituloNotificaionAnulacion = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jIconoNotificacionAnulacion = new javax.swing.JLabel();
        fndFondo2 = new javax.swing.JLabel();
        pnlConfirmacionAnulacion = new javax.swing.JPanel();
        btnSi = new javax.swing.JLabel();
        btnNo = new javax.swing.JLabel();
        jtituloconfirmacionAnulacion = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        fndFondo3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        pnlPrincipal.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnlPrincipal.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnlPrincipal.setLayout(new java.awt.CardLayout());

        pnl_principal.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnl_principal.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnl_principal.setLayout(null);

        pn_contenido_medio.setOpaque(false);
        pn_contenido_medio.setLayout(new java.awt.CardLayout());

        pnl_cambio_medio.setOpaque(false);
        pnl_cambio_medio.setLayout(null);

        jmedioVoucher.setFont(new java.awt.Font("Bebas Neue", 0, 36)); // NOI18N
        jmedioVoucher.setForeground(new java.awt.Color(51, 51, 51));
        jmedioVoucher.setToolTipText("");
        jmedioVoucher.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 0, 0), 1, true));
        jmedioVoucher.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jmedioVoucherFocusGained(evt);
            }
        });
        jmedioVoucher.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jmedioVoucherKeyTyped(evt);
            }
        });
        pnl_cambio_medio.add(jmedioVoucher);
        jmedioVoucher.setBounds(40, 230, 270, 45);

        jeliminar_medio.setFont(new java.awt.Font("Bebas Neue", 0, 24)); // NOI18N
        jeliminar_medio.setForeground(new java.awt.Color(255, 255, 255));
        jeliminar_medio.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jeliminar_medio.setIcon(botonBloqueado);
        jeliminar_medio.setText("BORRAR");
        jeliminar_medio.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jeliminar_medio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jeliminar_medioMouseReleased(evt);
            }
        });
        pnl_cambio_medio.add(jeliminar_medio);
        jeliminar_medio.setBounds(450, 500, 174, 70);

        jeliminar_medio1.setFont(new java.awt.Font("Bebas Neue", 0, 22)); // NOI18N
        jeliminar_medio1.setForeground(new java.awt.Color(255, 255, 255));
        jeliminar_medio1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jeliminar_medio1.setIcon(ImageCache.getImage("/com/firefuel/resources/botones/bt-danger-small.png"));
        jeliminar_medio1.setText("QUITAR TODOS");
        jeliminar_medio1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jeliminar_medio1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jeliminar_medio1MouseReleased(evt);
            }
        });
        pnl_cambio_medio.add(jeliminar_medio1);
        jeliminar_medio1.setBounds(260, 500, 180, 70);

        jLabel5.setFont(new java.awt.Font("Conthrax", 0, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(102, 102, 102));
        jLabel5.setText("RECIBIDO:");
        pnl_cambio_medio.add(jLabel5);
        jLabel5.setBounds(40, 110, 250, 40);

        jtotal_venta.setFont(new java.awt.Font("Conthrax", 0, 36)); // NOI18N
        jtotal_venta.setForeground(new java.awt.Color(186, 12, 47));
        jtotal_venta.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jtotal_venta.setText("$ 0");
        jtotal_venta.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jtotal_ventaMouseReleased(evt);
            }
        });
        pnl_cambio_medio.add(jtotal_venta);
        jtotal_venta.setBounds(290, 50, 340, 60);

        jLabel4.setFont(new java.awt.Font("Conthrax", 0, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(102, 102, 102));
        jLabel4.setText("MEDIO PAGO:");
        pnl_cambio_medio.add(jLabel4);
        jLabel4.setBounds(40, 160, 250, 50);

        jmedioPagoCombo.setFont(new java.awt.Font("Bebas Neue", 0, 30)); // NOI18N
        jmedioPagoCombo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 0, 0), 1, true));
        jmedioPagoCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmedioPagoComboActionPerformed(evt);
            }
        });
        pnl_cambio_medio.add(jmedioPagoCombo);
        jmedioPagoCombo.setBounds(330, 160, 300, 50);

        jmedioCantidad.setFont(new java.awt.Font("Bebas Neue", 0, 36)); // NOI18N
        jmedioCantidad.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jmedioCantidad.setText("0");
        jmedioCantidad.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 0, 0), 1, true));
        jmedioCantidad.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jmedioCantidadFocusGained(evt);
            }
        });
        jmedioCantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jmedioCantidadKeyTyped(evt);
            }
        });
        pnl_cambio_medio.add(jmedioCantidad);
        jmedioCantidad.setBounds(330, 230, 250, 50);
        pnl_cambio_medio.add(panel_teclado_medios);
        panel_teclado_medios.setBounds(680, 74, 580, 464);

        jbutton_addMedio.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jbutton_addMedio.setForeground(new java.awt.Color(255, 255, 255));
        jbutton_addMedio.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jbutton_addMedio.setText("+");
        jbutton_addMedio.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbutton_addMedio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jbutton_addMedioMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jbutton_addMedioMouseReleased(evt);
            }
        });
        pnl_cambio_medio.add(jbutton_addMedio);
        jbutton_addMedio.setBounds(570, 230, 60, 50);

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setIcon(ImageCache.getImage("/com/firefuel/resources/botones/bt-danger-xsmall.png"));
        pnl_cambio_medio.add(jLabel6);
        jLabel6.setBounds(580, 230, 50, 50);

        jtableMediosPago.setFont(new java.awt.Font("Bebas Neue", 0, 28)); // NOI18N
        jtableMediosPago.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {

                },
                new String [] {
                        "MEDIO", "VOUCHER", "VALOR"
                }
        ) {
            Class[] types = new Class [] {
                    java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                    false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtableMediosPago.setRowHeight(40);
        jtableMediosPago.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jtableMediosPagoMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jtableMediosPago);
        if (jtableMediosPago.getColumnModel().getColumnCount() > 0) {
            jtableMediosPago.getColumnModel().getColumn(0).setResizable(false);
            jtableMediosPago.getColumnModel().getColumn(0).setPreferredWidth(200);
            jtableMediosPago.getColumnModel().getColumn(1).setResizable(false);
            jtableMediosPago.getColumnModel().getColumn(2).setResizable(false);
        }

        pnl_cambio_medio.add(jScrollPane1);
        jScrollPane1.setBounds(30, 310, 600, 190);

        jLabel7.setFont(new java.awt.Font("Conthrax", 0, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(102, 102, 102));
        jLabel7.setText("TOTAL: ");
        pnl_cambio_medio.add(jLabel7);
        jLabel7.setBounds(40, 50, 250, 60);

        jrecibido_venta.setFont(new java.awt.Font("Conthrax", 0, 36)); // NOI18N
        jrecibido_venta.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jrecibido_venta.setText("$ 0");
        pnl_cambio_medio.add(jrecibido_venta);
        jrecibido_venta.setBounds(290, 110, 340, 40);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("NÚMERO DE RECIBO");
        pnl_cambio_medio.add(jLabel2);
        jLabel2.setBounds(30, 280, 270, 20);

        btnAnularMedios.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnAnularMedios.setForeground(new java.awt.Color(255, 255, 255));
        btnAnularMedios.setIcon(ImageCache.getImage("/com/firefuel/resources/botones/bt-danger-small.png"));
        btnAnularMedios.setText("ANULAR");
        btnAnularMedios.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAnularMedios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAnularMediosMouseClicked(evt);
            }
        });
        pnl_cambio_medio.add(btnAnularMedios);
        btnAnularMedios.setBounds(70, 505, 180, 60);

        pn_contenido_medio.add(pnl_cambio_medio, "pnl_cambio_medio");

        pnl_principal.add(pn_contenido_medio);
        pn_contenido_medio.setBounds(10, 90, 1260, 610);

        jLabel3.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("ASIGNAR MEDIOS DE PAGO");
        pnl_principal.add(jLabel3);
        jLabel3.setBounds(100, 5, 710, 75);

        jMensajes.setFont(new java.awt.Font("Terpel Sans", 0, 24)); // NOI18N
        jMensajes.setForeground(new java.awt.Color(255, 255, 255));
        jMensajes.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        pnl_principal.add(jMensajes);
        jMensajes.setBounds(90, 700, 540, 100);

        btnAnterior.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        btnAnterior.setForeground(new java.awt.Color(153, 0, 0));
        btnAnterior.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnAnterior.setIcon(desigButton);
        btnAnterior.setText("ANTERIOR");
        btnAnterior.setToolTipText("");
        btnAnterior.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAnterior.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnAnteriorMouseReleased(evt);
            }
        });
        pnl_principal.add(btnAnterior);
        btnAnterior.setBounds(690, 726, 270, 60);

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setIcon(ImageCache.getImage("/com/firefuel/resources/btn_atras.png"));
        jLabel11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel11MouseReleased(evt);
            }
        });
        pnl_principal.add(jLabel11);
        jLabel11.setBounds(10, 10, 70, 71);

        jguardarVenta.setFont(new java.awt.Font("Bebas Neue", 0, 24)); // NOI18N
        jguardarVenta.setForeground(new java.awt.Color(153, 0, 0));
        jguardarVenta.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jguardarVenta.setIcon(desigButton);
        jguardarVenta.setText("SIGUIENTE");
        jguardarVenta.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jguardarVenta.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jguardarVentaMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jguardarVentaMouseReleased(evt);
            }
        });
        pnl_principal.add(jguardarVenta);
        jguardarVenta.setBounds(970, 720, 300, 70);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(ImageCache.getImage("/com/firefuel/resources/separadorVertical.png"));
        pnl_principal.add(jLabel27);
        jLabel27.setBounds(80, 10, 10, 68);

        jalerta.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jalerta.setIcon(ImageCache.getImage("/com/firefuel/resources/alerta.png"));
        pnl_principal.add(jalerta);
        jalerta.setBounds(0, 700, 80, 100);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(ImageCache.getImage("/com/firefuel/resources/separadorVertical.png"));
        pnl_principal.add(jLabel28);
        jLabel28.setBounds(80, 713, 3, 80);

        fondoMedio.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fondoMedio.setIcon(ImageCache.getImage("/com/firefuel/resources/fndMediosPagoBlanco.jpg"));
        fondoMedio.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        fondoMedio.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fondoMedio.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        pnl_principal.add(fondoMedio);
        fondoMedio.setBounds(0, 0, 1280, 800);

        pnlPrincipal.add(pnl_principal, "pnl_principal");

        jPanel1.setLayout(null);

        jCerrar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jCerrar.setForeground(new java.awt.Color(153, 0, 0));
        jCerrar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jCerrar.setIcon(desigButton);
        jCerrar.setText("CERRAR");
        jCerrar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jCerrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jCerrarMouseClicked(evt);
            }
        });
        jPanel1.add(jCerrar);
        jCerrar.setBounds(990, 18, 280, 53);

        jTitulo.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jTitulo.setForeground(new java.awt.Color(186, 12, 47));
        jTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jTitulo.setText("TITULO MENSAJE");
        jTitulo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel1.add(jTitulo);
        jTitulo.setBounds(150, 448, 980, 80);

        jInfoDatafono.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jInfoDatafono.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jInfoDatafono.setText("INFORMACION DATAFONO");
        jInfoDatafono.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel1.add(jInfoDatafono);
        jInfoDatafono.setBounds(280, 530, 740, 28);

        jTransacciones.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jTransacciones.setForeground(new java.awt.Color(186, 12, 47));
        jTransacciones.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jTransacciones.setText("TRANSACCION");
        jTransacciones.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel1.add(jTransacciones);
        jTransacciones.setBounds(280, 570, 740, 28);

        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setIcon(ImageCache.getImage("/com/firefuel/resources/separadorVertical.png"));
        jPanel1.add(jLabel32);
        jLabel32.setBounds(80, 10, 10, 68);

        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setIcon(ImageCache.getImage("/com/firefuel/resources/separadorVertical.png"));
        jPanel1.add(jLabel33);
        jLabel33.setBounds(120, 710, 10, 80);

        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setIcon(ImageCache.getImage("/com/firefuel/resources/separadorVertical.png"));
        jPanel1.add(jLabel34);
        jLabel34.setBounds(1130, 710, 10, 80);

        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setIcon(ImageCache.getImage("/com/firefuel/resources/logoDevitech_3.png"));
        jPanel1.add(jLabel35);
        jLabel35.setBounds(10, 710, 100, 80);

        jIcono.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jIcono.setIcon(loaderDatafono);
        jIcono.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel1.add(jIcono);
        jIcono.setBounds(520, 190, 248, 248);

        fndFondo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fndFondo.setIcon(ImageCache.getImage("/com/firefuel/resources/fndMsjDatafonos.png"));
        fndFondo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fndFondo.setMaximumSize(new java.awt.Dimension(1280, 800));
        fndFondo.setMinimumSize(new java.awt.Dimension(1280, 800));
        fndFondo.setPreferredSize(new java.awt.Dimension(1280, 800));
        fndFondo.setRequestFocusEnabled(false);
        jPanel1.add(fndFondo);
        fndFondo.setBounds(0, 0, 1280, 800);

        pnlPrincipal.add(jPanel1, "mensajesDatafono");

        pnlConfirmacion.setLayout(null);

        btnContinuar.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        btnContinuar.setForeground(new java.awt.Color(255, 255, 255));
        btnContinuar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnContinuar.setIcon(ImageCache.getImage("/com/firefuel/resources/botones/bt-danger-small.png"));
        btnContinuar.setText("SI");
        btnContinuar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnContinuar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnContinuarMouseReleased(evt);
            }
        });
        pnlConfirmacion.add(btnContinuar);
        btnContinuar.setBounds(417, 450, 180, 60);

        btnSalir.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        btnSalir.setForeground(new java.awt.Color(255, 255, 255));
        btnSalir.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnSalir.setIcon(ImageCache.getImage("/com/firefuel/resources/botones/bt-danger-small.png"));
        btnSalir.setText("NO");
        btnSalir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSalir.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnSalirMouseReleased(evt);
            }
        });
        pnlConfirmacion.add(btnSalir);
        btnSalir.setBounds(690, 450, 180, 54);

        jSalirConfirmacion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jSalirConfirmacion.setIcon(ImageCache.getImage("/com/firefuel/resources/btn_atras.png"));
        jSalirConfirmacion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSalirConfirmacionMouseReleased(evt);
            }
        });
        pnlConfirmacion.add(jSalirConfirmacion);
        jSalirConfirmacion.setBounds(10, 10, 70, 71);

        jsubtitulo.setFont(new java.awt.Font("Segoe UI", 1, 32)); // NOI18N
        jsubtitulo.setForeground(new java.awt.Color(153, 0, 0));
        jsubtitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jsubtitulo.setText("PERDERA LOS BONOS INGRESADOS");
        pnlConfirmacion.add(jsubtitulo);
        jsubtitulo.setBounds(210, 370, 860, 40);

        jtitulo.setFont(new java.awt.Font("Segoe UI", 1, 34)); // NOI18N
        jtitulo.setForeground(new java.awt.Color(153, 0, 0));
        jtitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jtitulo.setText("¿ESTA SEGURO QUE DESEA SALIR ? ");
        pnlConfirmacion.add(jtitulo);
        jtitulo.setBounds(210, 290, 860, 80);

        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setIcon(ImageCache.getImage("/com/firefuel/resources/separadorVertical.png"));
        pnlConfirmacion.add(jLabel36);
        jLabel36.setBounds(80, 10, 10, 68);

        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel37.setIcon(ImageCache.getImage("/com/firefuel/resources/separadorVertical.png"));
        pnlConfirmacion.add(jLabel37);
        jLabel37.setBounds(120, 710, 10, 80);

        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setIcon(ImageCache.getImage("/com/firefuel/resources/separadorVertical.png"));
        pnlConfirmacion.add(jLabel38);
        jLabel38.setBounds(1130, 710, 10, 80);

        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel39.setIcon(ImageCache.getImage("/com/firefuel/resources/logoDevitech_3.png"));
        pnlConfirmacion.add(jLabel39);
        jLabel39.setBounds(10, 710, 100, 80);

        fndFondo1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fndFondo1.setIcon(ImageCache.getImage("/com/firefuel/resources/fndConfirmarMensaje.png"));
        fndFondo1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fndFondo1.setMaximumSize(new java.awt.Dimension(1280, 800));
        fndFondo1.setMinimumSize(new java.awt.Dimension(1280, 800));
        fndFondo1.setPreferredSize(new java.awt.Dimension(1280, 800));
        fndFondo1.setRequestFocusEnabled(false);
        pnlConfirmacion.add(fndFondo1);
        fndFondo1.setBounds(0, 0, 1280, 800);

        pnlPrincipal.add(pnlConfirmacion, "pnlConfirmacion");

        mensajeAnulacionDatafono.setLayout(null);

        jTituloNotificaionAnulacion.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jTituloNotificaionAnulacion.setForeground(new java.awt.Color(186, 12, 47));
        jTituloNotificaionAnulacion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jTituloNotificaionAnulacion.setText("TITULO MENSAJE");
        jTituloNotificaionAnulacion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        mensajeAnulacionDatafono.add(jTituloNotificaionAnulacion);
        jTituloNotificaionAnulacion.setBounds(350, 310, 850, 200);

        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel40.setIcon(ImageCache.getImage("/com/firefuel/resources/separadorVertical.png"));
        mensajeAnulacionDatafono.add(jLabel40);
        jLabel40.setBounds(80, 10, 10, 68);

        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel41.setIcon(ImageCache.getImage("/com/firefuel/resources/separadorVertical.png"));
        mensajeAnulacionDatafono.add(jLabel41);
        jLabel41.setBounds(120, 710, 10, 80);

        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel42.setIcon(ImageCache.getImage("/com/firefuel/resources/separadorVertical.png"));
        mensajeAnulacionDatafono.add(jLabel42);
        jLabel42.setBounds(1130, 710, 10, 80);

        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel43.setIcon(ImageCache.getImage("/com/firefuel/resources/logoDevitech_3.png"));
        mensajeAnulacionDatafono.add(jLabel43);
        jLabel43.setBounds(10, 710, 100, 80);

        jIconoNotificacionAnulacion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jIconoNotificacionAnulacion.setIcon(loaderDatafono);
        jIconoNotificacionAnulacion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        mensajeAnulacionDatafono.add(jIconoNotificacionAnulacion);
        jIconoNotificacionAnulacion.setBounds(110, 290, 248, 248);

        fndFondo2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fndFondo2.setIcon(ImageCache.getImage("/com/firefuel/resources/fndRumbo.png"));
        fndFondo2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fndFondo2.setMaximumSize(new java.awt.Dimension(1280, 800));
        fndFondo2.setMinimumSize(new java.awt.Dimension(1280, 800));
        fndFondo2.setPreferredSize(new java.awt.Dimension(1280, 800));
        fndFondo2.setRequestFocusEnabled(false);
        mensajeAnulacionDatafono.add(fndFondo2);
        fndFondo2.setBounds(0, 0, 1280, 800);

        pnlPrincipal.add(mensajeAnulacionDatafono, "mensajeAnulacionDatafono");

        pnlConfirmacionAnulacion.setLayout(null);

        btnSi.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        btnSi.setForeground(new java.awt.Color(255, 255, 255));
        btnSi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnSi.setIcon(ImageCache.getImage("/com/firefuel/resources/botones/bt-danger-small.png"));
        btnSi.setText("SI");
        btnSi.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnSiMouseReleased(evt);
            }
        });
        pnlConfirmacionAnulacion.add(btnSi);
        btnSi.setBounds(417, 450, 180, 60);

        btnNo.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        btnNo.setForeground(new java.awt.Color(255, 255, 255));
        btnNo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnNo.setIcon(ImageCache.getImage("/com/firefuel/resources/botones/bt-danger-small.png"));
        btnNo.setText("NO");
        btnNo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnNoMouseReleased(evt);
            }
        });
        pnlConfirmacionAnulacion.add(btnNo);
        btnNo.setBounds(690, 450, 180, 54);

        jtituloconfirmacionAnulacion.setFont(new java.awt.Font("Segoe UI", 1, 34)); // NOI18N
        jtituloconfirmacionAnulacion.setForeground(new java.awt.Color(153, 0, 0));
        jtituloconfirmacionAnulacion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jtituloconfirmacionAnulacion.setText("¿ESTÁ SEGURO QUE DESEA ANULAR EL PAGO ? ");
        pnlConfirmacionAnulacion.add(jtituloconfirmacionAnulacion);
        jtituloconfirmacionAnulacion.setBounds(210, 250, 860, 190);

        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel44.setIcon(ImageCache.getImage("/com/firefuel/resources/separadorVertical.png"));
        pnlConfirmacionAnulacion.add(jLabel44);
        jLabel44.setBounds(80, 10, 10, 68);

        jLabel45.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel45.setIcon(ImageCache.getImage("/com/firefuel/resources/separadorVertical.png"));
        pnlConfirmacionAnulacion.add(jLabel45);
        jLabel45.setBounds(120, 710, 10, 80);

        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel46.setIcon(ImageCache.getImage("/com/firefuel/resources/separadorVertical.png"));
        pnlConfirmacionAnulacion.add(jLabel46);
        jLabel46.setBounds(1130, 710, 10, 80);

        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel47.setIcon(ImageCache.getImage("/com/firefuel/resources/logoDevitech_3.png"));
        pnlConfirmacionAnulacion.add(jLabel47);
        jLabel47.setBounds(10, 710, 100, 80);

        fndFondo3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fndFondo3.setIcon(ImageCache.getImage("/com/firefuel/resources/fndConfirmarMensaje.png"));
        fndFondo3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fndFondo3.setMaximumSize(new java.awt.Dimension(1280, 800));
        fndFondo3.setMinimumSize(new java.awt.Dimension(1280, 800));
        fndFondo3.setPreferredSize(new java.awt.Dimension(1280, 800));
        fndFondo3.setRequestFocusEnabled(false);
        pnlConfirmacionAnulacion.add(fndFondo3);
        fndFondo3.setBounds(0, 0, 1280, 800);

        pnlPrincipal.add(pnlConfirmacionAnulacion, "pnlConfirmacionAnulacion");

        getContentPane().add(pnlPrincipal);
        pnlPrincipal.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1282, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnAnteriorMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAnteriorMouseReleased
        anterior();
    }//GEN-LAST:event_btnAnteriorMouseReleased

    private void jCerrarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCerrarMouseClicked
        cerrarMsjDatafono();
        if (timer != null) {
            timer.stop();
        }
    }//GEN-LAST:event_jCerrarMouseClicked

    private void jtotal_ventaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtotal_ventaMouseReleased
        cargarTotal();
    }//GEN-LAST:event_jtotal_ventaMouseReleased

    private void btnAnularMediosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAnularMediosMouseClicked
        if (btnAnularMedios.getIcon() != botonBloqueado && btnAnularMedios.isEnabled()) {
            confirmacionParaRealizarAnulacion();
        }
    }//GEN-LAST:event_btnAnularMediosMouseClicked

    private void jSalirConfirmacionMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSalirConfirmacionMouseReleased
        mostrarMenuPrincipal();
    }//GEN-LAST:event_jSalirConfirmacionMouseReleased

    private void btnSalirMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSalirMouseReleased
        mostrarMenuPrincipal();
    }//GEN-LAST:event_btnSalirMouseReleased

    private void btnContinuarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnContinuarMouseReleased
        if (asignarCliente) {
            if (cerrar != null) {
                VentasHistorialView.setEstadoActulizarDatafono(true);
                cerrar.run();
            }
        } else {
            VentasHistorialView.setEstadoActulizarDatafono(false);
            cerrar();
        }
    }//GEN-LAST:event_btnContinuarMouseReleased

    private void jCerrarNotificacionAnulacionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCerrar1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jCerrar1MouseClicked

    private void btnSiMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSiMouseReleased
        if (aceptar != null) {
            aceptar.run();
            aceptar = null;
        }
    }//GEN-LAST:event_btnSiMouseReleased

    private void btnNoMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnNoMouseReleased
        if (noAceptar != null) {
            noAceptar.run();
            noAceptar = null;
        }
    }//GEN-LAST:event_btnNoMouseReleased

    private void jmedioCantidadKeyTyped(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jmedioCantidadKeyTyped
        String caracteresAceptados = "[0-9.]";
        NovusUtils.limitarCarateres(evt, jmedioCantidad, 9, jMensajes, caracteresAceptados);
    }// GEN-LAST:event_jmedioCantidadKeyTyped

    private void jmedioVoucherKeyTyped(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jmedioVoucherKeyTyped
        String caracteresAceptados = "[0-9]";
        NovusUtils.limitarCarateres(evt, jmedioVoucher, 10, jMensajes, caracteresAceptados);
    }// GEN-LAST:event_jmedioVoucherKeyTyped

    private void jmedioCantidadFocusGained(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_jmedioCantidadFocusGained
        NovusUtils.deshabilitarCopiarPegar(jmedioCantidad);
    }// GEN-LAST:event_jmedioCantidadFocusGained

    private void jmedioVoucherFocusGained(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_jmedioVoucherFocusGained
        NovusUtils.deshabilitarCopiarPegar(jmedioVoucher);
    }// GEN-LAST:event_jmedioVoucherFocusGained

    private void jguardarVentaMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jguardarVentaMouseReleased

        if (consumoPropio == 20003) {
            isConsumoPropio = true;
            AutorizacionView auto = new AutorizacionView((InfoViewController) parent, true, this, isConsumoPropio, this,
                    true);
            auto.setVisible(true);
            dispose();
        } else {
            isConsumoPropio = false;
            try {

                if (validarEsPagoDatafono()) {//VALIDAR SI SE SELECCIONA DATAFONO ENTRE LOS MEDIOS DE PAGO

                    if (!hayTransaccionDatafono()) {//VALIDA QUE NO HAY TRANSACCIONES PENDIENTES
                        if (isVisibleDatafonos()) {//SI HAY DATAFONOS
                            if (datafonos.size() == 1) {// SI HAY SOLO 1 DATAFONO
                             
                                if (!new HasOngoingDatafonSalesUseCase(obtenerCodigoTerminal(datafonos)).execute()) {// VALIDA SI EL UNICO DATAFONO ESTA EN MEDIO DE UNA TRANSACCION
                                    if (asignarCliente) { //VALIDACION SI ASIGNA CLIENTE EN EL PROCESO DE F.E
                                        NovusUtils.printLn("TRANSACCION DATAFONO");
                                        System.out.println(Main.ANSI_RED + "Es por este Medio: " + Main.ANSI_RESET);
                                        JsonObject datafonosInfo = new JsonObject();
                                        datafonosInfo.add("Datafonos", datafonos);
                                        AsignacionClienteBean.agregarInformacionCliente("Datafonos", datafonosInfo);
                                        vistaAsignarCliente();
                                    } else {
                                        Runnable enviarPagoDatafono = () -> enviarPagoDatafono();
                                        enviarPagos(enviarPagoDatafono);
                                    }
                                } else {
                                    showMessage("EXISTE UNA TRANSACCIÓN EN CURSO EN ESTE DATÁFONO",
                                            "/com/firefuel/resources/btBad.png",
                                            true,
                                            this::mostrarMenuPrincipal,
                                            true,
                                            LetterCase.FIRST_UPPER_CASE);
                                }
                            } else {
                                if (asignarCliente) {
                                    NovusUtils.printLn("****************************");
                                    NovusUtils.printLn("Asignar Cliente varios Datafonos");
                                    NovusUtils.printLn("****************************");
                                    sendMedioPago();
                                }
                                enviarPagoDatafonos();
                            }
                        }
                    } else {
                        showMessage("YA EXISTE UNA TRANSACION EN CURSO",
                                "/com/firefuel/resources/btBad.png",
                                true,
                                this::mostrarMenuPrincipal,
                                true,
                                LetterCase.FIRST_UPPER_CASE);
                    }

                } else {
                    /*VALIDA APP TERPEL*/
                    NovusUtils.printLn("VALIDA APP TERPEL");
                    if (validaresAppterpel()) {
                        boolean intentoDisponible = SingletonMedioPago.ConetextDependecy.getEvaluarIntentosPagoAppterpel().execute(this.movimiento.getId());
                        NovusUtils.printLn("ES APPTERPEL ");
                        if (intentoDisponible) {
                            NovusUtils.printLn("PERMITE INTENTAR PAGAR APPTERPEL ");

                            senderPayment.execute((DefaultTableModel) jtableMediosPago.getModel(), movimiento);
                            ventaGuardada();
                        } else {
                            showMessage(TransaccionMessageView.NOTIFICACION_APPTERPEL_NEGADO,
                                    "/com/firefuel/resources/btBad.png",
                                    true, this::mostrarMenuPrincipal,
                                    true,
                                    LetterCase.FIRST_UPPER_CASE);
                        }

                    } else {
                        ventaGuardada();
                    }

                }
            } catch (Exception ex) {
                Logger.getLogger(MedioPagosConfirmarViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }// GEN-LAST:event_jguardarVentaMouseReleased

    private void jeliminar_medio1MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jeliminar_medio1MouseReleased
        removeMediosPago();
        changeLabelContinuar();
    }// GEN-LAST:event_jeliminar_medio1MouseReleased

    private void jeliminar_medioMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jeliminar_medioMouseReleased
        if (jeliminar_medio.getIcon() != botonBloqueado) {
            removeMedio(false);
        }
    }// GEN-LAST:event_jeliminar_medioMouseReleased

    private void jbutton_addMedioMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jbutton_addMedioMouseReleased
        if (jbutton_addMedio.isEnabled()) {
            anadirMedioPago();
            validarEsPagoDatafono();
        }

    }// GEN-LAST:event_jbutton_addMedioMouseReleased

    private void jtableMediosPagoMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jtableMediosPagoMouseReleased
        seleccionar();
    }// GEN-LAST:event_jtableMediosPagoMouseReleased

    private void jmedioPagoComboActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jmedioPagoComboActionPerformed
        changeMedio();
    }// GEN-LAST:event_jmedioPagoComboActionPerformed

    private void jguardarVentaMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jguardarVentaMousePressed
        // TODO add your handling code here:
    }// GEN-LAST:event_jguardarVentaMousePressed

    private void jbutton_addMedioMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jbutton_addMedioMousePressed
        // TODO add your handling code here:
    }// GEN-LAST:event_jbutton_addMedioMousePressed

    private void cargarTotal() {
        String valor = jtotal_venta.getText();
        String total = valor.replaceAll("[^\\d]+", "");
        jmedioCantidad.setText(total);
    }

    private void jLabel11MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel11MouseReleased
        if (MedioPagosConfirmarViewController.bonosValidados && !asignarCliente) {
            showPanelMensaje("pnlConfirmacion");
        } else {
            if (asignarCliente) {
                if (cerrar != null) {
                    VentasHistorialView.setEstadoActulizarDatafono(true);
                    cerrar.run();
                }
            } else {
                VentasHistorialView.setEstadoActulizarDatafono(false);
                cerrar();
            }
        }
    }

    public void procesoEnvioMediosPago() {
        NovusUtils.printLn("****************************");
        NovusUtils.printLn("procesoEnvioMediosPago");
        NovusUtils.printLn("****************************");
        sendMedioPago();
        if (datafonos.size() == 1) {
            Runnable enviarPagoDatafono = () -> {
                enviarPagoDatafono();
            };
            enviarPagos(enviarPagoDatafono);
        } else {
            enviarPagoDatafonos();
        }
    }

    private void procesarBonovalido(JsonObject bonoinfo) {
        Long temp = 0L;
        if (bonoinfo != null) {
            String total = bonoinfo.get("valor").getAsString();
            boolean isBonoTerpel = false;
            for (MediosPagosBean medio : this.mediosPagoVenta) {
                if (medio.isPagosExternoValidado()) {
                    isBonoTerpel = true;
                    break;
                }
            }
            this.dataViveTerpel = bonoinfo;
            jmedioCantidad.setText(total);
            jmedioVoucher.setText(bonoinfo.get("bono").getAsString());
            mostrarVenta(PANEL_MEDIOS_PAGOS, temp, 0L);
            procesarMedioPagoAgregado();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel btnAnterior;
    private javax.swing.JLabel btnAnularMedios;
    private javax.swing.JLabel btnContinuar;
    private javax.swing.JLabel btnNo;
    private javax.swing.JLabel btnSalir;
    private javax.swing.JLabel btnSi;
    private javax.swing.JLabel fndFondo;
    private javax.swing.JLabel fndFondo1;
    private javax.swing.JLabel fndFondo2;
    private javax.swing.JLabel fndFondo3;
    private javax.swing.JLabel fondoMedio;
    public static javax.swing.JLabel jCerrar;
    private javax.swing.JLabel jIcono;
    private javax.swing.JLabel jIconoNotificacionAnulacion;
    private javax.swing.JLabel jInfoDatafono;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    public javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jMensajes;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel jSalirConfirmacion;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jTitulo;
    private javax.swing.JLabel jTituloNotificaionAnulacion;
    private javax.swing.JLabel jTransacciones;
    private javax.swing.JLabel jalerta;
    private javax.swing.JLabel jbutton_addMedio;
    private javax.swing.JLabel jeliminar_medio;
    private javax.swing.JLabel jeliminar_medio1;
    private javax.swing.JLabel jguardarVenta;
    private javax.swing.JTextField jmedioCantidad;
    private javax.swing.JComboBox<String> jmedioPagoCombo;
    private javax.swing.JTextField jmedioVoucher;
    private javax.swing.JLabel jrecibido_venta;
    private javax.swing.JLabel jsubtitulo;
    private javax.swing.JTable jtableMediosPago;
    private javax.swing.JLabel jtitulo;
    private javax.swing.JLabel jtituloconfirmacionAnulacion;
    private javax.swing.JLabel jtotal_venta;
    private javax.swing.JPanel mensajeAnulacionDatafono;
    private javax.swing.JPanel panel_teclado_medios;
    private javax.swing.JPanel pn_contenido_medio;
    private javax.swing.JPanel pnlConfirmacion;
    private javax.swing.JPanel pnlConfirmacionAnulacion;
    private javax.swing.JPanel pnlPrincipal;
    private javax.swing.JPanel pnl_cambio_medio;
    private javax.swing.JPanel pnl_principal;
    // End of variables declaration//GEN-END:variables

    private void cerrar() {
        if (runnable != null) {
            runnable.run();
        }
        if (recargar != null) {
            recargar.run();
        }
        if (timer != null) {
            timer.stop();
        }
        dispose();
        mediosPagoVenta.clear();
        anulacion = Boolean.FALSE;
        datafonosView = null;
        mediosPagoVenta.clear();
    }

    private void cerrarMsjDatafono() {
        if (!MedioPagosConfirmarViewController.bonosValidados) {
            if (runnable != null) {
                runnable.run();
            }
            if (recargar != null) {
                recargar.run();
            }
            if (timer != null) {
                timer.stop();
            }
            dispose();
            datafonosView = null;
        } else {
            mostrarMenuPrincipal();
        }
    }

    /**
     * 🔄 MIGRADO A SERVICIO PYTHON
     * Impresión de venta desde confirmación de medios de pago
     * @version 2.0 - Migrado a Python
     */
    void impresionVenta() {
        // 🚀 MIGRACIÓN A SERVICIO PYTHON - INICIO
        NovusUtils.printLn("╔════════════════════════════════════════════════════════════╗");
        NovusUtils.printLn("║  🐍 SERVICIO PYTHON - CONFIRMACIÓN MEDIOS PAGO          ║");
        NovusUtils.printLn("╚════════════════════════════════════════════════════════════╝");
        NovusUtils.printLn("📋 Imprimiendo venta desde confirmación de medios de pago");
        NovusUtils.printLn("   - ID Movimiento: " + this.movimiento.getId());
        NovusUtils.printLn("   - Facturación POS: " + facturacionPOS);

        String route = facturacionPOS ? "factura" : "venta";
        TreeMap<String, String> header = new TreeMap<>();
        header.put("Content-type", "application/json");

        String funcion = "IMPRIMIR VENTAS";
        JsonObject json = new JsonObject();
        json.addProperty("flow_type", "PRINT_SALES");
        json.addProperty("movement_id", this.movimiento.getId());
        json.addProperty("report_type", route.toUpperCase());
        JsonObject bodyJson = new JsonObject();
        json.add("body", bodyJson);

        String url = NovusConstante.SECURE_CENTRAL_POINT_PRINT_TICKET_SALES;
        String method = NovusConstante.POST;
        boolean isArray = false;
        boolean isDebug = true;
        ClientWSAsync client = new ClientWSAsync(funcion, url, method, json, isDebug, isArray, header);

        try {
            client.start();
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
            showMessage("ERROR IMPRESION VENTA",
                    "/com/firefuel/resources/btBad.png",
                    true, this::mostrarMenuPrincipal, true,
                    LetterCase.FIRST_UPPER_CASE);
        }

    }

    public boolean existeBono(JsonArray bonos, long bonoVoucher) {
        boolean existe = false;
        if (bonos != null) {
            for (JsonElement bono : bonos) {
                JsonObject bonosVenta = bono.getAsJsonObject();
                if (bonoVoucher == bonosVenta.get("AFP").getAsLong()) {
                    existe = true;
                }
            }
        }
        return existe;
    }

    public void enviarPagos(Runnable runnable) {
        boolean isBonoTerpel = false;
        JsonArray bonosArray = new JsonArray();
        JsonArray bonosVenta = mdao.getBonosVenta(this.movimiento.getId());
        for (MediosPagosBean medio : this.mediosPagoVenta) {
            if (medio.isPagosExternoValidado()) {
                isBonoTerpel = true;
                break;
            }
        }
        if (isBonoTerpel && !MedioPagosConfirmarViewController.bonosValidados) {
            showMessage("REDIMIENDO BONOS, POR FAVOR ESPERE....", "/com/firefuel/resources/loader_fac.gif",
                    false, null, false, LetterCase.FIRST_UPPER_CASE);
            for (MediosPagosBean medio : this.mediosPagoVenta) {
                if (medio.isPagosExternoValidado()) {
                    long salesForceIdMp = sdao.getCodigoSalesForceMP(medio.getId());
                    for (BonoViveTerpel bono : medio.getBonosViveTerpel()) {
                        if (!existeBono(bonosVenta, Long.parseLong(bono.getVoucher()))) {
                            JsonObject bonoViveTerpelMP = new JsonObject();
                            bonoViveTerpelMP.addProperty("IFP", salesForceIdMp);
                            bonoViveTerpelMP.addProperty("VFP", bono.getValor());
                            bonoViveTerpelMP.addProperty("AFP", bono.getVoucher());
                            bonosArray.add(bonoViveTerpelMP);
                        }
                    }
                }
            }

          this.timeOutsManager.setTimeoutUtilManager(1, () -> {
            JsonObject respuestaReclamacion = validacionBonos.ReclamacionBonoViveTerpel(reciboRec, mediosPagoVenta, this.movimiento.getId(), bonosArray);
            JsonObject respuesta = validacionBonos.procesamientRespuestaReclamacion(respuestaReclamacion, this.movimiento.getId(), bonosArray);
            procesarRespuestaBonos(respuesta);
          });

        } else {
            runnable.run();
            runnable = null;
        }
    }

    public void procesarRespuestaBonos(JsonObject respuesta) {
        String mensaje = respuesta.get("mensaje").getAsString();
        if (respuesta.get("aprobado").getAsBoolean()) {
            showMessage(mensaje, "/com/firefuel/resources/btOk.png",
                    true, this::enviarPagoDatafono, true,
                    LetterCase.FIRST_UPPER_CASE);
        } else {
            showMessage(mensaje, "/com/firefuel/resources/btBad.png",
                    true, this::mostrarMenuPrincipal, true,
                    LetterCase.FIRST_UPPER_CASE);
        }
    }

    public void ventaGuardada() {
        try {
            NovusUtils.setMensaje("", jMensajes);

            NovusUtils.beep();
            boolean isBonoTerpel = false;
            boolean isRedencionPuntos = false;
            TreeMap<Long, MediosPagosBean> mediosMovimiento = new TreeMap<>();
            for (MediosPagosBean medio : this.mediosPagoVenta) {
                mediosMovimiento.put(medio.getId(), medio);
            }

            this.movimiento.setMediosPagos(mediosMovimiento);
            if (!isFac) {
                if (asignarCliente) {
                    NovusUtils.printLn("Asignar Cliente ventaGuardada()");
                    vistaAsignarCliente();
                } else {
                    if (!this.isNewVenta) {
                        boolean response;
                        boolean existAppTerpel = false;
                        for (MediosPagosBean medio : this.mediosPagoVenta) {
                            existAppTerpel = medio.getDescripcion().equals(MediosPagosDescription.APPTERPEL);
                            if (medio.isPagosExternoValidado()) {
                                isBonoTerpel = true;
                            } else if (medio.isRedencionMillas()) {
                                isRedencionPuntos = true;
                            }
                        }
                        NovusUtils.printLn("-->IsRedencionPuntos: " + isRedencionPuntos);
                        ValidateIsAppTerpelPaymentSuccess validateIsAppTerpelPaymentProcessed = SingletonMedioPago.ConetextDependecy.getValidateIsAppTerpelPaymentProcessed();
                        DefaultTableModel dm = (DefaultTableModel) jtableMediosPago.getModel();

                        boolean imprentIfAppTerpelSucces = validateIsAppTerpelPaymentProcessed.execute(this.movimiento.getId());

                        if (isBonoTerpel && this.reciboRec != null && !bonosValidados) {
                            ReclamacionBonoViveTerpel();
                        } else {
                            System.out.println("*****************************************************");
                            System.out.println("Enviando Medios de Pago ventaGuardada...................");
                            System.out.println("*****************************************************");
                            response = sendMedioPago();
                            if (response) {

                                if (existAppTerpel) {
                                    ParametrosMensajes parametrosMensajes = new ParametrosMensajesBuilder().setMsj(TransaccionMessageView.NOTIFICIACION_APPTERPEL)
                                            .setRuta("/com/firefuel/resources/btOk.png")
                                            .setHabilitar(true)
                                            .setRunnable(this::cerrar)
                                            .setAutoclose(true)
                                            .build();
                                    showMessageAppterpel(parametrosMensajes);
                                    if (imprentIfAppTerpelSucces) {
                                        impresionVenta();
                                    }
                                } else {
                                    showMessage("MEDIO DE PAGO EDITADO EXITOSAMENTE", "/com/firefuel/resources/btOk.png",
                                            true, this::cerrar, true,
                                            LetterCase.FIRST_UPPER_CASE);
                                    impresionVenta();
                                }

                            } else {
                                showMessage("NO SE PUEDE EDITAR MEDIO PAGO", "/com/firefuel/resources/btBad.png",
                                        true, this::mostrarMenuPrincipal, true,
                                        LetterCase.FIRST_UPPER_CASE);
                            }
                        }
                    } else {
                        MovimientosBean movimientoRealizado;
                        reciboBean.setPlaca("");
                        reciboBean.setEmpresa(this.movimiento.getEmpresa().getRazonSocial());
                        reciboBean.setDireccion(this.movimiento.getEmpresa().getDireccionPrincipal());
                        reciboBean.setTelefono(this.movimiento.getEmpresa().getTelefonoPrincipal());


                        boolean isCombustible = false;
                        if (this.movimiento.getDetalles() != null) {
                            for (Map.Entry<Long, MovimientosDetallesBean> entry : this.movimiento.getDetalles()
                                    .entrySet()) {
                                MovimientosDetallesBean value = entry.getValue();
                                if (value.getTipo() == NovusConstante.PRODUCTOS_TIPOS_COMBUSTIBLE) {
                                    isCombustible = true;
                                    break;
                                }
                            }
                        }

                        movimientoRealizado = this.movimiento;
                        boolean status = movimientoRealizado != null;

                        if (status) {
                            movimientoRealizado.setMovmientoEstado("A");
                            reciboBean.setMovimiento(movimientoRealizado);
                            procesarStatus(reciboBean, pedidoFacade, isCombustible);
                        } else if (isCombustible) {
                            procesarStatus(reciboBean, pedidoFacade, isCombustible);
                        } else {
                            showMessage("HA OCURRIDO UN ERROR AL REALIZAR VENTA",
                                    "/com/firefuel/resources/btBad.png",
                                    true, this::mostrarMenuPrincipal,
                                    true, LetterCase.FIRST_UPPER_CASE);
                        }
                    }
                    if (this.isNewVenta) {
                        volver();
                    }
                }
            } else {
                boolean inserted = mdao.crearMediosPagosFacturaElectronica(this.movimiento);
                if (inserted) {
                    showMessage("MEDIO DE PAGO EDITADO EXITOSAMENTE",
                            "/com/firefuel/resources/btOk.png",
                            true, this::cerrar,
                            true, LetterCase.FIRST_UPPER_CASE);
                    cerrar();
                } else {
                    showMessage("HA OCURRIDO UN ERROR AL REALIZAR EL CAMBIO DE MEDIO DE PAGO",
                            "/com/firefuel/resources/btBad.png",
                            true, this::mostrarMenuPrincipal,
                            true, LetterCase.FIRST_UPPER_CASE);
                }
            }
            if (!isBonoTerpel) {
                jMensajes.setVisible(true);
            }
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
            Logger
                    .getLogger(MedioPagosConfirmarViewController.class
                            .getName()).log(Level.SEVERE, null, e);
        }
    }

    public void procesarStatus(Recibo rec, PedidoFacade pedidoF, boolean isCombustible) {
        try {
            if (movimiento.getOperacionId() == NovusConstante.MOVIMIENTO_TIPO_COMPLEMENTARIO) {
                pedidoF.sendVenta(this.movimiento, false);
            } else {
                pedidoF.sendVentaKIOSCO(this.movimiento, true, false);
            }
            showMessage("VENTA REALIZADA EXITOSAMENTE",
                    "/com/firefuel/resources/btOk.png",
                    true, this::volver,
                    true, LetterCase.FIRST_UPPER_CASE);
            StoreViewController.isCombustible = false;
            KCOViewController.isCombustible = false;
            if (!isCombustible) {
                if (movimiento.getOperacionId() == NovusConstante.MOVIMIENTO_TIPO_COMPLEMENTARIO) {
                    printerFacade.printRecibo(rec);
                } else {
                    printerFacade.printReciboKIOSCO(null, rec);
                }
            }
        } catch (DAOException e) {
            NovusUtils.printLn("ERROR: " + e.getMessage());
        }

    }

    private void volver() {
        this.parent.dispose();
        cerrar();
    }

    public void loadMediosPagosTable() {
        DefaultTableModel dm = (DefaultTableModel) jtableMediosPago.getModel();
        dm.getDataVector().removeAllElements();
        dm.fireTableDataChanged();
        DefaultTableModel defaultModel = (DefaultTableModel) jtableMediosPago.getModel();
        for (MediosPagosBean medio : this.mediosPagoVenta) {
            boolean hayAunilacionEnCurso = datafonosDao.hayAnulacionEncurso(this.movimiento.getId(), medio.getId(), medio.getVoucher());
            String descripcionMedioPago = hayAunilacionEnCurso ? "ANULANDO MEDIO" : medio.getDescripcion().toUpperCase();
            defaultModel.addRow(new Object[]{descripcionMedioPago, medio.getVoucher(),
                    "$ " + df.format(medio.getRecibido())});
        }
        this.changeMedio();
    }

    private void seleccionar() {
        int r = jtableMediosPago.getSelectedRow();
        DefaultTableModel dm = (DefaultTableModel) jtableMediosPago.getModel();
        selectedRow = r;
        if (r > -1) {
            NovusUtils.beep();
            boolean isAppTerpel = dm.getValueAt(r, 0).equals("APP TERPEL");
            if (!dm.getValueAt(r, 0).equals("EFECTIVO")) {
                if (!getIsGoPassMovimientoUseCase.execute() && !isAppTerpel) {
                    jeliminar_medio.setIcon(ImageCache.getImage("/com/firefuel/resources/botones/bt-danger-small.png"));
                } else if (isAppTerpel) {
                    ValidateIfDeleteAppTerpelPayment validatePaymentStatus = SingletonMedioPago.ConetextDependecy.getValidateIfDeleteAppTerpelPayment();
                    boolean bloquear = validatePaymentStatus.execute(this.movimiento.getId());
                    if (!bloquear) {
                        jeliminar_medio.setIcon(botonBloqueado);
                    } else {
                        jeliminar_medio.setIcon(ImageCache.getImage("/com/firefuel/resources/botones/bt-danger-small.png"));
                    }
                } else {
                    jeliminar_medio.setIcon(botonBloqueado);
                }
            } else {
                jeliminar_medio.setIcon(botonBloqueado);
            }
            ArrayList<MediosPagosBean> mediosValidar = new ArrayList<>();
            mediosValidar.add(this.mediosPagoVenta.get(selectedRow));
            if (validarMediosConDatafono(mediosValidar, Boolean.FALSE)) {
                    btnAnularMedios.setIcon(ImageCache.getImage("/com/firefuel/resources/botones/bt-danger-small.png"));

            } else {
                btnAnularMedios.setIcon(botonBloqueado);
            }
        }
    }

    private void loadEditMedio(int index) {
        MediosPagosBean medio = this.mediosPagoVenta.get(index);
        jmedioPagoCombo.setSelectedItem(medio.getDescripcion().toUpperCase());
        jmedioCantidad.setText(df.format(medio.getRecibido()));
        jmedioVoucher.setEnabled(true);
        if (this.mediosPagoVenta.get(index).isComprobante()) {
            jmedioVoucher.setText(medio.getVoucher());
            if (medio.isPagosExternoValidado()) {
                jmedioCantidad.setText("");
                jmedioVoucher.setText("");
            } else {
                jmedioVoucher.setEnabled(true);
            }

        }
    }

    private void loadMediosPagosImagen() {
        try {
            SingletonMedioPago.ConetextDependecy.getRecoverMedio().loadMedioPago();
            List<MedioPagoImageBean> medios = SingletonMedioPago.ConetextDependecy.getMedioPagoWithoutGoPass().execute();
            mediosPagosDisponibles = new ArrayList<>();
            for (MediosPagosBean medio : medios) {
                mediosPagosDisponibles.add(medio);
                NovusUtils.printLn(medio.getDescripcion());
            }
        } catch (SQLException ex) {
            Logger.getLogger(MedioPagosConfirmarViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadMediosPagos() {

        loadMediosPagosImagen();
        if (mediosPagosDisponibles != null) {
            boolean isGLP = esVentaGLP();
            ArrayList<MediosPagosBean> mediosFiltrados = new ArrayList<>();
            
            for (MediosPagosBean medio : mediosPagosDisponibles) {
                // 🔍 VALIDACIÓN GLP: Ocultar APP TERPEL y GoPass para ventas GLP
                boolean esDeshabilitadoPorGLP = isGLP && (medio.getDescripcion().equals(MediosPagosDescription.APPTERPEL) || medio.getId() == NovusConstante.ID_MEDIO_GOPASS);
                
                if (esDeshabilitadoPorGLP) {
                    NovusUtils.printLn("🚫 [GLP] Medio oculto para GLP: " + medio.getDescripcion() + " (ID=" + medio.getId() + ")");
                    // No agregar al combo ni a la lista filtrada - simplemente omitir
                    continue;
                } else if (!isMedioEspecial(medio) || medio.getId() == 20004) {
                    NovusUtils.printLn("✅ Agregado al combo: " + medio.getDescripcion());
                    jmedioPagoCombo.addItem(medio.getDescripcion().toUpperCase());
                    mediosFiltrados.add(medio);
                } else {
                    NovusUtils.printLn("❌ Filtrado (medio especial): " + medio.getDescripcion() + " (ID=" + medio.getId() + ")");
                }
            }
            
            // Usar la lista filtrada para que los índices coincidan con el ComboBox
            mediosPagosDisponibles = mediosFiltrados;
        }

        if (!this.isNewVenta && !isFac) {
            mediosPagoVenta.clear();
            JsonArray data = new JsonArray();
            if (!AsignacionClienteBean.getDatosCliente().isEmpty() && AsignacionClienteBean.getDatosCliente().containsKey("MediosPago")) {
                NovusUtils.printLn("los datos de medios de pagos viene por ");
                data = AsignacionClienteBean.getDatosCliente().get("MediosPago").get("mediosDePagos").getAsJsonArray();
                NovusUtils.printLn("contenido de data -> " + data);
            } else {
                data = mdao.obtenerMediosPagoVenta(this.movimiento.getId());
            }
            llenarMediosDePagos(data);
        }
        this.resetFields();
        this.loadMediosPagosTable();
        this.changeMedio();
        this.validateCalcule(0);
    }

    private void llenarMediosDePagos(JsonArray data) {
        NovusUtils.printLn("🔄 Iniciando mapeo de medios de pago desde JSON...");
        int index = 0;

        for (JsonElement arr : data) {
            JsonObject json = arr.getAsJsonObject();

            NovusUtils.printLn("📦 Medio[" + index + "] JSON recibido: " + json.toString());
            MediosPagosBean medioPagoBean = new MediosPagosBean();

            // 🔐 ID que se usará para código Salesforce (id_medio_pago)
            if (json.has("id_medio_pago") && !json.get("id_medio_pago").isJsonNull()) {
                long idSalesforce = json.get("id_medio_pago").getAsLong();
                medioPagoBean.setId(idSalesforce);
                NovusUtils.printLn("✅ id_medio_pago encontrado: " + idSalesforce);
            } else {
                NovusUtils.printLn("⚠️ id_medio_pago no encontrado en JSON, se usará ct_medios_pagos_id");
                medioPagoBean.setId(json.get("ct_medios_pagos_id").getAsLong());
            }

            // ℹ️ Valores estándar
            medioPagoBean.setIdMedioPago(json.get("ct_medios_pagos_id").getAsLong());
            medioPagoBean.setDescripcion(json.get("descripcion").getAsString().toUpperCase());
            medioPagoBean.setRecibido(json.get("valor_recibido").getAsFloat());
            medioPagoBean.setCambio(json.get("valor_cambio").getAsFloat());
            medioPagoBean.setValor(json.get("valor_total").getAsFloat());
            medioPagoBean.setVoucher(json.get("numero_comprobante").getAsString());
            medioPagoBean.setNumeroRecibo(json.get("numero_recibo").isJsonNull() ? "" : json.get("numero_recibo").getAsString());
            medioPagoBean.setIdTransaccion(json.get("id_transaccion").isJsonNull() ? 0L : json.get("id_transaccion").getAsLong());
            medioPagoBean.setFranquicia(json.get("franquicia").isJsonNull() ? "" : json.get("franquicia").getAsString());
            medioPagoBean.setIdAdquiriente(json.get("id_adquiriente").isJsonNull() ? 0L : json.get("id_adquiriente").getAsLong());
            medioPagoBean.setTipoDeCuenta(json.get("tipo_cuenta").isJsonNull() ? "" : json.get("tipo_cuenta").getAsString());
            medioPagoBean.setBin(json.get("bin").isJsonNull() ? "" : json.get("bin").getAsString());
            medioPagoBean.setCodigoDian(json.get("codigo_dian").isJsonNull() ? 0 : json.get("codigo_dian").getAsInt());
            medioPagoBean.setTrm(json.get("trm").isJsonNull() ? 0 : json.get("trm").getAsFloat());

            // 🎯 Bono especial
            if (json.get("ct_medios_pagos_id").getAsLong() == 20000) {
                medioPagoBean.setConfirmacionBono(true);
                NovusUtils.printLn("🎟️ Bono Vive Terpel detectado");
            } else {
                medioPagoBean.setConfirmacionBono(false);
            }

            // 🧾 Resultado del mapeo
            // 💬 Aquí va el log que te mencioné:
            NovusUtils.printLn("📋 Medio cargado -> " + medioPagoBean.getDescripcion()
                    + " | Externo: " + medioPagoBean.isIsPagoExterno()
                    + " | Validado: " + medioPagoBean.isPagosExternoValidado());
            mediosPagoVenta.add(medioPagoBean);
            index++;
        }

        NovusUtils.printLn("✅ Total de medios cargados: " + mediosPagoVenta.size());
    }


    private boolean isMedioEspecial(MediosPagosBean medio) {
        return medio.getId() >= 20001;
    }


    private void mostrarVenta(String ventana, Long valorRecibido, Long totalVenta) {
        NovusUtils.setMensaje("", jMensajes);
        if (ventana.equals(PANEL_MEDIOS_PAGOS)) {
            jLabel3.setText("CONFIRMACION DE VENTA");
            jguardarVenta.setVisible(true);
        }

        Notificador notify = (JsonObject data) -> {
            procesarBonovalido(data);
        };
        if (ventana.equals(PANEL_VALIDACION_BONO)) {
            if (!jmedioCantidad.isEnabled()) {
                valorRecibido = 0L;
            }
            FidelizacionValidacionVoucher voucher = new FidelizacionValidacionVoucher((InfoViewController) this.parent,
                    true, false, (int) valorRecibido.intValue(), notify, this.mediosPagoVenta, totalVenta);
            voucher.setVisible(true);
        } else if (ventana.equals(PANEL_REDENCION_VIVE_TERPEL)) {
            FidelizacionRedencion redencionView = new FidelizacionRedencion((InfoViewController) this.parent, true,
                    (int) valorRecibido.intValue(), notify);
            redencionView.setVisible(true);
        }
    }

    private void procesarNotificacionVive(JsonObject data) {
        NovusUtils.printLn(data.toString());
    }

    private void abrirVentanaMedioViveTerpel() {
        try {
            Long TotalRecibido = 0L;
            try {
                TotalRecibido = (long) ((Float.parseFloat(jmedioCantidad.getText().trim().replace(".", "").replace(",", "")) * 100) / 100.0);
            } catch (NumberFormatException e) {
            }

            if (!jmedioCantidad.getText().trim().equals("") || TotalRecibido > 0) {
                mostrarVenta(PANEL_VALIDACION_BONO, TotalRecibido, totalMediosEfectivoTabla());
            } else {
                NovusUtils.setMensaje("CANTIDAD DEBE SER MAYOR A 0", jMensajes);
                jguardarVenta.setVisible(false);

            }
        } catch (NumberFormatException | SecurityException e) {
            Logger.getLogger(MedioPagosConfirmarViewController.class
                    .getName()).log(Level.SEVERE, null, e);
        }
    }

    private void abrirVentanaRedencionViveTerpel() {
        try {
            Long TotalRecibido = 0L;
            try {
                TotalRecibido = (long) ((Float.parseFloat(jmedioCantidad.getText().trim().replace(".", "").replace(",", "")) * 100) / 100.0);

            } catch (NumberFormatException e) {
                Logger.getLogger(MedioPagosConfirmarViewController.class
                        .getName()).log(Level.SEVERE, null, e);
            }

            if (!jmedioCantidad.getText().trim().equals("") || TotalRecibido > 0) {
                mostrarVenta(PANEL_REDENCION_VIVE_TERPEL, TotalRecibido, totalMediosEfectivoTabla());
            } else {
                NovusUtils.setMensaje("CANTIDAD DEBE SER MAYOR A 0", jMensajes);
                jguardarVenta.setVisible(false);

            }
        } catch (NumberFormatException | SecurityException e) {
            Logger.getLogger(MedioPagosConfirmarViewController.class
                    .getName()).log(Level.SEVERE, null, e);
        }
    }

    public Map<Long, String> getMediosDatafono() {
        TreeMap<Long, String> medio = new TreeMap<>();
        medio.put(5L, "CON DATAFONO");
        return medio;
    }

    public boolean isDatafono(MediosPagosBean medioSeleccionado) {
        return medioSeleccionado.getDescripcion().equalsIgnoreCase("CON DATAFONO");
    }

    private void getDatafonosView(ArrayList<MediosPagosBean> mediosPagoVenta1, MovimientosBean movimiento1, ArrayList<MediosPagosBean> mediosPagosSindatafonos) {

        if (asignarCliente) {
            datafonosView = new DatafonosView(principal, mediosPagoVenta1, reciboRec, movimiento1, runnable, this::mostrarMenuPrincipalDatafono, this::mostrarMenuPrincipal, mediosPagosSindatafonos, true, cerrarTodo);
        } else {
            datafonosView = new DatafonosView(this, principal, mediosPagoVenta1, movimiento1, runnable, this::mostrarMenuPrincipalDatafono, this::mostrarMenuPrincipal, mediosPagosSindatafonos, reciboRec);
        }
        showDialog(datafonosView);
    }

    private void anadirMedioPago() {
        int index = jmedioPagoCombo.getSelectedIndex();

        if (mediosPagosDisponibles.get(index) != null) {
            MediosPagosBean medioSeleccinado = mediosPagosDisponibles.get(index);
            
            if (mediosPagosDisponibles.get(index).getId() == 20003) {
                consumoPropio = mediosPagosDisponibles.get(index).getId();
            }
            if (medioSeleccinado.getDescripcion().equals(MediosPagosDescription.APPTERPEL)) {
                float valorSeleccionado = Float.valueOf(jmedioCantidad.getText());
                float valorEsperado = movimiento.getVentaTotal();
                if (valorSeleccionado != valorEsperado) {
                    NovusUtils.setMensaje("DEBE ASIGNAR EL TOTAL DE LA VENTA ", jMensajes);
                    jguardarVenta.setVisible(false);
                    return;
                }
            }
            if (isBonoViveTerpel(medioSeleccinado)) {
                MediosPagosBean medio = mediosPagosDisponibles.get(index);

                medio.setPagosExternoValidado(false);

                if (cambio >= 0 && !medio.isCambio()) {
                    NovusUtils.setMensaje("CANTIDAD EXCEDE RESTANTE ( " + medio.getDescripcion().toUpperCase()
                            + " NO ADMITE CAMBIO)", jMensajes);
                    jguardarVenta.setVisible(false);
                    return;
                } else {
                    abrirVentanaMedioViveTerpel();
                    return;
                }
            } else if (isRedencionViveTerpel(medioSeleccinado)) {
                abrirVentanaRedencionViveTerpel();
                return;
            }
            if (medioSeleccinado.getDescripcion().equals(MediosPagosDescription.APPTERPEL)) {

                DefaultTableModel dm = (DefaultTableModel) jtableMediosPago.getModel();

                for (Vector vectorMedio : dm.getDataVector()) {
                    if (vectorMedio.get(0).equals(MediosPagosDescription.APPTERPEL)) {
                        NovusUtils.setMensaje(medioSeleccinado.getDescripcion().toUpperCase() + " SOLO SE PUEDE AGREGAR UNA VEZ", jMensajes);
                        jguardarVenta.setVisible(false);
                        return;
                    }
                }

            }
        }
        procesarMedioPagoAgregado();
    }

    private boolean validaresAppterpel() {
        DefaultTableModel dm = (DefaultTableModel) jtableMediosPago.getModel();
        int filas = dm.getRowCount();

        for (int i = 0; i < filas; i++) {
            if (dm.getValueAt(i, 0).equals(MediosPagosDescription.APPTERPEL)) {
                return true;
            }
        }
        return false;
    }

    private boolean validarEsPagoDatafono() {
        DefaultTableModel dm = (DefaultTableModel) jtableMediosPago.getModel();
        int filas = dm.getRowCount();
        for (int i = 0; i < filas; i++) {
            if (dm.getValueAt(i, 0).equals("CON DATAFONO")) {
                boolean datafono = mdao.validarMedio(movimiento.getId(), mediosPagoVenta.get(i).getId());
                if (asignarCliente) {
                    if (dm.getValueAt(i, 0).equals("CON DATAFONO")) {
                        jguardarVenta.setText("GUARDAR");
                    } else {
                        jguardarVenta.setText("GUARDAR Y ENVIAR");
                    }
                } else {
                    jguardarVenta.setText("GUARDAR Y ENVIAR");
                }
                return !datafono;
            } else {
                jguardarVenta.setText("GUARDAR VENTA");
            }
        }
        return false;
    }

    private boolean hayTransaccionDatafono() {
        return new BuscarTransaccionDatafonoCaseUse(movimiento.getId()).execute();
    }

    public boolean isVisibleDatafonos() {
        boolean isVisible = false;
        try {
            datafonos = edao.datafonosInfo();
            if (datafonos != null) {
                if (datafonos.size() >= 1) {
                    isVisible = true;
                }
            } else {
                showMessage("NO SE TIENEN DATAFONOS CONFIGURADOS",
                        "/com/firefuel/resources/btBad.png",
                        true, this::mostrarMenuPrincipalDatafono,
                        true, LetterCase.FIRST_UPPER_CASE);

            }
        } catch (DAOException | SQLException ex) {
            Logger.getLogger(MedioPagosConfirmarViewController.class
                            .getName())
                    .log(Level.SEVERE, null, ex);
        }
        return isVisible;
    }

    private void procesarMedioPagoAgregado() {
        System.out.println("🔄 Iniciando procesarMedioPagoAgregado");

        medioPagoBean = new MediosPagosBean();

        int index = jmedioPagoCombo.getSelectedIndex();
        long totalEfectivo = totalMediosEfectivoTabla();
        DefaultTableModel dm = (DefaultTableModel) jtableMediosPago.getModel();
        if (!jmedioCantidad.getText().trim().equals("")) {
            float totalRecibido = (float) ((Float
                    .parseFloat(jmedioCantidad.getText().trim().replace(".", "").replace(",", "")) * 100) / 100.0);
            if (totalRecibido > 0) {
                float recibidoTotalSinMedio = 0;
                float cambioSinMedio = 0;
                boolean invalido = false;

                medioPagoBean.setId(mediosPagosDisponibles.get(index).getId());
                medioPagoBean.setDescripcion(mediosPagosDisponibles.get(index).getDescripcion());
                medioPagoBean.setCambio(mediosPagosDisponibles.get(index).isCambio());
                medioPagoBean.setIsPagoExterno(mediosPagosDisponibles.get(index).isIsPagoExterno());
                medioPagoBean.setPagosExternoValidado(false);
                if (isBonoViveTerpel(mediosPagosDisponibles.get(index))) {
                    medioPagoBean.setPagosExternoValidado(true);
                } else if (isRedencionViveTerpel(mediosPagosDisponibles.get(index))) {
                    medioPagoBean.setRedencionMillas(true);
                }
                if (!this.validate(index)) {
                    int indexExistente = 0;
                    for (MediosPagosBean medios : this.mediosPagoVenta) {
                        if (medios.getId() == medioPagoBean.getId()) {
                            break;
                        }
                        indexExistente++;
                    }
                    recibidoTotalSinMedio = this.movimiento.getRecibidoTotal()
                            - this.mediosPagoVenta.get(indexExistente).getRecibido();
                    cambioSinMedio = recibidoTotalSinMedio - this.movimiento.getVentaTotal();

                    if (!hayEfectivo()) {
                        NovusUtils.printLn("Valido si hay efectivo: 1)");
                        if (totalMediosTabla() <= totalRecibido) {
                            invalido = true;
                        }
                    }
                } else {

                    if (!hayEfectivo()) {
                        NovusUtils.printLn("Valido si hay efectivo: 2)");
                        if (totalMediosTabla() <= totalRecibido) {
                            invalido = true;
                        }
                    } else {
                        if (!validarTotalMedio(totalRecibido)) {
                            invalido = true;
                        }
                    }
                }
                if (!invalido) {
                    jMensajes.setVisible(true);
                    if (this.validate(index)) {

                        System.out.println(Main.ANSI_RED + "Validacion 1" + Main.ANSI_RESET);

                        medioPagoBean.setValor(this.movimiento.getVentaTotal() - this.movimiento.getRecibidoTotal());
                        medioPagoBean.setRecibido(totalRecibido);
                        medioPagoBean.setCambio(0);
                        medioPagoBean.setCredito(mediosPagosDisponibles.get(index).isCredito());
                        medioPagoBean.setVoucher(jmedioVoucher.getText().trim());
                        medioPagoBean.setComprobante(mediosPagosDisponibles.get(index).isComprobante());

                        if (hayEfectivo()) {
                            if (actualizarEfectivo(totalRecibido, true)) {
                                if (totalMediosEfectivoTabla() == 0) {
                                    medioPagoBean.setRecibido(Math.abs(totalEfectivo));
                                }
                                mediosPagoVenta.add(medioPagoBean);
                                loadMediosPagosTable();
                            } else {
                                System.out.println("Cantidad Excede Total 1");
                                NovusUtils.setMensaje("CANTIDAD EXCEDE TOTAL", jMensajes);
                                jguardarVenta.setVisible(false);
                            }
                        }
                        selectedRow = -1;
                    } else {

                        System.out.println(Main.ANSI_RED + "Validacion 2" + Main.ANSI_RESET);

                        medioPagoBean.setValor(this.movimiento.getVentaTotal() - this.movimiento.getRecibidoTotal());
                        medioPagoBean.setRecibido(totalRecibido);
                        medioPagoBean.setCambio(0);
                        medioPagoBean.setCredito(mediosPagosDisponibles.get(index).isCredito());
                        medioPagoBean.setVoucher(jmedioVoucher.getText().trim());
                        medioPagoBean.setComprobante(mediosPagosDisponibles.get(index).isComprobante());

                        if (hayEfectivo()) {
                            if (actualizarEfectivo(totalRecibido, true)) {
                                if (totalMediosEfectivoTabla() == 0) {
                                    medioPagoBean.setRecibido(Math.abs(totalEfectivo));
                                }
                                mediosPagoVenta.add(medioPagoBean);
                                loadMediosPagosTable();
                            } else {
                                System.out.println("Cantidad Excede Total 2");
                                NovusUtils.setMensaje("CANTIDAD EXCEDE TOTAL", jMensajes);
                                jguardarVenta.setVisible(false);
                            }
                        }
                        selectedRow = -1;
                    }
                    if (medioPagoBean.isPagosExternoValidado()) {
                        ArrayList<BonoViveTerpel> bonosVenta = new ArrayList<>();
                        BonoViveTerpel nuevoBono = new BonoViveTerpel();
                        nuevoBono.setVoucher(medioPagoBean.getVoucher());
                        nuevoBono.setValor(totalRecibido);
                        bonosVenta.add(nuevoBono);
                        medioPagoBean.setBonosViveTerpel(bonosVenta);
                        medioPagoBean.setRecibido(totalRecibido);
                    }
                    this.resetFields();
                    this.changeMedio();
                    this.validateCalcule(index);
                } else {
                    NovusUtils.setMensaje("CANTIDAD EXCEDE TOTAL", jMensajes);
                    jmedioCantidad.setText("");
                    selectedRow = -1;
                    float totalVenta = totalMediosTabla();
                    if (totalVenta == movimiento.getVentaTotal()) {
                        jguardarVenta.setVisible(true);
                    } else {
                        jguardarVenta.setVisible(false);
                    }
                }
            } else if (totalRecibido == 0) {

                this.timeOutsManager.setTimeoutUtilManager(2, () -> {
                    NovusUtils.setMensaje("", jMensajes);
                });

                NovusUtils.setMensaje("CANTIDAD DEBE SER MAYOR A 0", jMensajes);
            } else {
                NovusUtils.setMensaje("CANTIDAD DEBE SER MAYOR A 0", jMensajes);
                jguardarVenta.setVisible(false);
            }
        }
        validarEsPagoDatafono();
    }

    private boolean hayEfectivo() {
        boolean valido = false;
        DefaultTableModel dm = (DefaultTableModel) jtableMediosPago.getModel();
        for (int i = 0; i < dm.getRowCount(); i++) {
            if (dm.getValueAt(i, 0).equals("EFECTIVO")) {
                valido = true;
                break;
            }
        }
        return valido;
    }

    private float totalMediosTabla() {
        float valorInicial = 0f;
        DefaultTableModel dm = (DefaultTableModel) jtableMediosPago.getModel();
        for (int i = 0; i < dm.getRowCount(); i++) {
            String valor = dm.getValueAt(i, 2).toString();
            valorInicial += Float.parseFloat(valor.replaceAll("[^\\d]+", ""));
        }
        return valorInicial;
    }

    private Long totalMediosEfectivoTabla() {
        Long valorInicial = 0L;
        DefaultTableModel dm = (DefaultTableModel) jtableMediosPago.getModel();
        for (int i = 0; i < dm.getRowCount(); i++) {
            if (dm.getValueAt(i, 0).equals("EFECTIVO")) {
                String valor = dm.getValueAt(i, 2).toString();
                valorInicial = Long.parseLong(valor.replaceAll("[^\\d]+", ""));
            }
        }
        return valorInicial;
    }

    private boolean actualizarEfectivo(float valorIngresado, boolean isAgregarMedio) {
        boolean isValido = true;
        int indexEfectivo = -1;
        DefaultTableModel dm = (DefaultTableModel) jtableMediosPago.getModel();
        for (int i = 0; i < dm.getRowCount(); i++) {
            if (dm.getValueAt(i, 0).equals("EFECTIVO")) {
                indexEfectivo = i;
                String valor = dm.getValueAt(i, 2).toString();
                String fall = valor.replaceAll("[^\\d]+", "");
                float valorInicial = Float.parseFloat(fall);
                float valorObtenido = 0f;
                if (isAgregarMedio) {
                    valorObtenido = valorInicial - valorIngresado;
                } else {
                    valorObtenido = valorInicial + valorIngresado;
                }
                String valorFinal = "$ " + df.format(valorObtenido);
                if (valorObtenido == 0) {
                    dm.removeRow(i);
                    mediosPagoVenta.remove(i);
                } else if (valorObtenido < 0) {

                    String medio = jmedioPagoCombo.getSelectedItem().toString();
                    if (medio.equalsIgnoreCase("BONO VIVE TERPEL")) {
                        dm.removeRow(indexEfectivo);
                        mediosPagoVenta.remove(indexEfectivo);
                    } else {
                        isValido = false;
                    }

                } else {
                    for (MediosPagosBean medio : this.mediosPagoVenta) {
                        if (medio.getDescripcion().equals("EFECTIVO")) {
                            medio.setRecibido(valorObtenido);
                        }
                    }
                    dm.setValueAt(valorFinal, i, 2);
                }
            }
        }
        jtableMediosPago.repaint();
        return isValido;
    }

    private boolean validarTotalMedio(float valorIngresado) {
        boolean valido = true;
        DefaultTableModel dm = (DefaultTableModel) jtableMediosPago.getModel();
        String medio = jmedioPagoCombo.getSelectedItem().toString();
        if (!medio.equalsIgnoreCase("BONO VIVE TERPEL")) {
            for (int i = 0; i < dm.getRowCount(); i++) {
                if (dm.getValueAt(i, 0).equals("EFECTIVO")) {
                    String valor = dm.getValueAt(i, 2).toString();
                    float valorInicial = Float.parseFloat(valor.replaceAll("[^\\d]+", ""));
                    float valorObtenido = valorInicial - valorIngresado;
                    valido = valorObtenido >= 0;
                }
            }
        }
        return valido;
    }

    private void validateCalcule(int indexMedio) {
        Long totalRecibido = 0L;
        final long EFECTIVOID = 1;

        if (this.mediosPagoVenta.size() != 1) {
            for (MediosPagosBean medios : this.mediosPagoVenta) {
                totalRecibido = (long) this.movimiento.getVentaTotal();
                NovusUtils.printLn("DESCRIPCION:" + medios.getDescripcion());
                NovusUtils.printLn("CAMBIO:" + medios.getCambio());
                NovusUtils.printLn("VALOR:" + medios.getValor());
                NovusUtils.printLn("RECIBIDO:" + medios.getRecibido());
                NovusUtils.printLn("----------------------");
            }
        }
        boolean cambioValido = true;
        this.movimiento.setRecibidoTotal((long) totalRecibido);
        this.cambio = (long) this.movimiento.getVentaTotal() - (long) this.movimiento.getRecibidoTotal();
        jrecibido_venta.setText("$ " + df.format((long) this.movimiento.getVentaTotal()));
        String mensaje = "";

        if (this.mediosPagoVenta.isEmpty()) {
            mensaje = "DEBE AGREGAR AL MENOS 1 MEDIO";
            cambioValido = false;
        }
        NovusUtils.setMensaje(mensaje, jMensajes);
        jguardarVenta.setVisible(cambioValido);
        jeliminar_medio.setIcon(botonBloqueado);
    }

    boolean estaMedioAsignadoAlMovimiento(int medioId) {
        for (MediosPagosBean medio : this.mediosPagoVenta) {
            if (medio.getId() == medioId) {
                return true;
            }
        }
        return false;
    }

    private boolean validate(long index) {
        boolean validate = true;
        for (MediosPagosBean medio : this.mediosPagoVenta) {
            if (medio.getId() == this.mediosPagosDisponibles.get((int) index).getId()) {
                validate = false;
                break;
            }
        }
        if (this.mediosPagosDisponibles.get((int) index).isCredito() && !this.mediosPagoVenta.isEmpty()) {
            validate = false;
        } else {
            for (MediosPagosBean medio : this.mediosPagoVenta) {
                if (medio.isCredito()) {
                    validate = false;
                    break;
                }
            }
        }
        return validate;
    }

    private void fix() {
        try {
            String text = jmedioCantidad.getText().trim();
            if (!text.equals("")) {
                float isRecibido = (float) ((Float.parseFloat(text.replace(",", "").replace(".", "")) * 100) / 100.0);
                jmedioCantidad.setText(df.format(isRecibido));
            }
        } catch (NumberFormatException ex) {
            NovusUtils.printLn(ex.getMessage());
        }
    }

    void cargarRestante() {
        try {
            if (this.cambio < 0) {
                jmedioCantidad.setText(df.format(Math.abs(cambio)));

            }
        } catch (NumberFormatException ex) {
            Logger.getLogger(MedioPagosConfirmarViewController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void changeMedio() {
        int index = jmedioPagoCombo.getSelectedIndex();
        this.fieldStates(true, index);
        this.resetFields();
    }

    private void fieldStates(boolean active, int index) {
        jmedioVoucher.setEnabled((selectedRow > -1 && this.mediosPagosDisponibles.get(index).isComprobante())
                || (active && this.mediosPagosDisponibles.get(index).isComprobante()));
        jmedioCantidad.setEnabled((selectedRow > -1) || active);
        jmedioCantidad.setText(Math.abs(this.cambio) + "");
        jbutton_addMedio.setEnabled((selectedRow > -1) || active);
    }

    private void resetFields() {
        jmedioVoucher.setText("");
        jmedioCantidad.setText("");
        int index = jmedioPagoCombo.getSelectedIndex();
        MediosPagosBean medioSeleccinado = mediosPagosDisponibles.get(index);

    }

    private void mostrarMenuPrincipalDatafono() {
        CardLayout layout = (CardLayout) pnlPrincipal.getLayout();
        layout.show(pnlPrincipal, "pnl_principal");
        this.resetFields();
        this.loadMediosPagosTable();
        this.changeMedio();
        jtableMediosPago.repaint();
    }

    private void removeMediosPago() {
        NovusUtils.beep();
        if (mdao.getBonosVenta(this.movimiento.getId()) == null) {

            if (!getIsGoPassMovimientoUseCase.execute()) {
                DefaultTableModel dm = (DefaultTableModel) jtableMediosPago.getModel();
                ArrayList<MediosPagosBean> mediosCondatafono = new ArrayList<>();
                boolean efectivo = false;
                boolean eliminar = true;
                for (int i = dm.getRowCount() - 1; i >= 0; i--) {
                    mediosCondatafono.add(mediosPagoVenta.get(i));
                    if (!dm.getValueAt(i, 0).equals("EFECTIVO")) {

                        if (dm.getValueAt(i, 0).equals("APP TERPEL")) {
                            eliminar = SingletonMedioPago.ConetextDependecy.getValidateIfDeleteAppTerpelPayment().execute(Long.valueOf(this.movimiento.getId()));
                        }

                        if (eliminar) {
                            this.mediosPagoVenta.remove(i);
                            dm.removeRow(i);
                        }

                    } else {
                        dm.setValueAt(jtotal_venta.getText(), i, 2);
                        for (MediosPagosBean medio : this.mediosPagoVenta) {
                            if (medio.getDescripcion().equals("EFECTIVO")) {
                                medio.setRecibido(this.movimiento.getVentaTotal());
                                efectivo = true;
                            }
                        }
                    }
                }
                if (!eliminar) {
                    showMessage("<html><center>NO PUEDE ELIMINAR EL MEDIO DE PAGO APP TERPEL</center></html>",
                            "/com/firefuel/resources/btBad.png",
                            true, this::mostrarMenuPrincipal,
                            true, LetterCase.FIRST_UPPER_CASE);
                    return;
                }

                if (!efectivo) {
                    this.mediosPagoVenta.clear();
                    anadirMedioEfectivo(dm, true);
                }

                validarMediosConDatafono(mediosCondatafono, Boolean.TRUE);
                eliminarBonos();
                this.validateCalcule(0);
                changeMedio();
                jtableMediosPago.repaint();
                selectedRow = -1;
            } else {
                showMessage("<html><center>NO PUEDE ELIMINAR EL MEDIO DE PAGO</center></html>",
                        "/com/firefuel/resources/btBad.png",
                        true, this::mostrarMenuPrincipal,
                        true, LetterCase.FIRST_UPPER_CASE);
            }
        } else {
            showMessage("<html><center>NO PUEDEN ELIMINAR BONOS RECLAMADOS</center></html>",
                    "/com/firefuel/resources/btBad.png",
                    true, this::mostrarMenuPrincipal,
                    true, LetterCase.FIRST_UPPER_CASE);
        }
    }

    private void eliminarBonos() {
        JsonArray bonos = mdao.getBonosVenta(this.movimiento.getId());
        if (bonos == null) {
            NovusUtils.printLn("No Hay Bonos Confirmados");
            this.BonosViveTerpel.clear();
        }
    }

    private boolean validarMediosConDatafono(ArrayList<MediosPagosBean> medios, boolean addMedioPago) {
        boolean mediosDePagoEliminar = false;
        for (MediosPagosBean medio : medios) {
            boolean addMedio = mdao.validarMedio(movimiento.getId(), medio.getId());
            mediosDePagoEliminar = addMedio;
            if (addMedioPago) {
                if (addMedio && actualizarEfectivo(medio.getRecibido(), true)) {
                    mediosPagoVenta.add(medio);
                    loadMediosPagosTable();
                }
            } else {
                mediosDePagoEliminar = mdao.validarMedio(movimiento.getId(), medio.getId());
            }

        }
        return mediosDePagoEliminar;
    }

    private void confirmacionParaRealizarAnulacion() {
        showPanelMensaje("pnlConfirmacionAnulacion");
        //JsonObject data = datafonosDao.advertenciaDeNotificacionDeCierreTurno();
        JsonObject data = advertenciaDeNotificacionDeCierreTurnoUseCase.execute();
        if(data == null){   
            String mensaje = "No existen pagos por anular ";
            jtituloconfirmacionAnulacion.setText(mensaje);
            return;
        }
        String mensaje = "¿ESTÁ SEGURO QUE DESEA ANULAR EL PAGO ? ";
        if (data.get("cerrarTurno").getAsBoolean()) {
            mensaje = "<html><center>El cierre de turno se ejecutará una vez se concluya este proceso ¿Desea continuar?</center><html>".toUpperCase();
        }
        jtituloconfirmacionAnulacion.setText(mensaje);
        aceptar = () -> {
            anularPago();
        };
        noAceptar = () -> {
            mostrarMenuPrincipal();
        };
    }

    private void anularPago() {
        if (!InfoViewController.hayInternet) {
            showMessage("NO SE PUEDE ANULAR SIN CONEXION A INTERNET",
                    "/com/firefuel/resources/btBad.png",
                    true, this::mostrarMenuPrincipal,
                    true, LetterCase.FIRST_UPPER_CASE);
        } else {
            if (asignarCliente) {
                validarTiempoAnulacion();
            } else {
                validarCierreTurno();
            }
        }
    }

    private void validarTiempoAnulacion() {
        String fechaCambiar = datafonosDao.fechaTransccionVentaDatafono(movimiento.getId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        try {
            LocalDateTime conver = LocalDateTime.parse(fechaCambiar, formatter);
            LocalDateTime horaLocal = LocalDateTime.parse(formatter.format(LocalDateTime.now()), formatter);
            LocalDateTime hora = conver.plusMinutes(5);
            if (horaLocal.isEqual(hora) || horaLocal.isAfter(hora)) {
                showMessage("SE HA COMPLETADO EL TIEMPO MAXIMO PARA HACER LA ANULACIÓN",
                        "/com/firefuel/resources/btBad.png",
                        true,
                        this::mostrarMenuPrincipal,
                        true, LetterCase.FIRST_UPPER_CASE);
            } else {
                validarCierreTurno();
            }
        } catch (DateTimeParseException e) {
            System.out.println("Failed to parse the date and time: " + e.getMessage());
        }
    }

    private void validarCierreTurno() {
        //JsonObject data = datafonosDao.advertenciaDeNotificacionDeCierreTurno();
        JsonObject data = advertenciaDeNotificacionDeCierreTurnoUseCase.execute();
        validarSiHayAnulacionesEncurso();
    }

    private void validarSiHayAnulacionesEncurso() {
        String numeroRecibo;
        if (selectedRow > -1) {
            numeroRecibo = (String) jtableMediosPago.getValueAt(selectedRow, 1);
            MediosPagosBean medios = this.mediosPagoVenta.get(selectedRow);
            if (datafonosDao.hayAnulacionEncurso(this.movimiento.getId(), medios.getId(), numeroRecibo)) {
                showMessage("ESTE PAGO YA TIENE UNA ANULACIÓN PENDIENTE",
                        "/com/firefuel/resources/btBad.png",
                        true, this::mostrarMenuPrincipal,
                        true, LetterCase.FIRST_UPPER_CASE);
            } else {
                segirProcesoDeAnulacion();
            }
        }
    }

    private void segirProcesoDeAnulacion() {
        String numeroRecibo;
        if (selectedRow > -1) {
            int fila = selectedRow;
            numeroRecibo = (String) jtableMediosPago.getValueAt(selectedRow, 1);
            MediosPagosBean medios = this.mediosPagoVenta.get(selectedRow);
            if (datafonosDao.pagoconfirmadoSinAnular(this.movimiento.getId(), medios.getId(), medios.getVoucher())) {
                enviarAnulacion(this.movimiento.getId(), medios.getId(), fila, numeroRecibo);
            } else {
                showMessage("NO SE PUEDE ANULAR EL PAGO SI NO ESTÁ CONFIRMADO",
                        "/com/firefuel/resources/btBad.png",
                        true, this::mostrarMenuPrincipal,
                        true, LetterCase.FIRST_UPPER_CASE);
            }
        }
    }

    private void enviarAnulacion(long idMovimiento, long idMedioPago, int fila, String voucher) {
        ImageIcon icono = ImageCache.getImage("/com/firefuel/resources/loader_fac.gif");
        notificacionAnulacion("mensajeAnulacionDatafono", "ENVIANDO ANULACION", icono);
        AnulacionMedioPago anulacionMedioPago = new AnulacionMedioPago();
        Thread response = new Thread() {
            @Override
            public void run() {
                JsonObject data = anulacionMedioPago.anularPago(idMovimiento, idMedioPago, voucher);
                String rutaIcono = data.get("status").getAsInt() == 200 ? "/com/firefuel/resources/btOk.png" : "/com/firefuel/resources/btBad.png";
                if (data.get("status").getAsInt() == 200) {
                    selectedRow = fila;
                    jguardarVenta.setText("ANULAR");
                    anulacion = Boolean.TRUE;
                    cambiarNombreDeMedioParaAnulacion(fila);
                    Runnable accion;
                    if (asignarCliente) {
                        accion = () -> asignarCliente();
                    } else {
                        accion = () -> cerrar();
                    }
                    notificacionMensajeAnulacion(data.get("mensaje").getAsString(),
                            rutaIcono, true,
                            accion, true, LetterCase.FIRST_UPPER_CASE);
                } else {
                    anulacion = Boolean.FALSE;
                    Runnable volver = () -> mostrarMenuPrincipal();
                    notificacionMensajeAnulacion(data.get("mensaje").getAsString(),
                            rutaIcono, true,
                            volver, true, LetterCase.FIRST_UPPER_CASE);
                }

            }
        };
        response.start();
    }

    private void notificacionMensajeAnulacion(String mensaje, String rutaIcono, boolean habilitar, Runnable accion, boolean autoCerrar, String letterCase) {
        showMessage(mensaje, rutaIcono, habilitar, accion, autoCerrar, letterCase);
    }

    private void cambiarNombreDeMedioParaAnulacion(int fila) {
        jtableMediosPago.setValueAt("ANULANDO MEDIO", fila, 0);
    }

    private void removeMedio(boolean anular) {
        JsonArray bonosVenta = mdao.getBonosVenta(this.movimiento.getId());
        NovusUtils.printLn("Bonos de esta venta: " + bonosVenta);
        if (selectedRow > -1) {
            NovusUtils.beep();
            DefaultTableModel dm = (DefaultTableModel) jtableMediosPago.getModel();
            dm.fireTableDataChanged();
            if (this.mediosPagoVenta.get(selectedRow).isPagosExternoValidado()) {
                this.BonosViveTerpel.clear();
            }
            ArrayList<MediosPagosBean> mediosValidar = new ArrayList<>();
            mediosValidar.add(this.mediosPagoVenta.get(selectedRow));
            if (validarMediosConDatafono(mediosValidar, Boolean.FALSE) && !anular) {
                showMessage("NO SE PUEDE ELIMINAR EL MEDIO DE PAGO CONFIRMADO POR EL DATAFONO",
                        "/com/firefuel/resources/btBad.png",
                        true, this::mostrarMenuPrincipal,
                        true, LetterCase.FIRST_UPPER_CASE);
                mediosValidar.clear();
            } else {
                for (int i = 0; i < dm.getRowCount(); i++) {
                    if (!dm.getValueAt(i, 0).equals("EFECTIVO")) {
                        if (dm.getValueAt(selectedRow, 0).equals("BONO VIVE TERPEL") && bonosVenta != null) {
                            showMessage("NO SE PUEDEN ELIMINAR BONOS RECLAMADOS",
                                    "/com/firefuel/resources/btBad.png",
                                    true, this::mostrarMenuPrincipal,
                                    true, LetterCase.FIRST_UPPER_CASE);
                        } else {
                            String valor = dm.getValueAt(selectedRow, 2).toString();
                            float valorInicial = Float.parseFloat(valor.replaceAll("[^\\d]+", ""));
                            actualizarEfectivo(valorInicial, false);
                            this.mediosPagoVenta.remove(selectedRow);
                            dm.removeRow(selectedRow);
                            break;
                        }
                    }
                }
            }

            if (!hayEfectivo()) {
                anadirMedioEfectivo(dm, false);
            }

            this.validateCalcule(0);
            this.resetFields();
            jtableMediosPago.repaint();
            selectedRow = -1;
        }
    }

    private void anadirMedioEfectivo(DefaultTableModel dm, boolean borrarTodo) {
        float totalEfectivo = 0f;
        medioPagoBean.setId(1);
        medioPagoBean.setDescripcion("EFECTIVO");
        medioPagoBean.setCambio(true);
        medioPagoBean.setIsPagoExterno(false);
        medioPagoBean.setPagosExternoValidado(false);
        medioPagoBean.setValor(this.movimiento.getVentaTotal() - this.movimiento.getRecibidoTotal());
        if (borrarTodo) {
            totalEfectivo = this.movimiento.getVentaTotal();
        } else {
            float totalMediosActuales = totalMediosTabla();
            float valorAgregar = this.movimiento.getVentaTotal() - totalMediosActuales;
            totalEfectivo = valorAgregar;
        }
        medioPagoBean.setRecibido(totalEfectivo);
        medioPagoBean.setCambio(0);
        medioPagoBean.setCredito(false);
        medioPagoBean.setVoucher("");
        medioPagoBean.setComprobante(false);
        if (totalEfectivo > 0) {
            mediosPagoVenta.add(medioPagoBean);
            dm.addRow(new Object[]{"EFECTIVO", "",
                    "$ " + df.format(totalEfectivo)});
        }
    }

    private void removeMedios() {
        if (selectedRow > -1) {
            NovusUtils.beep();
            if (this.mediosPagoVenta.get(selectedRow).isPagosExternoValidado()) {
                this.BonosViveTerpel.clear();
            }
            this.mediosPagoVenta.remove(selectedRow);
            this.validateCalcule(0);
            this.loadMediosPagosTable();
            this.resetFields();
            selectedRow = -1;
        }
    }

    private void removeMedioParametro(int i) {
        this.mediosPagoVenta.remove(i);
        this.loadMediosPagosTable();
        this.validateCalcule(0);
        this.resetFields();
    }

    private void removeTodosMedio() {
        NovusUtils.beep();
        this.mediosPagoVenta.clear();
        this.BonosViveTerpel.clear();
        this.validateCalcule(0);
        this.loadMediosPagosTable();
        this.resetFields();
        selectedRow = -1;
    }

    public boolean sendMedioPago() {

        NovusUtils.printLn("📤 Enviando medios de pago para el movimiento: " + this.movimiento.getId());
        NovusUtils.printLn("🧾 Lista completa de mediosPagoVenta: " + this.mediosPagoVenta);

        boolean response = false;
        JsonObject objMain = new JsonObject();
        objMain.addProperty("identificadorMovimiento", this.movimiento.getId());
        objMain.addProperty("identificadorEquipo", Main.credencial.getEquipos_id());
        objMain.addProperty("validarTurno", false);
        JsonArray mediosArray = new JsonArray();
        JsonArray bonosArray = new JsonArray();
        boolean isBonoTerpelMP = false;

        //Si valida si en la venta se encuentra Bonos Vive Terpel
        ArrayList<MediosPagosBean> mediosList = validacionMediosBonos(mediosPagoVenta);

        for (MediosPagosBean medio : mediosList) {
            JsonObject objMedios = new JsonObject();
            objMedios.addProperty("ct_medios_pagos_id", medio.getId());
            objMedios.addProperty("descripcion", medio.getDescripcion());
            objMedios.addProperty("valor_recibido", medio.getRecibido());
            objMedios.addProperty("valor_cambio", medio.getCambio());
            objMedios.addProperty("valor_total", medio.getRecibido());
            objMedios.addProperty("voucher", medio.getVoucher());
            objMedios.addProperty("moneda_local", "");
            objMedios.addProperty("numero_comprobante", medio.getVoucher());
            objMedios.addProperty("ing_pago_datafono", mdao.validarMedio(movimiento.getId(), medio.getId()));
            objMedios.addProperty("id_adquiriente", medio.getIdAdquiriente());
            objMedios.addProperty("franquicia", medio.getFranquicia());
            objMedios.addProperty("tipo_cuenta", medio.getTipoDeCuenta());
            objMedios.addProperty("id_transaccion", medio.getIdTransaccion());
            objMedios.addProperty("numero_recibo", medio.getNumeroRecibo());
            objMedios.addProperty("id", medio.getIdMedioPago());
            objMedios.addProperty("trm", medio.getTrm());
            objMedios.addProperty("bin", medio.getBin());
            objMedios.addProperty("codigo_dian", medio.getCodigoDian());
            objMedios.addProperty("confirmacionBono", medio.isConfirmacionBono());

            NovusUtils.printLn("✅ Medio armado: " + objMedios);
            mediosArray.add(objMedios);

        }

        for (MediosPagosBean medio : this.mediosPagoVenta) {
            if (medio.isPagosExternoValidado()) {
                isBonoTerpelMP = true;
                long salesForceIdMp = sdao.getCodigoSalesForceMP(medio.getId());
                NovusUtils.printLn("🎟️ Medio con bonos detectado: ID=" + medio.getId() + " -> SalesForceID=" + salesForceIdMp);

                for (BonoViveTerpel bono : medio.getBonosViveTerpel()) {
                    JsonObject bonoViveTerpelMP = new JsonObject();
                    bonoViveTerpelMP.addProperty("IFP", salesForceIdMp);
                    bonoViveTerpelMP.addProperty("VFP", bono.getValor());
                    bonoViveTerpelMP.addProperty("AFP", bono.getVoucher());
                    bonosArray.add(bonoViveTerpelMP);
                    NovusUtils.printLn("🎯 Bono agregado: " + bonoViveTerpelMP);

                }
            }
        }

        JsonObject mediosVenta = new JsonObject();
        mediosVenta.add("mediosDePagos", mediosArray);
        AsignacionClienteBean.agregarInformacionCliente("MediosPago", mediosVenta);

        JsonArray mediosObtenidos = getMediosPagoSinDatafono(mediosArray);

        this.movimiento.setRecibidoTotal(recibido);
        objMain.add("mediosDePagos", mediosObtenidos);

        NovusUtils.printLn("---------------------");
        NovusUtils.printLn("📦 Objeto Medios de Pago final a enviar: \n" + new GsonBuilder().setPrettyPrinting().create().toJson(objMain));
        NovusUtils.printLn("---------------------");

        if (asignarCliente) {
            //Medios de pago sin Datafono
            JsonObject medioSinDatafono = new JsonObject();
            medioSinDatafono.add("mediosPago", mediosObtenidos);
            AsignacionClienteBean.agregarInformacionCliente("MediosObtenidos", medioSinDatafono);

        } else {

            NovusUtils.printLn("🛠️ Actualizando medios de pago en base de datos...");
            response = mdao.actualizarMediosPagoVenta(objMain.toString());
            if (isBonoTerpelMP) {
                ActualizarMovViveTerpel(response, bonosArray);
            }
            if (response) {
                mdao.ingresarAuditoriaMediosPago(this.movimiento.getId(), mediosArray, this.movimiento.getVentaTotal());
            }
        }


        return response;
    }




    public ArrayList<MediosPagosBean> validacionMediosBonos(ArrayList<MediosPagosBean> mediosVenta) {
        int indexBonoViveTerpel = -1;
        boolean isBonoViveTerpel = false;
        ArrayList<MediosPagosBean> mediosList = new ArrayList<>();
        for (int i = 0; i < mediosVenta.size(); i++) {
            String descripcion = mediosVenta.get(i).getDescripcion();
            if (descripcion.equals("BONO VIVE TERPEL") && !isBonoViveTerpel) {
                isBonoViveTerpel = true;
                mediosList.add(mediosVenta.get(i));
                indexBonoViveTerpel = i;
            } else if (descripcion.equals("BONO VIVE TERPEL") && isBonoViveTerpel) {
                String separador = !mediosList.get(indexBonoViveTerpel).getVoucher().equals("") ? "," : "";
                String voucher = mediosVenta.get(i).getVoucher()
                        + separador + mediosList.get(indexBonoViveTerpel).getVoucher();
                float valor = mediosVenta.get(i).getRecibido()
                        + mediosList.get(indexBonoViveTerpel).getRecibido();
                mediosList.get(indexBonoViveTerpel).setVoucher(voucher);
                mediosList.get(indexBonoViveTerpel).setValor(valor);
                mediosList.get(indexBonoViveTerpel).setRecibido(valor);
            } else {
                mediosList.add(mediosVenta.get(i));
            }

        }
        return mediosList;
    }

    public JsonArray getMediosPagoSinDatafono(JsonArray mediosArray) {
        JsonArray medios = new JsonArray();
        medios = mediosArray;
        JsonArray pagosSinDatafono = new JsonArray();
        List<JsonObject> pagosList = new ArrayList<>();
        int indexEfectivo = -1;
        float totalDatafono = 0f;
        float inicialEfectivo = 0f;
        boolean hayEfectivo = false;
        boolean hayDatafono = false;
        for (int i = 0; i < medios.size(); i++) {
            JsonObject pago = medios.get(i).getAsJsonObject();
            String descripcion = pago.get("descripcion").getAsString();
            float valorRecibido = pago.get("valor_recibido").getAsFloat();
            if (!descripcion.equals("CON DATAFONO") && !descripcion.equals(MediosPagosDescription.APPTERPEL)) {
                pagosList.add(pago);
                if (descripcion.equals("EFECTIVO")) {
                    indexEfectivo = i;
                    inicialEfectivo = valorRecibido;
                    hayEfectivo = true;
                }
            } else if (descripcion.equals("CON DATAFONO")) {
                totalDatafono += valorRecibido;
                if (mdao.validarMedio(movimiento.getId(), pago.get("ct_medios_pagos_id").getAsLong())) {
                    pagosList.add(pago);
                    hayDatafono = Boolean.FALSE;
                } else {
                    hayDatafono = Boolean.TRUE;
                }

            } else if (descripcion.equals(MediosPagosDescription.APPTERPEL)) {
                totalDatafono += valorRecibido;
                ValidateIsAppTerpelPaymentSuccess validateIsAppTerpelPaymentProcessed = SingletonMedioPago.ConetextDependecy.getValidateIsAppTerpelPaymentProcessed();
                boolean appTerpelAprobado = validateIsAppTerpelPaymentProcessed.execute(movimiento.getId());
                if (appTerpelAprobado) {
                    pagosList.add(pago);
                    hayDatafono = Boolean.FALSE;
                } else {
                    hayDatafono = Boolean.TRUE;
                }
            }
        }

        if (!hayEfectivo && hayDatafono) {
            JsonObject efectivoPago = new JsonObject();
            efectivoPago.addProperty("ct_medios_pagos_id", 1);
            efectivoPago.addProperty("descripcion", "EFECTIVO");
            efectivoPago.addProperty("valor_recibido", totalDatafono);
            efectivoPago.addProperty("valor_cambio", 0);
            efectivoPago.addProperty("valor_total", totalDatafono);
            efectivoPago.addProperty("voucher", "");
            efectivoPago.addProperty("moneda_local", "");
            efectivoPago.addProperty("numero_comprobante", "");
            pagosList.add(efectivoPago);
        } else {
            float totalFinalEfectivo = inicialEfectivo + totalDatafono;
            if (indexEfectivo > -1) {
                pagosList.remove(indexEfectivo);
                JsonObject pago = medios.get(indexEfectivo).getAsJsonObject();
                pago.addProperty("valor_recibido", totalFinalEfectivo);
                pago.addProperty("valor_total", totalFinalEfectivo);
                pagosList.add(pago);
            }

        }

        Gson gson = new Gson();
        pagosSinDatafono
                = gson.fromJson(pagosList.toString(), JsonArray.class
        );

        JsonArray mediosEnviar = new JsonArray();
        for (JsonElement jsonElement : pagosSinDatafono) {
            JsonObject mediosSinDescripcion = jsonElement.getAsJsonObject();
            mediosSinDescripcion.remove("descripcion");
            mediosEnviar.add(mediosSinDescripcion);
        }

        return mediosEnviar;
    }

    public ArrayList<MediosPagosBean> getMediosActualizar(ArrayList<MediosPagosBean> mediosPago) {
        ArrayList<MediosPagosBean> pagosList = new ArrayList<>();
        int indexEfectivo = -1;
        float totalDatafono = 0f;
        float inicialEfectivo = 0f;
        boolean hayEfectivo = false;
        boolean hayDatafono = false;
        for (int i = 0; i < mediosPago.size(); i++) {
            String descripcion = mediosPago.get(i).getDescripcion();
            float valorRecibido = mediosPago.get(i).getRecibido();
            if (!descripcion.equals("CON DATAFONO") && !descripcion.equals(MediosPagosDescription.APPTERPEL)) {
                pagosList.add(mediosPago.get(i));
                if (descripcion.equals("EFECTIVO")) {
                    indexEfectivo = i;
                    inicialEfectivo = valorRecibido;
                    hayEfectivo = true;
                }
            } else if (descripcion.equals("CON DATAFONO")) {
                totalDatafono += valorRecibido;
                hayDatafono = Boolean.TRUE;
            } else if (descripcion.equals(MediosPagosDescription.APPTERPEL)) {
                totalDatafono += valorRecibido;
                hayDatafono = Boolean.TRUE;
            }
        }
        if (!hayEfectivo && hayDatafono) {
            medioPagoBean.setId(1);
            medioPagoBean.setDescripcion("EFECTIVO");
            medioPagoBean.setRecibido(totalDatafono);
            medioPagoBean.setCambio(0);
            medioPagoBean.setValor(totalDatafono);
            medioPagoBean.setVoucher("");
            pagosList.add(medioPagoBean);
        } else {
            float totalFinalEfectivo = inicialEfectivo + totalDatafono;
            if (indexEfectivo > -1) {
                pagosList.get(indexEfectivo).setRecibido(totalFinalEfectivo);
                pagosList.get(indexEfectivo).setValor(totalFinalEfectivo);
            }
        }
        return pagosList;
    }

    public boolean isBonoViveTerpel(MediosPagosBean medioSeleccinado) {
        return medioSeleccinado.getAtributos() != null && !medioSeleccinado.getAtributos().isJsonNull()
                && medioSeleccinado.getAtributos().get("bonoTerpel") != null
                && !medioSeleccinado.getAtributos().get("bonoTerpel").isJsonNull()
                && medioSeleccinado.getAtributos().get("bonoTerpel").getAsBoolean();
    }

    public boolean isRedencionViveTerpel(MediosPagosBean medioSeleccinado) {
        return medioSeleccinado.getAtributos() != null && !medioSeleccinado.getAtributos().isJsonNull()
                && medioSeleccinado.getAtributos().get("redencionViveTerpel") != null
                && !medioSeleccinado.getAtributos().get("redencionViveTerpel").isJsonNull()
                && medioSeleccinado.getAtributos().get("redencionViveTerpel").getAsBoolean();
    }

    public void ActualizarMovViveTerpel(boolean response, JsonArray bonos) {
        if (response) {
            Long idVenta = this.movimiento.getId();
            try {
                if (mdao.ActualizarAtributosViveTerpel(idVenta, bonos)) {
                    NovusUtils.printLn("ATRIBUTOS VIVE TERPEL ACTUALIZADOS");
                } else {
                    NovusUtils.printLn("NO SE PUDO ACTUALIZAR ATRIBUTOS");
                }
            } catch (DAOException e) {
                NovusUtils.printLn(e.getMessage());
            }

        }
    }

    public void ReclamacionBonoViveTerpel() {
        showMessage("RECLAMANDO BONO VIVE TERPEL....",
                "/com/firefuel/resources/loader_fac.gif",
                false, null,
                false, LetterCase.FIRST_UPPER_CASE);
        ReciboExtended saleFacture = this.getSaleFacture();
        String idPuntoVenta = ConfigurationFacade.fetchSalePointIdentificator();
        ArrayList<MediosPagosBean> mediosP = this.mediosPagoVenta;
        this.timeOutsManager.setTimeoutUtilManager(1, () -> {
            JsonObject response = FidelizacionFacade.fetchClientReclamacion(saleFacture, "", "", idPuntoVenta, mediosP);
            procesamientRespuestaReclamacion(response);
        });
    }

    void RedencionPuntosViveTerpel(JsonObject cliente) {
        showMessage("REDIMIENDO PUNTOS, POR FAVOR ESPERE....",
                "/com/firefuel/resources/loader_fac.gif",
                false, null,
                false, LetterCase.FIRST_UPPER_CASE);
        ReciboExtended saleFacture = this.getSaleFacture();
        String idCliente = cliente.get("idCliente").getAsString();
        String idTipoCliente = cliente.get("tipo_id").getAsString();
        String pin = cliente.get("pin").getAsString();
        String idPuntoVenta = ConfigurationFacade.fetchSalePointIdentificator();
        ArrayList<MediosPagosBean> mediosP = this.mediosPagoVenta;
        this.timeOutsManager.setTimeoutUtilManager(1, () -> {
            JsonObject response = FidelizacionFacade.fetchClientRedencion(saleFacture, idCliente, idTipoCliente, pin,
                    idPuntoVenta, mediosP);
            procesamientRespuestaReclamacion(response);
        });
    }

    void procesamientRespuestaReclamacion(JsonObject response) {
        final int CLIENT_IDENTIFIED_CODE = 20000;
        boolean respuesta = false;
        int statusCode = 0;
        if (response != null && !response.get("statusCode").isJsonNull()) {
            statusCode = response.get("statusCode").getAsInt();
        }
        NovusUtils.printLn("STATUS CODE: " + statusCode);
        String mensaje;
        switch (statusCode) {
            case 200:
                if (response != null) {
                    if (response.get("codigoRespuesta") != null && !response.get("codigoRespuesta").isJsonNull()) {
                        String responseMessage = response.get("mensajeRespuesta") != null
                                ? response.get("mensajeRespuesta").getAsString().toUpperCase()
                                : "ERROR EN EL PROCESO";
                        int responseCode = response.get("codigoRespuesta").getAsInt();

                        if (responseCode == CLIENT_IDENTIFIED_CODE) {
                            NovusUtils.printLn("****************************");
                            NovusUtils.printLn("Enviando Medios procesamientRespuestaReclamacion ");
                            NovusUtils.printLn("****************************");
                            respuesta = sendMedioPago();
                            if (respuesta) {
                                this.ErrorViveTerpel = false;
                                mensaje = "";
                                if (runnableClientesDatafono != null) {
                                    runnableClientesDatafono.run();
                                } else {
                                    showMessage("MEDIO DE PAGO EDITADO EXITOSAMENTE",
                                            "/com/firefuel/resources/btOk.png",
                                            true, this::cerrar,
                                            true, LetterCase.FIRST_UPPER_CASE);
                                    impresionVenta();
                                }
                            } else {
                                this.ErrorViveTerpel = true;
                                mensaje = "NO SE PUEDE EDITAR MEDIO PAGO";
                            }
                        } else {
                            this.ErrorViveTerpel = true;
                            mensaje = responseMessage;
                        }
                    } else {
                        this.ErrorViveTerpel = true;
                        mensaje = response.get("mensajeError").getAsString();
                    }
                } else {
                    this.ErrorViveTerpel = true;
                    mensaje = "La transacción no se puede realizar en este momento";
                }
                break;
            default:
                this.ErrorViveTerpel = true;
                mensaje = "La transacción no se puede realizar en este momento";
        }
        if (mensaje.length() > 0) {
            showMessage(mensaje,
                    "/com/firefuel/resources/btBad.png",
                    true, this::mostrarMenuPrincipal,
                    true, LetterCase.FIRST_UPPER_CASE);
        }
    }

    private ReciboExtended getSaleFacture() {
        return this.reciboRec;
    }

    private void Async(Runnable runnable, int tiempo) {
        new Thread(() -> {
            try {
                Thread.sleep(tiempo * 1000);
                runnable.run();

            } catch (InterruptedException e) {
                Logger.getLogger(MedioPagosConfirmarViewController.class
                        .getName()).log(Level.SEVERE, null, e);
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public void showMessage(String msj, String ruta,
                            boolean habilitar, Runnable runnable,
                            boolean autoclose, String letterCase) {
        ParametrosMensajes parametrosMensajes = new ParametrosMensajesBuilder().setMsj(msj)
                .setRuta(ruta).setHabilitar(habilitar)
                .setRunnable(runnable).setAutoclose(autoclose)
                .setLetterCase(letterCase).build();
        mostrarSubPanel(ShowMessageSingleton.showMassegesInstance().execute(parametrosMensajes));
    }

    public void showMessageAppterpel(ParametrosMensajes parametrosMensajes) {
        mostrarSubPanel(ShowMessageSingleton.showMassegesInstance().executeAppterpel(parametrosMensajes));
    }

    private void showPanelMensaje(String panel) {
        NovusUtils.showPanel(pnlPrincipal, panel);
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

    public void mostrarMenuPrincipal() {
        CardLayout layout = (CardLayout) pnlPrincipal.getLayout();
        layout.show(pnlPrincipal, "pnl_principal");
    }



    private void vistaAsignarCliente() {
        NovusUtils.printLn("****************************");
        NovusUtils.printLn("Enviando Medios Pago vistaAsignarCliente");
        NovusUtils.printLn("****************************");
        sendMedioPago();
        asignarCliente();
    }

    private void anterior() {
        if (cerrar != null) {
            NovusUtils.printLn("****************************");
            NovusUtils.printLn("Enviando Medios Pago anterior()");
            NovusUtils.printLn("****************************");
            sendMedioPago();
            cerrar.run();
            if (timer != null) {
                timer.stop();
            }
        }
    }

    private void asignarCliente() {
        NovusUtils.printLn("[asignarCliente]");
        if (validarEsPagoDatafono()) {
            if (cerrarTodo != null) {
                // PASA A LA VENTANA ASIGNAR DATOS
                NovusUtils.printLn("PASA A LA VENTANA ASIGNAR DATOS");
                NovusUtils.printLn("****************************");
                NovusUtils.printLn("Enviando Medios Pago asignarCliente()");
                NovusUtils.printLn("****************************");
                cerrarTodo.run();
            }
        }
        if (validaresAppterpel()) {
            cerrarSinDatatafono.run();
        } else {
            if (cerrarSinDatatafono != null) {
                // PASA A LA VENTANA ASIGNAR DATOS
                cerrarSinDatatafono.run();
            }
        }
    }

    private void enviarPagoDatafonos() {
        pagosDatafono.clear();
        for (int i = 0; i < mediosPagoVenta.size(); i++) {
            if (mediosPagoVenta.get(i).getDescripcion().equalsIgnoreCase("CON DATAFONO")) {
                pagosDatafono.add(mediosPagoVenta.get(i));
            }
        }
        @SuppressWarnings("unchecked")
        ArrayList<MediosPagosBean> mediosActualizar = getMediosActualizar((ArrayList<MediosPagosBean>) mediosPagoVenta.clone());
        getDatafonosView(pagosDatafono, this.movimiento, mediosActualizar);
    }

    private void enviarPagoDatafono() {
        for (JsonElement datafono : datafonos) {
            JsonObject infoDatafono = datafono.getAsJsonObject();
            plaquetaDatafono = infoDatafono.get("plaqueta").getAsString();
            adquiriente = infoDatafono.get("proveedor").getAsString();
            codigoTerminal = infoDatafono.get("codigo_terminal").getAsString();
            idAquiriente = infoDatafono.get("id_adquiriente").getAsInt();
        }
        for (int i = 0; i < mediosPagoVenta.size(); i++) {
            if (mediosPagoVenta.get(i).getDescripcion().equalsIgnoreCase("CON DATAFONO")) {
                pagosDatafono.add(mediosPagoVenta.get(i));
            }
        }
        tipoVenta();
        @SuppressWarnings("unchecked")
        ArrayList<MediosPagosBean> mediosActualizar = getMediosActualizar((ArrayList<MediosPagosBean>) mediosPagoVenta.clone());
        procesarPago(this.movimiento, mediosActualizar);
    }

    private void procesarPago(MovimientosBean movimiento, ArrayList<MediosPagosBean> mediosPagosSinDatafono) {
        showPanel("mensajesDatafono", "ENVIANDO EL PAGO AL DATAFONO", "", "", loaderDatafono, false, null);
        pagoMixto = pagosDatafono.size() > 1 ? Boolean.TRUE : Boolean.FALSE;

        Thread response = new Thread() {
            @Override
            public void run() {
                JsonObject respuestaEnvio = new JsonObject();
                int contador = 0;
                for (MediosPagosBean medios : pagosDatafono) {
                    float totalPagoDatafono = medios.getRecibido();
                    if (contador == 0) {
                        respuestaEnvio = enviarPagoDatafono.enviarVentaDatafonos(totalPagoDatafono, movimiento, adquiriente, codigoTerminal, idAquiriente, pagoMixto);
                    } else {
                        NovusUtils.pause(10);
                        respuestaEnvio = enviarPagoDatafono.enviarVentaDatafonos(totalPagoDatafono, movimiento, adquiriente, codigoTerminal, idAquiriente, pagoMixto);
                    }
                    contador++;
                }
                Runnable volver = () -> {
                    if (asignarCliente) {
                        NovusUtils.printLn("Asignar Cliente procesarPago");
                        vistaAsignarCliente();
                    } else {
                        if (runnable != null) {
                            runnable.run();
                            runnable = null;
                        }
                    }
                };
                Runnable mostrarPrincipal = () -> mostrarMenuPrincipal();
                if (respuestaEnvio.get("estado").getAsInt() == 200) {
                    showPanel("mensajesDatafono",
                            respuestaEnvio.get("mensaje").getAsString(),
                            respuestaEnvio.get("informacionDatafono").getAsString(),
                            respuestaEnvio.get("transacciones").getAsString(),
                            pagoDatafono,
                            respuestaEnvio.get("cerrar").getAsBoolean(),
                            volver);
                    ActualizarMediosPagos medios = new ActualizarMediosPagos();
                    if (!mediosPagosSinDatafono.isEmpty()) {
                        medios.actulizarMediosDePagoSinDatafonos(movimiento, mediosPagosSinDatafono);
                    }
                } else {
                    showPanel("mensajesDatafono",
                            respuestaEnvio.get("mensaje").getAsString(),
                            respuestaEnvio.get("informacionDatafono").getAsString(),
                            respuestaEnvio.get("transacciones").getAsString(),
                            errorDatafono,
                            respuestaEnvio.get("cerrar").getAsBoolean(),
                            mostrarPrincipal);
                    pagosDatafono.clear();
                }
            }
        };
        response.start();
    }

    public void loaderReclamacionBonos() {
        showMessage("REDIMIENDO PUNTOS, POR FAVOR ESPERE....", "/com/firefuel/resources/loader_fac.gif",
                false, null, false, LetterCase.FIRST_UPPER_CASE);
    }

    public void tipoVenta() {
        if (pagosDatafono.size() > 1) {
            tipoVenta = NovusConstante.PAGO_MIXTO;
        }
    }

    private void showPanel(String panel, String msjPrincipal, String informacionDatafono, String transacciones, Icon icono, boolean cerrar, Runnable accion) {
        CardLayout cardPanelLayout = (CardLayout) this.pnlPrincipal.getLayout();
        cardPanelLayout.show(pnlPrincipal, panel);
        jTitulo.setText(msjPrincipal);
        jInfoDatafono.setText(informacionDatafono);
        jTransacciones.setText(transacciones);
        jIcono.setIcon(icono);
        jCerrar.setVisible(cerrar);
        runnableDatafono = accion;
        if (accion != null) {
            this.timeOutsManager.setTimeoutUtilManager(5, accion);
        }
    }

    private void notificacionAnulacion(String panel, String mensaje, Icon icono) {
        CardLayout card = (CardLayout) this.pnlPrincipal.getLayout();
        card.show(pnlPrincipal, panel);
        jTituloNotificaionAnulacion.setText(mensaje);
        jIconoNotificacionAnulacion.setIcon(icono);
    }

    private String obtenerCodigoTerminal(JsonArray datafonos) {
        String codigoTermianl = "";
        for (JsonElement objDatafonos : datafonos) {
            JsonObject dataitemDatafono = objDatafonos.getAsJsonObject();
            codigoTermianl = dataitemDatafono.get("codigo_terminal").getAsString();
        }
        return codigoTermianl;
    }

    /**
     * Verifica si la venta actual es de tipo GLP basándose en el producto del movimiento
     * @return true si es GLP, false en caso contrario
     */
    private boolean esVentaGLP() {
        try {
            if (this.movimiento != null && this.movimiento.getId() > 0) {
                // Obtener el ID del producto del movimiento
                long idProducto = GetByProductoMovimientoDetalleUseCase.obtenerProductoId(this.movimiento.getId());
                
                if (idProducto > 0) {
                    // Obtener la información del producto
                    MovimientosDao mdao = new MovimientosDao();
                    ProductoBean producto = mdao.findProductByIdActive(idProducto);
                    
                    if (producto != null && producto.getDescripcion() != null) {
                        String descripcionProducto = producto.getDescripcion().toUpperCase();
                        NovusUtils.printLn("[VALIDACION GLP] Producto de la venta: " + descripcionProducto);
                        
                        // Verificar si la descripción contiene "GLP"
                        return descripcionProducto.contains("GLP");
                    }
                }
            }
            
            NovusUtils.printLn("[VALIDACION GLP] No se pudo determinar el tipo de producto para la venta");
            return false;
            
        } catch (DAOException e) {
            NovusUtils.printLn("[ERROR VALIDACION GLP] Error DAO al verificar tipo de producto: " + e.getMessage());
            return false;
        } catch (Exception e) {
            NovusUtils.printLn("[ERROR VALIDACION GLP] Error general al verificar tipo de producto: " + e.getMessage());
            return false;
        }
    }

}
