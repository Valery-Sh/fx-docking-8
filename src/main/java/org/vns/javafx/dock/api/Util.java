package org.vns.javafx.dock.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import javafx.collections.ObservableList;
import javafx.css.Styleable;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.stage.Window;
import org.vns.javafx.JdkUtil;
import static org.vns.javafx.dock.api.Constants.FOREIGN;
import static org.vns.javafx.dock.api.Constants.SKIP_CSS_CLASS;
import org.vns.javafx.dock.api.resizer.DividerLine;
import org.vns.javafx.dock.api.resizer.ResizeShape;
import org.vns.javafx.dock.api.selection.SelectionFrame;
import static org.vns.javafx.dock.api.selection.SelectionFrame.FRAME_CSS_CLASS;

/**
 *
 * @author Valery Shyshkin
 */
public class Util {

    public static final String PIXEL_SHAPE_ID = "pixel_shape_id";

    public static String repeatString(String s, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(s);
        } 
        return sb.toString();
    }
    

    public static Color pixelToColor(int pixel) {
        int alpha = ((pixel >> 24) & 0xff);

        int red = ((pixel >> 16) & 0xff);
        int green = ((pixel >> 8) & 0xff);
        int blue = ((pixel) & 0xff);
        return Color.rgb(red, green, blue);
    }

    public static Circle getPixelShape(Window win, double screenX, double screenY, Color fillColor) {
        if (win.getScene() == null || win.getScene().getRoot() == null) {
            return null;
        }
        Parent p = win.getScene().getRoot();
        Circle c = (Circle) win.getScene().lookup("#" + PIXEL_SHAPE_ID);
        if (c == null) {
            c = newPixelShape(win, screenX, screenY, fillColor);
        }
        c.setFill(fillColor);
        c.setVisible(true);
        //Point2D pos = p.screenToLocal( (int)screenX - 2, (int)screenY - 2);
        Point2D pos = p.screenToLocal(0, 0);
        //c.relocate(pos.getX() + 2, pos.getY() + 2);
        c.setCenterX(((int) pos.getX()));
        c.setCenterY(((int) pos.getY()));

        return c;
    }

    public static Circle newPixelShape(Window win, double screenX, double screenY, Color fillColor) {
        if (win.getScene() == null || win.getScene().getRoot() == null) {
            return null;
        }
        Parent p = win.getScene().getRoot();
        System.err.println("screenX = " + screenX + "; screenY = " + screenY);
        Point2D pos = p.screenToLocal((int) screenX - 2, (int) screenY - 2);
        System.err.println("   --- poz.X = " + pos.getX() + "; pos.Y = " + pos.getY());
        Circle c = new Circle(6);
        c.setId(PIXEL_SHAPE_ID);
        c.setFill(fillColor);

        c.setManaged(false);
        getChildren(p).add(c);
        //c.setLayoutX(pos.getX());
        c.setCenterX(((int) pos.getX()));
        c.setCenterY(((int) pos.getY()));
        //c.setLayoutY(pos.getY());
        c.toFront();
        return c;
    }

    private static boolean isFrame(Object obj) {

        if (obj == null || (!(obj instanceof SelectionFrame) && !(obj instanceof ResizeShape))) {
            return false;
        }
        return ((Node) obj).getStyleClass().contains(FRAME_CSS_CLASS);
    }

    public static boolean isFrameShape(Object obj) {
        boolean retval = isFrame(obj);
        if (!retval && (obj instanceof Shape)) {
            retval = ResizeShape.isResizeShape((Shape) obj);
        }
        return retval;
    }

    public static boolean isFrameLine(Object obj) {
        boolean retval = isFrame(obj) || isFrameShape(obj);
        if (!retval && (obj instanceof Shape)) {
            retval = DividerLine.isDividerLine((Shape) obj);
        }
        return retval;
    }

    public static boolean isForeign(Object obj) {
        if (!(obj instanceof Styleable)) {
            return false;
        }
        if (isFrame(obj)) {
            return true;
        }
        boolean retval = ((Styleable) obj).getStyleClass().contains(FOREIGN);
        if (!retval && (obj instanceof Node) && ((Node) obj).getParent() != null && ((Node) obj).getParent().getStyleClass().contains(FOREIGN)) {
            retval = true;
        }
        return retval;
    }

    public static void setForeign(Styleable... nodes) {
        for (Styleable node : nodes) {
            if (!node.getStyleClass().contains(FOREIGN)) {
                node.getStyleClass().add(FOREIGN);
            }
        }
    }

    public static List<Node> getChildren(Parent p) {
        ObservableList list = null;

        if (p instanceof Pane) {
            list = ((Pane) p).getChildren();
        }
        if (p instanceof Group) {
            list = ((Group) p).getChildren();
        }

        if (list != null) {
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
        if (node.getScene() != null && node.getScene().getWindow() != null && node.getScene().getWindow().isShowing()) {
            w = node.localToScreen(node.getBoundsInLocal()).getWidth();
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
        return getTop(root.getScene().getWindow(), screenX, screenY, predicate);
    }

    /*    private static Node findNode(Parent root, Node toSearch) {
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
     */
    public static void print(Parent root) {
        print(root, 1, " ", p -> {
            return ((p instanceof Control) || (p instanceof Pane))
                    && !(p.getClass().getName().startsWith("com.sun.javafx"));
        });
    }

    public static Bounds sceneIntersection(Node node) {
        Scene scene = node.getScene();
        if (scene == null) {
            return null;
        }
        Bounds retval = null;

        Bounds nodeBnd = node.localToScene(node.getBoundsInLocal());

        if (nodeBnd.getMinX() > scene.getWidth()) {
            return retval;
        }
        if (nodeBnd.getMinX() + nodeBnd.getWidth() < 0) {
            return retval;
        }
        if (nodeBnd.getMinY() > scene.getHeight()) {
            return retval;
        }
        if (nodeBnd.getMinY() + nodeBnd.getHeight() < 0) {
            return retval;
        }

        double x = 0;
        double y = 0;
        double w = 0;
        double h = 0;

        if (nodeBnd.getMinX() >= 0) {
            x = nodeBnd.getMinX();
            if (x + nodeBnd.getWidth() <= scene.getWidth()) {
                w = nodeBnd.getWidth();
            } else {
                w = scene.getWidth() - x;
            }
        } else {
            w = nodeBnd.getWidth() - nodeBnd.getMinX();
            if (w > scene.getWidth()) {
                w = scene.getWidth();
            }
        }
        if (nodeBnd.getMinY() >= 0) {
            y = nodeBnd.getMinY();
            if (y + nodeBnd.getHeight() <= scene.getHeight()) {
                h = nodeBnd.getHeight();
            } else {
                h = scene.getHeight() - y;
            }
        } else {
            h = nodeBnd.getHeight() - nodeBnd.getMinY();
            if (h > scene.getHeight()) {
                h = scene.getHeight();
            }

        }
        return new BoundingBox(x, y, w, h);
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
    public static Window getWindowIfSingle(double screenX, double screenY, Window... excl) {
        List<Window> exclList = Arrays.asList(excl);
        Window retval = null;
        for ( Window w : JdkUtil.getWindows() ) {
            if ( ! contains(w, screenX, screenY)) {
                continue;
            }
            if ( ! exclList.contains(w) && retval == null ) {
                retval = w;
            } else if ( ! exclList.contains(w) ) {
                retval = null;
                break;
            } 
        }
        
        return retval;
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

    /*    private static Node findNode(Pane pane, double x, double y) {
        Node retval = null;
        for (Node node : pane.getChildren()) {
            if (contains(node, x, y)) {
                retval = node;
                break;
            }
        }
        return retval;
    }
     */
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
        if (win == null) {
            return false;
        }
        return ((screenX >= win.getX() && screenX <= win.getX() + win.getWidth()
                && screenY >= win.getY() && screenY <= win.getY() + win.getHeight()));
    }

    /*    private static Node findNode(List<Node> list, double x, double y) {
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
     */
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

    /*    private static List<Parent> getParentChain(Parent root, Node child, Predicate<Parent> predicate) {
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
     */
    /**
     * Returns the top node in the scene graph of the scene of the specified
     * window. May be used with the code dealing with the mouse events and takes
     * into account the visible property and mouseTransparent property of the
     * tested nodes.
     *
     * @param win the window to be tested
     * @param screenX the x coordinate in the screen coordinate space
     * @param screenY the y coordinate in the screen coordinate space
     * @param predicate the predicate used to filter nodes
     *
     * @return the top node in the scene graph or null if not found
     */
    public static Node getTop(Window win, double screenX, double screenY, Predicate<Node> predicate) {
        if (win == null || win.getScene() == null || win.getScene().getRoot() == null) {
            return null;
        }
        return getTop(win.getScene().getRoot(), screenX, screenY, predicate);
    }

    /**
     * Returns the top node in the scene graph of the specified node. The method
     * doesn't take into account the {@code visible} property and
     * {@code mouseTransparent} property. If no node found the method return
     * {@code null}. If the specified node doesn't contain the given point the
     * method returns null.
     *
     * @param node the node to be tested
     * @param screenX the x coordinate in the screen coordinate space
     * @param screenY the y coordinate in the screen coordinate space
     *
     * @return the top node in the scene graph of the node given as parameter
     */
    public static Node getTop(Node node, double screenX, double screenY) {
        return getTop(node, screenX, screenY, n -> {
            return true;
        });
    }

    /**
     * Returns the top node in the scene graph of the specified node. The method
     * doesn't take into account the {@code visible} property and
     * {@code mouseTransparent} property. If no node found the method return
     * {@code null}. If the specified node doesn't contain the given point or
     * does not pass the test specified by the predicate the the method returns
     * null.
     *
     * @param node the node to be tested
     * @param screenX the x coordinate in the screen coordinate space
     * @param screenY the y coordinate in the screen coordinate space
     * @param predicate the predicate used to filter nodes
     * @return the top node in the scene graph of the node given as parameter
     */
    public static Node getTop(Node node, double screenX, double screenY, Predicate<Node> predicate) {

        String skip = SKIP_CSS_CLASS;
        Point2D p = node.screenToLocal(screenX, screenY);

        if (!node.contains(p) || node.getStyleClass().contains(skip) || Util.isForeign(node)) {
            return null;
        }
        if (!(node instanceof Parent) && predicate.test(node)) {
            return node;
        } else if (!(node instanceof Parent)) {
            return null;
        }

        Node top = testTop(node, screenX, screenY, predicate);

        if (top == null) {
            return null;
        }
        while (top != null && !predicate.test(top)) {
            top = top.getParent();
        }

        return top;
    }

    private static Node testTop(Node node, double screenX, double screenY, Predicate<Node> predicate) {
        String skip = SKIP_CSS_CLASS;
        Point2D p = node.screenToLocal(screenX, screenY);

        if (!(node.contains(p) && !node.getStyleClass().contains(skip) && !Util.isForeign(node))) {
            return node;
        }
        if (node instanceof Parent) {
            Node top = null;
            List<Node> children = ((Parent) node).getChildrenUnmodifiable();
            for (int i = children.size() - 1; i >= 0; i--) {
                Node c = children.get(i);
                p = c.screenToLocal(screenX, screenY);
                if (c.isVisible() && !node.isMouseTransparent() && c.contains(p) && !c.getStyleClass().contains(skip)) {
                    top = c;
                    break;
                }
            }

            if (top != null) {
                return testTop(top, screenX, screenY, predicate);
            }
        }
        return node;
    }

}
