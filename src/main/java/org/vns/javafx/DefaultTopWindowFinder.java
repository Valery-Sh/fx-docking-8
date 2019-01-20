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

import java.util.ArrayList;
import java.util.List;
import javafx.stage.Stage;
import javafx.stage.Window;
import static org.vns.javafx.DefaultTopWindowFinder.getInstance;
import org.vns.javafx.dock.api.Util;

/**
 *
 * @author Valery Shyshkin
 */
public class DefaultTopWindowFinder extends AbstractTopWindowFinder {

    public void add(Window win) {
        if (win instanceof Stage) {
            return;
        }
        getWindows().add(win);
    }

    public void remove(Window win) {
        if (win instanceof Stage) {
            return;
        }
        getWindows().remove(win);
    }

    public static DefaultTopWindowFinder getInstance() {
        return Singleton.INSTANCE;
    }

    public static void printOwnerTree() {
        List<String> list = new ArrayList<>();
        WindowWrapper tree = getInstance().getOwnerTree();
        for (WindowWrapper wr : tree.getChildren()) {
            list.add(wr.toString());
//            System.err.println("******** wr = " + wr);
            add(list.size(), list, wr, 1);
        }

        System.err.println("=============================================== size = " + list.size());
        list.forEach(s -> {
            System.err.println(s);
        });
        System.err.println("===============================================");
    }

    private static void add(int idx, List<String> list, WindowWrapper wrapper, int level) {
        String space = Util.repeatString(" ", level);
        for (WindowWrapper wr : wrapper.getChildren()) {
            list.add(idx, space + wr.toString());
            add(idx + 1, list, wr, level + 1);
        }
    }

    private static class Singleton {

        private static final DefaultTopWindowFinder INSTANCE = new DefaultTopWindowFinder();
    }

}
