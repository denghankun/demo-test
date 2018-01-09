package com.google.gson.extension;

import org.apache.commons.lang.StringUtils;

public class EndianConverter {

    /** 
     * 将Java char转换为byte数组.
     * 下标0为高位,下标1为地位
     *  
     * @param ch 
     * @return 
     */  
    public static byte[] toBytes(char ch) {  
        byte[] bytes = new byte[2];  
        bytes[0] = (byte) (ch >> 8 & 0xff);  
        bytes[1] = (byte) (ch & 0xff);  
        return bytes;  
    }
    
}
