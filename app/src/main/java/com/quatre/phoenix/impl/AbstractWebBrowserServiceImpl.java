package com.quatre.phoenix.impl;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractWebBrowserServiceImpl {

    protected final ListeningExecutorService listeningExecutor = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

    public void onDestroy() {
        listeningExecutor.shutdown();
    }

    public ListenableFuture<List<Element>> getAllElementsFromUrl(final String url, final String cssQuery) {
        return listeningExecutor.submit(() -> {
            try {
                Document doc = Jsoup.connect(url).get(); // Fetch the HTML content
                return doc.select(cssQuery).asList();
            } catch (IOException e) {
                log.error("Error while processing url {}", url, e);
                throw e;
            }
        });
    }
}
