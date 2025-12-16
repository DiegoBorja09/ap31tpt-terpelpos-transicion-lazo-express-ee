/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.ImpresonesFE.application.service;

import com.WT2.ImpresonesFE.domain.entities.ParametrosPeticionFePrinter;
import com.WT2.ImpresonesFE.domain.entities.PeticionFeImprimir;
import com.WT2.ImpresonesFE.domain.entities.RespuestaFeImprimir;
import com.WT2.ImpresonesFE.domain.valueObject.ListaConsurrente;
import com.WT2.commons.domain.adapters.IUseCase;
import com.controllers.NovusUtils;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author USUARIO
 */
public class GestionarFeTirillaImpresion implements Runnable {

    private IUseCase<ParametrosPeticionFePrinter, RespuestaFeImprimir> enviarFePrinter;
    private IUseCase<ParametrosPeticionFePrinter, Boolean> eliminarDeCola;

    public GestionarFeTirillaImpresion(IUseCase<ParametrosPeticionFePrinter, RespuestaFeImprimir> enviarFePrinter, IUseCase<ParametrosPeticionFePrinter, Boolean> eliminarDeCola) {
        this.enviarFePrinter = enviarFePrinter;
        this.eliminarDeCola = eliminarDeCola;
    }

    public void execute() throws Exception {

        CopyOnWriteArrayList<ParametrosPeticionFePrinter> listaConcuerrente = ListaConsurrente.getListaPeticionesImprimir();

        if (listaConcuerrente.size() > 0) {
            Iterator<ParametrosPeticionFePrinter> iterador = listaConcuerrente.iterator();
            while (iterador.hasNext()) {

                NovusUtils.printLn("TAMAÑO ACTUAL DE LAS PETICIONES AL INICIO " + listaConcuerrente.size());
                ParametrosPeticionFePrinter peticionFeImprimir = iterador.next();
                if (!peticionFeImprimir.isProcesada()) {
                    RespuestaFeImprimir respuesta = enviarFePrinter.execute(peticionFeImprimir);
                    NovusUtils.printLn("Respuesta del servicio de impersion atraves de lazoExpres " + respuesta.getMensaje());
                    
                } 
                eliminarDeCola.execute(peticionFeImprimir);
                NovusUtils.printLn("TAMAÑO ACTUAL DE LAS PETICIONES DESPUES " + listaConcuerrente.size());

            }

        }
    }

    @Override
    public void run() {
        try {
            this.execute();
        } catch (Exception ex) {
            Logger.getLogger(GestionarFeTirillaImpresion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
