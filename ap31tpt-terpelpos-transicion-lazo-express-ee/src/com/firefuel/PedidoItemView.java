/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firefuel;

import com.bean.MovimientosDetallesBean;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JScrollPane;

/**
 *
 * @author novus
 */
public class PedidoItemView extends javax.swing.JPanel {

    private final javax.swing.JScrollPane jScrollPane;
    private final String COLOR_SELECCION = "#FFFB00";

    int posAnt = 0;
    boolean selection = false;
    boolean selectionCantidad = false;
    MovimientosDetallesBean producto;
    TreeMap<Long, MovimientosDetallesBean> seleccionado;

    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);
    DecimalFormat dfCant = new DecimalFormat("#.##");

    StoreViewController pedido = null;
    KCOViewController pedidoKCO = null;

    PedidoItemView(MovimientosDetallesBean producto, JScrollPane jScrollPane,
            TreeMap<Long, MovimientosDetallesBean> seleccionado, StoreViewController pedido) {
        this.pedido = pedido;
        this.jScrollPane = jScrollPane;
        this.seleccionado = seleccionado;
        this.producto = producto;
        initComponents();
        this.init();
    }

    PedidoItemView(MovimientosDetallesBean producto, JScrollPane jScrollPane,
            TreeMap<Long, MovimientosDetallesBean> seleccionado, KCOViewController pedido) {
        this.pedidoKCO = pedido;
        this.jScrollPane = jScrollPane;
        this.seleccionado = seleccionado;
        this.producto = producto;
        initComponents();
        this.init();
    }

    void init() {
        NovusUtils.ajusteFuente(this.getComponents(), NovusConstante.EXTRABOLD);
        jnombre.setText(producto.getDescripcion());
        jcantidad.setText("" + Utils.fmt(producto.getCantidadUnidad()));
        float totalProducto = producto.getPrecio() * producto.getCantidadUnidad();
        jtotal.setText("$ " + df.format(totalProducto));
        jpreciunitario.setText("<html>PLU: <b>" + producto.getPlu() + "</b> - P/U <b>$ "
                + df.format(producto.getPrecio()) + "</b></html>");
    }

    public void updateProductoView(MovimientosDetallesBean producto) {
        jnombre.setText(producto.getDescripcion());
        jcantidad.setText("" + Utils.fmt(producto.getCantidadUnidad()));
        float totalProducto = producto.getPrecio() * producto.getCantidadUnidad();
        jtotal.setText("$ " + df.format(totalProducto));
        jpreciunitario.setText("<html>PLU: <b>" + producto.getPlu() + "</b> - P/U <b>$ "
                + df.format(producto.getPrecio()) + "</b></html>");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jtotal = new javax.swing.JLabel();
        jnombre = new javax.swing.JLabel();
        jcantidad = new javax.swing.JLabel();
        jpreciunitario = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });
        setLayout(null);

        jtotal.setBackground(new java.awt.Color(255, 255, 255));
        jtotal.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jtotal.setForeground(new java.awt.Color(186, 12, 47));
        jtotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jtotal.setText("$ 2.250.000");
        jtotal.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jtotalMouseDragged(evt);
            }
        });
        jtotal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jtotalMousePressed(evt);
            }
        });
        add(jtotal);
        jtotal.setBounds(400, 0, 160, 40);

        jnombre.setBackground(new java.awt.Color(255, 255, 255));
        jnombre.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jnombre.setText("COCACOLA");
        jnombre.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jnombreMouseDragged(evt);
            }
        });
        jnombre.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jnombreMousePressed(evt);
            }
        });
        add(jnombre);
        jnombre.setBounds(10, 0, 300, 25);

        jcantidad.setBackground(new java.awt.Color(204, 255, 255));
        jcantidad.setFont(new java.awt.Font("Roboto", 1, 16)); // NOI18N
        jcantidad.setForeground(new java.awt.Color(186, 12, 47));
        jcantidad.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jcantidad.setText("2");
        jcantidad.setOpaque(true);
        jcantidad.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jcantidadMousePressed(evt);
            }
        });
        add(jcantidad);
        jcantidad.setBounds(320, 0, 70, 40);

        jpreciunitario.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jpreciunitario.setForeground(new java.awt.Color(153, 153, 153));
        jpreciunitario.setText("Precio unitario");
        jpreciunitario.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jpreciunitarioMousePressed(evt);
            }
        });
        add(jpreciunitario);
        jpreciunitario.setBounds(10, 25, 270, 15);

        jLabel4.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        add(jLabel4);
        jLabel4.setBounds(0, 0, 580, 40);
    }// </editor-fold>//GEN-END:initComponents

    private void jnombreMouseDragged(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jnombreMouseDragged

        // TODO add your handling code here:
    }// GEN-LAST:event_jnombreMouseDragged

    private void formMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_formMousePressed
        // selectMe(evt);
    }// GEN-LAST:event_formMousePressed

    private void jnombreMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jnombreMousePressed
        selectMe(evt);
    }// GEN-LAST:event_jnombreMousePressed

    private void jcantidadMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jcantidadMousePressed
        selectMeToCantidad(evt);
    }// GEN-LAST:event_jcantidadMousePressed

    private void jtotalMouseDragged(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jtotalMouseDragged
        mouseMoving(evt);
        // TODO add your handling code here:
    }// GEN-LAST:event_jtotalMouseDragged

    private void jpreciunitarioMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jpreciunitarioMousePressed
        selectMe(evt);
    }// GEN-LAST:event_jpreciunitarioMousePressed

    private void jtotalMousePressed(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jtotalMousePressed
        selectMeToDinero(evt);
    }// GEN-LAST:event_jtotalMousePressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jcantidad;
    private javax.swing.JLabel jnombre;
    private javax.swing.JLabel jpreciunitario;
    private javax.swing.JLabel jtotal;
    // End of variables declaration//GEN-END:variables

    private void mouseMoving(java.awt.event.MouseEvent evt) {
        int h = evt.getY();
        if (h > 0) {
            jScrollPane.getVerticalScrollBar().setValue(jScrollPane.getVerticalScrollBar().getValue() - 40);
        } else {
            jScrollPane.getVerticalScrollBar().setValue(jScrollPane.getVerticalScrollBar().getValue() + 40);
        }
    }

    private void selectMe(MouseEvent evt) {
        if (pedido != null) {
            selection = !selection;
            if (selection) {
                seleccionado.put(producto.getId(), producto);
                setBackground(Color.decode(COLOR_SELECCION));
                pedido.showMensajes("¿BORRAR DE LA LISTA?", "notaBorrar.png");
                pedido.mostrarPaneTeclado();
            } else {
                seleccionado.remove(producto.getId());
                setBackground(Color.white);
                pedido.showMensajes(null, null);
            }
            pedido.resetEntries();
            if (!seleccionado.isEmpty()) {
                for (Map.Entry<Long, MovimientosDetallesBean> entry : seleccionado.entrySet()) {
                    Long key = entry.getKey();
                    MovimientosDetallesBean value = entry.getValue();
                    NovusUtils.printLn("SE SELECCIONO EL PRODUCTO " + value.getDescripcion());
                }
            } else {
                pedido.cerrarTeclado();
            }
        } else {
            selection = !selection;
            if (selection) {
                seleccionado.put(producto.getId(), producto);
                setBackground(Color.decode(COLOR_SELECCION));
                pedidoKCO.showMensajes("¿BORRAR DE LA LISTA?", "notaBorrar.png");
                pedidoKCO.mostrarPanelTecladoBusqueda(false);
            } else {
                seleccionado.remove(producto.getId());
                setBackground(Color.white);
                pedidoKCO.showMensajes(null, null);
            }
            pedidoKCO.resetEntries();
            if (!seleccionado.isEmpty()) {
                for (Map.Entry<Long, MovimientosDetallesBean> entry : seleccionado.entrySet()) {
                    Long key = entry.getKey();
                    MovimientosDetallesBean value = entry.getValue();
                    NovusUtils.printLn("SE SELECCIONO EL PRODUCTO " + value.getDescripcion());
                }
            } else {
                pedidoKCO.cerrarTeclado();
            }
        }
    }

    private void selectMeToCantidad(MouseEvent evt) {
        boolean productoCombustibleDespachado = producto
                .getTipo() == NovusConstante.PRODUCTO_DESPACHO_COMBUSTIBLE_VENTA_MIXTA;
        boolean productoCombustibleManual = producto.getTipo() == NovusConstante.PRODUCTOS_TIPOS_COMBUSTIBLE;
        if (!productoCombustibleDespachado) {
            if (pedido != null) {
                if (StoreViewController.seleccion == null) {
                    if (productoCombustibleManual) {
                        setBackground(Color.decode("#42eef4"));
                    } else {
                        setBackground(Color.ORANGE);
                        setBackground(Color.decode("#f2b200"));
                    }
                    StoreViewController.seleccion = producto;
                } else {
                    StoreViewController.seleccion.getPanelView().setBackground(Color.white);
                    StoreViewController.seleccion = producto;
                    setBackground(Color.decode("#42eef4"));
                }

                if (productoCombustibleManual) {
                    pedido.showMensajes("CANTIDAD A VENDER", "notaCantidadGalon.png");
                } else {
                    setBackground(Color.decode("#f2b200"));
                    pedido.showMensajes("CANTIDAD A VENDER", "notaCantidadProduct.png");
                }
                pedido.mostrarPaneTeclado();
                pedido.changeEntry("Ingrese cantidad: ", "");
            } else {
                if (KCOViewController.seleccion == null) {
                    if (productoCombustibleManual) {
                        setBackground(Color.decode("#42eef4"));
                    } else {
                        setBackground(Color.ORANGE);
                        setBackground(Color.decode("#f2b200"));
                    }
                    KCOViewController.seleccion = producto;
                } else {
                    KCOViewController.seleccion.getPanelView().setBackground(Color.white);
                    KCOViewController.seleccion = producto;
                    setBackground(Color.decode("#42eef4"));
                }

                if (productoCombustibleManual) {
                    pedidoKCO.showMensajes("CANTIDAD A VENDER", "notaCantidadGalon.png");
                } else {
                    setBackground(Color.decode("#f2b200"));
                    pedidoKCO.showMensajes("CANTIDAD A VENDER", "notaCantidadProduct.png");
                }
                pedidoKCO.mostrarPanelTecladoBusqueda(false);
                pedidoKCO.opcionesPanelProductos(false);

                boolean productoGranel = producto.getUnidades_medida().equalsIgnoreCase("GRANEL");
                pedidoKCO.mostrarPaneTeclado(productoGranel);
                pedidoKCO.iconosIngresarCantidad();
                pedidoKCO.changeEntry("Ingrese cantidad: ", "");

            }
        }
    }

    private void selectMeToDinero(MouseEvent evt) {
        if (pedido != null) {
            if (producto.getTipo() == NovusConstante.PRODUCTOS_TIPOS_COMBUSTIBLE) {
                if (StoreViewController.seleccion == null) {
                    setBackground(Color.decode("#3AFF00"));
                    StoreViewController.seleccion = producto;
                } else {
                    StoreViewController.seleccion.getPanelView().setBackground(Color.white);
                    StoreViewController.seleccion = producto;
                    setBackground(Color.decode("#3AFF00"));
                }
                pedido.showMensajes("CANTIDAD PRECIO $", "notaPrecioProd.png");
                pedido.mostrarPaneTeclado();
                pedido.changeEntry("Ingrese Precio: ", "");

            }
        } else {
            if (producto.getTipo() == NovusConstante.PRODUCTOS_TIPOS_COMBUSTIBLE) {
                if (KCOViewController.seleccion == null) {
                    setBackground(Color.decode("#3AFF00"));
                    StoreViewController.seleccion = producto;
                } else {
                    KCOViewController.seleccion.getPanelView().setBackground(Color.white);
                    KCOViewController.seleccion = producto;
                    setBackground(Color.decode("#3AFF00"));
                }
                boolean productoGranel = producto.getUnidades_medida().equalsIgnoreCase("GRANEL");
                pedidoKCO.mostrarPaneTeclado(productoGranel);
                pedidoKCO.changeEntry("Ingrese Precio: ", "");
            }
        }
    }
}
