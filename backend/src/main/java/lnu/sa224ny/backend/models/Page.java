package lnu.sa224ny.backend.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class Page {
    private String url;
    private List<Integer> words;

    private double pageRank = 1.0;

    private HashSet<String> links;

    public Page() {
        this.words = new ArrayList<>();
        this.links = new HashSet<>();
    }

    public int getNumberOfLinks() {
        return links.size();
    }


    public void addWordId(int wordId) {
        words.add(wordId);
    }

    public boolean hasLinkTo(String url) {
        String searchItem = "/wiki/" + url;
        return links.contains(searchItem);
    }

    public void addLink(String link) {
        links.add(link);
    }
}
