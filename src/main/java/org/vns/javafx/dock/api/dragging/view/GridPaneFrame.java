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
package org.vns.javafx.dock.api.dragging.view;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import org.vns.javafx.JdkUtil;
import static org.vns.javafx.dock.DockUtil.FOREIGN;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.indicator.GridPaneConstraintsDividers;

/**
 *
 * @author Valery
 */
public class GridPaneFrame extends Group implements GridPaneConstraintsDividers.GridCellBoundsObservable {

    private GridPane contentGrid;
    private GridPane topGrid;
    private GridPane leftGrid;

    private GridPaneConstraintsDividers dividers;// = new GridPaneConstraintsDividers(gridPane);

    protected boolean update;

    public GridPaneFrame(GridPane contentGrid) {
        this.contentGrid = contentGrid;
        init();
    }

    private void init() {
        setVisible(false);
        setManaged(false);
        visibleProperty().addListener((v,ov,nv) -> {
            if ( nv ) {
                show();
            } else {
                hide();
            }
        });
        getStyleClass().addAll("grid-pane-frame", FOREIGN);

        topGrid = new GridPane() {
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                GridPaneFrame.this.layoutChildren();
            }

            @Override
            public String getUserAgentStylesheet() {
                return Dockable.class.getResource("resources/default.css").toExternalForm();
            }
        };

        topGrid.getStyleClass().addAll("top-grid", FOREIGN);

        leftGrid = new GridPane() {

            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                GridPaneFrame.this.layoutChildren();
            }

            @Override
            public String getUserAgentStylesheet() {
                return Dockable.class.getResource("resources/default.css").toExternalForm();
            }
        };

        leftGrid.getStyleClass().addAll("left-grid", FOREIGN);

        getChildren().addAll(topGrid, leftGrid);

        //adjust();

        dividers = new GridPaneConstraintsDividers(contentGrid);
        dividers.getGridCellBoundsObservables().add(this);

    }

    public void show() {
        setVisible(true);
        adjust();
//        leftGrid.layout();
//        layoutChildren();
        dividers.show();
    }
    public void hide() {
        setVisible(false);
        //clearSelection();
        dividers.hide();
    }
    public void selectRow(Node node) {
        int idx = GridPane.getRowIndex(node);
        if (idx < 0) {
            return;
        }
        if (idx < contentGrid.getRowConstraints().size()) {
            selectRow(idx);
        }
    }

    public void selectColumn(Node node) {
        int idx = GridPane.getColumnIndex(node);
        if (idx < 0) {
            return;
        }
        if (idx < contentGrid.getColumnConstraints().size()) {
            selectRow(idx);
        }
    }

    public void selectRow(int rowIndex) {
        if (rowIndex >= leftGrid.getRowConstraints().size()) {
            return;
        }
        for (Node node : leftGrid.getChildren()) {
            if ((node instanceof LabelEx) && GridPane.getRowIndex(node) == rowIndex) {
                setSelected((LabelEx) node);
                break;
            }
        }
    }

    protected void setSelected(LabelEx lb) {
        clearSelection();
        if (lb == null || !topGrid.getChildren().contains(lb) && !leftGrid.getChildren().contains(lb)) {
            return;
        }
        lb.setSelected(true);
    }

    public void selectColumn(int index) {
        if (index >= topGrid.getColumnConstraints().size()) {
            return;
        }
        for (Node node : topGrid.getChildren()) {
            if ((node instanceof LabelEx) && GridPane.getColumnIndex(node) == index) {
                setSelected((LabelEx) node);
                break;
            }
        }
    }

    public LabelEx getSelected(Side side) {
        LabelEx retval = null;
        if (side == Side.TOP) {
            int idx = getSelectedColumn();
            if (idx >= 0) {
                retval = (LabelEx) topGrid.getChildren().get(idx);
            }
        } else if (side == Side.LEFT) {
            int idx = getSelectedRow();
            if (idx >= 0) {
                retval = (LabelEx) leftGrid.getChildren().get(idx);
            }
        }
        return retval;
    }

    public int getSelectedRow() {
        int idx = -1;
        for (int i = 0; i < leftGrid.getChildren().size(); i++) {
            Node node = leftGrid.getChildren().get(i);

            if ((node instanceof LabelEx) && ((LabelEx) node).isSelected()) {
                idx = i;
                break;
            }
        }
        return idx;
    }

    public int getSelectedColumn() {
        int idx = -1;
        for (int i = 0; i < topGrid.getChildren().size(); i++) {
            Node node = topGrid.getChildren().get(i);

            if ((node instanceof LabelEx) && ((LabelEx) node).isSelected()) {
                idx = i;
                break;
            }
        }
        return idx;
    }

    public void clearSelection() {
        clearRowSelection();
        clearColumnSelection();
    }

    public void clearRowSelection() {
        LabelEx lb = getSelected(Side.LEFT);
        if (lb != null) {
            lb.setSelected(false);
        }
    }

    public void clearColumnSelection() {
        LabelEx lb = getSelected(Side.TOP);
        if (lb != null) {
            lb.setSelected(false);
        }

    }

    protected void onAddAction(Constraints constraints, Constraints contentConstraints, MenuItem mi, Label targeLabel, int idx) {

        constraints.addNew(idx);
        contentConstraints.addNew(idx);

        for (Node node : constraints.getGrid().getChildren()) {
            Label lb = (Label) node;
            int idx1 = constraints.getIndex(node);
            if (idx1 >= idx) {
                constraints.setIndex(node, idx1 + 1);
                lb.setText("" + (idx1 + 1));
            } else if (idx1 < idx) {
                lb.setText("" + idx1);
            }

        }
        Label addedLabel = new LabelEx("" + idx);
        addedLabel.setMinWidth(0);
        addedLabel.setMaxWidth(1000);
        addedLabel.setMaxHeight(1000);
        addedLabel.setPrefHeight(20);
        addedLabel.setAlignment(Pos.CENTER);

        constraints.add(addedLabel, idx);
        constraints.select(idx);

        createContextMenu(constraints, contentConstraints, addedLabel);

        //addedLabel.setStyle("-fx-border-width: 1; -fx-border-color: black; -fx-border-radius: 3 3 0 0; -fx-background-color: lightgrey");
        for (Node node : contentGrid.getChildren()) {

            int idx1 = contentConstraints.getIndex(node);
            if (idx1 >= idx) {
                contentConstraints.setIndex(node, idx1 + 1);
            }
        }
    }

    protected void createContextMenu(Constraints constraints, Constraints contentConstraints, Label targetLabel) {

        ContextMenu menu = new ContextMenu();

        final MenuItem miDelete = new MenuItem("Delete");
        menu.getItems().add(miDelete);
        miDelete.setOnAction(e -> {
            update = true;
            dividers.hide();

            int idx = constraints.getIndex(targetLabel);

            List<Node> list = new ArrayList<>();
            contentGrid.getChildren().forEach(node -> {
                if (contentConstraints.getIndex(node) == idx) {
                    list.add(node);
                }
            });
            list.forEach(node -> {
                contentGrid.getChildren().remove(node);
            });

            constraints.remove(idx);
            constraints.removeChild(targetLabel);

            for (Node node : constraints.getGrid().getChildren()) {
                Label lb = (Label) node;
                int idx1 = constraints.getIndex(node);
                if (idx1 > idx) {
                    constraints.setIndex(node, idx1 - 1);
                    lb.setText("" + (idx1 - 1));
                } else if (idx1 <= idx) {
                    lb.setText("" + idx1);
                }
            }

            if (!contentConstraints.isEmpty()) {
                contentConstraints.remove(idx);

                for (Node node : contentGrid.getChildren()) {
                    int idx1 = contentConstraints.getIndex(node);
                    if (idx1 >= idx) {
                        contentConstraints.setIndex(node, idx1 - 1);
                    }
                }
            }
            dividers.show();
            update = false;
        });
        menu.getItems().add(new SeparatorMenuItem());

        final MenuItem miBefore = new MenuItem("Add Before");
        menu.getItems().add(miBefore);
        miBefore.setOnAction(e -> {
            update = true;
            dividers.hide();

            int idx = constraints.getIndex(targetLabel);

            onAddAction(constraints, contentConstraints, miBefore, targetLabel, idx);
            dividers.show();
            update = false;
        });

        final MenuItem miAfter = new MenuItem("Add After");
        menu.getItems().add(miAfter);
        miAfter.setOnAction(e -> {
            update = true;
            dividers.hide();
            int idx = constraints.getIndex(targetLabel);
            onAddAction(constraints, contentConstraints, miAfter, targetLabel, ++idx);
            dividers.show();
            update = false;
        });

        targetLabel.setContextMenu(menu);

    }

    protected void adjust() {
        topGrid.getRowConstraints().clear();
        topGrid.getColumnConstraints().clear();
        RowConstraints rc = new RowConstraints();
        topGrid.getRowConstraints().add(rc);

        for (int i = 0; i < contentGrid.getColumnConstraints().size(); i++) {
            Bounds b = JdkUtil.getGridCellBounds(contentGrid, i, 0);
            ColumnConstraints c = new ColumnConstraints(b.getWidth(), b.getWidth(), b.getWidth());
            topGrid.getColumnConstraints().add(c);

            rc.setVgrow(Priority.ALWAYS);
            rc.setFillHeight(true);
            rc.setValignment(VPos.BASELINE);

            Label lb = new LabelEx("" + i);
            if (!lb.getStyleClass().contains("label")) {
                lb.getStyleClass().add("label");
            }
            if (!lb.getStyleClass().contains(FOREIGN)) {
                lb.getStyleClass().add(FOREIGN);
            }

            createContextMenu(new Constraints(Side.TOP, topGrid), new Constraints(Side.TOP, contentGrid), lb);

            lb.setMaxWidth(1000);
            lb.setMaxHeight(1000);
            lb.setPrefHeight(b.getHeight());
            lb.setAlignment(Pos.CENTER);
            //lb.setStyle("-fx-border-width: 1; -fx-border-color: black; -fx-border-radius: 3 3 0 0; -fx-background-color: lightgrey");
            topGrid.add(lb, i, 0);
        }

        ColumnConstraints c = new ColumnConstraints();
        leftGrid.getRowConstraints().clear();
        leftGrid.getColumnConstraints().clear();

        leftGrid.getColumnConstraints().add(c);
        c.setHgrow(Priority.ALWAYS);
        c.setFillWidth(true);

        for (int i = 0; i < contentGrid.getRowConstraints().size(); i++) {
            Bounds b = JdkUtil.getGridCellBounds(contentGrid, 0, i);
            rc = new RowConstraints(b.getHeight(), b.getHeight(), b.getHeight());
            Label lb = new LabelEx("" + i);
            if (!lb.getStyleClass().contains("label")) {
                lb.getStyleClass().add("label");

            }
            if (!lb.getStyleClass().contains(FOREIGN)) {
                lb.getStyleClass().add(FOREIGN);
            }

            createContextMenu(new Constraints(Side.LEFT, leftGrid), new Constraints(Side.LEFT, contentGrid), lb);

            lb.setMinHeight(0);
            lb.setMaxWidth(1000);
            lb.setMaxHeight(1000);
            lb.setPrefHeight(b.getHeight());

            lb.setAlignment(Pos.CENTER);
            //lb.setStyle("-fx-border-width: 1; -fx-border-color: black; -fx-border-radius: 3 0 0 3; -fx-background-color: lightgrey");
            leftGrid.add(lb, 0, i);
            leftGrid.getRowConstraints().add(rc);
        }

    }

    @Override
    protected void layoutChildren() {
        try {
            Bounds sceneBounds = contentGrid.localToScene(contentGrid.getBoundsInLocal());
            Insets ins = contentGrid.getInsets();
            topGrid.setLayoutX(sceneBounds.getMinX() + ins.getLeft());
            double d = JdkUtil.getGridCellBounds(topGrid, 0, 0).getHeight();
            topGrid.setLayoutY(sceneBounds.getMinY() - d + ins.getTop());

            ColumnConstraints c = leftGrid.getColumnConstraints().get(0);
            c.setMinWidth(d);
            c.setMaxWidth(d);
            c.setPrefWidth(d);
            leftGrid.setLayoutX(sceneBounds.getMinX() + ins.getLeft() - d);
            leftGrid.setLayoutY(sceneBounds.getMinY() + ins.getTop());
        } catch (Exception ex) {

        }
    }

    @Override
    public void resizeRelocate(Bounds bounds) {
        if (update) {
            return;
        }
        try {
            for (int i = 0; i < contentGrid.getColumnConstraints().size(); i++) {

                Bounds b = JdkUtil.getGridCellBounds(contentGrid, i, 0);
                ColumnConstraints c = topGrid.getColumnConstraints().get(i);
                c.setMinWidth(b.getWidth());
                c.setMaxWidth(b.getWidth());
                c.setPrefWidth(b.getWidth());
            }
            for (int i = 0; i < contentGrid.getRowConstraints().size(); i++) {
                Bounds b = JdkUtil.getGridCellBounds(contentGrid, 0, i);
                RowConstraints rc = leftGrid.getRowConstraints().get(i);
                rc.setMinHeight(b.getHeight());
                rc.setMaxHeight(b.getHeight());
                rc.setPrefHeight(b.getHeight());
            }

            layoutChildren();
        } catch (Exception ex) {
        }
    }

    public static class Constraints {

        private final Side side;
        private final GridPane grid;

        public Constraints(Side side, GridPane grid) {
            this.side = side;
            this.grid = grid;
        }

        public int getIndex(Node node) {
            int retval = -1;
            if (side == Side.TOP || side == Side.BOTTOM) {
                retval = GridPane.getColumnIndex(node);
            } else {
                retval = GridPane.getRowIndex(node);
            }
            return retval;
        }

        public void setIndex(Node node, int idx) {
            if (side == Side.TOP || side == Side.BOTTOM) {
                GridPane.setColumnIndex(node, idx);
            } else {
                GridPane.setRowIndex(node, idx);
            }
        }

        public void remove(int idx) {
            if (side == Side.TOP || side == Side.BOTTOM) {
                grid.getColumnConstraints().remove(idx);
            } else {
                grid.getRowConstraints().remove(idx);
            }

        }

        public void add(Node node, int idx) {
            if (side == Side.TOP || side == Side.BOTTOM) {
                grid.add(node, idx, 0);
            } else {
                grid.add(node, 0, idx);
            }

        }

        //headerGrid.add(lb1, idx, 0);        
        public void addNew(int idx) {
            if (side == Side.TOP || side == Side.BOTTOM) {
                ColumnConstraints ci = new ColumnConstraints(20, 20, 20);
                grid.getColumnConstraints().add(idx, ci);
            } else {
                RowConstraints ci = new RowConstraints(20, 20, 20);
                grid.getRowConstraints().add(idx, ci);
            }

        }

        public void removeChild(Node node) {
            grid.getChildren().remove(node);
        }

        public GridPane getGrid() {
            return grid;
        }

        public boolean isEmpty() {
            if (side == Side.TOP || side == Side.BOTTOM) {
                return grid.getColumnConstraints().isEmpty();
            } else {
                return grid.getRowConstraints().isEmpty();
            }
        }

        public void select(int idx) {
            if (side == Side.TOP || side == Side.BOTTOM) {
                ((GridPaneFrame) grid.getParent()).selectColumn(idx);
            } else {
                ((GridPaneFrame) grid.getParent()).selectRow(idx);
            }

        }

    }

    static class LabelEx extends Label {

        private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");

        private BooleanProperty selected = new SimpleBooleanProperty(false);

        public LabelEx() {
            init();
        }

        public LabelEx(String text) {
            super(text);
            init();
        }

        public LabelEx(String text, Node graphic) {
            super(text, graphic);
            init();
        }

        private void init() {
            getStyleClass().add("labelex");
            selected.addListener((v, ov, nv) -> {
                pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, nv);
            });
            setOnMouseClicked(e -> {
                ((GridPaneFrame) getParent().getParent()).setSelected(this);
            });
        }

        public void setSelected(boolean selected) {
            this.selected.set(selected);
        }

        public boolean isSelected() {
            return this.selected.get();
        }

        @Override
        public String getUserAgentStylesheet() {
            return Dockable.class.getResource("resources/default.css").toExternalForm();
        }
    }
}//GridPaneFrame
