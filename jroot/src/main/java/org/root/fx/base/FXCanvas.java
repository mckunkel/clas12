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
public class FXCanvas {
    
    public FXCanvas(){
        
    }
    
    public void draw(String h){
        System.out.println("drawing ---> " + h);
        GraphicsCanvasApplication.getInstance();
    }
    
    public void init(){
        new Thread() {
            @Override
            public void run() {
                GraphicsCanvasApplication.launch(GraphicsCanvasApplication.class);
            }
        }.start();

    }
    
    
}
