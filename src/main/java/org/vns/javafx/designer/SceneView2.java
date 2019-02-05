package org.vns.javafx.designer;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Region;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.ScenePaneContext.ScenePaneContextFactory;
import org.vns.javafx.dock.api.Selection;
import org.vns.javafx.dock.api.bean.BeanAdapter;
import org.vns.javafx.dock.api.selection.SelectionFrame;
import static org.vns.javafx.dock.api.selection.SelectionFrame.NODE_ID;
import static org.vns.javafx.dock.api.selection.SelectionFrame.PARENT_ID;
import org.vns.javafx.dock.api.selection.NodeFraming;
import static org.vns.javafx.dock.api.selection.SelectionFrame.FRAME_CSS_CLASS;

/**
 *
 * @author Valery
 */
@DefaultProperty(value = "content")
public class SceneView2 extends Control {

    private SceneGraphViewLayoutContext targetContext;

    public static final int LAST = 0;
    public static final int FIRST = 2;

    public static double ANCHOR_OFFSET = 4;

    private final TreeViewEx treeView;

    private final ObjectProperty<Node> root = new SimpleObjectProperty<>();

    private final ObjectProperty<Node> statusBar = new SimpleObjectProperty<>();

    private Map<Class<?>, Map<String, Object>> saved = new HashMap<>();
    
    private RootHandler rootHandler;
    
    public SceneView2(Node rootNode) {
        this.treeView = new TreeViewEx<>(null);
        root.set(rootNode);
        init();
    }

    private void init() {
        rootHandler = new RootHandler(this);
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

        SceneView2 sv = DesignerLookup.lookup(SceneView2.class);
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
        Parent p = (Parent) DesignerLookup.lookup(SceneView2.class).getRoot();
        return (SelectionFrame) p.getScene().getRoot().lookup("#" + NODE_ID);
    }

    public static SelectionFrame getParentFrame() {
        Parent p = (Parent) DesignerLookup.lookup(SceneView2.class).getRoot();
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

    public void visitRoot(Consumer<TreeItemEx> consumer) {
        getTreeView().getRoot();
        visit((TreeItemEx) getTreeView().getRoot(), consumer);
    }

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

    public static class RootHandler {

        private SceneView2 control;
        private boolean update;

        public RootHandler(SceneView2 control) {
            this.control = control;
            if (control.getRoot() != null) {
                createSceneGraph(control.getRoot());
            }

            TreeView treeView = control.getTreeView();
            treeView.rootProperty().addListener((v, ov, nv) -> {
                if (nv != null && control.getRoot() == null) {
                    control.setRoot((Node) ((TreeItem) nv).getValue());
                } else if (nv == null) {
                    control.setRoot(null);
                }
            });

            control.rootProperty().addListener(this::rootChanged);
        }

        protected void rootChanged(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
            if (update) {
                return;
            }
            try {

                if (oldValue != null && oldValue.getScene() != null) {
                    if (oldValue.getScene() != null) {
                        if ((oldValue.getScene().getEventDispatcher() instanceof DesignerSceneEventDispatcher)) {
                            ((DesignerSceneEventDispatcher) oldValue.getScene().getEventDispatcher()).finish(oldValue.getScene());
                        }
                        DesignerSceneEventDispatcher d = new DesignerSceneEventDispatcher();
                    }
                }
                if (oldValue != null && oldValue.getScene() != null) {
                    oldValue.getScene().getRoot().getStylesheets().remove(DesignerLookup.class.getResource("resources/styles/designer-customize.css").toExternalForm());
                    oldValue.getScene().getRoot().getStyleClass().remove("designer-mode-root");
                    SceneView.removeFramePanes(oldValue.getScene().getRoot());
                }

                if (newValue == null) {
                    control.getTreeView().setRoot(null);
                    return;
                }

                createSceneGraph(newValue);
            } finally {
                update = false;
            }
        }

        private void createSceneGraph(Node node) {
            ChangeListener<? super Scene> sceneListener = (v, oldScene, newScene) -> {
                if (oldScene != null) {
                    if ((oldScene.getEventDispatcher() instanceof DesignerSceneEventDispatcher)) {
                        ((DesignerSceneEventDispatcher) oldScene.getEventDispatcher()).finish(oldScene);
                    }
                }
                if (newScene != null) {
                    DesignerSceneEventDispatcher d = new DesignerSceneEventDispatcher();
                    d.start(newScene);
                    SceneView.addFramePanes(newScene.getRoot());
                    if (control.getRoot() != null) {
                        //SceneView.getParentFrame().hide();
                        //SceneView.getResizeFrame().hide();
                    }
                    newScene.getRoot().getStyleClass().add("designer-mode-root");
                    newScene.getRoot().getStylesheets().add(DesignerLookup.class.getResource("resources/styles/designer-customize.css").toExternalForm());
                }
            };
            if (node == null) {
                control.getTreeView().setRoot(null);
                return;
            }
            if (node.getScene() != null) {
                if ((node.getScene().getEventDispatcher() instanceof DesignerSceneEventDispatcher)) {
                    ((DesignerSceneEventDispatcher) node.getScene().getEventDispatcher()).finish(node.getScene());
                }
                DesignerSceneEventDispatcher d = new DesignerSceneEventDispatcher();
                d.start(node.getScene());
            }
            //node.sceneProperty().addListener(sceneListener);

            if (control.getTreeView().getRoot() == null || control.getTreeView().getRoot().getValue() != node) {
                TreeItemEx item = new TreeItemBuilder().build(node);
                control.getTreeView().setRoot(item);
            }

            Parent parent;// = null;
            if (node instanceof Parent) {
                parent = (Parent) node;
            } else if (node.getParent() != null) {
                parent = node.getParent();
            }

            if (node.getScene() != null && node.getScene().getRoot() != null) {
                SceneView.addFramePanes(node.getScene().getRoot());
                //getSkinnable().getContext().lookup(SceneView.class).getParentFrame().hide();
                //SceneView.getResizeFrame().hide();
                node.getScene().getRoot().getStyleClass().add("designer-mode-root");
                node.getScene().getRoot().getStylesheets().add(DesignerLookup.class.getResource("resources/styles/designer-customize.css").toExternalForm());
            }
        }
    }
}
