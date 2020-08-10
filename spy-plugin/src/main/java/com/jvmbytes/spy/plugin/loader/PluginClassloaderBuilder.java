package com.jvmbytes.spy.plugin.loader;

import java.net.URL;

/**
 * @author wongoo
 */
public class PluginClassloaderBuilder {
    private static final String JAR_PATH_SEPARATOR = "/";
    private static final String JAR_FILE_SEPARATOR = "!" + JAR_PATH_SEPARATOR;
    private static final String FILE_FLAG_PREFIX = "file:";

    public static ClassLoader build(String key) throws Exception {
        String dir = "/" + key;
        URL url = PluginClassloaderBuilder.class.getResource(dir);
        if (url == null) {
            throw new Exception("can't find plugin " + dir);
        }
        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        String path = url.getPath();
        if (path.startsWith(FILE_FLAG_PREFIX)) {
            path = path.substring(FILE_FLAG_PREFIX.length());
        }

        int jarIndex = path.lastIndexOf(JAR_FILE_SEPARATOR);
        if (jarIndex == -1) {
            return new DirectoryClassLoader(parent, key + JAR_PATH_SEPARATOR);
        }

        String prefix = path.substring(jarIndex + JAR_FILE_SEPARATOR.length());
        if (!prefix.endsWith(JAR_PATH_SEPARATOR)) {
            prefix += JAR_PATH_SEPARATOR;
        }

        path = path.substring(0, jarIndex);

        jarIndex = path.lastIndexOf(JAR_FILE_SEPARATOR);
        if (jarIndex == -1) {
            return new JarClassLoader(parent, path, prefix);
        }

        String libPath = path.substring(jarIndex + JAR_FILE_SEPARATOR.length());
        path = path.substring(0, jarIndex);

        return new BootClassLoader(parent, path, libPath, prefix);
    }
}
