package com.infrastructure.repositories;

import com.bean.BodegaBean;
import com.bean.entradaCombustible.EntradaCombustibleBean;
import com.bean.entradaCombustible.EntradaCombustibleHistorialBean;
import com.bean.entradaCombustible.ConfirmarRemisionParams;
import com.bean.entradaCombustible.ProductoSAP;
import com.domain.entities.RemisionSapEntity;
import com.infrastructure.Enums.SqlQueryEnum;
import com.infrastructure.core.BaseRepositoryInterface;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.firefuel.Main;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class EntradaCombustibleRepository implements BaseRepositoryInterface<Object> {
    
    private final EntityManager entityManager;
    
    public EntradaCombustibleRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    /**
     * 🚀 MIGRACIÓN: Obtiene información de entrada de remisión por delivery
     * Equivale al método infoEntradaRemision() del DAO original
     * 
     * @param delivery número de entrega/delivery de la remisión
     * @return EntradaCombustibleBean con información de la remisión o null si no existe
     */
    public EntradaCombustibleBean obtenerInfoEntradaRemision(String delivery) {
        try {
            // 🔍 DEBUG: Mostrar la consulta que se va a ejecutar
            String queryString = SqlQueryEnum.INFO_ENTRADA_REMISION.getQuery();
            System.out.println("🔍 DEBUG: Ejecutando consulta: " + queryString);
            System.out.println("🔍 DEBUG: Parámetro delivery: '" + delivery + "'");
            
            Query query = entityManager.createNativeQuery(queryString);
            query.setParameter(1, delivery);
            
            // 🔍 DEBUG: Intentar obtener resultado
            Object[] resultado = (Object[]) query.getSingleResult();

            if (resultado != null) {
                System.out.println("✅ DEBUG: Resultado encontrado, mapeando...");
                return mapearResultadoABean(resultado);
            }
        } catch (NoResultException ex) {
            System.out.println("🔍 No se encontró remisión para delivery: " + delivery);
            System.out.println("🔍 DEBUG: NoResultException capturada - la consulta no retornó resultados");
            return null;
        } catch (Exception ex) {
            System.err.println("❌ Error en EntradaCombustibleRepository.obtenerInfoEntradaRemision(): " + ex.getMessage());
            System.err.println("🔍 DEBUG: Tipo de excepción: " + ex.getClass().getSimpleName());
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * 🔧 Mapea el resultado de la consulta nativa a EntradaCombustibleBean
     * 
     * @param resultado array de objetos del resultado de la consulta
     * @return EntradaCombustibleBean mapeado
     */
    private EntradaCombustibleBean mapearResultadoABean(Object[] resultado) {
        EntradaCombustibleBean bean = new EntradaCombustibleBean();
        try {
            System.out.println("🔍 DEBUG: Mapeando resultado con " + resultado.length + " columnas");
            for (int i = 0; i < resultado.length; i++) {
                System.out.println("🔍 DEBUG: Columna " + i + ": " + (resultado[i] != null ? resultado[i].getClass().getSimpleName() + " = " + resultado[i] : "null"));
            }
            // Mapeo de los 13 campos
            bean.setIdRemision(resultado[0] != null ? ((Number) resultado[0]).longValue() : null); // id_remision_sap
            bean.setDelivery(resultado[1] != null ? resultado[1].toString() : ""); // delivery
            bean.setDocumentDate(resultado[2] != null ? resultado[2].toString() : ""); // document_date
            bean.setWayBill(resultado[3] != null ? resultado[3].toString() : ""); // way_bill
            bean.setLogisticCenter(resultado[4] != null ? resultado[4].toString() : ""); // logistic_center
            bean.setSupplyingCenter(resultado[5] != null ? resultado[5].toString() : ""); // supplying_center
            bean.setStatus(resultado[6] != null ? resultado[6].toString() : ""); // status
            bean.setModificationDate(resultado[7] != null ? resultado[7].toString() : ""); // modification_date (Date/Time)
            bean.setModificationHour(resultado[8] != null ? resultado[8].toString() : ""); // modification_hour (Date/Time)
            bean.setFrontierLaw(resultado[9] != null ? resultado[9].toString() : ""); // frontier_law
            // Si tu bean tiene más campos, mapea los siguientes:
            // Por ejemplo:
            // bean.setCampo10(resultado[10] != null ? resultado[10].toString() : "");
            // bean.setCampo11(resultado[11] != null ? resultado[11].toString() : "");
            // bean.setCampo12(resultado[12] != null ? resultado[12].toString() : "");
            System.out.println("✅ DEBUG: Mapeo completado exitosamente");
        } catch (Exception ex) {
            System.err.println("❌ Error al mapear resultado a EntradaCombustibleBean: " + ex.getMessage());
            System.err.println("🔍 DEBUG: Error en mapeo - tipo de excepción: " + ex.getClass().getSimpleName());
            throw new RuntimeException("Error en mapeo de datos de remisión", ex);
        }
        return bean;
    }
    
    /**
     * 🔧 Método auxiliar para convertir cualquier tipo de dato a String de forma segura
     * 
     * @param valor el valor a convertir
     * @return String convertido o " " si es null
     */
    private String convertirAString(Object valor) {
        if (valor == null) {
            return " ";
        }
        
        if (valor instanceof String) {
            return (String) valor;
        } else if (valor instanceof java.sql.Date) {
            return ((java.sql.Date) valor).toString();
        } else if (valor instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) valor).toString();
        } else if (valor instanceof java.util.Date) {
            return ((java.util.Date) valor).toString();
        } else if (valor instanceof java.time.LocalDate) {
            return ((java.time.LocalDate) valor).toString();
        } else if (valor instanceof java.time.LocalDateTime) {
            return ((java.time.LocalDateTime) valor).toString();
        } else {
            return valor.toString();
        }
    }
    
    /**
     * 🔧 Método auxiliar para convertir cualquier tipo de dato a entero de forma segura
     * 
     * @param valor el valor a convertir
     * @param valorPorDefecto valor a retornar si la conversión falla
     * @return int convertido o valorPorDefecto si falla
     */
    private int convertirAEntero(Object valor, int valorPorDefecto) {
        if (valor == null) {
            return valorPorDefecto;
        }
        
        try {
            if (valor instanceof Integer) {
                return (Integer) valor;
            } else if (valor instanceof BigInteger) {
                return ((BigInteger) valor).intValue();
            } else if (valor instanceof Long) {
                return ((Long) valor).intValue();
            } else if (valor instanceof Number) {
                return ((Number) valor).intValue();
            } else {
                return Integer.parseInt(valor.toString());
            }
        } catch (NumberFormatException e) {
            System.err.println("⚠️ Error al convertir a entero: " + valor + ", usando valor por defecto: " + valorPorDefecto);
            return valorPorDefecto;
        }
    }
    
    /**
     * 🔧 Método auxiliar para convertir cualquier tipo de dato a long de forma segura
     * 
     * @param valor el valor a convertir
     * @param valorPorDefecto valor a retornar si la conversión falla
     * @return long convertido o valorPorDefecto si falla
     */
    private long convertirALong(Object valor, long valorPorDefecto) {
        if (valor == null) {
            return valorPorDefecto;
        }
        
        try {
            if (valor instanceof Long) {
                return (Long) valor;
            } else if (valor instanceof BigInteger) {
                return ((BigInteger) valor).longValue();
            } else if (valor instanceof Integer) {
                return ((Integer) valor).longValue();
            } else if (valor instanceof Number) {
                return ((Number) valor).longValue();
            } else {
                return Long.parseLong(valor.toString());
            }
        } catch (NumberFormatException e) {
            System.err.println("⚠️ Error al convertir a long: " + valor + ", usando valor por defecto: " + valorPorDefecto);
            return valorPorDefecto;
        }
    }
    
    /**
     * 🔧 Método auxiliar para convertir cualquier tipo de dato a float de forma segura
     * 
     * @param valor el valor a convertir
     * @param valorPorDefecto valor a retornar si la conversión falla
     * @return float convertido o valorPorDefecto si falla
     */
    private float convertirAFloat(Object valor, float valorPorDefecto) {
        if (valor == null) {
            return valorPorDefecto;
        }
        
        try {
            if (valor instanceof Float) {
                return (Float) valor;
            } else if (valor instanceof Number) {
                return ((Number) valor).floatValue();
            } else {
                return Float.parseFloat(valor.toString());
            }
        } catch (NumberFormatException e) {
            System.err.println("⚠️ Error al convertir a float: " + valor + ", usando valor por defecto: " + valorPorDefecto);
            return valorPorDefecto;
        }
    }
    
    /**
     * 🔧 Método auxiliar para convertir cualquier tipo de dato a double de forma segura
     * 
     * @param valor el valor a convertir
     * @param valorPorDefecto valor a retornar si la conversión falla
     * @return double convertido o valorPorDefecto si falla
     */
    private double convertirADouble(Object valor, double valorPorDefecto) {
        if (valor == null) {
            return valorPorDefecto;
        }
        
        try {
            if (valor instanceof Double) {
                return (Double) valor;
            } else if (valor instanceof Number) {
                return ((Number) valor).doubleValue();
            } else {
                return Double.parseDouble(valor.toString());
            }
        } catch (NumberFormatException e) {
            System.err.println("⚠️ Error al convertir a double: " + valor + ", usando valor por defecto: " + valorPorDefecto);
            return valorPorDefecto;
        }
    }
    
    /**
     * 🔧 Método auxiliar para convertir cualquier tipo de dato a boolean de forma segura
     * 
     * @param valor el valor a convertir
     * @param valorPorDefecto valor a retornar si la conversión falla
     * @return boolean convertido o valorPorDefecto si falla
     */
    private boolean convertirABoolean(Object valor, boolean valorPorDefecto) {
        if (valor == null) {
            return valorPorDefecto;
        }
        
        try {
            if (valor instanceof Boolean) {
                return (Boolean) valor;
            } else if (valor instanceof Number) {
                return ((Number) valor).intValue() != 0;
            } else {
                return Boolean.parseBoolean(valor.toString());
            }
        } catch (NumberFormatException e) {
            System.err.println("⚠️ Error al convertir a boolean: " + valor + ", usando valor por defecto: " + valorPorDefecto);
            return valorPorDefecto;
        }
    }
    
    /**
     * 🚀 MIGRACIÓN: Obtiene tanques de remisión usando native query de SqlQueryEnum
     * Equivale al método getTanquesRemision() del DAO original
     * 
     * @param delivery número de entrega/delivery de la remisión
     * @return Map con tanques agrupados por producto
     */
    public Map<String, ArrayList<BodegaBean>> obtenerTanquesRemision(String delivery) {
        HashMap<String, ArrayList<BodegaBean>> tanques = new HashMap<>();
        try {
            Query query = entityManager.createNativeQuery(
                SqlQueryEnum.OBTENER_TANQUES_REMISION.getQuery()
            );
            query.setParameter(1, delivery);
            @SuppressWarnings("unchecked")
            List<Object[]> resultados = query.getResultList();
            for (Object[] fila : resultados) {
                BodegaBean tanque = new BodegaBean();
                // id
                int id = 0;
                if (fila[0] instanceof Number) {
                    id = ((Number) fila[0]).intValue();
                } else if (fila[0] != null) {
                    id = (int) Double.parseDouble(fila[0].toString());
                }
                tanque.setId(id);
                // bodega
                tanque.setDescripcion(fila[1] != null ? fila[1].toString() : "");
                // codigo
                tanque.setCodigo(fila[2] != null ? fila[2].toString() : "");
                // numero
                int numero = 0;
                if (fila[3] instanceof Number) {
                    numero = ((Number) fila[3]).intValue();
                } else if (fila[3] != null) {
                    numero = (int) Double.parseDouble(fila[3].toString());
                }
                tanque.setNumeroStand(numero);
                // volumen_maximo
                int volumenMaximo = 0;
                if (fila[4] instanceof Number) {
                    volumenMaximo = ((Number) fila[4]).intValue();
                } else if (fila[4] != null) {
                    volumenMaximo = (int) Double.parseDouble(fila[4].toString());
                }
                tanque.setVolumenMaximo(volumenMaximo);
                // familia
                long familia = 0L;
                if (fila[5] instanceof Number) {
                    familia = ((Number) fila[5]).longValue();
                } else if (fila[5] != null) {
                    familia = (long) Double.parseDouble(fila[5].toString());
                }
                tanque.setFamiliaId(familia);
                // producto_id
                int productoId = 0;
                if (fila[6] instanceof Number) {
                    productoId = ((Number) fila[6]).intValue();
                } else if (fila[6] != null) {
                    productoId = (int) Double.parseDouble(fila[6].toString());
                }
                // id_remision_producto
                int idRemisionProducto = 0;
                if (fila[7] instanceof Number) {
                    idRemisionProducto = ((Number) fila[7]).intValue();
                } else if (fila[7] != null) {
                    idRemisionProducto = (int) Double.parseDouble(fila[7].toString());
                }
                // Agrupar tanques por producto
                String claveTanque = "P-" + productoId;
                if (tanques.containsKey(claveTanque)) {
                    tanques.get(claveTanque).add(tanque);
                } else {
                    ArrayList<BodegaBean> tanqueBodega = new ArrayList<>();
                    tanqueBodega.add(tanque);
                    tanques.put(claveTanque, tanqueBodega);
                }
            }
        } catch (Exception ex) {
            System.err.println("Error en EntradaCombustibleRepository.obtenerTanquesRemision(): " + ex.getMessage());
            ex.printStackTrace();
        }
        return tanques;
    }
    
    /**
     * 🚀 MIGRACIÓN: Obtiene historial de remisiones con límite de registros
     * Equivale al método infoHistorialRemisiones() del DAO original
     * 
     * @param registros número máximo de registros a retornar
     * @return JsonArray con historial de remisiones
     */
    public JsonArray obtenerHistorialRemisiones(long registros) {
        JsonArray info = new JsonArray();
        
        try {
            Query query = entityManager.createNativeQuery(
                SqlQueryEnum.INFO_HISTORIAL_REMISIONES.getQuery()
            );
            query.setParameter(1, registros);
            
            @SuppressWarnings("unchecked")
            List<Object[]> resultados = query.getResultList();
            
            for (Object[] fila : resultados) {
                EntradaCombustibleHistorialBean historialBean = mapearResultadoAHistorialBean(fila);
                JsonObject object = Main.gson.toJsonTree(historialBean).getAsJsonObject();
                info.add(object);
            }
            
        } catch (Exception ex) {
            System.err.println("❌ Error en EntradaCombustibleRepository.obtenerHistorialRemisiones(): " + ex.getMessage());
            ex.printStackTrace();
        }
        
        return info;
    }
    
    /**
     * 🔧 Mapea el resultado de la consulta nativa a EntradaCombustibleHistorialBean
     * 
     * @param resultado array de objetos del resultado de la consulta
     * @return EntradaCombustibleHistorialBean mapeado
     */
    private EntradaCombustibleHistorialBean mapearResultadoAHistorialBean(Object[] resultado) {
        EntradaCombustibleHistorialBean bean = new EntradaCombustibleHistorialBean();
        
        try {
            // Mapeo basado en la consulta de historial de remisiones
            bean.setDelivery(convertirAString(resultado[0])); // delivery
            bean.setProduct(convertirAString(resultado[1])); // product
            bean.setQuantity(convertirAFloat(resultado[2], 0.0f)); // quantity
            bean.setUnit(convertirAString(resultado[3])); // unit
            bean.setCreationDate(convertirAString(resultado[4])); // creation_date
            bean.setCreationHour(convertirAString(resultado[5])); // creation_hour
            bean.setModificationDate(convertirAString(resultado[6])); // modification_date
            bean.setModificationHour(convertirAString(resultado[7])); // modification_hour
            bean.setStatus(convertirAString(resultado[8])); // status
            
        } catch (Exception ex) {
            System.err.println("❌ Error al mapear resultado a EntradaCombustibleHistorialBean: " + ex.getMessage());
            throw new RuntimeException("Error en mapeo de datos de historial de remisión", ex);
        }
        
        return bean;
    }
    
    /**
     * 🚀 MIGRACIÓN: Obtiene ID de remisión por número de delivery
     * Equivale al método obtenerIdRemision() del DAO original
     * 
     * @param numeroRemision número de delivery de la remisión
     * @return Long con el ID de la remisión o 0L si no existe
     */
    public Long obtenerIdRemision(String numeroRemision) {
        try {
            Query query = entityManager.createNativeQuery(
                SqlQueryEnum.OBTENER_ID_REMISION.getQuery()
            );
            query.setParameter(1, numeroRemision);
            
            Object resultado = query.getSingleResult();
            
            if (resultado != null) {
                if (resultado instanceof BigInteger) {
                    return ((BigInteger) resultado).longValue();
                } else if (resultado instanceof Number) {
                    return ((Number) resultado).longValue();
                } else {
                    return Long.parseLong(resultado.toString());
                }
            }
            
            return 0L;
            
        } catch (NoResultException ex) {
            System.out.println("🔍 No se encontró remisión para delivery: " + numeroRemision);
            return 0L;
            
        } catch (Exception ex) {
            System.err.println("❌ Error en EntradaCombustibleRepository.obtenerIdRemision(): " + ex.getMessage());
            ex.printStackTrace();
            return 0L;
        }
    }
    
    /**
     * 🚀 MIGRACIÓN: Valida si una remisión está confirmada (estado = 2)
     * Equivale al método validarSiremisionEstaConfirmada() del DAO original
     * 
     * @param numeroRemision número de delivery de la remisión
     * @return boolean true si la remisión está confirmada, false en caso contrario
     */
    public boolean validarSiRemisionEstaConfirmada(String numeroRemision) {
        try {
            Query query = entityManager.createNativeQuery(
                SqlQueryEnum.VALIDAR_SI_REMISION_ESTA_CONFIRMADA.getQuery()
            );
            query.setParameter(1, numeroRemision);
            
            // Si encuentra al menos un resultado, la remisión está confirmada
            @SuppressWarnings("unchecked")
            List<Object[]> resultados = query.getResultList();
            
            boolean confirmada = !resultados.isEmpty();
            
            System.out.println(confirmada 
                ? "✅ Remisión " + numeroRemision + " está confirmada (estado = 2)"
                : "🔍 Remisión " + numeroRemision + " NO está confirmada o no existe");
            
            return confirmada;
            
        } catch (Exception ex) {
            System.err.println("❌ Error en EntradaCombustibleRepository.validarSiRemisionEstaConfirmada(): " + ex.getMessage());
            System.err.println("   Delivery: " + numeroRemision);
            ex.printStackTrace();
            
            // En caso de error, asume que no está confirmada por seguridad
            return false;
        }
    }
    
    /**
     * 🚀 MIGRACIÓN: Confirma una remisión actualizando su estado
     * Equivale al método confirmarRemision() del DAO original
     * 
     * @param params Parámetros de confirmación (ID de remisión y estado)
     * @return boolean true si se actualizó exitosamente, false en caso contrario
     */
    public boolean confirmarRemision(ConfirmarRemisionParams params) {
        try {
            Query query = entityManager.createNativeQuery(
                SqlQueryEnum.CONFIRMAR_REMISION.getQuery()
            );
            query.setParameter(1, params.getEstado());
            query.setParameter(2, params.getIdRemision());
            
            int resultadosAfectados = query.executeUpdate();
            
            boolean exitoso = resultadosAfectados > 0;
            
            if (exitoso) {
                System.out.println("✅ Remisión confirmada exitosamente");
                System.out.println("   ID Remisión: " + params.getIdRemision());
                System.out.println("   Nuevo Estado: " + params.getEstado());
                System.out.println("   Registros actualizados: " + resultadosAfectados);
            } else {
                System.err.println("⚠️ No se pudo confirmar la remisión");
                System.err.println("   ID Remisión: " + params.getIdRemision());
                System.err.println("   Estado deseado: " + params.getEstado());
                System.err.println("   Registros afectados: " + resultadosAfectados);
            }
            
            return exitoso;
            
        } catch (Exception ex) {
            System.err.println("❌ Error en EntradaCombustibleRepository.confirmarRemision(): " + ex.getMessage());
            System.err.println("   Parámetros: " + params);
            ex.printStackTrace();
            
            // En caso de error, retorna false
            return false;
        }
    }
    
    /**
     * �� MIGRACIÓN: Obtiene el detalle de productos de una remisión
     * Equivale al método obtenerDetalleRemision() del DAO original
     * 
     * @param idRemision ID de la remisión
     * @return Map con productos SAP agrupados por clave
     */
    public Map<String, ProductoSAP> obtenerDetalleRemision(Long idRemision) {
        Map<String, ProductoSAP> productos = new TreeMap<>();
        
        try {
            System.out.println("🔍 EntradaCombustibleRepository.obtenerDetalleRemision() - Buscando productos para remisión ID: " + idRemision);
            
            String sql = "SELECT " +
                "trps.id_remision_producto, " +
                "trps.id_producto, " +
                "trps.product, " +
                "trps.quantity, " +
                "trps.unit, " +
                "trps.sales_cost_value, " +
                "p.descripcion " +
                "FROM sap.tbl_remisiones_productos_sap trps " +
                "INNER JOIN productos p ON trps.id_producto = p.id " +
                "WHERE trps.id_remision_sap = ?";
            
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, idRemision);
            
            @SuppressWarnings("unchecked")
            List<Object[]> resultados = query.getResultList();
            
            System.out.println("🔍 Productos encontrados: " + resultados.size());
            
            for (Object[] fila : resultados) {
                ProductoSAP productoSAP = new ProductoSAP();
                
                try {
                    // Mapeo basado en el orden de columnas de la consulta
                    productoSAP.setIdRemisionProducto(convertirALong(fila[0], 0L)); // id_remision_producto
                    int idProducto = convertirAEntero(fila[1], 0); // id_producto
                    productoSAP.setProducID(idProducto);
                    productoSAP.setProduct(convertirAString(fila[2])); // product
                    productoSAP.setQuantity(convertirAFloat(fila[3], 0.0f)); // quantity
                    productoSAP.setUnit(convertirAString(fila[4])); // unit
                    productoSAP.setSalesCostValue(convertirAFloat(fila[5], 0.0f)); // sales_cost_value
                    productoSAP.setDescripcion(convertirAString(fila[6])); // descripcion
                    productoSAP.setClave("P-" + idProducto);
                    
                    productos.put("P-" + idProducto, productoSAP);
                    
                    System.out.println("✅ Producto mapeado: " + productoSAP.getDescripcion() + " (ID: " + idProducto + ")");
                    
                } catch (Exception e) {
                    System.err.println("❌ Error al procesar fila de producto: " + e.getMessage());
                    System.err.println("📊 Datos de la fila: " + java.util.Arrays.toString(fila));
                    continue;
                }
            }
            
        } catch (Exception ex) {
            System.err.println("❌ Error en EntradaCombustibleRepository.obtenerDetalleRemision(): " + ex.getMessage());
            ex.printStackTrace();
        }
        
        return productos;
    }
    
    // 🔧 Implementaciones requeridas por BaseRepositoryInterface
    
    @Override
    public Object save(Object entity) {
        throw new UnsupportedOperationException("Método save no implementado para EntradaCombustibleRepository");
    }
    
    @Override
    public Object update(Object entity) {
        throw new UnsupportedOperationException("Método update no implementado para EntradaCombustibleRepository");
    }
    
    @Override
    public void delete(Object entity) {
        throw new UnsupportedOperationException("Método delete no implementado para EntradaCombustibleRepository");
    }
    
    @Override
    public Optional<Object> findById(Object id) {
        throw new UnsupportedOperationException("Método findById no implementado para EntradaCombustibleRepository");
    }
    
    @Override
    public List<Object> findAll() {
        throw new UnsupportedOperationException("Método findAll no implementado para EntradaCombustibleRepository");
    }
    
    @Override
    public List<Object> findByQuery(String query, Object... parameters) {
        throw new UnsupportedOperationException("Método findByQuery no implementado para EntradaCombustibleRepository");
    }
    
    @Override
    public List<?> findByNativeQuery(String query, Object... parameters) {
        throw new UnsupportedOperationException("Método findByNativeQuery no implementado para EntradaCombustibleRepository");
    }
} 