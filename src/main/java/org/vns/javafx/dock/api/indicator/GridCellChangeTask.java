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
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.geometry.Bounds;
import org.vns.javafx.JdkUtil;

/**
 *
 * @author Valery
 */
public class GridCellChangeTask extends Task<ObjectProperty<Bounds>> {

    private GridPaneConstraintsDividers dividers;
    final ObjectProperty<Bounds> result = new SimpleObjectProperty<>();

    public GridCellChangeTask(GridPaneConstraintsDividers dividers) {
        this.dividers = dividers;
    }

    @Override
    protected ObjectProperty<Bounds> call() throws Exception {

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
            try {
                boolean changed = false;
                for (int i = 0; i < dividers.getColumnDividers().size(); i++) {

                    Bounds cellBounds = JdkUtil.getGridCellBounds(dividers.getGridPane(), i, 0);
                    //Bounds b = new BoundingBox(cellBounds.getMinX(), cellBounds.getMinY(), cellBounds.getWidth(), cellBounds.getHeight());
                    result.set(cellBounds);
                    //res = b;
                    changed = true;
                }
                //System.err.println("TASK 1");
                if (changed) {
                    //continue;
                }
                //System.err.println("TASK 2");
                for (int i = 0; i < dividers.getRowDividers().size(); i++) {

                    Bounds cellBounds = JdkUtil.getGridCellBounds(dividers.getGridPane(), 0, i);
                    //System.err.println("TASK bounds = " + cellBounds);
                    //Bounds b = new BoundingBox(cellBounds.getMinX(), cellBounds.getMinY(), cellBounds.getWidth(), cellBounds.getHeight());

//                if (res ==null  || res != null && (b.getMinY() != res.getMinY() || b.getMaxY() != res.getMaxY())) {
                    result.set(cellBounds);
                    //res = b;
//                }
                }
            } catch (Exception ex) {
                //System.err.println("Service exception msg = " + ex.getMessage());
            }
        }//while
        Platform.runLater(() -> removeListeners(this));
        return null;
    }
    private ChangeListener<? super Bounds> listener = (v, ov, nv) -> {
        Platform.runLater(() -> {
            dividers.resizeRelocate(nv);
            dividers.getGridCellBoundsObservables().forEach( o -> {
                o.resizeRelocate(nv);
            });
        });
    };

    public void removeListeners(Task<ObjectProperty<Bounds>> task) {
        task.valueProperty().getValue().removeListener(listener);
    }

    public void addListeners(Task<ObjectProperty<Bounds>> task) {
        task.valueProperty().getValue().addListener(listener);
    }

}
