/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.loyalty.presentation.builder;

import com.WT2.commons.domain.adapters.IBuilder;
import com.WT2.loyalty.domain.entities.beans.IdentificationClient;
import com.WT2.loyalty.presentation.dto.ParamsAcumularLoyaltyDto;
import com.bean.MediosPagosBean;
import com.bean.MovimientosBean;
import com.bean.MovimientosDetallesBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author USUARIO
 */
public class ParamsAcumularLoyaltyDtoKioscoBuilder implements IBuilder<Map,ParamsAcumularLoyaltyDto> {

    private Map input;

    public void setElementParamas(Map input) {
        this.input = input;
    }

    @Override
    public ParamsAcumularLoyaltyDto build() {

        ParamsAcumularLoyaltyDto params = new ParamsAcumularLoyaltyDto();
        params.setFechaTransaccion((String) input.get("fechaTransaccion"));
        params.setIdentificacionPuntoVenta((String) input.get("identificacionPuntoVenta"));
        params.setOrigenVenta((String) input.get("origenVenta"));
        params.setTipoVenta((String) input.get("tipoVenta"));
        params.setIdentificacionPromotor((String) input.get("identificacionPromotor"));
        params.setIdentificacionVenta((String) input.get("identificacionVenta"));
        params.setValorTotalVenta((int) input.get("totalImpuesto"));
        params.setDescuentoVenta((int) input.get("descuentoVenta"));
        params.setPagoTotal((int) input.get("pagoTotal"));
        params.setIdentificacionCliente((IdentificationClient) input.get("identificacionCliente"));
        params.setComplementario((boolean)input.get("complementario"));
        MovimientosBean movimientosBean = (MovimientosBean) input.get("moviemiento");

        params.setMovimiento(movimientosBean);
        params.setMovimientoId(movimientosBean.getId() );
        List<MediosPagosBean> mediosPagosBeans = new ArrayList<>();
        for (MediosPagosBean mediosPagosBean : movimientosBean.getMediosPagos().values()) {
            mediosPagosBeans.add(mediosPagosBean);
        }
        
        params.setMediosPagos(mediosPagosBeans);

        List<MovimientosDetallesBean> movimientosDetallesBeans = new ArrayList<>();
        for (MovimientosDetallesBean movimientosDetallesBean : movimientosBean.getDetalles().values()) {

            movimientosDetallesBeans.add(movimientosDetallesBean);

        }
        params.setMedMovimientosDetallesBeans(movimientosDetallesBeans);

        return params;

    }

}
