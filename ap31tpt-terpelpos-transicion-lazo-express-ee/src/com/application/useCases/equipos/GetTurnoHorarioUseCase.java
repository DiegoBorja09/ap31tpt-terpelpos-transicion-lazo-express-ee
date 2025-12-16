package com.application.useCases.equipos;

import com.application.core.BaseUseCases;
import com.domain.entities.LtHorarioEntity;
import com.infrastructure.repositories.LtHorarioRepository;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.database.DatabaseNames;
import com.google.gson.JsonObject;
import java.util.Optional;

/*public JsonObject getTurnoHorario(long id) {
    JsonObject data = null;
    try {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "SELECT * FROM LT_HORARIOS WHERE ID= ?;";
        PreparedStatement ps = conexion.prepareStatement(sql);
        ps.setLong(1, id);
        ResultSet re = ps.executeQuery();
        if (re.next()) {
            data = new JsonObject();
            data.addProperty("horaInicio", re.getString("hora_inicio"));
            data.addProperty("horaFin", re.getString("hora_fin"));
        }
    } catch (SQLException e) {
    }
    return data;
}*/
public class GetTurnoHorarioUseCase implements BaseUseCases<JsonObject> {

    private final EntityManagerFactory entityManagerFactory;
    private final long id;

    public GetTurnoHorarioUseCase(long id) {
        this.id = id;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public JsonObject execute() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        
        try {
            LtHorarioRepository horarioRepository = new LtHorarioRepository(entityManager);
            Optional<LtHorarioEntity> horarioOpt = horarioRepository.findById(id);
            
            if (horarioOpt.isPresent()) {
                LtHorarioEntity horario = horarioOpt.get();
                JsonObject data = new JsonObject();
                
                // Convertir Time a String igual que la función original
                String horaInicio = horario.getHoraInicio() != null ? horario.getHoraInicio().toString() : null;
                String horaFin = horario.getHoraFin() != null ? horario.getHoraFin().toString() : null;
                
                data.addProperty("horaInicio", horaInicio);
                data.addProperty("horaFin", horaFin);
                
                return data;
            }
            
            return null;
            
        } catch (Exception e) {
            return null;
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
} 