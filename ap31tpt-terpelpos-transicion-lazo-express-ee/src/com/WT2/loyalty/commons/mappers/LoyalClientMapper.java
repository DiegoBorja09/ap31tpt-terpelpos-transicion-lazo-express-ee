
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.WT2.loyalty.commons.mappers;

import com.WT2.commons.domain.adapters.IMapper;
import com.WT2.loyalty.presentation.dto.LoyalClientDto;
import com.WT2.loyalty.domain.entities.beans.LoyalClient;

/**
 *
 * @author USUARIO
 */
public class LoyalClientMapper implements IMapper<LoyalClientDto, LoyalClient>{

    @Override
    public LoyalClient mapTo(LoyalClientDto input) {

        LoyalClient loyalClient = new LoyalClient();
        loyalClient.setFidelizado(input.isFidelizado());
        loyalClient.setNumberDoc(input.getNumberDoc());
        loyalClient.setTypeDocument(input.getTypeDocument());
        
        return loyalClient;
    }
    
    
    
    
    
}
