/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.firefuel.facturacion.electronica;

import com.application.useCases.ventas.BuscarVentasClienteUseCase;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.MovimientosDao;
import com.firefuel.Main;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.TimerTask;

/**
 *
 * @author Devitech
 */
public class AsignacionclienteRemision extends TimerTask {

    JsonObject datosClientes = new JsonObject();
    MovimientosDao dao = new MovimientosDao();   
    int dias = 3;

    @Override
    public void run() {

        if (NovusConstante.HAY_INTERNET) {
            System.gc();
            int diastemp = Main.getParametroIntCore("DIAS_REENVIO_FE", true);
            if (diastemp >= 1) {
                dias = diastemp;
            }
            //JsonArray asignarCliente = dao.buscarVentaCliente(dias);
            JsonArray asignarCliente = new BuscarVentasClienteUseCase().execute();
            if (asignarCliente.size() > 0) {
                NovusUtils.printLn("iniciando la asignacion de clientes");
                NovusUtils.printLn("******************* iniciando asignaciones ************************");
                for (JsonElement element : asignarCliente) {
                    datosClientes = element.getAsJsonObject();
                    asignarCliente(datosClientes);
                }
                NovusUtils.printLn("************************ asignaciones terminadas *************************");
            }
        }
    }

    private JsonObject consultaCliente(String numeroDocumento, int identificador) {
        String funcion = "CONSUILTA DE CLIENTE FACTURACION";
        String url = NovusConstante.getServer(NovusConstante.SOURCE_END_POINT_CONSULTA_CLIENTE);
        String method = "POST";
        JsonObject json = new JsonObject();
        boolean debug = true;
        json.addProperty("documentoCliente", numeroDocumento);
        json.addProperty("tipoDocumentoCliente", identificador);

        ClientWSAsync client = new ClientWSAsync(funcion, url, method, json, debug);
        JsonObject clientes = new JsonObject();
        client.esperaRespuesta();
        if (client.getResponse() != null) {
            clientes = client.getResponse();
            clientes = NovusUtils.remplazarCaracteresEspecialesClientesFe(clientes);
            clientes.addProperty("error", false);
            clientes.addProperty("errorServicio", false);
        } else {
            if (client.getError() != null) {
                clientes.addProperty("error", true);
                clientes.addProperty("errorServicio", false);
            } else {
                clientes.addProperty("errorServicio", true);
            }
        }
        return clientes;
    }


    private void asignarCliente(JsonObject json) {
        try {
            String documento = json.get("cliente").getAsJsonObject().has("documentoCliente") ? json.get("cliente").getAsJsonObject().get("documentoCliente").getAsString() : String.valueOf(json.get("cliente").getAsJsonObject().get("numeroDocumento").getAsLong());
            int tipoDocumento = json.get("cliente").getAsJsonObject().has("identificacion_cliente") ? json.get("cliente").getAsJsonObject().get("identificacion_cliente").getAsInt() : json.get("cliente").getAsJsonObject().get("tipoDocumento").getAsInt();
            JsonObject cliente = consultaCliente(documento, tipoDocumento);
            if (cliente.get("error").getAsBoolean()) {
                cliente = consultaCliente("222222222222", 31);
            }
            if (cliente.get("nombreComercial").isJsonNull() && cliente.get("nombreRazonSocial").isJsonNull()) {
                cliente = consultaCliente("222222222222", 31);
            }
            json.remove("cliente");
            String nombre = !cliente.get("nombreComercial").isJsonNull() ? cliente.get("nombreComercial").getAsString() : cliente.get("nombreRazonSocial").getAsString();
            json.addProperty("personas_nombre", nombre);
            json.addProperty("personas_identificacion", cliente.get("numeroDocumento").getAsLong());
            cliente.addProperty("descripcionTipoDocumento", NovusUtils.tipoDocumento(tipoDocumento));
            json.add("cliente", cliente);
            long id = json.get("id_transmision").getAsLong();
            json.remove("id_transmision");
            boolean actulizarClietne = (json.get("sincronizado").getAsInt() != 4 && json.get("actuluziar_venta_combustible").getAsBoolean());
            dao.actulizarDatosCliente(id, json, actulizarClietne);
        } catch (Exception e) {
            NovusUtils.printLn(Main.ANSI_RED + "hay un error en el proceso asignarCliente_" + e + " - " + e.getMessage() + " - " + e.getClass().getName() + Main.ANSI_RESET);
        }
    }

}
