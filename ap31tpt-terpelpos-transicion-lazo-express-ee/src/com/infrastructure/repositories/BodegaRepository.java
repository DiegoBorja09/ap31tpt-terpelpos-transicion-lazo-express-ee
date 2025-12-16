package com.infrastructure.repositories;

import com.infrastructure.Enums.SqlQueryEnum;
import com.bean.CategoriaBean;
import com.controllers.NovusConstante;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.ArrayList;

public class BodegaRepository {

    private final EntityManager entityManager;

    public BodegaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Verifica si existen combustibles activos en la base de datos.
     * @return true si hay combustibles activos, false en caso contrario.
     */
    public boolean combustiblesRepository() {
        try {
            String sql = SqlQueryEnum.CHECK_COMBUSTIBLES.getQuery();
            Query query = entityManager.createNativeQuery(sql);
            return !query.getResultList().isEmpty();
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar combustibles activos desde el repositorio", e);
        }
    }

    /**
     * Verifica si existen registros de canastilla o kiosco activos en la base de datos.
     * @param tipoNegocio El tipo de negocio ("C" para canastilla, "K" para kiosco).
     * @return true si hay registros activos, false en caso contrario.
     */
    public boolean canastillaKioscoRepository(String tipoNegocio) {
        try {
            String sql = SqlQueryEnum.CHECK_CANASTILLA_KIOSCO.getQuery();
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, tipoNegocio);
            return !query.getResultList().isEmpty();
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar canastilla/kiosco activos desde el repositorio", e);
        }
    }

    /**
     * Obtiene el estado de los negocios activos.
     * @return JsonObject con los datos de los negocios activos (combustible, canastilla, kiosco).
     */
    public JsonObject negociosActivosRepository() {
        try {
            JsonObject data = new JsonObject();
            data.addProperty("combustible", combustiblesRepository());
            data.addProperty("canastilla", canastillaKioscoRepository("C"));
            data.addProperty("kiosco", canastillaKioscoRepository("K"));
            System.out.println(data);
            return data;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los negocios activos desde el repositorio", e);
        }
    }

    /**
     * Obtiene todas las categorías del KIOSCO
     * @param isCDL indica si es un negocio tipo CDL
     * @return Lista de categorías
     */
    public List<CategoriaBean> findAllCategoriasKIOSCO(boolean isCDL) {
        try {
            String sql = SqlQueryEnum.OBTENER_CATEGORIAS_KIOSCO.getQuery();
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, isCDL);
            
            List<CategoriaBean> lista = new ArrayList<>();
            List<Object[]> results = query.getResultList();
            
            for (Object[] result : results) {
                CategoriaBean bean = new CategoriaBean();
                bean.setId(((Number) result[0]).longValue());
                bean.setGrupo((String) result[1]);
                lista.add(bean);
            }
            
            // Agregar categoría por defecto "OTROS"
            CategoriaBean otros = new CategoriaBean();
            otros.setId(-1L);
            otros.setGrupo("OTROS");
            otros.setCantidad(1);
            lista.add(otros);
            
            return lista;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener las categorías del KIOSCO", e);
        }
    }
     
     /**
     * Consulta en base de datos por ID y retorna un valor long.
     * @param id El ID a consultar en la base de datos.
     * @return long con el resultado de la consulta, 0 si no se encuentra.
     */
    public long consultarBodegaProductoPorIdProducto(Long id) {
        try {
            // Ejemplo de consulta - ajustar según la tabla y campo específico necesario
            String sql = SqlQueryEnum.BUSCAR_BODEGA_PRODUCTO_POR_ID_PRODUCTO.getQuery();
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, id);
            
            Object result = query.getSingleResult();
            return result != null ? ((Number) result).longValue() : 0L;
            
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar por ID: " + id, e);
        }
    }
}
