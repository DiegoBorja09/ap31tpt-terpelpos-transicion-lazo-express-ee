/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.commons.application.UseCase;

import com.WT2.commons.domain.adapters.IMapper;
import com.WT2.commons.domain.adapters.IRepository;
import com.WT2.commons.domain.adapters.IUseCase;
import com.WT2.commons.domain.entity.WatcherParameter;
import com.WT2.loyalty.domain.entities.beans.LoyalityConfig;

/**
 *
 * @author USUARIO
 */
public class RecuperarParameterFidelizacion implements IUseCase<String, LoyalityConfig> {

    private IRepository<String, WatcherParameter> watcherParameter;
    private IMapper<String, LoyalityConfig> loyaltiParameterCongif;

    public RecuperarParameterFidelizacion(IRepository<String, WatcherParameter> watcherParameter, IMapper<String, LoyalityConfig> loyaltiParameterCongif) {
        this.watcherParameter = watcherParameter;
        this.loyaltiParameterCongif = loyaltiParameterCongif;
    }

    @Override
    public LoyalityConfig execute(String input) {

        WatcherParameter watcherParameterData = watcherParameter.getData(input);
        return loyaltiParameterCongif.mapTo(watcherParameterData.getValor());

    }

}
