/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.basic;

import org.root.pad.TEmbeddedCanvas;

/**
 *
 * @author gavalian
 */
public interface IDetectorDisplay {
    String[]  getOptions();
    void draw(TEmbeddedCanvas canvas, int sector, int layer, int component, String option);
}
