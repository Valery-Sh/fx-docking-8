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

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.layout.GridPane;
import org.vns.javafx.JdkUtil;
import org.vns.javafx.dock.api.dragging.view.DividerLine;

/**
 *
 * @author Valery
 */
public class GridCellChageTask2 extends Task<ObjectProperty<CellBounds>> {

    private GridPaneConstraintsDividers dividers;
    final ObjectProperty<CellBounds> result = new SimpleObjectProperty<>();

    public GridCellChageTask2(GridPaneConstraintsDividers dividers) {
        this.dividers = dividers;
    }

    @Override
    protected ObjectProperty<CellBounds> call() throws Exception {

        updateValue(result);
        Platform.runLater(() -> addListeners(this));
        while (true) {
            if (this.isCancelled()) {
                break;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                if (this.isCancelled()) {
                    break;
                }
            }
            CellBounds bounds = new CellBounds();
            for (int i = 0; i < dividers.getRowDividers().size(); i++) {
                Bounds cellBounds = JdkUtil.getGridCellBounds(dividers.getGridPane(), 0, i);
                bounds.getRowBounds().add(cellBounds);
            }
            for (int j = 0; j < dividers.getColumnDividers().size(); j++) {
                Bounds cellBounds = JdkUtil.getGridCellBounds(dividers.getGridPane(), j, 0);
                bounds.getColumnBounds().add(cellBounds);
            }
            result.set(bounds);


        }//while
        return null;
    }

    public void addListeners(Task<ObjectProperty<CellBounds>> task) {
        task.valueProperty().getValue().addListener((v, ov, nv) -> {
            Platform.runLater(() -> {
                dividers.resizeRelocate(nv);
            });
        });
    }

}
