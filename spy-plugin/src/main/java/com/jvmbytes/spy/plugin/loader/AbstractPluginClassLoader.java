package com.jvmbytes.spy.plugin.loader;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

/**
 * @author wongoo
 */
public abstract class AbstractPluginClassLoader extends ClassLoader {
    /**
     * file path prefix
     */
    private String prefix;

    public String getPrefix() {
        return prefix;
    }

    public AbstractPluginClassLoader(ClassLoader parent, String prefix) {
        super(parent);
        this.prefix = prefix;
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
