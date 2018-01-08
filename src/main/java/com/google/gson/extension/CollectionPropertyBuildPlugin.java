package com.google.gson.extension;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class CollectionPropertyBuildPlugin {
    
    @SuppressWarnings("unchecked")
    public static <T> void beginCreateCollection(Collection<T> collection, Type elementType, boolean isRoot) {
        StringBuilder sb = GsonContextHolder.getGsonContext().getPropertyDesc();
        Class<T> elementClass = null;
        if (elementType instanceof Class) {
            elementClass = (Class<T>) elementType;
        }
        String elementClassName = elementClass != null ? elementClass.getSimpleName() : elementType.toString();
        // 为List实例
        if (collection instanceof List) {
            // 只处理非包装类
            if (!BuildUtils.isWrapClass(elementClass)) {
                String instListStr = StringUtils.uncapitalize(elementClassName) + "List";
                String colStr = String.format("List<%s> %s = new ArrayList<%s>();", elementClassName, instListStr, elementClassName);
                sb.append(colStr).append(Constants.LINE_SEPARATOR);
                
                if (isRoot) {
                    InstanceInfo root = new InstanceInfo(instListStr, collection.getClass());
                    // root 和 cur保持一致
                    GsonContextHolder.getGsonContext().setRootInstanceInfo(root);
                    GsonContextHolder.getGsonContext().setCurInstanceInfo(root);
                } else {
                    // 子元素
                    InstanceInfo parent = GsonContextHolder.getGsonContext().getCurInstanceInfo();
                    InstanceInfo child = new InstanceInfo(instListStr, collection.getClass(), parent, true);
                    GsonContextHolder.getGsonContext().setCurInstanceInfo(child);
                }
            }
        }
        
    }
    
    @SuppressWarnings("rawtypes")
    public static void beginCreateElement(Type elementType, boolean isRoot) {
        if (elementType instanceof Class) {
            Class elementClass = (Class) elementType;
            if (!BuildUtils.isWrapClass(elementClass)) {
                InstancePropertyBuildPlugin.beginCreateInstance(elementClass, isRoot);
                // 添加到List中
                InstanceInfo cur = GsonContextHolder.getGsonContext().getCurInstanceInfo();
                String parentName = cur.getParent().getName();
                StringBuilder sb = GsonContextHolder.getGsonContext().getPropertyDesc();
                String addStr = String.format("%s.add(%s);", parentName, cur.getName());
                sb.append(addStr).append(Constants.LINE_SEPARATOR);
            }
        }
    }
    
    @SuppressWarnings("rawtypes")
    public static void endCreateElement(Type elementType, boolean isRoot) {
        if (isRoot) {
            return;
        }
        
        if (elementType instanceof Class) {
            Class elementClass = (Class) elementType;
            if (!BuildUtils.isWrapClass(elementClass)) {
                // 把cur的parent节点设置为cur
                InstanceInfo parent = GsonContextHolder.getGsonContext().getCurInstanceInfo().getParent();
                GsonContextHolder.getGsonContext().setCurInstanceInfo(parent);
            }
        }
    }
    
    //@SuppressWarnings("rawtypes")
    public static<T> void endCreateCollection(Collection<T> collection, Type elementType, boolean isRoot) {
        if (isRoot) {
            return;
        }
        
        if (elementType instanceof Class) {
            Class elementClass = (Class) elementType;
            if (!BuildUtils.isWrapClass(elementClass)) {
                // 把cur的parent节点设置为cur
                String fieldValue = GsonContextHolder.getGsonContext().getCurInstanceInfo().getName();
                InstanceInfo parent = GsonContextHolder.getGsonContext().getCurInstanceInfo().getParent();
                parent.setCollectionFieldValue(fieldValue);
                GsonContextHolder.getGsonContext().setCurInstanceInfo(parent);
            }
        }
    }
    
}
