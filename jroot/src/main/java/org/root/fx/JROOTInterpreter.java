/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.root.fx;

import java.util.ArrayList;
import java.util.List;
import org.root.group.TDirectory;

/**
 *
 * @author gavalian
 */
public class JROOTInterpreter implements IConsoleInterpreter {
    
    String  message = "";
    TDirectory  directory = new TDirectory();
    
    @Override
    public int parseCommand(String command) {
        this.message = "> command executed successfuly\n";
        return 0;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
    
    public class Command {
        
        String  object = "";
        String  method = "";
        List<String>  arguments = new ArrayList<String>();
        List<String>  argTypes  = new ArrayList<String>();
        
        public Command(){
            
        }
        
        
    }
}
