/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dao.anulacion;

import com.bean.BodegaBean;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.DAOException;
import com.dao.EquipoDao;
import com.dao.MovimientosDao;
import com.facade.anulacion.AnulacionProductos;
import com.facade.anulacion.Atributos;
import com.facade.anulacion.Impuestos;
import com.facade.anulacion.Ingredientes;
import com.firefuel.Main;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Usuario
 */
public class AnulacionDao {

    public List<AnulacionProductos> obtenerProductos(Long idMovimiento) {
        MovimientosDao dao = new MovimientosDao();

        String sql = "select p.id as id_producto, cm.consecutivo, cm.prefijo, p.descripcion,\n"
                + "((cmd.sub_total) + COALESCE((SELECT SUM(cmi.impuesto_valor) \n"
                + "              FROM ct_movimientos_impuestos cmi \n"
                + "              WHERE cmi.movimientos_detalles_id = cmd.id ), 0)) as total,\n"
                + "(cmd.cantidad - coalesce (cmd.atributos::json->>'cantidadAnulada', '0')::numeric(12,3)) as cantidad,\n"
                + "p.productos_tipos_id,\n"
                + "cmd.id,\n"
                + "pu.descripcion as unidad,\n"
                + "pt.descripcion as categoria, pu.id as id_unidad, p.plu, cmd.precio\n"
                + "from ct_movimientos cm\n"
                + "inner join ct_movimientos_detalles cmd on cm.id = cmd.movimientos_id \n"
                + "inner join productos p on cmd.productos_id = p.id\n"
                + "inner join productos_unidades pu on pu.id = p.unidad_medida_id\n"
                + "inner join productos_tipos pt on pt.id = p.productos_tipos_id \n"
                + "where cm.id = ? and p.productos_tipos_id not in(24) and (cmd.cantidad - coalesce (cmd.atributos::json->>'cantidadAnulada', '0')::numeric(12,3)) > 0;";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        ArrayList<AnulacionProductos> productos = new ArrayList<>();
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, idMovimiento);
            ResultSet rs = pmt.executeQuery();
            while (rs.next()) {
                AnulacionProductos anulacionProductos = new AnulacionProductos();
                anulacionProductos.setConsecutivo(rs.getLong("consecutivo"));
                anulacionProductos.setPrefijo(rs.getString("prefijo"));
                anulacionProductos.setTotal(rs.getDouble("total"));
                anulacionProductos.setPrecio(rs.getDouble("precio"));
                anulacionProductos.setCantidad(rs.getFloat("cantidad"));
                anulacionProductos.setProducto(rs.getString("descripcion"));
                anulacionProductos.setId(rs.getLong("id_producto"));
                anulacionProductos.setProductos_plu(rs.getString("plu"));
                anulacionProductos.setUnidad_descripcion(rs.getString("unidad"));
                anulacionProductos.setUnidad(dao.unidadProducto(rs.getLong("id_unidad")));
                anulacionProductos.setUnidadId(rs.getLong("id_unidad"));
                anulacionProductos.setAtributos(obtenerAtributos(rs.getLong("id_producto"), rs.getInt("productos_tipos_id")));
                anulacionProductos.setIngredientes(obtenerIngredientes(rs.getLong("id_producto"), rs.getFloat("cantidad")));
                anulacionProductos.setImpuestos(obtenerImpuestos(rs.getLong("id_producto")));
                anulacionProductos.setIdentificadorBodega(obtenerBodegaId(rs.getLong("id_producto")));
                anulacionProductos.setCosto(obtenerCosto(rs.getLong("id_producto")));
                productos.add(anulacionProductos);
            }
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error en el metodo public List<AnulacionProductos> obtenerProductos(Long idMovimiento)"
                    + " ubicado en la clase " + AnulacionDao.class.getName()
                    + " error -> " + e.getMessage());
        }
        return productos;
    }

    public List<Impuestos> obtenerImpuestos(Long id) {
        String sql = "select i.id, i.descripcion, i.porcentaje_valor, i.valor from productos_impuestos pi2 \n"
                + "inner join impuestos i on i.id = pi2.impuestos_id\n"
                + "where pi2.productos_id = ?";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSREGISTRY);
        ArrayList<Impuestos> impuestos = new ArrayList<>();
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, id);
            ResultSet rs = pmt.executeQuery();
            while (rs.next()) {
                Impuestos impuestosObj = new Impuestos();
                impuestosObj.setValor(rs.getInt("valor"));
                impuestosObj.setTipo(rs.getString("porcentaje_valor"));
                impuestosObj.setImpuestos_id(rs.getLong("id"));
                impuestosObj.setValor_imp(0);
                impuestos.add(impuestosObj);
            }
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo  public List<Impuestos> obtenerImpuestos(Long id) "
                    + "ubicado en la clase " + AnulacionDao.class.getName() + " error -> " + e.getMessage());
        }
        return impuestos;
    }

    public double obtenerCosto(long id) {
        double costo = 0;
        String sql = "select bp.costo  from bodegas_productos bp where bp.productos_id = ?";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSREGISTRY);
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, id);
            ResultSet rs = pmt.executeQuery();
            while (rs.next()) {
                costo = rs.getDouble("costo");
            }
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo  public double obtenerCosto(long id) "
                    + "ubicado en la clase " + AnulacionDao.class.getName() + " error -> " + e.getMessage());
        }
        return costo;
    }

    public List<Ingredientes> obtenerIngredientes(Long id, float cantidad) {
        String sql = "select pc.*, p.descripcion, p.plu  from productos_compuestos pc\n"
                + "inner join productos p on p.id = pc.ingredientes_id\n"
                + "where pc.productos_id = ?;";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSREGISTRY);
        ArrayList<Ingredientes> ingredientes = new ArrayList<>();
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, id);
            ResultSet rs = pmt.executeQuery();
            while (rs.next()) {
                Ingredientes ingredientesObj = new Ingredientes();
                ingredientesObj.setId(rs.getLong("id"));
                ingredientesObj.setProductos_descripcion(rs.getString("descripcion"));
                ingredientesObj.setProductos_id(rs.getLong("ingredientes_id"));
                ingredientesObj.setProductos_plu(rs.getString("plu"));
                ingredientesObj.setCantidad(cantidad * rs.getFloat("cantidad"));
                ingredientesObj.setCompuesto_cantidad(0);
                ingredientesObj.setDescuento_base(0);
                ingredientesObj.setIdentificadorProducto(rs.getLong("ingredientes_id"));
                ingredientesObj.setImpuestos(new ArrayList<>());
                ingredientesObj.setProductos_descuento_valor(0);
                ingredientesObj.setProductos_precio(0);
                ingredientesObj.setProductos_precio_especial(0);
                ingredientesObj.setCosto(obtenerCosto(rs.getLong("ingredientes_id")) * cantidad);
                ingredientes.add(ingredientesObj);
            }
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo public List<Ingredientes> obtenerIngredientes(Long id, float cantidad)"
                    + " ubicado en la clase " + AnulacionDao.class.getName() + " error -> " + e.getMessage());
        }
        return ingredientes;
    }

    public Atributos obtenerAtributos(long id, int tipo) {
        String sql = "select g.grupo, g.id  from grupos g \n"
                + "inner join grupos_entidad ge on ge.grupo_id  = g.id \n"
                + "where ge.entidad_id = ?";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSREGISTRY);
        Atributos atributos = new Atributos();
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, id);
            ResultSet rs = pmt.executeQuery();
            while (rs.next()) {
                atributos.setCategoriaDescripcion(rs.getString("grupo"));
                atributos.setCategoriaId(rs.getLong("id"));
                atributos.setIsElectronica(true);
                atributos.setTipo(tipo);
            }
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo public Atributos obtenerAtributos(long id) "
                    + "ubicado en la clase " + AnulacionDao.class.getName() + " error -> " + e.getMessage());
        }
        return atributos;
    }

    public int obtenerCantidadDetalles(long id) {
        int cantidadDetalles = 0;
        String sql = "select count(cmd.id) as cantidad from ct_movimientos_detalles cmd\n"
                + "inner join productos p on cmd.productos_id = p.id\n"
                + "where cmd.movimientos_id = ? and p.productos_tipos_id not in(24);";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, id);
            ResultSet rs = pmt.executeQuery();
            if (rs.next()) {
                cantidadDetalles = rs.getInt("cantidad");
            }
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo public int obtenerCantidadDetalles() "
                    + "ubicado en la clase " + AnulacionDao.class.getName() + " error -> " + e.getMessage());
        }
        return cantidadDetalles;
    }

    public long obtenerBodegaId(long id) {
        try {
            EquipoDao equipoDao = new EquipoDao();
            BodegaBean bodega = equipoDao.findBodegaByProductoIdAnulacion(id);
            if (bodega != null) {
                return bodega.getId();
            } else {
                return 0;
            }
        } catch (DAOException ex) {
            Logger.getLogger(AnulacionDao.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    public boolean anulacionTotal(long id) {
        boolean anulacionTotal = false;
        String sql = "SELECT * from verificar_cantidades_iguales(?) as anulacion_total;";
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        try (PreparedStatement pmt = conexion.prepareStatement(sql)) {
            pmt.setLong(1, id);
            ResultSet rs = pmt.executeQuery();
            if (rs.next()) {
                anulacionTotal = rs.getBoolean("anulacion_total");
            }
        } catch (Exception e) {
            NovusUtils.printLn("ha ocurrido un error inesperado en el metodo public boolean anulacionTotal(long id) "
                    + "ubicado en la clase " + AnulacionDao.class.getName() + " error -> " + e.getMessage());
        }
        return anulacionTotal;
    }

}
