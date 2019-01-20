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

import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.stage.Window;
//import static org.vns.javafx.dock.api.dragging.view.NodeResizer.windowBounds;

/**
 *
 * @author Valery
 */
public class EllipseResizer extends AbstractResizer {

    public EllipseResizer(Window window, Node node) {
        super(window, node);
    }

    @Override
    protected void setSize() {
    }

    @Override
    protected void setXLayout(double wDelta, double xDelta, double curX) {
        Ellipse node = (Ellipse) getNode();
        node.setRadiusX(wDelta / 2 + node.getRadiusX());
        node.autosize();
        mouseXProperty().set(curX);
   }

    @Override
    protected void setYLayout(double hDelta, double yDelta, double curY) {

        Ellipse node = (Ellipse) getNode();
        node.setRadiusY(hDelta / 2 + node.getRadiusY());
        node.autosize();        
        mouseYProperty().set(curY);
    }
}
