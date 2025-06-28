package com.quatre.phoenix.utils;

import org.jsoup.select.Elements;
import java.io.IOException;

public interface PictureCallback {
    void onResult(Elements elements) throws IOException;
    void onError(Exception e);
}
