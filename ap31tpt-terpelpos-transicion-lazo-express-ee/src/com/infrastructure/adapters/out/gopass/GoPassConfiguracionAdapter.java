package com.infrastructure.adapters.out.gopass;

import com.application.ports.out.gopass.GoPassConfiguracionPort;
import com.WT2.Containers.Dependency.SingletonMedioPago;
import com.WT2.goPass.domain.entity.beans.GopassParameters;
import com.controllers.NovusConstante;
import com.firefuel.Main;

import java.util.logging.Logger;

/**
 * ✅ ARQUITECTURA HEXAGONAL - Adaptador de Salida
 * Adaptador para obtener la configuración de GoPass
 * Implementa el puerto GoPassConfiguracionPort
 */
public class GoPassConfiguracionAdapter implements GoPassConfiguracionPort {

    private static final Logger LOGGER = Logger.getLogger(GoPassConfiguracionAdapter.class.getName());
    
    @Override
    public ConfiguracionGoPass obtenerConfiguracion() {
        LOGGER.info("✅ [ADAPTER] Obteniendo configuración de GoPass");
        
        // Obtener parámetros desde el singleton existente
        GopassParameters params = SingletonMedioPago.ConetextDependecy
            .getRecuperarParametrosGopass()
            .execute(null);
        
        // Obtener código de establecimiento
        String codigoEstablecimiento = Main.getParametroCore(
            NovusConstante.PREFERENCE_CODIGO_GOPASS, 
            true
        );
        
        // Mapear a objeto de dominio
        return new ConfiguracionGoPass(
            params.getConfiguracionTokenCantidadReintentos(),
            params.getConfiguracionTokenTiempoMuerto(),
            params.getConfiguracionPagoTiempoReintentos(),
            params.getConfiguracionConsultaPlacaCantidadReintentos(),
            params.getConfiguracionConsultaPlacaTiempoMuerto(),
            params.getConfiguracionConsultaPlacaTiempoReintentos(),
            params.getConfiguracionPagoCantidadReintentos(),
            codigoEstablecimiento
        );
    }
}

