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
public class Region2D {
    
    private static double SMALL_NUMBER = 10e-9;
    
    double regionX      = 0.0;
    double regionY      = 0.0;
    double regionWidth  = 0.0;
    double regionHeight = 0.0;
    
    double regionXscaleMin = 0.0;
    double regionXscaleMax = 1.0;
    double regionYscaleMin = 0.0;
    double regionYscaleMax = 1.0;
    
    boolean isLogX         = false;
    boolean isLogY         = false;
    
    public Region2D(){
        
    }
    
    public Region2D(double x, double y, double w, double h){
        regionX = x; regionY = y;
        regionWidth = w; regionHeight = h;
    }
    
    public double x(){ return regionX;}
    public double y(){ return regionY;}
    public double width(){ return regionWidth;}
    public double height(){ return regionHeight;}
    public double maxX(){ return regionX + regionWidth;}
    public double maxY(){ return regionY + regionHeight;}
    
    public boolean containsX(double x){
        return (x>=regionX&&x<=(regionX+regionWidth));
    }
    
    public void copy(Region2D region){
        this.set(region.x(), region.y(), region.width(), region.height());
    }
    
    public boolean containsY(double y){
        return (y>=regionY&&y<=(regionY+regionHeight));
    }
    
    public void setLogX(boolean flag){ 
        System.out.println("Setting Axis X log to = " + flag);
        this.isLogX = flag;
    }
    public void setLogY(boolean flag){ this.isLogY = flag;}
    public boolean getLogY(){ return this.isLogY; }
    public boolean getLogX(){ return this.isLogX; }
    
    
    public void setScaleX(double min, double max){
        this.regionXscaleMin = min;
        this.regionXscaleMax = max;
    }
    
    public void setScaleY(double min, double max){
        this.regionYscaleMin = min;
        this.regionYscaleMax = max;
    }
    
    public double getFractionX(double xcoord){
        double min = this.regionX + this.regionWidth*this.regionXscaleMin;
        double max = this.regionX + this.regionWidth*this.regionXscaleMax;
        return (xcoord-min)/(max-min);        
    }
    
    public double getFractionY(double ycoord){
        double min = this.regionY + this.regionHeight*this.regionYscaleMin;
        double max = this.regionY + this.regionHeight*this.regionYscaleMax;
                
        if(isLogY==false) return (ycoord-min)/(max-min);
                
        if(min<SMALL_NUMBER) min = SMALL_NUMBER;
        double logFraction = (Math.log10(ycoord)-Math.log10(min))/(Math.log10(max-min));
        double nfraction = (ycoord-min)/(max-min);
        System.out.println("min/max " + min + " " + max + " " + 
                Math.log10(min) + "  " + Math.log10(max));
        System.out.println("VALUE RANGE " + Math.log10(ycoord) + " " + + Math.log10(max-min)); 
        System.out.println("calculating log before " + nfraction + "  log " + logFraction );
        return (Math.log10(ycoord)-Math.log10(min))/(Math.log10(max-min));
        
    }
    /**
     * returns an X coordinate given the fraction of the range in X
     * @param fractionX
     * @return 
     */
    public double getCoordinateX(double fractionX){
        //double min = regionX + this.regionXscaleMin*regionWidth;
        //double max = regionX + this.regionXscaleMax*regionWidth;
        //return regionX + fractionX*(max-min);
        return regionX + fractionX*regionWidth;
    }
    /**
     * returns an Y coordinate given the fraction of the range in Y
     * @param fractionY
     * @return 
     */
    public double getCoordinateY(double fractionY){
        return regionY + fractionY*regionHeight;
    }
    /**
     * set the region coordinates (X,Y) and the region size (W,H)
     * @param x
     * @param y
     * @param w
     * @param h 
     */
    public final void set(double x, double y, double w, double h){
        regionX = x; regionY = y;
        regionWidth = w; regionHeight = h;
    }
    /**
     * grows the region in direction X to include the point X
     * @param x 
     */
    public void growX(double x){
        if(x>maxX()){
            regionWidth = x - regionX;
        } else {
            if(x<regionX){
                regionWidth = regionWidth + (regionX-x);
                regionX = x;
            }
        }
    }
    /**
     * grows the region in direction of Y to include the point Y
     * @param y 
     */
    public void growY(double y){
        if(y>maxY()){
            regionHeight = y - regionY;
        } else {
            if(y<regionY){
                regionHeight = regionHeight + (regionY-y);
                regionY = y;
            }
        }
    }
    /**
     * Grows region to include point (x,y)
     * @param x
     * @param y 
     */
    public void grow(double x, double y){
        growX(x);
        growY(y);
    }
    
    public void reset(){
        this.regionXscaleMin = 0.0;
        this.regionXscaleMax = 1.0;
        this.regionYscaleMin = 0.0;
        this.regionYscaleMax = 1.0;
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append(String.format("[%.2f %.2f %.2f %.2f]", x(),y(),width(),height()));
        return str.toString();
    }
}
