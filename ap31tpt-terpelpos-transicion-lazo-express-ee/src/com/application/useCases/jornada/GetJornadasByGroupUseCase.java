package com.application.useCases.jornada;

import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.repositories.JornadaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Caso de uso para obtener cualquier grupo de jornada de la tabla jornadas.
 * Reemplaza: MovimientosDao.getJornadaIdCore()
 * Usado para obtener un grupo_jornada sin filtros específicos
 */
public class GetJornadasByGroupUseCase {

    private final EntityManagerFactory entityManagerFactory;

    public GetJornadasByGroupUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    /**
     * Ejecuta la consulta para obtener cualquier grupo_jornada
     * @return grupo_jornada encontrado o 0 si no hay registros o hay error
     */
    public long execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            JornadaRepository repository = new JornadaRepository(em);
            return repository.findAnyGrupoJornada();
        } catch (Exception ex) {
            // Retorna 0 en caso de error, manteniendo la compatibilidad
            // con el comportamiento original
            return 0L;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}
