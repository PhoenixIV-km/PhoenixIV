package com.quatre.phoenix.utils;

import java.net.URL;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UrlUtils {
    public static String extractBaseUrl(String fullUrl) {
        try {
            URL url = new URL(fullUrl);
            return url.getProtocol() + "://" + url.getHost() + "/";
        } catch (Exception e) {
            log.error("Failed to extract base url from {}", fullUrl, e);
            throw new RuntimeException(e);
        }
    }
}
