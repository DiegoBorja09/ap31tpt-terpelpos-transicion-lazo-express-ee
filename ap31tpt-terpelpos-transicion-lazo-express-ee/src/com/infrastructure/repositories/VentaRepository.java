package com.infrastructure.repositories;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.infrastructure.Enums.SqlQueryEnum;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import com.google.gson.JsonObject;
import org.postgresql.util.PGobject;

import java.util.List;

public class VentaRepository {

    private final EntityManager entityManager;

    public VentaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public boolean isPendienteTransaccionMovimiento(long idMovimiento) {
        String sql = SqlQueryEnum.IS_PENDIENTE_TRANSACCION_MOVIMIENTO.getQuery();

        try {
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, idMovimiento);

            Object result = query.getSingleResult();
            if (result != null) {
                return Boolean.parseBoolean(result.toString());
            }
        } catch (Exception e) {
            System.err.println("Error al consultar estado de la transacción: " + e.getMessage());
        }

        return false;
    }
    public Long obtenerIdMovimientoDesdeDatafono(long idTransaccionDatafono) {
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.OBTENER_ID_MOVIMIENTO_DESDE_DATAFONO.getQuery());
            query.setParameter(1, idTransaccionDatafono);
            Object result = query.getSingleResult();
            return result != null ? ((Number) result).longValue() : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }

    public Long obtenerEstadoTransaccionDatafono(long idTransaccionDatafono) {
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.VALIDAR_ESTADO_TRANSACCION_DATAFONO.getQuery());
            query.setParameter(1, idTransaccionDatafono);
            Object result = query.getSingleResult();
            return result != null ? ((Number) result).longValue() : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }

    public boolean hayVentaPendienteDePagoMixto(long idMovimiento, int estadoPendiente) {
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.HAY_VENTA_PENDIENTE_PAGO_MIXTO.getQuery());
            query.setParameter(1, idMovimiento);
            query.setParameter(2, estadoPendiente);
            Object result = query.getSingleResult();
            return ((Number) result).intValue() > 0;
        } catch (Exception e) {
            return false;
        }
    }
    public void setReaperturaInOne(Long idVenta) {
        try {
            // Iniciar transacción si no está activa
            boolean wasActive = entityManager.getTransaction().isActive();
            if (!wasActive) {
                entityManager.getTransaction().begin();
            }
            
            Query query = entityManager.createNativeQuery(SqlQueryEnum.SET_REAPERTURA_EN_UNO.getQuery());
            query.setParameter(1, idVenta);
            query.executeUpdate();
            
            // Commit si iniciamos la transacción
            if (!wasActive) {
                entityManager.getTransaction().commit();
            }
        } catch (Exception e) {
            // Rollback si hay error y iniciamos la transacción
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("❌ Error al actualizar reapertura: " + e.getMessage());
            throw new RuntimeException("Error al actualizar reapertura", e);
        }
    }
    public boolean isVentaFidelizada(Long idVenta) {
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.IS_VENTA_FIDELIZADA.getQuery());
            query.setParameter(1, idVenta);

            Object result = query.getSingleResult();
            return result != null && ((Number) result).intValue() > 0;

        } catch (Exception e) {
            System.err.println("Error al verificar si la venta está fidelizada: " + e.getMessage());
            return false;
        }
    }
    public boolean existeFidelizacion(Long idVenta) {
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.EXISTE_FIDELIZACION.getQuery());
            query.setParameter(1, idVenta);

            Object result = query.getSingleResult();
            return ((Number) result).intValue() > 0;
        } catch (Exception e) {
            System.err.println("Error en existeFidelizacion: " + e.getMessage());
            return false;
        }
    }
    public void actualizarTransmisionAtributosVenta(Long idTransmision, JsonObject json) {
        try {
            // Iniciar transacción si no está activa
            boolean wasActive = entityManager.getTransaction().isActive();
            if (!wasActive) {
                entityManager.getTransaction().begin();
            }
            
            Query query = entityManager.createNativeQuery(SqlQueryEnum.ACTUALIZAR_TRANSMISION_ATRIBUTOS_VENTA.getQuery());
            query.setParameter(1, json.toString());
            query.setParameter(2, idTransmision);
            int resultado = query.executeUpdate();

            // Commit si iniciamos la transacción
            if (!wasActive) {
                entityManager.getTransaction().commit();
            }

            if (resultado > 0) {
                System.out.println("✅ Actualización realizada actualizarTransmisionAtributosVenta");
            } else {
                System.out.println("⚠️ Error al realizar la actualización");
            }
        } catch (Exception e) {
            // Rollback si hay error y iniciamos la transacción
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            System.err.println("❌ Error al actualizar atributos en transmision: " + e.getMessage());
            throw new RuntimeException("Error al actualizar atributos en transmision", e);
        }
    }

    public JsonArray buscarVentaCliente() {
        JsonArray data = new JsonArray();
        Gson gson = new Gson();

        try {
            String sql = SqlQueryEnum.BUSCAR_VENTA_CLIENTE.getQuery();
            Query query = entityManager.createNativeQuery(sql);
            List<Object[]> resultados = query.getResultList();

            for (Object[] row : resultados) {
                long id = ((Number) row[0]).longValue();               // id
                PGobject jsonObject = (PGobject) row[1];               // atributos (PGobject JSON)
                String atributosJson = jsonObject.getValue();          // ✅ Extraer como String
                int sincronizado = ((Number) row[2]).intValue();       // sincronizado
                boolean actualizarVenta = (Boolean) row[3];            // actuluziar_venta_combustible

                JsonObject json = gson.fromJson(atributosJson, JsonObject.class);
                json.addProperty("id_transmision", id);
                json.addProperty("sincronizado", sincronizado);
                json.addProperty("actuluziar_venta_combustible", actualizarVenta);

                data.add(json);
            }

        } catch (Exception e) {
            System.err.println("Error al consultar ventas cliente: " + e.getMessage());
        }

        return data;
    }
    public boolean validarTurnoMedioPago(long idMovimiento) {
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.VALIDAR_TURNO_MEDIO_PAGO.getQuery());
            query.setParameter("idMovimiento", idMovimiento);
            return !query.getResultList().isEmpty();
        } catch (Exception e) {
            System.err.println("Error en validarTurnoMedioPago: " + e.getMessage());
            return false;
        }
    }
    /**
     * Busca una transacción de datafono por ID de movimiento
     * @param idMovimiento ID del movimiento a buscar
     * @return true si encuentra la transacción, false en caso contrario
     */
    public boolean buscarTransaccionDatafono(long idMovimiento) {
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.BUSCAR_TRANSACCION_DATAFONO.getQuery());
            query.setParameter(1, idMovimiento);
            List<?> results = query.getResultList();
            return !results.isEmpty();
        } catch (Exception e) {
            System.err.println("Error al buscar transacción datafono: " + e.getMessage());
            return false;
        }
    }


    /**
     * Obtiene los datos de una venta pendiente de datafono
     * @param idTransacionDatafono ID de la transacción del datafono
     * @return JsonObject con los datos de la venta o null si no existe
     */
    public JsonObject obtenerDatosVentaPendienteDatafono(long idTransacionDatafono) {
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.OBTENER_DATOS_VENTA_PENDIENTE_DATAFONO.getQuery());
            query.setParameter("idTransaccion", idTransacionDatafono);
            
            Object[] result = (Object[]) query.getSingleResult();
            
            if (result != null) {
                JsonObject data = new JsonObject();
                data.addProperty("idTransaccionEstado", ((Number) result[0]).longValue());
                data.addProperty("descripcion", (String) result[1]);
                data.addProperty("idAdquiriente", ((Number) result[2]).longValue());
                data.addProperty("proveedor", (String) result[3]);
                return data;
            }
            
            return null;

        } catch (Exception e) {
            System.err.println("Error al obtener datos venta pendiente datafono: " + e.getMessage());
            return null;
        }
    }

    /**
     * Verifica si hay ventas para un turno y responsable específicos
     * @param turno ID del turno a consultar
     * @param idResponsable ID del responsable
     * @return JsonObject con información de ventas
     */
    public JsonObject hayVentas(long turno, long idResponsable) {
        try {
            Query query = entityManager.createNativeQuery(SqlQueryEnum.OBTENER_TOTAL_VENTAS_TURNO.getQuery());
            query.setParameter("turno", turno);
            query.setParameter("idResponsable", idResponsable);

            JsonObject data = new JsonObject();
            data.addProperty("hay_ventas", false);
            data.addProperty("venta_total", 0);

            Object result = query.getSingleResult();
            if (result != null) {
                data.addProperty("hay_ventas", true);
                data.addProperty("venta_total", ((Number)result).longValue());
            }

            return data;

        } catch (Exception e) {
            System.err.println("Error al consultar ventas: " + e.getMessage());
            JsonObject data = new JsonObject();
            data.addProperty("hay_ventas", false); 
            data.addProperty("venta_total", 0);
            return data;
        }
    }


}
