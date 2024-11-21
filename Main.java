import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Label label = new Label("I hate microsoft...\nAlso sobek is good sobek :D");
        label.setAlignment(Pos.CENTER);
        primaryStage.setScene(new Scene(label, 300, 250));
        primaryStage.setTitle("MyMicrosobtPain");
        primaryStage.show();
    }
}
