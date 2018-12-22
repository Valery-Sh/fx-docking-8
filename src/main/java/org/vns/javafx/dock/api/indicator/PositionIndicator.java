package org.vns.javafx.dock.api.indicator;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import org.vns.javafx.dock.api.LayoutContext;

public abstract class PositionIndicator {

    private final LayoutContext layoutContext;

    private IndicatorPopup indicatorPopup;

    private Node dockPlace;

    private Pane indicatorPane;

    protected PositionIndicator(LayoutContext layoutContext) {
        this.layoutContext = layoutContext;
        init();
    }

    private void init() {
        indicatorPane = createIndicatorPane();
        
        dockPlace = createDockPlace();
        dockPlace.getStyleClass().add("dock-place");
        addDockPlace();
        Node node = layoutContext.getLayoutNode();
        if ( node.getScene() != null && node.getScene().getWindow() != null) {
            updateIndicatorPane();
        }
    }


    protected void updateIndicatorPane() {
        
    }
    
    protected void addDockPlace() {
        getIndicatorPane().getChildren().add(dockPlace);
    }

    protected Node createDockPlace() {
        Node retval = new Rectangle();
        retval.setId("dockPlace");
        return retval;
    }

    protected Boolean intersects(Node node1, Node node2) {
        if (node1 == null || node2 == null) {
            return false;
        }
        Bounds b1 = node1.localToScreen(node1.getBoundsInLocal());
        Bounds b2 = node2.localToScreen(node2.getBoundsInLocal());
        return b1.intersects(b2);

    }

    protected void showIndicatorPopup(Node ownerNode, double screenX, double screenY) {
        getIndicatorPopup().show(ownerNode, screenX, screenY);
    }

    public Node getDockPlace() {
        return dockPlace;
    }

    public void showDockPlace(double x, double y) {
        getDockPlace().setVisible(true);
    }

    public LayoutContext getLayoutContext() {
        return layoutContext;
    }

    protected abstract Pane createIndicatorPane();

    public IndicatorPopup getIndicatorPopup() {
        if (indicatorPopup == null) {
            indicatorPopup = (IndicatorPopup) layoutContext.getLookup().lookup(IndicatorManager.class);
        }
        return indicatorPopup;
    }

    public Pane getIndicatorPane() {
        return indicatorPane;
    }

    public void hideDockPlace() {
        getDockPlace().setVisible(false);
    }


    protected void updateSnapshot(boolean controlDown) {
        Pane pane = getIndicatorPane();
        Node node = pane.lookup("#snapshot-image-view");
        if (node != null) {
            pane.getChildren().remove(node);
        }
        if (!controlDown) {
            return;
        }
        node = getIndicatorPopup().getTargetNode();

        Bounds nodeBounds = node.localToScreen(node.getBoundsInLocal());
        Insets ins = pane.getInsets();

        WritableImage im = new WritableImage((int) Math.round(nodeBounds.getWidth()), (int) Math.round(nodeBounds.getHeight()));
        im = node.snapshot(null, im);
        ImageView iv = new ImageView(im);
        
        iv.setId("snapshot-image-view");

        pane.getChildren().add(iv);
        Bounds ivBounds = iv.localToScreen(iv.getBoundsInLocal());
        
        double dw = 0;
        double dh = 0;
        if ( ivBounds != null ) {
            dw = ivBounds.getWidth()- nodeBounds.getWidth();
            dh = ivBounds.getHeight()- nodeBounds.getHeight();
        }
        
        iv.setLayoutX(ins.getLeft() + dw);
        iv.setLayoutY(ins.getTop() + dh);
        iv.toBack();
        iv.setMouseTransparent(true);
        pane.setMouseTransparent(true);
    }

}//PositionIndicator
