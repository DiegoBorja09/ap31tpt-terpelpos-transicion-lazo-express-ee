package com.WT2.appTerpel.application.UseCase;

import com.WT2.appTerpel.domain.context.Concurrents;
import com.WT2.appTerpel.domain.entities.MedioPagoImageBean;
import java.util.ArrayList;

public class RecoverMedioPagoImage {

 

    public ArrayList<MedioPagoImageBean> execute() {
        ArrayList<MedioPagoImageBean> listMedio = new ArrayList<>();
        Concurrents.medioPagoImageList.forEach(data -> {

           if(data.getId()!= 20000L) listMedio.add(data);

        });
        return listMedio;
    }


}
