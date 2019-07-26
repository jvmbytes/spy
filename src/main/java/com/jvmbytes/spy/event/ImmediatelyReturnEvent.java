package com.jvmbytes.spy.event;

/**
 * 立即返回事件
 *
 * @author luanjia
 */
public class ImmediatelyReturnEvent extends ReturnEvent {

    /**
     * 构造立即返回事件
     *
     * @param processId 调用过程ID
     * @param invokeId  调用ID
     * @param object    调用返回值(void方法返回值为null)
     */
    public ImmediatelyReturnEvent(final int processId,
                                  final int invokeId,
                                  final Object object) {
        super(EventType.IMMEDIATELY_RETURN, processId, invokeId, object);
    }

}
