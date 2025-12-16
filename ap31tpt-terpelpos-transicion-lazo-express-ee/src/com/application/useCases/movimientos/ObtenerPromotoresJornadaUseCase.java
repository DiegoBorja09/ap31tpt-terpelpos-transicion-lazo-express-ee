package com.application.useCases.movimientos;

import com.application.core.BaseUseCases;
import com.bean.PersonaBean;
import com.domain.entities.JornadaEntity;
import com.firefuel.Main;
import com.infrastructure.repositories.JornadaRepository;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.database.DatabaseNames;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.math.BigInteger;
import com.dao.DAOException;

/**
 * Caso de uso para obtener los promotores con jornadas activas
 * Este caso de uso consulta las jornadas activas junto con la información
 * de los promotores y tipos de identificación asociados
 */
public class ObtenerPromotoresJornadaUseCase implements BaseUseCases<List<PersonaBean>> {

    private final EntityManagerFactory entityManagerFactory;

    public ObtenerPromotoresJornadaUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public List<PersonaBean> execute() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        List<PersonaBean> promotores = new ArrayList<>();
        
        try {
            JornadaRepository jornadaRepository = new JornadaRepository(entityManager);
            List<Object[]> results = jornadaRepository.findAllActiveJornadasWithPersonInfo();
            
            for (Object[] result : results) {
                PersonaBean promotor = new PersonaBean();
                
                // Mapear los campos del resultado SQL nativo a PersonaBean
                // El query retorna: j.*, p.nombre as nombres, p.identificacion, ti.descripcion
                // Necesitamos identificar la estructura real de las columnas
                
                // Buscar los valores en las posiciones correctas basándose en los tipos
                Long jornadaId = null;
                Date fechaInicio = null;
                Long personaId = null;
                Long grupoJornada = null;
                String nombres = null;
                String identificacion = null;
                String tipoIdentificacionDesc = null;
                
                // Extraer datos basándose en la estructura real de la tabla jornadas
                try {
                    // Estructura del query: J.* (10 columnas) + nombres + identificacion + descripcion
                    // J.*: id, personas_id, fecha_inicio, fecha_fin, sincronizado, grupo_jornada, saldo, surtidores_id, equipos_id, atributos
                    if (result.length >= 13) {
                        jornadaId = convertToLong(result[0]);                    // j.id (index 0)
                        personaId = convertToLong(result[1]);                   // j.personas_id (index 1)
                        fechaInicio = convertToDate(result[2]);                 // j.fecha_inicio (index 2)
                        grupoJornada = convertToLong(result[5]);                // j.grupo_jornada (index 5)
                        
                        // Los últimos 3 campos del SELECT son: nombres, identificacion, descripcion
                        nombres = (String) result[10];                         // p.nombre nombres (index 10)
                        identificacion = (String) result[11];                  // p.identificacion (index 11)
                        tipoIdentificacionDesc = (String) result[12];          // ti.descripcion (index 12)
                    }
                } catch (Exception e) {
                    System.err.println("Error al mapear resultado: " + e.getMessage());
                    e.printStackTrace();
                    continue; // Saltar este registro si hay error
                }
                
                promotor.setId(personaId);
                promotor.setIdentificacion(identificacion);
                promotor.setTipoIdentificacionDesc(tipoIdentificacionDesc);
                promotor.setFechaInicio(fechaInicio);
                promotor.setNombre(nombres);
                promotor.setApellidos("");  // Campo vacío según requerimiento
                promotor.setGrupoJornadaId(grupoJornada);
                Main.persona = promotor;
                promotores.add(promotor);
            }
            
            return promotores;
        } catch (DAOException e) {
            throw new RuntimeException("Error al obtener promotores de jornada: " + e.getMessage(), e);
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
    
    /**
     * Convierte un Object a Long, manejando tanto Long como BigInteger
     * @param value el valor a convertir
     * @return el valor como Long
     */
    private Long convertToLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof BigInteger) {
            return ((BigInteger) value).longValue();
        }
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        }
        // Fallback: intentar convertir desde String
        return Long.valueOf(value.toString());
    }
    
    /**
     * Convierte un Object a Date, manejando Date y Timestamp
     * @param value el valor a convertir
     * @return el valor como Date
     */
    private Date convertToDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Date) {
            return (Date) value;
        }
        if (value instanceof java.sql.Timestamp) {
            return new Date(((java.sql.Timestamp) value).getTime());
        }
        if (value instanceof java.sql.Date) {
            return new Date(((java.sql.Date) value).getTime());
        }
        // Si no es un tipo de fecha conocido, retornar null
        return null;
    }
}