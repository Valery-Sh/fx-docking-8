/*
 * Copyright 2019 Your Organisation.
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

/**
 *
 * @author Valery
 */
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class TestFullScreenMode extends Application {

    @Override
    public void start(Stage stage) {
        VBox vbox = new VBox();
        Scene scene = new Scene(vbox);
        stage.setScene(scene);
        Button button1 = new Button("New Tool Window");
        button1.setOnAction((e) -> {
            Stage toolStage = new Stage();
            Scene toolScene = new Scene(new Label("Am I on top?"), 300, 250);
            toolStage.setScene(toolScene);
            toolStage.initOwner(stage);
            toolStage.setAlwaysOnTop(true);
            toolStage.show();
        });
        Button button2 = new Button("Close");
        button2.setOnAction((e) -> System.exit(0));
        vbox.getChildren().addAll(button1, button2);
        stage.show();
        stage.setFullScreen(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
