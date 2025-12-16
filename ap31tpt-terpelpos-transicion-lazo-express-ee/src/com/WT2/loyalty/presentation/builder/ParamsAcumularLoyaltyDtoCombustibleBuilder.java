/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.loyalty.presentation.builder;

import com.WT2.appTerpel.domain.entities.MedioPagoImageBean;
import com.WT2.commons.domain.adapters.IBuilder;
import com.WT2.loyalty.domain.entities.beans.IdentificationClient;
import com.WT2.loyalty.presentation.dto.ParamsAcumularLoyaltyDto;
import com.bean.MovimientosDetallesBean;
import java.util.ArrayList;
import com.bean.MediosPagosBean;
import java.util.List;
import java.util.Map;

/**
 *
 * @author USUARIO
 */
public class ParamsAcumularLoyaltyDtoCombustibleBuilder implements IBuilder<Map<String, Object>, ParamsAcumularLoyaltyDto> {

    private Map<String, Object> input;
    private ParamsAcumularLoyaltyDto params;

    public ParamsAcumularLoyaltyDtoCombustibleBuilder() {
        this.params = new ParamsAcumularLoyaltyDto();;
    }

    @Override

    public ParamsAcumularLoyaltyDto build() {

        params.setFechaTransaccion((String) input.get("fechaTransaccion"));
        params.setIdentificacionPuntoVenta((String) input.get("identificacionPuntoVenta"));
        params.setOrigenVenta((String) input.get("origenVenta"));
        params.setTipoVenta((String) input.get("tipoVenta"));
        params.setIdentificacionPromotor((String) input.get("identificacionPromotor"));
        params.setIdentificacionVenta((String) input.get("identificacionVenta"));
        params.setValorTotalVenta((int) input.get("totalImpuesto"));
        params.setDescuentoVenta((int) input.get("descuentoVenta"));
        params.setPagoTotal((long) input.get("pagoTotal"));
        params.setMovimientoId(Long.valueOf((String) input.get("movimientoId")));
        List<MediosPagosBean> mediosPagosBeans = new ArrayList<>();
        List<com.neo.app.bean.MediosPagosBean> mediosPagosBeansNeo = (List<com.neo.app.bean.MediosPagosBean>) input.get("mediosPago");
             
        for(com.neo.app.bean.MediosPagosBean medioNeo : mediosPagosBeansNeo){
            MediosPagosBean mediosPagosBean = new MedioPagoImageBean();
            mediosPagosBean.setId(medioNeo.getId());
            mediosPagosBean.setRecibido(medioNeo.getValor());
            mediosPagosBeans.add(mediosPagosBean);
        }
        params.setMediosPagos(mediosPagosBeans);
        List<Map<String, Map>> products = (List<Map<String, Map>>) input.get("productos");
        List<MovimientosDetallesBean> movimientosDetallesBeans = new ArrayList<>();

        for (Map<String, Map> product : products) {
            MovimientosDetallesBean movimientosDetallesBean = new MovimientosDetallesBean();
            movimientosDetallesBean.setCantidadUnidad(Float.valueOf(product.get("cantidadProducto") + ""));
            movimientosDetallesBean.setPrecio(Float.valueOf(product.get("valorUnitarioProducto") + ""));
            movimientosDetallesBean.setId(Long.valueOf(product.get("identificacionProducto") + ""));
            movimientosDetallesBeans.add(movimientosDetallesBean);
        }

        params.setMedMovimientosDetallesBeans(movimientosDetallesBeans);

        IdentificationClient identificationClient = new IdentificationClient();
        identificationClient.setCodigoTipoIdentificacion((String) input.get("codigoTipoIdentificacion"));
        identificationClient.setNumeroIdentificacion((String) input.get("numeroIdentificacion"));
        params.setIdentificacionCliente(identificationClient);

        return this.params;

    }

    @Override
    public void setElementParamas(Map elements) {
        this.input = elements;
    }

}
