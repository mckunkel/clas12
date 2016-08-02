/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.root.fx.base;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author gavalian
 */
public class GraphicsFrame {
    
    Region2D  canvasFrame     = new Region2D(); // This is the dimensions of the Graphics Frame
    Region2D  graphFrame      = new Region2D(); // This is the dimensions of the inside of the frame
    Region2D  dataRegion      = new Region2D(); // region of the data that is represented by the graphs
    Region2D  dataRegionScale = new Region2D(); // subset of the the data region
    
    Color     colorBackground      = Color.WHITE;
    Color     colorFrameBackground = Color.ANTIQUEWHITE;
    Color     colorFrameLine       = Color.BLACK;
    
    Region2D   axisZoom              = new Region2D();
    
    boolean    axisZoomInProgressX   = false;
    boolean    axisZoomInProgressY   = false;
    boolean    axisZoomEnabledX      = true;
    boolean    axisZoomEnabledY      = true;
    boolean    axisXdrawGrid         = false;
    boolean    axisYdrawGrid         = false;
    
    double[] xpoints = new double[]{2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0};
    //double[] ypoints = new double[]{3.0,4.0,5.0,6.0,5.0,4.0,3.0,2.0,1.5};
    double[] ypoints = new double[]{2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,1.5};
    
    
    private List<IGraphCanvasObject>   canvasObjects = new ArrayList<IGraphCanvasObject>();
    /**
     * initializes the Graphics canvas with size given.
     * @param xsize
     * @param ysize 
     */
    public GraphicsFrame(double xsize, double ysize){
        resize(xsize,ysize);
        dataRegion.set(1.0, 1.0, 12.0, 10.0);
        this.setScaleX(0.0, 1.0);
        this.setScaleY(0.0, 1.0);
    }
    
    public GraphicsFrame(double x, double y,double xsize, double ysize){
        resize(x,y,xsize,ysize);
        dataRegion.set(1.0, 1.0, 12.0, 10.0);
        this.setScaleX(0.0, 1.0);
        this.setScaleY(0.0, 1.0);
    }
    /**
     * resize the canvas
     * @param x0
     * @param y0
     * @param xsize
     * @param ysize 
     */
    public final void resize(double x0, double y0,double xsize, double ysize){
        canvasFrame.set(x0, y0, xsize, ysize);
        graphFrame.set(x0+25.0, y0+25.0, xsize-50.0, ysize-50.0);
    }
    
    public final void resize(double xsize, double ysize){
        canvasFrame.set(0.0, 0.0, xsize, ysize);
        graphFrame.set(25.0, 25.0, xsize-50.0, ysize-50.0);
    }
    
    public void draw(GraphicsContext gc){
        
        
        gc.clearRect(canvasFrame.x(), canvasFrame.y(), 
                canvasFrame.width(), canvasFrame.height());
        gc.setLineDashes(new double[]{4,5,1});        
        gc.setFill(this.colorBackground);
        gc.fillRect(canvasFrame.x(), canvasFrame.y(),
                canvasFrame.width(),canvasFrame.height());
        gc.setFill(this.colorFrameBackground);
        gc.fillRect(graphFrame.x(),graphFrame.y(),
                graphFrame.width(),graphFrame.height());
        gc.setStroke(colorFrameLine);
        gc.strokeRect(graphFrame.x(),graphFrame.y(),
                graphFrame.width(),graphFrame.height());
        
        //(this.graphFrame.x(), this.graphFrame.y(),
        //        this.graphFrame.width(),this.graphFrame.height());
        //gc.beginPath();
        //gc.moveTo(graphFrame.x(),graphFrame.y());
        //gc.lineTo(graphFrame.x()+graphFrame.width(), graphFrame.y());
        //gc.lineTo(graphFrame.x()+graphFrame.width(), graphFrame.y() + graphFrame.height());
        //gc.lineTo(graphFrame.x(), graphFrame.y() + graphFrame.height());
        gc.save();
        gc.beginPath();
        //gc.rect(graphFrame.x(),graphFrame.y(),500,500);
        gc.rect(graphFrame.x(),graphFrame.y(),graphFrame.width(),graphFrame.height());
        gc.closePath();
        gc.clip();
        //gc.rect(graphFrame.x(),graphFrame.y(),graphFrame.width(),graphFrame.height());
        //System.out.println("--->  " + graphFrame.width() + "  " + graphFrame.height());
        //gc.closePath();
        //gc.clip();

        gc.setFill(Color.RED);
        gc.setStroke(Color.RED);
        gc.setLineWidth(2.0);
        for(int i = 0; i < xpoints.length; i++){
            double xp = this.getDataPointX(xpoints[i]);
            double yp = this.getDataPointY(ypoints[i]);
            gc.fillOval(xp-5, yp-5, 10, 10);
            if(i!=0){
                //System.out.println("--> draw line");
                gc.strokeLine(
                        this.getDataPointX(xpoints[i-1]), 
                        this.getDataPointY(ypoints[i-1]), 
                        this.getDataPointX(xpoints[i]), 
                        this.getDataPointY(ypoints[i]));
            }
        }
        gc.restore();
        //gc.clearRect(canvasFrame.x(), canvasFrame.y(), 
        //        canvasFrame.width(), canvasFrame.height());
        gc.setFill(Color.BLACK);
        gc.fillText(this.debugLine(), 5, 15);
        
        if(this.axisZoomInProgressX==true||this.axisZoomInProgressY==true){
            gc.setFill(Color.rgb(80, 35, 190, 0.4));
            gc.fillRect(axisZoom.x(), axisZoom.y(), axisZoom.width(), axisZoom.height());
        }
    }
    
    public String debugLine(){
        StringBuilder str = new StringBuilder();
        str.append(String.format("FRAME %s", this.graphFrame.toString()));
        str.append(String.format(" DATA %s",this.dataRegion.toString()));
        str.append(String.format(" SCALE %s",this.dataRegionScale.toString()));
        return str.toString();
    }
    
    public double getDataPointY(double y){
        //double yp = this.dataRegion.getFractionY(y);
        //double ymin = this.dataRegion.y() + this.dataRegionScale.y()*this.dataRegion.height();
        //double ymax = this.dataRegion.y() + dataRegionScale.maxY()*this.dataRegion.height();        
        //double yp = (y - ymin)/(ymax-ymin);
        double yf = this.dataRegion.getFractionY(y);
        return this.graphFrame.getCoordinateY(yf);
    }
    
    public double getDataPointX(double x){        
        //double xmin = this.dataRegion.x() + this.dataRegionScale.x()*this.dataRegion.width();
        //double xmax = this.dataRegion.x() + dataRegionScale.maxX()*this.dataRegion.width();        
        //double xp = (x - xmin)/(xmax-xmin);
        double xf = this.dataRegion.getFractionX(x);
        return this.graphFrame.getCoordinateX(xf);
    }
    
    public final void setScaleX(double xmin, double xmax){
        this.dataRegion.setScaleX(xmin, xmax);
        //this.dataRegionScale.set(xmin, 
        //        dataRegion.y(), xmax-xmin, dataRegionScale.height());
    }
    
    public final void setScaleY(double ymin, double ymax){
        this.dataRegion.setScaleY(ymin, ymax);
        //this.dataRegionScale.set( 
        //        dataRegionScale.x(), ymin, dataRegionScale.width(),ymax-ymin);
    }
    
    public Region2D  getDataFrame(){
        return this.dataRegion;
    }
    
    public Region2D getDataScale(){
        return this.dataRegionScale;
    }
    
    public void update(){
        
    }
    
    public void setLogX(boolean flag){        
        this.dataRegion.setLogX(flag);
    }
    
    public void setLogY(boolean flag){
        this.dataRegion.setLogY(flag);
    }
    
    public boolean getLogX(){
        return this.dataRegion.getLogX();
    }
    
    public boolean getLogY(){
        return this.dataRegion.getLogY();
    }
    
    public void setAxisZoom(boolean flag){
        this.axisZoomEnabledX = flag;
        this.axisZoomEnabledY = flag;
    }
    
    public void controlsMousePressed(double x, double y){

        if(y>this.graphFrame.maxY()&&x>this.graphFrame.x()&&this.axisZoomEnabledX==true){
            System.out.println("mouse clicked at " + x + " " + y + this.graphFrame.maxY());
            this.axisZoomInProgressX = true;
            this.axisZoom.set(x, this.graphFrame.y(), 0.0, this.graphFrame.height());
        }

        if(x<this.graphFrame.x()&&this.axisZoomEnabledY==true){
            this.axisZoomInProgressY = true;
            this.axisZoom.set(this.graphFrame.x(), y, this.graphFrame.width(), 0.0);
        }
        
    }
    
    public void controlsMouseDragged(double x, double y){
        //System.out.println("mouse dragged at " + x + " " + y);
        if(this.axisZoomInProgressX==true){
            double xdistance = x - this.axisZoom.x();
            if(xdistance>=0){
                this.axisZoom.set(axisZoom.x(), axisZoom.y(), xdistance, axisZoom.height());
            } else {
                this.axisZoom.set(x, axisZoom.y(), -xdistance, axisZoom.height());
            }
        }
        
        if(this.axisZoomInProgressY==true){
            double ydistance = y - this.axisZoom.y();
            if(ydistance>=0){
                this.axisZoom.set(axisZoom.x(), axisZoom.y(), axisZoom.width(), 
                        ydistance);
            } else {
                this.axisZoom.set(x, axisZoom.y(), -ydistance, axisZoom.height());
            }
        }
        
    }
    
    public void controlsMouseReleased(double x, double y){

        if(this.axisZoomInProgressX==true){
            this.axisZoomInProgressX = false;
            if(this.axisZoom.width()>5){
                double  fractionXmin = this.graphFrame.getFractionX(axisZoom.x());
                double  fractionXmax = this.graphFrame.getFractionX(axisZoom.maxX());
                System.out.println("mouse released at " + x + " " + y + " / " + 
                        fractionXmin + " " + fractionXmax);
                this.setScaleX(fractionXmin, fractionXmax);
            }
        }
        
        if(this.axisZoomInProgressY==true){
            this.axisZoomInProgressY = false;
            if(this.axisZoom.height()>5){
                double  fractionYmin = this.graphFrame.getFractionY(axisZoom.y());
                double  fractionYmax = this.graphFrame.getFractionY(axisZoom.maxY());
                this.setScaleY(fractionYmin, fractionYmax);
            }
        }
    }
}
