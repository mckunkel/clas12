/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.root.basic;


import java.awt.BorderLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author gavalian
 */
public class EmbeddedCanvasTab extends JPanel {
    JTabbedPane tabbedPane = new JTabbedPane();
    Map<String,EmbeddedCanvas>  embeddedCanvases = new LinkedHashMap<String,EmbeddedCanvas>();
    
    public EmbeddedCanvasTab(){
        //tabbedPane.add("test",new JButton("hello"));
        this.setLayout(new BorderLayout());
        this.add(tabbedPane,BorderLayout.CENTER);
    }
    
    public void addCanvas(String name){
        addCanvas(name,1,1);
    }
    
    public void addCanvas(String name, int cols, int rows){
        EmbeddedCanvas  canvas = new EmbeddedCanvas(600,600,cols,rows);
        //canvas.divide(cols,rows);
        embeddedCanvases.put(name, canvas);
        tabbedPane.add(name, canvas);
    }
    
    public EmbeddedCanvas getCanvas(String name){
        return this.embeddedCanvases.get(name);
    }
    
    public static void main(String[] args){
        JFrame frame = new JFrame();
        EmbeddedCanvasTab tab = new EmbeddedCanvasTab();
        tab.addCanvas("FTOF",2,2);
        frame.add(tab);
        frame.pack();
        frame.setVisible(true);
    }
}
