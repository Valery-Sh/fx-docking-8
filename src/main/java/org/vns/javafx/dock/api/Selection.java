/*
 * Copyright 2018 Your Organisation.
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
package org.vns.javafx.dock.api;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import org.vns.javafx.dock.api.dragging.view.NodeFraming;

/**
 *
 * @author Valery
 */
public abstract class Selection {

    private final ObjectProperty selected = new SimpleObjectProperty();

    public Selection() {
        init();
    }

    private void init() {
        selected.addListener(this::selectedChanged);
    }

    protected void selectedChanged(ObservableValue ov, Object oldValue, Object newValue) {
        NodeFraming nf = DockRegistry.lookup(NodeFraming.class);
        if (newValue == null) {
            if (nf != null) {
                nf.hide();
            }
            return;
        }

        if (nf != null) {
            nf.hide();
        }
        if (newValue instanceof Node) {
            nf.show((Node) newValue);
        }
        notifySelected(newValue);
    }

    public abstract void notifySelected(Object value);

    public ObjectProperty selectedProperty() {
        return selected;
    }

    public void setSelected(Object toSelect) {
        Object old = getSelected();
        this.selected.set(toSelect);
        if ( old == getSelected() ) {
            selectedChanged(null, old, getSelected());
        }
    }

    public Object getSelected() {
        return selected.get();
    }

    /*    protected boolean doSelect(Object toSelect) {
        boolean retval = false;
        return retval;
    }

    public void removeSelected() {
        setSelected(null);
    }

    public void removeSelected(Object obj) {
        if (getSelected() == obj) {
            setSelected(null);
        }
    }
     */
    public static void addListeners(Node node) {
        MouseSelectionListener l = DockRegistry.lookup(MouseSelectionListener.class);
        if (l != null) {
            node.addEventHandler(MouseEvent.MOUSE_PRESSED, l);
            node.addEventHandler(MouseEvent.MOUSE_RELEASED, l);
        }
    }

    public static void removeListeners(Node node) {
        MouseSelectionListener l = DockRegistry.lookup(MouseSelectionListener.class);
        if (l != null) {
            node.removeEventHandler(MouseEvent.MOUSE_PRESSED, l);
            node.removeEventHandler(MouseEvent.MOUSE_RELEASED, l);
        }

    }

    public static interface MouseSelectionListener extends EventHandler<MouseEvent> {

        Object getSource();

        void setSource(Object source);

        void handle(MouseEvent event, Node node);
        //void mousePressed(MouseEvent ev);
        //void mouseReleased(MouseEvent ev);
    }

    public static class SelectionHandler implements MouseSelectionListener {

        private Object source;

        public SelectionHandler() {
        }

        @Override
        public Object getSource() {
            return source;
        }

        @Override
        public void setSource(Object source) {
            this.source = source;
        }

        @Override
        public void handle(MouseEvent ev) {
            if (ev.getEventType() == MouseEvent.MOUSE_PRESSED) {
                mousePressed(ev);

            }
            if (ev.getEventType() == MouseEvent.MOUSE_RELEASED) {
                mouseReleased(ev);
            }
        }

        @Override
        public void handle(MouseEvent ev, Node node) {
            if (ev.getEventType() == MouseEvent.MOUSE_PRESSED) {
                mousePressed(ev, node);
            }
            if (ev.getEventType() == MouseEvent.MOUSE_RELEASED) {
                mouseReleased(ev, node);
            }
        }

        protected void mousePressed(MouseEvent ev) {
            if (ev.getSource() instanceof Node) {
                Selection sel = DockRegistry.lookup(Selection.class);
                sel.setSelected(ev.getSource());
            }
/*            if (true) {
                return;
            }

            NodeFraming nf = DockRegistry.lookup(NodeFraming.class);
            if (nf != null && (ev.getSource() instanceof Node)) {
                nf.show((Node) ev.getSource());
            }
*/
            ev.consume();

        }

        protected void mouseReleased(MouseEvent ev) {
            if ((ev.getSource() == getSource() || getSource() == null) && Dockable.of(ev.getSource()) != null) {
                ev.consume();
            }
        }

        protected void mousePressed(MouseEvent ev, Node node) {
            Selection sel = DockRegistry.lookup(Selection.class);
            sel.setSelected(node);
/*          if (true) {
                return;
            }
            NodeFraming nf = DockRegistry.lookup(NodeFraming.class);
            nf.show(node);
*/
            ev.consume();
        }

        protected void mouseReleased(MouseEvent ev, Node node) {
            if (Dockable.of(node) != null) {
                ev.consume();
            }
        }
    }
}
