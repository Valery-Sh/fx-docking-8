package org.vns.javafx.dock;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javafx.collections.ObservableList;
import javafx.css.Styleable;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Window;
import org.vns.javafx.dock.api.DockableContext;
import org.vns.javafx.dock.api.LayoutContext;
import org.vns.javafx.dock.api.TopNodeHelper;
import org.vns.javafx.dock.api.Dockable;

/**
 *
 * @author Valery
 */
public class DockUtil {
    
    public static final String FOREIGN = "FOREIGN-89528991-bd7a-4792-911b-21bf56660bfb";
    
    public static boolean isForeign(Object obj) {
        if ( ! (obj instanceof Styleable) ) {
            return false;
        }
        return ((Styleable)obj).getStyleClass().contains(FOREIGN);
    }
    public static void setForeign(Styleable... nodes) {
        for ( Styleable node : nodes) {
            if ( ! node.getStyleClass().contains(FOREIGN) ) {
                node.getStyleClass().add(FOREIGN);
            }
        }
    }
    
    public static List<Node> getChildren(Parent p) {
        
        
        ObservableList list = null;
        
        if ( p instanceof Pane ) {
            list = ((Pane)p).getChildren();
        }        
        if ( list != null ) {
            return list;
        }
        Class<?> c = p.getClass();
        Method method;
        try {
            method = c.getDeclaredMethod("getChildren");
            method.setAccessible(true);
            list = (ObservableList) method.invoke(p);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            
        }
        return list;
    }

    public static Window getOwnerWindow(Node node) {
        Window retval = null;
        if (node != null && node.getScene() != null && node.getScene().getWindow() != null) {
            retval = node.getScene().getWindow();
        }
        return retval;
    }

    public static double widthOf(Node node) {
        double w = 0;
        if (node.getScene() != null && node.getScene().getWindow() != null && node.getScene().getWindow().isShowing()) {            w = node.localToScreen(node.getBoundsInLocal()).getWidth();
            w = node.localToScreen(node.getBoundsInLocal()).getWidth();
        } else {
            w = node.getLayoutBounds().getWidth();
        }
        return w;
    }

    public static double heightOf(Node node) {
        double h = 0;
        if (node.getScene() != null && node.getScene().getWindow() != null && node.getScene().getWindow().isShowing()) {
            h = node.localToScreen(node.getBoundsInLocal()).getHeight();
        } else {
            h = node.getLayoutBounds().getHeight();
        }

        return h;
    }

    public static Node findDockable(Node root, double screenX, double screenY) {

        Predicate<Node> predicate = (node) -> {
            Point2D p = node.localToScreen(0, 0);
            boolean b = false;

            if (Dockable.of(node) != null) {
                b = true;
                LayoutContext layoutContext = Dockable.of(node).getContext().getLayoutContext();
                DockableContext context = Dockable.of(node).getContext();
                if (layoutContext == null) {
                    b = false;
                } else {
                    b = layoutContext.isUsedAsDockLayout() && context.isUsedAsDockLayout();
                }
            }
            return b;
        };
        //return TopNodeHelperOLD.getTopNode(root.getScene().getWindow(), screenX, screenY, predicate);
        return TopNodeHelper.getTop(root.getScene().getWindow(), screenX, screenY, predicate);
    }

    private static Node findNode(Parent root, Node toSearch) {
        if (toSearch == null) {
            return null;
        }
        Node retval = null;
        for (Node node : root.getChildrenUnmodifiable()) {
            if (node == toSearch) {
                retval = node;
            } else if (node instanceof Parent) {
                retval = findNode((Parent) node, toSearch);
            }
            if (retval != null) {
                break;
            }
        }
        return retval;

    }

    public static void print(Parent root) {
        print(root, 1, " ", p -> {
            return ((p instanceof Control) || (p instanceof Pane))
                    && !(p.getClass().getName().startsWith("com.sun.javafx"));
        });
    }
    public static Bounds sceneIntersection(Node node) {
        Scene scene = node.getScene();
        if ( scene == null ) {
            return null;
        }
        Bounds retval = null;
        
        Bounds nodeBnd = node.localToScene(node.getBoundsInLocal());
        
        if ( nodeBnd.getMinX() > scene.getWidth() ) {
            return retval;
        }
        if ( nodeBnd.getMinX() + nodeBnd.getWidth() < 0 ) {
            return retval;
        }
        if ( nodeBnd.getMinY() > scene.getHeight() ) {
            return retval;
        }
        if ( nodeBnd.getMinY() + nodeBnd.getHeight() < 0 ) {
            return retval;
        }
        
        double x = 0;
        double y = 0;
        double w = 0;
        double h = 0;

        if ( nodeBnd.getMinX() >=  0 ) {
            x = nodeBnd.getMinX();
            if ( x + nodeBnd.getWidth() <= scene.getWidth() ) {
                w = nodeBnd.getWidth();
            } else {
                w = scene.getWidth() - x;
            }
        } else {
            w = nodeBnd.getWidth() - nodeBnd.getMinX() ;
            if ( w > scene.getWidth() ) {
                w = scene.getWidth();
            }
        }
        if ( nodeBnd.getMinY() >=  0 ) {
            y = nodeBnd.getMinY();
            if ( y + nodeBnd.getHeight() <= scene.getHeight() ) {
                h = nodeBnd.getHeight();
            } else {
                h = scene.getHeight() - y;
            }
        } else {
            h = nodeBnd.getHeight() - nodeBnd.getMinY();
            if ( h > scene.getHeight() ) {
                h = scene.getHeight();
            }

        }
        return new BoundingBox(x,y,w,h);
    }
    public static Bounds sceneIntersection_OLD(Node node) {
        Scene scene = node.getScene();
        if ( scene == null ) {
            return null;
        }
        Bounds nodeBnd = node.localToScene(node.getBoundsInLocal());
        
        double x = Math.max(0d, nodeBnd.getMinX() );
        if ( x >= scene.getWidth() ) {
            x = scene.getWidth();
        }
        double y = Math.max(0d, nodeBnd.getMinY() );
        if ( y >= scene.getHeight() ) {
            y = scene.getHeight();
        }
        double w = scene.getWidth() - x;
        double h = scene.getHeight()- y;
        return new BoundingBox(x,y,w,h);
    }
    
    /**
     * Returns {@code true} if the given point (specified in the screen
     * coordinate space) is contained within the shape of the given node. The
     * method doesn't take into account the visibility an transparency of the
     * specified node.
     *
     * @param node the node to be checked
     * @param screenX the x coordinate of the screen
     * @param screenY the y coordinate of the screen
     * @return true if the node contains the point. Otherwise returns false
     */
    public static boolean contains(Node node, double screenX, double screenY) {
        Bounds b = node.localToScreen(node.getBoundsInLocal());
        if (b == null) {
            return false;
        }
        return b.contains(screenX, screenY);
    }

    public static Bounds getHalfBounds(Side side, Node node, double x, double y) {
        Bounds retval;
        Bounds b = node.localToScreen(node.getBoundsInLocal());
        if (!b.contains(x, y)) {
            retval = null;
        } else if (side == Side.TOP) {
            retval = new BoundingBox(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight() / 2);
        } else if (side == Side.BOTTOM) {
            retval = new BoundingBox(b.getMinX(), b.getMinY() + b.getHeight() / 2, b.getWidth(), b.getHeight() / 2);
        } else if (side == Side.LEFT) {
            retval = new BoundingBox(b.getMinX(), b.getMinY(), b.getWidth() / 2, b.getHeight());
        } else {
            retval = new BoundingBox(b.getMinX() + b.getWidth() / 2, b.getMinY(), b.getWidth() / 2, b.getHeight());
        }
        return retval;
    }

    private static Node findNode(Pane pane, double x, double y) {
        Node retval = null;
        for (Node node : pane.getChildren()) {
            if (contains(node, x, y)) {
                retval = node;
                break;
            }
        }
        return retval;
    }

    /**
     * Returns {@code true} if the given point (specified in the screen
     * coordinate space) is contained within the given window. The method
     * doesn't take into account the visibility an transparency of the specified
     * node.
     *
     * @param win the window to be checked
     * @param screenX the x coordinate of the screen
     * @param screenY the y coordinate of the screen
     * @return true if the node contains the point. Otherwise returns false
     */
    public static boolean contains(Window win, double screenX, double screenY) {
        return ((screenX >= win.getX() && screenX <= win.getX() + win.getWidth()
                && screenY >= win.getY() && screenY <= win.getY() + win.getHeight()));
    }

    private static Node findNode(List<Node> list, double x, double y) {
        Node retval = null;
        for (Node node : list) {
            if (!(node instanceof Region)) {
                continue;
            }
            if (contains((Region) node, x, y)) {
                retval = node;
                break;
            }
        }
        return retval;
    }

    public static void print(Parent root, int level, String indent, Predicate<Node> predicate) {
        StringBuilder sb = new StringBuilder();
        print(sb, root, level, indent, predicate);
    }

    public static void print(StringBuilder sb, Node node, int level, String indent, Predicate<Node> predicate) {
        String id = node.getId() == null ? " " : node.getId() + " ";
        String ln = level + "." + id;
        String ind = new String(new char[level]).replace("\0", indent);
        if (predicate.test(node)) {
            sb.append(ind)
                    .append(ln)
                    .append(" : ")
                    .append(node.getClass().getName())
                    .append(System.lineSeparator());
        }
        if (node instanceof Parent) {
            List<Node> list = ((Parent) node).getChildrenUnmodifiable();
            for (Node n : list) {
                int newLevel = level;
                if (predicate.test(n)) {
                    newLevel++;
                }
                print(sb, n, newLevel, indent, predicate);
            }
        }
    }

    private static List<Parent> getParentChain(Parent root, Node child, Predicate<Parent> predicate) {
        List<Parent> retval = new ArrayList<>();
        Node p = child;
        while (true) {
            Parent p1 = getImmediateParent(root, p);
            if (p1 != null) {
                p = p1;
                if (predicate.test(p1)) {
                    retval.add(0, p1);
                }
            } else {
                break;
            }
        }
        return retval;
    }

    private static Parent getImmediateParent(Parent root, Node child) {
        Parent retval = null;
        List<Node> list = root.getChildrenUnmodifiable();
        for (int i = 0; i < list.size(); i++) {
            Node r = list.get(i);
            if (r == child) {
                retval = root;
                break;
            }
            if (r instanceof Parent) {
                retval = getImmediateParent((Parent) r, child);
                if (retval != null) {
                    break;
                }
            }
        }
        return retval;
    }

    private static Parent getImmediateParent(Parent root, Node child, Predicate<Parent> predicate) {
        List<Parent> chain = getParentChain(root, child, predicate);
        Parent retval = null;
        if (!chain.isEmpty() && root != chain.get(chain.size() - 1)) {
            retval = chain.get(chain.size() - 1);
        }
        return retval;
    }

    private static Parent getImmediateParent(Node child, Predicate<Parent> predicate) {

        if (child == null) {
            return null;
        }
        Parent retval = null;
        Parent p = child.getParent();
        while (true) {
            if (p == null) {
                break;
            }
            if (predicate.test(p)) {
                retval = p;
                break;
            }
            p = p.getParent();
        }

        return retval;
    }

}
