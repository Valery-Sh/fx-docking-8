/*
 * Copyright 2017 Your Organisation.
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
package org.vns.javafx.dock.api.demo;

import java.util.List;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import static javafx.scene.Node.BASELINE_OFFSET_SAME_AS_HEIGHT;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.ConstraintsBase;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.JdkUtil;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.indicator.GridPaneConstraintsDividers;

/**
 *
 * @author Valery
 */
public class TestResizeLine extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox root = new VBox();
        Button bb1 = new Button("bb1");
        Button bb2 = new Button("bb2");
        Button bb3 = new Button("bb3");
        Button bb4 = new Button("bb4");
        //bb3.setManaged(false);
        VBox vb1 = new VBox(bb1, bb2, bb3);
        //root.setStyle("-fx-background-color: YELLOW");
        Button b1 = new Button("Button b1");
        Button b2 = new Button("Button b2");
        Button b3 = new Button("Button b3");
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);
        gridPane.setPrefHeight(300);
        gridPane.setMinHeight(300);
        gridPane.setMaxHeight(300);
        gridPane.setPrefWidth(150);
        gridPane.setMinWidth(150);
        gridPane.setMaxWidth(150);

        gridPane.setStyle("-fx-background-color: yellow; -fx-border-width: 15; -fx-border-color: red");
        //gridPane.setPrefSize(100,150);
/*        DividerLine lineH = new DividerLine(Orientation.HORIZONTAL);
        lineH.setPrefWidth(110);

        DividerLine lineV = new DividerLine(Orientation.VERTICAL);
        
        lineV.setPrefHeight(50);
        lineH.show(gridPane, true);
         */
        GridPaneConstraintsDividers dividers = new GridPaneConstraintsDividers(gridPane);
        //dividers.show(true);
        //lineV.show(gridPane,true);    

        //root.getChildren().addAll(lineH, lineV);
        //GridPane.setValignment(lineH, VPos.CENTER);
        Scene scene = new Scene(root);
        ColumnConstraints cc0 = new ColumnConstraints();
        cc0.setHgrow(Priority.SOMETIMES);
        cc0.setMinWidth(75);
        cc0.setMaxWidth(75);
        cc0.setPrefWidth(75);

        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setHgrow(Priority.SOMETIMES);
        cc1.setMinWidth(10);
        //cc1.setMaxWidth(2);
        cc1.setPrefWidth(10);

        ColumnConstraints cc2 = new ColumnConstraints();
            //cc2.setMinWidth(45);
        cc2.setPrefWidth(45);        
        //cc0.setPrefWidth(200);
        RowConstraints rc0 = new RowConstraints();
        rc0.setVgrow(Priority.SOMETIMES);
        gridPane.add(vb1, 0, 0);
        rc0.setMinHeight(30);
        rc0.setMaxHeight(30);
        rc0.setPrefHeight(30);
        rc0.setPercentHeight(-1);

        RowConstraints rc1 = new RowConstraints();
        rc1.setVgrow(Priority.SOMETIMES);
        rc1.setMinHeight(60);
        rc1.setMaxHeight(60);
        rc1.setPrefHeight(60);
        //gridPane.add(new Label("label"), 0, 1);
        rc1.setValignment(VPos.BOTTOM);
        Label rc1Lb = new Label("label");
        gridPane.add(rc1Lb, 0, 1);

        RowConstraints rc2 = new RowConstraints();
        rc2.setVgrow(Priority.SOMETIMES);
        rc2.setValignment(VPos.BOTTOM);

        rc2.setPercentHeight(50);
        rc2.setPrefHeight(-1);
        gridPane.add(bb3, 0, 2);

        RowConstraints rc3 = new RowConstraints();
        rc3.setVgrow(Priority.SOMETIMES);
        rc3.setMinHeight(20);
        rc3.setMaxHeight(20);
        rc3.setPrefHeight(20);        
        
        RowConstraints rc4 = new RowConstraints();
        rc4.setVgrow(Priority.SOMETIMES);
        rc4.setMinHeight(30);
        rc4.setMaxHeight(Region.USE_COMPUTED_SIZE);
        rc4.setPrefHeight(30);
        rc4.setPercentHeight(10);
        rc4.setValignment(VPos.BOTTOM);
        gridPane.add(bb4, 0, 4);

        rc3.setValignment(VPos.BOTTOM);
        //rc1.setPrefHeight(100);
        gridPane.getRowConstraints().addAll(rc0, rc1, rc2, rc3, rc4);
        gridPane.getColumnConstraints().addAll(cc0, cc1);

        //gridPane.setSnapToPixel(false);
        root.getChildren().addAll(b1, b2,b3, gridPane);

        bb2.setOnAction(e -> {
            //dividers.hide();

            dividers.show();
            System.err.println("JdkUtil.getGridCellBounds(gridPane, 0, 0) = " + JdkUtil.getGridCellBounds(gridPane, 0, 0));
            System.err.println("JdkUtil.getGridCellBounds(gridPane, 0, 1) = " + JdkUtil.getGridCellBounds(gridPane, 0, 1));
            /*            List<Node> ll = new ArrayList<>();
            System.err.println("cellBounds 0 = " + gridPane.impl_getCellBounds(0, 0).getHeight());
            System.err.println("cellBounds 1 = " + gridPane.impl_getCellBounds(0, 1).getHeight());
            System.err.println("cellBounds 2 = " + gridPane.impl_getCellBounds(0, 2).getHeight());
            System.err.println("cellBounds 3 = " + gridPane.impl_getCellBounds(0, 3).getHeight());
            System.err.println("cellBounds 4 = " + gridPane.impl_getCellBounds(0, 4).getHeight());            
            ll.add(rc1Lb);
            double d = getBaselineComplement(ll, false, false);
            System.err.println("COMPLEMENT pref = " + d);
            d = getBaselineComplement(ll, true, false);
            System.err.println("COMPLEMENT min = " + d);
            d = getBaselineComplement(ll, false, true);
            System.err.println("COMPLEMENT max = " + d);            
            Bounds lb = gridPane.localToScene(gridPane.getLayoutBounds());
            System.err.println("lb = " + lb);
            RowsInfo ri = new RowsInfo(gridPane);
            List<Info> list = ri.getInfoList();
            System.err.println("0 - list.get(0).getValue() = " + list.get(0).getValue());
            //lineH.setLayoutY(lb.getMinY() + list.get(0).getValue());
            //lineH.setLayoutY(lb.getMinY() + list.get(1).getValue() + list.get(0).getValue());   
            //lineH.setLayoutY(rc1Lb.prefHeight(rc1Lb.getWidth()) + lb.getMinY() + list.get(2).getValue() + list.get(1).getValue() + list.get(0).getValue());            
            //lineH.setLayoutY(rc1Lb.prefHeight(rc1Lb.getWidth())/2 + lb.getMinY() + list.get(1).getValue() + list.get(0).getValue());                        
            //lineH.setLayoutY(lb.getMinY() + list.get(1).getValue() + list.get(0).getValue());                        
            lineH.setLayoutY(lb.getMinY() + getLayoutY(gridPane, 1));                                    
            System.err.println("rc1Lb min = " + rc1Lb.minHeight(-1));
            System.err.println("rc1Lb max = " + rc1Lb.maxHeight(-1));            
            System.err.println("rc1Lb pref = " + rc1Lb.prefHeight(-1));   
            System.err.println("rc1Lb bias = " + rc1Lb.getContentBias());
             */
        });
        bb1.setOnAction(e -> {
            dividers.hide();
            rc0.setMaxHeight(45);
            rc0.setMinHeight(45);
            rc0.setPrefHeight(45);

            rc1.setMaxHeight(45);
            rc1.setMinHeight(45);
            rc1.setPrefHeight(45);
            //dividers.show(true);

            /*            System.err.println("vb1 insets = " + vb1.getInsets());
            System.err.println("vb1 layoutBounds   = " + vb1.getLayoutBounds());
            System.err.println("vb1 BoundsInLocal  = " + vb1.getBoundsInLocal());
            System.err.println("vb1 BoundsInParent = " + vb1.getBoundsInParent());
            System.err.println("***");
            System.err.println("gridPane.layoutBounds   = " + gridPane.getLayoutBounds());
            System.err.println("gridPane.BoundsInLocal  = " + gridPane.getBoundsInLocal());
            System.err.println("gridPane.BoundsInParent = " + gridPane.getBoundsInParent());
            System.err.println("***");
            System.err.println("bb1.layoutBounds   = " + bb1.getLayoutBounds());
            System.err.println("bb1.BoundsInLocal  = " + bb1.getBoundsInLocal());
            System.err.println("bb1.BoundsInParent = " + bb1.getBoundsInParent());

            System.err.println("***");
            System.err.println("gridPane.layoutBounds to Scereen   = " + gridPane.localToScreen(gridPane.getLayoutBounds()));
            System.err.println("gridPane.BoundsInLocal to Scereen  = " + gridPane.localToScreen(gridPane.getBoundsInLocal()));
            System.err.println("gridPane.BoundsInParent to Scereen = " + gridPane.localToScreen(gridPane.getBoundsInParent()));
            System.err.println("***");
            System.err.println("vb1.layoutBounds to Scereen   = " + vb1.localToScreen(vb1.getLayoutBounds()));
            System.err.println("vb1.BoundsInLocal to Scereen  = " + vb1.localToScreen(vb1.getBoundsInLocal()));
            System.err.println("vb1.BoundsInParen to Scereent = " + vb1.localToScreen(vb1.getBoundsInParent()));
            System.err.println("---");
            System.err.println("bb1.layoutBounds to Scereen   = " + bb1.localToScreen(bb1.getLayoutBounds()));
            System.err.println("bb1.BoundsInLocal to Scereen  = " + bb1.localToScreen(bb1.getBoundsInLocal()));
            System.err.println("bb1.BoundsInParen to Scereent = " + bb1.localToScreen(bb1.getBoundsInParent()));
             */
        });

        primaryStage.setTitle("JavaFX TestCanvas");
        primaryStage.setScene(scene);
        primaryStage.setHeight(130);

        scene.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {

        });
        b1.setOnAction(e -> {
            System.err.println("*************************");
            System.err.println("gridPane.getColumnConstraints().size() = " + gridPane.getColumnConstraints().size());
            System.err.println("gridPane.width = " + gridPane.getWidth());
            
            System.err.println("columnDividers.size() = " + dividers.getColumnDividers().size());
            for (int i = 0; i < gridPane.getColumnConstraints().size(); i++) {
                Bounds b = JdkUtil.getGridCellBounds(gridPane, i, 0);
                System.err.println("   --- i = " + i + "; bounds = " + b);
            }
            /*            ColumnConstraints cc = gridPane.getColumnConstraints().get(1);
            System.err.println("0 CELL BOUNDS = " + JdkUtil.getGridCellBounds(gridPane, 0, 0));
            System.err.println("1 CELL BOUNDS = " + JdkUtil.getGridCellBounds(gridPane, 1, 0));
            System.err.println("   --- min = " + cc.getMinWidth());
            System.err.println("   --- max = " + cc.getMaxWidth());
            System.err.println("   --- pre = " + cc.getPrefWidth());
            System.err.println("GRID BOUNDS = " + gridPane.getLayoutBounds());
             */
            //System.err.println("dividers.getColumnDividers().get(0).getParent() = " + dividers.getColumnDividers().get(0).getParent().getBoundsInParent());
            //System.err.println("dividers.getColumnDividers().get(1) = " + dividers.getColumnDividers().get(1).getBoundsInParent());            
            /*            RowConstraints rc5 = new RowConstraints();
            rc5.setMinHeight(45);
            rc5.setPrefHeight(45);
            rc5.setMaxHeight(45);
            rc5.setPercentHeight(20);

            Button bb5 = new Button("bb5");
            gridPane.add(bb5, 0, 5);
            gridPane.getRowConstraints().add(rc5);
             */
        });
        b2.setOnAction(e -> {
            
            //cc2.setMaxWidth(45);
            //cc2.setPercentWidth(20);

            Button bb6 = new Button("bb6");
            //cc2.setHgrow(Priority.ALWAYS);
            gridPane.getColumnConstraints().add(cc2);
            gridPane.add(bb6, gridPane.getColumnConstraints().size() - 1, 0);

        });
        b3.setOnAction(e -> {

            //ColumnConstraints cc1 = gridPane.getColumnConstraints().get(1);
            System.err.println("cc1.getMinWidth = " + cc1.getMinWidth());
            System.err.println("cc1.getMaxWidth = " + cc1.getMaxWidth());
            System.err.println("cc1.getPrefWidth = " + cc1.getPrefWidth());
            System.err.println("cc1 cellBounds = " + JdkUtil.getGridCellBounds(gridPane, 1, 0));
            
            System.err.println("cc2.getMinWidth = " + cc2.getMinWidth());
            System.err.println("cc2.getMaxWidth = " + cc2.getMaxWidth());
            System.err.println("cc2.getPrefWidth = " + cc2.getPrefWidth());
            System.err.println("cc2 cellBounds = " + JdkUtil.getGridCellBounds(gridPane, 2, 0));
            
            /*
            ColumnConstraints cc2 = gridPane.getColumnConstraints().get(2);
            System.err.println("cc2.getMinWidth = " + cc2.getMinWidth());
            System.err.println("cc2.getMaxWidth = " + cc2.getMaxWidth());
            System.err.println("cc2.getPrefWidth = " + cc2.getPrefWidth());
            */
        });
        
        primaryStage.setOnShown(s -> {

        });

        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();

        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);

        Dockable.initDefaultStylesheet(null);

    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public double getLayoutY(GridPane grid, int rowIndex) {
        double y = 0;
        for (int i = 0; i <= rowIndex; i++) {
            y += grid.impl_getCellBounds(0, i).getHeight();
        }
        return y;
    }

    public static void handle(MouseEvent e) {
        System.out.println("Scene MOUSE PRESSED handle ");
    }

    public static class Info {

        private ConstraintsBase constraints;

        private double value;
        private double minValue;
        private double maxValue;
        private double prefValue;
        private double percentValue;
        private final GridPane gridPane;
        double percentSum;

        public Info(GridPane grid, ConstraintsBase constraints) {
            this.gridPane = grid;
            if (constraints instanceof RowConstraints) {
                this.constraints = new RowConstraints(((RowConstraints) constraints).getMinHeight(),
                        ((RowConstraints) constraints).getPrefHeight(),
                        ((RowConstraints) constraints).getMaxHeight());
                ((RowConstraints) this.constraints).setPercentHeight(((RowConstraints) constraints).getPercentHeight());
            } else {
                this.constraints = new ColumnConstraints(((ColumnConstraints) constraints).getMinWidth(),
                        ((ColumnConstraints) constraints).getPrefWidth(),
                        ((ColumnConstraints) constraints).getMaxWidth());
                ((ColumnConstraints) this.constraints).setPercentWidth(((ColumnConstraints) constraints).getPercentWidth());
            }

            init();
        }

        private void init() {
            if (min() < 0 && min() != -1) {

            }
            initByContent();
            System.err.println("1 THIS = " + this);
            sumPercent();
            calcHeight();
            System.err.println("2 THIS = " + this);
        }

        private void sumPercent() {

            percentSum = 0;

            ObservableList list = FXCollections.observableArrayList();

            if (constraints instanceof RowConstraints) {
                list.addAll(gridPane.getRowConstraints());
            } else {
                list.addAll(gridPane.getColumnConstraints());
            }

            for (Object o : list) {
                System.err.println("1 sumPercent");
                if (percent((ConstraintsBase) o) >= 0) {

                    percentSum += percent((ConstraintsBase) o);
                    System.err.println("2 sumPercent percentSum = " + percentSum);
                }
            }
        }

        private void initByContent() {
            /*            int idx ;// -1;
            if (constraints instanceof RowConstraints) {
                idx = gridPane.getRowConstraints().indexOf(constraints);
            } else {
                idx = gridPane.getColumnConstraints().indexOf(constraints);
            }
             */
            double min = Double.MAX_VALUE;
            double max = Double.MAX_VALUE;
            double pref = -1;

            minValue = Double.MAX_VALUE;
            maxValue = Double.MAX_VALUE;
            prefValue = Double.MAX_VALUE;
            percentValue = percent();
            System.err.println("percentValue = " + percentValue);
            System.err.println("percentSum = " + percentSum);

            for (Node node : gridPane.getChildren()) {
                if (node instanceof Region) {
                    Region r = (Region) node;
                    if (min(r) < min && min() == -1) {
                        min = min(r);
                        minValue = min;
                    }
                    if (max(r) < max && max() == -1) {
                        max = max(r);
                        maxValue = max;
                    }
                    if (pref(r) > pref && pref() == -1) {
                        pref = pref(r);
                        prefValue = pref;
                    }
                }
            }
            if (minValue == Double.MAX_VALUE) {
                minValue = min();
            }
            if (maxValue == Double.MAX_VALUE) {
                maxValue = max();
            }
            if (prefValue == Double.MAX_VALUE) {
                prefValue = pref();
            }

        }

        private void calcHeight() {
            Insets ins = gridPane.getInsets();
            double size = (constraints instanceof RowConstraints) ? gridPane.getHeight() - ins.getTop() - ins.getBottom()
                    : gridPane.getWidth() - ins.getLeft() - ins.getRight();
            if (percent() > 0 && percentSum > 100) {
                value = size * percent() / percentSum;
                System.err.println("1 size = " + size + "; percent() = " + percent() + "; percentSum =  " + percentSum + "; valu = " + value);
            } else if (percent() > 0) {
                value = size * percent() / 100;
                System.err.println("2 size = " + size + "; percent() = " + percent() + "; percentSum =  " + percentSum + "; valu = " + value);
            } else if (maxValue == -1 && prefValue == -1) {
                value = minValue;
            } else if (maxValue == -1 && prefValue != -1) {
                value = Math.max(minValue, prefValue);
            } else if (maxValue != -1 && prefValue == -1) {
                value = Math.max(minValue, maxValue);
            } else if (maxValue != -1 && prefValue != -1) {
                value = Math.max(minValue, Math.min(maxValue, prefValue));
            } else if (minValue != -1 && maxValue == Region.USE_PREF_SIZE && prefValue != -1) {
                value = Math.max(minValue, prefValue);
            } else if (minValue == Region.USE_PREF_SIZE && maxValue == Region.USE_PREF_SIZE) {
                value = prefValue;
            }

        }

        public double min(Region node) {
            if (constraints instanceof RowConstraints) {
                return node.getMinHeight();
            } else {
                return node.getMinWidth();
            }
        }

        public double max(Region node) {
            if (constraints instanceof RowConstraints) {
                return node.getMaxHeight();
            } else {
                return node.getMaxWidth();
            }
        }

        public double pref(Region node) {
            if (constraints instanceof RowConstraints) {
                return node.getPrefHeight();
            } else {
                return node.getPrefWidth();
            }
        }

        public double min() {
            return min(constraints);
        }

        public double max() {
            return max(constraints);
        }

        public double pref() {
            return pref(constraints);
        }

        public double percent() {
            return percent(constraints);
        }

        public static double min(ConstraintsBase c) {
            if (c instanceof RowConstraints) {
                return ((RowConstraints) c).getMinHeight();
            } else {
                return ((ColumnConstraints) c).getMinWidth();
            }
        }

        public static double max(ConstraintsBase c) {
            if (c instanceof RowConstraints) {
                return ((RowConstraints) c).getMaxHeight();
            } else {
                return ((ColumnConstraints) c).getMaxWidth();
            }
        }

        public static double pref(ConstraintsBase c) {
            if (c instanceof RowConstraints) {
                return ((RowConstraints) c).getPrefHeight();
            } else {
                return ((ColumnConstraints) c).getPrefWidth();
            }
        }

        public static double percent(ConstraintsBase c) {
            if (c instanceof RowConstraints) {
                return ((RowConstraints) c).getPercentHeight();
            } else {
                return ((ColumnConstraints) c).getPercentWidth();
            }
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public double getMinValue() {
            return minValue;
        }

        public void setMinValue(double minValue) {
            this.minValue = minValue;
        }

        public double getMaxValue() {
            return maxValue;
        }

        public void setMaxValue(double maxValue) {
            this.maxValue = maxValue;
        }

        public double getPrefValue() {
            return prefValue;
        }

        public void setPrefValue(double prefValue) {
            this.prefValue = prefValue;
        }

        public double getPercentValue() {
            return percentValue;
        }

        public void setPercentValue(double percentValue) {
            this.percentValue = percentValue;
        }

        public static ObservableList<Info> getRowsInfo(GridPane grid) {
            ObservableList<Info> list = FXCollections.observableArrayList();
            int calcCount = 0;
            double percentSpace = 0;
            double fixedSpace = 0;
            for (RowConstraints r : grid.getRowConstraints()) {
                list.add(new Info(grid, r));
            }
            return list;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("min=" + getMinValue())
                    .append("; max=")
                    .append(getMaxValue())
                    .append("; pref=")
                    .append(getPrefValue())
                    .append("; percent=")
                    .append(getPercentValue())
                    .append("; value=")
                    .append(getValue());

            return sb.toString();

        }
    }

    public static class RowsInfo {

        private double percentSpace = 0;
        private double fixedSpace = 0;
        private int calcCount = 0;
        private final GridPane gridPane;
        private ObservableList<Info> infoList;

        public RowsInfo(GridPane gridPane) {
            this.gridPane = gridPane;
            init();
        }

        private void init() {
            infoList = Info.getRowsInfo(gridPane);
            for (Info info : infoList) {
                if (info.getValue() == -1) {
                    calcCount++;
                } else if (info.getPercentValue() > 0) {
                    percentSpace += info.getValue();
                } else {
                    fixedSpace += info.getValue();
                }
            }
            System.err.println("calcCount = " + calcCount);
            System.err.println("percentSpace = " + percentSpace);
            System.err.println("fixedSpace = " + fixedSpace);

            double size = gridPane.getHeight() - gridPane.getInsets().getTop() - gridPane.getInsets().getBottom();
            for (Info info : infoList) {
                if (info.getValue() == -1) {
                    info.setValue((size - percentSpace - fixedSpace) / calcCount);
                }
                System.err.println("info.value = " + info.getValue());
            }

        }

        public ObservableList<Info> getInfoList() {
            return infoList;
        }

    }

    public static class CommonConstraints {

        protected static final String FIXED = "common-grid-constraints-fixed-space";
        protected static final String COMPUTED = "common-grid-constraints-computed-count";
        protected static final String PERCENT = "common-grid-constraints-percent-space";
        protected static final String PREF = "common-grid-constraints-pref-space";

        private final ConstraintsBase constraints;

        private double value;
        private double minValue;
        private double maxValue;
        private double prefValue;
        private double percentValue;

        public CommonConstraints(ConstraintsBase constraints) {
            this.constraints = constraints;
            if (constraints instanceof RowConstraints) {
                minValue = ((RowConstraints) constraints).getMinHeight();
                prefValue = ((RowConstraints) constraints).getPrefHeight();
                maxValue = ((RowConstraints) constraints).getMaxHeight();
                percentValue = ((RowConstraints) constraints).getPercentHeight();
            } else {
                minValue = ((ColumnConstraints) constraints).getMinWidth();
                prefValue = ((ColumnConstraints) constraints).getPrefWidth();
                maxValue = ((ColumnConstraints) constraints).getMaxWidth();
                percentValue = ((ColumnConstraints) constraints).getPercentWidth();
            }

            init();
        }

        private void init() {
            if (minValue < 0 && minValue != -1) {
                minValue = -1;
            }
            if (maxValue < 0 && maxValue != -1) {
                maxValue = -1;
            }
            if (prefValue < 0 && prefValue != -1) {
                prefValue = -1;
            }
            if (percentValue < 0 && percentValue != -1) {
                percentValue = -1;
            }
        }

        /*        private void sumPercent() {

            percentSum = 0;

            ObservableList list = FXCollections.observableArrayList();

            if (constraints instanceof RowConstraints) {
                list.addAll(gridPane.getRowConstraints());
            } else {
                list.addAll(gridPane.getColumnConstraints());
            }

            for (Object o : list) {
                System.err.println("1 sumPercent");
                if (percent((ConstraintsBase) o) >= 0) {

                    percentSum += percent((ConstraintsBase) o);
                    System.err.println("2 sumPercent percentSum = " + percentSum);
                }
            }
        }
         */
        protected void fixByContent(GridPane grid) {
            double min = Double.MAX_VALUE;
            double max = Double.MAX_VALUE;
            double pref = Double.MAX_VALUE;

            for (Node node : grid.getChildren()) {

                if (node instanceof Region) {
                    Region r = (Region) node;
                    if (min(r) >= 0 && minValue != -1 && min(r) > min) {
                        min = min(r);
                    }
                    if (max(r) >= 0 && maxValue != -1 && max(r) < max) {
                        max = max(r);
                    }

                    if (pref(r) >= 0 && prefValue == -1 && pref(r) > pref) {
                        pref = pref(r);
                    }
                }
            }
            if (min == Double.MAX_VALUE && minValue == -1) {
                minValue = min;
            }
            if (max == Double.MAX_VALUE && maxValue == -1) {
                maxValue = max;
            }
            if (pref == Double.MAX_VALUE && prefValue == -1) {
                prefValue = pref;
            }

        }

        private void computeValue(GridPane gridPane) {
            if (percentValue != -1) {
                value = percentValue;
                return;
            }
            Insets ins = gridPane.getInsets();
            //double size = (constraints instanceof RowConstraints) ? gridPane.getHeight() - ins.getTop() - ins.getBottom()
            //        : gridPane.getWidth() - ins.getLeft() - ins.getRight();
            /*            if ( percent() > 0 && percentSum > 100 ) {
                value = size * percent() / percentSum;
                System.err.println("1 size = " + size + "; percent() = " + percent() + "; percentSum =  " + percentSum + "; valu = " + value);
            } else if ( percent() > 0 ) {
                value = size * percent() / 100;
                System.err.println("2 size = " + size + "; percent() = " + percent() + "; percentSum =  " + percentSum + "; valu = " + value);
            } else 
             */
            if (minValue == Region.USE_PREF_SIZE && maxValue == Region.USE_PREF_SIZE) {
                value = prefValue;
                minValue = prefValue;
                maxValue = prefValue;
            } else if (minValue != -1 && maxValue == Region.USE_PREF_SIZE && prefValue != -1) {
                value = Math.max(minValue, prefValue);
                maxValue = prefValue;
            } else if (maxValue == -1 && prefValue == -1) {
                value = minValue;
            } else if (maxValue == -1 && prefValue != -1) {
                value = Math.max(minValue, prefValue);
            } else if (maxValue != -1 && prefValue == -1) {
                value = Math.max(minValue, maxValue);
            } else if (maxValue != -1 && prefValue != -1) {
                value = Math.max(minValue, Math.min(maxValue, prefValue));
            }

        }

        public static ObservableList<Double> getHeights(GridPane gridPane) {
            ObservableList<Double> list = FXCollections.observableArrayList();
            ObservableList<CommonConstraints> constraints = FXCollections.observableArrayList();
            double fixedSpace = 0;
            double prefSpace = 0;
            double percentSpace = 0;
            double percentSum = 0;

            int computedCount = 0;

            Insets ins = gridPane.getInsets();
            double size = gridPane.getHeight() - ins.getTop() - ins.getBottom();

            for (RowConstraints rc : gridPane.getRowConstraints()) {
                CommonConstraints c = new CommonConstraints(rc);
                constraints.add(c);

                c.fixByContent(gridPane);
                c.computeValue(gridPane);
                if (c.getPercentValue() != -1) {
                    percentSum += c.getPercentValue();
                    continue;
                }
                if (c.getValue() == -1) {
                    computedCount++;
                } else if (c.getValue() == c.getMinValue() && c.getValue() == c.getMaxValue() && c.getValue() == c.getPrefValue()) {
                    fixedSpace += c.getValue();
                } else {
                    prefSpace += c.getValue();
                }
            }
            for (CommonConstraints c : constraints) {
                if (c.getPercentValue() != -1 && percentSum > 100) {
                    c.setValue(size * c.getPercentValue() / percentSum);
                } else if (c.getPercentValue() != -1) {
                    c.setValue(size * c.getPercentValue() / 100);
                }

            }

            return list;
        }

        public double min(Region node) {
            if (constraints instanceof RowConstraints) {
                return node.getMinHeight();
            } else {
                return node.getMinWidth();
            }
        }

        public double max(Region node) {
            if (constraints instanceof RowConstraints) {
                return node.getMaxHeight();
            } else {
                return node.getMaxWidth();
            }
        }

        public double pref(Region node) {
            if (constraints instanceof RowConstraints) {
                return node.getPrefHeight();
            } else {
                return node.getPrefWidth();
            }
        }

        public double min() {
            return min(constraints);
        }

        public double max() {
            return max(constraints);
        }

        public double pref() {
            return pref(constraints);
        }

        public double percent() {
            return percent(constraints);
        }

        public static double min(ConstraintsBase c) {
            if (c instanceof RowConstraints) {
                return ((RowConstraints) c).getMinHeight();
            } else {
                return ((ColumnConstraints) c).getMinWidth();
            }
        }

        public static double max(ConstraintsBase c) {
            if (c instanceof RowConstraints) {
                return ((RowConstraints) c).getMaxHeight();
            } else {
                return ((ColumnConstraints) c).getMaxWidth();
            }
        }

        public static double pref(ConstraintsBase c) {
            if (c instanceof RowConstraints) {
                return ((RowConstraints) c).getPrefHeight();
            } else {
                return ((ColumnConstraints) c).getPrefWidth();
            }
        }

        public static double percent(ConstraintsBase c) {
            if (c instanceof RowConstraints) {
                return ((RowConstraints) c).getPercentHeight();
            } else {
                return ((ColumnConstraints) c).getPercentWidth();
            }
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public double getMinValue() {
            return minValue;
        }

        public void setMinValue(double minValue) {
            this.minValue = minValue;
        }

        public double getMaxValue() {
            return maxValue;
        }

        public void setMaxValue(double maxValue) {
            this.maxValue = maxValue;
        }

        public double getPrefValue() {
            return prefValue;
        }

        public void setPrefValue(double prefValue) {
            this.prefValue = prefValue;
        }

        public double getPercentValue() {
            return percentValue;
        }

        public void setPercentValue(double percentValue) {
            this.percentValue = percentValue;
        }

        public static ObservableList<Info> getRowsInfo(GridPane grid) {
            ObservableList<Info> list = FXCollections.observableArrayList();
            int calcCount = 0;
            double percentSpace = 0;
            double fixedSpace = 0;
            for (RowConstraints r : grid.getRowConstraints()) {
                list.add(new Info(grid, r));
            }
            return list;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("min=" + getMinValue())
                    .append("; max=")
                    .append(getMaxValue())
                    .append("; pref=")
                    .append(getPrefValue())
                    .append("; percent=")
                    .append(getPercentValue())
                    .append("; value=")
                    .append(getValue());

            return sb.toString();

        }
    }

    private static double getBaselineComplement(List<Node> children, boolean min, boolean max) {
        double bc = 0;
        for (Node n : children) {
            final double bo = n.getBaselineOffset();
            if (bo == BASELINE_OFFSET_SAME_AS_HEIGHT) {
                continue;
            }
            if (n.isResizable()) {
                bc = Math.max(bc, (min ? n.minHeight(-1) : max ? n.maxHeight(-1) : n.prefHeight(-1)) - bo);
            } else {
                bc = Math.max(bc, n.getLayoutBounds().getHeight() - bo);
            }
        }
        return bc;
    }

}
