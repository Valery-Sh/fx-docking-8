package org.vns.javafx.dock.api.demo;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.selection.GridSelectionFrame;

/**
 *
 * @author Valery
 */
public class TestGridFramePane1 extends Application {

    public static Stage frontStage;
    public static Stage stg01;
    public static Stage stg02;

    @Override
    public void start(Stage stage) throws Exception {
        
        StackPane stackPane = new StackPane();
        
        Button showHideButton = new Button("Show or Hide");
        Button addColumnButton = new Button("Add Column");
        Button delColumnButton = new Button("Del Column");
        HBox hbox = new HBox(showHideButton, addColumnButton,delColumnButton); 
        VBox vbox = new VBox(hbox,stackPane);
        GridPane gridPane = new GridPane();
        //gridPane.setGridLinesVisible(true);
        GridSelectionFrame frame = new GridSelectionFrame(gridPane);
        addColumnButton.setOnAction(e -> {
            System.err.println("frame.sel column = " + frame.getSelectedColumn());
            System.err.println("frame.sel column = " + frame.getSelectedConstraints());
            if ( frame.getSelectedConstraints() != null ) {
                System.err.println("gridPane.getColConstr " + gridPane.getColumnConstraints().indexOf(frame.getSelectedConstraints()));
            }
            //ColumnConstraints cc = new ColumnConstraints(20,20,20);
            //gridPane.getColumnConstraints().add(0,cc);
        });
        delColumnButton.setOnAction(e -> {
            gridPane.getColumnConstraints().remove(0);
        });        
        showHideButton.setOnAction(e -> {
            if ( frame.isVisible() ) {
                System.err.println("gridPane.size = " + gridPane.getChildren().size());
                //gridPane.getChildren().get(0).toFront();
                //frame.hide();
                gridPane.setGridLinesVisible(true);
            } else {
                frame.show();
                //gridPane.setGridLinesVisible(true);
            }
            
            System.err.println("topGrid.parent = " + frame.getParent());
            
/*            System.err.println("topGrid.isVis = " + frame.getTopGrid().isVisible());
            System.err.println("topGrid.width = " + frame.getTopGrid().getWidth());
            System.err.println("topGrid.height = " + frame.getTopGrid().getHeight());
            System.err.println("topGrid.layoutBounds = " + frame.getTopGrid().getLayoutBounds());
            System.err.println("topGrid.BoundsInLocal = " + frame.getTopGrid().getBoundsInLocal());
            frame.getTopGrid().setStyle("-fx-background-color: aqua");
            frame.getTopGrid().setPrefHeight(20);
            frame.getTopGrid().setPrefWidth(240);
*/            
        });
        RowConstraints rc0 = new RowConstraints();
        RowConstraints rc1 = new RowConstraints();
        RowConstraints rc2 = new RowConstraints();

        ColumnConstraints cc0 = new ColumnConstraints();
        ColumnConstraints cc1 = new ColumnConstraints();
        ColumnConstraints cc2 = new ColumnConstraints();

        gridPane.getColumnConstraints().addAll(cc0, cc1, cc2);
        gridPane.getRowConstraints().addAll(rc0, rc1, rc2);

        for (int i = 0; i < gridPane.getColumnConstraints().size(); i++) {
            for (int j = 0; j < gridPane.getRowConstraints().size(); j++) {
                Button btn = new Button("Btn " + i + "," + j);
                gridPane.add(btn,i,j);
            }
        }
        
        gridPane.add(new Label("lb0_5"), 0, 5);
        gridPane.setStyle("-fx-border-width: 30;  -fx-border-color: red");
        
        stackPane.getChildren().add(gridPane);
        StackPane.setAlignment(gridPane, Pos.CENTER);
        
        stage.setTitle("Test DockSideBar");

        stackPane.setPrefHeight(300);
        stackPane.setPrefWidth(300);

        Button b01 = new Button("Change Rotate Angle");

        //((Region)borderPane.getRight()).setMaxWidth(0);
        Scene scene = new Scene(vbox);
        //scene.getRoot().setStyle("-fx-background-color: yellow");

        Button b02 = new Button("Change Orientation");
        Button b03 = new Button("Change Side");
        Button b04 = new Button("center Button");
        //borderPane.getChildren().addAll(b01,b02,b03);
        b01.setOnAction(e -> {
        });

        stage.setScene(scene);
        stage.setOnShown(s -> {
        });
        stage.show();

        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);

        Dockable.initDefaultStylesheet(null);
        
        //GridPaneFrame frame = new GridPaneFrame(gridPane);
        //frame.setManaged(false);
        //frame.setLayoutX(0);
        //frame.setLayoutY(0);
        //frame.show();
        vbox.getChildren().add(frame);
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
