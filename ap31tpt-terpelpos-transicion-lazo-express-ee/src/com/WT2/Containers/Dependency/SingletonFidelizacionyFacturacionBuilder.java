
package com.WT2.Containers.Dependency;

import com.WT2.loyalty.infrastructure.builders.FidelizacionyFacturacionBuilder;


public class SingletonFidelizacionyFacturacionBuilder {
    
    private static FidelizacionyFacturacionBuilder instance;

    public static FidelizacionyFacturacionBuilder getFidelizacionyFacturacionBuilder() {
        if (instance == null) {
            instance = new FidelizacionyFacturacionBuilder();
        }
        return instance;
    }
    
}
