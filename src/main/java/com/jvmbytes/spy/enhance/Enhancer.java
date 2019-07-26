package com.jvmbytes.spy.enhance;

import com.jvmbytes.spy.event.EventType;

import java.util.Set;

/**
 * 代码增强
 *
 * @author luanjia
 */
public interface Enhancer {

    /**
     * 转换为增强后的字节码数组
     *
     * @param loader           目标类加载器
     * @param srcByteCodeArray 源字节码数组
     * @param signCodes        需要被增强的行为签名
     * @param namespace        命名空间
     * @param listenerId       需要埋入的监听器ID
     * @param eventTypeArray   需要配埋入的事件类型
     * @return 增强后的字节码数组
     */
    byte[] toByteCodeArray(ClassLoader loader,
                           byte[] srcByteCodeArray,
                           Set<String> signCodes,
                           String namespace,
                           int listenerId,
                           EventType[] eventTypeArray);

}
