package com.google.gson.extension;

public class GsonContextHolder {

    public StringBuilder propertyDesc;
    
    public static final ThreadLocal<GsonContext> gsonContextHolder = new ThreadLocal<GsonContext>() {
        @Override
        protected GsonContext initialValue() {
            return GsonContext.newGsonContext();
        }
    };
    
    /**
     * 重置上下文
     */
    public static void resetGsonContext() {
        gsonContextHolder.remove();
    }
    
    /**
     * 设置上下文
     * @param value
     */
    public static void setGsonContext(GsonContext value) {
        if (value == null) {
            resetGsonContext();
        } else {
            gsonContextHolder.set(value);
        }
    }
    
    public static GsonContext getGsonContext() {
        return gsonContextHolder.get();
    }
}
