package com.application.services;

import com.WT2.commons.domain.valueObject.LetterCase;
import com.WT2.goPass.domain.entity.beans.GopassParameters;
import com.WT2.goPass.domain.entity.beans.PaymentGopassParams;
import com.WT2.goPass.domain.valueObject.GoPassStateDictionary;
import com.WT2.goPass.infrastructure.controllers.ProcesarPagoGoPassController;
import com.WT2.payment.domian.entities.PaymentRequest;
import com.WT2.payment.domian.entities.PaymentResponse;
import com.bean.GopassResponse;
import com.bean.VentaGo;
import com.controllers.NovusUtils;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 * Servicio para manejar la lógica de pagos de GoPass
 */
public class GoPassPaymentService {
    
    private static final Logger logger = Logger.getLogger(GoPassPaymentService.class.getName());
    
    private final GopassParameters parametrosGopass;
    
    public interface UICallback {
        void updateUI(int state);
        void printInvoices(String idventa);
        void refreshSales();
    }
    
    private UICallback uiCallback;
    
    public GoPassPaymentService(GopassParameters parametrosGopass) {
        this.parametrosGopass = parametrosGopass;
    }
    
    public void setUICallback(UICallback callback) {
        this.uiCallback = callback;
    }

    public GopassResponse procesarPagoGoPass(VentaGo selectedVenta, Object selectedPlaca) {
        GopassResponse res = new GopassResponse(false);
        
        try {
            NovusUtils.printLn("CONSULTANDO A API PAGOS GOPASS");
            
            // Construir parámetros de pago
            PaymentRequest paymentRequest = buildPaymentRequest(selectedVenta);
            PaymentGopassParams paymentGopassParams = buildPaymentGopassParams(paymentRequest, selectedPlaca);
            
            // Procesar pago
            PaymentResponse response = processPayment(paymentGopassParams);
            
            // Procesar respuesta
            processPaymentResponse(response, res, selectedVenta);
            
        } catch (ExecutionException | InterruptedException e) {
            NovusUtils.printLn(e.getMessage());
        }
        
        return res;
    }
    
    private PaymentRequest buildPaymentRequest(VentaGo selectedVenta) {
        String idventa = "" + selectedVenta.getId();
        
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setIdentificadorMovimiento(Long.valueOf(idventa));
        paymentRequest.setMedioDescription("GOPASS");
        
        return paymentRequest;
    }
    
    private PaymentGopassParams buildPaymentGopassParams(PaymentRequest paymentRequest, Object selectedPlaca) {
        PaymentGopassParams paymentGopassParams = new PaymentGopassParams();
        paymentGopassParams.setIdentificadorVentaTerpel(paymentRequest);
        
        // Acceder a la placa usando reflexión para mantener compatibilidad
        try {
            String placa = (String) selectedPlaca.getClass().getMethod("getPlaca").invoke(selectedPlaca);
            paymentGopassParams.setPlaca(placa);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error al obtener placa: " + e.getMessage());
        }
        
        // Calcular timeout
        calculateAndSetTimeout(paymentGopassParams);
        
        return paymentGopassParams;
    }
    
    private void calculateAndSetTimeout(PaymentGopassParams paymentGopassParams) {
        int timeoutGopassConsult = ((parametrosGopass.getConfiguracionTokenCantidadReintentos() * 
            (parametrosGopass.getConfiguracionTokenTiempoMuerto() + parametrosGopass.getConfiguracionPagoTiempoReintentos())) + 
            (parametrosGopass.getConfiguracionPagoCantidadReintentos() * 
            (parametrosGopass.getConfiguracionPagoTiempoReintentos() + parametrosGopass.getConfiguracionConsultaPlacaTiempoReintentos())));
        
        timeoutGopassConsult = (timeoutGopassConsult + 5) * 1000;
        System.out.println("timeoutGopassConsult: " + timeoutGopassConsult);
        paymentGopassParams.setTimeOut((long) timeoutGopassConsult);
    }
    
    private PaymentResponse processPayment(PaymentGopassParams paymentGopassParams) throws ExecutionException, InterruptedException {
        ProcesarPagoGoPassController procesarPagoGoPassController = new ProcesarPagoGoPassController();
        return procesarPagoGoPassController.execute(paymentGopassParams);
    }
    
    private void processPaymentResponse(PaymentResponse response, GopassResponse res, VentaGo selectedVenta) {
        String mensaje = "Pago no pudo ser procesado, verifique conexión a internet";
        String estado = response.getEstadoPago();
        res.setError(true);
        
        switch (estado) {
            case GoPassStateDictionary.APROBADO:
                mensaje = handleApprovedPayment(response, res, selectedVenta);
                break;
            case "P":
                mensaje = handlePendingPayment(res);
                break;
            case GoPassStateDictionary.RECHAZADO:
                mensaje = handleRejectedPayment(response);
                break;
            case "X":
                mensaje = handleErrorPayment(response);
                break;
            default:
                mensaje = handleDefaultPayment(res);
                break;
        }
        
        res.setMensaje(mensaje);
        // Usar callbacks en lugar de llamadas directas al controlador
        notifyPaymentResult(res, mensaje);
    }
    
    private void notifyPaymentResult(GopassResponse res, String mensaje) {
        if (uiCallback != null) {
            final int finalState = res.isError() ? com.application.constants.GopassConstants.ERR_PAGO : com.application.constants.GopassConstants.SUCCESS_PAGO;
            SwingUtilities.invokeLater(() -> {
                uiCallback.updateUI(finalState);
            });
        }
    }

    private String handleApprovedPayment(PaymentResponse response, GopassResponse res, VentaGo selectedVenta) {
        String mensaje = "Pago aprobado";
        String idventa = "" + selectedVenta.getId();
        
        NovusUtils.printLn("########## ACTUALIZANDO ATRIBUTOS DE PAGO #######");
        res.setEstadoPago(response.getEstadoPago());
        res.setMensaje(mensaje);
        res.setError(false);
        
        if (uiCallback != null) {
            uiCallback.printInvoices(idventa);
            uiCallback.refreshSales();
        }
        
        return mensaje;
    }
    

    private String handlePendingPayment(GopassResponse res) {
        String mensaje = "Pago pendiente";
        res.setError(false);
        return mensaje;
    }

    private String handleRejectedPayment(PaymentResponse response) {
        return "Pago rechazado " + response.getMensaje().toLowerCase();
    }

    private String handleErrorPayment(PaymentResponse response) {
        return NovusUtils.convertMessage(
                LetterCase.FIRST_UPPER_CASE,
                response.getMensaje()
        );
    }

    private String handleDefaultPayment(GopassResponse res) {
        String mensaje = "Pago no procesado";
        res.setError(false);
        return mensaje;
    }
    
}

