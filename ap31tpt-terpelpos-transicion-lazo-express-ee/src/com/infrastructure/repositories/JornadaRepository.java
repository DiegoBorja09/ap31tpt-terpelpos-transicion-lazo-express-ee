package com.infrastructure.repositories;

import com.domain.entities.JornadaEntity;
import com.infrastructure.core.BaseRepositoryInterface;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import com.dao.DAOException;
import com.infrastructure.Enums.SqlQueryEnum;
import javax.persistence.Query;

public class JornadaRepository implements BaseRepositoryInterface<JornadaEntity> {

    private final EntityManager entityManager;

    public JornadaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public JornadaEntity save(JornadaEntity entity) {
        try {
            entityManager.persist(entity);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Error saving jornada", e);
        }
    }

    @Override
    public JornadaEntity update(JornadaEntity entity) {
        try {
            return entityManager.merge(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error updating jornada", e);
        }
    }

    @Override
    public void delete(JornadaEntity entity) {
        try {
            entityManager.remove(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting jornada", e);
        }
    }

    @Override
    public Optional<JornadaEntity> findById(Object id) {
        try {
            JornadaEntity jornada = entityManager.find(JornadaEntity.class, id);
            return Optional.ofNullable(jornada);
        } catch (Exception e) {
            throw new RuntimeException("Error finding jornada by id", e);
        }
    }

    @Override
    public List<JornadaEntity> findAll() {
        try {
            TypedQuery<JornadaEntity> query = entityManager.createQuery("SELECT j FROM JornadaEntity j", JornadaEntity.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all jornadas", e);
        }
    }

    @Override
    public List<JornadaEntity> findByQuery(String jpql, Object... parameters) {
        try {
            TypedQuery<JornadaEntity> query = entityManager.createQuery(jpql, JornadaEntity.class);
            for (int i = 0; i < parameters.length; i++) {
                query.setParameter(i + 1, parameters[i]);
            }
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error executing custom query", e);
        }
    }

    @Override
    public List<?> findByNativeQuery(String sql, Object... parameters) {
        try {
            javax.persistence.Query query = entityManager.createNativeQuery(sql, JornadaEntity.class);
            for (int i = 0; i < parameters.length; i++) {
                query.setParameter(i + 1, parameters[i]);
            }
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error executing native query", e);
        }
    }

    // Métodos específicos para Jornada
    public List<JornadaEntity> findByPersonaId(Long personaId) {
        try {
            String jpql = "SELECT j FROM JornadaEntity j WHERE j.persona.id = :personaId";
            TypedQuery<JornadaEntity> query = entityManager.createQuery(jpql, JornadaEntity.class);
            query.setParameter("personaId", personaId);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding jornadas by persona id", e);
        }
    }
    // Metodo para traer el gripo de la jornada
    public Long findGrupoJornadaByPersonaId(Long personaId) {
        try {
            String sql = SqlQueryEnum.OBTENER_JORNADA.getQuery();
            List<Long> result = entityManager.createQuery(sql, Long.class)
                    .setParameter("personaId", personaId)
                    .getResultList();
            return result.isEmpty() ? -1L : result.get(0);
        } catch (Exception e) {
            throw new RuntimeException("Error finding grupo_jornada by persona id", e);
        }
    }

    /**
     * Obtiene cualquier grupo_jornada de la tabla jornadas sin filtros específicos
     * Reemplaza: MovimientosDao.getJornadaIdCore()
     * @return grupo_jornada encontrado o 0 si no hay registros
     */
    public Long findAnyGrupoJornada() {
        try {
            String sql = SqlQueryEnum.OBTENER_CUALQUIER_JORNADA.getQuery();
            List<Long> result = entityManager.createNativeQuery(sql)
                    .getResultList();
            return result.isEmpty() ? 0L : ((Number) result.get(0)).longValue();
        } catch (Exception e) {
            // Retorna 0 en caso de error, manteniendo compatibilidad
            return 0L;
        }
    }

    public List<JornadaEntity> findActivasByPersonaId(Long personaId) {
        try {
            String jpql = "SELECT j FROM JornadaEntity j WHERE j.persona.id = :personaId AND j.fechaFin IS NULL";
            TypedQuery<JornadaEntity> query = entityManager.createQuery(jpql, JornadaEntity.class);
            query.setParameter("personaId", personaId);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding active jornadas by persona id", e);
        }
    }

    /**
     * Obtiene todas las jornadas activas con información de la persona y su tipo de identificación
     * @return Lista de Object[] con datos de jornadas activas
     * @throws DAOException si ocurre un error al acceder a la base de datos
     */
    public List<Object[]> findAllActiveJornadasWithPersonInfo() throws DAOException {
        try {
            String nativeSql = "select " +
                    "J.*, " +
                    "p.nombre nombres, " +
                    "p.identificacion, " +
                    "ti.descripcion " +
                    "from " +
                    "jornadas J " +
                    "inner join PERSONAS P on " +
                    "P.ID = PERSONAS_ID " +
                    "inner join tipos_identificaciones ti on " +
                    "ti.id = p.tipos_identificacion_id " +
                    "where " +
                    "fecha_fin is null";

            
            return entityManager.createNativeQuery(nativeSql)
                              .getResultList();
        } catch (Exception e) {
            throw new DAOException("Error al obtener las jornadas activas con información de persona", e);
        }
    }

    /**
     * Obtiene el último grupo_jornada de la tabla jornadas
     * Migración JPA del método jornadaId() de RumboDao
     * @return grupo_jornada como long, 0 si no se encuentra
     */
    public long obtenerJornadaId() {
        String sql = SqlQueryEnum.OBTENER_JORNADA_ID.getQuery();

        Query nativeQuery = entityManager.createNativeQuery(sql);

        @SuppressWarnings("unchecked")
        List<Object> resultados = nativeQuery.getResultList();

        if (!resultados.isEmpty()) {
            // Itera hasta el último resultado (mantiene la lógica original)
            Object ultimoGrupoJornada = null;
            for (Object resultado : resultados) {
                ultimoGrupoJornada = resultado;
            }
            
            if (ultimoGrupoJornada instanceof Number) {
                return ((Number) ultimoGrupoJornada).longValue();
            }
        }

        return 0L; // Retorna 0 si no encuentra jornadas
    }
} 