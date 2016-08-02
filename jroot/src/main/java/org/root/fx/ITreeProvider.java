/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.root.fx;

import javafx.scene.control.TreeItem;
import org.root.basic.EmbeddedCanvas;

/**
 *
 * @author gavalian
 */
public interface ITreeProvider {
    
    String    getName();
    TreeItem  getTree();
    void      setName(String name);
    void      draw(EmbeddedCanvas canvas, String item);
    void      setSource(String filename);
}
