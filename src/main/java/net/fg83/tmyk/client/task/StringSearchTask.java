package net.fg83.tmyk.client.task;

import net.fg83.tmyk.client.TmykClient;

import java.net.URLEncoder;
import java.nio.charset.Charset;

public class StringSearchTask implements Runnable {

    String searchString;

    public StringSearchTask(String searchString) {
        this.searchString = searchString;
    }

    /**
     * Runs this operation.
     */
    @Override
    public void run() {
        String queryString = URLEncoder.encode(searchString, Charset.defaultCharset());
        System.out.println(queryString);
        LookupTask lookupTask = new LookupTask(TmykClient.buildSearchUrl(queryString, true), true);
        lookupTask.run();
        TmykClient.sendMessages(lookupTask.messages);
    }

}
