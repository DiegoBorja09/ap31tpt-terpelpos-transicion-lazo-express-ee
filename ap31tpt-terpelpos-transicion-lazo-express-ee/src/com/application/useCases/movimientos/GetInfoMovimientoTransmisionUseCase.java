package com.application.useCases.movimientos;

import com.application.core.BaseUseCases;
import com.google.gson.JsonObject;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import com.firefuel.Main;

/*public JsonObject infoMovimientoTransmision(String prefijo, long consecutivo) {
        JsonObject infoMovimiento = new JsonObject();
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSREGISTRY);
        String sql = "select * from public.fnc_consultar_movimiento_transmision(?,?) respuesta;";
        try ( PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setString(1, prefijo);
            pst.setLong(2, consecutivo);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                infoMovimiento = Main.gson.fromJson(rs.getString("respuesta"), JsonObject.class);
            }
        } catch (SQLException e) {
            Logger.getLogger(SincronizadorMarketCanDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return infoMovimiento;
    }*/

public class GetInfoMovimientoTransmisionUseCase implements BaseUseCases<JsonObject> {

    private final EntityManagerFactory entityManagerFactory;
    private final String prefijo;
    private final Long consecutivo;

    public GetInfoMovimientoTransmisionUseCase(String prefijo, Long consecutivo) {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
        this.prefijo = prefijo;
        this.consecutivo = consecutivo;
    }

    @Override
    public JsonObject execute() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            String sql = "select * from public.fnc_consultar_movimiento_transmision(?,?) respuesta";
            String result = (String) entityManager.createNativeQuery(sql)
                    .setParameter(1, prefijo)
                    .setParameter(2, consecutivo)
                    .getSingleResult();
            return Main.gson.fromJson(result, JsonObject.class);
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}