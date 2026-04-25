package com.yori3o.yo_hooks.common.util;


import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;



public class ResourceResolver {


    public static List<URL> findFiles(String path) {
        List<URL> urls = new ArrayList<>();
        try {
            Enumeration<URL> resources = Thread.currentThread()
                .getContextClassLoader()
                .getResources(path);

            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                urls.add(url);
            }       
        } catch (Exception e) {
            LoggerUtil.errorWithException("Error when get files from resources: ", e);
        }
        return urls;
    }

}