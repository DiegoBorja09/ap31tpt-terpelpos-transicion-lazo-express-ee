package com.application.useCases.datafonos;
/* public JsonObject advertenciaDeNotificacionDeCierreTurno() {
     JsonObject data = new JsonObject();
     String sql = "select true as cierre from lt_horarios lh "
            + " where ((extract(EPOCH from ( lh.hora_fin - now()::time )) / 60)) >0"
            + " and  ((extract(EPOCH from (lh.hora_fin - now()::time)) / 60)) <= 5;";
    Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
    try (Statement smt = conexion.createStatement()) {
        ResultSet rs = smt.executeQuery(sql);
        if (rs.next()) {
            data.addProperty(CERRAR_TURNO, rs.getBoolean("cierre"));
            data.addProperty(MENSAJE, "NO SE PUEDE HACER EL PROCESO DE ANULACION POR QUE EL CIERRE DE TURNO SE ENCUENTRA PROXIMO");
        } else {
            data.addProperty(CERRAR_TURNO, Boolean.FALSE);
            data.addProperty(MENSAJE, "no hay cieres pendientes");
        }
    } catch (SQLException e) {
        data.addProperty(CERRAR_TURNO, Boolean.FALSE);
        data.addProperty(MENSAJE, "ha ocurrrido un error inisperado");
        NovusUtils.printLn("ha ocurrido un error inisperado en el metodo de advertenciaDeNotificacionDeCierreTurno() " + e.getMessage());
    }
    return data; */
import com.application.core.BaseUseCases;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.firefuel.Main;
import com.google.gson.JsonObject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AdvertenciaDeNotificacionDeCierreTurnoUseCase implements BaseUseCases<JsonObject> {

    private static final String MENSAJE = "mensaje";
    private static final String CERRAR_TURNO = "cerrarTurno";

    @Override
    public JsonObject execute() {
        JsonObject data = new JsonObject();
        String sql = "select true as cierre from lt_horarios lh "
                + " where ((extract(EPOCH from ( lh.hora_fin - now()::time )) / 60)) >0"
                + " and  ((extract(EPOCH from (lh.hora_fin - now()::time)) / 60)) <= 5;";
        
        System.out.println("🔍 [CIERRE_TURNO] Ejecutando consulta SQL: " + sql);
        
        Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
        try (Statement smt = conexion.createStatement()) {
            ResultSet rs = smt.executeQuery(sql);
            System.out.println("🔍 [CIERRE_TURNO] Resultado de la consulta: " + rs);
            
            if (rs.next()) {
                boolean cierre = rs.getBoolean("cierre");
                System.out.println("🔍 [CIERRE_TURNO] Valor de cierre: " + cierre);
                data.addProperty(CERRAR_TURNO, cierre);
                data.addProperty(MENSAJE, "NO SE PUEDE HACER EL PROCESO DE ANULACION POR QUE EL CIERRE DE TURNO SE ENCUENTRA PROXIMO");
            } else {
                System.out.println("🔍 [CIERRE_TURNO] No hay cierres pendientes");
                data.addProperty(CERRAR_TURNO, Boolean.FALSE);
                data.addProperty(MENSAJE, "no hay cieres pendientes");
            }
        } catch (SQLException e) {
            System.out.println("❌ [CIERRE_TURNO] Error en la consulta: " + e.getMessage());
            data.addProperty(CERRAR_TURNO, Boolean.FALSE);
            data.addProperty(MENSAJE, "ha ocurrrido un error inisperado");
            NovusUtils.printLn("ha ocurrido un error inisperado en el metodo de advertenciaDeNotificacionDeCierreTurno() " + e.getMessage());
        }
        
        System.out.println("🔍 [CIERRE_TURNO] Resultado final: " + data);
        return data;
    }
}
