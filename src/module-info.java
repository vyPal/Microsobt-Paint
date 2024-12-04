module com.sword.mymicrosobtpain {
    //requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.swing;
    requires transitive java.desktop;


    opens com.sword.mymicrosobtpain to javafx.fxml;
    exports com.sword.mymicrosobtpain;
}