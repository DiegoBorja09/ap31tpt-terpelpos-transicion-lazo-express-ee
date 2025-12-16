/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.application.UseCase;

import com.WT2.commons.domain.adapters.IRepository;
import com.WT2.commons.domain.adapters.IUseCase;
import com.WT2.commons.domain.entity.TransaccionProcesParams;
import com.WT2.commons.domain.entity.TransaccionProceso;
import com.WT2.commons.domain.valueObject.ProcessStatus;

/**
 *
 * @author USUARIO
 */
public class EvaluarIntentosPagosAppterpel implements IUseCase<Long, Boolean> {

    //MODIFICASTE CASOS DE FIDELIZACION PARA PODER USAR ESTE REPOSITORY
    private IRepository< Long, Integer> validarIntentosRepo;

    public EvaluarIntentosPagosAppterpel(IRepository<Long, Integer> validarIntentosRepo) {
        this.validarIntentosRepo = validarIntentosRepo;
    }

    @Override
    public Boolean execute(Long input) {
       

        int  estadoIntento = validarIntentosRepo.getData(input);
        System.out.println("Evaluando estado Reintento de medio de pago appterpel: "+estadoIntento);
        return estadoIntento == 1;

    }

}
