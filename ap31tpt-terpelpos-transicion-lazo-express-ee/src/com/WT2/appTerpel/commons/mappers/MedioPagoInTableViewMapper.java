/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.commons.mappers;

import com.WT2.commons.domain.adapters.IMapper;
import com.WT2.appTerpel.commons.dto.MedioPagoInTableViewDto;
import com.WT2.appTerpel.domain.entities.MedioPagoInTableView;

/**
 *
 * @author USUARIO
 */
public class MedioPagoInTableViewMapper implements IMapper<MedioPagoInTableViewDto,MedioPagoInTableView>{

    @Override
    public MedioPagoInTableView mapTo(MedioPagoInTableViewDto input) {
        
        
        MedioPagoInTableView medioPagoInTableView = new MedioPagoInTableView();
        medioPagoInTableView.setMedio(input.getMedio());
        medioPagoInTableView.setValor(input.getValor());
        medioPagoInTableView.setVoucher(input.getVoucher());
        
        return  medioPagoInTableView;
    }
    
    
    
    
    
}
