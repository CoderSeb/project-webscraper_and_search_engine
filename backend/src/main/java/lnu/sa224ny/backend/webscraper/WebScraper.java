package lnu.sa224ny.backend.webscraper;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import lnu.sa224ny.backend.models.Page;
import lnu.sa224ny.backend.utils.FileHandler;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class WebScraper {
    private final Queue<String> linkQueue;
    private final List<String> scrapedLinks;
    private final String entrySite;

    public WebScraper(String entrySite) {
        this.entrySite = entrySite;
        this.linkQueue = new ArrayDeque<>();
        this.scrapedLinks = new ArrayList<>();
    }

    public void runScraper() {
        String wikiBaseUrl = "https://en.wikipedia.org";
        FileHandler fileHandler = new FileHandler();
        linkQueue.add("/wiki/" + entrySite);
        int counter = 0;
        while (!linkQueue.isEmpty() && counter < 200) {
            String currentUrl = linkQueue.poll();
            System.out.println(counter + ": SCRAPING " + wikiBaseUrl + currentUrl);
            scrapedLinks.add(currentUrl);
            HtmlPage page = getPage(wikiBaseUrl + currentUrl);

            fileHandler.saveToFiles(entrySite, currentUrl, page);
            DomElement bodyNode = page.getElementById("bodyContent");

            for (DomElement a : bodyNode.getElementsByTagName("a")) {
                String currentHref = ((HtmlAnchor)a).getHrefAttribute();
                if (currentHref.startsWith("/wiki/")
                        && !currentHref.contains(".jpg")
                        && !currentHref.contains(".svg")) {
                    if (!scrapedLinks.contains(currentHref)) {
                        linkQueue.add(currentHref);
                    }
                }
            }
            counter++;
        }
    }




    private HtmlPage getPage(String url) {
        HtmlPage page = null;
        try (final WebClient webClient = new WebClient()) {
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(false);
            page = webClient.getPage(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return page;
    }
}
