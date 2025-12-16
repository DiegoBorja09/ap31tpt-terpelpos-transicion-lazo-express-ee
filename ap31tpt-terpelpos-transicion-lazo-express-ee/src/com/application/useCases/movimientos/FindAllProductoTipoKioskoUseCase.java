package com.application.useCases.movimientos;

import com.application.commons.ResultadoProductosKiosko;
import com.application.core.BaseUseCases;
import com.bean.ImpuestosBean;
import com.bean.MovimientosDetallesBean;
import com.bean.ProductoBean;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.infrastructure.Enums.SqlQueryEnum;
import com.infrastructure.repositories.ProductoRepository;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.database.DatabaseNames;
import com.controllers.NovusConstante;
import com.firefuel.Main;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Caso de uso para obtener los productos tipo KIOSKO y CANASTILLA
 * Este caso de uso consulta los productos del market usando la función fnc_buscar_productos_market
 */
public class FindAllProductoTipoKioskoUseCase implements BaseUseCases<List<MovimientosDetallesBean>> {

    private final EntityManagerFactory entityManagerFactory;
    private static final Logger logger = Logger.getLogger(FindAllProductoTipoKioskoUseCase.class.getName());
    private final Integer currentPage;
    private final Integer pageSize;

    public FindAllProductoTipoKioskoUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
        this.currentPage = null;
        this.pageSize = null;
    }

    public FindAllProductoTipoKioskoUseCase(int currentPage, int pageSize) {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    public ResultadoProductosKiosko executePaginated() {
        if (currentPage == null || pageSize == null) {
            throw new IllegalStateException("Pagination parameters must be set for paginated execution");
        }
        
        try {
            com.infrastructure.cache.KioscoCacheServiceLiviano cache = 
                com.infrastructure.cache.KioscoCacheServiceLiviano.getInstance();
            
            // Obtener productos desde cache
            List<MovimientosDetallesBean> todosProductos = cache.obtenerProductosPopularesConCache(10000);
            
            if (todosProductos != null && !todosProductos.isEmpty()) {
                // Aplicar paginación manualmente
                int fromIndex = currentPage * pageSize;
                int toIndex = Math.min(fromIndex + pageSize, todosProductos.size());
                
                if (fromIndex < todosProductos.size()) {
                    List<MovimientosDetallesBean> paginatedResults = new ArrayList<>(todosProductos.subList(fromIndex, toIndex));
                    return new ResultadoProductosKiosko(paginatedResults, todosProductos.size());
                }
                return new ResultadoProductosKiosko(new ArrayList<>(), todosProductos.size());
            }
            
            // Fallback a método original si cache falla o está vacío
            List<MovimientosDetallesBean> results = execute();
            return new ResultadoProductosKiosko(results, results.size());
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error executing paginated query", e);
            List<MovimientosDetallesBean> results = execute();
            return new ResultadoProductosKiosko(results, results.size());
        }
    }

    @Override
    public List<MovimientosDetallesBean> execute() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            ProductoRepository productoRepo = new ProductoRepository(entityManager);

            boolean isCDL = Main.TIPO_NEGOCIO.equals(NovusConstante.PARAMETER_CDL);
            String sql = SqlQueryEnum.BUSCAR_PRODUCTOS_MARKET.getQuery();

            // Aplicar paginación solo si es necesario
            if (currentPage != null && pageSize != null) {
                sql += " LIMIT ? OFFSET ?";
            }

            @SuppressWarnings("unchecked")
            List<Object[]> resultados;
            if (currentPage != null && pageSize != null) {
                resultados = (List<Object[]>) productoRepo.findByNativeQuery(sql,
                    isCDL,
                    Long.valueOf(NovusConstante.IDENTIFICADOR_CODIGO_BARRA),
                    pageSize,
                    currentPage * pageSize
                );
            } else {
                resultados = (List<Object[]>) productoRepo.findByNativeQuery(sql,
                    isCDL,
                    Long.valueOf(NovusConstante.IDENTIFICADOR_CODIGO_BARRA)
                );
            }

            List<MovimientosDetallesBean> lista = new ArrayList<>();

            // Procesar los resultados y mapearlos a MovimientosDetallesBean
            for (Object[] row : resultados) {
                MovimientosDetallesBean movBean = mapearResultadoABean(row);
                lista.add(movBean);
            }

            return lista;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al obtener productos del market", e);
            throw new RuntimeException("Error al obtener productos del market: " + e.getMessage(), e);
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    private MovimientosDetallesBean mapearResultadoABean(Object[] row) {
        MovimientosDetallesBean movBean = new MovimientosDetallesBean();
        
        // Mapeo basado en la estructura de fnc_buscar_productos_market
        movBean.setId(Long.parseLong(row[0].toString())); // ID
        movBean.setCategoriaId(Long.parseLong(row[11].toString())); // categoria_id
        movBean.setCategoriaDesc(row[12].toString()); // categoria_descripcion
        movBean.setProductoId(Long.parseLong(row[0].toString())); // productos_id (mismo que ID)
        movBean.setPlu(row[1].toString()); // PLU
        movBean.setDescripcion(row[5].toString()); // DESCRIPCION
        movBean.setPrecio(Float.parseFloat(row[6].toString())); // PRECIO
        movBean.setSaldo(Float.parseFloat(row[20].toString())); // SALDO
        movBean.setTipo(Integer.parseInt(row[7].toString())); // tipo
        movBean.setUnidades_medida_id(Long.parseLong(row[3].toString())); // unidades_medida
        movBean.setCantidadIngredientes(Integer.parseInt(row[9].toString())); // CANTIDAD_INGREDIENTES
        movBean.setCantidadImpuestos(Integer.parseInt(row[10].toString())); // CANTIDAD_IMPUESTOS
        movBean.setEstado(row[2].toString()); // estado
        movBean.setCosto(Float.parseFloat(row[21].toString())); // COSTO
        movBean.setCodigoBarra(row[13].toString()); // codigo_barra
        movBean.setProducto_compuesto_costo(Float.parseFloat(row[21].toString())); // COSTO
        movBean.setBodegasId(safeLong(row[19].toString())); // bodega_id

        // Determinar si es producto compuesto
        int tipoProducto = Integer.parseInt(row[7].toString()); // TIPO
        movBean.setCompuesto(tipoProducto == NovusConstante.TIPO_PRODUCTO_COMPUESTO || 
                           tipoProducto == NovusConstante.TIPO_PRODUCTO_PROMOCION);
        
        // Procesar impuestos si están disponibles en la respuesta
        String impuestosJson = row[14].toString(); // impuestos
        if (impuestosJson != null && !impuestosJson.isEmpty() && !impuestosJson.equals("[]") && !impuestosJson.equals("{}")) {
            try {
                JsonElement jsonElement = JsonParser.parseString(impuestosJson);
                if (jsonElement.isJsonArray()) {
                    movBean.setImpuestos(organizarImpuestosProductos(jsonElement.getAsJsonArray()));
                } else if (jsonElement.isJsonObject()) {
                    // Si es un objeto, convertirlo a array con un solo elemento
                    JsonArray jsonArray = new JsonArray();
                    jsonArray.add(jsonElement.getAsJsonObject());
                    movBean.setImpuestos(organizarImpuestosProductos(jsonArray));
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error procesando impuestos JSON: " + impuestosJson, e);
            }
        }
        
        // Procesar ingredientes si están disponibles en la respuesta
        String ingredientesJson = row[18].toString(); // ingredientes
        
        if (ingredientesJson != null && !ingredientesJson.isEmpty() && !ingredientesJson.equals("[]") && !ingredientesJson.equals("{}")) {
            try {
                JsonElement jsonElement = JsonParser.parseString(ingredientesJson);
                if (jsonElement.isJsonArray()) {
                    ArrayList<ProductoBean> ingredientesProcesados = organizarIngredientesProductos(jsonElement.getAsJsonArray());
                    movBean.setIngredientes(ingredientesProcesados);
                } else if (jsonElement.isJsonObject()) {
                    // Si es un objeto, convertirlo a array con un solo elemento
                    JsonArray jsonArray = new JsonArray();
                    jsonArray.add(jsonElement.getAsJsonObject());
                    ArrayList<ProductoBean> ingredientesProcesados = organizarIngredientesProductos(jsonArray);
                    movBean.setIngredientes(ingredientesProcesados);
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error procesando ingredientes JSON: " + ingredientesJson, e);
                movBean.setIngredientes(new ArrayList<>()); // Lista vacía en caso de error
            }
        } else {
            movBean.setIngredientes(new ArrayList<>());
        }

        if(!movBean.getIngredientes().isEmpty()) {
            for (ProductoBean ingrediente : movBean.getIngredientes()) {
                movBean.setProducto_compuesto_costo(movBean.getProducto_compuesto_costo() + 
                    (ingrediente.getProducto_compuesto_costo() * ingrediente.getProducto_compuesto_cantidad()));
            }
        }
        
        return movBean;
    }

    private ArrayList<ImpuestosBean> organizarImpuestosProductos(JsonArray arrayImpuestos) {
        ArrayList<ImpuestosBean> impuestos = new ArrayList<>();
        for (JsonElement element : arrayImpuestos) {
            JsonObject jsonImpuesto = element.getAsJsonObject();
            ImpuestosBean impuesto = new ImpuestosBean();
            impuesto.setId(jsonImpuesto.get("impuesto_id").getAsLong());
            impuesto.setDescripcion(jsonImpuesto.get("descripcion").getAsString());
            impuesto.setIva_incluido(jsonImpuesto.get("iva_incluido").getAsString().equals("S"));
            impuesto.setPorcentaje_valor(jsonImpuesto.get("porcentaje_valor").getAsString());
            impuesto.setValor(jsonImpuesto.get("valor").getAsFloat());
            impuestos.add(impuesto);
        }
        return impuestos;
    }

    private ArrayList<ProductoBean> organizarIngredientesProductos(JsonArray arrayIngredientes) {
        ArrayList<ProductoBean> ingredientes = new ArrayList<>();
        
        if (arrayIngredientes == null || arrayIngredientes.size() == 0) {
            return ingredientes;
        }
        
        for (JsonElement element : arrayIngredientes) {
            try {
                if (element == null || element.isJsonNull() || !element.isJsonObject()) {
                    continue;
                }
                
                JsonObject jsonIngrediente = element.getAsJsonObject();
                ProductoBean producto = new ProductoBean();
                
                if (jsonIngrediente.has("ingredientes_id") && !jsonIngrediente.get("ingredientes_id").isJsonNull()) {
                    producto.setId(jsonIngrediente.get("ingredientes_id").getAsLong());
                } else {
                    producto.setId(0L);
                }
                
                if (jsonIngrediente.has("descripcion") && !jsonIngrediente.get("descripcion").isJsonNull()) {
                    producto.setDescripcion(jsonIngrediente.get("descripcion").getAsString());
                } else {
                    producto.setDescripcion("");
                }
                
                if (jsonIngrediente.has("saldo") && !jsonIngrediente.get("saldo").isJsonNull()) {
                    int saldo = jsonIngrediente.get("saldo").getAsInt();
                    producto.setSaldo(saldo);
                    producto.setCantidadUnidad(saldo);
                } else {
                    producto.setSaldo(0);
                    producto.setCantidadUnidad(0);
                }
                
                if (jsonIngrediente.has("cantidad") && !jsonIngrediente.get("cantidad").isJsonNull()) {
                    producto.setProducto_compuesto_cantidad(jsonIngrediente.get("cantidad").getAsFloat());
                } else {
                    producto.setProducto_compuesto_cantidad(0);
                }
                
                if (jsonIngrediente.has("costo") && !jsonIngrediente.get("costo").isJsonNull()) {
                    producto.setProducto_compuesto_costo(jsonIngrediente.get("costo").getAsFloat());
                } else {
                    producto.setProducto_compuesto_costo(0);
                }
                
                ingredientes.add(producto);
                
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error procesando ingrediente: " + (element != null ? element.toString() : "NULL"), e);
                // Continuar con el siguiente ingrediente
            }
        }
        
        return ingredientes;
    }

    //  HELPER: Conversión segura de Object a Long
    private Long safeLong(Object value) {
        if (value == null) return 0L;
        if (value instanceof Number) return ((Number) value).longValue();
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return 0L;
            }
        }
        return 0L;
    }


}