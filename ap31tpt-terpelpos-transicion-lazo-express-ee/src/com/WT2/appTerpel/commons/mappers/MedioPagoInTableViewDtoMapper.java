/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.commons.mappers;

import com.WT2.commons.domain.adapters.IMapper;
import com.WT2.appTerpel.commons.dto.MedioPagoInTableViewDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author USUARIO
 */
public class MedioPagoInTableViewDtoMapper implements IMapper<Vector<Vector>, List<MedioPagoInTableViewDto>> {

    @Override
    public List<MedioPagoInTableViewDto> mapTo(Vector<Vector> input) {
        
        
        List<MedioPagoInTableViewDto> listaMedio = new ArrayList<>();
        for(Vector fila : input){
             if (fila != null) {
                MedioPagoInTableViewDto medioPagoInTableViewDto = new MedioPagoInTableViewDto();
                String medio = fila.get(0).toString();
                String voucher = fila.get(1).toString();
                String valor = fila.get(2).toString().replace("$", "").replace(".", "").trim();
                int value = Integer.valueOf(valor);

                medioPagoInTableViewDto.setMedio(medio);
                medioPagoInTableViewDto.setValor(value);
                medioPagoInTableViewDto.setVoucher(voucher);

               listaMedio.add(medioPagoInTableViewDto);
            }
        }
       

       
        return listaMedio;

    }

}
