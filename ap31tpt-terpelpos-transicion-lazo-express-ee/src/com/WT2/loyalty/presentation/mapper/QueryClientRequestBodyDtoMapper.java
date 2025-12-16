/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.loyalty.presentation.mapper;

import com.WT2.commons.domain.adapters.IMapper;
import com.WT2.loyalty.presentation.dto.ConsultClientRequestBodyDto;
import com.controllers.NovusConstante;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author USUARIO
 */
public class QueryClientRequestBodyDtoMapper implements IMapper<Map<String, String>, ConsultClientRequestBodyDto> {

    private SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);

    @Override
    public ConsultClientRequestBodyDto mapTo(Map<String, String> input) {

        ConsultClientRequestBodyDto consultClientRequestBodyDto = new ConsultClientRequestBodyDto();

        consultClientRequestBodyDto.setOrigenVenta("EDS");
        consultClientRequestBodyDto.setIdentificacionPuntoVenta(input.get("identificacionPuntoVenta"));
        consultClientRequestBodyDto.setFechaTransaccion(sdf.format(new Date()));
        consultClientRequestBodyDto.setCodigoTipoIdentificacion(input.get("codigoTipoIdentificacion"));
        consultClientRequestBodyDto.setNumeroIdentificacion(input.get("identifier"));
        consultClientRequestBodyDto.setIdIntegracion(1);

        return consultClientRequestBodyDto;
    }

}
