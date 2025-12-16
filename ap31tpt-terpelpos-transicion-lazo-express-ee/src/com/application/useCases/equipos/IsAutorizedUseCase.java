package com.application.useCases.equipos;

import com.application.core.BaseUseCases;
import com.domain.entities.EquipoEntity;
import com.infrastructure.repositories.EquipoRepository;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.database.DatabaseNames;
import java.util.Optional;

/*public boolean isAutorized(long equipoId) {
    boolean autorizado = false;
    try {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "SELECT autorizado FROM EQUIPOS WHERE ID= ?;";
        PreparedStatement ps = conexion.prepareStatement(sql);
        ps.setLong(1, equipoId);
        ResultSet re = ps.executeQuery();
        if (re.next()) {
            autorizado = re.getString("autorizado").equals("S");
        }
    } catch (SQLException e) {
    }

    return autorizado;
}*/
public class IsAutorizedUseCase implements BaseUseCases<Boolean> {

    private final EntityManagerFactory entityManagerFactory;
    private final long equipoId;

    public IsAutorizedUseCase(long equipoId) {
        this.equipoId = equipoId;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Boolean execute() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        
        try {
            EquipoRepository equipoRepository = new EquipoRepository(entityManager);
            Optional<EquipoEntity> equipoOpt = equipoRepository.findById(equipoId);
            
            if (equipoOpt.isPresent()) {
                String autorizado = equipoOpt.get().getAutorizado();
                return autorizado != null && autorizado.equals("S");
            }
            
            return false;
            
        } catch (Exception e) {
            return false;
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
} 