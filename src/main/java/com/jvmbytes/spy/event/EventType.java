package com.jvmbytes.spy.event;

/**
 * 事件枚举类型
 */
public enum EventType {

    /**
     * 调用:BEFORE
     */
    BEFORE,

    /**
     * 调用:RETURN
     */
    RETURN,

    /**
     * 调用:THROWS
     */
    THROWS,

    /**
     * 调用:LINE
     * 一行被调用了
     */
    LINE,

    /**
     * 调用:CALL_BEFORE
     * 一个方法被调用之前
     */
    CALL_BEFORE,

    /**
     * 调用:CALL_RETURN
     * 一个方法被调用正常返回之后
     */
    CALL_RETURN,

    /**
     * 调用:CALL_THROWS
     * 一个方法被调用抛出异常之后
     */
    CALL_THROWS,


    /**
     * 立即调用:RETURN
     */
    IMMEDIATELY_RETURN,

    /**
     * 立即调用:THROWS
     */
    IMMEDIATELY_THROWS,

}