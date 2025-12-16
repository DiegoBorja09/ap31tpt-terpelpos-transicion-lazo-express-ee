
package com.WT2.appTerpel.application.UseCase;

import com.WT2.appTerpel.domain.entities.MedioPagoAtributos;
import com.WT2.appTerpel.domain.entities.MedioPagoImageBean;


public class ValidateAttributos {

    public void execute(MedioPagoImageBean medio) {

        MedioPagoAtributos medioPagoAtributos = medio.getAtributo();
        
        // Verificar que medioPagoAtributos no sea null
        if (medioPagoAtributos == null) {
            medioPagoAtributos = new MedioPagoAtributos();
            medio.setAtributo(medioPagoAtributos);
        }
        
        if (medioPagoAtributos.getVisible() == null) {
            medioPagoAtributos.setVisible(Boolean.FALSE);
        }

        if (medioPagoAtributos.getBonoTerpel() != null && medioPagoAtributos.getBonoTerpel()) {
            medio.setIsPagoExterno(true);
            medio.setCambio(true);
        }
        
      
    }

}
