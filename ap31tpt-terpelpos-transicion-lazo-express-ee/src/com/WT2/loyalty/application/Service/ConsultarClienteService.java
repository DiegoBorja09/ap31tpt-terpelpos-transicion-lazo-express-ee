/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.loyalty.application.Service;

import com.WT2.commons.application.UseCase.RecuperarParameterFidelizacion;
import com.WT2.loyalty.application.UseCase.ConsultarClienteEndpointTerpel;
import com.WT2.loyalty.domain.entities.request.ConsultClientRequestBody;
import com.WT2.loyalty.domain.entities.beans.TransactionData;
import com.WT2.loyalty.domain.entities.beans.FoundClient;
import com.WT2.loyalty.domain.entities.beans.IdentificationClient;
import com.WT2.loyalty.domain.entities.beans.LoyalityConfig;
import com.WT2.loyalty.domain.entities.params.ParamsConsultarCliente;

/**
 *
 * @author USUARIO
 */
public class ConsultarClienteService {

    private ConsultarClienteEndpointTerpel clienteEndpointTerpel;
    private RecuperarParameterFidelizacion recuperarParameterFidelizacion;

    public ConsultarClienteService(ConsultarClienteEndpointTerpel clienteEndpointTerpel, RecuperarParameterFidelizacion recuperarParameterFidelizacion) {
        this.clienteEndpointTerpel = clienteEndpointTerpel;
        this.recuperarParameterFidelizacion = recuperarParameterFidelizacion;
    }

    public FoundClient execute(ParamsConsultarCliente input) {

        LoyalityConfig loyalityConfig = this.recuperarParameterFidelizacion.execute("FIDELIZACION");
        IdentificationClient identificationClient = new IdentificationClient();
        TransactionData customer = new TransactionData();
        
        identificationClient.setCodigoTipoIdentificacion(input.getCodigoTipoIdentificacion());
        identificationClient.setNumeroIdentificacion(input.getIdentifier());

        ConsultClientRequestBody consultClientRequestBody = new ConsultClientRequestBody();
        customer.setOrigenVenta(input.getTipoSitioVenta());
        customer.setIdentificacionPuntoVenta(loyalityConfig.getIdentificacionPuntoVenta());
        customer.setFechaTransaccion(input.getFechaTransaccion());
        consultClientRequestBody.setIdIntegracion(1);
        consultClientRequestBody.setCustomer(identificationClient);
        consultClientRequestBody.setTransactionData(customer);
        
        FoundClient resultado = this.clienteEndpointTerpel.execute(consultClientRequestBody);
        
        // INTERCEPTAR SOLO SI LA VENTA ES GLP
        // Nota: La validación GLP se hace en FidelizacionyFacturacionElectronica
        // donde tenemos acceso al ReciboExtended con la información de la cara
        
        return resultado;
    }

}
