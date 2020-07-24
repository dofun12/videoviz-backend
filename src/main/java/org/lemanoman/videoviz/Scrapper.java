package org.lemanoman.videoviz;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.lemanoman.videoviz.dto.PageNotFoundException;
import org.lemanoman.videoviz.dto.ScrapResult;
import org.lemanoman.videoviz.dto.VideoNotFoundException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Scrapper {
    public static ScrapResult getScrapResult(String url) throws VideoNotFoundException, PageNotFoundException, IOException {
        String html = Scrapper.getHtml(url);
        if(html!=null){
            //logger.debug(html);
            ScrapResult result = new ScrapResult();
            String[] lines = html.split("\\r?\\n");

            String title = "";
            for(String line:lines){
                if (line.matches(".*title\\>.*")) {
                    title = line.replaceAll("title", "");
                }
            }
            result.setTitle(title);
            Document doc = Jsoup.parse(html);
            Elements elements = doc.select("a.btn.btn-default");
            List<String> tags = new ArrayList<String>();
            elements.forEach(action->{
                if(action.attr("href").contains("tags")){
                    tags.add(action.text());
                }
            });
            Elements elementsV2 = doc.select(".metadata-row.video-tags a");
            elementsV2.forEach(action->{
                if(action.attr("href").contains("search")){
                    tags.add(action.text());
                }
            });
            result.setTags(tags);
            return result;
        }
        return null;

    }

    public static String getHtml(String url) throws UnsupportedEncodingException, VideoNotFoundException, IOException, PageNotFoundException {
        int CONNECTION_TIMEOUT_MS = 3 * 1000; // Timeout in millis.
        StringBuilder builder = new StringBuilder();
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(CONNECTION_TIMEOUT_MS)
                .setConnectTimeout(CONNECTION_TIMEOUT_MS)
                .setSocketTimeout(CONNECTION_TIMEOUT_MS)
                .build();

        url = url.split(" ")[0];

        HttpClient httpclient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        //httpGet.addHeader("User-Agent",
        //        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36");
        // httpGet.addHeader("User-Agent", "Mozilla/5.0 (Linux; U; Android
        // 4.0.4; en-gb; GT-I9300 Build/IMM76D) AppleWebKit/534.30 (KHTML, like
        // Gecko) Version/4.0 Mobile Safari/534.30");
        String toDownload = "";
        HttpResponse response = httpclient.execute(httpGet);
        if (response.getStatusLine().getStatusCode() == 200) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            while ((line = rd.readLine()) != null) {
                builder.append(line + "\n");
            }
        } else {
            if (response.getStatusLine().getStatusCode() == 404) {
                throw new PageNotFoundException("Error: " + response.getStatusLine().getStatusCode());
            } else {
                throw new VideoNotFoundException("Error: " + response.getStatusLine().getStatusCode());
            }

        }
        return builder.toString();
    }
}
