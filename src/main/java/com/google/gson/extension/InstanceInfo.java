package com.google.gson.extension;

import java.util.ArrayList;
import java.util.List;

public class InstanceInfo {

    private String name;
    
    private InstanceInfo parent;
    
    private List<InstanceInfo> children;
    
    public InstanceInfo(String name) {
        this.name = name;
        this.parent = null;
        this.children = new ArrayList<>(1);
    }
    
    public InstanceInfo(String name, InstanceInfo parent) {
        this.name = name;
        this.parent = parent;
        this.children = new ArrayList<>(1);
    }
    
    public void addChild(final InstanceInfo child) {
        if (child != null) {
            this.children.add(child);
            child.parent = this;
        }
    }

    public String getName() {
        return this.name;
    }
    
    public InstanceInfo getParent() {
        return this.parent;
    }
    
    
}
