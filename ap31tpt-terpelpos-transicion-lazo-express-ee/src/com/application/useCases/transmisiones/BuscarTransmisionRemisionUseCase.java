package com.application.useCases.transmisiones;

import com.application.core.BaseUseCases;
import com.google.gson.JsonArray;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.TransmisionRemisionRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
/**
 * Caso de uso para buscar la TransmisionRemision.
 * Reemplaza: MovimientosDao.buscarTransminionRemision()
 * Usado en: VentasFE.
 */
public class BuscarTransmisionRemisionUseCase implements BaseUseCases<JsonArray> {

    private final int sincronizado;
    private final String queryExtra;
    private final EntityManagerFactory entityManagerFactory;

    public BuscarTransmisionRemisionUseCase(int sincronizado, String queryExtra) {
        this.sincronizado = sincronizado;
        this.queryExtra = queryExtra;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public JsonArray execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            TransmisionRemisionRepository repository = new TransmisionRemisionRepository(em);
            return repository.buscarTransmisionRemision(sincronizado, queryExtra);
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}
