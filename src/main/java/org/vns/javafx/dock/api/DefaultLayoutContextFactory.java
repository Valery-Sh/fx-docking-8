/*
 * Copyright 2017 Your Organisation.
 *
 * Licensed under the Apache License, Verion 2.0 (the "License");
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
package org.vns.javafx.dock.api;

import org.vns.javafx.ContextLookup;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.ConstraintsBase;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.JdkUtil;
import org.vns.javafx.dock.api.Constraints.GridPaneConstraints;
import static org.vns.javafx.dock.api.LayoutContext.getValue;
import org.vns.javafx.dock.api.selection.GridSelectionFrame;
import org.vns.javafx.dock.api.selection.ObjectFraming;
import org.vns.javafx.dock.api.selection.ObjectFramingProvider;
import org.vns.javafx.dock.api.indicator.IndicatorPopup;
import org.vns.javafx.dock.api.indicator.IndicatorPopup.KeysDown;
import org.vns.javafx.dock.api.indicator.PositionIndicator;

/**
 *
 * @author Valery
 */
public class DefaultLayoutContextFactory extends LayoutContextFactory {

    @Override
    public LayoutContext getContext(Node targetNode) {
        if (DockRegistry.isDockLayout(targetNode)) {
            return DockRegistry.dockLayout(targetNode).getLayoutContext();
        }
        LayoutContext retval = null;
        if (targetNode instanceof StackPane) {
            retval = getStackPaneContext((StackPane) targetNode);
        } else if (targetNode instanceof VBox) {
            retval = new ListBasedTargetContext(targetNode);
        } else if (targetNode instanceof HBox) {
            retval = new ListBasedTargetContext(targetNode);
        } else if (targetNode instanceof BorderPane) {
            retval = new DockBorderPaneContext(targetNode);

            for (Node obj : ((BorderPane) targetNode).getChildren()) {
                DockableContext dc = null;
                if (Dockable.of(obj) != null) {
                    dc = Dockable.of(obj).getContext();
                } else {
                    dc = DockRegistry.makeDockable(obj).getContext();
                }
                dc.setLayoutContext(retval);

            }
        } else if ((targetNode instanceof SplitPane && !(targetNode instanceof DockSplitPane))) {
            retval = new ListBasedTargetContext(targetNode);
        } else if (targetNode instanceof FlowPane) {
            retval = new ListBasedTargetContext(targetNode);
        } else if (targetNode instanceof TabPane) {
            retval = new DockTabPane2Context(targetNode);
        } else if (targetNode instanceof TextFlow) {
            retval = new ListBasedTargetContext(targetNode);
        } else if (targetNode instanceof AnchorPane) {
            retval = new ListBasedTargetContext(targetNode);
        } else if (targetNode instanceof GridPane) {
            retval = getGridPaneContext((GridPane) targetNode);
            retval.getLookup().putUnique(ConstraintsFactory.class, new GridPaneConstraintsFactory());
        } else if (targetNode instanceof Pane) {
            retval = getPaneContext((Pane) targetNode);
        } else if (targetNode instanceof Accordion) {
            retval = new ListBasedTargetContext<TitledPane>(targetNode) {
                @Override
                public boolean isAcceptable(Dockable obj) {
                    boolean b = super.isAcceptable(obj);
                    if (b) {
                        Dockable dragged = obj;
                        Object v = getValue(obj);
                        if (Dockable.of(v) != null) {
                            dragged = Dockable.of(v);
                            b = (dragged.getNode() instanceof TitledPane);
                        }
                    }
                    return b;
                }
            };
        }
        return retval;
    }

    protected LayoutContext getStackPaneContext(StackPane pane) {
        LayoutContext lc = new StackPaneContext(pane);
        pane.getChildren().forEach(obj -> {
            DockableContext dc = null;
            if (Dockable.of(obj) != null) {
                dc = Dockable.of(obj).getContext();
            } else {
                dc = DockRegistry.makeDockable(obj).getContext();
            }
            dc.setLayoutContext(lc);
        });
        return lc;
    }

    protected LayoutContext getGridPaneContext(GridPane pane) {
        LayoutContext lc = new GridPaneContext(pane);
        pane.getChildren().forEach(obj -> {
            DockableContext dc = null;
            if (Dockable.of(obj) != null) {
                dc = Dockable.of(obj).getContext();
            } else {
                dc = DockRegistry.makeDockable(obj).getContext();
            }
            dc.setLayoutContext(lc);
        });
        return lc;
    }

    protected LayoutContext getPaneContext(Pane pane) {
        LayoutContext lc = new PaneContext(pane);
        pane.getChildren().forEach(obj -> {
            DockableContext dc = null;
            if (Dockable.of(obj) != null) {
                dc = Dockable.of(obj).getContext();
            } else {
                dc = DockRegistry.makeDockable(obj).getContext();
            }
            dc.setLayoutContext(lc);
        });
        return lc;

    }

    protected LayoutContext getBorderPaneContext(BorderPane pane) {
        LayoutContext lc = new DockBorderPaneContext(pane);
        pane.getChildren().forEach(obj -> {
            DockableContext dc = null;
            if (Dockable.of(obj) != null) {
                dc = Dockable.of(obj).getContext();
            } else {
                dc = DockRegistry.makeDockable(obj).getContext();
            }
            dc.setLayoutContext(lc);
        });
        return lc;
    }

    public static class StackPaneContext extends LayoutContext {

        public StackPaneContext(Node pane) {
            super(pane);
            init();
        }

        private void init() {
            ((StackPane) getLayoutNode()).getChildren().addListener(new NodeListChangeListener(this));
        }

        @Override
        protected void initLookup(ContextLookup lookup) {
            lookup.putUnique(PositionIndicator.class, new StackPositionIndicator(this));
        }

        @Override
        public void dock(Point2D mousePos, Dockable dockable) {
            Object o = getValue(dockable);
            if (o == null || Dockable.of(o) == null) {
                return;
            }

            Dockable d = Dockable.of(o);

            dockable.getContext().getLayoutContext().undock(dockable);

            Node node = d.getNode();
            Window stage = null;
            if (node.getScene() != null && node.getScene().getWindow() != null) { //&& (node.getScene().getWindow() instanceof Stage)) {
                stage = node.getScene().getWindow();
            }

            if (doDock(mousePos, d.getNode()) && stage != null) {
                //d.getContext().setFloating(false);
                if ((stage instanceof Stage)) {
                    ((Stage) stage).close();
                } else {
                    stage.hide();
                }
                d.getContext().setLayoutContext(this);
            }
        }

        protected boolean doDock(Point2D mousePos, Node node) {
            boolean retval = true;
            StackPane target = (StackPane) getLayoutNode();
            BorderPane bp = (BorderPane) getPositionIndicator().getIndicatorPane().lookup("#stack-pane-indicator");
            if (Util.contains(bp.getTop(), mousePos.getX(), mousePos.getY())) {
                target.getChildren().add(node);
                StackPane.setAlignment(node, Pos.TOP_CENTER);
            } else if (Util.contains(bp.getRight(), mousePos.getX(), mousePos.getY())) {
                target.getChildren().add(node);
                StackPane.setAlignment(node, Pos.CENTER_RIGHT);

            } else if (Util.contains(bp.getBottom(), mousePos.getX(), mousePos.getY())) {
                target.getChildren().add(node);
                StackPane.setAlignment(node, Pos.BOTTOM_CENTER);

            } else if (Util.contains(bp.getLeft(), mousePos.getX(), mousePos.getY())) {
                target.getChildren().add(node);
                StackPane.setAlignment(node, Pos.CENTER_LEFT);

            } else if (Util.contains(bp.getCenter(), mousePos.getX(), mousePos.getY())) {
                target.getChildren().add(node);
                StackPane.setAlignment(node, Pos.CENTER);
            } else {
                retval = false;
            }
            return retval;
        }

        @Override
        public void remove(Object obj) {
            if (!(obj instanceof Node)) {
                return;
            }
            Node dockNode = (Node) obj;
            ((StackPane) getLayoutNode()).getChildren().remove(dockNode);
        }

        @Override
        public boolean contains(Object obj) {
            return ((StackPane) getLayoutNode()).getChildren().contains(obj);
        }

        /**
         * For test purpose
         *
         * @return th elis of dockables
         */
        public List<Dockable> getDockables() {
            BorderPane bp = (BorderPane) getLayoutNode();
            List<Dockable> list = FXCollections.observableArrayList();
            bp.getChildren().forEach(node -> {
                if (DockRegistry.isDockable(node)) {
                    list.add(Dockable.of(node));
                }
            });
            return list;
        }
    }

    public static class StackPositionIndicator extends PositionIndicator {

        public StackPositionIndicator(LayoutContext targetContext) {
            super(targetContext);
        }

        @Override
        protected Pane createIndicatorPane() {
            Pane indicator = new Pane() {
                @Override
                public String getUserAgentStylesheet() {
                    return Dockable.class.getResource("resources/default.css").toExternalForm();
                }
            };
            indicator.setId("stackpane-ind-pane");
            indicator.getStyleClass().add("stackpane-indicator");
            return indicator;
        }

        protected boolean isControlDown() {
            if (getIndicatorPopup() == null) {
                return false;
            }
            return getIndicatorPopup().getKeysDown() == IndicatorPopup.KeysDown.CTLR_DOWN;
        }

        @Override
        protected void updateIndicatorPane() {

            Pane targetNode = (Pane) getLayoutContext().getLayoutNode();
            Label topNode = new Label("Top");
            topNode.getStyleClass().add("top");
            Label rightNode = new Label("Right");
            rightNode.getStyleClass().add("right");
            Label bottomNode = new Label("Bottom");
            bottomNode.getStyleClass().add("bottom");
            Label leftNode = new Label("Left");
            leftNode.getStyleClass().add("left");
            Label centerNode = new Label("Center");
            centerNode.getStyleClass().add("center");

            topNode.prefWidthProperty().bind(targetNode.widthProperty());
            topNode.prefHeightProperty().bind(targetNode.heightProperty().divide(4));

            rightNode.prefHeightProperty().bind(targetNode.heightProperty().divide(2));
            rightNode.prefWidthProperty().bind(targetNode.widthProperty().divide(4));

            leftNode.prefHeightProperty().bind(targetNode.heightProperty().divide(2));
            leftNode.prefWidthProperty().bind(targetNode.widthProperty().divide(4));

            bottomNode.prefWidthProperty().bind(targetNode.widthProperty());
            bottomNode.prefHeightProperty().bind(targetNode.heightProperty().divide(4));

            centerNode.prefHeightProperty().bind(targetNode.heightProperty().divide(2));
            centerNode.prefWidthProperty().bind(targetNode.widthProperty().divide(2));

            Bounds bnd = targetNode.localToScene(targetNode.getBoundsInLocal());
            if (!isControlDown()) {
                bnd = Util.sceneIntersection(targetNode);
            }
            updateSnapshot(isControlDown());

            BorderPane indicator = new BorderPane(centerNode, topNode, rightNode, bottomNode, leftNode) {
                @Override
                public String getUserAgentStylesheet() {
                    return Dockable.class.getResource("resources/default.css").toExternalForm();
                }
            };
            indicator.setId("stack-pane-indicator");
            indicator.getStyleClass().add("stack-pane-indicator");

            Node child = getIndicatorPane().lookup("#stack-pane-indicator");
            getIndicatorPane().getChildren().remove(child);
            getIndicatorPane().getChildren().add(indicator);
            indicator.setManaged(false);

            topNode.setAlignment(Pos.CENTER);
            rightNode.setAlignment(Pos.CENTER);
            bottomNode.setAlignment(Pos.CENTER);
            leftNode.setAlignment(Pos.CENTER);
            centerNode.setAlignment(Pos.CENTER);

            indicator.setPrefWidth(bnd.getWidth());
            indicator.setPrefHeight(bnd.getHeight());
            indicator.setMinWidth(bnd.getWidth());
            indicator.setMinHeight(bnd.getHeight());

            Bounds indPaneBnd = getIndicatorPane().localToScreen(getIndicatorPane().getBoundsInLocal());
            Insets ins = getIndicatorPane().getInsets();

            double dx = 0;
            double dy = 0;

            if (targetNode.getScene() != null && targetNode.getScene().getWindow() != null && indPaneBnd != null) {
                if (isControlDown()) {
                    dx = ins.getLeft();
                    dy = ins.getTop();
                } else {
                    dx = targetNode.getScene().getWindow().getX() + targetNode.getScene().getX() + bnd.getMinX() - indPaneBnd.getMinX();
                    dy = targetNode.getScene().getWindow().getY() + targetNode.getScene().getY() + bnd.getMinY() - indPaneBnd.getMinY();
                }
            }

            indicator.setLayoutX(dx);
            indicator.setLayoutY(dy);

        }

        @Override
        public void showDockPlace(double x, double y) {

            boolean visible = true;

            BorderPane bp = (BorderPane) getIndicatorPane().lookup("#stack-pane-indicator");

            if (Util.contains(bp.getTop(), x, y)) {
                adjustPlace(bp.getTop());
            } else if (Util.contains(bp.getRight(), x, y)) {
                adjustPlace(bp.getRight());
            } else if (Util.contains(bp.getBottom(), x, y)) {
                adjustPlace(bp.getBottom());
            } else if (Util.contains(bp.getLeft(), x, y)) {
                adjustPlace(bp.getLeft());
            } else if (Util.contains(bp.getCenter(), x, y)) {
                adjustPlace(bp.getCenter());
            } else {
                visible = false;
            }

            getDockPlace().setVisible(visible);
        }

        private void adjustPlace(Node node) {
            Rectangle r = (Rectangle) getDockPlace();
            r.setHeight(((Region) node).getHeight());
            r.setWidth(((Region) node).getWidth());
            r.setX(((Region) node).getLayoutX());
            r.setY(((Region) node).getLayoutY());
        }

    }//stackpane positionIndicator

    public static class PaneContext extends LayoutContext {

        public PaneContext(Node pane) {
            super(pane);
            init();
        }

        private void init() {
            ((Pane) getLayoutNode()).getChildren().addListener(new NodeListChangeListener(this));
        }

        @Override
        protected void initLookup(ContextLookup lookup) {
            lookup.putUnique(PositionIndicator.class, new PanePositionIndicator(this));
        }

        @Override
        public void dock(Point2D mousePos, Dockable dockable) {
            Object o = getValue(dockable);
            if (o == null || Dockable.of(o) == null) {
                return;
            }

            Dockable d = Dockable.of(o);
            //
            // Test is we drag dockable or the value of a dragContainer 
            //
            dockable.getContext().getLayoutContext().undock(dockable);

            Node node = d.getNode();
            Window stage = null;
            if (node.getScene() != null && node.getScene().getWindow() != null) { //&& (node.getScene().getWindow() instanceof Stage)) {
                stage = node.getScene().getWindow();
            }

            if (doDock(mousePos, d.getNode()) && stage != null) {
                if ((stage instanceof Stage)) {
                    ((Stage) stage).close();
                } else {
                    stage.hide();
                }
                d.getContext().setLayoutContext(this);
            }
        }

        protected boolean doDock(Point2D mousePos, Node node) {
            boolean retval = true;
            Pane pane = (Pane) getLayoutNode();

//            double x = node.getScene().getWindow().getX();
//            double y = node.getScene().getWindow().getY();
            double x = mousePos.getX();
            double y = mousePos.getY();

            if (!Util.contains(getLayoutNode(), x, y)) {
                return false;
            }

            Point2D pt = pane.screenToLocal(x, y);
            pane.getChildren().add(node);
            node.relocate(pt.getX(), pt.getY());

            return retval;
        }

        @Override
        public void remove(Object obj) {
            if (!(obj instanceof Node)) {
                return;
            }
            Node dockNode = (Node) obj;
            ((Pane) getLayoutNode()).getChildren().remove(dockNode);
        }

        @Override
        public boolean contains(Object obj) {
            return ((Pane) getLayoutNode()).getChildren().contains(obj);
        }

        /**
         * For test purpose
         *
         * @return th elis of dockables
         */
        /*        public List<Dockable> getDockables() {
            BorderPane bp = (BorderPane) getLayoutNode();
            List<Dockable> list = FXCollections.observableArrayList();
            bp.getChildren().forEach(node -> {
                if (DockRegistry.isDockable(node)) {
                    list.add(DockRegistry.dockable(node));
                }
            });
            return list;
        }
         */
    }

    public static class PanePositionIndicator extends PositionIndicator {

        public PanePositionIndicator(LayoutContext targetContext) {
            super(targetContext);
        }

        @Override
        protected Pane createIndicatorPane() {
            Pane indicator = new Pane() {
                @Override
                public String getUserAgentStylesheet() {
                    return Dockable.class.getResource("resources/default.css").toExternalForm();
                }
            };
            indicator.getStyleClass().add("pane-indicator");
            return indicator;
        }

        @Override
        public void showDockPlace(double x, double y) {

            boolean visible = true;
            Pane p = (Pane) getIndicatorPane();

            if (getIndicatorPopup().getDraggedNode() != null) {
                x = getIndicatorPopup().getDraggedNode().getScene().getWindow().getX();
                y = getIndicatorPopup().getDraggedNode().getScene().getWindow().getY();
                //Node n = getIndicatorPopup().getDraggedNode();
                //Bounds b = n.localToScreen(n.getBoundsInLocal());
                //Bounds b = n.localToScreen(n.getBoundsInLocal());
            }
            if (Util.contains(p, x, y)) {
                adjustPlace(p, x, y);
            } else {
                visible = false;
            }
            getDockPlace().setVisible(visible);
        }

        /*        private void adjustPlace(Node node) {
            Rectangle r = (Rectangle) getDockPlace();
            r.setHeight(((Region) node).getHeight());
            r.setWidth(((Region) node).getWidth());
            r.setX(((Region) node).getLayoutX());
            r.setY(((Region) node).getLayoutY());
        }
         */
        private void adjustPlace(Pane pane, double x, double y) {
            Rectangle r = (Rectangle) getDockPlace();
            Point2D pt = pane.screenToLocal(x, y);
            Node draggedNode = getIndicatorPopup().getDraggedNode();
            if (draggedNode != null) {
                r.setWidth(draggedNode.getScene().getWindow().getWidth());
                r.setHeight(draggedNode.getScene().getWindow().getHeight());
                pt = pane.screenToLocal(draggedNode.getScene().getWindow().getX(), draggedNode.getScene().getWindow().getY());
                r.setX(pt.getX());
                r.setY(pt.getY());

            } else {
                r.setHeight(20);
                r.setWidth(75);
                r.setX(pt.getX());
                r.setY(pt.getY());
            }
        }

    }

    public static class ListBasedTargetContext<T extends Node> extends LayoutContext {

        private boolean listBased;
        private ObservableList<T> items;

        public ListBasedTargetContext(Node pane) {
            super(pane);
            init();
        }

        @Override
        protected void initLookup(ContextLookup lookup) {
            lookup.putUnique(PositionIndicator.class, new ListBasedPositionIndicator(this));
        }

        public boolean isListBased() {
            return listBased;
        }

        public ObservableList<T> getItems() {
            return items;
        }

        protected void setItems(ObservableList<T> items) {
            this.items = items;
        }

        protected ObservableList<T> getNodeList(Node node) {
            if ((getLayoutNode() instanceof Pane)) {
                return (ObservableList<T>) ((Pane) getLayoutNode()).getChildren();
            }
            if ((getLayoutNode() instanceof Accordion)) {
                return (ObservableList<T>) ((Accordion) getLayoutNode()).getPanes();
            }

            ObservableList<T> retval = null;

            Class<?> clazz = node.getClass();
            DefaultProperty a = clazz.getAnnotation(DefaultProperty.class);
            if (a == null) {
                return null;
            }
            String value = a.value();

            String methodName = "get" + value.substring(0, 1).toUpperCase() + value.substring(1);

            try {
                Method method = clazz.getMethod(methodName, new Class[0]);
                //if (method.getReturnType().isInstance(retval)) {
                retval = (ObservableList<T>) method.invoke(node);
                //}
            } catch (NoSuchMethodException | SecurityException ex) {
                return null;
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(DefaultLayoutContextFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
            return retval;
        }

        private void init() {
            listBased = true;
            items = getNodeList(getLayoutNode());
            items.addListener(new NodeListChangeListener(this));
            items.forEach(it -> {
                DockableContext dc = null;
                if (Dockable.of(dc) != null) {
                    dc = Dockable.of(it).getContext();
                    dc.setLayoutContext(this);
                } else {
                    DockRegistry.makeDockable(it);
                    if (Dockable.of(it) != null) {
                        Dockable.of(it).getContext().setLayoutContext(this);
                    }
                }

            });
        }

        @Override
        public void dock(Point2D mousePos, Dockable dockable) {
            Object o = getValue(dockable);
            if (o == null || Dockable.of(o) == null) {
                return;
            }

            Dockable d = Dockable.of(o);

            Node node = d.getNode();
            Window stage = null;
            if (node.getScene() != null && node.getScene().getWindow() != null) { //&& (node.getScene().getWindow() instanceof Stage)) {
                stage = node.getScene().getWindow();
            }

            if (doDock(mousePos, d.getNode()) && stage != null) {
                if ((stage instanceof Stage)) {
                    ((Stage) stage).close();
                } else {
                    stage.hide();
                }
                d.getContext().setLayoutContext(this);
            }
        }

        protected boolean doDock(Point2D mousePos, Node node) {
            boolean retval = true;
            Node targetNode = getLayoutNode();

            if (!Util.contains(targetNode, mousePos.getX(), mousePos.getY())) {
                return false;
            }
            int idx = -1;
            Node innerNode = Util.getTop(targetNode, mousePos.getX(), mousePos.getY(), n -> {
                return getItems().contains(n);
            });
            if (innerNode != null) {
                idx = getItems().indexOf(innerNode);
            }
            /*            for (int i = 0; i < items.size(); i++) {
                innerNode = items.get(i);
                if (DockUtil.contains(innerNode, mousePos.getX(), mousePos.getY())) {
                    idx = i;
                    break;
                }
            }
             */
            if (idx == -1) {
                items.add((T) node);
            } else if (((targetNode instanceof VBox) || ((targetNode instanceof Accordion)))) {
                Bounds b = Util.getHalfBounds(Side.TOP, innerNode, mousePos.getX(), mousePos.getY());
                if (b != null && b.contains(mousePos)) {
                    items.add(idx, (T) node);
                } else {
                    b = Util.getHalfBounds(Side.BOTTOM, innerNode, mousePos.getX(), mousePos.getY());
                    if (b != null && b.contains(mousePos)) {
                        items.add(idx + 1, (T) node);
                    }
                }
            } else if (targetNode instanceof HBox) {
                Bounds b = Util.getHalfBounds(Side.LEFT, innerNode, mousePos.getX(), mousePos.getY());
                if (b != null && b.contains(mousePos)) {
                    items.add(idx, (T) node);
                } else {
                    b = Util.getHalfBounds(Side.RIGHT, innerNode, mousePos.getX(), mousePos.getY());
                    if (b != null && b.contains(mousePos)) {
                        items.add(idx + 1, (T) node);
                    }
                }
            } else {
            }
            return retval;
        }

        @Override
        public void remove(Object obj) {
            if (!(obj instanceof Node)) {
                return;
            }
            Node dockNode = (Node) obj;
            items.remove((T) dockNode);
        }

        @Override
        public boolean contains(Object obj) {
            return items.contains((T) obj);
        }

    }

    public static class ListBasedPositionIndicator extends PositionIndicator {

        public ListBasedPositionIndicator(LayoutContext targetContext) {
            super(targetContext);
        }

        @Override
        protected void updateIndicatorPane() {
            if (getIndicatorPopup() != null && getIndicatorPopup().getKeysDown() == KeysDown.CTLR_DOWN) {
                updateSnapshot(true);
            } else if (getIndicatorPopup() != null) {
                updateSnapshot(false);
            }
        }

        @Override
        public IndicatorPopup getIndicatorPopup() {
            IndicatorPopup ip = super.getIndicatorPopup();
//            ((Region) ip.getTargetNode()).layout();
//            ((Region) ip.getTargetNode()).requestLayout();
            return ip;
        }

        @Override
        protected Pane createIndicatorPane() {

            Pane indicator = new Pane() {
                @Override
                public String getUserAgentStylesheet() {
                    return Dockable.class.getResource("resources/default.css").toExternalForm();
                }
            };
            indicator.setId("list-ind-pane");
            indicator.getStyleClass().add("list-based-indicator");
            return indicator;
        }

        @Override
        public void showDockPlace(double x, double y) {
            
            boolean visible = true;

            Pane p = (Pane) getIndicatorPane();
            if (Util.contains(p, x, y)) {

                adjustPlace(p, x, y);
            } else {
                visible = false;
            }
            getDockPlace().setVisible(visible);
        }

        protected void adjustPlace(Node node) {

            Pane p = getIndicatorPane();

            Rectangle r = (Rectangle) getDockPlace();
            r.setHeight(((Region) node).getHeight());
            r.setWidth(((Region) node).getWidth());

            r.setX(((Region) node).getLayoutX());
            r.setY(((Region) node).getLayoutY());

        }

        protected void adjustPlace(Node indPane, double x, double y) {
            Rectangle r = (Rectangle) getDockPlace();
            ListBasedTargetContext ctx = (ListBasedTargetContext) getLayoutContext();
            Region targetPane = (Region) ctx.getLayoutNode();

            Node innerNode = Util.getTop(ctx.getLayoutNode(), x, y, n -> {
                return ctx.getItems().contains(n);
            });
            //
            // We know that the indicatore pate never transformed
            //
            Bounds indBounds = indPane.getBoundsInLocal();
            Insets ins = getIndicatorPane().getInsets();

            if (innerNode != null) {
                Bounds screenBounds = innerNode.localToScreen(innerNode.parentToLocal(innerNode.getBoundsInParent()));
                Bounds nodeBounds = indPane.screenToLocal(screenBounds);

                if ((targetPane instanceof VBox) || (targetPane instanceof Accordion)) {
                    r.setX(ins.getLeft());
                    r.setWidth(indBounds.getWidth() - ins.getLeft() - ins.getRight());

                    if (nodeBounds.getHeight() < 20) {
                        r.setHeight(nodeBounds.getHeight() / 2);
                    } else {
                        r.setHeight(10);
                    }
                    if (y < screenBounds.getMinY() + screenBounds.getHeight() / 2) {
                        r.setY(nodeBounds.getMinY());
                    } else if (nodeBounds.getHeight() < 20) {
                        r.setY(nodeBounds.getMinY() + nodeBounds.getHeight() / 2);
                    } else {
                        r.setY(nodeBounds.getMinY() + nodeBounds.getHeight() - 10);
                    }

                } else if (targetPane instanceof HBox) {
                    r.setHeight(indBounds.getHeight() - ins.getTop() - ins.getBottom());
                    r.setY(ins.getTop());
                    if (nodeBounds.getWidth() < 20) {
                        r.setWidth(nodeBounds.getWidth() / 2);
                    } else {
                        r.setWidth(10);
                    }
                    if (x < screenBounds.getMinX() + nodeBounds.getWidth() / 2) {
                        r.setX(nodeBounds.getMinX());
                    } else if (nodeBounds.getWidth() < 20) {
                        r.setX(nodeBounds.getMinX() + (nodeBounds.getWidth() / 2));
                    } else {
                        r.setX(nodeBounds.getMinX() + nodeBounds.getWidth() - 10);
                    }
                } else {
                    r.setWidth(nodeBounds.getWidth());
                    r.setHeight(nodeBounds.getHeight());

                    r.setX(nodeBounds.getMinX());
                    r.setY(nodeBounds.getMinY());
                }
            }
        }

    }

    public static class NodeListChangeListener implements ListChangeListener<Node> {

        private final LayoutContext context;

        public NodeListChangeListener(LayoutContext context) {
            this.context = context;
        }

        @Override
        public void onChanged(ListChangeListener.Change<? extends Node> change) {
            while (change.next()) {
                if (change.wasRemoved()) {
                    List<? extends Node> list = change.getRemoved();

                    for (Node d : list) {
                        if (DockRegistry.isDockable(d)) {
                            context.undock(Dockable.of(d));
                        }
                    }
                }
                if (change.wasAdded()) {
                    List<? extends Node> list = change.getAddedSubList();
                    for (Node n : list) {
                        if (Dockable.of(n) != null) {
                            context.commitDock(n);
                        }
                    }
                }
            }//while
        }
    }

    public static class GridPaneContext extends LayoutContext {

        public GridPaneContext(Node pane) {
            super(pane);
            init();
        }

        private void init() {
            ((GridPane) getLayoutNode()).getChildren().addListener(new NodeListChangeListener(this));
        }

        @Override
        protected void initLookup(ContextLookup lookup) {
            lookup.putUnique(PositionIndicator.class, new GridPositionIndicator(this));
            lookup.putUnique(ObjectFramingProvider.class, new GridObjectFramingProvider((GridPane) getLayoutNode()));
        }

        @Override
        public void dock(Point2D mousePos, Dockable dockable) {
            Object o = getValue(dockable);
            if (o == null || Dockable.of(o) == null) {
                return;
            }

            Dockable d = Dockable.of(o);

            dockable.getContext().getLayoutContext().undock(dockable);

            Node node = d.getNode();
            Window stage = null;
            if (node.getScene() != null && node.getScene().getWindow() != null) { //&& (node.getScene().getWindow() instanceof Stage)) {
                stage = node.getScene().getWindow();
            }

            if (doDock(mousePos, d.getNode()) && stage != null) {
                //d.getContext().setFloating(false);
                if ((stage instanceof Stage)) {
                    ((Stage) stage).close();
                } else {
                    stage.hide();
                }
                d.getContext().setLayoutContext(this);
            }
        }

        public static int getComputedColumn(GridPaneContext context, int row, double screenX) {
            int retval = -1;
            Pane ip = context.getPositionIndicator().getIndicatorPane();
            GridPane gp = (GridPane) ip.lookup("#grid-pane-indicator");

            if (row >= 0) {
                int[] dim = JdkUtil.getGridDimensions(gp);
                if (dim[0] > 0) {
                    Bounds b = JdkUtil.getGridCellBounds(gp, dim[0] - 1, row);
                    Bounds sb = gp.localToScreen(b);
                    Bounds gpb = gp.localToScreen(gp.getBoundsInLocal());
                    if (screenX > sb.getMinX() + sb.getWidth() && screenX < gpb.getMinX() + gpb.getWidth()) {
                        retval = dim[0];
                    }
                }
            }
            return retval;
        }

        public static int getComputedRow(GridPaneContext context, int col, double screenY) {
            int retval = -1;
            Pane ip = context.getPositionIndicator().getIndicatorPane();
            GridPane gp = (GridPane) ip.lookup("#grid-pane-indicator");

            if (col >= 0) {
                int[] dim = JdkUtil.getGridDimensions(gp);
                if (dim[1] > 0) {
                    Bounds b = JdkUtil.getGridCellBounds(gp, col, dim[1] - 1);
                    Bounds sb = gp.localToScreen(b);
                    Bounds gpb = gp.localToScreen(gp.getBoundsInLocal());
                    if (screenY > sb.getMinY() + sb.getHeight() && screenY < gpb.getMinY() + gpb.getHeight()) {
                        retval = dim[1];
                    }
                }
            }
            return retval;
        }

        protected static int getRowIndex(GridPane grid, double screenX, double screenY) {
            int retval = -1;
            int[] dim = JdkUtil.getGridDimensions(grid);
            for (int i = 0; i < dim[1]; i++) {
                Bounds cellBnd = JdkUtil.getGridCellBounds(grid, 0, i);
                if ( cellBnd == null ) {
                    continue;
                }
                Bounds gridBnd = grid.localToScreen(grid.getBoundsInLocal());
                if (screenY >= gridBnd.getMinY() + cellBnd.getMinY() && screenY <= gridBnd.getMinY() + cellBnd.getMinY() + cellBnd.getHeight()) {
                    retval = i;
                    break;
                }
            }
            return retval;
        }

        protected static int getColumnIndex(GridPane grid, double screenX, double screenY) {
            int retval = -1;
            int[] dim = JdkUtil.getGridDimensions(grid);
            for (int i = 0; i < dim[0]; i++) {
                Bounds cellBnd = JdkUtil.getGridCellBounds(grid, i, 0);
                if ( cellBnd == null ) {
                    continue;
                }
                Bounds gridBnd = grid.localToScreen(grid.getBoundsInLocal());
                if (screenX >= gridBnd.getMinX() + cellBnd.getMinX() && screenX <= gridBnd.getMinX() + cellBnd.getMinX() + cellBnd.getWidth()) {
                    retval = i;
                    break;
                }
            }
            return retval;
        }

        protected boolean doDock(Point2D mousePos, Node node) {
            boolean retval = true;
            GridPane target = (GridPane) getLayoutNode();
            GridPane bp = (GridPane) getPositionIndicator().getIndicatorPane().lookup("#grid-pane-indicator");
            int row = getRowIndex(bp, mousePos.getX(), mousePos.getY());
            int col = getColumnIndex(bp, mousePos.getX(), mousePos.getY());
            if (row >= 0 && col >= 0) {
                target.add(node, col, row);
            } else if (row >= 0) {
                col = getComputedColumn(this, row, mousePos.getX());
                if (col >= 0) {
                    target.add(node, col, row);
                }
            } else if (col >= 0) {
                row = getComputedRow(this, col, mousePos.getY());
                if (row >= 0) {
                    target.add(node, col, row);
                }
            } else {
                retval = false;
            }
            return retval;
        }

        @Override
        public void remove(Object obj) {
            if (!(obj instanceof Node)) {
                return;
            }
            Node dockNode = (Node) obj;
            ((GridPane) getLayoutNode()).getChildren().remove(dockNode);
        }

        @Override
        public boolean contains(Object obj) {
            return ((GridPane) getLayoutNode()).getChildren().contains(obj);
        }

        /**
         * For test purpose
         *
         * @return th elis of dockables
         */
        /*        public List<Dockable> getDockables() {
            BorderPane bp = (BorderPane) getLayoutNode();
            List<Dockable> list = FXCollections.observableArrayList();
            bp.getChildren().forEach(node -> {
                if (DockRegistry.isDockable(node)) {
                    list.add(Dockable.of(node));
                }
            });
            return list;
        }
         */
    }

    public static class GridPositionIndicator extends PositionIndicator {

        public GridPositionIndicator(LayoutContext targetContext) {
            super(targetContext);
        }

        @Override
        protected Pane createIndicatorPane() {

            Pane indicator = new Pane() {
                @Override
                public String getUserAgentStylesheet() {
                    return Dockable.class.getResource("resources/default.css").toExternalForm();
                }
            };
            indicator.setId("gridpane-ind-pane");
            indicator.getStyleClass().add("grid-pane-indicator-pane");
            GridPane grid = new GridPane() {
                @Override
                public String getUserAgentStylesheet() {
                    return Dockable.class.getResource("resources/default.css").toExternalForm();
                }
            };

            grid.setId("grid-pane-indicator");
            grid.getStyleClass().add("grid-pane-indicator");
            grid.setPrefWidth(40);
            grid.setPrefHeight(60);
            indicator.getChildren().add(grid);
            return indicator;
        }

        protected boolean isControlDown() {
            if (getIndicatorPopup() == null) {
                return false;
            }
            return getIndicatorPopup().getKeysDown() == IndicatorPopup.KeysDown.CTLR_DOWN;
        }

        @Override
        protected void updateIndicatorPane() {

            Pane targetNode = (Pane) getLayoutContext().getLayoutNode();

            Bounds bnd = targetNode.localToScene(targetNode.getBoundsInLocal());
            if (!isControlDown()) {
                bnd = Util.sceneIntersection(targetNode);
            }
            if (bnd == null) {
                return;
            }
            updateSnapshot(isControlDown());

            GridPane indicator = (GridPane) getIndicatorPane().lookup("#grid-pane-indicator");
            double dw = targetNode.getInsets().getLeft() + targetNode.getInsets().getRight();
            double dh = targetNode.getInsets().getTop() + targetNode.getInsets().getBottom();

            indicator.setPrefWidth(bnd.getWidth() - dw);
            indicator.setPrefHeight(bnd.getHeight() - dh);
            indicator.setMinWidth(bnd.getWidth() - dw);
            indicator.setMinHeight(bnd.getHeight() - dh);
            indicator.setMaxWidth(bnd.getWidth() - dw);
            indicator.setMaxHeight(bnd.getHeight() - dh);

            indicator.setGridLinesVisible(true);

            Bounds indPaneBnd = getIndicatorPane().localToScreen(getIndicatorPane().getBoundsInLocal());
            double dx = 0;
            double dy = 0;
            Insets ins = targetNode.getInsets();
            if (targetNode.getScene() != null && targetNode.getScene().getWindow() != null && indPaneBnd != null) {
                if (isControlDown()) {
                    dx = ins.getLeft();
                    dy = ins.getTop();
                } else {
                    dx = targetNode.getScene().getWindow().getX() + targetNode.getScene().getX() + bnd.getMinX() - indPaneBnd.getMinX() + ins.getLeft();
                    dy = targetNode.getScene().getWindow().getY() + targetNode.getScene().getY() + bnd.getMinY() - indPaneBnd.getMinY() + ins.getTop();
                }
            }

            indicator.setLayoutX(dx);
            indicator.setLayoutY(dy);
        }

        @Override
        public void showDockPlace(double x, double y) {

            boolean visible = true;

            GridPane gp = (GridPane) getIndicatorPane().lookup("#grid-pane-indicator");

            adjustConstraints(gp);

            int row = GridPaneContext.getRowIndex(gp, x, y);
            int col = GridPaneContext.getColumnIndex(gp, x, y);
            if (row >= 0 && col >= 0) {
                adjustPlace(gp, col, row);
            } else if (row >= 0) {
                int[] dim = JdkUtil.getGridDimensions(gp);
                if (dim[0] > 0) {
                    Bounds b = JdkUtil.getGridCellBounds(gp, dim[0] - 1, row);
                    if ( b == null ) {
                        return;
                    }
                    Bounds sb = gp.localToScreen(b);
                    Bounds gpb = gp.localToScreen(gp.getBoundsInLocal());
                    if (x > sb.getMinX() + sb.getWidth() && x < gpb.getMinX() + gpb.getWidth()) {
                        Bounds cell = new BoundingBox(b.getMinX() + b.getWidth(), b.getMinY(),
                                gpb.getMinX() + gpb.getWidth() - sb.getMinX() - sb.getWidth(), sb.getHeight());
                        adjustPlace(gp, cell);
                    }
                }
            } else if (col >= 0) {
                int[] dim = JdkUtil.getGridDimensions(gp);
                if (dim[1] > 0) {
                    Bounds b = JdkUtil.getGridCellBounds(gp, col, dim[1] - 1);
                    if ( b == null ) {
                        return;
                    }
                    Bounds sb = gp.localToScreen(b);
                    Bounds gpb = gp.localToScreen(gp.getBoundsInLocal());
                    if (y > sb.getMinY() + sb.getHeight() && y < gpb.getMinY() + gpb.getHeight()) {
                        Bounds cell = new BoundingBox(b.getMinX(), b.getMinY() + b.getHeight(),
                                sb.getWidth(), gpb.getMinY() + gpb.getHeight() - sb.getMinY() - sb.getHeight());
                        adjustPlace(gp, cell);
                    }
                }

            } else {
                visible = false;
            }
            getDockPlace().setVisible(visible);
        }

        private void adjustConstraints(GridPane grid) {

            GridPane targetNode = (GridPane) getLayoutContext().getLayoutNode();
            Bounds bnd = targetNode.localToScene(targetNode.getBoundsInLocal());

            double dw = targetNode.getInsets().getLeft() + targetNode.getInsets().getRight();
            double dh = targetNode.getInsets().getTop() + targetNode.getInsets().getBottom();

            int[] dim = JdkUtil.getGridDimensions(targetNode);

            if (dim[0] < 0) {
                grid.getColumnConstraints().clear();
            }
            if (dim[1] < 0) {
                grid.getRowConstraints().clear();
            }
            if (dim[0] < 0) {
                ColumnConstraints cc = new ColumnConstraints(bnd.getWidth() - dw, bnd.getWidth() - dw, bnd.getWidth() - dw);
                grid.getColumnConstraints().add(cc);
            } else {
                grid.getColumnConstraints().clear();
                for (int i = 0; i < dim[0]; i++) {
                    grid.getColumnConstraints().add(getColumnConstraintsByIndex(i));
                }
            }

            if (dim[1] < 0) {
                RowConstraints rc = new RowConstraints(bnd.getHeight() - dh, bnd.getHeight() - dh, bnd.getHeight() - dh);
                grid.getRowConstraints().add(rc);
            } else {
                grid.getRowConstraints().clear();
                for (int i = 0; i < dim[1]; i++) {
                    grid.getRowConstraints().add(getRowConstraintsByIndex(i));
                }
            }
        }

        private ColumnConstraints getCopy(ColumnConstraints c) {
            ColumnConstraints retval = new ColumnConstraints(c.getMinWidth(), c.getPrefWidth(), c.getMaxWidth(), c.getHgrow(), c.getHalignment(), c.isFillWidth());
            retval.setPercentWidth(c.getPercentWidth());
            return retval;
        }

        private RowConstraints getCopy(RowConstraints c) {
            RowConstraints retval = new RowConstraints(c.getMinHeight(), c.getPrefHeight(), c.getMaxHeight(), c.getVgrow(), c.getValignment(), c.isFillHeight());
            retval.setPercentHeight(c.getPercentHeight());
            return retval;
        }

        private ColumnConstraints getColumnConstraintsByIndex(int idx) {
            GridPane grid = (GridPane) getLayoutContext().getLayoutNode();
            ColumnConstraints retval = null;
            if (idx >= 0 && idx < grid.getColumnConstraints().size()) {
                ColumnConstraints c = getCopy(grid.getColumnConstraints().get(idx));
                if ( grid.getRowConstraints().size() > 0 ) {
                    
                    Bounds b = JdkUtil.getGridCellBounds(grid, idx, 0);      
                    Bounds sb = grid.localToScene(b);
                    c.setMinWidth(sb.getWidth());
                    c.setPrefWidth(sb.getWidth());
                    c.setMaxWidth(sb.getWidth());
                    c.setPercentWidth(-1);
                }
                return c;
            }

            int[] dim = JdkUtil.getGridDimensions(grid);
            if (dim[1] > 0) {
                Bounds b = JdkUtil.getGridCellBounds(grid, idx, 0);
                if ( b != null ) {
                    b = grid.localToScene(b);
                    retval = new ColumnConstraints(b.getWidth(), b.getWidth(), b.getWidth());
                }
            } else {
                Bounds b = grid.localToScene(new BoundingBox(0,0, 50, 50));
                retval = new ColumnConstraints(b.getWidth(), b.getWidth(), b.getWidth());
            }
            return retval;
        }

        private RowConstraints getRowConstraintsByIndex(int idx) {
            GridPane grid = (GridPane) getLayoutContext().getLayoutNode();
            RowConstraints retval = null;
            if (idx >= 0 && idx < grid.getRowConstraints().size()) {
                RowConstraints c = getCopy(grid.getRowConstraints().get(idx));
                if ( grid.getColumnConstraints().size() > 0 ) {
                    Bounds b = JdkUtil.getGridCellBounds(grid, 0, idx);    
                    Bounds sb = grid.localToScene(b);

                    c.setMinHeight(sb.getHeight());
                    c.setPrefHeight(sb.getHeight());
                    c.setMaxHeight(sb.getHeight());
                    c.setPercentHeight(-1);
                }
                return c;

            }

            int[] dim = JdkUtil.getGridDimensions(grid);
            if (dim[0] > 0) {
                Bounds b = JdkUtil.getGridCellBounds(grid, 0, idx);
                if ( b != null ) {
                    b = grid.localToScene(b);
                    retval = new RowConstraints(b.getHeight(), b.getHeight(), b.getHeight());
                }
            } else {
                Bounds b = grid.localToScene(new BoundingBox(0,0, 25, 25));
                retval = new RowConstraints(b.getHeight(), b.getHeight(), b.getHeight());
                
            }
            return retval;
        }

        private void adjustPlace(GridPane grid, int column, int row) {
            Bounds b = JdkUtil.getGridCellBounds(grid, column, row);

            Rectangle r = (Rectangle) getDockPlace();
            r.setHeight(b.getHeight());
            r.setWidth(b.getWidth());
            r.setX(grid.getLayoutX() + b.getMinX());
            r.setY(grid.getLayoutY() + b.getMinY());

        }

        private void adjustPlace(GridPane grid, Bounds place) {
            Rectangle r = (Rectangle) getDockPlace();
            r.setHeight(place.getHeight());
            r.setWidth(place.getWidth());
            r.setX(grid.getLayoutX() + place.getMinX());
            r.setY(grid.getLayoutY() + place.getMinY());
        }

    }//gridpane positionIndicator

    public static class GridPaneConstraintsFactory extends ConstraintsFactory {

        @Override
        public Constraints getConstraints(Node node) {
            return new GridPaneConstraints(node);
        }
    }

    public static class GridObjectFramingProvider implements ObjectFramingProvider {

        private final GridPane gridPane;
        private final ObservableMap<String, ConstraintsFraming> framings = FXCollections.observableHashMap();
        private ConstraintsFraming parentFraming;
        
        public GridObjectFramingProvider(GridPane gridPane) {
            this.gridPane = gridPane;
        }

        @Override
        public ObjectFraming getInstance(Node childNode) {
            if ( parentFraming == null ) {
                parentFraming = new ConstraintsFraming(gridPane, childNode);
            }
            return parentFraming;
        }        
        @Override
        public ObjectFraming getInstance(String propertyName) {
            if (!"rowConstraints".equals(propertyName) && !"columnConstraints".equals(propertyName)) {
                return null;
            }
            if (framings.containsKey("rowConstraints")) {
                return framings.get("rowConstraints");
            }
            if (framings.containsKey("columnConstraints")) {
                return framings.get("columnConstraints");
            }
            ConstraintsFraming cf = new ConstraintsFraming(gridPane);
            framings.put("rowConstraints", cf);
            return cf;
        }

    }

    public static class ConstraintsFraming implements ObjectFraming {

        private final ObjectProperty<ConstraintsBase> selected = new SimpleObjectProperty<>();
        //private String propertyName;

        private GridSelectionFrame frame;
        private final GridPane gridPane;
        private ObjectProperty<Node> childNode = new SimpleObjectProperty<>();
        
        private ChangeListener<? super ConstraintsBase> selectedConstraintsListener = (v, ov, nv) -> {
            Selection sel = DockRegistry.lookup(Selection.class);
            if (sel != null) {
                sel.notifySelected(nv);
            }
        };

        public ConstraintsFraming(GridPane gridPane) {
            this(gridPane,null);
        }
        public ConstraintsFraming(GridPane gridPane, Node childNode) {
            this.gridPane = gridPane;
            this.childNode.set(childNode);
            
        }     

        public ObjectProperty<Node> childNodeProperty() {
            return childNode;
        }
        
        public Node getChildNode() {
            return childNode.get();
        }

        public void setChildNode(Node childNode) {
            this.childNode.set(childNode);
        }
        public boolean isShowing() {
            return false;
        }
        
        
        @Override
        public void showParent(Object... parms) {
            if (frame != null) {
                frame.hide();
                gridPane.getChildren().remove(frame);
                //02.01DockUtil.getChildren(gridPane.getScene().getRoot()).remove(frame);
            }
            if (parms != null && parms.length > 0) {
                setSelected((ConstraintsBase) parms[0]);
            } else {
                setSelected(null);
            }
            frame = new GridSelectionFrame(gridPane,getChildNode());
            frame.selectedConstraintsProperty().addListener(selectedConstraintsListener);
            gridPane.getChildren().add(frame);
            //02.01DockUtil.getChildren(gridPane.getScene().getRoot()).add(frame);
            frame.show();
/*            if (getSelected() == null) {
                return;
            }
            if (getSelected() instanceof RowConstraints) {
                frame.selectRow(gridPane.getRowConstraints().indexOf(getSelected()));
            } else {
                frame.selectColumn(gridPane.getColumnConstraints().indexOf(getSelected()));
            }
  */          
        }     
        
        @Override
        public void show(String propertyName, Object... parms) {
            //this.propertyName = propertyName;
            if (frame != null) {
                frame.hide();
                gridPane.getChildren().remove(frame);
                //02.01DockUtil.getChildren(gridPane.getScene().getRoot()).remove(frame);
            }
            if (parms != null && parms.length > 0) {
                setSelected((ConstraintsBase) parms[0]);
            } else {
                setSelected(null);
            }
            frame = new GridSelectionFrame(gridPane);
            frame.selectedConstraintsProperty().addListener(selectedConstraintsListener);
            gridPane.getChildren().add(frame);
            //02.01DockUtil.getChildren(gridPane.getScene().getRoot()).add(frame);
            frame.show();
            if (getSelected() == null) {
                return;
            }
            if (getSelected() instanceof RowConstraints) {
                frame.selectRow(gridPane.getRowConstraints().indexOf(getSelected()));
            } else {
                frame.selectColumn(gridPane.getColumnConstraints().indexOf(getSelected()));
            }

        }

        @Override
        public void hide() {
            if (frame != null) {
                frame.hide();
                gridPane.getChildren().remove(frame);
                //02.01DockUtil.getChildren(gridPane.getScene().getRoot()).remove(frame);
            }

        }

        public ObjectProperty<ConstraintsBase> selectedProperty() {
            return selected;
        }

        public ConstraintsBase getSelected() {
            return selected.get();
        }

        public void setSelected(ConstraintsBase selected) {
            this.selected.set(selected);
        }

    }

}//LayoutContextFactory
