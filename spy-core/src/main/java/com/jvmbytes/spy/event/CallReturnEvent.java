package com.jvmbytes.spy.event;

/**
 * 方法调用追踪事件:RETURN
 *
 * @author luanjia
 */
public class CallReturnEvent extends InvokeEvent {
    /**
     * 构造调用事件
     *
     * @param processId 调用过程ID
     * @param invokeId  调用ID
     */
    public CallReturnEvent(int processId, int invokeId) {
        super(processId, invokeId, EventType.CALL_RETURN);
    }
}
