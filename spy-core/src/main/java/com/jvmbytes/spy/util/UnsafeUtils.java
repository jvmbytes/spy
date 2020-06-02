package com.jvmbytes.spy.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Sum Jvm Unsafe
 *
 * @author luanjia
 */
public class UnsafeUtils {

    public static Unsafe getUnsafe() throws NoSuchFieldException, IllegalAccessException {
        final Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (Unsafe) f.get(null);
    }

}
