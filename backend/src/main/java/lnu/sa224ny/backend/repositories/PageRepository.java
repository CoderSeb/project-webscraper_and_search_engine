package lnu.sa224ny.backend.repositories;

import lnu.sa224ny.backend.models.Page;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@AllArgsConstructor
@Getter
@Setter
public class PageRepository {
    private HashMap<String, Integer> wordToId;
    private List<Page> pages;

    public PageRepository() {
        this.pages = new ArrayList<>();
        this.wordToId = new HashMap<>();
    }

    public int getIdForWord(String word) {
        if (wordToId.containsKey(word)) {
            return wordToId.get(word);
        } else {
            int id = wordToId.size();
            wordToId.put(word, id);
            return id;
        }
    }

    public void addPage(Page newPage) {
        pages.add(newPage);
    }

    public void addWordIdToPage(Page page, String word) {
        int wordId = getIdForWord(word);
        page.addWordId(wordId);
    }

    public Page findByUrl(String url) {
        for (Page page : pages) {
            if (Objects.equals(page.getUrl(), url)) {
                return page;
            }
        }
        return null;
    }
}
