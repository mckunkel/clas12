/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.root.fx.base;

/**
 *
 * @author gavalian
 */
public interface IDataSetFX {
    
    String  getName();
    int     getDimension();
    int     getDataSize();
    double  getValue(int dim, int bin);
    double  getError(int dim, int bin);
    
}
