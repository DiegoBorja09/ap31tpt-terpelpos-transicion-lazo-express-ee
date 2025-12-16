package com.application.useCases.ventas;

import com.application.core.BaseUseCases;
import com.firefuel.datafonos.EstadoVentaDatafono;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.repositories.VentaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Caso de uso para validar si una venta de datáfono se encuentra pendiente.
 * Reemplaza: MovimientosDao.validarVentaPendienteDatafono()
 * Usado en: flujo de pagos mixtos y validación de ventas activas por datáfono.
 */
public class ValidarVentaPendienteDatafonoUseCase implements BaseUseCases<Boolean> {

    private final long idTransaccionDatafono;
    private final EntityManagerFactory entityManagerFactory;

    public ValidarVentaPendienteDatafonoUseCase(long idTransaccionDatafono) {
        this.idTransaccionDatafono = idTransaccionDatafono;
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public Boolean execute() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            VentaRepository repository = new VentaRepository(em);

            Long estadoTransaccion = repository.obtenerEstadoTransaccionDatafono(idTransaccionDatafono);
            Long idMovimiento = repository.obtenerIdMovimientoDesdeDatafono(idTransaccionDatafono);

            return estadoTransaccion == EstadoVentaDatafono.POR_ENVIAR.getValor()
                    && !repository.hayVentaPendienteDePagoMixto(idMovimiento, EstadoVentaDatafono.PENDIENTE.getValor());

        } catch (Exception e) {
            System.err.println("Error en ValidarVentaPendienteDatafonoUseCase: " + e.getMessage());
            return false;
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}



