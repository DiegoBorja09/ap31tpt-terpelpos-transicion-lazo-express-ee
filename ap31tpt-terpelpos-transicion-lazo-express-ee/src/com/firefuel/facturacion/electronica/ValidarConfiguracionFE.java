/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.firefuel.facturacion.electronica;

import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.FacturacionElectronicaDao;
import static com.firefuel.Main.getParametroIntCore;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Usuario
 */
public class ValidarConfiguracionFE extends TimerTask {

    Timer tareas = new Timer();
    FacturacionElectronica factura = new FacturacionElectronica();
    FacturacionElectronicaDao facturacionElectronicaDao = new FacturacionElectronicaDao();
    int tareasIniciadasFe = 0;
    int tareasIniciadasRemision = 0;

    @Override
    public void run() {
        activarReenvioRemision(factura.remisionActiva());
        activarReenviofe(factura.aplicaFE());
        consultarPordefecto();
    }

    private void consultarPordefecto() {
        NovusConstante.IS_DEFAULT_FE = facturacionElectronicaDao.isDefaultFe();
    }

    private void activarReenviofe(boolean isFE) {
        if (!isFE) {
            if (tareasIniciadasFe > 0) {
                tareas.cancel();
                tareas.purge();
                tareasIniciadasFe = 0;
                tareas = new Timer();
                NovusUtils.printLn("tarea cerrada para facturación electrónica numero de tareas -> " + tareasIniciadasFe);
            }
        } else {
            if (tareasIniciadasFe == 0) {
                int tiempo = getParametroIntCore("TIEMPO_REENVIO_FE", true);
                if (tiempo <= 30000) {
                    tiempo = 30000;
                }
                tareasIniciadasFe = 1;
                tareas.schedule(new ReenviodeFE(), tiempo, tiempo);
                NovusUtils.printLn("tarea creada para facturación electrónica numero de tareas -> " + tareasIniciadasFe);
            }
        }

    }

    private void activarReenvioRemision(boolean isRemision) {
        if (!isRemision) {
            if (tareasIniciadasRemision > 0) {
                tareas.cancel();
                tareas.purge();
                tareas = new Timer();
                tareasIniciadasRemision = 0;
                NovusUtils.printLn("tarea cerrada para remisión electrónica numero de tareas -> " + tareasIniciadasRemision);
            }
        } else {
            if (tareasIniciadasRemision == 0) {
                int tiempo = getParametroIntCore("TIEMPO_REENVIO_FE", true);
                if (tiempo <= 30000) {
                    tiempo = 30000;
                }
                tareasIniciadasRemision = 1;
                tareas.schedule(new AsignacionclienteRemision(), tiempo, tiempo);
                NovusUtils.printLn("tarea creada para remisión electrónica numero de tareas -> " + tareasIniciadasRemision);
            }
        }
    }

}
