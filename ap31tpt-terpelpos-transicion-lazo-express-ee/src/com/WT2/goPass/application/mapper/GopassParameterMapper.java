/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.goPass.application.mapper;

import com.WT2.commons.domain.adapters.IMapper;
import com.WT2.goPass.domain.entity.dto.GopassParametersDto;
import com.WT2.goPass.domain.entity.beans.GopassParameters;
import com.WT2.goPass.domain.valueObject.GopassParametersDefaultValues;
import com.WT2.goPass.domain.valueObject.GopassParametersDictionary;
import java.util.LinkedList;

/**
 *
 * @author Jeison
 */
public class GopassParameterMapper implements IMapper<LinkedList<GopassParametersDto>, GopassParameters> {

    @Override
    public GopassParameters mapTo(LinkedList<GopassParametersDto> input) {
        GopassParameters gopassParameters = new GopassParameters();
        int configuracionTokenTiempoReintentos = GopassParametersDefaultValues.TOKEN_REINTENTOS_TIEMPO;
        int configuracionTokenTiempoMuerto = GopassParametersDefaultValues.TOKEN_TIEMPO_MUERTO;
        int configuracionTokenCantidadReintentos = GopassParametersDefaultValues.TOKEN_REINTENTOS_CANTIDAD;
        int configuracionPagoTiempoReintentos = GopassParametersDefaultValues.PAGO_REINTENTOS_TIEMPO;
        int configuracionPagoCantidadReintentos = GopassParametersDefaultValues.PAGO_REINTENTOS_CANTIDAD;
        int configuracionConsultaPlacaTiempoReintentos = GopassParametersDefaultValues.CONSULTA_REINTENTOS_TIEMPO;
        int configuracionConsultaPlacaCantidadReintentos = GopassParametersDefaultValues.CONSULTA_REINTENTOS_CANTIDAD;
        int configuracionConsultaPlacaTiempoMuerto = GopassParametersDefaultValues.TOKEN_TIEMPO_MUERTO;

        int configuracionPuerto = GopassParametersDefaultValues.PUERTO;
        String configuracionHost = "";
        String configuracionCodigoEds = "";
        String configuracionProtocoloGopass = GopassParametersDefaultValues.PROTOCOLO;
        String configuracionVersionamiento = GopassParametersDefaultValues.VERSION;

        String credencialesUsuario = "";
        String credencialesContrasena = "";

        if (!input.isEmpty()) {
            for (GopassParametersDto parameterDto : input) {
                if (parameterDto.getCodigo() != null && !parameterDto.getCodigo().isEmpty()) {
                    switch (parameterDto.getValor()) {
                        case GopassParametersDictionary.GOPASS_INTEGRATION_SERVICE_CONSULT_PAYMENT_RETRY_TIME:
                            configuracionPagoTiempoReintentos = isNumericInt(parameterDto.getValor()) ? Integer.valueOf(parameterDto.getValor()) : GopassParametersDefaultValues.PAGO_REINTENTOS_TIEMPO;
                            break;
                        case GopassParametersDictionary.GOPASS_INTEGRATION_SERVICE_CONSULT_PAYMENT_RETRY_NUMBERS:
                            configuracionPagoCantidadReintentos = isNumericInt(parameterDto.getValor()) ? Integer.valueOf(parameterDto.getValor()) : GopassParametersDefaultValues.CONSULTA_REINTENTOS_CANTIDAD;
                            break;
                        case GopassParametersDictionary.GOPASS_INTEGRATION_SERVICE_CONSULT_PLATE_RETRY_NUMBERS:
                            configuracionConsultaPlacaCantidadReintentos = isNumericInt(parameterDto.getValor()) ? Integer.valueOf(parameterDto.getValor()) : GopassParametersDefaultValues.CONSULTA_REINTENTOS_CANTIDAD;

                        case GopassParametersDictionary.GOPASS_INTEGRATION_SERVICE_CONSULT_PLATE_RETRY_TIME:
                            configuracionConsultaPlacaTiempoReintentos = isNumericInt(parameterDto.getValor()) ? Integer.valueOf(parameterDto.getValor()) : GopassParametersDefaultValues.CONSULTA_REINTENTOS_TIEMPO;
                            break;
                        case GopassParametersDictionary.GOPASS_INTEGRATION_SERVICE_CONSULT_PLATE_SLEEP_RETRY_TIME:
                            configuracionConsultaPlacaTiempoMuerto = isNumericInt(parameterDto.getValor()) ? Integer.valueOf(parameterDto.getValor()) : GopassParametersDefaultValues.TOKEN_TIEMPO_MUERTO;
                            break;
                        case GopassParametersDictionary.GOPASS_INTEGRATION_SERVICE_PAYMENT_RETRY_TIME:
                            configuracionPagoTiempoReintentos = isNumericInt(parameterDto.getValor()) ? Integer.valueOf(parameterDto.getValor()) : GopassParametersDefaultValues.PAGO_REINTENTOS_TIEMPO;
                            break;
                        case GopassParametersDictionary.GOPASS_INTEGRATION_SERVICE_PAYMENT_RETRY_NUMBERS:
                            configuracionPagoCantidadReintentos = isNumericInt(parameterDto.getValor()) ? Integer.valueOf(parameterDto.getValor()) : GopassParametersDefaultValues.PAGO_REINTENTOS_CANTIDAD;
                            break;
                        case GopassParametersDictionary.GOPASS_INTEGRATION_SERVICE_TOKEN_RETRY_TIME:
                            configuracionTokenTiempoReintentos = isNumericInt(parameterDto.getValor()) ? Integer.valueOf(parameterDto.getValor()) : GopassParametersDefaultValues.TOKEN_REINTENTOS_TIEMPO;
                            break;
                        case GopassParametersDictionary.GOPASS_INTEGRATION_SERVICE_TOKEN_RETRY_NUMBERS:
                            configuracionTokenCantidadReintentos = isNumericInt(parameterDto.getValor()) ? Integer.valueOf(parameterDto.getValor()) : GopassParametersDefaultValues.TOKEN_REINTENTOS_CANTIDAD;
                            break;
                        case GopassParametersDictionary.GOPASS_INTEGRATION_SERVICE_TOKEN_SLEEP_RETRY_TIME:
                            configuracionTokenTiempoMuerto = isNumericInt(parameterDto.getValor()) ? Integer.valueOf(parameterDto.getValor()) : GopassParametersDefaultValues.TOKEN_TIEMPO_MUERTO;
                            break;
                        case GopassParametersDictionary.GOPASS_CONFIG_AUTH_USER:
                            credencialesUsuario = parameterDto.getValor();
                            break;
                        case GopassParametersDictionary.GOPASS_CONFIG_AUTH_PASS:
                            credencialesContrasena = parameterDto.getValor();
                            break;
                        case GopassParametersDictionary.GOPASS_CONFIG_PROTOCOL:
                            configuracionProtocoloGopass = parameterDto.getValor();
                            break;
                        case GopassParametersDictionary.GOPASS_CONFIG_HOST:
                            configuracionHost = parameterDto.getValor();
                            break;
                        case GopassParametersDictionary.GOPASS_CONFIG_PORT:
                            configuracionPuerto = isNumericInt(parameterDto.getValor()) ? Integer.valueOf(parameterDto.getValor()) : GopassParametersDefaultValues.PUERTO;
                            break;
                        case GopassParametersDictionary.GOPASS_CONFIG_VERSION:
                            configuracionVersionamiento = parameterDto.getValor();
                            break;
                        case GopassParametersDictionary.GOPASS_CONFIG_CODE:
                            configuracionCodigoEds = parameterDto.getValor();
                            break;

                    }

                    gopassParameters.setConfiguracionConsultaPlacaCantidadReintentos(configuracionConsultaPlacaCantidadReintentos);
                    gopassParameters.setConfiguracionConsultaPlacaTiempoReintentos(configuracionConsultaPlacaTiempoReintentos);

                    gopassParameters.setConfiguracionPagoCantidadReintentos(configuracionPagoCantidadReintentos);
                    gopassParameters.setConfiguracionPagoTiempoReintentos(configuracionPagoTiempoReintentos);
                    gopassParameters.setConfiguracionTokenCantidadReintentos(configuracionTokenCantidadReintentos);
                    gopassParameters.setConfiguracionTokenTiempoReintentos(configuracionTokenTiempoReintentos);
                    gopassParameters.setConfiguracionTokenTiempoMuerto(configuracionTokenTiempoMuerto);

                    gopassParameters.setConfiguracionHost(configuracionHost);
                    gopassParameters.setConfiguracionPuerto(configuracionPuerto);
                    gopassParameters.setConfiguracionProtocoloGopass(configuracionProtocoloGopass);
                    gopassParameters.setCredencialesUsuario(credencialesUsuario);
                    gopassParameters.setCredencialesContrasena(credencialesContrasena);
                    gopassParameters.setConfiguracionVersionamiento(configuracionVersionamiento);
                    gopassParameters.setConfiguracionCodigoEds(configuracionCodigoEds);
                    gopassParameters.setConfiguracionConsultaPlacaTiempoMuerto(configuracionConsultaPlacaTiempoMuerto);
                }
            }
        }

        return gopassParameters;
    }

    public static boolean isNumericInt(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNumericLong(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
