package com.infrastructure.cache;

import com.application.useCases.categorias.FindAllCategoriasKIOSCOUseCase;
import com.application.useCases.movimientos.FindAllProductoTipoKioskoUseCase;
import com.application.useCases.productos.BuscarProductosTipoKioskoUseCase;
import com.application.useCases.productos.BuscarProductoPorPluKioskoUseCase;
import com.bean.CategoriaBean;
import com.bean.MovimientosDetallesBean;
import com.dao.MovimientosDao;
import com.dao.DAOException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Servicio de cache LIVIANO para KIOSCO - SIN SPRING
 * 
 *  OPTIMIZADO: Sin dependencias de Spring
 *  LIVIANO: Mínimo consumo de memoria
 *  AUTOMÁTICO: Actualización diaria y por cambios de productos
 * 
 * @author Equipo Desarrollo
 * @version 3.0 - Versión Liviana Sin Spring
 */
public class KioscoCacheServiceLiviano {
    
    // Singleton Instance
    private static volatile KioscoCacheServiceLiviano INSTANCE;
    private static final Object lock = new Object();
    
    // Cache Storage (ConcurrentHashMap es thread-safe)
    private final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> timestamps = new ConcurrentHashMap<>();
    
    // TTL en milisegundos
    private static final long TTL_CATEGORIAS = 30 * 60 * 1000L;      // 30 minutos
    private static final long TTL_PRODUCTOS_POPULARES = 10 * 60 * 1000L; // 10 minutos  
    private static final long TTL_CONFIGURACIONES = 60 * 60 * 1000L;     // 1 hora
    private static final long TTL_BUSQUEDAS = 5 * 60 * 1000L;           // 5 minutos
    
    // Control de actualizaciones
    private final AtomicBoolean actualizacionEnProgreso = new AtomicBoolean(false);
    private java.time.LocalDateTime ultimaActualizacion = java.time.LocalDateTime.now();
    
    // Métricas para monitoreo de hit ratio
    private long totalHits = 0;
    private long totalMisses = 0;
    private long totalRequests = 0;
    
    // Control de logs automáticos (mostrar métricas cada 50 consultas)
    private static final long LOG_INTERVAL = 50;
    private long ultimoLogRequest = 0;
    
    // Scheduler para actualización automática
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    // Nota: Se eliminó DatabaseChangeDetector por incompatibilidad con arquitectura sin triggers
    
    // Use Cases (lazy loading)
    private FindAllCategoriasKIOSCOUseCase categoriaUseCase;
    private FindAllProductoTipoKioskoUseCase productoUseCase;
    
    // Constructor privado (Singleton)
    private KioscoCacheServiceLiviano() {
        inicializarScheduler();
        System.out.println("KioscoCacheServiceLiviano inicializado (SIN SPRING - Solo interceptores Java)");
        System.out.println(" Cache liviano completamente operativo");
    }
    
    /**
     * NOTA: Se eliminó inicializarDetectorBDSiNoExiste() por incompatibilidad con arquitectura sin triggers
     * El cache ahora funciona solo con interceptores Java para máxima simplicidad y compatibilidad
     */
    
    // Singleton getInstance
    public static KioscoCacheServiceLiviano getInstance() {
        if (INSTANCE == null) {
            synchronized (lock) {
                if (INSTANCE == null) {
                    INSTANCE = new KioscoCacheServiceLiviano();
                }
            }
        }
        return INSTANCE;
    }
    
    /**
     * Inicializar scheduler para actualización diaria automática
     */
    private void inicializarScheduler() {
        // Calcular tiempo hasta las 3:00 AM del siguiente día
        long initialDelay = calcularTiempoHasta3AM();
        
        // Programar actualización diaria
        scheduler.scheduleAtFixedRate(
            this::actualizacionDiariaCache,
            initialDelay,
            24 * 60 * 60, // 24 horas
            TimeUnit.SECONDS
        );
        
        System.out.println(" Scheduler configurado - Próxima actualización: 3:00 AM");
    }
    

    /**
     * Calcular segundos hasta las 3:00 AM
     */
    private long calcularTiempoHasta3AM() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.LocalDateTime proximas3AM = now.withHour(3).withMinute(0).withSecond(0).withNano(0);
        
        if (now.isAfter(proximas3AM)) {
            proximas3AM = proximas3AM.plusDays(1);
        }
        
        return java.time.Duration.between(now, proximas3AM).getSeconds();
    }
    
    // =====================================
    // MÉTODOS DE CACHE
    // =====================================
    
    /**
     * Obtener categorías con cache
     */
    @SuppressWarnings("unchecked")
    public List<CategoriaBean> obtenerCategoriasConCache() {
        String key = "categorias";
        
        // Verificar si está en cache y no expirado
        Object cached = getCacheIfValid(key, TTL_CATEGORIAS);
        if (cached != null) {
            List<CategoriaBean> categorias = (List<CategoriaBean>) cached;
            System.out.println(" CACHE HIT - Categorías - " + categorias.size() + " categorías");
            return categorias;
        }
        
        // Cache MISS - consultar BD
        System.out.println(" BD QUERY - Categorías - Consultando Base de Datos");
        try {
            // USAR DAO DIRECTO para evitar recursión infinita
            MovimientosDao mdao = new MovimientosDao();
            TreeMap<Long, CategoriaBean> categoriasMap = mdao.getInventario();
            
            // Convertir TreeMap a List para mantener compatibilidad
            List<CategoriaBean> result = new ArrayList<>(categoriasMap.values());
            
            // Almacenar en cache
            if (result != null && !result.isEmpty()) {
                cache.put(key, result);
                timestamps.put(key, System.currentTimeMillis());
                System.out.println("CACHE STORE - Categorías - " + result.size() + " categorías guardadas en cache");
            } else {
                System.out.println(" Sin categorías desde BD - No se almacena en cache");
            }
            
            return result != null ? result : new java.util.ArrayList<>();
            
        } catch (com.dao.DAOException e) {
            System.err.println("Error BD consultando categorías: " + e.getMessage());
            return new java.util.ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error general consultando categorías: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }
    
    /**
     * Obtener productos populares con cache
     */
    @SuppressWarnings("unchecked")
    public List<MovimientosDetallesBean> obtenerProductosPopularesConCache(int limit) {
        String key = "productos_populares_" + limit;
        
        Object cached = getCacheIfValid(key, TTL_PRODUCTOS_POPULARES);
        if (cached != null) {
            List<MovimientosDetallesBean> productos = (List<MovimientosDetallesBean>) cached;
            
            //  DEBUG: Verificar si products tienen estado válido (fix temporal)
            boolean tieneEstadoValido = productos.stream().anyMatch(p -> p.getEstado() != null);
            if (!tieneEstadoValido) {
                System.out.println(" CACHE INVALIDADO - Productos sin estado, recargando...");
                cache.remove(key);
                timestamps.remove(key);
            } else {
                System.out.println(" CACHE HIT - Productos populares - " + productos.size() + " productos");
                return productos;
            }
        }
        
        System.out.println(" BD QUERY - Productos populares - Consultando Base de Datos");
        try {
            // USAR UseCase que incluye campo 'estado' correctamente
            com.application.useCases.movimientos.FindAllProductoTipoKioskoUseCase useCase = 
                new com.application.useCases.movimientos.FindAllProductoTipoKioskoUseCase();
            
            // Llamar directamente al fallback del UseCase para evitar recursión
            List<MovimientosDetallesBean> todosProductos = useCase.execute();
            
            // Filtrar productos ACTIVOS y con saldo
            List<MovimientosDetallesBean> productosPopulares = todosProductos.stream()
                    .filter(p -> p.getSaldo() > 0)
                    .filter(p -> "A".equals(p.getEstado())) //  FILTRO: Solo productos ACTIVOS
                    .sorted((p1, p2) -> Float.compare(p2.getSaldo(), p1.getSaldo()))
                    .limit(limit)
                    .collect(Collectors.toList());
            
            // Almacenar en cache
            cache.put(key, productosPopulares);
            timestamps.put(key, System.currentTimeMillis());
            System.out.println("CACHE STORE - Productos populares - " + productosPopulares.size() + " productos guardados en cache");
            
            return productosPopulares;
            
        } catch (Exception e) {
            System.err.println("Error consultando productos populares desde BD: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }
    
    /**
     * Buscar productos con cache
     */
    @SuppressWarnings("unchecked")
    public List<MovimientosDetallesBean> buscarProductosConCache(String busqueda) {
        String key = "busqueda_" + busqueda.toLowerCase();
        
        Object cached = getCacheIfValid(key, TTL_BUSQUEDAS);
        if (cached != null) {
            totalHits++;
            totalRequests++;
            mostrarMetricasAutomaticas();
            List<MovimientosDetallesBean> resultados = (List<MovimientosDetallesBean>) cached;
            System.out.println(" CACHE HIT - Búsqueda: '" + busqueda + "' - " + resultados.size() + " productos");
            return resultados;
        }
        
        totalMisses++;
        totalRequests++;
        mostrarMetricasAutomaticas();
        System.out.println(" BD QUERY - Búsqueda: '" + busqueda + "' - Consultando Base de Datos");
        try {
            BuscarProductosTipoKioskoUseCase buscarUseCase = new BuscarProductosTipoKioskoUseCase(busqueda);
            List<MovimientosDetallesBean> resultados = buscarUseCase.execute();
            
            // Almacenar en cache
            if (resultados != null && !resultados.isEmpty()) {
                cache.put(key, resultados);
                timestamps.put(key, System.currentTimeMillis());
                System.out.println("CACHE STORE - Búsqueda: '" + busqueda + "' - " + resultados.size() + " productos guardados en cache");
            } else {
                System.out.println(" Sin resultados para búsqueda '" + busqueda + "' - No se almacena en cache");
            }
            
            return resultados != null ? resultados : new java.util.ArrayList<>();
            
        } catch (Exception e) {
            System.err.println("Error en búsqueda: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }
    
    /**
     * Buscar producto por PLU con cache OPTIMIZADO
     * PRIMERO busca en cache de productos, si no encuentra va a BD
     */
    public MovimientosDetallesBean buscarProductoPorPluConCache(String plu) {
        //  OPTIMIZACIÓN: Buscar PRIMERO en productos cacheados
        try {
            List<MovimientosDetallesBean> productosEnCache = obtenerProductosPopularesConCache(100);
            if (productosEnCache != null && !productosEnCache.isEmpty()) {
                for (MovimientosDetallesBean producto : productosEnCache) {
                    if (plu.equals(producto.getPlu())) {
                        totalHits++;
                        totalRequests++;
                        mostrarMetricasAutomaticas();
                        System.out.println(" CACHE HIT - PLU: " + plu + " - " + producto.getDescripcion());
                        return producto;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(" Error buscando en productos cacheados: " + e.getMessage());
        }
        
        // Si no está en productos populares, buscar en cache específico PLU
        String key = "plu_" + plu;
        Object cached = getCacheIfValid(key, TTL_BUSQUEDAS);
        if (cached != null) {
            totalHits++;
            totalRequests++;
            mostrarMetricasAutomaticas();
            MovimientosDetallesBean producto = (MovimientosDetallesBean) cached;
            
            //  AUTO-VERIFICACIÓN: Verificar ocasionalmente si datos cambiaron
            verificarYActualizarSiEsNecesario(plu, producto);
            
            System.out.println(" CACHE HIT - PLU: " + plu + " - " + producto.getDescripcion());
            return producto;
        }
        
        // Solo ir a BD si NO está en ningún cache
        totalMisses++;
        totalRequests++;
        mostrarMetricasAutomaticas();
        System.out.println(" BD QUERY - PLU: " + plu + " - Consultando Base de Datos directamente");
        try {
            //  FIX: Usar DAO original para mantener misma lógica (evita recursión porque no usa cache)
            MovimientosDao dao = new MovimientosDao();
            MovimientosDetallesBean resultado = dao.findByPluKIOSCO(plu);
            
            if (resultado != null) {
                cache.put(key, resultado);
                timestamps.put(key, System.currentTimeMillis());
                System.out.println("CACHE STORE - PLU: " + plu + " - " + resultado.getDescripcion() + " guardado en cache");
            } else {
                System.out.println(" Producto PLU: " + plu + " NO ENCONTRADO en BASE DE DATOS");
            }
            
            return resultado;
            
        } catch (DAOException | Exception e) {
            System.err.println("Error buscando PLU: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * AUTO-VERIFICACIÓN: Detectar si un producto en cache necesita actualizarse
     * Se ejecuta ocasionalmente (cada 10 consultas) para verificar cambios de precio
     */
    private void verificarYActualizarSiEsNecesario(String plu, MovimientosDetallesBean productoCacheado) {
        String key = "plu_" + plu;
        String verifyKey = key + "_verify_count";
        
        // Contador de verificaciones
        Integer count = (Integer) cache.get(verifyKey);
        if (count == null) count = 0;
        count++;
        cache.put(verifyKey, count);
        
        // Verificar cada 10 consultas para no sobrecargar BD
        if (count % 10 == 0) {
            try {
                MovimientosDao dao = new MovimientosDao();
                MovimientosDetallesBean productoBD = dao.findByPluKIOSCO(plu);
                
                if (productoBD != null && hasCambiadoProducto(productoCacheado, productoBD)) {
                    System.out.println(" AUTO-UPDATE DETECTADO - PLU: " + plu);
                    System.out.println(" Precio: $" + productoCacheado.getPrecio() + " → $" + productoBD.getPrecio());
                    System.out.println(" Saldo: " + productoCacheado.getSaldo() + " → " + productoBD.getSaldo());
                    
                    // Actualizar cache con datos frescos
                    cache.put(key, productoBD);
                    timestamps.put(key, System.currentTimeMillis());
                    
                    // También actualizar en productos populares si está ahí
                    actualizarEnProductosPopulares(plu, productoBD);
                }
            } catch (DAOException | Exception e) {
                System.err.println(" Error en auto-verificación para PLU " + plu + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Detectar si los datos críticos del producto han cambiado
     */
    private boolean hasCambiadoProducto(MovimientosDetallesBean cacheProduct, MovimientosDetallesBean bdProduct) {
        if (cacheProduct == null || bdProduct == null) return true;
        
        // Comparar campos críticos que afectan al usuario
        boolean precioCambio = !Objects.equals(cacheProduct.getPrecio(), bdProduct.getPrecio());
        boolean saldoCambio = Math.abs(cacheProduct.getSaldo() - bdProduct.getSaldo()) > 0.01f; // Tolerancia decimal
        boolean estadoCambio = !Objects.equals(cacheProduct.getEstado(), bdProduct.getEstado());
        
        return precioCambio || saldoCambio || estadoCambio;
    }
    
    /**
     * Eliminar producto completamente del cache (individual y listas populares)
     */
    private void eliminarProductoDelCache(Long productoId) {
        try {
            // Primero obtener PLU del producto para eliminarlo
            com.dao.SetupDao setupDao = new com.dao.SetupDao();
            MovimientosDetallesBean producto = setupDao.findProductoByIdParaCache(productoId);
            
            if (producto != null) {
                String plu = producto.getPlu();
                String codigoBarra = producto.getCodigoBarra();
                
                // PASO 1: Eliminar de cache individual por PLU
                String keyPlu = "plu_" + plu;
                if (cache.containsKey(keyPlu)) {
                    cache.remove(keyPlu);
                    timestamps.remove(keyPlu);
                    System.out.println(" Eliminado cache individual PLU: " + plu);
                }
                
                // PASO 2: Eliminar de cache individual por código de barras
                String keyBarra = "codigo_" + codigoBarra;
                if (cache.containsKey(keyBarra)) {
                    cache.remove(keyBarra);
                    timestamps.remove(keyBarra);
                    System.out.println(" Eliminado cache individual Código: " + codigoBarra);
                }
                
                // PASO 3: Eliminar de listas de productos populares
                eliminarDeProductosPopulares(plu);
            }
            
        } catch (Exception e) {
            System.err.println("Error eliminando producto ID " + productoId + " del cache: " + e.getMessage());
        }
    }
    
    /**
     * Eliminar producto de las listas de productos populares
     */
    private void eliminarDeProductosPopulares(String plu) {
        try {
            for (String key : cache.keySet()) {
                if (key.startsWith("productos_populares_")) {
                    Object cached = cache.get(key);
                    if (cached instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<MovimientosDetallesBean> productos = (List<MovimientosDetallesBean>) cached;
                        
                        // Buscar y eliminar el producto de la lista
                        productos.removeIf(producto -> plu.equals(producto.getPlu()));
                        cache.put(key, productos); // Actualizar cache
                        System.out.println(" Producto eliminado de lista popular: " + key);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(" Error eliminando de productos populares: " + e.getMessage());
        }
    }
    
    /**
     * Actualizar producto directamente en cache (sin validaciones adicionales)
     */
    private void actualizarProductoEnCacheDirecto(MovimientosDetallesBean producto) {
        try {
            String plu = producto.getPlu();
            String codigoBarra = producto.getCodigoBarra();
            
            // Actualizar cache individual por PLU
            String keyPlu = "plu_" + plu;
            cache.put(keyPlu, producto);
            timestamps.put(keyPlu, System.currentTimeMillis());
            
            // Actualizar cache individual por código de barras  
            String keyBarra = "codigo_" + codigoBarra;
            cache.put(keyBarra, producto);
            timestamps.put(keyBarra, System.currentTimeMillis());
            
            // Actualizar en listas de productos populares
            actualizarEnProductosPopulares(plu, producto);
            
            System.out.println(" Producto actualizado en cache: " + producto.getDescripcion());
            
        } catch (Exception e) {
            System.err.println("Error actualizando producto en cache: " + e.getMessage());
        }
    }
    
    /**
     * Actualizar producto en la lista de productos populares si está presente
     */
    private void actualizarEnProductosPopulares(String plu, MovimientosDetallesBean productoActualizado) {
        try {
            // Buscar en todas las listas de productos populares cacheadas
            for (String key : cache.keySet()) {
                if (key.startsWith("productos_populares_")) {
                    Object cached = cache.get(key);
                    if (cached instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<MovimientosDetallesBean> productos = (List<MovimientosDetallesBean>) cached;
                        
                        // Buscar y actualizar el producto en la lista
                        for (int i = 0; i < productos.size(); i++) {
                            MovimientosDetallesBean producto = productos.get(i);
                            if (plu.equals(producto.getPlu())) {
                                productos.set(i, productoActualizado);
                                cache.put(key, productos); // Actualizar cache
                                System.out.println(" Producto actualizado en lista popular: " + key);
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(" Error actualizando productos populares: " + e.getMessage());
        }
    }
    
    /**
     * Buscar producto por CÓDIGO DE BARRAS con cache OPTIMIZADO
     * PRIMERO busca en cache de productos, si no encuentra va a BD
     */
    public MovimientosDetallesBean buscarProductoPorCodigoBarraConCache(String codigoBarra) {
        //  OPTIMIZACIÓN: Buscar PRIMERO en productos cacheados
        try {
            List<MovimientosDetallesBean> productosEnCache = obtenerProductosPopularesConCache(100);
            if (productosEnCache != null && !productosEnCache.isEmpty()) {
                for (MovimientosDetallesBean producto : productosEnCache) {
                    if (codigoBarra.equals(producto.getCodigoBarra())) {
                        totalHits++;
                        totalRequests++;
                        mostrarMetricasAutomaticas();
                        
                        //  AUTO-VERIFICACIÓN: Verificar ocasionalmente si datos cambiaron
                        verificarYActualizarSiEsNecesario(producto.getPlu(), producto);
                        
                        System.out.println(" CACHE HIT - Código: " + codigoBarra + " - " + producto.getDescripcion());
                        return producto;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(" Error buscando en productos cacheados: " + e.getMessage());
        }
        
        // Si no está en productos populares, buscar en cache específico
        String key = "codigo_" + codigoBarra;
        Object cached = getCacheIfValid(key, TTL_BUSQUEDAS);
        if (cached != null) {
            totalHits++;
            totalRequests++;
            mostrarMetricasAutomaticas();
            MovimientosDetallesBean producto = (MovimientosDetallesBean) cached;
            
            //  AUTO-VERIFICACIÓN: Verificar ocasionalmente si datos cambiaron
            verificarYActualizarSiEsNecesario(producto.getPlu(), producto);
            
            System.out.println(" CACHE HIT - Código: " + codigoBarra + " - " + producto.getDescripcion());
            return producto;
        }
        
        // Solo ir a BD si NO está en ningún cache
        totalMisses++;
        totalRequests++;
        mostrarMetricasAutomaticas();
        System.out.println(" BD QUERY - Código: " + codigoBarra + " - Consultando Base de Datos directamente");
        try {
            MovimientosDao dao = new MovimientosDao();
            MovimientosDetallesBean resultado = dao.findByBarCodeKIOSCO(codigoBarra);
            
            if (resultado != null) {
                cache.put(key, resultado);
                timestamps.put(key, System.currentTimeMillis());
                System.out.println("CACHE STORE - Código: " + codigoBarra + " - " + resultado.getDescripcion() + " guardado en cache");
            } else {
                System.out.println(" Producto Código: " + codigoBarra + " NO ENCONTRADO en BASE DE DATOS");
            }
            
            return resultado;
            
        } catch (DAOException | Exception e) {
            System.err.println("Error buscando código de barras: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Actualizar cache con producto encontrado (para que Use Cases puedan guardar en cache)
     */
    public void actualizarCacheProducto(String identificador, MovimientosDetallesBean producto, String tipo) {
        if (producto == null || identificador == null) return;
        
        try {
            String key = tipo + "_" + identificador;
            cache.put(key, producto);
            timestamps.put(key, System.currentTimeMillis());
            System.out.println(" Cache STORE - " + tipo + ": " + identificador);
        } catch (Exception e) {
            System.err.println(" Error guardando en cache: " + e.getMessage());
        }
    }
    
    /**
     * Obtener configuración con cache
     */
    public String obtenerConfiguracionConCache(String configKey) {
        String key = "config_" + configKey;
        
        Object cached = getCacheIfValid(key, TTL_CONFIGURACIONES);
        if (cached != null) {
            System.out.println("Cache HIT - Config: " + configKey);
            return (String) cached;
        }
        
        System.out.println("Cache MISS - Config: " + configKey);
        String configValue = getDefaultConfigValue(configKey);
        
        if (configValue != null) {
            cache.put(key, configValue);
            timestamps.put(key, System.currentTimeMillis());
            System.out.println(" Cache STORE - Config: " + configKey);
        }
        
        return configValue;
    }
    
    // =====================================
    // MÉTODOS DE INVALIDACIÓN
    // =====================================
    
    /**
     * Invalidar cache por producto actualizado
     */
    public void invalidarCachePorProductoActualizado(Long productoId) {
        try {
            System.out.println("🔔 Producto actualizado - ID: " + productoId);
            
            // Invalidar caches relacionados con productos
            invalidarCachesTipo("productos_populares");
            invalidarCachesTipo("busqueda");
            
            System.out.println(" Cache invalidado por producto " + productoId);
            
        } catch (Exception e) {
            System.err.println("Error invalidando cache: " + e.getMessage());
        }
    }
    
    /**
     * Invalidar todos los caches
     */
    public void invalidarTodosLosCaches() {
        cache.clear();
        timestamps.clear();
        System.out.println(" Todos los caches invalidados");
    }
    
    /**
     * Actualización diaria automática
     */
    public void actualizacionDiariaCache() {
        if (actualizacionEnProgreso.compareAndSet(false, true)) {
            try {
                System.out.println(" === ACTUALIZACIÓN DIARIA CACHE LIVIANO ===");
                
                // Invalidar todos los caches
                invalidarTodosLosCaches();
                
                // Pre-cargar datos críticos
                obtenerCategoriasConCache();
                obtenerProductosPopularesConCache(1000); // ⬆️ Aumentado de 20 a 1000 productos
                obtenerConfiguracionConCache("TIMEOUT_VENTA");
                
                ultimaActualizacion = java.time.LocalDateTime.now();
                System.out.println(" Actualización diaria completada");
                
            } catch (Exception e) {
                System.err.println("Error en actualización diaria: " + e.getMessage());
            } finally {
                actualizacionEnProgreso.set(false);
            }
        }
    }
    
    // =====================================
    // MÉTODOS AUXILIARES
    // =====================================
    
    // MÉTODO ELIMINADO - Duplicado, se mantiene la versión con logs detallados más abajo
    
    /**
     * Invalidar caches por tipo (público para uso del interceptor)
     */
    public void invalidarCachesTipo(String tipo) {
        cache.entrySet().removeIf(entry -> entry.getKey().startsWith(tipo));
        timestamps.entrySet().removeIf(entry -> entry.getKey().startsWith(tipo));
        System.out.println(" Invalidados caches tipo: " + tipo);
    }
    
    /**
     * Configuraciones por defecto
     */
    private String getDefaultConfigValue(String configKey) {
        switch (configKey) {
            case "TIMEOUT_VENTA": return "300";
            case "MAX_PRODUCTOS_CATEGORIA": return "50";
            case "ENABLE_AUDIO_FEEDBACK": return "true";
            case "CACHE_TTL_PRODUCTOS": return "600";
            case "MONEDA_DEFAULT": return "COP";
            case "IDIOMA_DEFAULT": return "es";
            default: return "default_" + configKey;
        }
    }
    
    /**
     * Obtener estadísticas del cache incluyendo hit ratio
     */
    public java.util.Map<String, Object> obtenerEstadisticas() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        
        // Estadísticas básicas
        stats.put("total_entries", cache.size());
        stats.put("ultima_actualizacion", ultimaActualizacion.toString());
        stats.put("actualizacion_en_progreso", actualizacionEnProgreso.get());
        
        // Métricas de rendimiento
        stats.put("total_requests", totalRequests);
        stats.put("total_hits", totalHits);
        stats.put("total_misses", totalMisses);
        
        // Hit ratio (porcentaje)
        double hitRatio = totalRequests > 0 ? (double) totalHits / totalRequests * 100 : 0;
        stats.put("hit_ratio_percent", String.format("%.2f%%", hitRatio));
        
        // Información del sistema
        stats.put("version", "3.0-Liviano-Optimizado");
        stats.put("tipo", "Sin Spring - Monitoreo Incluido");
        stats.put("ttl_categorias_min", TTL_CATEGORIAS / 60000);
        stats.put("ttl_productos_min", TTL_PRODUCTOS_POPULARES / 60000);
        stats.put("ttl_configuraciones_min", TTL_CONFIGURACIONES / 60000);
        
        return stats;
    }
    
    /**
     * Obtener métricas específicas de hit ratio
     */
    public java.util.Map<String, Object> obtenerMetricasHitRatio() {
        java.util.Map<String, Object> metricas = new java.util.HashMap<>();
        
        metricas.put("total_requests", totalRequests);
        metricas.put("hits", totalHits);
        metricas.put("misses", totalMisses);
        
        if (totalRequests > 0) {
            double hitRatio = (double) totalHits / totalRequests;
            double missRatio = (double) totalMisses / totalRequests;
            
            metricas.put("hit_ratio", hitRatio);
            metricas.put("miss_ratio", missRatio);
            metricas.put("hit_ratio_percent", String.format("%.2f%%", hitRatio * 100));
            metricas.put("miss_ratio_percent", String.format("%.2f%%", missRatio * 100));
            
            // Evaluación de eficiencia
            if (hitRatio >= 0.8) {
                metricas.put("evaluacion", "EXCELENTE");
                metricas.put("color", "VERDE");
            } else if (hitRatio >= 0.6) {
                metricas.put("evaluacion", "BUENO");
                metricas.put("color", "AMARILLO");
            } else {
                metricas.put("evaluacion", "NECESITA_OPTIMIZACION");
                metricas.put("color", "ROJO");
            }
        } else {
            metricas.put("evaluacion", "SIN_DATOS");
            metricas.put("color", "GRIS");
        }
        
        return metricas;
    }
    
    /**
     * Reiniciar métricas de hit ratio
     */
    public void reiniciarMetricas() {
        totalHits = 0;
        totalMisses = 0;
        totalRequests = 0;
        System.out.println("Métricas de hit ratio reiniciadas");
    }
    
    /**
     * Verificar salud del cache
     */
    public boolean isCacheHealthy() {
        return cache != null && !actualizacionEnProgreso.get();
    }
    
    /**
     * Shutdown del scheduler y detector (para cleanup)
     */
    public void shutdown() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            System.out.println(" Scheduler del cache detenido");
        }
        
        // Nota: DatabaseChangeDetector fue eliminado por incompatibilidad con arquitectura
    }
    
    /**
     * Verificar si un objeto está en cache y no ha expirado (con logs detallados)
     */
    private Object getCacheIfValid(String key, long ttl) {
        totalRequests++;
        
        if (!cache.containsKey(key)) {
            totalMisses++;
            System.out.println(" Cache key '" + key + "' NO EXISTE en cache");
            return null;
        }
        
        // Verificar timestamp
        Long timestamp = timestamps.get(key);
        if (timestamp == null) {
            cache.remove(key);
            totalMisses++;
            System.out.println(" Cache key '" + key + "' SIN TIMESTAMP - Eliminado");
            return null;
        }
        
        // Verificar TTL
        long edad = System.currentTimeMillis() - timestamp;
        if (edad > ttl) {
            cache.remove(key);
            timestamps.remove(key);
            totalMisses++;
            System.out.println("     Cache key '" + key + "' EXPIRADO (edad: " + (edad/60000) + " min, TTL: " + (ttl/60000) + " min) - Eliminado");
            return null;
        }
        
        // Cache válido
        totalHits++;
        return cache.get(key);
    }
    
    /**
     * Mostrar métricas de consultas BD vs Cache en tiempo real
     */
    public void mostrarMetricasConsultas() {
        double hitRatio = totalRequests > 0 ? ((double) totalHits / totalRequests) * 100 : 0;
        
        System.out.println("\n========== MÉTRICAS CONSULTAS BD vs CACHE ==========");
        System.out.println("Total consultas: " + totalRequests);
        System.out.println(" Consultas desde CACHE: " + totalHits + " (" + String.format("%.1f%%", hitRatio) + ")");
        System.out.println("Consultas a BASE DE DATOS: " + totalMisses + " (" + String.format("%.1f%%", (100 - hitRatio)) + ")");
        System.out.println("Eficiencia del cache: " + String.format("%.2f%%", hitRatio));
        
        if (hitRatio >= 80) {
            System.out.println("Cache funcionando EXCELENTE (≥80% hit ratio)");
        } else if (hitRatio >= 60) {
            System.out.println("Cache funcionando BIEN (60-79% hit ratio)");
        } else {
            System.out.println("Cache necesita optimización (<60% hit ratio)");
        }
        System.out.println("====================================================\n");
    }
    
    /**
     * Mostrar métricas automáticamente cada ciertas consultas
     */
    private void mostrarMetricasAutomaticas() {
        if (totalRequests > 0 && totalRequests % LOG_INTERVAL == 0 && totalRequests != ultimoLogRequest) {
            ultimoLogRequest = totalRequests;
            mostrarMetricasConsultas();
            // Mostrar también el contenido del cache cada 50 consultas
            mostrarContenidoCache();
        }
    }
    
    /**
     * Mostrar estadísticas detalladas del cache con logs
     */
    public void mostrarLogsCacheDetallados() {
        System.out.println("\n========== ESTADÍSTICAS DETALLADAS DEL CACHE ==========");
        System.out.println("Total requests: " + totalRequests);
        System.out.println(" Cache hits: " + totalHits);
        System.out.println("Cache misses: " + totalMisses);
        
        if (totalRequests > 0) {
            double hitRatio = (double) totalHits / totalRequests * 100;
            System.out.println("Hit ratio: " + String.format("%.2f%%", hitRatio));
        }
        
        System.out.println("========== CONTENIDO ACTUAL DEL CACHE ==========");
        System.out.println(" Items en cache: " + cache.size());
        
        if (cache.size() > 0) {
            System.out.println(" Keys almacenadas:");
            cache.keySet().forEach(key -> {
                Long timestamp = timestamps.get(key);
                if (timestamp != null) {
                    long edad = (System.currentTimeMillis() - timestamp) / 60000; // en minutos
                    Object item = cache.get(key);
                    String tipo = "Unknown";
                    int items = 0;
                    
                    if (item instanceof java.util.List) {
                        items = ((java.util.List<?>) item).size();
                        tipo = "List";
                    } else if (item instanceof String) {
                        tipo = "String";
                        items = 1;
                    }
                    
                    System.out.println("  " + key + " [" + tipo + ", " + items + " items, " + edad + " min]");
                } else {
                    System.out.println("  " + key + " [Sin timestamp]");
                }
            });
        }
        
        System.out.println("\n ========== CONFIGURACIÓN TTL ==========");
        System.out.println("Categorías: " + (TTL_CATEGORIAS/60000) + " minutos");
        System.out.println("Productos populares: " + (TTL_PRODUCTOS_POPULARES/60000) + " minutos");
        System.out.println(" Búsquedas: " + (TTL_BUSQUEDAS/60000) + " minutos");
        System.out.println(" Configuraciones: " + (TTL_CONFIGURACIONES/60000) + " minutos");
        System.out.println("=======================================================\n");
    }
    
    /**
     * Invalidar cache específico por producto (cuando se actualiza)
     */
    public void invalidarCacheProducto(String identificador) {
        boolean invalidado = false;
        
        // Invalidar cache específico de PLU
        String keyPlu = "plu_" + identificador;
        if (cache.remove(keyPlu) != null) {
            timestamps.remove(keyPlu);
            invalidado = true;
        }
        
        // Invalidar cache específico de código de barras
        String keyCodigo = "codigo_" + identificador;
        if (cache.remove(keyCodigo) != null) {
            timestamps.remove(keyCodigo);
            invalidado = true;
        }
        
        // Invalidar productos populares para que se actualicen
        cache.keySet().removeIf(key -> key.startsWith("productos_populares_"));
        timestamps.keySet().removeIf(key -> key.startsWith("productos_populares_"));
        
        if (invalidado) {
            System.out.println(" CACHE INVALIDATED - Producto: " + identificador + " - Cache limpiado");
        }
    }
    
    /**
     * Limpiar todo el cache
     */
    public void limpiarCacheCompleto() {
        int itemsEliminados = cache.size();
        cache.clear();
        timestamps.clear();
        System.out.println(" CACHE CLEARED - " + itemsEliminados + " items eliminados del cache");
    }
    
    /**
     * SINCRONIZACIÓN AVANZADA: Integración HO → POS para cambios en tiempo real
     * FLUJO: BD → Validar estado → Cache (actualizar/eliminar/agregar según estado)
     */
    public void sincronizarProductoDesdeHO(Long productoId) {
        System.out.println(" SINCRONIZACIÓN HO→POS para producto ID: " + productoId);
        System.out.println("Estado cache ANTES - Items: " + cache.size());
        
        try {
            // PASO 1: Obtener PLU del producto para consultarlo con la función especializada
            com.dao.SetupDao setupDao = new com.dao.SetupDao();
            MovimientosDetallesBean productoBD = setupDao.findProductoByIdParaCache(productoId);
            
            if (productoBD == null) {
                // CASO 1: Producto eliminado/no existe → ELIMINAR del cache
                System.out.println("Producto ID " + productoId + " NO existe en BD → ELIMINANDO del cache");
                eliminarProductoDelCache(productoId);
                
                // Invalidar listas populares al eliminar productos
                invalidarCachesTipo("productos_populares");
                System.out.println(" Listas populares invalidadas tras eliminación de BD");
                return;
            }
            
            // PASO 2: Usar función especializada para validar si es un producto KIOSCO válido
            String plu = productoBD.getPlu();
            System.out.println("Producto ID " + productoId + " - " + productoBD.getDescripcion() + " (PLU: " + plu + ")");
            
            // Consultar con la función que valida todas las reglas de negocio
            MovimientosDao dao = new MovimientosDao();
            MovimientosDetallesBean productoValidado = null;
            try {
                productoValidado = dao.findByPluKIOSCO(plu);
            } catch (DAOException e) {
                System.err.println("Error consultando producto PLU " + plu + " con función especializada: " + e.getMessage());
                return;
            }
            
            // PASO 3: Verificar si está en cache
            boolean estaEnCache = productoEstaEnCache(productoId);
            
            if (productoValidado != null) {
                // CASO A: Producto VÁLIDO según reglas de negocio
                System.out.println(" Producto VÁLIDO para KIOSCO (Estado: " + productoValidado.getEstado() + ", Tipo: " + productoValidado.getTipo() + ", Bodega: " + productoValidado.getBodegasId() + ")");
                
                if (estaEnCache) {
                    // Actualizar cache con datos validados
                    System.out.println(" Producto EN cache → ACTUALIZANDO con datos validados");
                    actualizarProductoEnCacheDirecto(productoValidado);
                } else {
                    // Agregar al cache
                    System.out.println(" Producto NO en cache → AGREGANDO producto válido");
                    agregarProductoDirectoAlCache(productoValidado);
                }
                
                // IMPORTANTE: Invalidar listas populares para que se actualicen con nuevos precios
                invalidarCachesTipo("productos_populares");
                System.out.println(" Listas populares invalidadas - se actualizarán en próxima consulta");
            } else {
                // CASO B: Producto NO VÁLIDO según reglas de negocio
                System.out.println("Producto NO VÁLIDO para KIOSCO (no cumple criterios de fnc_buscar_productos_market_plu)");
                
                if (estaEnCache) {
                    // Eliminar del cache porque ya no cumple criterios
                    System.out.println(" Producto EN cache pero YA NO VÁLIDO → ELIMINANDO");
                    eliminarProductoDelCache(productoId);
                    
                    // Invalidar listas populares al eliminar productos
                    invalidarCachesTipo("productos_populares");
                    System.out.println(" Listas populares invalidadas tras eliminación");
                } else {
                    // No hacer nada
                    System.out.println(" Producto NO en cache y NO es válido → IGNORANDO");
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error sincronizando producto ID " + productoId + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("Estado cache DESPUÉS - Items: " + cache.size());
    }
    
    /**
     * SINCRONIZACIÓN INTELIGENTE: Actualizar si existe, agregar solo si es KIOSCO+bodega
     * FLUJO: Verificar cache → Si existe actualizar → Si no existe: validar KIOSCO+bodega → agregar solo si cumple
     */
    public void sincronizarProductoInteligente(Long productoId) {
        System.out.println(" Iniciando SINCRONIZACIÓN INTELIGENTE para producto ID: " + productoId);
        
        try {
            // PASO 1: Verificar si el producto YA está en cache
            boolean estaEnCache = productoEstaEnCache(productoId);
            
            if (estaEnCache) {
                // CASO A: Producto YA en cache → ACTUALIZAR
                System.out.println(" Producto ID " + productoId + " YA está en cache → ACTUALIZANDO");
                agregarProductoAlCachePorId(productoId); // Reutilizar método existente
                
            } else {
                // CASO B: Producto NO en cache → VALIDAR antes de agregar
                System.out.println(" Producto ID " + productoId + " NO está en cache → VALIDANDO si debe agregarse");
                
                // Consultar BD para validar tipo y bodega
                com.dao.SetupDao setupDao = new com.dao.SetupDao();
                MovimientosDetallesBean producto = setupDao.findProductoByIdParaCache(productoId);
                
                if (producto != null) {
                    boolean esKiosco = (producto.getTipo() == 23 || producto.getTipo() == 25);
                    boolean tieneBodega = (producto.getBodegasId() > 0);
                    
                    System.out.println("Validación producto ID " + productoId + ":");
                    System.out.println("    Tipo: " + producto.getTipo() + " (KIOSCO: " + esKiosco + ")");
                    System.out.println("    Bodega ID: " + producto.getBodegasId() + " (Tiene bodega: " + tieneBodega + ")");
                    
                    if (esKiosco && tieneBodega) {
                        // AGREGAR al cache - cumple criterios
                        System.out.println(" CRITERIOS CUMPLIDOS → Agregando producto KIOSCO al cache");
                        agregarProductoDirectoAlCache(producto);
                        
                    } else {
                        // NO AGREGAR - no cumple criterios
                        String razon = "";
                        if (!esKiosco) razon += "No es KIOSCO ";
                        if (!tieneBodega) razon += "Sin bodega ";
                        
                        System.out.println(" CRITERIOS NO CUMPLIDOS → NO se agrega al cache. Razón: " + razon.trim());
                    }
                    
                } else {
                    System.out.println("Producto ID " + productoId + " no encontrado en BD");
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error en sincronización inteligente para producto ID " + productoId + ": " + e.getMessage());
        }
    }
    
    /**
     * Verificar si un producto está actualmente en cache (por PLU o código)
     */
    private boolean productoEstaEnCache(Long productoId) {
        try {
            // Consultar BD para obtener PLU y código del producto
            com.dao.SetupDao setupDao = new com.dao.SetupDao();
            MovimientosDetallesBean producto = setupDao.findProductoByIdParaCache(productoId);
            
            if (producto != null) {
                // Verificar si está en cache por PLU
                if (producto.getPlu() != null && !producto.getPlu().isEmpty()) {
                    String keyPlu = "plu_" + producto.getPlu();
                    if (cache.containsKey(keyPlu)) {
                        System.out.println(" Producto encontrado en cache por PLU: " + producto.getPlu());
                        return true;
                    }
                }
                
                // Verificar si está en cache por código de barras
                if (producto.getCodigoBarra() != null && !producto.getCodigoBarra().isEmpty()) {
                    String keyCodigo = "codigo_" + producto.getCodigoBarra();
                    if (cache.containsKey(keyCodigo)) {
                        System.out.println(" Producto encontrado en cache por código: " + producto.getCodigoBarra());
                        return true;
                    }
                }
            }
            
            return false;
            
        } catch (Exception e) {
            System.err.println("Error verificando si producto está en cache: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Agregar producto directamente al cache (asumiendo que ya fue validado)
     */
    private void agregarProductoDirectoAlCache(MovimientosDetallesBean producto) {
        long timestamp = System.currentTimeMillis();
        
        // Agregar en cache por PLU si existe
        if (producto.getPlu() != null && !producto.getPlu().isEmpty()) {
            String keyPlu = "plu_" + producto.getPlu();
            cache.put(keyPlu, producto);
            timestamps.put(keyPlu, timestamp);
            System.out.println(" CACHE ADD - Producto agregado por PLU: " + producto.getPlu() + " → " + producto.getDescripcion());
        }
        
        // Agregar en cache por código de barra si existe
        if (producto.getCodigoBarra() != null && !producto.getCodigoBarra().isEmpty()) {
            String keyCodigo = "codigo_" + producto.getCodigoBarra();
            cache.put(keyCodigo, producto);
            timestamps.put(keyCodigo, timestamp);
            System.out.println(" CACHE ADD - Producto agregado por código: " + producto.getCodigoBarra() + " → " + producto.getDescripcion());
        }
        
        System.out.println(" Producto KIOSCO ID " + producto.getId() + " agregado exitosamente al cache");
    }

    /**
     * AGREGAR producto al cache SIN eliminar contenido existente
     * FLUJO: Consultar BD → Agregar al cache → Mantener listas existentes
     */
    public void agregarProductoAlCachePorId(Long productoId) {
        System.out.println(" Iniciando AGREGADO de producto ID: " + productoId + " (sin eliminar cache existente)");
        
        try {
            //  PASO 1: IR DIRECTAMENTE A BD (SIN INVALIDAR CACHE EXISTENTE)
            System.out.println(" BD QUERY - Consultando producto ID " + productoId + " desde Base de Datos");
            com.dao.SetupDao setupDao = new com.dao.SetupDao();
            MovimientosDetallesBean producto = setupDao.findProductoByIdParaCache(productoId);
            
            if (producto != null) {
                //  PASO 2: VALIDAR CON FUNCIÓN ESPECIALIZADA (como la sincronización HO)
                String plu = producto.getPlu();
                System.out.println(" Validando producto con función especializada - PLU: " + plu);
                
                try {
                    MovimientosDao dao = new MovimientosDao();
                    MovimientosDetallesBean productoValidado = dao.findByPluKIOSCO(plu);
                    
                    if (productoValidado != null) {
                        System.out.println(" Producto VÁLIDO para KIOSCO: " + productoValidado.getDescripcion() + " (Estado: " + productoValidado.getEstado() + ", Tipo: " + productoValidado.getTipo() + ")");
                        
                        // PASO 3: AGREGAR/ACTUALIZAR EN CACHE (sin invalidar listas)
                        long timestamp = System.currentTimeMillis();
                        
                        // Agregar en cache por PLU si existe
                        if (productoValidado.getPlu() != null && !productoValidado.getPlu().isEmpty()) {
                            String keyPlu = "plu_" + productoValidado.getPlu();
                            cache.put(keyPlu, productoValidado);
                            timestamps.put(keyPlu, timestamp);
                            System.out.println(" CACHE ADD - Producto agregado por PLU: " + productoValidado.getPlu() + " → " + productoValidado.getDescripcion());
                        }
                        
                        // Agregar en cache por código de barra si existe
                        if (productoValidado.getCodigoBarra() != null && !productoValidado.getCodigoBarra().isEmpty()) {
                            String keyCodigo = "codigo_" + productoValidado.getCodigoBarra();
                            cache.put(keyCodigo, productoValidado);
                            timestamps.put(keyCodigo, timestamp);
                            System.out.println(" CACHE ADD - Producto agregado por código: " + productoValidado.getCodigoBarra() + " → " + productoValidado.getDescripcion());
                        }
                        
                        //  PASO 4: NO INVALIDAR LISTAS - Mantener productos populares existentes
                        System.out.println(" MANTENER - Listas de productos populares conservadas (no invalidadas)");
                        
                        System.out.println(" Producto KIOSCO ID " + productoId + " AGREGADO al cache sin eliminar contenido existente");
                    } else {
                        System.out.println("Producto ID " + productoId + " NO es válido para KIOSCO según función especializada (inactivo o no cumple criterios)");
                    }
                } catch (DAOException e) {
                    System.err.println("Error validando producto PLU " + plu + " con función especializada: " + e.getMessage());
                }
            } else {
                System.out.println("Producto ID " + productoId + " no encontrado en BD");
            }
            
        } catch (Exception e) {
            System.err.println("Error general agregando producto ID " + productoId + " al cache: " + e.getMessage());
        }
    }
    
    /**
     * Actualizar producto específico en cache por ID
     * FLUJO: Invalidar cache → Consultar BD → Actualizar cache con datos frescos
     */
    public void actualizarProductoEnCachePorId(Long productoId) {
        System.out.println(" Iniciando actualización de producto ID: " + productoId);
        
        try {
            //  PASO 1: INVALIDAR CACHE EXISTENTE para este producto
            boolean cacheInvalidado = invalidarCacheProductoPorId(productoId);
            if (cacheInvalidado) {
                System.out.println(" Cache invalidado para producto ID: " + productoId);
            }
            
            //  PASO 2: IR A BD Y CONSULTAR INFORMACIÓN ACTUALIZADA
            System.out.println(" BD QUERY - Consultando producto ID " + productoId + " desde Base de Datos");
            com.dao.SetupDao setupDao = new com.dao.SetupDao();
            MovimientosDetallesBean producto = setupDao.findProductoByIdParaCache(productoId);
            
            if (producto != null) {
                // Verificar si es producto de KIOSCO (tipo 23 = KIOSCO, tipo 25 = COMPUESTO)
                if (producto.getTipo() == 23 || producto.getTipo() == 25) {
                    System.out.println(" Producto KIOSCO encontrado: " + producto.getDescripcion() + " (ID: " + productoId + ")");
                    
                    // PASO 3: ACTUALIZAR CACHE CON DATOS FRESCOS DE BD
                    long timestamp = System.currentTimeMillis();
                    
                    // Actualizar en cache por PLU si existe
                    if (producto.getPlu() != null && !producto.getPlu().isEmpty()) {
                        String keyPlu = "plu_" + producto.getPlu();
                        cache.put(keyPlu, producto);
                        timestamps.put(keyPlu, timestamp);
                        System.out.println("CACHE STORE - Producto actualizado por PLU: " + producto.getPlu() + " -> " + producto.getDescripcion());
                    }
                    
                    // Actualizar en cache por código de barra si existe
                    if (producto.getCodigoBarra() != null && !producto.getCodigoBarra().isEmpty()) {
                        String keyCodigo = "codigo_" + producto.getCodigoBarra();
                        cache.put(keyCodigo, producto);
                        timestamps.put(keyCodigo, timestamp);
                        System.out.println("CACHE STORE - Producto actualizado por código: " + producto.getCodigoBarra() + " -> " + producto.getDescripcion());
                    }
                    
                    //  PASO 4: INVALIDAR LISTAS para forzar recarga con datos actualizados
                    invalidarCachesTipo("productos_populares");
                    invalidarCachesTipo("busqueda");
                    System.out.println(" Listas de productos invalidadas - se recargarán automáticamente con datos frescos");
                    
                    System.out.println(" Producto KIOSCO ID " + productoId + " actualizado completamente en cache desde BD");
                } else {
                    System.out.println(" Producto ID " + productoId + " no es tipo KIOSCO (tipo: " + producto.getTipo() + ") - no se actualiza cache KIOSCO");
                }
            } else {
                System.out.println("Producto ID " + productoId + " no encontrado en BD");
            }
            
        } catch (java.sql.SQLException e) {
            System.err.println("Error BD consultando producto ID " + productoId + ": " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error general actualizando producto ID " + productoId + " en cache: " + e.getMessage());
        }
    }
    
    /**
     * Invalidar cache específico para un producto por ID
     * @param productoId ID del producto a invalidar
     * @return true si se invalidó algo, false si no había cache
     */
    private boolean invalidarCacheProductoPorId(Long productoId) {
        boolean invalidado = false;
        
        // Necesitamos buscar en cache por PLU y código ya que no guardamos por ID
        // Esto es una limitación del diseño actual, pero invalidamos por patrones
        
        // Invalidar caches que podrían contener este producto
        int itemsAntes = cache.size();
        
        // Invalidar productos populares (que podrían contener este producto)
        invalidarCachesTipo("productos_populares");
        invalidarCachesTipo("busqueda");
        
        int itemsDespues = cache.size();
        if (itemsAntes > itemsDespues) {
            invalidado = true;
            System.out.println(" " + (itemsAntes - itemsDespues) + " items de cache invalidados para producto ID " + productoId);
        }
        
        return invalidado;
    }
    
    /**
     * Mostrar contenido actual del cache (para debugging)
     */
    public void mostrarContenidoCache() {
        System.out.println("\n ========== CONTENIDO ACTUAL DEL CACHE ==========");
        System.out.println("Total items en cache: " + cache.size());
        
        if (cache.isEmpty()) {
            System.out.println(" Cache vacío - No hay items almacenados");
        } else {
            System.out.println("Items almacenados:");
            cache.forEach((key, value) -> {
                Long timestamp = timestamps.get(key);
                long edad = timestamp != null ? (System.currentTimeMillis() - timestamp) / 60000 : 0;
                
                String tipoItem = "";
                String detalleItems = "";
                
                if (key.startsWith("plu_")) {
                    tipoItem = " PLU";
                    if (value instanceof MovimientosDetallesBean) {
                        MovimientosDetallesBean producto = (MovimientosDetallesBean) value;
                        detalleItems = " → " + producto.getDescripcion();
                    }
                } else if (key.startsWith("codigo_")) {
                    tipoItem = "Código";
                    if (value instanceof MovimientosDetallesBean) {
                        MovimientosDetallesBean producto = (MovimientosDetallesBean) value;
                        detalleItems = " → " + producto.getDescripcion();
                    }
                } else if (key.startsWith("busqueda_")) {
                    tipoItem = " Búsqueda";
                    if (value instanceof List) {
                        List<?> lista = (List<?>) value;
                        detalleItems = " → " + lista.size() + " productos";
                        if (!lista.isEmpty() && lista.get(0) instanceof MovimientosDetallesBean) {
                            // Mostrar los primeros 3 productos
                            StringBuilder nombres = new StringBuilder();
                            for (int i = 0; i < Math.min(3, lista.size()); i++) {
                                MovimientosDetallesBean prod = (MovimientosDetallesBean) lista.get(i);
                                if (i > 0) nombres.append(", ");
                                nombres.append(prod.getDescripcion());
                            }
                            if (lista.size() > 3) nombres.append("...");
                            detalleItems += " [" + nombres.toString() + "]";
                        }
                    }
                } else if (key.startsWith("productos_populares_")) {
                    tipoItem = "Productos populares";
                    if (value instanceof List) {
                        List<?> lista = (List<?>) value;
                        detalleItems = " → " + lista.size() + " productos";
                        if (!lista.isEmpty() && lista.get(0) instanceof MovimientosDetallesBean) {
                            // Mostrar los primeros 5 productos más populares
                            StringBuilder nombres = new StringBuilder();
                            for (int i = 0; i < Math.min(5, lista.size()); i++) {
                                MovimientosDetallesBean prod = (MovimientosDetallesBean) lista.get(i);
                                if (i > 0) nombres.append(", ");
                                nombres.append(prod.getDescripcion());
                            }
                            if (lista.size() > 5) nombres.append("...");
                            detalleItems += " [" + nombres.toString() + "]";
                        }
                    }
                } else if (key.equals("categorias")) {
                    tipoItem = " Categorías";
                    if (value instanceof List) {
                        List<?> lista = (List<?>) value;
                        detalleItems = " → " + lista.size() + " categorías";
                    }
                } else {
                    tipoItem = " Config";
                }
                
                System.out.println("    " + tipoItem + " - Key: '" + key + "' (edad: " + edad + " min)" + detalleItems);
            });
            
            // Mostrar métricas rápidas
            double hitRatio = totalRequests > 0 ? ((double) totalHits / totalRequests) * 100 : 0;
            System.out.println("\nMétricas:");
            System.out.println("    Total consultas: " + totalRequests);
            System.out.println("     Cache hits: " + totalHits + " (" + String.format("%.1f%%", hitRatio) + ")");
            System.out.println("    Cache misses: " + totalMisses + " (" + String.format("%.1f%%", (100 - hitRatio)) + ")");
        }
        System.out.println("================================================\n");
    }
}
