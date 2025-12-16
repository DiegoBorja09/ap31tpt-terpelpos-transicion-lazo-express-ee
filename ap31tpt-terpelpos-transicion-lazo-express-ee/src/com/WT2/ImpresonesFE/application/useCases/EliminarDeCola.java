/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.ImpresonesFE.application.useCases;

import com.WT2.ImpresonesFE.domain.entities.ParametrosPeticionFePrinter;
import com.WT2.ImpresonesFE.domain.valueObject.ListaConsurrente;
import com.WT2.commons.domain.adapters.IUseCase;

/**
 *
 * @author USUARIO
 */
public class EliminarDeCola implements IUseCase<ParametrosPeticionFePrinter, Boolean> {

    @Override
    public Boolean execute(ParametrosPeticionFePrinter input) {
        long difTime = System.currentTimeMillis() - input.getInitialTime();
        if ( difTime > input.getTimeOut()) {
            ListaConsurrente.getListaPeticionesImprimir().remove(input);
            return true;
        }
        return false;

    }

}
