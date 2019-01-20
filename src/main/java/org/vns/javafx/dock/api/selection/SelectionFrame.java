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
package org.vns.javafx.dock.api.selection;

import java.util.Set;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import javafx.stage.Window;
import org.vns.javafx.designer.SceneViewUtil;
import static org.vns.javafx.dock.api.Constants.SKIP_CSS_CLASS;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.Selection;
import org.vns.javafx.dock.api.resizer.ResizeShape;
import static org.vns.javafx.dock.api.selection.SelectionFrame.Direction.*;
import static org.vns.javafx.dock.api.Constants.SKIP_VALUE;

/**
 *
 * @author Valery Shyskin
 */
public class SelectionFrame extends Control {

    public static final String ID = "ID-" + SKIP_VALUE;
    public static final String FRAME_CSS_CLASS = SKIP_CSS_CLASS;
    public static final String NODE_ID = "NODE-" + ID;
    public static final String PARENT_ID = "PARENT-" + ID;

    public static final String SHAPE_ID = "RESIZE-SHAPE-" + ID;

    private final ObservableMap<Direction, ResizeShape> sideShapes = FXCollections.observableHashMap();

    //private final ObservableList<ResizeShape> cellHorLines = FXCollections.observableArrayList();
    //private final ObservableList<ResizeShape> cellVertLines = FXCollections.observableArrayList();
    private Class<?> shapeClass;
    private final ObjectProperty<Node> boundNode = new SimpleObjectProperty<>();
    private final boolean enableResize;
    private Point2D startMousePos;
    private Parent root;

    public enum Direction {
        nShape, //north indicator
        neShape, //north-east indicator
        eShape, //east indicator
        seShape, //south-east indicator
        sShape, //south indicator

        swShape, // south-west indicator
        wShape, // west indicator
        nwShape   // north-west indicator
    }

    public SelectionFrame(Parent root) {
        this(root, true);
    }

    public SelectionFrame(Parent root, boolean enableResize) {
        this(root, null, enableResize);
    }

    public SelectionFrame(Parent root, Class<?> resizeShapeClass) {
        this(root, resizeShapeClass, true);
    }

    public SelectionFrame(Parent root, Class<?> resizeShapeClass, boolean enableResize) {
        this.shapeClass = resizeShapeClass;
        if (shapeClass == null) {
            this.shapeClass = Circle.class;
        }
        this.enableResize = enableResize;
        this.root = root;

        init();
    }

    private void init() {
        getStyleClass().add(FRAME_CSS_CLASS);
        getStyleClass().add("frame-control");
        setManaged(false);
        //if (!enableResize) {
        setMouseTransparent(true);
        //}
    }

    public void hide() {
        setVisible(false);
        getSideShapes().forEach((k, v) -> {
            v.setVisible(false);
        });
    }

    public void show() {
        setVisible(true);
        toFront();
        skinBase.adjustBoundsToNode();
        layoutChildren();
        getSideShapes().forEach((k, v) -> {
            v.setVisible(true);
            v.toFront();
        });
     
    }

    public Parent getRoot() {
        return root;
    }

    public static void hideAll(Window win) {
        Set<Node> set = win.getScene().getRoot().lookupAll("#" + NODE_ID);
        for (Node node : set) {
            node.setVisible(false);
        }
        set = win.getScene().getRoot().lookupAll("#" + PARENT_ID);
        for (Node node : set) {
            node.setVisible(false);
        }
        set = win.getScene().getRoot().lookupAll("#" + SHAPE_ID);
        for (Node node : set) {
            node.setVisible(false);
        }
    }

 
    public Point2D getStartMousePos() {
        return startMousePos;
    }

    public void setStartMousePos(Point2D startMousePos) {
        this.startMousePos = startMousePos;
    }

    public boolean isEnableResize() {
        return enableResize;
    }

    public ObservableMap<Direction, ResizeShape> getSideShapes() {
        return sideShapes;
    }

    public ObjectProperty<Node> boundNodeProperty() {
        return boundNode;
    }

    public Node getBoundNode() {
        return boundNode.get();
    }

    public void setBoundNode(Node boundNode) {
        this.boundNode.set(boundNode);
    }

    public Class<?> getShapeClass() {
        return shapeClass;
    }
    private FramePaneSkin skinBase;

    @Override
    protected Skin<?> createDefaultSkin() {
        skinBase = new FramePaneSkin(this);
        return skinBase;
    }

    public Pane getPane() {
        return (Pane) getChildren().get(0);
    }

    @Override
    public String getUserAgentStylesheet() {
        return Dockable.class.getResource("resources/default.css").toExternalForm();
    }

    @Override
    protected double computeMinWidth(final double height) {
        return ((FramePaneSkin) skinBase).computeMinWidth(height, snappedTopInset(), snappedRightInset(), snappedBottomInset(), snappedLeftInset());
    }

    /**
     * Computes the minimum allowable height of the Control, based on the
     * provided width. The minimum height is not calculated within the Control,
     * instead the calculation is delegated to the
     * {@link Node#minHeight(double)} method of the {@link Skin}. If the Skin is
     * null, the returned value is 0.
     *
     * @param width The width of the Control, in case this value might dictate
     * the minimum height.
     * @return A double representing the minimum height of this control.
     */
    @Override
    protected double computeMinHeight(final double width) {
        return skinBase.computeMinHeight(width, snappedTopInset(), snappedRightInset(), snappedBottomInset(), snappedLeftInset());
    }

    /**
     * Computes the maximum allowable width of the Control, based on the
     * provided height. The maximum width is not calculated within the Control,
     * instead the calculation is delegated to the {@link Node#maxWidth(double)}
     * method of the {@link Skin}. If the Skin is null, the returned value is 0.
     *
     * @param height The height of the Control, in case this value might dictate
     * the maximum width.
     * @return A double representing the maximum width of this control.
     */
    @Override
    protected double computeMaxWidth(double height) {
        return skinBase.computeMaxWidth(height, snappedTopInset(), snappedRightInset(), snappedBottomInset(), snappedLeftInset());
    }

    /**
     * Computes the maximum allowable height of the Control, based on the
     * provided width. The maximum height is not calculated within the Control,
     * instead the calculation is delegated to the
     * {@link Node#maxHeight(double)} method of the {@link Skin}. If the Skin is
     * null, the returned value is 0.
     *
     * @param width The width of the Control, in case this value might dictate
     * the maximum height.
     * @return A double representing the maximum height of this control.
     */
    @Override
    protected double computeMaxHeight(double width) {
        return skinBase.computeMaxHeight(width, snappedTopInset(), snappedRightInset(), snappedBottomInset(), snappedLeftInset());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double computePrefWidth(double height) {
        return skinBase.computePrefWidth(height, snappedTopInset(), snappedRightInset(), snappedBottomInset(), snappedLeftInset());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double computePrefHeight(double width) {
        return skinBase.computePrefHeight(width, snappedTopInset(), snappedRightInset(), snappedBottomInset(), snappedLeftInset());
    }

    /**
     * *************************************************************************
     * Implementation of layout bounds for the Control. We want to preserve *
     * the lazy semantics of layout bounds. So whenever the width/height *
     * changes on the node, we end up invalidating layout bounds. We then *
     * recompute it on demand. *
     * ************************************************************************
     */
    /**
     * {@inheritDoc}
     */
    @Override
    public void layoutChildren() {

   
        final double x = snappedLeftInset();
        final double y = snappedTopInset();

        final double w = snapSize(getWidth()) - x - snappedRightInset();
        final double h = snapSize(getHeight()) - y - snappedBottomInset();

        if (getBoundNode() != null) {
            Bounds nodeBounds = getBoundNode().localToScene(getBoundNode().getBoundsInLocal());
            skinBase.layoutChildren(0, 0, nodeBounds.getWidth(), nodeBounds.getHeight());
        }

    }

    public static class FramePaneSkin extends SkinBase<SelectionFrame> {

        MouseEventHandler mouseHandler;
        Pane pane;
        SelectionFrame ctrl;
        Rectangle rect;

        private ChangeListener<Node> boundNodeListener;
        
        private final ChangeListener<? super Number> layoutXYListener = (o, ov, nv) -> {
            adjustBoundsToNode();
        };
        private final ChangeListener<Bounds> boundsListener = (o, ov, nv) -> {
            adjustBoundsToNode();
        };
        private final ChangeListener<Background> backgroundListener = (o, ov, nv) -> {
            if (nv != null) {
                //20.01.2019adjustBoundsToNode(nv.getOutsets());
                adjustBoundsToNode();
            }
        };

        private final ChangeListener<Transform> localToSceneTransformListener = (o, ov, nv) -> {
            adjustBoundsToNode();
        };

        public FramePaneSkin(SelectionFrame control) {
            super(control);
            ctrl = control;
            //ctrl.setId(NODE_ID);
            rect = new Rectangle();
            rect.setId("rectangle");
            rect.getStyleClass().add(FRAME_CSS_CLASS);

            mouseHandler = new MouseEventHandler(ctrl);
            rect.setMouseTransparent(true);
            if (ctrl.isEnableResize()) {
                rect.getStyleClass().add("resizable");
            } else {
                rect.getStyleClass().add("not-resizable");
            }
            pane = new Pane(rect);
            pane.setManaged(false);
            rect.setManaged(false);
            ctrl.setManaged(false);
            pane.getStyleClass().add(FRAME_CSS_CLASS);

            rect.toBack();
            pane.setStyle("-fx-background-color: transparent");
            //pane.setStyle("-fx-background-color: yellow; -fx-border-color: green; -fx-border-width: 8");
            ctrl.setStyle("-fx-background-color: transparent");
            //ctrl.setStyle("-fx-background-color: yellow; -fx-border-color: red; -fx-border-width: 8");

            ctrl.setMouseTransparent(true);
            pane.setMouseTransparent(true);
            rect.setMouseTransparent(true);

            if (ctrl.isEnableResize()) {
                createSideShapes();
            }
            initBoundNode();
            getChildren().add(pane);
            ctrl.setVisible(false);
        }

        private void createSideShapes() {
            ResizeShape sh = createSideShape(nShape);
            sh.centerXProperty().bind(ctrl.layoutXProperty().add(rect.widthProperty().divide(2)));
            sh.centerYProperty().bind(ctrl.layoutYProperty());

            sh = createSideShape(neShape);
            sh.centerXProperty().bind(ctrl.layoutXProperty().add(rect.widthProperty()));
            sh.centerYProperty().bind(ctrl.layoutYProperty());

            sh = createSideShape(eShape);
            sh.centerXProperty().bind(ctrl.layoutXProperty().add(rect.widthProperty()));
            sh.centerYProperty().bind(ctrl.layoutYProperty().add(rect.heightProperty().divide(2)));

            sh = createSideShape(seShape);
            sh.centerXProperty().bind(ctrl.layoutXProperty().add(rect.widthProperty()));
            sh.centerYProperty().bind(ctrl.layoutYProperty().add(rect.heightProperty()));

            sh = createSideShape(sShape);
            sh.centerXProperty().bind(ctrl.layoutXProperty().add(rect.widthProperty().divide(2)));
            sh.centerYProperty().bind(ctrl.layoutYProperty().add(rect.heightProperty()));

            sh = createSideShape(swShape);
            sh.centerXProperty().bind(ctrl.layoutXProperty());
            sh.centerYProperty().bind(ctrl.layoutYProperty().add(rect.heightProperty()));

            sh = createSideShape(wShape);
            sh.centerXProperty().bind(ctrl.layoutXProperty());
            sh.centerYProperty().bind(ctrl.layoutYProperty().add(rect.heightProperty().divide(2)));

            sh = createSideShape(nwShape);
            sh.centerXProperty().bind(ctrl.layoutXProperty());
            sh.centerYProperty().bind(ctrl.layoutYProperty());

        }

        protected ResizeShape createSideShape(Direction d) {
            ResizeShape retval = new ResizeShape(ctrl.getShapeClass());
            retval.getStyleClass().add(FRAME_CSS_CLASS);
            retval.setId(SHAPE_ID);
            ctrl.getSideShapes().put(d, retval);
            SceneViewUtil.getChildren(ctrl.getRoot()).add(retval);
            retval.toFront();
            retval.setVisible(false);
            return retval;
        }

        private void initBoundNode() {

            boundNodeListener = (v, ov, nv) -> {
                if (ov != null) {
                    ov.boundsInParentProperty().removeListener(boundsListener);
                    ov.layoutBoundsProperty().removeListener(boundsListener);

                    ov.layoutXProperty().removeListener(layoutXYListener);
                    ov.layoutYProperty().removeListener(layoutXYListener);
                    //ov.layoutBoundsProperty().removeListener(boundsInParentListener);

                    ov.localToSceneTransformProperty().removeListener(localToSceneTransformListener);
                    if (ov instanceof Region) {
                        ((Region) ov).backgroundProperty().removeListener(backgroundListener);
                    }

                    if (!ctrl.getSideShapes().isEmpty()) {
                        removeShapeMouseEventHandlers();
                    }
                    Selection sel = DockRegistry.lookup(Selection.class);
                    if (sel != null) {
                        //    sel.setSelected(null);
                    } else {
                        //    ctrl.hide();
                    }
                    ctrl.hide();
                }
                if (nv != null) {
                    if (!ctrl.getSideShapes().isEmpty()) {
                        addShapeMouseEventHandlers();
                    }
                    nv.boundsInParentProperty().addListener(boundsListener);
                    nv.layoutBoundsProperty().addListener(boundsListener);

                    nv.layoutXProperty().addListener(layoutXYListener);
                    nv.layoutYProperty().addListener(layoutXYListener);

                    nv.localToSceneTransformProperty().addListener(localToSceneTransformListener);
                    if (nv instanceof Region) {
                        ((Region) nv).backgroundProperty().addListener(backgroundListener);
                    }
                } else if (!ctrl.getSideShapes().isEmpty()) {
                    removeShapeMouseEventHandlers();
                }
                if (nv == null) {
                    Selection sel = DockRegistry.lookup(Selection.class);
                    if (sel != null) {
                        //    sel.setSelected(null);
                    } else {
                        //    ctrl.hide();
                    }

                    ctrl.hide();
                } else {
                    Selection sel = DockRegistry.lookup(Selection.class);
                    if (sel != null) {
                        //    sel.setSelected(nv);
                    } else {
                        //    ctrl.show();
                    }

                    ctrl.show();
                    if (ctrl.getBoundNode().getScene() != null && ctrl.getBoundNode().getScene().getWindow() != null) {
                        Bounds bounds = ctrl.getBoundNode().localToScene(ctrl.getBoundNode().getBoundsInLocal());
                        adjustBoundsToNode();
                    }
                }

            };
            ctrl.boundNodeProperty().addListener(boundNodeListener);
        }

        protected void adjustBoundsToNode__OLD() {
            Insets ins = null;
            if (ctrl.getBoundNode() != null && (ctrl.getBoundNode() instanceof Region) && ((Region) ctrl.getBoundNode()).getBackground() != null) {
                ins = ((Region) ctrl.getBoundNode()).getBackground().getOutsets();
            }
            //20.01.2019this.adjustBoundsToNode(ins);
        }

        //20.01.2019protected void adjustBoundsToNode(Insets insets) {
        protected void adjustBoundsToNode() {
            rect.setX(0);
            rect.setY(0);
            rect.setX(0);
            rect.setY(0);
            if (ctrl.getBoundNode() == null) {
                return;
            }
            if (ctrl.getBoundNode() != null) {

                Bounds nodeBounds = ctrl.getBoundNode().localToScene(ctrl.getBoundNode().getBoundsInLocal());
                
                Bounds rootBounds = ctrl.getParent().localToScene(ctrl.getParent().getBoundsInLocal());
                Bounds ctrlBounds;// = ctrl.localToScene(ctrl.getBoundsInLocal());
                ctrl.relocate(nodeBounds.getMinX() - rootBounds.getMinX(), nodeBounds.getMinY() - rootBounds.getMinY());
                //
                // Now we must take into account the fact that the frame rect 
                // may have stroke
                //
                if (ctrl.isEnableResize()) {
                    ctrlBounds = ctrl.localToScene(ctrl.getBoundsInLocal());
                    double xd = nodeBounds.getMinX() - ctrlBounds.getMinX();
                    double yd = nodeBounds.getMinY() - ctrlBounds.getMinY();
                    if (xd != 0 || yd != 0) {
                        ctrl.relocate(nodeBounds.getMinX() - rootBounds.getMinX() + xd, nodeBounds.getMinY() - rootBounds.getMinY() + yd);
                    }
                } else {
                    ctrl.relocate(nodeBounds.getMinX(), nodeBounds.getMinY());
                }

            }
            //Bounds sceneB = ctrl.getBoundNode().localToScene(ctrl.getBoundNode().getLayoutBounds());
            Bounds sceneB = ctrl.getBoundNode().localToScene(ctrl.getBoundNode().getBoundsInLocal());
//            double insw = insets != null ? insets.getLeft() + insets.getRight() : 0;
//            double insh = insets != null ? insets.getTop() + insets.getBottom() : 0;

            //20.01.2019rect.setWidth(sceneB.getWidth() + insw);
            //20.01.2019rect.setHeight(sceneB.getHeight() + insh);
            rect.setWidth(sceneB.getWidth());
            rect.setHeight(sceneB.getHeight());
            
            ctrl.toFront();
            rect.toFront();
        }


        protected void addShapeMouseEventHandlers() {
            ctrl.getSideShapes().forEach((k, v) -> {
                addShapeMouseEventHandlers(v);
            });

        }

        protected void removeShapeMouseEventHandlers() {
            ctrl.getSideShapes().forEach((k, v) -> {
                removeShapeMouseEventHandlers(v);
            });
        }

        protected void addShapeMouseEventHandlers(ResizeShape shape) {
            shape.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseHandler);
            shape.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseHandler);
            shape.addEventFilter(MouseEvent.MOUSE_MOVED, mouseHandler);
            shape.addEventFilter(MouseEvent.MOUSE_EXITED, mouseHandler);
            shape.addEventFilter(MouseEvent.DRAG_DETECTED, mouseHandler);
        }

        protected void removeShapeMouseEventHandlers(ResizeShape shape) {
            shape.removeEventFilter(MouseEvent.MOUSE_PRESSED, mouseHandler);
            shape.removeEventFilter(MouseEvent.MOUSE_RELEASED, mouseHandler);
            shape.removeEventFilter(MouseEvent.MOUSE_MOVED, mouseHandler);
            shape.removeEventFilter(MouseEvent.MOUSE_EXITED, mouseHandler);
            shape.removeEventFilter(MouseEvent.DRAG_DETECTED, mouseHandler);
        }

        @Override
        protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
            return pane.minWidth(height);
        }

        @Override
        protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
            return pane.minHeight(width);
        }

        @Override
        protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
            return pane.prefWidth(height) + leftInset + rightInset;
        }

        @Override
        protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
            return pane.prefHeight(width) + topInset + bottomInset;
        }

        @Override
        protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
            return computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
        }

        @Override
        protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
            return computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
        }

        @Override
        protected void layoutChildren(double x, double y, double w, double h) {
            if (ctrl.getBoundNode() != null) {
                rect.setX(0);
                rect.setY(0);
            } else {
                pane.setLayoutX(0);
                pane.setLayoutY(0);
                rect.setX(0);
                rect.setY(0);
            }
        }

    }//skin

    public static class MouseEventHandler implements EventHandler<MouseEvent> {

        private SelectionFrame framePane;

        public MouseEventHandler(SelectionFrame frameRect) {
            this.framePane = frameRect;
        }

        public void handle(MouseEvent ev, ResizeShape shape, Cursor c) {
            if (ev.getEventType() == MouseEvent.MOUSE_MOVED) {
                shape.getScene().setCursor(c);
            } else if (ev.getEventType() == MouseEvent.MOUSE_EXITED) {
                shape.getScene().setCursor(Cursor.DEFAULT);
            } else if (ev.getEventType() == MouseEvent.MOUSE_PRESSED) {
                removeMouseExitedListener(shape);
                framePane.setStartMousePos(new Point2D(ev.getScreenX(), ev.getScreenY()));
            } else if (ev.getEventType() == MouseEvent.DRAG_DETECTED) {
                WindowNodeFraming wnf = DockRegistry.getInstance().lookup(WindowNodeFraming.class);
                Node node = framePane.getBoundNode();
                NodeFraming nf = DockRegistry.lookup(NodeFraming.class);
                if (nf != null) {
                    nf.hide();
                }
                nf.showParent(node);
                //Platform.runLater(() -> {
                    wnf.show(node);
                    wnf.redirectMouseEvents(ev, framePane.getStartMousePos(), framePane);
                //});
            } else if (ev.getEventType() == MouseEvent.MOUSE_RELEASED) {
                shape.getScene().setCursor(Cursor.DEFAULT);
                addMouseExitedListener(shape);
            }
        }

        @Override
        public void handle(MouseEvent ev) {
            ResizeShape shape = (ev.getSource() instanceof ResizeShape) ? (ResizeShape) ev.getSource() : null;
            if (shape == null) {
                return;
            }
            if (shape == framePane.getSideShapes().get(nShape)) {
                handle(ev, framePane.getSideShapes().get(nShape), Cursor.N_RESIZE);

            } else if (shape == framePane.getSideShapes().get(neShape)) {
                handle(ev, framePane.getSideShapes().get(neShape), Cursor.NE_RESIZE);
            } else if (shape == framePane.getSideShapes().get(eShape)) {
                handle(ev, framePane.getSideShapes().get(eShape), Cursor.E_RESIZE);
            } else if (shape == framePane.getSideShapes().get(seShape)) {
                handle(ev, framePane.getSideShapes().get(seShape), Cursor.SE_RESIZE);
            } else if (shape == framePane.getSideShapes().get(sShape)) {
                handle(ev, framePane.getSideShapes().get(sShape), Cursor.S_RESIZE);
            } else if (shape == framePane.getSideShapes().get(swShape)) {
                handle(ev, framePane.getSideShapes().get(swShape), Cursor.SW_RESIZE);
            } else if (shape == framePane.getSideShapes().get(wShape)) {
                handle(ev, framePane.getSideShapes().get(wShape), Cursor.W_RESIZE);
            } else if (shape == framePane.getSideShapes().get(nwShape)) {
                handle(ev, framePane.getSideShapes().get(nwShape), Cursor.NW_RESIZE);
            }
            ev.consume();
        }

        protected void removeMouseExitedListener(ResizeShape shape) {
            shape.removeEventFilter(MouseEvent.MOUSE_EXITED, this);
        }

        protected void addMouseExitedListener(ResizeShape shape) {
            shape.addEventFilter(MouseEvent.MOUSE_EXITED, this);
        }
    }
}
