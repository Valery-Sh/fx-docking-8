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
package org.vns.javafx.designer.demo;

import com.sun.glass.ui.Robot;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Window;
import org.vns.javafx.JdkUtil;
import org.vns.javafx.dock.api.Util;

/**
 *
 * @author Valery
 */
public class TopWindowTracker extends Group {

    private static final int RADIUS = 5;
    private final Circle topLeft;
    private final Circle bottomLeft;
    private final Circle topRight;
    private final Circle bottomRight;

    private Color topLeftColor;
    private Color bottomLeftColor;
    private Color topRightColor;
    private Color bottomRightColor;

    private Color origtopLeftColor;
    private Color origbottomLeftColor;
    private Color origtopRightColor;
    private Color origbottomRightColor;

    private final Window window;
    private ObservableList<Window> windows;

    private boolean mouseEntered;
    //private boolean picked;
    
    private Robot robot;
    
    public TopWindowTracker(Window window) {
        this.window = window;

        topLeft = new Circle(RADIUS);
        topRight = new Circle(RADIUS);
        bottomLeft = new Circle(RADIUS);
        bottomRight = new Circle(RADIUS);
        init();
    }

    private void init() {
        setManaged(false);
        setTransparent();
        getChildren().add(topLeft);
        getChildren().add(topRight);
        getChildren().add(bottomLeft);
        getChildren().add(bottomRight);
        robot = JdkUtil.newRobot();
    }

    /**
     * Called when drag detected.
     */
    public void prepare() {
        if (window.getScene() != null && window.getScene().getRoot() != null) {
            Util.getChildren(window.getScene().getRoot()).add(this);
            setLayoutX(0);
            setLayoutY(0);
            
            positionCircles();
            setFill();
            
        }
    }

    private void positionCircles() {
        Bounds b = window.getScene().getRoot().getLayoutBounds();
        topLeft.setCenterX(RADIUS);
        topLeft.setCenterY(RADIUS);

        topRight.setCenterX(b.getMinX() + b.getWidth() - RADIUS - 1);
        topRight.setCenterY(RADIUS);

        bottomLeft.setCenterX(RADIUS);
        bottomLeft.setCenterY(b.getHeight() - RADIUS);

        bottomRight.setCenterX(b.getWidth() - RADIUS);
        bottomRight.setCenterY(b.getHeight() - RADIUS);

    }

    private void setFill() {
        topLeft.setFill(Color.RED);
        topRight.setFill(Color.GREEN);
        bottomLeft.setFill(Color.CADETBLUE);
        bottomRight.setFill(Color.BLACK);
    }

    public void finish() {
        Util.getChildren(window.getScene().getRoot()).remove(this);
    }

    public void dragged(double x, double y) {
        //System.err.println("DRAGGED 1");
        if (! Util.contains(window.getScene().getRoot(), x, y)) {
            mouseEntered = false;
            return;
        }
        //System.err.println("DRAGGED 2");
        //System.err.println("PICK " + );
        //pick(x, y);
        if (mouseEntered ) {//&& ! isTransparent()) {
            pick(x, y);
            //setTransparent();
        } else {
            //setFill();
            mouseEntered = true;
            //System.err.println("ORIG COLOR ======== ");            
            //System.err.println("YELLOW COLOR = " + Color.YELLOW);            
        }
    }
    
    private void setTransparent(boolean transparent) {
        if ( transparent ) {
            topLeft.setFill(Color.TRANSPARENT);
        } else {
            topLeft.setFill(Color.RED);
        }
    }
    private boolean isTransparent() {
        return topLeft.getFill() == Color.TRANSPARENT;
    }
    private void setTransparent() {
        topLeft.setFill(Color.TRANSPARENT);
        topRight.setFill(Color.TRANSPARENT);
        bottomLeft.setFill(Color.TRANSPARENT);
        bottomRight.setFill(Color.TRANSPARENT);
    }
    
    private void pick(double x, double y) {
//        Circle circle = topLeft;
        Parent root = window.getScene().getRoot();
        //int cx = (int) Math.ceil(root.localToScreen(circle.getCenterX(), circle.getCenterY()).getX());
        //int cy = (int) Math.ceil(root.localToScreen(circle.getCenterX(), circle.getCenterY()).getY());
        System.err.println("root.bounds = " + root.localToScreen(root.getBoundsInLocal()));
        int cx = (int) Math.ceil(root.localToScreen(1,1).getX());
        int cy = (int) Math.ceil(root.localToScreen(1,1).getY());
        System.err.println("cx = " + cx + "; cy = " + cy);
        System.err.println("topLeft.bounds = " + topLeft.localToScreen(topLeft.getBoundsInLocal()));
        
        //Robot robot = JdkUtil.newRobot();
        //Platform.runLater(() -> {
//        int c = robot.getPixelColor(cx, cy);

        int cx1 = (int) Math.ceil(root.localToScreen(20, 20).getX());
        int cy1 = (int) Math.ceil(root.localToScreen(20, 20).getY());
        int c = robot.getPixelColor(cx1, cy1);
        
        System.err.println("COLOR = " + Util.pixelToColor(c) + "; RED = " + Color.RED);
        c = robot.getPixelColor(cx, cy);
        
        System.err.println("COLOR = " + Util.pixelToColor(c) + "; RED = " + Color.RED);
        System.err.println("------------------");
        if ( Util.contains(topLeft, x, y)) {
            cx1 = (int) Math.ceil(topLeft.localToScreen(topLeft.getBoundsInLocal()).getMinX());
            cy1 = (int) Math.ceil(topLeft.localToScreen(topLeft.getBoundsInLocal()).getMinY());
            c = robot.getPixelColor(cx1 + 2, cy1 + 2);
            System.err.println(" ---  " + Util.pixelToColor(c) + "; RED = " + Color.RED);
            
        }
        System.err.println("------------------");
    }
}
