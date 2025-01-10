package com.sword.mymicrosobtpain;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.event.EventHandler;



import javax.imageio.ImageIO;
import java.awt.*;
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
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(800);
        primaryStage.getIcons().add(new Image("file:assets/paint-palette.png"));
        primaryStage.sizeToScene();
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: #ADD8E6;");
        Scene scene = new Scene(pane, 800, 600);






        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER_RIGHT);

        // Vytvoření tlačítek z MenuItems
        Button loadButton = new Button("Load");
        Button saveButton = new Button("Save");
        Button blueButton = new Button("Blue");
        Button redButton = new Button("Red");
        Button blackButton = new Button("Black");
        Button clearCanvasButton = new Button("Clear canvas");
        Button undo = new Button("Undo");
        Button invert = new Button("Invert");
        Button generateImage = new Button("Random Image");
        Button generatePattern = new Button("Random Pattern");
        Button aboutUs = new Button("About Us");

        // Nastavení kulatého tvaru tlačítek
        Button[] buttons = {loadButton, saveButton, blueButton, redButton, blackButton, clearCanvasButton, undo, invert, generateImage, generatePattern, aboutUs};

        for (Button button : buttons) {
            button.setShape(new Circle(15));
            button.setMinSize(60, 60);
            button.setMaxSize(60, 60);
            vbox.getChildren().add(button);
        }


        aboutUs.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                Stage dialog = new Stage();
                dialog.setTitle("About");
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.initOwner(primaryStage);
                dialog.getIcons().add(new Image("file:assets/lb.png"));
                VBox dialogVbox = new VBox(20);
                Text text = new Text("Members: Michal Prikasky, Sebastian Ondruska, Jiri Novak, Jakub Palacky");
                dialogVbox.getChildren().add(text);
                dialogVbox.setAlignment(Pos.CENTER);
                Scene dialogScene = new Scene(dialogVbox, 400, 100);
                dialog.setScene(dialogScene);
                dialog.setMinWidth(400);
                dialog.setMinHeight(100);
                dialog.show();
            }
        });




        canvas = new Canvas(800, 600);
        primaryStage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                canvas.setWidth(t1.doubleValue() * 0.9);
            }
        });
        primaryStage.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                canvas.setHeight(t1.doubleValue());
            }
        });
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


        loadButton.setOnAction(e -> loadImage(primaryStage, canvasController));
        saveButton.setOnAction(e -> saveImage(primaryStage));
        clearCanvasButton.setOnAction(e-> clearCanvas(canvasController));

        invert.setOnAction(e -> invertCanvas(canvasController));

        generateImage.setOnAction(e -> generateImage(canvasController));
        generatePattern.setOnAction(e -> generatePattern(canvasController));

        redButton.setOnAction(e-> canvasController.changeColor(Color.RED));
        blueButton.setOnAction(e-> canvasController.changeColor(Color.BLUE));
        blackButton.setOnAction(e-> canvasController.changeColor(Color.BLACK));
        undo.setOnAction(e-> canvasController.undo());


        HBox hBox = new HBox();
        hBox.getChildren().addAll(canvas, vbox);
        pane.getChildren().add(hBox);

        primaryStage.setScene(scene);
        primaryStage.show();
        invertCanvas(canvasController);
        invertCanvas(canvasController);




    }

    private void invertCanvas(CanvasController canvasController){
                canvasController.invert();
    }

    private void clearCanvas(CanvasController canvasController){
        canvasController.saveState();
        canvasController.clearCanvas();
        invertCanvas(canvasController);
        invertCanvas(canvasController);
    }

    private void generateImage(CanvasController canvasController){
        canvasController.generateRandomImage();
    }

    private void generatePattern(CanvasController canvasController) {
        canvasController.generateRandomPattern();
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
        if (file != null) {
            System.out.println("To je ten controlFlow...");
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



