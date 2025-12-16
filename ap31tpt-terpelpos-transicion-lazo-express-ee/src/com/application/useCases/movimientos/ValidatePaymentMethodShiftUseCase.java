package com.application.useCases.movimientos;

/*
public boolean validarTurnoMedioPago(long idMovimiento) {
    boolean existe = false;
    Connection conexion = Main.obtenerConexion("lazoexpresscore");
    try {
        String sql = "select cm.id\n" + "from ct_movimientos cm\n" + "inner join jornadas j\n" + "on j.grupo_jornada = cm.jornadas_id\n" + "where cm.id = ?";
        PreparedStatement ps = conexion.prepareStatement(sql);
        ps.setLong(1, idMovimiento);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            existe = true;
        }
    } catch (SQLException s) {
        NovusUtils.printLn(s.getMessage());
    }
    return existe;
*/


import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import com.application.core.BaseUseCases;

public class ValidatePaymentMethodShiftUseCase implements BaseUseCases<Boolean> {
   

    private final EntityManagerFactory entityManagerFactory;
    private final long idMovimiento;

    public ValidatePaymentMethodShiftUseCase(long idMovimiento) {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
        this.idMovimiento = idMovimiento;
    }

    public Boolean execute() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            String sql = "select cm.id from ct_movimientos cm " +
                        "inner join jornadas j on j.grupo_jornada = cm.jornadas_id " +
                        "where cm.id = ?1";

            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, idMovimiento);

            return !query.getResultList().isEmpty();
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

}
