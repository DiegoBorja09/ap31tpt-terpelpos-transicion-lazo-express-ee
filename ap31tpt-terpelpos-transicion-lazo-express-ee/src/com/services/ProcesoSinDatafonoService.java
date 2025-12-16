package com.services;

import com.bean.MovimientosBean;
import com.controllers.NovusUtils;
import com.dao.MovimientosDao;
import com.application.useCases.movimientocliente.ObtenerIdTransmisionDesdeMovimientoUseCase;

public class ProcesoSinDatafonoService {

    ObtenerIdTransmisionDesdeMovimientoUseCase obtenerIdTransmisionDesdeMovimientoUseCase;
    private final MovimientosDao movimientosDao;
    MovimientosBean movimientosBean;

    public ProcesoSinDatafonoService(

    ) {
        this.obtenerIdTransmisionDesdeMovimientoUseCase = new ObtenerIdTransmisionDesdeMovimientoUseCase(0L);

        this.movimientosDao = new MovimientosDao();
        this.movimientosBean = new MovimientosBean();
    }

    public long ejecutarProcesoAnulacion(Long idMovimiento) {
        obtenerIdTransmisionDesdeMovimientoUseCase.setIdMovimiento(idMovimiento);
        return obtenerIdTransmisionDesdeMovimientoUseCase.execute();
    }

    public long obtenerIdTransmisionSeguro() {
        if (this.movimientosBean != null && this.movimientosBean.getAtributos() != null && this.movimientosBean.getAtributos().has("idTransmision") && !this.movimientosBean.getAtributos().get("idTransmision").isJsonNull()) {
            return this.movimientosBean.getAtributos().get("idTransmision").getAsLong();
        } else {
            System.err.println("⚠ 'idTransmision' no disponible en movimiento o nulo");
            return 0L;
        }
    }

    public long getIdTransmisionRobusto() {
        long id = obtenerIdTransmisionSeguro();
        if (id == 0L) {
            obtenerIdTransmisionDesdeMovimientoUseCase.setIdMovimiento(this.movimientosBean.getId());
            id = obtenerIdTransmisionDesdeMovimientoUseCase.execute();
            NovusUtils.printLn("🔁 getIdTransmisionRobusto(): Fallback obtenido desde DAO: " + id);
        }
        return id;
    }

    public long ejecutarProcesoRegular() {
        return getIdTransmisionRobusto();
    }


}
