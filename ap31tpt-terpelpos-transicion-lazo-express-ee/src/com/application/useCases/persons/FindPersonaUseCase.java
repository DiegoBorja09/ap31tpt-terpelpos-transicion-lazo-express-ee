package com.application.useCases.persons;

import com.bean.PersonaBean;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.PersonaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Caso de uso para buscar una persona por identificación, PIN y origen RFID.
 * Reemplaza: PersonasDao.findPersona(String identificacion, int pin, boolean fromRFID)
 * Usado en: [Especificar dónde se usa, si se conoce]
 */
public class FindPersonaUseCase {

    private final EntityManagerFactory entityManagerFactory;

    public FindPersonaUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
    }

    /**
     * Ejecuta el caso de uso para buscar una persona.
     * @param identificacion La identificación de la persona.
     * @param pin El PIN del usuario.
     * @param fromRFID Indica si la búsqueda es por RFID.
     * @return PersonaBean con los datos de la persona, o null si no se encuentra.
     */
    public PersonaBean execute(String identificacion, int pin, boolean fromRFID) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            PersonaRepository repository = new PersonaRepository(em);
            return repository.findPersonaRepository(identificacion, pin, fromRFID);
        } catch (Exception ex) {
            throw new RuntimeException("Error al buscar la persona", ex);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}