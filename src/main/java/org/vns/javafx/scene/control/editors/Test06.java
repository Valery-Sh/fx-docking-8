package org.vns.javafx.scene.control.editors;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.scene.control.editors.beans.PropertyPaneModelRegistry;

/**
 *
 * @author Valery
 */
public class Test06 extends Application {

    VBox vb = new VBox();
    List<Label> list = new ArrayList<>();
    WeakReference listWr = new WeakReference(list);
    Stage stage;
    Scene scene;
    int top = 1;

    //Map<Object, 
    @Override
    public void start(Stage stage) {
        PropertyPaneModelRegistry.getPropertyPaneModel();
        PropertyEditorPane editorPane = new PropertyEditorPane();

        Button btn1 = new Button("Clear");
        Button btn2 = new Button("Create");
        Button btn3 = new Button("Recreate");

        VBox root = new VBox(btn1, btn2, btn3);
        vb = new VBox();
        root.getChildren().add(vb);
        btn1.setOnAction(a -> {
            list = null;
            System.gc();
            try {
                Thread.sleep((100));
            } catch (InterruptedException ex) {
                Logger.getLogger(Test06.class.getName()).log(Level.SEVERE, null, ex);
            }
            list = new ArrayList<>();
            listWr = new WeakReference(list);
//            list.clear();
        });
        btn2.setOnAction(a -> {
            for (int i = 0; i < 1; i++) {
                list.clear();
//                list = null;

//                list = new ArrayList<>();
//                listWr = new WeakReference(list);
                execute();
                System.err.println("EXECUTED " + i);
            }
        });
        /*        btn3.setOnAction(a1 -> {
            vb.getChildren().clear();
            for (int i = 0; i < 6; i++) {
                TitledPane tp = new TitledPane();
                GridPane nodes = new GridPane();
                StackPane sp = new StackPane(nodes);
                tp.setContent(sp);
                for (int j = 0; j < 60; j++) {

                    Label lb = new Label("label " + j);
                    Button b = new Button("Btn " + j);
                    HBox hb = new HBox(new VBox(), new VBox(), new VBox(), new VBox(), new VBox(), new VBox(), new VBox(), new VBox(), new VBox(), new VBox(), new VBox());
                    nodes.add(lb, 0, j);
                    nodes.add(b, 1, j);
                    nodes.add(hb, 0, j + 1);

                }
                vb.getChildren().add(tp);
            }
        });
         */
 /*            if (root.getChildren().get(root.getChildren().size() - 1) instanceof GridPane) {
                root.getChildren().remove(root.getChildren().size() - 1);
            }
         */
        long end = System.currentTimeMillis();
        //System.err.println("SHOW INTERVAL = " + (end - start));

        root.setPrefSize(500, 70);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Scrolling Text");
        stage.setHeight(600);
        stage.setWidth(300);
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

    public void execute() {
        for (int i = 0; i < 200000; i++) {
            //           list.add("11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
            list.add(new Label("lab = " + i));
        }
        System.err.println("list size = " + list.size());
    }
}
