package com.infrastructure.repositories;

import com.domain.entities.EquipoEntity;
import com.infrastructure.core.BaseRepositoryInterface;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class EquipoRepository implements BaseRepositoryInterface<EquipoEntity> {
    private final EntityManager entityManager;

    public EquipoRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public EquipoEntity save(EquipoEntity entity) {
        try {
            entityManager.persist(entity);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Error saving equipo", e);
        }
    }

    @Override
    public EquipoEntity update(EquipoEntity entity) {
        try {
            return entityManager.merge(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error updating equipo", e);
        }
    }

    @Override
    public void delete(EquipoEntity entity) {
        try {
            entityManager.remove(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting equipo", e);
        }
    }

    @Override
    public Optional<EquipoEntity> findById(Object id) {
        try {
            EquipoEntity equipo = entityManager.find(EquipoEntity.class, id);
            return Optional.ofNullable(equipo);
        } catch (Exception e) {
            throw new RuntimeException("Error finding equipo by id", e);
        }
    }

    @Override
    public List<EquipoEntity> findAll() {
        try {
            TypedQuery<EquipoEntity> query = entityManager.createQuery("SELECT e FROM EquipoEntity e", EquipoEntity.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all equipos", e);
        }
    }

    @Override
    public List<EquipoEntity> findByQuery(String jpql, Object... parameters) {
        try {
            TypedQuery<EquipoEntity> query = entityManager.createQuery(jpql, EquipoEntity.class);
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
        return List.of();
    }

    
    public boolean cierreDeTurno() {
        try {
            String sql = "update cierre_turno_excepciones "
                    + " set query = 'select count(*) from datafonos.transacciones t"
                    + " where id_transaccion_estado in (1,5,8)' , ind_activo = 1 "
                    + " where id_cierre_turno_excepcion  = 6";
            
            int updated = entityManager.createNativeQuery(sql).executeUpdate();
            return updated == 1;
            
        } catch (Exception e) {
            System.err.println("❌ Error actualizando funcion cierre de turno: " + e.getMessage());
            return false;
        }
    }

    public boolean cierreDeTurnoConDatafono() {
        try {
            String sql = "UPDATE cierre_turno_excepciones\n"
                    + "SET query = 'SELECT CASE WHEN negocio = ''KCO'' THEN ''KIOSCO'' WHEN negocio = ''CAN'' THEN ''CANASTILLA'' WHEN negocio = ''KCO-WEB'' THEN ''POS MOVIL'' WHEN negocio = ''DATAFONO'' THEN ''DATAFONO'' END AS negocio FROM lt_ventas_curso;',\n"
                    + "    ind_activo = 1\n"
                    + "WHERE id_cierre_turno_excepcion = 3;";
            int updated = entityManager.createNativeQuery(sql).executeUpdate();
            return updated == 1;
        } catch (Exception e) {
            System.err.println("❌ Error actualizando funcion cierre de turno con datafono: " + e.getMessage());
            return false;
        }
    }
}