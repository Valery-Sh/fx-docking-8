/*
 * Copyright 2018 Your Organisation.
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
package org.vns.javafx.dock.api.resizer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import org.vns.javafx.dock.api.Util;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.indicator.GridPaneConstraintsDividers.GridLineResizer;

/**
 *
 * @author Valery Shyskin
 */
public class DividerLine extends Control implements EventHandler<MouseEvent> {

    public static final String DIVIDER_ID = "divider-line-control";
    public static final String DIVIDER_RECT = "divider-rectangle";
    public static final String DIVIDER_LINE = "divider-line";

    protected DividerLineSkin skinBase;

    //private Node boundNode;
    private double startPosition;

    private ChangeListener<? super Scene> sceneListener = (v, ov, nv) -> {
        if (nv != null) {
            Util.getChildren(nv.getRoot()).add(this);
        }
    };

    private Resizer resizer;

    private boolean listenMouseEvents;

    private final Orientation orientation;

    private Point2D startMousePos;

    public DividerLine(Orientation orientation) {
        this.orientation = orientation;
        init();
    }

    private void init() {
        getStyleClass().add(DIVIDER_ID);
        setId(DIVIDER_ID);
        if (orientation == Orientation.HORIZONTAL) {
            getStyleClass().add("divider-line-horizontal");
        } else {
            getStyleClass().add("divider-line-vertical");
        }
        setVisible(false);
        setManaged(false);

    }

    public void resetOnHide() {
        if (skinBase != null) {
            skinBase.resetOnHide();
        }
    }

    public boolean isListenMouseEvents() {
        return listenMouseEvents;
    }

    public void setListenMouseEvents(boolean listenMouseEvents) {
        this.listenMouseEvents = listenMouseEvents;
    }

    /**
     *
     * @return screen position of the mouse cursor
     */
    public Point2D getStartMousePos() {
        return startMousePos;
    }

    /**
     * Sets the specified screen position.
     * 
     * @param startMousePos screen position of the mouse cursor
     */
    public void setStartMousePos(Point2D startMousePos) {
        this.startMousePos = startMousePos;
    }

    public Resizer getResizer() {
        return resizer;
    }

    public void setResizer(Resizer resizer) {
        this.resizer = resizer;
    }

    public void show(boolean listenMouseEvent) {

        this.listenMouseEvents = listenMouseEvent;
        if (resizer == null) {
            resizer = new DefaultResizer(this);
        }
        if (listenMouseEvent) {
            this.addEventFilter(MouseEvent.MOUSE_MOVED, this);
            this.addEventFilter(MouseEvent.MOUSE_EXITED, this);

            this.addEventFilter(MouseEvent.MOUSE_PRESSED, this);
            this.addEventFilter(MouseEvent.DRAG_DETECTED, this);
            this.addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            this.addEventFilter(MouseEvent.MOUSE_RELEASED, this);
        }

        if (skinBase != null) {
            skinBase.layout();
        }
        //if (boundNode.getScene() != null) {

        //DockUtil.getChildren(boundNode.getScene().getRoot()).add(this);
        //}
        setVisible(true);
        toFront();
    }

    public double getOffset() {
        return 1.5;
        /*        Rectangle r = (Rectangle) lookup("." + DIVIDER_RECT);
        if (r == null) {
            return 1.5;
        }
        return getOrientation() == Orientation.HORIZONTAL ? r.getHeight() / 2 : r.getWidth() / 2;
         */
    }

    public void hide() {
        resetOnHide();
        if (getScene() != null) {
            //DockUtil.getChildren(getScene().getRoot()).remove(this);
        }
//        if (boundNode != null) {
//            boundNode.sceneProperty().removeListener(sceneListener);
//        }
//        boundNode = null;
        resizer = null;
        this.listenMouseEvents = false;
        this.removeEventFilter(MouseEvent.MOUSE_MOVED, this);
        this.removeEventFilter(MouseEvent.MOUSE_EXITED, this);
        this.removeEventFilter(MouseEvent.MOUSE_PRESSED, this);
        this.removeEventFilter(MouseEvent.DRAG_DETECTED, this);
        this.removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
        this.removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
        setVisible(false);

    }

    public static boolean isDividerLine(Node node) {
        if (!(node instanceof Shape)) {
            return false;
        }
        if (node instanceof DividerLine) {
            return true;
        }
        if (node.getParent() instanceof DividerLine) {
            return true;
        } else {
            return false;
        }
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void handle(MouseEvent ev, Cursor c) {
        if (ev.getEventType() == MouseEvent.MOUSE_MOVED) {
            getScene().setCursor(c);
        } else if (ev.getEventType() == MouseEvent.MOUSE_EXITED) {
            getScene().setCursor(Cursor.DEFAULT);
        } else if (ev.getEventType() == MouseEvent.MOUSE_PRESSED) {
            //removeMouseExitedListener(shape);
            removeEventFilter(MouseEvent.MOUSE_EXITED, this);
            setStartMousePos(new Point2D(ev.getScreenX(), ev.getScreenY()));

        } else if (ev.getEventType() == MouseEvent.DRAG_DETECTED) {
            resizer.start(ev, c);
        }
        if (ev.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            if (resizer.isStarted()) {
                resizer.resize(ev.getScreenX(), ev.getScreenY());
            }
        } else if (ev.getEventType() == MouseEvent.MOUSE_RELEASED) {
            getScene().setCursor(Cursor.DEFAULT);
            resizer.finish();
            if (ev.isControlDown()) {
                resizer.resetToDefault();
            } else {
                ((GridLineResizer) resizer).updateOnRelease();
            }

            addEventFilter(MouseEvent.MOUSE_EXITED, this);
        }
    }

    @Override
    public void handle(MouseEvent ev) {
        if (!listenMouseEvents) {
            return;
        }
        if (getOrientation() == Orientation.HORIZONTAL) {
            handle(ev, Cursor.N_RESIZE);
        } else {
            handle(ev, Cursor.E_RESIZE);
        }
        ev.consume();
    }

    @Override
    protected void layoutChildren() {
        getChildren().forEach(c -> {
            if (c instanceof Rectangle) {
                c.setLayoutX(0);
                c.setLayoutY(0);
            } else {
                if (getOrientation() == Orientation.VERTICAL) {
                    //((Line) c).setStartX(0);
                    ((Line) c).setStartY(0);
                } else {
                    ((Line) c).setStartX(0);
                }
            }
        });
    }

    @Override

    protected Skin<?> createDefaultSkin() {
        return (skinBase = new DividerLineSkin(this));
    }

    @Override
    public String getUserAgentStylesheet() {
        return Dockable.class.getResource("resources/default.css").toExternalForm();
    }

    public static class DividerLineSkin extends SkinBase<DividerLine> {

        public static final double SIZE = 3;
        DividerLine ctrl;
        Rectangle rect;
        Line line;

        private ChangeListener<? super Insets> insetsListener = (v, ov, nv) -> {
            layout();

        };

        public DividerLineSkin(DividerLine control) {
            super(control);

            ctrl = control;
            rect = new Rectangle();
            rect.getStyleClass().add(DIVIDER_RECT);
            line = new Line();
            line.getStyleClass().add(DIVIDER_LINE);
            ctrl.setManaged(false);
            line.setManaged(false);
            rect.setManaged(true);

            //rect.setStyle("-fx-fill: aqua");
            rect.setFill(Color.TRANSPARENT);

            layout();
            getChildren().addAll(rect, line);
            ctrl.toFront();
            rect.toFront();
            line.toFront();

            Util.setForeign(ctrl, rect, line);
        }

        public void resetOnHide() {
            ctrl.prefHeightProperty().unbind();
            ctrl.prefWidthProperty().unbind();
            line.startXProperty().unbind();
            line.endXProperty().unbind();
            line.startYProperty().unbind();
            line.endYProperty().unbind();
            rect.heightProperty().unbind();
            rect.widthProperty().unbind();

            ctrl.setVisible(false);
        }

        private void layout() {
            rect.getStyleClass().add("rectangle");
            line.getStyleClass().add("line");
            ctrl.prefHeightProperty().unbind();
            ctrl.prefWidthProperty().unbind();
            line.startXProperty().unbind();
            line.endXProperty().unbind();
            line.startYProperty().unbind();
            line.endYProperty().unbind();
            rect.heightProperty().unbind();
            rect.widthProperty().unbind();

            Insets ins = Insets.EMPTY;

            if (ctrl.getOrientation() == Orientation.VERTICAL) {
                rect.heightProperty().bind(ctrl.prefHeightProperty());
                rect.widthProperty().bind(ctrl.prefWidthProperty());

                line.startXProperty().bind(rect.widthProperty().divide(2));
                line.endXProperty().bind(rect.widthProperty().divide(2));
                line.setStartY(0);
                line.endYProperty().bind(rect.heightProperty());

                rect.setLayoutX(0);
                rect.setLayoutY(0);

            } else {

                rect.widthProperty().bind(ctrl.prefWidthProperty());
                rect.heightProperty().bind(ctrl.prefHeightProperty());

                rect.setLayoutX(0);
                rect.setLayoutY(0);

                line.startYProperty().bind(rect.heightProperty().divide(2));
                line.endYProperty().bind(rect.heightProperty().divide(2));
                line.setStartX(0);
                line.endXProperty().bind(rect.widthProperty());

            }

        }

    }//DividerLineSkin

    public static class DefaultResizer extends Resizer {

        public DefaultResizer(DividerLine dividerLine) {
            super(dividerLine);

        }

        @Override
        protected void relocateX(double delta) {
            if (getDividerLine().getOrientation() == Orientation.HORIZONTAL) {
                return;
            }
            Node root = getDividerLine().getScene().getRoot();
            Bounds rootBounds = root.localToScreen(root.getBoundsInLocal());
            double pos = getDividerLine().getStartMousePos().getX() + delta;
            if (isAcceptableX(delta)) {
                getDividerLine().setLayoutX(pos - rootBounds.getMinX());
            }
        }

        @Override
        protected void relocateY(double delta) {
            if (getDividerLine().getOrientation() == Orientation.VERTICAL) {
                return;
            }
            Node root = getDividerLine().getScene().getRoot();
            Bounds rootBounds = root.localToScreen(root.getBoundsInLocal());
            System.err.println("setYLayout delta = " + delta);
            double pos = getDividerLine().getStartMousePos().getY() + delta;
            if (isAcceptableY(delta)) {
                //getDividerLine().setLayoutY(pos - rootBounds.getMinY());
                relocateY(delta);
            }

        }

        @Override
        protected double getMinWidth() {
            return -1;
        }

        @Override
        protected double getMinHeight() {
            return -1;
        }

        @Override
        public boolean isStarted() {
            return getDividerLine() != null && getCursor() != null;
        }

        @Override
        protected void setSize() {
        }

    }

    public abstract static class Resizer {

        private final DoubleProperty mouseX = new SimpleDoubleProperty();
        private final DoubleProperty mouseY = new SimpleDoubleProperty();

        private Cursor cursor;

        private final Set<Cursor> cursorTypes = new HashSet<>();
        private final DividerLine dividerLine;

        public Resizer(DividerLine dividerLine) {
            this.dividerLine = dividerLine;
            Collections.addAll(cursorTypes,
                    Cursor.S_RESIZE, Cursor.E_RESIZE, Cursor.N_RESIZE, Cursor.W_RESIZE);
        }

        protected void resetToDefault() {

        }

        public DividerLine getDividerLine() {
            return dividerLine;
        }

        private void setCursorTypes(Cursor... cursors) {
            cursorTypes.clear();
            Collections.addAll(this.cursorTypes, cursors);
        }

        public void resize(double x, double y) {
            double xDelta = 0, yDelta = 0, wDelta = 0, hDelta = 0;
            double curX = mouseX.get();
            double curY = mouseY.get();

            if (cursor == Cursor.E_RESIZE) {
                wDelta = x - getDividerLine().getStartMousePos().getX();
            } else if (cursor == Cursor.N_RESIZE) {
                hDelta = y - getDividerLine().getStartMousePos().getY();
            }
            if (isAcceptableX(wDelta)) {
                relocateX(wDelta);
            }
            if (isAcceptableY(hDelta)) {
                relocateY(hDelta);
            }
        }

        protected abstract void relocateX(double wDelta);

        protected abstract void relocateY(double hDelta);

        protected abstract double getMinWidth();

        protected abstract double getMinHeight();

        public abstract boolean isStarted();

        public void start(MouseEvent ev, Cursor cursor, Cursor... supportedCursors) {
            setCursorTypes(supportedCursors);
            this.mouseX.set(ev.getScreenX());
            this.mouseY.set(ev.getScreenY());
            this.cursor = cursor;
            setSize();
        }

        public void finish() {
            this.cursor = null;
        }

        protected abstract void setSize();

        public Cursor getCursor() {
            return cursor;
        }

        public void setCursor(Cursor cursor) {
            this.cursor = cursor;
        }

        public DoubleProperty mouseXProperty() {
            return mouseX;
        }

        public DoubleProperty mouseYProperty() {
            return mouseY;
        }

        public Double getMouseX() {
            return mouseX.get();
        }

        public Double getMouseY() {
            return mouseY.get();
        }

        public void setMouseX(Double mX) {
            this.mouseX.set(mX);
        }

        public void setMouseY(Double mY) {
            this.mouseY.set(mY);
        }

        public boolean isAcceptableX(double delta) {
            return true;
        }

        public boolean isAcceptableY(double delta) {
            return true;
        }

    }
}
