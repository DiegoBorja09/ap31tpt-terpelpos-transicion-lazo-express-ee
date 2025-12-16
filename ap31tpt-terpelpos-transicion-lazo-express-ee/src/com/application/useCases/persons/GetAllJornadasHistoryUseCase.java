package com.application.useCases.persons;

import com.bean.PersonaBean;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.PersonaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;

/**
 * Caso de uso para obtener el historial completo de jornadas.
 * Reemplaza: PersonasDao.getAllJornadasHistory()
 * Usado en: HistorialCierreView.mostarTurnos
 */
public class GetAllJornadasHistoryUseCase {

    private final EntityManagerFactory entityManagerFactory;

    public GetAllJornadasHistoryUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    /**
     * Ejecuta el caso de uso para obtener el historial completo de jornadas.
     * @return ArrayList de PersonaBean con el historial de jornadas.
     */
    public ArrayList<PersonaBean> execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            PersonaRepository repository = new PersonaRepository(em);
            return repository.getAllJornadasHistoryRepository();
        } catch (Exception ex) {
            throw new RuntimeException("Error al obtener el todo el historial de la jornada", ex);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}