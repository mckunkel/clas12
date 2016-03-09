/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.root.utils;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author gavalian
 */
public class DataUtilities {
    
    public static Map<Integer,String>  stringFormats = DataUtilities.generateStringFormatMap();
    
    public static int getSignificantFigures(double number){
        int   placeOfDifference = (int) Math.floor(Math.log(number) / Math.log(10));
        return placeOfDifference;
    }
    
    public static String getNumberString(double value, int decimal){
        int sigfig = 0;
        sigfig = -DataUtilities.getSignificantFigures(value);

        if(sigfig<0){
            if(sigfig>-4) return String.format("%.0f", value);
            double newValue = value*Math.pow(10, sigfig);
            return String.format("%.0fx10^%d", newValue,sigfig);
        }
        if(sigfig>=0){
            if(sigfig < 4) return String.format(DataUtilities.stringFormats.get(sigfig), value);
            double newValue = value*Math.pow(10, sigfig);
            return String.format("%.2fx10^-%d", newValue,sigfig);
        } 
        return "-";
    }
    
    public static Map<Integer,String>  generateStringFormatMap(){
        Map<Integer,String>  sf = new TreeMap<Integer,String>();
        sf.put(0, "%.2f");
        sf.put(1, "%.3f");
        sf.put(2, "%.4f");
        sf.put(3, "%.5f");
        return sf;
    } 
    
    public static String getNumberStringLog(double value){
        return "";
    }
    
    public static void main(String[] args){
        //System.out.println(DataUtilities.getSignificantFigures(0.000000056));
        double value = 0.000000578984;
        System.out.println("string " + value + "  string --> " + DataUtilities.getNumberString(value, 3));
        
        for(int loop = -10; loop < 10; loop++){
            double logValue = Math.pow(10, loop);
            System.out.println( loop + "  " + logValue + " ---> " + DataUtilities.getNumberString(logValue, 0));
        }
    }
}
