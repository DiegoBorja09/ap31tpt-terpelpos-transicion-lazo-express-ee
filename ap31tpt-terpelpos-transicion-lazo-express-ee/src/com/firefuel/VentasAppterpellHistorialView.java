package com.firefuel;

import com.WT2.appTerpel.application.UseCase.Payment.Listing.RecoverPaymentProcessedAppterpel;
import com.WT2.Containers.Dependency.SingletonMedioPago;
import com.WT2.appTerpel.domain.entities.PaymentProcessedParams;
import com.WT2.appTerpel.domain.entities.Sales;
import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.application.useCases.wacherparametros.FindParameterValueUseCase;
import com.bean.AsignacionClienteBean;
import com.bean.Notificador;
import com.bean.PersonaBean;
import com.bean.ReciboExtended;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.MovimientosDao;
import com.dao.PersonasDao;
import com.dao.SurtidorDao;
import com.firefuel.components.RenderTablaColor;
import com.firefuel.facturacion.electronica.NotasCredito;
import com.firefuel.facturacion.electronica.VentasSinResolverFE;
import com.firefuel.utils.ShowMessageSingleton;
import com.google.gson.JsonObject;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.HierarchyEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import com.application.useCases.jornada.GetJornadasByGroupUseCase;

public class VentasAppterpellHistorialView extends JDialog {
    GetJornadasByGroupUseCase getJornadasByGroupUseCase;

    MovimientosDao mdao = new MovimientosDao();
    SurtidorDao dao = new SurtidorDao();
    ImageIcon btnRight = new ImageIcon(getClass().getResource("/com/firefuel/resources/btn_right1.png"));
    ImageIcon btnLeft = new ImageIcon(getClass().getResource("/com/firefuel/resources/btn_izquierda.png"));
    ImageIcon btnRightPressed = new ImageIcon(getClass().getResource("/com/firefuel/resources/btn_right2.png"));
    ImageIcon btnlefttPressed = new ImageIcon(getClass().getResource("/com/firefuel/resources/btn_izquierda-press.png"));
    SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATETIME_AM);
    SimpleDateFormat sdf2 = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATE);
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);
    boolean activarAcumular;
    final int CONSULTAR_VENTAS_FECHAS = 1;
    String tipo = "";
    String cedula = "";
    public JsonObject notaP;
    ArrayList<ReciboExtended> lista;
    ArrayList<ReciboExtended> listaVentasSinResolver = new ArrayList<>();
    ReciboExtended reciboFidelizar = null;
    DefaultTableCellRenderer textRight;
    DefaultTableCellRenderer textCenter;
    boolean resetear = false;
    boolean aplicaFE = false;
    InfoViewController parent;
    public int devolucion = 0;
    private final Icon botonActivoIcono = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-rojo-1.png"));
    private final Icon botonBloqueadoIcono = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-gris-1.png"));
    private final Icon botonTabsSeleccionado = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-tabs-2.png"));
    private final Icon botonTabsNoSeleccionado = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-tabs-1.png"));
    private final Icon botonSeleccionado = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/btn-seleccionar-small.png"));
    private final Icon botonActivoIconoIconoImpresora = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/impresora-boton.png"));
    private final Icon botonBloqueadoIconoImpresora = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/impresora-boton-gris.png"));
    private Runnable runnable;
    private Runnable handler;
    private Runnable anulacion;
    private Runnable noAceptar;
    static private Runnable refrescar;
    VentasHistorialPlacaViewController ventaPlaca;
    PersonaBean persona = Main.persona;
    PersonasDao pdao = new PersonasDao();
    public static boolean estadoActulizar = false;
    public static boolean estadoActulizarDatafono;
    long idTransacciondatafono;
    long id = 0L;
    boolean fidelizada;
    int validarTipoVenta = 0;
    long tiempoMaximoCliente = 5;
    Runnable irAventas;

    Runnable runnableDatafonos = null;
    Timer timer;
    boolean ejecutarAccionDeCerrado = true;

    private final Icon loaderDatafono = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/enviando pago.gif"));
    private final Icon errorDatafono = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/venta error.gif"));
    private final Icon okDatafono = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/venta check.gif"));
    private final Icon iconoOk = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/iconoCheck.png"));
    private final Icon iconoError = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/iconoError.png"));

    public VentasAppterpellHistorialView(InfoViewController parent, boolean modal) {
        super(parent, modal);
        lista = new ArrayList<>();
        this.parent = parent;
        initComponents();
        init();
    }

    public VentasAppterpellHistorialView(InfoViewController parent, boolean modal, Runnable runnable) {
        super(parent, modal);
        lista = new ArrayList<>();
        this.parent = parent;
        this.runnable = runnable;
        initComponents();
        init();
    }

    public static void setEstadoActulizarDatafono(boolean estado) {
        VentasAppterpellHistorialView.estadoActulizarDatafono = estado;
    }

    private void init() {
        this.getJornadasByGroupUseCase = new GetJornadasByGroupUseCase();

        InfoViewController.NotificadorInfoView = null;
        InfoViewController.NotificadorVentasHistorial = new Notificador() {
            @Override
            public void send(JsonObject data) {
                mostrarPanelMensaje(data.get("mensaje").getAsString(), data, true);
            }
        };

        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);
        CardLayout cartd = (CardLayout) cardVentas.getLayout();
        cartd.show(cardVentas, "ventasAppTerpel");

        if (Main.persona != null) {
            jpromotor.setText(Main.persona.getNombre());
        } else {
            jpromotor.setText("NO HAY TURNO ABIERTO");
        }

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(186, 12, 47));
        headerRenderer.setForeground(new Color(255, 255, 255));
        jTable1.setFillsViewportHeight(true);
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < jTable1.getModel().getColumnCount(); i++) {
            TableColumn column = jTable1.getColumnModel().getColumn(i);
            column.setHeaderRenderer(headerRenderer);
            column.setCellRenderer(cellRenderer);
        }
        TableRowSorter<TableModel> rowSorter = new TableRowSorter<TableModel>(jTable1.getModel()) {
            @Override
            public boolean isSortable(int column) {
                super.isSortable(column);
                return false;
            }
        };
        jTable1.setRowSorter(rowSorter);

        jScrollPane2.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        jScrollPane2.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane2.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));

        NovusUtils.setUnsortableTable(this.jTable1);
        cargarPromotores();
        jScrollPane1.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        jScrollPane1.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane1.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));
        jComboPromotor.addActionListener((java.awt.event.ActionEvent evt) -> {
            Async(() -> {
                refrescarDatos("JCOMBOX " + jComboPromotor.getSelectedIndex());
            });
        });

        this.tiempoMaximoCliente = FindParameterValueUseCase.consultarTiempoMaximoDatosCliente();
        this.renderizarUsuarios();
        this.renderizarUsuarios();
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0) {
                if (!isDisplayable()) {
                    jclock.stopClock();
                }
            }
        });
    }

    public void mostartPanelMensajeGeneral(JsonObject data, boolean autecerrar) {
        CardLayout panel = (CardLayout) pnlPrincipal.getLayout();
        panel.show(pnlPrincipal, "pnlMensajeGopass");
        lblTitulo.setText(data.get("titulo").getAsString());
        if (data.get("estado").getAsBoolean()) {
            fndIcono.setIcon(iconoOk);
        } else {
            fndIcono.setIcon(iconoError);
        }
        lblFooter.setText("");
        lblMsg.setText("<html><center>" + data.get("mensaje").getAsString() + "</center></html>");

    }

    public void mostrarPanelMensaje(String mensaje, JsonObject data, boolean autecerrar) {

        if (data.get("tipo").getAsInt() > 3) {
            mostartPanelMensajeGeneral(data, autecerrar);
        } else {
            CardLayout panel = (CardLayout) pnlPrincipal.getLayout();
            panel.show(pnlPrincipal, "mensajesDatafono");
            if (data.get("estado").getAsBoolean()) {
                jTitulo.setText("<html><center><b>" + data.get("mensaje").getAsString() + "</b></center></html>");
                jIcono.setIcon(okDatafono);
            } else {
                jTitulo.setText("<html><center><b>" + data.get("mensaje").getAsString() + "</b></center></html>");
                jIcono.setIcon(errorDatafono);
            }
            if (!data.get("codigoAutorizacion").getAsString().isEmpty()) {
                jTransacciones.setText("ID TRANSACCIÓN " + data.get("codigoAutorizacion").getAsString());
            } else {
                jTransacciones.setText("");
            }

        }

        setTimeoutMensajeDatafono(5, () -> mostrarMenuPrincipal(autecerrar));

    }

    private void showPanel(String panel) {        
        NovusUtils.showPanel(pnlPrincipal, panel);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        pnlPrincipal = new javax.swing.JPanel();
        pnlReporteVentas = new javax.swing.JPanel();
        jclock = ClockViewController.getInstance();
        jpromotor3 = new javax.swing.JLabel();
        jComboMostrar = new javax.swing.JComboBox<>();
        jpromotor4 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jpromotor = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jpromotor1 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jComboPromotor = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        accionesDeVentas = new javax.swing.JPanel();
        cardVentas = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jpromotor2 = new javax.swing.JLabel();
        pnlPromotor = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jNotificacion = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        pnl_confirmacion = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        NO = new javax.swing.JLabel();
        SI1 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        fnd = new javax.swing.JLabel();
        card_mensaje = new PanelNotificacion();
        pnlMsjDatafonos = new javax.swing.JPanel();
        jCerrar = new javax.swing.JLabel();
        jTitulo = new javax.swing.JLabel();
        jInfoDatafono = new javax.swing.JLabel();
        jTransacciones = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jIcono = new javax.swing.JLabel();
        fndFondo = new javax.swing.JLabel();
        pnlMensajeGopass = new javax.swing.JPanel();
        jCerrar1 = new javax.swing.JLabel();
        lblTitulo = new javax.swing.JLabel();
        lblFooter = new javax.swing.JLabel();
        lblMsg = new javax.swing.JLabel();
        fndIcono = new javax.swing.JLabel();
        fndMensaje = new javax.swing.JLabel();
        fndFondo1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(null);

        pnlPrincipal.setMaximumSize(new java.awt.Dimension(1280, 800));
        pnlPrincipal.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnlPrincipal.setName(""); // NOI18N
        pnlPrincipal.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnlPrincipal.setLayout(new java.awt.CardLayout());

        pnlReporteVentas.setLayout(null);

        jclock.setMaximumSize(new java.awt.Dimension(110, 60));
        jclock.setOpaque(false);
        jclock.setLayout(null);
        pnlReporteVentas.add(jclock);
        jclock.setBounds(1150, 720, 110, 60);

        jpromotor3.setFont(new java.awt.Font("Conthrax", 1, 24)); // NOI18N
        jpromotor3.setForeground(new java.awt.Color(153, 0, 0));
        jpromotor3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jpromotor3.setText("MOSTRAR");
        pnlReporteVentas.add(jpromotor3);
        jpromotor3.setBounds(990, 100, 130, 40);

        jComboMostrar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jComboMostrar.setForeground(new java.awt.Color(51, 51, 51));
        jComboMostrar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "20", "60", "120", "200", "Todos" }));
        jComboMostrar.setBorder(null);
        jComboMostrar.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboMostrarItemStateChanged(evt);
            }
        });
        jComboMostrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jComboMostrarMouseReleased(evt);
            }
        });
        jComboMostrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboMostrarActionPerformed(evt);
            }
        });
        pnlReporteVentas.add(jComboMostrar);
        jComboMostrar.setBounds(1120, 100, 110, 40);

        jpromotor4.setFont(new java.awt.Font("Conthrax", 1, 24)); // NOI18N
        jpromotor4.setForeground(new java.awt.Color(153, 0, 0));
        jpromotor4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jpromotor4.setText("PROMOTOR");
        pnlReporteVentas.add(jpromotor4);
        jpromotor4.setBounds(40, 110, 150, 40);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        pnlReporteVentas.add(jLabel28);
        jLabel28.setBounds(10, 710, 100, 80);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnlReporteVentas.add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jpromotor.setFont(new java.awt.Font("Conthrax", 0, 36)); // NOI18N
        jpromotor.setForeground(new java.awt.Color(255, 255, 0));
        jpromotor.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jpromotor.setText("PROMOTOR");
        pnlReporteVentas.add(jpromotor);
        jpromotor.setBounds(500, 30, 760, 50);

        jLabel3.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("HISTORIAL VENTAS APPTERPEL");
        pnlReporteVentas.add(jLabel3);
        jLabel3.setBounds(100, 0, 720, 90);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel7MouseReleased(evt);
            }
        });
        pnlReporteVentas.add(jLabel7);
        jLabel7.setBounds(10, 10, 70, 71);

        jpromotor1.setFont(new java.awt.Font("Conthrax", 1, 24)); // NOI18N
        jpromotor1.setForeground(new java.awt.Color(255, 255, 255));
        jpromotor1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jpromotor1.setText("PROMOTOR");
        pnlReporteVentas.add(jpromotor1);
        jpromotor1.setBounds(800, 10, 460, 20);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnlReporteVentas.add(jLabel31);
        jLabel31.setBounds(80, 10, 10, 68);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnlReporteVentas.add(jLabel29);
        jLabel29.setBounds(1140, 710, 10, 80);

        jPanel1.setBackground(new java.awt.Color(186, 12, 47));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jComboPromotor.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jComboPromotor.setForeground(new java.awt.Color(51, 51, 51));
        jComboPromotor.setBorder(null);
        jComboPromotor.setName(""); // NOI18N
        jPanel1.add(jComboPromotor, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 410, 60));

        jLabel1.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/actualizar.png"))); // NOI18N
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel1MousePressed(evt);
            }
        });
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 0, 80, 60));

        pnlReporteVentas.add(jPanel1);
        jPanel1.setBounds(190, 100, 490, 60);

        accionesDeVentas.setOpaque(false);
        accionesDeVentas.setLayout(new java.awt.CardLayout());
        pnlReporteVentas.add(accionesDeVentas);
        accionesDeVentas.setBounds(130, 700, 1010, 100);

        cardVentas.setBackground(new java.awt.Color(255, 255, 255));
        cardVentas.setLayout(new java.awt.CardLayout());

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane2.setBorder(null);

        jTable1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jTable1.setFont(new java.awt.Font("Bebas Neue", 0, 26)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "NRO", "PREFIJO", "FECHA", "PRODUCTO", "PROMOTOR", "ESTADO", "CARA", "CANT", "VALOR"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setRequestFocusEnabled(false);
        jTable1.setRowHeight(35);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.setShowGrid(true);
        jTable1.setShowHorizontalLines(false);
        jTable1.setShowVerticalLines(false);
        jTable1.getTableHeader().setResizingAllowed(false);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(90);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(190);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(200);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(150);
            jTable1.getColumnModel().getColumn(5).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(130);
            jTable1.getColumnModel().getColumn(6).setResizable(false);
            jTable1.getColumnModel().getColumn(7).setResizable(false);
            jTable1.getColumnModel().getColumn(8).setResizable(false);
            jTable1.getColumnModel().getColumn(8).setPreferredWidth(150);
        }

        jPanel6.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1220, -1));

        cardVentas.add(jPanel6, "ventasAppterpel");

        pnlReporteVentas.add(cardVentas);
        cardVentas.setBounds(30, 250, 1220, 440);

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        pnlReporteVentas.add(jLabel5);
        jLabel5.setBounds(0, 0, 1280, 800);

        jpromotor2.setFont(new java.awt.Font("Conthrax", 1, 24)); // NOI18N
        jpromotor2.setForeground(new java.awt.Color(255, 255, 255));
        jpromotor2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jpromotor2.setText("PROMOTOR");
        pnlReporteVentas.add(jpromotor2);
        jpromotor2.setBounds(800, 10, 460, 20);

        pnlPrincipal.add(pnlReporteVentas, "pnl_principal");

        pnlPromotor.setLayout(null);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(153, 0, 0));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("IDENTIFICACION");
        pnlPromotor.add(jLabel6);
        jLabel6.setBounds(310, 90, 180, 40);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(153, 0, 0));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("PROMOTOR");
        pnlPromotor.add(jLabel8);
        jLabel8.setBounds(520, 90, 480, 40);

        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setPreferredSize(new java.awt.Dimension(800, 610));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 830, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 610, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(jPanel4);

        pnlPromotor.add(jScrollPane1);
        jScrollPane1.setBounds(240, 120, 840, 580);

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("SELECCIONE EL PROMOTOR");
        pnlPromotor.add(jLabel11);
        jLabel11.setBounds(140, 720, 980, 70);

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        pnlPromotor.add(jNotificacion);
        jNotificacion.setBounds(150, 720, 970, 70);

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        pnlPromotor.add(jLabel30);
        jLabel30.setBounds(10, 710, 100, 80);

        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnlPromotor.add(jLabel32);
        jLabel32.setBounds(120, 710, 10, 80);

        jLabel12.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("CONSULTA VENTAS");
        pnlPromotor.add(jLabel12);
        jLabel12.setBounds(100, 0, 680, 90);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel13MouseReleased(evt);
            }
        });
        pnlPromotor.add(jLabel13);
        jLabel13.setBounds(10, 10, 70, 71);

        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnlPromotor.add(jLabel33);
        jLabel33.setBounds(80, 10, 10, 68);

        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnlPromotor.add(jLabel34);
        jLabel34.setBounds(1130, 710, 10, 80);

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        jLabel14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel14MousePressed(evt);
            }
        });
        pnlPromotor.add(jLabel14);
        jLabel14.setBounds(0, 0, 1281, 800);

        pnlPrincipal.add(pnlPromotor, "pnlPromotor");

        pnl_confirmacion.setLayout(null);

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnl_confirmacion.add(jLabel9);
        jLabel9.setBounds(307, 250, 690, 180);

        NO.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        NO.setForeground(new java.awt.Color(255, 255, 255));
        NO.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        NO.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"))); // NOI18N
        NO.setText("NO");
        NO.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        NO.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                NOMouseClicked(evt);
            }
        });
        pnl_confirmacion.add(NO);
        NO.setBounds(330, 480, 264, 54);

        SI1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        SI1.setForeground(new java.awt.Color(255, 255, 255));
        SI1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        SI1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"))); // NOI18N
        SI1.setText("SI");
        SI1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        SI1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                SI1MouseClicked(evt);
            }
        });
        pnl_confirmacion.add(SI1);
        SI1.setBounds(700, 480, 264, 54);

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/card.png"))); // NOI18N
        jLabel10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnl_confirmacion.add(jLabel10);
        jLabel10.setBounds(250, 210, 800, 360);

        fnd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_principal0.png"))); // NOI18N
        fnd.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                fndMousePressed(evt);
            }
        });
        pnl_confirmacion.add(fnd);
        fnd.setBounds(0, 0, 1280, 800);

        pnlPrincipal.add(pnl_confirmacion, "pnl_confirmacion");
        pnlPrincipal.add(card_mensaje, "card_mensaje");

        pnlMsjDatafonos.setLayout(null);

        jCerrar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jCerrar.setForeground(new java.awt.Color(153, 0, 0));
        jCerrar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-blanco-2.png"))); // NOI18N
        jCerrar.setText("CERRAR");
        jCerrar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jCerrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jCerrarMouseClicked(evt);
            }
        });
        pnlMsjDatafonos.add(jCerrar);
        jCerrar.setBounds(990, 18, 249, 53);

        jTitulo.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jTitulo.setForeground(new java.awt.Color(186, 12, 47));
        jTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jTitulo.setText("TITULO MENSAJE");
        jTitulo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnlMsjDatafonos.add(jTitulo);
        jTitulo.setBounds(280, 450, 740, 80);

        jInfoDatafono.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jInfoDatafono.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jInfoDatafono.setText("INFORMACION DATAFONO");
        jInfoDatafono.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnlMsjDatafonos.add(jInfoDatafono);
        jInfoDatafono.setBounds(290, 530, 740, 28);

        jTransacciones.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jTransacciones.setForeground(new java.awt.Color(186, 12, 47));
        jTransacciones.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jTransacciones.setText("TRANSACCION");
        jTransacciones.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnlMsjDatafonos.add(jTransacciones);
        jTransacciones.setBounds(290, 570, 740, 28);

        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnlMsjDatafonos.add(jLabel35);
        jLabel35.setBounds(80, 10, 10, 68);

        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnlMsjDatafonos.add(jLabel36);
        jLabel36.setBounds(120, 710, 10, 80);

        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnlMsjDatafonos.add(jLabel37);
        jLabel37.setBounds(1130, 710, 10, 80);

        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        pnlMsjDatafonos.add(jLabel38);
        jLabel38.setBounds(10, 710, 100, 80);

        jIcono.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/enviando pago.gif"))); // NOI18N
        jIcono.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnlMsjDatafonos.add(jIcono);
        jIcono.setBounds(520, 190, 248, 248);

        fndFondo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fndFondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndMsjDatafonos.png"))); // NOI18N
        fndFondo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fndFondo.setMaximumSize(new java.awt.Dimension(1280, 800));
        fndFondo.setMinimumSize(new java.awt.Dimension(1280, 800));
        fndFondo.setPreferredSize(new java.awt.Dimension(1280, 800));
        fndFondo.setRequestFocusEnabled(false);
        pnlMsjDatafonos.add(fndFondo);
        fndFondo.setBounds(0, 0, 1280, 800);

        pnlPrincipal.add(pnlMsjDatafonos, "mensajesDatafono");

        pnlMensajeGopass.setLayout(null);

        jCerrar1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jCerrar1.setForeground(new java.awt.Color(153, 0, 0));
        jCerrar1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jCerrar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/boton-blanco-2.png"))); // NOI18N
        jCerrar1.setText("CERRAR");
        jCerrar1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jCerrar1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jCerrar1MouseClicked(evt);
            }
        });
        pnlMensajeGopass.add(jCerrar1);
        jCerrar1.setBounds(990, 18, 249, 53);

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(186, 12, 47));
        lblTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitulo.setText("GOPASS");
        pnlMensajeGopass.add(lblTitulo);
        lblTitulo.setBounds(550, 310, 390, 40);

        lblFooter.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblFooter.setForeground(new java.awt.Color(186, 12, 47));
        lblFooter.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFooter.setText("PLACA");
        pnlMensajeGopass.add(lblFooter);
        lblFooter.setBounds(530, 430, 430, 40);

        lblMsg.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        lblMsg.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMsg.setText("RESPUESTA TRANSACCION");
        pnlMensajeGopass.add(lblMsg);
        lblMsg.setBounds(530, 360, 430, 70);

        fndIcono.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fndIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/iconoCheck.png"))); // NOI18N
        pnlMensajeGopass.add(fndIcono);
        fndIcono.setBounds(380, 330, 120, 120);

        fndMensaje.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fndMensaje.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndPanelMensaje.png"))); // NOI18N
        pnlMensajeGopass.add(fndMensaje);
        fndMensaje.setBounds(116, 220, 1080, 350);

        fndFondo1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fndFondo1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        fndFondo1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fndFondo1.setMaximumSize(new java.awt.Dimension(1280, 800));
        fndFondo1.setMinimumSize(new java.awt.Dimension(1280, 800));
        fndFondo1.setPreferredSize(new java.awt.Dimension(1280, 800));
        fndFondo1.setRequestFocusEnabled(false);
        pnlMensajeGopass.add(fndFondo1);
        fndFondo1.setBounds(0, 0, 1280, 800);

        pnlPrincipal.add(pnlMensajeGopass, "pnlMensajeGopass");

        getContentPane().add(pnlPrincipal);
        pnlPrincipal.setBounds(0, 0, 1280, 800);
        pnlPrincipal.getAccessibleContext().setAccessibleName("");

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jComboMostrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboMostrarActionPerformed
        NovusUtils.beep();
        Async(() -> {
            refrescarDatos("BOTON REFRESCAR");
        });
    }//GEN-LAST:event_jComboMostrarActionPerformed

    private void jComboMostrarItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboMostrarItemStateChanged

    }//GEN-LAST:event_jComboMostrarItemStateChanged

    private void jComboMostrarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jComboMostrarMouseReleased

    }//GEN-LAST:event_jComboMostrarMouseReleased

    private void NOMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_NOMouseClicked
        if (noAceptar != null) {
            noAceptar.run();
        } else {
            mostrarMenuPrincipal(true);
        }
    }//GEN-LAST:event_NOMouseClicked

    private void SI1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SI1MouseClicked
        if (anulacion != null) {
            anulacion.run();
        } else {
            handler.run();
            mostrarMenuPrincipal(true);
        }

    }//GEN-LAST:event_SI1MouseClicked

    private void fndMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fndMousePressed

    }//GEN-LAST:event_fndMousePressed

    private void jLabel13MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel13MouseReleased
        cerrar();
    }//GEN-LAST:event_jLabel13MouseReleased

    private void jLabel14MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel14MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel14MousePressed

    private void jCerrarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCerrarMouseClicked
        ejecutarAccionDeCerrado = false;
        closeMensajeDatafono();
    }//GEN-LAST:event_jCerrarMouseClicked

    private void jMedioMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMedioMouseReleased

    }//GEN-LAST:event_jMedioMouseReleased

    private void jCerrar1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCerrar1MouseClicked
        closeMensajeDatafono();
    }//GEN-LAST:event_jCerrar1MouseClicked

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jTable1MouseClicked

    public void closeMensajeDatafono() {
        mostrarMenuPrincipal(false);
        if (timer != null) {
            timer.stop();
        }
    }

    private void jLabel1MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel1MousePressed
        NovusUtils.beep();
        Async(() -> {
            refrescarDatos("BOTON REFRESCAR");
        });

    }// GEN-LAST:event_jLabel1MousePressed

    // GEN-LAST:event_jImprimirMousePressed
    private void jMedioMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jMedioMousePressed

    }// GEN-LAST:event_jMedioMousePressed

    private void jFacturaMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jFacturaMousePressed

    }// GEN-LAST:event_jFacturaMousePressed

    private void jLabel7MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel7MouseReleased
        NovusUtils.beep();
        cerrar();
    }// GEN-LAST:event_jLabel7MouseReleased

    private void jAnularMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jAnularMouseReleased

    }// GEN-LAST:event_jAnularMouseReleased

    private void jLabel4MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel4MousePressed

    }// GEN-LAST:event_jLabel4MousePressed

    private void jLabel4MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel4MouseReleased

    }// GEN-LAST:event_jLabel4MouseReleased

    private void jLabel4MouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel4MouseClicked
        cargarTablaHIstorialVentas(accionesDeVentas, "accionesVentasHistorialMod2");
    }// GEN-LAST:event_jLabel4MouseClicked

    // GEN-LAST:event_jRecuperacionMouseReleased
    private void cerrar() {
        if (timer != null) {
            timer.stop();
        }
        timer = null;
        InfoViewController.NotificadorVentasHistorial = null;
        parent.cargarNotificador();
        if (lista != null) {
            lista.clear();
        }
        lista = null;
        reciboFidelizar = null;
        estadoActulizar = false;
        estadoActulizarDatafono = false;
        System.gc();
        if (runnable != null) {
            runnable.run();
        }
        parent.mostrarSubPanel(new VentaMenuPanelController(parent, parent));
        dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel NO;
    private javax.swing.JLabel SI1;
    private javax.swing.JPanel accionesDeVentas;
    private javax.swing.ButtonGroup buttonGroup1;
    public static javax.swing.JPanel cardVentas;
    private javax.swing.JPanel card_mensaje;
    private javax.swing.JLabel fnd;
    private javax.swing.JLabel fndFondo;
    private javax.swing.JLabel fndFondo1;
    private javax.swing.JLabel fndIcono;
    private javax.swing.JLabel fndMensaje;
    public static javax.swing.JLabel jCerrar;
    public static javax.swing.JLabel jCerrar1;
    private javax.swing.JComboBox<String> jComboMostrar;
    private javax.swing.JComboBox<PersonaBean> jComboPromotor;
    private javax.swing.JLabel jIcono;
    private javax.swing.JLabel jInfoDatafono;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    public javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jNotificacion;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel jTitulo;
    private javax.swing.JLabel jTransacciones;
    private ClockViewController jclock;
    private javax.swing.JLabel jpromotor;
    private javax.swing.JLabel jpromotor1;
    private javax.swing.JLabel jpromotor2;
    private javax.swing.JLabel jpromotor3;
    private javax.swing.JLabel jpromotor4;
    private javax.swing.JLabel lblFooter;
    private javax.swing.JLabel lblMsg;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlMensajeGopass;
    private javax.swing.JPanel pnlMsjDatafonos;
    private javax.swing.JPanel pnlPrincipal;
    private javax.swing.JPanel pnlPromotor;
    private javax.swing.JPanel pnlReporteVentas;
    private javax.swing.JPanel pnl_confirmacion;
    // End of variables declaration//GEN-END:variables

    public void refrescarDatos(String origen) {
        int limite = 1000;
        try {
            if (!jComboMostrar.getSelectedItem().toString().toLowerCase().contains("todos")) {
                limite = Integer.parseInt(jComboMostrar.getSelectedItem().toString().trim());
            }
        } catch (NumberFormatException e) {
            Logger.getLogger(VentasAppterpellHistorialView.class.getName()).log(Level.SEVERE, null, e);
        }
        PersonaBean person = (PersonaBean) jComboPromotor.getSelectedItem();
        if (person != null) {
            PersonaBean promotor = null;
            if (person.getId() != 0) {
                promotor = person;
            }
            if (promotor != null) {
                Main.persona = promotor;
                jpromotor.setText(Main.persona.getNombre());
            } else {
                jpromotor.setText("TODOS");
            }
            if (lista != null) {
                lista.clear();
            }

            long promotorId = promotor == null ? 0L : promotor.getId();
            renderSales(promotorId, getJornadasByGroupUseCase.execute(), limite);
            AsignacionClienteBean.getDatosCliente().clear();
        }
    }

    private void Async(Runnable runnable) {
        new Thread(() -> {
            try {
                Thread.sleep(100);
                runnable.run();
            } catch (InterruptedException e) {
                Logger.getLogger(VentasAppterpellHistorialView.class
                        .getName()).log(Level.SEVERE, null, e);
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void Async(Runnable runnable, int seconds) {
        new Thread(() -> {
            try {
                Thread.sleep(1000 * seconds);
                runnable.run();
            } catch (InterruptedException e) {
                Logger.getLogger(VentasAppterpellHistorialView.class
                        .getName()).log(Level.SEVERE, null, e);
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    //Promotores
    private void selectmeAnulacion(long id) {
        NotasCredito nota = new NotasCredito();
        Runnable panelPrincipal = () -> refrescarDatos("BOTON REFRESCAR");
        showPanel("pnl_principal");
        nota.enviar(notaP, parent, this, true, panelPrincipal, id);
    }

    //Un Promotor
    private void selectmeAnulacion() {
        NotasCredito nota = new NotasCredito();
        Runnable panelPrincipal = () -> refrescarDatos("BOTON REFRESCAR");
        showPanel("pnl_principal");
        nota.enviar(notaP, parent, this, true, panelPrincipal);
    }

    //Se muestran los Promotores con Turnos Iniciados
    private void renderizarUsuarios() {
        try {
            int i = 0;
            int height = 97;
            for (PersonaBean personaT : InfoViewController.turnosPersonas) {
                PromotoresVentas item = new PromotoresVentas(personaT);
                item.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseReleased(java.awt.event.MouseEvent evt) {
                        id = personaT.getId();
                        selectmeAnulacion(id);
                    }
                });
                jPanel4.add(item);
                item.setBounds(10, (i * height) + (5 * (i + 1)), 763, height);
                i++;
            }
        } catch (Exception ex) {
            Logger.getLogger(VentasAppterpellHistorialView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void volverMenu() {
        if (ventaPlaca != null) {
            ventaPlaca.setVisible(false);
            ventaPlaca.dispose();
            pnlPrincipal.getLayout().removeLayoutComponent(ventaPlaca);
            ventaPlaca = null;
        }
        refrescar = null;
        cerrar();
        mostrarMenuPrincipal(true);
    }

    public void renderSales(long personaId, long jornadaId, int rango) {
        try {

            DefaultTableModel dm = (DefaultTableModel) jTable1.getModel();
            dm.getDataVector().removeAllElements();
            dm.fireTableDataChanged();

            DefaultTableModel defaultModel = (DefaultTableModel) jTable1.getModel();

            Map<Long, VentasSinResolverFE> renderizarClientesSinClientes = new TreeMap<>();
            RecoverPaymentProcessedAppterpel recoverPaymentProcessedAppterpel = SingletonMedioPago.ConetextDependecy.getRecoverPaymentProcessedAppterpel();

            PaymentProcessedParams params = new PaymentProcessedParams();
            params.setIdJornada(jornadaId);
            params.setIdPromotor(personaId);
            params.setRange_register(rango);
            List<Sales> sales = recoverPaymentProcessedAppterpel.execute(params);

            for (Sales sale : sales) {
                String consecutivo;
                String prefijo = "";

                prefijo = sale.getAtributos().getConsecutive().getPrefijo();
                prefijo = prefijo.equals("") ? "" : prefijo.concat("-");
                consecutivo = sale.getAtributos().getConsecutive().getConsecutivoActual() + "";

                try {
                    defaultModel.addRow(new Object[]{
                        sale.getNumero(),
                        prefijo.concat(consecutivo),
                        sale.getFecha().format(DateTimeFormatter.ofPattern("d-MM hh:mm a")),
                        sale.getProducto(),
                        sale.getOperador(),
                        sale.getStatus(),
                        sale.getCara(),
                        String.format("%.3f", sale.getCantidad()) + sale.getUnidadMedida(),
                        "$ " + df.format(sale.getTotal())});

                } catch (NumberFormatException e) {
                    Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, e);
                }
            }
            jTable1.setModel(defaultModel);
            jTable1.clearSelection();

            RenderTablaColor colroTabla = new RenderTablaColor();
            colroTabla.datos(renderizarClientesSinClientes);
            jTable1.setDefaultRenderer(Object.class, colroTabla);
        } catch (Exception ex) {
            Logger.getLogger(VentasAppterpellHistorialView.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    void cargarPromotores() {

        jComboPromotor.removeAllItems();

        PersonaBean p = new PersonaBean();
        p.setNombre("TODOS");
        p.setId(0);

        jComboPromotor.addItem(p);
        for (PersonaBean persona : InfoViewController.turnosPersonas) {
            jComboPromotor.addItem(persona);
            if (Main.persona.getId() == persona.getId()) {
                jComboPromotor.setSelectedItem(persona);
                Async(() -> {
                    refrescarDatos("PROMOTOR CARGADO");
                });
            }
        }

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

    private void mostrarMenuPrincipal(boolean autoCerrar) {
        CardLayout layout = (CardLayout) pnlPrincipal.getLayout();
        String panel = NovusConstante.ventanaFE ? "pnl_ext" : "pnl_principal";
        layout.show(pnlPrincipal, panel);
        InfoViewController.NotificadorVentasHistorial = new Notificador() {
            @Override
            public void send(JsonObject data) {
                mostrarPanelMensaje(data.get("mensaje").getAsString(), data, autoCerrar);
            }
        };

    }

    public void setTimeout(int delay, Runnable runnable) {
        new Thread(() -> {
            try {
                Thread.sleep((long) delay * 1000);
                runnable.run();
            } catch (InterruptedException e) {
                NovusUtils.printLn("Interrupted.setTimeout" + e.getLocalizedMessage());
                // Restore interrupted state...
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public void setTimeoutMensajeDatafono(int delay, Runnable runnable) {
        new Thread(() -> {
            try {
                Thread.sleep((long) delay * 1000);
                if (ejecutarAccionDeCerrado) {
                    runnable.run();
                }
                ejecutarAccionDeCerrado = true;
            } catch (InterruptedException e) {
                NovusUtils.printLn("Interrupted.setTimeout" + e.getLocalizedMessage());
                // Restore interrupted state...
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void confirmar(String text) {
        CardLayout card = (CardLayout) pnlPrincipal.getLayout();
        card.show(pnlPrincipal, "pnl_confirmacion");
        jLabel9.setText(text.toUpperCase());
    }

    public void cargarTablaHIstorialVentas(JPanel panel, String panelVenta) {
        CardLayout cartd = (CardLayout) panel.getLayout();
        cartd.show(panel, panelVenta);
    }

}
