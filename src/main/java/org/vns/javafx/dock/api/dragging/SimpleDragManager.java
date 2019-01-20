/*
 * Copyright 2017 Your Organisation.
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
package org.vns.javafx.dock.api.dragging;

import org.vns.javafx.dock.api.dragging.view.FloatViewFactory;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.PopupControl;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.JdkUtil;
import org.vns.javafx.TopWindowFinder;
import org.vns.javafx.DefaultTopWindowFinder;
import org.vns.javafx.dock.api.Util;
import org.vns.javafx.dock.api.DockLayout;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.DragContainer;
import org.vns.javafx.dock.api.LayoutContext;
import org.vns.javafx.dock.api.SaveRestore;
import org.vns.javafx.dock.api.Selection;
import static org.vns.javafx.dock.api.dragging.DragManager.HideOption.ALL;
import static org.vns.javafx.dock.api.dragging.DragManager.HideOption.CARRIER;
import static org.vns.javafx.dock.api.dragging.DragManager.HideOption.CARRIERED;
import static org.vns.javafx.dock.api.dragging.DragManager.HideOption.NONE;
import org.vns.javafx.dock.api.dragging.view.FloatView;
import org.vns.javafx.dock.api.selection.SelectionFrame;
import org.vns.javafx.dock.api.indicator.IndicatorManager;
import org.vns.javafx.dock.api.indicator.IndicatorPopup.KeysDown;

/**
 * The class manages the process of dragging of the object of type
 * {@link Dockable}} from the moment you press the mouse button and ending by
 * initiation docking operations.
 *
 * The objects of typo {@code Dockable} can have a title bar. It is an object of
 * type {@code Region}, which is assigned by calling the method
 * DockableContext.setTitleBar(javafx.scene.layout.Region) or by applying the
 * method DockableContext.createDefaultTitleBar(java.lang.String). The title bar
 * object automatically becomes a listener of mouse events by executing the code
 * below:
 * <pre>
 *   titleBar.addEventHandler(MouseEvent.MOUSE_PRESSED,  this);
 *   titleBar.addEventHandler(MouseEvent.DRAG_DETECTED,  this);
 *   titleBar.addEventHandler(MouseEvent.MOUSE_DRAGGED,  this);
 *   titleBar.addEventHandler(MouseEvent.MOUSE_RELEASED, this);
 * </pre> Thus, if the object of type {@code Dockable} has a title bar and it is
 * visible on screen, then it can be used to perform mouse dragging.
 * <p>
 * The object of type {@code Dockable} has a method
 * DockableContext#setDragNode(javafx.scene.Node) . The {@code Node } which has
 * been set by the method may be used to drag the {@literal dockable} in the
 * same manner as the title bar is used. Thus, both objects, such as a title bar
 * and a drag node can be used to perform dragging.
 *
 * </p>
 *
 * @author Valery Shyshkin
 */
public class SimpleDragManager implements DragManager, EventHandler<MouseEvent> {

    private Window dockableWindow;

    private Window floatingWindow;
    /**
     * The object to be dragged
     */
    private final Dockable dockable;

    private Node dragSource;
    /**
     * Pop up window which provides indicators to choose a place of the
     * layoutNode object
     */
    private IndicatorManager indicatorManager;

    private HideOption hideOption = NONE;
    /**
     * The layoutNode dock layoutNode
     */
    private Node targetDockPane;
    /**
     * The window that contains layoutNode
     */
    private Window resultStage;
    /**
     * The mouse screen coordinates assigned by the mousePressed method.
     */
    private Point2D startMousePos;

    /**
     * Create a new instance for the given dock node.
     *
     * @param dockNode the object to be dragged
     */
    public SimpleDragManager(Dockable dockNode) {
        this.dockable = dockNode;
        init();
    }

    private void init() {
        if (dockable.getNode().getScene() != null) {
            dockableWindow = dockable.getNode().getScene().getWindow();
        }
    }

    protected Window getFloatingWindow() {
        return floatingWindow;
    }

    public HideOption getHideOption() {
        return hideOption;
    }

    @Override
    public void setHideOption(HideOption hideOption) {
        this.hideOption = hideOption;
    }

    @Override
    public void mouseDragDetected(MouseEvent ev, Point2D startMousePos) {
        //JdkUtil.setWindows();
        DockRegistry.getTopWindowFinder().dragDetected();
        setStartMousePos(startMousePos);
        this.dragSource = (Node) ev.getSource();

        //dragSource.startFullDrag();
        if (!getDockable().getContext().isFloating()) {
            SaveRestore sr = DockRegistry.lookup(SaveRestore.class);
            if (sr != null) {
                sr.add(getDockable());
            }
            /*            NodeFraming nf = DockRegistry.lookup(NodeFraming.class);
            if (nf != null) {
                nf.hide();
            }
             */
            Selection sel = DockRegistry.lookup(Selection.class);
            if (sel != null) {
                sel.setSelected(null);
            }
            targetDockPane = ((Node) ev.getSource()).getScene().getRoot();

            FloatViewFactory f = null;
            if (getTargetContext(getDockable()) != null) {
                f = getTargetContext(getDockable()).getLookup().lookup(FloatViewFactory.class);
            }
            if (f == null) {
                f = getDockable().getContext().getLookup().lookup(FloatViewFactory.class);
            }
            FloatView view = f.getFloatView(this);

            floatingWindow = (Window) view.make(getDockable());
            DockRegistry.getInstance().getLookup().putUnique(FloatView.class, view);

            targetDockPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            targetDockPane.addEventFilter(MouseEvent.MOUSE_RELEASED, this);
        } else {
            if (floatingWindow == null) {
                //
                // floatingWindow is null if the dragMaager changed
                //
                floatingWindow = getDockable().getNode().getScene().getWindow();
            }
            //
            // If floating window contains snapshot and not the dockable then
            // the folowing two operator must be skipped
            //
            floatingWindow.getScene().getRoot().addEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            floatingWindow.getScene().getRoot().addEventFilter(MouseEvent.MOUSE_RELEASED, this);

        }

    }

    protected LayoutContext getTargetContext(Dockable d) {
        return d.getContext().getLayoutContext();
    }

    protected KeysDown createKeysDown(MouseEvent ev) {
        KeysDown retval = KeysDown.NONE;
        if (ev.isControlDown() && !ev.isAltDown() && !ev.isShiftDown()) {
            retval = KeysDown.CTLR_DOWN;
        } else if (ev.isControlDown() && ev.isAltDown() && !ev.isShiftDown()) {
            retval = KeysDown.CTLR_ALT_DOWN;
        } else if (ev.isControlDown() && !ev.isAltDown() && ev.isShiftDown()) {
            retval = KeysDown.CTLR_SHIFT_DOWN;
        } else if (ev.isAltDown()) {
            retval = KeysDown.ALT_DOWN;
        } else if (ev.isShiftDown()) {
            retval = KeysDown.SHIFT_DOWN;
        }
        return retval;
    }

    /**
     * The method is called when the user moves the mouse and the primary mouse
     * button is pressed. The method checks whether the {@literal  dockable} node
     * is in the {@code floating} state and if not the method returns.<P>
     * If the method encounters a {@literal dockable} node or a
     * {@code dock layoutNode layoutNode} then it shows a pop up window which
     * contains indicators to select a dock place on the layoutNode dock node or
     * layoutNode.
     * <p>
     * The method checks whether the {@code control key} of the keyboard is
     * pressed and if so then it shows a special indicator window which allows
     * to select a dock layoutNode or one of it's parents.
     *
     * @param ev the event that describes the mouse events
     */
    protected void mouseDragged(MouseEvent ev) {
        //List<Window> list = DockRegistry.getWindows__(ev.getSceneX(), ev.getScreenY(), resultStage);
        //List<Window> list = JdkUtil.getWindows();
//        System.err.println("ev.getSource = " + ev.getSource());
//        System.err.println("ev.getTarget = " + ev.getTarget());
//        System.err.println("ev.getPickResult = " + ev.getPickResult());

//        list.forEach(w -> {
//            MouseEvent copy = ev.copyFor(w.getScene().getRoot(), w.getScene().getRoot());
        //System.err.println("fireEvent w.getScene().getRoot = " + w.getScene().getRoot());
//            if ( "VBBBBB".equals(w.getScene().getRoot().getId())) {
        //System.err.println(" isTreeVis =  = " + w.getScene().getRoot().impl_isTreeVisible());
        //ev.get
        //ev.fireEvent(w.getScene().getRoot(), copy);
        //copy.fireEvent(w.getScene().getRoot(), copy);
//            }
//        });
        if (indicatorManager != null && !(indicatorManager instanceof Window)) {
            indicatorManager.hide();
        }
        if (!ev.isPrimaryButtonDown()) {
            ev.consume();
            return;
        }
        if (!getDockable().getContext().isFloating()) {
            return;
        }
        /*        Window ww = TopWindowManager.getInstance().getTopWindow(ev.getScreenX(), ev.getScreenY(), floatingWindow);
        if (ww != null) {
            String idw = (String) ww.getProperties().get("id");
            if (idw != null) {
                System.err.println("TOP WINDOW = " + idw);
            } else {
                Node nd = ww.getScene().lookup("#scene-view");
                if (nd != null) {
                    System.err.println("scene-view");
                } else {
                    nd = ww.getScene().lookup("#palette-pane");
                    if (nd != null) {
                        System.err.println("palette-pane");
                    } else {
                        
                    }
                }
            }
        }
         */
        //
        // The floatingWindow where the floating dockable resides may have a root node as a StackPane
        //
        double leftDelta = 0;
        double topDelta = 0;

        if (getFloatingWindowRoot() instanceof Pane) {
            Insets insets = ((Pane) getFloatingWindowRoot()).getInsets();

            leftDelta = insets.getLeft();
            topDelta = insets.getTop();
        }
        double dx = getDeltaX();
        double dy = getDeltaY();
        if (floatingWindow instanceof PopupControl) {
            ((PopupControl) floatingWindow).setAnchorX(ev.getScreenX() - dx - leftDelta - getStartMousePos().getX());
            ((PopupControl) floatingWindow).setAnchorY(ev.getScreenY() - dy - topDelta - getStartMousePos().getY());
        } else {
            floatingWindow.setX(ev.getScreenX() - dx - leftDelta - getStartMousePos().getX());
            floatingWindow.setY(ev.getScreenY() - dy - topDelta - getStartMousePos().getY());
        }
        if (!getDockable().getContext().isAcceptable()) {
            return;
        }
        if (indicatorManager != null && indicatorManager.isShowing()) {
            if (createKeysDown(ev) != KeysDown.CTLR_DOWN) {
                indicatorManager.hideWhenOut(ev.getScreenX(), ev.getScreenY());
            }
        }
        Window newWindow = null;
        /*        if (Util.contains(dockableWindow, ev.getScreenX(), ev.getScreenY())) {

            newWindow = dockableWindow;
            if (newWindow != null && (newWindow instanceof Stage)) {
                System.err.println("newWindow = " + ((Stage) newWindow).getTitle());
            }

        } else {
            TopWindowFinder wf = DockRegistry.lookup(TopWindowFinder.class);
            if ( wf != null ) {
                newWindow = wf.getTopWindow(ev.getScreenX(), ev.getScreenY(), floatingWindow);
            }
        }
         */

        newWindow = DockRegistry.getTopWindowFinder().getTopWindow(ev.getScreenX(), ev.getScreenY(), floatingWindow);

        if (newWindow == null) {
            return;
        }

        if (newWindow != resultStage && createKeysDown(ev) != KeysDown.CTLR_DOWN) {
            if (indicatorManager != null) {
                indicatorManager.hide();
                indicatorManager = null;
            }
        }
        if ((indicatorManager == null || !indicatorManager.isShowing())) {
            resultStage = DockRegistry.getInstance().getTarget(ev.getScreenX(), ev.getScreenY(), floatingWindow);
        }
/*        if ((indicatorManager == null || !indicatorManager.isShowing())) {
            if (Util.contains(dockableWindow, ev.getScreenX(), ev.getScreenY())) {
                resultStage = dockableWindow;
//                if (resultStage != null && (resultStage instanceof Stage)) {
//                    System.err.println("newWindow = " + ((Stage) newWindow).getTitle());
//                }

            } else {
                resultStage = DockRegistry.getInstance().getTarget(ev.getScreenX(), ev.getScreenY(), floatingWindow);
            }

            //if (indicatorManager != null) {
            //indicatorManager.hide();
            //}
        }
*/
        if (resultStage == null) {
            return;
        }
        Node root = resultStage.getScene().getRoot();

        if (root == null || !(root instanceof Pane) && !(DockRegistry.isDockLayout(root))) {
            return;
        }
        SelectionFrame.hideAll(resultStage);

        LayoutContext tc = null;
        if (indicatorManager != null && indicatorManager.isShowing() && createKeysDown(ev) == KeysDown.CTLR_DOWN) {
            tc = indicatorManager.getPositionIndicator().getLayoutContext();
            root = tc.getLayoutNode();
        } else {
            Node topPane = Util.getTop(resultStage, ev.getScreenX(), ev.getScreenY(), (n) -> {
                return DockRegistry.isDockLayout(n);
            });
            if (topPane != null) {
                root = topPane;
            } else if (!DockRegistry.isDockLayout(root)) {
                return;
            }
            tc = DockRegistry.dockLayout(root).getLayoutContext();
        }

        tc.mouseDragged(getDockable(), ev);

        Object o = getDockable().getContext().getDragValue();
        Node node = null;
        if (DockRegistry.isDockable(o)) {
            node = Dockable.of(o).getNode();
        }
        boolean accept = node != tc.getLayoutNode();

        if (!accept || !DockLayout.of(root).getLayoutContext().isAcceptable(getDockable())) {
            return;
        }

        if (!DockLayout.of(root).getLayoutContext().isAdmissiblePosition(getDockable(), new Point2D(ev.getScreenX(), ev.getScreenY()))) {
            return;
        }
        if (!DockRegistry.dockLayout(root).getLayoutContext().isUsedAsDockLayout()) {
            return;
        }
        //
        // Start use of IndicatorPopup
        //
        IndicatorManager newPopup = DockRegistry.dockLayout(root).getLayoutContext().getLookup().lookup(IndicatorManager.class);
        if (newPopup == null) {
            return;
        }

        newPopup.setDraggedNode(getDockable().getNode());

        if (indicatorManager != newPopup && indicatorManager != null) {
            indicatorManager.hide();
        }
        indicatorManager = newPopup;
        indicatorManager.setKeysDown(createKeysDown(ev));

        if (!indicatorManager.isShowing()) {
            indicatorManager.showIndicator(createKeysDown(ev));
        }
        if (createKeysDown(ev) == KeysDown.CTLR_DOWN) {
            //indicatorManager.getPositionIndicator().modifyOnControlDown(isControlDown(ev));
        }

        indicatorManager.handle(ev.getScreenX(), ev.getScreenY());
    }

    protected double getDeltaX() {
        Node r = floatingWindow.getScene().getRoot();
        double dx = 0;
        if ((r instanceof Pane) && (((Pane) r).getChildren().size() == 1)) {
            dx = ((Pane) r).getChildren().get(0).getBoundsInParent().getMinX();
        }
        return dx;
    }

    protected double getDeltaY() {
        Node r = floatingWindow.getScene().getRoot();
        double dy = 0;
        if ((r instanceof Pane) && (((Pane) r).getChildren().size() == 1)) {
            dy = ((Pane) r).getChildren().get(0).getBoundsInParent().getMinX();
        }
        return dy;

    }

    /**
     * The method is called when a user releases the mouse button.
     *
     * Depending on whether or not the layoutNode object is detected during
     * dragging the method initiates a dock operation or just returns.
     *
     * @param ev the event that describes the mouse events.
     */
    protected void mouseReleased(MouseEvent ev) {
        DockRegistry.getTopWindowFinder().dropped();
        if (!getDockable().getContext().isAcceptable()) {
            return;
        }

        if (indicatorManager != null && indicatorManager.isShowing()) {
            indicatorManager.handle(ev.getScreenX(), ev.getScreenY());
        }
        if (targetDockPane != null) {
            targetDockPane.removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            targetDockPane.removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
            targetDockPane.removeEventHandler(MouseEvent.DRAG_DETECTED, this);
            targetDockPane.removeEventHandler(MouseEvent.MOUSE_DRAGGED, this);
            targetDockPane.removeEventHandler(MouseEvent.MOUSE_RELEASED, this);

        }
        if (dragSource != null) {
            dragSource.removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            dragSource.removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
            dragSource.removeEventHandler(MouseEvent.MOUSE_DRAGGED, this);
            dragSource.removeEventHandler(MouseEvent.MOUSE_RELEASED, this);
        }
        if (floatingWindow != null) {
            floatingWindow.getScene().getRoot().removeEventFilter(MouseEvent.MOUSE_DRAGGED, this);
            floatingWindow.getScene().getRoot().removeEventFilter(MouseEvent.MOUSE_RELEASED, this);
        }
        boolean isDocked = false;
        Object dragValue = getDockable().getContext().getDragValue();

        if (indicatorManager != null) {
            Point2D pt = new Point2D(ev.getScreenX(), ev.getScreenY());
            LayoutContext tc = indicatorManager.getTargetContext();
            //
            // Dragged value cannot be the same as targetNode 
            //
            Node node = null;
            if (DockRegistry.isDockable(dragValue)) {
                node = Dockable.of(dragValue).getNode();
            }
            boolean accept = node != tc.getLayoutNode() && tc.isAdmissiblePosition(getDockable(), pt);
            if (accept && (indicatorManager.isShowing() || indicatorManager.getPositionIndicator() == null)) {
                tc.executeDock(pt, getDockable());

                if (LayoutContext.isDocked(tc, getDockable())) {
                    Object obj = LayoutContext.getValue(getDockable());
                    if (obj != null) {
                        Node nodeObj = null;
                        if (obj instanceof Node) {
                            nodeObj = (Node) obj;
                        } else if (Dockable.of(obj) != null) {
                            nodeObj = Dockable.of(obj).getNode();
                        }
                        if (nodeObj != null) {
                            Selection sel = DockRegistry.lookup(Selection.class);
                            if (sel != null) {
                                sel.setSelected(null);
                                sel.setSelected(nodeObj);
                            }
                        }
                    }
                }

                isDocked = LayoutContext.isDocked(tc, getDockable());
                if (isDocked && floatingWindow != null && floatingWindow.isShowing()) {
                    hideFloatingWindow();
                }
            }
            if (indicatorManager != null && indicatorManager.isShowing()) {
                indicatorManager.hide();
            }
        }
        if ((getHideOption() == ALL || getHideOption() == CARRIERED) && getContainerValue() != null && floatingWindow != null) {
            hideFloatingWindow();
        }
        if ((getHideOption() == ALL || getHideOption() == CARRIER) && getContainerValue() == null && floatingWindow != null) {
            hideFloatingWindow();
        }

        DragContainer dc = getDockable().getContext().getDragContainer();

        if (dc != null && dc.getPlaceholder() != null) {
            getDockable().getContext().setDragContainer(null);
        }
        DockRegistry.getInstance().getLookup().clear(FloatView.class);

        if (!ev.isAltDown()) {
            SaveRestore sr = DockRegistry.lookup(SaveRestore.class);
            if (sr != null && sr.isSaved()) {
                Object o = dragValue;
                if (Dockable.of(o) != null) {
                    o = Dockable.of(o).getNode();
                }
                sr.restore(o);
                sr.remove(o);
                if (floatingWindow != null) {
                    hideFloatingWindow();
                }
            }
        } else {
            SaveRestore sr = DockRegistry.lookup(SaveRestore.class);
            if (sr != null && sr.isSaved()) {
                Object o = dragValue;
                if (Dockable.of(o) != null) {
                    o = Dockable.of(o).getNode();
                    System.err.println("Dockable.of(o).dragNode = " + Dockable.of(o).getContext().getDragNode());
                }
                sr.remove(o);
            }
        }
    }

    protected void hideFloatingWindow() {
        if (floatingWindow != null && (floatingWindow instanceof Stage)) {
            ((Stage) floatingWindow).close();
        } else {
            floatingWindow.hide();
        }
    }

    protected Dockable getContainerDockable() {
        Dockable retval = null;
        DragContainer dc = getDockable().getContext().getDragContainer();
        Object v = dc.getValue();

        if (v != null && (dc.isValueDockable())) {
            retval = Dockable.of(v);
        }
        return retval;
    }

    protected Object getContainerValue() {
        DragContainer dc = getDockable().getContext().getDragContainer();
        return dc == null ? null : dc.getValue();
    }

    @Override
    public Dockable getDockable() {
        return dockable;
    }

    protected Node getFloatingWindowRoot() {
        return floatingWindow.getScene().getRoot();
    }

    @Override
    public void handle(MouseEvent ev) {
        ev.consume();
        if (ev.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
            mouseDragged(ev);
        } else if (ev.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
            mouseReleased(ev);
        }

    }

    public Node getDragSource() {
        return dragSource;
    }

    public IndicatorManager getIndicatorManager() {
        return indicatorManager;
    }

    public Node getTargetDockPane() {
        return targetDockPane;
    }

    public Window getResultStage() {
        return resultStage;
    }

    public Point2D getStartMousePos() {
        return startMousePos;
    }

    public void setTargetDockPane(Parent targetDockPane) {
        this.targetDockPane = targetDockPane;
    }

    public void setResultStage(Window resultStage) {
        this.resultStage = resultStage;
    }

    public void setStartMousePos(Point2D startMousePos) {
        this.startMousePos = startMousePos;
    }

    public void setIndicatorManager(IndicatorManager indicatorManager) {
        this.indicatorManager = indicatorManager;
    }

    public void setDragSource(Node dragSource) {
        this.dragSource = dragSource;
    }
}
