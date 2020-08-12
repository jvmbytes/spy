package com.jvmbytes.spy.plugin.loader;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

/**
 * @author wongoo
 */
public abstract class AbstractPluginClassLoader extends ClassLoader implements Closeable {
    /**
     * file path prefix
     */
    private String prefix;

    /**
     * the prefixes of packages which are allowed to load from parent class loader
     */
    private String[] parentPackagePrefixes;

    public String getPrefix() {
        return prefix;
    }

    public void setParentPackagePrefixes(String[] parentPackagePrefixes) {
        this.parentPackagePrefixes = parentPackagePrefixes;
    }

    public AbstractPluginClassLoader(ClassLoader parent, String prefix, String[] parentPackagePrefixes) {
        super(parent);
        this.prefix = prefix;
        this.parentPackagePrefixes = parentPackagePrefixes;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    protected URL findResource(String name) {
        return getParent().getResource(prefix + name);
    }

    @Override
    protected Enumeration<URL> findResources(String name) throws IOException {
        return getParent().getResources(prefix + name);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (parentPackagePrefixes != null) {
            for (String packagePrefix : parentPackagePrefixes) {
                if (name.startsWith(packagePrefix)) {
                    try {
                        return getParent().loadClass(name);
                    } catch (Throwable ignore) {
                        // 父类找不到则继续当前类加载器查找
                    }
                }
            }
        }

        // First, check if the class has already been loaded
        Class<?> c = findLoadedClass(name);
        if (c != null) {
            return c;
        }

        Class<?> aClass = findClass(name);
        if (resolve) {
            resolveClass(aClass);
        }
        return aClass;
    }

    @Override
    protected final Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            byte[] data = readClassFile(name);
            data = enhance(name, data);
            return defineClass(name, data, 0, data.length);
        } catch (Exception e) {
            throw new ClassNotFoundException("find class error", e);
        }
    }

    /**
     * read class file bytes
     *
     * @param name class name
     * @return class file bytes
     * @throws Exception when failed to read
     */
    protected abstract byte[] readClassFile(String name) throws Exception;

    /**
     * override this method to enhance the bytes as required
     *
     * @param className class name
     * @param data      bytes
     * @return enhanced bytes
     */
    protected byte[] enhance(String className, byte[] data) {
        return data;
    }

    protected byte[] readBytes(InputStream is) throws IOException {
        byte[] data;
        BufferedInputStream bis = null;
        ByteArrayOutputStream baos = null;
        try {
            bis = new BufferedInputStream(is);
            baos = new ByteArrayOutputStream();
            int size;
            byte[] bytes = new byte[1024];
            while ((size = bis.read(bytes)) != -1) {
                baos.write(bytes, 0, size);
            }
            data = baos.toByteArray();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException ignored) {
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException ignored) {
                }
            }
        }
        return data;
    }
}
