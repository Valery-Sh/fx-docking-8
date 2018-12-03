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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class DemoEventDispatcher extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        Button btn1 = new Button("btn1");
        ComboBox cb = new ComboBox();
        TextField tf = new TextField("Valery");
        Circle circle  = new Circle(10);
        

        VBox vbox = new VBox(btn1, cb, tf, circle);
        HBox root = new HBox(vbox);

        Scene scene = new Scene(root);

        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            System.err.println("filter scene mouse pressed source = " + e.getSource() + "; target = " + e.getTarget());
            //e.consume();
        });
        scene.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            System.err.println("handler scene mouse pressed source = " + e.getSource() + "; target = " + e.getTarget());
        });
        cb.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            System.err.println("filter cb mouse pressed source = " + e.getSource() + "; target = " + e.getTarget());
        });
        cb.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            System.err.println("handler cb mouse pressed source = " + e.getSource() + "; target = " + e.getTarget());

        });
        
        vbox.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            System.err.println("filter vbox mouse pressed source = " + e.getSource() + "; target = " + e.getTarget());
        });
        
        vbox.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            System.err.println("handler vbox mouse pressed source = " + e.getSource() + "; target = " + e.getTarget());
            //e.consume();
        });
        circle.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            System.err.println("filter circle mouse pressed source = " + e.getSource() + "; target = " + e.getTarget());
        });
        
        circle.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            System.err.println("handler circle mouse pressed source = " + e.getSource() + "; target = " + e.getTarget());
            //e.consume();
        });
        
/*        cb.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            System.err.println("filter cb mouse clicked source = " + e.getSource() + "; target = " + e.getTarget());
        });
        cb.addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
            //System.err.println("handler cb mouse moved source = " + e.getSource() + "; target = " + e.getTarget());
            //e.consume();
        });

        cb.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            System.err.println("handler cb mouse ENTERED source = " + e.getSource() + "; target = " + e.getTarget());
            //e.consume();
        });
*/
        TestEventDispatcher ed = new TestEventDispatcher("SCENE");
        TestEventDispatcher rootEd = new TestEventDispatcher("HBOX - root");
        TestEventDispatcher vboxEd = new TestEventDispatcher("VBOX");
        TestEventDispatcher btn1Ed = new TestEventDispatcher("BUTTON btn1");
        TestEventDispatcher cbEd = new TestEventDispatcher("COMBOBOX cb");
        TestEventDispatcher tfEd = new TestEventDispatcher("TEXTFIELT tf");
        ed.start(scene);
        rootEd.start(root);
        vboxEd.start(vbox);
        btn1Ed.start(btn1);
        cbEd.start(cb);
        tfEd.start(tf);

        stage.setScene(scene);

        stage.setHeight(300);
        stage.setWidth(200);
        stage.setX(100);
        stage.show();
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

}
