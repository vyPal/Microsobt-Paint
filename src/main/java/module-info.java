module com.sword.mymicrosobtpain {
    //requires javafx.fxml;
    requires javafx.controls;
    requires javafx.base;
    requires javafx.fxml;


    opens com.sword.mymicrosobtpain to javafx.fxml;
    exports com.sword.mymicrosobtpain;
}