/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.root.attr;

import java.awt.Color;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 *
 * @author gavalian
 */
public class ColorPalette {
    double[] red   = {0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,
                             0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,
                             0.,0.,0.,0.17,0.33,0.50,0.67,0.83,1.00,1.00,
                             1.,1.,1.,1.,1.,1.,1.,1.,1.,1.,
                             1.,1.,1.,1.,1.,1.,1.};
    double[] green = {0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,
                             0.,0.08,0.15,0.23,0.31,0.38,0.46,0.53,0.59,0.66,
                             0.73,0.80,0.87,0.72,0.58,0.43,0.29,0.14,0.00,0.08,
                             0.17,0.25,0.33,0.42,0.50,0.58,0.67,0.75,0.83,0.92,
                             1.,1.,1.,1.,1.,1.,1.};
    double[] blue  = {0.30,0.33,0.36,0.39,0.42,0.45,0.48,0.52,0.56,0.60,
                             0.64,0.68,0.68,0.70,0.70,0.70,0.70,0.64,0.56,0.48,
                             0.40,0.33,0.,0.,0.,0.,0.,0.,0.,0.,
                             0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,
                             0.,0.17,0.33,0.50,0.67,0.83,1.};
    
    private final ArrayList<Color>  palette = new ArrayList<Color>();
    private static TreeMap<Integer,Color>  colorPalette = ColorPalette.initColorMap();

    public ColorPalette(){
        this.set(3);
    }
    
    public void ColorPalette(int mode){
        this.set(mode);
    }
    
    public final void set(int mode){
        if(mode==1){
            palette.clear();
            palette.add(new Color(0x00,0x00,0x90));
            palette.add(new Color(0x00,0x0f,0xff));
            palette.add(new Color(0x00,0x90,0xff));
            palette.add(new Color(0x0f,0xff,0xee));
            palette.add(new Color(0x90,0xff,0x70));
            palette.add(new Color(0xff,0xee,0x00));
            palette.add(new Color(0xff,0x70,0x00));
            palette.add(new Color(0xee,0x00,0x00));            
            palette.add(new Color(0x7f,0x00,0x00));
        }
        
        if(mode==2){
            palette.clear();
            palette.add(new Color(255,  0,  0));
            palette.add(new Color(255, 15,  0));
            palette.add(new Color(255, 67,  0));
            palette.add(new Color(255,110,  0));
            palette.add(new Color(255,132,  0));
            palette.add(new Color(255,160,  0));
            
            palette.add(new Color(255,187,  0));
            palette.add(new Color(255,215,  0));
            
            palette.add(new Color(255,240,  0));
            palette.add(new Color(250,255,  0));
            palette.add(new Color(250,255, 85));
            palette.add(new Color(250,255,155));
            palette.add(new Color(250,255,224));                                
        }
        
        if(mode==3){
            palette.clear();
            //palette.add(new Color(245,245,245));
            for(int loop = 0; loop < red.length; loop++){
                int pred   = (int) (255.0*red[loop]);
                int pgreen = (int) (255.0*green[loop]);
                int pblue  = (int) (255.0*blue[loop]);
                palette.add(new Color(pred,pgreen,pblue));
            }
        }
    }
    
    public Color getColor3D(int bin){
        return this.palette.get(bin);
    }
    
    public int getColor3DSize(){
        return this.palette.size();
    }
    
    public Color getColor3D(double value, double max, boolean islog){        
        double fraction = 0.0;
        
        if(max!=0){
            if(islog==true){
                fraction = Math.log(value+1.0)/Math.log(max);
            } else {
                fraction = value/max;
            }
        }
        
        if(fraction>1.0) fraction = 1.0;
        if(fraction<=0.0){
            return new Color(200,200,200);
        }        
        //System.out.println("PALETTE MAX = " + max + " LOG = " + islog + " VALUE = " + value +
        //        "  FRACTION = " + fraction + "  NEW FRACTION = " + (value/max));
        double binC = fraction*palette.size();
        int bin = (int) binC;
        if(bin>=palette.size()) bin = palette.size()-1;
        return palette.get(bin);
    }
    
    public Color getRange(double fraction){
        if(fraction>1.0) fraction = 1;
        if(fraction<0.0){
            return new Color(128,128,128);
        }
        /*
        if(fraction<0.0){
            return new Color(168,168,168);
        }*/
        
        double binC = fraction*palette.size();
        int bin = (int) binC;
        if(bin>=palette.size()) bin = palette.size()-1;
        return palette.get(bin);
    }
    
    public static void   setColor(int color, int red, int green, int blue, int alpha){
        ColorPalette.colorPalette.put(color, new Color(red,green,blue,alpha));
    }
    
    public static void   setColor(int color, int red, int green, int blue){
        ColorPalette.colorPalette.put(color, new Color(red,green,blue));
    }
    
    public static Color  getColor(int color){
        return ColorPalette.colorPalette.get(color);
    }
    
    public static TreeMap<Integer,Color> initColorMap(){
        TreeMap<Integer,Color> colors = new TreeMap<Integer,Color>();
        
        colors.put(0, Color.white);
        colors.put(1, Color.BLACK);
        colors.put(2,new Color(210,79,68));
        colors.put(3,new Color(137,216,68));
        colors.put(4,new Color(77,176,221));
        colors.put(5,new Color(246,188,47));        
        colors.put(6,new Color(222,82,111));
        colors.put(7,new Color(230,130,58));
        colors.put(8,new Color(90,207,161));
        colors.put(9,new Color(106,120,203));
        
        for(int loop = 0; loop < 10; loop++){
            colors.put(20+loop, getTranslucent(colors.get(loop),200));
            colors.put(30+loop, getTranslucent(colors.get(loop),160));
            colors.put(40+loop, getTranslucent(colors.get(loop),120));
            colors.put(50+loop, getTranslucent(colors.get(loop), 50));
        }
        
        
        
        
        return colors;
    }
    
    
    private static Color getTranslucent(Color col, int alpha){
        return new Color(col.getRed(),col.getGreen(),col.getBlue(),alpha);
    }
}
