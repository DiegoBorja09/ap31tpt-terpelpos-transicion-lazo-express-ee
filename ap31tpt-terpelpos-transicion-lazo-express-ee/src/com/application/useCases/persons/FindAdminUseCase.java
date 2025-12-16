package com.application.useCases.persons;

import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.PersonaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Caso de uso para encontrar un administrador por usuario, PIN y configuración por defecto.
 * Reemplaza: PersonasDao.findAdmin(String us, String psw, boolean adminDefaultPos)
 * Usado en: [Especificar dónde se usa, si se conoce]
 */
public class FindAdminUseCase {

    private final EntityManagerFactory entityManagerFactoryRegistry;
    private final EntityManagerFactory entityManagerFactoryCore;

    public FindAdminUseCase() {
        this.entityManagerFactoryRegistry = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
        this.entityManagerFactoryCore = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    /**
     * Ejecuta el caso de uso para encontrar un administrador.
     * @param us El nombre de usuario.
     * @param psw El PIN o contraseña del usuario.
     * @param adminDefaultPos Indica si se debe buscar en la base de datos core si no se encuentra en registry.
     * @return int con el ID del tipo de perfil del administrador, o -1 si no se encuentra.
     */
    public int execute(String us, String psw, boolean adminDefaultPos) {
        EntityManager emRegistry = entityManagerFactoryRegistry.createEntityManager();
        EntityManager emCore = adminDefaultPos ? entityManagerFactoryCore.createEntityManager() : null;
        try {
            PersonaRepository repositoryRegistry = new PersonaRepository(emRegistry);
            int id = repositoryRegistry.findAdminRegistryRepository(us, psw);
            if (id == -1 && adminDefaultPos && emCore != null) {
                PersonaRepository repositoryCore = new PersonaRepository(emCore);
                id = repositoryCore.findAdminCoreRepository(us, psw);
            }
            return id;
        } catch (Exception ex) {
            throw new RuntimeException("Error al buscar el administrador", ex);
        } finally {
            if (emRegistry != null && emRegistry.isOpen()) {
                emRegistry.close();
            }
            if (emCore != null && emCore.isOpen()) {
                emCore.close();
            }
        }
    }
}