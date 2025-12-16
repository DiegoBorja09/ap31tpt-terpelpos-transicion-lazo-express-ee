package com.firefuel;

import com.WT2.appTerpel.domain.valueObject.MediosPagosDescription;
import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.application.useCases.persons.FindAdminUseCase;
import com.bean.ConsecutivoBean;
import com.bean.MediosPagosBean;
import com.bean.PersonaBean;
import com.bean.Surtidor;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.MovimientosDao;
import com.dao.PersonasDao;
import com.dao.SetupDao;
import com.firefuel.facturacion.electronica.FacturacionElectronica;
import com.firefuel.facturacion.electronica.TiposDocumentos;
import com.firefuel.utils.ShowMessageSingleton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import teclado.view.common.TecladoExtendido;
import teclado.view.common.TecladoNumerico;

public class FacturacionManualView extends javax.swing.JFrame {

    MovimientosDao mdao = new MovimientosDao();
    public static JsonObject respuesta;
    public JsonObject clienteFE;
    SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_DATE_SQL);
    long identificadorTipoDocumento = 0;
    List<PersonaBean> promotoresDisponibles = new LinkedList<>();
    List<MediosPagosBean> mediosPagoDisponibles = new LinkedList<>();
    List<MediosPagosBean> mediosPagoVenta = new LinkedList<>();
    boolean tipoVentaPos;
    public String TIPO_DOCUMENTO = "";
    public static final int DOCUMENTO_REGISTRO_CIVIL = 11;
    public static final int DOCUMENTO_TARJ_IDENTIFICACION = 12;
    public static final int DOCUMENTO_CEDULA = 13;
    public static final int DOCUMENTO_TARJ_EXTRANJERIA = 21;
    public static final int DOCUMENTO_CEDULA_EXTRANJERIA = 22;
    public static final int DOCUMENTO_NIT = 31;
    public static final int DOCUMENTO_PASAPORTE = 41;
    public static final int DOCUMENTO_ID_EXTRANJERIA = 42;
    public static final int DOCUMENTO_NIT_OTRO_PAIS = 50;
    public static final int DOCUMENTO_NUIP = 91;
    DefaultTableCellRenderer textRight;
    DefaultTableCellRenderer textCenter;
    public final String PNL_PRINCIPAL = "pnlPrincipal";
    public final String PNL_CLIENTE = "pnlCliente";
    public final String PNL_PAGOS = "pnlPagos";
    public long consumoPropio;
    boolean imprimir = false;
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);
    JFrame parent;
    InfoViewController info;
    Runnable handler;
    Runnable negar;
    PersonaBean persona = null;
    boolean facturacionNormal = false;
    boolean menuManual = false;
    VentaManualMenuPanel venta;
    String identificadorPromotor = null;
    PersonasDao personasDao = new PersonasDao();
    public boolean isElectronica = false;
    boolean confirmacionFaturacion = false;
    Runnable ejecutarFaturaPOS;
    String caracteresPermitidos = "[0-9]";
    int cantidadCaracteres = 10;

    public FacturacionManualView(InfoViewController parent, boolean pos, boolean menuManual) {
        this.info = parent;
        this.tipoVentaPos = pos;
        this.persona = Main.persona;
        this.menuManual = menuManual;
        initComponents();
        init(tipoVentaPos);
        showMensajes(null);
    }

    public FacturacionManualView(InfoViewController parent, boolean pos, VentaManualMenuPanel venta, boolean menuManual) {
        this.info = parent;
        this.tipoVentaPos = pos;
        this.venta = venta;
        this.persona = Main.persona;
        this.menuManual = menuManual;
        initComponents();
        init(tipoVentaPos);
        showMensajes(null);
    }

    JPanel panel;

    public FacturacionManualView(JPanel parent, boolean pos) {
        this.panel = parent;
        this.tipoVentaPos = pos;
        initComponents();
        this.persona = Main.persona;
        init(tipoVentaPos);
        showMensajes(null);
    }

    JDialog dialog;

    public FacturacionManualView(JDialog parent, boolean pos) {
        this.dialog = parent;
        this.tipoVentaPos = pos;
        this.persona = Main.persona;
        initComponents();
        init(tipoVentaPos);
        showMensajes(null);
    }

    public FacturacionManualView(InfoViewController parent, boolean pos) {
        this.info = parent;
        this.tipoVentaPos = pos;
        this.persona = Main.persona;
        initComponents();
        init(tipoVentaPos);
        showMensajes(null);
    }

    public void setParent(JFrame parent) {
        this.parent = parent;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tipoDocumento = new javax.swing.ButtonGroup();
        pnl_Principal = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        pnlPrincipal = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        lblFactura = new javax.swing.JLabel();
        txtFactura = new javax.swing.JTextField();
        lblCara = new javax.swing.JLabel();
        lblValor = new javax.swing.JLabel();
        lblFecha = new javax.swing.JLabel();
        lblVolCalculado = new javax.swing.JLabel();
        lblHora = new javax.swing.JLabel();
        lbl = new javax.swing.JLabel();
        txtValor = new javax.swing.JTextField();
        lblProducto = new javax.swing.JLabel();
        lblPrecio = new javax.swing.JLabel();
        lblPrecioProducto = new javax.swing.JLabel();
        jlblManguera = new javax.swing.JLabel();
        lblVolumen = new javax.swing.JLabel();
        txtFecha = new rojeru_san.componentes.RSDateChooser();
        cmbCara = new javax.swing.JComboBox<>();
        cmbHoras = new javax.swing.JComboBox<>();
        cmbMinutos = new javax.swing.JComboBox<>();
        cmbManguera = new javax.swing.JComboBox<>();
        cmbProducto = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        tecNumericoPnlPrincipal = new TecladoNumerico()
        ;
        jclock2 = new javax.swing.JPanel();
        pnlDatosCliente = new javax.swing.JPanel();
        btnCliente1 = new javax.swing.JLabel();
        jPanel2 = new TecladoExtendido();
        jPanel7 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        lblCara1 = new javax.swing.JLabel();
        lblCara4 = new javax.swing.JLabel();
        lblCara6 = new javax.swing.JLabel();
        lblCara5 = new javax.swing.JLabel();
        txtIdentificacion = new javax.swing.JTextField();
        txtNombre = new javax.swing.JTextField();
        JComboTipoDocumentos = new javax.swing.JComboBox<>();
        txtPlaca = new javax.swing.JTextField();
        txtKms = new javax.swing.JTextField();
        pnlMediosPago = new javax.swing.JPanel();
        btnGuardar = new javax.swing.JLabel();
        jmedioCantidad = new javax.swing.JTextField();
        jbutton_addMedio = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cmbMedioPago = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jmedioVoucher = new javax.swing.JTextField();
        jeliminar_medio1 = new javax.swing.JLabel();
        jeliminar_medio = new javax.swing.JLabel();
        jclock = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblTotalCalculado = new javax.swing.JLabel();
        lblRecibidoCalculado = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jPanel6 = new TecladoNumerico()
        ;
        jScrollPane1 = new javax.swing.JScrollPane();
        tbMediosPago = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        lblCambio = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        autenticacion = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPasswordField1 = new javax.swing.JPasswordField();
        juser = new javax.swing.JTextField();
        jPanel11 = new TecladoNumerico() ;
        jNotificacionAutenticacion = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTitle = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblMensaje = new javax.swing.JLabel();
        jNotificacion = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jMensajes = new javax.swing.JLabel();
        jfondoMensajes = new javax.swing.JLabel();
        fondo = new javax.swing.JLabel();
        pnl_confirmacion = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        NO = new javax.swing.JLabel();
        SI1 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        fnd = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1280, 800));
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(null);

        pnl_Principal.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnl_Principal.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnl_Principal.setLayout(new java.awt.CardLayout());

        jPanel4.setMaximumSize(new java.awt.Dimension(1280, 800));
        jPanel4.setLayout(null);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.CardLayout());

        pnlPrincipal.setOpaque(false);
        pnlPrincipal.setLayout(null);

        jPanel3.setOpaque(false);
        jPanel3.setLayout(null);

        lblFactura.setFont(new java.awt.Font("Bebas Neue", 0, 20)); // NOI18N
        lblFactura.setForeground(new java.awt.Color(186, 12, 47));
        lblFactura.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblFactura.setText("NRO. FACTURA");
        lblFactura.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel3.add(lblFactura);
        lblFactura.setBounds(130, 60, 160, 50);

        txtFactura.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        txtFactura.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtFactura.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFacturaFocusGained(evt);
            }
        });
        txtFactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFacturaActionPerformed(evt);
            }
        });
        txtFactura.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFacturaKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtFacturaKeyTyped(evt);
            }
        });
        jPanel3.add(txtFactura);
        txtFactura.setBounds(300, 60, 260, 50);

        lblCara.setFont(new java.awt.Font("Bebas Neue", 0, 20)); // NOI18N
        lblCara.setForeground(new java.awt.Color(186, 12, 47));
        lblCara.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCara.setText("CARA");
        lblCara.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel3.add(lblCara);
        lblCara.setBounds(50, 150, 60, 30);

        lblValor.setFont(new java.awt.Font("Bebas Neue", 0, 20)); // NOI18N
        lblValor.setForeground(new java.awt.Color(186, 12, 47));
        lblValor.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblValor.setText("VALOR");
        lblValor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel3.add(lblValor);
        lblValor.setBounds(50, 450, 110, 30);

        lblFecha.setFont(new java.awt.Font("Bebas Neue", 0, 20)); // NOI18N
        lblFecha.setForeground(new java.awt.Color(186, 12, 47));
        lblFecha.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblFecha.setText("FECHA");
        lblFecha.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel3.add(lblFecha);
        lblFecha.setBounds(190, 150, 80, 30);

        lblVolCalculado.setFont(new java.awt.Font("Bebas Neue", 0, 28)); // NOI18N
        lblVolCalculado.setForeground(new java.awt.Color(186, 12, 47));
        lblVolCalculado.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblVolCalculado.setText("0,0");
        lblVolCalculado.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel3.add(lblVolCalculado);
        lblVolCalculado.setBounds(530, 490, 130, 50);

        lblHora.setFont(new java.awt.Font("Bebas Neue", 0, 20)); // NOI18N
        lblHora.setForeground(new java.awt.Color(186, 12, 47));
        lblHora.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHora.setText("HORA");
        lblHora.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel3.add(lblHora);
        lblHora.setBounds(460, 150, 200, 30);

        lbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl.setText(":");
        lbl.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel3.add(lbl);
        lbl.setBounds(550, 190, 20, 30);

        txtValor.setFont(new java.awt.Font("Roboto", 0, 28)); // NOI18N
        txtValor.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtValor.setAlignmentX(0.0F);
        txtValor.setAlignmentY(0.0F);
        txtValor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(186, 12, 47)));
        txtValor.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtValorCaretUpdate(evt);
            }
        });
        txtValor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtValorFocusGained(evt);
            }
        });
        txtValor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtValorKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtValorKeyTyped(evt);
            }
        });
        jPanel3.add(txtValor);
        txtValor.setBounds(50, 490, 240, 50);

        lblProducto.setFont(new java.awt.Font("Bebas Neue", 0, 20)); // NOI18N
        lblProducto.setForeground(new java.awt.Color(186, 12, 47));
        lblProducto.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblProducto.setText("PRODUCTO");
        lblProducto.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel3.add(lblProducto);
        lblProducto.setBounds(190, 290, 130, 30);

        lblPrecio.setFont(new java.awt.Font("Bebas Neue", 0, 20)); // NOI18N
        lblPrecio.setForeground(new java.awt.Color(186, 12, 47));
        lblPrecio.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblPrecio.setText("PRECIO:");
        lblPrecio.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel3.add(lblPrecio);
        lblPrecio.setBounds(420, 290, 90, 30);

        lblPrecioProducto.setFont(new java.awt.Font("Bebas Neue", 0, 20)); // NOI18N
        lblPrecioProducto.setForeground(new java.awt.Color(186, 12, 47));
        lblPrecioProducto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblPrecioProducto.setText("$0");
        lblPrecioProducto.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel3.add(lblPrecioProducto);
        lblPrecioProducto.setBounds(520, 290, 140, 30);

        jlblManguera.setFont(new java.awt.Font("Bebas Neue", 0, 20)); // NOI18N
        jlblManguera.setForeground(new java.awt.Color(186, 12, 47));
        jlblManguera.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jlblManguera.setText("MANGUERA");
        jlblManguera.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel3.add(jlblManguera);
        jlblManguera.setBounds(50, 290, 130, 30);

        lblVolumen.setFont(new java.awt.Font("Bebas Neue", 0, 20)); // NOI18N
        lblVolumen.setForeground(new java.awt.Color(186, 12, 47));
        lblVolumen.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblVolumen.setText("VOLUMEN");
        lblVolumen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel3.add(lblVolumen);
        lblVolumen.setBounds(530, 460, 130, 30);

        txtFecha.setColorBackground(new java.awt.Color(186, 12, 47));
        txtFecha.setColorButtonHover(new java.awt.Color(186, 12, 47));
        txtFecha.setColorDiaActual(new java.awt.Color(186, 12, 47));
        txtFecha.setColorForeground(new java.awt.Color(186, 12, 47));
        txtFecha.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        txtFecha.setFormatoFecha("yyyy-MM-dd");
        txtFecha.setFuente(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        txtFecha.setPlaceholder("yyyy-mm-dd");
        txtFecha.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtFechaMouseClicked(evt);
            }
        });
        jPanel3.add(txtFecha);
        txtFecha.setBounds(190, 180, 240, 50);

        cmbCara.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        cmbCara.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbCaraItemStateChanged(evt);
            }
        });
        cmbCara.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbCaraActionPerformed(evt);
            }
        });
        jPanel3.add(cmbCara);
        cmbCara.setBounds(50, 180, 110, 50);

        cmbHoras.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        cmbHoras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbHorasActionPerformed(evt);
            }
        });
        jPanel3.add(cmbHoras);
        cmbHoras.setBounds(460, 180, 90, 50);

        cmbMinutos.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        cmbMinutos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbMinutosActionPerformed(evt);
            }
        });
        jPanel3.add(cmbMinutos);
        cmbMinutos.setBounds(570, 180, 90, 50);

        cmbManguera.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        cmbManguera.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbMangueraItemStateChanged(evt);
            }
        });
        cmbManguera.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbMangueraActionPerformed(evt);
            }
        });
        jPanel3.add(cmbManguera);
        cmbManguera.setBounds(50, 320, 110, 50);

        cmbProducto.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        cmbProducto.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbProductoItemStateChanged(evt);
            }
        });
        cmbProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbProductoActionPerformed(evt);
            }
        });
        jPanel3.add(cmbProducto);
        cmbProducto.setBounds(190, 320, 470, 50);

        pnlPrincipal.add(jPanel3);
        jPanel3.setBounds(0, 0, 710, 610);

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 51, 51));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndventamanual.png"))); // NOI18N
        pnlPrincipal.add(jLabel1);
        jLabel1.setBounds(0, 0, 710, 610);
        pnlPrincipal.add(tecNumericoPnlPrincipal);
        tecNumericoPnlPrincipal.setBounds(710, 70, 550, 470);

        jclock2.setOpaque(false);
        jclock2.setLayout(null);
        pnlPrincipal.add(jclock2);
        jclock2.setBounds(1150, 630, 110, 66);

        jPanel1.add(pnlPrincipal, "card4");

        pnlDatosCliente.setBackground(new java.awt.Color(255, 255, 255));
        pnlDatosCliente.setMaximumSize(new java.awt.Dimension(1280, 710));
        pnlDatosCliente.setName("pnlCliente"); // NOI18N
        pnlDatosCliente.setOpaque(false);
        pnlDatosCliente.setLayout(null);

        btnCliente1.setFont(new java.awt.Font("Bebas Neue", 0, 24)); // NOI18N
        btnCliente1.setForeground(new java.awt.Color(255, 255, 255));
        btnCliente1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnCliente1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"))); // NOI18N
        btnCliente1.setText("SIGUIENTE");
        btnCliente1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCliente1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCliente1MouseClicked(evt);
            }
        });
        pnlDatosCliente.add(btnCliente1);
        btnCliente1.setBounds(830, 630, 300, 70);
        pnlDatosCliente.add(jPanel2);
        jPanel2.setBounds(120, 230, 1040, 380);

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setLayout(null);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setOpaque(false);
        jPanel5.setLayout(null);

        lblCara1.setBackground(new java.awt.Color(0, 0, 0));
        lblCara1.setFont(new java.awt.Font("Bebas Neue", 1, 18)); // NOI18N
        lblCara1.setForeground(new java.awt.Color(186, 12, 47));
        lblCara1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCara1.setText("NOMBRE");
        lblCara1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel5.add(lblCara1);
        lblCara1.setBounds(440, 10, 240, 30);

        lblCara4.setBackground(new java.awt.Color(0, 0, 0));
        lblCara4.setFont(new java.awt.Font("Bebas Neue", 1, 18)); // NOI18N
        lblCara4.setForeground(new java.awt.Color(186, 12, 47));
        lblCara4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCara4.setText("PLACA");
        lblCara4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel5.add(lblCara4);
        lblCara4.setBounds(50, 120, 220, 30);

        lblCara6.setBackground(new java.awt.Color(0, 0, 0));
        lblCara6.setFont(new java.awt.Font("Bebas Neue", 1, 18)); // NOI18N
        lblCara6.setForeground(new java.awt.Color(186, 12, 47));
        lblCara6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCara6.setText("IDENTIFICACIÓN");
        lblCara6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel5.add(lblCara6);
        lblCara6.setBounds(50, 10, 240, 30);

        lblCara5.setBackground(new java.awt.Color(0, 0, 0));
        lblCara5.setFont(new java.awt.Font("Bebas Neue", 1, 18)); // NOI18N
        lblCara5.setForeground(new java.awt.Color(186, 12, 47));
        lblCara5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCara5.setText("KILOMETRAJE");
        lblCara5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel5.add(lblCara5);
        lblCara5.setBounds(440, 120, 230, 30);

        txtIdentificacion.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        txtIdentificacion.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtIdentificacion.setBorder(new com.firefuel.components.panelesPersonalizados.BordesRedondos());
        txtIdentificacion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtIdentificacionFocusGained(evt);
            }
        });
        txtIdentificacion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                txtIdentificacionMouseReleased(evt);
            }
        });
        txtIdentificacion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtIdentificacionKeyTyped(evt);
            }
        });
        jPanel5.add(txtIdentificacion);
        txtIdentificacion.setBounds(40, 50, 350, 60);

        txtNombre.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        txtNombre.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtNombre.setBorder(new com.firefuel.components.panelesPersonalizados.BordesRedondos());
        txtNombre.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNombreFocusGained(evt);
            }
        });
        txtNombre.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                txtNombreMouseReleased(evt);
            }
        });
        jPanel5.add(txtNombre);
        txtNombre.setBounds(440, 50, 350, 60);

        JComboTipoDocumentos.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        JComboTipoDocumentos.setForeground(new java.awt.Color(51, 51, 51));
        JComboTipoDocumentos.setBorder(new com.firefuel.components.panelesPersonalizados.BordesRedondos());
        JComboTipoDocumentos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JComboTipoDocumentosActionPerformed(evt);
            }
        });
        jPanel5.add(JComboTipoDocumentos);
        JComboTipoDocumentos.setBounds(810, 50, 400, 60);

        txtPlaca.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        txtPlaca.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPlaca.setBorder(new com.firefuel.components.panelesPersonalizados.BordesRedondos());
        txtPlaca.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPlacaFocusGained(evt);
            }
        });
        txtPlaca.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                txtPlacaMouseReleased(evt);
            }
        });
        jPanel5.add(txtPlaca);
        txtPlaca.setBounds(40, 150, 350, 60);

        txtKms.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        txtKms.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtKms.setBorder(new com.firefuel.components.panelesPersonalizados.BordesRedondos());
        txtKms.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtKmsFocusGained(evt);
            }
        });
        txtKms.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                txtKmsMouseReleased(evt);
            }
        });
        txtKms.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtKmsKeyTyped(evt);
            }
        });
        jPanel5.add(txtKms);
        txtKms.setBounds(440, 150, 350, 60);

        jPanel7.add(jPanel5);
        jPanel5.setBounds(30, 10, 1220, 220);

        pnlDatosCliente.add(jPanel7);
        jPanel7.setBounds(0, 0, 1280, 620);

        jPanel1.add(pnlDatosCliente, "card3");

        pnlMediosPago.setMaximumSize(new java.awt.Dimension(1280, 710));
        pnlMediosPago.setOpaque(false);
        pnlMediosPago.setLayout(null);

        btnGuardar.setFont(new java.awt.Font("Bebas Neue", 0, 24)); // NOI18N
        btnGuardar.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"))); // NOI18N
        btnGuardar.setText("GUARDAR");
        btnGuardar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGuardar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnGuardarMouseClicked(evt);
            }
        });
        pnlMediosPago.add(btnGuardar);
        btnGuardar.setBounds(840, 630, 300, 70);

        jmedioCantidad.setFont(new java.awt.Font("Bebas Neue", 0, 36)); // NOI18N
        jmedioCantidad.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jmedioCantidad.setText("0");
        jmedioCantidad.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jmedioCantidadFocusLost(evt);
            }
        });
        jmedioCantidad.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jmedioCantidadMouseClicked(evt);
            }
        });
        pnlMediosPago.add(jmedioCantidad);
        jmedioCantidad.setBounds(350, 240, 250, 50);

        jbutton_addMedio.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jbutton_addMedio.setForeground(new java.awt.Color(255, 255, 255));
        jbutton_addMedio.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jbutton_addMedio.setText("+");
        jbutton_addMedio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jbutton_addMedioMouseClicked(evt);
            }
        });
        pnlMediosPago.add(jbutton_addMedio);
        jbutton_addMedio.setBounds(600, 240, 50, 50);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-xsmall.png"))); // NOI18N
        pnlMediosPago.add(jLabel7);
        jLabel7.setBounds(600, 240, 50, 50);

        cmbMedioPago.setFont(new java.awt.Font("Bebas Neue", 0, 30)); // NOI18N
        cmbMedioPago.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(186, 12, 47), 1, true));
        pnlMediosPago.add(cmbMedioPago);
        cmbMedioPago.setBounds(350, 180, 300, 50);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("NUMERO DE RECIBO");
        pnlMediosPago.add(jLabel2);
        jLabel2.setBounds(60, 290, 260, 20);

        jmedioVoucher.setFont(new java.awt.Font("Bebas Neue", 0, 36)); // NOI18N
        jmedioVoucher.setForeground(new java.awt.Color(51, 51, 51));
        jmedioVoucher.setToolTipText("");
        pnlMediosPago.add(jmedioVoucher);
        jmedioVoucher.setBounds(60, 240, 260, 50);

        jeliminar_medio1.setFont(new java.awt.Font("Bebas Neue", 0, 20)); // NOI18N
        jeliminar_medio1.setForeground(new java.awt.Color(255, 255, 255));
        jeliminar_medio1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jeliminar_medio1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jeliminar_medio1.setText("QUITAR TODOS");
        jeliminar_medio1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jeliminar_medio1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jeliminar_medio1MouseClicked(evt);
            }
        });
        pnlMediosPago.add(jeliminar_medio1);
        jeliminar_medio1.setBounds(280, 510, 173, 70);

        jeliminar_medio.setFont(new java.awt.Font("Bebas Neue", 0, 20)); // NOI18N
        jeliminar_medio.setForeground(new java.awt.Color(255, 255, 255));
        jeliminar_medio.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jeliminar_medio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-link-small.png"))); // NOI18N
        jeliminar_medio.setText("BORRAR");
        jeliminar_medio.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jeliminar_medio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jeliminar_medioMouseClicked(evt);
            }
        });
        pnlMediosPago.add(jeliminar_medio);
        jeliminar_medio.setBounds(470, 510, 174, 70);

        jclock.setOpaque(false);
        jclock.setLayout(null);
        pnlMediosPago.add(jclock);
        jclock.setBounds(1150, 630, 110, 66);

        jLabel14.setFont(new java.awt.Font("Conthrax", 0, 24)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(102, 102, 102));
        jLabel14.setText("CAMBIO:");
        pnlMediosPago.add(jLabel14);
        jLabel14.setBounds(730, 40, 170, 40);

        jLabel8.setFont(new java.awt.Font("Conthrax", 0, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(102, 102, 102));
        jLabel8.setText("TOTAL: ");
        pnlMediosPago.add(jLabel8);
        jLabel8.setBounds(60, 50, 220, 50);

        jLabel5.setFont(new java.awt.Font("Conthrax", 0, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(102, 102, 102));
        jLabel5.setText("RECIBIDO:");
        pnlMediosPago.add(jLabel5);
        jLabel5.setBounds(60, 110, 220, 50);

        jLabel4.setFont(new java.awt.Font("Conthrax", 0, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(102, 102, 102));
        jLabel4.setText("MEDIO PAGO:");
        pnlMediosPago.add(jLabel4);
        jLabel4.setBounds(60, 180, 260, 50);

        lblTotalCalculado.setFont(new java.awt.Font("Conthrax", 0, 36)); // NOI18N
        lblTotalCalculado.setForeground(new java.awt.Color(186, 12, 47));
        lblTotalCalculado.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotalCalculado.setText("$ 0");
        pnlMediosPago.add(lblTotalCalculado);
        lblTotalCalculado.setBounds(300, 50, 340, 50);

        lblRecibidoCalculado.setFont(new java.awt.Font("Conthrax", 0, 36)); // NOI18N
        lblRecibidoCalculado.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblRecibidoCalculado.setText("$ 0");
        pnlMediosPago.add(lblRecibidoCalculado);
        lblRecibidoCalculado.setBounds(300, 110, 340, 50);

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_recibido_venta_manual.png"))); // NOI18N
        pnlMediosPago.add(jLabel11);
        jLabel11.setBounds(50, 50, 610, 50);

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_recibido_venta_manual.png"))); // NOI18N
        pnlMediosPago.add(jLabel15);
        jLabel15.setBounds(50, 110, 610, 50);
        pnlMediosPago.add(jPanel6);
        jPanel6.setBounds(710, 110, 550, 470);

        tbMediosPago.setFont(new java.awt.Font("Bebas Neue", 0, 28)); // NOI18N
        tbMediosPago.setModel(new javax.swing.table.DefaultTableModel(
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
        tbMediosPago.setRowHeight(40);
        tbMediosPago.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tbMediosPagoMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tbMediosPago);

        pnlMediosPago.add(jScrollPane1);
        jScrollPane1.setBounds(60, 320, 590, 190);

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndventamanual.png"))); // NOI18N
        pnlMediosPago.add(jLabel9);
        jLabel9.setBounds(0, 0, 710, 610);

        lblCambio.setFont(new java.awt.Font("Conthrax", 0, 36)); // NOI18N
        lblCambio.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCambio.setText("$ 0");
        lblCambio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblCambioMouseClicked(evt);
            }
        });
        pnlMediosPago.add(lblCambio);
        lblCambio.setBounds(950, 40, 290, 40);

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_cambio_ventamanual.png"))); // NOI18N
        pnlMediosPago.add(jLabel13);
        jLabel13.setBounds(710, 30, 550, 60);

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        pnlMediosPago.add(jPanel8);
        jPanel8.setBounds(0, 0, 1280, 625);

        jPanel1.add(pnlMediosPago, "card2");

        autenticacion.setFocusTraversalPolicyProvider(true);
        autenticacion.setLayout(null);

        jScrollPane2.setBorder(null);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "PORMOTOR", "IDENTIFICACION", "F. INICIO"
            }
        ));
        jTable1.setCellSelectionEnabled(true);
        jTable1.setMaximumSize(new java.awt.Dimension(21474836, 60));
        jTable1.setMinimumSize(new java.awt.Dimension(50, 60));
        jTable1.setPreferredSize(new java.awt.Dimension(250, 60));
        jTable1.setRowHeight(28);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable1);

        autenticacion.add(jScrollPane2);
        jScrollPane2.setBounds(50, 320, 613, 232);

        jPasswordField1.setBackground(new java.awt.Color(186, 12, 47));
        jPasswordField1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jPasswordField1.setForeground(new java.awt.Color(255, 255, 255));
        jPasswordField1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPasswordField1.setBorder(null);
        jPasswordField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPasswordField1ActionPerformed(evt);
            }
        });
        autenticacion.add(jPasswordField1);
        jPasswordField1.setBounds(70, 191, 327, 44);

        juser.setBackground(new java.awt.Color(186, 12, 47));
        juser.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        juser.setForeground(new java.awt.Color(255, 255, 255));
        juser.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        juser.setToolTipText("");
        juser.setBorder(null);
        juser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                juserActionPerformed(evt);
            }
        });
        autenticacion.add(juser);
        juser.setBounds(70, 85, 320, 44);
        autenticacion.add(jPanel11);
        jPanel11.setBounds(680, 70, 590, 500);

        jNotificacionAutenticacion.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jNotificacionAutenticacion.setForeground(new java.awt.Color(255, 255, 255));
        jNotificacionAutenticacion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        autenticacion.add(jNotificacionAutenticacion);
        jNotificacionAutenticacion.setBounds(20, 620, 1240, 70);

        jLabel20.setBackground(new java.awt.Color(186, 12, 47));
        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("TURNOS ACTUALES");
        jLabel20.setToolTipText("");
        jLabel20.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel20.setAlignmentX(0.5F);
        jLabel20.setOpaque(true);
        autenticacion.add(jLabel20);
        jLabel20.setBounds(60, 280, 526, 30);

        jLabel21.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(186, 12, 47));
        jLabel21.setText("USUARIO");
        autenticacion.add(jLabel21);
        jLabel21.setBounds(50, 60, 180, 20);

        jLabel19.setFont(new java.awt.Font("Terpel Sans", 1, 20)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(186, 12, 47));
        jLabel19.setText("CONTRASEÑA");
        autenticacion.add(jLabel19);
        jLabel19.setBounds(50, 160, 200, 20);

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_autorizacion.png"))); // NOI18N
        jLabel6.setText("jLabel6");
        autenticacion.add(jLabel6);
        jLabel6.setBounds(0, -80, 1280, 800);

        jPanel1.add(autenticacion, "autenticacion");

        jPanel4.add(jPanel1);
        jPanel1.setBounds(0, 80, 1280, 720);
        jPanel1.getAccessibleContext().setAccessibleName("");

        jTitle.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jTitle.setForeground(new java.awt.Color(255, 255, 255));
        jTitle.setText("INGRESO DE  FACTURA DE CONTINGENCIA");
        jPanel4.add(jTitle);
        jTitle.setBounds(120, 15, 860, 50);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel3MouseReleased(evt);
            }
        });
        jPanel4.add(jLabel3);
        jLabel3.setBounds(10, 10, 70, 71);

        lblMensaje.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblMensaje.setForeground(new java.awt.Color(255, 255, 255));
        jPanel4.add(lblMensaje);
        lblMensaje.setBounds(110, 710, 860, 80);

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        jPanel4.add(jNotificacion);
        jNotificacion.setBounds(730, 10, 530, 70);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel4.add(jLabel27);
        jLabel27.setBounds(90, 10, 10, 68);

        jMensajes.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jPanel4.add(jMensajes);
        jMensajes.setBounds(110, 720, 590, 60);

        jfondoMensajes.setFont(new java.awt.Font("Roboto", 0, 24)); // NOI18N
        jPanel4.add(jfondoMensajes);
        jfondoMensajes.setBounds(0, 710, 710, 70);

        fondo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_principal0.png"))); // NOI18N
        fondo.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel4.add(fondo);
        fondo.setBounds(0, 0, 1280, 800);

        pnl_Principal.add(jPanel4, "pnl_principal");

        pnl_confirmacion.setLayout(null);

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnl_confirmacion.add(jLabel10);
        jLabel10.setBounds(307, 250, 690, 180);

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

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/card.png"))); // NOI18N
        jLabel12.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnl_confirmacion.add(jLabel12);
        jLabel12.setBounds(250, 210, 800, 360);

        fnd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_principal0.png"))); // NOI18N
        fnd.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                fndMousePressed(evt);
            }
        });
        pnl_confirmacion.add(fnd);
        fnd.setBounds(0, 0, 1280, 800);

        pnl_Principal.add(pnl_confirmacion, "pnl_confirmacion");

        getContentPane().add(pnl_Principal);
        pnl_Principal.setBounds(0, 0, 1280, 800);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtFacturaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFacturaKeyTyped
        String caracteresAceptados = "[0-9]";
        NovusUtils.limitarCarateres(evt, txtFactura, 10, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_txtFacturaKeyTyped

    private void txtValorKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorKeyTyped
        String caracteresAceptados = "[0-9.]";
        NovusUtils.limitarCarateres(evt, txtValor, 10, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_txtValorKeyTyped

    private void txtFacturaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFacturaFocusGained
        NovusUtils.deshabilitarCopiarPegar(txtFactura);
    }//GEN-LAST:event_txtFacturaFocusGained

    private void txtValorFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtValorFocusGained
        NovusUtils.deshabilitarCopiarPegar(txtValor);
    }//GEN-LAST:event_txtValorFocusGained

    private void NOMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_NOMouseClicked
        mostrarMenuPrincipal();
        if (ejecutarFaturaPOS != null) {
            ejecutarFaturaPOS.run();
            ejecutarFaturaPOS = null;
        } else {
            if (confirmacionFaturacion) {
                panelCliente(aux);
            }
            if (negar != null) {
                negar.run();
            }
        }
    }//GEN-LAST:event_NOMouseClicked

    private void SI1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SI1MouseClicked
        mostrarMenuPrincipal();
        imprimir = true;
        if (handler != null) {
            handler.run();
        }
    }//GEN-LAST:event_SI1MouseClicked

    private void fndMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fndMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_fndMousePressed

    private void juserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_juserActionPerformed
        autenticar();
    }//GEN-LAST:event_juserActionPerformed

    private void jPasswordField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPasswordField1ActionPerformed
        autenticar();
    }//GEN-LAST:event_jPasswordField1ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        selectme();
    }//GEN-LAST:event_jTable1MouseClicked

    private void JComboTipoDocumentosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JComboTipoDocumentosActionPerformed
        if (JComboTipoDocumentos.getSelectedItem() != null) {
            if (JComboTipoDocumentos.getSelectedItem().toString().equals("CONSUMIDOR FINAL")) {
                txtIdentificacion.setText("222222222222");
            } else {
                txtIdentificacion.setText("");
            }
            txtIdentificacion.requestFocus();
            TecladoExtendido teclado = (TecladoExtendido) jPanel2;
            identificadorTipoDocumento = NovusUtils.tipoDeIndentificacion(JComboTipoDocumentos.getSelectedItem().toString().toUpperCase());
            TIPO_DOCUMENTO = NovusUtils.tipoDocumento(NovusUtils.tipoDeIndentificacion(JComboTipoDocumentos.getSelectedItem().toString().toUpperCase()));
            caracteresPermitidos = NovusUtils.obtenerRestriccionCaracteres(JComboTipoDocumentos.getSelectedItem().toString().toUpperCase());
            cantidadCaracteres = NovusUtils.obtenerLimiteCaracteres(JComboTipoDocumentos.getSelectedItem().toString().toUpperCase());
            teclado.habilitarAlfanumeric(NovusUtils.habilitarTecladoAlfanumerico(caracteresPermitidos));
            teclado.habilitarPunto(NovusUtils.habilitarPunto(caracteresPermitidos));
            teclado.habilitarDosPuntos(NovusUtils.habilitarDosPunto(caracteresPermitidos));
        }
    }//GEN-LAST:event_JComboTipoDocumentosActionPerformed

    private void txtIdentificacionKeyTyped(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txtIdentificacionKeyTyped
        NovusUtils.limitarCarateres(evt, txtIdentificacion, cantidadCaracteres, jNotificacion, caracteresPermitidos);
    }// GEN-LAST:event_txtIdentificacionKeyTyped

    private void txtIdentificacionMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_txtIdentificacionMouseReleased
        teclado_extendido.habilitarAlfanumeric(false);
    }// GEN-LAST:event_txtIdentificacionMouseReleased

    private void txtNombreMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_txtNombreMouseReleased
        teclado_extendido.habilitarAlfanumeric(true);
    }// GEN-LAST:event_txtNombreMouseReleased

    private void txtPlacaMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_txtPlacaMouseReleased
        teclado_extendido.habilitarAlfanumeric(true);
    }// GEN-LAST:event_txtPlacaMouseReleased

    private void txtKmsMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_txtKmsMouseReleased
        teclado_extendido.habilitarAlfanumeric(false);
    }// GEN-LAST:event_txtKmsMouseReleased

    private void txtIdentificacionFocusGained(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txtIdentificacionFocusGained
        teclado_extendido.habilitarAlfanumeric(false);
    }// GEN-LAST:event_txtIdentificacionFocusGained

    private void txtNombreFocusGained(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txtNombreFocusGained
        teclado_extendido.habilitarAlfanumeric(true);
    }// GEN-LAST:event_txtNombreFocusGained

    private void txtPlacaFocusGained(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txtPlacaFocusGained
        teclado_extendido.habilitarAlfanumeric(true);
    }// GEN-LAST:event_txtPlacaFocusGained

    private void txtKmsFocusGained(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txtKmsFocusGained
        teclado_extendido.habilitarAlfanumeric(false);
    }// GEN-LAST:event_txtKmsFocusGained

    private void txtKmsKeyTyped(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txtKmsKeyTyped
        soloNumeros(evt);
    }// GEN-LAST:event_txtKmsKeyTyped

    private void jLabel3MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel3MouseReleased
    }// GEN-LAST:event_jLabel3MouseReleased

    private void cmbMangueraItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cmbMangueraItemStateChanged
        cargarDatosProductos();
    }// GEN-LAST:event_cmbMangueraItemStateChanged

    private void cmbCaraItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cmbCaraItemStateChanged
        cargarDatosMangueras();
    }// GEN-LAST:event_cmbCaraItemStateChanged

    private void cmbProductoItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cmbProductoItemStateChanged
        NovusUtils.printLn(Integer.toString(productos.size()));
        if (cmbProducto.getSelectedIndex() != -1) {
            String seleccionado = cmbProducto.getSelectedItem().toString();
            NovusUtils.printLn("Producto Seleccionado: " + seleccionado);
            if (!productos.isEmpty()) {
                float precioSeleccionado = productos.get(seleccionado);
                lblPrecioProducto.setText("$ " + df.format(precioSeleccionado));
                if (!txtValor.getText().isEmpty()) {
                    float valor = Float.parseFloat(txtValor.getText());
                    lblVolCalculado.setText("" + calcularVolumen(valor, precioSeleccionado));
                }
            }
        }
    }// GEN-LAST:event_cmbProductoItemStateChanged

    private void cmbProductoActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cmbProductoActionPerformed
        fix();
    }// GEN-LAST:event_cmbProductoActionPerformed

    private void cmbMangueraActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cmbMangueraActionPerformed
        fix();
    }// GEN-LAST:event_cmbMangueraActionPerformed

    private void cmbCaraActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cmbCaraActionPerformed
        fix();
    }// GEN-LAST:event_cmbCaraActionPerformed

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel3MouseClicked
        cerrar();
    }// GEN-LAST:event_jLabel3MouseClicked

    private void cmbMinutosActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cmbMinutosActionPerformed
        fix();
    }// GEN-LAST:event_cmbMinutosActionPerformed

    private void cmbHorasActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cmbHorasActionPerformed
        fix();
    }// GEN-LAST:event_cmbHorasActionPerformed

    private void txtFechaMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_txtFechaMouseClicked
        fix();
    }// GEN-LAST:event_txtFechaMouseClicked

    private void btnCliente1MouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_btnCliente1MouseClicked
        facturacionNormal = true;
        abrirMediosPago();
    }// GEN-LAST:event_btnCliente1MouseClicked

    private void jbutton_addMedioMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jbutton_addMedioMouseClicked
        agregarMedioPago();
    }// GEN-LAST:event_jbutton_addMedioMouseClicked

    private void tbMediosPagoMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_tbMediosPagoMouseReleased
        seleccionar();
    }// GEN-LAST:event_tbMediosPagoMouseReleased

    private void lblCambioMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_lblCambioMouseClicked
        jmedioCantidad.setText(lblCambio.getText().replace(".", "").replace("$", "").trim());
    }// GEN-LAST:event_lblCambioMouseClicked

    private void btnGuardarMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_btnGuardarMouseClicked
        if (consumoPropio == 20001) {
            CardLayout card = (CardLayout) jPanel1.getLayout();
            card.show(jPanel1, "autenticacion");
            juser.requestFocus();
        } else {
            guardarVenta();
        }
    }// GEN-LAST:event_btnGuardarMouseClicked

    private void jeliminar_medioMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jeliminar_medioMouseClicked
        eliminarMedioPago();
    }// GEN-LAST:event_jeliminar_medioMouseClicked

    private void jeliminar_medio1MouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jeliminar_medio1MouseClicked
        eliminarTodos();
    }// GEN-LAST:event_jeliminar_medio1MouseClicked

    private void jmedioCantidadMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jmedioCantidadMouseClicked
        if (jmedioCantidad.getText().equals("0")) {
            jmedioCantidad.setText("");
        }
    }// GEN-LAST:event_jmedioCantidadMouseClicked

    private void jmedioCantidadFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_jmedioCantidadFocusLost
        if (jmedioCantidad.getText().isEmpty()) {
            jmedioCantidad.setText("0");
        }
    }// GEN-LAST:event_jmedioCantidadFocusLost

    private void txtValorCaretUpdate(javax.swing.event.CaretEvent evt) {// GEN-FIRST:event_txtValorCaretUpdate
        if (!txtValor.getText().isEmpty() && cmbProducto.getSelectedIndex() != -1) {
            float valor = Float.parseFloat(txtValor.getText());
            String seleccionado = cmbProducto.getSelectedItem().toString();
            lblVolCalculado.setText("" + calcularVolumen(valor, productos.get(seleccionado)));
        }
    }// GEN-LAST:event_txtValorCaretUpdate

    private void txtValorKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txtValorKeyReleased
        enviarDatosVenta(evt);
    }// GEN-LAST:event_txtValorKeyReleased

    private void txtFacturaKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txtFacturaKeyReleased
        enviarDatosVenta(evt);
    }// GEN-LAST:event_txtFacturaKeyReleased

    private void txtFacturaActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
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

    int selectedRow = -1;

    private void seleccionar() {
        int r = tbMediosPago.getSelectedRow();
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
        cmbMedioPago.setSelectedItem(medio.getDescripcion().toUpperCase());
        jmedioCantidad.setText(df.format(medio.getRecibido()));
        if (this.mediosPagoVenta.get(index).isComprobante()) {
            jmedioVoucher.setText(medio.getVoucher());
            jmedioVoucher.setEnabled(true);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> JComboTipoDocumentos;
    private javax.swing.JLabel NO;
    private javax.swing.JLabel SI1;
    private javax.swing.JPanel autenticacion;
    private javax.swing.JLabel btnCliente1;
    private javax.swing.JLabel btnGuardar;
    private javax.swing.JComboBox<String> cmbCara;
    private javax.swing.JComboBox<String> cmbHoras;
    private javax.swing.JComboBox<String> cmbManguera;
    public javax.swing.JComboBox<String> cmbMedioPago;
    private javax.swing.JComboBox<String> cmbMinutos;
    private javax.swing.JComboBox<String> cmbProducto;
    private javax.swing.JLabel fnd;
    private javax.swing.JLabel fondo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jMensajes;
    private javax.swing.JLabel jNotificacion;
    private javax.swing.JLabel jNotificacionAutenticacion;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    public static javax.swing.JLabel jTitle;
    private javax.swing.JLabel jbutton_addMedio;
    private javax.swing.JPanel jclock;
    private javax.swing.JPanel jclock2;
    private javax.swing.JLabel jeliminar_medio;
    private javax.swing.JLabel jeliminar_medio1;
    private javax.swing.JLabel jfondoMensajes;
    public javax.swing.JLabel jlblManguera;
    private javax.swing.JTextField jmedioCantidad;
    private javax.swing.JTextField jmedioVoucher;
    private javax.swing.JTextField juser;
    public javax.swing.JLabel lbl;
    public javax.swing.JLabel lblCambio;
    public javax.swing.JLabel lblCara;
    public javax.swing.JLabel lblCara1;
    public javax.swing.JLabel lblCara4;
    public javax.swing.JLabel lblCara5;
    public javax.swing.JLabel lblCara6;
    public javax.swing.JLabel lblFactura;
    public javax.swing.JLabel lblFecha;
    public javax.swing.JLabel lblHora;
    private javax.swing.JLabel lblMensaje;
    public javax.swing.JLabel lblPrecio;
    public javax.swing.JLabel lblPrecioProducto;
    public javax.swing.JLabel lblProducto;
    public javax.swing.JLabel lblRecibidoCalculado;
    public javax.swing.JLabel lblTotalCalculado;
    public javax.swing.JLabel lblValor;
    public javax.swing.JLabel lblVolCalculado;
    public javax.swing.JLabel lblVolumen;
    private javax.swing.JPanel pnlDatosCliente;
    private javax.swing.JPanel pnlMediosPago;
    private javax.swing.JPanel pnlPrincipal;
    private javax.swing.JPanel pnl_Principal;
    private javax.swing.JPanel pnl_confirmacion;
    public javax.swing.JTable tbMediosPago;
    private javax.swing.JPanel tecNumericoPnlPrincipal;
    private javax.swing.ButtonGroup tipoDocumento;
    public javax.swing.JTextField txtFactura;
    private rojeru_san.componentes.RSDateChooser txtFecha;
    public javax.swing.JTextField txtIdentificacion;
    public javax.swing.JTextField txtKms;
    public javax.swing.JTextField txtNombre;
    public javax.swing.JTextField txtPlaca;
    public static javax.swing.JTextField txtValor;
    // End of variables declaration//GEN-END:variables

    private TecladoExtendido teclado_extendido;

    private void init(boolean pos) {
        if (pos) {
            txtFactura.setEnabled(false);
            txtFactura.requestFocus();
            obtenerConsecutivoActualContingencia();
        } else {
            txtFactura.setEnabled(false);
        }
        this.persona = Main.persona;
        btnGuardar.setVisible(false);

        CardLayout layout = (CardLayout) jPanel1.getLayout();
        layout.addLayoutComponent(pnlPrincipal, "pnlPrincipal");
        layout.addLayoutComponent(pnlDatosCliente, "pnlCliente");
        layout.addLayoutComponent(pnlMediosPago, "pnlPagos");

        ajustarFuente(jPanel3, "Cronthox", 0, 18);
        ajustarFuente(jPanel5, "Cronthox", 0, 20);
        NovusUtils.llenarComboBox(JComboTipoDocumentos);
        lblVolCalculado.setFont(new java.awt.Font("Cronthox", 0, 24));
        txtValor.setFont(new java.awt.Font("Cronthox", 0, 24));
        lblPrecioProducto.setFont(new java.awt.Font("Cronthox", 0, 24));

        teclado_extendido = (TecladoExtendido) jPanel2;

        addHora();
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);

        txtFecha.setDatoFecha(new Date());
        textRight = new DefaultTableCellRenderer();
        textRight.setHorizontalAlignment(JLabel.RIGHT);
        textCenter = new DefaultTableCellRenderer();
        textCenter.setHorizontalAlignment(JLabel.CENTER);

        tbMediosPago.setSelectionBackground(new Color(255, 182, 0));
        tbMediosPago.setSelectionForeground(new Color(0, 0, 0));
        tbMediosPago.setFont(new java.awt.Font("Bebas Neue", 0, 24)); // NOI18N

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(186, 12, 47));
        headerRenderer.setForeground(new Color(255, 255, 255));
        tbMediosPago.setFillsViewportHeight(true);
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < tbMediosPago.getModel().getColumnCount(); i++) {
            tbMediosPago.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
            tbMediosPago.getColumnModel().getColumn(i).setCellRenderer(textCenter);
        }

        TableRowSorter<TableModel> rowSorter = new TableRowSorter<TableModel>(tbMediosPago.getModel()) {
            @Override
            public boolean isSortable(int column) {
                super.isSortable(column);
                return false;
            }

        };

        tbMediosPago.setRowSorter(rowSorter);

        jScrollPane1.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        jScrollPane1.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane1.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));

        cargarDatos();

        setLocationRelativeTo(null);
//        setTimeoutMS(100, () -> {
//            if (info != null) {
//                this.info.setVisible(false);
//            }
//        });

        tablaturnos();
        //lblMensaje.setVisible(false);
    }

    private void addHora() {
        LocalDateTime now = LocalDateTime.now();
        String hora, min;
        for (int i = 0; i < 24; i++) {
            hora = "";
            if (i < 10) {
                hora = "0" + i + hora;
            } else {
                hora = i + hora;
            }
            cmbHoras.addItem(hora);
        }
        cmbHoras.setSelectedIndex(now.getHour());

        for (int i = 0; i < 60; i++) {
            min = "";
            if (i < 10) {
                min = "0" + i + min;
            } else {
                min = i + min;
            }
            cmbMinutos.addItem(min);
        }
        cmbMinutos.setSelectedIndex(now.getMinute());

    }

    String line = "";

    private void soloNumeros(KeyEvent e) {
        char c = e.getKeyChar();
        if (!Character.isDigit(c)) {
            e.consume();
        }
    }

    public void show(String name) {
        jPanel1.requestFocus();
        CardLayout layout = (CardLayout) jPanel1.getLayout();
        layout.show(jPanel1, name);
    }

    public static void personas(PersonaBean persona) {
        Main.persona = persona;
    }

    boolean aux = false;

    private void enter() {
        float montoMinimo = 0;
        String activarFE = "";
        try {
            FacturacionElectronica fac = new FacturacionElectronica();
            boolean isObligatorio = sdao.isObligatorioFE();
            boolean porDefectoFacturaElectronica = fac.isDefaultFe();
            JsonObject data = sdao.parametrosFE();
            float montoMinimoFE = data.has("montoMinimo") && !data.get("montoMinimo").isJsonNull() ? data.get("montoMinimo").getAsFloat() : 0;
            String activarModulo = data.has("activar_modulo") && !data.get("activar_modulo").isJsonNull() ? data.get("activar_modulo").getAsString() : "N";
            if (!valorValido()) {
                return;
            }
            totalVenta = Integer.parseInt(txtValor.getText());
            ClienteFacturaElectronica clientFE = new ClienteFacturaElectronica(this, info, true, persona);
            if (NovusConstante.VENTAS_CONTINGENCIA) {
                NovusUtils.printLn("VENTAS_CONTINGENCIA abriendo la vista de facturacio electronica desde -> " + FacturacionManualView.class.getName());
                clientFE.setVisible(true);
                return;
            } else if (isObligatorio && totalVenta >= montoMinimoFE && activarModulo.equals("S")) {
                NovusUtils.printLn("(isObligatorio && totalVenta >= montoMinimoFE && activarModulo.equals(\"S\") abriendo la vista de facturacio electronica desde -> " + FacturacionManualView.class.getName());
                clientFE.setVisible(true);
            } else if (activarModulo.equals("S") && !NovusConstante.VENTAS_CONTINGENCIA) {
                confirmacionFaturacion = true;
                aux = false;
                if (porDefectoFacturaElectronica || isObligatorio) {
                    NovusUtils.printLn("(porDefectoFacturaElectronica || isObligatorio) abriendo la vista de facturacio electronica desde -> " + FacturacionManualView.class.getName());
                    clientFE.setVisible(true);
                    aux = true;
                    confirmacionFaturacion = false;
                } else {
                    jLabel10.setText("<html><center>¿Desea facturación electrónica?</center></html>");
                    showPanel("pnl_confirmacion");
                    handler = () -> {
                        NovusUtils.printLn("handler = () -> { abriendo la vista de facturacio electronica desde -> " + FacturacionManualView.class.getName());
                        clientFE.setVisible(true);
                        aux = true;
                        confirmacionFaturacion = false;
                        panelCliente(aux);
                    };
                }
            } else {
                aux = false;
                isElectronica = false;
                panelCliente(aux);
            }

        } catch (DAOException e) {
            //
        }
    }

    public void panelCliente(boolean aux) {
        if (!aux) {
            show(PNL_CLIENTE);
        }
    }

    private void cerrar() {
        if (pnlMediosPago.isShowing()) {
            if (!isElectronica) {
                show(PNL_CLIENTE);
            } else {
                show(PNL_PRINCIPAL);
                fix();
            }
            NovusConstante.VENTAS_CONTINGENCIA = false;
        } else if (pnlDatosCliente.isShowing()) {
            show(PNL_PRINCIPAL);
            NovusConstante.VENTAS_CONTINGENCIA = false;
            fix();

        } else if (pnlPrincipal.isShowing()) {
            jLabel10.setText("<html><center>Si regresa se perderán los datos sin guardar ¿Desea continuar?</center></html>");
            showPanel("pnl_confirmacion");
            handler = () -> {
                this.info.contadorVentaManual = 0;

                if (menuManual) {
                    if (this.venta != null) {
                        this.venta.setVisible(true);
                    } else {
                        this.info.abrirMenuVentas();
                    }
                } else {
                    this.info.setVisible(true);
                }

                setTimeoutMS(100, () -> {
                    this.dispose();
                });
            };
        } else if (!pnlPrincipal.isShowing() && !pnlDatosCliente.isShowing() && !pnlMediosPago.isShowing()) {

            if (menuManual) {
                this.info.abrirVentaManual();
            } else {
                this.info.setVisible(true);
            }
            NovusConstante.VENTAS_CONTINGENCIA = false;
            info.contadorVentaManual = 0;
            setTimeoutMS(100, () -> {
                this.dispose();
            });
        }
    }

    private void showPanel(String panel) {
        NovusUtils.showPanel(pnl_Principal, panel);
    }

    private void ajustarFuente(JPanel container, String fuente, int style, int size) {
        for (Component component : container.getComponents()) {
            Component comp = component;
            if (comp instanceof JLabel) {
                comp.setFont(new java.awt.Font(fuente, style, size));
            }
        }
    }

    TreeMap<Long, Surtidor> caras;

    private void cargarDatos() {
        caras = sdao.getCaras();
        cmbCara.removeAllItems();
        TreeMap<Integer, String> temp = new TreeMap<>();
        for (Map.Entry<Long, Surtidor> entry : caras.entrySet()) {
            if (!temp.containsKey(entry.getValue().getCara())) {
                cmbCara.addItem(entry.getValue().getCara() + "");
                temp.put(entry.getValue().getCara(), entry.getValue().getCara() + "");
            }
        }
        cargarDatosMangueras();
    }

    private void cargarDatosMangueras() {
        productos.clear();
        cmbManguera.removeAllItems();
        TreeMap<Integer, String> temp = new TreeMap<>();
        if (cmbCara.getSelectedIndex() > -1) {
            long caraSeleccionada = Long.parseLong(cmbCara.getSelectedItem().toString().trim());
            for (Map.Entry<Long, Surtidor> entry : caras.entrySet()) {
                if (entry.getValue().getCara() == caraSeleccionada) {
                    if (!temp.containsKey(entry.getValue().getManguera())) {
                        cmbManguera.addItem(entry.getValue().getManguera() + "");
                        temp.put((int) entry.getValue().getManguera(),
                                entry.getValue().getManguera() + "");
                    }
                    productos.put(entry.getValue().getProductoDescripcion(), entry.getValue().getProductoPrecio());
                }
            }
        }
        cargarDatosProductos();
    }

    TreeMap<String, Float> productos = new TreeMap<>();

    private void cargarDatosProductos() {
        cmbProducto.removeAllItems();
        TreeMap<Long, String> temp = new TreeMap<>();
        if (cmbManguera.getSelectedIndex() > -1) {
            long caraSeleccionada = Long.parseLong(cmbCara.getSelectedItem().toString().trim());
            long mangueraSeleccionada = Long.parseLong(cmbManguera.getSelectedItem().toString().trim());
            for (Map.Entry<Long, Surtidor> entry : caras.entrySet()) {
                if (entry.getValue().getCara() == caraSeleccionada
                        && entry.getValue().getManguera() == mangueraSeleccionada) {
                    if (!temp.containsKey(entry.getValue().getProductoIdentificador())) {
                        cmbProducto.addItem(entry.getValue().getProductoDescripcion());
                        temp.put(entry.getValue().getProductoIdentificador(),
                                entry.getValue().getProductoDescripcion());
                    }
                }
            }
        }
    }

    private float calcularVolumen(float valor, float precio) {
        return Math.round((valor / precio) * 1000) / 1000f;
    }

    private boolean valorValido() {
        String valor = txtValor.getText();
        return !valor.isEmpty() && Integer.parseInt(valor) != 0;
    }

    public JsonObject crearDetalleVenta() {
        NovusUtils.printLn("Creando detalle de la Venta");
        JsonObject detalleVenta = new JsonObject();
        int cara = Integer.parseInt(cmbCara.getSelectedItem().toString());
        int manguera = Integer.parseInt(cmbManguera.getSelectedItem().toString());

        String tipoVenta = "001";
        if (tipoVentaPos) {
            tipoVenta = "002";
            detalleVenta.addProperty("consecutivo", txtFactura.getText());
            detalleVenta.addProperty("contingencia", true);
        } else {
            detalleVenta.addProperty("contingencia", false);
        }

        detalleVenta.addProperty("tipoVenta", tipoVenta);
        detalleVenta.addProperty("fecha", fecha);
        detalleVenta.addProperty("sutidor_detalle_id", sdao.getSurtidorManguera(cara, manguera).getId());
        detalleVenta.addProperty("total_galones", lblVolCalculado.getText());
        detalleVenta.addProperty("placa", txtPlaca.getText());
        detalleVenta.addProperty("isElectronica", isElectronica);
        NovusUtils.printLn("Detalles: " + detalleVenta);
        return detalleVenta;
    }

    public JsonArray crearMediosPago() {
        JsonArray mediosPago = new JsonArray();
        for (MediosPagosBean medio : mediosPagoVenta) {
            JsonObject obj = new JsonObject();
            obj.addProperty("medios_pagos_id", medio.getId());
            obj.addProperty("descripcion", medio.getDescripcion());
            obj.addProperty("valor_recibido", medio.getRecibido());
            obj.addProperty("valor_cambio", medio.getCambio());
            obj.addProperty("valor_total", medio.getValor());
            obj.addProperty("numero_comprobante", medio.getVoucher() + " ");
            mediosPago.add(obj);
        }
        return mediosPago;
    }

    public JsonObject crearDetalleCliente() {
        JsonObject detalleCliente = new JsonObject();
        if (!TIPO_DOCUMENTO.isEmpty()) {
            detalleCliente.addProperty("nombre", txtNombre.getText());
            detalleCliente.addProperty("identificacion", TIPO_DOCUMENTO);
            detalleCliente.addProperty("descripcionTipoDocumento", NovusUtils.tipoDocumento(identificadorTipoDocumento));
            detalleCliente.addProperty("identificacionCliente", txtIdentificacion.getText());
        }
        return detalleCliente;
    }

    private JsonObject crearRespuesta(boolean imprimir) {
        respuesta = new JsonObject();
        respuesta.addProperty("consumoPropio", consumoPropio == 20001);
        respuesta.add("detalleVenta", crearDetalleVenta());
        if (!isElectronica) {
            respuesta.add("cliente", crearDetalleCliente());
        } else {
            clienteFE.addProperty("imprimir", imprimir);
            respuesta.add("cliente", clienteFE);
        }
        respuesta.add("mediosPago", crearMediosPago());
        if (Main.persona != null) {
            respuesta.addProperty("idResponsable", persona.getId());
        }
        NovusUtils.printLn(Main.ANSI_RED + respuesta + Main.ANSI_RESET);
        return respuesta;
    }

    SetupDao sdao = new SetupDao();

    private void loadMediosPago() {
        mediosPagoDisponibles = sdao.getMediosPagosDefault(false, NovusConstante.ID_MEDIO_GOPASS + "");
        for (MediosPagosBean pagos : mediosPagoDisponibles) {
            if (!pagos.getDescripcion().equals(MediosPagosDescription.APPTERPEL)) {
                cmbMedioPago.addItem(pagos.getDescripcion());
            }
        }
        lblTotalCalculado.setText("$ " + df.format(Float.parseFloat(txtValor.getText())));
    }

    float cambio = 0;
    float totalVenta = 0;

    private void actualizar() {
        float recibido = 0;
        if (!mediosPagoVenta.isEmpty()) {
            for (MediosPagosBean mediosPagosBean : mediosPagoVenta) {
                recibido += mediosPagosBean.getRecibido();
            }
        }
        lblRecibidoCalculado.setText("$ " + df.format(recibido));
        cambio = totalVenta - recibido;
        lblCambio.setText("$ " + df.format(cambio));

        if (recibido >= totalVenta) {
            btnGuardar.setVisible(true);
        } else {
            btnGuardar.setVisible(false);
        }
    }

    private void resetFields() {
        jmedioCantidad.setText("");
        cmbMedioPago.setSelectedIndex(0);
        tbMediosPago.clearSelection();
    }

    public void abrirMediosPago() {
        if (!txtIdentificacion.getText().isEmpty()) {
            if (TIPO_DOCUMENTO.isEmpty()) {
                showMessage("POR FAVOR SELECCIONE TIPO DE IDENTIFICACIÓN",
                        "/com/firefuel/resources/btBad.png",
                        true, this::mostrarMenuPrincipal,
                        true, LetterCase.FIRST_UPPER_CASE);
                return;
            }
        }
        loadMediosPago();
        actualizar();
        show(PNL_PAGOS);
    }

    String fecha;

    private boolean validarHora() {
        LocalDateTime now = LocalDateTime.now();
        String[] date = sdf.format(txtFecha.getDatoFecha()).split("-");
        int year = Integer.parseInt(date[0]);
        int month = Integer.parseInt(date[1]);
        int day = Integer.parseInt(date[2]);
        int hour = cmbHoras.getSelectedIndex();
        int min = cmbMinutos.getSelectedIndex();
        int seg = now.getSecond();
        LocalDateTime target = LocalDateTime.of(year, month, day, hour, min);
        fecha = year + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day)
                + " " + (hour < 10 ? "0" + hour : hour) + ":" + (min < 10 ? "0" + min : min) + ":" + (seg < 10 ? "0" + seg : seg);
        return target.isBefore(now);
    }

    private int existMedioPago() {
        int result = -1;
        String medio_pago = cmbMedioPago.getSelectedItem().toString();
        for (int i = 0; i < mediosPagoVenta.size(); i++) {
            if (mediosPagoVenta.get(i).getDescripcion().equals(medio_pago)) {
                result = i;
                break;
            }
        }
        return result;
    }

    private void agregarMedioPago() {
        int r = tbMediosPago.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) tbMediosPago.getModel();
        int index = cmbMedioPago.getSelectedIndex();
        int exist = existMedioPago();
        boolean invalido;
        if (!jmedioCantidad.getText().trim().equals("")) {
            float isRecibido = (float) ((Float.parseFloat(jmedioCantidad.getText().trim().replace(".", "").replace(",", "")) * 100)
                    / 100.0);
            if (isRecibido > 0) {
                MediosPagosBean medio_pago = new MediosPagosBean();
                medio_pago.setId(mediosPagoDisponibles.get(index).getId());
                if (mediosPagoDisponibles.get(index).getId() == 20001) {
                    consumoPropio = mediosPagoDisponibles.get(index).getId();
                }
                medio_pago.setDescripcion(mediosPagoDisponibles.get(index).getDescripcion());
                medio_pago.setCambio(mediosPagoDisponibles.get(index).isCambio());
                invalido = isRecibido > Math.abs(cambio) && !medio_pago.isCambio();
                if (!invalido) {
                    if (exist == -1) {
                        medio_pago.setValor(isRecibido);
                        medio_pago.setRecibido(isRecibido);
                        medio_pago.setCambio(medio_pago.isCambio() ? medio_pago.getRecibido() - medio_pago.getValor() : 0);
                        medio_pago.setCredito(mediosPagoDisponibles.get(index).isCredito());
                        medio_pago.setVoucher(jmedioVoucher.getText().trim());
                        medio_pago.setComprobante(mediosPagoDisponibles.get(index).isComprobante());
                        mediosPagoVenta.add(medio_pago);
                        model.addRow(new Object[]{medio_pago.getDescripcion(), medio_pago.getVoucher(), "$ " + df.format(medio_pago.getRecibido())});
                    } else {
                        if (r != -1) {
                            float rec = isRecibido;
                            mediosPagoVenta.get(exist).setRecibido(rec);
                            mediosPagoVenta.get(exist).setValor(rec);
                            model.setValueAt("$ " + df.format(rec), exist, 2);
                        } else {
                            float sum = mediosPagoVenta.get(exist).getRecibido() + isRecibido;
                            mediosPagoVenta.get(exist).setRecibido(sum);
                            mediosPagoVenta.get(exist).setValor(sum);
                            model.setValueAt("$ " + df.format(sum), exist, 2);
                        }
                    }
                    actualizar();
                    resetFields();
                } else {

                    showMessage("CANTIDAD EXCEDE RESTANTE ( " + medio_pago.getDescripcion().toUpperCase()
                            + " NO ADMITE CAMBIO)", "/com/firefuel/resources/btBad.png",
                            true, this::mostrarMenuPrincipal,
                            true, LetterCase.FIRST_UPPER_CASE);

                    btnGuardar.setVisible(false);
                }
            } else {
                showMensajes("Cantidad debe ser mayor a 0");
                btnGuardar.setVisible(false);
            }

        }
    }

    public final void showMensajes(String texto) {

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

    public void setTimeout(int delay, Runnable runnable) {
        new Thread(() -> {
            try {
                Thread.sleep(delay * 1000);
                runnable.run();
            } catch (InterruptedException e) {
                NovusUtils.printLn("Interrupted.setTimeout" + e.getLocalizedMessage());
                // Restore interrupted state...
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public void setTimeoutMS(int delay, Runnable runnable) {
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            } catch (InterruptedException e) {
                NovusUtils.printLn("Interrupted.setTimeout" + e.getLocalizedMessage());
                // Restore interrupted state...
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public void guardarVenta() {
        if (!facturacionNormal) {
            jLabel10.setText("<html><center>¿Desea comprobante de venta?</center></html>");
            showPanel("pnl_confirmacion");
            handler = () -> {
                imprimir = true;
                enviarVenta();
            };
            negar = () -> {
                imprimir = false;
                enviarVenta();
            };
        } else {
            enviarVenta();
        }

    }

    private void enviarVenta() {
        boolean isArray = false;
        boolean isDebug = true;
        String url = NovusConstante.SECURE_CENTRAL_POINT_VENTA_MANUAL;
        TreeMap<String, String> header = new TreeMap<>();
        header.put("Content-type", "application/json");
        ClientWSAsync client = new ClientWSAsync("GUARDAR VENTA MANUAL", url, NovusConstante.POST, crearRespuesta(imprimir),
                isDebug, isArray, header);
        JsonObject response = null;
        try {
            response = client.esperaRespuesta();
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
        }
        if (response != null) {
            showMessage("VENTA GUARDADA CORRECTAMENTE",
                    "/com/firefuel/resources/btOk.png",
                    true, this::cerrar,
                    true, LetterCase.FIRST_UPPER_CASE);
        } else {
            showMessage("ERROR AL GENERAR LA VENTA",
                    "/com/firefuel/resources/btBad.png",
                    true, this::mostrarMenuPrincipal,
                    true, LetterCase.FIRST_UPPER_CASE);
        }
    }

    private void eliminarMedioPago() {
        int selected = tbMediosPago.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) tbMediosPago.getModel();
        if (selected != -1) {
            if (mediosPagoVenta.get(selected) != null) {
                mediosPagoVenta.remove(selected);
            }
            model.removeRow(selected);
            actualizar();
            resetFields();
        }
    }

    private void eliminarTodos() {
        DefaultTableModel model = (DefaultTableModel) tbMediosPago.getModel();
        model.setRowCount(0);
        mediosPagoVenta.clear();
        actualizar();
        resetFields();
    }

    private boolean consecutivoValido() {
        boolean valido = false;
        try {
            ConsecutivoBean consecutivo = !mdao.facturacionExterna()
                    ? mdao.getConsecutivo("018")
                    : mdao.getConsecutivoRegistry(18);
            if (consecutivo != null) {
                long nroFactura = Long.parseLong(txtFactura.getText());
                long consecutivoActual = consecutivo.getConsecutivo_actual();
                if (nroFactura < consecutivoActual) {
                    showMessage("EL CONSECUTIVO INGRESADO ES MENOR AL ULTIMO REGISTRADO",
                            "/com/firefuel/resources/btBad.png",
                            true, this::mostrarMenuPrincipal,
                            true, LetterCase.FIRST_UPPER_CASE);
                } else if (nroFactura > consecutivoActual) {
                    showMessage("EL CONSECUTIVO INGRESADO ES MAYOR AL ULTIMO REGISTRADO",
                            "/com/firefuel/resources/btBad.png",
                            true, this::mostrarMenuPrincipal,
                            true, LetterCase.FIRST_UPPER_CASE);
                } else {
                    valido = true;
                }
            } else {
                showMessage("NO HAY RANGO DE FACTURACION EN CONTINGENCIA",
                        "/com/firefuel/resources/btBad.png",
                        true, this::mostrarMenuPrincipal,
                        true, LetterCase.FIRST_UPPER_CASE);
            }
        } catch (DAOException ex) {
            Logger.getLogger(FacturacionManualView.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return valido;
    }

    private void enviarDatosVenta(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (txtFactura.isEnabled()) {
                if (txtFactura.getText().isEmpty()) {
                    showMessage("POR FAVOR VERIFIQUE EL NO. DE FACTURA",
                            "/com/firefuel/resources/btBad.png",
                            true, this::mostrarMenuPrincipal,
                            true, LetterCase.FIRST_UPPER_CASE);
                    return;
                } else {
                    if (!consecutivoValido()) {
                        return;
                    }
                }
            } else {
                if (txtFactura.getText().isEmpty()) {
                    showMessage("NO HAY RANGO DE FACTURACION EN CONTINGENCIA",
                            "/com/firefuel/resources/btBad.png",
                            true, this::mostrarMenuPrincipal,
                            true, LetterCase.FIRST_UPPER_CASE);
                    return;
                }
            }
            if (!validarHora()) {
                showMessage("LA FECHA ES INVALIDA, POR FAVOR VERIFIQUE.",
                        "/com/firefuel/resources/btBad.png",
                        true, this::mostrarMenuPrincipal,
                        true, LetterCase.FIRST_UPPER_CASE);
                return;
            }
            enter();
        }
    }

    private void fix() {
        if (txtFactura.isEnabled()) {
            txtFactura.requestFocus();
        } else {
            txtValor.requestFocus();
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

    public void mostrarSubPanel(JPanel panel) {
        CardLayout layout = (CardLayout) pnl_Principal.getLayout();
        pnl_Principal.add("pnl_ext", panel);
        layout.show(pnl_Principal, "pnl_ext");
    }

    public void mostrarMenuPrincipal() {
        CardLayout layout = (CardLayout) pnl_Principal.getLayout();
        layout.show(pnl_Principal, "pnl_principal");
    }

    /*validacion de usuario*/
    private void autenticar() {
        CardLayout card = (CardLayout) jPanel1.getLayout();
        card.show(jPanel1, "autenticacion");
        String user = juser.getText();
        String pass = new String(jPasswordField1.getPassword());
        if (user.isEmpty()) {
            juser.requestFocus();
        } else if (pass.isEmpty()) {
            jPasswordField1.requestFocus();
        } else {
            try {
               // PersonasDao pdao = new PersonasDao();
               //int id = pdao.findAdmin(user, pass, true);
                FindAdminUseCase findAdminUseCase = new FindAdminUseCase();
                int id = findAdminUseCase.execute(user, pass, true);
                if (id != -1) {
                    guardarVenta();
                } else {
                    jNotificacionAutenticacion.setText("Este usuario no tiene acceso al módulo");
                    setTimeout(3, () -> jNotificacionAutenticacion.setText(""));
                }
            } catch (Exception ex) {
                NovusUtils.printLn(ex.getMessage());
            }
        }
    }

    private void tablaturnos() {
        textCenter = new DefaultTableCellRenderer();
        textCenter.setHorizontalAlignment(JLabel.CENTER);
        jTable1.setAutoCreateRowSorter(true);
        jTable1.setFont(new java.awt.Font("Bebas Neue", 0, 24));
        jTable1.setSelectionBackground(new Color(255, 182, 0));
        jTable1.setSelectionForeground(new Color(0, 0, 0));
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(186, 12, 47));
        headerRenderer.setForeground(new Color(255, 255, 255));
        jTable1.setFillsViewportHeight(true);
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < jTable1.getModel().getColumnCount(); i++) {
            jTable1.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
            jTable1.getColumnModel().getColumn(i).setCellRenderer(textCenter);
        }
        TableRowSorter<TableModel> rowSorter = new TableRowSorter<TableModel>(jTable1.getModel()) {
            @Override
            public boolean isSortable(int column) {
                super.isSortable(column);
                return false;
            }
        };
        jTable1.setRowSorter(rowSorter);

        jScrollPane1.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        jScrollPane1.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane1.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));

        mostrarTurnos();
    }

    private void mostrarTurnos() {
        SimpleDateFormat sdf2 = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
        try {
            Main.info.recargarPersona();
            ArrayList<PersonaBean> personas = new ArrayList<>();
            if (Main.persona != null && InfoViewController.turnosPersonas != null
                    && !InfoViewController.turnosPersonas.isEmpty()) {
                personas = InfoViewController.turnosPersonas;
            }
            DefaultTableModel dm = (DefaultTableModel) jTable1.getModel();
            dm.getDataVector().removeAllElements();
            dm.fireTableDataChanged();
            DefaultTableModel defaultModel = (DefaultTableModel) jTable1.getModel();
            for (PersonaBean list : personas) {
                defaultModel.addRow(new Object[]{list.getNombre(), list.getIdentificacion(),
                    sdf2.format(list.getFechaInicio())});
            }
        } catch (Exception ex) {
            Logger.getLogger(TurnosFinalizarViewController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void selectme() {
        int r = jTable1.getSelectedRow();
        if (r > -1) {
            String identificacion;
            identificacion = (String) jTable1.getValueAt(r, 1);
            juser.setText(identificacion);
            jPasswordField1.setText("");
        }
    }

    private void obtenerConsecutivoActualContingencia() {
        try {
            if (!mdao.facturacionExterna()) {
                ConsecutivoBean consecutivo = mdao.getConsecutivo("018");
                if (consecutivo != null) {
                    txtFactura.setText(Long.toString(consecutivo.getConsecutivo_actual()));
                } else {
                    lblMensaje.setVisible(true);
                    lblMensaje.setText("No hay rango de facturación en contingencia");
                    setTimeout(30, () -> {
                        lblMensaje.setVisible(false);
                        lblMensaje.setText("");
                    });
                }
            } else {
                ConsecutivoBean consecutivo = mdao.getConsecutivoRegistry(18);
                if (consecutivo != null) {
                    txtFactura.setText(Long.toString(consecutivo.getConsecutivo_actual()));
                } else {
                    lblMensaje.setVisible(true);
                    lblMensaje.setText("No hay rango de facturación en contingencia");
                    setTimeout(30, () -> {
                        lblMensaje.setVisible(false);
                        lblMensaje.setText("");
                    });
                }
            }
        } catch (DAOException ex) {
            Logger.getLogger(FacturacionManualView.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

}
