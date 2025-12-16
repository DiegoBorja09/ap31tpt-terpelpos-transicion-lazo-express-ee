
package com.firefuel;

import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import javax.swing.JFrame;


public class CambiarPrecioMenuPanelController extends javax.swing.JDialog {

    
    InfoViewController base;
    JFrame parent;
    
    public CambiarPrecioMenuPanelController(InfoViewController base, boolean modal) {
        super(base, modal);
        this.base = base;
        initComponents();
    }
    
    void init() {
        NovusUtils.ajusteFuente(jTitle, NovusConstante.EXTRABOLD);
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btn_atras = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabelHistorial = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabelCambioPrecio = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabelProgramacion = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jTitle = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);

        jPanel1.setMaximumSize(new java.awt.Dimension(1280, 800));
        jPanel1.setMinimumSize(new java.awt.Dimension(1280, 800));
        jPanel1.setPreferredSize(new java.awt.Dimension(1280, 800));
        jPanel1.setLayout(null);

        btn_atras.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btn_atras.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        btn_atras.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btn_atrasMouseReleased(evt);
            }
        });
        jPanel1.add(btn_atras);
        btn_atras.setBounds(20, 10, 80, 70);

        jLabel30.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(186, 12, 47));
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setText("3");
        jPanel1.add(jLabel30);
        jLabel30.setBounds(40, 300, 70, 50);

        jLabel31.setFont(new java.awt.Font("Roboto", 1, 22)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(255, 255, 255));
        jLabel31.setText("HISTORIAL");
        jPanel1.add(jLabel31);
        jLabel31.setBounds(120, 290, 280, 70);

        jLabelHistorial.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        jLabelHistorial.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabelHistorialMouseReleased(evt);
            }
        });
        jPanel1.add(jLabelHistorial);
        jLabelHistorial.setBounds(30, 290, 390, 70);

        jLabel28.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(186, 12, 47));
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText("1");
        jPanel1.add(jLabel28);
        jLabel28.setBounds(30, 120, 80, 50);

        jLabel27.setFont(new java.awt.Font("Roboto", 1, 22)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(255, 255, 255));
        jLabel27.setText("CAMBIAR PRECIO");
        jPanel1.add(jLabel27);
        jLabel27.setBounds(120, 110, 280, 70);

        jLabelCambioPrecio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        jLabelCambioPrecio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabelCambioPrecioMouseReleased(evt);
            }
        });
        jPanel1.add(jLabelCambioPrecio);
        jLabelCambioPrecio.setBounds(30, 110, 390, 70);

        jLabel23.setFont(new java.awt.Font("Roboto", 1, 22)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("PROGRAMADO");
        jPanel1.add(jLabel23);
        jLabel23.setBounds(120, 200, 280, 70);

        jLabel22.setFont(new java.awt.Font("Impact", 1, 36)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(186, 12, 47));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("2");
        jPanel1.add(jLabel22);
        jLabel22.setBounds(40, 210, 70, 45);

        jLabelProgramacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_submenu.png"))); // NOI18N
        jLabelProgramacion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabelProgramacionMouseReleased(evt);
            }
        });
        jPanel1.add(jLabelProgramacion);
        jLabelProgramacion.setBounds(30, 200, 390, 70);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/icono_lg_surtidor.png"))); // NOI18N
        jLabel13.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel1.add(jLabel13);
        jLabel13.setBounds(710, 200, 360, 410);

        jTitle.setFont(new java.awt.Font("Conthrax", 1, 36)); // NOI18N
        jTitle.setForeground(new java.awt.Color(255, 255, 255));
        jTitle.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jTitle.setText("CAMBIO DE PRECIO");
        jPanel1.add(jTitle);
        jTitle.setBounds(110, 15, 420, 60);

        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel1.add(jLabel33);
        jLabel33.setBounds(1180, 3, 10, 80);

        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel1.add(jLabel34);
        jLabel34.setBounds(1130, 710, 10, 80);

        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        jPanel1.add(jLabel35);
        jLabel35.setBounds(120, 710, 10, 80);

        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        jPanel1.add(jLabel36);
        jLabel36.setBounds(10, 710, 100, 80);

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fnd_submenu_blanco.png"))); // NOI18N
        jLabel12.setText("jLabel12");
        jPanel1.add(jLabel12);
        jLabel12.setBounds(0, 0, 1280, 800);
        jPanel1.add(jLabel3);
        jLabel3.setBounds(27, 16, 60, 60);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1280, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        setSize(new java.awt.Dimension(1280, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btn_atrasMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_atrasMouseReleased

        cerrar();
    }//GEN-LAST:event_btn_atrasMouseReleased

    private void jLabelHistorialMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelHistorialMouseReleased
        mostrarHistorial();
    }//GEN-LAST:event_jLabelHistorialMouseReleased

    private void jLabelCambioPrecioMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelCambioPrecioMouseReleased
        mostrarCambioPrecio();
    }//GEN-LAST:event_jLabelCambioPrecioMouseReleased

    private void jLabelProgramacionMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelProgramacionMouseReleased
        mostrarProgramacion();
    }//GEN-LAST:event_jLabelProgramacionMouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel btn_atras;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabelCambioPrecio;
    private javax.swing.JLabel jLabelHistorial;
    private javax.swing.JLabel jLabelProgramacion;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel jTitle;
    // End of variables declaration//GEN-END:variables

    private void cerrar() {
        dispose();
    }

    private void mostrarCambioPrecio() {
        NovusUtils.beep();
        CambiarPrecioViewController cambiarpv = new CambiarPrecioViewController(base, true);
        cambiarpv.setVisible(true);
    }

    private void mostrarProgramacion() {
        NovusUtils.beep();
        CambiarPrecioProgramadoView cambioprog = new CambiarPrecioProgramadoView(base, true);
        cambioprog.setVisible(true);
    }

    private void mostrarHistorial() {
        NovusUtils.beep();
        CambiarPrecioHistorialView cambiohistorial = new CambiarPrecioHistorialView(base, true);
        cambiohistorial.setVisible(true);
    }

}
