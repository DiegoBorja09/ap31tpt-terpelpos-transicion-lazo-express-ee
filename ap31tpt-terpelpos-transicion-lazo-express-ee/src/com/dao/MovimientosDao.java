package com.dao;

import com.WT2.commons.domain.entity.TransaccionProceso;
import com.WT2.commons.domain.entity.beans.InformacionFidelizacionRetenida;
import com.WT2.commons.domain.entity.beans.InformacionVentaFidelizacionRetenida;
import com.WT2.loyalty.domain.entities.beans.ParamsLoyaltySinInternet;
import static com.controllers.NovusUtils.encriptacionBase64AES256;
import com.infrastructure.cache.ProductoUpdateInterceptorLiviano;

import com.application.commons.db_utils.DatabaseConnectionManager;
import com.application.commons.CtWacherParametrosEnum;
import com.application.useCases.wacherparametros.FindByParameterUseCase;
import com.application.useCases.consecutivos.ObtenerConsecutivoUseCase;
import com.bean.BodegaBean;
import com.bean.CatalogoBean;
import com.bean.CategoriaBean;
import com.bean.ConsecutivoBean;
import com.bean.CredencialBean;
import com.bean.ImpuestosBean;
import com.bean.MediosPagosBean;
import com.bean.MovimientosBean;
import com.bean.MovimientosDetallesBean;
import com.bean.PersonaBean;
import com.bean.ProductoBean;
import com.bean.ReporteJornadaBean;
import com.bean.SaltosBean;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.firefuel.Main;
import com.firefuel.datafonos.EstadoVentaDatafono;
import com.firefuel.facturacion.electronica.CodigosDianUnidad;
import com.firefuel.facturacion.electronica.TiposDocumentos;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.postgresql.util.PSQLException;
import com.application.useCases.movimientocliente.ObtenerIdTransmisionDesdeMovimientoUseCase;

/**
 *
 * @author novus
 */
public class MovimientosDao {

    ObtenerIdTransmisionDesdeMovimientoUseCase obtenerIdTransmisionDesdeMovimientoUseCase ;
    public static final String CONSECUTIVO_ESTADO_ACTIVO = "A";
    public static final String CONSECUTIVO_ESTADO_USO = "U";
    public static final String CONSECUTIVO_ESTADO_VENCIDO = "V";
    
    // Interceptor LIVIANO para invalidación automática de cache
    private ProductoUpdateInterceptorLiviano productoUpdateInterceptor;
    
    FindByParameterUseCase findByParameterUseCase = new FindByParameterUseCase(CtWacherParametrosEnum.CODIGO.getColumnName(), "REMISION");

    SimpleDateFormat sdf = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_AM);
    SimpleDateFormat sdf2 = new SimpleDateFormat(NovusConstante.FORMAT_BASIC_DATE);
    SimpleDateFormat sf = new SimpleDateFormat(NovusConstante.SIMPLE_FORMAT);
    DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);
    
    // Constructor único que inicializa interceptor liviano y otros componentes
    public MovimientosDao() {
        // Inicializar interceptor de cache
        try {
            this.productoUpdateInterceptor = new ProductoUpdateInterceptorLiviano();
            System.out.println("OK: Interceptor liviano inicializado en MovimientosDao");
        } catch (Exception e) {
            System.err.println("ERROR: No se pudo inicializar interceptor liviano en MovimientosDao: " + e.getMessage());
            this.productoUpdateInterceptor = null;
        }
        
        // Inicializar otros componentes
        this.obtenerIdTransmisionDesdeMovimientoUseCase = new ObtenerIdTransmisionDesdeMovimientoUseCase(0L);
    }

    // public long obtenerPrecioDesdeMovimientoDetalleComoLong(long idMovimiento) throws SQLException {
    //     Connection conexion = Main.obtenerConexion("lazoexpresscore");
    //     String sql = "SELECT precio FROM ct_movimientos_detalles WHERE movimientos_id = ? ORDER BY id ASC LIMIT 1";

    //     try (PreparedStatement ps = conexion.prepareStatement(sql)) {
    //         ps.setLong(1, idMovimiento);
    //         NovusUtils.printLn("com.dao.MovimientosDao.obtenerPrecioDesdeMovimientoDetalleComoLong() " + ps.toString());

    //         try (ResultSet rs = ps.executeQuery()) {
    //             if (rs.next()) {
    //                 BigDecimal precioDecimal = rs.getBigDecimal("precio");
    //                 if (precioDecimal != null) {
    //                     // Elimina decimales sin escalar, para que 9.999 → 9
    //                     return precioDecimal.setScale(0, RoundingMode.DOWN).longValue();
    //                 }
    //             }
    //         }
    //     } catch (SQLException ex) {
    //         Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
    //     }

    //     return 0L;
    // }
    //Caos de uso FindByParamerMovementDetailDUseCase remplaza al metodo obtenerPrecioDesdeMovimientoDetalleComoLong se usa en FidelizacionFacade
    


    // public void destruirVentaDespachadaCombustible(long idVenta) throws SQLException {
    //     Connection conexion = Main.obtenerConexion("lazoexpresscore");
    //     String sql = "UPDATE ct_movimientos SET estado_movimiento='034002' WHERE id = ?";
    //     try (PreparedStatement ps = conexion.prepareStatement(sql)) {
    //         ps.setLong(1, idVenta);
    //         NovusUtils.printLn("com.dao.MovimientosDao.destruirVentaDespachadaCombustible()" + ps.toString());
    //         ps.executeUpdate();
    //     } catch (SQLException ex) {
    //         Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
    //     }
    // }
    // caso de uso UpadteByEstateMovimiento remplaza al metodo destruirVentaDespachadaCombustible


    // public String consultarTipoMovimiento(long idMovimiento) {
    //     String tipo = null;
    //     String sql = "SELECT tipo FROM ct_movimientos WHERE id = ?";
    //     try (Connection conn = Main.obtenerConexionAsync("lazoexpresscore");
    //          PreparedStatement stmt = conn.prepareStatement(sql)) {
    //         stmt.setLong(1, idMovimiento);
    //         ResultSet rs = stmt.executeQuery();
    //         if (rs.next()) {
    //             tipo = rs.getString("tipo");
    //         }
    //     } catch (SQLException e) {
    //         Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, " Error consultando tipo por ID", e);
    //     }
    //     return tipo;
    // }
    // caso de uso FinById remplaza el metodo consultarTipoMovimiento


    // caso de uso FinById remplaza el metodo consultarConsecutivoPorId
    // public String consultarConsecutivoPorId(long idMovimiento) {
    //     String sql = "SELECT consecutivo FROM ct_movimientos WHERE id = ?";
    //     try (Connection conn = Main.obtenerConexionAsync("lazoexpresscore");
    //          PreparedStatement stmt = conn.prepareStatement(sql)) {
    //         stmt.setLong(1, idMovimiento);
    //         ResultSet rs = stmt.executeQuery();
    //         if (rs.next()) {
    //             return rs.getString("consecutivo");
    //         }
    //     } catch (SQLException e) {
    //         Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, " Error consultando consecutivo por ID", e);
    //     }
    //     return null;
    // }

    public ArrayList<MovimientosDetallesBean> getDespachosCombustiblePorPersona(long personaId, long jornada) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        ArrayList<MovimientosDetallesBean> lista = new ArrayList<>();
        try {
            String sql
                    = "	select  \n"
                    + "			cm.id,\n"
                    + "			0 as categoria_id,\n"
                    + "			'COMBUSTIBLE' as categoria_descripcion,\n"
                    + "			CONCAT('',coalesce(p.descripcion,''),'-> S',cm.atributos::json->>'surtidor','C',cm.atributos::json->>'cara','M',cm.atributos::json->>'manguera') as descripcion,\n"
                    + "			'A' as estado,\n"
                    + "			cmd.precio,\n"
                    + "			cmd.precio  as costo,\n"
                    + "			'[]'::json as impuestos,\n"
                    + "			'[]'::json as ingredientes,\n"
                    + "			1 as saldo,\n"
                    + "			1 as tipo,\n"
                    + "			null as codigo_barra,\n"
                    + "			0 as CANTIDAD_INGREDIENTES,\n"
                    + "			0 as CANTIDAD_IMPUESTOS,\n"
                    + "			cmd.cantidad,\n"
                    + "			cmd.productos_id ,\n"
                    + "			cmd.bodegas_id ,\n"
                    + "			cm.venta_total,\n"
                    + "			p.plu,\n"
                    + "               				pu.descripcion as unidad_descripcion,\n"
                    + "			p.unidad_medida_id as unidades_medida,\n"
                    + "			p.productos_tipos_id \n"
                    + "			from ct_movimientos cm \n"
                    + "			inner join surtidores_detalles sd on sd.cara::text = (coalesce(cm.atributos::json->>'cara','0')::text)\n"
                    + "			inner join ct_movimientos_detalles cmd on cmd.movimientos_id =cm.id \n"
                    + "			inner join productos p on p.id = cmd.productos_id \n"
                    + "			inner join productos_unidades pu on p.unidad_medida_id = pu.id \n"
                    + "			where tipo ='034' and estado_movimiento ='034001'  and cm.responsables_id = ? and cm.jornadas_id = ? \n"
                    + "			order by id desc \n"
                    + "		limit 1 ";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, personaId);
            ps.setLong(2, jornada);
            ResultSet re = ps.executeQuery();

            while (re.next()) {
                MovimientosDetallesBean movBean = new MovimientosDetallesBean();

                movBean.setId(re.getLong("ID"));
                movBean.setCategoriaId(re.getLong("categoria_id"));
                movBean.setCategoriaDesc(re.getString("categoria_descripcion"));
                movBean.setProductoId(re.getLong("productos_id"));
                movBean.setPlu(re.getString("PLU"));
                movBean.setDescripcion(re.getString("DESCRIPCION"));
                movBean.setPrecio(re.getFloat("PRECIO"));
                movBean.setSaldo(re.getInt("SALDO"));
                movBean.setTipo(NovusConstante.PRODUCTO_DESPACHO_COMBUSTIBLE_VENTA_MIXTA);
                movBean.setBodegasId(re.getInt("bodegas_id"));
                movBean.setCantidadUnidad(re.getFloat("cantidad"));
                movBean.setCantidad(re.getFloat("cantidad"));
                movBean.setUnidades_medida(re.getString("unidad_descripcion"));
                movBean.setUnidades_medida_id(re.getInt("unidades_medida"));
                movBean.setCantidadIngredientes(re.getInt("CANTIDAD_INGREDIENTES"));
                movBean.setCantidadImpuestos(re.getInt("CANTIDAD_IMPUESTOS"));
                movBean.setEstado(re.getString("estado"));
                movBean.setCosto(re.getFloat("venta_total"));
                movBean.setCodigoBarra(re.getString("codigo_barra"));
                movBean.setProducto_compuesto_costo(re.getFloat("COSTO"));
                movBean.setCompuesto(false);
                movBean.setImpuestos(organizarImpuestosProductos(new Gson().fromJson(re.getString("impuestos"), JsonArray.class)));
                movBean.setIngredientes(organizarIngredientesProductos(new Gson().fromJson(re.getString("ingredientes"), JsonArray.class)));
                lista.add(movBean);
            }
        } catch (PSQLException s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);
            throw new DAOException(s.getMessage());
        }
        return lista;
    }

    /*public void getTipoClienteREgistrado() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        try {
            String sql = "select * from personas where id=3";
            PreparedStatement ps = conexion.prepareStatement(sql);
            boolean clienteRegistrado = ps.executeQuery().next();
            if (!clienteRegistrado) {
                sql
                        = "INSERT INTO personas\n"
                        + "(id, tipos_identificaciones_id, identificacion, nombres, apellidos, estado, empresas_id, genero, sangre, create_user, create_date, update_user, update_date, ciudades_id, url_foto, perfiles_id, direccion, telefono)\n"
                        + "VALUES(3, 1, '3333333', 'CLIENTES', 'REGISTRADOS', 'A', ?, '', 'A+', 1, '2020-12-02 11:48:28.287', 1, '2020-12-21 09:29:36.378', 1, NULL, NULL, NULL, NULL);";
                ps = conexion.prepareStatement(sql);
                ps.setLong(1, Main.credencial.getEmpresas_id());
                ps.executeUpdate();
            }
        } catch (PSQLException s) {
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }
    }
    */
    /*
    public ArrayList<MovimientosDetallesBean> getCategoriasMovimiento(Set<Long> productosids) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        ArrayList<MovimientosDetallesBean> productosConCategorias = new ArrayList<>();
        String productosIdString = NovusUtils.join(",", productosids).toString();
        NovusUtils.printLn(productosIdString);
        try {
            String sql
                    = "select g.id as categoria_id, p.id as producto_id, g.grupo as categoria_descripcion from productos as p\n"
                    + "inner join grupos_entidad as ge on ge.entidad_id = p.id \n"
                    + "inner join grupos as g on g.id = ge.grupo_id \n"
                    + "where p.id in($1)";
            sql = sql.replace("$1", productosIdString);
            PreparedStatement ps = conexion.prepareStatement(sql);
            NovusUtils.printLn(sql);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                MovimientosDetallesBean detalle = new MovimientosDetallesBean();
                detalle.setId(re.getLong("producto_id"));
                detalle.setCategoriaId(re.getLong("categoria_id"));
                detalle.setCategoriaDesc(re.getString("categoria_descripcion"));
                productosConCategorias.add(detalle);
            }
        } catch (PSQLException s) {
            NovusUtils.printLn(s.getMessage());
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return productosConCategorias;
    }

     */

    ArrayList<ImpuestosBean> organizarImpuestosProductos(JsonArray arrayImpuestos) {
        ArrayList<ImpuestosBean> impuestos = new ArrayList<>();
        for (JsonElement element : arrayImpuestos) {
            JsonObject jsonImpuesto = element.getAsJsonObject();
            ImpuestosBean impuesto = new ImpuestosBean();
            impuesto.setId(jsonImpuesto.get("impuesto_id").getAsLong());
            impuesto.setDescripcion(jsonImpuesto.get("descripcion").getAsString());
            impuesto.setIva_incluido(jsonImpuesto.get("iva_incluido").getAsString().equals("S"));
            impuesto.setPorcentaje_valor(jsonImpuesto.get("porcentaje_valor").getAsString());
            impuesto.setValor(jsonImpuesto.get("valor").getAsFloat());
            impuestos.add(impuesto);
        }
        return impuestos;
    }

    ArrayList<ProductoBean> organizarIngredientesProductos(JsonArray arrayIngredientes) {
        ArrayList<ProductoBean> ingredientes = new ArrayList<>();
        for (JsonElement element : arrayIngredientes) {
            JsonObject jsonIngrediente = element.getAsJsonObject();
            ProductoBean producto = new ProductoBean();
            producto.setId(jsonIngrediente.get("ingredientes_id").getAsLong());
            producto.setDescripcion(jsonIngrediente.get("descripcion").getAsString());
            // producto.setCantidad(jsonIngrediente.get("SALDO").getAsInt());
            if (jsonIngrediente.get("saldo") != null && !jsonIngrediente.get("saldo").isJsonNull()) {
                producto.setSaldo(jsonIngrediente.get("saldo").getAsInt());
                producto.setCantidadUnidad(jsonIngrediente.get("saldo").getAsInt());
            } else {
                producto.setSaldo(0);
                producto.setCantidadUnidad(0);
            }
            if (jsonIngrediente.get("cantidad") != null && !jsonIngrediente.get("cantidad").isJsonNull()) {
                producto.setProducto_compuesto_cantidad(jsonIngrediente.get("cantidad").getAsFloat());
            } else {
                producto.setProducto_compuesto_cantidad(0);
            }
            if (jsonIngrediente.get("costo") != null && !jsonIngrediente.get("costo").isJsonNull()) {
                producto.setProducto_compuesto_costo(jsonIngrediente.get("costo").getAsFloat());
            } else {
                producto.setProducto_compuesto_costo(0);
            }
            ingredientes.add(producto);
        }
        return ingredientes;
    }

    public ArrayList<SaltosBean> historialSaltos(int length) throws DAOException, SQLException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");

        ArrayList<SaltosBean> lista = new ArrayList<>();

        String sql
                = "select sl.id,\n"
                + "sl.fecha_correcion,\n"
                + "sl.motivo,\n"
                + "sl.cara,\n"
                + "sl.manguera,\n"
                + "p.descripcion,\n"
                + "ps.nombre,\n"
                + "sl.sistema_acu_volumen,\n"
                + "sl.surtidor_acu_volumen\n"
                + "from saltos_lecturas sl\n"
                + "inner join\n"
                + "productos p on sl.producto_id = p.id\n"
                + "inner join personas ps\n"
                + "on sl.resposable = ps.id\n"
                + "order by fecha_correcion desc\n"
                + " FETCH FIRST ? ROWS ONLY";
        PreparedStatement ps = conexion.prepareStatement(sql);
        ps.setInt(1, length);
        ResultSet re = ps.executeQuery();
        while (re.next()) {
            SaltosBean sb = new SaltosBean();
            sb.setId(re.getInt("id"));
            sb.setFecha(re.getTimestamp("fecha_correcion"));
            sb.setMotivo(re.getInt("motivo"));
            sb.setCara(re.getInt("cara"));
            sb.setManguera(re.getInt("manguera"));
            sb.setDescripcion(re.getString("descripcion"));
            sb.setPersona(re.getString("nombre"));
            sb.setSistema_acu_v(re.getLong("sistema_acu_volumen"));
            sb.setSurtidor_acu_v(re.getLong("surtidor_acu_volumen"));
            lista.add(sb);
        }

        return lista;
    }

    public ArrayList<MovimientosDetallesBean> CambiosProgramadorInfo() throws DAOException, SQLException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");

        ArrayList<MovimientosDetallesBean> lista = new ArrayList<>();

        String sql
                = "select cp.id,"
                + " p.descripcion, "
                + "cp.fecha, "
                + "cp.aplicado,"
                + "cp.precio_original, "
                + "cp.estado  "
                + "from cambio_precio cp "
                + "inner join productos p "
                + "on cp.producto = p.id "
                + "order by cp.fecha desc";
        PreparedStatement ps = conexion.prepareStatement(sql);
        ResultSet re = ps.executeQuery();
        while (re.next()) {
            MovimientosDetallesBean movBean = new MovimientosDetallesBean();
            movBean.setId(re.getLong("ID"));
            movBean.setDescripcion(re.getString("DESCRIPCION"));
            movBean.setFecha(re.getTimestamp("FECHA"));
            movBean.setAplicado(re.getTimestamp("APLICADO"));
            movBean.setPrecio(re.getFloat("PRECIO_ORIGINAL"));
            movBean.setEstado(re.getString("ESTADO"));
            lista.add(movBean);
        }
        return lista;
    }

    public ArrayList<MovimientosDetallesBean> CambiosHistorialInfo() throws DAOException, SQLException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");

        ArrayList<MovimientosDetallesBean> lista = new ArrayList<>();

        String sql
                = "select acp.id, "
                + "acp.fecha_cambio, "
                + "acp.surtidor,"
                + "acp.cara, "
                + "acp.manguera,"
                + "acp.producto, "
                + "acp.precio_antiguo, "
                + "acp.precio_nuevo\n"
                + "from auditoria_cambio_precio acp\n"
                + "order by acp.fecha_cambio desc";
        PreparedStatement ps = conexion.prepareStatement(sql);
        ResultSet re = ps.executeQuery();
        while (re.next()) {
            MovimientosDetallesBean movBeans = new MovimientosDetallesBean();
            movBeans.setId(re.getLong("ID"));
            movBeans.setFecha(re.getTimestamp("FECHA_CAMBIO"));
            movBeans.setSurtidor(re.getInt("SURTIDOR"));
            movBeans.setCara(re.getInt("CARA"));
            movBeans.setManguera(re.getInt("MANGUERA"));
            movBeans.setDescripcion(re.getString("PRODUCTO"));
            movBeans.setPrecio(re.getFloat("PRECIO_ANTIGUO"));
            movBeans.setPrecioN(re.getFloat("PRECIO_NUEVO"));
            lista.add(movBeans);
        }

        return lista;
    }

    // public boolean VentaEnCurso(String estado, String negocio) {
    //     boolean respuesta = false;
    //     Connection conexion = Main.obtenerConexion("lazoexpresscore");
    //     try {
    //         String CreateTable = "CREATE TABLE if not exists public.lt_ventas_curso (\n" + "id serial NOT NULL,\n" + "negocio varchar NOT NULL,\n" + "estado varchar NOT NULL DEFAULT 'A')";

    //         PreparedStatement ps = conexion.prepareStatement(CreateTable);
    //         ps.executeUpdate();
    //         String sql = "";
    //         if (estado.equals("A")) {
    //             sql = "insert into lt_ventas_curso(negocio) values (?) returning * ";
    //             respuesta = true;
    //         } else {
    //             sql = "delete from lt_ventas_curso where negocio = ? returning * ";
    //         }
    //         ps = conexion.prepareStatement(sql);
    //         ps.setString(1, negocio);
    //         ps.executeQuery();
    //     } catch (SQLException ex) {
    //         Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
    //     }
    //     return respuesta;
    // }

    public boolean VentaEnCursoDatafono(long codigoAutorizacion, String estado, String negocio) {
        boolean respuesta = false;
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try {
            String CreateTable = "CREATE TABLE if not exists public.lt_ventas_curso (\n" + "id serial NOT NULL,\n" + "negocio varchar NOT NULL,\n" + "estado varchar NOT NULL DEFAULT 'A')";

            PreparedStatement ps = conexion.prepareStatement(CreateTable);
            ps.executeUpdate();
            String sql = "";
            sql = "insert into lt_ventas_curso(negocio, estado, codigo_autorizacion) values (?,?,?)";
            respuesta = true;
            ps = conexion.prepareStatement(sql);
            ps.setString(1, negocio);
            ps.setString(2, estado);
            ps.setLong(3, codigoAutorizacion);
            ps.executeQuery();
        } catch (SQLException ex) {
            NovusUtils.printLn(MovimientosDao.class.getName() + " " + ex);
        }
        return respuesta;
    }

    public ArrayList<MovimientosDetallesBean> findSummarizedProductsInfo() throws DAOException, SQLException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        ArrayList<MovimientosDetallesBean> lista = new ArrayList<>();

        String sql = "select p.id,\n"
                + " p.plu,p.descripcion,p.precio,coalesce(bp.saldo, 0) saldo \n"
                + "from bodegas_productos bp \n"
                + "inner join  productos p  on bp.productos_id=p.id\n"
                + "inner join  bodegas b ON b.id = BP.bodegas_id \n"
                + "where b.estado ='A' and  P.ESTADO in ('A', 'B') and p.puede_vender='S'\n"
                + " and p.p_atributos::json->>'tipoStore'= 'C' and  p.tipo <> -1 \n"
                + " ORDER BY bp.saldo ASC";
        PreparedStatement ps = conexion.prepareStatement(sql);
        ResultSet re = ps.executeQuery();
        while (re.next()) {
            MovimientosDetallesBean movBean = new MovimientosDetallesBean();
            movBean.setId(re.getLong("ID"));
            movBean.setPlu(re.getString("PLU"));
            movBean.setDescripcion(re.getString("DESCRIPCION"));
            movBean.setPrecio(re.getFloat("PRECIO"));
            movBean.setSaldo(re.getInt("SALDO"));
            lista.add(movBean);
        }
        return lista;
    }

    public ArrayList<MovimientosDetallesBean> findSummarizedProductsInfoMarket() throws DAOException, SQLException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        ArrayList<MovimientosDetallesBean> lista = new ArrayList<>();
        String sql;
        if (Main.TIPO_NEGOCIO.equals(NovusConstante.PARAMETER_CDL)) {
            sql = "SELECT p.id, p.plu, p.descripcion, p.precio, COALESCE(bp.saldo, 0) AS saldo\n"
                    + "FROM productos p\n"
                    + "LEFT JOIN bodegas_productos bp ON bp.productos_id = p.id\n"
                    + "LEFT JOIN bodegas b ON b.id = bp.bodegas_id AND b.estado = 'A'\n"
                    + "WHERE p.estado IN ('A', 'B') AND p.puede_vender = 'S' AND p.p_atributos::json->>'tipoStore' = 'CDL'\n"
                    + "and bp.saldo is not null;";
        } else {
            sql = "SELECT p.id, p.plu, p.descripcion, p.precio, COALESCE(bp.saldo, 0) AS saldo\n"
                    + "FROM productos p\n"
                    + "LEFT JOIN bodegas_productos bp ON bp.productos_id = p.id\n"
                    + "LEFT JOIN bodegas b ON b.id = bp.bodegas_id AND b.estado = 'A'\n"
                    + "WHERE p.estado IN ('A', 'B') AND p.puede_vender = 'S' AND p.p_atributos::json->>'tipoStore' = 'K' AND p.tipo = 23\n"
                    + "and bp.saldo is not null\n"
                    + "UNION\n"
                    + "SELECT p.id, p.plu, p.descripcion, p.precio, 0 AS saldo\n"
                    + "FROM productos p\n"
                    + "inner join productos_compuestos pc on p.id=pc.productos_id \n"
                    + "inner join bodegas_productos bp2 on bp2.productos_id = pc.ingredientes_id \n"
                    + "WHERE p.estado IN ('A', 'B') AND p.puede_vender = 'S' AND p.p_atributos::json->>'tipoStore' = 'K' AND p.tipo = 25\n"
                    + "and p.cantidad_ingredientes != 0\n"
                    + "ORDER BY saldo ASC;";
        }
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                NovusUtils.printLn(re.getString("DESCRIPCION"));
                MovimientosDetallesBean movBean = new MovimientosDetallesBean();
                movBean.setId(re.getLong("ID"));
                movBean.setPlu(re.getString("PLU"));
                movBean.setDescripcion(re.getString("DESCRIPCION"));
                movBean.setPrecio(re.getFloat("PRECIO"));
                movBean.setSaldo(re.getFloat("SALDO"));
                lista.add(movBean);
            }
        } catch (SQLException s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);
        }
        return lista;
    }

    /* public ArrayList<MovimientosDetallesBean> findAllProductoTipoKIOSCO(Long tipo) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        ArrayList<MovimientosDetallesBean> lista = new ArrayList<>();
        boolean isCDL = Main.TIPO_NEGOCIO
                .equals(NovusConstante.PARAMETER_CDL);
        String sql = "select * from fnc_buscar_productos_market (?, ?);";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setBoolean(1, isCDL);
            ps.setLong(2, NovusConstante.IDENTIFICADOR_CODIGO_BARRA);
            ResultSet re = ps.executeQuery();

            while (re.next()) {
                MovimientosDetallesBean movBean = new MovimientosDetallesBean();
                movBean.setId(re.getLong("ID"));
                movBean.setCategoriaId(re.getLong("categoria_id"));
                movBean.setCategoriaDesc(re.getString("categoria_descripcion"));
                movBean.setProductoId(re.getLong("ID"));
                movBean.setPlu(re.getString("PLU"));
                movBean.setDescripcion(re.getString("DESCRIPCION"));
                movBean.setPrecio(re.getFloat("PRECIO"));
                movBean.setSaldo(re.getInt("SALDO"));
                movBean.setTipo(re.getInt("tipo"));
                // bean.setCantidadUnidad(re.getInt("SALDO"));
                // bean.setCantidad(re.getInt("SALDO"));
                movBean.setUnidades_medida_id(re.getInt("unidades_medida"));

                movBean.setCantidadIngredientes(re.getInt("CANTIDAD_INGREDIENTES"));
                movBean.setCantidadImpuestos(re.getInt("CANTIDAD_IMPUESTOS"));
                movBean.setEstado(re.getString("estado"));
                movBean.setCosto(re.getFloat("COSTO"));
                movBean.setCodigoBarra(re.getString("codigo_barra"));
                movBean.setProducto_compuesto_costo(re.getFloat("COSTO"));
                movBean.setCompuesto(re.getInt("TIPO") == NovusConstante.TIPO_PRODUCTO_COMPUESTO || re.getInt("TIPO") == NovusConstante.TIPO_PRODUCTO_PROMOCION);
                movBean.setImpuestos(organizarImpuestosProductos(new Gson().fromJson(re.getString("impuestos"), JsonArray.class)));
                movBean.setIngredientes(organizarIngredientesProductos(new Gson().fromJson(re.getString("ingredientes"), JsonArray.class)));

                if (!movBean.getIngredientes().isEmpty()) {
                    //Encontrar bodega del ingrediente
                    for (ProductoBean ingrediente : movBean.getIngredientes()) {
                        if (movBean.getBodegasId() == 0) {
                            //Encontrar bodega del ingrediente
                            try (PreparedStatement pst = conexion.prepareStatement("SELECT BODEGAS_ID FROM BODEGAS_PRODUCTOS WHERE PRODUCTOS_ID=?")) {
                                pst.setLong(1, ingrediente.getId());

                                ResultSet re1 = pst.executeQuery();
                                while (re1.next()) {
                                    movBean.setBodegasId(re1.getLong(1));
                                }
                            }
                        }
                        movBean.setProducto_compuesto_costo(movBean.getProducto_compuesto_costo() + (ingrediente.getProducto_compuesto_costo() * ingrediente.getProducto_compuesto_cantidad()));
                    }
                }

                lista.add(movBean);
            }
        } catch (PSQLException s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);
            throw new DAOException(s.getMessage());
        }
        return lista;
    } */

    /* and coalesce(p.p_atributos::json->>'tipoStore', 'C') not in('K','T') */
    public ArrayList<MovimientosDetallesBean> findAllProductoTipo(Long tipo) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        ArrayList<MovimientosDetallesBean> lista = new ArrayList<>();

        try {
            String sql
                    = "select * from (select p.id,\n"
                    + "  p.plu, \n"
                    + "p.estado, \n"
                    + "  p.unidades_medida,\n"
                    + "p.descripcion,\n"
                    + " p.precio, \n"
                    + "p.tipo,\n"
                    + " p.cantidad_ingredientes, \n"
                    + "p.cantidad_impuestos,  \n"
                    + "coalesce (g.id ,-1) categoria_id,\n"
                    + "coalesce (g.grupo ,'OTROS') categoria_descripcion,\n"
                    + "coalesce((select identificador from identificadores i2 where entidad_id = p.id and origen = ? limit 1) ,'') codigo_barra, \n"
                    + "COALESCE(\n"
                    + "(SELECT array_to_json(array_agg(row_to_json(t))) FROM (\n"
                    + "		    SELECT i.id impuesto_id, i.descripcion, productos_id, iva_incluido, porcentaje_valor, valor\n"
                    + "                FROM public.productos_impuestos pi INNER JOIN impuestos i ON i.id=pi.impuestos_id\n"
                    + "                 WHERE pi.productos_id = p.id\n"
                    + ") t) ,'[]') impuestos,\n"
                    + "\n"
                    + "COALESCE(\n"
                    + "(SELECT array_to_json(array_agg(row_to_json(t))) FROM (\n"
                    + "		   SELECT PC.*, COSTO, SALDO, PI.DESCRIPCION, PI.TIPO FROM PRODUCTOS P\n"
                    + "                INNER JOIN PRODUCTOS_COMPUESTOS PC ON PC.PRODUCTOS_ID=P.ID\n"
                    + "                 INNER JOIN BODEGAS_PRODUCTOS BP ON PC.INGREDIENTES_ID=BP.PRODUCTOS_ID\n"
                    + "   INNER JOIN BODEGAS B ON BP.bodegas_id = B.id"
                    + "                  INNER JOIN PRODUCTOS PI ON PI.ID=PC.INGREDIENTES_ID WHERE P.ID=P.ID and B.estado != 'I'\n"
                    + ") t) ,'[]') ingredientes,\n"
                    + "		coalesce(bp.saldo, 0) saldo , \n"
                    + "		coalesce(bp.costo,0) costo \n"
                    + "		from productos p inner join bodegas_productos bp on bp.productos_id=p.id \n"
                    + "		left join grupos_entidad as ge on p.id = ge.entidad_id\n"
                    + "		left join grupos as g on ge.grupo_id = g.id\n"
                    + "where  P.ESTADO in ('A', 'B') and p.puede_vender='S' and p.id = bp.productos_id and coalesce(p.p_atributos::json->>'tipoStore', 'C') not in('K','T') ORDER BY p.descripcion ) productos where ( tipo <> -1)  ";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, NovusConstante.IDENTIFICADOR_CODIGO_BARRA);
            ResultSet re = ps.executeQuery();

            while (re.next()) {
                MovimientosDetallesBean movBean = new MovimientosDetallesBean();
                movBean.setId(re.getLong("ID"));
                movBean.setCategoriaId(re.getLong("categoria_id"));
                movBean.setCategoriaDesc(re.getString("categoria_descripcion"));
                movBean.setProductoId(re.getLong("ID"));
                movBean.setPlu(re.getString("PLU"));
                movBean.setDescripcion(re.getString("DESCRIPCION"));
                movBean.setPrecio(re.getFloat("PRECIO"));
                movBean.setSaldo(re.getInt("SALDO"));
                movBean.setTipo(re.getInt("tipo"));
                // bean.setCantidadUnidad(re.getInt("SALDO"));
                // bean.setCantidad(re.getInt("SALDO"));
                movBean.setUnidades_medida_id(re.getInt("unidades_medida"));

                movBean.setCantidadIngredientes(re.getInt("CANTIDAD_INGREDIENTES"));
                movBean.setCantidadImpuestos(re.getInt("CANTIDAD_IMPUESTOS"));
                movBean.setEstado(re.getString("estado"));
                movBean.setCosto(re.getFloat("COSTO"));
                movBean.setCodigoBarra(re.getString("codigo_barra"));
                movBean.setProducto_compuesto_costo(re.getFloat("COSTO"));
                movBean.setCompuesto(re.getInt("TIPO") == NovusConstante.TIPO_PRODUCTO_COMPUESTO || re.getInt("TIPO") == NovusConstante.TIPO_PRODUCTO_PROMOCION);
                movBean.setImpuestos(organizarImpuestosProductos(new Gson().fromJson(re.getString("impuestos"), JsonArray.class)));
                movBean.setIngredientes(organizarIngredientesProductos(new Gson().fromJson(re.getString("ingredientes"), JsonArray.class)));

                if (!movBean.getIngredientes().isEmpty()) {
                    //Encontrar bodega del ingrediente
                    for (ProductoBean ingrediente : movBean.getIngredientes()) {
                        if (movBean.getBodegasId() == 0) {
                            //Encontrar bodega del ingrediente
                            ps = conexion.prepareStatement("SELECT BODEGAS_ID FROM BODEGAS_PRODUCTOS WHERE PRODUCTOS_ID=?");
                            ps.setLong(1, ingrediente.getId());

                            ResultSet re1 = ps.executeQuery();
                            while (re1.next()) {
                                movBean.setBodegasId(re1.getLong(1));
                            }
                        }
                        movBean.setProducto_compuesto_costo(movBean.getProducto_compuesto_costo() + (ingrediente.getProducto_compuesto_costo() * ingrediente.getProducto_compuesto_cantidad()));
                    }
                }

                lista.add(movBean);
            }
        } catch (PSQLException s) {
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }
        return lista;
    }

    MovimientosDetallesBean bean = null;

    public MovimientosDetallesBean findByBarCodeKIOSCO(String code) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        String sql = "select * from fnc_buscar_productos_market_bar (?, ?, ?);";
        boolean isCDL = Main.TIPO_NEGOCIO
                .equals(NovusConstante.PARAMETER_CDL);
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setBoolean(2, isCDL);
            ps.setLong(3, NovusConstante.IDENTIFICADOR_CODIGO_BARRA);
            ResultSet re = ps.executeQuery();

            while (re.next()) {
                bean = new MovimientosDetallesBean();
                bean.setId(re.getLong("ID"));
                bean.setProductoId(re.getLong("ID"));
                bean.setPlu(re.getString("PLU"));
                bean.setBodegasId(re.getLong("bodega_id"));
                bean.setCategoriaId(re.getLong("categoria_id"));
                NovusUtils.printLn("[DEBUG] Asignando bodega_id: " + bean.getBodegasId() + " y categoria_id: " + bean.getCategoriaId() + " para producto: " + bean.getProductoId());
                bean.setCategoriaDesc(re.getString("categoria_descripcion"));
                bean.setDescripcion(re.getString("DESCRIPCION"));
                bean.setPrecio(re.getFloat("PRECIO"));
                bean.setTipo(re.getInt("tipo"));
                // bean.setCantidadUnidad(re.getInt("SALDO"));
                bean.setUnidades_medida_id(re.getInt("unidades_medida"));
                // bean.setCantidad(re.getInt("SALDO"));
                bean.setSaldo(re.getInt("SALDO"));
                bean.setCantidadIngredientes(re.getInt("CANTIDAD_INGREDIENTES"));
                bean.setCantidadImpuestos(re.getInt("CANTIDAD_IMPUESTOS"));
                bean.setEstado(re.getString("estado"));
                bean.setCosto(re.getFloat("COSTO"));
                bean.setCodigoBarra(re.getString("codigo_barra"));
                bean.setProducto_compuesto_costo(re.getFloat("COSTO"));
                bean.setCompuesto(re.getInt("TIPO") == NovusConstante.TIPO_PRODUCTO_COMPUESTO || re.getInt("TIPO") == NovusConstante.TIPO_PRODUCTO_PROMOCION);
                bean.setImpuestos(organizarImpuestosProductos(new Gson().fromJson(re.getString("impuestos"), JsonArray.class)));
                bean.setIngredientes(organizarIngredientesProductos(new Gson().fromJson(re.getString("ingredientes"), JsonArray.class)));
                if (!bean.getIngredientes().isEmpty()) {
                    //Encontrar bodega del ingrediente
                    for (ProductoBean ingrediente : bean.getIngredientes()) {
                        if (bean.getBodegasId() == 0) {
                            //Encontrar bodega del ingrediente
                            try (PreparedStatement pst = conexion.prepareStatement("SELECT BODEGAS_ID FROM BODEGAS_PRODUCTOS WHERE PRODUCTOS_ID=?")) {
                                pst.setLong(1, ingrediente.getId());

                                ResultSet re1 = pst.executeQuery();
                                while (re1.next()) {
                                    bean.setBodegasId(re1.getLong(1));
                                }
                            }
                        }
                        bean.setProducto_compuesto_costo(bean.getProducto_compuesto_costo() + (ingrediente.getProducto_compuesto_costo() * ingrediente.getProducto_compuesto_cantidad()));
                    }
                }
            }
        } catch (PSQLException s) {
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }
        return bean;
    }

    public MovimientosDetallesBean findByBarCode(String code) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        try {
            String sql
                    = "select * from (\n"
                    + "select p.id, p.plu, \n"
                    + "   p.estado, p.unidades_medida,\n"
                    + "   p.descripcion, p.precio, p.tipo,\n"
                    + "   grupo_id, p.cantidad_ingredientes, \n"
                    + "   p.cantidad_impuestos,  \n"
                    + "  coalesce (g.id ,-1) categoria_id,\n"
                    + "  coalesce (g.grupo ,'OTROS') categoria_descripcion,\n"
                    + "  coalesce((select identificador from identificadores i2 where entidad_id = p.id and origen = ? limit 1) ,'') codigo_barra, \n"
                    + "   COALESCE(\n"
                    + "   (SELECT array_to_json(array_agg(row_to_json(t))) FROM (\n"
                    + "    SELECT i.id impuesto_id, i.descripcion, productos_id, iva_incluido, porcentaje_valor, valor\n"
                    + "                   FROM public.productos_impuestos pi INNER JOIN impuestos i ON i.id=pi.impuestos_id\n"
                    + "                    WHERE pi.productos_id = p.id\n"
                    + "   ) t) ,'[]') impuestos,\n"
                    + "     COALESCE(p.p_atributos::json,'{}'::json) as p_atributos,      puede_vender,ingrediente,   COALESCE(\n"
                    + "   (SELECT array_to_json(array_agg(row_to_json(t))) FROM (\n"
                    + "   SELECT PC.*, COSTO, SALDO, PI.DESCRIPCION, PI.TIPO \n"
                    + "            FROM PRODUCTOS_COMPUESTOS PC\n"
                    + "            LEFT JOIN BODEGAS_PRODUCTOS BP ON PC.INGREDIENTES_ID=BP.PRODUCTOS_ID\n"
                    + "   INNER JOIN BODEGAS B ON BP.bodegas_id = B.id\n"
                    + "            INNER JOIN PRODUCTOS PI ON PI.ID=PC.INGREDIENTES_ID WHERE PC.productos_id = P.ID and B.estado != 'I' \n"
                    + "   ) t) ,'[]') ingredientes, coalesce(bp.bodegas_id) bodega_id,\n"
                    + "coalesce(bp.saldo, 0) saldo , \n"
                    + "coalesce(bp.costo,0) costo\n"
                    + "from productos p \n"
                    + "INNER join bodegas_productos bp on bp.productos_id=p.id\n"
                    + "left join grupos_entidad ge on p.id=ge.entidad_id  \n"
                    + "left join grupos  as g on g.id = ge.grupo_id \n"
                    + "where p.id= (select entidad_id from identificadores i where i.identificador=? and origen=?) and P.ESTADO in ('A', 'B') and p.puede_vender='S' and p.ingrediente='N' ORDER BY p.descripcion ) productos \n"
                    + "where (saldo > 0 OR tipo in (23, 25)) and coalesce(p_atributos::json->>'tipoStore', '') = 'C'";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, NovusConstante.IDENTIFICADOR_CODIGO_BARRA);
            ps.setString(2, code);
            ps.setLong(3, NovusConstante.IDENTIFICADOR_CODIGO_BARRA);
            ResultSet re = ps.executeQuery();

            while (re.next()) {
                bean = new MovimientosDetallesBean();
                bean.setId(re.getLong("ID"));
                bean.setProductoId(re.getLong("ID"));
                bean.setPlu(re.getString("PLU"));
                bean.setBodegasId(re.getLong("bodega_id"));
                bean.setCategoriaId(re.getLong("categoria_id"));
                bean.setCategoriaDesc(re.getString("categoria_descripcion"));
                bean.setDescripcion(re.getString("DESCRIPCION"));
                bean.setPrecio(re.getFloat("PRECIO"));
                bean.setTipo(re.getInt("tipo"));
                bean.setUnidades_medida_id(re.getInt("unidades_medida"));
                bean.setSaldo(re.getInt("SALDO"));
                bean.setCantidadIngredientes(re.getInt("CANTIDAD_INGREDIENTES"));
                bean.setCantidadImpuestos(re.getInt("CANTIDAD_IMPUESTOS"));
                bean.setEstado(re.getString("estado"));
                bean.setCosto(re.getFloat("COSTO"));
                bean.setCodigoBarra(re.getString("codigo_barra"));
                bean.setProducto_compuesto_costo(re.getFloat("COSTO"));
                bean.setCompuesto(re.getInt("TIPO") == NovusConstante.TIPO_PRODUCTO_COMPUESTO || re.getInt("TIPO") == NovusConstante.TIPO_PRODUCTO_PROMOCION);
                bean.setImpuestos(organizarImpuestosProductos(new Gson().fromJson(re.getString("impuestos"), JsonArray.class)));
                bean.setIngredientes(organizarIngredientesProductos(new Gson().fromJson(re.getString("ingredientes"), JsonArray.class)));

                if (!bean.getIngredientes().isEmpty()) {
                    //Encontrar bodega del ingrediente
                    for (ProductoBean ingrediente : bean.getIngredientes()) {
                        if (bean.getBodegasId() == 0) {
                            //Encontrar bodega del ingrediente
                            ps = conexion.prepareStatement("SELECT BODEGAS_ID FROM BODEGAS_PRODUCTOS WHERE PRODUCTOS_ID=?");
                            ps.setLong(1, ingrediente.getId());

                            ResultSet re1 = ps.executeQuery();
                            while (re1.next()) {
                                bean.setBodegasId(re1.getLong(1));
                            }
                        }
                        bean.setProducto_compuesto_costo(bean.getProducto_compuesto_costo() + (ingrediente.getProducto_compuesto_costo() * ingrediente.getProducto_compuesto_cantidad()));
                    }
                }
            }
        } catch (PSQLException s) {
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }
        return bean;
    }

    public MovimientosDetallesBean findByPluKIOSCO(String plu) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        MovimientosDetallesBean movBean = null;
        boolean isCDL = Main.TIPO_NEGOCIO
                .equals(NovusConstante.PARAMETER_CDL);
        String sql = "select * from fnc_buscar_productos_market_plu(?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, plu);
            ps.setBoolean(2, isCDL);
            ps.setLong(3, NovusConstante.IDENTIFICADOR_CODIGO_BARRA);

            System.out.println("Esta es la Query: " + ps.toString());
            ResultSet re = ps.executeQuery();

            while (re.next()) {
                movBean = new MovimientosDetallesBean();
                movBean.setId(re.getLong("ID"));
                movBean.setProductoId(re.getLong("ID"));
                movBean.setPlu(re.getString("PLU"));
                movBean.setDescripcion(re.getString("DESCRIPCION"));
                movBean.setPrecio(re.getFloat("PRECIO"));
                movBean.setTipo(re.getInt("tipo"));
                movBean.setBodegasId(re.getLong("bodega_id"));
                movBean.setCategoriaId(re.getLong("categoria_id"));
                movBean.setCategoriaDesc(re.getString("categoria_descripcion"));
                // bean.setCantidad(re.getInt("SALDO"));
                // bean.setCantidadUnidad(re.getInt("SALDO"));
                movBean.setUnidades_medida_id(re.getInt("unidades_medida"));
                movBean.setUnidades_medida(re.getString("unidad_descripcion"));
                movBean.setSaldo(re.getInt("SALDO"));
                movBean.setCantidadIngredientes(re.getInt("CANTIDAD_INGREDIENTES"));
                movBean.setCantidadImpuestos(re.getInt("CANTIDAD_IMPUESTOS"));
                movBean.setEstado(re.getString("estado"));
                movBean.setCosto(re.getFloat("COSTO"));
                movBean.setCodigoBarra(re.getString("codigo_barra"));
                movBean.setProducto_compuesto_costo(re.getFloat("COSTO"));
                movBean.setCompuesto(re.getInt("TIPO") == NovusConstante.TIPO_PRODUCTO_COMPUESTO || re.getInt("TIPO") == NovusConstante.TIPO_PRODUCTO_PROMOCION);

                movBean.setImpuestos(organizarImpuestosProductos(new Gson().fromJson(re.getString("impuestos"), JsonArray.class)));

                movBean.setIngredientes(organizarIngredientesProductos(new Gson().fromJson(re.getString("ingredientes"), JsonArray.class)));

                if (!movBean.getIngredientes().isEmpty()) {
                    //Encontrar bodega del ingrediente
                    for (ProductoBean ingrediente : movBean.getIngredientes()) {
                        if (movBean.getBodegasId() == 0) {
                            //Encontrar bodega del ingrediente
                            try (PreparedStatement pst = conexion.prepareStatement("SELECT BODEGAS_ID FROM BODEGAS_PRODUCTOS WHERE PRODUCTOS_ID=?")) {
                                pst.setLong(1, ingrediente.getId());
                                ResultSet re1 = pst.executeQuery();
                                while (re1.next()) {
                                    movBean.setBodegasId(re1.getLong(1));
                                }
                            }
                        }
                        movBean.setProducto_compuesto_costo(movBean.getProducto_compuesto_costo() + (ingrediente.getProducto_compuesto_costo() * ingrediente.getProducto_compuesto_cantidad()));
                    }
                }
            }
        } catch (PSQLException s) {
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }

        return movBean;
    }
    // esta funcion no esta en uso
    public MovimientosDetallesBean findByProductIdKIOSCO(long id) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        MovimientosDetallesBean movBean = null;

        try {
            String sql
                    = "select * from (\n"
                    + "select p.id, p.plu, \n"
                    + "   p.estado, p.unidades_medida,\n"
                    + "   p.descripcion, p.precio, p.tipo,\n"
                    + "   grupo_id, p.cantidad_ingredientes, \n"
                    + "   p.cantidad_impuestos,  \n"
                    + "  coalesce (g.id ,-1) categoria_id,\n"
                    + "  coalesce (g.grupo ,'OTROS') categoria_descripcion,\n"
                    + "  coalesce((select identificador from identificadores i2 where entidad_id = p.id and origen = ? limit 1) ,'') codigo_barra, \n"
                    + "   COALESCE(\n"
                    + "   (SELECT array_to_json(array_agg(row_to_json(t))) FROM (\n"
                    + "    SELECT i.id impuesto_id, i.descripcion, productos_id, iva_incluido, porcentaje_valor, valor\n"
                    + "                   FROM public.productos_impuestos pi INNER JOIN impuestos i ON i.id=pi.impuestos_id\n"
                    + "                    WHERE pi.productos_id = p.id\n"
                    + "   ) t) ,'[]') impuestos,\n"
                    + "     COALESCE(p.p_atributos::json,'{}'::json) as p_atributos,      puede_vender,ingrediente,   COALESCE(\n"
                    + "   (SELECT array_to_json(array_agg(row_to_json(t))) FROM (\n"
                    + "   SELECT PC.*, COSTO, SALDO, PI.DESCRIPCION, PI.TIPO \n"
                    + "            FROM PRODUCTOS_COMPUESTOS PC\n"
                    + "            LEFT JOIN BODEGAS_PRODUCTOS BP ON PC.INGREDIENTES_ID=BP.PRODUCTOS_ID\n"
                    + "   INNER JOIN BODEGAS B ON BP.bodegas_id = B.id\n"
                    + "            INNER JOIN PRODUCTOS PI ON PI.ID=PC.INGREDIENTES_ID WHERE PC.productos_id = P.ID and B.estado != 'I'\n"
                    + "   ) t) ,'[]') ingredientes, coalesce(bp.bodegas_id) bodega_id,\n"
                    + "coalesce(bp.saldo,0) saldo , \n"
                    + "coalesce(bp.costo,0) costo\n"
                    + "from productos p \n"
                    + "left join bodegas_productos bp on bp.productos_id=p.id\n"
                    + "left join grupos_entidad ge on p.id=ge.entidad_id  \n"
                    + "left join grupos  as g on g.id = ge.grupo_id \n"
                    + "where p.id = ? and P.ESTADO in ('A', 'B') and p.puede_vender='S' and p.ingrediente='N' ORDER BY p.descripcion ) productos \n"
                    + "where (tipo in (23, 25, 32)) and coalesce(p_atributos::json->>'tipoStore', '') in ('K', 'S')";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, NovusConstante.IDENTIFICADOR_CODIGO_BARRA);
            ps.setLong(2, id);
            ResultSet re = ps.executeQuery();

            while (re.next()) {
                movBean = new MovimientosDetallesBean();
                movBean.setId(re.getLong("ID"));
                movBean.setProductoId(re.getLong("ID"));
                movBean.setPlu(re.getString("PLU"));
                movBean.setDescripcion(re.getString("DESCRIPCION"));
                movBean.setPrecio(re.getFloat("PRECIO"));
                movBean.setTipo(re.getInt("tipo"));
                movBean.setBodegasId(re.getLong("bodega_id"));
                movBean.setCategoriaId(re.getLong("categoria_id"));
                movBean.setCategoriaDesc(re.getString("categoria_descripcion"));
                // bean.setCantidad(re.getInt("SALDO"));
                // bean.setCantidadUnidad(re.getInt("SALDO"));
                movBean.setUnidades_medida_id(re.getInt("unidades_medida"));
                movBean.setSaldo(re.getInt("SALDO"));
                movBean.setCantidadIngredientes(re.getInt("CANTIDAD_INGREDIENTES"));
                movBean.setCantidadImpuestos(re.getInt("CANTIDAD_IMPUESTOS"));
                movBean.setEstado(re.getString("estado"));
                movBean.setCosto(re.getFloat("COSTO"));
                movBean.setCodigoBarra(re.getString("codigo_barra"));
                movBean.setProducto_compuesto_costo(re.getFloat("COSTO"));
                movBean.setCompuesto(re.getInt("TIPO") == NovusConstante.TIPO_PRODUCTO_COMPUESTO || re.getInt("TIPO") == NovusConstante.TIPO_PRODUCTO_PROMOCION);
                movBean.setImpuestos(organizarImpuestosProductos(new Gson().fromJson(re.getString("impuestos"), JsonArray.class)));
                movBean.setIngredientes(organizarIngredientesProductos(new Gson().fromJson(re.getString("ingredientes"), JsonArray.class)));
                if (!movBean.getIngredientes().isEmpty()) {
                    //Encontrar bodega del ingrediente
                    for (ProductoBean ingrediente : movBean.getIngredientes()) {
                        if (movBean.getBodegasId() == 0) {
                            //Encontrar bodega del ingrediente
                            ps = conexion.prepareStatement("SELECT BODEGAS_ID FROM BODEGAS_PRODUCTOS WHERE PRODUCTOS_ID=?");
                            ps.setLong(1, ingrediente.getId());

                            ResultSet re1 = ps.executeQuery();
                            while (re1.next()) {
                                movBean.setBodegasId(re1.getLong(1));
                            }
                        }
                        movBean.setProducto_compuesto_costo(movBean.getProducto_compuesto_costo() + (ingrediente.getProducto_compuesto_costo() * ingrediente.getProducto_compuesto_cantidad()));
                    }
                }
            }
        } catch (PSQLException s) {
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }

        return movBean;
    }

    public MovimientosDetallesBean findByPlu(String plu) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        MovimientosDetallesBean movBean = null;

        try {
            String sql
                    = "select * from (\n"
                    + "select p.id, p.plu, \n"
                    + "   p.estado, p.unidades_medida,\n"
                    + "   p.descripcion, p.precio, p.tipo,\n"
                    + "   grupo_id, p.cantidad_ingredientes, \n"
                    + "   p.cantidad_impuestos,  \n"
                    + "  coalesce (g.id ,-1) categoria_id,\n"
                    + "  coalesce (g.grupo ,'OTROS') categoria_descripcion,\n"
                    + "  coalesce((select identificador from identificadores i2 where entidad_id = p.id and origen = ? limit 1) ,'') codigo_barra, \n"
                    + "   COALESCE(\n"
                    + "   (SELECT array_to_json(array_agg(row_to_json(t))) FROM (\n"
                    + "    SELECT i.id impuesto_id, i.descripcion, productos_id, iva_incluido, porcentaje_valor, valor\n"
                    + "                   FROM public.productos_impuestos pi INNER JOIN impuestos i ON i.id=pi.impuestos_id\n"
                    + "                    WHERE pi.productos_id = p.id\n"
                    + "   ) t) ,'[]') impuestos,\n"
                    + "     COALESCE(p.p_atributos::json,'{}'::json) as p_atributos,      puede_vender,ingrediente,   COALESCE(\n"
                    + "   (SELECT array_to_json(array_agg(row_to_json(t))) FROM (\n"
                    + "   SELECT PC.*, COSTO, SALDO, PI.DESCRIPCION, PI.TIPO \n"
                    + "            FROM PRODUCTOS_COMPUESTOS PC\n"
                    + "            LEFT JOIN BODEGAS_PRODUCTOS BP ON PC.INGREDIENTES_ID=BP.PRODUCTOS_ID\n"
                    + "   INNER JOIN BODEGAS B ON BP.bodegas_id = B.id\n"
                    + "            INNER JOIN PRODUCTOS PI ON PI.ID=PC.INGREDIENTES_ID WHERE PC.productos_id = P.ID and B.estado != 'I'\n"
                    + "   ) t) ,'[]') ingredientes, coalesce(bp.bodegas_id) bodega_id,\n"
                    + "coalesce(bp.saldo,0) saldo , \n"
                    + "coalesce(bp.costo,0) costo\n"
                    + "from productos p \n"
                    + "INNER join bodegas_productos bp on bp.productos_id=p.id\n"
                    + "left join grupos_entidad ge on p.id=ge.entidad_id  \n"
                    + "left join grupos  as g on g.id = ge.grupo_id \n"
                    + "where plu = ? and P.ESTADO in ('A', 'B') and p.puede_vender='S' and p.ingrediente='N' ORDER BY p.descripcion ) productos \n"
                    + "where (tipo in (23, 25, 32)) and coalesce(p_atributos::json->>'tipoStore', '') = 'C'";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, NovusConstante.IDENTIFICADOR_CODIGO_BARRA);
            ps.setString(2, plu);
            ResultSet re = ps.executeQuery();

            while (re.next()) {
                movBean = new MovimientosDetallesBean();
                movBean.setId(re.getLong("ID"));
                movBean.setProductoId(re.getLong("ID"));
                movBean.setPlu(re.getString("PLU"));
                movBean.setDescripcion(re.getString("DESCRIPCION"));
                movBean.setPrecio(re.getFloat("PRECIO"));
                movBean.setTipo(re.getInt("tipo"));
                movBean.setBodegasId(re.getLong("bodega_id"));
                movBean.setCategoriaId(re.getLong("categoria_id"));
                movBean.setCategoriaDesc(re.getString("categoria_descripcion"));
                // bean.setCantidad(re.getInt("SALDO"));
                // bean.setCantidadUnidad(re.getInt("SALDO"));
                movBean.setUnidades_medida_id(re.getInt("unidades_medida"));
                movBean.setSaldo(re.getInt("SALDO"));
                movBean.setCantidadIngredientes(re.getInt("CANTIDAD_INGREDIENTES"));
                movBean.setCantidadImpuestos(re.getInt("CANTIDAD_IMPUESTOS"));
                movBean.setEstado(re.getString("estado"));
                movBean.setCosto(re.getFloat("COSTO"));
                movBean.setCodigoBarra(re.getString("codigo_barra"));
                movBean.setProducto_compuesto_costo(re.getFloat("COSTO"));
                movBean.setCompuesto(re.getInt("TIPO") == NovusConstante.TIPO_PRODUCTO_COMPUESTO || re.getInt("TIPO") == NovusConstante.TIPO_PRODUCTO_PROMOCION);
                movBean.setImpuestos(organizarImpuestosProductos(new Gson().fromJson(re.getString("impuestos"), JsonArray.class)));
                movBean.setIngredientes(organizarIngredientesProductos(new Gson().fromJson(re.getString("ingredientes"), JsonArray.class)));
                if (!movBean.getIngredientes().isEmpty()) {
                    //Encontrar bodega del ingrediente
                    for (ProductoBean ingrediente : movBean.getIngredientes()) {
                        if (movBean.getBodegasId() == 0) {
                            //Encontrar bodega del ingrediente
                            ps = conexion.prepareStatement("SELECT BODEGAS_ID FROM BODEGAS_PRODUCTOS WHERE PRODUCTOS_ID=?");
                            ps.setLong(1, ingrediente.getId());

                            ResultSet re1 = ps.executeQuery();
                            while (re1.next()) {
                                movBean.setBodegasId(re1.getLong(1));
                            }
                        }
                        movBean.setProducto_compuesto_costo(movBean.getProducto_compuesto_costo() + (ingrediente.getProducto_compuesto_costo() * ingrediente.getProducto_compuesto_cantidad()));
                    }
                }
            }
        } catch (PSQLException s) {
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }
        return movBean;
    }

    public ArrayList<ProductoBean> findIngredientesById(long id) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        ArrayList<ProductoBean> lista = new ArrayList<>();

        try {
            String sql
                    = "SELECT PC.*, COSTO, SALDO, PI.DESCRIPCION, PI.TIPO\n"
                    + "FROM PRODUCTOS P\n"
                    + "INNER JOIN PRODUCTOS_COMPUESTOS PC ON PC.PRODUCTOS_ID=P.ID\n"
                    + "LEFT JOIN BODEGAS_PRODUCTOS BP ON PC.INGREDIENTES_ID=BP.PRODUCTOS_ID\n"
                    + "INNER JOIN PRODUCTOS PI ON PI.ID=PC.INGREDIENTES_ID\n"
                    + "WHERE P.ID=?";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet re = ps.executeQuery();

            while (re.next()) {
                ProductoBean productBean = new ProductoBean();
                productBean.setId(re.getLong("INGREDIENTES_ID"));
                productBean.setDescripcion(re.getString("DESCRIPCION"));
                productBean.setCantidad(re.getInt("SALDO"));
                productBean.setSaldo(re.getInt("SALDO"));
                productBean.setProducto_compuesto_cantidad(re.getFloat("cantidad"));
                productBean.setProducto_compuesto_costo(re.getFloat("costo"));
                if (re.getInt("TIPO") == NovusConstante.TIPO_PRODUCTO_COMPUESTO) {
                    productBean.setCompuesto(true);
                    productBean.setIngredientes(findIngredientesById(re.getLong("ID")));
                }
                lista.add(productBean);
            }
        } catch (PSQLException s) {
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }
        return lista;
    }
    // esta funcion no esta en uso
    public ArrayList<MovimientosDetallesBean> findCategoriaNegocio(CategoriaBean cat, String tipoNegocio) {
        ArrayList<MovimientosDetallesBean> lista = new ArrayList<>();
        switch (tipoNegocio) {
            case "KCO":
            case "CDL": {
                try {
                    lista = findByCategoriaKIOSCO(cat);
                } catch (DAOException ex) {
                    Logger.getLogger(MovimientosDao.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
            }
            break;
            case "CAN": {
                try {
                    lista = findByCategoria(cat);
                } catch (DAOException ex) {
                    Logger.getLogger(MovimientosDao.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
            }
            break;
            default:
                try {
                    lista = findByCategoriaKIOSCO(cat);
                } catch (DAOException ex) {
                    Logger.getLogger(MovimientosDao.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
                break;
        }
        return lista;
    }

    public ArrayList<MovimientosDetallesBean> findByCategoriaKIOSCO(CategoriaBean cat) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        boolean isCDL = Main.TIPO_NEGOCIO
                .equals(NovusConstante.PARAMETER_CDL);
        ArrayList<MovimientosDetallesBean> lista = new ArrayList<>();
        try {
            String sql
                    = "  select * from (\n"
                    + "select p.id, p.plu, \n"
                    + "   p.estado, p.unidades_medida,\n"
                    + "   p.descripcion, p.precio, p.tipo,\n"
                    + "   grupo_id, p.cantidad_ingredientes, \n"
                    + "   p.cantidad_impuestos,  \n"
                    + "  coalesce (g.id ,-1) categoria_id,\n"
                    + "  coalesce (g.grupo ,'OTROS') categoria_descripcion,\n"
                    + "  coalesce((select identificador from identificadores i2 where entidad_id = p.id and origen = ? limit 1) ,'') codigo_barra, \n"
                    + "   COALESCE(\n"
                    + "   (SELECT array_to_json(array_agg(row_to_json(t))) FROM (\n"
                    + "    SELECT i.id impuesto_id, i.descripcion, productos_id, iva_incluido, porcentaje_valor, valor\n"
                    + "                   FROM public.productos_impuestos pi INNER JOIN impuestos i ON i.id=pi.impuestos_id\n"
                    + "                    WHERE pi.productos_id = p.id\n"
                    + "   ) t) ,'[]') impuestos,\n"
                    + "     COALESCE(p.p_atributos::json,'{}'::json) as p_atributos,      puede_vender,ingrediente,   COALESCE(\n"
                    + "   (SELECT array_to_json(array_agg(row_to_json(t))) FROM (\n"
                    + "   SELECT PC.*, COSTO, SALDO, PI.DESCRIPCION, PI.TIPO \n"
                    + "            FROM PRODUCTOS_COMPUESTOS PC\n"
                    + "            LEFT JOIN BODEGAS_PRODUCTOS BP ON PC.INGREDIENTES_ID=BP.PRODUCTOS_ID\n"
                    + "   INNER JOIN BODEGAS B ON BP.bodegas_id = B.id\n"
                    + "            INNER JOIN PRODUCTOS PI ON PI.ID=PC.INGREDIENTES_ID WHERE PC.productos_id = P.ID and B.estado != 'I'\n"
                    + "   ) t) ,'[]') ingredientes, coalesce(bp.bodegas_id) bodega_id,\n"
                    + "coalesce(bp.saldo, 0) saldo , \n"
                    + "coalesce(bp.costo,0) costo from productos p \n"
                    + "inner join bodegas_productos bp on bp.productos_id=p.id\n"
                    + "left join grupos_entidad ge on p.id=ge.entidad_id  \n"
                    + "left join grupos  as g on g.id = ge.grupo_id \n"
                    + "where grupo_id "
                    + (cat.getId() > -1 ? "=? " : " is null \n")
                    + "and P.ESTADO in ('A', 'B') and p.puede_vender='S' and p.ingrediente='N' ORDER BY p.descripcion ) productos \n"
                    + "where (tipo in (23, 25, 32)) and coalesce(p_atributos::json->>'tipoStore', '') "
                    + (isCDL ? "in ('CDL');" : "in ('K', 'S');");

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, NovusConstante.IDENTIFICADOR_CODIGO_BARRA);
            if (cat.getId() > -1) {
                ps.setLong(2, cat.getId());
            }
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                MovimientosDetallesBean movBean = new MovimientosDetallesBean();
                movBean.setId(re.getLong("ID"));
                movBean.setProductoId(re.getLong("ID"));
                movBean.setBodegasId(re.getLong("bodega_id"));
                movBean.setPlu(re.getString("PLU"));
                movBean.setDescripcion(re.getString("DESCRIPCION"));
                movBean.setPrecio(re.getFloat("PRECIO"));
                movBean.setCategoriaId(re.getLong("categoria_id"));
                movBean.setCategoriaDesc(re.getString("categoria_descripcion"));
                movBean.setTipo(re.getInt("tipo"));
                movBean.setUnidades_medida_id(re.getInt("unidades_medida"));
                movBean.setSaldo(re.getInt("SALDO"));
                movBean.setCantidadIngredientes(re.getInt("CANTIDAD_INGREDIENTES"));
                movBean.setCantidadImpuestos(re.getInt("CANTIDAD_IMPUESTOS"));
                movBean.setEstado(re.getString("estado"));
                movBean.setCosto(re.getFloat("COSTO"));
                movBean.setCodigoBarra(re.getString("codigo_barra"));
                movBean.setProducto_compuesto_costo(re.getFloat("COSTO"));
                movBean.setCompuesto(re.getInt("TIPO") == NovusConstante.TIPO_PRODUCTO_COMPUESTO || re.getInt("TIPO") == NovusConstante.TIPO_PRODUCTO_PROMOCION);
                movBean.setImpuestos(organizarImpuestosProductos(new Gson().fromJson(re.getString("impuestos"), JsonArray.class)));
                movBean.setIngredientes(organizarIngredientesProductos(new Gson().fromJson(re.getString("ingredientes"), JsonArray.class)));
                if (!movBean.getIngredientes().isEmpty()) {
                    //Encontrar bodega del ingrediente
                    for (ProductoBean ingrediente : movBean.getIngredientes()) {
                        if (movBean.getBodegasId() == 0) {
                            //Encontrar bodega del ingrediente
                            ps = conexion.prepareStatement("SELECT BODEGAS_ID FROM BODEGAS_PRODUCTOS WHERE PRODUCTOS_ID=?");
                            ps.setLong(1, ingrediente.getId());

                            ResultSet re1 = ps.executeQuery();
                            while (re1.next()) {
                                movBean.setBodegasId(re1.getLong(1));
                            }
                        }
                        movBean.setProducto_compuesto_costo(movBean.getProducto_compuesto_costo() + (ingrediente.getProducto_compuesto_costo() * ingrediente.getProducto_compuesto_cantidad()));
                    }
                }
                System.out.println("CATEGORIA " + movBean.getCategoriaDesc() + " CATEGORIA_ID" + movBean.getCategoriaId());
                lista.add(movBean);
            }
        } catch (PSQLException s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);
            throw new DAOException(s.getMessage());
        }
        return lista;
    }

    /* and coalesce(p.p_atributos::json->>'tipoStore', 'C') not in('K','T') */
    public ArrayList<MovimientosDetallesBean> findByCategoria(CategoriaBean cat) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        ArrayList<MovimientosDetallesBean> lista = new ArrayList<>();

        try {
            String sql
                    = "  select * from (select p.id,p.plu,\n"
                    + "   p.estado, \n"
                    + "   p.unidades_medida,\n"
                    + "   p.descripcion,\n"
                    + "   p.precio, \n"
                    + "   p.tipo,\n"
                    + "   grupo_id,\n"
                    + "   p.cantidad_ingredientes, \n"
                    + "   p.cantidad_impuestos, COALESCE(p.p_atributos::json,'{}'::json) as p_atributos, \n"
                    + "  coalesce (g.id ,-1) categoria_id,\n"
                    + "  coalesce (g.grupo ,'OTROS') categoria_descripcion,\n"
                    + "   coalesce((select identificador from identificadores i2 where entidad_id = p.id and origen = ? limit 1) ,'') codigo_barra, \n"
                    + "   COALESCE(\n"
                    + "   (SELECT array_to_json(array_agg(row_to_json(t))) FROM (\n"
                    + "    SELECT i.id impuesto_id, i.descripcion, productos_id, iva_incluido, porcentaje_valor, valor\n"
                    + "                   FROM public.productos_impuestos pi INNER JOIN impuestos i ON i.id=pi.impuestos_id\n"
                    + "                    WHERE pi.productos_id = p.id\n"
                    + "   ) t) ,'[]') impuestos,\n"
                    + "   COALESCE(\n"
                    + "   (SELECT array_to_json(array_agg(row_to_json(t))) FROM (\n"
                    + "   SELECT PC.*, COSTO, SALDO, PI.DESCRIPCION, PI.TIPO FROM PRODUCTOS P\n"
                    + "                      INNER JOIN PRODUCTOS_COMPUESTOS PC ON PC.PRODUCTOS_ID=P.ID\n"
                    + "                    INNER JOIN BODEGAS_PRODUCTOS BP ON PC.INGREDIENTES_ID=BP.PRODUCTOS_ID\n"
                    + "   INNER JOIN BODEGAS B ON BP.bodegas_id = B.id\n"
                    + "                     INNER JOIN PRODUCTOS PI ON PI.ID=PC.INGREDIENTES_ID WHERE P.ID=P.ID and B.estado != 'I'\n"
                    + "   ) t) ,'[]') ingredientes, coalesce(bp.bodegas_id) bodega_id,\n"
                    + "coalesce(bp.saldo, 0) saldo , \n"
                    + "coalesce(bp.costo,0) costo \n"
                    + "from productos p \n"
                    + "INNER join bodegas_productos bp on bp.productos_id=p.id\n"
                    + "left join grupos_entidad ge on p.id=ge.entidad_id  \n"
                    + "left join grupos  as g on g.id = ge.grupo_id \n"
                    + "where grupo_id "
                    + (cat.getId() > -1 ? "=? " : " is null ")
                    + "and P.ESTADO in ('A', 'B') and p.puede_vender='S' and coalesce(p.p_atributos::json->>'tipoStore', 'C') = 'C' ORDER BY p.descripcion ) productos where saldo > 0  ";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, NovusConstante.IDENTIFICADOR_CODIGO_BARRA);
            if (cat.getId() > -1) {
                ps.setLong(2, cat.getId());
            }
            ResultSet re = ps.executeQuery();

            while (re.next()) {
                MovimientosDetallesBean movBean = new MovimientosDetallesBean();
                movBean.setId(re.getLong("ID"));
                movBean.setProductoId(re.getLong("ID"));
                movBean.setBodegasId(re.getLong("bodega_id"));
                movBean.setPlu(re.getString("PLU"));
                movBean.setDescripcion(re.getString("DESCRIPCION"));
                movBean.setPrecio(re.getFloat("PRECIO"));
                movBean.setCategoriaId(re.getLong("categoria_id"));
                movBean.setCategoriaDesc(re.getString("categoria_descripcion"));
                movBean.setTipo(re.getInt("tipo"));
                // bean.setCantidadUnidad(re.getInt("SALDO"));
                movBean.setUnidades_medida_id(re.getInt("unidades_medida"));
                movBean.setSaldo(re.getInt("SALDO"));
                // bean.setCantidad(re.getInt("SALDO"));
                movBean.setCantidadIngredientes(re.getInt("CANTIDAD_INGREDIENTES"));
                movBean.setCantidadImpuestos(re.getInt("CANTIDAD_IMPUESTOS"));
                movBean.setEstado(re.getString("estado"));
                movBean.setCosto(re.getFloat("COSTO"));
                movBean.setCodigoBarra(re.getString("codigo_barra"));
                movBean.setProducto_compuesto_costo(re.getFloat("COSTO"));
                movBean.setCompuesto(re.getInt("TIPO") == NovusConstante.TIPO_PRODUCTO_COMPUESTO || re.getInt("TIPO") == NovusConstante.TIPO_PRODUCTO_PROMOCION);
                movBean.setImpuestos(organizarImpuestosProductos(new Gson().fromJson(re.getString("impuestos"), JsonArray.class)));
                movBean.setIngredientes(organizarIngredientesProductos(new Gson().fromJson(re.getString("ingredientes"), JsonArray.class)));
                if (!movBean.getIngredientes().isEmpty()) {
                    //Encontrar bodega del ingrediente
                    for (ProductoBean ingrediente : movBean.getIngredientes()) {
                        if (movBean.getBodegasId() == 0) {
                            //Encontrar bodega del ingrediente
                            ps = conexion.prepareStatement("SELECT BODEGAS_ID FROM BODEGAS_PRODUCTOS WHERE PRODUCTOS_ID=?");
                            ps.setLong(1, ingrediente.getId());

                            ResultSet re1 = ps.executeQuery();
                            while (re1.next()) {
                                movBean.setBodegasId(re1.getLong(1));
                            }
                        }
                        movBean.setProducto_compuesto_costo(movBean.getProducto_compuesto_costo() + (ingrediente.getProducto_compuesto_costo() * ingrediente.getProducto_compuesto_cantidad()));
                    }
                }
                System.out.println("CATEGORIA " + movBean.getCategoriaDesc() + " CATEGORIA_ID" + movBean.getCategoriaId());
                lista.add(movBean);
            }
        } catch (PSQLException s) {
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }
        return lista;
    }

    //esta funcion no esta en uso
    public ArrayList<MediosPagosBean> consultarMediosDePago(long idMovimiento) {
        ArrayList<MediosPagosBean> lista = new ArrayList<>();

        String sql = "SELECT\n" +
                "            cmmp.ct_medios_pagos_id AS id,\n" +
                "            cmp.descripcion\n" +
                "        FROM\n" +
                "            ct_movimientos_medios_pagos cmmp\n" +
                "        JOIN\n" +
                "            ct_medios_pagos cmp ON cmmp.ct_medios_pagos_id = cmp.id\n" +
                "        WHERE\n" +
                "            cmmp.ct_movimientos_id = ?"
                ;

        try (Connection conn = Main.obtenerConexionAsync("lazoexpresscore");
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idMovimiento);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                MediosPagosBean medio = new MediosPagosBean();
                medio.setId(rs.getLong("id"));
                medio.setDescripcion(rs.getString("descripcion"));
                lista.add(medio);
            }

        } catch (SQLException e) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, "Error consultando medios de pago", e);
        }

        return lista;
    }


    //esta funcion no esta en uso
    public ArrayList<MediosPagosBean> findMediosPagos() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        ArrayList<MediosPagosBean> lista = new ArrayList<>();

        try {
            String sql = "SELECT * FROM MEDIOS_PAGOS WHERE ESTADO='A' ORDER BY DESCRIPCION DESC";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();

            while (re.next()) {
                MediosPagosBean mpBean = new MediosPagosBean();
                mpBean.setId(re.getLong("ID"));
                mpBean.setDescripcion(re.getString("DESCRIPCION"));
                mpBean.setComprobante(re.getString("COMPROBANTE").equals("S"));
                lista.add(mpBean);
            }
        } catch (PSQLException s) {
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }
        return lista;
    }
    // esta funcion no esta en uso
    public ProductoBean findProductByIdActive(long id) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        ProductoBean product = null;

        try {
            String sql
                    = "Select p.id, p.plu, p.estado, p.descripcion, p.precio, p.tipo, p.cantidad_ingredientes,  p.cantidad_impuestos, COALESCE(saldo, 0) saldo\n"
                    + "from productos p left join bodegas_productos bp on bp.productos_id=p.id "
                    + "where p.id=? and P.ESTADO in ('A', 'B') ";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                product = new ProductoBean();
                product.setId(re.getLong("ID"));
                product.setPlu(re.getString("PLU"));
                product.setDescripcion(re.getString("DESCRIPCION"));
                product.setPrecio(re.getFloat("PRECIO"));
                product.setCantidad(re.getInt("SALDO"));
                product.setSaldo(re.getInt("SALDO"));
                product.setEstado(re.getString("estado"));
                product.setCantidadIngredientes(re.getInt("CANTIDAD_INGREDIENTES"));
                product.setCantidadImpuestos(re.getInt("CANTIDAD_IMPUESTOS"));
                product.setImpuestos(findById(re.getLong("ID")));
                product.setIngredientes(findIngredientesById(re.getLong("ID")));
            }
        } catch (PSQLException s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);

            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);

            throw new DAOException(s.getMessage());
        }
        return product;
    }
    // esta funcion no esta en uso
    public ProductoBean findProductByOnlyId(long id) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        ProductoBean product = null;

        try {
            String sql
                    = "Select p.id, p.plu, p.estado, p.descripcion, p.precio, p.tipo, p.cantidad_ingredientes, u.descripcion unidad, p.cantidad_impuestos, COALESCE(saldo, 0) saldo\n"
                    + "from productos p \n"
                    + "left join bodegas_productos bp on bp.productos_id=p.id join unidades u on p.unidades_medida = u.id\n"
                    + "where p.id=?\n"
                    + "";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet re = ps.executeQuery();

            while (re.next()) {
                product = new ProductoBean();
                product.setId(re.getLong("ID"));
                product.setPlu(re.getString("PLU"));
                product.setDescripcion(re.getString("DESCRIPCION"));
                product.setPrecio(re.getFloat("PRECIO"));
                product.setCantidad(re.getInt("SALDO"));
                product.setUnidades_medida(re.getString("UNIDAD"));
                product.setSaldo(re.getInt("SALDO"));
                product.setEstado(re.getString("ESTADO"));
                product.setCantidadIngredientes(re.getInt("CANTIDAD_INGREDIENTES"));
                product.setCantidadImpuestos(re.getInt("CANTIDAD_IMPUESTOS"));
                product.setImpuestos(findById(re.getLong("ID")));
                product.setIngredientes(findIngredientesById(re.getLong("ID")));
            }
        } catch (PSQLException s) {
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }
        return product;
    }
    /* 
    public ArrayList<ProductoBean> buscarListaBasicaProductor(String keyword) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        ArrayList<ProductoBean> lista = new ArrayList<>();

        try {
            int NORMALES = 23;
            int COMPUESTOS = 25;
            int COMBOS = 32;
            String sql
                    = "Select p.id, p.plu, p.estado, p.descripcion, p.precio, "
                    + "p.tipo, p.cantidad_ingredientes,  p.cantidad_impuestos, COALESCE(saldo, 0) saldo\n"
                    + "from productos p \n"
                    + "left join bodegas_productos bp on bp.productos_id=p.id\n"
                    + "where p.estado in('A') AND p.descripcion like ? and tipo in ("
                    + NORMALES
                    + ", "
                    + COMPUESTOS
                    + ","
                    + COMBOS
                    + ")";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ResultSet re = ps.executeQuery();

            while (re.next()) {
                ProductoBean product = new ProductoBean();
                product.setId(re.getLong("ID"));
                product.setPlu(re.getString("PLU"));
                product.setDescripcion(re.getString("DESCRIPCION"));
                product.setPrecio(re.getFloat("PRECIO"));
                product.setSaldo(re.getInt("SALDO"));
                product.setEstado(re.getString("estado"));
                lista.add(product);
            }
        } catch (PSQLException s) {
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }
        return lista;
    }
     */
    /* 
    public ArrayList<CatalogoBean> buscarListaBasicaCategorias() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        ArrayList<CatalogoBean> lista = new ArrayList<>();

        try {
            String sql = "select * from grupos where atributos->>'visible'='true' ";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();

            while (re.next()) {
                CatalogoBean grupo = new CatalogoBean();
                grupo.setId(re.getLong("ID"));
                grupo.setDescripcion(re.getString("GRUPO"));
                lista.add(grupo);
            }
        } catch (PSQLException s) {
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }
        return lista;
    }
    */
    // esta funcion no esta en uso
    public ArrayList<ProductoBean> buscarListaBasicaProductosPorCategoria(int id) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        ArrayList<ProductoBean> lista = new ArrayList<>();

        try {
            String sql
                    = "select g.id, g.grupo, p.id pid, p.plu, p.estado, p.descripcion, p.precio, \n"
                    + "p.tipo, p.cantidad_ingredientes,  p.cantidad_impuestos, COALESCE(saldo, 0) saldo\n"
                    + "from productos p \n"
                    + "left join bodegas_productos bp on bp.productos_id=p.id\n"
                    + "inner join grupos_entidad ge on ge.entidad_id = p.id \n"
                    + "inner join grupos g on g.id = ge.grupo_id \n"
                    + "where g.id=? and p.estado in('A')";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet re = ps.executeQuery();

            while (re.next()) {
                ProductoBean producto = new ProductoBean();
                producto.setId(re.getLong("pid"));
                producto.setPlu(re.getString("PLU"));
                producto.setDescripcion(re.getString("DESCRIPCION"));
                producto.setPrecio(re.getFloat("PRECIO"));
                producto.setSaldo(re.getInt("SALDO"));
                producto.setEstado(re.getString("estado"));
                lista.add(producto);
            }
        } catch (PSQLException s) {
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }
        return lista;
    }

    public ArrayList<ImpuestosBean> findById(long id) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        ArrayList<ImpuestosBean> lista = new ArrayList<>();

        try {
            String sql
                    = "SELECT i.id impuesto_id, i.descripcion, productos_id, iva_incluido, porcentaje_valor, valor\n"
                    + "  FROM public.productos_impuestos pi\n"
                    + "  INNER JOIN impuestos i ON i.id=pi.impuestos_id WHERE pi.productos_id=?";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet re = ps.executeQuery();

            while (re.next()) {
                ImpuestosBean impBean = new ImpuestosBean();
                impBean.setId(re.getLong("impuesto_id"));
                impBean.setDescripcion(re.getString("descripcion"));
                impBean.setIva_incluido(re.getString("iva_incluido").equals("S"));
                impBean.setPorcentaje_valor(re.getString("porcentaje_valor"));
                impBean.setValor(re.getFloat("valor"));
                lista.add(impBean);
            }
        } catch (PSQLException s) {
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }
        return lista;
    }
    // esta funcion no esta en uso
    public MovimientosBean getLast(long empresaId, MovimientosBean movimiConsecutivo) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        MovimientosBean movimiento = null;

        try {
            String sql
                    = "SELECT id, empresas_id, operacion, fecha, consecutivo, consecutivo_id, consecutivo_observacion, "
                    + "       consecutivo_prefijo, persona_id, persona_nit, \n"
                    + "       persona_nombre, tercero_id, tercero_nit, tercero_nombre, costo_total, \n"
                    + "       venta_total, impuesto_total, descuento_total, origen_id, impreso, \n"
                    + "       create_user, create_date, update_user, update_date, remoto_id, \n"
                    + "       sincronizado, movimiento_estado\n"
                    + "  FROM public.movimientos WHERE empresas_id=? ORDER BY 1 DESC LIMIT 1";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, empresaId);
            ResultSet re = ps.executeQuery();

            while (re.next()) {
                movimiento = movimiConsecutivo;
                movimiento.setId(re.getLong("id"));
                movimiento.setFecha(re.getTimestamp("fecha"));

                movimiento.setConsecutivo(new ConsecutivoBean());
                movimiento.getConsecutivo().setConsecutivo_actual(re.getLong("consecutivo"));
                movimiento.getConsecutivo().setId(re.getLong("consecutivo_id"));
                movimiento.getConsecutivo().setFormato(re.getString("consecutivo_observacion"));
                movimiento.getConsecutivo().setPrefijo(re.getString("consecutivo_prefijo"));

                movimiento.setPersonaId(re.getLong("persona_id"));
                movimiento.setPersonaNit(re.getString("persona_nit"));
                movimiento.setPersonaNombre(re.getString("persona_nombre"));

                movimiento.setClienteId(re.getLong("tercero_id"));
                movimiento.setClienteNit(re.getString("tercero_nit"));
                movimiento.setClienteNombre(re.getString("tercero_nombre"));

                movimiento.setCostoTotal(re.getFloat("costo_total"));
                movimiento.setVentaTotal(re.getFloat("venta_total"));
                movimiento.setImpuestoTotal(re.getFloat("impuesto_total"));
                movimiento.setDescuentoTotal(re.getFloat("descuento_total"));

                movimiento.setOrigenId(re.getLong("origen_id"));
                movimiento.setImpreso(re.getString("impreso"));

                movimiento.setCreateUser(re.getLong("create_user"));
                movimiento.setCreateDate(re.getTimestamp("create_date"));
                movimiento.setUpdateUser(re.getLong("update_user"));
                movimiento.setUpdateDate(re.getTimestamp("update_date"));

                movimiento.setRemotoId(re.getLong("remoto_id"));
                movimiento.setSincronizado(re.getInt("sincronizado"));

                movimiento.setEmpresasId(empresaId);
            }

            if (movimiento != null) {
                sql
                        = "SELECT md.id, movimientos_id, bodegas_id, productos_id, cantidad, costo_producto, plu, descripcion, \n"
                        + "       tipo_operacion, fecha, md.precio, descuento_id, descuento_producto, \n"
                        + "       remoto_id, sincronizado, subtotal, sub_detalle_id\n"
                        + "  FROM movimientos_detalles md\n"
                        + "  INNER JOIN productos p ON p.id=md.productos_id\n"
                        + " WHERE movimientos_id=?";

                ps = conexion.prepareStatement(sql);
                ps.setLong(1, movimiento.getId());
                re = ps.executeQuery();

                while (re.next()) {
                    if (movimiento.getDetalles() == null) {
                        movimiento.setDetalles(new LinkedHashMap<>());
                    }
                    MovimientosDetallesBean detalle = new MovimientosDetallesBean();
                    detalle.setId(re.getLong("id"));
                    detalle.setMovimientoId(movimiento.getId());
                    detalle.setBodegasId(re.getLong("bodegas_id"));
                    detalle.setProductoId(re.getLong("productos_id"));
                    detalle.setCantidad(re.getFloat("cantidad"));
                    detalle.setPlu(re.getString("plu"));
                    detalle.setDescripcion(re.getString("descripcion"));
                    // detalle.setCostoProducto(re.getFloat("costo_producto"));
                    detalle.setProducto_compuesto_costo(re.getFloat("costo_producto"));
                    detalle.setTipo_operacion(re.getInt("tipo_operacion"));
                    detalle.setFecha(re.getTimestamp("fecha"));
                    detalle.setPrecio(re.getFloat("precio"));
                    detalle.setDescuentoId(re.getLong("descuento_id"));
                    detalle.setDescuentoProducto(re.getLong("descuento_producto"));
                    detalle.setRemotoId(re.getLong("remoto_id"));
                    detalle.setSincronizado(re.getInt("sincronizado"));
                    detalle.setSaldo(re.getInt("subtotal"));
                    detalle.setSubtotal(re.getInt("subtotal"));

                    movimiento.getDetalles().put(detalle.getId(), detalle);
                }

                for (Map.Entry<Long, MovimientosDetallesBean> entry : movimiento.getDetalles().entrySet()) {
                    Long key = entry.getKey();
                    MovimientosDetallesBean detalle = entry.getValue();

                    // CARGUE DE IMPUESTOS
                    sql = "select mi.*, i.descripcion, i.porcentaje_valor from movimientos_impuestos mi\n" + "inner join impuestos i on i.id=mi.impuestos_id\n" + "where mi.movimientos_detalles_id = ?";

                    ps = conexion.prepareStatement(sql);
                    ps.setLong(1, detalle.getId());
                    re = ps.executeQuery();

                    while (re.next()) {
                        if (detalle.getImpuestos() == null) {
                            detalle.setImpuestos(new ArrayList<>());
                        }
                        ImpuestosBean impuesto = new ImpuestosBean();
                        impuesto.setId(re.getLong("impuestos_id"));
                        impuesto.setDescripcion(re.getString("descripcion"));
                        impuesto.setCalculado(re.getFloat("impuesto_valor"));
                        impuesto.setPorcentaje_valor(re.getString("porcentaje_valor"));
                        detalle.getImpuestos().add(impuesto);
                    }
                }
            }
        } catch (PSQLException s) {
            throw new DAOException("11." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("12." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("13." + s.getMessage());
        }
        return movimiento;
    }
    // esta funcion no esta en uso
    public MovimientosBean getById(long movimientoId, MovimientosBean movimiConsecutivo) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        MovimientosBean movimiento = null;

        try {
            String sql
                    = "SELECT id, empresas_id, operacion, fecha, consecutivo, persona_id, persona_nit, \n"
                    + "       persona_nombre, tercero_id, tercero_nit, tercero_nombre, costo_total, \n"
                    + "       venta_total, impuesto_total, descuento_total, origen_id, impreso, \n"
                    + "       create_user, create_date, update_user, update_date, remoto_id, \n"
                    + "       sincronizado, movimiento_estado\n"
                    + "  FROM public.movimientos WHERE id=? ORDER BY 1 DESC LIMIT 1";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, movimientoId);
            ResultSet re = ps.executeQuery();

            while (re.next()) {
                movimiento = movimiConsecutivo;
                movimiento.setId(re.getLong("id"));
                movimiento.setFecha(re.getTimestamp("fecha"));
                movimiento.getConsecutivo().setConsecutivo_actual(re.getLong("consecutivo"));
                movimiento.setPersonaId(re.getLong("persona_id"));
                movimiento.setPersonaNit(re.getString("persona_nit"));
                movimiento.setPersonaNombre(re.getString("persona_nombre"));

                movimiento.setClienteId(re.getLong("tercero_id"));
                movimiento.setClienteNit(re.getString("tercero_nit"));
                movimiento.setClienteNombre(re.getString("tercero_nombre"));

                movimiento.setCostoTotal(re.getFloat("costo_total"));
                movimiento.setVentaTotal(re.getFloat("venta_total"));
                movimiento.setImpuestoTotal(re.getFloat("impuesto_total"));
                movimiento.setDescuentoTotal(re.getFloat("descuento_total"));

                movimiento.setOrigenId(re.getLong("origen_id"));
                movimiento.setImpreso(re.getString("impreso"));

                movimiento.setCreateUser(re.getLong("create_user"));
                movimiento.setCreateDate(re.getTimestamp("create_date"));
                movimiento.setUpdateUser(re.getLong("update_user"));
                movimiento.setUpdateDate(re.getTimestamp("update_date"));

                movimiento.setRemotoId(re.getLong("remoto_id"));
                movimiento.setSincronizado(re.getInt("sincronizado"));

                movimiento.setEmpresasId(re.getLong("empresas_id"));
            }

            if (movimiento != null) {
                sql
                        = "SELECT md.id, movimientos_id, bodegas_id, productos_id, cantidad, costo_producto, plu, descripcion, \n"
                        + "       tipo_operacion, fecha, md.precio, descuento_id, descuento_producto, \n"
                        + "       remoto_id, sincronizado, subtotal, sub_detalle_id\n"
                        + "  FROM movimientos_detalles md\n"
                        + "  INNER JOIN productos p ON p.id=md.productos_id\n"
                        + " WHERE movimientos_id=?";

                ps = conexion.prepareStatement(sql);
                ps.setLong(1, movimiento.getId());
                re = ps.executeQuery();

                while (re.next()) {
                    if (movimiento.getDetalles() == null) {
                        movimiento.setDetalles(new LinkedHashMap<>());
                    }
                    MovimientosDetallesBean detalle = new MovimientosDetallesBean();
                    detalle.setId(re.getLong("id"));
                    detalle.setMovimientoId(movimiento.getId());
                    detalle.setBodegasId(re.getLong("bodegas_id"));
                    detalle.setProductoId(re.getLong("productos_id"));
                    detalle.setCantidad(re.getFloat("cantidad"));
                    detalle.setPlu(re.getString("plu"));
                    detalle.setDescripcion(re.getString("descripcion"));
                    // detalle.setCostoProducto(re.getFloat("costo_producto"));
                    detalle.setProducto_compuesto_costo(re.getFloat("costo_producto"));
                    detalle.setTipo_operacion(re.getInt("tipo_operacion"));
                    detalle.setFecha(re.getTimestamp("fecha"));
                    detalle.setPrecio(re.getFloat("precio"));
                    detalle.setDescuentoId(re.getLong("descuento_id"));
                    detalle.setDescuentoProducto(re.getLong("descuento_producto"));
                    detalle.setRemotoId(re.getLong("remoto_id"));
                    detalle.setSincronizado(re.getInt("sincronizado"));
                    detalle.setSaldo(re.getInt("subtotal"));
                    detalle.setSubtotal(re.getInt("subtotal"));

                    movimiento.getDetalles().put(detalle.getId(), detalle);
                }

                for (Map.Entry<Long, MovimientosDetallesBean> entry : movimiento.getDetalles().entrySet()) {
                    Long key = entry.getKey();
                    MovimientosDetallesBean detalle = entry.getValue();

                    // CARGUE DE IMPUESTOS
                    sql = "select mi.*, i.descripcion, i.porcentaje_valor from movimientos_impuestos mi\n" + "inner join impuestos i on i.id=mi.impuestos_id\n" + "where mi.movimientos_detalles_id = ?";

                    ps = conexion.prepareStatement(sql);
                    ps.setLong(1, detalle.getId());
                    re = ps.executeQuery();

                    while (re.next()) {
                        if (detalle.getImpuestos() == null) {
                            detalle.setImpuestos(new ArrayList<>());
                        }
                        ImpuestosBean impuesto = new ImpuestosBean();
                        impuesto.setId(re.getLong("impuestos_id"));
                        impuesto.setDescripcion(re.getString("descripcion"));
                        impuesto.setCalculado(re.getFloat("impuesto_valor"));
                        impuesto.setPorcentaje_valor(re.getString("porcentaje_valor"));
                        detalle.getImpuestos().add(impuesto);
                    }
                }
            }
        } catch (PSQLException s) {
            throw new DAOException("11." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("12." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("13." + s.getMessage());
        }
        return movimiento;
    }

    public MovimientosBean createKIOSCO(MovimientosBean movimiento, CredencialBean cr, boolean isFe) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        NovusUtils.printLn("[createKIOSCO]" + movimiento.toString());
        try {
            ConsecutivoBean consecutivo = getConsecutivoMarket(isFe);

            if (consecutivo != null && consecutivo.getConsecutivo_actual() != 0) {
                String sql
                        = "INSERT INTO movimientos(\n"
                        + "            id, empresas_id, operacion, fecha, consecutivo, persona_id, persona_nit, \n"
                        + "            persona_nombre, tercero_id, tercero_nit, tercero_nombre, costo_total, \n"
                        + "            venta_total, impuesto_total, descuento_total, origen_id, impreso, \n"
                        + "            create_user, create_date, update_user, update_date, remoto_id, \n"
                        + "            sincronizado, movimiento_estado, consecutivo_id, consecutivo_observacion, consecutivo_prefijo, grupo_jornada_id)\n"
                        + "    VALUES (nextval('movimientos_id'), ?, ?, ?, ?, ?, ?, \n"
                        + "            ?, ?, ?, ?, ?, \n"
                        + "            ?, ?, ?, ?, ?, \n"
                        + "            ?, ?, NULL, NULL, CURRVAL('movimientos_id'), \n"
                        + "            0, 0, ?, ?, ?, ?) RETURNING ID";

                PreparedStatement ps = conexion.prepareStatement(sql);

                ps.setLong(1, movimiento.getEmpresasId());
                ps.setLong(2, movimiento.getOperacionId());
                ps.setTimestamp(3, new Timestamp(movimiento.getFecha().getTime()));
                ps.setLong(4, consecutivo.getConsecutivo_actual());
                ps.setLong(5, movimiento.getPersonaId());
                ps.setString(6, movimiento.getPersonaNit() != null ? movimiento.getPersonaNit() : "");
                ps.setString(7, movimiento.getPersonaNombre());
                if (movimiento.getClienteId() != 0) {
                    ps.setLong(8, movimiento.getClienteId());
                    ps.setString(9, movimiento.getClienteNit());
                    ps.setString(10, movimiento.getClienteNombre());
                } else {
                    ps.setNull(8, Types.NULL);
                    ps.setNull(9, Types.NULL);
                    ps.setNull(10, Types.NULL);
                }
                ps.setFloat(11, movimiento.getCostoTotal());
                ps.setFloat(12, movimiento.getVentaTotal());
                ps.setFloat(13, movimiento.getImpuestoTotal());
                ps.setFloat(14, movimiento.getDescuentoTotal());

                ps.setLong(15, Main.credencial.getId());
                ps.setString(16, "N");

                ps.setLong(17, 1);
                ps.setTimestamp(18, new Timestamp(new Date().getTime()));

                ps.setLong(19, consecutivo.getId());
                ps.setString(20, consecutivo.getFormato());
                ps.setString(21, consecutivo.getPrefijo());
                ps.setLong(22, movimiento.getGrupoJornadaId());

                ResultSet re = ps.executeQuery();

                while (re.next()) {
                    movimiento.setId(re.getLong("id"));
                    movimiento.setRemotoId(re.getLong("id"));
                }

                if (movimiento.getId() != 0) {
                    if (movimiento.getMediosPagos() != null) {
                        for (Map.Entry<Long, MediosPagosBean> entry : movimiento.getMediosPagos().entrySet()) {
                            Long key = entry.getKey();
                            MediosPagosBean value = entry.getValue();
                            sql
                                    = "INSERT INTO movimientos_medios_pagos(\n"
                                    + "            id, medios_pagos_id, movimientos_id, recibido,cambio,valor,comprobante_valor)\n"
                                    + "    VALUES (nextval('movimientos_medios_pagos_id'), ?, ?, ?,?,?,?);";
                            ps = conexion.prepareStatement(sql);
                            ps.setLong(1, value.getId());
                            ps.setLong(2, movimiento.getId());
                            ps.setFloat(3, value.getRecibido());
                            ps.setFloat(4, value.getCambio());
                            ps.setFloat(5, value.getValor());
                            ps.setString(6, value.getVoucher());
                            ps.executeUpdate();
                        }
                    }

                    for (Map.Entry<Long, MovimientosDetallesBean> entry : movimiento.getDetalles().entrySet()) {
                        Long key = entry.getKey();
                        MovimientosDetallesBean value = entry.getValue();
                        NovusUtils.printLn("[DEBUG] Antes de guardar: bodegasId=" + value.getBodegasId() + ", categoriaId=" + value.getCategoriaId() + ", productoId=" + value.getProductoId());
                        sql
                                = "INSERT INTO movimientos_detalles(\n"
                                + "            movimientos_id, bodegas_id, productos_id, cantidad, costo_producto, \n"
                                + "            tipo_operacion, fecha, precio, descuento_id, descuento_producto, \n"
                                + "            remoto_id, sincronizado, subtotal)\n"
                                + "    VALUES (?, ?, ?, ?, ?, \n"
                                + "            ?, ?, ?, ?, ?, \n"
                                + "            ?, 0, ?) RETURNING ID";

                        ps = conexion.prepareStatement(sql);

                        ps.setLong(1, movimiento.getId());
                        ps.setLong(2, value.getBodegasId());
                        ps.setLong(3, value.getProductoId());
                        ps.setFloat(4, value.getCantidadUnidad());
                        ps.setFloat(5, value.getCosto());

                        ps.setFloat(6, value.getTipo_operacion());
                        ps.setTimestamp(7, new Timestamp(new Date().getTime()));
                        ps.setFloat(8, value.getPrecio());
                        ps.setLong(9, value.getDescuentoId());
                        ps.setFloat(10, value.getDescuentoProducto());

                        ps.setFloat(11, cr.getId());
                        ps.setFloat(12, value.getSubtotal());

                        re = ps.executeQuery();

                        while (re.next()) {
                            value.setId(re.getLong("id"));
                            value.setRemotoId(re.getLong("id"));
                        }

                        if (value.isCompuesto()) {
                            for (ProductoBean ingrediente : value.getIngredientes()) {
                                sql = "UPDATE BODEGAS_PRODUCTOS SET SALDO=SALDO-? WHERE bodegas_id=? and productos_id=?";
                                ps = conexion.prepareStatement(sql);
                                ps.setFloat(1, (ingrediente.getProducto_compuesto_cantidad() * value.getCantidadUnidad()));

                                if (value.getBodegasId() == 0) {
                                    ps.setLong(2, movimiento.getBodegaId());
                                } else {
                                    ps.setLong(2, value.getBodegasId());
                                }
                                ps.setLong(3, ingrediente.getId());
                                ps.executeUpdate();
                            }
                        }

                        if (value.getTipo() != NovusConstante.PRODUCTO_DESPACHO_COMBUSTIBLE_VENTA_MIXTA) {
                            NovusUtils.printLn("[createKIOSCO] Iniciando actualización de inventario para producto simple");
                            NovusUtils.printLn("[createKIOSCO] Detalles del producto - ID: " + value.getProductoId() + 
                                              ", Cantidad: " + value.getCantidadUnidad() + 
                                              ", BodegaId: " + value.getBodegasId() + 
                                              ", BodegaId Movimiento: " + movimiento.getBodegaId());
                            // ✅ REFACTORED: Usar método helper para actualización segura de inventario
                            try {
                                actualizarInventarioProducto(conexion, value.getProductoId(), value.getCantidadUnidad(), value.getBodegasId());
                            } catch (DAOException e) {
                                NovusUtils.printLn("[createKIOSCO] ERROR actualizando inventario: " + e.getMessage());
                                continue; // Saltar este producto si hay error
                            }
                            
                            if (productoUpdateInterceptor != null && value.getProductoId() > 0) {
                                System.out.println(" Invalidando cache por VENTA - Producto ID: " + value.getProductoId() + ", Cantidad vendida: " + value.getCantidadUnidad());
                                productoUpdateInterceptor.onProductoActualizado(value.getProductoId());
                            }
                        }

                        if (value.getImpuestos() != null) {
                            for (ImpuestosBean impuesto : value.getImpuestos()) {
                                sql
                                        = "INSERT INTO movimientos_impuestos(\n" + "            id, impuestos_id, movimientos_detalles_id, impuesto_valor)\n" + "    VALUES (nextval('movimientos_impuestos_id'), ?, ?, ?);";
                                ps = conexion.prepareStatement(sql);
                                ps.setLong(1, impuesto.getId());
                                ps.setLong(2, value.getId());
                                ps.setFloat(3, impuesto.getCalculado());
                                ps.executeUpdate();
                            }
                        }
                    }

                    movimiento.setSuccess(true);
                    movimiento.setConsecutivo(consecutivo);
                }
            }
        } catch (PSQLException s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);
            throw new DAOException("11." + s.getMessage());
        } catch (SQLException s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);

            throw new DAOException("12." + s.getMessage());
        } catch (Exception s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);

            throw new DAOException("13." + s.getMessage());
        }

        try {
            if (movimiento.getConsecutivo() != null) {
                String sql = "UPDATE CONSECUTIVOS SET consecutivo_actual=? WHERE ID = ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, movimiento.getConsecutivo().getConsecutivo_actual() + 1);
                ps.setLong(2, movimiento.getConsecutivo().getId());
                ps.executeUpdate();
            } else {
                ConsecutivoBean cons = getConsecutivo(isFe);
                movimiento.setConsecutivo(cons);

                String sql = "UPDATE CONSECUTIVOS SET CONSECUTIVO_ACTUAL=CONSECUTIVO_ACTUAL + 1 WHERE ID = ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, movimiento.getConsecutivo().getId());
                ps.executeUpdate();
            }
        } catch (PSQLException s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);

            throw new DAOException("11." + s.getMessage());
        } catch (SQLException s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);

            throw new DAOException("12." + s.getMessage());
        } catch (Exception s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);

            throw new DAOException("13." + s.getMessage());
        }
        return movimiento;
    }

    public MovimientosBean create(MovimientosBean movimiento, CredencialBean cr, boolean isFe) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        try {
            ConsecutivoBean consecutivo = getConsecutivo(isFe);

            if (consecutivo != null && consecutivo.getConsecutivo_actual() != 0) {
                String sql
                        = "INSERT INTO movimientos(\n"
                        + "            id, empresas_id, operacion, fecha, consecutivo, persona_id, persona_nit, \n"
                        + "            persona_nombre, tercero_id, tercero_nit, tercero_nombre, costo_total, \n"
                        + "            venta_total, impuesto_total, descuento_total, origen_id, impreso, \n"
                        + "            create_user, create_date, update_user, update_date, remoto_id, \n"
                        + "            sincronizado, movimiento_estado, consecutivo_id, consecutivo_observacion, consecutivo_prefijo, grupo_jornada_id)\n"
                        + "    VALUES (nextval('movimientos_id'), ?, ?, ?, ?, ?, ?, \n"
                        + "            ?, ?, ?, ?, ?, \n"
                        + "            ?, ?, ?, ?, ?, \n"
                        + "            ?, ?, NULL, NULL, CURRVAL('movimientos_id'), \n"
                        + "            0, 0, ?, ?, ?, ?) RETURNING ID";

                PreparedStatement ps = conexion.prepareStatement(sql);

                ps.setLong(1, movimiento.getEmpresasId());
                ps.setLong(2, movimiento.getOperacionId());
                ps.setTimestamp(3, new Timestamp(movimiento.getFecha().getTime()));
                ps.setLong(4, consecutivo.getConsecutivo_actual());
                ps.setLong(5, movimiento.getPersonaId());
                ps.setString(6, movimiento.getPersonaNit() != null ? movimiento.getPersonaNit() : "");
                ps.setString(7, movimiento.getPersonaNombre());
                if (movimiento.getClienteId() != 0) {
                    ps.setLong(8, movimiento.getClienteId());
                    ps.setString(9, movimiento.getClienteNit());
                    ps.setString(10, movimiento.getClienteNombre());
                } else {
                    ps.setNull(8, Types.NULL);
                    ps.setNull(9, Types.NULL);
                    ps.setNull(10, Types.NULL);
                }
                ps.setFloat(11, movimiento.getCostoTotal());
                ps.setFloat(12, movimiento.getVentaTotal());
                ps.setFloat(13, movimiento.getImpuestoTotal());
                ps.setFloat(14, movimiento.getDescuentoTotal());

                ps.setLong(15, Main.credencial.getId());
                ps.setString(16, "N");

                ps.setLong(17, 1);
                ps.setTimestamp(18, new Timestamp(new Date().getTime()));

                ps.setLong(19, consecutivo.getId());
                ps.setString(20, consecutivo.getFormato());
                ps.setString(21, consecutivo.getPrefijo());
                ps.setLong(22, movimiento.getGrupoJornadaId());

                ResultSet re = ps.executeQuery();

                while (re.next()) {
                    movimiento.setId(re.getLong("id"));
                    movimiento.setRemotoId(re.getLong("id"));
                }

                if (movimiento.getId() != 0) {
                    if (movimiento.getMediosPagos() != null) {
                        for (Map.Entry<Long, MediosPagosBean> entry : movimiento.getMediosPagos().entrySet()) {
                            Long key = entry.getKey();
                            MediosPagosBean value = entry.getValue();
                            sql
                                    = "INSERT INTO movimientos_medios_pagos(\n"
                                    + "            id, medios_pagos_id, movimientos_id, recibido,cambio,valor,comprobante_valor)\n"
                                    + "    VALUES (nextval('movimientos_medios_pagos_id'), ?, ?, ?,?,?,?);";
                            ps = conexion.prepareStatement(sql);
                            ps.setLong(1, value.getId());
                            ps.setLong(2, movimiento.getId());
                            ps.setFloat(3, value.getRecibido());
                            ps.setFloat(4, value.getCambio());
                            ps.setFloat(5, value.getValor());
                            ps.setString(6, value.getVoucher());
                            ps.executeUpdate();
                        }
                    }

                    for (Map.Entry<Long, MovimientosDetallesBean> entry : movimiento.getDetalles().entrySet()) {
                        Long key = entry.getKey();
                        MovimientosDetallesBean value = entry.getValue();

                        sql
                                = "INSERT INTO movimientos_detalles(\n"
                                + "            movimientos_id, bodegas_id, productos_id, cantidad, costo_producto, \n"
                                + "            tipo_operacion, fecha, precio, descuento_id, descuento_producto, \n"
                                + "            remoto_id, sincronizado, subtotal)\n"
                                + "    VALUES (?, ?, ?, ?, ?, \n"
                                + "            ?, ?, ?, ?, ?, \n"
                                + "            ?, 0, ?) RETURNING ID";

                        ps = conexion.prepareStatement(sql);

                        ps.setLong(1, movimiento.getId());
                        ps.setLong(2, value.getBodegasId());
                        ps.setLong(3, value.getProductoId());
                        ps.setFloat(4, value.getCantidadUnidad());
                        // ps.setFloat(5, value.getCostoProducto());
                        ps.setFloat(5, value.getProducto_compuesto_costo());

                        ps.setFloat(6, value.getTipo_operacion());
                        ps.setTimestamp(7, new Timestamp(new Date().getTime()));
                        ps.setFloat(8, value.getPrecio());
                        ps.setLong(9, value.getDescuentoId());
                        ps.setFloat(10, value.getDescuentoProducto());

                        ps.setFloat(11, cr.getId());
                        ps.setFloat(12, value.getSubtotal());

                        re = ps.executeQuery();

                        while (re.next()) {
                            value.setId(re.getLong("id"));
                            value.setRemotoId(re.getLong("id"));
                        }

                        if (value.isCompuesto()) {
                            for (ProductoBean ingrediente : value.getIngredientes()) {
                                sql = "UPDATE BODEGAS_PRODUCTOS SET SALDO=SALDO-? WHERE bodegas_id=? and productos_id=?";
                                ps = conexion.prepareStatement(sql);
                                ps.setFloat(1, (ingrediente.getProducto_compuesto_cantidad() * value.getCantidadUnidad()));
                                ps.setLong(2, value.getBodegasId());
                                ps.setLong(3, ingrediente.getId());
                                ps.executeUpdate();
                            }
                        } else if (value.getTipo() != NovusConstante.PRODUCTO_DESPACHO_COMBUSTIBLE_VENTA_MIXTA) {
                            // ✅ REFACTORED: Usar método helper para actualización segura de inventario
                            try {
                                actualizarInventarioProducto(conexion, value.getProductoId(), value.getCantidadUnidad(), value.getBodegasId());
                            } catch (DAOException e) {
                                NovusUtils.printLn("[createKIOSCO] ERROR actualizando inventario: " + e.getMessage());
                                continue; // Saltar este producto si hay error
                            }
                            
                            //  NUEVO: Invalidar cache después de VENTA simple (actualización de saldo)
                            if (productoUpdateInterceptor != null && value.getProductoId() > 0) {
                                System.out.println(" Invalidando cache por VENTA SIMPLE - Producto ID: " + value.getProductoId() + ", Cantidad vendida: " + value.getCantidadUnidad());
                                productoUpdateInterceptor.onProductoActualizado(value.getProductoId());
                            }
                        }

                        if (value.getImpuestos() != null) {
                            for (ImpuestosBean impuesto : value.getImpuestos()) {
                                sql
                                        = "INSERT INTO movimientos_impuestos(\n" + "            id, impuestos_id, movimientos_detalles_id, impuesto_valor)\n" + "    VALUES (nextval('movimientos_impuestos_id'), ?, ?, ?);";
                                ps = conexion.prepareStatement(sql);
                                ps.setLong(1, impuesto.getId());
                                ps.setLong(2, value.getId());
                                ps.setFloat(3, impuesto.getCalculado());
                                ps.executeUpdate();
                            }
                        }
                    }

                    movimiento.setSuccess(true);
                    movimiento.setConsecutivo(consecutivo);
                }
            }
        } catch (PSQLException s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);
            throw new DAOException("11." + s.getMessage());
        } catch (SQLException s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);

            throw new DAOException("12." + s.getMessage());
        } catch (Exception s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);

            throw new DAOException("13." + s.getMessage());
        }
        try {
            if (movimiento.getConsecutivo() != null) {
                String sql = "UPDATE CONSECUTIVOS SET consecutivo_actual=? WHERE ID = ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, movimiento.getConsecutivo().getConsecutivo_actual() + 1);
                ps.setLong(2, movimiento.getConsecutivo().getId());
                ps.executeUpdate();
            } else {
                ConsecutivoBean cons = getConsecutivo(isFe);
                movimiento.setConsecutivo(cons);

                String sql = "UPDATE CONSECUTIVOS SET CONSECUTIVO_ACTUAL=CONSECUTIVO_ACTUAL + 1 WHERE ID = ?";
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setLong(1, movimiento.getConsecutivo().getId());
                ps.executeUpdate();
            }
        } catch (PSQLException s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);

            throw new DAOException("11." + s.getMessage());
        } catch (SQLException s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);

            throw new DAOException("12." + s.getMessage());
        } catch (Exception s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);

            throw new DAOException("13." + s.getMessage());
        }
        return movimiento;
    }

    // esta funcion no esta en uso
    public void integrarProductoCore(ProductoBean bean, ProductoBean ingrediente) throws SQLException, DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");

        String sql = "CREATE TABLE IF NOT EXISTS public.productos_compuestos (\n" + "	id int NULL,\n" + "	productos_id int NULL,\n" + "	ingredientes_id int NULL,\n" + "	cantidad numeric(3) NULL\n" + ");";
        PreparedStatement ps = conexion.prepareStatement(sql);
        ps.executeUpdate();

        try {
            sql = "INSERT INTO public.productos_compuestos(\n" + " id, productos_id, ingredientes_id, cantidad)\n" + "    VALUES (?, ?, ?, ?);";
            ps = conexion.prepareStatement(sql);
            ps.setLong(1, ingrediente.getProducto_compuesto_id());
            ps.setLong(2, bean.getId());
            ps.setLong(3, ingrediente.getId());
            ps.setFloat(4, ingrediente.getProducto_compuesto_cantidad());
            ps.executeUpdate();
        } catch (PSQLException s) {
            if (s.getMessage().contains("productos_compuestos_pk")) {
                try {
                    sql = "UPDATE public.productos_compuestos\n" + "   SET productos_id=?, ingredientes_id=?, cantidad=?\n" + " WHERE id=?";
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
    // esta funcion no esta en uso
    public void integrarProdcto(ProductoBean bean, ProductoBean ingrediente) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        try {
            String sql = "INSERT INTO public.productos_compuestos(\n" + "            id, productos_id, ingredientes_id, cantidad)\n" + "    VALUES (?, ?, ?, ?);";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, ingrediente.getId());
            ps.setLong(2, bean.getId());
            ps.setLong(3, ingrediente.getProducto_compuesto_id());
            ps.setFloat(4, ingrediente.getProducto_compuesto_cantidad());
            ps.executeUpdate();
        } catch (PSQLException s) {
            if (s.getMessage().contains("productos_compuestos_pk")) {
                try {
                    String sql = "UPDATE public.productos_compuestos\n" + "   SET productos_id=?, ingredientes_id=?, cantidad=?\n" + " WHERE id=?";
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

    public TreeMap<Long, CategoriaBean> getInventario() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        TreeMap<Long, CategoriaBean> lista = new TreeMap<>();

        try {
            String sql
                    = "SELECT G.ID GRUPO_ID, G.GRUPO GRUPO_DESC, P.ID, P.PLU, P.DESCRIPCION, SALDO\n"
                    + "FROM PRODUCTOS P\n"
                    + "LEFT JOIN GRUPOS_ENTIDAD GD ON GD.ENTIDAD_ID=P.ID\n"
                    + "LEFT JOIN GRUPOS G ON GD.GRUPO_ID=G.ID \n"
                    + "LEFT JOIN BODEGAS_PRODUCTOS BP ON BP.PRODUCTOS_ID=P.ID\n"
                    + "WHERE GRUPOS_TIPOS_ID=1 OR GRUPOS_TIPOS_ID IS NULL ORDER BY PLU ";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();

            while (re.next()) {
                long categoria = re.getLong("GRUPO_ID");

                if (lista.get(categoria) == null) {
                    ArrayList<ProductoBean> productos = new ArrayList<>();
                    CategoriaBean cat = new CategoriaBean(productos);
                    cat.setId(categoria);
                    if (re.getString("GRUPO_DESC") != null) {
                        cat.setGrupo(re.getString("GRUPO_DESC"));
                    } else {
                        cat.setGrupo(NovusConstante.SIN_GRUPO);
                    }
                    lista.put(categoria, cat);
                }
                ProductoBean productBean = new ProductoBean();
                productBean.setId(re.getLong("id"));
                productBean.setPlu(re.getString("plu"));
                productBean.setDescripcion(re.getString("descripcion"));
                productBean.setSaldo(re.getFloat("saldo"));
                lista.get(categoria).setTotales(lista.get(categoria).getTotales() + productBean.getSaldo());
                lista.get(categoria).getProductos().add(productBean);
            }
        } catch (PSQLException s) {
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }
        return lista;
    }
    // esta funcion no esta en uso
    public LinkedHashSet<ProductoBean> getSaldoBodedaExistentes() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        LinkedHashSet<ProductoBean> lista = new LinkedHashSet<>();

        try {
            String sql
                    = "select array_to_string(ARRAY( \n"
                    + "	SELECT  G.GRUPO GRUPO_DESC\n"
                    + "	FROM PRODUCTOS P\n"
                    + "	LEFT JOIN GRUPOS_ENTIDAD GD ON GD.ENTIDAD_ID=P.ID\n"
                    + "	LEFT JOIN GRUPOS G ON GD.GRUPO_ID=G.ID \n"
                    + "	WHERE P.ID=px.id\n"
                    + "), ', ') grupos, px.*, saldo FROM productos PX \n"
                    + "LEFT JOIN BODEGAS_PRODUCTOS BP ON BP.PRODUCTOS_ID=PX.ID\n"
                    + "order by NULLIF(PX.plu, '0')::int";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();

            while (re.next()) {
                if (re.getFloat("saldo") > 0) {
                    ProductoBean productBean = new ProductoBean();
                    productBean.setId(re.getLong("id"));
                    productBean.setPlu(re.getString("plu"));
                    productBean.setDescripcion(re.getString("descripcion"));
                    productBean.setSaldo(re.getFloat("saldo"));
                    productBean.setGrupos(re.getString("grupos"));
                    lista.add(productBean);
                }
            }
        } catch (PSQLException s) {
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }
        return lista;
    }

    public ConsecutivoBean getConsecutivo(String tipo_documento) throws DAOException {
        NovusUtils.printLn("Consultando Consecutivos Core");
        Connection conexion = Main.obtenerConexion("lazoexpresscore");

        ConsecutivoBean consAct = null;

        try {
            String sql = "select * from ct_consecutivos cc \n"
                    + "inner join equipos e on e.id = cc.equipos_id \n"
                    + "where cc.tipo_documento = ? \n"
                    + "AND cc.estado in('A', 'U') and cc.atributos->>'destino' = 'COM'\n"
                    + "order by cc.fecha_fin asc LIMIT 1;";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, tipo_documento);
            NovusUtils.printLn("Query: " + ps.toString());
            ResultSet re = ps.executeQuery();
            if (re.next()) {
                consAct = new ConsecutivoBean();
                consAct.setId(re.getLong("id"));

                consAct.setConsecutivo_inicial(re.getLong("consecutivo_inicial"));
                consAct.setConsecutivo_actual(re.getLong("consecutivo_actual"));
                consAct.setConsecutivo_final(re.getLong("consecutivo_final"));

                consAct.setEstado(re.getString("estado"));
                consAct.setResolucion(re.getString("resolucion"));

                consAct.setFecha_inicio(re.getDate("fecha_inicio"));
                consAct.setFecha_fin(re.getDate("fecha_fin"));

                consAct.setObservaciones(re.getString("observaciones"));
                consAct.setPrefijo(re.getString("prefijo"));
            }
        } catch (SQLException e) {
            NovusUtils.printLn(Main.ANSI_RED + "Error getConsecutivo(String tipo_documento) " + e.getMessage() + Main.ANSI_RESET);
        }
        return consAct;
    }

    public ConsecutivoBean getConsecutivoRegistry(int tipo_documento) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        ConsecutivoBean consAct = null;

        try {
            String sql = "select * from consecutivos cc \n"
                    + "inner join equipos e on e.id = cc.equipos_id \n"
                    + "where cc.tipo_documento = ? \n"
                    + "AND cc.estado in('A', 'U') and cc.cs_atributos->>'destino' = 'CAN' \n"
                    + "order by cc.fecha_fin asc LIMIT 1;";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, tipo_documento);
            NovusUtils.printLn("Query: " + ps.toString());
            ResultSet re = ps.executeQuery();
            if (re.next()) {
                consAct = new ConsecutivoBean();
                consAct.setId(re.getLong("id"));

                consAct.setConsecutivo_inicial(re.getLong("consecutivo_inicial"));
                consAct.setConsecutivo_actual(re.getLong("consecutivo_actual"));
                consAct.setConsecutivo_final(re.getLong("consecutivo_final"));

                consAct.setEstado(re.getString("estado"));
                consAct.setResolucion(re.getString("resolucion"));

                consAct.setFecha_inicio(re.getDate("fecha_inicio"));
                consAct.setFecha_fin(re.getDate("fecha_fin"));

                consAct.setObservaciones(re.getString("observaciones"));
                consAct.setPrefijo(re.getString("prefijo"));
            }
        } catch (SQLException e) {
            NovusUtils.printLn(Main.ANSI_RED + "Error getConsecutivoRegistry(int tipo_documento) " + e.getMessage() + Main.ANSI_RESET);
        }
        return consAct;
    }
    // esta funcion no esta en uso
    public ConsecutivoBean getConsecutivoCanKco(int tipoDocumento, String negocio) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        ConsecutivoBean consAct = null;
        String tipoNegocio = "CAN";
        if (negocio.equals("K")) {
            tipoNegocio = "KSC";
        }
        String sql = "select * from consecutivos c inner join equipos e on "
                .concat(" e.id = c.equipos_id where c.tipo_documento = ?")
                .concat(" and c.estado in('A', 'U') and c.cs_atributos::json->>'destino' = ?")
                .concat("order by c.fecha_fin asc limit 1;");
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setLong(1, tipoDocumento);
            ps.setString(2, tipoNegocio);
            ResultSet re = ps.executeQuery();
            if (re.next()) {
                consAct = new ConsecutivoBean();
                consAct.setId(re.getLong("id"));

                consAct.setConsecutivo_inicial(re.getLong("consecutivo_inicial"));
                consAct.setConsecutivo_actual(re.getLong("consecutivo_actual"));
                consAct.setConsecutivo_final(re.getLong("consecutivo_final"));

                consAct.setEstado(re.getString("estado"));
                consAct.setResolucion(re.getString("resolucion"));

                consAct.setFecha_inicio(re.getDate("fecha_inicio"));
                consAct.setFecha_fin(re.getDate("fecha_fin"));

                consAct.setObservaciones(re.getString("observaciones"));
                consAct.setPrefijo(re.getString("prefijo"));
            }
        } catch (SQLException e) {
        }
        return consAct;
    }

        /*
    public String getObservaciones(long consecutivo_id) {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        String observaciones = "";
        try {
            String sql = "select observaciones from consecutivos where id = ? limit 1";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, consecutivo_id);
            ResultSet re = ps.executeQuery();
            if (re.next()) {
                observaciones = re.getString(1);
            }
        } catch (SQLException e) {
            NovusUtils.printLn(e.getMessage());
        }
        return observaciones;
    }

         */

    public ConsecutivoBean getConsecutivo(int TIPO_FACTURACION) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        ConsecutivoBean consAct = null;
        ArrayList<ConsecutivoBean> consecutivos = new ArrayList<>();

        try {
            String sql = "select * from consecutivos where estado in ('A', 'U') and tipo_documento=? order by id";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, TIPO_FACTURACION);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                ConsecutivoBean cs = new ConsecutivoBean();
                cs.setId(re.getLong("id"));
                cs.setConsecutivo_inicial(re.getLong("consecutivo_inicial"));
                cs.setConsecutivo_actual(re.getLong("consecutivo_actual"));
                cs.setConsecutivo_final(re.getLong("consecutivo_final"));
                cs.setEstado(re.getString("estado"));
                cs.setResolucion(re.getString("resolucion"));

                cs.setFecha_inicio(re.getDate("fecha_inicio"));
                cs.setFecha_fin(re.getDate("fecha_fin"));

                cs.setObservaciones(re.getString("observaciones"));
                cs.setPrefijo(re.getString("prefijo"));
                consecutivos.add(cs);
            }
        } catch (PSQLException s) {
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }

        if (!consecutivos.isEmpty()) {
            // SI SOLO HAY UN CONSECUTIVO
            if (consecutivos.size() == 1) {
                // SI EL CONSECUTIVO ESTA EN USO
                if (consecutivos.get(0).getEstado().equals(CONSECUTIVO_ESTADO_USO) || consecutivos.get(0).getEstado().equals(CONSECUTIVO_ESTADO_ACTIVO)) {
                    // SI ES MENOR O IGUAL AL FINAL LO PUEDE USAR
                    if (consecutivos.get(0).getConsecutivo_actual() <= consecutivos.get(0).getConsecutivo_final()) {
                        consAct = consecutivos.get(0);
                    } else {
                        actualizaEstadoConsecutivo(consecutivos.get(0), CONSECUTIVO_ESTADO_VENCIDO);
                    }
                }
            } else {
                // SI HAY VARIOS, CONSIGUE EL CONSECUTIVO EN USO
                for (ConsecutivoBean cons : consecutivos) {
                    if (cons.getEstado().equals(CONSECUTIVO_ESTADO_USO)) {
                        // SI ES MENOR O IGUAL AL FINAL LO PUEDE USAR
                        if (cons.getConsecutivo_actual() <= cons.getConsecutivo_final()) {
                            consAct = cons;
                            break;
                        } else {
                            actualizaEstadoConsecutivo(cons, CONSECUTIVO_ESTADO_VENCIDO);
                        }
                    }
                }
                // NO HAY NINGUNO EN USO
                if (consAct == null) {
                    for (ConsecutivoBean cons : consecutivos) {
                        // BUSCAMOS EL CONSECUTIVO DISPONIBLE
                        if (cons.getConsecutivo_actual() <= cons.getConsecutivo_final()) {
                            consAct = cons;
                            break;
                        } else {
                            actualizaEstadoConsecutivo(cons, CONSECUTIVO_ESTADO_VENCIDO);
                        }
                    }

                    // SOLO AVISA UNA VEZ AL SISTEMA QUE CONSECUTIVO USO
                    if (consAct != null) {
                        actualizaEstadoConsecutivo(consAct, CONSECUTIVO_ESTADO_USO);
                    }
                }
            }
        }

        if (consAct != null) {
            if (consAct.getConsecutivo_actual() < consAct.getConsecutivo_final()) {
                try {
                    String sql = "select * from consecutivos where estado in ('A', 'U') order by id";
                    PreparedStatement ps = conexion.prepareStatement(sql);
                    ResultSet re = ps.executeQuery();
                    while (re.next()) {
                        ConsecutivoBean cs = new ConsecutivoBean();
                        cs.setId(re.getLong("id"));
                        cs.setConsecutivo_inicial(re.getLong("consecutivo_inicial"));
                        cs.setConsecutivo_actual(re.getLong("consecutivo_actual"));
                        cs.setConsecutivo_final(re.getLong("consecutivo_final"));
                        cs.setEstado(re.getString("estado"));
                        cs.setResolucion(re.getString("resolucion"));
                        cs.setObservaciones(re.getString("observaciones"));
                        cs.setPrefijo(re.getString("prefijo"));
                        consecutivos.add(cs);
                    }
                } catch (PSQLException s) {
                    throw new DAOException(s.getMessage());
                } catch (SQLException s) {
                    throw new DAOException(s.getMessage());
                } catch (Exception s) {
                    throw new DAOException(s.getMessage());
                }
            }
        }
        return consAct;
    }
     // esta funcion no esta en uso
    public LinkedHashSet<ProductoBean> getSaldoBodeda() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        LinkedHashSet<ProductoBean> lista = new LinkedHashSet<>();

        try {
            String sql
                    = "select array_to_string(ARRAY( \n"
                    + "	SELECT  G.GRUPO GRUPO_DESC\n"
                    + "	FROM PRODUCTOS P\n"
                    + "	LEFT JOIN GRUPOS_ENTIDAD GD ON GD.ENTIDAD_ID=P.ID\n"
                    + "	LEFT JOIN GRUPOS G ON GD.GRUPO_ID=G.ID \n"
                    + "	WHERE P.ID=px.id\n"
                    + "), ', ') grupos, px.*, saldo FROM productos PX \n"
                    + "LEFT JOIN BODEGAS_PRODUCTOS BP ON BP.PRODUCTOS_ID=PX.ID\n"
                    + "order by NULLIF(PX.plu, '0')::int";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();

            while (re.next()) {
                ProductoBean productBean = new ProductoBean();
                productBean.setId(re.getLong("id"));
                productBean.setPlu(re.getString("plu"));
                productBean.setDescripcion(re.getString("descripcion"));
                productBean.setSaldo(re.getFloat("saldo"));
                productBean.setGrupos(re.getString("grupos"));
                lista.add(productBean);
            }
        } catch (PSQLException s) {
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }
        return lista;
    }
    // esta funcion no esta en uso
    public ReporteJornadaBean getLastCierreByPersona(PersonaBean promotor) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        ReporteJornadaBean reporteBean = null;

        TreeMap<Long, ProductoBean> productos = new TreeMap<>();

        try {
            String sql = "select * from jornadas_hist where personas_id=? order by id desc limit 1";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, promotor.getId());
            ResultSet re = ps.executeQuery();

            while (re.next()) {
                reporteBean = new ReporteJornadaBean();
                reporteBean.setId(re.getLong("id"));
                reporteBean.setJornadaId(re.getLong("jornada_id"));
                reporteBean.setInicio(re.getTimestamp("fecha_inicio"));
                reporteBean.setFin(re.getTimestamp("fecha_fin"));
            }

            if (reporteBean != null) {
                sql = "select impreso \n" + "from movimientos m\n" + "where \n" + "persona_id=? and m.fecha>=? and m.fecha<=?";
                ps = conexion.prepareStatement(sql);
                ps.setLong(1, promotor.getId());
                ps.setTimestamp(2, new Timestamp(reporteBean.getInicio().getTime()));
                ps.setTimestamp(3, new Timestamp(reporteBean.getFin().getTime()));
                re = ps.executeQuery();

                while (re.next()) {
                    if (re.getString("impreso").equals("N")) {
                        reporteBean.setImpresos(reporteBean.getImpresos() + 1);
                    } else {
                        reporteBean.setReimpresos(reporteBean.getReimpresos() + 1);
                    }
                    reporteBean.setNumeroVentas(reporteBean.getNumeroVentas() + 1);
                }

                sql
                        = "select md.productos_id, p.descripcion, md.cantidad,  ( md.cantidad * md.precio ) saldo\n"
                        + "from movimientos m\n"
                        + "inner join movimientos_detalles md on md.movimientos_id=m.id\n"
                        + "inner join productos p on p.id=md.productos_id\n"
                        + "where \n"
                        + "persona_id=? and md.fecha>=? and md.fecha<=? ";
                ps = conexion.prepareStatement(sql);
                ps.setLong(1, promotor.getId());
                ps.setTimestamp(2, new Timestamp(reporteBean.getInicio().getTime()));
                ps.setTimestamp(3, new Timestamp(reporteBean.getFin().getTime()));
                re = ps.executeQuery();

                while (re.next()) {
                    ProductoBean product = new ProductoBean();
                    product.setId(re.getLong("productos_id"));
                    product.setDescripcion(re.getString("descripcion"));
                    product.setCantidad(re.getFloat("cantidad"));
                    product.setSaldo(re.getFloat("saldo"));

                    if (productos.get(product.getId()) == null) {
                        productos.put(product.getId(), product);
                    } else {
                        ProductoBean temp = productos.get(product.getId());
                        temp.setCantidad(temp.getCantidad() + product.getCantidad());
                        temp.setSaldo(temp.getSaldo() + product.getSaldo());
                        productos.put(product.getId(), temp);
                    }

                    ArrayList<ProductoBean> ventas = new ArrayList<>();
                    for (Map.Entry<Long, ProductoBean> entry : productos.entrySet()) {
                        ProductoBean value = entry.getValue();
                        ventas.add(value);
                    }
                    reporteBean.setVentas(ventas);
                }
            }
        } catch (PSQLException s) {
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }
        return reporteBean;
    }

    public boolean crearMediosPagosFacturaElectronica(MovimientosBean movimiento) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");

        boolean inserted = false;
        try {
            String sql;
            PreparedStatement ps;
            sql = "DELETE FROM public.ventas_medios_pagos WHERE ventas_id=?";
            ps = conexion.prepareStatement(sql);
            ps.setLong(1, movimiento.getId());
            ps.executeUpdate();

            for (Map.Entry<Long, MediosPagosBean> entry : movimiento.getMediosPagos().entrySet()) {
                MediosPagosBean value = entry.getValue();
                sql
                        = "INSERT INTO public.ventas_medios_pagos " + "(ventas_id, medios_pagos_id, md_fecha, valor_recibido, valor_cambio, valor_total, numero_comprobante) " + "	VALUES(?, ? , now(), ?, ?, ?, ?); ";
                ps = conexion.prepareStatement(sql);
                ps.setLong(1, movimiento.getId());
                ps.setLong(2, value.getId());
                ps.setFloat(3, value.getRecibido());
                ps.setFloat(4, value.getCambio());
                ps.setFloat(5, value.getValor());
                ps.setString(6, value.getVoucher());
                ps.executeUpdate();
            }

            sql = "UPDATE ventas SET atributos = ?::json WHERE id=?";
            ps = conexion.prepareStatement(sql);
            JsonObject atributosJson = new JsonObject();
            atributosJson.addProperty("vehiculo_placa", "");
            atributosJson.addProperty("solicitudMediosPagos", true);
            ps.setString(1, atributosJson.toString());
            ps.setLong(2, movimiento.getId());
            ps.executeUpdate();
            inserted = true;
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
        }
        return inserted;
    }

    public boolean createVentasCombustible(MovimientosBean movimiento) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");

        boolean inserted = false;

        try {
            String sql
                    = "INSERT INTO public.ventas \n"
                    + "(id, jornada_id, tipo_origen_id, origen_id, surtidor, cara, manguera, grado,\n"
                    + "operario_id, cliente_id, fecha_inicio, fecha_fin, total, impuesto, sincronizado,\n"
                    + "placa, impresion, token_process_id, medios_pagos_id, voucher, atributos) \n"
                    + "VALUES (nextval('ventas_id'), ?, 2, -1, -1, -1, -1, -1, ?, 2, ?, ?, ?, 0, 0, '', 'N', NULL, 1, NULL, NULL) RETURNING ID;";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, movimiento.getGrupoJornadaId());
            ps.setLong(2, Main.persona.getId());
            ps.setTimestamp(3, new Timestamp(new Date().getTime()));
            ps.setTimestamp(4, new Timestamp(new Date().getTime()));
            ps.setFloat(5, movimiento.getVentaTotal());
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                movimiento.setId(re.getLong("ID"));
                movimiento.setRemotoId(re.getLong("ID"));
            }
            if (movimiento.getId() != 0) {
                for (Map.Entry<Long, MovimientosDetallesBean> entry : movimiento.getDetalles().entrySet()) {
                    Long key = entry.getKey();
                    MovimientosDetallesBean value = entry.getValue();

                    try {
                        sql
                                = "INSERT INTO public.ventas_detalles "
                                + "(ventas_id, id, productos_id, cantidad, precio, total, sincronizado, cantidad_precisa, "
                                + "acum_vol_inicial, acum_vol_final, acum_ven_inicial, acum_ven_final, cantidad_factor) "
                                + "VALUES (?, (SELECT coalesce(MAX(id),0) + 1 FROM ventas_detalles), ?, ?, ?, ?, 0, ?, 0, 0, 0, 0, NULL);";
                        ps = conexion.prepareStatement(sql);
                        ps.setLong(1, movimiento.getId());
                        ps.setLong(2, value.getProductoId());
                        ps.setFloat(3, value.getCantidadUnidad());
                        ps.setFloat(4, value.getPrecio());
                        ps.setFloat(5, value.getPrecio() * value.getCantidadUnidad());
                        ps.setFloat(6, value.getCantidadUnidad());
                        ps.executeUpdate();
                    } catch (Exception e) {
                        NovusUtils.printLn(e.getMessage());
                    }
                }

                for (Map.Entry<Long, MediosPagosBean> entry : movimiento.getMediosPagos().entrySet()) {
                    Long key = entry.getKey();
                    MediosPagosBean value = entry.getValue();
                    sql
                            = "INSERT INTO public.ventas_medios_pagos " + "(ventas_id, medios_pagos_id, md_fecha, valor_recibido, valor_cambio, valor_total, numero_comprobante) " + "	VALUES(?, ? , now(), ?, ?, ?, ?); ";
                    ps = conexion.prepareStatement(sql);
                    ps.setLong(1, movimiento.getId());
                    ps.setLong(2, value.getId());
                    ps.setFloat(3, value.getRecibido());
                    ps.setFloat(4, value.getCambio());
                    ps.setFloat(5, value.getValor());
                    ps.setString(6, value.getVoucher());
                    ps.executeUpdate();
                }
            }
        } catch (Exception e) {
            NovusUtils.printLn(e.getMessage());
        }
        return inserted;
    }
    // esta funcion no esta en uso
    public ConsecutivoBean getActualizarConsecutivoFE(int ID, int CONSECUTIVO) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        ConsecutivoBean consAct = null;

        try {
            String sql = "update consecutivos set consecutivo_actual=? where id=?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, CONSECUTIVO);
            ps.setInt(2, ID);
            //ps.executeUpdate();
            int n = ps.executeUpdate();
            if (n > 0) {
                NovusUtils.printLn(Main.ANSI_YELLOW + "Se ha actualizado el consecutivo actual " + Main.ANSI_RESET);
            } else {
                NovusUtils.printLn(Main.ANSI_YELLOW + "Error al actulizar el consecutivo actual " + Main.ANSI_RESET);
            }
        } catch (PSQLException s) {
            NovusUtils.printLn(Main.ANSI_YELLOW + " " + s + " " + Main.ANSI_RESET);
        } catch (SQLException s) {
            NovusUtils.printLn(Main.ANSI_YELLOW + " " + s + " " + Main.ANSI_RESET);
        }

        return consAct;
    }
    // esta funcion no esta en uso
    public ReporteJornadaBean getVentasByPersona(PersonaBean promotor) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        ReporteJornadaBean reporteBean = null;
        TreeMap<Long, ProductoBean> productos = new TreeMap<>();

        try {
            String sql = "select * from jornadas where personas_id=? order by id desc limit 1";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, promotor.getId());
            ResultSet re = ps.executeQuery();

            while (re.next()) {
                reporteBean = new ReporteJornadaBean();
                reporteBean.setId(re.getLong("id"));
                reporteBean.setJornadaId(re.getLong("id"));
                reporteBean.setInicio(re.getTimestamp("fecha_inicio"));
                reporteBean.setFin(re.getTimestamp("fecha_fin"));
            }

            if (reporteBean != null) {
                sql
                        = "select impreso, md.productos_id, p.descripcion, md.cantidad,  ( md.cantidad * md.precio ) saldo\n"
                        + "from movimientos m\n"
                        + "inner join movimientos_detalles md on md.movimientos_id=m.id\n"
                        + "inner join productos p on p.id=md.productos_id\n"
                        + "where \n"
                        + "persona_id=? and md.fecha>=? ";
                ps = conexion.prepareStatement(sql);
                ps.setLong(1, promotor.getId());
                ps.setTimestamp(2, new Timestamp(reporteBean.getInicio().getTime()));
                re = ps.executeQuery();

                while (re.next()) {
                    ProductoBean product = new ProductoBean();
                    product.setId(re.getLong("productos_id"));
                    product.setDescripcion(re.getString("descripcion"));
                    product.setCantidad(re.getFloat("cantidad"));
                    product.setSaldo(re.getFloat("saldo"));

                    if (re.getString("impreso").equals("N")) {
                        reporteBean.setImpresos(reporteBean.getImpresos() + 1);
                    } else {
                        reporteBean.setReimpresos(reporteBean.getReimpresos() + 1);
                    }
                    reporteBean.setNumeroVentas(reporteBean.getNumeroVentas() + 1);

                    if (productos.get(product.getId()) == null) {
                        productos.put(product.getId(), product);
                    } else {
                        ProductoBean temp = productos.get(product.getId());
                        temp.setCantidad(temp.getCantidad() + product.getCantidad());
                        temp.setSaldo(temp.getSaldo() + product.getSaldo());
                        productos.put(product.getId(), temp);
                    }

                    ArrayList<ProductoBean> ventas = new ArrayList<>();
                    for (Map.Entry<Long, ProductoBean> entry : productos.entrySet()) {
                        ProductoBean value = entry.getValue();
                        ventas.add(value);
                    }
                    reporteBean.setVentas(ventas);
                }
            }
        } catch (PSQLException s) {
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }
        return reporteBean;
    }
    // esta funcion no esta en uso
    public boolean limpiarIngredientes(long productoId) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

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
    // esta funcion no esta en uso
    public ArrayList<MovimientosBean> findMovimientos() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        ArrayList<MovimientosBean> lista = new ArrayList<>();

        try {
            String sql = "SELECT * FROM MOVIMIENTOS WHERE FECHA>=CURRENT_DATE - 7 ORDER BY ID DESC";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();

            while (re.next()) {
                MovimientosBean movimiento = new MovimientosBean();
                movimiento.setId(re.getLong("id"));
                movimiento.setFecha(re.getTimestamp("fecha"));
                movimiento.setConsecutivo(new ConsecutivoBean());
                movimiento.getConsecutivo().setConsecutivo_actual(re.getLong("consecutivo"));
                movimiento.getConsecutivo().setId(re.getLong("consecutivo_id"));
                movimiento.getConsecutivo().setFormato(re.getString("consecutivo_observacion"));
                movimiento.getConsecutivo().setPrefijo(re.getString("consecutivo_prefijo"));
                movimiento.setPersonaId(re.getLong("persona_id"));
                movimiento.setPersonaNit(re.getString("persona_nit"));
                movimiento.setPersonaNombre(re.getString("persona_nombre"));

                movimiento.setClienteId(re.getLong("tercero_id"));
                movimiento.setClienteNit(re.getString("tercero_nit"));
                movimiento.setClienteNombre(re.getString("tercero_nombre"));

                movimiento.setCostoTotal(re.getFloat("costo_total"));
                movimiento.setVentaTotal(re.getFloat("venta_total"));
                movimiento.setImpuestoTotal(re.getFloat("impuesto_total"));
                movimiento.setDescuentoTotal(re.getFloat("descuento_total"));

                movimiento.setOrigenId(re.getLong("origen_id"));
                movimiento.setImpreso(re.getString("impreso"));

                movimiento.setCreateUser(re.getLong("create_user"));
                movimiento.setCreateDate(re.getTimestamp("create_date"));
                movimiento.setUpdateUser(re.getLong("update_user"));
                movimiento.setUpdateDate(re.getTimestamp("update_date"));

                movimiento.setRemotoId(re.getLong("remoto_id"));
                movimiento.setSincronizado(re.getInt("sincronizado"));

                lista.add(movimiento);
            }
        } catch (PSQLException s) {
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }
        return lista;
    }

    /*
     * public boolean hayConsecutivoCombustible() throws DAOException {
     * boolean hayConsecutivoCombustible = false;
     * if(dbCore.conectar()){
     * String sql =
     * "select * from consecutivos where estado in ('A', 'U') order by id";
     * PreparedStatement ps = conexion.prepareStatement(sql);
     * ResultSet re = ps.executeQuery();
     * }
     * return hayConsecutivoCombustible;
     * }
     */
    public ConsecutivoBean getConsecutivoMarket(boolean isFe) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        ConsecutivoBean consAct = null;

        System.out.println("TIPO NEGOCIO: " + Main.TIPO_NEGOCIO);

        ArrayList<ConsecutivoBean> consecutivos = new ArrayList<>();
        LocalDate todaysDate = LocalDate.now();
        String sql
                = "select\n".concat(" *,")
                .concat(" extract(epoch")
                .concat(" from")
                .concat(" (c.fecha_fin - '" + todaysDate + "'))/ 3600 / 24::int as dias")
                .concat(" from")
                .concat(" consecutivos c")
                .concat(" where")
                .concat(" estado in ('A', 'U')")
                .concat(" and tipo_documento=? and c.cs_atributos::json->>'destino' = ? ")
                .concat(" order by fecha_fin asc LIMIT 1");
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            if (isFe) {
                ps.setInt(1, 31);
            } else {
                ps.setInt(1, 35);
            }
            if (Main.TIPO_NEGOCIO.equals(NovusConstante.PARAMETER_CDL)) {
                ps.setString(2, "CDL");
            } else {
                ps.setString(2, "KSC");
            }
            NovusUtils.printLn("[getConsecutivoKIOSCO]" + ps.toString());
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                ConsecutivoBean cs = new ConsecutivoBean();
                cs.setId(re.getLong("id"));
                cs.setConsecutivo_inicial(re.getLong("consecutivo_inicial"));
                cs.setConsecutivo_actual(re.getLong("consecutivo_actual"));
                cs.setConsecutivo_final(re.getLong("consecutivo_final"));
                cs.setEstado(re.getString("estado"));
                cs.setResolucion(re.getString("resolucion"));
                cs.setObservaciones(re.getString("observaciones"));
                cs.setPrefijo(re.getString("prefijo"));
                cs.setDias(re.getInt("dias"));
                consecutivos.add(cs);
            }
        } catch (PSQLException s) {
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }

        if (!consecutivos.isEmpty()) {
            // SI SOLO HAY UN CONSECUTIVO
            if (consecutivos.size() == 1) {
                // SI EL CONSECUTIVO ESTA EN USO
                if (consecutivos.get(0).getEstado().equals(NovusConstante.CONSECUTIVO_ESTADO_USO) || consecutivos.get(0).getEstado().equals(NovusConstante.CONSECUTIVO_ESTADO_ACTIVO)) {
                    // SI ES MENOR O IGUAL AL FINAL LO PUEDE USAR
                    if (consecutivos.get(0).getConsecutivo_actual() <= consecutivos.get(0).getConsecutivo_final() && consecutivos.get(0).getDias() >= 1) {
                        consAct = consecutivos.get(0);
                    } else {
                        actualizaEstadoConsecutivo(consecutivos.get(0), NovusConstante.CONSECUTIVO_ESTADO_VENCIDO);
                    }
                }
            } else {
                // SI HAY VARIOS, CONSIGUE EL CONSECUTIVO EN USO
                for (ConsecutivoBean cons : consecutivos) {
                    if (cons.getEstado().equals(NovusConstante.CONSECUTIVO_ESTADO_USO)) {
                        // SI ES MENOR O IGUAL AL FINAL LO PUEDE USAR
                        if (cons.getConsecutivo_actual() <= cons.getConsecutivo_final() && cons.getDias() >= 1) {
                            consAct = cons;
                            break;
                        } else {
                            actualizaEstadoConsecutivo(cons, NovusConstante.CONSECUTIVO_ESTADO_VENCIDO);
                        }
                    }
                }
                // NO HAY NINGUNO EN USO
                if (consAct == null) {
                    for (ConsecutivoBean cons : consecutivos) {
                        // BUSCAMOS EL CONSECUTIVO DISPONIBLE
                        if (cons.getConsecutivo_actual() <= cons.getConsecutivo_final() && cons.getDias() >= 1) {
                            consAct = cons;
                            break;
                        } else {
                            actualizaEstadoConsecutivo(cons, NovusConstante.CONSECUTIVO_ESTADO_VENCIDO);
                        }
                    }

                    // SOLO AVISA UNA VEZ AL SISTEMA QUE CONSECUTIVO USO
                    if (consAct != null) {
                        actualizaEstadoConsecutivo(consAct, NovusConstante.CONSECUTIVO_ESTADO_USO);
                    }
                }
            }
        }

        if (consAct != null && consAct.getConsecutivo_actual() < consAct.getConsecutivo_final()) {
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                if (isFe) {
                    ps.setInt(1, 31);
                } else {
                    ps.setInt(1, 35);
                }
                if (Main.TIPO_NEGOCIO.equals(NovusConstante.PARAMETER_CDL)) {
                    ps.setString(2, "CDL");
                } else {
                    ps.setString(2, "KSC");
                }
                ResultSet re = ps.executeQuery();
                while (re.next()) {
                    ConsecutivoBean cs = new ConsecutivoBean();
                    cs.setId(re.getLong("id"));
                    cs.setConsecutivo_inicial(re.getLong("consecutivo_inicial"));
                    cs.setConsecutivo_actual(re.getLong("consecutivo_actual"));
                    cs.setConsecutivo_final(re.getLong("consecutivo_final"));
                    cs.setEstado(re.getString("estado"));
                    cs.setResolucion(re.getString("resolucion"));
                    cs.setObservaciones(re.getString("observaciones"));
                    cs.setPrefijo(re.getString("prefijo"));
                    consecutivos.add(cs);
                }
            } catch (PSQLException s) {
                throw new DAOException(s.getMessage());
            } catch (SQLException s) {
                throw new DAOException(s.getMessage());
            }
        }
        return consAct;
    }
    // esta funcion no esta en uso
    public ConsecutivoBean getConsecutivoContingencia() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");

        ConsecutivoBean consAct = null;
        LocalDate todaysDate = LocalDate.now();
        ArrayList<ConsecutivoBean> consecutivos = new ArrayList<>();
        String sql
                = "select\n"
                + " *,\n"
                + " extract(epoch\n"
                + " from\n"
                + " (c.fecha_fin - now()))/ 3600 / 24::int as dias\n"
                + " from\n"
                + " ct_consecutivos c\n"
                + " where\n"
                + " estado in ('A', 'U')\n"
                + " and tipo_documento='018'\n"
                + " order by fecha_fin asc LIMIT 1;";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                ConsecutivoBean cs = new ConsecutivoBean();
                cs.setId(re.getLong("id"));
                cs.setConsecutivo_inicial(re.getLong("consecutivo_inicial"));
                cs.setConsecutivo_actual(re.getLong("consecutivo_actual"));
                cs.setConsecutivo_final(re.getLong("consecutivo_final"));
                cs.setEstado(re.getString("estado"));
                cs.setResolucion(re.getString("resolucion"));
                cs.setObservaciones(re.getString("observaciones"));
                cs.setPrefijo(re.getString("prefijo"));
                cs.setDias(re.getInt("dias"));
                consecutivos.add(cs);
            }
        } catch (PSQLException s) {
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }

        if (!consecutivos.isEmpty()) {
            // SI SOLO HAY UN CONSECUTIVO
            if (consecutivos.size() == 1) {
                // SI EL CONSECUTIVO ESTA EN USO
                if (consecutivos.get(0).getEstado().equals(NovusConstante.CONSECUTIVO_ESTADO_USO) || consecutivos.get(0).getEstado().equals(NovusConstante.CONSECUTIVO_ESTADO_ACTIVO)) {
                    // SI ES MENOR O IGUAL AL FINAL LO PUEDE USAR
                    if (consecutivos.get(0).getConsecutivo_actual() <= consecutivos.get(0).getConsecutivo_final() && consecutivos.get(0).getDias() >= 1) {
                        consAct = consecutivos.get(0);
                    } else {
                        actualizaEstadoConsecutivo(consecutivos.get(0), NovusConstante.CONSECUTIVO_ESTADO_VENCIDO);
                    }
                }
            } else {
                // SI HAY VARIOS, CONSIGUE EL CONSECUTIVO EN USO
                for (ConsecutivoBean cons : consecutivos) {
                    if (cons.getEstado().equals(NovusConstante.CONSECUTIVO_ESTADO_USO)) {
                        // SI ES MENOR O IGUAL AL FINAL LO PUEDE USAR
                        if (cons.getConsecutivo_actual() <= cons.getConsecutivo_final() && cons.getDias() >= 1) {
                            consAct = cons;
                            break;
                        } else {
                            actualizaEstadoConsecutivo(cons, NovusConstante.CONSECUTIVO_ESTADO_VENCIDO);
                        }
                    }
                }
                // NO HAY NINGUNO EN USO
                if (consAct == null) {
                    for (ConsecutivoBean cons : consecutivos) {
                        // BUSCAMOS EL CONSECUTIVO DISPONIBLE
                        if (cons.getConsecutivo_actual() <= cons.getConsecutivo_final() && cons.getDias() >= 1) {
                            consAct = cons;
                            break;
                        } else {
                            actualizaEstadoConsecutivo(cons, NovusConstante.CONSECUTIVO_ESTADO_VENCIDO);
                        }
                    }

                    // SOLO AVISA UNA VEZ AL SISTEMA QUE CONSECUTIVO USO
                    if (consAct != null) {
                        actualizaEstadoConsecutivo(consAct, NovusConstante.CONSECUTIVO_ESTADO_USO);
                    }
                }
            }
        }

        if (consAct != null && consAct.getConsecutivo_actual() < consAct.getConsecutivo_final()) {
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ResultSet re = ps.executeQuery();
                while (re.next()) {
                    ConsecutivoBean cs = new ConsecutivoBean();
                    cs.setId(re.getLong("id"));
                    cs.setConsecutivo_inicial(re.getLong("consecutivo_inicial"));
                    cs.setConsecutivo_actual(re.getLong("consecutivo_actual"));
                    cs.setConsecutivo_final(re.getLong("consecutivo_final"));
                    cs.setEstado(re.getString("estado"));
                    cs.setResolucion(re.getString("resolucion"));
                    cs.setObservaciones(re.getString("observaciones"));
                    cs.setPrefijo(re.getString("prefijo"));
                    consecutivos.add(cs);
                }
            } catch (PSQLException s) {
                throw new DAOException(s.getMessage());
            } catch (SQLException s) {
                throw new DAOException(s.getMessage());
            }
        }
        return consAct;
    }


    public ConsecutivoBean getConsecutivo(boolean isFe) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        ConsecutivoBean consAct = null;
        LocalDate todaysDate = LocalDate.now();
        ArrayList<ConsecutivoBean> consecutivos = new ArrayList<>();
        String sql
                = "select\n".concat(" *,")
                .concat(" extract(epoch")
                .concat(" from")
                .concat(" (c.fecha_fin - '" + todaysDate + "'))/ 3600 / 24::int as dias")
                .concat(" from")
                .concat(" consecutivos c")
                .concat(" where")
                .concat(" estado in ('A', 'U')")
                .concat(" and tipo_documento = ? and c.cs_atributos::json->>'destino' = ?")
                .concat(" and equipos_id = ?")  // 
                .concat(" order by fecha_fin asc LIMIT 1");
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            if (isFe) {
                ps.setInt(1, 31);
            } else {
                ps.setInt(1, 9);
            }
            ps.setString(2, "CAN");
            ps.setLong(3, com.firefuel.Main.credencial.getEquipos_id());  //  AGREGADO: equipos_id
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                ConsecutivoBean cs = new ConsecutivoBean();
                cs.setId(re.getLong("id"));
                cs.setConsecutivo_inicial(re.getLong("consecutivo_inicial"));
                cs.setConsecutivo_actual(re.getLong("consecutivo_actual"));
                cs.setConsecutivo_final(re.getLong("consecutivo_final"));
                cs.setEstado(re.getString("estado"));
                cs.setResolucion(re.getString("resolucion"));
                cs.setObservaciones(re.getString("observaciones"));
                cs.setPrefijo(re.getString("prefijo"));
                cs.setDias(re.getInt("dias"));
                consecutivos.add(cs);
            }
        } catch (PSQLException s) {
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }

        if (!consecutivos.isEmpty()) {
            // SI SOLO HAY UN CONSECUTIVO
            if (consecutivos.size() == 1) {
                // SI EL CONSECUTIVO ESTA EN USO
                if (consecutivos.get(0).getEstado().equals(NovusConstante.CONSECUTIVO_ESTADO_USO) || consecutivos.get(0).getEstado().equals(NovusConstante.CONSECUTIVO_ESTADO_ACTIVO)) {
                    // SI ES MENOR O IGUAL AL FINAL LO PUEDE USAR
                    if (consecutivos.get(0).getConsecutivo_actual() <= consecutivos.get(0).getConsecutivo_final() && consecutivos.get(0).getDias() >= 1) {
                        consAct = consecutivos.get(0);
                    } else {
                        actualizaEstadoConsecutivo(consecutivos.get(0), NovusConstante.CONSECUTIVO_ESTADO_VENCIDO);
                    }
                }
            } else {
                // SI HAY VARIOS, CONSIGUE EL CONSECUTIVO EN USO
                for (ConsecutivoBean cons : consecutivos) {
                    if (cons.getEstado().equals(NovusConstante.CONSECUTIVO_ESTADO_USO)) {
                        // SI ES MENOR O IGUAL AL FINAL LO PUEDE USAR
                        if (cons.getConsecutivo_actual() <= cons.getConsecutivo_final() && cons.getDias() >= 1) {
                            consAct = cons;
                            break;
                        } else {
                            actualizaEstadoConsecutivo(cons, NovusConstante.CONSECUTIVO_ESTADO_VENCIDO);
                        }
                    }
                }
                // NO HAY NINGUNO EN USO
                if (consAct == null) {
                    for (ConsecutivoBean cons : consecutivos) {
                        // BUSCAMOS EL CONSECUTIVO DISPONIBLE
                        if (cons.getConsecutivo_actual() <= cons.getConsecutivo_final() && cons.getDias() >= 1) {
                            consAct = cons;
                            break;
                        } else {
                            actualizaEstadoConsecutivo(cons, NovusConstante.CONSECUTIVO_ESTADO_VENCIDO);
                        }
                    }

                    // SOLO AVISA UNA VEZ AL SISTEMA QUE CONSECUTIVO USO
                    if (consAct != null) {
                        actualizaEstadoConsecutivo(consAct, NovusConstante.CONSECUTIVO_ESTADO_USO);
                    }
                }
            }
        }

        if (consAct != null && consAct.getConsecutivo_actual() < consAct.getConsecutivo_final()) {
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                if (isFe) {
                    ps.setInt(1, 31);
                } else {
                    ps.setInt(1, 9);
                }
                ps.setString(2, "CAN");
                ps.setLong(3, com.firefuel.Main.credencial.getEquipos_id());  // AGREGADO: equipos_id
                ResultSet re = ps.executeQuery();
                while (re.next()) {
                    ConsecutivoBean cs = new ConsecutivoBean();
                    cs.setId(re.getLong("id"));
                    cs.setConsecutivo_inicial(re.getLong("consecutivo_inicial"));
                    cs.setConsecutivo_actual(re.getLong("consecutivo_actual"));
                    cs.setConsecutivo_final(re.getLong("consecutivo_final"));
                    cs.setEstado(re.getString("estado"));
                    cs.setResolucion(re.getString("resolucion"));
                    cs.setObservaciones(re.getString("observaciones"));
                    cs.setPrefijo(re.getString("prefijo"));
                    consecutivos.add(cs);
                }
            } catch (PSQLException s) {
                throw new DAOException(s.getMessage());
            } catch (SQLException s) {
                throw new DAOException(s.getMessage());
            }
        }
        return consAct;
    }

    public void actualizaEstadoConsecutivo(ConsecutivoBean cs, String estado) {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        try {
            String sql = "UPDATE CONSECUTIVOS SET ESTADO=? WHERE ID=?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, estado);
            ps.setLong(2, cs.getId());
            ps.executeUpdate();
        } catch (SQLException s) {
            NovusUtils.printLn(s.getMessage());
        } catch (Exception s) {
            NovusUtils.printLn(s.getMessage());
        }
    }

    public void actualizaEstadoConsecutivoFe(long id, String estado, String bd) {

        Connection conexion = Main.obtenerConexion(bd);
        String sql = "UPDATE CONSECUTIVOS SET ESTADO=? WHERE ID=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (SQLException s) {
            NovusUtils.printLn(s.getMessage());
        } catch (Exception s) {
            NovusUtils.printLn(s.getMessage());
        }
    }

    /*
    public boolean validarTurnoMedioPago(long idMovimiento) {
        boolean existe = false;
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try {
            String sql = "select cm.id\n" + "from ct_movimientos cm\n" + "inner join jornadas j\n" + "on j.grupo_jornada = cm.jornadas_id\n" + "where cm.id = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, idMovimiento);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                existe = true;
            }
        } catch (SQLException s) {
            NovusUtils.printLn(s.getMessage());
        }
        return existe;
    }

     */

    public Date getLastDateMovimiento() {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        Date fecha = null;
        Date fechaMovimiento;
        Date fechaJornada;

        try {
            String sql
                    = "select (select max(fecha) from movimientos) fecha_movimientos, "
                    + "max(fecha_jornadas) fecha_jornadas\n"
                    + "from (\n"
                    + "select max(fecha_inicio) fecha_jornadas from jornadas\n"
                    + "union\n"
                    + "select max(fecha_fin) fecha_jornadas from jornadas_hist\n"
                    + ") jornadas";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                fechaMovimiento = re.getTimestamp("fecha_movimientos");
                fechaJornada = re.getTimestamp("fecha_jornadas");
                if (fechaJornada != null && fechaMovimiento != null) {
                    if (fechaJornada.after(fechaMovimiento)) {
                        fecha = fechaJornada;
                    } else {
                        fecha = fechaMovimiento;
                    }
                } else {
                    fecha = fechaMovimiento;
                }
            }
        } catch (PSQLException s) {
            // throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            // throw new DAOException(s.getMessage());
        } catch (Exception s) {
            // throw new DAOException(s.getMessage());
        }
        return fecha;
    }

    // esta funcion no esta en uso
    public long getMyJornadaId(long id) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        long jornada = -1;

        try {
            String sql = "SELECT id FROM jornadas WHERE personas_id=?";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet re = ps.executeQuery();

            while (re.next()) {
                jornada = re.getLong("id");
            }
        } catch (PSQLException s) {
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }
        return jornada;
    }

    /*public long getMyJornadaIdCore(long id) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");

        long jornada = -1;

        try {
            String sql = "SELECT grupo_jornada FROM jornadas WHERE personas_id=?";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet re = ps.executeQuery();

            while (re.next()) {
                jornada = re.getLong("grupo_jornada");
            }
        } catch (PSQLException s) {
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }
        return jornada;
    }
        */

    // public long getJornadaIdCore() {
    //     Connection conexion = Main.obtenerConexion("lazoexpresscore");

    //     long jornada = 0;

    //     try {
    //         String sql = "SELECT grupo_jornada FROM jornadas LIMIT 1";

    //         PreparedStatement ps = conexion.prepareStatement(sql);
    //         ResultSet re = ps.executeQuery();

    //         while (re.next()) {
    //             jornada = re.getLong("grupo_jornada");
    //         }
    //     } catch (PSQLException s) {
    //         Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);
    //     } catch (SQLException s) {
    //         Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);
    //     } catch (Exception s) {
    //         Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);
    //     }
    //     return jornada;
    // }

    public LinkedHashSet<MovimientosBean> getSobresRealizados(long id, long jornada) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        LinkedHashSet<MovimientosBean> movimientos = new LinkedHashSet<>();

        try {
            String sql = "SELECT * FROM movimientos WHERE persona_id=? and grupo_jornada_id=? and operacion=?";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, id);
            ps.setLong(2, jornada);
            ps.setLong(3, NovusConstante.MOVIMIENTO_TIPO_SOBRES);
            ResultSet re = ps.executeQuery();

            while (re.next()) {
                MovimientosBean movimiento = new MovimientosBean();
                movimiento.setFecha(re.getTimestamp("fecha"));
                movimiento.setVentaTotal(re.getInt("venta_total"));
                movimiento.setConsecutivo(new ConsecutivoBean());
                movimiento.getConsecutivo().setConsecutivo_actual(re.getLong("consecutivo"));
                NovusUtils.printLn("entro al get sobres");
                movimientos.add(movimiento);
            }
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }
        return movimientos;
    }

    public long getConsecutivoSobre(MovimientosBean movimiento) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        long consecutivo = 1;

        try {
            String sql = "select count(1)+1 numero \n" + "from movimientos m \n" + "where \n" + "m.persona_id = ? and m.operacion = 13 and grupo_jornada_id = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, movimiento.getPersonaId());
            ps.setLong(2, movimiento.getGrupoJornadaId());
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                consecutivo = re.getLong("numero");
            }
        } catch (PSQLException s) {
            throw new DAOException("11." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("12." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("13." + s.getMessage());
        }
        return consecutivo;
    }
    // esta funcion no esta en uso
    public BodegaBean getBodegas(long bodega, long empresa) {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        BodegaBean consAS;
        ArrayList<BodegaBean> bodegabean = new ArrayList<>();

        try {
            String sql = "select * from bodegas b where b.id = ? and empresas_id = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, bodega);
            ps.setLong(2, empresa);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BodegaBean bd = new BodegaBean();
                bd.setId(rs.getInt("id"));
                bd.setDescripcion(rs.getString("descripcion"));
                bd.setCodigo(rs.getString("codigo"));
                bodegabean.add(bd);
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar las bodegas " + e);
        }
        consAS = bodegabean.get(bodegabean.size() - 1);
        return consAS;
    }

    // public long obtenerIdProductoDesdeMovimiento(long movimientoId) {
    //     Connection conexion = Main.obtenerConexion("lazoexpresscore");
    //     long idProducto = 0;

    //     try {
    //         String sql = "SELECT productos_id FROM ct_movimientos_detalles WHERE movimientos_id = ? LIMIT 1";
    //         PreparedStatement ps = conexion.prepareStatement(sql);
    //         ps.setLong(1, movimientoId);
    //         ResultSet rs = ps.executeQuery();

    //         if (rs.next()) {
    //             idProducto = rs.getLong("productos_id");
    //             NovusUtils.printLn(" ID producto recuperado desde movimiento_id: " + idProducto);
    //         } else {
    //             NovusUtils.printLn(" No se encontró producto para movimiento_id: " + movimientoId);
    //         }

    //     } catch (SQLException e) {
    //         System.out.println(" Error al obtener el ID de producto desde ct_movimientos_detalles: " + e.getMessage());
    //     }

    //     return idProducto;
    // }



    public ConsecutivoBean getPrefijo(Object tipoFacturacion, String resolucion) throws DAOException {
        String bd;
        String tabla = "consecutivos";
        String tipoDocumento = tipoFacturacion.toString();
        if (resolucion.equals("COM")) {
            bd = "lazoexpresscore";
            tabla = "ct_consecutivos";
            tipoDocumento = tipoFacturacion.toString();
        } else {
            bd = "lazoexpressregistry";
        }
        Connection conexion = Main.obtenerConexion(bd);
        ConsecutivoBean consAct = null;
        ArrayList<ConsecutivoBean> consecutivos = new ArrayList<>();
        LocalDate todaysDate = LocalDate.now();
        Calendar calendario = Calendar.getInstance();
        String sql = "select c.id, c.consecutivo_inicial, c.consecutivo_actual, c.consecutivo_final, c.estado, c.resolucion, c.fecha_inicio,c.fecha_fin, c.observaciones, c.prefijo, "
                .concat(" extract(epoch FROM (c.fecha_fin  - '" + todaysDate + "'))/3600/24::int as dias, ")
                .concat("( select extract( hour from  c.fecha_fin) - " + calendario.get(Calendar.HOUR_OF_DAY) + ")as hora, ")
                .concat("( select extract( minute from  c.fecha_fin) - " + calendario.get(Calendar.MINUTE) + ")as minuto, ")
                .concat("( select extract( second  from  c.fecha_fin) - " + calendario.get(Calendar.SECOND) + ")as segundos ")
                + "from  " + tabla + " c where cs_atributos ::json->>'destino' = ? and tipo_documento = ? and estado in('U', 'A') and equipos_id =?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, resolucion);
            if (!resolucion.equals("COM")) {
                ps.setInt(2, Integer.parseInt(tipoDocumento));
            } else {
                ps.setString(2, tipoDocumento);
            }
            ps.setLong(3, Main.credencial.getEquipos_id());
            ResultSet re = ps.executeQuery();
            if (re != null) {
                while (re.next()) {
                    ConsecutivoBean cs = new ConsecutivoBean();
                    cs.setId_fe(re.getLong("id"));
                    cs.setConsecutivo_inicial_fe(re.getLong("consecutivo_inicial"));
                    cs.setConsecutivo_actual_fe(re.getLong("consecutivo_actual"));
                    cs.setConsecutivo_final_fe(re.getLong("consecutivo_final"));
                    cs.setEstado_fe(re.getString("estado"));
                    cs.setDiasFe(re.getInt("dias"));
                    cs.setResolucion_fe(re.getString("resolucion"));
                    cs.setFecha_inicio(re.getDate("fecha_inicio"));
                    cs.setFecha_fin(re.getDate("fecha_fin"));
                    cs.setObservaciones(re.getString("observaciones"));
                    cs.setPrefijo_fe(re.getString("prefijo"));
                    if (re.getLong("dias") >= 1) {
                        cs.setHoras(1);
                        cs.setMinutos(1);
                    } else if (re.getLong("hora") > 0) {
                        cs.setDiasFe(re.getInt(1));
                        cs.setMinutos(1);
                        cs.setHoras(re.getLong("hora"));
                    } else if (re.getLong("minuto") > 0) {
                        cs.setDiasFe(re.getInt(1));
                        cs.setMinutos(re.getLong("minuto"));
                        cs.setHoras(1);
                    } else {
                        cs.setMinutos(re.getLong("minuto"));
                        cs.setHoras(re.getLong("hora"));
                    }

                    consecutivos.add(cs);
                    consAct = consecutivos.get(consecutivos.size() - 1);
                }
            }
        } catch (PSQLException s) {
            NovusUtils.printLn("error bd  -> " + s);
        } catch (SQLException s) {
            NovusUtils.printLn("error bd  -> " + s);
        } catch (Exception s) {
            NovusUtils.printLn("error bd  -> " + s);
        }

        if (!consecutivos.isEmpty()) {
            // SI SOLO HAY UN CONSECUTIVO
            if (consecutivos.size() == 1) {
                // SI EL CONSECUTIVO ESTA EN USO
                if (consecutivos.get(0).getEstado_fe().equals(NovusConstante.CONSECUTIVO_ESTADO_USO) || consecutivos.get(0).getEstado_fe().equals(NovusConstante.CONSECUTIVO_ESTADO_ACTIVO)) {
                    // SI ES MENOR O IGUAL AL FINAL LO PUEDE USAR
                    if (consecutivos.get(0).getConsecutivo_actual_fe() <= consecutivos.get(0).getConsecutivo_final_fe()
                            && consecutivos.get(0).getDiasFe() >= 1 && consecutivos.get(0).getHoras() > 0 && consecutivos.get(0).getMinutos() > 0) {
                        consAct = consecutivos.get(0);
                    } else {
                        actualizaEstadoConsecutivoFe(consecutivos.get(0).getId_fe(), NovusConstante.CONSECUTIVO_ESTADO_VENCIDO, bd);
                        consAct = null;
                    }
                }
            } else {
                // SI HAY VARIOS, CONSIGUE EL CONSECUTIVO EN USO
                for (ConsecutivoBean cons : consecutivos) {
                    if (cons.getEstado_fe().equals(NovusConstante.CONSECUTIVO_ESTADO_USO)) {
                        // SI ES MENOR O IGUAL AL FINAL LO PUEDE USAR
                        if (cons.getConsecutivo_actual_fe() <= cons.getConsecutivo_final_fe() && cons.getDiasFe() >= 1
                                && cons.getHoras() > 0 && cons.getMinutos() > 0) {
                            consAct = cons;
                            break;
                        } else {
                            actualizaEstadoConsecutivoFe(consecutivos.get(0).getId_fe(), NovusConstante.CONSECUTIVO_ESTADO_VENCIDO, bd);
                            consAct = null;
                        }
                    }
                }
                // NO HAY NINGUNO EN USO
                if (consAct == null) {
                    for (ConsecutivoBean cons : consecutivos) {
                        // BUSCAMOS EL CONSECUTIVO DISPONIBLE
                        if (cons.getConsecutivo_actual_fe() <= cons.getConsecutivo_final_fe() && cons.getDiasFe() >= 1
                                && cons.getHoras() > 0 && cons.getMinutos() > 0) {
                            consAct = cons;
                            break;
                        } else {
                            actualizaEstadoConsecutivoFe(consecutivos.get(0).getId_fe(), NovusConstante.CONSECUTIVO_ESTADO_VENCIDO, bd);
                            consAct = null;
                        }
                    }

                    // SOLO AVISA UNA VEZ AL SISTEMA QUE CONSECUTIVO USO
                    if (consAct != null) {
                        actualizaEstadoConsecutivo(consAct, NovusConstante.CONSECUTIVO_ESTADO_USO);
                    }
                }
            }
        }
        if (consAct != null && consAct.getConsecutivo_actual_fe() < consAct.getConsecutivo_final_fe()) {
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, resolucion);
                if (!resolucion.equals("COM")) {
                    ps.setInt(2, Integer.parseInt(tipoDocumento));
                } else {
                    ps.setString(2, tipoDocumento);
                }
                ps.setLong(3, Main.credencial.getEquipos_id());
                ResultSet re = ps.executeQuery();
                while (re.next()) {
                    ConsecutivoBean cs = new ConsecutivoBean();
                    cs.setId_fe(re.getLong("id"));
                    cs.setConsecutivo_inicial_fe(re.getLong("consecutivo_inicial"));
                    cs.setConsecutivo_actual_fe(re.getLong("consecutivo_actual"));
                    cs.setConsecutivo_final_fe(re.getLong("consecutivo_final"));
                    cs.setEstado_fe(re.getString("estado"));
                    cs.setDiasFe(re.getInt("dias"));
                    cs.setResolucion_fe(re.getString("resolucion"));
                    cs.setFecha_inicio(re.getDate("fecha_inicio"));
                    cs.setFecha_fin(re.getDate("fecha_fin"));
                    cs.setObservaciones(re.getString("observaciones"));
                    cs.setPrefijo_fe(re.getString("prefijo"));
                    cs.setHoras(re.getLong("hora"));
                    cs.setMinutos(re.getLong("minuto"));
                    consecutivos.add(cs);
                    consAct = consecutivos.get(consecutivos.size() - 1);
                }
            } catch (PSQLException s) {
                throw new DAOException(s.getMessage());
            } catch (SQLException s) {
                throw new DAOException(s.getMessage());
            }
        }
        return consAct;
    }

    /*
    public boolean consecutivoUsado(String prefeijo, long consecutivo) {
        String sql = "select * from ct_movimientos cm where "
                + " consecutivo = ? and prefijo = ? "
                + " and cm.fecha > (NOW() - interval '2 hours');";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, consecutivo);
            pmt.setString(2, prefeijo);
            ResultSet rs = pmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo public boolean consecutivoUsado(String prefeijo, long consecutivo) "
                    + "ubicado en la clase -> " + MovimientosDao.class.getName() + "error -> " + e.getMessage());
            return false;
        }
    }

     */
    /*
    public long obtenerConsecutivoDesdeMovimiento(long numeroMovimiento) {
        long consecutivo = 0;
        java.sql.Connection con = null;
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        try {
            String sql = "SELECT consecutivo FROM ct_movimientos WHERE id = ?";
            ps = conexion.prepareStatement(sql);
            ps.setLong(1, numeroMovimiento);
            rs = ps.executeQuery();
            if (rs.next()) {
                consecutivo = rs.getLong("consecutivo");
            }
        } catch (SQLException e) {
            System.out.println(" [ERROR] No se pudo obtener consecutivo de BD: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (ps != null) ps.close(); } catch (Exception e) {}
            try { if (conexion != null) conexion.close(); } catch (Exception e) {}
        }
        return consecutivo;
    }

     */

    /*
    public String actualizarConsecutivo(long id, long consecutivo) {
        String respuesta = "";
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        String sql = "update consecutivos set consecutivo_actual=? where id=?";
        try {
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, consecutivo);
            ps.setLong(2, id);
            int res = ps.executeUpdate();
            if (res == 1) {
                System.out.println(Main.ANSI_GREEN + "Datos actualizados con exito" + Main.ANSI_RESET);
            } else {
                System.out.println(Main.ANSI_RED + "Error al actulizar los datos" + Main.ANSI_RESET);
            }
        } catch (SQLException ex) {
            System.out.println("Error al actualizar los consecutivos " + ex);
        }

        return respuesta;
    }

     */

    /*
    public JsonArray motivosAnulacion() {
        JsonArray motivos = new JsonArray();
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "select * from wacher_parametros where codigo = 'MOTIVOS_ANULACION_FACTURA_ELECTRONICA'";
        try {
            Statement smt = conexion.createStatement();
            ResultSet res = smt.executeQuery(sql);
            while (res.next()) {
                Gson gson = new Gson();
                motivos = gson.fromJson(res.getString("valor"), JsonArray.class);
            }
        } catch (SQLException ex) {
            System.out.println("error al obtener los parametros de los motivos de anulacion");
        }
        return motivos;
    }
     */
    // esta funcion no esta en uso
    public ProductoBean findProductNoPromo(long id) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        ProductoBean productBean = null;

        try {
            String sql
                    = "Select p.id, p.plu, p.estado, p.descripcion, p.precio, p.tipo, p.cantidad_ingredientes, p.cantidad_impuestos, COALESCE(saldo, 0) saldo\n"
                    + "from productos p left join bodegas_productos bp on bp.productos_id=p.id where p.id=? and p.promocion = 'N'";

            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet re = ps.executeQuery();
            if (re.next()) {
                productBean = new ProductoBean();
                productBean.setId(re.getLong("ID"));
                productBean.setPlu(re.getString("PLU"));
                productBean.setDescripcion(re.getString("DESCRIPCION"));
                productBean.setPrecio(re.getFloat("PRECIO"));
                productBean.setCantidad(re.getInt("SALDO"));
                productBean.setSaldo(re.getInt("SALDO"));
                productBean.setEstado(re.getString("estado"));
                productBean.setCantidadIngredientes(re.getInt("CANTIDAD_INGREDIENTES"));
                productBean.setCantidadImpuestos(re.getInt("CANTIDAD_IMPUESTOS"));
                productBean.setImpuestos(findById(re.getLong("ID")));
                productBean.setIngredientes(findIngredientesById(re.getLong("ID")));
            }
        } catch (PSQLException s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);

            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);

            throw new DAOException(s.getMessage());
        }
        return productBean;
    }
    // esta funcion no esta en uso
    public long guardarSobre(MovimientosBean movimiento) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        String sql;
        try {
            long consecutivo = this.getConsecutivoSobre(movimiento);
            sql
                    = "INSERT INTO movimientos(\n"
                    + "            id, empresas_id, operacion, fecha, consecutivo, persona_id, persona_nit, \n"
                    + "            persona_nombre, tercero_id, tercero_nit, tercero_nombre, costo_total, \n"
                    + "            venta_total, impuesto_total, descuento_total, origen_id, impreso, \n"
                    + "            create_user, create_date, update_user, update_date, remoto_id, \n"
                    + "            sincronizado, movimiento_estado, consecutivo_id, consecutivo_observacion, consecutivo_prefijo, grupo_jornada_id)\n"
                    + "    VALUES (nextval('movimientos_id'), ?, ?, ?, ?, ?, ?, \n"
                    + "            ?, ?, ?, ?, ?, \n"
                    + "            ?, ?, ?, ?, ?, \n"
                    + "            ?, ?, NULL, NULL, CURRVAL('movimientos_id'), \n"
                    + "            0, 0, ?, ?, ?, ?) RETURNING ID";
            NovusUtils.printLn(sql);
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, movimiento.getEmpresasId());
            ps.setLong(2, movimiento.getOperacionId());
            ps.setTimestamp(3, new Timestamp(new Date().getTime()));
            ps.setLong(4, consecutivo);
            ps.setLong(5, movimiento.getPersonaId());
            ps.setString(6, movimiento.getPersonaNit() + "");
            ps.setString(7, movimiento.getPersonaNombre());
            if (movimiento.getClienteId() != 0) {
                ps.setLong(8, movimiento.getClienteId());
                ps.setString(9, movimiento.getClienteNit());
                ps.setString(10, movimiento.getClienteNombre());
            } else {
                ps.setNull(8, Types.NULL);
                ps.setNull(9, Types.NULL);
                ps.setNull(10, Types.NULL);
            }
            ps.setFloat(11, movimiento.getCostoTotal());
            ps.setFloat(12, movimiento.getVentaTotal());
            ps.setFloat(13, movimiento.getImpuestoTotal());
            ps.setFloat(14, movimiento.getDescuentoTotal());

            ps.setLong(15, Main.credencial.getId());
            ps.setString(16, "N");

            ps.setLong(17, movimiento.getCreateUser());
            ps.setTimestamp(18, new Timestamp(new Date().getTime()));

            ps.setNull(19, Types.NULL);
            ps.setNull(20, Types.NULL);
            ps.setNull(21, Types.NULL);
            ps.setLong(22, movimiento.getGrupoJornadaId());

            ResultSet re = ps.executeQuery();

            while (re.next()) {
                movimiento.setId(re.getLong("id"));
                movimiento.setRemotoId(re.getLong("id"));
            }
        } catch (PSQLException s) {
            throw new DAOException("11." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("12." + s.getMessage());
        } catch (Exception s) {
            throw new DAOException("13." + s.getMessage());
        }
        return movimiento.getId();
    }

    public LinkedHashSet<MovimientosBean> getVentas() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        LinkedHashSet<MovimientosBean> movimientos = new LinkedHashSet<>();

        try {
            String sql = "SELECT * FROM movimientos WHERE operacion=35 and fecha >= '2022-01-01 00:00:00'";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                MovimientosBean movimiento = new MovimientosBean();
                movimiento.setId(re.getLong("id"));
                movimiento.setFecha(re.getTimestamp("fecha"));
                movimiento.setVentaTotal(re.getInt("venta_total"));
                movimiento.setCostoTotal(re.getFloat("costo_total"));
                movimiento.setImpuestoTotal(re.getFloat("impuesto_total"));

                movimiento.setPersonaId(re.getLong("persona_id"));
                movimiento.setPersonaNit(re.getString("persona_nit"));
                movimiento.setPersonaNombre(re.getString("persona_nombre"));

                movimiento.setGrupoJornadaId(re.getLong("grupo_jornada_id"));

                movimiento.setConsecutivo(new ConsecutivoBean());
                movimiento.getConsecutivo().setId(re.getLong("consecutivo_id"));
                movimiento.getConsecutivo().setConsecutivo_actual(re.getLong("consecutivo"));
                movimiento.getConsecutivo().setPrefijo(re.getString("consecutivo_prefijo"));
                movimientos.add(movimiento);
            }
        } catch (SQLException s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);
            throw new DAOException(s.getMessage());
        }
        return movimientos;
    }
    // esta funcion no esta en uso
    public LinkedHashSet<MovimientosDetallesBean> getVentasDetalles(MovimientosBean movimiento) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        LinkedHashSet<MovimientosDetallesBean> movimientos = new LinkedHashSet<>();

        try {
            String sql = "SELECT md.*, p.plu " + "FROM movimientos_detalles md " + "inner join productos p on p.id = md.productos_id " + "WHERE movimientos_id=?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, movimiento.getId());
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                MovimientosDetallesBean detalles = new MovimientosDetallesBean();
                detalles.setRemotoId(re.getLong("id"));
                detalles.setId(re.getLong("productos_id"));
                detalles.setPlu(re.getString("plu"));
                detalles.setCantidadUnidad(re.getFloat("cantidad"));
                detalles.setSubtotal(re.getFloat("subtotal"));
                detalles.setCosto(re.getFloat("costo_producto") * re.getFloat("cantidad"));
                movimientos.add(detalles);
            }
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }
        return movimientos;
    }
    // esta funcion no esta en uso
    public TreeMap<Long, MediosPagosBean> getMediosPagoById(long id) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        TreeMap<Long, MediosPagosBean> medios = new TreeMap<>();

        try {
            String sql = "SELECT mp.id medio_id, mp.descripcion, mmp.*\n" + "FROM movimientos_medios_pagos mmp\n" + "inner join medios_pagos mp on mp.id = mmp.medios_pagos_id \n" + "WHERE movimientos_id=?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                MediosPagosBean medio = new MediosPagosBean();
                medio.setIdRegistro(re.getLong("id"));
                medio.setId(re.getLong("medio_id"));
                medio.setDescripcion(re.getString("descripcion"));
                medio.setRecibido(re.getFloat("recibido"));
                medio.setValor(re.getFloat("recibido"));
                medio.setVoucher(re.getString("comprobante_valor"));
                medios.put(medio.getId(), medio);
            }
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        }
        return medios;
    }
    // esta funcion no esta en uso
    public float getValorImpuesto(long id) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");

        float valor = 0;

        try {
            String sql = "SELECT valor FROM impuestos where id=?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                valor = re.getFloat("valor");
            }
        } catch (SQLException s) {
            // throw new DAOException(s.getMessage());
        } catch (Exception s) {
            // throw new DAOException(s.getMessage());
        }
        return valor;
    }

    public boolean ActualizarAtributosViveTerpel(Long id, JsonArray BonosViveTerpel) throws DAOException {

        NovusUtils.printLn("ActualizarAtributosViveTerpel ");

        Connection conexion = Main.obtenerConexion("lazoexpresscore");

        boolean actualizado = false;

        try {
            String sql = "SELECT ATRIBUTOS FROM CT_MOVIMIENTOS WHERE id=" + id + " LIMIT 1";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                JsonObject atributos = new Gson().fromJson(re.getString("atributos"), JsonObject.class);

                if (!atributos.isJsonNull()) {
                    atributos.addProperty("redencion", "S");
                    if (atributos.has("Bonos_Vive_Terpel") && !atributos.get("Bonos_Vive_Terpel").isJsonNull()) {
                        atributos.get("Bonos_Vive_Terpel").getAsJsonArray().addAll(BonosViveTerpel);
                    } else {
                        atributos.add("Bonos_Vive_Terpel", BonosViveTerpel);
                    }
                    NovusUtils.printLn("SETEANDO BONOS EN ATRIBUTOS");
                    String sql2 = "UPDATE CT_MOVIMIENTOS SET estado='M', atributos=" + "'" + atributos + "'" + "WHERE id= ?";
                    PreparedStatement ps2 = conexion.prepareStatement(sql2);
                    ps2.setLong(1, id);
                    int x = ps2.executeUpdate();
                    if (x == 1) {
                        actualizado = true;
                    }
                }
            }
        } catch (PSQLException s) {
            throw new DAOException("31." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("32." + s.getMessage());
        } catch (JsonSyntaxException s) {
            throw new DAOException("33." + s.getMessage());
        }
        return actualizado;
    }

    public JsonObject findAllCierresDia(String dia) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");

        JsonObject data = new JsonObject();
        JsonObject info = new JsonObject();
        try {
            String sql
                    = "SELECT \n"
                    + "\n"
                    + "SUM(((datos->>'lt_data')::json->>'TotalVentasSistema')::numeric) \"totalVentasCombustible\",\n"
                    + "SUM(((datos->>'lt_data')::json->>'TotalVentasCanastilla')::numeric) \"totalVentasCanastilla\",\n"
                    + "SUM(((datos->>'lt_data')::json->>'CantidadVentasSistema')::numeric) \"cantidadVentasCombustible\",\n"
                    + "SUM(((datos->>'lt_data')::json->>'NumeroVentasCanastilla')::numeric) \"cantidadVentasCanastilla\"\n"
                    + "\n"
                    + "FROM cierres WHERE datos is not null \n"
                    + "and ((((datos->>'lt_data')::json->>'Turnos')::json->>0)::json->>'fecha')::date = ?::date  ";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, dia);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                data.addProperty("totalVentasCombustible", re.getLong(1));
                data.addProperty("totalVentasCanastilla", re.getLong(2));
                data.addProperty("cantidadVentasCombustible", re.getFloat(3));
                data.addProperty("cantidadVentasCanastilla", re.getFloat(4));
                JsonArray arr = new JsonArray();

                sql
                        = " SELECT \n"
                        + "((datos->>'lt_data')::json->>'ReporteMedios')  \"ReporteMedios\" \n"
                        + "FROM cierres WHERE datos is not null \n"
                        + "and ((((datos->>'lt_data')::json->>'Turnos')::json->>0)::json->>'fecha')::date = ?::date ";
                ps = conexion.prepareStatement(sql);
                ps.setString(1, dia);
                re = ps.executeQuery();

                while (re.next()) {
                    JsonArray arrayMedios = new Gson().fromJson(re.getString(1), JsonArray.class);
                    if (arr.size() == 0) {
                        for (JsonElement Item22 : arrayMedios) {
                            if (Item22.getAsJsonObject().get("total") == null) {
                                Item22.getAsJsonObject().addProperty("total", Item22.getAsJsonObject().get("valor_total").getAsFloat());
                            }
                            arr.add(Item22);
                        }
                    } else {
                        for (JsonElement ReporteMediosjElement1 : arrayMedios) {
                            JsonObject Item1 = ReporteMediosjElement1.getAsJsonObject();
                            boolean existe = false;
                            for (JsonElement Item22 : arr) {
                                JsonObject Item2 = Item22.getAsJsonObject();
                                if (Item1.get("total") == null) {
                                    Item1.addProperty("total", Item1.get("valor_total").getAsFloat());
                                }

                                if (Item2.get("total") == null) {
                                    Item2.addProperty("total", Item2.get("valor_total").getAsFloat());
                                }

                                if (Item1.get("id").getAsLong() == Item2.get("id").getAsLong()) {
                                    Item2.addProperty("cantidad", Item2.get("cantidad").getAsFloat() + Item1.get("cantidad").getAsFloat());
                                    Item2.addProperty("total", Item2.get("total").getAsFloat() + Item1.get("total").getAsFloat());
                                    existe = true;
                                    break;
                                }
                            }
                            if (!existe) {
                                arr.add(Item1);
                            }
                        }
                    }
                }
                data.add("mediosPagos", arr);
            }
        } catch (PSQLException s) {
            throw new DAOException("31." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("32." + s.getMessage());
        } catch (JsonSyntaxException s) {
            throw new DAOException("33." + s.getMessage());
        }
        info.add("data", data);
        return info;
    }

    public JsonObject findAllCierresDiaReporteria(String dia) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        JsonObject data = new JsonObject();
        JsonObject info = new JsonObject();

        String sql = "select * from reporteria_cierres.fnc_consultar_cierre_dia(?) data;";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, dia);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                if (re.getString(1) != null) {
                    JsonObject reporteCierre = new Gson().fromJson(re.getString(1), JsonObject.class);
                    if (reporteCierre != null) {
                        data.addProperty("totalVentasCombustible", reporteCierre.get("totalVentasCombustible").getAsLong());
                        data.addProperty("totalVentasCanastilla", reporteCierre.get("totalVentasCanastilla").getAsLong());
                        data.addProperty("cantidadVentasCombustible", reporteCierre.get("cantidadVentasCombustible").getAsFloat());
                        data.addProperty("cantidadVentasCanastilla", reporteCierre.get("cantidadVentasCanastilla").getAsFloat());
                        data.addProperty("cantidadVentasCDL", reporteCierre.get("cantidadVentasCDL").getAsFloat());
                        data.addProperty("totalVentasCDL", reporteCierre.get("totalVentasCDL").getAsFloat());
                        JsonArray arr = new JsonArray();

                        JsonArray arrayMedios = reporteCierre.get("ReporteMedios").getAsJsonArray();
                        for (JsonElement ReporteMediosjElement1 : arrayMedios) {
                            JsonObject item1 = ReporteMediosjElement1.getAsJsonObject();
                            boolean existe = false;
                            for (JsonElement Item22 : arr) {
                                JsonObject item2 = Item22.getAsJsonObject();
                                if (item1.get("total") == null) {
                                    item1.addProperty("total", item1.get("total").getAsFloat());
                                }

                                if (item2.get("total") == null) {
                                    item2.addProperty("total", item2.get("total").getAsFloat());
                                }

                                if (item1.get("id").getAsLong() == item2.get("id").getAsLong()) {
                                    item2.addProperty("cantidad", item2.get("cantidad").getAsFloat() + item1.get("cantidad").getAsFloat());
                                    item2.addProperty("total", item2.get("total").getAsFloat() + item1.get("total").getAsFloat());
                                    existe = true;
                                    break;
                                }
                            }
                            if (!existe) {
                                arr.add(item1);
                            }
                        }
                        data.add("mediosPagos", arr);
                        info.add("data", data);
                    }
                }
            }
        } catch (PSQLException s) {
            throw new DAOException("31." + s.getMessage());
        } catch (SQLException s) {
            throw new DAOException("32." + s.getMessage());
        } catch (JsonSyntaxException s) {
            throw new DAOException("33." + s.getMessage());
        }
        return info;
    }

     /* public ArrayList<PersonaBean> obtenerPromotoresJornada() throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");

        SimpleDateFormat sdf2 = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
        ArrayList<PersonaBean> jornadas = new ArrayList<>();

        try {
            String sql = "select\n"
                    + "	J.*,\n"
                    + "	p.nombre nombres,\n"
                    + "	p.identificacion,\n"
                    + "	ti.descripcion \n"
                    + "from\n"
                    + "	jornadas J\n"
                    + "inner join PERSONAS P on\n"
                    + "	P.ID = PERSONAS_ID\n"
                    + "inner join tipos_identificaciones ti on \n"
                    + "ti.id = p.tipos_identificacion_id \n"
                    + "where\n"
                    + "	fecha_fin is null;";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                PersonaBean promotor = new PersonaBean();
                promotor.setId(re.getLong("personas_id"));
                promotor.setIdentificacion(re.getLong("identificacion") + "");
                promotor.setTipoIdentificacionDesc(re.getString("descripcion"));
                promotor.setFechaInicio(sdf2.parse(re.getString("fecha_inicio")));
                promotor.setNombre(re.getString("nombres"));
                promotor.setApellidos("");
                promotor.setModulos(new ArrayList<>());
                promotor.setGrupoJornadaId(re.getLong("grupo_jornada"));
                Main.persona = promotor;
                jornadas.add(promotor);
            }
        } catch (PSQLException s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);
        } catch (SQLException | JsonSyntaxException | NumberFormatException | ParseException s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);
        }
        return jornadas;
    } */

    // esta funcion no esta en uso
    public JsonObject buscarVentas(long promotorId, long grupoJornadaId, int length) {
        JsonObject json = null;
        DatabaseConnectionManager.DatabaseResources resources = null;

        try {
            String sql
                    = "select cm.id numero, ce.razon_social, ce.nit, cmd.cantidad, cmd.precio, cm.venta_total total, cm.tipo,\n"
                    + "  coalesce((cm.atributos::json->'recaudo')::text, '0') recaudo, p.descripcion producto, pu.descripcion unidad_medida, coalesce(cm.consecutivo, 0) consecutivo,\n"
                    + "  (cm.atributos::json->'surtidor')::text surtidor,\n"
                    + "  coalesce((cm.atributos::json->'cara')::text, '0') cara,\n"
                    + "  coalesce((cm.atributos::json->'manguera')::text, '0') manguera,\n"
                    + "  coalesce((cm.atributos::json->'islas'), '0') islas, cm.atributos,\n"
                    + "  'DUPLICADO' copia, cm.fecha, concat(cper.nombres, ' ', cper.apellidos) operador,\n"
                    + "  cper.identificacion \"identificacionPromotor\",\n"
                    + "  cmd.productos_id \"identificacionProducto\",\n"
                    + "  COALESCE((SELECT array_to_json(array_agg(row_to_json(t))) FROM (\n"
                    + "        SELECT cmp.id medio, cmp.descripcion \"medioDescripcion\",\n"
                    + "        cmmp.valor_total \"valorPago\"\n"
                    + "        FROM ct_movimientos_medios_pagos cmmp\n"
                    + "        inner join ct_medios_pagos cmp on cmmp.ct_medios_pagos_id = cmp.id\n"
                    + "        where cmmp.ct_movimientos_id = cm.id\n"
                    + "  ) t) ,'[]') medios_pagos\n"
                    + "  from ct_movimientos cm\n"
                    + "  inner join ct_empresas ce on cm.empresas_id = ce.id\n"
                    + "  inner join ct_movimientos_detalles cmd ON cm.id = cmd.movimientos_id and cmd.ano= cm.ano and cmd.mes=cm.mes and cmd.dia= cm.dia\n"
                    + "  inner join productos p on cmd.productos_id = p.id\n"
                    + "  inner join ct_personas cper on cm.responsables_id = cper.id\n"
                    + "  inner join productos_unidades pu on p.unidad_medida_id = pu.id\n"
                    + "  where cm.jornadas_id = ? and cm.tipo in ('016', '017', '032','014') and estado_movimiento in ('016001', '017000', '032001','014001')\n"
                    + "  and (cm.atributos->'tipoVenta')::text <> '5' \n"
                    + ((promotorId > 0) ? " and cm.responsables_id=? " : " ")
                    + "  order by cm.id DESC\n"
                    + " FETCH FIRST ? ROWS ONLY";

            resources = DatabaseConnectionManager.createDatabaseResources("lazoexpresscore", sql);
            resources.getPreparedStatement().setLong(1, grupoJornadaId);
            resources.getPreparedStatement().setInt(2, length);
            if (promotorId > 0) {
                resources.getPreparedStatement().setLong(2, promotorId);
                resources.getPreparedStatement().setInt(3, length);
            }
            resources = DatabaseConnectionManager.executeQuery(resources);
            JsonArray data = null;
            SimpleDateFormat sdf2 = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
            while (resources.getResultSet().next()) {
                if (data == null) {
                    data = new JsonArray();
                }
                JsonObject row = new JsonObject();
                JsonObject atributosDB = new Gson().fromJson(resources.getResultSet().getString("atributos"), JsonObject.class);
                JsonObject atributos = new JsonObject();

                if (resources.getResultSet().getString("tipo").equals("016") && atributosDB.has("isElectronica") && !atributosDB.get("isElectronica").getAsBoolean()) {
                    JsonObject temp1 = new JsonObject();
                    temp1.addProperty("prefijo", "PROPIO");
                    temp1.addProperty("consecutivo_actual", resources.getResultSet().getLong("numero"));
                    atributosDB.add("consecutivo", temp1);
                } else if (resources.getResultSet().getString("tipo").equals("032")) {
                    JsonObject temp1 = new JsonObject();
                    temp1.addProperty("prefijo", "CREDITO");
                    temp1.addProperty("consecutivo_actual", resources.getResultSet().getLong("numero"));
                    atributosDB.add("consecutivo", temp1);
                }

                atributos.addProperty("vehiculo_placa", atributosDB.get("vehiculo_placa").getAsString());
                atributos.addProperty("voucher", atributosDB.get("voucher") != null && !atributosDB.get("voucher").isJsonNull() ? atributosDB.get("voucher").getAsString() : "");
                atributos.addProperty("orden", atributosDB.get("orden") != null && !atributosDB.get("orden").isJsonNull() ? atributosDB.get("orden").getAsString() : "");
                atributos.addProperty(
                        "vehiculo_odometro",
                        atributosDB.get("vehiculo_odometro") != null && !atributosDB.get("vehiculo_odometro").isJsonNull() ? atributosDB.get("vehiculo_odometro").getAsString() : ""
                );
                atributos.addProperty(
                        "vehiculo_numero",
                        atributosDB.get("vehiculo_numero") != null && !atributosDB.get("vehiculo_numero").isJsonNull() ? atributosDB.get("vehiculo_numero").getAsString() : ""
                );
                atributos.add(
                        "consecutivo",
                        atributosDB.get("consecutivo") != null && !atributosDB.get("consecutivo").isJsonNull() && atributosDB.get("consecutivo").isJsonObject()
                                ? atributosDB.get("consecutivo").getAsJsonObject()
                                : null
                );
                atributos.addProperty("isElectronica", atributosDB.get("isElectronica").getAsBoolean());
                atributos.addProperty("recuperada", atributosDB.get("recuperada") != null && !atributosDB.get("recuperada").isJsonNull() ? atributosDB.get("recuperada").getAsBoolean() : false);
                atributos.addProperty("isCredito", atributosDB.get("isCredito").getAsBoolean());
                atributos.addProperty("tipoVenta", atributosDB.get("tipoVenta").getAsFloat());

                row.addProperty("consecutivo", resources.getResultSet().getLong("consecutivo"));
                row.addProperty("numero", resources.getResultSet().getLong("numero"));
                row.addProperty("tipo", resources.getResultSet().getString("tipo"));
                row.addProperty("cantidad", resources.getResultSet().getDouble("cantidad"));
                row.addProperty("precio", resources.getResultSet().getLong("precio"));
                row.addProperty("total", resources.getResultSet().getLong("total"));
                row.addProperty("cara", resources.getResultSet().getLong("cara"));
                row.addProperty("manguera", resources.getResultSet().getLong("manguera"));
                row.addProperty("fecha", sdf2.format(sdf2.parse(resources.getResultSet().getString("fecha"))));

                row.addProperty("producto", resources.getResultSet().getString("producto"));
                row.addProperty("operador", resources.getResultSet().getString("operador"));
                row.addProperty("identificacionPromotor", resources.getResultSet().getString("identificacionPromotor"));
                row.addProperty("identificacionProducto", resources.getResultSet().getLong("identificacionProducto"));
                row.addProperty("unidad_medida", resources.getResultSet().getString("unidad_medida"));

                row.add("atributos", atributos);

                JsonArray medios = new Gson().fromJson(resources.getResultSet().getString("medios_pagos"), JsonArray.class);
                row.add("medios_pagos", medios);
                data.add(row);
            }
            if (data != null) {
                json = new JsonObject();
                json.add("data", data);
            }
        } catch (PSQLException s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);
        } catch (SQLException | JsonSyntaxException | NumberFormatException | ParseException s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);
        } finally {
            DatabaseConnectionManager.closeDatabaseResources(resources);
        }
        return json;
    }



    public JsonObject buscarVentasFidelizar(long promotorId, long grupoJornadaId) {
        JsonObject json = null;
        DatabaseConnectionManager.DatabaseResources resources = null;

        try {
            String sql
                    = "select cm.id numero, ce.razon_social, ce.nit, cmd.cantidad, cmd.precio, cm.venta_total total, cm.tipo,\n"
                    + "  coalesce((cm.atributos::json->'recaudo')::text, '0') recaudo, p.descripcion producto, pu.descripcion unidad_medida, coalesce(cm.consecutivo, 0) consecutivo,\n"
                    + "  (cm.atributos::json->'surtidor')::text surtidor,\n"
                    + "  coalesce((cm.atributos::json->'cara')::text, '0') cara,\n"
                    + "  coalesce((cm.atributos::json->'manguera')::text, '0') manguera,\n"
                    + "  coalesce((cm.atributos::json->'islas'), '0') islas, cm.atributos,\n"
                    + "  'DUPLICADO' copia, cm.fecha, concat(cper.nombres, ' ', cper.apellidos) operador,\n"
                    + "  cper.identificacion \"identificacionPromotor\",\n"
                    + "  cmd.productos_id \"identificacionProducto\",\n"
                    + "  COALESCE((SELECT array_to_json(array_agg(row_to_json(t))) FROM (\n"
                    + "        SELECT cmp.id medio, cmp.descripcion \"medioDescripcion\",\n"
                    + "        cmmp.valor_total \"valorPago\"\n"
                    + "        FROM ct_movimientos_medios_pagos cmmp\n"
                    + "        inner join ct_medios_pagos cmp on cmmp.ct_medios_pagos_id = cmp.id\n"
                    + "        where cmmp.ct_movimientos_id = cm.id\n"
                    + "  ) t) ,'[]') medios_pagos\n"
                    + "  from ct_movimientos cm\n"
                    + "  inner join ct_empresas ce on cm.empresas_id = ce.id\n"
                    + "  inner join ct_movimientos_detalles cmd ON cm.id = cmd.movimientos_id and cmd.ano= cm.ano and cmd.mes=cm.mes and cmd.dia= cm.dia\n"
                    + "  inner join productos p on cmd.productos_id = p.id\n"
                    + "  inner join ct_personas cper on cm.responsables_id = cper.id\n"
                    + "  inner join productos_unidades pu on p.unidad_medida_id = pu.id\n"
                    + "left join (select * \n"
                    + "from procesos.tbl_transaccion_proceso ttp \n"
                    + "inner join ct_movimientos cm on cm.id =  ttp.id_movimiento \n"
                    + "and id_integracion <> 3\n"
                    + "and id_estado_integracion in (4,5,8,9)) ttp on ttp.id_movimiento =cm.id \n"
                    + "  where cm.jornadas_id = ? and cm.tipo in ('017','032') and cm.estado_movimiento in ('017000', '032001')\n"
                    + "  and (cm.atributos->'tipoVenta')::text <> '5' \n"
                    + "  and cm.fecha between (now() - '3 minutes'::interval) and now()\n"
                    + "  and cm.atributos::json->>'fidelizada' = 'N'\n"
                    + ((promotorId > 0) ? " and cm.responsables_id=? " : " ")
                    + "  order by cm.id DESC\n";

            resources = DatabaseConnectionManager.createDatabaseResources("lazoexpresscore", sql);
            resources.getPreparedStatement().setLong(1, grupoJornadaId);

            if (promotorId > 0) {
                resources.getPreparedStatement().setLong(2, promotorId);
            }
            resources = DatabaseConnectionManager.executeQuery(resources);
            JsonArray data = null;
            SimpleDateFormat sdf2 = new SimpleDateFormat(NovusConstante.FORMAT_DATETIME_SQL);
            while (resources.getResultSet().next()) {
                if (data == null) {
                    data = new JsonArray();
                }
                JsonObject row = new JsonObject();
                JsonObject atributosDB = new Gson().fromJson(resources.getResultSet().getString("atributos"), JsonObject.class);
                JsonObject atributos = new JsonObject();

                if (resources.getResultSet().getString("tipo").equals("016")) {
                    JsonObject temp1 = new JsonObject();
                    temp1.addProperty("prefijo", "PROPIO");
                    temp1.addProperty("consecutivo_actual", resources.getResultSet().getLong("numero"));
                    atributosDB.add("consecutivo", temp1);
                } else if (resources.getResultSet().getString("tipo").equals("032")) {
                    JsonObject temp1 = new JsonObject();
                    temp1.addProperty("prefijo", "CREDITO");
                    temp1.addProperty("consecutivo_actual", resources.getResultSet().getLong("numero"));
                    atributosDB.add("consecutivo", temp1);
                }

                atributos.addProperty("vehiculo_placa", atributosDB.has("vehiculo_placa") ? atributosDB.get("vehiculo_placa").getAsString() : "");
                atributos.addProperty("voucher", atributosDB.get("voucher") != null && !atributosDB.get("voucher").isJsonNull() ? atributosDB.get("voucher").getAsString() : "");
                atributos.addProperty("orden", atributosDB.get("orden") != null && !atributosDB.get("orden").isJsonNull() ? atributosDB.get("orden").getAsString() : "");
                atributos.addProperty(
                        "vehiculo_odometro",
                        atributosDB.get("vehiculo_odometro") != null && !atributosDB.get("vehiculo_odometro").isJsonNull() ? atributosDB.get("vehiculo_odometro").getAsString() : ""
                );
                atributos.addProperty(
                        "vehiculo_numero",
                        atributosDB.get("vehiculo_numero") != null && !atributosDB.get("vehiculo_numero").isJsonNull() ? atributosDB.get("vehiculo_numero").getAsString() : ""
                );
                atributos.add(
                        "consecutivo",
                        atributosDB.get("consecutivo") != null && !atributosDB.get("consecutivo").isJsonNull() && atributosDB.get("consecutivo").isJsonObject()
                                ? atributosDB.get("consecutivo").getAsJsonObject()
                                : null
                );
                atributos.addProperty("isElectronica", atributosDB.has("isElectronica") ? atributosDB.get("isElectronica").getAsBoolean() : false);
                atributos.addProperty("recuperada", atributosDB.get("recuperada") != null && !atributosDB.get("recuperada").isJsonNull() ? atributosDB.get("recuperada").getAsBoolean() : false);
                atributos.addProperty("isCredito", atributosDB.has("isCredito") ? atributosDB.get("isCredito").getAsBoolean() : false);

                row.addProperty("consecutivo", resources.getResultSet().getLong("consecutivo"));
                row.addProperty("numero", resources.getResultSet().getLong("numero"));
                row.addProperty("tipo", resources.getResultSet().getString("tipo"));
                row.addProperty("cantidad", resources.getResultSet().getDouble("cantidad"));
                row.addProperty("precio", resources.getResultSet().getLong("precio"));
                row.addProperty("total", resources.getResultSet().getLong("total"));
                row.addProperty("cara", resources.getResultSet().getLong("cara"));
                row.addProperty("manguera", resources.getResultSet().getLong("manguera"));
                row.addProperty("fecha", sdf2.format(sdf2.parse(resources.getResultSet().getString("fecha"))));

                row.addProperty("producto", resources.getResultSet().getString("producto"));
                row.addProperty("operador", resources.getResultSet().getString("operador"));
                row.addProperty("identificacionPromotor", resources.getResultSet().getString("identificacionPromotor"));
                row.addProperty("identificacionProducto", resources.getResultSet().getLong("identificacionProducto"));
                row.addProperty("unidad_medida", resources.getResultSet().getString("unidad_medida"));

                row.add("atributos", atributos);

                JsonArray medios = new Gson().fromJson(resources.getResultSet().getString("medios_pagos"), JsonArray.class);
                row.add("medios_pagos", medios);
                data.add(row);
            }
            if (data != null) {
                json = new JsonObject();
                json.add("data", data);
            }
        } catch (PSQLException s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);
        } catch (SQLException | JsonSyntaxException | NumberFormatException | ParseException s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);
        } finally {
            DatabaseConnectionManager.closeDatabaseResources(resources);
        }
        return json;
    }
    /*
    public JsonObject montoMinimoFE() {
        JsonObject montoFE = new JsonObject();
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "select valor, codigo from wacher_parametros where codigo = 'OBLIGATORIO_FE'";
        try (Statement smt = conexion.createStatement()) {
            ResultSet rs = smt.executeQuery(sql);
            if (rs.next()) {
                montoFE = new JsonObject();
                montoFE.addProperty("OBLIGATORIO_FE", rs.getString("valor"));
                montoFE.addProperty("monto_minimo", valorMontoMinimo());
                montoFE.addProperty("error", valorMontoMinimo() == -1.0f);
            }
        } catch (SQLException ex) {
            NovusUtils.printLn("ha ocurrido un error inesperado al momento de conusltar la obligatoriedad de la factura electronica en le metodo public JsonObject montoMinimoFE() -> " + ex.getMessage());
            montoFE = new JsonObject();
            montoFE.addProperty("error", true);
        }
        return montoFE;
    }

     */
    /*
    private float valorMontoMinimo() {
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "select valor, codigo from wacher_parametros where codigo = 'MONTO_MINIMO_FE'";
        try (Statement smt = conexion.createStatement()) {
            ResultSet rs = smt.executeQuery(sql);
            if (rs.next()) {
                return Float.parseFloat(rs.getString("valor"));
            } else {
                return -1.0f;
            }
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error inisperado al momento de consultar el valor del monto minimo private float valorMontoMinimo () -> " + e.getMessage());
            return -1.0f;
        }
    }
     */
    /*
    public JsonObject montoMinimoRemision() {
        JsonObject montoFE = new JsonObject();
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "select valor, codigo from wacher_parametros where codigo = 'OBLIGATORIEDAD_REMISION'";
        try (Statement smt = conexion.createStatement()) {
            ResultSet rs = smt.executeQuery(sql);
            if (rs.next()) {
                montoFE.addProperty("OBLIGATORIO_FE", rs.getString("valor"));
                montoFE.addProperty("monto_minimo", valorMontoMinimoRemision());
                montoFE.addProperty("error", valorMontoMinimoRemision() == -1.0f);
            }
        } catch (SQLException ex) {
            NovusUtils.printLn("ha ocurrido un error inesperado al momento de conusltar la obligatoriedad de la factura electronica en le metodo public JsonObject montoMinimoRemision() -> " + ex.getMessage());
            montoFE = new JsonObject();
            montoFE.addProperty("error", true);

        }
        return montoFE;
    }

     */
    /*
    private float valorMontoMinimoRemision() {
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "select valor, codigo from wacher_parametros where codigo = 'MONTO_MINIMO_REMISION'";
        try (Statement smt = conexion.createStatement()) {
            ResultSet rs = smt.executeQuery(sql);
            if (rs.next()) {
                return Float.parseFloat(rs.getString("valor"));
            } else {
                return -1.0f;
            }
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error inisperado al momento de consultar el valor del monto minimo private float valorMontoMinimoRemision() -> " + e.getMessage());
            return -1.0f;
        }
    }

     */
    // esta funcion no esta en uso
    public JsonArray buscarVentasFE() {
        JsonArray respuesta = new JsonArray();
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "select cm.fecha, cm.id, cm.venta_total, cmd.cantidad, cp.descripcion, cmd.precio from ct_movimientos cm \n"
                + "inner join ct_movimientos_detalles cmd  on cm.id  = cmd.movimientos_id \n"
                + "inner join ct_productos cp on cmd.productos_id = cp.id \n"
                + "where cm.tipo = '017' and cm.atributos::json->>'cliente' is not null  \n";
//                + "and ((cm.atributos::json)->>'cliente')::json->>'consultarCliente' = 'false' \n"
//                + "order by fecha desc ";
        try {
            Statement smt = conexion.createStatement();
            ResultSet rs = smt.executeQuery(sql);
            while (rs.next()) {
                JsonObject data = new JsonObject();
                data.addProperty("id", rs.getInt("id"));
                data.addProperty("fecha_fin", rs.getString("fecha"));
                data.addProperty("descripcion", rs.getString("descripcion"));
                data.addProperty("cantidad", rs.getFloat("cantidad"));
                data.addProperty("precio", rs.getFloat("precio"));
                data.addProperty("total", rs.getFloat("venta_total"));
                respuesta.add(data);
            }
        } catch (SQLException ex) {
            NovusUtils.printLn("error al consultar la venta");
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return respuesta;
    }

    public JsonObject fechaDevencimiento(int tipoDoc, String destino, String base) {
        JsonObject vencimientoConsecutivo = new JsonObject();
        String fecha_inicio = "";
        String fecha_fin = "";
        String consecutivos = "consecutivos";
        if (!base.equals("lazoexpressregistry")) {
            consecutivos = "ct_consecutivos";
        }
        Connection conexion = Main.obtenerConexion(base);
        LocalDate todaysDate = LocalDate.now();
        Calendar calendario = Calendar.getInstance();
        String sql = "select ".concat(" c.fecha_fin,")
                .concat(" c.fecha_inicio,")
                .concat(" c.cs_atributos->>'alerta_dias' as aleterta_dias,")
                .concat(" c.cs_atributos->>'alerta_consecutivos' as aleterta_consecutivo,")
                .concat(" c.consecutivo_final - c.consecutivo_actual as consecutivos_restantes,")
                .concat(" extract(epoch FROM (c.fecha_fin  - '" + todaysDate + "'))/3600/24::int as dias,")
                .concat("( select extract( hour from  c.fecha_fin) - " + calendario.get(Calendar.HOUR_OF_DAY) + ")as hora, ")
                .concat("( select extract( minute from  c.fecha_fin) - " + calendario.get(Calendar.MINUTE) + ")as minuto, ")
                .concat("( select extract( second  from  c.fecha_fin) - " + calendario.get(Calendar.SECOND) + ")as segundos, ")
                .concat(" c.id")
                .concat(" from")
                .concat(" " + consecutivos + " c")
                .concat(" where")
                .concat(" c.estado in('A', 'U')")
                .concat(" and c.cs_atributos->>'destino' = ?")
                .concat(" and tipo_documento = ?");
        try (PreparedStatement ps = conexion.prepareStatement(sql);) {
            ps.setString(1, destino);
            ps.setInt(2, tipoDoc);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                fecha_fin = rs.getString(1);
                fecha_inicio = rs.getString(2);
                vencimientoConsecutivo.addProperty("alertaDias", Integer.parseInt(rs.getString(3)));
                vencimientoConsecutivo.addProperty("alertaConsecutivos", Integer.parseInt(rs.getString(4)));
                vencimientoConsecutivo.addProperty("consecutivosRestantes", Integer.parseInt(rs.getString(5)));
                vencimientoConsecutivo.addProperty("diasConsecutivos", rs.getInt("dias") > 0 ? rs.getInt("dias") : 0);
                vencimientoConsecutivo.addProperty("horaCencimiento", rs.getInt("hora") > 0 ? rs.getInt("hora") : 0);
                vencimientoConsecutivo.addProperty("segundoCencimiento", rs.getInt("minuto") > 0 ? rs.getInt("minuto") : 0);
                vencimientoConsecutivo.addProperty("id", rs.getLong("id"));
            }
        } catch (SQLException ex) {
            NovusUtils.printLn(Main.ANSI_RED + "Error al consultar las fechas de vencimiento: " + ex + Main.ANSI_RESET);
        }
        return vencimientoConsecutivo;
    }

    public boolean facturacionExterna() {
        boolean facturacionExterna = false;
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "select valor from wacher_parametros where codigo = 'FACTURACION_EXTERNA'";
        try {
            Statement stm = conexion.createStatement();
            ResultSet rs = stm.executeQuery(sql);
            if (rs.next()) {
                if (rs.getString(1).equals("S")) {
                    facturacionExterna = true;
                }
            }
        } catch (SQLException ex) {
            System.out.println("error al consultar el parametro de la facturacion externa " + ex);
        }
        return facturacionExterna;
    }
    /*
    public JsonObject mensajesFE() {
        JsonObject mensajes = new JsonObject();
        String sql = "select valor from wacher_parametros where codigo = 'MENSAJES_FE'";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try (Statement stm = conexion.createStatement();) {
            ResultSet rs = stm.executeQuery(sql);
            if (rs.next()) {
                Gson gson = new Gson();
                mensajes = gson.fromJson(rs.getString("valor"), JsonObject.class);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mensajes;
    }

     */
    /*
    public JsonObject mensajesComprobante() {
        JsonObject mensajes = new JsonObject();
        String sql = "select valor from wacher_parametros where codigo = 'MENSAJES_FE'";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try (Statement stm = conexion.createStatement();) {
            ResultSet rs = stm.executeQuery(sql);
            if (rs.next()) {
                Gson gson = new Gson();
                mensajes = gson.fromJson(rs.getString("valor"), JsonObject.class);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mensajes;
    }

     */

    public JsonObject hayVentas(long turno, long idResponsable) {
        JsonObject data = new JsonObject();
        data.addProperty("hay_ventas", false);
        data.addProperty("venta_total", 0);
        String sql = "select"
                .concat(" SUM(cmmp.valor_total)")
                .concat(" from")
                .concat(" ct_movimientos cm")
                .concat(" inner join ct_movimientos_medios_pagos cmmp on")
                .concat(" cm.id = cmmp.ct_movimientos_id")
                .concat(" where")
                .concat(" cmmp.ct_medios_pagos_id = 1")
                .concat(" and jornadas_id =?")
                .concat(" and responsables_id =?")
                .concat(" and cm.tipo not in ('016','014')");
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try (PreparedStatement pst = conexion.prepareStatement(sql);) {
            pst.setLong(1, turno);
            pst.setLong(2, idResponsable);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                data.addProperty("hay_ventas", true);
                data.addProperty("venta_total", rs.getLong(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }

    public JsonArray buscarTransminionFE(int sincronicado, String datoAdicional) {
        JsonArray data = new JsonArray();
        String sql = "select * from transmision where sincronizado = ? and coalesce(status,'0')::int != 200 order by  fecha_generado desc " + datoAdicional;
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        try (PreparedStatement stm = conexion.prepareStatement(sql)) {
            stm.setInt(1, sincronicado);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                long id = rs.getInt("id");
                Gson gson = new Gson();
                JsonObject json = gson.fromJson(rs.getString("request"), JsonObject.class);
                long idMovimiento = 0;
                if (json.has("identificadorMovimiento") && !json.get("identificadorMovimiento").isJsonNull()) {
                    idMovimiento = json.get("identificadorMovimiento").getAsLong();
                }
                if (idMovimiento > 0) {
                    JsonArray medios = mediosPagos(idMovimiento);
                    if (medios.size() > 0) {
                        json.add("pagos", medios);
                    }
                }
                json.addProperty("id_transmision", id);
                data.add(json);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }
    //no hace nada con lo dias
    /*
    public JsonArray buscarVentaCliente(int dias) {
        Gson gson = new Gson();
        JsonArray data = new JsonArray();
        String sql = "select * from fnc_conseguir_ventas_cliente() as data;";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            ResultSet rs = pmt.executeQuery();
            while (rs.next()) {
                long id = rs.getLong("id");
                JsonObject json = gson.fromJson(rs.getString("atributos"), JsonObject.class);
                json.addProperty("id_transmision", id);
                json.addProperty("sincronizado", rs.getInt("sincronizado"));
                json.addProperty("actuluziar_venta_combustible", rs.getBoolean("actuluziar_venta_combustible"));
                data.add(json);
            }
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error inesperado -> " + e.getMessage());
        }
        return data;
    }
     */

    public boolean guardarFeTransmision(JsonObject dataP) {
        DatabaseConnectionManager.DatabaseResources resources = null;
        try {
            String sql
                    = "INSERT INTO public.transmision(\n"
                    + "             equipo_id, request, response, sincronizado, fecha_generado, \n"
                    + "            fecha_trasmitido, fecha_ultima, url, method, reintentos, status)\n"
                    + "    VALUES (?, ?::json, NULL, 2, now(), \n"
                    + "            NULL, NULL, ?, ?, 0, 0);";

            resources = DatabaseConnectionManager.createDatabaseResources("lazoexpressregistry", sql);
            resources.getPreparedStatement().setLong(1, Main.credencial.getId());
            resources.getPreparedStatement().setString(2, com.utils.JsonSanitizer.escapeInvalidControlChars(dataP.toString()));
            resources.getPreparedStatement().setString(3, "");
            resources.getPreparedStatement().setString(4, "POST");
            int respuesta = resources.getPreparedStatement().executeUpdate();
            return respuesta > 0;
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            DatabaseConnectionManager.closeDatabaseResources(resources);
        }
    }

    public void actualizarTransmision(long idTransmision, int status, JsonObject response) {
        DatabaseConnectionManager.DatabaseResources resources = null;
        try {
            String sql = "update transmision set sincronizado = ?, status =  ?, reintentos = reintentos + ?, response =?::json where id =?";
            //  CORRECCIÓN: Crear recursos SIN ejecutar
            resources = DatabaseConnectionManager.createDatabaseResources("lazoexpressregistry", sql);
            
            //  Establecer parámetros ANTES de ejecutar
            resources.getPreparedStatement().setInt(1, status != 200 ? 2 : 1);
            resources.getPreparedStatement().setInt(2, status != 200 ? 0 : 200);
            resources.getPreparedStatement().setInt(3, status != 200 ? 1 : 0);
            if (response != null) {
                resources.getPreparedStatement().setString(4, response.toString());
            } else {
                resources.getPreparedStatement().setString(4, "{\"mensaje\":\"ha ocurrido un error en el servicio\"}");
            }
            resources.getPreparedStatement().setLong(5, idTransmision);

            //  AHORA SÍ ejecutar la consulta
            int res = resources.getPreparedStatement().executeUpdate();
            if (res > 0) {
                NovusUtils.printLn(Main.ANSI_GREEN + "actualizacion realizada actualizarTransmision" + Main.ANSI_RESET);
            } else {
                NovusUtils.printLn(Main.ANSI_RED + "error al realizar la actualizacion" + Main.ANSI_RESET);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            DatabaseConnectionManager.closeDatabaseResources(resources);
        }
    }
    /*
    public void actualizarTransmisionAtributosVenta(long idTransmision, JsonObject json) {
        String sql = "update transmision set request=?::json  where id =?";
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        try (PreparedStatement ps = conexion.prepareStatement(sql);) {
            ps.setString(1, json.toString());
            ps.setLong(2, idTransmision);
            int res = ps.executeUpdate();
            if (res > 0) {
                NovusUtils.printLn(Main.ANSI_GREEN + "actualizacion realizada actualizarTransmisionAtributosVenta" + Main.ANSI_RESET);
            } else {
                NovusUtils.printLn(Main.ANSI_RED + "error al realizar la actualizacion" + Main.ANSI_RESET);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     */



    public void actulizarDatosCliente(long id, JsonObject data, boolean actualizarCliente) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
       

        if (findByParameterUseCase.execute()) {
            NovusUtils.printLn("remisionActiva::" + findByParameterUseCase.execute());
            String sql = "select sincronizado from ct_movimientos where id = ?";
            try (PreparedStatement ptmM = conexion.prepareStatement(sql)) {
                ptmM.setLong(1, id);
                ResultSet rs = ptmM.executeQuery();
                if (rs.next()) {
                    actualizarClietneRemision(id, rs.getString("sincronizado"), data);
                }
            } catch (SQLException e) {
                NovusUtils.printLn("error en el metodo actulizarDatosCliente en remision activa " + e.getMessage() + " " + e.getClass().getName());
            }
        } else {
            NovusUtils.printLn("remisionActiva::" + findByParameterUseCase.execute());
            data = validacionTipoDocumentoInfoCliente(data);
            String sql = "update ct_movimientos set atributos=?::json, estado = 'M' where id=?";

            try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
                pmt.setString(1, data.toString());
                pmt.setLong(2, id);
                int status = pmt.executeUpdate();
                if (status > 0 && actualizarCliente) {
                    actulizarDatosClientes(id);
                    NovusUtils.printLn(Main.ANSI_GREEN + "datos del cliente actualizados " + Main.ANSI_RESET);
                } else {
                    if (actualizarCliente) {
                        NovusUtils.printLn(Main.ANSI_RED + "error al actualizar el cliente" + Main.ANSI_RESET);
                    }
                }
            } catch (SQLException e) {
                NovusUtils.printLn("ha ocurrido un error inesperado -> " + e.getMessage());
            }
        }
    }

    public JsonObject validacionTipoDocumentoInfoCliente(JsonObject info) {
        String tipoDocumento = "";
        if (info.has("cliente")) {
            if (info.get("cliente").getAsJsonObject().has("tipoDocumento")) {
                tipoDocumento = tipoDocumento(info.get("cliente").getAsJsonObject().get("tipoDocumento").getAsInt());
                info.get("cliente").getAsJsonObject().addProperty("descripcionTipoDocumento", tipoDocumento);
            }
        }
        return info;
    }

    public String tipoDocumento(int id) {
        for (TiposDocumentos tipo : TiposDocumentos.values()) {
            if (id == tipo.getValor()) {
                return tipo.getDescripcion();
            }
        }
        return "";
    }

    private void actulizarDatosClientes(long idMovimiento) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "call prc_registrar_cliente_movimiento(?, 0, 2, '{}'::json)";
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, idMovimiento);
            pmt.execute();
        } catch (Exception e) {
            NovusUtils.printLn(Main.ANSI_RED + "Ha ocurrido un error inisperaro el metodo actulizarDatosClientes " + e.getMessage() + Main.ANSI_RESET);
        }
    }

    private void actualizarClietneRemision(long id, String sincronizado, JsonObject data) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        System.out.println("este es el sincronizado de la venta " + sincronizado);
        String estado = sincronizado.equals("1") ? "M" : "A";
        System.out.println(" este es el estado de la venta " + estado);
        String sql = "update ct_movimientos set atributos=?::json, estado=? where id=?";
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setString(1, data.toString());
            pmt.setString(2, estado);
            pmt.setLong(3, id);
            int status = pmt.executeUpdate();
            if (status > 0) {
                NovusUtils.printLn(Main.ANSI_GREEN + "datos del cliente actualizados " + Main.ANSI_RESET);
            } else {
                NovusUtils.printLn(Main.ANSI_RED + "error al actualizar el cliente" + Main.ANSI_RESET);
            }
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error inesperado -> " + e.getMessage());
        }
    }

    // public long obtenerIdTransmisionDesdeMovimiento(long idMovimiento) {
    //     long idTransmision = 0;
        
    //     System.out.println(" DEBUG obtenerIdTransmisionDesdeMovimiento():");
    //     System.out.println("    - Buscando idTransmision para idMovimiento: " + idMovimiento);
        
    //     // 🔧 CORRECCIÓN: Implementar mapeo inverso según respuesta chat 14.3.12
        
    //     // 1. Primero intentar en ct_movimientos_cliente (para datos de cliente)
    //     String sql1 = "SELECT id_transmision FROM ct_movimientos_cliente WHERE id_movimiento = ?";
    //     try (Connection conexion = Main.obtenerConexion("lazoexpresscore");
    //          PreparedStatement pst = conexion.prepareStatement(sql1)) {
    //         pst.setLong(1, idMovimiento);
    //         System.out.println("    - Consultando ct_movimientos_cliente: " + pst.toString());
    //         ResultSet rs = pst.executeQuery();
    //         if (rs.next()) {
    //             idTransmision = rs.getLong("id_transmision");
    //             System.out.println("    -  Encontrado en ct_movimientos_cliente: " + idTransmision);
    //             return idTransmision;
    //         }
    //     } catch (SQLException e) {
    //         System.err.println(" Error en ct_movimientos_cliente: " + e.getMessage());
    //     }
        
    //     // 2. Si no está en cliente, intentar en transmisiones_remision (para remisiones)
    //     String sql2 = "SELECT id_transmicion_remision FROM transmisiones_remision WHERE id_movimiento = ?";
    //     try (Connection conexion = Main.obtenerConexion("lazoexpresscore");
    //          PreparedStatement pst = conexion.prepareStatement(sql2)) {
    //         pst.setLong(1, idMovimiento);
    //         System.out.println("    - Consultando transmisiones_remision: " + pst.toString());
    //         ResultSet rs = pst.executeQuery();
    //         if (rs.next()) {
    //             idTransmision = rs.getLong("id_transmicion_remision");
    //             System.out.println("    -  Encontrado en transmisiones_remision: " + idTransmision);
    //             return idTransmision;
    //         }
    //     } catch (SQLException e) {
    //         System.err.println(" Error en transmisiones_remision: " + e.getMessage());
    //     }
        
    //     // 3. Finalmente intentar en transmision (para FE normal) - requiere búsqueda en JSON
    //     String sql3 = "SELECT id FROM transmision WHERE request::json->>'identificadorMovimiento' = ?";
    //     try (Connection conexion = Main.obtenerConexion("lazoexpressregistry");
    //          PreparedStatement pst = conexion.prepareStatement(sql3)) {
    //         pst.setString(1, String.valueOf(idMovimiento));
    //         System.out.println("    - Consultando transmision (JSON): " + pst.toString());
    //         ResultSet rs = pst.executeQuery();
    //         if (rs.next()) {
    //             idTransmision = rs.getLong("id");
    //             System.out.println("    -  Encontrado en transmision: " + idTransmision);
    //             return idTransmision;
    //         }
    //     } catch (SQLException e) {
    //         System.err.println(" Error en transmision: " + e.getMessage());
    //     }
        
    //     System.err.println("⚠ No se encontró idTransmision para idMovimiento: " + idMovimiento + " en ninguna tabla");
    //     return idTransmision;
    // }

    // MÉTODO SOBRECARGADO: Nuevo método con fallback para cuando numeroVenta es 0
    public boolean updateVentasSinFacturar(JsonObject cliente, JsonObject datosCliente, long numeroVenta, boolean datafono, boolean appterpelPendiente, long idMovimiento) {
        
        System.out.println(" DEBUG updateVentasSinFacturar() con fallback:");
        System.out.println("    - numeroVenta recibido: " + numeroVenta);
        System.out.println("    - idMovimiento recibido: " + idMovimiento);
        System.out.println("    - datafono: " + datafono);
        System.out.println("    - appterpelPendiente: " + appterpelPendiente);
        
        // FALLBACK: Si numeroVenta es 0, intentar obtenerlo desde el idMovimiento
        if (numeroVenta == 0 && idMovimiento > 0) {
            System.out.println(" Aplicando fallback: numeroVenta es 0, intentando obtener desde idMovimiento...");
            obtenerIdTransmisionDesdeMovimientoUseCase.setIdMovimiento(idMovimiento);
            long idTransmisionObtenido = obtenerIdTransmisionDesdeMovimientoUseCase.execute();
            if (idTransmisionObtenido > 0) {
                numeroVenta = idTransmisionObtenido;
                System.out.println(" Fallback exitoso: nuevo numeroVenta = " + numeroVenta);
            } else {
                System.err.println(" Fallback falló: No se encontró idTransmision para idMovimiento " + idMovimiento);
                return false;
            }
        }
        
        // Llamar al método original con el numeroVenta corregido
        return updateVentasSinFacturar(cliente, datosCliente, numeroVenta, datafono, appterpelPendiente);
    }

    public boolean updateVentasSinFacturar(JsonObject cliente, JsonObject datosCliente, long numeroVenta, boolean datafono, boolean appterpelPendiente) {
        boolean ventas = false;
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        String consulta = "select * from transmision where id=?";
        try {
            PreparedStatement pstm = conexion.prepareStatement(consulta);
            pstm.setLong(1, numeroVenta);
            ResultSet rs = pstm.executeQuery();
            JsonObject atributos = new JsonObject();
            if (rs.next()) {
                JsonObject data = new Gson().fromJson(rs.getString("request"), JsonObject.class);

                if (data != null) {
                    atributos = data;
                }

            }
            NovusUtils.printLn("datos de la venta -> " + atributos);
            NovusUtils.printLn("[numeroVenta] " + numeroVenta);
            
            //  DEBUG: Mostrar el JSON completo para análisis
            NovusUtils.printLn(" JSON completo de atributos: " + atributos.toString());
            
            //  Buscar ID de movimiento con diferentes nombres posibles
            Long idMovimiento = buscarIdMovimientoEnJson(atributos);
            if (idMovimiento == null) {
                NovusUtils.printLn(" No se pudo encontrar ningún ID de movimiento válido en el JSON");
                return false;
            }
            
            NovusUtils.printLn(" ID de movimiento encontrado: " + idMovimiento);
            JsonObject json = datosCliente(idMovimiento);
            NovusUtils.printLn("atributos ------------------->" + json);
            json.add("cliente", cliente);
            json.addProperty("personas_nombre", cliente.has("nombreRazonSocial") ? cliente.get("nombreRazonSocial").getAsString() : "CONSUMIDOR FINAL");
            json.addProperty("asignarCliente", cliente.has("nombreRazonSocial"));
            String numeroDocumento = cliente.has("numeroDocumento") ? cliente.get("numeroDocumento").getAsString() : "222222222222L";
            json.addProperty("personas_identificacion", numeroDocumento);
            if (!appterpelPendiente) {
                actulizarDatosCliente(idMovimiento, json, true);
                String sql = "update transmision set request=?::json, sincronizado = ? where id=?";
                    try (PreparedStatement pst = conexion.prepareStatement(sql);) {
                    if (datosCliente == null) {
                        NovusUtils.printLn(" El objeto datosCliente es nulo, no se puede agregar al request.");
                        return false;
                    }
                    atributos.add("cliente", datosCliente);                    atributos.add("cliente", datosCliente);
                    pst.setString(1, atributos.toString());
                    pst.setInt(2, datafono ? 4 : 2);
                    pst.setLong(3, numeroVenta);
                    NovusUtils.printLn("[updateVentasSinFacturar]  " + pst.toString());
                    int resp = pst.executeUpdate();
                    if (resp > 0) {
                        NovusUtils.printLn("[updateVentasSinFacturar]  Cliente facturado ");
                        ventas = true;
                    } else {
                        NovusUtils.printLn("[updateVentasSinFacturar] Error al facturar ");
                        ventas = false;
                    }
                } catch (SQLException e) {
                    NovusUtils.printLn("[updateVentasSinFacturar]ha ocurrido un error al agregar al cliente" + sql);
                }
            }

        } catch (SQLException e) {
            NovusUtils.printLn("[updateVentasSinFacturar]ha ocurrido un error al agregar al cliente" + e);
        }

        return ventas;
    }
    /*
    public Long obtenerIdRemisionDesdeMovimiento(long idMovimiento) {
        Long idRemision = null;
        String query = "SELECT id_transmicion_remision FROM transmisiones_remision WHERE id_movimiento = ?";
        try (Connection con = Main.obtenerConexion("lazoexpresscore");
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setLong(1, idMovimiento);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                idRemision = rs.getLong("id_transmicion_remision");
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo id_transmision_remision: " + e.getMessage());
        }
        return idRemision;
    }

     */


    public boolean updateRemisionSinFacturar(JsonObject cliente, JsonObject datosCliente, long numeroVenta) {
        boolean ventas = false;
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String consulta = "SELECT * FROM transmisiones_remision WHERE id_transmicion_remision = ?";

        try {
            PreparedStatement pstm = conexion.prepareStatement(consulta);
            pstm.setLong(1, numeroVenta);
            ResultSet rs = pstm.executeQuery();

            JsonObject atributos = new JsonObject();
            long idMovimientoRemision = 0;

            if (rs.next()) {
                JsonObject data = new Gson().fromJson(rs.getString("request"), JsonObject.class);
                idMovimientoRemision = rs.getLong("id_movimiento");
                if (data != null) {
                    atributos = data;
                }
            } else {
                System.err.println(" No se encontró remisión con ID: " + numeroVenta);
                return false;
            }

            System.out.println(" Datos de la venta (atributos): " + atributos.toString());

            // Validación de movimientoId
            if (!atributos.has("movimientoId") || atributos.get("movimientoId").isJsonNull()) {
                System.err.println(" 'movimientoId' no está presente o es null en atributos.");
                return false;
            }

            // Validación de transaccion.atributos
            if (!atributos.has("transaccion") ||
                    !atributos.get("transaccion").getAsJsonObject().has("atributos")) {
                System.err.println(" 'transaccion.atributos' no está disponible en los datos de la remisión.");
                return false;
            }

            long idMovimiento = atributos.get("movimientoId").getAsLong();
            JsonObject json = datosCliente(idMovimiento);
            System.out.println("🧾 Atributos del cliente actuales: " + json);

            json.add("cliente", cliente);
            json.addProperty("personas_nombre", cliente.has("nombreRazonSocial") ?
                    cliente.get("nombreRazonSocial").getAsString() : "CONSUMIDOR FINAL");

            String numeroDocumento = cliente.has("numeroDocumento") ?
                    cliente.get("numeroDocumento").getAsString() : "222222222222L";

            json.addProperty("personas_identificacion", numeroDocumento);

            // Actualizamos cliente
            actulizarDatosCliente(idMovimiento, json, true);

            // Actualizar la remisión con los nuevos datos
            atributos.get("transaccion").getAsJsonObject()
                    .get("atributos").getAsJsonObject()
                    .add("cliente", cliente);

            String updateSql = "UPDATE transmisiones_remision SET sincronizado = 2, request = ?::json WHERE id_transmicion_remision = ?";
            PreparedStatement pst = conexion.prepareStatement(updateSql);
            pst.setString(1, atributos.toString());
            pst.setLong(2, numeroVenta);
            int resp = pst.executeUpdate();

            if (resp > 0) {
                System.out.println(Main.ANSI_GREEN + " Cliente facturado correctamente en remisión." + Main.ANSI_RESET);
                ventas = true;
            } else {
                System.out.println(Main.ANSI_RED + " No se pudo actualizar la remisión." + Main.ANSI_RESET);
            }

        } catch (SQLException e) {
            System.err.println(" Error SQL al agregar cliente en remisión: " + e.getMessage());
        } catch (Exception e) {
            System.err.println(" Error inesperado al actualizar remisión: " + e.getMessage());
            e.printStackTrace();
        }

        return ventas;
    }


    private JsonObject datosCliente(long id) {
        String sql = "select atributos from ct_movimientos cm where cm.id = ?";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        JsonObject datos = new JsonObject();
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, id);
            ResultSet rs = pmt.executeQuery();
            Gson gson = new Gson();
            while (rs.next()) {
                datos = gson.fromJson(rs.getString("atributos"), JsonObject.class);
            }
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error en el metodo datosCliente -> " + e.getMessage());
        }
        return datos;
    }

    private JsonArray mediosPagos(long id) {
        String sql = "select * from ct_movimientos_medios_pagos cp where cp.ct_movimientos_id = ?";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        JsonArray datos = new JsonArray();
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, id);
            ResultSet rs = pmt.executeQuery();
            Gson gson = new Gson();
            while (rs.next()) {
                JsonObject pago = new JsonObject();
                pago.addProperty("medios_pagos_id", rs.getLong("ct_medios_pagos_id"));
                pago.addProperty("valor", rs.getFloat("valor_total"));
                pago.addProperty("recibido", rs.getFloat("valor_recibido"));
                pago.addProperty("cambio", rs.getFloat("valor_cambio"));
                datos.add(pago);
            }
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error en el metodo mediosPagos -> " + e.getMessage());
        }
        return datos;
    }

    /**
     * Busca el ID de movimiento en un JSON con diferentes nombres posibles
     * @param json JSON donde buscar el ID
     * @return ID de movimiento o null si no se encuentra
     */
    private Long buscarIdMovimientoEnJson(JsonObject json) {
        // Lista de posibles nombres para el ID de movimiento
        String[] posiblesNombres = {
            "identificadorMovimiento",
            "idMovimiento", 
            "movimientoId",
            "movimiento_id",
            "id_movimiento",
            "idTransaccionVenta",
            "transaccionId",
            "numeroMovimiento"
        };
        
        for (String nombre : posiblesNombres) {
            if (json.has(nombre) && !json.get(nombre).isJsonNull()) {
                try {
                    return json.get(nombre).getAsLong();
                } catch (Exception e) {
                    NovusUtils.printLn(" Error al convertir " + nombre + " a Long: " + e.getMessage());
                }
            }
        }
        
        // Si no encuentra nada, buscar en objetos anidados
        if (json.has("transaccion") && json.get("transaccion").isJsonObject()) {
            JsonObject transaccion = json.get("transaccion").getAsJsonObject();
            for (String nombre : posiblesNombres) {
                if (transaccion.has(nombre) && !transaccion.get(nombre).isJsonNull()) {
                    try {
                        return transaccion.get(nombre).getAsLong();
                    } catch (Exception e) {
                        NovusUtils.printLn(" Error al convertir transaccion." + nombre + " a Long: " + e.getMessage());
                    }
                }
            }
        }
        
        return null;
    }

    public boolean guardarProcesoFidelizacionSinInternet(ParamsLoyaltySinInternet paramsLoyaltySinInternet) {
        String sql = "select * from fnc_insertar_transaccion_datos_cliente(?::json);";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        Gson gson = new Gson();
        String json = gson.toJson(paramsLoyaltySinInternet);
        boolean response = false;
        try (PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setString(1, json);

            ResultSet resultSet = pst.executeQuery();
            while (resultSet.next()) {
                response = resultSet.getBoolean("fnc_insertar_transaccion_datos_cliente");
            }
        } catch (SQLException ex) {
            NovusUtils.printLn("Error al guardar " + "la transmision de la fidelizacion " + ex.getMessage());
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;

    }
    // esta funcion no esta en uso
    public void guardarTransmisionFidelizacion(JsonObject dataFidelizacion, String urlAcumulacion, String urlValidacion, String metodo, Long empresasId, Long idVenta, int estado, int reintentos) {
        String sql
                = "INSERT INTO public.transacciones_fidelizaciones".concat(" (id_empresa, id_movimiento, estado_transaccion, ")
                .concat(" metodo, url_acumulacion, url_validacion, request, ")
                .concat(" response, reintentos, fecha_reintento)")
                .concat(" VALUES(?, ?, ?, ?, ?, ?, (?)::json, NULL, ?, NULL);");
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        Gson gson = new Gson();
        gson.toJson(dataFidelizacion);
        try (PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setLong(1, empresasId);
            pst.setLong(2, idVenta);
            pst.setInt(3, estado);
            pst.setString(4, metodo);
            pst.setString(5, urlAcumulacion);
            pst.setString(6, urlValidacion);
            pst.setString(7, dataFidelizacion.toString());
            pst.setInt(8, reintentos);
            pst.executeUpdate();
            actulizarMOvimientoFidelizacion(idVenta, true);
        } catch (SQLException ex) {
            NovusUtils.printLn("Error al guardar " + "la transmision de la fidelizacion " + ex.getMessage());
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void actulizarMOvimientoFidelizacion(long idMovimiento, boolean editarFidelizacion) {
        DatabaseConnectionManager.DatabaseResources selectResources = null;
        DatabaseConnectionManager.DatabaseResources updateResources = null;
        JsonObject data = null;

        try {
            String selectSql = "select atributos from ct_movimientos where id = ?";
            selectResources = DatabaseConnectionManager.createDatabaseResources("lazoexpresscore", selectSql);
            selectResources.getPreparedStatement().setLong(1, idMovimiento);
            selectResources = DatabaseConnectionManager.executeQuery(selectResources);

            if (selectResources.getResultSet().next()) {
                Gson gson = new Gson();
                data = gson.fromJson(selectResources.getResultSet().getString("atributos"), JsonObject.class);
            }
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error en inesperado -> " + e.getMessage());
        } finally {
            DatabaseConnectionManager.closeDatabaseResources(selectResources);
        }
        if (data != null) {
            try {
                String sqlUpdate = "update ct_movimientos set atributos = ?::json where id = ?";
                updateResources = DatabaseConnectionManager.createDatabaseResources("lazoexpresscore", sqlUpdate);
                data.addProperty("fidelizada", "S");
                data.addProperty("editarFidelizacion", editarFidelizacion);
                updateResources.getPreparedStatement().setString(1, data.toString());
                updateResources.getPreparedStatement().setLong(2, idMovimiento);
                int resp = updateResources.getPreparedStatement().executeUpdate();
                if (resp > 0) {
                    NovusUtils.printLn("******************* datos actulizados para la fidelizacion ***********************");
                } else {
                    NovusUtils.printLn("********** error al actulizar los datos de la fidelizacion  **********************");
                }
            } catch (Exception e) {
                NovusUtils.printLn("ha ocurrido inesperado -> " + e.getMessage());
            } finally {
                DatabaseConnectionManager.closeDatabaseResources(updateResources);
            }
        } else {
            NovusUtils.printLn("no se encontroron los atroibutos para actulizar");
        }

    }

    /*
    public boolean existeFidelizacion(Long idVenta) {
        boolean result = false;
        String sql = "select *".concat(" from procesos.tbl_transaccion_proceso").concat(" where id_movimiento = ? and id_integracion = 3  and id_estado_integracion = 7 and id_estado_proceso = 3");
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try (PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setLong(1, idVenta);
            ResultSet rs = pst.executeQuery();
            result = rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

     */

    /*
    public boolean isVentaFidelizada(Long idVenta) {
        boolean result = false;
        String sql = "select *".concat(" from procesos.tbl_transaccion_proceso").concat(" where id_movimiento = ? and id_integracion = 3  and id_estado_integracion = 6 and id_estado_proceso = 3");
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try (PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setLong(1, idVenta);
            ResultSet rs = pst.executeQuery();
            result = rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
     */

    public JsonObject getRequest(Long idVenta) {
        JsonObject request = new JsonObject();
        String sql = "select request::json".concat(" from transacciones_fidelizaciones ").concat(" where id_movimiento = ?");
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try (PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setLong(1, idVenta);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                request = new Gson().fromJson(rs.getString("request"), JsonObject.class);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return request;
    }
    // esta funcion no esta en uso
    public void actualizarFidelizacion(Long idVenta, String identificacion, String tipo) {
        String identidi = encriptacionBase64AES256(identificacion);
        JsonObject atributos = getRequest(idVenta);

        JsonObject identificaionCliente = new JsonObject();
        identificaionCliente.addProperty("codigoTipoIdentificacion", tipo);
        identificaionCliente.addProperty("numeroIdentificacion", identidi);

        JsonObject body = atributos.get("body").getAsJsonObject();
        body.add("identificacionCliente", identificaionCliente);
        atributos.add("body", body);

        String sql = "update transacciones_fidelizaciones "
                .concat(" set request = (?)::json, reintentos = 1, estado_transaccion = 0 ")
                .concat(" where id_movimiento = ?");
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try (PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setString(1, atributos.toString());
            pst.setLong(2, idVenta);
            pst.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<InformacionFidelizacionRetenida> getInformacionFidelizacion() {
        List<InformacionFidelizacionRetenida> informarFidelizacionesRetenidas = new ArrayList<>();
        String sql = "select * from  procesos.obtener_fidelizaciones_retenidas_viveterpel()";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try (Statement stm = conexion.createStatement()) {
            ResultSet rs = stm.executeQuery(sql);
            DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            while (rs.next()) {


                JsonArray resultArray = Main.gson.fromJson(rs.getString("obtener_fidelizaciones_retenidas_viveterpel"), JsonArray.class) == null ? new JsonArray() : Main.gson.fromJson(rs.getString("obtener_fidelizaciones_retenidas_viveterpel"), JsonArray.class);
                String rawJson = rs.getString("obtener_fidelizaciones_retenidas_viveterpel");
                System.out.println(">>> JSON crudo recibido: " + rawJson);
                for (JsonElement resultElem : resultArray) {
                    JsonObject result = resultElem.getAsJsonObject();

                    InformacionFidelizacionRetenida infoFidelizacionRetenida = new InformacionFidelizacionRetenida();
                    infoFidelizacionRetenida.setIdMovimiento(result.get("idMovimiento").getAsLong());

                    String dateStr = result.get("fechaTransaccion").getAsString();
                    LocalDateTime dateFormatter = LocalDateTime.parse(dateStr, inputFormatter);
                    String dateFormatterStr = dateFormatter.format(DateTimeFormatter.ofPattern("d-MM hh:mm a"));

                    infoFidelizacionRetenida.setFechaTransaccion(dateFormatterStr);
                    infoFidelizacionRetenida.setOrigenVenta(result.get("origenVenta").getAsString());
                    infoFidelizacionRetenida.setDescripcionNegocio(result.get("descripcionNegocio").getAsString());
                    infoFidelizacionRetenida.setPagoTotal(result.get("pagoTotal").getAsLong());
                    JsonArray productosArray = result.getAsJsonArray("productos");

                    List<InformacionVentaFidelizacionRetenida> productos = new ArrayList<>();
                    for (JsonElement jsonElement : productosArray) {
                        JsonObject infoVenta = jsonElement.getAsJsonObject();
                        InformacionVentaFidelizacionRetenida info = new InformacionVentaFidelizacionRetenida();
                        info.setCantidadProducto(infoVenta.get("cantidadProducto").getAsDouble());
                        info.setIdentificacionProducto(infoVenta.get("identificacionProducto").getAsString());
                        info.setValorUnitarioProducto(infoVenta.get("valorUnitarioProducto").getAsDouble());
                        productos.add(info);
                    }

                    infoFidelizacionRetenida.setProductos(productos);

                    informarFidelizacionesRetenidas.add(infoFidelizacionRetenida);
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return informarFidelizacionesRetenidas;
    }
/*
    public void setReaperturaInOne(Long idVenta) throws SQLException {
        String sql = "UPDATE procesos.tbl_transaccion_proceso SET reapertura  = 1 WHERE id_integracion = 3 and id_movimiento = ?;";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        PreparedStatement pst = conexion.prepareStatement(sql);
        pst.setLong(1, idVenta);
        pst.executeUpdate();
    }

 */

    // public boolean isValidEdit(Long idVenta) {
    //     int reapertura = 0;
    //     String sql = "select reapertura"
    //             .concat(" from procesos.tbl_transaccion_proceso ")
    //             .concat(" where id_movimiento = ? ")
    //             .concat(" and id_integracion  = 3");
    //     Connection conexion = Main.obtenerConexion("lazoexpresscore");
    //     try (PreparedStatement pst = conexion.prepareStatement(sql)) {
    //         pst.setLong(1, idVenta);
    //         NovusUtils.printLn("Consulta isValidEdit");
    //         NovusUtils.printLn("SQL: " + pst.toString());
    //         ResultSet rs = pst.executeQuery();

    //         if (rs.next()) {
    //             reapertura = rs.getInt("reapertura");
    //         }

    //     } catch (SQLException ex) {
    //         Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
    //     }
    //     NovusUtils.printLn("\u001B[33m @@@@@@@Reapertura@@@@@@@@: " + (reapertura < 1) + "\u001B[0m");
    //     return reapertura < 1;
    // }
    // Se remplaza por el use case de la transaccion proceso FinByTransaccionProcesoUseCase

    public JsonArray getInventarioTeorico(int idMovimiento, int idTanque, String fechaInicial, String fechaFinal, int length) throws SQLException {
        JsonArray info = new JsonArray();
        PreparedStatement ps = null;
        try {
            Connection conexion = Main.obtenerConexion("lazoexpresscore");
            String sql
                    = "select it.id_inventario_teorico,".concat(" tp.descripcion as movimiento,")
                    .concat(" it.fecha, cb.bodega as bodega, it.isla, it.cara,")
                    .concat(" it.manguera, pf.codigo as familia,")
                    .concat(" coalesce ((select p.descripcion from productos p ")
                    .concat(" where it.id_producto = p.id), '' ) as producto,")
                    .concat(" it.cantidad, it.valor, it.lectura_inicial,")
                    .concat(" it.lectura_final")
                    .concat(" from inventario_teorico it")
                    .concat(" inner join tipos_movimiento tp")
                    .concat(" on it.id_tipo_movimiento = tp.id_tipo_movimiento")
                    .concat(" inner join productos_familias pf")
                    .concat(" on it.id_familia = pf.id")
                    .concat(" inner join ct_bodegas cb on cb.id = it.id_bodega ");
            if ((idMovimiento > 0) && (idTanque > 0)) {
                sql
                        = sql
                        .concat(" where it.id_tipo_movimiento = ?")
                        .concat(" and it.id_bodega = ?")
                        .concat(" and it.fecha >= ? ")
                        .concat(" and it.fecha <= ? ")
                        .concat(" order by it.fecha desc")
                        .concat(" fetch first ? rows only");

                ps = conexion.prepareStatement(sql);
                ps.setInt(1, idMovimiento);
                ps.setInt(2, idTanque);
                ps.setTimestamp(3, Timestamp.valueOf(fechaInicial));
                ps.setTimestamp(4, Timestamp.valueOf(fechaFinal));
                ps.setInt(5, length);
            } else if ((idMovimiento > 0) && (idTanque == 0)) {
                sql = sql.concat(" where it.id_tipo_movimiento = ?").concat(" and it.fecha >= ? ").concat(" and it.fecha <= ? ").concat(" order by it.fecha desc").concat(" fetch first ? rows only");
                ps = conexion.prepareStatement(sql);
                ps.setInt(1, idMovimiento);
                ps.setTimestamp(2, Timestamp.valueOf(fechaInicial));
                ps.setTimestamp(3, Timestamp.valueOf(fechaFinal));
                ps.setInt(4, length);
            } else if ((idMovimiento == 0) && (idTanque > 0)) {
                sql = sql.concat(" where it.id_bodega = ?").concat(" and it.fecha >= ? ").concat(" and it.fecha <= ? ").concat(" order by it.fecha desc").concat(" fetch first ? rows only");

                ps = conexion.prepareStatement(sql);
                ps.setInt(1, idTanque);
                ps.setTimestamp(2, Timestamp.valueOf(fechaInicial));
                ps.setTimestamp(3, Timestamp.valueOf(fechaFinal));
                ps.setInt(4, length);
            } else {
                sql = sql.concat("where it.fecha >= ?").concat(" and it.fecha <= ? ").concat(" order by it.fecha desc").concat(" fetch first ? rows only");
                ps = conexion.prepareStatement(sql);
                ps.setTimestamp(1, Timestamp.valueOf(fechaInicial));
                ps.setTimestamp(2, Timestamp.valueOf(fechaFinal));
                ps.setInt(3, length);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                JsonObject data = new JsonObject();
                data.addProperty("id", rs.getInt("id_inventario_teorico"));
                data.addProperty("movimiento", rs.getString("movimiento"));
                data.addProperty("fecha", sf.format(rs.getTimestamp("fecha")));
                data.addProperty("bodega", rs.getString("bodega"));
                data.addProperty("isla", rs.getInt("isla"));
                data.addProperty("cara", rs.getInt("cara"));
                data.addProperty("manguera", rs.getInt("manguera"));
                data.addProperty("familia", rs.getString("familia"));
                data.addProperty("producto", rs.getString("producto"));
                data.addProperty("cantidad", rs.getFloat("cantidad"));
                data.addProperty("valor", rs.getFloat("valor"));
                data.addProperty("lecturaInicial", rs.getFloat("lectura_inicial"));
                data.addProperty("lecturaFinal", rs.getFloat("lectura_final"));
                info.add(data);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
        return info;
    }
    /*
        public TreeMap<Integer, String> getTanques() {
            TreeMap<Integer, String> tanque = new TreeMap<>();
            Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");
            String sql = "select cb.id, cb.bodega from ct_bodegas cb";
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    tanque.put(rs.getInt("id"), rs.getString("bodega"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return tanque;
        }

     */
        /*
        public TreeMap<Integer, String> getTipoMovimiento() {
            TreeMap<Integer, String> movimiento = new TreeMap<>();
            Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");
            String sql = "select tm.id_tipo_movimiento as id, tm.descripcion from tipos_movimiento tm";
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    movimiento.put(rs.getInt("id"), rs.getString("descripcion"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return movimiento;
        }
         */
    // esta funcion no esta en uso
    public JsonArray getInfoProductosArqueo(long id) {
        JsonArray info = new JsonArray();
        String sql
                = "select".concat(" array_to_json(array_agg(row_to_json(t)))as data")
                .concat(" from")
                .concat(" (")
                .concat(" select")
                .concat(" p2.nombre,")
                .concat(" (case")
                .concat(" when cm.tipo in ('017','016') then 'combustible'")
                .concat(" when cm.tipo = '035' then 'kiosco'")
                .concat(" when cm.tipo = '009' then 'canastilla'")
                .concat(" when cm.tipo = '014' then 'calibracion'")
                .concat(" end )tipo ,")
                .concat(" cp.id ,")
                .concat(" coalesce ((select pf.codigo from productos_familias pf inner ")
                .concat(" join productos pts on pts.familias = pf.id where cp.id = pts.id),")
                .concat(" cp.descripcion)")
                .concat(" as producto,")
                .concat(" (select pt.precio from productos pt where cp.id = pt.id),")
                .concat(" sum(cmd.cantidad) as cantidad,")
                .concat(" sum(cm.venta_total) as total,")
                .concat(" now() as fecha_actual,")
                .concat(" j.fecha_inicio")
                .concat(" from")
                .concat(" ct_movimientos cm")
                .concat(" inner join ct_movimientos_medios_pagos cmmp on")
                .concat(" cm.id = cmmp.ct_movimientos_id")
                .concat(" inner join ct_medios_pagos cmp on")
                .concat(" cmp.id = cmmp.ct_medios_pagos_id")
                .concat(" inner join ct_movimientos_detalles cmd on")
                .concat(" cm.id = cmd.movimientos_id")
                .concat(" inner join personas p2 on")
                .concat(" cm.responsables_id = p2.id")
                .concat(" inner join ct_productos cp on cmd.productos_id = cp.id ")
                .concat(" inner join jornadas j on cm.jornadas_id = j.grupo_jornada")
                .concat(" where")
                .concat(" cm.tipo in('009', '017', '035', '014','016')")
                .concat(" and cmp.id in ('1','4','5','6','8','9','11','12','74','75','76','77',")
                .concat(" '10000','20000','20001','20003','20004')")
                .concat(" and cm.responsables_id = ?")
                .concat(" and j.fecha_inicio <= now()")
                .concat(" group by")
                .concat(" p2.nombre,")
                .concat(" tipo,")
                .concat(" cp.id,")
                .concat(" cmd.cantidad, j.fecha_inicio order by producto) t");
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Gson gson = new Gson();
                info = gson.fromJson(rs.getString("data"), JsonArray.class);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return info;
    }
    // esta funcion no esta en uso
    public JsonArray getTotalMediosNegocio(long id) {
        JsonArray info = new JsonArray();
        String sql
                = "select".concat(" array_to_json(array_agg(row_to_json(t)))as data")
                .concat(" from (select")
                .concat(" (case")
                .concat(" when cmp.id = '1' then 'EFECTIVO'")
                .concat(" when cmp.id in ('4' ,'5' ,'11' ,'12', ")
                .concat(" '74','75','76','77') then 'DATAFONO'")
                .concat(" when cmp.id = '6' then 'BONO SODEXO'")
                .concat(" when cmp.id = '8' then 'RUMBO'")
                .concat(" when cmp.id = '10000' then 'CLIENTES PROPIOS'")
                .concat(" when cmp.id in ('20001','20003') then 'CONSUMO PROPIO'")
                .concat(" when cmp.id = '20000' then 'BONO VIVE TERPEL'")
                .concat(" when cmp.id = '20004' then 'GOPASS'")
                .concat(" when cmp.id = '9' then 'MI EMPRESA'")
                .concat(" end) medio_pago ,")
                .concat(" (case")
                .concat(" when cm.tipo in ('017','016') then 'combustible'")
                .concat(" when cm.tipo = '035' then 'kiosco'")
                .concat(" when cm.tipo = '009' then 'canastilla'")
                .concat(" when cm.tipo = '014' then 'calibracion'")
                .concat(" end )tipo ,")
                .concat(" sum(cm.venta_total) as total")
                .concat(" from ct_movimientos cm")
                .concat(" inner join ct_movimientos_medios_pagos cmmp on")
                .concat(" cm.id = cmmp.ct_movimientos_id")
                .concat(" inner join ct_medios_pagos cmp on")
                .concat(" cmp.id = cmmp.ct_medios_pagos_id")
                .concat(" inner join ct_movimientos_detalles cmd on")
                .concat(" cm.id = cmd.movimientos_id")
                .concat(" inner join personas p2 on")
                .concat(" cm.responsables_id = p2.id")
                .concat(" inner join ct_productos cp on")
                .concat(" cmd.productos_id = cp.id")
                .concat(" inner join jornadas j on")
                .concat(" cm.jornadas_id = j.grupo_jornada")
                .concat(" where cm.tipo in('009', '017', '035', '014', '016')")
                .concat(" and cmp.id in ('1','4','5','6','8','9','11','12','74','75','76','77',")
                .concat(" '10000','20000','20001','20003','20004')")
                .concat(" and cm.responsables_id = ?")
                .concat(" and j.fecha_inicio <= now()")
                .concat(" group by medio_pago, tipo order by medio_pago ) t");
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Gson gson = new Gson();
                info = gson.fromJson(rs.getString("data"), JsonArray.class);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return info;
    }
    // esta funcion no esta en uso
    public JsonObject getTotalSobres(Long id) {
        JsonObject sobres = new JsonObject();
        String sql
                = "select\n".concat(" count(cm.id) as cantidad,")
                .concat(" sum(cm.venta_total) as total")
                .concat(" from")
                .concat(" ct_movimientos cm")
                .concat(" inner join personas p on")
                .concat(" cm.responsables_id = p.id")
                .concat(" inner join jornadas j on")
                .concat(" cm.jornadas_id = j.grupo_jornada")
                .concat(" where")
                .concat(" cm.tipo = '013'")
                .concat(" and cm.responsables_id = ?")
                .concat(" and j.fecha_inicio <= now()");
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        JsonObject info = new JsonObject();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                info.addProperty("cantidad", rs.getString("cantidad"));
                info.addProperty("total", rs.getString("total"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        sobres.add("sobres", info);
        return sobres;
    }
    // esta funcion no esta en uso
    public JsonArray getLogsNotificaciones() {
        JsonArray info = new JsonArray();
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "select\n"
                + "array_to_json(array_agg(row_to_json(t)))as data\n"
                + "from\n"
                + "(\n"
                + "select\n"
                + "id,\n"
                + "tn.descripcion,\n"
                + "id_notificaciones,\n"
                + "logger,\n"
                + "to_char(fecha, 'dd-MM-yyyy HH12:MI AM') as fecha\n"
                + "from\n"
                + "notificaciones_logs\n"
                + "inner join tipo_notificacion tn on\n"
                + "tipo_notificacion_log = tn.tipo\n"
                + "order by id desc) t;";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Gson gson = new Gson();
                info = gson.fromJson(rs.getString("data"), JsonArray.class);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return info;
    }

    public JsonArray getNotificaciones(int notificacion, String fechaInicial, String fechaFinal) {
        JsonArray info = new JsonArray();
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        PreparedStatement ps = null;
        try {
            String sql = "select\n"
                    + "array_to_json(array_agg(row_to_json(t)))as data\n"
                    + "from\n"
                    + "(\n"
                    + "select\n"
                    + "id,\n"
                    + "tn.descripcion,\n"
                    + "id_notificaciones,\n"
                    + "logger,\n"
                    + "to_char(fecha, 'dd-MM-yyyy HH12:MI AM') as fecha\n"
                    + "from\n"
                    + "notificaciones_logs\n"
                    + "inner join tipo_notificacion tn on\n"
                    + "tipo_notificacion_log = tn.tipo\n";
            if (notificacion > 0) {
                sql = sql.concat(" where tipo_notificacion_log = ?")
                        .concat(" and fecha >= ? ")
                        .concat(" and fecha <= ? ")
                        .concat("order by id desc) t;");
                ps = conexion.prepareStatement(sql);
                ps.setInt(1, notificacion);
                ps.setTimestamp(2, Timestamp.valueOf(fechaInicial));
                ps.setTimestamp(3, Timestamp.valueOf(fechaFinal));
            } else {
                sql = sql.concat(" where fecha >= ? ")
                        .concat(" and fecha <= ? ")
                        .concat("order by id desc) t;");
                ps = conexion.prepareStatement(sql);
                ps.setTimestamp(1, Timestamp.valueOf(fechaInicial));
                ps.setTimestamp(2, Timestamp.valueOf(fechaFinal));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Gson gson = new Gson();
                info = gson.fromJson(rs.getString("data"), JsonArray.class);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                    Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return info;
    }

    public TreeMap<Integer, String> getTipoNotificaciones() {
        TreeMap<Integer, String> tanque = new TreeMap<>();
        Connection conexion = Main.obtenerConexionAsync("lazoexpresscore");
        String sql = "select * from tipo_notificacion tn;";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tanque.put(rs.getInt("tipo"), rs.getString("descripcion"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tanque;
    }

    public String crearNotificacion(int tipo_notificacion) {
        String idNotificacion = "NO SE PUDO PROCESAR LA SINCRONIZACION";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "call public.prc_procesar_notificacion(i_tipo_notificacion => ?::bigint, i_data => ?::text,i_prioridad=> ?, o_json_respuesta=>'{}'::json)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            JsonObject data = new JsonObject();
            data.addProperty("tipo", "MANUAL");
            data.addProperty("prioridad", 1);
            data.addProperty("promotor", Main.persona != null ? Main.persona.getNombre() : "");
            data.addProperty("turno", Main.persona != null ? Main.persona.getJornadaId() : 0);
            data.addProperty("version", Main.VERSION_CODE);
            ps.setInt(1, tipo_notificacion);
            ps.setString(2, data.toString());
            ps.setBoolean(3, true);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString("o_json_respuesta"));
                JsonObject info = new Gson().fromJson(rs.getString("o_json_respuesta"), JsonObject.class);
                idNotificacion = info.get("estado").getAsString();
            }
        } catch (Exception ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return idNotificacion;
    }

    // public JsonObject getAlertas(int negocio, String tipoNegocio) {
    //     JsonObject info = new JsonObject();
    //     Connection conexion = Main.obtenerConexion("lazoexpressregistry");
    //     String sql = "select\n"
    //             + "(consecutivo_final - consecutivo_actual) as rangoConsecutivo, \n"
    //             + "(fecha_fin::date - now()::date) as rangoFecha, \n"
    //             + "(cs_atributos::json->>'alerta_dias':: text) as alertaDias, \n"
    //             + "(cs_atributos::json->>'alerta_consecutivos':: text) as alertaConsecutivos \n"
    //             + "from consecutivos where tipo_documento = ? \n"
    //             + "and cs_atributos::json->>'destino' = ? \n"
    //             + "and estado in('A', 'U') order by fecha_fin asc limit 1; ";
    //     try (PreparedStatement ps = conexion.prepareStatement(sql)) {
    //         ps.setInt(1, negocio);
    //         ps.setString(2, tipoNegocio);
    //         ResultSet rs = ps.executeQuery();
    //         while (rs.next()) {
    //             info.addProperty("rangoConsecutivos", rs.getInt("rangoconsecutivo"));
    //             info.addProperty("rangoFecha", rs.getInt("rangofecha"));
    //             info.addProperty("alertaDias", rs.getInt("alertadias"));
    //             info.addProperty("alertaConsecutivos", rs.getInt("alertaconsecutivos"));
    //         }
    //     } catch (SQLException ex) {
    //         Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
    //     }
    //     return info;
    // }

    public long buscarMOvimientoId(long numeroVenta) {
        long id = 0;
        String sql = "select request from transmision where id = ?";
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, numeroVenta);
            NovusUtils.printLn(pmt.toString());
            ResultSet rs = pmt.executeQuery();
            if (rs.next()) {
                Gson gson = new Gson();
                JsonObject data = gson.fromJson(rs.getString("request"), JsonObject.class);
                id = data.get("identificadorMovimiento").getAsLong();
            }
        } catch (Exception e) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, e);
        }
        return id;
    }

    public long buscarMOvimientoIdRemision(long numeroVenta) {
        long id = 0;
        // 🔧 CORRECCIÓN: numeroVenta es el id_transmicion_remision, no el id_movimiento
        String sql = "select * from transmisiones_remision where id_transmicion_remision = ?";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        
        System.out.println(" DEBUG buscarMOvimientoIdRemision():");
        System.out.println("    - Buscando numeroVenta (id_transmicion_remision): " + numeroVenta);
        
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, numeroVenta);
            System.out.println("    - Query: " + pmt.toString());
            ResultSet rs = pmt.executeQuery();
            if (rs.next()) {
                id = rs.getLong("id_movimiento");
                System.out.println("    -  ID movimiento encontrado: " + id);
            } else {
                System.out.println("    -  No se encontró registro para numeroVenta: " + numeroVenta);
            }
        } catch (Exception e) {
            System.err.println(" Error en buscarMOvimientoIdRemision: " + e.getMessage() + " " + e.getClass().getName());
            NovusUtils.printLn("error buscar transmision remision" + e.getMessage() + " " + e.getClass().getName());
        }
        
        System.out.println("    - Resultado final: " + id);
        return id;
    }
    // public boolean remisionActiva() {
    //     Connection conexion = Main.obtenerConexion("lazoexpresscore");
    //     String sql = "select * from wacher_parametros where codigo = 'REMISION'";
    //     try (Statement ps = conexion.createStatement()) {
    //         ResultSet rs = ps.executeQuery(sql);
    //         if (rs.next()) {
    //             String habilitar = rs.getString("valor");
    //             return habilitar.equals("S");
    //         }
    //     } catch (SQLException ex) {
    //         NovusUtils.printLn("error");
    //     }
    //     return false;
    // }
    // public boolean remisionActiva() {
    //     try {
    //         FindAllWacherParametrosUseCase findAllWacherParametrosUseCase = new FindAllWacherParametrosUseCase();
    //         return findAllWacherParametrosUseCase.execute().stream()
    //             .filter(parametro -> "REMISION".equals(parametro.getCodigo()))
    //             .findFirst()
    //             .map(parametro -> "S".equals(parametro.getValor()))
    //             .orElse(false);
    //     } catch (Exception ex) {
    //         NovusUtils.printLn("Error al obtener parámetros: " + ex.getMessage());
    //         return false;
    //     }
    // }
    // esta funcion no esta en uso
    public long tiempoDeFacturacionElectronica() {
        String sql = "select wp.valor  from wacher_parametros wp where codigo  = 'TIEMPO_MAXIMO_DATOS_CLIENTE_FE'";
        long tiempoMaximo = 15;
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        try (Statement smt = conexion.createStatement()) {
            ResultSet rs = smt.executeQuery(sql);
            if (rs.next()) {
                tiempoMaximo = rs.getLong("valor");
            }
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error inisperado en el metodo de tiempoDeFacturacionElectronica() " + e.getMessage());
            return tiempoMaximo;
        }
        return tiempoMaximo;
    }

    public long numeroRemision() {
        long numeroRemision = 0L;
        String sql = findByParameterUseCase.execute() ? "select concat((select concat(empresas_id) from equipos e), max(id + 1))as numero  from movimientos m" : "select max(id + 1)as numero  from movimientos m";
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        try (Statement smt = conexion.createStatement()) {
            ResultSet rs = smt.executeQuery(sql);
            if (rs.next()) {
                numeroRemision = rs.getLong("numero");
                numeroRemision = Long.parseLong(findByParameterUseCase.execute() ? numeroPos() + "" + numeroRemision : numeroRemision + "");
            }
        } catch (Exception e) {
            NovusUtils.printLn("error al consultar el numero de la venta ");
        }
        return numeroRemision;
    }
    // ya se migro a jpa ObtenerNumeroPosUseCase
    public int numeroPos() {
        try {
            // OPTIMIZADO: Usar cache en lugar de consulta directa BD
            com.infrastructure.cache.WacherParametrosCacheSimple cache = 
                com.infrastructure.cache.WacherParametrosCacheSimple.getInstance();
            return cache.getPosId();
        } catch (Exception e) {
            // Fallback a consulta tradicional si el cache falla
            int numeroRemision = 0;
            String sql = "select valor from wacher_parametros p where codigo = 'POS_ID'";
            Connection conexion = Main.obtenerConexion("lazoexpresscore");
            try (Statement smt = conexion.createStatement()) {
                ResultSet rs = smt.executeQuery(sql);
                if (rs.next()) {
                    numeroRemision = rs.getInt("valor");
                }
            } catch (Exception fallbackError) {
                NovusUtils.printLn("error al consultar el numero de la venta (cache y BD fallaron)");
            }
            return numeroRemision;
        }
    }

    public boolean validarVentaEnCurso(int cara) {
        boolean isValida = true;
        int tipo = 0;
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        NovusUtils.printLn("::: Validando Venta :::");
        String sql = "select (t.trama::json->>'tipoVenta')::int tipo "
                + " from ventas_curso vc "
                + " inner join transacciones t on t.id = vc.token_process_id "
                + " where vc.cara = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, cara);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tipo = rs.getInt("tipo");
                isValida = tipo != 2;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        NovusUtils.printLn("::: Es Valida ?::: " + isValida);
        return isValida;
    }

    public float getPrecioEspecial(String plu) {
        float precio = 0f;
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        String sql = "select"
                + " (case when pe.precio = p.precio then 0 else pe.precio"
                + " end) precio"
                + " from precio_especial pe"
                + " inner join productos p on p.plu = ?"
                + " where pe.estado = 'A' "
                + " and (select * from fnc_select_precio_kiosco(pe.fecha_inicio,pe.fecha_fin,pe.hora_inicio,pe.hora_fin))"
                + " and pe.dias_semana @>array[(EXTRACT('dow' FROM now()::date))::int]"
                + " and pe.productos_id = p.id"
                + " and pe.fecha_fin >= now()::date"
                + " and pe.fecha_inicio <= now()::date;";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, plu);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                precio = rs.getFloat("precio");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        return precio;
    }

    public long getIdPrecioEspecial(long id) {
        long idProducto = 0l;
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        String sql = "select\n"
                + "	pe.precio_especial_id as id\n"
                + "from\n"
                + "	precio_especial pe\n"
                + "inner join productos p on\n"
                + "	p.id = ?\n"
                + "where\n"
                + "	pe.estado = 'A'\n"
                + "	and pe.fecha_fin >= now()::date\n"
                + "	and pe.fecha_inicio <= now()::date\n"
                + "	and (select * from fnc_select_precio_kiosco(pe.fecha_inicio,pe.fecha_fin,pe.hora_inicio,pe.hora_fin))\n"
                + "	and pe.dias_semana @>array[(extract('dow'\n"
                + "from\n"
                + "	now()::date))::int]\n"
                + "	and pe.productos_id = p.id;";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                idProducto = rs.getLong("id");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        return idProducto;
    }

    public JsonArray getInfoBodegas() {
        JsonArray info = new JsonArray();
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "select\n"
                + "array_to_json(array_agg(row_to_json(t)))as data\n"
                + "from\n"
                + "(\n"
                + "(select\n"
                + " * \n"
                + "from \n"
                + "ct_bodegas cb \n"
                + "full outer join \n"
                + "ct_bodegas_productos cbp on cb.id = cbp.bodegas_id order by bodegas_id)\n"
                + ") t;";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Gson gson = new Gson();
                info = gson.fromJson(rs.getString("data"), JsonArray.class);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return info;
    }

    public void ingresarAuditoriaMediosPago(long idMovimiento, JsonArray mediosPago, float valorTotalVenta) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "select * from public.fnc_insert_auditoria_medio_pago(?::int, ?::json, ?::numeric);";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setLong(1, idMovimiento);
            ps.setString(2, mediosPago.toString());
            ps.setFloat(3, valorTotalVenta);
            ps.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String unidadProducto(long unidadesId) {
        String sql = "select descripcion from unidades where id = ?";
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, unidadesId);
            ResultSet rs = pmt.executeQuery();
            if (rs.next()) {
                return unidadMedidaCodigo(rs.getString("descripcion"));
            }
        } catch (SQLException e) {
            NovusUtils.printLn("error el emetodo de buscar las entidades del producto -> " + e.getMessage());
        }
        return "94";
    }
    /*
    public String unidadProductoDescripcion(long unidadesId) {
        String sql = "select descripcion from unidades where id = ?";
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, unidadesId);
            ResultSet rs = pmt.executeQuery();
            if (rs.next()) {
                return rs.getString("descripcion");
            } else {
                return "";
            }
        } catch (SQLException e) {
            NovusUtils.printLn("error el emetodo de buscar las entidades del producto -> " + e.getMessage());
            return "";
        }

    }

     */

    private String unidadMedidaCodigo(String unidadMedida) {
        String unidad;
        switch (unidadMedida.toUpperCase()) {
            case "UNIDAD":
                unidad = "94";
                break;
            case "LITROS":
                unidad = CodigosDianUnidad.LTR.name();
                break;
            case "MILILITRO":
                unidad = CodigosDianUnidad.MLT.name();
                break;
            case "DOCENA":
                unidad = CodigosDianUnidad.DZN.name();
                break;
            case "CENTIMETRO":
                unidad = CodigosDianUnidad.CMT.name();
                break;
            case "GRAMOS":
                unidad = CodigosDianUnidad.GGR.name();
                break;
            default:
                unidad = "94";
        }
        return unidad;
    }

    public JsonObject obtenerAtributosVenta(long id) {
        JsonObject data = new JsonObject();
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "select atributos from ct_movimientos cm where id = ?;";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Gson gson = new Gson();
                data = gson.fromJson(rs.getString("atributos"), JsonObject.class);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }

        return data;
    }

    public boolean asignarDatosCliente(String data) {
        boolean respuesta = false;
        Connection conexion = Main.obtenerConexion("lazoexpresscore");

        System.out.println("DDDDDDDDDDDDDDDDDDDDDDDDDDDDDD");
        System.out.println("Asignar Datos Info::: _" + data);
        System.out.println("DDDDDDDDDDDDDDDDDDDDDDDDDDDDDD");

        String sql = "select * from fnc_asignar_datos_cliente(?::json) as info;";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, data);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                respuesta = rs.getBoolean("info");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return respuesta;
    }

    //METODO ESTADO 4 EN TRANSMISION EN REGISTRY
    public void actualizarEstadoTransmision(int sincronizado, long id) {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        String sql = "update transmision set sincronizado = ? where id = ?;";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, sincronizado);
            ps.setLong(2, id);
            NovusUtils.printLn(ps.toString());
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    // esta funcion no esta en uso
    public void actualizarEstadoTransmisionCliente(int sincronizado, long id) {
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "update public.ct_movimientos_cliente set  sincronizado= ? where id_transmision = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, sincronizado);
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void actualizarEstadoMovimientosClientes(int sincronizado, long id) {
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        String sql = "update public.ct_movimientos_cliente set sincronizado = ? where id_transmision = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, sincronizado);
            ps.setLong(2, id);
            NovusUtils.printLn(ps.toString());
            int resultado = ps.executeUpdate();
            if (resultado > 0) {
                NovusUtils.printLn("estado de venta actualizado en ct_movimientos_cliente. estado -> " + sincronizado);
            } else {
                NovusUtils.printLn("estado de venta no actualizado en ct_movimientos_cliente");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void actualizarClienteMovimiento(long idMovimiento, long idTransmision, int sincronizado) {
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "call prc_registrar_cliente_movimiento(?::bigint, ?::bigint, ?::integer, '[]'::json);";
        try (CallableStatement cstm = conexion.prepareCall(sql)) {
            cstm.setLong(1, idMovimiento);
            cstm.setLong(2, idTransmision);
            cstm.setInt(3, sincronizado);
            NovusUtils.printLn(cstm.toString());
            cstm.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /*
    public JsonArray buscarTransminionRemision(int sincronizado, String datoAdicional) {
        JsonArray data = new JsonArray();
        String sql = "select * from transmisiones_remision where sincronizado = ?" + datoAdicional;
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try (PreparedStatement stm = conexion.prepareStatement(sql)) {
            stm.setInt(1, sincronizado);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Gson gson = new Gson();
                JsonObject json = gson.fromJson(rs.getString("request"), JsonObject.class);
                json.addProperty("id_transmision", rs.getLong("id_transmicion_remision"));
                data.add(json);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }
     */

    // public int buscarTiempoMaximoDatosCliente() {
    //     int tiempoMaximo = 5;
    //     String sql = "select wp.valor::int tiempo from public.wacher_parametros wp  where wp.codigo = 'TIEMPO_MAXIMO_DATOS_CLIENTE_FE';";
    //     Connection conexion = Main.obtenerConexion("lazoexpresscore");
    //     try (PreparedStatement pstm = conexion.prepareStatement(sql)) {
    //         ResultSet rs = pstm.executeQuery();
    //         while (rs.next()) {
    //             tiempoMaximo = rs.getInt("tiempo");
    //         }
    //     } catch (SQLException ex) {
    //         Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
    //     }

    //     return tiempoMaximo;
    // }
    

    public int getCodigoAutorizacion() {
        int codigo = 0;
        String sql = "select nextval ('\"datafonos\".sq_transacciones_pagos'::regclass) codigo;";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try (PreparedStatement pstm = conexion.prepareStatement(sql)) {
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                codigo = rs.getInt("codigo");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo;
    }

    public JsonObject obtenerDatosVentaPendienteDatafono(long idTransacionDatafono) {
        JsonObject data = new JsonObject();
        String sql = "select td.id_transaccion_estado, td.descripcion, d.id_adquiriente, a.descripcion  as proveedor from datafonos.transacciones t \n"
                + "inner join datafonos.transacciones_estado td on t.id_transaccion_estado = td.id_transaccion_estado\n"
                + "inner join datafonos.datafonos d on t.id_datafono = d.id_datafono\n"
                + "inner join datafonos.adquirientes a on a.id_adquiriente = d.id_adquiriente \n"
                + "where  t.id_transaccion  = ?";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, idTransacionDatafono);
            ResultSet rs = pmt.executeQuery();
            if (rs.next()) {
                data.addProperty("idTransaccionEstado", rs.getLong("id_transaccion_estado"));
                data.addProperty("descripcion", rs.getString("descripcion"));
                data.addProperty("idAdquiriente", rs.getLong("id_adquiriente"));
                data.addProperty("proveedor", rs.getString("proveedor"));
            } else {
                data = null;
            }
        } catch (SQLException e) {
            NovusUtils.printLn(Main.ANSI_RED + "ha ocurrido un error al realizar la consulta " + e.getMessage() + Main.ANSI_RESET);
        }
        return data;
    }
    /* 
    public boolean validarVentaPendienteDatafono(long idTransacionDatafono) {
        String sql = "select * from  datafonos.fnc_consultar_estado(?)";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, idTransacionDatafono);
            ResultSet rs = pmt.executeQuery();
            if (rs.next()) {
                long idMovimiento = obtenerIdMovimiento(idTransacionDatafono);
                return rs.getLong("id_transaccion_estado") == EstadoVentaDatafono.POR_ENVIAR.getValor() && !hayVentaPendienteDePagoMixto(idMovimiento);
            }
        } catch (Exception e) {
            NovusUtils.printLn(Main.ANSI_RED + "ha ocurrido un error al realizar la consulta " + e.getMessage() + Main.ANSI_RESET);
            return false;
        }
        return false;
    }
        */
    
    /* 
    private long obtenerIdMovimiento(long idTransacionDatafono) {
        String sql = "select t.id_movimiento  from datafonos.transacciones t where t.id_transaccion = ?";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        long id = 0;
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, idTransacionDatafono);
            ResultSet rs = pmt.executeQuery();
            if (rs.next()) {
                id = rs.getLong("id_movimiento");
            }
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo obtenerIdMovimiento(long idTransacionDatafono) " + e.getMessage());
        }
        return id;
    }
        */
    /* 
    private boolean hayVentaPendienteDePagoMixto(long idMovimiento) {
        String sql = "select * from datafonos.transacciones t where t.id_movimiento = ? and t.id_transaccion_estado = ?";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        boolean ventaPendiente = false;
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, idMovimiento);
            pmt.setInt(2, EstadoVentaDatafono.PENDIENTE.getValor());
            ResultSet rs = pmt.executeQuery();
            ventaPendiente = rs.next();
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error inesperado el metodo hayVentaPendienteDePagoMixto(long idMovimiento) " + e.getMessage());
        }
        return ventaPendiente;
    }
        */

    // public JsonArray buscarmdiosPagosDatafonosCompletados(long idMovimiento) {
    //     JsonArray mediosPagos = new JsonArray();
    //     String sql = "select cmmp.id, cmmp.valor_total, cmmp.ing_pago_datafono, cmp.descripcion, cmp.id as id_medio_pago "
    //             + "from ct_movimientos_medios_pagos cmmp "
    //             + "inner join ct_medios_pagos cmp  on cmmp.ct_medios_pagos_id  = cmp.id "
    //             + "where cmmp.ct_movimientos_id = ?";
    //     Connection conexion = Main.obtenerConexion("lazoexpresscore");
    //     try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
    //         pmt.setLong(1, idMovimiento);
    //         ResultSet rs = pmt.executeQuery();
    //         while (rs.next()) {
    //             JsonObject objMediosPagos = new JsonObject();
    //             objMediosPagos.addProperty("idMovimientosMediosPagos", rs.getLong("id"));
    //             objMediosPagos.addProperty("idMedioPago", rs.getLong("id_medio_pago"));
    //             objMediosPagos.addProperty("valorTotal", rs.getFloat("valor_total"));
    //             objMediosPagos.addProperty("pagoDatafonoAprobado", rs.getBoolean("ing_pago_datafono"));
    //             objMediosPagos.addProperty("descripcion", rs.getString("descripcion"));
    //             mediosPagos.add(objMediosPagos);
    //         }
    //     } catch (SQLException e) {
    //         NovusUtils.printLn(Main.ANSI_RED + "Ha ocurrido un error en la consulta para obtener los medios de pagos " + e.getMessage() + " " + Main.ANSI_RESET);
    //     }
    //     return mediosPagos;
    // }

    public boolean validarMedio(long idMovimiento, long idMedioPago) {
        String sql = "select cmmp.id, cmmp.valor_total, cmmp.ing_pago_datafono, cmp.descripcion, cmp.id as id_medio_pago  \n"
                + "from public.ct_movimientos_medios_pagos cmmp \n"
                + "inner join public.ct_medios_pagos cmp  on cmmp.ct_medios_pagos_id  = cmp.id\n"
                + "where cmmp.ct_movimientos_id = ? and cmp.id = ?";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, idMovimiento);
            pmt.setLong(2, idMedioPago);
            ResultSet rs = pmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("ing_pago_datafono");
            }
        } catch (SQLException e) {
            NovusUtils.printLn(Main.ANSI_RED + "Ha ocurrido un error en la consulta para validar medios de pagos " + e.getMessage() + " " + Main.ANSI_RESET);
        }
        return false;
    }

    public JsonArray obtenerMediosPagoVenta(long idMovimiento) {
        JsonArray mediosPagoVenta = new JsonArray();
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "select array_to_json(array_agg(row_to_json(t))) "
                + " as medios from (select cmmp.*, cmp.descripcion, cmp.id as id_medio_pago "
                + " from ct_movimientos_medios_pagos cmmp "
                + " inner join ct_medios_pagos cmp on cmmp.ct_medios_pagos_id = cmp.id "
                + " where cmmp.ct_movimientos_id = ?)t;";
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, idMovimiento);
            ResultSet rs = pmt.executeQuery();
            if (rs.next()) {
                Gson gson = new Gson();
                mediosPagoVenta = gson.fromJson(rs.getString("medios"), JsonArray.class);
            }
        } catch (SQLException e) {
            NovusUtils.printLn(Main.ANSI_RED + "Ha ocurrido Al consultar los medios de pago" + e.getMessage() + " " + Main.ANSI_RESET);
        }
        return mediosPagoVenta;
    }
    /*
    public boolean isPendienteTransaccionMovimiento(long idMovimiento) {
        boolean isPendiente = false;
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "select * from fnc_consultar_venta_pendiente(?) pendiente;";
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, idMovimiento);
            ResultSet rs = pmt.executeQuery();
            if (rs.next()) {
                isPendiente = rs.getBoolean("pendiente");
            }
        } catch (SQLException e) {
            NovusUtils.printLn(Main.ANSI_RED + "Ha ocurrido Al consultar el estado de la transaccion de la venta" + e.getMessage() + " " + Main.ANSI_RESET);
        }
        return isPendiente;
    }
     */

    public boolean actualizarMediosPagoVenta(String mediosVenta) {
        boolean completado = false;
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "select * from fnc_actualizar_medios_de_pagos(?::json) completado;";
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setString(1, mediosVenta);
            ResultSet rs = pmt.executeQuery();
            if (rs.next()) {
                NovusUtils.printLn("Actualizado");
                completado = rs.getBoolean("completado");
            }
        } catch (SQLException e) {
            NovusUtils.printLn(Main.ANSI_RED + "Ha ocurrido un error al Actualizar los medios de Pago" + e.getMessage() + " " + Main.ANSI_RESET);
        }
        return completado;
    }

    public JsonArray getBonosVenta(long idMovimiento) {
        JsonArray bonosVenta = new JsonArray();
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "select (cm.atributos::json->>'Bonos_Vive_Terpel')::json bonos from ct_movimientos cm where cm.id = ?";
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, idMovimiento);
            ResultSet rs = pmt.executeQuery();
            if (rs.next()) {
                Gson gson = new Gson();
                bonosVenta = gson.fromJson(rs.getString("bonos"), JsonArray.class);
            }
        } catch (SQLException e) {
            NovusUtils.printLn(Main.ANSI_RED + "Ha ocurrido un error al consultar los Bonos" + e.getMessage() + " " + Main.ANSI_RESET);
        }
        return bonosVenta;
    }

    /*public boolean buscarTransaccionDatafono(long idMovimiento) {
        String sql = "select * from datafonos.transacciones ts where ts.id_transaccion_estado = 1 and ts.id_movimiento = ?";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, idMovimiento);
            ResultSet rs = pmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error inisperado en la clase movimientosDao en el metodo buscarTransaccionDatafono(long idMovimiento) " + e.getMessage());
            return false;
        }
    }*/

    public TransaccionProceso buscarAppTerpelRechazadaOnoExiste(long idMovimiento) {
        String sql = "select * from procesos.tbl_transaccion_proceso ttp where ttp.id_movimiento = ? and ttp.id_integracion in (1,2)  ";
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        TransaccionProceso transaccionProceso = new TransaccionProceso();
        transaccionProceso.setIdTrasccion(0);
        transaccionProceso.setIdMov(idMovimiento);
        transaccionProceso.setIdEstadoIntegracion(0);
        transaccionProceso.setIdEstadoProceso(0);

        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, idMovimiento);
            ResultSet rs = pmt.executeQuery();
            boolean existe = true;
            while (rs.next()) {
                transaccionProceso.setIdTrasccion(rs.getLong("id_transaccion_proceso"));
                transaccionProceso.setIdEstadoIntegracion(rs.getLong("id_estado_integracion"));
                transaccionProceso.setIdEstadoProceso(rs.getLong("id_estado_proceso"));

            }

            System.out.println("ENCONTRO APPTEPEL PENDIENTE: " + existe);

        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error inisperado en la clase movimientosDao en el metodo buscarAppTerpellPendientes(long idMovimiento) " + e.getMessage());

        }
        return transaccionProceso;
    }

    public void updateFEtoProcesoAppterpel(long idMovimiento) {
        String updateQRY = " update procesos.tbl_transaccion_proceso set isfe = false \n"
                + "where id_movimiento = ? and id_integracion = 1;";
        System.out.println("QQQQ QUERY UPDATE FE APPTERPEL QQQQQ " + updateQRY);
        System.out.println("@@@@@@ IDMOV_UPDATE_ FE_APPTERPEL  @@@@@: " + idMovimiento);
        Connection conexion = Main.obtenerConexion("lazoexpresscore");

        try (PreparedStatement pmt = conexion.prepareStatement(updateQRY)) {
            pmt.setLong(1, idMovimiento);
            int result = pmt.executeUpdate();

            System.out.println("Linea editardas : " + result);

        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error inisperado en la clase movimientosDao en el metodo updateFEtoProcesoAppterpel(long idMovimiento) " + e.getMessage());

        }

    }

    public TreeMap<String, String> buscarParametrizacion(int idAquiriente) {
        String sql = "select p.descripcion, ap.valor from parametrizacion.adquirientes_parametro ap "
                + " inner join parametrizacion.parametros p on ap.id_parametro = p.id_parametro "
                + " where ap.id_adquiriente = ?";
        TreeMap<String, String> parametrosMap = new TreeMap<>();
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setInt(1, idAquiriente);
            ResultSet rs = pmt.executeQuery();
            while (rs.next()) {
                String parametro = rs.getString("descripcion");
                parametrosMap.put(parametro, parametro);
            }
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error inisperado en el metodo buscarParametrizacion(int idAquiriente) " + e);
        }
        return parametrosMap;

    }

    // public boolean isPagoGopass(long idMovimiento) {
    //     boolean isPagoGopass = false;
    //     Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
    //     String sql = "select true as valor from ct_movimientos cm where cm.id = ? and cm.atributos::json->>'gopass' is not null;";
    //     try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
    //         pmt.setLong(1, idMovimiento);
    //         ResultSet rs = pmt.executeQuery();
    //         while (rs.next()) {
    //             isPagoGopass = rs.getBoolean("valor");
    //         }
    //     } catch (SQLException e) {
    //         NovusUtils.printLn("ha ocurrido un error al consultar el pago de la venta) " + e);
    //     }
    //     return isPagoGopass;
    // }

    public JsonObject getObservacionesFactura() {
        JsonObject observaciones = null;
        String sql = "select\n"
                + "	row_to_json(t) observaciones\n"
                + "from\n"
                + "	(\n"
                + "	select\n"
                + "		autorretenedor,\n"
                + "		autorretenedor_numero_autorizacion,\n"
                + "		to_char(autorretenedor_fecha_inicio, 'DD-MM-YYYY') as autorretenedor_fecha_inicio,\n"
                + "		responsable_iva,\n"
                + "		gran_contribuyente,\n"
                + "		gran_contribuyente_numero_autorizacion,\n"
                + "		to_char(gran_contribuyente_fecha_inicio, 'DD-MM-YYYY') as gran_contribuyente_fecha_inicio,\n"
                + "		retenedor_iva,\n"
                + "		pie_pagina_factura_pos\n"
                + "	from\n"
                + "		ct_observacion_por_eds) t;";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            ResultSet rs = pmt.executeQuery();
            while (rs.next()) {
                observaciones = new Gson().fromJson(rs.getString("observaciones"), JsonObject.class);
            }
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error inisperado en el metodo buscarParametrizacion(int idAquiriente) " + e);
        }
        return observaciones;
    }

    public ArrayList<MovimientosDetallesBean> busquedaProductoTipoKIOSCO(String busqueda) throws DAOException {
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        ArrayList<MovimientosDetallesBean> lista = new ArrayList<>();
        String sql = "select * from fnc_buscar_producto_market (?, ?, ?);";
        boolean isCDL = Main.TIPO_NEGOCIO
                .equals(NovusConstante.PARAMETER_CDL);
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, "%" + busqueda + "%");
            ps.setBoolean(2, isCDL);
            ps.setLong(3, NovusConstante.IDENTIFICADOR_CODIGO_BARRA);
            ResultSet re = ps.executeQuery();
            while (re.next()) {
                MovimientosDetallesBean movBean = new MovimientosDetallesBean();
                movBean.setId(re.getLong("ID"));
                movBean.setCategoriaId(re.getLong("categoria_id"));
                movBean.setCategoriaDesc(re.getString("categoria_descripcion"));
                movBean.setProductoId(re.getLong("ID"));
                movBean.setPlu(re.getString("PLU"));
                movBean.setDescripcion(re.getString("DESCRIPCION"));
                movBean.setPrecio(re.getFloat("PRECIO"));
                movBean.setSaldo(re.getInt("SALDO"));
                movBean.setTipo(re.getInt("tipo"));
                movBean.setUnidades_medida_id(re.getInt("unidades_medida"));
                movBean.setCantidadIngredientes(re.getInt("CANTIDAD_INGREDIENTES"));
                movBean.setCantidadImpuestos(re.getInt("CANTIDAD_IMPUESTOS"));
                movBean.setEstado(re.getString("estado"));
                movBean.setCosto(re.getFloat("COSTO"));
                movBean.setCodigoBarra(re.getString("codigo_barra"));
                movBean.setProducto_compuesto_costo(re.getFloat("COSTO"));
                movBean.setCompuesto(re.getInt("TIPO") == NovusConstante.TIPO_PRODUCTO_COMPUESTO || re.getInt("TIPO") == NovusConstante.TIPO_PRODUCTO_PROMOCION);
                movBean.setImpuestos(organizarImpuestosProductos(new Gson().fromJson(re.getString("impuestos"), JsonArray.class)));
                movBean.setIngredientes(organizarIngredientesProductos(new Gson().fromJson(re.getString("ingredientes"), JsonArray.class)));
                if (!movBean.getIngredientes().isEmpty()) {
                    //Encontrar bodega del ingrediente
                    for (ProductoBean ingrediente : movBean.getIngredientes()) {
                        if (movBean.getBodegasId() == 0) {
                            //Encontrar bodega del ingrediente
                            try (PreparedStatement pst = conexion.prepareStatement("SELECT BODEGAS_ID FROM BODEGAS_PRODUCTOS WHERE PRODUCTOS_ID=?")) {
                                pst.setLong(1, ingrediente.getId());
                                ResultSet re1 = pst.executeQuery();
                                while (re1.next()) {
                                    movBean.setBodegasId(re1.getLong(1));
                                }
                            } catch (SQLException s) {
                                Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);
                            }
                        }
                        movBean.setProducto_compuesto_costo(movBean.getProducto_compuesto_costo() + (ingrediente.getProducto_compuesto_costo() * ingrediente.getProducto_compuesto_cantidad()));
                    }
                }
                lista.add(movBean);
            }
        } catch (PSQLException s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);
        } catch (SQLException s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);
        } catch (Exception s) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, s);
        }
        return lista;
    }

    public JsonArray getCierreIslaReporteria(String fechaInicio, String fechaFin) {
        JsonArray dataCierre = new JsonArray();
        String sql = "select\n"
                + "array_to_json(array_agg(row_to_json(t))) as data\n"
                + "from\n"
                + "(\n"
                + "select\n"
                + "sum(coalesce(rjc.combustible_numero_ventas + rjc.kiosko_numero_ventas + rjc.canastilla_numero_ventas + rjc.cdl_numero_ventas , 0)::numeric)::numeric numero_ventas,\n"
                + "sum(coalesce(rjc.combustible_total_subtotal \n"
                + "+ rjc.kiosco_total_ventas \n"
                + "+ rjc.canastilla_total_ventas\n"
                + "+ rjc.kiosco_total_contingencia_ventas \n"
                + "+ rjc.canastilla_total_contingencia_ventas\n"
                + "+ rjc.cdl_total_ventas , 0)::numeric)::numeric total_ventas,\n"
                + "rjc.jornada turno,\n"
                + "to_char(min(rjc.fecha_inicio), 'YYYY-MM-DD HH24:MI:SS') as fecha_inicio,\n"
                + "to_char(max(rjc.fecha_fin), 'YYYY-MM-DD HH24:MI:SS') as fecha_fin\n"
                + "from\n"
                + "reporteria_cierres.rp_jornadas_cierres rjc\n"
                + "where\n"
                + "rjc.pos = ?\n"
                + "and rjc.fecha_inicio >= ?\n"
                + "and rjc.fecha_fin <= ?\n"
                + "and rjc.fecha_fin is not null\n"
                + "group by\n"
                + "rjc.jornada\n"
                + "order by\n"
                + "rjc.jornada desc)t;";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, Main.credencial.getEquipos_id());
            pmt.setTimestamp(2, Timestamp.valueOf(fechaInicio));
            pmt.setTimestamp(3, Timestamp.valueOf(fechaFin));
            ResultSet rs = pmt.executeQuery();
            while (rs.next()) {
                if (rs.getString("data") != null) {
                    dataCierre = new Gson().fromJson(rs.getString("data"), JsonArray.class);
                }
            }
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error inisperado en getCierreIsla " + e);
        }
        return dataCierre;
    }

    public JsonArray getCierreIsla(String fechaInicio, String fechaFin) {
        JsonArray dataCierre = new JsonArray();
        String sql = "select\n"
                + "array_to_json(array_agg(row_to_json(t))) as data\n"
                + "from\n"
                + "(\n"
                + "select\n"
                + "sum(coalesce((lt_data->>'numeroVentas'),(lt_data->>'NumeroVentasSistema'))::float)::float numero_ventas,\n"
                + "sum(coalesce((lt_data->>'totalVentas'),(lt_data->>'TotalVentasSistema'))::float) total_ventas,\n"
                + "lt_turno turno,\n"
                + "min(concat( coalesce(((lt_data->>'turno')::json->>'fecha'),(((lt_data->>'Turnos')::json->>0)::json->>'fecha')), ' ', coalesce( ((lt_data->>'turno')::json->>'hora_inicio'),( (((lt_data->>'Turnos')::json->>0)::json->>'hora_inicio'))))) fecha_inicio,\n"
                + "max(concat(coalesce((lt_data->>'turno')::json->>'hora_fin'),(((lt_data->>'Turnos')::json->>0)::json->>'hora_fin'))) fecha_fin\n"
                + "from\n"
                + "lt_consolidados lc\n"
                + "where\n"
                + "lt_equipos_id =(\n"
                + "select\n"
                + "id\n"
                + "from\n"
                + "equipos\n"
                + "limit 1)\n"
                + "and ((coalesce(((lt_data->>'turno')::json->>'fecha'),(((lt_data->>'Turnos')::json->>0)::json->>'fecha'))) >= ?\n"
                + "and (coalesce(((lt_data->>'turno')::json->>'fecha'),(((lt_data->>'Turnos')::json->>0)::json->>'fecha'))) <= ?)\n"
                + "and coalesce(((lt_data->>'turno')::json->>'hora_fin'),(((lt_data->>'Turnos')::json->>0)::json->>'hora_fin')) is not null\n"
                + "group by\n"
                + "lt_turno\n"
                + "order by\n"
                + "4 desc )t;";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setTimestamp(1, Timestamp.valueOf(fechaInicio));
            pmt.setTimestamp(2, Timestamp.valueOf(fechaFin));
            ResultSet rs = pmt.executeQuery();
            while (rs.next()) {
                if (rs.getString("data") != null) {
                    dataCierre = new Gson().fromJson(rs.getString("data"), JsonArray.class);
                }
            }
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error inisperado en getCierreIsla " + e);
        }
        return dataCierre;
    }

    public JsonArray getConsolidado(String fechaInicio, String fechaFin) {
        JsonArray dataCierre = new JsonArray();
        String sql = "select\n"
                + "array_to_json(array_agg(row_to_json(t))) as data\n"
                + "from\n"
                + "(\n"
                + "select\n"
                + "sum(coalesce((lt_data->>'numeroVentas'),(lt_data->>'NumeroVentasSistema'))::float)::float numero_ventas,\n"
                + "sum(coalesce((lt_data->>'totalVentas'),(lt_data->>'TotalVentasSistema'))::float) total_ventas,\n"
                + "lt_turno turno,\n"
                + "min(concat( coalesce(((lt_data->>'turno')::json->>'fecha'),(((lt_data->>'Turnos')::json->>0)::json->>'fecha')), ' ', coalesce( ((lt_data->>'turno')::json->>'hora_inicio'),( (((lt_data->>'Turnos')::json->>0)::json->>'hora_inicio'))))) fecha_inicio,\n"
                + "max(concat(coalesce((lt_data->>'turno')::json->>'hora_fin'),(((lt_data->>'Turnos')::json->>0)::json->>'hora_fin'))) fecha_fin\n"
                + "from\n"
                + "lt_consolidados lc\n"
                + "where\n"
                + "((coalesce(((lt_data->>'turno')::json->>'fecha'),(((lt_data->>'Turnos')::json->>0)::json->>'fecha'))) >= ?\n"
                + "and (coalesce(((lt_data->>'turno')::json->>'fecha'),(((lt_data->>'Turnos')::json->>0)::json->>'fecha'))) <= ?)\n"
                + "and coalesce(((lt_data->>'turno')::json->>'hora_fin'),(((lt_data->>'Turnos')::json->>0)::json->>'hora_fin')) is not null\n"
                + "group by\n"
                + "lt_turno\n"
                + "order by\n"
                + "4 desc )t;";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setTimestamp(1, Timestamp.valueOf(fechaInicio));
            pmt.setTimestamp(2, Timestamp.valueOf(fechaFin));
            ResultSet rs = pmt.executeQuery();
            while (rs.next()) {
                if (rs.getString("data") != null) {
                    dataCierre = new Gson().fromJson(rs.getString("data"), JsonArray.class);
                }
            }
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error inisperado en getCierreIsla " + e);
        }
        return dataCierre;
    }

    public JsonArray getConsolidadoReporteria(String fechaInicio, String fechaFin) {
        JsonArray dataCierre = new JsonArray();
        String sql = "select\n"
                + "array_to_json(array_agg(row_to_json(t))) as data\n"
                + "from\n"
                + "(\n"
                + "select\n"
                + "sum(coalesce(rjc.combustible_numero_ventas + rjc.kiosko_numero_ventas + rjc.canastilla_numero_ventas + rjc.cdl_numero_ventas, 0)::numeric)::numeric numero_ventas,\n"
                + "sum(coalesce(rjc.combustible_total_subtotal "
                + "+ rjc.kiosco_total_ventas "
                + "+ rjc.canastilla_total_ventas "
                + "+ rjc.kiosco_total_contingencia_ventas"
                + "+ rjc.canastilla_total_contingencia_ventas"
                + "+ rjc.cdl_total_ventas, 0)::numeric)::numeric total_ventas,\n"
                + "rjc.jornada turno,\n"
                + "to_char(min(rjc.fecha_inicio), 'YYYY-MM-DD HH24:MI:SS') as fecha_inicio,\n"
                + "to_char(max(rjc.fecha_fin), 'YYYY-MM-DD HH24:MI:SS') as fecha_fin\n"
                + "from\n"
                + "reporteria_cierres.rp_jornadas_cierres rjc\n"
                + "where\n"
                + "rjc.fecha_inicio >= ?\n"
                + "and rjc.fecha_fin <= ?\n"
                + "and rjc.fecha_fin is not null\n"
                + "group by\n"
                + "rjc.jornada\n"
                + "order by\n"
                + "rjc.jornada desc)t;";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setTimestamp(1, Timestamp.valueOf(fechaInicio));
            pmt.setTimestamp(2, Timestamp.valueOf(fechaFin));
            ResultSet rs = pmt.executeQuery();
            while (rs.next()) {
                if (rs.getString("data") != null) {
                    dataCierre = new Gson().fromJson(rs.getString("data"), JsonArray.class);
                }
            }
        } catch (SQLException e) {
            NovusUtils.printLn("ha ocurrido un error inisperado en getCierreIsla " + e);
        }
        return dataCierre;
    }

    public JsonObject procesarVentasKiosco(JsonObject datos, String tipo, String estadoMovimiento) {

        JsonObject data = new JsonObject();

        System.out.println("datos =>  " + datos);
        System.out.println("tipo =>  " + tipo);
        System.out.println("estadoMovimiento =>  " + estadoMovimiento);

        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "call public.prc_procesar_venta_kiosco_canastilla(\n"
                + "i_json_datos => ?::json,\n"
                + "i_tipo_transaccion => ?,\n"
                + "i_estado_movimiento => ?,\n"
                + "o_json_respuesta => '{}'::json\n"
                + ");";
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setString(1, datos.toString());
            pmt.setString(2, tipo);
            pmt.setString(3, estadoMovimiento);
            ResultSet rs = pmt.executeQuery();
            while (rs.next()) {
                if (rs.getString("o_json_respuesta") != null) {
                    data = new Gson().fromJson(rs.getString("o_json_respuesta"), JsonObject.class);
                    NovusUtils.printLn("RX [PRF] " + data);
                }
            }

        } catch (Exception e) {
            NovusUtils.printLn(Main.ANSI_RED + "Ha ocurrido un error inesperaro el metodo procesarVentasKiosco " + e.getMessage() + Main.ANSI_RESET);
        }
        return data;
    }

    /* public JsonObject obtenerVentasCanastilla(String fechaInicial, String fechaFinal, String promotor) {

        JsonObject data = new JsonObject();
        JsonArray info = new JsonArray();

        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "select * from public.fnc_consultar_ventas_canastilla(?, ?,?)resultado";
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {

            pmt.setString(1, fechaInicial);
            pmt.setString(2, fechaFinal);
            pmt.setString(3, promotor);
            ResultSet rs = pmt.executeQuery();
            NovusUtils.printLn("query " + pmt.toString());
            while (rs.next()) {
                if (rs.getString("resultado") != null) {
                    info = new Gson().fromJson(rs.getString("resultado"), JsonArray.class);
                }
            }

        } catch (Exception e) {
            NovusUtils.printLn(Main.ANSI_RED + "Ha ocurrido un error inesperado el metodo obtenerVentasCanastilla " + e.getMessage() + Main.ANSI_RESET);
        }
        data.add("data", info);
        return data;

    } */

    public JsonObject obtenerVentasKiosco(String fechaInicial, String fechaFinal, String promotor) {
        DatabaseConnectionManager.DatabaseResources resources = null;
        JsonObject data = new JsonObject();
        JsonArray info = new JsonArray();
        //Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "select * from public.fnc_consultar_ventas_kiosco(?, ?,?)resultado";
        try {
            resources = DatabaseConnectionManager.createDatabaseResources("lazoexpresscore", sql);
            resources.getPreparedStatement().setString(1, fechaInicial);
            resources.getPreparedStatement().setString(2, fechaFinal);
            resources.getPreparedStatement().setString(3, promotor);
            resources = DatabaseConnectionManager.executeQuery(resources);
            NovusUtils.printLn("query " + resources.toString());
            while (resources.getResultSet().next()) {
                if (resources.getResultSet().getString("resultado") != null) {
                    info = new Gson().fromJson(resources.getResultSet().getString("resultado"), JsonArray.class);
                }
            }

        } catch (Exception e) {
            NovusUtils.printLn(Main.ANSI_RED + "Ha ocurrido un error inesperado el metodo obtenerVentasKiosco " + e.getMessage() + Main.ANSI_RESET);
        }
        finally {
            DatabaseConnectionManager.closeDatabaseResources(resources);
        }
        data.add("data", info);
        return data;

    }

    public boolean consultaClienteReintentos(long idTransmision) {
        DatabaseConnectionManager.DatabaseResources resources = null;
        boolean consultar = true;
      //  Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSREGISTRY);
        String sql = "select t.reintentos_cliente from transmision t where t.id = ?;";
        try  {
            resources = DatabaseConnectionManager.createDatabaseResources(NovusConstante.LAZOEXPRESSREGISTRY, sql);
            resources.getPreparedStatement().setLong(1, idTransmision);
            resources = DatabaseConnectionManager.executeQuery(resources);
            while (resources.getResultSet().next()) {
                consultar = resources.getResultSet().getLong("reintentos_cliente") <= NovusConstante.NUMERO_INTENTOS_CONSULTA_CLIENTE;
            }
        } catch (Exception e) {
            NovusUtils.printLn(Main.ANSI_RED + "Ha ocurrido un error inesperado el metodo consultaClienteReintentos " + e.getMessage() + Main.ANSI_RESET);
        }
        finally {
            DatabaseConnectionManager.closeDatabaseResources(resources);
        }
        return consultar;
    }

    public void actualizarReintentosConsultaCliente(long idTransmision) {
        DatabaseConnectionManager.DatabaseResources resources = null;
        String sql = "update transmision set reintentos_cliente = reintentos_cliente + 1 where id =?";
        //Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSREGISTRY);
        try {
            resources = DatabaseConnectionManager.createDatabaseResources(NovusConstante.LAZOEXPRESSREGISTRY, sql);
            resources.getPreparedStatement().setLong(1, idTransmision);
            resources.getPreparedStatement().executeUpdate();
        } catch (Exception e) {
            NovusUtils.printLn(Main.ANSI_RED + "Ha ocurrido un error inesperado el metodo actualizarReintentosConsultaCliente " + e.getMessage() + Main.ANSI_RESET);
        }
        finally {
            DatabaseConnectionManager.closeDatabaseResources(resources);
        }
    }

    public void actualizarRequestTransmision(long idTransmision, JsonObject request) {
        DatabaseConnectionManager.DatabaseResources resources = null;
        String sql = "update transmision set request =?::json where id =?";
        //Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        try {
            resources = DatabaseConnectionManager.createDatabaseResources(NovusConstante.LAZOEXPRESSREGISTRY, sql);

            resources.getPreparedStatement().setString(1, com.utils.JsonSanitizer.escapeInvalidControlChars(request.toString()));
            resources.getPreparedStatement().setLong(2, idTransmision);
            int res = resources.getPreparedStatement().executeUpdate();
            if (res > 0) {
                NovusUtils.printLn(Main.ANSI_GREEN + "actualizacion realizada actualizarRequestTransmision" + Main.ANSI_RESET);
            } else {
                NovusUtils.printLn(Main.ANSI_RED + "error al realizar actualizarRequestTransmision" + Main.ANSI_RESET);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            DatabaseConnectionManager.closeDatabaseResources(resources);
        }
    }

    /*public JsonObject consultaClienteMovimientoTransmision(long idMovimiento) {
        JsonObject respuesta = new JsonObject();
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        String sql = "select * from fnc_consultar_cliente_transmision(?) cliente;";
        try (PreparedStatement ps = conexion.prepareStatement(sql);) {
            ps.setLong(1, idMovimiento);
            ResultSet res = ps.executeQuery();
            while (res.next()) {
                respuesta = Main.gson.fromJson(res.getString(1), JsonObject.class);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return respuesta;
    }
    */
    public void incrementarReintentos(long idTransmision) {
        DatabaseConnectionManager.DatabaseResources resources = null;
        String sql = "UPDATE transmision SET reintentos = COALESCE(reintentos, 0) + 1, fecha_ultima = NOW() WHERE id = ?";
        //Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        try {
            resources = DatabaseConnectionManager.createDatabaseResources(NovusConstante.LAZOEXPRESSREGISTRY, sql);
            resources.getPreparedStatement().setLong(1, idTransmision);
            int filasAfectadas = resources.getPreparedStatement().executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println(" DEBUG [incrementarReintentos]: Reintentos incrementados para transmisión: " + idTransmision);
            } else {
                System.out.println(" WARNING [incrementarReintentos]: No se encontró transmisión: " + idTransmision);
            }
        } catch (SQLException ex) {
            System.err.println(" ERROR [incrementarReintentos]: " + ex.getMessage());
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            DatabaseConnectionManager.closeDatabaseResources(resources);
        }
    }
    public int obtenerReintentos(long idTransmision) {
        DatabaseConnectionManager.DatabaseResources resources = null;
        String sql = "SELECT COALESCE(reintentos, 0) as reintentos FROM transmision WHERE id = ?";
        //Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        try  {
            resources = DatabaseConnectionManager.createDatabaseResources(NovusConstante.LAZOEXPRESSREGISTRY, sql);
            resources.getPreparedStatement().setLong(1, idTransmision);
            resources = DatabaseConnectionManager.executeQuery(resources);
            if (resources.getResultSet().next()) {
                int reintentos = resources.getResultSet().getInt("reintentos");
                System.out.println(" DEBUG [obtenerReintentos]: Transmisión " + idTransmision + " tiene " + reintentos + " reintentos");
                return reintentos;
            }
        } catch (SQLException ex) {
            System.err.println(" ERROR [obtenerReintentos]: " + ex.getMessage());
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            DatabaseConnectionManager.closeDatabaseResources(resources);
        }
        return 0;
    }
    public JsonObject obtenerDatosTransmision(long idTransmision) {
        DatabaseConnectionManager.DatabaseResources resources = null;
        String sql = "SELECT * FROM transmision WHERE id = ?";
        //Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        try  {
            resources = DatabaseConnectionManager.createDatabaseResources(NovusConstante.LAZOEXPRESSREGISTRY, sql);
            resources.getPreparedStatement().setLong(1, idTransmision);
            resources = DatabaseConnectionManager.executeQuery(resources);
            if (resources.getResultSet().next()) {
                Gson gson = new Gson();
                JsonObject json = gson.fromJson(resources.getResultSet().getString("request"), JsonObject.class);

                // Agregar metadata de la transmisión
                json.addProperty("id_transmision", idTransmision);
                json.addProperty("reintentos_actuales", resources.getResultSet().getInt("reintentos"));
                json.addProperty("sincronizado_actual", resources.getResultSet().getInt("sincronizado"));

                // Agregar medios de pago si tiene movimiento asociado
                if (json.has("identificadorMovimiento") && !json.get("identificadorMovimiento").isJsonNull()) {
                    long idMovimiento = json.get("identificadorMovimiento").getAsLong();
                    JsonArray medios = mediosPagos(idMovimiento);
                    if (medios.size() > 0) {
                        json.add("pagos", medios);
                    }
                }

                System.out.println("📤 DEBUG [obtenerDatosTransmision]: Datos obtenidos para transmisión: " + idTransmision);
                return json;
            }
        } catch (SQLException ex) {
            System.err.println(" ERROR [obtenerDatosTransmision]: " + ex.getMessage());
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            DatabaseConnectionManager.closeDatabaseResources(resources);
        }
        return new JsonObject();
    }
    public void asegurarReintentos(long idTransmision, int reintentosMinimos) {
        DatabaseConnectionManager.DatabaseResources resourcesConsulta = null, resourcesUpdate = null;
        String sqlSelect = "SELECT COALESCE(reintentos, 0) as reintentos FROM transmision WHERE id = ?";
        String sqlUpdate = "UPDATE transmision SET reintentos = ?, fecha_ultima = NOW() WHERE id = ?";

        //Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        try {
            resourcesConsulta = DatabaseConnectionManager.createDatabaseResources(NovusConstante.LAZOEXPRESSREGISTRY, sqlSelect);
            resourcesUpdate = DatabaseConnectionManager.createDatabaseResources(NovusConstante.LAZOEXPRESSREGISTRY, sqlUpdate);
            resourcesConsulta.getPreparedStatement().setLong(1, idTransmision);
            resourcesConsulta = DatabaseConnectionManager.executeQuery(resourcesConsulta);

            if (resourcesConsulta.getResultSet().next()) {
                int reintentosActuales = resourcesConsulta.getResultSet().getInt("reintentos");

                if (reintentosActuales < reintentosMinimos) {
                    resourcesUpdate.getPreparedStatement().setInt(1, reintentosMinimos);
                    resourcesUpdate.getPreparedStatement().setLong(2, idTransmision);
                    resourcesUpdate.getPreparedStatement().executeUpdate();

                    System.out.println(" DEBUG [asegurarReintentos]: Transmisión " + idTransmision +
                            " actualizada de " + reintentosActuales + " → " + reintentosMinimos + " reintentos");
                } else {
                    System.out.println(" DEBUG [asegurarReintentos]: Transmisión " + idTransmision +
                            " ya tiene " + reintentosActuales + " reintentos (>= " + reintentosMinimos + ")");
                }
            }
        } catch (SQLException ex) {
            System.err.println(" ERROR [asegurarReintentos]: " + ex.getMessage());
            Logger.getLogger(MovimientosDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            DatabaseConnectionManager.closeDatabaseResources(resourcesConsulta);
            DatabaseConnectionManager.closeDatabaseResources(resourcesUpdate);
        }
    }

    // =====================================
    // MÉTODOS HELPER PARA INVENTARIO
    // =====================================

    /**
     * ✅ HELPER METHOD: Obtiene la bodega correcta para un producto
     * Si el producto tiene BodegaId = 0, busca la bodega real en BODEGAS_PRODUCTOS
     *
     * @param conexion Conexión a la base de datos
     * @param productoId ID del producto
     * @param bodegaIdActual BodegaId actual del producto (puede ser 0)
     * @return BodegaId correcta para usar en actualizaciones de inventario
     * @throws DAOException Si no se puede obtener la bodega
     */
    private long obtenerBodegaCorrectaParaInventario(Connection conexion, long productoId, long bodegaIdActual) throws DAOException {
        // Si ya tiene una bodega válida, usarla
        if (bodegaIdActual > 0) {
            return bodegaIdActual;
        }

        // Si BodegaId es 0, buscar la bodega real en BODEGAS_PRODUCTOS
        try {
            PreparedStatement ps = conexion.prepareStatement(
                    "SELECT bodegas_id FROM bodegas_productos WHERE productos_id = ? LIMIT 1"
            );
            ps.setLong(1, productoId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                long bodegaEncontrada = rs.getLong(1);
                NovusUtils.printLn("[INVENTARIO] Bodega encontrada para producto " + productoId + ": " + bodegaEncontrada);
                return bodegaEncontrada;
            } else {
                throw new DAOException("No se encontró bodega para producto ID: " + productoId);
            }
        } catch (SQLException e) {
            throw new DAOException("Error buscando bodega para producto " + productoId + ": " + e.getMessage());
        }
    }

    /**
     * ✅ HELPER METHOD: Actualiza el inventario de un producto de forma segura
     * Maneja automáticamente el caso cuando BodegaId = 0
     *
     * @param conexion Conexión a la base de datos
     * @param productoId ID del producto
     * @param cantidad Cantidad a descontar
     * @param bodegaIdActual BodegaId actual del producto
     * @throws DAOException Si hay error en la actualización
     */
    private void actualizarInventarioProducto(Connection conexion, long productoId, float cantidad, long bodegaIdActual) throws DAOException {
        try {
            long bodegaIdCorrecta = obtenerBodegaCorrectaParaInventario(conexion, productoId, bodegaIdActual);

            String sql = "UPDATE BODEGAS_PRODUCTOS SET SALDO=SALDO-? WHERE bodegas_id=? AND productos_id=?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setFloat(1, cantidad);
            ps.setLong(2, bodegaIdCorrecta);
            ps.setLong(3, productoId);

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new DAOException("No se pudo actualizar inventario - producto " + productoId + " no encontrado en bodega " + bodegaIdCorrecta);
            }

            NovusUtils.printLn("[INVENTARIO] Actualizado - Producto: " + productoId +
                    ", Cantidad: " + cantidad +
                    ", Bodega: " + bodegaIdCorrecta +
                    ", Filas afectadas: " + filasAfectadas);

        } catch (SQLException e) {
            throw new DAOException("Error actualizando inventario para producto " + productoId + ": " + e.getMessage());
        }
    }

    /**
     * ✅ NUEVO MÉTODO: Devuelve inventario después de anulación
     * SUMA la cantidad al saldo (operación inversa a actualizarInventarioProducto)
     * 
     * @param productoId ID del producto a devolver inventario
     * @param cantidad Cantidad a devolver
     * @param bodegaId ID de la bodega (puede ser 0, se busca automáticamente)
     * @throws DAOException Si hay error en la devolución
     */
    public void devolverInventarioProducto(long productoId, float cantidad, long bodegaId) throws DAOException {
        Connection conexion = null;
        try {
            conexion = Main.obtenerConexion("lazoexpressregistry");
            
            long bodegaIdCorrecta = obtenerBodegaCorrectaParaInventario(conexion, productoId, bodegaId);

            String sql = "UPDATE BODEGAS_PRODUCTOS SET SALDO=SALDO+? WHERE bodegas_id=? AND productos_id=?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setFloat(1, cantidad);
            ps.setLong(2, bodegaIdCorrecta);
            ps.setLong(3, productoId);

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new DAOException("No se pudo devolver inventario - producto " + productoId + " no encontrado en bodega " + bodegaIdCorrecta);
            }

            NovusUtils.printLn("[DEVOLUCIÓN INVENTARIO] Producto: " + productoId +
                    ", Cantidad devuelta: +" + cantidad +
                    ", Bodega: " + bodegaIdCorrecta +
                    ", Filas afectadas: " + filasAfectadas);
            
            // Invalidar cache después de devolver inventario
            if (productoUpdateInterceptor != null && productoId > 0) {
                System.out.println(" Invalidando cache por DEVOLUCIÓN - Producto ID: " + productoId + ", Cantidad devuelta: " + cantidad);
                productoUpdateInterceptor.onProductoActualizado(productoId);
            }

        } catch (SQLException e) {
            throw new DAOException("Error devolviendo inventario para producto " + productoId + ": " + e.getMessage());
        }
    }
    
    /**
     * ✅ NUEVO MÉTODO: Devuelve inventario de ingredientes de producto compuesto
     * 
     * @param productoId ID del producto compuesto
     * @param cantidad Cantidad del producto compuesto anulado
     * @param bodegaId ID de la bodega
     * @throws DAOException Si hay error
     */
    public void devolverInventarioIngredientes(long productoId, float cantidad, long bodegaId) throws DAOException {
        try {
            // Obtener ingredientes del producto compuesto
            ArrayList<ProductoBean> ingredientes = findIngredientesById(productoId);
            
            if (ingredientes != null && !ingredientes.isEmpty()) {
                NovusUtils.printLn("[DEVOLUCIÓN INGREDIENTES] Producto compuesto ID: " + productoId + " tiene " + ingredientes.size() + " ingredientes");
                
                for (ProductoBean ingrediente : ingredientes) {
                    float cantidadIngrediente = ingrediente.getProducto_compuesto_cantidad() * cantidad;
                    devolverInventarioProducto(ingrediente.getId(), cantidadIngrediente, bodegaId);
                    
                    NovusUtils.printLn("[DEVOLUCIÓN INGREDIENTES]   → Ingrediente: " + ingrediente.getDescripcion() + 
                                      " (ID: " + ingrediente.getId() + "), Cantidad: +" + cantidadIngrediente);
                }
            }
        } catch (Exception e) {
            throw new DAOException("Error devolviendo inventario de ingredientes: " + e.getMessage());
        }
    }

}
