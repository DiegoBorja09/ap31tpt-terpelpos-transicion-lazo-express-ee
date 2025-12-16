package com.firefuel;

import com.bean.PersonaBean;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import java.text.SimpleDateFormat;

public class PromotorItem extends javax.swing.JPanel {

    PersonaBean persona = null;
    SimpleDateFormat sdf2 = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATE);

    public PromotorItem(PersonaBean persona) {
        initComponents();
        this.persona = persona;
        this.init();
    }

    void init() {
        NovusUtils.ajusteFuente(this.getComponents(), NovusConstante.EXTRABOLD);
        renderJornadaDatos();
    }

    void renderJornadaDatos() {
        jidentificacion.setText(this.persona.getIdentificacion());
        jnombre.setText(this.persona.getNombre() + " "
                + (this.persona.getApellidos() != null ? this.persona.getApellidos() : ""));
        jfecha.setText(sdf2.format(this.persona.getFechaInicio()));
    }

    public void setLabel(String texto) {
        // jLabel2.setText(texto);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jfecha = new javax.swing.JLabel();
        jnombre = new javax.swing.JLabel();
        jidentificacion = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setOpaque(false);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
        });
        setLayout(null);

        jfecha.setFont(new java.awt.Font("Tahoma", 0, 22)); // NOI18N
        jfecha.setForeground(new java.awt.Color(255, 255, 255));
        jfecha.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jfecha.setText("2020-02-01 20:20");
        add(jfecha);
        jfecha.setBounds(760, 20, 290, 50);

        jnombre.setFont(new java.awt.Font("Tahoma", 0, 22)); // NOI18N
        jnombre.setForeground(new java.awt.Color(255, 255, 255));
        jnombre.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jnombre.setText("ROBERTO MOLINA SILVERA");
        add(jnombre);
        jnombre.setBounds(230, 20, 500, 50);

        jidentificacion.setFont(new java.awt.Font("Tahoma", 0, 22)); // NOI18N
        jidentificacion.setForeground(new java.awt.Color(255, 255, 255));
        jidentificacion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jidentificacion.setText("123456789");
        add(jidentificacion);
        jidentificacion.setBounds(20, 20, 190, 50);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btPromotor.png"))); // NOI18N
        add(jLabel1);
        jLabel1.setBounds(0, 0, 1072, 88);
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_formMouseReleased
        desactivarButton();
    }// GEN-LAST:event_formMouseReleased

    void desactivarButton() {
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btPromotor.png")));
    }

    private void formMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_formMousePressed
        NovusUtils.beep();
        activarButton();
    }// GEN-LAST:event_formMousePressed

    void activarButton() {
        jLabel1.setIcon(
                new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btPressPromotor.png")));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jfecha;
    private javax.swing.JLabel jidentificacion;
    private javax.swing.JLabel jnombre;
    // End of variables declaration//GEN-END:variables
}
