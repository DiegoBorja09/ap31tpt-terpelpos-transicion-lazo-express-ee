package com.application.useCases.sutidores;

import com.application.core.BaseUseCasesWithParams;
import com.google.gson.JsonObject;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.SurtidorRepository;
import com.controllers.NovusUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class ActualizarVentaParaImprimirUseCase implements BaseUseCasesWithParams<JsonObject, Boolean> {

    private final EntityManagerFactory entityManagerFactory;

    public ActualizarVentaParaImprimirUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
            .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Boolean execute(JsonObject datos) {
        if (datos == null || !datos.has("cara") || !datos.has("manguera")) {
            NovusUtils.printLn("Error: Parámetros inválidos para actualizar venta para imprimir");
            return false;
        }

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            int cara = Integer.parseInt(datos.get("cara").getAsString());
            int manguera = Integer.parseInt(datos.get("manguera").getAsString());
            
            SurtidorRepository surtidorRepository = new SurtidorRepository(entityManager);
            boolean result = surtidorRepository.actualizarVentaParaImprimir(cara, manguera);
            entityManager.getTransaction().commit();
            return result;
        } catch (Exception e) {
            NovusUtils.printLn("Error al actualizar venta para imprimir: " + e.getMessage());
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            return false;
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
} 