package com.application.useCases.movimientos;

import com.application.core.BaseUseCasesWithParams;
import com.application.useCases.movimientos.dto.CtMovimientoJsonObjDto;
import com.controllers.NovusUtils;
import com.google.gson.JsonObject;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.CtMovimientoRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Use Case para insertar movimientos CT usando función PostgreSQL
 * Migración JPA del método insertCTmovimientos() de RumboDao
 */
/*public boolean insertCTmovimientos(JsonObject jsonMovimientos,
            JsonObject jsonMovimientosCredito,
            JsonObject jsonMovimientoDetalles,
            JsonObject jsonMovimientoMediosPago,
            JsonObject request,
            JsonObject response) {
        boolean insert = false;
        String identificacionAutorizacion = buildjsonIdentificadoresAutorizacion(response);
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "select * from fnc_insertar_ct_movimientos(?::json,?::json,?::json,?::json,?::json,?::json,?::json);";
        try (PreparedStatement psmt = conexion.prepareStatement(sql)) {
            psmt.setString(1, jsonMovimientos.toString());
            psmt.setString(2, jsonMovimientosCredito.toString());
            psmt.setString(3, jsonMovimientoDetalles.toString());
            psmt.setString(4, jsonMovimientoMediosPago.toString());
            psmt.setString(5, request.toString());
            psmt.setString(6, response.toString());
            psmt.setString(7, identificacionAutorizacion);
            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                insert = rs.getBoolean("fnc_insertar_ct_movimientos");
            }
        } catch (SQLException e) {
            NovusUtils.printLn(e.getMessage());
        }
        return insert;
    }*/
public class InsertCtMovimientosUseCase implements BaseUseCasesWithParams<CtMovimientoJsonObjDto, Boolean> {

    private final CtMovimientoRepository ctMovimientoRepository;
    private final EntityManager entityManager;

    public InsertCtMovimientosUseCase() {
        EntityManagerFactory entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
        this.entityManager = entityManagerFactory.createEntityManager();
        this.ctMovimientoRepository = new CtMovimientoRepository(entityManager);
    }

    @Override
    public Boolean execute(CtMovimientoJsonObjDto params) {

        try {
            // Crear la identificación de autorización usando el response
            String identificacionAutorizacion = buildjsonIdentificadoresAutorizacion(params.response);
            // Crear repositorio y ejecutar inserción
            return this.ctMovimientoRepository.insertarCtMovimientos(
                params.jsonMovimientos,
                params.jsonMovimientosCredito,
                params.jsonMovimientoDetalles,
                params.jsonMovimientoMediosPago,
                params.request,
                params.response,
                identificacionAutorizacion
            );
            
        } catch (Exception e) {
            NovusUtils.printLn("Error insertando CT movimientos: " + e.getMessage());
            return false;
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    /**
     * Construye el JSON de identificadores de autorización desde el response
     * Migrado de RumboDao.buildjsonIdentificadoresAutorizacion()
     * @param response JsonObject con datos de respuesta
     * @return String JSON con identificadores de autorización
     */
    private String buildjsonIdentificadoresAutorizacion(JsonObject response) {
        JsonObject object = new JsonObject();
        try {
            JsonObject data = response.get("data").getAsJsonObject();
            object.addProperty("identificadorAprobacion", 
                data.get("identificadorAprobacion").getAsString());
            object.addProperty("identificadorAutorizacionEDS", 
                data.get("identificadorAutorizacionEDS").getAsString());
        } catch (Exception e) {
            NovusUtils.printLn("Error construyendo identificadores de autorización: " + e.getMessage());
            // Retornar objeto vacío en caso de error
            object = new JsonObject();
        }
        return object.toString();
    }
} 