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

import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.stage.Window;

/**
 *
 * @author Valery
 */
public class WindowLookup { // implements ContextLookup {
    
    //private final ContextLookup lookup;
    
    private ObservableMap<Window,ContextLookup> lookups = FXCollections.observableHashMap();
            
    protected WindowLookup() {
        //lookup = new BaseContextLookup();
        init();
    }
    private void init() {
    }
    private static WindowLookup getInstance() {
        return SingletonInstance.INSTANCE;
    }
    public static ContextLookup getLookup(Window win) {
        
        ContextLookup lookup = getInstance().lookups.get(win);
        if ( lookup == null ) {
            lookup = new BaseContextLookup();
            getInstance().lookups.put(win, lookup);
        }
        return lookup;
    }
    public static <T> T lookup(Window win, Class<T> clazz) {
        return getLookup(win).lookup(clazz);
    }

    
    public static <T> List<? extends T> lookupAll(Window win, Class<T> clazz) {
        return getLookup(win).lookupAll(clazz);
    }

    public static <T> T lookupFirst(Class<T> clazz) {
        T retval = null;
        for ( ContextLookup lk : getInstance().lookups.values()) {
            if ( lk.lookup(clazz) != null ) {
                retval = lk.lookup(clazz);
                break;
            }
        }
        return retval;
    }
    
    public static <T> void add(Window win,T obj) {
        getLookup(win).add(obj);
    }

  
    public static <T> void remove(Window win, T obj) {
        getLookup(win).remove(obj);
    }
    public static <T> void removeLookup(Window win) {
        getInstance().lookups.remove(win);
    }

    
    public static <T> void putUnique(Window win, Class key, T obj) {
        getLookup(win).putUnique(key, obj);
    }

    
    public static <T> void remove(Window win,Class key, T obj) {
        getLookup(win).remove(key, obj);
    }
    

    private static class SingletonInstance {
        private static final WindowLookup INSTANCE = new WindowLookup();
    }
    
}
