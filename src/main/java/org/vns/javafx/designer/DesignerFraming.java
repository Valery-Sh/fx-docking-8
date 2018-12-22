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
package org.vns.javafx.designer;

import org.vns.javafx.dock.api.dragging.view.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.dock.api.Selection;

/**
 *
 * @author Olga
 */
public class DesignerFraming extends AbstractNodeFraming {

    @Override
    protected void initializeOnShow(Node node) {

        SceneView sv = DesignerLookup.lookup(SceneView.class);
        if (sv == null || sv.getRoot().getScene() != node.getScene()) {
            return;
        }
        Parent p = EditorUtil.getTopParentOf(node);
        //System.err.println("initializeOnShow node = " + node);
        //System.err.println("initializeOnShow p = " + p);
        if (p != null && node != sv.getRoot()) {
            if (node.getParent() != null && node.getParent() != sv.getRoot()) {
                System.err.println("node.getParent() = " + node.getParent());
                /*                if (node.getParent() instanceof GridPane) {
                    System.err.println("node.getParent() = " + node.getParent());
                    System.err.println("node.getScene().getRoot() = " + node.getScene().getRoot());
                    GridPaneFrame pf = new GridPaneFrame((GridPane) node.getParent());
                    ((Pane)node.getScene().getRoot()).getChildren().add(pf);
                    
                    pf.show();
                    pf.toFront();
                } else {
                    FramePane parentPane = SceneView.getParentFrame();
                    parentPane.setBoundNode(node.getParent());
                }
                 */
                FramePane parentPane = SceneView.getParentFrame();
                parentPane.setBoundNode(node.getParent());

            }
            FramePane resizePane = SceneView.getResizeFrame();
            resizePane.setBoundNode(node);
        }

        Selection sel = DockRegistry.lookup(Selection.class);
        if (sel != null) {
            sel.notifySelected(node);
        }
    }

    @Override
    public void showParent(Node node, Object... parms) {
        SceneView sv = DesignerLookup.lookup(SceneView.class);
        Parent p = EditorUtil.getTopParentOf(node);

        if (p != null && node != sv.getRoot()) {
            if (node.getParent() != null && node.getParent() != sv.getRoot()) {
                FramePane parentPane = SceneView.getParentFrame();
                parentPane.setBoundNode(node.getParent());
            }
        }
    }

    @Override
    protected void finalizeOnHide(Node node) {
        SceneView sv = DesignerLookup.lookup(SceneView.class);
        if (sv == null || sv.getRoot().getScene() != node.getScene()) {
            return;
        }
        Parent p = EditorUtil.getTopParentOf(node);
        if (p != null) {
            FramePane resizePane = SceneView.getResizeFrame();
            if (resizePane != null) {
                resizePane.setBoundNode(null);
            }
            FramePane parentPane = SceneView.getParentFrame();
            if (parentPane != null) {
                parentPane.setBoundNode(null);
            }
        }

    }
}
