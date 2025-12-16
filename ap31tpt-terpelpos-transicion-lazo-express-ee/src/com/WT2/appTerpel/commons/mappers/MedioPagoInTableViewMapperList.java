/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.commons.mappers;

import com.WT2.commons.domain.adapters.IMapper;
import com.WT2.Containers.Dependency.SingletonMedioPago;
import com.WT2.appTerpel.commons.dto.MedioPagoInTableViewDto;
import com.WT2.appTerpel.domain.entities.MedioPagoInTableView;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author USUARIO
 */
public class MedioPagoInTableViewMapperList implements IMapper<List<MedioPagoInTableViewDto>, List< MedioPagoInTableView>>{

    
    
    @Override
    public List<MedioPagoInTableView> mapTo(List<MedioPagoInTableViewDto> input) {
        
        List<MedioPagoInTableView> medioPagoInTableViews = new ArrayList<>();
        
        for(MedioPagoInTableViewDto dto : input){
            
            medioPagoInTableViews.add(SingletonMedioPago.ConetextDependecy.getMedioPagoInTableViewMapper().mapTo(dto));
        }
        return  medioPagoInTableViews;
    }
    
    
}
