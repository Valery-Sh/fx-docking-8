/*
 * Copyright 2019 Your Organisation.
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
package org.vns.javafx;

import com.sun.javafx.stage.StageHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.vns.javafx.dock.api.Util;

/**
 *
 * @author Valery Shyshkin
 */
public abstract class AbstractTopWindowFinder implements TopWindowFinder {

    public static final String WINDOW_WRAPPER = "window-wrapper-a3f6cb93-ac36-48ca-bb7a-7172095aed0c";
    private WindowWrapper ownerTree;

    private boolean started = false;
    private boolean ready = false;

    private final ObservableList<Window> orderedWindows = FXCollections.observableArrayList();
    private final ObservableList<Window> windows = FXCollections.observableArrayList();
//    private final ObservableList<Window> allWindows = FXCollections.observableArrayList();

    private final ChangeListener<? super Boolean> focusListener = (w, ov, nv) -> {
        Window win = (Window) ((ReadOnlyProperty) w).getBean();

        if (nv) {
            orderedWindows.remove(win);
            orderedWindows.add(0, win);
        }

    };
    private final ChangeListener<? super Boolean> alwaysOnTopListener = (ow, ov, nv) -> {
        Window win = (Window) ((ReadOnlyProperty) ow).getBean();
        if (nv && getWrapper(win).isImplicitlyOnTop()) {
            return;
        }
        List<Window> childs = getImmidiateChildren(win);
        if (!childs.isEmpty() && nv) {
            childs.forEach(w -> {
                WindowWrapper wr = getWrapper(w);
                if (getWrapper(win).isImplicitlyOnTop(false)) {
                    wr.setLateOnTop(false);
                } else if (!isAlwaysOnTop(w)) {
                    wr.setLateOnTop(true);
                }
            });
        } else if (!childs.isEmpty() && !nv) {
            childs.forEach(w -> {
                WindowWrapper wr = getWrapper(w);
                wr.setLateOnTop(false);
            });
        }
    };

    @Override
    public void start() {
        if (started) {
            return;
        }

        Iterator<Window> it = Window.impl_getWindows();
        int i = 0;
        while (it.hasNext()) {
            Window w = it.next();
            w.getProperties().put("window-title-ordered", i++);
            w.focusedProperty().addListener(focusListener);
            if (w instanceof Stage) {
                ((Stage) w).alwaysOnTopProperty().addListener(alwaysOnTopListener);
            }
            getWindows().add(w);
            WindowWrapper wr = new WindowWrapper(w);
            w.getProperties().put(WINDOW_WRAPPER, wr);
        }
        orderedWindows.addAll(getWindows());
        addWindowsChangeListener();
        started = true;
    }

    public static WindowWrapper getWrapper(Window win) {
        if (win == null) {
            return null;
        }
        if (win.getProperties().get(WINDOW_WRAPPER) == null) {
            //win.getProperties().put(WINDOW_WRAPPER, new WindowWrapper(win));
        }
        return (WindowWrapper) win.getProperties().get(WINDOW_WRAPPER);
    }

    @Override
    public void dropped() {
        ready = false;
    }

    @Override
    public void dragDetected() {
        if (!started) {
            return;
        }
        ready = true;

        orderedWindows.forEach(w -> {
            getWrapper(w).getChildren().clear();
        });

        ownerTree = new WindowWrapper();
        for (Window w : orderedWindows) {
//            System.err.println("1. getWrapper(w) = " + getWrapper(w));
            if (getOwner(w) == null || !getOwner(w).isShowing()) {
                WindowWrapper wr = getWrapper(w);
//                System.err.println("2. wr = " + wr);
                ownerTree.getChildren().add(wr);
                //wr.setLevel(0);
                createChidren(wr);
            }
        }
        //
        // determine lastFocused
        //

        for (WindowWrapper wr : ownerTree.getChildren()) {
            computeLastFocused(wr);
        }

    }

    protected void computeLastFocused(WindowWrapper wrapper) {
        WindowWrapper last = computeLastFocused(wrapper, wrapper.getAllChildren());
        wrapper.setLastFocused(last);
        for (WindowWrapper wr : wrapper.getChildren()) {
            computeLastFocused(wr);
        }

    }

    protected void createChidren(WindowWrapper wrapper) {
        getOrderedWindows().forEach(w -> {
            if (getOwner(w) != null && getOwner(w).isShowing() && getOwner(w) == wrapper.getWindow()) {
                WindowWrapper child = getWrapper(w);//,wrapper);
                wrapper.getChildren().add(child);
                createChidren(child);
            }
        });
    }

    protected WindowWrapper computeLastFocused(WindowWrapper listOwner, List<WindowWrapper> list) {
        WindowWrapper retval = null;
        int maxIdx = -1;
        int idx = -1;
        for (WindowWrapper wr : list) {
            idx = orderedWindows.indexOf(wr.getWindow());
            if (maxIdx == -1) {
                maxIdx = idx;
            } else if (idx < maxIdx) {
                maxIdx = idx;
            }
        }
        if (listOwner.getWindow() != null) {
            idx = orderedWindows.indexOf(listOwner.getWindow());
            if (maxIdx == -1) {
                maxIdx = idx;
            } else if (idx < maxIdx) {
                maxIdx = idx;
            }
        }
        if (maxIdx >= 0) {
            retval = getWrapper(orderedWindows.get(maxIdx));
        }
        return retval;
    }

    private List<Window> getImmidiateChildren(Window win) {
        List<Window> list = new ArrayList<>(0);
        for (Window w : getOrderedWindows()) {
            if (getOwner(w) == win) {
                list.add(w);
            }
        }
        return list;
    }

    protected WindowWrapper getOwnerTree() {
        return ownerTree;
    }

    protected void addWindowsChangeListener() {
        StageHelper.getStages().addListener((Change<? extends Stage> change) -> {
            while (change.next()) {
                if (change.wasPermutated()) {
                } else if (change.wasUpdated()) {
                } else if (change.wasReplaced()) {
                } else {
                    if (change.wasRemoved()) {
                        List<? extends Stage> list = change.getRemoved();
                        list.forEach(s -> {
                            getWindows().remove(s);
                            s.focusedProperty().removeListener(focusListener);
                            s.alwaysOnTopProperty().removeListener(alwaysOnTopListener);
                            getOrderedWindows().remove(s);
                        });
                    } else if (change.wasAdded()) {
                        List<? extends Stage> list = change.getAddedSubList();
                        list.forEach(s -> {
                            getWindows().add(0, s);
                            WindowWrapper wr = new WindowWrapper(s);
                            s.getProperties().put(WINDOW_WRAPPER, wr);
                            getOrderedWindows().add(0, s);
                            s.focusedProperty().addListener(focusListener);
                            s.alwaysOnTopProperty().addListener(alwaysOnTopListener);
                        });

                    }
                }
            }
        });

        getWindows().addListener((Change<? extends Window> change) -> {
            while (change.next()) {
                if (change.wasPermutated()) {
                } else if (change.wasUpdated()) {
                } else if (change.wasReplaced()) {
                } else {
                    if (change.wasRemoved()) {
                        List<? extends Window> list = change.getRemoved();
                        list.forEach(s -> {
                            if (!(s instanceof Stage)) {
                                getWindows().remove(s);
                                s.focusedProperty().removeListener(focusListener);
                                getOrderedWindows().remove(s);
                            }
                        });
                    } else if (change.wasAdded()) {
                        List<? extends Window> list = change.getAddedSubList();
                        list.forEach(s -> {
                            if (!(s instanceof Stage)) {
                                //getWindows().add(0, s);
                                WindowWrapper wr = new WindowWrapper(s);
                                s.getProperties().put(WINDOW_WRAPPER, wr);
                                getOrderedWindows().add(0, s);
                                s.focusedProperty().addListener(focusListener);
                            }
                        });

                    }
                }
            }
        });

    }

  
    protected ObservableList<Window> getWindows() {
        return windows;
    }

    /**
     * Returns ordered by focus window list. The element with 0 index is the
     * last focused window.
     *
     * @return ordered by focus window list
     */
    public ObservableList<Window> getOrderedWindows() {
        return orderedWindows;
    }

    public static Window getOwner(Window win) {
        if (win == null) {
            return null;
        }
        Window retval = null;
        if (win instanceof PopupWindow) {
            PopupWindow p = (PopupWindow) win;
            retval = p.getOwnerWindow();
        }
        if (win instanceof Stage) {
            Stage p = (Stage) win;
            retval = p.getOwner();
        }
        return retval;
    }

    public boolean isReady() {
        return ready;
    }
    public void print(Window win) {
        if (win != null) {
            if ( win instanceof Stage ) {
                System.err.println("RETVAL = " + ((Stage) win).getTitle());
            } else {
                System.err.println("RETVAL = " + win);                
            }
        } else {
            System.err.println("RETVAL = null");
        }

    }
    @Override
    public Window getTopWindow(double x, double y, Window... exclude) {
        if (!ready) {
            return null;
        }
        Window retval = Util.getWindowIfSingle(x, y, exclude);
        if (retval != null) {
            print(retval);
            return retval;
        }
        List<Window> list = new ArrayList<>();
        List<Window> exclList = Arrays.asList(exclude);
        for (Window w : getOrderedWindows()) {
            if (Util.contains(w, x, y) && !exclList.contains(w)) {
                list.add(w);
            }
        }
        if (list.isEmpty()) {
            print(retval);
            return null;
        } else if (list.size() == 1) {
            print(list.get(0));
            return list.get(0);
        }

        List<Window> list1 = new ArrayList<>(list);
        for (Window w : list1) {
            WindowWrapper wr = getWrapper(w); //wrapperMap.get(w);
            if (isAlwaysOnTop(w) || wr.isImplicitlyOnTop()) {
                wr = wr.getParent();
                while (wr != null && wr.getWindow() != null) {
                    list.remove(wr.getWindow());
                    wr = wr.getParent();
                }
            } else {
                wr = getWrapper(w).getParent();
                while (wr != null && wr.getWindow() != null && !isAlwaysOnTop(wr.getWindow())) {
                    list.remove(wr.getWindow());
                    wr = wr.getParent();
                }
            }
        }
        if (list.isEmpty()) {
            print(retval);
            return null;
        } else if (list.size() == 1) {
            print(list.get(0));
            return list.get(0);
        }

        boolean hasTop = false;
        for (Window w : list) {
            if (isAlwaysOnTop(w) || getWrapper(w).isImplicitlyOnTop()) {
                hasTop = true;
                break;
            }
        }
        if (hasTop) {
            list1 = new ArrayList<>(list);
            for (Window w : list1) {
                if (!isAlwaysOnTop(w) && !getWrapper(w).isImplicitlyOnTop()) {
                    list.remove(w);
                }
            }
        }
        //
        // Now the list contains windows which are all always on top (explicitly or implicitly)
        // or all are not on top (explicitly or implicitly)
        //

        if (list.isEmpty()) {
            retval = null;
        } else if (list.size() == 1) {
            retval = list.get(0);
        } else if (list.size() > 1) {
            retval = getTopWindow(list);
        }
        print(retval);
        return retval;
    }

    private boolean isOnTop(Window win) {
        return getWrapper(win).isOnTop();
    }

    protected Window getTopWindow(List<Window> list) {
        Map<Window, WindowWrapper> rootMap = new HashMap<>();
        for (Window w : list) {
            rootMap.put(w, getWrapper(w).getRoot());
        }

        List<Window> list1 = new ArrayList<>(list);

        Window top = list1.get(0);

        for (int i = 1; i < list1.size(); i++) {
            Window w = getTopWindow(top, rootMap.get(top), list1.get(i), rootMap.get(list1.get(i)));
            if (w != top) {
                top = w;
            }
        }
        return top;

    }

    protected Window getTopWindow(Window w1, WindowWrapper root1, Window w2, WindowWrapper root2) {
        Window retval = null;
//        System.err.println("root1.title = " + ((Stage) root1.getWindow()).getTitle());
//        System.err.println("   -- w1 = " + ((Stage) w1).getTitle());
//        System.err.println("root2.title = " + ((Stage) root2.getWindow()).getTitle());
//        System.err.println("   -- w2 = " + ((Stage) w2).getTitle());

        if (root1 == root2) {
            retval = getHigher(w1, w2);
        } else {
//            System.err.println("root1.title = " + ((Stage) root1.getWindow()).getTitle());
//            System.err.println("root2.title = " + ((Stage) root2.getWindow()).getTitle());
            WindowWrapper wr1 = root1.getLastFocused(); //12.01
            WindowWrapper wr2 = root2.getLastFocused(); //12.01

//            System.err.println("root1 wr1 = " + wr1);
//            System.err.println("root2 wr2 = " + wr2);
            if (isOnTop(w1) && !isOnTop(w2)) {
                retval = w1;
            } else if (!isOnTop(w1) && isOnTop(w2)) {
                retval = w2;
            } else if (isOnTop(w1) && isOnTop(w2)) {
                Window w1Last = wr1.getLastFocused().getWindow();
                Window w2Last = wr2.getLastFocused().getWindow();

                if (isOnTop(w1Last) && !isOnTop(w2Last)) {
                    retval = w1;
                } else if (!isOnTop(w1Last) && isOnTop(w2Last)) {
                    retval = w2;
                } else {

                    if (orderedWindows.indexOf(w1Last) < orderedWindows.indexOf(w2Last)) {
                        retval = w1;
                    } else {
                        retval = w2;
                    }
                }
            } else {
                Window w1Last = wr1.getLastFocused().getWindow();
                Window w2Last = wr2.getLastFocused().getWindow();
                if (orderedWindows.indexOf(w1Last) < orderedWindows.indexOf(w2Last)) {
                    retval = w1;
                } else {
                    retval = w2;
                }
            }
        }
        return retval;
    }

    protected Window getHigher(Window w1, Window w2) {

        Window retval = null;
        List<Window> w1Owners = new ArrayList<>();
        List<Window> w2Owners = new ArrayList<>();

        if (retval == null) {
            Window p = getOwner(w1);
            w1Owners.add(w1);
            boolean b = false;
            while (p != null) {
                if (p == w2) {
                    b = true;
                    break;
                }
                w1Owners.add(p);
                p = getOwner(p);
            }
            if (b) {
                retval = w1;
            } else {
                w2Owners.add(w2);
                p = getOwner(w2);
                b = false;
                while (p != null) {
                    if (p == w1) {
                        b = true;
                        break;
                    }
                    w2Owners.add(p);
                    p = getOwner(p);
                }
                if (b) {
                    retval = w2;
                }
            }
            if (retval == null) {
                //
                // w1 and w2 in different chains. We'll find the the nearest common 
                // owner Window.
                //
                if (isOnTop(w1) && isOnTop(w2)) {
                    retval = getHigherWhenBothOnTop(w1, w2);
                }
                if (retval == null) {

                    Window minRoot = null;
                    for (Window w : w1Owners) {
                        if (w2Owners.contains(w)) {
                            minRoot = w;
                            break;
                        }
                    }
                    int idx = w1Owners.indexOf(minRoot);
                    Window w1Root = w1Owners.get(idx - 1);

                    idx = w2Owners.indexOf(minRoot);
                    Window w2Root = w2Owners.get(idx - 1);

                    Window top1 = getWrapper(w1Root).getLastFocused().getWindow();//12.01
                    Window top2 = getWrapper(w2Root).getLastFocused().getWindow(); //12.01
                    if (orderedWindows.indexOf(top1) < orderedWindows.indexOf(top2)) {
                        retval = w1;
                    } else {
                        retval = w2;
                    }
                    if (retval == null) {
                        if (orderedWindows.indexOf(w1) < orderedWindows.indexOf(w2)) {
                            retval = w1;
                        } else {
                            retval = w2;
                        }
                    }
                }
            }
        }

        return retval;
    }

    protected Window getHigherWhenBothOnTop(Window w1, Window w2) {
        if (w1.isFocused()) {
            return w1;
        } else if (w2.isFocused()) {
            return w2;
        }
        WindowWrapper lastFocused = getWrapper(getRoot(w1)).getLastFocused();
        if (lastFocused.getWindow() == w1) {
            return w1;
        } else if (lastFocused.getWindow() == w2) {
            return w2;
        }
        if (isOnTop(lastFocused.getWindow())) {
            if (lastFocused.contains(getWrapper(w1)) || getWrapper(w1).contains(lastFocused)) {
                return w1;
            } else if (lastFocused.contains(getWrapper(w2)) || getWrapper(w2).contains(lastFocused)) {
                return w2;
            }
        }
        Window retval = null;
        //
        // w1 and w2 in different chains. We'll find the the nearest common 
        // owner Window.
        //
        Window commonRoot = getCommonNearestRoot(w1, w2);

        Window w1Common = getWrapper(w1).getAsChildOf(getWrapper(commonRoot)).getWindow();
        Window w2Common = getWrapper(w2).getAsChildOf(getWrapper(commonRoot)).getWindow();

        if (isOnTop(w1Common) && !isOnTop(w2Common)) {
            retval = w1;
        } else if (!isOnTop(w1Common) && isOnTop(w2Common)) {
            retval = w2;
        } else if (isOnTop(w1Common) && isOnTop(w2Common)) {
            retval = orderedWindows.indexOf(w1Common) < orderedWindows.indexOf(w2Common) ? w1 : w2;
        }
        return retval;
    }

    protected Window getRoot(Window win) {
        return getWrapper(win).getRoot().getWindow();
    }

    /**
     * Return the object of type {@code Window} which is the first owner of both
     * specified windows.
     *
     * @param w1 the Window as a starting point to search a root
     * @param w2the Window as a starting point to search a root
     *
     * @return the first owner of both specified windows.
     */
    protected Window getCommonNearestRoot(Window w1, Window w2) {
        Window retval = null;
        List<Window> list1 = new ArrayList<>();
        Window p = getOwner(w1);
        list1.add(w1);
        while (p != null) {
            list1.add(p);
            p = getOwner(p);
        }
        if (list1.contains(w2)) {
            retval = w2;
        } else {
            p = getOwner(w2);
            while (p != null) {
                if (list1.contains(p)) {
                    retval = p;
                    break;
                }
                p = getOwner(p);
            }
        }

        return retval;
    }

    public static boolean isAlwaysOnTop(Window win) {
        if (win == null || !win.isShowing()) {
            return false;
        }
        boolean retval = false;
        if (win instanceof PopupWindow) {
            retval = true;
        } else if (win instanceof Stage) {
            retval = ((Stage) win).isAlwaysOnTop();
        }

        return retval;
    }

    /**
     * !!! For test purpose only
     */
    public void printOrdered() {
        for (Window w : orderedWindows) {
            if (w instanceof Stage) {
                System.err.println("Window title = " + ((Stage) w).getTitle() + "; onTop = " + isAlwaysOnTop(w));
            }
        }
        System.err.println("************************************************");
    }

    public static class WindowWrapper {

        private Window window;
        private boolean lateOnTop;
        private WindowWrapper lastFocused;

        private final ObservableList<WindowWrapper> children = FXCollections.observableArrayList();

        public WindowWrapper() {
            this(null);
        }

        public WindowWrapper(Window window) {
            this.window = window;
        }

        private List<WindowWrapper> getAllChildren() {
            List<WindowWrapper> retval = new ArrayList<>();
            getChildren().forEach((wr) -> {
                retval.add(wr);
            });
            getChildren().forEach((wr) -> {
                getAllChildren(wr, retval);
            });
            return retval;
        }

        private void getAllChildren(WindowWrapper wrapper, List<WindowWrapper> list) {
            wrapper.getChildren().forEach((wr) -> {
                list.add(wr);
            });
            wrapper.getChildren().forEach((wr) -> {
                getAllChildren(wr, list);
            });
        }

        /**
         *
         * @return
         */
        public WindowWrapper getRoot() {
            if (getWindow() == null) {
                return null;
            }
            WindowWrapper retval = this;
            WindowWrapper p = getParent();
            while (p != null) {
                retval = p;
                p = p.getParent();
            }
            return retval;
        }

        /**
         * Returns the last focused window of the subtree defined by this
         * wrapper as a root.
         *
         * @return the last focused window of the subtree defined by this
         * wrapper as a root.
         */
        public WindowWrapper getLastFocused() {
            return lastFocused;
        }

        /**
         * Returns {@code true} if the object specified as a parameter has this
         * object as it's parent on any level.
         *
         * @param wrapper the wrapper to be checked
         * @return {@code true} if the object specified as a parameter has this
         * as it's parent
         */
        public boolean contains(WindowWrapper wrapper) {
            boolean retval = false;
            WindowWrapper p = wrapper.getParent();
            while (p != null) {
                if (p == this) {
                    retval = true;
                    break;
                }
                p = p.getParent();
            }
            return retval;
        }

        /**
         * Sets the last focused window of the subtree defined by this wrapper
         * as a root.
         *
         * @param the last focused window of the subtree defined by this wrapper
         * as a root.
         */
        public void setLastFocused(WindowWrapper lastFocused) {
            this.lastFocused = lastFocused;
        }

        /**
         * Returns the window this wrapper is created for.
         *
         * @return the window this wrapper is created for
         */
        public Window getWindow() {
            return window;
        }

        public boolean isLateOnTop() {
            return lateOnTop;
        }

        public void setLateOnTop(boolean lateOnTop) {
            this.lateOnTop = lateOnTop;
        }

        private boolean isOnTop() {
            return isAlwaysOnTop() || isImplicitlyOnTop();
        }

        public boolean isAlwaysOnTop() {
            if (getWindow() == null) {
                return false;
            }
            boolean retval = false;
            if (getWindow() instanceof PopupWindow) {
                retval = true;
            } else if (getWindow() instanceof Stage) {
                retval = ((Stage) getWindow()).isAlwaysOnTop();
            }

            return retval;
        }

        public boolean isAlwaysOnTop(Window win) {
            if (win == null) {
                return false;
            }
            boolean retval = false;
            if (win instanceof PopupWindow) {
                retval = true;
            } else if (win instanceof Stage) {
                retval = ((Stage) win).isAlwaysOnTop();
            }

            return retval;
        }

        public boolean isImplicitlyOnTop() {
            if (getWindow() == null || isAlwaysOnTop()) {
                return false;
            }
            if (isLateOnTop()) {
                return false;
            }
            boolean retval = false;
            WindowWrapper p = getParent();
            while (p != null) {
                if (p.isLateOnTop()) {
                    break;
                } else if (isAlwaysOnTop(p.getWindow())) {
                    retval = true;
                    break;
                }
                p = p.getParent();
            }
            return retval;
        }

        /**
         * The method is used in allwaysOnTopListener only.
         *
         * @param alwaysOnTopValue oldValue
         * @return true if the window considered to be implicitly on top
         */
        public boolean isImplicitlyOnTop(boolean alwaysOnTopValue) {

            if (getWindow() == null || alwaysOnTopValue) {
                return false;
            }
            if (isLateOnTop()) {
                return false;
            }
            boolean retval = false;
            Window p = getOwner(getWindow());
            while (p != null && p.isShowing() && !getWrapper(p).isLateOnTop()) {
                if (isAlwaysOnTop(p)) {
                    retval = true;
                    break;
                }
                p = getOwner(p);
            }
            return retval;
        }

        public ObservableList<WindowWrapper> getChildren() {
            return children;
        }

        public WindowWrapper getParent() {
            if (getWindow() == null || getOwner(getWindow()) == null || !getOwner(getWindow()).isShowing()) {
                return null;
            }
            return getWrapper(getOwner(getWindow()));
            //return parent;
        }

        public WindowWrapper getAsChildOf(WindowWrapper root) {
            WindowWrapper retval = null;
            if (root == this) {
                return null;
            }
            if (root.getChildren().contains(this)) {
                return this;
            }
            WindowWrapper p = this.getParent();
            while (p != null && p != root) {
                if (root.getChildren().contains(p)) {
                    retval = p;
                    break;
                }
                p = p.getParent();
            }
            return retval;
        }

        @Override
        public String toString() {
            if (getWindow() == null) {
                return "NULL";
            }
            StringBuilder sb = new StringBuilder();
            if (getWindow() instanceof Stage) {
                if (((Stage) getWindow()).getTitle() != null) {
                    sb.append(((Stage) getWindow()).getTitle())
                            .append(" : Stage");
                } else {
                    sb.append("Stage");
                }
            }
            String alwaysOnTop = isAlwaysOnTop(window) ? "true" : "false";
            alwaysOnTop = "alwaysOnTop=" + alwaysOnTop + ";";
            String implicitOnTop = isImplicitlyOnTop() ? "true" : "false";
            implicitOnTop = "implicitOnTop=" + implicitOnTop + ";";
            String late = isLateOnTop() ? "true" : "false";
            late = "lateOnTop=" + late;
            String lastFocus = "; lastFocus=";
            if (getLastFocused() == null || !(getLastFocused().getWindow() instanceof Stage)) {
                lastFocus = lastFocus + null;
            } else if (getLastFocused().getWindow() instanceof Stage) {
                lastFocus = lastFocus + ((Stage) getLastFocused().getWindow()).getTitle();
            }
            sb.append(" {")
                    .append(alwaysOnTop)
                    .append(implicitOnTop)
                    .append(late)
                    .append(lastFocus)
                    .append("}");

            return sb.toString();

        }
    }//WindowWrapper
}
