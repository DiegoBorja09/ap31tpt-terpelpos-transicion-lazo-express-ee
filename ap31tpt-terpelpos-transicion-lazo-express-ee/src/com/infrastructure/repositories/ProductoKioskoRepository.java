package com.infrastructure.repositories;

import com.bean.MovimientosDetallesBean;
import com.bean.ProductoBean;
import com.bean.ImpuestosBean;
import com.controllers.NovusConstante;
import com.firefuel.Main;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.infrastructure.Enums.SqlQueryEnum;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class ProductoKioskoRepository {

    private final EntityManager em;

    public ProductoKioskoRepository(EntityManager em) {
        this.em = em;
    }

    // 🔧 HELPER: Conversión segura de Object a Long
    private Long safeLong(Object value) {
        if (value == null) return 0L;
        if (value instanceof Number) return ((Number) value).longValue();
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return 0L;
            }
        }
        return 0L;
    }

    // 🔧 HELPER: Conversión segura de Object a Integer
    private Integer safeInt(Object value) {
        if (value == null) return 0;
        if (value instanceof Number) return ((Number) value).intValue();
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    // 🔧 HELPER: Conversión segura de Object a Float
    private Float safeFloat(Object value) {
        if (value == null) return 0.0f;
        if (value instanceof Number) return ((Number) value).floatValue();
        if (value instanceof String) {
            try {
                return Float.parseFloat((String) value);
            } catch (NumberFormatException e) {
                return 0.0f;
            }
        }
        return 0.0f;
    }

    // 🔧 HELPER: Conversión segura de Object a String
    private String safeString(Object value) {
        return value != null ? value.toString() : "";
    }

    // 🔧 HELPER: Parsing seguro de JSON a JsonArray
    private JsonArray safeJsonArray(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty() || jsonString.equals("null")) {
            return new JsonArray(); // Array vacío
        }
        
        try {
            JsonElement element = new Gson().fromJson(jsonString, JsonElement.class);
            
            if (element == null || element.isJsonNull()) {
                return new JsonArray();
            }
            
            if (element.isJsonArray()) {
                return element.getAsJsonArray();
            }
            
            if (element.isJsonObject()) {
                // Si es un objeto, convertirlo a array con un elemento
                JsonArray array = new JsonArray();
                array.add(element.getAsJsonObject());
                return array;
            }
            
            if (element.isJsonPrimitive()) {
                // Si es un primitivo, retornar array vacío
                return new JsonArray();
            }
            
        } catch (Exception e) {
            System.out.println("⚠️ Error parseando JSON: " + jsonString + " - Error: " + e.getMessage());
        }
        
        return new JsonArray(); // Fallback a array vacío
    }

    public ArrayList<MovimientosDetallesBean> buscarProductos(String busqueda) {
        ArrayList<MovimientosDetallesBean> lista = new ArrayList<>();
        boolean isCDL = Main.TIPO_NEGOCIO.equals(NovusConstante.PARAMETER_CDL);

        Query query = em.createNativeQuery(SqlQueryEnum.BUSQUEDA_PRODUCTO_TIPO_KIOSCO.getQuery());
        query.setParameter(1, "%"+busqueda+"%");
        query.setParameter(2, isCDL);
        query.setParameter(3, NovusConstante.IDENTIFICADOR_CODIGO_BARRA);

        List<Object[]> result = query.getResultList();

        for (Object[] row : result) {
            MovimientosDetallesBean movBean = new MovimientosDetallesBean();
            


            // 🔧 MAPEO CORREGIDO BASADO EN EL ARRAY REAL
            movBean.setId(safeLong(row[0]));                    // 17197 (ID)
            movBean.setPlu(safeString(row[1]));                 // 4484 (PLU) ✅ CORRECTO
            movBean.setEstado(safeString(row[2]));              // A (estado)
            movBean.setUnidades_medida_id(safeInt(row[3]));     // 13 (unidades_medida)
            movBean.setProductoId(safeLong(row[0]));            // productos_id (mismo que ID)
            movBean.setDescripcion(safeString(row[4]));         // VUSE GO DISP MENTHOL ICE 34 MG (descripcion)
            movBean.setPrecio(safeFloat(row[5]));               // 31500.0 (precio)
            movBean.setTipo(safeInt(row[6]));                   // 23 (tipo)
            movBean.setCategoriaId(safeLong(row[7]));           // 109 (grupo_id)
            movBean.setCantidadIngredientes(safeInt(row[8]));   // 0 (cantidad_ingredientes)
            movBean.setCantidadImpuestos(safeInt(row[9]));      // 2 (cantidad_impuestos)
            movBean.setCategoriaId(safeLong(row[10]));          // 109 (categoria_id) - duplicado pero OK
            movBean.setCategoriaDesc(safeString(row[11]));      // TABACOS (categoria_descripcion)
            movBean.setCodigoBarra(safeString(row[12]));        // vacío (código_barra?)
            movBean.setSaldo(safeInt(row[19]));                 // 1.00 (saldo)
            movBean.setCosto(safeFloat(row[20]));               // 25147.0 (costo)
            movBean.setProducto_compuesto_costo(safeFloat(row[20]));

            Gson gson = new Gson();
            // 🔧 PARSING DE JSON BASADO EN EL ARRAY REAL
            ArrayList<ImpuestosBean> impuestos = organizarImpuestosProductos(safeJsonArray(safeString(row[13]))); // JSON de impuestos
            ArrayList<ProductoBean> ingredientes = organizarIngredientesProductos(safeJsonArray(safeString(row[17]))); // JSON de ingredientes
            movBean.setIngredientes(ingredientes);
            movBean.setImpuestos(impuestos);

            int tipo = safeInt(row[7]);
            movBean.setCompuesto(tipo == NovusConstante.TIPO_PRODUCTO_COMPUESTO || tipo == NovusConstante.TIPO_PRODUCTO_PROMOCION);

            movBean.setBodegasId(safeLong(row[18]));      // bodega_id
            movBean.setSaldo(safeInt(row[19]));           // saldo
            movBean.setCosto(safeFloat(row[20]));         // costo
            if (!ingredientes.isEmpty()) {
                for (ProductoBean ingrediente : ingredientes) {
                    movBean.setProducto_compuesto_costo(
                            movBean.getProducto_compuesto_costo() +
                                    (ingrediente.getProducto_compuesto_costo() * ingrediente.getProducto_compuesto_cantidad())
                    );
                }
            }
            lista.add(movBean);
        }

        return lista;
    }

    private ArrayList<ImpuestosBean> organizarImpuestosProductos(JsonArray arrayImpuestos) {
        ArrayList<ImpuestosBean> impuestos = new ArrayList<>();
        
        // 🔧 VALIDACIÓN: Array puede ser null o vacío
        if (arrayImpuestos == null || arrayImpuestos.size() == 0) {
            return impuestos;
        }
        
        for (JsonElement element : arrayImpuestos) {
            // 🔧 VALIDACIÓN: Elemento puede ser null
            if (element == null || element.isJsonNull() || !element.isJsonObject()) {
                continue;
            }
            
            JsonObject json = element.getAsJsonObject();
            ImpuestosBean impuesto = new ImpuestosBean();
            
            // 🔧 VALIDACIÓN NULL-SAFE: Cada propiedad
            try {
                if (json.has("impuesto_id") && !json.get("impuesto_id").isJsonNull()) {
                    impuesto.setId(json.get("impuesto_id").getAsLong());
                } else {
                    impuesto.setId(0L);
                }
                
                if (json.has("descripcion") && !json.get("descripcion").isJsonNull()) {
                    impuesto.setDescripcion(json.get("descripcion").getAsString());
                } else {
                    impuesto.setDescripcion("");
                }
                
                if (json.has("iva_incluido") && !json.get("iva_incluido").isJsonNull()) {
                    impuesto.setIva_incluido("S".equals(json.get("iva_incluido").getAsString()));
                } else {
                    impuesto.setIva_incluido(false);
                }
                
                if (json.has("porcentaje_valor") && !json.get("porcentaje_valor").isJsonNull()) {
                    impuesto.setPorcentaje_valor(json.get("porcentaje_valor").getAsString());
                } else {
                    impuesto.setPorcentaje_valor("%");
                }
                
                if (json.has("valor") && !json.get("valor").isJsonNull()) {
                    impuesto.setValor(json.get("valor").getAsFloat());
                } else {
                    impuesto.setValor(0.0f);
                }
                
                impuestos.add(impuesto);
                
            } catch (Exception e) {
                System.out.println("⚠️ Error procesando impuesto: " + json.toString() + " - Error: " + e.getMessage());
                // No agregar este impuesto y continuar con el siguiente
            }
        }
        return impuestos;
    }

    private ArrayList<ProductoBean> organizarIngredientesProductos(JsonArray arrayIngredientes) {
        ArrayList<ProductoBean> ingredientes = new ArrayList<>();
        
        // 🔧 VALIDACIÓN: Array puede ser null o vacío
        if (arrayIngredientes == null || arrayIngredientes.size() == 0) {
            return ingredientes;
        }
        
        for (JsonElement element : arrayIngredientes) {
            // 🔧 VALIDACIÓN: Elemento puede ser null
            if (element == null || element.isJsonNull() || !element.isJsonObject()) {
                continue;
            }
            
            JsonObject json = element.getAsJsonObject();
            ProductoBean producto = new ProductoBean();
            
            // 🔧 VALIDACIÓN NULL-SAFE: Cada propiedad
            try {
                if (json.has("ingredientes_id") && !json.get("ingredientes_id").isJsonNull()) {
                    producto.setId(json.get("ingredientes_id").getAsLong());
                } else {
                    producto.setId(0L);
                }
                
                if (json.has("descripcion") && !json.get("descripcion").isJsonNull()) {
                    producto.setDescripcion(json.get("descripcion").getAsString());
                } else {
                    producto.setDescripcion("");
                }

                if (json.has("saldo") && !json.get("saldo").isJsonNull()) {
                    producto.setSaldo(json.get("saldo").getAsInt());
                } else {
                    producto.setSaldo(0);
                }
                producto.setCantidadUnidad(producto.getSaldo());

                if (json.has("cantidad") && !json.get("cantidad").isJsonNull()) {
                    producto.setProducto_compuesto_cantidad(json.get("cantidad").getAsFloat());
                } else {
                    producto.setProducto_compuesto_cantidad(0f);
                }

                if (json.has("costo") && !json.get("costo").isJsonNull()) {
                    producto.setProducto_compuesto_costo(json.get("costo").getAsFloat());
                } else {
                    producto.setProducto_compuesto_costo(0f);
                }

                ingredientes.add(producto);
                
            } catch (Exception e) {
                System.out.println("⚠️ Error procesando ingrediente: " + json.toString() + " - Error: " + e.getMessage());
                // No agregar este ingrediente y continuar con el siguiente
            }
        }
        return ingredientes;
    }


    public MovimientosDetallesBean findByCodigoBarraKiosco(String code) {
        boolean isCDL = Main.TIPO_NEGOCIO.equals(NovusConstante.PARAMETER_CDL);

        Query query = em.createNativeQuery(SqlQueryEnum.BUSCAR_PRODUCTO_POR_CODIGO_BARRA_KIOSCO.getQuery());
        query.setParameter("code", code);
        query.setParameter("isCdl", isCDL);
        query.setParameter("identificador", NovusConstante.IDENTIFICADOR_CODIGO_BARRA);

        List<Object[]> results = query.getResultList();
        if (results.isEmpty()) return null;

        Object[] row = results.get(0);
        MovimientosDetallesBean bean = new MovimientosDetallesBean();

        // 🔧 USANDO CASTING SEGURO
        bean.setId(safeLong(row[0]));
        bean.setProductoId(safeLong(row[0]));
        bean.setPlu(safeString(row[1]));
        bean.setEstado(safeString(row[2]));
        bean.setUnidades_medida_id(safeInt(row[3]));
        bean.setDescripcion(safeString(row[5]));
        bean.setPrecio(safeFloat(row[6]));
        bean.setTipo(safeInt(row[7]));
        bean.setSaldo(safeInt(row[21]));
        bean.setCantidadIngredientes(safeInt(row[9]));
        bean.setCantidadImpuestos(safeInt(row[10]));
        bean.setCategoriaId(safeLong(row[11]));
        bean.setCategoriaDesc(safeString(row[12]));
        bean.setCodigoBarra(safeString(row[13]));
        bean.setCosto(safeFloat(row[22]));
        bean.setProducto_compuesto_costo(safeFloat(row[22]));
        bean.setCompuesto(bean.getTipo() == NovusConstante.TIPO_PRODUCTO_COMPUESTO ||
                bean.getTipo() == NovusConstante.TIPO_PRODUCTO_PROMOCION);

        Gson gson = new Gson();
        // 🔧 USANDO PARSING SEGURO DE JSON
        bean.setImpuestos((ArrayList<ImpuestosBean>) organizarImpuestosProductos(
                safeJsonArray(safeString(row[14]))));

        ArrayList<ProductoBean> ingredientes = organizarIngredientesProductos(
                safeJsonArray(safeString(row[17])));
        bean.setIngredientes(ingredientes);

        // USAR LOS DATOS QUE YA VIENEN DE fnc_buscar_productos_market_bar
        bean.setBodegasId(safeLong(row[18]));    // bodega_id de la SP
        bean.setSaldo(safeInt(row[19]));         // saldo de la SP
        bean.setCosto(safeFloat(row[20]));       // costo de la SP
        bean.setProducto_compuesto_costo(bean.getCosto());

        if (!ingredientes.isEmpty()) {
            for (ProductoBean ingrediente : ingredientes) {
                bean.setProducto_compuesto_costo(
                        bean.getProducto_compuesto_costo() +
                                ingrediente.getProducto_compuesto_costo() * ingrediente.getProducto_compuesto_cantidad());
            }
        }

        return bean;
    }

    public MovimientosDetallesBean findByPluKiosco(String plu) {
        boolean isCDL = Main.TIPO_NEGOCIO.equals(NovusConstante.PARAMETER_CDL);

        Query query = em.createNativeQuery(SqlQueryEnum.BUSCAR_PRODUCTO_POR_PLU_KIOSCO.getQuery());
        query.setParameter(1, plu);
        query.setParameter(2, isCDL);
        query.setParameter(3, NovusConstante.IDENTIFICADOR_CODIGO_BARRA);

        List<Object[]> results = query.getResultList();
        if (results.isEmpty()) return null;

        Object[] row = results.get(0);
        
        MovimientosDetallesBean bean = new MovimientosDetallesBean();

        // 🔧 MAPEO CORREGIDO basado en logs de fnc_buscar_productos_market_plu
        bean.setId(safeLong(row[0]));              // row[0] = 10246 (ID)
        bean.setProductoId(safeLong(row[0]));      // row[0] = 10246 (ID)
        bean.setPlu(safeString(row[1]));           // row[1] = 2067 (PLU)
        bean.setEstado(safeString(row[2]));        // row[2] = A (Estado)
        bean.setUnidades_medida_id(safeInt(row[3])); // row[3] = 13 (unidades_medida_id)
        bean.setUnidades_medida(safeString(row[4])); // row[4] = UNIDAD (unidad_descripcion)
        bean.setDescripcion(safeString(row[5]));   // row[5] = ADVIL MAX X 5 UN (descripcion)
        bean.setPrecio(safeFloat(row[6]));         // row[6] = 10500.0 (precio)
        bean.setTipo(safeInt(row[7]));             // row[7] = 23 (tipo)
        bean.setCategoriaId(safeLong(row[8]));     // row[8] = 1686 (categoria_id)
        bean.setCantidadIngredientes(safeInt(row[9])); // row[9] = cantidad_ingredientes
        bean.setCantidadImpuestos(safeInt(row[10])); // row[10] = cantidad_impuestos
        bean.setCategoriaId(safeLong(row[11]));     // row[11] = categoria_id
        bean.setCategoriaDesc(safeString(row[12])); // row[12] = categoria_descripcion
        bean.setCodigoBarra(safeString(row[13]));  // row[13] = codigo_barra

        // ✅ MAPEO CORRECTO SEGÚN fnc_buscar_productos_market_plu:
        // row[19] = bodega_id
        // row[20] = saldo
        // row[21] = costo
        bean.setBodegasId(safeLong(row[19]));      // row[19] = bodega_id ✅
        bean.setSaldo(safeInt(row[20]));           // row[20] = saldo ✅
        bean.setCosto(safeFloat(row[21]));         // row[21] = costo ✅
        bean.setProducto_compuesto_costo(bean.getCosto());
        bean.setBodegasId(safeLong(row[19]));      // row[19] = 2704 - bodega_id ✅ CORREGIDO

        bean.setCompuesto(bean.getTipo() == NovusConstante.TIPO_PRODUCTO_COMPUESTO || bean.getTipo() == NovusConstante.TIPO_PRODUCTO_PROMOCION);

        // Mapeo corregido basado en la estructura real de fnc_buscar_productos_market_plu
        bean.setImpuestos((ArrayList<ImpuestosBean>) organizarImpuestosProductos(
                safeJsonArray(safeString(row[14]))));
        
        bean.setIngredientes(organizarIngredientesProductos(
                safeJsonArray(safeString(row[18]))));

        if (!bean.getIngredientes().isEmpty()) {
            for (ProductoBean ingrediente : bean.getIngredientes()) {
                bean.setProducto_compuesto_costo(
                        bean.getProducto_compuesto_costo() +
                                ingrediente.getProducto_compuesto_costo() * ingrediente.getProducto_compuesto_cantidad()
                );
            }
        }

        return bean;
    }


}

