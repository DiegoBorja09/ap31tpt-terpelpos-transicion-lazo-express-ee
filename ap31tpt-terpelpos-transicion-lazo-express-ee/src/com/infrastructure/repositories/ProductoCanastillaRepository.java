package com.infrastructure.repositories;

import com.bean.ImpuestosBean;
import com.bean.MovimientosDetallesBean;
import com.bean.ProductoBean;
import com.bean.ResultadoProductosCanastila;
import com.controllers.NovusConstante;
import com.infrastructure.Enums.SqlQueryEnum;
import com.infrastructure.core.BaseRepositoryInterface;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.dao.DAOException;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

public class ProductoCanastillaRepository implements BaseRepositoryInterface<MovimientosDetallesBean> {

    private final EntityManager entityManager;
    private final Gson gson = new Gson();

    public ProductoCanastillaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Obtiene la lista de productos de canastilla.
     * @return Lista de productos de canastilla
     * @throws DAOException si ocurre un error en la base de datos
     */
    public ResultadoProductosCanastila obtenerProductosCanastilla(int currentPage, int pageSize) throws DAOException {
        try {
            long startTime = System.currentTimeMillis();
            long total = 0;
            String sql = SqlQueryEnum.OBTENER_PRODUCTOS_CANASTILLA.getQuery();
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, NovusConstante.IDENTIFICADOR_CODIGO_BARRA);
            query.setParameter(2, pageSize);
            query.setParameter(3, (currentPage*pageSize));

            List<MovimientosDetallesBean> lista = new ArrayList<>();
            List<Object[]> results = query.getResultList();

            long queryTime = System.currentTimeMillis();
            System.out.println(" Tiempo consulta SQL: " + (queryTime - startTime) + "ms");

            Gson gson = new Gson();

            for (Object[] result : results) {
                MovimientosDetallesBean producto = mapearProductoDesdeResultSet(result, gson);
                lista.add(producto);
            }
            total = Optional.of(results)
                    .filter(r -> !r.isEmpty())
                    .map(r -> r.get(0))
                    .filter(arr -> arr.length > 16 && arr[16] instanceof Number)
                    .map(arr -> ((Number) arr[16]).longValue())
                    .orElse(0L);

            return new ResultadoProductosCanastila(lista, total);
        } catch (Exception e) {
            throw new DAOException("Error al obtener productos de canastilla", e);
        }
    }

    private ArrayList<ImpuestosBean> organizarImpuestosProductos(JsonArray impuestosArray) {
        ArrayList<ImpuestosBean> impuestos = new ArrayList<>();
        if (impuestosArray != null) {
            for (int i = 0; i < impuestosArray.size(); i++) {
                ImpuestosBean impuesto = gson.fromJson(impuestosArray.get(i), ImpuestosBean.class);
                impuestos.add(impuesto);
            }
        }
        return impuestos;
    }

    private ArrayList<ProductoBean> organizarIngredientesProductos(JsonArray ingredientesArray) {
        ArrayList<ProductoBean> ingredientes = new ArrayList<>();
        if (ingredientesArray != null) {
            for (int i = 0; i < ingredientesArray.size(); i++) {
                ProductoBean ingrediente = gson.fromJson(ingredientesArray.get(i), ProductoBean.class);
                ingredientes.add(ingrediente);
            }
        }
        return ingredientes;
    }

    private MovimientosDetallesBean mapearProductoDesdeResultSet(Object[] result, Gson gson) {
        MovimientosDetallesBean producto = new MovimientosDetallesBean();
        int i = 0;

        producto.setId(((Number) result[i++]).longValue());
        producto.setPlu((String) result[i++]);
        producto.setEstado((String) result[i++]);
        producto.setUnidades_medida_id(getIntOrNull(result[i++]));
        producto.setDescripcion((String) result[i++]);
        producto.setPrecio(getFloatOrZero(result[i++]));
        producto.setTipo(getIntOrZero(result[i++]));
        producto.setCantidadIngredientes(getIntOrZero(result[i++]));
        producto.setCantidadImpuestos(getIntOrZero(result[i++]));
        producto.setCategoriaId(getLongOrZero(result[i++]));
        producto.setCategoriaDesc((String) result[i++]);
        producto.setCodigoBarra((String) result[i++]);

        // Procesar JSON de impuestos
        producto.setImpuestos(procesarJsonImpuestos(result[i++], gson));

        // Procesar JSON de ingredientes
        ArrayList<ProductoBean> ingredientes = procesarJsonIngredientes(result[i++], gson);
        producto.setIngredientes(ingredientes);

        producto.setSaldo(getIntOrZero(result[i++]));
        producto.setCosto(getFloatOrZero(result[i++]));

        calcularCostoCompuesto(producto, ingredientes);

        producto.setCompuesto(producto.getTipo() == NovusConstante.TIPO_PRODUCTO_COMPUESTO ||
                producto.getTipo() == NovusConstante.TIPO_PRODUCTO_PROMOCION);

        return producto;
    }

    private ArrayList<ImpuestosBean> procesarJsonImpuestos(Object jsonObj, Gson gson) {
        if (jsonObj == null) {
            return new ArrayList<>();
        }

        try {
            String jsonString = jsonObj.toString();

            if (jsonString == null || jsonString.trim().isEmpty() ||
                    jsonString.equals("[]") || jsonString.equals("null")) {
                return new ArrayList<>();
            }

            if (jsonString.startsWith("\"") && jsonString.endsWith("\"")) {
                jsonString = jsonString.substring(1, jsonString.length() - 1);
            }

            JsonElement element = gson.fromJson(jsonString, JsonElement.class);
            if (element != null && element.isJsonArray()) {
                return organizarImpuestosProductos(element.getAsJsonArray());
            }
        } catch (Exception e) {
            System.err.println(" Error procesando JSON impuestos: " + e.getMessage() +
                    ", Valor: " + jsonObj);
        }

        return new ArrayList<>();
    }


    private ArrayList<ProductoBean> procesarJsonIngredientes(Object jsonObj, Gson gson) {
        if (jsonObj == null) {
            return new ArrayList<>();
        }

        try {
            String jsonString = jsonObj.toString();

            if (jsonString == null || jsonString.trim().isEmpty() ||
                    jsonString.equals("[]") || jsonString.equals("null")) {
                return new ArrayList<>();
            }

            if (jsonString.startsWith("\"") && jsonString.endsWith("\"")) {
                jsonString = jsonString.substring(1, jsonString.length() - 1);
            }

            JsonElement element = gson.fromJson(jsonString, JsonElement.class);
            if (element != null && element.isJsonArray()) {
                return organizarIngredientesProductos(element.getAsJsonArray());
            }
        } catch (Exception e) {
            System.err.println(" Error procesando JSON ingredientes: " + e.getMessage() +
                    ", Valor: " + jsonObj);
        }

        return new ArrayList<>();
    }


    private void calcularCostoCompuesto(MovimientosDetallesBean producto, List<ProductoBean> ingredientes) {
        if (ingredientes == null || ingredientes.isEmpty()) {
            producto.setProducto_compuesto_costo(producto.getCosto());
            return;
        }
        float costoCompuesto = producto.getCosto();

        for (ProductoBean ingrediente : ingredientes) {
            costoCompuesto += (ingrediente.getProducto_compuesto_costo() * ingrediente.getProducto_compuesto_cantidad());
        }

        producto.setProducto_compuesto_costo(costoCompuesto);
    }


    private Integer getIntOrNull(Object value) {
        return (value != null) ? ((Number) value).intValue() : null;
    }

    private int getIntOrZero(Object value) {
        return (value != null) ? ((Number) value).intValue() : 0;
    }

    private long getLongOrZero(Object value) {
        return (value != null) ? ((Number) value).longValue() : 0L;
    }

    private float getFloatOrZero(Object value) {
        return (value != null) ? ((Number) value).floatValue() : 0.0f;
    }


    // Métodos de la interfaz que no usaremos
    @Override
    public MovimientosDetallesBean save(MovimientosDetallesBean entity) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public MovimientosDetallesBean update(MovimientosDetallesBean entity) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public void delete(MovimientosDetallesBean entity) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public Optional<MovimientosDetallesBean> findById(Object id) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public List<MovimientosDetallesBean> findAll() {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public List<MovimientosDetallesBean> findByQuery(String jpql, Object... parameters) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public List<?> findByNativeQuery(String sql, Object... parameters) {
        throw new UnsupportedOperationException("Operation not supported");
    }
} 