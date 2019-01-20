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

import org.vns.javafx.dock.api.selection.WindowNodeFraming;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;

/**
 *
 * @author Valery
 */
public interface Resizer {
    boolean isStarted();
    void resize(double x, double y);
    void start(MouseEvent ev, Window window, Cursor cursor, Cursor... supportedCursors);
    void start(MouseEvent ev, WindowNodeFraming resizer, Cursor cursor, Cursor... supportedCursors);
    
    public static class DefaultResizer implements Resizer {

        @Override
        public boolean isStarted() {
            return true;
        }

        @Override
        public void resize(double x, double y) {
            
        }

        @Override
        public void start(MouseEvent ev, Window window, Cursor cursor, Cursor... supportedCursors) {

        }

        @Override
        public void start(MouseEvent ev, WindowNodeFraming resizer, Cursor cursor, Cursor... supportedCursors) {
        }
        
    }
}
