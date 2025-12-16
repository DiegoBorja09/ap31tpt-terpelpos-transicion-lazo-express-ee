/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.application.UseCase;

import com.WT2.appTerpel.domain.context.Concurrents;
import com.WT2.appTerpel.domain.entities.MedioPagoImageBean;
import java.util.ArrayList;

/**
 *
 * @author USUARIO
 */
public class RecoverMedioPagoWithoutGoPass {
    
    
  
    
     public ArrayList<MedioPagoImageBean> execute() {
        ArrayList<MedioPagoImageBean> listMedio = new ArrayList<>();
        Concurrents.medioPagoImageList.forEach(data -> {
            if(data.getId() != 20004L && data.getId() != 1L) listMedio.add(data);
        });
        return listMedio;
    }
    
}
