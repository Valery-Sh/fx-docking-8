package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class TestGridFramePane extends Application {

    public static Stage frontStage;
    public static Stage stg01;
    public static Stage stg02;

    @Override
    public void start(Stage stage) throws Exception {
        
        StackPane stackPane = new StackPane();
        
        
        RowConstraints rc0 = new RowConstraints();
        RowConstraints rc1 = new RowConstraints();
        RowConstraints rc2 = new RowConstraints();

        ColumnConstraints cc0 = new ColumnConstraints();
        ColumnConstraints cc1 = new ColumnConstraints();
        ColumnConstraints cc2 = new ColumnConstraints();
        
        
        
        
        
        stage.setTitle("Test DockSideBar");

        stackPane.setPrefHeight(300);
        stackPane.setPrefWidth(300);
        
        Button b01 = new Button("Change Rotate Angle");
        
        //((Region)borderPane.getRight()).setMaxWidth(0);
        Scene scene = new Scene(stackPane);
        //scene.getRoot().setStyle("-fx-background-color: yellow");

        
        Button b02 = new Button("Change Orientation");
        Button b03 = new Button("Change Side");
        Button b04 = new Button("center Button");
        //borderPane.getChildren().addAll(b01,b02,b03);
        b01.setOnAction(e->{
        });
        
        stage.setScene(scene);
        stage.setOnShown(s -> {
        });
        stage.show();
        
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);

        Dockable.initDefaultStylesheet(null);

    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}

