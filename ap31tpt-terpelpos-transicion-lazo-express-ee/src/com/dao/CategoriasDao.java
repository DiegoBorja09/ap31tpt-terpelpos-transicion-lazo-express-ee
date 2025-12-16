/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dao;

import com.bean.CategoriaBean;
import com.bean.MovimientosDetallesBean;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.firefuel.Main;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.postgresql.util.PSQLException;

/**
 *
 * @author novus
 */
public class CategoriasDao {

    public ArrayList<CategoriaBean> findAllCategoriasKIOSCO() throws DAOException {
        ArrayList<CategoriaBean> lista = new ArrayList<>();
        Connection conexion = Main.obtenerConexion("lazoexpressregistry");
        if (conexion != null) {
            boolean isCDL = Main.TIPO_NEGOCIO
                    .equals(NovusConstante.PARAMETER_CDL);
            String sql = "select * from public.fnc_obtener_categorias_market(?);";
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setBoolean(1, isCDL);
                ResultSet re = ps.executeQuery();
                while (re.next()) {
                    CategoriaBean bean = new CategoriaBean();
                    bean.setId(re.getLong("ID"));
                    bean.setGrupo(re.getString("GRUPO"));
                    lista.add(bean);
                }
                CategoriaBean bean = new CategoriaBean();
                bean.setId(-1);
                bean.setGrupo("OTROS");
                bean.setCantidad(1);
                lista.add(bean);
            } catch (PSQLException s) {
                throw new DAOException(s.getMessage());
            } catch (SQLException s) {
                throw new DAOException(s.getMessage());
            } catch (Exception s) {
                throw new DAOException(s.getMessage());
            }
        }
        return lista;
    }

    public ArrayList<CategoriaBean> findAllCategorias() throws DAOException {
        ArrayList<CategoriaBean> lista = new ArrayList<>();
        Connection conexion = null;
        try {
            conexion = Main.obtenerConexion("lazoexpressregistry");
            if (conexion != null) {
                String sql = "select *, \n"
                        + "(select count(1) from grupos_entidad ge\n"
                        + "inner join bodegas_productos bp  on bp.productos_id = ge.entidad_id \n"
                        + "where grupo_id = g.id and bp.saldo > 0)  cantidad \n"
                        + "from grupos g where (select count(1) from grupos_entidad ge\n"
                        + "inner join bodegas_productos bp  on bp.productos_id = ge.entidad_id "
                        + "inner join productos as p on p.id = ge.entidad_id \n"
                        + "where grupo_id = g.id and bp.saldo > 0 and coalesce(p.p_atributos::json->>'tipoStore', 'C') not in('K','T'))  > 0   ";

                PreparedStatement ps = conexion.prepareStatement(sql);
                ResultSet re = ps.executeQuery();

                while (re.next()) {
                    CategoriaBean bean = new CategoriaBean();
                    bean.setId(re.getLong("ID"));
                    bean.setGrupo(re.getString("GRUPO"));
                    bean.setCantidad(re.getInt("CANTIDAD"));
                    lista.add(bean);
                }
                CategoriaBean bean = new CategoriaBean();
                bean.setId(-1);
                bean.setGrupo("SIN CATEGORIA");
                bean.setCantidad(1);
                lista.add(bean);
            }
        } catch (PSQLException s) {
            throw new DAOException(s.getMessage());
        } catch (SQLException s) {
            throw new DAOException(s.getMessage());
        } catch (Exception s) {
            throw new DAOException(s.getMessage());
        } finally {
            // Cerrar conexion para evitar memory leaks
            if (conexion != null) {
                try {
                    if (!conexion.isClosed()) {
                        conexion.close();
                    }
                } catch (SQLException e) {
                    Logger.getLogger(SetupDao.class.getName()).log(Level.SEVERE, "Error cerrando conexion en findAllCategorias", e);
                }
            }
        }        return lista;
    }

    /* 
    public ArrayList<MovimientosDetallesBean> findProductosPromocion() throws DAOException {
        ArrayList<MovimientosDetallesBean> lista = new ArrayList<>();
        try {
            Connection conexion = Main.obtenerConexion("lazoexpressregistry");
            if (conexion != null) {
                String sql = "Select p.id, p.plu, p.descripcion, p.precio, p.tipo, p.favorito, COALESCE(saldo, 0) saldo, bp.costo\n"
                        + "from productos p \n" + "left join bodegas_productos bp on bp.productos_id=p.id\n"
                        + "where P.promocion is not null and P.ESTADO='A' and P.puede_vender='S' \n" + "";

                PreparedStatement ps = conexion.prepareStatement(sql);
                ResultSet re = ps.executeQuery();

                while (re.next()) {
                    MovimientosDetallesBean bean = new MovimientosDetallesBean();
                    bean.setId(re.getLong("ID"));
                    bean.setDescripcion(re.getString("DESCRIPCION"));
                    bean.setPrecio(re.getLong("PRECIO"));
                    bean.setPlu(re.getString("PLU"));
                    bean.setTipo(re.getInt("TIPO"));
                    MovimientosDao dao = new MovimientosDao();
                    bean.setImpuestos(dao.findById(re.getLong("ID")));
                    if (re.getString("FAVORITO") != null) {
                        bean.setFavorito(re.getString("FAVORITO").equals("S"));
                    } else {
                        bean.setFavorito(false);
                    }
                    lista.add(bean);
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
    }*/
}
