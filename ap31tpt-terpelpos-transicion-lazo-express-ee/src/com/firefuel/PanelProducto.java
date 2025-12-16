package com.firefuel;

import com.bean.MovimientosDetallesBean;

public class PanelProducto extends javax.swing.JPanel {

    KCOViewController pedidoKCO;
    MovimientosDetallesBean producto;

    public PanelProducto(MovimientosDetallesBean producto, KCOViewController pedido) {
        initComponents();
        this.producto = producto;
        this.pedidoKCO = pedido;
        this.init();
    }

    public void init() {
        lblNombreProducto.setText("   " + producto.getDescripcion().trim());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblNombreProducto = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setMaximumSize(new java.awt.Dimension(1212, 54));
        setMinimumSize(new java.awt.Dimension(1212, 54));
        setPreferredSize(new java.awt.Dimension(1212, 54));
        setLayout(null);

        lblNombreProducto.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblNombreProducto.setText("  NOMBRE PRODUCTO");
        lblNombreProducto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblNombreProductoMouseReleased(evt);
            }
        });
        add(lblNombreProducto);
        lblNombreProducto.setBounds(0, 0, 1212, 54);
    }// </editor-fold>//GEN-END:initComponents

    private void lblNombreProductoMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblNombreProductoMouseReleased
        System.out.println("PLU-- " + producto.getPlu());
        pedidoKCO.agregarProducto("TECLADO", producto.getPlu(), null);
        pedidoKCO.mostrarPanelTecladoBusqueda(false);
    }//GEN-LAST:event_lblNombreProductoMouseReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblNombreProducto;
    // End of variables declaration//GEN-END:variables
}
