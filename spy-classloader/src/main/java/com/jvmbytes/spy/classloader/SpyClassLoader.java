package com.jvmbytes.spy.classloader;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author wongoo
 */
public class SpyClassLoader extends ClassLoader {

    private File jarFile;

    public SpyClassLoader(ClassLoader parent, String jarPath) {
        super(parent);
        this.jarFile = new File(jarPath);
    }

    private byte[] readClassFile(String jarPath, String className) throws Exception {
        className = className.replace('.', '/').concat(".class");
        URL classFileUrl = new URL("jar:file:" + jarPath + "!/" + className);
        byte[] data = null;
        BufferedInputStream is = null;
        ByteArrayOutputStream baos = null;
        try {
            is = new BufferedInputStream(classFileUrl.openStream());
            baos = new ByteArrayOutputStream();
            int ch = 0;
            while ((ch = is.read()) != -1) {
                baos.write(ch);
            }
            data = baos.toByteArray();
        } finally {
            if (is != null) {
                try {
                    is.close();
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

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String path = name.replace('.', '/').concat(".class");
        try {
            JarFile jar = new JarFile(jarFile);
            JarEntry entry = jar.getJarEntry(path);
            if (entry != null) {
                byte[] data = readClassFile(jarFile.getAbsolutePath(), name);
                data = enhance(name, data);
                return defineClass(name, data, 0, data.length);
            }
        } catch (Exception e) {
            throw new ClassNotFoundException("find class error", e);
        }
        throw new ClassNotFoundException(name);
    }

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

    @Override
    protected URL findResource(String name) {
        try {
            JarFile jar = new JarFile(jarFile);
            JarEntry entry = jar.getJarEntry(name);
            if (entry != null) {
                try {
                    return new URL("jar:file:" + jarFile.getAbsolutePath() + "!/" + name);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    protected Enumeration<URL> findResources(String name) throws IOException {
        List<URL> allResources = new LinkedList<URL>();
        JarFile jar = new JarFile(jarFile);
        JarEntry entry = jar.getJarEntry(name);
        if (entry != null) {
            allResources.add(new URL("jar:file:" + jarFile.getAbsolutePath() + "!/" + name));
        }

        final Iterator<URL> iterator = allResources.iterator();
        return new Enumeration<URL>() {
            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public URL nextElement() {
                return iterator.next();
            }
        };
    }
}
