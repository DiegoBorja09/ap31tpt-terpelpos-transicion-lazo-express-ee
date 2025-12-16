 /*
    * este metodo se remplaza por llamado directo de caso de uso ActualizarMediosPagoUseCase
    * este metodo actualiza el medio de pago en la tabla ct_movimientos_medios_pagos
    * se actualiza el medio de pago con el id de la tabla medios_pagos
    * se actualiza el numero de comprobante con el id del movimiento
    * se actualiza el medio de pago con el id de la tabla medios_pagos
    public boolean actualizarMediosPago(int id, String gopassID) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        boolean actualizado = false;
        try {
            String sql = "UPDATE ct_movimientos_medios_pagos "
                    + "SET ct_medios_pagos_id=(select id from medios_pagos mp where mp.mp_atributos::json->>'codigoExterno' notnull and mp.mp_atributos::json->>'codigoExterno'='238' limit 1), numero_comprobante=?"
                    + " WHERE ct_movimientos_id=?;";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, gopassID);
            ps.setInt(2, id);
            actualizado = ps.executeUpdate() != 0;
        } catch (PSQLException s) {
            throw new DAOException("31." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("32." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("33." + s.getMessage());
        }
        return actualizado;
    }
    */


package com.application.useCases.gopass;

import com.application.core.BaseUseCases;
import com.dao.DAOException;
import com.firefuel.Main;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.postgresql.util.PSQLException;
import com.infrastructure.Enums.SqlQueryEnum;

public class ActualizarMediosPagoUseCase implements BaseUseCases<Boolean> {

    private final EntityManagerFactory entityManagerFactory;
    private final Integer id;
    private final String gopassId;

    public ActualizarMediosPagoUseCase(Integer id, String gopassId) {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
        this.id = id;
        this.gopassId = gopassId;
    }

    @Override
    public Boolean execute() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            String sql = SqlQueryEnum.ACTUALIZAR_MEDIOS_PAGO.getQuery();
            return entityManager.createNativeQuery(sql)
                    .setParameter(1, gopassId)
                    .setParameter(2, id)
                    .executeUpdate() != 0;
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando medios de pago: " + e.getMessage());
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
   
}
