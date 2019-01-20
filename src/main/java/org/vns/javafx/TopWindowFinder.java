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

import javafx.stage.Window;

/**
 *
 * @author Valery
 */
public interface TopWindowFinder {
    public static final String WINDOW_WRAPPER = "window-wrapper-a3f6cb93-ac36-48ca-bb7a-7172095aed0c";
    void start();
    void dragDetected();
    void dropped();
    Window getTopWindow(double screenX, double screenY, Window... exclude);
    void add(Window win);
    void remove(Window win);
}
