package com.firefuel.utils;

import com.firefuel.FidelizacionyFacturacionElectronica;

public class ShowFidelizacionyFacturacion {

    private static FidelizacionyFacturacionElectronica instance;

    public static FidelizacionyFacturacionElectronica showFidelizacionyFacturacionElectronicaView() {
        if (instance == null) {
            instance = new FidelizacionyFacturacionElectronica();
        }
        return instance;
    }

}
