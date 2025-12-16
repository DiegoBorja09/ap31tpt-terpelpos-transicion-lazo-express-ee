package com.infrastructure.repositories;

import com.bean.CredencialBean;
import com.bean.JornadaBean;
import com.bean.ModulosBean;
import com.bean.PersonaBean;
import com.domain.entities.PersonaEntity;
import com.google.gson.*;
import com.infrastructure.Enums.SqlQueryEnum;
import com.infrastructure.core.BaseRepositoryInterface;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class PersonaRepository implements BaseRepositoryInterface<PersonaEntity> {


    private final EntityManager entityManager;

    public PersonaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public PersonaEntity save(PersonaEntity entity) {
        return null;
    }

    @Override
    public PersonaEntity update(PersonaEntity entity) {
        return null;
    }

    @Override
    public void delete(PersonaEntity entity) {

    }

    @Override
    public Optional<PersonaEntity> findById(Object id) {
        return Optional.empty();
    }

    @Override
    public List<PersonaEntity> findAll() {
        try {
            TypedQuery<PersonaEntity> query = entityManager.createQuery("SELECT p FROM Person p", PersonaEntity.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all persons", e);
        }
    }

    @Override
    public List<PersonaEntity> findByQuery(String jpql, Object... parameters) {
        return List.of();
    }

    @Override
    public List<?> findByNativeQuery(String sql, Object... parameters) {
        return List.of();
    }

    /**
     * Obtiene el historial completo de jornadas desde la base de datos.
     *
     * @return ArrayList de PersonaBean con los datos del historial de jornadas.
     */
    public ArrayList<PersonaBean> getAllJornadasHistoryRepository() {
        ArrayList<PersonaBean> personas = new ArrayList<>();
        try {
            String sql = SqlQueryEnum.GET_ALL_USUARIOS_CORE.getQuery();
            System.out.println("🔍 Consutla Obtener Jornadas History: " + sql);

            Query query = entityManager.createNativeQuery(sql);
            List<Object[]> results = query.getResultList();

            for (Object[] row : results) {
                PersonaBean persona = new PersonaBean();
                persona.setId(Long.parseLong(row[0].toString()));// PERSONAS_ID
                if(row[6]!=null){
                    persona.setPerfil(Integer.parseInt(row[6].toString()));// PERFILES_ID
                }
                persona.setNombre((String) row[1]);// NOMBRE
                persona.setIdentificacion((String) row[2]);// IDENTIFICACION
                persona.setEstado((String) row[3]);// ESTADO
                if(row[4]!=null){
                    persona.setTelefono((String) row[4]);// TELEFONO
                }

                if(row[5]!=null){
                    persona.setDireccion((String) row[5]);// DIRECCION
                }

                if(row[7]!=null){
                    persona.setTag((String) row[7]);// TAG
                }
                personas.add(persona);// AGREGAR PERSONA A LA LISTA
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener el historial de jornadas desde el repositorio", e);
        }
        return personas;
    }

    /**
     * Obtiene todos los usuarios del core desde la base de datos.
     *
     * @return ArrayList de PersonaBean con los datos de los usuarios.
     */
    public ArrayList<PersonaBean> getAllUsuariosCoreRepository() {
        ArrayList<PersonaBean> personas = new ArrayList<>();
        try {
            String sql = SqlQueryEnum.GET_ALL_USUARIOS_CORE.getQuery();
            Query query = entityManager.createNativeQuery(sql);
            List<Object[]> results = query.getResultList();

            for (Object[] row : results) {
                PersonaBean persona = new PersonaBean();
                persona.setId(((Number) row[0]).longValue()); // PERSONAS_ID
                persona.setNombre((String) row[1]); // NOMBRE
                persona.setIdentificacion((String) row[2]); // IDENTIFICACION
                persona.setEstado((String) row[3]); // ESTADO
                persona.setTelefono((String) row[4]); // TELEFONO
                persona.setDireccion((String) row[5]); // DIRECCION
                persona.setPerfil(((Number) row[6]).intValue()); // PERFILES_ID
                persona.setTag((String) row[7]); // TAG
                personas.add(persona);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los usuarios del core desde el repositorio", e);
        }
        return personas;
    }

    /**
     * Obtiene todos los promotores desde la base de datos.
     *
     * @return ArrayList de PersonaBean con los datos de los promotores.
     */
    public ArrayList<PersonaBean> getAllPromotoresRepository() {
        ArrayList<PersonaBean> personas = new ArrayList<>();
        try {
            String sql = SqlQueryEnum.GET_ALL_PROMOTORES.getQuery();
            Query query = entityManager.createNativeQuery(sql);
            List<Object[]> results = query.getResultList();

            for (Object[] row : results) {
                PersonaBean persona = new PersonaBean();
                persona.setId(((Number) row[0]).longValue()); // PERSONAS_ID
                persona.setNombre((String) row[1]); // NOMBRES
                persona.setApellidos((String) row[2]); // APELLIDOS
                persona.setIdentificacion((String) row[3]); // IDENTIFICACION
                personas.add(persona);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los promotores desde el repositorio", e);
        }
        return personas;
    }

    /**
     * Obtiene la lectura de un tag desde la base de datos.
     *
     * @return String con el valor de la lectura del tag, o null si no se encuentra.
     */
    public String obtenerLecturaTagRepository() {
        try {
            String sql = SqlQueryEnum.OBTENER_LECTURA_TAG.getQuery();
            Query query = entityManager.createNativeQuery(sql);
            Object result = query.getSingleResult();
            if (result instanceof Object[]) {
                Object[] row = (Object[]) result;
                return row.length > 0 ? (String) row[0] : null; // Asume que la columna 'lectura' es la primera
            }
            return (String) result; // En caso de que la consulta devuelva directamente el valor
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener la lectura del tag desde el repositorio", e);
        }
    }

    /**
     * Obtiene un usuario por lectura de identificador desde la base de datos.
     *
     * @param lectura El identificador del usuario.
     * @return PersonaBean con los datos del usuario, o null si no se encuentra.
     */
    public PersonaBean obtenerUsuarioRepository(String lectura) {
        try {
            String sql = SqlQueryEnum.OBTENER_USUARIO.getQuery();
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, lectura);
            Object[] result = (Object[]) query.getSingleResult();

            if (result != null) {
                PersonaBean persona = new PersonaBean();
                persona.setId(((Number) result[0]).longValue()); // ID
                persona.setIdentificacion((String) result[1]); // IDENTIFICACION
                persona.setPin(result[2] != null ? ((Number) result[2]).intValue() : 0); // PIN
                persona.setNombre((String) result[3]); // NOMBRES
                persona.setApellidos((String) result[4]); // APELLIDOS
                return persona;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener el usuario desde el repositorio", e);
        }
    }

    /**
     * Elimina todas las lecturas de tags desde la base de datos.
     */
    public void eliminarLecturaRepository() {
        try {
            String sql = SqlQueryEnum.ELIMINAR_LECTURA.getQuery();
            Query query = entityManager.createNativeQuery(sql);
            query.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar las lecturas de tags desde el repositorio", e);
        }
    }

    /**
     * Verifica si un usuario es administrador desde la base de datos.
     *
     * @param id El ID del usuario.
     * @return true si el usuario es administrador (perfil tipo 5), false en caso contrario.
     */
    public boolean isAdminRepository(long id) {
        try {
            String sql = SqlQueryEnum.IS_ADMIN.getQuery();
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, id);
            Object result = query.getSingleResult();
            long idPerfil = result instanceof Number ? ((Number) result).longValue() : 0;
            return idPerfil == 5;
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar si el usuario es administrador desde el repositorio", e);
        }
    }

    /**
     * Busca una persona por identificación, PIN y origen RFID desde la base de datos.
     *
     * @param identificacion La identificación de la persona.
     * @param pin            El PIN del usuario.
     * @param fromRFID       Indica si la búsqueda es por RFID.
     * @return PersonaBean con los datos de la persona, o null si no se encuentra.
     */
    public PersonaBean findPersonaRepository(String identificacion, int pin, boolean fromRFID) {
        System.out.println("🔍 findPersonaRepository: " + identificacion + " " + pin + " " + fromRFID);
        try {
            String sql = fromRFID ? SqlQueryEnum.FIND_PERSONA_RFID.getQuery() : SqlQueryEnum.FIND_PERSONA_NO_RFID.getQuery();
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, identificacion);
            if (!fromRFID) {
                query.setParameter(2, pin);
            }
            Object first = query.getResultList().stream().findFirst().orElse(null);
            Object[] result = (first instanceof Object[]) ? (Object[]) first : null;

            if (result == null) {
                return null;
            }

            PersonaBean persona = new PersonaBean();
            persona.setId(((Number) result[0]).longValue()); // ID
            persona.setIdentificacion((String) result[1]); // IDENTIFICACION
            persona.setPin(result[2] != null ? ((Number) result[2]).intValue() : 0); // PIN
            persona.setNombre((String) result[3]); // NOMBRES
            persona.setApellidos((String) result[4]); // APELLIDOS


            // Obtener permisos
            Query permisosQuery = entityManager.createNativeQuery(SqlQueryEnum.FIND_PERSONA_PERMISOS.getQuery());
            permisosQuery.setParameter(1, persona.getId());
            var permisosResult = permisosQuery.getResultList();

            if (!permisosResult.isEmpty()) {
                if (persona.getModulos() == null) {
                    persona.setModulos(new ArrayList<>());
                }

                for (Object row : permisosResult) {
                    Object[] permisosRow = (Object[]) row;
                    ModulosBean modulo = new ModulosBean();
                    modulo.setId(((Number) permisosRow[0]).intValue()); // modulos_id
                    modulo.setDescripcion((String) permisosRow[3]); // modulos_descripcion
                    persona.getModulos().add(modulo);
                }
            }

            return persona;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar la persona desde el repositorio", e);
        }
    }

    /**
     * Registra un tag para una persona en la base de datos.
     *
     * @param identificacion La identificación de la persona.
     * @param tag            El tag a registrar.
     * @return long con el ID de la persona actualizada, o 0 si no se encuentra.
     */
    public long registrarTagRepository(String identificacion, String tag) {
        try {
            entityManager.getTransaction().begin();
            // Limpiar el tag existente
            Query clearQuery = entityManager.createNativeQuery(SqlQueryEnum.CLEAR_PERSONA_TAG.getQuery());
            clearQuery.setParameter(1, tag);
            clearQuery.executeUpdate();

            // Registrar el nuevo tag y obtener el ID
            Query registerQuery = entityManager.createNativeQuery(SqlQueryEnum.REGISTER_PERSONA_TAG.getQuery());
            registerQuery.setParameter(1, tag);
            registerQuery.setParameter(2, identificacion);
            Object result = registerQuery.getSingleResult();
            entityManager.getTransaction().commit();
            return result instanceof Number ? ((Number) result).longValue() : 0;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Error al registrar el tag desde el repositorio", e);
        }
    }

    /**
     * Busca una persona por su ID en la base de datos.
     *
     * @param idPersona El ID de la persona.
     * @return PersonaBean con los datos de la persona, o null si no se encuentra.
     */
    public PersonaBean findPersonaByIdRepository(long idPersona) {
        try {
            String sql = SqlQueryEnum.FIND_PERSONA_BY_ID.getQuery();
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, idPersona);
            Object first = query.getResultList().stream().findFirst().orElse(null);
            Object[] result = (first instanceof Object[]) ? (Object[]) first : null;

            if (result == null) {
                return null;
            }

            PersonaBean persona = new PersonaBean();
            persona.setId(((Number) result[0]).longValue()); // ID
            persona.setIdentificacion((String) result[1]); // IDENTIFICACION
            persona.setNombre((String) result[2]); // NOMBRES
            persona.setApellidos((String) result[3]); // APELLIDOS
            persona.setPin(result[4] != null ? ((Number) result[4]).intValue() : 0); // PIN
            persona.setEmpresaId(((Number) result[5]).longValue()); // EMPRESAS_ID

            // Procesar módulos
            ArrayList<ModulosBean> modulosPersona = new ArrayList<>();
            try {
                Object modulosObj = result[6]; // modulos
                String modulosJson;

                if (modulosObj instanceof org.postgresql.util.PGobject) {
                    modulosJson = ((org.postgresql.util.PGobject) modulosObj).getValue();
                } else {
                    modulosJson = (String) modulosObj;
                }

                if (modulosJson != null) {
                    JsonArray jsonModulosArray = new Gson().fromJson(modulosJson, JsonArray.class);
                    for (JsonElement moduloElement : jsonModulosArray) {
                        JsonObject jsonModulo = moduloElement.getAsJsonObject();
                        ModulosBean modulo = new ModulosBean();
                        modulo.setId(jsonModulo.get("modulos_id").getAsInt());
                        modulo.setDescripcion(jsonModulo.get("modulos_descripcion").getAsString());
                        modulo.setLink(jsonModulo.get("link").getAsString());
                        modulosPersona.add(modulo);
                    }
                }
            } catch (JsonSyntaxException e) {
                throw new RuntimeException("Error al parsear los módulos JSON", e);
            }
            persona.setModulos(modulosPersona);

            return persona;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar la persona por ID desde el repositorio", e);
        }
    }

    /**
     * Obtiene el turno de una persona desde la base de datos.
     *
     * @param idPersona El ID de la persona.
     * @return PersonaBean con los datos del turno, o null si no se encuentra.
     */
    public PersonaBean getTurnoRepository(long idPersona) {
        try {
            String sql = SqlQueryEnum.GET_TURNO.getQuery();
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, idPersona);
            Object first = query.getResultList().stream().findFirst().orElse(null);
            Object[] result = (first instanceof Object[]) ? (Object[]) first : null;

            if (result == null) {
                return null;
            }

            PersonaBean persona = new PersonaBean();
            persona.setId(((Number) result[0]).longValue()); // id
            persona.setJornadaId(((Number) result[1]).longValue()); // idjornada
            persona.setFecha((Timestamp) result[2]); // fecha_inicio
            persona.setGrupoJornadaId(((Number) result[3]).longValue()); // grupo_jornada

            return persona;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener el turno de la persona desde el repositorio", e);
        }
    }

    /**
     * Obtiene el turno principal de una persona desde la base de datos.
     *
     * @return PersonaBean con los datos del turno principal, o null si no se encuentra.
     */
    public PersonaBean getPrincipalTurnoRepository() {
        try {
            String sql = SqlQueryEnum.GET_PRINCIPAL_TURNO.getQuery();
            Query query = entityManager.createNativeQuery(sql);
            Object first = query.getResultList().stream().findFirst().orElse(null);
            Object[] result = (first instanceof Object[]) ? (Object[]) first : null;

            if (result == null) {
                return null;
            }

            PersonaBean persona = new PersonaBean();
            persona.setId(((Number) result[0]).longValue()); // id
            persona.setNombre((String) result[2]); // nombre
            persona.setIdentificacion((String) result[3]); // identificacion
            persona.setTipoIdentificacionDesc((String) result[4]); // descripcion
            persona.setJornadaId(((Number) result[1]).longValue()); // idjornada
            persona.setGrupoJornadaId(((Number) result[6]).longValue()); // grupo_jornada
            persona.setFecha((Timestamp) result[5]); // fecha_inicio

            return persona;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener el turno principal desde el repositorio", e);
        }
    }

    /**
     * Verifica si una jornada está iniciada y es la última para una persona en la base de datos.
     *
     * @param persona    La persona asociada a la jornada.
     * @param credencial Las credenciales de la persona (no utilizadas en la consulta).
     * @return JornadaBean con los datos de la jornada, o null si no se encuentra.
     */
    public JornadaBean isIniciadaAndUltimoRepository(PersonaBean persona, CredencialBean credencial) {
        try {
            String sql = SqlQueryEnum.IS_INICIADA_AND_ULTIMO.getQuery();
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, persona.getId());
            Object first = query.getResultList().stream().findFirst().orElse(null);
            Object[] result = (first instanceof Object[]) ? (Object[]) first : null;

            if (result == null) {
                return null;
            }

            JornadaBean jornada = new JornadaBean();
            jornada.setIniciada(true);
            jornada.setSaldo(((Number) result[0]).floatValue()); // saldo (asumiendo primera columna)
            jornada.setGrupoJornada(((Number) result[1]).longValue()); // GRUPO_TURNO (asumiendo segunda columna)
            jornada.setPersonaId(persona.getId());
            jornada.setUltimo(((Number) result[2]).intValue() == 1); // OTROS (asumiendo tercera columna)

            return jornada;
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar la jornada desde el repositorio", e);
        }
    }

    /**
     * Finaliza y archiva una jornada en la base de datos.
     *
     * @param jornada La jornada a finalizar y archivar.
     */
    public void finalizarArchivarRepository(JornadaBean jornada) {
        try {
            // Calcular ventas
            String sqlVentas = SqlQueryEnum.CALCULATE_VENTAS_JORNADA.getQuery();
            Query queryVentas = entityManager.createNativeQuery(sqlVentas);
            queryVentas.setParameter(1, jornada.getGrupoJornada());
            queryVentas.setParameter(2, jornada.getPersonaId());
            Object firstVentas = queryVentas.getResultList().stream().findFirst().orElse(null);
            Object[] resultVentas = (firstVentas instanceof Object[]) ? (Object[]) firstVentas : null;

            if (resultVentas != null) {
                jornada.setCantidadVentas(((Number) resultVentas[0]).intValue()); // cantidad
                jornada.setTotalVentas(resultVentas[1] != null ? ((Number) resultVentas[1]).floatValue() : 0); // total
            }

            // Crear JSON para atributos
            JsonObject json = new JsonObject();
            json.addProperty("cantidad", jornada.getCantidadVentas());
            json.addProperty("total", jornada.getTotalVentas());

            // Actualizar jornada
            String sqlUpdate = SqlQueryEnum.UPDATE_JORNADA.getQuery();
            Query queryUpdate = entityManager.createNativeQuery(sqlUpdate);
            queryUpdate.setParameter(1, new Timestamp(jornada.getFechaFinal().getTime()));
            queryUpdate.setParameter(2, json.toString());
            queryUpdate.setParameter(3, jornada.getPersonaId());
            queryUpdate.executeUpdate();

            // Archivar jornada
            String sqlArchive = SqlQueryEnum.ARCHIVE_JORNADA.getQuery();
            Query queryArchive = entityManager.createNativeQuery(sqlArchive);
            queryArchive.setParameter(1, jornada.getPersonaId());
            queryArchive.executeUpdate();

            // Eliminar jornada
            String sqlDelete = SqlQueryEnum.DELETE_JORNADA.getQuery();
            Query queryDelete = entityManager.createNativeQuery(sqlDelete);
            queryDelete.setParameter(1, jornada.getPersonaId());
            queryDelete.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error al finalizar y archivar la jornada desde el repositorio", e);
        }
    }

    /**
     * Busca todas las personas en la base de datos.
     *
     * @param searchInactives Indica si se deben incluir personas inactivas.
     * @return JsonArray con los datos de las personas, o null si no se encuentran.
     */
    public JsonArray searchAllPersonsRepository(boolean searchInactives) {
        try {
            String condition = searchInactives ? "" : "AND estado IN ('A')";
            String sql = String.format(SqlQueryEnum.SEARCH_ALL_PERSONS.getQuery(), condition);
            Query query = entityManager.createNativeQuery(sql);
            Object result = query.getSingleResult();

            String jsonString = result instanceof org.postgresql.util.PGobject ? 
                ((org.postgresql.util.PGobject) result).getValue() : 
                (String) result;
            JsonArray data = new Gson().fromJson(jsonString, JsonArray.class);
            return data.size() > 0 ? data : null;
        } catch (JsonSyntaxException e) {
            throw new RuntimeException("Error al parsear el JSON de personas", e);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar todas las personas desde el repositorio", e);
        }
    }

    /**
     * Obtiene todas las jornadas desde la base de datos.
     *
     * @return ArrayList de PersonaBean con los datos de las jornadas.
     */
    public ArrayList<PersonaBean> getAllJornadasRepository() {
        ArrayList<PersonaBean> personas = new ArrayList<>();
        try {
            String sql = SqlQueryEnum.GET_ALL_JORNADAS.getQuery();
            Query query = entityManager.createNativeQuery(sql);
            List<Object[]> results = query.getResultList();

            int i = 0;
            for (Object[] result : results) {
                PersonaBean persona = new PersonaBean();
                persona.setId(((Number) result[0]).longValue()); // PERSONAS_ID
                persona.setNombre((String) result[1]); // NOMBRES
                persona.setApellidos((String) result[2]); // APELLIDOS
                persona.setIdentificacion((String) result[3]); // IDENTIFICACION
                persona.setGrupoJornadaId(((Number) result[4]).longValue()); // GRUPO_TURNO
                persona.setFecha((Timestamp) result[5]); // FECHA_INICIO
                if (i == 0) {
                    persona.setPrincipal(true);
                }
                i++;
                personas.add(persona);
            }

            return personas;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener las jornadas desde el repositorio", e);
        }
    }

    /**
     * Busca un administrador en la base de datos registry por usuario y PIN.
     *
     * @param us  El nombre de usuario.
     * @param psw El PIN del usuario.
     * @return int con el ID del tipo de perfil del administrador, o -1 si no se encuentra.
     */
    public int findAdminRegistryRepository(String us, String psw) {
        try {
            String sql = SqlQueryEnum.FIND_ADMIN_REGISTRY.getQuery();
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, us);
            query.setParameter(2, Integer.parseInt(psw));
            Object first = query.getResultList().stream().findFirst().orElse(null);
            Object result = (first instanceof Object[]) ? ((Object[]) first)[0] : first;
            return result instanceof Number ? ((Number) result).intValue() : -1;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar el administrador en registry desde el repositorio", e);
        }
    }

    /**
     * Busca un administrador en la base de datos core por usuario y contraseña.
     *
     * @param us  El nombre de usuario.
     * @param psw La contraseña del usuario.
     * @return int con el ID del tipo de perfil del administrador, o -1 si no se encuentra.
     */
    public int findAdminCoreRepository(String us, String psw) {
        try {
            String sql = SqlQueryEnum.FIND_ADMIN_CORE.getQuery();
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, us);
            query.setParameter(2, psw);
            Object first = query.getResultList().stream().findFirst().orElse(null);
            Object result = (first instanceof Object[]) ? ((Object[]) first)[0] : first;
            return result instanceof Number ? ((Number) result).intValue() : -1;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar el administrador en core desde el repositorio", e);
        }
    }

    /**
     * Obtiene el nombre de un promotor por su ID.
     * Migración JPA del método nombrePromotor() de RumboDao.
     *
     * @param id El ID del promotor.
     * @return String con el nombre del promotor, o cadena vacía si no se encuentra.
     */
    public String obtenerNombrePromotor(long id) {
        try {
            String sql = SqlQueryEnum.OBTENER_NOMBRE_PROMOTOR.getQuery();
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, id);
            Object result = query.getResultList().stream().findFirst().orElse(null);
            return result != null ? result.toString() : "";
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo nombre del promotor: " + e.getMessage());
            return "";
        }
    }
}