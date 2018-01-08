package com.google.gson.extension;

public class BuildUtils {
    
    /**
     * 是否为包装类,包括Integer/Float/DOUBLE/String
     * 
     * @param clz
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static boolean isWrapClass(Class clz) {
        try {
            if (clz.getName().equals("java.lang.String")) {
                return true;
            }
            return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }
    
    public static boolean isRootInstance() {
        InstanceInfo root = GsonContextHolder.getGsonContext().getRootInstanceInfo();
        return (root == null ? true : false);
    }
}
