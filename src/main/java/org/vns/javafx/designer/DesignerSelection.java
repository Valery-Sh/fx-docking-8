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
import org.vns.javafx.dock.api.selection.ObjectFramingProvider;
import org.vns.javafx.scene.control.editors.PropertyEditorPane;

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
    }

    public boolean isSelectable(Object value) {
        boolean retval = false;
        if (value != null && (value instanceof Node)) {
            return true;
        }
        TreeItemEx item = SceneViewUtil.findTreeItemByObject(value);
        if (null != item.getItemType()) {
            switch (item.getItemType()) {
                case LIST: {
                    
/*                    TreeItemEx parentItem = (TreeItemEx) item.getParent();
                    if (parentItem.getValue() != null && (parentItem.getValue() instanceof Node)) {
                        Node node = (Node) parentItem.getValue();
                        if (DockLayout.test(node)) {
                            ObjectFramingProvider p = DockLayout.of(node).getLayoutContext().getLookup().lookup(ObjectFramingProvider.class);
                            System.err.println("ObjectFramingProvider p = " + p);
                            if (p != null) {
                                if (p.getInstance(item.getPropertyName()) != null) {
                                    retval = true;
                                }
                            }
                        }
                    }
*/
                    break;
                }

                case ELEMENT: {
                    TreeItemEx parentItem = (TreeItemEx) item.getParent();
                    if (parentItem.getItemType() == TreeItemEx.ItemType.LIST) {
                        String propertyName = parentItem.getPropertyName();
                        parentItem = (TreeItemEx) parentItem.getParent();
                        if (parentItem != null && parentItem.getValue() != null && (parentItem.getValue() instanceof Node)) {
                            Node node = (Node) parentItem.getValue();
                            if (DockLayout.test(node)) {
                                ObjectFramingProvider p = DockLayout.of(node).getLayoutContext().getLookup().lookup(ObjectFramingProvider.class);
                                if (p != null) {
                                    if (p.getInstance(propertyName) != null) {
                                        retval = true;
                                    }
                                }
                            }
                        }
                    }
                    break;
                }
                case CONTENT: {
                    String propertyName = item.getPropertyName();
                    TreeItemEx parentItem = (TreeItemEx) item.getParent();
                    if (parentItem != null && parentItem.getValue() != null && (parentItem.getValue() instanceof Node)) {
                        Node node = (Node) parentItem.getValue();
                        if (DockLayout.test(node)) {
                            ObjectFramingProvider p = DockLayout.of(node).getLayoutContext().getLookup().lookup(ObjectFramingProvider.class);
                            if (p != null) {
                                if (p.getInstance(propertyName) != null) {
                                    retval = true;
                                }
                            }
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }

        return retval;
    }

    @Override
    protected boolean showObjectFraming(Object value) {
        boolean retval = false;

        TreeItemEx item = SceneViewUtil.findTreeItemByObject(value);
        if (null != item.getItemType()) {
            switch (item.getItemType()) {
                case LIST: {
                    TreeItemEx parentItem = (TreeItemEx) item.getParent();
                    if (parentItem.getValue() != null && (parentItem.getValue() instanceof Node)) {
                        Node node = (Node) parentItem.getValue();
                        if (DockLayout.test(node)) {
                            ObjectFramingProvider p = DockLayout.of(node).getLayoutContext().getLookup().lookup(ObjectFramingProvider.class);
                            if (p != null) {
                                setObjectFraming(p.getInstance(item.getPropertyName()));
                                if (getObjectFraming() != null) {
                                    getObjectFraming().show(item.getPropertyName());
                                    retval = true;
                                }
                            }
                        }
                    }
                    break;
                }
                case ELEMENT: {
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
                                        retval = true;
                                    }
                                }
                            }
                        }
                    }
                    break;
                }
                case CONTENT: {
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
                                    retval = true;
                                }
                            }
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
        return retval;
    }

    @Override
    public void notifySelected(Object value) {
        PropertyEditorPane editorPane = DesignerLookup.lookup(PropertyEditorPane.class);
        if (editorPane != null && ( value == null || ! isSelectable(value))) {
            editorPane.setBean(null);
        } else if (editorPane != null ) {
            editorPane.setBean(value);
        }
        if (value == null) {
            return;
        }

        SceneView sgv = DesignerLookup.lookup(SceneView.class);
        if (sgv != null && sgv.getTreeView().getRoot() != null) {
            TreeItemEx item;
            if (sgv.getTreeView().getRoot().getValue() == value) {
                item = (TreeItemEx) sgv.getTreeView().getRoot();
            } else {
                item = SceneViewUtil.findTreeItemByObject(sgv.getTreeView(), value);
            }
            if (item != null) {
                sgv.getTreeView().getSelectionModel().select(item);
            }
        }
    }
}
