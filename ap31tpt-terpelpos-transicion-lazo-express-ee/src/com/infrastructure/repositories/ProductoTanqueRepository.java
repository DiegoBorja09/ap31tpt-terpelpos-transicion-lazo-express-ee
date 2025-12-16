package com.infrastructure.repositories;

import com.application.useCases.productos.ProductoTanqueDto;
import com.dao.DAOException;
import com.domain.entities.ProductoEntity;
import com.infrastructure.core.BaseRepositoryInterface;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProductoTanqueRepository implements BaseRepositoryInterface<ProductoEntity> {

    private final EntityManager entityManager;

    public ProductoTanqueRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<ProductoTanqueDto> getProductosTanques(Long familia) throws DAOException {
        try {
            String sql = "SELECT p.id, p.familias, p.descripcion, p.precio, p.unidad_medida_id, u.descripcion as unidad_descripcion "
                    + "FROM productos p "
                    + "INNER JOIN unidades u ON u.id = p.unidad_medida_id "
                    + "WHERE p.familias = ?1 "
                    + "ORDER BY p.descripcion ASC";

            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, familia);

            @SuppressWarnings("unchecked")
            List<Object[]> resultados = query.getResultList();

            return resultados.stream()
                    .map(r -> new ProductoTanqueDto(
                            ((Number) r[0]).intValue(),       // id
                            (String) r[2],                   // descripcion
                            ((Number) r[1]).intValue(),       // familias
                            ((Number) r[3]).floatValue(),    // precio
                            0,                               // saldo por defecto
                            ((Number) r[4]).longValue(),     // unidad_medida_id
                            (String) r[5]                   // unidad_descripcion
                    ))
                    .collect(Collectors.toList());
                
        } catch (Exception e) {
            throw new DAOException("Error al obtener productos tanques: " + e.getMessage(), e);
        }
    }

    @Override
    public ProductoEntity save(ProductoEntity entity) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public ProductoEntity update(ProductoEntity entity) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public void delete(ProductoEntity entity) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public Optional<ProductoEntity> findById(Object id) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public List<ProductoEntity> findAll() {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public List<ProductoEntity> findByQuery(String query, Object... params) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public List<ProductoEntity> findByNativeQuery(String query, Object... params) {
        throw new UnsupportedOperationException("Operation not supported");
    }
} 