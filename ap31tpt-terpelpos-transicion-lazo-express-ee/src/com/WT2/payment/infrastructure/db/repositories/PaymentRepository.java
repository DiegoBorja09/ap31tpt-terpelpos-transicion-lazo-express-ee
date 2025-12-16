/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.payment.infrastructure.db.repositories;

import com.WT2.Containers.Dependency.SingletonMedioPago;
import com.WT2.appTerpel.commons.dto.PaymentDTO;
import com.WT2.appTerpel.domain.entities.Payment;
import com.WT2.payment.domian.valueObject.PaymentStatus;
import com.WT2.commons.domain.valueObject.ProcessStatus;
import com.WT2.appTerpel.domain.valueObject.QueryCollection;
import com.WT2.commons.domain.adapters.IConnectionDB;
import com.WT2.commons.domain.adapters.IRepository;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.ResultSet;

/**
 *
 * @author USUARIO
 */
public class PaymentRepository implements IRepository<Long, Payment> {

    public IConnectionDB<Connection> dbConnect;

    public PaymentRepository(IConnectionDB dbConnect) {
        this.dbConnect = dbConnect;
    }

    @Override
    public Payment getData(Long idMov) {
        String sql = QueryCollection.funGetPaymentByIdMov;
        try {

            PreparedStatement stm = dbConnect.getDBConection().prepareStatement(sql);
            
            stm.setLong(1, idMov);
            ResultSet re = stm.executeQuery();
            PaymentDTO paymentDto = new PaymentDTO();
            paymentDto.setEstado_proceso_descripcion(ProcessStatus.NO_ENCONTRADO);
            paymentDto.setEstado_venta_descripcion(PaymentStatus.NO_ENCONTRADO);

            while (re.next()) {
                paymentDto.setId_pago(re.getInt("id_pago"));
                paymentDto.setId_estado(re.getInt("id_estado"));
                paymentDto.setId_estatus(re.getInt("id_estatus"));
                paymentDto.setEstado_proceso_descripcion(re.getString("estado_proceso_descripcion"));
                paymentDto.setEstado_venta_descripcion(re.getString("estado_venta_descripcion"));
                paymentDto.setId_tipo_integracion(re.getInt("id_tipo_integracion"));
                paymentDto.setId_movimiento(re.getInt("id_movimiento"));
            }

            
            stm.close();
            return SingletonMedioPago.ConetextDependecy.getPaymentMapper().mapTo(paymentDto);

        } catch (SQLException ex) {
            Logger.getLogger(PaymentRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new Payment();
    }

  

    @Override
    public int updateData(Long params) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
