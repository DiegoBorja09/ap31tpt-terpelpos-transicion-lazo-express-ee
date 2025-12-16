/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.glp.application.UseCase;

import com.WT2.commons.domain.adapters.IRepository;
import com.WT2.commons.domain.adapters.IUseCase;
import com.WT2.commons.domain.entity.Parameter;
import com.controllers.NovusConstante;

/**
 *
 * @author USUARIO
 */
public class ValidaterUrlSicom implements IUseCase<Void, String> {

    IRepository<String, Parameter> parameterRepository;

    public ValidaterUrlSicom(IRepository<String, Parameter> parameterRepository) {
        this.parameterRepository = parameterRepository;
    }

    @Override
    public String execute(Void input) {
        Parameter parameter = parameterRepository.getData(NovusConstante.PARAMETER_URL_SICOM);
        return parameter.getValor();
    }

}
