package com.google.gson.extension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class InstanceInfo {

    private String name;
    
    private Class<?> rawType;
    
    private InstanceInfo parent;
    
    private List<InstanceInfo> children;
    
    private boolean hasSeq;
    
    private AtomicInteger seq;
    
    private String collectionFieldValue;
    
    private String fieldValue;
    
    public InstanceInfo(String name, Class<?> rawType) {
        this.name = name;
        this.rawType = rawType;
        this.parent = null;
        this.children = new ArrayList<>(1);
        this.hasSeq = false;
        this.seq = new AtomicInteger(-1);
    }
    
    public InstanceInfo(String name, Class<?> rawType, InstanceInfo parent) {
        this.name = name;
        this.rawType = rawType;
        this.parent = parent;
        this.children = new ArrayList<>(1);
        this.hasSeq = false;
        this.seq = new AtomicInteger(-1);
    }
    
    public InstanceInfo(String name, Class<?> rawType, InstanceInfo parent, boolean hasSeq) {
        this.name = name;
        this.rawType = rawType;
        this.parent = parent;
        this.children = new ArrayList<>(1);
        this.hasSeq = hasSeq;
        this.seq = new AtomicInteger(-1);
    }
    
    public void addChild(final InstanceInfo child) {
        if (child != null) {
            this.children.add(child);
            child.parent = this;
        }
    }
    
    public boolean isWrapClass() {
        return BuildUtils.isWrapClass(this.rawType);
    }
    
    public boolean isCollection() {
        return (Collection.class.isAssignableFrom(this.rawType));
    }
    
    public int getNextSeq() {
        return seq.incrementAndGet();
    }

    public String getName() {
        return this.name;
    }
    
    public InstanceInfo getParent() {
        return this.parent;
    }

    public boolean isHasSeq() {
        return hasSeq;
    }

    public String getCollectionFieldValue() {
        return collectionFieldValue;
    }

    public void setCollectionFieldValue(String collectionFieldValue) {
        this.collectionFieldValue = collectionFieldValue;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }
    
}
