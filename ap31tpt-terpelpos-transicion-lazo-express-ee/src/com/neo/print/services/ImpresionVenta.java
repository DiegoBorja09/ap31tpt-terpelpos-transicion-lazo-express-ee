/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.neo.print.services;

import com.WT2.Containers.Dependency.SingletonMedioPago;
import com.WT2.ImpresonesFE.domain.entities.ParametrosPeticionFePrinter;
import com.WT2.ImpresonesFE.domain.entities.PeticionFeImprimir;
import com.application.useCases.movimientos.FinByTipoMovimientoUseCase;
import com.controllers.ClientWSAsync;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.firefuel.Main;
import com.google.gson.JsonObject;
import com.neo.app.bean.Recibo;
import java.util.Objects;
import java.util.TreeMap;

/**
 *
 * @author Usuario
 */
public class ImpresionVenta {

    boolean facturaPOS = Main.getParametroCoreBoolean(NovusConstante.PREFERENCE_FACTURACION_POS, false);

    public ResultadoImpresion imprimirVenta(Recibo recibo, boolean isFe, String ruta) {
        if (ruta.isEmpty()) ruta = "factura";

        if (recibo.getAtributos() != null && recibo.getAtributos().has("tipoVenta") &&
                recibo.getAtributos().get("tipoVenta").getAsInt() == 100) {
            ruta = "remision";
        }

        if (recibo.getAtributos() != null &&
                recibo.getAtributos().has("is_especial") &&
                recibo.getAtributos().get("is_especial").getAsBoolean() &&
                recibo.getAtributos().has("consecutivo") &&
                recibo.getAtributos().get("consecutivo").isJsonObject() &&
                recibo.getAtributos().getAsJsonObject("consecutivo").has("prefijo") &&
                recibo.getAtributos().getAsJsonObject("consecutivo").get("prefijo").getAsString().contains("PROPIO")) {
            ruta = "consumo-propio";
        }

        // Nuevo: si no hay "tipo" en atributos, consultarlo en BD
        String tipo = null;
        if (recibo.getAtributos() != null && recibo.getAtributos().has("tipo")) {
            tipo = recibo.getAtributos().get("tipo").getAsString();
        } else {
            // Usar la nueva arquitectura dinámica
            tipo = FinByTipoMovimientoUseCase.consultarTipo(recibo.getNumero());
        }

        if ("014".equals(tipo)) {
            ruta = "calibracion";
        }

        System.out.println("🚀 Ruta seleccionada para imprimir: " + ruta);
        return impirmir(recibo.getNumero(), ruta, isFe, recibo);
    }


    // public void imprimirVenta(Recibo recibo, boolean isFe, String ruta) {
    //     if (ruta.isEmpty()) ruta = "factura";

    //     if (recibo.getAtributos() != null && recibo.getAtributos().has("tipoVenta") &&
    //             recibo.getAtributos().get("tipoVenta").getAsInt() == 100) {
    //         ruta = "remision";
    //     }

    //     if (recibo.getAtributos() != null &&
    //             recibo.getAtributos().has("is_especial") &&
    //             recibo.getAtributos().get("is_especial").getAsBoolean() &&
    //             recibo.getAtributos().has("consecutivo") &&
    //             recibo.getAtributos().get("consecutivo").isJsonObject() &&
    //             recibo.getAtributos().getAsJsonObject("consecutivo").has("prefijo") &&
    //             recibo.getAtributos().getAsJsonObject("consecutivo").get("prefijo").getAsString().contains("PROPIO")) {
    //         ruta = "consumo-propio";
    //     }

    //     // Nuevo: si no hay "tipo" en atributos, consultarlo en BD
    //     String tipo = null;
    //     if (recibo.getAtributos() != null && recibo.getAtributos().has("tipo")) {
    //         tipo = recibo.getAtributos().get("tipo").getAsString();
    //     } else {
    //         MovimientosDao dao = new MovimientosDao();
    //         tipo = dao.consultarTipoMovimiento(recibo.getNumero());
    //     }

    //     if ("014".equals(tipo)) {
    //         ruta = "calibracion";
    //     }

    //     System.out.println("🚀 Ruta seleccionada para imprimir: " + ruta);
    //     impirmir(recibo.getNumero(), ruta, isFe, recibo);
    // }
    private ResultadoImpresion impirmir(long idMovimiento, String path, boolean isFe, Recibo recibo) {
        JsonObject comando = new JsonObject();


        comando.addProperty("identificadorMovimiento", idMovimiento);
        comando.addProperty("identificadorEquipo", Main.credencial.getEquipos_id());
        comando.addProperty("placa", recibo.getPlaca());
        String odometro = recibo.getOdometro();
        if (odometro != null && !odometro.trim().isEmpty() && !odometro.equals("0")) {
            comando.addProperty("odometro", odometro);
        }
        comando.addProperty("numero", "");
        comando.addProperty("orden", "");
        

            // String funcion = "IMPRIMIR VENTAS";
            // String url = NovusConstante.SECURE_CENTRAL_POINT_IMPRESION_VENTA + "/" + path;
            // TreeMap<String, String> header = new TreeMap<>();
            // header.put("Content-type", "application/json");
            // ClientWSAsync clientWSAsync = new ClientWSAsync(funcion, url, NovusConstante.POST, comando, true, false, header);

        String funcion = "IMPRESION DE VENTA";
        String url = NovusConstante.SECURE_CENTRAL_POINT_ACTUALIZAR_ATRIBUTOS_VENTA;
        System.out.println("🔍 DEBUG impirmir(): archvivo ImpresionVenta.java linea 120");
        System.out.println("🔍 DEBUG: URL " + url);
        TreeMap<String, String> header = new TreeMap<>();
        header.put("Content-type", "application/json");
        ClientWSAsync clientWSAsync = new ClientWSAsync(funcion, url, NovusConstante.PUT, comando, true, false, header);
        if (!path.equals("factura-electronica")) {
            System.out.println("🔍 DEBUG: se ejecuta el if");
            try {
                clientWSAsync.start();
                JsonObject x = clientWSAsync.getResponse();
                NovusUtils.printLn(x.toString());

            } catch (Exception e) {
                NovusUtils.printLn(e.getMessage());
            }

        }

        funcion = "IMPRIMIR VENTAS";
        JsonObject json = new JsonObject();
        json.addProperty("movement_id", idMovimiento);
        json.addProperty("flow_type", "CONSULTAR_VENTAS");
        json.addProperty("report_type", path.toUpperCase());
        JsonObject bodyJson = new JsonObject();
        json.add("body", bodyJson);
        url = NovusConstante.SECURE_CENTRAL_POINT_PRINT_TICKET_SALES;
        System.out.println("imprimie url: " + url);
        System.out.println("imprimie body: " + json);
        header = new TreeMap<>();
        header.put("Content-type", "application/json");
        clientWSAsync = new ClientWSAsync(funcion, url, NovusConstante.POST, json, true, false, header);
        
        // Solo enviar el request sin esperar respuesta
        // ClientWSAsync extiende Thread, así que start() ya crea el hilo
        clientWSAsync.start();
        
        System.out.println("✅ Request de impresión enviado - Movimiento: " + idMovimiento + ", Tipo: " + path);
        return ResultadoImpresion.exito("Se ha enviado la impresión correctamente");

    }

    public static class ResultadoImpresion {
        private final boolean exito;
        private final String mensaje;

        private ResultadoImpresion(boolean exito, String mensaje) {
            this.exito = exito;
            this.mensaje = mensaje;
        }

        public static ResultadoImpresion exito(String mensaje) {
            return new ResultadoImpresion(true, mensaje);
        }

        public static ResultadoImpresion error(String mensaje) {
            return new ResultadoImpresion(false, mensaje);
        }

        public boolean esExito() {
            return exito;
        }

        public String getMensaje() {
            return mensaje;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ResultadoImpresion other = (ResultadoImpresion) obj;
            return exito == other.exito && Objects.equals(mensaje, other.mensaje);
        }

        @Override
        public int hashCode() {
            return Objects.hash(exito, mensaje);
        }

        @Override
        public String toString() {
            return "ResultadoImpresion{" + "exito=" + exito + ", mensaje='" + mensaje + '\'' + '}';
        }
    }
}