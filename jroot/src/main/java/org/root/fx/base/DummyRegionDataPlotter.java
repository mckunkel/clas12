/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.root.fx.base;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author gavalian
 */
public class DummyRegionDataPlotter implements IRegionDataPlotter {

    Dimension2D  dataRegion = new Dimension2D();
    
    double[]  x = null;
    double[]  y = null;
    
    public DummyRegionDataPlotter(int count){
        x = new double[count];
        y = new double[count];
        double step = 0.1;
        for(int i = 0; i < count; i++){
            x[i] = 0.0 + i*step;
            y[i] = Math.cos(x[i]);
        }
        dataRegion.set(0.0, step*count,-1.2, 1.2);
    }
    
    @Override
    public void draw(GraphicsContext gc, Dimension2D axisRegion, Dimension2D dataRegion) {
        //System.out.println("[plotting] --> draing dat on the pad");
        //System.out.println(" SCREEN = " + screenRegion);
        //System.out.println(" DATA   = " + dataRegion);
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
        for(int i = 0; i < x.length; i++){
            //double xv = screenRegion.getCoordinateX(dataRegion.getFractionX(x[i]));
            //double yv = screenRegion.getCoordinateY(dataRegion.getFractionY(y[i]));
            /*
            System.out.println(" point " + i + "  " + xv + " " + yv
                    + "  " + dataRegion.getFractionX(x[i]) 
                    + "  " + dataRegion.getFractionY(y[i]) 
                    + "  " + x[i] 
                    + "  " + y[i]                     
            );*/
            Point2D point = axisRegion.getPoint(x[i], y[i], dataRegion);
            gc.fillOval(point.getX(), point.getY(), 5.0, 5.0);
        }
        
    }

    @Override
    public Dimension2D getDataRegion() {
        return this.dataRegion;
    }
    
}
