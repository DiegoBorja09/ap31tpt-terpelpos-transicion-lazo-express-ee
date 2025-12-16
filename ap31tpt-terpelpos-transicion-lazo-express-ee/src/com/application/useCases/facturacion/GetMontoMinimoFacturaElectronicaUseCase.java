package com.application.useCases.facturacion;

import com.application.core.BaseUseCases;
import com.google.gson.JsonObject;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.CtWacherParametroRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class GetMontoMinimoFacturaElectronicaUseCase implements BaseUseCases<JsonObject> {

    private final EntityManagerFactory entityManagerFactory;

    public GetMontoMinimoFacturaElectronicaUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public JsonObject execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            CtWacherParametroRepository repository = new CtWacherParametroRepository(em);
            JsonObject response = new JsonObject();

            String obligatorio = repository.obtenerObligatoriedadFE();
            float montoMinimo = repository.obtenerMontoMinimoFE();

            response.addProperty("OBLIGATORIO_FE", obligatorio != null ? obligatorio : "");
            response.addProperty("monto_minimo", montoMinimo);
            response.addProperty("error", montoMinimo == -1.0f);

            return response;
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}


