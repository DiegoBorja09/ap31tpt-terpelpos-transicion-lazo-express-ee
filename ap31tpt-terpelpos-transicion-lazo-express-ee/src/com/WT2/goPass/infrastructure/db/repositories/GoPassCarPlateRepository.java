/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.goPass.infrastructure.db.repositories;

import com.WT2.commons.domain.adapters.IConnectionDB;
import com.WT2.commons.domain.adapters.IRepository;
import com.WT2.goPass.domain.entity.beans.PaymentGopassParams;
import com.WT2.goPass.domain.valueObject.QueryCollectionGoPass;
import com.firefuel.Main;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author USUARIO
 */
public class GoPassCarPlateRepository implements IRepository<PaymentGopassParams, Object> {

    private IConnectionDB<Connection> conection;

    public GoPassCarPlateRepository(IConnectionDB<Connection> conection) {
        this.conection = conection;
    }

    //NO SE USA POR LO QUE NO QUE NO IMPORTA LO QUE RETORNA, NO QUITAR, JAVA SE MOLESTA SI LE QUITAS UN METODO DE INTERFAZ
    // MODIFICAR LA INTERFAZ IREPOSITORY SOLO HARA LAS COSAS PEOR
    // ESTO NO ES UNA EMPANADA, EN UN FUTURO PUEDO SER UTIL
    @Override
    public Object getData(PaymentGopassParams params) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int updateData(PaymentGopassParams params) {
        try {

            Connection dbConnect = conection.getDBConection();

            PreparedStatement stm = dbConnect.prepareStatement(QueryCollectionGoPass.UPDATE_CAR_PLATE_CT_MOV);
            stm.setString(1, params.getPlaca());
            stm.setLong(2, params.getIdentificadorVentaTerpel().getIdentificadorMovimiento());
            int result = stm.executeUpdate();
            stm.close();
            return result;
        } catch (SQLException ex) {
            Logger.getLogger(GoPassCarPlateRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

}
