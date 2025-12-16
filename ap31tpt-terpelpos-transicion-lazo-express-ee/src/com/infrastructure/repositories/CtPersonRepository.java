package com.infrastructure.repositories;

import com.domain.entities.CtPerson;
import com.infrastructure.Enums.SqlQueryEnum;
import com.infrastructure.core.BaseRepositoryInterface;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class CtPersonRepository implements BaseRepositoryInterface<CtPerson> {


    private final EntityManager entityManager;

    public CtPersonRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public CtPerson save(CtPerson entity) {
        return null;
    }

    @Override
    public CtPerson update(CtPerson entity) {
        return null;
    }

    @Override
    public void delete(CtPerson entity) {

    }

    @Override
    public Optional<CtPerson> findById(Object id) {
        return Optional.empty();
    }

    @Override
    public List<CtPerson> findAll() {
        try {
            TypedQuery<CtPerson> query = entityManager.createQuery("SELECT p FROM CtPerson p", CtPerson.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all persons", e);
        }
    }

    @Override
    public List<CtPerson> findByQuery(String jpql, Object... parameters) {
        return List.of();
    }

    @Override
    public List<?> findByNativeQuery(String sql, Object... parameters) {
        return List.of();
    }

    public boolean existsById() {
        String sql = SqlQueryEnum.OBTENER_PERSONA.getQuery();
        Long count = entityManager.createQuery(sql, Long.class).getSingleResult();
        System.out.println("Resultado de búsqueda: " + count);
        return count > 0;
    }




}