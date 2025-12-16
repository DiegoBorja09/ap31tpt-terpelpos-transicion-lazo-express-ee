/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.loyalty.presentation.mapper;

import com.WT2.commons.domain.adapters.IMapper;
import com.WT2.loyalty.domain.entities.beans.MediosPagoLoyalty;
import com.WT2.loyalty.domain.entities.beans.ProductsLoyalty;
import com.WT2.loyalty.domain.entities.params.ParamsAcumularLoyalty;
import com.WT2.loyalty.presentation.dto.ParamsAcumularLoyaltyDto;
import com.bean.MediosPagosBean;
import com.bean.MovimientosDetallesBean;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author USUARIO
 */
public class ParamsAcumularLoyaltyMapper implements IMapper<ParamsAcumularLoyaltyDto, ParamsAcumularLoyalty> {


    @Override
    public ParamsAcumularLoyalty mapTo(ParamsAcumularLoyaltyDto input) {
        System.out.println("🧪 MediosPago (previo a mapper):");
        for (MediosPagosBean mp : input.getMediosPagos()) {
            System.out.println("  ➤ Bean ID: " + mp.getId() + ", Descripción: " + mp.getDescripcion() + ", Valor: " + mp.getValor() + ", Recibido: " + mp.getRecibido());
        }

        ParamsAcumularLoyalty paramsAcumularLoyalty = new ParamsAcumularLoyalty();
        paramsAcumularLoyalty.setDescuentoVenta(input.getDescuentoVenta());
        paramsAcumularLoyalty.setFechaTransaccion(input.getFechaTransaccion());
        paramsAcumularLoyalty.setIdentificacionCliente(input.getIdentificacionCliente());
        paramsAcumularLoyalty.setIdentificacionPromotor(input.getIdentificacionPromotor());
        paramsAcumularLoyalty.setIdentificacionPuntoVenta(input.getIdentificacionPuntoVenta());
        paramsAcumularLoyalty.setIdentificacionCliente(input.getIdentificacionCliente());
        paramsAcumularLoyalty.setOrigenVenta(input.getOrigenVenta());
        paramsAcumularLoyalty.setPagoTotal(input.getPagoTotal());
        paramsAcumularLoyalty.setTipoVenta(input.getTipoVenta());
        paramsAcumularLoyalty.setTotalImpuesto(input.getTotalImpuesto());
        paramsAcumularLoyalty.setValorTotalVenta(input.getPagoTotal());
        paramsAcumularLoyalty.setMovimientoId(input.getMovimientoId());
        paramsAcumularLoyalty.setIdentificacionVenta(input.getIdentificacionVenta());
        List<MediosPagoLoyalty> mediosPagoLoyalties = new ArrayList<>();
        List<ProductsLoyalty> productsLoyalties = new ArrayList<>();

        for (MediosPagosBean detallePagos : input.getMediosPagos()) {

            MediosPagoLoyalty mediosPagoLoyalty = new MediosPagoLoyalty();
            mediosPagoLoyalty.setIdentificacionMedioPago(detallePagos.getId() + "");
            mediosPagoLoyalty.setValorPago(Math.round(detallePagos.getRecibido()) + "");
            mediosPagoLoyalties.add(mediosPagoLoyalty);
            System.out.println("🎯 Mapeando medio: ID=" + detallePagos.getId() + ", Recibido=" + detallePagos.getRecibido() + " → Enviado=" + mediosPagoLoyalty.getIdentificacionMedioPago());


        }

        for (MovimientosDetallesBean detalle : input.getMedMovimientosDetallesBeans()) {

            ProductsLoyalty productsLoyalty = new ProductsLoyalty();
            productsLoyalty.setCantidadProducto(detalle.getCantidadUnidad());

            productsLoyalty.setValorUnitarioProducto(Math.round(detalle.getPrecio()));
            if (input.isComplementario()) {
                productsLoyalty.setIdentificacionProducto(detalle.getCodigoBarra() + "");
                productsLoyalty.setLineaNegocio(NovusUtils.getLineaNegocio(input.getTipoVenta()));
            } else {
                productsLoyalty.setIdentificacionProducto(detalle.getId() + "");
            }
            productsLoyalties.add(productsLoyalty);
        }

        paramsAcumularLoyalty.setMediosPago(mediosPagoLoyalties);
        paramsAcumularLoyalty.setProductos(productsLoyalties);
        return paramsAcumularLoyalty;

    }

}
