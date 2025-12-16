package com.application.useCases.ventas;

import com.application.core.BaseUseCases;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.VentaEnCursoRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Caso de uso para gestionar el estado de venta en curso.
 * Reemplaza: MovimientosDao.VentaEnCurso()
 * Usado en: StoreViewController para controlar ventas activas/inactivas.
 */
public class VentaEnCursoUseCase implements BaseUseCases<Boolean> {

    private final String estado;
    private final String negocio;
    private final EntityManagerFactory entityManagerFactory;

    /**
     * Constructor para el caso de uso VentaEnCurso
     * @param estado "A" para activar venta, "I" para inactivar venta
     * @param negocio Tipo de negocio (ej: "CAN" para canastilla)
     */
    public VentaEnCursoUseCase(String estado, String negocio) {
        this.estado = estado;
        this.negocio = negocio;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Boolean execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            VentaEnCursoRepository repository = new VentaEnCursoRepository(em);
            
            // Lógica condicional basada en el estado
            if ("A".equals(estado)) {
                // 🟢 ACTIVAR VENTA: Insertar registro
                return repository.activarVentaEnCurso(negocio);
            } else {
                // 🔴 DESACTIVAR VENTA: Eliminar registros  
                return repository.desactivarVentaEnCurso(negocio);
            }
            
        } catch (Exception e) {
            System.err.println("⚠️ Error en VentaEnCursoUseCase: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (em.isOpen()) em.close();
        }
    }
} 