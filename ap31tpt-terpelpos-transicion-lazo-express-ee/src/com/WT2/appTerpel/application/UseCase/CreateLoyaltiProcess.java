/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.application.UseCase;

import com.WT2.commons.domain.adapters.IRepository;
import com.WT2.commons.domain.adapters.IUseCase;
import com.WT2.commons.domain.entity.TransaccionProcesParams;
import com.WT2.commons.domain.entity.TransaccionProceso;
import com.WT2.loyalty.domain.entities.beans.ProcesosPagosFidelizacionParams;
import java.sql.Connection;
import java.util.Map;

/**
 *
 * @author USUARIO
 */
public class CreateLoyaltiProcess implements IUseCase<ProcesosPagosFidelizacionParams, Void>{
    
     public IRepository<ProcesosPagosFidelizacionParams,Void> CrearFidelizacionSinDatos;
     public IRepository< TransaccionProcesParams, TransaccionProceso> transpoceso;
     public IRepository< TransaccionProceso,Void > crearProceso;



    public CreateLoyaltiProcess(IRepository<ProcesosPagosFidelizacionParams,Void> CrearFidelizacionSinDatos,IRepository< TransaccionProcesParams, TransaccionProceso> transpoceso,IRepository< TransaccionProceso,Void > crearProceso) {
        this.crearProceso = crearProceso;
        this.CrearFidelizacionSinDatos = CrearFidelizacionSinDatos;
        this.transpoceso = transpoceso;
    }

    

    @Override
    public Void execute(ProcesosPagosFidelizacionParams procesosFidelizacion) {
       CrearFidelizacionSinDatos.getData(procesosFidelizacion);
       TransaccionProcesParams params = new TransaccionProcesParams();
       params.setIdMov(procesosFidelizacion.getIdMov());
       params.setIdintegracion(3L);
       TransaccionProceso transaccionProceso = this.transpoceso.getData(params);
       crearProceso.getData(transaccionProceso);
       
       
       return null;
    }
    
    
    
}
