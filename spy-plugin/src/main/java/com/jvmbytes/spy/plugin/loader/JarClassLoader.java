package com.jvmbytes.spy.plugin.loader;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * @author wongoo
 */
public class JarClassLoader extends AbstractPluginClassLoader {

    /**
     * the jar file where class files contains in
     */
    private JarFile jarFile;

    public JarClassLoader(ClassLoader parent, String jarPath, String prefix, String[] parentPackagePrefixes) throws Exception {
        super(parent, prefix, parentPackagePrefixes);
        this.jarFile = new JarFile(new File(jarPath));
    }

    @Override
    protected byte[] readClassFile(String name) throws Exception {
        String path = getPrefix() + name.replace(".", "/").concat(".class");
        ZipEntry entry = jarFile.getEntry(path);
        if (entry == null) {
            throw new ClassNotFoundException("can't find " + path);
        }
        return readBytes(jarFile.getInputStream(entry));
    }

    @Override
    public void close() throws IOException {
        if (jarFile != null) {
            jarFile.close();
            jarFile = null;
        }
    }
}
