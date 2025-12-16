package com.firefuel;

import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.firefuel.componentes.menuModElement.components.JpanelItem;
import com.firefuel.componentes.menuModElement.components.JpanelMenu;
import com.firefuel.componentes.menuModElement.renderization.LoadMenu;
import java.awt.Color;
import java.awt.Dimension;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.JLabel;

public class ReporteMenuPanelController extends javax.swing.JPanel {

    private JpanelMenu leftMenu = new JpanelMenu();
    private LoadMenu loadMenu = new LoadMenu();
    
    InfoViewController base;
    boolean activoMultisurtidores = false;

    private void inactivarConsolidado() {
        if (!activoMultisurtidores) {
            this.btconsolidado.setEnabled(activoMultisurtidores);
            this.numconsolidado.setEnabled(activoMultisurtidores);
            this.lbconsolidado.setEnabled(activoMultisurtidores);

            this.numconsolidado.setForeground(Color.WHITE);
            this.lbconsolidado.setForeground(Color.WHITE);
        }
    }

    public ReporteMenuPanelController(InfoViewController base) {
        this.base = base;
        initComponents();
        init();
        activoMultisurtidores = Main.getParametroCoreBoolean(NovusConstante.PREFERENCE_MULTISURTIDORES, true);
        inactivarConsolidado();
        updateUI();
    }

    private void init() {
        NovusUtils.ajusteFuente(jTitle, NovusConstante.EXTRABOLD);
        if (this.leftMenu.getListItems() == null) {

            ArrayList<JpanelItem> menuItems = new ArrayList<>();
            leftMenu.setMinItems(7);

            if (!Main.SIN_SURTIDOR) {

                menuItems.add(new JpanelItem(jLabel24, jLabel23, btisla));
                menuItems.add(new JpanelItem(numconsolidado, lbconsolidado, btconsolidado));
                menuItems.add(new JpanelItem(jLabel22, jLabel21, btcierredia));
                menuItems.add(new JpanelItem(jLabel29, jLabel28, btinventario));
                menuItems.add(new JpanelItem(jLabel32, jLabel31, btentrada));
                menuItems.add(new JpanelItem(jLabel33, jLabel34, btReporteKardex));
                menuItems.add(new JpanelItem(jLabel35, jLabel37, btVentasPromotor));
                
            } else {
                
                menuItems.add(new JpanelItem(jLabel24, jLabel23, btisla));
                menuItems.add(new JpanelItem(numconsolidado, lbconsolidado, btconsolidado));
                menuItems.add(new JpanelItem(jLabel22, jLabel21, btcierredia));
                menuItems.add(new JpanelItem(jLabel35, jLabel37, btVentasPromotor));
                
            }

            leftMenu.setListItems(menuItems);
            leftMenu.setjPanelRightMenu(jPanelRightMenu);

        }
        
       loadMenu.load(leftMenu);


    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelRightMenu = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        btisla = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        numconsolidado = new javax.swing.JLabel();
        lbconsolidado = new javax.swing.JLabel();
        btconsolidado = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        btcierredia = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        btinventario = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        btentrada = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        btReporteKardex = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        btVentasPromotor = new javax.swing.JLabel();
        jTitle = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();

        setLayout(null);

        jPanelRightMenu.setBackground(new java.awt.Color(255, 255, 255));
        jPanelRightMenu.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        jPanelRightMenu.setLayout(new javax.swing.BoxLayout(jPanelRightMenu, javax.swing.BoxLayout.Y_AXIS));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(null);

        jLabel23.setFont(new java.awt.Font("Terpel Sans", 1, 22)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("CIERRE ISLA");
        jPanel1.add(jLabel23);
        jLabel23.setBounds(150, 20, 160, 40);

        jLabel24.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(186, 12, 47));
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("1");
        jPanel1.add(jLabel24);
        jLabel24.setBounds(40, 10, 70, 60);

        btisla.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        btisla.setAlignmentX(0.5F);
        btisla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btislaMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btislaMouseReleased(evt);
            }
        });
        jPanel1.add(btisla);
        btisla.setBounds(30, -10, 390, 110);

        jPanelRightMenu.add(jPanel1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel2.setLayout(null);

        numconsolidado.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        numconsolidado.setForeground(new java.awt.Color(186, 12, 47));
        numconsolidado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        numconsolidado.setText("2");
        jPanel2.add(numconsolidado);
        numconsolidado.setBounds(40, 0, 70, 60);

        lbconsolidado.setFont(new java.awt.Font("Terpel Sans", 1, 22)); // NOI18N
        lbconsolidado.setForeground(new java.awt.Color(255, 255, 255));
        lbconsolidado.setText("CONSOLIDADOS");
        jPanel2.add(lbconsolidado);
        lbconsolidado.setBounds(120, 10, 180, 50);

        btconsolidado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        btconsolidado.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btconsolidadoMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btconsolidadoMouseReleased(evt);
            }
        });
        jPanel2.add(btconsolidado);
        btconsolidado.setBounds(30, 10, 400, 70);

        jPanelRightMenu.add(jPanel2);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(null);

        jLabel21.setFont(new java.awt.Font("Terpel Sans", 1, 22)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("CIERRE DIARIO");
        jPanel3.add(jLabel21);
        jLabel21.setBounds(150, 10, 170, 60);

        jLabel22.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(186, 12, 47));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("3");
        jPanel3.add(jLabel22);
        jLabel22.setBounds(40, 10, 70, 60);

        btcierredia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        btcierredia.setAlignmentX(0.5F);
        btcierredia.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btcierrediaMouseReleased(evt);
            }
        });
        jPanel3.add(btcierredia);
        btcierredia.setBounds(30, 10, 390, 60);

        jPanelRightMenu.add(jPanel3);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setLayout(null);

        jLabel29.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(186, 12, 47));
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("4");
        jPanel4.add(jLabel29);
        jLabel29.setBounds(40, 10, 70, 50);

        jLabel28.setFont(new java.awt.Font("Terpel Sans", 1, 22)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(255, 255, 255));
        jLabel28.setText("INVENTARIO TANQUES");
        jPanel4.add(jLabel28);
        jLabel28.setBounds(130, 10, 290, 60);

        btinventario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        btinventario.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btinventarioMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btinventarioMouseReleased(evt);
            }
        });
        jPanel4.add(btinventario);
        btinventario.setBounds(40, 0, 390, 80);

        jPanelRightMenu.add(jPanel4);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setLayout(null);

        jLabel32.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(186, 12, 47));
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setText("5");
        jPanel5.add(jLabel32);
        jLabel32.setBounds(50, 10, 70, 70);

        jLabel31.setFont(new java.awt.Font("Terpel Sans", 1, 22)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(255, 255, 255));
        jLabel31.setText("ENTRADAS COMBUSTIBLE");
        jPanel5.add(jLabel31);
        jLabel31.setBounds(130, 10, 290, 70);

        btentrada.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        btentrada.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btentradaMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btentradaMouseReleased(evt);
            }
        });
        jPanel5.add(btentrada);
        btentrada.setBounds(40, 0, 390, 90);

        jPanelRightMenu.add(jPanel5);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setLayout(null);

        jLabel34.setFont(new java.awt.Font("Terpel Sans", 1, 22)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(255, 255, 255));
        jLabel34.setText("REPORTE KARDEX");
        jPanel6.add(jLabel34);
        jLabel34.setBounds(130, 0, 240, 70);

        jLabel33.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(186, 12, 47));
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setText("6");
        jPanel6.add(jLabel33);
        jLabel33.setBounds(50, 10, 70, 60);

        btReporteKardex.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        btReporteKardex.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btReporteKardexMouseReleased(evt);
            }
        });
        jPanel6.add(btReporteKardex);
        btReporteKardex.setBounds(40, 0, 390, 80);

        jPanelRightMenu.add(jPanel6);

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel7.setLayout(null);

        jLabel35.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(186, 12, 47));
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setText("7");
        jPanel7.add(jLabel35);
        jLabel35.setBounds(40, 30, 70, 40);

        jLabel37.setFont(new java.awt.Font("Terpel Sans", 1, 22)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(255, 255, 255));
        jLabel37.setText("VENTAS POR PROMOTOR");
        jPanel7.add(jLabel37);
        jLabel37.setBounds(120, 10, 300, 70);

        btVentasPromotor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        btVentasPromotor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btVentasPromotorMouseReleased(evt);
            }
        });
        jPanel7.add(btVentasPromotor);
        btVentasPromotor.setBounds(30, 10, 390, 70);

        jPanelRightMenu.add(jPanel7);

        add(jPanelRightMenu);
        jPanelRightMenu.setBounds(0, 90, 460, 610);

        jTitle.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jTitle.setForeground(new java.awt.Color(255, 255, 255));
        jTitle.setText("REPORTES");
        add(jTitle);
        jTitle.setBounds(100, 5, 650, 80);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_home.png"))); // NOI18N
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel1MouseReleased(evt);
            }
        });
        add(jLabel1);
        jLabel1.setBounds(1180, 0, 90, 90);

        jLabel36.setFont(new java.awt.Font("Terpel Sans", 1, 22)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(255, 255, 255));
        jLabel36.setText("ARQUEO PROMOTOR");
        add(jLabel36);
        jLabel36.setBounds(130, 580, 290, 70);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/icono_triangulo_amarillo.png"))); // NOI18N
        add(jLabel16);
        jLabel16.setBounds(10, 10, 70, 70);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/icono_lg_reportes.png"))); // NOI18N
        jLabel13.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        add(jLabel13);
        jLabel13.setBounds(690, 220, 350, 360);

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        add(jLabel26);
        jLabel26.setBounds(10, 710, 100, 80);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        add(jLabel25);
        jLabel25.setBounds(1130, 710, 10, 80);

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        add(jLabel30);
        jLabel30.setBounds(1170, 3, 10, 80);

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_submenu_blanco.png"))); // NOI18N
        jLabel12.setText("jLabel12");
        add(jLabel12);
        jLabel12.setBounds(0, 0, 1280, 800);
    }// </editor-fold>//GEN-END:initComponents

    private void btislaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btislaMouseReleased
        abrirCierres();
    }//GEN-LAST:event_btislaMouseReleased

    private void btislaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btislaMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btislaMousePressed

    private void btinventarioMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btinventarioMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btinventarioMousePressed

    private void btinventarioMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btinventarioMouseReleased
        try {
            abrirInventarioTanques();
        } catch (DAOException ex) {
            Logger.getLogger(ReporteMenuPanelController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btinventarioMouseReleased

    private void btentradaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btentradaMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btentradaMousePressed

    private void btentradaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btentradaMouseReleased
        mostrarReporteEntradasCombustible();
    }//GEN-LAST:event_btentradaMouseReleased

    private void btconsolidadoMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btconsolidadoMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btconsolidadoMousePressed

    private void btconsolidadoMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btconsolidadoMouseReleased
        abrirConsolidadosCierres();
    }//GEN-LAST:event_btconsolidadoMouseReleased

    private void btReporteKardexMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btReporteKardexMouseReleased
        try {
            abrirKardex();
        } catch (SQLException ex) {
            Logger.getLogger(ReporteMenuPanelController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btReporteKardexMouseReleased

    private void btVentasPromotorMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btVentasPromotorMouseReleased
        abrirArqueoPromotor();
    }//GEN-LAST:event_btVentasPromotorMouseReleased

    private void jLabel1MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel1MouseReleased
        cerrarsubmenu();
    }// GEN-LAST:event_jLabel1MouseReleased

    private void btcierrediaMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel11MouseReleased
        mostrarReporteDiario();
    }// GEN-LAST:event_jLabel11MouseReleased

    void abrirConsolidadosCierres() {
        if (activoMultisurtidores) {
            NovusUtils.beep();
            ReportesHistorialTurnosViewConsolidados hist = new ReportesHistorialTurnosViewConsolidados(base, true);
            hist.setVisible(true);
        }
    }

    void abrirCierres() {
        NovusUtils.beep();
        ReportesHistorialTurnosViewCierres hist = new ReportesHistorialTurnosViewCierres(base, true);
        hist.setVisible(true);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel btReporteKardex;
    private javax.swing.JLabel btVentasPromotor;
    private javax.swing.JLabel btcierredia;
    private javax.swing.JLabel btconsolidado;
    private javax.swing.JLabel btentrada;
    private javax.swing.JLabel btinventario;
    private javax.swing.JLabel btisla;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanelRightMenu;
    private javax.swing.JLabel jTitle;
    private javax.swing.JLabel lbconsolidado;
    private javax.swing.JLabel numconsolidado;
    // End of variables declaration//GEN-END:variables

    private void cerrarsubmenu() {
        this.setVisible(false);
        this.base.cambiarPanelHome();
    }

    void abrirInventarioTanques() throws DAOException {
        NovusUtils.beep();
        InventariosTanques view = new InventariosTanques(this.base, true);
        view.setVisible(true);
    }

    void mostrarReporteEntradasCombustible() {
        NovusUtils.beep();
        ReporteRecepcionView hist = new ReporteRecepcionView(this.base, true);
        hist.setVisible(true);
    }

    private void mostrarReporteDiario() {
        NovusUtils.beep();
        ReportesHistorialTurnosDiarioView hist = new ReportesHistorialTurnosDiarioView(base, true);
        hist.setVisible(true);
    }

    private void abrirKardex() throws SQLException {
        NovusUtils.beep();
        ReporteInventarioKardex repoKardex = new ReporteInventarioKardex(base, true);
        repoKardex.setVisible(true);
    }

    private void abrirArqueoPromotor() {
        NovusUtils.beep();
        ReporteVentasPromotor arqueoPromotor = new ReporteVentasPromotor(base, true);
        arqueoPromotor.setVisible(true);
    }

}
