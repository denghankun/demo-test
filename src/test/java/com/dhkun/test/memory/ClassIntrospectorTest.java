package com.dhkun.test.memory;

import org.junit.Assert;
import org.junit.Test;

import com.dhkun.test.memory.model.ObjectA;
import com.dhkun.test.memory.model.ObjectC;
import com.dhkun.test.memory.model.ObjectD;

public class ClassIntrospectorTest {

    /**
     * 测试一般对象,value都为null或默认值
     * @throws IllegalAccessException
     */
    @Test
    public void testSimple() throws IllegalAccessException {
        final ClassIntrospector ci = new ClassIntrospector();
        ObjectInfo res = ci.introspect(new ObjectA());
        Assert.assertEquals(res.getDeepSize(), 32l);
    }
    
    /**
     * 测试包含字符串不为空的对象
     * @throws IllegalAccessException
     */
    @Test
    public void testNotBlankSring() throws IllegalAccessException {
        final ClassIntrospector ci = new ClassIntrospector();
        ObjectInfo res = ci.introspect(new ObjectC());
        Assert.assertEquals(res.getDeepSize(), 80l);
    }
    
    /**
     * 测试包含中文字符串的对象
     * @throws IllegalAccessException
     */
    @Test
    public void testChineseString() throws IllegalAccessException {
        final ClassIntrospector ci = new ClassIntrospector();
        ObjectInfo res = ci.introspect(new ObjectD());
        Assert.assertEquals(res.getDeepSize(), 80l);
    }
    
    @Test
    public void test4() {
        long l = 30000000l;
        int i = 2;
        System.out.println(l * i);
    }
}
