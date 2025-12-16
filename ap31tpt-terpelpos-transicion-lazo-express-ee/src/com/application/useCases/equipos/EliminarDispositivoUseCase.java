package com.application.useCases.equipos;

import com.application.core.BaseUseCases;
import com.domain.entities.DispositivoEntity;
import com.infrastructure.repositories.DispositivoRepository;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.database.DatabaseNames;
import java.util.Optional;

/*public void eliminarDispositivo(int id) {
    try {

        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "delete from dispositivos where id=?;";
        PreparedStatement ps;
        ps = conexion.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();

    } catch (SQLException ex) {
        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    } catch (Exception ex) {
        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    }
}*/
public class EliminarDispositivoUseCase implements BaseUseCases<Boolean> {

    private final EntityManagerFactory entityManagerFactory;
    private final int id;

    public EliminarDispositivoUseCase(int id) {
        this.id = id;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Boolean execute() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;
        
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            
            DispositivoRepository dispositivoRepository = new DispositivoRepository(entityManager);
            Optional<DispositivoEntity> dispositivoOpt = dispositivoRepository.findById(id);
            
            if (dispositivoOpt.isPresent()) {
                DispositivoEntity dispositivo = dispositivoOpt.get();
                dispositivoRepository.delete(dispositivo);
                
                transaction.commit();
                return true;
            } else {
                transaction.rollback();
                return false;
            }
            
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
} 