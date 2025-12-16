/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.ImpresonesFE.domain.valueObject;

import com.WT2.ImpresonesFE.domain.entities.ParametrosPeticionFePrinter;
import com.WT2.ImpresonesFE.domain.entities.PeticionFeImprimir;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author USUARIO
 */
public class ListaConsurrente {

    private ListaConsurrente() {
    }

    private static CopyOnWriteArrayList<ParametrosPeticionFePrinter> listaConcurrente;

    public static CopyOnWriteArrayList<ParametrosPeticionFePrinter> getListaPeticionesImprimir() {
        if (listaConcurrente == null) {
            listaConcurrente = new CopyOnWriteArrayList<>();
        }
        return listaConcurrente;
    }


}
