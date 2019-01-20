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
package org.vns.javafx.dock.api.resizer;

import org.vns.javafx.dock.api.resizer.AbstractResizer;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.stage.Window;
//import static org.vns.javafx.dock.api.dragging.view.NodeResizer.windowBounds;

/**
 *
 * @author Valery
 */
public class RectangleResizer extends AbstractResizer {

    public RectangleResizer(Window window, Node node) {
        super(window, node);
    }

    @Override
    protected void setSize() {
        Rectangle node = (Rectangle) getNode();
        node.setWidth(node.getWidth());
        node.setHeight(node.getHeight());
    }

    @Override
    protected void setXLayout(double wDelta, double xDelta, double curX) {
        Rectangle node = (Rectangle) getNode();
        node.setWidth(wDelta + node.getWidth());
        node.autosize();        
        mouseXProperty().set(curX);
    }

    @Override
    protected void setYLayout(double hDelta, double yDelta, double curY) {

        Rectangle node = (Rectangle) getNode();
        node.setHeight(hDelta / 2 + node.getHeight());
        node.autosize();        
        mouseYProperty().set(curY);
    }
}
