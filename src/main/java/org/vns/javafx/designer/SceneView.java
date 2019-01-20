package org.vns.javafx.designer;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Region;
import javafx.scene.shape.Shape;
import org.vns.javafx.dock.api.Util;
import org.vns.javafx.dock.api.LayoutContext;
import org.vns.javafx.dock.api.DockLayout;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.ScenePaneContext.ScenePaneContextFactory;
import org.vns.javafx.dock.api.Scope;
import org.vns.javafx.dock.api.Selection;
import org.vns.javafx.dock.api.bean.BeanAdapter;
import org.vns.javafx.dock.api.selection.SelectionFrame;
import static org.vns.javafx.dock.api.selection.SelectionFrame.NODE_ID;
import static org.vns.javafx.dock.api.selection.SelectionFrame.PARENT_ID;
import org.vns.javafx.dock.api.selection.NodeFraming;
import org.vns.javafx.dock.api.resizer.DividerLine;
import org.vns.javafx.dock.api.resizer.ResizeShape;
import static org.vns.javafx.dock.api.selection.SelectionFrame.FRAME_CSS_CLASS;

/**
 *
 * @author Valery
 */
@DefaultProperty(value = "content")
public class SceneView extends Control implements DockLayout {

    private SceneGraphViewLayoutContext targetContext;

    public static final int LAST = 0;
    public static final int FIRST = 2;

    public static double ANCHOR_OFFSET = 4;

    private final TreeViewEx treeView;

    private final ObjectProperty<Node> root = new SimpleObjectProperty<>();

    private final ObjectProperty<Node> statusBar = new SimpleObjectProperty<>();

    private final ObservableList<TreeCell> visibleCells = FXCollections.observableArrayList();

    private Map<Class<?>, Map<String, Object>> saved = new HashMap<>();

    private boolean designer;

    public SceneView() {
        this(null, false);
    }

    public SceneView(Node rootNode) {
        this(rootNode, false);
    }

    public SceneView(boolean designer) {
        this(null, designer);
    }

    public SceneView(Node rootNode, boolean designer) {
        this.treeView = new TreeViewEx<>(this);
        root.set(rootNode);
        this.designer = designer;
        init();
    }

    private void init() {
        if (getRoot() != null && isDesigner()) {
            PalettePane palette = DesignerLookup.lookup(PalettePane.class);
        }
        customizeCell();
    }

    public boolean isDesigner() {
        return designer;
    }

    public static void reset(Node startNode) {

        Node root = startNode;
        if (startNode.getScene() != null && startNode.getScene().getRoot() != null) {
            root = startNode.getScene().getRoot();
        }
        if (root.getScene() != null) {
            if ((root.getScene().getEventDispatcher() instanceof DesignerSceneEventDispatcher)) {
                ((DesignerSceneEventDispatcher) root.getScene().getEventDispatcher()).finish(root.getScene());
            }
        }

        DockRegistry.getInstance().getLookup().clear(ScenePaneContextFactory.class);
        NodeFraming fr = DockRegistry.lookup(NodeFraming.class);
        fr.hide();
        fr.removeListeners();

        SceneView sv = DesignerLookup.lookup(SceneView.class);
        sv.visitRoot(item -> {
            ((TreeItemEx) item).unregisterChangeHandlers();
        });

        Set<Node> nodes = root.lookupAll("." + FRAME_CSS_CLASS);
        nodes.forEach(node -> {
            if (node instanceof SelectionFrame) {
                ((SelectionFrame) node).setBoundNode(null);
            }
            if (node.getParent() != null) {
                SceneViewUtil.removeFromParent(node.getParent(), node);
            }
        });
        nodes = root.lookupAll(".designer-mode");
        nodes.forEach(node -> {
            node.getStyleClass().remove("designer-mode");
            node.getStyleClass().remove("designer-mode-root");
            if (node instanceof Parent) {
                ((Parent) node).getStylesheets().remove(DesignerLookup.class.getResource("resources/styles/designer-customize.css").toExternalForm());
            }
            if (node.getEventDispatcher() != null && (node.getEventDispatcher() instanceof PalettePane.PaletteEventDispatcher)) {
                ((PalettePane.PaletteEventDispatcher) node.getEventDispatcher()).finish(node);
            }
            Selection.removeListeners(node);
        });
        DesignerLookup.getInstance().restoreDockRegistry();

        nodes = root.lookupAll(".designer-dock-context");
        nodes.forEach(node -> {
            node.getStyleClass().remove("designer-dock-context");
            DockRegistry.unregisterDockLayout(node);
            DockRegistry.unregisterDockable(node);
        });
        DesignerLookup.getInstance().restoreDockRegistry();
    }

    @Override
    public String getUserAgentStylesheet() {
        return DesignerLookup.class.getResource("resources/styles/designer-default.css").toExternalForm();
    }

    public void save() {
        TreeViewEx tv = getTreeView();
        TreeItemEx r = (TreeItemEx) tv.getRoot();
        saveItem(r);
        r.getChildren().forEach((it) -> {
            save((TreeItemEx) it);
        });
    }

    private void save(TreeItemEx item) {
        saveItem(item);
        item.getChildren().forEach((it) -> {
            save((TreeItemEx) it);
        });
    }

    private void saveItem(TreeItemEx item) {
        Object o = item.getValue();
        if (o == null) {
            return;
        }
        BeanAdapter ba = new BeanAdapter(o);
        Set<String> set = BeanAdapter.getPropertyNames(o.getClass());
        Map<String, Object> map = new HashMap<>();
        set.forEach(name -> {
            Object obj = ba.get(name);
            ChoiceBox bb = null;

            Method fxPropMethod = ba.fxPropertyMethod(name);
            if (!name.equals("class") && !ba.isReadOnly(name) && fxPropMethod != null) {
                map.put(name, ba.get(name));
            }
        });
        saved.put(item.getValue().getClass(), map);

    }

    public ObservableList<TreeCell> getVisibleCells() {
        return visibleCells;
    }


    public ObjectProperty<Node> rootProperty() {
        return root;
    }

    public Node getRoot() {
        return root.get();
    }

    public void setRoot(Node rootNode) {
        this.root.set(rootNode);
    }

    public ObjectProperty<Node> statusParProperty() {
        return statusBar;
    }

    public Node getStatusBar() {
        return statusBar.get();
    }

    public void setStatusBar(Region statusBar) {
        this.statusBar.set(statusBar);
    }

    public TreeViewEx getTreeView() {
        return treeView;
    }

    protected void customizeCell() {
        TreeView<Object> t = treeView;
        t.setCellFactory((TreeView<Object> tv) -> {
            TreeCell cell = new TreeCell() {
                @Override
                public void updateItem(Object value, boolean empty) {
                    super.updateItem(value, empty);

                    if (empty) {
                        this.setMaxHeight(-1);
                        this.setPrefHeight(-1);
                        this.setMinHeight(-1);
                        setText(null);
                        setGraphic(null);
                        getVisibleCells().remove(this);

                    } else {
                        if (value != null && Util.isForeign(value)) {
                            setText(null);
                            this.setGraphic(null);
                            this.setMaxHeight(0);
                            this.setPrefHeight(0);
                            this.setMinHeight(0);
                        } else {
                            this.setGraphic(((TreeItemEx) this.getTreeItem()).getCellGraphic());
                        }
                        if (value != null && (value instanceof Node)) {
                            setId(((Node) value).getId());
                        }
                        if (!getVisibleCells().contains(this)) {
                            getVisibleCells().add(this);
                        }
                    }
                }
            };
            return cell;
        });
    }

   
    public static void addFramePanes(Parent parent) {
        //9.12Node framePane = parent.lookup("#" + FramePane.PARENT_ID);
        Node framePane = parent.lookup("#" + SelectionFrame.PARENT_ID);
        if (framePane == null) {
            //
            // Frame without resize shapes
            //
            framePane = new SelectionFrame(parent, false);
            framePane.setId(SelectionFrame.PARENT_ID);
            SceneViewUtil.addToParent(parent, framePane);
            
        }
        framePane.setVisible(false);
        
        framePane = parent.lookup("#" + SelectionFrame.NODE_ID);
        if (framePane == null) {
            //
            // Frame with resize shapes
            //
            framePane = new SelectionFrame(parent);
            framePane.setId(SelectionFrame.NODE_ID);
            SceneViewUtil.addToParent(parent, framePane);
        }
        framePane.setVisible(false);
    }
    public static void removeFramePanes(Parent parent) {
        Node framePane = parent.lookup("#" + SelectionFrame.PARENT_ID);
        if (framePane == null) {
            return;
        } else {
            SceneViewUtil.removeFromParent(parent, framePane);
        }
    }

    public static SelectionFrame getResizeFrame() {
        Parent p = (Parent) DesignerLookup.lookup(SceneView.class).getRoot();
        return (SelectionFrame) p.getScene().getRoot().lookup("#" + NODE_ID);
    }

    public static SelectionFrame getParentFrame() {
        Parent p = (Parent) DesignerLookup.lookup(SceneView.class).getRoot();
        return (SelectionFrame) p.getScene().getRoot().lookup("#" + PARENT_ID);
    }


    public static boolean removeFramePanes(Node root) {
        boolean retval = false;
        Parent parent = null;
        if (root != null && root.getParent() != null) {
            parent = root.getParent();
        } else if (root instanceof Parent) {
            parent = (Parent) root;
        }
        if (parent != null) {
            Node framePane = parent.lookup("#" + SelectionFrame.NODE_ID);
            if (framePane != null) {
                SceneViewUtil.removeFromParent(parent, framePane);
            }

            framePane = parent.lookup("#" + SelectionFrame.PARENT_ID);
            if (framePane != null) {
                SceneViewUtil.removeFromParent(parent, framePane);
            }
        }

        return retval;
    }

    public TreeViewEx getTreeView(double x, double y) {
        TreeViewEx retval = null;
        if (Util.contains(getTreeView(), x, y)) {
            return getTreeView();
        }
        return retval;
    }

    public TreeItemEx getTreeItem(double x, double y) {
        TreeItemEx retval = null;
        for (TreeCell cell : getVisibleCells()) {
            if (Util.contains(cell, x, y)) {
                retval = (TreeItemEx) cell.getTreeItem();
                break;
            }
        }
        return retval;
    }

    public TreeItemEx getTreeItem(Point2D p) {
        return getTreeItem(p.getX(), p.getY());
    }

    @Override
    public Node getLayoutNode() {
        return this;
    }

    @Override
    public LayoutContext getLayoutContext() {
        if (targetContext == null) {
            targetContext = new SceneGraphViewLayoutContext(this);
            if (isDesigner()) {
                targetContext.getScopes().add(new Scope("designer"));
            }
        }
        return targetContext;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new SceneViewSkin(this);
    }

    public void visitRoot(Consumer<TreeItemEx> consumer) {
        getTreeView().getRoot();
        visit((TreeItemEx) getTreeView().getRoot(), consumer);
    }

    /*    public void iterate(TreeItem item, Consumer<TreeItem> consumer) {
        consumer.accept(item);
        ObservableList<TreeItem> list = item.getChildren();
        list.forEach(it -> {
            iterate(it, consumer);
        });

    }
     */
    public static void visit(TreeItemEx item, Consumer<TreeItemEx> consumer) {
        consumer.accept(item);
        ObservableList list = item.getChildren();
        list.forEach(it -> {
            visit((TreeItemEx) it, consumer);
        });
    }

    public static void reset(TreeItemEx start) {
        visit(start, it -> {
            ((TreeItemEx) it).unregisterChangeHandlers();
        });
    }

}// SceneGraphView
