package com.application.useCases.productos;

import com.application.core.BaseUseCases;
import com.controllers.NovusUtils;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.ProductoRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class GetProductNameUseCase implements BaseUseCases<String> {

    private final EntityManagerFactory entityManagerFactory;
    private final long id;
    public GetProductNameUseCase(long id) {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
        this.id = id;
    }
    @Override
    public String execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            ProductoRepository productoRepository = new ProductoRepository(em);
            return productoRepository.getProductByName(id);
        } catch (Exception e) {
            NovusUtils.printLn("Error al obtener descripción producto: " + e.getMessage());
            return null;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }


}
