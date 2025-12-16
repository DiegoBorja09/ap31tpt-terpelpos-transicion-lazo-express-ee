/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.firefuel;

import com.controllers.NovusUtils;
import javax.swing.JLabel;

public class CategoriaProductoListItem2 extends javax.swing.JPanel {

    Long identificadorProducto;

    public CategoriaProductoListItem2(Long identificador) {
        identificadorProducto = identificador;
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jCantidad = new javax.swing.JLabel();
        jNombre = new javax.swing.JLabel();
        jPrecio = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPlu = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(204, 204, 204)));
        setMaximumSize(new java.awt.Dimension(540, 70));
        setMinimumSize(new java.awt.Dimension(540, 70));
        setPreferredSize(new java.awt.Dimension(540, 70));
        setLayout(null);

        jCantidad.setBackground(new java.awt.Color(255, 255, 255));
        jCantidad.setFont(new java.awt.Font("Roboto", 0, 16)); // NOI18N
        jCantidad.setForeground(new java.awt.Color(186, 12, 47));
        jCantidad.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jCantidad.setText("2");
        jCantidad.setAlignmentY(0.0F);
        jCantidad.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jCantidad.setOpaque(true);
        jCantidad.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jCantidadMousePressed(evt);
            }
        });
        add(jCantidad);
        jCantidad.setBounds(230, 40, 40, 20);

        jNombre.setFont(new java.awt.Font("Roboto", 1, 16)); // NOI18N
        jNombre.setText("<html>NOMBRE DEL PRODUCTO CON DESCRIPCIÓN LARGA</html>");
        add(jNombre);
        jNombre.setBounds(10, 0, 470, 40);

        jPrecio.setFont(new java.awt.Font("Roboto", 0, 17)); // NOI18N
        jPrecio.setForeground(new java.awt.Color(186, 12, 47));
        jPrecio.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jPrecio.setText("$");
        jPrecio.setAlignmentY(0.0F);
        jPrecio.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        add(jPrecio);
        jPrecio.setBounds(360, 40, 120, 20);

        jLabel4.setFont(new java.awt.Font("Roboto", 0, 15)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(153, 153, 153));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("VALOR:");
        jLabel4.setAlignmentY(0.0F);
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        add(jLabel4);
        jLabel4.setBounds(290, 40, 60, 20);

        jLabel2.setFont(new java.awt.Font("Roboto", 0, 8)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/addProducto.png"))); // NOI18N
        jLabel2.setText("AGREGAR");
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel2MouseReleased(evt);
            }
        });
        add(jLabel2);
        jLabel2.setBounds(480, 5, 50, 60);

        jLabel5.setFont(new java.awt.Font("Roboto", 0, 15)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(153, 153, 153));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("CANTIDAD:");
        jLabel5.setAlignmentY(0.0F);
        jLabel5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        add(jLabel5);
        jLabel5.setBounds(130, 40, 90, 20);

        jPlu.setFont(new java.awt.Font("Roboto", 0, 17)); // NOI18N
        jPlu.setForeground(new java.awt.Color(186, 12, 47));
        jPlu.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jPlu.setText("0000");
        jPlu.setAlignmentY(0.0F);
        jPlu.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        add(jPlu);
        jPlu.setBounds(60, 40, 60, 20);

        jLabel8.setFont(new java.awt.Font("Roboto", 0, 15)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(153, 153, 153));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("PLU:");
        jLabel8.setAlignmentY(0.0F);
        jLabel8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        add(jLabel8);
        jLabel8.setBounds(10, 40, 40, 20);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseReleased
        agregarProductoCortesia(evt);
    }//GEN-LAST:event_jLabel2MouseReleased

    private void jCantidadMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCantidadMousePressed
    }//GEN-LAST:event_jCantidadMousePressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JLabel jCantidad;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    public javax.swing.JLabel jNombre;
    public javax.swing.JLabel jPlu;
    public javax.swing.JLabel jPrecio;
    // End of variables declaration//GEN-END:variables
private void agregarProductoCortesia(java.awt.event.MouseEvent evt) {
        NovusUtils.beep();
        CategoriaProductoListItem2 fuente = (CategoriaProductoListItem2) ((JLabel) evt.getSource()).getParent();
        CortesiaView.productosVenta.put(identificadorProducto, fuente);
        CortesiaView.controlarProductosCortesia(identificadorProducto, 1);
    }
}
