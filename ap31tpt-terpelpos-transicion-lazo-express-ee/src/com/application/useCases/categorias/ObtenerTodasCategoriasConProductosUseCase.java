package com.application.useCases.categorias;

import com.application.core.BaseUseCases;
import com.bean.CategoriaBean;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.GrupoRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

/**
 * Caso de uso para obtener todas las categorías con productos activos.
 * Reemplaza: CategoriasDao.findAllCategorias()
 * Utiliza la consulta SqlQueryEnum.OBTENER_TODAS_CATEGORIAS_CON_PRODUCTOS
 */
public class ObtenerTodasCategoriasConProductosUseCase implements BaseUseCases<List<CategoriaBean>> {

    private final EntityManagerFactory entityManagerFactory;

    public ObtenerTodasCategoriasConProductosUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
    }

    @Override
    public List<CategoriaBean> execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            GrupoRepository repository = new GrupoRepository(em);
            return repository.obtenerTodasCategoriasConProductos();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
} 