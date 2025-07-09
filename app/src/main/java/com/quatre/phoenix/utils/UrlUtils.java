package com.quatre.phoenix.utils;

import java.net.MalformedURLException;
import java.net.URL;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UrlUtils {
    public static String extractBaseUrl(String fullUrl) throws MalformedURLException {
        URL url = new URL(fullUrl);
        return url.getProtocol() + "://" + url.getHost() + "/";
    }
}
