/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.root.fx.base;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.GridPane;

/**
 *
 * @author gavalian
 */
public class FXEmbeddedCanvas  extends Canvas {
    
    private long numberOfDraw      = 0;
    private long drawingTimeMs     = 0;
    private int  NUMBER_OF_COLUMNS = 1;
    private int  NUMBER_OF_ROWS    = 1;
    
    private List<FXEmbeddedPad>   canvasPads = new ArrayList<FXEmbeddedPad>();
    
    public FXEmbeddedCanvas(){
        this.divide(1, 1);
        this.widthProperty().addListener(observable -> update());
        this.heightProperty().addListener(observable -> update());
    }
    
    public FXEmbeddedCanvas(int col, int row){
        this.divide(col,row);
        this.widthProperty().addListener(observable -> update());
        this.heightProperty().addListener(observable -> update());
    }
    
    public final void divide(int columns, int rows){
        this.NUMBER_OF_COLUMNS = columns;
        this.NUMBER_OF_ROWS    = rows;
        canvasPads.clear();
        for(int i = 0; i < columns*rows; i++){
            canvasPads.add(new FXEmbeddedPad());
        }
        update();
    }
    
    public final void update(){
        double w = this.getWidth();
        double h = this.getHeight();
        double offsetX = w/(this.NUMBER_OF_COLUMNS);
        double offsetY = h/(this.NUMBER_OF_ROWS);
        int index = 0;
        //System.out.println(" drawing -> ");
        for(int r = 0; r < this.NUMBER_OF_ROWS; r++){
            for(int c = 0; c < this.NUMBER_OF_COLUMNS; c++){
                /*canvasPads.get(index).getRegion().set(
                        c*offsetX, r*offsetY,offsetX,offsetY
                );*/
                canvasPads.get(index).getPadRegion().set(
                        c*offsetX, (c+1)*offsetX,
                        r*offsetY, (r+1)*offsetY
                        );                
                index++;
            }
        }
        repaint();
    }
    
    public FXEmbeddedPad getPad(int index){
        return this.canvasPads.get(index);
    }
    
    public void repaint(){
        
        GraphicsContext gc = this.getGraphicsContext2D();
        long st_ = System.currentTimeMillis();
        for(FXEmbeddedPad pad : this.canvasPads){
            this.numberOfDraw++;
            pad.draw(gc);
        }
        long et_ = System.currentTimeMillis();
        this.drawingTimeMs += (et_-st_);
        
        if(numberOfDraw %100==0){
            double msPerDraw = ( (double) drawingTimeMs)/numberOfDraw;
            System.out.println("NUMBER OF REDRAW = " + numberOfDraw + "  Time = "
            + drawingTimeMs + "  Time per re-draw = " + msPerDraw + " ms");
        }
    }
    
    private void initMouseListeners(){
        
    }
}
