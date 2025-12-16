package com.application.useCases.persons;

import com.application.core.BaseUseCases;
import com.domain.entities.CtPerson;
import com.infrastructure.repositories.CtPersonRepository;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.database.DatabaseNames;
import java.util.List;
import java.util.Objects;

/**
 * Caso de uso para recuperar todas las personas de la base de datos.
 * Implementa la interfaz BaseUseCases para garantizar un contrato estándar.
 */
public class FindAllPersonsUseCase implements BaseUseCases<List<CtPerson>> {

    private final CtPersonRepository personRepository;
    private final EntityManagerFactory entityManagerFactory;

    /**
     * Constructor por defecto que inicializa la factoría de EntityManager usando la configuración predeterminada.
     */
    public FindAllPersonsUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
        this.personRepository = null; // Se instanciará en execute si es necesario
    }

    /**
     * Constructor que inicializa el caso de uso con una factoría de EntityManager.
     * @param entityManagerFactory Factoría de EntityManager para acceder a la base de datos.
     * @throws IllegalArgumentException si entityManagerFactory es nulo.
     */
    public FindAllPersonsUseCase(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = Objects.requireNonNull(entityManagerFactory, "EntityManagerFactory cannot be null");
        this.personRepository = null; // Se instanciará en execute si es necesario
    }

    /**
     * Constructor alternativo para inyección directa del repositorio.
     * Útil para configuraciones personalizadas o pruebas manuales.
     * @param personRepository Repositorio de personas.
     * @throws IllegalArgumentException si personRepository es nulo.
     */
    public FindAllPersonsUseCase(CtPersonRepository personRepository) {
        this.personRepository = Objects.requireNonNull(personRepository, "CtPersonRepository cannot be null");
        this.entityManagerFactory = null; // No se usa si se inyecta el repositorio
    }

    /**
     * Ejecuta el caso de uso para obtener todas las personas.
     * @return Lista de entidades CtPerson.
     * @throws RuntimeException si ocurre un error al acceder a la base de datos.
     */
    @Override
    public List<CtPerson> execute() {
        EntityManager entityManager = null;
        CtPersonRepository repository = personRepository;
        try {
            // Si no se inyectó un repositorio, crear uno con un nuevo EntityManager
            if (repository == null && entityManagerFactory != null) {
                entityManager = entityManagerFactory.createEntityManager();
                repository = new CtPersonRepository(entityManager);
            }
            return repository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving persons from database", e);
        } finally {
            // Cerrar EntityManager si fue creado en este método
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}