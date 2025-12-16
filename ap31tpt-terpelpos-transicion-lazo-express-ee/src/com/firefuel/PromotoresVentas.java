package com.firefuel;

import com.bean.PersonaBean;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;

public class PromotoresVentas extends javax.swing.JPanel {

    PersonaBean persona = null;

    public PromotoresVentas(PersonaBean persona) {
        initComponents();
        this.persona = persona;
        init();
    }

    void init() {
        NovusUtils.ajusteFuente(this.getComponents(), NovusConstante.EXTRABOLD);
        renderJornadaDatos();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jidentificacion = new javax.swing.JLabel();
        jnombre = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setOpaque(false);
        setLayout(null);

        jidentificacion.setFont(new java.awt.Font("Tahoma", 0, 22)); // NOI18N
        jidentificacion.setForeground(new java.awt.Color(255, 255, 255));
        jidentificacion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jidentificacion.setText("123456789");
        add(jidentificacion);
        jidentificacion.setBounds(20, 20, 270, 50);

        jnombre.setFont(new java.awt.Font("Tahoma", 0, 22)); // NOI18N
        jnombre.setForeground(new java.awt.Color(255, 255, 255));
        jnombre.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jnombre.setText("ROBERTO MOLINA SILVERA");
        add(jnombre);
        jnombre.setBounds(300, 20, 450, 50);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/Promotores.png"))); // NOI18N
        add(jLabel1);
        jLabel1.setBounds(0, 0, 760, 104);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jidentificacion;
    private javax.swing.JLabel jnombre;
    // End of variables declaration//GEN-END:variables

    void renderJornadaDatos() {
        jidentificacion.setText(this.persona.getIdentificacion());
        jnombre.setText(this.persona.getNombre() + " "
                + (this.persona.getApellidos() != null ? this.persona.getApellidos() : ""));
    }

}
