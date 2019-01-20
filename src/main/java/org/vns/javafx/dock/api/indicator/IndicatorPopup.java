package org.vns.javafx.dock.api.indicator;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;
import org.vns.javafx.dock.api.Util;
import org.vns.javafx.dock.api.LayoutContext;

/**
 * An instance of the class is created for each object of type
 * {@link LayoutContext} when the last is created.
 *
 * The instance of the class is used by the object of type
 * {@link org.vns.javafx.dock.api.dragging.DragManager} and provides a pop up
 * window in which the user can select a position on the screen where the
 * dragged node will be placed. As a rule, the position is determined as a
 * relative position to the target object, which can be an object of type
 * {@link org.vns.javafx.dock.api.Dockable} or
 * {@link org.vns.javafx.dock.api.DockLayout}. The position of the target object
 * is set as a value of type {@code javafx.geometry.Side} the object is given
 * enum type Side and can take one of the values: Side.TOP, Side.RIGHT,
 * Side.BOTTOM or Side.LEFT.
 * <p>
 * Most of the work with the object of the class is done in the method
 * DragManager.mouseDragged(MouseEvent) . If the mouse cursor resides above the
 * {@code dockable node} then {@code DragPoup} provides two panes of position
 * indicators:
 * </P>
 * <ul>
 * <li>The pane of indicators for the {@code dockable} node</li>
 * <li>The pane of indicators for the {@code DockPaneTarget} object which is a
 * parent of the {@code dockable node} mentioned above
 * </li>
 * </ul>
 *
 * If the mouse cursor resides above the {@code DockPaneTarget} then
 * {@code DragPoup} provides a single pane of position indicators.
 * <p>
 * Each pane generally comprises four indicators which are objects of type
 * Button. Every button is located on the top, right, bottom side of the
 * indicator pane. As noted above, the position of the button is determined in
 * terms of {@code enum Side} type.
 * </p>
 * When the user moves the mouse cursor and the cursor intersects one of the
 * buttons of the indicator pane, the rectangular area is displayed. This area
 * points to the position of the dragged object in case the user releases the
 * mouse button.
 * <p>
 * While dragging the object {@code Dockable} the drag manager object defines
 * the cursor position and calculates the object type {@code DockPaneTarget},
 * over which the mouse cursor is. For each recovered target it's own drag pop
 * up is used to display an indicator pane.Such approach allows for different
 * implementations of {@code DockPaneTarget} to use different types of indicator
 * pane. For instance, the class {@code org.vns.javafx.dock.DockTabPane} uses an
 * indicator pane containing a single button which moves with mouse cursor when
 * the later is above the {@code tab area} of the {@code TabPane} control. This
 * lets a user to insert a dragged object in the desired position or even
 * rearrange {@code Tab} objects.
 * </p>
 *
 * @author Valery Shyshkin
 */
public class IndicatorPopup extends Popup implements IndicatorManager {

    /**
     * The owner of this object
     */
    private final LayoutContext targetContext;

    public enum KeysDown {
        NONE,
        CTLR_DOWN,
        ALT_DOWN,
        SHIFT_DOWN,
        CTLR_ALT_DOWN,
        CTLR_SHIFT_DOWN,
        ALT_SHIFT_DOWN,
    }
    
    private final ObjectProperty<KeysDown> keysDown = new SimpleObjectProperty(KeysDown.NONE);
            
    private Node draggedNode;

    @Override
    public Node getDraggedNode() {
        return draggedNode;
    }

    @Override
    public void setDraggedNode(Node draggedNode) {
        this.draggedNode = draggedNode;
    }

    private final ObservableList<IndicatorPopup> childWindows = FXCollections.observableArrayList();

    /**
     * Creates a new instance for the specified target context.
     *
     * @param target the owner of the object to be created
     */
    public IndicatorPopup(LayoutContext target) {
        this.targetContext = target;
        init();
    }

    private void init() {
        initContent();
    }
    
    public ObjectProperty<KeysDown> keysDownProperty() {
        return keysDown;
    }
    public KeysDown getKeysDown() {
        return keysDown.get();
    }

    @Override
    public void setKeysDown(KeysDown keys) {
        this.keysDown.set(keys);
    }

    /*    public static IndicatorPopup getInstance(LayoutContext context) {
        return context.getLookup().lookup(IndicatorPopup.class);
    }
     */
    @Override
    public void show(Window ownerWindow) {
        if (!(ownerWindow instanceof IndicatorPopup)) {
            throw new IllegalStateException("The parameter 'ownerWindow' must be of type " + getClass().getName());
        }

        super.show(ownerWindow);
        if (getChildWindows().contains(ownerWindow)) {
            return;
        }

        check((IndicatorPopup) ownerWindow);
        getChildWindows().add((IndicatorPopup) ownerWindow);

    }

    @Override
    public void show(Window ownerWindow, double anchorX, double anchorY) {
        if (!(ownerWindow instanceof IndicatorPopup)) {
            throw new IllegalStateException("The parameter 'ownerWindow' must be of type " + getClass().getName());
        }

        super.show(ownerWindow, anchorX - 6, anchorY - 6);

        if (((IndicatorPopup) ownerWindow).getChildWindows().contains(this)) {
            return;
        }
        ((IndicatorPopup) ownerWindow).getChildWindows().add(this);
    }

    private void check(IndicatorPopup popup) {
        IndicatorPopup p = popup;
        while (p != null) {
            if (!(p.getOwnerWindow() instanceof IndicatorPopup)) {
                break;
            }
            if (p.getChildWindows().contains(popup)) {
                p.getChildWindows().remove(popup);
            }
            p = (IndicatorPopup) p.getOwnerWindow();
        }
    }

    @Override
    public void show(Node ownerNode, double anchorX, double anchorY) {
        Pane p = targetContext.getPositionIndicator().getIndicatorPane();
        Insets ins = p.getInsets();
        super.show(ownerNode.getScene().getWindow(), anchorX - ins.getLeft(), anchorY - ins.getTop());
    }

    @Override
    public void hide() {
        if (getOwnerWindow() instanceof IndicatorPopup) {
            IndicatorPopup p = (IndicatorPopup) getOwnerWindow();
        }
        super.hide();
    }

    public ObservableList<IndicatorPopup> getChildWindows() {
        return childWindows;
    }

    /**
     * Returns an object of type {@code Region} which corresponds to the pane
     * handler which used to create this object.
     *
     * @return Returns an object of type {@code Region}
     */
    public Node getTargetNode() {
        return targetContext.getLayoutNode();
    }

    protected void initContent() {
        ChangeListener<? super Bounds> boundsListener = (v, oldValue, newValue) -> {
            Pane indicatorPane = targetContext.getPositionIndicator().getIndicatorPane();
            indicatorPane.applyCss();
            Insets ins = indicatorPane.getInsets();
            Bounds bnd = getTargetNode().localToScene(getTargetNode().getBoundsInLocal());

            indicatorPane.setPrefHeight(bnd.getHeight() + ins.getTop() + ins.getBottom());
            indicatorPane.setPrefWidth(bnd.getWidth() + ins.getLeft() + ins.getRight());
            indicatorPane.setMinHeight(bnd.getHeight() + ins.getTop() + ins.getBottom());
            indicatorPane.setMinWidth(bnd.getWidth() + ins.getLeft() + ins.getRight());
            Point2D pos = getTargetNode().localToScreen(0, 0);

            setX(bnd.getMinX() - ins.getLeft());
            setY(bnd.getMinY() - ins.getTop());

            targetContext.getPositionIndicator().updateIndicatorPane();

        };
        ChangeListener<? super KeysDown> keysDownListener = (v, oldValue, newValue) -> {
            targetContext.getPositionIndicator().updateIndicatorPane();
        };        
        setOnHidden(e -> {
            getTargetNode().boundsInParentProperty().removeListener(boundsListener);
            getTargetNode().layoutBoundsProperty().removeListener(boundsListener);
            keysDownProperty().removeListener(keysDownListener);
        });
        setOnShown(e -> {
            keysDownProperty().addListener(keysDownListener);
            
            if (targetContext.getPositionIndicator() == null || targetContext.getPositionIndicator().getIndicatorPane() == null) {
                return;
            }
            if (targetContext.getPositionIndicator().getIndicatorPane() == null) {
                return;
            }
            
            Pane indicatorPane = targetContext.getPositionIndicator().getIndicatorPane();
            indicatorPane.applyCss();
            Insets ins = indicatorPane.getInsets();
            Bounds bnd = getTargetNode().localToScreen(getTargetNode().getBoundsInLocal());
            Bounds localBnd = getTargetNode().getBoundsInLocal();
            Point2D pos = getTargetNode().localToScreen(0, 0);
            Point2D scenePos = new Point2D( getTargetNode().getScene().getX(),getTargetNode().getScene().getY());
            Point2D winPos = new Point2D( getTargetNode().getScene().getWindow().getX(),getTargetNode().getScene().getWindow().getY());
            
            setX(bnd.getMinX() - ins.getLeft());
            setY(bnd.getMinY() - ins.getTop());

            if (getTargetNode() instanceof Region) {
                indicatorPane.setPrefHeight(bnd.getHeight() + ins.getTop() + ins.getBottom());
                indicatorPane.setPrefWidth(bnd.getWidth() + ins.getLeft() + ins.getRight());
                indicatorPane.setMinHeight(bnd.getHeight() + ins.getTop() + ins.getBottom());
                indicatorPane.setMinWidth(bnd.getWidth() + ins.getLeft() + ins.getRight());

                getTargetNode().boundsInParentProperty().addListener(boundsListener);

            } else {
                getTargetNode().layoutBoundsProperty().addListener(boundsListener);
            }
            
            indicatorPane.setMouseTransparent(true);
            if (!getContent().contains(indicatorPane)) {
                getContent().add(indicatorPane);
            }
            targetContext.getPositionIndicator().updateIndicatorPane();
            
        }
        );

    }

    public ObservableList<IndicatorPopup> getAllChildIndicatorPopup() {
        ObservableList<IndicatorPopup> list = FXCollections.observableArrayList();

        list.addAll(getChildWindows());
        getChildWindows().forEach(w -> {
            getAllChildIndicatorPopup(list, w);
        });
        return list;
    }

    private void getAllChildIndicatorPopup(ObservableList<IndicatorPopup> list, IndicatorPopup popup) {
        list.addAll(popup.getChildWindows());
    }

    /**
     * Returns an object of type {@link PositionIndicator} to display indicators
     * for an object of type {@link org.vns.javafx.dock.api.DockPaneContext }
     *
     * @return Returns an object of type {@code PositionIndicator}
     */
    @Override
    public PositionIndicator getPositionIndicator() {
        return targetContext.getPositionIndicator();
    }

    /**
     * Returns the owner of this object used when the instance created.
     *
     * @return the owner of this object used when the instance created.
     */
    @Override
    public LayoutContext getTargetContext() {
        return this.targetContext;
    }

    /**
     * Shows this pop up window
     */
    @Override
    public void showIndicator(KeysDown keysDown) {
        if (getPositionIndicator() == null) {
            return;
        }
        
        getPositionIndicator().updateIndicatorPane();
        setAutoFix(false);
        Point2D pos = getTargetNode().localToScreen(0, 0);
        Pane pane = getPositionIndicator().getIndicatorPane();
        Insets ins = pane.getInsets();
        getPositionIndicator().showIndicatorPopup(getDraggedNode(), pos.getX() - ins.getLeft(), pos.getY() - ins.getTop());
    }

    /**
     * Hides the pop up window when some condition are satisfied. If this pop up
     * is hidden returns true. If the mouse cursor is still inside the pane
     * indicator then return true. Otherwise hides the pop up and returns false
     *
     * @param x a screen x coordinate of the mouse cursor
     * @param y a screen y coordinate of the mouse cursor
     *
     * @return If this pop up is hidden returns true. If the mouse cursor is
     * still inside the pane indicator then return true. Otherwise hides the pop
     * up and returns false
     */
    @Override
    public boolean hideWhenOut(double x, double y) {
        if (!isShowing()) {
            return true;
        }
        boolean retval = false;
        if (Util.contains(getPositionIndicator().getIndicatorPane(), x, y)) {
            retval = true;
        } else if (isShowing()) {
            hide();
        }
        return retval;
    }

    /**
     * The method is called when the the mouse moved during drag operation.
     *
     * @param screenX a screen mouse position
     * @param screenY a screen mouse position
     */
    @Override
    public void handle(double screenX, double screenY) {
        if (getPositionIndicator() == null) {
            return;
        }
        
        getPositionIndicator().showDockPlace(screenX, screenY);

        ((Rectangle) getDockPlace()).strokeDashOffsetProperty().set(1);
        if (getDockPlace().isVisible()) {
            getDockPlace().toFront();

            Timeline placeTimeline = new Timeline();
            placeTimeline.setCycleCount(Timeline.INDEFINITE);
            KeyValue kv = new KeyValue(((Rectangle) getDockPlace()).strokeDashOffsetProperty(), 12);
            KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
            placeTimeline.getKeyFrames().add(kf);
            placeTimeline.play();
        }
    }

    /**
     * Returns a shape of type {@code  Rectangle} to be displayed to
     * showSideIndicator a proposed dock place
     *
     * @return a shape of type {@code  Rectangle} to be displayed to
     * showSideIndicator a proposed dock place
     */
    public Node getDockPlace() {
        return targetContext.getPositionIndicator().getDockPlace();
    }
}
