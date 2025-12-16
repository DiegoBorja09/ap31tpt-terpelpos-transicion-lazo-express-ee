package com.infrastructure.repositories;

import com.infrastructure.Enums.SqlQueryEnum;
import com.infrastructure.core.BaseRepositoryInterface;
import com.bean.RecepcionBean;
import com.bean.Surtidor;
import com.controllers.NovusUtils;
import com.firefuel.Main;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class SurtidorRepository implements BaseRepositoryInterface<Surtidor> {

    private EntityManager entityManager;

    public SurtidorRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Surtidor save(Surtidor entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public Surtidor update(Surtidor entity) {
        return entityManager.merge(entity);
    }

    @Override
    public void delete(Surtidor entity) {
        entityManager.remove(entity);
    }

    @Override
    public Optional<Surtidor> findById(Object id) {
        return Optional.ofNullable(entityManager.find(Surtidor.class, id));
    }

    @Override
    public List<Surtidor> findAll() {
        TypedQuery<Surtidor> query = entityManager.createQuery("SELECT s FROM Surtidor s", Surtidor.class);
        return query.getResultList();
    }

    @Override
    public List<Surtidor> findByQuery(String jpql, Object... parameters) {
        TypedQuery<Surtidor> query = entityManager.createQuery(jpql, Surtidor.class);
        for (int i = 0; i < parameters.length; i++) {
            query.setParameter(i + 1, parameters[i]);
        }
        return query.getResultList();
    }

    @Override
    public List<?> findByNativeQuery(String sql, Object... parameters) {
        Query query = entityManager.createNativeQuery(sql);
        for (int i = 0; i < parameters.length; i++) {
            query.setParameter(i + 1, parameters[i]);
        }
        return query.getResultList();
    }

    public int executeNativeUpdate(String sql, Object... parameters) {
        Query query = entityManager.createNativeQuery(sql);
        for (int i = 0; i < parameters.length; i++) {
            query.setParameter(i + 1, parameters[i]);
        }
        return query.executeUpdate();
    }

    // Métodos específicos para Surtidor
    public boolean ocultarInputsFacturaVenta(int cara) {
        try {
            String nativeQuery = SqlQueryEnum.OCULTAR_INPUTS_FACTURA_VENTA.getQuery();
            Query query = entityManager.createNativeQuery(nativeQuery);
            query.setParameter(1, cara);
            
            Object result = query.getSingleResult();
            return result != null && (Boolean) result;
        } catch (Exception e) {
            NovusUtils.printLn("Error en ocultarInputsFacturaVenta: " + e.getMessage());
            return false;
        }
    }

    public List<Surtidor> findBySurtidorAndCara(int surtidor, int cara) {
        String jpql = "SELECT s FROM Surtidor s WHERE s.surtidor = ?1 AND s.cara = ?2";
        return findByQuery(jpql, surtidor, cara);
    }

    public List<Surtidor> findByEstado(String estado) {
        String jpql = "SELECT s FROM Surtidor s WHERE s.estado = ?1";
        return findByQuery(jpql, estado);
    }

    public boolean habilitarBotonesFamiliaAutorizacion(String familia) {
        try {
            String nativeQuery = SqlQueryEnum.HABILITAR_BOTONES_FAMILIA_AUTORIZACION.getQuery();
            Query query = entityManager.createNativeQuery(nativeQuery);
            query.setParameter(1, familia);
            
            Object result = query.getSingleResult();
            return result != null && (Boolean) result;
        } catch (Exception e) {
            NovusUtils.printLn("Error en habilitarBotonesFamiliaAutorizacion: " + e.getMessage());
            return false;
        }
    }

    public boolean validarCorreccionSaltoLectura(JsonObject detailHose) {
        try {
            String nativeQuery = SqlQueryEnum.VALIDAR_CORRECCION_SALTO_LECTURA.getQuery();
            Query query = entityManager.createNativeQuery(nativeQuery);
            
            query.setParameter(1, detailHose.get("surtidor").getAsInt());
            query.setParameter(2, detailHose.get("manguera").getAsInt());
            query.setParameter(3, detailHose.get("cara").getAsInt());
            
            List<?> results = query.getResultList();
            return results.isEmpty(); // Si no hay resultados, significa que está corregido
            
        } catch (Exception e) {
            NovusUtils.printLn("Error en validarCorreccionSaltoLectura: " + e.getMessage());
            return true; // Por defecto asumimos que está corregido en caso de error
        }
    }

    public void actualizarNovedadSaltoLectura(JsonObject detailHose) {
        entityManager.getTransaction().begin();
        try {
            String nativeQuery = SqlQueryEnum.ACTUALIZAR_NOVEDAD_SALTO_LECTURA.getQuery();
            Query query = entityManager.createNativeQuery(nativeQuery);
            
            query.setParameter(1, detailHose.get("surtidor").getAsInt());
            query.setParameter(2, detailHose.get("manguera").getAsInt());
            query.setParameter(3, detailHose.get("cara").getAsInt());
            
            query.executeUpdate();
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            NovusUtils.printLn("Error al actualizar Novedad salto de lectura: " + e.getMessage());
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
        }
    }

    public String getCodigoExternoProducto(long cara, long grado) {
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.GET_CODIGO_EXTERNO_PRODUCTO.getQuery());
            query.setParameter(1, cara);
            query.setParameter(2, grado);
            return (String) query.getSingleResult();
        } catch (Exception e) {
            NovusUtils.printLn("Error en getCodigoExternoProducto: " + e.getMessage());
            return null;
        }
    }

    public JsonArray obtenerInfoSurtidoresEstacion() {
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.OBTENER_INFO_SURTIDORES_ESTACION.getQuery());
            List<Object[]> results = query.getResultList();
            JsonArray infoSurtidoresEstacion = new JsonArray();
            
            for (Object[] row : results) {
                JsonObject jsonSurtidor = new JsonObject();
                jsonSurtidor.addProperty("host", (String) row[0]);
                if (row[2] != null && ((Number) row[2]).longValue() == equipoId()) {
                    jsonSurtidor.addProperty("host", "localhost");
                }
                jsonSurtidor.addProperty("isla", ((Number) row[1]).longValue());
                JsonArray surtidores = new Gson().fromJson((String) row[3], JsonArray.class);
                jsonSurtidor.add("surtidores", surtidores);
                infoSurtidoresEstacion.add(jsonSurtidor);
            }
            return infoSurtidoresEstacion;
        } catch (Exception e) {
            NovusUtils.printLn("Error en obtenerInfoSurtidoresEstacion: " + e.getMessage());
            return new JsonArray();
        }
    }

    private long equipoId() {
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.EQUIPO_ID.getQuery());
            Object result = query.getSingleResult();
            return result != null ? ((Number) result).longValue() : 0;
        } catch (Exception e) {
            NovusUtils.printLn("Error al obtener ID del equipo: " + e.getMessage());
            return 0;
        }
    }

    public int getTimeoutTotalizadores() {
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.GET_TIMEOUT_TOTALIZADORES.getQuery());
            Object result = query.getSingleResult();
            return result != null ? Integer.parseInt(result.toString()) : 30000;
        } catch (Exception e) {
            NovusUtils.printLn("Error en getTimeoutTotalizadores: " + e.getMessage());
            return 30000;
        }
    }

    public JsonArray obtenerHostSurtidoresEstacion() {
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.OBTENER_HOST_SURTIDORES_ESTACION.getQuery());
            List<Object[]> results = query.getResultList();
            JsonArray infoHostSurtidores = new JsonArray();
            
            for (Object[] row : results) {
                JsonObject jsonSurtidor = new JsonObject();
                jsonSurtidor.addProperty("host", (String) row[0]);
                if (row[2] != null && ((Number) row[2]).longValue() == equipoId()) {
                    jsonSurtidor.addProperty("host", "localhost");
                }
                jsonSurtidor.addProperty("isla", ((Number) row[1]).longValue());
                infoHostSurtidores.add(jsonSurtidor);
            }
            return infoHostSurtidores;
        } catch (Exception e) {
            NovusUtils.printLn("Error en obtenerHostSurtidoresEstacion: " + e.getMessage());
            return new JsonArray();
        }
    }

    public JsonObject obtenerCapacidadMaxima(long tanque) {
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.OBTENER_CAPACIDAD_MAXIMA.getQuery());
            query.setParameter(1, tanque);
            Object result = query.getSingleResult();
            
            if (result != null) {
                try {
                    Object[] row = (Object[]) result;
                    if (row != null && row.length > 0 && row[0] != null) {
                        String jsonStr = row[3].toString().trim();
                        if (jsonStr.startsWith("{") && jsonStr.endsWith("}")) {
                            JsonElement element = new JsonParser().parse(jsonStr);
                            if (element.isJsonObject()) {
                                JsonObject jsonObj = element.getAsJsonObject();
                                // Validar campos requeridos
                                if (jsonObj.has("altura_maxima") && jsonObj.has("volumen_maximo")) {
                                    return jsonObj;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing JSON: " + e.getMessage());
                }
            }
            return new JsonObject();
        } catch (Exception e) {
            NovusUtils.printLn("Error en obtenerCapacidadMaxima: " + e.getMessage());
            return new JsonObject();
        }
    }

    public void setEstadoMovimiento(long id) {
        entityManager.getTransaction().begin();
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.SET_ESTADO_MOVIMIENTO.getQuery());
            query.setParameter(1, id);
            query.executeUpdate();
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            NovusUtils.printLn("Error en setEstadoMovimiento: " + e.getMessage());
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
        }
    }

    public void borrarRecepcion(long id) {
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.BORRAR_RECEPCION.getQuery());
            query.setParameter(1, id);
            query.executeUpdate();
        } catch (Exception e) {
            NovusUtils.printLn("Error en borrarRecepcion: " + e.getMessage());
            throw e; // Re-lanzar la excepción para que el caso de uso la maneje
        }
    }

    public RecepcionBean actualizarRecepcionCombustible(RecepcionBean bean) {
        entityManager.getTransaction().begin();
        try {
            Query query;
            if (bean.getId() == 0) {
                query = entityManager.createNativeQuery(SqlQueryEnum.INSERTAR_RECEPCION_COMBUSTIBLE.getQuery());
            } else {
                query = entityManager.createNativeQuery(SqlQueryEnum.ACTUALIZAR_RECEPCION_COMBUSTIBLE.getQuery());
            }
            query.setParameter(1, bean.getPromotor());
            query.setParameter(2, bean.getDocumento());
            query.setParameter(3, bean.getPlaca());
            query.setParameter(4, bean.getTanqueId());
            query.setParameter(5, bean.getProductoId());
            query.setParameter(6, bean.getCantidad());
            query.setParameter(7, bean.getFecha());
            query.setParameter(8, bean.getAlturaInicial());
            query.setParameter(9, bean.getVolumenInicial());
            query.setParameter(10, bean.getAguaInicial());
            if (bean.getId() != 0) {
                query.setParameter(11, bean.getId());
            }
            Object result = query.getSingleResult();
            if (result != null) {
                bean.setId(((Number) result).longValue());
            }
            entityManager.getTransaction().commit();
            return bean;
        } catch (Exception e) {
            NovusUtils.printLn("Error en actualizarRecepcionCombustible: " + e.getMessage());
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            return null;
        }
    }

    public List<RecepcionBean> getRecepciones() {
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.GET_RECEPCIONES.getQuery());
            List<Object[]> results = query.getResultList();
            List<RecepcionBean> recepciones = new ArrayList<>();
            
            for (Object[] row : results) {
                RecepcionBean bean = new RecepcionBean();
                bean.setId(((Number) row[0]).longValue());
                bean.setPromotor(((Number) row[1]).longValue());
                bean.setDocumento((String) row[2]);
                bean.setPlaca((String) row[3]);
                bean.setTanqueId(((Number) row[4]).longValue());
                bean.setProductoId(((Number) row[5]).longValue());
                bean.setCantidad(((Number) row[6]).intValue());
                bean.setFecha((Date) row[7]);
                bean.setAlturaInicial(((Number) row[8]).floatValue());
                bean.setVolumenInicial(((Number) row[9]).doubleValue());
                bean.setAguaInicial(((Number) row[10]).floatValue());
                if (row[11] != null) {
                    bean.setProductoDescripcion((String) row[11]);
                }
                recepciones.add(bean);
            }
            return recepciones;
        } catch (Exception e) {
            NovusUtils.printLn("Error en getRecepciones: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean fueFidelizada(long idMovimiento) {
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.FUE_FIDELIZADA.getQuery());
            query.setParameter(1, idMovimiento);
            Object result = query.getSingleResult();
            
            if (result != null) {
                String atributos = null;
                
                // Manejar tanto String como PGobject para campos JSON
                if (result instanceof String) {
                    atributos = (String) result;
                } else if (result instanceof org.postgresql.util.PGobject) {
                    org.postgresql.util.PGobject pgObject = (org.postgresql.util.PGobject) result;
                    atributos = pgObject.getValue();
                }
                
                if (atributos != null) {
                    JsonObject json = new Gson().fromJson(atributos, JsonObject.class);
                    if (!json.isJsonNull() && json.get("fidelizada") != null && 
                        !json.get("fidelizada").isJsonNull() && 
                        json.get("fidelizada").getAsString().equals("S")) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            NovusUtils.printLn("Error en fueFidelizada: " + e.getMessage());
            return false;
        }
    }

    public void actualizarFidelizacion(Long id) {
        try {
            List<?> result = findByNativeQuery(SqlQueryEnum.OBTENER_ATRIBUTOS_MOVIMIENTO.getQuery(), id);
            if (!result.isEmpty()) {
                Object rawAtributos = result.get(0);
                String atributos = null;
                if (rawAtributos instanceof String) {
                    atributos = (String) rawAtributos;
                } else if (rawAtributos instanceof org.postgresql.util.PGobject) {
                    atributos = ((org.postgresql.util.PGobject) rawAtributos).getValue();
                }
                if (atributos != null) {
                    JsonObject json = new Gson().fromJson(atributos, JsonObject.class);
                    if (!json.isJsonNull()) {
                        json.addProperty("editarFidelizacion", false);
                        javax.persistence.Query query = entityManager.createNativeQuery(SqlQueryEnum.ACTUALIZAR_FIDELIZACION.getQuery());
                        query.setParameter(1, json.toString());
                        query.setParameter(2, id);
                        query.executeUpdate();
                    }
                }
            }
        } catch (Exception e) {
            NovusUtils.printLn("Error al actualizar fidelización: " + e.getMessage());
            throw e;
        }
    }

    public void crearPosVentaFE(int cara, int manguera, JsonObject clienteJson) {
        entityManager.getTransaction().begin();
        try {
            String TAREA_FACTURA_ELECTRONICA = "3";
            executeNativeUpdate(
                SqlQueryEnum.CREAR_POS_VENTA_FE.getQuery(),
                cara,
                manguera,
                TAREA_FACTURA_ELECTRONICA,
                clienteJson.toString()
            );
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            NovusUtils.printLn("Error al crear venta de factura electrónica: " + e.getMessage());
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
        }
    }

    public boolean actualizarVentaParaImprimir(int cara, int manguera) {
        entityManager.getTransaction().begin();
        try {
            List<?> result = findByNativeQuery(SqlQueryEnum.OBTENER_ATRIBUTOS_VENTA_CURSO.getQuery(), cara, manguera);
            if (!result.isEmpty()) {
                String atributos = (String) result.get(0);
                if (atributos != null) {
                    JsonObject json = new Gson().fromJson(atributos, JsonObject.class);
                    if (json.has("remision")) {
                        json.get("remision").getAsJsonObject().addProperty("imprimir", true);
                    } else if (json.has("factura_electronica")) {
                        json.get("factura_electronica").getAsJsonObject().addProperty("imprimir", true);
                    }
                    
                    int updateResult = executeNativeUpdate(
                        SqlQueryEnum.ACTUALIZAR_VENTA_PARA_IMPRIMIR.getQuery(),
                        json.toString(),
                        cara,
                        manguera
                    );
                    entityManager.getTransaction().commit();
                    return updateResult > 0;
                }
            }
            entityManager.getTransaction().commit();
            return false;
        } catch (Exception e) {
            NovusUtils.printLn("Error al actualizar venta para imprimir: " + e.getMessage());
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            return false;
        }
    }

    public long getIdMedioPagoVentaCurso(int cara) {
        try {
            List<?> result = findByNativeQuery(SqlQueryEnum.OBTENER_ID_MEDIO_PAGO_VENTA_CURSO.getQuery(), cara);
            if (!result.isEmpty()) {
                long respuesta = ((Number) result.get(0)).longValue();
                return respuesta == 0 ? 1 : respuesta;
            }
            return 1;
        } catch (Exception e) {
            NovusUtils.printLn("Error al obtener ID del medio de pago de la venta en curso: " + e.getMessage());
            return 1;
        }
    }

    /**
     * Obtiene el ID_MOVIMIENTO (PK de la tabla MOVIMIENTOS) asociado a la cara del surtidor.
     * Usa la consulta nativa OBTENER_MOVIMIENTO_ID_DESDE_CARA, que debe retornar en su
     * primera columna el ID_MOVIMIENTO.
     *
     * @param cara Número de cara del surtidor (>0).
     * @return ID_MOVIMIENTO; 0L si no hay registro o ocurre un error.
     * @throws IllegalArgumentException Si cara <= 0.
     */
    public long getMovimientoIdDesdeVentaCurso(int cara) {
        try {
            List<?> result = findByNativeQuery(SqlQueryEnum.OBTENER_PRODUCTO_ID_DESDE_VENTA_CURSO.getQuery(), cara);
            if (!result.isEmpty()) {
                return ((Number) result.get(0)).longValue();
            }
            return 0L;
        } catch (Exception e) {
            NovusUtils.printLn("Error al obtener ID del movimiento desde venta curso: " + e.getMessage());
            return 0L;
        }
    }
} 