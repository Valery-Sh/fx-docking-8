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

import javafx.scene.Node;
import org.vns.javafx.dock.api.DockLayout;
import org.vns.javafx.dock.api.Selection;
import org.vns.javafx.dock.api.dragging.view.ObjectFramingProvider;

/**
 *
 * @author Valery
 */
public class DesignerSelection extends Selection {

    //private NodeResizer resizer;
    //private NodeFraming resizer;

    public DesignerSelection() {
        init();
    }

    private void init() {
        //selectedProperty().addListener(this::selectedChanged);
    }

/*    @Override
    public void setSelected(Object toSelect) {
        if (toSelect instanceof Node) {
            NodeFraming nf = DockRegistry.lookup(NodeFraming.class);
            if (nf != null) {
                nf.show((Node) toSelect);
            }
        }
        notifySelected(toSelect);
    }
*/
/*    protected void selectedChanged(ObservableValue ov, Object oldValue, Object newValue) {
        System.err.println("SELECTED CHANGED ov = " + oldValue + "; nv = " + newValue);
        NodeFraming nf = DockRegistry.lookup(NodeFraming.class);
        if (newValue == null) {
            if (nf != null) {
                nf.hide();
            }
            return;
        }

        if (nf != null) {
            nf.hide();
        }
        if (newValue instanceof Node) {
            nf.show((Node) newValue);
        }
        notifySelected(newValue);
    }
*/
    protected void showObjectFraming(Object value) {
        System.err.println("DESIGNER SELECTION value = " + value);
        TreeItemEx item = EditorUtil.findTreeItemByObject(value);
        if (item.getItemType() == TreeItemEx.ItemType.LIST) {
            TreeItemEx parentItem = (TreeItemEx) item.getParent();
            if (parentItem.getValue() != null && (parentItem.getValue() instanceof Node)) {
                Node node = (Node) parentItem.getValue();
                if (DockLayout.test(node)) {
                    ObjectFramingProvider p = DockLayout.of(node).getLayoutContext().getLookup().lookup(ObjectFramingProvider.class);
                    if (p != null) {
                        setObjectFraming(p.getInstance(item.getPropertyName()));
                        if (getObjectFraming() != null) {
                            getObjectFraming().show(item.getPropertyName());
                        }
                    }
                }
            }
        } else if (item.getItemType() == TreeItemEx.ItemType.ELEMENT) {
            TreeItemEx parentItem = (TreeItemEx) item.getParent();
            if (parentItem.getItemType() == TreeItemEx.ItemType.LIST) {
                String propertyName = parentItem.getPropertyName();
                parentItem = (TreeItemEx) parentItem.getParent();
                if (parentItem != null && parentItem.getValue() != null && (parentItem.getValue() instanceof Node)) {
                    Node node = (Node) parentItem.getValue();
                    if (DockLayout.test(node)) {
                        ObjectFramingProvider p = DockLayout.of(node).getLayoutContext().getLookup().lookup(ObjectFramingProvider.class);
                        if (p != null) {
                            setObjectFraming(p.getInstance(propertyName));
                            if (getObjectFraming() != null) {
                                getObjectFraming().show(item.getPropertyName(), value);
                            }
                        }
                    }
                }
            }
        } else if (item.getItemType() == TreeItemEx.ItemType.CONTENT) {
            String propertyName = item.getPropertyName();
            TreeItemEx parentItem = (TreeItemEx) item.getParent();
            if (parentItem != null && parentItem.getValue() != null && (parentItem.getValue() instanceof Node)) {
                Node node = (Node) parentItem.getValue();
                if (DockLayout.test(node)) {
                    ObjectFramingProvider p = DockLayout.of(node).getLayoutContext().getLookup().lookup(ObjectFramingProvider.class);
                    if (p != null) {
                        setObjectFraming(p.getInstance(propertyName));
                        if (getObjectFraming() != null) {
                            getObjectFraming().show(propertyName, value);
                        }
                    }
                }
            }
        }

    }
    
    @Override
    public void notifySelected(Object value) {
        SceneView sgv = DesignerLookup.lookup(SceneView.class);
        if (sgv != null) {
            TreeItemEx item;
            if (sgv.getTreeView().getRoot().getValue() == value) {
                item = (TreeItemEx) sgv.getTreeView().getRoot();
            } else {
                item = EditorUtil.findTreeItemByObject(sgv.getTreeView(), value);
            }
            if (item != null) {
                sgv.getTreeView().getSelectionModel().select(item);
            }
        }
    }
}
