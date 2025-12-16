package com.application.useCases.equipos;

import com.application.core.BaseUseCases;
import com.domain.entities.DispositivoEntity;
import com.infrastructure.repositories.DispositivoRepository;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.database.DatabaseNames;
import java.util.List;

/*public boolean isValidCOM(String conector) {
    boolean result;
    Connection conexion = Main.obtenerConexion("lazoexpresscore");
    try {
        String sql = "select id from dispositivos where conector = ?";
        PreparedStatement ps = conexion.prepareStatement(sql);
        ps.setString(1, conector);
        ResultSet rs = ps.executeQuery();
        result = rs.next();
    } catch (SQLException e) {
        result = false;
        System.out.println(e.getMessage());
    }
    return result;
}*/
public class IsValidComUseCase implements BaseUseCases<Boolean> {

    private final EntityManagerFactory entityManagerFactory;
    private final String conector;

    public IsValidComUseCase(String conector) {
        this.conector = conector;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Boolean execute() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        
        try {
            DispositivoRepository dispositivoRepository = new DispositivoRepository(entityManager);
            List<DispositivoEntity> dispositivos = dispositivoRepository.findByQuery(
                "SELECT d FROM DispositivoEntity d WHERE d.conector = ?1", conector);
            
            // Retorna true si encuentra al menos un dispositivo, false si no encuentra ninguno
            return !dispositivos.isEmpty();
            
        } catch (Exception e) {
            return false;
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
} 