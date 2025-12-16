/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.loyalty.presentation.mapper;

import com.WT2.commons.domain.adapters.IMapper;
import com.WT2.loyalty.domain.entities.params.ParamsConsultarCliente;
import com.controllers.NovusConstante;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author USUARIO
 */
public class ParamsConsultarClienteMapper implements IMapper<Map<String, String>, ParamsConsultarCliente> {

    private SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);

    @Override
    public ParamsConsultarCliente mapTo(Map<String, String> input) {

        ParamsConsultarCliente paramsConsultarCliente = new ParamsConsultarCliente();
        paramsConsultarCliente.setCodigoTipoIdentificacion(input.get("codigoTipoIdentificacion"));
        paramsConsultarCliente.setIdentifier(input.get("identifier"));
        paramsConsultarCliente.setFechaTransaccion(sdf.format(new Date()));
        paramsConsultarCliente.setTipoSitioVenta(input.get("identificacionPuntoVenta"));
        
         return paramsConsultarCliente;

    }

}
