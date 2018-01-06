package com.google.gson.extension;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class FieldPropertyBuildPlugin {

    final static String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
    
    public static void createField(final Field field, final Object fieldValue, final Object value) {
        StringBuilder sb = GsonContextHolder.getGsonContext().getPropertyDesc();
        String fieldName = field.getName();
        String setMethodName = "set" + StringUtils.capitalize(fieldName);
        String className = value.getClass().getSimpleName();
        String instName = StringUtils.uncapitalize(className);
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
            return "'" + fieldValue + "'"; 
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
            String[] array = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                array[i] = convertFieldValue(list.get(i));
            }
            return String.format("Arrays.toList(%s)", StringUtils.join(array, ","));
        }
        return fieldValue.toString();
    }
}
