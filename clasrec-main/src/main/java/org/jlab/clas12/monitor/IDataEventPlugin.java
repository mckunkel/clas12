/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.clas12.monitor;

import java.util.List;
import org.jlab.data.io.DataEventStore;
import org.root.base.IDataSet;
import org.root.pad.TEmbeddedCanvas;

/**
 *
 * @author gavalian
 */
public interface IDataEventPlugin {
    String   getName();
    String   getDescripton();
    void     process(DataEventStore store);
    void     init();
    void     reset();
    void     draw(TEmbeddedCanvas canvas);
    List<IDataSet>  getDataSets();
}
