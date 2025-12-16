package com.WT2.appTerpel.domain.valueObject;

public class QueryCollection {

    public static String sqlGetMedioPago = "select * from public.fnc_consultar_medios_pago_imagenes(?,?);";
    public static String funGetPaymentByIdMov = "select * from  procesos.obtener_pago_por_idmov(?);";
    public static String appterpelVentasProcesadas = "select * from public.fnc_consultar_ventas_procesadas_appterpel(?,?,?)";
    public static String evaluraIntentosAppterpel = "select * from  procesos.fnc_validar_intentos_appterpel(?,?)";
    public static String validarBotoneVentasAppterpel = "select * from procesos.fnc_validar_botones_ventas_appterpel(?)";


}
