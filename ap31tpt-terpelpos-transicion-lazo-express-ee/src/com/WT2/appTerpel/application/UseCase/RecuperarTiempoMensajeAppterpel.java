/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.application.UseCase;

import com.WT2.Containers.Dependency.DependencyInjection;
import com.WT2.appTerpel.domain.entities.AppterpelParametrosPos;
import com.WT2.appTerpel.domain.valueObject.AppterpelWatcherParametros;
import com.WT2.commons.domain.adapters.IRepository;
import com.WT2.commons.domain.adapters.IUseCase;
import com.WT2.commons.domain.entity.ValoresParametros;
import com.WT2.commons.domain.entity.WatcherParameter;
import com.controllers.NovusUtils;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author USUARIO
 */
public class RecuperarTiempoMensajeAppterpel implements IUseCase<Void, Integer> {

    private  IRepository<String, ValoresParametros> parametroParmetizadoRepo;

    public RecuperarTiempoMensajeAppterpel( IRepository<String, ValoresParametros> watcherParameterRepo) {
        this.parametroParmetizadoRepo = watcherParameterRepo;
    }

    @Override
    public Integer execute(Void input) {
        int tiempo = 30;
        ValoresParametros valor = parametroParmetizadoRepo.getData(AppterpelWatcherParametros.TIEMPO_DE_MENSAJE);
        if (!valor.getDescrtipcion().equals(AppterpelWatcherParametros.PARAMETRO_INVALIDO)) {
            try {
                tiempo = Integer.valueOf(valor.getValor());
            } catch (NumberFormatException ex) {
                tiempo = 30;
                Logger.getLogger(RecuperarTiempoMensajeAppterpel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        NovusUtils.printLn("El tiempo del mensaje appterpel es de "+ tiempo);
        return tiempo;

    }

}
