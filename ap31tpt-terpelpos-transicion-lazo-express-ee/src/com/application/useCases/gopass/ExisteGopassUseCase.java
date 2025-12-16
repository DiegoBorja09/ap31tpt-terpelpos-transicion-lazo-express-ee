package com.application.useCases.gopass;

import com.application.core.BaseUseCases;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.MedioPagoRepository;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.logging.Logger;
import java.util.logging.Level;

/*
 public boolean existeGopass() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        boolean exist = false;
        try {
            String sql = "select count(*) from medios_pagos mp \n"
                    + "where mp.mp_atributos::json->>'codigoExterno' is not null \n"
                    + "and mp.mp_atributos::json->>'codigoExterno'='238' \n"
                    + "limit 1";
            PreparedStatement ps = conexion.prepareStatement(sql);
            exist = ps.executeQuery().next();
        } catch (PSQLException s) {
            throw new DAOException("31." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("32." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("33." + s.getMessage());
        }
        return exist;
    }
    */

public class ExisteGopassUseCase implements BaseUseCases<Boolean> {

    private static final Logger LOGGER = Logger.getLogger(ExisteGopassUseCase.class.getName());
    private final EntityManagerFactory entityManagerFactory;
    

    public ExisteGopassUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Boolean execute() {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            MedioPagoRepository medioPagoRepository = new MedioPagoRepository(entityManager);
            return medioPagoRepository.existeGopass();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al verificar existencia de GoPass", e);
            return false;
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}
