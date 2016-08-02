/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.root.attr;

/**
 *
 * @author gavalian
 */
public enum AttributeType {
    
    UNDEFINED   ( 0, "undefined"),
    LINECOLOR   ( 1, "LINECOLOR"),    
    LINEWIDTH   ( 2, "LINEWIDTH"),
    LINESTYLE   ( 3, "LINESTYLE"),
    MARKERCOLOR ( 4, "MARKERCOLOR"),
    MARKERSIZE  ( 5, "MARKERSIZE"),
    MARKERSTYLE ( 6, "MARKERSTYLE"),    
    FILLCOLOR   ( 7, "FILLCOLOR");
    
    private final int attributeID;
    private final String attributeName;
    
    AttributeType(int id, String name){
        this.attributeID = id;
        this.attributeName = name;
    }
    public String getName() {
        return attributeName;
    }
    public Integer getId() {
        return attributeID;
    }
    
}
