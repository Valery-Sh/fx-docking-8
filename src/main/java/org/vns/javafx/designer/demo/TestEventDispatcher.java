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
package org.vns.javafx.designer.demo;

import org.vns.javafx.designer.*;
import java.util.function.BiPredicate;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Dockable;
import org.vns.javafx.dock.api.Selection;
import org.vns.javafx.dock.api.dragging.DragManager;

public class TestEventDispatcher implements PalettePane.PaletteEventDispatcher {

    private EventDispatcher initial;
    private Node target;
    private Node node;
    private BiPredicate<Event, Node> preventCondition;
    private Scene scene;

    boolean pressed;
    boolean dragDetected;
    boolean dragged;
    boolean released;
    String title; 
    public TestEventDispatcher(String title) {
        this(title,null);
        
    }

    public TestEventDispatcher(String title,BiPredicate<Event, Node> cond) {
//        this.scene = scene;
        preventCondition = cond;
        this.title = title;
        init();
    }

    private void init() {
        released = true;
    }

    @Override
    public void start(Node node) {
        this.target = node;
        initial = node.getEventDispatcher();
        node.setEventDispatcher(this);
    }

    public void start(Scene scene) {
        this.scene = scene;
        initial = scene.getEventDispatcher();
        scene.setEventDispatcher(this);
    }

    @Override
    public BiPredicate<Event, Node> getPreventCondition() {
        return preventCondition;
    }

    @Override
    public void setPreventCondition(BiPredicate<Event, Node> preventCondition) {
        this.preventCondition = preventCondition;
    }

    @Override
    public Event dispatchEvent(Event event, EventDispatchChain tail) {
        if (!(event instanceof MouseEvent)) {
            return null;
        }
        MouseEvent mouseEvent = (MouseEvent) event;        
        if (mouseEvent.getEventType() == MouseEvent.MOUSE_ENTERED || mouseEvent.getEventType() == MouseEvent.MOUSE_ENTERED_TARGET) {
           // return event;
        }         
        if (mouseEvent.getEventType() == MouseEvent.MOUSE_EXITED || mouseEvent.getEventType() == MouseEvent.MOUSE_EXITED_TARGET) {
            //return event;
        }         
        
        //
        // not primary button
        //
        if ( mouseEvent.getEventType() != MouseEvent.MOUSE_MOVED) {
            System.err.println("********* " + title);
            System.err.println("EVENT TYPE = " + mouseEvent.getEventType());
            System.err.println("   --- source = " + mouseEvent.getSource());
            System.err.println("   --- target = " + mouseEvent.getTarget());
            System.err.println("------------------------------------------------");
            return null;
        }
        
        if ( true ) {
            return initial.dispatchEvent(event, tail);
        }
      
        if (mouseEvent.getEventType() != MouseEvent.MOUSE_RELEASED && !mouseEvent.isPrimaryButtonDown()) {
            if ( mouseEvent.getEventType() != MouseEvent.MOUSE_MOVED) {
                //System.err.println("INITIAL !!!!!!!!!!!!!!!!!!!!!!! " + mouseEvent.getEventType());
            }
            //return initial.dispatchEvent(event, tail);
            
            //return event;
        }
        
//        if ( SceneView.isFrameShape(event.getTarget()) ) {
//            return initial.dispatchEvent(event, tail);
//        }
        //System.err.println("2 Scene dispatch ev " + mouseEvent.getEventType());

        if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
            return pressed(event, tail);
        } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
            return released(event, tail);
        } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {
            return mouseClicked(event, tail);
        } else if (mouseEvent.getEventType() == MouseEvent.DRAG_DETECTED) {
            return dragDetected(event, tail);
        } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            return dragged(event, tail);
        } else  if (mouseEvent.getEventType() == MouseEvent.MOUSE_EXITED || mouseEvent.getEventType() == MouseEvent.MOUSE_EXITED_TARGET) {
            return mouseEnteredOrExited(event, tail);
        } else {
            return null;
        }

        //return initial.dispatchEvent(event, tail);
    }

    protected boolean acceptable(Event event) {
        boolean retval = false;
        // if ((event instanceof MouseEvent) && event.getSource() == node) {
        //     retval = true;
        // }
        Node parent = null;
        if (event.getTarget() == target) {
            parent = target;
        } else if (Dockable.of(event.getTarget()) == null) {
            parent = getDockableParent((Node) event.getTarget());
        }
        if (parent == target) {
            retval = true;
        }
        return retval;
    }

    public Node getDockableParent(Node startNode) {
        if (Dockable.of(startNode) != null) {
            return startNode;
        }
        Parent p = startNode.getParent();
        while (p != null && p != this.target && Dockable.of(p) == null) {
            p = p.getParent();
        }
        return p;
    }
    protected Event mouseClicked(Event event, EventDispatchChain tail) {
        System.err.println("DISPATCHER MOUSE MOVED");
        return null;
    }
    protected Event pressed(Event event, EventDispatchChain tail) {
//        System.err.println("pickTop node = " + TopNodeHelper.pickTop(scene.getWindow(), ((MouseEvent)event).getScreenX(), ((MouseEvent)event).getScreenY(), n -> {return true;}));
//        System.err.println("SCENE PRESSED TARGET = " + target);
        System.err.println("Scene pressed ev.source = " + event.getSource());        
        System.err.println("Scene pressed ev.target = " + event.getTarget());
        
//        System.err.println("SCENE PRESSED isPrimary = " + ((MouseEvent)event).isPrimaryButtonDown());
//        System.err.println("Scene pressed node = " + node);
        if (target != null) {
            return null;
        }

        target = (Node) event.getTarget();
        node = getDockableParent(target);
        released = false;
        pressed = true;
        return null;

        //return initial.dispatchEvent(event, tail);
    }

    protected Event released(Event event, EventDispatchChain tail) {
        node = null;
        target = null;
        pressed = false;
        released = true;
        dragged = false;
        dragDetected = false;
        return null;

    }

    protected Event clicked(Event event, EventDispatchChain tail) {
        System.err.println("Scene CLICKED ev.source = " + event.getSource());
        System.err.println("Scene CLICKED ev.target = " + event.getTarget());
        return pressed(event, tail);
    }

    protected Event dragDetected(Event event, EventDispatchChain tail) {
            System.err.println("dragDetected dispatch source " + event.getSource());
            System.err.println("dragDetected dispatch target " + event.getTarget());
        if (dragDetected) {
            return null;
        }
        if (target != event.getTarget()) {
            return null;
        }

        dragDetected = true;
        return null;
    }

    protected Event dragged(Event event, EventDispatchChain tail) {

        if (target == null || !dragDetected) {
            return null;
        }

        return null;
    }
    protected Event mouseEnteredOrExited(Event event, EventDispatchChain tail) {
        return null;
    }

    @Override
    public void finish(Node node) {
        node.setEventDispatcher(initial);
    }

    public void finish(Scene scene) {
        scene.setEventDispatcher(initial);
    }

}
