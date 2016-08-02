/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.root.fx.base;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author gavalian
 */
public class GraphicsDataPad extends Canvas {
    
    GraphicsFrame   graphicsFrame = new GraphicsFrame(100,100);
    ContextMenu     contextMenu   = null;
    
    public GraphicsDataPad(){
        
        this.widthProperty().addListener(observable -> update());
        this.heightProperty().addListener(observable -> update());
        this.initContextMenu();
        this.initMouseListeners();
        /*
        this.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("mouse event happened");
                if (event.isSecondaryButtonDown()) {
                    System.out.println("right button is clicked");
                    contextMenu.show( instance(),event.getScreenX(), event.getScreenY());
                }
            }
        });*/
    }
        
    public Canvas instance(){
        return this;
    }
    
    public final void update(){
        double w = this.getWidth();
        double h = this.getHeight();
        
        graphicsFrame.resize(w, h);
        GraphicsContext gc = this.getGraphicsContext2D();
        graphicsFrame.draw(gc);
    }
    
    public final void initContextMenu(){
        contextMenu = new ContextMenu();
        MenuItem unZoom = new MenuItem("unZoom");
        unZoom.setOnAction(event -> { 
            graphicsFrame.setScaleX(0.0, 1.0);
            graphicsFrame.setScaleY(0.0, 1.0);
            update();
        });
        
        MenuItem copy = new MenuItem("Copy");
        Menu     zooming     = new Menu("Axis Zooming");
        MenuItem zoomEnable  = new MenuItem("Enable");
        MenuItem zoomDisable = new MenuItem("Disable");
        
        zoomEnable.setOnAction(event -> { graphicsFrame.setAxisZoom(true);});
        zoomDisable.setOnAction(event -> { graphicsFrame.setAxisZoom(false);});
        zooming.getItems().addAll(zoomEnable,zoomDisable);
        
        Menu  logMenu = new Menu("Log");
        MenuItem logMenuX = new MenuItem("Log X");
        MenuItem logMenuY = new MenuItem("Log Y");
        MenuItem logMenuZ = new MenuItem("Log Z");
        
        logMenu.getItems().add(logMenuX);
        logMenu.getItems().add(logMenuY);
        logMenu.getItems().add(logMenuZ);
        
        logMenuX.setOnAction(event->{ 
            if(graphicsFrame.getLogX()==true){
                graphicsFrame.setLogX(false);
            } else { graphicsFrame.setLogX(true);}
            }
        );
        logMenuY.setOnAction(event->{ 
            if(graphicsFrame.getLogY()==true){
                graphicsFrame.setLogY(false);
            } else { graphicsFrame.setLogY(true);}
            update();
        }
        );
        contextMenu.getItems().add(unZoom);
        contextMenu.getItems().add(zooming);
        contextMenu.getItems().add(logMenu);
        
        contextMenu.getItems().add(copy);
    }
    
    public GraphicsFrame  getGraphicsFrame(){
        return this.graphicsFrame;
    }
    
    public final void initMouseListeners(){
        
        this.setOnMousePressed(event -> { 
            //System.out.println(" mouse clicked " + event.isPrimaryButtonDown());
            //if(event.isPrimaryButtonDown()){
            if(event.isSecondaryButtonDown()==true){
                contextMenu.show( instance(),event.getScreenX(), event.getScreenY());
            } else {
                System.out.println("---> mouse pressed at : " + String.format("%8.3f %8.3f",
                        event.getX(),event.getY()));
                graphicsFrame.controlsMousePressed(event.getX(),event.getY());
            }
            //}
        });
        this.setOnMouseDragged( event -> {
            graphicsFrame.controlsMouseDragged(event.getX(),event.getY());
            update();
        });
        this.setOnMouseReleased( event -> {
            graphicsFrame.controlsMouseReleased(event.getX(),event.getY());
            update();
        });
    }
}
