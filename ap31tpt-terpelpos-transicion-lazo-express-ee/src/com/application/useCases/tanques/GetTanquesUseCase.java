package com.application.useCases.tanques;

import com.application.core.BaseUseCases;
import com.application.useCases.productos.ProductoTanqueDto;
import com.bean.BodegaBean;
import com.bean.ProductoBean;
import com.dao.DAOException;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.BodegaTanqueRepository;
import com.infrastructure.repositories.ProductoTanqueRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/* public LinkedList<BodegaBean> getTanques() {
       Connection conexion = Main.obtenerConexion("lazoexpresscore");
       LinkedList<BodegaBean> tanques = new LinkedList<>();
       String sql = "select id, bodega, codigo, "
               + "atributos->>'tanque' numero, "
               + "atributos->>'volumen_maximo' volumen_maximo, "
               + "atributos->>'familia' familia "
               + "from ct_bodegas "
               + "where atributos->>'tipo' = 'T' "
               + "and atributos->>'estado'='A' order by 4";
       try ( Statement ps = conexion.createStatement();) {
           ResultSet re = ps.executeQuery(sql);
           while (re.next()) {
               BodegaBean tanque = new BodegaBean();
               tanque.setId(re.getInt("id"));
               tanque.setDescripcion(re.getString("bodega"));
               tanque.setNumeroStand(re.getInt("numero"));
               tanque.setVolumenMaximo(re.getInt("volumen_maximo"));
               tanque.setFamiliaId(re.getLong("familia"));
               tanque.setProductos(getProductosTanques(tanque.getFamiliaId()));
               tanques.add(tanque);
           }
       } catch (SQLException ex) {
           Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, ex);
       }
       return tanques;
   }  */

/**
 * Caso de uso para obtener la lista de tanques (bodegas) registradas.
 * Devuelve un TreeMap donde la clave es el ID del tanque y el valor su
 * descripción.
 * Reemplaza: MovimientosDao.getTanques()
 * Usado en:ReporteInventarioKardex
 */

public class GetTanquesUseCase implements BaseUseCases<LinkedList<BodegaBean>> {

    private static final Logger logger = Logger.getLogger(GetTanquesUseCase.class.getName());
    private final EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;
    private final BodegaTanqueRepository repository;
    private final ProductoTanqueRepository getProductosTanquesRepo;

    public GetTanquesUseCase() {
        entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
        this.entityManager = entityManagerFactory.createEntityManager();
        this.repository = new BodegaTanqueRepository(entityManager);
        this.getProductosTanquesRepo = new ProductoTanqueRepository(entityManager);
    }

    @Override
    public LinkedList<BodegaBean> execute() {
        try {
            entityManager.getTransaction().begin();

            // Obtener los tanques
            List<TanqueDto> tanquesDto = repository.getTanques();
            LinkedList<BodegaBean> tanques = new LinkedList<>();

            // Convertir DTOs a BodegaBean y obtener productos
            for (TanqueDto tanqueDto : tanquesDto) {
                BodegaBean tanque = new BodegaBean();
                tanque.setId(tanqueDto.getId());
                tanque.setDescripcion(tanqueDto.getDescripcion());
                tanque.setNumeroStand(tanqueDto.getNumeroTanque());
                tanque.setVolumenMaximo(tanqueDto.getVolumenMaximo());
                tanque.setFamiliaId(tanqueDto.getFamiliaId());

                // Obtener productos usando el caso de uso existente
                List<ProductoTanqueDto> productosDto = getProductosTanquesRepo.getProductosTanques(tanque.getFamiliaId());
                ArrayList<ProductoBean> productos = new ArrayList<>();

                // Convertir ProductoTanqueDto a ProductoBean
                for (ProductoTanqueDto productoDto : productosDto) {
                    ProductoBean producto = new ProductoBean();
                    producto.setId(productoDto.getId());
                    producto.setDescripcion(productoDto.getDescripcion());
                    producto.setFamiliaId(productoDto.getFamiliaId());
                    producto.setPrecio(productoDto.getPrecio());
                    producto.setSaldo(productoDto.getSaldo());
                    producto.setUnidades_medida_id(productoDto.getUnidades_medida_id());
                    producto.setUnidades_medida(productoDto.getUnidades_medida());
                    productos.add(producto);
                }

                tanque.setProductos(productos);
                tanques.add(tanque);
            }

            logger.log(Level.INFO, "Tanques obtenidos correctamente");
            return tanques;

        } catch (DAOException e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            logger.log(Level.SEVERE, "Error al obtener tanques: " + e.getMessage(), e);
            throw new RuntimeException("Error al obtener tanques", e);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }
}
