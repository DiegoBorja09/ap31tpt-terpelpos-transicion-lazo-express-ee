package com.application.useCases.facturacionelectronica;

import com.application.core.BaseUseCases;
import com.controllers.NovusConstante;
import com.controllers.NovusUtils;
import com.dao.FacturacionElectronicaDao;
import com.facade.facturacionelectronica.TipoIdentificaion;
import com.firefuel.Main;
import com.infrastructure.database.DatabaseNames;
import com.infrastructure.database.JpaEntityManagerFactory;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.TreeMap;

/*public void cargarTiposIdentificaion() {
    Connection conexion = Main.obtenerConexion(NovusConstante.LAZOEXPRESSCORE);
    String sql = "   SELECT tipo_de_identificacion, codigo_identificacion, aplica_fidelizacion, caracteres_permitidos, limite_caracteres\n"
            + "FROM facturacion_electronica.identificacion_dian\n"
            + "ORDER BY\n"
            + "  CASE \n"
            + "    WHEN tipo_de_identificacion = 'Cedula de ciudadania' THEN 0\n"
            + "    ELSE 1\n"
            + "  END,\n"
            + "  tipo_de_identificacion;";
    TreeMap<String, TipoIdentificaion> tiposIdentificaion = new TreeMap<>();
    try (Statement smt = conexion.createStatement()) {
        ResultSet rs = smt.executeQuery(sql);
        while (rs.next()) {
            TipoIdentificaion tipoIdentificaion = new TipoIdentificaion();
            tipoIdentificaion.setTipoDocumento(rs.getString("tipo_de_identificacion"));
            tipoIdentificaion.setCodigoTipoDocumento(rs.getLong("codigo_identificacion"));
            tipoIdentificaion.setAplicaFidelizacion(rs.getBoolean("aplica_fidelizacion"));
            tipoIdentificaion.setCaracteresPermitidos(rs.getString("caracteres_permitidos"));
            tipoIdentificaion.setCantidadCaracteres(rs.getInt("limite_caracteres"));
            tiposIdentificaion.put(tipoIdentificaion.getTipoDocumento().toUpperCase(), tipoIdentificaion);
        }
        NovusConstante.setTiposIdentificaion(tiposIdentificaion);
    } catch (Exception e) {
        NovusUtils.printLn("ha ocurrido un error inesperado al momento de obtener la informacion de los medios de identificacion ubicado en la clase " + FacturacionElectronicaDao.class.getName() + " error -> " + e.getMessage());
    }
}*/

public class FindAllTiposIdentificacionDianUseCase implements BaseUseCases<TreeMap<String, TipoIdentificaion>> {

    private final EntityManagerFactory entityManagerFactory;

    public FindAllTiposIdentificacionDianUseCase() {
        this.entityManagerFactory = JpaEntityManagerFactory.INSTANCE
                .getEntityManagerFactory(DatabaseNames.LAZOEXPRESSCORE);
    }

    @Override
    public TreeMap<String, TipoIdentificaion> execute() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            String sql = "SELECT tipo_de_identificacion, codigo_identificacion, aplica_fidelizacion, " +
                    "caracteres_permitidos, limite_caracteres " +
                    "FROM facturacion_electronica.identificacion_dian " +
                    "ORDER BY CASE " +
                    "WHEN tipo_de_identificacion = 'Cedula de ciudadania' THEN 0 " +
                    "ELSE 1 END, " +
                    "tipo_de_identificacion";

            @SuppressWarnings("unchecked")
            List<Object[]> results = entityManager.createNativeQuery(sql).getResultList();
            TreeMap<String, TipoIdentificaion> tiposIdentificacionMap = new TreeMap<>();

            for (Object[] row : results) {
                TipoIdentificaion tipo = new TipoIdentificaion();
                tipo.setTipoDocumento((String) row[0]);
                tipo.setCodigoTipoDocumento(((Number) row[1]).longValue());
                tipo.setAplicaFidelizacion((Boolean) row[2]);
                tipo.setCaracteresPermitidos((String) row[3]);
                tipo.setCantidadCaracteres(((Number) row[4]).intValue());

                tiposIdentificacionMap.put(tipo.getTipoDocumento().toUpperCase(), tipo);
            }

            NovusConstante.setTiposIdentificaion(tiposIdentificacionMap);
            return tiposIdentificacionMap;
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}