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
import java.awt.image.BufferedImage;
import java.util.Random;

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
        saveState();
        if (steps.isEmpty()) return;

        WritableImage lastState = steps.get(steps.size() - 1);
        PixelReader reader = lastState.getPixelReader();

        WritableImage invertedImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        PixelWriter writer = invertedImage.getPixelWriter();

        for (int y = 0; y < (int) canvas.getHeight(); y++) {
            for (int x = 0; x < (int) canvas.getWidth(); x++) {
                Color color = reader.getColor(x, y);
                writer.setColor(x, y, color.invert());
            }
        }

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.drawImage(invertedImage, 0, 0);
    }

    public void generateRandomImage(){
        int CanvasWidth = (int)canvas.getWidth();
        int CanvasHeight = (int)canvas.getHeight();
        Random rand = new Random();

        WritableImage img = new WritableImage(CanvasWidth, CanvasHeight);
        for(int y = 0; y < CanvasHeight; y++) {
            for(int x = 0; x < CanvasWidth; x++) {
                int r = (int)(255 * (double)x / CanvasWidth) + rand.nextInt(20) - 10;
                int g = (int)(255 * (double)y / CanvasHeight) + rand.nextInt(20) - 10;
                int b = 128 + rand.nextInt(20) - 10; 
                int a = 255; 
                r = Math.max(0, Math.min(255, r));
                g = Math.max(0, Math.min(255, g));
                b = Math.max(0, Math.min(255, b));
                int pixel = (a << 24) | (r << 16) | (g << 8) | b;
                img.getPixelWriter().setArgb(x, y, pixel);
            }
        }
        gc.drawImage(img, 0, 0);
    }
}
