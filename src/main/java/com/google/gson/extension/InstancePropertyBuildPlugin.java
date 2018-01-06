package com.google.gson.extension;

import org.apache.commons.lang.StringUtils;

public class InstancePropertyBuildPlugin {

    final static String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
    
    public static void createInstance(Class<?> rawType, boolean isRoot) {
        StringBuilder sb = GsonContextHolder.getGsonContext().getPropertyDesc();
        try {
            if (rawType.getConstructor() == null) {
                throw new RuntimeException(rawType + "必须含有无参数构造函数");
            }
            String className = rawType.getSimpleName();
            String lowClassName = StringUtils.uncapitalize(className);
            String inst = String.format("%s %s = new %s();", className, lowClassName, className);
            sb.append(inst).append(LINE_SEPARATOR);
            // 跟元素
            if (isRoot) {
                InstanceInfo root = new InstanceInfo(lowClassName);
                // root 和 cur保持一致
                GsonContextHolder.getGsonContext().setRootInstanceInfo(root);
                GsonContextHolder.getGsonContext().setCurInstanceInfo(root);
            } else {
            // 子元素
                InstanceInfo parent = GsonContextHolder.getGsonContext().getCurInstanceInfo();
                InstanceInfo child = new InstanceInfo(lowClassName, parent);
                GsonContextHolder.getGsonContext().setCurInstanceInfo(child);
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("创建对象示例语句失败", e);
        } catch (SecurityException e) {
            throw new RuntimeException("创建对象示例语句失败", e);
        }
    }
}
