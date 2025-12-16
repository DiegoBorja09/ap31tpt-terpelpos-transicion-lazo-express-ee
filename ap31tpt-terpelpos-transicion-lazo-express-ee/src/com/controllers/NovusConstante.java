package com.controllers;
//1549 consecutivo vclaudia

import com.facade.facturacionelectronica.TipoIdentificaion;
import com.firefuel.Main;
import com.google.gson.JsonArray;
import java.awt.Color;
import java.util.Map;
import java.util.TreeMap;

public class NovusConstante {

    public static final int COMBO_OPTION_VENTA_FUERA_SISTEMA_ID = 2;

    public static String POSGRES_PORT = "5432";

    public static final String PROTOCOLO = "http://";
    public static final String PROTOCOLO_SSL = "https://";

    public static final String PREFERENCE_IP_IMPRESORA = "impresora";
    public static final String PREFERENCE_LECTOR_RFID = "lector_rfid";
    public static final String PREFERENCE_IBUTTON_PORT = "ibutton_puerto";
    public static final String PREFERENCE_LIMITE_REPORTE_GOPASS = "LIMITE_REPORTE_GOPASS";
    public static final String PREFERENCE_HOST_SERVER = "HOST_SERVER";

    public static final int CORRIENTE = 1;
    public static final int EXTRA = 3;
    public static final int DIESEL = 5;
    public static final int GAS = 6;
    public static final int GLP = 7;
    public static final int ADBLUE = 8;

    public static final Color CORRIENTE_COLOR = Color.red;
    public static final Color EXTRA_COLOR = Color.blue;
    public static final Color DIESEL_COLOR = Color.yellow;
    public static final Color GAS_COLOR = Color.green;
    public static final Color GLP_COLOR = Color.gray;
    public static final Color ADBLUE_COLOR = Color.gray;

    public static final int PORT_IMPRESORA = 9100;
    public static final int PORT_SERVER_WS_API = 10000;
    public static final int EXTRABOLD = -1;
    public static final Color ACCENT_COLOR = new Color(255, 182, 0);
    public static final Color PRIMARY_COLOR = new Color(186, 12, 47);
    public static final String PREFERENCE_IP_IMPRESORA_SOURCE = "impresora_puerto";
    public static final String PREFERENCE_HABILITAR_CONSULTA_SICOM = "HABILITAR_CONSULTA_SICOM";
    public static final String PREFERENCE_RESPONSABLE_IVA = "RESPONSABLE_IVA";
    public static final String PREFERENCE_IP_IMPRESORA_SOURCE_IP = "IP";
    public static final String PREFERENCE_LOGO_IMPRESORA = "logo";
    public static final String PREFERENCE_IP_IMPRESORA_SOURCE_SO = "SO";
    public static final String PREFERENCE_PLACA_IMPRESION = "solicitar_placa_impresion";
    public static final String PREFERENCE_FACTURACION_POS = "FACTURACION";
    public static final String PREFERENCE_POSID = "POS_ID";

    public static final String DATAFONO_ACTIVO = "activo";
    public static final String DATAFONO_ERROR = "error";
    public static final String DATAFONO_INACTIVO = "inactivo";
    public static final String DATAFONO_LOADER = "loader";
    public static boolean ventanaFE;

    public static final int STATUS_200 = 200;
    public static final int STATUS_409 = 409;
    public static final int STATUS_400 = 400;
    public static final int STATUS_404 = 404;
    public static final int STATUS_500 = 500;
    public static final int STATUS_503 = 503;

    public static final int ESTADO_DISPONIBLE = 1;
    public static final int ESTADO_LIBERAR = 7;

    public static final int ID_EFECTIVO = 1;
    public static final int ID_MEDIO_GOPASS = 20004;
    public static final int ID_TIPO_VENTA_GOPASS = 6;
    public static final int ID_TIPO_VENTA_EFECTIVO = 2;
    public static final int ID_TIPO_VENTA_APP_TERPEL = 7;
    public static final String PARAMETRO_GOPASS = "gopass_v2";
    public static final String PARAMETRO_INTEGRACION_APP_RUMBO = "INTEGRACION_APP_RUMBO";
    public static final String PRODUCTO_UREA = "UREA";

    public static final String TIPO_VENTA_KCO = "035";
    public static final String TIPO_VENTA_CAN = "009";

    public static final String ESTADO_MOVIMIENTO_KCO = "035001";
    public static final String ESTADO_MOVIMIENTO_CAN = "009001";
    public static final String ESTADO_ESPECIAL_VENTA = "X";
    public static final String TIPO_VENTA_COMBUSTIBLE = "017";
    public static final String TIPO_VENTA_COMBUSTIBLE_CREDITO = "032";

    public static final String MOV_VENTA_COMBUSTIBLE = "017006";
    public static final String MOV_VENTA_COMBUSTIBLE_CREDITO = "032001";

    public static final long ID_MEDIO_PAGO_CREDITO = 8;

    public static final long CODIGO_FAMILIA_PRODUCTO_UREA = 8;
    public static final String PARAMETER_BUSINESS_TYPE = "TIPO_NEGOCIO";
    public static final String PARAMETER_CDL = "CDL";
    public static final String PARAMETER_KCO = "KCO";
    public static final String PARAMETER_CAN = "CAN";
    public static final String PARAMETER_COMB = "COMB";
    public static final String PARAMETER_UREA = "UREA";

    public static final String PARAMETER_INTEGRACION_UREA = "INTEGRACION_UREA";
    public static final String NOMBRE_PRODUCTO_UREA = "UREA";
    public static final String TIPO_BODEGA_UREA = "V"; // Tipo de bodega para UREA (Venta)

    public static final String PARAMETER_CEDULA_DE_CIUDADANIA = "CEDULA DE CIUDADANIA";
    public static final String PARAMETER_CEDULA_DE_EXTRANJERIA = "CEDULA DE EXTRANJERIA";
    public static final String PARAMETER_NIT = "NIT";

    public static final long NUMERO_INTENTOS_CONSULTA_CLIENTE = 9;
    public static final String TIEMPO_REENVIO_FE = "TIEMPO_REENVIO_FE";

    public static final String PARAMETER_URL_SICOM = "URL_SICOM";
    public static Integer TIEMPO_MENSAJE_APPTERPEL = 30;

    public static final int DOCUMENTO_CEDULA = 13;
    public static final int DOCUMENTO_CLIENTES_VARIOS = 31;
    public static final int DOCUMENTO_TARJ_EXTRANJERIA = 21;
    public static final int DOCUMENTO_CEDULA_EXTRANJERIA = 22;
    public static final int DOCUMENTO_NIT = 31;
    public static final int DOCUMENTO_PASAPORTE = 41;
    public static final int DOCUMENTO_IDENTIFICACION_EXTRANJERO = 42;
    public static final int DOCUMENTO_NIT_DE_OTRO_PAIS = 50;
    public static final int DOCUMENTO_NUIP = 91;
    public static final int DOCUMENTO_REGISTRO_CIVIL = 11;
    public static final int DOCUMENTO_TARJETA_IDENTIFICACION = 12;

    public static final int TIEMPO_MAXIMO_FIDELIZAR = 3;//Minutos
    public static final int NEGOCIO_KCO = 1;
    public static final int NEGOCIO_CDL = 2;
    public static final int NEGOCIO_CAN = 3;

    public static final int TIPO_RESTRICCION_DINERO = 1;
    public static final int TIPO_RESTRICCION_GALONES = 2;

    public static final String TIPO_VENTA_COMBUSTIBLE_FIDELIZACION = "L";

    public static final String CARACTERES_ESPECIALES = "[A-Za-z0-9._@%:-]";
    public static final String CARACTERES_ALFANUMERICOS = "[A-Za-z0-9]";

    public static final int ID_MEDIO_CONSUMO_PROPIO = 20001;
    public static final int ID_MEDIO_BONO_TERPEL = 20000;

    /**
     * DEFINIR LOS PARAMETROS A CARGAR PARA CACHÉ AL INICIO
     */
    public static final String[] PREFERENCE_LOAD_AUTO_WA_PARAMETROS = new String[]{
        NovusConstante.PREFERENCE_RESPONSABLE_IVA, NovusConstante.PREFERENCE_MULTISURTIDORES,
        NovusConstante.PREFERENCE_VEEDER_ROOT, NovusConstante.PREFERENCE_MEDIDAS_TANQUES,
        NovusConstante.PREFERENCE_PLACA_IMPRESION, NovusConstante.PREFERENCE_IMPRESION_AUTOMATICA,
        NovusConstante.PREFERENCE_TIEMPO_AUTORIZACION, NovusConstante.PREFERENCE_FACTURACION_POS,
        NovusConstante.PREFERENCE_POSID, NovusConstante.PREFERENCE_LIMITE_REPORTE_GOPASS,
        NovusConstante.PREFERENCE_HOST_SERVER,
        NovusConstante.PREFERENCE_CODIGO_GOPASS,
        NovusConstante.TIEMPO_REENVIO_FE
    };

    public static final String[] PREFERENCE_LOAD_AUTO_PARAMETROS = new String[]{
        NovusConstante.PREFERENCE_HABILITAR_CONSULTA_SICOM,
        NovusConstante.PREFERENCE_IP_IMPRESORA
    };

    public static final String PREFERENCE_CODIGO_GOPASS = "CODIGO_GOPASS";
    public static final String PREFERENCE_IMPRESION_AUTOMATICA = "IMPRIMIR_VENTA_FINALIZADA";
    public static final String PREFERENCE_MEDIDAS_TANQUES = "solicitar_lecturas_tanques";
    public static final String PREFERENCE_VEEDER_ROOT = "HABILITAR_VEEDER_ROOT";
    public static final String PREFERENCE_MULTISURTIDORES = "TURNOS";
    public static final String PREFERENCE_COPYRIGHT = "copyright";
    public static final String PREFERENCE_TIEMPO_AUTORIZACION = "ibutton_tiempo_autorizacion";
    public static final String PREFERENCE_LECTURA_MEDIDA_TANQUE_TURNO = "LECTURA_MEDIDA_TANQUE_TURNO";
    public static final String SIN_SURTIDOR = "SIN_SURTIDOR";

    public static final String EMPRESA = "LAZO";
    public static final String MEDIDA = (EMPRESA.equals("CENPAR")) ? "GL" : "LT";
    public static final int PASSWORD_RESET = 15202015;
    public static final int PASSWORD_PARAMETRIZACION = 15202020;
    public static final int APPLICATION_WIDTH = 1280;
    public static final int APPLICATION_HEIGHT = 800;
    public static final int IDENTIFICADOR_CODIGO_BARRA = 2;
    public static final String MODULO_POS = "/POS";
    public static final String MODULO_TURNOS = MODULO_POS + "/" + "TURNOS";
    public static final String MODULO_VENTAS = MODULO_POS + "/" + "VENTAS";
    public static final String MODULO_USUARIOS = MODULO_POS + "/" + "USUARIOS";
    public static final String MODULO_REPORTES = MODULO_POS + "/" + "REPORTES";
    public static final String MODULO_STORE = MODULO_POS + "/" + "STORE";
    public static final String MODULO_CONFIGURACION = MODULO_POS + "/" + "CONFIGURACION";
    public static final String MODULO_FIDELIZACION = MODULO_POS + "/" + "FIDELIZACION";
    public static final String MODULO_CLIENTES = MODULO_POS + "/" + "CLIENTES";
    public static final String MODULO_SURTIDORES = MODULO_POS + "/" + "SURTIDORES";
    public static final String MODULO_PRODUCTOS = MODULO_POS + "/" + "PRODUCTOS";

    public static final String SUBMODULO_INICIAR_TURNO = MODULO_TURNOS + "/" + "INICIAR_TURNO";
    public static final String SUBMODULO_FINALIZAR_TURNO = MODULO_TURNOS + "/" + "FINALIZAR_TURNO";
    public static final String SUBMODULO_INFORMACION_TURNO = MODULO_TURNOS + "/" + "INFORMACION_TURNO";
    public static final String SUBMODULO_SOBRES = MODULO_TURNOS + "/" + "SOBRES";

    public static final String SUBMODULO_CONSULTAR_VENTAS = MODULO_VENTAS + "/" + "CONSULTAR_VENTAS";
    public static final String SUBMODULO_PREDETERMINAR_VENTA = MODULO_VENTAS + "/" + "PREDETERMINAR_VENTA";
    public static final String SUBMODULO_CONSUMO_PROPIO = MODULO_VENTAS + "/" + "CONSUMO_PROPIO";
    public static final String SUBMODULO_CONSULTAR_VENTA = MODULO_VENTAS + "/" + "CONSULTAR_VENTA";
    public static final String SUBMODULO_FACTURACION_ELECTRONICA = MODULO_VENTAS + "/" + "FACTURACION_ELECTRONICA";
    public static final String SUBMODULO_ANULACIONES = MODULO_VENTAS + "/" + "ANULACIONES";
    public static final String SUBMODULO_CONSECUTIVOS = MODULO_VENTAS + "/" + "CONSECUTIVOS";

    public static final String SUBMODULO_USUARIOS_REGISTRADOS = MODULO_USUARIOS + "/" + "USUARIOS_REGISTRADOS";
    public static final String SUBMODULO_REGISTRO_TAG = MODULO_USUARIOS + "/" + "REGISTRO_TAG";
    public static final String SUBMODULO_REPORTE_JORNADAS = MODULO_REPORTES + "/" + "REPORTE_JORNADAS";
    public static final String SUBMODULO_REPORTE_DIARIO = MODULO_REPORTES + "/" + "REPORTE_DIARIO";
    public static final String SUBMODULO_ABRIR_STORE = MODULO_STORE + "/" + "ABRIR_STORE";
    public static final String SUBMODULO_IMPRESORA = MODULO_CONFIGURACION + "/" + "IMPRESORA";
    public static final String SUBMODULO_SINCRONIZACION = MODULO_CONFIGURACION + "/" + "SINCRONIZACION";
    public static final String SUBMODULO_RESET_DEFAULT = MODULO_CONFIGURACION + "/" + "RESET_DEFAULT";

    public static final String SUBMODULO_CAMBIO_PRECIO = MODULO_SURTIDORES + "/" + "CAMBIO_PRECIO";
    public static final String SUBMODULO_CALIBRACIONES = MODULO_SURTIDORES + "/" + "CALIBRACIONES";
    public static final String SUBMODULO_BLOQUEO = MODULO_SURTIDORES + "/" + "BLOQUEO";
    public static final String SUBMODULO_DISPOSITIVOS = MODULO_SURTIDORES + "/" + "DISPOSITIVOS";

    public static final String SIMBOLS_PRICE = "$";
    public static final String SIMBOLS_PERCENTAGE = "%";
    public static final String SIMBOLS_VOLUMEN = "GL";
    public static final int COPYRIGHT_LZ_ID = 1;
    public static final int COPYRIGHT_FF_ID = 2;
    public static final int COPYRIGHT_FL_ID = 3;
    public static final String FORMAT_MONEY = "###,##0";
    public static final String FORMAT_MONEY_WITHOUT_ZERO = "###,###";
    public static final String FORMAT_DECIMALS = "#,##";
    public static final String FORMAT_MONEY_DECIMALS = "#,###";

    public static final String FORMAT_DATE = "dd-MM-yyyy";
    public static final String FORMAT_DATE_SQL = "yyyy-MM-dd";
    public static final String FORMAT_DATETIME_SQL = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DATETIME_SQL_A = "yyyy-mm-dd hh:mm:ss";
    public static final String FORMAT_DATETIME_HUMAN = "yyyy-MM-dd hh:mm:ss a";
    public static final String FORMAT_DATETIME_24 = "dd-MM-yyyy HH:mm:ss";
    public static final String FORMAT_DATETIME_AM = "dd-MM-yyyy hh:mm:ss a";

    public static final String DATETIME_AM = "dd-MM-yyyy hh:mm:ss a";
    public static final String FORMAT_TIME_AM = "hh:mm:ss a";
    public static final String FORMAT_TIME_24 = "HH:mm";
    public static final String FORMAT_TIME_HH = "HH";
    public static final String FORMAT_TIME_BASIC_AM = "hh:mm a";
    public static final String FORMAT_FULL_DATE = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String FORMAT_DATE_ISO = " yyyy-MM-dd'T'HH:mm:ss";
    public static final String FORMAT_FULL_DATE_ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String FORMAT_FULL_DATE_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss'-05:00'";
    public static final String FORMAT_PROCESS = "yyyyMMddHHmmssSSS";
    public static final String FORMAT_PROCESS_MIN = "MMddHHmmss";
    public static final String FORMAT_BASIC_DATETIME_AM = "dd-MM-yyyy hh:mm aa";
    public static final String FORMAT_BASIC_DATE = "dd-MM hh:mm aa";

    public static final String SIMPLE_FORMAT = "dd-MM-YYYY HH:mm:ss";

    public static final float MONTO_FE_REQUERIDO = 185540f;

    public static final int SELECCION_PROMOTOR_SOBRES = 1;
    public static final int SELECCION_PROMOTOR_INFORMACION_TURNOS = 2;
    public static final int SELECCION_PROMOTOR_VENTAS = 4;
    public static final int SELECCION_PROMOTOR_STORE = 3;
    public static final int SELECCION_PROMOTOR_KCO = 30;
    public static final int SELECCION_PROMOTOR_AUTORIZACION_VENTA = 31;
    public static final int SELECCION_PROMOTOR_RUMBO = 32;
    public static final int SELECCION_PROMOTOR_VENTAS_HISTORICA = 33;
    public static final int SELECCION_PROMOTOR_FACT_MANUAL = 34;
    public static final int SELECCION_PROMOTOR_TIPO_VENTA = 35;

    public static final int SELECCION_PROMOTOR_GOPASS = 20;

    public static final int SELECCION_PROMOTOR_FACTURACION_ELECTRONICA = 5;
    public static final int SELECCION_PROMOTOR_PREDETERMINAR = 6;
    public static final int SELECCION_PROMOTOR_CALIBRACION = 7;
    public static final int SELECCION_PROMOTOR_CONSUMO_PROPIO = 8;
    public static final int SELECCION_PROMOTOR_ENTRADA_COMBUSTIBLE = 9;
    public static final int SELECCION_PROMOTOR_INFORMACION_TURNOS_CONSOLIDADOS = 10;
    public static final int SELECCION_PROMOTOR_INFORMACION_VENTAS = 11;

    public static final int FACTOR_INVENTARIO = -1000;
    public static final int TIPO_PRODUCTO_NORMAL = 23;
    public static final int PRODUCTO_DESPACHO_COMBUSTIBLE_VENTA_MIXTA = -2;
    public static final int TIPO_PRODUCTO_COMPUESTO = 25;
    public static final int TIPO_PRODUCTO_PRODUCIDOS = 26;
    public static final int TIPO_PRODUCTO_PROMOCION = 32;

    public static final String SIN_GRUPO = "SIN CLASIFICACION";
    public static final String CATEGORIA = "categoria";
    public static final String PRODUCTOS = "productos";

    public static final String CONSECUTIVO_ESTADO_ACTIVO = "A";
    public static final String CONSECUTIVO_ESTADO_USO = "U";
    public static final String CONSECUTIVO_ESTADO_VENCIDO = "V";

    public static final String ACTIVE = "A";
    public static final String BLOQUEADO = "B";
    public static final String INACTIVE = "I";
    public static final String SI = "S";
    public static final String NO = "N";

    public static final String VENTA_ANULADA_CODIGO = "012002";

    public static final int DESCARGAR_DATOS_BASICOS_ID = 2; //EMPRESA, PERFILES, PARAMETROS, LOGO
    public static final int DESCARGAR_CATEGORIAS_ID = 5; // CATEGORIAS CANASTILLA
    public static final int DESCARGAR_BODEGAS_ID = 8; //BODEGAS Y CONSECUTIVOS CANASTILLA
    public static final int DESCARGAR_PRODUCTOS_CANASTILLA_ID = 3; //PRODUCTOS CANASTILLA
    public static final int DESCARGAR_SURTIDORES_ID = 15; //SURTIDORES, PRODUCTOS COMB, TANQUES, AFORO, TIPO, DETALLES, PROTOCOLOS, SURTIDORES_ESTADO
    public static final int DESCARGAR_KARDEX_ID = 13; // KARDEX CANAST.
    public static final int DESCARGAR_PERSONAS_ID = 1;
    public static final int DESCARGAR_MEDIOS_ID = 6;
    public static final int DESCARGAR_AJUSTE_INICIAL = 14;
    public static final int DESCARGAR_CONSECUTIVOS = 4;
    public static final int SINCRONIZAR_AFORO = 45;
    public static final int SINCRONIZAR_DATAFONOS = 122;

    //AUN NO ESTAN EN EL PL DE LAZO-MQTT
    public static final String[] NOTIFICACIONES_PERMITIDAS_AUTOMATICAS = {
        String.valueOf(DESCARGAR_PERSONAS_ID),
        String.valueOf(SINCRONIZAR_AFORO),
        String.valueOf(DESCARGAR_MEDIOS_ID),
        String.valueOf(DESCARGAR_AJUSTE_INICIAL),
        String.valueOf(DESCARGAR_KARDEX_ID)
    };

    public static final int CONTACTO_TIPO_CORREO = 1;
    public static final int CONTACTO_TIPO_TELEFONO = 2;
    public static final int CONTACTO_TIPO_DIRECCION = 3;
    public static final int CONTACTO_TIPO_SITIO_WEB = 4;
    public static final int CONTACTO_TIPO_SOCIAL = 5;

    public static final String PARAMETRO_IMPRIMIR_SOBRES = "imprimir_sobres";
    public static final String PARAMETRO_RFID_V1 = "lector_rfid";
    public static final String PARAMETRO_RFID_V2 = "rfid2_puerto";
    public static final String PARAMETRO_RFID_CARA = "rfid2_cara";
    public static final String PARAMETRO_W_FIDELIZACION = "FIDELIZACION";
    public static final String PARAMENTO_IBUTTON_SERIAL = "ibutton_cara";
    public static final String PARAMETRO_IBUTTON_PUERTO = "ibutton_puerto";
    public static final String PARAMETRO_DIGITOS_SURTIDOR = "cantidad_digitos_s1";
    public static final String PARAMETRO_TIPO_AUTO = "tipo_autorizacion";

    public static final String POSGRES_USER = "pilotico";
    public static final String POSGRES_CONTRASENA = "$2y$12$UWpxiZi3UaF7ZyKeySCpB.5Z5FfRtAAkgYuQz.m4qnLUFR7CmTOu";

    public static final String POSGRES_DBNAME = "lazoexpressregistry";
    public static final String LAZOEXPRESSREGISTRY = "lazoexpressregistry";
    public static final String LAZOEXPRESSCORE = "lazoexpresscore";

    public static final boolean ENABLE_HTTPS = true;
    public static final boolean DISABLE_HTTPS = false;
    public static final int MOVIMIENTO_TIPO_COMPLEMENTARIO = 9;
    public static final int MOVIMIENTO_TIPO_KIOSCO = 35;
    public static final int MOVIMIENTO_TIPO_COMBUSTIBLE = 17;
    public static final int MOVIMIENTO_TIPO_SOBRES = 13;
    public static final long PRODUCTOS_TIPOS_COMBUSTIBLE = -1;
    public static final long PRODUCTOS_TIPOS_NORMAL = 4;
    public static final long PRODUCTOS_TIPOS_INGREDIENTE = 6;
    public static String HOST_END_POINT = "servicio.terpelpos.com";
    public static final String HOST_INTERNET = "falcon.crowdstrike.com";
    public static final String HOST_CENTRAL_POINT = "localhost";
    public static final String HOST_CORE_POINT = HOST_CENTRAL_POINT + ":8000/api";
    public static final String POST = "POST";
    public static final String OPTIONS = "OPTIONS";
    public static final String GET = "GET";
    public static final String PUT = "PUT";

    public static final String UUID = "ef115004-39bd-4c51-945c-5ce4eb0c23c9";
    public static final String APLICATION = "LAZO_EXPRESS_MANAGER";

    public static final String SECURE_CENTRAL_POINT_ACUMULACION_CLIENTE = "http://" + HOST_CENTRAL_POINT + ":8010/v2.0/lazo.fidelizacion/acumulacionCliente";
    public static final String SECURE_CENTRAL_POINT_CONSULTA_CLIENTE = "http://" + HOST_CENTRAL_POINT + ":8010/v2.0/lazo.fidelizacion/validacionCliente";
    public static final String SECURE_CENTRAL_POINT_CONSULTA_VOUCHER = "http://" + HOST_CENTRAL_POINT + ":8010/v2.0/lazo.fidelizacion/validacionBono";
    public static final String SECURE_CENTRAL_POINT_RECLAMACION_CLIENTE = "http://" + HOST_CENTRAL_POINT + ":8010/v2.0/lazo.fidelizacion/redencionCliente";

    public static String SECURE_END_POINT_PRODUCTOS_POS_ACUERDOS_NEW = ":7002/api/pos/productos/canastilla";
    public static String SOURCE_END_POINT_FACTURA_ELECTRONICA = ":7011/guru.facturacion/EnviarDatosMovimientoDian";
    public static String SOURCE_END_POINT_CONSULTA_CLIENTE = ":7011/proxi.terpel/consultarCliente";
    public static final String SECURE_END_POINT_VALIDAR_LICENCIA = "http://localhost:8012/v1.0/mqtt/register";
    public static String SECURE_END_POINT_PRODUCTOS_INGREDIENTES = ":7001/api/producto/compuesto";

    public static String SECURE_CENTRAL_POINT_EMPLEADOS_TURNOS_CONSULTA_CONSOLIDADO = "http://" + HOST_CENTRAL_POINT + ":8010/api/reportes/listado/informacionTurnosConsolidado";
    public static String SECURE_CENTRAL_POINT_INFO_EMPRESA = ":7002/api/pos/estacion/info";
    public static String SECURE_CORE_POINT_CORRECCION_SALTOS = "http://" + HOST_CORE_POINT + "";
    public static String SECURE_END_POINT_BODEGA = ":7002/api/pos/bodegas/empresa";
    public static final String SECURE_CENTRAL_POINT_REPORTE_SOBRES_CONSOLIDADO = "http://" + HOST_CENTRAL_POINT + ":8010/api/reportes/caja-sobres-consolidado";
    public static String SECURE_END_POINT_CATEGORIAS = ":7002/api/pos/categorias/detalladas";
    public static String SECURE_END_POINT_PERSONAL = ":7002/api/pos/persona/empresa";
    public static final String SECURE_CENTRAL_POINT_IMPRIMIR_CONSOLIDADO_CIERRE = "http://" + HOST_CENTRAL_POINT + ":8019/api/imprimir";

    public static final String SECURE_CENTRAL_POINT_IMPRIMIR_ARQUEO_PROMOTOR = "http://" + HOST_CENTRAL_POINT + ":8019/api/tirilla/auditoria/promotor";

    public static final String SECURE_CENTRAL_POINT_IMPRIMIR_CONSOLIDADO_CIERRE_DIARIO = "http://" + HOST_CENTRAL_POINT + ":8019/api/imprimir";
    public static final String SECURE_CENTRAL_POINT_SOBRES = "http://" + HOST_CENTRAL_POINT + ":8019/api/sobres";
    public static final String SECURE_CENTRAL_POINT_EMPLEADOS_JORNADAS_CONSULTA = "http://" + HOST_CENTRAL_POINT + ":8010/api/reportes/informacionTurno";
    public static final String SECURE_CENTRAL_POINT_EMPLEADOS_JORNADAS_CONSULTA_CONSOLIDADO = "http://" + HOST_CENTRAL_POINT + ":8010/api/reportes/informacionTurnoConsolidado";
    public static final String SECURE_CENTRAL_SURTIDOR_TOTALIZADOR = "http://" + HOST_CENTRAL_POINT + ":" + Main.puertoTotalizadores + "/api/aperturaTurno";
    public static final String SECURE_CENTRAL_POINT_EMPLEADOS_JORNADAS_INICIAR = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/jornada/iniciar";
    public static final String SECURE_CENTRAL_POINT_IMPRIMIR_DETALLE_INVENTARIO_TANQUE = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/imprimir/lecturas/tanques";
    public static final String SECURE_CENTRAL_POINT_IMPRIMIR_DETALLE_ENTRADA = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/servicios/entradasAcciones/imprimirVenta";
    public static final String SECURE_CENTRAL_POINT_SALE_AUTHORIZATION_RUMBO = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/v1.0/lazo-rumbo/admin/authorization";

    public static final String SECURE_CENTRAL_POINT_CONSULTAR_CONEXION_HOST_SURTIDOR = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/";

    public static String SECURE_CENTRAL_POINT_SALE_AUTHORIZATION_LAZO = "https://serviciodev.terpelpos.com:7897/api/v1/Clientes/authorizer/cliente-propio";
    public static String SECURE_CENTRAL_POINT_SALE_AUTHORIZATION_LAZO_GLP = ":7001/api/vehiculo/autorizacion-cargue/idrom";
    public static String SECURE_CENTRAL_POINT_CONSULTA_PLACA_CLIENTES_PROPIOS = ":7001/api/cupos/validar-cupo-placa/v1";
    public static String BACKEND_POINT_VALIDAR_PLACA_GLP = PROTOCOLO+HOST_CENTRAL_POINT + ":49153/placas/validar";

    public static final String SECURE_CENTRAL_POINT_ADITIONAL_DATA = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/v1.0/lazo-rumbo/admin/additional-data-multiple";

    public static final String SECURE_URL_SICOM_GLP = PROTOCOLO_SSL + "eds.sicom.gov.co/eds/api/v1/autoglp/";

    public static final String SECURE_CETNRAL_POINT_REPORTE_RECEPCION_COMBUSTIBLE = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/reportes/entradas/combustible/criteria";
    public static final String SECURE_CENTRAL_POINT_EMPLEADOS_JORNADAS_FIN = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/jornada/finalizar";
    public static final String SECURE_CENTRAL_POINT_EMPLEADOS_JORNADAS_ACTUAL = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/reportes/informacionTurno";
    public static final String SECURE_CENTRAL_POINT_EMPLEADOS_TURNOS_CONSULTA = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/reportes/listado/informacionTurnos";
    public static final String SECURE_CENTRAL_POINT_DETALLES_VENTA_PROMOTOR = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/reportes/finTurno";
    public static final String SECURE_CENTRAL_POINT_REGISTRAR_SOBRES = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/movimiento/caja-sobres";
    public static final String SECURE_CENTRAL_POINT_REPORTE_SOBRES = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/reportes/caja-sobres";
    public static final String SECURE_CENTRAL_POINT_PRINT_TICKET_SHIFTS = "http://localhost:8001/print-ticket/shifts";
    public static String SECURE_END_POINT_MEDIOS_PAGOS = ":7002/api/pos/medios-pago";
    public static String SECURE_END_POINT_INVENTARIO = ":7002/api/pos/inventario-bodega";
    public static String SECURE_END_POINT_KARDEX = ":7002/api/pos/inventario/estacion";
    public static String SECURE_END_POINT_SURTIDORES = ":7002/api/pos/info-surtidor-v2";
    public static String SECURE_END_POINT_PRODUCTOS_POS_COMBUSTIBLE = ":7002/api/pos/productos/combustible";

    public static final String SECURE_END_POINT_CAMBIO_MEDIOSPAGOS = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/venta/medios-de-pagos";
    public static final String SECURE_CENTRAL_POINT_EMPLEADOS_TURNOS_DIARIO = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/reportes/dia-completo";
    public static final String SECURE_CENTRAL_POINT_EMPLEADOS_VENTAS_CONSULTA = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/reportes/ventas";
    public static final String SECURE_CENTRAL_POINT_EMPLEADOS_VENTAS_ANULADAS_CONSULTA = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/reportes/ventas-anuladas";
    public static final String SECURE_CENTRAL_POINT_IMPRESION_VENTA = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/imprimir";
    public static final String SECURE_CENTRAL_POINT_ACTUALIZAR_ATRIBUTOS_VENTA = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/venta/actualizar-datos-ventas";
    public static final String SECURE_CENTRAL_POINT_SUBIR_VENTA = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/venta/subir";
    public static final String SECURE_CENTRAL_POINT_SUBIR_VENTA_KIOSCO = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/venta/kioscos/subir";
    public static String SECURE_END_POINT_ASIGNACION_TAG = ":7001/api/medios-identificacion/create-identificador-persona";
    public static final String SECURE_CENTRAL_POINT_OBTENER_PARAMETROS = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/reportes/parametros";
    public static final String SECURE_CENTRAL_POINT_CREAR_PARAMETROS = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/reportes/parametro";
    public static final String SECURE_CENTRAL_POINT_OBTENER_TANQUES = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/servicios/bodegas";
    public static final String SECURE_CENTRAL_POINT_ENVIAR_MEDIDAS_TANQUES = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/jornada/medidasTanques";

    public static final String SECURE_CENTRAL_POINT_OBTENER_MEDIDAS_VEEDER_ROOT = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/lazo.telemetria/solicitudMedidasTanques";
    public static final String SECURE_CENTRAL_POINT_OBTENER_MEDIDAS_VEEDER_ROOT_POR_NUMERO = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/lazo.telemetria/solicitudMedidasTanque";

    public static final String SECURE_CENTRAL_POINT_PREVENTA = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/servicios/surtidor/preAutorizacionVenta";
    public static final String SECURE_CENTRAL_POINT_OBTENER_AFORO = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/servicios/bodegas/medidas";
    public static final String SECURE_CENTRAL_POINT_FAC_ELECTRONICA = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/venta/electronica";
    public static final String SECURE_CENTRAL_POINT_CONSECUTIVOS_FACTURAS = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/venta/consecutivos";
    public static final String SECURE_CENTRAL_POINT_ANULACIONES = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/venta/anulacionVentaPos";
    public static final String SECURE_CENTRAL_POINT_ANULACION_FE = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/venta/anulacionVentaElectronica";
    public static final String SECURE_CENTRAL_POINT_ANULACION_FE_PARCIAL = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/venta/partialAnnulment";
    public static final String SECURE_CENTRAL_POINT_VENTA_MANUAL = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/venta/venta_manual";
    public static final String SECURE_CENTRAL_POINT_CREAR_CONSECUTIVOS = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/consecutivos";
    public static final String SECURE_CENTRAL_POINT_REPORTE_CANASTILLA = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/reportes/ventas-canastilla";
    public static final String SECURE_CENTRAL_POINT_REPORTE_KIOSCO = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/reportes/ventas-kiosco";
    public static final String SECURE_CENTRAL_POINT_FIN_TURNO_RESOLUCION = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/reportes/finTurnoPorResolucion";
    public static final String SECURE_CENTRAL_POINT_FIN_TURNO_RESOLUCION_V2 = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/reportes/finTurnoPorResolucionV2";
    public static final String SECURE_CENTRAL_POINT_SINCRONIZACION_CENTRALIZADOR = PROTOCOLO + HOST_CENTRAL_POINT + ":8012/v1.0/mqtt/sync-up";
    public static final String SECURE_CENTRAL_POINT_ENTRADA_COMBUSTIBLE = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/servicios/inventario/nuevaEntrada";
    public static final String SECURE_CENTRAL_POINT_VENTA_CONSUMO_PROPIO = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/venta/cambiarVentaNormalAConsumoPropio";
    public static final String SECURE_CENTRAL_POINT_CORREGIR_SALTOS_LECTURAS = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/servicios/correccionSaltoLectura";
    public static final String SECURE_CENTRAL_POINT_ENVIAR_IMPRESION = PROTOCOLO + HOST_CENTRAL_POINT + ":8063/v1/print-bill";
    
    // 🚀 NUEVO SERVICIO PYTHON - ARQUITECTURA HEXAGONAL
    public static final String SECURE_CENTRAL_POINT_PYTHON_PRINT_FACTURA_ELECTRONICA = PROTOCOLO + HOST_CENTRAL_POINT + ":8001/api/imprimir/factura-electronica";

    public static final String SECURE_CENTRAL_POINT_ENVIAR_PAGO_GOPASS = PROTOCOLO + HOST_CENTRAL_POINT + ":7011/api/pagoGoPass";
    public static final String SECURE_CENTRAL_POINT_BUSCAR_PLACAS_GOPASS = PROTOCOLO + HOST_CENTRAL_POINT + ":7011/api/consulta_placas/";
    public static final String SECURE_CENTRAL_POINT_CONSULTAR_ESTADO = PROTOCOLO + HOST_CENTRAL_POINT + ":7011/api/consultaEstado";

    public static final String SECURE_CENTRAL_POINT_ENVIAR_PAGO_DATAFONO = PROTOCOLO + "127.0.0.1:8079/api/compras";
    public static final String SECURE_CENTRAL_POINT_ENVIAR_ANULACION_DATAFONO = PROTOCOLO + "localhost:8079/api/compras/anular";
    public static final String SECURE_CENTRAL_POINT_CONSULTAR_PLACA_RUMBO = ":8700/api/v1/authorization/check";
    public static final String SECURE_CENTRAL_POINT_CONFIRMAR_VENTA_APP_RUMBO = ":8700/api/v1/authorization/confirm";
    public static final String SECURE_CENTRAL_POINT_LIBERAR_AUTORIZACION_APP_RUMBO = ":8700/api/v1/authorization/release/";
    public static final String SECURE_CENTRAL_POINT_NOTIFICAR_AUTORIZACION_APP_RUMBO = PROTOCOLO + HOST_CENTRAL_POINT + ":8014/v1.0/lazo-rumbo/statechange";
    public static final String SECURE_CENTRAL_POINT_NOTIFICAR_AUTORIZACION_AD_BLUE = PROTOCOLO + HOST_CENTRAL_POINT + ":8014/v1.0/lazo-rumbo/auth/authorization-adblu";
    public static final String SECURE_CENTRAL_POINT_LIBERAR_AUTORIZACION_AD_BLUE = PROTOCOLO + HOST_CENTRAL_POINT + ":8014/v1.0/lazo-rumbo/auth/liberacion-adblu";
    public static final String SECURE_CENTRAL_POINT_FINALIZAR_AD_BLUE = PROTOCOLO + HOST_CENTRAL_POINT + ":8014/v1.0/lazo-rumbo/auth/finalizacion-adblu";

    public static final String SOURCE_END_POINT_ACTULIZAR_ESTADO_REMISION = ":7002/api/sap/actualizar-remisiones";
    public static final String SOURCE_END_POINT_CONSULTAR_REMISION = ":7002/api/sap/remisiones-pendientes-pos";
    public static final String SECURE_CENTRAL_POINT_LIBER_AUTORIZACION_CLIENTES_PROPIOS = ":7008/api/venta/liberarAutorizacion";
    public static final String SECURE_CENTRAL_POINT_PRINT_TICKET_SALES = PROTOCOLO + HOST_CENTRAL_POINT +":8001/print-ticket/sales";
    public static final String SECURE_CENTRAL_POINT_PRINT_TICKET_REPORTS = PROTOCOLO + HOST_CENTRAL_POINT +":8001/print-ticket/reports";
    public static final String SECURE_CENTRAL_POINT_PRINT_TICKET_HEALTH = PROTOCOLO + HOST_CENTRAL_POINT +":8001/health";
    // ========== PRINT TICKET SERVICE - INVENTARIO ==========
    // Servicio de impresión Python (Puerto 8001)
    public static final int PORT_PRINT_TICKET_SERVICE = 8001;

    public static final String PRINT_TICKET_SERVICE_HOST = HOST_CENTRAL_POINT + ":" + PORT_PRINT_TICKET_SERVICE;

    // Endpoints de inventario
    public static final String PRINT_TICKET_INVENTORY_PRINT = PROTOCOLO + PRINT_TICKET_SERVICE_HOST + "/print-ticket/inventory/print";

    public static final String PRINT_TICKET_INVENTORY_PRINT_POSITIVE = PROTOCOLO + PRINT_TICKET_SERVICE_HOST + "/print-ticket/inventory/print-positive";

    // Tipos de inventario soportados
    public static final String INVENTORY_TYPE_CANASTILLA = "CANASTILLA";
    public static final String INVENTORY_TYPE_KIOSCO = "KIOSCO";
    public static final String INVENTORY_TYPE_CDL = "CDL";
    public static final String INVENTORY_TYPE_MARKET = "MARKET";

    // ========== PRINT TICKET SERVICE - SHIFT REPORTS ==========
    // Endpoints de reportes de turno (Opción 3 y 5)
    public static final String PRINT_TICKET_SHIFT_INFORMATION = PROTOCOLO + PRINT_TICKET_SERVICE_HOST + "/print-ticket/shift-reports/information";

    public static final String PRINT_TICKET_SHIFT_CONSOLIDATED = PROTOCOLO + PRINT_TICKET_SERVICE_HOST + "/print-ticket/shift-reports/consolidated";

    // ========== PRINT TICKET SERVICE - TURNO (INICIO/FIN INDEPENDIENTE) ==========
    // Endpoints independientes para inicio y fin de turno (sin LazoExpress)
    public static final String PRINT_TICKET_TURNO_INICIO = PROTOCOLO + PRINT_TICKET_SERVICE_HOST + "/print-ticket/turno/inicio";
    
    public static final String PRINT_TICKET_TURNO_FIN = PROTOCOLO + PRINT_TICKET_SERVICE_HOST + "/print-ticket/turno/fin";

    // Debug mode para Print Ticket Service (false para producción)
    public static final boolean DEBUG_PRINT_TICKET = false;

    public static final String PAGO_UNICO = "UNICO";
    public static final String PAGO_MIXTO = "MIXTO";
    public static final String PAGO_EFECTIVO_MIXTO = "EFECTIVO_MIXTO";
    public static final String PAGO_BONO_MIXTO = "BONO_MIXTO";

    public static final String CODIGO_DATAFONO = "codigoDatafono";
    public static final String PROVEEDOR = "proveedor";
    public static final String PROMOTOR = "promotor";
    public static final String POS = "pos";
    public static final String PROVEEDOR_ID = "proveedorId";
    public static final String TRANSACCION_DATAFONO = "idTransaccion";
    public static final String VIGENCIA = "vigencia";
    public static final String CLAVE_SUPERVISOR = "claveSupervisor";
    public static boolean BASE_DE_dATOS_ACTIVA = true;
    public static JsonArray MAPEO_SURTIDOR = new JsonArray();
    public static JsonArray AUTODIAGNOSTICO_SURTIDOR = new JsonArray();

    public static final String SECURE_CENTRAL_POINT_RECUPERAR_VENTA_CREDITO = PROTOCOLO + HOST_CENTRAL_POINT + ":8010/api/venta/recuperar-venta-credito";
    public static boolean ESTADO_SURTIDOR;
    public static boolean VENTAS_CONTINGENCIA;
    public static boolean KIOSCO_CAN_CONTINGENCIA;
    public static boolean HAY_INTERNET;
    public static boolean ACTIVAR_BOTON_CONTINGENCIA;
    //codigos de tipos de notas credito
    public static int TIPO_ANULACION_NOTA_CREDITO;

    //aplica factura electronica para la estacion
    static final Map<String, TipoIdentificaion> TIPOS_IDENTIFICACION = new TreeMap<>();

    //aplica factura electronica para la estacion
    public static boolean APLICA_FE;
    public static boolean IS_DEFAULT_FE;
    public static boolean cerrarVentanaActivo = false;

    //GOPASS
    public static final int GOPASS_CANTIDAD_MAXIMA_REINTENTOS_PAGOS = 3;

    public static String getServer(String api) {
        return "https://" + HOST_END_POINT + api;
    }

    public static void setEstadoDeLaBaseDeDatos(boolean activo) {
        BASE_DE_dATOS_ACTIVA = activo;
    }

    public static void setDataMapeoSurtidor(JsonArray data) {
        MAPEO_SURTIDOR = data;
    }

    public static void setDataAutoDiagnosticoSurtidor(JsonArray data) {
        AUTODIAGNOSTICO_SURTIDOR = data;
    }

    public static void setTiposIdentificaion(Map<String, TipoIdentificaion> tiposIdentificacion) {
        TIPOS_IDENTIFICACION.clear();
        TIPOS_IDENTIFICACION.putAll(tiposIdentificacion);
    }

    public static Map<String, TipoIdentificaion> getTiposIdentificaion() {
        return TIPOS_IDENTIFICACION;
    }
}
