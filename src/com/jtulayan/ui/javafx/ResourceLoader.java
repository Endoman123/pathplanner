package com.jtulayan.ui.javafx;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Simple class to load resources from within the classpath.
 */
public class ResourceLoader {
    private ResourceLoader() {
    }

    public static URL getResource(String path) {
        return ResourceLoader.class.getResource(path);
    }

    public static InputStream getResourceAsStream(String path) {
        return ResourceLoader.class.getResourceAsStream(path);
    }

    // TODO: Figure out what manifest this method is actually getting
    public static Manifest getManifest() {
        Manifest mf = new Manifest();

        try {
            mf.read(getResourceAsStream("/META-INF/MANIFEST.MF"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mf;
    }
}
