package com.jtulayan.ui.javafx;

import java.net.URL;

/**
 * Simple class to load resources from within the classpath.
 */
public class ResourceLoader {
    private ResourceLoader() {
    }

    public static URL getResource(String path) {
        return ResourceLoader.class.getResource(path);
    }
}
