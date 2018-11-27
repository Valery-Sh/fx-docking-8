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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.scene.control.editors.BooleanPropertyEditor;
import org.vns.javafx.scene.control.editors.FontPropertyEditor;
import org.vns.javafx.scene.control.editors.FontStringConverter.SizeStringConverter;
import org.vns.javafx.scene.control.editors.SliderPropertyEditor;

/**
 *
 * @author Valery
 */
public class TestSliderPropertyEditor extends Application {

    @Override
    public void start(Stage stage) throws ClassNotFoundException {
        Button btn1 = new Button("Button btn1");
        Button btn2 = new Button("Button btn2");

        long start1 = System.currentTimeMillis();
        Pane p = new Pane();
        long end1 = System.currentTimeMillis();
        //System.err.println("DIF0 = " + (end1 - start1));


        GridPane grid = new GridPane();
        grid.setHgap(10);
        //AnchorPane anchor = new AnchorPane(grid);
        //anchor.setStyle("-fx-border-color: red; -fx-border-width: 4 ");
        //grid.setStyle("-fx-border-color: green; -fx-border-width: 2 ");

        StackPane root = new StackPane(grid);
        FontPropertyEditor fontEditor = new FontPropertyEditor();
        SliderPropertyEditor tf1 = new SliderPropertyEditor();
        btn2.setOpacity(1);
                
        //tf1.bindBidirectional(btn2.opacityProperty());
        tf1.getTextField().textProperty().bindBidirectional(btn2.fontProperty(), new SizeStringConverter(fontEditor));
        //System.err.println("IntegerPropertyEditor value=" + tf1.getRightValue());
        btn1.setOnAction(e -> {
            tf1.getTextField().setText("0.5");
            //tf1.getTextField().setText("0");
            //btn2.setOpacity(-12);
            //System.err.println("IntegerPropertyEditor ip=" + ip.get());
            System.err.println("tf1.text = " + tf1.getTextField().getText());
        });

   
        btn2.setOnAction(e -> {
            System.err.println("btn2.getOpacity=" + btn2.getOpacity());
        });
   
        grid.add(btn1, 0, 0);
        grid.add(tf1, 0, 1);
        grid.add(btn2, 0, 2);


        BooleanPropertyEditor tf3 = new BooleanPropertyEditor();


        root.setPrefSize(500, 200);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Scrolling Text");
        stage.show();

        Stage stage1 = new Stage();
        stage1.initOwner(stage);



        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        Dockable.initDefaultStylesheet(null);
        System.err.println("R = " + getClass().getResource("resources/demo-styles.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("resources/demo-styles.css").toExternalForm());

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
