
package com.firefuel;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.MovimientosDao;
import com.firefuel.utils.ShowMessageSingleton;
import java.awt.CardLayout;
import java.awt.Point;
import javax.swing.JPanel;

/**
 *
 * @author usuario
 */
public class ConfiguracionMenuPanelController extends javax.swing.JPanel {

    InfoViewController base;
    MovimientosDao mdao = new MovimientosDao();

    public ConfiguracionMenuPanelController(InfoViewController base, boolean modal) {
        this.base = base;
        initComponents();
        this.updateUI();
        init();
    }

    void init() {
        NovusUtils.ajusteFuente(jTitle, NovusConstante.EXTRABOLD);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlPrincipal = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        jTitle = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();

        setLayout(null);

        pnlPrincipal.setPreferredSize(new java.awt.Dimension(1280, 800));
        pnlPrincipal.setLayout(new java.awt.CardLayout());

        jPanel1.setPreferredSize(new java.awt.Dimension(1280, 800));
        jPanel1.setLayout(null);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel1.add(jLabel31);
        jLabel31.setBounds(90, 10, 10, 68);

        jTitle.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jTitle.setForeground(new java.awt.Color(255, 255, 255));
        jTitle.setText("CONFIGURACION");
        jPanel1.add(jTitle);
        jTitle.setBounds(120, 15, 720, 50);

        jLabel34.setFont(new java.awt.Font("Roboto", 1, 22)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(255, 255, 255));
        jLabel34.setText("CONSECUTIVOS");
        jPanel1.add(jLabel34);
        jLabel34.setBounds(120, 580, 270, 70);

        jLabel36.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(186, 12, 47));
        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setText("6");
        jPanel1.add(jLabel36);
        jLabel36.setBounds(30, 580, 70, 70);

        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        jLabel35.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel35MouseReleased(evt);
            }
        });
        jPanel1.add(jLabel35);
        jLabel35.setBounds(20, 580, 390, 70);

        jLabel22.setFont(new java.awt.Font("Roboto", 1, 22)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("SALIR");
        jPanel1.add(jLabel22);
        jLabel22.setBounds(120, 490, 270, 70);

        jLabel23.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(186, 12, 47));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText("5");
        jPanel1.add(jLabel23);
        jLabel23.setBounds(30, 500, 70, 50);

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        jLabel14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel14MouseReleased(evt);
            }
        });
        jPanel1.add(jLabel14);
        jLabel14.setBounds(20, 490, 390, 70);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        jPanel1.add(jLabel29);
        jLabel29.setBounds(10, 710, 100, 80);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel1.add(jLabel27);
        jLabel27.setBounds(120, 710, 10, 80);

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel1.add(jLabel30);
        jLabel30.setBounds(1130, 710, 10, 80);

        jLabel24.setFont(new java.awt.Font("Roboto", 1, 22)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setText("PARAMETRIZACIONES");
        jPanel1.add(jLabel24);
        jLabel24.setBounds(120, 310, 270, 70);

        jLabel25.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(186, 12, 47));
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setText("3");
        jPanel1.add(jLabel25);
        jLabel25.setBounds(30, 320, 70, 40);

        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        jLabel26.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel26MouseReleased(evt);
            }
        });
        jPanel1.add(jLabel26);
        jLabel26.setBounds(20, 310, 390, 70);

        jLabel21.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(186, 12, 47));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("4");
        jPanel1.add(jLabel21);
        jLabel21.setBounds(30, 410, 70, 50);

        jLabel17.setFont(new java.awt.Font("Roboto", 1, 22)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("RES. DEFAULT");
        jPanel1.add(jLabel17);
        jLabel17.setBounds(120, 400, 270, 70);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_home.png"))); // NOI18N
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel1MouseReleased(evt);
            }
        });
        jPanel1.add(jLabel1);
        jLabel1.setBounds(1200, 10, 70, 70);

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        jLabel11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel11MouseReleased(evt);
            }
        });
        jPanel1.add(jLabel11);
        jLabel11.setBounds(20, 400, 390, 70);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/icono_triangulo_amarillo.png"))); // NOI18N
        jPanel1.add(jLabel16);
        jLabel16.setBounds(10, 10, 62, 63);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/icono_lg_configuracion.png"))); // NOI18N
        jLabel13.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel1.add(jLabel13);
        jLabel13.setBounds(700, 230, 350, 360);

        jLabel19.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(186, 12, 47));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("1");
        jPanel1.add(jLabel19);
        jLabel19.setBounds(20, 140, 80, 50);

        jLabel20.setFont(new java.awt.Font("Roboto", 1, 22)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("IMPRESORA");
        jPanel1.add(jLabel20);
        jLabel20.setBounds(120, 130, 270, 70);

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel10MouseReleased(evt);
            }
        });
        jPanel1.add(jLabel10);
        jLabel10.setBounds(20, 130, 390, 70);

        jLabel15.setFont(new java.awt.Font("Roboto", 1, 22)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("SINCRONIZACION");
        jPanel1.add(jLabel15);
        jLabel15.setBounds(120, 220, 270, 70);

        jLabel18.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(186, 12, 47));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("2");
        jPanel1.add(jLabel18);
        jLabel18.setBounds(30, 230, 70, 40);

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        jLabel9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel9MouseReleased(evt);
            }
        });
        jPanel1.add(jLabel9);
        jLabel9.setBounds(20, 220, 390, 70);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel1.add(jLabel28);
        jLabel28.setBounds(1180, 10, 10, 68);

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_submenu_blanco.png"))); // NOI18N
        jPanel1.add(jLabel12);
        jLabel12.setBounds(0, 0, 1280, 800);

        pnlPrincipal.add(jPanel1, "pnl_principal");

        add(pnlPrincipal);
        pnlPrincipal.setBounds(0, 0, 1280, 800);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel26MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel26MouseReleased
        showParametrizacionesView();
    }//GEN-LAST:event_jLabel26MouseReleased

    private void jLabel14MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel14MouseReleased
        salir();
    }//GEN-LAST:event_jLabel14MouseReleased

    private void jLabel35MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel35MouseReleased
        mostrarConsecutivos();
    }//GEN-LAST:event_jLabel35MouseReleased
    Point origin;

    private void jLabel1MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel1MouseReleased
        cerrarsubmenu();
    }// GEN-LAST:event_jLabel1MouseReleased

    private void jLabel10MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel10MouseReleased
        mostrarImpresora();
    }// GEN-LAST:event_jLabel10MouseReleased

    private void jLabel9MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel9MouseReleased
        mostrarSincronizacion();
    }// GEN-LAST:event_jLabel9MouseReleased

    private void jLabel11MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel11MouseReleased
        mostrarFormateo();
    }// GEN-LAST:event_jLabel11MouseReleased

    private void jLabejl14MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel14MouseReleased

        //  mostrarConfigDispositivos();
        mostrarCambioPrecio();
    }// GEN-LAST:event_jLabel14MouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel jTitle;
    private javax.swing.JPanel pnlPrincipal;
    // End of variables declaration//GEN-END:variables

    void salir() {
        DialogoConfirmacionMensaje confirmacion = new DialogoConfirmacionMensaje(this.base, true, () -> {
            System.exit(0);
        });
        confirmacion.setMensajes("", "¿Desea cerrar la aplicación?");
        confirmacion.setVisible(true);
    }

    private void cerrarsubmenu() {
        this.setVisible(false);
        this.base.mostrarOcultarMenu();

        // this.vistaPrincipal.mostrarOcultarMenu();
    }

    private void mostrarImpresora() {
        NovusUtils.beep();
        ImpresoraView imp = new ImpresoraView(base, true);
        imp.setVisible(true);
    }

    private void mostrarSincronizacion() {
        isSynchronizing();
    }

    private void mostrarFormateo() {
        NovusUtils.beep();
        FormatearViewController fom = new FormatearViewController(base, true);
        fom.mostrarPantalla(FormatearViewController.VENTANA_RESTAURARA_EQUIPO);
        fom.setVisible(true);
    }

    void mostrarCambioPrecio() {
        NovusUtils.beep();
        TipoVentaViewController tipoVenta = new TipoVentaViewController(base, true);
        tipoVenta.setView("CAMBIO PRECIO");
        tipoVenta.setVisible(true);
    }

    private void showParametrizacionesView() {
        NovusUtils.beep();
        ParametrizacionesViewController fom = new ParametrizacionesViewController(base, true);
        fom.setVisible(true);
//        FormatearViewController fom = new FormatearViewController(base, true);
//        fom.mostrarPantalla(FormatearViewController.VENTANA_PARAMETRO);
//        fom.jLabel8.setText("PARAMETRIZACIONES");
//        fom.setVisible(true);
    }

    private void mostrarCalibraciones() {
        NovusUtils.beep();
        TipoVentaViewController tipoVenta = new TipoVentaViewController(base, true);
        tipoVenta.setView("CALIBRACIONES");
        tipoVenta.setVisible(true);
    }

    private void mostrarConsecutivos() {
        NovusUtils.beep();
        ConsecutivosViewController consecutivosView = new ConsecutivosViewController(base, true);
        consecutivosView.setVisible(true);
    }

    public void isSynchronizing() {
        NovusUtils.beep();
        SincronizarView syncView = new SincronizarView(base, true, false);
        syncView.setVisible(true);
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

    public void mostrarSubPanel(JPanel panel) {
        CardLayout layout = (CardLayout) pnlPrincipal.getLayout();
        pnlPrincipal.add("pnl_ext", panel);
        layout.show(pnlPrincipal, "pnl_ext");
    }

    private void mostrarMenuPrincipal() {
        CardLayout layout = (CardLayout) pnlPrincipal.getLayout();
        layout.show(pnlPrincipal, "pnl_principal");
    }

}
