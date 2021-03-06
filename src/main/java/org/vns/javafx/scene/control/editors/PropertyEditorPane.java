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
package org.vns.javafx.scene.control.editors;

import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.DefaultProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import static javafx.css.StyleOrigin.AUTHOR;
import static javafx.css.StyleOrigin.INLINE;
import javafx.css.StyleableProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import org.vns.javafx.MainLookup;
import org.vns.javafx.scene.control.editors.beans.BeanModel;
import org.vns.javafx.scene.control.editors.beans.Category;
import org.vns.javafx.scene.control.editors.beans.BeanProperty;
import org.vns.javafx.scene.control.editors.beans.PropertyPaneModelRegistry;
import org.vns.javafx.scene.control.editors.beans.PropertyPaneModelRegistry.Introspection;
import org.vns.javafx.scene.control.editors.beans.Section;

/**
 *
 * @author Valery
 */
@DefaultProperty("bean")
public class PropertyEditorPane extends Control {

    public static final String CATEGORY_BUTTON_ID_PREF = "toggle-";
    public static final String CATEGORY_SCROLLPANE_ID_PREF = "scrollpane-";
    public static final String STATUSBAR_ID = "statusbar";
    public static final String STATUSBAR_LABEL_ID = "statusbar-label";

    public static final String TOOLBAR_ID = "toolbar";

    private final ObjectProperty bean = new SimpleObjectProperty<>();

    private ObservableList<PropertyEditorPane> childEditorPanes = FXCollections.observableArrayList();

    //private final ObjectProperty<Node> compositeManager = new SimpleObjectProperty<>();
    private final ObjectProperty<Node> toolBar = new SimpleObjectProperty<>();
    private final ObjectProperty<Node> statusBar = new SimpleObjectProperty<>();

    private TilePane categoryButtonPane;

//    public WeakReference wr;
    private final ObjectProperty<ScrollPane.ScrollBarPolicy> scrollVBarPolicy = new SimpleObjectProperty<>(ScrollPane.ScrollBarPolicy.AS_NEEDED);

    private final TreePaneItem treePaneItem;

//    private VBox layout;
    private PropertyEditorPaneManager manager;

    public PropertyEditorPane() {
        this(null);
    }

    public PropertyEditorPane(TreePaneItem item) {
        this.treePaneItem = item;
        init();
    }

    private void init() {
        getStyleClass().add("property-editor-pane");

        if (treePaneItem != null) {
            getStyleClass().add("composite-property-editor-pane");
        }
        categoryButtonPane = new TilePane();
        ToolBar tb = new ToolBar();
        tb.setId(TOOLBAR_ID);
        tb.setVisible(false);
        setToolBar(tb);

        setStatusBar(createDefaultStatusBar());

        manager = new PropertyEditorPaneManager(this);
        //layout = manager.getLayout();
    }

    /*    protected VBox getLayout() {
        return layout;
    }
     */
    protected PropertyEditorPaneManager getManager() {
        return manager;
    }

    public TreePaneItem getTreePaneItem() {
        return treePaneItem;
    }

    public ObjectProperty beanProperty() {
        return bean;
    }

    public Object getBean() {
        return bean.get();
    }

    public void setBean(Object bean) {
        this.bean.set(bean);
    }

    public ObjectProperty<Node> toolBarProperty() {
        return toolBar;
    }

    public Node getToolBar() {
        return toolBar.get();
    }

    public void setToolBar(Node toolBar) {
        this.toolBar.set(toolBar);
    }

    public ObjectProperty<Node> statusBarProperty() {
        return statusBar;
    }

    public Node getStatusBar() {
        return statusBar.get();
    }

    public void setStatusBar(Node statusBar) {
        this.statusBar.set(statusBar);
    }

    private HBox createDefaultStatusBar() {
        Labeled lb = getBean() == null ? new Label()
                : new Label("Properties: " + getBean().getClass().getSimpleName());
        lb.setId(STATUSBAR_LABEL_ID);

        lb.setPadding(new Insets(4, 4, 4, 4));

        HBox hb = new HBox(lb);
        hb.setId(STATUSBAR_ID);
        hb.getStyleClass().add("status-bar");
        return hb;
    }

    private void updateStatusBar() {

        Labeled lb = getBean() == null ? new Label("No bean selected")
                : new Label("Properties: " + getBean().getClass().getSimpleName());
        lb.setId(STATUSBAR_LABEL_ID);

        lb.setPadding(new Insets(4, 4, 4, 4));

        HBox hb = new HBox(lb);
        hb.setId(STATUSBAR_ID);
        hb.getStyleClass().add("status-bar");
    }

    @Override
    public String getUserAgentStylesheet() {
        return PropertyEditor.class.getResource("resources/styles/styles.css").toExternalForm();
    }

    public Pane getCategoryButtonPane() {
        return categoryButtonPane;
    }

    @Override
    public Skin<?> createDefaultSkin() {
        return new PropertyEditorPaneSkin(this);
    }

    /**
     * Sets the policy for showing the vertical scroll bar.
     *
     * @param value the value of the scroll bar policy
     */
    public void setScrollPaneVbarPolicy(ScrollPane.ScrollBarPolicy value) {
        this.scrollVBarPolicy.set(value);
    }

    /**
     * Gets the value of the property vbarPolicy of the vertical scroll bar.
     *
     * @return the value of the property vbarPolicy of the vertical scroll bar.
     */
    public ScrollPane.ScrollBarPolicy getScrollPaneVbarPolicy() {
        return scrollVBarPolicy.get();
    }

    /**
     * Specifies the policy for showing the vertical scroll bar.
     *
     * @return value the value of the scroll bar policy
     */
    public ObjectProperty<ScrollPane.ScrollBarPolicy> scrollPaneVbarPolicy() {
        return scrollVBarPolicy;
    }

    public class PropertyEditorPaneSkin extends SkinBase<PropertyEditorPane> {

        private final VBox layout;

        public PropertyEditorPaneSkin(PropertyEditorPane control) {
            super(control);
            layout = control.getManager().getLayout();
            getChildren().add(layout);
        }

    }// Skin

    public static class PropertyEditorPaneManager {

        private final ObservableMap<String, Pane> categories = FXCollections.observableHashMap();
        private final ObservableMap<String, TitledPane> sections = FXCollections.observableHashMap();

        private final ObservableMap<String, PropertyEditor> editors = FXCollections.observableHashMap();

        private final ObservableMap<String, PropertyEditor> nodeEditors = FXCollections.observableHashMap();
        private final ObservableMap<String, PropertyEditor> regionEditors = FXCollections.observableHashMap();
        private final ObservableMap<String, PropertyEditor> shapeEditors = FXCollections.observableHashMap();

        private final ObservableMap<String, PropertyEditor> beanSpecificEditors = FXCollections.observableHashMap();
        private final ObservableMap<String, PropertyEditor> compositerEditors = FXCollections.observableHashMap();
        private final ObservableMap<String, StaticConstraintPropertyEditor> constraintsEditors = FXCollections.observableHashMap();

        private final ObservableMap<Class<?>, List<String>> layoutConstraints = FXCollections.observableHashMap();

        private final VBox layout;
        private VBox beanPane;

        private String lastSelectedToggleButtonId;
        //private VBox categoriesLayout;
        //private Pane categoryButtonPane;
        //private StackPane contentPane;
        //private ToggleGroup toggleGroup;

        //private final ObservableMap<Object, VBox> beanPanes = FXCollections.observableHashMap();
        private final Map<Property, ChangeListener> styleableListeners = FXCollections.observableHashMap(); // listenerMap.get(getSkinnable().getBean());

        private final ChangeListener<? extends Object> beanChangeListener = ((v, ov, nv) -> {
            beanChanged(v, ov, nv);
        });

        private PropertyEditorPane control;

        public PropertyEditorPaneManager(PropertyEditorPane control) {
            this.control = control;
            createLayoutConstraintMap();
            //beanPane = createBeanPane();
            layout = new VBox();
            if (control.getToolBar() != null) {
                layout.getChildren().add(control.getToolBar());
            }
            if (control.getStatusBar() != null) {
                layout.getChildren().add(control.getStatusBar());
            }
            //layout.getChildren().add(beanPane);
            layout.setSpacing(2);

            createPredefinedPropertyEditors();

            control.beanProperty().addListener(beanChangeListener);

            control.statusBarProperty().addListener((v, ov, nv) -> {
                if (ov != null) {
                    int idx = layout.getChildren().indexOf(ov);
                    if (nv == null) {
                        layout.getChildren().remove(idx);
                    } else {
                        layout.getChildren().set(idx, nv);
                    }
                } else if (nv != null) {
                    layout.getChildren().add(1, nv);
                }

            });
        }

        protected void createBeanPane() {
            beanPane = new VBox();
            //beanPane.setId("beanPane");

            VBox categoriesLayout = new VBox();
            beanPane.getChildren().add(categoriesLayout);
            //
            // initialize categoriesLayout (creadted only once)
            //
            //categoryButtonPane = control.getCategoryButtonPane();
            TilePane categoryButtonPane = new TilePane();
            categoriesLayout.getChildren().add(categoryButtonPane);
            StackPane contentPane = new StackPane();
            categoriesLayout.getChildren().add(contentPane);

            ToggleGroup toggleGroup = new ToggleGroup();

            toggleGroup.selectedToggleProperty().addListener((v, ov, nv) -> {
                //
                // We must set all ScrollPanes as invisible except selected 
                //
                lastSelectedToggleButtonId = null;
                if (ov != null) {
                    String id = "#" + CATEGORY_SCROLLPANE_ID_PREF + ((ToggleButton) ov).getId().substring(CATEGORY_BUTTON_ID_PREF.length());
                    contentPane.lookup(id).setVisible(false);
                    //contentPane.lookup(id).setVisible(false);
                }
                if (nv != null) {
                    String id = "#" + CATEGORY_SCROLLPANE_ID_PREF + ((ToggleButton) nv).getId().substring(CATEGORY_BUTTON_ID_PREF.length());
                    contentPane.lookup(id).setVisible(true);
                    lastSelectedToggleButtonId = ((ToggleButton) nv).getId();
                }
            });
            beanPane.getProperties().put("categoriesLayout", categoriesLayout);
            beanPane.getProperties().put("contentPane", contentPane);
            beanPane.getProperties().put("categoryButtonPane", categoryButtonPane);
            beanPane.getProperties().put("toggleGroup", toggleGroup);

        }

        protected VBox getLayout() {
            return layout;
        }

        protected void beanChanged(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
            if (oldValue != null) {
                editors.values().forEach(pe -> {
                    ((PropertyEditor) pe).unbind();
                });
                editors.clear();
                beanSpecificEditors.values().forEach(pe -> {
                    ((PropertyEditor) pe).unbind();
                });
                beanSpecificEditors.clear();

                compositerEditors.values().forEach(pe -> {
                    ((PropertyEditor) pe).unbind();
                });
                compositerEditors.clear();

                constraintsEditors.values().forEach(pe -> {
                    ((PropertyEditor) pe).unbind();
                });
                constraintsEditors.clear();

                nodeEditors.values().forEach(pe -> {
                    ((PropertyEditor) pe).unbind();
                });
                regionEditors.values().forEach(pe -> {
                    ((PropertyEditor) pe).unbind();
                });
                shapeEditors.values().forEach(pe -> {
                    ((PropertyEditor) pe).unbind();
                });

            }
            styleableListeners.forEach((k, v) -> {
                k.removeListener(v);
            });
            styleableListeners.clear();
            Label statusBarLabel = ((Label) layout.lookup("#" + STATUSBAR_LABEL_ID));
            if (statusBarLabel != null && newValue != null) {
                statusBarLabel.setText("Properties: " + newValue.getClass().getSimpleName());
            } else if (statusBarLabel != null) {
                statusBarLabel.setText("No Bean Selected");
            }
            if (oldValue != null && newValue == null) {
                layout.getChildren().remove(beanPane);
                beanPane = null;
                hide();
                return;
            }
            if (beanPane != null) {
                layout.getChildren().remove(beanPane);
            }
            //
            // creates but doesn't add to layout. The show() method adds 
            // the created beanPane to layout.
            //
            createBeanPane();

            if (newValue != null) {
                show();
            } else {
                hide();
            }
        }

        private void createLayoutConstraintMap() {
            ObservableList<BeanModel> beanModels = PropertyPaneModelRegistry.getPropertyPaneModel().getBeanModels();
            beanModels.forEach(model -> {
                for (Category cat : model.getItems()) {
                    if ("layout".equals(cat.getName())) {
                        for (Section sec : cat.getItems()) {
                            if ("constraint".equals(sec.getName())) {
                                List<String> names = FXCollections.observableArrayList();
                                sec.getItems().forEach(pi -> {
                                    if (pi.isConstraint()) {
                                        names.add(pi.getName());
                                    }
                                });
                                layoutConstraints.put(model.getBeanType(), names);
                            }
                        }
                    }
                }
            });
        }

        public void hide() {
            //toggleGroup.selectToggle(null);
            //categoryButtonPane.getChildren().clear();
        }

        public void show() {
            if (control.getBean() == null) {
                return;
            }

            StackPane contentPane = (StackPane) beanPane.getProperties().get("contentPane");
            TilePane categoryButtonPane = (TilePane) beanPane.getProperties().get("categoryButtonPane");
            ToggleGroup toggleGroup = (ToggleGroup) beanPane.getProperties().get("toggleGroup");

            toggleGroup.selectToggle(null);
            categoryButtonPane.getChildren().clear();
            contentPane.getChildren().clear();

            Introspection introspection = PropertyPaneModelRegistry.getInstance().introspect(control.getBean().getClass());

            BeanModel beanModel = PropertyPaneModelRegistry.getInstance().getBeanModel(control.getBean(), introspection);

            for (Category c : beanModel.getItems()) {
                VBox categoryContent = getCategoryPane(c.getName(), c.getDisplayName());
                if ("layout".equals(c.getName())) {
                    if ((control.getBean() instanceof Node) && ((Node) control.getBean()).getParent() != null) {

                        Class<?> parentClass = ((Node) control.getBean()).getParent().getClass();
                        List<String> list = layoutConstraints.get(parentClass);

                        if (list != null && !list.isEmpty()) {
                            TitledPane tp = getSectionPane(c.getName(), "constraint", parentClass.getSimpleName() + " Constraints");
                            categoryContent.getChildren().add(tp);

                            GridPane grid = (GridPane) ((StackPane) tp.getContent()).getChildren().get(0);
                            grid.getChildren().clear();

                            int i = 0;
                            for (String propName : list) {
                                StaticConstraintPropertyEditor editor = ConstraintPropertyEditorFactory.getDefault().getEditor(propName, parentClass);
                                if (editor != null) {
                                    constraintsEditors.put(propName, editor);
                                    editor.bindConstraint((Node) control.getBean());
                                    HyperlinkTitle title = editor.getTitle();
                                    grid.add(title, 0, i);
                                    grid.add((Node) editor, 1, i);
                                    i++;
                                }
                            }
                        }
                    }

                }

                for (Section s : c.getItems()) {
                    if ("layout".equals(c.getName()) && "constraint".equals(s.getName())) {
                        continue;
                    }

                    TitledPane tp = getSectionPane(c.getName(), s.getName(), s.getDisplayName());

                    categoryContent.getChildren().add(tp);

                    GridPane grid = (GridPane) ((StackPane) tp.getContent()).getChildren().get(0);
                    grid.getChildren().clear();
                    int i = 0;
                    for (BeanProperty propItem : s.getItems()) {
                        PropertyEditor editor = null;
                        if (control.getTreePaneItem() != null) {
                            //
                            // We deal with CompositeBeanModel
                            //
                            PropertyDescriptor pd = introspection.getPropertyDescriptors().get(propItem.getName());
                            if (pd != null && isComposite(pd.getPropertyType())) {
                                editor = CompositePropertyEditorFactory.getDefault().getEditor(propItem.getName(), pd.getPropertyType(), control.getBean().getClass());
                            }
                            if (editor != null) {
                                compositerEditors.put(propItem.getName(), editor);
                                TreePaneItem treePaneChild = (TreePaneItem) CompositePropertyEditorFactory.getDefault().getEditor(propItem.getName(), pd.getPropertyType(), control.getBean().getClass());
                                control.getTreePaneItem().getChildItems().add(treePaneChild);
                                ((TreePaneItem) editor).bindItems(treePaneChild);
                            }
                        }
                        if (editor == null) {
                            editor = nodeEditors.get(propItem.getName());
                            if (editor == null && (control.getBean() instanceof Shape)) {
                                editor = shapeEditors.get(propItem.getName());
                            }
                            if (editor == null && (control.getBean() instanceof Region)) {
                                editor = regionEditors.get(propItem.getName());
                            }

                        }
                        if (editor == null) {
                            Class[] propTypes = introspection.getPropTypes(propItem.getName());
                            if (propTypes == null) {
                                continue;
                            }
                            List<? extends BeanSpecificPropertyEditorFactory> list = MainLookup.lookupAll(BeanSpecificPropertyEditorFactory.class);
                            //PropertyEditor retval = null;
                            for (BeanSpecificPropertyEditorFactory f : list) {
                                editor = f.getEditor(control.getBean(), propItem.getName(), propTypes);
                                if (editor != null) {
                                    beanSpecificEditors.put(propItem.getName(), editor);
                                    break;
                                }
                            }
                            if (editor == null) {
                                editor = PropertyEditorFactory.getDefault().getEditor(propItem.getName(), propTypes);
                                editors.put(propItem.getName(), editor);
                            }
                        }
                        if (editor != null) {
                            HyperlinkTitle title = editor.getTitle();
                            Object value = null;

                            if ((editor instanceof ListPropertyEditor) && isObservableList(propItem.getName(), introspection)) {
                                PropertyDescriptor pd = introspection.getPropertyDescriptors().get(propItem.getName());
                                try {
                                    value = pd.getReadMethod().invoke(control.getBean(), new Object[0]);
                                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                                    Logger.getLogger(PropertyEditorPane.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            } else {
                                MethodDescriptor md = introspection.getMethodDescriptors().get(propItem.getName() + "Property");
                                try {
                                    value = md.getMethod().invoke(control.getBean(), new Object[0]);
                                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                                    Logger.getLogger(PropertyEditorPane.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                            if (value != null && isEditable(propItem, introspection)) {
                                if (value instanceof ObservableList) {
                                    ListProperty lp = new SimpleListProperty(control.getBean(), propItem.getName(), (ObservableList) value);
                                    editor.bindBidirectional(lp);
                                } else {
                                    editor.bindBidirectional((Property) value);
                                    modifyByStyleOrigin(propItem, (Property) value, editor, title, introspection);
                                }
                            } else if (value != null) {
                                if (value instanceof ObservableList) {
                                    ListProperty lp = new SimpleListProperty(control.getBean(), propItem.getName(), (ObservableList) value);
                                    editor.bind(lp);
                                } else {
                                    editor.bind((ReadOnlyProperty) value);
                                }

                            }

                            grid.add(title, 0, i);
                            grid.add((Node) editor, 1, i);
                            if ((editor instanceof TreePane)) {
                                StackPane sp = new StackPane();
                                grid.add(sp, 0, ++i);
                                GridPane.setColumnSpan(sp, 2);
                                ((TreePane) editor).setExternalValuePane(sp);
                            }
                        }
                        i++;
                    }
                }
            }

            contentPane.getChildren().forEach(node -> node.setVisible(false));

            if (toggleGroup.getToggles().size() > 0) {
                int sel = 0;
                if (lastSelectedToggleButtonId != null) {
                    for (Toggle t : toggleGroup.getToggles()) {
                        if (lastSelectedToggleButtonId.equals(((ToggleButton) t).getId())) {
                            sel = toggleGroup.getToggles().indexOf(t);
                            break;
                        }
                    }
                }
                toggleGroup.getToggles().get(sel).setSelected(true);
            }

            List<ScrollPane> list = FXCollections.observableArrayList();
            contentPane.getChildren().forEach(node -> {
                list.add((ScrollPane) node);
            });
            //beanPanes.put(control.getBean(), beanPane);
            //Platform.runLater(() -> {
            layout.getChildren().add(beanPane);
            beanModel.setBean(null);
            //});
        }

        /*        private Object getPropertyValue(String name, Object bean, Introspection introspection) {
            Object value = null;
            MethodDescriptor md = introspection.getMethodDescriptors().get(name + "Property");
            try {
                value = md.getMethod().invoke(bean, new Object[0]);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(PropertyEditorPane.class.getName()).log(Level.SEVERE, null, ex);
            }
            return value;
        }
         */
        /**
         * Return the pane for the specified category name.
         *
         * @param name the name of the category
         * @return the pane for the specified category name or null if not
         * found.
         */
        private VBox getCategoryPane(String name, String displayName) {
            VBox pane = (VBox) categories.get(name);

            StackPane contentPane = (StackPane) beanPane.getProperties().get("contentPane");
            TilePane categoryButtonPane = (TilePane) beanPane.getProperties().get("categoryButtonPane");
            ToggleGroup toggleGroup = (ToggleGroup) beanPane.getProperties().get("toggleGroup");
            if (pane == null) {
                if (displayName == null || displayName.trim().isEmpty()) {
                    displayName = EditorUtil.toDisplayName(name);
                }
                pane = new VBox();
                pane.setId(name);

                ScrollPane scrollPane = new ScrollPane(pane);
                scrollPane.setId(CATEGORY_SCROLLPANE_ID_PREF + name);
                scrollPane.setVbarPolicy(control.getScrollPaneVbarPolicy());
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scrollPane.setFitToWidth(true);

                contentPane.getChildren().add(scrollPane);
                
            }
            ToggleButton catBtn = new ToggleButton(displayName);
            catBtn.setId(CATEGORY_BUTTON_ID_PREF + name);
            toggleGroup.getToggles().add(catBtn);
            categoryButtonPane.getChildren().add(catBtn);

            pane.getChildren().clear();
            return pane;
        }

        protected void createPredefinedPropertyEditors() {
            List<String> names = PropertyPaneModelRegistry.getPropertyNames(Node.class);

            regionEditors.putAll(getPropertyEditors(new Region()));

            nodeEditors.putAll(regionEditors);

            Set<String> keys = new HashSet<>(nodeEditors.keySet());
            keys.forEach(key -> {
                if (!names.contains(key)) {
                    nodeEditors.remove(key);
                }
            });

            List<String> shapeNames = PropertyPaneModelRegistry.getPropertyNames(Shape.class);
            shapeEditors.putAll(getPropertyEditors(new Rectangle()));

            keys = new HashSet<>(shapeEditors.keySet());
            keys.forEach(key -> {
                if (!shapeNames.contains(key) || names.contains(key)) {
                    shapeEditors.remove(key);
                }
            });

            keys = new HashSet<>(regionEditors.keySet());
            keys.forEach(key -> {
                if (names.contains(key)) {
                    regionEditors.remove(key);
                }
            });
        }

        protected ObservableMap<String, PropertyEditor> getPropertyEditors(Node node) {

            ObservableMap<String, PropertyEditor> map = FXCollections.observableHashMap();

            Introspection introspection = PropertyPaneModelRegistry.getInstance().introspect(node.getClass());
            BeanModel beanModel = PropertyPaneModelRegistry.getInstance().getBeanModel(node, introspection);

            for (Category c : beanModel.getItems()) {

                /*                if ("layout".equals(c.getName())) {
                    if (node.getParent() != null) {

                        Class<?> parentClass = node.getParent().getClass();
                        List<String> list = layoutConstraints.get(parentClass);

                        if (list != null && !list.isEmpty()) {
                            for (String propName : list) {
                                StaticConstraintPropertyEditor editor = ConstraintPropertyEditorFactory.getDefault().getEditor(propName, parentClass);
                                if (editor != null) {
                                    //editor.bindConstraint((Node) control.getBean());
                                    //HyperlinkTitle title = editor.getTitle();
                                }
                            }
                        }
                    }
                }
                 */
                for (Section s : c.getItems()) {
                    if ("layout".equals(c.getName()) && "constraint".equals(s.getName())) {
                        continue;
                    }

                    for (BeanProperty propItem : s.getItems()) {
                        PropertyEditor editor = null;

                        if (editor == null) {
                            Class[] propTypes = introspection.getPropTypes(propItem.getName());
                            if (propTypes == null) {
                                continue;
                            }
                            List<? extends BeanSpecificPropertyEditorFactory> list = MainLookup.lookupAll(BeanSpecificPropertyEditorFactory.class);
                            for (BeanSpecificPropertyEditorFactory f : list) {
                                editor = f.getEditor(node, propItem.getName(), propTypes);
                                if (editor != null) {
                                    beanSpecificEditors.put(propItem.getName(), editor);
                                    break;
                                }
                            }
                            if (editor == null) {
                                editor = PropertyEditorFactory.getDefault().getEditor(propItem.getName(), propTypes);
                                map.put(propItem.getName(), editor);
                            }
                        }
                    }
                }

            }
            return map;
        }

        private TitledPane getSectionPane(String categoryName, String sectionName, String displayName) {
            String id = categoryName + "-" + sectionName;
            TitledPane pane = sections.get(id);
            if (displayName == null || displayName.trim().isEmpty()) {
                displayName = EditorUtil.toDisplayName(sectionName);
            }

            if (pane != null) {
                pane.setText(displayName);
            } else {
                pane = new TitledPane();
                pane.setId(id);
                pane.getStyleClass().add("section");
                pane.setText(displayName);

                sections.put(id, pane);
                GridPane grid = new GridPane();
                grid.getStyleClass().add("prop-grid");
                grid.setHgap(10);
                grid.setVgap(5);
                StackPane stackPane = new StackPane(grid);
                stackPane.getStyleClass().add("prop-grid-pane");

                ColumnConstraints cc0 = new ColumnConstraints();
                ColumnConstraints cc1 = new ColumnConstraints();

                cc0.setPercentWidth(35);
                cc1.setPercentWidth(65);
                grid.getColumnConstraints().addAll(cc0, cc1);
                pane.setContent(stackPane);

            }

            return pane;
        }

        private boolean isObservableList(String propName, Introspection introspection) {
            Class[] types = introspection.getPropTypes(propName);
            return types.length == 2 && ObservableList.class.isAssignableFrom(types[0]);
        }

        private boolean isComposite(Class<?> clazz) {
            return PropertyPaneModelRegistry.getPropertyPaneModel().isComposite(clazz);
        }

        private boolean isEditable(BeanProperty pi, Introspection introspection) {
            boolean retval = pi.isModifiable();
            if (retval) {
                PropertyDescriptor pd = introspection.getPropertyDescriptors().get(pi.getName());
                if (pd.getWriteMethod() == null && introspection.getPropTypes(pi.getName()).length == 1) {
                    retval = false;
                }
            }

            return retval;
        }

        private void modifyByStyleOrigin(BeanProperty propItem, Property prop, PropertyEditor editor, HyperlinkTitle title, Introspection introspection) {
            if (prop instanceof StyleableProperty) {

                StyleableProperty styleableProp = (StyleableProperty) prop;
                if (styleableProp.getStyleOrigin() != null && (styleableProp.getStyleOrigin().equals(INLINE) || styleableProp.getStyleOrigin().equals(AUTHOR))) {
                    editor.setEditable(false);
                    title.setCssValue(true);
                }
                ChangeListener listener = styleableListeners.get(styleableProp);

                if (listener == null) {
                    //
                    // ? Force
                    //
                    //originChanged(propItem, styleableProp, editor, title, introspection);

                    listener = (value, oldValue, newValue) -> {
                        originChanged(propItem, styleableProp, editor, title, introspection);
                    };

                    styleableListeners.put(prop, listener);
                    prop.addListener(listener);
                }

            }

        }

        private void originChanged(BeanProperty propItem, StyleableProperty styleableProp, PropertyEditor editor, HyperlinkTitle title, Introspection introspection) {
            Platform.runLater(() -> {
                if (styleableProp.getStyleOrigin() != null && (styleableProp.getStyleOrigin().equals(INLINE) || styleableProp.getStyleOrigin().equals(AUTHOR))) {
                    editor.setEditable(false);
                    title.setCssValue(true);
                } else {
                    if (isEditable(propItem, introspection)) {
                        editor.setEditable(true);
                        title.setCssValue(false);
                    }
                }
            });
        }

    }

}//class PropertyEditorPane
