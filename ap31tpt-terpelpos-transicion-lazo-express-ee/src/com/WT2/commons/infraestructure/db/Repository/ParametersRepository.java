/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.commons.infraestructure.db.Repository;

import com.WT2.commons.domain.adapters.IRepository;
import com.WT2.commons.domain.entity.Parameter;
import com.firefuel.Main;
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
public class ParametersRepository implements IRepository<String, Parameter> {

    public Connection connection;

    public ParametersRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Parameter getData(String params) {

        Parameter parameter = new Parameter();
        parameter.setCodigo("");
        parameter.setValor("");

        try {
            String sql = "select codigo, valor from parametros where codigo = ?;";
            if (connection.isClosed()) {
                connection = Main.obtenerConexion("lazoexpresscore");
            }

            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, params);
            ResultSet re = stm.executeQuery();

            while (re.next()) {
                parameter.setCodigo(re.getString("codigo"));
                parameter.setValor(re.getString("valor"));
            }

        } catch (SQLException s) {
            Logger.getLogger(Parameter.class.getName()).log(Level.SEVERE, null, s);
        }
        return parameter;
    }

    @Override
    public int updateData(String params) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
