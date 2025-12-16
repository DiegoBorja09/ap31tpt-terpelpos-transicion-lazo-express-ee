package com.firefuel;

import com.bean.*;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.application.useCases.categorias.ObtenerTodasCategoriasConProductosUseCase;
import com.dao.DAOException;
import com.dao.MovimientosDao;
import com.firefuel.components.panelesPersonalizados.BordesRedondos;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

import com.interfaces.BackgroundTask;
import teclado.view.common.TecladoNumerico;
import com.application.useCases.consecutivos.ObtenerAlertasResolucionUseCase;
import com.application.useCases.consecutivos.ObtenerConsecutivoUseCase;
import com.application.useCases.ventas.VentaEnCursoUseCase;
import com.application.useCases.productos.ObtenerProductosCanastillaUseCase;

public final class StoreViewController extends javax.swing.JFrame {
    ObtenerAlertasResolucionUseCase obtenerAlertasResolucionUseCase;
    ObtenerConsecutivoUseCase obtenerConsecutivoUseCase;
    boolean primeraVenta = true;
    int cantidad = 0;
    int pos = 0;
    long factura = 0l;
    InfoViewController parent = null;
    FacturacionManualKCView dialog;
    ConsecutivoBean newConsecutivo;
    boolean mostrarMenu = false;
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);
    ObtenerTodasCategoriasConProductosUseCase useCase;
    static Color colorProducto = new Color(102, 102, 102);
    MovimientosBean movimiento;
    MovimientosDao mdao = new MovimientosDao();
    public static boolean isCombustible = true;
    private TreeMap<Long, MovimientosDetallesBean> seleccionado;
    private final TreeMap<Long, ArrayList<MovimientosDetallesBean>> productosCategoria = new TreeMap<>();

    static MovimientosDetallesBean seleccion;
    public ArrayList<MediosPagosBean> medios = new ArrayList<>();
    CategoriaDetallesListView panelPromociones;
    PersonaBean persona;
    ArrayList<CategoriaBean> listaCategorias = null;
    String card = null;
    ArrayList<MovimientosDetallesBean> lista = null;
    long totalRecords = 0;

    boolean manual = false;
    String fecha = "";
    boolean ventanaVenta = false;
    boolean vistaprincipal = false;
    SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);

    public StoreViewController(InfoViewController parent, boolean modal) {
        this.parent = parent;
        this.persona = Main.persona;
        initComponents();
        this.init();
        showMensajes(null, null);
    }

    public StoreViewController(InfoViewController parent, boolean modal, boolean manual, FacturacionManualKCView dialogo, Long factura, String fecha, boolean ventanaVenta, boolean vistaprincipal) {
        this.parent = parent;
        this.persona = Main.persona;
        this.dialog = dialogo;
        this.factura = factura;
        this.manual = manual;
        this.fecha = fecha;
        this.ventanaVenta = ventanaVenta;
        this.vistaprincipal = vistaprincipal;
        initComponents();
        this.init();
        showMensajes(null, null);
    }

    public void init() {
        this.obtenerAlertasResolucionUseCase = new ObtenerAlertasResolucionUseCase(NovusConstante.IS_DEFAULT_FE ? 31 : 9, "CAN");
        this.obtenerConsecutivoUseCase = new ObtenerConsecutivoUseCase(NovusConstante.IS_DEFAULT_FE, "CAN");
        NovusUtils.ajusteFuente(getContentPane().getComponents(), NovusConstante.EXTRABOLD);
        NovusUtils.ajusteFuente(panelteclado.getComponents(), NovusConstante.EXTRABOLD);
        NovusUtils.ajusteFuente(pn_teclado.getComponents(), NovusConstante.EXTRABOLD);
        NovusUtils.ajusteFuente(pn_menu.getComponents(), NovusConstante.EXTRABOLD);
        NovusUtils.ajusteFuente(pn_categorias.getComponents(), NovusConstante.EXTRABOLD);
        NovusUtils.ajusteFuente(jPanel2.getComponents(), NovusConstante.EXTRABOLD);

        jPanel4.setBounds(0, 0, 495, 250);
        jPanel4.setPreferredSize(new java.awt.Dimension(495, 250));
        jScrollPane1.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        jScrollPane1.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane1.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));

        jScrollPane2.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });

        jScrollPane2.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane2.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));

        jScrollPane3.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });

        jScrollPane3.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane3.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane4.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        jScrollPane4.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane4.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));
        this.persona = Main.persona;
        showMensajes(null, null);
        limpiarTodos();
        mostrarPaneCategorias();
        jMensajes.setVisible(true);
        jfondoMensajes.setVisible(true);
        validarResolucion();

    }

    public void validarResolucion() {
        //int resolucion = NovusConstante.IS_DEFAULT_FE ? 31 : 9;
        JsonObject info = obtenerAlertasResolucionUseCase.execute();
        NovusUtils.printLn("Info: " + info);
        if (info.has("rangoFecha")) {

            NovusUtils.printLn("Info Rangos: " + info);

            NovusUtils.printLn("Mensajes ");

            int rangoFecha = info.get("rangoFecha").getAsInt();
            int rangoCon = info.get("rangoConsecutivos").getAsInt();
            int alertaDias = info.get("alertaDias").getAsInt();
            int alertConsecutivos = info.get("alertaConsecutivos").getAsInt();

            jLabel3.setVisible(true);
            jfondoMensajes.setVisible(true);
            if (rangoFecha <= alertaDias) {

                if (rangoFecha == alertaDias) {
                    setTimeout(10, () -> {
                        jfondoMensajes.setVisible(false);
                        jLabel3.setVisible(false);
                        jLabel3.setText("");
                    });
                    jLabel3.setText("SU RESOLUCION VENCERÁ HOY");
                    jLabel3.setVisible(true);
                } else {
                    setTimeout(10, () -> {
                        jfondoMensajes.setVisible(false);
                        jLabel3.setVisible(false);
                        jLabel3.setText("");
                    });
                    jLabel3.setText("SU RESOLUCION VENCERA EN " + rangoFecha + " DÍAS");
                    jLabel3.setVisible(true);
                }
            } else if (rangoCon <= alertConsecutivos) {
                setTimeout(10, () -> {
                    jfondoMensajes.setVisible(false);
                    jLabel3.setVisible(false);
                    jLabel3.setText("");
                });
                jLabel3.setText("SU RESOLUCION VENCERÁ POR CONSECUTIVOS");
                jLabel3.setVisible(true);
            }
            jLabel3.updateUI();
        }
    }

    public void limpiarTodos() {
        movimiento = new MovimientosBean();
        movimiento.setEmpresa(Main.credencial.getEmpresa());
        movimiento.setEmpresasId(Main.credencial.getEmpresas_id());
        movimiento.setPersonaId(persona.getId());
        movimiento.setPersonaNombre(persona.getNombre());
        movimiento.setPersonaApellidos(persona.getApellidos() != null ? persona.getApellidos() : "");
        movimiento.setPersonaNit(persona.getIdentificacion());
        movimiento.setClienteId(2);
        movimiento.setClienteNombre("CLIENTES VARIOS");
        movimiento.setClienteNit("2222222");
        seleccionado = new TreeMap<>();
        jpromotor.setText(persona.getNombre() + " " + (persona.getApellidos() != null ? persona.getApellidos() : ""));
        jScrollPane1.getVerticalScrollBar().setPreferredSize(new Dimension(25, 0));

        updateConsecutivoXS();
        repaintPanel();
        resetEntries();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jMensajes = new javax.swing.JLabel();
        jfondoMensajes = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jrecibo = new javax.swing.JLabel();
        jentry_lbl = new javax.swing.JLabel();
        jentry_txt = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        jcanastavacia = new javax.swing.JLabel();
        jDetalles = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jsubmenu = new javax.swing.JLabel();
        jImpuesto = new javax.swing.JLabel();
        jSubTotal = new javax.swing.JLabel();
        jTotal = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        panelteclado = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        pn_despachos = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jPanel9 = new javax.swing.JPanel();
        breadcumb_categoria1 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        pn_categorias = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel6 = new javax.swing.JPanel();
        breadcumb_path = new javax.swing.JLabel();
        breadcumb_categoria = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        pn_menu = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jPanel7 = new javax.swing.JPanel();
        jcanastilla_accent = new javax.swing.JLabel();
        jcombustible_accent = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        pn_teclado = new javax.swing.JPanel();
        jPanel1 = new TecladoNumerico();
        jcerrar = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jpromotor = new javax.swing.JLabel();
        jpromotor1 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        prevButton = new javax.swing.JLabel();
        nextButton = new javax.swing.JLabel();
        progressButton = new javax.swing.JLabel();
        totalRecordsLabel = new javax.swing.JLabel();

        String[] pageSizes = {"20", "50", "100"};
        pageSizeCombo = new javax.swing.JComboBox<>(pageSizes);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1280, 800));
        setUndecorated(true);

        jPanel2.setOpaque(false);
        jPanel2.setLayout(null);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel2.add(jLabel3);
        jLabel3.setBounds(17, 730, 430, 60);

        jMensajes.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jMensajes.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel2.add(jMensajes);
        jMensajes.setBounds(80, 740, 360, 40);

        jfondoMensajes.setFont(new java.awt.Font("Roboto", 0, 24)); // NOI18N
        jPanel2.add(jfondoMensajes);
        jfondoMensajes.setBounds(10, 730, 470, 60);

        jLabel11.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-warning-xsmall.png"))); // NOI18N
        jLabel11.setText("HISTORIAL");
        jLabel11.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel11MouseReleased(evt);
            }
        });
        jPanel2.add(jLabel11);
        jLabel11.setBounds(630, 730, 130, 60);

        jLabel13.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-warning-xsmall.png"))); // NOI18N
        jLabel13.setText("INVENTARIO");
        jLabel13.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel13MouseReleased(evt);
            }
        });
        jPanel2.add(jLabel13);
        jLabel13.setBounds(470, 730, 160, 60);

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel12MouseReleased(evt);
            }
        });
        jPanel2.add(jLabel12);
        jLabel12.setBounds(10, 10, 70, 71);

        jLabel4.setFont(new java.awt.Font("Roboto", 0, 30)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 219, 0));
        jLabel4.setText("VENTA No.");
        jPanel2.add(jLabel4);
        jLabel4.setBounds(120, 10, 220, 30);

        jrecibo.setBackground(new java.awt.Color(255, 255, 255));
        jrecibo.setFont(new java.awt.Font("Arial Narrow", 1, 38)); // NOI18N
        jrecibo.setForeground(new java.awt.Color(255, 255, 255));
        jrecibo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jrecibo.setText("10000");
        jPanel2.add(jrecibo);
        jrecibo.setBounds(120, 40, 220, 40);

        jentry_lbl.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jentry_lbl.setForeground(new java.awt.Color(255, 219, 0));
        jentry_lbl.setText("Ingresar PLU:");
        jPanel2.add(jentry_lbl);
        jentry_lbl.setBounds(660, 95, 190, 80);

        jentry_txt.setBackground(new java.awt.Color(186, 12, 47));
        jentry_txt.setFont(new java.awt.Font("Roboto", 0, 24)); // NOI18N
        jentry_txt.setForeground(new java.awt.Color(255, 255, 255));
        jentry_txt.setBorder(null);
        jentry_txt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jentry_txtFocusGained(evt);
            }
        });
        jentry_txt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jentry_txtActionPerformed(evt);
            }
        });
        jentry_txt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jentry_txtKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jentry_txtKeyTyped(evt);
            }
        });
        jPanel2.add(jentry_txt);
        jentry_txt.setBounds(870, 110, 270, 40);

        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setOpaque(false);
        jPanel4.setPreferredSize(new java.awt.Dimension(460, 0));
        jPanel4.setLayout(null);

        jcanastavacia.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jcanastavacia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndCanastaVacia.png"))); // NOI18N
        jPanel4.add(jcanastavacia);
        jcanastavacia.setBounds(10, 4, 580, 360);

        jScrollPane1.setViewportView(jPanel4);

        jPanel2.add(jScrollPane1);
        jScrollPane1.setBounds(660, 190, 590, 370);

        jDetalles.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        jDetalles.setBorder(null);
        jDetalles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jDetallesActionPerformed(evt);
            }
        });
        jDetalles.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jDetallesKeyReleased(evt);
            }
        });
        jPanel2.add(jDetalles);
        jDetalles.setBounds(1020, 0, 0, 50);

        jLabel1.setFont(new java.awt.Font("Roboto", 0, 16)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 51, 51));
        jLabel1.setText("Impuestos    ..................................");
        jPanel2.add(jLabel1);
        jLabel1.setBounds(680, 620, 270, 22);

        jsubmenu.setFont(new java.awt.Font("Roboto", 0, 16)); // NOI18N
        jsubmenu.setForeground(new java.awt.Color(51, 51, 51));
        jsubmenu.setText("Subtotal        ..................................");
        jPanel2.add(jsubmenu);
        jsubmenu.setBounds(680, 590, 270, 22);

        jImpuesto.setFont(new java.awt.Font("Roboto", 0, 16)); // NOI18N
        jImpuesto.setForeground(new java.awt.Color(51, 51, 51));
        jImpuesto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jImpuesto.setText("0");
        jPanel2.add(jImpuesto);
        jImpuesto.setBounds(1060, 620, 180, 22);

        jSubTotal.setFont(new java.awt.Font("Roboto", 0, 16)); // NOI18N
        jSubTotal.setForeground(new java.awt.Color(51, 51, 51));
        jSubTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jSubTotal.setText("0");
        jPanel2.add(jSubTotal);
        jSubTotal.setBounds(1060, 590, 180, 22);

        jTotal.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jTotal.setText("0");
        jPanel2.add(jTotal);
        jTotal.setBounds(1000, 650, 240, 40);

        jLabel5.setFont(new java.awt.Font("Roboto", 1, 20)); // NOI18N
        jLabel5.setText("TOTAL A PAGAR ..................");
        jPanel2.add(jLabel5);
        jLabel5.setBounds(680, 650, 300, 40);

        panelteclado.setMinimumSize(new java.awt.Dimension(512, 600));
        panelteclado.setOpaque(false);
        panelteclado.setLayout(null);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.CardLayout());

        pn_despachos.setOpaque(false);
        pn_despachos.setLayout(null);

        jScrollPane6.setBorder(null);
        jScrollPane6.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane6.setOpaque(false);

        jPanel9.setMaximumSize(new java.awt.Dimension(600, 490));
        jPanel9.setOpaque(false);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 490, Short.MAX_VALUE)
        );

        jScrollPane6.setViewportView(jPanel9);

        pn_despachos.add(jScrollPane6);
        jScrollPane6.setBounds(18, 80, 580, 440);

        breadcumb_categoria1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        breadcumb_categoria1.setForeground(new java.awt.Color(153, 153, 153));
        breadcumb_categoria1.setText("DESPACHOS COMBUSTIBLE");
        breadcumb_categoria1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                breadcumb_categoria1MouseReleased(evt);
            }
        });
        pn_despachos.add(breadcumb_categoria1);
        breadcumb_categoria1.setBounds(20, 20, 400, 60);

        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndListadoProd.png"))); // NOI18N
        pn_despachos.add(jLabel21);
        jLabel21.setBounds(0, 10, 620, 530);

        jPanel3.add(pn_despachos, "pn_despachos");

        pn_categorias.setOpaque(false);
        pn_categorias.setLayout(null);

        jScrollPane2.setBorder(null);
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setOpaque(false);

        jPanel5.setMaximumSize(new java.awt.Dimension(600, 490));
        jPanel5.setOpaque(false);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 490, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(jPanel5);

        pn_categorias.add(jScrollPane2);
        jScrollPane2.setBounds(18, 80, 580, 440);

        jScrollPane3.setBorder(null);
        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 598, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 488, Short.MAX_VALUE)
        );

        jScrollPane3.setViewportView(jPanel6);

        pn_categorias.add(jScrollPane3);
        jScrollPane3.setBounds(18, 80, 580, 440);

        breadcumb_path.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        breadcumb_path.setForeground(new java.awt.Color(153, 153, 153));
        pn_categorias.add(breadcumb_path);
        breadcumb_path.setBounds(190, 20, 300, 60);

        breadcumb_categoria.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        breadcumb_categoria.setForeground(new java.awt.Color(153, 153, 153));
        breadcumb_categoria.setText("CATEGORIAS");
        breadcumb_categoria.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                breadcumb_categoriaMouseReleased(evt);
            }
        });
        pn_categorias.add(breadcumb_categoria);
        breadcumb_categoria.setBounds(20, 20, 170, 60);

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndListadoProd.png"))); // NOI18N
        pn_categorias.add(jLabel15);
        jLabel15.setBounds(0, 10, 620, 530);

        jPanel3.add(pn_categorias, "pn_categorias");

        pn_menu.setBackground(new java.awt.Color(255, 255, 255));
        pn_menu.setOpaque(false);
        pn_menu.setLayout(null);

        jScrollPane4.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel7.setPreferredSize(new java.awt.Dimension(580, 0));
        jPanel7.setLayout(null);
        jScrollPane4.setViewportView(jPanel7);

        pn_menu.add(jScrollPane4);
        jScrollPane4.setBounds(20, 100, 580, 390);

        jcanastilla_accent.setBackground(new java.awt.Color(255, 219, 0));
        jcanastilla_accent.setOpaque(true);
        pn_menu.add(jcanastilla_accent);
        jcanastilla_accent.setBounds(370, 50, 180, 2);

        jcombustible_accent.setBackground(new java.awt.Color(255, 219, 0));
        jcombustible_accent.setOpaque(true);
        pn_menu.add(jcombustible_accent);
        jcombustible_accent.setBounds(70, 50, 180, 2);

        jLabel20.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("     COMBUSTIBLE ");
        jLabel20.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel20.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel20MouseReleased(evt);
            }
        });
        pn_menu.add(jLabel20);
        jLabel20.setBounds(20, 10, 270, 50);

        jLabel19.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("     CANASTILLA    ");
        jLabel19.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel19.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel19MouseReleased(evt);
            }
        });
        pn_menu.add(jLabel19);
        jLabel19.setBounds(330, 10, 260, 50);

        prevButton.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        prevButton.setForeground(new java.awt.Color(255, 255, 255));
        prevButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        prevButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/btnBack.png"))); // NOI18N
        prevButton.setText("");
        prevButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        prevButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ejecutarSwingWorker(() -> {
                    if (currentPage > 0) {
                        currentPage--;
                        getProductsC();
                    }
                });
            }
        });
        pn_menu.add(prevButton);
        prevButton.setBounds(495, 490, 60, 50);

        nextButton.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        nextButton.setForeground(new java.awt.Color(255, 255, 255));
        nextButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/btnNext.png"))); // NOI18N
        nextButton.setText("");
        nextButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        nextButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ejecutarSwingWorker(() -> {
                    if ((long) (currentPage + 1) * pageSize < totalRecords) {
                        currentPage++;
                        getProductsC();
                    }
                });
            }
        });
        pn_menu.add(nextButton);
        nextButton.setBounds(545, 490, 60, 50);


        totalRecordsLabel.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        totalRecordsLabel.setForeground(new java.awt.Color(0, 0, 0));

        totalRecordsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        actualizarEtiquetaPaginacion(totalRecords, pageSize, currentPage, totalRecordsLabel);
        totalRecordsLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        pn_menu.add(totalRecordsLabel);
        totalRecordsLabel.setBounds(390, 490, 120, 50);


        pageSizeCombo.addActionListener(e -> {

            ejecutarSwingWorker(() -> {
                int newPageSize = Integer.parseInt((String) Objects.requireNonNull(pageSizeCombo.getSelectedItem()));

                if(newPageSize!=pageSize){
                    currentPage = 0;
                    pageSize = newPageSize;
                    System.out.println("pageSize "+pageSize);
                    getProductsC();
                }
            });

        });

        pn_menu.add(pageSizeCombo);
        pageSizeCombo.setBounds(20, 500, 60, 30);

        progressButton.setPreferredSize(new Dimension(35, 30));

        progressButton.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        progressButton.setForeground(new java.awt.Color(255, 255, 255));
        progressButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        progressButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/btReintentarsmall.png"))); // NOI18N
        progressButton.setText("");
        progressButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pn_menu.add(progressButton);
        progressButton.setBounds(90, 500, 35, 30);

        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndTabs.png"))); // NOI18N
        pn_menu.add(jLabel18);
        jLabel18.setBounds(0, 0, 620, 550);

        jPanel3.add(pn_menu, "pn_menu");

        pn_teclado.setBackground(new java.awt.Color(255, 255, 255));
        pn_teclado.setOpaque(false);
        pn_teclado.setLayout(null);

        jPanel1.setPreferredSize(new java.awt.Dimension(440, 325));
        jPanel1.setRequestFocusEnabled(false);
        jPanel1.setLayout(null);
        pn_teclado.add(jPanel1);
        jPanel1.setBounds(30, 40, 550, 460);

        jcerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/BtCerrar.png"))); // NOI18N
        jcerrar.setText("jLabel6");
        jcerrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jcerrarMouseReleased(evt);
            }
        });
        pn_teclado.add(jcerrar);
        jcerrar.setBounds(570, 0, 50, 50);
        pn_teclado.add(jLabel2);
        jLabel2.setBounds(-6, -5, 630, 530);

        jPanel3.add(pn_teclado, "pn_teclado");

        panelteclado.add(jPanel3);
        jPanel3.setBounds(0, 0, 620, 570);

        jPanel2.add(panelteclado);
        panelteclado.setBounds(13, 90, 620, 540);

        jLabel16.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"))); // NOI18N
        jLabel16.setText("COMPLETAR VENTA");
        jLabel16.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel16.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel16MouseReleased(evt);
            }
        });
        jPanel2.add(jLabel16);
        jLabel16.setBounds(890, 730, 280, 60);

        jLabel17.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-xsmall.png"))); // NOI18N
        jLabel17.setText("BORRAR");
        jLabel17.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel17MouseReleased(evt);
            }
        });
        jPanel2.add(jLabel17);
        jLabel17.setBounds(770, 730, 123, 60);

        jpromotor.setFont(new java.awt.Font("Conthrax", 1, 36)); // NOI18N
        jpromotor.setForeground(new java.awt.Color(255, 255, 255));
        jpromotor.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jpromotor.setText("PROMOTOR");
        jPanel2.add(jpromotor);
        jpromotor.setBounds(680, 30, 580, 40);

        jpromotor1.setFont(new java.awt.Font("Conthrax", 1, 18)); // NOI18N
        jpromotor1.setForeground(new java.awt.Color(255, 219, 0));
        jpromotor1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jpromotor1.setText("PROMOTOR");
        jPanel2.add(jpromotor1);
        jpromotor1.setBounds(1110, 10, 160, 24);

        jLabel8.setFont(new java.awt.Font("Juicebox", 0, 22)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btTeclado.png"))); // NOI18N
        jLabel8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel8MouseReleased(evt);
            }
        });
        jPanel2.add(jLabel8);
        jLabel8.setBounds(1170, 100, 80, 60);

        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel2.add(jLabel37);
        jLabel37.setBounds(1170, 725, 10, 68);

        jLabel6.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jLabel6.setText("CATEGORIAS");
        jLabel6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel6MouseReleased(evt);
            }
        });
        jPanel2.add(jLabel6);
        jLabel6.setBounds(20, 640, 180, 60);

        jLabel9.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jLabel9.setText("PRODUCTOS");
        jLabel9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel9MouseReleased(evt);
            }
        });
        jPanel2.add(jLabel9);
        jLabel9.setBounds(200, 640, 180, 60);

        jLabel10.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-small.png"))); // NOI18N
        jLabel10.setText("<html><center>ULT. VENTA<br/>COMBUSTIBLE</center></html>");
        jLabel10.setEnabled(false);
        jLabel10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel10MouseReleased(evt);
            }
        });
        jPanel2.add(jLabel10);
        jLabel10.setBounds(450, 640, 180, 60);

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd.png"))); // NOI18N
        jPanel2.add(jLabel7);
        jLabel7.setBounds(0, 0, 1280, 800);


        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1280, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    public void ejecutarSwingWorker(BackgroundTask task) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                progressButton.setIcon(new ImageIcon(getClass().getResource("/com/firefuel/resources/btSincro_small.gif")));
                task.run(); // lógica personalizada
                return null;
            }

            @Override
            protected void done() {
                showPnMenu();
                cargarPanelCanastilla();
                progressButton.setIcon(new ImageIcon(getClass().getResource("/com/firefuel/resources/botones/btReintentarsmall.png")));
                actualizarEtiquetaPaginacion(totalRecords, pageSize, currentPage, totalRecordsLabel);
            }
        };
        worker.execute();
    }
    public void actualizarEtiquetaPaginacion(long totalRecords, int pageSize, int currentPage, JLabel totalRecordsLabel) {
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        int currentPageDisplay = currentPage + 1;
        String mensaje = "Listando " + currentPageDisplay + " de " + totalPages;
        totalRecordsLabel.setText(mensaje);
    }

    private void jLabel10MouseReleased(java.awt.event.MouseEvent evt) {
    }
    private void jentry_txtKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jentry_txtKeyTyped
        String caracteresAceptados = "[0-9]";
        NovusUtils.limitarCarateres(evt, jentry_txt, 20, jMensajes, caracteresAceptados);
    }//GEN-LAST:event_jentry_txtKeyTyped

    private void jentry_txtFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jentry_txtFocusGained
        NovusUtils.deshabilitarCopiarPegar(jentry_txt);
    }//GEN-LAST:event_jentry_txtFocusGained

    private void breadcumb_categoria1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_breadcumb_categoria1MouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_breadcumb_categoria1MouseReleased

    private void jLabel16MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel16MouseReleased
        StoreConfirmarViewController.CANASTILLA = true;
        generarVenta();
    }//GEN-LAST:event_jLabel16MouseReleased

    private void jLabel17MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel17MouseReleased
        borrar();
    }//GEN-LAST:event_jLabel17MouseReleased

    private void jLabel19MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel19MouseReleased
        cargarPanelCanastilla();
    }//GEN-LAST:event_jLabel19MouseReleased

    private void jcerrarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jcerrarMouseReleased
        cerrarTeclado();
    }//GEN-LAST:event_jcerrarMouseReleased

    private void jLabel6MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseReleased
        mostrarPaneCategorias();
    }//GEN-LAST:event_jLabel6MouseReleased

    private void jLabel9MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel9MouseReleased
        mostrarPaneMenu();
        cargarPanelCanastilla();
    }//GEN-LAST:event_jLabel9MouseReleased

    private void jLabel12MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseReleased
        cerrar();
    }//GEN-LAST:event_jLabel12MouseReleased

    private void jLabel11MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel11MouseReleased
        abrirReporteCanastilla();
    }//GEN-LAST:event_jLabel11MouseReleased

    private void jLabel13MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel13MouseReleased
        abrirInventarioProductos();
    }//GEN-LAST:event_jLabel13MouseReleased

    private void jentry_txtActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jentry_txtActionPerformed
        boolean presiono = ((TecladoNumerico) jPanel1).isPresiono();
        if (presiono) {
            agregarProducto("TECLADO", jentry_txt.getText(), null);
        } else {
            agregarProducto("LECTOR", jentry_txt.getText(), null);
        }
        ((TecladoNumerico) jPanel1).setPresiono(false);
    }

    private void jentry_txtKeyReleased(java.awt.event.KeyEvent evt) {
        onChanged(evt);
    }

    private void breadcumb_categoriaMouseReleased(java.awt.event.MouseEvent evt) {
        mostrarPaneCategorias();
    }

    private void jDetallesActionPerformed(java.awt.event.ActionEvent evt) {
        boolean presiono = ((TecladoNumerico) jPanel1).isPresiono();
        if (presiono) {
            agregarProducto("TECLADO", jDetalles.getText(), null);
        } else {
            agregarProducto("LECTOR", jDetalles.getText(), null);
        }
        ((TecladoNumerico) jPanel1).setPresiono(false);
    }

    private void jDetallesKeyReleased(java.awt.event.KeyEvent evt) {
        onChanged(evt);
    }

    private void jLabel8MouseReleased(java.awt.event.MouseEvent evt) {
        mostrarPaneTeclado();
        showMensajes(null, null);
    }

    private void jLabel20MouseReleased(java.awt.event.MouseEvent evt) {
        listarProductos(NovusConstante.PRODUCTOS_TIPOS_COMBUSTIBLE, jPanel7);
        StoreViewController.isCombustible = true;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel breadcumb_categoria;
    private javax.swing.JLabel breadcumb_categoria1;
    private javax.swing.JLabel breadcumb_path;
    private javax.swing.JTextField jDetalles;
    private javax.swing.JLabel jImpuesto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jMensajes;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JLabel jSubTotal;
    private javax.swing.JLabel jTotal;
    private javax.swing.JLabel jcanastavacia;
    public javax.swing.JLabel jcanastilla_accent;
    public javax.swing.JLabel jcerrar;
    public javax.swing.JLabel jcombustible_accent;
    public javax.swing.JLabel jentry_lbl;
    public static javax.swing.JTextField jentry_txt;
    private javax.swing.JLabel jfondoMensajes;
    private javax.swing.JLabel jpromotor;
    private javax.swing.JLabel jpromotor1;
    private javax.swing.JLabel jrecibo;
    private javax.swing.JLabel jsubmenu;
    private javax.swing.JPanel panelteclado;
    private javax.swing.JPanel pn_categorias;
    private javax.swing.JPanel pn_despachos;
    private javax.swing.JPanel pn_menu;
    private javax.swing.JPanel pn_teclado;
    private javax.swing.JComboBox<String> pageSizeCombo;
    private javax.swing.JLabel prevButton;
    private javax.swing.JLabel nextButton;
    private javax.swing.JLabel progressButton ;
    private javax.swing.JLabel totalRecordsLabel ;
    int currentPage = 0;
    int pageSize = 20;
    // End of variables declaration//GEN-END:variables
    private void cerrar() {
        if (isCanastaVacia()) {
            salir(true);
        }
    }

    public void resetEntries() {
        jentry_txt.setText("");
        jentry_lbl.setText("Ingrese PLU");
    }

    boolean isMixto(long tipoProducto) {
        boolean isMixto = false;
        if (tipoProducto == NovusConstante.PRODUCTOS_TIPOS_COMBUSTIBLE) {
            for (Map.Entry<Long, MovimientosDetallesBean> entry : movimiento.getDetalles().entrySet()) {
                MovimientosDetallesBean value = entry.getValue();
                if (value.getTipo() != NovusConstante.PRODUCTOS_TIPOS_COMBUSTIBLE) {
                    isMixto = true;
                    break;
                }
            }
        } else {
            for (Map.Entry<Long, MovimientosDetallesBean> entry : movimiento.getDetalles().entrySet()) {
                MovimientosDetallesBean value = entry.getValue();
                if (value.getTipo() == NovusConstante.PRODUCTOS_TIPOS_COMBUSTIBLE) {
                    isMixto = true;
                    break;
                }
            }
        }
        return isMixto;
    }

    MovimientosDetallesBean findByPlu(String codigo) {
        codigo = codigo.trim();
        MovimientosDetallesBean productoConsultado = null;
        for (MovimientosDetallesBean producto : lista) {
            if (producto.getPlu().trim().equals(codigo)) {
                productoConsultado = producto;
                break;
            }
        }
        return productoConsultado;
    }

    MovimientosDetallesBean findByBarCode(String codigo) {
        codigo = codigo.trim();
        MovimientosDetallesBean productoConsultado = null;
        for (MovimientosDetallesBean producto : lista) {
            if (producto.getCodigoBarra().trim().equals(codigo)) {
                productoConsultado = producto;
                break;
            }
        }
        return productoConsultado;
    }

    public void agregarProducto(String origen, String codigo, MovimientosDetallesBean producTemp) {
        jentry_txt.requestFocus();
        jentry_txt.setText("");
        if (!codigo.equals("")) {
            try {
                MovimientosDao dao = new MovimientosDao();
                if (producTemp == null) {
                    if (origen.equals("TECLADO")) {
                        producTemp = dao.findByPlu(codigo);
                    } else {
                        producTemp = dao.findByBarCode(codigo);
                    }
                }
                if (seleccion == null) {
                    if (producTemp != null && producTemp.getEstado().equals(NovusConstante.ACTIVE)) {
                        if ((producTemp.getCantidadIngredientes() == producTemp.getIngredientes().size())
                                && (producTemp.getCantidadImpuestos() == producTemp.getImpuestos().size())) {
                            jentry_txt.setText("");
                            showMensajes(null, null);
                            MovimientosDetallesBean producto = movimiento.getDetalles().get(producTemp.getId());
                            if (producto == null) {
                                if (isMixto(producTemp.getTipo())) {
                                    showMensajes("Operacion no permitida", "notaBorrar.png");
                                } else {
                                    agregaProductoAlista(producTemp != null && producTemp.getTipo() == NovusConstante.PRODUCTO_DESPACHO_COMBUSTIBLE_VENTA_MIXTA ? producTemp.getCantidadUnidad() : 1, producTemp);
                                }
                            } else {
                                if (Main.FACTURACION_NEGATIVO) {
                                    int cantidadIngreado = (int) producto.getCantidadUnidad() + 1;
                                    agregaProductoAlista(cantidadIngreado, producTemp);
                                } else {
                                    if (!producto.isCompuesto()
                                            && producto.getCantidadUnidad() >= producto.getSaldo()) {
                                        showMensajes("Solo puede vender maximo (" + producto.getSaldo() + ")",
                                                "notaBorrar.png");
                                    } else {
                                        agregaProductoAlista(1, producTemp);
                                    }
                                }
                            }
                        } else {
                            if (producTemp.getCantidadIngredientes() != producTemp.getIngredientes().size()) {
                                showMensajes("Descarga incompleta de ingredientes", "notaBorrar.png");

                            } else if (producTemp.getCantidadImpuestos() != producTemp.getImpuestos().size()) {
                                showMensajes("Descarga incompleta de impuestos", "notaBorrar.png");
                            }
                        }
                    } else if (producTemp != null && producTemp.getEstado().equals(NovusConstante.BLOQUEADO)) {
                        showMensajes("Producto bloqueado", "notaBorrar.png");
                    } else {
                        showMensajes("Producto no encontrado", "notaBorrar.png");
                    }
                    repaintPanel();
                } else {
                    // No agrega solo cambia la cantidad de la unidad seleccionada
                    if (!codigo.trim().equals("0")) {
                        try {
                            int cant = Integer.parseInt(codigo);
                            if (cant <= 0) {
                                throw new NumberFormatException("Valor no soportado ");
                            }
                            MovimientosDetallesBean producto = movimiento.getDetalles().get(seleccion.getId());

                            if (producto.isCompuesto() || cant <= producto.getSaldo() || Main.FACTURACION_NEGATIVO) {
                                boolean puedeAgregar = true;

                                if (producto.isCompuesto()) {
                                    if (!Main.FACTURACION_NEGATIVO) {
                                        puedeAgregar = validaExistenciaIngredientes(cant, producto);
                                    }
                                }

                                if (puedeAgregar) {
                                    agregaProductoAlista(cant, producto);
                                    repaintPanel();
                                    jentry_txt.setText("");
                                    showMensajes(null, null);
                                    // jMensajes.setText("");
                                } else {
                                    showMensajes("Uno de los ingredientes sin cantidad solicitada", "notaBorrar.png");
                                }
                            } else {
                                showMensajes("Solo se puede vender un maximo de (" + producto.getSaldo() + ")",
                                        "notaBorrar.png");
                                repaintPanel();
                            }
                        } catch (NumberFormatException e) {
                            seleccion = null;
                            jentry_txt.setText("");
                            showMensajes(null, null);
                            repaintPanel();
                            showMensajes("Operacion no permitida", "notaBorrar.png");
                        }
                    } else {
                        showMensajes("Operacion no permitida", "notaBorrar.png");
                    }
                }

            } catch (Exception ex) {
                Logger.getLogger(InfoViewController.class.getName()).log(Level.SEVERE, null, ex);
                showMensajes("Ocurrio un error inesperado", "notaBorrar.png");
            } catch (DAOException ex) {
                Logger.getLogger(StoreViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            generarVenta();
        }
        seleccion = null;

    }

    public void generarVenta() {
        if (movimiento.getDetalles().isEmpty()) {
            Toolkit.getDefaultToolkit().beep();
            showMensajes("Canasta vacia", "notaBorrar.png");
        } else {
            jentry_txt.setText("");
            jMensajes.setText("");
            showMensajes(null, null);
            movimiento.setGrupoJornadaId(persona.getGrupoJornadaId());
            boolean isNewVenta = true;

            MediosPagosBean efectivoInicial = new MediosPagosBean();
            TreeMap<Long, MediosPagosBean> med = new TreeMap<>();
            efectivoInicial.setId(1);
            efectivoInicial.setValor(movimiento.getVentaTotal());
            efectivoInicial.setRecibido(movimiento.getVentaTotal());
            efectivoInicial.setDescripcion("EFECTIVO");
            efectivoInicial.setCambio(true);
            efectivoInicial.setCambio(0);

            med.put(1L, efectivoInicial);
            movimiento.setMediosPagos(med);

            movimiento.setConsecutivo(this.newConsecutivo);

            if (manual) {
                try {
                    Date fechaTransaccion = sdf.parse(this.fecha);
                    movimiento.setFecha(fechaTransaccion);
                } catch (ParseException ex) {
                    Logger.getLogger(KCOViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                movimiento.setFecha(new Date());
            }

            movimiento.setOperacionId(NovusConstante.MOVIMIENTO_TIPO_COMPLEMENTARIO);
            StoreConfirmarViewController ctd = new StoreConfirmarViewController(this, this.parent, true, movimiento, isNewVenta, false, manual, ventanaVenta);
            ctd.setVisible(true);
        }
    }

    private void onChanged(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() == KeyEvent.VK_BACK_SPACE
                && jentry_txt.getText().trim().equals("")) {
            borrar();
        }
    }

    public boolean isCanastaVacia() {
        boolean canastaVacia = true;
        for (Map.Entry<Long, MovimientosDetallesBean> entry : seleccionado.entrySet()) {
            canastaVacia = false;
            MovimientosDetallesBean value = entry.getValue();
            movimiento.getDetalles().remove(value.getId());
        }
        return canastaVacia;
    }

    private void borrar() {
        NovusUtils.printLn("borrar producto");
        boolean canastaVacia = isCanastaVacia();
        if (!canastaVacia) {
            repaintPanel();
            cantidad--;
            seleccionado.clear();
            seleccion = null;
            showMensajes(null, null);
        }
        resetEntries();
        cerrarTeclado();
    }

    private void repaintPanel() {
        AdjustmentListener ajuste = new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                e.getAdjustable().setValue(Integer.MAX_VALUE);
                jScrollPane1.getVerticalScrollBar().removeAdjustmentListener(this);
            }
        };
        jScrollPane1.getVerticalScrollBar().addAdjustmentListener(ajuste);
        jPanel4.removeAll();
        int cant = 1;
        movimiento.setCostoTotal(0);
        movimiento.setVentaTotal(0);
        movimiento.setImpuestoTotal(0);
        medios = new ArrayList<>();
        if (!movimiento.getDetalles().isEmpty()) {
            jcanastavacia.setVisible(false);
            float impuestoTotal = 0;
            float impoConsumo = 0;
            for (Map.Entry<Long, MovimientosDetallesBean> entry : movimiento.getDetalles().entrySet()) {
                Long key = entry.getKey();
                MovimientosDetallesBean producto = entry.getValue();
                float precioOriginal = producto.getTipo() == NovusConstante.PRODUCTO_DESPACHO_COMBUSTIBLE_VENTA_MIXTA ? producto.getCosto() : producto.getPrecio() * producto.getCantidadUnidad();
                float precioBase;
                float impuestoProducto = 0;
                float impuestoTotalProducto = 0;
                float costoTotal;
                if (producto.getImpuestos() != null) {
                    float impuestoCalculado;

                    for (ImpuestosBean impuesto : producto.getImpuestos()) {
                        if (!impuesto.getPorcentaje_valor().equals(NovusConstante.SIMBOLS_PERCENTAGE)) {
                            impuestoCalculado = impuesto.getValor() * producto.getCantidadUnidad();
                            impuestoProducto += impuestoCalculado;
                            impuesto.setCalculado(impuestoCalculado);
                        }
                    }

                    precioBase = precioOriginal - impuestoProducto;
                    float preciobaseSinImpo = precioOriginal - impuestoProducto;
                    impoConsumo = impuestoProducto;
                    impuestoTotalProducto = impuestoTotalProducto + impuestoProducto;
                    impuestoCalculado = 0;
                    impuestoProducto = 0;
                    for (ImpuestosBean impuesto : producto.getImpuestos()) {
                        if (impuesto.getPorcentaje_valor().equals(NovusConstante.SIMBOLS_PERCENTAGE)) {
                            impuestoCalculado = (float) (preciobaseSinImpo - (preciobaseSinImpo / ((impuesto.getValor() / 100f) + 1f)));
//                            impuestoCalculado = (precioBase - impoConsumo) - ((precioBase - impoConsumo) / (impuesto.getValor() / 100 + 1));
                            impuestoProducto = impuestoProducto + impuestoCalculado;
                            impuesto.setCalculado(impuestoCalculado);
                        }
                    }
                    impuestoTotalProducto = impuestoTotalProducto + impuestoProducto;
                    if (!producto.getImpuestos().isEmpty()) {
                        impuestoTotal = impuestoTotal + impuestoTotalProducto;
                        movimiento.setImpuestoTotal(impuestoTotal);

                        producto.setSubtotal(precioOriginal - impuestoTotalProducto);
                    }
                }
                if (cant > 6) {
                    jPanel4.setPreferredSize(new java.awt.Dimension(jPanel4.getWidth(), jPanel4.getHeight() + 40));
                    jPanel4.setBounds(0, 0, jPanel4.getWidth(), jPanel4.getHeight() + 40);
                }
                producto.setItem(cant);
                PedidoItemView jitem = new PedidoItemView(producto, jScrollPane1, seleccionado, this);
                jPanel4.add(jitem);
                jitem.setBounds(0, (cant - 1) * 40, 590, 40);
                producto.setPanelView(jitem);
                producto.getPanelView().updateProductoView(producto);
                // Corregido: usar el costo real para productos simples
                if (producto.getTipo() == NovusConstante.PRODUCTO_DESPACHO_COMBUSTIBLE_VENTA_MIXTA) {
                    producto.setCosto(producto.getCosto());
                } else if (producto.isCompuesto()) {
                    producto.setCosto(producto.getProducto_compuesto_costo() * producto.getCantidadUnidad());
                } else {
                    producto.setCosto(producto.getCosto());
                }
                movimiento.setVentaTotal(precioOriginal + movimiento.getVentaTotal());
                movimiento.setCostoTotal(producto.getCosto() + movimiento.getCostoTotal());
                jSubTotal.setText(NovusConstante.SIMBOLS_PRICE + " "
                        + df.format(movimiento.getVentaTotal() - movimiento.getImpuestoTotal() + impoConsumo));
                jImpuesto.setText(
                        NovusConstante.SIMBOLS_PRICE + " " + df.format(movimiento.getImpuestoTotal() - impoConsumo));
                jTotal.setText(NovusConstante.SIMBOLS_PRICE + " " + df.format(movimiento.getVentaTotal()));
                cant++;
                if (primeraVenta) {
                    new VentaEnCursoUseCase("A", "CAN").execute();
                }
                primeraVenta = false;
            }

        } else {
            System.out.println("cerrando venta");
            primeraVenta = true;
            new VentaEnCursoUseCase("I", "CAN").execute();
            jPanel4.add(jcanastavacia);
            jcanastavacia.setVisible(true);
            movimiento.setVentaTotal(0);
            jImpuesto.setText(NovusConstante.SIMBOLS_PRICE + " 0");
            jSubTotal.setText(NovusConstante.SIMBOLS_PRICE + " 0");
            jTotal.setText(NovusConstante.SIMBOLS_PRICE + " 0");
            jentry_txt.setText("");
        }
        jPanel4.validate();
        jPanel4.repaint();
        jScrollPane1.getVerticalScrollBar().setValue(jScrollPane1.getVerticalScrollBar().getMaximum());
    }

    void loadPromociones() {
        panelPromociones = new CategoriaDetallesListView(null, this);
        jPanel2.add(panelPromociones, 0);
        panelPromociones.setBounds(0, 0, 1024, 600);
        panelPromociones.setVisible(true);
    }

    void restoreView() {
        hiddenAll();
        jentry_txt.requestFocus();
        jentry_txt.setEditable(true);
        panelteclado.setVisible(true);
    }

    void hiddenAll() {
        if (panelteclado != null) {
            panelteclado.setVisible(false);
        }
        if (panelPromociones != null) {
            panelPromociones.setVisible(false);
        }

    }

    public void listarProductos(long tipo, JPanel panel) {

        int i = 1;
        int componentesX = 0;
        int componentesY = 0;
        int ancho = 580;
        int altoComponente = 65;
        int columnas = 1;
        int margenx = 5;
        int margeny = 5;
        if (tipo == NovusConstante.PRODUCTOS_TIPOS_COMBUSTIBLE) {
            jcanastilla_accent.setOpaque(false);
            jcombustible_accent.setOpaque(true);
            jcombustible_accent.setBackground(new Color(255, 219, 0));
            jcanastilla_accent.setBackground(new Color(0, 0, 0));
        } else {
            jcanastilla_accent.setOpaque(true);
            jcombustible_accent.setOpaque(false);
            jcombustible_accent.setBackground(new Color(0, 0, 0));
            jcanastilla_accent.setBackground(new Color(255, 219, 0));
        }
        panel.removeAll();

        for (MovimientosDetallesBean producto : lista) {
            if ((tipo == NovusConstante.PRODUCTOS_TIPOS_COMBUSTIBLE && producto.getTipo() == tipo)
                    || (tipo != NovusConstante.PRODUCTOS_TIPOS_COMBUSTIBLE
                    && producto.getTipo() != NovusConstante.PRODUCTOS_TIPOS_COMBUSTIBLE)) {
                if (componentesX == columnas) {
                    componentesX = 0;
                    componentesY++;
                }
                CategoriaProductoListItem jitem = new CategoriaProductoListItem(producto, this);
                panel.add(jitem);
                jitem.setBounds(componentesX * (ancho + margenx), componentesY * (altoComponente + margeny), ancho,
                        altoComponente);
                componentesX++;
                i++;
            }
        }

        int altoInicial = (lista.size() / columnas) + 1;
        int altoFinal = (altoInicial * (altoComponente + margeny)) + (altoComponente);
        actualizarEtiquetaPaginacion(totalRecords, pageSize, currentPage, totalRecordsLabel);
        panel.setPreferredSize(new Dimension(panel.getWidth(), altoFinal));
        panel.setBounds(0, 0, panel.getWidth(), altoFinal);
        panel.validate();
        panel.repaint();
    }

    public JPanel getContenedor() {
        return jPanel2;
    }

    public void agregarCanastillaAPanel() {
        for (Map.Entry<Long, MovimientosDetallesBean> entry : seleccionado.entrySet()) {
            Long key = entry.getKey();
            MovimientosDetallesBean producTemp = entry.getValue();
            agregaProductoAlista(1, producTemp);
        }
    }

    private void agregaProductoAlista(float cantidadSolicitada, MovimientosDetallesBean producTemp) {
        if (!producTemp.isCompuesto()) {
            if (Main.FACTURACION_NEGATIVO) {
                if (jMensajes.getText().contains("PRECIO")) {
                    if (producTemp.isCombustible()) {
                        producTemp.setSubtotal(0);
                    } else {
                        producTemp.setCantidadUnidad(cantidadSolicitada / producTemp.getPrecio());
                        producTemp.setSubtotal(cantidadSolicitada);

                    }
                } else {
                    producTemp.setCantidadUnidad(cantidadSolicitada);
                    if (producTemp.isCombustible()) {
                        producTemp.setSubtotal(0);
                    } else {
                        producTemp.setSubtotal(producTemp.getPrecio() * producTemp.getCantidadUnidad());
                    }
                }

                cargaProducto(producTemp);
            } else {
                if (producTemp.getCantidadUnidad() > 0) {
                    if (producTemp.getCantidadUnidad() < cantidadSolicitada) {
                        showMensajes("Cantidad solicitada no permitida", "notaBorrar.png");
                    } else {
                        producTemp.setCantidadUnidad(cantidadSolicitada);
                        cargaProducto(producTemp);
                    }
                } else {
                    showMensajes("Sin existencia en bodega", "notaBorrar.png");
                }
            }
        } else {
            boolean existencia = true;
            ProductoBean faltante = null;
            if (!producTemp.getIngredientes().isEmpty()) {
                producTemp.setProducto_compuesto_costo(0);
                for (ProductoBean ingrediente : producTemp.getIngredientes()) {

                    if (ingrediente.isCompuesto()) {
                        existencia = validaExistenciaIngredientes(Integer.parseInt(cantidadSolicitada + ""), ingrediente);
                    } else {
                        if (ingrediente.getCantidadUnidad() < 0
                                || ingrediente.getCantidadUnidad() < (ingrediente.getProducto_compuesto_cantidad()
                                * cantidadSolicitada)) {
                            existencia = false;
                        }
                    }
                    if (!existencia) {
                        faltante = ingrediente;
                        break;
                    } else {
                        producTemp.setProducto_compuesto_costo(
                                producTemp.getProducto_compuesto_costo() + ingrediente.getProducto_compuesto_cantidad()
                                * ingrediente.getProducto_compuesto_costo());
                        // producTemp.setCostos(producTemp.getProducto_compuesto_costo());
                    }
                }
            } else {
                existencia = false;
            }
            if (existencia) {
                producTemp.setCantidadUnidad(cantidadSolicitada);
                cargaProducto(producTemp);
            } else {
                if (faltante != null) {
                    showMensajes("Falta ingrediente: " + faltante.getDescripcion(), "notaBorrar.png");
                } else {
                    showMensajes("Producto sin existencia", "notaBorrar.png");
                }
            }
        }
    }

    void cargaProducto(MovimientosDetallesBean producTemp) {
        MovimientosDetallesBean producto = movimiento.getDetalles().get(producTemp.getId());
        if (producto == null) {
            producTemp.setCantidadUnidad(producTemp.getTipo() == NovusConstante.PRODUCTO_DESPACHO_COMBUSTIBLE_VENTA_MIXTA ? producTemp.getCantidadUnidad() : 1);
        }
        try {
            producTemp = (MovimientosDetallesBean) producTemp.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(StoreViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        movimiento.getDetalles().put(producTemp.getId(), producTemp);
    }

    JsonObject solicitarConsecutivos() {
        JsonObject response = null;
        TreeMap<String, String> header = new TreeMap<>();
        header.put("content-type", "application/json");
        ClientWSAsync client = new ClientWSAsync("CONSEGUIR CONSECUTIVOS", NovusConstante.SECURE_CENTRAL_POINT_CONSECUTIVOS_FACTURAS, NovusConstante.GET, null, true, false, header);
        try {
            response = client.esperaRespuesta();
        } catch (Exception e) {
            Logger.getLogger(StoreViewController.class.getName()).log(Level.SEVERE, null, e);
        }
        return response;
    }

    boolean hayConsecutivosCombustible() {
        boolean hayConsecutivosCombustible = false;
        JsonObject response = solicitarConsecutivos();
        if (response != null) {
            JsonArray data = response.get("data") != null && !response.get("data").isJsonNull() ? response.get("data").getAsJsonArray() : new JsonArray();
            hayConsecutivosCombustible = data.size() > 0;
        }
        return hayConsecutivosCombustible;
    }

    void updateConsecutivoXS() {
        try {
            // ✅ REVERTIDO: Usar DAO tradicional como en v14.3.12 que SÍ funcionaba
            newConsecutivo = mdao.getConsecutivo(NovusConstante.IS_DEFAULT_FE);
            String resolucion = "CAN";
            if (!manual) {
                if (newConsecutivo != null) {
                    jrecibo.setText(Utils.str_pad(newConsecutivo.getConsecutivo_actual() + "", 6, "0", "STR_PAD_LEFT"));
                } else if (hayConsecutivosCombustible()) {  // ✅ FALLBACK API como en v14.3.12
                    // No hace nada - continúa normal como en la versión anterior
                } else {
                    // ❌ SOLO AQUÍ se muestra la pantalla de error (como en v14.3.12)
                    ConsecutivosDialogView dialog = new ConsecutivosDialogView(this, true);
                    dialog.setVisible(true);
                    this.dispose();
                }
            } else {
                newConsecutivo = mdao.getPrefijo(18, resolucion);
                jrecibo.setText(Utils.str_pad(newConsecutivo.getConsecutivo_actual_fe() + "", 6, "0", "STR_PAD_LEFT"));
            }
        } catch (DAOException a) {
            Logger.getLogger(StoreViewController.class.getName()).log(Level.SEVERE, null, a);
        }
    }

    public PersonaBean getPersona() {
        return persona;
    }

    public void setPersona(PersonaBean persona) {
        this.persona = persona;
    }

    private boolean validaExistenciaIngredientes(int cant, ProductoBean producto) {
        boolean existencia = true;
        if (!producto.getIngredientes().isEmpty()) {
            for (ProductoBean ingrediente : producto.getIngredientes()) {
                if (ingrediente.getProducto_compuesto_cantidad() < 0) {
                    existencia = false;
                    break;
                }
                float cantidadAc = ingrediente.getCantidadUnidad();
                float solicitud = cant * ingrediente.getProducto_compuesto_cantidad();
                if (cantidadAc < solicitud) {
                    existencia = false;
                    break;
                }
            }
        }
        return existencia;
    }

    private void getProductsC (){
        try {
            ObtenerProductosCanastillaUseCase useCase = new ObtenerProductosCanastillaUseCase(currentPage, pageSize);
            ResultadoProductosCanastila result = useCase.execute();
            lista = new ArrayList<>(result.getProductos());
            totalRecords = result.getTotalRegistros();
        } catch (RuntimeException ex) {
            Logger.getLogger(StoreViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void showPnMenu(){
        if (StoreViewController.isCombustible) {
            listarProductos(NovusConstante.PRODUCTOS_TIPOS_COMBUSTIBLE, jPanel7);
        } else {
            listarProductos(NovusConstante.PRODUCTOS_TIPOS_NORMAL, jPanel7);
        }
        StoreViewController.isCombustible = true;
        CardLayout crr = (CardLayout) (jPanel3.getLayout());
        crr.show(jPanel3, "pn_menu");
        this.card = "pn_menu";
    }
    private void mostrarPaneMenu() {
        getProductsC();
        showPnMenu();
    }

    private boolean productoEstaEnCanasta(long id) {
        LinkedHashMap<Long, MovimientosDetallesBean> productosCanasta = this.movimiento.getDetalles();
        return productosCanasta.containsKey(id);
    }

    private void listarDespachosCombustible() {
        this.jScrollPane6.setVisible(true);
        this.jPanel9.setVisible(true);
        int i = 1;
        int componentesX = 0;
        int componentesY = 0;
        int ancho = 580;
        int altoComponente = 65;
        int columnas = 1;
        int margenx = 5;
        int margeny = 5;
        this.jPanel9.removeAll();
        for (MovimientosDetallesBean producto : lista) {
            System.out.println(producto.getDescripcion());
            if (componentesX == columnas) {
                componentesX = 0;
                componentesY++;
            }
            CategoriaProductoListItem jitem = new CategoriaProductoListItem(producto, this, true, this.productoEstaEnCanasta(producto.getId()));
            this.jPanel9.add(jitem);
            jitem.setBounds(componentesX * (ancho + margenx), componentesY * (altoComponente + margeny), ancho,
                    altoComponente);
            componentesX++;
            i++;
        }

        int altoInicial = (lista.size() / columnas) + 1;
        int altoFinal = (altoInicial * (altoComponente + margeny)) + (altoComponente);
        if (altoFinal >= jPanel9.getHeight()) {
            jPanel9.setPreferredSize(new java.awt.Dimension(jPanel9.getWidth(), altoFinal));
            jPanel9.setBounds(0, 0, jPanel9.getWidth(), altoFinal);
        }
        jPanel9.validate();
        jPanel9.repaint();
    }

    private void mostrarPaneDespachosCombustible() {
        MovimientosDao dao = new MovimientosDao();
        try {
            lista = dao.getDespachosCombustiblePorPersona(Main.persona.getId(), Main.persona.getGrupoJornadaId());
        } catch (Exception | DAOException ex) {
            Logger.getLogger(StoreViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

        StoreViewController.isCombustible = false;

        listarDespachosCombustible();
        CardLayout crr = (CardLayout) (jPanel3.getLayout());
        crr.show(jPanel3, "pn_despachos");
        this.card = "pn_despachos";
    }

    private void mostrarPaneCategorias() {
        jScrollPane3.setVisible(false);
        jScrollPane2.setVisible(true);

        try {
            useCase = new ObtenerTodasCategoriasConProductosUseCase();
            listaCategorias = new ArrayList<>(useCase.execute());
        } catch (RuntimeException ex) {
            Logger.getLogger(StoreViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        changeBreadCumbs(null);
        AdjustmentListener ajuste = new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                e.getAdjustable().setValue(Integer.MAX_VALUE);
                jScrollPane2.getVerticalScrollBar().removeAdjustmentListener(this);
            }
        };
        jScrollPane2.getVerticalScrollBar().setValue(jScrollPane1.getVerticalScrollBar().getMinimum());

        listarCategorias();
        CardLayout crr = (CardLayout) (jPanel3.getLayout());
        crr.show(jPanel3, "pn_categorias");
        this.card = "pn_categorias";
    }

    void changeBreadCumbs(CategoriaBean categoria) {
        if (categoria == null) {
            breadcumb_categoria.setForeground(new Color(186, 12, 47));
            breadcumb_path.setText("");
        } else {
            breadcumb_categoria.setForeground(new Color(153, 153, 153));
            breadcumb_path.setForeground(new Color(186, 12, 47));
            breadcumb_path.setText("\\" + categoria.getGrupo().toUpperCase());
        }
    }

    void listarCategorias() {
        int j = 0, k = 0, i = 0;
        for (CategoriaBean categoria : listaCategorias) {
            JLabel buttonCategoria = new JLabel();
            JLabel labelCategoria = new JLabel();
            buttonCategoria.setIcon(new javax.swing.ImageIcon(
                    getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png")));
            labelCategoria.setText(categoria.getGrupo().toUpperCase());
            labelCategoria.setForeground(new Color(255, 255, 255));
            labelCategoria.setFont(new java.awt.Font("Terpel Sans ExtraBold", Font.BOLD, 20));
            labelCategoria.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            if ((i + 1) % 2 != 0) {
                buttonCategoria.setBounds(10, (j * 60) + 10, 270, 60);
                labelCategoria.setBounds(10, (j * 60) + 10, 270, 60);
                j++;
            } else {
                buttonCategoria.setBounds(10 + 270 + 10, (k * 60) + 10, 270, 60);
                labelCategoria.setBounds(10 + 270 + 10, (k * 60) + 10, 270, 60);
                k++;
            }
            buttonCategoria.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseReleased(java.awt.event.MouseEvent evt) {
                    mostrarCategoria(categoria);
                }
            });
            jPanel5.add(labelCategoria);
            jPanel5.add(buttonCategoria);
            i++;
        }
    }

    void mostrarCategoria(CategoriaBean categoria) {
        jScrollPane3.getVerticalScrollBar().setValue(jScrollPane1.getVerticalScrollBar().getMinimum());
        jScrollPane3.setVisible(true);
        jScrollPane2.setVisible(false);
        changeBreadCumbs(categoria);
        MovimientosDao dao = new MovimientosDao();
        if (!productosCategoria.containsKey(categoria.getId())) {
            try {
                productosCategoria.put(categoria.getId(), dao.findByCategoria(categoria));
            } catch (DAOException ex) {
                Logger.getLogger(StoreViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        lista = productosCategoria.get(categoria.getId());
        listarProductos(NovusConstante.PRODUCTOS_TIPOS_NORMAL, jPanel6);
    }

    public void cerrarTeclado() {
        showMensajes(null, null);
        switch (this.card) {
            case "pn_menu":
                mostrarPaneMenu();
                break;
            case "pn_categorias":
                mostrarPaneCategorias();
                break;
            case "pn_despachos":
                mostrarPaneDespachosCombustible();
                break;
            default:
                break;
        }

        jentry_txt.setText("");
        seleccion = null;
    }

    public void changeEntry(String label, String value) {
        jentry_txt.setText(value);
        jentry_lbl.setText(label);
    }

    public void showMensajes(String texto, String rutaImagenMensaje) {

        jMensajes.setVisible(true);
        jfondoMensajes.setVisible(true);

        System.out.println(texto + "     " + rutaImagenMensaje);
        if (texto == null) {
            jMensajes.setVisible(false);
            jfondoMensajes.setVisible(false);
            jMensajes.setText("");
            jfondoMensajes.setIcon(null);
        } else {
            jMensajes.setText(texto);
            jfondoMensajes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/" + rutaImagenMensaje)));
            setTimeout(10000, () -> {
                jMensajes.setVisible(false);
                jfondoMensajes.setVisible(false);
                jMensajes.setText("");
            });
        }
        jMensajes.updateUI();
    }

    public void mostrarPaneTeclado() {
        CardLayout crr = (CardLayout) (jPanel3.getLayout());
        crr.show(jPanel3, "pn_teclado");
        pn_teclado.remove(jLabel2);
        pn_teclado.add(jLabel2);
        jentry_txt.requestFocus();
    }

    private void salir(boolean canastaVacia) {
        PedidoDialogCancelarView cancelar = new PedidoDialogCancelarView(this, true);
        cancelar.setVisible(canastaVacia);
        boolean resp = cancelar.respuesta;
        if (resp) {
            new VentaEnCursoUseCase("I", "CAN").execute();
            if (parent != null) {
                if (manual) {
                    parent.mostrarSubPanel(new VentaManualMenuPanel(parent, vistaprincipal, true));
                } else {
                    parent.showPanel("home");
                }
            } else {
                dialog.dispose();
            }
        }
    }

    public JPanel getjPanel3() {
        return jPanel3;
    }

    private void cargarPanelCanastilla() {
        listarProductos(NovusConstante.PRODUCTOS_TIPOS_NORMAL, jPanel7);
        StoreViewController.isCombustible = false;
    }

    void abrirReporteCanastilla() {
        NovusUtils.beep();
        VentasHistorialCanastillaView hiscanastilla = new VentasHistorialCanastillaView(parent, true);
        this.dispose();
        hiscanastilla.setVisible(true);
        this.dispose();
    }

    void abrirInventarioProductos() {
        NovusUtils.beep();
        InventarioProductosViewController hiscanastilla = new InventarioProductosViewController(parent, true);
        this.dispose();
        hiscanastilla.setVisible(true);
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
}
