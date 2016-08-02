/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.root.fx.base;

import javafx.scene.canvas.GraphicsContext;

/**
 *
 * @author gavalian
 */
public interface IDataSetPlotter {
    void draw(GraphicsContext gc, GraphicsFrame frame, IDataSetFX ds);
}
