package com.jvmbytes.spy.plugin;

import com.jvmbytes.spy.inject.SpyInjector;
import com.jvmbytes.spy.plugin.loader.AbstractPluginClassLoader;
import com.jvmbytes.spy.plugin.loader.PluginClassloaderBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author wongoo
 */
public final class PluginLoader {

    private static final Logger logger = LoggerFactory.getLogger(PluginLoader.class);

    private static final String[] defaultParentPackagePrefix = new String[]{
            "java.",
            "javax.",
            "com.jvmbytes.",
            "org.ow2",
            "org.slf4j"
    };

    private static final Map<String, Map<String, Integer>> PLUGIN_LISTEN_IDS =
            new ConcurrentHashMap<String, Map<String, Integer>>(32);
    private static final Map<String, ClassLoader> PLUGIN_CLASS_LOADERS =
            new ConcurrentHashMap<String, ClassLoader>(32);

    private static final ReentrantLock LOCK = new ReentrantLock(true);

    public static String pluginKey(String groupId, String artifactId) {
        return groupId + "@" + artifactId;
    }

    /**
     * load plugin
     */
    public static void load(String groupId, String artifactId) throws Exception {
        String key = pluginKey(groupId, artifactId);
        if (PLUGIN_LISTEN_IDS.containsKey(key)) {
            logger.debug("{} already loaded", key);
            return;
        }

        LOCK.lock();
        try {
            if (PLUGIN_LISTEN_IDS.containsKey(key)) {
                logger.debug("{} already loaded", key);
                return;
            }
            AbstractPluginClassLoader classLoader = PluginClassloaderBuilder.build(key);
            classLoader.setParentPackagePrefixes(defaultParentPackagePrefix);

            Iterator<SpyPlugin> plugins = ServiceLoader.load(SpyPlugin.class, classLoader).iterator();
            Map<String, Integer> ids = new HashMap<String, Integer>(8);
            PLUGIN_LISTEN_IDS.put(key, ids);
            PLUGIN_CLASS_LOADERS.put(key, classLoader);

            HashSet<String> allowPackages = new HashSet<String>(8);
            allowPackages.addAll(Arrays.asList(defaultParentPackagePrefix));

            while (plugins.hasNext()) {
                SpyPlugin plugin = plugins.next();

                if (plugin.getParentPackagePrefixes() != null) {
                    allowPackages.addAll(Arrays.asList(plugin.getParentPackagePrefixes()));
                    classLoader.setParentPackagePrefixes(allowPackages.toArray(new String[0]));
                }

                String namespace = plugin.getNamespace();
                if (namespace == null) {
                    namespace = SpyInjector.DEFAULT_NAMESPACE;
                }

                int listenId = SpyInjector.inject(namespace,
                        plugin.getMatcher(),
                        plugin.getEventTypes(),
                        plugin.getEventListener());

                ids.put(plugin.getName(), listenId);
                logger.info("plugin {} loaded, listen id {}", plugin.getName(), listenId);
            }
        } finally {
            LOCK.unlock();
        }
    }

    /**
     * unload plugin
     */
    public static void unload(String groupId, String artifactId) throws Exception {
        String key = pluginKey(groupId, artifactId);
        if (!PLUGIN_LISTEN_IDS.containsKey(key)) {
            logger.debug("{} not loaded", key);
            return;
        }

        LOCK.lock();
        try {
            Map<String, Integer> ids = PLUGIN_LISTEN_IDS.remove(key);
            if (ids != null) {
                for (String name : ids.keySet()) {
                    Integer id = ids.get(name);
                    SpyInjector.remove(id);
                    logger.info("plugin {} unloaded, listen id {}", name, id);
                }
            }

            ClassLoader classLoader = PLUGIN_CLASS_LOADERS.remove(key);
            closeClassloader(classLoader);
            System.gc();
        } finally {
            LOCK.unlock();
        }
    }

    private static void closeClassloader(ClassLoader classLoader) {
        if (classLoader == null) {
            return;
        }

        if (classLoader instanceof Closeable) {
            try {
                ((Closeable) classLoader).close();
            } catch (Throwable ignored) {
            }
            return;
        }
    }

}
