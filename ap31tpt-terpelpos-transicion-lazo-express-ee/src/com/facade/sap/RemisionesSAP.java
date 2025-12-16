/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.facade.sap;

import com.WT2.commons.domain.valueObject.LetterCase;
//import com.application.useCases.remision.ObtenerRemisionPorDeliveryUseCase;
import com.application.useCases.entradaCombustible.ObtenerTanquesRemisionUseCase;
import com.application.useCases.entradaCombustible.ObtenerInfoEntradaRemisionUseCase;
import com.application.useCases.entradaCombustible.ObtenerProductosSAPUseCase;
import com.bean.BodegaBean;
import com.bean.entradaCombustible.EntradaCombustibleBean;
import com.bean.entradaCombustible.ProductoSAP;
import com.controllers.NovusConstante;
import com.dao.EntradaCombustibleDao.EntradaCombustibleDao;
import com.application.useCases.entradaCombustible.ObtenerIdRemisionUseCase;
import com.dao.EntradaCombustibleDao.ProductoDivididoDao;
import com.firefuel.RecepcionCombustibleView;
import com.utils.enums.remisionesSAP.EstadoResmisiones;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import javax.swing.table.DefaultTableModel;
import com.application.useCases.entradaCombustible.ValidarSiRemisionEstaConfirmadaUseCase;
import com.application.useCases.entradaCombustible.ConfirmarRemisionUseCase;
import com.bean.entradaCombustible.ConfirmarRemisionParams;

/**
 *
 * @author Devitech
 */
public class RemisionesSAP {

    RecepcionCombustibleView combustibleView;
    private static final String ICONO_ERROR = "/com/firefuel/resources/btBad.png";
    private static final String PANEL_INFORMACION_GENERAL = "panelInformacionGeneral";
    boolean entrarOtravez;
 
    ObtenerTanquesRemisionUseCase obtenerTanquesRemisionUseCase;
    ObtenerInfoEntradaRemisionUseCase obtenerInfoEntradaRemisionUseCase;
    ObtenerProductosSAPUseCase obtenerProductosSAPUseCase;
    ObtenerIdRemisionUseCase obtenerIdRemisionUseCase;
    ValidarSiRemisionEstaConfirmadaUseCase validarSiRemisionEstaConfirmadaUseCase;
    ConfirmarRemisionUseCase confirmarRemisionUseCase;

    public RemisionesSAP(RecepcionCombustibleView combustibleView) {
        this.combustibleView = combustibleView;
        confirmarRemisionUseCase = new ConfirmarRemisionUseCase();
    }

    public EntradaCombustibleBean consultarRemision(String numeroRemision) {
        // 🚀 MIGRACIÓN: Usar caso de uso de Clean Architecture para validar confirmación
        if (validarSiRemisionEstaConfirmadaUseCase == null) {
            validarSiRemisionEstaConfirmadaUseCase = new ValidarSiRemisionEstaConfirmadaUseCase();
        }
        
        if (validarSiRemisionEstaConfirmadaUseCase.execute(numeroRemision)) {
            mensajes(PANEL_INFORMACION_GENERAL, "Número de Remisión ya ha sido Confirmado, por favor verifique".toUpperCase(), ICONO_ERROR);
            return null;
        } else {
            obtenerInfoEntradaRemisionUseCase = new ObtenerInfoEntradaRemisionUseCase();
            EntradaCombustibleBean combustibleBean = obtenerInfoEntradaRemisionUseCase.execute(numeroRemision);
            
            if (combustibleBean != null) {
                obtenerTanquesRemisionUseCase = new ObtenerTanquesRemisionUseCase();
                combustibleBean.setTanques(obtenerTanquesRemisionUseCase.execute(numeroRemision));

                // 🚀 MIGRACIÓN: Usar caso de uso de Clean Architecture para obtener productos SAP
                obtenerProductosSAPUseCase = new ObtenerProductosSAPUseCase();
                combustibleBean.setProductoSAP(obtenerProductosSAPUseCase.execute(combustibleBean.getIdRemision()));

                System.out.println("🔍 DEBUG: RemisionesSAP - Productos SAP llenados para remisión: " + numeroRemision);
            } else {
                validarInformacionHO(numeroRemision);
            }

            return combustibleBean;
        }
    }

    private void validarInformacionHO(String remision) {
        if (NovusConstante.HAY_INTERNET) {
            this.combustibleView.manual = false;
            Runnable enviarConsultaHO = () -> {
                ConsultarNumerosRemisionesHO consultarNumerosRemisionesHO = new ConsultarNumerosRemisionesHO();
                String respuesta = consultarNumerosRemisionesHO.consultarRemisionHO(remision);
                if (!respuesta.isEmpty()) {
                    Runnable cerrar = () -> this.combustibleView.cerrar();
                    this.combustibleView.showMessage(respuesta, "/com/firefuel/resources/btOk.png",
                            true, cerrar, true,
                            LetterCase.FIRST_UPPER_CASE);
                } else {
                    this.combustibleView.manual = true;
                    this.combustibleView.btnCerrarModal.setVisible(true);
                    this.mensajes(PANEL_INFORMACION_GENERAL, "Orden de Remisión no existe, Por favor verifique".toUpperCase(), ICONO_ERROR);
                }
            };
            CompletableFuture.runAsync(enviarConsultaHO);
            this.combustibleView.btnCerrarModal.setVisible(false);
            this.mensajes(PANEL_INFORMACION_GENERAL, "CONSULTANDO REMISIÓN EN HO", "/com/firefuel/resources/loader_fac.gif");
        } else {
            this.combustibleView.manual = true;
            mensajes(PANEL_INFORMACION_GENERAL, "Orden de Remisión no existe, Por favor verifique", ICONO_ERROR);
        }
    }

    public void validarRemision(EntradaCombustibleBean combustibleBean) {
        if (combustibleBean != null) {
            if (combustibleBean.getProductoSAP() == null || combustibleBean.getProductoSAP().isEmpty()) {
                mensajes(PANEL_INFORMACION_GENERAL, "Los productos de esta remisión están en descargue en curso", ICONO_ERROR);
            } else {
                mostrarInformacionSAP(combustibleBean);
            }
        }
    }

    private void mostrarInformacionSAP(EntradaCombustibleBean combustibleBean) {
        this.combustibleView.showPanel("detalleSAP");
        this.combustibleView.lblTituloRemision.setText("DETALLE DE N° DE ORDEN - " + combustibleBean.getDelivery());
        this.combustibleView.lblNumeroDocumentoSAP.setText(combustibleBean.getDelivery());
        this.combustibleView.lblFechaDocumento.setText(combustibleBean.getDocumentDate());
        this.combustibleView.lblGuiaTransporte.setText(combustibleBean.getWayBill());
        this.combustibleView.lblCentroLogistico.setText(combustibleBean.getLogisticCenter());
        this.combustibleView.lblCentroOrigen.setText(combustibleBean.getSupplyingCenter());
        DefaultTableModel defaultModel = (DefaultTableModel) this.combustibleView.jInformacionDescargue.getModel();
        defaultModel.getDataVector().removeAllElements();
        combustibleBean.getProductoSAP().forEach((key, producto) -> llenarInfoProducto(producto, combustibleBean.getTanques()));
    }

    private void llenarInfoProducto(ProductoSAP productoSAP, Map<String, ArrayList<BodegaBean>> tanques) {
        DefaultTableModel defaultModel = (DefaultTableModel) this.combustibleView.jInformacionDescargue.getModel();
        defaultModel.addRow(new Object[]{
            nombretanques(productoSAP, tanques),
            productoSAP.getDescripcion(),
            productoSAP.getQuantity(),
            productoSAP.getUnit(),
            productoSAP.getProduct()
        });
    }

    private String nombretanques(ProductoSAP productoSAP, Map<String, ArrayList<BodegaBean>> tanque) {
        String nombreBodega = "";
        String claveProducto = productoSAP.getClave();
        System.out.println("DEBUG: Buscando tanques para producto clave: " + claveProducto);
        boolean encontrado = false;

        if (claveProducto.startsWith("T-")) {
            ArrayList<BodegaBean> tanq = tanque.get(claveProducto);
            String idTanqueStr = claveProducto.substring(2);
            try {
                long idTanque = Long.parseLong(idTanqueStr);
                if (tanq != null && !tanq.isEmpty()) {
                    // Filtrar solo el tanque cuyo id coincida con xxxx
                    for (BodegaBean objBodegas : tanq) {
                        if (objBodegas.getId() == idTanque) {
                            System.out.println("DEBUG: [TANK ID FILTERED] Descripción tanque: " + objBodegas.getDescripcion());
                            nombreBodega = nombreBodega.concat(objBodegas.getDescripcion());
                            encontrado = true;
                            break;
                        }
                    }
                } else {
                    // Si no existe la clave T-xxxx, buscar en P-<producto_id> y filtrar por idTanque
                    String claveProductoId = "P-" + productoSAP.getProducID();
                    ArrayList<BodegaBean> tanqFallback = tanque.get(claveProductoId);
                    if (tanqFallback != null && !tanqFallback.isEmpty()) {
                        for (BodegaBean objBodegas : tanqFallback) {
                            if (objBodegas.getId() == idTanque) {
                                System.out.println("DEBUG: [FALLBACK TANK ID FILTERED] Descripción tanque: " + objBodegas.getDescripcion());
                                nombreBodega = nombreBodega.concat(objBodegas.getDescripcion());
                                encontrado = true;
                                break;
                            }
                        }
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("DEBUG: Error al parsear id de tanque en clave: " + claveProducto);
            }
            // Si no encontró por id tanque, mostrar todos los tanques del producto_id (fallback final)
            if (!encontrado) {
                String claveProductoId = "P-" + productoSAP.getProducID();
                tanq = tanque.get(claveProductoId);
                if (tanq != null && !tanq.isEmpty()) {
                    for (BodegaBean objBodegas : tanq) {
                        System.out.println("DEBUG: [PRODUCTO_ID FALLBACK] Descripción tanque: " + objBodegas.getDescripcion());
                        nombreBodega = nombreBodega.concat(objBodegas.getDescripcion() + ", ");
                    }
                    encontrado = true;
                }
            }
        } else if (claveProducto.startsWith("P-")) {
            ArrayList<BodegaBean> tanq = tanque.get(claveProducto);
            if (tanq != null && !tanq.isEmpty()) {
                for (BodegaBean objBodegas : tanq) {
                    System.out.println("DEBUG: [PRODUCTO_ID MATCH] Descripción tanque: " + objBodegas.getDescripcion());
                    nombreBodega = nombreBodega.concat(objBodegas.getDescripcion() + ", ");
                }
                encontrado = true;
            }
        }
        int longitudTexto = nombreBodega.length();
        System.out.println("DEBUG: nombreBodega resultante: '" + nombreBodega + "' (longitud: " + longitudTexto + ")");
        if (longitudTexto > 2 && nombreBodega.endsWith(", ")) {
            nombreBodega = nombreBodega.substring(0, longitudTexto - 2);
        }
        return nombreBodega;
    }

    public void continuar(EntradaCombustibleBean combustibleBean, String titulo) {
        boolean seguir = combustibleBean.getProductoSAP().isEmpty();
        if (seguir) {
            this.combustibleView.showPanel("panelIngresoEntradasFinales");
            this.combustibleView.jTitle.setText(titulo);
        } else {
            this.combustibleView.recepcion = null;
            this.combustibleView.showPanel("panelSeleccionTanques");
            this.combustibleView.jTitle.setText("RECEPCIÓN DE COMBUSTIBLE");
        }
        if (!combustibleBean.getProductoSAP().isEmpty()) {
            TreeMap<String, ProductoSAP> productosMap = (TreeMap<String, ProductoSAP>) combustibleBean.getProductoSAP();
            ArrayList<ProductoSAP> productos = new ArrayList<>(productosMap.values());
            ProductoSAP productoSAP = productos.get(0);
            // Asigna el producto SAP activo para la vista
            this.combustibleView.productoSAPActivo = fromProductoSAP(productoSAP);
            ArrayList<BodegaBean> bodegaBean = combustibleBean.getTanques().get(productoSAP.getClave());
            // Fallback: buscar por producto_id y filtrar por idTanque si es T-xxxx
            if (bodegaBean == null || bodegaBean.isEmpty()) {
                if (productoSAP.getClave().startsWith("T-")) {
                    String idTanqueStr = productoSAP.getClave().substring(2);
                    try {
                        long idTanque = Long.parseLong(idTanqueStr);
                        String claveProductoId = "P-" + productoSAP.getProducID();
                        ArrayList<BodegaBean> tanqFallback = combustibleBean.getTanques().get(claveProductoId);
                        if (tanqFallback != null && !tanqFallback.isEmpty()) {
                            bodegaBean = new ArrayList<>();
                            for (BodegaBean objBodegas : tanqFallback) {
                                if (objBodegas.getId() == idTanque) {
                                    bodegaBean.add(objBodegas);
                                    break;
                                }
                            }
                        }
                    } catch (NumberFormatException e) {
                        // Ignorar, no es un T-xxxx válido
                    }
                }
            }
            // Fallback final: mostrar todos los tanques del producto_id si sigue vacío
            if (bodegaBean == null || bodegaBean.isEmpty()) {
                String claveProductoId = "P-" + productoSAP.getProducID();
                bodegaBean = combustibleBean.getTanques().get(claveProductoId);
            }
            this.combustibleView.jcantidad.setText(productoSAP.getQuantity() + "");
            this.combustibleView.jcantidad.setEnabled(false);
            
            
            // NUEVO FLUJO: Siempre reconstruir optionTanksCombo solo con los tanques del producto actual
            this.combustibleView.optionTanksCombo = new TreeMap<>();
            if (bodegaBean != null && !bodegaBean.isEmpty()) {
                int idx = 0;
                for (BodegaBean tanque : bodegaBean) {
                    this.combustibleView.optionTanksCombo.put(idx++, tanque);
                }
                this.combustibleView.jcantidad.setVisible(true);
            }
            this.combustibleView.renderTanksSelect();
        }
    }

    public EntradaCombustibleBean devolderEntradaArealizar(EntradaCombustibleBean entradaCombustibleBean) {
        int[] seleccionadas = this.combustibleView.jInformacionDescargue.getSelectedRows();
        if (seleccionadas.length == entradaCombustibleBean.getProductoSAP().size()) {
            return entradaCombustibleBean;
        } else {
            entradaCombustibleBean.setProductoSAP(filtrarSoloSeleccionados(seleccionadas, entradaCombustibleBean.getProductoSAP()));
            return entradaCombustibleBean;
        }
    }

    // Nuevo método para filtrar los productos seleccionados
    public Map<String, ProductoSAP> filtrarSoloSeleccionados(int[] indices, Map<String, ProductoSAP> productos) {
        TreeMap<String, ProductoSAP> productosMap = (TreeMap<String, ProductoSAP>) productos;
        ArrayList<ProductoSAP> productosList = new ArrayList<>(productosMap.values());
        productos.clear();
        for (int idx : indices) {
            ProductoSAP productoSAP = productosList.get(idx);
            productos.put(productoSAP.getClave(), productoSAP);
        }
        return productos;
    }

    public boolean validarSeleccion() {
        return obtenerSelecciones().length >= 1;
    }

    private int[] obtenerSelecciones() {
        return this.combustibleView.jInformacionDescargue.getSelectedRows();
    }

    public EntradaCombustibleBean eliminarRemisionUsada(EntradaCombustibleBean combustibleBean) {
        TreeMap<String, ProductoSAP> productosMap = (TreeMap<String, ProductoSAP>) combustibleBean.getProductoSAP();
        ArrayList<ProductoSAP> productos = new ArrayList<>(productosMap.values());
        if (!productos.isEmpty()) {
            ProductoSAP productoSAP = productos.get(0);
            combustibleBean.getTanques().remove(productoSAP.getClave());
            combustibleBean.getProductoSAP().remove(productoSAP.getClave());
            cambiarEstadoDelaEntrada(productoSAP.getProducID(), combustibleBean.getIdRemision(), productoSAP.getIdRemisionProducto(), EstadoResmisiones.EN_USO.getEstado());
        }
        return combustibleBean;
    }

    private void cambiarEstadoDelaEntrada(int producto, Long idRemision, long idRemisionProducto, int estado) {
        EntradaCombustibleDao entradaCombustibleDao = new EntradaCombustibleDao();
        if (entradaCombustibleDao.validarSihayProductoDividido(producto, idRemisionProducto, idRemision)) {
            long idTanque = this.combustibleView.obtenerTanqueSeleccionado().getId();
            cambiarEstadoDelaEntradaDividida(producto, idRemision, idTanque, idRemisionProducto, estado);
        } else {
            procesoFinalDeCambiarEstdoDelaEntrada(producto, idRemision, estado);
        }

    }

    private void procesoFinalDeCambiarEstdoDelaEntrada(int producto, Long idRemision, int estado) {
        EntradaCombustibleDao entradaCombustibleDao = new EntradaCombustibleDao();
        entradaCombustibleDao.marcarProductoDescargueComoUsado(producto, idRemision, estado);
    }

    private void cambiarEstadoDelaEntradaDividida(int idProducto, long idRemision, long idTanque, long idRemisionProducto, int estado) {
        ProductoDivididoDao productoDivididoDao = new ProductoDivididoDao();
        productoDivididoDao.actualizarEstadoProductoDividido(estado, idRemisionProducto, idProducto, idRemision, idTanque);
    }

    public void seleccionarTodo() {
        int rows = this.combustibleView.jInformacionDescargue.getRowCount() - 1;
        this.combustibleView.jInformacionDescargue.setRowSelectionInterval(0, rows);
    }

    public EntradaCombustibleBean obtenerProductoDescargue(EntradaCombustibleBean combustibleBean) {
        return combustibleBean;
    }

    public void limpiarCampos() {
        this.combustibleView.jaltura_final.setText("");
        this.combustibleView.jagua_inicial.setText("");
        this.combustibleView.jaltura_inicial.setText("");
        this.combustibleView.jaltura_final.setText("");
        this.combustibleView.jvolumen_final.setText("");
        this.combustibleView.jvolumen_inicial.setText("");
        this.combustibleView.jrecibido.setText("");
        this.combustibleView.jdensidad.setText("");
        this.combustibleView.jLabel35.setText("");
        this.combustibleView.jLabel37.setText("");
    }

    public boolean getEntrarOtravez() {
        return this.entrarOtravez;
    }

    public void setEntrarOtravez(boolean entrarOtravez) {
        this.entrarOtravez = entrarOtravez;
    }

    public void confirmarEntrada(EntradaCombustibleBean combustibleBean, int idProducto) {
        EntradaCombustibleDao combustibleDao = new EntradaCombustibleDao();
        ProductoDivididoDao productoDivididoDao = new ProductoDivididoDao();
        TreeMap<String, Long> idtanqueIdRemisionProducto = (TreeMap<String, Long>) productoDivididoDao.obtenerIdTanqueyIdRemisionProducto(idProducto, combustibleBean.getIdRemision());
        long idTanque = this.combustibleView.obtenerTanqueSeleccionado().getId();
        long idRemisionProducto = idtanqueIdRemisionProducto.get("idRemisionProducto");
        if (combustibleDao.validarSihayProductoDividido(idProducto, idRemisionProducto, combustibleBean.getIdRemision())) {
            confirmarProductoDivididoEntrada(idProducto, idRemisionProducto, combustibleBean.getIdRemision(), idTanque, combustibleBean);
        } else {
            procesoFinalDeconfirmacion(combustibleBean, idProducto);
        }
    }

    private void confirmarProductoDivididoEntrada(int idProducto, long idRemisionProducto, long idRemisionSap, long idTanque, EntradaCombustibleBean combustibleBean) {
        ProductoDivididoDao productoDivididoDao = new ProductoDivididoDao();
        int cantidadProductoDividido = productoDivididoDao.cantidadDeProductoDividido(idProducto, idRemisionSap, idRemisionProducto);
        if (cantidadProductoDividido <= 1) {
            productoDivididoDao.actualizarEstadoProductoDividido(EstadoResmisiones.FINALIZADA.getEstado(), idRemisionProducto, idProducto, idRemisionSap, idTanque);
            procesoFinalDeconfirmacion(combustibleBean, idProducto);
        } else {
            productoDivididoDao.actualizarEstadoProductoDividido(EstadoResmisiones.FINALIZADA.getEstado(), idRemisionProducto, idProducto, idRemisionSap, idTanque);
        }

    }

    private void procesoFinalDeconfirmacion(EntradaCombustibleBean combustibleBean, int idProducto) {
        EntradaCombustibleDao combustibleDao = new EntradaCombustibleDao();
        procesoFinalDeCambiarEstdoDelaEntrada(idProducto, combustibleBean.getIdRemision(), EstadoResmisiones.FINALIZADA.getEstado());
        if (combustibleDao.remisionConfirmada(combustibleBean.getIdRemision())) {
            // 🚀 MIGRACIÓN: Usar caso de uso de Clean Architecture para confirmar remisión
            confirmarRemisionUseCase.finalizarRemision(combustibleBean.getIdRemision());
            EnviarConfirmacionRemisionHO enviarConfirmacionRemisionHO = new EnviarConfirmacionRemisionHO();
            enviarConfirmacionRemisionHO.enviarConfirmacion(combustibleBean.getDelivery());
        }
    }

    public Long obtenerIdRemision(String numeroRemision) {
        // 🚀 MIGRACIÓN: Usar caso de uso de Clean Architecture
        if (obtenerIdRemisionUseCase == null) {
            obtenerIdRemisionUseCase = new ObtenerIdRemisionUseCase();
        }
        return obtenerIdRemisionUseCase.execute(numeroRemision);
    }

    public void mensajes(String panel, String mensaje, String icono) {
        this.combustibleView.panelAmostrar = panel;
        this.combustibleView.jtext.setText("<html>" + mensaje + "</html>");
        this.combustibleView.jIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource(icono)));
        this.combustibleView.showPanel("mensajeNotificacion");
    }

    // Método adaptador para convertir ProductoSAP a ProductoBean
    public static com.bean.ProductoBean fromProductoSAP(ProductoSAP sap) {
        com.bean.ProductoBean bean = new com.bean.ProductoBean();
        bean.setId(sap.getProducID());
        bean.setDescripcion(sap.getDescripcion());
        bean.setCantidad(sap.getQuantity());
        bean.setPrecio(sap.getSalesCostValue());
        bean.setUnidades_medida(sap.getUnit());
        bean.setIdRemisionProducto(sap.getIdRemisionProducto());
        return bean;
    }

}
