package com.firefuel;

import com.controllers.NovusConstante;
import com.bean.MovimientosDetallesBean;
import java.awt.Color;
import java.text.DecimalFormat;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author novus
 */
public class CategoriaProductoItemView extends javax.swing.JPanel {

    int item;
    MovimientosDetallesBean producto;
    StoreViewController pedido;
    KCOViewController pedidoKCO;
    boolean seleccion;
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);

    public CategoriaProductoItemView(StoreViewController pedido, MovimientosDetallesBean producto) {
        this.producto = producto;
        this.pedido = pedido;
        initComponents();
        jLabel1.setText("<html><center>" + producto.getDescripcion().toUpperCase() + "</center></html>");

        jLabel4.setText(NovusConstante.SIMBOLS_PRICE + " " + df.format(producto.getPrecio()));
        if (producto.isNuevo()) {
            jnuevo.setVisible(true);
        } else {
            jnuevo.setVisible(false);
        }

        if (producto.isFavorito()) {
            jfavorito.setVisible(true);
        } else {
            jfavorito.setVisible(false);
        }
        jselecion.setVisible(false);
    }

    public CategoriaProductoItemView(KCOViewController pedido, MovimientosDetallesBean producto) {
        this.producto = producto;
        this.pedidoKCO = pedido;
        initComponents();
        jLabel1.setText("<html><center>" + producto.getDescripcion().toUpperCase() + "</center></html>");

        jLabel4.setText(NovusConstante.SIMBOLS_PRICE + " " + df.format(producto.getPrecio()));
        if (producto.isNuevo()) {
            jnuevo.setVisible(true);
        } else {
            jnuevo.setVisible(false);
        }

        if (producto.isFavorito()) {
            jfavorito.setVisible(true);
        } else {
            jfavorito.setVisible(false);
        }
        jselecion.setVisible(false);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jnuevo = new javax.swing.JLabel();
        jfavorito = new javax.swing.JLabel();
        jselecion = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(150, 150));
        setLayout(null);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/butter/view/resources/bg_blanco_50.png"))); // NOI18N
        jLabel1.setText("PRUEBA");
        jLabel1.setToolTipText("");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        add(jLabel1);
        jLabel1.setBounds(5, 95, 140, 50);

        jLabel4.setBackground(new java.awt.Color(255, 51, 51));
        jLabel4.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("$ 1.250.000 ");
        jLabel4.setOpaque(true);
        add(jLabel4);
        jLabel4.setBounds(75, 0, 80, 15);
        jLabel4.getAccessibleContext().setAccessibleName("");
        jLabel4.getAccessibleContext().setAccessibleDescription("");

        jnuevo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jnuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/butter/view/resources/new.png"))); // NOI18N
        jnuevo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        add(jnuevo);
        jnuevo.setBounds(32, 0, 30, 32);

        jfavorito.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jfavorito.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/butter/view/resources/favorito.png"))); // NOI18N
        jfavorito.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        add(jfavorito);
        jfavorito.setBounds(0, 0, 26, 32);

        jselecion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/butter/view/resources/tick.png"))); // NOI18N
        add(jselecion);
        jselecion.setBounds(95, 30, 52, 50);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/food.png"))); // NOI18N
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel2MouseReleased(evt);
            }
        });
        add(jLabel2);
        jLabel2.setBounds(0, 0, 150, 150);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseReleased
        selectMe();
    }//GEN-LAST:event_jLabel2MouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jfavorito;
    private javax.swing.JLabel jnuevo;
    private javax.swing.JLabel jselecion;
    // End of variables declaration//GEN-END:variables

    public void selectMe() {
        seleccion = !seleccion;
        if (seleccion) {
            setBackground(Color.green);
        } else {
            setBackground(Color.gray);
        }
        jselecion.setVisible(seleccion);
        //pedido.agregarCanastilla(producto);
    }

}
