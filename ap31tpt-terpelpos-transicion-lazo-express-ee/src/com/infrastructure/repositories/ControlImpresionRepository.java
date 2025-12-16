package com.infrastructure.repositories;

import com.firefuel.controlImpresion.dto.Venta;
import com.infrastructure.Enums.SqlQueryEnum;
import com.infrastructure.core.BaseRepositoryInterface;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;
import java.util.TreeMap;

/**
 * 🚀 MIGRACIÓN: Repositorio para operaciones de control de impresión
 * Reemplaza consultas directas en ControlImpresionDao
 * 
 * ARQUITECTURA LIMPIA:
 * - Implementa BaseRepositoryInterface
 * - Utiliza consultas nativas desde SqlQueryEnum
 * - Manejo de errores y conversiones de tipos
 * 
 * @author Clean Architecture Migration
 * @version 1.0
 */
public class ControlImpresionRepository implements BaseRepositoryInterface<Venta> {
    
    private final EntityManager entityManager;
    
    public ControlImpresionRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    /**
     * 🚀 MIGRACIÓN: Obtiene ventas pendientes de impresión
     * Equivale al método ventasPedientesImpresion() del DAO original
     * 
     * @return TreeMap con las ventas pendientes de impresión (usando intervalo fijo de 15 minutes)
     */
    public TreeMap<Long, Venta> obtenerVentasPendientesImpresion() {
        TreeMap<Long, Venta> ventasPendientesImpresion = new TreeMap<>();
        
        try {
            System.out.println("[DEBUG] Ejecutando consulta con nombre = " + SqlQueryEnum.OBTENER_VENTAS_PENDIENTES_IMPRESION.getQuery());
            Query query = entityManager.createNativeQuery(
                SqlQueryEnum.OBTENER_VENTAS_PENDIENTES_IMPRESION.getQuery()
            );
            query.setParameter(1, "15 minutes");
            
            @SuppressWarnings("unchecked")
            List<Object[]> resultados = query.getResultList();
            
            for (Object[] resultado : resultados) {
                if (resultado != null) {
                    Venta venta = mapearResultadoAVenta(resultado);
                    if (venta != null) {
                        ventasPendientesImpresion.put(venta.getId(), venta);
                    }
                }
            }
            
            System.out.println("🔍 Ventas pendientes de impresión encontradas: " + ventasPendientesImpresion.size());
            
        } catch (Exception ex) {
            System.err.println("❌ Error en ControlImpresionRepository.obtenerVentasPendientesImpresion(): " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("Error al obtener ventas pendientes de impresión", ex);
        }
        
        return ventasPendientesImpresion;
    }
    
    /**
     * 🔧 Mapea el resultado de la consulta nativa a objeto Venta
     * 
     * ESTRUCTURA DE LA FUNCIÓN REAL EN BD:
     * - resultado[0]: id (integer)
     * - resultado[1]: fecha_actualizacion_cliente (timestamp)
     * - resultado[2]: placa_vehiculo (varchar)
     * 
     * @param resultado array de objetos del resultado de la consulta
     * @return Venta mapeada o null si hay error
     */
    private Venta mapearResultadoAVenta(Object[] resultado) {
        try {
            Venta venta = new Venta();
            
            // Mapeo de ID (campo 0) - La función retorna INTEGER
            if (resultado[0] != null) {
                if (resultado[0] instanceof Integer) {
                    venta.setId(((Integer) resultado[0]).longValue());
                } else if (resultado[0] instanceof BigInteger) {
                    venta.setId(((BigInteger) resultado[0]).longValue());
                } else if (resultado[0] instanceof Long) {
                    venta.setId((Long) resultado[0]);
                } else if (resultado[0] instanceof Number) {
                    venta.setId(((Number) resultado[0]).longValue());
                } else {
                    venta.setId(Long.parseLong(resultado[0].toString()));
                }
            } else {
                venta.setId(0L);
            }
            
            // Mapeo de fecha (campo 1) - fecha_actualizacion_cliente
            if (resultado[1] != null) {
                venta.setFecha((Timestamp) resultado[1]);
            }
            
            // Mapeo de placa (campo 2) - placa_vehiculo (campo directo, no JSON)
            if (resultado[2] != null) {
                venta.setPlaca((String) resultado[2]);
            }
            
            return venta;
            
        } catch (Exception ex) {
            System.err.println("❌ Error al mapear resultado a Venta: " + ex.getMessage());
            System.err.println("📊 Resultado recibido: " + java.util.Arrays.toString(resultado));
            return null;
        }
    }
    
    /**
     * 🎯 Método de conveniencia para verificar si hay ventas pendientes
     * 
     * @param intervalo Intervalo de tiempo
     * @return true si hay ventas pendientes, false en caso contrario
     */
    public boolean hayVentasPendientes(String intervalo) {
        TreeMap<Long, Venta> ventas = obtenerVentasPendientesImpresion();
        return !ventas.isEmpty();
    }
    
    /**
     * 🔢 Obtiene el conteo de ventas pendientes de impresión
     * 
     * @param intervalo Intervalo de tiempo
     * @return Número de ventas pendientes
     */
    public int contarVentasPendientes(String intervalo) {
        TreeMap<Long, Venta> ventas = obtenerVentasPendientesImpresion();
        return ventas.size();
    }
    
    /**
     * 🚀 MIGRACIÓN: Obtiene tiempo de impresión FE por código de parámetro
     * Equivale al método tiempoImpresionFE() del DAO original
     * 
     * @param codigoParametro Código del parámetro en wacher_parametros
     * @return Tiempo en segundos, 40 por defecto si no se encuentra
     */
    public int obtenerTiempoImpresionFE(String codigoParametro) {
        final int TIEMPO_POR_DEFECTO = 40;
        
        try {
            Query query = entityManager.createNativeQuery(
                SqlQueryEnum.OBTENER_TIEMPO_IMPRESION_FE.getQuery()
            );
            query.setParameter(1, codigoParametro);
            
            Object resultado = query.getSingleResult();
            
            if (resultado != null) {
                // Manejo robusto de diferentes tipos de números
                if (resultado instanceof Integer) {
                    return (Integer) resultado;
                } else if (resultado instanceof Long) {
                    return ((Long) resultado).intValue();
                } else if (resultado instanceof BigInteger) {
                    return ((BigInteger) resultado).intValue();
                } else if (resultado instanceof String) {
                    try {
                        return Integer.parseInt((String) resultado);
                    } catch (NumberFormatException ex) {
                        System.err.println("⚠️ Error al convertir valor de parámetro a entero: " + resultado);
                        return TIEMPO_POR_DEFECTO;
                    }
                } else {
                    return Integer.parseInt(resultado.toString());
                }
            }
            
        } catch (javax.persistence.NoResultException ex) {
            System.out.println("🔍 No se encontró parámetro: " + codigoParametro + ", usando valor por defecto: " + TIEMPO_POR_DEFECTO);
            return TIEMPO_POR_DEFECTO;
            
        } catch (Exception ex) {
            System.err.println("❌ Error en ControlImpresionRepository.obtenerTiempoImpresionFE(): " + ex.getMessage());
            ex.printStackTrace();
            System.out.println("🔄 Retornando valor por defecto: " + TIEMPO_POR_DEFECTO);
            return TIEMPO_POR_DEFECTO;
        }
        
        return TIEMPO_POR_DEFECTO;
    }
    
    /**
     * 🚀 MIGRACIÓN: Actualiza estado de impresión de un movimiento
     * Equivale al método actualizarEstadoImpresion() del DAO original
     * 
     * @param idVenta ID del movimiento/venta a actualizar
     * @return boolean true si se actualizó exitosamente, false en caso contrario
     */
    public boolean actualizarEstadoImpresion(long idVenta) {
        try {
            Query query = entityManager.createNativeQuery(
                SqlQueryEnum.ACTUALIZAR_ESTADO_IMPRESION.getQuery()
            );
            query.setParameter(1, idVenta);
            
            int filasAfectadas = query.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✅ Estado de impresión actualizado para venta ID: " + idVenta);
                return true;
            } else {
                System.out.println("⚠️ No se encontró movimiento con ID: " + idVenta);
                return false;
            }
            
        } catch (Exception ex) {
            System.err.println("❌ Error en ControlImpresionRepository.actualizarEstadoImpresion(): " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }
    
    // 🔧 Implementaciones requeridas por BaseRepositoryInterface
    
    @Override
    public Venta save(Venta entity) {
        throw new UnsupportedOperationException("Método save no implementado para ControlImpresionRepository");
    }
    
    @Override
    public Venta update(Venta entity) {
        throw new UnsupportedOperationException("Método update no implementado para ControlImpresionRepository");
    }
    
    @Override
    public void delete(Venta entity) {
        throw new UnsupportedOperationException("Método delete no implementado para ControlImpresionRepository");
    }
    
    @Override
    public java.util.Optional<Venta> findById(Object id) {
        throw new UnsupportedOperationException("Método findById no implementado para ControlImpresionRepository");
    }
    
    @Override
    public java.util.List<Venta> findAll() {
        throw new UnsupportedOperationException("Método findAll no implementado para ControlImpresionRepository");
    }
    
    @Override
    public java.util.List<Venta> findByQuery(String query, Object... parameters) {
        throw new UnsupportedOperationException("Método findByQuery no implementado para ControlImpresionRepository");
    }
    
    @Override
    public java.util.List<?> findByNativeQuery(String query, Object... parameters) {
        throw new UnsupportedOperationException("Método findByNativeQuery no implementado para ControlImpresionRepository");
    }
} 