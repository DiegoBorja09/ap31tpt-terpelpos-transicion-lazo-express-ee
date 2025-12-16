/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.firefuel.fidelizacionYfacturaElectronica;

import com.WT2.loyalty.domain.entities.beans.FoundClient;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.EquipoDao;
import com.facade.fidelizacion.ConsultarCliente;
import com.firefuel.FidelizacionyFacturacionElectronica;
import com.firefuel.Main;
import com.firefuel.ProcesosFEyFidelizacion;
import com.firefuel.facturacion.electronica.ConsultaClienteEnviarFE;
import com.firefuel.facturacion.electronica.TiposDocumentos;
import com.google.gson.JsonObject;
import com.utils.enums.TipoNegociosFidelizacion;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JPanel;

/**
 *
 * @author Devitech
 */
public class RenderizarProcesosFidelizacionyFE {

    public static final int DOCUMENTO_CEDULA = 13;
    public static final int DOCUMENTO_CLIENTES_VARIOS = 31;
    public static final int DOCUMENTO_TARJ_EXTRANJERIA = 21;
    public static final int DOCUMENTO_CEDULA_EXTRANJERIA = 22;
    public static final int DOCUMENTO_NIT = 31;
    public static final int DOCUMENTO_PASAPORTE = 41;
    public static final int DOCUMENTO_IDENTIFICACION_EXTRANJERO = 42;
    public static final int DOCUMENTO_NIT_DE_OTRO_PAIS = 50;
    public static final int DOCUMENTO_NUIP = 91;
    public static final int DOCUMENTO_REGISTRO_CIVIL = 11;
    public static final int DOCUMENTO_TARJETA_IDENTIFICACION = 12;
    private static final String NOMBRE_RAZON_SOCIAL = "nombreRazonSocial";
    private static final String NOMBRE_RAZON_SOCIAL_ERROR = "nombreRazonSocialError";
    private static final String NOMBRE_CLIENTE = "nombreCliente";
    private static final String ERROR = "error";
    private static final String CONTINGENCIA = "contingencia";
    private static final String MENSAJE = "mensaje";
    private static final String CLIENTE_FACTURA_FE = "clienteFacturacionElectronica";
    private static final String CLIENTE_FACTURA_FIDELIZACION = "clienteFidelizacion";
    private static final String CLIENTE_FACTURA_FIDELIZACION_RESPONSE = "responseCliente";
    JsonObject objClienteFidelizacion;

    public void listarProcesos(JPanel principal, FidelizacionyFacturacionElectronica procesoFidelizacionYFacturacion, boolean seleccionado, String tipoProceso) {
        ProcesosFEyFidelizacion procesosFEyFidelizacion = new ProcesosFEyFidelizacion();
        procesosFEyFidelizacion.panelProceso.setPreferredSize(new Dimension(410, 50));
        procesosFEyFidelizacion.lblOperacion.setText(tipoProceso);
        procesosFEyFidelizacion.panelProceso.setName(tipoProceso);
        principal.add(procesosFEyFidelizacion.panelProceso);
        principal.repaint();
        principal.revalidate();
    }

    public void removerProceso(JPanel principal, FidelizacionyFacturacionElectronica procesoFidelizacionYFacturacion, String tipoProceso) {
        for (Component component : principal.getComponents()) {
            if (component.getName().equals(tipoProceso)) {
                component.setVisible(false);
                principal.remove(component);
            }
        }
    }


    public FoundClient fidelizarVenta(String numeroIdentificacion, String tipoDeDocumento) {
        ConsultarCliente consultarCliente = new ConsultarCliente();
        FoundClient foundClient = consultarCliente.execute(tipoDeDocumento, numeroIdentificacion, TipoNegociosFidelizacion.COMBUSTIBLE.getTipoNegocio());
        return foundClient;
    }


    public void cambiarPanel(JPanel panel, String nombre) {
        CardLayout layout = (CardLayout) panel.getLayout();
        layout.show(panel, nombre);
    }

    public JsonObject consultarCliente(String numeroDocumento, String tipoIdentificaion, boolean soloFidelizacion) {
        ConsultaClienteEnviarFE consultaClienteEnviarFE = new ConsultaClienteEnviarFE();
        long tipoIdentificaionCliente = NovusUtils.tipoDeIndentificacion(tipoIdentificaion);
        JsonObject clietneFE = consultaClienteEnviarFE.consultarCliente(numeroDocumento, tipoIdentificaionCliente, null);
        clietneFE.addProperty("descripcionTipoDocumento", NovusUtils.tipoDocumento(tipoIdentificaionCliente));
        FoundClient clienteFedelizacion = fidelizarVenta(numeroDocumento, tiposIdentificaionFidelizaicon(tipoIdentificaionCliente));
        if (tiposIdentificaionFidelizaicon(tipoIdentificaionCliente).equals("")) {
            clienteFedelizacion.setMensaje("Tipo de documento no válido para fidelizar");
        }
        JsonObject data = new JsonObject();
        data.add(CLIENTE_FACTURA_FE, clietneFE);
        data.add(CLIENTE_FACTURA_FIDELIZACION, Main.gson.toJsonTree(clienteFedelizacion));
        validarCliente(data, soloFidelizacion);

        return data;
    }

    public String tiposIdentificaionFidelizaicon(long tipo) {
        String tipoIdentificaion;
        switch ((int) tipo) {
            case DOCUMENTO_CEDULA:
                tipoIdentificaion = "CC";
                break;
            case DOCUMENTO_CEDULA_EXTRANJERIA:
                tipoIdentificaion = "CE";
                break;
            case DOCUMENTO_PASAPORTE:
                tipoIdentificaion = "PAS";
                break;
            default:
                tipoIdentificaion = "";
        }

        return tipoIdentificaion;
    }

    public String validarCliente(JsonObject data, boolean soloFidelizacion) {
        JsonObject clienteFE = validarClienteFacturacionElctronica(data.get(CLIENTE_FACTURA_FE).getAsJsonObject());
        JsonObject clienteFidelizacion = data.get(CLIENTE_FACTURA_FIDELIZACION).getAsJsonObject();
        String cliente = "NO REGISTRADO";
        if (soloFidelizacion) {
            if (clienteFidelizacion.has(NOMBRE_CLIENTE) && clienteFidelizacion.get(NOMBRE_CLIENTE) != null && !clienteFidelizacion.get(NOMBRE_CLIENTE).getAsString().equals(cliente) && !clienteFidelizacion.get(NOMBRE_CLIENTE).getAsString().isEmpty() ) {
                FidelizacionyFacturacionElectronica.nombreClienteFidelizacion.setText(clienteFidelizacion.get(NOMBRE_CLIENTE).getAsString());
            } else {
                FidelizacionyFacturacionElectronica.nombreClienteFidelizacion.setText(cliente);
            }
        } else {
            cliente = validarClienteFidelizacionYFacturaElectronica(clienteFidelizacion, clienteFE);
        }

        return cliente;
    }

    public JsonObject validarClienteFE(String numeroDocumento, String tipoIdentificaion) {
        String cliente = NovusConstante.HAY_INTERNET ? "NO REGISTRADO" : "CLIENTE";
        ConsultaClienteEnviarFE consultaClienteEnviarFE = new ConsultaClienteEnviarFE();
        long tipoIdentificaionCliente = NovusUtils.tipoDeIndentificacion(tipoIdentificaion);
        JsonObject clienteFE = consultaClienteEnviarFE.consultarCliente(numeroDocumento, tipoIdentificaionCliente, null);
        if (clienteFE.has(NOMBRE_RAZON_SOCIAL) && clienteFE.get(NOMBRE_RAZON_SOCIAL) != null && !clienteFE.has(NOMBRE_RAZON_SOCIAL_ERROR)) {
            FidelizacionyFacturacionElectronica.nombreClienteFE.setText(clienteFE.get(NOMBRE_RAZON_SOCIAL).getAsString());
        } else {
            if (clienteFE.has(CONTINGENCIA) && clienteFE.get(CONTINGENCIA).getAsBoolean()) {
                cliente = "CLIENTE";
            }
            FidelizacionyFacturacionElectronica.nombreClienteFE.setText(cliente);
        }
        JsonObject data = new JsonObject();
        data.add(CLIENTE_FACTURA_FE, clienteFE);
        return data;
    }

     public void validarClienteFidelizacion(FoundClient clienteFidelizacion) {
        String cliente = NovusConstante.HAY_INTERNET ? "NO REGISTRADO" : "CLIENTE";
        if (!clienteFidelizacion.getNombreCliente().equals(cliente) && !clienteFidelizacion.getNombreCliente().equals("")) {
            FidelizacionyFacturacionElectronica.nombreClienteFidelizacion.setText(clienteFidelizacion.getNombreCliente());
            NovusUtils.printLn(clienteFidelizacion.getNombreCliente());
        } else {
            FidelizacionyFacturacionElectronica.nombreClienteFidelizacion.setText(cliente);
        } 
    } 
    
    public void validarClienteFidelizacion(JsonObject clienteFidelizacion) {
        String cliente = NovusConstante.HAY_INTERNET ? "NO REGISTRADO" : "CLIENTE";
        clienteFidelizacion = clienteFidelizacion.get(CLIENTE_FACTURA_FIDELIZACION_RESPONSE).getAsJsonObject();
        if (clienteFidelizacion.has(NOMBRE_CLIENTE) && clienteFidelizacion.get(NOMBRE_CLIENTE) != null && !clienteFidelizacion.get(NOMBRE_CLIENTE).getAsString().equals(cliente)) {
            FidelizacionyFacturacionElectronica.nombreClienteFidelizacion.setText(clienteFidelizacion.get(NOMBRE_CLIENTE).getAsString());
            NovusUtils.printLn(clienteFidelizacion.get(NOMBRE_CLIENTE).getAsString());
        } else {
            FidelizacionyFacturacionElectronica.nombreClienteFidelizacion.setText(cliente);
        }
    }

    private String validarClienteFidelizacionYFacturaElectronica(JsonObject clienteFidelizacion, JsonObject clienteFE) {
        String cliente = NovusConstante.HAY_INTERNET ? "NO REGISTRADO" : "CLIENTE";

        if (clienteFE.has(NOMBRE_RAZON_SOCIAL) && clienteFE.get(NOMBRE_RAZON_SOCIAL) != null && !clienteFE.has(NOMBRE_RAZON_SOCIAL_ERROR)) {
            FidelizacionyFacturacionElectronica.nombreClienteFE.setText(clienteFE.get(NOMBRE_RAZON_SOCIAL).getAsString());
        } else {
            if (clienteFE.has(CONTINGENCIA) && clienteFE.get(CONTINGENCIA).getAsBoolean()) {
                cliente = "CLIENTE";
            }
            FidelizacionyFacturacionElectronica.nombreClienteFE.setText(cliente);
        }
        NovusUtils.printLn(clienteFidelizacion + "");
        if (clienteFidelizacion.has(NOMBRE_CLIENTE) && clienteFidelizacion.get(NOMBRE_CLIENTE) != null && !clienteFidelizacion.get(NOMBRE_CLIENTE).getAsString().equals(cliente) && !clienteFidelizacion.get(NOMBRE_CLIENTE).getAsString().isEmpty()) {
            FidelizacionyFacturacionElectronica.nombreClienteFidelizacion.setText(clienteFidelizacion.get(NOMBRE_CLIENTE).getAsString());
            NovusUtils.printLn(clienteFidelizacion.get(NOMBRE_CLIENTE).getAsString());
        } else {
            FidelizacionyFacturacionElectronica.nombreClienteFidelizacion.setText(cliente);
        }
        return cliente;
    }

    private JsonObject validarClienteFacturacionElctronica(JsonObject cliente) {
        if (!cliente.has(ERROR) && !cliente.has(CONTINGENCIA)) {
            return cliente;

        } else {
            if (cliente.has(ERROR) && cliente.get(ERROR).getAsBoolean()) {
                JsonObject datosCliente = new JsonObject();
                datosCliente.addProperty(MENSAJE, cliente.get(MENSAJE).getAsString());
                datosCliente.addProperty(ERROR, true);
                datosCliente.addProperty(NOMBRE_RAZON_SOCIAL_ERROR, cliente.get(MENSAJE).getAsString());
                return datosCliente;
            }
            if (cliente.has(CONTINGENCIA)) {
                return cliente;
            }

        }
        return cliente;
    }


}
