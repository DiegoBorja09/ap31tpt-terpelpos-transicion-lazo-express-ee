/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.goPass.application.useCase;

import com.WT2.commons.domain.adapters.IUseCase;
import com.WT2.goPass.application.mapper.GopassParameterMapper;
import com.WT2.goPass.domain.entity.beans.GopassParameters;
import com.WT2.goPass.domain.entity.dto.GopassParametersDto;
import com.WT2.goPass.infrastructure.db.repositories.GopassParametersRepository;
import com.WT2.goPass.infrastructure.services.GopassParametersService;
import java.util.LinkedList;

/**
 *
 * @author Jeison
 */
public class ObtenerParametrosGopass implements IUseCase<Void, GopassParameters>{

    private GopassParameterMapper gopassMapper;
    private GopassParametersRepository gopassParametersRepo;
    private GopassParametersService gopassParametersService;

    public ObtenerParametrosGopass(GopassParametersRepository gopassParametersRepo, GopassParametersService gopassParametersService) {
        this.gopassParametersRepo = gopassParametersRepo;
        this.gopassParametersService = gopassParametersService;
        this.gopassMapper = new GopassParameterMapper();
    }
    
  
    
    
    @Override
    public GopassParameters execute(Void input) {
        LinkedList<GopassParametersDto> gopassParamsDto = gopassParametersService.execute(gopassParametersRepo.getData(null));
        return gopassMapper.mapTo(gopassParamsDto);
        
    }
    
}
