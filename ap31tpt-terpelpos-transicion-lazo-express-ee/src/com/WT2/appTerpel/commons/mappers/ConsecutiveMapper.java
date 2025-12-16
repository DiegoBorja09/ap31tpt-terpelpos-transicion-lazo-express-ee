/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.commons.mappers;

import com.WT2.commons.domain.adapters.IMapper;
import com.WT2.appTerpel.commons.dto.ConsecutiveDto;
import com.WT2.appTerpel.domain.entities.Consecutive;

/**
 *
 * @author USUARIO
 */
public class ConsecutiveMapper implements IMapper<ConsecutiveDto, Consecutive>{

    private ConsecutiveAtributesMapper consAtrMapper = new ConsecutiveAtributesMapper();
    
    @Override
    public Consecutive mapTo(ConsecutiveDto input) {
        Consecutive consecutive = new Consecutive();
        consecutive.setConsecutivoActual(input.getConsecutivoActual());
        consecutive.setConsecutivoFinal(input.getConsecutivoFinal());
        consecutive.setConsecutivoInicial(input.getConsecutivoInicial());
        consecutive.setEmpresaId(input.getEmpresaId());
        consecutive.setEquipoId(input.getEquipoId());
        consecutive.setEstado(input.getEstado());
        consecutive.setFecha_fin(input.getFecha_fin());
        consecutive.setFecha_inicio(input.getFecha_inicio());
        consecutive.setId(input.getId());
        consecutive.setObservaciones(input.getObservaciones());
        consecutive.setPrefijo(input.getPrefijo());
        consecutive.setResolucion(input.getResolucion());
        consecutive.setTipoDocumento(input.getTipoDocumento());
        consecutive.setConsecutiveAtribute(consAtrMapper.mapTo(input.getConsecutiveAtributeDto()));
        
        return consecutive;

    }
    
}
