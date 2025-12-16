/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.turns.application.useCase;

import com.WT2.commons.domain.adapters.IRepository;
import com.WT2.commons.domain.adapters.IUseCase;
import com.WT2.commons.domain.entity.WatcherParameter;
import com.controllers.NovusConstante;

/**
 *
 * @author USUARIO
 */
public class ValidateModeWithoutPump implements IUseCase<Void, Boolean> {

    IRepository<String, WatcherParameter> watcherRepository;

    public ValidateModeWithoutPump(IRepository<String, WatcherParameter> watcherRepository) {
        this.watcherRepository = watcherRepository;
    }

    @Override
    public Boolean execute(Void input) {

        WatcherParameter watcherParameter = watcherRepository.getData(NovusConstante.SIN_SURTIDOR);
        return watcherParameter.getValor().equals("S");

    }

}
