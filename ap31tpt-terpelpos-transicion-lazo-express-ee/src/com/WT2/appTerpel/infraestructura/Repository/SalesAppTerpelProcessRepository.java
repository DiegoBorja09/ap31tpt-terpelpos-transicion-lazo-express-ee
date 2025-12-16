/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.appTerpel.infraestructura.Repository;

import com.WT2.commons.domain.adapters.IMapper;
import com.WT2.appTerpel.commons.dto.SalesDto;
import com.WT2.commons.domain.adapters.IRepository;
import com.WT2.appTerpel.domain.entities.PaymentProcessedParams;
import com.WT2.appTerpel.domain.entities.Sales;
import com.WT2.appTerpel.domain.valueObject.QueryCollection;
import com.controllers.NovusUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author USUARIO
 */
public class SalesAppTerpelProcessRepository implements IRepository<PaymentProcessedParams, List<Sales>> {

    private Connection dbConnect;
    private IMapper<ResultSet, SalesDto>  salesDtoMapper;
    private  IMapper<SalesDto, Sales>  salesMapper;

    public SalesAppTerpelProcessRepository(Connection dbConnect, IMapper<ResultSet, SalesDto> salesDtoMapper, IMapper<SalesDto, Sales> salesMapper) {
        this.dbConnect = dbConnect;
        this.salesDtoMapper = salesDtoMapper;
        this.salesMapper = salesMapper;
    }

    @Override
    public List<Sales> getData(PaymentProcessedParams params) {
        List<Sales> sales = new ArrayList<>();
        try {
            PreparedStatement stm = dbConnect.prepareStatement(QueryCollection.appterpelVentasProcesadas);

            stm.setLong(1, params.getIdJornada());

            stm.setLong(2, params.getIdPromotor());
            stm.setInt(3, params.getRange_register());
            ResultSet re = stm.executeQuery();

            while (re.next()) {
                SalesDto salesDto = salesDtoMapper.mapTo(re);
                Sales sale = salesMapper.mapTo(salesDto);
                sales.add(sale);
            }
            stm.close();
        } catch (SQLException ex) {
            Logger.getLogger(SalesAppTerpelProcessRepository.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // Cerrar conexion para evitar memory leaks
            if (dbConnect != null) {
                try {
                    if (!dbConnect.isClosed()) {
                        dbConnect.close();
                        //NovusUtils.printLn("CONEXION CERRADA: SalesAppTerpelProcessRepository.getData()");
                    }
                } catch (SQLException e) {
                    Logger.getLogger(SalesAppTerpelProcessRepository.class.getName()).log(Level.SEVERE, "Error cerrando conexion en getData", e);
                }
            }
        }
        return sales;
    }

    @Override
    public int updateData(PaymentProcessedParams params) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }



}
