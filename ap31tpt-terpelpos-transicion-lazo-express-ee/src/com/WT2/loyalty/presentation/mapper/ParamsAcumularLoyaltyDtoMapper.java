/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.loyalty.presentation.mapper;

import com.WT2.commons.domain.adapters.IBuilder;
import com.WT2.commons.domain.adapters.IMapper;
import com.WT2.loyalty.domain.entities.beans.IdentificationClient;
import com.WT2.loyalty.domain.entities.params.ParamsAcumularLoyalty;
import com.WT2.loyalty.presentation.builder.ParamsAcumularLoyaltyDtoCombustibleBuilder;
import com.WT2.loyalty.presentation.builder.ParamsAcumularLoyaltyDtoKioscoBuilder;
import com.WT2.loyalty.presentation.dto.ParamsAcumularLoyaltyDto;
import com.bean.MovimientosBean;
import com.google.gson.JsonObject;
import java.util.Map;

/**
 *
 * @author USUARIO
 */
public class ParamsAcumularLoyaltyDtoMapper implements IMapper<Map<String, Object>, ParamsAcumularLoyaltyDto> {

    IBuilder<Map,ParamsAcumularLoyaltyDto> paramsKioscoBuilder = new ParamsAcumularLoyaltyDtoKioscoBuilder();
    IBuilder<Map<String, Object>, ParamsAcumularLoyaltyDto> paramsCombustibleBuilder = new ParamsAcumularLoyaltyDtoCombustibleBuilder();
    
    @Override
    public ParamsAcumularLoyaltyDto mapTo(Map<String, Object> input) {

        if(input.get("movimientoId") != null){
            paramsCombustibleBuilder.setElementParamas(input);
            return paramsCombustibleBuilder.build();
        }else{
           paramsKioscoBuilder.setElementParamas(input);
           return paramsKioscoBuilder.build();
        }

    }

}
