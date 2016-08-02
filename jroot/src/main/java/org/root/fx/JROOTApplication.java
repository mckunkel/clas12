/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.root.fx;

import java.awt.Dimension;
import java.util.Map;
import java.util.TreeMap;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingNode;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.root.basic.EmbeddedCanvas;
import org.root.data.NTuple;
import org.root.histogram.H1D;

/**
 *
 * @author gavalian
 */
public class JROOTApplication extends Application {
    StackPane  canvasPane = new StackPane();
    SplitPane  mainSplitPane = null;
    SplitPane  consoleSplitPane = null;
    ToolBar    toolBar          = null;
    NTuple     ntuple           = null;
    TreeView   treeView   = new TreeView();
    Image      leafImage  = null;
    //final SwingNode swingNode = new SwingNode();
    EmbeddedCanvas  embeddedCanvas = new EmbeddedCanvas(800,650);
    
    JROOTInterpreter jrootInterpreter = new JROOTInterpreter();
    /**
     * This section is for upper trees
     */
    TabPane   appTabPane    = null;  
    Map<String,ITreeProvider> treeProviders  = new TreeMap<String,ITreeProvider>();
    Map<String,TreeView>           treeViews = new TreeMap<String,TreeView>();
    /**
     * Tab pane for storing canvases.
     */
    TabPane   canvasTabPane = null;
    Map<String,EmbeddedCanvas>  canvases = new TreeMap<String,EmbeddedCanvas>();
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        this.leafImage = new Image(getClass().getResourceAsStream("/icons/tea-plant-leaf-icon.png"));
        
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10,10,10,10));
        
        this.mainSplitPane = new SplitPane();
        
        JROOTConsole console = new JROOTConsole();
        console.setInterpreter(jrootInterpreter);
        
        this.consoleSplitPane = new SplitPane();
        this.consoleSplitPane.setOrientation(Orientation.VERTICAL);
        this.embeddedCanvas.setPreferredSize(new Dimension(800,600));
        VBox  container = new VBox();
        //this.swingNode.setContent(embeddedCanvas);
        
        container.getChildren().add(console.getPane());
        container.getChildren().add(treeView);
        
        this.appTabPane = new TabPane();
        this.canvasTabPane = new TabPane();
        
        this.consoleSplitPane.getItems().addAll(this.appTabPane,console.getPane());
        //swingNode.resize(800, 600);
        this.mainSplitPane.getItems().addAll(this.consoleSplitPane,this.canvasTabPane);
        this.mainSplitPane.setDividerPositions( 0.35f);        
        this.consoleSplitPane.setDividerPositions( 0.7f);
        //root.setLeft(container);
        root.setCenter(this.mainSplitPane);
        this.initToolBar();
        root.setTop(toolBar);
        this.addTreeProvider(new Tuple());
        
        //this.open();
        this.addCanvas();
        this.addCanvas();
        this.addCanvas();
        Scene scene = new Scene(root, 1100, 850);
        
        primaryStage.setTitle("java-buddy.blogspot.com");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
            
    public void addCanvas(){
        Integer currentCanvas = this.canvases.size();
        currentCanvas++;
        String  canvasName    = "Canvas_" + currentCanvas.toString();
        SwingNode swing = new SwingNode();
        EmbeddedCanvas canvas = new EmbeddedCanvas();
        swing.setContent(canvas);
        this.canvases.put(canvasName, canvas);
        Tab canvasTab = new Tab();
        canvasTab.setText(canvasName);
        canvasTab.setContent(swing);
        this.canvasTabPane.getTabs().add(canvasTab);
        H1D h = new H1D("h",100,1.2,1.8);
        canvas.draw(h);
        //this.treeProviders.get(0).draw(canvas, canvasName);
    }
    
    public void addTreeProvider(ITreeProvider provider){        
        this.treeProviders.put(provider.getName(), provider);
        TreeView  tv = new TreeView(provider.getTree());
        tv.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if(mouseEvent.getClickCount() == 2){
                        processDoubleClick();
                    }
                }
            }
        });
        
        this.treeViews.put(provider.getName(), tv);
        Tab  tb = new Tab();
        tv.setPadding(new Insets(10,10,10,10));
        tb.setText(provider.getName());
        tb.setContent(tv);
        this.appTabPane.getTabs().add(tb);
    }
    
    public void initToolBar(){
        this.toolBar = new ToolBar();
        ComboBox  canvasZone = new ComboBox();
        canvasZone.getItems().addAll((Object[]) new String[]{"1x1","1x2","1x3","2x2"});
        Button btOpen = new Button("Open");
        toolBar.getItems().addAll(
                new Separator(),
                btOpen,
                new Separator(),
                canvasZone
        );
        //btOpen.setOnAction(e -> {this.treeProviders.get(0).draw(, STYLESHEET_MODENA);});
    }
    public void processDoubleClick(){
        String tabName = this.appTabPane.getSelectionModel().getSelectedItem().getText();

        String item = ((TreeItem<String>) this.treeViews.get(tabName).getSelectionModel().getSelectedItem()).getValue();
        System.out.println("selected tab = " + tabName + " item = " + item);
    }
    public void open(){
        this.ntuple = new NTuple("T",12);
        this.ntuple.getVariables();
        TreeItem<String> rootItem = new TreeItem("T");
        for(String item : this.ntuple.getVariables()){
            TreeItem leaf = new TreeItem(item,new ImageView(this.leafImage));
            rootItem.getChildren().add(leaf);
        }
        this.treeView.setRoot(rootItem);
        this.treeView.getRoot().setExpanded(true);
    }
    
    public static void main(String[] args){
        launch(args);
    }
}
