/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firefuel;

import com.bean.BodegaBean;
import java.util.ArrayList;

/**
 *
 * @author Admin
 */
public interface MyCallback extends Runnable {

    abstract void run(ArrayList<BodegaBean> data);

    void sendNotificacion(String mensaje);
    
}
