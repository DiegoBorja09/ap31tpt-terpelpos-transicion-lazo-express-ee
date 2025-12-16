package com.application.useCases.sutidores;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.application.core.BaseUseCasesWithParams;
import com.controllers.NovusUtils;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.SurtidorRepository;

public class GetCodigoExternoProductoUseCase implements BaseUseCasesWithParams<Long[], String> {
    
    private final EntityManagerFactory entityManagerFactory;

    public GetCodigoExternoProductoUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }
    
    @Override
    public String execute(Long[] params) {
        try {
            if (params == null || params.length != 2) {
                throw new IllegalArgumentException("Se requieren exactamente 2 parámetros: cara y grado");
            }
            
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            SurtidorRepository surtidorRepository = new SurtidorRepository(entityManager);
            return surtidorRepository.getCodigoExternoProducto(params[0], params[1]);
            
        } catch (Exception e) {
            NovusUtils.printLn("Error en GetCodigoExternoProductoUseCase: " + e.getMessage());
            return null;
        }
    }
} 