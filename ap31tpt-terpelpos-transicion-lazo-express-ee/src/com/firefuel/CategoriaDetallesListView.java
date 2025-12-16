/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firefuel;

import com.bean.CategoriaBean;
import com.bean.MovimientosDetallesBean;
import com.bean.ProductoBean;
import com.dao.CategoriasDao;
import com.dao.DAOException;
import com.dao.MovimientosDao;
import com.application.useCases.productos.GetProductosPromocionUseCase;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author novus
 */
public class CategoriaDetallesListView extends javax.swing.JPanel {

    StoreViewController pedido;
    KCOViewController pedidoKCO;

    CategoriaDetallesListView(CategoriaBean categoria, KCOViewController pedido) {
        try {
            this.pedidoKCO = pedido;
            initComponents();
            //jLabel3.setIcon(Main.fondoNegro); // NOI18N

            if (categoria != null) {
                jLabel2.setText(categoria.getGrupo());
            } else {
                jLabel2.setText("PRODUCTOS EN PROMOCION");
            }

            int componentesX = 0;
            int componentesY = 0;

            ArrayList<MovimientosDetallesBean> productos;
            if (null == categoria) {
                GetProductosPromocionUseCase useCase = new GetProductosPromocionUseCase();
                List<ProductoBean> productosPromocion = useCase.execute();
                productos = convertirProductosAMovimientos(productosPromocion);
            } else {
                MovimientosDao mdao = new MovimientosDao();
                productos = mdao.findByCategoria(categoria);
            }

            jPanel1.setPreferredSize(new Dimension((int) jPanel1.getPreferredSize().getWidth(), 0));
            int ancho = 150;
            int alto = 150;
            int i = 0;
            for (MovimientosDetallesBean producto : productos) {
                if (i == 4) {
                    producto.setNuevo(true);
                }
                if (i == 10) {
                    producto.setNuevo(true);
                    producto.setFavorito(true);
                }
                if (i == 15) {
                    producto.setFavorito(true);
                }
                CategoriaProductoItemView jitem = new CategoriaProductoItemView(pedidoKCO, producto);
                if (componentesX == 6) {
                    componentesX = 0;
                    componentesY++;
                    jPanel1.setPreferredSize(new java.awt.Dimension(jPanel1.getWidth(), jPanel1.getHeight() + alto + 5));
                    jPanel1.setBounds(0, 0, jPanel1.getWidth(), jPanel1.getHeight() + alto + 5);
                }
                jPanel1.add(jitem);
                jitem.setBounds(20 + componentesX * (ancho + 10), componentesY * (alto + 10), ancho, alto);
                componentesX++;
                i++;
            }
            jPanel1.setPreferredSize(new java.awt.Dimension(jPanel1.getWidth(), jPanel1.getHeight() + alto + 5));
            jScrollPane1.getVerticalScrollBar().setPreferredSize(new Dimension(25, 0));
            jPanel1.revalidate();
            jScrollPane1.revalidate();
            jPanel1.updateUI();
        } catch (DAOException ex) {
            Logger.getLogger(CategoriaDetallesListView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    CategoriaDetallesListView(CategoriaBean categoria, StoreViewController pedido) {
        try {
            this.pedido = pedido;
            initComponents();
            //jLabel3.setIcon(Main.fondoNegro); // NOI18N

            if (categoria != null) {
                jLabel2.setText(categoria.getGrupo());
            } else {
                jLabel2.setText("PRODUCTOS EN PROMOCION");
            }

            int componentesX = 0;
            int componentesY = 0;

            ArrayList<MovimientosDetallesBean> productos;
            if (null == categoria) {
                GetProductosPromocionUseCase useCase = new GetProductosPromocionUseCase();
                List<ProductoBean> productosPromocion = useCase.execute();
                productos = convertirProductosAMovimientos(productosPromocion);
            } else {
                MovimientosDao mdao = new MovimientosDao();
                productos = mdao.findByCategoria(categoria);
            }

            jPanel1.setPreferredSize(new Dimension((int) jPanel1.getPreferredSize().getWidth(), 0));
            int ancho = 150;
            int alto = 150;
            int i = 0;
            for (MovimientosDetallesBean producto : productos) {
                if (i == 4) {
                    producto.setNuevo(true);
                }
                if (i == 10) {
                    producto.setNuevo(true);
                    producto.setFavorito(true);
                }
                if (i == 15) {
                    producto.setFavorito(true);
                }
                CategoriaProductoItemView jitem = new CategoriaProductoItemView(pedido, producto);
                if (componentesX == 6) {
                    componentesX = 0;
                    componentesY++;
                    jPanel1.setPreferredSize(new java.awt.Dimension(jPanel1.getWidth(), jPanel1.getHeight() + alto + 5));
                    jPanel1.setBounds(0, 0, jPanel1.getWidth(), jPanel1.getHeight() + alto + 5);
                }
                jPanel1.add(jitem);
                jitem.setBounds(20 + componentesX * (ancho + 10), componentesY * (alto + 10), ancho, alto);
                componentesX++;
                i++;
            }
            jPanel1.setPreferredSize(new java.awt.Dimension(jPanel1.getWidth(), jPanel1.getHeight() + alto + 5));
            jScrollPane1.getVerticalScrollBar().setPreferredSize(new Dimension(25, 0));
            jPanel1.revalidate();
            jScrollPane1.revalidate();
            jPanel1.updateUI();
        } catch (DAOException ex) {
            Logger.getLogger(CategoriaDetallesListView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setOpaque(false);
        setLayout(null);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setAutoscrolls(true);

        jPanel1.setPreferredSize(new java.awt.Dimension(520, 10));
        jPanel1.setLayout(null);
        jScrollPane1.setViewportView(jPanel1);

        add(jScrollPane1);
        jScrollPane1.setBounds(10, 50, 1010, 460);

        jLabel2.setFont(new java.awt.Font("Roboto", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("NOMBRE DE LA CATEGORIA");
        add(jLabel2);
        jLabel2.setBounds(10, 10, 740, 30);

        jLabel4.setFont(new java.awt.Font("Juicebox", 1, 26)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/butter/view/resources/btn_success.png"))); // NOI18N
        jLabel4.setText("ACEPTAR");
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel4MouseReleased(evt);
            }
        });
        add(jLabel4);
        jLabel4.setBounds(620, 510, 180, 80);

        jLabel5.setFont(new java.awt.Font("Juicebox", 1, 26)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/butter/view/resources/btn_warning.png"))); // NOI18N
        jLabel5.setText("REGRESAR");
        jLabel5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabel5MouseReleased(evt);
            }
        });
        add(jLabel5);
        jLabel5.setBounds(235, 515, 180, 80);
        add(jLabel3);
        jLabel3.setBounds(0, 0, 1500, 700);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel5MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseReleased
        ocultarme();
    }//GEN-LAST:event_jLabel5MouseReleased

    private void jLabel4MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseReleased
        selectAllProdcut();
    }//GEN-LAST:event_jLabel4MouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    private void ocultarme() {
        this.setVisible(false);
        if (pedido != null) {
            pedido.restoreView();

        } else {
            pedidoKCO.restoreView();

        }
    }

    private void selectAllProdcut() {
        this.setVisible(false);
        // pedido.refrescarLista();
        if (pedido != null) {
            pedido.restoreView();
        } else {
            pedidoKCO.restoreView();
        }
    }

    /**
     * Convierte una lista de ProductoBean a MovimientosDetallesBean
     * para mantener compatibilidad con la interfaz existente.
     */
    private ArrayList<MovimientosDetallesBean> convertirProductosAMovimientos(List<ProductoBean> productos) {
        ArrayList<MovimientosDetallesBean> movimientos = new ArrayList<>();
        for (ProductoBean producto : productos) {
            MovimientosDetallesBean movimiento = new MovimientosDetallesBean();
            movimiento.setId(producto.getId());
            movimiento.setPlu(producto.getPlu());
            movimiento.setDescripcion(producto.getDescripcion());
            movimiento.setPrecio(producto.getPrecio());
            movimiento.setTipo(producto.getTipo());
            movimiento.setSaldo(producto.getSaldo());
            movimiento.setCosto(producto.getCosto());
            movimiento.setFavorito(producto.isFavorito());
            movimientos.add(movimiento);
        }
        return movimientos;
    }
}
