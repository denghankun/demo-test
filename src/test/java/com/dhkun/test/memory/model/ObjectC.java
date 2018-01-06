package com.dhkun.test.memory.model;

/**
 * http://blog.csdn.net/iter_zc/article/details/41822719
 * 
 * 内存分布
 * 
 * -:表示占用位,包括mark,oop指针和其他字段
 * s:字符串(String)指针占用位(压缩后都为4)
 * p:补位
 * a:数组指针占用位(压缩后都为4)
 * l:数组长度占用位(压缩后都为4)
 * i:整形(int)占用位(长度都为4)
 * c:字符(char)占用位(长度都为2)
 * 
 * OjbectC:
 * ------------------------sssspppp
 * str:
 * ------------aaaaiiiipppp
 * char[]
 * ------------llll cc cc cc cc
 */
public class ObjectC {
    String str = "abcd"; // 4
    int i1; // 4
    int i2; // 4
    byte b1; // 1
    byte b2; // 1
    byte b3; // 1
    
}
