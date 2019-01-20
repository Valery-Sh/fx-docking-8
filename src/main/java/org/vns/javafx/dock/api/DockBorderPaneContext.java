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
package org.vns.javafx.dock.api;

import org.vns.javafx.ContextLookup;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.dock.api.indicator.IndicatorPopup;
import org.vns.javafx.dock.api.indicator.PositionIndicator;

/**
 *
 * @author Valery
 */
public class DockBorderPaneContext extends LayoutContext {

    public DockBorderPaneContext(Node dockPane) {
        super(dockPane);
        init();
    }

    private void init() {
        BorderPane pane = (BorderPane) getLayoutNode();
        pane.topProperty().addListener((ov, oldValue, newValue) -> {
            if (oldValue != null) {
                if (Dockable.of(oldValue) != null) {
                    undock(Dockable.of(oldValue));
                }
            }
            if (newValue != null) {
                commitDock(newValue);
            }
        });
        pane.rightProperty().addListener((ov, oldValue, newValue) -> {
            if (oldValue != null) {
                if (Dockable.of(oldValue) != null) {
                    undock(Dockable.of(oldValue));
                }
            }
            if (newValue != null) {
                commitDock(newValue);
            }
        });
        pane.bottomProperty().addListener((ov, oldValue, newValue) -> {
            if (oldValue != null) {
                if (Dockable.of(oldValue) != null) {
                    undock(Dockable.of(oldValue));
                }
            }
            if (newValue != null) {
                commitDock(newValue);
            }
        });

        pane.leftProperty().addListener((ov, oldValue, newValue) -> {
            if (oldValue != null) {
                if (Dockable.of(oldValue) != null) {
                    undock(Dockable.of(oldValue));
                }
            }
            if (newValue != null) {
                commitDock(newValue);
            }
        });

    }

    @Override
    protected void initLookup(ContextLookup lookup) {
        super.initLookup(lookup);
        lookup.putUnique(PositionIndicator.class, new BorderPanePositionIndicator(this));
    }

    /*        @Override
        protected boolean isDocked(Node node) {
            return ((BorderPane) getLayoutNode()).getChildren().contains(node);
        }
     */
    @Override
    public boolean contains(Object obj) {
        return ((BorderPane) getLayoutNode()).getChildren().contains(obj);
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

        if (contains(d.getNode()) && d == dockable) {
            return;
        } else if (contains(d.getNode())) {
            LayoutContext tc = d.getContext().getLayoutContext();
            if (tc != null && isDocked(tc, d)) {
                tc.undock(dockable);
            }
        }

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
        BorderPane target = (BorderPane) getLayoutNode();
        //(BorderPane) getIndicatorPane().lookup("#border-pane-indicator");
        BorderPane bp = (BorderPane) getPositionIndicator().getIndicatorPane().lookup("#border-pane-indicator");

        if (target.getTop() == null && Util.contains(bp.getTop(), mousePos.getX(), mousePos.getY())) {
            target.setTop(node);
        } else if (target.getRight() == null && Util.contains(bp.getRight(), mousePos.getX(), mousePos.getY())) {
            target.setRight(node);
        } else if (target.getBottom() == null && Util.contains(bp.getBottom(), mousePos.getX(), mousePos.getY())) {
            target.setBottom(node);
        } else if (target.getLeft() == null && Util.contains(bp.getLeft(), mousePos.getX(), mousePos.getY())) {
            target.setLeft(node);
        } else if (target.getCenter() == null && Util.contains(bp.getCenter(), mousePos.getX(), mousePos.getY())) {
            target.setCenter(node);
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
        BorderPane target = (BorderPane) getLayoutNode();
        if (dockNode == target.getTop()) {
            target.setTop(null);
        } else if (dockNode == target.getRight()) {
            target.setRight(null);
        } else if (dockNode == target.getBottom()) {
            target.setBottom(null);
        } else if (dockNode == target.getLeft()) {
            target.setLeft(null);
        } else if (dockNode == target.getCenter()) {
            target.setCenter(null);
        }

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

    public static class BorderPanePositionIndicator extends PositionIndicator {

        public BorderPanePositionIndicator(LayoutContext targetContext) {
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
            indicator.setId("borderpane-ind-pane");
            indicator.getStyleClass().add("borderpane-indicator");
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
            Bounds bnd = targetNode.localToScene(targetNode.getBoundsInLocal());

            if (!isControlDown()) {
                bnd = Util.sceneIntersection(targetNode);
            } 
            updateSnapshot(isControlDown());

            topNode.setPrefWidth(bnd.getWidth());
            topNode.setPrefHeight(bnd.getHeight() / 4);
            topNode.setMaxWidth(1000);
            topNode.setMaxHeight(1000);

            //topNode.prefWidthProperty().bind(targetPane.widthProperty());
            //topNode.prefHeightProperty().bind(targetPane.heightProperty().divide(4));
            rightNode.setPrefWidth(bnd.getWidth() / 4);
            rightNode.setPrefHeight(bnd.getHeight() / 2);
            rightNode.setMaxWidth(1000);
            rightNode.setMaxHeight(1000);

            //rightNode.prefHeightProperty().bind(targetPane.heightProperty().divide(2));
            //rightNode.prefWidthProperty().bind(targetPane.widthProperty().divide(4));
            leftNode.setPrefWidth(bnd.getWidth() / 4);
            leftNode.setPrefHeight(bnd.getHeight() / 2);
            leftNode.setMaxWidth(1000);
            leftNode.setMaxHeight(1000);

//            leftNode.prefHeightProperty().bind(targetPane.heightProperty().divide(2));
//            leftNode.prefWidthProperty().bind(targetPane.widthProperty().divide(4));
            bottomNode.setPrefWidth(bnd.getWidth());
            bottomNode.setPrefHeight(bnd.getHeight() / 4);
            bottomNode.setMaxWidth(1000);
            bottomNode.setMaxHeight(1000);

//            bottomNode.prefWidthProperty().bind(targetPane.widthProperty());
//            bottomNode.prefHeightProperty().bind(targetPane.heightProperty().divide(4));
            centerNode.setPrefWidth(bnd.getWidth() / 2);
            centerNode.setPrefHeight(bnd.getHeight() / 2);
            centerNode.setMaxWidth(1000);
            centerNode.setMaxHeight(1000);

//            centerNode.prefHeightProperty().bind(targetPane.heightProperty().divide(2));
//            centerNode.prefWidthProperty().bind(targetPane.widthProperty().divide(2));
            BorderPane indicator = new BorderPane(centerNode, topNode, rightNode, bottomNode, leftNode) {
                @Override
                public String getUserAgentStylesheet() {
                    return Dockable.class.getResource("resources/default.css").toExternalForm();
                }
            };
            indicator.setId("border-pane-indicator");

            indicator.getStyleClass().add("border-pane-indicator");
            topNode.setAlignment(Pos.CENTER);
            rightNode.setAlignment(Pos.CENTER);
            bottomNode.setAlignment(Pos.CENTER);
            //bottomNode.setMouseTransparent(true);

            leftNode.setAlignment(Pos.CENTER);
            centerNode.setAlignment(Pos.CENTER);

            Node child = getIndicatorPane().lookup("#border-pane-indicator");

            getIndicatorPane().getChildren().remove(child);
            getIndicatorPane().getChildren().add(indicator);
            indicator.setManaged(false);

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
                    dx = bnd.getMinX();
                    dy = bnd.getMinY();
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
            BorderPane target = (BorderPane) getLayoutContext().getLayoutNode();
            Bounds bnd = target.localToScene(target.getBoundsInLocal());
            if (!isControlDown()) {
                bnd = Util.sceneIntersection(target);
            }
            BorderPane bp = (BorderPane) getIndicatorPane().lookup("#border-pane-indicator");
            if (target.getTop() == null && Util.contains(bp.getTop(), x, y)) {
                adjustPlace(bp.getTop(), 0, 0);
            } else if (target.getRight() == null && Util.contains(bp.getRight(), x, y)) {
                adjustPlace(bp.getRight(), ((Region) bp.getLeft()).getWidth() + ((Region) bp.getCenter()).getWidth(), ((Region) bp.getTop()).getHeight());
            } else if (target.getBottom() == null && Util.contains(bp.getBottom(), x, y)) {
                adjustPlace(bp.getBottom(), 0, ((Region) bp.getTop()).getHeight() + ((Region) bp.getCenter()).getHeight());
            } else if (target.getLeft() == null && Util.contains(bp.getLeft(), x, y)) {
                adjustPlace(bp.getLeft(), 0, ((Region) bp.getTop()).getHeight());
            } else if (target.getCenter() == null && Util.contains(bp.getCenter(), x, y)) {
                adjustPlace(bp.getCenter(), ((Region) bp.getLeft()).getWidth(), ((Region) bp.getTop()).getHeight());
            } else {
                visible = false;
            }

            getDockPlace().setVisible(visible);
        }


        private void adjustPlace(Node node, double x, double y) {
            //BorderPane target = (BorderPane) getLayoutContext().getLayoutNode();

            BorderPane bp = (BorderPane) getIndicatorPane().lookup("#border-pane-indicator");
            Bounds bpBnd = bp.getBoundsInParent();
            Rectangle r = (Rectangle) getDockPlace();
            r.setHeight(((Region) node).getHeight());
            r.setWidth(((Region) node).getWidth());

            Bounds pos = node.getBoundsInLocal();
            r.setX(bpBnd.getMinX() + pos.getMinX() + x);
            r.setY(bpBnd.getMinY() + pos.getMinY() + y);
            bp.toFront();
            r.toFront();
        }

    } //PositionIndicator

}//BorderPaneContext

