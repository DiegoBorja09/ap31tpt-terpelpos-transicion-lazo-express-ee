/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.commons.mappers;

import com.WT2.commons.domain.adapters.IMapper;
import com.WT2.appTerpel.commons.dto.ConsecutiveAtributeDto;
import com.WT2.appTerpel.domain.entities.ConsecutiveAtribute;

/**
 *
 * @author USUARIO
 */
public class ConsecutiveAtributesMapper implements IMapper<ConsecutiveAtributeDto,ConsecutiveAtribute >{

    @Override
    public ConsecutiveAtribute mapTo(ConsecutiveAtributeDto input) {
        ConsecutiveAtribute consecutiveAtribute = new ConsecutiveAtribute();
        consecutiveAtribute.setAlertaConsecutivo(input.getAlertaConsecutivo());
        consecutiveAtribute.setAlertaDia(input.getAlertaDia());
        consecutiveAtribute.setAutoretenedor1(input.isAutoretenedor1());
        consecutiveAtribute.setAutoretenedor2(consecutiveAtribute.isAutoretenedor2());
        consecutiveAtribute.setAutoretenedor3(consecutiveAtribute.isAutoretenedor3());
        return consecutiveAtribute;
    }
    
}
