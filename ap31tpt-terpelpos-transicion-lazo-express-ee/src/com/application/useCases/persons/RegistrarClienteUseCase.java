package com.application.useCases.persons;

import com.application.core.BaseUseCases;
import com.domain.entities.CtPerson;
import com.infrastructure.database.JpaEntityManagerFactory;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.repositories.CtPersonRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Date;
/**
 * Caso de uso para registrar un cliente genérico con ID fijo (3) si no existe.
 * Reemplaza: MovimientosDao.getTipoClienteRegistrado()
 * Usado en: procesos de venta para asegurar que el cliente "CLIENTES REGISTRADOS" esté creado - ClienteFacturaElectronica.java
 */

public class RegistrarClienteUseCase {

    private final EntityManagerFactory entityManagerFactory;

    public RegistrarClienteUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    public void execute(Long empresasId) {
        EntityManager em = entityManagerFactory.createEntityManager();

        try {
            em.getTransaction().begin();
            CtPersonRepository repository = new CtPersonRepository(em);



            if (!repository.existsById()) {
                CtPerson persona = getCtPerson(empresasId);

                repository.save(persona);
            }

            em.getTransaction().commit();
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new RuntimeException("Error al registrar persona", ex);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    private static CtPerson getCtPerson(Long empresasId) {
        Long idPersona = 3L;
        CtPerson persona = new CtPerson();
        persona.setId(idPersona);
        persona.setTiposIdentificacionId(1L);
        persona.setIdentificacion("3333333");
        persona.setNombre("CLIENTES REGISTRADOS");
        persona.setEstado("A");
        persona.setEmpresasId(empresasId);
        persona.setGenero("");
        persona.setSangre("A+");
        persona.setCreateUser("1");
        persona.setCreateDate(new Date());
        persona.setUpdateUser("1");
        persona.setUpdateDate(new Date());
        persona.setCiudadesId(1L);
        persona.setCorreo(null);
        persona.setPerfilesId(null);
        persona.setDireccion(null);
        persona.setTelefono(null);
        persona.setCelular(null);
        persona.setSucursalesId(null);
        persona.setSincronizado(false);
        persona.setTag(null);
        persona.setPin(null);
        return persona;
    }
}


