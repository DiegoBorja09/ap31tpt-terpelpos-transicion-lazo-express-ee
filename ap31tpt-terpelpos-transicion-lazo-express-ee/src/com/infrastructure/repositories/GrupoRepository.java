package com.infrastructure.repositories;

import com.bean.CatalogoBean;
import com.bean.CategoriaBean;
import com.domain.entities.GrupoEntity;
import com.infrastructure.core.BaseRepositoryInterface;
import com.infrastructure.Enums.SqlQueryEnum;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GrupoRepository implements BaseRepositoryInterface<GrupoEntity> {

    private final EntityManager entityManager;

    public GrupoRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public GrupoEntity save(GrupoEntity entity) {
        return null;
    }

    @Override
    public GrupoEntity update(GrupoEntity entity) {
        return null;
    }

    @Override
    public void delete(GrupoEntity entity) {
        // vacío
    }

    @Override
    public Optional<GrupoEntity> findById(Object id) {
        return Optional.empty();
    }

    @Override
    public List<GrupoEntity> findAll() {
        return Collections.emptyList();
    }

    @Override
    public List<GrupoEntity> findByQuery(String jpql, Object... parameters) {
        return Collections.emptyList();
    }

    @Override
    public List<?> findByNativeQuery(String sql, Object... parameters) {
        return Collections.emptyList();
    }

    public List<CatalogoBean> buscarCategoriasVisibles() {
        List<CatalogoBean> lista = new ArrayList<>();
        try {
            String sql = SqlQueryEnum.BUSCAR_CATEGORIAS_VISIBLES.getQuery();
            @SuppressWarnings("unchecked")
            List<Object[]> resultados = (List<Object[]>) entityManager.createNativeQuery(sql)
                    .getResultList();

            for (Object[] fila : resultados) {
                CatalogoBean grupo = new CatalogoBean();
                grupo.setId(((Number) fila[0]).longValue());
                grupo.setDescripcion((String) fila[1]);
                lista.add(grupo);
            }

            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error al buscar categorías visibles", e);
        }
    }

    /**
     * Obtiene todas las categorías con productos activos usando la nueva consulta del SqlQueryEnum
     * Reemplaza la consulta hardcodeada del CategoriasDao.findAllCategorias()
     */
    public List<CategoriaBean> obtenerTodasCategoriasConProductos() {
        List<CategoriaBean> lista = new ArrayList<>();
        try {
            String sql = SqlQueryEnum.OBTENER_TODAS_CATEGORIAS_CON_PRODUCTOS.getQuery();
            @SuppressWarnings("unchecked")
            List<Object[]> resultados = (List<Object[]>) entityManager.createNativeQuery(sql)
                    .getResultList();

            for (Object[] fila : resultados) {
                CategoriaBean categoria = new CategoriaBean();
                categoria.setId(((Number) fila[0]).longValue());
                categoria.setGrupo((String) fila[1]);
                // El campo cantidad está en la posición que corresponde según la consulta
                // SELECT *, cantidad FROM grupos
                categoria.setCantidad(((Number) fila[fila.length - 1]).intValue()); // último campo es cantidad
                lista.add(categoria);
            }

            // Agregar categoría por defecto "SIN CATEGORIA"
            CategoriaBean sinCategoria = new CategoriaBean();
            sinCategoria.setId(-1L);
            sinCategoria.setGrupo("SIN CATEGORIA");
            sinCategoria.setCantidad(1);
            lista.add(sinCategoria);

            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todas las categorías con productos", e);
        }
    }
}
