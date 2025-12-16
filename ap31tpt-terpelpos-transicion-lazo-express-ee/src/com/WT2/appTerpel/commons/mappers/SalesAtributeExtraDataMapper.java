/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.commons.mappers;

import com.WT2.commons.domain.adapters.IMapper;
import com.WT2.appTerpel.commons.dto.SalesAtributeExtraDataDto;
import com.WT2.appTerpel.domain.entities.SalesAtributeExtraData;

/**
 *
 * @author USUARIO
 */
public class SalesAtributeExtraDataMapper implements IMapper<SalesAtributeExtraDataDto, SalesAtributeExtraData>{

    @Override
    public SalesAtributeExtraData mapTo(SalesAtributeExtraDataDto input) {
      SalesAtributeExtraData salesAtributeExtraData =  new SalesAtributeExtraData();
      salesAtributeExtraData.setRecaudar(input.getRecaudar());
      salesAtributeExtraData.setVehiculo_numero(input.getVehiculo_numero());
      salesAtributeExtraData.setVehiculo_odometro(input.getVehiculo_odometro());
      return salesAtributeExtraData;
    }
    
}
