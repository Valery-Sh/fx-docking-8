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

import javafx.scene.layout.Region;
import javafx.stage.Window;

/**
 *
 * @author Valery
 */
public class NodeResizer extends AbstractResizer {

    public NodeResizer(Window window, Region node) {
        super(window,node);
    }
    @Override
    protected void setSize() {
        Region node = (Region) getNode();
        node.setPrefWidth(node.getWidth());
        node.setPrefHeight(node.getHeight());
    }    
    @Override
    protected void setXLayout(double wDelta, double xDelta, double curX) {
        Region node = (Region) getNode();
/*        System.err.println("setLayoutX: node.getWidth() = " + node.getWidth());
        System.err.println("setLayoutX: node.getHeight() = " + node.getHeight());
        System.err.println("setLayoutX: node.getPrefHeight() = " + node.getPrefHeight());
        System.err.println("setLayoutX: node.minWidth(-1) = " + node.minWidth(-1));
        System.err.println("setLayoutX: node.minWidth(height) = " + node.minWidth(node.getHeight()));
        System.err.println("setLayoutX: xDelta = " + xDelta);
        System.err.println("setLayoutX: wDelta = " + wDelta);
        System.err.println("setLayoutX: node.prefWidth() = " + node.getPrefWidth());
        
        System.err.println("=================================");
*/       
        if ((node.getWidth() > node.minWidth(node.getHeight()) || xDelta <= 0)) {
            //if ( wDelta != 0 ) {
            node.setPrefWidth(wDelta + node.getPrefWidth());
            node.autosize();
            //}
            mouseXProperty().set(curX);
        }

    }
    @Override
    protected void setYLayout(double hDelta, double yDelta, double curY) {
         Region node = (Region) getNode();
         if ((node.getHeight() > node.minHeight(node.getWidth()) || yDelta <= 0)) {
            node.setPrefHeight(hDelta + node.getPrefHeight());
            node.autosize();
            mouseYProperty().set(curY);
         }
    }    
}
