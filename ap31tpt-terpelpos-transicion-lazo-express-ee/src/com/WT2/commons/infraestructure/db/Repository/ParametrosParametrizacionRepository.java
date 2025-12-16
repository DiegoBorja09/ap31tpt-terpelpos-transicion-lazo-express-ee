/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.commons.infraestructure.db.Repository;

import com.WT2.appTerpel.commons.dto.PaymentDTO;
import com.WT2.appTerpel.domain.valueObject.AppterpelWatcherParametros;
import com.WT2.commons.domain.adapters.IConnectionDB;
import com.WT2.commons.domain.adapters.IRepository;
import com.WT2.commons.domain.adapters.IUseCase;
import com.WT2.commons.domain.entity.ValoresParametros;
import com.WT2.commons.domain.valueObject.ProcessStatus;
import com.WT2.commons.domain.valueObject.QueryColectionCommons;
import com.WT2.payment.domian.valueObject.PaymentStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author USUARIO
 */
public class ParametrosParametrizacionRepository implements IRepository<String, ValoresParametros> {

    public IConnectionDB<Connection> dbConnect;

    public ParametrosParametrizacionRepository(IConnectionDB<Connection> dbConnect) {
        this.dbConnect = dbConnect;
    }

    @Override
    public ValoresParametros getData(String params) {

        ValoresParametros valoresParametros = new ValoresParametros();
        valoresParametros.setNombre(AppterpelWatcherParametros.PARAMETRO_INVALIDO);
        valoresParametros.setDescrtipcion(AppterpelWatcherParametros.PARAMETRO_INVALIDO);

        try {
            PreparedStatement stm = dbConnect.getDBConection().prepareStatement(QueryColectionCommons.RECUPERA_PARAMETROS);
            stm.setString(1, params);
            ResultSet re = stm.executeQuery();

            while (re.next()) {
                valoresParametros.setDescrtipcion(re.getString("descripcion"));
                valoresParametros.setEstado(re.getString("estado"));
                valoresParametros.setNombre(re.getString("nombre"));
                valoresParametros.setValor(re.getString("valor"));
                valoresParametros.setId_empresa(re.getLong("id_empresa"));
                valoresParametros.setId_parametro_valor(re.getLong("id_parametro_valor"));
                valoresParametros.setValor_maximo(re.getLong("valor_maximo"));
                valoresParametros.setValor_maximo(re.getLong("valor_minimo"));

            }

        } catch (SQLException ex) {
            Logger.getLogger(ParametrosParametrizacionRepository.class.getName()).log(Level.SEVERE, null, ex);
        }

        return valoresParametros;
    }

    @Override
    public int updateData(String params) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
