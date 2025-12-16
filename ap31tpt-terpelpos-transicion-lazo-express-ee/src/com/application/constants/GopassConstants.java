package com.application.constants;


public class GopassConstants {
    
    // ========================================
    // PANELES
    // ========================================
    public static final String MENU_GOPASS = "menu_gopass";
    public static final String PAGO_GOPASS = "pago_gopass";
    public static final String ESTADO_PAGO = "estado_pago";
    public static final String PLACAS_GOPASS = "placas_gopass";
    public static final String VALIDA_PLACAS = "validad_placa";
    public static final String INFO_PAGO_GOPASS = "info_pago_gopass";
    
    // ========================================
    // ESTADOS
    // ========================================
    public static final int LOADING_PAGO = 1;
    public static final int SUCCESS_PLACA = 2;
    public static final int SUCCESS_PAGO = 2;
    public static final int ERROR_CODE = 3;
    public static final int GOPASS_INACTIVE = 4;
    public static final int LOADING_PLACAS = 6;
    public static final int ERR_PAGO = 30;
    
    // ========================================
    // RUTAS
    // ========================================
    public static final String RUTA_IMAGEN = "/com/firefuel/resources/";
    public static final String RUTA_BOTONES = RUTA_IMAGEN + "botones/";
    
    // ========================================
    // RECURSOS COMUNES
    // ========================================
    public static final String BTN_PAGO_ACTIVADO = RUTA_BOTONES + "bt-danger-normal.png";
    public static final String BTN_PAGO_INACTIVO = RUTA_BOTONES + "bt-link-normal.png";
    public static final String BTN_OK = RUTA_IMAGEN + "btOk.png";
    public static final String BTN_ERROR = RUTA_IMAGEN + "btBad.png";
    public static final String LOADER_FAC = RUTA_IMAGEN + "loader_fac.gif";

    // ========================================
    // MENSAJES
    // ========================================
    public static final String MSG_PROCESANDO_PAGO = "<html><center>Procesando pago...</center></html>";
    public static final String MSG_PAGO_EXITOSO = "<html><center>Pago enviado a Gopass correctamente<br></center></html>";
    public static final String MSG_ERROR_PAGO = "<html><center>NO SE PUDO PROCESAR EL PAGO <br>MOTIVO: ";
    public static final String MSG_CONSULTANDO_PLACAS = "<html><center>CONSULTANDO PLACAS</center></html>";
    public static final String MSG_BUSCANDO_PLACAS = "<html><center>Buscando las placas</center></html>";
    public static final String MSG_ERROR_CONEXION_INTERNET = "<html><center>CONSULTANDO PLACAS</center></html>";
    public static final String MSG_ERROR_GOPASS_NO_ACTIVADO = "<html><center>Pago con GoPass no activado</center></html>";
    public static final String MSG_ERROR_FALLO_RED = "<html><center>Fallo de red - Error de conexión - intente con otro medio de pago</center></html>";

    // ========================================
    // IMPRESIÓN
    // ========================================
    public static final String FUNCION_IMPRIMIR_VENTAS = "IMPRIMIR VENTA GOPASS";
    public static final String TIPO = "CONSULTAR_VENTAS";
    public static final String TIPO_DOCUMENTO_DEFAULT = "13";
    public static final String NUMERO_DOCUMENTO_DEFAULT = "2222222";
    public static final int IDENTIFICADOR_TIPO_PERSONA_DEFAULT = 1;
    public static final String NOMBRE_COMERCIAL_DEFAULT = "CLIENTES VARIOS";
    public static final String CIUDAD_DEFAULT = "BARRANQUILLA";
    public static final String DEPARTAMENTO_DEFAULT = "ATLANTICO";
    public static final String REGIMEN_FISCAL_DEFAULT = "48";
    public static final String TIPO_RESPONSABILIDAD_DEFAULT = "R-99-PN";
    public static final String CODIGO_SAP_DEFAULT = "0";
    public static final String CONTENT_TYPE_JSON = "application/json";

}
