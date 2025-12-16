package com.firefuel;

import com.application.useCases.bodegas.NegociosActivosUseCase;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.google.gson.JsonObject;
import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class VentaManualMenuPanel extends javax.swing.JPanel {

    InfoViewController vistaPrincipal;
    JFrame parent;
    String canastilla = "C";
    String market = "M";
    boolean ventanaPrincipal = false;
    boolean activarBotonesContingencia = false;
    NegociosActivosUseCase gegociosActivosUseCase = new NegociosActivosUseCase();
    public VentaManualMenuPanel(InfoViewController vistaPrincipal) {
        this.vistaPrincipal = vistaPrincipal;
        initComponents();
        botonoesActivos();
    }

    public VentaManualMenuPanel(InfoViewController base, boolean ventanaPrincipal) {
        this.vistaPrincipal = base;
        this.ventanaPrincipal = ventanaPrincipal;
        initComponents();
        botonoesActivos();
    }

    public VentaManualMenuPanel(InfoViewController base, boolean ventanaPrincipal, boolean activarBotonesContingencia) {
        this.vistaPrincipal = base;
        this.ventanaPrincipal = ventanaPrincipal;
        this.activarBotonesContingencia = activarBotonesContingencia;
        initComponents();
        botonoesActivos();
    }

    private void botonoesActivos() {
        //BotonesContingenciaDAO botones = new BotonesContingenciaDAO();
        //JsonObject data = botones.negociosActivos();
        JsonObject data = gegociosActivosUseCase.execute();

        boolean combustible = data.get("combustible").getAsBoolean();
        boolean canastillas = data.get("canastilla").getAsBoolean();
        boolean kioscos = data.get("kiosco").getAsBoolean();
        if (NovusConstante.ACTIVAR_BOTON_CONTINGENCIA && combustible) {
            btnContingenciaCombustible.setEnabled(combustible);
            btnManualCombustible.setEnabled(NovusConstante.ESTADO_SURTIDOR && combustible);
            tituloContingenciaCOM.setEnabled(combustible);
            numeroContingenciaCOM.setEnabled(combustible);
            tituloVentaManual.setEnabled(combustible);
            numeroVentaManual.setEnabled(combustible);
        } else {
            combustibleBotones(combustible);
        }
        if (!NovusConstante.HAY_INTERNET) {
            btnContingenciaCanastilla.setEnabled(canastillas);
            tituloContingenciaCAN.setEnabled(canastillas);
            numeroCanastilla.setEnabled(canastillas);
            btnContingenciaKiosco.setEnabled(kioscos);
            tituloContingenciaKIOSCO.setEnabled(kioscos);
            numeroKiosco.setEnabled(kioscos);
        } else if (NovusConstante.ACTIVAR_BOTON_CONTINGENCIA) {
            activarContingenciaManual(data);
        } else {
            btnContingenciaCanastilla.setEnabled(false);
            btnContingenciaKiosco.setEnabled(false);
            negociosActivosKisocoCan(kioscos, canastillas);
        }

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlPrincipal = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jTitle = new javax.swing.JLabel();
        tituloContingenciaCAN = new javax.swing.JLabel();
        numeroKiosco = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        tituloContingenciaKIOSCO = new javax.swing.JLabel();
        tituloContingenciaCOM = new javax.swing.JLabel();
        numeroContingenciaCOM = new javax.swing.JLabel();
        tituloVentaManual = new javax.swing.JLabel();
        btnContingenciaCombustible = new javax.swing.JLabel();
        numeroCanastilla = new javax.swing.JLabel();
        numeroVentaManual = new javax.swing.JLabel();
        btnContingenciaKiosco = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        btnCerrar = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        btnContingenciaCanastilla = new javax.swing.JLabel();
        btnManualCombustible = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(1280, 800));
        setLayout(null);

        pnlPrincipal.setLayout(new java.awt.CardLayout());

        jPanel1.setLayout(null);

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/icono_triangulo_amarillo.png"))); // NOI18N
        jPanel1.add(jLabel18);
        jLabel18.setBounds(0, 5, 90, 80);

        jTitle.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jTitle.setForeground(new java.awt.Color(255, 255, 255));
        jTitle.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jTitle.setText("VENTAS CONTINGENCIA");
        jPanel1.add(jTitle);
        jTitle.setBounds(90, 0, 490, 85);

        tituloContingenciaCAN.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        tituloContingenciaCAN.setForeground(new java.awt.Color(255, 255, 255));
        tituloContingenciaCAN.setText("F. CONTINGENCIA CANASTILLA");
        jPanel1.add(tituloContingenciaCAN);
        tituloContingenciaCAN.setBounds(120, 220, 290, 70);

        numeroKiosco.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        numeroKiosco.setForeground(new java.awt.Color(186, 12, 47));
        numeroKiosco.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        numeroKiosco.setText("3");
        jPanel1.add(numeroKiosco);
        numeroKiosco.setBounds(40, 320, 70, 70);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/icono_lg_ventas.png"))); // NOI18N
        jPanel1.add(jLabel3);
        jLabel3.setBounds(690, 220, 350, 360);

        tituloContingenciaKIOSCO.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        tituloContingenciaKIOSCO.setForeground(new java.awt.Color(255, 255, 255));
        tituloContingenciaKIOSCO.setText("F. CONTINGENCIA MARKET");
        jPanel1.add(tituloContingenciaKIOSCO);
        tituloContingenciaKIOSCO.setBounds(120, 320, 270, 70);

        tituloContingenciaCOM.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        tituloContingenciaCOM.setForeground(new java.awt.Color(255, 255, 255));
        tituloContingenciaCOM.setText("F. CONTINGENCIA COMBUSTIBLE");
        jPanel1.add(tituloContingenciaCOM);
        tituloContingenciaCOM.setBounds(110, 110, 310, 70);

        numeroContingenciaCOM.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        numeroContingenciaCOM.setForeground(new java.awt.Color(186, 12, 47));
        numeroContingenciaCOM.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        numeroContingenciaCOM.setText("1");
        jPanel1.add(numeroContingenciaCOM);
        numeroContingenciaCOM.setBounds(37, 110, 70, 70);

        tituloVentaManual.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        tituloVentaManual.setForeground(new java.awt.Color(255, 255, 255));
        tituloVentaManual.setText("F. MANUAL COMBUSTIBLE");
        jPanel1.add(tituloVentaManual);
        tituloVentaManual.setBounds(120, 420, 310, 70);

        btnContingenciaCombustible.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        btnContingenciaCombustible.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnContingenciaCombustibleMouseReleased(evt);
            }
        });
        jPanel1.add(btnContingenciaCombustible);
        btnContingenciaCombustible.setBounds(30, 110, 390, 70);

        numeroCanastilla.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        numeroCanastilla.setForeground(new java.awt.Color(186, 12, 47));
        numeroCanastilla.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        numeroCanastilla.setText("2");
        jPanel1.add(numeroCanastilla);
        numeroCanastilla.setBounds(40, 220, 70, 70);

        numeroVentaManual.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        numeroVentaManual.setForeground(new java.awt.Color(186, 12, 47));
        numeroVentaManual.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        numeroVentaManual.setText("4");
        jPanel1.add(numeroVentaManual);
        numeroVentaManual.setBounds(40, 420, 70, 70);

        btnContingenciaKiosco.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        btnContingenciaKiosco.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnContingenciaKioscoMouseReleased(evt);
            }
        });
        jPanel1.add(btnContingenciaKiosco);
        btnContingenciaKiosco.setBounds(30, 320, 390, 70);

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        jPanel1.add(jLabel26);
        jLabel26.setBounds(10, 710, 100, 80);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel1.add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel1.add(jLabel25);
        jLabel25.setBounds(1130, 710, 10, 80);

        btnCerrar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_home.png"))); // NOI18N
        btnCerrar.setAlignmentY(0.0F);
        btnCerrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnCerrarMouseReleased(evt);
            }
        });
        jPanel1.add(btnCerrar);
        btnCerrar.setBounds(1190, 10, 90, 70);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel1.add(jLabel28);
        jLabel28.setBounds(1170, 3, 10, 80);

        btnContingenciaCanastilla.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        btnContingenciaCanastilla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnContingenciaCanastillaMouseReleased(evt);
            }
        });
        jPanel1.add(btnContingenciaCanastilla);
        btnContingenciaCanastilla.setBounds(30, 220, 390, 70);

        btnManualCombustible.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        btnManualCombustible.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btnManualCombustibleMouseReleased(evt);
            }
        });
        jPanel1.add(btnManualCombustible);
        btnManualCombustible.setBounds(30, 420, 390, 70);

        jLabel36.setFont(new java.awt.Font("Terpel Sans", 1, 22)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(255, 255, 255));
        jLabel36.setText("VENTA CANASTILLA");
        jPanel1.add(jLabel36);
        jLabel36.setBounds(120, 550, 270, 70);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_submenu_blanco.png"))); // NOI18N
        jPanel1.add(jLabel1);
        jLabel1.setBounds(0, 0, 1280, 800);

        pnlPrincipal.add(jPanel1, "card2");

        add(pnlPrincipal);
        pnlPrincipal.setBounds(0, 0, 1280, 800);
    }// </editor-fold>//GEN-END:initComponents

    private void btnCerrarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCerrarMouseReleased
        cerrar();
    }//GEN-LAST:event_btnCerrarMouseReleased

    private void btnContingenciaCombustibleMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnContingenciaCombustibleMouseReleased
        if (btnContingenciaCombustible.isEnabled()) {
            abrirFactManual();
        }
    }//GEN-LAST:event_btnContingenciaCombustibleMouseReleased

    private void btnContingenciaKioscoMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnContingenciaKioscoMouseReleased
        if (btnContingenciaKiosco.isEnabled()) {
            facManualKcoCan(market);
        }
    }//GEN-LAST:event_btnContingenciaKioscoMouseReleased

    private void btnContingenciaCanastillaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnContingenciaCanastillaMouseReleased
        if (btnContingenciaCanastilla.isEnabled()) {
            facManualKcoCan(canastilla);
        }
    }//GEN-LAST:event_btnContingenciaCanastillaMouseReleased

    private void btnManualCombustibleMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnManualCombustibleMouseReleased
        if (btnManualCombustible.isEnabled()) {
            facManualCombustible();
        }
    }//GEN-LAST:event_btnManualCombustibleMouseReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel btnCerrar;
    private javax.swing.JLabel btnContingenciaCanastilla;
    private javax.swing.JLabel btnContingenciaCombustible;
    private javax.swing.JLabel btnContingenciaKiosco;
    private javax.swing.JLabel btnManualCombustible;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel jTitle;
    private javax.swing.JLabel numeroCanastilla;
    private javax.swing.JLabel numeroContingenciaCOM;
    private javax.swing.JLabel numeroKiosco;
    private javax.swing.JLabel numeroVentaManual;
    private javax.swing.JPanel pnlPrincipal;
    private javax.swing.JLabel tituloContingenciaCAN;
    private javax.swing.JLabel tituloContingenciaCOM;
    private javax.swing.JLabel tituloContingenciaKIOSCO;
    private javax.swing.JLabel tituloVentaManual;
    // End of variables declaration//GEN-END:variables

    private void abrirFactManual() {
        NovusUtils.beep();
        NovusConstante.VENTAS_CONTINGENCIA = true;
        FacturacionManualView factManual = new FacturacionManualView(vistaPrincipal, true);
        factManual.jTitle.setText("INGRESO DE  FACTURA DE CONTINGENCIA");
        factManual.setVisible(true);
    }

    private void facManualKcoCan(String negocio) {
        NovusUtils.beep();
        NovusConstante.KIOSCO_CAN_CONTINGENCIA = true;
        NovusConstante.VENTAS_CONTINGENCIA = true;
        FacturacionManualKCView fManual = new FacturacionManualKCView(parent, true, negocio, vistaPrincipal, activarBotonesContingencia, ventanaPrincipal);
        fManual.setVisible(true);
    }

    private void facManualCombustible() {
        NovusUtils.beep();
        NovusConstante.VENTAS_CONTINGENCIA = false;
        FacturacionManualView fManual = new FacturacionManualView(vistaPrincipal, false, this, true);
        fManual.jTitle.setText("INGRESO DE  FACTURA DE VENTAS MANUAL");
        fManual.setVisible(true);
    }

    public void showParent(JFrame frame) {
        JPanel panel = (JPanel) frame.getContentPane();
        mostrarSubPanel(panel);
    }

    public void mostrarSubPanel(JPanel panel) {
        CardLayout layout = (CardLayout) pnlPrincipal.getLayout();
        pnlPrincipal.add("pnl_ext", panel);
        layout.show(pnlPrincipal, "pnl_ext");
    }

    private void cerrar() {
        NovusUtils.beep();
        if (this.ventanaPrincipal) {
            vistaPrincipal.cerrarSubmenu();
            this.vistaPrincipal.contadorVentaManual = 0;
        } else {
            vistaPrincipal.mostrarSubPanel(new VentaMenuPanelController(vistaPrincipal, parent));
        }
        NovusConstante.ACTIVAR_BOTON_CONTINGENCIA = false;
        NovusConstante.VENTAS_CONTINGENCIA = false;
    }

    private void activarContingenciaManual(JsonObject data) {
        boolean canastillas = data.get("canastilla").getAsBoolean();
        boolean kioscos = data.get("kiosco").getAsBoolean();
        if (canastillas && activarBotonesContingencia) {
            btnContingenciaCanastilla.setEnabled(true);
            tituloContingenciaCAN.setEnabled(true);
            numeroCanastilla.setEnabled(true);
        } else {
            btnContingenciaCanastilla.setEnabled(canastillas);
            tituloContingenciaCAN.setEnabled(canastillas);
            numeroCanastilla.setEnabled(canastillas);
        }
        if (kioscos && activarBotonesContingencia) {
            btnContingenciaKiosco.setEnabled(true);
            tituloContingenciaKIOSCO.setEnabled(true);
            numeroKiosco.setEnabled(true);
        } else {
            btnContingenciaKiosco.setEnabled(kioscos);
            tituloContingenciaKIOSCO.setEnabled(kioscos);
            numeroKiosco.setEnabled(kioscos);
        }
    }

    private void combustibleBotones(boolean combustible) {
        if (combustible) {
            if (!NovusConstante.HAY_INTERNET && NovusConstante.ESTADO_SURTIDOR) {
                btnContingenciaCombustible.setEnabled(true);
                btnManualCombustible.setEnabled(true);
            } else if (NovusConstante.HAY_INTERNET && NovusConstante.ESTADO_SURTIDOR) {
                btnContingenciaCombustible.setEnabled(false);
                btnManualCombustible.setEnabled(true);
            } else if (!NovusConstante.HAY_INTERNET && !NovusConstante.ESTADO_SURTIDOR) {
                btnContingenciaCombustible.setEnabled(true);
                btnManualCombustible.setEnabled(false);
            } else {
                btnContingenciaCombustible.setEnabled(false);
                btnManualCombustible.setEnabled(false);
            }
        } else {
            btnContingenciaCombustible.setEnabled(false);
            btnManualCombustible.setEnabled(false);
            tituloContingenciaCOM.setEnabled(false);
            numeroContingenciaCOM.setEnabled(false);
            tituloVentaManual.setEnabled(false);
            numeroVentaManual.setEnabled(false);
        }
    }

    private void negociosActivosKisocoCan(boolean kiosco, boolean canastilla) {
        tituloContingenciaCAN.setEnabled(canastilla);
        numeroCanastilla.setEnabled(canastilla);
        tituloContingenciaKIOSCO.setEnabled(kiosco);
        numeroKiosco.setEnabled(kiosco);
    }

}
