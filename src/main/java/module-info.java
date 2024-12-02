module com.sword.microsobtpain {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.sword.microsobtpain to javafx.fxml;
    exports com.sword.microsobtpain;
}