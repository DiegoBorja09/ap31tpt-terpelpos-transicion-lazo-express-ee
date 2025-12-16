package com.dao;

import com.bean.CredencialBean;
import com.bean.JornadaBean;
import com.bean.ModulosBean;
import com.bean.MovimientosBean;
import com.bean.PersonaBean;
import com.bean.ProductoBean;
import com.controllers.NovusUtils;
import com.firefuel.Main;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.postgresql.util.PSQLException;

public class PersonasDao {
    //refactorizado en un UseCase: package com.application.useCases.persons.FindAdminUseCase;
    /*
    public int findAdmin(String us, String psw, boolean adminDefaultPos) throws DAOException {
        Connection conexionRegistry = Main.obtenerConexion("lazoexpressregistry");
        Connection conexionCore = Main.obtenerConexion("lazoexpresscore");
        int id = -1;
        try {
            String sql = "select\n"
                    .concat(" pf.tipo as id")
                    .concat(" from")
                    .concat(" usuarios u")
                    .concat(" inner join personas p on")
                    .concat(" u.personas_id = p.id")
                    .concat(" inner join perfiles pf on")
                    .concat(" p.perfiles_id = pf.id")
                    .concat(" where")
                    .concat(" u.estado in('A')")
                    .concat(" and u.usuario = ?")
                    .concat(" and pin = ?")
                    .concat(" limit 1 ;");
            ResultSet re;
            PreparedStatement ps = conexionRegistry.prepareStatement(sql);
            ps.setString(1, us);
            ps.setInt(2, Integer.parseInt(psw));
            re = ps.executeQuery();
            if (re.next()) {
                id = re.getInt("id");
            }
        } catch (SQLException e) {
            NovusUtils.printLn(e.getMessage());
        }
        if (id == -1 && adminDefaultPos) {
            try {
                String sql = "select\n"
                        .concat(" pf.tipo as id")
                        .concat(" from")
                        .concat(" wacher_api_usuarios wau")
                        .concat(" inner join personas p on")
                        .concat(" wau.persona_id = p.id")
                        .concat(" inner join perfiles pf on")
                        .concat(" p.perfiles_id = pf.id")
                        .concat(" where\n")
                        .concat(" wau.usuario = ?")
                        .concat(" and wau.clave = md5(?)")
                        .concat(" and p.perfiles_id != 8")
                        .concat(" limit 1;");
                ResultSet re;
                PreparedStatement ps = conexionCore.prepareStatement(sql);
                ps.setString(1, us);
                ps.setString(2, psw);
                re = ps.executeQuery();
                if (re.next()) {
                    id = re.getInt("id");
                }
            } catch (SQLException e) {
                NovusUtils.printLn(e.getMessage());
            }
        }
        return id;
    }
     */
    // esta función no está en uso - cmceledon
    /*
    public PersonaBean findPersonaConsumoPropio(String identificacion, int pin, boolean fromRFID) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        PersonaBean persona = null;

        try {
            ResultSet re;
            if (!fromRFID) {
                String sql = "SELECT P.ID, P.IDENTIFICACION, U.PIN, P.NOMBRES, P.APELLIDOS,P.perfiles_id  FROM PERSONAS P \n"
                        + "INNER JOIN USUARIOS U ON U.PERSONAS_ID=P.ID\n"
                        + "WHERE P.ESTADO='A' and p.identificacion = ? and u.pin = ? LIMIT 1";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setString(1, identificacion);
                ps.setInt(2, pin);

                re = ps.executeQuery();
            } else {
                String sql = "SELECT P.ID, P.IDENTIFICACION, U.PIN, P.NOMBRES, P.APELLIDOS,P.perfiles_id  \n"
                        + "FROM PERSONAS P \n" + "left JOIN IDENTIFICADORES ID ON ID.ENTIDAD_ID=P.ID\n"
                        + "LEFT JOIN USUARIOS U ON U.PERSONAS_ID=P.ID\n"
                        + "WHERE IDENTIFICADOR=? AND ORIGEN=5 AND ID.ESTADO='A' LIMIT 1";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setString(1, identificacion);
                re = ps.executeQuery();
            }
            while (re.next()) {
                persona = new PersonaBean();
                persona.setId(re.getLong("id"));
                persona.setNombre(re.getString("nombres"));
                persona.setApellidos(re.getString("apellidos"));
                persona.setIdentificacion(re.getString("identificacion"));
                persona.setPin(re.getInt("pin"));
                persona.setPerfil(re.getInt("perfiles_id"));
            }
        } catch (PSQLException s) {
            throw new DAOException("41." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("42." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("43." + s.getMessage());
        }
        return persona;
    }
    */
    
    // esta función no está en uso - cmceledon
    /*
    public PersonaBean findAdministradores(String identificacion, int pin, String rutaModulo) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        PersonaBean persona = null;
        rutaModulo = rutaModulo.trim();
        try {

            String sql = "SELECT P.ID,\n" + "COALESCE((SELECT TRUE \n" + "FROM permisos_post pp \n"
                    + "inner join modulos m on m.id = pp.modulos_id \n"
                    + "where pp.personas_id = P.id and   m.atributos::json->>'url' = ? ) , false) permiso_modulo \n"
                    + "FROM PERSONAS P \n" + "INNER JOIN USUARIOS U ON U.PERSONAS_ID=P.ID \n"
                    + "INNER JOIN PERFILES PER ON PER.ID = P.PERFILES_ID \n"
                    + "WHERE P.ESTADO='A' and p.identificacion = ? and u.pin = ?  LIMIT 1";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, rutaModulo);
            ps.setString(2, identificacion);
            ps.setInt(3, pin);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                if (re.getBoolean("permiso_modulo")) {
                    persona = this.findPersonaById(re.getLong("id"));
                }
                break;
            }
        } catch (PSQLException s) {
            NovusUtils.printLn(s.getMessage());
        } catch (SQLException s) {
            NovusUtils.printLn(s.getMessage());
        } catch (Exception s) {
            NovusUtils.printLn(s.getMessage());
        }
        return persona;

    }
    */
    //refactorizado en un UseCase: package com.application.useCases.persons.FindPersonaUseCase;
    /*
    public PersonaBean findPersona(String identificacion, int pin, boolean fromRFID) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        PersonaBean persona = null;

        try {
            ResultSet re;
            if (!fromRFID) {
                String sql = "SELECT P.ID, P.IDENTIFICACION, U.PIN, P.NOMBRES, P.APELLIDOS FROM PERSONAS P \n"
                        + "INNER JOIN USUARIOS U ON U.PERSONAS_ID=P.ID\n"
                        + "WHERE P.ESTADO='A' and p.identificacion = ? and u.pin = ? LIMIT 1";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setString(1, identificacion);
                ps.setInt(2, pin);

                re = ps.executeQuery();
            } else {
                String sql = "SELECT P.ID, P.IDENTIFICACION, U.PIN, P.NOMBRES, P.APELLIDOS \n"
                        + "FROM PERSONAS P \n" + "left JOIN IDENTIFICADORES ID ON ID.ENTIDAD_ID=P.ID\n"
                        + "LEFT JOIN USUARIOS U ON U.PERSONAS_ID=P.ID\n"
                        + "WHERE IDENTIFICADOR=? AND ORIGEN=5 AND ID.ESTADO='A' LIMIT 1";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setString(1, identificacion);
                re = ps.executeQuery();
            }

            while (re.next()) {
                persona = new PersonaBean();
                persona.setId(re.getLong("id"));
                persona.setNombre(re.getString("nombres"));
                persona.setApellidos(re.getString("apellidos"));
                persona.setIdentificacion(re.getString("identificacion"));
                persona.setPin(re.getInt("pin"));
            }

            if (persona != null) {
                String sql = "SELECT * FROM PERMISOS_POST WHERE PERSONAS_ID=?";
                PreparedStatement ps = conexion.prepareStatement(sql);

                ps.setLong(1, persona.getId());
                re = ps.executeQuery();
                int i = 0;
                while (re.next()) {
                    if (i == 0) {
                        persona.setModulos(new ArrayList<>());
                    }
                    ModulosBean m = new ModulosBean();
                    m.setId(re.getInt("modulos_id"));
                    m.setDescripcion(re.getString("modulos_descripcion"));
                    persona.getModulos().add(m);
                    i++;
                }
            }
        } catch (PSQLException s) {
            throw new DAOException("41." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("42." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("43." + s.getMessage());
        }
        return persona;
    }
     */
    // esta función no está en uso - cmceledon
    /*
    public PersonaBean findPersonaByCedula(String identificacion) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        PersonaBean persona = null;

        try {
            ResultSet re;

            String sql = "SELECT P.ID, P.IDENTIFICACION, P.NOMBRES, P.APELLIDOS, P.EMPRESAS_ID FROM PERSONAS P \n"
                    + "WHERE P.ESTADO='A' and p.identificacion = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);

            ps.setString(1, identificacion);
            re = ps.executeQuery();

            while (re.next()) {
                persona = new PersonaBean();
                persona.setId(re.getLong("id"));
                persona.setNombre(re.getString("nombres"));
                persona.setApellidos(re.getString("apellidos"));
                persona.setIdentificacion(re.getString("identificacion"));
                persona.setEmpresaId(re.getLong("empresas_id"));
            }

        } catch (PSQLException s) {
            throw new DAOException("41." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("42." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("43." + s.getMessage());
        }
        return persona;
    }
    */
    // esta función no está en uso - cmceledon
    /*
    public boolean tagExiste(String tag) {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        boolean tagExist = false;
        try {
            String sql = "SELECT * FROM IDENTIFICADORES WHERE IDENTIFICADOR=? and ORIGEN=5";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, tag);
            tagExist = ps.executeQuery().next();
        } catch (SQLException ex) {
            Logger.getLogger(PersonasDao.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return tagExist;
    }
    */
    //refactorizado en un UseCase: package com.application.useCases.persons.RegistrarTagUseCase;
    /*
    public long registrarTag(String identificacion, String tag) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        long idPersona = 0;
        try {

            ResultSet re;
            String sql = "UPDATE PERSONAS SET TAG=NULL WHERE TAG=?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, tag);
            ps.executeUpdate();

            sql = "UPDATE PERSONAS SET TAG=? WHERE IDENTIFICACION=? RETURNING ID";
            ps = conexion.prepareStatement(sql);
            ps.setString(1, tag);
            ps.setString(2, identificacion);
            re = ps.executeQuery();
            if (re.next()) {
                idPersona = re.getLong("ID");
            }

        } catch (SQLException ex) {
            Logger.getLogger(PersonasDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return idPersona;
    }
    */
    //refactorizado en un UseCase: package com.application.useCases.persons.FindPersonaByIdUseCase;
    /*
    public PersonaBean findPersonaById(long idPersona) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        PersonaBean persona = null;

        try {
            ResultSet re;
            String sql = "SELECT P.ID, P.IDENTIFICACION, P.NOMBRES, P.APELLIDOS,U.PIN as PIN, P.EMPRESAS_ID ,\n"
                    + "COALESCE((SELECT array_to_json(array_agg(row_to_json(t))) FROM (\n"
                    + "SELECT pp.*, m.atributos::json->>'url' link FROM permisos_post pp \n"
                    + "inner join modulos m on m.id = pp.modulos_id \n"
                    + "where pp.personas_id = P.id \n" + ") t) ,'[]') modulos\n"
                    + "FROM PERSONAS P \n" + "INNER JOIN  USUARIOS U ON U.PERSONAS_ID=P.ID \n"
                    + "WHERE P.ESTADO='A' and p.id = ? ";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, idPersona);
            re = ps.executeQuery();

            while (re.next()) {
                persona = new PersonaBean();
                persona.setId(re.getLong("id"));
                persona.setNombre(re.getString("nombres"));
                persona.setApellidos(re.getString("apellidos"));
                persona.setIdentificacion(re.getString("identificacion"));
                persona.setPin(re.getInt("PIN"));
                persona.setEmpresaId(re.getLong("empresas_id"));
                ArrayList<ModulosBean> modulosPersona = new ArrayList<>();
                try {
                    JsonArray jsonModulosArray = new Gson().fromJson(re.getString("modulos"), JsonArray.class);
                    for (JsonElement moduloElement : jsonModulosArray) {
                        JsonObject jsonModulo = moduloElement.getAsJsonObject();
                        ModulosBean modulo = new ModulosBean();
                        modulo.setId(jsonModulo.get("modulos_id").getAsInt());
                        modulo.setDescripcion(jsonModulo.get("modulos_descripcion").getAsString());
                        modulo.setLink(jsonModulo.get("link").getAsString());
                        modulosPersona.add(modulo);
                    }
                } catch (JsonSyntaxException | SQLException e) {
                    NovusUtils.printLn(e + "ERROR");
                    Logger.getLogger(PersonasDao.class.getName()).log(Level.SEVERE, null, e);

                }
                persona.setModulos(modulosPersona);
            }

        } catch (PSQLException s) {
            NovusUtils.printLn(s.getMessage() + "ERROR");
            Logger.getLogger(PersonasDao.class.getName()).log(Level.SEVERE, null, s);
        } catch (SQLException s) {
            Logger.getLogger(PersonasDao.class.getName()).log(Level.SEVERE, null, s);
            NovusUtils.printLn(s.getMessage() + "ERROR");
        } catch (Exception s) {
            NovusUtils.printLn(s.getMessage() + "ERROR");
            Logger.getLogger(PersonasDao.class.getName()).log(Level.SEVERE, null, s);
        }
        return persona;
    }
     */
    // esta función no está en uso - cmceledon
    /*
    public JornadaBean iniciaJornada(PersonaBean persona, float saldo, CredencialBean credencial) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        JornadaBean jornada = new JornadaBean();
        jornada.setPersonaId(persona.getId());
        jornada.setSaldo(saldo);

        try {
            String sql = "SELECT COALESCE((SELECT MAX(GRUPO_TURNO) GRUPO_TURNO FROM JORNADAS LIMIT 1), 0) AS GRUPO_TURNO";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            if (re.next()) {
                jornada.setGrupoJornada(re.getLong("GRUPO_TURNO"));
            }
            if (jornada.getGrupoJornada() == 0) {
                sql = "INSERT INTO jornadas(\n"
                        + "            fecha_inicio, fecha_fin, equipos_id, personas_id, sincronizado, grupo_turno, saldo)\n"
                        + "    VALUES (?, null, ?, ?, 0, nextval('grupo_jornada_id'), ?) RETURNING currval('grupo_jornada_id')";
                ps = conexion.prepareStatement(sql);
                ps.setTimestamp(1, new Timestamp(new Date().getTime()));
                ps.setLong(2, credencial.getId());
                ps.setLong(3, persona.getId());
                ps.setFloat(4, saldo);
                re = ps.executeQuery();
                if (re.next()) {
                    jornada.setGrupoJornada(re.getLong("currval"));
                }
                jornada.setIniciada(true);
            } else {
                sql = "SELECT * FROM JORNADAS WHERE PERSONAS_ID=?";
                ps = conexion.prepareStatement(sql);
                ps.setLong(1, persona.getId());
                re = ps.executeQuery();
                if (re.next()) {
                    jornada.setIniciada(true);
                    jornada.setSaldo(re.getFloat("saldo"));
                }
                if (!jornada.isIniciada()) {
                    sql = "INSERT INTO jornadas(\n"
                            + "            fecha_inicio, fecha_fin, equipos_id, personas_id, sincronizado, grupo_turno, saldo)\n"
                            + "    VALUES (?, null, ?, ?, 0, ?, ?);";
                    ps = conexion.prepareStatement(sql);
                    ps.setTimestamp(1, new Timestamp(new Date().getTime()));
                    ps.setLong(2, credencial.getId());
                    ps.setLong(3, persona.getId());
                    ps.setLong(4, jornada.getGrupoJornada());
                    ps.setFloat(5, 0);
                    ps.executeUpdate();
                }
            }
        } catch (PSQLException s) {
            throw new DAOException("41." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("42." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("43." + s.getMessage());
        }
        return jornada;
    }
    */
    // esta función no está en uso - cmceledon
    /*
    public void guardarInventariosIniciales(JornadaBean jornada, CredencialBean credencial) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        try {
            String sql = "INSERT INTO jornadas_inventarios(\n"
                    + "            id, jornadas_id, grupo_jornada_id, producto_id, equipos_detalles_id, \n"
                    + "            acumulado_venta_inicial, acumulado_cantidad_inicial, acumulado_venta_final, \n"
                    + "            acumulado_cantidad_final)\n"
                    + "    VALUES (nextval('jornadas_inventarios_id'), null, ?, ?, ?, \n"
                    + "            null, ?, null, \n" + "            null);";
            PreparedStatement ps = conexion.prepareStatement(sql);
            for (ProductoBean inventario : jornada.getInventarios()) {
                ps.setLong(1, jornada.getGrupoJornada());
                ps.setLong(2, inventario.getId());
                ps.setLong(3, credencial.getId());
                ps.setLong(4, inventario.getContador());
                ps.executeUpdate();
            }
        } catch (PSQLException s) {
            throw new DAOException("41." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("42." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("43." + s.getMessage());
        }
    }*/
    // esta función no está en uso - cmceledon
    /*
    public void guardarInventariosFinales(JornadaBean jornada, CredencialBean credencial) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        try {
            String sql = "UPDATE public.jornadas_inventarios\n" + "   SET acumulado_cantidad_final=?\n"
                    + " WHERE grupo_jornada_id=? and producto_id=?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            for (ProductoBean inventario : jornada.getInventarios()) {
                ps.setLong(1, inventario.getContador());
                ps.setLong(2, jornada.getGrupoJornada());
                ps.setLong(3, inventario.getId());
                ps.executeUpdate();
            }
        } catch (PSQLException s) {
            throw new DAOException("41." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("42." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("43." + s.getMessage());
        }
    }
    */
    // esta función no está en uso - cmceledon
    /*
    public JornadaBean isIniciada(PersonaBean persona, CredencialBean credencial) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        JornadaBean jornada = null;
        try {
            String sql = "SELECT * FROM JORNADAs WHERE PERSONAS_ID=?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, persona.getId());
            ResultSet re = ps.executeQuery();
            if (re.next()) {
                jornada = new JornadaBean();
                jornada.setIniciada(true);
                jornada.setSaldo(re.getFloat("saldo"));
                jornada.setGrupoJornada(re.getLong("GRUPO_TURNO"));
                jornada.setPersonaId(persona.getId());
            }
        } catch (SQLException er) {

        }
        return jornada;
    }
    */
    //refactorizado en un UseCase: package com.application.useCases.persons.GetTurnoUseCase;
    /*
    public PersonaBean getTurno(long idPersona) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");

        PersonaBean persona = null;

        try {
            String sql = "select p2.id, j.id as idjornada, j.fecha_inicio, j.grupo_jornada from jornadas j\n"
                    + "inner join personas p2 on p2.id = j.personas_id  where j.personas_id = ?\n"
                    + "order by j.id asc";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, idPersona);
            ResultSet re = ps.executeQuery();

            if (re.next()) {
                persona = new PersonaBean();
                persona.setId(re.getLong("id"));
                persona.setJornadaId(re.getLong("idjornada"));
                persona.setGrupoJornadaId(re.getLong("grupo_jornada"));
                persona.setFecha(re.getTimestamp("fecha_inicio"));

            }

        } catch (PSQLException s) {
            throw new DAOException("31." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("32." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("33." + s.getMessage());
        }
        return persona;
    }
     */
    //refactorizado en un UseCase: package com.application.useCases.persons.GetPrincipalTurnoUseCase;
/*
    public PersonaBean getPrincipalTurno() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        PersonaBean persona = null;
        try {
            String sql = "select\n"
                    + "	p2.id,\n"
                    + "	j.id as idjornada,\n"
                    + "	p2.nombre as nombre,\n"
                    + "	p2.identificacion as identificacion,\n"
                    + "	ti.descripcion,\n"
                    + "	j.fecha_inicio,\n"
                    + "	j.grupo_jornada\n"
                    + "from jornadas j\n"
                    + "inner join personas p2 on\n"
                    + "	p2.id = j.personas_id\n"
                    + "inner join tipos_identificaciones ti on \n"
                    + "ti.id = p2.tipos_identificacion_id \n"
                    + "order by j.id asc limit 1;";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            if (re.next()) {
                persona = new PersonaBean();
                persona.setId(re.getLong("id"));
                persona.setNombre(re.getString("nombre"));
                persona.setIdentificacion(re.getString("identificacion"));
                persona.setTipoIdentificacionDesc(re.getString("descripcion"));
                persona.setJornadaId(re.getLong("idjornada"));
                persona.setGrupoJornadaId(re.getLong("grupo_jornada"));
                persona.setFecha(re.getTimestamp("fecha_inicio"));
            }
        } catch (PSQLException s) {
            throw new DAOException("31." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("32." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("33." + s.getMessage());
        }
        return persona;
    }
*/
    //refactorizado en un UseCase: package com.application.useCases.persons.IsIniciadaAndUltimoUseCase;
/*
    public JornadaBean isIniciadaAndUltimo(PersonaBean persona, CredencialBean credencial) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        JornadaBean jornada = null;
        try {
            String sql = "SELECT *, (SELECT COUNT(1) FROM JORNADAS) OTROS FROM JORNADAs WHERE PERSONAS_ID=?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, persona.getId());
            ResultSet re = ps.executeQuery();
            if (re.next()) {
                jornada = new JornadaBean();
                jornada.setIniciada(true);
                jornada.setSaldo(re.getFloat("saldo"));
                jornada.setGrupoJornada(re.getLong("GRUPO_TURNO"));
                jornada.setPersonaId(persona.getId());
                jornada.setUltimo(re.getInt("otros") == 1);
            }
        } catch (SQLException er) {
        }
        return jornada;
    }
*/
    //refactorizado en un UseCase: package com.application.useCases.persons.FinalizarArchivarUseCase
    /*
    public void finalizarArchivar(JornadaBean jornada) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        try {
            String sql = "select count(1) cantidad, sum(venta_total) total\n" + "from movimientos m \n" + "where \n"
                    + "operacion = 9  and grupo_jornada_id = ? and persona_id = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, jornada.getGrupoJornada());
            ps.setLong(2, jornada.getPersonaId());
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                jornada.setCantidadVentas(re.getInt(1));
                jornada.setTotalVentas(re.getFloat(2));
            }
            JsonObject json = new JsonObject();
            json.addProperty("cantidad", jornada.getCantidadVentas());
            json.addProperty("total", jornada.getTotalVentas());

            sql = "UPDATE jornadas SET fecha_fin=?, atributos=?::json WHERE personas_id=?";
            ps = conexion.prepareStatement(sql);
            ps.setTimestamp(1, new Timestamp(jornada.getFechaFinal().getTime()));
            ps.setString(2, json.toString());
            ps.setLong(3, jornada.getPersonaId());
            ps.executeUpdate();

            sql = "INSERT INTO jornadas_hist SELECT nextval('JORNADAS_HIST_ID'), J.* FROM jornadas J WHERE personas_id=?";
            ps = conexion.prepareStatement(sql);
            ps.setLong(1, jornada.getPersonaId());
            ps.executeUpdate();

            sql = "DELETE FROM jornadas WHERE personas_id=?";
            ps = conexion.prepareStatement(sql);
            ps.setLong(1, jornada.getPersonaId());
            ps.executeUpdate();

        } catch (SQLException er) {
            NovusUtils.printLn(er.getMessage());
        } catch (Exception er) {
            NovusUtils.printLn(er.getMessage());
        }
    }*/
    // esta función no está en uso - cmceledon
    /*
    public void finalizarArchivarAllD(PersonaBean p, Date date) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        try {
            String sql = "UPDATE JORNADAS SET FECHA_FIN=? WHERE PERSONAS_ID=?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setTimestamp(1, new Timestamp(date.getTime()));
            ps.setLong(2, p.getId());
            ps.executeUpdate();

            sql = "INSERT INTO JORNADAS_HIST SELECT nextval('JORNADAS_HIST_ID'), J.* FROM JORNADAS J WHERE PERSONAS_ID=?";
            ps = conexion.prepareStatement(sql);
            ps.setLong(1, p.getId());
            ps.executeUpdate();

            sql = "DELETE FROM JORNADAS WHERE PERSONAS_ID=?";
            ps = conexion.prepareStatement(sql);
            ps.setLong(1, p.getId());
            ps.executeUpdate();

        } catch (SQLException er) {
            NovusUtils.printLn(er.getMessage());
        } catch (Exception er) {
            NovusUtils.printLn(er.getMessage());
        }
    }
    */
//refactorizado en un UseCase: package com.application.useCases.persons.FinalizarArchivarUseCase
    /*
    public JsonArray searchAllPersons(boolean searchInactives) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");

        JsonArray allPersons = null;

        String sql = "select coalesce((SELECT array_to_json(array_agg(row_to_json(t))) FROM ( \n"
                + "         select  id, estado, nombre, identificacion,\n"
                + "          p.perfiles_id as perfil \n"
                + "          from personas p where p.id > 3 " + (searchInactives ? "" : " and estado in('A')") + "\n"
                + "  ) t),'[]') as persons_array";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ResultSet re = ps.executeQuery();
            while (re.next()) {
                JsonArray data = new Gson().fromJson(re.getString("persons_array"), JsonArray.class);
                allPersons = data.size() > 0 ? data : null;
                break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(SurtidorDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return allPersons;
    }
     */
    /*
     * Esta función ha sido reemplazada por el caso de uso GetVentasGoPassUseCase
     * Obtenía las ventas de GoPass desde la base de datos
     * Ahora esta lógica se maneja en GetVentasGoPassUseCase usando la consulta de la base de datos
     */
/*
    public ArrayList<MovimientosBean> getVentas(PersonaBean persona) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        ArrayList<MovimientosBean> movimientos = new ArrayList<>();

        try {
            String sql = "SELECT * FROM MOVIMIENTOS WHERE PERSONAS_ID=?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, persona.getId());
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                MovimientosBean mov = new MovimientosBean();
            }
        } catch (SQLException er) {
            NovusUtils.printLn(er.getMessage());
        } catch (Exception er) {
            NovusUtils.printLn(er.getMessage());
        }
        return movimientos;
    }
*/
    //refactorizado en un UseCase: package com.application.useCases.persons.GetAllJornadasUseCase
    /*
    public ArrayList<PersonaBean> getAllJornadas() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        ArrayList<PersonaBean> personas = new ArrayList<>();
        try {
            String sql = "SELECT P.ID PERSONAS_ID, P.NOMBRES, P.APELLIDOS, P.IDENTIFICACION, J.GRUPO_TURNO, J.FECHA_INICIO FROM \n"
                    + "JORNADAS J \n" + "INNER JOIN PERSONAS P ON P.ID=J.PERSONAS_ID ORDER BY J.ID";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            int i = 0;
            while (re.next()) {
                PersonaBean persona = new PersonaBean();
                persona.setId(re.getLong("PERSONAS_ID"));
                persona.setNombre(re.getString("NOMBRES"));
                persona.setApellidos(re.getString("APELLIDOS"));
                persona.setIdentificacion(re.getString("IDENTIFICACION"));
                persona.setGrupoJornadaId(re.getLong("GRUPO_TURNO"));
                persona.setFecha(re.getTimestamp("FECHA_INICIO"));
                if (i == 0) {
                    persona.setPrincipal(true);
                }
                i++;
                personas.add(persona);
            }
        } catch (SQLException er) {
            NovusUtils.printLn(er.getMessage());
        } catch (Exception er) {
            NovusUtils.printLn(er.getMessage());
        }
        return personas;
    }
     */
    // esta función no está en uso - cmceledon
    /*
    public ArrayList<PersonaBean> getAllUsuarios() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        ArrayList<PersonaBean> personas = new ArrayList<>();

        try {
            String sql = "SELECT P.ID PERSONAS_ID, P.NOMBRES, P.APELLIDOS, P.IDENTIFICACION, P.ESTADO, P.TELEFONO, P.DIRECCION, P.PERFILES_ID "
                    + "FROM \n" + "PERSONAS P";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                PersonaBean persona = new PersonaBean();
                persona.setId(re.getLong("PERSONAS_ID"));
                persona.setPerfil(re.getInt("PERFILES_ID"));
                persona.setNombre(re.getString("NOMBRES"));
                persona.setApellidos(re.getString("APELLIDOS"));
                persona.setIdentificacion(re.getString("IDENTIFICACION"));
                persona.setEstado(re.getString("ESTADO"));
                persona.setTelefono(re.getString("TELEFONO"));
                persona.setDireccion(re.getString("DIRECCION"));

                personas.add(persona);
            }
        } catch (SQLException er) {
            NovusUtils.printLn(er.getMessage());
        } catch (Exception er) {
            NovusUtils.printLn(er.getMessage());
        }
        return personas;
    }
    */
    //refactorizado en un UseCase: package com.application.useCases.persons.GetAllUsuariosCoreUseCase;
    /*
    public ArrayList<PersonaBean> getAllUsuariosCore() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");

        ArrayList<PersonaBean> personas = new ArrayList<>();

        try {
            String sql = "SELECT P.ID PERSONAS_ID, P.NOMBRE, P.IDENTIFICACION, P.ESTADO, P.TELEFONO, P.DIRECCION, P.PERFILES_ID, P.TAG "
                    + "FROM \n"
                    + "PERSONAS P WHERE P.IDENTIFICACION <> '2222222' OR P.NOMBRE NOT IN('CLIENTES','VARIOS') and perfiles_id is not null";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                PersonaBean persona = new PersonaBean();
                persona.setId(re.getLong("PERSONAS_ID"));
                persona.setPerfil(re.getInt("PERFILES_ID"));
                persona.setNombre(re.getString("NOMBRE"));
                persona.setIdentificacion(re.getString("IDENTIFICACION"));
                persona.setEstado(re.getString("ESTADO"));
                persona.setTelefono(re.getString("TELEFONO"));
                persona.setDireccion(re.getString("DIRECCION"));
                persona.setTag(re.getString("TAG"));
                personas.add(persona);
            }
        } catch (SQLException er) {
            NovusUtils.printLn(er.getMessage());
        } catch (Exception er) {
            NovusUtils.printLn(er.getMessage());
        }
        return personas;
    }
    */
    //refactorizado en un UseCase: package com.application.useCases.persons.GetAllJornadasHistoryUseCase;
    /*
    public ArrayList<PersonaBean> getAllJornadasHistory() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        ArrayList<PersonaBean> personas = new ArrayList<>();
        try {
            String sql = "SELECT J.ID JID, P.ID PERSONAS_ID, P.NOMBRES, P.APELLIDOS, P.IDENTIFICACION, J.GRUPO_TURNO, J.FECHA_INICIO, J.FECHA_FIN, ATRIBUTOS \n"
                    + "FROM JORNADAS_HIST J \n" + "INNER JOIN PERSONAS P ON P.ID=J.PERSONAS_ID ORDER BY J.ID";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            int i = 0;
            while (re.next()) {
                PersonaBean persona = new PersonaBean();
                persona.setId(re.getLong("PERSONAS_ID"));
                persona.setNombre(re.getString("NOMBRES"));
                persona.setApellidos(re.getString("APELLIDOS"));
                persona.setIdentificacion(re.getString("IDENTIFICACION"));
                persona.setGrupoJornadaId(re.getLong("GRUPO_TURNO"));
                persona.setJornadaId(re.getLong("JID"));
                persona.setFechaInicio(re.getTimestamp("FECHA_INICIO"));
                persona.setFechaFin(re.getTimestamp("FECHA_FIN"));

                JsonObject jsonObject = new Gson().fromJson(re.getString("atributos"), JsonObject.class);
                persona.setAtributos(jsonObject);

                if (i == 0) {
                    persona.setPrincipal(true);
                }
                i++;
                personas.add(persona);
            }
        } catch (SQLException | JsonSyntaxException er) {
            NovusUtils.printLn(er.getMessage());
        }
        return personas;
    }
     */
    // esta función no está en uso - cmceledon
    /*
    public ArrayList<PersonaBean> getAllPersonas() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        ArrayList<PersonaBean> personas = new ArrayList<>();
        try {
            String sql = "SELECT P.ID PERSONAS_ID, P.NOMBRES, P.APELLIDOS, P.IDENTIFICACION, P.ESTADO, I.IDENTIFICADOR, I.origen \n"
                    + "FROM personas p \n" + "INNER JOIN USUARIOS U        ON U.PERSONAS_ID=P.ID\n"
                    + "LEFT JOIN IDENTIFICADORES I ON P.ID=I.ENTIDAD_ID and ORIGEN=5\n" + "where P.ESTADO='A'";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                PersonaBean persona = new PersonaBean();
                persona.setId(re.getLong("PERSONAS_ID"));
                persona.setNombre(re.getString("NOMBRES"));
                persona.setApellidos(re.getString("APELLIDOS"));
                persona.setIdentificacion(re.getString("IDENTIFICACION"));
                persona.setEstado(re.getString("ESTADO"));
                persona.setIdentificador(re.getString("IDENTIFICADOR"));
                personas.add(persona);
            }
        } catch (SQLException er) {
            NovusUtils.printLn(er.getMessage());
        } catch (Exception er) {
            NovusUtils.printLn(er.getMessage());
        }
        return personas;
    }
    */
    //refactorizado en un UseCase: package com.application.useCases.persons.GetAllPromotoresUseCase;
   /*
    public ArrayList<PersonaBean> getAllPromotores() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        ArrayList<PersonaBean> personas = new ArrayList<>();
        try {
            String sql = "SELECT P.ID PERSONAS_ID, P.NOMBRES, P.APELLIDOS, P.IDENTIFICACION FROM PERSONAS P WHERE ESTADO='A' AND PERFILES_ID <> 4 and ID>2";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                PersonaBean persona = new PersonaBean();
                persona.setId(re.getLong("PERSONAS_ID"));
                persona.setNombre(re.getString("NOMBRES"));
                persona.setApellidos(re.getString("APELLIDOS"));
                persona.setIdentificacion(re.getString("IDENTIFICACION"));
                personas.add(persona);
            }
        } catch (SQLException er) {
            NovusUtils.printLn(er.getMessage());
        } catch (Exception er) {
            NovusUtils.printLn(er.getMessage());
        }
        return personas;
    }
    */
    //refactorizado en un UseCase: package com.application.useCases.persons.ObtenerLecturaTagUseCase;
    /*
    public String obtenerLecturaTag() {
        String lectura = null;
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "select * from lecturas_tag limit 1";
        try {
            Statement smt = conexion.createStatement();
            ResultSet rs = smt.executeQuery(sql);
            if (rs.next()) {
                lectura = rs.getString("lectura");
            }
        } catch (SQLException e) {
            NovusUtils.printLn(e.getMessage());
        }
        return lectura;
    }
    */
    //refactorizado en un UseCase: package com.application.useCases.persons.ObtenerUsuarioUseCase;
    /*
    public PersonaBean obtenerUsuario(String lectura) {
        PersonaBean personas = new PersonaBean();
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        String sql = "SELECT P.ID, P.IDENTIFICACION, U.PIN, P.NOMBRES, P.APELLIDOS \n"
                + "FROM PERSONAS P \n" + "left JOIN IDENTIFICADORES ID ON ID.ENTIDAD_ID=P.ID\n"
                + "LEFT JOIN USUARIOS U ON U.PERSONAS_ID=P.ID\n"
                + "WHERE IDENTIFICADOR=? AND ORIGEN=5 AND ID.ESTADO='A' LIMIT 1";
        try {
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, lectura);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                personas.setId(rs.getLong("id"));
                personas.setIdentificacion(rs.getString("identificacion"));
                personas.setPin(rs.getInt("pin"));
                personas.setNombre(rs.getString("nombres"));
                personas.setApellidos(rs.getString("apellidos"));
            } else {
                personas = null;
            }
        } catch (SQLException e) {
            NovusUtils.printLn(e.getMessage());
        }
        return personas;
    }
*/
    //refactorizado en un UseCase: package com.application.useCases.persons.EliminarLecturaUseCase;
    /*
    public void eliminarLectura() {
        String sql = "delete from lecturas_tag";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try {
            Statement smt = conexion.createStatement();
            smt.executeUpdate(sql);
        } catch (SQLException ex) {
            NovusUtils.printLn(ex.getMessage());
        }
    }
     */
//refactorizado en un UseCase: package com.application.useCases.persons.IsAdminUseCase;
    /*
    public boolean isAdmin(long id) {
        long idPerfil = 0;
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        String sql = "select pf.tipo as id from personas p inner join perfiles pf on p.perfiles_id = pf.id where p.id = ?;";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                idPerfil = rs.getLong("id");
            }
        } catch (SQLException e) {
            NovusUtils.printLn(e.getMessage());
        }
        return idPerfil == 5;
    }
    */
}
