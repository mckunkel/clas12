/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.root.fx;

import javafx.scene.control.TreeItem;
import org.root.basic.EmbeddedCanvas;
import org.root.histogram.H1D;

/**
 *
 * @author gavalian
 */
public class Tuple implements ITreeProvider {
    String tupleName = "Tuple";
    
    @Override
    public String getName() {
        return this.tupleName;
    }

    @Override
    public TreeItem getTree() {
        TreeItem<String> root = new TreeItem<String>(this.tupleName);
        root.getChildren().add(new TreeItem<String>("a"));
        root.getChildren().add(new TreeItem<String>("b"));
        root.getChildren().add(new TreeItem<String>("c"));
        return root;
    }

    @Override
    public void draw(EmbeddedCanvas canvas, String item) {
        H1D h1 = new H1D("h2",100,0.2,0.4);
        canvas.draw(h1);
    }

    @Override
    public void setSource(String filename) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setName(String name) {
        this.tupleName = name;
    }
    
}
