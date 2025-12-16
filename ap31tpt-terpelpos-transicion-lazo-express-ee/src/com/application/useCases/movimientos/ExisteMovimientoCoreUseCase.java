package com.application.useCases.movimientos;

import com.application.core.BaseUseCases;
import com.domain.entities.CtMovimientoEntity;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;

/*public boolean existeMovimientoCore(long idMovimiento) {
        boolean existe = false;
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "select true existe from ct_movimientos cm where cm.remoto_id = ?";
        try ( PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setLong(1, idMovimiento);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                existe = rs.getBoolean("existe");
            }
        } catch (SQLException e) {
            Logger.getLogger(SincronizadorMarketCanDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return existe;
    }*/

public class ExisteMovimientoCoreUseCase implements BaseUseCases<Boolean> {

    private final EntityManagerFactory entityManagerFactory;
    private final Long idMovimiento;

    public ExisteMovimientoCoreUseCase(Long idMovimiento) {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
        this.idMovimiento = idMovimiento;
    }

    @Override
    public Boolean execute() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            String sql = "select true existe from ct_movimientos cm where cm.remoto_id = ?";
            return (Boolean) entityManager.createNativeQuery(sql)
                    .setParameter(1, idMovimiento)
                    .getSingleResult();
        } catch (NoResultException e) {
            return false;
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}