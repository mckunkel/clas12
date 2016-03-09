/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.root.basic;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class GraphicsAxisNumber extends GraphicsAxis {

    private int axisMaxTicks = 10;
    private double axisMin   = 0.0;
    private double axisMax   = 1.0;
    private String textOutputFormat = "%.1f";
    
    public GraphicsAxisNumber(){
        super();
        this.setMinMax(axisMin,axisMax);
        this.updateTicks();        
    }
    
    public GraphicsAxisNumber(double min, double max){
        super();
        this.axisMin = min;
        this.axisMax = max;
        this.setMinMax(axisMin,axisMax);
        this.updateTicks();        
    }
    
    public void setRange(double min, double max){
        this.axisMin = min;
        this.axisMax = max;
        this.setMinMax(min, max);
        this.updateTicks();
    }
    
    @Override
    public void update(Graphics2D g2d, int width, int height){
        FontMetrics fm = g2d.getFontMetrics(this.getAxisFont());
        this.axisMaxTicks = 10;
        this.updateTicks();
        double fraction = this.axisLabelFraction(fm);
        double fractionLimit = 0.7;
        if(this.isVertical()==true){
            fractionLimit = 0.95;
        }
        //System.out.println(" FRACTION =  " + this.isVertical() + "  " + fraction);
        while(fraction>fractionLimit&&this.axisMaxTicks>2){
            this.axisMaxTicks--;
            fraction = this.axisLabelFraction(fm);
            //System.out.println(" FRACTION INSIDE = " + fraction + "  MAX TICKS = " + this.axisMaxTicks);
            //this.show();
            this.updateTicks();
        }                
    }
    
    private double axisLabelFraction(FontMetrics fm){
        double length     = getLength();
        double fontLength = 0.0;
        for(String str : this.getAxisMarksString()){
            if(this.isVertical()){
                fontLength += (double) fm.getHeight();
            } else {
                fontLength += (double) fm.stringWidth(str);
            }
        }
        return (fontLength/length);
    }
    
    public final void updateTicks(){
        
        double range = this.getNiceNumber(axisMax - axisMin, false);
        double tickSpacing = this.getNiceNumber(range / (axisMaxTicks - 1), true);
        double niceMin =
                Math.floor(axisMin / tickSpacing) * tickSpacing;
        double niceMax =
                Math.ceil(axisMax / tickSpacing) * tickSpacing;
        double numberX = niceMin;        
        
        this.getAxisMarks().clear();
        while(numberX<=this.axisMax){
            if(numberX>=this.axisMin&&numberX<=this.axisMax){
                this.getAxisMarks().add(numberX);                
            }
            numberX += tickSpacing;
        }
        
        this.updateStringFormat();
        this.getAxisMarksString().clear();
        for(Double num : this.getAxisMarks()){
            this.getAxisMarksString().add(String.format(textOutputFormat, num));
        }
    }
    
    public void updateStringFormat(){
        int sigfig = this.getSignificantFigures(this.getAxisMarks());
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
}
