/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dao;

import com.application.useCases.equipos.CierreDeTurnoUseCase;
import com.application.useCases.equipos.CierreDeTurnoConDatafonoUseCase;
import com.bean.BodegaBean;
import com.bean.ConsecutivoBean;
import com.bean.ContactoBean;
import com.bean.CredencialBean;
import com.bean.DispositivosBean;
import com.bean.EmpresaBean;
import com.bean.EquiposAutorizacionBean;
import com.bean.MediosPagosBean;
import com.bean.ModulosBean;
import com.bean.MovimientosBean;
import com.bean.PerfilesBean;
import com.bean.ProductoBean;
import com.bean.Surtidor;
import com.bean.TransmisionBean;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.firefuel.Main;
import static com.firefuel.Main.dbCore;
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
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.postgresql.util.PSQLException;

/**
 *
 * @author novus
 */
public class EquipoDao {

    /*public String getMacEquipo(long equipoId) {
        String mac = "NO TIENE";
        try {
            Connection conexion = Main.obtenerConexion("lazoexpressregistry");
            if (conexion != null) {
                String sql = "SELECT MAC FROM EQUIPOS WHERE ID= ?;";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, equipoId);
                ResultSet re = ps.executeQuery();
                if (re.next()) {
                    mac = re.getString("MAC");
                }
            }
        } catch (SQLException e) {
        }
        return mac;
    }*/

   /* public JsonObject getTurnoHorario(long id) {
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

    /*public boolean isAutorized(long equipoId) {
        boolean autorizado = false;
        try {
            Connection conexion = Main.obtenerConexion("lazoexpresscore");
            String sql = "SELECT autorizado FROM EQUIPOS WHERE ID= ?;";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, equipoId);
            ResultSet re = ps.executeQuery();
            if (re.next()) {
                autorizado = re.getString("autorizado").equals("S");
            }
        } catch (SQLException e) {
        }

        return autorizado;
    }*/

    /*public boolean isValidCOM(String conector) {
        boolean result;
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try {
            String sql = "select id from dispositivos where conector = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, conector);
            ResultSet rs = ps.executeQuery();
            result = rs.next();
        } catch (SQLException e) {
            result = false;
            System.out.println(e.getMessage());
        }
        return result;
    }*/

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

    /*public void eliminarDispositivo(int id) {
        try {

            Connection conexion = Main.obtenerConexion("lazoexpresscore");
            String sql = "delete from dispositivos where id=?;";
            PreparedStatement ps;
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/

   /* public void editarDispositivo(int id, String tipos, String conector, String interfaz, String estado, JsonObject d_atributos) {
        try {

            Connection conexion = Main.obtenerConexion("lazoexpresscore");
            String sql = "update dispositivos set id=?, tipos=?, conector=?, interfaz=?, estado=?, d_atributos=(?)::json where id=?";
            PreparedStatement ps;
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, id);
            ps.setString(2, tipos);
            ps.setString(3, conector);
            ps.setString(4, interfaz);
            ps.setString(5, estado);
            ps.setString(6, d_atributos.toString());
            ps.setInt(7, id);
            ps.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    } */

    public CredencialBean createUpdateCredencial(CredencialBean bean) throws DAOException {
        String databases[] = {"lazoexpressregistry", "lazoexpresscore"};
        for (String database : databases) {
            try {
                Connection conexion = Main.obtenerConexion(database);
                String sql = "SELECT * FROM EQUIPOS";
                PreparedStatement ps = conexion.prepareStatement(sql);
                boolean existe = ps.executeQuery().next();
                if (!existe) {
                    sql = "INSERT INTO equipos (\n"
                            + "            id, empresas_id, serial_equipo, almacenamientos_id, estado, equipos_tipos_id, \n"
                            + "            equipos_protocolos_id, mac, ip, port, create_user, create_date, \n"
                            + "            update_user, update_date, token, password, factor_precio, factor_importe, \n"
                            + "            factor_inventario, lector_ip, lector_port, impresora_ip, impresora_port, \n"
                            + "            url_foto, autorizado)\n" + "VALUES (?, ?, ?, NULL, ?, NULL, \n"
                            + "            NULL, ?, NULL, NULL, ?, NOW(), \n"
                            + "            NULL, NULL, ?, ?, NULL, NULL, \n"
                            + "            NULL, NULL, NULL, NULL, NULL, \n" + " NULL, ?) RETURNING ID";

                    ps = conexion.prepareStatement(sql);
                    if (bean.getEquipos_id() == null) {
                        ps.setLong(1, 1);
                    } else {
                        ps.setLong(1, bean.getEquipos_id());
                    }
                    if (bean.getEmpresas_id() != null && bean.getEmpresas_id() != 0) {
                        ps.setLong(2, bean.getEmpresas_id());
                    } else {
                        ps.setNull(2, Types.NULL);
                    }
                    ps.setString(3, bean.getReferencia() == null ? "*" : bean.getReferencia());
                    ps.setString(4, NovusConstante.ACTIVE);
                    ps.setString(5, bean.getMac());
                    ps.setLong(6, 1);
                    ps.setString(7, bean.getToken() != null ? bean.getToken() : "");
                    ps.setString(8, bean.getPassword() != null ? bean.getPassword() : "");
                    ps.setString(9, bean.isAutorizado() ? NovusConstante.SI : NovusConstante.NO);
                    ResultSet re = ps.executeQuery();
                    while (re.next()) {
                        bean.setEquipos_id(re.getLong("id"));
                    }
                } else if (bean.getEquipos_id() == 1) {
                    sql = "UPDATE EQUIPOS SET id=?, token=?, password=?, serial_equipo=? WHERE ID=1";
                    ps = conexion.prepareStatement(sql);
                    ps.setLong(1, bean.getEquipos_id());
                    ps.setString(2, bean.getToken() != null ? bean.getToken() : "");
                    ps.setString(3, bean.getPassword() != null ? bean.getPassword() : "");
                    ps.setString(4, bean.getReferencia() == null ? "*" : bean.getReferencia());
                    ps.executeUpdate();
                } else if (bean.getEquipos_id() > 1) {
                    sql = "UPDATE EQUIPOS SET id=?, token=?, password=?, serial_equipo=?, autorizado=?, empresas_id=?";
                    ps = conexion.prepareStatement(sql);
                    ps.setLong(1, bean.getEquipos_id());
                    ps.setString(2, bean.getToken() != null ? bean.getToken() : "");
                    ps.setString(3, bean.getPassword() != null ? bean.getPassword() : "");
                    ps.setString(4, bean.getReferencia() == null ? "*" : bean.getReferencia());
                    ps.setString(5, bean.isAutorizado() ? NovusConstante.SI : NovusConstante.NO);
                    if (bean.getEmpresas_id() != 0) {
                        ps.setLong(6, bean.getEmpresas_id());
                    } else {
                        ps.setNull(6, Types.NULL);
                    }
                    ps.executeUpdate();
                }
            } catch (PSQLException s) {
                throw new DAOException("11." + s.getMessage());
            } catch (SQLException s) {
                throw new DAOException("12." + s.getMessage());
            } catch (Exception s) {
                throw new DAOException("13." + s.getMessage());
            }
        }
        return bean;
    }

    public void createEmpresas(EmpresaBean bean) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        try {
            String sql = "INSERT INTO empresas_tipos(\n"
                    + "                        id, descripcion, estado, create_user, create_date, update_user,\n"
                    + "                        update_date)\n"
                    + "                VALUES( 5,  'TIENDAS',  'A',  1,  NOW(),  NULL,\n"
                    + "                         NULL);";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.executeUpdate();

        } catch (Exception s) {
            NovusUtils.printLn("EXISTE TIPOS DE EMPRESA");
        }
        try {
            String sql = "INSERT INTO public.paises(\n"
                    + "            id, descripcion, nomenclatura, moneda, indicador, create_user, \n"
                    + "            create_date, update_user, update_date, estado)\n"
                    + "    VALUES (?, ?, ?, ?, ?, 1, \n" + "            NOW(), NULL, NULL, 'A');";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, bean.getPaisId());
            ps.setString(2, bean.getPaisDescripcion());
            ps.setString(3, bean.getPaisNomenclatura());
            ps.setString(4, bean.getPaisMoneda());
            ps.setInt(5, bean.getPaisIndicador());
            ps.executeUpdate();
        } catch (SQLException s) {
            NovusUtils.printLn("EXISTE PAISES");
        }
        try {
            String sql = "INSERT INTO public.provincias(\n"
                    + "            id, descripcion, estado, paises_id, create_user, create_date, \n"
                    + "            update_user, update_date)\n" + "    VALUES (?, ?, 'A', ?, 1, NOW(), \n"
                    + "            NULL, NULL);";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, bean.getProvinciaId());
            ps.setString(2, bean.getProvinciaDescripcion());
            ps.setLong(3, bean.getPaisId());
            ps.executeUpdate();
        } catch (SQLException s) {
            NovusUtils.printLn("EXISTE PROVINCIAS");
        }
        try {
            String sql = "INSERT INTO public.ciudades(\n"
                    + "            id, descripcion, zona_horaria, indicadores, provincia_id, estado, \n"
                    + "            create_user, create_date, update_user, update_date)\n"
                    + "    VALUES (?, ?, ?, ?, ?, 'A', \n" + "            1, NOW(), NULL, NULL);";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, bean.getCiudadId());
            ps.setString(2, bean.getCiudadDescripcion());
            ps.setString(3, bean.getCiudadZonaHoraria());
            ps.setInt(4, bean.getCiudadIndicador());
            ps.setLong(5, bean.getProvinciaId());
            ps.executeUpdate();
        } catch (Exception s) {
            NovusUtils.printLn("EXISTE CIUDADES");
        }
        try {
            String sql = "INSERT INTO empresas(\n"
                    + "            id, razon_social, nit, localizacion, estado, cantidad_sucursales, \n"
                    + "            ciudades_id, empresas_id, create_user, create_date, update_user, \n"
                    + "            update_date, empresas_tipos_id, url_foto, dominio, alias, codigo, negocio)\n"
                    + "    VALUES (?, ?, ?, ?, 'A', 1, \n" + "            ?, null, 1, now(), null, \n"
                    + "            null, 5, ?, ?, ?, ?, ?);";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, bean.getId());
            ps.setString(2, bean.getRazonSocial());
            ps.setString(3, bean.getNit());
            ps.setString(4, bean.getLocalizacion());
            ps.setLong(5, bean.getCiudadId());
            ps.setString(6, bean.getUrlFotos());
            ps.setString(7, bean.getDominioId() + "");
            ps.setString(8, bean.getAlias() + "");
            ps.setString(9, bean.getCodigo() + "");
            ps.setLong(10, bean.getNegocioId());
            ps.executeUpdate();
        } catch (PSQLException s) {
            if (!s.getMessage().contains("pk_empresa")) {
                throw new DAOException("createEmpresas 11." + s.getMessage());
            } else {
                try {
                    String sql = "UPDATE public.empresas\n" + "   SET razon_social=?, nit=?, localizacion=?, \n"
                            + "       ciudades_id=?, url_foto=?, dominio=?, alias=?, codigo=?\n" + " WHERE ID=?";

                    PreparedStatement ps = conexion.prepareStatement(sql);
                    ps.setString(1, bean.getRazonSocial());
                    ps.setString(2, bean.getNit());
                    ps.setString(3, bean.getLocalizacion());
                    ps.setLong(4, bean.getCiudadId());
                    ps.setString(5, bean.getUrlFotos());
                    ps.setString(6, bean.getDominioId() + "");
                    ps.setString(7, bean.getAlias());
                    ps.setString(8, bean.getCodigo());
                    ps.setLong(9, bean.getId());
                    ps.executeUpdate();
                } catch (Exception a) {
                    NovusUtils.printLn("EXCEPTION ACTUALIZANDO EMPRESAS");
                }
            }
        } catch (SQLException s) {
            if (!s.getMessage().contains("pk_empresa")) {
                throw new DAOException("createEmpresas 11." + s.getMessage());
            } else {
                try {
                    String sql = "UPDATE public.empresas\n" + "   SET razon_social=?, nit=?, localizacion=?, \n"
                            + "       ciudades_id=?, url_foto=?, dominio=?, alias=?, codigo=?\n" + " WHERE ID=?";

                    PreparedStatement ps = conexion.prepareStatement(sql);
                    ps.setString(1, bean.getRazonSocial());
                    ps.setString(2, bean.getNit());
                    ps.setString(3, bean.getLocalizacion());
                    ps.setLong(4, bean.getCiudadId());
                    ps.setString(5, bean.getUrlFotos());
                    ps.setString(6, bean.getDominioId() + "");
                    ps.setString(7, bean.getAlias());
                    ps.setString(8, bean.getCodigo());
                    ps.setLong(9, bean.getId());
                    ps.executeUpdate();
                } catch (Exception a) {
                    NovusUtils.printLn("EXCEPTION ACTUALIZANDO EMPRESAS");
                }
            }
        } catch (Exception s) {
            throw new DAOException("createEmpresas 13." + s.getMessage());
        }
        try {

            String sql = "INSERT INTO descriptores(\n" + "            id, empresas_id, header, footer)\n"
                    + "    VALUES (?, ?, ?, ?);";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, bean.getDescriptorId());
            ps.setLong(2, bean.getId());
            ps.setString(3, bean.getDescriptorHeader());
            ps.setString(4, bean.getDescriptorFooter());
            ps.executeUpdate();
        } catch (Exception s) {
            if (s.getMessage().contains("descriptores")) {
                try {
                    String sql = "UPDATE public.descriptores\n" + "   SET empresas_id=?, header=?, footer=?\n"
                            + " WHERE id=?";
                    PreparedStatement ps = conexion.prepareStatement(sql);
                    ps.setLong(1, bean.getId());
                    ps.setString(2, bean.getDescriptorHeader());
                    ps.setString(3, bean.getDescriptorFooter());
                    ps.setLong(4, bean.getDescriptorId());
                    ps.executeUpdate();
                } catch (Exception a) {
                    NovusUtils.printLn("566. Error al actualizar los descriptores");
                }
            } else {
                NovusUtils.printLn("567. Error al actualizar los descriptores");
                NovusUtils.printLn(s.getMessage());
            }
            NovusUtils.printLn("error ciudades insert");
        }
        if (bean.getContacto() != null) {
            try {
                String sql = "DELETE FROM CONTACTOS WHERE EMPRESAS_ID = ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, bean.getId());
                ps.executeUpdate();
            } catch (Exception a) {
                NovusUtils.printLn("566. Error al actualizar los contactos de la empresa");
            }

            for (ContactoBean contacto : bean.getContacto()) {
                try {
                    String sql = "INSERT INTO CONTACTOS(\n"
                            + "            id, empresas_id, personas_id, tipo, etiqueta, contacto, estado, principal)\n"
                            + "    VALUES (nextval('contactos_id'), ?, null, ?, ?, ?, 'A', ?);";
                    PreparedStatement ps = conexion.prepareStatement(sql);

                    ps.setLong(1, bean.getId());
                    ps.setInt(2, contacto.getTipo());
                    ps.setString(3, contacto.getEtiqueta().toUpperCase().trim());
                    ps.setString(4, contacto.getContacto().toUpperCase().trim());
                    ps.setString(5, contacto.isPrincipal() ? "S" : "N");
                    ps.executeUpdate();

                } catch (PSQLException s) {
                    throw new DAOException("241." + s.getMessage());
                } catch (SQLException s) {
                    throw new DAOException("242." + s.getMessage());
                } catch (Exception ex) {
                    throw new DAOException("243." + ex.getMessage());
                }
            }
        }
    }

    public CredencialBean update(CredencialBean bean, long id) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        try {
            if (id == 0) {
                String sql = "UPDATE public.equipos\n"
                        + "   SET  token=?, password=?, autorizado=?, empresas_id=?\n" + " WHERE id=?";
                PreparedStatement ps = conexion.prepareStatement(sql);

                ps.setString(1, bean.getToken() != null ? bean.getToken() : "");
                ps.setString(2, bean.getPassword() != null ? bean.getPassword() : "");
                ps.setString(3, bean.isAutorizado() ? "S" : "N");
                if (bean.getEmpresas_id() != null) {
                    ps.setLong(4, bean.getEmpresas_id());
                } else {
                    ps.setNull(4, java.sql.Types.NULL);
                }
                ps.setLong(5, bean.getId());
                int r = ps.executeUpdate();

            } else {
                String sql = "UPDATE public.equipos\n"
                        + "   SET  token=?, password=?, autorizado=?, id=?, empresas_id=?\n" + " WHERE id=?";
                PreparedStatement ps = conexion.prepareStatement(sql);

                ps.setString(1, bean.getToken() != null ? bean.getToken() : "");
                ps.setString(2, bean.getPassword() != null ? bean.getPassword() : "");
                ps.setString(3, bean.isAutorizado() ? "S" : "N");
                ps.setLong(4, bean.getEquipos_id());
                ps.setLong(5, bean.getEmpresas_id());
                ps.setLong(6, id);

                NovusUtils.printLn(ps.toString());
                int r = ps.executeUpdate();
            }

        } catch (PSQLException s) {
            throw new DAOException("21." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("22." + s.getMessage());
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, ex);
            throw new DAOException("23." + ex.getMessage());
        }
        return bean;
    }

    public CredencialBean findToken(CredencialBean bean) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        bean.setRegistroPrevio(false);
        try {
            String sql = "SELECT ID, SERIAL_EQUIPO, TOKEN, PASSWORD, AUTORIZADO, EMPRESAS_ID, (select id from bodegas) BODEGA_ID\n"
                    + "FROM \n" + "EQUIPOS LIMIT 1";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();

            while (re.next()) {
                bean.setId(re.getLong("ID"));
                bean.setEquipos_id(re.getLong("ID"));
                bean.setReferencia(re.getString("SERIAL_EQUIPO"));
                bean.setToken(re.getString("TOKEN"));
                bean.setPassword(re.getString("PASSWORD"));
                bean.setEmpresas_id(re.getLong("EMPRESAS_ID"));

                if (re.getString("AUTORIZADO") != null) {
                    bean.setAutorizado(re.getString("AUTORIZADO").equals("S"));
                } else {
                    bean.setAutorizado(false);
                }
                bean.setRegistroPrevio(true);
            }

            if (bean.getToken() != null) {
                bean.setToken(bean.getToken().equals("") ? null : bean.getToken());
            }

            if (bean.getPassword() != null) {
                bean.setPassword(bean.getPassword().equals("") ? null : bean.getPassword());
            }

        } catch (PSQLException s) {
            throw new DAOException("31." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("32." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("33." + s.getMessage());
        }
        return bean;
    }

    public long findBodegaId() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        long bodegaId = 0;
        try {
            String sql = "SELECT ID FROM BODEGAS ORDER BY id ASC LIMIT 1";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            if (re.next()) {
                bodegaId = re.getLong("ID");
            }

        } catch (PSQLException s) {
            throw new DAOException("31." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("32." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("33." + s.getMessage());
        }
        return bodegaId;
    }

    public long findBodegaMovimientoId() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        long bodegaId = 0;
        try {
            String sql = "SELECT b.ID "
                    + "FROM BODEGAS b "
                    + "inner join bodegas_productos on bodegas_id=b.id "
                    + "limit 1";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            if (re.next()) {
                bodegaId = re.getLong("ID");
            }

        } catch (PSQLException s) {
            throw new DAOException("31." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("32." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("33." + s.getMessage());
        }
        return bodegaId;
    }

    public BodegaBean findBodega(long id) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        BodegaBean bodega = null;
        try {
            String sql = "SELECT * FROM BODEGAS WHERE ID=?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet re = ps.executeQuery();

            if (re.next()) {
                bodega = new BodegaBean();
                bodega.setId(re.getLong("ID"));
                bodega.setDescripcion(re.getString("descripcion"));
                bodega.setCodigo(re.getString("codigo"));
            }

        } catch (PSQLException s) {
            throw new DAOException("31." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("32." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("33." + s.getMessage());
        }
        return bodega;
    }

    public BodegaBean findBodegaPrincipalMaximoProducto() throws DAOException {
        BodegaBean bodega = null;
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        try {
            String sql = "select * from bodegas b \n"
                    + "inner join (\n"
                    + "select bodegas_id, count(productos_id) \n"
                    + "from bodegas_productos bp \n"
                    + "group by 1\n"
                    + "order by 2 desc\n"
                    + "limit 1\n"
                    + ") t on t.bodegas_id = b.id";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();

            if (re.next()) {
                bodega = new BodegaBean();
                bodega.setId(re.getLong("ID"));
                bodega.setDescripcion(re.getString("descripcion"));
                bodega.setCodigo(re.getString("codigo"));
            }

        } catch (PSQLException s) {
            throw new DAOException("31." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("32." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("33." + s.getMessage());
        }
        return bodega;
    }

    public BodegaBean findBodegaByProductoId(String finalidad, long idProducto) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        BodegaBean bodega = null;
        try {
            String sql = "SELECT * FROM BODEGAS b\n"
                    + "inner join bodegas_productos bp on bp.bodegas_id = b.id \n"
                    + "inner join productos p on p.id = bp.productos_id \n"
                    + "WHERE b.finalidad=? and  b.estado='A' and  p.ID = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, finalidad);
            ps.setLong(2, idProducto);
            ResultSet re = ps.executeQuery();
            if (re.next()) {
                bodega = new BodegaBean();
                bodega.setId(re.getLong("ID"));
                bodega.setDescripcion(re.getString("descripcion"));
                bodega.setCodigo(re.getString("codigo"));
            }

        } catch (PSQLException s) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, s);

            throw new DAOException("31." + s.getMessage());
        } catch (SQLException s) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, s);

            throw new DAOException("32." + s.getMessage());
        } catch (Exception s) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, s);

            throw new DAOException("33." + s.getMessage());
        }
        return bodega;
    }

    public BodegaBean findBodegaByProductoIdAnulacion(long idProducto) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        BodegaBean bodega = null;
        try {
            String sql = "SELECT * FROM BODEGAS b\n"
                    + "inner join bodegas_productos bp on bp.bodegas_id = b.id \n"
                    + "inner join productos p on p.id = bp.productos_id \n"
                    + "WHERE b.estado='A' and  p.ID = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, idProducto);
            ResultSet re = ps.executeQuery();
            if (re.next()) {
                bodega = new BodegaBean();
                bodega.setId(re.getLong("ID"));
                bodega.setDescripcion(re.getString("descripcion"));
                bodega.setCodigo(re.getString("codigo"));
            }

        } catch (PSQLException s) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, s);

            throw new DAOException("31." + s.getMessage());
        } catch (SQLException s) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, s);

            throw new DAOException("32." + s.getMessage());
        } catch (Exception s) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, s);

            throw new DAOException("33." + s.getMessage());
        }
        return bodega;
    }

    public long findEquipoId() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        long equipoId = 0;
        try {
            String sql = "SELECT id FROM EQUIPOS LIMIT 1";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            if (re.next()) {
                equipoId = re.getLong("id");
            }
        } catch (PSQLException s) {
            throw new DAOException("31." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("32." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("33." + s.getMessage());
        }
        return equipoId;
    }

    public long findEquipoIdCore() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        long equipoId = 0;
        try {
            String sql = "SELECT id FROM EQUIPOS LIMIT 1";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            if (re.next()) {
                equipoId = re.getLong("id");
            }
        } catch (PSQLException s) {
            throw new DAOException("31." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("32." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("33." + s.getMessage());
        }
        return equipoId;
    }

    public long findEmpresaEquipoId() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        long empresaId = 0;
        try {
            String sql = "SELECT empresas_id FROM EQUIPOS LIMIT 1";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            if (re.next()) {
                empresaId = re.getLong("empresas_id");
            }
        } catch (PSQLException s) {
            throw new DAOException("31." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("32." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("33." + s.getMessage());
        }
        return empresaId;
    }

    public long findEmpresaEquipoIdCore() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        long empresaId = 0;
        try {
            String sql = "SELECT empresas_id FROM EQUIPOS LIMIT 1";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            if (re.next()) {
                empresaId = re.getLong("empresas_id");
            }
        } catch (PSQLException s) {
            throw new DAOException("31." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("32." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("33." + s.getMessage());
        }
        return empresaId;
    }

    public long findEmpresaId() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        long equipo = 0;
        try {
            String sql = "SELECT id FROM EMPRESAS WHERE estado='A' LIMIT 1";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            if (re.next()) {
                equipo = re.getLong("id");
            }
        } catch (PSQLException s) {
            throw new DAOException("31." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("32." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("33." + s.getMessage());
        }
        return equipo;
    }

    public EmpresaBean findEmpresa(CredencialBean bean) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        EmpresaBean empresa = null;
        if (bean.getEmpresas_id() > 0) {
            try {
                String sql = "SELECT * ,COALESCE((SELECT array_to_json(array_agg(row_to_json(t))) FROM (SELECT * FROM perfiles) as t) ,'[]') perfiles "
                        + " FROM EMPRESAS  WHERE ID= ?  ";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, bean.getEmpresas_id());
                ResultSet re = ps.executeQuery();
                if (re.next()) {
                    empresa = new EmpresaBean();
                    empresa.setId(re.getLong("id"));
                    empresa.setRazonSocial(re.getString("razon_social"));
                    empresa.setNit(re.getString("nit"));
                    empresa.setAlias(re.getString("alias"));
                    empresa.setDominioId(re.getLong("dominio"));
                    empresa.setNegocioId(re.getLong("negocio"));
                    empresa.setEstado(re.getString("estado"));
                    empresa.setCodigo(re.getString("codigo"));
                    empresa.setCiudadDescripcion(re.getString("ciudades_descripcion"));
                    empresa.setPerfilesEmpresa(organizarPerfilesEmpresa(
                            new Gson().fromJson(re.getString("perfiles"), JsonArray.class)));
                }
                int TIPO_CONTACTO_TELEFONO = 1;
                int TIPO_CONTACTO_DIRECCION = 3;

                if (empresa != null) {
                    sql = "SELECT * FROM CONTACTOS WHERE PRINCIPAL='S'";
                    ps = conexion.prepareStatement(sql);
                    re = ps.executeQuery();
                    while (re.next()) {
                        if (empresa.getContacto() == null) {
                            empresa.setContacto(new ArrayList<>());
                        }
                        ContactoBean cont = new ContactoBean();
                        cont.setId(re.getLong("id"));
                        cont.setEtiqueta(re.getString("etiqueta"));
                        cont.setContacto(re.getString("contacto"));
                        cont.setTipo(re.getInt("tipo"));
                        empresa.getContacto().add(cont);

                        if (cont.getTipo() == TIPO_CONTACTO_TELEFONO) {
                            empresa.setTelefonoPrincipal(cont.getContacto());
                        }
                        if (cont.getTipo() == TIPO_CONTACTO_DIRECCION) {
                            empresa.setDireccionPrincipal(cont.getContacto());
                        }
                    }
                }
            } catch (PSQLException s) {
                throw new DAOException("31." + s.getMessage());
            } catch (SQLException s) {
                throw new DAOException("32." + s.getMessage());
            } catch (Exception s) {
                throw new DAOException("33." + s.getMessage());
            }
        }
        return empresa;
    }

    public EmpresaBean findEmpresaCore(CredencialBean bean) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        EmpresaBean empresa = null;
        if (bean.getEmpresas_id() > 0) {
            try {
                String sql = "SELECT * ,COALESCE((SELECT array_to_json(array_agg(row_to_json(t))) FROM (SELECT * FROM perfiles) as t) ,'[]') perfiles "
                        + " FROM EMPRESAS  WHERE ID= ?  ";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, bean.getEmpresas_id());
                ResultSet re = ps.executeQuery();
                if (re.next()) {
                    empresa = new EmpresaBean();
                    empresa.setId(re.getLong("id"));
                    empresa.setRazonSocial(re.getString("razon_social"));
                    empresa.setNit(re.getString("nit"));
                    empresa.setAlias(re.getString("alias"));
                    empresa.setDominioId(re.getLong("dominio"));
                    empresa.setNegocioId(re.getLong("negocio_id"));
                    empresa.setEstado(re.getString("estado"));
                    empresa.setCodigo(re.getString("codigo_empresa"));
                    empresa.setCiudadDescripcion(re.getString("ciudades_descripcion"));
                    empresa.setPerfilesEmpresa(organizarPerfilesEmpresa(
                            new Gson().fromJson(re.getString("perfiles"), JsonArray.class)));
                }
                int TIPO_CONTACTO_TELEFONO = 1;
                int TIPO_CONTACTO_DIRECCION = 3;
                if (empresa != null) {
                    sql = "SELECT * FROM CONTACTOS WHERE PRINCIPAL='S'";
                    ps = conexion.prepareStatement(sql);
                    re = ps.executeQuery();
                    while (re.next()) {
                        if (empresa.getContacto() == null) {
                            empresa.setContacto(new ArrayList<>());
                        }
                        ContactoBean cont = new ContactoBean();
                        cont.setId(re.getLong("id"));
                        cont.setEtiqueta(re.getString("etiqueta"));
                        cont.setContacto(re.getString("contacto"));
                        cont.setTipo(re.getInt("tipo"));
                        empresa.getContacto().add(cont);
                        if (cont.getTipo() == TIPO_CONTACTO_TELEFONO) {
                            empresa.setTelefonoPrincipal(cont.getContacto());
                        }
                        if (cont.getTipo() == TIPO_CONTACTO_DIRECCION) {
                            empresa.setDireccionPrincipal(cont.getContacto());
                        }
                    }
                }
            } catch (PSQLException s) {
                throw new DAOException("31." + s.getMessage());
            } catch (SQLException s) {
                throw new DAOException("32." + s.getMessage());
            } catch (Exception s) {
                throw new DAOException("33." + s.getMessage());
            }
        }
        return empresa;
    }

    ArrayList<PerfilesBean> organizarPerfilesEmpresa(JsonArray jsonArrayPerfilesEmpresa) {
        ArrayList<PerfilesBean> perfilesEmpresa = new ArrayList<>();
        for (JsonElement element : jsonArrayPerfilesEmpresa) {
            JsonObject jsonPerfil = element.getAsJsonObject();
            PerfilesBean perfil = new PerfilesBean();
            perfil.setId(jsonPerfil.get("id").getAsLong());
            perfil.setDescripcion(jsonPerfil.get("descripcion").getAsString());
            perfilesEmpresa.add(perfil);
        }
        return perfilesEmpresa;
    }

    public ArrayList<TransmisionBean> getTransmisiones() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        ArrayList<TransmisionBean> lista = new ArrayList<>();
        try {
            String sql = "SELECT * FROM TRANSMISION WHERE SINCRONIZADO=0 ORDER BY fecha_generado DESC";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();

            while (re.next()) {
                TransmisionBean tx = new TransmisionBean();
                tx.setId(re.getLong("id"));
                tx.setRequest(re.getString("request"));
                tx.setUrl(re.getString("url"));
                tx.setMethod(re.getString("method"));
                tx.setReintentos(re.getInt("reintentos"));
                tx.setFechaTrasmitido(re.getTimestamp("fecha_trasmitido"));
                lista.add(tx);
            }
        } catch (PSQLException s) {
            throw new DAOException("TXDAO41." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("TXDAO42." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("TXDAO43." + s.getMessage());
        }
        return lista;
    }

    public int getCantidadTransmisionePendientes() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        int cantidad = 0;
        try {
            String sql = "SELECT count(1) FROM TRANSMISION WHERE SINCRONIZADO=0";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            if (re.next()) {
                cantidad = re.getInt(1);
            }
        } catch (PSQLException s) {
            throw new DAOException("TXDAO41." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("TXDAO42." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("TXDAO43." + s.getMessage());
        }
        return cantidad;
    }

    public ArrayList<ProductoBean> getAllProductos() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        ArrayList<ProductoBean> lista = new ArrayList<>();
        MovimientosDao mdao = new MovimientosDao();
        try {

            String sql = "Select p.id, p.plu, p.estado, p.descripcion, p.precio, p.tipo, p.cantidad_ingredientes, p.cantidad_impuestos, COALESCE(saldo, 0) saldo, bp.costo\n"
                    + "from productos p \n" + "left join bodegas_productos bp on bp.productos_id=p.id\n"
                    + "where P.ESTADO in ('A', 'B') and P.puede_vender='S' \n" + "";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();

            while (re.next()) {
                ProductoBean bean = new ProductoBean();
                bean.setId(re.getLong("ID"));
                bean.setPlu(re.getString("PLU"));
                bean.setDescripcion(re.getString("DESCRIPCION"));
                bean.setPrecio(re.getFloat("PRECIO"));
                bean.setCantidad(re.getInt("SALDO"));
                bean.setSaldo(re.getInt("SALDO"));
                bean.setCantidadIngredientes(re.getInt("CANTIDAD_INGREDIENTES"));
                bean.setCantidadImpuestos(re.getInt("CANTIDAD_IMPUESTOS"));
                bean.setEstado(re.getString("ESTADO"));
                // bean.setCostos(re.getFloat("COSTO"));
                // bean.setCostoProducto(re.getFloat("COSTO"));
                bean.setProducto_compuesto_costo(re.getFloat("COSTO"));
                bean.setCompuesto(re.getInt("TIPO") == 2);
                bean.setImpuestos(mdao.findById(re.getLong("ID")));
                bean.setIngredientes(mdao.findIngredientesById(re.getLong("ID")));
                if (!bean.getIngredientes().isEmpty()) {
                    for (ProductoBean ingrediente : bean.getIngredientes()) {
                        bean.setProducto_compuesto_costo(
                                bean.getProducto_compuesto_costo() + (ingrediente.getProducto_compuesto_costo()
                                * ingrediente.getProducto_compuesto_cantidad()));
                    }
                }

                lista.add(bean);
            }

        } catch (PSQLException s) {
            throw new DAOException("TXDAO41." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("TXDAO42." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("TXDAO43." + s.getMessage());
        }
        return lista;
    }

    public TreeMap<Integer, Surtidor> getSurtidores() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        TreeMap<Integer, Surtidor> lista = new TreeMap<>();
        try {

            String sql = "SELECT * FROM surtidores;";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();

            while (re.next()) {
                Surtidor bean = new Surtidor();
                bean.setSurtidor(re.getInt("id"));
                bean.setIp(re.getString("ip"));
                bean.setPort(re.getInt("puerto"));
                lista.put(bean.getSurtidor(), bean);
            }

        } catch (PSQLException s) {
            throw new DAOException("TXDAO41." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("TXDAO42." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("TXDAO43." + s.getMessage());
        }
        return lista;
    }

    public ArrayList<MediosPagosBean> getMediosPagos() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        ArrayList<MediosPagosBean> medios = new ArrayList<>();
        try {

            String sql = "SELECT * FROM medios_pagos";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();

            while (re.next()) {
                MediosPagosBean bean = new MediosPagosBean();
                bean.setId(re.getInt("id"));
                bean.setDescripcion(re.getString("descripcion"));
                bean.setEstado(re.getString("estado"));
                medios.add(bean);
            }

        } catch (PSQLException s) {
            throw new DAOException("TXDAO41." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("TXDAO42." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("TXDAO43." + s.getMessage());
        }
        return medios;
    }

    public void guardarTransmision(CredencialBean credencial, String request, String url, String method)
            throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        try {
            String sql = "INSERT INTO public.transmision(\n"
                    + "             equipo_id, request, response, sincronizado, fecha_generado, \n"
                    + "            fecha_trasmitido, fecha_ultima, url, method, reintentos)\n"
                    + "    VALUES (?, ?::json, NULL, 0, now(), \n" + "            NULL, NULL, ?, ?, 0);";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, credencial.getId());
            // Sanear posibles caracteres de control no válidos
            ps.setString(2, com.utils.JsonSanitizer.escapeInvalidControlChars(request));
            ps.setString(3, url);
            ps.setString(4, method);
            ps.executeUpdate();

        } catch (PSQLException s) {
            throw new DAOException("41." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("42." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("43." + s.getMessage());
        }
    }

    public void guardarTransmisionKioscoCanastilla(CredencialBean credencial, String request, String url, String method)
            throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        try {
            String sql = "INSERT INTO public.transmision(\n"
                    + "             equipo_id, request, response, sincronizado, fecha_generado, \n"
                    + "            fecha_trasmitido, fecha_ultima, url, method, reintentos)\n"
                    + "    VALUES (?, ?::json, NULL, 1, now(), \n" + "            NULL, NULL, ?, ?, 0);";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, credencial.getId());
            // Sanear posibles caracteres de control no válidos
            ps.setString(2, com.utils.JsonSanitizer.escapeInvalidControlChars(request));
            ps.setString(3, url);
            ps.setString(4, method);
            ps.executeUpdate();

        } catch (PSQLException s) {
            throw new DAOException("41." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("42." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("43." + s.getMessage());
        }
    }

    public void updateTransmision(long serial, JsonObject response, int sincronizado, Date txdate, int reintentos,
            int status) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        try {
            String sql = "UPDATE public.transmision\n" + "   SET response=?, sincronizado=?,\n"
                    + "       fecha_trasmitido=?, fecha_ultima=now(), reintentos=?, status=?\n" + " WHERE id=?;";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, response.toString());
            ps.setInt(2, sincronizado);
            ps.setTimestamp(3, new Timestamp(txdate.getTime()));
            ps.setInt(4, reintentos);
            ps.setLong(5, status);
            ps.setLong(6, serial);

            ps.executeUpdate();

        } catch (PSQLException s) {
            throw new DAOException("41." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("42." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("43." + s.getMessage());
        }
    }

    public void updateNormalizaConsecutivo() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        try {

            String sql;
            PreparedStatement ps;

            try {
                sql = "ALTER TABLE MOVIMIENTOS ADD COLUMN CONSECUTIVO_ID BIGINT;";
                ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
            } catch (PSQLException s) {
                NovusUtils.printLn("UPDATE 141." + s.getMessage());
                NovusUtils.printLn("UPDATE 141." + s.getMessage());
            } catch (SQLException s) {
                NovusUtils.printLn("UPDATE 142." + s.getMessage());
                NovusUtils.printLn("UPDATE 142." + s.getMessage());
            }
            try {
                sql = "ALTER TABLE MOVIMIENTOS ADD COLUMN CONSECUTIVO_OBSERVACION CHARACTER VARYING (255);";
                ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
            } catch (PSQLException s) {
                NovusUtils.printLn("UPDATE 241." + s.getMessage());
                NovusUtils.printLn("UPDATE 241." + s.getMessage());
            } catch (SQLException s) {
                NovusUtils.printLn("UPDATE 242." + s.getMessage());
                NovusUtils.printLn("UPDATE 242." + s.getMessage());
            }

            try {
                sql = "ALTER TABLE MOVIMIENTOS ADD COLUMN CONSECUTIVO_PREFIJO CHARACTER VARYING (255);";
                ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
            } catch (PSQLException s) {
                NovusUtils.printLn("UPDATE 341." + s.getMessage());
            } catch (SQLException s) {
                NovusUtils.printLn("UPDATE 342." + s.getMessage());
            }

            try {

                sql = "SELECT * FROM CONSECUTIVOS WHERE ESTADO='U' LIMIT 1";
                ps = conexion.prepareStatement(sql);
                ResultSet re = ps.executeQuery();

                if (re.next()) {

                    ConsecutivoBean cons = new ConsecutivoBean();
                    cons.setId(re.getLong("id"));
                    cons.setResolucion(re.getString("resolucion"));
                    cons.setConsecutivo_inicial(re.getLong("consecutivo_inicial"));
                    cons.setConsecutivo_final(re.getLong("consecutivo_final"));
                    cons.setPrefijo(re.getString("prefijo"));
                    cons.setObservaciones(re.getString("observaciones"));

                    sql = "UPDATE MOVIMIENTOS set CONSECUTIVO_ID = ?, CONSECUTIVO_OBSERVACION=?, CONSECUTIVO_PREFIJO=?";
                    ps = conexion.prepareStatement(sql);
                    ps.setLong(1, cons.getId());
                    ps.setString(2, cons.getFormato());
                    ps.setString(3, cons.getPrefijo());
                    ps.executeUpdate();
                }

            } catch (PSQLException s) {
                NovusUtils.printLn("UPDATE 441." + s.getMessage());
            } catch (SQLException s) {
                NovusUtils.printLn("UPDATE 442." + s.getMessage());
            }

        } catch (Exception s) {
            throw new DAOException("UPDATE 041." + s.getMessage());
        }

    }

    public void updateCantidadIngredientes() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        try {
            String sql;
            PreparedStatement ps;
            try {
                sql = "ALTER TABLE PRODUCTOS ADD COLUMN CANTIDAD_INGREDIENTES INTEGER DEFAULT 0;";
                ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
            } catch (PSQLException s) {
                NovusUtils.printLn("UPDATE 141." + s.getMessage());
            } catch (SQLException s) {
                NovusUtils.printLn("UPDATE 142." + s.getMessage());
            }

        } catch (Exception s) {
            throw new DAOException("UPDATE 041." + s.getMessage());
        }
    }

    public void updateCantidadImpuestos() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        try {
            String sql;
            PreparedStatement ps;
            try {
                sql = "ALTER TABLE PRODUCTOS ADD COLUMN CANTIDAD_IMPUESTOS INTEGER DEFAULT 0;";
                ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
            } catch (PSQLException s) {
                NovusUtils.printLn("UPDATE 1441." + s.getMessage());
            } catch (SQLException s) {
                NovusUtils.printLn("UPDATE 1442." + s.getMessage());
            }

        } catch (Exception s) {
            throw new DAOException("UPDATE 041." + s.getMessage());
        }
    }

    public void formatearEquipo() {
        Connection conexionRegistry = Main.obtenerConexion("lazoexpressregistry");

        try {
            String sql;
            sql = "SELECT table_name FROM information_schema.tables WHERE table_schema='public' AND table_type='BASE TABLE'";
            try ( PreparedStatement ps = conexionRegistry.prepareStatement(sql)) {
                ResultSet re = ps.executeQuery();
                while (re.next()) {
                    sql = "TRUNCATE TABLE " + re.getString("table_name") + " CASCADE; ";
                    try ( PreparedStatement ps2 = conexionRegistry.prepareStatement(sql)) {
                        ps2.executeUpdate();
                    }
                }
            }

        } catch (SQLException s) {
            NovusUtils.printLn("UPDATE 041." + s.getMessage());
        }
        Connection conexionCore = Main.obtenerConexion("lazoexpresscore");
        try {
            String sql;
            PreparedStatement ps;
            try {
                sql = " SELECT table_name FROM information_schema.tables WHERE table_schema='public' AND table_type='BASE TABLE';";
                ps = conexionCore.prepareStatement(sql);
                ResultSet re = ps.executeQuery();
                while (re.next()) {
                    sql = "TRUNCATE TABLE " + re.getString("table_name") + " CASCADE; ";
                    ps = conexionCore.prepareStatement(sql);
                    ps.executeUpdate();
                }
            } catch (PSQLException s) {
                NovusUtils.printLn("UPDATE 1441." + s.getMessage());
            } catch (SQLException s) {
                NovusUtils.printLn("UPDATE 1442." + s.getMessage());
            }
        } catch (Exception s) {
            NovusUtils.printLn("UPDATE 041." + s.getMessage());
        }
    }

    public MovimientosBean lastMovimiento() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        MovimientosBean movimiento = null;
        try {
            String sql = "SELECT consecutivo, fecha FROM movimientos ORDER BY id DESC LIMIT 1";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();

            if (re.next()) {
                movimiento = new MovimientosBean();
                movimiento.setId(re.getLong("consecutivo"));
                movimiento.setFecha(re.getTimestamp("fecha"));
            }

        } catch (PSQLException s) {
            throw new DAOException("31." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("32." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("33." + s.getMessage());
        }
        return movimiento;
    }

    public EquiposAutorizacionBean getEquipo() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        EquiposAutorizacionBean equipoRegistrado = null;
        try {
            String sql = "SELECT * FROM equipos LIMIT 1";
            Statement stm = conexion.createStatement();
            ResultSet re = stm.executeQuery(sql);
            if (re.next()) {
                equipoRegistrado = new EquiposAutorizacionBean();
                equipoRegistrado.setIdEquipo(re.getInt("id"));
                equipoRegistrado.setSerialEquipo(re.getString("serial_equipo"));
                equipoRegistrado.setEstado(re.getString("estado"));
                equipoRegistrado.setMacEquipo(re.getString("mac"));
                equipoRegistrado.setTokenEquipo(re.getString("token"));
            }
        } catch (PSQLException s) {
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }
        return equipoRegistrado;

    }

    public void registrarEquipoCore(JsonObject response, CredencialBean eq) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try {
            String sql = "INSERT INTO equipos(id,empresas_id, serial_equipo,almacenamientos_id,"
                    + "estado,equipos_tipos_id,equipos_protocolos_id,mac,ip,port,create_user,"
                    + "create_date,update_date,token,password,factor_precio,factor_importe,factor_inventario,"
                    + "lector_ip,lector_port,impresora_ip,impresora_port,url_foto,autorizado)\n"
                    + "  VALUES (?,?,?,NULL,?,NULL,NULL,?,?,NULL,0,now(),NULL,?,?,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,?)";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, response.get("equipos_id").getAsLong());
            ps.setLong(2, response.get("empresas_id").getAsLong());
            ps.setString(3, eq.getSerial());
            ps.setString(4, (response.get("autorizado").getAsBoolean()) ? "A" : "N");
            ps.setString(5, eq.getMac());
            ps.setString(6, eq.getIpEquipo());
            ps.setString(7, response.get("token").getAsString());
            ps.setString(8, response.get("password").getAsString());
            ps.setString(9, (response.get("autorizado").getAsBoolean()) ? "A" : "N");
            ps.executeUpdate();
        } catch (PSQLException s) {
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }

    }

    public ArrayList<ModulosBean> getModulosRegistrados() {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        ArrayList<ModulosBean> modulos_registrados = new ArrayList<>();
        try {
            String sql = "SELECT ID, ATRIBUTOS::json->>'url' LINK, DESCRIPCION, ACCIONES FROM MODULOS";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                ModulosBean modulo = new ModulosBean();
                modulo.setId(re.getInt("ID"));
                modulo.setLink(re.getString("LINK"));
                modulo.setDescripcion(re.getString("DESCRIPCION"));
                modulo.setAcciones(re.getString("ACCIONES"));
                modulos_registrados.add(modulo);
            }
        } catch (SQLException ex) {
            NovusUtils.printLn("ERROR EN LA BD TreeMap<String, String> getParametros(): " + ex.getMessage());
        } catch (Exception ex) {
            NovusUtils.printLn(ex.getMessage());
        }
        return modulos_registrados;
    }

    public String getParametro(String parametro) {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        String valor = null;
        try {
            NovusUtils.printLn("BUSCA DE PARAMETRO:" + parametro);
            String sql = "SELECT valor FROM parametros WHERE nombre ilike ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, "%" + parametro + "%");
            ResultSet re = ps.executeQuery();
            if (re.next()) {
                valor = re.getString("valor");
            }
        } catch (SQLException ex) {
            NovusUtils.printLn("getParametro(?): " + ex.getMessage());
        } catch (Exception ex) {
            NovusUtils.printLn(ex.getMessage());
        }
        return valor;
    }

    public String getParametrosImpresoraKCO() {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String valor = null;
        try {
            String sql = "SELECT valor FROM parametros WHERE codigo='impresora_kcos'";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            if (re.next()) {
                valor = re.getString("valor");
            }
            if (valor == null) {
                sql = "INSERT INTO public.parametros (id,codigo,valor,tipo,opciones,valor_default,descripcion) VALUES\n"
                        + "	 (1000,'impresora_kcos','localhost','STRING',NULL,NULL,NULL); ";
                ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
                valor = "localhost";
            }
        } catch (SQLException ex) {
            NovusUtils.printLn("ERROR EN LA BD TreeMap<String, String> getParametros(): " + ex.getMessage());
        } catch (Exception ex) {
            NovusUtils.printLn(ex.getMessage());
        }
        return valor;
    }

    public static TreeMap<String, String> getParametros(String database) {
        Connection conexion = Main.obtenerConexion(database);

        TreeMap<String, String> valores = new TreeMap<>();
        try {

            if (database.contains("lazoexpresscore")) {
                String sql = "SELECT * FROM wacher_parametros where codigo in( ";
                for (String item : NovusConstante.PREFERENCE_LOAD_AUTO_WA_PARAMETROS) {
                    sql += "'";
                    sql += (item);
                    sql += "',";
                }
                sql = sql.substring(0, sql.length() - 1) + " )";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ResultSet re = ps.executeQuery();
                while (re.next()) {
                    valores.put(re.getString("codigo"), re.getString("valor"));
                }
                sql = "SELECT * FROM parametros where codigo in( ";
                for (String item : NovusConstante.PREFERENCE_LOAD_AUTO_PARAMETROS) {
                    sql += "'";
                    sql += (item);
                    sql += "',";
                }
                sql = sql.substring(0, sql.length() - 1) + " )";
                ps = conexion.prepareStatement(sql);
                re = ps.executeQuery();
                while (re.next()) {
                    valores.put(re.getString("codigo"), re.getString("valor"));
                }
            } else {
                String sql = "SELECT * FROM parametros";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ResultSet re = ps.executeQuery();
                while (re.next()) {
                    valores.put(re.getString(database.contains("registry") ? "nombre" : "codigo"), re.getString("valor"));
                }
            }
        } catch (SQLException ex) {
            NovusUtils.printLn("ERROR EN LA BD TreeMap<String, String> getParametros(): " + ex.getMessage());
        } catch (Exception ex) {
            NovusUtils.printLn(ex.getMessage());
        }
        return valores;
    }

    public static void setParametro(String key, String value, boolean isCore) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try {
            NovusUtils.printLn("PARAMETROS INSERTADO => " + key + ": " + value);
            String sql = "SELECT valor FROM parametros WHERE codigo = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, key);

            ResultSet re = ps.executeQuery();
            boolean existe = re.next();
            if (!existe) {
                long ultimoId = 0;
                sql = "SELECT coalesce(max(id),0) id FROM parametros";
                ps = conexion.prepareStatement(sql);
                re = ps.executeQuery();
                if (re.next()) {
                    ultimoId = re.getLong("id");
                }

                sql = "INSERT INTO parametros(id, codigo, valor,tipo) VALUES (?,? ,?,'TEXT')";
                ps = conexion.prepareStatement(sql);
                ps.setLong(1, ultimoId + 1);
                ps.setString(2, key);
                ps.setString(3, value);
                ps.executeUpdate();
            } else {
                sql = "UPDATE parametros set valor=? WHERE codigo=?";
                ps = conexion.prepareStatement(sql);
                ps.setString(1, value);
                ps.setString(2, key);
                ps.executeUpdate();
            }

        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            NovusUtils.printLn("PARAMETRO: " + key + " VALUE: " + value);
            NovusUtils.printLn(ex.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            NovusUtils.printLn("PARAMETRO: " + key + " VALUE: " + value);
            NovusUtils.printLn(ex.getMessage());
        }
    }

    public static void setParametro(String key, String value) {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        try {
            String sql = "SELECT valor FROM parametros WHERE nombre = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, key);
            ResultSet re = ps.executeQuery();
            boolean existe = re.next();
            if (!existe) {
                sql = "INSERT INTO parametros(nombre, valor) VALUES (? ,?)";
                ps = conexion.prepareStatement(sql);
                ps.setString(1, key);
                ps.setString(2, value);
                ps.executeUpdate();
            } else {
                sql = "UPDATE parametros set valor=? WHERE nombre=?";
                ps = conexion.prepareStatement(sql);
                ps.setString(1, value);
                ps.setString(2, key);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            NovusUtils.printLn("PARAMETRO: " + key + " VALUE: " + value);
            NovusUtils.printLn(ex.getMessage());
        } catch (Exception ex) {
            NovusUtils.printLn("PARAMETRO: " + key + " VALUE: " + value);
            NovusUtils.printLn(ex.getMessage());
        }
    }

    public boolean createEmpresasRegistry(EmpresaBean bean) throws DAOException {
        class Inner {

            Connection conexion = Main.obtenerConexion("lazoexpressregistry");

            boolean existeEmpresa() throws SQLException {
                String sql = "SELECT ID FROM empresas WHERE ID = ? LIMIT 1";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, bean.getId());
                return ps.executeQuery().next();
            }

            boolean existeDescriptor() throws SQLException {
                String sql = "SELECT ID FROM descriptores WHERE EMPRESAS_ID = ? LIMIT 1";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, Main.credencial.getEmpresas_id());
                return ps.executeQuery().next();
            }

            void insertarDescriptor() throws SQLException {
                String sql = "INSERT INTO descriptores(id, empresas_id, header, footer,subheader) "
                        + "    VALUES (?, ?, ?, ?, NULL);";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, bean.getDescriptorId());
                ps.setLong(2, bean.getId());
                ps.setString(3, bean.getDescriptorHeader() != null ? bean.getDescriptorHeader() : "");
                ps.setString(4, bean.getDescriptorFooter() != null ? bean.getDescriptorFooter() : "");
                ps.executeUpdate();
            }

            void actualizarDescriptor() throws SQLException {
                String sql = "UPDATE descriptores SET empresas_id=?, header=?, footer=? " + " WHERE id=?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, bean.getId());
                ps.setString(2, bean.getDescriptorHeader() != null ? bean.getDescriptorHeader() : "");
                ps.setString(3, bean.getDescriptorFooter() != null ? bean.getDescriptorFooter() : "");
                ps.setLong(4, bean.getDescriptorId());
                ps.executeUpdate();
            }

            void insertarEmpresa() throws SQLException {
                String sql = "INSERT INTO empresas(id, razon_social,nit,localizacion,estado,"
                        + "cantidad_sucursales, ciudades_id, empresas_id, create_user, create_date,"
                        + "update_user,update_date, empresas_tipos_id, url_foto,"
                        + "dominio, codigo,alias,negocio,ciudades_descripcion) VALUES(?,?,?,?,?,1,?,?,1,now(),NULL, NULL,?,NULL,?,?,?,?,?)";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, bean.getId());
                ps.setString(2, bean.getRazonSocial() != null ? bean.getRazonSocial() : "");
                ps.setString(3, bean.getNit() != null ? bean.getNit() : "");
                ps.setString(4, bean.getLocalizacion() != null ? bean.getLocalizacion() : "");
                ps.setString(5, bean.getEstado());
                ps.setLong(6, bean.getCiudadId());
                ps.setLong(7, bean.getId());
                ps.setLong(8, bean.getEmpresaTipoId());
                ps.setLong(9, bean.getDominioId());
                ps.setString(10, bean.getCodigo() != null ? bean.getCodigo() : "");
                ps.setString(11, bean.getAlias() != null ? bean.getAlias() : "");
                ps.setLong(12, bean.getNegocioId());
                ps.setString(13, bean.getCiudadDescripcion());
                ps.executeUpdate();
            }

            void actualizarEmpresa() throws SQLException {
                String sql = "UPDATE public.empresas SET razon_social=?, nit=?, localizacion=?,"
                        + " ciudades_id=?, url_foto=?, dominio=?, alias=?, codigo=?,negocio=?, ciudades_descripcion=? "
                        + " WHERE id=?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setString(1, bean.getRazonSocial() != null ? bean.getRazonSocial() : "");
                ps.setString(2, bean.getNit() != null ? bean.getNit() : "");
                ps.setString(3, bean.getLocalizacion() != null ? bean.getLocalizacion() : "");
                ps.setLong(4, bean.getCiudadId());
                ps.setString(5, bean.getUrlFotos());
                ps.setString(6, bean.getDominioId() + "");
                ps.setString(7, bean.getAlias() != null ? bean.getAlias() : "");
                ps.setString(8, bean.getCodigo() != null ? bean.getCodigo() : "");
                ps.setLong(9, bean.getNegocioId());
                ps.setString(10, bean.getCiudadDescripcion());
                ps.setLong(11, bean.getId());
                ps.executeUpdate();
            }

            void eliminarContactosEmpresa() throws SQLException {
                String sql = "DELETE FROM CONTACTOS WHERE EMPRESAS_ID = ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, bean.getId());
                ps.executeUpdate();
            }

            void insertarContactosEmpresa(ContactoBean contacto) throws SQLException {
                String sql = "INSERT INTO CONTACTOS("
                        + "            id, empresas_id, personas_id, tipo, etiqueta, contacto, estado, principal) "
                        + "    VALUES (nextval('contactos_id'), ?, null, ?, ?, ?, 'A', ?);";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, bean.getId());
                ps.setInt(2, contacto.getTipo());
                ps.setString(3, contacto.getEtiqueta().toUpperCase().trim());
                ps.setString(4, contacto.getContacto().toUpperCase().trim());
                ps.setString(5, contacto.isPrincipal() ? "S" : "N");
                ps.executeUpdate();
                if (contacto.isPrincipal() && contacto.getTipo() == NovusConstante.CONTACTO_TIPO_DIRECCION) {
                    bean.setDireccionPrincipal(contacto.getContacto());
                }
                if (contacto.isPrincipal() && contacto.getTipo() == NovusConstante.CONTACTO_TIPO_TELEFONO) {
                    bean.setTelefonoPrincipal(contacto.getContacto());
                }
            }
        }
        Inner innerCall = new Inner();
        boolean insertado = true;
        boolean existeEmpresa;
        boolean existeDescriptores;
        try {
            existeEmpresa = innerCall.existeEmpresa();
            if (!existeEmpresa) {
                innerCall.insertarEmpresa();
            } else {
                innerCall.actualizarEmpresa();
            }
            SetupDao sdao = new SetupDao();
            sdao.insertarPerfiles(Main.dbRegistry, bean.getPerfilesEmpresa());
            if (bean.getContacto() != null) {
                innerCall.eliminarContactosEmpresa();
                for (ContactoBean contacto : bean.getContacto()) {
                    innerCall.insertarContactosEmpresa(contacto);
                }
            }
            existeDescriptores = innerCall.existeDescriptor();
            if (!existeDescriptores) {
                innerCall.insertarDescriptor();
            } else {
                innerCall.actualizarDescriptor();
            }
        } catch (SQLException e) {
            insertado = false;
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, e);
            NovusUtils.printLn(e.getMessage());
        }

        return insertado;
    }

    public void actualizarNombreReginal(EmpresaBean bean) {
        try {
            Connection conexion = Main.obtenerConexion("lazoexpresscore");
            String sql = "UPDATE empresas set atributos=?::json where id=?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            JsonObject nombreRegional = new JsonObject();
            nombreRegional.addProperty("nombreRegional", bean.getNombreReginal());
            if (!nombreRegional.get("nombreRegional").isJsonNull() || !nombreRegional.get("nombreRegional").equals("")) {
                ps.setString(1, nombreRegional.toString());
                ps.setLong(2, bean.getId());
                int resp = ps.executeUpdate();
                if (resp > 0) {
                    System.out.println("dato de nombre reginal editado ");
                } else {
                    System.out.println("dato de nombre reginal error al editar ");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean createEmpresasCore(EmpresaBean bean) throws DAOException {
        class Inner {

            Connection conexion = Main.obtenerConexion("lazoexpresscore");

            boolean existeEmpresa() throws SQLException {
                String sql = "SELECT ID FROM empresas WHERE ID = ? LIMIT 1";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, bean.getId());
                return ps.executeQuery().next();
            }

            boolean existeDescriptor() throws SQLException {
                String sql = "SELECT ID FROM descriptores WHERE empresas_id=? LIMIT 1";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, bean.getId());
                return ps.executeQuery().next();
            }

            void insertarEmpresa() throws SQLException {
                String sql = "INSERT INTO empresas(id, razon_social,nit,direccion,telefono,estado, correo,"
                        + " contacto_nombre,contacto_telefono, contacto_correo, fecha_creacion, localizacion,empresas_id,"
                        + "cantidad_sucursales, ciudades_id, create_user, create_date, update_user, update_date, "
                        + "empresas_tipos_id, url_foto, dominio, alias, codigo_empresa, dominio_id, negocio_id, ciudades_descripcion, atributos)"
                        + " VALUES(?, ?, ?, ?, ?, ?, NULL, NULL, NULL, NULL , ?, NULL, ?, 1, ?, NULL,NULL,NULL, "
                        + "NULL, ? ,NULL,NULL, ?, ?, ?, ?, ?, ?::json)";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, bean.getId());
                ps.setString(2, bean.getRazonSocial() != null ? bean.getRazonSocial() : "");
                ps.setString(3, bean.getNit() != null ? bean.getNit() : "");
                ps.setString(4, bean.getDireccionPrincipal() != null ? bean.getDireccionPrincipal() : "");
                ps.setString(5, bean.getTelefonoPrincipal() != null ? bean.getTelefonoPrincipal() : "");
                ps.setString(6, bean.getEstado());
                ps.setTimestamp(7, new Timestamp(new Date().getTime()));
                ps.setLong(8, bean.getEmpresasId());
                ps.setLong(9, bean.getCiudadId());
                ps.setLong(10, bean.getEmpresaTipoId());
                ps.setString(11, bean.getAlias() != null ? bean.getAlias() : "");
                ps.setString(12, bean.getCodigo() != null ? bean.getCodigo() : "");
                ps.setLong(13, bean.getDominioId());
                ps.setLong(14, bean.getNegocioId());
                ps.setString(15, bean.getCiudadDescripcion());
                JsonObject nombreRegional = new JsonObject();
                nombreRegional.addProperty("nombreRegional", bean.getNombreReginal() != null ? bean.getNombreReginal() : "");
                ps.setString(16, nombreRegional.toString());
                ps.executeUpdate();
            }

            void actualizarEmpresa() throws SQLException {
                String sql = "UPDATE empresas  SET razon_social=?, nit=?, localizacion=?,"
                        + " ciudades_id=?, url_foto=?, dominio_id=?, alias=?,codigo_empresa=?, negocio_id=?,ciudades_descripcion=?, telefono=?,direccion=?"
                        + " WHERE id=?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setString(1, bean.getRazonSocial() != null ? bean.getRazonSocial() : "");
                ps.setString(2, bean.getNit() != null ? bean.getNit() : "");
                ps.setString(3, bean.getLocalizacion() != null ? bean.getLocalizacion() : "");
                ps.setLong(4, bean.getCiudadId());
                ps.setString(5, bean.getUrlFotos() != null ? bean.getUrlFotos() : "");
                ps.setLong(6, bean.getDominioId());
                ps.setString(7, bean.getAlias() != null ? bean.getAlias() : "");
                ps.setString(8, bean.getCodigo() != null ? bean.getCodigo() : "");
                ps.setLong(9, bean.getNegocioId());
                ps.setString(10, bean.getCiudadDescripcion());
                ps.setString(11, bean.getTelefonoPrincipal());
                ps.setString(12, bean.getDireccionPrincipal());
                ps.setLong(13, bean.getId());
                ps.executeUpdate();
            }

            void insertarDescriptor() throws SQLException {
                String sql = "INSERT INTO descriptores( id, empresas_id, header, footer)" + " VALUES (?, ?, ?, ?);";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, bean.getDescriptorId());
                ps.setLong(2, bean.getId());
                ps.setString(3, bean.getDescriptorHeader() != null ? bean.getDescriptorHeader() : "");
                ps.setString(4, bean.getDescriptorFooter() != null ? bean.getDescriptorFooter() : "");
                ps.executeUpdate();
            }

            void actualizarDescriptor() throws SQLException {
                String sql = "UPDATE descriptores SET empresas_id=?, header=?, footer=?" + " WHERE id=?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, bean.getId());
                ps.setString(2, bean.getDescriptorHeader() != null ? bean.getDescriptorHeader() : "");
                ps.setString(3, bean.getDescriptorFooter() != null ? bean.getDescriptorFooter() : "");
                ps.setLong(4, bean.getDescriptorId());
                ps.executeUpdate();
            }

            void eliminarContactosEmpresa() throws SQLException {
                String sql = "DELETE FROM CONTACTOS WHERE EMPRESAS_ID = ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, bean.getId());
                ps.executeUpdate();
            }

            void insertarContactosEmpresa(ContactoBean contacto) throws SQLException {
                String sql = "INSERT INTO CONTACTOS("
                        + " id, empresas_id, personas_id, tipo, etiqueta, contacto, estado, principal)"
                        + " VALUES (nextval('contactos_id'), ?, null, ?, ?, ?, 'A', ?);";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, bean.getId());
                ps.setInt(2, contacto.getTipo());
                ps.setString(3, contacto.getEtiqueta().toUpperCase().trim());
                ps.setString(4, contacto.getContacto().toUpperCase().trim());
                ps.setString(5, contacto.isPrincipal() ? "S" : "N");
                ps.executeUpdate();
                if (contacto.isPrincipal() && contacto.getTipo() == NovusConstante.CONTACTO_TIPO_DIRECCION) {
                    bean.setDireccionPrincipal(contacto.getContacto());
                }
                if (contacto.isPrincipal() && contacto.getTipo() == NovusConstante.CONTACTO_TIPO_TELEFONO) {
                    bean.setTelefonoPrincipal(contacto.getContacto());
                }
            }
        }
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        Inner innerCall = new Inner();
        boolean insertado = true;
        boolean existeEmpresa;
        boolean existeDescriptores;
        try {

            existeEmpresa = innerCall.existeEmpresa();
            if (!existeEmpresa) {
                innerCall.insertarEmpresa();
            } else {
                innerCall.actualizarEmpresa();
            }

            SetupDao sdao = new SetupDao();

            sdao.insertarPerfiles(Main.dbCore, bean.getPerfilesEmpresa());

            if (bean.getContacto() != null) {
                innerCall.eliminarContactosEmpresa();
                for (ContactoBean contacto : bean.getContacto()) {
                    innerCall.insertarContactosEmpresa(contacto);
                }
            }
            existeDescriptores = innerCall.existeDescriptor();
            if (!existeDescriptores) {
                innerCall.insertarDescriptor();
            } else {
                innerCall.actualizarDescriptor();
            }
        } catch (SQLException e) {
            insertado = false;
            NovusUtils.printLn(e.getMessage());
        }

        String sql;
        PreparedStatement ps;
        try {
            sql = "INSERT INTO public.wacher_parametros\n" + "(id, codigo, tipo, valor)\n"
                    + "VALUES(2, 'PORT_PPX', 1, '9000');\n" + "INSERT INTO public.wacher_parametros\n"
                    + "(id, codigo, tipo, valor)\n"
                    + "VALUES(5, 'TIEMPO_MAXIMO_RESPUESTA_GOPASS', 3, '122000');\n"
                    + "INSERT INTO public.wacher_parametros\n" + "(id, codigo, tipo, valor)\n"
                    + "VALUES(6, 'IMPRIMIR_TIQUETE_GOPASS', 2, 'S');\n"
                    + "INSERT INTO public.wacher_parametros\n" + "(id, codigo, tipo, valor)\n"
                    + "VALUES(7, 'HOST_GOPASS', 1, 'servicesapi.gopass.com.co');\n"
                    + "INSERT INTO public.wacher_parametros\n" + "(id, codigo, tipo, valor)\n"
                    + "VALUES(9, 'PROTO_GOPASS', 1, 'https');\n" + "INSERT INTO public.wacher_parametros\n"
                    + "(id, codigo, tipo, valor)\n" + "VALUES(8, 'PORT_GOPASS', 1, '');\n"
                    + "INSERT INTO public.wacher_parametros\n" + "(id, codigo, tipo, valor)\n"
                    + "VALUES(13, 'USUARIO_GOPASS_AUT', 1, 'Ajdnjmiirgp721d20063543ceeg');\n"
                    + "INSERT INTO public.wacher_parametros\n" + "(id, codigo, tipo, valor)\n"
                    + "VALUES(14, 'CLAVE_GOPASS_AUT', 1, 'ab7e1e2b270d5aasafdgtrteb86');\n"
                    + "INSERT INTO public.wacher_parametros\n" + "(id, codigo, tipo, valor)\n"
                    + "VALUES(20, 'RUTAS_PERMITIDAS', 1, '[\n" + "  {\n" + "    \"host\": \"192.168.0.220\",\n"
                    + "    \"path\": \"/api/aperturaTurno\",\n" + "    \"port\": 8000\n" + "  },\n"
                    + "  {\n" + "    \"host\": \"localhost\",\n"
                    + "    \"path\": \"/gopass/notificacionTransaccion\",\n" + "    \"port\": 7001\n" + "  },\n"
                    + "  {\n" + "    \"host\": \"192.168.0.220\",\n" + "    \"path\": \"/api/precio\",\n"
                    + "    \"port\": 8000\n" + "  },\n" + "    {\n" + "    \"host\": \"192.168.0.220\",\n"
                    + "    \"path\": \"/api\",\n" + "    \"port\": 8000\n" + "  }\n" + "]');\n"
                    + "INSERT INTO public.wacher_parametros\n" + "(id, codigo, tipo, valor)\n"
                    + "VALUES(19, 'EXPIRATION_TRANSACCION', 1, '120000');\n"
                    + "INSERT INTO public.wacher_parametros\n" + "(id, codigo, tipo, valor)\n"
                    + "VALUES(17, 'EXPIRATION_TIME_GOPASS', 3, '20');\n"
                    + "INSERT INTO public.wacher_parametros\n" + "(id, codigo, tipo, valor)\n"
                    + "VALUES(18, 'IDENTIFICADOR_POS', 1, 'POS1');\n" + "INSERT INTO public.wacher_parametros\n"
                    + "(id, codigo, tipo, valor)\n" + "VALUES(22, 'PORT_SERVER', 3, '7008');\n"
                    + "INSERT INTO public.wacher_parametros\n" + "(id, codigo, tipo, valor)\n"
                    + "VALUES(16, 'APPS_PERMITIDAS', 4, '[\n" + "   {\n" + "      \"aplicacion\":\"GOPASS\",\n"
                    + "      \"autorizacion\":\"Basic U2FsdGVkX1+VC16nQ1/xH1d2V7hk6aXx3qoPawEyd337wQvckkv643YptgSVShDG:U2FsdGVkX1+e4b+gZbWHAgMtMnN6kGk2Cj+Cpx2dum6e59IriC2CJzqU/rR+z2UB02fGRijlpSBjJEsUvas06g==\"\n"
                    + "   }\n" + "]');\n" + "INSERT INTO public.wacher_parametros\n"
                    + "(id, codigo, tipo, valor)\n" + "VALUES(30, 'HOST_LZ', 1, 'localhost');\n"
                    + "INSERT INTO public.wacher_parametros\n" + "(id, codigo, tipo, valor)\n"
                    + "VALUES(1, 'HOST_PPX', 1, 'localhost');\n" + "INSERT INTO public.wacher_parametros\n"
                    + "(id, codigo, tipo, valor)\n" + "VALUES(33, 'HOST_VEEDER_ROOT', 1, 'localhost');\n"
                    + "INSERT INTO public.wacher_parametros\n" + "(id, codigo, tipo, valor)\n"
                    + "VALUES(4, 'IMPRIMIR_PAGO_VENTA_GOPASS', 2, 'S');\n"
                    + "INSERT INTO public.wacher_parametros\n" + "(id, codigo, tipo, valor)\n"
                    + "VALUES(27, 'TEST_AUTO_AUTORIZACION', 2, 'N');\n"
                    + "INSERT INTO public.wacher_parametros\n" + "(id, codigo, tipo, valor)\n"
                    + "VALUES(35, 'TIEMPO_ESPERA_VEEDER_ROOT', 3, '500');\n"
                    + "INSERT INTO public.wacher_parametros\n" + "(id, codigo, tipo, valor)\n"
                    + "VALUES(34, 'PORT_VEEDER_ROOT', 3, '9099');\n" + "INSERT INTO public.wacher_parametros\n"
                    + "(id, codigo, tipo, valor)\n" + "VALUES(32, 'TIEMPO_ENCUESTA_VEEDER_ROOT', 3, '5000');\n"
                    + "INSERT INTO public.wacher_parametros\n" + "(id, codigo, tipo, valor)\n"
                    + "VALUES(11, 'TIEMPO_REENVIO_PETICIONES', 2, '30000');\n"
                    + "INSERT INTO public.wacher_parametros\n" + "(id, codigo, tipo, valor)\n"
                    + "VALUES(46, 'VERSION_B', 2, '1');\n" + "INSERT INTO public.wacher_parametros\n"
                    + "(id, codigo, tipo, valor)\n" + "VALUES(36, 'HABILITAR_VEEDER_ROOT', 2, 'N');\n"
                    + "INSERT INTO public.wacher_parametros\n" + "(id, codigo, tipo, valor)\n"
                    + "VALUES(23, 'HOST_SERVER', 1, 'api.century.devitech.com.co');\n"
                    + "INSERT INTO public.wacher_parametros\n" + "(id, codigo, tipo, valor)\n"
                    + "VALUES(24, 'PROTO_SERVER', 1, 'https');\n" + "INSERT INTO public.wacher_parametros\n"
                    + "(id, codigo, tipo, valor)\n" + "VALUES(40, 'TIEMPO_VENTAS_SERVER', 3, '2000');\n"
                    + "INSERT INTO public.wacher_parametros\n" + "(id, codigo, tipo, valor)\n"
                    + "VALUES(25, 'ENABLE_SERVER_VENTAS', 2, 'S');\n" + "INSERT INTO public.wacher_parametros\n"
                    + "(id, codigo, tipo, valor)\n" + "VALUES(3, 'IMPRIMIR_ULTIMA_VENTA', 2, 'S');\n"
                    + "INSERT INTO public.wacher_parametros\n" + "(id, codigo, tipo, valor)\n"
                    + "VALUES(38, 'ENABLE_LAZO', 2, 'S');\n" + "INSERT INTO public.wacher_parametros\n"
                    + "(id, codigo, tipo, valor)\n" + "VALUES(31, 'PROTO_LZ', 1, 'http');\n"
                    + "INSERT INTO public.wacher_parametros\n" + "(id, codigo, tipo, valor)\n"
                    + "VALUES(39, 'MEDIO_PAGO_DEFAULT', 3, '1');\n" + "INSERT INTO public.wacher_parametros\n"
                    + "(id, codigo, tipo, valor)\n" + "VALUES(10, 'PROTO_PPX', 1, 'http');\n"
                    + "INSERT INTO public.wacher_parametros\n" + "(id, codigo, tipo, valor)\n"
                    + "VALUES(42, 'SURTIDOR_REMOTO', 2, 'S');\n" + "INSERT INTO public.wacher_parametros\n"
                    + "(id, codigo, tipo, valor)\n" + "VALUES(26, 'SURTIDOR_SERIAL_P', 1, 'localhost');\n"
                    + "INSERT INTO public.wacher_parametros\n" + "(id, codigo, tipo, valor)\n"
                    + "VALUES(28, 'ENABLE_PPX', 2, 'S');\n" + "INSERT INTO public.wacher_parametros\n"
                    + "(id, codigo, tipo, valor)\n" + "VALUES(41, 'MOSTRAR_ESTADOS_SURTIDOR', 2, 'N');\n"
                    + "INSERT INTO public.wacher_parametros\n" + "(id, codigo, tipo, valor)\n"
                    + "VALUES(43, 'TIEMPO_ENVIO_DESPACHO_PPX', 3, '0');\n"
                    + "INSERT INTO public.wacher_parametros\n" + "(id, codigo, tipo, valor)\n"
                    + "VALUES(44, 'IMPRIMIR_VENTA_FINALIZADA', 2, 'S');\n"
                    + "INSERT INTO public.wacher_parametros\n" + "(id, codigo, tipo, valor)\n"
                    + "VALUES(45, 'AUTORIZACION_GOPASS', 1, '');" + "INSERT INTO personas\n"
                    + "(id, usuario, clave, identificacion, tipos_identificacion_id,"
                    + " nombre, estado, correo, direccion, fecha_nacimiento, perfiles_id,"
                    + " telefono, celular, create_user, create_date, update_user, "
                    + "update_date, ciudades_id, sangre, genero, empresas_id, "
                    + "sucursales_id, sincronizado, tag)\n" + "VALUES(1, NULL, NULL, '123456789', 1,"
                    + " 'SUPER ADMIN', 'A', NULL, NULL, NULL, 1, NULL, "
                    + "NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, " + "150, NULL, 1, NULL);";
            ps = conexion.prepareStatement(sql);
            ps.executeUpdate();
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
        }
        try {
            sql = "INSERT INTO wacher_api_usuarios\n" + "(id, usuario, clave, estado, persona_id)\n"
                    + "VALUES(1, 'LAZO', '4643b4598ec4edbb08e6d4d7c50dc904', 'A', 1);";
            ps = conexion.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException e) {
            NovusUtils.printLn(e.getMessage());
        }

        String[] inserts = {
            "INSERT INTO wacher_api_modulos_usuarios\n" + "(id, usuarios_id, modulos_id, estado)\n"
            + "VALUES(27, 1, 17, 'A');",
            "INSERT INTO wacher_api_modulos_usuarios\n" + "(id, usuarios_id, modulos_id, estado)\n"
            + "VALUES(3, 1, 2, 'A');",
            "INSERT INTO wacher_api_modulos_usuarios\n" + "(id, usuarios_id, modulos_id, estado)\n"
            + "VALUES(2, 1, 1, 'A');",
            "INSERT INTO wacher_api_modulos_usuarios\n" + "(id, usuarios_id, modulos_id, estado)\n"
            + "VALUES(8, 1, 5, 'A');",
            "INSERT INTO wacher_api_modulos_usuarios\n" + "(id, usuarios_id, modulos_id, estado)\n"
            + "VALUES(25, 1, 13, 'A');",
            "INSERT INTO wacher_api_modulos_usuarios\n" + "(id, usuarios_id, modulos_id, estado)\n"
            + "VALUES(5, 1, 3, 'A');",
            "INSERT INTO wacher_api_modulos_usuarios\n" + "(id, usuarios_id, modulos_id, estado)\n"
            + "VALUES(7, 1, 4, 'A');",
            "INSERT INTO wacher_api_modulos_usuarios\n" + "(id, usuarios_id, modulos_id, estado)\n"
            + "VALUES(9, 1, 6, 'A');",
            "INSERT INTO wacher_api_modulos_usuarios\n" + "(id, usuarios_id, modulos_id, estado)\n"
            + "VALUES(11, 1, 7, 'A');",
            "INSERT INTO wacher_api_modulos_usuarios\n" + "(id, usuarios_id, modulos_id, estado)\n"
            + "VALUES(26, 1, 16, 'A');",
            "INSERT INTO wacher_api_modulos_usuarios\n" + "(id, usuarios_id, modulos_id, estado)\n"
            + "VALUES(15, 1, 10, 'I');",
            "INSERT INTO wacher_api_modulos_usuarios\n" + "(id, usuarios_id, modulos_id, estado)\n"
            + "VALUES(18, 1, 11, 'I');",
            "INSERT INTO wacher_api_modulos_usuarios\n" + "(id, usuarios_id, modulos_id, estado)\n"
            + "VALUES(32, 1, 22, 'A');",
            "INSERT INTO wacher_api_modulos_usuarios\n" + "(id, usuarios_id, modulos_id, estado)\n"
            + "VALUES(33, 1, 24, 'A');",
            "INSERT INTO wacher_api_modulos_usuarios\n" + "(id, usuarios_id, modulos_id, estado)\n"
            + "VALUES(14, 1, 9, 'A');",
            "INSERT INTO wacher_api_modulos_usuarios\n" + "(id, usuarios_id, modulos_id, estado)\n"
            + "VALUES(31, 1, 23, 'I');",
            "INSERT INTO wacher_api_modulos_usuarios\n" + "(id, usuarios_id, modulos_id, estado)\n"
            + "VALUES(30, 1, 20, 'I');",
            "INSERT INTO wacher_api_modulos_usuarios\n" + "(id, usuarios_id, modulos_id, estado)\n"
            + "VALUES(12, 1, 8, 'A');",
            "INSERT INTO wacher_api_modulos_usuarios\n" + "(id, usuarios_id, modulos_id, estado)\n"
            + "VALUES(29, 1, 19, 'A');",
            "INSERT INTO wacher_api_modulos_usuarios\n" + "(id, usuarios_id, modulos_id, estado)\n"
            + "VALUES(54, 1, 26, 'A');",
            "INSERT INTO wacher_api_modulos_usuarios\n" + "(id, usuarios_id, modulos_id, estado)\n"
            + "VALUES(45, 1, 25, 'A');",
            "INSERT INTO wacher_api_modulos_usuarios\n" + "(id, usuarios_id, modulos_id, estado)\n"
            + "VALUES(152, 1, 12, 'A');",
            "INSERT INTO wacher_api_modulos_usuarios\n" + "(id, usuarios_id, modulos_id, estado)\n"
            + "VALUES(28, 1, 18, 'I');"

        };
        for (String insert : inserts) {
            try {
                sql = insert;
                ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
            } catch (SQLException e) {
                NovusUtils.printLn(e.getMessage());
            }
        }
        return insertado;
    }

    public TreeMap<String, String[]> getDispositivos() {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        TreeMap<String, String[]> dispositivos = new TreeMap<>();
        try {
            String sql = "SELECT id, tipos, conector, interfaz, puerto, estado" + " FROM dispositivos order by id";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                String[] tipos = new String[4];
                tipos[0] = re.getString("conector");
                tipos[1] = re.getString("interfaz");
                tipos[2] = re.getString("puerto");
                tipos[3] = re.getString("estado");
                dispositivos.put(re.getString("tipos"), tipos);
            }
        } catch (SQLException ex) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dispositivos;
    }

    public void establecerParametrosInicialesCore() {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");

        try {
            int cantidad = 0;
            String sql = "SELECT count(1) FROM parametros";
            boolean hayRegistros = false;
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            if (re.next()) {
                cantidad = re.getInt(1);
                hayRegistros = true;
            }
            // Inicialización de parámetro para alerta de registro RFID
            setParametro("registro_tag_sin_turno_silencio", "N", true);
            if (!hayRegistros || cantidad <= 10) {
                try {
                    sql = "truncate parametros";
                    ps = conexion.prepareStatement(sql);
                    ps.executeUpdate();

                    sql = "ALTER TABLE public.parametros ADD CONSTRAINT parametros_unicos UNIQUE (codigo);";
                    ps = conexion.prepareStatement(sql);
                    ps.executeUpdate();
                } catch (SQLException ex) {
                }

                try {
                    sql = "INSERT INTO public.parametros (id,codigo,valor,tipo,opciones,valor_default,descripcion) VALUES\n"
                            + "	 (1,'version','9','NUMBER',NULL,NULL,'Version de la base de datos'),\n"
                            + "	 (8,'impresora','192.168.1.201','TEXT','','',''),\n"
                            + "	 (9,'impresora_puerto','9100','NUMBER',NULL,NULL,NULL),\n"
                            + "	 (10,'tipo_impresora','1','NUMBER',NULL,NULL,'1:TCP, 2:SISTEMA'),\n"
                            + "	 (11,'medio_pago_efectivo','1','NUMBER',NULL,NULL,'Cual es el id del tercero para identificar el medio de pago en efectivo'),\n"
                            + "	 (12,'ibutton_puerto','0','NUMBER',NULL,NULL,NULL),\n"
                            + "	 (13,'publicar_mqtt_activar','N','BOOLEAN',NULL,NULL,'Conectar a un servidor mqtt para publicar estados'),\n"
                            + "	 (14,'publicar_mqtt_server','tcp://localhost:1883','TEXT',NULL,NULL,NULL),\n"
                            + "	 (15,'publicar_mqtt_user','user','TEXT',NULL,NULL,NULL),\n"
                            + "	 (16,'publicar_mqtt_password','password','TEXT',NULL,NULL,NULL);\n"
                            + "INSERT INTO public.parametros (id,codigo,valor,tipo,opciones,valor_default,descripcion) VALUES\n"
                            + "	 (17,'socket_externo_activar','N','BOOLEAN',NULL,NULL,'Conectarse a un socket externo para publicar estados del surtidor'),\n"
                            + "	 (18,'debug_notificacion_interna','N','BOOLEAN','','',''),\n"
                            + "	 (19,'push_mqtt_activar','N','BOOLEAN',NULL,NULL,NULL),\n"
                            + "	 (20,'push_mqtt_server','tcp://repositorio.pilotico.co:1883','TEXT',NULL,NULL,NULL),\n"
                            + "	 (27,'lector_rfid','0','TEXT','','','Puerdo COM para el RFID versión USB, Ninguno dejar en 0 o vacio'),\n"
                            + "	 (31,'socket_notificaciones_host','localhost','TEXT','','',''),\n"
                            + "	 (32,'socket_notificaciones_port','7002','NUMBER','','',''),\n"
                            + "	 (33,'empresa_dococumento_alias','NIT','TEXT','','','Documento Defautl de la empresa'),\n"
                            + "	 (40,'recepcion_requiere_densidad','N','BOOLEAN','','','Si el combustible se recibe con densidad'),\n"
                            + "	 (50,'cantidad_digitos_s1','4','NUMBER','','','');\n"
                            + "INSERT INTO public.parametros (id,codigo,valor,tipo,opciones,valor_default,descripcion) VALUES\n"
                            + "	 (51,'tiempo_duracion_tag','30','NUMBER','','','Duracion de lectura del lector RFID, o RFIDv2'),\n"
                            + "	 (52,'tipo_autorizacion','2','NUMBER','','','METODO_AUTORIZACION_SIN_TAG = 1, METODO_AUTORIZACION_TAG_GLOBAL = 2, METODO_AUTORIZACION_TAG_CARA = 3'),\n"
                            + "	 (53,'rfid2_puerto','','TEXT','','','Puerto COM para el RFIDv2, Ninguno dejar en 0 o vacio'),\n"
                            + "	 (54,'rfid2_cara','','TEXT','','','Formato esperado: 1=C5;2=C6;3=C7;4=C8'),\n"
                            + "	 (55,'totalizador_origen','1','NUMBER','','1=BD','De donde se entregan los totalizadores'),\n"
                            + "	 (56,'imprimir_sobres','N','BOOLEAN',NULL,NULL,'Imprimir consignaciones de sobres'),\n"
                            + "	 (57,'manguera_requiere_placa','','NUMBER','','','Si las manguera requiere solicitar placa. Formato: 5,6 '),\n"
                            + "	 (58,'ibutton_cara','','NUMBER','','','Traductor de puertos a cara del surtidor. Formato: 4=C5;2=C6'),\n"
                            + "	 (59,'tiempo_borrar_autorizacion','50','NUMBER','','','Tiempo para borrar una autorización de procesos lectura ibutton o facturación'),\n"
                            + "	 (500,'solicitar_placa_impresion','S','STRING',NULL,NULL,'Requerir que sea obligarorio escribir placa para imprimir');\n"
                            + "INSERT INTO public.parametros (id,codigo,valor,tipo,opciones,valor_default,descripcion) VALUES\n"
                            + "	 (501,'solicitar_lecturas_tanques','S','STRING',NULL,NULL,'Solicitar lecturas de tanques cuando inician jornada'),\n"
                            + "	 (502,'logo','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','TEXT',NULL,NULL,'Imagen en base64 del logo de la impresion, dejar en blanco si no requiere'),\n"
                            + "	 (1000,'impresora_kcos','172.31.99.168','STRING',NULL,NULL,'Impresora usada por POSExpress');";
                    ps = conexion.prepareStatement(sql);
                    ps.executeUpdate();
                } catch (SQLException ex) {
                    Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getIdentificacionPuntoVenta() {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String mac = null;
        try {
            String sql = "SELECT valor FROM wacher_parametros WHERE codigo = 'FIDELIZACION'";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            if (re.next()) {
                try {
                    JsonObject json = Main.gson.fromJson(re.getString("valor"), JsonObject.class);
                    mac = json.get("node").getAsJsonObject().get("IDENTIFICACION_PUNTO_VENTA").getAsString();
                    if (mac != null && mac.equals("")) {
                        mac = null;
                    }
                } catch (JsonSyntaxException | SQLException e) {
                }
            }
        } catch (SQLException e) {
        }
        return mac;
    }

    public String getParametroWacher(String parametro) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String valor = null;
        try {
            String sql = "SELECT valor FROM wacher_parametros WHERE codigo = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, parametro);
            ResultSet re = ps.executeQuery();
            if (re.next()) {
                valor = re.getString("valor");
            }
        } catch (SQLException e) {
        }
        return valor;
    }

    public String getParametroCore(String parametro) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String valor = null;
        try {
            String sql = "SELECT valor FROM parametros WHERE codigo = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, parametro);
            ResultSet re = ps.executeQuery();
            if (re.next()) {
                valor = re.getString("valor");
            }
        } catch (SQLException e) {
        }
        return valor;
    }

    public JsonObject getParametrosInicio(String ibutton_cara, String ibutton_puerto, String rfid2_puerto, String rfid2_cara, String lector_rfid) {

        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        JsonObject parametros = new JsonObject();
        try {
            String sql = "SELECT * FROM parametros WHERE codigo in(?,?,?,?,?)";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, ibutton_cara);
            ps.setString(2, ibutton_puerto);
            ps.setString(3, rfid2_puerto);
            ps.setString(4, rfid2_cara);
            ps.setString(5, lector_rfid);
            ResultSet rs = ps.executeQuery();

            parametros.addProperty(ibutton_cara, "");
            parametros.addProperty(ibutton_puerto, "");
            parametros.addProperty(rfid2_puerto, "");
            parametros.addProperty(rfid2_cara, "");
            parametros.addProperty(lector_rfid, "");

            while (rs.next()) {
                parametros.addProperty("" + rs.getString("codigo") + "", rs.getString("valor"));
            }

        } catch (SQLException ex) {
            System.out.println("error en la consulta de los parametros");
        }

        return parametros;

    }

    public void restructurarBD() {
        validarVersion();
    }

    public boolean validarVersion() {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        boolean isValida = false;
        String version = "";
        String sql = "select valor from parametros where codigo = 'version_pos';";
        try ( PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                isValida = true;
                version = rs.getString("valor");
            }
            if (!isValida) {
                insertVersion();
            } else {
                updateVersion(version);
                NovusUtils.printLn("Version " + Main.APLICACION_CODE + " Base de Datos Actualizada");
            }

        } catch (SQLException s) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, s);
            NovusUtils.printLn("Error [validarVersion]" + s.getMessage());
        }
        return isValida;
    }

    public void insertVersion() {
        String version = Main.APLICACION_CODE;
        Connection conexion = Main.obtenerConexion("lazoexpresscore");

        try {
            conexion.setAutoCommit(true); // asegúrate del commit
            String sql = "INSERT INTO public.parametros(id, codigo, valor, tipo) " +
                    "VALUES((SELECT COALESCE(MAX(id), 0) FROM parametros) + 1, 'version_pos', ?, 'STRING');";
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, version);
                ps.executeUpdate();
                System.out.println("[DB] Versión insertada: " + version);
            }
        } catch (SQLException s) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, s);
        }
    }


    public void updateVersion(String version) {
        
        String versionActual = Main.APLICACION_CODE;
        NovusUtils.printLn("[updateVersion] Version actual: " + versionActual);

        if (!version.equals(versionActual)) {
            Connection conexion = Main.obtenerConexion("lazoexpresscore");

            try {
                conexion.setAutoCommit(true); // asegúrate del commit
                String sql = "UPDATE public.parametros SET valor = ? WHERE codigo = 'version_pos';";
                try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                    ps.setString(1, versionActual);
                    int filas = ps.executeUpdate();
                    System.out.println("[DB] Versión actualizada de " + version + " a " + versionActual + ". Filas afectadas: " + filas);
                }
            } catch (SQLException s) {
                Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, s);
                NovusUtils.printLn("Error al actualizar la versión de la base de datos: " + s.getMessage());
            }
        }
    }

    public void insertBD() {
        crearColumnaPin();
        cambiarColumnaVoucher();
        crearAtributosMediosPagosRegistry();
        crearTablaRecepcionCombustible();
        actualizarTablaVedderFecha();
        actualizarTablaCategorias();
        actualizarColumnasSincronizacion();
        crearColumnaFinalidad();
        crearColumnaEstado();
        createTableDetallado();
        crearColumnaTipo();
        crearTablaNotificaciones();
        deleteConstraintVentaDetalles();
        createTablePrecioEspecial();
        createFunctionPrecioEspecial();
        actualizarTablaCtBodegas();
        createFunctionGetPrecioDiferencial();
        createTableAuditoriaMediosPago();
        createTriggerCtMovimientosMediosPago();
        prcDepuracionMovimientos();
        crearTablaRemision();
        createTableDatafonosProveedores();
        creacionColumnaCodigoAdquirienteMediosPagos();
        creacionColumnaCodigoAdquirienteMediosPagosRegistry();
        CierreDeTurnoUseCase cierreDeTurnoUseCase = new CierreDeTurnoUseCase();
        cierreDeTurnoUseCase.execute();
        CierreDeTurnoConDatafonoUseCase cierreDeTurnoConDatafonoUseCase = new CierreDeTurnoConDatafonoUseCase();
        cierreDeTurnoConDatafonoUseCase.execute();
        crearEsquemaAutorizaciones();
        crearTablaEstadosAutorizaciones();
        insertarEstadosAutorizaciones();
        crearTablaAutorizacionesPos();
        crearFuncionInsertarAutorizacionRumbo();
        crearFuncionInsertarConfirmacionVentaRumbo();
        crearColumnaSincronizacionAutorizacion();
        crearFuncionConsultarReporteriaCierre();
    }

    private void crearFuncionConsultarReporteriaCierre() {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "CREATE OR REPLACE FUNCTION reporteria_cierres.fnc_consultar_cierre_dia(i_fecha character varying)\n"
                + " RETURNS json\n"
                + " LANGUAGE plpgsql\n"
                + "AS $function$\n"
                + "declare\n"
                + "	v_dia numeric;\n"
                + "	v_mes numeric;\n"
                + "	v_ano numeric;\n"
                + "	respuesta json;\n"
                + "begin\n"
                + "\n"
                + "	select extract (day from i_fecha::date) into v_dia;\n"
                + "	select extract (month from i_fecha::date) into v_mes;\n"
                + "	select extract (year from i_fecha::date) into v_ano;\n"
                + "\n"
                + "	select row_to_json(t) from (select rcd.combustible_total_subtotal \"totalVentasCombustible\",\n"
                + "	rcd.canastilla_total_ventas \"totalVentasCanastilla\",\n"
                + "	rcd.combustible_total_cantidad \"cantidadVentasCombustible\",\n"
                + "	rcd.canastilla_total_cantidad \"cantidadVentasCanastilla\",\n"
                + "	rcd.cdl_total_cantidad \"cantidadVentasCDL\",\n"
                + "	rcd.cdl_total_ventas \"totalVentasCDL\",\n"
                + "	coalesce ((\n"
                + "	select\n"
                + "		array_to_json(array_agg(row_to_json(t)))\n"
                + "		from(\n"
                + "		select\n"
                + "			rmp.medio_id id,\n"
                + "			rmp.descripcion,\n"
                + "			rmp.total total,\n"
                + "			rmp.cantidad\n"
                + "		from\n"
                + "			reporteria_cierres.rp_medio_pagos rmp\n"
                + "		inner join reporteria_cierres.rp_jornadas_cierres rjc on\n"
                + "			rjc.id = rmp.jornadas_cierres_id\n"
                + "		where\n"
                + "			rjc.cierre_dias_id = rcd.id )t),'[]' )\"ReporteMedios\"\n"
                + "from\n"
                + "	reporteria_cierres.rp_cierre_dias rcd\n"
                + "where\n"
                + "	rcd.dia = v_dia\n"
                + "	and rcd.ano = v_ano\n"
                + "	and rcd.mes = v_mes)t into respuesta;\n"
                + "\n"
                + "	return respuesta;\n"
                + "\n"
                + "end;\n"
                + "$function$;";
        try ( PreparedStatement pm = conexion.prepareStatement(sql)) {
            pm.executeUpdate();
        } catch (SQLException e) {
            NovusUtils.printLn("Error al crear fnc_consultar_cierre_dia");
        }
    }

    /**
     * 🚀 SOLUCIÓN AL ERROR: Crea la función PostgreSQL faltante
     * fnc_obtener_ventas_pendientes_impresion
     * 
     * Esta función busca ventas que están pendientes de impresión
     * dentro del intervalo de tiempo especificado.
     * 
     * PARÁMETROS:
     * - intervalo_tiempo: interval (ej: '15 minutes'::interval)
     * 
     * RETORNA:
     * - id: ID del movimiento
     * - fecha: Timestamp de la venta
     * - placa: Placa del vehículo (desde atributos JSON)
     */
    private void crearFuncionObtenerVentasPendientesImpresion() {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "CREATE OR REPLACE FUNCTION public.fnc_obtener_ventas_pendientes_impresion(intervalo_tiempo interval)\n"
                + "RETURNS TABLE(\n"
                + "    id BIGINT,\n"
                + "    fecha TIMESTAMP,\n"
                + "    placa VARCHAR\n"
                + ") \n"
                + "LANGUAGE plpgsql\n"
                + "AS $function$\n"
                + "BEGIN\n"
                + "    RETURN QUERY\n"
                + "    SELECT \n"
                + "        cm.id,\n"
                + "        cm.fecha,\n"
                + "        COALESCE((cm.atributos::json->>'placa')::varchar, '') as placa\n"
                + "    FROM ct_movimientos cm\n"
                + "    WHERE cm.pendiente_impresion = true\n"
                + "    AND cm.fecha >= (NOW() - intervalo_tiempo)\n"
                + "    AND cm.fecha <= NOW()\n"
                + "    ORDER BY cm.fecha ASC;\n"
                + "END;\n"
                + "$function$;";
        try ( PreparedStatement pm = conexion.prepareStatement(sql)) {
            pm.executeUpdate();
            System.out.println("✅ Función fnc_obtener_ventas_pendientes_impresion creada exitosamente");
        } catch (SQLException e) {
            System.err.println("❌ Error al crear fnc_obtener_ventas_pendientes_impresion: " + e.getMessage());
            NovusUtils.printLn("Error al crear fnc_obtener_ventas_pendientes_impresion");
        }
    }

    private void crearColumnaSincronizacionAutorizacion() {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "alter table transacciones add column if not exists autorizacion_sincronizada varchar(1);";
        try ( PreparedStatement pm = conexion.prepareStatement(sql)) {
            pm.executeUpdate();
        } catch (SQLException e) {
            NovusUtils.printLn("Error al crear Columna Sincronizacion Autorizacion");
        }
    }

    private void crearEsquemaAutorizaciones() {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "CREATE SCHEMA IF NOT EXISTS autorizacion AUTHORIZATION postgres;";
        try ( PreparedStatement pm = conexion.prepareStatement(sql)) {
            pm.executeUpdate();
        } catch (SQLException e) {
            NovusUtils.printLn("Error al crear Esquema Autorizaciones");
        }
    }

    private void crearTablaEstadosAutorizaciones() {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "CREATE TABLE IF NOT EXISTS autorizacion.tbl_estados (\n"
                + "id_estado serial4 NOT NULL,\n"
                + "nombre_estado varchar(50) NOT NULL,\n"
                + "fecha_creacion timestamp NOT NULL,\n"
                + "CONSTRAINT tbl_estados_pk PRIMARY KEY (id_estado)\n"
                + ");";
        try ( PreparedStatement pm = conexion.prepareStatement(sql)) {
            pm.executeUpdate();
        } catch (SQLException e) {
            NovusUtils.printLn("Error al crear tbl_estados");
        }
    }

    private void insertarEstadosAutorizaciones() {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "INSERT INTO autorizacion.tbl_estados(id_estado, nombre_estado, fecha_creacion)VALUES(1, 'ACTIVO', now()) on conflict(id_estado) do nothing;\n"
                + "INSERT INTO autorizacion.tbl_estados(id_estado, nombre_estado, fecha_creacion)VALUES(2, 'PENDIENTE', now()) on conflict(id_estado) do nothing;\n"
                + "INSERT INTO autorizacion.tbl_estados(id_estado, nombre_estado, fecha_creacion)VALUES(3, 'APLICADO', now()) on conflict(id_estado) do nothing;\n"
                + "INSERT INTO autorizacion.tbl_estados(id_estado, nombre_estado, fecha_creacion)VALUES(4, 'BLOQUEADO', now()) on conflict(id_estado) do nothing;\n"
                + "INSERT INTO autorizacion.tbl_estados(id_estado, nombre_estado, fecha_creacion)VALUES(5, 'NO NOTIFICADO', now()) on conflict(id_estado) do nothing;\n"
                + "INSERT INTO autorizacion.tbl_estados(id_estado, nombre_estado, fecha_creacion)VALUES(6, 'NOTIFICADO', now()) on conflict(id_estado) do nothing;\n"
                + "INSERT INTO autorizacion.tbl_estados(id_estado, nombre_estado, fecha_creacion)VALUES(7, 'NO USADO (LIBERADO)', now()) on conflict(id_estado) do nothing;";
        try ( PreparedStatement pm = conexion.prepareStatement(sql)) {
            pm.executeUpdate();
        } catch (SQLException e) {
            NovusUtils.printLn("Error al insertar Estados Autorizaciones");
        }
    }

    private void crearTablaAutorizacionesPos() {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "create table if not exists autorizacion.tbl_autorizaciones_pos(\n"
                + "id serial not null,\n"
                + "id_autorizacion int8 not null,\n"
                + "id_estado_autorizacion int8 not null,\n"
                + "empresa_id int8 not null,\n"
                + "equipo_id int8 not null,\n"
                + "placa varchar not null,\n"
                + "fecha_creacion timestamp not null default now(),\n"
                + "constraint tbl_autorizacion_pos_pk primary key (id),\n"
                + "constraint autorizacion_pos_estado_fk foreign key (id_estado_autorizacion) references autorizacion.tbl_estados(id_estado)\n"
                + "); ";
        try ( PreparedStatement pm = conexion.prepareStatement(sql)) {
            pm.executeUpdate();
        } catch (SQLException e) {
            NovusUtils.printLn("Error al crear tbl_autorizaciones_pos");
        }
    }

    private void crearFuncionInsertarConfirmacionVentaRumbo() {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "create or replace function fnc_insert_tbl_autorizaciones_pos(i_info_autorizacion json)\n"
                + "returns boolean\n"
                + " language plpgsql\n"
                + "as $function$\n"
                + "declare\n"
                + "begin \n"
                + "\n"
                + "insert into autorizacion.tbl_autorizaciones_pos (\n"
                + "id_autorizacion,\n"
                + "id_estado_autorizacion,\n"
                + "empresa_id,\n"
                + "equipo_id,\n"
                + "placa,\n"
                + "fecha_creacion)\n"
                + "values(\n"
                + "(i_info_autorizacion->>'id_autorizacion')::numeric,\n"
                + "5,\n"
                + "(i_info_autorizacion->>'empresa_id')::numeric,\n"
                + "(i_info_autorizacion->>'equipo_id')::numeric,\n"
                + "(i_info_autorizacion->>'placa')::varchar,\n"
                + "now());\n"
                + "\n"
                + "return true;\n"
                + "\n"
                + "exception when others then return false;\n"
                + "\n"
                + "end;\n"
                + "$function$\n"
                + ";";
        try ( PreparedStatement pm = conexion.prepareStatement(sql)) {
            pm.executeUpdate();
        } catch (SQLException e) {
            NovusUtils.printLn("Error al Crear la funcion fnc_insert_tbl_autorizaciones_pos");
        }
    }

    private void crearFuncionInsertarAutorizacionRumbo() {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "create or replace function fnc_insertar_autorizacion(i_info_autorizacion json)\n"
                + "returns boolean\n"
                + " language plpgsql\n"
                + "as $function$\n"
                + "declare\n"
                + "begin \n"
                + "insert into public.transacciones (codigo,\n"
                + "surtidor,\n"
                + "cara,\n"
                + "grado,\n"
                + "proveedores_id,\n"
                + "preventa,\n"
                + "estado,\n"
                + "documento_identificacion_cliente,\n"
                + "documento_identificacion_conductor,\n"
                + "placa_vehiculo,\n"
                + "precio_unidad,\n"
                + "porcentaje_descuento_cliente,\n"
                + "monto_maximo,\n"
                + "cantidad_maxima,\n"
                + "usado,\n"
                + "fecha_servidor,\n"
                + "fecha_creacion,\n"
                + "fecha_uso,\n"
                + "metodo_pago,\n"
                + "medio_autorizacion,\n"
                + "serial_dispositivo,\n"
                + "conductor_nombre,\n"
                + "cliente_nombre,\n"
                + "vehiculo_odometro,\n"
                + "trama,\n"
                + "codigo_tercero,\n"
                + "transaccion_sincronizada,\n"
                + "promotor_id)\n"
                + "values((i_info_autorizacion->>'codigo')::varchar,\n"
                + "(i_info_autorizacion->>'surtidor')::numeric,\n"
                + "(i_info_autorizacion->>'cara')::numeric,\n"
                + "(i_info_autorizacion->>'grado')::numeric,\n"
                + "(i_info_autorizacion->>'proveedoresId')::numeric,\n"
                + "(i_info_autorizacion->>'preventa')::boolean,\n"
                + "(i_info_autorizacion->>'estado')::varchar,\n"
                + "(i_info_autorizacion->>'documentoCliente')::varchar,\n"
                + "null,\n"
                + "(i_info_autorizacion->>'placaVehiculo')::varchar,\n"
                + "(i_info_autorizacion->>'precioUnidad')::numeric,\n"
                + "(i_info_autorizacion->>'porcentajeDescuentoCliente')::numeric,\n"
                + "(i_info_autorizacion->>'montoMaximo')::numeric,\n"
                + "(i_info_autorizacion->>'cantidadMaxima')::numeric,\n"
                + "null,\n"
                + "now(),\n"
                + "now(),\n"
                + "null,\n"
                + "(i_info_autorizacion->>'metodo_pago')::numeric,\n"
                + "(i_info_autorizacion->>'medioAutorizacion')::varchar,\n"
                + "null,\n"
                + "null,\n"
                + "(i_info_autorizacion->>'clienteNombre')::varchar,\n"
                + "(i_info_autorizacion->>'vehiculoOdometro')::varchar,\n"
                + "(i_info_autorizacion->>'trama')::varchar,\n"
                + "null,\n"
                + "'N'::bpchar,\n"
                + "(i_info_autorizacion->>'promotorId')::numeric);\n"
                + "\n"
                + "return true;\n"
                + "\n"
                + "exception when others then return false;\n"
                + "\n"
                + "end;\n"
                + "$function$\n"
                + ";";
        try ( PreparedStatement pm = conexion.prepareStatement(sql)) {
            pm.executeUpdate();
        } catch (SQLException e) {
            NovusUtils.printLn("Error al Crear la funcion fnc_insertar_autorizacion");
        }
    }

    private void crearFuncionConsultarVentaPendiente() {
        String sql = "create or replace function  fnc_consultar_venta_pendiente(i_id_movimiento numeric) \n"
                + "returns boolean \n"
                + "language plpgsql \n"
                + "as $function$ \n"
                + "declare \n"
                + "  v_es_pendiente boolean := false;\n"
                + "  v_estado numeric;\n"
                + "begin\n"
                + "  select tr.id_transaccion_estado into v_estado \n"
                + "  from ct_movimientos cm\n"
                + "  inner join datafonos.transacciones tr on tr.id_movimiento = cm.id\n"
                + "  where cm.id = i_id_movimiento and\n"
                + "        tr.id_transaccion_estado not in (2, 3, 4, 6) and\n"
                + "        cm.fecha::date >= (CURRENT_DATE - 5)::date and\n"
                + "        cm.fecha::date <= CURRENT_DATE::date;\n"
                + "\n"
                + "  if found and v_estado in (1, 5, 7) then \n"
                + "    v_es_pendiente := true;\n"
                + "  end if;\n"
                + "\n"
                + "  return v_es_pendiente; \n"
                + "\n"
                + "exception when NO_DATA_FOUND then \n"
                + "  return false;\n"
                + "end;\n"
                + "$function$;";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try ( PreparedStatement pm = conexion.prepareStatement(sql)) {
            pm.executeUpdate();
        } catch (SQLException e) {
            NovusUtils.printLn("Error al Crear la funcion fnc_consultar_venta_pendiente");
        }
    }

    private void crearTablaRemision() {
        String sql = "create table if not exists transmisiones_remision (\n"
                + "id_transmicion_remision serial\n"
                + ", request text\n"
                + ", id_movimiento int constraint transmisiones_remision_id_movimiento REFERENCES ct_movimientos (id)\n"
                + ", sincronizado int4\n"
                + ", fecha_registro timestamp default clock_timestamp()"
                + ",ano int default to_char (clock_timestamp(), 'YYYY')::int"
                + ",mes int default to_char (clock_timestamp(), 'MM')::int"
                + ",dia int default to_char (clock_timestamp(), 'DD')::int"
                + ");\n";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try ( PreparedStatement pm = conexion.prepareStatement(sql)) {
            pm.executeUpdate();
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error al crear la tabla de transmision de remisiones " + e.getMessage());
        }
    }

    /*
    private void actulizacionFuncionCierreDeTurno() {
        String sql = "update cierre_turno_excepciones "
                + " set query = 'select count(*) from datafonos.transacciones t"
                + " where id_transaccion_estado in (1,5,8)' , ind_activo = 1 "
                + " where id_cierre_turno_excepcion  = 6";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try ( PreparedStatement pm = conexion.prepareStatement(sql)) {
            pm.executeUpdate();
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error al ejecuatar la actualizacion de actulizacionFuncionCierreDeTurno()" + e.getMessage());
        }
    }*/

   /*
   private void actualizacionFuncionCierreDeTurnoConDatafono() {
        String sql = "UPDATE cierre_turno_excepciones\n"
                + "SET query = 'SELECT CASE WHEN negocio = ''KCO'' THEN ''KIOSCO'' WHEN negocio = ''CAN'' THEN ''CANASTILLA'' WHEN negocio = ''KCO-WEB'' THEN ''POS MOVIL'' WHEN negocio = ''DATAFONO'' THEN ''DATAFONO'' END AS negocio FROM lt_ventas_curso;',\n"
                + "    ind_activo = 1\n"
                + "WHERE id_cierre_turno_excepcion = 3;";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try ( PreparedStatement pm = conexion.prepareStatement(sql)) {
            pm.executeUpdate();
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error al ejecuatar la actualizacion de actulizacionFuncionCierreDeTurnoConDatafono()" + e.getMessage());
        }
    }*/

    /* No se usa
    private void crearIndiceRemision() {
        String sqlIndexx1 = "create index if not exists transmisiones_remision_idx_1 on transmisiones_remision (fecha_registro);";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try ( PreparedStatement pm = conexion.prepareStatement(sqlIndexx1)) {
            pm.executeUpdate();
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error al crear el indice de transmision remisiones " + e.getMessage() + " -> " + e.getClass().getName());
        }

        String sqlIndexx2 = "create index if not exists transmisiones_remision_idx_2 on transmisiones_remision (ano, mes, dia);";
        try ( PreparedStatement pm = conexion.prepareStatement(sqlIndexx2)) {
            pm.executeUpdate();
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error al crear el indice 2 de transmision remisiones " + e.getMessage());
        }
    }
     */

    public void createFunctionGetPrecioDiferencial() {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        NovusUtils.printLn("Creando Funcion select Precio Diferencial..");

        String sql = "CREATE OR REPLACE FUNCTION public.fnc_select_precio_diferencial(_idmovimiento numeric)\n"
                + " RETURNS numeric\n"
                + " LANGUAGE plpgsql\n"
                + "AS $function$\n"
                + "declare\n"
                + "precio numeric :=0;\n"
                + "begin\n"
                + "\n"
                + "/*select into precio\n"
                + "coalesce ((case\n"
                + "		when ((cm.atributos::json->>'precioDiferencial')) != null or \n"
                + "		((cm.atributos::json->>'precioDiferencial')) != ''\n"
                + "		then ((((cm.atributos::json->>'precioDiferencial')::json)::json)->>'valor')::int\n"
                + "		else \n"
                + "		0 end),0)\n"
                + "from\n"
                + "	ct_movimientos cm where cm.id=_idMovimiento;\n"
                + "*/\n"
                + "return precio;\n"
                + "end;\n"
                + "$function$\n"
                + ";";
        try ( PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException s) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, s);
        }
    }

    public void createFunctionPrecioEspecial() {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        NovusUtils.printLn("Creando Funcion Precio Especial..");

        String sql = "CREATE OR REPLACE FUNCTION public.fnc_select_precio_kiosco_especial(_producto numeric)\n"
                + " RETURNS numeric\n"
                + " LANGUAGE plpgsql\n"
                + "AS $function$\n"
                + "declare\n"
                + "precio numeric := null ;\n"
                + "begin\n"
                + "select pe.precio into precio from precio_especial pe where pe.estado = 'A'\n"
                + "and (select * from fnc_select_precio_kiosco(pe.fecha_inicio,pe.fecha_fin,pe.hora_inicio,pe.hora_fin))\n"
                + "and pe.dias_semana @>array[(EXTRACT('dow' FROM now()::date))::int]\n"
                + "and pe.productos_id = _producto\n"
                + "and pe.fecha_fin >= now()::date\n"
                + "and pe.fecha_inicio <= now()::date ;\n"
                + "return precio;\n"
                + "end;\n"
                + "$function$\n"
                + ";";
        try ( PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException s) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, s);
        }
    }

    public void actualizarTablaCtBodegas() {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        NovusUtils.printLn("Creando columna estado - fecha modificacion ct_bodegas");
        String sql = "alter table ct_bodegas "
                + "add column if not exists estado bpchar(1) default 'A', "
                + "add column if not exists fecha_modificacion "
                + "timestamp default now();";
        try ( PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createTablePrecioEspecial() {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        NovusUtils.printLn("Creando Tabla Precio Especial");
        String sql = "create table if not exists public.precio_especial (\n"
                + "precio_especial_id int NOT NULL,\n"
                + "productos_id int8 NOT NULL,\n"
                + "precio int8 NOT NULL,\n"
                + "fecha_inicio date NOT NULL,\n"
                + "fecha_fin date NOT NULL,\n"
                + "hora_inicio time NOT NULL,\n"
                + "hora_fin time NOT NULL,\n"
                + "dias_semana integer[] NOT NULL,\n"
                + "estado bpchar(1) NOT NULL,\n"
                + "CONSTRAINT precio_especial_pkey PRIMARY KEY (precio_especial_id),\n"
                + "CONSTRAINT precio_especial_un UNIQUE (productos_id, precio_especial_id),\n"
                + "CONSTRAINT precio_especial_fk FOREIGN KEY (productos_id) REFERENCES public.productos(id)\n"
                + ");";
        try ( PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, ex);
        }

        String sqlFuncion = "CREATE OR REPLACE FUNCTION public.fnc_select_precio_kiosco(fecha_inicio date,fecha_fin date, hora_inicio time, hora_fin time)\n"
                + " RETURNS boolean\n"
                + " LANGUAGE plpgsql\n"
                + "AS $function$\n"
                + "declare\n"
                + "is_valido boolean;\n"
                + "begin\n"
                + "	if fecha_inicio::date-fecha_fin::date = 0\n"
                + "	then\n"
                + "		is_valido:= (hora_inicio <= now()::time and hora_fin >= now()::time);\n"
                + "	else\n"
                + "		if hora_inicio < hora_fin\n"
                + "		then\n"
                + "			is_valido:= hora_inicio <= now()::time	and hora_fin >= now()::time;\n"
                + "		else\n"
                + "			is_valido:= ((hora_inicio <= now()::time	and hora_fin <= now()::time) or (hora_inicio >= now()::time	and hora_fin >= now()::time));\n"
                + "	end if;\n"
                + "		\n"
                + "	end if; \n"
                + "return is_valido;\n"
                + "end;\n"
                + "$function$\n"
                + ";";
        try ( PreparedStatement ps = conexion.prepareStatement(sqlFuncion)) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void deleteConstraintVentaDetalles() {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        NovusUtils.printLn("Eliminando Constraint ventas detalles");
        String sql = "ALTER TABLE "
                + "public.ventas_detalles "
                + "DROP constraint if exists ventas_detalles_un;";
        try ( PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void creacionColumnaCodigoAdquirienteMediosPagos() {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        NovusUtils.printLn("creando campo de codigo adquiriente en medios de pagos");
        String sql = "ALTER table public.medios_pagos ADD if not exists codigo_adquiriente varchar(30) NULL";
        try ( PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void creacionColumnaCodigoAdquirienteMediosPagosRegistry() {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        NovusUtils.printLn("creando campo de codigo adquiriente en medios de pagos");
        String sql = "ALTER table public.medios_pagos ADD if not exists codigo_adquiriente varchar(30) NULL";
        try ( PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void crearTablaNotificaciones() {

        NovusUtils.printLn(">>Creando tabla Notificaciones<<");

        Connection conexion = Main.obtenerConexion("lazoexpresscore");

        String sqlNotificaciones = "create table if not exists public.notificaciones (\n"
                + "id serial NOT NULL,\n"
                + "tipo_notificacion int NOT NULL,\n"
                + "fecha_recibido timestamp(0) NULL,\n"
                + "fecha_completado timestamp(0) NULL,\n"
                + " \"data\" json NULL,\n"
                + "CONSTRAINT notificaciones_pk PRIMARY KEY (id)\n"
                + ");";
        try ( PreparedStatement ps = conexion.prepareStatement(sqlNotificaciones)) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, ex);
        }

        String sqlTipoNotificacion = "create table if not exists public.tipo_notificacion (\n"
                + "tipo int NULL,\n"
                + "descripcion varchar NULL\n"
                + ");";
        try ( PreparedStatement ps = conexion.prepareStatement(sqlTipoNotificacion)) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, ex);
        }

        String sqlNotificacionesHist = "create table if not exists public.notificaciones_hist (\n"
                + "id serial NOT NULL,\n"
                + "tipo_notificacion int NOT NULL,\n"
                + "fecha_recibido timestamp(0) NULL,\n"
                + "fecha_completado timestamp(0) NULL default now(),\n"
                + "\"data\" json NULL,\n"
                + "CONSTRAINT notificaciones_hist_pk PRIMARY KEY (id)\n"
                + ");";
        try ( PreparedStatement ps = conexion.prepareStatement(sqlNotificacionesHist)) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, ex);
        }

        String sqlNotificacionesLogs = "create table if not exists public.notificaciones_logs (\n"
                + "id serial NOT NULL,\n"
                + "tipo_notificacion_log int NULL,\n"
                + "id_notificaciones int NULL,\n"
                + "logger varchar NULL,\n"
                + "fecha timestamp(0) NULL,\n"
                + "CONSTRAINT notificaciones_logs_pk PRIMARY KEY (id)\n"
                + ");";
        try ( PreparedStatement ps = conexion.prepareStatement(sqlNotificacionesLogs)) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, ex);
        }

        String sqlInsertTipoNotificaciones = "insert into public.tipo_notificacion (tipo, descripcion) select  1, 'ACTUALIZACION EMPLEADO' where not exists (select tipo from public.tipo_notificacion where tipo = 1);\n"
                + "insert into public.tipo_notificacion (tipo, descripcion) select 2, 'ACTUALIZACION DEL ESTABLECIMIENTO' where not exists (select tipo from tipo_notificacion where tipo = 2);\n"
                + "insert into public.tipo_notificacion (tipo, descripcion) select 3, 'ACTUALIZACION DE PRODUCTO'where not exists (select tipo from tipo_notificacion where tipo = 3);\n"
                + "insert into public.tipo_notificacion (tipo, descripcion) select 4, 'ACTUALIZACION EN CONSECUTIVO O RESOLUCION' where not exists (select tipo from tipo_notificacion where tipo = 4);\n"
                + "insert into public.tipo_notificacion (tipo, descripcion) select 5, 'CATEGORIA' where not exists (select tipo from tipo_notificacion where tipo = 5);\n"
                + "insert into public.tipo_notificacion (tipo, descripcion) select 6, 'ACTUALIZACION DE LOS MEDIOS DE PAGO' where not exists (select tipo from tipo_notificacion where tipo = 6);\n"
                + "insert into public.tipo_notificacion (tipo, descripcion) select 7, 'ACTUALIZACION EN LOS MOVIMIENTOS' where not exists (select tipo from tipo_notificacion where tipo = 7);\n"
                + "insert into public.tipo_notificacion (tipo, descripcion) select 8, 'ACTUALIZACION EN LAS BODEGAS' where not exists (select tipo from tipo_notificacion where tipo = 8);\n"
                + "insert into public.tipo_notificacion (tipo, descripcion) select 9, 'ACTUALIZACION DE LOS TANQUES'where not exists (select tipo from tipo_notificacion where tipo = 9);\n"
                + "insert into public.tipo_notificacion (tipo, descripcion) select 10, 'CREACION/ACTUALIZACION DE WACHER PARAMETROS' where not exists (select tipo from tipo_notificacion where tipo = 10);\n"
                + "insert into public.tipo_notificacion (tipo, descripcion) select 11, 'CAMBIO DE PRECIO' where not exists (select tipo from tipo_notificacion where tipo = 11);\n"
                + "insert into public.tipo_notificacion (tipo, descripcion) select 12, 'SINCRONIZACION DE DISPOSITIVOS' where not exists (select tipo from tipo_notificacion where tipo = 12);\n"
                + "insert into public.tipo_notificacion (tipo, descripcion) select 13, 'SINCRONIZACION DE INVENTARIO KARDEX' where not exists (select tipo from tipo_notificacion where tipo = 13);\n"
                + "insert into public.tipo_notificacion (tipo, descripcion) select 14, 'SINCRONIZACION DE AJUSTE INICIAL'where not exists (select tipo from tipo_notificacion where tipo = 14);\n"
                + "insert into public.tipo_notificacion (tipo, descripcion) select 15, 'SURTIDOR' where not exists (select tipo from tipo_notificacion where tipo = 15);\n"
                + "insert into public.tipo_notificacion (tipo, descripcion) select 100, 'SYNCHRONITACTION' where not exists (select tipo from tipo_notificacion where tipo = 100);\n"
                + "insert into public.tipo_notificacion (tipo, descripcion) select 101, 'MENSAJE POS'where not exists (select tipo from tipo_notificacion where tipo = 101);\n"
                + "insert into public.tipo_notificacion (tipo, descripcion) select 102, 'COMANDO ESTADO SERVICIO' where not exists (select tipo from tipo_notificacion where tipo = 102);\n"
                + "insert into public.tipo_notificacion (tipo, descripcion) select 122, 'ACTUALIZACION DATAFONOS' where not exists (select tipo from tipo_notificacion where tipo = 122);";
        try ( PreparedStatement ps = conexion.prepareStatement(sqlInsertTipoNotificaciones)) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, ex);
        }

        String sqlCreateFunctionInsert = " create or replace function fnc_insertar_notificaciones_hist()\n"
                + "returns trigger\n"
                + "LANGUAGE PLPGSQL\n"
                + "AS\n"
                + "$$\n"
                + "begin\n"
                + "INSERT INTO notificaciones_hist (tipo_notificacion,fecha_recibido,\"data\")\n"
                + "VALUES(new.tipo_notificacion, new.fecha_recibido,new.\"data\");\n"
                + "\n"
                + "return new;\n"
                + "END;\n"
                + "$$";
        try ( PreparedStatement ps = conexion.prepareStatement(sqlCreateFunctionInsert)) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, ex);
        }

        String sqlCreateTriggerInsert = "drop trigger if exists trg_insert_notificaciones_hist on public.notificaciones;  \n"
                + "create TRIGGER trg_insert_notificaciones_hist\n"
                + "after  insert\n"
                + "ON notificaciones\n"
                + "for each row \n"
                + "execute function fnc_insertar_notificaciones_hist();";
        try ( PreparedStatement ps = conexion.prepareStatement(sqlCreateTriggerInsert)) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, ex);
        }

        String sqlCreateFunctionUpdate = "create or replace function fnc_pasar_notificaciones()\n"
                + "returns trigger\n"
                + "LANGUAGE PLPGSQL\n"
                + "AS\n"
                + "$$\n"
                + "begin\n"
                + "\n"
                + "update notificaciones_hist set fecha_completado = new.fecha_completado where id = old.id;\n"
                + "\n"
                + "delete from notificaciones where id = old.id;\n"
                + "\n"
                + "return new;\n"
                + "END;\n"
                + "$$";
        try ( PreparedStatement ps = conexion.prepareStatement(sqlCreateFunctionUpdate)) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, ex);
        }

        String sqlCreateTriggerUpdate = "drop trigger if exists trg_notificaciones_hist on public.notificaciones;  \n"
                + "create TRIGGER trg_notificaciones_hist\n"
                + "after  update\n"
                + "ON notificaciones\n"
                + "for each row \n"
                + "execute function fnc_pasar_notificaciones();";
        try ( PreparedStatement ps = conexion.prepareStatement(sqlCreateTriggerUpdate)) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void actualizarTablaVedderFecha() {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try {
            String sql = "SELECT column_name FROM information_schema.columns WHERE table_name='ct_lecturas_veeder' and column_name='hora';";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            boolean existe = re.next();
            if (!existe) {
                sql = "alter table ct_lecturas_veeder rename column fecha to hora ";
                ps = conexion.prepareStatement(sql);
                ps.executeUpdate();

                sql = "alter table ct_lecturas_veeder add column fecha timestamp";
                ps = conexion.prepareStatement(sql);
                ps.executeUpdate();

                sql = "alter table ct_lecturas_veeder ALTER COLUMN hora SET DEFAULT CURRENT_TIME";
                ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void crearColumnaPin() {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try {
            String sql = "SELECT column_name FROM information_schema.columns WHERE table_name='personas' and column_name='pin';";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            boolean existe = re.next();
            if (!existe) {
                sql = "alter table personas add column pin varchar(10)";
                ps = conexion.prepareStatement(sql);
                ps.executeUpdate();

                sql = "alter table impuestos alter column valor type numeric(12,3)";
                ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void cambiarColumnaVoucher() {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try {
            String sql = " ALTER TABLE public.ct_movimientos_medios_pagos ALTER COLUMN numero_comprobante TYPE text USING numero_comprobante::text;";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }

    private void crearAtributosMediosPagosCore() {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try {
            String sql = "SELECT column_name FROM information_schema.columns WHERE table_name='medios_pagos' and column_name='atributos';";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            boolean existe = re.next();
            if (!existe) {
                sql = "alter table medios_pagos add column atributos json";
                ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void crearAtributosMediosPagosRegistry() {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        try {
            String sql = "SELECT column_name FROM information_schema.columns WHERE table_name='medios_pagos' and column_name='atributos';";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            boolean existe = re.next();
            if (!existe) {
                sql = "alter table medios_pagos add column atributos json";
                ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void crearTablaRecepcionCombustible() {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try {
            String sql = "SELECT table_name FROM information_schema.columns WHERE table_name='recepcion_combustible'";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            boolean existe = re.next();
            if (!existe) {
                sql = "CREATE TABLE public.recepcion_combustible (\n"
                        + "	id serial not null,\n"
                        + "	promotor_id int8 not null,\n"
                        + "	documento varchar(50),\n"
                        + "	placa varchar(10),\n"
                        + "	tanques_id int8 NOT NULL, \n"
                        + "	productos_id int8 NOT NULL,\n"
                        + "	cantidad int8,\n"
                        + "	fecha timestamp,\n"
                        + "	altura_inicial numeric(12,3),\n"
                        + "	volumen_inicial numeric(12,3),\n"
                        + "	agua_inicial numeric(12,3),\n"
                        + "	CONSTRAINT recepcion_combustible_pk PRIMARY KEY (id),\n"
                        + "	CONSTRAINT recepcion_combustible_ps FOREIGN KEY (promotor_id) REFERENCES public.personas(id),\n"
                        + "	CONSTRAINT recepcion_combustible_bg FOREIGN KEY (tanques_id) REFERENCES public.ct_bodegas(id),\n"
                        + "	CONSTRAINT recepcion_combustible_pd FOREIGN KEY (productos_id) REFERENCES public.productos(id)\n"
                        + ");";
                ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void actualizaParametro(String codigo, String valor, String tipo, String observaciones) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try {

            String sql = "SELECT codigo FROM parametros where codigo=? limit 1";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, codigo);
            boolean existe = ps.executeQuery().next();

            if (existe) {
                sql = "UPDATE parametros SET valor = ? WHERE codigo=?";
                ps = conexion.prepareStatement(sql);
                ps.setString(1, valor);
                ps.setString(2, codigo);
                ps.executeUpdate();
            } else {
                sql = "INSERT INTO parametros (id, codigo,valor,tipo,opciones,valor_default,descripcion) VALUES\n"
                        + "((select max(id)+1 from parametros p),?,?,?,NULL,NULL,?);";
                ps = conexion.prepareStatement(sql);
                ps.setString(1, codigo);
                ps.setString(2, valor);
                ps.setString(3, tipo);
                ps.setString(4, observaciones);
                ps.executeUpdate();

            }
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void actualizaParametroWacher(String codigo, String valor, int tipo) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try {
            String sql = "SELECT codigo FROM wacher_parametros where codigo=? limit 1";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, codigo);
            boolean existe = ps.executeQuery().next();
            if (existe) {
                sql = "UPDATE wacher_parametros SET valor = ? WHERE codigo=?";
                ps = conexion.prepareStatement(sql);
                ps.setString(1, valor);
                ps.setString(2, codigo);
                ps.executeUpdate();
            } else {
                sql = "INSERT INTO wacher_parametros (id, codigo,valor,tipo) VALUES\n"
                        + "((select max(id)+1 from wacher_parametros),?,?,?);";
                ps = conexion.prepareStatement(sql);
                ps.setString(1, codigo);
                ps.setInt(2, tipo);
                ps.setString(3, valor);
                ps.executeUpdate();

            }
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void agregarParametroWacher(String codigo, String valor, int tipo) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try {
            String sql = "SELECT codigo FROM wacher_parametros where codigo=? limit 1";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, codigo);
            boolean existe = ps.executeQuery().next();
            if (!existe) {
                sql = "INSERT INTO wacher_parametros (id, codigo,valor,tipo) VALUES\n"
                        + "((select max(id)+1 from wacher_parametros),?,?,?);";
                ps = conexion.prepareStatement(sql);
                ps.setString(1, codigo);
                ps.setInt(2, tipo);
                ps.setString(3, valor);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

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

   /* public ArrayList<ProductoBean> getProductosTanques(long familia) {
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
    }*/ 

    

    private void actualizarTablaCategorias() {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        NovusUtils.printLn(Main.ANSI_CYAN + "VALIDANDO TABLA GRUPOS");
        try {
            String sql = "SELECT column_name FROM information_schema.columns WHERE table_name='grupos' and column_name='atributos';";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            boolean existe = re.next();
            if (!existe) {
                NovusUtils.printLn(Main.ANSI_CYAN + "SE AGREGA ATRIBUTOS A LA TABLA GRUPOS");
                sql = "alter table grupos add column atributos json";
                ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    private void crearColumnaTipo() {

        String[] databases = {"lazoexpressregistry", "lazoexpresscore"};
        for (String database : databases) {
            String sql = "alter table public.perfiles add column if not exists tipo int4 null";
            Connection conexion = Main.obtenerConexion(database);
            try ( PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private void crearColumnaEstado() {
        String databases[] = {"lazoexpressregistry", "lazoexpresscore"};

        for (String database : databases) {
            try {
                Connection conexion = Main.obtenerConexion(database);
                String sql = "alter table public.impuestos add column if not exists estado varchar(1) null";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void createTableDetallado() {
        String databases[] = {"lazoexpresscore"};

        for (String database : databases) {
            try {
                Connection conexion = Main.obtenerConexion(database);
                String sql = "create table if not exists lt_consolidado_detallado (\n"
                        + "    consecutivo varchar (50)not null,\n"
                        + "    fecha varchar(40),\n"
                        + "    promotor varchar(50) not null,\n"
                        + "    producto varchar(50)not null,\n"
                        + "    cantidad numeric(12,3)not null,\n"
                        + "    subtotal numeric(12,3)not null,\n"
                        + "    impuesto numeric(12,3),\n"
                        + "    total numeric(12,3),\n"
                        + "    tipo varchar(40),\n"
                        + "    sincronizado boolean default 'false',\n"
                        + "    turno int8 not null\n"
                        + ");";
                try ( PreparedStatement ps = conexion.prepareStatement(sql)) {
                    ps.executeUpdate();
                    NovusUtils.printLn("se ha creado la tabla consolidado detallado");
                }
            } catch (SQLException ex) {
                Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void actualizarColumnasSincronizacion() {
        String consultarColumnaVentascurso = "select column_name from information_schema.columns where table_name = 'ventas_curso' and column_name = 'atributos'";
        try {
            Statement ps = dbCore.conectar().createStatement();
            ResultSet rs = ps.executeQuery(consultarColumnaVentascurso);
            boolean isExisteColumna = false;
            while (rs.next()) {
                NovusUtils.printLn("");
                isExisteColumna = true;
            }
            if (isExisteColumna) {
                NovusUtils.printLn("La columna atributos ya existe ");
            } else {
                String crearColumnaVentasCurso = "ALTER TABLE public.ventas_curso ADD atributos json";
                try {
                    PreparedStatement pst = dbCore.conectar().prepareStatement(crearColumnaVentasCurso);
                    int resultado = pst.executeUpdate();

                    NovusUtils.printLn("Creacion de columna exitosa");

                } catch (DAOException ex) {
                    NovusUtils.printLn("Error :" + ex);
                }
            }

            String consultarcolumnaCarasurtido = "select column_name from information_schema.columns where table_name = 'surtidores_detalles' and column_name = 'puerto'";
            ps = dbCore.conectar().createStatement();
            rs = ps.executeQuery(consultarcolumnaCarasurtido);
            boolean existeColumnaCaraSurtidor = rs.next();
            if (!existeColumnaCaraSurtidor) {
                String crearColumnaVentasCurso = "ALTER TABLE public.surtidores_detalles ADD puerto varchar(10) NULL";
                try {
                    PreparedStatement pst = dbCore.conectar().prepareStatement(crearColumnaVentasCurso);
                    int resultado = pst.executeUpdate();
                    NovusUtils.printLn("Creacion de columna exitosa");
                } catch (DAOException ex) {
                    NovusUtils.printLn("Error :" + ex);
                }
            } else {
                NovusUtils.printLn("La columna puerto ya existe ");
            }

            String consultarcolumnaAtributos = "select column_name from information_schema.columns where table_name = 'empresas' and column_name = 'atributos'";
            ps = dbCore.conectar().createStatement();
            rs = ps.executeQuery(consultarcolumnaAtributos);
            boolean existeConsultarcolumnaAtributos = rs.next();
            if (!existeConsultarcolumnaAtributos) {
                String crearColumnaVentasCurso = "ALTER TABLE public.empresas ADD atributos json NULL;";
                try {
                    PreparedStatement pst = dbCore.conectar().prepareStatement(crearColumnaVentasCurso);
                    int resultado = pst.executeUpdate();
                    NovusUtils.printLn("Creacion de columna exitosa");
                } catch (DAOException ex) {
                    NovusUtils.printLn("Error :" + ex);
                }
            } else {
                NovusUtils.printLn("La columna atributos ya existe ");
            }

            String sqlCrearTabla = " CREATE TABLE if not exists public.lecturas_tag (\n"
                    + "id serial NOT NULL,\n"
                    + "fecha timestamp NOT NULL,\n"
                    + "lectura text NOT NULL,\n"
                    + "CONSTRAINT lecturas_tag_pk PRIMARY KEY (id)\n"
                    + ");";
            PreparedStatement psm;
            psm = dbCore.conectar().prepareStatement(sqlCrearTabla);
            psm.execute();

        } catch (DAOException | SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void crearColumnaFinalidad() {
        String existe = "select column_name from information_schema.columns where table_name = 'bodegas' and column_name = 'finalidad'";
        try {
            Connection conexion = Main.obtenerConexion("lazoexpressregistry");
            Statement ps = conexion.createStatement();
            ResultSet rs = ps.executeQuery(existe);
            boolean isExisteColumna = false;
            while (rs.next()) {
                NovusUtils.printLn("");
                isExisteColumna = true;
            }
            if (!isExisteColumna) {
                String crearColumnaVentasCurso = "ALTER TABLE public.bodegas ADD finalidad varchar(10);";
                PreparedStatement pst = conexion.prepareStatement(crearColumnaVentasCurso);
                pst.executeUpdate();
            }

        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public JsonArray getMedidaTanque(BodegaBean tanque) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        JsonArray array = new JsonArray();
        try {
            String sql = "SELECT\n"
                    + "(atributos->>'altura_unidades_id')::numeric altura_unidades_id,\n"
                    + "(SELECT ALIAS FROM  UNIDADES WHERE ID=(atributos->>'altura_unidades_id')::numeric) altura_unidades_desc ,\n"
                    + "(atributos->>'altura_valor')::numeric  altura_valor,\n"
                    + "(atributos->>'cantidad_unidades_id')::numeric cantidad_unidades_id,\n"
                    + "(SELECT ALIAS FROM UNIDADES WHERE ID=(atributos->>'cantidad_unidades_id')::numeric) cantidad_unidades_desc ,\n"
                    + "(atributos->>'cantidad_valor')::numeric cantidad_valor\n"
                    + "FROM  CT_TABLA_AFORO  BM\n"
                    + "WHERE BM.BODEGAS_ID=? \n" //-- and (atributos->>'altura_valor')::numeric = 17
                    + "order by altura_valor asc";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, tanque.getId());
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                JsonObject json = new JsonObject();
                json.addProperty("altura_unidades_id", re.getFloat("altura_unidades_id"));
                json.addProperty("altura_unidades_desc", re.getString("altura_unidades_desc"));
                json.addProperty("cantidad_unidades_id", re.getFloat("cantidad_unidades_id"));
                json.addProperty("cantidad_unidades_desc", re.getString("cantidad_unidades_desc"));
                json.addProperty("cantidad_valor", re.getFloat("cantidad_valor"));
                json.addProperty("altura_valor", re.getFloat("altura_valor"));
                array.add(json);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return array;
    }

    public boolean existeAutorizacionCaraC(String saleAuthorizationIdentifier, int cara) {
        boolean status = false;
        try {
            Connection conexion = Main.obtenerConexion("lazoexpresscore");
            PreparedStatement ps = conexion.prepareStatement("SELECT 1 FROM TRANSACCIONES WHERE PROVEEDORES_ID=1 \n"
                    + "AND CARA=? \n"
                    + "AND CODIGO=? \n"
                    + "and trama::json->>'tipoVenta' != '1' ");
            ps.setInt(1, cara);
            ps.setString(2, saleAuthorizationIdentifier);
            ResultSet re = ps.executeQuery();
            System.out.println("---> " + re.toString());
            while (re.next()) {
                status = true;
            }
        } catch (Exception e) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, e);
        }
        return status;
    }

    public boolean existeAutorizacionCaraRumbo(String saleAuthorizationIdentifier, int cara) {
        boolean status = false;
        try {
            Connection conexion = Main.obtenerConexion("lazoexpresscore");
            PreparedStatement ps = conexion.prepareStatement("SELECT 1 FROM TRANSACCIONES WHERE PROVEEDORES_ID=3 AND CARA=? AND CODIGO=?");
            ps.setInt(1, cara);
            ps.setString(2, saleAuthorizationIdentifier);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                status = true;
            }
        } catch (Exception e) {
        }
        return status;
    }

    public boolean existeAutorizacionCaraClientePropios(String saleAuthorizationIdentifier, int cara) {
        boolean status = false;
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "SELECT 1 FROM TRANSACCIONES "
                + " WHERE PROVEEDORES_ID = 1 AND CARA = ? AND CODIGO = ? "
                + " AND trama::json->>'identificadorCupo' IS NOT NULL "
                + " AND ( CASE WHEN trama::json->>'isComunidades' = 'true' THEN false ELSE true END )";
        try ( PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, cara);
            ps.setString(2, saleAuthorizationIdentifier);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                status = true;
            }
        } catch (Exception e) {
            NovusUtils.printLn("error inesperado en el metodo  public boolean existeAutorizacionCaraClientePropios(String saleAuthorizationIdentifier, int cara) " + e.getMessage() + " ubicado en " + EquipoDao.class.getName());
        }
        return status;
    }

    public boolean existeAutorizacionCaraClientecomunidades(String saleAuthorizationIdentifier, int cara) {
        boolean status = false;
        String sql = "SELECT 1 FROM TRANSACCIONES WHERE PROVEEDORES_ID = 1"
                + " AND CARA = ? AND CODIGO = ?"
                + " AND trama::json->>'identificadorCupo' IS NULL "
                + " AND ( CASE WHEN trama::json->>'isComunidades' = 'true' THEN true ELSE false END)";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        try ( PreparedStatement ps = conexion.prepareStatement(sql);) {
            ps.setInt(1, cara);
            ps.setString(2, saleAuthorizationIdentifier);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                status = true;
            }
        } catch (Exception e) {
            NovusUtils.printLn("error inesperado en el metodo  public boolean existeAutorizacionCaraClientecomunidades(String saleAuthorizationIdentifier, int cara) " + e.getMessage() + " ubicado en " + EquipoDao.class.getName());
        }
        return status;
    }

    public JsonObject getDatosCliente(String saleAuthorizationIdentifier, int cara) {
        JsonObject datos = null;
        try {
            Connection conexion = Main.obtenerConexion("lazoexpresscore");
            PreparedStatement ps = conexion.prepareStatement("SELECT (trama::json->>'placaVehiculo') placa,(trama::json->>'nombreCliente') cliente FROM TRANSACCIONES WHERE PROVEEDORES_ID=1 AND CARA=? AND CODIGO=? and ((trama::json->>'identificadorCupo') is not null or ( CASE WHEN trama::json->>'isComunidades' = 'true' THEN true ELSE false END ))");
            ps.setInt(1, cara);
            ps.setString(2, saleAuthorizationIdentifier);
            ResultSet re = ps.executeQuery();
            NovusUtils.printLn(ps.toString());

            while (re.next()) {
                datos = new JsonObject();
                datos.addProperty("placa", re.getString(1));
                datos.addProperty("cliente", re.getString(2));
            }

            if (datos == null) {
                ps = conexion.prepareStatement("SELECT "
                        + "((trama::json->>'response')::json->>'placaVehiculo') placa,\n"
                        + "((trama::json->>'response')::json->>'nombreCliente') cliente "
                        + " FROM TRANSACCIONES WHERE PROVEEDORES_ID=3 AND CARA=? AND CODIGO=? ");
                ps.setInt(1, cara);
                ps.setString(2, saleAuthorizationIdentifier);
                NovusUtils.printLn(ps.toString());
                re = ps.executeQuery();

                while (re.next()) {
                    datos = new JsonObject();
                    datos.addProperty("placa", re.getString(1));
                    datos.addProperty("cliente", re.getString(2));
                }
            }
            NovusUtils.printLn("DATOS->" + datos.toString());
        } catch (Exception e) {
        }
        return datos;
    }

    public static String posPrincipal() {
        String host = "";
        String sql = "select valor from wacher_parametros wp where codigo = 'HOST_LAZO_PRINCIPAL'";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try ( Statement smt = conexion.createStatement()) {
            ResultSet rs = smt.executeQuery(sql);
            if (rs.next()) {
                host = rs.getString("valor");
            }
        } catch (SQLException e) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, e);
        }
        return host;
    }

    public void createTableAuditoriaMediosPago() {
        String sql = "create table if not exists auditoria_movimiento_medios_pago(\n"
                + "id serial primary key,\n"
                + "fecha_registro timestamp default now(),\n"
                + "id_movimiento int8,\n"
                + "medios_pagos json,\n"
                + "valor_total_venta numeric,\n"
                + "ind_correccion boolean default false,\n"
                + "fecha_correccion timestamp,\n"
                + "estado bpchar(1) default 'A'\n"
                + ");";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try ( Statement smt = conexion.createStatement()) {
            smt.executeUpdate(sql);
        } catch (SQLException e) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, e);
        }

        String sqlFunction = "create or replace\n"
                + "function public.fnc_insert_auditoria_medio_pago(_id_movimiento int , _medios_pagos json, _valor_total_venta numeric) \n"
                + "returns void \n"
                + "language plpgsql \n"
                + "as $function$ \n"
                + "declare \n"
                + "begin \n"
                + "	if not exists (\n"
                + "select\n"
                + "	*\n"
                + "from\n"
                + "	auditoria_movimiento_medios_pago\n"
                + "where\n"
                + "	id_movimiento = _id_movimiento) then\n"
                + "insert\n"
                + "	into public.auditoria_movimiento_medios_pago (id_movimiento, medios_pagos, valor_total_venta)\n"
                + "values(_id_movimiento, _medios_pagos, _valor_total_venta);\n"
                + "\n"
                + "else\n"
                + "\n"
                + "update public.auditoria_movimiento_medios_pago set estado='I' where id_movimiento = _id_movimiento ;\n"
                + "\n"
                + "insert\n"
                + "	into public.auditoria_movimiento_medios_pago (id_movimiento, medios_pagos, valor_total_venta)\n"
                + "values(_id_movimiento, _medios_pagos, _valor_total_venta);\n"
                + "\n"
                + "end if;\n"
                + "end;\n"
                + "$function$ \n"
                + ";";
        try ( PreparedStatement ps = conexion.prepareStatement(sqlFunction)) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createTriggerCtMovimientosMediosPago() {
        String sql = "CREATE OR REPLACE FUNCTION public.fnc_trigger_ct_movimientos_medios_pago()\n"
                + " RETURNS trigger\n"
                + " LANGUAGE plpgsql\n"
                + "AS $function$\n"
                + "declare\n"
                + "\n"
                + "	v_total_venta					float;\n"
                + "	v_sum_total_ventas_medios_pago	float;\n"
                + "	v_count_medio_pago_efectivo		int := 0;\n"
                + "\n"
                + "	v_tipo_movimiento				varchar(10);\n"
                + "\n"
                + "	r_medios_pago					record;\n"
                + "\n"
                + "	v_nombre_up						text	:= 'fnc_trigger_ct_movimientos_medios_pago';\n"
                + "	v_texto_log						text;\n"
                + "	v_state   						text;\n"
                + "	v_msg   						text;\n"
                + "    v_detail   						text;\n"
                + "    v_hint   						text;\n"
                + "    v_contex   						text;\n"
                + "  	v_error   						text;\n"
                + "  	\n"
                + "begin\n"
                + "	if (tg_op = 'INSERT') then\n"
                + "		begin \n"
                + "			select coalesce (venta_total,0), tipo\n"
                + "			  into v_total_venta, v_tipo_movimiento\n"
                + "			  from ct_movimientos\n"
                + "			 where id = new.ct_movimientos_id;\n"
                + "			\n"
                + "			select sum(valor_total)\n"
                + "			  into v_sum_total_ventas_medios_pago \n"
                + "			  from ct_movimientos_medios_pagos\n"
                + "			 where ct_movimientos_id  = new.ct_movimientos_id\n"
                + "			   and id != new.id;\n"
                + "		\n"
                + "			if ((v_sum_total_ventas_medios_pago + new.valor_total) > v_total_venta ) then \n"
                + "				v_texto_log := 'Valores de Medios de Pagos Superior a total de ventas ' || coalesce(new.ct_movimientos_id, -1) || '. Valor de Medios de Pagos ' || v_sum_total_ventas_medios_pago || '. Nuevo Valor Medio de Pago ' || new.valor_total || ' ct_medios_pagos_id: ' || new.ct_medios_pagos_id; \n"
                + "				insert into logs (nombre_up, texto_log)  values (v_nombre_up, v_texto_log);			\n"
                + "				for r_medios_pago in (select *\n"
                + "										from ct_movimientos_medios_pagos \n"
                + "									   where ct_movimientos_id = new.ct_movimientos_id\n"
                + "									    and id != new.id\n"
                + "									    order by ct_medios_pagos_id) loop \n"
                + "					if r_medios_pago.ct_medios_pagos_id = new.ct_medios_pagos_id then \n"
                + "						delete from ct_movimientos_medios_pagos where id =  new.id;\n"
                + "						v_texto_log := 'Id mov.' || new.ct_movimientos_id || ' Registro duplicado. No se puede agregar el Medio de Pago. Se supera el total de la venta (' || v_total_venta || '). Total de ventas registrado ' || v_sum_total_ventas_medios_pago || ' Nuevo valor_recibido. ' || new.valor_recibido || ' Suma de valores ' || (v_sum_total_ventas_medios_pago + new.valor_recibido); \n"
                + "					\n"
                + "					elsif (r_medios_pago.ct_medios_pagos_id in (10000, 8, 9, 79)) then -- 10000: Clientes Propios; -- 8, 9, 79 : Rumbo\n"
                + "							delete from ct_movimientos_medios_pagos where id =  new.id;\n"
                + "							v_texto_log := 'Id mov.' || new.ct_movimientos_id || ' No se puede cambiar el medio de pago. Medio de pago actual: ' || r_medios_pago.ct_medios_pagos_id; \n"
                + "						\n"
                + "					elsif (\n"
                + "							  (\n"
                + "								(r_medios_pago.ct_medios_pagos_id = 1 and new.ct_medios_pagos_id != 1) or \n"
                + "								(r_medios_pago.ct_medios_pagos_id != 1 and new.ct_medios_pagos_id = 1)\n"
                + "							  )\n"
                + "								and (r_medios_pago.valor_total = new.valor_total)\n"
                + "							\n"
                + "						  ) then \n"
                + "							\n"
                + "						update ct_movimientos_medios_pagos \n"
                + "						   set ct_medios_pagos_id = new.ct_medios_pagos_id \n"
                + "						 where ct_movimientos_id = new.ct_movimientos_id\n"
                + "						   and ct_medios_pagos_id = 1;\n"
                + "						  delete from ct_movimientos_medios_pagos where id =  new.id;\n"
                + "						v_texto_log := 'Id mov.' || new.ct_movimientos_id || ' Cambio de medio de pago. Se actualiza el medio de pago. Se supera el total de la venta (' || v_total_venta || '). Total de ventas registrado ' || v_sum_total_ventas_medios_pago || ' Nuevo valor_recibido. ' || new.valor_recibido || ' Suma de valores ' || (v_sum_total_ventas_medios_pago + new.valor_recibido); \n"
                + "					end if;				\n"
                + "					insert into logs (nombre_up, texto_log)  values (v_nombre_up, v_texto_log);				\n"
                + "				end loop;			\n"
                + "				return new;\n"
                + "			else\n"
                + "				return new;\n"
                + "			end if;\n"
                + "		exception\n"
                + "			when others then \n"
                + "				GET STACKED DIAGNOSTICS \n"
                + "					v_state   = RETURNED_SQLSTATE,\n"
                + "		            v_msg     = MESSAGE_TEXT,\n"
                + "		            v_detail  = PG_EXCEPTION_DETAIL,\n"
                + "		            v_hint    = PG_EXCEPTION_HINT;\n"
                + "					v_error	:= 'Error al consultar la familia. ' || v_state || ' ' || v_msg || ' ' || v_detail || ' ' || v_hint;\n"
                + "					insert into logs (nombre_up, texto_log) values (v_nombre_up, v_error);\n"
                + "					raise exception '% ', v_error;\n"
                + "		end;\n"
                + "	end if;\n"
                + "	\n"
                + "end;\n"
                + "\n"
                + "$function$\n"
                + ";";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try ( PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }

        String sqlTrigger = "drop trigger if exists tgr_ct_movimientos_medios_pagos on public.ct_movimientos_medios_pagos;\n"
                + "create trigger tgr_ct_movimientos_medios_pagos after\n"
                + "insert on  public.ct_movimientos_medios_pagos for each row execute function fnc_trigger_ct_movimientos_medios_pago(); ";
        try ( PreparedStatement ps = conexion.prepareStatement(sqlTrigger)) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void prcDepuracionMovimientos() {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sqlPrc = "CREATE OR REPLACE PROCEDURE public.prc_depuracion_movimientos(i_id_empresa integer)\n"
                + " LANGUAGE plpgsql\n"
                + "AS $procedure$\n"
                + "\n"
                + "declare\n"
                + "	v_fecha_hora_inicio				timestamp;\n"
                + "	v_fecha_hora_fin				timestamp;\n"
                + "	v_duracion_proceso				varchar(50);\n"
                + "	v_dias_historico				int	:= 0;\n"
                + "	v_total_movimientos				int	:= 0;\n"
                + "	v_total_movimientos_md			int	:= 0;\n"
                + "	v_total_movimientos_mp			int	:= 0;\n"
                + "	v_total_movimientos_im			int	:= 0;\n"
                + "\n"
                + "	v_total_movimientos_1			int	:= 0;\n"
                + "	v_total_movimientos_md_1		int	:= 0;\n"
                + "	v_total_movimientos_mp_1		int	:= 0;\n"
                + "	v_total_movimientos_im_1		int	:= 0;\n"
                + "\n"
                + "	v_total_movimientos_antes		json;\n"
                + "	v_total_movimientos_depurados	json;\n"
                + "	v_total_movimientos_despues		json;\n"
                + "	v_movimientos_depurados_fecha	json;\n"
                + "	v_movimientos_depurados			json;\n"
                + "	v_count_mov_eliminados			int	:= 0;\n"
                + "	c_movimientos					record;\n"
                + "	c_movimi_detalle				record;\n"
                + "	v_num_movimientos_procesados	int := 0;\n"
                + "	v_error 						text;\n"
                + "	v_num_movimientos_error			int := 0;\n"
                + "	v_observacion					varchar(255);\n"
                + "	v_estado_depuracion				varchar(50)		:= 'EXITOSA';\n"
                + "	v_state   						text;\n"
                + "    v_msg   						text;\n"
                + "    v_detail   						text;\n"
                + "    v_hint   						text;\n"
                + "    v_contex   						text;\n"
                + "    v_json_errores					json;\n"
                + "   	v_log							text;\n"
                + "	v_count_movimientos_hijos		int	:= 0;\n"
                + "	v_fecha_min_movimie_hijos		timestamp;\n"
                + "	v_fecha_max_movimie_hijos		timestamp;\n"
                + "	v_total_movimientos_hijos		int	:= 0;\n"
                + "	v_registros_eliminados			int	:= 0;\n"
                + "\n"
                + "	v_eliminacion_rango				int := 0;\n"
                + "	v_id_inicio						int	:= 1200;\n"
                + "	v_id_fin						int	:= 1600;\n"
                + "\n"
                + "	v_num_regis_depurado_md_1		int;\n"
                + "	v_num_regis_depurado_mp_1		int;\n"
                + "	v_num_regis_depurado_im_1		int;\n"
                + "	\n"
                + "	r_movimiento_hijo				record;\n"
                + "\n"
                + "	v_nombre_up			text	:= 'prc_depuracion_movimientos';\n"
                + "	v_texto_log			text;\n"
                + "\n"
                + "begin \n"
                + "	insert into logs (nombre_up, texto_log) values (v_nombre_up, 'INICIO');\n"
                + "	v_texto_log := 'Parametros de entrada  i_id_empresa: ' 			|| coalesce(i_id_empresa, -1);	\n"
                + "	insert into logs (nombre_up, texto_log) values (v_nombre_up, v_texto_log);\n"
                + "	\n"
                + "	if i_id_empresa is not null or i_id_empresa > 0 then\n"
                + "		v_fecha_hora_inicio := clock_timestamp();\n"
                + "		insert into logs (nombre_up, texto_log) values (v_nombre_up, 'v_fecha_hora_inicio: ' || v_fecha_hora_inicio);\n"
                + "	\n"
                + "		select valor::int\n"
                + "	      into v_dias_historico\n"
                + "	      from depuracion_parametro \n"
                + "	     where codigo = 'DHS';\n"
                + "		\n"
                + "		v_texto_log :=  'v_dias_historico: ' || v_dias_historico || ' fecha actual menos los dias de depuración: ' ||  (current_date - v_dias_historico);\n"
                + "	    insert into logs (nombre_up, texto_log) values (v_nombre_up, v_texto_log);\n"
                + "	   \n"
                + "	   select coalesce(count(cm.id),0)\n"
                + "	     into v_total_movimientos\n"
                + "	     from ct_movimientos 	cm \n"
                + "	    where empresas_id		= i_id_empresa\n"
                + "	      and sincronizado 		= '1';\n"
                + "	   \n"
                + "	   select coalesce(count(b.id),0)\n"
                + "	     into v_total_movimientos_md\n"
                + "	     from ct_movimientos 			a\n"
                + "	     join ct_movimientos_detalles	b on a.id = b.movimientos_id \n"
                + "	    where a.empresas_id				= i_id_empresa\n"
                + "	      and a.sincronizado 			= '1';\n"
                + "									    \n"
                + "		select coalesce(count(b.id),0)\n"
                + "	   	  into v_total_movimientos_mp\n"
                + "		  from ct_movimientos 				a\n"
                + "	     join ct_movimientos_medios_pagos	b on a.id = b.ct_movimientos_id \n"
                + "	    where a.empresas_id					= i_id_empresa\n"
                + "	      and a.sincronizado 			= '1';				   \n"
                + "		  \n"
                + "		select coalesce(count(b.id),0)\n"
                + "	     into v_total_movimientos_im\n"
                + "	     from ct_movimientos 			a\n"
                + "	     join ct_movimientos_detalles	b on a.id = b.movimientos_id  \n"
                + "	     join ct_movimientos_impuestos	c on b.id = c.movimientos_detalles_id \n"
                + "	    where a.empresas_id				= i_id_empresa\n"
                + "	      and a.sincronizado 			= '1';\n"
                + "	\n"
                + "	   	v_total_movimientos_antes:= '{\"Movimientos\":' || v_total_movimientos || ', \"Detalle\":' || v_total_movimientos_md || ', \"Medios de Pago\":' || v_total_movimientos_mp 	|| ', \"Impuestos\":' || v_total_movimientos_im ||'}';\n"
                + "	    insert into logs (nombre_up, texto_log) values (v_nombre_up, 'v_total_movimientos_antes: ' || v_total_movimientos_antes); \n"
                + "	\n"
                + "	   if v_total_movimientos is not null and v_total_movimientos > 0 then\n"
                + "		   select array_to_json(array_agg(t))\n"
                + "		     into v_movimientos_depurados_fecha\n"
                + "			  from (\n"
                + "					select (fecha::date), count(id)\n"
                + "					  from ct_movimientos 	cm \n"
                + "					 where empresas_id		= i_id_empresa\n"
                + "	      			   and fecha :: date 	<= current_date - v_dias_historico\n"
                + "	      			   and sincronizado 	= '1'\n"
                + "				  group by (fecha::date)\n"
                + "					) t;\n"
                + "		    insert into logs (nombre_up, texto_log) values (v_nombre_up, 'v_movimientos_depurados_fecha: ' || v_movimientos_depurados_fecha); \n"
                + "		    \n"
                + "		   select array_to_json(array_agg(t))\n"
                + "		     into v_movimientos_depurados\n"
                + "			  from (\n"
                + "					select id\n"
                + "					  from ct_movimientos 	cm \n"
                + "					 where empresas_id		= i_id_empresa\n"
                + "	      			   and fecha :: date 	<= current_date - v_dias_historico\n"
                + "	      			   and sincronizado 	= '1'\n"
                + "					) t;\n"
                + "		    insert into logs (nombre_up, texto_log) values (v_nombre_up, 'v_movimientos_depurados: ' || v_movimientos_depurados);\n"
                + "			\n"
                + "		   v_count_mov_eliminados	:= 0;\n"
                + "			v_total_movimientos_md	:= 0;\n"
                + "			v_total_movimientos_mp	:= 0;\n"
                + "			v_total_movimientos_im	:= 0;\n"
                + "					\n"
                + "			for c_movimientos in (select id\n"
                + "								    from ct_movimientos 	cm \n"
                + "								   where empresas_id		= i_id_empresa\n"
                + "	      			   				 and fecha :: date 		<= current_date - v_dias_historico\n"
                + "								     and sincronizado 		= '1'\n"
                + "								     --and ((v_eliminacion_rango = 1 and id between v_id_inicio and v_id_fin) or v_eliminacion_rango = 0)\n"
                + "							    order by id 				desc\n"
                + "							    	   , movimientos_id 	nulls last ) loop \n"
                + "				\n"
                + "					v_num_movimientos_procesados	:= v_num_movimientos_procesados + 1;\n"
                + "					if mod(v_num_movimientos_procesados, 100) = 00 then\n"
                + "						v_texto_log	:= 'v_num_movimientos_procesados: ' || v_num_movimientos_procesados \n"
                + "								|| ', Movimientos con errores: ' || v_num_movimientos_error \n"
                + "								|| '... ' || clock_timestamp()\n"
                + "							    || '... ' || age(clock_timestamp(), v_fecha_hora_inicio);\n"
                + "						insert into logs (nombre_up, texto_log) values (v_nombre_up, v_texto_log);\n"
                + "					end if;\n"
                + "					v_texto_log	:= 'v_num_movimientos_procesados: ' || v_num_movimientos_procesados \n"
                + "								|| ', c_movimientos.id: ' || c_movimientos.id; \n"
                + "					insert into logs (nombre_up, texto_log) values (v_nombre_up, v_texto_log);\n"
                + "					\n"
                + "					begin\n"
                + "						call prc_depuracion_movimiento (i_id_empresa			=> i_id_empresa\n"
                + "													  , i_id_movimiento 		=> c_movimientos.id\n"
                + "													  , i_dias_historico		=> v_dias_historico\n"
                + "													  , o_num_regis_depurado_mv	=> v_total_movimientos_1\n"
                + "													  , o_num_regis_depurado_md	=> v_num_regis_depurado_md_1\n"
                + "													  , o_num_regis_depurado_mp	=> v_num_regis_depurado_mp_1\n"
                + "													  , o_num_regis_depurado_im	=> v_num_regis_depurado_im_1\n"
                + "													   );\n"
                + "						v_count_mov_eliminados	:= v_count_mov_eliminados + v_total_movimientos_1;\n"
                + "						v_total_movimientos_md	:= v_total_movimientos_md + v_num_regis_depurado_md_1;\n"
                + "						v_total_movimientos_mp	:= v_total_movimientos_mp + v_num_regis_depurado_mp_1;\n"
                + "						v_total_movimientos_im	:= v_total_movimientos_im + v_num_regis_depurado_im_1;\n"
                + "						\n"
                + "						v_texto_log := '{\"Movimientos\":' || v_count_mov_eliminados || ', \"Detalle\":' || v_total_movimientos_md || ', \"Medios de Pago\":' || v_total_movimientos_mp 	|| ', \"Impuestos\":' || v_total_movimientos_im ||'}';\n"
                + "	    				insert into logs (nombre_up, texto_log) values (v_nombre_up, 'v_texto_log: '|| coalesce(v_texto_log, '{}'));\n"
                + "					exception\n"
                + "						when others then\n"
                + "							GET STACKED DIAGNOSTICS \n"
                + "							v_state   = RETURNED_SQLSTATE,\n"
                + "				            v_msg     = MESSAGE_TEXT,\n"
                + "				            v_detail  = PG_EXCEPTION_DETAIL,\n"
                + "				            v_hint    = PG_EXCEPTION_HINT;\n"
                + "				           \n"
                + "							v_num_movimientos_error	:= v_num_movimientos_error + 1;\n"
                + "							v_error	:= v_state || ' ' || v_msg || ' ' || v_detail || ' ' || v_hint;\n"
                + "							v_json_errores :=  '{\"id_movimiento\":\"'||c_movimientos.id||'\",\"error\":\"'||v_error||'\"}';\n"
                + "							\n"
                + "							RAISE WARNING 'Error. %, %, \"%\"', now(), v_num_movimientos_procesados, v_error;\n"
                + "					end;\n"
                + "			end loop;\n"
                + "			v_total_movimientos_depurados := '{\"Movimientos\":' || v_count_mov_eliminados || ', \"Detalle\":' || v_total_movimientos_md || ', \"Medios de Pago\":' || v_total_movimientos_mp 	|| ', \"Impuestos\":' || v_total_movimientos_im ||'}';\n"
                + "	    	insert into logs (nombre_up, texto_log) values (v_nombre_up, 'v_total_movimientos_depurados: '|| coalesce(v_total_movimientos_depurados, '{}'));\n"
                + "			\n"
                + "			if v_num_movimientos_error > 0 then\n"
                + "				v_estado_depuracion	:= 'CON ERRORES';\n"
                + "			end if;\n"
                + "	\n"
                + "			-------------------------  \n"
                + "			   select coalesce(count(a.id),0)\n"
                + "			     into v_total_movimientos\n"
                + "			     from ct_movimientos 	a \n"
                + "			    where empresas_id		= i_id_empresa\n"
                + "			      and a.sincronizado 	= '1';\n"
                + "			   \n"
                + "			   select coalesce(count(b.id),0)\n"
                + "			     into v_total_movimientos_md\n"
                + "			     from ct_movimientos 			a\n"
                + "			     join ct_movimientos_detalles	b on a.id = b.movimientos_id  \n"
                + "			    where a.empresas_id				= i_id_empresa\n"
                + "			      and a.sincronizado 			= '1';\n"
                + "											    \n"
                + "				select coalesce(count(b.id),0)\n"
                + "			   	  into v_total_movimientos_mp\n"
                + "				  from ct_movimientos 				a\n"
                + "			     join ct_movimientos_medios_pagos	b on a.id = b.ct_movimientos_id \n"
                + "			    where a.empresas_id					= i_id_empresa\n"
                + "			      and a.sincronizado 				= '1';				   \n"
                + "				  \n"
                + "				select coalesce(count(b.id),0)\n"
                + "			     into v_total_movimientos_im\n"
                + "			     from ct_movimientos 			a\n"
                + "			     join ct_movimientos_detalles	b on a.id = b.movimientos_id  \n"
                + "			     join ct_movimientos_impuestos	c on b.id = c.movimientos_detalles_id \n"
                + "			    where a.empresas_id				= i_id_empresa\n"
                + "			      and a.sincronizado 			= '1';\n"
                + "			    \n"
                + "			    v_total_movimientos_despues 	:= '{\"Movimientos\":' || v_total_movimientos 	|| ', \"Detalle\":' || v_total_movimientos_md || ', \"Medios de Pago\":' || v_total_movimientos_mp 	|| ', \"Impuestos\":' || v_total_movimientos_im ||'}';	  \n"
                + "			    insert into logs (nombre_up, texto_log) values (v_nombre_up, 'v_total_movimientos_despues: '|| coalesce(v_total_movimientos_despues, '{}'));\n"
                + "			\n"
                + "			   -------------------------\n"
                + "		else\n"
                + "			v_observacion 		= 'No se encontraron datos sincronizados';\n"
                + "			insert into logs (nombre_up, texto_log) values (v_nombre_up, 'v_observacion: '|| v_observacion);\n"
                + "		end if;\n"
                + "\n"
                + "	   	v_fecha_hora_fin := clock_timestamp();\n"
                + "		insert into logs (nombre_up, texto_log) values (v_nombre_up, 'v_fecha_hora_fin: '|| v_fecha_hora_fin);\n"
                + "		v_duracion_proceso	:= age(v_fecha_hora_fin, v_fecha_hora_inicio);\n"
                + "		\n"
                + "		begin\n"
                + "			insert into depuracion_log (id_empresa,						fecha_inicio, 				    fecha_fin\n"
                + "									  , duracion_proceso,				dia_historico,					total_movimientos_antes\n"
                + "									  , total_movimientos_depurados,	total_movimientos_despues,		movimientos_depurados\n"
                + "									  , num_movimientos_procesados,		num_movimientos_error,			estado_depuracion\n"
                + "									  , observacion,					json_errores)\n"
                + "								values (i_id_empresa,					v_fecha_hora_inicio,			v_fecha_hora_fin\n"
                + "									  , v_duracion_proceso,				v_dias_historico,				v_total_movimientos_antes\n"
                + "									  , v_total_movimientos_depurados,	v_total_movimientos_despues,	v_movimientos_depurados\n"
                + "									  , v_num_movimientos_procesados,	v_num_movimientos_error,		v_estado_depuracion\n"
                + "									  , v_observacion,					v_json_errores);\n"
                + "		end;\n"
                + "		\n"
                + "		v_texto_log :=  'Duracion Proceso: ' 		||  v_duracion_proceso 			|| ' Movimientos procesados: ' || v_num_movimientos_procesados\n"
                + "					 || ' Movimientos con hijos: '  || v_total_movimientos_hijos 	|| ' Movimientos eliminados: ' || v_count_mov_eliminados\n"
                + "					 || ' Movimientos con error: '  || v_num_movimientos_error error ;\n"
                + "		insert into logs (nombre_up, texto_log) values (v_nombre_up, v_texto_log);\n"
                + "	end if;\n"
                + "						   \n"
                + "end;\n"
                + "$procedure$\n"
                + ";";
        try ( PreparedStatement ps = conexion.prepareStatement(sqlPrc)) {
            ps.executeUpdate();
        } catch (SQLException s) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, s);
        }

        String sqlPrcMovimiento = "CREATE OR REPLACE PROCEDURE public.prc_depuracion_movimiento(i_id_empresa integer, i_id_movimiento integer, i_dias_historico integer, INOUT o_num_regis_depurado_mv integer, INOUT o_num_regis_depurado_md integer, INOUT o_num_regis_depurado_mp integer, INOUT o_num_regis_depurado_im integer)\n"
                + " LANGUAGE plpgsql\n"
                + "AS $procedure$\n"
                + "\n"
                + "declare\n"
                + "	v_registros_eliminados 			int := 0;\n"
                + "	v_total_movimientos				int := 0;\n"
                + "	v_total_movimientos_md			int := 0;\n"
                + "	v_total_movimientos_mp			int := 0;\n"
                + "	v_total_movimientos_im			int := 0;\n"
                + "	c_movimi_detalle				record;\n"
                + "	r_movimiento_hijo				record;\n"
                + "	v_total_movimientos_1			int := 0;\n"
                + "	v_num_regis_depurado_md_1		int := 0;\n"
                + "	v_num_regis_depurado_mp_1		int := 0;\n"
                + "	v_num_regis_depurado_im_1		int := 0;\n"
                + "\n"
                + "	v_nombre_up			text	:= 'prc_depuracion_movimiento';\n"
                + "	v_texto_log			text;\n"
                + "begin \n"
                + "	insert into logs (nombre_up, texto_log) values (v_nombre_up, 'INICIO');\n"
                + "	v_texto_log := 'Parametros de entrada  i_id_empresa: ' 			|| coalesce(i_id_empresa, -1);	\n"
                + "	insert into logs (nombre_up, texto_log) values (v_nombre_up, v_texto_log);\n"
                + "\n"
                + "	-- MEDIOS DE PAGOS\n"
                + "	delete from ct_movimientos_medios_pagos where ct_movimientos_id = i_id_movimiento;\n"
                + "	--\n"
                + "	v_registros_eliminados	:= 0;\n"
                + "	get diagnostics v_registros_eliminados := ROW_COUNT;\n"
                + "	v_total_movimientos_mp	:= v_total_movimientos_mp + v_registros_eliminados;\n"
                + "	insert into logs (nombre_up, texto_log) values (v_nombre_up, 'Se elimino medio de pagos. v_registros_eliminados: ' || v_registros_eliminados || ' v_total_movimientos_mp: ' || v_total_movimientos_mp);\n"
                + "	-- FIN MEDIOS DE PAGOS\n"
                + "	\n"
                + "	for c_movimi_detalle in (select id \n"
                + "							   from ct_movimientos_detalles \n"
                + "							  where movimientos_id =  i_id_movimiento) loop \n"
                + "		-- IMPUESTOS\n"
                + "		delete from ct_movimientos_impuestos where movimientos_detalles_id = c_movimi_detalle.id;\n"
                + "		-- \n"
                + "		v_registros_eliminados	:= 0;\n"
                + "		get diagnostics v_registros_eliminados := ROW_COUNT;\n"
                + "		v_total_movimientos_im	:= v_total_movimientos_im + v_registros_eliminados;\n"
                + "		insert into logs (nombre_up, texto_log) values (v_nombre_up, 'Se elimino impuestos. v_registros_eliminados: ' || v_registros_eliminados || ' v_total_movimientos_im: ' || v_total_movimientos_im);\n"
                + "		-- FIN IMPUESTOS\n"
                + "	\n"
                + "		-- MOVIMIENTO DETALLE\n"
                + "		delete from ct_movimientos_detalles where id = c_movimi_detalle.id; \n"
                + "		--\n"
                + "		v_registros_eliminados	:= 0;\n"
                + "		get diagnostics v_registros_eliminados := ROW_COUNT;\n"
                + "		v_total_movimientos_md	:= v_total_movimientos_md + v_registros_eliminados;\n"
                + "		insert into logs (nombre_up, texto_log) values (v_nombre_up, 'Se elimino movimiento detalle. v_registros_eliminados: ' || v_registros_eliminados || ' v_total_movimientos_md: ' || v_total_movimientos_md);\n"
                + "		-- FIN MOVIMIENTO DETALLE\n"
                + "	end loop;\n"
                + "	\n"
                + "	begin\n"
                + "		-- MOVIMIENTOS\n"
                + "		delete from ct_movimientos where id = i_id_movimiento;\n"
                + "		v_total_movimientos	:= v_total_movimientos + 1;\n"
                + "		--\n"
                + "		v_registros_eliminados	:= 0;\n"
                + "		get diagnostics v_registros_eliminados := ROW_COUNT;\n"
                + "		v_total_movimientos	:= v_total_movimientos + v_registros_eliminados;\n"
                + "		insert into logs (nombre_up, texto_log) values (v_nombre_up, 'Se elimino movimientos. v_registros_eliminados: ' || v_registros_eliminados || ' v_total_movimientos: ' || v_total_movimientos);\n"
                + "		-- FIN MOVIMIENTO\n"
                + "	exception\n"
                + "			when others then\n"
                + "				select id, fecha, tipo, sincronizado \n"
                + "				  into r_movimiento_hijo\n"
                + "				  from ct_movimientos cm \n"
                + "				 where movimientos_id = i_id_movimiento;\n"
                + "				raise notice 'r_movimiento_hijo.tipo: %, r_movimiento_hijo.sincronizado: %, r_movimiento_hijo.fecha:: date: %', r_movimiento_hijo.tipo, r_movimiento_hijo.sincronizado, r_movimiento_hijo.fecha::date;\n"
                + "			 	if (r_movimiento_hijo.tipo = '032' \n"
                + "			 	and r_movimiento_hijo.fecha:: date <= current_date - i_dias_historico\n"
                + "			 	and r_movimiento_hijo.sincronizado = '0'\n"
                + "			 	) then\n"
                + "					call prc_depuracion_movimiento (i_id_empresa			=> i_id_empresa\n"
                + "												  , i_id_movimiento 		=> r_movimiento_hijo.id\n"
                + "												  , i_dias_historico		=> i_dias_historico\n"
                + "												  , o_num_regis_depurado_mv	=> v_total_movimientos_1\n"
                + "												  , o_num_regis_depurado_md	=> v_num_regis_depurado_md_1\n"
                + "												  , o_num_regis_depurado_mp	=> v_num_regis_depurado_mp_1\n"
                + "												  , o_num_regis_depurado_im	=> v_num_regis_depurado_im_1\n"
                + "												   ); \n"
                + "			 		insert into logs (nombre_up, texto_log) values (v_nombre_up, 'Se elimino el registro hijo');\n"
                + "					v_texto_log := '{\"HIJO --- Movimientos\":' || v_total_movimientos_1 || ', \"Detalle\":' || v_num_regis_depurado_md_1 || ', \"Medios de Pago\":' || v_num_regis_depurado_mp_1 	|| ', \"Impuestos\":' || v_num_regis_depurado_im_1 ||'}';\n"
                + "					insert into logs (nombre_up, texto_log) values (v_nombre_up, 'v_texto_log: '|| coalesce(v_texto_log, '{}'));\n"
                + "			 	\n"
                + "			 		if v_total_movimientos_1 > 0 then \n"
                + "				 		delete from ct_movimientos where id = i_id_movimiento;\n"
                + "						v_total_movimientos	:= v_total_movimientos + 1;\n"
                + "					end if;\n"
                + "				end if;\n"
                + "	end;\n"
                + "	o_num_regis_depurado_mv		:= v_total_movimientos;\n"
                + "	o_num_regis_depurado_md		:= v_total_movimientos_md ;\n"
                + "	o_num_regis_depurado_mp		:= v_total_movimientos_mp;\n"
                + "	o_num_regis_depurado_im		:= v_total_movimientos_im;\n"
                + "	v_texto_log := '{\"Movimientos\":' || o_num_regis_depurado_mv || ', \"Detalle\":' || o_num_regis_depurado_md || ', \"Medios de Pago\":' || o_num_regis_depurado_mp 	|| ', \"Impuestos\":' || o_num_regis_depurado_im ||'}';\n"
                + "	insert into logs (nombre_up, texto_log) values (v_nombre_up, 'v_texto_log: '|| coalesce(v_texto_log, '{}'));\n"
                + "end;\n"
                + "$procedure$\n"
                + ";";
        try ( PreparedStatement ps = conexion.prepareStatement(sqlPrcMovimiento)) {
            ps.executeUpdate();
        } catch (SQLException s) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, s);
        }

        String sqlDepuracionLog = "CREATE TABLE if not exists public.depuracion_log (\n"
                + "	id serial NOT NULL,\n"
                + "	id_empresa int4 NULL,\n"
                + "	dia_historico int4 NULL,\n"
                + "	num_movimientos_procesados int4 NULL,\n"
                + "	num_movimientos_error int4 NULL,\n"
                + "	fecha_inicio varchar NULL,\n"
                + "	fecha_fin varchar NULL,\n"
                + "	duracion_proceso varchar NULL,\n"
                + "	movimientos_depurados varchar NULL,\n"
                + "	estado_depuracion varchar NULL,\n"
                + "	observacion varchar NULL,\n"
                + "	total_movimientos_antes varchar NULL,\n"
                + "	total_movimientos_depurados varchar NULL,\n"
                + "	total_movimientos_despues varchar NULL,\n"
                + "	json_errores varchar NULL,\n"
                + "	CONSTRAINT depuracion_log_id_pk PRIMARY KEY (id)\n"
                + ");";
        try ( PreparedStatement ps = conexion.prepareStatement(sqlDepuracionLog)) {
            ps.executeUpdate();
        } catch (SQLException s) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, s);
        }

        String sqlDepuracionParametro = "CREATE TABLE if not exists public.depuracion_parametro (\n"
                + "	id serial NOT NULL,\n"
                + "	codigo varchar(10) NOT NULL,\n"
                + "	descripcion varchar(100) NOT NULL,\n"
                + "	valor varchar(100) NOT NULL,\n"
                + "	CONSTRAINT depuracion_parametro_id_pk PRIMARY KEY (id)\n"
                + ");";
        try ( PreparedStatement ps = conexion.prepareStatement(sqlDepuracionParametro)) {
            ps.executeUpdate();
        } catch (SQLException s) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, s);
        }

        String insertParametro = "INSERT INTO public.depuracion_parametro (id, codigo, descripcion, valor) select 1, 'DHS', 'Día de Historico', '30'  where not exists (select id from depuracion_parametro where id = 1);\n"
                + "INSERT INTO public.depuracion_parametro (id, codigo, descripcion, valor) select 2, 'HDP', 'Hora depuración', '02:00'  where not exists (select id from depuracion_parametro where id = 2);";
        try ( PreparedStatement ps = conexion.prepareStatement(insertParametro)) {
            ps.executeUpdate();
        } catch (SQLException s) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, s);
        }

    }

    public JsonArray datafonosInfo() throws DAOException, SQLException {
        JsonArray datafonosInfo = new JsonArray();
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "select\n"
                + "	array_to_json(array_agg(row_to_json(t))) as data\n"
                + "from\n"
                + "	(\n"
                + "	select\n"
                + "		a.id_adquiriente,\n"
                + "		d.serial,\n"
                + "		a.descripcion as proveedor,\n"
                + "		d.plaqueta,\n"
                + "		d.codigo_terminal\n"
                + "	from\n"
                + "		datafonos.datafonos d\n"
                + "	inner join datafonos.adquirientes a on\n"
                + "		a.id_adquiriente = d.id_adquiriente\n"
                + "	where\n"
                + "		d.ind_activo = 1\n"
                + "		and d.serial is not null\n"
                + "		and a.descripcion is not null\n"
                + "		and d.plaqueta is not null \n"
                + "		and d.codigo_terminal is not null) t;";
        try ( PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Gson gson = new Gson();
                datafonosInfo = gson.fromJson(rs.getString("data"), JsonArray.class);
            }
        } catch (SQLException e) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, e);
        }
        return datafonosInfo;
    }

    public void createTableDatafonosProveedores() {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");

        String sqlProveedores = "CREATE TABLE if not exists public.ct_proveedor (\n"
                + "id serial4 NOT NULL,\n"
                + "descripcion varchar NULL,\n"
                + "nombre varchar NULL,\n"
                + "CONSTRAINT ct_proveedor_pk PRIMARY KEY (id)\n"
                + ");";
        try ( PreparedStatement ps = conexion.prepareStatement(sqlProveedores)) {
            ps.executeUpdate();
        } catch (SQLException s) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, s);
        }

        String sqlDatafonos = "CREATE TABLE if not exists public.ct_datafono (\n"
                + "id serial4 NOT NULL,\n"
                + "empresa_id int8 NULL,\n"
                + "proveedor_id int8 NULL,\n"
                + "descripcion varchar NULL,\n"
                + "serial_id varchar NULL,\n"
                + "plaqueta varchar NULL,\n"
                + "estado varchar NULL,\n"
                + "codigo_terminal varchar NULL,\n"
                + "CONSTRAINT ct_datafono_pk PRIMARY KEY (id),\n"
                + "CONSTRAINT ct_datafono_fk_1 FOREIGN KEY (empresa_id) REFERENCES public.ct_empresas(id),\n"
                + "CONSTRAINT ct_datafono_fk_2 FOREIGN KEY (proveedor_id) REFERENCES public.ct_proveedor(id)\n"
                + ");";
        try ( PreparedStatement ps = conexion.prepareStatement(sqlDatafonos)) {
            ps.executeUpdate();
        } catch (SQLException s) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, s);
        }
    }

    public String getCodigoEstacion() {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String codigoEstacion = null;
        String sql = "select\n"
                + "valor\n"
                + "from\n"
                + "wacher_parametros wp\n"
                + "where\n"
                + "wp.codigo = 'codigoBackoffice';";
        try ( PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                codigoEstacion = rs.getString("valor");
            }
        } catch (SQLException s) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, s);
        }
        return codigoEstacion;
    }

    public String getHostServer() {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String hostServer = "";
        String sql = "select valor from wacher_parametros wp where wp.codigo = 'HOST_SERVER';";
        try ( PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                hostServer = rs.getString("valor");
            }
        } catch (SQLException s) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, s);
        }
        return hostServer;
    }

    public long getTimeoutVentaRumbo() {
        long timeout = 30000;
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "select coalesce (((valor::json->'timeout')::json->>'authorization')::numeric,30) as timeout from wacher_parametros wp where wp.codigo  = 'LAZO_RUMBO';";
        try ( PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                timeout = rs.getLong("timeout") * 1000;
            }
        } catch (SQLException s) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, s);
        }
        return timeout;
    }

    public void getInformacionSurtidorIsla() {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "select surtidor , islas_id isla from surtidores s ;";
        try ( PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Main.credencial.setSurtidor(rs.getInt("surtidor"));
                Main.credencial.setIsla(rs.getInt("isla"));
            }
        } catch (SQLException s) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, s);
        }
    }

    public boolean consultaParametroIntegracion(String parametro) {
        boolean respuesta = false;
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "select valor from wacher_parametros where codigo = ?;";
        try ( PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, parametro);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                respuesta = rs.getString("valor").equalsIgnoreCase("S");
            }
        } catch (SQLException s) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, s);
        }
        return respuesta;
    }

    public String consultaParametroTipoNegocio(String parametro) {
        String respuesta = "COMB";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "select valor from wacher_parametros where codigo = ?;";
        try ( PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, parametro);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                respuesta = rs.getString("valor");
            }
        } catch (SQLException s) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, s);
        }
        return respuesta;
    }

    public int tipoVenta(String saleAuthorizationIdentifier, int cara) {
        int tipoVenta = -1;
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "SELECT trama::json->>'tipoVenta' tipo FROM TRANSACCIONES WHERE PROVEEDORES_ID=1 \n"
                + "AND CARA=?\n"
                + "AND CODIGO=? ;";
        try ( PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, cara);
            ps.setString(2, saleAuthorizationIdentifier);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                tipoVenta = re.getInt("tipo");
            }
        } catch (Exception e) {
            Logger.getLogger(EquipoDao.class.getName()).log(Level.SEVERE, null, e);
        }
        NovusUtils.printLn("Tipo Venta: " + tipoVenta);
        return tipoVenta;
    }
}
