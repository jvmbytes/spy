package com.jvmbytes.spy.event;


import static com.jvmbytes.spy.event.EventType.LINE;

/**
 * 方法调用行事件
 *
 * @author luanjia
 */
public class LineEvent extends InvokeEvent {

    public final int lineNumber;

    /**
     * 构造调用事件
     *
     * @param processId  调用过程ID
     * @param invokeId   调用ID
     * @param lineNumber 代码行号
     */
    public LineEvent(int processId, int invokeId, int lineNumber) {
        super(processId, invokeId, LINE);
        this.lineNumber = lineNumber;
    }

}
