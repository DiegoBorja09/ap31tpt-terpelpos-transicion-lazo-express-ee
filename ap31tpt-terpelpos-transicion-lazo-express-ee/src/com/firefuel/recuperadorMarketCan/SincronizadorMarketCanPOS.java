package com.firefuel.recuperadorMarketCan;

import com.application.useCases.jornada.GetJornadaActualUseCase;
import com.application.useCases.movimientos.ExisteMovimientoCoreUseCase;
import com.application.useCases.movimientos.GetInfoMovimientoGeneradaUseCase;
import com.application.useCases.movimientos.GetInfoMovimientoTransmisionUseCase;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import java.util.TimerTask;
import com.firefuel.Main;
import com.firefuel.recuperadorMarketCan.dto.Movimiento;
import com.firefuel.recuperadorMarketCan.dto.TipoMovimiento;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.TreeMap;

public class SincronizadorMarketCanPOS extends TimerTask {

    TreeMap<Long, Movimiento> movimientosRegistry = new TreeMap<>();
    boolean existeRegistro = false;

    @Override
    public void run() {
        //NovusUtils.printLn("************************   "+ "Buscando ventas Sincronizar MARKET - CAN    " + "***************************");
        long jornada = new GetJornadaActualUseCase().execute();
        if (jornada != 0) {
            //NovusUtils.printLn("** Jornada " + jornada + " **");
            movimientosRegistry = Main.sincronizarMarketCanDAO.getIdsMovimientosRegistry(jornada);
            if (!movimientosRegistry.isEmpty()) {
                NovusUtils.printLn("** ventas realizadas" + movimientosRegistry.size() + "**");
                for (Map.Entry<Long, Movimiento> movimiento : movimientosRegistry.entrySet()) {
                    existeRegistro = new ExisteMovimientoCoreUseCase(movimiento.getKey()).execute();
                    if (!existeRegistro) {
                        NovusUtils.printLn("el movimiento " + movimiento.getKey() + " no se encuentra en ct_movimientos");
                        sincronizarVenta(movimiento.getValue());
                    }
                }
            }
        } else {
           // NovusUtils.printLn("** No Hay Jornadas Activas **");
        }

    }

    public void sincronizarVenta(Movimiento movimiento) {
        long idTransmision = 0;
        JsonObject infoMovimiento = new GetInfoMovimientoTransmisionUseCase(movimiento.getConsecutivoPrefijo(),
                movimiento.getConsecutivo()).execute();
        TipoMovimiento tipoMovimiento = getTipoMovimiento(movimiento.getOperacion());
        JsonObject respuesta;
        if (infoMovimiento.has("transaccion")) {
            respuesta = insertarMovimientoCore(infoMovimiento, tipoMovimiento);
            idTransmision = infoMovimiento.get("id_transmision").getAsLong();
        } else {
            JsonObject infoMovimientoGenerada = new GetInfoMovimientoGeneradaUseCase(movimiento.getId()).execute();
            NovusUtils.printLn("Info Transaccion: \n" + infoMovimientoGenerada);
            respuesta = insertarMovimientoCore(infoMovimiento, tipoMovimiento);
        }
        procesarRespuesta(respuesta, idTransmision);
    }

    public TipoMovimiento getTipoMovimiento(int Operacion) {
        TipoMovimiento tipoMovimiento = new TipoMovimiento();
        switch (Operacion) {
            case 35:
                tipoMovimiento.setTipo(NovusConstante.TIPO_VENTA_KCO);
                tipoMovimiento.setEstadoMovimiento(NovusConstante.ESTADO_MOVIMIENTO_KCO);
                break;
            case 9:
                tipoMovimiento.setTipo(NovusConstante.TIPO_VENTA_CAN);
                tipoMovimiento.setEstadoMovimiento(NovusConstante.ESTADO_MOVIMIENTO_CAN);
                break;
            default:
                break;
        }
        return tipoMovimiento;
    }

    public JsonObject insertarMovimientoCore(JsonObject infoMovimiento, TipoMovimiento tipoMovimiento) {
        JsonObject respuesta = Main.mdao.procesarVentasKiosco(infoMovimiento, tipoMovimiento.getTipo(),
                tipoMovimiento.getEstadoMovimiento());
        return respuesta;
    }

    public void procesarRespuesta(JsonObject respuesta, long idTransmision) {
        NovusUtils.printLn("");
    }

}