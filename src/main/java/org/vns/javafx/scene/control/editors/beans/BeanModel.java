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
package org.vns.javafx.scene.control.editors.beans;

import javafx.beans.DefaultProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.effect.Bloom;

/**
 *
 * @author Valery
 */
@DefaultProperty("items")
public class BeanModel extends AbstractNamedItem implements NamedItemList<Category> {

    private Class<?> beanType;
    private String beanClassName;
    private Object bean;

    private final ObservableList<Category> categories = FXCollections.observableArrayList();


    public Class<?> getBeanType() {
        return beanType;
    }

    public void setBeanType(Class<?> beanType) {
        this.beanType = beanType;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    @Override
    public ObservableList<Category> getItems() {
        return categories;
    }

    public BeanModel getCopyFor(Class<?> clazz) {
        BeanModel ppd;
        if ( Bloom.class.equals(clazz)) {
            System.err.println("");
        }
        if ( Bloom.class.equals(this.getBeanType())) {
            System.err.println("");
        }        
        if ( this instanceof CompositeBeanModel ) {
            ppd = new CompositeBeanModel();
            ppd.getItems().clear();
        } else {
            ppd = new BeanModel();            
        }
        
        for (Category c : categories) {
            ppd.getItems().add(c.getCopyFor(clazz, ppd));
        }
        return ppd;
    }
    
    public BeanModel getCopyFor(BeanModel source) {
        BeanModel ppd;
        if ( source instanceof CompositeBeanModel ) {
            ppd = new CompositeBeanModel();
            ppd.getItems().clear();
        } else {
            ppd = new BeanModel();
        }
        Class<?> clazz = source.getBeanType();
        for (Category c : categories) {
            ppd.getItems().add(c.getCopyFor(clazz, ppd));
        }
        return ppd;
    }
    @Override
    public String toString() {
        return "name = " + getName() + "; beanClassName = " + getBeanClassName() + "items.size = " + getItems().size(); 
    }


    protected Category addCategory(String cat, String displayName) {
        Category retval = null;
        for (Category c : categories) {
            if (c.getName().equals(cat)) {
                retval = c;
                break;
            }
        }
        if (retval == null) {
            retval = new Category(cat, displayName);
            categories.add(retval);
        }

        return retval;
    }

}
