package com.firefuel;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.EquipoDao;
import com.firefuel.utils.ShowMessageSingleton;
import java.awt.CardLayout;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class SurtidorMenuPanelController extends javax.swing.JDialog {

    InfoViewController base;
    JFrame parent;

    public SurtidorMenuPanelController(InfoViewController base, boolean modal) {
        super(base, modal);
        this.base = base;
        initComponents();
        init();
    }

    void init() {
        NovusUtils.ajusteFuente(jTitle, NovusConstante.EXTRABOLD);
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_container = new javax.swing.JPanel();
        menu_surtidores = new javax.swing.JPanel();
        pnlContenedor = new javax.swing.JPanel();
        pnlPrincipal = new javax.swing.JPanel();
        jTitle = new javax.swing.JLabel();
        btnHome = new javax.swing.JLabel();
        iconSurtidor = new javax.swing.JLabel();
        lbl1 = new javax.swing.JLabel();
        lblCalibraciones = new javax.swing.JLabel();
        btnCalibraciones = new javax.swing.JLabel();
        lblCambioPrecio = new javax.swing.JLabel();
        lbl2 = new javax.swing.JLabel();
        btnCambioPrecio = new javax.swing.JLabel();
        lbl3 = new javax.swing.JLabel();
        lblBloqueo = new javax.swing.JLabel();
        btnBloqueo = new javax.swing.JLabel();
        lblSaltosLecturas = new javax.swing.JLabel();
        lbl4 = new javax.swing.JLabel();
        btnSaltosLecturas = new javax.swing.JLabel();
        lblEntradaCombustible = new javax.swing.JLabel();
        lbl5 = new javax.swing.JLabel();
        btnEntradaCombustible = new javax.swing.JLabel();
        lbl6 = new javax.swing.JLabel();
        lblHistorialRemisiones = new javax.swing.JLabel();
        btnHistorialRemisiones = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        fnd = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1280, 800));
        setUndecorated(true);

        pnl_container.setLayout(new java.awt.CardLayout());

        menu_surtidores.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlContenedor.setMaximumSize(new java.awt.Dimension(1280, 800));
        pnlContenedor.setMinimumSize(new java.awt.Dimension(1280, 800));
        pnlContenedor.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnlContenedor.setLayout(new java.awt.CardLayout());

        pnlPrincipal.setLayout(null);

        jTitle.setFont(new java.awt.Font("Conthrax", 1, 36)); // NOI18N
        jTitle.setForeground(new java.awt.Color(255, 255, 255));
        jTitle.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jTitle.setText("SURTIDORES");
        pnlPrincipal.add(jTitle);
        jTitle.setBounds(110, 15, 420, 60);

        btnHome.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_home.png"))); // NOI18N
        btnHome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnHomeMouseReleased(evt);
            }
        });
        pnlPrincipal.add(btnHome);
        btnHome.setBounds(1200, 10, 70, 70);

        iconSurtidor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iconSurtidor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/icono_triangulo_amarillo.png"))); // NOI18N
        pnlPrincipal.add(iconSurtidor);
        iconSurtidor.setBounds(20, 0, 70, 80);

        lbl1.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        lbl1.setForeground(new java.awt.Color(186, 12, 47));
        lbl1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl1.setText("1");
        pnlPrincipal.add(lbl1);
        lbl1.setBounds(30, 120, 80, 50);

        lblCalibraciones.setFont(new java.awt.Font("Roboto", 1, 22)); // NOI18N
        lblCalibraciones.setForeground(new java.awt.Color(255, 255, 255));
        lblCalibraciones.setText("CALIBRACIONES");
        pnlPrincipal.add(lblCalibraciones);
        lblCalibraciones.setBounds(120, 110, 280, 70);

        btnCalibraciones.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        btnCalibraciones.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnCalibracionesMouseReleased(evt);
            }
        });
        pnlPrincipal.add(btnCalibraciones);
        btnCalibraciones.setBounds(30, 110, 390, 70);

        lblCambioPrecio.setFont(new java.awt.Font("Roboto", 1, 22)); // NOI18N
        lblCambioPrecio.setForeground(new java.awt.Color(255, 255, 255));
        lblCambioPrecio.setText("CAMBIO PRECIO");
        pnlPrincipal.add(lblCambioPrecio);
        lblCambioPrecio.setBounds(120, 200, 280, 70);

        lbl2.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        lbl2.setForeground(new java.awt.Color(186, 12, 47));
        lbl2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl2.setText("2");
        pnlPrincipal.add(lbl2);
        lbl2.setBounds(40, 210, 70, 45);

        btnCambioPrecio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        btnCambioPrecio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnCambioPrecioMouseReleased(evt);
            }
        });
        pnlPrincipal.add(btnCambioPrecio);
        btnCambioPrecio.setBounds(30, 200, 390, 70);

        lbl3.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        lbl3.setForeground(new java.awt.Color(186, 12, 47));
        lbl3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl3.setText("3");
        pnlPrincipal.add(lbl3);
        lbl3.setBounds(40, 300, 70, 50);

        lblBloqueo.setFont(new java.awt.Font("Roboto", 1, 22)); // NOI18N
        lblBloqueo.setForeground(new java.awt.Color(255, 255, 255));
        lblBloqueo.setText("BLOQUEO");
        pnlPrincipal.add(lblBloqueo);
        lblBloqueo.setBounds(120, 290, 280, 70);

        btnBloqueo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        btnBloqueo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnBloqueoMouseReleased(evt);
            }
        });
        pnlPrincipal.add(btnBloqueo);
        btnBloqueo.setBounds(30, 290, 390, 70);

        lblSaltosLecturas.setFont(new java.awt.Font("Roboto", 1, 22)); // NOI18N
        lblSaltosLecturas.setForeground(new java.awt.Color(255, 255, 255));
        lblSaltosLecturas.setText("SALTOS LECTURAS");
        pnlPrincipal.add(lblSaltosLecturas);
        lblSaltosLecturas.setBounds(120, 380, 280, 70);

        lbl4.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        lbl4.setForeground(new java.awt.Color(186, 12, 47));
        lbl4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl4.setText("4");
        pnlPrincipal.add(lbl4);
        lbl4.setBounds(40, 390, 70, 50);

        btnSaltosLecturas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        btnSaltosLecturas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnSaltosLecturasMouseReleased(evt);
            }
        });
        pnlPrincipal.add(btnSaltosLecturas);
        btnSaltosLecturas.setBounds(30, 380, 390, 70);

        lblEntradaCombustible.setFont(new java.awt.Font("Roboto", 1, 22)); // NOI18N
        lblEntradaCombustible.setForeground(new java.awt.Color(255, 255, 255));
        lblEntradaCombustible.setText("ENTRADA COMBUSTIBLE");
        pnlPrincipal.add(lblEntradaCombustible);
        lblEntradaCombustible.setBounds(120, 470, 280, 70);

        lbl5.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        lbl5.setForeground(new java.awt.Color(186, 12, 47));
        lbl5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl5.setText("5");
        pnlPrincipal.add(lbl5);
        lbl5.setBounds(40, 480, 70, 50);

        btnEntradaCombustible.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        btnEntradaCombustible.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnEntradaCombustibleMouseReleased(evt);
            }
        });
        pnlPrincipal.add(btnEntradaCombustible);
        btnEntradaCombustible.setBounds(30, 470, 390, 70);

        lbl6.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        lbl6.setForeground(new java.awt.Color(186, 12, 47));
        lbl6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl6.setText("6");
        pnlPrincipal.add(lbl6);
        lbl6.setBounds(40, 570, 70, 50);

        lblHistorialRemisiones.setFont(new java.awt.Font("Roboto", 1, 22)); // NOI18N
        lblHistorialRemisiones.setForeground(new java.awt.Color(255, 255, 255));
        lblHistorialRemisiones.setText("HISTORIAL REMISIONES");
        pnlPrincipal.add(lblHistorialRemisiones);
        lblHistorialRemisiones.setBounds(120, 560, 280, 70);

        btnHistorialRemisiones.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        btnHistorialRemisiones.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnHistorialRemisionesMouseReleased(evt);
            }
        });
        pnlPrincipal.add(btnHistorialRemisiones);
        btnHistorialRemisiones.setBounds(30, 560, 390, 70);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/icono_lg_surtidor.png"))); // NOI18N
        jLabel13.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnlPrincipal.add(jLabel13);
        jLabel13.setBounds(710, 200, 360, 410);

        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnlPrincipal.add(jLabel33);
        jLabel33.setBounds(1180, 3, 10, 80);

        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnlPrincipal.add(jLabel34);
        jLabel34.setBounds(1130, 710, 10, 80);

        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        pnlPrincipal.add(jLabel35);
        jLabel35.setBounds(120, 710, 10, 80);

        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        pnlPrincipal.add(jLabel36);
        jLabel36.setBounds(10, 710, 100, 80);

        fnd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_submenu_blanco.png"))); // NOI18N
        fnd.setText("jLabel12");
        pnlPrincipal.add(fnd);
        fnd.setBounds(0, 0, 1280, 800);

        pnlContenedor.add(pnlPrincipal, "pnlPrincipal");

        menu_surtidores.add(pnlContenedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pnl_container.add(menu_surtidores, "menu_surtidores");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(pnl_container, javax.swing.GroupLayout.PREFERRED_SIZE, 1280, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(pnl_container, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnHistorialRemisionesMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHistorialRemisionesMouseReleased
        HistorialRemisiones historialRemisiones = new HistorialRemisiones(() -> {
            cambiarPanelHome();
        });
        mostrarSubPanel(historialRemisiones);
    }//GEN-LAST:event_btnHistorialRemisionesMouseReleased

    private void btnEntradaCombustibleMouseReleased(java.awt.event.MouseEvent evt) {
        mostrarVistaRecepcionCombustible();
    }

    private void btnSaltosLecturasMouseReleased(java.awt.event.MouseEvent evt) {
        mostrarVistaSaltos();
    }

    private void btnBloqueoMouseReleased(java.awt.event.MouseEvent evt) {
        mostrarBloqueo();
    }

    private void btnCalibracionesMouseReleased(java.awt.event.MouseEvent evt) {
        mostrarCalibraciones();
    }

    private void btnCambioPrecioMouseReleased(java.awt.event.MouseEvent evt) {
        mostrarCambioPrecio();
    }

    private void btnHomeMouseReleased(java.awt.event.MouseEvent evt) {
        cerrarsubmenu();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel btnBloqueo;
    private javax.swing.JLabel btnCalibraciones;
    private javax.swing.JLabel btnCambioPrecio;
    private javax.swing.JLabel btnEntradaCombustible;
    private javax.swing.JLabel btnHistorialRemisiones;
    private javax.swing.JLabel btnHome;
    private javax.swing.JLabel btnSaltosLecturas;
    private javax.swing.JLabel fnd;
    private javax.swing.JLabel iconSurtidor;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jTitle;
    private javax.swing.JLabel lbl1;
    private javax.swing.JLabel lbl2;
    private javax.swing.JLabel lbl3;
    private javax.swing.JLabel lbl4;
    private javax.swing.JLabel lbl5;
    private javax.swing.JLabel lbl6;
    private javax.swing.JLabel lblBloqueo;
    private javax.swing.JLabel lblCalibraciones;
    private javax.swing.JLabel lblCambioPrecio;
    private javax.swing.JLabel lblEntradaCombustible;
    private javax.swing.JLabel lblHistorialRemisiones;
    private javax.swing.JLabel lblSaltosLecturas;
    public javax.swing.JPanel menu_surtidores;
    private javax.swing.JPanel pnlContenedor;
    private javax.swing.JPanel pnlPrincipal;
    public javax.swing.JPanel pnl_container;
    // End of variables declaration//GEN-END:variables

    private void cerrarsubmenu() {
        this.setVisible(false);
        this.base.mostrarOcultarMenu();
    }

    void mostrarCambioPrecio() {
        NovusUtils.beep();
        CambiarPrecioMenuPanelController cambiarp = new CambiarPrecioMenuPanelController(this.base, true);
        AutorizacionView autoView = new AutorizacionView(this.base, true, cambiarp, false);
        autoView.setVisible(true);
    }

    private void mostrarCalibraciones() {
        NovusUtils.beep();
        base.recargarPersona();
        if (Main.persona == null) {
            cerrarsubmenu();
            base.mostrarPanelSinTurnos();
        } else {
            TipoVentaViewController tipoVenta = new TipoVentaViewController(base, true);
            AutorizacionView auto = new AutorizacionView(base, true, tipoVenta);
            tipoVenta.setView("CALIBRACIONES");
            auto.setVisible(true);
        }
    }

    private void mostrarBloqueo() {
        NovusUtils.beep();
        BloqueoViewController tipoVenta = new BloqueoViewController(base, true);
        tipoVenta.setVisible(true);
    }

    void mostrarVistaRecepcionCombustible() {
        NovusUtils.beep();
        if (Main.persona == null) {
            cerrarsubmenu();
            base.mostrarPanelSinTurnos();
        } else {
            EquipoDao dao = new EquipoDao();
            String valor = dao.getParametroWacher(NovusConstante.PREFERENCE_VEEDER_ROOT);
            if (valor != null) {
                boolean hayVeederRoot = false;
                if (valor.equals("S")) {
                    hayVeederRoot = true;
                }
                RecepcionCombustibleView tipoVenta = new RecepcionCombustibleView(base, true, hayVeederRoot);
                tipoVenta.setVisible(true);
            } else {
                RecepcionCombustibleView tipoVenta = new RecepcionCombustibleView(base, true, false);
                tipoVenta.setVisible(true);
            }
        }
    }

    private void mostrarVistaSaltos() {
        NovusUtils.beep();
        Runnable runnable = () -> cargarPanel(menu_surtidores);
        MenuSaltosLecturas menu = new MenuSaltosLecturas(runnable);
        AutorizacionView autoView = new AutorizacionView(this.parent, () -> cargarPanel(menu), true, runnable);
        autoView.setVisible(true);

    }

    public void cargarPanel(JPanel panel) {
        pnl_container.removeAll();
        pnl_container.add(panel);
        pnl_container.revalidate();
        pnl_container.repaint();
    }

    private void Async(Runnable runnable, int tiempo) {
        new Thread(() -> {
            try {
                Thread.sleep(tiempo * 1000);
                runnable.run();
            } catch (InterruptedException e) {
                Logger.getLogger(SurtidorMenuPanelController.class.getName()).log(Level.SEVERE, null, e);
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public void mostrarPanelMensaje(String mensaje, String icono, String letterCase) {

        if (icono.length() == 0) {
            icono = "/com/firefuel/resources/btBad.png";
        }
        if (icono.length() == 1) {
            icono = "/com/firefuel/resources/btOk.png";
        }

        Runnable runnable = () -> {
            cambiarPanelHome();
        };

        ParametrosMensajes parametrosMensajes = new ParametrosMensajesBuilder().setMsj(mensaje)
                .setRuta(icono).setHabilitar(true).setRunnable(runnable)
                .setLetterCase(letterCase).build();
        
        pnl_container.add("card_mensajes", ShowMessageSingleton.showMassegesInstance().execute(parametrosMensajes));
        CardLayout panel = (CardLayout) pnl_container.getLayout();
        panel.show(pnl_container, "card_mensajes");
        Async(runnable, 3);
    }

    public void cambiarPanelHome() {
        CardLayout panel = (CardLayout) pnlContenedor.getLayout();
        panel.show(pnlContenedor, "pnlPrincipal");
    }

    private void mostrarSubPanel(JPanel panel) {
        CardLayout layout = (CardLayout) pnlContenedor.getLayout();
        pnlContenedor.add("pnl_ext", panel);
        layout.show(pnlContenedor, "pnl_ext");
    }

}
