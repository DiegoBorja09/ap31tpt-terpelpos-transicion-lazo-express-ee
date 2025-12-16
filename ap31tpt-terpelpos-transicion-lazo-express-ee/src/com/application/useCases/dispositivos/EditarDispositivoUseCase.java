package com.application.useCases.dispositivos;


import com.application.core.BaseUseCasesWithParams;
import com.dao.DAOException;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.DispositivoRepository;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EditarDispositivoUseCase implements BaseUseCasesWithParams<DispositivoDto, Boolean> {

    

    private static final Logger logger = Logger.getLogger(EditarDispositivoUseCase.class.getName());
    private final EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;
    DispositivoRepository repository ;
    

     

   /*  public EditarDispositivoUseCase(){
        this.entityManagerFactory = Persistence.createEntityManagerFactory("LazoExpressEEPU");

        this.repository = new DispositivoRepository(entityManagerFactory.createEntityManager());
        
    }*/

    public EditarDispositivoUseCase() {
         entityManagerFactory = JpaEntityManagerFactory.INSTANCE
        .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);

        this.entityManager = entityManagerFactory.createEntityManager();
        this.repository = new DispositivoRepository(entityManager);
    }

    @Override
    public Boolean execute(DispositivoDto params) {
        EntityManager entityManager = null;
        try {
            //entityManager = entityManagerFactory.createEntityManager(); 

          //  DispositivoRepository repository = new DispositivoRepository(entityManager);
            try {
                repository.actualizarDispositivo(params);
                logger.log(Level.INFO, "Conexion correcta");
            } catch (DAOException e) {
                logger.log(Level.SEVERE, "Error en el repositorio al actualizar dispositivo: " + e.getMessage(), e);
                return false;
            }

           
            return true;
        } catch (Exception e) {
            
            logger.log(Level.SEVERE, "Error al ejecutar el caso de uso ActualizarDispositivoUseCase: " + e.getMessage(), e);
            return false;
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }
} 