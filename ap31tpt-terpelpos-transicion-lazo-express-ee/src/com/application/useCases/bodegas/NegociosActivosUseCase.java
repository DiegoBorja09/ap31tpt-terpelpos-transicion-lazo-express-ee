package com.application.useCases.bodegas;

import com.google.gson.JsonObject;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.BodegaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Caso de uso para obtener el estado de los negocios activos.
 * <p>
 * Reemplaza: PersonasDao.negociosActivos()
 * Usado en: VentaManualMenuPanel
 */
public class NegociosActivosUseCase {

    private final EntityManagerFactory coreFactory;
    private final EntityManagerFactory registryFactory;

    public NegociosActivosUseCase() {
        this.coreFactory = JpaEntityManagerFactory.INSTANCE.getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
        this.registryFactory = JpaEntityManagerFactory.INSTANCE.getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
    }

    /**
     * Ejecuta el caso de uso para obtener el estado de los negocios activos.
     *
     * @return JsonObject con información de los negocios activos: combustible, canastilla y kiosco.
     */
    public JsonObject execute() {
        EntityManager emCore = coreFactory.createEntityManager();
        EntityManager emRegistry = registryFactory.createEntityManager();

        try {
            BodegaRepository coreRepo = createRepository(emCore);
            BodegaRepository registryRepo = createRepository(emRegistry);

            JsonObject result = new JsonObject();
            result.addProperty("combustible", coreRepo.combustiblesRepository());
            result.addProperty("canastilla", registryRepo.canastillaKioscoRepository("C"));
            result.addProperty("kiosco", registryRepo.canastillaKioscoRepository("K"));
            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener el estado de los negocios activos.", e);
        } finally {
            closeEntityManager(emCore);
            closeEntityManager(emRegistry);
        }
    }

    private BodegaRepository createRepository(EntityManager em) {
        return new BodegaRepository(em);
    }

    private void closeEntityManager(EntityManager em) {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
}
