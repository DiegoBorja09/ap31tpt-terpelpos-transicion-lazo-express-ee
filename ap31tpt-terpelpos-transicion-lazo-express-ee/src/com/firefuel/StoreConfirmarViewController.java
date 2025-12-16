package com.firefuel;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.WT2.appTerpel.domain.valueObject.MediosPagosDescription;
import com.WT2.loyalty.application.UseCase.ObtenerBonosRedencion;
import com.WT2.loyalty.domain.entities.beans.IdentificationClient;
import com.WT2.loyalty.domain.entities.response.RespuestasAcumulacion;
import com.application.useCases.ventas.SetReaperturaEnUnoUseCase;
import com.application.useCases.ventas.VentaEnCursoUseCase;
import com.application.useCases.persons.IsAdminUseCase;
import com.application.useCases.movimientosdetalles.GetByProductoMovimientoDetalleUseCase;
import com.bean.BonoViveTerpel;
import com.bean.MediosPagosBean;
import com.bean.MovimientosBean;
import com.bean.MovimientosDetallesBean;
import com.bean.Notificador;
import com.bean.PersonaBean;
import com.bean.ProductoBean;
import com.bean.Recibo;
import com.controllers.ClientWSAsync;

import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.EquipoDao;
import com.dao.MovimientosDao;
import com.dao.PersonasDao;
import com.dao.SetupDao;
import com.dao.fidelizacionDAO.FidelizacionDao;
import com.facade.fidelizacion.AcumulacionFidelizacion;
import com.facade.PedidoFacade;
import com.firefuel.facturacion.electronica.ConfiguracionFE;
import com.firefuel.facturacion.electronica.FacturacionElectronica;
import com.firefuel.utils.ShowMessageSingleton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.neo.print.services.PrinterFacade;
import com.utils.enums.TipoNegociosFidelizacion;
import java.awt.CardLayout;
import java.awt.Color;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
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

import jakarta.persistence.PersistenceException;
import teclado.view.common.TecladoNumericoGray;
import com.application.useCases.wacherparametros.FindByParameterUseCase;
import com.application.commons.CtWacherParametrosEnum;


public class StoreConfirmarViewController extends javax.swing.JDialog {

    JsonObject respuestaCortesia = new JsonObject();
    long cambio = 0;
    int selectedRow = -1;
    int idMovimiento = 0;
    float recibido = 0;
    JFrame parent;
    InfoViewController principal;
    boolean facturacionPOS = false;
    boolean existeCortesia;
    FindByParameterUseCase findByParameterUseCase = new FindByParameterUseCase(CtWacherParametrosEnum.CODIGO.getColumnName(), "REMISION");
    boolean isNewVenta;
    boolean isFac = false;
    MovimientosBean movimiento = new MovimientosBean();
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY_WITHOUT_ZERO);
    DefaultTableCellRenderer textRight;
    DefaultTableCellRenderer textCenter;
    ArrayList<MediosPagosBean> mediosPagosDisponibles;
    ArrayList<MediosPagosBean> mediosPagoVenta = new ArrayList<>();
    public static int CONSECUTIVO = 0;
    public static String RESOLUCION;
    public static String ESTADO;
    public static int ID_CONSECUTIVO;
    public static int CONSECUTIVO_ACTUAL;
    MovimientosDao mdao = new MovimientosDao();
    float totalVenta;
    float totalOtros;
    JsonObject respuestaFactura = new JsonObject();
    JsonObject respuestaFacturaCortesia = new JsonObject();
    public static boolean CANASTILLA;
    public boolean isConsumoPropio;
    long consumoPropio;
    boolean cortesia = false;
    public static Notificador notificadorView = null;
    static final Logger logger = Logger.getLogger(StoreConfirmarViewController.class.getName());
    Runnable pasar;
    Runnable noPasar;
    Timer timer = null;
    int time = 0;
    boolean manual = false;
    boolean ventanaVenta = false;
    PersonaBean persona = Main.persona;
    IsAdminUseCase isAdminUseCase = new IsAdminUseCase();
    PersonasDao pdao = new PersonasDao();
    ConfiguracionFE configuracionFE = new ConfiguracionFE();
    private static IdentificationClient datosCliente;

    FacturacionManualKCView fcManual;
    JsonArray datafonos = new JsonArray();
    EquipoDao edao = new EquipoDao();

    public static final String PANEL_MEDIOS_PAGOS = "pnl_cambio_medio";
    public static final String PANEL_VALIDACION_BONO = "pnl_validar_bono";
    JsonObject dataViveTerpel = null;

    public StoreConfirmarViewController(JFrame parent, boolean modal, MovimientosBean movimiento, boolean isNewVenta, boolean isFac) {
        super(parent, modal);
        this.parent = parent;
        initComponents();
        this.isNewVenta = isNewVenta;
        this.isFac = isFac;
        this.movimiento = movimiento;
        this.init();
    }

    public StoreConfirmarViewController(JFrame parent, InfoViewController principal, boolean modal, MovimientosBean movimiento, boolean isNewVenta, boolean isFac) {
        super(parent, modal);
        this.parent = parent;
        this.principal = principal;
        initComponents();
        this.isNewVenta = isNewVenta;
        this.isFac = isFac;
        this.movimiento = movimiento;
        this.init();
    }

    public StoreConfirmarViewController(JFrame parent, InfoViewController principal, boolean modal, MovimientosBean movimiento, boolean isNewVenta, boolean isFac, boolean manual, boolean ventanaVenta) {
        super(parent, modal);
        this.parent = parent;
        this.principal = principal;
        initComponents();
        this.isNewVenta = isNewVenta;
        this.isFac = isFac;
        this.movimiento = movimiento;
        this.manual = manual;
        this.ventanaVenta = ventanaVenta;
        this.init();
    }

    public void setRespuestaCortesia(JsonObject respuestaCortesia) {
        this.respuestaCortesia = respuestaCortesia;
    }

    private void init() {
        FacturacionElectronica fac = new FacturacionElectronica();
        ajustarPosicionBoton(CANASTILLA);
        jCortesias.setVisible(false);
        boolean aplicaFE = fac.aplicaFE();
        if (!aplicaFE && !configuracionFE.remisionHabilitada()) {
            jfacturacion.setVisible(false);
            jCortesias.setVisible(false);
        } else {
            System.out.println(" estoy habilitado " + aplicaFE);
            jfacturacion.setVisible(true);
            jCortesias.setVisible(false);
        }
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);

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

        /*   TextPrompt placeholder = new TextPrompt("Cod. voucher", jmedioVoucher);
        placeholder.changeAlpha(0.95f); */
        this.movimiento.setVentaTotal((float) ((this.movimiento.getVentaTotal() * 100) / 100.0));
        jtotal_venta.setText("$ " + df.format(this.movimiento.getVentaTotal()));
        totalVenta = this.movimiento.getVentaTotal();
        cambio = 0;
        selectedRow = -1;
        this.movimiento.setRecibidoTotal(this.movimiento.getVentaTotal());
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
        if (this.mediosPagoVenta.isEmpty()) {
            jmedioCantidad.setText(df.format(this.movimiento.getVentaTotal()));
        }
        notificadorView = this::recibir;

        cambiarBotones(fac.isDefaultFe());
    }

    public void cambiarBotones(boolean isDefault) {
        if (isDefault) {
            jfacturacion.setText("FACTURA POS");
        }
    }

    void recibir(JsonObject data) {
        if (data.has("alertas") && data.get("alertas").getAsBoolean()) {
            time = 5000;
            mostrarAlertas(data.get("mensajeError").getAsString(), data.get("icono").getAsString());
        } else {
            if (data.get("autoclose").getAsBoolean()) {
                showMessage(data.get("mensajeError").getAsString(),
                        data.get("icono").getAsString(),
                        data.get("habilitar").getAsBoolean(),
                        this::mostrarMenuPrincipal,
                        data.get("autoclose").getAsBoolean(),
                        LetterCase.FIRST_UPPER_CASE);
            } else {
                showMessage(data.get("mensajeError").getAsString(),
                        data.get("icono").getAsString(),
                        false, null, false, LetterCase.FIRST_UPPER_CASE);
            }
        }

    }

    public static void setDatosCliente(IdentificationClient datosClienteResponse) {
        datosCliente = datosClienteResponse;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlPrincipal = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jMensajes = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jfondoMensajes = new javax.swing.JLabel();
        jeliminar_medio = new javax.swing.JLabel();
        jeliminar_medio1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jtotal_venta = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jmedioPagoCombo = new javax.swing.JComboBox<>();
        jmedioCantidad = new javax.swing.JTextField();
        jmedioVoucher = new javax.swing.JTextField();
        jPanel1 = new TecladoNumericoGray() ;
        jLabel11 = new javax.swing.JLabel();
        jbutton_addMedio = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jguardarVenta = new javax.swing.JLabel();
        jfacturacion = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtableMediosPago = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jrecibido_venta = new javax.swing.JLabel();
        jcambio_venta = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        btnFidelizacion = new javax.swing.JLabel();
        jCortesias = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jNotificacion = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        alertas_consecutivos = new javax.swing.JPanel();
        jIcono = new javax.swing.JLabel();
        jCerrar = new javax.swing.JLabel();
        jMensaje = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(null);

        pnlPrincipal.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnlPrincipal.setLayout(new java.awt.CardLayout());

        jPanel3.setLayout(null);

        jMensajes.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jPanel3.add(jMensajes);
        jMensajes.setBounds(110, 660, 590, 60);

        jLabel10.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("CONFIRMACIÓN DE VENTA");
        jPanel3.add(jLabel10);
        jLabel10.setBounds(100, 5, 710, 75);

        jfondoMensajes.setFont(new java.awt.Font("Roboto", 0, 24)); // NOI18N
        jPanel3.add(jfondoMensajes);
        jfondoMensajes.setBounds(0, 650, 710, 70);

        jeliminar_medio.setFont(new java.awt.Font("Bebas Neue", 0, 20)); // NOI18N
        jeliminar_medio.setForeground(new java.awt.Color(255, 255, 255));
        jeliminar_medio.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jeliminar_medio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png"))); // NOI18N
        jeliminar_medio.setText("BORRAR");
        jeliminar_medio.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jeliminar_medio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jeliminar_medioMouseReleased(evt);
            }
        });
        jPanel3.add(jeliminar_medio);
        jeliminar_medio.setBounds(470, 580, 174, 80);

        jeliminar_medio1.setFont(new java.awt.Font("Bebas Neue", 0, 20)); // NOI18N
        jeliminar_medio1.setForeground(new java.awt.Color(255, 255, 255));
        jeliminar_medio1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jeliminar_medio1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jeliminar_medio1.setText("QUITAR TODOS");
        jeliminar_medio1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jeliminar_medio1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jeliminar_medio1MouseReleased(evt);
            }
        });
        jPanel3.add(jeliminar_medio1);
        jeliminar_medio1.setBounds(280, 580, 173, 80);

        jLabel5.setFont(new java.awt.Font("Conthrax", 0, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(102, 102, 102));
        jLabel5.setText("RECIBIDO:");
        jPanel3.add(jLabel5);
        jLabel5.setBounds(60, 200, 250, 40);

        jtotal_venta.setFont(new java.awt.Font("Conthrax", 0, 36)); // NOI18N
        jtotal_venta.setForeground(new java.awt.Color(186, 12, 47));
        jtotal_venta.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jtotal_venta.setText("$ 0");
        jPanel3.add(jtotal_venta);
        jtotal_venta.setBounds(310, 140, 340, 60);

        jLabel4.setFont(new java.awt.Font("Conthrax", 0, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(102, 102, 102));
        jLabel4.setText("MEDIO PAGO:");
        jPanel3.add(jLabel4);
        jLabel4.setBounds(60, 250, 250, 50);

        jmedioPagoCombo.setFont(new java.awt.Font("Bebas Neue", 0, 30)); // NOI18N
        jmedioPagoCombo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(186, 12, 47), 1, true));
        jmedioPagoCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmedioPagoComboActionPerformed(evt);
            }
        });
        jPanel3.add(jmedioPagoCombo);
        jmedioPagoCombo.setBounds(350, 250, 300, 50);

        jmedioCantidad.setFont(new java.awt.Font("Bebas Neue", 0, 36)); // NOI18N
        jmedioCantidad.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jmedioCantidad.setText("0");
        jmedioCantidad.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jmedioCantidadFocusGained(evt);
            }
        });
        jmedioCantidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmedioCantidadActionPerformed(evt);
            }
        });
        jmedioCantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jmedioCantidadKeyTyped(evt);
            }
        });
        jPanel3.add(jmedioCantidad);
        jmedioCantidad.setBounds(350, 320, 250, 50);

        jmedioVoucher.setFont(new java.awt.Font("Bebas Neue", 0, 36)); // NOI18N
        jmedioVoucher.setForeground(new java.awt.Color(51, 51, 51));
        jmedioVoucher.setToolTipText("");
        jmedioVoucher.setBorder(null);
        jmedioVoucher.setFocusable(false);
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
        jPanel3.add(jmedioVoucher);
        jmedioVoucher.setBounds(52, 322, 270, 45);
        jPanel3.add(jPanel1);
        jPanel1.setBounds(705, 220, 550, 470);

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel11MouseReleased(evt);
            }
        });
        jPanel3.add(jLabel11);
        jLabel11.setBounds(10, 10, 70, 71);

        jbutton_addMedio.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jbutton_addMedio.setForeground(new java.awt.Color(255, 255, 255));
        jbutton_addMedio.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jbutton_addMedio.setText("+");
        jbutton_addMedio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jbutton_addMedioMouseReleased(evt);
            }
        });
        jPanel3.add(jbutton_addMedio);
        jbutton_addMedio.setBounds(600, 320, 50, 50);

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-xsmall.png"))); // NOI18N
        jPanel3.add(jLabel6);
        jLabel6.setBounds(600, 320, 50, 50);

        jguardarVenta.setFont(new java.awt.Font("Bebas Neue", 0, 20)); // NOI18N
        jguardarVenta.setForeground(new java.awt.Color(255, 255, 255));
        jguardarVenta.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jguardarVenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jguardarVenta.setText("GUARDAR VENTA");
        jguardarVenta.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jguardarVenta.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jguardarVentaMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jguardarVentaMouseReleased(evt);
            }
        });
        jPanel3.add(jguardarVenta);
        jguardarVenta.setBounds(840, 730, 173, 54);

        jfacturacion.setFont(new java.awt.Font("Bebas Neue", 0, 20)); // NOI18N
        jfacturacion.setForeground(new java.awt.Color(255, 255, 255));
        jfacturacion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jfacturacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jfacturacion.setText("F. ELECTRONICA");
        jfacturacion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jfacturacion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jfacturacionMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jfacturacionMouseReleased(evt);
            }
        });
        jPanel3.add(jfacturacion);
        jfacturacion.setBounds(450, 730, 180, 54);

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

        jPanel3.add(jScrollPane1);
        jScrollPane1.setBounds(50, 400, 600, 180);

        jLabel7.setFont(new java.awt.Font("Conthrax", 0, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(102, 102, 102));
        jLabel7.setText("TOTAL: ");
        jPanel3.add(jLabel7);
        jLabel7.setBounds(60, 140, 250, 60);

        jLabel8.setFont(new java.awt.Font("Conthrax", 0, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(102, 102, 102));
        jLabel8.setText("CAMBIO:");
        jPanel3.add(jLabel8);
        jLabel8.setBounds(700, 140, 160, 60);

        jrecibido_venta.setFont(new java.awt.Font("Conthrax", 0, 36)); // NOI18N
        jrecibido_venta.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jrecibido_venta.setText("$ 0");
        jPanel3.add(jrecibido_venta);
        jrecibido_venta.setBounds(310, 200, 340, 40);

        jcambio_venta.setFont(new java.awt.Font("Conthrax", 0, 36)); // NOI18N
        jcambio_venta.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jcambio_venta.setText("$ 0");
        jcambio_venta.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jcambio_ventaMouseReleased(evt);
            }
        });
        jPanel3.add(jcambio_venta);
        jcambio_venta.setBounds(950, 140, 280, 60);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel3.add(jLabel25);
        jLabel25.setBounds(1120, 710, 10, 80);

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/alerta.png"))); // NOI18N
        jPanel3.add(jLabel9);
        jLabel9.setBounds(0, 700, 80, 100);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel3.add(jLabel28);
        jLabel28.setBounds(80, 713, 3, 80);

        btnFidelizacion.setFont(new java.awt.Font("Bebas Neue", 0, 20)); // NOI18N
        btnFidelizacion.setForeground(new java.awt.Color(255, 255, 255));
        btnFidelizacion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnFidelizacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        btnFidelizacion.setText("FIDELIZAR");
        btnFidelizacion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFidelizacion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnFidelizacionMouseClicked(evt);
            }
        });
        jPanel3.add(btnFidelizacion);
        btnFidelizacion.setBounds(650, 730, 173, 54);

        jCortesias.setFont(new java.awt.Font("Bebas Neue", 0, 20)); // NOI18N
        jCortesias.setForeground(new java.awt.Color(255, 255, 255));
        jCortesias.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jCortesias.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jCortesias.setText("CORTESIAS");
        jCortesias.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jCortesias.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jCortesiasMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jCortesiasMouseReleased(evt);
            }
        });
        jPanel3.add(jCortesias);
        jCortesias.setBounds(260, 730, 173, 54);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("NÚMERO DE RECIBO");
        jPanel3.add(jLabel2);
        jLabel2.setBounds(50, 370, 270, 20);

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        jPanel3.add(jNotificacion);
        jNotificacion.setBounds(670, 10, 590, 60);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_confirmar.png"))); // NOI18N
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel1.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel3.add(jLabel1);
        jLabel1.setBounds(0, 0, 1280, 800);

        pnlPrincipal.add(jPanel3, "pnl_principal");

        alertas_consecutivos.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jIcono.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jIcono.setForeground(new java.awt.Color(186, 12, 47));
        jIcono.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        alertas_consecutivos.add(jIcono, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 220, 410, 250));

        jCerrar.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jCerrar.setForeground(new java.awt.Color(255, 255, 255));
        jCerrar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"))); // NOI18N
        jCerrar.setText("CERRAR");
        jCerrar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jCerrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jCerrarMouseClicked(evt);
            }
        });
        alertas_consecutivos.add(jCerrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 500, 290, 60));

        jMensaje.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jMensaje.setForeground(new java.awt.Color(186, 12, 47));
        jMensaje.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jMensaje.setText("OK");
        alertas_consecutivos.add(jMensaje, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 220, 810, 250));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        alertas_consecutivos.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1280, 800));

        pnlPrincipal.add(alertas_consecutivos, "alertas");

        getContentPane().add(pnlPrincipal);
        pnlPrincipal.setBounds(0, 0, 1280, 800);

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jmedioCantidadKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jmedioCantidadKeyTyped
        int cantidadPermitida = jtotal_venta.getText().replace("$ ", "").length() + 1;
        String caracteresAceptados = "[0-9.]";
        NovusUtils.limitarCarateres(evt, jmedioCantidad, cantidadPermitida, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_jmedioCantidadKeyTyped

    private void jmedioVoucherKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jmedioVoucherKeyTyped
        String caracteresAceptados = "[0-9]";
        NovusUtils.limitarCarateres(evt, jmedioVoucher, 10, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_jmedioVoucherKeyTyped

    private void jmedioVoucherFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jmedioVoucherFocusGained
        NovusUtils.deshabilitarCopiarPegar(jmedioVoucher);
    }//GEN-LAST:event_jmedioVoucherFocusGained

    private void jmedioCantidadFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jmedioCantidadFocusGained
        NovusUtils.deshabilitarCopiarPegar(jmedioCantidad);

    }//GEN-LAST:event_jmedioCantidadFocusGained

    private void jcambio_ventaMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jcambio_ventaMouseReleased
        cargarRestante();
    }
    private void jguardarVentaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jguardarVentaMouseReleased
        FacturacionElectronica fac = new FacturacionElectronica();
        boolean isObligatorio = false;
        boolean error = false;
        if ((fac.aplicaFE() || fac.remisionActiva())) {
            if (existeCortesia) {
                float venta = respuestaFacturaCortesia.get("venta").getAsJsonObject().get("ventaTotal").getAsFloat();
                venta = venta - respuestaFacturaCortesia.get("venta").getAsJsonObject().get("descuentoTotal").getAsFloat();
                JsonObject data = fac.montoMinimoobligatorioFE(venta);
                isObligatorio = data.get("obligatorio").getAsBoolean();
                error = data.get("error").getAsBoolean();
                movimiento.setVentaTotal(venta);
            } else {
                JsonObject data = fac.montoMinimoobligatorioFE(movimiento.getVentaTotal());
                isObligatorio = data.get("obligatorio").getAsBoolean();
                error = data.get("error").getAsBoolean();
            }
            boolean isDefault = fac.isDefaultFe();
            if (isDefault) {
                if (consumoPropio == 20001) {
                    isConsumoPropio = true;
                    AutorizacionView auto = new AutorizacionView(this.principal, true, this, isConsumoPropio, this, true);
                    auto.setAlwaysOnTop(true);
                    auto.setVisible(true);
                } else {
                    isConsumoPropio = false;
                    enviarFacturaElectronica();
                }
            } else {
                ventaPos(isObligatorio, error);
            }
        } else {
            if (consumoPropio == 20001) {

                Long id = persona.getId();
                NovusUtils.printLn("Id: " + id + ">>>>>>>>>");
                //NovusUtils.printLn("isAdmin :" + pdao.isAdmin(id));
                NovusUtils.printLn("isAdmin :" + isAdminUseCase.execute(id));

                //if (pdao.isAdmin(id)) {
                if (isAdminUseCase.execute(id)) {
                    isConsumoPropio = true;
                    ventaGuardada();
                } else {
                    System.out.println(Main.ANSI_GREEN + consumoPropio + Main.ANSI_RESET);
                    isConsumoPropio = true;
                    this.parent.setVisible(false);
                    AutorizacionView auto = new AutorizacionView(this.principal, true, this, isConsumoPropio, this, false);
                    auto.setVisible(true);
                    dispose();
                }

            } else {
                isConsumoPropio = false;
                ventaGuardada();
            }
            consumoPropio = 0;
        }
    }//GEN-LAST:event_jguardarVentaMouseReleased

    public void ventaPos(boolean isObligatorio, boolean error) {
        if (isObligatorio || NovusConstante.VENTAS_CONTINGENCIA || error) {
            String mensaje = error ? "ERROR EN LA CONFIGURACIÓN DE LA OBLIGATORIEDAD" : "TRANSACCIÓN OBLIGATORIA PARA FACTURACIÓN ELECTRÓNICA";
            showMessage(mensaje, "/com/firefuel/resources/btBad.png",
                    true, this::mostrarMenuPrincipal, true, LetterCase.FIRST_UPPER_CASE);
        } else {
            if (consumoPropio == 20001) {
                Long id = persona.getId();
                NovusUtils.printLn("Id: " + id + ">>>>>>>>>");
                NovusUtils.printLn("isAdmin :" + isAdminUseCase.execute(id));

                //if (pdao.isAdmin(id)) {
                if (isAdminUseCase.execute(id)) {
                    isConsumoPropio = true;
                    ventaGuardada();
                } else {
                    System.out.println(Main.ANSI_GREEN + consumoPropio + Main.ANSI_RESET);
                    isConsumoPropio = true;
                    AutorizacionView auto = new AutorizacionView(this.principal, true, this, isConsumoPropio, this, false);
                    this.parent.setVisible(false);
                    this.setVisible(false);
                    auto.setVisible(true);
                    dispose();
                }
            } else {
                isConsumoPropio = false;
                ventaGuardada();
            }
            consumoPropio = 0;
        }
    }

    private void jeliminar_medio1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jeliminar_medio1MouseReleased
        removeTodosMedio();
        jCortesias.setVisible(false);
    }//GEN-LAST:event_jeliminar_medio1MouseReleased

    private void jeliminar_medioMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jeliminar_medioMouseReleased
        removeMedio();
    }//GEN-LAST:event_jeliminar_medioMouseReleased

    private void jbutton_addMedioMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbutton_addMedioMouseReleased
        if (jbutton_addMedio.isEnabled()) {
            anadirMedio();
        }
    }//GEN-LAST:event_jbutton_addMedioMouseReleased

    private void jtableMediosPagoMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtableMediosPagoMouseReleased
        seleccionar();
    }//GEN-LAST:event_jtableMediosPagoMouseReleased

    private void jmedioPagoComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmedioPagoComboActionPerformed
        changeMedio();
    }//GEN-LAST:event_jmedioPagoComboActionPerformed

    private void jguardarVentaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jguardarVentaMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jguardarVentaMousePressed

    private void jfacturacionMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jfacturacionMousePressed

    }//GEN-LAST:event_jfacturacionMousePressed

    private void jfacturacionMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jfacturacionMouseReleased
        if (this.cambio >= 0) {
            FacturacionElectronica fact = new FacturacionElectronica();
            JsonObject data = fact.montoMinimoobligatorioFE(movimiento.getVentaTotal());
            boolean isObligatorio = data.get("obligatorio").getAsBoolean();
            boolean error = data.get("error").getAsBoolean();
            if (fact.isDefaultFe()) {
                ventaPos(isObligatorio, error);
            } else {
                if (consumoPropio == 20001) {

                    Long id = persona.getId();
                    System.out.println("Id: " + id + ">>>>>>>>>");
                   // System.out.println("isAdmin :" + pdao.isAdmin(id));
                    System.out.println("isAdmin :" + isAdminUseCase.execute(id));

                    //if (pdao.isAdmin(id)) {
                    if (isAdminUseCase.execute(id)) {
                        isConsumoPropio = true;
                        enviarFacturaElectronica();
                    } else {
                        System.out.println(Main.ANSI_GREEN + consumoPropio + Main.ANSI_RESET);
                        isConsumoPropio = true;
                        AutorizacionView auto = new AutorizacionView(this.principal, true, this, isConsumoPropio, this, true);
                        auto.setAlwaysOnTop(true);
                        auto.setVisible(true);
                    }
                } else {
                    isConsumoPropio = false;
                    enviarFacturaElectronica();
                }
                consumoPropio = 0;
            }
        } else {
            String mensaje = "<html><center>RECIBIDO DEBE SER MAYOR O IGUAL AL TOTAL DE LA VENTA</center></html>";
            showMessage(mensaje, "/com/firefuel/resources/btBad.png",
                    true, this::mostrarMenuPrincipal,
                    true, LetterCase.FIRST_UPPER_CASE);
        }
    }//GEN-LAST:event_jfacturacionMouseReleased

    private void jCortesiasMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCortesiasMousePressed
    }//GEN-LAST:event_jCortesiasMousePressed

    private void jCortesiasMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCortesiasMouseReleased
        abrirCortesia();
    }//GEN-LAST:event_jCortesiasMouseReleased

    private void jCerrarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCerrarMouseClicked
        if (timer != null) {
            timer.stop();
        }
        if (pasar != null) {
            pasar.run();
            cerrarTimer();
        } else {
            pasar = () -> {
                mostrarPanel("pnl_principal");
                cerrarTimer();
            };
        }
        NovusConstante.VENTAS_CONTINGENCIA = false;
    }//GEN-LAST:event_jCerrarMouseClicked

    private void btnFidelizacionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnFidelizacionMouseClicked
        if (btnFidelizacion.isEnabled()) {
            showPanelFidelizacion();
        }

    }//GEN-LAST:event_btnFidelizacionMouseClicked

    private void jmedioCantidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmedioCantidadActionPerformed
        boolean presiono = ((TecladoNumericoGray) jPanel1).isPresiono();
        if (presiono) {
            anadirMedio();
        }
        ((TecladoNumericoGray) jPanel1).setPresiono(false);
    }//GEN-LAST:event_jmedioCantidadActionPerformed

    private void jLabel11MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel11MouseReleased
        cerrar();
    }

    private void jmedioCantidadKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jmedioCantidadKeyReleased
        fix();
    }

    private void jmedioCantidadKeyPressed(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_jmedioCantidadKeyPressed
        fix();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel alertas_consecutivos;
    public javax.swing.JLabel btnFidelizacion;
    private javax.swing.JLabel jCerrar;
    public javax.swing.JLabel jCortesias;
    private javax.swing.JLabel jIcono;
    private javax.swing.JLabel jLabel1;
    public javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jMensaje;
    private javax.swing.JLabel jMensajes;
    private javax.swing.JLabel jNotificacion;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jbutton_addMedio;
    public javax.swing.JLabel jcambio_venta;
    private javax.swing.JLabel jeliminar_medio;
    private javax.swing.JLabel jeliminar_medio1;
    private javax.swing.JLabel jfacturacion;
    private javax.swing.JLabel jfondoMensajes;
    public javax.swing.JLabel jguardarVenta;
    private javax.swing.JTextField jmedioCantidad;
    public javax.swing.JComboBox<String> jmedioPagoCombo;
    private javax.swing.JTextField jmedioVoucher;
    public javax.swing.JLabel jrecibido_venta;
    public javax.swing.JTable jtableMediosPago;
    public javax.swing.JLabel jtotal_venta;
    private javax.swing.JPanel pnlPrincipal;
    // End of variables declaration//GEN-END:variables
    void cargarCliente() {
        cargarVistaCliente();
    }

    void cargarVistaCliente() {
    }

    private void cerrarParent() {
        this.principal.showPanel("home");
        setDatosCliente(null);
    }

    private void cerrar() {
        setDatosCliente(null);
        this.parent.dispose();
        dispose();
    }

    /**
     * 🔄 MIGRADO A SERVICIO PYTHON
     * Impresión de venta desde Store - Confirmación
     * @version 2.0 - Migrado a Python
     */
    void impresionVenta() {
        // 🚀 MIGRACIÓN A SERVICIO PYTHON - INICIO
        NovusUtils.printLn("╔════════════════════════════════════════════════════════════╗");
        NovusUtils.printLn("║  🐍 SERVICIO PYTHON - CONFIRMACIÓN STORE                 ║");
        NovusUtils.printLn("╚════════════════════════════════════════════════════════════╝");
        NovusUtils.printLn("📋 Imprimiendo venta desde confirmación de Store");
        NovusUtils.printLn("   - ID Movimiento: " + this.movimiento.getId());
        NovusUtils.printLn("   - Facturación POS: " + facturacionPOS);
        
        String route = facturacionPOS ? "factura" : "venta";
        String tipoDocumento = facturacionPOS ? "FACTURA" : "VENTA";
        
        TreeMap<String, String> header = new TreeMap<>();
        header.put("Content-type", "application/json; charset=UTF-8");
        header.put("Accept", "application/json");

        String funcion = "IMPRIMIR VENTAS - CONFIRMAR STORE";
        JsonObject json = new JsonObject();
        json.addProperty("identificadorMovimiento", this.movimiento.getId());
        json.addProperty("movement_id", this.movimiento.getId());
        json.addProperty("flow_type", "CONFIRMAR_STORE");
        json.addProperty("report_type", tipoDocumento);
        
        // Body con datos del cliente
        JsonObject bodyJson = new JsonObject();
        bodyJson.addProperty("tipoDocumento", "13");
        bodyJson.addProperty("numeroDocumento", "2222222");
        bodyJson.addProperty("identificadorTipoPersona", 1);
        bodyJson.addProperty("nombreComercial", "CLIENTES VARIOS");
        bodyJson.addProperty("nombreRazonSocial", "CLIENTES VARIOS");
        bodyJson.addProperty("direccionTicket", "");
        bodyJson.addProperty("correoElectronico", "");
        bodyJson.addProperty("ciudad", "BARRANQUILLA");
        bodyJson.addProperty("departamento", "ATLANTICO");
        bodyJson.addProperty("regimenFiscal", "48");
        bodyJson.addProperty("telefonoTicket", "");
        bodyJson.addProperty("tipoResponsabilidad", "R-99-PN");
        bodyJson.addProperty("codigoSAP", "0");
        json.add("body", bodyJson);

        // URL del servicio Python
        String url = NovusConstante.SECURE_CENTRAL_POINT_PRINT_TICKET_SALES;
        NovusUtils.printLn("🌐 URL Servicio Python: " + url);
        NovusUtils.printLn("📤 Tipo: " + tipoDocumento);
        
        ClientWSAsync client = new ClientWSAsync(funcion, url, NovusConstante.POST, json, true, false, header, 10000);
        try {
            client.start();
            JsonObject response = client.getResponse();
            
            if (response != null) {
                NovusUtils.printLn("✅ Respuesta del servicio Python: " + response.toString());
                if (response.has("success") && response.get("success").getAsBoolean()) {
                    NovusUtils.printLn("✅ IMPRESIÓN EXITOSA EN SERVICIO PYTHON - CONFIRMAR STORE");
                } else {
                    String mensaje = response.has("message") ? response.get("message").getAsString() : "Sin mensaje";
                    NovusUtils.printLn("⚠️  Python respondió con error: " + mensaje);
                }
            } else {
                NovusUtils.printLn("⚠️  Sin respuesta del servicio Python (puede estar apagado)");
            }
            
            NovusUtils.printLn("═══════════════════════════════════════════════════════════");
        } catch (Exception e) {
            NovusUtils.printLn("❌ ERROR en impresionVenta (Servicio Python - Store): " + e.getMessage());
            Logger.getLogger(StoreConfirmarViewController.class.getName()).log(Level.SEVERE, "Error impresión Python", e);
            showMessage("ERROR IMPRESION VENTA",
                    "/com/firefuel/resources/btBad.png",
                    true, this::cerrar,
                    true, LetterCase.FIRST_UPPER_CASE);
        }
        // 🚀 MIGRACIÓN A SERVICIO PYTHON - FIN
    }

    public void showMensajes(String texto) {

        jMensajes.setVisible(true);
        jfondoMensajes.setVisible(true);

        if (texto == null) {
            jMensajes.setVisible(false);
            jfondoMensajes.setVisible(false);
            jMensajes.setText("");
            jfondoMensajes.setIcon(null);
        } else {
            jMensajes.setText(texto);
            jfondoMensajes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/notaBorrarExtendido.png")));
            setTimeout(10, () -> {
                jMensajes.setVisible(false);
                jfondoMensajes.setVisible(false);
                jMensajes.setText("");
            });
        }
    }

    public boolean existeCortesia() {
        existeCortesia = false;
        DefaultTableModel dm = (DefaultTableModel) jtableMediosPago.getModel();
        int filas = dm.getRowCount();
        for (int i = 0; i < filas; i++) {
            if (dm.getValueAt(i, 0).equals("CORTESIA")) {
                existeCortesia = true;
                break;
            }
        }
        return existeCortesia;
    }

    public void ventaGuardada() {
        NovusUtils.beep();
        TreeMap<Long, MediosPagosBean> mediosMovimiento = new TreeMap<>();
        for (MediosPagosBean medio : this.mediosPagoVenta) {
            mediosMovimiento.put(medio.getId(), medio);
        }
        PedidoFacade pedf = new PedidoFacade();
        this.movimiento.setMediosPagos(mediosMovimiento);
        if (!isFac) {
            if (!this.isNewVenta) {
                JsonObject response;
                response = sendMedioPago();
                if (response != null) {
                    showMessage("MEDIO DE PAGO EDITADO EXITOSAMENTE",
                            "/com/firefuel/resources/btOk.png",
                            true, this::cerrar,
                            true, LetterCase.FIRST_UPPER_CASE);
                    impresionVenta();
                } else {
                    showMessage("NO SE PUEDE EDITAR MEDIO PAGO",
                            "/com/firefuel/resources/btBad.png",
                            true, this::mostrarMenuPrincipal,
                            true, LetterCase.FIRST_UPPER_CASE);
                }
            } else {
                MovimientosBean movimientoRealizado = null;
                Recibo rec = new Recibo();
                rec.setPlaca("");
                rec.setEmpresa(this.movimiento.getEmpresa().getRazonSocial());
                rec.setDireccion(this.movimiento.getEmpresa().getDireccionPrincipal());
                rec.setTelefono(this.movimiento.getEmpresa().getTelefonoPrincipal());

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

                if (!isCombustible) {
                    try {
                        if (movimiento.getOperacionId() == NovusConstante.MOVIMIENTO_TIPO_COMPLEMENTARIO) {
                            mdao.create(this.movimiento, Main.credencial, isFac);
                        } else {
                            mdao.createKIOSCO(this.movimiento, Main.credencial, false);
                        }
                    } catch (DAOException e) {
                        Logger.getLogger(StoreConfirmarViewController.class.getName()).log(Level.SEVERE, null, e);
                    }
                }

                movimientoRealizado = movimiento;

                if (movimientoRealizado != null || isCombustible) {
                    if (movimientoRealizado != null) {
                        movimientoRealizado.setMovmientoEstado("A");
                        Long id = null;
                        for (Map.Entry<Long, MovimientosDetallesBean> entry : this.movimiento.getDetalles().entrySet()) {
                            MovimientosDetallesBean value = entry.getValue();
                            if (value.getIngredientes() != null && !value.getIngredientes().isEmpty()) {
                                for (ProductoBean ingrediente : value.getIngredientes()) {
                                    id = ingrediente.getId();
                                }
                            } else {
                                id = value.getProductoId();
                            }
                        }

                        rec.setMovimiento(movimientoRealizado);

                        StoreViewController.isCombustible = false;
                        KCOViewController.isCombustible = false;
                        if (!isCombustible) {
                            PrinterFacade pf = new PrinterFacade();
                            if (movimiento.getOperacionId() == NovusConstante.MOVIMIENTO_TIPO_COMPLEMENTARIO) {
                                if (existeCortesia) {
                                    showMessage("VENTA GUARDADA EXITOSAMENTE", "/com/firefuel/resources/btOk.png", true, () -> {
                                        this.cerrar();
                                        if (principal != null) {
                                            this.cerrarParent();
                                        } else {
                                            fcManual.dispose();
                                        }
                                    }, true, LetterCase.FIRST_UPPER_CASE);
                                    JsonObject resp = pf.reporteCortesia(respuestaFacturaCortesia, rec);
                                    pedf.sendCanCortecia(resp, existeCortesia);
                                } else {
                                    System.out.println("******************************");
                                    System.out.println("Finalizar Venta ");
                                    System.out.println("******************************");
                                    finalizarProcesoVenta(rec, isConsumoPropio, TipoNegociosFidelizacion.CANASTILLA.getTipoNegocio());
                                }
                                new VentaEnCursoUseCase("I", NovusUtils.getTipoNegocioComplementario()).execute();
                                new VentaEnCursoUseCase("I", "CAN").execute();
                              
                            } else {
                                if (existeCortesia) {
                                    showMessage("VENTA GUARDADA EXITOSAMENTE", "/com/firefuel/resources/btOk.png", true, () -> {
                                        this.cerrar();
                                        if (principal != null) {
                                            this.cerrarParent();
                                        } else {
                                            fcManual.dispose();
                                        }
                                    }, true, LetterCase.FIRST_UPPER_CASE);
                                    JsonObject resp = pf.reporteCortesia(respuestaFacturaCortesia, rec);
                                    pedf.sendKcoCortecia(resp, existeCortesia);
                                } else {
                                    finalizarProcesoVenta(rec, isConsumoPropio, TipoNegociosFidelizacion.KIOSCO.getTipoNegocio());
                                }
                                new VentaEnCursoUseCase("I", NovusUtils.getTipoNegocioComplementario()).execute();
                                new VentaEnCursoUseCase("I", "CAN").execute();
                                
                            }
                        }
                    } else {
                        showMessage("HA OCURRIDO UN ERROR AL REALIZAR VENTA",
                                "/com/firefuel/resources/btBad.png",
                                true, this::cerrar,
                                true, LetterCase.FIRST_UPPER_CASE);
                    }
                }
            }
        } else {
            boolean inserted = mdao.crearMediosPagosFacturaElectronica(this.movimiento);
            if (inserted) {
                showMessage("MEDIO DE PAGO EDITADO EXITOSAMENTE",
                        "/com/firefuel/resources/btOk.png",
                        true, this::mostrarMenuPrincipal,
                        true, LetterCase.FIRST_UPPER_CASE);
            } else {
                showMessage("HA OCURRIDO UN ERROR AL REALIZAR EL CAMBIO DE MEDIO DE PAGO",
                        "/com/firefuel/resources/btBad.png",
                        true, this::mostrarMenuPrincipal,
                        true, LetterCase.FIRST_UPPER_CASE);
            }
        }
    }

    void finalizarProcesoVenta(Recibo rec, boolean isConsumoPropio, String tipoNegocio) {
        showMessage("ESPERE UN MOMENTO", "/com/firefuel/resources/loader_fac.gif", false);
        Thread response = new Thread() {
            @Override
            public void run() {
                String mensaje = "VENTA GUARDADA EXITOSAMENTE";
                switch (tipoNegocio) {
                    case "KCO":
                        if (datosCliente != null) {
                            enviarFidelizacion(tipoNegocio);
                            // mensaje = mensaje.concat(" " + enviarFidelizacion(tipoNegocio));
                            respuesta(rec, isConsumoPropio, tipoNegocio, mensaje);
                        } else {
                            movimiento.setVentaFidelizada("N");
                            respuesta(rec, isConsumoPropio, tipoNegocio, mensaje);
                        }
                        break;
                    case "CAN":
                        if (datosCliente != null) {
                            enviarFidelizacion(tipoNegocio);
                            // mensaje = mensaje.concat("\n" + );
                            respuesta(rec, isConsumoPropio, tipoNegocio, mensaje);
                        } else {
                            movimiento.setVentaFidelizada("N");
                            respuesta(rec, isConsumoPropio, tipoNegocio, mensaje);
                        }
                        break;
                    default:
                        if (datosCliente != null) {
                            enviarFidelizacion(tipoNegocio);
                            // mensaje = mensaje.concat("\n" + );
                            respuesta(rec, isConsumoPropio, tipoNegocio, mensaje);
                        } else {
                            movimiento.setVentaFidelizada("N");
                            respuesta(rec, isConsumoPropio, tipoNegocio, mensaje);
                        }
                        break;
                }
            }
        };
        response.start();
    }

    void respuesta(Recibo rec, boolean isConsumoPropio, String tipoNegocio, String mensaje) {
        PrinterFacade pf = new PrinterFacade();
        PedidoFacade pedf = new PedidoFacade();
        switch (tipoNegocio) {
            case "KCO":
                NovusUtils.printLn("  @@@@@@@@@@@@@@@@@  KIOSKO  @@@@@@@@@@@@@@@@@   ");
                try {
                    pedf.consumoPropio = isConsumoPropio;
                    pedf.sendVentaKIOSCO(movimiento, true, existeCortesia);
                } catch (DAOException ex) {
                    Logger.getLogger(StoreConfirmarViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
                pf.consumoPropio = isConsumoPropio;
                pf.printReciboKIOSCO(null, rec);
                showMessage(mensaje, "/com/firefuel/resources/btOk.png", true, () -> {
                    this.cerrar();
                    if (principal != null) {
                        this.cerrarParent();
                    } else {
                        fcManual.dispose();
                    }
                }, true, LetterCase.FIRST_UPPER_CASE);
                break;
            case "CAN":
                NovusUtils.printLn("  @@@@@@@@@@@@@@@@@  CANASTILLA  @@@@@@@@@@@@@@@@@   ");
                try {
                    pedf.consumoPropio = isConsumoPropio;
                    pedf.sendVenta(movimiento, existeCortesia);
                } catch (DAOException ex) {
                    Logger.getLogger(StoreConfirmarViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
                pf.consumoPropio = isConsumoPropio;
                pf.printRecibo(rec);
                showMessage(mensaje, "/com/firefuel/resources/btOk.png", true, () -> {
                    this.cerrar();
                    if (principal != null) {
                        this.cerrarParent();
                    } else {
                        fcManual.dispose();
                    }
                }, true, LetterCase.FIRST_UPPER_CASE);
                break;
            default:
                NovusUtils.printLn("  @@@@@@@@@@@@@@@@@  CANASTILLA @DEFAULT  @@@@@@@@@@@@@@@@@   ");
                try {
                    pedf.consumoPropio = isConsumoPropio;
                    pedf.sendVenta(movimiento, existeCortesia);
                } catch (DAOException ex) {
                    Logger.getLogger(StoreConfirmarViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
                pf.consumoPropio = isConsumoPropio;
                pf.printRecibo(rec);
                showMessage(mensaje, "/com/firefuel/resources/btOk.png", true, () -> {
                    this.cerrar();
                    if (principal != null) {
                        this.cerrarParent();
                    } else {
                        fcManual.dispose();
                    }
                }, true, LetterCase.FIRST_UPPER_CASE);
                break;
        }
    }

    private String enviarFidelizacion(String negocio) {
        String mensaje = " Y FIDELIZACIÓN ENVIADA CON EXITO ";
        AcumulacionFidelizacion acumulacionFidelizacion = new AcumulacionFidelizacion();
        FidelizacionDao fidelizacionDao = new FidelizacionDao();
        String identificadorPuntoDeVenta = fidelizacionDao.indentficadorPuntoVenta(negocio);
        String consecutivo = movimiento.getConsecutivo().getPrefijo() != null ? movimiento.getConsecutivo().getPrefijo() : "";
        consecutivo = consecutivo.isEmpty() ? movimiento.getConsecutivo().getConsecutivo_actual() + "" : consecutivo + "-" + movimiento.getConsecutivo().getConsecutivo_actual() + "";

        RespuestasAcumulacion response = acumulacionFidelizacion.acumular(datosCliente, movimiento, identificadorPuntoDeVenta, negocio, consecutivo);

        try {
            //mdao.setReaperturaInOne(movimiento.getId());
            new SetReaperturaEnUnoUseCase(movimiento.getId()).execute();
        } catch (PersistenceException ex) {
            Logger.getLogger(StoreConfirmarViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
//        if (response.has("fidelizacionContingencia")) {
//            movimiento.setFidelizar(true);
//            if (response.get("fidelizacionContingencia").getAsBoolean()) {
//                movimiento.setDatosFidelizacion(response.get("fidelizacion").getAsJsonObject());
//                movimiento.setVentaFidelizada("S");
//            }
//        } else {
//            if (response.get("status").getAsInt() == 404) {
//                movimiento.setVentaFidelizada("N");
//                movimiento.setFidelizar(false);
//                mensaje = ", NO HAY CODIGO DE FIDELIZACIÓN DE " + (negocio.equals("KCO") ? "KIOSCO" : "CANASTILLA") + " PARA REALIZAR LA ACUMULACIÓN";
//            } else {
//                movimiento.setVentaFidelizada("S");
//                movimiento.setFidelizar(false);
//            }
//        }
        return response.getMensajeRespuesta();
    }

    public void loadMediosPagosTable() {
        DefaultTableModel dm = (DefaultTableModel) jtableMediosPago.getModel();
        dm.getDataVector().removeAllElements();
        dm.fireTableDataChanged();
        DefaultTableModel defaultModel = (DefaultTableModel) jtableMediosPago.getModel();
        for (MediosPagosBean medio : this.mediosPagoVenta) {
            defaultModel.addRow(new Object[]{medio.getDescripcion().toUpperCase(), medio.getVoucher(),
                "$ " + df.format(medio.getRecibido())});
        }
        this.changeMedio();
    }

    private void seleccionar() {
        int r = jtableMediosPago.getSelectedRow();
        selectedRow = r;
        if (r > -1) {
            NovusUtils.beep();
            jeliminar_medio.setIcon(new javax.swing.ImageIcon(
                    getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png")));
            loadEditMedio(r);
        }
    }

    private void loadEditMedio(int index) {
        MediosPagosBean medio = this.mediosPagoVenta.get(index);
        jmedioPagoCombo.setSelectedItem(medio.getDescripcion().toUpperCase());
        jmedioCantidad.setText(df.format(medio.getValor()));
        if (this.mediosPagoVenta.get(index).isComprobante()) {
            jmedioVoucher.setText(medio.getVoucher());
            jmedioVoucher.setEnabled(true);
        }
    }

    private void loadMediosPagos() {
        SetupDao dao = new SetupDao();
        String idsMediosValidar = "10000, 20004";
        ArrayList<MediosPagosBean> mediosOriginales = dao.getMediosPagosDefault(false, idsMediosValidar);
        
        if (mediosOriginales != null) {
            boolean isGLP = esVentaGLP();
            ArrayList<MediosPagosBean> mediosFiltrados = new ArrayList<>();
            
            // Inicializar mediosPagosDisponibles ANTES de agregar elementos al ComboBox
            mediosPagosDisponibles = mediosFiltrados;
            
            for (MediosPagosBean medio : mediosOriginales) {
                // 🔍 VALIDACIÓN GLP: Ocultar APP TERPEL y GoPass para ventas GLP
                boolean esDeshabilitadoPorGLP = isGLP && (medio.getDescripcion().equals(MediosPagosDescription.APPTERPEL) || medio.getId() == NovusConstante.ID_MEDIO_GOPASS);
                
                if (esDeshabilitadoPorGLP) {
                    NovusUtils.printLn("🚫 [GLP] Medio oculto para GLP: " + medio.getDescripcion() + " (ID=" + medio.getId() + ")");
                    // No agregar al combo ni a la lista filtrada - simplemente omitir
                    continue;
                } else if (!medio.getDescripcion().toUpperCase().equals("APP TERPEL")) {
                    mediosFiltrados.add(medio);
                    jmedioPagoCombo.addItem(medio.getDescripcion().toUpperCase());
                }
            }
        }
        
        if (!this.isNewVenta && !isFac) {
            JsonObject response = new JsonObject();
            JsonObject objMain = new JsonObject();
            objMain.addProperty("identificadorMovimiento", this.movimiento.getId());
            TreeMap<String, String> header = new TreeMap<>();
            header.put("Content-Type", "application/json");
            ClientWSAsync ws = new ClientWSAsync("g", NovusConstante.SECURE_END_POINT_CAMBIO_MEDIOSPAGOS,
                    NovusConstante.POST, objMain, false, false, header);
            try {
                ws.start();
                ws.join();
                response = ws.getResponse();
            } catch (InterruptedException ex) {
                Logger.getLogger(StoreConfirmarViewController.class.getName()).log(Level.SEVERE, null, ex);
                Thread.currentThread().interrupt();
            }
            mediosPagoVenta.clear();
            if (response != null) {
                JsonArray data = response.get("data").getAsJsonArray();
                for (JsonElement arr : data) {
                    JsonObject json = arr.getAsJsonObject();
                    MediosPagosBean medio = new MediosPagosBean();
                    medio.setId(json.get("ct_medios_pagos_id").getAsLong());
                    medio.setDescripcion(json.get("descripcion").getAsString().toUpperCase());
                    medio.setRecibido(json.get("valor_recibido").getAsFloat());
                    medio.setCambio(json.get("valor_cambio").getAsFloat());
                    medio.setValor(json.get("valor_total").getAsFloat());
                    medio.setVoucher(json.get("numero_comprobante").getAsString());
                    mediosPagoVenta.add(medio);
                }
            }
        }
        this.resetFields();
        this.loadMediosPagosTable();
        this.changeMedio();
        this.validateCalcule(0);
    }

    public Map<Long, String> getMediosDatafono() {
        TreeMap<Long, String> medio = new TreeMap<>();
        medio.put(15001L, "CON DATAFONO");
        return medio;
    }

    public boolean isDatafono(MediosPagosBean medioSeleccionado) {
        return medioSeleccionado.getDescripcion().equalsIgnoreCase("CON DATAFONO");
    }

    private void getDatafonosView(JsonObject data) {
        DatafonosView datafonosView = new DatafonosView(this, data, this::mostrarMenuPrincipalDatafono, this::mostrarMenuPrincipal);
        showDialog(datafonosView);
    }

    public boolean isVisibleDatafonos() {
        boolean isVisible = false;
        try {
            datafonos = edao.datafonosInfo();
            if (datafonos != null) {
                if (datafonos.size() > 1) {
                    isVisible = true;
                }
            } else {
                showMessage("NO SE TIENEN DATAFONOS CONFIGURADOS",
                        "/com/firefuel/resources/btBad.png", true,
                        this::mostrarMenuPrincipalDatafono, true,
                        LetterCase.FIRST_UPPER_CASE);
            }
        } catch (DAOException | SQLException ex) {
            Logger.getLogger(StoreConfirmarViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return isVisible;
    }

    private void anadirMedio() {
        int index = jmedioPagoCombo.getSelectedIndex();
        if (mediosPagosDisponibles.get(index) != null) {
            MediosPagosBean medioSeleccinado = mediosPagosDisponibles.get(index);
            
            if (mediosPagosDisponibles.get(index).getId() == 20001) {
                consumoPropio = mediosPagosDisponibles.get(index).getId();
            }
            if (isDatafono(medioSeleccinado)) {
                MediosPagosBean medio = new MediosPagosBean();
                medio.setId(mediosPagosDisponibles.get(index).getId());
                medio.setDescripcion(mediosPagosDisponibles.get(index).getDescripcion());
                medio.setCambio(mediosPagosDisponibles.get(index).isCambio());
                medio.setIsPagoExterno(mediosPagosDisponibles.get(index).isIsPagoExterno());
                medio.setPagosExternoValidado(false);

                if (cambio >= 0 && !medio.isCambio()) {
                    NovusUtils.setMensaje("CANTIDAD EXCEDE RESTANTE ( " + medio.getDescripcion().toUpperCase()
                            + " NO ADMITE CAMBIO)", jMensajes);
                    jguardarVenta.setVisible(false);
                } else {
                    JsonObject json = new JsonObject();
                    if (isVisibleDatafonos()) {
                        getDatafonosView(json);
                    }
                }
            }

            if (NovusUtils.isBonoViveTerpel(medioSeleccinado)) {
                MediosPagosBean medio = mediosPagosDisponibles.get(index);
                if (cambio >= 0) {
                    showMensajes("CANTIDAD EXCEDE RESTANTE ( " + medio.getDescripcion().toUpperCase()
                            + " NO ADMITE CAMBIO)");
                    jguardarVenta.setVisible(false);
                    return;
                } else {
                    abrirVentanaMedioViveTerpel();
                    return;
                }
            }

        }
        validarIngresoMedio(NovusConstante.ID_MEDIO_CONSUMO_PROPIO);
    }

    private void validarIngresoMedio(long idMedio) {
        if (!existeMedio(idMedio)) {
            anadirMedioPago();
        } else {
            if (existeMedio(idMedio) && mediosPagoVenta.isEmpty()) {
                anadirMedioPago();
            } else {
                showMensajes("ACCIÓN NO PERMITIDA");
            }
        }
    }

    private boolean existeMedio(long idMedio) {
        boolean existeMedio = false;
        for (MediosPagosBean mediosPago : mediosPagoVenta) {
            if (mediosPago.getId() == idMedio) {
                existeMedio = true;
                break;
            }
        }
        return existeMedio;
    }

    private void anadirMedioPago() {
        int index = jmedioPagoCombo.getSelectedIndex();
        if (!jmedioCantidad.getText().trim().equals("")) {
            float isRecibido = (float) ((Float.parseFloat(jmedioCantidad.getText().trim().replace(".", "").replace(",", "")) * 100)
                    / 100.0);
            if (isRecibido > 0) {
                float RecibidoTotalSinMedio = 0;
                float cambioSinMedio = 0;
                boolean invalido = false;
                MediosPagosBean medio = new MediosPagosBean();
                medio.setId(mediosPagosDisponibles.get(index).getId());
                if (mediosPagosDisponibles.get(index).getId() == 20001) {
                    consumoPropio = mediosPagosDisponibles.get(index).getId();
                }
                medio.setDescripcion(mediosPagosDisponibles.get(index).getDescripcion());
                medio.setCambio(mediosPagosDisponibles.get(index).isCambio());
                if (NovusUtils.isBonoViveTerpel(mediosPagosDisponibles.get(index))) {
                    medio.setPagosExternoValidado(true);
                }
                if (!this.validate(index)) {
                    int indexExistente = 0;
                    for (MediosPagosBean medios : this.mediosPagoVenta) {
                        if (medios.getId() == medio.getId()) {
                            break;
                        }
                        indexExistente++;
                    }
                    RecibidoTotalSinMedio = this.movimiento.getRecibidoTotal()
                            - this.mediosPagoVenta.get(indexExistente).getRecibido();
                    cambioSinMedio = RecibidoTotalSinMedio - this.movimiento.getVentaTotal();
                    if (isRecibido > Math.abs(cambioSinMedio) && !medio.isCambio()) {
                        invalido = true;
                    }
                } else {
                    if (isRecibido > Math.abs(cambio) && !medio.isCambio()) {
                        invalido = true;
                    }
                }
                if (!invalido) {
                    if (this.validate(index)) {
                        medio.setValor((isRecibido < Math.abs(cambio)) ? isRecibido
                                : this.movimiento.getVentaTotal() - this.movimiento.getRecibidoTotal());
                        medio.setRecibido(isRecibido);
                        medio.setCambio(medio.isCambio() ? medio.getRecibido() - medio.getValor() : 0);
                        medio.setCredito(mediosPagosDisponibles.get(index).isCredito());
                        medio.setVoucher(jmedioVoucher.getText().trim());
                        medio.setComprobante(mediosPagosDisponibles.get(index).isComprobante());
                        mediosPagoVenta.add(medio);
                        selectedRow = -1;
                    } else {
                        medio.setValor((isRecibido < Math.abs(cambioSinMedio)) ? isRecibido
                                : this.movimiento.getVentaTotal() - RecibidoTotalSinMedio);
                        medio.setRecibido(isRecibido);
                        medio.setCambio(medio.isCambio() ? medio.getRecibido() - medio.getValor() : 0);
                        medio.setCredito(mediosPagosDisponibles.get(index).isCredito());
                        medio.setVoucher(jmedioVoucher.getText().trim());
                        medio.setComprobante(mediosPagosDisponibles.get(index).isComprobante());
                        mediosPagoVenta.add(medio);
                        selectedRow = -1;
                    }
                    if (medio.isPagosExternoValidado()) {
                        ArrayList<BonoViveTerpel> bonosVenta = new ArrayList<>();
                        BonoViveTerpel bonos = new BonoViveTerpel();
                        bonos.setVoucher(medio.getVoucher());
                        bonos.setValor(isRecibido);
                        bonosVenta.add(bonos);
                        medio.setBonosViveTerpel(bonosVenta);
                        medio.setRecibido(isRecibido);
                    }
                    this.resetFields();
                    this.loadMediosPagosTable();
                    this.changeMedio();
                    this.validateCalcule(index);
                } else {
                    showMensajes("CANTIDAD EXCEDE RESTANTE ( " + medio.getDescripcion().toUpperCase()
                            + " NO ADMITE CAMBIO)");
                    jguardarVenta.setVisible(false);
                }
            } else {
                showMensajes("CANTIDAD DEBE SER MAYOR A 0");
                jguardarVenta.setVisible(false);
            }
        }
    }

    public void validateCalcule(int indexMedio) {
        float totalRecibido = 0;
        final long EFECTIVOID = 1;

        for (MediosPagosBean medios : this.mediosPagoVenta) {

            totalRecibido += medios.getRecibido();
        }
        boolean cambioValido = true;
        this.movimiento.setRecibidoTotal(totalRecibido);
        try {
            String totalVentaText = jtotal_venta.getText().replace("$ ", "");
            this.cambio = (long) (this.movimiento.getRecibidoTotal() - df.parse(totalVentaText).floatValue());
        } catch (java.text.ParseException ex) {
            Logger.getLogger(StoreConfirmarViewController.class.getName()).log(Level.SEVERE, null, ex);
            this.cambio = 0;
        }
        jcambio_venta.setText("$ " + df.format(this.cambio));
        jrecibido_venta.setText("$ " + df.format(this.movimiento.getRecibidoTotal()));
        String mensaje = "";
        if (!this.mediosPagoVenta.isEmpty()) {
            if (this.cambio >= 0) {
                // if (this.mediosPagoVenta.size() > 1) {
                boolean sePermiteCambio = false;
                for (MediosPagosBean medio : this.mediosPagoVenta) {
                    if (medio.isCambio()) {
                        sePermiteCambio = true;
                        break;
                    }
                }
                if (!sePermiteCambio && (this.movimiento.getRecibidoTotal() > this.movimiento.getVentaTotal())) {
                    mensaje = "MEDIO(S) DE PAGO NO ADMITE CAMBIO";
                    cambioValido = false;
                }

            } else {
                mensaje = "RECIBIDO DEBE SER MAYOR O IGUAL AL TOTAL DE LA VENTA";
                cambioValido = false;
            }
        } else {
            mensaje = "DEBE AGREGAR AL MENOS 1 MEDIO";
            cambioValido = false;
        }
        if (!mensaje.isEmpty()) {
            showMensajes(mensaje);
        }
        jguardarVenta.setVisible(cambioValido);
        jeliminar_medio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png")));
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
        
        // Validar que mediosPagosDisponibles no sea null y el índice esté dentro del rango
        if (this.mediosPagosDisponibles == null || index < 0 || index >= this.mediosPagosDisponibles.size()) {
            return false;
        }
        
        MediosPagosBean medioSeleccionado = this.mediosPagosDisponibles.get((int) index);
        if (medioSeleccionado == null) {
            return false;
        }
        
        for (MediosPagosBean medio : this.mediosPagoVenta) {
            if (medio.getId() == medioSeleccionado.getId()) {
                validate = false;
                break;
            }
        }
        if (medioSeleccionado.isCredito() && !this.mediosPagoVenta.isEmpty()) {
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
            Logger.getLogger(StoreConfirmarViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void cargarRestante() {
        try {
            if (this.cambio < 0) {
                jmedioCantidad.setText(df.format(Math.abs(cambio)));
            }
        } catch (NumberFormatException ex) {
            Logger.getLogger(StoreConfirmarViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void changeMedio() {
        int index = jmedioPagoCombo.getSelectedIndex();
        this.fieldStates(this.validate(index), index);
        this.resetFields();
        jmedioCantidad.requestFocus();
    }

    private void fieldStates(boolean active, int index) {
        // Validar que mediosPagosDisponibles no sea null y el índice esté dentro del rango
        if (this.mediosPagosDisponibles == null || index < 0 || index >= this.mediosPagosDisponibles.size()) {
            return;
        }
        
        MediosPagosBean medioSeleccionado = this.mediosPagosDisponibles.get(index);
        if (medioSeleccionado == null) {
            return;
        }
        
        if (medioSeleccionado.isIsBono()) {
            active = true;
        }
        jmedioVoucher.setEnabled((selectedRow > -1 && medioSeleccionado.isComprobante())
                || (active && medioSeleccionado.isComprobante()));
        jmedioCantidad.setEnabled((selectedRow > -1) || active);
        jmedioCantidad.setText(Math.abs(this.cambio) + "");
        jbutton_addMedio.setEnabled((selectedRow > -1) || active);
    }

    private void resetFields() {
        jmedioVoucher.setText("");
        jmedioCantidad.setText("");
    }

    private void removeMedio() {
        if (jtableMediosPago.getValueAt(selectedRow, 0).equals("CORTESIA")) {
            jCortesias.setVisible(false);
            jtotal_venta.setText("$ " + df.format(movimiento.getVentaTotal()));
        }
        if (selectedRow > -1) {
            NovusUtils.beep();
            this.mediosPagoVenta.remove(selectedRow);
            this.loadMediosPagosTable();
            this.validateCalcule(0);
            this.resetFields();
            selectedRow = -1;
        }

    }

    private int obtenerPosicionDatafono() {
        int posicion = 0;
        DefaultTableModel dm = (DefaultTableModel) jtableMediosPago.getModel();
        int filas = dm.getRowCount();
        for (int i = 0; i < filas; i++) {
            if (dm.getValueAt(i, 0).equals("CON DATAFONO")) {
                posicion = i;
                break;
            }
        }
        return posicion;
    }

    private void removeMedioParametro(int i) {
        this.mediosPagoVenta.remove(i);
        this.loadMediosPagosTable();
        this.validateCalcule(0);
        this.resetFields();
    }

    private void removeTodosMedio() {
        jtotal_venta.setText("$ " + df.format(movimiento.getVentaTotal()));
        NovusUtils.beep();
        this.mediosPagoVenta.clear();
        this.validateCalcule(0);
        this.loadMediosPagosTable();
        this.resetFields();
        selectedRow = -1;
    }

    private JsonObject sendMedioPago() {
        JsonObject response = null;
        JsonObject objMain = new JsonObject();
        objMain.addProperty("identificadorMovimiento", this.movimiento.getId());
        objMain.addProperty("identificadorEquipo", Main.credencial.getEquipos_id());
        objMain.addProperty("validarTurno", false);
        JsonArray mediosArray = new JsonArray();
        for (MediosPagosBean medio : this.mediosPagoVenta) {
            JsonObject objMedios = new JsonObject();
            objMedios.addProperty("identificador", medio.getId());
            objMedios.addProperty("valorRecibido", medio.getRecibido());
            objMedios.addProperty("valorCambio", medio.getCambio());
            objMedios.addProperty("valorTotal", medio.getValor());
            objMedios.addProperty("voucher", medio.getVoucher());
            objMedios.addProperty("monedaLocal", "");
            mediosArray.add(objMedios);
        }

        this.movimiento.setRecibidoTotal(recibido);
        objMain.add("mediosDePagos", mediosArray);
        TreeMap<String, String> header = new TreeMap<>();
        header.put("Content-Type", "application/json");
        ClientWSAsync ws = new ClientWSAsync("g", NovusConstante.SECURE_END_POINT_CAMBIO_MEDIOSPAGOS,
                NovusConstante.PUT, objMain, false, false, header);
        try {
            ws.start();
            ws.join();
            response = ws.getResponse();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            Logger.getLogger(StoreConfirmarViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }
    public void enviarFacturaElectronica() {
        NovusUtils.beep();
        mdao = new MovimientosDao();
        TreeMap<Long, MediosPagosBean> mediosMovimiento = new TreeMap<>();

        List<MediosPagosBean> mediosVenta = ObtenerBonosRedencion.execute(this.mediosPagoVenta);

        for (MediosPagosBean medio : mediosVenta) {
            mediosMovimiento.put(medio.getId(), medio);
        }
        this.movimiento.setMediosPagos(mediosMovimiento);

        if (!isFac) {
            if (!this.isNewVenta) {
                JsonObject response;
                response = sendMedioPago();
                medioPago(response);
            } else {
                MovimientosBean movimientoRealizado;
                JsonObject response = null;
                movimientoRealizado = this.movimiento;
                movimientoRealizado.setMovmientoEstado("A");
                if (movimiento.getOperacionId() == NovusConstante.MOVIMIENTO_TIPO_COMPLEMENTARIO || movimiento.getOperacionId() == NovusConstante.MOVIMIENTO_TIPO_KIOSCO) {
                    FacturacionElectronica factura = new FacturacionElectronica();
                    boolean existeConsecutivo;
                    existeConsecutivo = factura.enviarFacturacion(movimiento, notificadorView, CANASTILLA, 2, manual);
                    if (findByParameterUseCase.execute()) {
                        existeConsecutivo = true;
                    }
                    JsonObject respuesta = null;
                    respuesta = validarConsecutivo(existeConsecutivo, factura);
                    if (respuesta != null) {
                        continuarConelEnvio(respuesta, existeCortesia);
                    }
                }
            }

        }
    }
    // public void enviarFacturaElectronica() {
    //     NovusUtils.beep();
    //     mdao = new MovimientosDao();
    //     TreeMap<Long, MediosPagosBean> mediosMovimiento = new TreeMap<>();

    //     List<MediosPagosBean> mediosVenta = ObtenerBonosRedencion.execute(this.mediosPagoVenta);

    //     for (MediosPagosBean medio : mediosVenta) {
    //         mediosMovimiento.put(medio.getId(), medio);
    //     }
    //     this.movimiento.setMediosPagos(mediosMovimiento);

    //     if (!isFac) {
    //         if (!this.isNewVenta) {
    //             JsonObject response;
    //             response = sendMedioPago();
    //             medioPago(response);
    //         } else {
    //             MovimientosBean movimientoRealizado;
    //             JsonObject response = null;
    //             movimientoRealizado = this.movimiento;
    //             movimientoRealizado.setMovmientoEstado("A");
    //             if (movimiento.getOperacionId() == NovusConstante.MOVIMIENTO_TIPO_COMPLEMENTARIO || movimiento.getOperacionId() == NovusConstante.MOVIMIENTO_TIPO_KIOSCO) {
    //                 FacturacionElectronica factura = new FacturacionElectronica();
    //                 boolean existeConsecutivo;
    //                 existeConsecutivo = factura.enviarFacturacion(movimiento, notificadorView, CANASTILLA, 2, manual);
    //                 if (mdao.remisionActiva()) {
    //                     existeConsecutivo = true;
    //                 }
    //                 JsonObject respuesta = null;
    //                 respuesta = validarConsecutivo(existeConsecutivo, factura);
    //                 if (respuesta != null) {
    //                     continuarConelEnvio(respuesta, existeCortesia);
    //                 }
    //             }
    //         }

    //     }
    // }

    private JsonObject validarConsecutivo(boolean existeConsecutivo, FacturacionElectronica factura) {
        if (existeConsecutivo) {
            return factura.generarJsonFE(isConsumoPropio);
        } else {
            pasar = () -> {
                mostrarPanel("pnl_principal");
                cerrarTimer();
            };
            timer = new Timer(time, e -> pasar.run());
            timer.start();
        }
        return null;
    }

    private void continuarConelEnvio(JsonObject respuesta, boolean existeCortesia) {
        NovusUtils.printLn("continuarConelEnvio(JsonObject respuesta, boolean existeCortesia)");
        ClienteFacturaElectronica fac = new ClienteFacturaElectronica(this.parent,
                this.principal, true, isConsumoPropio,movimiento, 
                ventanaVenta, true);
        if (existeCortesia) {
            JsonObject data = new JsonObject();
            data.add("datos_FE", respuestaFacturaCortesia);
            fac.RESPUESTA_FACTURA = data;
            fac.MANUAL = manual;
            fac.convertir = false;
        } else {
            System.out.println("->>>>>>>>>>>>>> " + respuesta);
            fac.RESPUESTA_FACTURA = respuesta;
            fac.MANUAL = manual;
        }

        fac.lblConsultar.setVisible(false);
        fac.jLabel17.setVisible(true);
        fac.jLabel17.setBounds(690, 250, 180, 70);
        fac.jLabel7.setEnabled(true);

        fac.CANASTILLA = CANASTILLA;
        FidelizacionDao fidelizacionDao = new FidelizacionDao();
        String negocio = CANASTILLA ? TipoNegociosFidelizacion.CANASTILLA.getTipoNegocio() : TipoNegociosFidelizacion.KIOSCO.getTipoNegocio();
        String identificadorPuntoDeVenta = fidelizacionDao.indentficadorPuntoVenta(negocio);
        fac.setIdentificacionPuntoDeVenta(identificadorPuntoDeVenta);
        fac.setDatosClientesFidelizacion(datosCliente);
        datosCliente = fac.getDatosClienteFidelizaion();
        System.out.println("ejecute el Runnable de pasar en " + StoreConfirmarViewController.class.getName());
        NovusUtils.printLn("abriendo la vista de facturacio electronica desde -> " + StoreConfirmarViewController.class.getName());
        fac.setVisible(true);
        ClienteFacturaElectronica.jTextField1.requestFocus();
        this.dispose();
        this.parent.dispose();

    }

    private void medioPago(JsonObject response) {
        if (response != null) {
            showMessage("MEDIO DE PAGO EDITADO EXITOSAMENTE",
                    "/com/firefuel/resources/btOk.png", true, this::cerrar,
                    true, LetterCase.FIRST_UPPER_CASE);
            impresionVenta();
        } else {
            showMessage("NO SE PUEDE EDITAR MEDIO PAGO",
                    "/com/firefuel/resources/btBad.png", true, this::cerrar,
                    true, LetterCase.FIRST_UPPER_CASE);
        }
    }

    private void abrirCortesia() {
        FacturacionElectronica factura = new FacturacionElectronica();
        factura.enviarFacturacion(movimiento, notificadorView, CANASTILLA, 2, manual);
        JsonObject respuesta = factura.generarJsonFE(false);
        CortesiaView cortesiaView = new CortesiaView(this, true, respuesta, movimiento);
        AutorizacionView auto = new AutorizacionView(parent, true, cortesiaView);
        auto.setVisible(true);
        cortesia = true;
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

    private void showMessage(String msj, String ruta, boolean habilitar) {
        PanelNotificacion notificacion = PanelNotificacion.getInstance();
        notificacion.loader(msj, ruta, habilitar);
        mostrarSubPanel(notificacion);
    }

    private void mostrarSubPanel(JPanel panel) {
        loadPanel();
        CardLayout layout = (CardLayout) pnlPrincipal.getLayout();
        pnlPrincipal.add("pnl_ext", panel);
        layout.show(pnlPrincipal, "pnl_ext");
    }

    private void mostrarMenuPrincipal() {
        CardLayout layout = (CardLayout) pnlPrincipal.getLayout();
        layout.show(pnlPrincipal, "pnl_principal");
    }

    private void loadPanel() {
        pnlPrincipal.add(jPanel3, "pnl_principal");
        pnlPrincipal.add(alertas_consecutivos, "alertas");
    }

    private void mostrarMenuPrincipalDatafono() {
        CardLayout layout = (CardLayout) pnlPrincipal.getLayout();
        layout.show(pnlPrincipal, "pnl_principal");
        removeMedioParametro(obtenerPosicionDatafono());
    }

    public void setTimeout(int delay, Runnable runnable) {
        new Thread(() -> {
            try {
                Thread.sleep(delay * 1000);
                runnable.run();
            } catch (InterruptedException e) {
                NovusUtils.printLn("Interrupted.setTimeout" + e.getLocalizedMessage());
                logger.log(java.util.logging.Level.WARNING, "Interrupted!", e);
                // Restore interrupted state...
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void mostrarAlertas(String mensaje, String icono) {
        mostrarPanel("alertas");
        jMensaje.setText("<html><center>" + mensaje + "</center></html>");
        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource(icono)));
    }

    private void mostrarPanel(String panel) {
        CardLayout card = (CardLayout) pnlPrincipal.getLayout();
        card.show(pnlPrincipal, panel);

    }

    private void cerrarTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    private void showDialog(JDialog dialog) {
        JPanel panel = (JPanel) dialog.getContentPane();
        mostrarSubPanel(panel);
    }

    private void showPanelFidelizacion() {
        FidelizacionKioscos fidelizacionKioscos = new FidelizacionKioscos(movimiento, () -> showPrincipal(), CANASTILLA);
        pnlPrincipal.removeAll();
        pnlPrincipal.add(fidelizacionKioscos);
        pnlPrincipal.revalidate();
        pnlPrincipal.repaint();
    }

    private void showPrincipal() {
        pnlPrincipal.removeAll();
        pnlPrincipal.add(jPanel3);
        pnlPrincipal.revalidate();
        pnlPrincipal.repaint();
    }

    private void ajustarPosicionBoton(boolean tipoventa) {
        // if (tipoventa) {
        btnFidelizacion.setVisible(false);
        jfacturacion.setBounds(650, 730, 180, 54);
        jCortesias.setBounds(450, 730, 180, 54);
        //}
    }

    private void abrirVentanaMedioViveTerpel() {
        try {
            Long TotalRecibido = 0L;
            try {
                TotalRecibido = (long) ((Float.parseFloat(jmedioCantidad.getText().trim().replace(".", "").replace(",", "")) * 100) / 100.0);
            } catch (NumberFormatException e) {
            }

            if (!jmedioCantidad.getText().trim().equals("") || TotalRecibido > 0) {
                long restanteVenta = 0;
                if (this.cambio < 0) {
                    restanteVenta = Math.abs((long) cambio);
                }
                mostrarVenta(PANEL_VALIDACION_BONO, TotalRecibido, restanteVenta);
            } else {
                NovusUtils.setMensaje("CANTIDAD DEBE SER MAYOR A 0", jMensajes);
                jguardarVenta.setVisible(false);

            }
        } catch (NumberFormatException | SecurityException e) {
            Logger.getLogger(MedioPagosConfirmarViewController.class
                    .getName()).log(Level.SEVERE, null, e);
        }
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
            FidelizacionValidacionVoucher voucher = new FidelizacionValidacionVoucher((InfoViewController) this.principal,
                    true, false, (int) valorRecibido.intValue(), notify, this.mediosPagoVenta, totalVenta);
            voucher.setVisible(true);
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
            anadirMedioPago();
        }
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
