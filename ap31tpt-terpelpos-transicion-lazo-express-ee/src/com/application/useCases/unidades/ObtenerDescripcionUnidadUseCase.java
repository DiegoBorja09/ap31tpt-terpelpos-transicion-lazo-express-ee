package com.application.useCases.unidades;

import com.application.core.BaseUseCases;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.repositories.UnidadRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Caso de uso para obtener la descripción de una unidad por su ID.
 * Reemplaza: MovimientosDao.unidadProductoDescripcion()
 * Usado en: FacturacionElectronica.
 */
public class ObtenerDescripcionUnidadUseCase implements BaseUseCases<String> {

    private final Long unidadId;
    private final EntityManagerFactory entityManagerFactory;

    public ObtenerDescripcionUnidadUseCase(Long unidadId) {
        this.unidadId = unidadId;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
    }

    @Override
    public String execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            UnidadRepository repository = new UnidadRepository(em);
            return repository.obtenerDescripcionPorId(unidadId);
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}

