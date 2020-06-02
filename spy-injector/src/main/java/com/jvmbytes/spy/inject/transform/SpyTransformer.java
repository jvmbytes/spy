package com.jvmbytes.spy.inject.transform;

import com.jvmbytes.commons.structure.ClassStructure;
import com.jvmbytes.commons.structure.ClassStructureFactory;
import com.jvmbytes.filter.matcher.Matcher;
import com.jvmbytes.filter.matcher.MatchingResult;
import com.jvmbytes.spy.enhance.EventEnhancer;
import com.jvmbytes.spy.event.EventType;
import com.jvmbytes.spy.listener.EventListener;
import com.jvmbytes.spy.util.ObjectIDs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * @author wongoo
 */
public class SpyTransformer implements ClassFileTransformer {

    private static final Logger logger = LoggerFactory.getLogger("spy-transformer");

    EventEnhancer enhancer = new EventEnhancer();

    private int listenerId = ObjectIDs.instance.identity(SpyTransformer.class);

    private EventListener listener;

    private String namespace;

    private EventType[] eventTypes;

    private Matcher matcher;

    public SpyTransformer(String namespace, Matcher matcher, EventType[] types, EventListener listener) {
        this.namespace = namespace;
        this.listener = listener;
        this.eventTypes = types;
        this.matcher = matcher;
    }

    public int getListenerId() {
        return listenerId;
    }

    public Matcher getMatcher() {
        return matcher;
    }

    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            ClassStructure classStructure = null == classBeingRedefined
                    ? ClassStructureFactory.createClassStructure(classfileBuffer, loader)
                    : ClassStructureFactory.createClassStructure(classBeingRedefined);

            MatchingResult matchingResult = matcher.matching(classStructure);

            if (!matchingResult.isMatched()) {
                return null;
            }

            return enhancer.toByteCodeArray(loader,
                    classfileBuffer,
                    matchingResult.getBehaviorSignCodes(),
                    namespace,
                    listenerId,
                    eventTypes);
        } catch (Exception e) {
            logger.debug("transform {} error: {},{}", className, e.getClass().getName(), e.getMessage());
            return null;
        }
    }
}
