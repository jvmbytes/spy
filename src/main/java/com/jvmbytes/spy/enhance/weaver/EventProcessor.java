package com.jvmbytes.spy.enhance.weaver;

import com.jvmbytes.commons.utils.collection.Stack;
import com.jvmbytes.commons.utils.collection.ThreadUnsafeStack;
import com.jvmbytes.spy.enhance.annotation.Interrupted;
import com.jvmbytes.spy.event.Event;
import com.jvmbytes.spy.event.EventType;
import com.jvmbytes.spy.listener.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 事件处理器
 */
class EventProcessor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 处理单元
     */
    class Process {

        // 事件工厂
        private final SingleEventFactory eventFactory
                = new SingleEventFactory();

        // 调用堆栈
        private final Stack<Integer> stack
                = new ThreadUnsafeStack<Integer>();

        // 需要忽略的过程ID
        private Integer ignoreProcessId = null;

        void pushInvokeId(int invokeId) {
            stack.push(invokeId);
            logger.debug("push process-stack, invoke-id={};deep={};listener={};",
                    invokeId,
                    stack.deep(),
                    listenerId
            );
        }

        int popInvokeId() {
            final int invokeId = stack.pop();
            logger.debug("pop process-stack, invoke-id={};deep={};listener={};",
                    invokeId,
                    stack.deep(),
                    listenerId
            );
            if (stack.isEmpty()) {
                processRef.remove();
                logger.debug("clean TLS: event-processor, listener={};", listenerId);
            }
            return invokeId;
        }

        int getInvokeId() {
            return stack.peek();
        }

        int getProcessId() {
            return stack.peekLast();
        }

        boolean isEmptyStack() {
            return stack.isEmpty();
        }

        boolean touchIsIgnoreProcess(int processId) {
            if (null != ignoreProcessId
                    && ignoreProcessId == processId) {
                return true;
            } else {
                ignoreProcessId = null;
                return false;
            }
        }

        void markIgnoreProcessId(int processId) {
            ignoreProcessId = processId;
        }

        SingleEventFactory getEventFactory() {
            return eventFactory;
        }

    }

    @Interrupted
    private class InterruptedEventListenerImpl implements EventListener {

        private final EventListener listener;

        private InterruptedEventListenerImpl(EventListener listener) {
            this.listener = listener;
        }

        @Override
        public void onEvent(Event event) throws Throwable {
            listener.onEvent(event);
        }

    }

    final int listenerId;
    final EventListener listener;
    final EventType[] eventTypes;
    final ThreadLocal<Process> processRef = new ThreadLocal<Process>() {
        @Override
        protected Process initialValue() {
            return new Process();
        }
    };

    EventProcessor(final int listenerId,
                   final EventListener listener,
                   final EventType[] eventTypes) {

        this.listenerId = listenerId;
        this.eventTypes = eventTypes;
        this.listener = isInterruptEventHandler(listener.getClass())
                ? new InterruptedEventListenerImpl(listener)
                : listener;
    }

    /**
     * 判断是否是中断式事件处理器
     *
     * @param listenerClass 事件监听器类型
     * @return TRUE:中断式;FALSE:非中断式
     */
    public static boolean isInterruptEventHandler(final Class<? extends EventListener> listenerClass) {
        return listenerClass.isAnnotationPresent(Interrupted.class);
    }

    /**
     * 校验器，用于校验事件处理器状态是否正确
     * <p>用于测试用例</p>
     */
    class Checker {

        void check() {

            final EventProcessor.Process process = processRef.get();
            final ThreadUnsafeStack<Integer> stack = (ThreadUnsafeStack<Integer>) process.stack;

            if (!process.isEmptyStack()) {
                throw new IllegalStateException(String.format("process-stack is not empty! listener=%s;\n%s",
                        listenerId,
                        toString(stack)
                ));
            }

            for (int index = 0; index < stack.getElementArray().length; index++) {
                if (index <= stack.getCurrent()) {
                    if (null == stack.getElementArray()[index]) {
                        throw new IllegalStateException(String.format("process-stack element is null at index=[%d], listener=%s;\n%s",
                                index,
                                listenerId,
                                toString(stack)
                        ));
                    }
                } else {
                    if (null != stack.getElementArray()[index]) {
                        throw new IllegalStateException(String.format("process-stack element is not null at index=[%d], listener=%s;\n%s",
                                index,
                                listenerId,
                                toString(stack)
                        ));
                    }
                }
            }

            if (null != process.ignoreProcessId) {
                throw new IllegalStateException(String.format("process ignoreProcessId is not null!, processId=%d", process.ignoreProcessId));
            }


        }

        String toString(ThreadUnsafeStack<Integer> stack) {
            final StringBuilder stackSB = new StringBuilder(String.format("stack[deep=%d;current=%d;]{\n", stack.deep(), stack.getCurrent()));
            for (int index = 0; index < stack.getElementArray().length; index++) {
                stackSB.append("\t[" + index + "] = " + stack.getElementArray()[index] + "\n");
            }
            stackSB.append("}");
            return stackSB.toString();
        }

    }

    /**
     * 校验事件处理器
     * <p>用于测试用例</p>
     */
    void check() {
        new Checker().check();
    }

}
