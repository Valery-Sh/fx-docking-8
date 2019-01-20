package org.vns.javafx.dock.demo;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.dragging.view.DnDPopup;

public class FxDragDropExample2 extends Application {

    private DnDPopup dragPopup;
    // Create the TextFields
    TextField sourceFld = new TextField("This is the source text");
    TextField targetFld = new TextField("Drag and drop the source text here");

    // Create the LoggingArea
    TextArea loggingArea = new TextArea("");

    public static void main(String[] args) {
        Application.launch(args);
    }
    long num = 0;

    @Override
    public void start(Stage stage) {
        // Create the Labels
        Label headerLbl = new Label("Drag and drop the source text field onto the target text field.");
        Label sourceLbl = new Label("Gesture Source:");
        Label targetLbl = new Label("Gesture Target:");

        // Set the Prompt on the TextFields
        sourceFld.setPromptText("Enter text to drag");
        targetFld.setPromptText("Drag the source text here");

        // Create the GridPane
        GridPane pane = new GridPane();
        pane.setHgap(5);
        pane.setVgap(20);

        // Add the Labels and Fields to the Pane
        pane.add(headerLbl, 0, 0, 2, 1);
        pane.addRow(1, sourceLbl, sourceFld);
        pane.addRow(2, targetLbl, targetFld);

        // Add mouse event handlers for the source
        sourceFld.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                writelog("Event on Source: drag detected");
                dragDetected(event);
            }
        });

        sourceFld.setOnDragDone(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                writelog("Event on Source: drag done");
                dragDone(event);
            }
        });

        // Add mouse event handlers for the target
        targetFld.setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                writelog("Event on Target: drag over num = " + (++num));
                dragOver(event);
            }
        });

        targetFld.setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                writelog("Event on Target: drag dropped");
                dragDropped(event);
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
        Scene scene = new Scene(root);
        // Add the Scene to the Stage
        stage.setScene(scene);
        // Set the Title
        stage.setTitle("A Drag and Drop Example");
        // Display the Stage
        stage.show();
    }

    private void dragDetected(MouseEvent event) {
        // User can drag only when there is text in the source field
        String sourceText = sourceFld.getText();

        if (sourceText == null || sourceText.trim().equals("")) {
            event.consume();
            return;
        }

        // Initiate a drag-and-drop gesture
        Dragboard dragboard = sourceFld.startDragAndDrop(TransferMode.COPY_OR_MOVE);

        // Add the source text to the Dragboard
        ClipboardContent content = new ClipboardContent();
        content.putString(sourceText);
        dragboard.setContent(content);
        dragPopup = new DnDPopup(sourceFld, event.getScreenX(), event.getScreenY());
        event.consume();
    }

    private void dragOver(DragEvent event) {
        // If drag board has a string, let the event know that
        // the target accepts copy and move transfer modes
        Dragboard dragboard = event.getDragboard();

        if (dragboard.hasString()) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }

        event.consume();
    }

    private void dragDropped(DragEvent event) {
        dragPopup.hide();
        // Transfer the data to the target

        Dragboard dragboard = event.getDragboard();

        if (dragboard.hasString()) {
            targetFld.setText(dragboard.getString());

            // Data transfer is successful
            event.setDropCompleted(true);
        } else {
            // Data transfer is not successful
            event.setDropCompleted(false);
        }

        event.consume();
    }

    private void dragDone(DragEvent event) {
        // Check how data was transfered to the target. If it was moved, clear the text in the source.
        TransferMode modeUsed = event.getTransferMode();

        if (modeUsed == TransferMode.MOVE) {
            sourceFld.setText("");
        }
        dragPopup.hide();
        event.consume();
    }

    // Helper Method for Logging
    private void writelog(String text) {
        this.loggingArea.appendText(text + "\n");
    }

/*    public static class DnDPopup extends PopupControl {

        private Service<ObjectProperty<Point2D>> service;

        private Node node;
        private Point2D startPos;

        public DnDPopup(Node node, double x, double y) {
            this.node = node;
            this.startPos = new Point2D(x, y);
            init();
        }

        private void init() {
            getScene().addEventFilter(DragEvent.DRAG_OVER, e -> {
                //dragOver(e);
                System.err.println("OVER OVER OVER");
            });
          Bounds bnd = node.localToScreen(node.getBoundsInLocal());

            //setWidth(75);
            //setHeight(30);
            StackPane nodePane = new StackPane();
            StackPane pane = new StackPane(nodePane);
            //pane.setPrefSize(75, 30);
            pane.setStyle("-fx-background-color: yellow;-fx-border-width: 1; -fx-border-color: black; -fx-border-style: dotted");
            nodePane.setStyle("-fx-background-color: yellow; -fx-opacity: 0.3;");
            //pane.setStyle("-fx-background-color: transparent; -fx-border-width: 1; -fx-border-color: gray;");
            //pane.setMouseTransparent(true);
            getScene().setFill(null);

            ImageView iv = getSnapshot(nodePane);
            getScene().setRoot(pane);
            show(node.getScene().getWindow(), startPos.getX(), startPos.getY() + 5);
            service = new Service<ObjectProperty<Point2D>>() {
                @Override
                protected Task<ObjectProperty<Point2D>> createTask() {
                    return new MousePosTask(DnDPopup.this);
                }
            };
            service.start();

        }

        protected ImageView getSnapshot(Pane pane) {
            Bounds nodeBounds = node.localToScreen(node.getBoundsInLocal());
            WritableImage im = new WritableImage((int) Math.round(nodeBounds.getWidth()), (int) Math.round(nodeBounds.getHeight()));
            im = node.snapshot(null, im);
            ImageView iv = new ImageView(im);
            iv.setId("dragged-snapshot-image-view");

            ((Pane) pane.getParent()).getChildren().add(iv);
            Bounds ivBounds = iv.localToScreen(iv.getBoundsInLocal());

            double dw = 0;
            double dh = 0;
            if (ivBounds != null) {
                dw = ivBounds.getWidth() - nodeBounds.getWidth();
                dh = ivBounds.getHeight() - nodeBounds.getHeight();
            }

            Insets ins = pane.getInsets();

            iv.setLayoutX(ins.getLeft() + dw);
            iv.setLayoutY(ins.getTop() + dh);
            iv.toBack();
            return iv;
        }

        @Override
        public void hide() {
            super.hide();
            service.cancel();
        }

        private void dragOver(DragEvent event) {
            // If drag board has a string, let the event know that
            // the target accepts copy and move transfer modes
            double x = event.getScreenX();
            double y = event.getScreenY();
            //this.setX(x - 5);
            //this.setY(y - 5);
            Dragboard dragboard = event.getDragboard();

            if (dragboard.hasString()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }

            event.consume();
        }

        public void setPos(double x, double y) {
            // If drag board has a string, let the event know that
            // the target accepts copy and move transfer modes
            this.setX(x);
            this.setY(y + 5);
        }

        public static class MousePosTask extends Task<ObjectProperty<Point2D>> {

            final ObjectProperty<Point2D> result = new SimpleObjectProperty<>();
            private DnDPopup dragPopup;
            private Robot robot;
            private long xyPos = 0;

            public MousePosTask(DnDPopup dragPopup) {
                robot = JdkUtil.newRobot();
                this.dragPopup = dragPopup;
            }

            @Override
            protected ObjectProperty<Point2D> call() throws Exception {

                updateValue(result);
                Platform.runLater(() -> addListeners(this));

                while (true) {
                    if (this.isCancelled()) {
                        break;
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        if (this.isCancelled()) {
                            break;
                        }
                    }
                    try {
                        System.err.println("Service 1");
                        if (xyPos == Long.MAX_VALUE) {
                            xyPos = 0;
                        }
                        Point2D pos = new Point2D(xyPos++, xyPos);
                        result.set(pos);
                    } catch (Exception ex) {
                        System.err.println("Service exception msg = " + ex.getMessage());
                    }
                }//while
                Platform.runLater(() -> removeListeners(this));
                return null;
            }
            private ChangeListener<? super Point2D> listener = (v, ov, nv) -> {
                Platform.runLater(() -> {
                    //System.err.println("robot.getMouseX() = " + robot.getMouseX());
                    dragPopup.setPos(robot.getMouseX(), robot.getMouseY());
                });
            };

            public void removeListeners(Task<ObjectProperty<Point2D>> task) {
                task.valueProperty().getValue().removeListener(listener);
            }

            public void addListeners(Task<ObjectProperty<Point2D>> task) {
                task.valueProperty().getValue().addListener(listener);
            }

        }
    }//Dragpopup
*/
}
