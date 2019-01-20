/*
 * Copyright 2019 Your Organisation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vns.javafx.dock.api.dragging.view;

import com.sun.glass.ui.Robot;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.PopupControl;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.vns.javafx.JdkUtil;

public class DnDPopup extends PopupControl {

    private Service<ObjectProperty<Point2D>> service;

    private final Node node;
    private final Point2D startPos;
    private double offset;
//    private Robot robot;

    public DnDPopup(Node node, double x, double y) {
        this.node = node;
        this.startPos = new Point2D(x, y);
        init();
    }

    private void init() {
        offset = 5;
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
        getScene().addEventFilter(DragEvent.DRAG_OVER, e -> {
            System.err.println("DRRRRRRRRRRRRRRRRRRRRRR");
        });
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

    public void setPos(double x, double y) {
        this.setX(x);
        this.setY(y + offset);
    }

    public static class MousePosTask extends Task<ObjectProperty<Point2D>> {

        final ObjectProperty<Point2D> result = new SimpleObjectProperty<>();
        private DnDPopup dragPopup;
        private Robot robot;
        private long xyPos = 0;

        private ChangeListener<? super Point2D> listener = (v, ov, nv) -> {
            Platform.runLater(() -> {
                dragPopup.setPos(robot.getMouseX(), robot.getMouseY());
            });
        };

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

        public void removeListeners(Task<ObjectProperty<Point2D>> task) {
            task.valueProperty().getValue().removeListener(listener);
        }

        public void addListeners(Task<ObjectProperty<Point2D>> task) {
            task.valueProperty().getValue().addListener(listener);
        }

    }//MousePosTask

}//Dragpopup
