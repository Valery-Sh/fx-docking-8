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

import java.util.Set;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.vns.javafx.designer.SceneView;
import org.vns.javafx.dock.api.DockLayout;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.selection.SelectionFrame;
import static org.vns.javafx.dock.api.selection.SelectionFrame.NODE_ID;
import static org.vns.javafx.dock.api.selection.SelectionFrame.PARENT_ID;
import static org.vns.javafx.dock.api.selection.SelectionFrame.FRAME_CSS_CLASS;

/**
 *
 * @author Valery
 */
public class Demo01  extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        
        Button btn1 = new Button("Btn1");
        Button btn2 = new Button("Btn2");        
        VBox vboxLeft = new VBox(btn1);
        
        vboxLeft.setPrefWidth(100);
        vboxLeft.setStyle("-fx-background-color: aqua");
        VBox vboxRight = new VBox(btn2);
        vboxRight.setPrefWidth(100);        
        vboxRight.setStyle("-fx-background-color: yellow");
        
        HBox hbox = new HBox(vboxLeft,vboxRight);
        hbox.setStyle("-fx-border-color: red; -fx-border-width: 20 20 20 20; -fx-background-color: BLANCHEDALMOND");
        hbox.setPrefHeight(100);
        StackPane root = new StackPane(hbox);
        SceneView.addFramePanes(vboxRight);
        
        //SceneView.getParentFrame().hide();
        //SceneView.getResizeFrame().hide();
        
        Dockable.register(btn1);
        Dockable.register(btn2);
        DockLayout.tryRegister(vboxLeft);
        DockLayout.tryRegister(vboxRight);
        DockLayout.tryRegister(hbox);
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        
        stage.setHeight(400);
        stage.setWidth(500);
        

        stage.show();
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);

        Dockable.initDefaultStylesheet(null);

        
        SelectionFrame fp1 = (SelectionFrame) vboxRight.lookup("#" + PARENT_ID);
        System.err.println("fp1.isVisible() = " + fp1.isVisible());
        SelectionFrame fp2 = (SelectionFrame) vboxRight.lookup("#" + NODE_ID);
        System.err.println("fp2.isVisible() = " + fp2.isVisible());
        //fp1.setVisible(false);
        //fp2.setVisible(false);
/*        Set<Node> set = root.lookupAll("." + CSS_CLASS);
        fp1.setVisible(false);
        fp2.setVisible(false);
        set.forEach(n -> {
            System.err.println("shape = " + n);
            n.setVisible(false);
        });
*/
        fp1.hide();
        fp2.hide();
/*        fp1.getSideShapes().forEach((k, v) -> {
            System.err.println("set false");
            v.setVisible(false);
        });        
        fp2.getSideShapes().forEach((k, v) -> {
            System.err.println("set false");
            v.setVisible(false);
        });        
*/  
    }

   
    public static void main(String[] args) {
        launch(args);
    }

}
