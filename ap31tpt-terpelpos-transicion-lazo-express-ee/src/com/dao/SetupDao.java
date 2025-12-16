/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dao;

import com.WT2.appTerpel.domain.valueObject.QueryCollection;
import com.application.commons.db_utils.DatabaseConnectionManager;
import com.bean.BodegaBean;
import com.bean.CategoriaBean;
import com.bean.ConsecutivoBean;
import com.bean.ContactoBean;
import com.bean.CredencialBean;
import com.bean.DescriptorBean;
import com.bean.EmpresaBean;
import com.bean.IdentificadoresBean;
import com.bean.ImpuestosBean;
import com.bean.MediosPagosBean;
import com.bean.ModulosBean;
import com.bean.PerfilesBean;
import com.bean.PersonaBean;
import com.bean.ProductoBean;
import com.bean.Surtidor;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.firefuel.InfoViewController;
import com.firefuel.Main;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.infrastructure.cache.ProductoUpdateInterceptorLiviano;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.postgresql.util.PSQLException;
import com.application.useCases.productos.ObtenerPrecioUreaUseCase;
import com.application.useCases.wacherparametros.VerificarIntegracionUreaUseCase;
import com.application.commons.db_utils.DatabaseConnectionManager;

/**
 *
 * @author usuario
 */
public class SetupDao {
    // lista de caras usadas usado en el metodo getManguerasProductosAutorizacion
    ArrayList<Integer> carasUsadas = new ArrayList<>();
    // Interceptor LIVIANO para invalidación automática de cache
    private ProductoUpdateInterceptorLiviano productoUpdateInterceptor;


    // Constructor que inicializa el interceptor liviano
    public SetupDao() {
        try {
            this.productoUpdateInterceptor = new ProductoUpdateInterceptorLiviano();
            System.out.println(" Interceptor liviano inicializado en SetupDao");
        } catch (Exception e) {
            System.err.println(" No se pudo inicializar interceptor liviano en SetupDao: " + e.getMessage());
            this.productoUpdateInterceptor = null;
        }
    }

    public JsonArray searchConfigurationParams(String stringParams) {
        JsonArray params = null;
        DatabaseConnectionManager.DatabaseResources resources = null;
        
        try {
            String sql = "select tipo, valor, codigo from wacher_parametros where codigo in(" + stringParams + ")";
            
            // Usar DatabaseConnectionManager para crear recursos
            resources = DatabaseConnectionManager.createDatabaseResources("lazoexpresscore", sql);
            
            // Ejecutar consulta usando DatabaseConnectionManager
            DatabaseConnectionManager.executeQuery(resources);
            
            while (resources.getResultSet().next()) {
                if (params == null) {
                    params = new JsonArray();
                }
                JsonObject object = new JsonObject();
                object.addProperty("tipo", resources.getResultSet().getString("tipo"));
                object.addProperty("valor", resources.getResultSet().getString("valor"));
                object.addProperty("codigo", resources.getResultSet().getString("codigo"));
                params.add(object);
            }
//            String parametros = params.toString();
//            NovusUtils.printLn(params.getAsString());
//            NovusUtils.printLn(parametros);

        } catch (SQLException ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // Cerrar recursos usando DatabaseConnectionManager
            DatabaseConnectionManager.closeDatabaseResources(resources);
        }
        return params;
    }

    public boolean isObligatorioFE() throws DAOException {
        boolean isObligatorio = false;
        DatabaseConnectionManager.DatabaseResources resources = null;
        
        try {
            String sql = "SELECT VALOR FROM WACHER_PARAMETROS WHERE CODIGO = 'OBLIGATORIO_FE'";
            
            // Usar DatabaseConnectionManager para crear recursos
            resources = DatabaseConnectionManager.createDatabaseResources("lazoexpresscore", sql);
            
            // Ejecutar consulta usando DatabaseConnectionManager
            DatabaseConnectionManager.executeQuery(resources);
            
            if (resources.getResultSet().next()) {
                isObligatorio = resources.getResultSet().getString(1).equals("S");
            }
        } catch (SQLException e) {
            NovusUtils.printLn(e.getMessage());
        } finally {
            // Cerrar recursos usando DatabaseConnectionManager
            DatabaseConnectionManager.closeDatabaseResources(resources);
        }
        return isObligatorio;
    }

    public JsonObject parametrosFE() {
        JsonObject parametros = new JsonObject();
        DatabaseConnectionManager.DatabaseResources resources = null;
        
        try {
            // Una sola consulta para ambos parámetros - más eficiente
            String sql = "SELECT codigo, valor FROM wacher_parametros WHERE codigo IN ('MONTO_MINIMO_FE', 'MODULO_FACTURACION_ELECTRONICA')";
            
            // Usar DatabaseConnectionManager para crear recursos
            resources = DatabaseConnectionManager.createDatabaseResources("lazoexpresscore", sql);
            
            // Ejecutar consulta usando DatabaseConnectionManager
            DatabaseConnectionManager.executeQuery(resources);
            
            while (resources.getResultSet().next()) {
                String codigo = resources.getResultSet().getString("codigo");
                String valor = resources.getResultSet().getString("valor");
                
                if ("MONTO_MINIMO_FE".equals(codigo)) {
                    parametros.addProperty("montoMinimo", Float.parseFloat(valor));
                } else if ("MODULO_FACTURACION_ELECTRONICA".equals(codigo)) {
                    parametros.addProperty("activar_modulo", valor);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error en la consulta de los parametros de la FE");
        } catch (NumberFormatException ex) {
            System.out.println("Error al convertir monto mínimo FE a número");
        } finally {
            // Cerrar recursos usando DatabaseConnectionManager
            DatabaseConnectionManager.closeDatabaseResources(resources);
        }

        return parametros;
    }

    public boolean liberarProductos(long id) throws DAOException {
        Connection conexion = Main.obtenerConexionAsync("lazoexpressregistry");
        boolean liberado = true;
        try {
            String sql = "UPDATE PRODUCTOS SET ESTADO='A' WHERE id=?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException s) {
            NovusUtils.printLn(s.getMessage());
            liberado = false;
        } catch (Exception s) {
            NovusUtils.printLn(s.getMessage());
            liberado = false;
        } finally {
            // Cerrar conexion para evitar memory leaks
            if (conexion != null) {
                try {
                    if (!conexion.isClosed()) {
                        conexion.close();
                        //NovusUtils.printLn("CONEXION CERRADA: SetupDao.liberarProductos()");
                    }
                } catch (SQLException e) {
                    Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, "Error cerrando conexion en liberarProductos", e);
                }
            }
        }
        return liberado;
    }

    public boolean liberarProductosCore(long id) throws DAOException {
        Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");
        boolean liberado = true;
        try {
            String sql = "UPDATE PRODUCTOS SET ESTADO='A' WHERE id=?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException s) {
            NovusUtils.printLn(s.getMessage());
            liberado = false;
        } catch (Exception s) {
            NovusUtils.printLn(s.getMessage());
            liberado = false;
        } finally {
            // Cerrar conexion para evitar memory leaks
            if (conexion != null) {
                try {
                    if (!conexion.isClosed()) {
                        conexion.close();
                        //NovusUtils.printLn("CONEXION CERRADA: SetupDao.liberarProductosCore()");
                    }
                } catch (SQLException e) {
                    Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, "Error cerrando conexion en liberarProductosCore", e);
                }
            }
        }
        return liberado;
    }

    public void limpiarDatosSurtidores() {
        class Inner {
        }
        Inner innerCall = new Inner();
    }

    public TreeMap<Long, BodegaBean> obtenerTanques() {
        Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");
        TreeMap<Long, BodegaBean> tanques = new TreeMap<>();
        try {
            String sql = "SELECT ID,bodega as descripcion, codigo,atributos::json->>'dimensiones' as dimensiones  from ct_bodegas WHERE atributos::json->>'estado'='A'";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                BodegaBean tanque = new BodegaBean();
                tanque.setId(re.getLong("ID"));
                tanque.setDescripcion(re.getString("DESCRIPCION"));
                tanque.setCodigo(re.getString("CODIGO"));
                tanque.setDimension(re.getString("dimensiones"));
                tanques.put(tanque.getId(), tanque);
            }
        } catch (SQLException s) {
            NovusUtils.printLn(s.getMessage());
        } catch (Exception s) {
            NovusUtils.printLn(s.getMessage());
        }
        return tanques;
    }

    public DescriptorBean getDescriptores(long empresasId) throws DAOException {
        Connection conexion = Main.obtenerConexionAsync("lazoexpressregistry");
        DescriptorBean descriptor = null;
        try {
            String sql = "SELECT id, empresas_id, header, footer, subheader\n"
                    + "  FROM descriptores WHERE empresas_id = ? LIMIT 1";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, empresasId);
            ResultSet re = ps.executeQuery();

            if (re.next()) {
                descriptor = new DescriptorBean();
                descriptor.setHeader(re.getString("header"));
                descriptor.setFooter(re.getString("footer"));
            }

        } catch (SQLException s) {
            NovusUtils.printLn(s.getMessage());
        } catch (Exception s) {
            NovusUtils.printLn(s.getMessage());
        }
        return descriptor;
    }

    public TreeMap<Long, Surtidor> getMangueras() {
        TreeMap<Long, Surtidor> surtidor = new TreeMap<>();
        DatabaseConnectionManager.DatabaseResources resources = null;

        try {
            String sql = "select\n"
                    .concat("sur.*,\n")
                    .concat("p.descripcion,\n")
                    .concat("pf.codigo familia_descripcion,\n")
                    .concat("p.precio,\n")
                    .concat("pf.id familia_id\n")
                    .concat("from\n")
                    .concat("surtidores_detalles sur\n")
                    .concat("inner join productos p on\n")
                    .concat("p.id = sur.productos_id\n")
                    .concat("inner join productos_familias pf on\n")
                    .concat("p.familias = pf.id\n")
                    .concat("where\n")
                    .concat("cara not in( select cara from surtidores_detalles sur inner join productos p on p.id = sur.productos_id inner join productos_familias pf on p.familias = pf.id where sur.estado_publico <> 100 group by 1) and \n")
                    .concat("sur.estado_publico = 100");

            resources = DatabaseConnectionManager.executeCompleteQuery("lazoexpresscore", sql);
            while (resources.getResultSet().next()) {
                Surtidor sur = new Surtidor();
                sur.setSurtidor((int) resources.getResultSet().getLong("surtidor"));
                sur.setCara((int) resources.getResultSet().getLong("cara"));
                sur.setManguera((int) resources.getResultSet().getLong("manguera"));
                sur.setGrado((int) resources.getResultSet().getLong("grado"));
                sur.setProductoIdentificador(resources.getResultSet().getLong("productos_id"));
                sur.setProductoPrecio(resources.getResultSet().getFloat("precio"));
                sur.setProductoDescripcion(resources.getResultSet().getString("descripcion"));
                try {
                    sur.setMotivoBloqueo(resources.getResultSet().getString("motivo_bloqueo"));
                    if (resources.getResultSet().getString("bloqueo") != null) {
                        sur.setBloqueo(resources.getResultSet().getString("bloqueo").equals("S"));
                    } else {
                        sur.setBloqueo(false);
                    }
                } catch (SQLException e) {
                    sur.setBloqueo(false);
                    sur.setMotivoBloqueo("");
                }
                sur.setFamiliaDescripcion(resources.getResultSet().getString("familia_descripcion"));
                sur.setFamiliaIdentificador(resources.getResultSet().getLong("familia_id"));
                surtidor.put((long) sur.getManguera(), sur);
            }

            if (new VerificarIntegracionUreaUseCase().execute()) {
                Surtidor sur = infoProductoUrea();
                surtidor.put((long) sur.getManguera(), sur);
            }

        } catch (SQLException s) {
            NovusUtils.printLn(s.getMessage());
        } catch (Exception s) {
            NovusUtils.printLn(s.getMessage());
        } finally {
            // Asegurarse de cerrar los recursos
            DatabaseConnectionManager.closeDatabaseResources(resources);
        }
        return surtidor;
    }

    public Surtidor infoProductoUrea() {
        Surtidor sur = new Surtidor();
        sur.setSurtidor(1);
        sur.setCara(1);
        sur.setManguera(99);
        sur.setProductoIdentificador(9999);
        sur.setProductoPrecio(new ObtenerPrecioUreaUseCase().execute());
        sur.setProductoDescripcion(NovusConstante.PRODUCTO_UREA);
        sur.setMotivoBloqueo(null);
        sur.setBloqueo(false);
        sur.setFamiliaDescripcion("UREA");
        sur.setFamiliaIdentificador(NovusConstante.CODIGO_FAMILIA_PRODUCTO_UREA);
        return sur;
    }

    public TreeMap<Long, Surtidor> getManguerasAppRumbo(int idFamilia) {
        Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");
        TreeMap<Long, Surtidor> surtidor = new TreeMap<>();
        try {
            String sql = "select\n"
                    + "sur.*,\n"
                    + "p.descripcion,\n"
                    + "pf.codigo familia_descripcion,\n"
                    + "p.precio,\n"
                    + "pf.id familia_id\n"
                    + "from\n"
                    + "surtidores_detalles sur\n"
                    + "inner join productos p on\n"
                    + "p.id = sur.productos_id\n"
                    + "inner join productos_familias pf on\n"
                    + "p.familias = pf.id\n"
                    + "where\n"
                    + "cara not in( select cara from surtidores_detalles sur inner join productos p on p.id = sur.productos_id inner join productos_familias pf on p.familias = pf.id where sur.estado_publico <> 100 group by 1) and \n"
                    + "sur.estado_publico = 100 and pf.id = ?;";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, idFamilia);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                Surtidor sur = new Surtidor();
                sur.setSurtidor((int) re.getLong("surtidor"));
                sur.setCara((int) re.getLong("cara"));
                sur.setManguera((int) re.getLong("manguera"));
                sur.setGrado((int) re.getLong("grado"));
                sur.setProductoIdentificador(re.getLong("productos_id"));
                sur.setProductoPrecio(re.getFloat("precio"));
                sur.setProductoDescripcion(re.getString("descripcion"));
                try {
                    sur.setMotivoBloqueo(re.getString("motivo_bloqueo"));
                    if (re.getString("bloqueo") != null) {
                        sur.setBloqueo(re.getString("bloqueo").equals("S"));
                    } else {
                        sur.setBloqueo(false);
                    }
                } catch (SQLException e) {
                    sur.setBloqueo(false);
                    sur.setMotivoBloqueo("");
                }
                sur.setFamiliaDescripcion(re.getString("familia_descripcion"));
                sur.setFamiliaIdentificador(re.getLong("familia_id"));
                surtidor.put((long) sur.getManguera(), sur);
            }
        } catch (SQLException s) {
            NovusUtils.printLn(s.getMessage());
        } catch (Exception s) {
            NovusUtils.printLn(s.getMessage());
        }
        return surtidor;
    }

    public boolean getManguerasOcupadas() {
        boolean ocupadas;
        Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");
        try {
            String sql = "select\n"
                    + "	sur.*,\n"
                    + "	p.descripcion,\n"
                    + "	pf.codigo familia_descripcion,\n"
                    + "	p.precio,\n"
                    + "	pf.id familia_id\n"
                    + "from\n"
                    + "	surtidores_detalles sur\n"
                    + "inner join productos p on\n"
                    + "	p.id = sur.productos_id\n"
                    + "inner join productos_familias pf on\n"
                    + "	p.familias = pf.id where sur.estado_publico != 100";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            ocupadas = re.next();
        } catch (SQLException s) {
            ocupadas = false;
            NovusUtils.printLn(s.getMessage());
        }
        return ocupadas;
    }

    public boolean getSaltoLectura() {
        boolean isSalto = false;
        Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");
        try {
            String sql = "select salto_lectura\n"
                    + "from surtidores_detalles\n"
                    + "where salto_lectura = 'S'";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            isSalto = re.next();
        } catch (SQLException s) {
            NovusUtils.printLn(s.getMessage());
        }

        return isSalto;
    }

    public Surtidor getSurtidorManguera(int cara, int manguera) {
        Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");
        Surtidor sur = null;
        try {
            String sql = "SELECT sur.* FROM surtidores_detalles sur WHERE cara = ? AND manguera = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, cara);
            ps.setInt(2, manguera);
            ResultSet re = ps.executeQuery();
            if (re.next()) {
                sur = new Surtidor();
                sur.setId((int) re.getLong("id"));
                sur.setSurtidor((int) re.getLong("surtidor"));
                sur.setCara((int) re.getLong("cara"));
                sur.setManguera((int) re.getLong("manguera"));
                sur.setGrado((int) re.getLong("grado"));
            }
        } catch (SQLException s) {
            NovusUtils.printLn(s.getMessage());
        }

        return sur;
    }

    public TreeMap<Long, Surtidor> getCaras() {
        TreeMap<Long, Surtidor> surtidor = new TreeMap<>();
        DatabaseConnectionManager.DatabaseResources resources = null;

        try {
            String sql = "SELECT sur.*, p.descripcion, p.precio, pf.codigo familia_descripcion, pf.id  familia_id FROM surtidores_detalles sur\n"
                    + "INNER JOIN productos p on p.id = sur.productos_id \n"
                    + "INNER JOIN productos_familias pf on p.familias = pf.id \n"
                    + "INNER JOIN surtidores s \n"
                    + "on sur.surtidores_id = s.id \n"
                    + "WHERE s.estado='A'";

            resources = DatabaseConnectionManager.executeCompleteQuery("lazoexpresscore", sql);
            while (resources.getResultSet().next()) {
                Surtidor sur = new Surtidor();
                sur.setId((int) resources.getResultSet().getLong("id"));
                sur.setSurtidorId((int) resources.getResultSet().getLong("surtidores_id"));
                sur.setSurtidor((int) resources.getResultSet().getLong("surtidor"));
                sur.setCara((int) resources.getResultSet().getLong("cara"));
                sur.setManguera((int) resources.getResultSet().getLong("manguera"));
                sur.setGrado((int) resources.getResultSet().getLong("grado"));
                sur.setProductoIdentificador(resources.getResultSet().getLong("productos_id"));
                sur.setProductoDescripcion(resources.getResultSet().getString("descripcion"));
                sur.setProductoPrecio(resources.getResultSet().getFloat("precio"));
                try {
                    sur.setMotivoBloqueo(resources.getResultSet().getString("motivo_bloqueo"));
                    String bloqueo = resources.getResultSet().getString("bloqueo");
                    sur.setBloqueo(bloqueo != null ? bloqueo.equals("S") : false);
                } catch (Exception e) {
                    sur.setBloqueo(false);
                    sur.setMotivoBloqueo("");
                    NovusUtils.printLn(e.getMessage());
                }
                sur.setFamiliaDescripcion(resources.getResultSet().getString("familia_descripcion"));
                sur.setFamiliaIdentificador(resources.getResultSet().getLong("familia_id"));
                surtidor.put((long) resources.getResultSet().getLong("id"), sur);
            }
        } catch (SQLException s) {
            NovusUtils.printLn(s.getMessage());
        } catch (Exception s) {
            NovusUtils.printLn(s.getMessage());
        } finally {
            DatabaseConnectionManager.closeDatabaseResources(resources);
        }
        return surtidor;
    }

    public TreeMap<Long, Surtidor> getCarasN() {
        Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");

        TreeMap<Long, Surtidor> surtidor = new TreeMap<>();
        try {
            String sql = "select distinct pf.id, pf.codigo familia_descripcion, p.precio  from productos p\n"
                    + "inner join surtidores_detalles sd on sd.productos_id = p.id\n"
                    + "INNER JOIN productos_familias pf on p.familias = pf.id";
            PreparedStatement ps = conexion.prepareStatement(sql);

            ResultSet re = ps.executeQuery();
            while (re.next()) {
                Surtidor sur = new Surtidor();
                sur.setId((int) re.getLong("id"));
                sur.setFamiliaDescripcion(re.getString("familia_descripcion"));
                sur.setProductoPrecio(re.getFloat("precio"));
                surtidor.put((long) re.getLong("id"), sur);
            }
        } catch (SQLException s) {
            NovusUtils.printLn(s.getMessage());
        } catch (Exception s) {
            NovusUtils.printLn(s.getMessage());
        }
        return surtidor;
    }

    public HashSet<String> getOnlyCaras() {
        Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");

        HashSet<String> caras = new HashSet<>();
        try {
            String sql = "select surtidores_id, cara from surtidores_detalles sd ";
            PreparedStatement ps = conexion.prepareStatement(sql);

            ResultSet re = ps.executeQuery();
            while (re.next()) {
                Surtidor sur = new Surtidor();
                sur.setId(re.getInt("surtidores_id"));
                sur.setCara(re.getInt("cara"));
                caras.add(sur.getCara() + "");
            }
        } catch (SQLException s) {
            NovusUtils.printLn(s.getMessage());
        } catch (Exception s) {
            NovusUtils.printLn(s.getMessage());
        }
        return caras;
    }

    public TreeMap<Long, Surtidor> getCarasEstacion() {
        Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");
        TreeMap<Long, Surtidor> surtidor = new TreeMap<>();
        try {
            String sql = "SELECT sur.*, p.descripcion, pf.codigo familia_descripcion, pf.id  familia_id FROM surtidores_detalles sur\n"
                    + "INNER JOIN productos p on p.id = sur.productos_id \n"
                    + "INNER JOIN productos_familias pf on p.familias = pf.id";
            PreparedStatement ps = conexion.prepareStatement(sql);

            ResultSet re = ps.executeQuery();
            while (re.next()) {
                Surtidor sur = new Surtidor();
                sur.setSurtidor((int) re.getLong("surtidor"));
                sur.setCara((int) re.getLong("cara"));
                sur.setManguera((int) re.getLong("manguera"));
                sur.setGrado((int) re.getLong("grado"));
                sur.setProductoIdentificador(re.getLong("productos_id"));
                sur.setProductoDescripcion(re.getString("descripcion"));
                try {
                    sur.setMotivoBloqueo(re.getString("motivo_bloqueo"));
                    sur.setBloqueo(re.getString("bloqueo").equals("S"));

                } catch (Exception e) {
                    sur.setBloqueo(false);
                    sur.setMotivoBloqueo("");
                    NovusUtils.printLn(e.getMessage());
                }
                sur.setFamiliaDescripcion(re.getString("familia_descripcion"));
                sur.setFamiliaIdentificador(re.getLong("familia_id"));
                surtidor.put((long) re.getLong("cara"), sur);
            }
        } catch (SQLException s) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, s);
        } catch (Exception s) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, s);
        }
        return surtidor;
    }

// trae la cara con  autorizacion en uso
    public ArrayList<Integer> getCarasUsadas(int cara){
        
        DatabaseConnectionManager.DatabaseResources resources = null;
        // ✅ LIMPIAR LA LISTA ANTES DE USARLA
        carasUsadas.clear();
        
        try{
            String sql = "select cara from transacciones t2 where usado is  null and cara = ?";
            resources = DatabaseConnectionManager.createDatabaseResources("lazoexpresscore", sql);
            PreparedStatement ps = resources.getPreparedStatement();
            ps.setInt(1, cara);
            DatabaseConnectionManager.executeQuery(resources);
            while(resources.getResultSet().next()){
                carasUsadas.add(resources.getResultSet().getInt("cara"));
            }            
        }catch(SQLException s){
            NovusUtils.printLn(s.getMessage());
        }catch(Exception s){
            NovusUtils.printLn(s.getMessage());
        }finally{
            DatabaseConnectionManager.closeDatabaseResources(resources);
        }
        return carasUsadas;
    }

    public TreeMap<Integer, Surtidor> getManguerasProductosAutorizacion() {
        TreeMap<Integer, Surtidor> surtidor = new TreeMap<>();
        DatabaseConnectionManager.DatabaseResources resources = null;

        try {
            String sql = "SELECT sur.*,\n"
                    + "p.descripcion,\n"
                    + "pf.codigo familia_descripcion,\n"
                    + "pf.id  familia_id,\n"
                    + "p.precio\n"
                    + "FROM surtidores_detalles sur\n"
                    + "INNER JOIN productos p on p.id = sur.productos_id \n"
                    + "INNER JOIN productos_familias pf on p.familias = pf.id \n"
                    + "where sur.estado_publico = 100\n "
                    + "AND pf.codigo NOT ILIKE '%GLP%' \n" 
                    + " order by manguera;";

            resources = DatabaseConnectionManager.executeCompleteQuery("lazoexpresscore", sql);
            while (resources.getResultSet().next()) {
                Surtidor sur = new Surtidor();
                sur.setSurtidor((int) resources.getResultSet().getLong("surtidor"));
                sur.setCara((int) resources.getResultSet().getLong("cara"));
                //sur.setCara(caraActual);
                
                // ✅ USAR LA LISTA QUE YA OBTUVIMOS
                // if (carasUsadas.contains(caraActual)) {
                //     // Esta cara está siendo usada, no la incluir o marcarla como no disponible
                //     continue; // Salta al siguiente registro sin agregarlo al TreeMap
                // } else {
                //     sur.setEstaDentroDelRango(true);
                // }
                sur.setManguera((int) resources.getResultSet().getLong("manguera"));
                sur.setGrado((int) resources.getResultSet().getLong("grado"));
                sur.setProductoIdentificador(resources.getResultSet().getLong("productos_id"));
                sur.setProductoDescripcion(resources.getResultSet().getString("descripcion"));
                sur.setProductoPrecio(resources.getResultSet().getFloat("precio"));
                try {
                    sur.setMotivoBloqueo(resources.getResultSet().getString("motivo_bloqueo"));
                    String bloqueo = resources.getResultSet().getString("bloqueo");
                    sur.setBloqueo(bloqueo != null ? bloqueo.equals("S") : false);
                } catch (Exception e) {
                    sur.setBloqueo(false);
                    sur.setMotivoBloqueo("");
                    NovusUtils.printLn(e.getMessage());
                }
                sur.setFamiliaDescripcion(resources.getResultSet().getString("familia_descripcion"));
                sur.setFamiliaIdentificador(resources.getResultSet().getLong("familia_id"));
                surtidor.put((int) resources.getResultSet().getLong("manguera"), sur);
            }
        } catch (SQLException s) {
            NovusUtils.printLn(s.getMessage());
        } catch (Exception s) {
            NovusUtils.printLn(s.getMessage());
        } finally {
            // Asegurarse de cerrar los recursos
            DatabaseConnectionManager.closeDatabaseResources(resources);
        }
        return surtidor;
    }
    
    
    public TreeMap<Integer, Surtidor> getManguerasProductosAutorizacionGLP() {
        TreeMap<Integer, Surtidor> surtidor = new TreeMap<>();
        DatabaseConnectionManager.DatabaseResources resources = null;

        try {
            
            String sql = "SELECT sur.*,\n"
                    + "p.descripcion,\n"
                    + "pf.codigo familia_descripcion,\n"
                    + "pf.id  familia_id,\n"
                    + "lp.precio\n"
                    + "FROM surtidores_detalles sur\n"
                    + "INNER JOIN productos p on p.id = sur.productos_id \n"
                    + "INNER JOIN productos_familias pf on p.familias = pf.id \n"
                    + "inner join lt_productos lp on pf.id = lp.familia_id\n"
                    + "where sur.estado_publico = 100 and lp.familia_id = 7\n "
                    + " order by manguera;";

            resources = DatabaseConnectionManager.executeCompleteQuery("lazoexpresscore", sql);
            while (resources.getResultSet().next()) {
                Surtidor sur = new Surtidor();
                sur.setSurtidor((int) resources.getResultSet().getLong("surtidor"));
                sur.setCara((int) resources.getResultSet().getLong("cara"));
                //sur.setCara(caraActual);
                
                // ✅ USAR LA LISTA QUE YA OBTUVIMOS
                // if (carasUsadas.contains(caraActual)) {
                //     // Esta cara está siendo usada, no la incluir o marcarla como no disponible
                //     continue; // Salta al siguiente registro sin agregarlo al TreeMap
                // } else {
                //     sur.setEstaDentroDelRango(true);
                // }
                sur.setManguera((int) resources.getResultSet().getLong("manguera"));
                sur.setGrado((int) resources.getResultSet().getLong("grado"));
                sur.setProductoIdentificador(resources.getResultSet().getLong("productos_id"));
                sur.setProductoDescripcion(resources.getResultSet().getString("descripcion"));
                sur.setProductoPrecio(resources.getResultSet().getFloat("precio"));
                try {
                    sur.setMotivoBloqueo(resources.getResultSet().getString("motivo_bloqueo"));
                    String bloqueo = resources.getResultSet().getString("bloqueo");
                    sur.setBloqueo(bloqueo != null ? bloqueo.equals("S") : false);
                } catch (Exception e) {
                    sur.setBloqueo(false);
                    sur.setMotivoBloqueo("");
                    NovusUtils.printLn(e.getMessage());
                }
                sur.setFamiliaDescripcion(resources.getResultSet().getString("familia_descripcion"));
                sur.setFamiliaIdentificador(resources.getResultSet().getLong("familia_id"));
                surtidor.put((int) resources.getResultSet().getLong("manguera"), sur);
            }
        } catch (SQLException s) {
            NovusUtils.printLn(s.getMessage());
        } catch (Exception s) {
            NovusUtils.printLn(s.getMessage());
        } finally {
            // Asegurarse de cerrar los recursos
            DatabaseConnectionManager.closeDatabaseResources(resources);
        }
        return surtidor;
    }
    
    

    public void actualizarDispositivos(String[] controlador, String[] ibutton, String[] rfid) {
        Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");
        try {
            String[][] dispositivos = {controlador, ibutton, rfid};
            for (int i = 0; i < dispositivos.length; i++) {
                String sql = "UPDATE public.dispositivos\n" + " SET  conector=?, interfaz=?," + " puerto=? ,"
                        + "estado=?\n" + " WHERE id=?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setString(1, dispositivos[i][0]);
                ps.setString(2, dispositivos[i][1]);
                ps.setInt(3, Integer.parseInt(dispositivos[i][2]));
                ps.setString(4, dispositivos[i][3]);
                ps.setLong(5, (i + 1));
                ps.executeUpdate();
            }
            String sql = "UPDATE surtidores\n" + " SET  ip=?, port=?," + " controlador=? " + " WHERE surtidor=?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, dispositivos[0][1]);
            ps.setLong(2, Integer.parseInt(dispositivos[0][2]));
            ps.setInt(3, dispositivos[0][0].equals("tcp") ? 1 : 2);
            ps.setLong(4, 1);
            ps.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void desactivarProducto(Long id) {

        String databases[] = {"lazoexpressregistry", "lazoexpresscore"};

        for (String database : databases) {
            try {
                Connection conexion = Main.obtenerConexionAsync(database);
                String sql = "update productos set estado='I' where id=?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, id);
                int estado = ps.executeUpdate();
                if (estado > 0) {
                    System.out.println(Main.ANSI_GREEN + "PRODUCTO INACTIVADO: " + id + Main.ANSI_RESET);
                } else {
                    System.out.println(Main.ANSI_RED + "ERROR AL INACTIVAR EL PRODUCTO: " + id + Main.ANSI_RESET);
                }
            } catch (SQLException ex) {
                Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void inhabilitarProductos(long productoId) {

        try {
            Connection conexion = Main.obtenerConexionAsync("lazoexpressregistry");
            String sql = "update productos set estado='I' ";
            if (productoId > 0) {
                sql = sql + " WHERE id =" + productoId;
            }
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void inhabilitarBodega() {

        String databases[] = {"lazoexpressregistry"};

        for (String database : databases) {
            try {
                Connection conexion = Main.obtenerConexionAsync(database);
                String sql = "update bodegas set estado ='I'";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.executeUpdate();

            } catch (SQLException ex) {
                Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public boolean limpiaEmpleados() throws DAOException {
        Connection conexion = Main.obtenerConexionAsync("lazoexpressregistry");

        class Inner {

            boolean existeIdentificadorPersonas() throws SQLException {
                boolean existeIdentificadorPersonas = false;
                String sql = "SELECT ID FROM identificadores_origenes WHERE ID= 5";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ResultSet re = ps.executeQuery();
                while (re.next()) {
                    existeIdentificadorPersonas = true;
                }
                return existeIdentificadorPersonas;
            }

            void insertarIdentificadorPersonas() throws SQLException {
                String sql = "INSERT INTO identificadores_origenes(id, descripcion, estado)\n"
                        + "    VALUES (5, 'PERSONAS', 'A');";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
            }

            void inactivarPersonal() throws SQLException {
                String sql = "UPDATE PERSONAS SET ESTADO='I'";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
            }

            void eliminarIdentificadoresPersonas() throws SQLException {
                String sql = "DELETE FROM IDENTIFICADORES WHERE ORIGEN = 5";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
            }

            void eliminarPermisosPost() throws SQLException {
                String sql = "DELETE FROM PERMISOS_POST";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.executeUpdate();

            }
        }
        Inner innerCall = new Inner();
        boolean limpiado = true;
        try {
            boolean existeIdentificadorPersonas = innerCall.existeIdentificadorPersonas();
            if (!existeIdentificadorPersonas) {
                innerCall.insertarIdentificadorPersonas();
            }
            innerCall.inactivarPersonal();
            innerCall.eliminarIdentificadoresPersonas();
            innerCall.eliminarPermisosPost();
        } catch (SQLException s) {
            NovusUtils.printLn(s.getMessage());
            limpiado = false;
        }
        return limpiado;
    }

    public boolean limpiaEmpleadosCore() throws DAOException {
        Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");
        class Inner {

            void inactivarPersonal() throws SQLException {
                String sql = "UPDATE PERSONAS SET ESTADO='I'";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
            }

        }
        Inner innerCall = new Inner();
        boolean limpiado = true;
        try {
            innerCall.inactivarPersonal();
        } catch (SQLException s) {
            NovusUtils.printLn(s.getMessage());
            limpiado = false;
        }
        return limpiado;
    }

    public boolean limpiaEmpleadosReg() throws DAOException {
        Connection conexion = Main.obtenerConexionAsync("lazoexpressregistry");
        class Inner {

            void inactivarPersonal() throws SQLException {
                String sql = "UPDATE PERSONAS SET ESTADO='I'";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
            }

        }
        Inner innerCall = new Inner();
        boolean limpiado = true;
        try {
            innerCall.inactivarPersonal();
        } catch (SQLException s) {
            NovusUtils.printLn(s.getMessage());
            limpiado = false;
        }
        return limpiado;
    }

    public boolean borrarCategorias() throws DAOException {
        Connection conexion = Main.obtenerConexionAsync("lazoexpressregistry");
        class Inner {

            void identificadoresGrupo() throws SQLException {
                String sql = "DELETE FROM grupos";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
            }

            void identificadoresTiposGrupo() throws SQLException {
                String sql = "DELETE FROM grupos_tipos";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
            }

            void identificadoresEntidadGrupo() throws SQLException {
                String sql = "DELETE FROM grupos_entidad";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
            }
        }
        Inner innerCall = new Inner();
        boolean limpiado = true;
        try {
            innerCall.identificadoresEntidadGrupo();
            innerCall.identificadoresGrupo();
            innerCall.identificadoresTiposGrupo();
        } catch (SQLException s) {
            NovusUtils.printLn(s.getMessage());
            limpiado = false;
        }
        return limpiado;
    }

    public boolean actulizarHorarios() throws DAOException {
        Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");

        class Inner {

            void actualizarHorarios() throws SQLException {
                String sql = "update wacher_parametros set valor = 'S' where codigo = 'OBTENER_HORARIOS';";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
            }
        }
        Inner innerCall = new Inner();
        boolean limpiado = true;
        try {
            innerCall.actualizarHorarios();
        } catch (SQLException s) {
            NovusUtils.printLn(s.getMessage());
            limpiado = false;
        }
        return limpiado;
    }

    public void procesarClientesFijosRegistry(CredencialBean cre) {
        Connection conexion = Main.obtenerConexionAsync("lazoexpressregistry");
        try {
            // CREACION DE CLIENTES VARIOS
            String sql1 = "INSERT INTO personas (id, tipos_identificaciones_id, identificacion, nombres, apellidos, estado, empresas_id, genero,"
                    + " sangre, create_user, create_date, update_user, update_date, ciudades_id, "
                    + "url_foto, perfiles_id, direccion, telefono) VALUES(2,1, '2222222', 'CLIENTES VARIOS',"
                    + " 'REGISTRADOS', 'A', ?, '', 'A+', 1, '2020-12-02 11:48:28.287', 1, '2020-12-21 09:29:36.378', 1, NULL, NULL, NULL, NULL);";
            PreparedStatement ps;
            String sql = "SELECT 1 FROM PERSONAS WHERE ID=2";
            try {
                ps = conexion.prepareStatement(sql);
                ResultSet re = ps.executeQuery();
                boolean existe = re.next();
                if (!existe) {
                    ps = conexion.prepareStatement(sql1);
                    ps.setLong(1, Main.credencial.getEmpresas_id());
                    ps.executeUpdate();
                }
            } catch (SQLException ex) {
                Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            }
            // CLIENTE REGISTRADOS
            sql1 = "INSERT INTO personas (id,tipos_identificaciones_id, identificacion, nombres, apellidos, estado, empresas_id, genero,"
                    + " sangre, create_user, create_date, update_user, update_date, ciudades_id, "
                    + "url_foto, perfiles_id, direccion, telefono) VALUES(3, 1, '3333333', 'CLIENTES REGISTRADO',"
                    + " 'REGISTRADOS', 'A', ?, '', 'A+', 1, '2020-12-02 11:48:28.287', 1, '2020-12-21 09:29:36.378', 1, NULL, NULL, NULL, NULL);";

            sql = "SELECT 1 FROM PERSONAS WHERE ID=3";
            try {
                ps = conexion.prepareStatement(sql);
                ResultSet re = ps.executeQuery();
                boolean existeRegistrado = re.next();
                if (!existeRegistrado) {
                    ps = conexion.prepareStatement(sql1);
                    ps.setLong(1, Main.credencial.getEmpresas_id());
                    ps.executeUpdate();
                }
            } catch (Exception ex) {
                Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (Exception ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void procesarClientesFijosCore(CredencialBean cre) {
        Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");
        try {
            String sql1 = "INSERT INTO personas (id, usuario, clave, identificacion, tipos_identificacion_id, nombre, estado, correo, direccion,"
                    + " fecha_nacimiento, perfiles_id, telefono, celular, create_user, create_date, update_user, update_date, ciudades_id, sangre, genero, empresas_id, sucursales_id, sincronizado,"
                    + " tag) VALUES(2, '2222222', '81dc9bdb52d04dc20036dbd8313ed055', '2222222', 1, 'CLIENTES VARIOS', 'A', '', NULL,"
                    + " NULL, NULL, NULL, NULL, 1, '2021-03-12 16:41:37.639', NULL, NULL, 1, 'A+', '', ?, 1, 1, NULL);";
            PreparedStatement ps;
            String sql = "SELECT 1 FROM PERSONAS WHERE ID=2";
            try {
                ps = conexion.prepareStatement(sql);
                ResultSet re = ps.executeQuery();
                boolean existe = re.next();
                if (!existe) {
                    ps = conexion.prepareStatement(sql1);
                    ps.setLong(1, Main.credencial.getEmpresas_id());
                    ps.executeUpdate();
                }
            } catch (SQLException ex) {
                Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            }

            sql1 = "INSERT INTO personas (id, usuario, clave, identificacion, tipos_identificacion_id, nombre,"
                    + " estado, correo, direccion, fecha_nacimiento, perfiles_id, telefono, celular, create_user,"
                    + " create_date, update_user, update_date, ciudades_id, sangre, genero, empresas_id, sucursales_id, sincronizado, tag) "
                    + "VALUES(3, '3333333', '81dc9bdb52d04dc20036dbd8313ed055', '3333333', 1, 'CLIENTES REGISTRADOS', 'A', '', NULL, NULL, NULL, "
                    + "NULL, NULL, 1, '2021-03-12 16:41:37.639', NULL, NULL, 1, 'A+', '', ?, 1, 1, NULL);";

            try {
                sql = "SELECT 1 FROM PERSONAS WHERE ID=3";
                ps = conexion.prepareStatement(sql);
                ResultSet re = ps.executeQuery();
                boolean existeRegistrado = re.next();
                if (!existeRegistrado) {
                    ps = conexion.prepareStatement(sql1);
                    ps.setLong(1, Main.credencial.getEmpresas_id());
                    ps.executeUpdate();
                }
            } catch (SQLException ex) {
                Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (Exception ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean existePerfil(Postgrest db, PerfilesBean perfil) throws SQLException, DAOException {
        Connection conexion = Main.obtenerConexionAsync("lazoexpressregistry");
        String sql = "SELECT ID FROM PERFILES WHERE ID=?";
        PreparedStatement ps = conexion.prepareStatement(sql);
        ps.setLong(1, perfil.getId());
        ResultSet re = ps.executeQuery();
        return re.next();
    }

    public boolean existeBodega(long bodega) throws SQLException, DAOException {
        Connection conexion = Main.obtenerConexionAsync("lazoexpressregistry");
        String sql = "SELECT ID FROM BODEGAS WHERE ID=?";
        PreparedStatement ps = conexion.prepareStatement(sql);
        ps.setLong(1, bodega);
        ResultSet re = ps.executeQuery();
        return re.next();
    }

    public void insertarPerfiles(Postgrest db, ArrayList<PerfilesBean> perfiles) throws SQLException, DAOException {
        Connection conexion = Main.obtenerConexionAsync("lazoexpressregistry");
        for (PerfilesBean perfil : perfiles) {
            if (!existePerfil(db, perfil)) {
                String sql = "INSERT INTO perfiles\n" + "(id, descripcion, estado)\n" + "VALUES(?, ?, ?);";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, perfil.getId());
                ps.setString(2, perfil.getDescripcion());
                ps.setString(3, perfil.getEstado());
                ps.executeUpdate();
            }
        }
    }

    public void limpiaEmpleadosRemotos() throws DAOException {
        Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");
        try {
            String sql = "UPDATE PERSONAS SET ESTADO = 'I' WHERE id > 2";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException s) {
            NovusUtils.printLn(s.getMessage());
        } catch (Exception s) {
            NovusUtils.printLn(s.getMessage());
        }
    }

    public boolean procesarEmpleadoRegistry(PersonaBean pers, CredencialBean credencial) throws DAOException {
        Connection conexion = Main.obtenerConexionAsync("lazoexpressregistry");

        class Inner {

            boolean existeTipoIdentificador() throws SQLException {
                String sql = "SELECT ID FROM tipos_identificaciones WHERE ID = ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, pers.getTipoIdentificacionId());
                ResultSet re = ps.executeQuery();
                return re.next();
            }

            void insertarTipoIdentificador() throws SQLException {
                String sql = "INSERT INTO tipos_identificaciones (id, descripcion, estado) "
                        + "    VALUES (?, ?, 'A');";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, pers.getTipoIdentificacionId());
                ps.setString(2, pers.getTipoIdentificacionDesc());
                ps.executeUpdate();
            }

            boolean existeModulo(ModulosBean modulo) throws SQLException {
                String sql = "SELECT ID FROM modulos WHERE ID = ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, modulo.getId());
                ResultSet re = ps.executeQuery();
                return re.next();
            }

            void insertarModulo(ModulosBean modulo) throws SQLException {
                String sql = "INSERT INTO modulos "
                        + "(id, descripcion, acciones, estado, modulos_id, niveles, atributos) "
                        + "VALUES(?, ?, ?, ?, ?, ?, ?::json);";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, modulo.getId());
                ps.setString(2, modulo.getDescripcion() != null ? modulo.getDescripcion() : "");
                ps.setString(3, modulo.getAcciones() != null ? modulo.getAcciones() : "");
                ps.setString(4, modulo.getEstado());
                ps.setLong(5, modulo.getModuloId());
                ps.setString(6, modulo.getNivel());
                ps.setString(7, modulo.getAtributos().toString());
                ps.executeUpdate();
            }

            void actualizarModulo(ModulosBean modulo) throws SQLException {
                String sql = "UPDATE modulos SET descripcion = ?, acciones = ?, modulos_id = ?, atributos = ?::json WHERE ID = ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setString(1, modulo.getDescripcion() != null ? modulo.getDescripcion() : "");
                ps.setString(2, modulo.getAcciones() != null ? modulo.getAcciones() : "");
                ps.setLong(3, modulo.getModuloId());
                ps.setString(4, modulo.getAtributos().toString());
                ps.setLong(5, modulo.getId());
                ps.executeUpdate();
            }

            boolean existePermiso(ModulosBean modulo) throws SQLException {
                String sql = "SELECT ID FROM permisos_post WHERE ID = ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, modulo.getId());
                ResultSet re = ps.executeQuery();
                return re.next();
            }

            void insertarPermiso(ModulosBean modulo) throws SQLException {
                String sql = "INSERT INTO permisos_post ( " + "   id, personas_id, modulos_id, modulos_descripcion) "
                        + "   VALUES (nextval('permisos_post_id'), ?, ?, ?);";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, pers.getId());
                ps.setLong(2, modulo.getId());
                ps.setString(3, modulo.getDescripcion() != null ? modulo.getDescripcion() : "");
                ps.executeUpdate();
            }

            boolean existePersona() throws SQLException {
                String sql = "SELECT ID FROM personas WHERE ID = ? OR IDENTIFICACION=?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, pers.getId());
                ps.setString(2, pers.getIdentificacion());
                ResultSet re = ps.executeQuery();
                return re.next();
            }

            void insertarPersona() throws SQLException {
                String sql = "INSERT INTO personas ( "
                        + "   id, tipos_identificaciones_id, identificacion, nombres, apellidos, "
                        + "   estado, empresas_id, genero, sangre, create_user, create_date, "
                        + "   update_user, update_date, ciudades_id, url_foto,perfiles_id ) "
                        + "   VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 1, now(), " + "   null, null, ?, null,?);";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, pers.getId());
                ps.setLong(2, pers.getTipoIdentificacionId());
                ps.setString(3, pers.getIdentificacion() != null ? pers.getIdentificacion() : "");
                ps.setString(4, pers.getNombre() != null ? pers.getNombre() : "");
                ps.setString(5, pers.getApellidos() != null ? pers.getApellidos() : "");
                ps.setString(6, pers.getEstado());
                ps.setLong(7, credencial.getEmpresas_id());
                ps.setString(8, pers.getGenero());
                ps.setString(9, pers.getSangre() != null ? pers.getSangre() : "");
                ps.setLong(10, pers.getCiudadId());
                ps.setLong(11, pers.getPerfil());

                ps.executeUpdate();
            }

            void actualizarPersona() throws SQLException {

                String sql = "UPDATE personas " + "   SET  tipos_identificaciones_id=?, identificacion=?, nombres=?, "
                        + "   apellidos=?, estado=?, empresas_id=?, genero=?, sangre=?, "
                        + "   update_user=1, update_date=NOW(), ciudades_id=?, url_foto=null, perfiles_id = ? " + " WHERE id=?;";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, pers.getTipoIdentificacionId());
                ps.setString(2, pers.getIdentificacion() != null ? pers.getIdentificacion() : "");
                ps.setString(3, pers.getNombre() != null ? pers.getNombre() : "");
                ps.setString(4, pers.getApellidos() != null ? pers.getApellidos() : "");
                ps.setString(5, pers.getEstado());
                ps.setLong(6, credencial.getEmpresas_id());
                ps.setString(7, pers.getGenero());
                ps.setString(8, pers.getSangre() != null ? pers.getSangre() : "");
                ps.setLong(9, pers.getCiudadId());
                ps.setInt(10, pers.getPerfil());
                ps.setLong(11, pers.getId());
                ps.executeUpdate();
            }

            void actualizarPerfilUsuario() throws SQLException {
                String sql = "UPDATE usuarios" + "   SET  personas_id=?, usuario=?, clave=?, correo=?, empresas_id=?, "
                        + "   dominio_id=?, estado=?, pin=?  WHERE id=?;";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, pers.getId());
                ps.setString(2, pers.getIdentificacion() != null ? pers.getIdentificacion() : "");
                ps.setString(3, pers.getClave() != null ? pers.getClave() : "");
                ps.setString(4, pers.getCorreo() != null ? pers.getCorreo() : "");
                ps.setLong(5, credencial.getEmpresas_id());
                ps.setLong(6, credencial.getEmpresas_id());
                ps.setString(7, pers.getEstado());
                ps.setInt(8, pers.getPin());
                ps.setLong(9, pers.getId());
                ps.executeUpdate();
            }

            void insertarPerfilUsuario() throws SQLException {
                String sql = "INSERT INTO usuarios ( "
                        + " id, personas_id, usuario, clave, correo, empresas_id, dominio_id, "
                        + " estado, pin)  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, pers.getId());
                ps.setLong(2, pers.getId());
                ps.setString(3, pers.getIdentificacion() != null ? pers.getIdentificacion() : "");
                ps.setString(4, pers.getClave() != null ? pers.getClave() : "");
                ps.setString(5, pers.getCorreo() != null ? pers.getCorreo() : "");
                ps.setLong(6, credencial.getEmpresas_id());
                ps.setLong(7, credencial.getEmpresas_id());
                ps.setString(8, pers.getEstado());
                ps.setInt(9, pers.getPin());
                ps.executeUpdate();
            }

            boolean existePerfilUsuario() throws SQLException {
                String sql = "SELECT ID FROM USUARIOS WHERE PERSONAS_ID = ? OR USUARIO=?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, pers.getId());
                ps.setString(2, pers.getIdentificacion());
                ResultSet re = ps.executeQuery();
                return re.next();
            }

            void insertarIdentificadoresPersonas(IdentificadoresBean identi) throws SQLException {
                String sql = "INSERT INTO identificadores ("
                        + "            id, identificador, origen, placa_vin, fecha_instalacion, fecha_revision, "
                        + "            fecha_vencimiento, estado, create_user, create_date, update_user, "
                        + "            update_date, empresas_id, entidad_id ) "
                        + "    VALUES (?, ?, 5, null, now(), null, " + "    NULL, ?, 1, NOW(), NULL,  NULL, ?, ?);";
                PreparedStatement ps = conexion.prepareStatement(sql);

                ps.setLong(1, identi.getId());
                ps.setString(2, identi.getIdentificador() != null ? identi.getIdentificador() : "");
                ps.setString(3, identi.getEstado());
                ps.setLong(4, credencial.getEmpresas_id());
                ps.setLong(5, pers.getId());
                ps.executeUpdate();
            }

            void actualizarIdentificadoresPersona(IdentificadoresBean identi) throws SQLException {
                String sql = "UPDATE identificadores SET identificador = ?, estado= ?, entidad_id= ? where ID = ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setString(1, identi.getIdentificador() != null ? identi.getIdentificador() : "");
                ps.setString(2, identi.getEstado());
                ps.setLong(3, pers.getId());
                ps.setLong(4, identi.getId());
                ps.executeUpdate();
            }

            boolean existeIdentificadoresPersona(IdentificadoresBean identi) throws SQLException {
                String sql = "SELECT ID FROM identificadores WHERE IDENTIFICADOR = ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setString(1, identi.getIdentificador());
                ResultSet re = ps.executeQuery();
                return re.next();
            }

        }
        boolean insertado = true;
        Inner innerCall = new Inner();

        try {

            boolean existeTipoIdentificador = innerCall.existeTipoIdentificador();
            if (!existeTipoIdentificador) {
                innerCall.insertarTipoIdentificador();
            }
            boolean existePersona = innerCall.existePersona();
            if (!existePersona) {
                innerCall.insertarPersona();
            } else {
                innerCall.actualizarPersona();
            }
            if (pers.getPin() != 0) {
                boolean existePerfilUsuario = innerCall.existePerfilUsuario();
                if (!existePerfilUsuario) {
                    innerCall.insertarPerfilUsuario();
                } else {
                    innerCall.actualizarPerfilUsuario();
                }
            }
            if (pers.getModulos() != null) {
                for (ModulosBean modulo : pers.getModulos()) {
                    boolean existeModulo = innerCall.existeModulo(modulo);
                    boolean existePermiso = innerCall.existePermiso(modulo);
                    if (!existeModulo) {
                        innerCall.insertarModulo(modulo);
                    } else {
                        innerCall.actualizarModulo(modulo);
                    }
                    if (!existePermiso) {
                        innerCall.insertarPermiso(modulo);
                    }
                }
            }
            if (pers.getIdentificadores() != null) {
                for (IdentificadoresBean identi : pers.getIdentificadores()) {
                    boolean existeIdentificadoresPersona = innerCall.existeIdentificadoresPersona(identi);
                    if (!existeIdentificadoresPersona) {
                        innerCall.insertarIdentificadoresPersonas(identi);
                    } else {
                        innerCall.actualizarIdentificadoresPersona(identi);
                    }
                    break;
                }
            }
        } catch (SQLException s) {
            NovusUtils.printLn(s + "registry");
            insertado = false;
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, s);
        } catch (Exception ex) {
            insertado = false;
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return insertado;
    }

    public boolean procesarEmpleadoCore(PersonaBean pers, CredencialBean credencial) throws DAOException {
        Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");
        class Inner {

            boolean existeTipoIdentificador() throws SQLException {
                String sql = "SELECT ID FROM tipos_identificaciones WHERE ID = ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, pers.getTipoIdentificacionId());
                ResultSet re = ps.executeQuery();
                return re.next();
            }

            boolean existePersona() throws SQLException {
                String sql = "SELECT ID FROM personas WHERE ID=? OR IDENTIFICACION = ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, pers.getId());
                ps.setString(2, pers.getIdentificacion());
                ResultSet re = ps.executeQuery();
                return re.next();
            }

            void insertarTipoIdentificador() throws SQLException {
                String sql = "INSERT INTO tipos_identificaciones (id, descripcion, estado, prioridad) "
                        + "    VALUES (?, ?, 'A',1);";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, pers.getTipoIdentificacionId());
                ps.setString(2, pers.getTipoIdentificacionDesc());
                ps.executeUpdate();
            }

            boolean esPerfilPOS(int perfilId) {
                boolean esPerfilPOS = false;
                for (PerfilesBean perfil : Main.credencial.getEmpresa().getPerfilesEmpresa()) {
                    if (perfil.getId() == perfilId) {
                        esPerfilPOS = true;
                        break;
                    }
                }
                return esPerfilPOS;
            }

            void insertarPersona() throws SQLException {
                String sql = "INSERT INTO personas ( id, usuario, clave, identificacion, tipos_identificacion_id,"
                        + " nombre, estado, correo, direccion, fecha_nacimiento,"
                        + " telefono, celular, create_user, create_date,"
                        + " update_user, update_date, ciudades_id, sangre, genero, empresas_id, "
                        + "sucursales_id, sincronizado, tag, perfiles_id, pin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NULL, "
                        + "NULL,  NULL, NULL, 1, now(), NULL, NULL, ?, ?, ?, ?, 1, 1, ?, ?, ?);";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, pers.getId());
                ps.setString(2, pers.getIdentificacion() != null ? pers.getIdentificacion() : "");
                ps.setString(3, pers.getClave() != null ? pers.getClave() : "");
                ps.setString(4, pers.getIdentificacion() != null ? pers.getIdentificacion() : "");
                ps.setLong(5, pers.getTipoIdentificacionId());
                ps.setString(6, (pers.getNombre() != null ? pers.getNombre() : "")
                        + (pers.getApellidos() != null ? (" " + pers.getApellidos()) : ""));
                ps.setString(7, pers.getEstado() != null ? pers.getEstado() : "A");
                ps.setString(8, pers.getCorreo() != null ? pers.getCorreo() : "");
                ps.setLong(9, pers.getCiudadId());
                ps.setString(10, pers.getSangre() != null ? pers.getSangre() : "");
                ps.setString(11, pers.getGenero() != null ? pers.getGenero() : "");
                ps.setLong(12, Main.credencial.getEmpresas_id());
                if (pers.getIdentificadores() != null && !pers.getIdentificadores().isEmpty()) {
                    for (IdentificadoresBean identi : pers.getIdentificadores()) {
                        ps.setString(13, identi.getIdentificador() != null ? identi.getIdentificador() : "");
                        break;
                    }
                } else {
                    ps.setNull(13, Types.NULL);
                }
                if (pers.getPerfil() > 0 && esPerfilPOS(pers.getPerfil())) {
                    ps.setInt(14, pers.getPerfil());
                } else {
                    ps.setNull(14, Types.NULL);
                }
                ps.setInt(15, pers.getPin());

                ps.executeUpdate();
            }

            void actualizarPersona() throws SQLException {
                String sql = "UPDATE personas " + "  SET tipos_identificacion_id=?, identificacion=?, nombre=?,"
                        + "  estado=?, tag = ?, perfiles_id = ?, pin=? WHERE id=?;";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, pers.getTipoIdentificacionId());
                ps.setString(2, pers.getIdentificacion() != null ? pers.getIdentificacion() : "");
                ps.setString(3, (pers.getNombre() != null ? pers.getNombre() : "")
                        + (pers.getApellidos() != null ? (" " + pers.getApellidos()) : ""));
                ps.setString(4, pers.getEstado() != null ? pers.getEstado() : "A");
                if (pers.getIdentificadores() != null && !pers.getIdentificadores().isEmpty()) {
                    for (IdentificadoresBean identi : pers.getIdentificadores()) {
                        ps.setString(5, identi.getIdentificador() != null ? identi.getIdentificador() : "");
                        break;
                    }
                } else {
                    ps.setNull(5, Types.NULL);
                }
                if (pers.getPerfil() > 0 && esPerfilPOS(pers.getPerfil())) {
                    ps.setInt(6, pers.getPerfil());
                } else {
                    ps.setNull(6, Types.NULL);
                }
                ps.setLong(7, pers.getPin());
                ps.setLong(8, pers.getId());
                ps.executeUpdate();
            }

            void insertarCliente() throws SQLException {
                String sql = "INSERT INTO clientes "
                        + "( personas_id, idrom_id, create_user, create_date, update_user, update_date) "
                        + "VALUES( ?, NULL, NULL, NULL, NULL, NULL);";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, pers.getId());
            }

            boolean existeCliente() throws SQLException {
                String sql = "SELECT personas_id FROM clientes WHERE personas_id = ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, pers.getId());
                ResultSet re = ps.executeQuery();
                return re.next();
            }
        }
        Inner innerCall = new Inner();
        boolean insertado = true;
        try {

            boolean existeTipoIdentificador = innerCall.existeTipoIdentificador();
            if (!existeTipoIdentificador) {
                innerCall.insertarTipoIdentificador();
            }
            boolean existePersona = innerCall.existePersona();
            if (!existePersona) {
                innerCall.insertarPersona();
            } else {
                innerCall.actualizarPersona();
            }
            if (pers.isCliente()) {
                boolean existeCliente = innerCall.existeCliente();
                if (!existeCliente) {
                    innerCall.insertarCliente();
                }
            }
        } catch (SQLException s) {
            NovusUtils.printLn(s + "CORE");
            insertado = false;
        }
        return insertado;
    }

    public void procesarEmpleadoRemoto(PersonaBean pers, CredencialBean credencial) throws DAOException {
        Connection conexion = Main.obtenerConexionAsync("lazoexpressregistry");

        try {
            String sql = "INSERT INTO ciudades(\n"
                    + "            id, descripcion, zona_horaria, indicadores, provincia_id, estado, \n"
                    + "            create_user, create_date, update_user, update_date)\n"
                    + "    VALUES (?, ?, null, null, null, 'A', \n" + "            1, NOW(), null, null);";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, pers.getCiudadId());
            ps.setString(2, pers.getCiudadDesc());

            ps.executeUpdate();

        } catch (Exception s) {
            if (!s.getMessage().contains("duplicate")) {
                NovusUtils.printLn(s.getMessage());
            }
        }

        try {
            String sql = "INSERT INTO tipos_identificaciones(\n" + "            id, descripcion, estado)\n"
                    + "    VALUES (?, ?, 'A');";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, pers.getTipoIdentificacionId());
            ps.setString(2, pers.getTipoIdentificacionDesc());

            ps.executeUpdate();

        } catch (Exception s) {
            if (!s.getMessage().contains("duplicate")) {
                NovusUtils.printLn(s.getMessage());
            }
        }

        try {
            String sql = "INSERT INTO personas(\n"
                    + "            id, tipos_identificacion_id, identificacion, nombre, estado,\n"
                    + "            perfiles_id, empresas_id, create_user, create_date, sincronizado)\n"
                    + "    VALUES (?, ?, ?, ?, ?, \n" + "            ?, ?, 1, now(), 1);";
            int PERFILES_ID = 1;
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, pers.getId());
            ps.setLong(2, pers.getTipoIdentificacionId());
            ps.setString(3, pers.getIdentificacion());
            ps.setString(4, pers.getNombre() + " " + pers.getApellidos());
            ps.setString(5, pers.getEstado());
            ps.setLong(6, PERFILES_ID);
            ps.setLong(7, credencial.getEmpresas_id());

            ps.executeUpdate();

        } catch (PSQLException s) {
            NovusUtils.printLn("ERROR AL CREAR LA PERSONAS ");
            NovusUtils.printLn(s.getMessage());
            if (s.getMessage().contains("duplic")) {
                try {
                    String sql = "UPDATE personas\n" + "   SET identificacion=?, nombre=?, estado=?,"
                            + " update_user=1, update_date=NOW()\n" + " WHERE id=?;";

                    PreparedStatement ps = conexion.prepareStatement(sql);

                    ps.setString(1, pers.getIdentificacion());
                    ps.setString(2, pers.getNombre() + " " + pers.getApellidos());
                    ps.setString(3, "A");
                    ps.setLong(4, pers.getId());

                    ps.executeUpdate();

                } catch (SQLException ed) {
                    throw new DAOException("procesarEmpleadoRemoto 175." + ed.getMessage());
                }
            }
        } catch (SQLException s) {
            throw new DAOException("procesarEmpleadoRemoto 21." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("procesarEmpleadoRemoto 31." + s.getMessage());
        }
    }

    public boolean createBloqueadoCore(int index, ProductoBean p, CredencialBean cr, JsonArray tipos)
            throws DAOException {
        Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");

        class Inner {

            void crearTablaGruposTipos() throws SQLException {
                String sql = " CREATE TABLE IF NOT EXISTS public.grupos_tipos (\n"
                        + "	id int NOT NULL,\n"
                        + "	descripcion text NULL,\n"
                        + "	estado varchar(1) NULL,\n"
                        + "	entidad int NULL\n"
                        + ");";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
            }

            boolean existeGruposTipos(CategoriaBean cate) throws SQLException {
                String sql = "SELECT ID FROM GRUPOS_TIPOS WHERE ID = ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, cate.getGruposTiposId());
                ResultSet re = ps.executeQuery();
                return re.next();
            }

            void insertarGruposTipos(CategoriaBean cate) throws SQLException {
                String sql = "INSERT INTO grupos_tipos ( id, descripcion, estado, entidad) " + " VALUES (?, ?, ?, ?);";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, cate.getGruposTiposId());
                ps.setString(2, cate.getGrupo() != null ? cate.getGrupo() : "");
                ps.setString(3, "A");
                ps.setLong(4, cate.getGp_id());
                ps.executeUpdate();
            }

            void actualizarGruposTipos(CategoriaBean cate) throws SQLException {
                String sql = "UPDATE grupos_tipos SET descripcion=?, estado=?, entidad=?  WHERE id=?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setString(1, cate.getGrupo() != null ? cate.getGrupo() : "");
                ps.setString(2, "A");
                ps.setLong(3, cate.getGp_id());
                ps.setLong(4, cate.getGruposTiposId());
                ps.executeUpdate();
            }

            boolean existeTipoProducto(JsonObject tipo) throws SQLException {
                String sql = "SELECT ID FROM productos_tipos WHERE ID = ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, tipo.get("id").getAsLong());
                ResultSet re = ps.executeQuery();
                return re.next();
            }

            void insertarTipoProducto(JsonObject tipo) throws SQLException {
                String sql = "INSERT INTO productos_tipos( id, descripcion, estado) " + " VALUES (?, ?, ?);";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, tipo.get("id").getAsLong());
                ps.setString(2, tipo.get("descripcion").getAsString());
                ps.setString(3, tipo.get("estado").getAsString());
                ps.executeUpdate();

            }

            void actualizarTipoProducto(JsonObject tipo) throws SQLException {
                String sql = "UPDATE productos_tipos SET descripcion=?, estado=?  WHERE id=?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setString(1, tipo.get("descripcion").getAsString());
                ps.setString(2, tipo.get("estado").getAsString());
                ps.setLong(3, tipo.get("id").getAsLong());
                ps.executeUpdate();

            }

            void crearTablaGrupos() throws SQLException {
                String sql = "CREATE TABLE IF NOT EXISTS public.grupos (\n"
                        + "	id int NULL,\n"
                        + "	grupo int NULL,\n"
                        + "	estado varchar(1) NULL,\n"
                        + "	grupos_tipos int NULL,\n"
                        + "	empresas_id int NULL,\n"
                        + "	grupos_id int NULL,\n"
                        + "	url_foto varchar(255) NULL\n"
                        + ");";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
            }

            boolean existeGrupo(CategoriaBean categoria) throws SQLException {
                String sql = "SELECT ID FROM GRUPOS WHERE ID = ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, categoria.getGp_id());
                ResultSet re = ps.executeQuery();
                return re.next();
            }

            void insertarGrupo(CategoriaBean categoria) throws SQLException {
                String sql = "INSERT INTO grupos ("
                        + " id, grupo, estado, grupos_tipos_id, empresas_id, grupos_id, url_foto) "
                        + " VALUES (?, ?, ?, ?, ?, NULL, ?);";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, categoria.getGp_id());
                ps.setString(2, categoria.getGrupo() != null ? categoria.getGrupo() : "");
                ps.setString(3, "A");
                ps.setLong(4, categoria.getGruposTiposId());
                ps.setLong(5, cr.getEmpresas_id());
                ps.setString(6, categoria.getUrl_foto() != null ? categoria.getUrl_foto() : "");
                ps.executeUpdate();
            }

            void actualizarGrupo(CategoriaBean categoria) throws SQLException {
                String sql = "UPDATE grupos SET "
                        + " grupo=?, estado=?, grupos_tipos_id=?, empresas_id=?, grupos_id=?, url_foto=? "
                        + " WHERE id=?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setString(1, categoria.getGrupo() != null ? categoria.getGrupo() : "");
                ps.setString(2, "A");
                ps.setLong(3, categoria.getGruposTiposId());
                ps.setLong(4, cr.getEmpresas_id());
                ps.setString(5, categoria.getUrl_foto() != null ? categoria.getUrl_foto() : "");
                ps.setLong(6, categoria.getGp_id());
                ps.executeUpdate();
            }

            boolean existeUnidad() throws SQLException {
                String sql = "SELECT ID FROM productos_unidades WHERE ID = ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, p.getUnidades_medida_id());
                ResultSet re = ps.executeQuery();
                return re.next();
            }

            void insertarUnidad() throws SQLException {
                String sql = "INSERT INTO productos_unidades (  id, descripcion, estado)"
                        + " VALUES (?, ?, 'A')";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, p.getUnidades_medida_id());
                ps.setString(2, p.getUnidades_medida());
                ps.executeUpdate();
            }

            void actualizarUnidad() throws SQLException {
                String sql = "UPDATE productos_unidades SET "
                        + " descripcion=? WHERE id=?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setString(1, p.getUnidades_medida());
                ps.setLong(2, p.getUnidades_medida_id());
                ps.executeUpdate();
            }

            boolean existeProducto() throws SQLException {
                String sql = "SELECT ID FROM productos WHERE ID = ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, p.getId());
                ResultSet re = ps.executeQuery();
                return re.next();
            }

            void actualizarProducto() throws DAOException {
                try {
                    String sql = "UPDATE productos SET descripcion=?, estado=?,productos_tipos_id = ?,empresas_id = ?, precio= ?, precio2= ?, unidad_medida_id= ?, plu= ?, p_atributos=?::json WHERE id = ?";
                    PreparedStatement ps = conexion.prepareStatement(sql);
                    ps.setString(1, p.getDescripcion());
                    ps.setString(2, p.getEstado());
                    ps.setLong(3, p.getTipo());
                    ps.setLong(4, cr.getEmpresas_id());
                    ps.setFloat(5, p.getPrecio());
                    ps.setFloat(6, p.getPrecio());
                    ps.setLong(7, p.getUnidades_medida_id());
                    ps.setString(8, p.getPlu());
                    ps.setString(9, p.getAtributos() != null ? p.getAtributos().toString() : null);
                    ps.setLong(10, p.getId());

                    ps.executeUpdate();
                    
                    if (productoUpdateInterceptor != null && p != null) {
                        productoUpdateInterceptor.onProductoActualizado(p.getId());
                    }
                    
                } catch (SQLException s) {
                    NovusUtils.printLn(s.getMessage());
                } catch (Exception s) {
                    NovusUtils.printLn(s.getMessage());
                }
            }

            void insertarProducto() throws DAOException {
                try {
                    String sql = "INSERT INTO productos ( "
                            + "        id, descripcion, estado, productos_tipos_id, empresas_id, familias, publico, "
                            + "          precio, precio2, unidad_medida_id, plu, p_atributos ) "
                            + "    VALUES (?, ?, ?, ?, ?, NULL, NULL, ?, ?, ?, ?, ?::json );";
                    PreparedStatement ps = conexion.prepareStatement(sql);
                    ps.setLong(1, p.getId());
                    ps.setString(2, p.getDescripcion());
                    ps.setString(3, p.getEstado());
                    ps.setLong(4, p.getTipo());
                    ps.setLong(5, cr.getEmpresas_id());
                    ps.setFloat(6, p.getPrecio());
                    ps.setFloat(7, p.getPrecio());
                    ps.setLong(8, p.getUnidades_medida_id());
                    ps.setString(9, p.getPlu());
                    ps.setString(10, p.getAtributos() != null ? p.getAtributos().toString() : null);
                    ps.executeUpdate();
                    
                    //  NUEVO: Invalidar cache después de insertar producto
                    if (productoUpdateInterceptor != null && p != null) {
                        productoUpdateInterceptor.onProductoCreado(p.getId());
                    }
                    
                } catch (SQLException s) {
                    NovusUtils.printLn(s.getMessage());
                } catch (Exception s) {
                    NovusUtils.printLn(s.getMessage());
                }
            }

            void crearTablaGrupoEntidad() throws SQLException {
                String sql = "CREATE TABLE IF NOT EXISTS public.grupos_entidad (\n"
                        + "	id int NULL,\n"
                        + "	grupo_id int NULL,\n"
                        + "	entidad_id int NULL\n"
                        + ");";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.executeUpdate();

                sql = "CREATE SEQUENCE IF NOT EXISTS id_entidad";
                ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
            }

            boolean existeGrupoEntidad(CategoriaBean categoria) throws SQLException {
                String sql = "SELECT ID FROM grupos_entidad WHERE grupo_id = ? AND entidad_id= ?  ";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, categoria.getGp_id());
                ps.setLong(2, p.getId());
                ResultSet re = ps.executeQuery();
                return re.next();
            }

            void insertarGrupoEntidad(CategoriaBean categoria) throws SQLException {
                String sql = "INSERT INTO grupos_entidad( id, grupo_id, entidad_id)"
                        + "  VALUES (nextval('id_entidad'), ?, ?);";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, categoria.getGp_id());
                ps.setLong(2, p.getId());
                ps.executeUpdate();
            }

            void crearTablaImpuesto() throws SQLException {
                String sql = " CREATE TABLE IF NOT EXISTS public.impuestos (\n"
                        + "	id int NULL,\n"
                        + "	descripcion varchar(255) NULL,\n"
                        + "	porcentaje_valor text NULL,\n"
                        + "	valor numeric(14,3) NULL,\n"
                        + "	empresas_id int NULL\n"
                        + ");";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
            }

            boolean existeImpuesto(ImpuestosBean imp) throws SQLException {
                String sql = "SELECT ID FROM impuestos WHERE ID = ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, imp.getImpuestos_id());
                ResultSet re = ps.executeQuery();
                return re.next();
            }

            void insertarImpuestos(ImpuestosBean impuesto) throws SQLException {
                String sql = "INSERT INTO impuestos( id, descripcion, porcentaje_valor, valor, estado, empresas_id) "
                        + "    VALUES (?, ?, ?, ?, ?, ?);";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, impuesto.getImpuestos_id());
                ps.setString(2, impuesto.getDescripcion() != null ? impuesto.getDescripcion().toUpperCase() : "");
                ps.setString(3, impuesto.getPorcentaje_valor());
                ps.setFloat(4, impuesto.getValor());
                ps.setString(5, "A");
                ps.setLong(6, cr.getEmpresas_id());
                ps.executeUpdate();
            }

            void actualizarImpuestos(ImpuestosBean impuesto) throws SQLException {
                String sql = "UPDATE impuestos "
                        + "   SET descripcion=?, porcentaje_valor=?, valor=?, empresas_id=? WHERE id=?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setString(1, impuesto.getDescripcion() != null ? impuesto.getDescripcion().toUpperCase() : "");
                ps.setString(2, impuesto.getPorcentaje_valor());
                ps.setFloat(3, impuesto.getValor());
                ps.setLong(4, cr.getEmpresas_id());
                ps.setLong(5, impuesto.getImpuestos_id());
                ps.executeUpdate();
            }

            void crearTablaProductosImpuestos() throws SQLException {
                String sql = " CREATE TABLE IF NOT EXISTS public.productos_impuestos (\n"
                        + "	id int NULL,\n"
                        + "	productos_id int NULL,\n"
                        + "	impuestos_id int NULL,\n"
                        + "	iva_incluido varchar(1) NULL,\n"
                        + "	tipo varchar(255) NULL\n"
                        + ");";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.executeUpdate();

            }

            void eliminarImpuestosProducto() throws SQLException {
                String sql = "DELETE FROM productos_impuestos WHERE productos_id=?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, p.getId());
                ps.executeUpdate();
            }

            boolean existeImpuestoProducto(ImpuestosBean imp) throws SQLException {
                String sql = "SELECT ID FROM productos_impuestos WHERE ID = ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, imp.getId());
                ResultSet re = ps.executeQuery();
                return re.next();
            }

            void insertarImpuestoProducto(ImpuestosBean impuesto) throws SQLException {
                String sql = "INSERT INTO productos_impuestos ( "
                        + "  id, productos_id, impuestos_id, iva_incluido, tipo)\n" + "  VALUES (?, ?, ?, ?,?);";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, impuesto.getId());
                ps.setLong(2, p.getId());
                ps.setLong(3, impuesto.getImpuestos_id());
                ps.setString(4, impuesto.isIva_incluido() ? "S" : "N");
                ps.setString(5, impuesto.getTipo() != null ? impuesto.getTipo() : "");
                ps.executeUpdate();
            }

            void actualizarImpuestoProducto(ImpuestosBean impuesto) throws SQLException {
                String sql = "UPDATE productos_impuestos SET productos_id=?, impuestos_id=?, iva_incluido=? , tipo = ? WHERE id=?;";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, p.getId());
                ps.setLong(2, impuesto.getImpuestos_id());
                ps.setString(3, impuesto.isIva_incluido() ? "S" : "N");
                ps.setString(4, impuesto.getTipo() != null ? impuesto.getTipo() : "");
                ps.setLong(5, impuesto.getId());
                ps.executeUpdate();
            }
        }
        boolean creado = true;
        Inner innerCall = new Inner();
        try {
            if (p.getCategorias() != null && !p.getCategorias().isEmpty()) {
                innerCall.crearTablaGruposTipos();
                for (CategoriaBean cate : p.getCategorias()) {
                    boolean existeGruposTipos = innerCall.existeGruposTipos(cate);
                    if (!existeGruposTipos) {
                        innerCall.insertarGruposTipos(cate);
                    } else {
                        innerCall.actualizarGruposTipos(cate);
                    }
                }
            }
            if (index == 0) {
                for (int j = 0; j < tipos.size(); j++) {
                    JsonObject tipo = tipos.get(j).getAsJsonObject();
                    boolean existeTipoProducto = innerCall.existeTipoProducto(tipo);
                    if (!existeTipoProducto) {
                        innerCall.insertarTipoProducto(tipo);
                    } else {
                        innerCall.actualizarTipoProducto(tipo);
                    }
                }
            }
            if (p.getCategorias() != null && !p.getCategorias().isEmpty()) {
                innerCall.crearTablaGrupos();
                for (CategoriaBean categoria : p.getCategorias()) {
                    if (categoria.getGrupo() != null) {
                        boolean existeGrupo = innerCall.existeGrupo(categoria);
                        if (!existeGrupo) {
                            innerCall.insertarGrupo(categoria);
                        } else {
                            innerCall.actualizarGrupo(categoria);
                        }
                    }
                }
            }
            if (p.getUnidades_medida_id() > 0) {
                boolean existeUnidad = innerCall.existeUnidad();
                if (!existeUnidad) {
                    innerCall.insertarUnidad();
                } else {
                    innerCall.actualizarUnidad();
                }
            }

            boolean existeProducto = innerCall.existeProducto();
            if (!existeProducto) {
                innerCall.insertarProducto();
            } else {
                innerCall.actualizarProducto();
            }

            if (p.getCategorias() != null && !p.getCategorias().isEmpty()) {
                innerCall.crearTablaGrupoEntidad();
                for (CategoriaBean categoria : p.getCategorias()) {
                    boolean existeGrupoEntidad = innerCall.existeGrupoEntidad(categoria);
                    if (!existeGrupoEntidad) {
                        innerCall.insertarGrupoEntidad(categoria);
                    }
                }
            }
            innerCall.crearTablaImpuesto();
            innerCall.crearTablaProductosImpuestos();
            innerCall.eliminarImpuestosProducto();
            if (p.getImpuestos() != null && !p.getImpuestos().isEmpty()) {
                for (ImpuestosBean impuesto : p.getImpuestos()) {
                    boolean existeImpuesto = innerCall.existeImpuesto(impuesto);
                    if (!existeImpuesto) {
                        innerCall.insertarImpuestos(impuesto);
                    } else {
                        innerCall.actualizarImpuestos(impuesto);
                    }
                    boolean existeImpuestoProducto = innerCall.existeImpuestoProducto(impuesto);
                    if (!existeImpuestoProducto) {
                        innerCall.insertarImpuestoProducto(impuesto);
                    } else {
                        innerCall.actualizarImpuestoProducto(impuesto);
                    }
                }
            }
        } catch (SQLException s) {
            NovusUtils.printLn(s.getMessage());
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, s);
            creado = false;
        } catch (Exception s) {
            NovusUtils.printLn(s.getMessage());
            creado = false;
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, s);
        }
        return creado;
    }

    public boolean borraTodosImpuestos(long productoId) throws DAOException {
        Connection conexion = Main.obtenerConexionAsync("lazoexpressregistry");
        boolean limpiado = true;
        try {
            String sql = "DELETE FROM PRODUCTOS_IMPUESTOS";
            if (productoId > 0) {
                sql = sql + " WHERE productos_id =" + productoId;
            }
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);
            NovusUtils.printLn(s.getMessage());
            limpiado = false;
        }
        return limpiado;
    }

    public void deleteIdentificadores(long productoId) {
        Connection conexion = Main.obtenerConexionAsync("lazoexpressregistry");
        String sql = "delete from identificadores where origen = 2";
        if (productoId > 0) {
            sql = sql + " and entidad_id =" + productoId;
        }
        try {
            Statement st = conexion.createStatement();
            int rs = st.executeUpdate(sql);
            if (rs == 1) {
                NovusUtils.printLn(Main.ANSI_GREEN + "funcionó la eliminación " + Main.ANSI_RESET);
            } else {
                NovusUtils.printLn(Main.ANSI_RED + "no hay datos para eliminar" + Main.ANSI_RESET);
            }
        } catch (SQLException ex) {
            NovusUtils.printLn(Main.ANSI_RED + "Error al hacer la eliminacion: " + ex + Main.ANSI_RESET);
        }

    }

    public boolean createBloqueado(int index, ProductoBean p, CredencialBean cr, JsonArray tipos) throws DAOException {
        class Inner {

            Connection conexionRegistry = Main.obtenerConexionAsync("lazoexpressregistry");

            boolean existeGruposTipos(CategoriaBean cate) throws SQLException {
                String sql = "SELECT ID FROM GRUPOS_TIPOS WHERE ID = ?";
                PreparedStatement ps = conexionRegistry.prepareStatement(sql);
                ps.setLong(1, cate.getGruposTiposId());
                ResultSet re = ps.executeQuery();
                return re.next();
            }

            void insertarGruposTipos(CategoriaBean cate) throws SQLException {
                String sql = "INSERT INTO grupos_tipos ( id, descripcion, estado, entidad) " + " VALUES (?, ?, ?, ?);";
                PreparedStatement ps = conexionRegistry.prepareStatement(sql);
                ps.setLong(1, cate.getGruposTiposId());
                ps.setString(2, cate.getGrupo() != null ? cate.getGrupo() : "");
                ps.setString(3, "A");
                ps.setLong(4, cate.getGp_id());
                ps.executeUpdate();
            }

            void actualizarGruposTipos(CategoriaBean cate) throws SQLException {
                String sql = "UPDATE grupos_tipos SET descripcion=?, estado=?, entidad=?  WHERE id=?";
                PreparedStatement ps = conexionRegistry.prepareStatement(sql);
                ps.setString(1, cate.getGrupo() != null ? cate.getGrupo() : "");
                ps.setString(2, "A");
                ps.setLong(3, cate.getGp_id());
                ps.setLong(4, cate.getGruposTiposId());
                ps.executeUpdate();
            }

            boolean existeTipoProducto(JsonObject tipo) throws SQLException {
                String sql = "SELECT ID FROM productos_tipos WHERE ID = ?";
                PreparedStatement ps = conexionRegistry.prepareStatement(sql);
                ps.setLong(1, tipo.get("id").getAsLong());
                ResultSet re = ps.executeQuery();
                return re.next();
            }

            void insertarTipoProducto(JsonObject tipo) throws SQLException {
                String sql = "INSERT INTO productos_tipos( id, descripcion, estado) " + " VALUES (?, ?, ?);";
                PreparedStatement ps = conexionRegistry.prepareStatement(sql);
                ps.setLong(1, tipo.get("id").getAsLong());
                ps.setString(2, tipo.get("descripcion").getAsString());
                ps.setString(3, tipo.get("estado").getAsString());
                ps.executeUpdate();
            }

            void actualizarTipoProducto(JsonObject tipo) throws SQLException {
                String sql = "UPDATE productos_tipos SET descripcion=?, estado=?  WHERE id=?";
                PreparedStatement ps = conexionRegistry.prepareStatement(sql);
                ps.setString(1, tipo.get("descripcion").getAsString());
                ps.setString(2, tipo.get("estado").getAsString());
                ps.setLong(3, tipo.get("id").getAsLong());
                ps.executeUpdate();
            }

            Connection conexionCore = Main.obtenerConexionAsync("lazoexpresscore");

            boolean existeTipoProductoCore(JsonObject tipo) throws SQLException {
                String sql = "SELECT ID FROM productos_tipos WHERE ID = ?";
                PreparedStatement ps = conexionCore.prepareStatement(sql);
                ps.setLong(1, tipo.get("id").getAsLong());
                ResultSet re = ps.executeQuery();
                return re.next();
            }

            void insertarTipoProductoCore(JsonObject tipo) throws SQLException {
                String sql = "INSERT INTO productos_tipos( id, descripcion, estado) " + " VALUES (?, ?, ?);";
                PreparedStatement ps = conexionCore.prepareStatement(sql);
                ps.setLong(1, tipo.get("id").getAsLong());
                ps.setString(2, tipo.get("descripcion").getAsString());
                ps.setString(3, tipo.get("estado").getAsString());
                ps.executeUpdate();
            }

            void actualizarTipoProductoCore(JsonObject tipo) throws SQLException {
                String sql = "UPDATE productos_tipos SET descripcion=?, estado=?  WHERE id=?";
                PreparedStatement ps = conexionCore.prepareStatement(sql);
                ps.setString(1, tipo.get("descripcion").getAsString());
                ps.setString(2, tipo.get("estado").getAsString());
                ps.setLong(3, tipo.get("id").getAsLong());
                ps.executeUpdate();
            }

            boolean existeGrupo(CategoriaBean categoria) throws SQLException {
                String sql = "SELECT ID FROM GRUPOS WHERE ID = ?";
                PreparedStatement ps = conexionRegistry.prepareStatement(sql);
                ps.setLong(1, categoria.getGp_id());
                ResultSet re = ps.executeQuery();
                return re.next();
            }

            void insertarGrupo(CategoriaBean categoria) throws SQLException {
                String sql = "INSERT INTO grupos ("
                        + " id, grupo, estado, grupos_tipos_id, empresas_id, grupos_id, url_foto) "
                        + " VALUES (?, ?, ?, ?, ?, NULL, ?);";
                PreparedStatement ps = conexionRegistry.prepareStatement(sql);
                ps.setLong(1, categoria.getGp_id());
                ps.setString(2, categoria.getGrupo() != null ? categoria.getGrupo() : "");
                ps.setString(3, "A");
                ps.setLong(4, categoria.getGruposTiposId());
                ps.setLong(5, cr.getEmpresas_id());
                ps.setString(6, categoria.getUrl_foto() != null ? categoria.getUrl_foto() : "");
                ps.executeUpdate();
            }

            void actualizarGrupo(CategoriaBean categoria) throws SQLException {
                String sql = "UPDATE grupos SET "
                        + " grupo=?, estado=?, grupos_tipos_id=?, empresas_id=?, grupos_id=?, url_foto=? "
                        + " WHERE id=?";
                PreparedStatement ps = conexionRegistry.prepareStatement(sql);
                ps.setString(1, categoria.getGrupo() != null ? categoria.getGrupo() : "");
                ps.setString(2, "A");
                ps.setLong(3, categoria.getGruposTiposId());
                ps.setLong(4, cr.getEmpresas_id());
                ps.setString(5, categoria.getUrl_foto() != null ? categoria.getUrl_foto() : "");
                ps.setLong(6, categoria.getGp_id());
                ps.executeUpdate();
            }

            boolean existeUnidad() throws SQLException {
                String sql = "SELECT ID FROM unidades WHERE ID = ?";
                PreparedStatement ps = conexionRegistry.prepareStatement(sql);
                ps.setLong(1, p.getUnidades_medida_id());
                ResultSet re = ps.executeQuery();
                return re.next();
            }

            void insertarUnidad() throws SQLException {
                String sql = "INSERT INTO unidades ( " + " id, descripcion, valor, empresas_id, estado,unidad_basica)"
                        + " VALUES (?, ?, ?, ?, 'A', NULL)";
                PreparedStatement ps = conexionRegistry.prepareStatement(sql);
                ps.setLong(1, p.getUnidades_medida_id());
                ps.setString(2, p.getUnidades_medida());
                ps.setFloat(3, p.getUnidades_medida_valor());
                ps.setFloat(4, cr.getEmpresas_id());
                ps.executeUpdate();
            }

            void actualizarUnidad() throws SQLException {
                String sql = "UPDATE unidades SET "
                        + " descripcion=?, valor=?, empresas_id=?, estado='A', unidad_basica=NULL  " + " WHERE id=?";
                PreparedStatement ps = conexionRegistry.prepareStatement(sql);
                ps.setString(1, p.getUnidades_medida());
                ps.setFloat(2, p.getUnidades_medida_valor());
                ps.setFloat(3, cr.getEmpresas_id());
                ps.setLong(4, p.getUnidades_medida_id());
                ps.executeUpdate();
            }

            boolean existeIdentificadorOrigenProducto() throws SQLException {
                String sql = "SELECT ID FROM identificadores_origenes WHERE ID IN(1,2)";
                PreparedStatement ps = conexionRegistry.prepareStatement(sql);
                ResultSet re = ps.executeQuery();
                return re.next();
            }

            void insertarIdentificadorOrigenProducto() throws SQLException {
                String sql = "INSERT INTO identificadores_origenes (id, descripcion, estado) "
                        + " VALUES (1, 'PRODUCTOS_PLU', 'A');"
                        + "INSERT INTO identificadores_origenes (id, descripcion, estado) "
                        + " VALUES (2, 'PRODUCTOS_CODIGO_BARRA', 'A');";
                PreparedStatement ps = conexionRegistry.prepareStatement(sql);
                ps.executeUpdate();

            }

            boolean existeProducto() throws SQLException {
                String sql = "SELECT ID FROM productos WHERE ID = ?";
                PreparedStatement ps = conexionRegistry.prepareStatement(sql);
                ps.setLong(1, p.getId());
                ResultSet re = ps.executeQuery();
                return re.next();
            }

            void insertarProducto() throws SQLException {
                String sql = "INSERT INTO productos ( "
                        + "        id, empresas_id, plu, descripcion, precio, puede_vender, puede_comprar, "
                        + "          ingrediente, tipo, url_foto, unidades_medida, unidades_contenido, "
                        + "            usa_balanza, estado, create_user, create_date, update_user, update_date, "
                        + "            favorito, seguimiento, promocion, cantidad_ingredientes, cantidad_impuestos,p_atributos) "
                        + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                        + "    ?, ?, 1, now(), null, null, ?, ?, ?, ?, ?, ?::json);";
                PreparedStatement ps = conexionRegistry.prepareStatement(sql);
                ps.setLong(1, p.getId());
                ps.setLong(2, cr.getEmpresas_id());
                ps.setString(3, p.getPlu());
                ps.setString(4, p.getDescripcion() != null ? p.getDescripcion().toUpperCase() : "");
                ps.setFloat(5, p.getPrecio());
                ps.setString(6, p.isPuede_vender() ? "S" : "N");
                ps.setString(7, p.isPuede_comprar() ? "S" : "N");
                ps.setString(8, p.isIngrediente() ? "S" : "N");
                ps.setInt(9, p.getTipo());
                if (p.getUrl_foto() != null) {
                    ps.setString(10, p.getUrl_foto());
                } else {
                    ps.setNull(10, Types.NULL);
                }
                ps.setLong(11, p.getUnidades_medida_id());
                ps.setLong(12, p.getUnidades_contenido_id());
                ps.setString(13, p.getUsa_balanza());
                ps.setString(14, ProductoBean.BLOQUEADO);
                if (p.isFavorito()) {
                    ps.setString(15, p.isFavorito() ? "S" : "N");
                } else {
                    ps.setNull(15, Types.NULL);
                }
                if (p.getDispensado() != null) {
                    ps.setString(16, p.getDispensado());
                } else {
                    ps.setNull(16, Types.NULL);
                }
                if (p.getPromocion() != null) {
                    ps.setString(17, p.getPromocion());
                } else {
                    ps.setNull(17, Types.NULL);
                }
                ps.setInt(18, p.getCantidadIngredientes());
                ps.setInt(19, p.getCantidadImpuestos());
                ps.setString(20, p.getAtributos() != null ? p.getAtributos().toString() : null);

                ps.executeUpdate();
            }

            void actualizarProducto() throws SQLException {
                String sqlup = "UPDATE productos "
                        + "   SET empresas_id=?, plu=?, descripcion=?, precio=?, puede_vender=?, "
                        + "       puede_comprar=?, ingrediente=?, tipo=?, url_foto=?, unidades_medida=?, "
                        + "       unidades_contenido=?, usa_balanza=?, estado=?, favorito=?, seguimiento=?, promocion=?,"
                        + "       cantidad_ingredientes=?, cantidad_impuestos=?, p_atributos=?::json WHERE id=?";
                PreparedStatement ps2 = conexionRegistry.prepareStatement(sqlup);
                ps2.setLong(1, cr.getEmpresas_id());
                ps2.setString(2, p.getPlu());
                ps2.setString(3, p.getDescripcion().toUpperCase());
                ps2.setFloat(4, p.getPrecio());
                ps2.setString(5, p.isPuede_vender() ? "S" : "N");
                ps2.setString(6, p.isPuede_comprar() ? "S" : "N");
                ps2.setString(7, p.isIngrediente() ? "S" : "N");
                ps2.setInt(8, p.getTipo());
                if (p.getUrl_foto() != null) {
                    ps2.setString(9, p.getUrl_foto());
                } else {
                    ps2.setNull(9, Types.NULL);
                }
                ps2.setLong(10, p.getUnidades_medida_id());
                ps2.setLong(11, p.getUnidades_contenido_id());
                ps2.setString(12, p.getUsa_balanza());
                ps2.setString(13, ProductoBean.BLOQUEADO);
                if (p.isFavorito()) {
                    ps2.setString(14, p.isFavorito() ? "S" : "N");
                } else {
                    ps2.setNull(14, Types.NULL);
                }
                if (p.getDispensado() != null) {
                    ps2.setString(15, p.getDispensado());
                } else {
                    ps2.setNull(15, Types.NULL);
                }
                if (p.getPromocion() != null) {
                    ps2.setString(16, p.getPromocion());
                } else {
                    ps2.setNull(16, Types.NULL);
                }
                ps2.setInt(17, p.getCantidadIngredientes());
                ps2.setInt(18, p.getCantidadImpuestos());
                ps2.setString(19, p.getAtributos() != null ? p.getAtributos().toString() : null);
                ps2.setLong(20, p.getId());

                ps2.executeUpdate();
                
                //  NUEVO: Invalidar cache después de actualizar producto (Registry)
                if (productoUpdateInterceptor != null && p != null) {
                    productoUpdateInterceptor.onProductoActualizado(p.getId());
                }
            }

            void insertarIdentificadoresProductos(IdentificadoresBean identi) throws SQLException {
                String sql = "INSERT INTO identificadores ("
                        + "            id, identificador, origen, placa_vin, fecha_instalacion, fecha_revision, "
                        + "            fecha_vencimiento, estado, create_user, create_date, update_user, "
                        + "            update_date, empresas_id, entidad_id ) "
                        + "    VALUES (?, ?, ?, null, now(), null, " + "    NULL, ?, 1, NOW(), NULL,  NULL, ?, ? );";
                PreparedStatement ps = conexionRegistry.prepareStatement(sql);

                ps.setLong(1, identi.getId());
                ps.setString(2, identi.getIdentificador() != null ? identi.getIdentificador() : "");
                ps.setLong(3, Long.parseLong(identi.getOrigen()));
                ps.setString(4, identi.getEstado() != null ? identi.getEstado() : "A");
                ps.setLong(5, Main.credencial.getEmpresas_id());
                ps.setLong(6, p.getId());
                ps.executeUpdate();
            }

            void actualizarIdentificadoresProductos(IdentificadoresBean identi) {
                try {
                    String sql = "UPDATE identificadores SET identificador = ?, estado= ?, entidad_id= ? ,origen=? where ID = ?";
                    PreparedStatement ps = conexionRegistry.prepareStatement(sql);
                    ps.setString(1, identi.getIdentificador() != null ? identi.getIdentificador() : "");
                    ps.setString(2, identi.getEstado() != null ? identi.getEstado() : "A");
                    ps.setLong(3, p.getId());
                    ps.setLong(4, Long.parseLong(identi.getOrigen()));
                    ps.setLong(5, identi.getId());
                    ps.executeUpdate();
                } catch (SQLException ex) {
                    Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
                    NovusUtils.printLn(Main.ANSI_RED + "error con los codigos de barra este es el identificadores" + Main.ANSI_RESET);
                }
            }

            boolean existeIdentificadoresProductos(IdentificadoresBean identi) {
                boolean respuesta = false;
                try {
                    String sql = "SELECT ID FROM identificadores WHERE identificador = ?";
                    PreparedStatement ps = conexionRegistry.prepareStatement(sql);
                    ps.setString(1, identi.getIdentificador() != null ? identi.getIdentificador() : "");
                    respuesta = ps.executeQuery().next();
                } catch (SQLException ex) {
                    Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
                    NovusUtils.printLn(Main.ANSI_RED + "error con los codigos de barra este es el identificadores" + Main.ANSI_RESET);
                }
                return respuesta;
            }

            boolean existeGrupoEntidad(CategoriaBean categoria) throws SQLException {
                String sql = "SELECT ID FROM grupos_entidad WHERE grupo_id = ? AND entidad_id= ?  ";
                PreparedStatement ps = conexionRegistry.prepareStatement(sql);
                ps.setLong(1, categoria.getGp_id());
                ps.setLong(2, p.getId());
                return ps.executeQuery().next();
            }

            void insertarGrupoEntidad(CategoriaBean categoria) throws SQLException {
                String sql = "INSERT INTO grupos_entidad( id, grupo_id, entidad_id)"
                        + "  VALUES (nextval('grupos_entidad_id'), ?, ?);";
                PreparedStatement ps = conexionRegistry.prepareStatement(sql);
                ps.setLong(1, categoria.getGp_id());
                ps.setLong(2, p.getId());
                ps.executeUpdate();
            }

            void eliminarImpuestosProducto() throws SQLException {
                String sql = "DELETE FROM productos_impuestos WHERE productos_id=?";
                PreparedStatement ps = conexionRegistry.prepareStatement(sql);
                ps.setLong(1, p.getId());
                ps.executeUpdate();
            }

            boolean existeImpuesto(ImpuestosBean imp) throws SQLException {
                String sql = "SELECT ID FROM impuestos WHERE ID = ?";
                PreparedStatement ps = conexionRegistry.prepareStatement(sql);
                ps.setLong(1, imp.getImpuestos_id());
                ResultSet re = ps.executeQuery();
                return ps.executeQuery().next();
            }

            void insertarImpuestos(ImpuestosBean impuesto) throws SQLException {
                String sql = "INSERT INTO impuestos( id, descripcion, porcentaje_valor, valor, fecha_inicio,"
                        + "      fecha_fin, estado, empresas_id) " + "    VALUES (?, ?, ?, ?, null, null, 'A', ?);";
                PreparedStatement ps = conexionRegistry.prepareStatement(sql);
                ps.setLong(1, impuesto.getImpuestos_id());
                ps.setString(2, impuesto.getDescripcion() != null ? impuesto.getDescripcion().toUpperCase() : "");
                ps.setString(3, impuesto.getPorcentaje_valor());
                ps.setFloat(4, impuesto.getValor());
                ps.setLong(5, cr.getEmpresas_id());
                ps.executeUpdate();
            }

            void actualizarImpuestos(ImpuestosBean impuesto) throws SQLException {
                String sql = "UPDATE impuestos "
                        + "   SET descripcion=?, porcentaje_valor=?, valor=?, fecha_inicio=null, "
                        + "   fecha_fin=null, estado='A', empresas_id=? WHERE id=?";
                PreparedStatement ps = conexionRegistry.prepareStatement(sql);
                ps.setString(1, impuesto.getDescripcion() != null ? impuesto.getDescripcion().toUpperCase() : "");
                ps.setString(2, impuesto.getPorcentaje_valor());
                ps.setFloat(3, impuesto.getValor());
                ps.setLong(4, cr.getEmpresas_id());
                ps.setLong(5, impuesto.getImpuestos_id());
                ps.executeUpdate();
            }

            boolean existeImpuestoProducto(ImpuestosBean imp) throws SQLException {
                String sql = "SELECT ID FROM productos_impuestos WHERE ID = ?";
                PreparedStatement ps = conexionRegistry.prepareStatement(sql);
                ps.setLong(1, imp.getId());
                return ps.executeQuery().next();
            }

            void insertarImpuestoProducto(ImpuestosBean impuesto) throws SQLException {
                String sql = "INSERT INTO productos_impuestos ( "
                        + "  id, productos_id, impuestos_id, iva_incluido, tipo)\n" + "  VALUES (?, ?, ?, ?,?);";
                PreparedStatement ps = conexionRegistry.prepareStatement(sql);
                ps.setLong(1, impuesto.getId());
                ps.setLong(2, p.getId());
                ps.setLong(3, impuesto.getImpuestos_id());
                ps.setString(4, impuesto.isIva_incluido() ? "S" : "N");
                ps.setString(5, impuesto.getTipo() != null ? impuesto.getTipo() : "");
                ps.executeUpdate();
            }

            void actualizarImpuestoProducto(ImpuestosBean impuesto) throws SQLException {
                String sql = "UPDATE productos_impuestos SET productos_id=?, impuestos_id=?, iva_incluido=? , tipo = ? WHERE id=?;";
                PreparedStatement ps = conexionRegistry.prepareStatement(sql);
                ps.setLong(1, p.getId());
                ps.setLong(2, impuesto.getImpuestos_id());
                ps.setString(3, impuesto.isIva_incluido() ? "S" : "N");
                ps.setString(4, impuesto.getTipo() != null ? impuesto.getTipo() : "");
                ps.setLong(5, impuesto.getId());
                ps.executeUpdate();
            }
        }
        boolean creado = true;
        Inner innerCall = new Inner();
        try {
            if (p.getCategorias() != null && !p.getImpuestos().isEmpty()) {
                for (CategoriaBean cate : p.getCategorias()) {
                    boolean existeGruposTipos = innerCall.existeGruposTipos(cate);
                    if (!existeGruposTipos) {
                        innerCall.insertarGruposTipos(cate);
                    } else {
                        innerCall.actualizarGruposTipos(cate);
                    }
                }
            }
            if (index == 0) {
                JsonObject tipoCombustible = new JsonObject();
                tipoCombustible.addProperty("id", -1);
                tipoCombustible.addProperty("descripcion", "COMBUSTIBLE");
                tipoCombustible.addProperty("estado", "A");
                tipos.add(tipoCombustible);
            }

            for (int j = 0; j < tipos.size(); j++) {
                JsonObject tipo = tipos.get(j).getAsJsonObject();
                boolean existeTipoProducto = innerCall.existeTipoProducto(tipo);
                if (!existeTipoProducto) {
                    innerCall.insertarTipoProducto(tipo);
                } else {
                    innerCall.actualizarTipoProducto(tipo);
                }

                boolean existeTipoProductoCore = innerCall.existeTipoProductoCore(tipo);
                if (!existeTipoProductoCore) {
                    innerCall.insertarTipoProductoCore(tipo);
                } else {
                    innerCall.actualizarTipoProductoCore(tipo);
                }
            }

            if (p.getCategorias() != null && !p.getImpuestos().isEmpty()) {
                for (CategoriaBean categoria : p.getCategorias()) {
                    if (categoria.getGrupo() != null) {
                        boolean existeGrupo = innerCall.existeGrupo(categoria);
                        if (!existeGrupo) {
                            innerCall.insertarGrupo(categoria);
                        } else {
                            innerCall.actualizarGrupo(categoria);
                        }
                    }
                }
            }
            if (p.getUnidades_medida_id() > 0) {
                boolean existeUnidad = innerCall.existeUnidad();
                if (!existeUnidad) {
                    innerCall.insertarUnidad();
                } else {
                    innerCall.actualizarUnidad();
                }
            }
            boolean existeIdentificadorOrigenProducto = innerCall.existeIdentificadorOrigenProducto();
            if (!existeIdentificadorOrigenProducto) {
                innerCall.insertarIdentificadorOrigenProducto();
            }
            boolean existeProducto = innerCall.existeProducto();
            if (!existeProducto) {
                innerCall.insertarProducto();
            } else {
                innerCall.actualizarProducto();
            }
            if (p.getIdentificadores() != null && !p.getImpuestos().isEmpty()) {
                for (IdentificadoresBean identi : p.getIdentificadores()) {
                    boolean existeIdentificadoresProductos = innerCall.existeIdentificadoresProductos(identi);
                    if (!existeIdentificadoresProductos) {
                        innerCall.insertarIdentificadoresProductos(identi);
                    } else {
                        innerCall.actualizarIdentificadoresProductos(identi);
                    }
                }
            }
            if (p.getCategorias() != null && !p.getImpuestos().isEmpty()) {
                for (CategoriaBean categoria : p.getCategorias()) {
                    boolean existeGrupoEntidad = innerCall.existeGrupoEntidad(categoria);
                    if (!existeGrupoEntidad) {
                        innerCall.insertarGrupoEntidad(categoria);
                    }
                }
            }
            innerCall.eliminarImpuestosProducto();
            if (p.getImpuestos() != null && !p.getImpuestos().isEmpty()) {
                for (ImpuestosBean impuesto : p.getImpuestos()) {
                    boolean existeImpuesto = innerCall.existeImpuesto(impuesto);
                    if (!existeImpuesto) {
                        innerCall.insertarImpuestos(impuesto);
                    } else {
                        innerCall.actualizarImpuestos(impuesto);
                    }
                    boolean existeImpuestoProducto = innerCall.existeImpuestoProducto(impuesto);
                    if (!existeImpuestoProducto) {
                        innerCall.insertarImpuestoProducto(impuesto);
                    } else {
                        innerCall.actualizarImpuestoProducto(impuesto);
                    }
                }
            }
        } catch (SQLException s) {
            NovusUtils.printLn(s.getMessage());
            creado = false;
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, s);
        } catch (Exception s) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, s);
            NovusUtils.printLn(s.getMessage());
            creado = false;
        }
        return creado;
    }

    public boolean createBodega(BodegaBean bodega, CredencialBean cr) throws DAOException {
        class Inner {

            Connection conexion = Main.obtenerConexionAsync("lazoexpressregistry");

            boolean existeBodega() throws SQLException {
                String sql = "SELECT ID FROM BODEGAS WHERE ID=?;";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, bodega.getId());
                return ps.executeQuery().next();
            }

            void insertarBodega() throws SQLException {
                String sql = "INSERT INTO bodegas ("
                        + " id, descripcion, estado, empresas_id, codigo, dimension, ubicacion,"
                        + "numeros_stand,finalidad)  VALUES (?, ?, ?, ?, ?, ?, ?, ?,?);";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, bodega.getId());
                ps.setString(2, bodega.getDescripcion() != null ? bodega.getDescripcion() : "");
                ps.setString(3, bodega.getEstado());
                ps.setLong(4, bodega.getEmpresaId());
                ps.setString(5, bodega.getCodigo() != null ? bodega.getCodigo() : "");
                ps.setString(6, bodega.getDimension());
                ps.setString(7, bodega.getUbicacion());
                ps.setInt(8, bodega.getNumeroStand());
                ps.setString(9, bodega.getFinalidad());
                ps.executeUpdate();
            }

            void actualizarBodega() throws SQLException {
                String sql = "UPDATE bodegas "
                        + "   SET descripcion=?, estado=?, empresas_id=?, codigo=?, dimension=?, "
                        + "   ubicacion=?, numeros_stand=?,finalidad=? WHERE id=?";

                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setString(1, bodega.getDescripcion() != null ? bodega.getDescripcion() : "");
                ps.setString(2, bodega.getEstado());
                ps.setLong(3, bodega.getEmpresaId());
                ps.setString(4, bodega.getCodigo() != null ? bodega.getCodigo() : "");
                ps.setString(5, bodega.getDimension());
                ps.setString(6, bodega.getUbicacion());
                ps.setInt(7, bodega.getNumeroStand());
                ps.setString(8, bodega.getFinalidad());
                ps.setLong(9, bodega.getId());

                ps.executeUpdate();
            }
        }
        Inner innerCall = new Inner();
        boolean insertado = true;
        try {
            boolean existeBodega = innerCall.existeBodega();
            if (!existeBodega) {
                innerCall.insertarBodega();
            } else {
                innerCall.actualizarBodega();
            }
        } catch (SQLException s) {
            NovusUtils.printLn(s.getMessage());
            insertado = false;
        }
        return insertado;
    }

    public boolean createConsecutivos(ArrayList<ConsecutivoBean> consecutivos, CredencialBean cr) throws DAOException {
        class Inner {

            Connection conexion = Main.obtenerConexionAsync("lazoexpressregistry");

            void insertarConsecutivo(ConsecutivoBean consecutivo) throws SQLException {
                String sql = "INSERT INTO consecutivos ("
                        + "            id, empresas_id, tipo_documento, prefijo, fecha_inicio, fecha_fin, "
                        + "            consecutivo_inicial, consecutivo_final, consecutivo_actual,  estado, "
                        + "            resolucion, observaciones, equipos_id, cs_atributos) "
                        + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::json);";

                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, consecutivo.getId());
                ps.setLong(2, cr.getEmpresas_id());
                ps.setLong(3, consecutivo.getTipo_documento());
                ps.setString(4, consecutivo.getPrefijo());
                if (consecutivo.getFecha_inicio() != null) {
                    ps.setDate(5, new Date(consecutivo.getFecha_inicio().getTime()));
                } else {
                    ps.setNull(5, Types.NULL);
                }
                if (consecutivo.getFecha_fin() != null) {
                    ps.setDate(6, new Date(consecutivo.getFecha_fin().getTime()));
                } else {
                    ps.setNull(6, Types.NULL);
                }
                ps.setLong(7, consecutivo.getConsecutivo_inicial());
                if (consecutivo.getConsecutivo_final() != 0) {
                    ps.setLong(8, consecutivo.getConsecutivo_final());
                } else {
                    ps.setNull(8, Types.NULL);
                }
                if (consecutivo.getConsecutivo_actual() != 0) {
                    ps.setLong(9, consecutivo.getConsecutivo_actual());
                } else {
                    ps.setNull(9, Types.NULL);
                }
                ps.setString(10, consecutivo.getEstado());
                ps.setString(11, consecutivo.getResolucion());
                ps.setString(12, consecutivo.getObservaciones());
                if (consecutivo.getEquipo_id() != 0) {
                    ps.setLong(13, consecutivo.getEquipo_id());
                } else {
                    ps.setNull(13, Types.NULL);
                }
                ps.setString(14, consecutivo.getAtributos().toString());
                System.out.println(consecutivo.getAtributos());
                ps.executeUpdate();
            }

            void validarConsecutivoActual(ConsecutivoBean consecutivo) throws SQLException {
                String sql = "SELECT consecutivo_actual FROM consecutivos where id=? ";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, consecutivo.getId());
                ResultSet res = ps.executeQuery();
                while (res.next()) {
                    if (consecutivo.getConsecutivo_actual() < res.getLong("consecutivo_actual")) {
                        consecutivo.setConsecutivo_actual(res.getLong("consecutivo_actual"));
                    }
                }
            }

            void actualizarConsecutivo(ConsecutivoBean consecutivo) throws SQLException {
                this.validarConsecutivoActual(consecutivo);
                String sql = "UPDATE consecutivos "
                        + "   SET id=?, empresas_id=?, tipo_documento=?, prefijo=?, fecha_inicio=?, "
                        + "       fecha_fin=?, consecutivo_inicial=?, consecutivo_actual=?, consecutivo_final=?, "
                        + "       estado=?, resolucion=?, observaciones=?, equipos_id=?, cs_atributos=?::json " + " WHERE id=?;";

                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, consecutivo.getId());
                ps.setLong(2, cr.getEmpresas_id());
                ps.setLong(3, consecutivo.getTipo_documento());
                ps.setString(4, consecutivo.getPrefijo());
                if (consecutivo.getFecha_inicio() != null) {
                    ps.setDate(5, new Date(consecutivo.getFecha_inicio().getTime()));
                } else {
                    ps.setNull(5, Types.NULL);
                }
                if (consecutivo.getFecha_fin() != null) {
                    ps.setDate(6, new Date(consecutivo.getFecha_fin().getTime()));
                } else {
                    ps.setNull(6, Types.NULL);
                }
                ps.setLong(7, consecutivo.getConsecutivo_inicial());
                if (consecutivo.getConsecutivo_actual() != 0) {
                    ps.setLong(8, consecutivo.getConsecutivo_actual());
                } else {
                    ps.setNull(8, Types.NULL);
                }
                if (consecutivo.getConsecutivo_final() != 0) {
                    ps.setLong(9, consecutivo.getConsecutivo_final());
                } else {
                    ps.setNull(9, Types.NULL);
                }
                ps.setString(10, consecutivo.getEstado());
                ps.setString(11, consecutivo.getResolucion());
                ps.setString(12, consecutivo.getObservaciones());
                if (consecutivo.getEquipo_id() != 0) {
                    ps.setLong(13, consecutivo.getEquipo_id());
                } else {
                    ps.setNull(13, Types.NULL);
                }
                ps.setString(14, consecutivo.getAtributos().toString());
                ps.setLong(15, consecutivo.getId());
                int res = ps.executeUpdate();
                if (res >= 1) {
                    System.out.println("datos actualizados consecutivos");
                } else {
                    System.out.println("error al datos actualizados consecutivos");
                }
            }

            boolean existeConsecutivo(ConsecutivoBean consecutivo) throws SQLException {
                String sql = "SELECT ID FROM consecutivos WHERE ID = ? LIMIT 1";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, consecutivo.getId());
                ResultSet rs = ps.executeQuery();
                return ps.executeQuery().next();
            }

            void inactivarConsecutivos() throws SQLException {
                String sql = "UPDATE consecutivos SET estado='I'";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
            }

            void eliminarConsecutivosInactivos() throws SQLException {
                String sql = "DELETE FROM consecutivos WHERE estado='I'";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
            }
        }
        Inner innerCall = new Inner();
        boolean insertado = true;
        if (!consecutivos.isEmpty()) {
            try {
                innerCall.inactivarConsecutivos();
                for (ConsecutivoBean consecutivo : consecutivos) {
                    boolean existeConsecutivo = innerCall.existeConsecutivo(consecutivo);
                    if (!existeConsecutivo) {
                        innerCall.insertarConsecutivo(consecutivo);
                    } else {
                        innerCall.actualizarConsecutivo(consecutivo);
                    }
                }
                innerCall.eliminarConsecutivosInactivos();
            } catch (SQLException s) {
                NovusUtils.printLn(s.getMessage());
                insertado = false;
            }
        } else {
            insertado = false;
        }
        return insertado;
    }

    public void eliminarCategorias(long productoId) {
        Connection conexion = Main.obtenerConexionAsync("lazoexpressregistry");
        String sql = "DELETE FROM grupos_entidad";
        if (productoId > 0) {
            sql = sql + " WHERE entidad_id =" + productoId;
        }
        try {
            Statement stm = conexion.createStatement();
            stm.executeUpdate(sql);
        } catch (Exception ex) {
            System.out.println("Error al eliminar las asociaciones de las categoria " + ex);
        }

    }

    public boolean eliminarProductosDuplicados() {
        boolean status = false;
        Connection conexion = Main.obtenerConexionAsync("lazoexpressregistry");
        boolean isCDL = Main.TIPO_NEGOCIO
                .equals(NovusConstante.PARAMETER_CDL);
        String sql = "SELECT bp.id FROM BODEGAS B \n"
                + " inner join bodegas_productos bp on bp.bodegas_id = b.id \n"
                + " INNER JOIN PRODUCTOS P ON P.ID = bp.PRODUCTOS_ID \n"
                + " where bp.productos_id = P.ID and B.ID=B.ID  \n"
                + " AND (B.FINALIDAD)::text != (P.p_atributos->>'tipoStore')::text"
                + (isCDL ? " and P.p_atributos->>'tipoStore' != 'CDL';" : ";");
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                status = true;
                sql = " DELETE FROM BODEGAS_PRODUCTOS WHERE ID=?";
                try (PreparedStatement pst = conexion.prepareStatement(sql)) {
                    pst.setLong(1, re.getLong(1));
                    pst.executeUpdate();
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;
    }

    public boolean procesarCategorias(int index, CategoriaBean categoria, CredencialBean cr) throws DAOException {
        class Inner {

            Connection conexion = Main.obtenerConexionAsync("lazoexpressregistry");

            boolean existeGruposTipos() throws SQLException {
                String sql = "SELECT ID FROM grupos_tipos WHERE ID = ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, categoria.getGruposTiposId());
                return ps.executeQuery().next();
            }

            void insertarGruposTipos() throws SQLException {
                String sql = "INSERT INTO grupos_tipos(id, descripcion, estado, entidad) VALUES (?, ?, ?, ?);";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, categoria.getGruposTiposId());
                ps.setString(2, categoria.getGrupo() != null ? categoria.getGrupo() : "");
                ps.setString(3, categoria.getEstado());
                ps.setLong(4, categoria.getGp_id());
                ps.executeUpdate();
            }

            void actualizarGruposTipos() throws SQLException {
                String sql = "UPDATE grupos_tipos SET descripcion=?, estado=?, entidad=? WHERE id=?;";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setString(1, categoria.getGrupo() != null ? categoria.getGrupo() : "");
                ps.setString(2, categoria.getEstado());
                ps.setLong(3, categoria.getGp_id());
                ps.setLong(4, categoria.getGruposTiposId());
                ps.executeUpdate();
            }

            boolean existeGrupo() throws SQLException {
                String sql = "SELECT ID FROM grupos WHERE ID = ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, categoria.getId());
                return ps.executeQuery().next();
            }

            void insertarGrupo() throws SQLException {
                String sql = "INSERT INTO grupos("
                        + "   id, grupo, estado, grupos_tipos_id, empresas_id, grupos_id, url_foto, atributos)"
                        + "  VALUES (?, ?, ?, ?, ?, ?, ?, ?::json);";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, categoria.getId());
                ps.setString(2, categoria.getGrupo() != null ? categoria.getGrupo() : "");
                ps.setString(3, categoria.getEstado());
                ps.setLong(4, categoria.getGruposTiposId());
                ps.setLong(5, cr.getEmpresas_id());
                ps.setNull(6, Types.NULL);
                ps.setString(7, categoria.getUrl_foto() != null ? categoria.getUrl_foto() : "");
                ps.setString(8, categoria.getAtributos().toString());
                ps.executeUpdate();
            }

            void actualizarGrupo() throws SQLException {
                String sql = "UPDATE GRUPOS SET grupo= ?, estado=?, grupos_tipos_id = ?, atributos = ?::json WHERE id= ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setString(1, categoria.getGrupo() != null ? categoria.getGrupo() : "");
                ps.setString(2, categoria.getEstado());
                ps.setLong(3, categoria.getGruposTiposId());
                ps.setString(4, categoria.getAtributos().toString());
                ps.setLong(5, categoria.getId());
                ps.executeUpdate();
            }

            void eliminarGruposEntidades() throws SQLException {
                String sql = "DELETE FROM GRUPOS_ENTIDAD";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
            }

            boolean hayGruposInsertados() throws SQLException {
                String sql = "SELECT ID FROM grupos LIMIT 1";
                PreparedStatement ps = conexion.prepareStatement(sql);
                return ps.executeQuery().next();
            }
        }
        Inner innerCall = new Inner();
        boolean insertado = true;
        try {
            if (index == 0) {
                boolean existeGruposTipos = innerCall.existeGruposTipos();
                if (!existeGruposTipos) {
                    innerCall.insertarGruposTipos();
                } else {
                    innerCall.actualizarGruposTipos();
                }
//                boolean hayGruposInsertados = innerCall.hayGruposInsertados();
//                if (!hayGruposInsertados) {
//                    innerCall.eliminarGruposEntidades();
//                }
            }
            boolean existeGrupo = innerCall.existeGrupo();
            if (!existeGrupo) {
                innerCall.insertarGrupo();
            } else {
                innerCall.actualizarGrupo();
            }
        } catch (SQLException e) {
            insertado = false;
            NovusUtils.printLn(e.getMessage());
        }
        return insertado;
    }

    public boolean LimpiarInventariosInactivos() {
        Connection conexion = Main.obtenerConexionAsync("lazoexpressregistry");
        boolean limpiado = true;
        java.util.Date currentDate = new java.util.Date();

//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_PROCESS_MIN);
//            String sql = "create table bodegas_productos_hist_" + sdf.format(currentDate) + " as select * from bodegas_productos bp where bodegas_id in(  select b.id from bodegas b where estado ='I' );";
//            PreparedStatement ps = conexion.prepareStatement(sql);
//            ps.executeUpdate();
//        } catch (SQLException s) {
//            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, s);
//            NovusUtils.printLn(s.getMessage());
//            limpiado = false;
//        }
        try {
            String sql = "DELETE FROM BODEGAS_PRODUCTOS WHERE BODEGAS_ID IN(  SELECT  B.ID FROM BODEGAS B WHERE ESTADO='I' )";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException s) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, s);
            NovusUtils.printLn(s.getMessage());
            limpiado = false;
        }

        try {
            String sql = "DELETE FROM BODEGAS WHERE ESTADO='I' ";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException s) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, s);
            NovusUtils.printLn(s.getMessage());
            limpiado = false;
        }
        return limpiado;
    }

    public void limpiarInventarios() throws SQLException {
        Connection conexion = Main.obtenerConexionAsync("lazoexpressregistry");
        String sql = "DELETE FROM bodegas_productos";
        PreparedStatement ps = conexion.prepareStatement(sql);
        ps.executeUpdate();
    }

    public boolean procesarInventario(int i, long empresa, ProductoBean producto) throws DAOException {
        class Inner {

            Connection conexion = Main.obtenerConexionAsync("lazoexpressregistry");

            boolean existeInventario() throws SQLException {
                String sql = "SELECT ID FROM bodegas_productos WHERE productos_id = ?  and bodegas_id=?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, producto.getId());
                ps.setLong(2, producto.getBodega_producto_id());
                return ps.executeQuery().next();
            }

            boolean existeProducto() throws SQLException {
                String sql = "SELECT id FROM productos WHERE id = ?  ";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, producto.getId());
                return ps.executeQuery().next();
            }

            void insertarInventario() throws SQLException {
                String sql = "INSERT INTO bodegas_productos( "
                        + " id, productos_id, bodegas_id, saldo, cantidad_minima, cantidad_maxima, "
                        + "   empresas_id, tiempo_reorden, costo,unidades_id) "
                        + "    VALUES (nextval('bodegas_productos_id'), ?, ?, ?, ?, ?,  ?, ?, ?, ?);";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, producto.getId());
                ps.setLong(2, producto.getBodega_producto_id());
                ps.setFloat(3, producto.getSaldo());
                ps.setFloat(4, producto.getCantidadMinima());
                ps.setFloat(5, producto.getCantidadMaxima());
                ps.setLong(6, empresa);
                ps.setInt(7, producto.getTiempoReorden());
                ps.setFloat(8, producto.getCosto());
                ps.setLong(9, producto.getUnidades_medida_id());
                ps.executeUpdate();
            }

            void actualizarInventario() throws SQLException {
                String sql = "UPDATE bodegas_productos  SET saldo=?, cantidad_minima=?, "
                        + "  cantidad_maxima=?, empresas_id=?, tiempo_reorden=?, costo=? "
                        + " WHERE productos_id=? AND bodegas_id=?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setFloat(1, producto.getSaldo());
                ps.setFloat(2, producto.getCantidadMinima());
                ps.setFloat(3, producto.getCantidadMaxima());
                ps.setLong(4, empresa);
                ps.setInt(5, producto.getTiempoReorden());
                ps.setFloat(6, producto.getCosto());
                ps.setLong(7, producto.getId());
                ps.setLong(8, producto.getBodega_producto_id());
                ps.executeUpdate();
                
                //  NUEVO: Invalidar cache después de actualizar INVENTARIO (saldo/costo)
                if (productoUpdateInterceptor != null && producto != null) {
                    System.out.println(" Invalidando cache por cambio de INVENTARIO - Producto ID: " + producto.getId() + ", Nuevo saldo: " + producto.getSaldo());
                    productoUpdateInterceptor.onProductoActualizado(producto.getId());
                }
            }
        }
        boolean insertado;
        Inner innerCall = new Inner();
        try {
            boolean existeProducto = innerCall.existeProducto();
            if (existeProducto) {
                boolean existeInventario = innerCall.existeInventario();
                if (!existeInventario) {
                    innerCall.insertarInventario();
                } else {
                    innerCall.actualizarInventario();
                }
                insertado = true;
            } else {
                insertado = false;
            }
        } catch (SQLException s) {
            NovusUtils.printLn(s.getMessage());
            insertado = false;
        }
        return insertado;
    }

    public void limpiarTanquesExternos(String id) throws SQLException, DAOException {

        limpiaProductosTanqueExternos(id);

        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "DELETE FROM ct_bodegas WHERE id not in(".concat(id) + ")";
        try (PreparedStatement ps = conexion.prepareCall(sql);) {
            ps.executeUpdate();
        } catch (PSQLException ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            NovusUtils.printLn(ex.getMessage());
        } catch (SQLException ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            NovusUtils.printLn(ex.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            NovusUtils.printLn(ex.getMessage());
        }
    }

    public void limpiaProductosTanqueExternos(String id) throws SQLException, DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "DELETE FROM ct_bodegas_productos WHERE bodegas_id not in(".concat(id) + ")";
        try (PreparedStatement ps = conexion.prepareCall(sql);) {
            ps.executeUpdate();
        } catch (PSQLException ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            NovusUtils.printLn(ex.getMessage());
        } catch (SQLException ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            NovusUtils.printLn(ex.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            NovusUtils.printLn(ex.getMessage());
        }
    }

    public boolean createTanque(JsonObject tanque) throws DAOException {
        boolean result = true;
        class Inner {

            Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");

            void limpiarAforo() throws SQLException {
                String sql = "DELETE FROM CT_TABLA_AFORO WHERE bodegas_id = ?";
                PreparedStatement ps = conexion.prepareCall(sql);
                ps.setLong(1, tanque.get("id").getAsLong());
                ps.executeUpdate();
            }

            boolean existeTanque() throws SQLException {
                boolean existeTanque = false;
                String sql = "SELECT ID FROM CT_BODEGAS WHERE ID = ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, tanque.get("id").getAsLong());
                ResultSet re = ps.executeQuery();
                while (re.next()) {
                    existeTanque = true;
                }
                return existeTanque;
            }

            void insertarTanque() throws SQLException {
                String sql = "INSERT INTO ct_bodegas \n" + "(id, bodega, codigo, atributos) \n"
                        + "VALUES(?, ?, ?, ?::json); ";
                PreparedStatement ps = conexion.prepareCall(sql);
                ps.setLong(1, tanque.get("id").getAsLong());
                ps.setString(2, tanque.get("bodega").getAsString());
                ps.setString(3, tanque.get("codigo").getAsString());
                ps.setString(4, tanque.get("atributos").getAsJsonObject().toString());
                ps.executeUpdate();
            }

            void actualizarTanque() throws SQLException {
                String sql = "UPDATE ct_bodegas SET bodega=?, codigo = ? , atributos = ?::json WHERE ID= ? ";
                PreparedStatement ps = conexion.prepareCall(sql);
                ps.setString(1, tanque.get("bodega").getAsString());
                ps.setString(2, tanque.get("codigo").getAsString());
                ps.setString(3, tanque.get("atributos").getAsJsonObject().toString());
                ps.setLong(4, tanque.get("id").getAsLong());
                ps.executeUpdate();
            }
        }
        Inner innerCall = new Inner();
        try {
            boolean existeTanque = innerCall.existeTanque();
            if (!existeTanque) {
                innerCall.insertarTanque();
            } else {
                innerCall.actualizarTanque();
            }
            innerCall.limpiarAforo();
        } catch (PSQLException ex) {
            NovusUtils.printLn(ex.getMessage());
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            result = false;
        } catch (SQLException ex) {
            result = false;
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);

            NovusUtils.printLn(ex.getMessage());

        } catch (Exception ex) {
            result = false;
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);

            NovusUtils.printLn(ex.getMessage());

        }
        return result;

    }

    public boolean createUnidades(JsonObject unidad) throws DAOException {
        boolean result = true;
        class Inner {

            Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");

            boolean existeUnidades() throws SQLException {
                String sql = "SELECT ID FROM UNIDADES WHERE ID=?";
                PreparedStatement ps = conexion.prepareCall(sql);
                ps.setLong(1, unidad.get("id").getAsLong());
                return ps.executeQuery().next();
            }

            void insertarUnidades() throws SQLException {
                String sql = "INSERT INTO unidades\n" + "(id, descripcion, valor,empresas_id,estado, alias)\n"
                        + "VALUES(?, ?, ?, ?,'A', ?);";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, unidad.get("id").getAsLong());
                ps.setString(2, unidad.get("descripcion").getAsString());
                ps.setInt(3, unidad.get("valor").getAsInt());
                ps.setLong(4, Main.credencial.getEmpresas_id());
                ps.setString(5,
                        unidad.get("alias") != null && !unidad.get("alias").isJsonNull()
                        ? unidad.get("alias").getAsString()
                        : "");
                ps.executeUpdate();
            }

            void actualizarUnidades() throws SQLException {
                String sql = "UPDATE unidades set descripcion=?, valor=?, alias=? \n" + "WHERE id=?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setString(1, unidad.get("descripcion").getAsString());
                ps.setInt(2, unidad.get("valor").getAsInt());
                ps.setString(3,
                        unidad.get("alias") != null && !unidad.get("alias").isJsonNull()
                        ? unidad.get("alias").getAsString()
                        : "");
                ps.setLong(4, unidad.get("id").getAsLong());
                ps.executeUpdate();
            }
        }
        Inner innerCall = new Inner();
        try {
            boolean existeUnidades = innerCall.existeUnidades();
            if (!existeUnidades) {
                innerCall.insertarUnidades();
            } else {
                innerCall.actualizarUnidades();
            }
        } catch (SQLException ex) {
            NovusUtils.printLn(ex.getMessage());
            result = false;
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            NovusUtils.printLn(ex.getMessage());
            result = false;
        }
        return result;
    }

    public boolean crearDetalles(JsonObject detalle) throws DAOException {
        Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");
        boolean result = false;
        try {
            // crearProductos(detalle);
            String sql = "SELECT 1 FROM ct_bodegas_productos WHERE bodegas_id=? and productos_id=?";
            PreparedStatement ps = conexion.prepareCall(sql);
            ps.setLong(1, detalle.get("ct_bodegas_id").getAsLong());
            ps.setLong(2, detalle.get("productos_id").getAsLong());
            boolean existe = ps.executeQuery().next();
            if (!existe) {
                sql = "INSERT INTO ct_bodegas_productos\n"
                        + "(productos_id, bodegas_id, saldo, empresas_id, fecha_creacion, ultimo_actualizacion, altura)\n"
                        + "VALUES(?, ?, ?, ?, now(), null, 0);";
                ps = conexion.prepareCall(sql);
                ps.setLong(1, detalle.get("productos_id").getAsLong());
                ps.setLong(2, detalle.get("ct_bodegas_id").getAsLong());
                ps.setLong(3, detalle.get("saldo").getAsLong());
                ps.setLong(4, detalle.get("empresas_id").getAsLong());
                ps.executeUpdate();
            } else {
                sql = "UPDATE ct_bodegas_productos set saldo=?, ultimo_actualizacion=now() WHERE productos_id=? and bodegas_id=?";
                ps = conexion.prepareCall(sql);

                ps.setLong(1, detalle.get("saldo").getAsLong());
                ps.setLong(2, detalle.get("productos_id").getAsLong());
                ps.setLong(3, detalle.get("ct_bodegas_id").getAsLong());
                ps.executeUpdate();
                
                //  NUEVO: Invalidar cache después de actualizar SALDO en ct_bodegas_productos
                if (productoUpdateInterceptor != null) {
                    Long productoId = detalle.get("productos_id").getAsLong();
                    Long nuevoSaldo = detalle.get("saldo").getAsLong();
                    System.out.println(" Invalidando cache por cambio de SALDO CT - Producto ID: " + productoId + ", Nuevo saldo: " + nuevoSaldo);
                    productoUpdateInterceptor.onProductoActualizado(productoId);
                }

            }
            result = true;
        } catch (PSQLException ex) {
            throw new DAOException("ERROR: " + ex.getMessage());
        } catch (SQLException ex) {
            throw new DAOException("ERROR: " + ex.getMessage());
        } catch (Exception ex) {
            throw new DAOException("ERROR: " + ex.getMessage());
        }
        return result;
    }

    public void eliminarAforo() throws SQLException {
        Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");
        try {
            String sql = "DELETE FROM CT_TABLA_AFORO";
            PreparedStatement ps = conexion.prepareCall(sql);
            ps.executeUpdate();
        } catch (PSQLException ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            NovusUtils.printLn(ex.getMessage());
        } catch (SQLException ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            NovusUtils.printLn(ex.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            NovusUtils.printLn(ex.getMessage());
        }
    }

    public boolean registrarAforo(JsonArray aforoArray) throws DAOException {
        boolean result = true;
        class Inner {

            Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");

            void insertarAforo(JsonObject aforo) throws SQLException {
                String sql = "INSERT INTO CT_TABLA_AFORO (id, bodegas_id, atributos) VALUES(?, ?, ?::json);";
                PreparedStatement ps = conexion.prepareCall(sql);
                ps.setLong(1, aforo.get("id").getAsLong());
                ps.setLong(2, aforo.get("bodegas_id").getAsLong());
                ps.setString(3, aforo.get("atributos").getAsJsonObject().toString());
                ps.executeUpdate();
            }

        }

        Inner innerCall = new Inner();
        try {
            for (JsonElement aforo : aforoArray) {
                innerCall.insertarAforo(aforo.getAsJsonObject());
            }
        } catch (PSQLException ex) {
            NovusUtils.printLn(ex.getMessage());
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);

            result = false;
        } catch (SQLException ex) {
            NovusUtils.printLn(ex.getMessage());
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);

            result = false;
        } catch (Exception ex) {
            NovusUtils.printLn(ex.getMessage());
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);

            result = false;
        }
        return result;

    }

    public boolean crearProductosCombustible(JsonArray familias, JsonArray tipos, JsonArray unidades,
            JsonArray productos) throws DAOException {
        boolean insertado = true;
        final int NORMAL = 1;
        final int COMPUESTO = 2;
        final int PRODUCIDO = 3;
        class Inner {

            Connection conexionRegistry = Main.obtenerConexionAsync("lazoexpressregistry");
            Connection conexionCore = Main.obtenerConexionAsync("lazoexpresscore");

            boolean existeFamiliaProducto(JsonObject jsonFamilia) throws SQLException {
                String sql = "SELECT ID FROM PRODUCTOS_FAMILIAS WHERE ID=?";
                PreparedStatement ps = conexionCore.prepareCall(sql);
                ps.setLong(1, jsonFamilia.get("id").getAsLong());
                return ps.executeQuery().next();
            }

            void insertarFamiliaProducto(JsonObject jsonFamilia) throws SQLException {
                String sql = "INSERT INTO PRODUCTOS_FAMILIAS (id, codigo, atributos) VALUES(?,?,?::json);";
                PreparedStatement ps = conexionCore.prepareCall(sql);
                ps.setLong(1, jsonFamilia.get("id").getAsLong());
                ps.setString(2, jsonFamilia.get("descripcion").getAsString());
                ps.setString(3, jsonFamilia.get("atributos").getAsJsonObject().toString());
                ps.executeUpdate();
            }

            void actualizarFamiliaProducto(JsonObject jsonFamilia) throws SQLException {
                String sql = "UPDATE PRODUCTOS_FAMILIAS SET codigo=?, atributos = ?::json WHERE id=?;";
                PreparedStatement ps = conexionCore.prepareCall(sql);
                ps.setString(1, jsonFamilia.get("descripcion").getAsString());
                ps.setString(2, jsonFamilia.get("atributos").getAsJsonObject().toString());
                ps.setLong(3, jsonFamilia.get("id").getAsLong());
                ps.executeUpdate();
            }

            boolean existeTipoProducto(JsonObject jsonTipo) throws SQLException {
                String sql = "SELECT ID FROM PRODUCTOS_TIPOS WHERE ID =?";
                PreparedStatement ps = conexionCore.prepareCall(sql);
                ps.setLong(1, jsonTipo.get("id").getAsLong());
                return ps.executeQuery().next();
            }

            void insertarTipoProducto(JsonObject jsonTipo) throws SQLException {
                String sql = "INSERT INTO PRODUCTOS_TIPOS (id, descripcion, estado) VALUES(?,?,?);";
                PreparedStatement ps = conexionCore.prepareCall(sql);
                switch (jsonTipo.get("id").getAsInt()) {
                    case 23:
                        ps.setLong(1, NORMAL);
                        break;
                    case 25:
                        ps.setLong(1, COMPUESTO);
                        break;
                    case 26:
                        ps.setLong(1, PRODUCIDO);
                        break;
                    default:
                        ps.setLong(1, jsonTipo.get("id").getAsLong());
                        break;
                }
                ps.setString(2, jsonTipo.get("descripcion").getAsString());
                ps.setString(3, jsonTipo.get("estado").getAsString());
                ps.executeUpdate();
            }

            void actualizarTipoProducto(JsonObject jsonTipo) throws SQLException {
                String sql = "UPDATE PRODUCTOS_TIPOS SET descripcion=?, estado=? WHERE id=?";
                PreparedStatement ps = conexionCore.prepareCall(sql);
                switch (jsonTipo.get("id").getAsInt()) {
                    case 23:
                        ps.setLong(3, NORMAL);
                        break;
                    case 25:
                        ps.setLong(3, COMPUESTO);
                        break;
                    case 26:
                        ps.setLong(3, PRODUCIDO);
                        break;
                    default:
                        ps.setLong(3, jsonTipo.get("id").getAsLong());
                        break;
                }
                ps.setString(1, jsonTipo.get("descripcion").getAsString());
                ps.setString(2, jsonTipo.get("estado").getAsString());
                ps.executeUpdate();
            }

            boolean existeUnidadProducto(JsonObject jsonUnidad) throws SQLException {
                String sql = "SELECT * FROM PRODUCTOS_UNIDADES WHERE ID =?";
                PreparedStatement ps = conexionCore.prepareCall(sql);
                ps.setLong(1, jsonUnidad.get("id").getAsLong());
                return ps.executeQuery().next();
            }

            void insertarUnidadProducto(JsonObject jsonUnidad) throws SQLException {
                String sql = "INSERT INTO PRODUCTOS_UNIDADES (id, descripcion, estado) VALUES(?,?,?);";
                PreparedStatement ps = conexionCore.prepareCall(sql);
                ps.setLong(1, jsonUnidad.get("id").getAsLong());
                ps.setString(2, jsonUnidad.get("descripcion").getAsString());
                ps.setString(3, jsonUnidad.get("estado").getAsString());
                ps.executeUpdate();
            }

            void actualizarUnidadProducto(JsonObject jsonUnidad) throws SQLException {
                String sql = "UPDATE PRODUCTOS_UNIDADES SET descripcion=?, estado=? WHERE id=?;";
                PreparedStatement ps = conexionCore.prepareCall(sql);
                ps.setString(1, jsonUnidad.get("descripcion").getAsString());
                ps.setString(2, jsonUnidad.get("estado").getAsString());
                ps.setLong(3, jsonUnidad.get("id").getAsLong());
                ps.executeUpdate();
            }

            boolean existeProducto(JsonObject jsonProducto) throws SQLException {
                String sql = "SELECT * FROM PRODUCTOS WHERE ID = ?";
                PreparedStatement ps = conexionCore.prepareCall(sql);
                ps.setLong(1, jsonProducto.get("id").getAsLong());
                return ps.executeQuery().next();
            }

            boolean existeTipoProductoRegistry() throws SQLException, DAOException {
                String sql = "SELECT ID FROM productos_tipos WHERE ID = ?";
                PreparedStatement ps = conexionRegistry.prepareCall(sql);
                ps.setLong(1, -1);
                return ps.executeQuery().next();
            }

            void insertarTipoProductoRegistry() throws SQLException, DAOException {
                JsonObject tipo = new JsonObject();
                tipo.addProperty("id", -1);
                tipo.addProperty("descripcion", "COMBUSTIBLE");
                tipo.addProperty("estado", "A");
                String sql = "INSERT INTO productos_tipos( id, descripcion, estado) " + " VALUES (?, ?, ?);";
                PreparedStatement ps = conexionRegistry.prepareStatement(sql);
                ps.setLong(1, tipo.get("id").getAsLong());
                ps.setString(2, tipo.get("descripcion").getAsString());
                ps.setString(3, tipo.get("estado").getAsString());
                ps.executeUpdate();
            }

            void insertarProducto(JsonObject jsonProducto) throws SQLException {
                String sql = "INSERT INTO productos (id, descripcion, estado, productos_tipos_id, empresas_id, "
                        + "familias, publico, precio, precio2, unidad_medida_id, plu ,p_atributos) VALUES(?,?,'A',?,?, "
                        + "?,NULL,?,?,?,?,?::json);";
                PreparedStatement ps = conexionCore.prepareCall(sql);
                ps.setLong(1, jsonProducto.get("id").getAsLong());
                ps.setString(2, jsonProducto.get("descripcion").getAsString());
                switch (jsonProducto.get("tipo_producto_id").getAsInt()) {
                    case 23:
                        ps.setLong(3, NORMAL);
                        break;
                    case 25:
                        ps.setLong(3, COMPUESTO);
                        break;
                    case 26:
                        ps.setLong(3, PRODUCIDO);
                        break;
                    default:
                        ps.setLong(3, jsonProducto.get("tipo_producto_id").getAsLong());
                        break;
                }
                ps.setLong(4, jsonProducto.get("empresas_id").getAsLong());
                ps.setLong(5, jsonProducto.get("familia_id").getAsLong());
                ps.setLong(6, jsonProducto.get("precio").getAsLong());
                ps.setLong(7, jsonProducto.get("precio").getAsLong());
                ps.setLong(8, jsonProducto.get("unidad_medida_id").getAsLong());
                ps.setString(9, jsonProducto.get("plu").getAsString());

                JsonObject item = new JsonObject();
                if (jsonProducto.get("codigoSAP") != null && !jsonProducto.get("codigoSAP").isJsonNull()) {
                    item.addProperty("codigoSAP", jsonProducto.get("codigoSAP").getAsString());
                } else {
                    item.addProperty("codigoSAP", "");
                }

                if (jsonProducto.get("codigoExterno") != null && !jsonProducto.get("codigoExterno").isJsonNull()) {
                    item.addProperty("codigoExterno", jsonProducto.get("codigoExterno").getAsString());
                } else {
                    item.addProperty("codigoExterno", "");
                }

                if (jsonProducto.get("atributos") != null && !jsonProducto.get("atributos").isJsonNull()) {
                    item.add("atributos", jsonProducto.get("atributos"));
                }

                ps.setString(10, item.toString());
                ps.executeUpdate();
            }

            void actualizarProducto(JsonObject jsonProducto) throws SQLException {
                String sql = "UPDATE productos SET descripcion= ?, productos_tipos_id=?, familias=?, precio=?, precio2=?, unidad_medida_id=?, plu=? ,p_atributos=?::json WHERE id=?;";
                PreparedStatement ps = conexionCore.prepareCall(sql);
                ps.setString(1, jsonProducto.get("descripcion").getAsString());
                switch (jsonProducto.get("tipo_producto_id").getAsInt()) {
                    case 23:
                        ps.setLong(2, NORMAL);
                        break;
                    case 25:
                        ps.setLong(2, COMPUESTO);
                        break;
                    case 26:
                        ps.setLong(2, PRODUCIDO);
                        break;
                    default:
                        ps.setLong(2, jsonProducto.get("tipo_producto_id").getAsLong());
                        break;
                }
                ps.setLong(3, jsonProducto.get("familia_id").getAsLong());
                ps.setLong(4, jsonProducto.get("precio").getAsLong());
                ps.setLong(5, jsonProducto.get("precio").getAsLong());
                ps.setLong(6, jsonProducto.get("unidad_medida_id").getAsLong());
                ps.setString(7, jsonProducto.get("plu").getAsString());

                JsonObject item = new JsonObject();
                if (jsonProducto.get("codigoSAP") != null && !jsonProducto.get("codigoSAP").isJsonNull()) {
                    item.addProperty("codigoSAP", jsonProducto.get("codigoSAP").getAsString());
                } else {
                    item.addProperty("codigoSAP", "");
                }

                if (jsonProducto.get("codigoExterno") != null && !jsonProducto.get("codigoExterno").isJsonNull()) {
                    item.addProperty("codigoExterno", jsonProducto.get("codigoExterno").getAsString());
                } else {
                    item.addProperty("codigoExterno", "");
                }

                ps.setString(8, item.toString());
                ps.setLong(9, jsonProducto.get("id").getAsLong());
                ps.executeUpdate();
                
                //  NUEVO: Invalidar cache después de actualizar PRECIO/DESCRIPCIÓN (Core)
                if (productoUpdateInterceptor != null && jsonProducto != null) {
                    Long productoId = jsonProducto.get("id").getAsLong();
                    Long nuevoPrecio = jsonProducto.get("precio").getAsLong();
                    System.out.println(" Invalidando cache por cambio de PRECIO CORE - Producto ID: " + productoId + ", Nuevo precio: " + nuevoPrecio);
                    productoUpdateInterceptor.onProductoActualizado(productoId);
                }
            }

            boolean existeProductoRegistryVentaCanastilla(JsonObject jsonProducto) throws SQLException {
                boolean existeProductoRegistryVentaCanastilla = false;
                String sql = "SELECT ID FROM productos WHERE ID = ?";
                PreparedStatement ps = conexionRegistry.prepareStatement(sql);
                ps.setLong(1, jsonProducto.get("id").getAsLong());
                ResultSet re = ps.executeQuery();
                while (re.next()) {
                    existeProductoRegistryVentaCanastilla = true;
                }
                return existeProductoRegistryVentaCanastilla;
            }

            void insertarProductoRegistryVentaCanastilla(JsonObject jsonProducto) throws SQLException {
                String sql = "INSERT INTO productos ( "
                        + "        id, empresas_id, plu, descripcion, precio, puede_vender, puede_comprar, "
                        + "          ingrediente, tipo, url_foto, unidades_medida, unidades_contenido, "
                        + "            usa_balanza, estado, create_user, create_date, update_user, update_date, "
                        + "            favorito, seguimiento, promocion, cantidad_ingredientes, cantidad_impuestos) "
                        + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                        + "    ?, ?, 1, now(), null, null, ?, ?, ?, ?, ?);";
                PreparedStatement ps = conexionRegistry.prepareStatement(sql);
                ps.setLong(1, jsonProducto.get("id").getAsLong());
                ps.setLong(2, Main.credencial.getEmpresas_id());
                ps.setString(3, jsonProducto.get("plu").getAsString());
                ps.setString(4, jsonProducto.get("descripcion").getAsString());
                ps.setFloat(5, jsonProducto.get("precio").getAsLong());
                ps.setString(6, "S");
                ps.setString(7, "N");
                ps.setString(8, "N");
                ps.setLong(9, NovusConstante.PRODUCTOS_TIPOS_COMBUSTIBLE);
                ps.setNull(10, Types.NULL);
                ps.setNull(11, Types.NULL);
                ps.setNull(12, Types.NULL);
                ps.setString(13, "N");
                ps.setString(14, "A");
                ps.setNull(15, Types.NULL);
                ps.setNull(16, Types.NULL);
                ps.setString(17, "N");
                ps.setInt(18, 0);
                ps.setInt(19, 0);
                ps.executeUpdate();
            }

            void actualizarProductoRegistryVentaCanastilla(JsonObject jsonProducto) throws SQLException {
                String sqlup = "UPDATE productos "
                        + "     SET empresas_id=?, plu=?, descripcion=?, precio=?, puede_vender=?, "
                        + "       puede_comprar=?, ingrediente=?, tipo=?, url_foto=?, unidades_medida=?, "
                        + "       unidades_contenido=?, usa_balanza=?, estado=?, favorito=?, seguimiento=?, promocion=?,"
                        + "       cantidad_ingredientes=?, cantidad_impuestos=? WHERE id=?";
                PreparedStatement ps = conexionRegistry.prepareStatement(sqlup);
                ps.setLong(1, Main.credencial.getEmpresas_id());
                ps.setString(2, jsonProducto.get("plu").getAsString());
                ps.setString(3, jsonProducto.get("descripcion").getAsString());
                ps.setFloat(4, jsonProducto.get("precio").getAsLong());
                ps.setString(5, "S");
                ps.setString(6, "N");
                ps.setString(7, "N");
                ps.setLong(8, NovusConstante.PRODUCTOS_TIPOS_COMBUSTIBLE);
                ps.setNull(9, Types.NULL);
                ps.setNull(10, Types.NULL);
                ps.setNull(11, Types.NULL);
                ps.setString(12, "N");
                ps.setString(13, "A");
                ps.setNull(14, Types.NULL);
                ps.setNull(15, Types.NULL);
                ps.setNull(16, Types.NULL);
                ps.setInt(17, 0);
                ps.setInt(18, 0);
                ps.setLong(19, jsonProducto.get("id").getAsLong());
                ps.executeUpdate();
            }
        }
        Inner innerCall = new Inner();
        try {
            for (JsonElement familia : familias) {
                JsonObject jsonFamilia = familia.getAsJsonObject();
                boolean existeFamiliaProducto = innerCall.existeFamiliaProducto(jsonFamilia);
                if (!existeFamiliaProducto) {
                    innerCall.insertarFamiliaProducto(jsonFamilia);
                } else {
                    innerCall.actualizarFamiliaProducto(jsonFamilia);
                }
            }
            for (JsonElement tipo : tipos) {
                JsonObject jsonTipo = tipo.getAsJsonObject();
                boolean existeTipoProducto = innerCall.existeTipoProducto(jsonTipo);
                if (!existeTipoProducto) {
                    innerCall.insertarTipoProducto(jsonTipo);
                } else {
                    innerCall.actualizarTipoProducto(jsonTipo);
                }
            }
            if (!innerCall.existeTipoProductoRegistry()) {
                innerCall.insertarTipoProductoRegistry();
            }
            for (JsonElement unidad : unidades) {
                JsonObject jsonUnidad = unidad.getAsJsonObject();
                boolean existeUnidadProducto = innerCall.existeUnidadProducto(jsonUnidad);
                if (!existeUnidadProducto) {
                    innerCall.insertarUnidadProducto(jsonUnidad);
                } else {
                    innerCall.actualizarUnidadProducto(jsonUnidad);
                }
            }
            for (JsonElement producto : productos) {
                JsonObject jsonProducto = producto.getAsJsonObject();
                boolean existeProducto = innerCall.existeProducto(jsonProducto);
                if (!existeProducto) {
                    innerCall.insertarProducto(jsonProducto);
                } else {
                    innerCall.actualizarProducto(jsonProducto);
                }
                boolean existeProductoRegistryVentaCanastilla = innerCall
                        .existeProductoRegistryVentaCanastilla(jsonProducto);
                if (!existeProductoRegistryVentaCanastilla) {
                    innerCall.insertarProductoRegistryVentaCanastilla(jsonProducto);
                } else {
                    innerCall.actualizarProductoRegistryVentaCanastilla(jsonProducto);
                }
            }
        } catch (SQLException e) {
            insertado = false;
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, e);
            NovusUtils.printLn(e.getMessage());
        } catch (Exception e) {
            insertado = false;
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, e);
            NovusUtils.printLn(e.getMessage());
        }
        return insertado;
    }

    public boolean crearProductosCombustibleTodos(JsonArray productos) throws DAOException {
        final int DB_FAMILIA_CORRIENTE = 27;
        final int DB_FAMILIA_EXTRA = 28;
        final int DB_FAMILIA_DIESEL = 29;
        final int DB_FAMILIA_GAS = 30;
        final int DB_FAMILIA_GLP = 31;
        final int DB_FAMILIA_ADBLUE = 33;

        final int LC_FAMILIA_CORRIENTE = 1;
        final int LC_FAMILIA_EXTRA = 3;
        final int LC_FAMILIA_DIESEL = 5;
        final int LC_FAMILIA_GAS = 6;
        final int LC_FAMILIA_GLP = 7;
        final int LC_FAMILIA_ADBLUE = 8;

        class Inner {

            Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");

            boolean existeProducto(JsonObject jsonProducto) throws SQLException {
                boolean existeProducto = false;
                String sql = "SELECT * FROM PRODUCTOS WHERE ID = ?";
                PreparedStatement ps = conexion.prepareCall(sql);
                ps.setLong(1, jsonProducto.get("id").getAsLong());
                ResultSet re = ps.executeQuery();
                while (re.next()) {
                    existeProducto = true;
                }
                return existeProducto;
            }

            void insertarProducto(JsonObject jsonProducto) throws SQLException {
                /*
                    27	CORRIENTE
                    28	EXTRA
                    29	DIESEL
                    30	GAS
                    31	GLP
                    33	ADBLUE

                    1	CORRIENTE
                    3	EXTRA
                    5	DIESEL
                    6	GAS
                    7	GLP
                    8	ADBLUE
                 */
                int familia = 0;
                switch (jsonProducto.get("tipo_producto_id").getAsInt()) {
                    case DB_FAMILIA_CORRIENTE:
                        familia = LC_FAMILIA_CORRIENTE;
                        break;
                    case DB_FAMILIA_EXTRA:
                        familia = LC_FAMILIA_EXTRA;
                        break;
                    case DB_FAMILIA_DIESEL:
                        familia = LC_FAMILIA_DIESEL;
                        break;
                    case DB_FAMILIA_GAS:
                        familia = LC_FAMILIA_GAS;
                        break;
                    case DB_FAMILIA_GLP:
                        familia = LC_FAMILIA_GLP;
                        break;
                    case DB_FAMILIA_ADBLUE:
                        familia = LC_FAMILIA_ADBLUE;
                        break;
                    default:
                        System.err.println("FAMILIA NO COMTEMPLADA VERIFIQUE EL PRODUCTO ID:" + jsonProducto.get("id").getAsLong());
                        throw new AssertionError();
                }

                String sql = "INSERT INTO productos (id, descripcion, estado, productos_tipos_id, empresas_id, "
                        + "familias, publico, precio, precio2, unidad_medida_id, plu ,p_atributos) VALUES(?,?,'A',?,?, "
                        + "?,NULL,?,?,?,?,?::json);";
                PreparedStatement ps = conexion.prepareCall(sql);
                ps.setLong(1, jsonProducto.get("id").getAsLong());
                ps.setString(2, jsonProducto.get("descripcion").getAsString());
                ps.setLong(3, jsonProducto.get("tipo_producto_id").getAsLong());
                ps.setLong(4, jsonProducto.get("empresas_id").getAsLong());
                ps.setLong(5, familia);
                ps.setLong(6, jsonProducto.get("precio").getAsLong());
                ps.setLong(7, jsonProducto.get("precio").getAsLong());
                ps.setLong(8, jsonProducto.get("atributos").getAsJsonObject().get("unidad_medida").getAsJsonObject().get("value").getAsLong());
                ps.setString(9, jsonProducto.get("plu").getAsString());

                JsonObject item = new JsonObject();
                if (jsonProducto.get("codigoSAP") != null && !jsonProducto.get("codigoSAP").isJsonNull()) {
                    item.addProperty("codigoSAP", jsonProducto.get("codigoSAP").getAsString());
                } else {
                    item.addProperty("codigoSAP", "");
                }

                if (jsonProducto.get("codigoExterno") != null && !jsonProducto.get("codigoExterno").isJsonNull()) {
                    item.addProperty("codigoExterno", jsonProducto.get("codigoExterno").getAsString());
                } else {
                    item.addProperty("codigoExterno", "");
                }

                if (jsonProducto.get("atributos") != null && !jsonProducto.get("atributos").isJsonNull()) {
                    item.add("atributos", jsonProducto.get("atributos"));
                }

                ps.setString(10, item.toString());
                ps.executeUpdate();
            }

            void actualizarProducto(JsonObject jsonProducto) throws SQLException {
                String sql = "UPDATE productos SET descripcion= ?, productos_tipos_id=?, familias=?, precio=?, precio2=?, unidad_medida_id=?, plu=? ,p_atributos=?::json WHERE id=?;";
                PreparedStatement ps = conexion.prepareCall(sql);
                ps.setString(1, jsonProducto.get("descripcion").getAsString());
                ps.setLong(2, jsonProducto.get("tipo_producto_id").getAsLong());
                ps.setLong(3, jsonProducto.get("familia_id").getAsLong());
                ps.setLong(4, jsonProducto.get("precio").getAsLong());
                ps.setLong(5, jsonProducto.get("precio").getAsLong());
                ps.setLong(6, jsonProducto.get("unidad_medida_id").getAsLong());
                ps.setString(7, jsonProducto.get("plu").getAsString());

                JsonObject item = new JsonObject();
                if (jsonProducto.get("codigoSAP") != null && !jsonProducto.get("codigoSAP").isJsonNull()) {
                    item.addProperty("codigoSAP", jsonProducto.get("codigoSAP").getAsString());
                } else {
                    item.addProperty("codigoSAP", "");
                }

                if (jsonProducto.get("codigoExterno") != null && !jsonProducto.get("codigoExterno").isJsonNull()) {
                    item.addProperty("codigoExterno", jsonProducto.get("codigoExterno").getAsString());
                } else {
                    item.addProperty("codigoExterno", "");
                }

                ps.setString(8, item.toString());
                ps.setLong(9, jsonProducto.get("id").getAsLong());
                ps.executeUpdate();
            }

        }
        Inner innerCall = new Inner();
        boolean resultado = false;
        try {
            for (JsonElement producto : productos) {
                JsonObject jsonProducto = producto.getAsJsonObject();
                boolean existeProducto = innerCall.existeProducto(jsonProducto);
                if (!existeProducto) {
                    innerCall.insertarProducto(jsonProducto);
                } else {
                    innerCall.actualizarProducto(jsonProducto);
                }
            }
            resultado = true;
        } catch (SQLException e) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, e);
            NovusUtils.printLn(e.getMessage());
        } catch (Exception e) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, e);
            NovusUtils.printLn(e.getMessage());
        }
        return resultado;
    }

    public void inactivarSurtidores() throws SQLException, DAOException {
        DatabaseConnectionManager.DatabaseResources resources = null;
        try {
            String sql = "UPDATE surtidores  "
                    + " SET estado='I'";
            resources = DatabaseConnectionManager.executeCompleteQuery("lazoexpresscore", sql);
        } catch (PSQLException ex) {
            NovusUtils.printLn(ex.getMessage());
        } catch (SQLException ex) {
            NovusUtils.printLn(ex.getMessage());
        } catch (Exception ex) {
            NovusUtils.printLn(ex.getMessage());
        } finally {
            DatabaseConnectionManager.closeDatabaseResources(resources);
        }
    }

    public void inactivarDetalles() throws SQLException, DAOException {
        DatabaseConnectionManager.DatabaseResources resources = null;
        try {
            String sql = "UPDATE surtidores_detalles  "
                    + " SET estado=0";
            resources = DatabaseConnectionManager.executeCompleteQuery("lazoexpresscore", sql);
        } catch (PSQLException ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            NovusUtils.printLn(ex.getMessage());
        } catch (SQLException ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            NovusUtils.printLn(ex.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            NovusUtils.printLn(ex.getMessage());
        } finally {
            DatabaseConnectionManager.closeDatabaseResources(resources);
        }
    }

    public void eliminarDetallesInactivos() throws SQLException, DAOException {
        DatabaseConnectionManager.DatabaseResources resources = null;
        try {
            String sql = "DELETE FROM surtidores_detalles  WHERE estado=0";
            resources = DatabaseConnectionManager.executeCompleteQuery("lazoexpresscore", sql);
        } catch (PSQLException ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            NovusUtils.printLn(ex.getMessage());
        } catch (SQLException ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            NovusUtils.printLn(ex.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            NovusUtils.printLn(ex.getMessage());
        } finally {
            DatabaseConnectionManager.closeDatabaseResources(resources);
        }
    }

    public void eliminarDetallesById(long id) throws SQLException, DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try {
            String sql = "DELETE FROM surtidores_detalles  WHERE id=?";
            PreparedStatement ps = conexion.prepareCall(sql);
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (PSQLException ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            NovusUtils.printLn(ex.getMessage());
        } catch (SQLException ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            NovusUtils.printLn(ex.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            NovusUtils.printLn(ex.getMessage());
        }
    }

    public void eliminarSurtidoresInactivos() throws SQLException, DAOException {
        Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");
        try {
            String sql = "SELECT ID FROM surtidores WHERE estado='I'";
            PreparedStatement ps = conexion.prepareCall(sql);
            ResultSet res = ps.executeQuery();
            while (res.next()) {
                eliminarDetallesById(res.getLong(1));
            }
            sql = "DELETE FROM surtidores  WHERE estado='I'";
            ps = conexion.prepareCall(sql);
            ps.executeUpdate();
        } catch (PSQLException ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            NovusUtils.printLn(ex.getMessage());
        } catch (SQLException ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            NovusUtils.printLn(ex.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            NovusUtils.printLn(ex.getMessage());
        }
    }

    public boolean createSurtidor(JsonObject surtidor, int index) throws DAOException {
        class Inner {

            Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");

            boolean existeSurtidor() throws SQLException {
                boolean existeSurtidor = false;
                String sql = "SELECT ID FROM surtidores WHERE ID = ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, surtidor.get("id").getAsLong());
                ResultSet re = ps.executeQuery();
                while (re.next()) {
                    existeSurtidor = true;
                }
                return existeSurtidor;
            }

            void insertarSurtidor() throws SQLException {
                String sql = "INSERT INTO surtidores (id, surtidor, islas_id, estado, surtidores_tipos_id, "
                        + "surtidores_protocolos_id, mac, ip, port, factor_volumen_parcial, "
                        + "factor_importe_parcial, factor_precio, lector_ip, lector_port, impresora_ip, "
                        + "impresora_port, factor_inventario, empresas_id, controlador, debug_estado, "
                        + "debug_tramas, tiene_echo, create_user, create_date, token)  VALUES (?,?,?,?,?,"
                        + "?,?,?,?,?," + "?,?,?,?,?," + "?,?,?,?,?," + "?,?,1,now(),'');";
                PreparedStatement ps = conexion.prepareCall(sql);
                ps.setLong(1, surtidor.get("id").getAsLong());
                ps.setInt(2, surtidor.get("surtidor").getAsInt());
                ps.setInt(3, surtidor.get("islas_id").getAsInt());
                ps.setString(4, surtidor.get("estado").getAsString());
                ps.setInt(5, surtidor.get("codigo_tipo").getAsInt());
                ps.setInt(6, surtidor.get("codigo").getAsInt());
                ps.setString(7, surtidor.get("mac").getAsString());
                ps.setString(8, surtidor.get("ip").getAsString());
                ps.setInt(9, surtidor.get("port").getAsInt());
                ps.setInt(10, surtidor.get("factor_volumen_parcial").getAsInt());
                ps.setInt(11, surtidor.get("factor_importe_parcial").getAsInt());
                ps.setInt(12, surtidor.get("factor_precio").getAsInt());
                ps.setString(13, surtidor.get("lector_ip").getAsString());
                ps.setInt(14, surtidor.get("lector_port").getAsInt());
                ps.setString(15, surtidor.get("impresora_ip").getAsString());
                ps.setInt(16, surtidor.get("impresora_port").getAsInt());
                ps.setInt(17, surtidor.get("factor_inventario").getAsInt());
                ps.setLong(18, surtidor.get("empresas_id").getAsLong());
                ps.setInt(19, surtidor.get("controlador").getAsInt());

                if (surtidor.get("debug_estado") != null) {
                    ps.setString(20, surtidor.get("debug_estado").getAsString());
                } else {
                    ps.setString(20, "N");
                }
                if (surtidor.get("debug_tramas") != null) {
                    ps.setString(21, surtidor.get("debug_tramas").getAsString());
                } else {
                    ps.setString(21, "N");
                }
                if (surtidor.get("tiene_echo") != null && !surtidor.get("tiene_echo").isJsonNull()) {
                    ps.setString(22, surtidor.get("tiene_echo").getAsString());
                } else {
                    ps.setString(22, "S");
                }
                ps.executeUpdate();
            }

            void actualizarSurtidor() throws SQLException {
                String sql = "UPDATE public.surtidores " + " SET surtidor=?, islas_id=?, "
                        + "estado=?, surtidores_tipos_id=?," + " surtidores_protocolos_id=?, ip=?, "
                        + "port=?, factor_volumen_parcial=?, " + "factor_importe_parcial=?, factor_precio=?,"
                        + " lector_ip=?, lector_port=?, impresora_ip=?, "
                        + "impresora_port=?, factor_inventario=?, controlador=?,"
                        + " debug_estado=?, debug_tramas=?, tiene_echo=? " + " WHERE id=?;";
                PreparedStatement ps = conexion.prepareCall(sql);
                ps.setInt(1, surtidor.get("surtidor").getAsInt());
                ps.setInt(2, surtidor.get("islas_id").getAsInt());
                ps.setString(3, surtidor.get("estado").getAsString());
                ps.setInt(4, surtidor.get("codigo_tipo").getAsInt());
                ps.setInt(5, surtidor.get("codigo").getAsInt());
                ps.setString(6, surtidor.get("ip").getAsString());
                ps.setInt(7, surtidor.get("port").getAsInt());
                ps.setInt(8, surtidor.get("factor_volumen_parcial").getAsInt());
                ps.setInt(9, surtidor.get("factor_importe_parcial").getAsInt());
                ps.setInt(10, surtidor.get("factor_precio").getAsInt());
                ps.setString(11, surtidor.get("lector_ip").getAsString());
                ps.setInt(12, surtidor.get("lector_port").getAsInt());
                ps.setString(13, surtidor.get("impresora_ip").getAsString());
                ps.setInt(14, surtidor.get("impresora_port").getAsInt());
                ps.setInt(15, surtidor.get("factor_inventario").getAsInt());
                ps.setInt(16, surtidor.get("controlador").getAsInt());

                if (surtidor.get("debug_estado") != null) {
                    ps.setString(17, surtidor.get("debug_estado").getAsString());
                } else {
                    ps.setString(17, "N");
                }
                if (surtidor.get("debug_tramas") != null) {
                    ps.setString(18, surtidor.get("debug_tramas").getAsString());
                } else {
                    ps.setString(18, "N");
                }
                if (surtidor.get("tiene_echo") != null && !surtidor.get("tiene_echo").isJsonNull()) {
                    ps.setString(19, surtidor.get("tiene_echo").getAsString());
                } else {
                    ps.setString(19, "S");
                }
                ps.setLong(20, surtidor.get("id").getAsLong());
                ps.executeUpdate();
            }
        }
        boolean result = true;
        Inner innerCall = new Inner();
        try {
            if (!innerCall.existeSurtidor()) {
                innerCall.insertarSurtidor();
            } else {
                innerCall.actualizarSurtidor();
            }
        } catch (SQLException s) {
            result = false;
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, s);

            NovusUtils.printLn(s.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);

            result = false;
            NovusUtils.printLn(ex.getMessage());
        }
        return result;
    }

    public boolean createProtocolo(JsonObject protocolo) throws DAOException, SQLException {
        boolean result = true;
        class Inner {

            Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");

            boolean existeProtocolo() throws SQLException {
                String sql = "SELECT ID FROM  surtidores_protocolos WHERE ID=?";
                PreparedStatement ps = conexion.prepareCall(sql);
                ps.setLong(1, protocolo.get("codigo").getAsInt());
                return ps.executeQuery().next();
            }

            void insertarProtocolo() throws SQLException {
                String sql = "INSERT INTO surtidores_protocolos " + "(id, descripcion, estado,codigo)"
                        + "VALUES (?, ?, ?, ?);";
                PreparedStatement ps = conexion.prepareCall(sql);
                ps.setInt(1, protocolo.get("codigo").getAsInt());
                ps.setString(2, protocolo.get("nombre").getAsString());
                ps.setString(3, protocolo.get("estado").getAsString());
                ps.setInt(4, protocolo.get("id").getAsInt());
                ps.executeUpdate();
            }

            void actualizarProtocolo() throws SQLException {
                String sql = "UPDATE public.surtidores_protocolos" + "  SET descripcion=?, estado=?, codigo=?"
                        + " WHERE id=?;";
                PreparedStatement ps = conexion.prepareCall(sql);
                ps.setString(1, protocolo.get("nombre").getAsString());
                ps.setString(2, protocolo.get("estado").getAsString());
                ps.setInt(3, protocolo.get("id").getAsInt());
                ps.setInt(4, protocolo.get("codigo").getAsInt());
                ps.executeUpdate();
            }
        }

        Inner innerCall = new Inner();
        try {
            if (!innerCall.existeProtocolo()) {
                innerCall.insertarProtocolo();
            } else {
                innerCall.actualizarProtocolo();
            }
        } catch (PSQLException ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            NovusUtils.printLn(ex.getMessage());
            result = false;
        } catch (SQLException ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            NovusUtils.printLn(ex.getMessage());
            result = false;

        } catch (Exception ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            NovusUtils.printLn(ex.getMessage());
            result = false;
        }
        return result;

    }

    public boolean createTiposSurtidor(JsonObject tipo) throws DAOException, SQLException {
        boolean result = true;
        class Inner {

            Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");

            boolean existeTipoSurtidor() throws SQLException {
                String sql = "SELECT ID FROM  surtidores_tipos WHERE ID=?";
                PreparedStatement ps = conexion.prepareCall(sql);
                ps.setLong(1, tipo.get("codigo").getAsInt());
                return ps.executeQuery().next();
            }

            void insertarTipoSurtidor() throws SQLException {
                String sql = "INSERT INTO public.surtidores_tipos (id, descripcion, estado)\n" + " VALUES (?, ?, ?);";
                PreparedStatement ps = conexion.prepareCall(sql);
                ps.setInt(1, tipo.get("codigo").getAsInt());
                ps.setString(2, tipo.get("nombre").getAsString());
                ps.setString(3, tipo.get("estado").getAsString());

                ps.executeUpdate();
            }

            void actualizarTipoSurtidor() throws SQLException {
                String sql = "UPDATE public.surtidores_tipos" + " SET descripcion=?, estado=?" + " WHERE id=? ";
                PreparedStatement ps = conexion.prepareCall(sql);
                ps.setString(1, tipo.get("nombre").getAsString());
                ps.setString(2, tipo.get("estado").getAsString());
                ps.setInt(3, tipo.get("codigo").getAsInt());
                ps.executeUpdate();
            }
        }
        Inner innerCall = new Inner();
        try {
            if (!innerCall.existeTipoSurtidor()) {
                innerCall.insertarTipoSurtidor();
            } else {
                innerCall.actualizarTipoSurtidor();
            }
        } catch (PSQLException ex) {
            NovusUtils.printLn(ex.getMessage());
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);

            result = false;
        } catch (SQLException ex) {
            NovusUtils.printLn(ex.getMessage());
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);

            result = false;

        } catch (Exception ex) {
            NovusUtils.printLn(ex.getMessage());
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);

            result = false;
        }
        return result;
    }

    public boolean createDetallesSurtidor(JsonObject detalles) throws DAOException, SQLException {
        class Inner {

            Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");

            boolean existeDetalle() throws SQLException {
                boolean existeDetalle = false;
                String sql = "SELECT ID FROM  surtidores_detalles WHERE ID=?";
                PreparedStatement ps = conexion.prepareCall(sql);
                ps.setLong(1, detalles.get("id").getAsLong());
                ResultSet re = ps.executeQuery();
                while (re.next()) {
                    existeDetalle = true;
                }
                return existeDetalle;
            }

            void insertarDetalles() throws SQLException {
                String sql = "INSERT INTO surtidores_detalles (id, surtidores_id, surtidor, cara, manguera,"
                        + "grado, productos_id, acumulado_venta, acumulado_cantidad, ultima_conexion, "
                        + "lector_puerto, lector_rfid, salto_lectura, acumulado_venta_surt, acumulado_cantidad_surt, "
                        + "estado, estado_publico, bodegas_id,lector_ip,motivo_bloqueo,bloqueo,puerto)  VALUES ("
                        + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'','','',?);";
                PreparedStatement ps = conexion.prepareCall(sql);
                ps.setLong(1, detalles.get("id").getAsLong());
                ps.setInt(2, detalles.get("surtidores_id").getAsInt());
                ps.setInt(3, detalles.get("surtidor").getAsInt());
                ps.setInt(4, detalles.get("cara").getAsInt());
                ps.setInt(5, detalles.get("manguera").getAsInt());
                ps.setInt(6, detalles.get("grado").getAsInt());
                ps.setLong(7, detalles.get("productos_id").getAsLong());
                if (detalles.get("acumulado_venta").isJsonNull()) {
                    ps.setNull(8, Types.NULL);
                    ps.setNull(9, Types.NULL);
                } else {
                    ps.setLong(8, detalles.get("acumulado_venta").getAsLong());
                    ps.setLong(9, detalles.get("acumulado_cantidad").getAsLong());
                }
                ps.setNull(10, Types.NULL);
                ps.setInt(11, detalles.get("lector_puerto").getAsInt());
                Main.parametrosCore.put(NovusConstante.PREFERENCE_IBUTTON_PORT, "0");
                EquipoDao.setParametro(NovusConstante.PREFERENCE_IBUTTON_PORT, "0", true);
                EquipoDao.setParametro(NovusConstante.PARAMETRO_RFID_V2, "0", true);
                if (detalles.get("lector_rfid") != null && !detalles.get("lector_rfid").isJsonNull()) {
                    ps.setString(12, detalles.get("lector_rfid").getAsString());
                    Main.parametrosCore.put(NovusConstante.PREFERENCE_LECTOR_RFID, "0");
                    EquipoDao.setParametro(NovusConstante.PREFERENCE_LECTOR_RFID, "0", true);
                    EquipoDao.setParametro(NovusConstante.PARAMETRO_RFID_V2, "0", true);
                } else {
                    ps.setNull(12, Types.NULL);
                }
                if (detalles.get("salto_lectura") == null || detalles.get("salto_lectura").isJsonNull()) {
                    ps.setNull(13, Types.NULL);
                    ps.setNull(14, Types.NULL);
                    ps.setNull(15, Types.NULL);
                } else {
                    ps.setString(13, detalles.get("salto_lectura").getAsString());
                    ps.setLong(14, detalles.get("acumulado_venta_surt").getAsLong());
                    ps.setLong(15, detalles.get("acumulado_cantidad_surt").getAsLong());
                }
                ps.setInt(16, 1);
                ps.setNull(17, Types.NULL);
                ps.setLong(18, detalles.get("bodegas_id").getAsLong());
                ps.setString(19, detalles.get("cara_surtidor") == null || detalles.get("cara_surtidor").isJsonNull() ? "0" : detalles.get("cara_surtidor").getAsString());
                ps.executeUpdate();
            }

            void actualizarDetalles() throws SQLException {
                String sql = "UPDATE surtidores_detalles  "
                        + " SET surtidores_id=?, surtidor=?, cara=?, manguera=?, grado=?, productos_id=?, "
                        + " lector_puerto=?, bodegas_id=?, lector_rfid=?, puerto=? ,estado=1 WHERE id=?;";
                PreparedStatement ps = conexion.prepareCall(sql);
                ps.setInt(1, detalles.get("surtidores_id").getAsInt());
                ps.setInt(2, detalles.get("surtidor").getAsInt());
                ps.setInt(3, detalles.get("cara").getAsInt());
                ps.setInt(4, detalles.get("manguera").getAsInt());
                ps.setInt(5, detalles.get("grado").getAsInt());
                ps.setLong(6, detalles.get("productos_id").getAsLong());
                ps.setInt(7, detalles.get("lector_puerto").getAsInt());
                Main.parametrosCore.put(NovusConstante.PREFERENCE_IBUTTON_PORT, "0");
                EquipoDao.setParametro(NovusConstante.PREFERENCE_IBUTTON_PORT, "0", true);
                EquipoDao.setParametro(NovusConstante.PARAMETRO_RFID_V2, "0", true);
                Main.parametrosCore.put(NovusConstante.PREFERENCE_LECTOR_RFID, "0");
                EquipoDao.setParametro(NovusConstante.PREFERENCE_LECTOR_RFID, "0", true);
                EquipoDao.setParametro(NovusConstante.PARAMETRO_RFID_V2, "0", true);
                ps.setLong(8, detalles.get("bodegas_id").getAsLong());
                ps.setString(9, detalles.get("lector_rfid").isJsonNull() ? "" : detalles.get("lector_rfid").getAsString());
                ps.setString(10, detalles.get("cara_surtidor") == null || detalles.get("cara_surtidor").isJsonNull() ? "" : detalles.get("cara_surtidor").getAsString());
                ps.setLong(11, detalles.get("id").getAsLong());
                ps.executeUpdate();
            }

        }
        boolean result = true;
        Inner innerCall = new Inner();
        try {
            if (!innerCall.existeDetalle()) {
                innerCall.insertarDetalles();
            } else {
                innerCall.actualizarDetalles();
            }

        } catch (PSQLException ex) {
            result = false;
            NovusUtils.printLn(ex.getMessage());
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);

        } catch (SQLException ex) {
            result = false;
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);

            NovusUtils.printLn(ex.getMessage());
        } catch (Exception ex) {
            result = false;
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);

            NovusUtils.printLn(ex.getMessage());
        }
        return result;
    }

    public boolean procesarMediosPago(MediosPagosBean medio, CredencialBean cr) throws DAOException {
        class Inner {

            Connection conexionRegistry = Main.obtenerConexionAsync("lazoexpressregistry");

            boolean existeMedioPago(Postgrest db) throws SQLException {
                String sql = "SELECT ID FROM medios_pagos WHERE ID = ?";
                PreparedStatement ps = conexionRegistry.prepareStatement(sql);
                ps.setLong(1, medio.getId());
                return ps.executeQuery().next();
            }

            void insertarMedioPagoRegistry() throws SQLException {
                String sql = "INSERT INTO medios_pagos ( "
                        + " id, descripcion, empresas_id, credito, estado, cambio, minimo_valor, \n"
                        + "  maximo_cambio, comprobante, atributos, codigo_adquiriente) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?::json, ?);";
                PreparedStatement ps = conexionRegistry.prepareStatement(sql);
                ps.setLong(1, medio.getId());
                ps.setString(2, medio.getDescripcion() != null ? medio.getDescripcion() : "");
                ps.setLong(3, cr.getEmpresas_id());
                ps.setString(4, medio.isCredito() ? "S" : "N");
                ps.setString(5, medio.getEstado());
                ps.setString(6, medio.isCambio() ? "S" : "N");
                ps.setFloat(7, medio.getMinimo_valor());
                ps.setFloat(8, medio.getMaximo_valor());
                ps.setString(9, medio.isComprobante() ? "S" : "N");
                ps.setString(10, medio.getAtributos().toString());
                ps.setString(11, medio.getCodigoAdquiriente());
                ps.executeUpdate();
            }

            void actualizarMedioPagoRegistry() throws SQLException {
                String sql = "UPDATE medios_pagos "
                        + "   SET descripcion=?, empresas_id=?, credito=?, estado=?, cambio=?, "
                        + "    minimo_valor=?, maximo_cambio=?, comprobante=?, atributos=?::json, codigo_adquiriente=?  WHERE id=? ";
                PreparedStatement ps = conexionRegistry.prepareStatement(sql);

                ps.setString(1, medio.getDescripcion() != null ? medio.getDescripcion() : "");
                ps.setLong(2, cr.getEmpresas_id());
                ps.setString(3, medio.isCredito() ? "S" : "N");
                ps.setString(4, medio.getEstado());
                ps.setString(5, medio.isCambio() ? "S" : "N");
                ps.setFloat(6, medio.getMinimo_valor());
                ps.setFloat(7, medio.getMaximo_valor());
                ps.setString(8, medio.isComprobante() ? "S" : "N");
                ps.setString(9, medio.getAtributos().toString());
                ps.setString(10, medio.getCodigoAdquiriente());
                ps.setLong(11, medio.getId());
                ps.executeUpdate();
            }

            Connection conexionCore = Main.obtenerConexionAsync("lazoexpresscore");

            void insertarMedioPagoCore() throws SQLException {
                String sql = "INSERT INTO medios_pagos ( id, descripcion, estado,mp_atributos, codigo_adquiriente) " + " VALUES (?, ?, ? ,?::json, ?);";
                PreparedStatement ps = conexionCore.prepareStatement(sql);

                ps.setLong(1, medio.getId());
                ps.setString(2, medio.getDescripcion() != null ? medio.getDescripcion() : "");
                ps.setString(3, medio.getEstado());
                if (medio.getAtributos() == null) {
                    ps.setNull(4, Types.NULL);
                } else {
                    ps.setString(4, medio.getAtributos().toString());
                }
                ps.setString(5, medio.getCodigoAdquiriente());
                ps.executeUpdate();
            }

            void actualizarMedioPagoCore() throws SQLException {
                String sql = "UPDATE medios_pagos  SET descripcion=?, estado=? ,mp_atributos=?::json, codigo_adquiriente=? WHERE id=? ";
                PreparedStatement ps = conexionCore.prepareStatement(sql);
                ps.setString(1, medio.getDescripcion() != null ? medio.getDescripcion() : "");
                ps.setString(2, medio.getEstado());

                if (medio.getAtributos() == null) {
                    ps.setNull(3, Types.NULL);
                } else {
                    ps.setString(3, medio.getAtributos().toString());
                }
                ps.setString(4, medio.getCodigoAdquiriente());
                ps.setLong(5, medio.getId());
                ps.executeUpdate();
            }

        }
        Inner innerCall = new Inner();
        boolean insertado = true;
        try {
            boolean existeMedioPago = innerCall.existeMedioPago(Main.dbRegistry);
            if (!existeMedioPago) {
                innerCall.insertarMedioPagoRegistry();
            } else {
                innerCall.actualizarMedioPagoRegistry();
            }
        } catch (SQLException s) {
            NovusUtils.printLn(s.getMessage());
            insertado = false;
        }

        try {
            boolean existeMedioPago = innerCall.existeMedioPago(Main.dbCore);
            if (!existeMedioPago) {
                innerCall.insertarMedioPagoCore();
            } else {
                innerCall.actualizarMedioPagoCore();
            }
        } catch (SQLException s) {
            NovusUtils.printLn(s.getMessage());
            insertado = false;
        }
        return insertado;
    }

    public ResultSet getMediosPagosWithImages(boolean traerEfectivo) throws SQLException {
        Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");
        PreparedStatement ps = conexion.prepareCall(QueryCollection.sqlGetMedioPago);
        ps.setBoolean(1, InfoViewController.hayInternet);
        ps.setBoolean(2, traerEfectivo);
        ResultSet result = ps.executeQuery();
        //  ps.close();
        return result;
    }

    public ArrayList<MediosPagosBean> getMediosPagos(boolean traerEfectivo) {
        Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");
        ArrayList<MediosPagosBean> mediosPagos = new ArrayList<>();
        try {
            String sql;
            boolean hayInternet = InfoViewController.hayInternet;
            sql = "select * from fnc_consultar_medios_pago_imagenes (?,?);";
            PreparedStatement ps = conexion.prepareCall(sql);
            ps.setBoolean(1, hayInternet);
            ps.setBoolean(2, traerEfectivo);
            ResultSet re = ps.executeQuery();
            mediosPagos = new ArrayList<>();
            while (re.next()) {
                MediosPagosBean medio = new MediosPagosBean();
                medio.setId(re.getLong("id"));
                medio.setCredito(false);
                medio.setDescripcion(re.getString("descripcion"));
                NovusUtils.printLn(medio.getDescripcion());
                medio.setComprobante(true);
                medio.setMaximo_valor(9999999);
                medio.setCambio(false);
                medio.setMinimo_valor(0);
                JsonObject atribuM = Main.gson.fromJson(re.getString("atributos"), JsonObject.class);
                medio.setAtributos(atribuM);
                boolean visible = true;
                medio.setPagosExternoValidado(false);
                medio.setIsPagoExterno(false);
                if (re.getString("atributos") != null) {
                    JsonObject atributos = Main.gson.fromJson(re.getString("atributos"), JsonObject.class);
                    if (atributos.get("visible") != null && !atributos.get("visible").isJsonNull()) {
                        visible = atributos.get("visible").getAsBoolean();
                        medio.setAtributos(atributos);
                    }
                    if (atributos.get("bonoTerpel") != null && !atributos.get("bonoTerpel").isJsonNull() && atributos.get("bonoTerpel").getAsBoolean()) {
                        medio.setIsPagoExterno(true);
                        medio.setCambio(true);
                    }
                }

                if (visible) {
                    mediosPagos.add(medio);
                }
            }
            return mediosPagos;
        } catch (SQLException ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mediosPagos;
    }

    public ArrayList<MediosPagosBean> getMediosPagosDefault(boolean mostrarDatafono, String mediosValidar) {
        Connection conexion = Main.obtenerConexionAsync("lazoexpressregistry");
        ArrayList<MediosPagosBean> mediosPagos = new ArrayList<>();
        try {
            String sql = "SELECT id, descripcion, empresas_id, credito, estado, \n"
                    + "cambio, minimo_valor, maximo_cambio, comprobante, atributos\n"
                    + " FROM medios_pagos WHERE estado='A' and id not in (" + mediosValidar + ")"
                            .concat(!mostrarDatafono ? " and descripcion not like 'CON DATAFONO%' " : "")
                            .concat("order by id asc;");
            PreparedStatement ps = conexion.prepareCall(sql);
            ResultSet re = ps.executeQuery();
            mediosPagos = new ArrayList<>();
            while (re.next()) {
                MediosPagosBean medio = new MediosPagosBean();
                medio.setId(re.getLong("id"));
                medio.setCredito(re.getString("credito").equals("S"));
                medio.setDescripcion(re.getString("descripcion"));
                medio.setComprobante(re.getString("comprobante").equals("S"));
                medio.setMaximo_valor(re.getFloat("maximo_cambio"));
                medio.setCambio(re.getString("cambio").equals("S"));
                medio.setMinimo_valor(re.getFloat("minimo_valor"));
                JsonObject atribuM = Main.gson.fromJson(re.getString("atributos"), JsonObject.class);
                medio.setAtributos(atribuM);
                boolean visible = true;
                medio.setPagosExternoValidado(false);
                medio.setIsPagoExterno(false);
                if (re.getString("atributos") != null) {
                    JsonObject atributos = Main.gson.fromJson(re.getString("atributos"), JsonObject.class);
                    if (atributos.get("visible") != null && !atributos.get("visible").isJsonNull()) {
                        visible = atributos.get("visible").getAsBoolean();
                        medio.setAtributos(atributos);
                    }
                    if (atributos.get("bonoTerpel") != null && !atributos.get("bonoTerpel").isJsonNull() && atributos.get("bonoTerpel").getAsBoolean()) {
                        medio.setIsPagoExterno(true);
                        medio.setCambio(true);
                        medio.setIsBono(true);
                    }
                }
                if (visible) {
                    if (!medio.getDescripcion().equalsIgnoreCase("APP TERPEL")) {
                        mediosPagos.add(medio);
                    }
                }
            }
            return mediosPagos;
        } catch (SQLException ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mediosPagos;
    }

    public boolean guardarEstadosSurtidor() {
        boolean insertado = true;
        class Inner {

            Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");

            void insertarEstadosSurtidor(String sql) throws SQLException {
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
            }

            boolean existeEstados() throws SQLException {
                String sql = "SELECT ID FROM surtidor_estado LIMIT 1";
                PreparedStatement ps = conexion.prepareStatement(sql);
                return ps.executeQuery().next();
            }

            boolean existeVentasOrigenes() throws SQLException {
                String sql = "SELECT ID FROM ventas_tipo_origen LIMIT 1";
                PreparedStatement ps = conexion.prepareStatement(sql);
                return ps.executeQuery().next();
            }

            void insertarVentasOrigenes() throws SQLException {
                String sql = "INSERT INTO ventas_tipo_origen (id,descripcion,estado) VALUES \n" + "(1,'SURTIDOR','A')\n"
                        + ",(2,'TIENDA','A')\n" + ",(3,'CANASTILLA','A')\n" + ";";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.executeUpdate();
            }
        }
        String[] estadosSurtidorQueries = {
            "INSERT INTO surtidor_estado (id, descripcion, detalles, tipo) VALUES(108, 'error', 'ERROR EN EL SURTIDOR', 5);",
            "INSERT INTO public.surtidor_estado (id, descripcion, detalles, tipo) VALUES(107, 'paused', 'SURTIDOR DETENIDO', 7);",
            "INSERT INTO public.surtidor_estado (id, descripcion, detalles, tipo) VALUES(106, 'terminated', 'VENTA TERMINADA', NULL);",
            "INSERT INTO public.surtidor_estado (id, descripcion, detalles, tipo) VALUES(100, 'idle', 'EN ESPERA', 1);",
            "INSERT INTO public.surtidor_estado (id, descripcion, detalles, tipo) VALUES(102, 'authorization_in_progresss', 'AUTORIZADO', 2);",
            "INSERT INTO public.surtidor_estado\n"
            + "(id, descripcion, detalles, tipo) VALUES(105, 'terminated', 'VENTA TERMINADA', 4);\n",
            "INSERT INTO public.surtidor_estado (id, descripcion, detalles, tipo)\n"
            + "VALUES(103, 'fueling', 'DESPACHO.', 3);\n",
            "INSERT INTO public.surtidor_estado\n"
            + "(id, descripcion, detalles, tipo) VALUES(104, 'fueling', 'DESPACHO..', 3);\n",
            "INSERT INTO public.surtidor_estado (id, descripcion, detalles, tipo)\n"
            + "VALUES(109, 'stoped', 'VENTA DETENIDA ', NULL);\n",
            "INSERT INTO public.surtidor_estado\n" + "(id, descripcion, detalles, tipo)\n"
            + "VALUES(101, 'authorization_in_progresss', 'SOLICITUD AUT', 2);\n",
            "INSERT INTO public.surtidor_estado\n" + "(id, descripcion, detalles, tipo)\n"
            + "VALUES(110, 'operacion', 'SURTIDOR OPERACION', NULL);"};
        try {
            Inner innerCall = new Inner();
            if (!innerCall.existeEstados()) {
                for (String sql : estadosSurtidorQueries) {
                    innerCall.insertarEstadosSurtidor(sql);
                }
            }
            if (!innerCall.existeVentasOrigenes()) {
                innerCall.insertarVentasOrigenes();
            }
        } catch (SQLException ex) {
            NovusUtils.printLn(ex.getMessage());
            insertado = false;
        }
        return insertado;
    }

    public ProductoBean getUnidades(long productoId) {
        Connection conexion = Main.obtenerConexionAsync("lazoexpressregistry");
        ProductoBean product = null;
        String sql = "Select p.id, p.plu, p.estado, p.descripcion, p.precio, p.tipo, p.cantidad_ingredientes, u.descripcion unidad, p.cantidad_impuestos, COALESCE(saldo, 0) saldo\n"
                + "from productos p \n"
                + "left join bodegas_productos bp on bp.productos_id=p.id join unidades u on p.unidades_medida = u.id\n"
                + "where p.id=?\n" + "";
        try {
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, productoId);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                product = new ProductoBean();
                product.setId(re.getLong("ID"));
                product.setPlu(re.getString("PLU"));
                product.setDescripcion(re.getString("DESCRIPCION"));
                product.setPrecio(re.getFloat("PRECIO"));
                product.setCantidad(re.getInt("SALDO"));
                product.setUnidades_medida(re.getString("UNIDAD"));
            }
        } catch (SQLException ex) {
            NovusUtils.printLn("Error: " + ex);
        }
        return product;
    }

    public boolean limpiarIngredientes(long productoId) throws DAOException {
        Connection conexion = Main.obtenerConexionAsync("lazoexpressregistry");

        boolean eliminado = true;

        try {
            String sql = "DELETE FROM productos_compuestos WHERE productos_id=?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, productoId);
            ps.executeUpdate();
        } catch (PSQLException s) {
            NovusUtils.printLn(s.getMessage());
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);

            eliminado = false;

        } catch (SQLException s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);

            NovusUtils.printLn(s.getMessage());
            eliminado = false;

        } catch (Exception s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);
            NovusUtils.printLn(s.getMessage());
            eliminado = false;
        }
        return eliminado;
    }

    public void integrarProducto(ProductoBean bean, ProductoBean ingrediente) throws DAOException {
        Connection conexion = Main.obtenerConexionAsync("lazoexpressregistry");

        try {
            String sql = "INSERT INTO public.productos_compuestos(\n"
                    + "            id, productos_id, ingredientes_id, cantidad)\n" + "    VALUES (?, ?, ?, ?);";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, ingrediente.getId());
            ps.setLong(2, bean.getId());
            ps.setLong(3, ingrediente.getProducto_compuesto_id());
            ps.setFloat(4, ingrediente.getProducto_compuesto_cantidad());
            ps.executeUpdate();
        } catch (PSQLException s) {
            if (s.getMessage().contains("productos_compuestos_pk")) {
                try {
                    String sql = "UPDATE public.productos_compuestos\n"
                            + "   SET productos_id=?, ingredientes_id=?, cantidad=?\n" + " WHERE id=?";
                    PreparedStatement ps = conexion.prepareStatement(sql);

                    ps.setLong(1, bean.getId());
                    ps.setLong(2, ingrediente.getProducto_compuesto_id());
                    ps.setFloat(3, ingrediente.getProducto_compuesto_cantidad());
                    ps.setFloat(4, ingrediente.getId());
                    ps.executeUpdate();

                } catch (Exception a) {
                    NovusUtils.printLn(a.getMessage());
                }
            } else {
                NovusUtils.printLn(Main.ANSI_RED + "hola soy yo el de where:( 2" + Main.ANSI_RESET);
                throw new DAOException(s.getMessage());
            }
        } catch (SQLException s) {
            NovusUtils.printLn(Main.ANSI_RED + "hola soy yo el de where:( 3" + Main.ANSI_RESET);
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            NovusUtils.printLn(Main.ANSI_RED + "hola soy yo el de where:( 4" + Main.ANSI_RESET);
            throw new DAOException(s.getMessage());
        }

    }

    public void integrarProductoCore(ProductoBean bean, ProductoBean ingrediente) throws SQLException, DAOException {
        Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");

        String sql = "CREATE TABLE IF NOT EXISTS public.productos_compuestos (\n"
                + "	id int NULL,\n"
                + "	productos_id int NULL,\n"
                + "	ingredientes_id int NULL,\n"
                + "	cantidad numeric(3) NULL\n"
                + ");";
        PreparedStatement ps = conexion.prepareStatement(sql);
        ps.executeUpdate();

        try {
            sql = "INSERT INTO public.productos_compuestos(\n"
                    + " id, productos_id, ingredientes_id, cantidad)\n" + "    VALUES (?, ?, ?, ?);";
            ps = conexion.prepareStatement(sql);
            ps.setLong(1, ingrediente.getProducto_compuesto_id());
            ps.setLong(2, bean.getId());
            ps.setLong(3, ingrediente.getId());
            ps.setFloat(4, ingrediente.getProducto_compuesto_cantidad());
            ps.executeUpdate();
        } catch (PSQLException s) {
            if (s.getMessage().contains("productos_compuestos_pk")) {
                try {
                    sql = "UPDATE public.productos_compuestos\n"
                            + "   SET productos_id=?, ingredientes_id=?, cantidad=?\n" + " WHERE id=?";
                    ps = conexion.prepareStatement(sql);
                    ps.setLong(1, bean.getId());
                    ps.setLong(2, ingrediente.getId());
                    ps.setFloat(3, ingrediente.getProducto_compuesto_cantidad());
                    ps.setFloat(4, ingrediente.getProducto_compuesto_id());
                    ps.executeUpdate();

                } catch (Exception a) {
                    NovusUtils.printLn(a.getMessage());
                }
            } else {
                throw new DAOException(s.getMessage());
            }
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }

    }

    public void actualizarNombreReginal(EmpresaBean bean) {
        try {
            Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");
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

            Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");

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
                        + "empresas_tipos_id, url_foto, dominio, alias, codigo_empresa, dominio_id, negocio_id, ciudades_descripcion, atributos,id_tipo_empresa)"
                        + " VALUES(?, ?, ?, ?, ?, ?, NULL, NULL, NULL, NULL , ?, NULL, ?, 1, ?, NULL,NULL,NULL, "
                        + "NULL, ? ,NULL,NULL, ?, ?, ?, ?, ?, ?::json,?)";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, bean.getId());
                ps.setString(2, bean.getRazonSocial() != null ? bean.getRazonSocial() : "");
                ps.setString(3, bean.getNit() != null ? bean.getNit() : "");
                ps.setString(4, bean.getDireccionPrincipal() != null ? bean.getDireccionPrincipal() : "");
                ps.setString(5, bean.getTelefonoPrincipal() != null ? bean.getTelefonoPrincipal() : "");
                ps.setString(6, bean.getEstado());
                ps.setTimestamp(7, new Timestamp(new java.util.Date().getTime()));
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
                ps.setLong(17, bean.getIdTipoEmpresa());
                ps.executeUpdate();
            }

            void actualizarEmpresa() throws SQLException {
                String sql = "UPDATE empresas  SET razon_social=?, nit=?, localizacion=?,"
                        + " ciudades_id=?, url_foto=?, dominio_id=?, alias=?,codigo_empresa=?, negocio_id=?,ciudades_descripcion=?, telefono=?,direccion=?,"
                        + " id_tipo_empresa=?"
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
                ps.setLong(13, bean.getIdTipoEmpresa());
                ps.setLong(14, bean.getId());
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
        Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");
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

    public boolean createEmpresasRegistry(EmpresaBean bean) throws DAOException {
        class Inner {

            Connection conexion = Main.obtenerConexionAsync("lazoexpressregistry");

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

    public boolean bloqueo() {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "select bloqueo from surtidores_detalles where bloqueo = 'S'";
        try (Statement st = conexion.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            return rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
            NovusUtils.printLn(ex.getMessage());
        }
        return false;
    }

    public void actualizarPerfil(int tipo, int id, String descripcion, String estado) {
        boolean result;
        String databases[] = {"lazoexpressregistry", "lazoexpresscore"};
        for (String database : databases) {
            try {
                Connection conexion = Main.obtenerConexion(database);
                String sqlI = "select id from perfiles where id = ?";
                PreparedStatement psI = conexion.prepareStatement(sqlI);
                psI.setInt(1, tipo);
                ResultSet rs = psI.executeQuery();
                result = rs.next();
                if (result) {
                    String sqlU = "update perfiles set tipo = ? where id = ?;";
                    PreparedStatement pst = conexion.prepareStatement(sqlU);
                    pst.setInt(1, id);
                    pst.setInt(2, tipo);
                    pst.executeUpdate();
                } else {
                    String sqlIn = "INSERT INTO public.perfiles (id, descripcion, estado, tipo) select ?, ?, ?, ?\n"
                            + "where not exists (select id from perfiles where id = ?) ;";
                    PreparedStatement pstIn = conexion.prepareStatement(sqlIn);
                    pstIn.setInt(1, tipo);
                    pstIn.setString(2, descripcion);
                    pstIn.setString(3, estado);
                    pstIn.setInt(4, id);
                    pstIn.setInt(5, tipo);
                    pstIn.executeUpdate();
                }
            } catch (SQLException ex) {
                Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
                NovusUtils.printLn(ex.getMessage());
            }
        }
    }

    public void actualizarPersona(long id, int tipo) {
        String databases[] = {"lazoexpressregistry", "lazoexpresscore"};
        for (String database : databases) {
            try {
                Connection conexion = Main.obtenerConexion(database);
                String sqlI = "update personas set perfiles_id = ? where id = ;";
                PreparedStatement psI = conexion.prepareStatement(sqlI);
                psI.setInt(1, tipo);
                psI.setLong(2, id);
                psI.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, null, ex);
                NovusUtils.printLn(ex.getMessage());
            }
        }
    }

    public void actualizarImagenReferenciaMedioPago() throws SQLException {
        String sql = "call public.actualizar_referencia_de_imagen_medio_pago();";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        PreparedStatement ps = conexion.prepareStatement(sql);
        ps.executeUpdate();
    }
    
    /**
     * Buscar producto por ID específicamente para actualización de cache
     * Utilizado por sincronización remota para obtener datos actualizados de BD
     */
    public com.bean.MovimientosDetallesBean findProductoByIdParaCache(Long productoId) throws SQLException {
        try {
            String sql = "SELECT " +
                        "p.id, " +
                        "p.descripcion, " +
                        "p.plu, " +
                        "COALESCE((SELECT identificador FROM identificadores i WHERE i.entidad_id = p.id AND i.origen = 2 LIMIT 1), '') as codigo_barra, " +
                        "p.precio, " +
                        "p.tipo, " +
                        "COALESCE(bp.saldo, 0) as saldo, " +
                        "p.estado " +
                        "FROM productos p " +
                        "LEFT JOIN bodegas_productos bp ON p.id = bp.productos_id " +
                        "WHERE p.id = ? AND p.estado IN ('A', 'B')";
            
            Connection conexion = Main.obtenerConexion("lazoexpressregistry");
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, productoId);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                com.bean.MovimientosDetallesBean producto = new com.bean.MovimientosDetallesBean();
                
                producto.setProductoId(rs.getLong("id"));
                producto.setDescripcion(rs.getString("descripcion"));
                producto.setPlu(rs.getString("plu"));
                producto.setCodigoBarra(rs.getString("codigo_barra"));
                producto.setPrecio((float) rs.getDouble("precio"));
                producto.setTipo(rs.getInt("tipo"));
                producto.setSaldo((float) rs.getDouble("saldo"));
                producto.setEstado(rs.getString("estado"));
                
                // Logs para seguimiento
                System.out.println(" Producto consultado BD para cache: " + producto.getDescripcion() + 
                                   " (PLU: " + producto.getPlu() + ", Precio: $" + producto.getPrecio() + 
                                   ", Saldo: " + producto.getSaldo() + ")");
                
                rs.close();
                ps.close();
                conexion.close();
                
                return producto;
            } else {
                // Producto no encontrado o inactivo
                System.out.println(" Producto ID " + productoId + " no encontrado o inactivo en BD");
                
                rs.close();
                ps.close();
                conexion.close();
                
                return null;
            }
            
        } catch (Exception e) {
            System.err.println(" Error consultando producto ID " + productoId + " para cache: " + e.getMessage());
            throw new SQLException("Error consultando producto para cache", e);
        }
    }

}
