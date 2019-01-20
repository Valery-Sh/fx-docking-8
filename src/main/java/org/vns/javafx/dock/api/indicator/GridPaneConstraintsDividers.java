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
package org.vns.javafx.dock.api.indicator;

import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.ConstraintsBase;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import org.vns.javafx.JdkUtil;
import org.vns.javafx.dock.api.Util;
import static org.vns.javafx.dock.api.Constants.FOREIGN;
import org.vns.javafx.dock.api.resizer.DividerLine;
import org.vns.javafx.dock.api.resizer.Dividers;

/**
 *
 * @author Valery
 */
public class GridPaneConstraintsDividers implements Dividers {

    private Service<ObjectProperty<Bounds>> service;

    private ObservableList<GridCellBoundsObservable> gridCellBoundsObservables = FXCollections.observableArrayList();
    private boolean resizable;

    private Group lineGroup;
    private boolean showing;
    private boolean resizing;

    private final GridPane gridPane;
    private final ObservableList<DividerLine> rowDividers = FXCollections.observableArrayList();
    private final ObservableList<DividerLine> columnDividers = FXCollections.observableArrayList();

 
    private final ListChangeListener rowConstraintsListener = (ListChangeListener<RowConstraints>) (ListChangeListener.Change<? extends RowConstraints> change) -> {
        if (!isShowing()) {
            return;
        }
        hide();
        show();

        while (change.next()) {
            if (change.wasPermutated()) {
           } else if (change.wasUpdated()) {
            } else if (change.wasReplaced()) {
            } else {
                if (change.wasRemoved()) {
                } else if (change.wasAdded()) {
                }
            }
        }
    };
    private final ListChangeListener columnConstraintsListener = (ListChangeListener<ColumnConstraints>) (ListChangeListener.Change<? extends ColumnConstraints> c) -> {
        if (!isShowing()) {
            return;
        }
        hide();
        show();
    };

    public GridPaneConstraintsDividers(GridPane gridPane) {
        this(gridPane, true);
    }

    public GridPaneConstraintsDividers(GridPane gridPane, boolean resizable) {
        this.gridPane = gridPane;
        this.resizable = resizable;
        init();
    }

    public ObservableList<GridCellBoundsObservable> getGridCellBoundsObservables() {
        return gridCellBoundsObservables;
    }

    public ObservableList<DividerLine> getRowDividers() {
        return rowDividers;
    }

    public ObservableList<DividerLine> getColumnDividers() {
        return columnDividers;
    }

    public ConstraintsBase getConstraints(DividerLine line) {
        int idx;
        if (line.getOrientation() == Orientation.HORIZONTAL) {
            idx = rowDividers.indexOf(line);
            if (idx < 0) {
                return null;
            }
            return gridPane.getRowConstraints().get(idx);
        } else {
            idx = columnDividers.indexOf(line);
            if (idx < 0) {
                return null;
            }
            return gridPane.getColumnConstraints().get(idx);
        }
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    private void init() {
        lineGroup = new Group();
        lineGroup.setManaged(false);
        lineGroup.getStyleClass().add(FOREIGN);
        gridPane.getRowConstraints().addListener(rowConstraintsListener);
        gridPane.getColumnConstraints().addListener(columnConstraintsListener);
        if (!resizable) {
            return;
        }
        service = new Service<ObjectProperty<Bounds>>() {
            @Override
            protected Task<ObjectProperty<Bounds>> createTask() {
                return new GridCellChangeTask(GridPaneConstraintsDividers.this);
            }
        };
        service.start();
    }

    public boolean isResizing() {
        return resizing;
    }

    public void setResizing(boolean resizing) {
        this.resizing = resizing;
    }

    @Override
    public boolean isShowing() {
        return showing;
    }

    @Override
    public void setResizable(boolean resizable) {
        this.resizable = resizable;
    }

    protected void restore() {
        if (isShowing()) {
            return;
        }
        gridPane.getRowConstraints().forEach(c -> {
            constraintsShow(c);
        });
        gridPane.getColumnConstraints().forEach(c -> {
            constraintsShow(c);
        });
        gridPane.getChildren().add(lineGroup);
        //DockUtil.getChildren(gridPane.getScene().getRoot()).add(lineGroup);
        showing = true;
    }

    @Override
    public void show() {
        if (isShowing()) {
            return;
        }
        restore();
        resizeRelocate((Bounds) null);
        if (!resizable) {
            return;
        }
        if (service.getState() == State.READY) {
            service.start();
        }
    }

    protected void constraintsShow(ConstraintsBase constraints) {
        if (constraints instanceof RowConstraints) {
            DividerLine line = new DividerLine(Orientation.HORIZONTAL);

            rowDividers.add(line);
            line.setResizer(new GridLineResizer(this, line));

            lineGroup.getChildren().add(line);
            line.show(resizable);

        } else {
            DividerLine line = new DividerLine(Orientation.VERTICAL);
            columnDividers.add(line);
            line.setResizer(new GridLineResizer(this, line));

            lineGroup.getChildren().add(line);
            line.show(resizable);
        }
    }

    public void resizeRelocate(Bounds newCellBounds) {
        if (service != null && !service.isRunning()) {
            return;
        }
        try {
            
            Bounds bnds = JdkUtil.getGridCellBounds(gridPane, columnDividers.size() - 1, rowDividers.size() - 1);
            if ( bnds == null ) {
                return;
            }
        } catch (Exception ex) {
            
            return;
        }

        Bounds gridBounds = gridPane.localToScene(gridPane.getLayoutBounds());

        if (rowDividers.isEmpty() && columnDividers.isEmpty()) {
            return;
        }
        double w = 0;
        double h = 0;

        Bounds cellBounds = JdkUtil.getGridCellBounds(gridPane, 0, 0);
        
        //double x = cellBounds.getMinX() + gridBounds.getMinX();
        //double y = cellBounds.getMinY() + gridBounds.getMinY();
        double x = cellBounds.getMinX();
        double y = cellBounds.getMinY();
        
        final Bounds lastCellBounds;
        try {
            if (columnDividers.isEmpty()) {
                w = cellBounds.getWidth() - cellBounds.getMinX();
            } else if (rowDividers.isEmpty()) {
                h = cellBounds.getHeight() - cellBounds.getMinY();
            } else {
                lastCellBounds = JdkUtil.getGridCellBounds(gridPane, columnDividers.size() - 1, rowDividers.size() - 1);
                w = lastCellBounds.getMinX() + lastCellBounds.getWidth() - cellBounds.getMinX();
                h = lastCellBounds.getMinY() + lastCellBounds.getHeight() - cellBounds.getMinY();
            }

            DividerLine line;
            for (int i = 0; i < rowDividers.size(); i++) {
                cellBounds = JdkUtil.getGridCellBounds(gridPane, 0, i);
                line = rowDividers.get(i);

                line.setLayoutX(x);
                line.setPrefWidth(w);
                //2.01line.setLayoutY(gridBounds.getMinY() + cellBounds.getMinY() + cellBounds.getHeight() - line.getOffset());
                line.setLayoutY(cellBounds.getMinY() + cellBounds.getHeight() - line.getOffset());
            }
            for (int j = 0; j < columnDividers.size(); j++) {
                cellBounds = JdkUtil.getGridCellBounds(gridPane, j, 0);
                line = columnDividers.get(j);

                line.setLayoutY(y);
                line.setPrefHeight(h);
                line.setLayoutX(cellBounds.getMinX() + cellBounds.getWidth() - line.getOffset());
            }
        } catch (Exception ex) {
        }
    }

    public void resizeRelocate(CellBounds newCellBounds) {
        Bounds gridBounds = gridPane.localToScene(gridPane.getLayoutBounds());

        if (rowDividers.isEmpty() && columnDividers.isEmpty()) {
            return;
        }
        double w = 0;
        double h = 0;

        Bounds cellBounds = JdkUtil.getGridCellBounds(gridPane, 0, 0);
//        double x = cellBounds.getMinX() + gridBounds.getMinX();
//        double y = cellBounds.getMinY() + gridBounds.getMinY();
        double x = cellBounds.getMinX();
        double y = cellBounds.getMinY();

        final Bounds lastCellBounds;

        if (columnDividers.isEmpty()) {
            w = cellBounds.getWidth() - cellBounds.getMinX();
        } else if (rowDividers.isEmpty()) {
            h = cellBounds.getHeight() - cellBounds.getMinY();
        } else {
            lastCellBounds = JdkUtil.getGridCellBounds(gridPane, columnDividers.size() - 1, rowDividers.size() - 1);
            w = lastCellBounds.getMinX() + lastCellBounds.getWidth() - cellBounds.getMinX();
            h = lastCellBounds.getMinY() + lastCellBounds.getHeight() - cellBounds.getMinY();
        }

        DividerLine line;
        for (int i = 0; i < newCellBounds.getRowBounds().size(); i++) {
            cellBounds = newCellBounds.getRowBounds().get(i);
            line = rowDividers.get(i);

            line.setLayoutX(x);
            line.setPrefWidth(w);
            line.setLayoutY(gridBounds.getMinY() + cellBounds.getMinY() + cellBounds.getHeight() - line.getOffset());
        }

        for (int j = 0; j < newCellBounds.getColumnBounds().size(); j++) {
            cellBounds = newCellBounds.getColumnBounds().get(j);
            line = columnDividers.get(j);
            line.setLayoutY(y);
            line.setPrefHeight(h);

            line.setLayoutX(gridBounds.getMinX() + cellBounds.getMinX() + cellBounds.getWidth() - line.getOffset());
        }
    }

    @Override
    public void hide() {
        if (!resizable) {
            reset();
            return;
        }
        service.cancel();
        service.reset();
        if (service.getState() == State.READY) {
            reset();
        } else {
            service.setOnReady(v -> {
                reset();
            });
        }
    }

    protected void reset() {
        showing = false;
        rowDividers.forEach(d -> {
            d.hide();
        });
        columnDividers.forEach(d -> {
            d.hide();
        });
        lineGroup.getChildren().removeAll(rowDividers);
        lineGroup.getChildren().removeAll(columnDividers);
        rowDividers.clear();
        columnDividers.clear();
        gridPane.getChildren().remove(lineGroup);
        //DockUtil.getChildren(gridPane.getScene().getRoot()).remove(lineGroup);

    }

    public static class GridLineResizer extends DividerLine.DefaultResizer {

        private double minValue;
        private double maxValue;
        private double prefValue;
        private double percentValue;

        private double saveMinValue;
        private double saveMaxValue;
        private double savePrefValue;
        private double savePercentValue;

        private final GridPaneConstraintsDividers dividers;

        public GridLineResizer(GridPaneConstraintsDividers dividers, DividerLine dividerLine) {
            super(dividerLine);
            this.dividers = dividers;
        }

        public GridPaneConstraintsDividers getDividers() {
            return dividers;
        }

        @Override
        public void start(MouseEvent ev, Cursor cursor, Cursor... supportedCursors) {
            getDividers().setResizing(true);
            super.start(ev, cursor, supportedCursors);
        }

        @Override
        public void finish() {
            super.finish();
            getDividers().setResizing(false);
        }

        @Override
        protected void setSize() {
            if (getDividerLine().getOrientation() == Orientation.HORIZONTAL) {
                RowConstraints c = (RowConstraints) dividers.getConstraints(getDividerLine());
                percentValue = c.getPercentHeight();
                minValue = c.getMinHeight();
                if (c.getMinHeight() == Region.USE_PREF_SIZE) {
                    minValue = c.getPrefHeight();
                }
                maxValue = c.getMaxHeight();
                if (c.getMaxHeight() == Region.USE_PREF_SIZE) {
                    maxValue = c.getPrefHeight();
                }
                prefValue = c.getPrefHeight();
            } else {
                ColumnConstraints c = (ColumnConstraints) dividers.getConstraints(getDividerLine());
                percentValue = c.getPercentWidth();
                minValue = c.getMinWidth();
                if (c.getMinWidth() == Region.USE_PREF_SIZE) {
                    minValue = c.getPrefWidth();
                }
                maxValue = c.getMaxWidth();
                if (c.getMaxWidth() == Region.USE_PREF_SIZE) {
                    maxValue = c.getPrefWidth();
                }
                prefValue = c.getPrefWidth();
            }
            saveMinValue = minValue;
            saveMaxValue = maxValue;
            savePrefValue = prefValue;
            savePercentValue = percentValue;
        }

        private boolean isFixedSize() {
            boolean retval = false;
            double min;
            double max;
            double pref;
            if (percentValue == -1 && minValue == prefValue && maxValue == prefValue && prefValue != -1) {
                retval = true;
            } else if (percentValue == -1 && minValue == prefValue && maxValue == prefValue && prefValue == -1) {
                return false;
            }
//            if ( true ) return false;
            if (getDividerLine().getOrientation() == Orientation.HORIZONTAL) {
                RowConstraints c = (RowConstraints) dividers.getConstraints(getDividerLine());
                if (c.getPercentHeight() != -1) {
                    return false;
                }
                min = c.getMinHeight();
                if (c.getMinHeight() == Region.USE_PREF_SIZE) {
                    min = c.getPrefHeight();
                }
                max = c.getMaxHeight();
                if (c.getMaxHeight() == Region.USE_PREF_SIZE) {
                    max = c.getPrefHeight();
                }
                pref = c.getPrefHeight();
                if (min == pref && max == pref && pref != -1) {
                    retval = true;
                }
            } else {
                ColumnConstraints c = (ColumnConstraints) dividers.getConstraints(getDividerLine());
                if (c.getPercentWidth() != -1) {
                    return false;
                }
                min = c.getMinWidth();
                if (c.getMinWidth() == Region.USE_PREF_SIZE) {
                    min = c.getPrefWidth();
                }
                max = c.getMaxWidth();
                if (c.getMaxWidth() == Region.USE_PREF_SIZE) {
                    max = c.getPrefWidth();
                }
                pref = c.getPrefWidth();
                if (min == pref && max == pref) {
                    retval = true;
                }
            }
            return retval;
        }

        @Override
        public boolean isAcceptableY(double delta) {
            if (getDividerLine().getOrientation() == Orientation.VERTICAL) {
                return false;
            }
            boolean retval = false;
            if (isFixedSize()) {
                if (maxValue + delta >= 0) {
                    retval = true;
                }
            } else if (percentValue >= 0 && percentValue + delta / 5 >= 0) {
                retval = true;
            } else { //if ( prefValue + delta >= 0 && (prefValue + delta >= minValue) && ( maxValue >= 0 && prefValue + delta <= maxValue || maxValue == -1) ) {
                retval = getCellBounds().getHeight() + delta >= 0;
            }
            return retval;
        }

        @Override
        public boolean isAcceptableX(double delta) {
            if (getDividerLine().getOrientation() == Orientation.HORIZONTAL) {
                return false;
            }

            boolean retval = false;
            if (isFixedSize()) {
                if (maxValue + delta >= 0) {
                    retval = true;
                }
            } else if (percentValue >= 0 && percentValue + delta / 5 >= 0) {
                retval = true;
            } else { //if ( prefValue + delta >= 0 && (prefValue + delta >= minValue) && ( maxValue >= 0 && prefValue + delta <= maxValue || maxValue == -1) ) {
                retval = getCellBounds().getWidth() + delta >= 0;
            }
            return retval;
        }

        @Override
        protected void resetToDefault() {
            if (getDividerLine().getOrientation() == Orientation.HORIZONTAL) {
                RowConstraints c = (RowConstraints) getDividers().getConstraints(getDividerLine());
                c.setMaxHeight(saveMaxValue);
                c.setMinHeight(saveMinValue);
                c.setPrefHeight(savePrefValue);
                c.setPercentHeight(savePercentValue);
            } else {
                ColumnConstraints c = (ColumnConstraints) getDividers().getConstraints(getDividerLine());
                c.setMaxWidth(saveMaxValue);
                c.setMinWidth(saveMinValue);
                c.setPrefWidth(savePrefValue);
                c.setPercentWidth(savePercentValue);
            }
        }

        @Override
        public void relocateY(double delta) {
            GridPane g = getDividers().getGridPane();
            RowConstraints c = (RowConstraints) getDividers().getConstraints(getDividerLine());
            if (isFixedSize()) {
                c.setMaxHeight(maxValue + delta);
                c.setMinHeight(minValue + delta);
                c.setPrefHeight(prefValue + delta);
            } else if (percentValue >= 0) {
                c.setPercentHeight(percentValue + delta / 5);
            } else {
                double w = getCellBounds().getHeight() + delta;
                c.setPrefHeight(w);
                c.setMinHeight(w);
                c.setMaxHeight(w);
                minValue = w;
                maxValue = w;
                prefValue = w;
                percentValue = -1;

            }
        }

        @Override
        public void relocateX(double delta) {
            GridPane g = getDividers().getGridPane();
            ColumnConstraints c = (ColumnConstraints) getDividers().getConstraints(getDividerLine());
            if (isFixedSize()) {
                c.setMaxWidth(maxValue + delta);
                c.setMinWidth(minValue + delta);
                c.setPrefWidth(prefValue + delta);
            } else if (percentValue >= 0) {
                c.setPercentWidth(percentValue + delta / 5);
            } else {
                double w = getCellBounds().getWidth() + delta;
                c.setMaxWidth(w);
                c.setMinWidth(w);
                c.setPrefWidth(w);
                minValue = w;
                maxValue = w;
                prefValue = w;
                percentValue = -1;
            }
        }

        public void updateOnRelease() {
            /*            ColumnConstraints c = (ColumnConstraints) getDividers().getConstraints(getDividerLine());
            
            //c.setMaxWidth(c.getMinWidth() - 1);
            //c.setPrefWidth(c.getMinWidth() - 1);

            System.err.println("updateOnRelease minValue  = " + minValue);
            System.err.println("updateOnRelease maxValue  = " + maxValue);
            System.err.println("updateOnRelease prefValue = " + prefValue);
            System.err.println("updateOnRelease cell width     w = " + getCellBounds().getWidth());
             */
        }

        protected Bounds getCellBounds() {
            Bounds bounds;
            int idx;
            if (getDividerLine().getOrientation() == Orientation.HORIZONTAL) {
                idx = getDividers().getRowDividers().indexOf(getDividerLine());
                bounds = JdkUtil.getGridCellBounds(getDividers().getGridPane(), 0, idx);
            } else {
                idx = getDividers().getColumnDividers().indexOf(getDividerLine());
                bounds = JdkUtil.getGridCellBounds(getDividers().getGridPane(), idx, 0);
            }
            return bounds;
        }
    }

    public static interface GridCellBoundsObservable {

        void resizeRelocate(Bounds bounds);
    }
}
