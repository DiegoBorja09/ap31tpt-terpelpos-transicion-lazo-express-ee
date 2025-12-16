/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.commons.mappers;

import com.WT2.commons.domain.adapters.IMapper;
import com.WT2.appTerpel.commons.dto.SalesDto;
import com.WT2.appTerpel.domain.entities.Sales;

/**
 *
 * @author USUARIO
 */
public class SalesMapper implements IMapper<SalesDto, Sales>{

    private SalesAtributeMapper salesAtrMapper = new SalesAtributeMapper();
    
    @Override
    public Sales mapTo(SalesDto input) {
        
        Sales sales = new Sales();
        sales.setCantidad(input.getCantidad());
        sales.setCara(input.getCara());
        sales.setConsecutivo(input.getConsecutivo());
        sales.setFecha(input.getFecha());
        sales.setIdentificacionProducto(input.getIdentificacionProducto());
        sales.setIdentificacionPromotor(input.getIdentificacionPromotor());
        sales.setManguera(input.getManguera());
        sales.setNumero(input.getNumero());
        sales.setOperador(input.getOperador());
        sales.setPrecio(input.getPrecio());
        sales.setProceso(input.getProceso());
        sales.setProducto(input.getProducto());
        sales.setTipo(input.getTipo());
        sales.setTotal(input.getTotal());
        sales.setUnidadMedida(input.getUnidadMedida());
        sales.setAtributos(salesAtrMapper.mapTo(input.getSalesAtributesDto()));
        sales.setStatus(input.getStatus());
        sales.setOperador(input.getOperador());
        return sales;

    }
    
    
    
}
