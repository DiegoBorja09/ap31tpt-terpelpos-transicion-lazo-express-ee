package com.application.useCases.equipos;

import com.application.core.BaseUseCases;
import com.domain.entities.EquipoEntity;
import com.infrastructure.repositories.EquipoRepository;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.database.DatabaseNames;
import java.util.Optional;
/*public String getMacEquipo(long equipoId) {
    String mac = "NO TIENE";
    try {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        if (conexion != null) {
            String sql = "SELECT MAC FROM EQUIPOS WHERE ID= ?;";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, equipoId);
            ResultSet re = ps.executeQuery();
            if (re.next()) {
                mac = re.getString("MAC");
            }
        }
    } catch (SQLException e) {
    }
    return mac;
}*/
public class GetMacEquipoUseCase implements BaseUseCases<String> {

    private final EntityManagerFactory entityManagerFactory;
    private final long equipoId;

    public GetMacEquipoUseCase(long equipoId) {
        this.equipoId = equipoId;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
    }

    @Override
    public String execute() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        
        try {
            // Opción 1: Usando el repositorio completo
            EquipoRepository equipoRepository = new EquipoRepository(entityManager);
            Optional<EquipoEntity> equipoOpt = equipoRepository.findById(equipoId);
            
            if (equipoOpt.isPresent()) {
                String mac = equipoOpt.get().getMac();
                return mac != null ? mac : "NO TIENE";
            }
            
            return "NO TIENE";
            
        } catch (Exception e) {
            return "NO TIENE";
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
    
    // Alternativa usando consulta JPQL directa (más eficiente)
    /*public String executeWithQuery() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        
        try {
            TypedQuery<String> query = entityManager.createQuery(
                "SELECT e.mac FROM EquipoEntity e WHERE e.id = :equipoId", String.class);
            query.setParameter("equipoId", equipoId);
            
            String mac = query.getSingleResult();
            return mac != null ? mac : "NO TIENE";
            
        } catch (Exception e) {
            return "NO TIENE";
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }*/
} 