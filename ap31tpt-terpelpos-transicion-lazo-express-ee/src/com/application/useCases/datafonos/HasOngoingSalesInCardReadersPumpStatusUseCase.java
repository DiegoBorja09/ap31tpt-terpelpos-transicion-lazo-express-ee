package com.application.useCases.datafonos;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import com.application.core.BaseUseCases;
import com.controllers.NovusUtils;
import com.google.gson.JsonObject;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;



/**
 * Verifica si hay ventas en curso para un datafono específico.
 *
 * @return Boolean indicando si existen ventas en curso para el datafono, o false si ocurre un error.
 */

public class HasOngoingSalesInCardReadersPumpStatusUseCase implements BaseUseCases<Boolean> {

    private final EntityManagerFactory entityManagerFactory;
    private final JsonObject datafono;
    public HasOngoingSalesInCardReadersPumpStatusUseCase(JsonObject datafono) {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
        this.datafono = datafono;
    }


    @Override
    public Boolean execute() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            String sql = "select * from ventas_curso vc " +
                    "where vc.atributos::json->'datafono'->>'proveedor' = ?1 " +
                    "and vc.atributos::json->'datafono'->>'codigoDatafono' = ?2";
            
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, datafono.get("proveedor").getAsString());
            query.setParameter(2, datafono.get("codigoDatafono").getAsString());

            return !query.getResultList().isEmpty();

        } catch (Exception e) {
            NovusUtils.printLn("Error checking ongoing sales in card readers pump status: " + e.getMessage());
            return false;
        } finally {
            entityManager.close();
        }
    }
}
