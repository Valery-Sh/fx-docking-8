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
package org.vns.javafx;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

/**
 *
 * @author Valery
 */
public class JdkUtil {
    public static Bounds getGridCellBounds(GridPane grid, int columnIndex, int rowIndex) {
        int[] dim = getGridDimensions(grid);
        if ( dim[0] < 0 || dim[1] < 0 || columnIndex >= dim[0] || rowIndex >= dim[1]) {
            return null;
        }
        Bounds retval = null;
        try {
            retval =  grid.impl_getCellBounds(columnIndex, rowIndex);    
        } finally {
            return retval;
        }
        
    }
    public static int[] getGridDimensions(GridPane grid) {
        int[] d = new int[2];
        int maxRow = -1;
        int maxColumn = -1;
        for ( Node node : grid.getChildren()) {
            if ( GridPane.getColumnIndex(node) != null && GridPane.getColumnIndex(node) > maxColumn) {
                maxColumn = GridPane.getColumnIndex(node);
            }
            if ( GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > maxRow) {
                maxRow = GridPane.getRowIndex(node);
            }
        }
        if ( grid.getColumnConstraints().size() - 1 > maxColumn ) {
            maxColumn = grid.getColumnConstraints().size() - 1;
        }
        if ( grid.getRowConstraints().size() - 1 > maxRow ) {
            maxRow = grid.getRowConstraints().size() - 1;
        }
        
        d[0] = maxColumn == -1 ? -1 : maxColumn + 1;
        d[1] = maxRow == -1 ? -1 : maxRow + 1;
        
        
//        System.err.println("maxColumn = " + d[0]);
//        System.err.println("maxRow = " + d[1]);
        return d;
    }
    
}
