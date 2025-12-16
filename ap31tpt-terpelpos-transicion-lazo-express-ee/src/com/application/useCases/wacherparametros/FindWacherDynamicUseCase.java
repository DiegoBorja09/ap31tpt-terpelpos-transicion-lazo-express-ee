package com.application.useCases.wacherparametros;
import com.application.commons.DTO.FindByParameterDTO;
import com.application.core.BaseUseCasesWithParams;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.CtWacherParametroRepository;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class FindWacherDynamicUseCase implements BaseUseCasesWithParams<FindByParameterDTO, Boolean> {

    private final EntityManagerFactory entityManagerFactory;

    public FindWacherDynamicUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Boolean execute(FindByParameterDTO findByParameterDTO) {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CtWacherParametroRepository ctWacherParametroRepository = new CtWacherParametroRepository(entityManager);
            return ctWacherParametroRepository.findByParameter(findByParameterDTO.getParameterColumn(), findByParameterDTO.getValue())
                    .map(parametro -> "S".equals(parametro.getValor()))
                    .orElse(false);
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}
