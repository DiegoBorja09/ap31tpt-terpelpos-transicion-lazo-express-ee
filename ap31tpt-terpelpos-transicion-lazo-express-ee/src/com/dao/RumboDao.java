package com.dao;

import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.firefuel.Main;
import com.google.gson.JsonObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RumboDao {

    // public boolean insertarTransmision(String autorizacion) {
    //     boolean insertado = false;
    //     Connection conexion = Main.obtenerConexion("lazoexpresscore");
    //     String sql = "select * from public.fnc_insertar_autorizacion(?::json)";
    //     try (PreparedStatement psmt = conexion.prepareStatement(sql)) {
    //         psmt.setString(1, autorizacion);
    //         ResultSet rs = psmt.executeQuery();
    //         while (rs.next()) {
    //             insertado = rs.getBoolean("fnc_insertar_autorizacion");
    //         }
    //     } catch (SQLException e) {
    //         NovusUtils.printLn(e.getMessage());
    //     }
    //     return insertado;
    // }
    // Método migrado a InsertarTransmisionUseCase usando TransmisionRepository

    // public boolean insertarConfirmacionVentaApp(String autorizacion) {
    //     boolean insertado = false;
    //     Connection conexion = Main.obtenerConexion("lazoexpresscore");
    //     String sql = "select * from public.fnc_insert_tbl_autorizaciones_pos(?::json)";
    //     try (PreparedStatement psmt = conexion.prepareStatement(sql)) {
    //         psmt.setString(1, autorizacion);
    //         ResultSet rs = psmt.executeQuery();
    //         while (rs.next()) {
    //             insertado = rs.getBoolean("fnc_insert_tbl_autorizaciones_pos");
    //         }
    //     } catch (SQLException e) {
    //         NovusUtils.printLn(e.getMessage());
    //     }
    //     return insertado;
    // }
    // Método migrado a InsertarConfirmacionVentaAppUseCase usando TransmisionRepository
    // NOTA: Actualmente no se usa en el código, pero se mantiene preparado para futuro uso

    // public long codigoExternoProductoUREA() {
    //     long codigoExterno = 0l;
    //     Connection conexion = Main.obtenerConexion("lazoexpresscore");
    //     String sql = "select\n"
    //             + "coalesce ((\n"
    //             + "select\n"
    //             + "(p.p_atributos::json->>'codigoExterno')::numeric\n"
    //             + "from\n"
    //             + "productos p\n"
    //             + "where\n"
    //             + "p.descripcion = ?), 0)codigo;";
    //     try (PreparedStatement psmt = conexion.prepareStatement(sql)) {
    //         psmt.setString(1, NovusConstante.NOMBRE_PRODUCTO_UREA);
    //         ResultSet rs = psmt.executeQuery();
    //         while (rs.next()) {
    //             codigoExterno = rs.getLong("codigo");
    //         }
    //     } catch (SQLException e) {
    //         NovusUtils.printLn(e.getMessage());
    //     }
    //     return codigoExterno;
    // }

    // 🚀 MIGRADO A JPA: Este método ha sido migrado a VerificarIntegracionUreaUseCase
    // Ubicación: src/com/application/useCases/wacherparametros/VerificarIntegracionUreaUseCase.java
    // Uso migrado en: SetupDao.getMangueras() - línea ~261
    // Fecha migración: Diciembre 2024
    /*
    public boolean integracionUREA() {
        boolean isAdblue = false;
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "select\n"
                + "coalesce ((\n"
                + "select\n"
                + "valor\n"
                + "from\n"
                + "wacher_parametros wp\n"
                + "where\n"
                + "wp.codigo = ?),'N') resultado;";
        try (PreparedStatement psmt = conexion.prepareStatement(sql)) {
            psmt.setString(1, NovusConstante.PARAMETER_INTEGRACION_UREA);
            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                isAdblue = rs.getString("resultado").equals("S");
            }
        } catch (SQLException e) {
            NovusUtils.printLn(e.getMessage());
        }
        return isAdblue;
    }
    */

    // public float precioUREA() {
    //     float precio = 0;
    //     Connection conexion = Main.obtenerConexion("lazoexpresscore");
    //     String sql = "select p.precio  "
    //             + "from productos p where p.descripcion = ?;";
    //     try (PreparedStatement psmt = conexion.prepareStatement(sql)) {
    //         psmt.setString(1, NovusConstante.NOMBRE_PRODUCTO_UREA);
    //         ResultSet rs = psmt.executeQuery();
    //         while (rs.next()) {
    //             precio = rs.getFloat("precio");
    //         }
    //     } catch (SQLException e) {
    //         NovusUtils.printLn(e.getMessage());
    //     }
    //     return precio;
    // }

    // 🚀 MIGRADO A JPA: Este método ha sido migrado a ObtenerIdProductoUreaUseCase
    // Ubicación: src/com/application/useCases/productos/ObtenerIdProductoUreaUseCase.java
    // Repositorio: ProductoRepository.obtenerIdProductoUrea()
    // Uso migrado en: VentasCombustibleFacade.buildObjectCtMovimientoDetalles() - línea ~172
    // Fecha migración: Diciembre 2024
    /*
    public long idProductoUREA() {
        long id = 0;
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "select p.id  "
                + "from productos p where p.descripcion = ?;";
        try (PreparedStatement psmt = conexion.prepareStatement(sql)) {
            psmt.setString(1, NovusConstante.NOMBRE_PRODUCTO_UREA);
            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                id = rs.getLong("id");
            }
        } catch (SQLException e) {
            NovusUtils.printLn(e.getMessage());
        }
        return id;
    }
    */

    /**
     * ✅ MIGRADO A JPA - idBodegaUREA()
     * 
     * 📋 MIGRACIÓN COMPLETA:
     * --------------------------------------------------
     * 
     * 🎯 PROPÓSITO:
     * - Obtener ID de bodega UREA con tipo 'V' (Venta)
     * - Consulta: "SELECT cb.id FROM ct_bodegas cb WHERE cb.atributos::json->>'tipo' = 'V'"
     * 
     * 🚀 COMPONENTES CREADOS:
     * 1. SqlQueryEnum.OBTENER_ID_BODEGA_UREA
     * 2. CtBodegaRepository.obtenerIdBodegaUrea()
     * 3. ObtenerIdBodegaUreaUseCase
     * 4. NovusConstante.TIPO_BODEGA_UREA
     * 
     * 📍 USOS MIGRADOS:
     * 1. VentasCombustibleFacade.java:158 → new ObtenerIdBodegaUreaUseCase().execute()
     * 2. RumboView.java:831 → obtenerIdBodegaUreaUseCase.execute() (atributo de clase)
     * 
     * 📅 FECHA MIGRACIÓN: 2025-06-10
     * 🏗️ PATRÓN JPA: Repository + UseCase + SqlQueryEnum
     * --------------------------------------------------
     */
    /*
    public long idBodegaUREA() {
        long id = 0;
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "select cb.id from ct_bodegas cb where cb.atributos::json->>'tipo' = 'V' ;";
        try (PreparedStatement psmt = conexion.prepareStatement(sql)) {
            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                id = rs.getLong("id");
            }
        } catch (SQLException e) {
            NovusUtils.printLn(e.getMessage());
        }
        return id;
    }
    */

    // public long jornadaId() {
    //     long jornadaId = 0l;
    //     Connection conexion = Main.obtenerConexion("lazoexpresscore");
    //     String sql = "select j2.grupo_jornada jornada from jornadas j2 ;";
    //     try (PreparedStatement psmt = conexion.prepareStatement(sql)) {
    //         ResultSet rs = psmt.executeQuery();
    //         while (rs.next()) {
    //             jornadaId = rs.getLong("jornada");
    //         }
    //     } catch (SQLException e) {
    //         NovusUtils.printLn(e.getMessage());
    //     }
    //     return jornadaId;
    // }

    /**
     * 🔄 MIGRADO A JPA - InsertCtMovimientosUseCase
     * 
     * MIGRACIÓN COMPLETA:
     * ✅ Repositorio: CtMovimientoRepository.insertarCtMovimientos()
     * ✅ UseCase: InsertCtMovimientosUseCase
     * ✅ DTO: CtMovimientoJsonObjDto
     * ✅ SQL Query: SqlQueryEnum.INSERTAR_CT_MOVIMIENTOS
     * ✅ Dependencia: buildjsonIdentificadoresAutorizacion() integrada en UseCase
     * 
     * USO ACTUAL:
     * - RumboView.java:2911 → new InsertCtMovimientosUseCase().execute(ctMovimientoJsonObjDto)
     * 
     * ARQUITECTURA JPA:
     * - InsertCtMovimientosUseCase implements BaseUseCasesWithParams<CtMovimientoJsonObjDto, Boolean>
     * - CtMovimientoRepository.insertarCtMovimientos() con EntityManager
     * - Manejo de transacciones y recursos JPA
     * - Integración de buildjsonIdentificadoresAutorizacion() en UseCase
     * 
     * Fecha migración: Diciembre 2024
     * Migrado por: Compañero del equipo
     */
    /*public boolean insertCTmovimientos(JsonObject jsonMovimientos,
            JsonObject jsonMovimientosCredito,
            JsonObject jsonMovimientoDetalles,
            JsonObject jsonMovimientoMediosPago,
            JsonObject request,
            JsonObject response) {
        boolean insert = false;
        String identificacionAutorizacion = buildjsonIdentificadoresAutorizacion(response);
        Connection conexion = Main.obtenerConexion("lazoexpresscore");
        String sql = "select * from fnc_insertar_ct_movimientos(?::json,?::json,?::json,?::json,?::json,?::json,?::json);";
        try (PreparedStatement psmt = conexion.prepareStatement(sql)) {
            psmt.setString(1, jsonMovimientos.toString());
            psmt.setString(2, jsonMovimientosCredito.toString());
            psmt.setString(3, jsonMovimientoDetalles.toString());
            psmt.setString(4, jsonMovimientoMediosPago.toString());
            psmt.setString(5, request.toString());
            psmt.setString(6, response.toString());
            psmt.setString(7, identificacionAutorizacion);
            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                insert = rs.getBoolean("fnc_insertar_ct_movimientos");
            }
        } catch (SQLException e) {
            NovusUtils.printLn(e.getMessage());
        }
        return insert;
    }*/

    /**
     * 🔄 MIGRADO A JPA - Integrado en InsertCtMovimientosUseCase
     * 
     * INTEGRACIÓN COMPLETA:
     * ✅ Método privado en: InsertCtMovimientosUseCase.buildjsonIdentificadoresAutorizacion()
     * ✅ Manejo de errores mejorado con try-catch
     * ✅ JsonObject vacío en caso de error
     * 
     * PROPÓSITO:
     * - Construir JSON con identificadores de autorización desde el response
     * - Extraer identificadorAprobacion e identificadorAutorizacionEDS
     * 
     * INTEGRACIÓN EN USECASE:
     * - Llamado automáticamente durante insertarCtMovimientos()
     * - Sin necesidad de uso externo independiente
     * 
     * Fecha migración: Diciembre 2024
     * Migrado por: Compañero del equipo
     */
    /*public String buildjsonIdentificadoresAutorizacion(JsonObject response) {
        JsonObject object = new JsonObject();
        object.addProperty("identificadorAprobacion", response.get("data").getAsJsonObject().get("identificadorAprobacion").getAsString());
        object.addProperty("identificadorAutorizacionEDS", response.get("data").getAsJsonObject().get("identificadorAutorizacionEDS").getAsString());
        return object.toString();
    }*/

    // public void actualizarEstadoMovimiento(long movimientoId) {
    //     Connection conexion = Main.obtenerConexion("lazoexpresscore");
    //     String sql = "update ct_movimientos set estado_movimiento = '017002' where id = ?;";
    //     try (PreparedStatement psmt = conexion.prepareStatement(sql)) {
    //         psmt.setLong(1, movimientoId);
    //         psmt.executeUpdate();
    //     } catch (SQLException e) {
    //         NovusUtils.printLn(e.getMessage());
    //     }
    // }

    // public void actualizarMovimientos(JsonObject data) {
    //     Connection conexion = Main.obtenerConexion("lazoexpresscore");
    //     String sql = "select * from fnc_actualizar_ct_movimientos(?::json);";
    //     try (PreparedStatement psmt = conexion.prepareStatement(sql)) {
    //         psmt.setString(1, data.toString());
    //         ResultSet rs = psmt.executeQuery();
    //     } catch (SQLException e) {
    //         NovusUtils.printLn(e.getMessage());
    //     }
    // }

    // public String nombrePromotor(long id) {
    //     String nombre = "";
    //     Connection conexion = Main.obtenerConexion("lazoexpresscore");
    //     String sql = "select nombre from personas p where id = ?;";
    //     try (PreparedStatement psmt = conexion.prepareStatement(sql)) {
    //         psmt.setLong(1, id);
    //         ResultSet rs = psmt.executeQuery();
    //         while (rs.next()) {
    //             nombre = rs.getString("nombre");
    //         }
    //     } catch (SQLException e) {
    //         NovusUtils.printLn(e.getMessage());
    //     }
    //     return nombre;
    // }

}
