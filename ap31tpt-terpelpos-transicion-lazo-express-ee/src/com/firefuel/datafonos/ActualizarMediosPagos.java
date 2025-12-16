/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.firefuel.datafonos;

import com.bean.BonoViveTerpel;
import com.bean.MediosPagosBean;
import com.bean.MovimientosBean;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.MovimientosDao;
import com.dao.SurtidorDao;
import com.firefuel.Main;
import com.firefuel.MedioPagosConfirmarViewController;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;

/**
 *
 * @author Devitech
 */
public class ActualizarMediosPagos {

    MovimientosDao mdao = new MovimientosDao();

    public void actulizarMediosDePagoSinDatafonos(MovimientosBean movimientosBean, ArrayList<MediosPagosBean> mediosDepago) {
        JsonObject objMain = new JsonObject();
        objMain.addProperty("identificadorMovimiento", movimientosBean.getId());
        objMain.addProperty("identificadorEquipo", Main.credencial.getEquipos_id());
        objMain.addProperty("validarTurno", false);
        JsonArray mediosArray = new JsonArray();
        JsonArray bonosArray = new JsonArray();
        boolean isBonoTerpelMP = Boolean.FALSE;
        SurtidorDao sdao = new SurtidorDao();

        //Si valida si en la venta se encuentran Bonos Vive Terpel
        ArrayList<MediosPagosBean> mediosList = validacionMediosBonos(mediosDepago);

        for (MediosPagosBean medio : mediosList) {
            JsonObject objMedios = new JsonObject();
            objMedios.addProperty("ct_medios_pagos_id", medio.getId());
            objMedios.addProperty("descripcion", medio.getDescripcion());
            objMedios.addProperty("valor_recibido", medio.getRecibido());
            objMedios.addProperty("valor_cambio", medio.getCambio());
            objMedios.addProperty("valor_total", medio.getRecibido());
            objMedios.addProperty("voucher", medio.getVoucher());
            objMedios.addProperty("moneda_local", "");
            objMedios.addProperty("numero_comprobante", medio.getVoucher());
            if (medio.getId() == 20000 && !MedioPagosConfirmarViewController.bonosValidados) {
                objMedios.addProperty("confirmacionBono", true);
            } else {
                objMedios.addProperty("confirmacionBono", false);
            }
            objMedios.addProperty("ing_pago_datafono", mdao.validarMedio(movimientosBean.getId(), medio.getId()));
            mediosArray.add(objMedios);
        }

        for (MediosPagosBean medio : mediosDepago) {
            if (medio.isPagosExternoValidado()) {
                isBonoTerpelMP = true;
                long salesForceIdMp = sdao.getCodigoSalesForceMP(medio.getId());
                for (BonoViveTerpel bono : medio.getBonosViveTerpel()) {
                    JsonObject bonoViveTerpelMP = new JsonObject();
                    bonoViveTerpelMP.addProperty("IFP", salesForceIdMp);
                    bonoViveTerpelMP.addProperty("VFP", bono.getValor());
                    bonoViveTerpelMP.addProperty("AFP", bono.getVoucher());
                    bonosArray.add(bonoViveTerpelMP);
                }
            }
        }

        objMain.add("mediosDePagos", mediosArray);
        NovusUtils.printLn(objMain.toString());

        boolean response = mdao.actualizarMediosPagoVenta(objMain.toString());
        if (isBonoTerpelMP) {
            actualizarMovViveTerpel(response, bonosArray, movimientosBean);
        }
        if (response) {
            mdao.ingresarAuditoriaMediosPago(movimientosBean.getId(), mediosArray, movimientosBean.getVentaTotal());
        }
        if (response) {
            NovusUtils.printLn(Main.ANSI_GREEN + " medios de pagos distintos a datafonos fueron actulizado con exito " + Main.ANSI_RESET);
        } else {
            NovusUtils.printLn(Main.ANSI_RED + " error al actulizar los medios de pagos distintos a datafonos " + Main.ANSI_RESET);
        }
    }

    public ArrayList<MediosPagosBean> validacionMediosBonos(ArrayList<MediosPagosBean> mediosVenta) {
        int indexBonoViveTerpel = -1;
        boolean isBonoViveTerpel = false;
        ArrayList<MediosPagosBean> mediosList = new ArrayList<>();
        for (int i = 0; i < mediosVenta.size(); i++) {
            String descripcion = mediosVenta.get(i).getDescripcion();
            if (descripcion.equals("BONO VIVE TERPEL") && !isBonoViveTerpel) {
                isBonoViveTerpel = true;
                mediosList.add(mediosVenta.get(i));
                indexBonoViveTerpel = i;
            } else if (descripcion.equals("BONO VIVE TERPEL") && isBonoViveTerpel) {
                String separador = !mediosList.get(indexBonoViveTerpel).getVoucher().equals("") ? "," : "";
                String voucher = mediosVenta.get(i).getVoucher()
                        + separador + mediosList.get(indexBonoViveTerpel).getVoucher();
                float valor = mediosVenta.get(i).getRecibido()
                        + mediosList.get(indexBonoViveTerpel).getRecibido();
                mediosList.get(indexBonoViveTerpel).setVoucher(voucher);
                mediosList.get(indexBonoViveTerpel).setValor(valor);
                mediosList.get(indexBonoViveTerpel).setRecibido(valor);
            } else {
                mediosList.add(mediosVenta.get(i));
            }
        }
        return mediosList;
    }

    public void actualizarMovViveTerpel(boolean response, JsonArray bonos, MovimientosBean movimientosBean) {
        if (response) {
            Long idVenta = movimientosBean.getId();
            try {
                if (mdao.ActualizarAtributosViveTerpel(idVenta, bonos)) {
                    NovusUtils.printLn("ATRIBUTOS VIVE TERPEL ACTUALIZADOS");
                } else {
                    NovusUtils.printLn("NO SE PUDO ACTUALIZAR ATRIBUTOS");
                }
            } catch (DAOException e) {
                NovusUtils.printLn(e.getMessage());
            }

        }
    }
}
