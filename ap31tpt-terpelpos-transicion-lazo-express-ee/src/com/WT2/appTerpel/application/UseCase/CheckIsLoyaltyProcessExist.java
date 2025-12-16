/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.application.UseCase;

import com.WT2.commons.domain.adapters.IRepository;
import com.WT2.commons.domain.adapters.IUseCase;
import com.WT2.commons.domain.entity.TransaccionProcesParams;
import com.WT2.commons.domain.entity.TransaccionProceso;

/**
 *
 * @author USUARIO
 */
public class CheckIsLoyaltyProcessExist implements IUseCase<TransaccionProcesParams, Boolean> {

    private IRepository< TransaccionProcesParams, TransaccionProceso> repositoryTransaccionesProceso;

    public CheckIsLoyaltyProcessExist(IRepository<TransaccionProcesParams, TransaccionProceso> iRepositoryTransaccionesProceso) {
        this.repositoryTransaccionesProceso = iRepositoryTransaccionesProceso;
    }

    @Override
    public Boolean execute(TransaccionProcesParams input) {

        TransaccionProceso trp = repositoryTransaccionesProceso.getData(input);
        if (trp.getIdEstadoIntegracion() == 7 ) {
            return true;
        }
        
        
        return trp.getIdTrasccion() != 0 && trp.getIdEstadoIntegracion() == 6 && (trp.getIdEstadoProceso() == 2 ||  trp.getIdEstadoProceso() == 3) ;

    }

}
