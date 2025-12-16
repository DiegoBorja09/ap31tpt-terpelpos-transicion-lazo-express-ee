/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.facade;

import com.WT2.FacturacionElectronica.domain.entities.DetallesVenta;
import com.WT2.FacturacionElectronica.domain.entities.InformacionVentaClienteFE;
import com.WT2.FacturacionElectronica.domain.entities.MediosPagoVenta;
import com.WT2.loyalty.domain.entities.beans.DatosVenta;
import com.WT2.loyalty.domain.entities.beans.IdentificacionClienteRedencion;
import com.WT2.loyalty.domain.entities.beans.MediosPagoRedencion;
import com.WT2.loyalty.domain.entities.beans.Productos;
import com.application.commons.CtMovimientoDetallesEnum;
import com.application.useCases.consecutivos.ObtenerConsecutivoDesdeMovimientoUseCase;
import com.application.useCases.movimientosdetalles.FindByParamerMovementDetailDUseCase;
import com.bean.*;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import static com.controllers.NovusUtils.encriptacionBase64AES256;
import com.dao.MovimientosDao;
import com.dao.SurtidorDao;
import com.firefuel.Main;
import com.google.gson.*;
import com.neo.app.bean.MediosPagosBean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author devitech
 */
public class FidelizacionFacade {

    public static SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
    public static SimpleDateFormat sdfISO = new SimpleDateFormat(NovusConstante.FORMAT_DATE_ISO);

    public static JsonObject fetchSearchClient(String identifier, String identifierTypeCode, String salePointCode) {
        JsonObject response = null;
        TreeMap<String, String> header = new TreeMap<>();
        header.put("Authorization", "Basic cGFzc3BvcnR4OlQ0MUFYUWtYSjh6");
        header.put("Content-Type", "application/json");
        header.put("Accept", "*/*");
        header.put("dispositivo", "proyectos");
        header.put("fecha", sdfISO.format(new Date()) + "-05");
        header.put("aplicacion", "lazoexpress");
        ClientWSAsync client = new ClientWSAsync("VALIDACION DE CLIENTE VIVE TERPEL",
                NovusConstante.SECURE_CENTRAL_POINT_CONSULTA_CLIENTE, NovusConstante.PUT,
                buildSearchClientRequestObject(identifier, identifierTypeCode, salePointCode), true, false, header);
        try {
            response = client.esperaRespuesta();
            if (response != null) {
                response.addProperty("statusCode", client.getStatus());
            }
            return response;
        } catch (Exception ex) {
            NovusUtils.printLn(ex.getMessage());
            return null;
        }
    }

    public static JsonObject buildSearchClientRequestObject(String identifier, String identifierTypeCode, String salePointCode) {
        JsonObject request = new JsonObject();

        request.addProperty("origenVenta", "EDS");
        request.addProperty("identificacionPuntoVenta", salePointCode);
        request.addProperty("fechaTransaccion", sdf.format(new Date()));
        JsonObject jsonClient = new JsonObject();
        jsonClient.addProperty("codigoTipoIdentificacion", identifierTypeCode);
        jsonClient.addProperty("numeroIdentificacion", identifier);
        request.add("identificacionCliente", jsonClient);
        return request;
    }

//    public static JsonObject fetchClientAcumulation(ReciboExtended saleFacture, String identifier, String identifierTypeCode, String salePointCode) {
//        JsonObject response = null;
//        TreeMap<String, String> header = new TreeMap<>();
//        header.put("Authorization", "Basic cGFzc3BvcnR4OlQ0MUFYUWtYSjh6");
//        header.put("Content-Type", "application/json");
//        header.put("Accept", "*/*");
//        header.put("dispositivo", "proyectos");
//        header.put("fecha", sdfISO.format(new Date()) + "-05");
//        header.put("aplicacion", "lazoexpress");
//        ClientWSAsync clientWS = new ClientWSAsync("ACUMULACION CLIENTE",
//                NovusConstante.SECURE_CENTRAL_POINT_ACUMULACION_CLIENTE, NovusConstante.POST,
//                buildClientAcumulationRequestObject(saleFacture, identifier, identifierTypeCode, salePointCode), true,
//                false, header);
//        try {
//            response = clientWS.esperaRespuesta();
//            return response;
//        } catch (Exception ex) {
//            NovusUtils.printLn(ex.getMessage());
//      return null;
//    }
    public static JsonObject fetchSearchVoucher(ConsultVoucher consult) {
        JsonObject response = null;
        TreeMap<String, String> header = header = new TreeMap<>();
        header.put("Authorization", "Basic cGFzc3BvcnR4OlQ0MUFYUWtYSjh6");
        header.put("Content-Type", "application/json");
        header.put("Accept", "*/*");
        header.put("dispositivo", "proyectos");
        header.put("fecha", sdfISO.format(new Date()) + "-05");
        header.put("aplicacion", "lazoexpress");

        JsonObject bodyVoucher = new JsonObject();
        bodyVoucher.addProperty("origenVenta", consult.getOrigenVenta());
        bodyVoucher.addProperty("identificacionPuntoVenta", consult.getIdentificacionPuntoVenta());
        bodyVoucher.addProperty("codigoBono", consult.getCodigoBono());
        bodyVoucher.addProperty("valorBono", consult.getValorBono());

        String url = NovusConstante.SECURE_CENTRAL_POINT_CONSULTA_VOUCHER;
        NovusUtils.printLn(url);

        ClientWSAsync clientWS = new ClientWSAsync("VALIDACION BONO", url, NovusConstante.POST, bodyVoucher, true, false, header);
        try {
            response = clientWS.esperaRespuesta();
            if (response != null) {
                response.addProperty("statusCode", clientWS.getStatus());
            }
            return response;
        } catch (Exception ex) {
            NovusUtils.printLn(ex.getMessage());
            return null;
        }
    }

    //BODY ACUMULAR CLIENTE FIDELIZACION
    // public static Map buildClientAcumulationRequestObject(ReciboExtended saleFacture, String identifier,
    //                                                       String identifierTypeCode, String salePointCode) {

    //     String salePrefix = (saleFacture.getAtributos() != null && saleFacture.getAtributos().get("consecutivo") != null
    //             && !saleFacture.getAtributos().get("consecutivo").isJsonNull())
    //             && !saleFacture.getAtributos().get("consecutivo").isJsonPrimitive()
    //             ? saleFacture.getAtributos().get("consecutivo").getAsJsonObject().get("prefijo").getAsString() + "-"
    //             + saleFacture.getAtributos().get("consecutivo").getAsJsonObject().get("consecutivo_actual").getAsString()
    //             : "0";

    //     String saleNumber = String.valueOf(saleFacture.getNumero());
    //     Map params = new TreeMap();
    //     params.put("fechaTransaccion", sdf.format(new Date()));
    //     params.put("identificacionPuntoVenta", salePointCode);
    //     params.put("origenVenta", "EDS");
    //     params.put("tipoVenta", "L");

    //     // >>> Lógica robusta para obtener identificacionPromotor
    //     String identificacionPromotor = saleFacture.getIdentificacionPromotor();
    //     if ((identificacionPromotor == null || identificacionPromotor.trim().isEmpty())
    //             && saleFacture.getAtributos() != null
    //             && saleFacture.getAtributos().has("responsables_identificacion")
    //             && !saleFacture.getAtributos().get("responsables_identificacion").isJsonNull()) {
    //         identificacionPromotor = saleFacture.getAtributos().get("responsables_identificacion").getAsString();
    //         System.out.println(">>> [LOG] Asignando identificacionPromotor desde atributos.responsables_identificacion: " + identificacionPromotor);
    //     }
    //     params.put("identificacionPromotor", identificacionPromotor);

    //     // >>> Logs extra útiles para debug
    //     System.out.println(">>> [LOG] identificacionPromotor desde ReciboExtended: " + saleFacture.getIdentificacionPromotor());
    //     System.out.println(">>> [LOG] Atributos completos: " + saleFacture.getAtributos());

    //     params.put("identificacionVenta", salePrefix + "-" + saleNumber);
    //     params.put("valorTotalVenta", saleFacture.getTotal());
    //     params.put("totalImpuesto", 0);
    //     params.put("descuentoVenta", 0);
    //     params.put("pagoTotal", saleFacture.getTotal());
    //     params.put("movimientoId", saleNumber);

    //     // >>> Productos
    //     List<Map<String, Object>> jsonArrayProducts = new ArrayList<>();
    //     Map<String, Object> productsJson = new HashMap<>();

    //     productsJson.put("identificacionProducto", String.valueOf(saleFacture.getIdentificacionProducto()));
    //     System.out.println(">>> [DEBUG] Cantidad obtenida de ReciboExtended.getCantidadFactor(): " + saleFacture.getCantidadFactor());

    //     productsJson.put("cantidadProducto", saleFacture.getCantidadFactor());

    //     long valorUnitarioProducto = 0L;

    //     try {
    //         MovimientosDao mdao = new MovimientosDao();
    //         valorUnitarioProducto = mdao.obtenerPrecioDesdeMovimientoDetalleComoLong(saleFacture.getNumero());
    //         System.out.println(">>> [LOG] Precio obtenido desde ct_movimientos_detalles: " + valorUnitarioProducto);
    //     } catch (SQLException e) {
    //         System.out.println(">>> [ERROR] No se pudo obtener el precio desde BD: " + e.getMessage());
    //     }


    //     productsJson.put("valorUnitarioProducto", valorUnitarioProducto);
    //     jsonArrayProducts.add(productsJson);
    //     params.put("productos", jsonArrayProducts);

    //     // >>> Cliente
    //     params.put("codigoTipoIdentificacion", identifierTypeCode);
    //     params.put("numeroIdentificacion", identifier);

    //     // >>> Medios de pago
    //     List<MediosPagosBean> mediosPagoList = saleFacture.getMediosPagos();

    //         try {
    //             MovimientosDao mdao = new MovimientosDao();
    //             JsonArray mediosJson = mdao.obtenerMediosPagoVenta(saleFacture.getNumero());
    //             mediosPagoList = new ArrayList<>();
    //             for (JsonElement elem : mediosJson) {
    //                 JsonObject obj = elem.getAsJsonObject();
    //                 MediosPagosBean medio = new MediosPagosBean();

    //                 medio.setId(obj.get("id_medio_pago").getAsLong());
    //                 medio.setDescripcion(obj.get("descripcion").getAsString());
    //                 medio.setValor(obj.has("valor_recibido") && !obj.get("valor_recibido").isJsonNull()
    //                         ? obj.get("valor_recibido").getAsLong() : 0L);
    //                 if (medio.getValor() <= 0) {
    //                     System.out.println("⚠️ Medio de pago con valor no válido: ID = " + medio.getId());
    //                 }
    //                 if (medio.getDescripcion() == null || medio.getDescripcion().trim().isEmpty()) {
    //                     System.out.println("⚠️ Medio de pago sin descripción: ID = " + medio.getId());
    //                 }
    //                 if (medio.getId() == 0) {
    //                     System.out.println("⚠️ ID de medio de pago no válido (0): " + medio);
    //                 }
    //                 mediosPagoList.add(medio);
    //                 System.out.println(">>> Medio ID: " + medio.getId() + ", valor: " + medio.getValor());

    //             }
    //         } catch (Exception e) {
    //             System.out.println("Error al recuperar medios de pago desde la BD: " + e.getMessage());
    //         }



    //     params.put("mediosPago", mediosPagoList);
    //     if (mediosPagoList == null || mediosPagoList.isEmpty()) {
    //         String error = "🚨 [ERROR] Medios de pago vacíos. No se puede fidelizar la venta. Movimiento ID: " + saleFacture.getNumero();
    //         System.err.println(error);
    //         throw new IllegalStateException(error);
    //     }

    //     return params;
    // }
      public static Map buildClientAcumulationRequestObject(ReciboExtended saleFacture, String identifier,
                                                          String identifierTypeCode, String salePointCode) {

        String salePrefix = (saleFacture.getAtributos() != null && saleFacture.getAtributos().get("consecutivo") != null
                && !saleFacture.getAtributos().get("consecutivo").isJsonNull())
                && !saleFacture.getAtributos().get("consecutivo").isJsonPrimitive()
                ? saleFacture.getAtributos().get("consecutivo").getAsJsonObject().get("prefijo").getAsString() + "-"
                + saleFacture.getAtributos().get("consecutivo").getAsJsonObject().get("consecutivo_actual").getAsString()
                : "0";

        String saleNumber = String.valueOf(saleFacture.getNumero());
        Map params = new TreeMap();
        params.put("fechaTransaccion", sdf.format(new Date()));
        params.put("identificacionPuntoVenta", salePointCode);
        params.put("origenVenta", "EDS");
        params.put("tipoVenta", "L");

        // >>> Lógica robusta para obtener identificacionPromotor
        String identificacionPromotor = saleFacture.getIdentificacionPromotor();
        if ((identificacionPromotor == null || identificacionPromotor.trim().isEmpty())
                && saleFacture.getAtributos() != null
                && saleFacture.getAtributos().has("responsables_identificacion")
                && !saleFacture.getAtributos().get("responsables_identificacion").isJsonNull()) {
            identificacionPromotor = saleFacture.getAtributos().get("responsables_identificacion").getAsString();
            System.out.println(">>> [LOG] Asignando identificacionPromotor desde atributos.responsables_identificacion: " + identificacionPromotor);
        }
        params.put("identificacionPromotor", identificacionPromotor);

        // >>> Logs extra útiles para debug
        System.out.println(">>> [LOG] identificacionPromotor desde ReciboExtended: " + saleFacture.getIdentificacionPromotor());
        System.out.println(">>> [LOG] Atributos completos: " + saleFacture.getAtributos());

        params.put("identificacionVenta", salePrefix + "-" + saleNumber);
        params.put("valorTotalVenta", saleFacture.getTotal());
        params.put("totalImpuesto", 0);
        params.put("descuentoVenta", 0);
        params.put("pagoTotal", saleFacture.getTotal());
        params.put("movimientoId", saleNumber);

        // >>> Productos
        List<Map<String, Object>> jsonArrayProducts = new ArrayList<>();
        Map<String, Object> productsJson = new HashMap<>();

        productsJson.put("identificacionProducto", String.valueOf(saleFacture.getIdentificacionProducto()));
        System.out.println(">>> [DEBUG] Cantidad obtenida de ReciboExtended.getCantidadFactor(): " + saleFacture.getCantidadFactor());

        productsJson.put("cantidadProducto", saleFacture.getCantidadFactor());

        long valorUnitarioProducto = 0L;

        try {
            Long movimientoId = saleFacture.getNumero();
            System.out.println(">>> [DEBUG] ID Movimiento a buscar: " + movimientoId);
    
            FindByParamerMovementDetailDUseCase<BigDecimal> useCase = 
        new FindByParamerMovementDetailDUseCase<>(
            saleFacture.getNumero(), 
            CtMovimientoDetallesEnum.PRECIO.getColumnName(),
            BigDecimal.class
        );
        Optional<BigDecimal> precioOptional = useCase.execute();
        System.out.println(">>> [DEBUG] Resultado Optional: " + precioOptional);

        if (precioOptional.isPresent() && precioOptional.get() != null) {
            valorUnitarioProducto = precioOptional.get().setScale(0, RoundingMode.DOWN).longValue();
        }
        System.out.println(">>> [LOG] Precio obtenido desde caso de uso: " + valorUnitarioProducto);
        } catch (Exception e) {
            System.out.println(">>> [ERROR] No se pudo obtener el precio desde BD: " + e.getMessage());
            e.printStackTrace();
        }


        productsJson.put("valorUnitarioProducto", valorUnitarioProducto);
        jsonArrayProducts.add(productsJson);
        params.put("productos", jsonArrayProducts);

        // >>> Cliente
        params.put("codigoTipoIdentificacion", identifierTypeCode);
        params.put("numeroIdentificacion", identifier);

        // >>> Medios de pago
        List<MediosPagosBean> mediosPagoList = saleFacture.getMediosPagos();

            try {
                MovimientosDao mdao = new MovimientosDao();
                JsonArray mediosJson = mdao.obtenerMediosPagoVenta(saleFacture.getNumero());
                mediosPagoList = new ArrayList<>();
                for (JsonElement elem : mediosJson) {
                    JsonObject obj = elem.getAsJsonObject();
                    MediosPagosBean medio = new MediosPagosBean();

                    medio.setId(obj.get("id_medio_pago").getAsLong());
                    medio.setDescripcion(obj.get("descripcion").getAsString());
                    medio.setValor(obj.has("valor_recibido") && !obj.get("valor_recibido").isJsonNull()
                            ? obj.get("valor_recibido").getAsLong() : 0L);
                    if (medio.getValor() <= 0) {
                        System.out.println("⚠️ Medio de pago con valor no válido: ID = " + medio.getId());
                    }
                    if (medio.getDescripcion() == null || medio.getDescripcion().trim().isEmpty()) {
                        System.out.println("⚠️ Medio de pago sin descripción: ID = " + medio.getId());
                    }
                    if (medio.getId() == 0) {
                        System.out.println("⚠️ ID de medio de pago no válido (0): " + medio);
                    }
                    mediosPagoList.add(medio);
                    System.out.println(">>> Medio ID: " + medio.getId() + ", valor: " + medio.getValor());

                }
            } catch (Exception e) {
                System.out.println("Error al recuperar medios de pago desde la BD: " + e.getMessage());
            }



        params.put("mediosPago", mediosPagoList);
        if (mediosPagoList == null || mediosPagoList.isEmpty()) {
            String error = "🚨 [ERROR] Medios de pago vacíos. No se puede fidelizar la venta. Movimiento ID: " + saleFacture.getNumero();
            System.err.println(error);
            throw new IllegalStateException(error);
        }

        return params;
    }


///Metodo unico para capturar  el consecutivo cuando si es remision
public static String obtenerConsecutivoRemision(ReciboExtended saleFacture, MovimientosDao mdao) {
    if (saleFacture.getAtributos() != null) {
        JsonObject atributos = saleFacture.getAtributos();
        // 1. Intentar obtener el consecutivo de los atributos
        if (atributos.has("consecutivo") && !atributos.get("consecutivo").isJsonNull()) {
            JsonElement consecutivo = atributos.get("consecutivo");
            if (consecutivo.isJsonPrimitive()) {
                return consecutivo.getAsString();
            } else if (consecutivo.isJsonObject() && consecutivo.getAsJsonObject().has("consecutivo_actual")) {
                return consecutivo.getAsJsonObject().get("consecutivo_actual").getAsString();
            }
        }
        // 2. Si es remisión y no hay consecutivo, buscar en la BD
        boolean esRemision = atributos.has("tipoVenta") && 
                             !atributos.get("tipoVenta").isJsonNull() && 
                             atributos.get("tipoVenta").getAsInt() == 100;
        if (esRemision) {
            //long consecutivoBD = mdao.obtenerConsecutivoDesdeMovimiento(saleFacture.getNumero());
            long consecutivoBD = new ObtenerConsecutivoDesdeMovimientoUseCase(saleFacture.getNumero()).execute();
            if (consecutivoBD > 0) {
                return String.valueOf(consecutivoBD);
            }
        }
    }
    // 3. Fallback: usar el número de venta
    return String.valueOf(saleFacture.getNumero());
}
    private static JsonObject buildClientReclamacionRequestObject(ReciboExtended saleFacture, String identifier,
                                                                  String identifierTypeCode, String salePointCode, ArrayList<com.bean.MediosPagosBean> MediosPagos) {
        JsonObject request = new JsonObject();
        MovimientosDao mdao = new MovimientosDao();
        // Asignación inicial por compatibilidad
        String saleId = obtenerConsecutivoRemision(saleFacture, mdao);


        System.out.println("🔍 [DEBUG] Analizando atributos del recibo para identificacionVenta:");
        System.out.println("🔍 [DEBUG] Atributos completos: " + (saleFacture.getAtributos() != null ? saleFacture.getAtributos().toString() : "null"));



        System.out.println("🔍 [DEBUG] SaleId final: " + saleId);

        String saleNumber = String.valueOf(saleFacture.getNumero());
        JsonArray bonosVenta = mdao.getBonosVenta(saleFacture.getNumero());
        SurtidorDao sdao = new SurtidorDao();
        long productSaleFamily = sdao.getCodigoProductoViveTerpel(saleFacture.getIdentificacionProducto());
        NovusUtils.printLn("🧾 ID Producto recibido: " + saleFacture.getIdentificacionProducto());
        NovusUtils.printLn("🧾 Código Salesforce producto: " + productSaleFamily);

        if (productSaleFamily == 0) {
            request = null;
            return request;
        }
        JsonObject saleJson = new JsonObject();
        saleJson.addProperty("fechaTransaccion", sdf.format(new Date()));
        saleJson.addProperty("identificacionPuntoVenta", salePointCode);
        saleJson.addProperty("origenVenta", "EDS");
        saleJson.addProperty("tipoVenta", "L");
        // 🔍 Obtener promotor con fallback a 'responsables_identificacion' en atributos
        String identificacionPromotor = saleFacture.getIdentificacionPromotor();
        if ((identificacionPromotor == null || identificacionPromotor.trim().isEmpty())
                && saleFacture.getAtributos() != null
                && saleFacture.getAtributos().has("responsables_identificacion")
                && !saleFacture.getAtributos().get("responsables_identificacion").isJsonNull()) {
            identificacionPromotor = saleFacture.getAtributos().get("responsables_identificacion").getAsString();
            NovusUtils.printLn("✅ Promotor obtenido desde atributos: " + identificacionPromotor);
        } else {
            NovusUtils.printLn("⚠️ Promotor viene vacío o nulo");
        }
        saleJson.addProperty("identificacionPromotor", identificacionPromotor);

        saleJson.addProperty("identificacionVenta", saleId);
        saleJson.addProperty("valorTotalVenta", saleFacture.getTotal());
        saleJson.addProperty("totalImpuesto", 0);
        saleJson.addProperty("descuentoVenta", 0);
        saleJson.addProperty("pagoTotal", saleFacture.getTotal());
        JsonArray jsonArrayProducts = new JsonArray();
        JsonObject productsJson = new JsonObject();
        productsJson.addProperty("identificacionProducto", String.valueOf(productSaleFamily));
        productsJson.addProperty("cantidadProducto", saleFacture.getCantidadFactor());
        productsJson.addProperty("valorUnitarioProducto", saleFacture.getPrecio());
        productsJson.addProperty("marca", "Terpel");
        productsJson.addProperty("categoria", "categoria");
        productsJson.addProperty("subcategoria", "subcategoria");
        productsJson.addProperty("codigoVoucher", saleNumber);
        jsonArrayProducts.add(productsJson);
        saleJson.add("productos", jsonArrayProducts);
        JsonObject clientJson = new JsonObject();
        clientJson.addProperty("codigoTipoIdentificacion", identifierTypeCode);
        clientJson.addProperty("numeroIdentificacion", identifier);
        JsonArray jsonArrayMeansPayments = new JsonArray();
        for (com.bean.MediosPagosBean meanPayment : MediosPagos) {
            JsonObject meansPaymentsJson = new JsonObject();
            long salesForceIdMp = sdao.getCodigoSalesForceMP(meanPayment.getId());
            if (salesForceIdMp == 0) {
                request = null;
                return request;
            }
            if (meanPayment.isPagosExternoValidado()) {
                for (BonoViveTerpel bono : meanPayment.getBonosViveTerpel()) {
                    if (!existeBono(bonosVenta, Long.parseLong(bono.getVoucher()))) {
                        JsonObject BonoViveTerpelMP = new JsonObject();
                        BonoViveTerpelMP.addProperty("identificacionMedioPago", String.valueOf(salesForceIdMp));
                        BonoViveTerpelMP.addProperty("valorPago", bono.getValor());
                        BonoViveTerpelMP.addProperty("millasRedimidas", 0);
                        BonoViveTerpelMP.addProperty("codigoVoucher", bono.getVoucher());
                        jsonArrayMeansPayments.add(BonoViveTerpelMP);
                    }
                }
                continue;
            }
            if (!meanPayment.getVoucher().isEmpty()) {
                for (String voucher : meanPayment.getVoucher().split(",")) {
                    if (meanPayment.getId() == 20000) {
                        if (!existeBono(bonosVenta, Long.parseLong(voucher))) {
                            meansPaymentsJson.addProperty("identificacionMedioPago", String.valueOf(salesForceIdMp));
                            meansPaymentsJson.addProperty("valorPago", meanPayment.getValor());
                            meansPaymentsJson.addProperty("millasRedimidas", 0);
                            meansPaymentsJson.addProperty("codigoVoucher", meanPayment.getVoucher());
                            jsonArrayMeansPayments.add(meansPaymentsJson);
                        }
                    }
                }
            }
        }
        request.add("datosVenta", saleJson);
        request.add("identificacionClienteRedencion", clientJson);
        request.add("mediosPagoRedencion", jsonArrayMeansPayments);
        return request;
    }






    public static JsonObject fetchClientReclamacion(ReciboExtended saleFacture, String identifier,
                                                    String identifierTypeCode, String salePointCode, ArrayList<com.bean.MediosPagosBean> MediosPagos) {
        JsonObject response = null;
        TreeMap<String, String> header = header = new TreeMap<>();
        header.put("Authorization", "Basic cGFzc3BvcnR4OlQ0MUFYUWtYSjh6");
        header.put("Content-Type", "application/json");
        header.put("Accept", "*/*");
        header.put("dispositivo", "proyectos");
        header.put("fecha", sdfISO.format(new Date()) + "-05");
        header.put("aplicacion", "lazoexpress");
        JsonObject body = buildClientReclamacionRequestObject(saleFacture, identifier, identifierTypeCode,
                salePointCode, MediosPagos);
        if (body == null) {
            JsonObject Error = new JsonObject();
            Error.addProperty("mensajeError", "Codigo Salesforce para producto o medio de pago no encontrado");
            Error.addProperty("statusCode", 200);
            return Error;
        }
        NovusUtils.printLn("JSON RECLAMACION: " + body);
        ClientWSAsync clientWS = new ClientWSAsync("RECLAMACION CLIENTE",
                NovusConstante.SECURE_CENTRAL_POINT_RECLAMACION_CLIENTE, NovusConstante.POST, body, true, false,
                header);
        try {
            response = clientWS.esperaRespuesta();
            if (response != null) {
                response.addProperty("statusCode", clientWS.getStatus());
            }
            return response;
        } catch (Exception ex) {
            NovusUtils.printLn(ex.getMessage());
            return null;
        }
    }


    public static JsonObject buildClientRedencionViveTerpelObject(ReciboExtended saleFacture, String identifier,
            String identifierTypeCode, String PinCliente, String salePointCode,
            ArrayList<com.bean.MediosPagosBean> MediosPagos) {
        JsonObject request = new JsonObject();
        String cipheredIdentifier = encriptacionBase64AES256(identifier);
        String cipheredPin = encriptacionBase64AES256(PinCliente);
        String salePrefix = (saleFacture.getAtributos() != null && saleFacture.getAtributos().get("consecutivo") != null
                && !saleFacture.getAtributos().get("consecutivo").isJsonNull())
                ? saleFacture.getAtributos().get("consecutivo").isJsonPrimitive()
                ? saleFacture.getAtributos().get("consecutivo").getAsString()
                : saleFacture.getAtributos().get("consecutivo").getAsJsonObject().get("prefijo")
                        .getAsString() + "-"
                + saleFacture.getAtributos().get("consecutivo").getAsJsonObject()
                        .get("consecutivo_actual").getAsString()
                : "";
        String saleNumber = String.valueOf(saleFacture.getNumero());
        SurtidorDao sdao = new SurtidorDao();
        long productSaleFamily = sdao.getCodigoProductoViveTerpel(saleFacture.getIdentificacionProducto());
        if (productSaleFamily == 0) {
            request = null;
            return request;
        }
        JsonObject saleJson = new JsonObject();
        saleJson.addProperty("fechaTransaccion", sdf.format(new Date()));
        saleJson.addProperty("identificacionPuntoVenta", salePointCode);
        saleJson.addProperty("origenVenta", "EDS");
        saleJson.addProperty("tipoVenta", "L");
        saleJson.addProperty("identificacionPromotor", saleFacture.getIdentificacionPromotor());
        saleJson.addProperty("identificacionVenta", salePrefix + "-" + saleNumber);
        saleJson.addProperty("valorTotalVenta", saleFacture.getTotal());
        saleJson.addProperty("totalImpuesto", 0);
        saleJson.addProperty("descuentoVenta", 0);
        saleJson.addProperty("pagoTotal", saleFacture.getTotal());
        JsonArray jsonArrayProducts = new JsonArray();
        JsonObject productsJson = new JsonObject();
        productsJson.addProperty("identificacionProducto", String.valueOf(productSaleFamily));
        productsJson.addProperty("cantidadProducto", saleFacture.getCantidadFactor());
        productsJson.addProperty("valorUnitarioProducto", saleFacture.getPrecio());
        productsJson.addProperty("marca", "Terpel");
        productsJson.addProperty("categoria", "categoria");
        productsJson.addProperty("subcategoria", "subcategoria");
        productsJson.addProperty("codigoVoucher", saleNumber);
        jsonArrayProducts.add(productsJson);
        saleJson.add("productos", jsonArrayProducts);
        JsonObject clientJson = new JsonObject();
        clientJson.addProperty("codigoTipoIdentificacion", identifierTypeCode);
        clientJson.addProperty("numeroIdentificacion", cipheredIdentifier);
        clientJson.addProperty("pin", cipheredPin);
        JsonArray jsonArrayMeansPayments = new JsonArray();
        for (com.bean.MediosPagosBean meanPayment : MediosPagos) {
            JsonObject meansPaymentsJson = new JsonObject();
            long salesForceIdMp = sdao.getCodigoSalesForceMP(meanPayment.getId());
            if (salesForceIdMp == 0) {
                request = null;
                return request;
            }
            // if (meanPayment.isPagosExternoValidado()) {
            // for (BonoViveTerpel bono : meanPayment.getBonosViveTerpel()) {
            // JsonObject BonoViveTerpelMP = new JsonObject();
            // BonoViveTerpelMP.addProperty("identificacionMedioPago",
            // String.valueOf(salesForceIdMp));
            // BonoViveTerpelMP.addProperty("valorPago", bono.getValor());
            // BonoViveTerpelMP.addProperty("millasRedimidas", 0);
            // BonoViveTerpelMP.addProperty("codigoVoucher", bono.getVoucher());
            // jsonArrayMeansPayments.add(BonoViveTerpelMP);
            // }
            // continue;
            // }
            meansPaymentsJson.addProperty("identificacionMedioPago", String.valueOf(salesForceIdMp));
            meansPaymentsJson.addProperty("valorPago", meanPayment.getValor());
            meansPaymentsJson.addProperty("millasRedimidas", 0);
            meansPaymentsJson.addProperty("codigoVoucher", meanPayment.getVoucher());
            jsonArrayMeansPayments.add(meansPaymentsJson);
        }
        request.add("datosVenta", saleJson);
        request.add("identificacionClienteRedencion", clientJson);
        request.add("mediosPagoRedencion", jsonArrayMeansPayments);
        return request;
    }

    public static JsonObject fetchClientRedencion(ReciboExtended saleFacture, String identifier,
            String identifierTypeCode, String PinCliente, String salePointCode,
            ArrayList<com.bean.MediosPagosBean> MediosPagos) {
        JsonObject response = null;
        TreeMap<String, String> header = new TreeMap<>();
        header.put("Authorization", "Basic cGFzc3BvcnR4OlQ0MUFYUWtYSjh6");
        header.put("Content-Type", "application/json");
        header.put("Accept", "*/*");
        header.put("dispositivo", "proyectos");
        header.put("fecha", sdfISO.format(new Date()) + "-05");
        header.put("aplicacion", "lazoexpress");
        JsonObject body = buildClientRedencionViveTerpelObject(saleFacture, identifier, identifierTypeCode, PinCliente,
                salePointCode, MediosPagos);
        if (body == null) {
            JsonObject error = new JsonObject();
            error.addProperty("mensajeError", "Codigo Salesforce para producto o medio de pago no encontrado");
            error.addProperty("statusCode", 200);
            return error;
        }
        NovusUtils.printLn("JSON RECLAMACION: " + body);
        JsonObject error = new JsonObject();
        error.addProperty("mensajeError", "Opps... aun no esta disponible");
        error.addProperty("statusCode", 200);
        return error;
        // ClientWSAsync clientWS = new ClientWSAsync("REDENCION PUNTOS VIVE TERPEL",
        // NovusConstante.SECURE_CENTRAL_POINT_RECLAMACION_CLIENTE, NovusConstante.POST,
        // body, true, false, header);
        // try {
        // response = clientWS.esperaRespuesta();
        // if (response != null) {
        // response.addProperty("statusCode", clientWS.getStatus());
        // }
        // return response;
        // } catch (Exception ex) {
        // NovusUtils.printLn(ex);
        // return null;
        // }
    }

    public static boolean existeBono(JsonArray bonos, long bonoVoucher) {
        boolean existe = false;
        if (bonos != null) {
            for (JsonElement bono : bonos) {
                JsonObject bonosVenta = bono.getAsJsonObject();
                if (bonoVoucher == bonosVenta.get("AFP").getAsLong()) {
                    existe = true;
                }
            }
        }
        return existe;
    }

    public static Map buildRequestRedencionBonoCanastilla(InformacionVentaClienteFE infoVenta) {
        Map params = new TreeMap();

        DatosVenta datosVenta = new DatosVenta();
        datosVenta.setFechaTransaccion(infoVenta.getDatos_FE().getVenta().getFechaTransaccion());
        datosVenta.setIdentificacionPuntoVenta(ConfigurationFacade.fetchSalePointIdentificator());
        datosVenta.setOrigenVenta("EDS");
        datosVenta.setTipoVenta(getTipoVenta(infoVenta.getDatos_FE().getVenta().getOperacion()));
        datosVenta.setIdentificacionPromotor(infoVenta.getDatos_FE().getVenta().getIdentificacionPromotor());
        datosVenta.setIdentificacionVenta(infoVenta.getDatos_FE().getVenta().getIdentificadorTicketVenta() + "");
        datosVenta.setValorTotalVenta(infoVenta.getDatos_FE().getVenta().getVentaTotal());
        datosVenta.setPagoTotal(infoVenta.getDatos_FE().getVenta().getVentaTotal());

        List<DetallesVenta> detallesVenta = infoVenta.getDatos_FE().getDetallesVenta();
        List<Productos> productos = new ArrayList<>();
        for (DetallesVenta detalleVenta : detallesVenta) {
            Productos producto = new Productos();
            producto.setIdentificacionProducto(detalleVenta.getCodigoBarra());
            producto.setCantidadProducto(detalleVenta.getCantidadVenta());
            producto.setValorUnitarioProducto(detalleVenta.getPrecioVentaFeWeb());
            producto.setMarca("Terpel");
            producto.setCategoria("categoria");
            producto.setSubcategoria("subcategoria");
            productos.add(producto);
        }
        datosVenta.setProductos(productos);

        IdentificacionClienteRedencion identificacionClienteRedencion = new IdentificacionClienteRedencion();
        identificacionClienteRedencion.setCodigoTipoIdentificacion("");
        identificacionClienteRedencion.setNumeroIdentificacion("");

        List<MediosPagoVenta> pagos = infoVenta.getDatos_FE().getPagos();
        List<MediosPagoRedencion> mediosPagoRedencion = new ArrayList<>();

        List<AtributosBono> detallesBonoVenta = infoVenta.getDatos_FE().getVenta().getDetallesBono();

        if (detallesBonoVenta != null) {
            for (AtributosBono atributosBono : detallesBonoVenta) {
                MediosPagoRedencion medioPago = new MediosPagoRedencion();
                medioPago.setIdentificacionMedioPago("32");
                medioPago.setValorPago(atributosBono.getVFP());
                medioPago.setCodigoVoucher(atributosBono.getAFP());
                mediosPagoRedencion.add(medioPago);
            }
        }

        params.put("datosVenta", datosVenta);
        params.put("identificacionClienteRedencion", identificacionClienteRedencion);
        params.put("mediosPagoRedencion", mediosPagoRedencion);

        return params;
    }

    public static String getTipoVenta(int operacion) {
        String tipoVenta = "";
        switch (operacion) {
            case 9:
                tipoVenta = "C";
                break;
            case 35:
                tipoVenta = Main.SIN_SURTIDOR ? "CDL" : "K";
                break;
            default:
                break;
        }
        return tipoVenta;
    }

}
