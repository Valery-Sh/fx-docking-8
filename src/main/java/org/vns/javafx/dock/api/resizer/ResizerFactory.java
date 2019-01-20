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
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Window;

/**
 *
 * @author Valery
 */
public abstract class ResizerFactory  {
    
    public abstract Resizer getResizer(Window window,Node node);
    
    public static class NodeResizerFactory extends ResizerFactory {

        @Override
        public Resizer getResizer(Window window,Node node) {
            if ( node instanceof Circle) {
                return new CircleResizer(window, node);
            }
            if ( node instanceof Rectangle) {
                return new RectangleResizer(window, node);
            }
            if ( node instanceof Ellipse) {
                return new EllipseResizer(window, node);
            }
            if ( node instanceof Text) {
                return new TextResizer(window, node);
            }
            
            if ( !(node instanceof Region)) {
                return null;
            }
            return new NodeResizer(window, (Region) node);
        }
        
    } 
}
