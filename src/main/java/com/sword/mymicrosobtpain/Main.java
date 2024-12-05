package com.sword.mymicrosobtpain;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.event.EventHandler;



import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Main extends Application {

    private Canvas canvas;
    private GraphicsContext gc;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("MyMicrosobtPain");

        primaryStage.getIcons().add(new Image("file:assets/paint-palette.png"));

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 800, 600);

        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem loadItem = new MenuItem("Load");
        MenuItem saveItem = new MenuItem("Save");

        Menu colorMenu = new Menu("Colors");

        Menu AboutMenu = new Menu("About");
        MenuItem AboutUs = new MenuItem("About Us");
        AboutUs.setOnAction(new EventHandler<ActionEvent>() {
            
            public void handle(ActionEvent event) {
                final Stage dialog = new Stage();
                dialog.setTitle("About our team!");
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.initOwner(primaryStage);
                VBox dialogVbox = new VBox(20);
                dialogVbox.getChildren().add(new Text("Members: Michal Prikasky, Sebastian Ondruska,\nJiri Novak, Jakub Palacky\nPS. JavaFx sucks :<"));
                Scene dialogScene = new Scene(dialogVbox, 400, 300);
                dialog.setScene(dialogScene);
                dialog.show();
            }
         });

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
        AboutMenu.getItems().addAll(AboutUs);
        menuBar.getMenus().add(AboutMenu);

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



        loadItem.setOnAction(e -> loadImage(primaryStage, canvasController));
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
        canvasController.saveState();
        canvasController.clearCanvas();
    }

    private void loadImage(Stage stage, CanvasController canvasController) {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files jpg, jpeg, bmp", "*.jpg", "*.jpeg", "*.bmp"));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                canvasController.saveState();
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
        System.out.println("Sobek pred if");
        if (file != null) {
            System.out.println("Sobek");
            try {
                WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                canvas.snapshot(null, writableImage);
                System.out.println("Pred");
                ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
                System.out.println("Po");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}