/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.firefuel;

import com.WT2.Containers.Dependency.SingletonMedioPago;
import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.WT2.goPass.domain.entity.beans.GopassParameters;
import com.WT2.goPass.domain.entity.beans.PaymentGopassParams;
import com.application.constants.GopassConstants;
import com.application.useCases.gopass.ImprimirVentaGoPassUseCase;
import com.bean.GopassResponse;
import com.bean.TransaccionGopass;
import com.bean.VentaGo;
import com.WT2.goPass.domain.entity.beans.PlacaGopass;
import com.WT2.goPass.domain.valueObject.GoPassStateDictionary;
import com.WT2.goPass.infrastructure.controllers.ProcesarPagoGoPassController;
import com.WT2.payment.domian.entities.PaymentRequest;
import com.WT2.payment.domian.entities.PaymentResponse;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.controllers.WSException;
import com.dao.Gopass;
import com.firefuel.utils.ShowMessageSingleton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.neo.app.bean.ParametrosBean;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicScrollBarUI;

import com.application.services.GoPassPaymentService;
import teclado.view.common.TecladoExtendido;
import com.application.useCases.gopass.ExisteGopassUseCase;
import com.application.useCases.gopass.GetVentasGoPassUseCase;
import com.application.useCases.gopass.GetTransacionesGoPassUseCase;
import com.application.ports.in.gopass.ConsultarPlacasGoPassPort;
import com.application.ports.in.gopass.ValidarPlacaGoPassPort;
import com.application.ports.in.gopass.ProcesarPagoGoPassPort;
import com.application.ports.in.gopass.ConsultarEstadoPagoGoPassPort;
// Configuración para inyectar dependencias
import com.infrastructure.config.GoPassDependencyConfig;

/**
 *
 * @author Devitech
 */
public class GoPassMenuController extends javax.swing.JPanel {

    private static final Logger logger = Logger.getLogger(GoPassMenuController.class.getName());

    InfoViewController base;
    
    private final ExisteGopassUseCase existeGopassUseCase;
    private final GetVentasGoPassUseCase getVentasGoPassUseCase;
    private final GetTransacionesGoPassUseCase getTransacionesGoPassUseCase;
    private final ConsultarPlacasGoPassPort consultarPlacasPort;
    private final ValidarPlacaGoPassPort validarPlacaPort;
    private final ProcesarPagoGoPassPort procesarPagoPort;
    private final ConsultarEstadoPagoGoPassPort consultarEstadoPagoPort;
    /* Paneles */
//    public static final String MENU_GOPASS = "menu_gopass";
//    public static final String PAGO_GOPASS = "pago_gopass";
//    public static final String ESTADO_PAGO = "estado_pago";
//    public static final String PlACAS_GOPASS = "placas_gopass";
//    public static final String VALIDA_PLACAS = "validad_placa";
//    public static final String INFO_PAGO_GOPASS = "info_pago_gopass";
    private GopassParameters parametrosGopass;
    private int timeoutGopass;
    private int timeoutGopassConsult;
    /* hasta aquí */

 /* enviar pagos gopass */
    Gopass Core = new Gopass();
    ItemMovimientoGopass ItemSelected;
    VentaGo selectedVenta;
    PlacaGopass selectedPlaca;
    ArrayList<VentaGo> ventas;
    private final Icon btn_pago_activado = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"));
    private final Icon btn_pago_inactivo = new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-link-normal.png"));
    int segundos = 0;
    Runnable runnable;

    TransaccionGopass transaccionSelected;
    private GoPassPaymentService goPassPaymentService;
    /* hasta aqui */

    JsonArray placasPrueba = new JsonArray();
    JsonArray fetchPlacas;
    ArrayList<PlacaGopass> placas = new ArrayList<>();
    CompletableFuture task = null;
    /* hasta aqui */
    /* consulta de placas */
    ItemPlacaGopass ItemPlacaSelected;
    /**
     * **********************************
     */
    /* validacion de placas */
    PlacaGopass placaGopass = new PlacaGopass();
    /* hasta aqui */
 /* info pago gopass */
    VentaGo venta;
    GopassResponse res;
    /* hasta aqui */

 /* panel de estado pago */
    ArrayList<TransaccionGopass> pagos = new ArrayList<>();
    /**/

 /*runnable para el boton de reintentar*/
    Runnable reintentar = null;

    JsonArray status_placas = null;
    JsonObject errorPlacas = null;

    /**/
    public GoPassMenuController(InfoViewController base) {
        this.base = base;
        
        this.existeGopassUseCase = new ExisteGopassUseCase();
        this.getVentasGoPassUseCase = new GetVentasGoPassUseCase();
        this.getTransacionesGoPassUseCase = new GetTransacionesGoPassUseCase();
        
        this.consultarPlacasPort = GoPassDependencyConfig.crearConsultarPlacasUseCase();
        this.validarPlacaPort = GoPassDependencyConfig.crearValidarPlacaUseCase();
        this.procesarPagoPort = GoPassDependencyConfig.crearProcesarPagoUseCase();
        this.consultarEstadoPagoPort = GoPassDependencyConfig.crearConsultarEstadoPagoUseCase();
        this.imprimirVentaUseCase = GoPassDependencyConfig.crearImprimirVentaUseCase();
        
        initComponents();
        init();
        
        parametrosGopass = SingletonMedioPago.ConetextDependecy.getRecuperarParametrosGopass().execute(null);
        this.goPassPaymentService = new GoPassPaymentService(parametrosGopass);
    }

    private void init() {
        NovusUtils.ajusteFuente(this.getComponents(), Font.BOLD);
        NovusUtils.ajusteFuente(jLabel3, NovusConstante.EXTRABOLD);
        this.updateUI();
        mostrarPanel(GopassConstants.MENU_GOPASS);
    }

    private void render() {
        jScrollPane1.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        jScrollPane1.getHorizontalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        jScrollPane1.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane1.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane1.setOpaque(false);
        jScrollPane1.getViewport().setOpaque(false);
        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane1.setViewportBorder(BorderFactory.createEmptyBorder());

    }

    private void mostrarPlacas() {
        btnSiguiente.setVisible(false);
        NovusUtils.ajusteFuente(jLabel2, NovusConstante.EXTRABOLD);
        int x = 15, y = 10;
        int componentesX = 0;
        int componentesY = 0;
        int ancho = 1080;
        int altoComponente = 100;
        int columnas = 1;
        int margeny = 5;
        jPanel6.removeAll();
        for (PlacaGopass p : this.placas) {

            if (componentesX == columnas) {
                componentesX = 0;
                componentesY++;
            }

            ItemPlacaGopass item = new ItemPlacaGopass(this, p, false);
            item.setBounds(x, y, ancho,
                    altoComponente);
            componentesX++;
            jPanel6.add(item);
            y += altoComponente;
        }

        int altoInicial = (this.placas.size() / columnas) + 1;
        int altoFinal = (altoInicial * (altoComponente + margeny)) + (altoComponente);
        if (altoFinal >= jPanel6.getHeight()) {
            jPanel6.setPreferredSize(new java.awt.Dimension(jPanel6.getWidth(), altoFinal));
            jPanel6.setBounds(0, 0, jPanel6.getWidth(), altoFinal);
        }
        jPanel6.validate();
        jPanel6.repaint();

        this.getRootPane().setOpaque(false);
    }

    public void SetItemPlacaSelected(ItemPlacaGopass ItemPlacaSelected) {
        this.ItemPlacaSelected = ItemPlacaSelected;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel_principal = new javax.swing.JPanel();
        menu_gopass = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        pago_gopass = new javax.swing.JPanel();
        pagar_gopass = new javax.swing.JLabel();
        observaciones = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        back_button = new javax.swing.JLabel();
        devitech_logo = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        fondo_main = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        estado_pago = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jTitle = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        btn_back = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        devitech_logo1 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        fondo = new javax.swing.JLabel();
        modal_placa = new javax.swing.JPanel();
        jIcono = new javax.swing.JLabel();
        jMensajes = new javax.swing.JLabel();
        btn_cerrar = new javax.swing.JLabel();
        btn_reintentar = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        gopass_placa = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel6 = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        devitech_logo2 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        back_button1 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        btnReintentar = new javax.swing.JLabel();
        btnSiguiente = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jTitle1 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        validar_placa = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel7 = new TecladoExtendido();
        txtPlaca = new javax.swing.JTextField();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jNotificacion = new javax.swing.JLabel();
        btnSiguiente1 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        info_pago_gopass = new javax.swing.JPanel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        producto_name = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        product_price = new javax.swing.JLabel();
        product_cant = new javax.swing.JLabel();
        valor_pago_total = new javax.swing.JLabel();
        detalle_pago = new javax.swing.JLabel();
        btn_pago_gopass = new javax.swing.JLabel();
        detalle_producto_gopass = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        valor_pago_gopass = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        cliente_label1 = new javax.swing.JLabel();
        placa_label1 = new javax.swing.JLabel();
        placa_label = new javax.swing.JLabel();
        cliente_label = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        devitech_logo3 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        back_button2 = new javax.swing.JLabel();
        jfondo = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(0, 0));
        setLayout(null);

        panel_principal.setLayout(new java.awt.CardLayout());

        menu_gopass.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/icono_triangulo_amarillo.png"))); // NOI18N
        menu_gopass.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jLabel3.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("GOPASS");
        jLabel3.setMaximumSize(new java.awt.Dimension(146, 47));
        jLabel3.setMinimumSize(new java.awt.Dimension(146, 47));
        jLabel3.setPreferredSize(new java.awt.Dimension(146, 47));
        menu_gopass.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 17, 270, 50));

        jLabel6.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(186, 12, 47));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("1");
        menu_gopass.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 120, 30, -1));

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_home.png"))); // NOI18N
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel8MouseReleased(evt);
            }
        });
        menu_gopass.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(1190, 10, -1, -1));

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/icono_lg_gopass.png"))); // NOI18N
        jLabel9.setToolTipText("");
        jLabel9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel9.setMaximumSize(new java.awt.Dimension(347, 372));
        jLabel9.setMinimumSize(new java.awt.Dimension(347, 372));
        jLabel9.setPreferredSize(new java.awt.Dimension(347, 372));
        menu_gopass.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 170, -1, -1));

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        menu_gopass.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(1170, 5, 11, -1));

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("ENVIAR PAGO");
        jLabel17.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jLabel17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel17MousePressed(evt);
            }
        });
        menu_gopass.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 130, -1, -1));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel5MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel5MouseReleased(evt);
            }
        });
        menu_gopass.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, -1, -1));

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        menu_gopass.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        jLabel12.setMaximumSize(new java.awt.Dimension(120, 83));
        jLabel12.setMinimumSize(new java.awt.Dimension(120, 83));
        jLabel12.setPreferredSize(new java.awt.Dimension(120, 83));
        menu_gopass.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 710, -1, -1));

        jLabel14.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(186, 12, 47));
        jLabel14.setText("2");
        menu_gopass.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 230, 20, -1));

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Terpel Sans", 1, 24)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("ESTADO PAGO");
        jLabel18.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        menu_gopass.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 240, -1, -1));

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        jLabel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel13MouseReleased(evt);
            }
        });
        menu_gopass.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, -1, -1));

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        menu_gopass.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 710, 20, -1));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_submenu_blanco.png"))); // NOI18N
        jLabel2.setText("jLabel2");
        jLabel2.setPreferredSize(new java.awt.Dimension(1332, 800));
        menu_gopass.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1280, 800));

        panel_principal.add(menu_gopass, "menu_gopass");

        pago_gopass.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pagar_gopass.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        pagar_gopass.setForeground(new java.awt.Color(255, 255, 255));
        pagar_gopass.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pagar_gopass.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-link-normal.png"))); // NOI18N
        pagar_gopass.setText("PAGAR CON GOPASS");
        pagar_gopass.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pagar_gopass.setMaximumSize(new java.awt.Dimension(174, 53));
        pagar_gopass.setMinimumSize(new java.awt.Dimension(174, 53));
        pagar_gopass.setPreferredSize(new java.awt.Dimension(174, 53));
        pagar_gopass.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                pagar_gopassMouseReleased(evt);
            }
        });
        pago_gopass.add(pagar_gopass, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 730, 270, -1));

        observaciones.setBackground(new java.awt.Color(255, 255, 255));
        observaciones.setFont(new java.awt.Font("Terpel Sans", 1, 30)); // NOI18N
        observaciones.setForeground(new java.awt.Color(51, 51, 51));
        observaciones.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        observaciones.setText("<html><center>ESPERE...</center></html>");
        observaciones.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        pago_gopass.add(observaciones, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 1230, 530));

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setMinimumSize(new java.awt.Dimension(25, 26));
        jScrollPane1.setOpaque(false);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(450, 400));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setForeground(new java.awt.Color(255, 255, 255));
        jPanel2.setOpaque(false);
        jPanel2.setLayout(null);
        jScrollPane1.setViewportView(jPanel2);

        pago_gopass.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 1230, 570));

        back_button.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        back_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        back_button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                back_buttonMouseReleased(evt);
            }
        });
        pago_gopass.add(back_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        devitech_logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        pago_gopass.add(devitech_logo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 710, -1, -1));

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pago_gopass.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 710, 10, -1));

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pago_gopass.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(1170, 0, 10, -1));

        jLabel19.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(186, 12, 47));
        jLabel19.setText("1");
        pago_gopass.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 20, -1, -1));

        jLabel21.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(186, 12, 47));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("3");
        pago_gopass.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 10, -1, -1));

        jLabel22.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(186, 12, 47));
        jLabel22.setText("4");
        pago_gopass.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jLabel24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/gopass_logo.png"))); // NOI18N
        pago_gopass.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 710, -1, -1));

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setText("PAGO GOPASS");
        jLabel25.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        pago_gopass.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 20, -1, -1));

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_home.png"))); // NOI18N
        jLabel26.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel26MouseReleased(evt);
            }
        });
        pago_gopass.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(1190, 10, -1, -1));

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pago_gopass.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 3, 10, -1));

        fondo_main.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        fondo_main.setMaximumSize(new java.awt.Dimension(1200, 800));
        fondo_main.setMinimumSize(new java.awt.Dimension(1200, 800));
        pago_gopass.add(fondo_main, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jLabel20.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(186, 12, 47));
        jLabel20.setText("2");
        jLabel20.setMaximumSize(new java.awt.Dimension(16, 45));
        jLabel20.setMinimumSize(new java.awt.Dimension(16, 45));
        jLabel20.setPreferredSize(new java.awt.Dimension(16, 45));
        pago_gopass.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 30, -1, -1));

        panel_principal.add(pago_gopass, "pago_gopass");

        estado_pago.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel23.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel23MouseReleased(evt);
            }
        });
        estado_pago.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        estado_pago.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 4, -1, -1));

        jTitle.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jTitle.setForeground(new java.awt.Color(255, 255, 255));
        jTitle.setText("CONSULTA ESTADO DE PAGO");
        estado_pago.add(jTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 20, -1, -1));

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_home.png"))); // NOI18N
        jLabel29.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel29MouseReleased(evt);
            }
        });
        estado_pago.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(1180, 10, -1, -1));

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        estado_pago.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(1150, 4, 10, -1));

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jPanel3.setOpaque(false);
        jPanel3.setLayout(null);
        jScrollPane2.setViewportView(jPanel3);

        estado_pago.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 1260, 590));

        btn_back.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        btn_back.setForeground(new java.awt.Color(255, 255, 255));
        btn_back.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btn_back.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"))); // NOI18N
        btn_back.setText("VERIFICAR GOPASS");
        btn_back.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn_back.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btn_backMousePressed(evt);
            }
        });
        estado_pago.add(btn_back, new org.netbeans.lib.awtextra.AbsoluteConstraints(990, 730, -1, -1));

        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/gopass_logo.png"))); // NOI18N
        estado_pago.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 710, -1, -1));

        devitech_logo1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        estado_pago.add(devitech_logo1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 710, -1, -1));

        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        estado_pago.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 714, -1, -1));

        fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        estado_pago.add(fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        panel_principal.add(estado_pago, "estado_pago");

        modal_placa.setLayout(null);

        jIcono.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/loader_fac.gif"))); // NOI18N
        modal_placa.add(jIcono);
        jIcono.setBounds(60, 260, 280, 250);

        jMensajes.setBackground(new java.awt.Color(244, 244, 244));
        jMensajes.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jMensajes.setForeground(new java.awt.Color(186, 12, 47));
        jMensajes.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jMensajes.setText("OK");
        modal_placa.add(jMensajes);
        jMensajes.setBounds(370, 280, 830, 220);

        btn_cerrar.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btn_cerrar.setForeground(new java.awt.Color(255, 255, 255));
        btn_cerrar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btn_cerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"))); // NOI18N
        btn_cerrar.setText("CERRAR");
        btn_cerrar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn_cerrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_cerrarMouseClicked(evt);
            }
        });
        modal_placa.add(btn_cerrar);
        btn_cerrar.setBounds(920, 730, 264, 54);

        btn_reintentar.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btn_reintentar.setForeground(new java.awt.Color(255, 255, 255));
        btn_reintentar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btn_reintentar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"))); // NOI18N
        btn_reintentar.setText("REINTENTAR");
        btn_reintentar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn_reintentar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_reintentarMouseClicked(evt);
            }
        });
        modal_placa.add(btn_reintentar);
        btn_reintentar.setBounds(620, 730, 264, 54);

        jLabel33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        modal_placa.add(jLabel33);
        jLabel33.setBounds(0, 0, 1281, 801);

        panel_principal.add(modal_placa, "modal");

        gopass_placa.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setOpaque(false);
        jPanel5.setLayout(null);

        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane3.setOpaque(false);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setLayout(null);
        jScrollPane3.setViewportView(jPanel6);

        jPanel5.add(jScrollPane3);
        jScrollPane3.setBounds(90, 210, 1110, 430);

        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separador_placas_gopass.png"))); // NOI18N
        jLabel35.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel5.add(jLabel35);
        jLabel35.setBounds(0, 610, 1280, 106);

        jLabel36.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(186, 47, 12));
        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setText("P L A C A S");
        jPanel5.add(jLabel36);
        jLabel36.setBounds(410, 140, 440, 40);

        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separador_placas_gopass.png"))); // NOI18N
        jPanel5.add(jLabel37);
        jLabel37.setBounds(0, 150, 1280, 106);
        jPanel5.add(jLabel38);
        jLabel38.setBounds(350, 40, 0, 0);

        jLabel39.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/gopass_logo.png"))); // NOI18N
        jPanel5.add(jLabel39);
        jLabel39.setBounds(145, 710, 250, 85);

        devitech_logo2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        jPanel5.add(devitech_logo2);
        devitech_logo2.setBounds(10, 710, 100, 80);

        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel40.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel5.add(jLabel40);
        jLabel40.setBounds(125, 710, 10, 80);

        back_button1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        back_button1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        back_button1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        back_button1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                back_button1MouseClicked(evt);
            }
        });
        jPanel5.add(back_button1);
        back_button1.setBounds(10, 10, 70, 71);

        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel41.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel5.add(jLabel41);
        jLabel41.setBounds(1130, 710, 10, 80);

        btnReintentar.setFont(new java.awt.Font("Bebas Neue", 0, 24)); // NOI18N
        btnReintentar.setForeground(new java.awt.Color(255, 255, 255));
        btnReintentar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnReintentar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"))); // NOI18N
        btnReintentar.setText("REFRESCAR");
        btnReintentar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnReintentar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnReintentarMousePressed(evt);
            }
        });
        jPanel5.add(btnReintentar);
        btnReintentar.setBounds(560, 725, 264, 54);

        btnSiguiente.setFont(new java.awt.Font("Bebas Neue", 0, 24)); // NOI18N
        btnSiguiente.setForeground(new java.awt.Color(255, 255, 255));
        btnSiguiente.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnSiguiente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"))); // NOI18N
        btnSiguiente.setText("SIGUIENTE");
        btnSiguiente.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSiguiente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSiguienteMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnSiguienteMouseEntered(evt);
            }
        });
        jPanel5.add(btnSiguiente);
        btnSiguiente.setBounds(850, 725, 264, 54);

        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel42.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel5.add(jLabel42);
        jLabel42.setBounds(90, 10, 10, 68);

        jTitle1.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jTitle1.setForeground(new java.awt.Color(255, 255, 255));
        jTitle1.setText("CONSULTA PLACAS");
        jPanel5.add(jTitle1);
        jTitle1.setBounds(120, 15, 720, 50);

        jLabel43.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        jPanel5.add(jLabel43);
        jLabel43.setBounds(0, 0, 1280, 800);

        gopass_placa.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1280, 800));

        panel_principal.add(gopass_placa, "placas_gopass");

        validar_placa.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel4.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 330, 1040, 360));

        txtPlaca.setBackground(new java.awt.Color(255, 182, 0));
        txtPlaca.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        txtPlaca.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPlaca.setBorder(null);
        txtPlaca.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPlacaFocusGained(evt);
            }
        });
        txtPlaca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPlacaKeyTyped(evt);
            }
        });
        jPanel4.add(txtPlaca, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 230, 340, -1));

        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel44.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/input_placa_gopass.png"))); // NOI18N
        jPanel4.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 200, -1, 120));

        jLabel45.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel45.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel45.setText("INGRESE LOS 3 DÍGITOS DE SU PLACA");
        jPanel4.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 150, -1, 40));

        jLabel46.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel46.setForeground(new java.awt.Color(255, 255, 255));
        jLabel46.setText("VALIDACIÓN PLACA");
        jPanel4.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 20, -1, -1));

        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel47.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jLabel47.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel47.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel47MouseClicked(evt);
            }
        });
        jPanel4.add(jLabel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, 80));

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        jPanel4.add(jNotificacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 10, 550, 60));

        btnSiguiente1.setFont(new java.awt.Font("Bebas Neue", 0, 24)); // NOI18N
        btnSiguiente1.setForeground(new java.awt.Color(255, 255, 255));
        btnSiguiente1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnSiguiente1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"))); // NOI18N
        btnSiguiente1.setText("SIGUIENTE");
        btnSiguiente1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSiguiente1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSiguiente1MouseClicked(evt);
            }
        });
        jPanel4.add(btnSiguiente1, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 730, -1, -1));

        jLabel34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        jLabel34.setText("jLabel34");
        jPanel4.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1280, 800));

        validar_placa.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1280, 800));

        panel_principal.add(validar_placa, "validad_placa");

        info_pago_gopass.setLayout(null);

        jLabel48.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel48.setForeground(new java.awt.Color(186, 12, 47));
        jLabel48.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel48.setText("PRODUCTO");
        info_pago_gopass.add(jLabel48);
        jLabel48.setBounds(170, 120, 920, 30);

        jLabel49.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel49.setForeground(new java.awt.Color(186, 12, 47));
        jLabel49.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel49.setText("CANTIDAD");
        info_pago_gopass.add(jLabel49);
        jLabel49.setBounds(910, 200, 190, 30);

        jLabel50.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel50.setForeground(new java.awt.Color(186, 12, 47));
        jLabel50.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel50.setText("PRECIO");
        info_pago_gopass.add(jLabel50);
        jLabel50.setBounds(160, 200, 160, 30);

        jLabel51.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel51.setForeground(new java.awt.Color(255, 182, 0));
        jLabel51.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel51.setText("TOTAL");
        info_pago_gopass.add(jLabel51);
        jLabel51.setBounds(180, 290, 440, 29);

        jLabel52.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel52.setForeground(new java.awt.Color(255, 182, 0));
        jLabel52.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel52.setText("METODO DE PAGO");
        info_pago_gopass.add(jLabel52);
        jLabel52.setBounds(710, 290, 380, 30);

        producto_name.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        producto_name.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        producto_name.setText("GASOLINA 10% OXIGENADA");
        info_pago_gopass.add(producto_name);
        producto_name.setBounds(170, 150, 920, 29);

        jLabel53.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel53.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/gopass_logo.png"))); // NOI18N
        jLabel53.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        info_pago_gopass.add(jLabel53);
        jLabel53.setBounds(780, 320, 250, 85);

        product_price.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        product_price.setText("$100,471.00");
        info_pago_gopass.add(product_price);
        product_price.setBounds(160, 230, 210, 30);

        product_cant.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        product_cant.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        product_cant.setText("10 GALONES");
        info_pago_gopass.add(product_cant);
        product_cant.setBounds(910, 230, 190, 29);

        valor_pago_total.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        valor_pago_total.setForeground(new java.awt.Color(255, 255, 255));
        valor_pago_total.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        valor_pago_total.setText("$100,471.00");
        info_pago_gopass.add(valor_pago_total);
        valor_pago_total.setBounds(180, 350, 440, 44);

        detalle_pago.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/detalle_vehiculo_go_pass.png"))); // NOI18N
        info_pago_gopass.add(detalle_pago);
        detalle_pago.setBounds(130, 280, 1000, 130);

        btn_pago_gopass.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btn_pago_gopass.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/enviar_pago_go_pass.png"))); // NOI18N
        btn_pago_gopass.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btn_pago_gopassMouseReleased(evt);
            }
        });
        info_pago_gopass.add(btn_pago_gopass);
        btn_pago_gopass.setBounds(800, 700, 320, 97);

        detalle_producto_gopass.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        detalle_producto_gopass.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/go_pass_product_detail.png"))); // NOI18N
        info_pago_gopass.add(detalle_producto_gopass);
        detalle_producto_gopass.setBounds(130, 110, 1000, 160);

        jLabel54.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel54.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/go_pass_logo2.png"))); // NOI18N
        info_pago_gopass.add(jLabel54);
        jLabel54.setBounds(220, 450, 360, 116);

        valor_pago_gopass.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        valor_pago_gopass.setForeground(new java.awt.Color(186, 12, 47));
        valor_pago_gopass.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        valor_pago_gopass.setText("$100,471.00");
        info_pago_gopass.add(valor_pago_gopass);
        valor_pago_gopass.setBounds(260, 570, 280, 90);

        jLabel55.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/modal.png"))); // NOI18N
        info_pago_gopass.add(jLabel55);
        jLabel55.setBounds(130, 420, 540, 260);

        cliente_label1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        cliente_label1.setForeground(new java.awt.Color(255, 182, 0));
        cliente_label1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        cliente_label1.setText("CLIENTE");
        info_pago_gopass.add(cliente_label1);
        cliente_label1.setBounds(800, 570, 200, 40);

        placa_label1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        placa_label1.setForeground(new java.awt.Color(255, 182, 0));
        placa_label1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        placa_label1.setText("PLACA");
        info_pago_gopass.add(placa_label1);
        placa_label1.setBounds(800, 440, 200, 40);

        placa_label.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        placa_label.setForeground(new java.awt.Color(255, 255, 255));
        placa_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        placa_label.setText("PLACA");
        info_pago_gopass.add(placa_label);
        placa_label.setBounds(720, 490, 360, 40);

        cliente_label.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        cliente_label.setForeground(new java.awt.Color(255, 255, 255));
        cliente_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        cliente_label.setText("NOMBRE CLIENTE");
        info_pago_gopass.add(cliente_label);
        cliente_label.setBounds(720, 620, 360, 45);

        jLabel56.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel56.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/cardinfo.png"))); // NOI18N
        info_pago_gopass.add(jLabel56);
        jLabel56.setBounds(670, 420, 460, 260);

        jLabel57.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/gopass_logo.png"))); // NOI18N
        info_pago_gopass.add(jLabel57);
        jLabel57.setBounds(145, 710, 250, 85);

        devitech_logo3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        info_pago_gopass.add(devitech_logo3);
        devitech_logo3.setBounds(10, 710, 100, 80);

        jLabel58.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel58.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        info_pago_gopass.add(jLabel58);
        jLabel58.setBounds(125, 710, 10, 80);

        jLabel59.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel59.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        info_pago_gopass.add(jLabel59);
        jLabel59.setBounds(1130, 710, 10, 80);

        jLabel60.setBackground(new java.awt.Color(255, 255, 255));
        jLabel60.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jLabel60.setForeground(new java.awt.Color(255, 255, 255));
        jLabel60.setText("CONFIRMACION DE PAGO");
        jLabel60.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        info_pago_gopass.add(jLabel60);
        jLabel60.setBounds(110, 0, 490, 90);

        jLabel61.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel61.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        info_pago_gopass.add(jLabel61);
        jLabel61.setBounds(90, 10, 10, 68);

        back_button2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        back_button2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        back_button2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                back_button2MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                back_button2MouseReleased(evt);
            }
        });
        info_pago_gopass.add(back_button2);
        back_button2.setBounds(10, 10, 70, 71);

        jfondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndSurtidores.png"))); // NOI18N
        info_pago_gopass.add(jfondo);
        jfondo.setBounds(-1, -1, 1280, 800);

        panel_principal.add(info_pago_gopass, "info_pago_gopass");

        add(panel_principal);
        panel_principal.setBounds(0, 0, 1280, 800);
        panel_principal.getAccessibleContext().setAccessibleName("panel_principal");
    }// </editor-fold>//GEN-END:initComponents

    private void back_button1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_back_button1MouseClicked
        mostrarPanel(GopassConstants.PAGO_GOPASS);
        solicitarVentas();
    }//GEN-LAST:event_back_button1MouseClicked

    private void btnReintentarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnReintentarMousePressed
        AbrirModalPlacas();
    }//GEN-LAST:event_btnReintentarMousePressed

    private void jLabel5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel5MousePressed

    private void jLabel17MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel17MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel17MousePressed

    private void jLabel5MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel5MouseReleased
        mostrarPanelPagoGoPass();
    }// GEN-LAST:event_jLabel5MouseReleased

    private void jLabel8MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel8MouseReleased
        cerrar();
    }// GEN-LAST:event_jLabel8MouseReleased

    private void jLabel13MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel13MouseReleased
        abrirEstadopago();
    }// GEN-LAST:event_jLabel13MouseReleased

    private void pagar_gopassMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_pagar_gopassMouseReleased
        AbrirModalPlacas();
    }// GEN-LAST:event_pagar_gopassMouseReleased

    private void jLabel26MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel26MouseReleased
        cerrar();
    }// GEN-LAST:event_jLabel26MouseReleased

    private void jLabel23MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel23MouseReleased
        mostrarPanel(GopassConstants.MENU_GOPASS);
    }// GEN-LAST:event_jLabel23MouseReleased

    private void jLabel29MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel29MouseReleased
        cerrar();
    }// GEN-LAST:event_jLabel29MouseReleased

    private void btn_backMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_btn_backMousePressed

        transaccionSelected = new TransaccionGopass();
        transaccionSelected.setIdentificadorventaterpel(1);
        transaccionSelected.setIdentificadortransacciongopass(1);
        consultarEstadoPago();
    }// GEN-LAST:event_btn_backMousePressed

    private void btn_cerrarMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_btn_cerrarMouseClicked
        runnable.run();
    }// GEN-LAST:event_btn_cerrarMouseClicked

    private void btn_reintentarMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_btn_reintentarMouseClicked
        reintentar.run();
    }// GEN-LAST:event_btn_reintentarMouseClicked

    private void back_button1MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_back_button1MousePressed
        // TODO add your handling code here:
    }// GEN-LAST:event_back_button1MousePressed

    private void back_button1MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_back_button1MouseReleased
        // TODO add your handling code here:
    }// GEN-LAST:event_back_button1MouseReleased

    private void btnCancelarMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_btnCancelarMouseClicked
        // TODO add your handling code here:
    }// GEN-LAST:event_btnCancelarMouseClicked

    private void btnSiguienteMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_btnSiguienteMouseClicked
        if (ItemPlacaSelected != null) {
            SetPlacaSelected(ItemPlacaSelected.getPlaca());
            placaGopass = ItemPlacaSelected.getPlaca();
            txtPlaca.setText(placaGopass.getPlaca().substring(0, 3));
            mostrarPanel(GopassConstants.VALIDA_PLACAS);
            txtPlaca.requestFocus();
        }
    }// GEN-LAST:event_btnSiguienteMouseClicked

    private void jLabel47MouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel47MouseClicked
        mostrarPanel(GopassConstants.PLACAS_GOPASS);
    }// GEN-LAST:event_jLabel47MouseClicked

    private void btnSiguiente1MouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_btnSiguiente1MouseClicked
        siguiente();
    }// GEN-LAST:event_btnSiguiente1MouseClicked

    private void txtPlacaFocusGained(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txtPlacaFocusGained
        desactivarCaratecresEspeciales();
        NovusUtils.deshabilitarCopiarPegar(txtPlaca);
    }// GEN-LAST:event_txtPlacaFocusGained

    private void txtPlacaKeyTyped(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txtPlacaKeyTyped
        String caracteresAceptados = "[0-9a-zA-Z]";
        NovusUtils.limitarCarateres(evt, txtPlaca, 6, jNotificacion, caracteresAceptados);
    }// GEN-LAST:event_txtPlacaKeyTyped

    private void btnSiguienteMouseEntered(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_btnSiguienteMouseEntered
        // TODO add your handling code here:
    }// GEN-LAST:event_btnSiguienteMouseEntered

    private void btn_pago_gopassMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_btn_pago_gopassMouseReleased
        setStateInfoPago(GopassConstants.LOADING_PAGO);
    }// GEN-LAST:event_btn_pago_gopassMouseReleased

    private void back_button2MousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_back_button2MousePressed
        // TODO add your handling code here:
    }// GEN-LAST:event_back_button2MousePressed

    private void back_button2MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_back_button2MouseReleased
        mostrarPanel(GopassConstants.VALIDA_PLACAS);
    }// GEN-LAST:event_back_button2MouseReleased

    private void back_buttonMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_back_buttonMouseReleased
        mostrarPanel(GopassConstants.MENU_GOPASS);
    }// GEN-LAST:event_back_buttonMouseReleased

    private void mostrarPanelPagoGoPass() {
        NovusUtils.beep();
        if (!InfoViewController.turnosPersonas.isEmpty() && InfoViewController.turnosPersonas.size() >= 1) {
            mostrarPanel(GopassConstants.PAGO_GOPASS);
            render();
            if (!InfoViewController.turnosPersonas.isEmpty()) {
                solicitarVentas();
            } else {
                observaciones.setVisible(true);
                observaciones.setText("<html><center>No hay ventas de combustible disponibles</center></html>");
            }
        } else {
            base.mostrarPanelSinTurnos();
        }
    }

    public void abrirEstadopago() {
        NovusUtils.beep();
        selectedVenta = null;
        estadoEnvioPago();
        getTransaccionesFromDB();
        mostrarPanel(GopassConstants.ESTADO_PAGO);
        if (time != null) {
            time.stop();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel back_button;
    private javax.swing.JLabel back_button1;
    private javax.swing.JLabel back_button2;
    private javax.swing.JLabel btnReintentar;
    public static javax.swing.JLabel btnSiguiente;
    private javax.swing.JLabel btnSiguiente1;
    private javax.swing.JLabel btn_back;
    private javax.swing.JLabel btn_cerrar;
    private javax.swing.JLabel btn_pago_gopass;
    private javax.swing.JLabel btn_reintentar;
    private javax.swing.JLabel cliente_label;
    private javax.swing.JLabel cliente_label1;
    private javax.swing.JLabel detalle_pago;
    private javax.swing.JLabel detalle_producto_gopass;
    private javax.swing.JLabel devitech_logo;
    private javax.swing.JLabel devitech_logo1;
    private javax.swing.JLabel devitech_logo2;
    private javax.swing.JLabel devitech_logo3;
    private javax.swing.JPanel estado_pago;
    private javax.swing.JLabel fondo;
    private javax.swing.JLabel fondo_main;
    private javax.swing.JPanel gopass_placa;
    private javax.swing.JPanel info_pago_gopass;
    private javax.swing.JLabel jIcono;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
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
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jMensajes;
    private javax.swing.JLabel jNotificacion;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel jTitle;
    private javax.swing.JLabel jTitle1;
    private javax.swing.JLabel jfondo;
    private javax.swing.JPanel menu_gopass;
    private javax.swing.JPanel modal_placa;
    private javax.swing.JLabel observaciones;
    private javax.swing.JLabel pagar_gopass;
    public javax.swing.JPanel pago_gopass;
    private javax.swing.JPanel panel_principal;
    private javax.swing.JLabel placa_label;
    private javax.swing.JLabel placa_label1;
    private javax.swing.JLabel product_cant;
    private javax.swing.JLabel product_price;
    private javax.swing.JLabel producto_name;
    private javax.swing.JTextField txtPlaca;
    private javax.swing.JPanel validar_placa;
    private javax.swing.JLabel valor_pago_gopass;
    private javax.swing.JLabel valor_pago_total;
    // End of variables declaration//GEN-END:variables

    // enviar pagos goupas E
    public void AbrirModalPago() {
        NovusUtils.beep();
    }

    public void SetPlacaSelected(PlacaGopass selectedPlaca) {
        this.selectedPlaca = selectedPlaca;
    }


    public void AbrirModalPlacas() {
        int state = GopassConstants.LOADING_PLACAS;
        if (time != null) {
            time.stop();
            time = null;
        }
        NovusUtils.beep();
        if (this.selectedVenta != null) {
            if (!verificarGoPass()) {
                state = 4;
            }
            setState(state);
        }
    }

    public void CargarVentas() {
        int i = 1;
        int componentesX = 0;
        int componentesY = 0;
        int ancho = 1188;
        int altoComponente = 195;
        int columnas = 1;
        int margenx = 5;
        int margeny = 5;
        jPanel2.removeAll();

        if (!ventas.isEmpty()) {
            observaciones.setVisible(false);
            for (VentaGo v : ventas) {
                if (componentesX == columnas) {
                    componentesX = 0;
                    componentesY++;
                }
                ItemMovimientoGopass item = new ItemMovimientoGopass(this, false, v);
                jPanel2.add(item);
                item.setBounds(componentesX * (ancho + margenx), componentesY * (altoComponente + margeny), ancho,
                        altoComponente);
                componentesX++;
                i++;

                int altoInicial = (ventas.size() / columnas) + 1;
                int altoFinal = (altoInicial * (altoComponente + margeny)) + (altoComponente);
                if (altoFinal >= jPanel2.getHeight()) {
                    jPanel2.setPreferredSize(new java.awt.Dimension(jPanel2.getWidth(), altoFinal));
                    jPanel2.setBounds(0, 0, jPanel2.getWidth(), altoFinal);
                }
            }
            jPanel2.validate();
            jPanel2.repaint();
        } else {
            observaciones.setVisible(true);
            observaciones.setText("<html><center>No hay ventas de combustible disponibles</center></html>");
        }

    }

    public void setVentas(ArrayList<VentaGo> ventas) {
        this.ventas = ventas;
    }

    public void estadoEnvioPago() {
        if (selectedVenta != null) {
            pagar_gopass.setIcon(btn_pago_activado);
        } else {
            pagar_gopass.setIcon(btn_pago_inactivo);
        }
    }

    private void enviarPagoGoPassV2() {
        try {
            ProcesarPagoGoPassPort.ProcesarPagoCommand command = 
                new ProcesarPagoGoPassPort.ProcesarPagoCommand(selectedVenta, selectedPlaca);
            ProcesarPagoGoPassPort.ProcesarPagoResult resultado = procesarPagoPort.execute(command);
            
            // Convertir resultado a GopassResponse para compatibilidad
            res = new GopassResponse(false);
            res.setMensaje(resultado.getMensaje());
            res.setEstadoPago(resultado.getEstadoPago());
            res.setError(!resultado.isExitoso());
            
            final boolean isError = !resultado.isExitoso();
            final String idventa = String.valueOf(selectedVenta.getId());
            
            javax.swing.SwingUtilities.invokeLater(() -> {
                try {
                    RenderState(isError ? GopassConstants.ERR_PAGO : GopassConstants.SUCCESS_PAGO);
                    
                    if (!isError) {
                        CompletableFuture.runAsync(() -> {
                            try {
                                ImprimirVenta(Long.parseLong(idventa), "factura");
                            } catch (Exception e) {
                                logger.log(Level.WARNING, "Error en impresión (no crítico)", e);
                            }
                        });
                        
                        // Refrescar ventas
                        solicitarVentas();
                    }
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error actualizando UI después del pago", e);
                }
            });
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error procesando pago GoPass", e);
            
            res = new GopassResponse(false);
            res.setMensaje("Error procesando pago: " + e.getMessage());
            res.setError(true);
            
            javax.swing.SwingUtilities.invokeLater(() -> {
                RenderState(GopassConstants.ERR_PAGO);
            });
        }
    }
    // ENVIAR PAGO GO PASS
    private GopassResponse enviarPagoGoPass() {
        res = new GopassResponse(false);

        // JsonObject data_pago = new JsonObject();
        String idventa = "" + selectedVenta.getId();

        NovusUtils.printLn("CONSULTANDO A API PAGOS GOPASS");
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setIdentificadorMovimiento(Long.valueOf(idventa));
        paymentRequest.setMedioDescription("GOPASS");

        PaymentGopassParams paymentGopassParams = new PaymentGopassParams();
        paymentGopassParams.setIdentificadorVentaTerpel(paymentRequest);
        paymentGopassParams.setPlaca(this.selectedPlaca.getPlaca());

        timeoutGopassConsult = ((parametrosGopass.getConfiguracionTokenCantidadReintentos() * ( parametrosGopass.getConfiguracionTokenTiempoMuerto() + parametrosGopass.getConfiguracionPagoTiempoReintentos()) )  ) + (parametrosGopass.getConfiguracionPagoCantidadReintentos() * ( parametrosGopass.getConfiguracionPagoTiempoReintentos() + parametrosGopass.getConfiguracionConsultaPlacaTiempoReintentos() ) );
        timeoutGopassConsult = (timeoutGopassConsult + 5) * 1000;
        System.out.println("timeoutGopassConsult: "+ timeoutGopassConsult);
        paymentGopassParams.setTimeOut((long) timeoutGopassConsult);

        ProcesarPagoGoPassController procesarPagoGoPassController = new ProcesarPagoGoPassController();
        try {
            PaymentResponse response = procesarPagoGoPassController.execute(paymentGopassParams);
            String mensaje = "Pago no pudo ser procesado, verifique conexión a internet";
            String estado = response.getEstadoPago();
            res.setError(true);
            switch (estado) {
                case GoPassStateDictionary.APROBADO:
                    mensaje = "Pago aprobado";
                    //Integer IDpago = Integer.valueOf(response.getIdTransaccion());
                    NovusUtils.printLn("########## ACTUALIZANDO ATRIBUTOS DE PAGO #######");
                    res.setEstadoPago(estado);
                    res.setMensaje(mensaje);
                    // res.setIDgopass(IDpago);
                    res.setError(false);
                    
                    solicitarVentas();
                    break;
                case "P":
                    mensaje = "Pago pendiente";
                    res.setError(false);
                    break;
                case GoPassStateDictionary.RECHAZADO:
                    mensaje = "Pago rechazado " + response.getMensaje().toLowerCase();
                    break;

                case "X":
                    mensaje = NovusUtils.convertMessage(
                            LetterCase.FIRST_UPPER_CASE,
                            response.getMensaje()
                    );
                    break;
                default:
                    mensaje = "Pago no procesado";
                    res.setError(false);
                    break;
            }
            res.setMensaje(mensaje);
            RenderState(res.isError() ?  GopassConstants.ERR_PAGO : GopassConstants.SUCCESS_PAGO);

        } catch (NumberFormatException | ExecutionException | InterruptedException e) {
            NovusUtils.printLn(e.getMessage());
        }
        return res;
    }
    // SOLICITAR VENTAS

    public void solicitarVentas() {
        selectedVenta = null;
        estadoEnvioPago();
        ArrayList<VentaGo> v = new ArrayList<>();
        try {
            v = getVentasGoPassUseCase.execute();
        } catch (Exception ex) {
            Logger.getLogger(GoPassMenuController.class.getName()).log(Level.SEVERE, null, ex);
        }
        setVentas(v);
        CargarVentas();

    }

    private boolean verificarGoPass() {
        boolean exist = false;
        try {
            if (existeGopassUseCase.execute()) {
                exist = true;
            }
        } catch (Exception e) {
            NovusUtils.printLn("Error al verificar GoPass: " + e.getMessage());
        }
        return exist;
    }

    private final ImprimirVentaGoPassUseCase imprimirVentaUseCase;
    
    public void ImprimirVenta(long id, String route) {
        try {
            NovusUtils.printLn("Iniciando impresión venta " + id + " (background silencioso)");
        
        ImprimirVentaGoPassUseCase.ImprimirVentaResult resultado = imprimirVentaUseCase.execute(id, route);
        
        if (resultado.isExito()) {
                NovusUtils.printLn("Venta " + id + " impresa exitosamente");
                logger.info("Venta " + id + " impresa correctamente");
                
        } else {
                NovusUtils.printLn("Impresión falló para venta " + id + ": " + resultado.getMensaje());
                NovusUtils.printLn("La venta se procesó correctamente - Solo falló la impresión");
                logger.warning("Impresión no exitosa para venta " + id + ": " + resultado.getMensaje());
            }
            
        } catch (Exception e) {
            NovusUtils.printLn("Excepción en impresión venta " + id + ": " + e.getMessage());
            NovusUtils.printLn("La venta se procesó correctamente - Solo falló la impresión");
            logger.log(Level.WARNING, "Excepción en impresión venta " + id + " (no crítico)", e);
        }
    }


    /**
     * **************+
     */
    public void Async(Runnable runnable) {
        CompletableFuture.runAsync(() -> {
        try {
                runnable.run();
        } catch (Exception e) {
                Logger.getLogger(GoPassMenuController.class.getName())
                    .log(Level.SEVERE, "Error en tarea asíncrona", e);
                NovusUtils.printLn("Error en tarea asíncrona: " + e.getMessage());
            }
        }).exceptionally(throwable -> {
            Logger.getLogger(GoPassMenuController.class.getName())
                .log(Level.SEVERE, "Error crítico en CompletableFuture", throwable);
            NovusUtils.printLn("Error crítico en tarea asíncrona: " + throwable.getMessage());
            return null;
        });
    }

    public void mostrarPanel(String panel) {
        observaciones.setText("<html><center>Espere...</center></html>");
        if (panel.length() > 0) {
            CardLayout card = (CardLayout) panel_principal.getLayout();
            card.show(panel_principal, panel);
        }
    }

    /* modal de las placas */
    private void RenderPlacas(int state) {
        switch (state) {
            case GopassConstants.LOADING_PLACAS:
                NovusUtils.printLn("Iniciando Consulta Placas Modulo");
                mostrarMensaje(GopassConstants.MSG_CONSULTANDO_PLACAS, GopassConstants.LOADER_FAC, "");
                btn_cerrar.setVisible(false);
                btn_reintentar.setVisible(false);
                break;
            case GopassConstants.SUCCESS_PLACA:
                placas.clear();
                //AQUI DIBUJA LAS PLACAS
                NovusUtils.printLn("PLACAS OBTENIDAS: " + fetchPlacas);
                for (JsonElement element : fetchPlacas) {
                    JsonObject json = element.getAsJsonObject();
                    PlacaGopass p = new PlacaGopass();
                    try {
                        NovusUtils.printLn(Main.ANSI_RED + json + Main.ANSI_RESET);
                        p.setPlaca(json.get("placa").getAsString());
                        p.setTagGopass(json.get("tagGopass").getAsString());
                        p.setNombreUsuario(json.get("nombreUsuario").getAsString());
                        p.setIsla(json.get("isla").getAsString());
                        p.setFechahora(json.get("fechahora").getAsString());
                    } catch (Exception e) {
                        NovusUtils.printLn(e.getMessage());
                    }
                    placas.add(p);
                }
                mostrarPanel(GopassConstants.PLACAS_GOPASS);
                mostrarPlacas();
                break;
            case GopassConstants.ERROR_CODE:
                mostrarMensaje(GopassConstants.MSG_ERROR_CONEXION_INTERNET, GopassConstants.BTN_ERROR, GopassConstants.PAGO_GOPASS);
                reintentar = () -> {
                    AbrirModalPlacas();
                    reintentar = null;
                };
                btn_reintentar.setVisible(true);
                btn_cerrar.setVisible(true);
                break;
            case GopassConstants.GOPASS_INACTIVE:
                mostrarMensaje(GopassConstants.MSG_ERROR_GOPASS_NO_ACTIVADO, GopassConstants.BTN_ERROR, GopassConstants.PAGO_GOPASS);
                observaciones.setVisible(true);
                jLabel2.setVisible(true);
                break;
        }
    }

    public void setState(int state) {
        System.out.println("State: " + state);
        RenderPlacas(state);
        switch (state) {
            case GopassConstants.LOADING_PLACAS:
                this.ThreadPlacas();
                break;
            case GopassConstants.GOPASS_INACTIVE:
                RenderPlacas(4);
                break;
            default:
                break;
        }
    }

    public void ThreadPlacas() {
        this.task = CompletableFuture.supplyAsync(() -> {
            setFetchPlacasArray(new JsonArray());
            consultarPlacas();
            return null;
        }).thenAccept(x -> {
            procesarRespuesta();
        });
    }

    private JsonArray consultarPlacas() {
        errorPlacas = null;
        status_placas = null;

        NovusUtils.printLn("Consultando Placas Modulo....");
        mostrarMensaje(GopassConstants.MSG_BUSCANDO_PLACAS,
                GopassConstants.LOADER_FAC, "");
        
        // Validar venta seleccionada
        if (selectedVenta == null) {
            NovusUtils.printLn("VENTA NO SELECCIONADA");
            mostrarMensaje(GopassConstants.MSG_ERROR_FALLO_RED,
                    GopassConstants.LOADER_FAC, "");
            try {
                Thread.sleep(3000); 
            } catch (InterruptedException e) {
                e.printStackTrace(); 
            }
            return status_placas;
        }
        
        try {
            // Llamar directamente al caso de uso hexagonal (sin intermediario)
            ConsultarPlacasGoPassPort.ConsultarPlacasResult resultado = 
                consultarPlacasPort.execute((long) selectedVenta.getId());
            
            if (resultado.isExitoso()) {
                // Convertir List<PlacaGopass> a JsonArray
                JsonArray placasArray = new JsonArray();
                for (PlacaGopass placa : resultado.getPlacas()) {
                    JsonObject placaJson = new JsonObject();
                    placaJson.addProperty("placa", placa.getPlaca());
                    placaJson.addProperty("tagGopass", placa.getTagGopass());
                    placaJson.addProperty("nombreUsuario", placa.getNombreUsuario());
                    placaJson.addProperty("isla", placa.getIsla());
                    placaJson.addProperty("fechahora", placa.getFechahora());
                    placasArray.add(placaJson);
                }
                status_placas = placasArray;
            } else {
                // Manejar error
                JsonObject error = new JsonObject();
                error.addProperty("mensajeError", resultado.getMensaje());
                errorPlacas = error;
                status_placas = null;
                System.out.println("com.firefuel.GoPassMenuController.consultarPlacas() - Error: " + resultado.getMensaje());
            }
        } catch (Exception e) {
            NovusUtils.printLn("consultarPlacas: " + e.getMessage());
            JsonObject error = new JsonObject();
            error.addProperty("mensajeError", "Error inesperado consultando placas");
            errorPlacas = error;
            status_placas = null;
        }
        
        if (status_placas == null) {
            mostrarMensaje("<html><center>Error al obtener placas reintentando, espere un momento</center></html>",
                    GopassConstants.LOADER_FAC, "");
        }
        
        return status_placas;
    }

    private void procesarRespuesta() {
        if (status_placas == null) {
            runnable = () -> {
                mostrarPanel(GopassConstants.MENU_GOPASS);
            };
            String mensaje = "Error al obtener placas, intente nuevamente";

            if (errorPlacas != null && errorPlacas.has("mensajeError") && errorPlacas.get("mensajeError") != null) {
                mensaje = errorPlacas.get("mensajeError").getAsString();
            }

            showMessage("<html><center>" + mensaje + "</center></html>",
                    GopassConstants.BTN_ERROR, true, runnable,
                    true, LetterCase.FIRST_UPPER_CASE);
            setFetchPlacasArray(status_placas);
        } else {
            setFetchPlacasArray(status_placas);
            setState(status_placas != null ? GopassConstants.SUCCESS_PLACA : GopassConstants.ERROR_CODE);
        };
        errorPlacas = null;
        status_placas = null;
    }

    public void setFetchPlacasArray(JsonArray placas) {
        this.fetchPlacas = placas;
    }

    private void siguiente() {
        String digitosIngresados = txtPlaca.getText().trim();
        ValidarPlacaGoPassPort.ValidarPlacaCommand command = 
            new ValidarPlacaGoPassPort.ValidarPlacaCommand(placaGopass, digitosIngresados);
        ValidarPlacaGoPassPort.ValidarPlacaResult resultado = validarPlacaPort.execute(command);
        
        if (!resultado.isValida()) {
            showMessage("<html><center>" + resultado.getMensaje().toUpperCase() + "</center></html>",
                    "/com/firefuel/resources/btBad.png", true, () -> {
                        mostrarPanel(GopassConstants.VALIDA_PLACAS);
                    }, true, LetterCase.FIRST_UPPER_CASE);
            return;
        }
        
        // Placa válida, continuar con el flujo
        mostrarPanel(GopassConstants.INFO_PAGO_GOPASS);
        venta = selectedVenta;
        cargarDatosPagos();
    }

    /* hasta aquí */

 /* info pago gopass */
    private void cargarDatosPagos() {
        if (venta != null) {
            String nombre_producto = venta.getDescription();
            String total_pago = "" + venta.getVentaTotal();
            String cantidad = "" + venta.getCantidad() + " GL";
            producto_name.setText(nombre_producto);
            product_price.setText(venta.getPrecio_producto());
            product_cant.setText(cantidad);
            valor_pago_total.setText(total_pago);
            valor_pago_gopass.setText(total_pago);
            placa_label.setText(selectedPlaca.getPlaca());
            cliente_label.setText(selectedPlaca.getNombreUsuario());
        } else {
            NovusUtils.printLn("VENTA O PLACA NULA ############");
        }
    }

    public void RenderState(int state) {

        switch (state) {
            case GopassConstants.LOADING_PAGO:
                mostrarMensaje(GopassConstants.MSG_PROCESANDO_PAGO,
                        GopassConstants.LOADER_FAC, "");
                btn_cerrar.setVisible(false);
                btn_reintentar.setVisible(false);
                break;
            case GopassConstants.SUCCESS_PAGO:
                String respuesta = "";
                if (res.getMensaje() != null) {
                    respuesta = res.getMensaje();
                }
                if (time != null) {
                    time.stop();
                    time = null;
                }
                showMessage(GopassConstants.MSG_PAGO_EXITOSO,
                        GopassConstants.BTN_OK, true, this::abrirEstadopago,
                        true, LetterCase.FIRST_UPPER_CASE);
                break;
            case  GopassConstants.ERR_PAGO:
                respuesta = "";
                if (res.getMensaje() != null) {
                    respuesta = res.getMensaje();
                }
                btn_cerrar.setVisible(true);
                runnable = () -> {
                    mostrarPanel(GopassConstants.MENU_GOPASS);
                };
                if (time != null) {
                    time.stop();
                    time = null;
                }
                showMessage(GopassConstants.MSG_ERROR_PAGO.concat(respuesta) + "</center></html>",
                        GopassConstants.BTN_ERROR, true, runnable,
                        true, LetterCase.FIRST_UPPER_CASE);
                jLabel2.setVisible(true);
                jLabel3.setVisible(true);
                break;
        }
    }

    public void setStateInfoPago(int state) {
        RenderState(state);
        if (state == GopassConstants.LOADING_PAGO) {
            this.ThreadPago();
        } else {
        }
    }

    public void ThreadPago() {
        task = CompletableFuture.runAsync(this::enviarPagoGoPassV2).exceptionally(throwable -> {
            logger.log(Level.SEVERE, "Error en proceso de pago", throwable);
            NovusUtils.printLn("Error procesando pago: " + throwable.getMessage());
            return null;
        });
    }

    /**/

//  /* Panel de estado pago */
    // public void getTransaccionesFromDB() {
    //     Async(() -> {
    //         try {
    //             ArrayList<TransaccionGopass> transacciones = Core.getTransaccionesGoPass();
    //             setPagos(transacciones);
    //             MostrarPagos();
    //         } catch (DAOException ex) {
    //             Logger.getLogger(GoPassMenuController.class.getName()).log(Level.SEVERE, null, ex);
    //         }
    //     });
    // }

/* Panel de estado pago */
    public void getTransaccionesFromDB() {
        Async(() -> {
            try {
                ArrayList<TransaccionGopass> transacciones = getTransacionesGoPassUseCase.execute();
                setPagos(transacciones);
                MostrarPagos();
            } catch (Exception ex) {
                NovusUtils.printLn("Error al obtener transacciones de GoPass: " + ex.getMessage());
            }
        });
    }

    public void setPagos(ArrayList<TransaccionGopass> pagos) {
        this.pagos = pagos;
    }

    public void MostrarPagos() {
        int i = 1;
        int componentesX = 0;
        int componentesY = 0;
        int ancho = 1250;
        int altoComponente = 93;
        int columnas = 1;
        int margenx = 5;
        int margeny = 5;
        jPanel3.removeAll();

        for (TransaccionGopass p : this.pagos) {

            if (componentesX == columnas) {
                componentesX = 0;
                componentesY++;
            }

            ItemConsultaPago item = new ItemConsultaPago(this, p);
            jPanel3.add(item);
            item.setBounds(componentesX * (ancho + margenx), componentesY * (altoComponente + margeny), ancho,
                    altoComponente);
            componentesX++;
            i++;
        }

        int altoInicial = (this.pagos.size() / columnas) + 1;
        int altoFinal = (altoInicial * (altoComponente + margeny)) + (altoComponente);
        if (altoFinal >= jPanel3.getHeight()) {
            jPanel3.setPreferredSize(new java.awt.Dimension(jPanel3.getWidth(), altoFinal));
            jPanel3.setBounds(0, 0, jPanel3.getWidth(), altoFinal);
        }
        jPanel3.validate();
        jPanel3.repaint();

    }

    public void reImprimirPago(long id, String route) {
        // Create JSON request
        String funcion = "IMPRIMIR VENTAS";
        JsonObject json = new JsonObject();
        json.addProperty("identificadorMovimiento", id);
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

        // Log the JSON request
        NovusUtils.printLn("Use Case - JSON Request: " + json.toString());

        // Existing code to send the request
        String url = NovusConstante.SECURE_CENTRAL_POINT_IMPRESION_VENTA + "/" + route;
        String metho = NovusConstante.POST;
        boolean isArray = false;
        boolean isDebug = true;
        TreeMap<String, String> header = new TreeMap<>();
        header.put("Content-type", "application/json");
        ClientWSAsync client = new ClientWSAsync(funcion, url, metho, json, isDebug, isArray, header);
        String mensaje, rutaImg = "/com/firefuel/resources/loader_fac.gif";
        notificacion("Imprimiendo...", rutaImg, GopassConstants.ESTADO_PAGO);

        try {
            client.start();
            NovusUtils.printLn("****** VENTA IMPRESA ******");
            mensaje = "VENTA IMPRESA CORRECTAMENTE";
            rutaImg = "/com/firefuel/resources/btOk.png";
        } catch (Exception e) {
            NovusUtils.printLn("****** ERROR A IMPRIMIR VENTA ******");
            mensaje = "ERROR AL IMPRIMIR VENTA";
            rutaImg = "/com/firefuel/resources/btBad.png";
            NovusUtils.printLn(e.getMessage());
        }
        notificacion(mensaje, rutaImg, GopassConstants.ESTADO_PAGO);
    }

    public void consultarEstadoPago(TransaccionGopass transaccion) {
        transaccionSelected = transaccion;
        consultarEstadoPago();
    }

    /**
     * ✅ ARQUITECTURA HEXAGONAL: Usa el puerto ConsultarEstadoPagoGoPassPort
     * Consulta el estado de un pago GoPass
     */
    private void consultarEstadoPago() {
        mostrarMensaje("Consultando estado GoPass....", "/com/firefuel/resources/loader_fac.gif", "");
        btn_cerrar.setVisible(false);
        btn_reintentar.setVisible(false);

        try {
            setTimeout(1, () -> {
                ConsultarEstadoPagoGoPassPort.ConsultarEstadoResult resultado = 
                    consultarEstadoPagoPort.execute(transaccionSelected);

                if (resultado.isExitoso()) {
                    abrirEstadopago();
                    String msj = "<html>ESTADO GOPASS :<br/>" + resultado.getMensaje().toUpperCase() + "</html>";
            String icon = "/com/firefuel/resources/btOk.png";

            mostrarMensaje(NovusUtils.convertMessage(
                    LetterCase.FIRST_UPPER_CASE,
                    msj), icon, 8, () -> {
                        mostrarPanel(GopassConstants.ESTADO_PAGO);
                    });
        } else {
            reintentar = () -> {
                consultarEstadoPago();
                reintentar = null;
            };
                    
                    String mensaje = resultado.getMensaje();
                    jMensajes.setFont(new java.awt.Font("Terpel Sans", 1, mensaje.length() > 30 ? 20 : 36));
                    
            mostrarMensaje(NovusUtils.convertMessage(
                    LetterCase.FIRST_UPPER_CASE,
                    mensaje), "/com/firefuel/resources/btBad.png", 6, () -> {
                        mostrarPanel(GopassConstants.ESTADO_PAGO);
                    });
            btn_reintentar.setVisible(true);
        }
            });

        } catch (Exception e) {
            Logger.getLogger(GoPassMenuController.class.getName()).log(Level.SEVERE, null, e);
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

    private void mostrarSubPanel(JPanel panel) {
        CardLayout layout = (CardLayout) panel_principal.getLayout();
        panel_principal.add("pnl_ext", panel);
        layout.show(panel_principal, "pnl_ext");
    }

    private void mostrarMensaje(String mensaje, String imagen, int tiempo, Runnable run) {
        jMensajes.setText(mensaje);
        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource(imagen)));
        mostrarPanel("modal");
        setTimeout(tiempo, run);
    }

    private void mostrarMensaje(String mensaje, String imagen, String panel) {
        jMensajes.setFont(new java.awt.Font("Terpel Sans", 1, 36));
        jMensajes.setText(mensaje);
        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource(imagen)));
        mostrarPanel("modal");
        mostrarPanel(panel);
    }

    private void notificacion(String mensaje, String icono, String panel) {
        jMensajes.setFont(new java.awt.Font("Terpel Sans", 1, 36));
        jMensajes.setText(NovusUtils.convertMessage(
                LetterCase.FIRST_UPPER_CASE,
                mensaje));
        jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource(icono)));
        btn_reintentar.setVisible(false);
        btn_cerrar.setVisible(true);
        mostrarPanel("modal");
        runnable = () -> {
            mostrarPanel(panel);
        };
        setTimeout(5, runnable);
    }

    private void desactivarCaratecresEspeciales() {
        TecladoExtendido teclado = (TecladoExtendido) jPanel7;
        teclado.deshabilitarCaracteresEspeciales(false);
    }

    void cerrar() {
        setVisible(false);
        segundos = 0;
        if (time != null) {
            time.stop();
        }
        base.mostrarOcultarMenu();
        runnable = null;
    }

    Timer time;

    private void setTimeout(int delay, Runnable runnable) {

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
