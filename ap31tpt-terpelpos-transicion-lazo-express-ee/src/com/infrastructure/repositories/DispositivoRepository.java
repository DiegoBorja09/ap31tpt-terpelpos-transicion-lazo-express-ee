package com.infrastructure.repositories;

import com.domain.entities.DispositivoEntity;
import com.infrastructure.core.BaseRepositoryInterface;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

import com.application.useCases.dispositivos.DispositivoDto;
import com.dao.DAOException;

public class DispositivoRepository implements BaseRepositoryInterface<DispositivoEntity> {

    private final EntityManager entityManager;

    public DispositivoRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public DispositivoEntity save(DispositivoEntity entity) {
        try {
            // Usar INSERT nativo para castear atributos a json apropiadamente
            javax.persistence.Query q = entityManager.createNativeQuery(
                    "INSERT INTO dispositivos (tipos, conector, interfaz, estado, d_atributos) VALUES (?, ?, ?, ?, ?::json) RETURNING id");

            q.setParameter(1, entity.getTipos());
            q.setParameter(2, entity.getConector());
            q.setParameter(3, entity.getInterfaz());
            q.setParameter(4, entity.getEstado());
            String jsonSanitized = com.utils.JsonSanitizer.escapeInvalidControlChars(entity.getAtributos());
            q.setParameter(5, jsonSanitized);

            Object result = q.getSingleResult();
            if (result != null) {
                entity.setId(Integer.valueOf(result.toString()));
            }
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Error saving dispositivo", e);
        }
    }

    /**
     * Inserta usando SQL nativo para castear d_atributos a JSON explícitamente
     */
    public void saveNative(DispositivoEntity entity) {
        try {
            javax.persistence.Query q = entityManager.createNativeQuery(
                "INSERT INTO dispositivos (tipos, conector, interfaz, puerto, notificar, icono, estado, d_atributos) " +
                "VALUES (?, ?, ?, NULL, NULL, NULL, ?, (?::json))"
            );
            q.setParameter(1, entity.getTipos());
            q.setParameter(2, entity.getConector());
            q.setParameter(3, entity.getInterfaz());
            q.setParameter(4, entity.getEstado());
            q.setParameter(5, entity.getAtributos());
            q.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error saving dispositivo (native)", e);
        }
    }

    @Override
    public DispositivoEntity update(DispositivoEntity entity) {
        try {
            return entityManager.merge(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error updating dispositivo", e);
        }
    }

    @Override
    public void delete(DispositivoEntity entity) {
        try {
            entityManager.remove(entity);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting dispositivo", e);
        }
    }

    @Override
    public Optional<DispositivoEntity> findById(Object id) {
        try {
            DispositivoEntity dispositivo = entityManager.find(DispositivoEntity.class, id);
            return Optional.ofNullable(dispositivo);
        } catch (Exception e) {
            throw new RuntimeException("Error finding dispositivo by id", e);
        }
    }

    @Override
    public List<DispositivoEntity> findAll() {
        try {
            TypedQuery<DispositivoEntity> query = entityManager.createQuery("SELECT d FROM DispositivoEntity d", DispositivoEntity.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all dispositivos", e);
        }
    }

    @Override
    public List<DispositivoEntity> findByQuery(String jpql, Object... parameters) {
        try {
            TypedQuery<DispositivoEntity> query = entityManager.createQuery(jpql, DispositivoEntity.class);
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
            javax.persistence.Query query = entityManager.createNativeQuery(sql, DispositivoEntity.class);
            for (int i = 0; i < parameters.length; i++) {
                query.setParameter(i + 1, parameters[i]);
            }
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error executing native query", e);
        }
    }

    /**
     * Actualiza un dispositivo existente
     * @param id ID del dispositivo
     * @param tipos Tipos del dispositivo
     * @param conector Conector del dispositivo
     * @param interfaz Interfaz del dispositivo
     * @param estado Estado del dispositivo
     * @param atributos Atributos del dispositivo en formato JSON
     * @throws DAOException si ocurre un error durante la actualización
     */
    public void actualizarDispositivo(DispositivoDto dispositivoDto) throws DAOException {
        try {
            DispositivoEntity dispositivo = entityManager.find(DispositivoEntity.class, dispositivoDto.getId());
            if (dispositivo == null) {
                throw new DAOException("No se encontró el dispositivo con ID: " + dispositivoDto.getId());
            }

            dispositivo.setTipos(dispositivoDto.getTipos());
            dispositivo.setConector(dispositivoDto.getConector());
            dispositivo.setInterfaz(dispositivoDto.getInterfaz());
            dispositivo.setEstado(dispositivoDto.getEstado());
            dispositivo.setAtributos(dispositivoDto.getAtributos().toString());

            update(dispositivo);
        } catch (Exception e) {
            throw new DAOException("Error al actualizar el dispositivo: " + e.getMessage(), e);
        }
    }
} 