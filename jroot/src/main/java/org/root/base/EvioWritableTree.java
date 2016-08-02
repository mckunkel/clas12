/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.root.base;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author gavalian
 */
public interface EvioWritableTree {
    
    String getName();
    Map<Integer,Object>  toTreeMap();
    void                     fromTreeMap(Map<Integer,Object> map);
}
