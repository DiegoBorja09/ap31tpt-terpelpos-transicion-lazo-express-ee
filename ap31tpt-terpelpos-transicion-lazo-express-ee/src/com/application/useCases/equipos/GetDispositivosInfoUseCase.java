package com.application.useCases.equipos;

import com.application.core.BaseUseCases;
import com.domain.entities.DispositivoEntity;
import com.infrastructure.repositories.DispositivoRepository;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.database.DatabaseNames;
import java.util.List;
import java.util.TreeMap;

/*public TreeMap<Long, DispositivosBean> dispositivosInfo() throws DAOException, SQLException {

    Connection conexion = Main.obtenerConexion("lazoexpresscore");

    TreeMap<Long, DispositivosBean> lista = new TreeMap<>();

    String sql = "select d.id, "
            + "d.tipos, "
            + "d.conector, "
            + "d.interfaz, "
            + "d.estado, "
            + "d.d_atributos "
            + "from dispositivos d ";
    PreparedStatement ps = conexion.prepareStatement(sql);
    ResultSet rs = ps.executeQuery();

    while (rs.next()) {
        DispositivosBean dBeans = new DispositivosBean();
        dBeans.setId(rs.getLong("ID"));
        dBeans.setTipos(rs.getString("TIPOS"));
        dBeans.setConector(rs.getString("CONECTOR"));
        dBeans.setInterfaz(rs.getString("INTERFAZ"));
        dBeans.setEstado(rs.getString("ESTADO"));
        dBeans.setAtributos(rs.getString("D_ATRIBUTOS"));
        lista.put(dBeans.getId(), dBeans);
    }

    return lista;
}*/
public class GetDispositivosInfoUseCase implements BaseUseCases<TreeMap<Long, DispositivoEntity>> {

    private final EntityManagerFactory entityManagerFactory;

    public GetDispositivosInfoUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public TreeMap<Long, DispositivoEntity> execute() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        
        try {
            DispositivoRepository dispositivoRepository = new DispositivoRepository(entityManager);
            List<DispositivoEntity> dispositivos = dispositivoRepository.findAll();
            
            TreeMap<Long, DispositivoEntity> lista = new TreeMap<>();
            
            for (DispositivoEntity dispositivo : dispositivos) {
                // Convertir Integer id a Long para mantener compatibilidad con la función original
                Long id = dispositivo.getId() != null ? dispositivo.getId().longValue() : 0L;
                lista.put(id, dispositivo);
            }
            
            return lista;
            
        } catch (Exception e) {
            // Retornar TreeMap vacío en caso de error
            return new TreeMap<>();
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
} 