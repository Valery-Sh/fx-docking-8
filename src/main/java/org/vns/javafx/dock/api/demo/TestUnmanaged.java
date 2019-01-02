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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

/**
 *
 * @author Valery
 */
public class TestUnmanaged extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        //root.setStyle("-fx-background-color: YELLOW");
        Button b1 = new Button("Button b1");
        Button b2 = new Button("b2r");
        Rectangle ub = new Rectangle(75, 40);

        ub.setManaged(false);
        /*        ub.setPrefWidth(75);
        ub.setPrefHeight(40);
        ub.setMinWidth(75);
        ub.setMinHeight(40);
        ub.setMaxWidth(75);
        ub.setMaxHeight(40);
         */
        //b1.setGraphic(b2);

        //VBox root = new VBox(b1,b2,ub);
        VBox root = new VBox(b1, ub);
        root.setStyle("-fx-background-color: yellow");
        ub.setLayoutX(10);
        ub.setLayoutY(10);
        ub.toBack();
        root.getChildren().addListener(new ListChangeListener() {
            @Override
            public void onChanged(Change change) {
                while (change.next()) {
                    if (change.wasPermutated()) {
                        System.err.println("PERMUTATED");
                    } else if (change.wasUpdated()) {
                        System.err.println("UPDATE");
                    } else if (change.wasReplaced()) {
                        System.err.println("REPLACED");
                    } else {
                        if (change.wasRemoved()) {
                            System.err.println("REMOVED");
                        } else if (change.wasAdded()) {
                            System.err.println("ADDED");
                        }
                    }
                }
            }
        });
        b1.setOnAction(e -> {
            System.err.println("ub bounds = " + ub.getBoundsInLocal());
            ub.toFront();

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
