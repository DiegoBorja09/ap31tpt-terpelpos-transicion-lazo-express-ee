package com.application.useCases.reportes;

import com.application.core.AbstractUseCase;
import com.controllers.NovusUtils;
import com.domain.dto.reportes.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;

/**
 * 🎯 Caso de uso para procesar los datos raw del arqueo de promotor.
 * 
 * Responsabilidades:
 * - Extraer datos del JsonObject del backend
 * - Ordenar y consolidar productos
 * - Agrupar medios de pago
 * - Calcular totales
 * - Retornar DTO estructurado y limpio
 * 
 * @author Clean Architecture
 * @version 1.0
 */
public class ProcesarDatosArqueoUseCase extends AbstractUseCase<ArqueoProcesadoDTO> {
    
    private final JsonObject data;
    private final String promotor;
    private final Date fecha;
    private final Long promotorId;
    
    public ProcesarDatosArqueoUseCase(JsonObject data, String promotor, Date fecha, Long promotorId) {
        this.data = data;
        this.promotor = promotor;
        this.fecha = fecha;
        this.promotorId = promotorId;
    }
    
    @Override
    public ArqueoProcesadoDTO run() {
        try {
            NovusUtils.printLn("🔄 Iniciando procesamiento de datos de arqueo...");
            
            ArqueoProcesadoDTO arqueo = new ArqueoProcesadoDTO();
            
            // 1. Información básica
            arqueo.setPromotor(promotor);
            arqueo.setPromotorId(promotorId);
            arqueo.setFechaInicio(fecha);
            
            // 2. Procesar ventas
            procesarVentas(arqueo);
            
            // 3. Procesar medios de pago
            procesarMediosPago(arqueo);
            
            // 4. Calcular totales
            calcularTotales(arqueo);
            
            NovusUtils.printLn("✅ Datos procesados correctamente");
            NovusUtils.printLn("   - Ventas combustible: " + arqueo.getVentasCombustible().size());
            NovusUtils.printLn("   - Ventas canastilla: " + arqueo.getVentasCanastilla().size());
            NovusUtils.printLn("   - Ventas market: " + arqueo.getVentasMarket().size());
            NovusUtils.printLn("   - Medios de pago: " + arqueo.getMediosPago().size());
            
            return arqueo;
            
        } catch (Exception e) {
            NovusUtils.printLn("❌ Error procesando datos: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al procesar datos del arqueo", e);
        }
    }
    
    /**
     * 🏗️ Procesar ventas desde el JSON raw
     */
    private void procesarVentas(ArqueoProcesadoDTO arqueo) {
        if (!data.has("data") || !data.get("data").getAsJsonObject().has("promotor")) {
            return;
        }
        
        JsonArray ventasArray = data.get("data").getAsJsonObject().get("promotor").getAsJsonArray();
        
        // Mapas para consolidar productos por código+precio
        Map<String, VentaCombustibleDTO> mapCombustible = new TreeMap<>();
        Map<String, VentaProductoDTO> mapCanastilla = new TreeMap<>();
        Map<String, VentaProductoDTO> mapMarket = new TreeMap<>();
        Map<String, VentaProductoDTO> mapComplementarios = new TreeMap<>();
        
        int transaccionesCombustible = 0;
        int transaccionesCanastilla = 0;
        int transaccionesMarket = 0;
        int transaccionesComplementarios = 0;
        
        // Procesar cada venta
        for (JsonElement element : ventasArray) {
            JsonObject venta = element.getAsJsonObject();
            String tipo = venta.get("tipo").getAsString();
            
            switch (tipo) {
                case "combustible":
                case "calibracion":
                    procesarVentaCombustible(venta, mapCombustible);
                    transaccionesCombustible++;
                    break;
                    
                case "canastilla":
                    procesarVentaProducto(venta, mapCanastilla);
                    transaccionesCanastilla++;
                    break;
                    
                case "kiosco":
                    procesarVentaProducto(venta, mapMarket);
                    transaccionesMarket++;
                    break;
                    
                case "complementarios":
                    procesarVentaProducto(venta, mapComplementarios);
                    transaccionesComplementarios++;
                    break;
            }
        }
        
        // Convertir mapas a listas
        arqueo.setVentasCombustible(new ArrayList<>(mapCombustible.values()));
        arqueo.setVentasCanastilla(new ArrayList<>(mapCanastilla.values()));
        arqueo.setVentasMarket(new ArrayList<>(mapMarket.values()));
        arqueo.setVentasComplementarios(new ArrayList<>(mapComplementarios.values()));
        
        // Guardar metadata
        arqueo.setNumeroTransaccionesCombustible(transaccionesCombustible);
        arqueo.setNumeroTransaccionesCanastilla(transaccionesCanastilla);
        arqueo.setNumeroTransaccionesMarket(transaccionesMarket);
        arqueo.setNumeroTransaccionesComplementarios(transaccionesComplementarios);
    }
    
    /**
     * 🛢️ Procesar venta de combustible (consolida por producto+precio)
     */
    private void procesarVentaCombustible(JsonObject venta, Map<String, VentaCombustibleDTO> map) {
        String producto = venta.get("producto").getAsString();
        float precio = venta.get("precio").getAsFloat();
        float galones = venta.get("cantidad").getAsFloat();
        float descuento = venta.get("descuento_calculado").getAsFloat();
        float total = venta.get("total").getAsFloat();
        
        String key = producto + "-" + precio;
        
        if (map.containsKey(key)) {
            // Consolidar
            VentaCombustibleDTO existing = map.get(key);
            existing.setGalones(existing.getGalones() + galones);
            existing.setDescuento(existing.getDescuento() + descuento);
            existing.setTotal(existing.getTotal() + total);
        } else {
            // Crear nuevo
            VentaCombustibleDTO dto = new VentaCombustibleDTO(
                producto, galones, precio, descuento, total
            );
            map.put(key, dto);
        }
    }
    
    /**
     * 🛒 Procesar venta de producto (consolida por código+precio)
     */
    private void procesarVentaProducto(JsonObject venta, Map<String, VentaProductoDTO> map) {
        String codigo = venta.get("id").getAsString();
        String producto = venta.get("producto").getAsString();
        float precio = venta.get("precio").getAsFloat();
        float cantidad = venta.get("cantidad").getAsFloat();
        float total = venta.get("total").getAsFloat();
        
        String key = codigo + "-" + precio;
        
        if (map.containsKey(key)) {
            // Consolidar
            VentaProductoDTO existing = map.get(key);
            existing.setCantidad(existing.getCantidad() + cantidad);
            existing.setTotal(existing.getTotal() + total);
        } else {
            // Crear nuevo
            VentaProductoDTO dto = new VentaProductoDTO(
                codigo, producto, cantidad, precio, total
            );
            map.put(key, dto);
        }
    }
    
    /**
     * 💳 Procesar medios de pago
     */
    private void procesarMediosPago(ArqueoProcesadoDTO arqueo) {
        if (!data.has("data") || !data.get("data").getAsJsonObject().has("medios_pagos")) {
            return;
        }
        
        JsonArray mediosArray = data.get("data").getAsJsonObject().get("medios_pagos").getAsJsonArray();
        Map<String, MedioPagoResumenDTO> mapMedios = new TreeMap<>();
        
        for (JsonElement element : mediosArray) {
            JsonObject medio = element.getAsJsonObject();
            
            String nombreMedio = medio.get("medio_pago").getAsString();
            String tipo = medio.get("tipo").getAsString();
            float monto = medio.get("total").getAsFloat();
            
            // Obtener o crear medio
            MedioPagoResumenDTO dto = mapMedios.get(nombreMedio);
            if (dto == null) {
                dto = new MedioPagoResumenDTO();
                dto.setMedio(nombreMedio);
                mapMedios.put(nombreMedio, dto);
            }
            
            // Sumar por tipo
            switch (tipo) {
                case "combustible":
                case "calibracion":
                    dto.setCombustible(dto.getCombustible() + monto);
                    break;
                case "canastilla":
                    dto.setCanastilla(dto.getCanastilla() + monto);
                    break;
                case "kiosco":
                    dto.setMarket(dto.getMarket() + monto);
                    break;
                case "complementarios":
                    dto.setComplementarios(dto.getComplementarios() + monto);
                    break;
            }
        }
        
        arqueo.setMediosPago(new ArrayList<>(mapMedios.values()));
    }
    
    /**
     * 🧮 Calcular totales generales
     */
    private void calcularTotales(ArqueoProcesadoDTO arqueo) {
        TotalesArqueoDTO totales = new TotalesArqueoDTO();
        
        // Total combustible
        float totalComb = 0;
        for (VentaCombustibleDTO venta : arqueo.getVentasCombustible()) {
            totalComb += venta.getTotal();
        }
        totales.setTotalCombustible(totalComb);
        
        // Total canastilla
        float totalCan = 0;
        for (VentaProductoDTO venta : arqueo.getVentasCanastilla()) {
            totalCan += venta.getTotal();
        }
        totales.setTotalCanastilla(totalCan);
        
        // Total market
        float totalMkt = 0;
        for (VentaProductoDTO venta : arqueo.getVentasMarket()) {
            totalMkt += venta.getTotal();
        }
        totales.setTotalMarket(totalMkt);
        
        // Total complementarios
        float totalComp = 0;
        for (VentaProductoDTO venta : arqueo.getVentasComplementarios()) {
            totalComp += venta.getTotal();
        }
        totales.setTotalComplementarios(totalComp);
        
        // Sobres
        if (data.has("data") && data.get("data").getAsJsonObject().has("sobres")) {
            JsonObject sobres = data.get("data").getAsJsonObject().get("sobres").getAsJsonObject();
            if (!sobres.get("total").isJsonNull()) {
                totales.setTotalSobres(sobres.get("total").getAsFloat());
            }
        }
        
        // Calibraciones
        if (data.has("data") && data.get("data").getAsJsonObject().has("calibracion")) {
            JsonObject calibracion = data.get("data").getAsJsonObject().get("calibracion").getAsJsonObject();
            if (calibracion.get("cantidad").getAsInt() > 0 && !calibracion.get("total").isJsonNull()) {
                totales.setTotalCalibraciones(calibracion.get("total").getAsFloat());
            }
        }
        
        arqueo.setTotales(totales);
    }
}

