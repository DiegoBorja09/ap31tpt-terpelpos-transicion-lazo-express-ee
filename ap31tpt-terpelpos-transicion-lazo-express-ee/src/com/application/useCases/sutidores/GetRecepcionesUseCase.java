package com.application.useCases.sutidores;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;

import com.application.core.BaseUseCases;
import com.bean.RecepcionBean;
import com.controllers.NovusUtils;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.SurtidorRepository;

public class GetRecepcionesUseCase implements BaseUseCases<ArrayList<RecepcionBean>> {
    
    private final EntityManagerFactory entityManagerFactory;

    public GetRecepcionesUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }
    
    @Override
    public ArrayList<RecepcionBean> execute() {
        try {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            SurtidorRepository surtidorRepository = new SurtidorRepository(entityManager);
            return new ArrayList<>(surtidorRepository.getRecepciones());
        } catch (Exception e) {
            NovusUtils.printLn("Error en GetRecepcionesUseCase: " + e.getMessage());
            return new ArrayList<>();
        }
    }
} 