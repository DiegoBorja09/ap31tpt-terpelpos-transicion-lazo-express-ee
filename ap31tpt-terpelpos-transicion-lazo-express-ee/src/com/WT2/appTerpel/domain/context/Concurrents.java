
package com.WT2.appTerpel.domain.context;

import com.WT2.appTerpel.domain.entities.MedioPagoImageBean;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class Concurrents {
    
    public static ConcurrentHashMap<Long,MedioPagoImageBean> medioPagoImage = new ConcurrentHashMap<>();
    public static CopyOnWriteArrayList<MedioPagoImageBean> medioPagoImageList = new CopyOnWriteArrayList<>();
    private Concurrents(){}
    
}
