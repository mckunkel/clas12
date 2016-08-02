/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.root.basic;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class GraphicsAxisDouble {
    
    private Font axisFont = new Font("Avenir",Font.PLAIN,12);
    private double axisMin = 0.0;
    private double axisMax = 0.0;
    
    private int    axisLabelOffset = 6;
    private int    axisTitleOffset = 6;
    
    private List<Double>   axisMarks    = new ArrayList<Double>();
    private int            axisLength   = 100;
    private double         axisAngle    = 0.0;
    private int            axisMaxTicks = 10;
    private String         textOutputFormat = "%.1f";
    
    public GraphicsAxisDouble(){
        
    }
    
    public GraphicsAxisDouble(int length, double min, double max){
        this.setLength(length);
        this.setMinMax(min, max);
    }
    
    public GraphicsAxisDouble(double min, double max){
        this.setMinMax(min, max);
    }
    /**
     * set length of the axis. user for plotting.
     * @param length 
     */
    public final void setLength(int length){
        this.axisLength = length;
    }
    /**
     * Set minimum and maximum for the axis
     * @param min
     * @param max 
     */
    public final void setMinMax(double min, double max){
        this.axisMin = min;
        this.axisMax = max;
        this.calculate();
        this.updateStringFormat();
    }
    /**
     * Set maximum number of ticks.
     * @param maxticks 
     */
    public void setMaxTicks(int maxticks){
        this.axisMaxTicks = maxticks;
        this.calculate();
        this.updateStringFormat();
    }
    /**
     * Calculates the axis tick marks and places them into an array.
     */
    public void calculate(){
        double range = this.getNiceNumber(axisMax - axisMin, false);
        double tickSpacing = this.getNiceNumber(range / (axisMaxTicks - 1), true);
        double niceMin =
                Math.floor(axisMin / tickSpacing) * tickSpacing;
        double niceMax =
                Math.ceil(axisMax / tickSpacing) * tickSpacing;
        double numberX = niceMin;
        
        this.axisMarks.clear();
        while(numberX<=this.axisMax){
            if(numberX>=this.axisMin&&numberX<=this.axisMax){
                this.axisMarks.add(numberX);
            }
            numberX += tickSpacing;
        }
    }
     /**
     * Returns a "nice" number approximately equal to range Rounds
     * the number if round = true Takes the ceiling if round = false.
     *
     * @param range the data range
     * @param round whether to round the result
     * @return a "nice" number to be used for the data range
     */
    private double getNiceNumber(double range, boolean round) {
        double exponent; /** exponent of range */
        double fraction; /** fractional part of range */
        double niceFraction; /** nice, rounded fraction */
        
        exponent = Math.floor(Math.log10(range));
        fraction = range / Math.pow(10, exponent);
        
        if (round) {
            if (fraction < 1.5)
                niceFraction = 1;
            else if (fraction < 3)
                niceFraction = 2;
            else if (fraction < 7)
                niceFraction = 5;
            else
                niceFraction = 10;
        } else {
            if (fraction <= 1)
                niceFraction = 1;
            else if (fraction <= 2)
                niceFraction = 2;
            else if (fraction <= 5)
                niceFraction = 5;
            else
                niceFraction = 10;
        }
        
        return niceFraction * Math.pow(10, exponent);
    }
    
    /**
     * Sets Axis marks manually 
     * @param marks 
     */
    public void setAxisMarks(double[] marks){
        this.axisMarks.clear();
        for(int loop = 0; loop < marks.length; loop++){
            this.axisMarks.add(marks[loop]);
        }
    }
    /**
     * Calculates 
     * @param array
     * @return 
     */
    public int getSignificantFigures(List<Double> array){
        if(array.size()<2) return 0;
        double min = array.get(0);
        double max = array.get(array.size()-1);
        double difference = max-min;
        int   placeOfDifference = (int) Math.floor(Math.log(difference) / Math.log(10));
        return -placeOfDifference;
    }
    
    public void updateStringFormat(){
        int sigfig = this.getSignificantFigures(axisMarks);     
        StringBuilder str = new StringBuilder();
        if(sigfig<0) sigfig = -1;
        str.append('%');
        str.append('.');
        str.append(sigfig+1);
        str.append('f');
        str.toString();
        this.textOutputFormat = str.toString();
    }
    /**
     * Printout of the graphics axis parameters
     */
    public void show(){
        System.out.println(String.format("Graphics Axis = (%.5f %.5f)", this.axisMin,this.axisMax));
        int sigfig = this.getSignificantFigures(this.axisMarks);
        System.out.println("SIGFIG = " + sigfig + "  " + this.textOutputFormat);
        for(int i = 0; i < this.axisMarks.size();i++){
            System.out.print(String.format(" %9.4f ",this.axisMarks.get(i)));
        }
        System.out.println();
    }
    
    public static void main(String[] args){
        GraphicsAxisDouble axis = new GraphicsAxisDouble(240,1700,24500);
        axis.show();
        axis.setMaxTicks(5);
        axis.show();
    }
}
