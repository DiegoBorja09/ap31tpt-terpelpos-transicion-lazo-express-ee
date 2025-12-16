package com.firefuel.asignarCliente.useCase;

import com.WT2.loyalty.presentation.handler.AcomulacionPuntosHandler;
import com.application.useCases.ventas.SetReaperturaEnUnoUseCase;
import com.bean.ReciboExtended;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.facade.ConfigurationFacade;
import com.facade.FidelizacionFacade;
import com.firefuel.FidelizacionCliente;
import com.firefuel.Main;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FidelizarVentaCliente {

    public static void fidelizar(ReciboExtended saleFacture, String numeroIdentificacion, String tipoIdentificacion) {
        AcomulacionPuntosHandler acumulacionPuntosHandler = new AcomulacionPuntosHandler();
        Map<String, Object> paramsAcumularRaw = FidelizacionFacade.buildClientAcumulationRequestObject(saleFacture, numeroIdentificacion, tipoIdentificacion, ConfigurationFacade.fetchSalePointIdentificator());


        Runnable runnableFidelizacion = () -> {
            acumulacionPuntosHandler.execute(paramsAcumularRaw);
            try {
                if (NovusConstante.HAY_INTERNET) {
                    //Main.mdao.setReaperturaInOne(saleFacture.numero);
                    new SetReaperturaEnUnoUseCase(saleFacture.numero).execute();
                }
            } catch (RuntimeException ex) {
                Logger.getLogger(FidelizacionCliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        };

        CompletableFuture.runAsync(runnableFidelizacion);

        NovusUtils.ventaFidelizadaAudio();
    }

}
