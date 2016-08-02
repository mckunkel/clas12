/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.root.fx.base;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.root.base.IDataSet;
import org.root.histogram.H1D;

/**
 *
 * @author gavalian
 */
public class GraphErrorsDataPlotter1D implements IRegionDataPlotter {
    
    private Dimension2D   histogramDataRange = new Dimension2D();
    private String        plottingOptions    = "";
    private IDataSet      histogram          = null;
    
    public GraphErrorsDataPlotter1D(IDataSet h, String options){
        this.histogram = h;
        this.plottingOptions = options;
        this.updateDataRegion();
    }
    
    @Override
    public void draw(GraphicsContext gc, Dimension2D axisRegion, Dimension2D dataRegion) {
        int npoints = this.histogram.getDataSize();
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);
        
        for(int loop = 0; loop < npoints; loop++){
            Point2D point = axisRegion.getPoint(histogram.getDataX(loop),
                    histogram.getDataY(loop),
                    dataRegion);
            Point2D pointErrorYup = axisRegion.getPoint(
                    histogram.getDataX(loop),
                    histogram.getDataY(loop) + histogram.getErrorY(loop),
                    dataRegion);
            Point2D pointErrorYdw = axisRegion.getPoint(
                    histogram.getDataX(loop),
                    histogram.getDataY(loop) - histogram.getErrorY(loop),
                    dataRegion);
            gc.strokeLine(
                    point.getX(),pointErrorYdw.getY(),
                    point.getX(),pointErrorYup.getY()
                    );
            gc.fillOval(point.getX()-3, point.getY()-3, 6, 6);
        }
    }
    
    private void updateDataRegion(){
        int npoints = this.histogram.getDataSize();
        if(npoints==0){
            this.histogramDataRange.set(0, 1, 0, 1);
        } else {
            this.histogramDataRange.set(
                    histogram.getDataX(0),histogram.getDataX(0),
                    histogram.getDataY(0),histogram.getDataY(0)
                    );
        }
        for(int loop = 0; loop < npoints; loop++){
            this.histogramDataRange.grow(
                    histogram.getDataX(loop)
                    ,histogram.getDataY(loop) + histogram.getErrorY(loop));
        }
        
        this.histogramDataRange.grow(
                histogramDataRange.getDimension(0).getMin(), 
                histogramDataRange.getDimension(1).getMax()
                + histogramDataRange.getDimension(1).getLength()*0.2
        );
    }
    
    @Override
    public Dimension2D getDataRegion() {
        this.updateDataRegion();
        return this.histogramDataRange;
    }
    
}
