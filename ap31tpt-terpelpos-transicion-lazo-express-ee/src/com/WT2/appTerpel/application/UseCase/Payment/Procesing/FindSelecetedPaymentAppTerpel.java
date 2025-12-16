/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.application.UseCase.Payment.Procesing;

import com.WT2.appTerpel.domain.entities.MedioPagoInTableView;
import com.WT2.appTerpel.domain.valueObject.MediosPagosDescription;
import java.util.List;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import com.WT2.commons.domain.adapters.IUseCase;

/**
 *
 * @author USUARIO
 */
public class FindSelecetedPaymentAppTerpel implements IUseCase<List<MedioPagoInTableView>, MedioPagoInTableView> {
    
    @Override
    public MedioPagoInTableView execute(List<MedioPagoInTableView> input) {
        Boolean exist = false;
        int index = 0;
        
        MedioPagoInTableView medioPagoInTableView = new MedioPagoInTableView();
        medioPagoInTableView.setMedio("");
        medioPagoInTableView.setVoucher("");
        while (!exist && index < input.size()) {
            exist = input.get(index).validateIsPayment(MediosPagosDescription.APPTERPEL);
            
            if (exist) {
                medioPagoInTableView = input.get(index);
            }
            index++;
        }
        
        return medioPagoInTableView;
        
    }
    
}
