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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Window;
//import static org.vns.javafx.dock.api.dragging.view.NodeResizer.windowBounds;

/**
 *
 * @author Valery
 */
public class TextResizer extends AbstractResizer {

    public TextResizer(Window window, Node node) {
        super(window, node);
    }

    @Override
    protected void setSize() {
        Text node = (Text) getNode();
        node.setWrappingWidth(node.localToScreen(node.getBoundsInLocal()).getWidth());
    }

    @Override
    protected void setXLayout(double wDelta, double xDelta, double curX) {
        Text node = (Text) getNode();
        if ((node.getWrappingWidth() >= node.minWidth(-1) || xDelta <= 0)) {
            node.setWrappingWidth(wDelta + node.getWrappingWidth());
            node.autosize();            
            mouseXProperty().set(curX);
        }

    }

    @Override
    protected void setYLayout(double hDelta, double yDelta, double curY) {
    }
}
