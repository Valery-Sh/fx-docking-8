package org.vns.javafx.dock.demo;

import com.sun.glass.ui.Robot;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class FxDragDropExample1 extends Application {
    // Create the TextFields

    TextField sourceFld = new TextField("This is the Source Text");
    TextField targetFld = new TextField("Drag and drop the source text here");

    // Create the LoggingArea
    TextArea loggingArea = new TextArea("");

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Robot robot = com.sun.glass.ui.Application.GetApplication().createRobot();

        Button b = new Button("Button b");

        b.setOnMouseReleased(e -> {
//            System.err.println(" !!! MOUSE Released on b");
        });

        Button b1 = new Button("Move b");
        VBox vb = new VBox(b1, b);
        Scene scene1 = new Scene(vb);

        scene1.addEventFilter(MouseDragEvent.MOUSE_DRAG_ENTERED, e -> {
            System.err.println("ON MOUSE DRAGG ENTERED !!! ");
        });
        scene1.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> {
            System.err.println("ON MOUSE DRAGG ENTERED !!! ");
        });

        Stage stage1 = new Stage();
        stage1.setScene(scene1);
        stage1.setWidth(200);
        stage1.setHeight(200);

        scene1.setFill(Color.ALICEBLUE);
        /*        scene1.addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
            System.err.println(" !!! MOUSE Moved on scene1");
        });
         */
        scene1.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            System.err.println(" !!! MOUSE Moved on scene1");
        });

//        d.start(st2Scene);
        stage1.show();
        // Set the Size of the TextFields
        sourceFld.setOnMouseDragged(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                //System.err.println("mouse dragged");
                event.setDragDetect(false);
                Bounds bnd = b.localToScreen(b.getBoundsInLocal());
                if (bnd.contains(event.getScreenX(), event.getScreenY())) {
                    //System.err.println("mouse dragged over b");
                    int x = (int) event.getScreenX();
                    int y = (int) event.getScreenY();
                    robot.mouseMove(x + 1, y + 1);
                    //robot.keyPress('a');
                    //robot.mouseRelease(1);
                }

            }
        });

        sourceFld.setPrefSize(
                200, 20);
        targetFld.setPrefSize(
                200, 20);

        // Create the Labels
        Label sourceLbl = new Label("Source Node:");
        Label targetLbl = new Label("Target Node:");

        // Create the GridPane
        GridPane pane = new GridPane();

        pane.setHgap(
                5);
        pane.setVgap(
                20);

        // Add the Labels and Fields to the Pane
        pane.addRow(
                0, sourceLbl, sourceFld);
        pane.addRow(
                1, targetLbl, targetFld);

        // Add mouse event handlers for the source
        sourceFld.setOnMousePressed(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent event) {
                sourceFld.setMouseTransparent(true);
                writelog("Event on Source: mouse pressed");
                event.setDragDetect(true);
            }
        });

        sourceFld.setOnMouseReleased(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                sourceFld.setMouseTransparent(false);
                writelog("Event on Source: mouse released");
            }
        });

        sourceFld.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                sourceFld.startFullDrag();
                System.err.println("Event on Source: drag detected");
            }
        });

        // Add mouse event handlers for the target
        targetFld.setOnMouseDragEntered(new EventHandler<MouseDragEvent>() {
            public void handle(MouseDragEvent event) {
                System.err.println("Event on Target: mouse dragged");
            }
        });

        targetFld.setOnMouseDragOver(new EventHandler<MouseDragEvent>() {
            public void handle(MouseDragEvent event) {
                System.err.println("Event on Target: mouse drag over");
            }
        });

        targetFld.setOnMouseDragReleased(new EventHandler<MouseDragEvent>() {
            public void handle(MouseDragEvent event) {
                targetFld.setText(sourceFld.getSelectedText());
                writelog("Event on Target: mouse drag released");
            }
        });

        targetFld.setOnMouseDragExited(new EventHandler<MouseDragEvent>() {
            public void handle(MouseDragEvent event) {
                writelog("Event on Target: mouse drag exited");
            }
        });

        // Create the VBox
        VBox root = new VBox();
        // Add the Pane and The LoggingArea to the VBox
        root.getChildren().addAll(pane, loggingArea);
        // Set the Style of the VBox
        root.setStyle("-fx-padding: 10;"
                + "-fx-border-style: solid inside;"
                + "-fx-border-width: 2;"
                + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;"
                + "-fx-border-color: blue;");

        // Create the Scene
        Scene scene = new Scene(root, 300, 200);
        // Add the Scene to the Stage
        stage.setScene(scene);
        // Set the Title
        stage.setTitle("A Press Drag Release Example");
        // Display the Stage
        stage.show();
        scene.getRoot().setOnMouseMoved(e -> {
            System.err.println("ROBOT moved to scene.root");
        });
        b.setOnMousePressed(e -> {
            System.err.println(" !!! MOUSE pressed on b");
            Bounds bnd = scene.getRoot().localToScreen(scene.getRoot().getBoundsInLocal());
            int x = robot.getMouseX();
            int y = robot.getMouseY();
            Platform.runLater(() -> {
                robot.mouseMove((int) bnd.getMinX() + 10, (int) bnd.getMinY() + 10);
                robot.mouseMove((int) bnd.getMinX() + 12, (int) bnd.getMinY() + 12);
                //robot.mouseMove(x, y);
            });
            //robot.mouseMove(x,y);
        });

    }

    // Helper Method for Logging
    private void writelog(String text) {
        this.loggingArea.appendText(text + "\n");
    }
    

}
