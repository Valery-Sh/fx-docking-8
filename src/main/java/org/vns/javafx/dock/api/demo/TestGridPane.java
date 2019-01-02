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
package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import org.vns.javafx.JdkUtil;

/**
 *
 * @author Valery
 */
public class TestGridPane extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        //root.setStyle("-fx-background-color: YELLOW");
        Button b1 = new Button("Button b1");
        Button b2 = new Button("b2r");
        
        GridPane gridPane = new GridPane();
        gridPane.setStyle("-fx-background-color: aqua");
/*        gridPane.add(new Label("lb0_0"), 0, 0);
        gridPane.add(new Label("lb0_1"), 0, 1);        
        gridPane.add(new Label("lb1_0"), 1, 0);        
        gridPane.add(new Label("lb1_1"), 1, 1);
*/
        gridPane.getRowConstraints().add(new RowConstraints(50, 50, 50));
       
        gridPane.setGridLinesVisible(true);
        
        /*        ub.setPrefWidth(75);
        ub.setPrefHeight(40);
        ub.setMinWidth(75);
        ub.setMinHeight(40);
        ub.setMaxWidth(75);
        ub.setMaxHeight(40);
         */
        //b1.setGraphic(b2);

        //VBox root = new VBox(b1,b2,ub);
        VBox root = new VBox(b1, gridPane);
        root.setStyle("-fx-background-color: yellow");
        b1.setOnAction(e -> {
            Bounds bnd = JdkUtil.getGridCellBounds(gridPane, 0, 5);
            System.err.println("bnd0_1 = " + bnd);
        });
        Scene primaryScene = new Scene(root);

        primaryStage.setScene(primaryScene);

        primaryStage.setOnShown(s -> {

        });
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();
        primaryStage.setHeight(150);
        primaryStage.setWidth(150);

        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);

        //Dockable.initDefaultStylesheet(null);
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

    public static void handle(MouseEvent e) {
        System.out.println("Scene MOUSE PRESSED handle ");
    }
}
