package com.sword.mymicrosobtpain;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MyController {

    @FXML
    private Button myButton;

    @FXML
    public void initialize() {
        System.out.println("FXML Initialized!");
    }
}