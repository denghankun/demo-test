package com.google.gson.extension;

import org.apache.commons.lang.StringUtils;

public class InstancePropertyBuildPlugin {

    public static void beginCreateInstance(Class<?> rawType, boolean isRoot) {
        // 必须为一般对象
        if (rawType.isPrimitive() || BuildUtils.isWrapClass(rawType)) {
            return;
        }
        StringBuilder sb = GsonContextHolder.getGsonContext().getPropertyDesc();
        try {
            if (rawType.getConstructor() == null) {
                throw new RuntimeException(rawType + "必须含有无参数构造函数");
            }
            String className = rawType.getSimpleName();
            String lowClassName = StringUtils.uncapitalize(className);
            InstanceInfo cur = GsonContextHolder.getGsonContext().getCurInstanceInfo();
            if (cur != null && cur.isHasSeq()) {
                lowClassName += cur.getNextSeq();
            }
            String inst = String.format("%s %s = new %s();", className, lowClassName, className);
            sb.append(inst).append(Constants.LINE_SEPARATOR);
            // 跟元素
            if (isRoot) {
                InstanceInfo root = new InstanceInfo(lowClassName, rawType);
                // root 和 cur保持一致
                GsonContextHolder.getGsonContext().setRootInstanceInfo(root);
                GsonContextHolder.getGsonContext().setCurInstanceInfo(root);
            } else {
                // 子元素
                InstanceInfo parent = GsonContextHolder.getGsonContext().getCurInstanceInfo();
                InstanceInfo child = new InstanceInfo(lowClassName, rawType, parent);
                GsonContextHolder.getGsonContext().setCurInstanceInfo(child);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException("创建对象实例语句失败", e);
        } catch (SecurityException e) {
            e.printStackTrace();
            throw new RuntimeException("创建对象实例语句失败", e);
        }
    }
    
    public static void endCreateInstance(Class<?> rawType, boolean isRoot) {
        if (isRoot) {
            GsonContextHolder.getGsonContext().setCurInstanceInfo(null);
            return;
        }
        String curFieldValue = GsonContextHolder.getGsonContext().getCurInstanceInfo().getName();
        InstanceInfo parent = GsonContextHolder.getGsonContext().getCurInstanceInfo().getParent();
        parent.setFieldValue(curFieldValue);
        GsonContextHolder.getGsonContext().setCurInstanceInfo(parent);
    }
}
