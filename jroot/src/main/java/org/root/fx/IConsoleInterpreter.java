/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.root.fx;

/**
 *
 * @author gavalian
 */
public interface IConsoleInterpreter {
    int      parseCommand(String command);
    String   getMessage();
}
