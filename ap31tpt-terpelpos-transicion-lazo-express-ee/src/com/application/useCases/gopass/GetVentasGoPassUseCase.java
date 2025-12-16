package com.application.useCases.gopass;

import com.application.core.BaseUseCases;
import com.bean.VentaGo;
import com.controllers.NovusConstante;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import org.postgresql.util.PSQLException;

import org.postgresql.util.PGobject;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import com.dao.DAOException;

/**
 * Esta clase es un caso de uso que obtiene las ventas de gopass
 * 
 * 
 * 
 * 
 */

// public ArrayList<VentaGo> getVentas() throws DAOException {
// Connection conexion = Main.obtenerConexion("lazoexpresscore");
// ArrayList<VentaGo> ventas = new ArrayList<>();
// try {
// String sql = "select c.id , c.fecha, c.venta_total, c.atributos,
// c.consecutivo, C.prefijo, C.atributos::json->>'cara' as cara, cmd.cantidad
// ,\n"
// + "cmd.precio as precio_producto , p.descripcion, ttp.id_estado_integracion,
// tep.descripcion \n"
// + " from (select c.*, row_number() over (partition by
// c.atributos::json->>'cara' order by c.fecha desc) as rn from ct_movimientos
// c) c\n"
// + "left join ct_movimientos_detalles cmd on C.id = cmd.movimientos_id\n"
// + "left join productos p on cmd.productos_id = p.id\n"
// + "left join ct_movimientos_medios_pagos cmmp on cmmp.ct_movimientos_id =
// c.id \n"
// + "left join ct_medios_pagos cmp on cmp.id = cmmp.ct_medios_pagos_id \n"
// + "left join (select * from procesos.tbl_transaccion_proceso ttp \n"
// + " where ttp.id_integracion =2 \n"
// + " and ttp.id_estado_integracion in (4,5,3)) ttp on ttp.id_movimiento =
// c.id\n"
// + "left join procesos.tbl_estados_procesos tep on ttp.id_estado_proceso =
// tep.id_estado_proceso\n"
// + "where c.rn <= 6 and C.JORNADAS_ID = (SELECT GRUPO_JORNADA FROM JORNADAS
// LIMIT 1) and (C.atributos::json->>'gopass' isnull or
// ttp.id_estado_integracion in (4,5,3) )\n"
// + "and C.fecha between (now() - '60 minutes'::interval) and now()\n"
// + "and C.atributos::json->>'cara' in (select cara::text from
// surtidores_detalles group by cara )\n"
// + "and C.tipo = '017'\n"
// + "and C.estado_movimiento = '017000'\n"
// + "and C.atributos::json->>'isCredito'='false'\n"
// + "and cmp.id = 1 \n"
// + "order by C.atributos::json->>'cara', C.fecha desc nulls last;";
// PreparedStatement ps = conexion.prepareStatement(sql);
// ResultSet re = ps.executeQuery();
// System.out.println("CONSULTA GOPASS VENTAS: " + sql);
// while (re.next()) {
// VentaGo venta = new VentaGo();
// venta.setId(re.getInt("id"));
// venta.setFecha(re.getString("fecha"));
// venta.setVentaTotal("$ " + df.format(re.getInt("venta_total")));
// JsonObject atributos = new Gson().fromJson(re.getString("atributos"),
// JsonObject.class);
// if (!atributos.isJsonNull()) {
// venta.setAtributos(atributos);
// }
// venta.setConsecutivo(re.getInt("consecutivo"));
// venta.setCantidad(re.getDouble("cantidad"));
// venta.setPrecio_producto("$ " + df.format(re.getDouble("precio_producto")));
// venta.setDescription(re.getString("descripcion"));
// venta.setPrefijo(re.getString("prefijo"));
// ventas.add(venta);
//
// }
// } catch (PSQLException s) {
// throw new DAOException("31." + s.getMessage());
// } catch (SQLException s) {
// throw new DAOException("32." + s.getMessage());
// } catch (JsonSyntaxException s) {
// throw new DAOException("33." + s.getMessage());
// }
// return ventas;
// }

public class GetVentasGoPassUseCase implements BaseUseCases<ArrayList<VentaGo>> {

    private final EntityManagerFactory entityManagerFactory;
    private final DecimalFormat df = new DecimalFormat(NovusConstante.FORMAT_MONEY);

    public GetVentasGoPassUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public ArrayList<VentaGo> execute() {
        EntityManager entityManager = null;
        ArrayList<VentaGo> ventas = new ArrayList<>();
        try {
            entityManager = entityManagerFactory.createEntityManager();
            String sql = "select c.id , c.fecha, c.venta_total, c.atributos, c.consecutivo, C.prefijo, C.atributos::json->>'cara' as cara, cmd.cantidad ,\n"
                    + "cmd.precio as precio_producto , p.descripcion, ttp.id_estado_integracion, tep.descripcion \n"
                    + " from (select c.*, row_number() over (partition by c.atributos::json->>'cara' order by c.fecha desc) as rn from ct_movimientos c) c\n"
                    + "left join ct_movimientos_detalles cmd on C.id = cmd.movimientos_id\n"
                    + "left join productos p on cmd.productos_id = p.id\n"
                    + "left join ct_movimientos_medios_pagos cmmp on cmmp.ct_movimientos_id = c.id \n"
                    + "left join ct_medios_pagos cmp on cmp.id = cmmp.ct_medios_pagos_id \n"
                    + "left join (select * from procesos.tbl_transaccion_proceso ttp \n"
                    + "	where ttp.id_integracion  =2 \n"
                    + "	and ttp.id_estado_integracion in (4,5,3)) ttp on ttp.id_movimiento = c.id\n"
                    + "left join procesos.tbl_estados_procesos tep on ttp.id_estado_proceso = tep.id_estado_proceso\n"
                    + "where c.rn <= 6 and C.JORNADAS_ID = (SELECT GRUPO_JORNADA FROM JORNADAS LIMIT 1) and  (C.atributos::json->>'gopass' isnull or  ttp.id_estado_integracion in (4,5,3) )\n"
                    + "and C.fecha between (now() - '60 minutes'::interval) and now()\n"
                    + "and C.atributos::json->>'cara' in (select cara::text from surtidores_detalles group by cara )\n"
                    + "and C.tipo = '017'\n"
                    + "and C.estado_movimiento = '017000'\n"
                    + "and C.atributos::json->>'isCredito'='false'\n"
                    + "and cmp.id = 1 \n"
                    + "order by C.atributos::json->>'cara', C.fecha desc nulls last;";

            System.out.println("CONSULTA GOPASS VENTAS: " + sql);
            
            Query query = entityManager.createNativeQuery(sql);
            List<Object[]> results = query.getResultList();

            for (Object[] result : results) {
                try {
                    VentaGo venta = new VentaGo();
                    venta.setId(((Number) result[0]).intValue());
                    venta.setFecha(result[1] != null ? result[1].toString() : null);
                    venta.setVentaTotal("$ " + df.format(((Number) result[2]).intValue()));
                    
                    String atributosStr = result[3] != null ? result[3].toString() : null;
                    if (atributosStr != null) {
                        JsonObject atributos = new Gson().fromJson(atributosStr, JsonObject.class);
                        if (!atributos.isJsonNull()) {
                            venta.setAtributos(atributos);
                        }
                    }
                    
                    venta.setConsecutivo(result[4] != null ? ((Number) result[4]).intValue() : 0);
                    venta.setCantidad(result[7] != null ? ((Number) result[7]).doubleValue() : 0.0);
                    venta.setPrecio_producto("$ " + df.format(result[8] != null ? ((Number) result[8]).doubleValue() : 0.0));
                    venta.setDescription(result[9] != null ? result[9].toString() : "");
                    
                    ventas.add(venta);
                } catch (Exception e) {
                    System.err.println("Error procesando resultado: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            return ventas;
        } catch (javax.persistence.PersistenceException e) {
            Throwable cause = e.getCause();
            String errorMessage = "Error de base de datos: ";
            if (cause != null) {
                errorMessage += cause.getMessage();
                System.err.println(errorMessage);
                cause.printStackTrace();
            } else {
                errorMessage += e.getMessage();
                System.err.println(errorMessage);
                e.printStackTrace();
            }
            throw new RuntimeException(errorMessage);
        } catch (Exception e) {
            String errorMessage = "Error obteniendo ventas de gopass: " + e.getMessage();
            System.err.println(errorMessage);
            e.printStackTrace();
            throw new RuntimeException(errorMessage);
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}
