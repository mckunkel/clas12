/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clasrec.utils;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author gavalian
 */
public class CalibrationEntry {
    TreeMap<String,Double>  constants  = new TreeMap<String,Double>();

    public CalibrationEntry(){
        
    }
    
    public Map<String,Double>  getMap(){
        return this.constants;
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        for(Map.Entry<String,Double>  entry : this.constants.entrySet()){
            str.append(String.format("%s=%9.5f,",entry.getKey(),entry.getValue()));
        }
        return str.toString();
    }
}
