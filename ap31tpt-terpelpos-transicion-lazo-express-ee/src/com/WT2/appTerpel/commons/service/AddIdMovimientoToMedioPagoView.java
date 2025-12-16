/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.commons.service;

import com.WT2.appTerpel.domain.entities.MedioPagoInTableView;
import java.util.List;

/**
 *
 * @author USUARIO
 */
public class AddIdMovimientoToMedioPagoView {

    public void execute(List<MedioPagoInTableView> mediosPagoTableView, long id) {
        for (MedioPagoInTableView medioPagoInTableView : mediosPagoTableView) {
            medioPagoInTableView.setIdentificadorMovimeinto(id);
        }
    }
}
