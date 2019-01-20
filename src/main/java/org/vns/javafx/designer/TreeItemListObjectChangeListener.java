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

import java.util.ArrayList;
import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import org.vns.javafx.dock.api.DockRegistry;
import org.vns.javafx.designer.TreeItemEx.ItemType;
import org.vns.javafx.dock.api.Util;
import org.vns.javafx.dock.api.Selection;
import org.vns.javafx.dock.api.selection.SelectionFrame;
import org.vns.javafx.dock.api.selection.NodeFraming;

/**
 *
 * @author Valery
 */
public class TreeItemListObjectChangeListener implements ListChangeListener {

    private final TreeItemEx treeItem;
    //private final String propertyName;

    public TreeItemListObjectChangeListener(TreeItemEx treeItem, String propertyName) {
        this.treeItem = treeItem;
        //this.propertyName = propertyName;
    }

    @Override
    public void onChanged(Change change) {
        while (change.next()) {

            if (change.wasRemoved()) {

                List list = change.getRemoved();
                /*                if (!list.isEmpty()) {
                    SaveRestore sr = DockRegistry.lookup(SaveRestore.class);
                    if (sr != null) {
                        //savasr.save(list.get(list.size() - 1), change.getTo());
                    }
                }
                 */
                for (Object elem : list) {
                    if ( Util.isForeign(elem) ) {
                        continue;
                    }
                    TreeItemEx toRemove = null;
                    for (TreeItem it : treeItem.getChildren()) {
                        if (((TreeItemEx) it).getItemType() == ItemType.LIST) {
                            for (TreeItem ith : ((TreeItemEx) it).getChildren()) {
                                if (((TreeItemEx) ith).getValue() == elem) {
                                    toRemove = (TreeItemEx) ith;
                                    it.getChildren().remove(toRemove);
                                    SceneView.reset(toRemove);
                                    return;
                                }
                            }
                        }
                        if (((TreeItemEx) it).getValue() == elem) {
                            toRemove = (TreeItemEx) it;
                            break;
                        }
                    }
                    //if ((elem instanceof Node) && ! SceneView.isFrame(elem)) {
                    if ((elem instanceof Node) && ! Util.isForeign(elem)) {
                        Selection sel = DockRegistry.lookup(Selection.class);
                        if (sel != null) {
                            sel.setSelected(null);
                        }
                    }
                    treeItem.getChildren().remove(toRemove);
                    SceneView.reset(toRemove);
                }

            }

            if (change.wasAdded()) {
                List list = change.getAddedSubList();
                List<TreeItemEx> itemList = new ArrayList();
                list.forEach(elem -> {
                    TreeItemEx it = new TreeItemBuilder().build(elem);
                    if (it != null) {
                        it.setExpanded(false);
                        itemList.add(it);
                    }
                });
                int idx = change.getFrom();
                for ( int i=0; i < change.getFrom(); i++ ) {
                    if ( Util.isForeign(change.getList().get(i)) ) {
                        idx--;
                    }
                }
                for ( TreeItemEx it  : itemList) {
                    if ( it.getValue() != null && Util.isForeign(it.getValue())) {
                        continue;
                    }
                    treeItem.getChildren().add(idx, it);
                    idx++;
                }
            }
        }//while
    }
}
