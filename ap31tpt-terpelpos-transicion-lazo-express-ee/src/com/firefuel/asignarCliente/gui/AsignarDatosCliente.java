package com.firefuel.asignarCliente.gui;

import com.WT2.Containers.Dependency.SingletonFidelizacionyFacturacionBuilder;
import com.WT2.appTerpel.domain.valueObject.MediosPagosDescription;
import com.WT2.appTerpel.domain.valueObject.TransaccionMessageView;
import com.WT2.commons.domain.entity.ParametrosMensajes;
import com.WT2.commons.domain.entity.TransaccionProceso;
import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.commons.infraestructure.builders.ParametrosMensajesBuilder;
import com.WT2.loyalty.domain.entities.beans.FoundClient;
import com.application.useCases.movimientos.BuscarTransaccionDatafonoCaseUse;
import com.bean.AsignacionClienteBean;
import com.bean.BonoViveTerpel;
import com.bean.MediosPagosBean;

import com.bean.MovimientosBean;
import com.bean.ReciboExtended;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.FacturacionElectronicaDao;

import com.dao.SurtidorDao;
import com.firefuel.*;
import com.firefuel.asignarCliente.beans.InformacionVentaCliente;
import com.firefuel.asignarCliente.beans.RespuestaMensaje;
import com.firefuel.asignarCliente.useCase.FidelizarVentaCliente;
import com.firefuel.asignarCliente.useCase.ImprimirVenta;

import com.firefuel.facturacion.electronica.FacturaElectronicaVentaEnVivo;
import com.firefuel.fidelizacionYfacturaElectronica.VentasCurso;
import com.firefuel.utils.ImageCache;
import com.firefuel.utils.ShowMessageSingleton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.neo.app.bean.Recibo;
import com.services.TimeOutsManager;

import java.awt.CardLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.swing.*;

import static com.firefuel.Main.mdao;
import com.application.useCases.wacherparametros.FindByParameterUseCase;
import com.application.commons.CtWacherParametrosEnum;
public class AsignarDatosCliente extends javax.swing.JPanel {

    FindByParameterUseCase findByParameterUseCase = new FindByParameterUseCase(CtWacherParametrosEnum.CODIGO.getColumnName(), "REMISION");
    long identificadorTipoDocumento = 0;
    int cantidadCaracteres = 10;
    String reNumerico = "[0-9]*";
    String caracteresPermitidos = reNumerico;
    String consumidorFinal = "222222222222";
    String numeroDocumento,descripcionTipoDocumento;
    MovimientosBean movimiento;
    Runnable regresar;
    ReciboExtended reciboRec;
    Recibo recibo;
    long idTransmision;
    JsonObject atributosVenta;
    InformacionVentaCliente informacionVentaCliente;
    InfoViewController parent;
    public TransaccionProceso transaccionProceso = null;
    public boolean isProcesoRechazada = false;
    FidelizacionyFacturacionElectronica fidelizacionyFacturacoinElectronica;
    FacturaElectronicaVentaEnVivo facturaElectronicaVentaEnVivo;
    private static MedioPagosConfirmarViewController viewMedios;
    SurtidorDao sdao;
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static ScheduledFuture<?> tareaProgramada;
    boolean datafono,isAppTerpelPendiente,hayDatafono;
    VentasCurso ventasCurso;
    JsonArray bonosObtenidos;
    FoundClient foundClient;
    ArrayList<MediosPagosBean> mediosPagoVenta;
    AsignacionClienteBean asignacionClienteBean;
    RespuestaMensaje respuestaMensaje;
    public static ImageIcon botonBlanco1,
    btnOk, fndRumbo, btnBad;
    JsonObject datosCliente;
    ValidacionBonosViveTerpel validacionBonosViveTerpel;
    TimeOutsManager timeOutsManager;
    FacturacionElectronicaDao electronicaDao;

    public void loadIconsAndImages(){
        botonBlanco1 = ImageCache.getImage("/com/firefuel/resources/botones/boton-blanco-1.png");
        btnOk = ImageCache.getImage("/com/firefuel/resources/botones/btnOk.png");
        fndRumbo = ImageCache.getImage("/com/firefuel/resources/fndRumbo.png");
        btnBad = ImageCache.getImage("/com/firefuel/resources/botones/btnBad.png");
    }

    public AsignarDatosCliente(Runnable regresar, ReciboExtended reciboRec, Recibo recibo, MovimientosBean movimiento, InfoViewController parent) {
        this.regresar = regresar;
        this.reciboRec = reciboRec;
        this.recibo = recibo;
        this.movimiento = new MovimientosBean();
        this.informacionVentaCliente = new InformacionVentaCliente();
        this.sdao = new SurtidorDao();
        this.parent = parent;
        this.ventasCurso = new VentasCurso();
        this.bonosObtenidos = new JsonArray();
        this.mediosPagoVenta = new ArrayList<>();
        this.timeOutsManager = new TimeOutsManager();
        this.facturaElectronicaVentaEnVivo = new FacturaElectronicaVentaEnVivo();
        this.atributosVenta = new JsonObject();
        this.asignacionClienteBean = new AsignacionClienteBean();
        this.respuestaMensaje = new RespuestaMensaje();
        this.electronicaDao = new FacturacionElectronicaDao();
        this.datosCliente = new JsonObject();
        this.validacionBonosViveTerpel = new ValidacionBonosViveTerpel();
        initComponents();
        consultaAsignarCliente();
        actualizarEstadoVenta();
        this.asignacionClienteBean.getDatosCliente().clear();
        setViews();
        loadInfo();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlContainer = new javax.swing.JPanel();
        pnlMensaje = new javax.swing.JPanel();
        jCerrar = new javax.swing.JLabel();
        jMensaje = new javax.swing.JLabel();
        jIcono = new javax.swing.JLabel();
        lblFondo = new javax.swing.JLabel();
        setMinimumSize(new java.awt.Dimension(1280, 800));
        setPreferredSize(new java.awt.Dimension(1280, 800));
        setLayout(null);
        pnlContainer.setLayout(new java.awt.CardLayout());
        pnlMensaje.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jCerrar.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jCerrar.setForeground(new java.awt.Color(153, 3, 3));
        jCerrar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jCerrar.setIcon(botonBlanco1); // NOI18N
        jCerrar.setText("CERRAR");
        jCerrar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jCerrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jCerrarMouseClicked(evt);
            }
        });
        pnlMensaje.add(jCerrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 10, 290, 70));
        jMensaje.setFont(new java.awt.Font("Tahoma", 1, 28)); // NOI18N
        jMensaje.setForeground(new java.awt.Color(186, 12, 47));
        jMensaje.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jMensaje.setText("OK");
        jMensaje.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pnlMensaje.add(jMensaje, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 210, 710, 390));
        jIcono.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jIcono.setIcon(btnOk); // NOI18N
        jIcono.setToolTipText("");
        pnlMensaje.add(jIcono, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 220, 330, 360));
        lblFondo.setIcon(fndRumbo); // NOI18N
        pnlMensaje.add(lblFondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1280, 800));
        pnlContainer.add(pnlMensaje, "pnlMensaje");
        add(pnlContainer);
        pnlContainer.setBounds(0, 0, 1280, 800);
    }// </editor-fold>//GEN-END:initComponents

    private void jCerrarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCerrarMouseClicked
        cerrarMensaje();
    }//GEN-LAST:event_jCerrarMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JLabel jCerrar;
    private javax.swing.JLabel jIcono;
    private javax.swing.JLabel jMensaje;
    private javax.swing.JLabel lblFondo;
    private javax.swing.JPanel pnlContainer;
    private javax.swing.JPanel pnlMensaje;
    private static ClienteFacturaElectronica viewFe;

    // End of variables declaration//GEN-END:variables

    private void consultaAsignarCliente() {
        descripcionTipoDocumento = "CONSUMIDOR FINAL";
        numeroDocumento = consumidorFinal;
        identificadorTipoDocumento = NovusConstante.DOCUMENTO_CLIENTES_VARIOS;
    }

    private void consultar() {
        Runnable regresarRunnable = () -> {
            cerrar();
        };

        Runnable enviar = () -> {
            pnlContainer.revalidate();
            pnlContainer.repaint();
            enviar();
        };

        fidelizacionyFacturacoinElectronica = SingletonFidelizacionyFacturacionBuilder
                .getFidelizacionyFacturacionBuilder()
                .setRecibo(reciboRec)
                .setNumeroDocumento(numeroDocumento)
                .setTipoDocumento(descripcionTipoDocumento)
                .setRegresar(regresarRunnable)
                .setFidelizacion(false)
                .setAsignarDatos(true)
                .setInformacionVentaCliente(informacionVentaCliente)
                .setEnviar(enviar)
                .build();

        ventasCurso.cargarPanelExterno(pnlContainer, fidelizacionyFacturacoinElectronica);
    }

    public void cerrar() {
        NovusUtils.printLn("cerrar");
        if (regresar != null) {
            this.asignacionClienteBean.getDatosCliente().clear();
            mdao.actualizarEstadoTransmision(3, idTransmision);
            mdao.actualizarEstadoMovimientosClientes(3, idTransmision);
            regresar.run();
        }
    }

    public void regresar() {
        if (regresar != null) {
            regresar.run();
        }
    }

    private void actualizarEstadoVenta() {
        atributosVenta = mdao.obtenerAtributosVenta(movimiento.getId());
        idTransmision = movimiento.getAtributos().get("idTransmision").getAsLong();
        //Se coloca transmision en valor 4 antes de acceder a la vista
        System.out.println("👍 Realizo Cambio a estado 4 para el estado de venta");
        mdao.actualizarEstadoTransmision(4, idTransmision);
        mdao.actualizarEstadoMovimientosClientes(4, idTransmision);
    }

    void loadInfo() {
        this.informacionVentaCliente.setRecibo(recibo);
        this.informacionVentaCliente.setReciboRec(reciboRec);
        this.informacionVentaCliente.setAtributosVenta(atributosVenta);
        this.informacionVentaCliente.setInfoMovimiento(movimiento);
        this.transaccionProceso = mdao.buscarAppTerpelRechazadaOnoExiste(this.movimiento.getId());
        this.isProcesoRechazada = this.transaccionProceso.esRechazadoOnoExiste();
        consultar();
    }

    void enviar() {
        if (!isProcesoRechazada) {
            if (this.transaccionProceso.getIdintegracion() == 1) {
                String APMessage = "Se enviara factura electronica una vez se confirme el pago con APPTERPEL".toUpperCase();
                enviarVentaSinFacturarAppTerpel(idTransmision, APMessage);
            } else {
                RespuestaMensaje respuesta = enviarVentaSinFacturar(idTransmision);
                showMessage(respuesta.getMensaje(),
                        respuesta.getIconMensaje(),
                        true, () -> regresar(),
                        true, LetterCase.FIRST_UPPER_CASE);
            }
        } else {
            // PASA A LA VENTANA DE MEDIOS DE PAGOS // 5UVT FE
            vistaMediosPago();
        }
    }

    private void showDialog(JDialog dialog) {
        JPanel panel = (JPanel) dialog.getContentPane();
        mostrarSubPanel(panel);
    }

    private void vistaMediosPago() {
        showDialog(viewMedios);
    }



    private void enviarVentaSinFacturarAppTerpel(long numeroVenta, String msg) {
        jIcono.setText("");
        jMensaje.setText("");
        NovusUtils.printLn("[enviarVentaSinFacturar]" + numeroVenta);
        String icon = "/com/firefuel/resources/btOk.png";
        datosCliente.addProperty("documentoCliente", numeroDocumento);
        datosCliente.addProperty("identificacion_cliente", identificadorTipoDocumento);
        RespuestaMensaje respuestaMensaje = facturaElectronicaVentaEnVivo.datosSinfacturar(fidelizacionyFacturacoinElectronica.datosClienteFE, datosCliente, numeroVenta, true, this.movimiento.getId(), msg, true);
        mdao.actualizarEstadoTransmision(3, numeroVenta);
        mdao.actualizarEstadoMovimientosClientes(3, numeroVenta);

        if (respuestaMensaje.isError()) {
            icon = "/com/firefuel/resources/btBad.png";
        }

        showMessage(respuestaMensaje.getMensaje(),
                icon,
                true, () -> regresar(),
                true, LetterCase.FIRST_UPPER_CASE);
    }



    private RespuestaMensaje enviarVentaSinFacturar(long numeroVenta) {
        RespuestaMensaje respuestaMensaje;
        String datafonoMsg = "Se enviará factura electrónica una vez se confirme el pago con datafono";
        NovusUtils.printLn("🔎 [enviarVentaSinFacturar]" + numeroVenta);
        String icon = "/com/firefuel/resources/btOk.png";

        // Si numeroDocumento está vacío, consultamos en la base de datos
        if (numeroDocumento == null || numeroDocumento.isEmpty() || consumidorFinal.equals(numeroDocumento)) {
            JsonObject datosDB = consultarClienteDesdeBD(numeroVenta);
            if (datosDB != null) {
                datosCliente.addProperty("documentoCliente", datosDB.get("numero_documento").getAsString());
                datosCliente.addProperty("identificacion_cliente", datosDB.get("tipo_documento").getAsInt());
            }
        } else {
            datosCliente.addProperty("documentoCliente", numeroDocumento);
            datosCliente.addProperty("identificacion_cliente", identificadorTipoDocumento);
        }

        System.out.println("😶‍🌫️ Datos de cliente después de la consulta: " + datosCliente);

        if (new BuscarTransaccionDatafonoCaseUse(this.movimiento.getId()).execute()) {
            
            respuestaMensaje = facturaElectronicaVentaEnVivo.datosSinfacturar(fidelizacionyFacturacoinElectronica.datosClienteFE, datosCliente, numeroVenta, true, this.movimiento.getId(), datafonoMsg, false);
        } else {
            respuestaMensaje = facturaElectronicaVentaEnVivo.datosSinfacturar(fidelizacionyFacturacoinElectronica.datosClienteFE, datosCliente, numeroVenta, false, this.movimiento.getId(), datafonoMsg, false);
        }

        if (respuestaMensaje.isError()) {
            icon = "/com/firefuel/resources/btBad.png";
        }

        if (fidelizacionyFacturacoinElectronica.datosClienteFE.get("pendiente_impresion").getAsBoolean()) {
            NovusUtils.printLn("Ejecutando impresión en enviarVentaSinFacturar");
            ImprimirVenta.imprimirVentaFe(numeroVenta, idTransmision);
        }

        respuestaMensaje.setIconMensaje(icon);
        return respuestaMensaje;
    }

    private JsonObject consultarClienteDesdeBD(long idTransmision) {
        JsonObject clienteDB = new JsonObject();
        String consulta = "SELECT numero_documento, tipo_documento FROM ct_movimientos_cliente WHERE id_transmision = ?";

        try (Connection conexion = Main.obtenerConexion("lazoexpresscore");
             PreparedStatement pstmt = conexion.prepareStatement(consulta)) {
            pstmt.setLong(1, idTransmision);
            try(ResultSet rs = pstmt.executeQuery()){
                if(rs.next()){
                    clienteDB.addProperty("numero_documento", rs.getString("numero_documento"));
                    clienteDB.addProperty("tipo_documento", rs.getInt("tipo_documento"));
                }
            }
        } catch (SQLException e) {
            NovusUtils.printLn("❌ Error al consultar cliente desde la BD: " + e.getMessage());
        }
        return clienteDB;
    }



    private void showMessage(String msj, String ruta,
                             boolean habilitar, Runnable runnable,
                             boolean autoclose, String letterCase) {
        ParametrosMensajes parametrosMensajes = new ParametrosMensajesBuilder().setMsj(msj)
                .setRuta(ruta).setHabilitar(habilitar)
                .setRunnable(runnable).setAutoclose(autoclose)
                .setLetterCase(letterCase).build();
        mostrarSubPanel(ShowMessageSingleton.showMassegesInstance().execute(parametrosMensajes));
    }

    private void mostrarSubPanel(JPanel panel) {
        CardLayout layout = (CardLayout) pnlContainer.getLayout();
        pnlContainer.add("pnl_ext", panel);
        layout.show(pnlContainer, "pnl_ext");
    }


    static void setViewMedios(MedioPagosConfirmarViewController medios) {
        AsignarDatosCliente.viewMedios = medios;
    }

    @SuppressWarnings("unchecked")

    public void continuarProceso() {
        Gson gson = new Gson();
        bonosObtenidos = new JsonArray();
        ArrayList<MediosPagosBean> mediosDePagoVenta = (ArrayList<MediosPagosBean>) viewMedios.mediosPagoVenta.clone();
        JsonObject info = gson.toJsonTree(AsignacionClienteBean.getDatosCliente()).getAsJsonObject();
        JsonArray medios = info.get("MediosPago").getAsJsonObject().get("mediosDePagos").getAsJsonArray();
        JsonArray bonosVenta = mdao.getBonosVenta(this.movimiento.getId());
        JsonArray bonosArray = new JsonArray();
        boolean isBonoVive = false;
        boolean isDatafono = false;

        // 🔍 Depuración: Verificar si la lista de medios tiene datos
        System.out.println("📌 Medios de pago obtenidos: " + medios);
        if (medios.size() == 0) {
            System.out.println("⚠️ La lista de medios de pago está VACÍA. No hay datafono.");
        }

        setIsDatafono(false);

        for (JsonElement medio : medios) {
            JsonObject medioIt = medio.getAsJsonObject();

            // 🔍 Depuración: Imprimir cada medio de pago antes de evaluarlo
            System.out.println("🔎 Evaluando medio de pago: " + medioIt);

            if (medioIt.has("descripcion")) {
                String descripcion = medioIt.get("descripcion").getAsString().trim(); // 🔥 Eliminar espacios en blanco
                System.out.println("✅ Descripción del medio: '" + descripcion + "'");

                if (descripcion.equalsIgnoreCase("CON DATAFONO")) { // ✅ Comparación más robusta
                    isDatafono = true;
                    setIsDatafono(true);
                    System.out.println("✅ Se detectó un medio de pago con datafono.");
                }
            } else {
                System.out.println("⚠️ El objeto medio de pago no tiene la clave 'descripcion'.");
            }

            if (medioIt.has("ct_medios_pagos_id") && medioIt.get("ct_medios_pagos_id").getAsLong() == 20000) {
                isBonoVive = true;
                System.out.println("🎉 Se detectó un bono Vive Terpel.");
            }
        }

        if (isBonoVive && isDatafono && !MedioPagosConfirmarViewController.bonosValidados) {
            showMessage("REDIMIENDO BONOS, POR FAVOR ESPERE....",
                    "/com/firefuel/resources/loader_fac.gif",
                    false, null,
                    false, LetterCase.FIRST_UPPER_CASE);

            for (MediosPagosBean medio : mediosDePagoVenta) {
                if (medio.isPagosExternoValidado()) {
                    long salesForceIdMp = sdao.getCodigoSalesForceMP(medio.getId());
                    for (BonoViveTerpel bono : medio.getBonosViveTerpel()) {
                        if (!NovusUtils.existeBono(bonosVenta, Long.parseLong(bono.getVoucher()))) {
                            JsonObject bonoViveTerpelMP = new JsonObject();
                            bonoViveTerpelMP.addProperty("IFP", salesForceIdMp);
                            bonoViveTerpelMP.addProperty("VFP", bono.getValor());
                            bonoViveTerpelMP.addProperty("AFP", bono.getVoucher());
                            bonosArray.add(bonoViveTerpelMP);
                        }
                    }
                }
            }
            bonosObtenidos = bonosArray;

            this.timeOutsManager.setTimeoutUtilManager(1, () -> {
                boolean isDatafonoVenta = getIsDatafono();
                JsonObject respuestaReclamacion = validacionBonosViveTerpel.ReclamacionBonoViveTerpel(reciboRec, mediosDePagoVenta, movimiento.getId(), bonosObtenidos);
                JsonObject respuesta = validacionBonosViveTerpel.procesamientRespuestaReclamacion(respuestaReclamacion, movimiento.getId(), bonosObtenidos);
                procesarRespuestaBonos(respuesta, isDatafonoVenta);
            });

        } else if (isDatafono) {
            System.out.println("✅ Se detectó que es una venta con datafono. Continuando proceso...");
            continuarProcesoFe(); // 🔥 Aquí llamamos al proceso correcto
        } else {
            System.out.println("🔄 No es venta con datafono. Continuando proceso sin datafono...");
            continuarProcesoSinDatafono();
        }
    }


    public void iniciarProcesoSinDatafono() {
        Gson gson = new Gson();
        bonosObtenidos = new JsonArray();
        @SuppressWarnings("unchecked")
        ArrayList<MediosPagosBean> mediosDePagoVenta = (ArrayList<MediosPagosBean>) viewMedios.mediosPagoVenta.clone();
        JsonObject info = gson.toJsonTree(AsignacionClienteBean.getDatosCliente()).getAsJsonObject();
        JsonArray medios = info.get("MediosPago").getAsJsonObject().get("mediosDePagos").getAsJsonArray();
        JsonArray bonosVenta = mdao.getBonosVenta(this.movimiento.getId());
        JsonArray bonosArray = new JsonArray();
        boolean isBonoVive = false;
        setIsDatafono(false);
        for (JsonElement medio : medios) {
            JsonObject medioIt = medio.getAsJsonObject();
            if (medioIt.get("ct_medios_pagos_id").getAsLong() == 20000) {
                isBonoVive = true;
            }
        }
        if (isBonoVive && !MedioPagosConfirmarViewController.bonosValidados) {
            showMessage("REDIMIENDO BONOS, POR FAVOR ESPERE....",
                    "/com/firefuel/resources/loader_fac.gif",
                    false, null,
                    false, LetterCase.FIRST_UPPER_CASE);
            for (MediosPagosBean medio : mediosDePagoVenta) {
                if (medio.isPagosExternoValidado()) {
                    long salesForceIdMp = sdao.getCodigoSalesForceMP(medio.getId());
                    for (BonoViveTerpel bono : medio.getBonosViveTerpel()) {
                        if (!NovusUtils.existeBono(bonosVenta, Long.parseLong(bono.getVoucher()))) {
                            JsonObject bonoViveTerpelMP = new JsonObject();
                            bonoViveTerpelMP.addProperty("IFP", salesForceIdMp);
                            bonoViveTerpelMP.addProperty("VFP", bono.getValor());
                            bonoViveTerpelMP.addProperty("AFP", bono.getVoucher());
                            bonosArray.add(bonoViveTerpelMP);
                        }
                    }
                }
            }
            bonosObtenidos = bonosArray;

            this.timeOutsManager.setTimeoutUtilManager(1, () -> {
                JsonObject respuestaReclamacion = validacionBonosViveTerpel.ReclamacionBonoViveTerpel(reciboRec, mediosDePagoVenta, movimiento.getId(), bonosObtenidos);
                JsonObject respuesta = validacionBonosViveTerpel.procesamientRespuestaReclamacion(respuestaReclamacion, movimiento.getId(), bonosObtenidos);
                procesarRespuestaBonos(respuesta, false);
            });
        } else {
            continuarProcesoSinDatafono();
        }
    }

    // public void setTimeout(int delay, Runnable runnable) {
    //     new Thread(() -> {
    //         try {
    //             Thread.sleep((long) delay * 1000);
    //             if (runnable != null) {
    //                 runnable.run();
    //             }
    //         } catch (InterruptedException e) {
    //             NovusUtils.printLn("Interrupted.setTimeout" + e.getLocalizedMessage());
    //             Thread.currentThread().interrupt();
    //         }
    //     }).start();
    // }

    public void setIsDatafono(boolean isDatafono) {

        this.hayDatafono = isDatafono;
    }

    public boolean getIsDatafono() {
        return hayDatafono;
    }

    public void procesarRespuestaBonos(JsonObject respuesta, boolean isDatafono) {

        System.out.println("👽👽 obteniendo respuesta: " + respuesta);

        NovusUtils.printLn("procesarRespuestaBonos(JsonObject respuesta, boolean isDatafono)");
        String mensaje = respuesta.get("mensaje").getAsString();
        if (respuesta.get("aprobado").getAsBoolean()) {
            Runnable runnable = () -> {
                if (isDatafono) {
                    continuarProcesoFe();
                } else {
                    continuarProcesoSinDatafono();
                }
            };
            if (!AsignacionClienteBean.getDatosCliente().isEmpty() && AsignacionClienteBean.getDatosCliente().containsKey("VentaCliente")) {
                AsignacionClienteBean.getDatosCliente().get("VentaCliente").getAsJsonObject().get("Cliente").getAsJsonObject().add("Bonos_Vive_Terpel", bonosObtenidos);
            }
            showMessage(mensaje, "/com/firefuel/resources/btOk.png",
                    true, runnable,
                    true, LetterCase.FIRST_UPPER_CASE);
        } else {
            showMessage(mensaje, "/com/firefuel/resources/btBad.png",
                    true, this::vistaMediosPago,
                    true, LetterCase.FIRST_UPPER_CASE);//Validar
        }
    }

    private void mostrarMenuPrincipal() {
        NovusConstante.ventanaFE = Boolean.FALSE;
        fidelizacionyFacturacoinElectronica.mostrarPanelProcesos();
        ventasCurso.cargarPanelExterno(pnlContainer, fidelizacionyFacturacoinElectronica);
    }

    private void devolverDatafonos() {
        showDialog(viewMedios);
    }

    static void setViewFe(ClienteFacturaElectronica view) {
        AsignarDatosCliente.viewFe = view;

    }

    private void continuarConfirmarMediosView() {
        NovusConstante.ventanaFE = Boolean.FALSE;
        showDialog(viewMedios);
    }

    private void setViews() {
        MedioPagosConfirmarViewController viewmedios = new MedioPagosConfirmarViewController(parent, true, movimiento, false, false, reciboRec, this::continuarProceso, this::mostrarMenuPrincipal, true, this::iniciarProcesoSinDatafono);
        viewmedios.anulacion = Boolean.FALSE;
        setViewMedios(viewmedios);

        long numeroVenta = reciboRec.getAtributos().get("idTransmision").getAsLong();
        boolean asignarDatos = !new BuscarTransaccionDatafonoCaseUse(this.movimiento.getId()).execute();

        ClienteFacturaElectronica cliente = new ClienteFacturaElectronica(parent, true, numeroVenta, true, this::mostrarMenuPrincipal, this::continuarConfirmarMediosView, asignarDatos, true, true, this.movimiento, mediosPagoVenta);
        cliente.transaccionProceso = mdao.buscarAppTerpelRechazadaOnoExiste(this.movimiento.getId());
        cliente.isProcesoRechazada = cliente.transaccionProceso.esRechazadoOnoExiste();
        cliente.regresarHIstotial = this::cerrarVenta;
        cliente.jTextField1.requestFocus();
        cliente.jTextField1.requestFocusInWindow();
        setViewFe(cliente);
    }

    public void continuarProcesoFe() {
        System.out.println("👍 Estoy en este metodo para continuar PorcesoFe");
        NovusConstante.ventanaFE = Boolean.TRUE;
        viewFe.limpiarComponentes();
        viewFe.cerrarTodo = this::cerrarVenta;
        viewFe.devolverDatafonos = this::devolverDatafonos;
        showDialog(viewFe);
        long transmisionId = movimiento.getAtributos().get("idTransmision").getAsLong();
        NovusUtils.printLn("enviando el id de transmision en el proceso con datafono-> " + transmisionId);
        viewFe.enviarPagoAdquiriente(idTransmision);

    }


    private void continuarProcesoSinDatafono() {
        if (viewMedios.anulacion) {
            long transmisionId = movimiento.getAtributos().get("idTransmision").getAsLong();
            NovusUtils.printLn("enviando el id de transmision en el procesocontinuarProcesoSinDatafono() para anulacion  " + transmisionId);
            updateInfoCliente(transmisionId);
            Runnable ucerrar = () -> {
                cerrarVenta();
            };
            showMessage("Se enviara factura electronica una vez se confirme la anulacion en el datafono".toUpperCase(),
                    "/com/firefuel/resources/btOk.png",
                    true, ucerrar,
                    true, LetterCase.FIRST_UPPER_CASE);
        } else {
            NovusUtils.printLn("****************************");
            NovusUtils.printLn("Continuar Proceso sin Datafono");
            NovusUtils.printLn("****************************");
            long transmisionId = movimiento.getAtributos().get("idTransmision").getAsLong();
            NovusUtils.printLn("enviando el id de transmision en el proceso sin datafono-> " + transmisionId);
            updateInfoCliente(transmisionId);
            viewMedios.mediosPagoVenta.clear();
            if (isAppTerpelPendiente) {
                mostarMensaje(TransaccionMessageView.NOTIFICIACION_APPTERPEL, NovusConstante.TIEMPO_MENSAJE_APPTERPEL);
            } else {
                mostarMensaje("Factura electrónica enviada", 4);
                fidelizar();
            }
        }
    }

    public void updateInfoCliente(long idTransmision) {
        JsonObject infoActualizarCliente = new JsonObject();
        Gson gson = new Gson();
        NovusUtils.printLn("recibiendo el id de la transmision -> " + idTransmision);
        infoActualizarCliente = gson.toJsonTree(AsignacionClienteBean.getDatosCliente()).getAsJsonObject();

        long idMovimiento = 0l;

        if (findByParameterUseCase.execute()) {
            idMovimiento = mdao.buscarMOvimientoIdRemision(idTransmision);
        } else {
            idMovimiento = mdao.buscarMOvimientoId(idTransmision);
        }

        infoActualizarCliente.get("VentaCliente").getAsJsonObject().addProperty("identificadorMovimiento", idMovimiento);

        boolean respuestaAsignacion = mdao.asignarDatosCliente(infoActualizarCliente.toString());
        if (infoActualizarCliente.has("MediosPago")) {
            JsonArray mediosPago = infoActualizarCliente.get("MediosPago").getAsJsonObject().get("mediosDePagos").getAsJsonArray();

            if (respuestaAsignacion) {
                actualizarInformacionCliente(mediosPago, idTransmision);
                this.asignacionClienteBean.getDatosCliente().clear();
            } else {
                int sincronizado = isVentaDatafono(mediosPago) ? 4 : 3;
                mdao.actualizarEstadoTransmision(sincronizado, idTransmision);
                mdao.actualizarEstadoMovimientosClientes(sincronizado, idTransmision);
                showMessage("ERROR AL ASIGNAR DATOS CLIENTE",
                        "/com/firefuel/resources/btBad.png",
                        true, () -> cerrar(),
                        true, LetterCase.FIRST_UPPER_CASE);
            }
            // ACTUALIZAR INFORMACION DE CLIENTE Y ENVIAR FE
            enviarVentaSinFacturar(idTransmision);
        }
    }

    // public void updateInfoCliente(long idTransmision) {
    //     JsonObject infoActualizarCliente = new JsonObject();
    //     Gson gson = new Gson();
    //     NovusUtils.printLn("recibiendo el id de la transmision -> " + idTransmision);
    //     infoActualizarCliente = gson.toJsonTree(AsignacionClienteBean.getDatosCliente()).getAsJsonObject();

    //     long idMovimiento = 0l;

    //     if (mdao.remisionActiva()) {
    //         idMovimiento = mdao.buscarMOvimientoIdRemision(idTransmision);
    //     } else {
    //         idMovimiento = mdao.buscarMOvimientoId(idTransmision);
    //     }

    //     infoActualizarCliente.get("VentaCliente").getAsJsonObject().addProperty("identificadorMovimiento", idMovimiento);

    //     boolean respuestaAsignacion = mdao.asignarDatosCliente(infoActualizarCliente.toString());
    //     if (infoActualizarCliente.has("MediosPago")) {
    //         JsonArray mediosPago = infoActualizarCliente.get("MediosPago").getAsJsonObject().get("mediosDePagos").getAsJsonArray();

    //         if (respuestaAsignacion) {
    //             actualizarInformacionCliente(mediosPago, idTransmision);
    //             this.asignacionClienteBean.getDatosCliente().clear();
    //         } else {
    //             int sincronizado = isVentaDatafono(mediosPago) ? 4 : 3;
    //             mdao.actualizarEstadoTransmision(sincronizado, idTransmision);
    //             mdao.actualizarEstadoMovimientosClientes(sincronizado, idTransmision);
    //             showMessage("ERROR AL ASIGNAR DATOS CLIENTE",
    //                     "/com/firefuel/resources/btBad.png",
    //                     true, () -> cerrar(),
    //                     true, LetterCase.FIRST_UPPER_CASE);
    //         }
    //         // ACTUALIZAR INFORMACION DE CLIENTE Y ENVIAR FE
    //         enviarVentaSinFacturar(idTransmision);
    //     }
    // }

    private void actualizarInformacionCliente(JsonArray arrayMedios, long idTransmision) {
        NovusUtils.printLn("actualizarInformacionCliente(JsonArray arrayMedios, long idTransmision)");
        this.datafono = isVentaDatafono(arrayMedios);
        int sincronizado = this.datafono ? 4 : 2;

        if (!this.datafono) {
            // APPTERPEL VALIDA  QUE NO SE PONGA EN ESTADO 2
            this.isAppTerpelPendiente = NovusUtils.verificarMedioPago(arrayMedios, MediosPagosDescription.APPTERPEL);
            sincronizado = this.isAppTerpelPendiente ? 4 : 2;
        }
        NovusUtils.printLn("este es el numero de transmision recibido en el proceso actualizarInformacionCliente -> " + idTransmision);

        mdao.actualizarEstadoTransmision(sincronizado, idTransmision);
        mdao.actualizarEstadoMovimientosClientes(sincronizado, idTransmision);
        long idMovimiento = mdao.buscarMOvimientoId(idTransmision);
        mdao.actualizarClienteMovimiento(idMovimiento, idTransmision, sincronizado);
    }

    private boolean isVentaDatafono(JsonArray arrayMedios) {
        return NovusUtils.verificarMedioPago(arrayMedios, "CON DATAFONO");
    }

    public void mostarMensaje(String mensaje, Integer delay) {
        jMensaje.setText(mensaje);
        mostrarSubPanel(pnlMensaje);
        tareaProgramada = scheduler.schedule(this::cerrarMensaje, delay, TimeUnit.SECONDS);
        scheduler.shutdown();
    }

    public void cerrarMensaje() {
        NovusUtils.printLn("cerrarVentaMensaje");
        if (regresar != null) {
            this.asignacionClienteBean.getDatosCliente().clear();
            regresar.run();
            validarSincronizado(idTransmision);
            terminarTareaProgramada();
        }
    }

    public void terminarTareaProgramada() {
        if (tareaProgramada != null && !tareaProgramada.isDone()) {
            NovusUtils.printLn("Terminado Tarea");
            tareaProgramada.cancel(true);
        }
    }

    private void validarSincronizado(long idTransmision) {
        long idMovimiento = mdao.buscarMOvimientoId(idTransmision);
        if (!electronicaDao.hayTransaccionesPendientes(idMovimiento) && electronicaDao.ventasFeFinalizadas(idMovimiento)) {
            NovusUtils.printLn("********************** Cambiando el estado de la sincronizacion a estado 2 ************************************");
            mdao.actualizarEstadoTransmision(2, idTransmision);
            mdao.actualizarEstadoMovimientosClientes(2, idTransmision);
        }
    }

    public void cerrarVenta() {
        NovusUtils.printLn("cerrarVenta");
        if (regresar != null) {
            this.asignacionClienteBean.getDatosCliente().clear();
            regresar.run();
        }
    }

    private FoundClient validarInfoFidelizacion(JsonObject infoFidelizacion) {
        FoundClient client = new FoundClient();
        if (infoFidelizacion != null) {
            client = Main.gson.fromJson(infoFidelizacion, FoundClient.class);
        }
        return client;
    }

    private void fidelizar() {
        if (informacionVentaCliente.isFidelizar()) {
            foundClient = validarInfoFidelizacion(fidelizacionyFacturacoinElectronica.datosClienteFidelizacion);
            FidelizarVentaCliente.fidelizar(reciboRec,
                    foundClient.getDatosCliente().getCustomer().getNumeroIdentificacion(),
                    foundClient.getDatosCliente().getCustomer().getCodigoTipoIdentificacion());
        }
    }

}