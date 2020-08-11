package com.jvmbytes.spy.plugin.loader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.zip.ZipEntry;

/**
 * @author wongoo
 */
public class BootClassLoader extends AbstractPluginClassLoader {

    private static Method jarFileGetEntryMethod;
    private static Method jarFileGetNestedJarFileMethod;
    private static Method jarFileGetInputStreamMethod;

    /**
     * the jar file where class files contains in
     */
    private static Object bootJarFile;

    /**
     * the jar file where class files contains in
     */
    private final Object libJarFile;

    @Override
    public void close() throws IOException {
        if (libJarFile != null) {
            try {
                Method closeMethod = libJarFile.getClass().getMethod("close");
                closeMethod.invoke(libJarFile);
            } catch (Exception ignored) {
            }
        }
    }

    public BootClassLoader(ClassLoader parent, String bootJarPath, String libJarPath, String prefix) throws Exception {
        super(parent, prefix);
        if (bootJarFile == null) {
            Class<?> jarFileClass = Class.forName("org.springframework.boot.loader.jar.JarFile");
            Constructor<?> constructor = jarFileClass.getConstructor(File.class);
            bootJarFile = constructor.newInstance(new File(bootJarPath));
            jarFileGetEntryMethod = jarFileClass.getMethod("getEntry", String.class);
            jarFileGetNestedJarFileMethod = jarFileClass.getMethod("getNestedJarFile", ZipEntry.class);
            jarFileGetInputStreamMethod = jarFileClass.getMethod("getInputStream", ZipEntry.class);
        }

        Object zipEntry = jarFileGetEntryMethod.invoke(bootJarFile, libJarPath);
        if (zipEntry == null) {
            throw new ClassNotFoundException("can't find " + libJarPath);
        }
        this.libJarFile = jarFileGetNestedJarFileMethod.invoke(bootJarFile, zipEntry);
    }

    @Override
    protected byte[] readClassFile(String name) throws Exception {
        String path = getPrefix() + name.replace(".", "/").concat(".class");
        try {
            Object zipEntry = jarFileGetEntryMethod.invoke(libJarFile, path);
            if (zipEntry == null) {
                throw new ClassNotFoundException("can't find " + name);
            }
            InputStream is = (InputStream) jarFileGetInputStreamMethod.invoke(libJarFile, zipEntry);
            return readBytes(is);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
