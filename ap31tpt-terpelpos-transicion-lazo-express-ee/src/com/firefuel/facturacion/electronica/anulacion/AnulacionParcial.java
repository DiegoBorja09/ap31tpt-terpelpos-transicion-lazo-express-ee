/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.firefuel.facturacion.electronica.anulacion;

import com.controllers.NovusConstante;
import com.dao.DAOException;
import com.dao.anulacion.AnulacionDao;
import com.facade.anulacion.AnulacionProductos;
import com.facade.anulacion.Impuestos;
import com.facade.anulacion.Pagos;
import com.firefuel.ConfirmarAnulacionView;
import static com.firefuel.ConfirmarAnulacionView.notificadorView;
import com.firefuel.Main;
import com.firefuel.componentes.anulacion.ItemProducto;
import com.firefuel.facturacion.electronica.NotasCredito;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 *
 * @author Usuario
 */
public class AnulacionParcial {

    ConfirmarAnulacionView anulacionView;
    TreeMap<Long, AnulacionProductos> productosMapDetalle = new TreeMap<>();
    TreeMap<Long, AnulacionProductos> productosMapAnulaciones = new TreeMap<>();
    TreeMap<Long, Integer> listaProductosAnulacion = new TreeMap<>();
    TreeMap<Long, Integer> listaProductosDetalles = new TreeMap<>();
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);
    public long productoTemporal = 0;
    int consecutivo = 0;
    int cantidadDetalles = 0;

    public AnulacionParcial() {

    }

    public AnulacionParcial(ConfirmarAnulacionView anulacionView) {
        this.anulacionView = anulacionView;
    }

    public void listarProductos(long idMovimiento) {
        AnulacionDao anulacionDao = new AnulacionDao();
        productosMapAnulaciones.clear();
        productosMapDetalle.clear();
        listaProductosAnulacion.clear();
        listaProductosDetalles.clear();
        anulacionView.pnlContainerProductosDetalle.removeAll();
        anulacionView.pnlContainerProductosAnulacion.removeAll();
        validarBotonAnular();
        ArrayList<AnulacionProductos> productos = (ArrayList<AnulacionProductos>) anulacionDao.obtenerProductos(idMovimiento);
        cantidadDetalles = anulacionDao.obtenerCantidadDetalles(idMovimiento);
        productos.forEach(detalle -> {
            detalle = obtenerCalculosIva(detalle, true);
            detalle.setCantidadProducto(detalle.getCantidad());
            consecutivo = (int) detalle.getConsecutivo();
            productosMapDetalle.put(detalle.getId(), detalle);
            listarProductoDetalle(detalle, false);
        });
    }
    
    public void listarProductosAnular(List<AnulacionProductos> productos){
        listaProductosAnulacion.clear();
         productos.forEach(detalle -> {
            detalle = obtenerCalculosIva(detalle, true);
            detalle.setCantidadProducto(detalle.getCantidad());
            consecutivo = (int) detalle.getConsecutivo();
            productosMapAnulaciones.put(detalle.getId(), detalle);
        });
    }

    private double obtenerImpoconsumo(AnulacionProductos anulacionProductos) {
        for (Impuestos impuestos : anulacionProductos.getImpuestos()) {
            if (impuestos.getTipo().equals("$")) {
                impuestos.setValor_imp(impuestos.getValor() * anulacionProductos.getCantidad());
                return impuestos.getValor();
            }
        }
        return 0;
    }

    public AnulacionProductos obtenerCalculosIva(AnulacionProductos detalle, boolean agregarImpoconsumo) {
        AnulacionProductos anulacionProductos = detalle;
        double precio = detalle.getPrecio();
        double impoconsumo = obtenerImpoconsumo(detalle);
        precio = precio - impoconsumo;
        double base = 0;
        ArrayList<Impuestos> nuevosImpoCalculado = new ArrayList<>();
        ArrayList<Impuestos> nuevosImpoCalculadoImpoconsumo = new ArrayList<>();
        for (Impuestos impuestos : detalle.getImpuestos()) {

            if (impuestos.getTipo().equals("%")) {
                base = precio / ((impuestos.getValor() / 100d) + 1d);
                double impuestoCalculado = base * (impuestos.getValor() / 100d);
                anulacionProductos.setBase(base);
                anulacionProductos.setPrecio(detalle.getPrecio());
                impuestoCalculado = impuestoCalculado * detalle.getCantidad();
                impuestos.setValor_imp(impuestoCalculado);
                anulacionProductos.setTotalImpuesto(impuestoCalculado);
                nuevosImpoCalculado.add(impuestos);
            } else {
                impuestos.setValor_imp(impoconsumo * detalle.getCantidad());
                nuevosImpoCalculadoImpoconsumo.add(impuestos);
                if (agregarImpoconsumo) {
                    nuevosImpoCalculado.add(impuestos);
                }
            }

        }

        if (detalle.getImpuestos().isEmpty()) {
            precio = detalle.getPrecio();
            base = precio;
            anulacionProductos.setPrecio(precio);
            anulacionProductos.setBase(precio);
        }

        anulacionProductos.setImpuestos(nuevosImpoCalculado);
        anulacionProductos.setImpoconsumo(nuevosImpoCalculadoImpoconsumo);
        anulacionProductos.setSubTotal((base * detalle.getCantidad()) + (impoconsumo * detalle.getCantidad()));
        anulacionProductos.setTotal(detalle.getPrecio() * detalle.getCantidad());
        return anulacionProductos;
    }

    private void listarProductoDetalle(AnulacionProductos detalle, boolean agregarUnaSola) {
        this.anulacionView.pnlContainerProductosDetalle.setPreferredSize(new Dimension(580, 430));
        AnulacionProductos anulacionProductos = clonarObjeto(detalle);
        ItemProducto itemProducto;
        if (!hayProductosDetalles(anulacionProductos.getId())) {
            if (agregarUnaSola) {
                anulacionProductos.setTotal(anulacionProductos.getPrecio());
                anulacionProductos.setCantidad(agregarCantidad(anulacionProductos.getCantidad()));
                AnulacionProductos anulacionProductosCalculado = obtenerCalculosIva(anulacionProductos, true);
                itemProducto = agregarproducto(anulacionProductosCalculado, this.anulacionView.pnlContainerProductosDetalle, "+");
                listaProductosDetalles.put(anulacionProductos.getId(), anulacionView.pnlContainerProductosDetalle.getComponentCount() - 1);
                productosMapDetalle.put(anulacionProductosCalculado.getId(), anulacionProductosCalculado);
            } else {
                AnulacionProductos anulacionProductosCalculado = obtenerCalculosIva(anulacionProductos, true);
                itemProducto = agregarproducto(anulacionProductos, this.anulacionView.pnlContainerProductosDetalle, "+");
                listaProductosDetalles.put(anulacionProductos.getId(), anulacionView.pnlContainerProductosDetalle.getComponentCount() - 1);
                productosMapDetalle.put(anulacionProductosCalculado.getId(), anulacionProductosCalculado);
            }
            itemProducto.lblTotal.setText(productosMapDetalle.get(anulacionProductos.getId()) + "");
            itemProducto.lblAccion.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    listarProductoAnulacion(anulacionProductos);
                    productosMapDetalle.get(anulacionProductos.getId()).setCantidad(productosMapDetalle.get(anulacionProductos.getId()).getCantidad() - agregarCantidad(productosMapDetalle.get(anulacionProductos.getId()).getCantidad()));
                    itemProducto.lblCantidad.setText(productosMapDetalle.get(anulacionProductos.getId()).getCantidad() + "");
                    quitar(430, 70, anulacionView.pnlContainerProductosDetalle.getComponentCount() * (70 + 5), anulacionView.pnlContainerProductosDetalle);
                    anulacionView.pnlContainerProductosDetalle.revalidate();
                    anulacionView.pnlContainerProductosDetalle.repaint();
                    anulacionView.lblAnularParcial.setEnabled(true);
                    AnulacionProductos anulacionProductosCalculado = obtenerCalculosIva(productosMapDetalle.get(anulacionProductos.getId()), true);
                    productosMapDetalle.remove(detalle.getId());
                    productosMapDetalle.put(anulacionProductosCalculado.getId(), anulacionProductosCalculado);
                    itemProducto.lblTotal.setText("$ " + df.format(productosMapDetalle.get(anulacionProductos.getId()).getTotal()));
                    eliminarProductoPanel(itemProducto, true, anulacionProductos.getId());
                }
            });
        } else {
            itemProducto = obtenerComponente(anulacionProductos.getId(), true);
            productosMapDetalle.get(detalle.getId()).setCantidad(productosMapDetalle.get(anulacionProductos.getId()).getCantidad() + 1);
            AnulacionProductos anulacionProductosCalculado = productosMapDetalle.get(detalle.getId());
            anulacionProductosCalculado = obtenerCalculosIva(anulacionProductosCalculado, true);
            productosMapDetalle.remove(detalle.getId());
            productosMapDetalle.put(anulacionProductosCalculado.getId(), anulacionProductosCalculado);
        }
        itemProducto.lblTotal.setText("$ " + df.format(productosMapDetalle.get(anulacionProductos.getId()).getTotal()));
        itemProducto.lblCantidad.setText(productosMapDetalle.get(anulacionProductos.getId()).getCantidad() + "");
        this.anulacionView.pnlContainerProductosDetalle.revalidate();
        this.anulacionView.pnlContainerProductosDetalle.repaint();
    }

    private void listarProductoAnulacion(AnulacionProductos detalle) {
        this.anulacionView.pnlContainerProductosAnulacion.setPreferredSize(new Dimension(580, 430));
        AnulacionProductos anulacionProductos = clonarObjeto(detalle);
        ItemProducto itemProducto;
        if (!hayProductosDetallesAnulacion(anulacionProductos.getId())) {
            anulacionProductos.setTotal(anulacionProductos.getPrecio());
            anulacionProductos.setCantidad(agregarCantidad(anulacionProductos.getCantidad()));
            AnulacionProductos anulacionProductosCalculado = obtenerCalculosIva(anulacionProductos, true);
            itemProducto = agregarproducto(anulacionProductosCalculado, anulacionView.pnlContainerProductosAnulacion, "-");
            listaProductosAnulacion.put(anulacionProductosCalculado.getId(), anulacionView.pnlContainerProductosAnulacion.getComponentCount() - 1);
            productosMapAnulaciones.put(anulacionProductosCalculado.getId(), anulacionProductosCalculado);
            itemProducto.lblAccion.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    listarProductoDetalle(anulacionProductos, true);
                    quitar(430, 70, anulacionView.pnlContainerProductosAnulacion.getComponentCount() * (70 + 5), anulacionView.pnlContainerProductosAnulacion);
                    productosMapAnulaciones.get(anulacionProductos.getId()).setCantidad(productosMapAnulaciones.get(anulacionProductos.getId()).getCantidad() - agregarCantidad(productosMapAnulaciones.get(anulacionProductos.getId()).getCantidad()));
                    itemProducto.lblCantidad.setText(productosMapAnulaciones.get(anulacionProductos.getId()).getCantidad() + "");
                    anulacionView.pnlContainerProductosAnulacion.revalidate();
                    anulacionView.pnlContainerProductosAnulacion.repaint();
                    AnulacionProductos anulacionProductosCalculado = obtenerCalculosIva(productosMapAnulaciones.get(anulacionProductos.getId()), true);
                    productosMapAnulaciones.remove(detalle.getId());
                    productosMapAnulaciones.put(anulacionProductosCalculado.getId(), anulacionProductosCalculado);
                    itemProducto.lblTotal.setText("$ " + df.format(productosMapAnulaciones.get(anulacionProductos.getId()).getTotal()));
                    eliminarProductoPanel(itemProducto, false, anulacionProductos.getId());
                    validarBotonAnular();
                }
            });
        } else {
            itemProducto = obtenerComponente(anulacionProductos.getId(), false);
            productosMapAnulaciones.get(detalle.getId()).setCantidad(productosMapAnulaciones.get(anulacionProductos.getId()).getCantidad() + 1);
            AnulacionProductos anulacionProductosCalculado = productosMapAnulaciones.get(detalle.getId());
            anulacionProductosCalculado = obtenerCalculosIva(anulacionProductosCalculado, true);
            productosMapAnulaciones.remove(detalle.getId());
            productosMapAnulaciones.put(anulacionProductosCalculado.getId(), anulacionProductosCalculado);
        }

        itemProducto.lblCantidad.setText(productosMapAnulaciones.get(anulacionProductos.getId()).getCantidad() + "");
        itemProducto.lblTotal.setText("$ " + df.format(productosMapAnulaciones.get(anulacionProductos.getId()).getTotal()));

    }

    private float agregarCantidad(float cantidad) {
        if (validarCantidadGranel(cantidad)) {
            return cantidad;
        } else {
            return 1;
        }
    }

    public ItemProducto agregarproducto(AnulacionProductos detalle, JPanel panel, String boton) {

        ItemProducto itemProducto = new ItemProducto();
        itemProducto.lblProducto.setText(detalle.getProducto());
        itemProducto.lblCantidad.setText(detalle.getCantidad() + "");
        itemProducto.lblTotal.setText("$ " + df.format(detalle.getTotal()));
        itemProducto.lblAccion.setText(boton);
        itemProducto.setPreferredSize(new Dimension(570, 70));
        itemProducto.setName(detalle.getId() + "");
        panel.add(itemProducto);
        itemProducto.setPreferredSize(new Dimension(570, 70));
        agregarAltura(430, 70, panel.getComponentCount() * (70 + 5), panel);
        this.anulacionView.revalidate();
        this.anulacionView.repaint();
        return itemProducto;
    }

    private void agregarAltura(int alturaInicial, int alturaAgregar, int alturaComponentes, JPanel panel) {
        if (alturaComponentes >= alturaInicial) {
            alturaAgregar = alturaAgregar + panel.getHeight();
            panel.setPreferredSize(new Dimension(580, alturaAgregar + 5));
        }
    }

    private void quitar(int alturaInicial, int alturaAgregar, int alturaComponentes, JPanel panel) {
        if (alturaComponentes >= alturaInicial) {
            alturaAgregar = (alturaAgregar + 5) - panel.getHeight();
            panel.setPreferredSize(new Dimension(580, alturaAgregar));
        }
    }

    public void anular(long numeroVenta) {
        try {
            String url = "parcial";
            String observaciones = "";
            NotasCredito nota = new NotasCredito();
            JsonObject data = new JsonObject();
            JsonArray productosArr = new JsonArray();
            double totaImpuesto = 0d;
            double totalVenta = 0d;
            double costoTotal = 0d;
            for (Map.Entry<Long, AnulacionProductos> entry : productosMapAnulaciones.entrySet()) {
                AnulacionProductos productos = obtenerCalculosIva(entry.getValue(), false);
                JsonObject productosObj = Main.gson.fromJson(Main.gson.toJson(productos, AnulacionProductos.class), JsonObject.class);
                totalVenta = totalVenta + entry.getValue().getTotal();
                totaImpuesto = totaImpuesto + entry.getValue().getTotalImpuesto();
                costoTotal = costoTotal + entry.getValue().getCosto();
                productosArr.add(productosObj);
            }
            Pagos pagos = new Pagos();
            pagos.setDescripcionMedio("EFECTIVO");
            pagos.setIdentificacionComprobante("");
            pagos.setMonedaLocal("S");
            pagos.setIdentificacionMediosPagos(1);
            pagos.setTotalMedioPago(totalVenta);
            pagos.setRecibidoMedioPago(totalVenta);
            pagos.setVueltoMedioPago(0);
            pagos.setTrm(0);
            data.add("mediosDePago", Main.gson.fromJson(Main.gson.toJson(pagos, Pagos.class), JsonObject.class));
            data.add("productos", productosArr);
            data.addProperty("totalVenta", totalVenta);
            data.addProperty("totaImpuesto", totaImpuesto);
            data.addProperty("costoTotal", totaImpuesto);

            nota.recibirNotaCreditoWS(url, observaciones, true, numeroVenta, NovusConstante.TIPO_ANULACION_NOTA_CREDITO, 1, false, notificadorView, consecutivo, data);
        } catch (DAOException ex) {
            Logger.getLogger(ConfirmarAnulacionView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void editarCantidad() {
        ItemProducto componente = obtenerComponente(productoTemporal, false);

        float cantidadTotal = productosMapDetalle.get(productoTemporal).getCantidadProducto();
        int cantidadIngresada = Integer.parseInt(anulacionView.txtCantidad.getText());
        if (cantidadIngresada <= cantidadTotal) {
            validarProducto();
            ItemProducto itemProductoAnulacion = obtenerComponente(productoTemporal, true);
            componente.lblCantidad.setText(cantidadIngresada + "");
            eliminarProductoPanel(itemProductoAnulacion, true, productoTemporal);
        }
    }

    private void validarProducto() {
        if (productosMapDetalle.containsKey(productoTemporal)) {
            float cantidad = productosMapDetalle.get(productoTemporal).getCantidadProducto() - Float.parseFloat(anulacionView.txtCantidad.getText());
            productosMapAnulaciones.get(productoTemporal).setCantidad(Integer.parseInt(anulacionView.txtCantidad.getText()));
            AnulacionProductos detalle = productosMapAnulaciones.get(productoTemporal);
            detalle = obtenerCalculosIva(detalle, true);
            productosMapAnulaciones.remove(productoTemporal);
            productosMapAnulaciones.put(productoTemporal, detalle);
            ItemProducto itemProductoAnulacion = obtenerComponente(productoTemporal, true);
            itemProductoAnulacion.lblCantidad.setText(cantidad + "");
            productosMapDetalle.get(productoTemporal).setCantidad(cantidad);
        }
    }

    private ItemProducto obtenerComponente(long componente, boolean detalle) {
        ItemProducto itemProducto = null;
        if (detalle) {
            for (Component component : anulacionView.pnlContainerProductosDetalle.getComponents()) {
                if (component.getName().equals(componente + "")) {
                    itemProducto = (ItemProducto) component;
                    break;
                }
            }
        } else {
            for (Component component : anulacionView.pnlContainerProductosAnulacion.getComponents()) {
                if (component.getName().equals(componente + "")) {
                    itemProducto = (ItemProducto) component;
                    break;
                }
            }
        }
        return itemProducto;
    }

    private boolean hayProductosDetallesAnulacion(long id) {
        return listaProductosAnulacion.containsKey(id);
    }

    private boolean hayProductosDetalles(long id) {
        return listaProductosDetalles.containsKey(id);
    }

    private void validarBotonAnular() {
        if (anulacionView.pnlContainerProductosAnulacion.getComponentCount() < 1) {
            anulacionView.lblAnularParcial.setEnabled(false);
        }
    }

    private AnulacionProductos clonarObjeto(AnulacionProductos detalle) {
        AnulacionProductos anulacionProductos = new AnulacionProductos();
        anulacionProductos.setAtributos(detalle.getAtributos());
        anulacionProductos.setBase(detalle.getBase());
        anulacionProductos.setCantidad(detalle.getCantidad());
        anulacionProductos.setCompuesto(detalle.getCompuesto());
        anulacionProductos.setConsecutivo(detalle.getConsecutivo());
        anulacionProductos.setCortesia(detalle.isCortesia());
        anulacionProductos.setCosto_producto(detalle.getCosto_producto());
        anulacionProductos.setCosto_unidad(detalle.getCosto_unidad());
        anulacionProductos.setDescuento_id(detalle.getDescuento_id());
        anulacionProductos.setDescuentos(detalle.getDescuentos());
        anulacionProductos.setId(detalle.getId());
        anulacionProductos.setImpuestos(detalle.getImpuestos());
        anulacionProductos.setIngredientes(detalle.getIngredientes());
        anulacionProductos.setPrecio(detalle.getPrecio());
        anulacionProductos.setPrefijo(detalle.getPrefijo());
        anulacionProductos.setProducto(detalle.getProducto());
        anulacionProductos.setProducto_descripcion(detalle.getProducto_descripcion());
        anulacionProductos.setProducto_tipo(detalle.getProducto_tipo());
        anulacionProductos.setProductos_id(detalle.getProductos_id());
        anulacionProductos.setProductos_plu(detalle.getProductos_plu());
        anulacionProductos.setRemoto_id(detalle.getRemoto_id());
        anulacionProductos.setSincronizado(detalle.getSincronizado());
        anulacionProductos.setSubTotal(detalle.getSubTotal());
        anulacionProductos.setTotal(detalle.getTotal());
        anulacionProductos.setUnidad(detalle.getUnidad());
        anulacionProductos.setUnidad_descripcion(detalle.getUnidad_descripcion());
        anulacionProductos.setIdentificadorBodega(detalle.getIdentificadorBodega());
        anulacionProductos.setCantidadProducto(detalle.getCantidad());
        anulacionProductos.setTotalImpuesto(detalle.getTotalImpuesto());
        anulacionProductos.setCosto(detalle.getCosto());
        anulacionProductos.setProducto_descripcion(detalle.getProducto());
        anulacionProductos.setImpoconsumo(detalle.getImpuestos());
        anulacionProductos.setUnidadId(detalle.getUnidadId());
        return anulacionProductos;
    }

    private void eliminarProductoPanel(Component componente, boolean detalle, long id) {
        if (detalle) {
            if (productosMapDetalle.get(id).getCantidad() < 1) {
                anulacionView.pnlContainerProductosDetalle.remove(componente);
                listaProductosDetalles.remove(id);
                productosMapDetalle.remove(id);
            }
        } else {
            if (productosMapAnulaciones.get(id).getCantidad() < 1) {
                anulacionView.pnlContainerProductosAnulacion.remove(componente);
                listaProductosAnulacion.remove(id);
                productosMapAnulaciones.remove(id);
            }
        }
    }

    private boolean validarCantidadGranel(float cantidad) {
        String cantidadGranel = String.valueOf(cantidad);
        int indice = cantidadGranel.indexOf(".");
        return indice != -1 && cantidadGranel.substring(indice + 1).matches("[1-9]+");
    }

}
