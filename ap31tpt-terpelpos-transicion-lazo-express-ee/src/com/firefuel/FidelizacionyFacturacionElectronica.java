/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.firefuel;

import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.WT2.loyalty.domain.entities.beans.FoundClient;
import com.application.useCases.ventas.IsVentaFidelizadaUseCase;
import com.bean.AsignacionClienteBean;
import com.bean.ReciboExtended;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.EquipoDao;
import com.dao.SurtidorDao;
import com.firefuel.asignarCliente.beans.InformacionVentaCliente;
import com.firefuel.components.panelesPersonalizados.ComboBox;
import com.firefuel.facturacion.electronica.FacturacionElectronica;
import com.firefuel.facturacion.electronica.TiposDocumentos;
import com.firefuel.fidelizacionYfacturaElectronica.RenderizarProcesosFidelizacionyFE;
import com.firefuel.utils.ShowMessageSingleton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.neo.app.bean.Recibo;
import java.awt.Color;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import teclado.view.common.TecladoExtendidoGray;
import com.application.useCases.wacherparametros.FindByParameterUseCase;
import com.application.commons.CtWacherParametrosEnum;
import com.application.useCases.productos.GetProductNameUseCase;
import com.application.useCases.sutidores.GetMovimientoIdDesdeCaraUseCase;
import com.application.useCases.tbltransaccionproceso.FinByTransaccionProcesoUseCase;

/**
 *
 * @author Devitech
 */
public class FidelizacionyFacturacionElectronica extends javax.swing.JPanel {

    public JsonObject datosClienteFE;
    public JsonObject datosClienteFidelizacion;
    public JsonObject respuestaFactura;
    Color inactivo = new Color(163, 164, 164);
    Color activo = new Color(255, 255, 255);
    FindByParameterUseCase findByParameterUseCase = new FindByParameterUseCase(CtWacherParametrosEnum.CODIGO.getColumnName(), "REMISION");

    private static final String ERROR = "error";
    private static final String RUTA_NO_CHECK = "/com/firefuel/resources/undoCheck.png";
    private static final String ICONO_ERROR = "/com/firefuel/resources/btBad.png";
    private static final String NOMBRE_CLIENTE = "nombreCliente";
    private String identificadorPuntoDeVenta;

    String caracteresPermitidos = "[0-9]";
    int cantidadCaracteres = 10;
    String modificarDatos = "facturacionElectronica";

    private ReciboExtended recibo;
    private Runnable regresar;
    private Runnable enviar;
    private boolean asignarDatos;
    private boolean fidelizacion;
    private boolean ventaMarket;
    private boolean pagoHasBonoViveTerpel;
    private InformacionVentaCliente informacionVentaCliente;
    private String numeroDocumento;
    private String tipoDocumento;

    public FidelizacionyFacturacionElectronica() {
        initComponents();
    }

    public void iniciarProceso() {
        init();
        consultarCliente(numeroDocumento, tipoDocumento, fidelizacion);
        validarBotonFidelizacion(tipoDocumento);
        if (asignarDatos) {
            obtenerDatosVehiculo();
            validarTiempoFidelizacion();
            validarExisteFidelizacion();
        }
    }

    public FidelizacionyFacturacionElectronica(ReciboExtended recibo, String numeroDocumento, String tipoDocumento, Runnable regresar, boolean fidelizacion) {
        initComponents();
        this.fidelizacion = fidelizacion;
        chechFacturacionElectronica.setEnabled(!fidelizacion);
        modificarVistaVentaCurso();
        init();
        consultarCliente(numeroDocumento, tipoDocumento, fidelizacion);
        validarBotonFidelizacion(tipoDocumento);
        this.regresar = regresar;
        this.recibo = recibo;
    }

    public FidelizacionyFacturacionElectronica(ReciboExtended recibo,
            String numeroDocumento,
            String tipoDocumento,
            Runnable regresar,
            boolean fidelizacion,
            Runnable enviar,
            boolean ventaMarket,
            JsonObject respuestaFactura
    ) {
        this.respuestaFactura = respuestaFactura;
        initComponents();
        modificarVistaVentaCurso();
        this.enviar = enviar;
        this.ventaMarket = ventaMarket;
        this.fidelizacion = fidelizacion;
        chechFacturacionElectronica.setEnabled(!fidelizacion);
        init();
        consultarCliente(numeroDocumento, tipoDocumento, fidelizacion);
        validarBotonFidelizacion(tipoDocumento);
        this.regresar = regresar;
        this.recibo = recibo;
    }

    public FidelizacionyFacturacionElectronica(ReciboExtended recibo,
            String numeroDocumento,
            String tipoDocumento,
            Runnable regresar,
            boolean fidelizacion,
            boolean asignarDatos,
            InformacionVentaCliente informacionVentaCliente, Runnable enviar) {
        initComponents();
        this.fidelizacion = fidelizacion;
        chechFacturacionElectronica.setEnabled(!fidelizacion);
        init();
        consultarCliente(numeroDocumento, tipoDocumento, fidelizacion);
        validarBotonFidelizacion(tipoDocumento);
        this.regresar = regresar;
        this.recibo = recibo;
        this.asignarDatos = asignarDatos;
        this.informacionVentaCliente = informacionVentaCliente;
        this.enviar = enviar;
        obtenerDatosVehiculo();
        validarTiempoFidelizacion();
    }

    private void validatePagoHasBonoViveTerpel () {
        JsonArray pagos = (respuestaFactura != null && respuestaFactura.has("datos_FE") && respuestaFactura.getAsJsonObject("datos_FE").has("pagos"))
                ? respuestaFactura.getAsJsonObject("datos_FE").getAsJsonArray("pagos")
                : null;

        if (pagos != null) {
            for (JsonElement pagoElement : pagos) {
                JsonObject pago = pagoElement.getAsJsonObject();
                int medioPago = Integer.parseInt(String.valueOf(pago.get("identificacionMediosPagos")));

                if(medioPago == NovusConstante.ID_MEDIO_BONO_TERPEL) {
                    pagoHasBonoViveTerpel = true;
                    checkFidelizacion.setSelected(false);
                    checkFidelizacion.setEnabled(false);
                    checkFidelizacion.setBackground(inactivo);
                    btnModificarFidelizacion.setEnabled(false);
                    btnModificarFidelizacion.setBackground(inactivo);
                    lblMensaje.setText("No puedes fidelizar porque estas usando un bono vive terpel");
                    return;
                }
            }
        } else {
            System.out.println("No hay pagos disponibles.");
        }
    }


    // private void init() {
    //     EquipoDao equipoDao = new EquipoDao();
    //     ComboBox comboBox = new ComboBox();
    //     btnConsultar.setBackground(inactivo);
    //     JComboTipoDocumentos.setUI(comboBox.createUI(JComboTipoDocumentos));
    //     JComboTipoDocumentos.setBackground(new java.awt.Color(255, 255, 255));
    //     placheholder(txtNumeroDocumento, "NÚMERO DE DOCUMENTO");
    //     placheholder(txtTipoIdentificaion, "");
    //     llenarCampoTipoIdentificacion();
    //     txtDocumento.setEditable(false);
    //     txtTipoIdentificaion.setEditable(false);
    //     this.identificadorPuntoDeVenta = equipoDao.getIdentificacionPuntoVenta();
    //     RenderizarProcesosFidelizacionyFE renderizarProcesosFidelizacionyFE = new RenderizarProcesosFidelizacionyFE();
    //     FacturacionElectronica fac = new FacturacionElectronica();
    //     boolean aplicaFE = fac.aplicaFE();
    //     MovimientosDao mdao = new MovimientosDao();
    //     boolean botonActivo = !aplicaFE && !mdao.remisionActiva();

    //     if (!botonActivo) {
    //         chechFacturacionElectronica.setVisible(!this.fidelizacion);
    //         btnModificarDatos.setVisible(!fidelizacion);
    //     } else {
    //         chechFacturacionElectronica.setVisible(false);
    //         btnModificarDatos.setVisible(false);
    //     }

    //     ajustarAlturaConfirmacion(this.fidelizacion || botonActivo);
    //     cargarImagen(chechFacturacionElectronica);
    //     cargarImagen(checkFidelizacion);
    //     renderizarProcesosFidelizacionyFE.cambiarPanel(pnlProcesosClientes, "pnlProcesoNormal");
    //     btnCancelar.setVisible(false);
    //     lblMensaje.setText("");
    // }
    private void init() {
        EquipoDao equipoDao = new EquipoDao();
        ComboBox comboBox = new ComboBox();
        btnConsultar.setBackground(inactivo);
        JComboTipoDocumentos.setUI(comboBox.createUI(JComboTipoDocumentos));
        JComboTipoDocumentos.setBackground(new java.awt.Color(255, 255, 255));
        placheholder(txtNumeroDocumento, "NÚMERO DE DOCUMENTO");
        placheholder(txtTipoIdentificaion, "");
        llenarCampoTipoIdentificacion();
        txtDocumento.setEditable(false);
        txtTipoIdentificaion.setEditable(false);
        this.identificadorPuntoDeVenta = equipoDao.getIdentificacionPuntoVenta();
        RenderizarProcesosFidelizacionyFE renderizarProcesosFidelizacionyFE = new RenderizarProcesosFidelizacionyFE();
        FacturacionElectronica fac = new FacturacionElectronica();
        boolean aplicaFE = fac.aplicaFE();
        boolean botonActivo = !aplicaFE && !findByParameterUseCase.execute();

        if (!botonActivo) {
            chechFacturacionElectronica.setVisible(!this.fidelizacion);
            btnModificarDatos.setVisible(!fidelizacion);
        } else {
            chechFacturacionElectronica.setVisible(false);
            btnModificarDatos.setVisible(false);
        }

        ajustarAlturaConfirmacion(this.fidelizacion || botonActivo);
        cargarImagen(chechFacturacionElectronica);
        cargarImagen(checkFidelizacion);
        renderizarProcesosFidelizacionyFE.cambiarPanel(pnlProcesosClientes, "pnlProcesoNormal");
        btnCancelar.setVisible(false);
        lblMensaje.setText("");
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        comboBox1 = new com.firefuel.components.panelesPersonalizados.ComboBox();
        comboBox2 = new com.firefuel.components.panelesPersonalizados.ComboBox();
        panelRedondo1 = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        panelRedondo4 = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        pnlPrincipal = new javax.swing.JPanel();
        procesoFidelizacionYFacturacion = new javax.swing.JPanel();
        lblRegresar = new javax.swing.JLabel();
        lblTitulo = new javax.swing.JLabel();
        btnConsultar = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblConsultar = new javax.swing.JLabel();
        btnCancelar = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblCancelar = new javax.swing.JLabel();
        btnContinuar = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lbCContinuar = new javax.swing.JLabel();
        Informacion = new javax.swing.JLabel();
        pnlProcesosClientes = new javax.swing.JPanel();
        pnlProcesoNormal = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        pnlOperaciones = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jLabel6 = new javax.swing.JLabel();
        checkFidelizacion = new javax.swing.JCheckBox();
        chechFacturacionElectronica = new javax.swing.JCheckBox();
        btnModificarDatos = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblModificarDatos = new javax.swing.JLabel();
        btnModificarFidelizacion = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblModificarFidelizacion = new javax.swing.JLabel();
        lblMensaje = new javax.swing.JLabel();
        pnlInformacionCliente = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        txtTipoIdentificaion = new javax.swing.JTextField();
        txtDocumento = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        pnlContenedorProcesos = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblProcesos = new javax.swing.JLabel();
        lblCliente = new javax.swing.JLabel();
        nombreClienteFE = new javax.swing.JLabel();
        nombreClienteFidelizacion = new javax.swing.JLabel();
        pnlAsignarDatosVehiculo = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        pnlOpcionDatosVehiculo = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        btnAgregarDatos = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblModificarDatos1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        pnlDatosVehiculoIngresado = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jLabel7 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        pnlPlaca = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jLabel15 = new javax.swing.JLabel();
        infoPlaca = new javax.swing.JTextField();
        pnlKm = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jLabel16 = new javax.swing.JLabel();
        infoKm = new javax.swing.JTextField();
        pnlOrden = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jLabel17 = new javax.swing.JLabel();
        infoOrden = new javax.swing.JTextField();
        btnEditarDatos = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblIconEdit = new javax.swing.JLabel();
        pnlProcesoCambioCliente = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        pnlTeclado = new TecladoExtendidoGray();
        pnlDatosClientes = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        txtNumeroDocumento = new javax.swing.JTextField();
        JComboTipoDocumentos = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblMensajeInfo = new javax.swing.JLabel();
        pnlLoad = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblInformacionEsperar1 = new javax.swing.JLabel();
        lblIcono1 = new javax.swing.JLabel();
        pnlConfirmacionComprobante = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        pnlPregunta = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jLabel3 = new javax.swing.JLabel();
        btnNO = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblNO = new javax.swing.JLabel();
        btnSI = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblSI = new javax.swing.JLabel();
        pnlMensajeClienteFe = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        pnlInformacion = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblInformacionMensajeErrorFe = new javax.swing.JLabel();
        panelRedondo2 = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jLabel4 = new javax.swing.JLabel();
        lblFondo = new javax.swing.JLabel();
        loader = new javax.swing.JPanel();
        lblInformacionEsperar = new javax.swing.JLabel();
        lblIcono = new javax.swing.JLabel();
        lblFondoLoader = new javax.swing.JLabel();
        pnlDatosVehiculo = new javax.swing.JPanel();
        lblRegresarDatosVehiculo = new javax.swing.JLabel();
        lblTituloDatos = new javax.swing.JLabel();
        pnlContenedorRegistro = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        pnlBorde = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        pnlContenedor = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        pnlRegistroPlaca = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jLabel11 = new javax.swing.JLabel();
        panelRedondo5 = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        panelRedondo6 = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        txtPlaca = new javax.swing.JTextField();
        pnlRegistroKm = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jLabel12 = new javax.swing.JLabel();
        panelRedondo7 = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        panelRedondo8 = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        txtKms = new javax.swing.JTextField();
        pnlRegistroAutorizacion = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jLabel13 = new javax.swing.JLabel();
        panelRedondo9 = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        panelRedondo10 = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        txtAutorizacion = new javax.swing.JTextField();
        pnlRegistroNoVehiculo = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        jLabel14 = new javax.swing.JLabel();
        panelRedondo11 = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        panelRedondo12 = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        txtNoVehiculo = new javax.swing.JTextField();
        btnAgregarDatosVehiculo = new com.firefuel.components.panelesPersonalizados.PanelRedondo();
        lblAgregarDatos = new javax.swing.JLabel();
        jPanel1 = new TecladoExtendidoGray();
        jNotificacion = new javax.swing.JLabel();
        lblFondoDatosVehiculo = new javax.swing.JLabel();

        panelRedondo1.setName("panelRedondo1"); // NOI18N

        panelRedondo4.setName("panelRedondo4"); // NOI18N

        setLayout(null);

        pnlPrincipal.setName("pnlPrincipal"); // NOI18N
        pnlPrincipal.setLayout(new java.awt.CardLayout());

        procesoFidelizacionYFacturacion.setName("procesoFidelizacionYFacturacion"); // NOI18N
        procesoFidelizacionYFacturacion.setLayout(null);

        lblRegresar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRegresar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt_back_white.png"))); // NOI18N
        lblRegresar.setName("lblRegresar"); // NOI18N
        lblRegresar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblRegresarMouseClicked(evt);
            }
        });
        procesoFidelizacionYFacturacion.add(lblRegresar);
        lblRegresar.setBounds(30, 10, 70, 60);

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblTitulo.setForeground(new java.awt.Color(255, 255, 255));
        lblTitulo.setText("PROCESOS DE VENTA");
        lblTitulo.setName("lblTitulo"); // NOI18N
        procesoFidelizacionYFacturacion.add(lblTitulo);
        lblTitulo.setBounds(120, 10, 580, 60);

        btnConsultar.setBackground(new java.awt.Color(164, 162, 163));
        btnConsultar.setName("btnConsultar"); // NOI18N
        btnConsultar.setRoundBottomLeft(30);
        btnConsultar.setRoundBottomRight(30);
        btnConsultar.setRoundTopLeft(30);
        btnConsultar.setRoundTopRight(30);
        btnConsultar.setLayout(null);

        lblConsultar.setBackground(new java.awt.Color(164, 162, 163));
        lblConsultar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblConsultar.setForeground(new java.awt.Color(228, 30, 19));
        lblConsultar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblConsultar.setText("CONSULTAR");
        lblConsultar.setName("lblConsultar"); // NOI18N
        lblConsultar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblConsultarMouseClicked(evt);
            }
        });
        btnConsultar.add(lblConsultar);
        lblConsultar.setBounds(0, 0, 280, 60);

        procesoFidelizacionYFacturacion.add(btnConsultar);
        btnConsultar.setBounds(650, 720, 280, 60);

        btnCancelar.setBackground(new java.awt.Color(255, 255, 255));
        btnCancelar.setName("btnCancelar"); // NOI18N
        btnCancelar.setRoundBottomLeft(30);
        btnCancelar.setRoundBottomRight(30);
        btnCancelar.setRoundTopLeft(30);
        btnCancelar.setRoundTopRight(30);
        btnCancelar.setLayout(null);

        lblCancelar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblCancelar.setForeground(new java.awt.Color(228, 30, 19));
        lblCancelar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCancelar.setText("CANCELAR");
        lblCancelar.setName("lblCancelar"); // NOI18N
        lblCancelar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblCancelarMouseClicked(evt);
            }
        });
        btnCancelar.add(lblCancelar);
        lblCancelar.setBounds(0, 0, 280, 60);

        procesoFidelizacionYFacturacion.add(btnCancelar);
        btnCancelar.setBounds(350, 720, 280, 60);

        btnContinuar.setBackground(new java.awt.Color(255, 255, 255));
        btnContinuar.setName("btnContinuar"); // NOI18N
        btnContinuar.setRoundBottomLeft(30);
        btnContinuar.setRoundBottomRight(30);
        btnContinuar.setRoundTopLeft(30);
        btnContinuar.setRoundTopRight(30);
        btnContinuar.setLayout(null);

        lbCContinuar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbCContinuar.setForeground(new java.awt.Color(228, 30, 19));
        lbCContinuar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbCContinuar.setText("CONTINUAR");
        lbCContinuar.setName("lbCContinuar"); // NOI18N
        lbCContinuar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbCContinuarMouseClicked(evt);
            }
        });
        btnContinuar.add(lbCContinuar);
        lbCContinuar.setBounds(0, 0, 280, 60);

        procesoFidelizacionYFacturacion.add(btnContinuar);
        btnContinuar.setBounds(950, 720, 280, 60);

        Informacion.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        Informacion.setForeground(new java.awt.Color(255, 255, 255));
        Informacion.setName("Informacion"); // NOI18N
        procesoFidelizacionYFacturacion.add(Informacion);
        Informacion.setBounds(707, 10, 550, 60);

        pnlProcesosClientes.setName("pnlProcesosClientes"); // NOI18N
        pnlProcesosClientes.setOpaque(false);
        pnlProcesosClientes.setLayout(new java.awt.CardLayout());

        pnlProcesoNormal.setBackground(new java.awt.Color(220, 220, 220));
        pnlProcesoNormal.setName("pnlProcesoNormal"); // NOI18N
        pnlProcesoNormal.setRoundBottomLeft(30);
        pnlProcesoNormal.setRoundBottomRight(30);
        pnlProcesoNormal.setRoundTopLeft(30);
        pnlProcesoNormal.setRoundTopRight(30);
        pnlProcesoNormal.setLayout(null);

        pnlOperaciones.setBackground(new java.awt.Color(255, 255, 255));
        pnlOperaciones.setName("pnlOperaciones"); // NOI18N
        pnlOperaciones.setRoundBottomLeft(30);
        pnlOperaciones.setRoundBottomRight(30);
        pnlOperaciones.setRoundTopLeft(30);
        pnlOperaciones.setRoundTopRight(30);
        pnlOperaciones.setLayout(null);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(51, 51, 51));
        jLabel6.setText("¿Qué proceso quiere realizar?");
        jLabel6.setName("jLabel6"); // NOI18N
        pnlOperaciones.add(jLabel6);
        jLabel6.setBounds(20, 0, 380, 40);

        checkFidelizacion.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        checkFidelizacion.setForeground(new java.awt.Color(51, 51, 51));
        checkFidelizacion.setText("FIDELIZACIÓN");
        checkFidelizacion.setName("checkFidelizacion"); // NOI18N
        checkFidelizacion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                checkFidelizacionMouseClicked(evt);
            }
        });
        pnlOperaciones.add(checkFidelizacion);
        checkFidelizacion.setBounds(20, 55, 230, 49);

        chechFacturacionElectronica.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        chechFacturacionElectronica.setForeground(new java.awt.Color(51, 51, 51));
        chechFacturacionElectronica.setText("FACTURACIÓN ELECTRÓNICA");
        chechFacturacionElectronica.setName("chechFacturacionElectronica"); // NOI18N
        chechFacturacionElectronica.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                chechFacturacionElectronicaMouseClicked(evt);
            }
        });
        pnlOperaciones.add(chechFacturacionElectronica);
        chechFacturacionElectronica.setBounds(20, 180, 370, 49);

        btnModificarDatos.setBackground(new java.awt.Color(228, 30, 19));
        btnModificarDatos.setName("btnModificarDatos"); // NOI18N
        btnModificarDatos.setRoundBottomLeft(30);
        btnModificarDatos.setRoundBottomRight(30);
        btnModificarDatos.setRoundTopLeft(30);
        btnModificarDatos.setRoundTopRight(30);
        btnModificarDatos.setLayout(null);

        lblModificarDatos.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        lblModificarDatos.setForeground(new java.awt.Color(255, 255, 255));
        lblModificarDatos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblModificarDatos.setText("MODIFICAR DATOS FACTURACIÓN ELECTRÓNICA");
        lblModificarDatos.setName("lblModificarDatos"); // NOI18N
        lblModificarDatos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblModificarDatosMouseClicked(evt);
            }
        });
        btnModificarDatos.add(lblModificarDatos);
        lblModificarDatos.setBounds(0, 0, 580, 50);

        pnlOperaciones.add(btnModificarDatos);
        btnModificarDatos.setBounds(10, 240, 580, 50);

        btnModificarFidelizacion.setBackground(new java.awt.Color(228, 30, 19));
        btnModificarFidelizacion.setName("btnModificarFidelizacion"); // NOI18N
        btnModificarFidelizacion.setRoundBottomLeft(30);
        btnModificarFidelizacion.setRoundBottomRight(30);
        btnModificarFidelizacion.setRoundTopLeft(30);
        btnModificarFidelizacion.setRoundTopRight(30);
        btnModificarFidelizacion.setLayout(null);

        lblModificarFidelizacion.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        lblModificarFidelizacion.setForeground(new java.awt.Color(255, 255, 255));
        lblModificarFidelizacion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblModificarFidelizacion.setText("MODIFICAR DATOS DE FIDELIZACIÓN");
        lblModificarFidelizacion.setName("lblModificarFidelizacion"); // NOI18N
        lblModificarFidelizacion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblModificarFidelizacionMouseClicked(evt);
            }
        });
        btnModificarFidelizacion.add(lblModificarFidelizacion);
        lblModificarFidelizacion.setBounds(0, 0, 580, 50);
        lblModificarFidelizacion.getAccessibleContext().setAccessibleName("MODIFICAR DATOS DE FIDELIZACION");

        pnlOperaciones.add(btnModificarFidelizacion);
        btnModificarFidelizacion.setBounds(10, 110, 580, 50);

        lblMensaje.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblMensaje.setForeground(new java.awt.Color(228, 30, 19));
        lblMensaje.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblMensaje.setName("lblMensaje"); // NOI18N
        pnlOperaciones.add(lblMensaje);
        lblMensaje.setBounds(20, 160, 560, 22);

        pnlProcesoNormal.add(pnlOperaciones);
        pnlOperaciones.setBounds(610, 30, 600, 310);

        pnlInformacionCliente.setBackground(new java.awt.Color(255, 255, 255));
        pnlInformacionCliente.setName("pnlInformacionCliente"); // NOI18N
        pnlInformacionCliente.setRoundBottomLeft(30);
        pnlInformacionCliente.setRoundBottomRight(30);
        pnlInformacionCliente.setRoundTopLeft(30);
        pnlInformacionCliente.setRoundTopRight(30);
        pnlInformacionCliente.setLayout(null);

        txtTipoIdentificaion.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        txtTipoIdentificaion.setBorder(new com.firefuel.components.panelesPersonalizados.BordesRedondos());
        txtTipoIdentificaion.setName("txtTipoIdentificaion"); // NOI18N
        pnlInformacionCliente.add(txtTipoIdentificaion);
        txtTipoIdentificaion.setBounds(20, 50, 560, 50);

        txtDocumento.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        txtDocumento.setBorder(new com.firefuel.components.panelesPersonalizados.BordesRedondos());
        txtDocumento.setName("txtDocumento"); // NOI18N
        pnlInformacionCliente.add(txtDocumento);
        txtDocumento.setBounds(20, 130, 560, 50);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(51, 51, 51));
        jLabel8.setText("TIPO DE IDENTIFICACIÓN");
        jLabel8.setName("jLabel8"); // NOI18N
        pnlInformacionCliente.add(jLabel8);
        jLabel8.setBounds(20, 10, 350, 40);

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(51, 51, 51));
        jLabel9.setText("NÚMERO DE DOCUMENTO");
        jLabel9.setName("jLabel9"); // NOI18N
        pnlInformacionCliente.add(jLabel9);
        jLabel9.setBounds(20, 95, 230, 40);

        pnlProcesoNormal.add(pnlInformacionCliente);
        pnlInformacionCliente.setBounds(610, 350, 600, 190);

        pnlContenedorProcesos.setBackground(new java.awt.Color(255, 255, 255));
        pnlContenedorProcesos.setForeground(new java.awt.Color(102, 102, 102));
        pnlContenedorProcesos.setName("pnlContenedorProcesos"); // NOI18N
        pnlContenedorProcesos.setRoundBottomLeft(30);
        pnlContenedorProcesos.setRoundBottomRight(30);
        pnlContenedorProcesos.setRoundTopLeft(30);
        pnlContenedorProcesos.setRoundTopRight(30);
        pnlContenedorProcesos.setLayout(null);

        lblProcesos.setBackground(new java.awt.Color(255, 51, 51));
        lblProcesos.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblProcesos.setForeground(new java.awt.Color(228, 30, 19));
        lblProcesos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProcesos.setText("FACTURA ELECTRÓNICA");
        lblProcesos.setName("lblProcesos"); // NOI18N
        pnlContenedorProcesos.add(lblProcesos);
        lblProcesos.setBounds(10, 200, 530, 50);

        lblCliente.setBackground(new java.awt.Color(255, 51, 51));
        lblCliente.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblCliente.setForeground(new java.awt.Color(228, 30, 19));
        lblCliente.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCliente.setText("ACUMULA PUNTOS");
        lblCliente.setName("lblCliente"); // NOI18N
        pnlContenedorProcesos.add(lblCliente);
        lblCliente.setBounds(10, 40, 530, 50);

        nombreClienteFE.setBackground(new java.awt.Color(0, 0, 0));
        nombreClienteFE.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        nombreClienteFE.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nombreClienteFE.setText("JHOEYKOL STIVEN CERA ALTAMAR");
        nombreClienteFE.setName("nombreClienteFE"); // NOI18N
        pnlContenedorProcesos.add(nombreClienteFE);
        nombreClienteFE.setBounds(10, 270, 530, 50);

        nombreClienteFidelizacion.setBackground(new java.awt.Color(0, 0, 0));
        nombreClienteFidelizacion.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        nombreClienteFidelizacion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nombreClienteFidelizacion.setText("JHOEYKOL STIVEN CERA ALTAMAR");
        nombreClienteFidelizacion.setName("nombreClienteFidelizacion"); // NOI18N
        pnlContenedorProcesos.add(nombreClienteFidelizacion);
        nombreClienteFidelizacion.setBounds(10, 110, 530, 50);

        pnlProcesoNormal.add(pnlContenedorProcesos);
        pnlContenedorProcesos.setBounds(20, 30, 550, 380);

        pnlAsignarDatosVehiculo.setBackground(new java.awt.Color(255, 255, 255));
        pnlAsignarDatosVehiculo.setName("pnlAsignarDatosVehiculo"); // NOI18N
        pnlAsignarDatosVehiculo.setRoundBottomLeft(30);
        pnlAsignarDatosVehiculo.setRoundBottomRight(30);
        pnlAsignarDatosVehiculo.setRoundTopLeft(30);
        pnlAsignarDatosVehiculo.setRoundTopRight(30);
        pnlAsignarDatosVehiculo.setLayout(new java.awt.CardLayout());

        pnlOpcionDatosVehiculo.setBackground(new java.awt.Color(255, 255, 255));
        pnlOpcionDatosVehiculo.setName("pnlOpcionDatosVehiculo"); // NOI18N
        pnlOpcionDatosVehiculo.setRoundBottomLeft(30);
        pnlOpcionDatosVehiculo.setRoundBottomRight(30);
        pnlOpcionDatosVehiculo.setRoundTopLeft(30);
        pnlOpcionDatosVehiculo.setRoundTopRight(30);
        pnlOpcionDatosVehiculo.setLayout(null);

        btnAgregarDatos.setBackground(new java.awt.Color(228, 30, 19));
        btnAgregarDatos.setName("btnAgregarDatos"); // NOI18N
        btnAgregarDatos.setRoundBottomLeft(30);
        btnAgregarDatos.setRoundBottomRight(30);
        btnAgregarDatos.setRoundTopLeft(30);
        btnAgregarDatos.setRoundTopRight(30);
        btnAgregarDatos.setLayout(null);

        lblModificarDatos1.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        lblModificarDatos1.setForeground(new java.awt.Color(255, 255, 255));
        lblModificarDatos1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblModificarDatos1.setText("SI, AGREGAR DATOS");
        lblModificarDatos1.setName("lblModificarDatos1"); // NOI18N
        lblModificarDatos1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblModificarDatos1MouseClicked(evt);
            }
        });
        btnAgregarDatos.add(lblModificarDatos1);
        lblModificarDatos1.setBounds(0, 0, 510, 50);

        pnlOpcionDatosVehiculo.add(btnAgregarDatos);
        btnAgregarDatos.setBounds(20, 54, 510, 50);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(51, 51, 51));
        jLabel5.setText("¿Requiere agregar datos del vehículo?");
        jLabel5.setToolTipText("");
        jLabel5.setName("jLabel5"); // NOI18N
        pnlOpcionDatosVehiculo.add(jLabel5);
        jLabel5.setBounds(20, 10, 510, 30);

        pnlAsignarDatosVehiculo.add(pnlOpcionDatosVehiculo, "pnlOpcionDatos");

        pnlDatosVehiculoIngresado.setBackground(new java.awt.Color(255, 255, 255));
        pnlDatosVehiculoIngresado.setName("pnlDatosVehiculoIngresado"); // NOI18N
        pnlDatosVehiculoIngresado.setRoundBottomLeft(30);
        pnlDatosVehiculoIngresado.setRoundBottomRight(30);
        pnlDatosVehiculoIngresado.setRoundTopLeft(30);
        pnlDatosVehiculoIngresado.setRoundTopRight(30);
        pnlDatosVehiculoIngresado.setLayout(null);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(51, 51, 51));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/check.png"))); // NOI18N
        jLabel7.setToolTipText("");
        jLabel7.setName("jLabel7"); // NOI18N
        pnlDatosVehiculoIngresado.add(jLabel7);
        jLabel7.setBounds(18, 18, 24, 24);

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(15, 171, 10));
        jLabel10.setText("Datos del vehículo agregados con éxito");
        jLabel10.setToolTipText("");
        jLabel10.setName("jLabel10"); // NOI18N
        pnlDatosVehiculoIngresado.add(jLabel10);
        jLabel10.setBounds(50, 14, 480, 30);

        pnlPlaca.setBackground(new java.awt.Color(255, 255, 255));
        pnlPlaca.setName("pnlPlaca"); // NOI18N
        pnlPlaca.setLayout(null);

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(51, 51, 51));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel15.setText("PLACA");
        jLabel15.setName("jLabel15"); // NOI18N
        pnlPlaca.add(jLabel15);
        jLabel15.setBounds(0, 0, 50, 22);

        infoPlaca.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        infoPlaca.setForeground(new java.awt.Color(51, 51, 51));
        infoPlaca.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        infoPlaca.setText("QWERT123");
        infoPlaca.setBorder(null);
        infoPlaca.setFocusable(false);
        infoPlaca.setName("infoPlaca"); // NOI18N
        pnlPlaca.add(infoPlaca);
        infoPlaca.setBounds(58, 0, 94, 22);

        pnlDatosVehiculoIngresado.add(pnlPlaca);
        pnlPlaca.setBounds(16, 60, 150, 22);

        pnlKm.setBackground(new java.awt.Color(255, 255, 255));
        pnlKm.setName("pnlKm"); // NOI18N
        pnlKm.setLayout(null);

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(51, 51, 51));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("KM");
        jLabel16.setName("jLabel16"); // NOI18N
        pnlKm.add(jLabel16);
        jLabel16.setBounds(0, 0, 30, 22);

        infoKm.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        infoKm.setForeground(new java.awt.Color(51, 51, 51));
        infoKm.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        infoKm.setText("123456789012");
        infoKm.setBorder(null);
        infoKm.setFocusable(false);
        infoKm.setName("infoKm"); // NOI18N
        infoKm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infoKmActionPerformed(evt);
            }
        });
        pnlKm.add(infoKm);
        infoKm.setBounds(32, 0, 110, 22);

        pnlDatosVehiculoIngresado.add(pnlKm);
        pnlKm.setBounds(176, 60, 150, 22);

        pnlOrden.setBackground(new java.awt.Color(255, 255, 255));
        pnlOrden.setName("pnlOrden"); // NOI18N
        pnlOrden.setLayout(null);

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(51, 51, 51));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("ORDEN");
        jLabel17.setName("jLabel17"); // NOI18N
        pnlOrden.add(jLabel17);
        jLabel17.setBounds(0, 0, 56, 22);

        infoOrden.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        infoOrden.setForeground(new java.awt.Color(51, 51, 51));
        infoOrden.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        infoOrden.setText("1234567890");
        infoOrden.setBorder(null);
        infoOrden.setFocusable(false);
        infoOrden.setName("infoOrden"); // NOI18N
        infoOrden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infoOrdenActionPerformed(evt);
            }
        });
        pnlOrden.add(infoOrden);
        infoOrden.setBounds(64, 0, 90, 22);

        pnlDatosVehiculoIngresado.add(pnlOrden);
        pnlOrden.setBounds(326, 60, 160, 22);

        btnEditarDatos.setBackground(new java.awt.Color(228, 30, 19));
        btnEditarDatos.setName("btnEditarDatos"); // NOI18N
        btnEditarDatos.setRoundBottomLeft(10);
        btnEditarDatos.setRoundBottomRight(10);
        btnEditarDatos.setRoundTopLeft(10);
        btnEditarDatos.setRoundTopRight(10);
        btnEditarDatos.setLayout(null);

        lblIconEdit.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        lblIconEdit.setForeground(new java.awt.Color(255, 255, 255));
        lblIconEdit.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblIconEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/Edit.png"))); // NOI18N
        lblIconEdit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblIconEdit.setName("lblIconEdit"); // NOI18N
        lblIconEdit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblIconEditMouseClicked(evt);
            }
        });
        btnEditarDatos.add(lblIconEdit);
        lblIconEdit.setBounds(0, 0, 40, 26);

        pnlDatosVehiculoIngresado.add(btnEditarDatos);
        btnEditarDatos.setBounds(496, 60, 40, 26);

        pnlAsignarDatosVehiculo.add(pnlDatosVehiculoIngresado, "pnlInfoDatos");

        pnlProcesoNormal.add(pnlAsignarDatosVehiculo);
        pnlAsignarDatosVehiculo.setBounds(20, 420, 550, 120);

        pnlProcesosClientes.add(pnlProcesoNormal, "pnlProcesoNormal");

        pnlProcesoCambioCliente.setBackground(new java.awt.Color(220, 220, 220));
        pnlProcesoCambioCliente.setName("pnlProcesoCambioCliente"); // NOI18N
        pnlProcesoCambioCliente.setRoundBottomLeft(30);
        pnlProcesoCambioCliente.setRoundBottomRight(30);
        pnlProcesoCambioCliente.setRoundTopLeft(30);
        pnlProcesoCambioCliente.setRoundTopRight(30);
        pnlProcesoCambioCliente.setLayout(null);

        pnlTeclado.setName("pnlTeclado"); // NOI18N
        pnlTeclado.setOpaque(false);
        pnlProcesoCambioCliente.add(pnlTeclado);
        pnlTeclado.setBounds(110, 200, 1030, 343);

        pnlDatosClientes.setBackground(new java.awt.Color(255, 255, 255));
        pnlDatosClientes.setName("pnlDatosClientes"); // NOI18N
        pnlDatosClientes.setRoundBottomLeft(30);
        pnlDatosClientes.setRoundBottomRight(30);
        pnlDatosClientes.setRoundTopLeft(30);
        pnlDatosClientes.setRoundTopRight(30);
        pnlDatosClientes.setLayout(null);

        txtNumeroDocumento.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        txtNumeroDocumento.setForeground(new java.awt.Color(51, 51, 51));
        txtNumeroDocumento.setBorder(new com.firefuel.components.panelesPersonalizados.BordesRedondos());
        txtNumeroDocumento.setName("txtNumeroDocumento"); // NOI18N
        txtNumeroDocumento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNumeroDocumentoActionPerformed(evt);
            }
        });
        txtNumeroDocumento.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNumeroDocumentoKeyTyped(evt);
            }
        });
        pnlDatosClientes.add(txtNumeroDocumento);
        txtNumeroDocumento.setBounds(40, 50, 440, 60);

        JComboTipoDocumentos.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        JComboTipoDocumentos.setForeground(new java.awt.Color(51, 51, 51));
        JComboTipoDocumentos.setBorder(new com.firefuel.components.panelesPersonalizados.BordesRedondos());
        JComboTipoDocumentos.setName("JComboTipoDocumentos"); // NOI18N
        JComboTipoDocumentos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JComboTipoDocumentosActionPerformed(evt);
            }
        });
        pnlDatosClientes.add(JComboTipoDocumentos);
        JComboTipoDocumentos.setBounds(500, 50, 480, 60);

        jLabel1.setBackground(new java.awt.Color(0, 0, 0));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 51, 51));
        jLabel1.setText("TIPO DE DOCUMENTO");
        jLabel1.setName("jLabel1"); // NOI18N
        pnlDatosClientes.add(jLabel1);
        jLabel1.setBounds(510, 20, 420, 30);

        jLabel2.setBackground(new java.awt.Color(0, 0, 0));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(51, 51, 51));
        jLabel2.setText("NÚMERO DE DOCUMENTO");
        jLabel2.setName("jLabel2"); // NOI18N
        pnlDatosClientes.add(jLabel2);
        jLabel2.setBounds(50, 20, 420, 30);

        lblMensajeInfo.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblMensajeInfo.setForeground(new java.awt.Color(204, 0, 0));
        lblMensajeInfo.setName("lblMensajeInfo"); // NOI18N
        pnlDatosClientes.add(lblMensajeInfo);
        lblMensajeInfo.setBounds(40, 110, 440, 40);

        pnlProcesoCambioCliente.add(pnlDatosClientes);
        pnlDatosClientes.setBounds(110, 40, 1030, 150);

        pnlProcesosClientes.add(pnlProcesoCambioCliente, "pnlProcesoCambioCliente");

        pnlLoad.setBackground(new java.awt.Color(255, 255, 255));
        pnlLoad.setName("pnlLoad"); // NOI18N
        pnlLoad.setRoundBottomLeft(30);
        pnlLoad.setRoundBottomRight(30);
        pnlLoad.setRoundTopLeft(30);
        pnlLoad.setRoundTopRight(30);
        pnlLoad.setLayout(null);

        lblInformacionEsperar1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblInformacionEsperar1.setForeground(new java.awt.Color(75, 74, 92));
        lblInformacionEsperar1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblInformacionEsperar1.setText("ESPERE UN MOMENTO ");
        lblInformacionEsperar1.setName("lblInformacionEsperar1"); // NOI18N
        pnlLoad.add(lblInformacionEsperar1);
        lblInformacionEsperar1.setBounds(330, 340, 610, 70);

        lblIcono1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblIcono1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/spiner.gif"))); // NOI18N
        lblIcono1.setName("lblIcono1"); // NOI18N
        pnlLoad.add(lblIcono1);
        lblIcono1.setBounds(300, 80, 640, 240);

        pnlProcesosClientes.add(pnlLoad, "pnlLoad");

        pnlConfirmacionComprobante.setBackground(new java.awt.Color(220, 220, 220));
        pnlConfirmacionComprobante.setName("pnlConfirmacionComprobante"); // NOI18N
        pnlConfirmacionComprobante.setRoundBottomLeft(30);
        pnlConfirmacionComprobante.setRoundBottomRight(30);
        pnlConfirmacionComprobante.setRoundTopLeft(30);
        pnlConfirmacionComprobante.setRoundTopRight(30);
        pnlConfirmacionComprobante.setLayout(null);

        pnlPregunta.setBackground(new java.awt.Color(255, 255, 255));
        pnlPregunta.setName("pnlPregunta"); // NOI18N
        pnlPregunta.setRoundBottomLeft(30);
        pnlPregunta.setRoundBottomRight(30);
        pnlPregunta.setRoundTopLeft(30);
        pnlPregunta.setRoundTopRight(30);
        pnlPregunta.setLayout(null);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(204, 0, 0));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("<HTML><CENTER>¿ DESEA COMPROBANTE PARA FACTURA ELECTRÓNICA?</CENTER></HTML>");
        jLabel3.setName("jLabel3"); // NOI18N
        pnlPregunta.add(jLabel3);
        jLabel3.setBounds(0, 40, 770, 100);

        btnNO.setBackground(new java.awt.Color(153, 153, 153));
        btnNO.setName("btnNO"); // NOI18N
        btnNO.setRoundBottomLeft(30);
        btnNO.setRoundBottomRight(30);
        btnNO.setRoundTopLeft(30);
        btnNO.setRoundTopRight(30);
        btnNO.setLayout(null);

        lblNO.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblNO.setForeground(new java.awt.Color(255, 255, 255));
        lblNO.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNO.setText("NO");
        lblNO.setName("lblNO"); // NOI18N
        lblNO.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblNOMouseClicked(evt);
            }
        });
        btnNO.add(lblNO);
        lblNO.setBounds(0, 0, 250, 60);

        pnlPregunta.add(btnNO);
        btnNO.setBounds(100, 180, 250, 60);

        btnSI.setBackground(new java.awt.Color(228, 30, 19));
        btnSI.setName("btnSI"); // NOI18N
        btnSI.setRoundBottomLeft(30);
        btnSI.setRoundBottomRight(30);
        btnSI.setRoundTopLeft(30);
        btnSI.setRoundTopRight(30);
        btnSI.setLayout(null);

        lblSI.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblSI.setForeground(new java.awt.Color(255, 255, 255));
        lblSI.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSI.setText("SI");
        lblSI.setName("lblSI"); // NOI18N
        lblSI.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblSIMouseClicked(evt);
            }
        });
        btnSI.add(lblSI);
        lblSI.setBounds(0, 0, 250, 60);

        pnlPregunta.add(btnSI);
        btnSI.setBounds(390, 180, 250, 60);

        pnlConfirmacionComprobante.add(pnlPregunta);
        pnlPregunta.setBounds(210, 120, 770, 280);

        pnlProcesosClientes.add(pnlConfirmacionComprobante, "pnlConfirmacionComprobante");

        pnlMensajeClienteFe.setBackground(new java.awt.Color(220, 220, 220));
        pnlMensajeClienteFe.setName("pnlMensajeClienteFe"); // NOI18N
        pnlMensajeClienteFe.setRoundBottomLeft(30);
        pnlMensajeClienteFe.setRoundBottomRight(30);
        pnlMensajeClienteFe.setRoundTopLeft(30);
        pnlMensajeClienteFe.setRoundTopRight(30);
        pnlMensajeClienteFe.setLayout(null);

        pnlInformacion.setBackground(new java.awt.Color(255, 255, 255));
        pnlInformacion.setName("pnlInformacion"); // NOI18N
        pnlInformacion.setRoundBottomLeft(30);
        pnlInformacion.setRoundBottomRight(30);
        pnlInformacion.setRoundTopLeft(30);
        pnlInformacion.setRoundTopRight(30);
        pnlInformacion.setLayout(null);

        lblInformacionMensajeErrorFe.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        lblInformacionMensajeErrorFe.setForeground(new java.awt.Color(204, 0, 0));
        lblInformacionMensajeErrorFe.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblInformacionMensajeErrorFe.setText("<HTML><CENTER>¿ DESEA COMPROBANTE PARA FACTURA ELECTRÓNICA?</CENTER></HTML>");
        lblInformacionMensajeErrorFe.setName("lblInformacionMensajeErrorFe"); // NOI18N
        pnlInformacion.add(lblInformacionMensajeErrorFe);
        lblInformacionMensajeErrorFe.setBounds(20, 40, 900, 310);

        panelRedondo2.setBackground(new java.awt.Color(228, 30, 19));
        panelRedondo2.setName("panelRedondo2"); // NOI18N
        panelRedondo2.setRoundBottomLeft(30);
        panelRedondo2.setRoundBottomRight(30);
        panelRedondo2.setRoundTopLeft(30);
        panelRedondo2.setRoundTopRight(30);
        panelRedondo2.setLayout(null);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("CERRAR");
        jLabel4.setName("jLabel4"); // NOI18N
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel4MouseClicked(evt);
            }
        });
        panelRedondo2.add(jLabel4);
        jLabel4.setBounds(0, 0, 320, 70);

        pnlInformacion.add(panelRedondo2);
        panelRedondo2.setBounds(300, 380, 320, 70);

        pnlMensajeClienteFe.add(pnlInformacion);
        pnlInformacion.setBounds(150, 30, 940, 490);

        pnlProcesosClientes.add(pnlMensajeClienteFe, "pnlMensajeClienteFe");

        procesoFidelizacionYFacturacion.add(pnlProcesosClientes);
        pnlProcesosClientes.setBounds(20, 120, 1240, 560);

        lblFondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        lblFondo.setName("lblFondo"); // NOI18N
        procesoFidelizacionYFacturacion.add(lblFondo);
        lblFondo.setBounds(0, 0, 1280, 800);

        pnlPrincipal.add(procesoFidelizacionYFacturacion, "fidelizacionYfacturaElectronica");

        loader.setName("loader"); // NOI18N
        loader.setLayout(null);

        lblInformacionEsperar.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblInformacionEsperar.setForeground(new java.awt.Color(75, 74, 92));
        lblInformacionEsperar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblInformacionEsperar.setText("ESPERE UN MOMENTO ");
        lblInformacionEsperar.setName("lblInformacionEsperar"); // NOI18N
        loader.add(lblInformacionEsperar);
        lblInformacionEsperar.setBounds(360, 440, 610, 70);

        lblIcono.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblIcono.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/spiner.gif"))); // NOI18N
        lblIcono.setName("lblIcono"); // NOI18N
        loader.add(lblIcono);
        lblIcono.setBounds(330, 176, 640, 240);

        lblFondoLoader.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        lblFondoLoader.setName("lblFondoLoader"); // NOI18N
        loader.add(lblFondoLoader);
        lblFondoLoader.setBounds(0, 0, 1280, 800);

        pnlPrincipal.add(loader, "loader");

        pnlDatosVehiculo.setName("pnlDatosVehiculo"); // NOI18N
        pnlDatosVehiculo.setLayout(null);

        lblRegresarDatosVehiculo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRegresarDatosVehiculo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/botones/bt_back_white.png"))); // NOI18N
        lblRegresarDatosVehiculo.setName("lblRegresarDatosVehiculo"); // NOI18N
        lblRegresarDatosVehiculo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblRegresarDatosVehiculoMouseClicked(evt);
            }
        });
        pnlDatosVehiculo.add(lblRegresarDatosVehiculo);
        lblRegresarDatosVehiculo.setBounds(10, 10, 70, 60);

        lblTituloDatos.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblTituloDatos.setForeground(new java.awt.Color(255, 255, 255));
        lblTituloDatos.setText("Datos del vehículo");
        lblTituloDatos.setName("lblTituloDatos"); // NOI18N
        pnlDatosVehiculo.add(lblTituloDatos);
        lblTituloDatos.setBounds(100, 10, 580, 60);

        pnlContenedorRegistro.setBackground(new java.awt.Color(240, 239, 245));
        pnlContenedorRegistro.setName("pnlContenedorRegistro"); // NOI18N
        pnlContenedorRegistro.setRoundBottomLeft(40);
        pnlContenedorRegistro.setRoundBottomRight(40);
        pnlContenedorRegistro.setRoundTopLeft(40);
        pnlContenedorRegistro.setRoundTopRight(40);
        pnlContenedorRegistro.setLayout(null);

        pnlBorde.setBackground(new java.awt.Color(223, 26, 9));
        pnlBorde.setName("pnlBorde"); // NOI18N
        pnlBorde.setRoundBottomLeft(40);
        pnlBorde.setRoundBottomRight(40);
        pnlBorde.setRoundTopLeft(40);
        pnlBorde.setRoundTopRight(40);
        pnlBorde.setLayout(null);

        pnlContenedor.setBackground(new java.awt.Color(255, 255, 255));
        pnlContenedor.setName("pnlContenedor"); // NOI18N
        pnlContenedor.setRoundBottomLeft(30);
        pnlContenedor.setRoundBottomRight(30);
        pnlContenedor.setRoundTopLeft(30);
        pnlContenedor.setRoundTopRight(30);
        pnlContenedor.setLayout(null);

        pnlRegistroPlaca.setBackground(new java.awt.Color(255, 255, 255));
        pnlRegistroPlaca.setName("pnlRegistroPlaca"); // NOI18N
        pnlRegistroPlaca.setLayout(null);

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(72, 71, 80));
        jLabel11.setText("PLACA");
        jLabel11.setName("jLabel11"); // NOI18N
        pnlRegistroPlaca.add(jLabel11);
        jLabel11.setBounds(0, 0, 80, 48);

        panelRedondo5.setBackground(new java.awt.Color(220, 221, 225));
        panelRedondo5.setName("panelRedondo5"); // NOI18N
        panelRedondo5.setRoundBottomLeft(10);
        panelRedondo5.setRoundBottomRight(10);
        panelRedondo5.setRoundTopLeft(10);
        panelRedondo5.setRoundTopRight(10);
        panelRedondo5.setLayout(null);

        panelRedondo6.setBackground(new java.awt.Color(255, 255, 255));
        panelRedondo6.setName("panelRedondo6"); // NOI18N
        panelRedondo6.setLayout(null);

        txtPlaca.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        txtPlaca.setForeground(new java.awt.Color(72, 71, 80));
        txtPlaca.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPlaca.setBorder(null);
        txtPlaca.setName("txtPlaca"); // NOI18N
        txtPlaca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPlacaKeyTyped(evt);
            }
        });
        panelRedondo6.add(txtPlaca);
        txtPlaca.setBounds(0, 0, 241, 44);

        panelRedondo5.add(panelRedondo6);
        panelRedondo6.setBounds(2, 2, 241, 44);

        pnlRegistroPlaca.add(panelRedondo5);
        panelRedondo5.setBounds(80, 0, 245, 48);

        pnlContenedor.add(pnlRegistroPlaca);
        pnlRegistroPlaca.setBounds(130, 27, 328, 48);

        pnlRegistroKm.setBackground(new java.awt.Color(255, 255, 255));
        pnlRegistroKm.setName("pnlRegistroKm"); // NOI18N
        pnlRegistroKm.setLayout(null);

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(72, 71, 80));
        jLabel12.setText("KMS");
        jLabel12.setName("jLabel12"); // NOI18N
        pnlRegistroKm.add(jLabel12);
        jLabel12.setBounds(0, 0, 80, 48);

        panelRedondo7.setBackground(new java.awt.Color(220, 221, 225));
        panelRedondo7.setName("panelRedondo7"); // NOI18N
        panelRedondo7.setRoundBottomLeft(10);
        panelRedondo7.setRoundBottomRight(10);
        panelRedondo7.setRoundTopLeft(10);
        panelRedondo7.setRoundTopRight(10);
        panelRedondo7.setLayout(null);

        panelRedondo8.setBackground(new java.awt.Color(255, 255, 255));
        panelRedondo8.setName("panelRedondo8"); // NOI18N
        panelRedondo8.setLayout(null);

        txtKms.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        txtKms.setForeground(new java.awt.Color(72, 71, 80));
        txtKms.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtKms.setBorder(null);
        txtKms.setName("txtKms"); // NOI18N
        txtKms.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtKmsKeyTyped(evt);
            }
        });
        panelRedondo8.add(txtKms);
        txtKms.setBounds(0, 0, 241, 44);

        panelRedondo7.add(panelRedondo8);
        panelRedondo8.setBounds(2, 2, 241, 44);

        pnlRegistroKm.add(panelRedondo7);
        panelRedondo7.setBounds(80, 0, 245, 48);

        pnlContenedor.add(pnlRegistroKm);
        pnlRegistroKm.setBounds(566, 27, 330, 48);

        pnlRegistroAutorizacion.setBackground(new java.awt.Color(255, 255, 255));
        pnlRegistroAutorizacion.setName("pnlRegistroAutorizacion"); // NOI18N
        pnlRegistroAutorizacion.setLayout(null);

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(72, 71, 80));
        jLabel13.setText("No. AUTORIZACÍON");
        jLabel13.setName("jLabel13"); // NOI18N
        pnlRegistroAutorizacion.add(jLabel13);
        jLabel13.setBounds(0, 0, 188, 48);

        panelRedondo9.setBackground(new java.awt.Color(220, 221, 225));
        panelRedondo9.setName("panelRedondo9"); // NOI18N
        panelRedondo9.setRoundBottomLeft(10);
        panelRedondo9.setRoundBottomRight(10);
        panelRedondo9.setRoundTopLeft(10);
        panelRedondo9.setRoundTopRight(10);
        panelRedondo9.setLayout(null);

        panelRedondo10.setBackground(new java.awt.Color(255, 255, 255));
        panelRedondo10.setName("panelRedondo10"); // NOI18N
        panelRedondo10.setLayout(null);

        txtAutorizacion.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        txtAutorizacion.setForeground(new java.awt.Color(72, 71, 80));
        txtAutorizacion.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtAutorizacion.setBorder(null);
        txtAutorizacion.setName("txtAutorizacion"); // NOI18N
        txtAutorizacion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtAutorizacionKeyTyped(evt);
            }
        });
        panelRedondo10.add(txtAutorizacion);
        txtAutorizacion.setBounds(0, 0, 241, 44);

        panelRedondo9.add(panelRedondo10);
        panelRedondo10.setBounds(2, 2, 241, 44);

        pnlRegistroAutorizacion.add(panelRedondo9);
        panelRedondo9.setBounds(194, 0, 245, 48);

        pnlContenedor.add(pnlRegistroAutorizacion);
        pnlRegistroAutorizacion.setBounds(17, 106, 440, 48);

        pnlRegistroNoVehiculo.setBackground(new java.awt.Color(255, 255, 255));
        pnlRegistroNoVehiculo.setName("pnlRegistroNoVehiculo"); // NOI18N
        pnlRegistroNoVehiculo.setLayout(null);

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(72, 71, 80));
        jLabel14.setText("No. VEHÍCULO");
        jLabel14.setName("jLabel14"); // NOI18N
        pnlRegistroNoVehiculo.add(jLabel14);
        jLabel14.setBounds(0, 0, 130, 48);

        panelRedondo11.setBackground(new java.awt.Color(220, 221, 225));
        panelRedondo11.setName("panelRedondo11"); // NOI18N
        panelRedondo11.setRoundBottomLeft(10);
        panelRedondo11.setRoundBottomRight(10);
        panelRedondo11.setRoundTopLeft(10);
        panelRedondo11.setRoundTopRight(10);
        panelRedondo11.setLayout(null);

        panelRedondo12.setBackground(new java.awt.Color(255, 255, 255));
        panelRedondo12.setName("panelRedondo12"); // NOI18N
        panelRedondo12.setLayout(null);

        txtNoVehiculo.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        txtNoVehiculo.setForeground(new java.awt.Color(72, 71, 80));
        txtNoVehiculo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtNoVehiculo.setBorder(null);
        txtNoVehiculo.setName("txtNoVehiculo"); // NOI18N
        txtNoVehiculo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNoVehiculoKeyTyped(evt);
            }
        });
        panelRedondo12.add(txtNoVehiculo);
        txtNoVehiculo.setBounds(0, 0, 241, 44);

        panelRedondo11.add(panelRedondo12);
        panelRedondo12.setBounds(2, 2, 241, 44);

        pnlRegistroNoVehiculo.add(panelRedondo11);
        panelRedondo11.setBounds(136, 0, 245, 48);

        pnlContenedor.add(pnlRegistroNoVehiculo);
        pnlRegistroNoVehiculo.setBounds(509, 106, 384, 48);

        btnAgregarDatosVehiculo.setBackground(new java.awt.Color(228, 30, 19));
        btnAgregarDatosVehiculo.setName("btnAgregarDatosVehiculo"); // NOI18N
        btnAgregarDatosVehiculo.setRoundBottomLeft(30);
        btnAgregarDatosVehiculo.setRoundBottomRight(30);
        btnAgregarDatosVehiculo.setRoundTopLeft(30);
        btnAgregarDatosVehiculo.setRoundTopRight(30);
        btnAgregarDatosVehiculo.setLayout(null);

        lblAgregarDatos.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblAgregarDatos.setForeground(new java.awt.Color(255, 255, 255));
        lblAgregarDatos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAgregarDatos.setText("AGREGAR");
        lblAgregarDatos.setToolTipText("");
        lblAgregarDatos.setName("lblAgregarDatos"); // NOI18N
        lblAgregarDatos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblAgregarDatosMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblAgregarDatosMouseEntered(evt);
            }
        });
        btnAgregarDatosVehiculo.add(lblAgregarDatos);
        lblAgregarDatos.setBounds(0, 0, 178, 50);

        pnlContenedor.add(btnAgregarDatosVehiculo);
        btnAgregarDatosVehiculo.setBounds(954, 61, 178, 50);

        pnlBorde.add(pnlContenedor);
        pnlContenedor.setBounds(4, 4, 1192, 192);

        pnlContenedorRegistro.add(pnlBorde);
        pnlBorde.setBounds(11, 16, 1200, 200);

        pnlDatosVehiculo.add(pnlContenedorRegistro);
        pnlContenedorRegistro.setBounds(28, 100, 1224, 230);

        jPanel1.setName("jPanel1"); // NOI18N
        pnlDatosVehiculo.add(jPanel1);
        jPanel1.setBounds(110, 338, 1060, 360);

        jNotificacion.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jNotificacion.setForeground(new java.awt.Color(255, 255, 255));
        jNotificacion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jNotificacion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jNotificacion.setName("jNotificacion"); // NOI18N
        pnlDatosVehiculo.add(jNotificacion);
        jNotificacion.setBounds(510, 10, 730, 60);

        lblFondoDatosVehiculo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/firefuel/resources/fndRumbo.png"))); // NOI18N
        lblFondoDatosVehiculo.setName("lblFondoDatosVehiculo"); // NOI18N
        pnlDatosVehiculo.add(lblFondoDatosVehiculo);
        lblFondoDatosVehiculo.setBounds(0, 0, 1280, 800);

        pnlPrincipal.add(pnlDatosVehiculo, "pnlDatos");

        add(pnlPrincipal);
        pnlPrincipal.setBounds(0, 0, 1280, 800);
    }// </editor-fold>//GEN-END:initComponents

    private void lblRegresarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblRegresarMouseClicked
        cerrar();
    }//GEN-LAST:event_lblRegresarMouseClicked

    private void checkFidelizacionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_checkFidelizacionMouseClicked
        if (this.identificadorPuntoDeVenta != null) {
            clienteFidelizacion();
        } else {
            checkFidelizacion.setSelected(false);
            validatePagoHasBonoViveTerpel();
            Informacion.setText("No existe código de fidelización configurado");
        }
    }//GEN-LAST:event_checkFidelizacionMouseClicked

    private void chechFacturacionElectronicaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_chechFacturacionElectronicaMouseClicked
        if (datosClienteFE != null && datosClienteFE.has(ERROR) && datosClienteFE.get(ERROR).getAsBoolean()) {
            Informacion.setText("<html>" + datosClienteFE.get("mensaje").getAsString() + "</html>");
            chechFacturacionElectronica.setEnabled(false);
            chechFacturacionElectronica.setSelected(false);
            limpiarMensaje(3000, Informacion);
        }

    }//GEN-LAST:event_chechFacturacionElectronicaMouseClicked

    private void lbCContinuarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbCContinuarMouseClicked
        if (asignarDatos) {
            btnContinuar.setVisible(false);
            btnConsultar.setVisible(false);
            imprimirFe();
        } else {
            continuarVentaCurso();
        }
    }//GEN-LAST:event_lbCContinuarMouseClicked

    private void lblModificarDatosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblModificarDatosMouseClicked
        if (!btnModificarDatos.getBackground().equals(inactivo)) {
            modificarDatos = "facturacionElectronica";
            llenarCampoTipoIdentificacion();
            RenderizarProcesosFidelizacionyFE renderizarProcesosFidelizacionyFE = new RenderizarProcesosFidelizacionyFE();
            renderizarProcesosFidelizacionyFE.cambiarPanel(pnlProcesosClientes, "pnlProcesoCambioCliente");
            btnCancelar.setVisible(true);
            btnContinuar.setBackground(inactivo);
            btnConsultar.setBackground(activo);
            btnContinuar.setVisible(true);
            btnConsultar.setVisible(true);
            txtNumeroDocumento.requestFocus();
        }

    }//GEN-LAST:event_lblModificarDatosMouseClicked

    private void lblConsultarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblConsultarMouseClicked
        if (btnConsultar.getBackground().equals(activo)) {
            if (modificarDatos.equals("facturacionElectronica")) {
                consultarClienteFe();
            } else {
                try {
                    consultarClienteFdidelizacion();
                } catch (InterruptedException ex) {
                    Logger.getLogger(FidelizacionyFacturacionElectronica.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(FidelizacionyFacturacionElectronica.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_lblConsultarMouseClicked


    private void txtNumeroDocumentoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNumeroDocumentoKeyTyped
        NovusUtils.limitarCarateres(evt, txtNumeroDocumento, 20, Informacion, caracteresPermitidos);

    }//GEN-LAST:event_txtNumeroDocumentoKeyTyped

    private void lblCancelarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCancelarMouseClicked
        cancelar();
    }//GEN-LAST:event_lblCancelarMouseClicked

    private void JComboTipoDocumentosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JComboTipoDocumentosActionPerformed
        if (JComboTipoDocumentos.getSelectedItem() != null) {
            if (JComboTipoDocumentos.getSelectedItem().toString().equals("CONSUMIDOR FINAL")) {
                txtNumeroDocumento.setText("222222222222");
            } else {
                txtNumeroDocumento.setText("");
            }
            txtNumeroDocumento.requestFocus();
            TecladoExtendidoGray teclado = (TecladoExtendidoGray) pnlTeclado;
            caracteresPermitidos = NovusUtils.obtenerRestriccionCaracteres(JComboTipoDocumentos.getSelectedItem().toString().toUpperCase());
            cantidadCaracteres = NovusUtils.obtenerLimiteCaracteres(JComboTipoDocumentos.getSelectedItem().toString().toUpperCase());
            teclado.habilitarAlfanumeric(NovusUtils.habilitarTecladoAlfanumerico(caracteresPermitidos));
            teclado.habilitarPunto(NovusUtils.habilitarPunto(caracteresPermitidos));
            teclado.habilitarDosPuntos(NovusUtils.habilitarDosPunto(caracteresPermitidos));
        }

    }//GEN-LAST:event_JComboTipoDocumentosActionPerformed

    private void lblSIMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSIMouseClicked
        datosClienteFE.addProperty("pendiente_impresion", Boolean.TRUE);
        if (asignarDatos) {
            asignarDatosPlaca();
            asignarClienteVenta();
            fidelizar();
            enviar.run();
        } else {
            enviarProcesos();
        }
    }//GEN-LAST:event_lblSIMouseClicked

    private void lblNOMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblNOMouseClicked
        datosClienteFE.addProperty("pendiente_impresion", Boolean.FALSE);
        if (asignarDatos) {
            asignarDatosPlaca();
            asignarClienteVenta();
            enviar.run();
        } else {
            enviarProcesos();
        }
    }//GEN-LAST:event_lblNOMouseClicked

    private void lblModificarFidelizacionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblModificarFidelizacionMouseClicked
        if (!btnModificarFidelizacion.getBackground().equals(inactivo)) {
            this.lblMensaje.setText(null);
            modificarDatos = "fidelizacion";
            RenderizarProcesosFidelizacionyFE renderizarProcesosFidelizacionyFE = new RenderizarProcesosFidelizacionyFE();
            renderizarProcesosFidelizacionyFE.cambiarPanel(pnlProcesosClientes, "pnlProcesoCambioCliente");
            btnCancelar.setVisible(true);
            btnContinuar.setBackground(inactivo);
            btnConsultar.setBackground(activo);
            btnContinuar.setVisible(true);
            btnConsultar.setVisible(true);
            llenarCampoTipoIdentificacionFidelizacion();
            txtNumeroDocumento.requestFocus();
        }
    }//GEN-LAST:event_lblModificarFidelizacionMouseClicked

    private void jLabel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseClicked
        btnContinuar.setVisible(true);
        btnConsultar.setVisible(true);
        btnContinuar.setBackground(activo);
        btnConsultar.setBackground(inactivo);
        cancelar();
    }//GEN-LAST:event_jLabel4MouseClicked
    private void txtNumeroDocumentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNumeroDocumentoActionPerformed
        if (modificarDatos.equals("facturacionElectronica")) {
            consultarClienteFe();
        } else {
            try {
                consultarClienteFdidelizacion();
            } catch (InterruptedException ex) {
                Logger.getLogger(FidelizacionyFacturacionElectronica.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(FidelizacionyFacturacionElectronica.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_txtNumeroDocumentoActionPerformed

    private void lblModificarDatos1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblModificarDatos1MouseClicked
        mostrarPanelIngresoDatos();
    }//GEN-LAST:event_lblModificarDatos1MouseClicked

    private void lblRegresarDatosVehiculoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblRegresarDatosVehiculoMouseClicked
        NovusUtils.showPanel(pnlPrincipal, "fidelizacionYfacturaElectronica");
    }//GEN-LAST:event_lblRegresarDatosVehiculoMouseClicked

    private void lblAgregarDatosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAgregarDatosMouseClicked
        agregarDatos();
    }//GEN-LAST:event_lblAgregarDatosMouseClicked

    private void infoKmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoKmActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_infoKmActionPerformed

    private void infoOrdenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoOrdenActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_infoOrdenActionPerformed

    private void lblIconEditMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblIconEditMouseClicked
        mostrarPanelIngresoDatos();
    }//GEN-LAST:event_lblIconEditMouseClicked

    private void txtPlacaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPlacaKeyTyped
        String caracteresAceptados = "[0-9a-zA-Z]";
        NovusUtils.limitarCarateres(evt, txtPlaca, 8, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_txtPlacaKeyTyped

    private void txtKmsKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtKmsKeyTyped
        String caracteresAceptados = "[0-9]";
        NovusUtils.limitarCarateres(evt, txtKms, 12, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_txtKmsKeyTyped

    private void txtAutorizacionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAutorizacionKeyTyped
        String caracteresAceptados = "[0-9]";
        NovusUtils.limitarCarateres(evt, txtAutorizacion, 10, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_txtAutorizacionKeyTyped

    private void txtNoVehiculoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNoVehiculoKeyTyped
        String caracteresAceptados = "[0-9]";
        NovusUtils.limitarCarateres(evt, txtAutorizacion, 10, jNotificacion, caracteresAceptados);
    }//GEN-LAST:event_txtNoVehiculoKeyTyped

    private void lblAgregarDatosMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAgregarDatosMouseEntered
    }//GEN-LAST:event_lblAgregarDatosMouseEntered


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Informacion;
    private javax.swing.JComboBox<String> JComboTipoDocumentos;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo btnAgregarDatos;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo btnAgregarDatosVehiculo;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo btnCancelar;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo btnConsultar;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo btnContinuar;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo btnEditarDatos;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo btnModificarDatos;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo btnModificarFidelizacion;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo btnNO;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo btnSI;
    private javax.swing.JCheckBox chechFacturacionElectronica;
    private javax.swing.JCheckBox checkFidelizacion;
    private com.firefuel.components.panelesPersonalizados.ComboBox comboBox1;
    private com.firefuel.components.panelesPersonalizados.ComboBox comboBox2;
    private javax.swing.JTextField infoKm;
    private javax.swing.JTextField infoOrden;
    private javax.swing.JTextField infoPlaca;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jNotificacion;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lbCContinuar;
    private javax.swing.JLabel lblAgregarDatos;
    private javax.swing.JLabel lblCancelar;
    private javax.swing.JLabel lblCliente;
    private javax.swing.JLabel lblConsultar;
    public javax.swing.JLabel lblFondo;
    private javax.swing.JLabel lblFondoDatosVehiculo;
    private javax.swing.JLabel lblFondoLoader;
    private javax.swing.JLabel lblIconEdit;
    private javax.swing.JLabel lblIcono;
    private javax.swing.JLabel lblIcono1;
    private javax.swing.JLabel lblInformacionEsperar;
    private javax.swing.JLabel lblInformacionEsperar1;
    private javax.swing.JLabel lblInformacionMensajeErrorFe;
    private javax.swing.JLabel lblMensaje;
    private javax.swing.JLabel lblMensajeInfo;
    private javax.swing.JLabel lblModificarDatos;
    private javax.swing.JLabel lblModificarDatos1;
    private javax.swing.JLabel lblModificarFidelizacion;
    private javax.swing.JLabel lblNO;
    private javax.swing.JLabel lblProcesos;
    private javax.swing.JLabel lblRegresar;
    private javax.swing.JLabel lblRegresarDatosVehiculo;
    private javax.swing.JLabel lblSI;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblTituloDatos;
    private javax.swing.JPanel loader;
    public static javax.swing.JLabel nombreClienteFE;
    public static javax.swing.JLabel nombreClienteFidelizacion;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo panelRedondo1;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo panelRedondo10;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo panelRedondo11;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo panelRedondo12;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo panelRedondo2;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo panelRedondo4;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo panelRedondo5;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo panelRedondo6;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo panelRedondo7;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo panelRedondo8;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo panelRedondo9;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlAsignarDatosVehiculo;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlBorde;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlConfirmacionComprobante;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlContenedor;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlContenedorProcesos;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlContenedorRegistro;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlDatosClientes;
    private javax.swing.JPanel pnlDatosVehiculo;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlDatosVehiculoIngresado;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlInformacion;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlInformacionCliente;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlKm;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlLoad;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlMensajeClienteFe;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlOpcionDatosVehiculo;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlOperaciones;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlOrden;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlPlaca;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlPregunta;
    private javax.swing.JPanel pnlPrincipal;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlProcesoCambioCliente;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlProcesoNormal;
    public javax.swing.JPanel pnlProcesosClientes;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlRegistroAutorizacion;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlRegistroKm;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlRegistroNoVehiculo;
    private com.firefuel.components.panelesPersonalizados.PanelRedondo pnlRegistroPlaca;
    private javax.swing.JPanel pnlTeclado;
    private javax.swing.JPanel procesoFidelizacionYFacturacion;
    private javax.swing.JTextField txtAutorizacion;
    private javax.swing.JTextField txtDocumento;
    private javax.swing.JTextField txtKms;
    private javax.swing.JTextField txtNoVehiculo;
    private javax.swing.JTextField txtNumeroDocumento;
    private javax.swing.JTextField txtPlaca;
    private javax.swing.JTextField txtTipoIdentificaion;
    // End of variables declaration//GEN-END:variables

    private void enviarProcesos() {
        NovusUtils.printLn("**************");
        NovusUtils.printLn("Enviar Proceso");
        SurtidorDao dao = new SurtidorDao();
        if (checkFidelizacion.isSelected() && chechFacturacionElectronica.isSelected()) {
            NovusUtils.printLn("**************");
            NovusUtils.printLn("checkFidelizacion.isSelected() && chechFacturacionElectronica.isSelected()");
            int id = datosClienteFE.get("tipoDocumento") != null ? datosClienteFE.get("tipoDocumento").getAsInt() : datosClienteFE.get("identificacion_cliente").getAsInt();
            datosClienteFE.addProperty("descripcionTipoDocumento", NovusUtils.tipoDocumento(id));
            FacturacionElectronica facturacionElectronica = new FacturacionElectronica();
            dao.updateVentasEncurso(recibo, datosClienteFE, facturacionElectronica.remisionActiva() ? 4 : 3);
            dao.updateVentasEncurso(recibo, datosClienteFidelizacion, 1);
            cerrar();
            return;
        }
        if (chechFacturacionElectronica.isSelected()) {
            NovusUtils.printLn("**************");
            NovusUtils.printLn("chechFacturacionElectronica.isSelected()");
            int id = datosClienteFE.get("tipoDocumento") != null ? datosClienteFE.get("tipoDocumento").getAsInt() : datosClienteFE.get("identificacion_cliente").getAsInt();
            datosClienteFE.addProperty("descripcionTipoDocumento", NovusUtils.tipoDocumento(id));
            FacturacionElectronica facturacionElectronica = new FacturacionElectronica();
            dao.updateVentasEncurso(recibo, datosClienteFE, facturacionElectronica.remisionActiva() ? 4 : 3);
        } else {
            if (checkFidelizacion.isSelected()) {
                NovusUtils.printLn("**************");
                NovusUtils.printLn("checkFidelizacion.isSelected()");
                dao.updateVentasEncurso(recibo, datosClienteFidelizacion, 1);
            }
        }
        cerrar();

    }

    private void consultarCliente(String numeroDocumento, String tipoDocumento, boolean soloFidelizacion) {
        RenderizarProcesosFidelizacionyFE renderizarProcesosFidelizacionyFE = new RenderizarProcesosFidelizacionyFE();
        renderizarProcesosFidelizacionyFE.cambiarPanel(pnlPrincipal, "loader");
        Thread response = new Thread() {
            @Override
            public void run() {
                JsonObject data = renderizarProcesosFidelizacionyFE.consultarCliente(numeroDocumento, tipoDocumento, soloFidelizacion);
                datosClienteFE = data.get("clienteFacturacionElectronica").getAsJsonObject();
                datosClienteFidelizacion = data.get("clienteFidelizacion").getAsJsonObject();
                
                // INTERCEPTAR SOLO SI LA VENTA ES GLP
                if (esVentaGLPActual()) {
                    NovusUtils.printLn("🔒 [GLP] Interceptando respuesta de fidelización para venta GLP");
                    datosClienteFidelizacion.addProperty("existeCliente", false);
                    datosClienteFidelizacion.addProperty("mensaje", "No es posible fidelizar en ventas de GLP");
                    datosClienteFidelizacion.addProperty("nombreCliente", "NO REGISTRADO");
                    datosClienteFidelizacion.addProperty("status", 200); // Mantener status OK para no generar errores
                    NovusUtils.printLn("🔒 [GLP] Respuesta de fidelización modificada para GLP");
                }
                
                String nombreCliente = datosClienteFidelizacion.has(NOMBRE_CLIENTE) ? datosClienteFidelizacion.get(NOMBRE_CLIENTE).getAsString() : "";
                checkFidelizacion.setSelected(datosClienteFidelizacion != null && !nombreCliente.equals("NO REGISTRADO"));
                if (ventaMarket) {
                    boolean isValido = datosClienteFE != null && !datosClienteFE.has(ERROR);
                    chechFacturacionElectronica.setSelected(isValido);
                    chechFacturacionElectronica.setEnabled(!isValido);
                }
                validarBotonFidelizacionyFE();
                renderizarProcesosFidelizacionyFE.cambiarPanel(pnlPrincipal, "fidelizacionYfacturaElectronica");
                validatePagoHasBonoViveTerpel();
            }
        };
        response.start();
    }

    private void clienteFidelizacion(FoundClient clienteConsultado) throws InterruptedException {
        Informacion.setText(clienteConsultado.getMensaje());
        if (clienteConsultado.getNombreCliente().equals("NO REGISTRADO") || clienteConsultado.getNombreCliente().equals("CLIENTE")) {
            checkFidelizacion.setSelected(false);
            checkFidelizacion.setEnabled(false);
            validatePagoHasBonoViveTerpel();
        }
        Thread.sleep(5000);
        Informacion.setText("");
    }

    private void clienteFidelizacion() {
        JsonObject data = datosClienteFidelizacion;
        if (data != null && (data.get(NOMBRE_CLIENTE).getAsString().equals("NO REGISTRADO") || data.get(NOMBRE_CLIENTE).getAsString().isEmpty())) {
            if (!data.get("existeClient").getAsBoolean()) {
                Informacion.setText(data.get("mensaje").getAsString());
                if (NovusConstante.HAY_INTERNET) {
                    habilitarFidelizacion(false);
                }
                limpiarMensaje(3000, Informacion);
            } else {
                Informacion.setText("No hay conexión con el Motor de Fidelización");
                if (NovusConstante.HAY_INTERNET) {
                    habilitarFidelizacion(false);
                }
                limpiarMensaje(3000, Informacion);
            }
        }
    }

    private void habilitarFidelizacion(boolean habilitar) {
        checkFidelizacion.setEnabled(habilitar);
        checkFidelizacion.setSelected(habilitar);
        validatePagoHasBonoViveTerpel();
    }

    private void cerrar() {
        if (this.regresar != null) {
            regresar.run();
        }
    }

    private void limpiarMensaje(long segundos, JLabel label) {
        Runnable runaRunnable = () -> {
            try {
                Thread.sleep(segundos);
                label.setText("");
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        };
        CompletableFuture.runAsync(runaRunnable);

    }

    private void validarBotonFidelizacion(String tipoDocumento) {
        if (!tipoDocumento.equals(TiposDocumentos.CEDULA_DE_CIUDADANIA.getDescripcion().toUpperCase())
                && !tipoDocumento.equals(TiposDocumentos.DOCUMENTO_CEDULA_EXTRANJERIA.getDescripcion().toUpperCase())) {
            checkFidelizacion.setFocusable(false);
            checkFidelizacion.setEnabled(false);
            lblMensaje.setText("No es posible fidelizar con un tipo de documento diferente a C.C o C.E");
        }
        validatePagoHasBonoViveTerpel();
    }
   private void validarBotonFidelizacionyFE() {

        JsonObject data = datosClienteFidelizacion;
        String nombreCliente = datosClienteFidelizacion.has(NOMBRE_CLIENTE) ? datosClienteFidelizacion.get(NOMBRE_CLIENTE).getAsString() : "";
        if (data != null && (nombreCliente.equals("NO REGISTRADO") || nombreCliente.isEmpty())) {
            if (!data.get("existeClient").getAsBoolean()) {
                String mensaje = data.has("mensaje") ? data.get("mensaje").getAsString() : "";
                Informacion.setText(NovusUtils.convertMessage(LetterCase.FIRST_UPPER_CASE, mensaje));
                if (NovusConstante.HAY_INTERNET) {
                    habilitarFidelizacion(false);
                }
                limpiarMensaje(3000, Informacion);
            } else {
                Informacion.setText("No hay conexión con el Motor de Fidelización");
                if (NovusConstante.HAY_INTERNET) {
                    habilitarFidelizacion(false);
                }
                limpiarMensaje(3000, Informacion);
            }
        }

        if (datosClienteFE != null && datosClienteFE.has(ERROR) && datosClienteFE.get(ERROR).getAsBoolean()) {
            String mensaje = NovusUtils.convertMessage(
                    LetterCase.FIRST_UPPER_CASE,
                    datosClienteFE.get("mensaje").getAsString());
            Informacion.setText("<html>" + mensaje + "</html>");
            chechFacturacionElectronica.setEnabled(false);
            chechFacturacionElectronica.setSelected(false);
            limpiarMensaje(3000, Informacion);
        }

        FacturacionElectronica fac = new FacturacionElectronica();
        boolean aplicaFE = fac.aplicaFE();
        boolean feActivo = !aplicaFE && !findByParameterUseCase.execute();
        if (!feActivo && datosClienteFE != null && !fidelizacion) {
            mensajeValidacionCliente();
        }
    }
    // private void validarBotonFidelizacionyFE() {

    //     JsonObject data = datosClienteFidelizacion;
    //     String nombreCliente = datosClienteFidelizacion.has(NOMBRE_CLIENTE) ? datosClienteFidelizacion.get(NOMBRE_CLIENTE).getAsString() : "";
    //     if (data != null && (nombreCliente.equals("NO REGISTRADO") || nombreCliente.isEmpty())) {
    //         if (!data.get("existeClient").getAsBoolean()) {
    //             String mensaje = data.has("mensaje") ? data.get("mensaje").getAsString() : "";
    //             Informacion.setText(NovusUtils.convertMessage(LetterCase.FIRST_UPPER_CASE, mensaje));
    //             if (NovusConstante.HAY_INTERNET) {
    //                 habilitarFidelizacion(false);
    //             }
    //             limpiarMensaje(3000, Informacion);
    //         } else {
    //             Informacion.setText("No hay conexión con el Motor de Fidelización");
    //             if (NovusConstante.HAY_INTERNET) {
    //                 habilitarFidelizacion(false);
    //             }
    //             limpiarMensaje(3000, Informacion);
    //         }
    //     }

    //     if (datosClienteFE != null && datosClienteFE.has(ERROR) && datosClienteFE.get(ERROR).getAsBoolean()) {
    //         String mensaje = NovusUtils.convertMessage(
    //                 LetterCase.FIRST_UPPER_CASE,
    //                 datosClienteFE.get("mensaje").getAsString());
    //         Informacion.setText("<html>" + mensaje + "</html>");
    //         chechFacturacionElectronica.setEnabled(false);
    //         chechFacturacionElectronica.setSelected(false);
    //         limpiarMensaje(3000, Informacion);
    //     }

    //     FacturacionElectronica fac = new FacturacionElectronica();
    //     boolean aplicaFE = fac.aplicaFE();
    //     MovimientosDao mdao = new MovimientosDao();
    //     boolean feActivo = !aplicaFE && !mdao.remisionActiva();
    //     if (!feActivo && datosClienteFE != null && !fidelizacion) {
    //         mensajeValidacionCliente();
    //     }
    // }

    private void mensajeValidacionCliente() {
        if (datosClienteFE.has("errorFaltaCampos") && datosClienteFE.getAsJsonPrimitive("errorFaltaCampos").getAsBoolean()) {
            RenderizarProcesosFidelizacionyFE renderizarProcesosFidelizacionyFE = new RenderizarProcesosFidelizacionyFE();
            lblInformacionMensajeErrorFe.setText("<html><center>" + datosClienteFE.get("errorClienteFE").getAsString() + "</center></html>");
            btnContinuar.setVisible(false);
            btnConsultar.setVisible(false);
            btnCancelar.setVisible(false);
            chechFacturacionElectronica.setEnabled(false);
            chechFacturacionElectronica.setSelected(false);
            renderizarProcesosFidelizacionyFE.cambiarPanel(pnlProcesosClientes, "pnlMensajeClienteFe");
        }
    }

    private void ajustarAlturaConfirmacion(boolean fidelizacion) {
        if (fidelizacion) {
            btnModificarDatos.setVisible(false);
            chechFacturacionElectronica.setVisible(false);
            lblProcesos.setVisible(false);
            nombreClienteFE.setVisible(false);
            lblCliente.setBounds(10, 180, 530, 50);
            nombreClienteFidelizacion.setBounds(10, 240, 530, 50);
            pnlOperaciones.setBounds(610, 30, 600, 190);
            pnlInformacionCliente.setVisible(true);
            pnlInformacionCliente.setBounds(610, 250, 600, 190);
            btnConsultar.setVisible(true);
        }
    }

    private void cargarImagen(JCheckBox jCheckBox) {
        jCheckBox.setIcon(new ImageIcon(getClass().getResource(RUTA_NO_CHECK)));
        jCheckBox.setSelectedIcon(new ImageIcon(getClass().getResource("/com/firefuel/resources/iconCheck.png")));
        jCheckBox.setRolloverIcon(new ImageIcon(getClass().getResource(RUTA_NO_CHECK)));
        jCheckBox.setOpaque(false);
    }

    private void placheholder(JTextField campoDeTexto, String numeroDeCocumento) {
        TextPrompt placeholder = new TextPrompt(numeroDeCocumento, campoDeTexto);
        placeholder.changeAlpha(0.75f);
    }

    public void llenarCampoTipoIdentificacion() {
        JComboTipoDocumentos.removeAllItems();
        NovusUtils.llenarComboBox(JComboTipoDocumentos);

    }

    public void llenarCampoTipoIdentificacionFidelizacion() {
        JComboTipoDocumentos.removeAllItems();
        NovusUtils.llenarComboFidelizacion(JComboTipoDocumentos);
    }

    private void consultarClienteFe() {

        if (txtNumeroDocumento.getText().isEmpty()) {
            lblMensajeInfo.setText("INGRESE NÚMERO DE DOCUMENTO");
            limpiarMensaje(3000, lblMensajeInfo);
        } else {
            RenderizarProcesosFidelizacionyFE renderizarProcesosFidelizacionyFE = new RenderizarProcesosFidelizacionyFE();
            renderizarProcesosFidelizacionyFE.cambiarPanel(pnlProcesosClientes, "pnlLoad");
            btnContinuar.setVisible(false);
            btnConsultar.setVisible(false);
            btnCancelar.setVisible(false);

            Thread response = new Thread() {
                @Override
                public void run() {
                    JsonObject data = renderizarProcesosFidelizacionyFE.validarClienteFE(txtNumeroDocumento.getText(), JComboTipoDocumentos.getSelectedItem().toString());
                    datosClienteFE = data.get("clienteFacturacionElectronica").getAsJsonObject();

                    btnConsultar.setBackground(inactivo);
                    txtDocumento.setText(txtNumeroDocumento.getText());
                    txtTipoIdentificaion.setText(JComboTipoDocumentos.getSelectedItem().toString());
                    btnContinuar.setBackground(activo);
                    btnContinuar.setVisible(true);
                    btnConsultar.setVisible(true);
                    btnCancelar.setVisible(false);
                    chechFacturacionElectronica.setEnabled(true);

                    if (ventaMarket) {
                        boolean isValido = datosClienteFE != null && !datosClienteFE.has(ERROR);
                        chechFacturacionElectronica.setSelected(isValido);
                        chechFacturacionElectronica.setEnabled(!isValido);
                    }

                    renderizarProcesosFidelizacionyFE.cambiarPanel(pnlProcesosClientes, "pnlProcesoNormal");
                    txtNumeroDocumento.setText("");
                    JComboTipoDocumentos.setSelectedIndex(0);
                    validarClienteFe();
                }
            };
            response.start();
        }

    }

    private void validarClienteFe() {
        if (datosClienteFE != null && datosClienteFE.has(ERROR) && datosClienteFE.get(ERROR).getAsBoolean()) {
            chechFacturacionElectronica.setEnabled(false);
            chechFacturacionElectronica.setSelected(false);
        }
        if (datosClienteFE != null) {
            mensajeValidacionCliente();
        }
    }

    private void consultarClienteFdidelizacion() throws InterruptedException, ExecutionException {

        if (txtNumeroDocumento.getText().isEmpty()) {
            lblMensajeInfo.setText("INGRESE NÚMERO DE DOCUMENTO");
            limpiarMensaje(3000, lblMensajeInfo);
        } else {
            RenderizarProcesosFidelizacionyFE renderizarProcesosFidelizacionyFE = new RenderizarProcesosFidelizacionyFE();

            Runnable runnable = () -> {
                long tipo = NovusUtils.tipoDeIndentificacion(JComboTipoDocumentos.getSelectedItem().toString());
                FoundClient foundClient = renderizarProcesosFidelizacionyFE.fidelizarVenta(txtNumeroDocumento.getText(), renderizarProcesosFidelizacionyFE.tiposIdentificaionFidelizaicon(tipo));
                datosClienteFidelizacion = Main.gson.toJsonTree(foundClient).getAsJsonObject();
                btnConsultar.setBackground(inactivo);
                txtDocumento.setText(txtNumeroDocumento.getText());
                txtTipoIdentificaion.setText(JComboTipoDocumentos.getSelectedItem().toString());
                btnContinuar.setBackground(activo);
                btnContinuar.setVisible(true);
                btnConsultar.setVisible(true);
                btnCancelar.setVisible(false);
                checkFidelizacion.setEnabled(true);
                validatePagoHasBonoViveTerpel();
                renderizarProcesosFidelizacionyFE.cambiarPanel(pnlProcesosClientes, "pnlProcesoNormal");
                renderizarProcesosFidelizacionyFE.validarClienteFidelizacion(foundClient);
                txtNumeroDocumento.setText("");
                JComboTipoDocumentos.setSelectedIndex(0);
                try {
                    clienteFidelizacion(foundClient);
                } catch (InterruptedException ex) {
                    Logger.getLogger(FidelizacionyFacturacionElectronica.class.getName()).log(Level.SEVERE, null, ex);
                }
            };

            CompletableFuture.runAsync(runnable);

            btnContinuar.setVisible(false);
            btnConsultar.setVisible(false);
            btnCancelar.setVisible(false);
            renderizarProcesosFidelizacionyFE.cambiarPanel(pnlProcesosClientes, "pnlLoad");

        }

    }

    private void showMessage(String msj, String ruta, boolean habilitar,
            Runnable runnable, boolean autoclose, String letterCase) {
        ParametrosMensajes parametrosMensajes = new ParametrosMensajesBuilder().setMsj(msj)
                .setRuta(ruta).setHabilitar(habilitar)
                .setRunnable(runnable).setAutoclose(autoclose)
                .setLetterCase(letterCase).build();
        RenderizarProcesosFidelizacionyFE renderizarProcesosFidelizacionyFE = new RenderizarProcesosFidelizacionyFE();
        pnlPrincipal.add("pnl_ext", ShowMessageSingleton.showMassegesInstance().execute(parametrosMensajes));
        renderizarProcesosFidelizacionyFE.cambiarPanel(pnlPrincipal, "pnl_ext");
    }

    private void cancelar() {
        RenderizarProcesosFidelizacionyFE renderizarProcesosFidelizacionyFE = new RenderizarProcesosFidelizacionyFE();
        renderizarProcesosFidelizacionyFE.cambiarPanel(pnlProcesosClientes, "pnlProcesoNormal");
        btnConsultar.setBackground(inactivo);
        btnContinuar.setBackground(activo);
        btnCancelar.setVisible(false);
    }

    private void verificarSiHayFacturacionParaImprimir() {
        if (chechFacturacionElectronica.isSelected()) {
            btnContinuar.setVisible(false);
            btnConsultar.setVisible(false);
            imprimirFe();
        } else {
            enviarProcesos();
        }

    }

    private void imprimirFe() {
        RenderizarProcesosFidelizacionyFE renderizarProcesosFidelizacionyFE = new RenderizarProcesosFidelizacionyFE();
        renderizarProcesosFidelizacionyFE.cambiarPanel(pnlProcesosClientes, "pnlConfirmacionComprobante");
    }

    public void mostrarPanelProcesos() {
        RenderizarProcesosFidelizacionyFE renderizarProcesosFidelizacionyFE = new RenderizarProcesosFidelizacionyFE();
        renderizarProcesosFidelizacionyFE.cambiarPanel(pnlProcesosClientes, "pnlProcesoNormal");
        btnContinuar.setVisible(true);
        btnConsultar.setVisible(true);
        btnConsultar.setBackground(inactivo);
    }

    private void continuarVentaCurso() {
        if (btnContinuar.getBackground().equals(activo)) {
            if (!chechFacturacionElectronica.isSelected() && !checkFidelizacion.isSelected()) {
                Runnable volver = () -> {
                    RenderizarProcesosFidelizacionyFE renderizarProcesosFidelizacionyFE = new RenderizarProcesosFidelizacionyFE();
                    renderizarProcesosFidelizacionyFE.cambiarPanel(pnlPrincipal, "fidelizacionYfacturaElectronica");
                    renderizarProcesosFidelizacionyFE.cambiarPanel(pnlProcesosClientes, "pnlProcesoNormal");
                };
                showMessage("SELECCIONA UNA OPCIÓN PARA CONTINUAR", ICONO_ERROR,
                        true, volver,
                        true, LetterCase.FIRST_UPPER_CASE);
            } else {
                if (ventaMarket) {
                    if (enviar != null) {

                        NovusUtils.printLn("Datos Fidelizacion: " + datosClienteFidelizacion);
                        NovusUtils.printLn("checkFidelizacion.isSelected() " + checkFidelizacion.isSelected());

                        datosClienteFidelizacion.addProperty("fidelizarMarket", checkFidelizacion.isSelected());
                        enviar.run();
                        enviar = null;
                    }
                } else {
                    verificarSiHayFacturacionParaImprimir();
                }
            }
        }
    }

    private void modificarVistaVentaCurso() {
        pnlOpcionDatosVehiculo.setVisible(false);
        pnlOpcionDatosVehiculo.setFocusable(false);
        pnlContenedorProcesos.setBounds(20, 30, 550, 510);
        lblCliente.setBounds(10, 100, 530, 50);
        nombreClienteFidelizacion.setBounds(10, 170, 530, 50);
        lblProcesos.setBounds(10, 240, 530, 50);
        nombreClienteFE.setBounds(10, 310, 530, 50);
    }

    public void mostrarPanelIngresoDatos() {
        NovusUtils.showPanel(pnlPrincipal, "pnlDatos");
        txtPlaca.requestFocus();
    }

    private void obtenerDatosVehiculo() {
        Recibo reciboInfo = informacionVentaCliente.getRecibo();
        boolean isEspecial = false;
        if (reciboInfo.getAtributos() != null && !reciboInfo.getAtributos().isJsonNull() && reciboInfo.getAtributos().get("is_especial") != null) {
            isEspecial = reciboInfo.getAtributos().get("is_especial").getAsBoolean();
        }
        if (reciboInfo.getAtributos() != null && !reciboInfo.getAtributos().isJsonNull() && recibo.getAtributos().get("tipo") != null && reciboInfo.getAtributos().get("tipo").getAsString().equals("014")) {
            isEspecial = true;
        }
        datosPlaca(reciboInfo, isEspecial);
    }

    public void datosPlaca(Recibo recibo, boolean isEspecial) {
        if (recibo.getPlaca() != null && !recibo.getPlaca().trim().equals("") || isEspecial) {
            NovusUtils.showPanel(pnlAsignarDatosVehiculo, "pnlInfoDatos");
            btnEditarDatos.setVisible(false);
            infoPlaca.setText(recibo.getPlaca());
            if (recibo.getOdometro() != null) {
                infoKm.setText(recibo.getOdometro());
            }
            if (recibo.getVoucher() != null) {
                infoOrden.setText(recibo.getVoucher());
            }
        }
    }

    public void agregarDatos() {
        NovusUtils.showPanel(pnlPrincipal, "fidelizacionYfacturaElectronica");
        NovusUtils.showPanel(pnlAsignarDatosVehiculo, "pnlInfoDatos");
        btnEditarDatos.setVisible(true);
        infoPlaca.setText(txtPlaca.getText());
        infoKm.setText(txtKms.getText());
        infoOrden.setText(txtAutorizacion.getText());
    }

    public void asignarDatosPlaca() {
        JsonObject datosVenta = new JsonObject();
        JsonObject atributosVenta = informacionVentaCliente.getAtributosVenta();

        atributosVenta.addProperty("vehiculo_placa", txtPlaca.getText());
        atributosVenta.addProperty("vehiculo_odometro", txtKms.getText());
        atributosVenta.addProperty("vehiculo_numero", txtAutorizacion.getText());

        datosVenta.addProperty("identificadorMovimiento", informacionVentaCliente.getInfoMovimiento().getId());
        datosVenta.addProperty("identificadorEquipo", Main.credencial.getEquipos_id());
        datosVenta.add("Cliente", atributosVenta);

        AsignacionClienteBean.agregarInformacionCliente("VentaCliente", datosVenta);
    }

    public void asignarClienteVenta() {
        if (!AsignacionClienteBean.getDatosCliente().isEmpty() && AsignacionClienteBean.getDatosCliente().containsKey("VentaCliente")) {
            AsignacionClienteBean.getDatosCliente().get("VentaCliente").getAsJsonObject().get("Cliente").getAsJsonObject().add("cliente", datosClienteFE);
            System.out.println("[asignacionDatosCliente] " + datosClienteFE.toString());
        }
    }

    public void validarTiempoFidelizacion() {
        if (informacionVentaCliente.haTranscurridoTiempo(NovusConstante.TIEMPO_MAXIMO_FIDELIZAR)) {
            deshabilitarFidelizacion("No se puede fidelizar venta realizada hace mas de 3 min");
        }
    }

    public void validarExisteFidelizacion() {
        boolean fidelizada;
        String mensaje = "";

        if (NovusConstante.HAY_INTERNET) {
            //fidelizada = Main.mdao.isVentaFidelizada(informacionVentaCliente.getInfoMovimiento().getId());
            fidelizada = new IsVentaFidelizadaUseCase(informacionVentaCliente.getInfoMovimiento().getId()).execute();
            mensaje = "Venta fidelizada anteriormente";
        } else {
            fidelizada = !FinByTransaccionProcesoUseCase.isValidEdit(informacionVentaCliente.getInfoMovimiento().getId());
            mensaje = "Datos de fidelizacion editados anteriormente";
        }

        if (fidelizada) {
            deshabilitarFidelizacion(mensaje);
        }
    }

    // public void validarExisteFidelizacion() {
    //     boolean fidelizada;
    //     String mensaje = "";

    //     if (NovusConstante.HAY_INTERNET) {
    //         fidelizada = Main.mdao.isVentaFidelizada(informacionVentaCliente.getInfoMovimiento().getId());
    //         mensaje = "Venta fidelizada anteriormente";
    //     } else {
    //         fidelizada = !Main.mdao.isValidEdit(informacionVentaCliente.getInfoMovimiento().getId());
    //         mensaje = "Datos de fidelizacion editados anteriormente";
    //     }

    //     if (fidelizada) {
    //         deshabilitarFidelizacion(mensaje);
    //     }
    // }

    private void deshabilitarFidelizacion(String mensaje) {
        checkFidelizacion.setFocusable(false);
        checkFidelizacion.setEnabled(false);
        validatePagoHasBonoViveTerpel();
        lblMensaje.setText(mensaje);
        btnModificarFidelizacion.setBackground(inactivo);
    }

    /**
     * Deshabilita la fidelización específicamente para ventas GLP
     */
    public void deshabilitarFidelizacionParaGLP() {
        checkFidelizacion.setFocusable(false);
        checkFidelizacion.setEnabled(false);
        checkFidelizacion.setSelected(false);
        checkFidelizacion.setBackground(inactivo);
        validatePagoHasBonoViveTerpel();
        lblMensaje.setText("No es posible fidelizar en ventas de GLP");
        btnModificarFidelizacion.setEnabled(false);
        btnModificarFidelizacion.setBackground(inactivo);
        NovusUtils.printLn("🔒 [GLP] Fidelización deshabilitada para venta GLP");
    }

    private void fidelizar() {
        informacionVentaCliente.setFidelizar(checkFidelizacion.isSelected());
    }

    public JsonObject getDatosClienteFE() {
        return datosClienteFE;
    }

    public void setDatosClienteFE(JsonObject datosClienteFE) {
        this.datosClienteFE = datosClienteFE;
    }

    public JsonObject getDatosClienteFidelizacion() {
        return datosClienteFidelizacion;
    }

    public void setDatosClienteFidelizacion(JsonObject datosClienteFidelizacion) {
        this.datosClienteFidelizacion = datosClienteFidelizacion;
    }

    //------------------------------------------//
    //-----------------------------------------//
    public ReciboExtended getRecibo() {
        return recibo;
    }

    public void setRecibo(ReciboExtended recibo) {
        this.recibo = recibo;
    }

    public Runnable getRegresar() {
        return regresar;
    }

    public void setRegresar(Runnable regresar) {
        this.regresar = regresar;
    }

    public Runnable getEnviar() {
        return enviar;
    }

    public void setEnviar(Runnable enviar) {
        this.enviar = enviar;
    }

    public boolean isAsignarDatos() {
        return asignarDatos;
    }

    public void setAsignarDatos(boolean asignarDatos) {
        this.asignarDatos = asignarDatos;
    }

    public boolean isFidelizacion() {
        return fidelizacion;
    }

    public void setFidelizacion(boolean fidelizacion) {
        this.fidelizacion = fidelizacion;
    }

    public boolean isVentaMarket() {
        return ventaMarket;
    }

    public void setVentaMarket(boolean ventaMarket) {
        this.ventaMarket = ventaMarket;
    }

    public InformacionVentaCliente getInformacionVentaCliente() {
        return informacionVentaCliente;
    }

    public void setInformacionVentaCliente(InformacionVentaCliente informacionVentaCliente) {
        this.informacionVentaCliente = informacionVentaCliente;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    /**
     * Verifica si la venta actual es de tipo GLP basándose en el producto de la manguera
     * @return true si es GLP, false en caso contrario
     */
    private boolean esVentaGLPActual() {
        try {
            if (this.recibo != null && this.recibo.getCara() != null && !this.recibo.getCara().isEmpty()) {
                int cara = Integer.parseInt(this.recibo.getCara());
                if (cara > 0) {
                    NovusUtils.printLn("🔍 [GLP] Validando producto dinámicamente para cara: " + cara);
                    
                    // Obtener el ID del producto desde la consulta
                    long productoId = new GetMovimientoIdDesdeCaraUseCase().execute(cara);
                    NovusUtils.printLn("🔍 [GLP] Producto ID obtenido dinámicamente: " + productoId);
                    
                    if (productoId > 0) {
                        // Obtener la descripción del producto
                        String descripcionProducto = new GetProductNameUseCase(productoId).execute();
                        NovusUtils.printLn("🔍 [GLP] Descripción producto dinámica: '" + descripcionProducto + "'");
                        
                        if (descripcionProducto != null && !descripcionProducto.trim().isEmpty()) {
                            // Verificar si la descripción contiene "GLP" (case insensitive)
                            boolean esGLP = descripcionProducto.toUpperCase().contains("GLP");
                            NovusUtils.printLn("🔍 [GLP] ¿Es producto GLP dinámicamente? " + esGLP);
                            return esGLP;
                        } else {
                            NovusUtils.printLn("⚠️ [GLP] Descripción de producto vacía o nula dinámicamente");
                        }
                    } else {
                        NovusUtils.printLn("⚠️ [GLP] ID de producto no válido dinámicamente: " + productoId);
                    }
                } else {
                    NovusUtils.printLn("⚠️ [GLP] Cara no válida dinámicamente: " + cara);
                }
            } else {
                NovusUtils.printLn("⚠️ [GLP] Recibo o cara no válida dinámicamente");
            }
            
            NovusUtils.printLn("⚠️ [GLP] No se pudo determinar el tipo de producto dinámicamente - asumiendo NO es GLP");
            return false;
            
        } catch (Exception e) {
            NovusUtils.printLn("❌ [ERROR GLP] Error al verificar tipo de producto dinámicamente: " + e.getMessage());
            return false;
        }
    }

}
