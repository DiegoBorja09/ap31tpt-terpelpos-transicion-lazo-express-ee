package com.application.useCases.entradaCombustible;

import com.application.core.BaseUseCasesWithParams;
import com.bean.entradaCombustible.ProductoSAP;
import com.dao.EntradaCombustibleDao.EntradaCombustibleDao;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.Enums.SqlQueryEnum;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.TreeMap;

/**
 * 🚀 Caso de uso para obtener productos SAP de una remisión
 * Reemplaza: EntradaCombustibleDao.obtenerDetalleRemision(long idRemision)
 * Usado en: RemisionesSAP.consultarRemision() para llenar los productos de la remisión
 */
public class ObtenerProductosSAPUseCase implements BaseUseCasesWithParams<Long, Map<String, ProductoSAP>> {
    
    private final EntityManagerFactory entityManagerFactory;
    
    public ObtenerProductosSAPUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }
    
    /**
     * 🚀 Ejecuta la consulta para obtener productos SAP de una remisión
     * 
     * @param idRemision ID de la remisión SAP
     * @return Map con los productos SAP de la remisión
     * @throws RuntimeException si ocurre error en base de datos
     */
    @Override
    public Map<String, ProductoSAP> execute(Long idRemision) {
        if (idRemision == null || idRemision <= 0) {
            throw new IllegalArgumentException("ID de remisión debe ser válido");
        }
        
        EntityManager em = entityManagerFactory.createEntityManager();
        TreeMap<String, ProductoSAP> productos = new TreeMap<>();
        
        try {
            // Usar el DAO existente que ya tiene la lógica completa
            EntradaCombustibleDao dao = new EntradaCombustibleDao();
            productos = (TreeMap<String, ProductoSAP>) dao.obtenerDetalleRemision(idRemision);
            
            System.out.println("🚀 ObtenerProductosSAPUseCase - Productos obtenidos para remisión " + idRemision + ": " + productos.size() + " productos");
            
            return productos;
            
        } catch (Exception e) {
            System.err.println("❌ Error al obtener productos SAP para remisión " + idRemision + ": " + e.getMessage());
            throw new RuntimeException("Error al obtener productos SAP de la remisión", e);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
} 