package com.infrastructure.cache;

/**
 * Interceptor LIVIANO para detectar actualizaciones de productos
 * y disparar invalidación automática de cache
 * 
 * OPTIMIZADO: Sin dependencias de Spring
 * LIVIANO: Mínimo consumo de memoria
 * SEGURO: Los errores no afectan el flujo principal
 * 
 * @author Equipo Desarrollo Terpel
 * @version 3.0 - Versión Liviana Sin Spring
 */
public class ProductoUpdateInterceptorLiviano {

    private final KioscoCacheServiceLiviano cacheService;
    
    public ProductoUpdateInterceptorLiviano() {
        try {
            this.cacheService = KioscoCacheServiceLiviano.getInstance();
            // Mostrar contenido inicial del cache
            cacheService.mostrarContenidoCache();
        } catch (Exception e) {
            System.err.println(" No se pudo inicializar cache liviano: " + e.getMessage());
            throw new RuntimeException("Cache service requerido", e);
        }
    }
    
    /**
     * Notificar actualización de producto
     */
    public void onProductoActualizado(Long productoId) {
        try {
            if (productoId != null && cacheService != null) {
                System.out.println(" Interceptor Liviano: Producto actualizado ID " + productoId);
                cacheService.invalidarCachePorProductoActualizado(productoId);
            }
        } catch (Exception e) {
            // No debe fallar el flujo principal
            System.err.println(" Error en interceptor liviano (NO CRÍTICO): " + e.getMessage());
        }
    }
    
    /**
     * Notificar nuevo producto
     */
    public void onProductoCreado(Long productoId) {
        onProductoActualizado(productoId); // Misma lógica
    }
    
    /**
     * Notificar producto eliminado
     */
    public void onProductoEliminado(Long productoId) {
        try {
            if (productoId != null && cacheService != null) {
                System.out.println(" Interceptor Liviano: Producto eliminado ID " + productoId);
                cacheService.invalidarTodosLosCaches(); // Invalidar todo por seguridad
            }
        } catch (Exception e) {
            System.err.println(" Error en interceptor liviano (NO CRÍTICO): " + e.getMessage());
        }
    }
    
    /**
     * Invalidación masiva (para sincronizaciones)
     */
    public void onProductosMasivosActualizados() {
        try {
            if (cacheService != null) {
                System.out.println(" Interceptor Liviano: Invalidación masiva");
                cacheService.actualizacionDiariaCache();
            }
        } catch (Exception e) {
            System.err.println(" Error en invalidación masiva liviana: " + e.getMessage());
        }
    }
    
    /**
     * Notificar actualización de categoría
     */
    public void onCategoriaActualizada() {
        try {
            if (cacheService != null) {
                System.out.println(" Interceptor Liviano: Categoría actualizada");
                cacheService.invalidarCachesTipo("categorias");
            }
        } catch (Exception e) {
            System.err.println(" Error invalidando cache categorías: " + e.getMessage());
        }
    }
    
    /**
     * Actualizar producto específico en cache con datos de BD
     */
    public void onProductoEspecificoActualizado(Long productoId, com.bean.MovimientosDetallesBean producto) {
        try {
            if (productoId != null && producto != null && cacheService != null) {
                System.out.println(" Interceptor Liviano: Actualizando producto específico ID " + productoId);
                
                // Actualizar en cache por PLU si está disponible
                if (producto.getPlu() != null && !producto.getPlu().isEmpty()) {
                    cacheService.actualizarCacheProducto("busqueda_plu_" + producto.getPlu(), producto, "PLU");
                }
                
                // Actualizar en cache por código de barras si está disponible
                if (producto.getCodigoBarra() != null && !producto.getCodigoBarra().isEmpty()) {
                    cacheService.actualizarCacheProducto("busqueda_codigo_" + producto.getCodigoBarra(), producto, "CODIGO");
                }
                
                // Invalidar búsquedas generales para que se refresquen
                cacheService.invalidarCachesTipo("busqueda_productos");
                
                System.out.println("Producto " + producto.getDescripcion() + " actualizado en cache");
            }
        } catch (Exception e) {
            System.err.println(" Error actualizando producto específico en cache: " + e.getMessage());
        }
    }
    
    /**
     * Verificar si está activo
     */
    public boolean isActive() {
        return cacheService != null && cacheService.isCacheHealthy();
    }
}
