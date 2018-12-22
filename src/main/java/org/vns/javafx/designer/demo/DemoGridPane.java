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
package org.vns.javafx.designer.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.DockSideBar;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.LayoutContext;
import org.vns.javafx.dock.api.DefaultLayoutContextFactory;

import org.vns.javafx.designer.PalettePane;
import org.vns.javafx.designer.DesignerLookup;
import org.vns.javafx.designer.SceneView;
import org.vns.javafx.dock.api.Constraints;

/**
 *
 * @author Valery
 */
public class DemoGridPane extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        DoubleProperty dp = new SimpleDoubleProperty();
        dp.setValue(null);
        System.err.println("DoubleProperty " + dp.getValue());
        GridPane root = new GridPane();
        RowConstraints rc = new RowConstraints(25);
        root.getRowConstraints().add(rc);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        
        stage.setHeight(400);
        stage.setWidth(500);
        Button btn1 = new Button("btn1");
        root.getChildren().add(btn1);
        Button btn2 = new Button("btn2");
        
        //root.add(btn2, 2, 2);
        //GridPane.setConstraints(btn2, 3, 3, 1,1,HPos.LEFT,VPos.TOP,Priority.NEVER, Priority.NEVER, Insets.EMPTY);
        //GridPane.setConstraints(btn2, 3, 4, 1,2,null,VPos.TOP,null, Priority.NEVER, null);        
        GridPane.setFillHeight(btn2, true);
        GridPane.setFillWidth(btn2, false);
        Constraints.GridPaneConstraints gpcBtn2 = new Constraints.GridPaneConstraints(btn2);
        GridPane.setConstraints(btn2, 1, 2, 2, 2, HPos.LEFT, VPos.BASELINE, Priority.NEVER, Priority.ALWAYS, new Insets(1,2,3,4));        
//        System.err.println("priority = " + root.getRowConstraints().get(0));
        String str = gpcBtn2.toString(btn2.getProperties());
        System.err.println("====================================");
        System.err.println(str);
        System.err.println("------------------------------------");
        str = gpcBtn2.toString(gpcBtn2.getNamedValues());
        System.err.println(str);
//        Button btn3 = new Button("btn3");
//        Label lb3 = new Label("lb3");
//        HBox hbox = new HBox(btn3, lb3);
//        hbox.setStyle("-fx-background-color: transparent; ");
//        root.add(hbox, 0, 0);
        Constraints.GridPaneConstraints gpcBtn1 = new Constraints.GridPaneConstraints(btn1);
        //gpcBtn1.getNamedValues().get("fillHeight").bindBidirectional(gpcBtn2.getNamedValues().get("fillHeight"));
        //System.err.println("btn1 fillHeight = " + GridPane.isFillHeight(btn1));
/*
        System.err.println("1) getFillHeight = " + gpc1.getNamedValues().get("fillHeight").getValue());
        GridPane.setFillHeight(btn2, false);
        System.err.println("2) getFillHeight = " + gpc1.getNamedValues().get("fillHeight").getValue());
        gpc1.getNamedValues().get("fillHeight").setValue(true);
        System.err.println("3) getFillHeight = " + gpc1.getNamedValues().get("fillHeight").getValue());
        
        System.err.println("   ---  " + GridPane.isFillHeight(btn2));
        System.err.println("properties.get(gridpane-fill-height) = " + btn2.getProperties().get("gridpane-fill-height"));
        //GridPane.setFillWidth(btn2, false);
        System.err.println("======== fillWidth = " + GridPane.isFillWidth(btn2));
        gpc1.getNamedValues().get("fillWidth").setValue(false);
        System.err.println("   --- gpc1 fillWidth = " + gpc1.getNamedValues().get("fillWidth").getValue());
        System.err.println("   --- GridPane.isFillWidth " + GridPane.isFillWidth(btn2));
        System.err.println("   --- properties.get(gridpane-fill-Width) = " + btn2.getProperties().get("gridpane-fill-width"));        
        //GridPane.setFillHeight(btn2, null);
        //System.err.println("3) getFillHeight = " + gpc1.getNamedValues().get("fillHeight").getValue());
*/        
        //GridPane.setColumnIndex(btn1, 1);
        //GridPane.setRowIndex(btn1, 0);
        
        stage.show();
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);

        Dockable.initDefaultStylesheet(null);
        
//        System.err.println("btn1.getProperties().size = " + btn1.getProperties().size());
        
//        System.err.println("btn1.getProperties().getRow = " + btn1.getProperties().get("gridpane-row"));
//        System.err.println("GridPane(btn1).getRowIndex = " + GridPane.getRowIndex(btn1));
        //GridPane.setRowSpan(btn1, 2);
/*        btn1.getProperties().forEach((k,v) -> {
            System.err.println("btn1: k = " + k + "; v = " + v );
        });
*/        
         btn2.getProperties().forEach((k,v) -> {
//            System.err.println("btn2: k = " + k + "; v = " + v  );
        });
//        System.err.println("root.getProperties().size = " + root.getProperties().size());
//        System.err.println(" --- GridPane.getColumnIndex(btn2) = " + GridPane.getColumnIndex(btn2));
        
        VBox vbox = new VBox();
        Label lb1 = new Label("lb1");
        vbox.getChildren().add(lb1);
        VBox.setMargin(lb1, Insets.EMPTY);
        VBox.setVgrow(lb1, Priority.SOMETIMES);
        
        lb1.getProperties().forEach((k,v) -> {
//            System.err.println("lb1: k = " + k + "; v = " + v);
        });
        BorderPane borderPane = new BorderPane();
        Label lb2 = new Label("lb2");
        BorderPane.setAlignment(lb2, Pos.TOP_LEFT);
        BorderPane.setMargin(lb2, Insets.EMPTY);
        //borderPane.getChildren().add(lb2);
        //Pane pane = new Pane(lb2);
        lb2.getProperties().forEach((k,v) -> {
//            System.err.println("lb2: k = " + k + "; v = " + v);
        });
        
//        System.err.println("lb2.getProperties.class = " + lb2.getProperties().getClass().getName() );
        AnchorPane anchorPane = new AnchorPane();
        Label lb4 = new Label("lb4");
        AnchorPane.setTopAnchor(lb4, 14d);
//        lb4.getProperties().forEach((k,v) -> {
//            System.err.println("lb4: k = " + k + "; v = " + v);
//        });
//        System.err.println("GridPane.getHgrow(btn2) = " + GridPane.getHgrow(btn2));
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

}
