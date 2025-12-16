package com.application.useCases.movimientocliente;

import com.application.core.BaseUseCases;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.Enums.SqlQueryEnum;
import com.infrastructure.repositories.CtMovimientosClienteRepository;
import com.infrastructure.repositories.TransmisionRepository;
import com.infrastructure.repositories.TransmisionRemisionRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

/**
 * Caso de uso para obtener el ID de transmisión desde un movimiento específico
 * Reemplaza: MovimientosDao.obtenerIdTransmisionDesdeMovimiento(long idMovimiento)
 * 
 * Flujo de búsqueda (igual al método original):
 * 1. ct_movimientos_cliente (base: lazoexpresscore)
 * 2. transmisiones_remision (base: lazoexpresscore) 
 * 3. transmision con búsqueda JSON (base: lazoexpressregistry)
 */
public class ObtenerIdTransmisionDesdeMovimientoUseCase implements BaseUseCases<Long> {

    private Long idMovimiento;

    public ObtenerIdTransmisionDesdeMovimientoUseCase(Long idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    /**
     * Método para actualizar el ID de movimiento y reutilizar la instancia
     */
    public void setIdMovimiento(Long idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    @Override
    public Long execute() {
        System.out.println("🔍 DEBUG obtenerIdTransmisionDesdeMovimiento() JPA:");
        System.out.println("    - Buscando idTransmision para idMovimiento: " + idMovimiento);

        // PASO 1: Buscar en ct_movimientos_cliente (lazoexpresscore)
        Long idTransmision = buscarEnMovimientosCliente();
        if (idTransmision != null && idTransmision > 0) {
            System.out.println("    - ✅ Encontrado en ct_movimientos_cliente: " + idTransmision);
            return idTransmision;
        }

        // PASO 2: Buscar en transmisiones_remision (lazoexpresscore)
        idTransmision = buscarEnTransmisionesRemision();
        if (idTransmision != null && idTransmision > 0) {
            System.out.println("    - ✅ Encontrado en transmisiones_remision: " + idTransmision);
            return idTransmision;
        }

        // PASO 3: Buscar en transmision con JSON (lazoexpressregistry)
        idTransmision = buscarEnTransmisionJson();
        if (idTransmision != null && idTransmision > 0) {
            System.out.println("    - ✅ Encontrado en transmision (JSON): " + idTransmision);
            return idTransmision;
        }

        System.err.println("⚠ No se encontró idTransmision para idMovimiento: " + idMovimiento + " en ninguna tabla");
        return 0L;
    }

    /**
     * PASO 1: Buscar en ct_movimientos_cliente usando Repository
     */
    private Long buscarEnMovimientosCliente() {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        try {
            emf = JpaEntityManagerFactory.INSTANCE.getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
            em = emf.createEntityManager();
            
            CtMovimientosClienteRepository repository = new CtMovimientosClienteRepository(em);
            
            // Usar SqlQueryEnum y el método del repository
            List<?> resultados = repository.findByNativeQuery(
                SqlQueryEnum.BUSCAR_ID_TRANSMISION_EN_CLIENTE.getQuery(), 
                idMovimiento
            );
            
            if (!resultados.isEmpty() && resultados.get(0) != null) {
                Object resultado = resultados.get(0);
                if (resultado instanceof Number) {
                    return ((Number) resultado).longValue();
                }
            }
            return null;
            
        } catch (Exception e) {
            System.err.println("❌ Error en ct_movimientos_cliente: " + e.getMessage());
            return null;
        } finally {
            if (em != null) em.close();
            // NO cerrar emf - es compartido por toda la aplicación
        }
    }

    /**
     * PASO 2: Buscar en transmisiones_remision usando Repository
     */
    private Long buscarEnTransmisionesRemision() {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        try {
            emf = JpaEntityManagerFactory.INSTANCE.getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
            em = emf.createEntityManager();
            
            TransmisionRemisionRepository repository = new TransmisionRemisionRepository(em);
            
            // Usar SqlQueryEnum con repository
            List<?> resultados = repository.findByNativeQuery(
                SqlQueryEnum.BUSCAR_ID_TRANSMISION_EN_REMISION.getQuery(), 
                idMovimiento
            );
            
            if (!resultados.isEmpty() && resultados.get(0) != null) {
                Object resultado = resultados.get(0);
                if (resultado instanceof Number) {
                    return ((Number) resultado).longValue();
                }
            }
            return null;
            
        } catch (Exception e) {
            System.err.println("❌ Error en transmisiones_remision: " + e.getMessage());
            return null;
        } finally {
            if (em != null) em.close();
            // NO cerrar emf - es compartido por toda la aplicación
        }
    }

    /**
     * PASO 3: Buscar en transmision con búsqueda JSON usando Repository
     */
    private Long buscarEnTransmisionJson() {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        try {
            emf = JpaEntityManagerFactory.INSTANCE.getEntityManagerFactory(DatabaseNames.LAZOEXPRESSREGISTRY);
            em = emf.createEntityManager();
            
            TransmisionRepository repository = new TransmisionRepository(em);
            
            // Usar SqlQueryEnum con repository para la búsqueda JSON
            List<?> resultados = repository.findByNativeQuery(
                SqlQueryEnum.BUSCAR_ID_TRANSMISION_EN_JSON.getQuery(), 
                String.valueOf(idMovimiento)
            );
            
            if (!resultados.isEmpty() && resultados.get(0) != null) {
                Object resultado = resultados.get(0);
                if (resultado instanceof Number) {
                    return ((Number) resultado).longValue();
                }
            }
            return null;
            
        } catch (Exception e) {
            System.err.println("❌ Error en transmision: " + e.getMessage());
            return null;
        } finally {
            if (em != null) em.close();
            // NO cerrar emf - es compartido por toda la aplicación
        }
    }
} 