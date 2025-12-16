package com.application.useCases.gopass;

import com.application.commons.CtWacherParametrosEnum;
import com.application.core.BaseUseCases;
import com.application.useCases.wacherparametros.GetParameterWacherUseCase;
import com.bean.TransaccionGopass;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.GoPassRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;

public class GetTransacionesGoPassUseCase implements BaseUseCases<ArrayList<TransaccionGopass>> {

    private final EntityManagerFactory entityManagerFactory;
    private static GetParameterWacherUseCase getParameterWacherUseCase;

    public GetTransacionesGoPassUseCase() {
        getParameterWacherUseCase = new GetParameterWacherUseCase(CtWacherParametrosEnum.CODIGO.getColumnName());
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public ArrayList<TransaccionGopass> execute() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        String diasStr = getParameterWacherUseCase.execute("LIMITE_REPORTE_GOPASS");
        int dias;
        if(diasStr == null || diasStr.isEmpty()){
            System.out.println("::No se encontro valor de wacherparametros de LIMITE_REPORTE_GOPASS");
            dias = 30;
        } else {
            try {
                dias = Integer.parseInt(diasStr);
            } catch (NumberFormatException e) {
                System.out.println("::Error convirtiendo LIMITE_REPORTE_GOPASS a número, usando valor por defecto");
                dias = 30;
            }
        }
        System.out.println("::Obteniendo valor de wacherparametros de LIMITE_REPORTE_GOPASS: " + dias);
        try {
            GoPassRepository repository = new GoPassRepository(entityManager);
            return repository.getTransaccionesGoPass(dias);
        } catch (Exception e) {
            throw new RuntimeException("Error obteniendo transacciones de gopass: " + e.getMessage());
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}
