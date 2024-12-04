package com.sword.mymicrosobtpain;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;



public class Main extends Application {

    private Canvas canvas;
    private GraphicsContext gc;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("MyMicrosobtPain");

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 800, 600);


        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem loadItem = new MenuItem("Load");
        MenuItem saveItem = new MenuItem("Save");

        Menu colorMenu = new Menu("Colors");

        MenuItem blueButton = new MenuItem("Blue");
        MenuItem redButton = new MenuItem("Red");
        MenuItem blackButton = new MenuItem("Black");

        Menu canvasMenu = new Menu("Canvas");
        MenuItem clearCanvasItem = new MenuItem("Clear canvas");
        MenuItem undoStepItem = new MenuItem("Undo");

        fileMenu.getItems().addAll(loadItem, saveItem);
        menuBar.getMenus().add(fileMenu);
        canvasMenu.getItems().addAll(clearCanvasItem,undoStepItem);
        menuBar.getMenus().add(canvasMenu);
        colorMenu.getItems().addAll(blueButton,redButton,blackButton);
        menuBar.getMenus().add(colorMenu);

        canvas = new Canvas(800, 600);
        gc = canvas.getGraphicsContext2D();
        CanvasController canvasController = new CanvasController(canvas);


        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            canvasController.saveState();
            gc.beginPath();
            gc.moveTo(e.getX(), e.getY());
            gc.stroke();
        });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            gc.lineTo(e.getX(), e.getY());
            gc.stroke();
        });


        loadItem.setOnAction(e -> loadImage(primaryStage));
        saveItem.setOnAction(e -> saveImage(primaryStage));
        clearCanvasItem.setOnAction(e-> clearCanvas(canvasController));

        redButton.setOnAction(e-> canvasController.changeColor(Color.RED));
        blueButton.setOnAction(e-> canvasController.changeColor(Color.BLUE));
        blackButton.setOnAction(e-> canvasController.changeColor(Color.BLACK));
        undoStepItem.setOnAction(e-> canvasController.undo());

        root.setTop(menuBar);
        root.setCenter(canvas);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void clearCanvas(CanvasController canvasController){
        canvasController.clearCanvas();
    }

    private void loadImage(Stage stage) {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files jpg, jpeg, bmp", "*.jpg", "*.jpeg", "*.bmp"));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                Image image = new Image(file.toURI().toString());
                gc.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void saveImage(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.jpg"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPEG files (*.jpeg)", "*.jpeg"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("BMP files (*.bmp)", "*.bmp"));
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            //try {
                WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                canvas.snapshot(null, writableImage);
                //ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "jpg", file);
            /*} catch (IOException ex) {
                ex.printStackTrace();
            }*/
        }
    }
}