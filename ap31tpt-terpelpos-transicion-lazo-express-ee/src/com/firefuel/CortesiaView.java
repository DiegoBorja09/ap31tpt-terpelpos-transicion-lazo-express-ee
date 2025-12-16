package com.firefuel;

import com.bean.MediosPagosBean;
import com.bean.MovimientosBean;
import com.bean.MovimientosDetallesBean;
import com.controllers.NovusConstante;
import com.firefuel.facturacion.electronica.CortesiaFE;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author danie
 */
public class CortesiaView extends javax.swing.JDialog {

    JDialog parent;
    static JsonObject respuestaFactura;
    public static TreeMap<Long, CategoriaProductoListItem2> productosVenta = new TreeMap<>();
    public static TreeMap<Long, CategoriaProductoListItemCortesia> productosCortesia = new TreeMap<>();
    static JsonArray array;
    static float totalOtros = 0;
    static float totalCortesia = 0;
    static DecimalFormat df;
    static StoreConfirmarViewController store;
    public static float ventaTotal;
    MovimientosBean movimiento;
    static int cantidad;

    public CortesiaView(JDialog parent, boolean modal, JsonObject respuestaFactura) {
        super(parent, modal);
        this.parent = parent;
        store = (StoreConfirmarViewController) parent;
        CortesiaView.respuestaFactura = respuestaFactura;
        initComponents();
        setLocationRelativeTo(null);
        listarProductos(listaProductos);
    }

    public CortesiaView(JDialog parent, boolean modal, JsonObject respuestaFactura, MovimientosBean movimiento) {
        super(parent, modal);
        this.parent = parent;
        this.movimiento = movimiento;
        store = (StoreConfirmarViewController) parent;
        CortesiaView.respuestaFactura = respuestaFactura;
        initComponents();
        setLocationRelativeTo(null);
        listarProductos(listaProductos);
    }

    public static JsonObject getProducto(long identificadorProducto) {
        JsonObject producto = null;
        cantidad = 0;
        for (JsonElement jsonElement : respuestaFactura.get("datos_FE").getAsJsonObject().get("detallesVenta").getAsJsonArray()) {
            JsonObject obj = jsonElement.getAsJsonObject();

            if (obj.get("identificadorProducto").getAsLong() == identificadorProducto) {
                producto = obj;
                cantidad = obj.get("cantidadVenta").getAsInt();
                break;
            }
        }
        return producto;
    }

    public static void actualizarPagos() {
        JsonArray pagos = new JsonArray();
        for (MediosPagosBean mediosPagosBean : store.mediosPagoVenta) {
            JsonObject medio = new JsonObject();
            medio.addProperty("descripcionMedioPago", mediosPagosBean.getDescripcion());
            medio.addProperty("identificacionMediosPagos", mediosPagosBean.getId());
            medio.addProperty("vueltoMedioPago", mediosPagosBean.getCambio());
            medio.addProperty("identificacionComprobante", "");
            medio.addProperty("monedaLocal", "S");
            medio.addProperty("trm", 0);
            medio.addProperty("recibidoMedioPago", mediosPagosBean.getRecibido());
            medio.addProperty("totalMedioPago", mediosPagosBean.getValor());
            pagos.add(medio);
        }
        respuestaFactura.get("datos_FE").getAsJsonObject().remove("pagos");
        respuestaFactura.get("datos_FE").getAsJsonObject().add("pagos", pagos);
    }

    public static float getPrecioProducto(long identificadorProducto) {
        return getProducto(identificadorProducto).get("precioProducto").getAsFloat();
    }

    public static float getImpTotalProducto(long identificadorProducto) {
        float impuestoTotalAplicado = 0;
        JsonObject producto = getProducto(identificadorProducto);
        JsonArray impuestosProducto = producto.getAsJsonArray("impuestosAplicados");
        for (JsonElement jsonElement : impuestosProducto) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            impuestoTotalAplicado += jsonObject.get("valorImpuestoAplicado").getAsFloat();
        }
        return impuestoTotalAplicado / getProducto(identificadorProducto).get("cantidadVenta").getAsFloat();
    }

    public static void listarProductos(JPanel panelProducto) {
        df = new DecimalFormat(NovusConstante.FORMAT_MONEY);
        for (JsonElement jsonElement : respuestaFactura.get("datos_FE").getAsJsonObject().get("detallesVenta").getAsJsonArray()) {
            JsonObject producto = jsonElement.getAsJsonObject();

            float cantidadVenta = producto.get("cantidadVenta").getAsFloat();
            float impuesto = getImpTotalProducto(producto.get("identificadorProducto").getAsLong());
            float total = producto.get("precioProducto").getAsFloat() + (cantidadVenta * impuesto);

            CategoriaProductoListItem2 categProducto = new CategoriaProductoListItem2(producto.get("identificadorProducto").getAsLong());
            categProducto.jNombre.setText(producto.get("nombreProducto").getAsString());

            categProducto.jPrecio.setText(df.format(total));

            categProducto.jCantidad.setText(df.format(producto.get("cantidadVenta").getAsFloat()));

            categProducto.jPlu.setText(producto.get("identificacionProducto").getAsString());
            totalOtros += total;
            jTotalOtros.setText("$" + df.format(totalOtros));
            productosVenta.put(producto.get("identificadorProducto").getAsLong(), categProducto);
            panelProducto.add(categProducto, producto.get("identificadorProducto").getAsLong());
        }
    }

    static MovimientosDetallesBean findProducto(long identificadorProducto) {
        MovimientosDetallesBean mov;
        for (Map.Entry<Long, MovimientosDetallesBean> entry : store.movimiento.getDetalles().entrySet()) {
            mov = entry.getValue();
            if (mov.getProductoId() == identificadorProducto) {
                return mov;
            }
        }
        return null;
    }

    public static float getTotalProducto(Long identificadorProducto) {
        float cantidadVenta = 0;
        float impuesto = 0;
        float total = 0;

        for (JsonElement jsonElement : respuestaFactura.get("datos_FE").getAsJsonObject().get("detallesVenta").getAsJsonArray()) {
            JsonObject producto = jsonElement.getAsJsonObject();
            if (producto.get("identificadorProducto").getAsLong() == identificadorProducto) {
                cantidadVenta = producto.get("cantidadVenta").getAsFloat();
                impuesto = getImpTotalProducto(producto.get("identificadorProducto").getAsLong());
                total = producto.get("precioProducto").getAsFloat() + (cantidadVenta * impuesto);
            }
        }

        return total;
    }

    public static float getSubTotalProducto(Long identificadorProducto, int cantidad) {
        float cantidadVenta = 0;
        float impuesto = 0;
        float subtotal = 0;
        float total = 0;
        float precio = 0;

        for (JsonElement jsonElement : respuestaFactura.get("datos_FE").getAsJsonObject().get("detallesVenta").getAsJsonArray()) {
            JsonObject producto = jsonElement.getAsJsonObject();
            if (producto.get("identificadorProducto").getAsLong() == identificadorProducto) {
                cantidadVenta = producto.get("cantidadVenta").getAsFloat();
                impuesto = getImpTotalProducto(producto.get("identificadorProducto").getAsLong());
                subtotal = producto.get("precioProducto").getAsFloat() + (cantidadVenta * impuesto);
                total = subtotal / cantidadVenta;
            }
        }

        precio = total * cantidad;

        return precio;
    }

    /*@btn
    1 para agregar los productos a cortesia
    2 para eliminar productos de cortesia*/
    public static void controlarProductosCortesia(Long identificadorProducto, int btn) {

        float valorImpuestoAplicado = getImpTotalProducto(identificadorProducto);
        if (btn == 1) {
            CategoriaProductoListItem2 categoriaProductoListItem2Agregado = productosVenta.get(identificadorProducto);
            CategoriaProductoListItemCortesia categoriaProductoListItemCortesia;

            int cantidadCortesia;
            if (!productosVenta.isEmpty()) {
                if (productosCortesia.containsKey(identificadorProducto)) {
                    categoriaProductoListItemCortesia = productosCortesia.get(identificadorProducto);
                    cantidadCortesia = Integer.parseInt(categoriaProductoListItemCortesia.jCantidad.getText()) + 1;

                    categoriaProductoListItemCortesia.jCantidad.setText(String.valueOf(cantidadCortesia));
                } else {
                    categoriaProductoListItemCortesia = new CategoriaProductoListItemCortesia(identificadorProducto);
                    categoriaProductoListItemCortesia.jPlu.setText(categoriaProductoListItem2Agregado.jPlu.getText());
                    categoriaProductoListItemCortesia.jNombre.setText(categoriaProductoListItem2Agregado.jNombre.getText());
                    cantidadCortesia = 1;

                    categoriaProductoListItemCortesia.jCantidad.setText(String.valueOf(cantidadCortesia));
                    productosCortesia.put(identificadorProducto, categoriaProductoListItemCortesia);
                    listaProductosCortesia.add(categoriaProductoListItemCortesia, identificadorProducto);
                }

                listaProductosCortesia.repaint();
                int cantidadVenta = Integer.parseInt(categoriaProductoListItem2Agregado.jCantidad.getText()) - 1;

                float subtotal = getSubTotalProducto(identificadorProducto, cantidadVenta);
                float subtotalFinal = getSubTotalProducto(identificadorProducto, 1);

                categoriaProductoListItem2Agregado.jCantidad.setText(String.valueOf(cantidadVenta));
                categoriaProductoListItem2Agregado.jPrecio.setText(df.format(subtotal));

                totalOtros -= subtotalFinal;
                totalCortesia += valorImpuestoAplicado;

                if (cantidadVenta == 0) {
                    listaProductos.remove(categoriaProductoListItem2Agregado);
                    productosVenta.remove(identificadorProducto);
                }
                categoriaProductoListItemCortesia.jPrecio.setText(df.format(valorImpuestoAplicado));
                jTotalOtros.setText("$" + df.format(totalOtros));
                jTotalCortesia.setText("$" + df.format(totalCortesia));
                listaProductos.repaint();
            }
        } else {
            if (!productosCortesia.isEmpty()) {
                CategoriaProductoListItemCortesia categoriaProductoListItemCortesiaAgregado = productosCortesia.get(identificadorProducto);
                CategoriaProductoListItem2 categoriaProductoListItem2;

                float precioProducto = getPrecioProducto(identificadorProducto);
                int cantidadVenta;
                if (productosVenta.containsKey(identificadorProducto)) {
                    categoriaProductoListItem2 = productosVenta.get(identificadorProducto);
                    cantidadVenta = Integer.parseInt(categoriaProductoListItem2.jCantidad.getText()) + 1;
                    categoriaProductoListItem2.jCantidad.setText(String.valueOf(cantidadVenta));
                } else {
                    categoriaProductoListItem2 = new CategoriaProductoListItem2(identificadorProducto);
                    categoriaProductoListItem2.jPlu.setText(categoriaProductoListItemCortesiaAgregado.jPlu.getText());
                    categoriaProductoListItem2.jNombre.setText(categoriaProductoListItemCortesiaAgregado.jNombre.getText());
                    cantidadVenta = 1;
                    categoriaProductoListItem2.jCantidad.setText(String.valueOf(cantidadVenta));
                    productosVenta.put(identificadorProducto, categoriaProductoListItem2);
                    listaProductos.add(categoriaProductoListItem2, identificadorProducto);
                }
                
                float subtotalFinal = getSubTotalProducto(identificadorProducto, 1);
                
                totalCortesia -= valorImpuestoAplicado;
                totalOtros += subtotalFinal;

                listaProductos.repaint();
                int cantidadCortesia = Integer.parseInt(categoriaProductoListItemCortesiaAgregado.jCantidad.getText()) - 1;
                categoriaProductoListItemCortesiaAgregado.jCantidad.setText(String.valueOf(cantidadCortesia));
                if (cantidadCortesia == 0) {
                    listaProductosCortesia.remove(categoriaProductoListItemCortesiaAgregado);
                    productosCortesia.remove(identificadorProducto);
                }
                float subtotal = getSubTotalProducto(identificadorProducto, cantidadVenta);
                categoriaProductoListItem2.jPrecio.setText(df.format(subtotal));
                jTotalOtros.setText("$" + df.format(totalOtros));
                jTotalCortesia.setText("$" + df.format(totalCortesia));
                listaProductosCortesia.repaint();
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel27 = new javax.swing.JLabel();
        jTitle = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jguardar = new javax.swing.JLabel();
        jcancelar = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        listaProductos = new javax.swing.JPanel();
        listaProductosCortesia = new javax.swing.JPanel();
        jatras = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jTotalCortesia = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTotalOtros = new javax.swing.JLabel();
        fondo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(1280, 800));
        setMinimumSize(new java.awt.Dimension(1280, 800));
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(1280, 800));
        getContentPane().setLayout(null);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/separadorVertical.png"))); // NOI18N
        getContentPane().add(jLabel27);
        jLabel27.setBounds(90, 10, 10, 68);

        jTitle.setFont(new java.awt.Font("Terpel Sans", 1, 36)); // NOI18N
        jTitle.setForeground(new java.awt.Color(255, 255, 255));
        jTitle.setText("CORTESIAS");
        getContentPane().add(jTitle);
        jTitle.setBounds(120, 15, 720, 50);

        jLabel9.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("PRODUCTOS CON CORTESÍA");
        jLabel9.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        getContentPane().add(jLabel9);
        jLabel9.setBounds(750, 110, 400, 30);

        jLabel5.setFont(new java.awt.Font("Terpel Sans", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("PRODUCTOS");
        jLabel5.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        getContentPane().add(jLabel5);
        jLabel5.setBounds(130, 110, 390, 30);

        jguardar.setFont(new java.awt.Font("Bebas Neue", 0, 24)); // NOI18N
        jguardar.setForeground(new java.awt.Color(255, 255, 255));
        jguardar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jguardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"))); // NOI18N
        jguardar.setText("GUARDAR");
        jguardar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jguardar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jguardarMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jguardarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jguardarMouseReleased(evt);
            }
        });
        getContentPane().add(jguardar);
        jguardar.setBounds(950, 720, 300, 70);

        jcancelar.setFont(new java.awt.Font("Bebas Neue", 0, 24)); // NOI18N
        jcancelar.setForeground(new java.awt.Color(255, 255, 255));
        jcancelar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jcancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt-danger-normal.png"))); // NOI18N
        jcancelar.setText("CANCELAR");
        jcancelar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jcancelar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jcancelarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jcancelarMouseReleased(evt);
            }
        });
        getContentPane().add(jcancelar);
        jcancelar.setBounds(660, 720, 300, 70);

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/logoDevitech_3.png"))); // NOI18N
        getContentPane().add(jLabel6);
        jLabel6.setBounds(0, 710, 110, 90);

        listaProductos.setBackground(new java.awt.Color(204, 204, 204));
        listaProductos.setMaximumSize(new java.awt.Dimension(516, 486));
        listaProductos.setMinimumSize(new java.awt.Dimension(516, 486));
        listaProductos.setOpaque(false);
        listaProductos.setLayout(new javax.swing.BoxLayout(listaProductos, javax.swing.BoxLayout.Y_AXIS));
        getContentPane().add(listaProductos);
        listaProductos.setBounds(60, 150, 540, 470);

        listaProductosCortesia.setBackground(new java.awt.Color(204, 204, 204));
        listaProductosCortesia.setMaximumSize(new java.awt.Dimension(536, 486));
        listaProductosCortesia.setMinimumSize(new java.awt.Dimension(536, 486));
        listaProductosCortesia.setOpaque(false);
        listaProductosCortesia.setLayout(new javax.swing.BoxLayout(listaProductosCortesia, javax.swing.BoxLayout.Y_AXIS));
        getContentPane().add(listaProductosCortesia);
        listaProductosCortesia.setBounds(680, 150, 540, 470);

        jatras.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jatras.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/btn_atras.png"))); // NOI18N
        jatras.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jatrasMouseClicked(evt);
            }
        });
        getContentPane().add(jatras);
        jatras.setBounds(10, 10, 70, 71);

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(153, 153, 153));
        jLabel1.setText("TOTAL:");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(690, 630, 110, 40);

        jTotalCortesia.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        jTotalCortesia.setForeground(new java.awt.Color(153, 153, 153));
        jTotalCortesia.setText("0");
        getContentPane().add(jTotalCortesia);
        jTotalCortesia.setBounds(810, 630, 390, 40);

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(153, 153, 153));
        jLabel3.setText("TOTAL:");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(70, 630, 110, 40);

        jTotalOtros.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        jTotalOtros.setForeground(new java.awt.Color(153, 153, 153));
        jTotalOtros.setText("0");
        getContentPane().add(jTotalOtros);
        jTotalOtros.setBounds(190, 630, 390, 40);

        fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndSincronizacion.png"))); // NOI18N
        getContentPane().add(fondo);
        fondo.setBounds(-3, -6, 1280, 810);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jatrasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jatrasMouseClicked
        limpiarCortesia();
        cerrar();
    }//GEN-LAST:event_jatrasMouseClicked

    private void jcancelarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jcancelarMousePressed
    }//GEN-LAST:event_jcancelarMousePressed

    private void jcancelarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jcancelarMouseReleased
        limpiarCortesia();
        cerrar();
    }//GEN-LAST:event_jcancelarMouseReleased

    private void jguardarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jguardarMousePressed
    }//GEN-LAST:event_jguardarMousePressed

    private void jguardarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jguardarMouseReleased
        DefaultTableModel table = (DefaultTableModel) store.jtableMediosPago.getModel();
        guardarProductosCortesia();
        agregarMedioPago();
        CortesiaFE cortesia = new CortesiaFE();
        JsonObject respuesta = new JsonObject();
//        respuesta = cortesia.jsonCortesia(respuestaFactura);
        respuesta = cortesia.comboCortesia(respuestaFactura);
        store.respuestaFacturaCortesia = respuesta;
        store.existeCortesia = true;
        System.out.println(Main.ANSI_BLUE + respuesta + Main.ANSI_RESET);
        store.setRespuestaCortesia(respuesta);
        limpiarCortesia();
        cerrar();
    }//GEN-LAST:event_jguardarMouseReleased

    private void jguardarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jguardarMouseClicked
    }//GEN-LAST:event_jguardarMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel fondo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jTitle;
    private static javax.swing.JLabel jTotalCortesia;
    private static javax.swing.JLabel jTotalOtros;
    private javax.swing.JLabel jatras;
    private javax.swing.JLabel jcancelar;
    private javax.swing.JLabel jguardar;
    public static javax.swing.JPanel listaProductos;
    public static javax.swing.JPanel listaProductosCortesia;
    // End of variables declaration//GEN-END:variables

    private void cerrar() {
        dispose();
        if (store.existeCortesia()) {
            store.jCortesias.setVisible(false);
            store.jguardarVenta.setVisible(true);
        } else {
            store.jCortesias.setVisible(true);
        }
    }

    private void limpiarCortesia() {
        totalOtros = 0;
        totalCortesia = 0;
        productosVenta.clear();
        listaProductos.removeAll();
        productosCortesia.clear();
        listaProductosCortesia.removeAll();
    }

    private void guardarProductosCortesia() {
        store.cortesia = true;
        float descuentoTotal = 0;
        double totalVenta = 0;
        float totalImpuesto = 0;
        JsonArray detallesVenta = new JsonArray();
        double impuesto = 0;
        double impoconsumo = 0;
        float impTotal = 0;
        for (Map.Entry<Long, CategoriaProductoListItem2> entry
                : productosVenta.entrySet()) {
            long idProducto = entry.getValue().identificadorProducto;
            int cantidadProducto = Integer.parseInt(entry.getValue().jCantidad.getText());
            JsonObject obj = new JsonObject();
            obj.addProperty("idTransaccionVentaDetalle", getProducto(idProducto).get("idTransaccionVentaDetalle").getAsInt());
            obj.addProperty("idTransaccionDetalleVenta", getProducto(idProducto).get("idTransaccionVentaDetalle").getAsInt());
            obj.addProperty("identificadorProducto", idProducto);
            obj.addProperty("nombreProducto", getProducto(idProducto).get("nombreProducto").getAsString()); // CENTRALIZADOR
            obj.addProperty("identificacionProducto", getProducto(idProducto).get("identificacionProducto").getAsString());
            obj.addProperty("fechaTransaccion", getProducto(idProducto).get("fechaTransaccion").getAsString());
            obj.addProperty("cantidadVenta", cantidadProducto);
            obj.addProperty("unidad", getProducto(idProducto).get("unidad").getAsString());
            obj.addProperty("identificadorUnidad", getProducto(idProducto).get("identificadorUnidad").getAsInt());// DERRUMBAR
            obj.addProperty("costoProducto", getProducto(idProducto).get("costoProducto").getAsFloat());
            double precioProducto = getProducto(idProducto).get("precioProducto").getAsDouble();
            precioProducto = precioProducto / getProducto(idProducto).get("cantidadVenta").getAsDouble();
            precioProducto = precioProducto * cantidadProducto;
            obj.addProperty("precioProducto", precioProducto);
            
            double impoconsumoVenta = getProducto(idProducto).get("impoconsumo").getAsDouble();
            impoconsumoVenta = impoconsumoVenta / getProducto(idProducto).get("cantidadVenta").getAsDouble();
            impoconsumoVenta = impoconsumoVenta * cantidadProducto;
            obj.addProperty("impoconsumo", impoconsumoVenta);
            obj.addProperty("precioVentaFeWeb", getProducto(idProducto).get("precioVentaFeWeb").getAsDouble());
            obj.addProperty("identificadorDescuento", getProducto(idProducto).get("identificadorDescuento").getAsInt());
            obj.addProperty("descuentoTotal", getProducto(idProducto).get("descuentoTotal").getAsFloat());
            double subTotal = getProducto(idProducto).get("subTotalVenta").getAsDouble();
            subTotal = subTotal / getProducto(idProducto).get("cantidadVenta").getAsInt();
            subTotal = subTotal * cantidadProducto;
            obj.addProperty("subTotalVenta", subTotal);
            totalVenta += obj.get("subTotalVenta").getAsDouble();
            obj.addProperty("total_cantidad", getProducto(idProducto).get("total_cantidad").getAsFloat());
            obj.addProperty("cortesia", false);
            obj.addProperty("base", getProducto(idProducto).get("base").getAsDouble());
            detallesVenta.add(obj);

            JsonObject jsonAtributos = new JsonObject();
            jsonAtributos.addProperty("categoriaId", getProducto(idProducto).get("atributos").getAsJsonObject().get("categoriaId").getAsLong());
            jsonAtributos.addProperty("categoriaDescripcion", getProducto(idProducto).get("atributos").getAsJsonObject().get("categoriaDescripcion").getAsString());
            jsonAtributos.addProperty("tipo", getProducto(idProducto).get("atributos").getAsJsonObject().get("tipo").getAsLong());
            jsonAtributos.addProperty("isElectronica", "true");
            obj.add("atributos", jsonAtributos);
            JsonArray jsonImpuestos = new JsonArray();
            for (JsonElement elemt : getProducto(idProducto).getAsJsonArray("impuestosAplicados")) {
                JsonObject dataIpm = elemt.getAsJsonObject();
                JsonObject imp = new JsonObject();
                imp.addProperty("identificadorImpuesto", dataIpm.get("identificadorImpuesto").getAsInt());
                imp.addProperty("nombreImpuesto", dataIpm.get("nombreImpuesto").getAsString());
                imp.addProperty("tipoImpuesto", dataIpm.get("tipoImpuesto").getAsString());
                imp.addProperty("valorImpAplicado", dataIpm.get("valorImpAplicado").getAsFloat());
                imp.addProperty("valorImpuestoAplicado", dataIpm.get("valorImpuestoAplicado").getAsFloat());
                if (imp.get("tipoImpuesto").getAsString().equals("$")) {
                    impoconsumo = imp.get("valorImpuestoAplicado").getAsFloat();
                    impoconsumo = impoconsumo / cantidad;
                    impoconsumo = impoconsumo * cantidadProducto;
                    imp.addProperty("valorImpuestoAplicado", impoconsumo);
                } else {
                    impuesto = imp.get("valorImpuestoAplicado").getAsFloat();
                    impuesto = impuesto / cantidad;
                    impuesto = impuesto * cantidadProducto;
                    imp.addProperty("valorImpuestoAplicado", impuesto);
                    impTotal += imp.get("valorImpuestoAplicado").getAsFloat();
                }
                jsonImpuestos.add(imp);
            }

            JsonArray ingredientes = new JsonArray();
            for (JsonElement jsonElement1 : getProducto(idProducto).get("ingredientesAplicados").getAsJsonArray()) {
                JsonObject objIngredientes = new JsonObject();
                ingredientes.add(jsonElement1.getAsJsonObject());
            }
            obj.add("ingredientesAplicados", ingredientes);
            obj.add("impuestosAplicados", jsonImpuestos);

        }
        totalImpuesto += impTotal;
        totalVenta += impTotal;
        totalVenta += impoconsumo;
        impuesto = 0;
        impoconsumo = 0;
        impTotal = 0;
        for (Map.Entry<Long, CategoriaProductoListItemCortesia> entry
                : productosCortesia.entrySet()) {
            long idProducto = entry.getValue().identificadorProducto;
            int cantidadProducto = Integer.parseInt(entry.getValue().jCantidad.getText());
            JsonObject obj = new JsonObject();
            obj.addProperty("idTransaccionVentaDetalle", getProducto(idProducto).get("idTransaccionVentaDetalle").getAsInt());
            obj.addProperty("idTransaccionDetalleVenta", getProducto(idProducto).get("idTransaccionVentaDetalle").getAsInt());
            obj.addProperty("identificadorProducto", idProducto);
            obj.addProperty("nombreProducto", getProducto(idProducto).get("nombreProducto").getAsString()); // CENTRALIZADOR
            obj.addProperty("identificacionProducto", getProducto(idProducto).get("identificacionProducto").getAsString());
            obj.addProperty("fechaTransaccion", getProducto(idProducto).get("fechaTransaccion").getAsString());
            obj.addProperty("cantidadVenta", cantidadProducto);
            obj.addProperty("unidad", getProducto(idProducto).get("unidad").getAsString());
            obj.addProperty("identificadorUnidad", getProducto(idProducto).get("identificadorUnidad").getAsInt());// DERRUMBAR
            obj.addProperty("costoProducto", getProducto(idProducto).get("costoProducto").getAsFloat());
            double precioProducto = getProducto(idProducto).get("precioProducto").getAsDouble();
            precioProducto = precioProducto / getProducto(idProducto).get("cantidadVenta").getAsDouble();
            precioProducto = precioProducto * cantidadProducto;
            obj.addProperty("precioProducto", precioProducto);
            
            double impoconsumoVenta = getProducto(idProducto).get("impoconsumo").getAsDouble();
            impoconsumoVenta = impoconsumoVenta / getProducto(idProducto).get("cantidadVenta").getAsInt();
            impoconsumoVenta = impoconsumoVenta * cantidadProducto;
            obj.addProperty("impoconsumo", impoconsumoVenta);
            
            obj.addProperty("precioVentaFeWeb", getProducto(idProducto).get("precioVentaFeWeb").getAsDouble());
            
            obj.addProperty("identificadorDescuento", getProducto(idProducto).get("identificadorDescuento").getAsInt());
            double subTotal = getProducto(idProducto).get("subTotalVenta").getAsDouble();
            subTotal = subTotal / getProducto(idProducto).get("cantidadVenta").getAsInt();
            subTotal = subTotal * cantidadProducto;
            obj.addProperty("subTotalVenta", subTotal);
            obj.addProperty("descuentoTotal", getProducto(idProducto).get("descuentoTotal").getAsDouble());
            obj.addProperty("total_cantidad", getProducto(idProducto).get("total_cantidad").getAsFloat());
            obj.addProperty("cortesia", true);
            obj.addProperty("base", getProducto(idProducto).get("base").getAsDouble());

            JsonObject jsonAtributos = new JsonObject();
            jsonAtributos.addProperty("categoriaId", getProducto(idProducto).get("atributos").getAsJsonObject().get("categoriaId").getAsLong());
            jsonAtributos.addProperty("categoriaDescripcion", getProducto(idProducto).get("atributos").getAsJsonObject().get("categoriaDescripcion").getAsString());
            jsonAtributos.addProperty("tipo", getProducto(idProducto).get("atributos").getAsJsonObject().get("tipo").getAsLong());
            jsonAtributos.addProperty("isElectronica", "true");
            obj.add("atributos", jsonAtributos);

            JsonArray jsonImpuestos = new JsonArray();
            for (JsonElement elemt : getProducto(idProducto).getAsJsonArray("impuestosAplicados")) {
                JsonObject dataIpm = elemt.getAsJsonObject();
                JsonObject imp = new JsonObject();
                imp.addProperty("identificadorImpuesto", dataIpm.get("identificadorImpuesto").getAsInt());
                imp.addProperty("nombreImpuesto", dataIpm.get("nombreImpuesto").getAsString());
                imp.addProperty("tipoImpuesto", dataIpm.get("tipoImpuesto").getAsString());
                imp.addProperty("valorImpAplicado", dataIpm.get("valorImpAplicado").getAsFloat());
                imp.addProperty("valorImpuestoAplicado", dataIpm.get("valorImpuestoAplicado").getAsFloat());
                if (imp.get("tipoImpuesto").getAsString().equals("$")) {
                    impoconsumo = imp.get("valorImpuestoAplicado").getAsFloat();
                    impoconsumo = impoconsumo / cantidad;
                    impoconsumo = impoconsumo * cantidadProducto;
                    imp.addProperty("valorImpuestoAplicado", impoconsumo);
                } else {
                    impuesto = imp.get("valorImpuestoAplicado").getAsFloat();
                    impuesto = impuesto / cantidad;
                    impuesto = impuesto * cantidadProducto;
                    imp.addProperty("valorImpuestoAplicado", impuesto);
                    impTotal += imp.get("valorImpuestoAplicado").getAsFloat();
                }

                jsonImpuestos.add(imp);
            }
            JsonArray ingredientes = new JsonArray();
            for (JsonElement jsonElement1 : getProducto(idProducto).get("ingredientesAplicados").getAsJsonArray()) {
                ingredientes.add(jsonElement1.getAsJsonObject());
            }
            obj.add("ingredientesAplicados", ingredientes);
            obj.add("impuestosAplicados", jsonImpuestos);
            detallesVenta.add(obj);
        }

        descuentoTotal += impoconsumo;
        totalVenta += impTotal;
        respuestaFactura.get("datos_FE").getAsJsonObject().remove("detallesVenta");
        respuestaFactura.get("datos_FE").getAsJsonObject().add("detallesVenta", detallesVenta);
        store.totalVenta = ventaTotal;
        ventaTotal = 0;

    }

    private void agregarMedioPago() {
        if (!productosCortesia.isEmpty()) {
            MediosPagosBean medio = new MediosPagosBean();
            JsonObject atributos = new JsonObject();
            atributos.addProperty("visible", false);
            medio.setId(20002);
            medio.setDescripcion("CORTESIA");
            medio.setIdRegistro(15);
            medio.setCredito(false);
            medio.setEstado("A");
            medio.setMinimo_valor(0);
            medio.setMaximo_valor(0);
            medio.setComprobante(false);
            medio.setAtributos(atributos);
            medio.setRecibido(totalCortesia);
            medio.setValor(totalCortesia);
            store.mediosPagoVenta.add(medio);
            DefaultTableModel table = (DefaultTableModel) store.jtableMediosPago.getModel();
            int filas = table.getRowCount();
            if (filas > 0) {
                if (totalOtros > 0) {
                    store.mediosPagoVenta.get(0).setRecibido(totalOtros);
                    store.mediosPagoVenta.get(0).setValor(totalOtros);
                } else {
                    store.mediosPagoVenta.remove(0);
                }
            } else {
                if (totalOtros > 0) {
                    MediosPagosBean medioOtros = new MediosPagosBean();
                    medioOtros.setId(1);
                    medioOtros.setDescripcion("EFECTIVO");
                    medioOtros.setIdRegistro(15);
                    medioOtros.setCredito(false);
                    medioOtros.setEstado("A");
                    medioOtros.setMinimo_valor(0);
                    medioOtros.setMaximo_valor(0);
                    medioOtros.setComprobante(false);
                    medioOtros.setRecibido(totalOtros);
                    medioOtros.setValor(totalOtros);
                    store.mediosPagoVenta.add(medioOtros);
                }
            }
            for (MediosPagosBean mediosPagosBean : store.mediosPagoVenta) {
                System.out.println(Main.ANSI_PURPLE + mediosPagosBean.getId() + Main.ANSI_RESET);
            }

            store.loadMediosPagosTable();
            DefaultTableModel dm = (DefaultTableModel) store.jtableMediosPago.getModel();
            int n = dm.getRowCount();
            float recibidoVenta = 0;
            for (int i = 0; i < n; i++) {
                recibidoVenta += Float.parseFloat(dm.getValueAt(i, 2).toString().replace("$", "").replace(".", ""));
            }
            float totalVenta = totalOtros + totalCortesia;
            float cambioVenta = recibidoVenta - totalVenta;
            store.jtotal_venta.setText("$ " + df.format(totalVenta));
            store.jrecibido_venta.setText("$ " + df.format(totalVenta));
            store.jcambio_venta.setText("$ " + df.format(cambioVenta));
        }
        actualizarPagos();
    }
}

