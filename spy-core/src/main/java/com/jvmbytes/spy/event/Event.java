package com.jvmbytes.spy.event;

/**
 * 调用事件
 * JVM方法调用事件
 *
 * @author luanjia
 */
public abstract class Event {

    /**
     * 事件类型
     */
    public final EventType eventType;

    /**
     * 构造调用事件
     *
     * @param eventType 事件类型
     */
    protected Event(EventType eventType) {
        this.eventType = eventType;
    }


}
