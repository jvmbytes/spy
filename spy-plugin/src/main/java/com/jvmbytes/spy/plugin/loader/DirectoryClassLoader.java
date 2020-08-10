package com.jvmbytes.spy.plugin.loader;

import java.io.File;
import java.net.URL;

/**
 * @author wongoo
 */
public class DirectoryClassLoader extends AbstractPluginClassLoader {

    public DirectoryClassLoader(ClassLoader parent, String prefix) {
        super(parent, prefix);
    }

    @Override
    protected byte[] readClassFile(String className) throws Exception {
        String path = className.replace(".", File.separator).concat(".class");
        URL classFileUrl = findResource(path);
        return readBytes(classFileUrl.openStream());
    }

}
