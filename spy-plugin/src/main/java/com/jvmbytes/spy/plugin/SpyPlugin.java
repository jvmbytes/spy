package com.jvmbytes.spy.plugin;

import com.jvmbytes.filter.matcher.Matcher;
import com.jvmbytes.spy.event.EventType;
import com.jvmbytes.spy.listener.EventListener;

/**
 * spy plugin interface
 *
 * @author wongoo
 */
public interface SpyPlugin {
    /**
     * get plugin name
     *
     * @return name
     */
    String getName();

    /**
     * get plugin namespace
     *
     * @return namespace
     */
    String getNamespace();

    /**
     * get plugin matcher
     *
     * @return matcher
     */
    Matcher getMatcher();

    /**
     * get plugin event types
     *
     * @return event types
     */
    EventType[] getEventTypes();

    /**
     * get plugin event listener
     *
     * @return event listener
     */
    EventListener getEventListener();
}
