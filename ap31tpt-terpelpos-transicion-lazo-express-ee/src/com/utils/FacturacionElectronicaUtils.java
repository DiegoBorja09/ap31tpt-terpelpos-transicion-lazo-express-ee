package com.utils;

import com.google.gson.JsonObject;
import com.controllers.NovusUtils;

/**
 *  UTILIDADES COMUNES - FACTURACIÓN ELECTRÓNICA
 * 
 * Esta clase centraliza la lógica duplicada entre:
 * - RenvioFE
 * - ControlImpresion 
 * - AsignacionclienteRemision
 * - ConsultaClienteEnviarFE
 * 
 * Creada para eliminar duplicación de código y facilitar mantenimiento.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0
 */
public class FacturacionElectronicaUtils {
    
    /**
     *  EXTRACTOR DE DOCUMENTO - Manejo de campos opcionales
     * 
     * Extrae el documento del cliente desde JSON, manejando los diferentes
     * formatos que puede tener (documentoCliente vs numeroDocumento).
     * 
     * @param clienteJson JsonObject que contiene datos del cliente
     * @return String con el documento del cliente
     */
    public static String extraerDocumentoCliente(JsonObject clienteJson) {
        if (clienteJson == null) {
            throw new IllegalArgumentException(" Cliente JSON no puede ser null");
        }
        
        // Prioridad: documentoCliente -> numeroDocumento
        if (clienteJson.has("documentoCliente") && !clienteJson.get("documentoCliente").isJsonNull()) {
            return clienteJson.get("documentoCliente").getAsString();
        }
        
        if (clienteJson.has("numeroDocumento") && !clienteJson.get("numeroDocumento").isJsonNull()) {
            return String.valueOf(clienteJson.get("numeroDocumento").getAsLong());
        }
        
        throw new IllegalStateException(" No se encontró documento del cliente en JSON: " + clienteJson);
    }
    
    /**
     *  EXTRACTOR DE TIPO DOCUMENTO - Manejo de campos opcionales
     * 
     * Extrae el tipo de documento desde JSON, manejando los diferentes
     * formatos que puede tener.
     * 
     * @param clienteJson JsonObject que contiene datos del cliente
     * @return int con el tipo de documento
     */
    public static int extraerTipoDocumento(JsonObject clienteJson) {
        if (clienteJson == null) {
            throw new IllegalArgumentException(" Cliente JSON no puede ser null");
        }
        
        // Prioridad: identificacion_cliente -> tipoDocumento
        if (clienteJson.has("identificacion_cliente") && !clienteJson.get("identificacion_cliente").isJsonNull()) {
            return clienteJson.get("identificacion_cliente").getAsInt();
        }
        
        if (clienteJson.has("tipoDocumento") && !clienteJson.get("tipoDocumento").isJsonNull()) {
            return clienteJson.get("tipoDocumento").getAsInt();
        }
        
        throw new IllegalStateException(" No se encontró tipo documento del cliente en JSON: " + clienteJson);
    }
    
    /**
     *  ACTUALIZADOR DE TERCERO - Lógica centralizada
     * 
     * Actualiza los datos del tercero en el JSON de venta con información del cliente.
     * Esta lógica estaba duplicada en múltiples clases.
     * 
     * @param ventaJson JsonObject de la venta a actualizar
     * @param clienteJson JsonObject con datos del cliente consultado
     */
    public static void actualizarDatosTercero(JsonObject ventaJson, JsonObject clienteJson) {
        if (ventaJson == null || clienteJson == null) {
            throw new IllegalArgumentException(" Venta y Cliente JSON no pueden ser null");
        }
        
        //  TERCERO NIT - Manejo robusto de diferentes formatos
        long numeroDocumento;
        if (clienteJson.has("numeroDocumento") && !clienteJson.get("numeroDocumento").isJsonNull()) {
            numeroDocumento = clienteJson.get("numeroDocumento").getAsLong();
        } else if (clienteJson.has("documentoCliente") && !clienteJson.get("documentoCliente").isJsonNull()) {
            numeroDocumento = Long.parseLong(clienteJson.get("documentoCliente").getAsString());
        } else {
            throw new IllegalStateException(" No se encontró número de documento válido");
        }
        
        ventaJson.addProperty("tercero_nit", numeroDocumento);
        
        //  TERCERO NOMBRE - Manejo de nulls
        String nombreComercial = "";
        if (clienteJson.has("nombreComercial") && !clienteJson.get("nombreComercial").isJsonNull()) {
            nombreComercial = clienteJson.get("nombreComercial").getAsString();
        }
        ventaJson.addProperty("tercero_nombre", nombreComercial);
        
        //  TERCERO CORREO - Manejo de nulls  
        String correoElectronico = "";
        if (clienteJson.has("correoElectronico") && !clienteJson.get("correoElectronico").isJsonNull()) {
            correoElectronico = clienteJson.get("correoElectronico").getAsString();
        }
        ventaJson.addProperty("tercero_correo", correoElectronico);
        
        //  TERCERO TIPO PERSONA
        if (clienteJson.has("identificadorTipoPersona") && !clienteJson.get("identificadorTipoPersona").isJsonNull()) {
            ventaJson.addProperty("tercero_tipo_persona", clienteJson.get("identificadorTipoPersona").getAsLong());
        }
        
        //  TERCERO TIPO DOCUMENTO
        if (clienteJson.has("tipoDocumento") && !clienteJson.get("tipoDocumento").isJsonNull()) {
            ventaJson.addProperty("tercero_tipo_documento", clienteJson.get("tipoDocumento").getAsLong());
        }
        
        //  TERCERO RESPONSABILIDAD FISCAL
        if (clienteJson.has("regimenFiscal") && !clienteJson.get("regimenFiscal").isJsonNull()) {
            ventaJson.addProperty("tercero_responsabilidad_fiscal", clienteJson.get("regimenFiscal").getAsLong());
        }
        
        //  TERCERO CÓDIGO SAP
        String codigoSAP = "";
        if (clienteJson.has("codigoSAP") && !clienteJson.get("codigoSAP").isJsonNull()) {
            codigoSAP = clienteJson.get("codigoSAP").getAsString();
        }
        ventaJson.addProperty("tercero_codigo_sap", codigoSAP);
    }
    
    /**
     *  PROCESADOR COMPLETO DE REQUEST - One-stop solution
     * 
     * Procesa completamente un request de FE, extrayendo datos del cliente,
     * actualizando el tercero y preparando el JSON final.
     * 
     * @param requestFE JsonObject del request original
     * @param clienteConsultado JsonObject del cliente ya consultado
     * @return JsonObject procesado y listo para envío
     */
    public static JsonObject procesarRequestFE(JsonObject requestFE, JsonObject clienteConsultado) {
        if (requestFE == null || clienteConsultado == null) {
            throw new IllegalArgumentException(" Request y Cliente no pueden ser null");
        }
        
        //  PASO 1: Obtener tipo documento para descripción
        int tipoDocumento = extraerTipoDocumento(requestFE.get("cliente").getAsJsonObject());
        
        //  PASO 2: Actualizar datos del tercero en venta
        actualizarDatosTercero(requestFE.get("venta").getAsJsonObject(), clienteConsultado);
        
        //  PASO 3: Reemplazar cliente con datos consultados
        requestFE.remove("cliente");
        
        //  PASO 4: Agregar descripción de tipo documento
        String descripcionTipoDoc = NovusUtils.tipoDocumento(tipoDocumento);
        clienteConsultado.addProperty("descripcionTipoDocumento", descripcionTipoDoc);
        
        //  PASO 5: Agregar cliente actualizado
        requestFE.add("cliente", clienteConsultado);
        
        return requestFE;
    }
    
    /**
     *  VALIDADOR DE REQUEST FE - Verificaciones de integridad
     * 
     * Valida que un request de FE tenga la estructura mínima requerida.
     * 
     * @param requestFE JsonObject a validar
     * @return boolean true si es válido
     * @throws IllegalStateException si falta información crítica
     */
    public static boolean validarRequestFE(JsonObject requestFE) {
        if (requestFE == null || requestFE.entrySet().isEmpty()) {
            throw new IllegalStateException(" Request FE vacío o null");
        }
        
        // Validar estructura básica
        if (!requestFE.has("cliente")) {
            throw new IllegalStateException(" Request FE sin datos de cliente");
        }
        
        if (!requestFE.has("venta")) {
            throw new IllegalStateException(" Request FE sin datos de venta");
        }
        
        // Validar cliente
        JsonObject cliente = requestFE.get("cliente").getAsJsonObject();
        try {
            extraerDocumentoCliente(cliente);
            extraerTipoDocumento(cliente);
        } catch (Exception e) {
            throw new IllegalStateException(" Datos de cliente inválidos: " + e.getMessage());
        }
        
        return true;
    }
    
    /**
     *  EXTRACTOR DE DATOS CLIENTE - Para consulta externa
     * 
     * Extrae documento y tipo de documento de un request FE para 
     * facilitar la consulta externa del cliente.
     * 
     * @param requestFE JsonObject del request
     * @return DatosCliente con documento y tipo
     */
    public static DatosCliente extraerDatosParaConsulta(JsonObject requestFE) {
        validarRequestFE(requestFE);
        
        JsonObject clienteJson = requestFE.get("cliente").getAsJsonObject();
        String documento = extraerDocumentoCliente(clienteJson);
        int tipoDocumento = extraerTipoDocumento(clienteJson);
        
        return new DatosCliente(documento, tipoDocumento);
    }
    
    /**
     *  CLASE HELPER - Datos del cliente para consulta
     */
    public static class DatosCliente {
        public final String documento;
        public final int tipoDocumento;
        
        public DatosCliente(String documento, int tipoDocumento) {
            this.documento = documento;
            this.tipoDocumento = tipoDocumento;
        }
        
        @Override
        public String toString() {
            return String.format("Cliente{doc='%s', tipo=%d}", documento, tipoDocumento);
        }
    }
} 