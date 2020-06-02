package com.jvmbytes.spy.event;

/**
 * 异常/错误抛出事件
 *
 * @author luanjia
 */
public class ThrowsEvent extends InvokeEvent {

    /**
     * 抛出的异常/错误信息
     */
    public final Throwable throwable;

    /**
     * 构造异常/错误抛出调用事件
     *
     * @param processId 调用过程ID
     * @param invokeId  调用ID
     * @param throwable 抛出的异常/错误信息
     */
    public ThrowsEvent(final int processId,
                       final int invokeId,
                       final Throwable throwable) {
        super(processId, invokeId, EventType.THROWS);
        this.throwable = throwable;
    }

    /**
     * 构造异常/错误抛出调用事件
     * 主要开放给{@link ImmediatelyThrowsEvent}构造所使用
     *
     * @param eventType 必须是{@link EventType#THROWS}或{@link EventType#IMMEDIATELY_THROWS}两者之一的值
     * @param processId 调用过程ID
     * @param invokeId  调用ID
     * @param throwable 抛出的异常/错误信息
     */
    ThrowsEvent(final EventType eventType,
                final int processId,
                final int invokeId,
                final Throwable throwable) {
        super(processId, invokeId, eventType);
        this.throwable = throwable;

        // 对入参进行校验
        if (eventType != EventType.THROWS
                && eventType != EventType.IMMEDIATELY_THROWS) {
            throw new IllegalArgumentException(String.format("eventType must be %s or %s", EventType.THROWS, EventType.IMMEDIATELY_THROWS));
        }

    }

}
