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
 * 🎯 UseCase: Obtener Consecutivo con Lógica de Negocio Completa
 * 
 * ⚠️ MÉTODO CRÍTICO - Maneja consecutivos de facturación DIAN
 * 
 * LÓGICA DE NEGOCIO:
 * 1. Buscar consecutivos activos (A/U) por tipo y destino
 * 2. Validar rango: consecutivo_actual <= consecutivo_final
 * 3. Validar fecha: dias >= 1 (no vencido)
 * 4. Si 1 consecutivo: usar si válido, vencer si inválido
 * 5. Si múltiples: buscar EN USO (U), luego ACTIVO (A)
 * 6. Marcar como EN USO (U) cuando se activa por primera vez
 * 7. Vencer (V) consecutivos que no cumplen validaciones
 */
public class ObtenerConsecutivoUseCase implements BaseUseCases<ConsecutivoBean> {
    
    private final boolean isFe;
    private final String destino;
    private final EntityManagerFactory entityManagerFactory;
    
    // 🏗️ Constructor con parámetros fijos
    public ObtenerConsecutivoUseCase(boolean isFe, String destino) {
        this.isFe = isFe;
        this.destino = destino;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
    }
    
    /**
     * 🚀 Ejecutar lógica de obtención de consecutivo
     * @return ConsecutivoBean válido o null si no hay disponibles
     */
    @Override
    public ConsecutivoBean execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            ConsecutivoRepository consecutivoRepository = new ConsecutivoRepository(em);
            
            // 🔹 1. DETERMINAR TIPO DE DOCUMENTO
            int tipoDocumento = isFe ? 31 : 9;
            
            // 🔹 2. CONSULTAR CONSECUTIVOS ACTIVOS
            List<Object[]> resultados = consecutivoRepository.obtenerConsecutivo(tipoDocumento, destino);
            
            if (resultados.isEmpty()) {
                System.out.println("ℹ️ No se encontraron consecutivos para tipo=" + tipoDocumento + " destino=" + destino);
                return null;
            }
            
            // 🔹 3. CONVERTIR RESULTADOS A BEANS
            ArrayList<ConsecutivoBean> consecutivos = convertirResultadosABeans(resultados);
            
            // 🔹 4. APLICAR LÓGICA DE NEGOCIO COMPLEJA
            ConsecutivoBean consecutivoSeleccionado = aplicarLogicaDeNegocio(consecutivos, consecutivoRepository);
            
            return consecutivoSeleccionado;
            
        } catch (Exception e) {
            System.err.println("⚠️ Error en ObtenerConsecutivoUseCase: " + e.getMessage());
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
                    // [0] = 1552 (Long) → id
                    // [1] = 364 (Long) → empresas_id  
                    // [2] = 31 (Integer) → tipo_documento
                    // [3] = SET4 (String) → prefijo
                    // [4] = 2025-05-06... (Timestamp) → fecha_inicio
                    // [5] = 2026-07-28... (Timestamp) → fecha_fin
                    // [6] = 1 (Long) → equipos_id
                    // [7] = 3004445 (Long) → consecutivo_inicial
                    // [8] = 5000000 (Long) → consecutivo_actual
                    // [9] = U (String) → estado
                    // [10] = 5052025 (String) → consecutivo_final (como String en DB)
                    // [11] = Autorización... (String) → observaciones
                    // [12] = 527 (Long) → resolucion (como Long en DB)
                    // [13] = {...} (PGobject) → cs_atributos
                    // [14] = 412.04... (Double) → dias
                    
                    cs.setId(convertToLong(row[0]));                    // id = 1552 (Long)
                    cs.setConsecutivo_inicial(convertToLong(row[7]));   // consecutivo_inicial = 3004445 (Long)
                    cs.setConsecutivo_actual(convertToLong(row[8]));    // consecutivo_actual = 5000000 (Long)
                    cs.setConsecutivo_final(convertToLong(row[10]));    // consecutivo_final = 5052025 (String)
                    cs.setEstado((String) row[9]);                      // estado = U (String)
                    cs.setResolucion(String.valueOf(row[12]));          // resolucion = 527 (Long → String)
                    cs.setObservaciones((String) row[11]);              // observaciones = Autorización... (String)
                    cs.setPrefijo((String) row[3]);                     // prefijo = SET4 (String)
                    cs.setDias(convertToInt(row[14]));                  // dias = 412.04... (Double → int)
                    
                    consecutivos.add(cs);
                    System.out.println("✅ ConsecutivoBean creado: ID=" + cs.getId() + ", Prefijo=" + cs.getPrefijo() + ", Estado=" + cs.getEstado());
                } catch (Exception e) {
                    System.err.println("⚠️ Error convirtiendo fila a ConsecutivoBean: " + e.getMessage());
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
                        System.out.println("[LOG] ObtenerConsecutivoUseCase - Usando único consecutivo: " + consAct.getPrefijo() + "-" + consAct.getConsecutivo_actual());
                    } else {
                        System.out.println("[LOG] ObtenerConsecutivoUseCase - Venciendo consecutivo: actual=" + consecutivo.getConsecutivo_actual() + ", final=" + consecutivo.getConsecutivo_final() + ", dias=" + consecutivo.getDias());
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
                            System.out.println("[LOG] ObtenerConsecutivoUseCase - Usando consecutivo en uso: " + consAct.getPrefijo() + "-" + consAct.getConsecutivo_actual());
                            break;
                        } else {
                            System.out.println("[LOG] ObtenerConsecutivoUseCase - Venciendo consecutivo en uso: actual=" + cons.getConsecutivo_actual() + ", final=" + cons.getConsecutivo_final() + ", dias=" + cons.getDias());
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
                System.out.println("✅ Consecutivo ID=" + cs.getId() + " actualizado a estado: " + estado);
            } else {
                System.err.println("⚠️ No se pudo actualizar consecutivo ID=" + cs.getId() + " a estado: " + estado);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error actualizando estado consecutivo: " + e.getMessage());
        }
    }
} 