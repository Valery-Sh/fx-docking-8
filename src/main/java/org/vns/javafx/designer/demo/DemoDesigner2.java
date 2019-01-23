
/*
 * Copyright 2018 Your Organisation.
 *g
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

import com.sun.javafx.css.StyleManager;
import com.sun.javafx.stage.ScreenHelper;
import com.sun.javafx.stage.StageHelper;
import com.sun.javafx.tk.Toolkit;
import com.sun.javafx.util.WeakReferenceQueue;
import java.net.URL;
import java.util.List;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.collections.ListChangeListener.Change;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.JdkUtil;
import org.vns.javafx.DefaultTopWindowFinder;
import org.vns.javafx.dock.DockNode;
import org.vns.javafx.dock.DockPane;
import org.vns.javafx.dock.DockSideBar;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.LayoutContext;
import org.vns.javafx.dock.api.DefaultLayoutContextFactory;

import org.vns.javafx.designer.PalettePane;
import org.vns.javafx.designer.DesignerLookup;
import org.vns.javafx.designer.DesignerSceneEventDispatcher;
import org.vns.javafx.designer.SceneView;
import org.vns.javafx.designer.TrashTray;
import static org.vns.javafx.dock.api.Constants.FOREIGN;
import org.vns.javafx.dock.api.DockLayout;
import org.vns.javafx.dock.api.dragging.DragManager;
import org.vns.javafx.scene.control.editors.PropertyEditorPane;

/**
 *
 * @author Valery
 */
public class DemoDesigner2 extends Application {

    Label lb2_1 = new Label("Label (2,1)");

    @Override
    public void start(Stage stage) throws Exception {
        stage.getProperties().put("id", "primaryStage");
        DefaultTopWindowFinder.getInstance();
        
        Button tb = new Button();
        
        tb.toFront();

        Node n;

        Button b;

        stage.setAlwaysOnTop(true);
        DockPane rootDockPane = new DockPane();
        rootDockPane.setUsedAsDockLayout(false);
        StackPane root = new StackPane(rootDockPane);
        root.setId("mainStage " + root.getClass().getSimpleName());
        //DesignerLookup.putUnique(SceneGraphView.class, new SceneGraphView(root, true));
        DesignerLookup.putUnique(SceneView.class, new SceneView(true));
        SceneView sceneView = DesignerLookup.lookup(SceneView.class);
        sceneView.setPrefHeight(1000);
        //sceneGraphView.setOpacity(0.2);
        DockNode formDockNode = new DockNode("Form Designer");
        Button formButton = new Button("CLICK");
        Button resetButton = new Button("Reset Designer");

        HBox formPane = new HBox();
        formPane.setSpacing(20);
        formPane.setStyle("-fx-background-color: yellow");
        Button formButton1 = new Button("Btn1");
        formPane.getChildren().addAll(formButton, formButton1);//, resetButton);
        formDockNode.setContent(formPane);
        DefaultLayoutContextFactory ctxFactory = new DefaultLayoutContextFactory();
        LayoutContext ctx = ctxFactory.getContext(formPane);
        System.err.println("ctx=" + ctx);
        DockRegistry.makeDockLayout(formPane, ctx);
        VBox root1 = new VBox();
        Button fbtn1 = new Button("FOREIGN 1");
        fbtn1.getStyleClass().add(FOREIGN);
/*        root1.getChildren().add(fbtn1);
         fbtn1.setOnMouseDragOver(e -> {
            System.err.println("MOUSE DRAG OVER BUTTON");
        });
*/
        //root1.setScaleX(0.8);
        //root1.setScaleY(0.5);
        Button eb = new Button("Ext Button");
        //eb.setScaleX(0.5);
        //eb.setTranslateY(20);
        eb.setOnMousePressed(e -> {
            System.err.println("@@ eb mousepressed");
        });
        eb.addEventHandler(MouseEvent.MOUSE_PRESSED, h -> {
            System.err.println("@@ eb handler mousepressed = " + h.getTarget());
        });
        eb.addEventFilter(MouseEvent.MOUSE_PRESSED, h -> {
         });
        eb.addEventHandler(MouseEvent.MOUSE_RELEASED, h -> {
            System.err.println("@@## eb handler mouseReleased = " + h.getTarget());
        });
        eb.addEventFilter(MouseEvent.MOUSE_RELEASED, h -> {
            System.err.println("@@ eb filter mouseReleased = " + h.getTarget());
        });

        eb.addEventHandler(MouseEvent.DRAG_DETECTED, h -> {
            System.err.println("$$ eb handler DragDetected = " + h.getTarget());
        });

        eb.addEventFilter(MouseEvent.DRAG_DETECTED, h -> {
            System.err.println("$$ eb filter DragDetected = " + h.getTarget());
        });
        eb.addEventHandler(MouseEvent.MOUSE_DRAGGED, h -> {
            System.err.println("@@$$ eb handler MOUSE_DRAGGED = " + h.getTarget());
        });

        TextField tx = new TextField("Ext TextField");
        System.err.println("TX CSS = " + tx.getStyleClass());
        ComboBox cb = new ComboBox();
        //cb.setMouseTransparent(true);
        //cb.getItems().add("item 1");
        //cb.getItems().add("item 2");
        DockNode dn = new DockNode("Dock Node");
        dn.setContent(new Label("Dock Node Content"));
        System.err.println(java.util.UUID.randomUUID());

        //dn.setScaleX(0.5);
        Pane topPane = new Pane();
        topPane.setStyle("-fx-border-width: 3; -fx-border-color: blue");
        Circle circle = new Circle(10);
        Rectangle rect = new Rectangle(40, 20);

        Ellipse ellipse = new Ellipse(20, 20);
        Text text = new Text("text");
        //MyHBox centerPane = new MyHBox(eb,tx, cb,dn, text, circle, rect, ellipse);
        //HBox centerPane = new HBox(circle, rect);
        Rectangle r = new Rectangle(20, 20);
        //r.setStyle("-fx-fill: -fx-background");
        r.setFill(Color.rgb(240, 240, 240, 1));

        Button centerPane0Btn = new Button("Btn centerPane0");
        Button centerPane1Btn = new Button("Btn centerPane1");
        //centerPane0Btn.setFocusTraversable(false);
        //centerPane1Btn.setFocusTraversable(false);
        
        HBox centerPane = new HBox(r, centerPane0Btn, centerPane1Btn  );
        //centerPane.setStyle("-fx-background-color: -fx-background");
        centerPane0Btn.setOnAction(e -> {
            //JdkUtil.getWindows();
            if ( true ) {
                return;
            }
            System.err.println("centerPane.isMouseTransparent = " + centerPane.isMouseTransparent());
            System.err.println("RECT color : " + r.getFill());
            long start = System.currentTimeMillis();
            for (int i = 0; i < 10000; i++) {
                JdkUtil.getWindows();
            }
            long end = System.currentTimeMillis();
            System.err.println("INTERVAL = " + (end - start));

            start = System.currentTimeMillis();
            for (int i = 0; i < 10000; i++) {
                StageHelper.getStages();
            }
            end = System.currentTimeMillis();
            System.err.println("STAGES INTERVAL = " + (end - start));

        });
        centerPane.setId("my-test");
        centerPane.setSpacing(20);
        //centerPane.setScaleX(0.5);
        centerPane.setStyle("-fx-border-width: 3; -fx-border-color: blue;");
        //centerPane.setStyle("-fx-border-width: 3; -fx-border-color: blue; -fx-background-color: -fx-background");

        //eb.setFocusTraversable(false);
        root1.setFocusTraversable(false);

        GridPane gridPane1 = new GridPane();
        //gridPane1.setGridLinesVisible(true);
        System.err.println("size = " + gridPane1.getChildren().size());

        gridPane1.setStyle("-fx-border-width: 3; -fx-border-color: blue");
        gridPane1.setPrefWidth(50);
        gridPane1.setPrefHeight(50);
        gridPane1.setMinHeight(50);
        //gridPane1.setMaxHeight(50);         
        RowConstraints rc0 = new RowConstraints(25, 25, 25);
        rc0.setVgrow(Priority.NEVER);
        RowConstraints rc1 = new RowConstraints(45, 45, 45);
        rc1.setVgrow(Priority.NEVER);
        //gridPane1.getRowConstraints().addAll(rc0,rc1);
        gridPane1.getRowConstraints().addAll(rc0);
        gridPane1.getColumnConstraints().add(new ColumnConstraints(25, 25, 25));
        gridPane1.getColumnConstraints().add(new ColumnConstraints(35, 35, 35));
        //root1.getChildren().addAll(new Label("My Label 1"), topPane, centerPane, gridPane1);
        root1.getChildren().addAll(new Label("My Label 1"), topPane, centerPane);
        //root1.setScaleX(0.5);
        //root1.setTop(topPane);
        //root1.setCenter(centerPane);
        //root1.setLeft(new Label("My Label 1"));
        //root1.setRight(gridPane1);
        Button spButton = new Button(" StackPane Button");
        spButton.setId("stackPaneButton");
        StackPane stackPane1 = new StackPane(spButton);
        stackPane1.setId("stackPane1");
        stackPane1.setStyle("-fx-background-color: red; -fx-padding: 20 20 20 20");

        formButton1.setOnAction(e -> {
            System.gc ();
            System.runFinalization ();
            if ( true ) return;
//            System.err.println("gridPane1.getPrefHeight()  = " + gridPane1.getPrefHeight());
//            centerPane.setPrefHeight(centerPane.getPrefHeight() + 10);
            List<Window> list = JdkUtil.getWindows();
            for ( Window w : list) {
                System.err.println("win.root = " + w.getScene().getRoot());
            }
            System.err.println("=============================================");
            System.err.println("PrintOrdered =====================");
            DefaultTopWindowFinder.getInstance().printOrdered();
            //Selection sel = DockRegistry.lookup(Selection.class);
            //System.err.println("sel.selected = " + sel.getSelected());
/*            Label lb = (Label) gridPane1.lookup("#grid-lb");
            if ( lb == null ) {
                lb = new Label("lb of Grid");
                lb.setId("grid-lb");
                gridPane1.getChildren().add(lb);
                GridPane.setRowIndex(lb, 0);
                GridPane.setColumnIndex(lb, 3);
            } else {
                System.err.println("col index = " + GridPane.getColumnIndex(lb));
                Bounds b1 = JdkUtil.getGridCellBounds(gridPane1, 0, 0);
                System.err.println("bounds = " + b1);
            }
             */
        });

        //root1.setRight(stackPane1);
        //new TreeItemBuilder().build(null);
/*        System.err.println("root1.getChildren = " + root1.getChildren());
        root1.getChildren().forEach(n1 -> {
            System.err.println("root1 node = " + n1);
        } );
         */
        //root1.setCenter(eb);
        //VBox root1 = new VBox();
        HBox hbox = new HBox(new Label("root1 Label"));

        //root1.getChildren().add(hbox);
        root1.setId("root1");
        sceneView.setRoot(root1);

        //StackPane sp = new StackPane();
        StackPane sp = new StackPane(root1);
        sp.setId("root-stackpane");
        Scene scene1 = new Scene(sp);
        /*        scene1.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            //System.err.println("filter scene1 mouse pressed source = " + e.getSource() + "; target = " + e.getTarget());
            //e.consume();
        });
        scene1.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            System.err.println("handler scene1 mouse pressed source = " + e.getSource() + "; target = " + e.getTarget());
        });
        cb.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            System.err.println("filter cb mouse pressed source = " + e.getSource() + "; target = " + e.getTarget());
        });
         cb.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            System.err.println("filter cb mouse clicked source = " + e.getSource() + "; target = " + e.getTarget());
        });
        cb.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            System.err.println("handler cb mouse pressed source = " + e.getSource() + "; target = " + e.getTarget());
            //e.consume();
        });   
        cb.addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
            System.err.println("handler cb mouse moved source = " + e.getSource() + "; target = " + e.getTarget());
            //e.consume();
        });   
        
        cb.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            System.err.println("handler cb mouse ENTERED source = " + e.getSource() + "; target = " + e.getTarget());
            e.consume();
        });   
         */
        root1.setStyle("-fx-padding: 5 5 5 5; -fx-background-color: white");
        //root1.setTranslateX(70);

        sceneView.rootProperty().addListener((v, ov, nv) -> {

        });

        root1.setStyle("-fx-background-color: white;-fx-padding: 5 5 5 5");
        //rightPaneRoot.setStyle("-fx-background-color: SIENNA; -fx-padding: 10 10 10 10");
        sp.setStyle("-fx-background-color: SIENNA; -fx-padding: 20 20 20 20");
        //Scene scene1 = new Scene(sp);
        //sceneView.createDefaultSkin();
        sp.getChildren().addListener((Change<? extends Node> c) -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    //System.err.println("*** added size = " + c.getAddedSize());
                    for (Node n1 : c.getAddedSubList()) {
                        //System.err.println("   --- added node = " + n1);
                    }
                }
            }
        });
        resetButton.setOnAction(e -> {
            //sceneView.iterate(c -> {});
            System.err.println("Dockableof(eb) = " + Dockable.of(eb));
            if (Dockable.of(eb) != null) {
                //Dockable.of(eb).getContext().setDragNode(null);
            }
            //System.err.println("eb layoutContext = " + Dockable.of(eb).getContext().getLayoutContext());

            System.err.println("DockLayoutContext = " + DockLayout.of(centerPane));
            SceneView.reset(root1);
            sceneView.setRoot(null);
            scene1.setRoot(root1);
            
        });
        formButton.setOnAction(a -> {
            System.err.println("centerPaneBtn0 prefWidth = " + centerPane0Btn.getPrefWidth());
            //centerPane0Btn.setPrefHeight(centerPane0Btn.getHeight());
            System.err.println("centerPaneBtn0 width = " + centerPane0Btn.getWidth());
            //centerPane0Btn.setText("12345678901234567890");
            centerPane0Btn.setPrefHeight(50);            
            centerPane0Btn.setPrefWidth(150);
            root1.setScaleX(0.5);
            
            if ( true ) return;
            centerPane0Btn.setPrefWidth(centerPane0Btn.getPrefWidth() + 10);
            if ( true ) return;
            centerPane.getChildren().add(1, new Button("Inserted"));

            System.err.println("gridPane1.getRowConstraits = " + gridPane1);
            System.err.println("gridPane1.getRowConstraits.size = " + gridPane1.getRowConstraints().size());
            if (gridPane1.getRowConstraints().size() == 0) {
                //RowConstraints rc0 = new RowConstraints(30);
                //RowConstraints rc1 = new RowConstraints(40);
                //gridPane1.getRowConstraints().addAll(rc0,rc1);

                Label lb0_1 = new Label("label(0,1)");
                gridPane1.getChildren().addAll(lb2_1, lb0_1);
                GridPane.setRowIndex(lb2_1, 2);
                GridPane.setColumnIndex(lb2_1, 1);
                GridPane.setRowIndex(lb0_1, 0);
                GridPane.setColumnIndex(lb0_1, 0);
            } else if (gridPane1.getChildren().size() == 2) {
                gridPane1.getChildren().remove(lb2_1);
                System.err.println("remove: Label2_1 gridpane-row = " + lb2_1.getProperties().get("gridpane-row"));
            } else if (gridPane1.getChildren().size() == 1) {
                System.err.println("add: Label2_1 gridpane-row = " + lb2_1.getProperties().get("gridpane-row"));
                gridPane1.getChildren().add(lb2_1);
            }
            /*            System.err.println("topPane.insets = " + topPane.getInsets());
            eb.setFocusTraversable(false);
            Bounds br = rect.localToScene(rect.getBoundsInLocal());
            FramePane nr = (FramePane) root1.lookup("#" + FramePane.NODE_ID);
            Rectangle frameRect = (Rectangle) nr.lookup("#" + "rectangle");
            Bounds bnr = nr.localToScene(nr.getBoundsInLocal());
            Bounds panebnd = nr.getPane().localToScene(nr.getPane().getBoundsInLocal());
            Bounds frameRectbnd = frameRect.localToScene(frameRect.getBoundsInLocal());
            
            System.err.println("RECT = " + rect);
            System.err.println("   --- rect.bounds       = " + br);            
            System.err.println("   --- fp.bounds         = " + bnr);   
            System.err.println("   --- fp.getPane.bounds = " + panebnd);   
            System.err.println("   --- frameRect.bounds  = " + frameRectbnd);  
            System.err.println("   --- frameRect         = " + frameRect);  
             */

            System.err.println("===========================================");
            /*           System.err.println("root1.width = " + root1.getWidth()); 
           System.err.println("root1.bounds.width = " + root1.localToScene(root1.getBoundsInLocal()).getWidth()); 
           System.err.println("centerPane.width = " + centerPane.localToScene(centerPane.getBoundsInLocal()).getWidth()); 
           Set<Scope> scs = Dockable.of(centerPane).getContext().getScopes();
           System.err.println("centerPane is docklayout = " + DockLayout.of(centerPane));
           System.err.println("centerPane scopes = " + scs);
           scs = Dockable.of(gridPane1).getContext().getScopes();
           System.err.println("gridPane1 is dockable = " + Dockable.of(gridPane1));
           System.err.println("gridPane1 scopes = " + scs);
           scs = Dockable.of(topPane).getContext().getScopes();
           scs.forEach(s -> {
               System.err.println("   --- scope = " + s.getId());
           });

            if ( true ) {
               return;
            }
            
            System.err.println("centerPane is dockable = " + Dockable.of(centerPane));
            System.err.println("GridPane.rowConstraints size = " + gridPane1.getRowConstraints().size());
            System.err.println("CLICKED CENTER ");
            Node nd = root1.getCenter();
            System.err.println("CLICKED CENTER scaleX     = " + nd.getScaleX());
            System.err.println("CLICKED CENTER translateX = " + nd.getTranslateX());
            System.err.println("eb.getInsets() = " + eb.getInsets());
            System.err.println("CLICKED TX CSS = " + tx.getStyleClass());
            System.err.println("EventDisp = " + cb.getEventDispatcher());
            System.err.println("EventDisp formButton = " + formButton.getEventDispatcher());
            //eb.setFocusTraversable(false);
            TreeItemEx o = EditorUtil.findTreeItemByObject(sceneView.getTreeView(), centerPane);
            System.err.println("TREEITEMEX MyVBox itemValue = " + o.getItemType() );
            o = EditorUtil.findTreeItemByObject(sceneView.getTreeView(), topPane);
            NodeDescriptor nd1 = NodeDescriptor.get(centerPane.getClass());
            System.err.println("nd1 = " + nd1);
            System.err.println("TREEITEMEX Pane itemValue = " + o.getItemType() );
            System.err.println("TREEITEMEX ROOT itemValue = " + ((TreeItemEx)sceneView.getTreeView().getRoot()).getItemType() );
            if ( nd != null && nd.getScaleX() == 1 ) {
                nd.setScaleY(0.5);
                nd.setScaleX(0.8);
                //if ( nd instanceof VBox) {
                if ( false) {                    
                    eb.setTranslateY(5);
                    eb.setTranslateX(5);
                } else {
                    nd.setTranslateY(5);
                    nd.setTranslateX(5);
                }
            } else if ( nd != null ) {
                nd.setScaleX(1);
                //if ( nd instanceof VBox) {
                if ( false) {                                    
                    eb.setTranslateX(0);
                } else {
                    nd.setTranslateX(0);
                }

            }
             */
        });

        root1.setId("root1 " + root.getClass().getSimpleName());

        Stage stage1 = new Stage();
        stage1.setTitle("MAIN");
        stage1.getProperties().put("id", "MAIN STAGE");
        stage1.setAlwaysOnTop(true);
        stage1.setWidth(200);
        stage1.setHeight(200);
        stage1.setScene(scene1);
        stage1.initOwner(stage);

        DockSideBar sgvDockSideBar = new DockSideBar();
        sgvDockSideBar.setOrientation(Orientation.VERTICAL);
        sgvDockSideBar.setRotation(DockSideBar.Rotation.DOWN_UP);
        sgvDockSideBar.setSide(Side.RIGHT);
        sgvDockSideBar.setHideOnExit(false);

        DockNode sgvDockNode = new DockNode(" Hierarchy ");
        sgvDockNode.setId("scene-view");
        
        sgvDockNode.setContent(sceneView);
        sgvDockSideBar.getItems().add(Dockable.of(sgvDockNode));

        PalettePane palettePane = DesignerLookup.lookup(PalettePane.class);
        PropertyEditorPane editorPane = DesignerLookup.lookup(PropertyEditorPane.class);
        palettePane.setId("property-editor-pane");
        
        DockSideBar paletteDockSideBar = new DockSideBar();

        paletteDockSideBar.setOrientation(Orientation.VERTICAL);
        paletteDockSideBar.setRotation(DockSideBar.Rotation.UP_DOWN);
        paletteDockSideBar.setSide(Side.LEFT);

        DockNode palleteDockNode = new DockNode(" Palette ");
        palleteDockNode.setContent(palettePane);
        //palleteDockNode.getContext().getDragManager().getHideOption();
        palleteDockNode.getContext().getDragManager().setHideOption(DragManager.HideOption.NONE);
        //System.err.println("dragManager.class = " + palleteDockNode.getContext().getDragManager().getClass().getSimpleName());
        paletteDockSideBar.getItems().add(Dockable.of(palleteDockNode));

        DockNode editorPaneDockNode = new DockNode(" Property Editor ");
        editorPaneDockNode.setContent(editorPane);
        if ( editorPane != null ) {
            editorPane.setPrefWidth(300);
        }
        
        //palleteDockNode.getContext().getDragManager().getHideOption();
        editorPaneDockNode.getContext().getDragManager().setHideOption(DragManager.HideOption.NONE);
        //System.err.println("dragManager.class = " + palleteDockNode.getContext().getDragManager().getClass().getSimpleName());
        paletteDockSideBar.getItems().add(Dockable.of(editorPaneDockNode));        
        
        rootDockPane.dock(formDockNode, Side.TOP);
        rootDockPane.dock(sgvDockSideBar, Side.LEFT);
        rootDockPane.dock(paletteDockSideBar, Side.RIGHT);

        Scene scene = new Scene(root);
        stage.setScene(scene);

        stage.setHeight(400);
        stage.setWidth(500);

        stage.show();
        stage1.show();

        TrashTray tray = DockRegistry.lookup(TrashTray.class);
        if (tray != null) {
            //Stage trashStage = tray.show(stage1);
            Stage trashStage = tray.show(stage);
            trashStage.getProperties().put("id","trash-tray");
            trashStage.toFront();
        }

        Stage stage2 = new Stage();
        stage2.getProperties().put("id","stage2");
        stage2.setTitle("*** Stage2");
        stage2.setAlwaysOnTop(true);
        stage2.setWidth(250);
        stage2.setHeight(150);

        VBox vb = new VBox();
        
        //vb.setPickOnBounds(true);
        vb.setId("VBBBBB");
        
        Scene st2Scene = new Scene(vb);
        st2Scene.setFill(Color.ALICEBLUE);
        stage2.setScene(st2Scene);
        //st2Scene.setCursor(Cursor.SW_RESIZE);
        DesignerSceneEventDispatcher d = new DesignerSceneEventDispatcher();
        //WeakReferenceQueue<Window> windowQueue = new WeakReferenceQueue<Window>();
        //windowQueue.iterator().
      
        st2Scene.setEventDispatcher(d);
//        st2Scene.setOnMouseDragEntered(e -> {
//            System.err.println("MOUSE DRAG OVER ");
//        });
        d.start(st2Scene);
        stage2.show();
/*        StageHelper.StageAccessor sa = new StageHelper.StageAccessor() {
            @Override
            public ObservableList<Stage> getStages() {
                this.
            }

            @Override
            public void initSecurityDialog(Stage stage, boolean securityDialog) {

            }
        };
*/
        
/*        StageHelper.getStages().addListener(new ListChangeListener<Stage>() {
            @Override
            public void onChanged(Change<? extends Stage> c) {
                System.err.println("STAGES ------------------");
                c.getList().forEach(w -> {
                    System.err.println("stage = " + w.getScene().getRoot());
                });
                List<Window> list = JdkUtil.getWindows(true);
                System.err.println("WINDOWS ------------------");
                list.forEach(w -> {
                    System.err.println("win = " + w.getScene().getRoot());
                });
                
            }
        });
  */              
        /*DockNode dn = new DockNode("Ext DockNoce");
        formPane.getChildren().add(dn);
        Button dnBtn = new Button("SHOW Layout");
         */
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        System.err.println("getUserAgent = " + Application.getUserAgentStylesheet());
        System.err.println("Show window children.size() = " + sp.getChildren().size());
        //DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();        
        Dockable.initDefaultStylesheet(null);

        URL u = DesignerLookup.class.getResource("resources/styles/designer-default.css");

        StyleManager.getInstance()
                .addUserAgentStylesheet(u.toExternalForm());
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
