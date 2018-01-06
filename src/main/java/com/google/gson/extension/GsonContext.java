package com.google.gson.extension;

/**
 * @author denghankun
 * 
 * 创建gson运行上下文,用于扩展
 */
public class GsonContext {
    
    private StringBuilder propertyDesc;
    
    private InstanceInfo root;
    
    private InstanceInfo cur;
    
    public static GsonContext newGsonContext() {
        GsonContext context = new GsonContext();
        return context.initContext();
    }
    
    public StringBuilder getPropertyDesc() {
        return this.propertyDesc;
    }
    
    public InstanceInfo getRootInstanceInfo() {
        return this.root;
    }
    
    public void setRootInstanceInfo(InstanceInfo root) {
        this.root = root;
    }
    
    public InstanceInfo getCurInstanceInfo() {
        return this.cur;
    }
    
    public void setCurInstanceInfo(InstanceInfo cur) {
        this.cur = cur;
    }
    
    public void print() {
        System.out.println(propertyDesc.toString());
    }
    
    private GsonContext initContext() {
        this.propertyDesc = new StringBuilder();
        return this;
    }
    
    private GsonContext() {}
}
