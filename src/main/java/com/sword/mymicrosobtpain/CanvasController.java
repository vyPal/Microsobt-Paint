package com.sword.mymicrosobtpain;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.ArrayList;
import java.io.IOException;

//import javafx.scene.paint.Color;
//import javafx.scene.image.PixelReader;
public class CanvasController {

    @FXML
    private Canvas canvas;
    private GraphicsContext gc;
    private Color color;
    private ArrayList<WritableImage> steps;

    public CanvasController(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.color = Color.BLACK;
        this.steps = new ArrayList<>();
    }

    public void saveState() {
        WritableImage snapshot = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, snapshot);
        steps.add(snapshot);
    }

    public void handleMousePressed(double x, double y) {
        saveState();
        gc.beginPath();
        gc.moveTo(x, y);
        gc.stroke();
    }

    public void handleMouseDragged(double x, double y) {
        gc.lineTo(x, y);
        gc.stroke();
    }

    public void changeColor(Color color) {
        this.color = color;
        this.gc.setFill(color);
        this.gc.setStroke(color);
    }

    public void undo() {
        if (!steps.isEmpty()) {
            WritableImage lastState = steps.remove(steps.size() - 1);
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.drawImage(lastState, 0, 0, canvas.getWidth(), canvas.getHeight());
        }
    }


    public void loadImage(ActionEvent ae) {
        Node source = (Node) ae.getSource();
        Window stage = source.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPEG files (*.jpg)", "*.jpg"));
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

    public void saveImage(ActionEvent ae) {
        Node source = (Node) ae.getSource();
        Window stage = source.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPEG files (*.jpg)", "*.jpg"));
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                canvas.snapshot(null, writableImage);
                ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "jpg", file);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void clearCanvas() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void invert() {
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        PixelReader reader = steps[-1].get;
       WritableImage wImage = new WritableImage(w, h);//////////////
       PixelWriter writer = canvas.getPixelWriter();

        for(int y = 0; y < h; y++) {
         for(int x = 0; x < w; x++) {
            //Retrieving the color of the pixel of the loaded image
            Color color = reader.getColor(x, y);
            //Setting the color to the writable image
            writer.setColor(x, y, color.invert());
         }
      }

    }
}

