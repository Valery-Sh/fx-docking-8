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

import com.sun.glass.ui.Robot;
import java.util.Random;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.vns.javafx.JdkUtil;
import org.vns.javafx.DefaultTopWindowFinder;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class TestWindowZOrder extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        Random r = new Random();
        System.err.println(java.util.UUID.randomUUID());
        DefaultTopWindowFinder.getInstance().start();

        Stage dragStage = new Stage();
        Label dragLabel = new Label("drag label");
        Button changeBtn = new Button("Change");
        Button printBtn = new Button("Print Tree");
        Scene dragScene = new Scene(new HBox(dragLabel,changeBtn, printBtn));
        dragStage.setScene(dragScene);

        dragStage.show();

        Button btn1 = new Button("Btn1");
        Button btn2 = new Button("Btn2");
        VBox vbox0 = new VBox(btn1);

        vbox0.setPrefWidth(100);
        vbox0.setStyle("-fx-background-color: aqua");
        VBox vbox1 = new VBox(btn2);
        vbox1.setPrefWidth(100);
        vbox1.setStyle("-fx-background-color: yellow;");

        Scene scene = new Scene(vbox0);
        primaryStage.setScene(scene);
        primaryStage.setTitle("PRIME 1");
        primaryStage.setX(50);

        primaryStage.setHeight(100);
        primaryStage.setWidth(250);
        //primaryStage.setAlwaysOnTop(true);
        //primaryStage.show();

        Stage child0 = new Stage();
        Scene nextScene1 = new Scene(new VBox());
        child0.setScene(nextScene1);
        child0.setTitle("child0");
        child0.setX(50);
        child0.setY(100);

        child0.setHeight(100);
        child0.setWidth(250);
        child0.initOwner(primaryStage);
//        DesignerSceneEventDispatcher sed = new DesignerSceneEventDispatcher();
//        sed.start(nextPrimaryStage1.getScene());
        //primaryStage.setAlwaysOnTop(true);
//        nextPrimaryStage1.show();        

        Stage child0_0 = new Stage();
        Scene nextScene2 = new Scene(new VBox());
        child0_0.setScene(nextScene2);
        child0_0.setTitle("child0_0");
        child0_0.setX(50);
        child0_0.setY(100);

        child0_0.setHeight(100);
        child0_0.setWidth(250);
        child0_0.initOwner(child0);
        
        //primaryStage.setAlwaysOnTop(true);
//        nextPrimaryStage2.show();        

        Stage child0_0_0 = new Stage();
        child0_0_0.setTitle("child0_0_0");
        Scene scene0 = new Scene(new VBox());

        child0_0_0.setScene(scene0);
        child0_0_0.setX(310);
        child0_0_0.setHeight(100);
        child0_0_0.setWidth(250);

        child0_0_0.initOwner(child0_0);
        
        Stage child0_0_0_0 = new Stage();
        child0_0_0_0.setTitle("child0_0_0_0");
        Scene scene0000 = new Scene(new VBox());

        child0_0_0_0.setScene(scene0000);
        child0_0_0_0.setX(310);
        child0_0_0_0.setHeight(100);
        child0_0_0_0.setWidth(250);

        child0_0_0_0.initOwner(child0_0_0);        
//        primStage0.show();

        Stage child1 = new Stage();
        child1.setTitle("child1");
        Scene scene1 = new Scene(new VBox());

        child1.setScene(scene1);
        child1.setX(510);
        child1.setHeight(100);
        child1.setWidth(250);
        //primStage1.setAlwaysOnTop(true);
        child1.initOwner(primaryStage);
        
        Stage child1_0= new Stage();
        child1_0.setTitle("child1_0");
        Scene scene1_0 = new Scene(new VBox());
        child1_0.setScene(scene1_0);
        //primStage1.setAlwaysOnTop(true);
        child1_0.initOwner(child1);        
//        primStage1.show();
        Stage child2 = new Stage();
        child2.setTitle("child2");
        Rectangle rect = new Rectangle(50,50);
        HBox chbox0 = new HBox(new Rectangle(50, 50), rect);
        chbox0.setStyle("-fx-background-color: yellow; -fx-opacity: 0");
        Scene childscene0 = new Scene(chbox0);

        child2.setScene(childscene0);
        child2.setY(200);
        child2.setHeight(100);
        child2.setWidth(250);
        child2.initOwner(primaryStage);
        
        Stage child3 = new Stage();
        child3.setTitle("child3");
        Rectangle rect3 = new Rectangle(50,50);
        HBox chbox3 = new HBox(new Rectangle(50, 50), rect3);
        chbox3.setStyle("-fx-background-color: yellow; -fx-opacity: 0");
        Scene childscene3 = new Scene(chbox3);

        child3.setScene(childscene3);
        child3.setHeight(100);
        child3.setWidth(250);
        child3.initOwner(primaryStage);
        
        
        Robot robot = JdkUtil.newRobot();

//        child1.getScene().addEventFilter(MouseEvent.ANY, e -> {
//            System.err.println("MOUSE MOVED OVER primStage1" + e.getTarget());
//        });

        Stage primaryStage1 = new Stage();
        primaryStage1.setAlwaysOnTop(true);
        
        primaryStage1.setTitle("PRIME 2");
        
//        Circle circle = new Circle(4);
//        circle.setFill(Color.RED);
//        circle.setManaged(false);
        VBox vbps1 = new VBox();
        vbps1.setStyle("-fx-background-color: yellow; -fx-opacity: 0.999");
        
        Scene childscene1 = new Scene(vbps1);

        primaryStage1.setScene(childscene1);
        primaryStage1.setY(400);
        primaryStage1.setHeight(100);
        primaryStage1.setWidth(250);

        //childStage1.initOwner(primaryStage);
        primaryStage.show();
        primaryStage.setAlwaysOnTop(true);
        
        primaryStage1.show();          //5-stage   
        
        //child2.show();          //4-in-primaryStage
        child3.show();
        child2.setAlwaysOnTop(true);        
        
        child3.setAlwaysOnTop(true);
        
        child0.show();    //next-1-primary-stage
        //child0.setAlwaysOnTop(true);     
        //child0.setAlwaysOnTop(true);        
        //child0.setAlwaysOnTop(true);
                  //3-in-primaryStage       
        
        //nextPrimaryStage1.setAlwaysOnTop(true);    
        child0_0.getScene().focusOwnerProperty().addListener(node -> {
            System.err.println("focusOwnerProperty = " + node);
        } );
        child0_0.show();    //next-2-primary-stage        
        //child0_0.setAlwaysOnTop(true);
        //child0_0.setAlwaysOnTop(true);          
        child0_0_0.show();           //2-in-nextPrimaryStage2
        
        child0.show();    //next-2-primary-stage        
        //primaryStage.show();
        
        
        child0_0_0_0.show();  
        System.err.println("1. child0 owner = " + ((Stage)child0.getOwner()).getTitle());
        
        System.err.println("2. child0 owner = " + ((Stage)child0.getOwner()).getTitle());
        
        //child0_0.setAlwaysOnTop(true);                
//        primStage1.show();           //3-in-primaryStage
      
        
        child1.show();
        child1_0.show();
        
        Stage child2_0 = new Stage();
        Scene child2_0Scene = new Scene(new Button("child2_0"));
        child2_0.setScene(child2_0Scene);
        child2_0.initOwner(child2);
        child2_0.setTitle("child2_0");
        child2_0.setX(450);
        child2_0.setY(120);
        
        child2_0.show();
        child2_0.setAlwaysOnTop(true);        

        Stage child3_0 = new Stage();
        Scene child3_0Scene = new Scene(new Button("child3_0"));
        child3_0.setScene(child3_0Scene);
        child3_0.initOwner(child3);
        child3_0.setTitle("child3_0");
        
        child3_0.show();
        //child3_0.setAlwaysOnTop(true);        

        
        dragLabel.setOnMousePressed(e -> {
            //primaryStage.show();
            //primaryStage.setAlwaysOnTop(false);
            //primaryStage.toBack();
            //if ( true ) return;
            //circle.setVisible(false);    
          
            //WindowOrderManager.printOwnerTree();
        });        
        TopWindowTracker tracker = new TopWindowTracker(primaryStage1);
        
        dragLabel.addEventFilter(MouseEvent.DRAG_DETECTED, e -> {
            //tracker.prepare();
            //circle.setLayoutX(circle.getRadius());
            //circle.setLayoutY(circle.getRadius());
            //circle.setVisible(true);    
            
        });
        dragLabel.addEventFilter(MouseEvent.MOUSE_RELEASED , e -> {
            //tracker.finish();
            //circle.setLayoutX(circle.getRadius());
            //circle.setLayoutY(circle.getRadius());
            //circle.setVisible(true);    
            
        });
        dragLabel.addEventFilter(MouseEvent.MOUSE_DRAGGED , e -> {
            //tracker.dragged(e.getScreenX(), e.getScreenY());
            //circle.setLayoutX(circle.getRadius());
            //circle.setLayoutY(circle.getRadius());
            //circle.setVisible(true);    
            
        });
        
        dragLabel.setOnDragDetected(e -> {
            
/*            int x = (int)Math.ceil(circle.localToScreen(circle.getCenterX(),circle.getCenterY()).getX());
            int y = (int)Math.ceil(circle.localToScreen(circle.getCenterX(),circle.getCenterY()).getY());
            //Robot robot = JdkUtil.newRobot();
            //Platform.runLater(() -> {
            int c = robot.getPixelColor(x, y);
            System.err.println("COLOR = " + Util.pixelToColor(c) + "; RED = " + Color.RED);
*/            
            //primaryStage.setAlwaysOnTop(false);
            //primaryStage.toBack();
            //if ( true ) return;
            //circle.setVisible(false);
            //});
            DefaultTopWindowFinder.getInstance().dragDetected();
            //WindowOrderManager.printOwnerTree();
        });
        dragLabel.setOnMouseDragged(e -> {

/*            int x = (int)Math.ceil(circle.localToScreen(circle.getCenterX(),circle.getCenterY()).getX());
            int y = (int)Math.ceil(circle.localToScreen(circle.getCenterX(),circle.getCenterY()).getY());
            //Robot robot = JdkUtil.newRobot();
            //Platform.runLater(() -> {
                int c = robot.getPixelColor(x, y);
                System.err.println("COLOR = " + Util.pixelToColor(c) + "; RED = " + Color.RED);
*/            
//            if ( true) return;
            
            System.err.println("================================");
            DefaultTopWindowFinder.getInstance().getTopWindow(e.getScreenX(), e.getScreenY(), dragStage);
            System.err.println("================================");
            
        });
        
        
        printBtn.setOnAction(e -> {
            //primaryStage.setAlwaysOnTop(false);
            //primaryStage.toBack();
            //if ( true ) return;
            DefaultTopWindowFinder.getInstance().dragDetected();
            DefaultTopWindowFinder.printOwnerTree();
/*            List<WindowOrderManager.WindowWrapper> list = WindowOrderManager.getInstance().getOnwerTree().getAllChildren();
            System.err.println("******************************");
            list.forEach(wr -> {
                System.err.println(wr);
            });
            System.err.println("******************************");
*/            
        });
        
        primaryStage.getScene().addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            DefaultTopWindowFinder.getInstance().dragDetected();
            DefaultTopWindowFinder.getInstance().getTopWindow(e.getScreenX(), e.getScreenY(), dragStage);
        });
        changeBtn.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            child0_0.setAlwaysOnTop(true);
            //child0_0.requestFocus();
                //child0_0_0.setAlwaysOnTop(true);
            child0_0_0_0.setAlwaysOnTop(true);
            if ( true) return;
            if ( primaryStage.isAlwaysOnTop() ) {
                primaryStage.setAlwaysOnTop(false);
                child0_0_0_0.setAlwaysOnTop(false);
            } else {
                //primaryStage.setAlwaysOnTop(true);
                child0_0.setAlwaysOnTop(true);
                //child0_0_0.setAlwaysOnTop(true);
                child0_0_0_0.setAlwaysOnTop(true);
            }
        });
        
        //childStage1.show();          //5-stage   
        DefaultTopWindowFinder.getInstance().printOrdered();
/*        dragLabel.setOnMouseClicked(e -> {

            System.err.println("primaryStage.setX(" + primaryStage.getX() + ");");
            System.err.println("primaryStage.setY(" + primaryStage.getY() + ");");

//            primaryStage.setX(primaryStage.getX());
//            primaryStage.setY(primaryStage.getY());
            System.err.println("child0.setX(" + child0.getX() + ");");
            System.err.println("child0.setY(" + child0.getY() + ");");
//            child0.setX(child0.getX());
//            child0.setY(child0.getY());

            System.err.println("child1.setX(" + child1.getX() + ");");
            System.err.println("child1.setY(" + child1.getY() + ");");
//            child1.setX(child1.getX());
//            child1.setY(child1.getY());

            System.err.println("child2.setX(" + child2.getX() + ");");
            System.err.println("child2.setY(" + child2.getY() + ");");
//            child2.setX(child2.getX());
//            child2.setY(child2.getY());            
            System.err.println("child0_0.setX(" + child0_0.getX() + ");");
            System.err.println("child0_0.setY(" + child0_0.getY() + ");");
            System.err.println("child0_0_0.setX(" + child0_0_0.getX() + ");");
            System.err.println("child0_0_0.setY(" + child0_0_0.getY() + ");");

            System.err.println("primaryStage1.setX(" + primaryStage1.getX() + ");");
            System.err.println("primaryStage1.setY(" + primaryStage1.getY() + ");");

        });
*/
        primaryStage.setX(29.33333396911621);
        primaryStage.setY(18.66666603088379);
        child0.setX(20.66666603088379);
        child0.setY(102.0);
        child1.setX(214.6666717529297);
        child1.setY(103.33333587646484);
        child1_0.setX(child1.getX() + 20);
        child1_0.setY(child1.getY() + 50);
        child1_0.setHeight(100);
        child1_0.setWidth(250);
        
        
        child2.setX(432.6666564941406);
        child2.setY(103.33333587646484);
        child2_0.setX(child2.getX() + 20);
        child2_0.setY(child2.getY() + 50);
        
        child3.setY(child2.getY());
        child3.setX(child2.getX() + child2.getWidth() - 20);        
        
        child3_0.setX(child3.getX() + 20);
        child3_0.setY(child3.getY() + 50);
        
        child0_0.setX(-12.0);
        child0_0.setY(184.0);
        child0_0_0.setX(4.0);
        child0_0_0.setY(249.3333282470703);
        
        child0_0_0_0.setX(14.0);
        child0_0_0_0.setY(330);
        
        
        primaryStage1.setX(14.0);
        primaryStage1.setY(450);
        //primaryStage.setAlwaysOnTop(true);

        primaryStage.focusedProperty().addListener((v, ov, nv) -> {
            System.err.println("NEW VALUE = " + nv);
            //System.err.println("impl_getMXWindowType = " + primaryStage.impl_getMXWindowType());
            if (nv) {
                Platform.runLater(() -> {
                    //primaryStage.toFront();
                });

            }
        });
        child0.focusedProperty().addListener((v, ov, nv) -> {
            if (nv) {
                Platform.runLater(() -> {
                    child0.toFront();
                });

            }
        });
        child0_0.focusedProperty().addListener((v, ov, nv) -> {
            if (nv) {
                Platform.runLater(() -> {
                    child0_0.toFront();
                });

            }
        });
        primaryStage1.getScene().addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
            System.err.println("e.getSceneX = " + e.getSceneX() + "; e.getSceneY = " + e.getSceneY());
        });
        //nextPrimaryStage1.setAlwaysOnTop(true);        
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);

        Dockable.initDefaultStylesheet(null);

        //MouseEvent(Object source, EventTarget target, EventType<? extends MouseEvent> eventType, double x, double y, double screenX, double screenY, MouseButton button, int clickCount, boolean shiftDown, boolean controlDown, boolean altDown, boolean metaDown, boolean primaryButtonDown, boolean middleButtonDown, boolean secondaryButtonDown, boolean synthesized, boolean popupTrigger, boolean stillSincePress, PickResult pickResult)
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static class MyEvent extends MouseEvent {

        public MyEvent(MouseEvent ev) {
            this(MouseEvent.MOUSE_MOVED, ev.getX(), ev.getY(), ev.getScreenX(), ev.getScreenY(), MouseButton.PRIMARY, 0, false, false, false, false, true, false,
                    false, false, false, true, null);

        }

        public MyEvent(EventType<? extends MouseEvent> eventType, double x, double y, double screenX, double screenY, MouseButton button, int clickCount, boolean shiftDown, boolean controlDown, boolean altDown, boolean metaDown, boolean primaryButtonDown, boolean middleButtonDown,
                boolean secondaryButtonDown, boolean synthesized, boolean popupTrigger, boolean stillSincePress, PickResult pickResult) {
            super(eventType, x, y, screenX, screenY, button, clickCount, shiftDown, controlDown, altDown, metaDown, primaryButtonDown, middleButtonDown, secondaryButtonDown, synthesized, popupTrigger, stillSincePress, pickResult);
        }

        public MyEvent(Object source, EventTarget target, EventType<? extends MouseEvent> eventType, double x, double y, double screenX, double screenY, MouseButton button, int clickCount, boolean shiftDown, boolean controlDown, boolean altDown, boolean metaDown, boolean primaryButtonDown, boolean middleButtonDown, boolean secondaryButtonDown, boolean synthesized, boolean popupTrigger, boolean stillSincePress, PickResult pickResult) {
            super(source, target, eventType, x, y, screenX, screenY, button, clickCount, shiftDown, controlDown, altDown, metaDown, primaryButtonDown, middleButtonDown, secondaryButtonDown, synthesized, popupTrigger, stillSincePress, pickResult);
        }

    }
}
