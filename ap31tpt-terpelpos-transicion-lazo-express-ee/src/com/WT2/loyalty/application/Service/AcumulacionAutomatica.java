/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.loyalty.application.Service;

import com.WT2.loyalty.application.UseCase.ContingenciaFidelizacion;
import com.WT2.loyalty.domain.entities.request.ConsultClientRequestBody;
import com.facade.fidelizacion.AcumulacionFidelizacion;
import com.facade.fidelizacion.ConsultarCliente;

/**
 *
 * @author USUARIO
 */
public class AcumulacionAutomatica {

    private AcumulacionFidelizacion acumulacionFidelizacion;
    private ContingenciaFidelizacion contingenciaFidelizacion;
    private ConsultarCliente consultarCliente;

    public AcumulacionAutomatica(AcumulacionFidelizacion acumulacionFidelizacion, ContingenciaFidelizacion contingenciaFidelizacion, ConsultarCliente consultarCliente) {
        this.acumulacionFidelizacion = acumulacionFidelizacion;
        this.contingenciaFidelizacion = contingenciaFidelizacion;
        this.consultarCliente = consultarCliente;
    }

    public void execute(ConsultClientRequestBody consultClientRequestBody){
       
        
    }
    
}
