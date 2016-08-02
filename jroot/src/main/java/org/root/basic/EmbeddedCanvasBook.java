/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.root.basic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author gavalian
 */
public class EmbeddedCanvasBook extends JPanel implements ActionListener {
    
    List<EmbeddedPad>   bookPads   = new ArrayList<EmbeddedPad>();
    EmbeddedCanvas      bookCanavs = null;
    
    public EmbeddedCanvasBook(int xsize, int ysize){
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
    }
    
    
}
