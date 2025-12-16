package com.application.useCases.persons;

import com.bean.CredencialBean;
import com.bean.JornadaBean;
import com.bean.PersonaBean;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.PersonaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Caso de uso para verificar si una jornada está iniciada y es la última para una persona.
 * Reemplaza: PersonasDao.isIniciadaAndUltimo(PersonaBean persona, CredencialBean credencial)
 * Usado en: [Especificar dónde se usa, si se conoce]
 */
public class IsIniciadaAndUltimoUseCase {

    private final EntityManagerFactory entityManagerFactory;

    public IsIniciadaAndUltimoUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
    }

    /**
     * Ejecuta el caso de uso para verificar si una jornada está iniciada y es la última.
     * @param persona La persona asociada a la jornada.
     * @param credencial Las credenciales de la persona (no utilizadas en la consulta).
     * @return JornadaBean con los datos de la jornada, o null si no se encuentra.
     */
    public JornadaBean execute(PersonaBean persona, CredencialBean credencial) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            PersonaRepository repository = new PersonaRepository(em);
            return repository.isIniciadaAndUltimoRepository(persona, credencial);
        } catch (Exception ex) {
            throw new RuntimeException("Error al verificar si la jornada está iniciada y es la última", ex);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}