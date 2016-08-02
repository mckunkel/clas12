/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.root.fx.base;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import org.root.histogram.H1D;
import org.root.utils.DataFactory;

/**
 *
 * @author gavalian
 */
public class GraphicsCanvasApplication extends Application {
    
    private static GraphicsCanvasApplication appCanvas = null;
    FXEmbeddedCanvas canvas = null;
    
     @Override
    public void start(Stage primaryStage) throws Exception {
        
        
        BorderPane  root = new BorderPane();
        
        
        BorderPane  canvasPane = new BorderPane();
        addCanvas(canvasPane);
        
        //StackPane  root = new StackPane();
        //GridPane    grid = new GridPane();
        //GraphicsDataPad  canvas = new GraphicsDataPad();
        //canvas.graphicsFrame.setScaleX(0.0, 1.5);
        //canvas.graphicsFrame.setScaleY(0.2, 1.2);
        //canvas.getGraphicsFrame().setAxisZoom(false);
        //FXEmbeddedCanvas canvas = new FXEmbeddedCanvas(2,2);
        //root.setCenter(canvas);
        //root.getChildren().add(canvas);
        //Canvas c1 = new Canvas();
        //root.setCenter(c1);
        //canvas.getPad(0).addDataPlotter(new DummyRegionDataPlotter(120));
        //canvas.getPad(1).addDataPlotter(new DummyRegionDataPlotter(240));
        //canvas.getPad(1).getFrame().setScaleX(0.4, 0.6);
        //canvas.widthProperty().bind();
        //FXEmbeddedCanvas  emb = new FXEmbeddedCanvas();        
        //canvas.widthProperty().bind(root.widthProperty());
        //canvas.heightProperty().bind(root.heightProperty());
        //emb.getPane().setPrefHeight(600);
        //emb.getPane().setPrefWidth(600);
        //root.setCenter(emb.getPane());
        //root.prefWidth(500);
        //root.prefHeight(350);
        
        Button save = new Button("Save");
        save.setOnAction(event -> saveCanvas());
        
        root.setTop(save);
        root.setCenter(canvasPane);
        
        Scene scene = new Scene(root, 500, 350, Color.rgb(255,255,255));
        
        //Scene scene = new Scene(canvas, 500, 350, Color.rgb(255,255,255));
        
        primaryStage.setScene(scene);
        primaryStage.show();
        //appCanvas = this;
    }
    
    public void addCanvas(BorderPane pane){
        canvas = new FXEmbeddedCanvas(1,1);
        pane.setCenter(canvas);
        //canvas.getPad(2).addDataPlotter(new DummyRegionDataPlotter(120));
        //canvas.getPad(1).addDataPlotter(new DummyRegionDataPlotter(840));
        
        H1D h1 = new H1D("h1",120,0.0,5.0);
        DataFactory.createSampleH1D(h1, 2400, 2.4);
        
        canvas.getPad(0).addDataPlotter(new GraphErrorsDataPlotter1D(h1,""));
        
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
    }
    
    
    public void saveCanvas(){
        File file = new File("/Users/gavalian/canvas_c1.png");
        try {
            WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
            canvas.snapshot(null, writableImage);
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
            ImageIO.write(renderedImage, "png", file);
        } catch (IOException ex) {
            System.out.println("something went wrong");
        }
    }
    
    public static GraphicsCanvasApplication getInstance(){
        return appCanvas;
    }
    
    public static void main(String[] args){
        launch();
    }
}
