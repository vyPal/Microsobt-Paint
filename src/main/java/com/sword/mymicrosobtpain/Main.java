package com.sword.mymicrosobtpain;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
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
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(800);
        primaryStage.getIcons().add(new Image("file:assets/paint-palette.png"));
        primaryStage.sizeToScene();

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

        MenuItem blueButton = new MenuItem("Blue");
        MenuItem redButton = new MenuItem("Red");
        MenuItem blackButton = new MenuItem("Black");
        
        Menu canvasMenu = new Menu("Canvas");
        MenuItem clearCanvasItem = new MenuItem("Clear canvas");
        MenuItem undoStepItem = new MenuItem("Undo");
        Menu negativMenu = new Menu("Negativ");////
        MenuItem invertMenuButton = new MenuItem("Invert");////

        Menu generateMenu = new Menu("Generate");
        MenuItem generateImage = new MenuItem("Random Image");
        MenuItem generatePattern = new MenuItem("Random Pattern");


        fileMenu.getItems().addAll(loadItem, saveItem);
        menuBar.getMenus().add(fileMenu);
        canvasMenu.getItems().addAll(clearCanvasItem,undoStepItem);
        menuBar.getMenus().add(canvasMenu);
        colorMenu.getItems().addAll(blueButton,redButton,blackButton);
        menuBar.getMenus().add(colorMenu);
        negativMenu.getItems().addAll(invertMenuButton);
        menuBar.getMenus().add(negativMenu);
        AboutMenu.getItems().addAll(AboutUs);
        menuBar.getMenus().add(AboutMenu);
        generateMenu.getItems().add(generateImage);
        generateMenu.getItems().add(generatePattern);
        menuBar.getMenus().add(generateMenu);

        canvas = new Canvas(800, 600);
        primaryStage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                canvas.setWidth(t1.doubleValue());
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


        loadItem.setOnAction(e -> loadImage(primaryStage, canvasController));
        saveItem.setOnAction(e -> saveImage(primaryStage));
        clearCanvasItem.setOnAction(e-> clearCanvas(canvasController));

        invertMenuButton.setOnAction(e -> invertCanvas(canvasController));

        generateImage.setOnAction(e -> generateImage(canvasController));
        generatePattern.setOnAction(e -> generatePattern(canvasController));

        redButton.setOnAction(e-> canvasController.changeColor(Color.RED));
        blueButton.setOnAction(e-> canvasController.changeColor(Color.BLUE));
        blackButton.setOnAction(e-> canvasController.changeColor(Color.BLACK));
        undoStepItem.setOnAction(e-> canvasController.undo());

        root.setTop(menuBar);
        root.setCenter(canvas);

        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void invertCanvas(CanvasController canvasController){
                canvasController.invert();
    }

    private void clearCanvas(CanvasController canvasController){
        canvasController.saveState();
        canvasController.clearCanvas();
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