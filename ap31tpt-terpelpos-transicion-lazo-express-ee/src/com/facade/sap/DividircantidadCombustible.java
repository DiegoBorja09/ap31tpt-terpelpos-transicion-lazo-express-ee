package com.facade.sap;

import com.bean.BodegaBean;
import com.bean.entradaCombustible.EntradaCombustibleBean;
import com.bean.entradaCombustible.ProductoDividido;
import com.bean.entradaCombustible.ProductoSAP;
import com.controllers.NovusUtils;
import com.dao.EntradaCombustibleDao.EntradaCombustibleDao;
import com.dao.EntradaCombustibleDao.ProductoDivididoDao;
import com.firefuel.RecepcionCombustibleView;
import com.firefuel.componentes.sap.ItemTanques;
import com.firefuel.components.panelesPersonalizados.BordesRedondos;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Devitech
 */
public class DividircantidadCombustible {

    RecepcionCombustibleView recepcionCombustibleView;
    TreeMap<Long, ProductoDividido> tanqueDividido = new TreeMap<>();
    TreeMap<Long, Integer> indiceComponentes = new TreeMap<>();
    TreeMap<Integer, Long> tanques = new TreeMap<>();
    private float cantidadRestante = 0f;
    private float cantidadAgregada = 0f;
    private static final String ICONO_ERROR = "/com/firefuel/resources/btBad.png";
    private static final String ICONO_OK = "/com/firefuel/resources/btOk.png";
    private static final String DETALLES_SAP = "detalleSAP";

    public DividircantidadCombustible(RecepcionCombustibleView recepcionCombustibleView) {
        this.recepcionCombustibleView = recepcionCombustibleView;
    }

    public void agregarCantidadDividida(EntradaCombustibleBean entradaCombustibleBean, long promotorId) {
        String valorCantidad = this.recepcionCombustibleView.txtCantidad.getText();
        if (validarCampoCantidad(valorCantidad)) {
            float cantidad = Float.parseFloat(valorCantidad);
            if (validarCantidadAgregar(cantidad)) {
                if (tanqueDividido.isEmpty() || tanqueDividido.get(obtenerIdtanque()) == null) {
                    renderizarcomponenteItem(obtenerIdtanque());
                    agregarCantidadDivididaAguardar(entradaCombustibleBean, promotorId);
                    this.recepcionCombustibleView.txtCantidad.setText(this.cantidadRestante + "");
                    hablitarBotonGuardar();
                } else {
                    String mensaje = "Tanque seleccionado, por favor, seleccione otro.";
                    NovusUtils.setMensaje(mensaje.toUpperCase(), this.recepcionCombustibleView.jLabel1);
                    NovusUtils.printLn(mensaje);
                    limpiarMensaje();
                }
            }
        }
    }

    private boolean validarCampoCantidad(String cantidad) {
        if (cantidad.isEmpty()) {
            String mensaje = "este campo No puede quedar vacío".toUpperCase();
            NovusUtils.setMensaje(mensaje, this.recepcionCombustibleView.jLabel1);
            NovusUtils.printLn(mensaje);
            limpiarMensaje();
            return false;
        } else {
            return true;
        }
    }

    private boolean validarCantidadAgregar(float cantidad) {
        if (cantidad == 0) {
            String mensaje = "La cantidad a ingresar debe ser mayor a 0";
            NovusUtils.setMensaje(mensaje.toUpperCase(), this.recepcionCombustibleView.jLabel1);
            NovusUtils.printLn(mensaje);
            limpiarMensaje();
            return false;
        } else if (cantidad > this.cantidadRestante) {
            String mensaje = "La cantidad a ingresar no debe ser mayor al restante";
            NovusUtils.setMensaje(mensaje.toUpperCase(), this.recepcionCombustibleView.jLabel1);
            NovusUtils.printLn(mensaje);
            limpiarMensaje();
            return false;
        } else {
            return true;
        }
    }

    private void llenarTanqueCombo(EntradaCombustibleBean entradaCombustibleBean) {
        ArrayList<BodegaBean> bodegaBean = entradaCombustibleBean.getTanques().get(obtenerProducto(entradaCombustibleBean).getClave());
        int index = 0;
        tanques.clear();
        this.recepcionCombustibleView.jComboTanques.removeAllItems();
        if (!bodegaBean.isEmpty()) {
            for (BodegaBean bodegaBean1 : bodegaBean) {
                this.recepcionCombustibleView.jComboTanques.addItem(bodegaBean1.getDescripcion());
                tanques.put(index, bodegaBean1.getId());
                index++;
            }
        }
    }

    public boolean validarCantidadTanque(EntradaCombustibleBean entradaCombustibleBean) {
        ArrayList<BodegaBean> bodegaBean = entradaCombustibleBean.getTanques().get(obtenerProducto(entradaCombustibleBean).getClave());
        return bodegaBean != null && bodegaBean.size() > 1;
    }

    public void seleccionProducto(EntradaCombustibleBean entradaCombustibleBean) {
        int cantidadDeRegistrosSeleccionados = obtenerSelecciones().length;
        if (cantidadDeRegistrosSeleccionados > 1 || cantidadDeRegistrosSeleccionados == 0) {
            String mensaje = cantidadDeRegistrosSeleccionados > 1 ? "solo se puede un producto a la vez" : "Seleccione un registro";
            mensajes(DETALLES_SAP, mensaje.toUpperCase(), ICONO_ERROR, null);
            NovusUtils.printLn(mensaje);
        } else {
            if (validarCantidadTanque(entradaCombustibleBean)) {
                llenarInformacionGenral(entradaCombustibleBean);
                llenarTanqueCombo(entradaCombustibleBean);
                this.recepcionCombustibleView.showPanel("pnlDivisionProductos");
            } else {
                String mensaje = "la división solo está permitida cuando hay más de un tanque disponible";
                NovusUtils.printLn(mensaje);
                mensajes(DETALLES_SAP, mensaje.toUpperCase(), ICONO_ERROR, null);
            }
        }
    }

    public void habilitarbotonModificar(EntradaCombustibleBean entradaCombustibleBean) {

        if (obtenerSelecciones().length > 1 || !validarCantidadTanque(entradaCombustibleBean)) {
            this.recepcionCombustibleView.btnModificar.setBackground(new Color(227, 227, 232));
            this.recepcionCombustibleView.btnModificar.setBorder(new BordesRedondos(new Color(227, 227, 227), 20));
        } else {
            this.recepcionCombustibleView.btnModificar.setBackground(new Color(255, 255, 255));
            this.recepcionCombustibleView.btnModificar.setBorder(new BordesRedondos(new Color(204, 0, 0), 20));
        }
    }

    private void llenarInformacionGenral(EntradaCombustibleBean entradaCombustibleBean) {
        ProductoSAP productoSAP = obtenerProducto(entradaCombustibleBean);
        this.cantidadRestante = productoSAP.getQuantity();
        this.recepcionCombustibleView.txtCantidad.setText(productoSAP.getQuantity() + "");
        this.recepcionCombustibleView.lblCantidadInformacion.setText(productoSAP.getQuantity() + " " + productoSAP.getUnit());
        this.recepcionCombustibleView.lblProductoInformacion.setText(productoSAP.getDescripcion());
    }

    private void agregarCantidadDivididaAguardar(EntradaCombustibleBean entradaCombustibleBean, long promotorId) {
        ProductoDividido productoDividido = new ProductoDividido();
        productoDividido.setCantidad(Float.parseFloat(this.recepcionCombustibleView.txtCantidad.getText()));
        productoDividido.setIdTanque(obtenerIdtanque());
        productoDividido.setIdRemisionSap(entradaCombustibleBean.getIdRemision());
        productoDividido.setIdRemisionProducto(obtenerProducto(entradaCombustibleBean).getIdRemisionProducto());
        productoDividido.setIdProducto(obtenerProducto(entradaCombustibleBean).getProducID());
        productoDividido.setFechaDeCreacion(LocalDateTime.now());
        productoDividido.setYear(LocalDateTime.now().getYear());
        productoDividido.setMes(LocalDateTime.now().getMonthValue());
        productoDividido.setDia(LocalDateTime.now().getDayOfMonth());
        productoDividido.setIdPromotor(promotorId);
        tanqueDividido.put(obtenerIdtanque(), productoDividido);
    }

    private ProductoSAP obtenerProducto(EntradaCombustibleBean entradaCombustibleBean) {
        TreeMap<String, ProductoSAP> productosMap = (TreeMap<String, ProductoSAP>) entradaCombustibleBean.getProductoSAP();
        ArrayList<ProductoSAP> productos = new ArrayList<>(productosMap.values());
        int indice = this.recepcionCombustibleView.jInformacionDescargue.getSelectedRow();
        return productos.get(indice);
    }

    private long obtenerIdtanque() {
        return tanques.get(this.recepcionCombustibleView.jComboTanques.getSelectedIndex());
    }

    private void renderizarcomponenteItem(long idTanque) {
        ItemTanques itemTanques = new ItemTanques();
        itemTanques.pnlContenedor.setPreferredSize(new Dimension(580, 70));
        float canidad = Float.parseFloat(this.recepcionCombustibleView.txtCantidad.getText());
        itemTanques.lblCantidad.setText(canidad + "");
        itemTanques.lblNombreTanque.setText(this.recepcionCombustibleView.jComboTanques.getSelectedItem().toString());
        this.cantidadRestante = this.cantidadRestante - canidad;
        this.cantidadAgregada = this.cantidadAgregada + canidad;
        itemTanques.lblEliminar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                recepcionCombustibleView.ContenedorTanquesDividido.remove(itemTanques.pnlContenedor);
                NovusUtils.printLn("1- cantidad restante " + cantidadRestante);
                cantidadRestante = cantidadRestante + tanqueDividido.get(idTanque).getCantidad();
                NovusUtils.printLn("1- cantidad agregada " + cantidadAgregada);
                cantidadAgregada = cantidadAgregada - tanqueDividido.get(idTanque).getCantidad();
                tanqueDividido.remove(idTanque);
                recepcionCombustibleView.ContenedorTanquesDividido.revalidate();
                recepcionCombustibleView.ContenedorTanquesDividido.repaint();
                recepcionCombustibleView.txtCantidad.setText(cantidadRestante + "");
                NovusUtils.printLn("2- cantidad agregada " + cantidadAgregada);
                NovusUtils.printLn("2- cantidad restante " + cantidadRestante);
                hablitarBotonGuardar();
                reset();
            }
        });
        this.recepcionCombustibleView.ContenedorTanquesDividido.add(itemTanques.pnlContenedor);
        this.recepcionCombustibleView.ContenedorTanquesDividido.revalidate();
        this.recepcionCombustibleView.ContenedorTanquesDividido.repaint();
        itemTanques.Numero.setText(this.recepcionCombustibleView.ContenedorTanquesDividido.getComponents().length + "");
        int alturaComponente = this.recepcionCombustibleView.ContenedorTanquesDividido.getComponents().length * itemTanques.pnlContenedor.getPreferredSize().height + 20;
        this.indiceComponentes.put(idTanque, (this.recepcionCombustibleView.ContenedorTanquesDividido.getComponents().length - 1));
        agregarAltura(210, (itemTanques.pnlContenedor.getPreferredSize().height + 5), alturaComponente);
    }

    private void reset() {
        int index = 1;
        for (Component component : this.recepcionCombustibleView.ContenedorTanquesDividido.getComponents()) {
            if (component instanceof JPanel) {
                int indice = index;
                JPanel panel = (JPanel) component;
                Arrays.stream(panel.getComponents())
                        .filter(JPanel.class::isInstance)
                        .map(JPanel.class::cast)
                        .flatMap(panel1 -> Arrays.stream(panel1.getComponents()))
                        .filter(JLabel.class::isInstance)
                        .map(JLabel.class::cast)
                        .filter(label -> "numero".equals(label.getName()))
                        .forEach(label -> label.setText(indice + ""));
            }
            index++;
        }
    }

    private void agregarAltura(int alturaPadre, int agregarAltura, int alturaComponente) {
        if (alturaComponente >= alturaPadre) {
            int nuevaAltura = agregarAltura + this.recepcionCombustibleView.ContenedorTanquesDividido.getPreferredSize().height;
            this.recepcionCombustibleView.ContenedorTanquesDividido.setPreferredSize(new Dimension(560, nuevaAltura));
        }
    }

    private int[] obtenerSelecciones() {
        return this.recepcionCombustibleView.jInformacionDescargue.getSelectedRows();
    }

    public void guardarDivicion(String numeroRemision) {
        if (this.cantidadRestante != 0) {
            String mensaje = "debe asignar la totalidad de la cantidad antes de guardar";
            NovusUtils.setMensaje(mensaje.toUpperCase(), this.recepcionCombustibleView.jLabel1);
            NovusUtils.printLn(mensaje);
            limpiarMensaje();
        } else {
            ProductoDivididoDao productoDivididoDao = new ProductoDivididoDao();
            boolean respuesta = productoDivididoDao.guardarProductosDivididos(tanqueDividido);
            Runnable accion = () -> {
                RemisionesSAP remisionesSAP = new RemisionesSAP(this.recepcionCombustibleView);
                this.recepcionCombustibleView.setEntradaCombustible(remisionesSAP.consultarRemision(numeroRemision));
                remisionesSAP.validarRemision(this.recepcionCombustibleView.getEntradaCombustible());
            };
            if (respuesta) {
                this.recepcionCombustibleView.jTitle.setText("RECEPCIÓN DE COMBUSTIBLE");
                mensajes(DETALLES_SAP, "MODIFICACIÓN REALIZADA CON ÉXITO", ICONO_OK, accion);
            } else {
                mensajes(DETALLES_SAP, "ERROR AL REALIZAR LA MODIFICACIÓN", ICONO_ERROR, accion);
            }
            limpiarTodo();
        }

    }

    private void hablitarBotonGuardar() {
        if (this.cantidadRestante != 0) {
            this.recepcionCombustibleView.btnGuardar.setBackground(new Color(227, 227, 232));
        } else {
            this.recepcionCombustibleView.btnGuardar.setBackground(new Color(179, 15, 0));
        }
    }

    public void limpiarTodo() {
        tanqueDividido.clear();
        tanques.clear();
        indiceComponentes.clear();
        cantidadRestante = 0f;
        cantidadAgregada = 0f;
        this.recepcionCombustibleView.ContenedorTanquesDividido.removeAll();
    }

    public void mensajes(String panel, String mensaje, String icono, Runnable accion) {
        this.recepcionCombustibleView.panelAmostrar = panel;
        this.recepcionCombustibleView.accion = accion;
        this.recepcionCombustibleView.jtext.setText("<html>" + mensaje + "</html>");
        this.recepcionCombustibleView.jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource(icono)));
        this.recepcionCombustibleView.showPanel("mensajeNotificacion");
    }

    private void limpiarMensaje() {
        Runnable limpiarMensajeLabel = () -> NovusUtils.setMensaje("", this.recepcionCombustibleView.jLabel1);
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(limpiarMensajeLabel, 3, TimeUnit.SECONDS);
    }

    public void habilitarBoton(String valor) {
        if (valor.length() > 0) {
            this.recepcionCombustibleView.btnAgregar.setBackground(new Color(179, 15, 0));
        } else {
            this.recepcionCombustibleView.btnAgregar.setBackground(new Color(227, 227, 232));
        }
    }
}
