package com.application.useCases.sutidores;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.application.core.BaseUseCasesWithParams;
import com.bean.RecepcionBean;
import com.controllers.NovusUtils;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.SurtidorRepository;

public class ActualizarRecepcionCombustibleUseCase implements BaseUseCasesWithParams<RecepcionBean, RecepcionBean> {
    
    private final EntityManagerFactory entityManagerFactory;

    public ActualizarRecepcionCombustibleUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }
    
    @Override
    public RecepcionBean execute(RecepcionBean bean) {
        try {
            if (bean == null) {
                NovusUtils.printLn("Error: El bean de recepción no puede ser nulo");
                return null;
            }

            if (bean.getPromotor() <= 0 || bean.getTanqueId() <= 0 || bean.getProductoId() <= 0) {
                NovusUtils.printLn("Error: Los IDs de promotor, tanque y producto deben ser mayores a cero");
                return null;
            }

            if (bean.getDocumento() == null || bean.getDocumento().trim().isEmpty()) {
                NovusUtils.printLn("Error: El documento no puede estar vacío");
                return null;
            }

            if (bean.getPlaca() == null || bean.getPlaca().trim().isEmpty()) {
                NovusUtils.printLn("Error: La placa no puede estar vacía");
                return null;
            }

            if (bean.getFecha() == null) {
                NovusUtils.printLn("Error: La fecha no puede ser nula");
                return null;
            }

            EntityManager entityManager = entityManagerFactory.createEntityManager();
            SurtidorRepository surtidorRepository = new SurtidorRepository(entityManager);
            return surtidorRepository.actualizarRecepcionCombustible(bean);
            
        } catch (Exception e) {
            NovusUtils.printLn("Error en ActualizarRecepcionCombustibleUseCase: " + e.getMessage());
            return null;
        }
    }
} 