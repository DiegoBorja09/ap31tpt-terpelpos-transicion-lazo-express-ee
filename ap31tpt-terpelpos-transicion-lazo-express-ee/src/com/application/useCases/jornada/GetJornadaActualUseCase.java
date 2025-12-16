package com.application.useCases.jornada;

import com.application.core.BaseUseCases;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/*public Long getJornadaActual() {
        long jornada = 0;
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "select * from public.fnc_consultar_jornada_activa() jornada;";
        try ( PreparedStatement pst = conexion.prepareStatement(sql)) {
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                jornada = rs.getLong("jornada");
            }
        } catch (SQLException e) {
            Logger.getLogger(SincronizadorMarketCanDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return jornada;
    }*/

public class GetJornadaActualUseCase implements BaseUseCases<Long> {

    private final EntityManagerFactory entityManagerFactory;

    public GetJornadaActualUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Long execute() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            String sql = "select * from public.fnc_consultar_jornada_activa() jornada";
            return (Long) entityManager.createNativeQuery(sql)
                    .getSingleResult();
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}