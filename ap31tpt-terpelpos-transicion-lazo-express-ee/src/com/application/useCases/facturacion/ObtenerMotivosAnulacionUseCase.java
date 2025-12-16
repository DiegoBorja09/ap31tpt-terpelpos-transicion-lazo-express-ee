package com.application.useCases.facturacion;
import com.application.core.BaseUseCases;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.CtWacherParametroRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;



public class ObtenerMotivosAnulacionUseCase implements BaseUseCases<JsonArray> {

    private final EntityManagerFactory entityManagerFactory;

    public ObtenerMotivosAnulacionUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public JsonArray execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            CtWacherParametroRepository repository = new CtWacherParametroRepository(em);
            return repository.obtenerMotivosAnulacion();
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}
