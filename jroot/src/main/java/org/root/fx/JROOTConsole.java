/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.root.fx;



import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import org.root.group.TDirectory;

/**
 *
 * @author gavalian
 */
public class JROOTConsole {
    List<String>  history = new ArrayList<String>();
    TDirectory    directory      = new TDirectory();
    TextArea            textArea = new TextArea();
    BorderPane          pane     = new BorderPane();
    protected final TextField textField = new TextField();
    protected int historyPointer = 0;
    IConsoleInterpreter   interpreter = null;
    
    public JROOTConsole(){
        pane.setPadding(new Insets(10,10,10,10));
        textArea.setEditable(false);
        this.textField.setStyle("-fx-font-size: 18");
        this.textArea.setStyle("-fx-font-size: 18");
        pane.setCenter(textArea);
        this.setEventHandler();
    }
    
    public void setInterpreter(IConsoleInterpreter ip){
        this.interpreter = ip;
    }
    
    public BorderPane getPane(){
        return this.pane;
    }
    
    public void setEventHandler(){
        
        textField.addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
            switch (keyEvent.getCode()) {
                case ENTER:
                    String text = textField.getText();
                    if(text.length()==0) break;
                    textArea.appendText(String.format("[%d] %s", this.historyPointer, text) + System.lineSeparator());
                    history.add(text);
                    historyPointer++;
                    textField.clear();
                    if(this.interpreter!=null){
                        this.interpreter.parseCommand(text);
                        this.textArea.appendText(this.interpreter.getMessage());
                    }
                    break;
                case UP:
                     
                    if (historyPointer == 0) {
                        break;
                    }
                    historyPointer--;
                    
                    textField.setText(history.get(historyPointer));
                    //textField.selectAll();                                        
                    break;
                case DOWN:
                    if (historyPointer == history.size() - 1) {
                        break;
                    }
                    
                    historyPointer++;                    
                    textField.setText(history.get(historyPointer));
                    //textField.selectAll();                    
                    break;
                    
                default:
                    break;
            }
        });
        this.pane.setBottom(textField);
    }
}
