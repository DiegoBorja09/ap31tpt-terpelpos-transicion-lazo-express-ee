package com.application.useCases.sutidores;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.application.core.BaseUseCasesWithParams;
import com.controllers.NovusUtils;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.SurtidorRepository;

public class OcultarInputsFacturaVentaUseCase implements BaseUseCasesWithParams<Integer, Boolean> {
    
    private final EntityManagerFactory entityManagerFactory;

    public OcultarInputsFacturaVentaUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }
    
    @Override
    public Boolean execute(Integer cara) {
        try {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            SurtidorRepository surtidorRepository = new SurtidorRepository(entityManager);
            return surtidorRepository.ocultarInputsFacturaVenta(cara);
            
        } catch (Exception e) {
            NovusUtils.printLn("Error en OcultarInputsFacturaVentaUseCase: " + e.getMessage());
            return false;
        }
    }
}
