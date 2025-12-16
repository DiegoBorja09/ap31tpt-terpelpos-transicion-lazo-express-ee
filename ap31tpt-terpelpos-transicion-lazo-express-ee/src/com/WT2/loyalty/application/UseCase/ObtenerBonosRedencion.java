package com.WT2.loyalty.application.UseCase;

import com.bean.AtributosBono;
import com.bean.MediosPagosBean;
import com.controllers.NovusConstante;
import java.util.ArrayList;
import java.util.List;

public class ObtenerBonosRedencion {

    public static List<MediosPagosBean> execute(List<MediosPagosBean> mediosPago) {
        List<MediosPagosBean> resultadoMedios = new ArrayList<>();
        MediosPagosBean medios = null;

        float totalRecibido = 0;
        float totalMedioPago = 0;
        StringBuilder comprobantes = new StringBuilder();
        List<AtributosBono> Bonos_Vive_Terpel = new ArrayList<>();

        for (MediosPagosBean pago : mediosPago) {
            if (pago.getId() == NovusConstante.ID_MEDIO_BONO_TERPEL) {
                totalRecibido += pago.getRecibido();
                totalMedioPago += pago.getValor();

                if (comprobantes.length() > 0) {
                    comprobantes.append(",");
                }
                comprobantes.append(pago.getVoucher());

                AtributosBono atributosBono = new AtributosBono();
                atributosBono.setIFP(32);
                atributosBono.setVFP(pago.getValor());
                atributosBono.setAFP(pago.getVoucher());
                Bonos_Vive_Terpel.add(atributosBono);

            } else {
                resultadoMedios.add(pago);
            }
        }

        if (totalRecibido > 0 || totalMedioPago > 0) {
            medios = new MediosPagosBean();
            medios.setIdRegistro(0);
            medios.setIdMedioPago(0);
            medios.setFranquicia("");
            medios.setIdAdquiriente(0);
            medios.setIdTransaccion(0);
            medios.setNumeroRecibo("");
            medios.setBin("");
            medios.setTipoDeCuenta("");
            medios.setDescripcion("BONO VIVE TERPEL");
            medios.setId(NovusConstante.ID_MEDIO_BONO_TERPEL);
            medios.setRecibido(totalRecibido);
            medios.setValor(totalMedioPago);
            medios.setCambio(0);
            medios.setVoucher(comprobantes.toString());
            medios.setCredito(false);
            medios.setIsCambio(true);
            medios.setComprobante(true);
            medios.setSeleccionado(false);
            medios.setIsPagoExterno(false);
            medios.setPagosExternoValidado(true);
            medios.setRedencionMillas(false);
            medios.setConfirmacionBono(false);
            medios.setTrm(0);
            medios.setBonos_Vive_Terpel(Bonos_Vive_Terpel);

            resultadoMedios.add(medios);
        }

        return resultadoMedios;
    }
}
