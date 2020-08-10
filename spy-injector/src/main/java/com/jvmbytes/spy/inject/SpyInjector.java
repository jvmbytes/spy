package com.jvmbytes.spy.inject;

import com.jvmbytes.agent.inst.InstLoader;
import com.jvmbytes.filter.manager.ClassDataSource;
import com.jvmbytes.filter.matcher.Matcher;
import com.jvmbytes.spy.enhance.weaver.EventListenerHandlers;
import com.jvmbytes.spy.event.EventType;
import com.jvmbytes.spy.inject.transform.SpyTransformer;
import com.jvmbytes.spy.listener.EventListener;
import com.jvmbytes.spy.util.SpyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wongoo
 */
public final class SpyInjector {
    private static final Logger logger = LoggerFactory.getLogger(SpyInjector.class);

    private static Instrumentation inst;

    private static ClassDataSource classDataSource;

    public static final String DEFAULT_NAMESPACE = "default";

    private static Map<Integer, SpyTransformer> transformers
            = new ConcurrentHashMap<Integer, SpyTransformer>(8);

    static {
        try {
            setInst(InstLoader.loadInst());
        } catch (Exception e) {
        }
    }

    public static void setInst(Instrumentation inst) {
        SpyInjector.inst = inst;
        SpyInjector.classDataSource = new ClassDataSource(inst, true);
    }

    private static void checkInst() throws Exception {
        if (inst == null) {
            throw new Exception("Instrumentation not initialized");
        }
    }

    public static int inject(Matcher matcher, EventType[] types, EventListener listener) throws Exception {
        return inject(DEFAULT_NAMESPACE, matcher, types, listener);
    }

    public static int inject(String namespace, Matcher matcher, EventType[] types, EventListener listener) throws Exception {
        checkInst();

        SpyUtils.init(namespace);

        SpyTransformer transformer = new SpyTransformer(namespace, matcher, types, listener);

        transformers.put(transformer.getListenerId(), transformer);

        EventListenerHandlers.getSingleton().active(transformer.getListenerId(), listener, types);

        inst.addTransformer(transformer, true);

        retransform(matcher);

        return transformer.getListenerId();
    }

    public static void remove(int id) throws Exception {
        checkInst();

        SpyTransformer transformer = transformers.remove(id);
        if (transformer != null) {
            inst.removeTransformer(transformer);
            EventListenerHandlers.getSingleton().frozen(transformer.getListenerId());
            retransform(transformer.getMatcher());
        }
    }

    private static void retransform(Matcher matcher) {
        List<Class<?>> classes = classDataSource.find(matcher);
        if (classes.size() > 0) {
            try {
                inst.retransformClasses(classes.toArray(new Class[0]));
            } catch (UnmodifiableClassException e) {
                logger.debug("retransform error: {},{}", e.getClass().getName(), e.getMessage());
            }
        }
    }
}
