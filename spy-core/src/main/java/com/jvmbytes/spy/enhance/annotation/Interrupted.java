package com.jvmbytes.spy.enhance.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 中断式事件处理器
 * 当事件处理器处理事件抛出异常时,将会中断原有方法调用
 *
 * @author luanjia
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Interrupted {
}
