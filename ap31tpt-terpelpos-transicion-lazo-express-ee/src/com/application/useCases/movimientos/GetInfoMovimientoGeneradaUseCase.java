package com.application.useCases.movimientos;

import com.application.core.BaseUseCases;
import com.google.gson.JsonObject;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import com.firefuel.Main;

/*public JsonObject infoMovimientoGenerada(long idMovimiento) {
        JsonObject infoGenerada = new JsonObject();
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSREGISTRY);
        String sql = "select * from fnc_obtener_movimiento_transaccion(?) respuesta;";
        try ( PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setLong(1, idMovimiento);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                infoGenerada = Main.gson.fromJson(rs.getString("respuesta"), JsonObject.class);
            }
        } catch (SQLException e) {
            Logger.getLogger(SincronizadorMarketCanDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return infoGenerada;
    }*/

public class GetInfoMovimientoGeneradaUseCase implements BaseUseCases<JsonObject> {

    private final EntityManagerFactory entityManagerFactory;
    private final Long idMovimiento;

    public GetInfoMovimientoGeneradaUseCase(Long idMovimiento) {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
        this.idMovimiento = idMovimiento;
    }

    @Override
    public JsonObject execute() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            String sql = "select * from fnc_obtener_movimiento_transaccion(?) respuesta";
            String result = (String) entityManager.createNativeQuery(sql)
                    .setParameter(1, idMovimiento)
                    .getSingleResult();
            return Main.gson.fromJson(result, JsonObject.class);
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}