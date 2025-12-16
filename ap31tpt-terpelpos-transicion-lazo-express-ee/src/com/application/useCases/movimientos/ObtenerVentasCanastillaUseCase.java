package com.application.useCases.movimientos;

import com.application.core.BaseUseCases;
import com.domain.entities.CtMovimientoEntity;
import com.infrastructure.repositories.VentasCanastillaRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.database.DatabaseNames;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import org.postgresql.util.PGobject;

/**
 * Caso de uso para obtener las ventas de canastilla
 * Este caso de uso consulta las ventas de canastilla para un rango de fechas
 * y opcionalmente filtradas por promotor
 */
public class ObtenerVentasCanastillaUseCase implements BaseUseCases<JsonObject> {

    private final String fechaInicial;
    private final String fechaFinal;
    private final String promotor;
    private final EntityManagerFactory entityManagerFactory;
    private final JsonParser jsonParser;

    public ObtenerVentasCanastillaUseCase(String fechaInicial, String fechaFinal, String promotor) {
        this.fechaInicial = fechaInicial;
        this.fechaFinal = fechaFinal;
        this.promotor = promotor;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
        this.jsonParser = new JsonParser();
    }

    @Override
    public JsonObject execute() {
        EntityManager entityManager = null;
        JsonObject result = new JsonObject();
        JsonArray info = new JsonArray();

        try {
            entityManager = entityManagerFactory.createEntityManager();
            VentasCanastillaRepository ventasRepository = new VentasCanastillaRepository(entityManager);
            List<PGobject> results = ventasRepository.findVentasCanastilla(fechaInicial, fechaFinal, promotor);
            
            if (results != null && !results.isEmpty()) {
                for (PGobject pgObject : results) {
                    if (pgObject != null && pgObject.getValue() != null) {
                        info = jsonParser.parse(pgObject.getValue()).getAsJsonArray();
                    }
                }
            }
            
            result.add("data", info);
            
        } catch (Exception e) {
            System.err.println("Error in ObtenerVentasCanastillaUseCase: " + e.getMessage());
            result.addProperty("error", e.getMessage());
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }

        return result;
    }
}
