/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.presentation;

import com.WT2.appTerpel.application.UseCase.Payment.Listing.RecoverDataMedioPago;
import com.WT2.appTerpel.application.UseCase.RecoverMedioPagoImage;
import com.WT2.appTerpel.domain.entities.MedioPagoImageBean;
import com.dao.Postgrest;
import com.dao.SetupDao;
import com.firefuel.Main;
import java.util.ArrayList;

/**
 *
 * @author USUARIO
 */
public class MainTesting {

    public static void main(String args[]) throws Exception {

        Main.dbCoreAsync = new Postgrest("lazoexpresscore");
        System.out.println("TEST ANYTHING HERE BEFORE PASSING TO FRONT-END");
        RecoverDataMedioPago recover = new RecoverDataMedioPago(new SetupDao());
        RecoverMedioPagoImage getListImagnes = new RecoverMedioPagoImage();
       
        recover.loadMedioPago();
        
        ArrayList<MedioPagoImageBean> medioPagoList = getListImagnes.execute();
        
        for(MedioPagoImageBean medio : medioPagoList){
            System.out.println( medio.getDescripcion()+" - "+ medio.getImagePathChecked()+" - "+medio.getImagePathUnchecked());
        }
     }
}
