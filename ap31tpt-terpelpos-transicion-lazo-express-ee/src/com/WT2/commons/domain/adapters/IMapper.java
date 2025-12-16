/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.WT2.commons.domain.adapters;

import java.io.IOException;

/**
 *
 * @author USUARIO
 * @param <I>
 * @param <O>
 */
public interface IMapper<I,O> {
    
  public O mapTo(I input) ;
  
    
}
