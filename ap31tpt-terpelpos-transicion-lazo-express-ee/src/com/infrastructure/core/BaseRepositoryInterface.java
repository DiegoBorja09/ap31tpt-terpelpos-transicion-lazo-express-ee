package com.infrastructure.core;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public interface BaseRepositoryInterface<T> {

    T save(T entity);

    T update(T entity);

    void delete(T entity);

    Optional<T> findById(Object id);

    List<T> findAll();

    List<T> findByQuery(String jpql, Object... parameters);

    List<?> findByNativeQuery(String sql, Object... parameters);

}
