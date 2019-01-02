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
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.css.PseudoClass;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
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
import javafx.scene.control.Tooltip;
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
public class GridPaneFrame1 extends Group implements GridPaneConstraintsDividers.GridCellBoundsObservable {

//    public static final double GAP = 2;
    
    private GridPane contentGrid;
    private GridPane topGrid;
    private GridPane leftGrid;
    private GridPaneConstraintsDividers dividers;// = new GridPaneConstraintsDividers(gridPane);

    private ContextMenu topGridContextMenu;
    private ContextMenu leftGridContextMenu;
    private Tooltip topGridContextTooltip;
    private Tooltip leftGridContextTooltip;
    protected boolean update;

    private ListChangeListener<ColumnConstraints> columnConstraintsListener = (change) -> {

        while (change.next()) {
            if (change.wasPermutated()) {
            } else if (change.wasUpdated()) {
            } else if (change.wasReplaced()) {
            } else {
                if (change.wasRemoved()) {
                    handleColumnRemoved(change);
                } else if (change.wasAdded()) {
                    handleColumnAdded(change);
                }
            }
        }

    };
    private ListChangeListener<RowConstraints> rowConstraintsListener = (change) -> {
        while (change.next()) {
            if (change.wasPermutated()) {
            } else if (change.wasUpdated()) {
            } else if (change.wasReplaced()) {
            } else {
                if (change.wasRemoved()) {
                    handleRowRemoved(change);
                } else if (change.wasAdded()) {
                    handleRowAdded(change);
                }
            }
        }

    };

    public GridPaneFrame1(GridPane contentGrid) {
        this.contentGrid = contentGrid;
        init();
    }

    private void init() {
        setVisible(false);
        setManaged(false);
        /*        visibleProperty().addListener((v, ov, nv) -> {
            if (nv) {
                //show();
            } else {
                //hide();
            }
        });
         */
        getStyleClass().addAll("grid-pane-frame", FOREIGN);

        topGrid = new GridPane() {
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                GridPaneFrame1.this.layoutChildren();
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
                GridPaneFrame1.this.layoutChildren();
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

        final MenuItem item = new MenuItem("Add ColumnConstraints");
        item.setOnAction(e -> {
            contentGrid.getColumnConstraints().add(new ColumnConstraints(20, 20, 20));
        });
        topGridContextMenu = new ContextMenu(item);
        topGridContextTooltip = new Tooltip("Right click to show a context menu");
        MenuItem item1 = new MenuItem("Add RowConstraints");
        item1.setOnAction(e -> {
            contentGrid.getRowConstraints().add(new RowConstraints(20, 20, 20));
        });
        leftGridContextMenu = new ContextMenu(item1);
        leftGridContextTooltip = new Tooltip("Right click to show a context menu");

    }

    public void show() {
        if (isVisible()) {
            return;
        }
        setVisible(true);
        adjust();

        contentGrid.getColumnConstraints().addListener(columnConstraintsListener);
        contentGrid.getRowConstraints().addListener(rowConstraintsListener);

        dividers.show();
    }

    public void hide() {
        setVisible(false);
        //clearSelection();
        contentGrid.getColumnConstraints().removeListener(columnConstraintsListener);
        contentGrid.getRowConstraints().removeListener(rowConstraintsListener);

        dividers.hide();
    }

    private void handleColumnRemoved(Change<? extends ColumnConstraints> change) {
        handleColumnRemoved(change, false);
    }

    private void handleColumnRemoved(Change<? extends ColumnConstraints> change, boolean removeNodes) {
        update = true;
//        System.err.println("handleColumnRemoved update true");
        int offset = change.getTo() - change.getFrom() + 1;

        List<Node> nodes = new ArrayList<>();
        topGrid.getChildren().forEach(node -> {
            if (node.isManaged() && GridPane.getColumnIndex(node) != null && GridPane.getColumnIndex(node) >= change.getFrom() && GridPane.getColumnIndex(node) <= change.getTo()) {
                nodes.add(node);
            }
        });
        nodes.forEach(node -> {
            topGrid.getChildren().remove(node);
        });
        for (Node node : topGrid.getChildren()) {
            if (!(node instanceof LabelEx)) {
                continue;
            }
            int idx = GridPane.getColumnIndex(node);
            if (idx > change.getTo()) {
                GridPane.setColumnIndex(node, idx - offset);
            }
        }
        topGrid.getColumnConstraints().remove(change.getFrom(), change.getTo() + 1);

        for (Node node : contentGrid.getChildren()) {
            if (!node.isManaged()) {
                continue;
            }
//            System.err.println("handleColumnRemoved 3.1 node = " + node);
            int idx = GridPane.getColumnIndex(node);
            if (idx > change.getTo()) {
                GridPane.setColumnIndex(node, idx - offset);
            }
        }

        for (int i = 0; i < topGrid.getChildren().size(); i++) {
            if (topGrid.getChildren().get(i) instanceof LabelEx) {
                Label lb = (Label) topGrid.getChildren().get(i);
                lb.setText("" + GridPane.getColumnIndex(lb));
            }
        }
        updateOnEmptyColumns();

//        System.err.println("handleColumnRemoved update false");
//        System.err.println("=============");
        update = false;
    }

    private void handleColumnAdded(Change<? extends ColumnConstraints> change) {
        update = true;
//        System.err.println("handleColumnAdded update true");

        int offset = change.getTo() - change.getFrom();
        for (int i = 0; i < topGrid.getChildren().size(); i++) {
            Node node = topGrid.getChildren().get(i);
            if (!(node instanceof LabelEx)) {
                continue;
            }
            int idx = GridPane.getColumnIndex(node);
            if (idx >= change.getFrom()) {
                GridPane.setColumnIndex(node, idx + offset);
            }
        }

        for (int i = change.getFrom(); i < change.getTo(); i++) {
            ColumnConstraints added = contentGrid.getColumnConstraints().get(i);
            double minWidth = added.getPercentWidth() >= 0 ? added.getMinWidth() : (added.getMinWidth() < 0 ? 20 : added.getMinWidth());
            double prefWidth = added.getPercentWidth() >= 0 ? added.getPrefWidth() : (added.getPrefWidth() < 0 ? 20 : added.getPrefWidth());
            double maxWidth = added.getPercentWidth() >= 0 ? added.getMaxWidth() : (added.getMaxWidth() < 0 ? 20 : added.getMaxWidth());
            addColumnConstraints(i, minWidth , prefWidth , maxWidth, 20);
        }
//        System.err.println("topGrid.getChildren().size() = " + topGrid.getColumnConstraints().size());
//        System.err.println("topGrid.getColunn(3) = " + topGrid.getColumnConstraints().get(3));
        for (Node node : contentGrid.getChildren()) {
            if (!node.isManaged()) {
                continue;
            }
            int idx = GridPane.getColumnIndex(node);
            if (idx >= change.getFrom() && idx < contentGrid.getColumnConstraints().size() - offset) {
                GridPane.setColumnIndex(node, idx + offset);
            }
        }
        for (Node node : topGrid.getChildren()) {
            if (node instanceof LabelEx) {
                Label lb = (Label) node;
                lb.setText("" + GridPane.getColumnIndex(lb));
            }
        }
//        System.err.println("handleColumnAdded update false");
//        System.err.println("====================================");
        update = false;
    }

    private void handleRowRemoved(Change<? extends RowConstraints> change) {
        handleRowRemoved(change, false);
    }

    private void handleRowRemoved(Change<? extends RowConstraints> change, boolean removeNodes) {
        update = true;
//        System.err.println("handleRowRemoved update true");        
        int offset = change.getTo() - change.getFrom() + 1;

//        System.err.println("handleRowRemoved leftGrid.size = " + leftGrid.getChildren().size());
        List<Node> nodes = new ArrayList<>();
        leftGrid.getChildren().forEach(node -> {
//            System.err.println("GridPane.getRowIndex(node) = " + GridPane.getRowIndex(node));
//            System.err.println("    ---  getTo() = " + change.getTo());
            if (node.isManaged() && GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) >= change.getFrom() && GridPane.getRowIndex(node) <= change.getTo()) {
                nodes.add(node);
            }
        });
        nodes.forEach(node -> {
            leftGrid.getChildren().remove(node);
        });
        for (Node node : leftGrid.getChildren()) {
            if (!(node instanceof LabelEx)) {
                continue;
            }
            int idx = GridPane.getRowIndex(node);
            if (idx > change.getTo()) {
                GridPane.setRowIndex(node, idx - offset);
            }
        }

        leftGrid.getRowConstraints().remove(change.getFrom(), change.getTo() + 1);

        for (Node node : contentGrid.getChildren()) {
            if (!node.isManaged()) {
                continue;
            }
            int idx = GridPane.getRowIndex(node);
            if (idx > change.getTo()) {
                GridPane.setRowIndex(node, idx - offset);
            }
        }
        for (Node node : leftGrid.getChildren()) {
            if (node instanceof LabelEx) {
                Label lb = (Label) node;
                lb.setText("" + GridPane.getRowIndex(lb));
            }
        }
//        if (contentGrid.getRowConstraints().size() == 0) {
        updateOnEmptyRows();
//        }
//        System.err.println("contentGrid.size = " + contentGrid.getChildren().size());
//        System.err.println("handleRowRemoved update false");        
        update = false;
    }

    private void handleRowAdded(Change<? extends RowConstraints> change) {
        update = true;
//        System.err.println("handleRowAdded update true");        
        int offset = change.getTo() - change.getFrom();
        for (Node node : leftGrid.getChildren()) {

            if (!(node instanceof LabelEx)) {
                continue;
            }
            int idx = GridPane.getRowIndex(node);
            if (idx >= change.getFrom()) {
                GridPane.setRowIndex(node, idx + offset);
            }
        }

        for (int i = change.getFrom(); i < change.getTo(); i++) {
            RowConstraints added = contentGrid.getRowConstraints().get(i);
            double minHeight = added.getPercentHeight() >= 0 ? added.getMinHeight() : (added.getMinHeight() < 0 ? 20 : added.getMinHeight());
            double prefHeight = added.getPercentHeight() >= 0 ? added.getPrefHeight() : (added.getPrefHeight() < 0 ? 20 : added.getPrefHeight());
            double maxHeight = added.getPercentHeight() >= 0 ? added.getMaxHeight() : (added.getMaxHeight() < 0 ? 20 : added.getMaxHeight());
            addRowConstraints(i, minHeight, prefHeight, maxHeight, 20);
        }

        for (Node node : contentGrid.getChildren()) {
            if (!node.isManaged()) {
                continue;
            }
            int idx = GridPane.getRowIndex(node);
            if (idx >= change.getFrom() && idx < contentGrid.getRowConstraints().size() - offset) {
                GridPane.setRowIndex(node, idx + offset);
            }
        }
        for (Node node : leftGrid.getChildren()) {
            if (node instanceof LabelEx) {
                Label lb = (Label) node;
                lb.setText("" + GridPane.getRowIndex(lb));
            }
        }
//        System.err.println("handleRowAdded update false");                
        update = false;
    }

    private void removeNodesForColumn(int columnIndex) {
        List<Node> nodes = new ArrayList<>();
        contentGrid.getChildren().forEach(node -> {
            if (node.isManaged() && GridPane.getColumnIndex(node) != null && GridPane.getColumnIndex(node) == columnIndex) {
                nodes.add(node);
            }
        });
        nodes.forEach(node -> {
            contentGrid.getChildren().remove(node);
        });

    }

    private void removeNodesForRow(int rowIndex) {
        List<Node> nodes = new ArrayList<>();
        contentGrid.getChildren().forEach(node -> {
            if (node.isManaged() && GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) == rowIndex) {
                nodes.add(node);
            }
        });
        nodes.forEach(node -> {
            contentGrid.getChildren().remove(node);
        });

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

    protected LabelEx getSelected(Side side) {
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

    /*    protected void onAddAction(Constraints constraints, Constraints contentConstraints, MenuItem mi, Label targeLabel, int idx) {

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
     */
 /*    protected void createContextMenu(Constraints constraints, Constraints contentConstraints, Label targetLabel) {

        ContextMenu menu = new ContextMenu();

        final MenuItem miDelete = new MenuItem("Delete");
        menu.getItems().add(miDelete);
        miDelete.setOnAction(e -> {
            
            update = true;
            dividers.hide();

            int idx = constraints.getIndex(targetLabel);
//            System.err.println("idx = " + idx + "; targetLabel = " + targetLabel);
            List<Node> list = new ArrayList<>();
            contentGrid.getChildren().forEach(node -> {
                if (!DividerLine.isDividerLine(node) && contentConstraints.getIndex(node) == idx) {
                    list.add(node);
                }
            });
            list.forEach(node -> {

                contentGrid.getChildren().remove(node);
            });

            constraints.remove(idx);
//            System.err.println("0) topGrid children size = " + topGrid.getChildren().size());

            constraints.removeChild(targetLabel);
//            System.err.println("topGrid columnconstraints size = " + topGrid.getColumnConstraints().size());
//            System.err.println("topGrid children size = " + topGrid.getChildren().size());
//            System.err.println("constraints = " + constraints);
            //topGrid.getChildren().forEach(n -> {
            //System.err.println("   --- node = " + n);
            //});
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
//            if ( contentGrid.getColumnConstraints().size() == 0 ) {
            updateOnEmptyColumns();
//            }            
            dividers.show();
            //show();
            update = false;
        });
        menu.getItems().add(new SeparatorMenuItem());

        final MenuItem miBefore = new MenuItem("Add Before");
        menu.getItems().add(miBefore);
        miBefore.setOnAction(e -> {

            int idx = constraints.getIndex(targetLabel);
            contentConstraints.addNew(idx);


        });

        final MenuItem miAfter = new MenuItem("Add After");
        menu.getItems().add(miAfter);
        miAfter.setOnAction(e -> {
            update = true;
            dividers.hide();
            updateOnEmptyColumns();
            int idx = constraints.getIndex(targetLabel);
            onAddAction(constraints, contentConstraints, miAfter, targetLabel, ++idx);
            dividers.show();
            update = false;
        });

        targetLabel.setContextMenu(menu);

    }
     */
    protected void createColumnContextMenu(Label targetLabel) {

        ContextMenu menu = new ContextMenu();

        final MenuItem miDelete = new MenuItem("Delete");
        menu.getItems().add(miDelete);

        miDelete.setOnAction(e -> {
            int idx = GridPane.getColumnIndex(targetLabel);
            contentGrid.getColumnConstraints().remove(idx);
        });

        final MenuItem miDeleteWithNodes = new MenuItem("Delete with nodes");
        menu.getItems().add(miDeleteWithNodes);

        miDeleteWithNodes.setOnAction(e -> {
            int idx = GridPane.getColumnIndex(targetLabel);
            removeNodesForColumn(idx);
            contentGrid.getColumnConstraints().remove(idx);
        });
        menu.getItems().add(new SeparatorMenuItem());

        final MenuItem miBefore = new MenuItem("Add Before");
        menu.getItems().add(miBefore);
        miBefore.setOnAction(e -> {
            int idx = GridPane.getColumnIndex(targetLabel);
            ColumnConstraints cc = new ColumnConstraints(20, 20, 20);
            contentGrid.getColumnConstraints().add(idx, cc);
        });

        final MenuItem miAfter = new MenuItem("Add After");
        menu.getItems().add(miAfter);

        miAfter.setOnAction(e -> {
            int idx = GridPane.getColumnIndex(targetLabel);
            ColumnConstraints cc = new ColumnConstraints(20, 20, 20);
            contentGrid.getColumnConstraints().add(idx + 1, cc);
        });

        targetLabel.setContextMenu(menu);

    }

    protected void createRowContextMenu(Label targetLabel) {

        ContextMenu menu = new ContextMenu();

        final MenuItem miDelete = new MenuItem("Delete");
        menu.getItems().add(miDelete);

        miDelete.setOnAction(e -> {
            int idx = GridPane.getRowIndex(targetLabel);
            contentGrid.getRowConstraints().remove(idx);
        });

        final MenuItem miDeleteWithNodes = new MenuItem("Delete with nodes");
        menu.getItems().add(miDeleteWithNodes);

        miDeleteWithNodes.setOnAction(e -> {
            int idx = GridPane.getRowIndex(targetLabel);
            removeNodesForRow(idx);
            contentGrid.getRowConstraints().remove(idx);
        });
        menu.getItems().add(new SeparatorMenuItem());

        final MenuItem miBefore = new MenuItem("Add Before");
        menu.getItems().add(miBefore);
        miBefore.setOnAction(e -> {
            int idx = GridPane.getRowIndex(targetLabel);
            RowConstraints cc = new RowConstraints(20, 20, 20);
            contentGrid.getRowConstraints().add(idx, cc);
        });

        final MenuItem miAfter = new MenuItem("Add After");
        menu.getItems().add(miAfter);

        miAfter.setOnAction(e -> {
            int idx = GridPane.getRowIndex(targetLabel);
            RowConstraints cc = new RowConstraints(20, 20, 20);
            contentGrid.getRowConstraints().add(idx + 1, cc);
        });

        targetLabel.setContextMenu(menu);

    }

    protected void updateOnEmptyColumns() {
//        System.err.println("updateOnEmptyColumns 1 ");
        if (contentGrid.getColumnConstraints().isEmpty()) {
//            System.err.println("updateOnEmptyColumns 2");
            Label lb = new Label("");
            lb.getStyleClass().add("empty");
            topGrid.getChildren().clear();
            topGrid.getChildren().add(lb);

            double w = 0;
            w = contentGrid.getBoundsInParent().getWidth() - contentGrid.getInsets().getLeft() - contentGrid.getInsets().getRight();
            topGrid.setPrefWidth(w);
            topGrid.setMaxWidth(2000);
            topGrid.setMinWidth(w);
            topGrid.setPrefHeight(20);
            topGrid.setMaxHeight(1000);
            topGrid.setMinHeight(20);

            lb.setPrefHeight(20);
            lb.setMaxHeight(1000);
            lb.setMinHeight(20);
            lb.setPrefWidth(w);
            lb.setMaxWidth(2000);
            lb.setMinWidth(w);
            lb.setContextMenu(topGridContextMenu);
            lb.setTooltip(topGridContextTooltip);
        } else {
            Label lb = (Label) topGrid.lookup(".empty");
            if (lb != null) {
                topGrid.getChildren().remove(lb);
//                System.err.println("topGrid remove empty lb");
                //adjust();
                //resizeRelocate(null);
            }
        }
    }

    protected void updateOnEmptyRows() {
        if (contentGrid.getRowConstraints().isEmpty()) {
            Label lb = new Label("");
            lb.getStyleClass().add("empty");
            leftGrid.getChildren().clear();
            leftGrid.getChildren().add(lb);

            double w = 0;
            w = contentGrid.getBoundsInParent().getHeight() - contentGrid.getInsets().getTop() - contentGrid.getInsets().getBottom();
            leftGrid.setPrefHeight(w);
            leftGrid.setMaxHeight(2000);
            leftGrid.setMinHeight(w);
            leftGrid.setPrefWidth(20);
            leftGrid.setMaxWidth(2000);
            leftGrid.setMinWidth(20);

            lb.setPrefWidth(20);
            lb.setMaxWidth(2000);
            lb.setMinWidth(20);
            lb.setPrefHeight(w);
            lb.setMaxHeight(2000);
            lb.setMinHeight(w);
            lb.setContextMenu(leftGridContextMenu);
            lb.setTooltip(leftGridContextTooltip);
        } else {
            Label lb = (Label) leftGrid.lookup(".empty");
            if (lb != null) {
                leftGrid.getChildren().remove(lb);
//                System.err.println("leftGrid remove empty lb");
            }
        }
    }

    protected void adjust() {
        //System.err.println("ADJUST 1");
        topGrid.getRowConstraints().clear();
        topGrid.getColumnConstraints().clear();
        topGrid.getChildren().clear();
        RowConstraints rc = new RowConstraints();
        topGrid.getRowConstraints().add(rc);
        //System.err.println("ADJUST 2");
        for (int i = 0; i < contentGrid.getColumnConstraints().size(); i++) {
            //System.err.println("1) adjust columns before getCellBounds");
            Bounds b = JdkUtil.getGridCellBounds(contentGrid, i, 0);
            //System.err.println("2) adjust columns after getCellBounds b = " + b);
            rc.setVgrow(Priority.ALWAYS);
            rc.setFillHeight(true);
            rc.setValignment(VPos.BASELINE);

            addColumnConstraints(i, b.getWidth(), b.getWidth(), b.getWidth(), b.getHeight());

            /*            ColumnConstraints c = new ColumnConstraints(b.getWidth(), b.getWidth(), b.getWidth());
            topGrid.getColumnConstraints().add(c);

            Label lb = new LabelEx("" + i);
            if (!lb.getStyleClass().contains("labelex")) {
                lb.getStyleClass().add("labelex");
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
             */
        }

        ColumnConstraints c = new ColumnConstraints();
        leftGrid.getRowConstraints().clear();
        leftGrid.getColumnConstraints().clear();
        leftGrid.getChildren().clear();

        leftGrid.getColumnConstraints().add(c);
        //c.setHgrow(Priority.ALWAYS);
        //c.setFillWidth(true);

        for (int i = 0; i < contentGrid.getRowConstraints().size(); i++) {
            Bounds b = JdkUtil.getGridCellBounds(contentGrid, 0, i);
            //System.err.println("2) adjust columns after getCellBounds b = " + b);
            c.setHgrow(Priority.ALWAYS);
            c.setFillWidth(true);
            c.setHalignment(HPos.CENTER);
            addRowConstraints(i, b.getHeight(), b.getHeight(), b.getHeight(), 20);

            /*            addColumnConstraints(i, b.getWidth(), b.getWidth(), b.getWidth(), b.getHeight());

            //System.err.println("adjust rows before getCellBounds" );            
            Bounds b = JdkUtil.getGridCellBounds(contentGrid, 0, i);
            //System.err.println("adjust rows after getCellBounds b = " + b);            
            rc = new RowConstraints(b.getHeight(), b.getHeight(), b.getHeight());
            Label lb = new LabelEx("" + i);
            if (!lb.getStyleClass().contains("labelex")) {
                lb.getStyleClass().add("labelex");

            }
            if (!lb.getStyleClass().contains(FOREIGN)) {
                lb.getStyleClass().add(FOREIGN);
            }

            createRowContextMenu(lb);

            lb.setMinHeight(0);
            lb.setMaxWidth(1000);
            lb.setMaxHeight(1000);
            lb.setPrefHeight(b.getHeight());

            lb.setAlignment(Pos.CENTER);
            //lb.setStyle("-fx-border-width: 1; -fx-border-color: black; -fx-border-radius: 3 0 0 3; -fx-background-color: lightgrey");
            leftGrid.add(lb, 0, i);
            leftGrid.getRowConstraints().add(rc);
             */
        }

    }

    private void addColumnConstraints(int idx, double minWidth, double prefWidth, double maxWidth, double rowHeight) {
//        System.err.println("minW =" + minWidth + "; prefW = " + prefWidth + "; maxW = " + maxWidth + "; rh = " + rowHeight);
        ColumnConstraints c = new ColumnConstraints(minWidth, prefWidth, maxWidth);
        topGrid.getColumnConstraints().add(idx, c);

        Label lb = new LabelEx("" + idx);
        if (!lb.getStyleClass().contains("labelex")) {
            lb.getStyleClass().add("labelex");
        }
        if (!lb.getStyleClass().contains(FOREIGN)) {
            lb.getStyleClass().add(FOREIGN);
        }

        //createContextMenu(new Constraints(Side.TOP, topGrid), new Constraints(Side.TOP, contentGrid), lb);
        createColumnContextMenu(lb);

        lb.setMaxWidth(1000);
        lb.setMaxHeight(1000);
        lb.setPrefHeight(rowHeight);
        //lb.setMinWidth(prefWidth);
/*        if ( prefWidth < 0 ) {
            lb.setMinWidth(5);
        } else {
            lb.setMinWidth(prefWidth);
        }
         */

        lb.setAlignment(Pos.CENTER);
        //b.setStyle("-fx-border-width: 2; -fx-border-color: aqua; -fx-border-radius: 3 3 0 0; -fx-background-color: lightgrey");
        topGrid.add(lb, idx, 0);
        updateOnEmptyColumns();
    }

    private void addRowConstraints(int idx, double minHeight, double prefHeight, double maxHeight, double rowWidth) {
        //System.err.println("minW =" + minHeight + "; prefW = " + prefHeight + "; maxW = " + maxHeight + "; rh = " + rowWidth);
        RowConstraints c = new RowConstraints(minHeight, prefHeight, maxHeight);
        leftGrid.getRowConstraints().add(idx, c);

        Label lb = new LabelEx("" + idx);
        if (!lb.getStyleClass().contains("labelex")) {
            lb.getStyleClass().add("labelex");
        }
        if (!lb.getStyleClass().contains(FOREIGN)) {
            lb.getStyleClass().add(FOREIGN);
        }

        createRowContextMenu(lb);

        lb.setMaxWidth(2000);
        lb.setMaxHeight(2000);
        lb.setPrefWidth(rowWidth);
        //lb.setMinHeight(prefHeight);

        lb.setAlignment(Pos.CENTER);
        //b.setStyle("-fx-border-width: 2; -fx-border-color: aqua; -fx-border-radius: 3 3 0 0; -fx-background-color: lightgrey");
        leftGrid.add(lb, 0, idx);
        updateOnEmptyRows();
    }

    @Override
    protected void layoutChildren() {
        try {
            //System.err.println("layoutChildren");
            Bounds sceneBounds = contentGrid.localToScene(contentGrid.getBoundsInLocal());
            Insets ins = contentGrid.getInsets();
            topGrid.setLayoutX(sceneBounds.getMinX() + ins.getLeft());
            /*            Bounds cellBounds = JdkUtil.getGridCellBounds(topGrid, 0, 0);
            if (cellBounds != null) {
                double d = cellBounds.getHeight();
                topGrid.setLayoutY(sceneBounds.getMinY() - d + ins.getTop());

                if (!leftGrid.getColumnConstraints().isEmpty()) {
                    //System.err.println("layoutchildren d = " + d);
                    ColumnConstraints c = leftGrid.getColumnConstraints().get(0);
                    c.setMinWidth(d);
                    c.setMaxWidth(d);
                    c.setPrefWidth(d);
                }
                leftGrid.setLayoutX(sceneBounds.getMinX() + ins.getLeft() - d);
                leftGrid.setLayoutY(sceneBounds.getMinY() + ins.getTop());
             */
            double d = 20;

            topGrid.setLayoutY(sceneBounds.getMinY() - d + ins.getTop());
            if (!topGrid.getRowConstraints().isEmpty()) {
                //System.err.println("layoutchildren d = " + d);
                RowConstraints c = topGrid.getRowConstraints().get(0);
                c.setMinHeight(d);
                c.setMaxHeight(d);
                c.setPrefHeight(d);
            }
            if (!leftGrid.getColumnConstraints().isEmpty()) {
                //System.err.println("layoutchildren d = " + d);
                ColumnConstraints c = leftGrid.getColumnConstraints().get(0);
                c.setMinWidth(d);
                c.setMaxWidth(d);
                c.setPrefWidth(d);
            }
            leftGrid.setLayoutX(sceneBounds.getMinX() + ins.getLeft() - d);
            leftGrid.setLayoutY(sceneBounds.getMinY() + ins.getTop());
        } catch (Exception ex) {
//            System.err.println("layoutChildren EXCEPTION ex = " + ex.getMessage());
        }
    }

/*    public void resize(Side side) {
        if (!update) {
            return;
        }
        try {
            for (int i = 0; i < contentGrid.getColumnConstraints().size(); i++) {

                ColumnConstraints c = topGrid.getColumnConstraints().get(i);
                double w = 20;
                c.setMinWidth(w);
                c.setMaxWidth(w);
                c.setPrefWidth(w);
                ColumnConstraints cc = contentGrid.getColumnConstraints().get(i);
                cc.setMinWidth(w);
                cc.setMaxWidth(w);
                cc.setPrefWidth(w);

            }
            if (side == Side.TOP) {
                for (int i = 0; i < contentGrid.getRowConstraints().size(); i++) {

                    RowConstraints rc = leftGrid.getRowConstraints().get(i);
                    double w = contentGrid.getRowConstraints().get(i).getPrefHeight();
                    System.err.println("H = " + w);
                    System.err.println("rc H = " + rc.getPrefHeight());
                    rc.setMinHeight(w);
                    rc.setMaxHeight(w);
                    rc.setPrefHeight(w);

                    RowConstraints cc = contentGrid.getRowConstraints().get(i);
//                    cc.setMinHeight(w);
//                    cc.setMaxHeight(w);
//                    cc.setPrefHeight(w);

                }
            }
            layoutChildren();
        } catch (Exception ex) {
        }
    }
*/
    @Override
    public void resizeRelocate(Bounds bounds) {
        if (update) {
            //System.err.println("UPDATE");
            //return;
        }
        try {
            for (int i = 0; i < contentGrid.getColumnConstraints().size(); i++) {
                Bounds b = JdkUtil.getGridCellBounds(contentGrid, i, 0);
                ColumnConstraints c = topGrid.getColumnConstraints().get(i);
                double w = 20;
                if ( b != null ) {
                    w = b.getWidth();
                }
                /*                if (b.getWidth() <= 5) {
                    c.setMinWidth(5);
                    c.setMaxWidth(5);
                    c.setPrefWidth(5);
                } else {
                 */
                c.setMinWidth(b.getWidth());
                c.setMaxWidth(b.getWidth());
                c.setPrefWidth(b.getWidth());
//                }
            }
            for (int i = 0; i < contentGrid.getRowConstraints().size(); i++) {
                Bounds b = JdkUtil.getGridCellBounds(contentGrid, 0, i);
                RowConstraints rc = leftGrid.getRowConstraints().get(i);
                /*                if (b.getHeight() <= 5) {
                    rc.setMinHeight(5);
                    rc.setMaxHeight(5);
                    rc.setPrefHeight(5);
                } else {
                 */
                double h = 20;
                if ( b != null ) {
                    h = b.getHeight();
                }                
                rc.setMinHeight(h);
                rc.setMaxHeight(h);
                rc.setPrefHeight(h);
//                }
            }

            layoutChildren();

        } catch (Exception ex) {
//            System.err.println("resizeRelocate EXCEPTION ex = " + ex.getMessage());
        }
    }

    /*    public static class Constraints {

        private final Side side;
        private final GridPane grid;

        public Constraints(Side side, GridPane grid) {
            this.side = side;
            this.grid = grid;
        }

        public int getIndex(Node node) {
            int retval = -1;
            if (!node.isManaged()) {
                return -1;
            }
            if (side == Side.TOP || side == Side.BOTTOM) {
                //System.err.println("node = " + node);
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
     */
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
                ((GridPaneFrame1) getParent().getParent()).setSelected(this);
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
