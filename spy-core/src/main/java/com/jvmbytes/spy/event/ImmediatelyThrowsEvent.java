package com.jvmbytes.spy.event;

/**
 * 立即异常抛出事件
 *
 * @author luanjia
 */
public class ImmediatelyThrowsEvent extends ThrowsEvent {

    /**
     * 构造立即异常抛出事件
     *
     * @param processId 调用过程ID
     * @param invokeId  调用ID
     * @param throwable 抛出的异常/错误信息
     */
    public ImmediatelyThrowsEvent(final int processId,
                                  final int invokeId,
                                  final Throwable throwable) {
        super(EventType.IMMEDIATELY_THROWS, processId, invokeId, throwable);
    }

}
