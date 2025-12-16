/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.ImpresonesFE.application.service;

import com.WT2.ImpresonesFE.domain.entities.ParametrosPeticionFePrinter;
import com.WT2.ImpresonesFE.domain.entities.PeticionFeImprimir;
import com.WT2.ImpresonesFE.domain.valueObject.ListaConsurrente;
import com.WT2.commons.domain.adapters.IUseCase;
import com.controllers.NovusUtils;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author USUARIO
 */
public class EncolarPeticionFeImprimir implements IUseCase<ParametrosPeticionFePrinter, Boolean> {
    
    private IUseCase<PeticionFeImprimir, Boolean> validaEncolada;

    public EncolarPeticionFeImprimir(IUseCase<PeticionFeImprimir, Boolean> validaEncolada) {
        this.validaEncolada = validaEncolada;
    }



    @Override
    public Boolean execute(ParametrosPeticionFePrinter input) {
        CopyOnWriteArrayList<ParametrosPeticionFePrinter> listaPeticiones = ListaConsurrente.getListaPeticionesImprimir();

        boolean noContiene = !this.validaEncolada.execute(input.getPeticionFeImprimir());
        if (noContiene) {

            NovusUtils.printLn("ENCOLANDO PETICION IMPRESION FE CON IDMOVIMIENTO" + input.getPeticionFeImprimir().getIdentificadorMovimiento()+" VALOR "+  !listaPeticiones.contains(input));
            input.setTimeOut(5000);
            input.setInitialTime(System.currentTimeMillis());

            listaPeticiones.add(input);

        } else {
            NovusUtils.printLn("YA ESTA EN PROCESO PETICION IMPRESION FE CON IDMOVIMIENTO" + input.getPeticionFeImprimir().getIdentificadorMovimiento()+" VALOR "+  !listaPeticiones.contains(input));

        }

        NovusUtils.printLn("Tamaño de la cola " + listaPeticiones.size());

        return noContiene;
    }


}
