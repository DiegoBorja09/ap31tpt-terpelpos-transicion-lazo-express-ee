/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.firefuel.facturacion.electronica;

import com.application.useCases.movimientos.ConsultarClienteMovimientoTransmisionUseCase;
import com.application.useCases.ventas.ActualizarAtributosTransmisionUseCase;
import com.application.useCases.ventas.BuscarVentasClienteUseCase;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.MovimientosDao;
import com.facade.PedidoFacade;
import com.firefuel.Main;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.TimerTask;

/**
 *
 * @author Devitech
 */
public class ReenviodeFE extends TimerTask {

    JsonObject dataFE = new JsonObject();
    JsonObject datosClientes = new JsonObject();
    MovimientosDao dao = new MovimientosDao();
    PedidoFacade pedido = new PedidoFacade();
    long segundos = 1000;
    long minutos = segundos * 60;
    int dias = 3;

    @Override
    public void run() {

        if (NovusConstante.HAY_INTERNET) {
            System.gc();
            
            NovusUtils.printLn("🚀 RenvioFE: Iniciando ciclo - Buscando fallbacks de ControlImpresion y rescatando orfanas (>10s sin procesar)...");
            JsonArray arr = dao.buscarTransminionFE(2, "limit 20");

            if (arr.size() > 0) {
                NovusUtils.printLn("🔄 RenvioFE: " + arr.size() + " transmisiones encontradas para fallback");
                NovusUtils.printLn("******************* REENVÍO FALLBACK FE - INICIANDO ************************");
                for (JsonElement element : arr) {
                    dataFE = element.getAsJsonObject();
                    enviarFE(dataFE);
                }
                NovusUtils.printLn("******************* REENVÍO FALLBACK FE - COMPLETADO ************************");
            } else {
                NovusUtils.printLn("✅ RenvioFE: No hay transmisiones pendientes (ni fallbacks ni orfanas) - Sistema funcionando correctamente");
            }
            int diastemp = Main.getParametroIntCore("DIAS_REENVIO_FE", true);
            if (diastemp >= 1) {
                dias = diastemp;
            }
            //JsonArray asignarCliente = dao.buscarVentaCliente(dias);
            JsonArray asignarCliente = new BuscarVentasClienteUseCase().execute();

            if (asignarCliente.size() > 0) {
                NovusUtils.printLn("iniciando la asignacion de clientes");
                NovusUtils.printLn("******************* asignando clientes ************************");
                for (JsonElement element : asignarCliente) {
                    datosClientes = element.getAsJsonObject();
                    asignarCliente(datosClientes);
                }
                NovusUtils.printLn("************************ asignaciones terminadas *************************");
            }
        }
    }

    private JsonObject consultaCliente(String numeroDocumento, int identificador, long idTransmision) {

        NovusUtils.printLn("************************ - *************************");
        NovusUtils.printLn("Inicio Consulta al Cliente");
        NovusUtils.printLn("************************ - *************************");

        JsonObject clientes;

        if (idTransmision == 0) {
            clientes = envioConsultaCliente(numeroDocumento, identificador);
        } else {
            boolean consultar = dao.consultaClienteReintentos(idTransmision);
            if (consultar) {
                clientes = envioConsultaCliente(numeroDocumento, identificador);
                validarRespuestaConsulta(clientes, idTransmision);
            } else {
                clientes = Main.gson.fromJson(ClienteConsumidorFinal.cliente, JsonObject.class);
                clientes.addProperty("error", false);
                clientes.addProperty("errorServicio", false);
            }
        }
        return clientes;
    }

    public JsonObject validarRespuestaConsulta(JsonObject cliente, long IdTransmision) {
        JsonObject respuesta = new JsonObject();
        if (cliente.has("error") || cliente.get("errorServicio").getAsBoolean()) {
            dao.actualizarReintentosConsultaCliente(IdTransmision);
        } else {
            respuesta = cliente;
        }
        return respuesta;
    }

    public JsonObject envioConsultaCliente(String numeroDocumento, int identificador) {
        NovusUtils.printLn("************************ - *************************");
        NovusUtils.printLn("Consultando Cliente");
        NovusUtils.printLn("El Numero de documento es -> " + numeroDocumento);
        NovusUtils.printLn("Tipo de documento es -> " + identificador);
        NovusUtils.printLn("************************ - *************************");
        NovusUtils.printLn("************************ - *************************");

        String funcion = "CONSULTA DE CLIENTE FACTURACION";
        String url = NovusConstante.getServer(NovusConstante.SOURCE_END_POINT_CONSULTA_CLIENTE);
        String method = "POST";
        JsonObject json = new JsonObject();
        boolean debug = false;
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
                clientes.addProperty("errorServicio", true);
            } else {
                clientes.addProperty("errorServicio", true);
            }
        }
        NovusUtils.printLn("............................. Respuesta de la consulta de cliente .......................................................");
        NovusUtils.printLn(clientes.toString());
        NovusUtils.printLn("............................. Respuesta de la consulta de cliente .......................................................");
        return clientes;
    }

    private void enviarFE(JsonObject json) {
        long idTransmision = 0L;
        try {
            idTransmision = json.get("id_transmision").getAsLong();
            int reintentosActuales = json.has("reintentos_actuales") ? json.get("reintentos_actuales").getAsInt() : 1;
            
            // 🚀 COORDINACIÓN: Diferenciar entre fallback y rescate orfanas
            if (reintentosActuales == 0) {
                NovusUtils.printLn("🆘 RenvioFE: RESCATANDO transmisión orfana " + idTransmision + " (status=NULL, reintentos=0)");
            } else {
                NovusUtils.printLn("🔄 RenvioFE: Procesando transmisión " + idTransmision + " como fallback (reintentos=" + reintentosActuales + ")");
            }
            
            // 🚀 COORDINACIÓN: Incrementar reintentos para marcar que RenvioFE está procesando
            dao.incrementarReintentos(idTransmision);
            NovusUtils.printLn("📊 RenvioFE: Reintentos incrementados: " + reintentosActuales + " → " + (reintentosActuales + 1));
            
            // 🚀 PASO 1: Extraer datos del cliente usando utilidades (eliminando duplicación)
            com.utils.FacturacionElectronicaUtils.DatosCliente datosCliente = 
                com.utils.FacturacionElectronicaUtils.extraerDatosParaConsulta(json);
            
            JsonObject cliente = consultaCliente(datosCliente.documento, datosCliente.tipoDocumento, idTransmision);
            if (cliente.has("errorServicio") && !cliente.get("errorServicio").getAsBoolean()) {
                // 🚀 PASO 2: Procesar request usando utilidades centralizadas (eliminando 10+ líneas duplicadas)
                json = com.utils.FacturacionElectronicaUtils.procesarRequestFE(json, cliente);
                dao.actualizarRequestTransmision(idTransmision, json);
                String funcion = "ENVIAR FACTURA ELECTRONICA [RenvioFE-Fallback]";
                String url = NovusConstante.getServer(NovusConstante.SOURCE_END_POINT_FACTURA_ELECTRONICA);
                String method = "POST";
                ClientWSAsync client = new ClientWSAsync(funcion, url, method, json, false);
                if (client.esperaRespuesta() != null) {
                    JsonObject response = client.getResponse();
                    NovusUtils.printLn("✅ RenvioFE: Respuesta exitosa para transmisión " + idTransmision);
                    NovusUtils.printLn(response + "");
                    dao.actualizarTransmision(idTransmision, 200, response);
                    //dao.actualizarTransmisionAtributosVenta(idTransmision, json);
                    new ActualizarAtributosTransmisionUseCase(idTransmision, json).execute();
                    NovusUtils.printLn("🎯 RenvioFE: Transmisión " + idTransmision + " completada exitosamente como fallback");
                } else {
                    NovusUtils.printLn("❌ RenvioFE: Error en respuesta para transmisión " + idTransmision);
                    dao.actualizarTransmision(idTransmision, 409, client.getError());
                }
            } else {
                NovusUtils.printLn("❌ RenvioFE: Error consultando cliente para transmisión " + idTransmision);
            }
        } catch (Exception e) {
            JsonObject error = new JsonObject();
            error.addProperty("mensajeError", "hubo un error inesperado durante el envio de la facturacion electronica en RenvioFE");
            dao.actualizarTransmision(idTransmision, 400, error);
            NovusUtils.printLn("💥 RenvioFE: Error inesperado al reenviar transmisión " + idTransmision + " -> " + e.getMessage());
        }
    }

        private void asignarCliente(JsonObject json) {
        try {

            JsonObject cliente;

            // 🔧 EXTRACCIÓN ORIGINAL (funciona correctamente para movimientos cliente)
            String documento = json.get("cliente").getAsJsonObject().has("documentoCliente") ? 
                json.get("cliente").getAsJsonObject().get("documentoCliente").getAsString() : 
                String.valueOf(json.get("cliente").getAsJsonObject().get("numeroDocumento").getAsLong());
            int tipoDocumento = json.get("cliente").getAsJsonObject().has("identificacion_cliente") ? 
                json.get("cliente").getAsJsonObject().get("identificacion_cliente").getAsInt() : 
                json.get("cliente").getAsJsonObject().get("tipoDocumento").getAsInt();

            //JsonObject clienteTransmision = dao.consultaClienteMovimientoTransmision(json.get("id_transmision").getAsLong());
            JsonObject clienteTransmision = new ConsultarClienteMovimientoTransmisionUseCase()
            .execute(json.get("id_transmision").getAsLong());

            if (clienteTransmision.has("consultarCliente") || clienteTransmision.has("mensajeError")) {
                cliente = consultaCliente(documento, tipoDocumento, 0);
            } else {
                cliente = clienteTransmision;
            }

            // 🚨 VALIDACIÓN CRÍTICA: Verificar si el cliente es válido antes de procesarlo
            if (cliente.has("error") && cliente.get("error").getAsBoolean()) {
                System.out.println("❌ [asignarCliente] Cliente con error, usando consumidor final");
                cliente = envioConsultaCliente("222222222222", 31);
            }
            
            // 🚨 VALIDACIÓN ADICIONAL: Verificar que el cliente tenga campos mínimos requeridos
            if (!cliente.has("nombreComercial") || !cliente.has("nombreRazonSocial") || !cliente.has("numeroDocumento")) {
                System.out.println("❌ [asignarCliente] Cliente incompleto, usando consumidor final");
                cliente = envioConsultaCliente("222222222222", 31);
            } else if (cliente.get("nombreComercial").isJsonNull() && cliente.get("nombreRazonSocial").isJsonNull()) {
                System.out.println("❌ [asignarCliente] Cliente sin nombre, usando consumidor final");
                cliente = envioConsultaCliente("222222222222", 31);
            }
            
            if (cliente.has("tipoDocumento")) {
                tipoDocumento = cliente.get("tipoDocumento").getAsInt();
            }
            NovusUtils.printLn("******************************************");
            NovusUtils.printLn("ID transmision: " + json.get("id_transmision").getAsLong());
            NovusUtils.printLn("Tipo Documento :" + tipoDocumento);
            NovusUtils.printLn("******************************************");
            
            // 🔧 VERIFICACIÓN ORIGINAL de remisionActiva
            com.application.useCases.wacherparametros.FindByParameterUseCase findByParameterUseCase = 
                new com.application.useCases.wacherparametros.FindByParameterUseCase(com.application.commons.CtWacherParametrosEnum.CODIGO.getColumnName(), "REMISION");
            boolean remisionActiva = findByParameterUseCase.execute();
            NovusUtils.printLn("remisionActiva() " + remisionActiva);

            // 🚨 VALIDACIÓN FINAL antes de procesar datos del cliente
            if (!cliente.has("numeroDocumento") || !cliente.has("nombreComercial") || !cliente.has("nombreRazonSocial")) {
                System.out.println("❌ [asignarCliente] ABORTANDO: Cliente no tiene campos mínimos requeridos");
                return; // Salir sin procesar
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
            NovusUtils.printLn("datos del cliente actualizados");
        } catch (Exception e) {
            NovusUtils.printLn(Main.ANSI_RED + "hay un error en el proceso " + e + " - " + e.getMessage() + " - " + e.getClass().getName() + Main.ANSI_RESET);
        }
    }

}
