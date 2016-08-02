/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clasrec.ui;

import org.root.pad.TEmbeddedCanvas;

/**
 *
 * @author gavalian
 */
public interface IDetectorHistogramDraw {
    void drawComponent(int sector, int layer, int component, TEmbeddedCanvas canvas);
    void drawLayer(int sector, int layer, TEmbeddedCanvas canvas);
    void drawSector(int sector, TEmbeddedCanvas canvas);
}
