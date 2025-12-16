package com.application.useCases.equipos;

import com.application.core.BaseUseCases;
import com.domain.entities.DispositivoEntity;
import com.infrastructure.repositories.DispositivoRepository;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.database.DatabaseNames;
import com.google.gson.JsonObject;

/*public void ingresarDispositivo(String tipos, String conector, String interfaz, String estado, JsonObject d_atributos) {
    try {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "INSERT INTO dispositivos (tipos, conector, interfaz, puerto, notificar, icono, estado, d_atributos) VALUES\n"
                + "(?,?,?,NULL,NULL,NULL,?,(?)::json);";
        PreparedStatement ps;
        ps = conexion.prepareStatement(sql);
        ps.setString(1, tipos);
        ps.setString(2, conector);
        ps.setString(3, interfaz);
        ps.setString(4, estado);
        ps.setString(5, d_atributos.toString());
        ps.executeUpdate();

    } catch (SQLException ex) {
        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    } catch (Exception ex) {
        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    }
}*/
public class IngresarDispositivoUseCase implements BaseUseCases<Boolean> {

    private final EntityManagerFactory entityManagerFactory;
    private final String tipos;
    private final String conector;
    private final String interfaz;
    private final String estado;
    private final JsonObject dAtributos;

    public IngresarDispositivoUseCase(String tipos, String conector, String interfaz, String estado, JsonObject dAtributos) {
        this.tipos = tipos;
        this.conector = conector;
        this.interfaz = interfaz;
        this.estado = estado;
        this.dAtributos = dAtributos;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Boolean execute() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;
        
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            
            // Crear nueva entidad DispositivoEntity
            DispositivoEntity dispositivo = new DispositivoEntity();
            dispositivo.setTipos(tipos);
            dispositivo.setConector(conector);
            dispositivo.setInterfaz(interfaz);
            dispositivo.setEstado(estado);
            dispositivo.setAtributos(dAtributos != null ? dAtributos.toString() : null);
            
            DispositivoRepository dispositivoRepository = new DispositivoRepository(entityManager);
            // Usar insert nativo para castear d_atributos a JSON
            dispositivoRepository.saveNative(dispositivo);
            
            transaction.commit();
            return true;
            
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
} 