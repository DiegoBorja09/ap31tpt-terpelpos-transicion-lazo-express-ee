/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.firefuel;

import com.bean.VentaGo;
import com.controllers.NovusUtils;
import com.google.gson.JsonObject;
import javax.swing.Icon;
import javax.swing.JPanel;

/**
 *
 * @author Devitech
 */
public class ItemMovimientoGopass extends javax.swing.JPanel {

    boolean isSelected;
    GoPassMenuController panel;
    VentaGo venta;

    private final Icon MOV_DEFAULT = new javax.swing.ImageIcon(
            getClass().getResource("/com/firefuel/resources/MovimientoGopass_large.png"));
    private final Icon MOV_SELECT = new javax.swing.ImageIcon(
            getClass().getResource("/com/firefuel/resources/movimientoGopass_bordered.png"));

    public ItemMovimientoGopass(GoPassMenuController panel, boolean isSelected, VentaGo venta) {
        this.panel = panel;
        this.isSelected = isSelected;
        this.venta = venta;
        initComponents();
        init();
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
        if (isSelected) {
            panel.ItemSelected = this;
            panel.selectedVenta = venta;
            panel.estadoEnvioPago();
            jLabel1.setIcon(MOV_SELECT);
        } else {
            panel.estadoEnvioPago();
            panel.ItemSelected = null;
            jLabel1.setIcon(MOV_DEFAULT);
        }
    }

    private void init() {
        JsonObject atributos = venta.getAtributos();
        factura.setText(venta.getPrefijo() + "-" + venta.getConsecutivo());
        nombre_pro.setText(atributos.get("responsables_nombre").getAsString());
        desc_producto.setText(venta.getDescription());
        manguera.setText(atributos.get("manguera").getAsString());
        cantidad.setText("" + venta.getCantidad() + " " + "GL");
        precio.setText("" + venta.getPrecio_producto());
        hora.setText(venta.getFecha());
        total.setText("" + venta.getVentaTotal());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        factura = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        manguera = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        hora = new javax.swing.JLabel();
        nombre_pro = new javax.swing.JLabel();
        desc_producto = new javax.swing.JLabel();
        cantidad = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        total = new javax.swing.JLabel();
        precio = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setOpaque(false);
        setLayout(null);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(186, 12, 47));
        jLabel2.setText("FACTURA");
        add(jLabel2);
        jLabel2.setBounds(30, 10, 100, 40);

        factura.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        factura.setForeground(new java.awt.Color(153, 153, 153));
        factura.setText("NRO. RECIBO");
        add(factura);
        factura.setBounds(160, 10, 670, 40);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(186, 12, 47));
        jLabel4.setText("PROMOTOR");
        add(jLabel4);
        jLabel4.setBounds(30, 60, 120, 30);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(186, 12, 47));
        jLabel5.setText("PRODUCTO");
        add(jLabel5);
        jLabel5.setBounds(30, 100, 120, 30);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(186, 12, 47));
        jLabel6.setText("CARA");
        add(jLabel6);
        jLabel6.setBounds(30, 150, 120, 30);

        manguera.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        manguera.setForeground(new java.awt.Color(153, 153, 153));
        manguera.setText("NUMERO");
        add(manguera);
        manguera.setBounds(160, 150, 100, 30);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(186, 12, 47));
        jLabel8.setText("CANTIDAD");
        add(jLabel8);
        jLabel8.setBounds(320, 150, 120, 30);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(186, 12, 47));
        jLabel9.setText("PRECIO");
        add(jLabel9);
        jLabel9.setBounds(610, 150, 80, 30);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(186, 12, 47));
        jLabel11.setText("HORA");
        add(jLabel11);
        jLabel11.setBounds(930, 20, 100, 30);

        hora.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        hora.setForeground(new java.awt.Color(153, 153, 153));
        hora.setText("FECHA/HORA");
        add(hora);
        hora.setBounds(930, 50, 250, 35);

        nombre_pro.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        nombre_pro.setForeground(new java.awt.Color(153, 153, 153));
        nombre_pro.setText("NOMBRE PROMOTOR");
        add(nombre_pro);
        nombre_pro.setBounds(160, 60, 690, 30);

        desc_producto.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        desc_producto.setForeground(new java.awt.Color(153, 153, 153));
        desc_producto.setText("DESCRIPCION PRODUCTO");
        add(desc_producto);
        desc_producto.setBounds(160, 100, 720, 30);

        cantidad.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        cantidad.setForeground(new java.awt.Color(153, 153, 153));
        cantidad.setText("CANTIDAD");
        add(cantidad);
        cantidad.setBounds(440, 150, 150, 30);

        jLabel10.setFont(new java.awt.Font("Arial", 1, 28)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(186, 47, 12));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("TOTAL");
        add(jLabel10);
        jLabel10.setBounds(1040, 100, 120, 40);

        total.setFont(new java.awt.Font("Arial", 1, 32)); // NOI18N
        total.setForeground(new java.awt.Color(255, 51, 51));
        total.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        total.setText("$ 0");
        add(total);
        total.setBounds(920, 140, 240, 40);

        precio.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        precio.setForeground(new java.awt.Color(153, 153, 153));
        precio.setText("PRECIO");
        add(precio);
        precio.setBounds(710, 150, 200, 30);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/MovimientoGopass_large.png"))); // NOI18N
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel1MouseReleased(evt);
            }
        });
        add(jLabel1);
        jLabel1.setBounds(0, 0, 1190, 200);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(null);

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(186, 12, 47));
        jLabel18.setText("RECIBO");
        jPanel1.add(jLabel18);
        jLabel18.setBounds(28, 30, 100, 30);

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(153, 153, 153));
        jLabel19.setText("NRORECIBO");
        jPanel1.add(jLabel19);
        jLabel19.setBounds(160, 20, 150, 40);

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(186, 12, 47));
        jLabel20.setText("PROMOTOR");
        jPanel1.add(jLabel20);
        jLabel20.setBounds(28, 80, 120, 30);

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(186, 12, 47));
        jLabel21.setText("PRODUCTO");
        jPanel1.add(jLabel21);
        jLabel21.setBounds(28, 130, 120, 30);

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(186, 12, 47));
        jLabel22.setText("MANGUERA");
        jPanel1.add(jLabel22);
        jLabel22.setBounds(30, 190, 120, 30);

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(153, 153, 153));
        jLabel23.setText("NUMERO");
        jPanel1.add(jLabel23);
        jLabel23.setBounds(160, 187, 100, 35);

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(186, 12, 47));
        jLabel24.setText("CANTIDAD");
        jPanel1.add(jLabel24);
        jLabel24.setBounds(270, 190, 120, 30);

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(186, 12, 47));
        jLabel25.setText("TOTAL");
        jPanel1.add(jLabel25);
        jLabel25.setBounds(820, 195, 70, 22);

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(186, 12, 47));
        jLabel26.setText("PRECIO");
        jPanel1.add(jLabel26);
        jLabel26.setBounds(510, 190, 80, 30);

        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(186, 12, 47));
        jLabel27.setText("HORA");
        jPanel1.add(jLabel27);
        jLabel27.setBounds(820, 40, 100, 30);

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(153, 153, 153));
        jLabel28.setText("FECHA/HORA");
        jPanel1.add(jLabel28);
        jLabel28.setBounds(820, 80, 220, 35);

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(153, 153, 153));
        jLabel29.setText("NOMBRE PROMOTOR");
        jPanel1.add(jLabel29);
        jLabel29.setBounds(160, 77, 600, 35);

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(153, 153, 153));
        jLabel30.setText("DESCRIPCION PRODUCTO");
        jPanel1.add(jLabel30);
        jLabel30.setBounds(160, 128, 600, 35);

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(153, 153, 153));
        jLabel31.setText("CANTIDAD");
        jPanel1.add(jLabel31);
        jLabel31.setBounds(385, 187, 96, 35);

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(153, 153, 153));
        jLabel32.setText("PRECIO");
        jPanel1.add(jLabel32);
        jLabel32.setBounds(600, 187, 200, 35);

        jLabel33.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(153, 153, 153));
        jLabel33.setText("PRECIO TOTAL");
        jPanel1.add(jLabel33);
        jLabel33.setBounds(900, 187, 200, 35);

        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/MovimientoGopass_large.png"))); // NOI18N
        jLabel34.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jPanel1.add(jLabel34);
        jLabel34.setBounds(0, 0, 1050, 240);

        add(jPanel1);
        jPanel1.setBounds(0, 0, 0, 0);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel1MouseReleased(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jLabel1MouseReleased
        SelectMov();
    }// GEN-LAST:event_jLabel1MouseReleased

    public void SelectMov() {
        NovusUtils.beep();
        if (panel.ItemSelected != null) {
            panel.ItemSelected.setSelected(false);
        }
        this.setSelected(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel cantidad;
    private javax.swing.JLabel desc_producto;
    private javax.swing.JLabel factura;
    private javax.swing.JLabel hora;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
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
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel manguera;
    private javax.swing.JLabel nombre_pro;
    private javax.swing.JLabel precio;
    private javax.swing.JLabel total;
    // End of variables declaration//GEN-END:variables
}
