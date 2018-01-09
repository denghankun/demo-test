package com.google.gson.extension;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;

public class FieldPropertyBuildPlugin {

    final static String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
    
    public static void createField(final Field field, final Object fieldValue, final Object value) {
        if (fieldValue == null) {
            return;
        }
        StringBuilder sb = GsonContextHolder.getGsonContext().getPropertyDesc();
        String fieldName = field.getName();
        String setMethodName = "set" + StringUtils.capitalize(fieldName);
        String className = value.getClass().getSimpleName();
        String instName = StringUtils.uncapitalize(className);
        InstanceInfo cur = GsonContextHolder.getGsonContext().getCurInstanceInfo();
        if (cur != null) {
            instName = cur.getName(); // 使用当前上下文名字
        }
        String fieldStr = String.format("%s.%s(%s);", instName, setMethodName, convertFieldValue(fieldValue));
        sb.append(fieldStr).append(LINE_SEPARATOR);
    }
    
    private static String convertFieldValue(Object fieldValue) {
        
        if (fieldValue instanceof Long) {
            return fieldValue.toString() + "l";
        }
        
        if (fieldValue instanceof Float) {
            return fieldValue.toString() + "f";
        }
        
        if (fieldValue instanceof Character) {
            Character c = (Character) fieldValue;
            byte[] bytes = EndianConverter.toBytes(c.charValue());
            // 转为16进制
            return "0x" + Hex.encodeHexString(bytes); 
        }
        
        if (fieldValue instanceof String) {
            return "\"" + fieldValue + "\"";
        }
        
        if (fieldValue instanceof java.util.Date) {
            java.util.Date date = (java.util.Date) fieldValue;
            return String.format("new Date(%sl)", date.getTime());
        }
        
        if (fieldValue instanceof List) {
            List<?> list = (List<?>) fieldValue;
            if (list.size() > 0) {
                if (!BuildUtils.isWrapClass(list.get(0).getClass())) {
                    return GsonContextHolder.getGsonContext().getCurInstanceInfo().getCollectionFieldValue();
                }
                
                String[] array = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    array[i] = convertFieldValue(list.get(i));
                }
                return String.format("Arrays.toList(%s)", StringUtils.join(array, ","));
            }
        }
        // 默认返回当前
        String curFieldValue = GsonContextHolder.getGsonContext().getCurInstanceInfo().getFieldValue();
        return (curFieldValue == null ? fieldValue.toString() : curFieldValue);
    }
}
