package com.jvmbytes.spy.event;

/**
 * 方法调用RETURN事件
 *
 * @author luanjia
 */
public class ReturnEvent extends InvokeEvent {

    /**
     * 调用返回值
     */
    public final Object object;

    /**
     * 构造调用RETURN事件
     *
     * @param processId 调用过程ID
     * @param invokeId  调用ID
     * @param object    调用返回值(void方法返回值为null)
     */
    public ReturnEvent(final int processId,
                       final int invokeId,
                       final Object object) {
        super(processId, invokeId, EventType.RETURN);
        this.object = object;
    }

    /**
     * 构造调用RETURN事件，
     * 主要开放给{@link ImmediatelyReturnEvent}构造所使用
     *
     * @param eventType 必须是{@link EventType#RETURN}或{@link EventType#IMMEDIATELY_RETURN}两者之一的值
     * @param processId 调用过程ID
     * @param invokeId  调用ID
     * @param object    调用返回值(void方法返回值为null)
     */
    ReturnEvent(final EventType eventType,
                final int processId,
                final int invokeId,
                final Object object) {
        super(processId, invokeId, eventType);
        this.object = object;

        // 对入参进行校验
        if (eventType != EventType.IMMEDIATELY_RETURN
                && eventType != EventType.RETURN) {
            throw new IllegalArgumentException(String.format("eventType must be %s or %s", EventType.RETURN, EventType.IMMEDIATELY_RETURN));
        }

    }

}
