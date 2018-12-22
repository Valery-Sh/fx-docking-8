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
package org.vns.javafx.dock.api.indicator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;

/**
 *
 * @author Valery
 */
public class CellBounds {
    //private Orientation orientation;
    private final ObservableList<Bounds> rowBounds = FXCollections.observableArrayList();
    private final ObservableList<Bounds> columnBounds = FXCollections.observableArrayList();


    public ObservableList<Bounds> getRowBounds() {
        return rowBounds;
    }

    public ObservableList<Bounds> getColumnBounds() {
        return columnBounds;
    }
    
}
