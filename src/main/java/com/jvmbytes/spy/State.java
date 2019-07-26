package com.jvmbytes.spy;

/**
 * 流程控制状态
 */
public enum State {

    /**
     * 立即返回
     */
    RETURN_IMMEDIATELY,

    /**
     * 立即抛出异常
     */
    THROWS_IMMEDIATELY,

    /**
     * 不干预任何流程
     *
     * @since {@code sandbox-api:1.0.16}
     */
    NONE_IMMEDIATELY

}