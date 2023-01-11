package lnu.sa224ny.backend.webscraper;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
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

    public void runScraper(int noOfSites) {
        String wikiBaseUrl = "https://en.wikipedia.org";
        FileHandler fileHandler = new FileHandler();
        linkQueue.add("/wiki/" + entrySite);
        int counter = 0;
        while (!linkQueue.isEmpty() && counter < noOfSites) {
            String currentUrl = linkQueue.poll();
            if (!scrapedLinks.contains(currentUrl)) {
                System.out.println(counter + ": SCRAPING " + wikiBaseUrl + currentUrl);
                scrapedLinks.add(currentUrl);
                HtmlPage page = getPage(wikiBaseUrl + currentUrl);
                DomElement bodyNode = page.getElementById("bodyContent");


                if (bodyNode.getElementsByTagName("a").size() != 0) {

                    int linkCount = 0;
                    for (DomElement a : bodyNode.getElementsByTagName("a")) {
                        String currentHref = ((HtmlAnchor) a).getHrefAttribute();
                        if (currentHref.startsWith("/wiki/")
                                && !currentHref.contains(".jpg")
                                && !currentHref.contains(".svg")
                                && !currentHref.contains(":")
                                && !currentHref.contains(".")) {
                            linkCount++;
                            if (!scrapedLinks.contains(currentHref)) {
                                linkQueue.add(currentHref);
                            }
                        }
                    }

                    if (linkCount > 0) {
                        fileHandler.saveToFiles(entrySite, currentUrl, page);
                        counter++;
                    }
                }
            }


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
