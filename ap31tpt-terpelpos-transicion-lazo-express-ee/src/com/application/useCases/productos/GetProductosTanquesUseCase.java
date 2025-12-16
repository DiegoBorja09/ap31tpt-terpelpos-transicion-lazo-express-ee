package com.application.useCases.productos;

import com.application.core.BaseUseCasesWithParams;
import com.dao.DAOException;
import com.infrastructure.repositories.ProductoTanqueRepository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*  public ArrayList<ProductoBean> getProductosTanques(long familia) {
       Connection conexion = Main.obtenerConexion("lazoexpresscore");
       ArrayList<ProductoBean> tanques = new ArrayList<>();
       try {
           String sql = "select  p.id,p.familias,p.descripcion,p.precio,p.unidad_medida_id,u.alias "
                   + "from productos p "
                   + "inner join unidades u on u.id = p.unidad_medida_id "
                   + "where p.familias = ? "
                   + "order by  p.descripcion asc ";
           PreparedStatement ps = conexion.prepareStatement(sql);
           ps.setLong(1, familia);
           ResultSet re = ps.executeQuery();
           while (re.next()) {
               ProductoBean producto = new ProductoBean();
               producto.setId(re.getInt("id"));
               producto.setDescripcion(re.getString("descripcion"));
               producto.setFamiliaId(re.getInt("familias"));
               producto.setPrecio(re.getFloat("precio"));
               producto.setSaldo(0);
               producto.setUnidades_medida_id(re.getLong("unidad_medida_id"));
               producto.setUnidades_medida(re.getString("alias"));
               tanques.add(producto);
           }
       } catch (SQLException ex) {
           Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, ex);
       }
       return tanques;
   } */

public class GetProductosTanquesUseCase implements BaseUseCasesWithParams<Long, List<ProductoTanqueDto>> {

    private static final Logger logger = Logger.getLogger(GetProductosTanquesUseCase.class.getName());
    private final ProductoTanqueRepository repository;

    public GetProductosTanquesUseCase(EntityManager entityManager) {
        this.repository = new ProductoTanqueRepository(entityManager);
    }

    @Override
    public List<ProductoTanqueDto> execute(Long familia) {
        try {
            List<ProductoTanqueDto> productos = repository.getProductosTanques(familia);
            logger.log(Level.INFO, "Productos tanques obtenidos correctamente para familia: " + familia);
            return productos;
        } catch (DAOException e) {
            logger.log(Level.SEVERE, "Error al obtener productos tanques: " + e.getMessage(), e);
            throw new RuntimeException("Error al obtener productos tanques", e);
        }
    }
}