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
    private Random rand = new Random();
    private int[][] pixels;
    private boolean[][] visited;

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

    public void generateRandomPattern() {
        int CanvasWidth = (int) canvas.getWidth();
        int CanvasHeight = (int) canvas.getHeight();

        pixels = new int[CanvasWidth][CanvasHeight];
        visited = new boolean[CanvasWidth][CanvasHeight];

        int centerX = CanvasWidth / 2;
        int centerY = CanvasHeight / 2;

        int centerColor = generateRandomColor();
        pixels[centerX][centerY] = centerColor;
        visited[centerX][centerY] = true;

        for (int radius = 1; radius <= Math.max(CanvasWidth, CanvasHeight); radius++) {
            for (int x = centerX - radius; x <= centerX + radius; x++) {
                for (int y = centerY - radius; y <= centerY + radius; y++) {
                    if (x >= 0 && x < CanvasWidth && y >= 0 && y < CanvasHeight &&
                            Math.abs(x - centerX) + Math.abs(y - centerY) <= radius && !visited[x][y]) {
                        int[] surroundingColors = getSurroundingPixels(x, y, CanvasWidth, CanvasHeight);
                        int averageColor = calculateAverageColor(surroundingColors);
                        int newColor = generateVariedColor(averageColor);
                        pixels[x][y] = newColor;
                        visited[x][y] = true;
                    }
                }
            }
        }

        WritableImage img = new WritableImage(CanvasWidth, CanvasHeight);
        PixelWriter writer = img.getPixelWriter();
        for (int y = 0; y < CanvasHeight; y++) {
            for (int x = 0; x < CanvasWidth; x++) {
                writer.setArgb(x, y, pixels[x][y]);
            }
        }

        gc.drawImage(img, 0, 0);
    }

    private int generateRandomColor() {
        int r = 192 + rand.nextInt(64);
        int g = 192 + rand.nextInt(64);
        int b = 192 + rand.nextInt(64);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    private int[] getSurroundingPixels(int x, int y, int maxx, int maxy) {
        int[] colors = new int[24];
        int index = 0;

        if (x > 0 && visited[x - 1][y]) {
            colors[index++] = pixels[x - 1][y];
        }
        if (x < maxx - 1 && visited[x + 1][y]) {
            colors[index++] = pixels[x + 1][y];
        }
        if (y > 0 && visited[x][y - 1]) {
            colors[index++] = pixels[x][y - 1];
        }
        if (y < maxy - 1 && visited[x][y + 1]) {
            colors[index++] = pixels[x][y + 1];
        }
        if (x > 0 && y > 0 && visited[x - 1][y - 1]) {
            colors[index++] = pixels[x - 1][y - 1];
        }
        if (x < maxx - 1 && y > 0 && visited[x + 1][y - 1]) {
            colors[index++] = pixels[x + 1][y - 1];
        }
        if (x > 0 && y < maxy - 1 && visited[x - 1][y + 1]) {
            colors[index++] = pixels[x - 1][y + 1];
        }
        if (x < maxx - 1 && y < maxy - 1 && visited[x + 1][y + 1]) {
            colors[index++] = pixels[x + 1][y + 1];
        }

        if (x > 1 && visited[x - 2][y]) {
            colors[index++] = pixels[x - 2][y];
        }
        if (x < maxx - 2 && visited[x + 2][y]) {
            colors[index++] = pixels[x + 2][y];
        }
        if (y > 1 && visited[x][y - 2]) {
            colors[index++] = pixels[x][y - 2];
        }
        if (y < maxy - 2 && visited[x][y + 2]) {
            colors[index++] = pixels[x][y + 2];
        }
        if (x > 1 && y > 1 && visited[x - 2][y - 2]) {
            colors[index++] = pixels[x - 2][y - 2];
        }
        if (x < maxx - 2 && y > 1 && visited[x + 2][y - 2]) {
            colors[index++] = pixels[x + 2][y - 2];
        }
        if (x > 1 && y < maxy - 2 && visited[x - 2][y + 2]) {
            colors[index++] = pixels[x - 2][y + 2];
        }
        if (x < maxx - 2 && y < maxy - 2 && visited[x + 2][y + 2]) {
            colors[index++] = pixels[x + 2][y + 2];
        }
        if (x > 1 && y > 0 && visited[x - 2][y - 1]) {
            colors[index++] = pixels[x - 2][y - 1];
        }
        if (x < maxx - 2 && y > 0 && visited[x + 2][y - 1]) {
            colors[index++] = pixels[x + 2][y - 1];
        }
        if (x > 1 && y < maxy - 1 && visited[x - 2][y + 1]) {
            colors[index++] = pixels[x - 2][y + 1];
        }
        if (x < maxx - 2 && y < maxy - 1 && visited[x + 2][y + 1]) {
            colors[index++] = pixels[x + 2][y + 1];
        }
        if (x > 0 && y > 1 && visited[x - 1][y - 2]) {
            colors[index++] = pixels[x - 1][y - 2];
        }
        if (x < maxx - 1 && y > 1 && visited[x + 1][y - 2]) {
            colors[index++] = pixels[x + 1][y - 2];
        }
        if (x > 0 && y < maxy - 2 && visited[x - 1][y + 2]) {
            colors[index++] = pixels[x - 1][y + 2];
        }
        if (x < maxx - 1 && y < maxy - 2 && visited[x + 1][y + 2]) {
            colors[index++] = pixels[x + 1][y + 2];
        }

        return colors;
    }

    private int calculateAverageColor(int[] colors) {
        int rSum = 0;
        int gSum = 0;
        int bSum = 0;
        int count = 0;

        for (int color : colors) {
            if (color != 0) {
                rSum += (color >> 16) & 0xFF;
                gSum += (color >> 8) & 0xFF;
                bSum += color & 0xFF;
                count++;
            }
        }

        if (count == 0) {
            return generateRandomColor();
        }

        int avgR = Math.max(0, Math.min((int) Math.round(rSum / (double) count), 255));
        int avgG = Math.max(0, Math.min((int) Math.round(gSum / (double) count), 255));
        int avgB = Math.max(0, Math.min((int) Math.round(bSum / (double) count), 255));

        return 0xFF000000 | (avgR << 16) | (avgG << 8) | avgB;
    }

    private int generateVariedColor(int averageColor) {
        int r = (averageColor >> 16) & 0xFF;
        int g = (averageColor >> 8) & 0xFF;
        int b = averageColor & 0xFF;

        r = Math.max(0, Math.min(r + rand.nextInt(50) - 25, 255));
        g = Math.max(0, Math.min(g + rand.nextInt(50) - 25, 255));
        b = Math.max(0, Math.min(b + rand.nextInt(50) - 25, 255));

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}
