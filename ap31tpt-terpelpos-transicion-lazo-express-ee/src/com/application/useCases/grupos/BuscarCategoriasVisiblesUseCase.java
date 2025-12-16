package com.application.useCases.grupos;

import com.application.core.BaseUseCases;
import com.bean.CatalogoBean;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.GrupoRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

/**
 * Caso de uso para consultar las categorías visibles desde la tabla "grupos".
 * Realiza la consulta a través de una sentencia SQL nativa que filtra por `atributos->>'visible' = 'true'`.
 * Reemplaza: MovimientosDao.buscarListaBasicaCategorias()
 * Usado en: ServerComandoWS dentro del handler HttpHandlerCategorias
 */

public class BuscarCategoriasVisiblesUseCase implements BaseUseCases<List<CatalogoBean>> {

    private final EntityManagerFactory entityManagerFactory;

    public BuscarCategoriasVisiblesUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
    }

    public List<CatalogoBean> execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            GrupoRepository repository = new GrupoRepository(em);
            return repository.buscarCategoriasVisibles();
        } finally {
            em.close();
        }
    }
}
