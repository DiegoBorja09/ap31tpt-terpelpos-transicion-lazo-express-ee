package com.infrastructure.repositories;

import com.application.useCases.tanques.TanqueDto;
import com.dao.DAOException;
import com.domain.entities.BodegaEntity;
import com.infrastructure.Enums.SqlQueryEnum;
import com.infrastructure.core.BaseRepositoryInterface;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BodegaTanqueRepository implements BaseRepositoryInterface<BodegaEntity> {

    private final EntityManager entityManager;

    public BodegaTanqueRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<TanqueDto> getTanques() throws DAOException {
        try {
            String sql = SqlQueryEnum.GET_TANQUES_POR_TIPO_ESTADO.getQuery();
            Query query = entityManager.createNativeQuery(sql);
            List<Object[]> results = query.getResultList();
            // id, bodega, codigo, numero, volumen_maximo, familia
            return results.stream()
                    .map(row -> new TanqueDto(
                            Integer.parseInt(row[0].toString()), // id
                            (String) row[1], // bodega
                            (String) row[2], // codigo
                            Integer.parseInt(row[3].toString()), // numero_tanque
                            Integer.parseInt(row[4].toString()), // volumen_maximo
                            Long.parseLong(row[5].toString()))) // estado
                    .collect(Collectors.toList());
            /*BodegaBean tanque = new BodegaBean();
            tanque.setId(re.getInt("id"));
            tanque.setDescripcion(re.getString("bodega"));
            tanque.setNumeroStand(re.getInt("numero"));
            tanque.setVolumenMaximo(re.getInt("volumen_maximo"));
            tanque.setFamiliaId(re.getLong("familia"));
            tanque.setProductos(getProductosTanques(tanque.getFamiliaId()));*/
        } catch (Exception e) {
            throw new DAOException("Error al obtener tanques: " + e.getMessage(), e);
        }
    }

    @Override
    public BodegaEntity save(BodegaEntity entity) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public BodegaEntity update(BodegaEntity entity) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public void delete(BodegaEntity entity) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public Optional<BodegaEntity> findById(Object id) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public List<BodegaEntity> findAll() {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public List<BodegaEntity> findByQuery(String jpql, Object... parameters) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public List<?> findByNativeQuery(String sql, Object... parameters) {
        throw new UnsupportedOperationException("Operation not supported");
    }
}