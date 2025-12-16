package com.infrastructure.Enums;
 
public enum SqlQueryEnum {
 
    BUSCAR_CATEGORIAS_VISIBLES("SELECT id, grupo FROM grupos WHERE atributos->>'visible' = 'true'"),
    //CONSULTAR_CLIENTE_TRANSMISION("SELECT * FROM fnc_consultar_cliente_transmision(:idMovimiento)"),
    OBTENER_CATEGORIAS_KIOSCO("select * from public.fnc_obtener_categorias_market(?);"),
    OBTENER_TODAS_CATEGORIAS_CON_PRODUCTOS(
        "SELECT *, " +
        "(SELECT COUNT(1) FROM grupos_entidad ge " +
        "INNER JOIN bodegas_productos bp ON bp.productos_id = ge.entidad_id " +
        "WHERE grupo_id = g.id AND bp.saldo > 0) cantidad " +
        "FROM grupos g " +
        "WHERE (SELECT COUNT(1) FROM grupos_entidad ge " +
        "INNER JOIN bodegas_productos bp ON bp.productos_id = ge.entidad_id " +
        "INNER JOIN productos AS p ON p.id = ge.entidad_id " +
        "WHERE grupo_id = g.id AND bp.saldo > 0 " +
        "AND COALESCE(p.p_atributos::json->>'tipoStore', 'C') NOT IN('K','T')) > 0"
    ),

    CONSULTAR_CLIENTE_TRANSMISION("SELECT * FROM fnc_consultar_cliente_transmision(?)"),
    OBTENER_JORNADA("SELECT grupo_jornada FROM jornadas WHERE personas_id = :personaId"),
    OBTENER_CUALQUIER_JORNADA("SELECT grupo_jornada FROM jornadas LIMIT 1"),
    OBTENER_ID_PRODUCTO_DESDE_MOVIMIENTO("SELECT productos_id FROM ct_movimientos_detalles WHERE movimientos_id = ? LIMIT 1"),
    IS_PAGO_GOPASS("SELECT true as valor FROM ct_movimientos cm WHERE cm.id = ? AND cm.atributos::json->>'gopass' IS NOT NULL"),
    OBTENER_PERSONA("SELECT COUNT(p) FROM CtPerson p WHERE p.id = 3"),

    BUSCAR_PRODUCTOS_POR_CATEGORIA("SELECT g.id, g.grupo, p.id pid, p.plu, p.estado, p.descripcion, p.precio, " +
                    "p.tipo, p.cantidad_ingredientes, p.cantidad_impuestos, COALESCE(saldo, 0) saldo " +
                    "FROM productos p " +
                    "LEFT JOIN bodegas_productos bp ON bp.productos_id = p.id " +
                    "INNER JOIN grupos_entidad ge ON ge.entidad_id = p.id " +
                    "INNER JOIN grupos g ON g.id = ge.grupo_id " +
                    "WHERE g.id = ? AND p.estado IN('A')"),

    BUSCAR_PRODUCTOS_POR_DESCRIPCION("SELECT p.id, p.plu, p.estado, p.descripcion, p.precio, " +
                    "p.tipo, p.cantidad_ingredientes, p.cantidad_impuestos, COALESCE(saldo, 0) saldo " +
                    "FROM productos p " +
                    "LEFT JOIN bodegas_productos bp ON bp.productos_id = p.id " +
                    "WHERE p.estado IN('A') AND p.descripcion ILIKE ? " +
                    "AND p.tipo IN (23, 25, 32)"),
    GET_CATEGORIAS_MOVIMIENTO("SELECT g.id AS categoria_id, " +
                    "p.id AS producto_id, " +
                    "g.grupo AS categoria_descripcion " +
                    "FROM productos p " +
                    "INNER JOIN grupos_entidad ge ON ge.entidad_id = p.id " +
                    "INNER JOIN grupos g ON g.id = ge.grupo_id " +
                    "WHERE p.id = ANY(?::bigint[])"),

    IS_PENDIENTE_TRANSACCION_MOVIMIENTO("SELECT * FROM fnc_consultar_venta_pendiente(?) pendiente"),
    VALIDAR_ESTADO_TRANSACCION_DATAFONO("SELECT id_transaccion_estado FROM datafonos.fnc_consultar_estado(?)"),
    OBTENER_ID_MOVIMIENTO_DESDE_DATAFONO("SELECT t.id_movimiento FROM datafonos.transacciones t WHERE t.id_transaccion = ?"
    ),

    HAY_VENTA_PENDIENTE_PAGO_MIXTO("SELECT COUNT(*) FROM datafonos.transacciones t WHERE t.id_movimiento = ? AND t.id_transaccion_estado = ?"),
    BUSCAR_TRANSMISION_REMISION("SELECT * FROM transmisiones_remision WHERE sincronizado = ? "),
    OBTENER_UNIDAD_DESCRIPCION("SELECT descripcion FROM unidades WHERE id = ?"),
    NUMERO_POS("SELECT valor FROM wacher_parametros p WHERE codigo = 'POS_ID'"),
    GET_TIPOS_MOVIMIENTO(
            "SELECT tm.id_tipo_movimiento AS id, tm.descripcion FROM tipos_movimiento tm"
    ),

    GET_TANQUES("SELECT cb.id, cb.bodega FROM ct_bodegas cb"),
    SET_REAPERTURA_EN_UNO(
    "UPDATE procesos.tbl_transaccion_proceso " +
            "SET reapertura = 1 " +
            "WHERE id_integracion = 3 AND id_movimiento = ?"
    ),

    IS_VENTA_FIDELIZADA(
            "SELECT COUNT(*) FROM procesos.tbl_transaccion_proceso " +
                    "WHERE id_movimiento = ? " +
                    "AND id_integracion = 3 " +
                    "AND id_estado_integracion = 6 " +
                    "AND id_estado_proceso = 3"
    ),

    EXISTE_FIDELIZACION(
            "SELECT COUNT(*) FROM procesos.tbl_transaccion_proceso " +
                    "WHERE id_movimiento = ? " +
                    "AND id_integracion = 3 " +
                    "AND id_estado_integracion = 7 " +
                    "AND id_estado_proceso = 3"
    ),

    OBTENER_ID_REMISION_DESDE_MOVIMIENTO(
            "SELECT id_transmicion_remision FROM transmisiones_remision WHERE id_movimiento = ?"
    ),

    ACTUALIZAR_TRANSMISION_ATRIBUTOS_VENTA(
            "UPDATE transmision SET request = CAST(? AS json) WHERE id = ?"
    ),

    BUSCAR_VENTA_CLIENTE("SELECT * FROM fnc_conseguir_ventas_cliente() AS data;"),
    MENSAJES_COMPROBANTE("SELECT valor FROM wacher_parametros WHERE codigo = 'MENSAJES_FE'"),
    EXISTE_GOPASS("select count(*) from medios_pagos mp "
                    + "where mp.mp_atributos::json->>'codigoExterno' is not null "
                    + "and mp.mp_atributos::json->>'codigoExterno'='238' "
                    + "limit 1"),

    ACTUALIZAR_MEDIOS_PAGO("UPDATE ct_movimientos_medios_pagos "
                    + "SET ct_medios_pagos_id=(select id from medios_pagos mp where mp.mp_atributos::json->>'codigoExterno' notnull and mp.mp_atributos::json->>'codigoExterno'='238' limit 1), numero_comprobante=?"
                    + " WHERE ct_movimientos_id=?"),

    GET_TRANSACIONES_GOPASS("select * from public.fnc_recuperar_ventas_gopass(?::integer)"),
    GET_ALL_JORNADAS_HISTORY("SELECT J.ID JID, P.ID PERSONAS_ID, P.NOMBRES, P.APELLIDOS, P.IDENTIFICACION, " +
                                     "J.GRUPO_TURNO, J.FECHA_INICIO, J.FECHA_FIN, ATRIBUTOS " +
                                     "FROM JORNADAS_HIST J " +
                                     "INNER JOIN PERSONAS P ON P.ID=J.PERSONAS_ID " +
                                     "ORDER BY J.ID"),

    GET_ALL_USUARIOS_CORE("SELECT P.ID PERSONAS_ID, P.NOMBRE, P.IDENTIFICACION, P.ESTADO, P.TELEFONO, P.DIRECCION, P.PERFILES_ID, P.TAG " +
            "FROM PERSONAS P " +
            "WHERE P.IDENTIFICACION <> '2222222' OR P.NOMBRE NOT IN('CLIENTES','VARIOS') AND perfiles_id IS NOT NULL"),
    GET_ALL_PROMOTORES("SELECT P.ID PERSONAS_ID, P.NOMBRES, P.APELLIDOS, P.IDENTIFICACION " +
            "FROM PERSONAS P " +
            "WHERE ESTADO='A' AND PERFILES_ID <> 4 AND ID>2"),

    OBTENER_LECTURA_TAG("SELECT * FROM lecturas_tag LIMIT 1"),

    OBTENER_USUARIO("SELECT P.ID, P.IDENTIFICACION, U.PIN, P.NOMBRES, P.APELLIDOS FROM PERSONAS P LEFT JOIN IDENTIFICADORES ID ON ID.ENTIDAD_ID=P.ID LEFT JOIN USUARIOS U ON U.PERSONAS_ID=P.ID WHERE IDENTIFICADOR=? AND ORIGEN=5 AND ID.ESTADO='A' LIMIT 1"),
    ELIMINAR_LECTURA("DELETE FROM lecturas_tag"),

    IS_ADMIN("SELECT pf.tipo AS id FROM personas p INNER JOIN perfiles pf ON p.perfiles_id = pf.id WHERE p.id = ?"),
    FIND_PERSONA_NO_RFID("SELECT P.ID, P.IDENTIFICACION, U.PIN, P.NOMBRES, P.APELLIDOS FROM PERSONAS P INNER JOIN USUARIOS U ON U.PERSONAS_ID=P.ID WHERE P.ESTADO='A' AND P.IDENTIFICACION = ? AND U.PIN = ? LIMIT 1"),
    FIND_PERSONA_RFID("SELECT P.ID, P.IDENTIFICACION, U.PIN, P.NOMBRES, P.APELLIDOS FROM PERSONAS P LEFT JOIN IDENTIFICADORES ID ON ID.ENTIDAD_ID=P.ID LEFT JOIN USUARIOS U ON U.PERSONAS_ID=P.ID WHERE IDENTIFICADOR=? AND ORIGEN=5 AND ID.ESTADO='A' LIMIT 1"),
    FIND_PERSONA_PERMISOS("SELECT * FROM PERMISOS_POST WHERE PERSONAS_ID=?"),

    CLEAR_PERSONA_TAG("UPDATE PERSONAS SET TAG=NULL WHERE TAG=?"),

    REGISTER_PERSONA_TAG("UPDATE PERSONAS SET TAG=? WHERE IDENTIFICACION=? RETURNING ID"),
    FIND_PERSONA_BY_ID("SELECT P.ID, P.IDENTIFICACION, P.NOMBRES, P.APELLIDOS, U.PIN AS PIN, P.EMPRESAS_ID, COALESCE((SELECT array_to_json(array_agg(row_to_json(t))) FROM (SELECT pp.*, m.atributos::json->>'url' link FROM permisos_post pp INNER JOIN modulos m ON m.id = pp.modulos_id WHERE pp.personas_id = P.id) t), '[]') modulos FROM PERSONAS P INNER JOIN USUARIOS U ON U.PERSONAS_ID=P.ID WHERE P.ESTADO='A' AND P.ID = ?"),
    GET_TURNO("SELECT p2.id, j.id AS idjornada, j.fecha_inicio, j.grupo_jornada FROM jornadas j INNER JOIN personas p2 ON p2.id = j.personas_id WHERE j.personas_id = ? ORDER BY j.id ASC"),
    GET_PRINCIPAL_TURNO("select p2.id, j.id as idjornada, p2.nombre as nombre, p2.identificacion as identificacion, " +
                    "ti.descripcion, j.fecha_inicio, j.grupo_jornada " +
                    "from jornadas j " +
                    "inner join personas p2 on p2.id = j.personas_id " +
                    "inner join tipos_identificaciones ti on ti.id = p2.tipos_identificacion_id " +
                    "order by j.id asc limit 1"),

    IS_INICIADA_AND_ULTIMO("SELECT *, (SELECT COUNT(1) FROM JORNADAS) OTROS FROM JORNADAS WHERE PERSONAS_ID=? AND fecha_fin IS NULL"),
    CALCULATE_VENTAS_JORNADA("SELECT COUNT(1) cantidad, SUM(venta_total) total FROM movimientos m WHERE operacion = 9 AND grupo_jornada_id = ? AND persona_id = ?"),
    UPDATE_JORNADA("UPDATE jornadas SET fecha_fin = ?, atributos = ?::json WHERE personas_id = ?"),
    ARCHIVE_JORNADA("INSERT INTO jornadas_hist SELECT nextval('JORNADAS_HIST_ID'), J.* FROM jornadas J WHERE personas_id = ?"),
    DELETE_JORNADA("DELETE FROM jornadas WHERE personas_id = ?"),

    SEARCH_ALL_PERSONS("SELECT COALESCE((SELECT array_to_json(array_agg(row_to_json(t))) FROM (SELECT id, estado, nombre, identificacion, p.perfiles_id AS perfil FROM personas p WHERE p.id > 3 %s) t), '[]') AS persons_array"),
    GET_ALL_JORNADAS("SELECT P.ID PERSONAS_ID, P.NOMBRES, P.APELLIDOS, P.IDENTIFICACION, J.GRUPO_TURNO, J.FECHA_INICIO FROM JORNADAS J INNER JOIN PERSONAS P ON P.ID=J.PERSONAS_ID ORDER BY J.ID"),
    FIND_ADMIN_REGISTRY("SELECT pf.tipo AS id FROM usuarios u INNER JOIN personas p ON u.personas_id = p.id INNER JOIN perfiles pf ON p.perfiles_id = pf.id WHERE u.estado IN ('A') AND u.usuario = ? AND pin = ? LIMIT 1"),
    MONTO_MINIMO_REMISION("SELECT valor FROM wacher_parametros WHERE codigo = 'MONTO_MINIMO_REMISION'"),
    OBLIGATORIEDAD_REMISION("SELECT valor FROM wacher_parametros WHERE codigo = 'OBLIGATORIEDAD_REMISION'"),
    OBLIGATORIEDAD_FE("SELECT valor FROM wacher_parametros WHERE codigo = 'OBLIGATORIO_FE'"),
    MONTO_MINIMO_FE("SELECT valor FROM wacher_parametros WHERE codigo = 'MONTO_MINIMO_FE'"),
    MOTIVOS_ANULACION_FE("SELECT valor FROM wacher_parametros WHERE codigo = 'MOTIVOS_ANULACION_FACTURA_ELECTRONICA'"),
    ACTUALIZAR_CONSECUTIVO("UPDATE consecutivos SET consecutivo_actual = ? WHERE id = ?"),
    OBTENER_CONSECUTIVO_DESDE_MOVIMIENTO("SELECT consecutivo FROM ct_movimientos WHERE id = ?"),
    
    CONSECUTIVO_USADO(
            "SELECT 1 " +
                    "FROM ct_movimientos cm " +
                    "WHERE consecutivo = ? " +
                    "AND prefijo = ? " +
                    "AND cm.fecha > (NOW() - interval '2 hours')"
    ),

    GET_OBSERVACIONES_CONSECUTIVO(
            "SELECT observaciones FROM consecutivos WHERE id = ? LIMIT 1"
    ),

    FIND_ADMIN_CORE("SELECT pf.tipo AS id FROM wacher_api_usuarios wau INNER JOIN personas p ON wau.persona_id = p.id INNER JOIN perfiles pf ON p.perfiles_id = pf.id WHERE wau.usuario = ? AND wau.clave = md5(?) AND p.perfiles_id != 8 LIMIT 1"),
    VALIDAR_TURNO_MEDIO_PAGO("SELECT cm.id\n" + "from ct_movimientos cm\n" + "inner join jornadas j\n" + "on j.grupo_jornada = cm.jornadas_id\n" + "where cm.id = ?"),
    BUSCAR_MEDIOS_PAGOS_DATAFONOS_COMPLETADOS("SELECT cmmp.id, cmmp.valor_total, cmmp.ing_pago_datafono, cmp.descripcion, cmp.id as id_medio_pago FROM ct_movimientos_medios_pagos cmmp INNER JOIN ct_medios_pagos cmp ON cmmp.ct_medios_pagos_id = cmp.id WHERE cmmp.ct_movimientos_id = ?"),
    CHECK_COMBUSTIBLES("SELECT * FROM ct_bodegas cb INNER JOIN ct_bodegas_productos cbp ON cb.id = cbp.bodegas_id AND cb.atributos::json ->>'estado'= 'A' LIMIT 1"),
    CHECK_CANASTILLA_KIOSCO("SELECT * FROM bodegas b INNER JOIN bodegas_productos bp ON b.id = bp.bodegas_id WHERE b.finalidad = ? AND b.estado = 'A' LIMIT 1"),
    OBTENER_PRODUCTOS_PROMOCION(
        "SELECT p.id, p.plu, p.descripcion, p.precio, p.tipo, p.favorito, " +
        "COALESCE(saldo, 0) saldo, bp.costo " +
        "FROM productos p " +
        "LEFT JOIN bodegas_productos bp ON bp.productos_id = p.id " +
        "WHERE p.promocion IS NOT NULL " +
        "AND p.estado = 'A' " +
        "AND p.puede_vender = 'S'"
    ),
    
    OCULTAR_INPUTS_FACTURA_VENTA("SELECT * FROM fnc_consultar_autorizacion_cara(?) AS resultado"),
    HABILITAR_BOTONES_FAMILIA_AUTORIZACION("SELECT * FROM public.fnc_consultar_familia_autorizacion(?) AS resultado"),
    VALIDAR_CORRECCION_SALTO_LECTURA("SELECT id FROM surtidores_detalles sd " +
            "WHERE sd.surtidor = ? " +
            "AND sd.manguera = ? " +
            "AND sd.cara = ? " +
            "AND salto_lectura = 'S'"),
    ACTUALIZAR_NOVEDAD_SALTO_LECTURA("UPDATE reporteria_cierres.novedad_salto_lectura " +
            "SET estado='RESUELTO', fecha_actualizacion = now(), sincronizado = 0 " +
            "WHERE surtidor = ? AND manguera = ? AND cara = ? AND estado = 'PENDIENTE'"),
    GET_CODIGO_EXTERNO_PRODUCTO("SELECT (p2.p_atributos->>'codigoExterno') codigo " +
            "FROM surtidores_detalles sd " +
            "INNER JOIN productos p2 ON p2.id = sd.productos_id " +
            "WHERE sd.cara = ? AND sd.grado = ?"),
    EQUIPO_ID("select id from equipos"),
    OBTENER_INFO_SURTIDORES_ESTACION("select host, isla, equipos_id, COALESCE(atributos::json->>'surtidores','[]') surtidores from surtidores_core"),
    GET_TIMEOUT_TOTALIZADORES("select valor from wacher_parametros where codigo = 'TIMEOUT_ESPERA_LECTURAS'"),
    OBTENER_HOST_SURTIDORES_ESTACION("select host, isla, equipos_id from surtidores_core"),
    OBTENER_CAPACIDAD_MAXIMA("select * from ct_bodegas where id = ?"),
    GET_JORNADA_ACTIVA("SELECT * FROM public.fnc_consultar_jornada_activa()"),
 
    // 馃殌 Consultas para obtenerIdTransmisionDesdeMovimiento()
    BUSCAR_ID_TRANSMISION_EN_CLIENTE("SELECT id_transmision FROM ct_movimientos_cliente WHERE id_movimiento = ?"),
    BUSCAR_ID_TRANSMISION_EN_REMISION("SELECT id_transmicion_remision FROM transmisiones_remision WHERE id_movimiento = ?"),
    BUSCAR_ID_TRANSMISION_EN_JSON("SELECT id FROM transmision WHERE request::json->>'identificadorMovimiento' = ?"),
 
    // 馃殌 Consulta para hayAnulacionesEncursoEnGeneral()
    HAY_ANULACIONES_EN_CURSO_GENERAL("SELECT * FROM datafonos.transacciones t WHERE t.id_movimiento = ? AND t.id_transaccion_operacion = ? AND t.id_transaccion_estado = ?"),
 
    // 馃殌 Consulta para obtener precio de UREA
    OBTENER_PRECIO_UREA("SELECT p.precio FROM productos p WHERE p.descripcion = ?"),
 
    // 馃殌 Consulta para obtener c贸digo externo de UREA desde JSON
    OBTENER_CODIGO_EXTERNO_UREA("SELECT COALESCE((SELECT (p.p_atributos::json->>'codigoExterno')::numeric FROM productos p WHERE p.descripcion = ?), 0) AS codigo"),
 
    // 馃殌 Consulta para obtener jornada ID (grupo_jornada)
    OBTENER_JORNADA_ID("SELECT j2.grupo_jornada FROM jornadas j2"),

    // 🚀 Consulta para obtener información de entrada de remisión por delivery
    INFO_ENTRADA_REMISION(
        "SELECT " +
        "trs.id_remision_sap, " +
        "trs.delivery, " +
        "trs.document_date, " +
        "trs.way_bill, " +
        "trs.logistic_center, " +
        "trs.supplying_center, " +
        "trs.status, " +
        "trs.modification_date, " +
        "trs.modification_hour, " +
        "trs.frontier_law, " +
        "trs.creation_date, " +
        "trs.creation_hour, " +
        "trs.id_estado " +
        "FROM sap.tbl_remisiones_sap trs WHERE trs.delivery = ?"
    ),

    // 馃殌 Consulta para obtener historial de remisiones con l铆mite de registros
    INFO_HISTORIAL_REMISIONES(
        "SELECT " +
        "trs.delivery, " +
        "p.descripcion AS product, " +
        "trps.quantity, " +
        "u.alias AS unit, " +
        "to_char(trs.creation_date, 'DD-MM-YYYY') AS creation_date, " +
        "COALESCE(to_char(trs.creation_hour, 'HH24:MI:SS'), '') AS creation_hour, " +
        "COALESCE(to_char(trs.modification_date, 'DD-MM-YYYY'), '') AS modification_date, " +
        "COALESCE(to_char(trs.modification_hour, 'HH24:MI:SS'), '') AS modification_hour, " +
        "tes.descripcion AS status " +
        "FROM sap.tbl_remisiones_sap trs " +
        "INNER JOIN sap.tbl_remisiones_productos_sap trps ON trs.id_remision_sap = trps.id_remision_sap " +
        "INNER JOIN productos p ON trps.id_producto = p.id " +
        "INNER JOIN sap.tbl_estados_sap tes ON trps.id_estado = tes.id_estado " +
        "INNER JOIN public.unidades u ON p.unidad_medida_id = u.id " +
        "ORDER BY trs.creation_date DESC " +
        "FETCH FIRST ? ROWS ONLY"
    ),

    // 馃殌 Consulta para obtener tanques de remisi贸n
    OBTENER_TANQUES_REMISION("SELECT * FROM public.fnc_consultar_tanques_remision(?)"),

    // 馃殌 Consulta para obtener ID de remisi贸n por n煤mero de delivery
    OBTENER_ID_REMISION("SELECT id_remision_sap FROM sap.tbl_remisiones_sap trs WHERE trs.delivery = ?"),

    // 馃殌 Consulta para validar si una remisi贸n est谩 confirmada (estado = 2)
    VALIDAR_SI_REMISION_ESTA_CONFIRMADA("SELECT * FROM sap.tbl_remisiones_sap trs WHERE trs.delivery = ? AND trs.id_estado = 2"),

    // 馃殌 Consulta para confirmar una remisi贸n actualizando su estado
    CONFIRMAR_REMISION("UPDATE sap.tbl_remisiones_sap SET id_estado = ? WHERE id_remision_sap = ?"),

    // 馃殌 Consulta para obtener productos SAP de una remisi贸n
    OBTENER_PRODUCTOS_SAP_REMISION(
        "SELECT trps.*, p.descripcion " +
        "FROM sap.tbl_remisiones_productos_sap trps " +
        "INNER JOIN public.productos p ON p.id = trps.id_producto " +
        "WHERE trps.id_remision_sap = ? AND trps.id_estado = 1"
    ),

    // 馃殌 Consulta para actualizar movimientos CT usando funci贸n PostgreSQL
    ACTUALIZAR_CT_MOVIMIENTOS("SELECT * FROM fnc_actualizar_ct_movimientos(?::json)"),
    
    // 馃殌 Consulta para obtener nombre de promotor por ID
    OBTENER_NOMBRE_PROMOTOR("SELECT nombre FROM personas p WHERE id = ?"),
    
    // 馃殌 Consulta para actualizar estado de movimiento a anulado
    ACTUALIZAR_ESTADO_MOVIMIENTO("UPDATE ct_movimientos SET estado_movimiento = '017002' WHERE id = ?"),
    
    // 馃殌 Consulta para obtener c贸digo de estaci贸n desde wacher_parametros
    OBTENER_CODIGO_ESTACION("SELECT valor FROM wacher_parametros WHERE codigo = 'codigoBackoffice'"),
    
    // 馃殌 Consulta para insertar autorizaci贸n usando funci贸n PostgreSQL
    INSERTAR_AUTORIZACION("SELECT * FROM public.fnc_insertar_autorizacion(?::json)"),
    
    // 馃殌 Consulta para insertar confirmaci贸n de venta App (actualmente no se usa en el c贸digo, preparado para futuro uso)
    INSERTAR_CONFIRMACION_VENTA_APP("SELECT * FROM public.fnc_insert_tbl_autorizaciones_pos(?::json)"),
    SET_ESTADO_MOVIMIENTO("update ct_movimientos set estado = 'M' where id = ?"),
    BORRAR_RECEPCION("delete from recepcion_combustible where id = ?"),
    INSERTAR_RECEPCION_COMBUSTIBLE("INSERT INTO public.recepcion_combustible (promotor_id,documento,placa,tanques_id,productos_id,cantidad,fecha,altura_inicial,volumen_inicial,agua_inicial) VALUES (?,?,?,?,?,?,?,?,?,?) RETURNING id"),
    ACTUALIZAR_RECEPCION_COMBUSTIBLE("update recepcion_combustible set promotor_id=?,documento=?,placa=?,tanques_id=?,productos_id=?,cantidad=?,fecha=?,altura_inicial=?,volumen_inicial=?,agua_inicial=? where id=? RETURNING id"),
    GET_RECEPCIONES("SELECT rc.id, rc.promotor_id, rc.documento, rc.placa, rc.tanques_id, rc.productos_id, rc.cantidad, rc.fecha, rc.altura_inicial, rc.volumen_inicial, rc.agua_inicial, p.descripcion FROM recepcion_combustible rc LEFT JOIN productos p ON rc.productos_id = p.id ORDER BY rc.id DESC"),
    FUE_FIDELIZADA("SELECT atributos FROM ct_movimientos cm WHERE id = ?"),
    OBTENER_ATRIBUTOS_MOVIMIENTO("SELECT atributos FROM ct_movimientos cm WHERE id = ?"),
    ACTUALIZAR_FIDELIZACION("UPDATE ct_movimientos SET atributos = ?::json, estado = 'M' WHERE id = ?"),
    CREAR_POS_VENTA_FE("INSERT INTO public.tareas_pos_ventas (cara, manguera, tarea, atributos) VALUES (?, ?, ?, ?::json)"),
    OBTENER_ATRIBUTOS_VENTA_CURSO("SELECT atributos FROM ventas_curso WHERE cara = ? AND manguera = ?"),
    ACTUALIZAR_VENTA_PARA_IMPRIMIR("UPDATE ventas_curso SET atributos = ?::json WHERE cara = ? AND manguera = ?"),
    OBTENER_ID_MEDIO_PAGO_VENTA_CURSO("SELECT ((vc.atributos::json->>'DatosFactura')::json->>'medio_pago')::numeric respuesta FROM ventas_curso vc WHERE cara = ?"),
    OBTENER_MOVIMIENTO_ID_DESDE_CARA("SELECT cm.id FROM ct_movimientos cm WHERE cm.atributos::json->>'cara' = ?::text ORDER BY cm.fecha DESC LIMIT 1"),
    OBTENER_PRODUCTO_ID_DESDE_VENTA_CURSO("select productos_id from ventas_curso vc where vc.cara = ?"),
    OBTENER_PRODUCTO_POR_NOMBRE_VENTA_EN_CURSO("select descripcion from productos p where p.id = ?"),

    // 馃殌 Consulta para verificar integraci贸n UREA
    VERIFICAR_INTEGRACION_UREA("SELECT COALESCE((SELECT valor FROM wacher_parametros WHERE codigo = ?), 'N') AS resultado"),
    
    // 馃殌 Consulta para obtener ID del producto UREA por descripci贸n
    OBTENER_ID_PRODUCTO_UREA("SELECT p.id FROM productos p WHERE p.descripcion = ?"),
    
    // 馃殌 Consulta para obtener ID de bodega UREA con tipo 'V' (Venta)
    OBTENER_ID_BODEGA_UREA("SELECT cb.id FROM ct_bodegas cb WHERE cb.atributos::json->>'tipo' = 'V'"),
    // 馃殌 Consulta para insertar movimientos CT usando funci贸n PostgreSQL
    INSERTAR_CT_MOVIMIENTOS("SELECT * FROM fnc_insertar_ct_movimientos(?::json,?::json,?::json,?::json,?::json,?::json,?::json)"),
    // 馃殌 Consulta para buscar productos en el market
    BUSCAR_PRODUCTOS_MARKET("select * from fnc_buscar_productos_market (?, ?)"),
    // 馃殌 Consulta para obtener ventas pendientes de impresi贸n
    OBTENER_VENTAS_PENDIENTES_IMPRESION("SELECT * FROM public.fnc_obtener_ventas_pendientes_impresion(?::interval)"),

    // 馃殌 Consulta para obtener tiempo de impresi贸n FE por c贸digo de par谩metro
    OBTENER_TIEMPO_IMPRESION_FE("SELECT wp.valor FROM wacher_parametros wp WHERE wp.codigo = ?"),

    // 馃殌 Consulta para actualizar estado de impresi贸n de movimiento
    ACTUALIZAR_ESTADO_IMPRESION("UPDATE public.ct_movimientos SET pendiente_impresion = false WHERE id = ?"),

    // 馃殌 Consulta para obtener datos de venta pendiente de datafono
    OBTENER_DATOS_VENTA_PENDIENTE_DATAFONO(
        "SELECT td.id_transaccion_estado, td.descripcion, d.id_adquiriente, " +
        "a.descripcion as proveedor " +
        "FROM datafonos.transacciones t " +
        "INNER JOIN datafonos.transacciones_estados td ON td.id = t.id_transaccion_estado " +
        "INNER JOIN datafonos.datafonos d ON d.id = t.id_datafono " +
        "INNER JOIN datafonos.adquirientes a ON a.id = d.id_adquiriente " +
        "WHERE t.id_transaccion = :idTransaccion"),

    // 馃殌 Consulta para buscar transacci贸n de datafono por ID de movimiento
    BUSCAR_TRANSACCION_DATAFONO(
        "SELECT * FROM datafonos.transacciones ts " +
        "WHERE ts.id_transaccion_estado = 1 " +
        "AND ts.id_movimiento = ?"),

    // 馃殌 Consulta para obtener total de ventas por turno
    OBTENER_TOTAL_VENTAS_TURNO(
        "SELECT SUM(cmmp.valor_total) " +
        "FROM ct_movimientos cm " +
        "INNER JOIN ct_movimientos_medios_pagos cmmp ON cm.id = cmmp.ct_movimientos_id " +
        "WHERE cmmp.ct_medios_pagos_id = 1 " +
        "AND jornadas_id = ? " +
        "AND responsables_id = ? " +
        "AND cm.tipo NOT IN ('016','014')"),
 
 
    BUSCAR_BODEGA_PRODUCTO_POR_ID_PRODUCTO("SELECT BODEGAS_ID FROM BODEGAS_PRODUCTOS WHERE PRODUCTOS_ID=? LIMIT 1"),
    BUSQUEDA_PRODUCTO_TIPO_KIOSCO("select * from fnc_buscar_producto_market (?, ?, ?)"),
    OBTENER_BODEGA_PRODUCTO("SELECT BODEGAS_ID FROM BODEGAS_PRODUCTOS WHERE PRODUCTOS_ID = ?"),
    BUSCAR_PRODUCTO_POR_CODIGO_BARRA_KIOSCO(" SELECT * FROM fnc_buscar_productos_market_bar(?, ?, ?)"),
    BUSCAR_PRODUCTO_POR_PLU_KIOSCO("select * from fnc_buscar_productos_market_plu(?, ?, ?)"),

    // 🚀 Consulta para obtener alertas de resolución de consecutivos
    OBTENER_ALERTAS_RESOLUCION(
        "SELECT " +
        "(consecutivo_final - consecutivo_actual) as rangoConsecutivo, " +
        "(fecha_fin::date - now()::date) as rangoFecha, " +
        "(cs_atributos::json->>'alerta_dias':: text) as alertaDias, " +
        "(cs_atributos::json->>'alerta_consecutivos':: text) as alertaConsecutivos " +
        "FROM consecutivos " +
        "WHERE tipo_documento = ? " +
        "AND cs_atributos::json->>'destino' = ? " +
        "AND estado IN('A', 'U') " +
        "AND equipos_id = ? " +
        "ORDER BY fecha_fin ASC " +
        "LIMIT 1"
    ),

    // 🏗️ DDL: Crear tabla de ventas en curso si no existe
    CREAR_TABLA_VENTAS_CURSO(
        "CREATE TABLE IF NOT EXISTS public.lt_ventas_curso (" +
        "id serial NOT NULL, " +
        "negocio varchar NOT NULL, " +
        "estado varchar NOT NULL DEFAULT 'A')"
    ),

    // ➕ DML: Activar venta en curso (INSERT)
    ACTIVAR_VENTA_EN_CURSO(
        "INSERT INTO lt_ventas_curso(negocio) VALUES (?)"
    ),

    // ➖ DML: Desactivar venta en curso (DELETE)
    DESACTIVAR_VENTA_EN_CURSO(
        "DELETE FROM lt_ventas_curso WHERE negocio = ?"
    ),

    // 📊 SELECT: Obtener consecutivo con cálculo de días (Core - Registry)
    OBTENER_CONSECUTIVO(
        "SELECT *, " +
        "EXTRACT(EPOCH FROM (c.fecha_fin - ?)) / 3600 / 24::int AS dias " +
        "FROM consecutivos c " +
        "WHERE estado IN ('A', 'U') " +
        "AND tipo_documento = ? " +
        "AND c.cs_atributos::json->>'destino' = ? " +
        "ORDER BY fecha_fin ASC LIMIT 1"
    ),

    // 📊 SELECT: Obtener consecutivo con fecha literal (igual que DAO original)
    OBTENER_CONSECUTIVO_CON_FECHA_LITERAL(
        "SELECT *, " +
        "EXTRACT(EPOCH FROM (c.fecha_fin - '%s')) / 3600 / 24::int AS dias " +
        "FROM consecutivos c " +
        "WHERE estado IN ('A', 'U') " +
        "AND tipo_documento = ? " +
        "AND c.cs_atributos::json->>'destino' = ? " +
        "ORDER BY fecha_fin ASC LIMIT 1"
    ),

    // 🔄 UPDATE: Actualizar estado de consecutivo (Vencer/Activar)
    ACTUALIZAR_ESTADO_CONSECUTIVO(
        "UPDATE consecutivos SET estado = ? WHERE id = ?"
    ),

    BUSCAR_USUARIO_POR_NICKNAME("SELECT u.* FROM usuarios u WHERE u.nickname=? AND u.estado=?"),
    // 🚀 Consulta para obtener prefijo de consecutivo (Facturación Manual)
    // Reemplaza: MovimientosDao.getPrefijo(Object tipoFacturacion, String resolucion)
    // Parámetros: 1=resolucion, 2=tipoDocumento, 3=equipos_id
    // Nota: Los cálculos de tiempo (días, horas, minutos) se realizan dinámicamente
    OBTENER_PREFIJO_CONSECUTIVO(
        "SELECT c.id, c.consecutivo_inicial, c.consecutivo_actual, c.consecutivo_final, " +
        "c.estado, c.resolucion, c.fecha_inicio, c.fecha_fin, c.observaciones, c.prefijo, " +
        "EXTRACT(EPOCH FROM (c.fecha_fin - CURRENT_DATE)) / 3600 / 24::int AS dias, " +
        "EXTRACT(HOUR FROM c.fecha_fin) - EXTRACT(HOUR FROM CURRENT_TIMESTAMP) AS hora, " +
        "EXTRACT(MINUTE FROM c.fecha_fin) - EXTRACT(MINUTE FROM CURRENT_TIMESTAMP) AS minuto, " +
        "EXTRACT(SECOND FROM c.fecha_fin) - EXTRACT(SECOND FROM CURRENT_TIMESTAMP) AS segundos " +
        "FROM consecutivos c " +
        "WHERE c.cs_atributos::json->>'destino' = ? " +
        "AND tipo_documento = ? " +
        "AND estado IN('U', 'A') " +
        "AND equipos_id = ? " +
        "ORDER BY c.fecha_fin ASC"
    ),

    IS_MASSER("SELECT * FROM empresas e INNER JOIN tbl_tipos_empresas tte ON e.id_tipo_empresa = tte.id_tipo_empresa WHERE tte.descripcion ILIKE '%masser%' LIMIT 1"),

    GET_TANQUES_POR_TIPO_ESTADO("select id, bodega, codigo, "
            + "atributos->>'tanque' numero, "
            + "atributos->>'volumen_maximo' volumen_maximo, "
            + "atributos->>'familia' familia "
            + "from ct_bodegas "
            + "where atributos->>'tipo' = 'T' "
            + "and atributos->>'estado'='A' order by 4"),

    // 🔄 Consulta paginada para productos de canastilla
    OBTENER_PRODUCTOS_CANASTILLA(
        "SELECT * FROM ( " +
        "SELECT p.id, p.plu, p.estado, p.unidades_medida,   " +
        "p.descripcion, p.precio, p.tipo,   " +
        "p.cantidad_ingredientes, p.cantidad_impuestos,   " + " COALESCE(g.id, -1) as categoria_id,   " +
        "COALESCE(g.grupo, 'OTROS') as categoria_descripcion,   " +
        "COALESCE((SELECT identificador FROM identificadores i2 WHERE entidad_id = p.id AND origen = ? LIMIT 1), '') as codigo_barra,   " +
        "COALESCE((SELECT array_to_json(array_agg(row_to_json(t))) FROM ( " +
        "   SELECT i.id as impuesto_id, i.descripcion, productos_id, iva_incluido, porcentaje_valor, valor   " +
        "   FROM public.productos_impuestos pi   " +
        "   INNER JOIN impuestos i ON i.id = pi.impuestos_id   " +
        "   WHERE pi.productos_id = p.id " +
        ") t), '[]') as impuestos, " +
        "COALESCE((SELECT array_to_json(array_agg(row_to_json(t))) FROM ( " +
        "   SELECT PC.*, COSTO, SALDO, PI.DESCRIPCION, PI.TIPO   " +
        "   FROM PRODUCTOS_COMPUESTOS PC   " +
        "   INNER JOIN BODEGAS_PRODUCTOS BP ON PC.INGREDIENTES_ID = BP.PRODUCTOS_ID   " +
        "   INNER JOIN BODEGAS B ON BP.bodegas_id = B.id   " +
        "   INNER JOIN PRODUCTOS PI ON PI.ID = PC.INGREDIENTES_ID   " +
        "   WHERE PC.productos_id = p.id AND B.estado != 'I' " +
        ") t), '[]') as ingredientes, " +
        "COALESCE(bp.saldo, 0) as saldo,   " +
        "COALESCE(bp.costo, 0) as costo, " +
        "Count(p.id) OVER() AS total_registros " +
        "FROM productos p   " +
        "INNER JOIN bodegas_productos bp ON bp.productos_id = p.id   " +
        "LEFT JOIN grupos_entidad ge ON p.id = ge.entidad_id   " +
        "LEFT JOIN grupos g ON ge.grupo_id = g.id   " +
        "WHERE p.estado IN ('A', 'B')   " +
        "AND p.puede_vender = 'S'   " +
        "AND p.id = bp.productos_id  " +
        "AND COALESCE(p_atributos::json->>'tipoStore', 'C') " +
        "NOT IN ('K', 'T') " +
        "ORDER BY p.descripcion " +
        ") productos " +
        "WHERE tipo <> -1   " +
        "LIMIT ? " +
        "OFFSET ?;"
    );
    private final String sql;
 
    SqlQueryEnum(String sql) {
        this.sql = sql;
    }
 
    public String getQuery() {
        return sql;
    }
}