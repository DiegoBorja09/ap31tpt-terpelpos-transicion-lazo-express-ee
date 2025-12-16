package com.application.useCases.reportes;

import com.application.core.AbstractUseCase;
import com.bean.ResultBean;
import com.controllers.NovusUtils;
import com.domain.dto.reportes.ArqueoProcesadoDTO;
import com.infrastructure.adapters.PrintServiceAdapter;

/**
 * 🎯 Caso de uso para imprimir arqueo de promotor.
 * Integra con microservicio Python de impresión (o MOCK).
 * 
 * Responsabilidades:
 * - Recibir datos procesados del arqueo
 * - Delegar impresión al adaptador
 * - Retornar resultado de la operación
 * 
 * @author Clean Architecture
 * @version 1.0
 */
public class ImprimirArqueoPromotorUseCase extends AbstractUseCase<ResultBean> {
    
    private final ArqueoProcesadoDTO arqueoData;
    private final PrintServiceAdapter printAdapter;
    
    public ImprimirArqueoPromotorUseCase(ArqueoProcesadoDTO arqueoData) {
        this.arqueoData = arqueoData;
        this.printAdapter = new PrintServiceAdapter();
    }
    
    /**
     * Constructor con inyección de dependencias (para testing)
     */
    public ImprimirArqueoPromotorUseCase(ArqueoProcesadoDTO arqueoData, PrintServiceAdapter printAdapter) {
        this.arqueoData = arqueoData;
        this.printAdapter = printAdapter;
    }
    
    @Override
    public ResultBean run() {
        try {
            NovusUtils.printLn("🖨️ Iniciando impresión de arqueo promotor: " + arqueoData.getPromotor());
            NovusUtils.printLn("   Turno ID: " + arqueoData.getTurnoId());
            NovusUtils.printLn("   Fecha: " + arqueoData.getFechaInicio());
            
            // Validar datos
            if (arqueoData.getPromotor() == null || arqueoData.getPromotor().isEmpty()) {
                ResultBean errorResult = new ResultBean();
                errorResult.setSuccess(false);
                errorResult.setMessage("Error: Nombre de promotor no puede estar vacío");
                return errorResult;
            }
            
            // TurnoId es opcional (puede ser null para pruebas o reportes sin turno)
            if (arqueoData.getTurnoId() != null && arqueoData.getTurnoId() <= 0) {
                ResultBean errorResult = new ResultBean();
                errorResult.setSuccess(false);
                errorResult.setMessage("Error: ID de turno inválido");
                return errorResult;
            }
            
            // Llamar al adaptador de impresión
            ResultBean result = printAdapter.imprimirArqueoPromotor(arqueoData);
            
            if (result.isSuccess()) {
                NovusUtils.printLn("✅ Arqueo impreso exitosamente");
            } else {
                NovusUtils.printLn("❌ Error al imprimir arqueo: " + result.getMessage());
            }
            
            return result;
            
        } catch (Exception e) {
            NovusUtils.printLn("❌ Excepción al imprimir arqueo: " + e.getMessage());
            e.printStackTrace();
            
            ResultBean errorResult = new ResultBean();
            errorResult.setSuccess(false);
            errorResult.setMessage("Error inesperado al imprimir: " + e.getMessage());
            return errorResult;
        }
    }
}

