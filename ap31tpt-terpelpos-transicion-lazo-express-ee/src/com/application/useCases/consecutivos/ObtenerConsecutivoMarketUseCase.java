package com.application.useCases.consecutivos;

import com.application.core.BaseUseCases;
import com.bean.ConsecutivoBean;
import com.controllers.NovusConstante;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.ConsecutivoRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * 🎯 UseCase: Obtener Consecutivo específico para MARKET
 * 
 * ⚠️ MÉTODO CRÍTICO - Maneja consecutivos de facturación para módulo Market
 * 
 * DIFERENCIAS CON ObtenerConsecutivoUseCase:
 * - Destino fijo: "MAR" (Market)
 * - Misma lógica de negocio pero especializado para Market
 * - Usado por InfoViewController.abrirStore()
 */
public class ObtenerConsecutivoMarketUseCase implements BaseUseCases<ConsecutivoBean> {
    
    private final boolean isFe;
    private final EntityManagerFactory entityManagerFactory;
    private static final String DESTINO_MARKET = "MAR";
    
    // 🏗️ Constructor con parámetro isFe
    public ObtenerConsecutivoMarketUseCase(boolean isFe) {
        this.isFe = isFe;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
    }
    
    /**
     * 🚀 Ejecutar lógica de obtención de consecutivo para Market
     * @return ConsecutivoBean válido o null si no hay disponibles
     */
    @Override
    public ConsecutivoBean execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            ConsecutivoRepository consecutivoRepository = new ConsecutivoRepository(em);
            
            // 🔹 1. DETERMINAR TIPO DE DOCUMENTO
            int tipoDocumento = isFe ? 31 : 9;
            
            // 🔹 2. CONSULTAR CONSECUTIVOS ACTIVOS PARA MARKET
            List<Object[]> resultados = consecutivoRepository.obtenerConsecutivo(tipoDocumento, DESTINO_MARKET);
            
            if (resultados.isEmpty()) {
                System.out.println("ℹ️ No se encontraron consecutivos para Market tipo=" + tipoDocumento + " destino=" + DESTINO_MARKET);
                return null;
            }
            
            // 🔹 3. CONVERTIR RESULTADOS A BEANS
            ArrayList<ConsecutivoBean> consecutivos = convertirResultadosABeans(resultados);
            
            // 🔹 4. APLICAR LÓGICA DE NEGOCIO COMPLEJA
            ConsecutivoBean consecutivoSeleccionado = aplicarLogicaDeNegocio(consecutivos, consecutivoRepository);
            
            return consecutivoSeleccionado;
            
        } catch (Exception e) {
            System.err.println("⚠️ Error en ObtenerConsecutivoMarketUseCase: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (em.isOpen()) em.close();
        }
    }
    
    /**
     * 📊 Convertir Object[] a ConsecutivoBean (como el DAO original)
     */
    private ArrayList<ConsecutivoBean> convertirResultadosABeans(List<Object[]> resultados) {
        ArrayList<ConsecutivoBean> consecutivos = new ArrayList<>();
        
        for (Object[] row : resultados) {
            if (row != null && row.length >= 12) { // Asegurar que tenemos todos los campos
                ConsecutivoBean cs = new ConsecutivoBean();
                
                try {
                    // 🔹 Mapeo CORRECTO basado en el debug real de PostgreSQL
                    cs.setId(convertToLong(row[0]));                    // id
                    cs.setConsecutivo_inicial(convertToLong(row[7]));   // consecutivo_inicial
                    cs.setConsecutivo_actual(convertToLong(row[8]));    // consecutivo_actual
                    cs.setConsecutivo_final(convertToLong(row[10]));    // consecutivo_final
                    cs.setEstado((String) row[9]);                      // estado
                    cs.setResolucion(String.valueOf(row[12]));          // resolucion
                    cs.setObservaciones((String) row[11]);              // observaciones
                    cs.setPrefijo((String) row[3]);                     // prefijo
                    cs.setDias(convertToInt(row[14]));                  // dias
                    
                    consecutivos.add(cs);
                    System.out.println("✅ ConsecutivoBean Market creado: ID=" + cs.getId() + ", Prefijo=" + cs.getPrefijo() + ", Estado=" + cs.getEstado());
                } catch (Exception e) {
                    System.err.println("⚠️ Error convirtiendo fila a ConsecutivoBean Market: " + e.getMessage());
                    System.err.println("🔍 Contenido de la fila: " + java.util.Arrays.toString(row));
                    // Imprimir cada campo individualmente para debug
                    for (int i = 0; i < row.length; i++) {
                        System.err.println("  [" + i + "] = " + row[i] + " (" + (row[i] != null ? row[i].getClass().getSimpleName() : "null") + ")");
                    }
                }
            }
        }
        
        return consecutivos;
    }
    
    /**
     * 🔧 Convertir Object a Long de forma segura
     */
    private Long convertToLong(Object value) {
        if (value == null) return 0L;
        if (value instanceof Number) return ((Number) value).longValue();
        if (value instanceof String) return Long.parseLong((String) value);
        throw new IllegalArgumentException("No se puede convertir " + value.getClass() + " a Long");
    }
    
    /**
     * 🔧 Convertir Object a Integer de forma segura
     */
    private Integer convertToInt(Object value) {
        if (value == null) return 0;
        if (value instanceof Number) return ((Number) value).intValue();
        if (value instanceof String) return Integer.parseInt((String) value);
        if (value instanceof Double) return ((Double) value).intValue();
        throw new IllegalArgumentException("No se puede convertir " + value.getClass() + " a Integer");
    }
    
    /**
     * 🧠 LÓGICA DE NEGOCIO COMPLEJA - RÉPLICA EXACTA DEL DAO ORIGINAL
     */
    private ConsecutivoBean aplicarLogicaDeNegocio(ArrayList<ConsecutivoBean> consecutivos, ConsecutivoRepository consecutivoRepository) {
        ConsecutivoBean consAct = null;
        
        if (!consecutivos.isEmpty()) {
            
            // 🔹 ESCENARIO 1: SOLO HAY UN CONSECUTIVO
            if (consecutivos.size() == 1) {
                ConsecutivoBean consecutivo = consecutivos.get(0);
                
                // SI EL CONSECUTIVO ESTA EN USO O ACTIVO
                if (consecutivo.getEstado().equals(NovusConstante.CONSECUTIVO_ESTADO_USO) || 
                    consecutivo.getEstado().equals(NovusConstante.CONSECUTIVO_ESTADO_ACTIVO)) {
                    
                    // SI ES MENOR O IGUAL AL FINAL Y NO VENCIDO LO PUEDE USAR
                    if (consecutivo.getConsecutivo_actual() <= consecutivo.getConsecutivo_final() && 
                        consecutivo.getDias() >= 1) {
                        consAct = consecutivo;
                    } else {
                        // ❌ VENCER CONSECUTIVO
                        actualizaEstadoConsecutivo(consecutivo, NovusConstante.CONSECUTIVO_ESTADO_VENCIDO, consecutivoRepository);
                    }
                }
            } 
            // 🔹 ESCENARIO 2: HAY VARIOS CONSECUTIVOS
            else {
                
                // PASO 1: BUSCAR CONSECUTIVO EN USO
                for (ConsecutivoBean cons : consecutivos) {
                    if (cons.getEstado().equals(NovusConstante.CONSECUTIVO_ESTADO_USO)) {
                        // SI ES MENOR O IGUAL AL FINAL Y NO VENCIDO LO PUEDE USAR
                        if (cons.getConsecutivo_actual() <= cons.getConsecutivo_final() && 
                            cons.getDias() >= 1) {
                            consAct = cons;
                            break;
                        } else {
                            // ❌ VENCER CONSECUTIVO
                            actualizaEstadoConsecutivo(cons, NovusConstante.CONSECUTIVO_ESTADO_VENCIDO, consecutivoRepository);
                        }
                    }
                }
                
                // PASO 2: NO HAY NINGUNO EN USO - BUSCAR DISPONIBLE
                if (consAct == null) {
                    for (ConsecutivoBean cons : consecutivos) {
                        // BUSCAMOS EL CONSECUTIVO DISPONIBLE
                        if (cons.getConsecutivo_actual() <= cons.getConsecutivo_final() && 
                            cons.getDias() >= 1) {
                            consAct = cons;
                            break;
                        } else {
                            // ❌ VENCER CONSECUTIVO
                            actualizaEstadoConsecutivo(cons, NovusConstante.CONSECUTIVO_ESTADO_VENCIDO, consecutivoRepository);
                        }
                    }
                    
                    // ✅ SOLO AVISA UNA VEZ AL SISTEMA QUE CONSECUTIVO ESTA EN USO
                    if (consAct != null) {
                        actualizaEstadoConsecutivo(consAct, NovusConstante.CONSECUTIVO_ESTADO_USO, consecutivoRepository);
                    }
                }
            }
        }
        
        return consAct;
    }
    
    /**
     * 🔄 Actualizar estado de consecutivo (helper method)
     */
    private void actualizaEstadoConsecutivo(ConsecutivoBean cs, String estado, ConsecutivoRepository consecutivoRepository) {
        try {
            boolean actualizado = consecutivoRepository.actualizarEstadoConsecutivo(cs.getId(), estado);
            if (actualizado) {
                System.out.println("✅ Consecutivo Market ID=" + cs.getId() + " actualizado a estado: " + estado);
            } else {
                System.err.println("⚠️ No se pudo actualizar consecutivo Market ID=" + cs.getId() + " a estado: " + estado);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error actualizando estado consecutivo Market: " + e.getMessage());
        }
    }
} 