/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.WT2.appTerpel.application.UseCase;

import com.WT2.appTerpel.domain.entities.VentasAppterpelBotonesValidador;
import com.WT2.commons.domain.adapters.IRepository;
import com.WT2.commons.domain.adapters.IUseCase;

/**
 *
 * @author Devitech
 */
public class ValidarBotonesVentasAppterpel implements IUseCase<Long, VentasAppterpelBotonesValidador> {

    private IRepository<Long, VentasAppterpelBotonesValidador> validadorIRepository;

    public ValidarBotonesVentasAppterpel(IRepository<Long, VentasAppterpelBotonesValidador> validadorIRepository) {
        this.validadorIRepository = validadorIRepository;
    }
    

    @Override
    public VentasAppterpelBotonesValidador execute(Long input) {
     
        return validadorIRepository.getData(input);

    }

}
