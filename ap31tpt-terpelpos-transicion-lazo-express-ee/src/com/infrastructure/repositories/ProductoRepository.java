package com.infrastructure.repositories;

import com.bean.ImpuestosBean;
import com.bean.MovimientosDetallesBean;
import com.bean.ProductoBean;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.firefuel.Main;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.infrastructure.Enums.SqlQueryEnum;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProductoRepository {

    private final EntityManager entityManager;

    public ProductoRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<ProductoBean> obtenerProductosPromocion() {
        String sql = SqlQueryEnum.OBTENER_PRODUCTOS_PROMOCION.getQuery();

        Query nativeQuery = entityManager.createNativeQuery(sql);

        @SuppressWarnings("unchecked")
        List<Object[]> resultados = nativeQuery.getResultList();

        List<ProductoBean> lista = new ArrayList<>();
        for (Object[] row : resultados) {
            ProductoBean producto = new ProductoBean();
            producto.setId(((Number) row[0]).longValue());
            producto.setPlu((String) row[1]);
            producto.setDescripcion((String) row[2]);
            producto.setPrecio(((Number) row[3]).floatValue());
            producto.setTipo(((Number) row[4]).intValue());
            producto.setFavorito((Boolean) row[5]);
            producto.setSaldo(((Number) row[6]).intValue());
            producto.setCosto(((Number) row[7]).floatValue());
            lista.add(producto);
        }

        return lista;
    }

    public List<ProductoBean> buscarPorCategoria(int categoriaId) {
        String sql = SqlQueryEnum.BUSCAR_PRODUCTOS_POR_CATEGORIA.getQuery();

        Query nativeQuery = entityManager.createNativeQuery(sql);
        nativeQuery.setParameter(1, categoriaId);  // ✅ Usar parámetro posicional

        @SuppressWarnings("unchecked")
        List<Object[]> resultados = nativeQuery.getResultList();

        List<ProductoBean> lista = new ArrayList<>();
        for (Object[] row : resultados) {
            ProductoBean producto = new ProductoBean();
            producto.setId(((Number) row[2]).longValue());
            producto.setPlu((String) row[3]);
            producto.setEstado((String) row[4]);
            producto.setDescripcion((String) row[5]);
            producto.setPrecio(((Number) row[6]).floatValue());
            producto.setSaldo(((Number) row[10]).intValue());
            lista.add(producto);
        }

        return lista;
    }
    public List<ProductoBean> buscarPorDescripcion(String keyword) {
        String sql = SqlQueryEnum.BUSCAR_PRODUCTOS_POR_DESCRIPCION.getQuery();

        Query nativeQuery = entityManager.createNativeQuery(sql);
        nativeQuery.setParameter(1, "%" + keyword + "%");  // ✅ Usar parámetro posicional

        @SuppressWarnings("unchecked")
        List<Object[]> resultados = nativeQuery.getResultList();

        List<ProductoBean> lista = new ArrayList<>();
        for (Object[] row : resultados) {
            ProductoBean product = new ProductoBean();
            product.setId(((Number) row[0]).longValue());
            product.setPlu((String) row[1]);
            product.setEstado((String) row[2]);
            product.setDescripcion((String) row[3]);
            product.setPrecio(((Number) row[4]).floatValue());
            product.setSaldo(((Number) row[8]).intValue());
            lista.add(product);
        }

        return lista;
    }
    public List<MovimientosDetallesBean> getCategoriasMovimiento(Set<Long> productosIds) {
        String sql = SqlQueryEnum.GET_CATEGORIAS_MOVIMIENTO.getQuery();

        Query query = entityManager.createNativeQuery(sql);
        // ✅ Convertir Set a Array para PostgreSQL
        Long[] idsArray = productosIds.toArray(new Long[0]);
        query.setParameter(1, idsArray);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        List<MovimientosDetallesBean> lista = new ArrayList<>();
        for (Object[] fila : results) {
            MovimientosDetallesBean detalle = new MovimientosDetallesBean();
            detalle.setCategoriaId(((Number) fila[0]).longValue());
            detalle.setId(((Number) fila[1]).longValue());
            detalle.setCategoriaDesc((String) fila[2]);
            lista.add(detalle);
        }

        return lista;
    }

    /**
     * Obtiene el precio de un producto por su descripción (específicamente para UREA)
     * @param descripcion Descripción del producto (ej: "UREA")
     * @return precio del producto como float, 0 si no se encuentra
     */
    public float obtenerPrecioUrea(String descripcion) {
        String sql = SqlQueryEnum.OBTENER_PRECIO_UREA.getQuery();

        Query nativeQuery = entityManager.createNativeQuery(sql);
        nativeQuery.setParameter(1, descripcion);

        @SuppressWarnings("unchecked")
        List<Object> resultados = nativeQuery.getResultList();

        if (!resultados.isEmpty()) {
            Object precio = resultados.get(0);
            if (precio instanceof Number) {
                return ((Number) precio).floatValue();
            }
        }

        return 0f; // Retorna 0 si no encuentra el producto
    }

    /**
     * Obtiene el código externo de UREA desde el campo JSON p_atributos
     * @param descripcion Descripción del producto (ej: "UREA")
     * @return código externo del producto como long, 0 si no se encuentra
     */
    public long obtenerCodigoExternoUrea(String descripcion) {
        String sql = SqlQueryEnum.OBTENER_CODIGO_EXTERNO_UREA.getQuery();

        Query nativeQuery = entityManager.createNativeQuery(sql);
        nativeQuery.setParameter(1, descripcion);

        @SuppressWarnings("unchecked")
        List<Object> resultados = nativeQuery.getResultList();

        if (!resultados.isEmpty()) {
            Object codigo = resultados.get(0);
            if (codigo instanceof Number) {
                return ((Number) codigo).longValue();
            }
        }

        return 0L; // Retorna 0 si no encuentra el producto
    }

    public List<?> findByNativeQuery(String sql, Object... parameters) {
        try {
            var query = entityManager.createNativeQuery(sql);

            // Asignar parámetros posicionales (?, ?, ?)
            for (int i = 0; i < parameters.length; i++) {
                query.setParameter(i + 1, parameters[i]); // JPA usa índices 1-based
            }

            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error executing native query: " + sql, e);
        }
    }

    /**
     * Obtiene el ID de un producto por su descripción (específicamente para UREA)
     * @param descripcion Descripción del producto (ej: "UREA")
     * @return ID del producto como long, 0 si no se encuentra
     */
    public long obtenerIdProductoUrea(String descripcion) {
        String sql = SqlQueryEnum.OBTENER_ID_PRODUCTO_UREA.getQuery();

        Query nativeQuery = entityManager.createNativeQuery(sql);
        nativeQuery.setParameter(1, descripcion);

        @SuppressWarnings("unchecked")
        List<Object> resultados = nativeQuery.getResultList();

        if (!resultados.isEmpty()) {
            Object id = resultados.get(0);
            if (id instanceof Number) {
                return ((Number) id).longValue();
            }
        }

        return 0L; // Retorna 0 si no encuentra el producto
    }

    //AQUI SE CREA EL METODO PARA OBTENER EL  PRODUCTO EL NAME
    public String getProductByName(long id) {
        String sql = SqlQueryEnum.OBTENER_PRODUCTO_POR_NOMBRE_VENTA_EN_CURSO.getQuery();
        NovusUtils.printLn("🔍 SQL Query: " + sql);
        NovusUtils.printLn("🔍 Buscando producto con ID: " + id);
        
        Query nativeQuery = entityManager.createNativeQuery(sql);
        nativeQuery.setParameter(1, id); // Usar long directamente (bigint en PostgreSQL)
        
        try {
            @SuppressWarnings("unchecked")
            List<Object> resultados = nativeQuery.getResultList();
            NovusUtils.printLn("🔍 Resultados encontrados: " + resultados.size());
            NovusUtils.printLn("🔍 Resultados consulta producto: " + resultados);
            
            if (!resultados.isEmpty() && resultados.get(0) != null) {
                return resultados.get(0).toString();
            }
        } catch (Exception e) {
            NovusUtils.printLn("Error al obtener producto por ID " + id + ": " + e.getMessage());
        }
        
        return null; // Retorna null si no encuentra el producto
    }


}
