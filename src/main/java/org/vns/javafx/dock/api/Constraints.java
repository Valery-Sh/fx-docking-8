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
package org.vns.javafx.dock.api;

import java.util.Map;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.NumberExpressionBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.MapChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Node;

/**
 *
 * @author Valery Shyshkin
 */
public class Constraints {

    public static final String PROPERTY_NAME = "static-constraints";
    private final ObservableMap<String, Property> namedValues = FXCollections.observableHashMap();
    private final ObservableMap<String, String> keyMap = FXCollections.observableHashMap();
    
    private String[] propertyNames;
            
    private final Node node;
    protected boolean updateProperties;
    protected boolean updateProperty;
    //Change<? extends Object, ? extends Object> change
    private MapChangeListener<? super Object, ? super Object>  propertiesChangeListener = ( Change<? extends Object, ? extends Object> change) -> {
        this.propertiesChanged(change);
    };
    
    public Constraints(Node node) {
        this.node = node;
        init();
    }

    private void init() {
        updateProperties = false;
        updateProperty = false;
        
    }
    
    public String getTitle() {
        return null;
    }
    
    public Property getProperty(String name) {
        return namedValues.get(name);
    }
    public Object getPropertyValue(String name) {
        if ( namedValues.get(name) == null ) {
            return null;
        }
        return namedValues.get(name).getValue();
    }    
    public void delete() {
        node.getProperties().removeListener(propertiesChangeListener);
        node.getProperties().remove(PROPERTY_NAME);
        namedValues.forEach((k, v) -> {
            if (v instanceof ConstraintProperty) {
                ((ConstraintProperty) v).removeListeners();
            }
        });
        
    }

    public void addListener(final String propName) {
        Property prop = namedValues.get(propName);
        prop.addListener((v, ov, nv) -> {
            if (updateProperty) {
                return;
            }
            try {
                updateProperty = true;
                String key = null;
                for (Map.Entry<String, String> e : keyMap.entrySet()) {
                    if (propName.equals(e.getValue())) {
                        key = e.getKey();
                        break;
                    }
                }
                if (nv == null) {
                    node.getProperties().remove(key);
                } else {
                    node.getProperties().put(key, nv);
                }
            } finally {
                updateProperty = false;
            }
        });
    }
    /**
     * Returns property names ordered for visual representation.
     * @return property names ordered for visual representation.
     */
    public String[] getPropertyNames() {
        return propertyNames;
    }

    protected void create(String[] propertiesKeys, String[] propertyNames, Property[] values) {
        this.propertyNames = propertyNames;
        for (int i = 0; i < propertiesKeys.length; i++) {
            keyMap.put(propertiesKeys[i], propertyNames[i]);
            namedValues.put(propertyNames[i], values[i]);
        }

        keyMap.forEach((k, v) -> {
            Object value = node.getProperties().get(k);
            if (value == null) {
                if (getNamedValues().get(v) instanceof NumberExpressionBase) {
                    getNamedValues().get(v).setValue(0);
                }
                if (getNamedValues().get(v) instanceof BooleanExpression) {
                    getNamedValues().get(v).setValue(false);
                } else {
                    getNamedValues().get(v).setValue(null);
                }
            } else {
                getNamedValues().get(v).setValue(value);
            }
        });
        node.getProperties().addListener(propertiesChangeListener);
        namedValues.forEach((k, v) -> {
            addListener(k);
        });

    }

    /**
     * Returns a map collection which contains key/value pairs where key is a
     * property name and a value is an object of type {@code ReadOnlyProperty}.
     *
     * The key of the element in the map specifies the string value of the key
     * in the property of the constraint . The value of the element in the map
     * specifies the value of the property of the constraint.
     *
     *
     * @return a map collection which contains key/value pairs where key is a
     * property name and a value is an object of type {@code ReadOnlyProperty}.
     */
    public ObservableMap<String, Property> getNamedValues() {
        return namedValues;
    }

    /**
     * Returns a map collection which contains key/value pairs where key is a
     * property name in the node's {@code properties} and a value is a
     * corresponding property name.
     *
     * @return a map collection which contains key/value pairs where key is a
     * property name in the node's {@code properties} and a value is a
     * corresponding property name.
     */
    public ObservableMap<String, String> getKeyMap() {
        return keyMap;
    }

    public void propertiesChanged(Change<? extends Object, ? extends Object> change) {
        if (updateProperties) {
            return;
        }

        try {
            updateProperties = true;
            if (change.wasAdded() && (change.getKey() instanceof String) && getKeyMap().containsKey(change.getKey())) {
                String key = (String) change.getKey();
                String propName = getKeyMap().get(key);

                if (change.getValueAdded() == null) {
                    // usally cannot be
                    if (getNamedValues().get(propName) instanceof NumberExpressionBase) {
                        getNamedValues().get(propName).setValue(0);
                    } else if (getNamedValues().get(propName) instanceof BooleanExpression) {
                        getNamedValues().get(propName).setValue(false);
                    } else {
                        getNamedValues().get(propName).setValue(null);
                    }
                } else {
                    getNamedValues().get(propName).setValue(change.getValueAdded());
                }
            } else if (change.wasRemoved() && (change.getKey() instanceof String) && getKeyMap().containsKey(change.getKey())) {
                String key = (String) change.getKey();
                String propName = getKeyMap().get(key);

                if (getNamedValues().get(propName) instanceof NumberExpressionBase) {
                    getNamedValues().get(propName).setValue(0);
                } else if (getNamedValues().get(propName) instanceof BooleanExpression) {
                    getNamedValues().get(propName).setValue(false);
                } else {
                    getNamedValues().get(propName).setValue(null);
                }
            }
        } finally {
            updateProperties = false;
        }
    }

    public String toString(Map map) {
        StringBuilder sb = new StringBuilder();
        sb.append(node.getClass().getSimpleName())
                .append(": ");
        if (map == node.getProperties()) {
            sb.append("properties: ");
        } else if (map == namedValues) {
            sb.append("constraints: ");
        }
        if (map == node.getProperties()) {
            node.getProperties().forEach((k, v) -> {
                if (keyMap.containsKey(k)) {
                    sb.append('[')
                            .append((String) k)
                            .append(':')
                            .append(v == null ? "null" : v.toString())
                            .append(']')
                            .append(',');
                }

            });
        } else if ( map == namedValues ) {
            namedValues.forEach((k, v) -> {
                if (namedValues.containsKey(k)) {
                    sb.append('[')
                            .append((String) k)
                            .append(':')
                            .append(v.getValue() == null ? "null" : v.getValue().toString())
                            .append(']')
                            .append(',');
                }

            });
            
        }

        return sb.toString();
    }

    public static class ConstraintProperty<T> extends SimpleObjectProperty<T> {

        private ObservableList<ChangeListener<? super T>> changeListeners = FXCollections.observableArrayList();
        private ObservableList<InvalidationListener> invalidationListeners = FXCollections.observableArrayList();

        public ConstraintProperty() {
        }

        public ConstraintProperty(T initialValue) {
            super(initialValue);
        }

        public ConstraintProperty(Object bean, String name) {
            super(bean, name);
        }

        public ConstraintProperty(Object bean, String name, T initialValue) {
            super(bean, name, initialValue);

        }

        @Override
        public void addListener(ChangeListener<? super T> listener) {
            if (changeListeners.contains(listener)) {
                return;
            }
            changeListeners.add(listener);
            super.addListener(listener);
        }

        @Override
        public void addListener(InvalidationListener listener) {
            if (invalidationListeners.contains(listener)) {
                return;
            }
            invalidationListeners.add(listener);
            super.addListener(listener);
        }

        @Override
        public void removeListener(ChangeListener<? super T> listener) {
            changeListeners.remove(listener);
            super.removeListener(listener);
        }

        @Override
        public void removeListener(InvalidationListener listener) {
            invalidationListeners.remove(listener);
            super.removeListener(listener);
        }

        public void removeListeners() {
            changeListeners.forEach(l -> super.removeListener(l));
            changeListeners.clear();
            invalidationListeners.forEach(l -> super.removeListener(l));
            invalidationListeners.clear();

        }
    }

    public static class GridPaneConstraints extends Constraints {

        public GridPaneConstraints(Node node) {
            super(node);
            /**
             * A list of properties as they are may be saved in the GridPane's
             * getProperties() collection.
             */
            String[] propertiesKeys = new String[]{"gridpane-row", "gridpane-column",
                "gridpane-row-span", "gridpane-column-span",
                "gridpane-halignment", "gridpane-valignment",
                "gridpane-hgrow", "gridpane-vgrow",
                "gridpane-fill-width", "gridpane-fill-height",
                "gridpane-margin"

            };
            /**
             * A list of names of static properties of the GridPane.
             */            
            String[] propertyNames = new String[]{
                "rowIndex", "columnIndex",
                "rowSpan", "columnSpan",
                "hgrow", "vgrow",
                "halignment", "valignment",
                "fillWidth", "fillHeight",
                "margin"

            };
            ObjectProperty<Integer> rowIndex = new ConstraintProperty<>();
            ObjectProperty<Integer> columnIndex = new ConstraintProperty<>();
            ObjectProperty<Integer> rowSpan = new ConstraintProperty<>();
            ObjectProperty<Integer> columnSpan = new ConstraintProperty<>();

            Property[] values = new Property[]{
                rowIndex, columnIndex,
                rowSpan, columnSpan,
                new ConstraintProperty<>(), new ConstraintProperty<>(),
                new ConstraintProperty<>(), new ConstraintProperty<>(),
                new ConstraintProperty<>(), new ConstraintProperty<>(),
                new ConstraintProperty<>()

            };

            create(propertiesKeys, propertyNames, values);
        }
        @Override
        public String getTitle() {
            int r = getPropertyValue("rowIndex") == null ? 0 : (Integer)getPropertyValue("rowIndex");
            int c = getPropertyValue("columnIndex") == null ? 0 : (Integer)getPropertyValue("columnIndex");
            return "(" + r + "," + c + ")";
        }
   
    }//class GridPaneConstraint
}//class Constraint
