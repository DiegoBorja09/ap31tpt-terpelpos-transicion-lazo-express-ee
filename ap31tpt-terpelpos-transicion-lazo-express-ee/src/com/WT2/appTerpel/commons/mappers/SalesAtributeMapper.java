/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.commons.mappers;

import com.WT2.commons.domain.adapters.IMapper;
import com.WT2.appTerpel.commons.dto.SalesAtributesDto;
import com.WT2.appTerpel.domain.entities.SalesAtributs;

/**
 *
 * @author USUARIO
 */
public class SalesAtributeMapper implements IMapper<SalesAtributesDto, SalesAtributs>{

    private ConsecutiveMapper consecutiveMapper = new ConsecutiveMapper();
    private SalesAtributeExtraDataMapper salesAtributeExtraDataMapper = new SalesAtributeExtraDataMapper();
    @Override
    public SalesAtributs mapTo(SalesAtributesDto input) {
        
       SalesAtributs salesAtributs = new SalesAtributs();
      
       salesAtributs.setIsCredito(input.getIsCredito());
       salesAtributs.setIsElectronica(input.getIsCredito());
       salesAtributs.setOrden(input.getOrden());
       salesAtributs.setRecuperada(input.getRecuperada());
       salesAtributs.setTipoVenta(input.getTipoVenta());
       salesAtributs.setVehiculo_numero(input.getVehiculo_numero());
       salesAtributs.setVehiculo_odometro(input.getVehiculo_odometro());
       salesAtributs.setVehiculo_placa(input.getVehiculo_placa());
       salesAtributs.setVoucher(input.getVoucher());
       salesAtributs.setConsecutive(consecutiveMapper.mapTo(input.getConsecutiveDto()));
       salesAtributs.setExtraData(salesAtributeExtraDataMapper.mapTo(input.getSalesAtributeExtraDataDto()));
       return salesAtributs;
    }
    
}
