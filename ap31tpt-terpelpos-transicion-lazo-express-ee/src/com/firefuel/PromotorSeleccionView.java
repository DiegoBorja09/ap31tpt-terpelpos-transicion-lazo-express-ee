/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.firefuel;

import com.application.useCases.persons.IsAdminUseCase;
import com.bean.PersonaBean;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.PersonasDao;
import java.awt.Color;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 *
 * @author USUARIO
 */
public class PromotorSeleccionView extends javax.swing.JDialog {

    ArrayList<PersonaBean> personas = new ArrayList<>();
    SimpleDateFormat sdf2 = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
    public static boolean promotorSeleccionado = false;
    public boolean tipoVentaPos = false;
    public static PromotorSeleccionView instance = null;
    public int vista_identificador = -1;

    JPanel panel = null;
    JDialog dialog = null;
    JFrame frame = null;
    InfoViewController parent;
    AutorizacionView autoView;
    private boolean activarBotonesContingencia = false;
    Long id;
    IsAdminUseCase isAdminUseCase = new IsAdminUseCase();

    PersonasDao pdao = new PersonasDao();

    public PromotorSeleccionView(InfoViewController parent, int vista_identificador) {
        this.parent = parent;
        this.vista_identificador = vista_identificador;
        initComponents();
        this.init();
    }

    public PromotorSeleccionView(AutorizacionView parent, int vista_identificador) {
        this.autoView = parent;
        this.vista_identificador = vista_identificador;
        initComponents();
        this.init();
    }

    public PromotorSeleccionView(AutorizacionView parent, int vista_identificador, boolean activarBotonesContingencia) {
        this.autoView = parent;
        this.vista_identificador = vista_identificador;
        this.activarBotonesContingencia = activarBotonesContingencia;
        initComponents();
        this.init();
    }

    public PromotorSeleccionView(InfoViewController parent, JFrame frame) {
        initComponents();
        this.parent = parent;
        this.frame = frame;
        this.init();
    }

    public PromotorSeleccionView(InfoViewController parent, JPanel panel) {
        initComponents();
        this.parent = parent;
        this.panel = panel;
        this.init();
    }

    public PromotorSeleccionView(InfoViewController parent, JDialog dialog) {
        initComponents();
        this.parent = parent;
        this.dialog = dialog;
        this.init();
    }

    public static PromotorSeleccionView getInstance(InfoViewController parent, JPanel panel) {
        if (PromotorSeleccionView.instance == null) {
            PromotorSeleccionView.instance = new PromotorSeleccionView(parent, panel);
        } else {
            PromotorSeleccionView.instance.panel = panel;
            PromotorSeleccionView.instance.dialog = null;
            PromotorSeleccionView.instance.frame = null;
        }
        PromotorSeleccionView.promotorSeleccionado = false;
        return PromotorSeleccionView.instance;
    }

    public static PromotorSeleccionView getInstance(InfoViewController parent, JFrame frame) {
        if (PromotorSeleccionView.instance == null) {
            PromotorSeleccionView.instance = new PromotorSeleccionView(parent, frame);
        } else {
            PromotorSeleccionView.instance.panel = null;
            PromotorSeleccionView.instance.dialog = null;
            PromotorSeleccionView.instance.frame = frame;
        }
        PromotorSeleccionView.promotorSeleccionado = false;
        return PromotorSeleccionView.instance;
    }

    public static PromotorSeleccionView getInstance(InfoViewController parent, JDialog dialog) {
        if (PromotorSeleccionView.instance == null) {
            PromotorSeleccionView.instance = new PromotorSeleccionView(parent, dialog);
        } else {
            PromotorSeleccionView.instance.panel = null;
            PromotorSeleccionView.instance.dialog = dialog;
            PromotorSeleccionView.instance.frame = null;
        }
        PromotorSeleccionView.promotorSeleccionado = false;
        return PromotorSeleccionView.instance;
    }

    public static PromotorSeleccionView getInstance(InfoViewController parent, int vista_identificador) {

        if (PromotorSeleccionView.instance == null) {
            PromotorSeleccionView.instance = new PromotorSeleccionView(parent, vista_identificador);
        }
        PromotorSeleccionView.promotorSeleccionado = false;
        return PromotorSeleccionView.instance;
    }

    public static PromotorSeleccionView getInstance(AutorizacionView parent, int vista_identificador) {

        if (PromotorSeleccionView.instance == null) {
            PromotorSeleccionView.instance = new PromotorSeleccionView(parent, vista_identificador);
        }
        PromotorSeleccionView.promotorSeleccionado = false;
        return PromotorSeleccionView.instance;
    }

    private void init() {
        NovusUtils.ajusteFuente(this.getComponents(), NovusConstante.EXTRABOLD);

        setLocationRelativeTo(null);
        jScrollPane1.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(186, 12, 47);
            }
        });
        jScrollPane1.getVerticalScrollBar().setBackground(new Color(255, 255, 255));
        jScrollPane1.getHorizontalScrollBar().setBackground(new Color(255, 255, 255));
        this.renderizarUsuarios();
    }

    public void abrirVistaModulo(PersonaBean promotor) throws DAOException {
        try {
            Main.persona = promotor;
            switch (vista_identificador) {
                case NovusConstante.SELECCION_PROMOTOR_SOBRES:
                    SobresViewController sobres = new SobresViewController(parent, true);
                    sobres.setVisible(true);
                    break;
                case NovusConstante.SELECCION_PROMOTOR_INFORMACION_TURNOS:
                    TurnosInformeViewController turno = new TurnosInformeViewController(parent, true);
                    turno.setVisible(true);
                    break;
                case NovusConstante.SELECCION_PROMOTOR_INFORMACION_TURNOS_CONSOLIDADOS:
                    TurnosInformeConsolidadoViewController turnoConsolidado = new TurnosInformeConsolidadoViewController(parent, true);
                    turnoConsolidado.setVisible(true);
                    break;
                case NovusConstante.SELECCION_PROMOTOR_STORE:
                    StoreViewController store = new StoreViewController(parent, true);
                    parent.showParent(store);
                    StoreViewController.jentry_txt.requestFocus();
                    break;
                case NovusConstante.SELECCION_PROMOTOR_KCO:
                    KCOViewController kco = new KCOViewController(parent, true);
                    parent.showParent(kco);
                    KCOViewController.jentry_txt.requestFocusInWindow();
                    break;
                case NovusConstante.SELECCION_PROMOTOR_FACT_MANUAL:
                    if (activarBotonesContingencia) {
                        parent.mostrarSubPanel(new VentaManualMenuPanel(parent, true, true));
                    } else {
                        parent.mostrarSubPanel(new VentaManualMenuPanel(parent, true, false));
                    }
                    break;
                case NovusConstante.SELECCION_PROMOTOR_VENTAS:
                    VentasHistorialView hist = new VentasHistorialView(parent, true);
                    hist.setVisible(true);
                    break;
                case NovusConstante.SELECCION_PROMOTOR_VENTAS_HISTORICA:
                    VentasHistoriaFulLView hist2 = new VentasHistoriaFulLView(parent, true);
                    hist2.setVisible(true);
                    break;
                case NovusConstante.SELECCION_PROMOTOR_AUTORIZACION_VENTA:
                    VentaPredefinirPlaca instanciaVentaPlaca = VentaPredefinirPlaca.getInstance(parent, true);
                    instanciaVentaPlaca.setVisible(true);
                    break;
                case NovusConstante.SELECCION_PROMOTOR_RUMBO:
                    RumboView instanciaRumbo = RumboView.getInstance(parent, true);
                    instanciaRumbo.setVisible(true);
                    break;

                case NovusConstante.SELECCION_PROMOTOR_FACTURACION_ELECTRONICA:
                    ClienteFacturaElectronica fact = new ClienteFacturaElectronica(parent, true, false, false);
                    NovusUtils.printLn("abriendo la vista de facturacio electronica desde -> " + PromotorSeleccionView.class.getName());
                    fact.setVisible(true);
                    break;
                
                case NovusConstante.SELECCION_PROMOTOR_TIPO_VENTA:
                    VentaPlacaView ventaPlaca = VentaPlacaView.getInstance(parent, true);
                    ventaPlaca.setVisible(true);
                    break;
                case NovusConstante.SELECCION_PROMOTOR_PREDETERMINAR:
                case NovusConstante.SELECCION_PROMOTOR_CALIBRACION:
                case NovusConstante.SELECCION_PROMOTOR_CONSUMO_PROPIO:

                    switch (vista_identificador) {
                        case NovusConstante.SELECCION_PROMOTOR_PREDETERMINAR:
                            TipoVentaViewController tipoVenta = new TipoVentaViewController(parent, true);

                            //if (pdao.isAdmin(id)) {
                            if (isAdminUseCase.execute(id)) {
                                tipoVenta.setView("VENTAS PREDETERMINADAS");
                                tipoVenta.setVisible(true);
                            } else {
                                tipoVenta.setView("VENTAS PREDETERMINADAS");
                                AutorizacionView auto = new AutorizacionView(parent, true, tipoVenta);
                                auto.setVisible(true);
                            }
                            break;
                        case NovusConstante.SELECCION_PROMOTOR_CALIBRACION:
                            TipoVentaViewController tipoVentaCalibracion = new TipoVentaViewController(parent, true);
                            tipoVentaCalibracion.setView("CALIBRACIONES");
                            tipoVentaCalibracion.setVisible(true);
                            break;
                        case NovusConstante.SELECCION_PROMOTOR_CONSUMO_PROPIO:
                            TipoVentaViewController tipoVentaConsumo = new TipoVentaViewController(parent, true);

                            //if (pdao.isAdmin(id)) {
                            if (isAdminUseCase.execute(id)) {
                                tipoVentaConsumo.setView("CONSUMO PROPIO");
                                tipoVentaConsumo.setVisible(true);
                            } else {
                                tipoVentaConsumo.setView("CONSUMO PROPIO");
                                AutorizacionView auto = new AutorizacionView(parent, true, tipoVentaConsumo);
                                auto.setVisible(true);
                            }
                            break;
                    }
                    break;
            }
            cerrar(true);
        } catch (Exception ex) {
            Logger.getLogger(PromotorSeleccionView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void renderizarUsuarios() {
        try {
            int i = 0;
            int height = 88;
            int panelHeight;
            for (PersonaBean persona : InfoViewController.turnosPersonas) {
                PromotorItem item = new PromotorItem(persona);
                item.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseReleased(java.awt.event.MouseEvent evt) {
                        try {
                            PromotorSeleccionView.promotorSeleccionado = true;
                            id = persona.getId();
                            abrirVistaModulo(persona);
                        } catch (DAOException ex) {
                            Logger.getLogger(PromotorSeleccionView.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
                jPanel1.add(item);
                item.setBounds(20, (i * height) + (10 * (i + 1)), 1072, height);
                i++;
                panelHeight = Math.max(jPanel1.getHeight(), ((100 * i) + (1 * (i + 1))));
                jPanel1.setPreferredSize(new Dimension(jPanel1.getHeight(), panelHeight));
            }
        } catch (Exception ex) {
            Logger.getLogger(PromotorSeleccionView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void cerrar(Boolean seleccionado) {
        PromotorSeleccionView.promotorSeleccionado = seleccionado == null ? false : seleccionado;
        PromotorSeleccionView.instance = null;
        parent.contadorVentaManual = 0;
        this.setVisible(false);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1280, 800));
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(null);

        pnl.setLayout(null);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 51));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("FEC. INICIO");
        pnl.add(jLabel7);
        jLabel7.setBounds(890, 40, 280, 40);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 51));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("PROMOTOR");
        pnl.add(jLabel6);
        jLabel6.setBounds(300, 40, 590, 40);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/BtCerrar.png"))); // NOI18N
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel2MouseReleased(evt);
            }
        });
        pnl.add(jLabel2);
        jLabel2.setBounds(1210, 20, 50, 46);

        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1120, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 610, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(jPanel1);

        pnl.add(jScrollPane1);
        jScrollPane1.setBounds(80, 100, 1120, 590);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 51));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("IDENTIFICACION");
        pnl.add(jLabel5);
        jLabel5.setBounds(120, 40, 180, 40);

        jLabel3.setBackground(new java.awt.Color(255, 255, 0));
        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 28)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 0));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("SELECCIONE EL PROMOTOR");
        pnl.add(jLabel3);
        jLabel3.setBounds(30, 720, 1220, 70);

        jLabel1.setBackground(new java.awt.Color(102, 102, 102));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        pnl.add(jLabel1);
        jLabel1.setBounds(0, 0, 1280, 800);

        getContentPane().add(pnl);
        pnl.setBounds(0, 0, 1280, 800);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseReleased
        NovusUtils.beep();
        cerrar(null);
    }//GEN-LAST:event_jLabel2MouseReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    public javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnl;
    // End of variables declaration//GEN-END:variables
}
