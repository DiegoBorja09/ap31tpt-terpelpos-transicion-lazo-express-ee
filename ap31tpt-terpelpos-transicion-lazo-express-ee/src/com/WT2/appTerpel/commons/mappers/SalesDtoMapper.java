/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.commons.mappers;

import com.WT2.commons.domain.adapters.IMapper;
import com.WT2.appTerpel.commons.dto.ConsecutiveAtributeDto;
import com.WT2.appTerpel.commons.dto.ConsecutiveDto;
import com.WT2.appTerpel.commons.dto.PaymentMethodsDto;
import com.WT2.appTerpel.commons.dto.SalesAtributeExtraDataDto;
import com.WT2.appTerpel.commons.dto.SalesAtributesDto;
import com.WT2.appTerpel.commons.dto.SalesDto;
import com.WT2.appTerpel.domain.entities.SalesAtributeExtraData;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author USUARIO
 */
public class SalesDtoMapper implements IMapper<ResultSet, SalesDto> {

    private Gson gson = new Gson();

    @Override
    public SalesDto mapTo(ResultSet rs) {
        SalesDto salesDto = new SalesDto();
        SalesAtributesDto salesAtributesDto = new SalesAtributesDto();
        SalesAtributeExtraDataDto salesAtributeExtraDataDto = new SalesAtributeExtraDataDto();
        ConsecutiveDto consecutiveDto = new ConsecutiveDto();
        ConsecutiveAtributeDto consecutiveAtributeDto = new ConsecutiveAtributeDto();
        try {

            JsonObject atributosDB = gson.fromJson(rs.getString("atributos"), JsonObject.class);
            String prefijo = "";
            String voucher = "";
            String orden = "";
            consecutiveDto.setConsecutivoActual(rs.getLong("numero"));
            JsonObject consecutiveObj = atributosDB.get("consecutivo").getAsJsonObject();
            
            if (rs.getString("tipo").equals("016") && atributosDB.has("isElectronica") && !atributosDB.get("isElectronica").getAsBoolean() && atributosDB.get("tipoVenta").getAsInt() == 100) {
                prefijo = "PROPIO";
            } else if (rs.getString("tipo").equals("032")) {
                prefijo = "CREDITO";
            }else if(consecutiveObj.get("prefijo")!= null){
                prefijo = consecutiveObj.get("prefijo").getAsString();
            }

            if (atributosDB.get("voucher") != null) {
                voucher = atributosDB.get("voucher").getAsString();
            }

            consecutiveDto.setConsecutivoActual(rs.getLong("consecutivo"));
            consecutiveDto.setPrefijo(prefijo);

            salesAtributesDto.setVehiculo_placa(atributosDB.get("vehiculo_placa").getAsString());
            salesAtributesDto.setVoucher(voucher);
            salesAtributesDto.setOrden(orden);
            salesAtributesDto.setVehiculo_odometro(atributosDB.get("vehiculo_odometro") != null ? atributosDB.get("vehiculo_odometro").getAsString() : "");
            salesAtributesDto.setVehiculo_numero(atributosDB.get("vehiculo_numero") != null ? atributosDB.get("vehiculo_numero").getAsString() : "");

            salesAtributesDto.setIsElectronica(atributosDB.get("isElectronica").getAsBoolean());
            salesAtributesDto.setRecuperada(atributosDB.get("recuperada").getAsBoolean());
            salesAtributesDto.setIsCredito(atributosDB.get("isCredito").getAsBoolean());
            salesAtributesDto.setTipoVenta(atributosDB.get("tipoVenta").getAsFloat());
            salesDto.setOperador(rs.getString("operador"));
            salesDto.setConsecutivo(rs.getLong("consecutivo"));
            salesDto.setNumero(rs.getLong("numero"));
            salesDto.setTipo(rs.getString("tipo"));
            salesDto.setCantidad(rs.getDouble("cantidad"));
            salesDto.setPrecio(rs.getLong("precio"));
            salesDto.setTotal(rs.getLong("total"));
            salesDto.setCara(rs.getLong("cara"));
            salesDto.setManguera(rs.getLong("manguera"));
            salesDto.setProducto(rs.getString("producto"));
            salesDto.setOperador(rs.getString("operador"));
            salesDto.setIdentificacionPromotor(rs.getString("identificacionPromotor"));
            salesDto.setIdentificacionProducto(rs.getLong("identificacionProducto"));
            salesDto.setUnidadMedida(rs.getString("unidad_medida"));
            
            LocalDateTime localDate  = rs.getTimestamp("fecha").toLocalDateTime();
            localDate.format(DateTimeFormatter.ofPattern("d::MMM::uuuu HH::mm::ss a"));
            salesDto.setFecha(localDate);
            salesDto.setStatus(rs.getString("pago_estado"));
            salesAtributeExtraDataDto = gson.fromJson(atributosDB.get("extraData"), SalesAtributeExtraDataDto.class);
            
            

        } catch (SQLException ex) {
            Logger.getLogger(SalesDtoMapper.class.getName()).log(Level.SEVERE, null, ex);
        }

        salesDto.setSalesAtributesDto(salesAtributesDto);
        salesAtributesDto.setConsecutiveDto(consecutiveDto);
        salesAtributesDto.setSalesAtributeExtraDataDto(salesAtributeExtraDataDto);

        consecutiveDto.setConsecutiveAtributeDto(consecutiveAtributeDto);

        return salesDto;

    }

}
