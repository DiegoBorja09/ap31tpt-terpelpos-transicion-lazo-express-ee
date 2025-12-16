/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.ImpresonesFE.application.useCases;

import com.WT2.Containers.Dependency.SingletonMedioPago;
import com.WT2.ImpresonesFE.domain.entities.ParametrosPeticionFePrinter;
import com.WT2.ImpresonesFE.domain.entities.PeticionFeImprimir;
import com.WT2.ImpresonesFE.domain.valueObject.ListaConsurrente;
import com.WT2.commons.domain.adapters.IUseCase;
import com.controllers.NovusUtils;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author USUARIO
 */
public class ValidaPeticionFeEncolada implements IUseCase<PeticionFeImprimir, Boolean> {

    @Override
    public Boolean execute(PeticionFeImprimir input) {

        CopyOnWriteArrayList<ParametrosPeticionFePrinter> listaConsurrente = ListaConsurrente.getListaPeticionesImprimir();
        for (ParametrosPeticionFePrinter arametrosPeticionFePrinter : listaConsurrente) {

            if (input.getIdentificadorMovimiento() == arametrosPeticionFePrinter.getPeticionFeImprimir().getIdentificadorMovimiento()) {
                NovusUtils.printLn(input.getIdentificadorMovimiento()+"ESTA EN  LA LISTA ");
                return true;
            }

        }
        return false;
    }

}
