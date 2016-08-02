/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.root.attr;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 *
 * @author gavalian
 */
public class TStyle {
    
    private static Integer axisFrameLineWidth    = 1;
    private static String  axisFontStringName    = "Helvetica";
    private static Integer axisFontSize          = 18;
    
    private static Color   padBackgroundColor    = Color.WHITE;
    private static Color   padFrameColor         = Color.WHITE;
    private static Color   axisFrameColor        = Color.BLACK;
    
    private static String  statBoxFontStringName = "Courier";
    private static Integer statBoxFontSize       = 18;
    private static Boolean statBoxOptions        = true;
    private static Double  statBoxTextGap        = 1.2;
    public  static List<String>    systemFontNames       = TStyle.getSystemFontList();
    
            
    public static List<String>  getSystemFontList(){
        Set<String>  fontSet = new HashSet<String>();
        List<String> fontList = new ArrayList<String>();

        fontSet.add("Avenir");
        fontSet.add("Arial");
        fontSet.add("American Typewriter");
        fontSet.add("Bradley Hand");
        fontSet.add("Chalkduster");
        fontSet.add("Charter");
        fontSet.add("Courier");
        fontSet.add("HanziPen TC");
        fontSet.add("Helvetica");
        fontSet.add("Helvetica Neue");
        fontSet.add("Menlo");
        fontSet.add("Monaco");
        fontSet.add("Monospaced");
        fontSet.add("SansSerif");
        fontSet.add("Times");
        fontSet.add("Times New Roman");
        fontSet.add("Veranda");
        // Check if the system contains the fonts that we want the user
        // to use. If so, add them to available list
        String[] fonts = 
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        if(fonts!=null){
            for(String fontname : fonts){
                if(fontSet.contains(fontname)==true){
                    fontList.add(fontname);
                }
            }
        }
        System.out.println("[SystemFonts] ---> set size = " + fontSet.size()
        + ", available " + fontList.size());
        return fontList;
    }
    
    public static List<String>   systemFonts(){
        return TStyle.systemFontNames;
    }
    
    public  static void setAxisFont(String name, int size){
        TStyle.axisFontStringName = name;
        TStyle.axisFontSize = size;
    }
    
    public static Color getFrameBackgroundColor(){
        return TStyle.padBackgroundColor;
    }
    
    public static Color getFrameFillColor(){
        return TStyle.padFrameColor;
    }
    
    public static Color getAxisColor(){
        return TStyle.axisFrameColor;
    }
    
    public static void setFrameFillColor(int red, int green, int blue){
        padFrameColor = new Color(red,green,blue);
    }
    
    public static void setFrameBackgroundColor(int red, int green, int blue){
        padBackgroundColor = new Color(red,green,blue);
    }
    
    public static void setAxisColor(int red, int green, int blue){
        axisFrameColor = new Color(red,green,blue);
    }
    
    public static int getAxisFontSize(){
        return TStyle.axisFontSize;
    }
    
    public static String getAxisFontName(){
        return TStyle.axisFontStringName;
    }
    
    public  static void setStatBoxFont(String name, int size){
        TStyle.statBoxFontStringName = name;
        TStyle.statBoxFontSize = size;
    }
    
    public static int getStatBoxFontSize(){
        return TStyle.statBoxFontSize;
    }
    
    public static String getStatBoxFontName(){
        return TStyle.statBoxFontStringName;
    }
    
    public static void setFrameLineWidth(int width){
        TStyle.axisFrameLineWidth = width;
    }
    
    public static int getFrameLineWidth(){
        return TStyle.axisFrameLineWidth;
    }
    
    public static void setOptStat(boolean flag){
        TStyle.statBoxOptions = flag;
    }
    
    public static boolean getOptStat(){
        return TStyle.statBoxOptions;
    }
    
    public static void setStatBoxTextGap(double value){
        TStyle.statBoxTextGap = value;
    }
    
    public static double getStatBoxTextGap(){
        return TStyle.statBoxTextGap;
    }
}
